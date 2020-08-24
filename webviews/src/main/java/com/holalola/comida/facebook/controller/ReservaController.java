package com.holalola.comida.facebook.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.holalola.cine.ejb.modelo.ComplejoCine;
import com.holalola.cine.ejb.modelo.PedidoSupercines;
import com.holalola.cine.ejb.modelo.Pelicula;
import com.holalola.cine.ejb.servicio.SupercinesServicio;
import com.holalola.cine.ejb.servicio.UsuarioLoginServicio;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioLogin;
import com.holalola.supercines.client.SupercinesApi;
import com.holalola.supercines.client.vo.PrecioTicketMV;
import com.holalola.supercines.client.vo.PrecioTicketMV.DetallePrecioTicketMV;
import com.holalola.supercines.client.vo.TieneCBMV;
import com.holalola.supercines.client.vo.TipoTicket;
import com.holalola.supercines.cliente.mov.vo.UsuarioLoginMV;
import com.holalola.util.PedidoUtil;

@ManagedBean(name = "reservaController")
@ViewScoped
@SuppressWarnings("serial")
public class ReservaController extends GeneralController {

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private SupercinesServicio supercinesServicio;

	@EJB
	private UsuarioLoginServicio usuarioLoginServicio;

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public void setConFbExtensions(boolean conFbExtensions) {
		this.conFbExtensions = conFbExtensions;
	}

	public int getMaximoClubBeneficios() {
		return maximoClubBeneficios;
	}

	public void setMaximoClubBeneficios(int maximoClubBeneficios) {
		this.maximoClubBeneficios = maximoClubBeneficios;
	}

	public int getMaximoGeneral() {
		return maximoGeneral;
	}

	public void setMaximoGeneral(int maximoGeneral) {
		this.maximoGeneral = maximoGeneral;
	}

	public boolean getVerClubBeneficios() {
		return verClubBeneficios;
	}

	public void setVerClubBeneficios(boolean verClubBeneficios) {
		this.verClubBeneficios = verClubBeneficios;
	}

	public int getClubBeneficios() {
		return clubBeneficios;
	}

	public void setClubBeneficios(int clubBeneficios) {
		this.clubBeneficios = clubBeneficios;
	}

	public int getGeneral() {
		return general;
	}

	public void setGeneral(int general) {
		this.general = general;
	}

	public String getInformacionPelicula() {
		return informacionPelicula;
	}

	public void setInformacionPelicula(String informacionPelicula) {
		this.informacionPelicula = informacionPelicula;
	}

	public String getImagenPelicula() {
		return imagenPelicula;
	}

	public void setImagenPelicula(String imagenPelicula) {
		this.imagenPelicula = imagenPelicula;
	}

