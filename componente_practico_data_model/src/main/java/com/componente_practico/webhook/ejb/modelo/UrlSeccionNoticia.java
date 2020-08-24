package com.componente_practico.webhook.ejb.modelo;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@Table(name = "url_seccion_noticia")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "urlSeccionNoticia.porFuenteSeccion", query = "select o from UrlSeccionNoticia o where o.fuente.payload = :fuente and o.seccion.payload = :seccion and o.activo = true"),
	@NamedQuery(name = "urlSeccionNoticia.soloSeccionActiva", query = "select o.seccion from UrlSeccionNoticia o where o.fuente.payload = :fuente and o.activo = true"),
	@NamedQuery(name = "urlSeccionNoticia.soloFuenteActiva", query = "select distinct o.fuente from UrlSeccionNoticia o where o.activo = true"),
})

public class UrlSeccionNoticia extends EntityGeneral {
	
	private String url;
	private boolean activo;
	
	@ManyToOne
	private FuenteNoticia fuente;
	
	@ManyToOne
	private SeccionNoticia seccion;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public FuenteNoticia getFuente() {
		return fuente;
	}

	public void setFuente(FuenteNoticia fuente) {
		this.fuente = fuente;
	}

	public SeccionNoticia getSeccion() {
		return seccion;
	}

	public void setSeccion(SeccionNoticia seccion) {
		this.seccion = seccion;
	}
}
