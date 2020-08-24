package com.componente_practico.webhook.acciones;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.domotica.ejb.dao.AccionDomoticaDao;
import com.holalola.webhook.domotica.ejb.modelo.AccionDomotica;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;

import ai.api.model.Result;

@Stateless
@Path("/domotica")
public class ManejarDomotica {
	
	final static Logger log = LoggerFactory.getLogger(ManejarDomotica.class);
	
	@EJB
	private AccionDomoticaDao accionDomoticaDao;
	
	
	public ResponseMessageApiAi registrarAccionDomotica(Result resultAi, Usuario usuario) {

		String accion = ApiAiUtil.obtenerValorParametro(resultAi, "accionDomotica", null);
		String elemento = ApiAiUtil.obtenerValorParametro(resultAi, "elementoDomotica", null);

		accionDomoticaDao.insertar(new AccionDomotica(accion, elemento));
		
		final String speech = String.format("Listo %s, te ayudo en este momento! :)", usuario.getNombreFacebook());
		
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, "accionDomotica");
	}
	
	
	@GET
	@Path("/pendiente")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtnerAccionDomoticaPendiente() {
		List<AccionDomotica> pendientes = accionDomoticaDao.obtenerPrimeroPendiente();
		AccionDomotica result = pendientes.isEmpty() ? new AccionDomotica() : pendientes.get(0);
		
		return Response.status(200).entity(result).build();
	}

	@GET
	@Path("/confirmar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response obtnerConfirmarAccionDomotica() {
		List<AccionDomotica> pendientes = accionDomoticaDao.obtenerPrimeroPendiente();
		if (!pendientes.isEmpty()) {
			 accionDomoticaDao.eliminar(pendientes.get(0));
		}
		
		return Response.status(200).entity(new AccionDomotica()).build();
	}
	
	//********************************************DIALOGFLOW V2******************************************************
	public ResponseMessageApiAiV2 registrarAccionDomoticaV2(QueryResult resultAi, Usuario usuario) {

		String accion = ApiAiUtil.obtenerValorParametroV2(resultAi, "accionDomotica.original", null);
		String elemento = ApiAiUtil.obtenerValorParametroV2(resultAi, "elementoDomotica.original", null);

		accionDomoticaDao.insertar(new AccionDomotica(accion, elemento));
		
		final String speech = String.format("Listo %s, te ayudo en este momento! :)", usuario.getNombreFacebook());
		
		return new ResponseMessageApiAiV2(speech, "accionDomotica", new PayloadResponse(new TextMessageV2(speech)), null);
	}
}
