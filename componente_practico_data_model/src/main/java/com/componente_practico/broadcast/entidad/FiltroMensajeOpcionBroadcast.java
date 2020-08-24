package com.componente_practico.broadcast.entidad;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@Table(name = "filtro_mens_opc_broadcast")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "filtroMensajeOpcionBroadcast.activos", query="select o from FiltroMensajeOpcionBroadcast o where o.estaActivo = 1 and o.mensajesBroadcast = :mensajesBroadcast "),
	@NamedQuery(name = "filtroMensajeOpcionBroadcast.todos", query="select o from FiltroMensajeOpcionBroadcast o where o.mensajesBroadcast = :mensajesBroadcast ")
})
public class FiltroMensajeOpcionBroadcast extends EntityGeneral {

	@ManyToOne
	@JoinColumn(name = "mens_broadcast_id")
	private MensajesBroadcast mensajesBroadcast;
	
	@ManyToOne
	@JoinColumn(name = "opcion_broadcast_id")
	private OpcionesBroadcast opcionesBroadcast;
	
	@Column(name = "estaactivo")
	private boolean estaActivo;
	
	@Column(name = "fechacreacion")
	private Date fechaCreacion;
	
	public FiltroMensajeOpcionBroadcast() {
		super();
		this.mensajesBroadcast = null;
		this.opcionesBroadcast = null;
		this.estaActivo = true;
		this.fechaCreacion = new Date();
	}

	public FiltroMensajeOpcionBroadcast(MensajesBroadcast mensajesBroadcast, OpcionesBroadcast opcionesBroadcast,
			boolean estaActivo, Date fechaCreacion) {
		super();
		this.mensajesBroadcast = mensajesBroadcast;
		this.opcionesBroadcast = opcionesBroadcast;
		this.estaActivo = estaActivo;
		this.fechaCreacion = fechaCreacion;
	}

	public MensajesBroadcast getMensajesBroadcast() {
		return mensajesBroadcast;
	}

	public void setMensajesBroadcast(MensajesBroadcast mensajesBroadcast) {
		this.mensajesBroadcast = mensajesBroadcast;
	}

	public OpcionesBroadcast getOpcionesBroadcast() {
		return opcionesBroadcast;
	}

	public void setOpcionesBroadcast(OpcionesBroadcast opcionesBroadcast) {
		this.opcionesBroadcast = opcionesBroadcast;
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
	
}
