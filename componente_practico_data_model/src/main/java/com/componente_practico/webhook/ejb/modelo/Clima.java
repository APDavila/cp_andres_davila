package com.componente_practico.webhook.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@SuppressWarnings("serial")

@NamedQueries({
		@NamedQuery(name = "clima.ultimaPorCiudadTipo", query = "select o from Clima o where o.ciudad = :ciudad and o.tipo = :tipo and DATE(o.horaFin) >= DATE(CURRENT_TIMESTAMP) order by o.id desc") })

public class Clima extends EntityGeneral {

	private String ciudad;
	private String tipo;

	@Temporal(TemporalType.TIMESTAMP)
	private Date hora;

	@Column(name = "hora_fin")
	@Temporal(TemporalType.TIMESTAMP)
	private Date horaFin;

	private String info;
	private String fuente;
	private String visualizacion;

	protected Clima() {
	}

	public Clima(String ciudad, String tipo, Date hora, Date horaFin, String info, String fuente,
			String visualizacion) {
		this.ciudad = ciudad.toUpperCase();
		this.tipo = tipo;
		this.hora = hora;
		this.horaFin = horaFin;
		this.info = info;
		this.fuente = fuente;
		this.visualizacion = visualizacion;
	}

	public String getCiudad() {
		return ciudad;
	}

	public String getTipo() {
		return tipo;
	}

	public Date getHora() {
		return hora;
	}

	public Date getHoraFin() {
		return horaFin;
	}

	public String getInfo() {
		return info;
	}

	public String getFuente() {
		return fuente;
	}

	public String getVisualizacion() {
		return visualizacion;
	}

}
