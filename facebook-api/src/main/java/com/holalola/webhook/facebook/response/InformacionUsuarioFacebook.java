package com.holalola.webhook.facebook.response;

public class InformacionUsuarioFacebook {

	private String first_name;
	private String last_name;
	private String profile_pic;
	private String locale;
	private int timezone;
	private String gender;
	private String is_payment_enabled;
	private String birthday;
	private String email;

	@Override
	public String toString() {
		return "InformacionUsuarioFacebook [first_name=" + first_name + ", last_name=" + last_name + ", profile_pic="
				+ profile_pic + ", locale=" + locale + ", timezone=" + timezone + ", gender=" + gender
				+ ", is_payment_enabled=" + is_payment_enabled + ", birthday="+birthday+"]";
	}

	public String getFirst_name() {
		return first_name == null ? "" : first_name.trim();
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name == null ? "" : last_name.trim();
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getProfile_pic() {
		return profile_pic;
	}

	public void setProfile_pic(String profile_pic) {
		this.profile_pic = profile_pic;
	}


	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getIs_payment_enabled() {
		return is_payment_enabled;
	}

	public void setIs_payment_enabled(String is_payment_enabled) {
		this.is_payment_enabled = is_payment_enabled;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	
}