	public BigDecimal getPrecioCLB() {
		return precioCLB.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public void setPrecioCLB(BigDecimal precioCLB) {
		this.precioCLB = precioCLB;
	}

	public BigDecimal getPrecioGeneral() {
		return precioGeneral.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public void setPrecioGeneral(BigDecimal precioGeneral) {
		this.precioGeneral = precioGeneral;
	}

	public BigDecimal getPrecioTotalCLB() {
		return precioTotalCLB.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	public void setPrecioTotalCLB(BigDecimal precioTotalCLB) {
		this.precioTotalCLB = precioTotalCLB;
	}

	public BigDecimal getPrecioTotalGeneral() {
		return precioTotalGeneral;
	}

	public void setPrecioTotalGeneral(BigDecimal precioTotalGeneral) {
		this.precioTotalGeneral = precioTotalGeneral;
	}

	public boolean isVerPuestos() {
		return verPuestos;
	}

	public void setVerPuestos(boolean verPuestos) {
		this.verPuestos = verPuestos;
	}

	private PedidoSupercines pedido;

	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private static final DateFormat fmtFechaParam = new SimpleDateFormat("yyyy-MM-dd");
	private final String mesEspera = "Se esta obteniendo los asientos disponibles de su sala, por favor esperar...";

	//private List<TipoTicket> tiposTicket = null;
	private String informacionPelicula = "";
	private String imagenPelicula = "";

	private boolean verPuestos;
	private boolean conFbExtensions;
	private boolean verClubBeneficios;
	private int clubBeneficios = 0;
	private int maximoClubBeneficios = 0;
	private int maximoGeneral = 10;
	private int general = 0;
	private UsuarioLogin usuarioSesionActiva;
	private UsuarioLoginMV usrLoginJson;
	private BigDecimal precioCLB = BigDecimal.ZERO;
	private BigDecimal precioGeneral = BigDecimal.ZERO;

	private BigDecimal precioTotalCLB = BigDecimal.ZERO;
	private BigDecimal precioTotalGeneral = BigDecimal.ZERO;
	private Usuario usuario;
	private PrecioTicketMV peliculaTicket;
	
	Pelicula pelicula;

	private void Mensaje(String ls_mensaje, Boolean bl_warning, Boolean bl_error) {
		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, ls_mensaje, "");
		if (bl_warning)
			success = new FacesMessage(FacesMessage.SEVERITY_WARN, ls_mensaje, "");
		else {
			if (bl_error)
				success = new FacesMessage(FacesMessage.SEVERITY_ERROR, ls_mensaje, "");
		}
		FacesContext.getCurrentInstance().addMessage(null, success);
	}

	@PostConstruct
	public void inicializar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		try {

			String ls_idLoginServicio = params.get("ids");

			informacionPelicula = "No posee peliculas pendientes de Compra.";
			general = 0;
			clubBeneficios = 0;
			maximoClubBeneficios = 0;
			maximoGeneral = 10;

			if (ls_idLoginServicio == null || "".equals((ls_idLoginServicio.trim()))) {
				//logger.info(
				//		"\n--------\n Disculpa no puedo reconocer el servicio, por favor reingresa a la opción \n-----------------\n");
				Mensaje("Disculpa no puedo reconocer el servicio, por favor reingresa a la opción", false, true);
				return;
			}

			usuarioSesionActiva = usuarioLoginServicio.obtenerPorId(Long.parseLong(ls_idLoginServicio));

			usrLoginJson = GSON.fromJson(usuarioSesionActiva.getJson(), UsuarioLoginMV.class);

			TieneCBMV tieneCLB = SupercinesApi.validarClubBeneficios(usrLoginJson.getContent().getCedula());

			maximoClubBeneficios = 0;
			boolean poseeClubBeneficios = false;
			if (tieneCLB != null && tieneCLB.getContent().getIsMember()) { 
				maximoClubBeneficios = tieneCLB.getContent().getTicketsAvailable();
				poseeClubBeneficios = tieneCLB.getContent().getIsMember();
			}

			verClubBeneficios = maximoClubBeneficios > 0;

			usuario = usuarioSesionActiva.getUsuario();

			pedido = supercinesServicio.obtenerPedidoActivoUsuario(usuarioSesionActiva.getUsuario());

			if (pedido == null) {
				
				Mensaje("No tienes peliculas pendientes, por favor regresa al chat para seleccionar una pelicula...",
						false, false);
				return;
			}
			
			//Long idCinema, String sessionId, boolean perteneceAClubBeneficios, String as_area

			peliculaTicket = SupercinesApi.obtenerTiposTickets(pedido.getCodigoFormato().trim(), String.valueOf(pedido.getHorarioId()), poseeClubBeneficios, pedido.getTipoPuesto().trim());
			
			if (peliculaTicket == null) {

				supercinesServicio.crearLog("Login-Reserva", fechaInicioProceso,
						"FechaPelicula: " + pedido.getFechaPelicula() + " || ComplejoId: " + pedido.getComplejoId()
								+ " || CodigoFormato: " + pedido.getCodigoFormato() + " || HorarioId: "
								+ pedido.getHorarioId() + " || TipoPuesto: " + pedido.getTipoPuesto(),
						Calendar.getInstance().getTime(), "El servicio no devuelve Ticket.", false);

				//logger.info(
					//	"\n--------\n Disculpa no se puede acceder a Supercines, intenta mas tarde - peliculaTicket \n-----------------\n");
				Mensaje("Disculpa no se puede acceder a Supercines, por favor intenta mas tarde", false, true);
				return;
			}
			
			pelicula = supercinesServicio.obtenerPeliculaPorMovieToken(pedido.getMovieToken());

			imagenPelicula = pelicula.getUrl();
			
			/*if (peliculaTicket.getImage().size() > 0)
				imagenPelicula = peliculaTicket.getImage().get(0);*/

			/*tiposTicket = pedido.getTipoPuesto().equals("standard") ? peliculaTicket.getStantard_vermouth_seats()
					: peliculaTicket.getVip_vermouth_seats();*/

			if (peliculaTicket == null || peliculaTicket.getContent() == null || peliculaTicket.getContent().size() <= 0) {
				supercinesServicio.crearLog("Login-Reserva", fechaInicioProceso, "tiposTicket == null",
						Calendar.getInstance().getTime(), "El servicio no devuelve Ticket.", false);

				//logger.info(
					//	"\n--------\n Disculpa no se puede acceder a Supercines, intenta mas tarde - tiposTicket \n-----------------\n");
				Mensaje("Disculpa no se puede acceder a Supercines, por favor intenta mas tarde", false, true);
				return;
			}

			boolean ab_modificarCLB = maximoClubBeneficios != 0;
			boolean ab_pelTieneCLB = false;

			for (DetallePrecioTicketMV tipo : peliculaTicket.getContent()) {	
				if (tipo.getIsCB() && precioCLB == BigDecimal.ZERO) {
					ab_pelTieneCLB = true;
					precioCLB = new BigDecimal(tipo.getTicketPrice());
					/*if (maximoClubBeneficios > tipo.getCantidadMaximaTickets() && ab_modificarCLB)
						maximoClubBeneficios = tipo.getCantidadMaximaTickets();*/
				} else {
					if (!tipo.getIsCB() && precioGeneral == BigDecimal.ZERO) {
						precioGeneral = new BigDecimal(tipo.getTicketPrice());
						/*if (maximoGeneral > tipo.getCantidadMaximaTickets())
							maximoGeneral = tipo.getCantidadMaximaTickets();*/
					}
				}
			}
			
			
			
			if(!ab_pelTieneCLB)
			{
				ab_modificarCLB = ab_pelTieneCLB;
				maximoClubBeneficios = 0;
				verClubBeneficios = maximoClubBeneficios > 0;
			}

			armarInformacionPelicula();

		} catch (Exception e) {

			supercinesServicio.crearLogExc("Login-Reserva", fechaInicioProceso, "", Calendar.getInstance().getTime(), e,
					true);

			logger.info("\n--------\nError\n" + e + "\n-----------------\n");
			e.printStackTrace();
			Mensaje("No tienes peliculas pendientes, por favor regresa al chat para seleccionar una pelicula...", false,
					true);
			return;
		}
	}

	public void sumarCLB() {

		if (verPuestos) {
			Mensaje(mesEspera, false, false);
			return;
		}

		if (maximoClubBeneficios <= 0) {
			Mensaje("No tiene cupo para el Club de beneficios", false, false);
			return;
		}

		// logger.info("\n--------\n Suma Club Beneficios \n-----------------\n");

		clubBeneficios++;
		if (clubBeneficios > maximoClubBeneficios)
			clubBeneficios = maximoClubBeneficios;
		CalculaPrecio();
	}

	public void restarCLB() {

		if (verPuestos) {
			Mensaje(mesEspera, false, false);
			return;
		}

		// logger.info("\n--------\n Resta Club Beneficios \n-----------------\n");

		clubBeneficios--;
		if (clubBeneficios < 0)
			clubBeneficios = 0;
		CalculaPrecio();
	}

	public void sumarGeneral() {

		if (verPuestos) {
			Mensaje(mesEspera, false, false);
			return;
		}

		// logger.info("\n--------\n Suma General \n-----------------\n");

		general++;
		if (general > maximoGeneral)
			general = maximoGeneral;
		CalculaPrecio();
	}

	public void restarGeneral() {

		if (verPuestos) {
			Mensaje(mesEspera, false, false);
			return;
		}

		// logger.info("\n--------\n Resta General \n-----------------\n");

		general--;
		if (general < 0)
			general = 0;
		CalculaPrecio();
	}

	public void CalculaPrecio() {
		precioTotalCLB = precioCLB.multiply(new BigDecimal(clubBeneficios));
		precioTotalGeneral = precioGeneral.multiply(new BigDecimal(general));
	}

	public void irAReservarPuestos() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {
			if (!verPuestos) {
				Mensaje("Por favor Seleccionar un número de Entradas mayor a 0 (Cero).", false, false);
				return;
			}

			FacesContext contex = FacesContext.getCurrentInstance();
			contex.getExternalContext().redirect(PedidoUtil.armarUrlPuestosPelicula(pedido.getId(), pedido.getToken()));
		} catch (Exception e) {
			supercinesServicio.crearLogExc("Login-Reserva", fechaInicioProceso, "", Calendar.getInstance().getTime(), e,
					true);
			Mensaje("No pude redireccionar", false, true);
		}
	}

