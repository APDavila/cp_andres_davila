package com.componente_practico.ejb.config;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class PropiedadesLola {

	public final String facebookToken = System.getProperty("lola.facebook.token");
	public final String certificados = System.getProperty("lola.cert.path");
}
