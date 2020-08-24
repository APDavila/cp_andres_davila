package com.componente_practico.webhook.model.yahoo.weather;

import java.util.List;

public class Query {

	Location location;
	CurrentObservation current_observation;	
	List<Forecasts> forecasts;
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public CurrentObservation getCurrent_observation() {
		return current_observation;
	}

	public void setCurrent_observation(CurrentObservation current_observation) {
		this.current_observation = current_observation;
	}

	public List<Forecasts> getForecasts() {
		return forecasts;
	}

	public void setForecasts(List<Forecasts> forecasts) {
		this.forecasts = forecasts;
	}
	

	public final class Forecasts	
	{
		String day;
		int date;
		int low;
		int high;
		String text;
		int code;
		
		public String getDay() {
			return day;
		}
		public void setDay(String day) {
			this.day = day;
		}
		public int getDate() {
			return date;
		}
		public void setDate(int date) {
			this.date = date;
		}
		public int getLow() {
			return low;
		}
		public void setLow(int low) {
			this.low = low;
		}
		public int getHigh() {
			return high;
		}
		public void setHigh(int high) {
			this.high = high;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
	}
	
	public  final class CurrentObservation	
	{
		Wind wind;
		Atmosphere atmosphere;
		Astronomy astronomy;
		Condition condition;
		int pubDate;
		
		
		public Wind getWind() {
			return wind;
		}

		public void setWind(Wind wind) {
			this.wind = wind;
		}

		public Atmosphere getAtmosphere() {
			return atmosphere;
		}

		public void setAtmosphere(Atmosphere atmosphere) {
			this.atmosphere = atmosphere;
		}

		public Astronomy getAstronomy() {
			return astronomy;
		}

		public void setAstronomy(Astronomy astronomy) {
			this.astronomy = astronomy;
		}

		public Condition getCondition() {
			return condition;
		}

		public void setCondition(Condition condition) {
			this.condition = condition;
		}
		
		
		
		public int getPubDate() {
			return pubDate;
		}

		public void setPubDate(int pubDate) {
			this.pubDate = pubDate;
		}



		public  final class Wind	
		{
			int chill;
			int direction;
			double speed;
			
			public int getChill() {
				return chill;
			}
			public void setChill(int chill) {
				this.chill = chill;
			}
			public int getDirection() {
				return direction;
			}
			public void setDirection(int direction) {
				this.direction = direction;
			}
			public double getSpeed() {
				return speed;
			}
			public void setSpeed(double speed) {
				this.speed = speed;
			}
			
		}

		public  final class Condition	
		{
			String text;
			int code;
			int temperature;
			
			public String getText() {
				return text;
			}
			public void setText(String text) {
				this.text = text;
			}
			public int getCode() {
				return code;
			}
			public void setCode(int code) {
				this.code = code;
			}
			public int getTemperature() {
				return temperature;
			}
			public void setTemperature(int temperature) {
				this.temperature = temperature;
			}
			
			
		}
		
		public final class Astronomy	
		{
			String sunrise;
			String sunset;
			public String getSunrise() {
				return sunrise;
			}
			public void setSunrise(String sunrise) {
				this.sunrise = sunrise;
			}
			public String getSunset() {
				return sunset;
			}
			public void setSunset(String sunset) {
				this.sunset = sunset;
			}
		}
		
		public final class Atmosphere	
		{
			int humidity;
			int visibility;
			double pressure;
			int rising;
			
			public int getHumidity() {
				return humidity;
			}
			public void setHumidity(int humidity) {
				this.humidity = humidity;
			}
			public int getVisibility() {
				return visibility;
			}
			public void setVisibility(int visibility) {
				this.visibility = visibility;
			}
			public double getPressure() {
				return pressure;
			}
			public void setPressure(double pressure) {
				this.pressure = pressure;
			}
			public int getRising() {
				return rising;
			}
			public void setRising(int rising) {
				this.rising = rising;
			}
			
		}
	}
	
	public final class Location
	{
		int woeid;
		String city;
		String region;
		String country;
		double lat;
		double longt;
		String timezone_id;
 
		
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		public double getLong() {
			return longt;
		}
		public void setLong(double longt) {
			this.longt = longt;
		}
		public String getTimezone_id() {
			return timezone_id;
		}
		public void setTimezone_id(String timezone_id) {
			this.timezone_id = timezone_id;
		}
		public int getWoeid() {
			return woeid;
		}
		public void setWoeid(int woeid) {
			this.woeid = woeid;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getCountry() {
			return country;
		}
		public void setCountry(String country) {
			this.country = country;
		}
		public String getRegion() {
			return region;
		}
		public void setRegion(String region) {
			this.region = region;
		}
		
		
		
	}
	
	// **********************************   ANTERIOR ******************************
	/*private long count;
	private String created;
	private String lang;
	private Diagnostics diagnostics;
	private Result results;

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public Diagnostics getDiagnostics() {
		return diagnostics;
	}

	public void setDiagnostics(Diagnostics diagnostics) {
		this.diagnostics = diagnostics;
	}

	public Result getResults() {
		return results;
	}

	public void setResults(Result results) {
		this.results = results;
	}
 */
}
