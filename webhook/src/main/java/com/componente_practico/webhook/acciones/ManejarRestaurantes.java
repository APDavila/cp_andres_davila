package com.componente_practico.webhook.acciones;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.componente_practico.util.UrlUtil;
import com.componente_practico.util.ValidadorLocalizacionUsuarioUtil;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
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
import com.holalola.localizador.client.vo.ZonaVo;
import com.holalola.util.MailUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.MenuFavoritosDao;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.dao.ServicioDao;
import com.holalola.webhook.ejb.modelo.MenuFavoritos;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.ejb.modelo.Servicio;
import com.holalola.webhook.ejb.modelo.Servicios;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.ButtonTemplatePayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.RichMessageV2;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.WebUrlButton;

import ai.api.model.Result;

@Stateless
public class ManejarRestaurantes {

	private static final String PAYLOAD_HORARIO_ATENCION_PROVEEDOR = "MOSTRAR_HORARIO_ATENCION ";
	private static final String PARAM_SUBTIPO_COMIDA = "subtipoComida";
	private static final String PARAM_SUBTIPO_PREGRADO = "subTipoPregrado";
	private static final String PARAM_SUBTIPO_POSTGRADO = "subTipoPostgrado";
	private static final String PARAM_SUBTIPO_INSCRIPCION = "subTipoInscripcion";
	private static final String PARAM_SUBTIPO_CONTACTO = "subTipoContacto";
	
	private static final String TODOS_SUBTIPOS_COMIDA = "TODOS_COMIDA";
	private static final String TODOS_SUBTIPOS_LICORES = "TODOS_LICORES";
	private static final String TODOS_SUBTIPOS_MEDICINAS = "TODOS_MEDICINAS";
	
	private static final String CONTEXTO_PROCESA_RESERVA = "reservas";
	private static final String PARAM_FECHA = "date.original";
	private static final String PARAM_HORA = "time.original";
	private static final String PARAM_PERSONAS = "personas.original";
	private static final String PARAM_IDPROVEEDOR = "idProveedor.original";

	@EJB
	ProveedorDao carreraDao;

	@EJB
	ProveedorSubtipoComidaDao proveedorSubtipoComidaDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;
	
	@EJB
	RespuestaDao respuestaDao;

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
	ServicioDao servicioDao;
	
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

	private Proveedor carreraSeleccionada;
	
	public ResponseMessageApiAi mostrarRestaurantes(Result resultAi, Usuario usuario) {
		String subtipo = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_SUBTIPO_COMIDA,
				ArmarPedidoComida.CONTEXTO_CARRERA);
		String mensaje = null;
		int nivel=-1;
		
		try {
			nivel= Integer.parseInt(ApiAiUtil.obtenerValorParametro(resultAi, "idNivel",
					ArmarPedidoComida.CONTEXTO_CARRERA));}
			catch (Exception e) {
				nivel=-1;
			}

