package com.componente_practico.webhook.model.yahoo.weather;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Diagnostics {

	private String publiclyCallable;
	private List<Url> url;
	private Javascript javascript;
	@SerializedName("user-time")
	private String userTime;
	@SerializedName("service-time")
	private String serviceTime;
	@SerializedName("build-version")
	private String buildVersion;

	public String getPubliclyCallable() {
		return publiclyCallable;
	}

	public void setPubliclyCallable(String publiclyCallable) {
		this.publiclyCallable = publiclyCallable;
	}

	public List<Url> getUrl() {
		return url;
	}

	public void setUrl(List<Url> url) {
		this.url = url;
	}

	public Javascript getJavascript() {
		return javascript;
	}

	public void setJavascript(Javascript javascript) {
		this.javascript = javascript;
	}

	public String getUserTime() {
		return userTime;
	}

	public void setUserTime(String userTime) {
		this.userTime = userTime;
	}

	public String getServiceTime() {
		return serviceTime;
	}

	public void setServiceTime(String serviceTime) {
		this.serviceTime = serviceTime;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

}
