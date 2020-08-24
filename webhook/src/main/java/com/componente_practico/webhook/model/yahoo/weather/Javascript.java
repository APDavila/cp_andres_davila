package com.componente_practico.webhook.model.yahoo.weather;

import com.google.gson.annotations.SerializedName;

public class Javascript {

	@SerializedName("execution-start-time")
	private String executionStartTime;
	@SerializedName("execution-stop-time")
	private String executionStopTime;
	@SerializedName("execution-time")
	private String executionTime;
	@SerializedName("instructions-used")
	private String instructionsUsed;
	@SerializedName("table-name")
	private String tableName;

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

	public String getInstructionsUsed() {
		return instructionsUsed;
	}

	public void setInstructionsUsed(String instructionsUsed) {
		this.instructionsUsed = instructionsUsed;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
