package com.holalola.webhook.facebook.templates;

public class Summary {

	private double total_cost;
	// Opcionales
	private Double subtotal;
	private Double shipping_cost;
	private Double total_tax;
	
	public Summary(double total_cost, Double subtotal, Double shipping_cost) {
		super();
		this.total_cost = total_cost;
		this.subtotal = subtotal;
		this.shipping_cost = shipping_cost;
	}

	public Summary(double total_cost, Double subtotal, Double shipping_cost, Double total_tax) {
		super();
		this.total_cost = total_cost;
		this.subtotal = subtotal;
		this.shipping_cost = shipping_cost;
		this.total_tax = total_tax;
	}

	public double getTotal_cost() {
		return total_cost;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public Double getShipping_cost() {
		return shipping_cost;
	}

	public Double getTotal_tax() {
		return total_tax;
	}

}
