package com.holalola.webhook.facebook.response.message;

import java.util.List;

public class Delivery {

	private List<String> mids;
	private long watermark;
	private long seq;

	public List<String> getMids() {
		return mids;
	}

	public void setMids(List<String> mids) {
		this.mids = mids;
	}

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
