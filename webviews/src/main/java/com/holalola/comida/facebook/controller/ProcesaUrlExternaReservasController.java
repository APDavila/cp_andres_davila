package com.holalola.comida.facebook.controller;

import static com.holalola.webhook.PayloadConstantes.PAYLOAD_CONFIRMAR_PEDIDO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.comida.pedido.ejb.dao.ProveedorDatosPagoDao;
import com.holalola.comida.pedido.ejb.modelo.PedidoTarjeta;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.comida.pedido.ejb.modelo.ProveedorDatosPago;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.pagos.PlaceToPay;
import com.holalola.pagos.PlaceToPay.PlaceToPayConsultaRetorno;
import com.holalola.pagos.ReservasProcesos;
import com.holalola.pagos.entidades.PlaceToPayResponse;
import com.holalola.pagos.entidades.PlaceToPayRetornoReverso;
import com.holalola.reservas.dao.PedidoReservaTarjetaDao;
import com.holalola.reservas.dao.PedidoReservasDao;
import com.holalola.reservas.dao.ReservasDao;
import com.holalola.reservas.modelo.PedidoReservaTarjeta;
import com.holalola.reservas.modelo.PedidosReservas;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.Recipient;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.SenderActionParaFacebook;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;

@ViewScoped
@ManagedBean
@SuppressWarnings("serial")
public class ProcesaUrlExternaReservasController extends GeneralController {

	private String urlExterna;

	private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	private static final String baseUrl = System.getProperty("lola.base.url");
	
	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	PedidoReservasDao pedidoDao;

	@EJB
	PedidoReservaTarjetaDao pedidoTarjetaDao;
	
	@EJB
	private PedidoReservasDao pedidoReservasDao;
	
	@EJB
	private ProveedorDatosPagoDao proveedorDatosPagoDao;
	
	@EJB
	private ReservasDao reservasDao;
	
	
	@EJB
	private ReservasProcesos reservasProcesos;
	
	Boolean ab_listo = false;

	public Boolean getAb_listo() {
		//System.out.println("------------------------------------urlExterna----ab_listo  R----------" + ab_listo);
		return ab_listo;
	}

	public void setAb_listo(Boolean ab_listo) {
		this.ab_listo = ab_listo;
	}

	Long idPedido = (long) 0;

	@PostConstruct
	public void inicializar() throws InterruptedException {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String idPedidoParam = params.get("token");
		String pc = params.get("pc");

		try {
			idPedido = Long.valueOf(idPedidoParam);
		} catch (Exception err) {
			idPedido = (long) 0;
		}

		if (idPedido <= 0) { 
			return; // TODO: Mostrar mensaje de error
		}

		PedidosReservas pedido = pedidoDao.obtenerPorId(idPedido);

		if (pedido == null || pedido.getEstado() != PedidosReservas.PENDIENTE) {
			return; // TODO: Mostrar mensaje de error
		}

		final List<PedidoReservaTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
		if (listaPedidoTarjeta == null || listaPedidoTarjeta.size() <= 0)
			return; // TODO: Mostrar mensaje de error

		PedidoReservaTarjeta pedidoTarjeta = listaPedidoTarjeta.get(0);

		if (pedidoTarjeta == null) {
			return; // TODO: Mostrar mensaje de error
		}

		PlaceToPayResponse placeToPayResponse = GSON.fromJson(listaPedidoTarjeta.get(0).getRetorno(),
				PlaceToPayResponse.class);
		
		//System.out.println("------------------------------------urlExterna----pc----------" + pc);

		if(pc != "-1")
			urlExterna = placeToPayResponse.getProcessUrl();
		else
			ab_listo = true;		
	}
	
	private void mostrarAccionDeTipeo(String idUsuarioFacebook) {
		SenderActionParaFacebook senderActionParaFacebook = new SenderActionParaFacebook(new Recipient(idUsuarioFacebook), SenderActionParaFacebook.TYPING_ON);
		ConsultarFacebook.postToFacebook(senderActionParaFacebook, propiedadesLola.facebookToken);
	}
	
