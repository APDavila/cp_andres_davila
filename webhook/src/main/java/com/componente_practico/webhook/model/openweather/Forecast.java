package com.componente_practico.webhook.model.openweather;

import java.util.List;

public class Forecast {

	private long dt;
	private MainWeatherData main;
	private List<WeatherDescription> weather;
	private CloudInfo clouds;
	private WindInfo wind;
	private VolumenInfo rain;
	private VolumenInfo snow;
	private Sys sys;
	private String dt_txt;

	public long getDt() {
		return dt;
	}

	public void setDt(long dt) {
		this.dt = dt;
	}

	public MainWeatherData getMain() {
		return main;
	}

	public void setMain(MainWeatherData main) {
		this.main = main;
	}

	public List<WeatherDescription> getWeather() {
		return weather;
	}

	public void setWeather(List<WeatherDescription> weather) {
		this.weather = weather;
	}

	public CloudInfo getClouds() {
		return clouds;
	}

	public void setClouds(CloudInfo clouds) {
		this.clouds = clouds;
	}

	public WindInfo getWind() {
		return wind;
	}

	public void setWind(WindInfo wind) {
		this.wind = wind;
	}

	public VolumenInfo getRain() {
		return rain;
	}

	public void setRain(VolumenInfo rain) {
		this.rain = rain;
	}

	public VolumenInfo getSnow() {
		return snow;
	}

	public void setSnow(VolumenInfo snow) {
		this.snow = snow;
	}

	public Sys getSys() {
		return sys;
	}

	public void setSys(Sys sys) {
		this.sys = sys;
	}

	public String getDt_txt() {
		return dt_txt;
	}

	public void setDt_txt(String dt_txt) {
		this.dt_txt = dt_txt;
	}

}
