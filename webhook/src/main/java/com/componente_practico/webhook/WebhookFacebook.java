package com.componente_practico.webhook;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.webhook.acciones.ConsultarApiAi;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeExternoParaFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.response.InformacionUsuarioFacebook;
import com.holalola.webhook.facebook.response.message.Entry;
import com.holalola.webhook.facebook.response.message.MensajeDesdeFacebook;
import com.holalola.webhook.facebook.response.message.Messaging;
import com.holalola.webhook.facebook.templates.TextMessage;

import ai.api.AIServiceException;
import ai.api.GsonFactory;
import ai.api.model.AIResponse;



@Stateless
@Path("/facebook")
public class WebhookFacebook {
	
	final Logger log = LoggerFactory.getLogger(WebhookFacebook.class);
	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	@GET
    public Response doWebhook(@QueryParam("hub.mode") String hubMode,
    		@QueryParam("hub.challenge") String hubChallenge,
    		@QueryParam("hub.verify_token") String hubVerifyToken) {
		
		//hub.mode - The string "subscribe" is passed in this parameter
		//hub.challenge - A random string
		//hub.verify_token - The verify_token value you specified when you created the subscription
		if (log.isDebugEnabled()) {
			log.debug("\n--------\nValidacion de Facebbok:\nhub.mode {}\nhub.challenge {}\nhub.verify_token {}", hubMode, hubChallenge, hubVerifyToken);
		}
		
		if ("probandoWebhook".equals(hubVerifyToken))
			return Response.status(200).entity(hubChallenge).build();
		
		return Response.status(403).build();
    }
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    public Response doWebhook(String facebookMessage) {
		
		log.info("\n==Mensaje desde facebook===\n"+facebookMessage+"\n=====\n");
			
		MensajeDesdeFacebook mensajeRecibido = GSON.fromJson(facebookMessage, MensajeDesdeFacebook.class);
		
		if ("page".equals(mensajeRecibido.getObject())) {
			
			if (mensajeRecibido != null) {
				for(Entry entry : mensajeRecibido.getEntry()) {
					for(Messaging messaging : entry.getMessaging()){
							procesarMensaje(messaging);
					}
				}
			};	
		
		}
		
		return Response.status(200).build();
	}
	
	private void procesarMensaje(Messaging messaging) {
		
		//if (messaging.getOptin != null) procesarAutenticacion() {}
	        if (messaging.getMessage() != null) {
	          procesarMensajeDeUsuario(messaging);
	        
	        } else if (messaging.getDelivery() != null) {
	          procesarConfirmacionDeRecepcion(messaging);
	        
	        } else if (messaging.getPostback() != null) {
	          procesarPostback(messaging);
	        
	        } else if (messaging.getRead() != null) {
	          procesarConfirmacionDeLectura(messaging);
	        
	          //} else if (messaging.account_linking) {
	          //receivedAccountLink(messaging);
	        
	        } else {
	          log.error("No se sabe que tipo de mensaje se recibio.... Lanzar error");
	        }
	}
	
	private void procesarMensajeDeUsuario(Messaging mensajeDeUsuarioFacebbok) {
		try {
			
			//mostrarAccionDeTipeo(mensajeDeUsuarioFacebbok);
			
			ConsultarApiAi apiAi = new ConsultarApiAi();
			AIResponse aiResponse = apiAi.consultarApiAi(mensajeDeUsuarioFacebbok);
			
			
			if (aiResponse.getResult().getMetadata().isWebhookUsed() &&
				!aiResponse.getResult().isActionIncomplete()) {
				MensajeExternoParaFacebook mensajeParaFacebook = new MensajeExternoParaFacebook();
				mensajeParaFacebook.setRecipient(mensajeDeUsuarioFacebbok.getSender());
				
				Map<String, JsonElement> dataMap = aiResponse.getResult().getFulfillment().getData();
				mensajeParaFacebook.setMessage(dataMap.get("facebook"));
				ConsultarFacebook.postToFacebook(mensajeParaFacebook, propiedadesLola.facebookToken);
				
				return;
			}
			
			final String mensajeRespuesta = aiResponse.getResult().getFulfillment().getSpeech();
			
			InformacionUsuarioFacebook usuarioFacebook = ConsultarFacebook.obtenerInformacionDeUsuario(mensajeDeUsuarioFacebbok.getSender().getId(), propiedadesLola.facebookToken);
			String mensaje = String.format("%s :grinning:, %s",usuarioFacebook.getFirst_name(), mensajeRespuesta);
			String result = mensaje;// EmojiParser.parseToUnicode(mensaje);
			
			
			MensajeParaFacebook mensajeParaFacebbok = new MensajeParaFacebook();
			mensajeParaFacebbok.setRecipient(mensajeDeUsuarioFacebbok.getSender());
			mensajeParaFacebbok.setMessage(new TextMessage(result));
			
			ConsultarFacebook.postToFacebook(mensajeParaFacebbok, propiedadesLola.facebookToken);
			
			/*
			for (ResponseMessage rs : aiResponse.getResult().getFulfillment().getMessages()) {
				System.out.println("\n\n\n\n\n---->RS:" + rs);
				//ResponseSpeech;
				
				if (rs instanceof ResponseQuickReply) {
					ResponseQuickReply quickReply = (ResponseQuickReply) rs
					List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("", rs.g))
					
					MensajeParaFacebook mensajeParaFacebbok2 = new MensajeParaFacebook();
					mensajeParaFacebbok2.setRecipient(mensajeDeUsuarioFacebbok.getSender());
					mensajeParaFacebbok2.setMessage(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(quickReply.getTitle(), buttons)));
				}
			}
			*/
			
			
		} catch (AIServiceException e) {
			log.error("No se pudo procesar mensaje de Facebok por error en aipAi.", e);
		}
	}
	
	
	
	private void procesarConfirmacionDeRecepcion(Messaging messaging) {
		log.info("Procesando confirmacion de recepcion");
	}
	
	private void procesarPostback(Messaging messaging) {
		log.info("Procesando postback.. Payload: " + messaging.getPostback().getPayload());
	}
	
	private void procesarConfirmacionDeLectura(Messaging messaging) {
		log.info("Procesando confirmacion de lectura.. "+messaging.getRead().getSeq());
	}

}
