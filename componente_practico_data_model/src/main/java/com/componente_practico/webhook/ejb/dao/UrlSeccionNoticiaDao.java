package com.componente_practico.webhook.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.FuenteNoticia;
import com.componente_practico.webhook.ejb.modelo.SeccionNoticia;
import com.componente_practico.webhook.ejb.modelo.UrlSeccionNoticia;

@Stateless
public class UrlSeccionNoticiaDao {

	@PersistenceContext
	private EntityManager em;
	
	public List<FuenteNoticia> obtenerFuentesNoticas() {
		return em.createNamedQuery("urlSeccionNoticia.soloFuenteActiva", FuenteNoticia.class).getResultList();
	}
	
	public List<SeccionNoticia> obtenerSecciones(String fuente) {
		return em.createNamedQuery("urlSeccionNoticia.soloSeccionActiva", SeccionNoticia.class)
				.setParameter("fuente", fuente)
				.getResultList();
	}
	
	public List<UrlSeccionNoticia> obtenerUrlSeccion(String fuente, String seccion) {
		return em.createNamedQuery("urlSeccionNoticia.porFuenteSeccion", UrlSeccionNoticia.class)
				.setParameter("fuente", fuente)
				.setParameter("seccion", seccion)
				.getResultList();
	}
}
