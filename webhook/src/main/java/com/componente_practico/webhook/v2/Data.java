package com.componente_practico.webhook.v2;

import ai.api.model.Message;
import ai.api.model.OriginalRequestPostback;
import ai.api.model.Recipient;

public class Data {
	private Recipient sender;
	private Recipient recipient;
	private Message message;
	private OriginalRequestPostback postback;
	public Data() {
		super();
	}
	
	public Data(Recipient sender) {
		super();
		this.sender = sender;
	}

	public Data(Recipient sender, Recipient recipient, Message message, OriginalRequestPostback postback) {
		super();
		this.sender = sender;
		this.recipient = recipient;
		this.message = message;
		this.postback = postback;
	}

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
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public OriginalRequestPostback getPostback() {
		return postback;
	}
	public void setPostback(OriginalRequestPostback postback) {
		this.postback = postback;
	}
	
	
}