		switch (subtipo) {
		case TODOS_SUBTIPOS_MEDICINAS:
			// TODO: Este mensaje sacarlo de la base de datos
			mensaje = "%s, en que te podemos ayudar?";
			return armarGaleriaPorServicio(mensaje, Servicios.COMIDA.getServicio(), usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");
		case TODOS_SUBTIPOS_COMIDA:
			// TODO: Este mensaje sacarlo de la base de datos
			return armarGaleriaPorServicio("", Servicios.COMIDA.getServicio(), usuario, "", nivel, "USAR_DIRECCION_PRINCIPAL");

		case TODOS_SUBTIPOS_LICORES:
			// TODO: Este mensaje sacarlo de la base de datos
			mensaje = "%s, aquí puedes encontrar lo que necesitas ;)";
			return armarGaleriaPorServicio(mensaje, Servicios.LICORES.getServicio(), usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");

		default:
			final List<Proveedor> proveedores = proveedorSubtipoComidaDao
					.obtenerPorProveedoresActivosSubtipoComida(subtipo).stream().map(s -> {
						return s.getProveedor();
					}).collect(Collectors.toList());
			return armarGaleriaRestaurantes("", usuario, proveedores, "",-1,"");
		}

	}

	public ResponseMessageApiAi mostrarReservas(Result resultAi, Usuario usuario) {
		
		String mensaje = "%s, aquí puedes encontrar los que necesitas ;)";
		return armarGaleriaPorServicio(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Ver", -1,"USAR_CODIGO_PROVEEDOR_RESERVAS");
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
			
			Proveedor proveedor = carreraDao.obtenerPorId(Long.parseLong(idProveedor));
			
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

	private ResponseMessageApiAi armarGaleriaPorServicio(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String ls_intent) {
		if(mensaje != null && mensaje.trim().length() > 0)
		{
			String speech = String.format(mensaje, usuario.getNombreFacebook());
			ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
					propiedadesLola.facebookToken);
		}
		return armarGaleriaRestaurantes(mensaje, usuario, carreraDao.obtenerActivosPorServicio(servicio), as_textoVerMenu, nivel, ls_intent);//"USAR_DIRECCION_PRINCIPAL");
	}

	public ResponseMessageApiAi armarGaleriaRestaurantes(String mensaje, Usuario usuario, List<Proveedor> proveedores, String as_textoVerMenu, int nivel, String menuPorMostrar	) {

		String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
		
		final UbicacionUsuario ultimaUbicacion = ubicacionUsuarioServicio.obtenerUltimaUsuario(usuario);
		
		final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
		boolean esComida = false;
		if(proveedores != null && proveedores.size() > 0)
		{
			for (Proveedor p : proveedores) {
				
				esComida = p.getServicio().getId() == Servicios.COMIDA.getServicio().getId();
				break;
				
			}
		}
		
		String ls_textoTemp = "En pocos días";
		try
		{
			Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.PROXIMAMENTE);
			ls_textoTemp = respuesta != null ? respuesta.getTexto() : ls_textoTemp;
		}
		catch(Exception err)
		{
			ls_textoTemp = "En pocos días";
		}
		
		final String ls_textoProximamente = ls_textoTemp;
		
		
		List<Element> elementosRestaurantes = proveedores.stream().map(p -> {
			
			ButtonGeneral button;
			ButtonGeneral buttonHisto;
			List<ButtonGeneral> lista = new ArrayList();
			String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0 ? p.getDescripcion().trim() : "";
			
			if(p.isProximamente() && p.isActivo())
			{
				button = new PostbackButton(ls_textoProximamente, PAYLOAD_HORARIO_ATENCION_PROVEEDOR + p.getId());
				lista.add(button);
				return new Element(p.getNombre(), p.getLogoCerrado(), ls_decripcion, lista);

			}
			
			
			
			ZonaVo zona = null;
			boolean ab_puedeVer = false;
			boolean ab_abierto = false;
			if (p.isValidarUbicacion()) {
				zona = ValidadorLocalizacionUsuarioUtil.obtenerZonaCobertura(ultimaUbicacion, p.getId());  
				ab_puedeVer = zona != null;
			} else
				ab_puedeVer = true;

			buttonHisto = new PostbackButton("Tus últimos Pedidos",
					String.format("VER_HISTORIA_PROV " + p.getId()));
			ab_abierto = false;
			
			if (ab_puedeVer) {
				
				int li_validaHorario = estaDentroDeHorario(p.getId(), usuario,
						(ultimaUbicacion != null ? ultimaUbicacion.getCiudad() : ""),
						(zona != null && zona.getNombreGeneral() != null ? zona.getNombreGeneral() : ""));
				if (li_validaHorario >= 1) {
					ab_abierto = true;
					button = new PostbackButton(al_textoVerMenu,
							String.format("%s %s ", p.getPayloadValidarUbicacion(), p.getId()));
				} else {
					if (li_validaHorario == -1) {
						button = new PostbackButton("Hoy no Atendemos", PAYLOAD_HORARIO_ATENCION_PROVEEDOR + p.getId());
					} else {
						button = new PostbackButton("Ver Horario de Atención",
								PAYLOAD_HORARIO_ATENCION_PROVEEDOR + p.getId());
					}
				}
				
				lista.add(button);
				
				if(ab_abierto)
					lista.add(buttonHisto);
				
				return new Element(p.getNombre(), (ab_abierto ? p.getLogo() : p.getLogoCerrado()), ls_decripcion, lista);
			} else {
				int li_validador = 0;
				
				try
				{
					li_validador = Integer.valueOf(System.getProperty("lola.quitarSinCobertura")); 
				}
				catch(Exception err)
				{
					li_validador = 0;
				}
				
				if(li_validador != 1)
				{
					button = new PostbackButton("Sin Cobertura", PAYLOAD_HORARIO_ATENCION_PROVEEDOR + p.getId());
					lista.add(button);
					return new Element(p.getNombre(), p.getLogoCerrado(), ls_decripcion, lista);
				}
				else
					return null;
			}

		}).collect(Collectors.toList());
		
		List<Element> elementosRestaurantesTemp = new ArrayList<>();
		
		for (Element element : elementosRestaurantes) {
			if(element != null)
				elementosRestaurantesTemp.add(element);
		}
		
		elementosRestaurantes = elementosRestaurantesTemp;
		
		boolean ab_tieneUnoConCobertura = false;
		
		for (Element element : elementosRestaurantes) {
			for (ButtonGeneral item : element.getButtons()) {
				if(!item.getTitle().trim().equals("Sin Cobertura"))
				{
					ab_tieneUnoConCobertura = true;
					break;
				}
			}
			if(ab_tieneUnoConCobertura)
				break;
		}
		
		if(!ab_tieneUnoConCobertura)
			mensaje = "\n"+usuario.getNombreFacebook().trim() + ", no veo comercios cerca de ti :(, enseguida me pongo a trabajar para que muy pronto tengamos varias opciones en tu sector… (Y) \n";
		else
		{
			if(mensaje == null || mensaje.trim().length() <= 0)
				mensaje = usuario.getNombreFacebook().trim() + ", aquí puedes encontrar lo que necesitas\"";
		}
			
		String speech = String.format(mensaje, usuario.getNombreFacebook());
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);
		
		
		try
		{
			if(esComida)
			{
				List<MenuFavoritos> listaMenuFavoritos = menuFavoritosDao.obtenerMenuFavoritos(usuario.getId());
				List<Long> ll_ids = new ArrayList<Long>();
				List<Long> ll_idsProveedores = new ArrayList<Long>();
				String ls_imagenMenu = "";
				String ls_textoMenu = "";
				for (MenuFavoritos menuFavoritos : listaMenuFavoritos) {
					
					if(ll_idsProveedores.contains(menuFavoritos.getProveedor_id()))
						continue;
					
					ll_idsProveedores.add(menuFavoritos.getProveedor_id());
					
					if(ls_imagenMenu ==null || ls_imagenMenu == "")
						ls_imagenMenu = menuFavoritos.getImagenMenuPrincipal();
					
					if(ls_textoMenu ==null || ls_textoMenu == "")
						ls_textoMenu = menuFavoritos.getTextoMenuPrincipal();
					
					ZonaVo zona = null;
					boolean ab_puedeVer = false;
					if (menuFavoritos.isValidarubicacion()) {
						zona = ValidadorLocalizacionUsuarioUtil.obtenerZonaCobertura(ultimaUbicacion, menuFavoritos.getProveedor_id());  
						ab_puedeVer = zona != null;
					} else
						ab_puedeVer = true;
	
					if (ab_puedeVer) {
						
						int li_validaHorario = estaDentroDeHorario(menuFavoritos.getProveedor_id(), usuario,
								(ultimaUbicacion != null ? ultimaUbicacion.getCiudad() : ""),
								(zona != null && zona.getNombreGeneral() != null ? zona.getNombreGeneral() : ""));
						if (li_validaHorario >= 1) {
							ll_ids.add(menuFavoritos.getId());
						}					 
					}  
				}
			
				if(ll_ids != null && ll_ids.size() > 0)
				{
					if(ls_imagenMenu ==null || ls_imagenMenu == "")
						ls_imagenMenu = "ComidaFavorita.png";
					
					if(ls_textoMenu ==null || ls_textoMenu == "")
						ls_textoMenu = "Tu comida favorita";
										
//					ButtonGeneral button = new WebUrlButton("Iniciar",
//							UrlUtil.armarUrlIniciarCompra(usuario.getId()), true);
							
					ButtonGeneral button = new PostbackButton(al_textoVerMenu,
									String.format("%s %s ", "PIZZAHUT_VALIDAR_UBICACION", -99));
					
						Element element = new Element(ls_textoMenu.trim(), System.getProperty("lola.images.urlImghl") + ls_imagenMenu.trim(), "", Arrays.asList(button));
						
					elementosRestaurantes.add(0, element);
				}
			}
		}
		catch(Exception err)
		{
			System.out.println("No se pudo cargar menu favoritos "+err.getMessage());
			
			err.printStackTrace();
		}

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
		
		/*return ApiAiUtil.armarRespuestaGenericTemplate(elementosRestaurantes, "Galeria de restaurantes",
				"mostrarRestaurantes");*/
	}