	public void guardarDetallePedido() {

		if (verPuestos) {
			Mensaje(mesEspera, false, false);
			return;
		}

		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {

			supercinesServicio.borrarPorIdPedido(pedido);
			verPuestos = false;
			double  puestosCB = 0;
			double  puestosGen = 0;

			for (DetallePrecioTicketMV tipoTicket : peliculaTicket.getContent()) {

				if (tipoTicket.getIsCB()) {
					if (maximoClubBeneficios > 0 && clubBeneficios > 0 && puestosCB == 0) {
						// logger.info("\n--------\n Beneficios \n-----------------\n");
						// logger.info("\n--------\n "+tipoTicket.getTicketDescription()+"\n-----------------\n");
						// logger.info("\n--------\n "+clubBeneficios+" \n-----------------\n");
						supercinesServicio.agregarDetallePedidoSupercines(pedido, usuario, tipoTicket, clubBeneficios);
						verPuestos = true;
						puestosCB = puestosCB + Double.parseDouble(tipoTicket.getTicketPrice());
					}
					continue;
				} else {
					if (!tipoTicket.getIsCB() && general > 0 && puestosGen == 0) {
						// logger.info("\n--------\n general \n-----------------\n");
						// logger.info("\n--------\n "+tipoTicket.getTicketDescription()+"\n-----------------\n");
						// \n-----------------\n");
						// logger.info("\n--------\n "+clubBeneficios+" \n-----------------\n");
						supercinesServicio.agregarDetallePedidoSupercines(pedido, usuario, tipoTicket, general);
						verPuestos = true;
						puestosGen = puestosGen + Double.parseDouble(tipoTicket.getTicketPrice());
					}
				}

			}
			if (verPuestos) {
				
				pedido.setValorTotal(precioTotalCLB.add(precioTotalGeneral));
				supercinesServicio.guardarPedido(pedido);
				
				Mensaje("Guardado Correctamente.", false, false);
				irAReservarPuestos();
				
			} else
				Mensaje("No se pudo guardar.", true, false);

		} catch (Exception e) {

			supercinesServicio.crearLogExc("Login-Reserva", fechaInicioProceso, "", Calendar.getInstance().getTime(), e,
					true);

			//logger.info("\n--------\nError\n" + e + "\n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
			return;
		}
	}

	private void armarInformacionPelicula() {

		//final Pelicula pelicula = supercinesServicio.obtenerPeliculaPorMovieToken(pedido.getMovieToken());
		final ComplejoCine complejo = supercinesServicio.obtenerComplejoPorSupercinesId(pedido.getComplejoId());
		final DateFormat fmtFechaPelicula = new SimpleDateFormat("EEEEE dd 'de' MMMMM");
		// TODO: Obtener informacion de la pelicula
		if (imagenPelicula == null || "".equals(imagenPelicula.trim()))
			imagenPelicula = PedidoUtil.urlImagenesCine() + pelicula.getUrlImagenLola();

		informacionPelicula = String.format("%s %s en la Sala %s del complejo %s el %s a las %s", pelicula.getNombre(),
				pedido.getNombreFormato(), pedido.getSala(), complejo.getNombre(),
				fmtFechaPelicula.format(pedido.getFechaPelicula()), pedido.getHorario());
	}

}
