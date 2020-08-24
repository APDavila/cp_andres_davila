package com.holalola.webhook.facebook.templates;

public class Adjustment {

	private String name;
	private Double amount;

	public Adjustment(String name, Double amount) {
		this.name = name;
		this.amount = amount;
	}

	public String getName() {
		return name;
	}

	public Double getAmount() {
		return amount;
	}

}
