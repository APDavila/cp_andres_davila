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
		@NamedQuery(name = "noticia.porTituloFuente", query = "select o from Noticia o where o.titulo = :titulo and o.fuente = :fuente"),
		@NamedQuery(name = "noticia.ultimasPorFuenteSeccion", query = "select o from Noticia o where o.fuente = :fuente and o.seccion = :seccion order by o.id desc") })

public class Noticia extends EntityGeneral {

	private String fuente;
	private String seccion;
	private String titulo;
	private String resumen;

	@Temporal(TemporalType.TIMESTAMP)
	private Date fecha;

	@Column(name = "url_imagen")
	private String urlImagen;

	@Column(name = "url_noticia")
	private String urlNoticia;
	
	protected Noticia() {}

	public Noticia(String fuente, String seccion, String titulo, String resumen, Date fecha, String urlImagen, String urlNoticia) {
		this.fuente = fuente;
		this.seccion = seccion;
		this.titulo = titulo;
		this.resumen = resumen;
		this.fecha = (fecha == null ? new Date() : fecha);
		this.urlImagen = urlImagen;
		this.urlNoticia = urlNoticia;
	}

	public String getFuente() {
		return fuente;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getResumen() {
		return resumen;
	}

	public Date getFecha() {
		return fecha;
	}

	public String getUrlImagen() {
		return urlImagen;
	}

	public String getUrlNoticia() {
		return urlNoticia;
	}

	public String getSeccion() {
		return seccion;
	}

}
