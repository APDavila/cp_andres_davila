package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.ConfirmaCompra;

@Stateless
public class ConfirmaCompraDao {

	@PersistenceContext
	private EntityManager em;
	
	public ConfirmaCompra insertar(ConfirmaCompra confirmaCompra) {
		em.persist(confirmaCompra);
		return confirmaCompra;
	}
}
