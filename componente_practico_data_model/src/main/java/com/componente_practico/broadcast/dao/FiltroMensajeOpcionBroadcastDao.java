 package com.componente_practico.broadcast.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.broadcast.entidad.FiltroMensajeOpcionBroadcast;
import com.componente_practico.broadcast.entidad.MensajesBroadcast;

@Stateless
public class FiltroMensajeOpcionBroadcastDao {

	@PersistenceContext
	EntityManager em;
	
	public FiltroMensajeOpcionBroadcast insertar(FiltroMensajeOpcionBroadcast filtroMensajeOpcionBroadcast) {
		em.persist(filtroMensajeOpcionBroadcast);
		return filtroMensajeOpcionBroadcast;
	}
	
	public void modificar(FiltroMensajeOpcionBroadcast filtroMensajeOpcionBroadcast) {
		em.merge(filtroMensajeOpcionBroadcast);
	}
	
	public List<FiltroMensajeOpcionBroadcast> obtenerPorMensajesBroadcastActivos(MensajesBroadcast mensajesBroadcast) {
		try 
		{
			final List<FiltroMensajeOpcionBroadcast> result = em.createNamedQuery("filtroMensajeOpcionBroadcast.activos", FiltroMensajeOpcionBroadcast.class).setParameter("mensajesBroadcast", mensajesBroadcast).getResultList();
			return result.isEmpty() ? new ArrayList<FiltroMensajeOpcionBroadcast>() : result;
		}
		catch(Exception err)
		{
			return new ArrayList<FiltroMensajeOpcionBroadcast>();
		}
		
	}
	
	public List<FiltroMensajeOpcionBroadcast> obtenerPorMensajesBroadcastTodos(MensajesBroadcast mensajesBroadcast) {
		try 
		{
			final List<FiltroMensajeOpcionBroadcast> result = em.createNamedQuery("filtroMensajeOpcionBroadcast.todos", FiltroMensajeOpcionBroadcast.class).setParameter("mensajesBroadcast", mensajesBroadcast).getResultList();
			return result.isEmpty() ? new ArrayList<FiltroMensajeOpcionBroadcast>() : result;
		}
		catch(Exception err)
		{
			return new ArrayList<FiltroMensajeOpcionBroadcast>();
		}
	}
}
