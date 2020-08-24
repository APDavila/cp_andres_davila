package com.holalola.webhook.facebook.templates;

public class RichMessageV2 extends Facebook{
	private Attachment attachment;

	public RichMessageV2(Attachment attachment) {
		this.attachment = attachment;
	}

	public Attachment getAttachment() {
		return attachment;
	}
}
