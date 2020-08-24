package com.componente_practico.webhook.api.ai.v2;

import com.holalola.webhook.facebook.templates.Facebook;

public class PayloadResponse {
	private Facebook facebook;

	
	
	public PayloadResponse(Facebook facebook) {
		super();
		this.facebook = facebook;
	}

	public Facebook getFacebook() {
		return facebook;
	}

	public void setFacebook(Facebook facebook) {
		this.facebook = facebook;
	}
	
	
}
