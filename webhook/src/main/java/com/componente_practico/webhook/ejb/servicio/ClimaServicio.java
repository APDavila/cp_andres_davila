package com.componente_practico.webhook.ejb.servicio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.webhook.ejb.dao.ClimaDao;
import com.holalola.webhook.ejb.modelo.Clima;

@Stateless
public class ClimaServicio {

	@EJB
	private ClimaDao climaDao;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Clima guardar(Clima clima) {
		return climaDao.guardar(clima);
	}
	
	public Clima validarClima(String ciudad, String tipo) {
		final List<Clima> consultas = climaDao.obtenerUltimaPorCiudadTipo(ciudad, tipo);
		
		if (consultas.isEmpty()) return null;
		
		return consultas.get(0);
	}
}
