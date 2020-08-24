package com.componente_practico.webhook.ejb.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.eventos.ejb.modelo.Entradas;
import com.componente_practico.webhook.ejb.modelo.Servicio;

@Stateless
public class ServicioDao {

	@PersistenceContext
	EntityManager em;
	
	public List<Servicio> obtenerActivos() {
		return em.createNamedQuery("servicio.activos", Servicio.class).getResultList();
	}
	
	public Servicio obtenerPorPayload(String asPayload) {
		List<Servicio> result = em.createNamedQuery("servicio.porPayload", Servicio.class).
				setParameter("as_payload", asPayload).getResultList();
		if(result.size()<1) {
			Servicio servicios = Servicio.getInstanciaVacia();
			servicios.setId(0L);
			servicios.setNombre("");
			servicios.setOrden(0);
			servicios.setPayload("");
			servicios.setVisible(false);
			result.add(servicios);	
		}
		return result.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public List<Servicio> obtenerTipos(String as_nombre, boolean as_activo) {
		List<Servicio> result = em.createNativeQuery("{call SP_OBTENERSERVICIOS(?,?)}",Servicio.class).
		setParameter(1, "%"+as_nombre+"%").
		setParameter(2, as_activo).getResultList();
		return (result==null ? new ArrayList<Servicio>():result);
	}
	
	public List<Servicio> obtenerVisibles() {
		return em.createNamedQuery("servicio.visibles", Servicio.class).getResultList();
	}
	
	public Servicio modificar(Servicio servicio) {
		return em.merge(servicio);
	}
	
	public Servicio insertar(Servicio servicio) {
		em.persist(servicio);
		return servicio;
	}
}

