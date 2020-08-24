package com.holalola.ejb.general.servicio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.comida.pedido.ejb.dao.ProductoDao;
import com.holalola.comida.pedido.ejb.modelo.Producto;

@Stateless
public class ProductoServicio {
	@EJB
	ProductoDao productoDao;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Producto> obtenerProductoActivo(Long al_idProveedor) {

		return productoDao.obtenerProductoActivos(al_idProveedor);
	}
}
