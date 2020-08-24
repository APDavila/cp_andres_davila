package com.holalola.webhook.facebook.response;

public class UserLocation {

	private Location location;
	private String id;
	
	
	public UserLocation() {
		super();
	}
	protected UserLocation(Location location, String id) {
		super();
		this.location = location;
		this.id = id;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public class Location{
		private String city;
		private String country;
		private Double latitude;
		private String located_in;
		private Double longitude;
		private String state;
		private String street;
		private String zip;
		protected Location() {
			super();
		}
		
		public Double getLongitude() {
			return longitude;
		}

		public void setLongitude(Double longitude) {
			this.longitude = longitude;
		}

		protected Location(String city, String country, Double latitude, String located_in, Double longitude,
				String state, String street, String zip) {
			super();
			this.city = city;
			this.country = country;
			this.latitude = latitude;
			this.located_in = located_in;
			this.longitude = longitude;
			this.state = state;
			this.street = street;
			this.zip = zip;
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
		public Double getLatitude() {
			return latitude;
		}
		public void setLatitude(Double latitude) {
			this.latitude = latitude;
		}
		public String getLocated_in() {
			return located_in;
		}
		public void setLocated_in(String located_in) {
			this.located_in = located_in;
		}
		
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getStreet() {
			return street;
		}
		public void setStreet(String street) {
			this.street = street;
		}
		public String getZip() {
			return zip;
		}
		public void setZip(String zip) {
			this.zip = zip;
		}
		
		
		
	}
	
}
