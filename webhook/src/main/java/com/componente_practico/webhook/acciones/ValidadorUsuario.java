package com.componente_practico.webhook.acciones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.joda.time.DateTime;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.general.ejb.servicio.UbicacionUsuarioServicio;
import com.componente_practico.general.ejb.servicio.UsuarioServicio;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.comida.pedido.ejb.dao.OperadorProveedorDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.general.ejb.dao.UsuarioOperadorDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioOperador;
import com.holalola.util.MailUtil;
import com.holalola.webhook.PayloadConstantes;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.dao.ServicioDao;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.response.InformacionUsuarioFacebook;
import com.holalola.webhook.facebook.response.UserLocation;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.ButtonRichMessageV2;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.UbicacionQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;

import ai.api.model.Result;

@Stateless
public class ValidadorUsuario {

	private static final String CONTEXTO_FACEBOOK_LOCATION = "facebook_location";
	private static final String CONTEXTO_FACEBOOK_SUBTIPO = "subtipoComida";
	private static final String PARAM_LATITUD = "lat"; // "lat.original";
	private static final String PARAM_LONGITUD = "long"; // "long.original";
	private static final String PAYLOAD_SOLICITA_AYUDA_HUMANA = "SOLICITA_AYUDA_HUMANA";

	@EJB
	private ManejarRestaurantes manejarRestaurantes;
	@EJB
	private UsuarioServicio usuarioServicio;
	@EJB
	private UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	RespuestaDao respuestaDao;

	@EJB
	ServicioDao servicioDao;

	@EJB
	UsuarioOperadorDao usuarioOperadorDao;

	@EJB
	OperadorProveedorDao operadorProveedorDao;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private PedidoDao pedidoDao;

	public ResponseMessageApiAi validarDireccionUsuario(Result resultAi, Usuario usuario) {

		List<String> listaAliasUbicacion = usuario != null ? ubicacionUsuarioServicio.obtenerAlias(usuario)
				: new ArrayList<String>();

		UbicacionUsuario principal = ubicacionUsuarioServicio.obtenerPrincipalUsuario(usuario);
		if ((listaAliasUbicacion == null || listaAliasUbicacion.size() <= 0) && principal == null) {
			return solicitarDireccionUsuario(resultAi, usuario);
		}

		if (principal == null || principal.getCallePrincipal() == null || principal.getNumeracion() == null) {
			return solicitarDireccionUsuario(resultAi, usuario);
		}

		final String direccionUsuario = principal != null
				? String.format((principal.getAlias() != null && principal.getAlias().trim().length() > 0
						? "(" + principal.getAlias().trim() + ") "
						: "") + "%s %s...?", principal.getCallePrincipal(), principal.getNumeracion())
				: "";
		String speech = String.format("La entrega será en: " + direccionUsuario);

		List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();

		List<QuickReplyGeneral> listaPersonasTem = new ArrayList<QuickReplyGeneral>();

		if (principal != null)
			listaPersonasTem.add(new TextQuickReply("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL -1"));

		for (String direccion : listaAliasUbicacion) {
			if (direccion != null && direccion.trim().length() > 0)
				listaPersonasTem.add(
						new TextQuickReply(direccion.trim(), "USAR_DIRECCION_PRINCIPAL " + direccion.trim() + " -1"));
		}

		listaPersonasTem.add(new TextQuickReply("Otra dirección", "SOLICITAR_NUEVA_DIRECCION_USUARIO"));

		return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "Direccion", listaPersonasTem);

