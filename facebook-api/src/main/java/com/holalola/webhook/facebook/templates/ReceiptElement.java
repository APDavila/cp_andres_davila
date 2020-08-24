package com.holalola.webhook.facebook.templates;

public class ReceiptElement {

	private String title;
	private double price;
	// Opcionales
	private String subtitle;
	private Integer quantity;
	private String currency;
	private String image_url;
	
	public ReceiptElement(String title, double price, Integer quantity) {
		super();
		this.title = title;
		this.price = price;
		this.quantity = quantity;
	}

	public ReceiptElement(String title, double price, String subtitle, Integer quantity, String currency,
			String image_url) {
		this.title = title;
		this.price = price;
		this.subtitle = subtitle;
		this.quantity = quantity;
		this.currency = currency;
		this.image_url = image_url;
	}

	public String getTitle() {
		return title;
	}

	public double getPrice() {
		return price;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public String getCurrency() {
		return currency;
	}

	public String getImage_url() {
		return image_url;
	}

}
