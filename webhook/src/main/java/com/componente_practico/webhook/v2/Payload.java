package com.componente_practico.webhook.v2;

import ai.api.model.Message;
import ai.api.model.OriginalRequestPostback;
import ai.api.model.Recipient;

public class Payload {
	private Data data;
	
	public Payload() {
		super();
	}

	public Payload(Data data) {
		super();
		this.data = data;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
	
}
