package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.Logs;;

@Stateless
public class LogsDao {

	@PersistenceContext
	private EntityManager em;
	
	public Logs insertar(Logs logs) {
		em.persist(logs);
		return logs;
	}
	
	
}