		/*
		 * List<ButtonGeneral> buttons = Arrays.asList(new
		 * PostbackButton("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL"), new
		 * PostbackButton("No, en otra", "SOLICITAR_NUEVA_DIRECCION_USUARIO")); Data
		 * data = new Data(new ButtonRichMessage(speech, buttons)); return new
		 * ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
		 */
	}

	public ResponseMessageApiAi validarDireccionUsuario2(Result resultAi, Usuario usuario) {

		UbicacionUsuario principal = ubicacionUsuarioServicio.obtenerPrincipalUsuario(usuario);
		if (principal == null || principal.getCallePrincipal() == null || principal.getNumeracion() == null) {
			return solicitarDireccionUsuario(resultAi, usuario);
		}

		final String direccionUsuario = String.format("%s %s", principal.getCallePrincipal(),
				principal.getNumeracion());
		String speech = String.format("La entrega será en: %s...?", direccionUsuario);
		List<ButtonGeneral> buttons = Arrays.asList(
				new PostbackButton("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL -1"),
				new PostbackButton("No, en otra", "SOLICITAR_NUEVA_DIRECCION_USUARIO"));
		Data data = new Data(new ButtonRichMessage(speech, buttons));
		return new ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
	}

	public ResponseMessageApiAi ayudaHumana(Usuario usuario) {
		// Inactiva Chat
		usuario.setFechaInactivacion(DateTime.now().toDate());
		usuario.setChatActivo(false);
		usuarioServicio.modificar(usuario);

		String speech = "Listo " + usuario.getNombreFacebook() + " ya te ayudamos...";

		final TextMessage mensaje = new TextMessage(speech);
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje),
				propiedadesLola.facebookToken);

		UsuarioOperador usuarioOperadorTemp = usuarioOperadorDao.obtenerUltimoOperador(usuario);

		if (usuarioOperadorTemp != null) {
			try {
				MailUtil.enviarMail(usuarioOperadorTemp.getOperador().getEmail(),
						"El usuario " + usuario.toString() + " solicita ayuda personalizada",
						"El usuario " + usuario.toString() + " solicita ayuda personalizada, por favor ayudarlo.",
						false, "");
			} catch (Exception err) {

			}
		}

		speech = "";
		List<QuickReplyGeneral> opciones = new ArrayList<>();
		return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "", opciones);
	}

	public ResponseMessageApiAi activaChat(Usuario usuario) {
		// Activa Chat

		usuario.setChatActivo(true);
		usuario.setNoEntiendo(0);
		usuarioServicio.modificar(usuario);

		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.MOSTRAR_SERVICIOS);
		return armarMensajeRespuestaConServicios(servicios.getTexto(), servicios.isMostrarOpciones());
	}

	private ResponseMessageApiAi armarMensajeRespuestaConServicios(String speech, boolean mostrarServicios) {

		final TextMessage mensajeRespuesta = new TextMessage(speech);

		if (mostrarServicios) {
			final List<QuickReplyGeneral> quickReplies = servicioDao.obtenerActivos().stream().map(s -> {
				return new TextQuickReply(s.getNombre(), s.getPayload());
			}).collect(Collectors.toList());

			mensajeRespuesta.setQuick_replies(quickReplies);
		}

		return new ResponseMessageApiAi(speech, speech, new Data(mensajeRespuesta), null, "saludar");
	}

	public ResponseMessageApiAi solicitarAyudaHumana(Usuario usuario) {

		final InformacionUsuarioFacebook fb = ConsultarFacebook.obtenerInformacionDeUsuario(usuario.getIdFacebook(),
				propiedadesLola.facebookToken);

		if (!usuario.getUrlFotoPerfil().trim().equals(fb.getProfile_pic().trim())) {
			usuario.setUrlFotoPerfil(fb.getProfile_pic().trim());
			usuarioServicio.modificar(usuario);
		}

		String speech = usuario.getNombreFacebook() + ", deseas ayuda personalizada?";

		List<QuickReplyGeneral> opciones = new ArrayList<>();
		opciones.add(new TextQuickReply("Si", PAYLOAD_SOLICITA_AYUDA_HUMANA,
				"https://cdn.pixabay.com/photo/2012/04/01/17/43/buddy-23712_960_720.png"));
		opciones.add(new TextQuickReply("Seguir con Lola", PayloadConstantes.MOSTRAR_SERVICIOS,
				"https://cdn.pixabay.com/photo/2013/07/12/14/14/house-148033__340.png"));
		return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "", opciones);
	}

	public ResponseMessageApiAi solicitarDireccionUsuario(Result resultAi, Usuario usuario) {
		// String speech = "Por favor necesito conocer la ubicacion para la entrega.
		// Trata de ser lo mas exacto posible :)";
		String speech = "Antes de que escojas tu comida, necesito conocer la ubicación para la entrega :). \n \n*Por favor presiona \"Send Location\" ó \"Enviar Ubicación\" y confirma que esté activo tu GPS* 🗺 📍 ;)";
		Data data = new Data(new TextMessage(speech, Arrays.asList(new UbicacionQuickReply())));
		return new ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
	}

	public void marcarDireccionPrincipalComoUltima(Result resultAi, Usuario usuario) {
		String alias = "";
		try {
			alias = ApiAiUtil.obtenerValorParametro(resultAi, "alias", "pedir_comida");
		} catch (Exception err) {
			alias = "";
		}
		if (!alias.trim().equals(ubicacionUsuarioServicio.getGs_inAuxiliarParaDireccion().trim()))
			ubicacionUsuarioServicio.marcarUbicacionPrincipalComoUltima(usuario, alias);
	}

	public ResponseMessageApiAi calculcarUbicacion(Result resultAi, Usuario usuario) {
		String txtLatitud = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_LATITUD, CONTEXTO_FACEBOOK_LOCATION);
		String txtLongitud = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_LONGITUD, CONTEXTO_FACEBOOK_LOCATION);
		UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio
				.insertarBasadoEnGoogleMaps(Double.parseDouble(txtLatitud), Double.parseDouble(txtLongitud), usuario);

		// TODO: El mensaje sacar de la base de datos
		String speech = String.format(
				"	 %s, de acuerdo a lo que me indicas tú te encuentras por la calle %s. Más adelante te pediré más datos para la entrega :)",
				usuario.getNombreFacebook(), ubicacionUsuario.getCallePrincipalCalculada());

		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);

		return manejarRestaurantes.mostrarRestaurantes(resultAi, usuario);

	}

	public ResponseMessageApiAi solicitarCompletarDireccion(Result resultAi, Usuario usuario) {

		String idPedido = ApiAiUtil.obtenerValorParametro(resultAi, "idPedido", "pedir_comida");

		Long idProveedor = (long) 0;
		Long idPedidoLong = (long) 0;

		try {
			idPedidoLong = Long.parseLong(idPedido);
		} catch (Exception err) {
			idPedidoLong = (long) 0;
		}

		try {
			Pedido pedido = pedidoDao.obtenerPorId(idPedidoLong);
			idProveedor = pedido.getProveedor().getId();
		} catch (Exception err) {

		}

		String speech = String.format(
				"%s, ahora si necesito la información completa del lugar de entrega. Por favor haz click en 'Completar Dirección' :)",
				usuario.getNombreFacebook());
		UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
		List<ButtonGeneral> buttons = Arrays.asList(
				new WebUrlButton("Completar Dirección", UrlUtil.armarUrlCompletarDireccion(ubicacionUsuario.getId(),
						ubicacionUsuario.getToken(), "", idProveedor, idPedidoLong), true));
		Data data = new Data(new ButtonRichMessage(speech, buttons));

		return new ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
	}

	// ********************************************DialogFlowV2***************************************
	public ResponseMessageApiAiV2 activaChatV2(Usuario usuario) {
		// Activa Chat

		usuario.setChatActivo(true);
		usuario.setNoEntiendo(0);
		usuarioServicio.modificar(usuario);

		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.MOSTRAR_SERVICIOS);
		return armarMensajeRespuestaConServiciosV2(servicios.getTexto(), servicios.isMostrarOpciones());
	}

	private ResponseMessageApiAiV2 armarMensajeRespuestaConServiciosV2(String speech, boolean mostrarServicios) {

		final TextMessageV2 mensajeRespuesta = new TextMessageV2(speech);

		if (mostrarServicios) {
			final List<QuickReplyGeneral> quickReplies = servicioDao.obtenerActivos().stream().map(s -> {
				return new TextQuickReply(s.getNombre(), s.getPayload());
			}).collect(Collectors.toList());

			mensajeRespuesta.setQuick_replies(quickReplies);
		}

		return new ResponseMessageApiAiV2(speech, "saludar", new PayloadResponse(mensajeRespuesta), null);
	}

	public ResponseMessageApiAiV2 ayudaHumanaV2(Usuario usuario) {
		// Inactiva Chat
		usuario.setFechaInactivacion(DateTime.now().toDate());
		usuario.setChatActivo(false);
		usuarioServicio.modificar(usuario);

		String speech = "Listo " + usuario.getNombreFacebook() + " ya te ayudamos...";

		final TextMessage mensaje = new TextMessage(speech);
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje),
				propiedadesLola.facebookToken);

		UsuarioOperador usuarioOperadorTemp = usuarioOperadorDao.obtenerUltimoOperador(usuario);

		if (usuarioOperadorTemp != null) {
			try {
				MailUtil.enviarMail(usuarioOperadorTemp.getOperador().getEmail(),
						"El usuario " + usuario.toString() + " solicita ayuda personalizada",
						"El usuario " + usuario.toString() + " solicita ayuda personalizada, por favor ayudarlo.",
						false, "");
			} catch (Exception err) {

			}
		}

		speech = "";
		List<QuickReplyGeneral> opciones = new ArrayList<>();
		return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "", opciones);
	}

	public void marcarDireccionPrincipalComoUltimaV2(QueryResult resultAi, Usuario usuario) {
		String alias = "";
		try {
			alias = ApiAiUtil.obtenerValorParametroV2(resultAi, "alias.original", "pedir_comida");
		} catch (Exception err) {
			alias = "";
		}

		try {
			if (alias == "")
				alias = ApiAiUtil.obtenerValorParametroV2(resultAi, "alias", "pedir_comida");
		} catch (Exception err) {
			alias = "";
		}

		if(alias==null)
			alias="";
		System.out.println("Alias -----  " + alias);
		
		if (!alias.trim().equals(ubicacionUsuarioServicio.getGs_inAuxiliarParaDireccion().trim()))
			ubicacionUsuarioServicio.marcarUbicacionPrincipalComoUltima(usuario, alias);
		
	}

	public ResponseMessageApiAiV2 solicitarDireccionUsuarioV2(QueryResult resultAi, Usuario usuario) {
		// String speech = "Por favor necesito conocer la ubicacion para la entrega.
		// Trata de ser lo mas exacto posible :)";
		String speech = "Antes de que escojas tu comida, necesito conocer la ubicación para la entrega :). \n \n*Por favor presiona \"Send Location\" ó \"Enviar Ubicación\" y confirma que esté activo tu GPS* 🗺 📍 ;)";
		PayloadResponse data = new PayloadResponse(new TextMessageV2(speech, Arrays.asList(new UbicacionQuickReply())));
//		PayloadResponse data = new PayloadResponse(new TextMessageV2(speech, Arrays.asList(new UbicacionQuickReply())));

		return new ResponseMessageApiAiV2(speech, "validadorUsuario", data, null);
	}

	public ResponseMessageApiAiV2 solicitarDireccionUsuarioV3(QueryResult resultAi, Usuario usuario) {
		/*METODO ENCARGADO DE ENVIAR LOS PARÁMETROS DE GEOLOCALIZACION */
		String latitud = "";
		String longitud = "";
		
		/*SE OBTIENE EL PAYLOAD CORRESPONDIENTE A LA CAGTEGORÍA DEL SERVICIO (COMIDA, JUGUETES) */
		String payload = (String) resultAi.getOutputContexts().get(0).getParameters().get(CONTEXTO_FACEBOOK_SUBTIPO);
		try {
			UserLocation usuarioFacebook = ConsultarFacebook.obtenerUbicacionUsuario(usuario.getIdFacebook(),
					propiedadesLola.facebookToken);
			latitud = String.valueOf(usuarioFacebook.getLocation().getLatitude());
			longitud = String.valueOf(usuarioFacebook.getLocation().getLongitude());
		} catch (Exception e) {
			System.out.println(" ****************************************************************");
			System.out.println("*  ERROR:    NO SE PUDO OBTENER LA GEOLOCALIZAIÓN DEL USUARIO    *");
			System.out.println(" ****************************************************************");
		}
		/*SI NO ENCUENTRA LA LOCALIZACION ENVIA POR DEFECTO LA DIRECCION DE NUO */
		latitud = (latitud.trim().length() <= 0 ? "-0.156513" : latitud);
		longitud = (longitud.trim().length() <= 0 ? "-78.476008" : longitud);
		/*SE ARMA EL URL BUTTON CON EL LINK PARA LA GEOLOCALIZACIÓN */
		String speech = "Antes de que escojas tu comida, necesito conocer la ubicación para la entrega :). \n \n*Por favor presiona \"Send Location\" ó \"Enviar Ubicación\" y confirma que esté activo tu GPS* 🗺 📍 ;)";
//		List<ButtonGeneral> buttons = Arrays.asList(
//				new WebUrlButton("Ubicación", UrlUtil.urlGeoreferencia(usuario.getId(), latitud, longitud,payload), true));
//		PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
//		return new ResponseMessageApiAiV2(speech, "", data, null);
		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.VER_PERFIL);
		
		//UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
		List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
				UrlUtil.urlGeoreferencia(usuario.getId(), latitud, longitud,payload), true)); 
		PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
		return new ResponseMessageApiAiV2(speech, "", data, null);
		
		
	}

	public ResponseMessageApiAiV2 solicitarCompletarDireccionV2(QueryResult resultAi, Usuario usuario) {
		String idPedido = ApiAiUtil.obtenerValorParametroV2(resultAi, "idPedido.original", "pedir_comida");

		Long idProveedor = (long) 0;
		Long idPedidoLong = (long) 0;

		try {
			idPedidoLong = Long.parseLong(idPedido);
		} catch (Exception err) {
			idPedidoLong = (long) 0;
		}

		try {
			Pedido pedido = pedidoDao.obtenerPorId(idPedidoLong);
			idProveedor = pedido.getProveedor().getId();
		} catch (Exception err) {

		}

		String speech = String.format(
				"%s, ahora si necesito la información completa del lugar de entrega. Por favor haz click en 'Completar Dirección' :)",
				usuario.getNombreFacebook());
		UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
		List<ButtonGeneral> buttons = Arrays.asList(
				new WebUrlButton("Completar Dirección", UrlUtil.armarUrlCompletarDireccion(ubicacionUsuario.getId(),
						ubicacionUsuario.getToken(), "", idProveedor, idPedidoLong), true));
		PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));

		return new ResponseMessageApiAiV2(speech, "validadorUsuario", data, null);
	}

	public ResponseMessageApiAiV2 calculcarUbicacionV2(QueryResult resultAi, Usuario usuario) {
		String txtLatitud = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_LATITUD, CONTEXTO_FACEBOOK_LOCATION);
		String txtLongitud = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_LONGITUD, CONTEXTO_FACEBOOK_LOCATION);
		UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio
				.insertarBasadoEnGoogleMaps(Double.parseDouble(txtLatitud), Double.parseDouble(txtLongitud), usuario);
		// TODO: El mensaje sacar de la base de datos
		String speech = String.format(
				"Muy bien %s, de acuerdo a lo que me indicas tú te encuentras por la calle %s. Más adelante te pediré más datos para la entrega :)",
				usuario.getNombreFacebook(), ubicacionUsuario.getCallePrincipalCalculada());
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);
		return manejarRestaurantes.mostrarRestaurantesV3(resultAi, usuario);

	}

	public ResponseMessageApiAiV2 validarDireccionUsuarioV2(QueryResult resultAi, Usuario usuario) {
		List<String> listaAliasUbicacion = usuario != null ? ubicacionUsuarioServicio.obtenerAlias(usuario)
				: new ArrayList<String>();

		UbicacionUsuario principal = ubicacionUsuarioServicio.obtenerPrincipalUsuario(usuario);
		if ((listaAliasUbicacion == null || listaAliasUbicacion.size() <= 0) && principal == null) {
			return solicitarDireccionUsuarioV3(resultAi, usuario);
		}

		if (principal == null || principal.getCallePrincipal() == null || principal.getNumeracion() == null) {
			return solicitarDireccionUsuarioV3(resultAi, usuario);
		}

		final String direccionUsuario = principal != null
				? String.format((principal.getAlias() != null && principal.getAlias().trim().length() > 0
						? "(" + principal.getAlias().trim() + ") "
						: "") + "%s %s...?", principal.getCallePrincipal(), principal.getNumeracion())
				: "";
		String speech = String.format("La entrega será en: " + direccionUsuario);

		List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();

		List<QuickReplyGeneral> listaPersonasTem = new ArrayList<QuickReplyGeneral>();

		if (principal != null)
			listaPersonasTem.add(new TextQuickReply("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL -1"));

		for (String direccion : listaAliasUbicacion) {
			if (direccion != null && direccion.trim().length() > 0)
				listaPersonasTem.add(
						new TextQuickReply(direccion.trim(), "USAR_DIRECCION_PRINCIPAL " + direccion.trim() + " -1"));
		}

		listaPersonasTem.add(new TextQuickReply("Otra dirección", "SOLICITAR_NUEVA_DIRECCION_USUARIO"));

		return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "Direccion", listaPersonasTem);

		/*
		 * List<ButtonGeneral> buttons = Arrays.asList(new
		 * PostbackButton("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL"), new
		 * PostbackButton("No, en otra", "SOLICITAR_NUEVA_DIRECCION_USUARIO")); Data
		 * data = new Data(new ButtonRichMessage(speech, buttons)); return new
		 * ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
		 */
	}
	
	public ResponseMessageApiAiV2 validarDireccionUsuarioV3(QueryResult resultAi, Usuario usuario) {
		UserLocation ubicacionActualUsuario = new UserLocation();;
		try {
			ubicacionActualUsuario = ConsultarFacebook.obtenerUbicacionUsuario(usuario.getIdFacebook(),
					propiedadesLola.facebookToken);
			if(ubicacionActualUsuario.getLocation().getStreet()!=null || ubicacionActualUsuario.getLocation().getStreet().length()>0)
				System.out.println("Direccion Obtenida");
		} catch (Exception e) {
			System.out.println(" ****************************************************************");
			System.out.println("*  ERROR:    NO SE PUDO OBTENER LA GEOLOCALIZAIÓN DEL USUARIO    *");
			System.out.println(" ****************************************************************");
			ubicacionActualUsuario=null;
		}
		
		List<String> listaAliasUbicacion = usuario != null ? ubicacionUsuarioServicio.obtenerAlias(usuario)
				: new ArrayList<String>();

		UbicacionUsuario principal = ubicacionUsuarioServicio.obtenerPrincipalUsuario(usuario);
//		if ((listaAliasUbicacion == null || listaAliasUbicacion.size() <= 0) && principal == null) {
//			return solicitarDireccionUsuarioV3(resultAi, usuario);
//		}

		
		if (principal == null) {
			principal= new UbicacionUsuario();
			if(ubicacionActualUsuario==null) {
				principal.setCallePrincipal("Av. San Jose del Inca");
				principal.setNumeracion("449");
				principal.setLongitud(-0.156820);
				principal.setLatitud(-78.475997);
			}
			else {
				principal.setCallePrincipal((ubicacionActualUsuario.getLocation().getStreet()));
				principal.setLongitud(ubicacionActualUsuario.getLocation().getLongitude());
				principal.setLatitud(ubicacionActualUsuario.getLocation().getLatitude());
				principal.setCiudad(ubicacionActualUsuario.getLocation().getCity());
				principal.setPais(ubicacionActualUsuario.getLocation().getCountry());
			}
		}
		final String direccionUsuario = principal != null
				? String.format((principal.getAlias() != null && principal.getAlias().trim().length() > 0
						? "(" + principal.getAlias().trim() + ") "
						: "") + "%s %s...?", (principal.getCallePrincipal()==null ? principal.getCallePrincipalCalculada():principal.getCallePrincipal()), (principal.getNumeracion()==null ? "":principal.getNumeracion()))
				: "";
		String speech = String.format("La entrega será en: " + direccionUsuario);
		List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
		List<QuickReplyGeneral> listaPersonasTem = new ArrayList<QuickReplyGeneral>();
		if (principal != null)
			listaPersonasTem.add(new TextQuickReply("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL -1"));

		for (String direccion : listaAliasUbicacion) {
			if (direccion != null && direccion.trim().length() > 0)
				listaPersonasTem.add(
						new TextQuickReply(direccion.trim(), "USAR_DIRECCION_PRINCIPAL " + direccion.trim() + " -1"));
		}
		listaPersonasTem.add(new TextQuickReply("Otra dirección", "SOLICITAR_NUEVA_DIRECCION_USUARIO"));
		return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "Direccion", listaPersonasTem);
		
		
	}

}
