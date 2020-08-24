package com.holalola.webhook.facebook.templates;

public class Address {

	private String street_1;
	private String street_2; // Opcional
	private String city;
	private String postal_code;
	private String state; // State abbreviation or Region/Province
							// (international)
	private String country; // Two-letter country abbreviation

	public Address(String street_1, String street_2, String city, String postal_code, String state, String country) {
		this.street_1 = street_1;
		this.street_2 = street_2;
		this.city = city;
		this.postal_code = postal_code;
		this.state = state;
		this.country = country;
	}

	public String getStreet_1() {
		return street_1;
	}

	public String getStreet_2() {
		return street_2;
	}

	public String getCity() {
		return city;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public String getState() {
		return state;
	}

	public String getCountry() {
		return country;
	}

}
