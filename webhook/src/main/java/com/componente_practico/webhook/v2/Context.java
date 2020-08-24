package com.componente_practico.webhook.v2;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Context {
	private String name;
	private int lifespanCount;
	private Map<String, Object> parameters;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getLifespanCount() {
		return lifespanCount;
	}
	public void setLifespanCount(int lifespanCount) {
		this.lifespanCount = lifespanCount;
	}
	public Map<String, Object> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}
	
	
}
