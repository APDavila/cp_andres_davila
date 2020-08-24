package com.componente_practico.webhook.acciones;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.collections4.ListUtils;

import com.componente_practico.comida.pedido.ejb.servicio.ArmarPedidoComida;
import com.componente_practico.comida.pedido.ejb.servicio.PedidoServicio;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.general.ejb.servicio.UbicacionUsuarioServicio;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.comida.pedido.ejb.dao.DatoFacturaPedidoDao;
import com.holalola.comida.pedido.ejb.dao.DetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.FormaPagoProveedorDao;
import com.holalola.comida.pedido.ejb.dao.OperadorProveedorDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorSubtipoComidaDao;
import com.holalola.comida.pedido.ejb.modelo.DatoFacturaPedido;
import com.holalola.comida.pedido.ejb.modelo.DetallePedido;
import com.holalola.comida.pedido.ejb.modelo.DetalleProducto;
import com.holalola.comida.pedido.ejb.modelo.FormaPagoProveedor;
import com.holalola.comida.pedido.ejb.modelo.FormasPago;
import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.comida.pizzahut.ejb.dao.DetalleProductoDao;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.googlemaps.vo.DireccionGoogleMaps;
import com.holalola.util.MailUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.MenuFavoritosDao;
import com.holalola.webhook.ejb.modelo.Servicio;
import com.holalola.webhook.ejb.modelo.Servicios;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.TextMessage;

import ai.api.model.Result;

@Stateless
public class ManejarReservas {
	private static final String PAYLOAD_HORARIO_ATENCION_PROVEEDOR = "MOSTRAR_HORARIO_ATENCION ";
	private static final String PARAM_SUBTIPO_COMIDA = "subtipoComida";
	private static final String TODOS_SUBTIPOS_COMIDA = "TODOS_COMIDA";
	private static final String TODOS_SUBTIPOS_LICORES = "TODOS_LICORES";
	
	private static final String CONTEXTO_PROCESA_RESERVA = "reservas";
	private static final String PARAM_FECHA = "date.original";
	private static final String PARAM_HORA = "time.original";
	private static final String PARAM_PERSONAS = "personas.original";
	private static final String PARAM_IDPROVEEDOR = "idProveedor.original";
	private static final String CONTEXTO_MOSTRAR_MENU_COMIDA = "mostrar_menu_comida";

	@EJB
	ProveedorDao proveedorDao;

	@EJB
	ProveedorSubtipoComidaDao proveedorSubtipoComidaDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	PedidoServicio pedidoServicio;

	@EJB
	UbicacionUsuarioDao ubicacionUsuarioDao;
	
	@EJB
	PedidoDao pedidoDao;
	
	@EJB
	DetallePedidoDao detallePedidoDao;
	
	@EJB
	DetalleProductoDao detalleProductoDao;
	
	@EJB
	DatoFacturaPedidoDao datoFacturaPedidoDao; 
	
	@EJB
	OperadorProveedorDao operadorProveedorDao;
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	@EJB
	FormaPagoProveedorDao formaPagoProveedorDao;
	
	@EJB
	MenuFavoritosDao menuFavoritosDao;

	public ResponseMessageApiAi mostrarRestaurantes(Result resultAi, Usuario usuario) {
		String subtipo = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_SUBTIPO_COMIDA,
				ArmarPedidoComida.CONTEXTO_CARRERA);
		String mensaje = null;
		int nivel=-1;
		
		try {
		nivel= Integer.parseInt(ApiAiUtil.obtenerValorParametro(resultAi, "idNivel",
				ArmarPedidoComida.CONTEXTO_CARRERA));}
		catch (Exception e) {
			System.out.println("No se obtuvo el nivel");
		}
		/*String check=String.valueOf(nivel);
		if (check == null || check.isEmpty()) {
			nivel = -1;
		} else {
			
		}*/
		
		System.out.println("---------------------------nivel "+nivel);

