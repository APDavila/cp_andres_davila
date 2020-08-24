package com.componente_practico.webhook.api.ai.v2;

import java.util.Collections;
import java.util.List;

import com.holalola.webhook.facebook.templates.Facebook;

public class ResponseMessageApiAiV4 {
	private Facebook message;
	
	
	public ResponseMessageApiAiV4() {
		super();
	}
	
	
	public ResponseMessageApiAiV4(String fulfillmentText, String source, Facebook facebook,
			List<OutputContexts> outputContexts) {
		super();
		this.message = facebook;
		
	}


	public Facebook getFacebook() {
		return message;
	}


	public void setFacebook(Facebook facebook) {
		this.message = facebook;
	}
	
	


}
