package com.componente_practico.general.ejb.modelo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "usuario_payphone")
public class UsuarioPayPhone extends EntityGeneral {

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	
	@JoinColumn(name = "tarjetaencriptada")
	private String tarjetaEncriptada;
	
	private String token;
	
	private String csv;
	
	@JoinColumn(name = "fechacreacion")
	private Date fechaCreacion;
	
	public UsuarioPayPhone() {
		super();
	}

	public UsuarioPayPhone(Usuario usuario, String tarjetaEncriptada, String token, String csv, Date fechaCreacion) {
		super();
		this.usuario = usuario;
		this.tarjetaEncriptada = tarjetaEncriptada;
		this.token = token;
		this.csv = csv;
		this.fechaCreacion = fechaCreacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public String getTarjetaEncriptada() {
		return tarjetaEncriptada;
	}

	public void setTarjetaEncriptada(String tarjetaEncriptada) {
		this.tarjetaEncriptada = tarjetaEncriptada;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCsv() {
		return csv;
	}

	public void setCsv(String csv) {
		this.csv = csv;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	

}
