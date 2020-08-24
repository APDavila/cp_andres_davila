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
@Table(name = "opcion_broadcast")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "opcionesBroadcast.opcionesPorId", query="select o from OpcionesBroadcast o where o.id = :id "),
	@NamedQuery(name = "opcionesBroadcast.buscar", query="select o from OpcionesBroadcast o where o.estaActivo = :estaActivo and o.mensajesBroadcast = :mensajesBroadcast order by opcion"),
	@NamedQuery(name = "opcionesBroadcast.todasOpciones", query="select o from OpcionesBroadcast o where o.mensajesBroadcast = :mensajesBroadcast order by opcion"),
	@NamedQuery(name = "opcionesBroadcast.todasOpcionesProcesadas", query="select o from OpcionesBroadcast o where o.mensajesBroadcast.enviado = 1 and o.mensajesBroadcast.activoParaFiltro = 1 order by o.mensajesBroadcast.alias, opcion")
})
public class OpcionesBroadcast extends EntityGeneral {

	@ManyToOne
	@JoinColumn(name = "mens_broadcast_id")
	private MensajesBroadcast mensajesBroadcast;
	
	private String opcion;
	
	@Column(name = "estaactivo")
	private boolean estaActivo;
	
	@Column(name = "fechacreacion")
	private Date fechaCreacion;
	
	public OpcionesBroadcast() {
		super();
		this.mensajesBroadcast = null;
		this.opcion = "";
		this.estaActivo = true;
		this.fechaCreacion = new Date();
	}

	public OpcionesBroadcast(MensajesBroadcast mensajesBroadcast, String opcion, boolean estaActivo,
			Date fechaCreacion) {
		super();
		this.mensajesBroadcast = mensajesBroadcast;
		this.opcion = opcion;
		this.estaActivo = estaActivo;
		this.fechaCreacion = fechaCreacion;
	}

	public MensajesBroadcast getMensajesBroadcast() {
		return mensajesBroadcast;
	}

	public void setMensajesBroadcast(MensajesBroadcast mensajesBroadcast) {
		this.mensajesBroadcast = mensajesBroadcast;
	}

	public String getOpcion() {
		return opcion;
	}

	public void setOpcion(String opcion) {
		this.opcion = opcion;
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
