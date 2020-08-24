package com.holalola.ecommerce.client.vo;

public class GetByMailRequest {
	private String mail;

	public GetByMailRequest(String mail) {
		super();
		this.mail = mail;
	}

	protected GetByMailRequest() {
		super();
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}
	
}
