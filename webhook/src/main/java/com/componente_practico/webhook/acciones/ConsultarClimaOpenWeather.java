package com.componente_practico.webhook.acciones;


import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_FIGURA;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_FONDO;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_HORAS;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_TEMPERATURA;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.webhook.abstracto.ConsultarClimaAbstracto;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.ejb.servicio.ClimaServicio;
import com.componente_practico.webhook.model.openweather.Forecast;
import com.componente_practico.webhook.model.openweather.MainWeatherData;
import com.componente_practico.webhook.model.openweather.OpenWeatherActual;
import com.componente_practico.webhook.model.openweather.OpenWeatherForecast;
import com.componente_practico.webhook.model.openweather.WeatherDescription;
import com.componente_practico.webhook.v2.QueryResult;
import com.componente_practico.webhook.vo.ComposicionImagenClima;
import com.componente_practico.webhook.vo.FilaClima;
import com.google.gson.Gson;
import com.holalola.webhook.ejb.modelo.Clima;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;

import ai.api.GsonFactory;
import ai.api.model.Result;

@Stateless
public class ConsultarClimaOpenWeather extends ConsultarClimaAbstracto {
	
	static final Logger log = LoggerFactory.getLogger(ConsultarClimaOpenWeather.class);
	
	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();
	
	private static final String formatoUrlOpenWeather = "http://api.openweathermap.org/data/2.5/%s?appid=cb7c9891f067fab623e8fddead9b774d&units=metric&lang=es&q="; 
	private static final String urlForecast = String.format(formatoUrlOpenWeather, "forecast");
	private static final String urlClimaActual = String.format(formatoUrlOpenWeather, "weather");
	
	private static final String source = "OpenWeather";
	
	@EJB
	private ClimaServicio climaServicio;
	
	public ResponseMessageApiAi consultarForecast(Result resultAi) {
		try {
			final String ciudad = obtenerParametroCiudad(resultAi);
			
			ResponseMessageApiAi result;
			if ((result = validarClimaEnBD(climaServicio.validarClima(ciudad, TIPO_POR_HORAS), source))!= null) return result;
			
			StringBuffer urlConsultar = new StringBuffer(urlForecast).append(URLEncoder.encode(ciudad, "UTF-8"));
		
			//Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class)); // jersey
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(urlConsultar.toString());

			Response response = webTarget.request().get(Response.class);
			if (response.getStatus() == 200) {
				String openWeatherResponse = response.readEntity(String.class);
				return armarGraficaClima(GSON.fromJson(openWeatherResponse, OpenWeatherForecast.class), ciudad, openWeatherResponse);
			}
			
			throw new Exception(String.format("OpenWeather status: %s", response.getStatus()));

		} catch (Exception e) {
			log.error("No se pudo consultar OpenWeather forecast", e);
			final String speech = "Fallo consulta de clima";
			return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage("Tuvimos un problema al consulta el clima")), null, source);
		}
	}
	
	public ResponseMessageApiAi consultarClimaActual(Result resultAi) {
		try {
			final String ciudad = obtenerParametroCiudad(resultAi);
			StringBuffer urlConsultar = new StringBuffer(urlClimaActual).append(URLEncoder.encode(ciudad, "UTF-8"));
			
			ResponseMessageApiAi result;
			if ((result = validarClimaEnBD(climaServicio.validarClima(ciudad, TIPO_ACTUAL), source))!= null) return result;
		
			//Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class)); // jersey
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(urlConsultar.toString());

			Response response = webTarget.request().get(Response.class);
			if (response.getStatus() == 200) {
				String openWeatherResponse = response.readEntity(String.class);
				final OpenWeatherActual actual = GSON.fromJson(openWeatherResponse, OpenWeatherActual.class);
				final String speech = String.format("Actualmente %s presenta %s, con una temperatura de %s\u0970C", 
						actual.getName(),
						actual.getWeather().get(0).getDescription(), 
						actual.getMain().getTemp());
				
				guardarEnBD(ciudad, TIPO_ACTUAL, speech, openWeatherResponse);
				
				return armarResponseActual(speech, source);
			}
			
			throw new Exception(String.format("OpenWeather status: %s", response.getStatus()));

		} catch (Exception e) {
			log.error("No se pudo consultar OpenWeather actual", e);
			final String speech = "Fallo consulta de clima";
			return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage("Tuvimos un problema al consulta el clima")), null, source);
		}
	}
	
	
	private void guardarEnBD(String ciudad, String tipo, String visualuzacion, String json) {
		final Date esteMomento = new Date();
		climaServicio.guardar(new Clima(ciudad, tipo, esteMomento, calcularSiguienteActualizacion(esteMomento), json, source, visualuzacion));
	}
	
	
	private ResponseMessageApiAi armarGraficaClima(OpenWeatherForecast openWeather, String ciudad, String json) throws IOException {
		
		final String pathFondo = DIR_FONDO + "fondoGrande.png";
		
		int index = 0;
		List<FilaClima> filas = new ArrayList<>();
		for(Forecast f : openWeather.getList()) {
			FilaClima fila = new FilaClima();
			fila.agregarImagen(DIR_HORAS + obtenerNombreArchivoHora(f.getDt()));
			fila.agregarImagen(DIR_FIGURA + obtenerNombreArchivoFigura(f.getWeather().get(0)));
			fila.agregarImagen(DIR_TEMPERATURA + obtenerNombreArchivoTemperatura(f.getMain()));
			
			filas.add(fila);
			
			index++;
			if (index == GeneradorImagenClima.totalRegistrosClima) break;
			
		}
		
		ComposicionImagenClima composicionClima = new ComposicionImagenClima(ciudad,TIPO_POR_HORAS, pathFondo, filas, source);
		guardarEnBD(ciudad, TIPO_POR_HORAS, composicionClima.getImagenCompuesta(), json);
		
		return GeneradorImagenClima.generarImagen(composicionClima);
		
	}
	
	private String obtenerNombreArchivoHora(long millisHoraConsulta) {
		final Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millisHoraConsulta * 1000);
		
		final int hora = c.get(Calendar.HOUR_OF_DAY);
		
		return String.format("hora%s.png", hora);
		
	}
	
	private String obtenerNombreArchivoFigura(WeatherDescription w) {
		
		String nombreArchivo="";
		switch (w.getIcon()) {
		case "01d": nombreArchivo = "soleado.png"; break;
		case "01n": nombreArchivo = "luna.png"; break;
		case "02d": nombreArchivo = "parcialmenteNublado.png"; break;
		case "02n": nombreArchivo = "parcialmenteNubladoNoche.png"; break;
		case "03d":
		case "03n": nombreArchivo = "nube.png"; break;
		case "04d": 
		case "04n": nombreArchivo = "nubes.png"; break;
		case "09n":
		case "09d":
		case "10d":
		case "10n": nombreArchivo = "lluvias.png"; break;
		case "11d":
		case "11n": nombreArchivo = "tormenta.png"; break;
		}
		
		return nombreArchivo;
		
	}
	
	private String obtenerNombreArchivoTemperatura(MainWeatherData main) {
		
		return String.format("%s.png", main.getTemp());
		
	}
