package com.holalola.webhook.facebook.templates;

public class TextQuickReply extends QuickReplyGeneral {
	
	private String title;
	private String payload;
	private String image_url;
	
	public TextQuickReply(String title, String payload, String image_url) {
		this(title, payload);
		this.image_url = image_url;
	}
	
	public TextQuickReply(String title, String payload) {
		this.content_type = TEXTO;
		this.title = title;
		this.payload = payload;
	}

	public String getTitle() {
		return title;
	}

	public String getPayload() {
		return payload;
	}

	public String getImage_url() {
		return image_url;
	}
}
