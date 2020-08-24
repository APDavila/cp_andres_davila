package com.componente_practico.webhook.model.openweather;

public class MainWeatherData {

	private double temp;
	private double temp_min;
	private double temp_max;
	private double pressure;
	private double sea_level;
	private double grnd_level;
	private double humidity;
	private double temp_kf;

	public int getTemp() {
		return (int)temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public int getTemp_min() {
		return (int)temp_min;
	}

	public void setTemp_min(double temp_min) {
		this.temp_min = temp_min;
	}

	public int getTemp_max() {
		return (int)temp_max;
	}

	public void setTemp_max(double temp_max) {
		this.temp_max = temp_max;
	}

	public double getPressure() {
		return pressure;
	}

	public void setPressure(double pressure) {
		this.pressure = pressure;
	}

	public double getSea_level() {
		return sea_level;
	}

	public void setSea_level(double sea_level) {
		this.sea_level = sea_level;
	}

	public double getGrnd_level() {
		return grnd_level;
	}

	public void setGrnd_level(double grnd_level) {
		this.grnd_level = grnd_level;
	}

	public double getHumidity() {
		return humidity;
	}

	public void setHumidity(double humidity) {
		this.humidity = humidity;
	}

	public double getTemp_kf() {
		return temp_kf;
	}

	public void setTemp_kf(double temp_kf) {
		this.temp_kf = temp_kf;
	}

}
