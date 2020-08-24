package com.componente_practico.webhook.acciones;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.ejb.servicio.NoticiaServicio;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.ejb.dao.UrlSeccionNoticiaDao;
import com.holalola.webhook.ejb.modelo.Noticia;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.model.AIOutputContext;
import ai.api.model.Result;

@Stateless
public class ConsultarNoticias {
	
	private static final String CONTEXTO_FINAL_CONSULTA = "consultarnoticias_seccion";
	private static final String PARAM_FUENTE_NOTICIAS = "fuenteNoticias";
	private static final String PARAM_SECCION_NOTICIAS = "seccionNoticias";
	private static final String PARAM_ID_NOTICIA = "idNoticia.original";
	private static final String PARAM_CONTEXTO_RESUMEN_NOTICIA= "generic";
	
	@EJB
	private UrlSeccionNoticiaDao urlSeccionNoticiaDao;
	@EJB
	private ConsultarRss consultarRss;
	@EJB
	private NoticiaServicio noticiaServicio;
	
	public ResponseMessageApiAi obtenerFuentesNoticias(Usuario usuario) {
		
		// TODO: Este mensaje sacar de la base
		final String speech = String.format("%s, elige el medio de dónde quieres recibir noticias:", usuario.getNombreFacebook());
		
		List<QuickReplyGeneral> quickReplies = urlSeccionNoticiaDao.obtenerFuentesNoticas().stream().map(f -> {
			return new TextQuickReply(f.getNombre(), f.getPayload());
		}).collect(Collectors.toList());
		
		
		//ContextOut contextOut = new ContextOut("consultarNoticiasFuente", 5, new HashMap<>());
		
		final Data facebookData = new Data(new TextMessage(speech, quickReplies));
		return new ResponseMessageApiAi(speech, speech, facebookData, null, "consultarNoticias");
	}
	
	
	public ResponseMessageApiAi obtenerSeccionesNoticias(Result resultAi) {
		
		final String speech = "Secciones de noticias";
		final String fuenteNoticias = resultAi.getParameters().get(PARAM_FUENTE_NOTICIAS).getAsString();
		
		List<QuickReplyGeneral> quickReplies = urlSeccionNoticiaDao.obtenerSecciones(fuenteNoticias).stream().map(s -> {
			return new TextQuickReply(s.getNombre(), s.getPayload());
		}).collect(Collectors.toList());
		
		
		
		final Data facebookData = new Data(new TextMessage("Sobre que tema?", quickReplies));
		return new ResponseMessageApiAi(speech, speech, facebookData, null, "consultarNoticias");
	}
	
	public ResponseMessageApiAi obtenerNoticiaEfectiva(Result resultAi) {
		
		String paramFuente = null;
		String paramSeccion = null;
		
		AIOutputContext ctx;
		if ((ctx = resultAi.getContext(CONTEXTO_FINAL_CONSULTA)) != null) {
			paramFuente = ctx.getParameters().get(PARAM_FUENTE_NOTICIAS).getAsString();
			paramSeccion = ctx.getParameters().get(PARAM_SECCION_NOTICIAS).getAsString();
		
		} 
		
		if (paramFuente == null) {
			paramFuente = resultAi.getParameters().get(PARAM_FUENTE_NOTICIAS).getAsString();
		}
		
		if (paramSeccion == null) {
			paramSeccion = resultAi.getParameters().get(PARAM_SECCION_NOTICIAS).getAsString();
		}
		
		return armarNoticias(paramFuente, paramSeccion);
		
	}
	
	public ResponseMessageApiAi obtenerResumenNoticia(Result resultAi) {
		final String txtNoticiaId = resultAi.getParameters().get(PARAM_ID_NOTICIA).getAsString();
		final Noticia noticia = noticiaServicio.obtenerPorId(txtNoticiaId);
		
		final String speech = String.format("%s:\n\n%s", noticia.getTitulo(), noticia.getResumen());
		
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, "verResumenNoticia");
	}
	
	private ResponseMessageApiAi armarNoticias(String fuente, String seccion) {
		
		String urlRss = urlSeccionNoticiaDao.obtenerUrlSeccion(fuente, seccion).get(0).getUrl();
		return consultarRss.consultarRss(urlRss, fuente, seccion);
	}
	
