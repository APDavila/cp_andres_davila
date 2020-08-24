package com.holalola.webhook.facebook.templates;

import com.holalola.webhook.facebook.MensajeParaFacebookGeneral;
import com.holalola.webhook.facebook.Recipient;

public class SenderActionParaFacebook extends MensajeParaFacebookGeneral {

	public static final String TYPING_ON = "typing_on";
	public static final String TYPING_OFF = "typing_off";
	public static final String MARK_SEEN = "mark_seen";

	private String sender_action;

	public SenderActionParaFacebook(Recipient recipient, String sender_action) {
		this.recipient = recipient;
		this.sender_action = sender_action;
	}

	public String getSender_action() {
		return sender_action;
	}

	public void setSender_action(String sender_action) {
		this.sender_action = sender_action;
	}

}
