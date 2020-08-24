package com.componente_practico.general.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "rol_menu")
@NamedQueries({
	@NamedQuery(name = "rolMenu.porRol", query = "select o from RolMenu o where o.rol = :rol and estaActivo = 1")
})
public class RolMenu extends EntityGeneral {
	
	@Column(name = "rol")
	private String rol;
	
	@Column(name = "menu")
	private String menu;
	
	@Column(name = "estaactivo")
	private Boolean estaActivo;
	
	public RolMenu() {
		super();
		this.rol = "";
		this.menu = "";
		this.estaActivo = true;
	}

	public RolMenu(String rol, String menu, Boolean estaActivo) {
		super();
		this.rol = rol;
		this.menu = menu;
		this.estaActivo = estaActivo;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	public Boolean getEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(Boolean estaActivo) {
		this.estaActivo = estaActivo;
	}
	
	 
}
