package com.componente_practico.broadcast.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.broadcast.entidad.MensajesBroadcast;
import com.componente_practico.broadcast.entidad.OpcionesBroadcast;

@Stateless
public class OpcionesBroadcastDao {

	@PersistenceContext
	EntityManager em;
	
	public OpcionesBroadcast insertar(OpcionesBroadcast opcionesBroadcast) {
		em.persist(opcionesBroadcast);
		return opcionesBroadcast;
	}
	
	public void modificar(OpcionesBroadcast opcionesBroadcast) {
		em.merge(opcionesBroadcast);
	}
	
	public OpcionesBroadcast obtenerPorId(long id) {
		return em.find(OpcionesBroadcast.class, id);
	}
	
	public List<OpcionesBroadcast> buscar(boolean ab_activos, MensajesBroadcast mensajesBroadcast) {
		final List<OpcionesBroadcast> result = em.createNamedQuery("opcionesBroadcast.buscar", OpcionesBroadcast.class).setParameter("estaActivo", ab_activos)
				.setParameter("mensajesBroadcast", mensajesBroadcast).getResultList();
		return result.isEmpty() ? null : result;
	}
	
	public List<OpcionesBroadcast> buscar(MensajesBroadcast mensajesBroadcast) {
		final List<OpcionesBroadcast> result = em.createNamedQuery("opcionesBroadcast.todasOpciones", OpcionesBroadcast.class)
				.setParameter("mensajesBroadcast", mensajesBroadcast).getResultList();
		return result.isEmpty() ? null : result;
	}
	
	public List<OpcionesBroadcast> buscarProcesados() {
		final List<OpcionesBroadcast> result = em.createNamedQuery("opcionesBroadcast.todasOpcionesProcesadas", OpcionesBroadcast.class)
				.getResultList();
		return result.isEmpty() ? null : result;
	}
	
}
