package com.componente_practico.webhook.abstracto;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.webhook.ejb.modelo.Clima;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.RichMessageV2;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.model.AIOutputContext;
import ai.api.model.Result;

public abstract class ConsultarClimaAbstracto {
	
	protected static final String CONTEXTO_CLIMA_DETALLADO = "climadetallado";
	protected static final String PARAM_CIUDAD = "ciudad";
	
	protected static final String PAYLOAD_POR_HORAS = "CLIMA_POR_HORAS";
	protected static final String PAYLOAD_POR_SEMANA = "CLIMA_SEMANA";
	
	public static final String TIPO_POR_HORAS = "HORAS";
	public static final String TIPO_POR_DIAS = "DIAS";
	public static final String TIPO_ACTUAL = "ACTUAL";
	
	protected String obtenerParametroCiudad(Result resultAi) {
		
		String paramCiudad = null;
		
		AIOutputContext ctx;
		if ((ctx = resultAi.getContext(CONTEXTO_CLIMA_DETALLADO)) != null) {
			paramCiudad = ctx.getParameters().get(PARAM_CIUDAD).getAsString();
		} 
		
		if (paramCiudad == null) {
			paramCiudad = resultAi.getParameters().get(PARAM_CIUDAD).getAsString();
		}
		
		return paramCiudad;
	}
	
	protected ResponseMessageApiAi armarResponseActual(String speech, String source) {
		List<QuickReplyGeneral> opcionesClimaReply = Arrays.asList(
				new TextQuickReply("Para Hoy", PAYLOAD_POR_HORAS),
				new TextQuickReply("Para la Semana", PAYLOAD_POR_SEMANA)
				);
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech, opcionesClimaReply)), null, source);
	}
	
	protected ResponseMessageApiAi validarClimaEnBD(Clima clima, String source) throws UnsupportedEncodingException {
		
		if (clima != null) {
			
			if (TIPO_ACTUAL.equals(clima.getTipo())) {
				return armarResponseActual(clima.getVisualizacion(), source);
			} else {
				final String urlImagen =  UrlUtil.armarUrlImagenClima(clima.getVisualizacion());
				final String speech = String.format("El clima de %s", source);
				final Data weatherData = new Data(new RichMessage(new Attachment(Attachment.IMAGE, new MediaPayload(urlImagen))));
				return new ResponseMessageApiAi(speech, speech, weatherData, null, source);
			}
			
		}
		
		return null;
	}
	
	protected Date calcularSiguienteActualizacion(Date fechaInicio) {
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(fechaInicio);
		fecha.set(Calendar.MINUTE, 0);
		fecha.set(Calendar.SECOND, 0);
		fecha.set(Calendar.MILLISECOND, 0);
		
		int hora = fecha.get(Calendar.HOUR_OF_DAY) + 24;
		
		while ((hora - 1) % 3 != 0) {
			fecha.add(Calendar.HOUR_OF_DAY, 1);
			hora = fecha.get(Calendar.HOUR_OF_DAY) + 24;
		}
		
		return fecha.getTime();
	}
	
	protected ResponseMessageApiAi armarMensajeError(final String speech, final String source) {
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, source);
	}

//***********************************************DIALOGFLOW V2*****************************************************
	protected String obtenerParametroCiudadV2(QueryResult resultAi) {
		
		String paramCiudad = null;
		
		OutputContexts ctx;
		if ((ctx = resultAi.getContext(CONTEXTO_CLIMA_DETALLADO)) != null) {
			paramCiudad = ctx.getParameters().get(PARAM_CIUDAD).toString();
		} 
		
		if (paramCiudad == null) {
			paramCiudad = resultAi.getParameters().get(PARAM_CIUDAD).toString();
		}
		
		return paramCiudad;
	}
	protected ResponseMessageApiAiV2 armarResponseActualV2(String speech, String source) {
		List<QuickReplyGeneral> opcionesClimaReply = Arrays.asList(
				new TextQuickReply("Para Hoy", PAYLOAD_POR_HORAS),
				new TextQuickReply("Para la Semana", PAYLOAD_POR_SEMANA)
				);
		return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech, opcionesClimaReply)), null);
	}
	protected ResponseMessageApiAiV2 armarMensajeErrorV2(final String speech, final String source) {
		return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech)), null);
	}
	
protected ResponseMessageApiAiV2 validarClimaEnBDV2(Clima clima, String source) throws UnsupportedEncodingException {
		
		if (clima != null) {
			
			if (TIPO_ACTUAL.equals(clima.getTipo())) {
				return armarResponseActualV2(clima.getVisualizacion(), source);
			} else {
				final String urlImagen =  UrlUtil.armarUrlImagenClima(clima.getVisualizacion());
				final String speech = String.format("El clima de %s", source);
				final PayloadResponse weatherData = new PayloadResponse(new RichMessageV2(new Attachment(Attachment.IMAGE, new MediaPayload(urlImagen))));
				return new ResponseMessageApiAiV2(speech, source, weatherData, null);
			}
			
		}
		
		return null;
	}
}