		switch (subtipo) {
		case TODOS_SUBTIPOS_COMIDA:
			// TODO: Este mensaje sacarlo de la base de datos
			mensaje = "%s, qué tipo de comida te gustaría pedir?";
			return armarGaleriaPorServicio(mensaje, Servicios.COMIDA.getServicio(), usuario, "", nivel);

		case TODOS_SUBTIPOS_LICORES:
			// TODO: Este mensaje sacarlo de la base de datos
			mensaje = "%s, aquí puedes encontrar lo que necesitas ;)";
			return armarGaleriaPorServicio(mensaje, Servicios.LICORES.getServicio(), usuario, "", nivel);

		default:
			final List<Proveedor> proveedores = proveedorSubtipoComidaDao
					.obtenerPorProveedoresActivosSubtipoComida(subtipo).stream().map(s -> {
						return s.getProveedor();
					}).collect(Collectors.toList());
			return armarGaleriaRestaurantes(usuario, proveedores, "", -1, "");
		}

	}

	public ResponseMessageApiAi mostrarReservas(Result resultAi, Usuario usuario) {
		
		/*if(usuario.getEmail() == null || usuario.getEmail().trim().length() <= 0)
		{
			String speech = String.format("%s, necesito tu información completa. Por favor haz click en 'Completar' :)",
					usuario.getNombreFacebook());
			UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
			List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Completar",
					UrlUtil.armarUrlCompletarDireccion(ubicacionUsuario.getId(), ubicacionUsuario.getToken(), "Reservas", 0), true));
			Data data = new Data(new ButtonRichMessage(speech, buttons));

			return new ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
		}*/
		
		String mensaje = "%s, aquí puedes encontrar los que necesitas ;)";
		return armarGaleriaPorServicio(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Ver", -1);
	}
	
	private static Date addMinutesToDate(int minutes, Date beforeTime){
	    final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

	    long curTimeInMs = beforeTime.getTime();
	    Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
	    return afterAddingMins;
	}
	
    public ResponseMessageApiAi provesarReserva(Result resultAi, Usuario usuario) {
		//return null;
		try {
			
			String idProveedor = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_IDPROVEEDOR, CONTEXTO_PROCESA_RESERVA);
			String txtFecha = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_FECHA, CONTEXTO_PROCESA_RESERVA);
			final String txtHora = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_HORA, CONTEXTO_PROCESA_RESERVA);
			final String personas = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_PERSONAS, CONTEXTO_PROCESA_RESERVA);

			//2018-03-07
			
			//SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			 
			try
			{
				//System.out.println(txtFecha);
				//System.out.println(txtHora);
				
				Date fecha = dateformat2.parse(txtFecha.replace("/", "-").trim()+" "+txtHora.trim());
				
				//System.out.println(dateformat2.format(fecha));
				
				Date fechaActual = addMinutesToDate(240, new Date());
				
				//System.out.println(dateformat2.format(fechaActual));
				
				if(fecha.compareTo(fechaActual) < 0){
					return new ResponseMessageApiAi("Disculpame :( solo puedo hacer reservas con 4 horas de anticipación.", "Disculpame :( solo puedo hacer reservas con 4 horas de anticipación.", null, null, "reservas");
				}
			}
			catch(Exception Err)
			{
				return new ResponseMessageApiAi("Disculpame :( no entiendo la fecha.", "Disculpame :( no entiendo la fecha.", null, null, "reservas");
			}
			
			int numeroPersonas = 0;
			
			try
			{
				numeroPersonas = Integer.parseInt(personas);
				
				if(numeroPersonas > 100)
					return new ResponseMessageApiAi("Disculpame :( solo puedo reservar hasta 100 Personas.", "Disculpame :( solo puedo reservar hasta 100 Personas.", null, null, "reservas");
			}
			catch(Exception Err)
			{
				return new ResponseMessageApiAi("Disculpame :( no entiendo el número de personas.", "Disculpame :( no entiendo el número de personas.", null, null, "reservas");
			}
			
			Proveedor proveedor = proveedorDao.obtenerPorId(Long.parseLong(idProveedor));
			
			List<DetalleProducto> listaDetalleProducto = detalleProductoDao.obtenerPorSubmenuProveedor(usuario.getId(),proveedor.getId(), proveedor.getMenuPrincipal());
			
			if(listaDetalleProducto == null || listaDetalleProducto.size() <= 0)
			{
				return new ResponseMessageApiAi("Disculpame :( no tengo reservas disponibles.", "Disculpame :( no tengo reservas disponibles.", null, null, "reservas");
			}
			
			List<UbicacionUsuario> listaUbicacionUsuario = ubicacionUsuarioDao.obtenerPrincipalUsuario(usuario);
			UbicacionUsuario ubicacionUsuario = null;
			if(listaUbicacionUsuario == null || listaUbicacionUsuario.size() <= 0)
			{
				ubicacionUsuario = new UbicacionUsuario(new DireccionGoogleMaps(-1.264941, -78.6270599), usuario);
				ubicacionUsuario.setEsPrincipal(true);
				ubicacionUsuario = ubicacionUsuarioDao.insertar(ubicacionUsuario);
			}
			else
				ubicacionUsuario = listaUbicacionUsuario.get(0);
			
			String reserva = "Por favor reservar para el "+txtFecha+" a las "+txtHora+" para " +  personas + " persona"+(numeroPersonas > 1 ? "s" : "");
			
			Pedido pedido = new Pedido(proveedor, usuario, UUID.randomUUID().toString(), (long) 0, BigDecimal.ZERO, false, 0, 0, 0, "", pedidoDao.obtenerImpuesto());
			pedido.setNota(reserva);
			pedido = pedidoDao.insertar(pedido);
			pedido.setFormaPago(FormasPago.EFECTIVO.getFormaPago());
			
			FormaPagoProveedor formaPagoProveedor = formaPagoProveedorDao.obtenerPorProveedorFormaPago(proveedor, FormasPago.EFECTIVO.getFormaPago().getId());
			
			pedido.setComisionTotal(formaPagoProveedor != null ? formaPagoProveedor.getComisionTotal() : 0);
			pedido.setComisionFormpago(formaPagoProveedor != null ? formaPagoProveedor.getComisionFormpago() : 0);
			pedido.setImpuestos(proveedor.getPorcentajeResta());
			
			pedido.setConfirmadoUsuario(true);
			
			DatoFacturaPedido datoFacturaPedido = new DatoFacturaPedido(pedido, usuario.getNumeroIdentificacion() != null ? usuario.getNumeroIdentificacion().trim() : "", 
					                                                            usuario.getTipoIdentificacion() != null ? usuario.getTipoIdentificacion().trim() : "",
					                                                            usuario.getNombres() != null ? usuario.getNombres().trim() : (usuario.getNombreFacebook() != null ? usuario.getNombreFacebook().trim() : ""),
					                                                            usuario.getApellidos() != null ? usuario.getApellidos().trim() : (usuario.getApellidoFacebook() != null ? usuario.getApellidoFacebook().trim() : ""),
					                                                            (ubicacionUsuario.getCallePrincipal() != null ? ubicacionUsuario.getCallePrincipal() : "") + (ubicacionUsuario.getCalleSecundaria() != null ? " " + ubicacionUsuario.getCalleSecundaria() : ""),
					                                                            ubicacionUsuario.getTelefono() != null ? ubicacionUsuario.getTelefono().trim() : "",
					                                                            ubicacionUsuario.getCelular() != null ? ubicacionUsuario.getCelular().trim() : "",
					                                                            usuario.getEmail() != null ? usuario.getEmail().trim() : "");
			
			datoFacturaPedido = datoFacturaPedidoDao.insertar(datoFacturaPedido);
			
			pedido.setDatoFacturaPedido(datoFacturaPedido);
			pedido.setUbicacionUsuario(ubicacionUsuario);
			pedido.setTotal(listaDetalleProducto.get(0).getPrecio());
			
			pedidoDao.modificar(pedido);
			
			DetallePedido detallePedido = new DetallePedido(pedido, listaDetalleProducto.get(0), 1, reserva);
	
			detallePedidoDao.insertar(detallePedido);
				
			reserva = "Listo "+usuario.getNombreFacebook() +", tu reserva fue enviada a " + proveedor.getNombre() + ", en unos minutos te confirmaremos la aceptación de tu reserva.";
			
			final TextMessage mensaje1 = new TextMessage(reserva);

			ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje1),
					propiedadesLola.facebookToken);
			
			List<OperadorProveedor> listaOperadorProveedor = operadorProveedorDao.porProveedorRolOperador(pedido.getProveedor());
			String ls_emails = "";
			for (OperadorProveedor operadorProveedor : listaOperadorProveedor) {
				if(operadorProveedor.getEmail() != null && operadorProveedor.getEmail().trim().length() > 0)
				{
					if(ls_emails.trim().length() > 0)
						ls_emails = ls_emails + ";";
					ls_emails = ls_emails + operadorProveedor.getEmail().trim();
				}
			}
			
            List<String> ls_botones = new ArrayList<String>();
			
			ls_botones.add("Confirmar|NUO|"+pedidoDao.generaUrlConfirmaEmail(pedido, true).trim());
			ls_botones.add("Rechazar|NUO|"+pedidoDao.generaUrlConfirmaEmail(pedido, false).trim());
			
			MailUtil.enviarMail(ls_emails,"Reservas - Hola Lola", "Se solicitó una reserva a nombre de " + usuario.toString() + " " + pedido.getNota().replace("Por favor reservar", "")
										 , false, ls_botones, "");
			
			
			return new ResponseMessageApiAi(":)", ":)", null, null, "reservas");
		
		} catch (Exception e) {
			/*final String speech = "Ay que pena, creo que me he quedado dormida y no pude apuntarlo.\nInt\u00e9ntalo nuevamte por favor :(";
			log.error("No se pudo agendar evento.", e);
			return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, "tiemposRecordatorio");
			*/
		}
		return null;
	}

	private ResponseMessageApiAi armarGaleriaPorServicio(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel) {
		String speech = String.format(mensaje, usuario.getNombreFacebook());
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);
		return armarGaleriaRestaurantes(usuario, proveedorDao.obtenerActivosPorServicio(servicio), as_textoVerMenu, nivel, "USAR_CODIGO_PROVEEDOR_RESERVAS");
	}

	public ResponseMessageApiAi armarGaleriaRestaurantes(Usuario usuario, List<Proveedor> proveedores, String as_textoVerMenu, int nivel, String menuPorMostrar) {
		
		String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
	  		
		final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
		
	 
		List<Element> elementosRestaurantes = proveedores.stream().map(p -> {
			
			ButtonGeneral button;

			List<ButtonGeneral> lista = new ArrayList();
			String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0 ? p.getDescripcion().trim() : "";
					
			button = new PostbackButton(al_textoVerMenu,
							String.format("%s %s", menuPorMostrar, p.getId()));
				lista.add(button);
				//lista.add(buttonHisto);
				return new Element(p.getNombre(), p.getLogo(), ls_decripcion, lista);
		}).collect(Collectors.toList());
		
		
		
		List<List<Element>> elementos = ListUtils.partition(elementosRestaurantes, 9);

		int niveles = elementos.size();

		List<Element> elementosVer = new ArrayList<>();
		

		if (niveles > 0)
			elementosVer = new ArrayList<>(elementos.get(nivel < 0 ? 0 : nivel));
		if (niveles > 1) {

			int siguienteNivel = niveles == (nivel + 1) ? 0 : nivel + 1;
			if (niveles==(nivel+1)) {
				nivel=-1;
			}
		
			elementosVer = new ArrayList<>(elementos.get(siguienteNivel));

			final List<ButtonGeneral> buttons = new ArrayList<>();
			
			buttons.add(new PostbackButton(String.format("Más establecimientos"),
					String.format("%s %s",menuPorMostrar,
							nivel+1)));
		

			elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
		} else {
			
		}
		

		return ApiAiUtil.armarRespuestaGenericTemplate(elementosVer, "Galeria de restaurantes",
				source);
	}

	private final int estaDentroDeHorario(Long idProveedor, Usuario usuario, String ls_ciudad,
			String ls_local) {

			return proveedorDao.validaHorario(idProveedor, usuario, ls_ciudad, ls_local);
		
	}
	
