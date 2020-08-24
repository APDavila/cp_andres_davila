package com.componente_practico.general.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "registro_error")
@SuppressWarnings("serial")
public class RegistroError extends EntityGeneral {

	private String query;
	
	@Column(name = "request_body")
	private String requestBody;
	
	protected RegistroError() {}

	public RegistroError(String query, String requestBody) {
		this.query = query;
		this.requestBody = requestBody;
	}

	public String getQuery() {
		return query;
	}

	public String getRequestBody() {
		return requestBody;
	}
	
	
}
