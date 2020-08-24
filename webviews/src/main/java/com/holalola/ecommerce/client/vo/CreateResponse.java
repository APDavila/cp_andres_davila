package com.holalola.ecommerce.client.vo;

public class CreateResponse {
	private String cardToken;
	private String authorizationCode;
	private String message;
	private int messageCode;
	private String status;
	private int statusCode;
	private String transactionId;
	private String clientTransactionId;
	protected CreateResponse() {
		super();
	}
	protected CreateResponse(String cardToken, String authorizationCode, String message, int messageCode, String status,
			int statusCode, String transactionId, String clientTransactionId) {
		super();
		this.cardToken = cardToken;
		this.authorizationCode = authorizationCode;
		this.message = message;
		this.messageCode = messageCode;
		this.status = status;
		this.statusCode = statusCode;
		this.transactionId = transactionId;
		this.clientTransactionId = clientTransactionId;
	}
	public String getCardToken() {
		return cardToken;
	}
	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}
	public String getAuthorizationCode() {
		return authorizationCode;
	}
	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public int getMessageCode() {
		return messageCode;
	}
	public void setMessageCode(int messageCode) {
		this.messageCode = messageCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	public String getClientTransactionId() {
		return clientTransactionId;
	}
	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}
	
	

}