//************************************************DIALOGFLOW V2********************************************************
public ResponseMessageApiAiV2 mostrarReservasV2(QueryResult resultAi, Usuario usuario) {
		
		/*if(usuario.getEmail() == null || usuario.getEmail().trim().length() <= 0)
		{
			String speech = String.format("%s, necesito tu información completa. Por favor haz click en 'Completar' :)",
					usuario.getNombreFacebook());
			UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
			List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Completar",
					UrlUtil.armarUrlCompletarDireccion(ubicacionUsuario.getId(), ubicacionUsuario.getToken(), "Reservas", 0), true));
			Data data = new Data(new ButtonRichMessage(speech, buttons));

			return new ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
		}*/
		
		String mensaje = "%s, aquí puedes encontrar los que necesitas ;)";
		return armarGaleriaPorServicioV2(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Ver", -1);
	}

private ResponseMessageApiAiV2 armarGaleriaPorServicioV2(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel) {
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantesV2(usuario, proveedorDao.obtenerActivosPorServicio(servicio), as_textoVerMenu, nivel, "USAR_CODIGO_PROVEEDOR_RESERVAS");
}

public ResponseMessageApiAiV2 armarGaleriaRestaurantesV2(Usuario usuario, List<Proveedor> proveedores, String as_textoVerMenu, int nivel, String menuPorMostrar) {
	
	String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
  		
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
	
 
	List<Element> elementosRestaurantes = proveedores.stream().map(p -> {
		
		ButtonGeneral button;

		List<ButtonGeneral> lista = new ArrayList();
		String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0 ? p.getDescripcion().trim() : "";
				button = new PostbackButton(al_textoVerMenu,
						String.format("%s %s", menuPorMostrar, p.getId()));
			lista.add(button);
			//lista.add(buttonHisto);
			return new Element(p.getNombre(), p.getLogo(), ls_decripcion, lista);
	}).collect(Collectors.toList());
	
	
	
	List<List<Element>> elementos = ListUtils.partition(elementosRestaurantes, 9);

	int niveles = elementos.size();

	List<Element> elementosVer = new ArrayList<>();
	

	if (niveles > 0)
		elementosVer = new ArrayList<>(elementos.get(nivel < 0 ? 0 : nivel));
	if (niveles > 1) {

		int siguienteNivel = niveles == (nivel + 1) ? 0 : nivel + 1;
		if (niveles==(nivel+1)) {
			nivel=-1;
		}
	
		elementosVer = new ArrayList<>(elementos.get(siguienteNivel));

		final List<ButtonGeneral> buttons = new ArrayList<>();
		
		buttons.add(new PostbackButton(String.format("Más establecimientos"),
				String.format("%s %s",menuPorMostrar,
						nivel+1)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, "Galeria de restaurantes",
			source);
}

public ResponseMessageApiAiV2 provesarReservaV2(QueryResult resultAi, Usuario usuario) {
	//return null;
	try {
		
		String idProveedor = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_IDPROVEEDOR, CONTEXTO_PROCESA_RESERVA);
		String txtFecha = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_FECHA, CONTEXTO_PROCESA_RESERVA);
		final String txtHora = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_HORA, CONTEXTO_PROCESA_RESERVA);
		final String personas = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_PERSONAS, CONTEXTO_PROCESA_RESERVA);

		//2018-03-07
		
		//SimpleDateFormat dateformat2 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		SimpleDateFormat dateformat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		 
		try
		{
			//System.out.println(txtFecha);
			//System.out.println(txtHora);
			
			Date fecha = dateformat2.parse(txtFecha.replace("/", "-").trim()+" "+txtHora.trim());
			
			//System.out.println(dateformat2.format(fecha));
			
			Date fechaActual = addMinutesToDate(240, new Date());
			
			//System.out.println(dateformat2.format(fechaActual));
			
			if(fecha.compareTo(fechaActual) < 0){
				return new ResponseMessageApiAiV2("Disculpame :( solo puedo hacer reservas con 4 horas de anticipación.", "reservas", null, null);
			}
		}
		catch(Exception Err)
		{
			return new ResponseMessageApiAiV2("Disculpame :( no entiendo la fecha.", "reservas", null, null);
		}
		
		int numeroPersonas = 0;
		
		try
		{
			numeroPersonas = Integer.parseInt(personas);
			
			if(numeroPersonas > 100)
				return new ResponseMessageApiAiV2("Disculpame :( solo puedo reservar hasta 100 Personas.", "reservas", null, null);
		}
		catch(Exception Err)
		{
			return new ResponseMessageApiAiV2("Disculpame :( no entiendo el número de personas.", "reservas", null, null);
		}
		
		Proveedor proveedor = proveedorDao.obtenerPorId(Long.parseLong(idProveedor));
		
		List<DetalleProducto> listaDetalleProducto = detalleProductoDao.obtenerPorSubmenuProveedor(usuario.getId(),proveedor.getId(), proveedor.getMenuPrincipal());
		
		if(listaDetalleProducto == null || listaDetalleProducto.size() <= 0)
		{
			return new ResponseMessageApiAiV2("Disculpame :( no tengo reservas disponibles.", "reservas", null, null);
		}
		
		List<UbicacionUsuario> listaUbicacionUsuario = ubicacionUsuarioDao.obtenerPrincipalUsuario(usuario);
		UbicacionUsuario ubicacionUsuario = null;
		if(listaUbicacionUsuario == null || listaUbicacionUsuario.size() <= 0)
		{
			ubicacionUsuario = new UbicacionUsuario(new DireccionGoogleMaps(-1.264941, -78.6270599), usuario);
			ubicacionUsuario.setEsPrincipal(true);
			ubicacionUsuario = ubicacionUsuarioDao.insertar(ubicacionUsuario);
		}
		else
			ubicacionUsuario = listaUbicacionUsuario.get(0);
		
		String reserva = "Por favor reservar para el "+txtFecha+" a las "+txtHora+" para " +  personas + " persona"+(numeroPersonas > 1 ? "s" : "");
		
		Pedido pedido = new Pedido(proveedor, usuario, UUID.randomUUID().toString(), (long) 0, BigDecimal.ZERO, false, 0, 0, 0, "", pedidoDao.obtenerImpuesto());
		pedido.setNota(reserva);
		pedido = pedidoDao.insertar(pedido);
		pedido.setFormaPago(FormasPago.EFECTIVO.getFormaPago());
		
		FormaPagoProveedor formaPagoProveedor = formaPagoProveedorDao.obtenerPorProveedorFormaPago(proveedor, FormasPago.EFECTIVO.getFormaPago().getId());
		
		pedido.setComisionTotal(formaPagoProveedor != null ? formaPagoProveedor.getComisionTotal() : 0);
		pedido.setComisionFormpago(formaPagoProveedor != null ? formaPagoProveedor.getComisionFormpago() : 0);
		pedido.setImpuestos(proveedor.getPorcentajeResta());
		
		pedido.setConfirmadoUsuario(true);
		
		DatoFacturaPedido datoFacturaPedido = new DatoFacturaPedido(pedido, usuario.getNumeroIdentificacion() != null ? usuario.getNumeroIdentificacion().trim() : "", 
				                                                            usuario.getTipoIdentificacion() != null ? usuario.getTipoIdentificacion().trim() : "",
				                                                            usuario.getNombres() != null ? usuario.getNombres().trim() : (usuario.getNombreFacebook() != null ? usuario.getNombreFacebook().trim() : ""),
				                                                            usuario.getApellidos() != null ? usuario.getApellidos().trim() : (usuario.getApellidoFacebook() != null ? usuario.getApellidoFacebook().trim() : ""),
				                                                            (ubicacionUsuario.getCallePrincipal() != null ? ubicacionUsuario.getCallePrincipal() : "") + (ubicacionUsuario.getCalleSecundaria() != null ? " " + ubicacionUsuario.getCalleSecundaria() : ""),
				                                                            ubicacionUsuario.getTelefono() != null ? ubicacionUsuario.getTelefono().trim() : "",
				                                                            ubicacionUsuario.getCelular() != null ? ubicacionUsuario.getCelular().trim() : "",
				                                                            usuario.getEmail() != null ? usuario.getEmail().trim() : "");
		
		datoFacturaPedido = datoFacturaPedidoDao.insertar(datoFacturaPedido);
		
		pedido.setDatoFacturaPedido(datoFacturaPedido);
		pedido.setUbicacionUsuario(ubicacionUsuario);
		pedido.setTotal(listaDetalleProducto.get(0).getPrecio());
		
		pedidoDao.modificar(pedido);
		
		DetallePedido detallePedido = new DetallePedido(pedido, listaDetalleProducto.get(0), 1, reserva);

		detallePedidoDao.insertar(detallePedido);
			
		reserva = "Listo "+usuario.getNombreFacebook() +", tu reserva fue enviada a " + proveedor.getNombre() + ", en unos minutos te confirmaremos la aceptación de tu reserva.";
		
		final TextMessage mensaje1 = new TextMessage(reserva);

		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje1),
				propiedadesLola.facebookToken);
		
		List<OperadorProveedor> listaOperadorProveedor = operadorProveedorDao.porProveedorRolOperador(pedido.getProveedor());
		String ls_emails = "";
		for (OperadorProveedor operadorProveedor : listaOperadorProveedor) {
			if(operadorProveedor.getEmail() != null && operadorProveedor.getEmail().trim().length() > 0)
			{
				if(ls_emails.trim().length() > 0)
					ls_emails = ls_emails + ";";
				ls_emails = ls_emails + operadorProveedor.getEmail().trim();
			}
		}
		
        List<String> ls_botones = new ArrayList<String>();
		
		ls_botones.add("Confirmar|NUO|"+pedidoDao.generaUrlConfirmaEmail(pedido, true).trim());
		ls_botones.add("Rechazar|NUO|"+pedidoDao.generaUrlConfirmaEmail(pedido, false).trim());
		
		MailUtil.enviarMail(ls_emails,"Reservas - Hola Lola", "Se solicitó una reserva a nombre de " + usuario.toString() + " " + pedido.getNota().replace("Por favor reservar", "")
									 , false, ls_botones, "");
		
		
		return new ResponseMessageApiAiV2(":)", "reservas", null, null);
	
	} catch (Exception e) {
		/*final String speech = "Ay que pena, creo que me he quedado dormida y no pude apuntarlo.\nInt\u00e9ntalo nuevamte por favor :(";
		log.error("No se pudo agendar evento.", e);
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, "tiemposRecordatorio");
		*/
	}
	return null;
}
}
