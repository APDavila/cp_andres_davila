package com.componente_practico.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.componente_practico.cine.ejb.servicio.ChilkatServicio;
import com.holalola.cine.ejb.modelo.Pelicula;
import com.holalola.webhook.ejb.modelo.Servicio;
import com.holalola.webhook.ejb.modelo.Servicios;

public class UrlUtil {
	
	public static final String SERVICIO_COMIDA = "C";
	public static final String SERVICIO_RESERVA = "R";
	private static final String baseUrl = System.getProperty("lola.base.url");
	private static final String baseUrlImg = System.getProperty("lola.base.urlimg");
	public static final String localImagesUrl = baseUrlImg + "images/";
	//private static final String dropboxUrl = "https://www.dropbox.com/sh/tyxtz6ecbihmqfq/";
	
	public static String armarUrlImagenClima(String imagen) throws UnsupportedEncodingException {
		return localImagesUrl + "weather/compuesta/" + codificarValor(imagen);
	}
	
	public static String armarUrlImagenDia(int dia) {
		return localImagesUrl + "dias/" + dia + ".png";
	}
	
	public static String armarUrlCartelCine(Pelicula pelicula) {
		if (pelicula.getUrlImagenLola() == null) return pelicula.getUrlImagen();
		
		return localImagesUrl + "cine/" + pelicula.getUrlImagenLola();
	}
	
	public static String armarUrlCompletarDireccion(long direccionId, String token, String payload, long al_idProveedor, long idPedido) {
		return baseUrl + "webviews/facebook/completarDireccion.jsf?dir="+direccionId+"&token="+codificarValor(token)+"&tar="+payload.trim()+"&id_prov="+al_idProveedor+"&idPed="+idPedido;
	}
	
	public static String armarUrlLogin(Long idUsuario, Long idProveedor) {
		return baseUrl + "webviews/facebook/login.jsf?iu="+idUsuario+"&ip="+idProveedor;
	}
	
	public static String armarUrlDatosFacturacion(long pedidoId, String token) {
		return baseUrl + "webviews/facebook/datosFactura.jsf?pedido="+pedidoId+"&token="+token;
	}
	
	public static String armarUrlPuestosPelicula(long pedidoId, String token) {
		return baseUrl + "webviews/cine/index.jsf?pedido="+pedidoId+"&token="+token;
	}
	
	public static String armarUrlTrailerPelicula(String movienToken) {
		return baseUrl + "webviews/cine/trailer.jsf?token=" + movienToken;
	}
	
	public static String armarUrlTerminosCondicionesPlaceToPay() {
		return baseUrl + "webviews/facebook/TCPlaceToPay.jsf";
	}
	
	public static String armarUrlPreguntasFrecuentesPlaceToPay() {
		return baseUrl + "webviews/facebook/PreguntasFrecuentes.jsf";
	}
	
	
	public static String armarUrlPaginaPayPhonePagoPedido(Long idPedido, String as_Ttoken, String as_sevicio, Integer ai_anio, Integer ai_mes, Integer ai_dia, Integer ai_hora, Integer ai_minuto, int mitutosDuracion) {
		//System.out.println(baseUrl + "webviews/pagoPayPhone/pago.jsf?token=" + as_Ttoken.trim() +"&ids="+idPedido+"&fa="+ai_anio+"&fm="+ai_mes+"&fd="+ai_dia+"&fh="+ai_hora+"&fmi="+ai_minuto+"&me=" + mitutosDuracion);
		if(as_sevicio.trim().equals(SERVICIO_COMIDA))
			return baseUrl + "webviews/pagoPayPhone/pago.jsf?token=" + as_Ttoken.trim() +"&ids="+idPedido+"&fa="+ai_anio+"&fm="+ai_mes+"&fd="+ai_dia+"&fh="+ai_hora+"&fmi="+ai_minuto+"&me=" + mitutosDuracion;
		else
			return baseUrl + "webviews/pagoPayPhone/pagoReservas.jsf?token=" + as_Ttoken.trim() +"&ids="+idPedido; //No esta hecho aun 
	}
	