//**************************************************DIALOGFLOW V2*******************************************************
	public ResponseMessageApiAiV2 consultarForecastV2(QueryResult resultAi) {
		try {
			final String ciudad = obtenerParametroCiudadV2(resultAi);
			
			ResponseMessageApiAiV2 result;
			if ((result = validarClimaEnBDV2(climaServicio.validarClima(ciudad, TIPO_POR_HORAS), source))!= null) return result;
			
			StringBuffer urlConsultar = new StringBuffer(urlForecast).append(URLEncoder.encode(ciudad, "UTF-8"));
		
			//Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class)); // jersey
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(urlConsultar.toString());

			Response response = webTarget.request().get(Response.class);
			if (response.getStatus() == 200) {
				String openWeatherResponse = response.readEntity(String.class);
				return armarGraficaClimaV2(GSON.fromJson(openWeatherResponse, OpenWeatherForecast.class), ciudad, openWeatherResponse);
			}
			
			throw new Exception(String.format("OpenWeather status: %s", response.getStatus()));

		} catch (Exception e) {
			log.error("No se pudo consultar OpenWeather forecast", e);
			final String speech = "Fallo consulta de clima";
			return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2("Tuvimos un problema al consulta el clima")), null);
		}
	}
	
	private ResponseMessageApiAiV2 armarGraficaClimaV2(OpenWeatherForecast openWeather, String ciudad, String json) throws IOException {
		
		final String pathFondo = DIR_FONDO + "fondoGrande.png";
		
		int index = 0;
		List<FilaClima> filas = new ArrayList<>();
		for(Forecast f : openWeather.getList()) {
			FilaClima fila = new FilaClima();
			fila.agregarImagen(DIR_HORAS + obtenerNombreArchivoHora(f.getDt()));
			fila.agregarImagen(DIR_FIGURA + obtenerNombreArchivoFigura(f.getWeather().get(0)));
			fila.agregarImagen(DIR_TEMPERATURA + obtenerNombreArchivoTemperatura(f.getMain()));
			
			filas.add(fila);
			
			index++;
			if (index == GeneradorImagenClima.totalRegistrosClima) break;
			
		}
		
		ComposicionImagenClima composicionClima = new ComposicionImagenClima(ciudad,TIPO_POR_HORAS, pathFondo, filas, source);
		guardarEnBD(ciudad, TIPO_POR_HORAS, composicionClima.getImagenCompuesta(), json);
		
		return GeneradorImagenClima.generarImagenV2(composicionClima);
		
	}

}
