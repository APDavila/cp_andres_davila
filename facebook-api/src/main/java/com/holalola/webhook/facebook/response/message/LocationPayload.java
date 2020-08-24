package com.holalola.webhook.facebook.response.message;

import com.google.gson.annotations.SerializedName;
import com.holalola.webhook.facebook.payload.PayloadGeneral;

public class LocationPayload extends PayloadGeneral {

	@SerializedName("coordinates.lat")
	private long latitude;
	@SerializedName("coordinates.long")
	private long longitude;

	public long getLatitude() {
		return latitude;
	}

	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}

	public long getLongitude() {
		return longitude;
	}

	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}

}
