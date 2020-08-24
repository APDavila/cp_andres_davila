package com.componente_practico.broadcast.entidad;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@Table(name = "usuario_opcbroadcast")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "usuarioOpcionesBroadcast.activos", query="select o from UsuarioOpcionesBroadcast o where o.estaActivo = 1 and o.opcionbroadcastId = :opcionbroadcast_id "),
	@NamedQuery(name = "usuarioOpcionesBroadcast.usuariosOpcionesBroadcast", query="select o from UsuarioOpcionesBroadcast o where o.opcionbroadcastId = :opcionbroadcast_id ")
})
public class UsuarioOpcionesBroadcast extends EntityGeneral {

 
	@Column(name = "opcionbroadcast_id")
	private Long opcionbroadcastId;
	
	
	@Column(name = "usuario_id")
	private Long usuario;
	
	@Column(name = "estaactivo")
	private boolean estaActivo;
	
	@Column(name = "fechacreacion")
	private Date fechaCreacion;

	public UsuarioOpcionesBroadcast() {
		super();
		this.opcionbroadcastId = (long) 0;
		this.estaActivo = false;
		this.fechaCreacion = new Date();
	}
	
	public UsuarioOpcionesBroadcast(long usuario, long opcionbroadcastId, boolean estaActivo, Date fechaCreacion) {
		super();
		this.usuario = usuario;
		this.opcionbroadcastId = opcionbroadcastId;
		this.estaActivo = estaActivo;
		this.fechaCreacion = fechaCreacion;
	}

	public Long getOpcionbroadcastId() {
		return opcionbroadcastId;
	}

	public void setOpcionbroadcastId(Long opcionbroadcastId) {
		this.opcionbroadcastId = opcionbroadcastId;
	}

	public boolean isEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(boolean estaActivo) {
		this.estaActivo = estaActivo;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public long getUsuario() {
		return usuario;
	}

	public void setUsuario(long usuario) {
		this.usuario = usuario;
	}	
	
	
}
