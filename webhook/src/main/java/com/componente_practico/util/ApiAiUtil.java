package com.componente_practico.util;

import static com.holalola.util.TextoUtil.esVacio;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.webhook.facebook.ConstantesFacebook;
import com.holalola.webhook.facebook.payload.AttachmentIdPayload;
import com.holalola.webhook.facebook.payload.GenericTemplatePayload;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.payload.ReceiptTemplatePayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.ButtonRichMessageV2;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.RichMessageV2;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;
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

					/*
					 * for (Object key : resultAi.getParameters().keySet()) { //based on you key
					 * types String keyStr = (String)key; Object keyvalue =
					 * resultAi.getParameters().get(keyStr);
					 * 
					 * //Print key and value System.out.println("key: "+ keyStr + " value: " +
					 * keyvalue); }
					 */
				}
			}

			if (valorParametro == null || valorParametro.trim().length() <= 0) {

				/*
				 * for (Object key : resultAi.getParameters().keySet()) { //based on you key
				 * types String keyStr = (String)key; Object keyvalue =
				 * resultAi.getParameters().get(keyStr);
				 * 
				 * //Print key and value System.out.println("key1: "+ keyStr + " value1: " +
				 * keyvalue); }
				 */
				valorParametro = resultAi.getParameters().get(nombreParametro).getAsString();
			}

			if (valorParametro == null && nombreParametro.contains(".original")) {
				nombreParametro = nombreParametro.replace(".original", "");
				log.error("obtenerValorParametroV2 valorParametro Vito null " + nombreParametro + " - " + valorParametro
						+ " - " + nombreContexto + " {} ", resultAi.toString());
				valorParametro = obtenerValorParametro(resultAi, nombreParametro, nombreContexto);
			}

		} catch (Exception e) {
			// log.error("No se pudo obtener el parametro {} ", nombreParametro);
			log.error("No se pudo obtener el resultAi " + nombreParametro + " - " + nombreContexto + " {} ",
					resultAi.toString());
		}

		// System.out.println("--------------------------- valorParametro: " +
		// valorParametro);

		return valorParametro;

	}

	public static ResponseMessageApiAi armarRespuestaTextMessage(String speech, String source) {
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, source);
	}

	public static ResponseMessageApiAi armarRespuestaTextMessageConQuickReply(String speech, String source,
			List<QuickReplyGeneral> quickReplies) {
		final List<QuickReplyGeneral> quickRepliesFinal = quickReplies.stream()
				.limit(ConstantesFacebook.MAX_QUICK_REPLIES).collect(Collectors.toList());
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech, quickRepliesFinal)), null,
				source);
	}

	public static List<QuickReplyGeneral> armarRespuestaTextMessageConQuickReplyLista(String speech, String source,
			List<QuickReplyGeneral> quickReplies) {
		final List<QuickReplyGeneral> quickRepliesFinal = quickReplies.stream()
				.limit(ConstantesFacebook.MAX_QUICK_REPLIES).collect(Collectors.toList());
		return quickRepliesFinal;
	}

	public static ResponseMessageApiAi armarRespuestaConButton(String speech, List<ButtonGeneral> buttons,
			String source) {
		return new ResponseMessageApiAi(speech, speech, new Data(new ButtonRichMessage(speech, buttons)), null, source);
	}

	public static ResponseMessageApiAi armarRespuestaGenericTemplate(List<Element> elements, String speech,
			String source) {
		return new ResponseMessageApiAi(speech, speech,
				armarGenericTemplate(elements, GenericTemplatePayload.HORIZONTAL_ASPECT_RATIO), null, source);
	}

	public static ResponseMessageApiAi armarRespuestaGenericTemplateCuadrada(List<Element> elements, String speech,
			String source) {
		return new ResponseMessageApiAi(speech, speech,
				armarGenericTemplate(elements, GenericTemplatePayload.SQUARE_ASPECT_RATIO), null, source);
	}

	public static ResponseMessageApiAi armarRespuestaReceiptTemplate(String speech,
			ReceiptTemplatePayload receiptTemplatePayload, String source) {
		return new ResponseMessageApiAi(speech, speech,
				new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, receiptTemplatePayload))), null, source);
	}

	public static ResponseMessageApiAi armarRespuestaVideoMessage(String urlPelicula, String attahcmentId,
			String source) {
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
		final List<Element> elementsFinal = elements.stream().limit(ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE)
				.collect(Collectors.toList());
		return new Data(new RichMessage(
				new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elementsFinal, imageAspectRatio))));
	}

	public static List<QuickReplyGeneral> armarQuickRepliesCantidades(int desde, int hasta) {
		return IntStream.rangeClosed(desde, hasta).boxed().map(c -> {
			return new TextQuickReply(c.toString(), c.toString());
		}).collect(Collectors.toList());
	}

	// *************************************************Dialog
	// V2********************************************************
	public static String obtenerValorParametroV2(QueryResult resultAi, String nombreParametro, String nombreContexto) {
		String valorParametro = null;
		try {

			// log.error("obtenerValorParametroV2 valorParametro VitoYoyossss "+ " - " +
			// nombreParametro +" - "+ nombreContexto +" {} ", resultAi.toString());

			OutputContexts ctx;
			if (nombreContexto.length() > 0 && nombreContexto != null) {
				if ((ctx = resultAi.getContext(nombreContexto)) != null) {
					valorParametro = ctx.getParameters().get(nombreParametro).toString();
				}

				if (valorParametro == null || valorParametro.trim().length() <= 0) {
					try {
						valorParametro = resultAi.getParameters().get(nombreParametro).toString();
					} catch (Exception e) {
						valorParametro = null;
					}
				}
			}

			if (valorParametro == null && nombreParametro.contains(".original")) {
				if (valorParametro == null) {
					valorParametro = resultAi.getOutputContexts().get(0).getParameters().get(nombreParametro)
							.toString();
				}
				nombreParametro = nombreParametro.replace(".original", "");
				valorParametro = obtenerValorParametroV2(resultAi, nombreParametro, nombreContexto);

				if (valorParametro == null) {
					valorParametro = resultAi.getOutputContexts().get(0).getParameters().get(nombreParametro)
							.toString();
				}
			}

			if (valorParametro == null) {
				valorParametro = resultAi.getOutputContexts().get(0).getParameters().get(nombreParametro).toString();
			}
			valorParametro = valorParametro.trim() + " ";
			if (valorParametro.contains(".0 ") || valorParametro.contains(",0 ")) {
				valorParametro = valorParametro.replace(".0", " ").replace(",0", " ").trim();
			}

			valorParametro = valorParametro.trim();
			// log.error("obtenerValorParametroV2 valorParametro Vito "+ valorParametro + "
			// - " + nombreParametro +" - "+ nombreContexto +" {} ", resultAi.toString());

		} catch (Exception e) {
			log.error("No se pudo obtener el resultAi " + nombreParametro + " - " + nombreContexto + " {} ",
					resultAi.toString());
		}
		return valorParametro;
	}

	public static String obtenerValorParametroSCV2(QueryResult resultAi, String nombreParametro,
			String nombreContexto) {

		String valorParametro = null;
		try {

			OutputContexts ctx;

			if ((ctx = resultAi.getOutputContexts().get(0)) != null) {
				valorParametro = resultAi.getOutputContexts().get(0).getParameters().get(nombreParametro).toString();
			}

			if (valorParametro == null || valorParametro.trim().length() <= 0) {
				try {
					valorParametro = resultAi.getParameters().get(nombreParametro).toString();
				} catch (Exception e) {
					valorParametro = null;
				}
			}

			valorParametro = valorParametro.trim() + " ";

			if (valorParametro.contains(".0 ") || valorParametro.contains(",0 ")) {
				valorParametro = valorParametro.replace(".0", " ").replace(",0", " ").trim();
			}

			valorParametro = valorParametro.trim();

			if (valorParametro == null && nombreParametro.contains(".original")) {
				nombreParametro = nombreParametro.replace(".original", "");
				valorParametro = obtenerValorParametroSCV2(resultAi, nombreParametro, nombreContexto);
			}

			// log.error("obtenerValorParametroV2 valorParametro Vito "+ valorParametro + "
			// - " + nombreParametro +" - "+ nombreContexto +" {} ", resultAi.toString());

		} catch (Exception e) {
			log.error("No se pudo obtener el resultAi " + nombreParametro + " - " + nombreContexto + " {} ",
					resultAi.toString());
		}
		return valorParametro;
	}

	public static ResponseMessageApiAiV2 armarRespuestaTextMessageConQuickReplyV2(String speech, String source,
			List<QuickReplyGeneral> quickReplies) {
		final List<QuickReplyGeneral> quickRepliesFinal = quickReplies.stream()
				.limit(ConstantesFacebook.MAX_QUICK_REPLIES).collect(Collectors.toList());
		return new ResponseMessageApiAiV2(speech, source,
				new PayloadResponse(new TextMessageV2(speech, quickRepliesFinal)), null);
	}

	public static ResponseMessageApiAiV2 armarRespuestaGenericTemplateV2(List<Element> elements, String speech,
			String source) {
		return new ResponseMessageApiAiV2(speech, source,
				armarGenericTemplateV2(elements, GenericTemplatePayload.HORIZONTAL_ASPECT_RATIO), null);
	}

	private static PayloadResponse armarGenericTemplateV2(List<Element> elements, String imageAspectRatio) {
		final List<Element> elementsFinal = elements.stream().limit(ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE)
				.collect(Collectors.toList());
		return new PayloadResponse(new RichMessageV2(
				new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elementsFinal, imageAspectRatio))));
	}

	public static ResponseMessageApiAiV2 armarRespuestaTextMessageV2(String speech, String source) {
		return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech)), null);
	}

	public static ResponseMessageApiAiV2 armarRespuestaConButtonV2(String speech, List<ButtonGeneral> buttons,
			String source) {
		return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new ButtonRichMessageV2(speech, buttons)),
				null);
	}

	public static ResponseMessageApiAiV2 armarRespuestaGenericTemplateCuadradaV2(List<Element> elements, String speech,
			String source) {
		return new ResponseMessageApiAiV2(speech, source,
				armarGenericTemplateV2(elements, GenericTemplatePayload.SQUARE_ASPECT_RATIO), null);
	}

}
