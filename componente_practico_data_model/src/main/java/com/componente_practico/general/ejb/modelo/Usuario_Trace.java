package com.componente_practico.general.ejb.modelo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@SuppressWarnings("serial")
@Table(name = "usuario_trace")
public class Usuario_Trace extends EntityGeneral {

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	
	Date fecha;
	String accion;
	String result;
	
	public Usuario_Trace() {
		super();
		this.usuario = null;
		this.fecha = new Date();
		this.accion = "";
		this.result = "";
	}
	
	public Usuario_Trace(Usuario usuario, Date fecha, String accion, String result) {
		super();
		this.usuario = usuario;
		this.fecha = fecha;
		accion = accion ==null ? "" : accion;
		this.accion = accion;
		result = result ==null ? "" : result;
		if(result.trim().length() > 1000)
			result = result.trim().substring(1, 999);
		
		this.result = result;
	}
	
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		accion = accion ==null ? "" : accion;
		this.accion = accion;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		result = result ==null ? "" : result;
		if(result.trim().length() > 1000)
			result = result.trim().substring(1, 999);
		this.result = result;
	}
	
	
	
}
