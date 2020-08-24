package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.RegistroError;

@Stateless
public class RegistroErrorDao {

	@PersistenceContext
	EntityManager em;
	
	public void registrarError(RegistroError registroError) {
		em.persist(registroError);
	}
}
