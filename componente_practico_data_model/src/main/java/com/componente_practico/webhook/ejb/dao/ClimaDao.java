package com.componente_practico.webhook.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.Clima;

@Stateless
public class ClimaDao {

	@PersistenceContext
	private EntityManager em;
	
	public Clima guardar(Clima clima) {
		em.persist(clima);
		
		return clima;
	}
	
	public List<Clima> obtenerUltimaPorCiudadTipo(String ciudad, String tipo) {
		return em.createNamedQuery("clima.ultimaPorCiudadTipo", Clima.class)
				.setParameter("ciudad", ciudad)
				.setParameter("tipo", tipo)
				.setMaxResults(1)
				.getResultList();
	}
}
