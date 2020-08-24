package com.holalola.webhook.facebook.response.message;

import com.holalola.webhook.facebook.AttachmentGeneral;

public class Attachment extends AttachmentGeneral {

	private AttachmentPayload payload;

	public Attachment(String type, AttachmentPayload payload) {
		this.type = type;
		this.payload = payload;
	}

	public AttachmentPayload getPayload() {
		return payload;
	}

}
