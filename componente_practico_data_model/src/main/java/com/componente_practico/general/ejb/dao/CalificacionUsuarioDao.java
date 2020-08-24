package com.componente_practico.general.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.componente_practico.general.ejb.modelo.CalificacionUsuario;

@Stateless
public class CalificacionUsuarioDao {

	@PersistenceContext
	private EntityManager em;
	
	
	@Transactional(value = TxType.REQUIRED)
	public CalificacionUsuario insertar(CalificacionUsuario calificacionUsuario) {
		em.persist(calificacionUsuario);
		return calificacionUsuario;
	}
	
	@Transactional(value = TxType.REQUIRED)
	public CalificacionUsuario modificar(CalificacionUsuario calificacionUsuario) {
		em.merge(calificacionUsuario);
		return calificacionUsuario;
	}
	
	public List<CalificacionUsuario> obtenerListaCalificaciones() {
		return em.createNamedQuery("calificacionUsuario.todas", CalificacionUsuario.class).getResultList();
	}
}
