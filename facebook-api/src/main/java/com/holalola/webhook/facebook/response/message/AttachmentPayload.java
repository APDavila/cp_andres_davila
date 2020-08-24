package com.holalola.webhook.facebook.response.message;

public class AttachmentPayload {

	private String url;
	private Long sticker_id;

	public AttachmentPayload(String url, Long sticker_id) {
		this.url = url;
		this.sticker_id = sticker_id;
	}

	public String getUrl() {
		return url;
	}

	public Long getSticker_id() {
		return sticker_id;
	}

}
