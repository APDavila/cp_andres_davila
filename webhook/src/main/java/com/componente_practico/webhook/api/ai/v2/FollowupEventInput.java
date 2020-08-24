package com.componente_practico.webhook.api.ai.v2;

import java.util.Map;

public class FollowupEventInput {
	private String name;
	private String languajeCode;
	private Map<String, Object> parameters;
	
	
	
	public FollowupEventInput() {
		super();
	}



	public FollowupEventInput(String name, String languajeCode, Map<String, Object> parameters) {
		super();
		this.name = name;
		this.languajeCode = languajeCode;
		this.parameters = parameters;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getLanguajeCode() {
		return languajeCode;
	}



	public void setLanguajeCode(String languajeCode) {
		this.languajeCode = languajeCode;
	}



	public Map<String, Object> getParameters() {
		return parameters;
	}



	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	
	
	
}
