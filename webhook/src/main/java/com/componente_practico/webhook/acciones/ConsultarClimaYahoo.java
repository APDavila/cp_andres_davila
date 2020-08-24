package com.componente_practico.webhook.acciones;

import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_DIAS;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_FIGURA;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_FONDO;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_TEMPERATURA;
import static com.componente_practico.webhook.acciones.GeneradorImagenClima.DIR_TEMPERATURA_MINIMA;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.webhook.abstracto.ConsultarClimaAbstracto;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.ejb.servicio.ClimaServicio;
import com.componente_practico.webhook.model.yahoo.weather.Query;
import com.componente_practico.webhook.model.yahoo.weather.Query.CurrentObservation;
import com.componente_practico.webhook.model.yahoo.weather.Query.Forecasts;
import com.componente_practico.webhook.model.yahoo.weather.Query.Location;
import com.componente_practico.webhook.v2.QueryResult;
import com.componente_practico.webhook.vo.ComposicionImagenClima;
import com.componente_practico.webhook.vo.FilaClima;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.holalola.pagos.GacelaProcesos.Retorno;
import com.holalola.webhook.ejb.modelo.Clima;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;

import java.util.Base64;
import java.util.Base64.Encoder;
import ai.api.GsonFactory;
import ai.api.model.Result;
import okhttp3.OkHttpClient;
import okhttp3.Request;




@Stateless
public class ConsultarClimaYahoo extends ConsultarClimaAbstracto {

	final static Logger log = LoggerFactory.getLogger(ConsultarClimaYahoo.class);
	final static DateFormat formatoFechaClima = new SimpleDateFormat("EEEEE, d 'de' MMMMM", new Locale("es"));
	
	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();
	private static final String uriConsultaClima = "https://query.yahooapis.com/v1/public/yql?format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=&q=";
	
	private static final String source = "Yahoo";
	private String yahooWeatherResponse = null;
	
	@EJB
	private ClimaServicio climaServicio;