	public void confirmarplacetopayInt() {

		//System.out.println("----------------------Vito--------------urlExterna----ab_listo----------" + ab_listo);
		
				
		if(ab_listo)
			return;
		//System.out.println("----------------------Vito--------------urlExterna--1--ab_listo----------" + ab_listo);
		
		
		PlaceToPayConsultaRetorno li_estatoPago = null;  // 1 Pagado, 2 Reversado, 0 no procesado
		try {

			PedidosReservas pedido = pedidoReservasDao.obtenerPorId(idPedido);

			List<PedidoReservaTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
			PedidoReservaTarjeta pedidoTarjeta = listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0
					? listaPedidoTarjeta.get(0)
					: null;

			if (pedidoTarjeta == null) {
				ab_listo = true;
				//System.out.println("------------------------------------confirmarplacetopay----pedidoTarjeta == null----------");
				return;/*
						 * Response.status(200).header("Content-Type", "text/csv; charset=utf-8")
						 * .entity("No se reconoce el pedido....").build();
						 */
			} 

			ProveedorDatosPago proveedorDatosPago = null;
			
			Proveedor proveedor = pedido.getRestaurante().getCiudades().getProveedor();
			
			try
			{
				proveedorDatosPago = proveedorDatosPagoDao.porProveedorPlaceToPay(proveedor);
			}
			catch(Exception err)
			{
				proveedorDatosPago = null;
			}
			
			boolean ab_datosProveedor = proveedorDatosPago != null && proveedorDatosPago.getLogin() != null && proveedorDatosPago.getTranKey() != null && proveedorDatosPago.getLogin().trim().length() > 0 && proveedorDatosPago.getTranKey().trim().length() > 0;
			
			
			
				if (pedidoTarjeta.getConfirmado()) {
					ab_listo = true;
					return; 
				}

				PlaceToPayResponse placeToPayResponse = GSON.fromJson(listaPedidoTarjeta.get(0).getRetorno(),
						PlaceToPayResponse.class);
				
				li_estatoPago = PlaceToPay.validarPago(String.valueOf(placeToPayResponse.getRequestId()),
						(ab_datosProveedor ? proveedorDatosPago.getLogin() : ""),
						 (ab_datosProveedor ? proveedorDatosPago.getTranKey() : ""),
						 true);
				
				//System.out.println("----------------------Vito-------------li_estatoPago----------" + li_estatoPago);
				
				if ( li_estatoPago.getLi_validacionConsulta() == 0)
				{
					ab_listo = true;
					return;
				}
				
				if(li_estatoPago.getLi_validacionConsulta() == 4)
				{
					String speech = String.format("%s, tu reserva en %s, se encuentra en procesó, en cuanto tu Institución Financiera me confirme si el proceso se completo te lo informo.", pedido.getUsuario().getNombreFacebook(), pedido.getRestaurante().getNombre());

					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(speech)),
							propiedadesLola.facebookToken);
					ab_listo = true;
					return;
				}
				