	private final int estaDentroDeHorario(Long idProveedor, Usuario usuario, String ls_ciudad,
			String ls_local) {

			return carreraDao.validaHorario(idProveedor, usuario, ls_ciudad, ls_local);
		
	}
	
//*******************************DIALOG FLOW V2**********************************
	public ResponseMessageApiAiV2 mostrarRestaurantesV2(QueryResult resultAi, Usuario usuario) {
		String subtipo = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_SUBTIPO_COMIDA,
				ArmarPedidoComida.CONTEXTO_CARRERA);
		
		String mensaje = null;
		int nivel=-1;
		
		try {
			nivel= Integer.parseInt(ApiAiUtil.obtenerValorParametroV2(resultAi, "idNivel.original",
					ArmarPedidoComida.CONTEXTO_CARRERA));}
			catch (Exception e) {
				nivel=-1;
			}
		
		
		switch (subtipo) {
		case TODOS_SUBTIPOS_MEDICINAS:
			// TODO: Este mensaje sacarlo de la base de datos
			mensaje = "";
			return armarGaleriaPorServicioV2(mensaje, Servicios.MEDICINAS.getServicio(), usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");
		case TODOS_SUBTIPOS_COMIDA:
			// TODO: Este mensaje sacarlo de la base de datos
			return armarGaleriaPorServicioV2("", Servicios.COMIDA.getServicio(), usuario, "", nivel, "USAR_DIRECCION_PRINCIPAL");

		case TODOS_SUBTIPOS_LICORES:
			// TODO: Este mensaje sacarlo de la base de datos
			mensaje = "%s, aquí puedes encontrar lo que necesitas ;)";
			return armarGaleriaPorServicioV2(mensaje, Servicios.LICORES.getServicio(), usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");

		default:
			final List<Proveedor> proveedores = proveedorSubtipoComidaDao
					.obtenerPorProveedoresActivosSubtipoComida(subtipo).stream().map(s -> {
						return s.getProveedor();
					}).collect(Collectors.toList());
			return armarGaleriaCarreras("", usuario, proveedores, "",-1,"");
		}

	}
	
	public ResponseMessageApiAiV2 mostrarPregrado(QueryResult resultAi, Usuario usuario) {
		String subtipo = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_SUBTIPO_PREGRADO,
				ArmarPedidoComida.CONTEXTO_CARRERA);
		String mensaje = null;
		int nivel=-1;
		try {
			nivel= Integer.parseInt(ApiAiUtil.obtenerValorParametroV2(resultAi, "idNivel.original",
					ArmarPedidoComida.CONTEXTO_CARRERA));}
			catch (Exception e) {
				nivel=-1;
			}
		Servicio servicioActual;
		servicioActual=servicioDao.obtenerPorPayload(subtipo);	
			return armarGaleriaPorServicioV2(mensaje, servicioActual, usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");					
	}
	
	
	public ResponseMessageApiAiV2 mostrarDetallesCarrera(QueryResult resultAi, Usuario usuario) {		
		String subtipo = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_SUBTIPO_PREGRADO,
				ArmarPedidoComida.CONTEXTO_CARRERA);		
		long idCarrera = Long.valueOf(ApiAiUtil.obtenerValorParametroV2(resultAi, "idProveedor", ArmarPedidoComida.CONTEXTO_CARRERA));
		carreraSeleccionada = carreraDao.obtenerPorId(idCarrera);
		String mensaje = null;
		final String speech = String.format(carreraSeleccionada.getNotas_administrativas(), usuario.getNombreFacebook());

		final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Carreras", "PREGRADO"));
		PostbackButton but =new PostbackButton("Llenar Inscripción", "INSCRIBIRSE");
		final PayloadResponse data = new PayloadResponse(
				new RichMessageV2(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons))));

		return new ResponseMessageApiAiV2(speech, "iniciarConversacion", data, null);	
	}
	
	
	public ResponseMessageApiAiV2 mostrarPostgrado(QueryResult resultAi, Usuario usuario) {
		String subtipo = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_SUBTIPO_POSTGRADO,
				ArmarPedidoComida.CONTEXTO_CARRERA);
		String mensaje = null;
		int nivel=-1;
		try {
			nivel= Integer.parseInt(ApiAiUtil.obtenerValorParametroV2(resultAi, "idNivel.original",
					ArmarPedidoComida.CONTEXTO_CARRERA));}
			catch (Exception e) {
				nivel=-1;
			}
		Servicio servicioActual;
		servicioActual=servicioDao.obtenerPorPayload(subtipo);	
			return armarGaleriaPostgrado(mensaje, servicioActual, usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");					
	}
	
	public ResponseMessageApiAiV2 mostrarRestaurantesV3(QueryResult resultAi, Usuario usuario) {
		/*MÉTODO PARA ARMAR EL MENU DE ACUERDO AL SERVICIO*/
		String subtipo = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_SUBTIPO_COMIDA,
				ArmarPedidoComida.CONTEXTO_CARRERA);
		String mensaje = null;
		int nivel=-1;
		
		try {
			nivel= Integer.parseInt(ApiAiUtil.obtenerValorParametroV2(resultAi, "idNivel.original",
					ArmarPedidoComida.CONTEXTO_CARRERA));}
			catch (Exception e) {
				nivel=-1;
			}
		
		
		Servicio servicioActual;
		servicioActual=servicioDao.obtenerPorPayload(subtipo);
		if(servicioActual.getId()==null || servicioActual.getId()<=0) {
			final List<Proveedor> proveedores = proveedorSubtipoComidaDao
					.obtenerPorProveedoresActivosSubtipoComida(subtipo).stream().map(s -> {
						return s.getProveedor();
					}).collect(Collectors.toList());
			return armarGaleriaCarreras("", usuario, proveedores, "",-1,"");
		}
		else {
			return armarGaleriaPorServicioV2(mensaje, servicioActual, usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");
		}
			

	}
	
	public ResponseMessageApiAiV2 mostrarRestaurantesV4(String payload, Usuario usuario) {
		/*MÉTODO PARA ARMAR EL MENU DE ACUERDO AL SERVICIO*/
		String subtipo = payload;
		String mensaje = null;
		int nivel=-1;
		
		Servicio servicioActual;
		servicioActual=servicioDao.obtenerPorPayload(subtipo);
		if(servicioActual.getId()==null || servicioActual.getId()<=0) {
			final List<Proveedor> proveedores = proveedorSubtipoComidaDao
					.obtenerPorProveedoresActivosSubtipoComida(subtipo).stream().map(s -> {
						return s.getProveedor();
					}).collect(Collectors.toList());
			return armarGaleriaCarreras("", usuario, proveedores, "",-1,"");
		}
		else {
			return armarGaleriaPorServicioV2(mensaje, servicioActual, usuario, "", nivel,"USAR_DIRECCION_PRINCIPAL");
		}
			

	}
	
	private ResponseMessageApiAiV2 armarGaleriaPorServicioV2(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String ls_intent) {
		if(mensaje != null && mensaje.trim().length() > 0)
		{
		}
		return armarGaleriaCarreras(mensaje, usuario, carreraDao.obtenerActivosCiudadServicio(servicio,true,"Quito"), as_textoVerMenu, nivel, ls_intent);//"USAR_DIRECCION_PRINCIPAL");
	}
	
	
	private ResponseMessageApiAiV2 armarGaleriaPostgrado(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String ls_intent) {
		if(mensaje != null && mensaje.trim().length() > 0)
		{
		}
		return armarGaleriaPostgrado(mensaje, usuario, carreraDao.obtenerActivosCiudadServicio(servicio,true,"Quito"), as_textoVerMenu, nivel, ls_intent);//"USAR_DIRECCION_PRINCIPAL");
	}
	
	public ResponseMessageApiAiV2 armarGaleriaCarreras(String mensaje, Usuario usuario, List<Proveedor> proveedores, String as_textoVerMenu, int nivel, String menuPorMostrar	) {
		String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
				
		
		final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
		boolean esComida = false;
		
		
		if(proveedores != null && proveedores.size() > 0)
		{
			for (Proveedor p : proveedores) {
				esComida = p.getServicio().getId() == Servicios.COMIDA.getServicio().getId();
				break;
				
			}
		}
		List<Element> elementosRestaurantes = proveedores.stream().map(p -> {

			ButtonGeneral button;
			ButtonGeneral buttonHisto;
			List<ButtonGeneral> lista = new ArrayList();
			String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0 ? p.getDescripcion().trim() : "";			
			
			ZonaVo zona = null;
			boolean ab_puedeVer = false;
			boolean ab_abierto = false;
			if (p.isValidarUbicacion()) {
				ab_puedeVer = zona != null;
			} else
				ab_puedeVer = true;

			buttonHisto = new PostbackButton("Mas información",
					String.format("VER_HISTORIA_PROV " + p.getId()));
			ab_abierto = false;
			
			
			//System.out.println("-------------------  ab_puedeVer "+ab_puedeVer)
				
			button = new PostbackButton("Requisitos",
					"REQUISITOS");
			lista.add(button);
					ab_abierto = true;
						button= new WebUrlButton("Ver malla curricular",UrlUtil.verMallaCurricular(usuario.getId(),Long.toString(p.getId()),p.getMenuPrincipal(),p.getServicio()), true);
				
				lista.add(button);
				
				//System.out.println("-------------------  ab_abierto "+ab_abierto);
				
				if(ab_abierto)
					lista.add(buttonHisto);
				
				return new Element(p.getNombre(), (ab_abierto ? p.getLogo() : p.getLogoCerrado()), ls_decripcion, lista);

		}).collect(Collectors.toList());
		List<Element> elementosRestaurantesTemp = new ArrayList<>();
		
		for (Element element : elementosRestaurantes) {
			if(element != null)
				elementosRestaurantesTemp.add(element);
		}
		
		elementosRestaurantes = elementosRestaurantesTemp;
		boolean ab_tieneUnoConCobertura = false;
		
		if(mensaje == null || mensaje.trim().length() <= 0)
				mensaje = usuario.getNombreFacebook().trim() + ", aquí puedes encontrar lo que necesitas ";		
		String speech = String.format(mensaje, usuario.getNombreFacebook());
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);
		
		
	
		List<List<Element>> elementos = ListUtils.partition(elementosRestaurantes, 9);

		int niveles = elementos.size();

		List<Element> elementosVer = new ArrayList<>();
		if (niveles > 0)
		{
			try
			{
				elementosVer = new ArrayList<>(elementos.get(nivel < 0 ? 0 : nivel));
			}
			catch(Exception err)
			{
				elementosVer = new ArrayList<>(elementos.get(0));
			}
		}
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
		} 
		source = source +" -1";
		return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, "Galeria de restaurantes",
				source);
		
		/*return ApiAiUtil.armarRespuestaGenericTemplate(elementosRestaurantes, "Galeria de restaurantes",
				"mostrarRestaurantes");*/
	}
	
	public ResponseMessageApiAiV2 armarGaleriaPostgrado(String mensaje, Usuario usuario, List<Proveedor> proveedores, String as_textoVerMenu, int nivel, String menuPorMostrar	) {
		String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
				
		
		final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
		boolean esComida = false;
		
		
		if(proveedores != null && proveedores.size() > 0)
		{
			for (Proveedor p : proveedores) {
				esComida = p.getServicio().getId() == Servicios.COMIDA.getServicio().getId();
				break;
				
			}
		}
		List<Element> elementosRestaurantes = proveedores.stream().map(p -> {

			ButtonGeneral button;
			ButtonGeneral buttonHisto;
			List<ButtonGeneral> lista = new ArrayList();
			String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0 ? p.getDescripcion().trim() : "";			
			
			ZonaVo zona = null;
			boolean ab_puedeVer = false;
			boolean ab_abierto = false;
			if (p.isValidarUbicacion()) {
				ab_puedeVer = zona != null;
			} else
				ab_puedeVer = true;

			buttonHisto = new PostbackButton("Mas información",
					String.format("VER_HISTORIA_PROV " + p.getId()));
			ab_abierto = false;
			
			
			//System.out.println("-------------------  ab_puedeVer "+ab_puedeVer)
				
			button = new PostbackButton("Requisitos",
					"REQUISITOS");
			lista.add(button);
					ab_abierto = true;
						button= new WebUrlButton("Ver malla curricular",UrlUtil.verMallaCurricular(usuario.getId(),Long.toString(p.getId()),p.getMenuPrincipal(),p.getServicio()), true);
				
				lista.add(button);
				
				//System.out.println("-------------------  ab_abierto "+ab_abierto);
				
				if(ab_abierto)
					lista.add(buttonHisto);
				
				return new Element(p.getNombre(), (ab_abierto ? p.getLogo() : p.getLogoCerrado()), ls_decripcion, lista);

		}).collect(Collectors.toList());
		List<Element> elementosRestaurantesTemp = new ArrayList<>();
		
		for (Element element : elementosRestaurantes) {
			if(element != null)
				elementosRestaurantesTemp.add(element);
		}
		
		elementosRestaurantes = elementosRestaurantesTemp;
		boolean ab_tieneUnoConCobertura = false;
		
		if(mensaje == null || mensaje.trim().length() <= 0)
				mensaje = usuario.getNombreFacebook().trim() + ", aquí puedes encontrar lo que necesitas ";		
		String speech = String.format(mensaje, usuario.getNombreFacebook());
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);
		
		
	
		List<List<Element>> elementos = ListUtils.partition(elementosRestaurantes, 9);

		int niveles = elementos.size();

		List<Element> elementosVer = new ArrayList<>();
		if (niveles > 0)
		{
			try
			{
				elementosVer = new ArrayList<>(elementos.get(nivel < 0 ? 0 : nivel));
			}
			catch(Exception err)
			{
				elementosVer = new ArrayList<>(elementos.get(0));
			}
		}
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
		} 
		source = source +" -1";
		return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, "Galeria de restaurantes",
				source);
		
		/*return ApiAiUtil.armarRespuestaGenericTemplate(elementosRestaurantes, "Galeria de restaurantes",
				"mostrarRestaurantes");*/
	}

	
}
