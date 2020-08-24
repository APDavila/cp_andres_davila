package com.holalola.ecommerce.client.vo;

public class CreateRequest {

	private String data;
	private String phoneNumber;
	private String email;
	private String optionalParameter;
	private String documentId;
	private int amount;
	private int amountWithTax;
	private int amountWithoutTax;	
	private int tax;
	private int service;
	private int tip;
	private String clientTransactionId;
	private String storeId;
	private String terminalId;
	private String currency;
	private String deferredType;
	private String resposeUrl;
	protected CreateRequest() {
		super();
	}
	protected CreateRequest(String data, String phoneNumber, String email, String optionalParameter, String documentId,
			int amount, int amountWithTax, int amountWithoutTax, int tax, int service, int tip,
			String clientTransactionId, String storeId, String terminalId, String currency, String deferredType,
			String resposeUrl) {
		super();
		this.data = data;
		this.phoneNumber = phoneNumber;
		this.email = email;
		this.optionalParameter = optionalParameter;
		this.documentId = documentId;
		this.amount = amount;
		this.amountWithTax = amountWithTax;
		this.amountWithoutTax = amountWithoutTax;
		this.tax = tax;
		this.service = service;
		this.tip = tip;
		this.clientTransactionId = clientTransactionId;
		this.storeId = storeId;
		this.terminalId = terminalId;
		this.currency = currency;
		this.deferredType = deferredType;
		this.resposeUrl = resposeUrl;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getOptionalParameter() {
		return optionalParameter;
	}
	public void setOptionalParameter(String optionalParameter) {
		this.optionalParameter = optionalParameter;
	}
	public String getDocumentId() {
		return documentId;
	}
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getAmountWithTax() {
		return amountWithTax;
	}
	public void setAmountWithTax(int amountWithTax) {
		this.amountWithTax = amountWithTax;
	}
	public int getAmountWithoutTax() {
		return amountWithoutTax;
	}
	public void setAmountWithoutTax(int amountWithoutTax) {
		this.amountWithoutTax = amountWithoutTax;
	}
	public int getTax() {
		return tax;
	}
	public void setTax(int tax) {
		this.tax = tax;
	}
	public int getService() {
		return service;
	}
	public void setService(int service) {
		this.service = service;
	}
	public int getTip() {
		return tip;
	}
	public void setTip(int tip) {
		this.tip = tip;
	}
	public String getClientTransactionId() {
		return clientTransactionId;
	}
	public void setClientTransactionId(String clientTransactionId) {
		this.clientTransactionId = clientTransactionId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getDeferredType() {
		return deferredType;
	}
	public void setDeferredType(String deferredType) {
		this.deferredType = deferredType;
	}
	public String getResposeUrl() {
		return resposeUrl;
	}
	public void setResposeUrl(String resposeUrl) {
		this.resposeUrl = resposeUrl;
	}
	
	

}
