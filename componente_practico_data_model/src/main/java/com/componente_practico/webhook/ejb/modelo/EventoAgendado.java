package com.componente_practico.webhook.ejb.modelo;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.componente_practico.general.ejb.modelo.EntityGeneral;
import com.componente_practico.general.ejb.modelo.Usuario;

@Entity
@Table(name = "evento_agendado")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "eventoAgendado.porDiaUsuario", query = "select o from EventoAgendado o where o.usuario = :usuario and o.fecha between :fechaInicio and :fechaFin order by o.fecha"),
	@NamedQuery(name = "eventoAgendado.porNotificar", query = "select o from EventoAgendado o where o.notificado = false and o.fechaAviso < :fecha")
})

public class EventoAgendado extends EntityGeneral {

	private String descripcion;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;
	
	@Column(name = "fecha_aviso")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaAviso;
	
	private boolean notificado;
	private boolean leido;

	@ManyToOne(optional = false)
	private Usuario usuario;
	
	protected EventoAgendado() {}

	public EventoAgendado(String descripcion, Date fecha, Usuario usuario, int minutosAntes) {
		this.descripcion = descripcion;
		this.fecha = fecha;
		this.usuario = usuario;
		this.notificado = false;
		this.leido = false;
		
		Calendar dummyDate = Calendar.getInstance();
		dummyDate.setTime(fecha);
		dummyDate.add(Calendar.MINUTE, - minutosAntes);
		
		this.fechaAviso = dummyDate.getTime();
	}

	public String getDescripcion() {
		return descripcion;
	}

	public Date getFecha() {
		return fecha;
	}

	public boolean isNotificado() {
		return notificado;
	}

	public boolean isLeido() {
		return leido;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setNotificado(boolean notificado) {
		this.notificado = notificado;
	}

	public void setLeido(boolean leido) {
		this.leido = leido;
	}

}
