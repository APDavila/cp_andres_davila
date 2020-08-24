package com.holalola.cine.ejb.servicio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.holalola.cine.ejb.modelo.PedidoSupercines;
import com.holalola.general.ejb.dao.CiudadDao;
import com.holalola.general.ejb.modelo.Ciudad;

@Stateless
public class CiudadServicio {

	@EJB
	private CiudadDao ciudadDao;
	
	public List<Ciudad> obtenerCiudades() {
		final List<Ciudad> ciudades = ciudadDao.obtenerCiudades();
		return ciudades;
	}
}
