package com.holalola.webhook.facebook.templates;

public class WebUrlButton extends ButtonGeneral {

	public static final String WEBVIEW_HEIGHT_COMPACT="compact"; 
	public static final String WEBVIEW_HEIGHT_TALL="tall";
	public static final String WEBVIEW_HEIGHT_FULL="full";
	
	private static final String TYPE_WEB_URL = "web_url";
	private String url;
	private String webview_height_ratio;
	
	private String webview_share_button;
	private boolean messenger_extensions;  
    private String fallback_url;
	
	public WebUrlButton(String title, String url) {
		this(title, url, WEBVIEW_HEIGHT_TALL);
	}
	
	public WebUrlButton(String title, String url, boolean messengerExtensions) {
		this(title, url, messengerExtensions, WEBVIEW_HEIGHT_TALL);
	}
	
	public WebUrlButton(String title, String url, boolean messengerExtensions, String webview_height_ratio) {
		this.type = TYPE_WEB_URL;
		this.title = title;
		this.webview_height_ratio = webview_height_ratio;
		this.webview_share_button = "hide";
		this.messenger_extensions = messengerExtensions;
		
		if (this.messenger_extensions) {
			this.url = url  + "&ext=1";
			this.fallback_url = url + "&ext=0";
		
		} else {
			this.url = url  + "&ext=0";
		}
	}
	
	public WebUrlButton(String title, String url, String webview_height_ratio) {
		this.type = TYPE_WEB_URL;
		this.title = title;
		this.url = url;
		this.webview_height_ratio = webview_height_ratio;
		this.webview_share_button = "hide";
	}
	
	public WebUrlButton(String title, String url, int messenger_extensions) {
		this.type = TYPE_WEB_URL;
		this.title = title;
		this.url = url;
		this.webview_height_ratio = WEBVIEW_HEIGHT_TALL;
		this.webview_share_button = "hide";
		this.messenger_extensions = messenger_extensions == 1;
	}

	public String getUrl() {
		return url;
	}

	public String getWebview_height_ratio() {
		return webview_height_ratio;
	}

	public String getWebview_share_button() {
		return webview_share_button;
	}

	public boolean isMessenger_extensions() {
		return messenger_extensions;
	}

	public String getFallback_url() {
		return fallback_url;
	}

}
