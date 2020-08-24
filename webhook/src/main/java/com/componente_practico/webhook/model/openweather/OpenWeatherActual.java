package com.componente_practico.webhook.model.openweather;

import java.util.List;

public class OpenWeatherActual {

	private long id;
	private String name;
	private int cod;
	private long dt;
	private Coordinate coord;
	private List<WeatherDescription> weather;
	private String base;
	private MainWeatherData main;
	private CloudInfo clouds;
	private WindInfo wind;
	private VolumenInfo rain;
	private VolumenInfo snow;
	private Sys sys;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCod() {
		return cod;
	}

	public void setCod(int cod) {
		this.cod = cod;
	}

	public long getDt() {
		return dt;
	}

	public void setDt(long dt) {
		this.dt = dt;
	}

	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(Coordinate coord) {
		this.coord = coord;
	}

	public List<WeatherDescription> getWeather() {
		return weather;
	}

	public void setWeather(List<WeatherDescription> weather) {
		this.weather = weather;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public MainWeatherData getMain() {
		return main;
	}

	public void setMain(MainWeatherData main) {
		this.main = main;
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
}
