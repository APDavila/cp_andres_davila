package com.componente_practico.general.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "usu_cliente_comp")
public class UsuarioClienteCompra extends EntityGeneral {
	
	@Column(name = "tipo_identificacion")
	String tipoIdentificacion;
	
	@Column(name = "identificacion")
	String identificacion;
	
	@Column(name = "nombre")
	String nombre;
	
	@Column(name = "apellido")
	String apellido;
	
	@Column(name = "email")
	String email;
	
	@Column(name = "direccion")
	String direccion;
	
	@Column(name = "celular")
	String celular;
	
	
	public UsuarioClienteCompra() {
		super();
		this.tipoIdentificacion = "";
		this.identificacion = "";
		this.nombre = "";
		this.apellido = "";
		this.email = "";
		this.direccion = "";
		this.celular = "";
	}
	

	public UsuarioClienteCompra(String tipoIdentificacion, String identificacion, String nombre, String apellido,
			String email, String direccion, String celular) {
		super();
		this.tipoIdentificacion = tipoIdentificacion;
		this.identificacion = identificacion;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.direccion = direccion;
		this.celular = celular;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}
	
	@Override
	public String toString() {
		if(this == null)
			return "";
		else
		{
			return (this.apellido != null && this.apellido.trim().length() > 0 ? this.apellido.trim() : "") + " " + (this.nombre != null && this.nombre.trim().length() > 0 ? this.nombre.trim() : "");  
		}
	}

}
