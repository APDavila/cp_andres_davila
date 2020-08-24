package com.componente_practico.webhook.api.ai;

import com.holalola.webhook.facebook.templates.FacebookRequestGeneral;

public class Data {

	private FacebookRequestGeneral facebook;

	public Data(FacebookRequestGeneral facebook) {
		this.facebook = facebook;
	}

	public FacebookRequestGeneral getFacebook() {
		return facebook;
	}

}
