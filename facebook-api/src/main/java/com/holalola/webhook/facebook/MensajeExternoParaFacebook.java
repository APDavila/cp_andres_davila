package com.holalola.webhook.facebook;

import com.google.gson.JsonElement;

public class MensajeExternoParaFacebook extends MensajeParaFacebookGeneral {

	JsonElement message;

	public JsonElement getMessage() {
		return message;
	}

	public void setMessage(JsonElement message) {
		this.message = message;
	}

}
