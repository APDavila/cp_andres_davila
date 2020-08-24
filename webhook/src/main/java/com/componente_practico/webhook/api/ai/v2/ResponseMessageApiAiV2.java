package com.componente_practico.webhook.api.ai.v2;

import java.util.Collections;
import java.util.List;

public class ResponseMessageApiAiV2 {
	private String fulfillmentText;
	private String source;
	private PayloadResponse payload;
	private List<OutputContexts> outputContexts;
	
	
	public ResponseMessageApiAiV2() {
		super();
	}
	
	
	public ResponseMessageApiAiV2(String fulfillmentText, String source, PayloadResponse payload,
			List<OutputContexts> outputContexts) {
		super();
		this.fulfillmentText = fulfillmentText;
		this.source = source;
		this.payload = payload;
		this.outputContexts = outputContexts == null ? Collections.emptyList() : outputContexts;
		
	}


	public String getFulfillmentText() {
		return fulfillmentText;
	}
	public void setFulfillmentText(String fulfillmentText) {
		this.fulfillmentText = fulfillmentText;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public PayloadResponse getPayload() {
		return payload;
	}
	public void setPayload(PayloadResponse payload) {
		this.payload = payload;
	}
	public List<OutputContexts> getOutputContexts() {
		return outputContexts;
	}
	public void setOutputContexts(List<OutputContexts> outputContexts) {
		this.outputContexts = outputContexts;
	}
}
