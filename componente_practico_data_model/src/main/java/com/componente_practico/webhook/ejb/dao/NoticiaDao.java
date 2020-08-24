package com.componente_practico.webhook.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.Noticia;
import com.componente_practico.webhook.facebook.ConstantesFacebook;

@Stateless
public class NoticiaDao {

	@PersistenceContext
	private EntityManager em;
	
	public void guardar(Noticia noticia) {
		em.persist(noticia);
	}
	
	public Noticia obtenerPorId(long id) {
		return em.find(Noticia.class, id);
	}
	
	public List<Noticia> obtenerUtlimas(String fuente, String seccion) {
		return em.createNamedQuery("noticia.ultimasPorFuenteSeccion", Noticia.class)
				.setParameter("fuente", fuente)
				.setParameter("seccion", seccion)
				.setMaxResults(ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE)
				.getResultList();
	}
	
	public List<Noticia> obtenerPorTituloFuente(String titulo, String fuente) {
		return em.createNamedQuery("noticia.porTituloFuente", Noticia.class)
				.setParameter("titulo", titulo)
				.setParameter("fuente", fuente)
				.getResultList();
	}
}
