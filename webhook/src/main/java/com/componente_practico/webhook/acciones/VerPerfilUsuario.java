package com.componente_practico.webhook.acciones;

import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.ButtonRichMessageV2;
import com.holalola.webhook.facebook.templates.WebUrlButton;

import ai.api.model.Result;

@Stateless
public class VerPerfilUsuario {
	
	@EJB
	RespuestaDao respuestaDao;
	
	public ResponseMessageApiAi verPerfilUsuario(Result resultAi, Usuario usuario) {
		
		
		String idUsuario = ApiAiUtil.obtenerValorParametro(resultAi, "idUsuario", "");
		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.VER_PERFIL);
		
		String speech = String.format(
			servicios.getTexto(),
				usuario.getNombreFacebook());
		//UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
		List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
				UrlUtil.armarUrlVerPerfil(usuario.getId()), true)); 
		Data data = new Data(new ButtonRichMessage(speech, buttons));
		return new ResponseMessageApiAi(speech, speech, data, null, "");
	}
	
public ResponseMessageApiAiV2 verPerfilUsuarioV2(QueryResult resultAi, Usuario usuario) {
		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.VER_PERFIL);		
		String speech = String.format(
			servicios.getTexto(),
				usuario.getNombreFacebook());
		//UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
		List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
				UrlUtil.verSIGE(usuario.getId()), true)); 
		PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
		return new ResponseMessageApiAiV2(speech, "", data, null);
	}
public ResponseMessageApiAiV2 inscribirse(QueryResult resultAi, Usuario usuario) {
			
	String speech = "Listo "+usuario.getNombreFacebook()+", a continuación te presentamos el dormulario de registro, llenalo y nos pondremos en contacto contigo.";
	List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver formulario",
			UrlUtil.verFormulario(usuario.getId()), true)); 
	PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
	return new ResponseMessageApiAiV2(speech, "", data, null);
}
}
