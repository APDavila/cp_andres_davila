package com.holalola.webhook.facebook.payload;

public class AttachmentIdPayload extends PayloadGeneral {
	
	private String attachment_id;

	public AttachmentIdPayload(String attachment_id) {
		this.attachment_id = attachment_id;
	}

	public String getAttachment_id() {
		return attachment_id;
	}

}
