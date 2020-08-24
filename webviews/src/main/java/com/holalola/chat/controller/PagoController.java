package com.holalola.chat.controller;

import static com.holalola.util.TextoUtil.esVacio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chilkatsoft.CkPublicKey;
import com.chilkatsoft.CkRsa;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.holalola.cine.ejb.dao.ComplejoCineDao;
import com.holalola.cine.ejb.modelo.ComplejoCine;
import com.holalola.cine.ejb.modelo.PedidoSupercines;
import com.holalola.cine.ejb.modelo.Pelicula;
import com.holalola.cine.ejb.servicio.ChilkatServicio;
import com.holalola.cine.ejb.servicio.ProveedorServicio;
import com.holalola.cine.ejb.servicio.SupercinesServicio;
import com.holalola.cine.ejb.servicio.UsuarioLoginServicio;
import com.holalola.comida.pedido.ejb.dao.PedidoTarjetaDao;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.ejb.pedidos.servicio.PedidoServicio;
import com.holalola.general.ejb.modelo.UsuarioLogin;
import com.holalola.supercines.client.vo.EnvioPagoMV;
import com.holalola.supercines.client.vo.ReservaPuestoSupercinesMV;
import com.holalola.supercines.client.vo.RetornoPagoMV;
import com.holalola.supercines.client.vo.TarjetasMV;
import com.holalola.supercines.client.vo.TarjetasMV.TarjetasDetalleMV;
import com.holalola.supercines.cliente.mov.vo.UsuarioLoginMV;
import com.holalola.util.CodigoQR;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.response.message.Attachment;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;


@ManagedBean
@SuppressWarnings("serial")
@SessionScoped
public class PagoController extends GeneralController {

	private static final DateFormat fmtJavascript = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	
	public static final String CONFIRMAR_NUMERO_PERSONAS = "CONFIRMAR_NUM_PERSONA ";
	
	final private String pathImagenesQRSupercines = System.getProperty("lola.base.url") + "images/QR/Supercines/";
	

	String ls_fechaExpiracion;
	String ls_cvv;
	private PedidoSupercines pedido;
	private ReservaPuestoSupercinesMV reservaPuestoSupercinesMV;
	boolean ab_puedePagar = true;
	TarjetasMV tarjetasMV;
	private UsuarioLogin usuarioSesionActiva;
	private UsuarioLoginMV usrLoginJson;
	boolean ab_verComboPagos = true;
	private String tarjeta;
	private String tarjetaEscrita;
	private String ciDuenioTarjeta;
	private String duenioTarjeta;
	private String email;
	boolean ab_guardaTarjeta = true;
	boolean ab_verPago = true;
	boolean ab_ingresaTarjeta = true;
	boolean ab_procesandoPeticion = false;

	Map<String, String> tarjetas = new HashMap<String, String>();
	
	long pedidoId;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private SupercinesServicio supercinesServicio;

	@EJB
	private UsuarioLoginServicio usuarioLoginServicio;

	@EJB
	private ProveedorServicio proveedorServicio;

	@EJB
	private ComplejoCineDao complejoCineDao;
	
	@EJB
	private PedidoServicio pedidoServicio;
	
	@EJB
	private PedidoTarjetaDao pedidoTarjetaDao;

