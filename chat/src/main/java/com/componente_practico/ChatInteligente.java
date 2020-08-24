package com.componente_practico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.sql.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.componente_practico.ejb.modelo.Animal;
import com.componente_practico.ejb.modelo.Servicio.AnimalServicio;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.util.TextoUtil;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.google.gson.Gson;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.GsonFactory;
import ai.api.model.AIResponse;
import ai.api.model.Result;

@Path("/df")
@Stateless
public class ChatInteligente {

	@Resource(mappedName = "java:/holaLolaDS")
	DataSource dataSource;

	public String animales() throws Exception {
		String ls_salida = "";
		Connection con = null;
		PreparedStatement ps = null;
		try {
			con = dataSource.getConnection();
			ps = con.prepareStatement("SELECT codigo id, nombre FROM pruebas.prueba ");

			ResultSet rs = ps.executeQuery();
			if (rs != null) {
				ls_salida = "El listado de persona es el siguiente: ";

				while (rs.next()) {
					ls_salida = ls_salida + rs.getObject("nombre")+ "; " ;
				}
			}
		} catch (Exception e) {
			ls_salida = "Error";
			System.out.println("Error: ------------------------------ . " + e.getMessage());
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}

		return ls_salida;
	}

	

	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();

	@EJB
	AnimalServicio animalServicio;

	@POST
	// @Consumes(MediaType.APPLICATION_JSON)
	public Response Saludar(String facebookMessage) {

		System.out.println("\n==Mensaje desde facebook===\n" + facebookMessage + "\n=====\n");

		if (facebookMessage == null || "".equals(facebookMessage)) {
			System.out.println("Vacio");
			return Response.status(200).build();
		}

		final AIResponse response = GSON.fromJson(facebookMessage, AIResponse.class);
		final Result resultAi = response.getResult();
		final String accion = resultAi.getAction();
		String resultadoJson = null;

		ResponseMessageApiAi respuesta;

		// Switch de acciones que viene desde DialogFlow

		switch (accion) {
		case "mostrarFoto":
			String animal = ApiAiUtil.obtenerValorParametro(resultAi, "animales", null);

			if (TextoUtil.esVacio(animal)) {
				String texto = "Indicame que animal deseas ver";

				List<QuickReplyGeneral> botones = Arrays.asList(new TextQuickReply("Perro", "perro"),
						new TextQuickReply("Gato", "gato"));

				respuesta = ApiAiUtil.armarRespuestaTextMessageConQuickReply(texto, "mostrarFoto", botones);

				resultadoJson = GSON.toJson(respuesta);

				return Response.status(200).entity(resultadoJson).build();
			}

			Animal animalObjeto = animalServicio.insertar(animal);

			String as_adicional = "S/N";

			try {
				as_adicional = animales();
			} catch (Exception err1) {
				as_adicional = "Error: " + err1.getMessage();
			}
			respuesta = ApiAiUtil.armarRespuestaTextMessage(
					"Ya se guardo el Animal - " + as_adicional + "  ---   " + animalObjeto.getId().toString(),
					"Accion Salidar");

			// respuesta = ApiAiUtil.armarRespuestaTextMessage("Chevere voy a buscar una
			// foto de un "+ animal, "Accion Salidar"); // Accion Salidar no
			// es necesario
			// puede ir null es
			// como un log nada
			// mas

			// respuesta = ApiAiUtil.armarRespuestaVideoMessage(null, "165894107344094",
			// null);

			break;
		case "accionSaludar":
			respuesta = ApiAiUtil.armarRespuestaTextMessage("Hola desde Yoyos", "Accion Salidar"); // Accion Salidar no
																									// es necesario
																									// puede ir null es
																									// como un log nada
																									// mas
			break;
		default:
			respuesta = ApiAiUtil.armarRespuestaTextMessage("No entienndo", "Accion Salidar"); // Accion Salidar no es
																								// necesario puede ir
																								// null es como un log
																								// nada mas
			break;
		}

		resultadoJson = GSON.toJson(respuesta);

		return Response.status(200).entity(resultadoJson).build();
	}

}
