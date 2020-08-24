package com.holalola.webhook.facebook;

import com.holalola.webhook.facebook.templates.FacebookRequestGeneral;

public class MensajeParaFacebook extends MensajeParaFacebookGeneral {

	private FacebookRequestGeneral message;
	
	public MensajeParaFacebook() {}
	
	public MensajeParaFacebook(String facebookRecipientId, FacebookRequestGeneral message) {
		super.recipient = new Recipient(facebookRecipientId);
		this.message = message;
	}

	public FacebookRequestGeneral getMessage() {
		return message;
	}

	public void setMessage(FacebookRequestGeneral message) {
		this.message = message;
	}

}
