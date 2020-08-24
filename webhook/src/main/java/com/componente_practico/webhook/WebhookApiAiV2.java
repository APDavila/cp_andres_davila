package com.componente_practico.webhook;

import java.io.Console;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.cine.ejb.servicio.CineServicio;
import com.componente_practico.comida.pedido.ejb.servicio.ArmarPedidoComida;
import com.componente_practico.comida.pedido.ejb.servicio.CarritoCompras;
import com.componente_practico.comida.pedido.ejb.servicio.PedidoSatisfaccionServicio;
import com.componente_practico.comida.pedido.ejb.servicio.ProcesoPagoServicio;
import com.componente_practico.directorio.AccionNoExisteException;
import com.componente_practico.directorio.EjecutorAcciones;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.general.ejb.servicio.RegistroErrorServicio;
import com.componente_practico.general.ejb.servicio.UsuarioServicio;
import com.componente_practico.pagos.paymentez.PagosPaymentez;
import com.componente_practico.reservas.ejb.servicio.ArmarReservas;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.webhook.acciones.ConsultarClimaOpenWeather;
import com.componente_practico.webhook.acciones.ConsultarClimaYahoo;
import com.componente_practico.webhook.acciones.ConsultarNoticias;
import com.componente_practico.webhook.acciones.Conversar;
import com.componente_practico.webhook.acciones.IniciarCompra;
import com.componente_practico.webhook.acciones.ManejarAFNA;
import com.componente_practico.webhook.acciones.ManejarAgenda;
import com.componente_practico.webhook.acciones.ManejarDomotica;
import com.componente_practico.webhook.acciones.ManejarReservas;
import com.componente_practico.webhook.acciones.ManejarRestaurantes;
import com.componente_practico.webhook.acciones.Reservas;
import com.componente_practico.webhook.acciones.ValidadorUsuario;
import com.componente_practico.webhook.acciones.VerPerfilUsuario;
import com.componente_practico.webhook.api.ai.AccionesApiAi;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.componente_practico.webhook.v2.Request;
import com.google.cloud.dialogflow.v2.WebhookRequest;
import com.google.gson.Gson;
import com.holalola.comida.pedido.ejb.dao.OperadorProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.dao.UsuarioOperadorDao;
import com.holalola.general.ejb.dao.Usuario_TraceDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioOperador;
import com.holalola.general.ejb.modelo.Usuario_Trace;
import com.holalola.util.TextoUtil;
import com.holalola.webhook.PayloadConstantes;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.Recipient;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.SenderActionParaFacebook;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.GsonFactory;

@Path("/aiV2")
@Stateless
public class WebhookApiAiV2 {

	final Logger log = LoggerFactory.getLogger(WebhookApiAi.class);
	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();

	public static final TextQuickReply SERVICIOS_LOLA_REPLY = new TextQuickReply("Reiniciar con Lola",
			PayloadConstantes.MOSTRAR_SERVICIOS,
			"https://cdn.pixabay.com/photo/2013/07/12/14/14/house-148033__340.png");

	@EJB
	private armarOpcionesUniversidad armarOpcionesUniversidad;

	
	@EJB
	private ManejarDomotica manejarDomotica;
	@EJB
	private ManejarRestaurantes manejarRestaurantes;
	@EJB
	private ProcesoPagoServicio procesoPagoServicio;
	@EJB
	private RegistroErrorServicio registroErrorServicio;
	@EJB
	private UsuarioServicio usuarioServicio;
	@EJB
	private ValidadorUsuario validadorUsuario;
	
	@EJB
	private UsuarioOperadorDao usuarioOperadorDao;

	@EJB
	private OperadorProveedorDao operadorProveedorDao;


	@EJB
	EjecutorAcciones ejecutorAcciones;	

