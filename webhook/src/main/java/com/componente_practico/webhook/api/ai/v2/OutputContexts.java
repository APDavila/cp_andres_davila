package com.componente_practico.webhook.api.ai.v2;

import java.util.Map;

public class OutputContexts {
	private String name;
	private int lifespanCount;
	private Map<String, Object> parameters;
	public OutputContexts() {
		super();
	}
	
	
	
	protected OutputContexts(String name, Map<String, Object> parameters) {
		super();
		this.name = name;
		this.parameters = parameters;
	}



	public OutputContexts(String name, int lifespanCount, Map<String, Object> parameters) {
		super();
		this.name = name;
		this.lifespanCount = lifespanCount;
		this.parameters = parameters;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}



	public int getLifespanCount() {
		return lifespanCount;
	}



	public void setLifespanCount(int lifespanCount) {
		this.lifespanCount = lifespanCount;
	}
	
	
	
	
}
