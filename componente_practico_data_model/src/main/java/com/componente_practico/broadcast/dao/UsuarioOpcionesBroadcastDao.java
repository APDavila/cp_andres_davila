package com.componente_practico.broadcast.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.broadcast.entidad.OpcionesBroadcast;
import com.componente_practico.broadcast.entidad.UsuarioOpcionesBroadcast;

@Stateless
public class UsuarioOpcionesBroadcastDao {

	@PersistenceContext
	EntityManager em;
	
	public UsuarioOpcionesBroadcast insertar(UsuarioOpcionesBroadcast usuarioOpcionesBroadcast) {
		em.persist(usuarioOpcionesBroadcast);
		return usuarioOpcionesBroadcast;
	}
	
	public void modificar(UsuarioOpcionesBroadcast usuarioOpcionesBroadcast) {
		em.merge(usuarioOpcionesBroadcast);
	}
	
	public List<UsuarioOpcionesBroadcast> obtenerPorMensajesBroadcastActivos(OpcionesBroadcast opcionesBroadcast) {
		final List<UsuarioOpcionesBroadcast> result = em.createNamedQuery("usuarioOpcionesBroadcast.activos", UsuarioOpcionesBroadcast.class).setParameter("opcionbroadcast_id", opcionesBroadcast.getId()).getResultList();
		return result.isEmpty() ? null : result;
	}
	
	public List<UsuarioOpcionesBroadcast> obtenerPorMensajesBroadcastTodos(OpcionesBroadcast opcionesBroadcast) {
		final List<UsuarioOpcionesBroadcast> result = em.createNamedQuery("usuarioOpcionesBroadcast.usuariosOpcionesBroadcast", UsuarioOpcionesBroadcast.class).setParameter("opcionbroadcast_id", opcionesBroadcast.getId()).getResultList();
		return result.isEmpty() ? null : result;
	}
	
}
