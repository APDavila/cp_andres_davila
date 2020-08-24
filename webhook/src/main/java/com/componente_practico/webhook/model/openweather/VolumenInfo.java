package com.componente_practico.webhook.model.openweather;

import com.google.gson.annotations.SerializedName;

public class VolumenInfo {

	@SerializedName("3h")
	private double threeHour;

	public double getThreeHour() {
		return threeHour;
	}

	public void setThreeHour(double threeHour) {
		this.threeHour = threeHour;
	}

}
