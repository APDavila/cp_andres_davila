package com.holalola.webhook.facebook.response.message;

import com.holalola.webhook.facebook.Recipient;

public class Messaging {

	private Recipient sender;
	private Recipient recipient;
	private String timestamp;
	private MessageIn message;
	private Read read;
	private Delivery delivery;
	private Postback postback;

	public Recipient getSender() {
		return sender;
	}

	public void setSender(Recipient sender) {
		this.sender = sender;
	}

	public Recipient getRecipient() {
		return recipient;
	}

	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public MessageIn getMessage() {
		return message;
	}

	public void setMessage(MessageIn message) {
		this.message = message;
	}

	public Read getRead() {
		return read;
	}

	public void setRead(Read read) {
		this.read = read;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Postback getPostback() {
		return postback;
	}

	public void setPostback(Postback postback) {
		this.postback = postback;
	}
}
