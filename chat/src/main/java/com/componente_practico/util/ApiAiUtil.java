package com.componente_practico.util;

import static com.componente_practico.util.TextoUtil.esVacio;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.holalola.webhook.facebook.ConstantesFacebook;
import com.holalola.webhook.facebook.payload.AttachmentIdPayload;
import com.holalola.webhook.facebook.payload.GenericTemplatePayload;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.payload.ReceiptTemplatePayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.model.AIOutputContext;
import ai.api.model.Result;

public class ApiAiUtil {
	
	final static Logger log = LoggerFactory.getLogger(ApiAiUtil.class);
	
	public static String obtenerValorParametro(Result resultAi, String nombreParametro, String nombreContexto) {
		
		String valorParametro = null;
		
		try {
			if (!esVacio(nombreContexto)) {
				AIOutputContext ctx;
				if ((ctx = resultAi.getContext(nombreContexto)) != null) {
					valorParametro = ctx.getParameters().get(nombreParametro).getAsString();
				} 
			}
			
			if (valorParametro == null) {
				valorParametro = resultAi.getParameters().get(nombreParametro).getAsString();
			}
		
		} catch(Exception e) {
			log.error("No se pudo obtener el parametro {} ", nombreParametro);
		}
		
		return valorParametro;
		
	}
	
	public static ResponseMessageApiAi armarRespuestaTextMessage(String speech, String source) {
		return new ResponseMessageApiAi(speech , speech, new Data(new TextMessage(speech)), null, source);
	}
	
	public static ResponseMessageApiAi armarRespuestaTextMessageConQuickReply(String speech, String source, List<QuickReplyGeneral> quickReplies) {
		final List<QuickReplyGeneral> quickRepliesFinal = quickReplies.stream().limit(ConstantesFacebook.MAX_QUICK_REPLIES).collect(Collectors.toList());
		return new ResponseMessageApiAi(speech , speech, new Data(new TextMessage(speech, quickRepliesFinal)), null, source);
	}
	
	public static ResponseMessageApiAi armarRespuestaConButton(String speech, List<ButtonGeneral> buttons, String source) {
		return new ResponseMessageApiAi(speech, speech, new Data(new ButtonRichMessage(speech, buttons)), null, source);
	}
	
	public static ResponseMessageApiAi armarRespuestaGenericTemplate(List<Element> elements, String speech, String source) {
		return new ResponseMessageApiAi(speech , speech, armarGenericTemplate(elements, GenericTemplatePayload.HORIZONTAL_ASPECT_RATIO), null, source);
	}
	
	public static ResponseMessageApiAi armarRespuestaGenericTemplateCuadrada(List<Element> elements, String speech, String source) {
		return new ResponseMessageApiAi(speech , speech, armarGenericTemplate(elements, GenericTemplatePayload.SQUARE_ASPECT_RATIO), null, source);
	}
	
	public static ResponseMessageApiAi armarRespuestaReceiptTemplate(String speech, ReceiptTemplatePayload receiptTemplatePayload, String source) {
		return new ResponseMessageApiAi(speech , speech, new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, receiptTemplatePayload))), null, source);
	}
	
	public static ResponseMessageApiAi armarRespuestaVideoMessage(String urlPelicula, String attahcmentId, String source) {
		final String speech = String.format("Url de trariler de pelicula %s", urlPelicula);
		RichMessage richMessage;
		if (esVacio(attahcmentId)) {
			richMessage = new RichMessage(new Attachment(Attachment.VIDEO, new MediaPayload(urlPelicula)));
		} else {
			richMessage = new RichMessage(new Attachment(Attachment.VIDEO, new AttachmentIdPayload(attahcmentId)));
		}
		return new ResponseMessageApiAi(speech, speech, new Data(richMessage), null, source);
	}
	
	private static Data armarGenericTemplate(List<Element> elements, String imageAspectRatio) {
		final List<Element> elementsFinal = elements.stream().limit(ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE).collect(Collectors.toList());
		return new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elementsFinal, imageAspectRatio))));
	}
	
	public static List<QuickReplyGeneral> armarQuickRepliesCantidades(int desde, int hasta) {
		return IntStream.rangeClosed(desde, hasta).boxed().map(c -> {
			return new TextQuickReply(c.toString(), c.toString());
		}).collect(Collectors.toList());
	}

}