	@PostConstruct
	public void inicializar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		ab_verPago = true;
		try {

			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String token = params.get("token");
			String paramPedido = params.get("ids");

			if (esVacio(token) || esVacio(paramPedido)) {
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			pedidoId = Long.parseLong(paramPedido);
			pedido = supercinesServicio.obtenerPedidoCompleto(pedidoId);

			if (pedido == null || !token.equals(pedido.getToken())) {
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			reservaPuestoSupercinesMV = GSON.fromJson(pedido.getUrlPago(), new TypeToken<ReservaPuestoSupercinesMV>() {
			}.getType());

			if (reservaPuestoSupercinesMV == null || reservaPuestoSupercinesMV.getSeats() == null
					|| reservaPuestoSupercinesMV.getSeats().size() <= 0) {
				ab_puedePagar = false;
				ab_verPago = false;
				return;
			}

			Proveedor proveedor = proveedorServicio.obtenerPorId((long) 4);

			usuarioSesionActiva = usuarioLoginServicio.obtenerSesionActivaUsuario(pedido.getUsuario(), proveedor);

			if (usuarioSesionActiva == null) {
				ab_verPago = false;
				return;
			}

			try {
				usrLoginJson = GSON.fromJson(usuarioSesionActiva.getJson(), UsuarioLoginMV.class);
				
				ciDuenioTarjeta = usrLoginJson.getContent().getCedula();
				duenioTarjeta = usrLoginJson.getContent().getNombres();
				email = usrLoginJson.getContent().getEmail();
						
			} catch (Exception err) {
				err.printStackTrace();
			}

			try {
				tarjetasMV = supercinesServicio.recuperaTarjetas(usrLoginJson.getContent().getCedula());
				ab_verComboPagos = tarjetasMV.getContent().size() > 0;

				for (TarjetasDetalleMV tarjetaPgo : tarjetasMV.getContent()) {
					tarjetas.put(tarjetaPgo.getMaskedCreditCardNumber(), tarjetaPgo.getMaskedCreditCardNumber());
				}

			} catch (Exception err) {
				tarjetasMV = null;
				ab_verComboPagos = false;
			}

			// ftarjetas.put("Otra", "Otra");

		} catch (Exception e) {
			ab_verPago = false;
			logger.info("\n--------\nError\n" + e.getLocalizedMessage() + " \n-- " + e.getMessage() + " \n-- "
					+ e.getCause() + " \n-- " + e.getClass() + " \n-- " + e.getStackTrace() + " \n-- "
					+ e.getSuppressed() + "\n-----------------\n");
			supercinesServicio.crearLogExc("Pago-inicializar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
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

	public String Encriptar(String as_cadena, String as_keyPem) {

		Date fechaInicioProceso = Calendar.getInstance().getTime();

		try {

			CkPublicKey piblicKey = new CkPublicKey();

			boolean success = piblicKey.LoadOpenSslPemFile(propiedadesLola.certificados + "\\" + as_keyPem);// "\\login_and_register_public_key.pem");;

			if (success != true) {
				System.out.println(piblicKey.lastErrorText());
				return "";
			}

			CkRsa rsa = new CkRsa();

			success = rsa.UnlockComponent(ChilkatServicio.getIdAcceso());
			if (success != true) {
				System.out.println(rsa.lastErrorText());
				return "";
			}

			String publicKeyXml = piblicKey.getXml();
			success = rsa.ImportPrivateKey(publicKeyXml);
			if (success != true) {
				System.out.println(rsa.lastErrorText());
				return "";
			}

			rsa.put_EncodingMode("hex");

			boolean usePrivateKey = false;
			String encryptedStr = rsa.encryptStringENC(as_cadena, usePrivateKey);

			// System.out.println(encryptedStr);

			return encryptedStr;

		} catch (Exception e) {
			supercinesServicio.crearLogExc("Login-Encriptar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			logger.info("\n--------ERROR - - NUO \n " + e + " \n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
		return "";
	}

	@SuppressWarnings("deprecation")
	public void procesarPago() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {

			String ls_trajetaCompra = null;
			
			if(ab_procesandoPeticion) {
				Mensaje("Por favor esperar, se esta procesado el pago.", false, true);
				return;
			}
			
			if (ciDuenioTarjeta == null || ciDuenioTarjeta.trim().length() != 10 || !Utilidades.validaCedula(ciDuenioTarjeta)) {
				Mensaje("La identificación no es válida.", false, true);
				return;
			}
			
			if (duenioTarjeta == null || duenioTarjeta.trim().length() <= 6 ) {
				Mensaje("El Titular de la Tarjeta no es válida.", false, true);
				return;
			}

			if (tarjeta != null && tarjeta.trim().length() > 0) {
				for (TarjetasDetalleMV tarjetaPgo : tarjetasMV.getContent()) {
					if (tarjetaPgo.getMaskedCreditCardNumber().trim().equals(tarjeta.trim()))
					{
					   ls_trajetaCompra = tarjetaPgo.getCreditCardNumber();
					}
				}
			}

			ls_trajetaCompra = ls_trajetaCompra == null
					? tarjeta != null && tarjeta.trim().length() > 0 ? tarjeta.trim() : tarjetaEscrita.trim()
					: ls_trajetaCompra;

			if (ls_trajetaCompra == null || ls_trajetaCompra.trim().length() <= 0) {
				Mensaje("La tarjeta no es válida, por favor ingresar una tarjeta válida.", false, true);
				return;
			}

			if (ls_cvv == null || ls_cvv.trim().length() != 3) {
				Mensaje("El código CVV no es válido.", false, true);
				return;
			}

			if (ls_fechaExpiracion == null || ls_fechaExpiracion.trim().length() != 7) {
				Mensaje("La fecha de expiración no es válida.", false, true);
				return;
			}

			try {
				if (Integer.parseInt(ls_fechaExpiracion.split("/")[0]) > 12
						|| Integer.parseInt(ls_fechaExpiracion.split("/")[0]) <= 0
						|| Integer.parseInt(ls_fechaExpiracion.split("/")[1]) < (new Date()).getYear()) {
					Mensaje("La fecha de expiración no es válida.", false, true);
					return;
				}
			} catch (Exception err) {
				Mensaje("La fecha de expiración no es válida.", false, true);
				return;
			}
			
			if (email == null || email.trim().length() <= 0 || !Utilidades.validaEmail(email) ) {
				Mensaje("El E-mail no es válido.", false, true);
				return;
			}

			String ls_datosProceso = "{ \r\n" + "\"CI\": \"" + usrLoginJson.getContent().getCedula() + "\", "
					+ "\"NameTcredCard\": \"" + usrLoginJson.getContent().getNombres() + "\", " + "\"CreditCard\": \""
					+ ls_trajetaCompra.trim() + "\", " + "\"ExpDate\": \"" + ls_fechaExpiracion.split("/")[1].trim()
					+ ls_fechaExpiracion.split("/")[0].trim() + "\", " + "\"CVV\": \"" + ls_cvv.trim() + "\", "
					+ "\"EmailCC\": \"" + usrLoginJson.getContent().getEmail() + "\" " + "}";

			/*System.out.println(
					"-------------------------------------ls_datosProceso---------------------------------------");
			System.out.println(ls_datosProceso);
			System.out.println(
					"-------------------------------------ls_datosProceso---------------------------------------");
					*/

			ab_procesandoPeticion = true;
			Pelicula pelicula = supercinesServicio.obtenerPeliculaPorMovieToken(pedido.getMovieToken());
			List<ComplejoCine> listaComplejoCine = complejoCineDao.obtenerPorSupercinesId(pedido.getComplejoId());
			ComplejoCine complejoCine = listaComplejoCine.get(0);

			EnvioPagoMV paramReservaPuestos = new EnvioPagoMV();

			paramReservaPuestos.setAdditionalCI(ciDuenioTarjeta);
			paramReservaPuestos.setClientName(duenioTarjeta);
			paramReservaPuestos.setDuration(pelicula.getDuracion());
			paramReservaPuestos.setEmailClient(email);
			paramReservaPuestos.setMovieImageUrl(pelicula.getUrlImagen());
			paramReservaPuestos.setMovilVersion("Hola Lola");
			paramReservaPuestos.setPlace(complejoCine.getNombre());
			paramReservaPuestos.setRetireCI(
					pedido.getCedulapersonaRetiraEntrada() != null ? pedido.getCedulapersonaRetiraEntrada().trim()
							: "");
			paramReservaPuestos.setRetireName(
					pedido.getPersonaRetiraEntrada() != null ? pedido.getPersonaRetiraEntrada().trim() : "");
			paramReservaPuestos.setSaveDebitCreditCard(ab_guardaTarjeta);
			paramReservaPuestos.setScreen(pedido.getSala());
			paramReservaPuestos.setUserSessionId(reservaPuestoSupercinesMV.getUserSessionId());
			paramReservaPuestos.setData(Encriptar(ls_datosProceso, ChilkatServicio.getPemPublicAPP()));

			RetornoPagoMV retornoPagoMV = supercinesServicio.procesaPago(paramReservaPuestos);

			if (retornoPagoMV != null && retornoPagoMV.getContent() != null
					&& retornoPagoMV.getContent().getResponse() != null && retornoPagoMV.getContent().getResponse()){
				
				ab_verPago = false;
				
				try
				{
					pedido.setFechaConfirmacion(new Date());
				}
				catch(Exception err)
				{
					
				}
				pedido.setEstado(1);
				pedido.setUrlPago(pedido.getUrlPago() + "  |NUO|  " + GSON.toJson(retornoPagoMV));
				supercinesServicio.guardarPedido(pedido);
				
				CodigoQR codigoQR = new CodigoQR();
				
				/*final Date hoy = new Date();
								
				SimpleDateFormat dt1 = new SimpleDateFormat("dd - MMMM - yyyy  HH:mm");
				SimpleDateFormat dt2 = new SimpleDateFormat("ddMMyyyyHHmmss");
				*/
				
				String ls_nombreArchivo = pedido.getUsuario().getId().toString();//.trim()+dt2.format(hoy).trim();
				
				codigoQR.GeneraCodigoQRSuperCines(retornoPagoMV.getContent().getMessage().trim(), ls_nombreArchivo);
				
				SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");

				String speech = String.format("Muy bien %s, nos han confirmado la compra de los puestos "
						+ pedido.getPuestosSeleccionados().trim() + ", con un valor de " + pedido.getValorTotal()
						+ ", que disfrutes tu película \"" + pelicula.getNombre() + "\" el " + formateador.format(pedido.getFechaPelicula())
						+ " en " + complejoCine.getNombre() + " " + pedido.getSala() + " a las " + pedido.getHorario()+", te adjunto el codigo de acceso ;)",
						pedido.getUsuario().getNombreFacebook());

				ConsultarFacebook.postToFacebook(
						new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(speech)),
						propiedadesLola.facebookToken);
				
				ConsultarFacebook.postToFacebook(new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(),new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.IMAGE, new MediaPayload(pathImagenesQRSupercines.trim()+ls_nombreArchivo.trim()+".png")))),
						propiedadesLola.facebookToken);
				
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
				ab_procesandoPeticion = false;
				Mensaje("Los Datos no son válidos, no se puede realizar la compra" + (retornoPagoMV != null
						&& retornoPagoMV.getContent() != null && retornoPagoMV.getContent().getMessage() != null
								? " Me indican que: " + retornoPagoMV.getContent().getMessage()
								: "")
						+ ".", false, true);
				return;
			}

		} catch (Exception e) {
			
			ab_procesandoPeticion = false;
			
			logger.info("\n--------\nError\n" + e.getMessage());

			e.printStackTrace();

			supercinesServicio.crearLogExc("Pago-procesarPago", fechaInicioProceso, "",
					Calendar.getInstance().getTime(), e, true);

			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}

	public boolean isAb_guardaTarjeta() {
		return ab_guardaTarjeta;
	}

	public void setAb_guardaTarjeta(boolean ab_guardaTarjeta) {
		this.ab_guardaTarjeta = ab_guardaTarjeta;
	}

	public void onTarjetaChange() {
		ab_ingresaTarjeta = true;

		if (tarjeta != null && !tarjeta.equals("")) {
			//tarjetaEscrita = tarjeta;
			tarjetaEscrita = "";
			ab_ingresaTarjeta = false;

			for (TarjetasDetalleMV tarjetaPgo : tarjetasMV.getContent()) {
				if (tarjetaPgo.getMaskedCreditCardNumber().trim().equals(tarjeta.trim()))
				{
					//tarjetaEscrita = tarjetaPgo.getCreditCardNumber().substring(0,4);
					for(int i = 0; i < tarjetaPgo.getCreditCardNumber().trim().length(); i++)
					{
						tarjetaEscrita = tarjetaEscrita + (i < 4 || i > tarjetaPgo.getCreditCardNumber().trim().length() - 5 ? tarjetaPgo.getCreditCardNumber().trim().charAt(i) : "*");
					}
					
					ls_fechaExpiracion = tarjetaPgo.getExpirationDate();
					
					ls_fechaExpiracion = ls_fechaExpiracion.substring(4) + "/"+ls_fechaExpiracion.substring(0, 4);
					
					ls_cvv = null;
					
					break;
				}
			}

		} else {
			tarjetaEscrita = null;
			ls_fechaExpiracion = null;
			ls_cvv = null;
		}
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

	public String getLs_fechaExpiracion() {
		return ls_fechaExpiracion;
	}

	public void setLs_fechaExpiracion(String ls_fechaExpiracion) {
		this.ls_fechaExpiracion = ls_fechaExpiracion;
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

	public String getCiDuenioTarjeta() {
		return ciDuenioTarjeta;
	}

	public void setCiDuenioTarjeta(String ciDuenioTarjeta) {
		this.ciDuenioTarjeta = ciDuenioTarjeta;
	}

	public String getDuenioTarjeta() {
		return duenioTarjeta;
	}

	public void setDuenioTarjeta(String duenioTarjeta) {
		this.duenioTarjeta = duenioTarjeta;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
