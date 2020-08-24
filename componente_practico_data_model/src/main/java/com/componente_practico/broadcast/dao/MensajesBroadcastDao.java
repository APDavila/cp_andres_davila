package com.componente_practico.broadcast.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.broadcast.entidad.MensajesBroadcast;

@Stateless
public class MensajesBroadcastDao {

	@PersistenceContext
	EntityManager em;
	
	public MensajesBroadcast insertar(MensajesBroadcast mensajesBroadcast) {
		if(mensajesBroadcast.getFiltrarSexo() == null)
			 mensajesBroadcast.setFiltrarSexo(" ");
		
		em.persist(mensajesBroadcast);
		return mensajesBroadcast;
	}
	
	public void modificar(MensajesBroadcast mensajesBroadcast) {
		if(mensajesBroadcast.getFiltrarSexo() == null)
			 mensajesBroadcast.setFiltrarSexo(" ");
		em.merge(mensajesBroadcast);
	}
	
	public List<MensajesBroadcast> buscar(boolean ab_activos, String as_filtrarSexo, String as_filtrarNombre, String as_mensaje) {
		final List<MensajesBroadcast> result = em.createNamedQuery("mensajesBroadcast.buscar", MensajesBroadcast.class).setParameter("estaActivo", ab_activos)
				.setParameter("filtrarSexo", as_filtrarSexo)
				.setParameter("filtrarNombre", as_filtrarNombre)
				.setParameter("mensaje", as_mensaje).getResultList();
		return result;
	}
	
	public List<MensajesBroadcast> buscarConFechas(boolean ab_activos, String as_filtrarSexo, String as_filtrarNombre, String as_mensaje, 
												  Date ad_fechaInicio, Date ad_fechaFin) {
		final List<MensajesBroadcast> result = em.createNamedQuery("mensajesBroadcast.buscarConFechas", MensajesBroadcast.class).setParameter("estaActivo", ab_activos)
				.setParameter("filtrarSexo", as_filtrarSexo)
				.setParameter("filtrarNombre", as_filtrarNombre)
				.setParameter("mensaje", as_mensaje)
				.setParameter("fechaInicio", ad_fechaInicio)
				.setParameter("fechaFin", ad_fechaFin).getResultList();
		return result;
	}
	
	public List<BigInteger> dameUsuariosPorMensajeBroadCast(MensajesBroadcast mensajesBroadcast, String ls_filtroNombre, String ls_filtroGenero, Boolean ab_aplicarPorFiltro) {

		List<BigInteger> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_DAMEUSUARIOS_OPC_BROADCAST(?,?,?,?)}").
					setParameter(1, mensajesBroadcast.getId())
					.setParameter(2, (ls_filtroNombre != null ? "%"+ls_filtroNombre.trim()+"%" : "%"))
					.setParameter(3, (ls_filtroGenero != null ? "%"+ls_filtroGenero.trim()+"%" : "%"))
					.setParameter(4, (ab_aplicarPorFiltro ? 1 : 0)).getResultList();

			if (listaResultado != null )
				return listaResultado;
			else
				return new ArrayList<BigInteger>();
		}
		catch(Exception err)
		{
			return new ArrayList<BigInteger>();
		}
	}
}
