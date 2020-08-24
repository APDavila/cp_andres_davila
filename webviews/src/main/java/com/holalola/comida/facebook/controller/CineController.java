package com.holalola.comida.facebook.controller;

import static com.holalola.util.TextoUtil.esVacio;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chilkatsoft.CkPublicKey;
import com.chilkatsoft.CkRsa;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.holalola.chat.controller.Utilidades;
import com.holalola.cine.ejb.modelo.ComplejoCine;
import com.holalola.cine.ejb.modelo.DetallePedidoSupercines;
import com.holalola.cine.ejb.modelo.PedidoSupercines;
import com.holalola.cine.ejb.modelo.Pelicula;
import com.holalola.cine.ejb.servicio.ChilkatServicio;
import com.holalola.cine.ejb.servicio.ProveedorServicio;
import com.holalola.cine.ejb.servicio.SupercinesServicio;
import com.holalola.cine.ejb.servicio.UsuarioLoginServicio;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.modelo.UsuarioLogin;
import com.holalola.supercines.client.SupercinesApi;
import com.holalola.supercines.client.vo.DisposicionMV.Disposicion;
import com.holalola.supercines.client.vo.PuestoMV;
import com.holalola.supercines.client.vo.ReservaPuestoSupercinesMV;
import com.holalola.supercines.client.vo.RespuestaCancelacionMV;
import com.holalola.supercines.client.vo.RespuestaReservaPuestoMV;
import com.holalola.supercines.client.web.vo.DisposicionSalaSueprcines;
import com.holalola.supercines.cliente.mov.vo.UsuarioLoginMV;
import com.holalola.util.FechaUtil;
import com.holalola.util.PedidoUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.WebUrlButton;

@ViewScoped
@ManagedBean
@SuppressWarnings("serial")
public class CineController extends GeneralController {

	@EJB
	private SupercinesServicio supercinesServicio;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private UsuarioLoginServicio usuarioLoginServicio;

	@EJB
	private ProveedorServicio proveedorServicio;

