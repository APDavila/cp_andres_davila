package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.Usuario_Trace;

@Stateless
public class Usuario_TraceDao {

	@PersistenceContext
	private EntityManager em;
	
	public Usuario_Trace insertar(Usuario_Trace usuario_Trace) {
		em.persist(usuario_Trace);
		return usuario_Trace;
	}
	
	public void modificar(Usuario_Trace usuario_Trace) {
		em.merge(usuario_Trace);
	}
}
