package com.componente_practico.webhook.vo;

import java.util.Date;
import java.util.List;

public class ComposicionImagenClima {

	private String ciudad;
	private String tipo;
	private String fondo;
	private List<FilaClima> filas;
	private Date fecha = new Date();
	private String source;

	public ComposicionImagenClima(String ciudad, String tipo, String fondo, List<FilaClima> filas, String source) {
		this.ciudad = ciudad;
		this.tipo = tipo;
		this.fondo = fondo;
		this.filas = filas;
		this.source = source;
	}

	public String getFondo() {
		return fondo;
	}

	public List<FilaClima> getFilas() {
		return filas;
	}

	public String getImagenCompuesta() {
		return String.format("%s_%s_%s.png", ciudad.toUpperCase().replaceAll(" ", "_"), tipo, fecha.getTime());
	}

	public String getSource() {
		return source;
	}

}
