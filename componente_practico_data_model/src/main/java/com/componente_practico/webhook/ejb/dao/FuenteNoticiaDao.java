package com.componente_practico.webhook.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.FuenteNoticia;

@Stateless
public class FuenteNoticiaDao {

	@PersistenceContext
	private EntityManager em;
	
	public FuenteNoticia obtenerPorPayload(String fuente) {
		
		return em.createNamedQuery("fuenteNoticia.porPayload", FuenteNoticia.class).setParameter("payload", fuente).getSingleResult();
		
	}
}
