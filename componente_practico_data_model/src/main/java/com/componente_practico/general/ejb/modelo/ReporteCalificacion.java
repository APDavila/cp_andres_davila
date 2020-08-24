package com.componente_practico.general.ejb.modelo;

public class ReporteCalificacion {
	
	private String calificacion;
	private String nombreFacebook;
	private String apellidoFacebook;
	private String nombre;
	private String apellido;
	private int edad;
	private String ciudad;
	private String email;
	private String celular;
	private Float totalCompra;
	private Float promedioCompra;
	
	public ReporteCalificacion() {
		super();
		this.calificacion = "";
		this.nombreFacebook = "";
		this.apellidoFacebook = "";
		this.nombre = "";
		this.apellido = "";
		this.edad = 0;
		this.ciudad = "";
		this.email = "";
		this.celular = "";
		this.totalCompra = (float) 0;
		this.promedioCompra = (float) 0;
	}
	
	public ReporteCalificacion(String calificacion, String nombreFacebook, String apellidoFacebook, String nombre, String apellido, int edad,
			String ciudad, String email, String celular, Float totalCompra, Float promedioCompra) {
		super();
		this.calificacion = calificacion;
		this.nombreFacebook = nombreFacebook;
		this.apellidoFacebook = apellidoFacebook;
		this.nombre = nombre;
		this.apellido = apellido;
		this.edad = edad;
		this.ciudad = ciudad;
		this.email = email;
		this.celular = celular;
		this.totalCompra = totalCompra;
		this.promedioCompra = promedioCompra;
	}
	
	public String getNombreFacebook() {
		return nombreFacebook;
	}
	public void setNombreFacebook(String nombreFacebook) {
		this.nombreFacebook = nombreFacebook;
	}
	public String getApellidoFacebook() {
		return apellidoFacebook;
	}
	public void setApellidoFacebook(String apellidoFacebook) {
		this.apellidoFacebook = apellidoFacebook;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public int getEdad() {
		return edad;
	}
	public void setEdad(int edad) {
		this.edad = edad;
	}
	public String getCiudad() {
		return ciudad;
	}
	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public Float getTotalCompra() {
		return totalCompra;
	}
	public void setTotalCompra(Float totalCompra) {
		this.totalCompra = totalCompra;
	}
	public Float getPromedioCompra() {
		return promedioCompra;
	}
	public void setPromedioCompra(Float promedioCompra) {
		this.promedioCompra = promedioCompra;
	}
	public String getCalificacion() {
		return calificacion;
	}
	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}
	
}