	private static final DateFormat fmtJavascript = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static Gson GSON = new Gson();
	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);

	private static int minutosEspera = 5;

	private PedidoSupercines pedido;
	private DisposicionSalaSueprcines disposicionSala;

	private boolean puedeSeleccionarPuestos;
	private boolean aceptarTerminos;
	private boolean usuarioRetiraEntradas = true;
	private boolean usuarioNoRetiraEntradas = false;
	private String quienRetira;
	private String CIQuienRetira;
	private UsuarioLoginMV usrLoginJson;
	private String mensaje;
	private String estiloMensaje;
	private String fechaFin;
	private String informacionPelicula;
	private Integer numeroPuestosSolicitados;
	private BigDecimal valorPuestosSolicitados = BigDecimal.ZERO;
	private boolean conFbExtensions;
	private UsuarioLogin usuarioSesionActiva;

	private int deviceWidth;
	private int minHeight;

	@PostConstruct
	public void inicializar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {
			Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
					.getRequestParameterMap();
			String token = params.get("token");
			String paramPedido = params.get("pedido");
			String ext = params.get("ext");

			// conFbExtensions = !esVacio(ext) && Integer.parseInt(ext) == 1;
			conFbExtensions = true;
			mensaje = "Este link ya no es válido";
			estiloMensaje = "error";
			
			minutosEspera = supercinesServicio.minutosCancelacionPelicula();
			

			if (esVacio(token) || esVacio(paramPedido))
			{
				return;
			}

			long pedidoId = Long.parseLong(paramPedido);
			pedido = supercinesServicio.obtenerPedidoCompleto(pedidoId);

			if (pedido == null || !token.equals(pedido.getToken()))
				return;

			Proveedor proveedor = proveedorServicio.obtenerPorId((long) 4);
			
			usuarioSesionActiva = usuarioLoginServicio.obtenerUltimaSesionActivaPorIdUsuario(pedido.getUsuario(),
					proveedor);

			if (usuarioSesionActiva == null)
			{
				return;
			}

			usrLoginJson = GSON.fromJson(usuarioSesionActiva.getJson().trim(), UsuarioLoginMV.class);

			pedido.setPuestosSeleccionados("");
			
			if (pedido.getUserSessionId() != null && !"".equals(pedido.getUserSessionId().trim())) {
				
				String json = "";
				
				logger.info("\n--------\nIformación: Cancelación json long : "
						+ pedido.getUrlPago().split("\\|NUO\\|").length + " \n \n-----------------\n");
				
				if(pedido.getUrlPago().split("\\|NUO\\|").length > 0)
					json = pedido.getUrlPago().split("\\|NUO\\|")[0].trim(); 
				else
					json = pedido.getUrlPago().trim();
				
				try
				{
					if(json != null && json.trim().length() > 0)
					{
						logger.info("\n--------\nIformación: Cancelación json : "
								+ json + " \n \n-----------------\n");
						
						ReservaPuestoSupercinesMV reservaPuestoSupercinesMV = GSON.fromJson(json, ReservaPuestoSupercinesMV.class);
	
						if (!(reservaPuestoSupercinesMV == null || reservaPuestoSupercinesMV.getUserSessionId() == null || reservaPuestoSupercinesMV.getUserSessionId().trim().length() <= 0)) {
						
							RespuestaCancelacionMV retornoMessage = SupercinesApi.cancelarReserva(reservaPuestoSupercinesMV.getUserSessionId());
							
							if (retornoMessage != null && retornoMessage.getContent() != null && retornoMessage.getContent().getResponse())
								logger.info("\n--------\nIformación: Cancelación Realizada con Exito para : "
										+ pedido.getUserSessionId() + " \n \n-----------------\n");
							else
								logger.info("\n--------\nIformación: Cancelación Realizada Fallida para : "
										+ pedido.getUserSessionId() + " \n \n-----------------\n");
						}
					}
				}
				catch(Exception err)
				{
					logger.info("\n--------\nIformación: Cancelación Fallida ");
					err.printStackTrace();
				}
			}
			
			if (FechaUtil.isHanPasadoMinutos(minutosEspera, pedido.getFecha()))
			{
				return;
			}
			
			Date fechaInicioProcesoSala = Calendar.getInstance().getTime();
			
			disposicionSala = supercinesServicio.obtenerDisposicionSala(pedido, usrLoginJson.getContent().getEmail());// "juanfrugone@gmail.com");
			
			if (disposicionSala == null) {
				supercinesServicio.crearLog("Puesto-inicializar-DisposicionSala", fechaInicioProceso,
						"Pedido: " + pedido.getId().toString() + "; || Usuario: " + usrLoginJson.getContent().getEmail(),
						Calendar.getInstance().getTime(), "Servicio No devuelve Disposición de sala.", false);
				
				return;
			}

			supercinesServicio.crearLog("Puesto-DisposicionSala-Tiempos", fechaInicioProcesoSala,
					"IdPedido: " + pedido.getId() + " | IdComplejo: " + pedido.getComplejoId() + " | Username: " + usrLoginJson.getContent().getEmail(),
					Calendar.getInstance().getTime(),"Tiempos : " + disposicionSala.getTimepoProcesoURL() + " - " + disposicionSala.getTimepoProcesoLola(), false);

			pedido.setUserSessionId(disposicionSala.getUser_session_id());
			
			supercinesServicio.guardarPedido(pedido);

			numeroPuestosSolicitados = pedido.getDetallePedidoSupercines().stream()
					.mapToInt(DetallePedidoSupercines::getCantidad).sum();

			for (DetallePedidoSupercines value : pedido.getDetallePedidoSupercines()) {
				valorPuestosSolicitados = valorPuestosSolicitados.add(value.getValorTotal());
			}
			
			fechaFin = armarFechaFin(pedido);
			mensaje = null;

			armarInformacionPelicula();
			deviceWidth = 30 * (disposicionSala.getNumeroColumnas() + 1);
			minHeight = 30 * (disposicionSala.getNumeroFilas() + 5);
			puedeSeleccionarPuestos = true;

		} catch (Exception e) {
			logger.info("\n--------\nError\n" + e.getLocalizedMessage() + " \n-- " + e.getMessage() + " \n-- "
					+ e.getCause() + " \n-- " + e.getClass() + " \n-- " + e.getStackTrace() + " \n-- "
					+ e.getSuppressed() + "\n-----------------\n");
			supercinesServicio.crearLogExc("Puesto-inicializar", fechaInicioProceso, "",
					Calendar.getInstance().getTime(), e, true);
			
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}

	private void Mensaje(String ls_mensaje, Boolean bl_warning, Boolean bl_error) {
		
		if(esVacio(ls_mensaje))
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

	public void comprar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {

			int li_puestos = 0;
			try {
				li_puestos = pedido.getPuestosSeleccionados().split("\\|").length;
			} catch (Exception er) {
				li_puestos = 0;
			}

			if (numeroPuestosSolicitados > li_puestos) {
				Mensaje("Debes seleccionar " + numeroPuestosSolicitados + " pusto(s), te falta(n) "
						+ (numeroPuestosSolicitados - li_puestos) + " .", true, false);
				return;
			}

			String quienRetiraLasEntradas = "";
			if (usuarioRetiraEntradas) {
				if (esVacio(getNombreUsrLoginJson())) {
					Mensaje("Por favor ingresar el nombre de Quien va a retirar las entradas.", true, false);
					return;
				}
				quienRetiraLasEntradas = getNombreUsrLoginJson();
				if (usrLoginJson != null && usrLoginJson.getContent() != null)
					CIQuienRetira =  usrLoginJson.getContent().getCedula();
				
			} else {
				if (esVacio(quienRetira)) {
					Mensaje("Por favor ingresar el nombre de Quien va a retirar las entradas.", true, false);
					return;
				}
				
				if (esVacio(CIQuienRetira)) {
					Mensaje("Por favor ingresar la cédula de Quien va a retirar las entradas.", true, false);
					return;
				}
				
				if(!Utilidades.validaCedula(CIQuienRetira)){
					Mensaje("La cédula de Quien va a retirar las entradas no es válida.", true, false);
					return;
				}
				
				quienRetiraLasEntradas = quienRetira;
			}

			if (!aceptarTerminos) {
				mensaje = null;
				Mensaje("Por favor aceptar Términos y Condiciones.", true, false);
				// estiloMensaje = "warning";
				return;
			}

			if (!esVacio(pedido.getPuestosSeleccionados())) {

				pedido.setPersonaRetiraEntrada(quienRetiraLasEntradas);
				pedido.setCedulapersonaRetiraEntrada(CIQuienRetira);

				List<PuestoMV> seats = new ArrayList();
				
				//	
				//String cinemaId, String sessionId, List<PuestoMV> seats, String userSessionId
				
				//pedido.getCodigoFormato(),
				//String.valueOf(pedido.getHorarioId()),
				
				//String areaCode, String columnIndex, String rowIndex, String areaNumber
				
				String puestos = "";

				for (String puestoSeleccionado : pedido.getPuestosSeleccionados().split("\\|")) {
					for (Disposicion puesto : disposicionSala.getListaPuestos()) {
						if (puesto.getId().trim().equals(puestoSeleccionado.trim())) {
							
							PuestoMV reservaPuestoSupercines = new PuestoMV();
							
							reservaPuestoSupercines.setAreaCode(disposicionSala.getArea_category_code());
							reservaPuestoSupercines.setColumnIndex(String.valueOf(puesto.getC()));
							reservaPuestoSupercines.setRowIndex(String.valueOf(puesto.getR()));
							reservaPuestoSupercines.setAreaNumber(String.valueOf(disposicionSala.getNumeroArea()));
																
							seats.add(reservaPuestoSupercines);
							puestos = puestos + puesto.getId() + "|";
							break;
						}
					}
				}

				ReservaPuestoSupercinesMV paramReservaPuestos = new ReservaPuestoSupercinesMV(pedido.getCodigoFormato(), // external_identifier
						String.valueOf(pedido.getHorarioId()), // external_session_id,
						seats,
						disposicionSala.getUser_session_id()); // user_session_id,); 

				RespuestaReservaPuestoMV retornoMessage = supercinesServicio.reservarPuestos(paramReservaPuestos);

				if (retornoMessage != null && retornoMessage.getContent() != null && retornoMessage.getContent().getMessage() == null) {

					//String ls_data = Encriptar(CrearJsonParaEncriptar(), ChilkatServicio.getPemConsulta());

					//RetornoMessage retornoUrlPagos = supercinesServicio.urlPortalPagos(ls_data);
					
					pedido.setUrlPago( GSON.toJson(paramReservaPuestos));
					pedido.setPuestosSeleccionados(puestos);

					supercinesServicio.guardarPedido(pedido);
					
					try{
			            FacesContext contex = FacesContext.getCurrentInstance();
			            contex.getExternalContext().redirect(PedidoUtil.urlServidor().trim() + "webviews/cine/pago.jsf?ids=" + pedido.getId() + "&token="+pedido.getToken() );
					} catch (Exception e) {
						
						supercinesServicio.crearLogExc("CineControl-pago", fechaInicioProceso, "", 
								Calendar.getInstance().getTime(), e, true);
						
						Mensaje("No pude redireccionar", false, true);
					}

				} else {
					supercinesServicio.crearLog("Puesto-comprar", fechaInicioProceso, "",
							Calendar.getInstance().getTime(), retornoMessage.getContent().getMessage(), false);

					mensaje = retornoMessage.getContent().getMessage();
					Mensaje(mensaje, true, false);
					mensaje = null;
				}
			} else {
				mensaje = "Por favor selecciona por lo menos un puesto";
				Mensaje(mensaje, true, false);
				mensaje = null;
			}
		} catch (Exception e) {
			logger.info("\n--------\nError\n" + e + "\n-----------------\n");

			supercinesServicio.crearLogExc("Puesto-comprar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}

	private String CrearJsonParaEncriptar() {
		return "{\"comm_channel\": \"PAR_CHATHL\",\"external_identifier\": " + pedido.getComplejoId()
				+ ", \"user_session_id\": \"" + pedido.getUserSessionId() + "\", \"who_removed_tickets\": \""
				+ pedido.getPersonaRetiraEntrada() + "\" }";
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
			logger.info("\n--------ERROR - - NUO \n " + e + " \n-----------------\n");

			supercinesServicio.crearLogExc("Puesto-Encriptar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
		return "";
	}

	private void enviarMensajeAFacebook(String linkPago) {
		try {
			String speech = String.format(
					"Muy bien %s, para confirmar tus puestos %s, es necesario ejecutar el pago. Por favor haz clic en 'Procesar pago' para continuar",
					pedido.getUsuario().getNombreFacebook(), pedido.getPuestosSeleccionados());
			/*
			 * ButtonRichMessage buttonMessage = new ButtonRichMessage(speech,
			 * Arrays.asList(new PostbackButton("Procesar pago",
			 * PayloadConstantes.PAYLOAD_SELECCIONAR_FORMA_PAGO + pedido.getId())));
			 */

			ButtonRichMessage buttonMessage = new ButtonRichMessage(speech,
					Arrays.asList(new WebUrlButton("Procesar pago", linkPago)));
			ConsultarFacebook.postToFacebook(
					new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), buttonMessage),
					propiedadesLola.facebookToken);

		} catch (Exception e) {
			logger.info("\n--------ERROR - - NUO \n " + e + " \n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}

	private void enviarMensajeErrorAFacebook(String ls_link) {
		try {
			String speech = String.format("Disculpa %s, no me permiten procesar tu pago :(, " +
					"pero si gustas por favor ingresa al siguiente link para reintentar el pago \n" + ls_link,
					pedido.getUsuario().getNombreFacebook(), pedido.getPuestosSeleccionados());

			ConsultarFacebook.postToFacebook(
					new MensajeParaFacebook(pedido.getUsuario().getIdFacebook(), new TextMessage(speech)),
					propiedadesLola.facebookToken);

		} catch (Exception e) {
			logger.info("\n--------ERROR - - NUO \n " + e + " \n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}

	private String armarFechaFin(PedidoSupercines pedido) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(pedido.getFecha());
		calendar.add(Calendar.MINUTE, minutosEspera);

		return fmtJavascript.format(calendar.getTime());
	}

	private void armarInformacionPelicula() {

		final Pelicula pelicula = supercinesServicio.obtenerPeliculaPorMovieToken(pedido.getMovieToken());
		final ComplejoCine complejo = supercinesServicio.obtenerComplejoPorSupercinesId(pedido.getComplejoId());
		final DateFormat fmtFechaPelicula = new SimpleDateFormat("EEEEE dd 'de' MMMMM");
		// TODO: Obtener informacion de la pelicula
		informacionPelicula = String.format("%s %s en la Sala %s del complejo %s el %s a las %s", pelicula.getNombre(),
				pedido.getNombreFormato(), pedido.getSala(), complejo.getNombre(),
				fmtFechaPelicula.format(pedido.getFechaPelicula()), pedido.getHorario());
	}

	public BigDecimal getValorPuestosSolicitados() {
		return valorPuestosSolicitados;
	}

	public void setValorPuestosSolicitados(BigDecimal valorPuestosSolicitados) {
		this.valorPuestosSolicitados = valorPuestosSolicitados;
	}

	public DisposicionSalaSueprcines getDisposicionSala() {
		return disposicionSala;
	}

	public String getPuestosOcupados() {
		if (disposicionSala != null)
			return disposicionSala.getPuestosOcupados();
		return "";
	}

	public boolean isPuedeSeleccionarPuestos() {
		return puedeSeleccionarPuestos;
	}

	public String getMensaje() {
		return mensaje;
	}

	public String getFechaFin() {
		return fechaFin;
	}

	public boolean isMostrarMensajes() {
		return !esVacio(mensaje);
	}

	public String getEstiloMensaje() {
		return estiloMensaje;
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public String getInformacionPelicula() {
		return informacionPelicula;
	}

	public int getDeviceWidth() {
		return deviceWidth;
	}

	public PedidoSupercines getPedido() {
		return pedido;
	}

	public Integer getNumeroPuestosSolicitados() {
		return numeroPuestosSolicitados;
	}

	public int getMinHeight() {
		return minHeight;
	}

	public boolean isAceptarTerminos() {
		return aceptarTerminos;
	}

	public void setAceptarTerminos(boolean aceptarTerminos) {
		this.aceptarTerminos = aceptarTerminos;
	}

	public boolean isUsuarioRetiraEntradas() {
		return usuarioRetiraEntradas;
	}

	public boolean isUsuarioNoRetiraEntradas() {
		return usuarioNoRetiraEntradas;
	}

	public void setUsuarioNoRetiraEntradas(boolean usuarioNoRetiraEntradas) {
		usuarioRetiraEntradas = !usuarioNoRetiraEntradas;
		this.usuarioNoRetiraEntradas = usuarioNoRetiraEntradas;
	}

	public void setUsuarioRetiraEntradas(boolean usuarioRetiraEntradas) {
		usuarioNoRetiraEntradas = !usuarioRetiraEntradas;
		this.usuarioRetiraEntradas = usuarioRetiraEntradas;
	}

	public String getQuienRetira() {
		return quienRetira;
	}

	public void setQuienRetira(String quienRetira) {
		this.quienRetira = quienRetira;
	}

	public UsuarioLoginMV getUsrLoginJson() {
		return usrLoginJson;
	}

	/*public void setUsrLoginJson(ContentUsuarioLoginMV usrLoginJson) {
		this.usrLoginJson = usrLoginJson;
	}*/

	public String getNombreUsrLoginJson() {
		if (usrLoginJson != null && usrLoginJson.getContent() != null)
			return usrLoginJson.getContent().getNombres();
		return "-";
	}

	public String getCIQuienRetira() {
		return CIQuienRetira;
	}

	public void setCIQuienRetira(String cIQuienRetira) {
		CIQuienRetira = cIQuienRetira;
	}
	
	

}
