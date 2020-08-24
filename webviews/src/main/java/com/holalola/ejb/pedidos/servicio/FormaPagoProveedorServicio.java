package com.holalola.ejb.pedidos.servicio;

import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.holalola.comida.pedido.ejb.dao.FormaPagoProveedorDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.modelo.FormaPago;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;

@Stateless
public class FormaPagoProveedorServicio {

	@EJB
	FormaPagoProveedorDao formaPagoProveedorDao;
	
	@EJB
	PedidoDao pedidoDao;
	
	@Transactional(value = TxType.REQUIRED)
	public List<FormaPago> obtenerFormasPagoParaPedido(long pedidoId) {
		final Proveedor proveedor = pedidoDao.obtenerPorId(pedidoId).getProveedor();
		return obtenerFormasPagoPorProveedor(proveedor);
	}
	
	public List<FormaPago> obtenerFormasPagoPorProveedor(Proveedor proveedor) {
		return formaPagoProveedorDao.obtenerPorProveedor(proveedor).stream().map(f -> {
			return f.getFormaPago();
		}).collect(Collectors.toList());
	}
}
