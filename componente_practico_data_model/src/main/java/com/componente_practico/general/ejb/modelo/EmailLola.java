package com.componente_practico.general.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "emailLola.todos", query = "select o from EmailLola o where o.estaActivo = 1")
})
public class EmailLola extends EntityGeneral {
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "estaactivo")
	private boolean estaActivo;	
	
	protected EmailLola() {}
	

	public EmailLola(String email, boolean estaActivo) {
		super();
		this.email = email;
		this.estaActivo = estaActivo;
	}



	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(boolean estaActivo) {
		this.estaActivo = estaActivo;
	}
	
	
}
