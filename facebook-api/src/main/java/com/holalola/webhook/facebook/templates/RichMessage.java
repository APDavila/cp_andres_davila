package com.holalola.webhook.facebook.templates;

public class RichMessage extends FacebookRequestGeneral {

	private Attachment attachment;

	public RichMessage(Attachment attachment) {
		this.attachment = attachment;
	}

	public Attachment getAttachment() {
		return attachment;
	}

}
