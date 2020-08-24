package com.holalola.webhook.facebook.payload;

public class MediaPayload extends PayloadGeneral {

	private String url;
	private boolean is_reusable;
	
	public MediaPayload(String url) {
		this.url = url;
		this.is_reusable = true;
	}

	public String getUrl() {
		return url;
	}
	
	public boolean isIs_reusable() {
		return is_reusable;
	}
}
