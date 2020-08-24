package com.holalola.webhook.facebook;

public abstract class AttachmentGeneral  {
	
	public static final String IMAGE = "image";
	public static final String AUDIO = "audio";
	public static final String VIDEO = "video";
	public static final String FILE = "file";
	public static final String TEMPLATE = "template";
	
	public static final String FALLBACK = "fallback";

	protected String type;
	
	public String getType() {
		return type;
	}

}
