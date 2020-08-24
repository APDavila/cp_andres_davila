package com.holalola.ejb.pedidos.servicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.holalola.comida.pedido.ejb.dao.DatoFacturaPedidoDao;
import com.holalola.comida.pedido.ejb.modelo.DatoFacturaPedido;

@Stateless
public class DatoFacturaPedidoServicio {

	@EJB
	DatoFacturaPedidoDao datoFacturaPedidoDao;
	
	@Transactional(value = TxType.REQUIRED)
	public void insertar(DatoFacturaPedido datoFacturaPedido) {
		datoFacturaPedidoDao.insertar(datoFacturaPedido);
	}
		
}
