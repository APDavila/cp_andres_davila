package com.componente_practico;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.componente_practico.vo.Prueba;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.AttachmentIdPayload;
import com.holalola.webhook.facebook.payload.GenericTemplatePayload;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.response.message.Attachment;
import com.holalola.webhook.facebook.response.message.Entry;
import com.holalola.webhook.facebook.response.message.MensajeDesdeFacebook;
import com.holalola.webhook.facebook.response.message.Messaging;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.FacebookRequestGeneral;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;

@Path("/prueba")
public class ServicioPrueba {

	private final static Gson GSON = new GsonBuilder().create();
	
	@GET
	@Path("/Saludar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response Saludar()
	{
		Prueba lb_prueba = new Prueba("Victor", "Estinoza", new Date());
		return Response.status(200).entity(lb_prueba).build();
	}
	
	
	@GET
	@Path("/ValidaFacebook")
	@Produces(MediaType.APPLICATION_JSON)
	public Response ValidaFacebook(@QueryParam("hub.mode") String hubMode,
    		@QueryParam("hub.challenge") String hubChallenge,
    		@QueryParam("hub.verify_token") String hubVerifyToken)
	{
		//hub.mode - The string "subscribe" is passed in this parameter
		//hub.challenge - A random string
		//hub.verify_token - The verify_token value you specified when you created the subscription
				
		if ("NuoVito".equals(hubVerifyToken))
			return Response.status(200).entity(hubChallenge).build();
		
		return Response.status(403).build();
	}
	
	@POST
	@Path("/ValidaFacebook") 
	@Consumes(MediaType.APPLICATION_JSON)
    public Response RetornoFac(String facebookMessage) {
		
		System.out.println("\n==Mensaje desde facebook===\n"+facebookMessage+"\n=====\n");
			
		
		
		MensajeDesdeFacebook mensajeRecibido = GSON.fromJson(facebookMessage, MensajeDesdeFacebook.class);
		
		if ("page".equals(mensajeRecibido.getObject())) {
			
			if (mensajeRecibido != null) {
				for(Entry entry : mensajeRecibido.getEntry()) {
					for(Messaging messaging : entry.getMessaging()){
						 if (messaging.getMessage() != null) {
							 procesarMensaje(messaging);					        
					        } 
					}
				}
			};	
		
		}
		
		return Response.status(200).build();
	}
	
	//Procesa el Mensaje recibido desde Facbook
     private void procesarMensaje(Messaging messaging) {
		
		//if (messaging.getOptin != null) procesarAutenticacion() {}
	        if (messaging.getMessage() != null) {
	          procesarMensajeDeUsuario(messaging);
	        
	        } else if (messaging.getDelivery() != null) {
	          //procesarConfirmacionDeRecepcion(messaging);
	        
	        } else if (messaging.getPostback() != null) {
	          System.out.println("Esto es pos "+messaging.getPostback().getPayload());
	        	//procesarPostback(messaging);
	        
	        } else if (messaging.getRead() != null) {
	          //procesarConfirmacionDeLectura(messaging);
	        
	          //} else if (messaging.account_linking) {
	          //receivedAccountLink(messaging);
	        
	        } else {
	        	System.out.println("No se sabe que tipo de mensaje se recibio.... Lanzar error");
	        }
	}
     
     String tokenPaginaFacDeve = "EAABZBnaZAnDJgBAFZAiXWpSAJG4uZBNZBFoqZBpxW9eYJZBKpmi4jv9FiokLgud2DlZBvvl9FqkOEwnjzp1LAcxvuKCuN6Ep09Csp1JZBL8jEZA1jsDlAbTpjGGMsNCcZC3Al7ZB3VLHmuLaEm0VKUyV2xtIGkMh3KYppjYZCZBI2ulqzUSJEom3Hw9TZBZB";
     //Procesar mensaje envieado desde el cliente 
     private void procesarMensajeDeUsuario(Messaging messaging)
     {

    	 FacebookRequestGeneral respuesta;
    	 
    	 //obtener id de destinatatio a enviar el mensaje
    	 String destinatario = messaging.getSender().getId();
    	 String textoUsuario = messaging.getMessage().getText();
    	 String respuestaPorBotenes = "";
    	 
    	 if(messaging.getMessage().getQuick_reply() != null)
    		 respuestaPorBotenes = messaging.getMessage().getQuick_reply().getPayload();
    	 
    	 String textoRespuesta = "";
    	 
    	 List<QuickReplyGeneral> botones = Arrays.asList(
		 			new TextQuickReply("Opcion A", "Selecciono A"),
		 			new TextQuickReply("Opcion B", "Selecciono B"),
		 			new TextQuickReply("Opcion C", "Selecciono C")
		 			);

    	 
    	 switch(textoUsuario.toUpperCase())
    	 {
    	 case "HOLA":
    		 textoRespuesta = "Hola como estas";
    		 respuesta = new TextMessage(textoRespuesta, botones);
    	 case "CHAO":
    		 textoRespuesta = "Chao cuidate";
    		 respuesta = new TextMessage(textoRespuesta, botones);
    		 break;
    	 case "FOTO":
    		 respuesta = new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.IMAGE, new MediaPayload("https://www.istockphoto.com/resources/images/PhotoFTLP/img_75929395.jpg")));
    		 /*textoRespuesta = "Chao cuidate";
    		 respuesta = new TextMessage(textoRespuesta, botones);*/
    		 break;
    		 
    	 case "VIDEO":
    		 respuesta = new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.VIDEO, new MediaPayload("https://www.youtube.com/watch?v=qfI_jwDk4nw&list=TLGGlzAgXBHJKGEyMzExMjAxNw")));
    		 break;
    	 case "VIDEO2":
    		 ///Para ver videos subidos al face con la clase SubirVideo.Java
    		 respuesta = new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.VIDEO, new AttachmentIdPayload("165894107344094")));
    		 /*textoRespuesta = "Chao cuidate";
    		 respuesta = new TextMessage(textoRespuesta, botones);*/
    		 break;
    	 case "CARRUSEL":
    		 respuesta = armaCarrusel();
    		 break;
    	default:
    		textoRespuesta = "No entiendooooooo " + respuestaPorBotenes;
    		respuesta = new TextMessage(textoRespuesta, botones);
    	 }
    	 
    	 
    	 
    	 
    	 ConsultarFacebook.postToFacebook(new MensajeParaFacebook(destinatario, respuesta), tokenPaginaFacDeve);
     }
     
     private RichMessage armaCarrusel()
     {
    	 List<Element> elementosCarrucel = new ArrayList<>();
    	 
    	 //Dos tipos de botos 
    	 	//Pos Bak q manda un texto por actras para procesr
    	 	//WEB  sirven para abrir un popap
    	 
    	 String img1 = "https://i.pinimg.com/736x/7c/89/46/7c8946714a16269a1f90e4364b09dddc--forced-perspective-photography-perspective-photos.jpg";
    	 String img2 = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQk1_iYt-CCbodCfh2w-NgLcbi48oHrYv05G51YtnFskDVwS4G6Xw";
    	 
    	 List<ButtonGeneral> botones = Arrays.asList(
		 			new PostbackButton("Opcion A", "Selecciono A"),
		 			new WebUrlButton("Web", "https://www.google.com"),
		 			new WebUrlButton("Popap", "https://hola-lola.com/pagos.html?1",true)
		 			);
    	 
    	 Element elemento = new Element("Titulo Vito", img1 ,"Sub Titulo", botones);
    	 elementosCarrucel.add(elemento);
    	 Element elemento2 = new Element("Titulo Vito2", img2 ,"Sub Titulo2", botones);
    	 elementosCarrucel.add(elemento2);
    	 
    	 return new RichMessage(new com.holalola.webhook.facebook.templates.Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elementosCarrucel, GenericTemplatePayload.HORIZONTAL_ASPECT_RATIO)));
     }
     
     
}

