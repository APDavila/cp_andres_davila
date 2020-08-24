package com.holalola.webhook.facebook.templates;

public class DefaultAction {
	
	//public static final String HEIGHT_RATIO_TALL = "tall";
	//public static final String HEIGHT_RATIO_COMPACT = "compact";
	//public static final String HEIGHT_RATIO_FULL = "full";

	private final String type = "web_url";
	private String url;
	private boolean messenger_extensions;
	private String webview_height_ratio;
	private String fallback_url;

	public DefaultAction(String url, boolean messenger_extensions, String webview_height_ratio, String fallback_url) {
		this.url = url;
		this.messenger_extensions = messenger_extensions;
		this.webview_height_ratio = webview_height_ratio;
		this.fallback_url = fallback_url;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public boolean isMessenger_extensions() {
		return messenger_extensions;
	}

	public String getWebview_height_ratio() {
		return webview_height_ratio;
	}

	public String getFallback_url() {
		return fallback_url;
	}

}
