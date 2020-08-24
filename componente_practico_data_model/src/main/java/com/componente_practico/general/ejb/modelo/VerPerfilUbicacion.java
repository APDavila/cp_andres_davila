package com.componente_practico.general.ejb.modelo;

public class VerPerfilUbicacion {
	
	private String alias;
	private String esPrincipal;
	public VerPerfilUbicacion(String alias, String esPrincipal) {
		super();
		this.alias = alias;
		this.esPrincipal = esPrincipal;
	}
	public VerPerfilUbicacion() {
		super();
	}
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public String getEsPrincipal() {
		return esPrincipal;
	}
	public void setEsPrincipal(String esPrincipal) {
		this.esPrincipal = esPrincipal;
	}
	
	

}
