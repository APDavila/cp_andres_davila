package com.holalola.webhook.facebook;

import com.holalola.webhook.facebook.templates.Facebook;

public class MensajeParaFacebookV2 extends MensajeParaFacebookGeneralV2 {
private Facebook message;
	
	public MensajeParaFacebookV2() {}
	
	public MensajeParaFacebookV2(String facebookRecipientId, Facebook message) {
		super.recipient = new Recipient(facebookRecipientId);
		this.message = message;
	}

	public Facebook getMessage() {
		return message;
	}

	public void setMessage(Facebook message) {
		this.message = message;
	}
}
