package com.componente_practico;

import java.util.ArrayList;
import java.util.List;

import com.holalola.webhook.acciones.ConsultarFacebook;

public class SubirVideo {

	static String facebookToken = "EAAFTZAtHq8kIBANsfoOnpGuxD5NkrTJcsXMU9zGXfaOtujh8YviPq4ZB1KNRRWZBnRU5FncOO4PpbpRo3enoQsVD0576jBpFZAnZATmMt6it95p3Sl5rHT8kSx3u03cQ70kqtBZBSk3bfN4SWXodFNNglqmcztVlnnbzPISw6DeAZDZD";

	public static void main(String... arg) {

		List<String> dominios = new ArrayList<>();
		dominios.add("http://186.101.35.143");
		dominios.add("http://186.101.35.143:9080"); 
		dominios.add("http://200.115.33.93:9090");
		dominios.add("https://s3.amazonaws.com");
		dominios.add("https://e530aed94b3c.ngrok.io/");
		dominios.add("https://cdn.pixabay.com");
		dominios.add("https://test.placetopay.ec");
		dominios.add("https://secure.placetopay.ec");	
		dominios.add("http://clientes.nuo.com.ec");
		dominios.add("https://api.giphy.com");
		dominios.add("https://media0.giphy.com");
		dominios.add("https://media1.giphy.com");
		dominios.add("https://lola.nuo.com.ec");

		System.out.println("Inicia");

		ConsultarFacebook.whitelistDomains(dominios, facebookToken);


		System.out.println("Fin");
	}

}
