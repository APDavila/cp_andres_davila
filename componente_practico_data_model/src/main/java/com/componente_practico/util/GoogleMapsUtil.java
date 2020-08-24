package com.componente_practico.util;

import static com.componente_practico.util.TextoUtil.esVacio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.holalola.googlemaps.vo.DireccionGoogleMaps;

public class GoogleMapsUtil {

	static final Logger log = LoggerFactory.getLogger(GoogleMapsUtil.class);
	// TODO: Poner en propiedades y crear un key propio par holalola
	private static final GeoApiContext context = new GeoApiContext()
			.setApiKey("AIzaSyBsYVh4jkgxmtOwsg7IkrjmXtQxHQMQik4");

	public static DireccionGoogleMaps obtenerDireccion(double latitud, double longitud) {
		DireccionGoogleMaps direccion = new DireccionGoogleMaps(latitud, longitud);
		try {
			GeocodingResult[] results = GeocodingApi.newRequest(context).latlng(new LatLng(latitud, longitud)).await();

			String textoRespuesta = null;
			for (GeocodingResult result : results) {
				textoRespuesta = result.formattedAddress;
				if (result.types != null && !(esVacio(textoRespuesta))) {
					//System.out.println("Texto Respuesta " + textoRespuesta);
					
					textoRespuesta = textoRespuesta.split(",")[0];
					for (AddressType at : result.types) {
						//System.out.println("\t"+at.name());
						if (AddressType.ROUTE.equals(at) || AddressType.STREET_ADDRESS.equals(at)) {
							direccion.setCallePrincipal(textoRespuesta);
							break;
						}
						if (AddressType.NEIGHBORHOOD.equals(at)) {
							direccion.setBarrio(textoRespuesta);
							break;
						}
						if (AddressType.SUBLOCALITY.equals(at)) {
							direccion.setParroquia(textoRespuesta);
							break;
						}
						if (AddressType.LOCALITY.equals(at)) {
							direccion.setCiudad(textoRespuesta);
							break;
						}
						if (AddressType.POSTAL_CODE.equals(at)) {
							direccion.setCodigoPostal(textoRespuesta);
							break;
						}
						if (AddressType.ADMINISTRATIVE_AREA_LEVEL_1.equals(at)) {
							direccion.setProvincia(textoRespuesta);
							break;
						}
						if (AddressType.COUNTRY.equals(at)) {
							direccion.setPais(textoRespuesta);
							break;
						}

					}
				}

			}
		} catch (Exception e) {
			log.error("No se pudo obtener la direccion en base a coordenadas en google maps", e);
		}
		return direccion;
	}
}
