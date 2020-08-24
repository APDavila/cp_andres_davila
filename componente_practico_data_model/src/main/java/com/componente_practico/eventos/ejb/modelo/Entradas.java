package com.componente_practico.eventos.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.joda.time.DateTime;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@SuppressWarnings("serial")
@Table(name = "ep_entradas")
public class Entradas extends EntityGeneral {
	
	@Column(name = "usuario_crea_id")
	protected Long idUsuarioCrea;
	
	@Column(name = "usuario_resp_id")
	protected Long idUsuarioResponsableEntrada;
	
	@Column(name = "usuario_compra_id")
	protected Long idUsuarioAsignaEntrada;
	
	@Column(name = "usu_cliente_comp_id")
	protected Long idClienteAsignaEntrada;
	
	@Column(name = "usu_cliente_resp_id")
	protected Long idClienterResponsableEntrada;
	
	@Column(name = "fecha")
	protected Date fecha;
	
	@Column(name = "asiento")
	protected int asiento;
	
	@Column(name = "fila")
	protected int fila;
	
	@Column(name = "evento")
	protected String evento;
	
	@Column(name = "seccion")
	protected String seccion;
	
	@Column(name = "reservado")
	protected Boolean reservado;
	
	@Column(name = "activo")
	protected Boolean activo;
	
	@Column(name = "ingreso")
	protected Boolean ingreso;
	
	@Column(name = "fechasolicitud")
	protected Date fechaSolicitud;
	
	
	public Entradas() {
		super();
		this.idUsuarioCrea = (long) 0;
		this.idUsuarioResponsableEntrada = (long) 0;
		this.idUsuarioAsignaEntrada = (long) 0;
		this.idClienteAsignaEntrada = (long) 0;
		this.idClienterResponsableEntrada = (long) 0;
		this.fecha = new Date();
		this.asiento = -1;
		this.fila = -1;
		this.evento = "";
		this.seccion = "";
		this.reservado = false;
		this.activo = false;
		this.ingreso = false;
		this.fechaSolicitud  = new Date();
	}
	
	
	public Entradas(Long idUsuarioCrea, Long idUsuarioResponsableEntrada, Long idUsuarioAsignaEntrada,
			Long idClienterResponsableEntrada, Long idClienteAsignaEntrada, Date fecha, int asiento, int fila, String evento, String seccion,
			Boolean reservado, Boolean activo, Boolean ingreso, Date fechaSolicitud) {
		super();
		this.idUsuarioCrea = idUsuarioCrea;
		this.idUsuarioResponsableEntrada = idUsuarioResponsableEntrada;
		this.idUsuarioAsignaEntrada = idUsuarioAsignaEntrada;
		this.idClienterResponsableEntrada  = idClienterResponsableEntrada;
		this.idClienteAsignaEntrada = idClienteAsignaEntrada;
		this.fecha = fecha;
		this.asiento = asiento;
		this.fila = fila;
		this.evento = evento;
		this.seccion = seccion;
		this.reservado = reservado;
		this.activo = activo;
		this.ingreso = ingreso;
		this.fechaSolicitud = fechaSolicitud;
	}
	
	public Long getIdUsuarioCrea() {
		return idUsuarioCrea;
	}
	public void setIdUsuarioCrea(Long idUsuarioCrea) {
		this.idUsuarioCrea = idUsuarioCrea;
	}
	public Long getIdUsuarioResponsableEntrada() {
		return idUsuarioResponsableEntrada;
	}
	public void setIdUsuarioResponsableEntrada(Long idUsuarioResponsableEntrada) {
		this.idUsuarioResponsableEntrada = idUsuarioResponsableEntrada;
	}
	public Long getIdUsuarioAsignaEntrada() {
		return idUsuarioAsignaEntrada;
	}
	public void setIdUsuarioAsignaEntrada(Long idUsuarioAsignaEntrada) {
		this.idUsuarioAsignaEntrada = idUsuarioAsignaEntrada;
	}
	public Long getIdClienteAsignaEntrada() {
		return idClienteAsignaEntrada;
	}
	public void setIdClienteAsignaEntrada(Long idClienteAsignaEntrada) {
		this.idClienteAsignaEntrada = idClienteAsignaEntrada;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public int getAsiento() {
		return asiento;
	}
	public void setAsiento(int asiento) {
		this.asiento = asiento;
	}
	public int getFila() {
		return fila;
	}
	public void setFila(int fila) {
		this.fila = fila;
	}
	public String getEvento() {
		return evento;
	}
	public void setEvento(String evento) {
		this.evento = evento;
	}
	public String getSeccion() {
		return seccion;
	}
	public void setSeccion(String seccion) {
		this.seccion = seccion;
	}

	public Boolean getReservado() {
		return reservado;
	}

	public void setReservado(Boolean reservado) {
		this.reservado = reservado;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}


	public Long getIdClienterResponsableEntrada() {
		return idClienterResponsableEntrada;
	}


	public void setIdClienterResponsableEntrada(Long idClienterResponsableEntrada) {
		this.idClienterResponsableEntrada = idClienterResponsableEntrada;
	}


	public Boolean getIngreso() {
		return ingreso;
	}


	public void setIngreso(Boolean ingreso) {
		this.ingreso = ingreso;
	}


	public Date getFechaSolicitud() {
		return fechaSolicitud;
	}


	public void setFechaSolicitud(Date fechaSolicitud) {
		this.fechaSolicitud = fechaSolicitud;
	}
	
	

}
