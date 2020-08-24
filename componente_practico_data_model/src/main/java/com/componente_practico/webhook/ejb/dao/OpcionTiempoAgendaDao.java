package com.componente_practico.webhook.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.OpcionTiempoAgenda;

@Stateless
public class OpcionTiempoAgendaDao {

	@PersistenceContext
	private EntityManager em;
	
	public List<OpcionTiempoAgenda> obtenerActivas() {
		return em.createNamedQuery("opcionTiempoAgenda.activas", OpcionTiempoAgenda.class).getResultList();
	}
}
