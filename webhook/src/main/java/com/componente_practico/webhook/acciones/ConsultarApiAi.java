package com.componente_practico.webhook.acciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.holalola.webhook.facebook.response.message.Messaging;

import ai.api.AIConfiguration;
import ai.api.AIConfiguration.SupportedLanguages;
import ai.api.AIDataService;
import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;


public class ConsultarApiAi {
	
	final Logger log = LoggerFactory.getLogger(ConsultarApiAi.class);
	
	//private static final String clientApiKey = "8d13cdb9fa6d4559af08ae21a0d83947"; // pibi
	private static final String clientApiKey = System.getProperty("lola.api.ai.token");
	private static final AIConfiguration configuration = new AIConfiguration(clientApiKey, SupportedLanguages.Spanish); 
	
	public AIResponse consultarApiAi(Messaging mensajeDeUsuarioFacebbok) throws AIServiceException {
		
		final String query = mensajeDeUsuarioFacebbok.getMessage().getText();
		final String sessionId = mensajeDeUsuarioFacebbok.getSender().getId();
		
		AIServiceContext serviceContext = AIServiceContextBuilder.buildFromSessionId(sessionId);
		AIDataService dataService = new AIDataService(configuration, serviceContext);

		AIResponse response = dataService.request(new AIRequest(query));

		final int statusCode = response.getStatus().getCode();
		if (statusCode == 200 || statusCode == 206) {
			log.info("Api speech {}", response.getResult().getFulfillment().getSpeech());
		} else {
			log.error("Api error. Status code: {}, Mensaje: ", response.getStatus().getCode(), response.getStatus().getErrorDetails());
		}
		
		return response;
		
	}

}
