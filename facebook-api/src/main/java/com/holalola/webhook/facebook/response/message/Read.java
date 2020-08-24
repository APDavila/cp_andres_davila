package com.holalola.webhook.facebook.response.message;

public class Read {
	
	private long watermark;
	private long seq;
	
	public long getWatermark() {
		return watermark;
	}
	public void setWatermark(long watermark) {
		this.watermark = watermark;
	}
	public long getSeq() {
		return seq;
	}
	public void setSeq(long seq) {
		this.seq = seq;
	}
}