	@EJB
	Usuario_TraceDao usuarioTraceDao;

	
	@EJB
	private IniciarAcciones iniciarAcciones ;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	public Response ejecutarAccion(String requestBody) throws AccionNoExisteException, ParseException {
		if (TextoUtil.esVacio(requestBody)) {
			log.info("Requestbody es vacio");
			return Response.status(200).build();
		}
		// log.info("----------\nApiResponse para ejecutarAccion 1:\n {}\n---------",
		// requestBody);

		/*
		 * if (log.isDebugEnabled()) {
		 * log.debug("----------\nApiResponse para ejecutarAccion 2:\n {}\n---------",
		 * requestBody); }
		 */
		final Request response = GSON.fromJson(requestBody, Request.class);
		final QueryResult resultAi = response.getQueryResult();
		final String accion = resultAi.getAction();
		String resultadoJson = null;
		Usuario usuario = validarUsuario(response);
		boolean ab_bloquear = false;
		if (usuario == null) {
			log.info("----------\n Usuario Vaciooo \n---------", requestBody);
		} else {
			try {
				ab_bloquear = usuario.getBloqueado();
			} catch (Exception err) {
				ab_bloquear = false;
			}
		}
		// Para Chat
		if (!ab_bloquear && usuario != null && !usuario.getChatActivo()
				&& !accion.trim().equals(AccionesApiAi.ACTIVAR_CHAT)) {
			String ls_mensaje = "Por favor validar mensaje en chat de lola porque no se reconoce el mensaje o es demasiado largo";

			try {
				ls_mensaje = response.getOriginalDetectIntentRequest().getPayload().getData().getMessage().getText();
			} catch (Exception err) {

			}
			try {
				UsuarioOperador usuarioOperadorTemp = usuarioOperadorDao.obtenerUltimoOperador(usuario);
				OperadorProveedor operadorProveedorTemp = null;
				if (usuarioOperadorTemp == null) {
					try {
						Long idOperadorProveedor = operadorProveedorDao
								.dameUsuarioChat(Long.parseLong(System.getProperty("lola.chatLola_provee")));
						operadorProveedorTemp = operadorProveedorDao.obtenerPorId(idOperadorProveedor);
					} catch (Exception err1) {

					}
				}

				if (usuarioOperadorTemp != null || operadorProveedorTemp != null) {
					UsuarioOperador usuarioOperador = new UsuarioOperador(usuario,
							(usuarioOperadorTemp != null ? usuarioOperadorTemp.getOperador() : operadorProveedorTemp),
							new Date(), ls_mensaje.trim(), false);
					usuarioOperadorDao.insertar(usuarioOperador);
				}
			} catch (Exception err) {
				log.info("----------\n Error al Insertat Usuario Operador:\n " + err.getMessage() + "\n---------");
			}
			ab_bloquear = true;
		}
		if (ab_bloquear) {
			return Response.status(200)
					.entity("{\r\n" + "\"speech\": responseToSend, \"displayText\": responseToSend,\r\n"
							+ "\"data\": {\"facebook\": {}}\r\n" + "}")
					.build();
		}

		if (usuario != null) {
			usuarioTraceDao.insertar(
					new Usuario_Trace(usuario, new Date(), accion, (resultAi != null ? resultAi.toString() : "")));
		}
		System.out.println("WEBHOOK_______________________________________________________ = "+accion);
		switch (accion) {		
		case AccionesApiAi.MOSTRAR_PREGRADO:			
			resultadoJson = jsonApiLastVersion(GSON.toJson(manejarRestaurantes.mostrarPregrado(resultAi, usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();
		case AccionesApiAi.MOSTRAR_POSTGRADO:
			resultadoJson = jsonApiLastVersion(GSON.toJson(manejarRestaurantes.mostrarPostgrado(resultAi, usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);							
			return Response.status(200).entity(resultadoJson).build();
		case AccionesApiAi.MOSTRAR_SERVICOS:
			resultadoJson = GSON.toJson(conversar.mostrarServiciosv2());
			resultadoJson = "{\"recipient\":{\"id\":\""+usuario.getIdFacebook()+"\"},"+resultadoJson.substring(1,resultadoJson.length());
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			resultadoJson="{\"sender_action\":\"typing_off\",\"recipient\":{\"id\":\""+usuario.getIdFacebook()+"\"}}";
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();
		case AccionesApiAi.SALUDAR:
			resultadoJson = GSON.toJson(conversar.saludarV2(usuario));
			break;
		case AccionesApiAi.CONVERSACION_DUMMY:
			resultadoJson = GSON.toJson(conversar.conversarDummyV2(resultAi, usuario));
			break;
		case AccionesApiAi.INSCRIBIRSE:
			resultadoJson = jsonApiLastVersion(GSON.toJson(verPerfilUsuario.inscribirse(resultAi, usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();		
		case AccionesApiAi.MOSTRAR_REQUISITOS:
			resultadoJson = jsonApiLastVersion(GSON.toJson(conversar.mostrarRequisitos(usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();
		case AccionesApiAi.MOSTRAR_CONTACTO:
			resultadoJson = jsonApiLastVersion(GSON.toJson(conversar.mostrarContacto(usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();											
		case AccionesApiAi.DETALLES_CARRERA:			
			resultadoJson = jsonApiLastVersion(GSON.toJson(manejarRestaurantes.mostrarDetallesCarrera(resultAi, usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();
		case AccionesApiAi.INICIAR_CONVERSACION:
			resultadoJson = jsonApiLastVersion(GSON.toJson(conversar.iniciarConversacionV2(usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();											

		case AccionesApiAi.DIALOGV2:
			resultadoJson = GSON.toJson(pedidoSatisfaccionServicio.mensajePrueba(usuario));
			break;
		// *VER PERFIL
		case AccionesApiAi.INICIAR_PERFIL:
			resultadoJson = jsonApiLastVersion(GSON.toJson(verPerfilUsuario.verPerfilUsuarioV2(resultAi, usuario)), usuario);			
			ConsultarFacebook.postToFacebookString(resultadoJson, propiedadesLola.facebookToken);
			return Response.status(200).entity(resultadoJson).build();								
		// VER HISTORIAL DE PEDIDOS
		case AccionesApiAi.INICIAR_HISTORIAL_PEDIDOS:
			resultadoJson = GSON.toJson(verPerfilUsuario.verPerfilUsuarioV2(resultAi, usuario));
			break;
		default:
			// Para Chat Inicio
			int li_activoChat = 0;

			try {
				li_activoChat = Integer.valueOf(System.getProperty("lola.activaChatHumanos"));
			} catch (Exception err) {
				li_activoChat = 0;
			}

			int intentosFallidosRespuesta = 0;
			try {
				intentosFallidosRespuesta = Integer.valueOf(System.getProperty("lola.numRepSinEntender"));
			} catch (Exception Err) {
				intentosFallidosRespuesta = 3;
			}

			if (usuario != null) {
				usuario.setNoEntiendo((usuario.getNoEntiendo() != null ? usuario.getNoEntiendo() : 0) + 1);

			}

			if (usuario != null && li_activoChat == 1) {
				try {
					UsuarioOperador usuarioOperadorTemp = usuarioOperadorDao.obtenerUltimoOperador(usuario);
					OperadorProveedor operadorProveedorTemp = null;
					if (usuarioOperadorTemp == null) {
						try {
							Long idOperadorProveedor = operadorProveedorDao
									.dameUsuarioChat(Long.parseLong(System.getProperty("lola.chatLola_provee")));
							operadorProveedorTemp = operadorProveedorDao.obtenerPorId(idOperadorProveedor);
						} catch (Exception err1) {

						}
					}

					if (usuarioOperadorTemp != null || operadorProveedorTemp != null) {
						String ls_mensaje = "Por favor validar mensaje en chat de lola porque no se reconoce el mensaje o es demasiado largo";

						try {
							ls_mensaje = response.getOriginalDetectIntentRequest().getPayload().getData().getMessage()
									.getText();
						} catch (Exception err) {

						}

						UsuarioOperador usuarioOperador = new UsuarioOperador(usuario,
								(usuarioOperadorTemp != null ? usuarioOperadorTemp.getOperador()
										: operadorProveedorTemp),
								new Date(), ls_mensaje.trim(), false);
						usuarioOperadorDao.insertar(usuarioOperador);
					}
				} catch (Exception err) {
					log.info("----------\n Error al Insertat Usuario Operador:\n " + err.getMessage() + "\n---------");
				}

				if (usuario.getNoEntiendo() >= intentosFallidosRespuesta) {
					resultadoJson = GSON.toJson(validadorUsuario.solicitarAyudaHumana(usuario));

					usuario.setNoEntiendo(0);
					usuarioServicio.modificar(usuario);
					// registroErrorServicio.registarError(resultAi, requestBody);

					return Response.status(200).entity(resultadoJson).build();
				}
				// Para Chat Fin
			} else {
				if (usuario != null) {
					if (usuario.getNoEntiendo() >= intentosFallidosRespuesta) {
						resultadoJson = GSON.toJson(conversar.mostrarServicios());

						usuario.setNoEntiendo(0);
						usuarioServicio.modificar(usuario);
						// registroErrorServicio.registarError(resultAi, requestBody);

						return Response.status(200).entity(resultadoJson).build();
					}
				}
			}
			// registroErrorServicio.registarError(resultAi, requestBody);
			return Response.status(401).build();
		}
//		 log.info("Json respuesta desde webhook\n{}", resultadoJson);
		
		return Response.status(200).entity(resultadoJson).build();
	}

	private String jsonApiLastVersion(String oldJSON, Usuario usuario) {
		oldJSON = "\"messaging_type\": \"RESPONSE\",\"message\""+oldJSON.substring(oldJSON.indexOf(":{\"attachment\"") , oldJSON.length());
		oldJSON = "{\"recipient\":{\"id\":\""+usuario.getIdFacebook()+"\"},"+oldJSON;
		return oldJSON.replace("template\"}}}","template\"}}");			
	}
	private Usuario validarUsuario(Request response) {

		if (response.getOriginalDetectIntentRequest() == null) {
			// log.info("\n------------------\n Usuario: Vacio\n------------------\n");
			return null;
		}

		// log.info("\n------------------\n Usuario:
		// Vacio\n------------------"+response.getOriginalDetectIntentRequest().toString());

		final String idUsuarioFacebook = response.getOriginalDetectIntentRequest().getPayload().getData().getSender()
				.getId();

		mostrarAccionDeTipeo(idUsuarioFacebook);
		return usuarioServicio.validarUsuarioFacebook(idUsuarioFacebook);

	}
	
	public ResponseMessageApiAiV2 verComercios(Usuario usuario,String payload) {
		String speech="dddd";
		manejarRestaurantes.mostrarRestaurantesV4(payload, usuario);
	List<QuickReplyGeneral> listaPersonasTem = new ArrayList<QuickReplyGeneral>();

	return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "Direccion", listaPersonasTem);
	}

	private void mostrarAccionDeTipeo(String idUsuarioFacebook) {
		
		SenderActionParaFacebook senderActionParaFacebook = new SenderActionParaFacebook(
				new Recipient(idUsuarioFacebook), SenderActionParaFacebook.TYPING_ON);
		ConsultarFacebook.postToFacebook(senderActionParaFacebook, propiedadesLola.facebookToken);
	}

}