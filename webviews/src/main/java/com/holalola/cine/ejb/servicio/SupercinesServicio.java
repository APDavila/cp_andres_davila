package com.holalola.cine.ejb.servicio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.cine.ejb.dao.ComplejoCineDao;
import com.holalola.cine.ejb.dao.DetallePedidoSupercinesDao;
import com.holalola.cine.ejb.dao.PedidoSupercinesDao;
import com.holalola.cine.ejb.dao.PeliculaDao;
import com.holalola.cine.ejb.modelo.ComplejoCine;
import com.holalola.cine.ejb.modelo.DetallePedidoSupercines;
import com.holalola.cine.ejb.modelo.PedidoSupercines;
import com.holalola.cine.ejb.modelo.Pelicula;
import com.holalola.general.ejb.dao.LogsDao;
import com.holalola.general.ejb.modelo.Logs;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.supercines.client.SupercinesApi;
import com.holalola.supercines.client.exception.PeliculaNoDisponibleException;
import com.holalola.supercines.client.vo.EnvioPagoMV;
import com.holalola.supercines.client.vo.InformacionAdicionalMV;
import com.holalola.supercines.client.vo.PrecioTicketMV.DetallePrecioTicketMV;
import com.holalola.supercines.client.vo.ReservaPuestoSupercinesMV;
import com.holalola.supercines.client.vo.RespuestaCancelacionMV;
import com.holalola.supercines.client.vo.RespuestaReservaPuestoMV;
import com.holalola.supercines.client.vo.RetornoPagoMV;
import com.holalola.supercines.client.vo.TarjetasMV;
import com.holalola.supercines.client.vo.TipoTicketMV;
import com.holalola.supercines.client.web.vo.DisposicionSalaSueprcines;

@Stateless
public class SupercinesServicio {

	@EJB
	PedidoSupercinesDao pedidoSupercinesDao;

	@EJB
	PeliculaDao peliculaDao;

	@EJB
	ComplejoCineDao complejoCineDao;

	@EJB
	DetallePedidoSupercinesDao detallePedidoSupercinesDao;
	
	@EJB
	LogsDao logsDao;
	
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
	
	public int minutosCancelacionPelicula()
	{
		try
		{
			int minutos = Integer.parseInt(System.getProperty("lola.tiempoCancelacion"));
			
			if(minutos > 0)
				return minutos;
			else
			{
				if(minutos == 0)
					return 30;		//Retorna el Default
				else
					return minutos * -1;
			}
		}
		catch(Exception er)
		{
			return 30;				//Retorna el Default
		}
	}

	public PedidoSupercines obtenerPedidoActivoUsuario(Usuario usuario) {

		final PedidoSupercines pedido = pedidoSupercinesDao.obtenerPedidoActivoUsuario(usuario).get(0);

		return pedido;
	}

	public PedidoSupercines obtenerPedidoCompleto(long pedidoId) {
		final PedidoSupercines pedido = pedidoSupercinesDao.obtenerPorId(pedidoId);

		if (pedido == null)
			return null;

		pedido.getDetallePedidoSupercines().isEmpty();
		return pedido;
	}

	public void guardarPedido(PedidoSupercines pedido) {
		pedidoSupercinesDao.modificar(pedido);
	}

	public Pelicula obtenerPeliculaPorMovieToken(String movieToken) {
		return peliculaDao.obtenerPorSupercinesId(movieToken,4L).get(0);
	}

	public ComplejoCine obtenerComplejoPorSupercinesId(Long supercinesId) {
		return complejoCineDao.obtenerPorSupercinesId(supercinesId).get(0);
	}

	public DisposicionSalaSueprcines obtenerDisposicionSala(PedidoSupercines pedido, String email)
			throws PeliculaNoDisponibleException {

		return SupercinesApi.obtenerSalaCineParaWeb(pedido.getCodigoFormato(), String.valueOf(pedido.getHorarioId()), obtenerParamsTicketSeleccionados(pedido), email, new InformacionAdicionalMV());
	}
	
	public TarjetasMV recuperaTarjetas(String as_identidad) throws Exception {
		return SupercinesApi.recuperaTarjetas(as_identidad);
	}

	public RespuestaReservaPuestoMV reservarPuestos(ReservaPuestoSupercinesMV paramReservaPuestos) throws Exception {
		return SupercinesApi.reservarPuestos(paramReservaPuestos);
	}
	
	public RetornoPagoMV procesaPago(EnvioPagoMV paramReservaPuestos) throws Exception {
		return SupercinesApi.procesaPago(paramReservaPuestos);
	}

	public RespuestaCancelacionMV cancelarReserva(String user_session_id, boolean ab_forzada) throws Exception {
		return SupercinesApi.cancelarReserva(user_session_id);//, ab_forzada);
	}

	/*public RetornoMessage urlPortalPagos(String parametroENtrada) throws Exception {
		return SupercinesApi.urlPortalPagos(parametroENtrada);
	}*/
	
	protected String getLocalizedBigDecimalValue(BigDecimal input) {
		input = input.setScale(2, BigDecimal.ROUND_DOWN);
	    String ls_temp = input.toString();
		return ls_temp;
	}

	private List<TipoTicketMV> obtenerParamsTicketSeleccionados(PedidoSupercines pedido) {

		return pedido.getDetallePedidoSupercines().stream().map(d -> {
			return new TipoTicketMV(d.getCodigoTicket(), getLocalizedBigDecimalValue(d.getPrecio()).replace(',', '.'), d.getCantidad());
		}).collect(Collectors.toList());
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void agregarDetallePedidoSupercines(PedidoSupercines pedidoActual, Usuario usuario, DetallePrecioTicketMV tipoTicket,
			int cantidad) {
		if (cantidad < 1 || pedidoActual == null || usuario == null || tipoTicket == null)
			return;
		
		

		DetallePedidoSupercines detalle = null;
		try
		{
			detalle = new DetallePedidoSupercines(pedidoActual, tipoTicket.getTicketDescription(),
					tipoTicket.getTicketCode(), new BigDecimal(tipoTicket.getTicketPrice()), cantidad);
		}
		catch(Exception err)
		{
			detalle = new DetallePedidoSupercines(pedidoActual, tipoTicket.getTicketDescription(),
					tipoTicket.getTicketCode(), new BigDecimal(tipoTicket.getTicketPrice().replace('.', ',')), cantidad);
		}

		detallePedidoSupercinesDao.insertar(detalle);

		pedidoActual.calcularValorTotal(detalle.getValorTotal());
	}

	public void borrarPorIdPedido(PedidoSupercines pedidoActual) {
		if (pedidoActual == null)
			return;

		detallePedidoSupercinesDao.borrarPorIdPedido(pedidoActual);
	}
}
