package com.componente_practico.webhook.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Request {
	private String responseId;
	private String session;
	private QueryResult queryResult;

	private OriginalDetectIntentRequest originalDetectIntentRequest;
	
	
	
	
	public String getResponseId() {
		return responseId;
	}
	public void setResponseId(String responseId) {
		this.responseId = responseId;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	public QueryResult getQueryResult() {
		return queryResult;
	}
	public void setQueryResult(QueryResult queryResult) {
		this.queryResult = queryResult;
	}
	public OriginalDetectIntentRequest getOriginalDetectIntentRequest() {
		return originalDetectIntentRequest;
	}
	public void setOriginalDetectIntentRequest(OriginalDetectIntentRequest originalDetectIntentRequest) {
		this.originalDetectIntentRequest = originalDetectIntentRequest;
	}
	public Request(QueryResult queryResult, OriginalDetectIntentRequest originalDetectIntentRequest) {
		super();
		this.queryResult = queryResult;
		this.originalDetectIntentRequest = originalDetectIntentRequest;
	}
	
	public Request(QueryResult queryResult) {
		super();
		this.queryResult = queryResult;
	}
	

	
	
	
}
