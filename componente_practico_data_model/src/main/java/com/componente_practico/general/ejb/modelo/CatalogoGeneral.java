package com.componente_practico.general.ejb.modelo;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
@SuppressWarnings("serial")
public class CatalogoGeneral extends EntityGeneral {

	protected String nombre;
	protected boolean activo;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}
}
