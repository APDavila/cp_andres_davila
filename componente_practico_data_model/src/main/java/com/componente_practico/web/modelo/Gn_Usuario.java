package com.componente_practico.web.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@Table(name = "gn_usuario")

@NamedQueries({
	@NamedQuery(name = "ntUsuario.autentica", query = "select o from Gn_Usuario o where o.login = :login and clave = :clave")
})

@SuppressWarnings("serial")
public class Gn_Usuario  extends EntityGeneral {

	@Column(name = "nombre")
	private String nombre;
	
	@Column(name = "apellido")
	private String apellido;
	
	@Column(name = "login")
	private String login;
	
	@Column(name = "clave")
	private String clave;
	
	@Column(name = "esta_activo")
	private Boolean estaActivo;
	
	@Column(name = "fecha_creacion")
	private Date fechaCreacion;
	
	@Column(name = "fecha_actualizacion")
	private Date fechaActualizacion;
	
	public Gn_Usuario( ) {
		super();
		 
	}

	

	public Gn_Usuario(String nombre, String apellido, String login, String clave, Boolean estaActivo,
			Date fechaCreacion, Date fechaActualizacion) {
		super();
		this.nombre = nombre;
		this.apellido = apellido;
		this.login = login;
		this.clave = clave;
		this.estaActivo = estaActivo;
		this.fechaCreacion = fechaCreacion;
		this.fechaActualizacion = fechaActualizacion;
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

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Boolean getEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(Boolean estaActivo) {
		this.estaActivo = estaActivo;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Date getFechaActualizacion() {
		return fechaActualizacion;
	}

	public void setFechaActualizacion(Date fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}
	
	
	
}