	private Query consultar(String ciudad, Date fecha) throws Exception {
		
		
		final String appId = System.getProperty("lola.climaIdAplicacion");//wUIWnS5g
        final String consumerKey = System.getProperty("lola.climaConsumerKey");//"dj0yJmk9ekVsNXZzc09RUVU2JnM9Y29uc3VtZXJzZWNyZXQmc3Y9MCZ4PTg4";
        final String consumerSecret = System.getProperty("lola.climaConsumerSecret"); //"c5af2f457baf45c62feb6d8920632df4d09ab116";
		final String url = System.getProperty("lola.climaUrl"); //"https://weather-ydn-yql.media.yahoo.com/forecastrss";

        long timestamp = new Date().getTime() / 1000;
        byte[] nonce = new byte[32];
        Random rand = new Random();
        rand.nextBytes(nonce);
        String oauthNonce = new String(nonce).replaceAll("\\W", "");

        List<String> parameters = new ArrayList<>();
        parameters.add("oauth_consumer_key=" + consumerKey);
        parameters.add("oauth_nonce=" + oauthNonce);
        parameters.add("oauth_signature_method=HMAC-SHA1");
        parameters.add("oauth_timestamp=" + timestamp);
        parameters.add("oauth_version=1.0");
        // Make sure value is encoded
        parameters.add("location=" + URLEncoder.encode(ciudad, "UTF-8"));
        parameters.add("format=json");
        Collections.sort(parameters);

        StringBuffer parametersList = new StringBuffer();
        for (int i = 0; i < parameters.size(); i++) {
            parametersList.append(((i > 0) ? "&" : "") + parameters.get(i));
        }

        String signatureString = "GET&" +
            URLEncoder.encode(url, "UTF-8") + "&" +
            URLEncoder.encode(parametersList.toString(), "UTF-8");

        String signature = null;
        try {
            SecretKeySpec signingKey = new SecretKeySpec((consumerSecret + "&").getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            byte[] rawHMAC = mac.doFinal(signatureString.getBytes());
            Encoder encoder = Base64.getEncoder();
            signature = encoder.encodeToString(rawHMAC);
        } catch (Exception e) {
            System.err.println("Unable to append signature");
            System.exit(0);
        }

       /* String authorizationLine = "OAuth " +
            "oauth_consumer_key=\"" + consumerKey + "\", " +
            "oauth_nonce=\"" + oauthNonce + "\", " +
            "oauth_timestamp=\"" + timestamp + "\", " +
            "oauth_signature_method=\"HMAC-SHA1\", " +
            "oauth_signature=\"" + signature + "\", " +
            "oauth_version=\"1.0\"";
*/
        
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
          .url("https://weather-ydn-yql.media.yahoo.com/forecastrss?location="+ciudad+"&format=json&oauth_consumer_key="+consumerKey+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+timestamp
        		  +"&oauth_nonce="+oauthNonce+"&oauth_version=1.0&oauth_signature="+signature)
          .get()
          .addHeader("Content-Type", "application/json")
          .addHeader("cache-control", "no-cache")
         // .addHeader("Postman-Token", "fbbceb75-a2d3-4fa7-ab63-b981b4f26ff2")
          .build();
        
        
        //System.out.println("----------------------------------- ls_retorno  --URL-- "+"https://weather-ydn-yql.media.yahoo.com/forecastrss?location="+ciudad+"&format=json&oauth_consumer_key="+consumerKey+"&oauth_signature_method=HMAC-SHA1&oauth_timestamp="+timestamp
		 // +"&oauth_nonce="+oauthNonce+"&oauth_version=1.0&oauth_signature="+signature);
        
        try
        { 
        
			okhttp3.Response response = client.newCall(request).execute();
			 
			String ls_retorno = response.body().string();
			
			//System.out.println("----------------------------------- ls_retorno  -11--- "+ls_retorno);
			
			Query yahooWeather = GSON.fromJson(ls_retorno, Query.class);
			
			//System.out.println("----------------------------------- ls_retorno  ---- Paso ---- ");
			
			if(yahooWeather == null)
			{
				//System.out.println("----------------------------------- ls_retorno  ---- NULOOOOOOOOOOOOOO");
				final String speech = "No se pudo obtener infomacion de Yahoo.";
	        	throw new Exception(speech);
			}
			else
			{
				System.out.println("----------------------------------- ls_retorno  ---- "+ls_retorno);
				yahooWeatherResponse = ls_retorno;
				return yahooWeather;
			}
			
        }
        catch(Exception err)
        {
        	err.printStackTrace();
        	final String speech = "No se pudo obtener infomacion de Yahoo.";
        	throw new Exception(speech);
        }
	}
	
	public ResponseMessageApiAi consultarClimaActual(Result resultAi) {
		try {
			
			final String ciudad = obtenerParametroCiudad(resultAi);
			final String tipoConsulta = TIPO_ACTUAL;
			
			ResponseMessageApiAi result;
			if ((result = validarClimaEnBD(climaServicio.validarClima(ciudad, tipoConsulta), source))!= null) return result;
			
			final Query yahooWeather = consultar(ciudad, new Date());
				
			final Location channel = yahooWeather.getLocation();
			final CurrentObservation condition = yahooWeather.getCurrent_observation();
			
			final String speech = String.format("Actualmente %s se presenta %s, con una temperatura de %s\u0970C", 
					channel.getCity(),
					getClima(condition.getCondition().getCode()), 
					aCentrigados(condition.getCondition().getTemperature()));
			
			guardarEnBD(ciudad, TIPO_ACTUAL, speech, yahooWeatherResponse);
			
			return armarResponseActual(speech, source);
				
			
		} catch (Exception e) {
			log.error("Fallo clima actual", e);
			return armarMensajeError("Disculpame no pude encontrar el clima actual", source);
		}
		
	}
	
	public ResponseMessageApiAi consultarClimaSemana(Result resultAi) {
		try {
		
		final String ciudad = obtenerParametroCiudad(resultAi);
		final Query yahooWeather = consultar(ciudad,  new Date());
			
		return armarMensajeRespuestaClima(yahooWeather, ciudad, yahooWeatherResponse);
		
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return armarMensajeError("Disculpame no pude encontrar el clima de la semana :(", source);
		}
		
	}
	

	private String armarQuery(String ciudad) {
		return "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\"" + ciudad
				+ "\")";
	}
	
	private void guardarEnBD(String ciudad, String tipo, String visualizacion, String json) {
		final Date esteMomento = new Date();
		climaServicio.guardar(new Clima(ciudad, tipo, esteMomento, calcularSiguienteActualizacion(esteMomento), json, source, visualizacion));
	}
	
	private ResponseMessageApiAi armarMensajeRespuestaClima (Query yahooWeather, String ciudad, String json) throws IOException {
		
		
		if(yahooWeather.getForecasts().size() == 0) {
			Data data = new Data(new TextMessage(String.format("No encontre %s. Todavia estoy aprendiendo Geografia.", ciudad)));
			return new ResponseMessageApiAi("No se pudo obtener informacion desde yahoo", "No se pudo obtener informacion desde yahoo", data, null, source);
		} 
		
		final String pathFondo = DIR_FONDO + "fondoGrande.png";
		
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(new Date());
		final List<Forecasts> forecast = yahooWeather.getForecasts();
		//final Location location = yahooWeather.getQuery().getResults().getChannel().getLocation();
		
		int i = 1;
		List<FilaClima> filas = new ArrayList<>();
		for (Forecasts f : forecast) {
			FilaClima fila = new FilaClima();
			fila.agregarImagen(DIR_DIAS + getNombreArchivoDia(i == 1 ? -1 : fecha.get(Calendar.DAY_OF_WEEK)));
			fila.agregarImagen(DIR_FIGURA + getNombreArchivoFigura(f.getCode()));
			fila.agregarImagen(DIR_TEMPERATURA + getNombreArchivoTemperatura(aCentrigados(f.getHigh())));
			fila.agregarImagen(DIR_TEMPERATURA_MINIMA +  getNombreArchivoTemperatura(aCentrigados(f.getLow())));
			
			filas.add(fila);
			
			if (i == GeneradorImagenClima.totalRegistrosClima)break;
			i++;
			fecha.add(Calendar.DATE, 1);
			
		}
		
		ComposicionImagenClima composicionImagen = new ComposicionImagenClima(ciudad,TIPO_POR_DIAS, pathFondo, filas, source);
		guardarEnBD(ciudad, TIPO_POR_DIAS, composicionImagen.getImagenCompuesta(), json);
			
		return GeneradorImagenClima.generarImagen(composicionImagen);

	}
	
	private int aCentrigados(int farenheit) {
		return (int)((farenheit - 32) / 1.8);
	}
	
	private String getClima(int code) {
		
		switch (code) {
		case 3: return "con Tormenta severa";
		case 4: return "con Tormenta";
		case 11:
		case 12: return "con Lluvias";
		case 24: return "con Vientos";
		case 25: return "Frio";
		case 26: return "Nublado";
		case 27: return "Mayormente nublado (noche)";
		case 28: return "Mayormente nublado (dia)";
		case 29: return "Parcialmente nublado (noche)";
		case 30: return "Parcialmente nublado (dia)";
		case 31: return "con Noche clara";
		case 32: return "Soleado";
		case 34: return "Mayormente Soleado";
		case 47:
		case 37: return "con Tormentas electricas aisladas";
		case 38:
		case 39: return "con Tormentas electricas dispersas";
		case 40: return "con Lluvias dispersas";
		case 44: return "Parcialmente nublado";
		case 45: return "con Tormentas electricas";
		
		default: return "";
		}
	}
	
	private String getNombreArchivoFigura(int code) {
		
		switch (code) {
		case 3: 
		case 4: 
		case 37: 
		case 38:
		case 39: 
		case 45: 
		case 47: return "tormenta.png";
		
		case 11:
		case 12: 
		case 40: return "lluvias.png";
		
		
		case 24: 
		case 25: 
		case 26: 
		case 27: 
		case 28: return "nubes.png";
		
		case 29: return "parcialmenteNubladoNoche.png";
		
		case 30: 
		case 44: return "parcialmenteNublado.png";
		
		case 31: return "luna.png";
		case 32: 
		case 34: return "soleado.png";
		
		
		default: return "nubes.png";
		}		
	}
	
	private String getNombreArchivoDia(int dia) {
		
		return dia == -1 ? "hoy.png" : String.format("%s.png", dia);
	}
	
	private String getNombreArchivoTemperatura(int temp) {
		return String.format("%s.png", temp);
	}
//*********************************DIALOGFLOW V2**********************************
	public ResponseMessageApiAiV2 consultarClimaActualV2(QueryResult resultAi) {
		try {
			
			final String ciudad = obtenerParametroCiudadV2(resultAi);
			final String tipoConsulta = TIPO_ACTUAL;
			
			ResponseMessageApiAiV2 result;
			if ((result = validarClimaEnBDV2(climaServicio.validarClima(ciudad, tipoConsulta), source))!= null) return result;
			
			final Query yahooWeather = consultar(ciudad, new Date());
			
			
			final Location channel = yahooWeather.getLocation();
			final CurrentObservation condition = yahooWeather.getCurrent_observation();
			
			final String speech = String.format("Actualmente %s se presenta %s, con una temperatura de %s\u0970C", 
					channel.getCity(),
					getClima(condition.getCondition().getCode()), 
					aCentrigados(condition.getCondition().getTemperature()));
			
			guardarEnBD(ciudad, TIPO_ACTUAL, speech, yahooWeatherResponse);
			
			return armarResponseActualV2(speech, source);
				
			
		} catch (Exception e) {
			log.error("Fallo clima actual", e);
			return armarMensajeErrorV2("Disculpame no pude encontrar el clima actual", source);
		}
		
	}
	
	public ResponseMessageApiAiV2 consultarClimaSemanaV2(QueryResult resultAi) {
		try {
		
		final String ciudad = obtenerParametroCiudadV2(resultAi);
		final Query yahooWeather = consultar(ciudad, new Date());
			
		return armarMensajeRespuestaClimaV2(yahooWeather, ciudad, yahooWeatherResponse);
		
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return armarMensajeErrorV2("Disculpame no pude encontrar el clima de la semana :(", source);
		}
		
	}
	
	private ResponseMessageApiAiV2 armarMensajeRespuestaClimaV2 (Query yahooWeather, String ciudad, String json) throws IOException {
		
		
		if(yahooWeather.getForecasts().size() == 0) {
			PayloadResponse data = new PayloadResponse(new TextMessageV2(String.format("No encontre %s. Todavia estoy aprendiendo Geografia.", ciudad)));
			return new ResponseMessageApiAiV2("No se pudo obtener informacion desde yahoo",source, data, null);
		} 
		
		final String pathFondo = DIR_FONDO + "fondoGrande.png";
		
		Calendar fecha = Calendar.getInstance();
		fecha.setTime(new Date());
		final List<Forecasts> forecast = yahooWeather.getForecasts();
		//final Location location = yahooWeather.getQuery().getResults().getChannel().getLocation();
		
		int i = 1;
		List<FilaClima> filas = new ArrayList<>();
		for (Forecasts f : forecast) {
			FilaClima fila = new FilaClima();
			fila.agregarImagen(DIR_DIAS + getNombreArchivoDia(i == 1 ? -1 : fecha.get(Calendar.DAY_OF_WEEK)));
			fila.agregarImagen(DIR_FIGURA + getNombreArchivoFigura(f.getCode()));
			fila.agregarImagen(DIR_TEMPERATURA + getNombreArchivoTemperatura(aCentrigados(f.getHigh())));
			fila.agregarImagen(DIR_TEMPERATURA_MINIMA +  getNombreArchivoTemperatura(aCentrigados(f.getLow())));
			
			filas.add(fila);
			
			if (i == GeneradorImagenClima.totalRegistrosClima)break;
			i++;
			fecha.add(Calendar.DATE, 1);
			
		}
		
		ComposicionImagenClima composicionImagen = new ComposicionImagenClima(ciudad,TIPO_POR_DIAS, pathFondo, filas, source);
		guardarEnBD(ciudad, TIPO_POR_DIAS, composicionImagen.getImagenCompuesta(), json);
			
		return GeneradorImagenClima.generarImagenV2(composicionImagen);

	}

}
