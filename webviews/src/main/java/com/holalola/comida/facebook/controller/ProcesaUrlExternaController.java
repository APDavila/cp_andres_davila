package com.holalola.comida.facebook.controller;

import static com.holalola.webhook.PayloadConstantes.PAYLOAD_CONFIRMAR_PEDIDO;

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
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoTarjetaDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDatosPagoDao;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.PedidoTarjeta;
import com.holalola.comida.pedido.ejb.modelo.ProveedorDatosPago;
import com.holalola.ejb.pedidos.servicio.PedidoServicio;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.pagos.PlaceToPay;
import com.holalola.pagos.PlaceToPay.PlaceToPayConsultaRetorno;
import com.holalola.pagos.entidades.PlaceToPayResponse;
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
public class ProcesaUrlExternaController extends GeneralController {

	private String urlExterna;

	private final static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
	public static final String CONFIRMAR_NUMERO_PERSONAS = "CONFIRMAR_NUM_PERSONA ";
	private static final String baseUrl = System.getProperty("lola.base.url");
	
	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	PedidoDao pedidoDao;

	@EJB
	PedidoTarjetaDao pedidoTarjetaDao;
	
	@EJB
	private PedidoServicio pedidoServicio;
	
	@EJB
	ProveedorDatosPagoDao proveedorDatosPagoDao;
	
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

		Pedido pedido = pedidoDao.obtenerPorId(idPedido);

		if (pedido == null || pedido.getTiempoEntrega() != 0) {
			return; // TODO: Mostrar mensaje de error
		}

		final List<PedidoTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
		if (listaPedidoTarjeta == null || listaPedidoTarjeta.size() <= 0)
			return; // TODO: Mostrar mensaje de error

		PedidoTarjeta pedidoTarjeta = listaPedidoTarjeta.get(0);

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

			Pedido pedido = pedidoServicio.obtenerPorId(idPedido);

			List<PedidoTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
			PedidoTarjeta pedidoTarjeta = listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0
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
			
			try
			{
				proveedorDatosPago = proveedorDatosPagoDao.porProveedorPlaceToPay(pedido.getProveedor());
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
					String speech = String.format("%s, tu pedido de %s, se encuentra en procesó, en cuanto tu Institución Financiera me confirme si el proceso se completo te lo informo.", pedido.getUsuario().getNombreFacebook(), pedido.getProveedor().getNombre());

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
						
						String message = String.format("%s, se reversó el pago del Pedido "+pedido.getId()+" de "+pedido.getProveedor().getNombre()+".", pedido.getUsuario().getNombreFacebook());
												
						pedidoTarjeta.setReversado(true);
						//pedidoTarjeta.setRetornoreverso(GSON.toJson(placeToPayRetornoReverso).toString());
						pedidoTarjeta.setFechareverso(new Date());
						
						pedidoTarjetaDao.modificar(pedidoTarjeta);
						
						pedido.setTiempoEntrega(Pedido.PERDIDO);
						pedido.setFechaConfirmacion(new Date());
						pedido.setEnproceso(false);
						pedido.setConfirmadoUsuario(true);
						pedidoServicio.modificar(pedido);
						
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
					pedido.setTiempoEntrega(Pedido.PERDIDO);
					pedido.setFechaConfirmacion(new Date());
					pedidoServicio.modificar(pedido);
					
					String speech = String.format("%s, tu pedido de %s, no se pudo procesar ya que la tarjeta no procesó el pago :( ", pedido.getUsuario().getNombreFacebook(), pedido.getProveedor().getNombre());

					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(speech)),
							propiedadesLola.facebookToken);
					ab_listo = true;
					return;
				}
			

		
			final Usuario usuario = pedido.getUsuario();
			
			mostrarAccionDeTipeo(usuario.getIdFacebook());

			String speech = String.format(
					"Listo %s, tu pago está confirmado, está bien que la factura salga a nombre de %s %s?",
					usuario.getNombreFacebook(), usuario.getNombres(), usuario.getApellidos());

			List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
			
			ab_listo = true;
			urlExterna = "";
			
			pedido.setFechaConfirmacion(new Date());
			pedido.setEnproceso(false);
			pedido.setAutorizacionPago(li_estatoPago.getLs_autorizacion());
			pedido.setTiempoEntrega(0);
			pedido.setConfirmadoUsuario(false);
			pedidoServicio.modificar(pedido);

			if (pedido.getProveedor().getPreguntarXUtencillos() == null
					|| !pedido.getProveedor().getPreguntarXUtencillos()) {
				
				//System.out.println("----------------------Vito-------------Sí, esos datos----------" );
				
				buttons = Arrays.asList(new PostbackButton("Sí, esos datos", PAYLOAD_CONFIRMAR_PEDIDO + pedido.getId()),
						new WebUrlButton("No, otros datos",
								armarUrlDatosFacturacion(pedido.getId(), pedido.getToken()), true));

				ConsultarFacebook.postToFacebook(
						new MensajeParaFacebook(usuario.getIdFacebook(), new ButtonRichMessage(speech, buttons)),
						propiedadesLola.facebookToken);

			} else {
	
				//System.out.println("----------------------Vito-------------Listo tu pago está confirmado, por favor---------" );
				
				speech = String.format(
						"Listo %s tu pago está confirmado, por favor me indicas cuantas personas van a comer? Para enviarles servilletas, palillos, cubiertos, etc. Todo lo que necesitas para disfrutar tu plato ;) ",
						usuario.getNombreFacebook());
				
				List<QuickReplyGeneral> buttonsElemt = new ArrayList<QuickReplyGeneral>();

				for (int i = 1; i <= 10; i++) {
					buttonsElemt.add(new TextQuickReply(String.valueOf(i),
							CONFIRMAR_NUMERO_PERSONAS + pedido.getId() + " " + String.valueOf(i)));
				}

		       ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new TextMessage(speech, buttonsElemt)),
					propiedadesLola.facebookToken);
				
				
			}

			// return Response.status(200).header("Content-Type", "text/csv;
			// charset=utf-8").entity("OK").build();
		} catch (Exception err) {

			System.out.println("------------------------------------pedidoTarjeta------------ err--" + err);
			err.printStackTrace();
			/*
			 * return Response.status(200).header("Content-Type", "text/csv; charset=utf-8")
			 * .entity("No se reconoce el pedido....").build();
			 */
		}
	}

	
	public String armarUrlDatosFacturacion(long pedidoId, String token) {
		
		//System.out.println("------------------------------------urlExterna----ab_listo----------" + ab_listo);
		
		return baseUrl + "webviews/facebook/datosFactura.jsf?pedido="+pedidoId+"&token="+token;
	}
	
	public String getUrlExterna() {
		return urlExterna;
	}

	public void setUrlExterna(String urlExterna) {
		this.urlExterna = urlExterna;
	}

}