package com.componente_practico.webhook.ejb.modelo;

public enum Servicios {
	
	COMIDA(4),
	CINE(5),
	ECOMMERCE(11),
	RESERVAS(7),
	AFNA(8),
	MEDICINAS(9),
	LICORES(6);
	
	private long servicioId;
	
	private Servicios(long servicioId) {
		this.servicioId = servicioId;
	}

	public long getServicioId() {
		return servicioId;
	}
	
	public Servicio getServicio() {
		return new Servicio(servicioId);
	}
}
