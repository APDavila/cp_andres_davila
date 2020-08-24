package com.componente_practico.webhook.ejb.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.Usuario;
import com.componente_practico.webhook.ejb.modelo.EventoAgendado;

@Stateless
public class EventoAgendadoDao {

	@PersistenceContext
	private EntityManager em;
	
	public void guardar(EventoAgendado eventoAgendado) {
		em.persist(eventoAgendado);
	}
	
	public void modificar(EventoAgendado eventoAgendado) {
		em.merge(eventoAgendado);
	}
	
	public List<EventoAgendado> obtenerEventosPorDiaUsuario(Date fechaInicio, Date fechaFin, Usuario usuario) {
		return em.createNamedQuery("eventoAgendado.porDiaUsuario", EventoAgendado.class)
				.setParameter("fechaInicio", fechaInicio)
				.setParameter("fechaFin", fechaFin)
				.setParameter("usuario", usuario)
				.getResultList();
		
	}
	
	public List<EventoAgendado> obtenerParaNotificar(Date fecha) {
		return em.createNamedQuery("eventoAgendado.porNotificar", EventoAgendado.class)
				.setParameter("fecha", fecha)
				.getResultList();
		
	}
}
