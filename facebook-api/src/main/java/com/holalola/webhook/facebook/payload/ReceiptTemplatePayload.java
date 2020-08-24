package com.holalola.webhook.facebook.payload;

import java.util.List;

import com.holalola.webhook.facebook.templates.Address;
import com.holalola.webhook.facebook.templates.Adjustment;
import com.holalola.webhook.facebook.templates.ReceiptElement;
import com.holalola.webhook.facebook.templates.Summary;

public class ReceiptTemplatePayload extends PayloadGeneral {

	private final String template_type = "receipt";
	private final String currency = "USD";
	private String recipient_name;
	private String order_number;
	private String payment_method;
	private Summary summary;

	// Opcionales
	private String timestamp;
	private String merchant_name;
	private String order_url;
	private List<ReceiptElement> elements;
	private Address address;
	private List<Adjustment> adjustments;

	public ReceiptTemplatePayload(String recipient_name, String order_number, String payment_method, Summary summary) {
		this.recipient_name = recipient_name;
		this.order_number = order_number;
		this.payment_method = payment_method;
		this.summary = summary;
	}
	
	public ReceiptTemplatePayload(String recipient_name, String order_number, String payment_method, Summary summary,
			List<ReceiptElement> elements) {
		super();
		this.recipient_name = recipient_name;
		this.order_number = order_number;
		this.payment_method = payment_method;
		this.summary = summary;
		this.elements = elements;
	}



	public ReceiptTemplatePayload(String recipient_name, String order_number, String payment_method, Summary summary,
			String timestamp, String merchant_name, String order_url, List<ReceiptElement> elements, Address address,
			List<Adjustment> adjustments) {
		this.recipient_name = recipient_name;
		this.order_number = order_number;
		this.payment_method = payment_method;
		this.summary = summary;
		this.timestamp = timestamp;
		this.merchant_name = merchant_name;
		this.order_url = order_url;
		this.elements = elements;
		this.address = address;
		this.adjustments = adjustments;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setMerchant_name(String merchant_name) {
		this.merchant_name = merchant_name;
	}

	public void setOrder_url(String order_url) {
		this.order_url = order_url;
	}

	public void setElements(List<ReceiptElement> elements) {
		this.elements = elements;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setAdjustments(List<Adjustment> adjustments) {
		this.adjustments = adjustments;
	}

	public String getTemplate_type() {
		return template_type;
	}

	public String getCurrency() {
		return currency;
	}

	public String getRecipient_name() {
		return recipient_name;
	}

	public String getOrder_number() {
		return order_number;
	}

	public String getPayment_method() {
		return payment_method;
	}

	public Summary getSummary() {
		return summary;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getMerchant_name() {
		return merchant_name;
	}

	public String getOrder_url() {
		return order_url;
	}

	public List<ReceiptElement> getElements() {
		return elements;
	}

	public Address getAddress() {
		return address;
	}

	public List<Adjustment> getAdjustments() {
		return adjustments;
	}

}
