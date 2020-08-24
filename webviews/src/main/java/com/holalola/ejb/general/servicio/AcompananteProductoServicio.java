package com.holalola.ejb.general.servicio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.comida.pedido.ejb.dao.AcompananteProductoDao;
import com.holalola.comida.pedido.ejb.modelo.AcompananteProducto;
import com.holalola.comida.pedido.ejb.modelo.Producto;
@Stateless
public class AcompananteProductoServicio {
	@EJB
	AcompananteProductoDao acompananteProductoDao;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<AcompananteProducto> obtenerAcompananteProductoPorTipoAffna(long productoId) {
		
		return acompananteProductoDao.obtenerAcompanantesProductoPorTipoAcompanante(productoId, acompananteProductoDao.getLi_tipoAcompananteEntrada());
	}
	
	
	
}
