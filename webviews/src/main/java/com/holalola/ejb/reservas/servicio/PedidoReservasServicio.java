package com.holalola.ejb.reservas.servicio;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.dao.LogsDao;
import com.holalola.general.ejb.modelo.Logs;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.reservas.dao.PedidoReservasDao;
import com.holalola.reservas.modelo.PedidosReservas;

@Stateless
public class PedidoReservasServicio {

	@EJB
	PedidoReservasDao pedidoReservasDao;
	
	@EJB
	LogsDao logsDao;
	
	
	public PedidosReservas obtenerPendienteUsuario(Usuario usuario, Proveedor proveedor) {
		return pedidoReservasDao.obtenerPendienteUsuario(usuario, proveedor).get(0);
	}
	
	public PedidosReservas obtenerUnPendienteUsuario(Usuario usuario) {
		return pedidoReservasDao.listaPendientePorUsuario(usuario).get(0);
	}
	
	public PedidosReservas obtenerPorId(long pedidoId) {
		return pedidoReservasDao.obtenerPorId(pedidoId);
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