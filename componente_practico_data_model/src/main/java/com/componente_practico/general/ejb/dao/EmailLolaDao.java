package com.componente_practico.general.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.componente_practico.general.ejb.modelo.EmailLola;

@Stateless
public class EmailLolaDao {


	@PersistenceContext
	private EntityManager em;
	
	
	@Transactional(value = TxType.REQUIRED)
	public EmailLola insertar(EmailLola ciudad) {
		em.persist(ciudad);
		return ciudad;
	}

	public List<EmailLola> obtenerEmailsActivos() {
		return em.createNamedQuery("emailLola.todos", EmailLola.class).getResultList();
	}
	
}
