package com.holalola.webhook.facebook.templates;

public class PostbackButton extends ButtonGeneral {

	private static final String TYPE_POSTBACK = "postback";
	private String payload;

	public PostbackButton(String title, String payload) {
		this.type = TYPE_POSTBACK;
		this.title = title;
		this.payload = payload;
	}

	public String getPayload() {
		return payload;
	}

}
