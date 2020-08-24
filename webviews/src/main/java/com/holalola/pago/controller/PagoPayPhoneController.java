package com.holalola.pago.controller;

import static com.holalola.util.TextoUtil.esVacio;
import static com.holalola.webhook.PayloadConstantes.PAYLOAD_CONFIRMAR_PEDIDO;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.Utilidades;
import com.holalola.comida.pedido.ejb.dao.PedidoTarjetaDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDatosPagoDao;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.PedidoTarjeta;
import com.holalola.comida.pedido.ejb.modelo.ProveedorDatosPago;
import com.holalola.ejb.pedidos.servicio.PedidoServicio;
import com.holalola.general.ejb.dao.UsuarioPayPhoneDao;
import com.holalola.general.ejb.dao.UsuarioPayPhoneDao.TarjetasPayPhone;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.pagos.PayPhone;
import com.holalola.pagos.PayPhone.DatosCompletosCompra;
import com.holalola.pagos.PayPhone.DatosCompraPay;
import com.holalola.pagos.PayPhone.DatosRetornoCompra;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


@ManagedBean
@SuppressWarnings("serial")
@ViewScoped
public class PagoPayPhoneController extends GeneralController {

	// private final static Gson GSON = new
	// GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	private static final String baseUrl = System.getProperty("lola.base.url");
	public static final String CONFIRMAR_NUMERO_PERSONAS = "CONFIRMAR_NUM_PERSONA ";

	int la_anio;
	int la_mes;
	boolean ab_puedePagar = true;
	boolean ab_verComboPagos = true;
	private String tarjeta;
	private String tarjetaEscrita;
	private String email;
	private String celular;
	private String ls_cvv;
	boolean ab_guardaTarjeta = true;
	boolean ab_verPago = true;
	boolean ab_ingresaTarjeta = true;
	boolean ab_procesandoPeticion = false;
	String ls_error = "";

	Pedido pedido;
	Map<String, String> tarjetas = new HashMap<String, String>();

	long pedidoId;

	// @EJB
	// PayPhone payPhone;

	@EJB
	PedidoTarjetaDao pedidoTarjetaDao;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private UsuarioPayPhoneDao usuarioPayPhoneDao;

	@EJB
	private ProveedorDatosPagoDao proveedorDatosPagoDao;

	@EJB
	private PedidoServicio pedidoServicio;

	List<TarjetasPayPhone> tarjetasPayPhone;

 
	private ProveedorDatosPago proveedorDatosPago;

