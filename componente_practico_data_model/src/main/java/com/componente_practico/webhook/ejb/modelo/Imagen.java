package com.componente_practico.webhook.ejb.modelo;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.componente_practico.general.ejb.modelo.EntityGeneral;
import com.componente_practico.webhook.enumeracion.Categoria;

@Entity
@SuppressWarnings("serial")

@NamedQueries({ @NamedQuery(name = "imagen.activosCategoria", query = "select o from Imagen o where o.activo = true and o.categoria = :categoria") })

public class Imagen extends EntityGeneral {

	@Enumerated(EnumType.STRING)
	private Categoria categoria;
	private String url;
	private boolean activo;

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

}
