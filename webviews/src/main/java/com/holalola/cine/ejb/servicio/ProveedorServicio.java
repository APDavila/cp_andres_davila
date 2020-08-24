package com.holalola.cine.ejb.servicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;


@Stateless
public class ProveedorServicio {
	@EJB
	private ProveedorDao proveedorDao;
		
	public Proveedor obtenerPorId(Long id) {
		Proveedor result = proveedorDao.obtenerPorId(id); //em.createNamedQuery("usuario.porId", Usuario.class).setParameter("id", idUsuario).getResultList();
		return result;
	}
}
