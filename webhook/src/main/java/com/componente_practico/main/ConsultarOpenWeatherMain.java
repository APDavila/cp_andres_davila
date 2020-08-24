package com.componente_practico.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.componente_practico.webhook.model.openweather.OpenWeatherActual;
import com.google.gson.Gson;

import ai.api.GsonFactory;

public class ConsultarOpenWeatherMain {
	
	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();

	public static void main(String[] args) {
		try {
			//Client client = ClientBuilder.newClient(new ClientConfig().register(LoggingFilter.class)); // jersey
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target("http://api.openweathermap.org/data/2.5/weather?appid=cb7c9891f067fab623e8fddead9b774d&units=metric&lang=es&q=Quito");

			Response response = webTarget.request().get(Response.class);
			if (response.getStatus() == 200) {
				String openWeatherResponse = response.readEntity(String.class);
				OpenWeatherActual actual = GSON.fromJson(openWeatherResponse, OpenWeatherActual.class);
				//System.out.println(actual.getMain().getTemp());
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