				if(li_estatoPago.getLi_validacionConsulta() == 3)
				{
					if(listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0)
					{
						
						//PlaceToPayRetornoReverso placeToPayRetornoReverso = PlaceToPay.reversarCompra(String.valueOf(placeToPayResponse.getRequestId()));
						
						String message = String.format("%s, se reversó el pago de la Reserva "+pedido.getId()+" de "+pedido.getRestaurante().getNombre()+".", pedido.getUsuario().getNombreFacebook());
												
						pedidoTarjeta.setReversado(true);
						//pedidoTarjeta.setRetornoreverso(GSON.toJson(placeToPayRetornoReverso).toString());
						pedidoTarjeta.setFechareverso(new Date());
						
						pedidoTarjetaDao.modificar(pedidoTarjeta);
						
						pedido.setEstado(PedidosReservas.CANCELADO);
						pedido.setEnproceso(false);
						pedido.setConfirmadoUsuario(true);
						reservasDao.modificar(pedido);
						
						ConsultarFacebook.postToFacebook(
								new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(message)),
								propiedadesLola.facebookToken
								);
	
						ab_listo = true;
						return;
					}
				}
				
				if(li_estatoPago.getLi_validacionConsulta() == 2)
				{
					
					if(pedidoTarjeta.getRetornoreverso() == null || pedidoTarjeta.getRetornoreverso().trim().length() <= 0)
					{
						pedidoTarjeta.setRetornoreverso(PlaceToPay.consultaPago(String.valueOf(placeToPayResponse.getRequestId()),
								(ab_datosProveedor ? proveedorDatosPago.getLogin() : ""),
								 (ab_datosProveedor ? proveedorDatosPago.getTranKey() : "")).trim());
					}
				}
				
				pedidoTarjeta.setConfirmado(true);
				pedidoTarjeta.setFechaconfirma(new Date());
				pedidoTarjetaDao.modificar(pedidoTarjeta);
				
				if(li_estatoPago.getLi_validacionConsulta() == 2)
				{
					pedido.setEstado(PedidosReservas.ELIMINADO);
					pedido.setEnproceso(false);
					//pedido.setFechaConfirmacion(new Date());
					reservasDao.modificar(pedido);
					
					String speech = String.format("%s, tu reserva de %s, no se pudo procesar ya que la tarjeta no procesó el pago :( ", pedido.getUsuario().getNombreFacebook(), pedido.getRestaurante().getNombre());

					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(speech)),
							propiedadesLola.facebookToken);
					ab_listo = true;
					return;
				}
			

		
			final Usuario usuario = pedido.getUsuario();
			
			mostrarAccionDeTipeo(usuario.getIdFacebook());

			String speech = "";
			
			ab_listo = true;
			urlExterna = "";
			
			//pedido.setFechaConfirmacion(new Date());
			pedido.setEnproceso(false);
			pedido.setAutorizacionpago(li_estatoPago.getLs_autorizacion());
			pedido.setEstado(PedidosReservas.PAGADO);
			pedido.setConfirmadoUsuario(true);
			reservasDao.modificar(pedido);
			
		
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
	        String ls_fecha = "";

	        try {

 
	        	ls_fecha = formatter.format(pedido.getFechaReserva());

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
			
			int valorRetornoReserva= reservasProcesos.consultaReservaRestaurante(pedido.getRestaurante().getIdRestaurante().intValue(), ls_fecha, pedido.getHoraReserva(), pedido.getNumeroPersonas(), pedido.getEmailReserva(), pedido.getNombresReserva(), pedido.getApellidosReserva(), pedido.getTelefonoReserva(), pedido.getNota());
	    	
			if (valorRetornoReserva > 0) 
			{
				 ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new TextMessage("Listo "+usuario.getNombreFacebook()+", se encuentra registrada tu reserva ;) ")),
		  					propiedadesLola.facebookToken);
					
	    	} else {
	    		
	    		pedido.setEnproceso(false);
    			
    			pedido.setEstado(PedidosReservas.CANCELADO);
    			pedido.setConfirmadoUsuario(true);
    			reservasDao.modificar(pedido);
	    		
	    		if(listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0)
	    		{
	    			ProveedorDatosPago proveedorDatosPagoTemp = null;
	    			
	    			try
	    			{
	    				proveedorDatosPagoTemp = proveedorDatosPagoDao.porProveedorPlaceToPay(proveedor);
	    			}
	    			catch(Exception err)
	    			{
	    				proveedorDatosPagoTemp = null;
	    			}
	    			
	    			boolean ab_datosProveedorTemp = proveedorDatosPagoTemp != null && proveedorDatosPagoTemp.getLogin() != null && proveedorDatosPagoTemp.getTranKey() != null && proveedorDatosPagoTemp.getLogin().trim().length() > 0 && proveedorDatosPagoTemp.getTranKey().trim().length() > 0;
	    			
	    			
	    			placeToPayResponse = GSON.fromJson(listaPedidoTarjeta.get(0).getRetorno(), PlaceToPayResponse.class);
	    			PlaceToPayRetornoReverso placeToPayRetornoReverso = PlaceToPay.reversarCompra(String.valueOf(placeToPayResponse.getRequestId()),
	    																						 (ab_datosProveedor ? proveedorDatosPago.getLogin() : ""),
	    																						 (ab_datosProveedor ? proveedorDatosPago.getTranKey() : "")
	    																						  );
	    			
	    			String message = "";
	    			
	    			if(placeToPayRetornoReverso.isSeReverso())
	    			{
	    				message = String.format("%s, %s referente a la reserva "+pedido.getId()+" de "+pedido.getRestaurante().getNombre()+", porque el proveedor no procesó la reserva.", pedido.getUsuario().getNombreFacebook(), placeToPayRetornoReverso.getMensajeUsuario());
	    			}
	    			else
	    			{
	    				if(placeToPayRetornoReverso.getMensajeUsuario() != null && placeToPayRetornoReverso.getMensajeUsuario().trim().length() > 0)
	    					message = String.format("%s, se reversó tu pago para la reserva "+pedido.getId()+" de "+pedido.getRestaurante().getNombre()+" porque el proveedor no procesó la reserva, tu institución financiera me indica que el reverso fue: %s.", pedido.getUsuario().getNombreFacebook(), placeToPayRetornoReverso.getMensajeUsuario());
	    				else
	    					message = String.format("%s, el proveedor no procesó la reserva y no se pudo reversar el pago de tu tarjeta.", pedido.getUsuario().getNombreFacebook());
	    			}
	    			pedidoTarjeta = listaPedidoTarjeta.get(0);
	    			pedidoTarjeta.setReversado(true);
	    			pedidoTarjeta.setRetornoreverso(GSON.toJson(placeToPayRetornoReverso).toString());
	    			pedidoTarjeta.setFechareverso(new Date());
	    			
	    			pedidoTarjetaDao.modificar(pedidoTarjeta);
	    			
	    			pedido.setEnproceso(false);
	    			
	    			pedido.setEstado(PedidosReservas.CANCELADO);
	    			pedido.setConfirmadoUsuario(true);
	    			reservasDao.modificar(pedido);
	    			
	    			ConsultarFacebook.postToFacebook(
	    					new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(message)),
	    					propiedadesLola.facebookToken
	    					);
	    			return;
	    		}
	    		
	    		
	    		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new TextMessage("Lo siento "+usuario.getNombreFacebook()+", no puede registrar tu reserva :( ")),
	  					propiedadesLola.facebookToken);
	    	}
			
		} catch (Exception err) {

			System.out.println("------------------------------------pedidoTarjeta------------ err--" + err);
			err.printStackTrace();
		}
	}

	
	public String getUrlExterna() {
		return urlExterna;
	}

	public void setUrlExterna(String urlExterna) {
		this.urlExterna = urlExterna;
	}

}