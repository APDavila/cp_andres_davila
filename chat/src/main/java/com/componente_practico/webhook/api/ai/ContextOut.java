package com.componente_practico.webhook.api.ai;

import java.util.Map;

public class ContextOut {

	private String name;
	private int lifespan;
	private Map<String, Object> parameters; // tal vez JsonElement en lugar de object?

	public ContextOut(String name, int lifespan, Map<String, Object> parameters) {
		this.name = name;
		this.lifespan = lifespan;
		this.parameters = parameters;
	}

	public String getName() {
		return name;
	}

	public int getLifespan() {
		return lifespan;
	}

	public Map<String, Object> getParameters() {
		return parameters;
	}

}
