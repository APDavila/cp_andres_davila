package com.componente_practico.webhook.v2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.JsonElement;

import ai.api.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryResult {
	private String queryText;
	private Map<String, Object> parameters;
	private boolean allRequiredParamsPresent;
	private String fulfillmentText;
	private List<FulfillmentMessages> fulfillmentMessages;
	private List<OutputContexts> outputContexts;
	private Intent intent;
	private double intentDetectionConfidence;
	private double speechDetectionConfidence;
	private HashMap<String, JsonElement> diagnosticInfo;
	private String languageCode;
	private String action;

	public String getQueryText() {
		return queryText;
	}

	public void setQueryText(String queryText) {
		this.queryText = queryText;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public boolean isAllRequiredParamsPresent() {
		return allRequiredParamsPresent;
	}

	public void setAllRequiredParamsPresent(boolean allRequiredParamsPresent) {
		this.allRequiredParamsPresent = allRequiredParamsPresent;
	}

	public String getFulfillmentText() {
		return fulfillmentText;
	}

	public void setFulfillmentText(String fulfillmentText) {
		this.fulfillmentText = fulfillmentText;
	}

	public List<FulfillmentMessages> getFulfillmentMessages() {
		return fulfillmentMessages;
	}

	public void setFulfillmentMessages(List<FulfillmentMessages> fulfillmentMessages) {
		this.fulfillmentMessages = fulfillmentMessages;
	}

	public List<OutputContexts> getOutputContexts() {
		return outputContexts;
	}

	public void setOutputContexts(List<OutputContexts> outputContexts) {
		this.outputContexts = outputContexts;
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public double getIntentDetectionConfidence() {
		return intentDetectionConfidence;
	}

	public void setIntentDetectionConfidence(double intentDetectionConfidence) {
		this.intentDetectionConfidence = intentDetectionConfidence;
	}

	public double getSpeechDetectionConfidence() {
		return speechDetectionConfidence;
	}

	public void setSpeechDetectionConfidence(double speechDetectionConfidence) {
		this.speechDetectionConfidence = speechDetectionConfidence;
	}

	public HashMap<String, JsonElement> getDiagnosticInfo() {
		return diagnosticInfo;
	}

	public void setDiagnosticInfo(HashMap<String, JsonElement> diagnosticInfo) {
		this.diagnosticInfo = diagnosticInfo;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public OutputContexts getContext(String name) {
		if (StringUtils.isEmpty(name)) {
			name = "generic";
			// throw new IllegalArgumentException("name argument must be not empty");
		}

		if (outputContexts == null) {
			return null;
		}

		for (final OutputContexts c : outputContexts) {
			if (c.getName().contains(name)) {
				return c;
			}
		}

		return null;
	}

	public QueryResult(List<OutputContexts> outputContexts, Intent intent, String action) {
		super();
		this.outputContexts = outputContexts;
		this.intent = intent;
		this.action = action;
	}

	public QueryResult(String queryText, Map<String, Object> parameters, boolean allRequiredParamsPresent,
			String fulfillmentText, List<OutputContexts> outputContexts, Intent intent,
			double intentDetectionConfidence, String languageCode, String action) {
		super();
		this.queryText = queryText;
		this.parameters = parameters;
		this.allRequiredParamsPresent = allRequiredParamsPresent;
		this.fulfillmentText = fulfillmentText;
		this.outputContexts = outputContexts;
		this.intent = intent;
		this.intentDetectionConfidence = intentDetectionConfidence;
		this.languageCode = languageCode;
		this.action = action;
	}

	public QueryResult(String action) {
		super();
		this.action = action;
	}

}