	@PostConstruct
	private void inicializar() {
		
		//System.out.println("Entro inicializar el pago  PayPhone> ");
		
		ab_verPago = true;
		ls_error = "";
		try {

			 
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String token = params.get("token");
			String paramPedido = params.get("ids");
			String pa_anio = params.get("fa");
			String pa_mes = params.get("fm");
			String pa_dia = params.get("fd");
			String pa_hora = params.get("fh");
			String pa_minuto = params.get("fmi");
			String minExpiracion = params.get("me");
			Calendar cl_fechaUrl = Calendar.getInstance();
			try
			{
				 
				
				cl_fechaUrl.set(Integer.parseInt(pa_anio), 
								Integer.parseInt(pa_mes), 
							    Integer.parseInt(pa_dia), 
							    Integer.parseInt(pa_hora), 
							    Integer.parseInt(pa_minuto));
				
				Long minutos = diferenciaMinutos(cl_fechaUrl);
				
				if(minutos > Integer.parseInt(minExpiracion.trim()))
				{
					ls_error = "La solicitud del pedido ya caducó.";
					
					pedido = pedidoServicio.obtenerPorId(pedidoId);
					pedido.setEnproceso(false);
					pedidoServicio.modificar(pedido);
					
					ab_puedePagar = false;
					ab_verPago = false;
					return;
				}
				 
			}
			catch(Exception err)
			{
				System.out.println("Error al inicializar el pago  > "+err.getMessage());
				err.printStackTrace();
				ls_error = "La solicitud del pedido ya caducó.";
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}
			
			if (esVacio(token) || esVacio(paramPedido)) {
				Mensaje("El pedido no es válido, no se puede proceder (Info: 001).", false, true);
				ls_error = "El pedido no es válido, no se puede proceder (Info: 001).";
				System.out.println("El pedido no es válido, no se puede proceder (Info: 001).");
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			pedidoId = Long.parseLong(paramPedido);
			pedido = pedidoServicio.obtenerPorId(pedidoId);

			if (pedido == null || !token.equals(pedido.getToken())) {
				Mensaje("El pedido no es válido, no se puede proceder (Info: 002).", false, true);
				ls_error = "El pedido no es válido, no se puede proceder (Info: 002).";
				System.out.println("El pedido no es válido, no se puede proceder (Info: 002).");
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			if (pedido != null && !pedido.isEnproceso()) {
				Mensaje("El pedido ya fue procesado (Info: 003).", false, true);
				ls_error = "El pedido ya fue procesado (Info: 003).";
				System.out.println("El pedido ya fue procesado (Info: 003).");
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			proveedorDatosPago = proveedorDatosPagoDao.porProveedorPayPhone(pedido.getProveedor());

			if (proveedorDatosPago == null) {
				Mensaje("Disculpa " + pedido.getProveedor().getNombre().trim()
						+ " no poseen autorización para el uso de este medio de pago (Info: 004).", false, true);
				ls_error = "Disculpa " + pedido.getProveedor().getNombre().trim()
						+ " no poseen autorización para el uso de este medio de pago (Info: 004).";
				System.out.println("Disculpa " + pedido.getProveedor().getNombre().trim()
						+ " no poseen autorización para el uso de este medio de pago (Info: 004).");
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			try {
				tarjetasPayPhone = usuarioPayPhoneDao.dameTarjetas(pedido.getUsuario().getId());
				ab_verComboPagos = tarjetasPayPhone.size() > 0;

				for (TarjetasPayPhone tarjetaPgo : tarjetasPayPhone) {
					tarjetas.put(tarjetaPgo.getTarjetaEncriptada(), tarjetaPgo.getToken());
				}

			} catch (Exception err) {
				tarjetasPayPhone = null;
				ab_verComboPagos = false;
			}

			celular = pedido.getUsuario().getCelularPayphone();
			email = pedido.getUsuario().getEmail();

		} catch (Exception e) {
			ab_verPago = false;
			logger.info("\n--------\nError\n" + e.getLocalizedMessage() + " \n-- " + e.getMessage() + " \n-- "
					+ e.getCause() + " \n-- " + e.getClass() + " \n-- " + e.getStackTrace() + " \n-- "
					+ e.getSuppressed() + "\n-----------------\n");

			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reitenta por favor", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}
	
	private long diferenciaMinutos(Calendar fechaInicial){

		Calendar fechaFinal = Calendar.getInstance();
	    long diferenciaHoras=0;
	    diferenciaHoras=(fechaFinal.get(Calendar.MINUTE)-fechaInicial.get(Calendar.MINUTE));
	    return diferenciaHoras;
	}
	
	private String Desencriptar(String textoEncriptado) throws Exception {

        String secretKey = "||nuo||"; //llave para desenciptar datos
        String base64EncryptedString = "";

        try {
            byte[] message = Base64.getDecoder().decode(textoEncriptado);// .decodeBase64(textoEncriptado.getBytes("utf-8"));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digestOfPassword = md.digest(secretKey.getBytes("utf-8"));
            byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
            SecretKey key = new SecretKeySpec(keyBytes, "DESede");

            Cipher decipher = Cipher.getInstance("DESede");
            decipher.init(Cipher.DECRYPT_MODE, key);

            byte[] plainText = decipher.doFinal(message);

            base64EncryptedString = new String(plainText, "UTF-8");

        } catch (Exception ex) {
        }
        
        //System.out.println("Fecha  SC base64EncryptedString -    > "+ base64EncryptedString);
        
        return base64EncryptedString;
}

	private void Mensaje(String ls_mensaje, Boolean bl_warning, Boolean bl_error) {

		if (esVacio(ls_mensaje))
			return;

		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, ls_mensaje, "");
		if (bl_warning)
			success = new FacesMessage(FacesMessage.SEVERITY_WARN, ls_mensaje, "");
		else {
			if (bl_error)
				success = new FacesMessage(FacesMessage.SEVERITY_ERROR, ls_mensaje, "");
		}
		FacesContext.getCurrentInstance().addMessage(null, success);
	}

	public static String cifrarBase64(String as_mensaje) {
		try {
			Base64.Encoder encoder = Base64.getEncoder();
			String b = encoder.encodeToString(as_mensaje.getBytes(StandardCharsets.UTF_8));
			return b;
		} catch (Exception err) {
			System.out.println("Error al encriptar : " + as_mensaje);
			err.printStackTrace();
			throw err;
		}
	}
	
	public void dejarDeRecordar() {
		try {
			
			String ls_trajetaCompra = null;
			String ls_calidadorCVV = "";
			
			if (tarjeta != null && tarjeta.trim().length() > 0) {
				for (TarjetasPayPhone tarjetaPgo : tarjetasPayPhone) {
					if (tarjetaPgo.getToken().trim().equals(tarjeta.trim())) {
						ls_trajetaCompra = tarjetaPgo.getToken();
						ls_calidadorCVV = tarjetaPgo.getCsv();
					}
				}
			}
			
			
			try {
				
				tarjetasPayPhone = usuarioPayPhoneDao.eliminarTarjeta(pedido.getUsuario().getId(), ls_trajetaCompra, ls_calidadorCVV);
				ab_verComboPagos = tarjetasPayPhone.size() > 0;
				tarjetas.clear();
				for (TarjetasPayPhone tarjetaPgo : tarjetasPayPhone) {
					tarjetas.put(tarjetaPgo.getTarjetaEncriptada(), tarjetaPgo.getToken());
				}
				
				ab_ingresaTarjeta = true;
				
				Mensaje("Proceso realizado Exitosamente.", true, false);
				
			} catch (Exception err) {
				tarjetasPayPhone = null;
				ab_verComboPagos = false;
			}
		}
		catch(Exception err)
		{
			Mensaje("No pude inactivar tu tarjeta (Info: DR_001).", false, true);
			ls_error = "No pude inactivar tu tarjeta (Info: DR_001).";
		}
	}
 
	public void procesarPago() {
		try {

			String ls_trajetaCompra = null;
			String ls_calidadorCVV = "";

			if (ab_procesandoPeticion) {
				Mensaje("Por favor esperar, se esta procesado el pago.", false, true);
				return;
			}
			Calendar cal = Calendar.getInstance();

			if (tarjeta != null && tarjeta.trim().length() > 0) {
				for (TarjetasPayPhone tarjetaPgo : tarjetasPayPhone) {
					if (tarjetaPgo.getToken().trim().equals(tarjeta.trim())) {
						ls_trajetaCompra = tarjetaPgo.getToken();
						ls_calidadorCVV = tarjetaPgo.getCsv();
					}
				}
			}

			if (ls_calidadorCVV.trim().length() <= 0) {
				if (la_anio < cal.get(Calendar.YEAR)) {
					Mensaje("El año no es válido.", false, true);
					return;
				}

				if (la_mes < 1 || la_mes > 12) {
					Mensaje("El mes no es válido.", false, true);
					return;
				}
			}

			ls_trajetaCompra = ls_trajetaCompra == null
					? tarjeta != null && tarjeta.trim().length() > 0 ? tarjeta.trim() : tarjetaEscrita.trim()
					: ls_trajetaCompra;

			if (ls_trajetaCompra == null || ls_trajetaCompra.trim().length() <= 0) {
				Mensaje("La tarjeta no es válida, por favor ingresar una tarjeta válida.", false, true);
				return;
			}

			if (ls_calidadorCVV.trim().length() <= 0) {
				if (ls_cvv == null || ls_cvv.trim().length() != 3) {
					Mensaje("El código CVV no es válido.", false, true);
					return;
				}
			}

			if (email == null || email.trim().length() <= 0 || !Utilidades.validaEmail(email)) {
				Mensaje("El E-mail no es válido.", false, true);
				return;
			}

			if (celular != null && celular.trim().length() > 0 && celular.trim().length() < 8) {
				Mensaje("El celular no es válido.", false, true);
				return;
			}

			if (pedido.getTiempoEntrega() != 0) {
				Mensaje("El pedido ya fue procesado, no se puede continuar.", false, false);
				return;
			}

			List<PedidoTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
			PedidoTarjeta pedidoTarjeta = listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0
					? listaPedidoTarjeta.get(0)
					: null;

			if (pedidoTarjeta == null) {
				Mensaje("El pedido no está relacionado a un pago con tarjeta, no se puede continuar.", false, false);
				return;
			}

			if (pedidoTarjeta != null && pedidoTarjeta.getConfirmado() && !pedidoTarjeta.getReversado()
					&& pedido.getAutorizacionPago() != null && pedido.getAutorizacionPago().trim().length() > 0) {

				Mensaje(String.format("%s, este pedido ya fue pagado.", pedido.getUsuario().getNombreFacebook()), false,
						false);
				return;
			}

			ab_procesandoPeticion = true;

			BigDecimal lf_impuesto = pedido.getImpuestoAplicado();

			lf_impuesto = (new BigDecimal("1"))
					.add(lf_impuesto.divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP));

			BigDecimal lf_subtotal = pedido.getTotal().divide(lf_impuesto, 2, BigDecimal.ROUND_HALF_UP);

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();

			int valorIva = (int) ((pedido.getTotal().doubleValue() - (lf_subtotal).doubleValue()) * 100.00);
			int subTotal = (lf_subtotal.multiply(new BigDecimal(100))).toBigInteger().intValueExact();
			int total = (pedido.getTotal().multiply(new BigDecimal(100))).toBigInteger().intValueExact();
			
			//System.out.println("\n total "+total+"  subTotal "+subTotal+"  valorIva "+valorIva);

			if (total != (subTotal + valorIva)) {
				if (total > (subTotal + valorIva)) {
					 
					for (int i = 0; i < total; i++) {
						 
						if (total > (subTotal + valorIva)) {
							valorIva = valorIva + 1;
						} else
							break;
					}
				} else {
					System.out.println("\n Menor ");
					for (int i = 0; i < total; i++) {
						 
						if (total < (subTotal + valorIva)) {
							valorIva = valorIva - 1;
						} else
							break;
					}
				}
			}
			
			 
			
			Pedido pedidoTemp = pedidoServicio.obtenerPorId(pedidoId);
			
			if (pedidoTemp != null && (!pedido.isEnproceso() || pedidoTemp.getTiempoEntrega() != 0)) {
				Mensaje("El pedido ya fue procesado (Info: 003).", false, true);
				ls_error = "El pedido ya fue procesado (Info: 003).";
				System.out.println("El pedido ya fue procesado (Info: 003).");
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}


			DatosCompraPay datosCompraPay = new DatosCompraPay(ls_trajetaCompra, ls_calidadorCVV.trim(), total,
					subTotal, 0, valorIva, pedido.getId().toString(), "USD", email.trim(),
					(celular != null ? celular.trim() : "").toString(), proveedorDatosPago.getLogin());

			//System.out.println("Enviooo --  " + ow.writeValueAsString(datosCompraPay));

			DatosCompletosCompra datosCompletosCompra = new DatosCompletosCompra(
					(ls_calidadorCVV.trim().length() <= 0) ? cifrarBase64(ls_trajetaCompra) : "",
					(ls_calidadorCVV.trim().length() <= 0) ? cifrarBase64(String.valueOf(la_mes)) : "",
					(ls_calidadorCVV.trim().length() <= 0) ? cifrarBase64(String.valueOf(la_anio)) : "",
					(ls_calidadorCVV.trim().length() <= 0) ? cifrarBase64	(ls_cvv) : "",
					total,
					subTotal, 0,
					valorIva,
					pedido.getId().toString(), "USD", email.trim(), (celular != null ? celular.trim() : ""),
					proveedorDatosPago.getLogin());

			DatosRetornoCompra retornoPagoMV = (ls_calidadorCVV.trim().length() > 0
					? new PayPhone().procesarCompraPay(datosCompraPay, proveedorDatosPago.getTranKey())
					: new PayPhone().procesarCompraDatosCompletos(datosCompletosCompra,
							proveedorDatosPago.getTranKey()));

			String jsonEnvio = "";// ow.writeValueAsString(object);

			String ls_tarjetaParaGuardar = "";

			for (int i = 0; i < ls_trajetaCompra.length(); i++) {
				if (i <= 2 || (ls_trajetaCompra.length() - i) <= 3)
					ls_tarjetaParaGuardar = ls_tarjetaParaGuardar + ls_trajetaCompra.substring(i, i + 1);
				else
					ls_tarjetaParaGuardar = ls_tarjetaParaGuardar + "*";
			}

			datosCompraPay.setCardToken(ls_tarjetaParaGuardar);
			datosCompletosCompra.setNumber(ls_tarjetaParaGuardar);

			if (ls_calidadorCVV.trim().length() > 0)
				jsonEnvio = ow.writeValueAsString(datosCompraPay);
			else
				jsonEnvio = ow.writeValueAsString(datosCompletosCompra);

			if (retornoPagoMV != null && retornoPagoMV.getJson() != null && retornoPagoMV.getJson().trim().length() > 0
					&& retornoPagoMV.getError() == null) {

				ab_verPago = false;

				try {
					pedido.setFechaConfirmacion(new Date());
				} catch (Exception err) {

				}

				String jsonRetorno = ow.writeValueAsString(retornoPagoMV);

				pedidoTarjeta.setEnvio(jsonEnvio.trim() + " ||NUO|| "
						+ (pedidoTarjeta.getEnvio() != null ? pedidoTarjeta.getEnvio().trim() : ""));
				pedidoTarjeta.setRetorno(jsonRetorno.trim() + " ||NUO|| "
						+ (pedidoTarjeta.getRetorno() != null ? pedidoTarjeta.getRetorno().trim() : ""));
				pedidoTarjeta.setConfirmado(true);
				pedidoTarjeta.setFechaconfirma(new Date());
				pedidoTarjetaDao.modificar(pedidoTarjeta);

				// System.out.println("Validar la Insercion de la tarjeta "+
				// ls_calidadorCVV.trim().length());

				if (ls_calidadorCVV.trim().length() == 0) {
					usuarioPayPhoneDao.insertaActualizaTarjeta(pedido.getUsuario().getId(), ls_tarjetaParaGuardar,
							retornoPagoMV.getCardToken(), datosCompletosCompra.getVerificationCode());

					// System.out.println("Validar la Insercion de la tarjeta -- Se inserto");
				}

				final Usuario usuario = pedido.getUsuario();

				// mostrarAccionDeTipeo(usuario.getIdFacebook());

				String speech = String.format(
						"Listo %s, tu pago está confirmado, está bien que la factura salga a nombre de %s %s?",
						usuario.getNombreFacebook(), usuario.getNombres(), usuario.getApellidos());

				List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();

				pedido.setFechaConfirmacion(new Date());
				pedido.setEnproceso(false);
				pedido.setAutorizacionPago(retornoPagoMV.getAuthorizationCode());
				pedido.setTiempoEntrega(0);
				pedido.setConfirmadoUsuario(false);
				pedidoServicio.modificar(pedido);

				if (pedido.getProveedor().getPreguntarXUtencillos() == null
						|| !pedido.getProveedor().getPreguntarXUtencillos()) {

					// System.out.println("----------------------Vito-------------Sí, esos
					// datos----------" );

					buttons = Arrays.asList(
							new PostbackButton("Sí, esos datos", PAYLOAD_CONFIRMAR_PEDIDO + pedido.getId()),
							new WebUrlButton("No, otros datos",
									armarUrlDatosFacturacion(pedido.getId(), pedido.getToken()), true));

					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(usuario.getIdFacebook(), new ButtonRichMessage(speech, buttons)),
							propiedadesLola.facebookToken);

				} else {

					// System.out.println("----------------------Vito-------------Listo tu pago está
					// confirmado, por favor---------" );

					speech = String.format(
							"Listo %s tu pago está confirmado, por favor me indicas cuantas personas van a comer? Para enviarles servilletas, palillos, cubiertos, etc. Todo lo que necesitas para disfrutar tu plato ;) ",
							usuario.getNombreFacebook());

					List<QuickReplyGeneral> buttonsElemt = new ArrayList<QuickReplyGeneral>();

					for (int i = 1; i <= 10; i++) {
						buttonsElemt.add(new TextQuickReply(String.valueOf(i),
								CONFIRMAR_NUMERO_PERSONAS + pedido.getId() + " " + String.valueOf(i)));
					}

					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech, buttonsElemt)),
							propiedadesLola.facebookToken);

				}

				ls_error = "Se proceso el pago correctamente";

				Mensaje("Se proceso el pago correctamente", false, false);

				RequestContext.getCurrentInstance().update("bntSalir1");
				RequestContext.getCurrentInstance().update("msgs");
				RequestContext.getCurrentInstance().update("bntSalir");
				RequestContext.getCurrentInstance().update("pnGrid");
				RequestContext.getCurrentInstance().update("bntGuardar");

				RequestContext.getCurrentInstance().execute("closeView();");
				ab_procesandoPeticion = false;
				return;
			} else {

				String jsonRetorno = ow.writeValueAsString(retornoPagoMV.getError());

				pedidoTarjeta.setEnvio(jsonEnvio.trim() + " ||NUO|| "
						+ (pedidoTarjeta.getEnvio() != null ? pedidoTarjeta.getEnvio().trim() : ""));
				pedidoTarjeta.setRetorno(jsonRetorno.trim() + " ||NUO|| "
						+ (pedidoTarjeta.getRetorno() != null ? pedidoTarjeta.getRetorno().trim() : ""));
				pedidoTarjeta.setConfirmado(false);
				pedidoTarjeta.setFechaconfirma(new Date());
				pedidoTarjetaDao.modificar(pedidoTarjeta);

				ab_procesandoPeticion = false;
				Mensaje("No se puede realizar la compra, " + (retornoPagoMV != null && retornoPagoMV.getError() != null
						? " Me indican que: " + retornoPagoMV.getError().getMessage().trim()
						: "Los datos no son válidos.") + ".", false, true);
				return;
			}

		} catch (Exception e) {

			ab_procesandoPeticion = false;

			logger.info("\n--------\nError\n" + e.getMessage());

			e.printStackTrace();

			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}

	public String armarUrlDatosFacturacion(long pedidoId, String token) {

		// System.out.println("------------------------------------urlExterna----ab_listo----------"
		// + ab_listo);

		return baseUrl + "webviews/facebook/datosFactura.jsf?pedido=" + pedidoId + "&token=" + token;
	}

	public boolean isAb_guardaTarjeta() {
		return ab_guardaTarjeta;
	}

	public void setAb_guardaTarjeta(boolean ab_guardaTarjeta) {
		this.ab_guardaTarjeta = ab_guardaTarjeta;
	}

	public void getTarjetaChange() {

		tarjetaEscrita = tarjeta.trim();

		System.out.println("\n \n  onTarjetaChange  " + tarjetaEscrita);
	}

	public void onTarjetaChange() {

		tarjetaEscrita = tarjeta.trim();

		System.out.println("\n \n  onTarjetaChange  " + tarjetaEscrita);

		// ab_ingresaTarjeta = true;

		/*
		 * if (tarjeta != null && !tarjeta.equals("")) { //tarjetaEscrita = tarjeta;
		 * tarjetaEscrita = ""; ab_ingresaTarjeta = false;
		 * 
		 * 
		 * for (TarjetasPayPhone tarjetaPgo : tarjetasPayPhone) { if
		 * (tarjetaPgo.getMaskedCreditCardNumber().trim().equals(tarjeta.trim())) {
		 * //tarjetaEscrita = tarjetaPgo.getCreditCardNumber().substring(0,4); for(int i
		 * = 0; i < tarjetaPgo.getCreditCardNumber().trim().length(); i++) {
		 * tarjetaEscrita = tarjetaEscrita + (i < 4 || i >
		 * tarjetaPgo.getCreditCardNumber().trim().length() - 5 ?
		 * tarjetaPgo.getCreditCardNumber().trim().charAt(i) : "*"); }
		 * 
		 * ls_fechaExpiracion = tarjetaPgo.getExpirationDate();
		 * 
		 * ls_fechaExpiracion = ls_fechaExpiracion.substring(4) +
		 * "/"+ls_fechaExpiracion.substring(0, 4);
		 * 
		 * ls_cvv = null;
		 * 
		 * break; } }
		 * 
		 * } else { tarjetaEscrita = null; ls_fechaExpiracion = null; ls_cvv = null; }
		 */
	}

	public boolean isAb_ingresaTarjeta() {
		return ab_ingresaTarjeta;
	}

	public void setAb_ingresaTarjeta(boolean ab_ingresaTarjeta) {
		this.ab_ingresaTarjeta = ab_ingresaTarjeta;
	}

	public String getTarjetaEscrita() {
		return tarjetaEscrita;
	}

	public void setTarjetaEscrita(String tarjetaEscrita) {
		this.tarjetaEscrita = tarjetaEscrita;
	}

	public Map<String, String> getTarjetas() {
		return tarjetas;
	}

	public String getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(String tarjeta) {
		this.tarjeta = tarjeta;
		tarjetaEscrita = "";
		ab_ingresaTarjeta = (tarjeta == null || tarjeta.trim().length() <= 0);
		// System.out.println(" \n setTarjeta " + tarjeta);
	}

	public boolean isAb_verComboPagos() {
		return ab_verComboPagos;
	}

	public void setAb_verComboPagos(boolean ab_verComboPagos) {
		this.ab_verComboPagos = ab_verComboPagos;
	}

	public boolean isAb_puedePagar() {
		return ab_puedePagar;
	}

	public void setAb_puedePagar(boolean ab_puedePagar) {
		this.ab_puedePagar = ab_puedePagar;
	}

	public String getLs_cvv() {
		return ls_cvv;
	}

	public void setLs_cvv(String ls_cvv) {
		this.ls_cvv = ls_cvv;
	}

	public boolean isAb_verPago() {
		return ab_verPago;
	}

	public boolean isAb_noVerPago() {
		return !ab_verPago;
	}

	public void setAb_verPago(boolean ab_verPago) {
		this.ab_verPago = ab_verPago;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getLa_anio() {
		return la_anio;
	}

	public void setLa_anio(int la_anio) {
		this.la_anio = la_anio;
	}

	public int getLa_mes() {
		return la_mes;
	}

	public void setLa_mes(int la_mes) {
		this.la_mes = la_mes;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public String getLs_error() {
		return ls_error;
	}

	public void setLs_error(String ls_error) {
		this.ls_error = ls_error;
	}

}
