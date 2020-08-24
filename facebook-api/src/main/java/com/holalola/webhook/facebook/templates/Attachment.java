package com.holalola.webhook.facebook.templates;

import com.holalola.webhook.facebook.AttachmentGeneral;
import com.holalola.webhook.facebook.payload.PayloadGeneral;

public class Attachment extends AttachmentGeneral {
	
	private PayloadGeneral payload;

	public Attachment(String type, PayloadGeneral payload) {
		this.type = type;
		this.payload = payload;
	}

	public PayloadGeneral getPayload() {
		return payload;
	}

}