//*****************************************************DIALOGFLOW V2****************************************************
public ResponseMessageApiAiV2 obtenerFuentesNoticiasV2(Usuario usuario) {
		
		// TODO: Este mensaje sacar de la base
		final String speech = String.format("%s, elige el medio de dónde quieres recibir noticias:", usuario.getNombreFacebook());
		
		List<QuickReplyGeneral> quickReplies = urlSeccionNoticiaDao.obtenerFuentesNoticas().stream().map(f -> {
			return new TextQuickReply(f.getNombre(), f.getPayload());
		}).collect(Collectors.toList());
		
		
		//ContextOut contextOut = new ContextOut("consultarNoticiasFuente", 5, new HashMap<>());
		
		final PayloadResponse facebookData = new PayloadResponse(new TextMessageV2(speech, quickReplies));
		return new ResponseMessageApiAiV2(speech, "consultarNoticias", facebookData, null);
	}
public ResponseMessageApiAiV2 obtenerSeccionesNoticiasV2(QueryResult resultAi) {
	
	final String speech = "Secciones de noticias";
	final String fuenteNoticias = resultAi.getParameters().get(PARAM_FUENTE_NOTICIAS).toString();
	
	List<QuickReplyGeneral> quickReplies = urlSeccionNoticiaDao.obtenerSecciones(fuenteNoticias).stream().map(s -> {
		return new TextQuickReply(s.getNombre(), s.getPayload());
	}).collect(Collectors.toList());
	
	
	
	final PayloadResponse facebookData = new PayloadResponse(new TextMessageV2("Sobre que tema?", quickReplies));
	return new ResponseMessageApiAiV2(speech, "consultarNoticias", facebookData, null);
}
public ResponseMessageApiAiV2 obtenerNoticiaEfectivaV2(QueryResult resultAi) {
	
	String paramFuente = null;
	String paramSeccion = null;
	
	OutputContexts ctx;
	if ((ctx = resultAi.getContext(CONTEXTO_FINAL_CONSULTA)) != null) {
		paramFuente = ctx.getParameters().get(PARAM_FUENTE_NOTICIAS).toString();
		paramSeccion = ctx.getParameters().get(PARAM_SECCION_NOTICIAS).toString();
	
	} 
	
	if (paramFuente == null) {
		paramFuente = resultAi.getParameters().get(PARAM_FUENTE_NOTICIAS).toString();
	}
	
	if (paramSeccion == null) {
		paramSeccion = resultAi.getParameters().get(PARAM_SECCION_NOTICIAS).toString();
	}
	
	return armarNoticiasV2(paramFuente, paramSeccion);
	
}

private ResponseMessageApiAiV2 armarNoticiasV2(String fuente, String seccion) {
	
	String urlRss = urlSeccionNoticiaDao.obtenerUrlSeccion(fuente, seccion).get(0).getUrl();
	return consultarRss.consultarRssV2(urlRss, fuente, seccion);
}

public ResponseMessageApiAiV2 obtenerResumenNoticiaV2(QueryResult resultAi) {
	final String txtNoticiaId = resultAi.getContext(PARAM_CONTEXTO_RESUMEN_NOTICIA).getParameters().get(PARAM_ID_NOTICIA).toString();
	final Noticia noticia = noticiaServicio.obtenerPorId(txtNoticiaId);
	
	final String speech = String.format("%s:\n\n%s", noticia.getTitulo(), noticia.getResumen());
	
	return new ResponseMessageApiAiV2(speech, "verResumenNoticia", new PayloadResponse(new TextMessageV2(speech)), null);
}
	

}
