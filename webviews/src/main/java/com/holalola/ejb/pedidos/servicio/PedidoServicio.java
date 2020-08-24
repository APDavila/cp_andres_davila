package com.holalola.ejb.pedidos.servicio;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.dao.LogsDao;
import com.holalola.general.ejb.modelo.Logs;
import com.holalola.general.ejb.modelo.Usuario;

@Stateless
public class PedidoServicio {

	@EJB
	PedidoDao pedidoDao;
	
	@EJB
	LogsDao logsDao;
	
	@Transactional(value = TxType.REQUIRED)
	public Pedido obtenerPedidoCinePorId(long pedidoId) {
		final Pedido pedido = pedidoDao.obtenerPorId(pedidoId);
		//pedido.getDetallesPedidoCine().size();
		
		return pedido;
	}
	
	public Pedido obtenerPendienteUsuario(Usuario usuario, Proveedor proveedor) {
		return pedidoDao.obtenerPendienteUsuario(usuario, proveedor).get(0);
	}
	
	public Pedido obtenerUnPendienteUsuario(Usuario usuario) {
		return pedidoDao.listaPendientePorUsuario(usuario).get(0);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modificar(Pedido pedido) {
		pedidoDao.modificar(pedido);
	}
	
	@Transactional(value = TxType.REQUIRED)
	public Pedido obtenerPedidoComidaPorId(long pedidoId) {
		final Pedido pedido = pedidoDao.obtenerPorId(pedidoId);
		pedido.getDetallesPedido().size();
		
		return pedido;
	}
	
	public Pedido obtenerPorId(long pedidoId) {
		return pedidoDao.obtenerPorId(pedidoId);
	}
	
	public void crearLog(String metodo, Date fechaInicioProceso, String parametros, Date fechaFinProceso, String mensaje, Boolean esDeLola)
	{
		try
		{
			if (mensaje.length() > 500)
            	mensaje = mensaje.substring(0, 498);
			
			if (metodo.length() > 200)
				metodo = metodo.substring(0, 198);
			
			if (parametros.length() > 400)
				parametros = parametros.substring(0, 398);
			
			logsDao.insertar(new Logs(metodo, fechaInicioProceso, parametros, fechaFinProceso, mensaje, (esDeLola ? 1 : 0)));
		}
		catch(Exception err)
		{
			//No se pudo generar log
		}
	}
	
	public void crearLogExc(String metodo, Date fechaInicioProceso, String parametros, Date fechaFinProceso, Exception exception, Boolean esDeLola)
	{
		try
		{
			String mensaje = " Error: " +
            " | Mensaje: " + exception.getMessage() +
            " | Source: " + exception.getCause() +
            " | StackTrace: " + exception.getStackTrace();
			mensaje = mensaje.trim();

			if (mensaje.length() > 500)
            	mensaje = mensaje.substring(0, 498);
			
			if (metodo.length() > 200)
				metodo = metodo.substring(0, 198);
			
			if (parametros.length() > 400)
				parametros = parametros.substring(0, 398);
			
			logsDao.insertar(new Logs(metodo, fechaInicioProceso, parametros, fechaFinProceso, mensaje, (esDeLola ? 1 : 0)));
		}
		catch(Exception err)
		{
			//No se pudo generar log
		}
	}
}
