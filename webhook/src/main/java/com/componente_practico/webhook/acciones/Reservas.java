package com.componente_practico.webhook.acciones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.collections4.ListUtils;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.reservas.dao.ReservasDao;
import com.holalola.reservas.modelo.CategoriaCiudad;
import com.holalola.reservas.modelo.CiuProvBusquedaReserva;
import com.holalola.reservas.modelo.Ciudades;
import com.holalola.reservas.modelo.Restaurante;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.ejb.modelo.Servicio;
import com.holalola.webhook.ejb.modelo.Servicios;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.ButtonRichMessageV2;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;

import ai.api.model.Result;

@Stateless
public class Reservas {
	
	private static final String CONTEXTO_PROCESA_RESERVA = "reservas";
	private static final String CODIGO_CIUDAD = "idCodigoCiudad.original";
	private static final String CODIGO_CATEGORIA = "idCodigoCategoria.original";
	private static final String NIVEL_CATEGORIA = "idNivel.original";
	private static final String NIVEL_RESTAURANTE = "idNivel.original";
	private static final String ID_PROVEEDOR = "idProveedor.original";
	private static final String NOMBRE_RESTAURANTE = "nombreRestaurante.original";
	private static final String NOMBRE_CIUDAD = "nombreCiudad.original";
	
	public Proveedor proveedor;
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	@EJB
	RespuestaDao respuestaDao;
	
	@EJB
	ReservasDao reservasDao;
	
	@EJB
	ProveedorDao proveedorDao;
	
	public ResponseMessageApiAi mostrarCiudades(Result resultAi, Usuario usuario) {
		
		proveedor= new Proveedor();
		
		String idProveedor = ApiAiUtil.obtenerValorParametro(resultAi, ID_PROVEEDOR, CONTEXTO_PROCESA_RESERVA);
		proveedor=proveedorDao.obtenerPorId(new Long(idProveedor));
		
		List<Ciudades> listaNombreCiudades = reservasDao.obtenerActivas(proveedor);
		
		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS);
		String speech = String.format(
				servicios.getTexto(),
					usuario.getNombreFacebook());
		
		
		List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
		
		List<QuickReplyGeneral> listaCiudadesTem = new ArrayList<QuickReplyGeneral>();
		
		for (Ciudades ciudades : listaNombreCiudades) {
			if(ciudades.getCiudad().getNombre() != null && ciudades.getCiudad().getNombre().trim().length() > 0)
				listaCiudadesTem.add(new TextQuickReply(ciudades.getCiudad().getNombre().trim(), "USAR_CODIGO_CIUDAD " + ciudades.getCodigoCiudad().toString().trim()+" "+"-1")); 
		}
		
		return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "Direccion", listaCiudadesTem);
	}
	
//Reservas Alex 12/10/2018
public ResponseMessageApiAi mostrarCiudadesBuscar(Result resultAi, Usuario usuario) {
		
		
		
		String nombreRestaurante = ApiAiUtil.obtenerValorParametro(resultAi, NOMBRE_RESTAURANTE, "");
		
		List<CiuProvBusquedaReserva> listaNombreCiudades = reservasDao.obtenerCiudadesProveedoresReserva(nombreRestaurante);
		
		if (listaNombreCiudades != null && listaNombreCiudades.size()>0) {
		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS);
		String speech = String.format(
				servicios.getTexto(),
					usuario.getNombreFacebook());
		
		
		List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
		
		List<QuickReplyGeneral> listaCiudadesTem = new ArrayList<QuickReplyGeneral>();
		
		for (CiuProvBusquedaReserva ciuProvBusquedaReserva : listaNombreCiudades) {
			if(ciuProvBusquedaReserva.getNombre() != null && ciuProvBusquedaReserva.getNombre().trim().length() > 0)
				listaCiudadesTem.add(new TextQuickReply(ciuProvBusquedaReserva.getNombre().trim(), "USAR_NOMBRE_CIUDAD_RESTAURANTE "+nombreRestaurante.trim()+" "+ciuProvBusquedaReserva.getNombre().trim()+" "+"-1")); 
		}
		
		return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "Direccion", listaCiudadesTem);}
		else {
			String speech = String.format("%s, %s no esta disponible para reservar en ninguna de nuestras ciudades", usuario.getNombreFacebook(), nombreRestaurante);
			return ApiAiUtil.armarRespuestaTextMessage(speech, "mostrarCiudadesBusqueda");
		}
	}
	//Fin
