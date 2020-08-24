package com.componente_practico.general.ejb.modelo;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings("serial")
public class CatalogoGeneralFB extends CatalogoGeneral {

	private String payload;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}
}
