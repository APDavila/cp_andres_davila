package com.holalola.webhook.facebook;

public abstract class MensajeParaFacebookGeneralV2 {
protected Recipient recipient;
	
	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}
}