	public static String armarUrlPaginaExternaPagoPedido(Long idPedido, String as_pc, String as_sevicio) {
		//System.out.println(baseUrl + "webviews/facebook/pago.jsf?token=" + idPedido+"&pc="+as_pc.trim());
		if(as_sevicio.trim().equals(SERVICIO_COMIDA))
			return baseUrl + "webviews/facebook/pago.jsf?token=" + idPedido+"&pc="+as_pc.trim();
		else
			return baseUrl + "webviews/facebook/pagoReservas.jsf?token=" + idPedido+"&pc="+as_pc.trim();
	}
	
	public static String armarUrlGeneraEntradas(Long al_idEvento, String as_localidad, int ai_totalAsientos, Long al_idUsuario, boolean ab_esWeb) {
		SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
		return baseUrl + "webviews/facturas/procesaEntradas.jsf?iev="+ al_idEvento +"&iloc="+as_localidad.trim()+"&numasie="+ai_totalAsientos+"&token="+al_idUsuario+"&web="+(ab_esWeb ? 1 : 0) + 
				"&cliResp=0&tokensg="+formatter.format(new Date());
	}
	
    public static String armarUrlVerPerfil(Long idUsuario) {
        return baseUrl + "webviews/facebook/verPerfil.jsf?iu="+idUsuario;
    }
    
    public static String verSIGE(Long idUsuario) {
        return baseUrl + "webviews/facebook/sige.jsf?iu="+idUsuario;
    }
    
    
    public static String verFormulario(Long idUsuario) {
        return baseUrl + "webviews/facebook/formulario.jsf?iu="+idUsuario;
    }
    
    public static String armarUrlIniciarCompra(Long idUsuario, String idProveedor,String payload, Servicio servicio) {
//    	System.out.println("----------------------------------armarUrlVerPerfil  ="+baseUrl + "webviews/facebook/iniciarCompra.jsf?iu="+idUsuario+"&ir="+idProveedor);
    	if (servicio.getId().equals(Servicios.COMIDA.getServicio().getId()))
    		return baseUrl + "webviews/facebook/iniciarCompra.jsf?iu="+idUsuario+"&ir="+idProveedor+"&pl="+payload;
    	else if(servicio.getId().equals(Servicios.ECOMMERCE.getServicio().getId()))
    		return baseUrl + "webviews/ecommerce/inicioEcommerce.jsf?iu="+idUsuario+"&ip="+idProveedor;
    	else
    		return baseUrl + "webviews/facebook/iniciarCompra.jsf?iu="+idUsuario+"&ir="+idProveedor+"&pl="+payload;
    }
    
    public static String verMallaCurricular(Long idUsuario, String idProveedor,String payload, Servicio servicio) {
    		return baseUrl + "webviews/facebook/mallaCurricular.jsf?iu="+idUsuario+"&ip="+idProveedor;
    }

    public static String urlGeoreferencia(Long idUsuario, String latitud, String longitud, String payload) {
    	/*METODO CON LA URL DE LA GEOREFERENCIA, SE ENVÍA LOS PARAMETROS DE LONGITUD Y LATITUD */
//    	System.out.println("----------------------------------armarUrlVerPerfil  ="+baseUrl + "webviews/facebook/georeferencia.jsf?usu="+idUsuario);
    	return baseUrl + "webhook/facebook/georeferencia.jsf?usu="+idUsuario+"&lat="+latitud+"&lng="+longitud+"&pld="+payload;
    }
    
    public static String armarUrlInicioMulticines(long supercinesId, long ciudadId,long proveedorId,long ususarioId) {
//    	System.out.println("----------------------------------armarUrlVerPerfil  ="+baseUrl + "webviews/facebook/iniciarCompra.jsf?iu");
    	return baseUrl + "multicines/facebook/inicioMulticines.jsf?coId="+supercinesId+"&ciId="+ciudadId+"&pro="+proveedorId+"&usu="+ususarioId+"&op="+"cartelera";
    }
    
    public static String armarUrlCompletarDatosReserva(Long idUsuario, Long idRestaurante, Long idRestauranteReservas) {
        return baseUrl + "webviews/facebook/completarDatosReserva.jsf?iu="+idUsuario+"&ir="+idRestaurante+"&irr="+idRestauranteReservas;
    }
    
    public static String armarUrlVerInformacionRestaurante(Long idUsuario, Long idRestaurante) {
        return baseUrl + "webviews/facebook/verInformacionRestauranteReserva.jsf?iu="+idUsuario+"&ir="+idRestaurante;
    }

	
	private static String codificarValor(String valor) {
		try {
			return URLEncoder.encode(valor, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return valor;
		}
	}

}
