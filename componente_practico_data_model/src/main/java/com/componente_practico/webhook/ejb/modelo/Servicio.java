package com.componente_practico.webhook.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.componente_practico.general.ejb.modelo.CatalogoGeneralFB;

@Entity
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "servicio.activos", query = "select o from Servicio o where o.activo = true order by o.orden"),
	@NamedQuery(name = "servicio.visibles", query = "select o from Servicio o where o.visible = true"),
	@NamedQuery(name = "servicio.porPayload", query = "select o from Servicio o where o.payload like :as_payload")
})

public class Servicio extends CatalogoGeneralFB {
	
	private int orden;
	
	protected Servicio() {}
	
	public Servicio(long id) {
		this.id = id;
	}
	
	public static Servicio getInstanciaVacia() {
		return new Servicio();
	}

	@Column(name = "payload")
	private String payload;

	public int getOrden() {
		return orden;
	}

	@Column(name = "visible")
	private boolean visible;

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setOrden(int orden) {
		this.orden = orden;
	}

	public Servicio(int orden, String payload, boolean visible) {
		super();
		this.orden = orden;
		this.payload = payload;
		this.visible = visible;
	}



	
}
