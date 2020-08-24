package com.componente_practico.webhook.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.OpcionAgenda;

@Stateless
public class OpcionAgendaDao {

	@PersistenceContext
	private EntityManager em;
	
	public List<OpcionAgenda> obtenerActivas() {
		return em.createNamedQuery("opcionAgenda.activas", OpcionAgenda.class).getResultList();
	}
}