public ResponseMessageApiAi mostrarCategorias(Result resultAi, Usuario usuario) {
		
	String codigoCiudad = ApiAiUtil.obtenerValorParametro(resultAi, CODIGO_CIUDAD, CONTEXTO_PROCESA_RESERVA);
	String nivel = ApiAiUtil.obtenerValorParametro(resultAi, NIVEL_CATEGORIA, CONTEXTO_PROCESA_RESERVA);
	
		String mensaje = "%s, en que categoria deseas ver los restaurantes?";
		return armarGaleriaPorCiudad(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Ver", Integer.parseInt(nivel), codigoCiudad);
	}

private ResponseMessageApiAi armarGaleriaPorCiudad(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String codigoCiudad) {
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantes(usuario, reservasDao.obtenerCategoriasPorCiudad(new Integer(codigoCiudad)), as_textoVerMenu, nivel, "USAR_CODIGO_CIUDAD", codigoCiudad);
}


public ResponseMessageApiAi armarGaleriaRestaurantes(Usuario usuario, List<CategoriaCiudad> categoriaCiudad, String as_textoVerMenu, int nivel, String menuPorMostrar, String codigoCiudad) {
	
	String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
 
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
	
	List<Element> elementosCategoria = categoriaCiudad.stream().map(p -> {
		
		ButtonGeneral button;
		
		List<ButtonGeneral> lista = new ArrayList();
		
				button = new PostbackButton(al_textoVerMenu,
						String.format("USAR_CODIGO_CATEGORIA %s %s %s", "-1", p.getCategoria_id().toString().trim(), codigoCiudad));
			

			lista.add(button);
			
		return new Element(p.getNombre(), p.getImagen(), "", lista);
		

	}).collect(Collectors.toList());
	
	
	List<List<Element>> elementos = ListUtils.partition(elementosCategoria, 9);

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
		
		buttons.add(new PostbackButton(String.format("Más categorias"),
				String.format("%s %s %s",menuPorMostrar, codigoCiudad,
						nivel+1)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplate(elementosVer, "Galeria de restaurantes",
			source);
}


public ResponseMessageApiAi mostrarRestaurantes(Result resultAi, Usuario usuario) {
	
	String codigoCiudad = ApiAiUtil.obtenerValorParametro(resultAi, CODIGO_CIUDAD, CONTEXTO_PROCESA_RESERVA);
	String codigoCategoria = ApiAiUtil.obtenerValorParametro(resultAi, CODIGO_CATEGORIA, CONTEXTO_PROCESA_RESERVA);
	String idNivel = ApiAiUtil.obtenerValorParametro(resultAi, NIVEL_RESTAURANTE, CONTEXTO_PROCESA_RESERVA);

		String mensaje = "%s, estos son los restaurantes disponibles para reservar";
		return armarGaleriaPorRestaurante(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Reservar", Integer.parseInt(idNivel), codigoCiudad, codigoCategoria);
	}


private ResponseMessageApiAi armarGaleriaPorRestaurante(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String codigoCiudad, String codigoCategoria) {
	List<Restaurante> restauranteCiudadCategoria = new ArrayList<>();
	restauranteCiudadCategoria=reservasDao.obtenerPorCiudadCategoria(new Long(codigoCiudad) , new Long(codigoCategoria));
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantesPorCategoria(usuario, restauranteCiudadCategoria, as_textoVerMenu, nivel, "USAR_CODIGO_CATEGORIA", codigoCiudad, codigoCategoria);
}


public ResponseMessageApiAi armarGaleriaRestaurantesPorCategoria(Usuario usuario, List<Restaurante> restauranteCiudadCategoria, String as_textoVerMenu, int nivel, String menuPorMostrar, String codigoCiudad, String codigoCategoria) {
	
	String source = String.format("armarGaleriaRestaurantesPorCategoria %s", menuPorMostrar);
 
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";

	
	List<Element> elementosRestaurantes = restauranteCiudadCategoria.stream().map(p -> {
		
		WebUrlButton button;
		WebUrlButton buttonInformacion;
		List<ButtonGeneral> lista = new ArrayList();

		
		buttonInformacion = new WebUrlButton("Ver información",
				UrlUtil.armarUrlVerInformacionRestaurante(usuario.getId(), new Long(p.getId())), true);
		
		button = new WebUrlButton("Reservar",
				UrlUtil.armarUrlCompletarDatosReserva(usuario.getId(), new Long(p.getId()), new Long(p.getIdRestaurante())), true);
				

		ButtonGeneral buttonHistorico =  new PostbackButton("Tus últimas reservas",
				String.format("VER_HIST_REST_RESERVAS " + p.getId()));
				

			lista.add(buttonInformacion);
			lista.add(button);
			lista.add(buttonHistorico);
		
		return new Element(p.getNombre(), p.getUrlImagen(), p.getDescripcion(), lista);
		

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
		
		buttons.add(new PostbackButton(String.format("Más Restaurantes"),
				String.format("%s %s %s %s",menuPorMostrar,
						nivel+1, codigoCiudad, codigoCategoria)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplate(elementosVer, "Galeria de restaurantes",
			source);
}

//Busqueda Restaurantes Alex 12/10/2018

public ResponseMessageApiAi mostrarRestaurantesBusqueda(Result resultAi, Usuario usuario) {
	
	String nombreRestaurante = ApiAiUtil.obtenerValorParametro(resultAi, NOMBRE_RESTAURANTE, "");
	String nombreCiudad = ApiAiUtil.obtenerValorParametro(resultAi, NOMBRE_CIUDAD, "");
	String idNivel = ApiAiUtil.obtenerValorParametro(resultAi, NIVEL_RESTAURANTE, "");

		String mensaje = "%s, estos son los restaurantes disponibles para reservar";
		return armarGaleriaPorRestauranteBusqueda(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Reservar", Integer.parseInt(idNivel), nombreRestaurante, nombreCiudad);
	}


private ResponseMessageApiAi armarGaleriaPorRestauranteBusqueda(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String nombreRestaurante, String nombreCiudad) {
	List<Restaurante> restauranteNombreCiudad = new ArrayList<>();
	restauranteNombreCiudad=reservasDao.obtenerPorNombreCiudadRestaurante(nombreRestaurante.toUpperCase().trim(), nombreCiudad.toUpperCase().trim());
	
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantesPorBusqueda(usuario, restauranteNombreCiudad, as_textoVerMenu, nivel, "USAR_NOMBRE_CIUDAD_RESTAURANTE", nombreRestaurante, nombreCiudad);
}


public ResponseMessageApiAi armarGaleriaRestaurantesPorBusqueda(Usuario usuario, List<Restaurante> restauranteNombreCiudad, String as_textoVerMenu, int nivel, String menuPorMostrar, String nombreRestaurante, String nombreCiudad) {
	
	String source = String.format("armarGaleriaRestaurantesPorCategoria %s", menuPorMostrar);
 
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";

	
	List<Element> elementosRestaurantes = restauranteNombreCiudad.stream().map(p -> {
		
		WebUrlButton button;
		WebUrlButton buttonInformacion;
		List<ButtonGeneral> lista = new ArrayList();

		
		buttonInformacion = new WebUrlButton("Ver información",
				UrlUtil.armarUrlVerInformacionRestaurante(usuario.getId(), new Long(p.getId())), true);
		
		button = new WebUrlButton("Reservar",
				UrlUtil.armarUrlCompletarDatosReserva(usuario.getId(), new Long(p.getId()), new Long(p.getIdRestaurante())), true);
				

		ButtonGeneral buttonHistorico =  new PostbackButton("Tus últimas reservas",
				String.format("VER_HIST_REST_RESERVAS " + p.getId()));
				

			lista.add(buttonInformacion);
			lista.add(button);
			lista.add(buttonHistorico);
		
		return new Element(p.getNombre(), p.getUrlImagen(), p.getDescripcion(), lista);
		

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
		
		buttons.add(new PostbackButton(String.format("Más Restaurantes"),
				String.format("%s %s %s %s",menuPorMostrar,nombreRestaurante, nombreCiudad, nivel+1)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplate(elementosVer, "Galeria de restaurantes",
			source);
}
//Fin busqueda restaurantes
public ResponseMessageApiAi verCompletarDatosReserva(Result resultAi, Usuario usuario) {
	
	
	String idRestaurante = ApiAiUtil.obtenerValorParametro(resultAi, "idCodigoRestaurante", "reservas");
	String idRestauranteReservas = ApiAiUtil.obtenerValorParametro(resultAi, "idReservasCodigoRestaurante", "reservas");
	
	final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS_COMPLETAR_DATOS);
	
	String speech = String.format(
		servicios.getTexto(),
			usuario.getNombreFacebook());
	//UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
	List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
			UrlUtil.armarUrlCompletarDatosReserva(usuario.getId(), new Long(idRestaurante), new Long(idRestauranteReservas)), true)); 
	Data data = new Data(new ButtonRichMessage(speech, buttons));
	return new ResponseMessageApiAi(speech, speech, data, null, "");
}


public ResponseMessageApiAi verInformacionRestauranteReserva(Result resultAi, Usuario usuario) {
	
	
	String idRestaurante = ApiAiUtil.obtenerValorParametro(resultAi, "idRestaurante", "reservas");
		
	final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS_VER_INFO_RESTAURANTE);
	
	String speech = String.format(
		servicios.getTexto(),
			usuario.getNombreFacebook());
	List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
			UrlUtil.armarUrlVerInformacionRestaurante(usuario.getId(), new Long(idRestaurante)), true));  
	Data data = new Data(new ButtonRichMessage(speech, buttons));
	return new ResponseMessageApiAi(speech, speech, data, null, "");
}

//*************************************DIALOGFLOW V2*******************************************************************
public ResponseMessageApiAiV2 mostrarCiudadesV2(QueryResult resultAi, Usuario usuario) {
	
	proveedor= new Proveedor();
	
	String idProveedor = ApiAiUtil.obtenerValorParametroV2(resultAi, ID_PROVEEDOR, CONTEXTO_PROCESA_RESERVA);
	proveedor=proveedorDao.obtenerPorId(new Long(idProveedor));
	
	List<Ciudades> listaNombreCiudades = reservasDao.obtenerActivas(proveedor);
	
	final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS);
	String speech = String.format(
			servicios.getTexto(),
				usuario.getNombreFacebook());
	
	
	List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
	
	List<QuickReplyGeneral> listaCiudadesTem = new ArrayList<QuickReplyGeneral>();
	
	for (Ciudades ciudades : listaNombreCiudades) {
		if(ciudades.getCiudad().getNombre() != null && ciudades.getCiudad().getNombre().trim().length() > 0)
			listaCiudadesTem.add(new TextQuickReply(ciudades.getCiudad().getNombre().trim(), "USAR_CODIGO_CIUDAD " + ciudades.getCodigoCiudad().toString().trim()+" "+"-1")); 
	}
	
	return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "Direccion", listaCiudadesTem);
}

public ResponseMessageApiAiV2 mostrarCategoriasV2(QueryResult resultAi, Usuario usuario) {
	
	String codigoCiudad = ApiAiUtil.obtenerValorParametroV2(resultAi, CODIGO_CIUDAD, CONTEXTO_PROCESA_RESERVA);
	String nivel = ApiAiUtil.obtenerValorParametroV2(resultAi, NIVEL_CATEGORIA, CONTEXTO_PROCESA_RESERVA);
	
		String mensaje = "%s, en que categoria deseas ver los restaurantes?";
		return armarGaleriaPorCiudadV2(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Ver", Integer.parseInt(nivel), codigoCiudad);
	}
private ResponseMessageApiAiV2 armarGaleriaPorCiudadV2(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String codigoCiudad) {
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantesV2(usuario, reservasDao.obtenerCategoriasPorCiudad(new Integer(codigoCiudad)), as_textoVerMenu, nivel, "USAR_CODIGO_CIUDAD", codigoCiudad);
}

public ResponseMessageApiAiV2 armarGaleriaRestaurantesV2(Usuario usuario, List<CategoriaCiudad> categoriaCiudad, String as_textoVerMenu, int nivel, String menuPorMostrar, String codigoCiudad) {
	
	String source = String.format("armarGaleriaRestaurantes %s", menuPorMostrar);
 
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";
	
	List<Element> elementosCategoria = categoriaCiudad.stream().map(p -> {
		
		ButtonGeneral button;
		
		List<ButtonGeneral> lista = new ArrayList();
		
				button = new PostbackButton(al_textoVerMenu,
						String.format("USAR_CODIGO_CATEGORIA %s %s %s", "-1", p.getCategoria_id().toString().trim(), codigoCiudad));
			

			lista.add(button);
			
		return new Element(p.getNombre(), p.getImagen(), "", lista);
		

	}).collect(Collectors.toList());
	
	
	List<List<Element>> elementos = ListUtils.partition(elementosCategoria, 9);

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
		
		buttons.add(new PostbackButton(String.format("Más categorias"),
				String.format("%s %s %s",menuPorMostrar, codigoCiudad,
						nivel+1)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, "Galeria de restaurantes",
			source);
}

public ResponseMessageApiAiV2 mostrarRestaurantesV2(QueryResult resultAi, Usuario usuario) {
	
	String codigoCiudad = ApiAiUtil.obtenerValorParametroV2(resultAi, CODIGO_CIUDAD, CONTEXTO_PROCESA_RESERVA);
	String codigoCategoria = ApiAiUtil.obtenerValorParametroV2(resultAi, CODIGO_CATEGORIA, CONTEXTO_PROCESA_RESERVA);
	String idNivel = ApiAiUtil.obtenerValorParametroV2(resultAi, NIVEL_RESTAURANTE, CONTEXTO_PROCESA_RESERVA);

		String mensaje = "%s, estos son los restaurantes disponibles para reservar";
		return armarGaleriaPorRestauranteV2(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Reservar", Integer.parseInt(idNivel), codigoCiudad, codigoCategoria);
	}
private ResponseMessageApiAiV2 armarGaleriaPorRestauranteV2(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String codigoCiudad, String codigoCategoria) {
	List<Restaurante> restauranteCiudadCategoria = new ArrayList<>();
	restauranteCiudadCategoria=reservasDao.obtenerPorCiudadCategoria(new Long(codigoCiudad) , new Long(codigoCategoria));
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantesPorCategoriaV2(usuario, restauranteCiudadCategoria, as_textoVerMenu, nivel, "USAR_CODIGO_CATEGORIA", codigoCiudad, codigoCategoria);
}

public ResponseMessageApiAiV2 armarGaleriaRestaurantesPorCategoriaV2(Usuario usuario, List<Restaurante> restauranteCiudadCategoria, String as_textoVerMenu, int nivel, String menuPorMostrar, String codigoCiudad, String codigoCategoria) {
	
	String source = String.format("armarGaleriaRestaurantesPorCategoria %s", menuPorMostrar);
 
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";

	
	List<Element> elementosRestaurantes = restauranteCiudadCategoria.stream().map(p -> {
		
		WebUrlButton button;
		WebUrlButton buttonInformacion;
		List<ButtonGeneral> lista = new ArrayList();

		
		buttonInformacion = new WebUrlButton("Ver información",
				UrlUtil.armarUrlVerInformacionRestaurante(usuario.getId(), new Long(p.getId())), true);
		
		button = new WebUrlButton("Reservar",
				UrlUtil.armarUrlCompletarDatosReserva(usuario.getId(), new Long(p.getId()), new Long(p.getIdRestaurante())), true);
				

		ButtonGeneral buttonHistorico =  new PostbackButton("Tus últimas reservas",
				String.format("VER_HIST_REST_RESERVAS " + p.getId()));
				

			lista.add(buttonInformacion);
			lista.add(button);
			lista.add(buttonHistorico);
		
		return new Element(p.getNombre(), p.getUrlImagen(), p.getDescripcion(), lista);
		

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
		
		buttons.add(new PostbackButton(String.format("Más Restaurantes"),
				String.format("%s %s %s %s",menuPorMostrar,
						nivel+1, codigoCiudad, codigoCategoria)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, "Galeria de restaurantes",
			source);
}

public ResponseMessageApiAiV2 verCompletarDatosReservaV2(QueryResult resultAi, Usuario usuario) {
	
	
	String idRestaurante = ApiAiUtil.obtenerValorParametroV2(resultAi, "idCodigoRestaurante.original", "reservas");
	String idRestauranteReservas = ApiAiUtil.obtenerValorParametroV2(resultAi, "idReservasCodigoRestaurante.original", "reservas");
	
	final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS_COMPLETAR_DATOS);
	
	String speech = String.format(
		servicios.getTexto(),
			usuario.getNombreFacebook());
	//UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
	List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
			UrlUtil.armarUrlCompletarDatosReserva(usuario.getId(), new Long(idRestaurante), new Long(idRestauranteReservas)), true)); 
	PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
	return new ResponseMessageApiAiV2(speech, "", data, null);
}

public ResponseMessageApiAiV2 verInformacionRestauranteReservaV2(QueryResult resultAi, Usuario usuario) {
	
	
	String idRestaurante = ApiAiUtil.obtenerValorParametroV2(resultAi, "idRestaurante.original", "reservas");
		
	final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS_VER_INFO_RESTAURANTE);
	
	String speech = String.format(
		servicios.getTexto(),
			usuario.getNombreFacebook());
	List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Ver",
			UrlUtil.armarUrlVerInformacionRestaurante(usuario.getId(), new Long(idRestaurante)), true));  
	PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));
	return new ResponseMessageApiAiV2(speech, "", data, null);
}

public ResponseMessageApiAiV2 mostrarCiudadesBuscarV2(QueryResult resultAi, Usuario usuario) {
	
	
	
	String nombreRestaurante = ApiAiUtil.obtenerValorParametroSCV2(resultAi, NOMBRE_RESTAURANTE, "");
	
	List<CiuProvBusquedaReserva> listaNombreCiudades = reservasDao.obtenerCiudadesProveedoresReserva(nombreRestaurante);
	
	if (listaNombreCiudades != null && listaNombreCiudades.size()>0) {
	final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.RESERVAS);
	String speech = String.format(
			servicios.getTexto(),
				usuario.getNombreFacebook());
	
	
	List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
	
	List<QuickReplyGeneral> listaCiudadesTem = new ArrayList<QuickReplyGeneral>();
	
	for (CiuProvBusquedaReserva ciuProvBusquedaReserva : listaNombreCiudades) {
		if(ciuProvBusquedaReserva.getNombre() != null && ciuProvBusquedaReserva.getNombre().trim().length() > 0)
			listaCiudadesTem.add(new TextQuickReply(ciuProvBusquedaReserva.getNombre().trim(), "USAR_NOMBRE_CIUDAD_RESTAURANTE "+nombreRestaurante.trim()+" "+ciuProvBusquedaReserva.getNombre().trim()+" "+"-1")); 
	}
	
	return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "Direccion", listaCiudadesTem);}
	else {
		String speech = String.format("%s, %s no esta disponible para reservar en ninguna de nuestras ciudades", usuario.getNombreFacebook(), nombreRestaurante);
		return ApiAiUtil.armarRespuestaTextMessageV2(speech, "mostrarCiudadesBusqueda");
	}
}

public ResponseMessageApiAiV2 mostrarRestaurantesBusquedaV2(QueryResult resultAi, Usuario usuario) {
	
	String nombreRestaurante = ApiAiUtil.obtenerValorParametroSCV2(resultAi, NOMBRE_RESTAURANTE, "");
	String nombreCiudad = ApiAiUtil.obtenerValorParametroSCV2(resultAi, NOMBRE_CIUDAD, "");
	String idNivel = ApiAiUtil.obtenerValorParametroSCV2(resultAi, NIVEL_RESTAURANTE, "");

		String mensaje = "%s, estos son los restaurantes disponibles para reservar";
		return armarGaleriaPorRestauranteBusquedaV2(mensaje, Servicios.RESERVAS.getServicio(), usuario, "Reservar", Integer.parseInt(idNivel), nombreRestaurante, nombreCiudad);
	}

private ResponseMessageApiAiV2 armarGaleriaPorRestauranteBusquedaV2(String mensaje, Servicio servicio, Usuario usuario, String as_textoVerMenu, int nivel, String nombreRestaurante, String nombreCiudad) {
	List<Restaurante> restauranteNombreCiudad = new ArrayList<>();
	restauranteNombreCiudad=reservasDao.obtenerPorNombreCiudadRestaurante(nombreRestaurante.toUpperCase().trim(), nombreCiudad.toUpperCase().trim());
	
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaRestaurantesPorBusquedaV2(usuario, restauranteNombreCiudad, as_textoVerMenu, nivel, "USAR_NOMBRE_CIUDAD_RESTAURANTE", nombreRestaurante, nombreCiudad);
}

public ResponseMessageApiAiV2 armarGaleriaRestaurantesPorBusquedaV2(Usuario usuario, List<Restaurante> restauranteNombreCiudad, String as_textoVerMenu, int nivel, String menuPorMostrar, String nombreRestaurante, String nombreCiudad) {
	
	String source = String.format("armarGaleriaRestaurantesPorCategoria %s", menuPorMostrar);
 
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0 ? as_textoVerMenu.trim() : "Ver Menú";

	
	List<Element> elementosRestaurantes = restauranteNombreCiudad.stream().map(p -> {
		
		WebUrlButton button;
		WebUrlButton buttonInformacion;
		List<ButtonGeneral> lista = new ArrayList();

		
		buttonInformacion = new WebUrlButton("Ver información",
				UrlUtil.armarUrlVerInformacionRestaurante(usuario.getId(), new Long(p.getId())), true);
		
		button = new WebUrlButton("Reservar",
				UrlUtil.armarUrlCompletarDatosReserva(usuario.getId(), new Long(p.getId()), new Long(p.getIdRestaurante())), true);
				

		ButtonGeneral buttonHistorico =  new PostbackButton("Tus últimas reservas",
				String.format("VER_HIST_REST_RESERVAS " + p.getId()));
				

			lista.add(buttonInformacion);
			lista.add(button);
			lista.add(buttonHistorico);
		
		return new Element(p.getNombre(), p.getUrlImagen(), p.getDescripcion(), lista);
		

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
		
		buttons.add(new PostbackButton(String.format("Más Restaurantes"),
				String.format("%s %s %s %s",menuPorMostrar,nombreRestaurante, nombreCiudad, nivel+1)));
	

		elementosVer.add(new Element("Ver Más", System.getProperty("lola.imagenVerMas") , "", buttons));
	} else {
		
	}
	

	return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, "Galeria de restaurantes",
			source);
}


}
