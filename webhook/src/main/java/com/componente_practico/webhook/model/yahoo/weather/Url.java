package com.componente_practico.webhook.model.yahoo.weather;

import com.google.gson.annotations.SerializedName;

public class Url {

	@SerializedName("execution-start-time")
	private String executionStartTime;
	@SerializedName("execution-stop-time")
	private String executionStopTime;
	@SerializedName("execution-time")
	private String executionTime;
	private String content;

	public String getExecutionStartTime() {
		return executionStartTime;
	}

	public void setExecutionStartTime(String executionStartTime) {
		this.executionStartTime = executionStartTime;
	}

	public String getExecutionStopTime() {
		return executionStopTime;
	}

	public void setExecutionStopTime(String executionStopTime) {
		this.executionStopTime = executionStopTime;
	}

	public String getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(String executionTime) {
		this.executionTime = executionTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
