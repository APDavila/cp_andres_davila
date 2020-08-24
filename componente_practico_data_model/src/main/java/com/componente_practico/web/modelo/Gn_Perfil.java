package com.componente_practico.web.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@Table(name = "gn_perfil")

@NamedQueries({
	@NamedQuery(name = "gn_perfil.listaDatos", query = "select o from Gn_Perfil o where o.nombre like :nombre and o.estaActivo = :estaActivo")
})

@SuppressWarnings("serial")
public class Gn_Perfil extends EntityGeneral {

	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "estaactivo")
	private boolean estaActivo;
	
	public Gn_Perfil() {
		super();
		this.nombre = "";
		this.estaActivo = false;
	}
	
	public Gn_Perfil(String nombre, boolean estaActivo) {
		super();
		this.nombre = nombre;
		this.estaActivo = estaActivo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public boolean isEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(boolean estaActivo) {
		this.estaActivo = estaActivo;
	}
	
}
