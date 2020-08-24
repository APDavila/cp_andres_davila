package com.componente_practico.webhook.api.ai;

import java.util.Collections;
import java.util.List;

public class ResponseMessageApiAi {

	private String speech;
	private String displayText;
	private Data data;
	private List<ContextOut> contextOut;// "contextOut": [{"name":"weather",
										// "lifespan":2,
										// "parameters":{"city":"Rome"}}]
	private String source;

	public ResponseMessageApiAi(String speech, String displayText, Data data, List<ContextOut> contextOut,
			String source) {

		this.speech = speech;
		this.displayText = displayText;
		this.data = data;
		this.contextOut = contextOut == null ? Collections.emptyList() : contextOut;
		this.source = source;
	}

	public String getSpeech() {
		return speech;
	}

	public String getDisplayText() {
		return displayText;
	}

	public Data getData() {
		return data;
	}

	public List<ContextOut> getContextOut() {
		return contextOut;
	}

	public String getSource() {
		return source;
	}

}
