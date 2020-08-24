package com.componente_practico.util;

import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.localizador.client.ValidadorLocalizacionUsuario;
import com.holalola.localizador.client.vo.ZonaVo;

public class ValidadorLocalizacionUsuarioUtil {
	

	public static boolean tieneCobertura(UbicacionUsuario ubicacionUsuario, long proveedorId) {
		return ValidadorLocalizacionUsuario.tieneCobertura(ubicacionUsuario.getLatitud(), ubicacionUsuario.getLongitud(), proveedorId);
	}
	
	public static ZonaVo obtenerZonaCobertura(UbicacionUsuario ubicacionUsuario, long proveedorId) {
		return ValidadorLocalizacionUsuario.obtenerZonaCobertura(ubicacionUsuario.getLatitud(), ubicacionUsuario.getLongitud(), proveedorId);
	}
	
	public static void enviarMensajeNoCobertura(UbicacionUsuario ubicacionUsuario, String facebookToken) {
		final Usuario usuario = ubicacionUsuario.getUsuario();
		ValidadorLocalizacionUsuario.enviarMensajeNoCobertura(
				ubicacionUsuario.getCallePrincipal(),
				ubicacionUsuario.getNumeracion(),
				usuario.getNombreFacebook(),
				usuario.getIdFacebook(),
				facebookToken);
	}

}
