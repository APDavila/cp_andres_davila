package com.componente_practico.webhook.vo;

import java.util.ArrayList;
import java.util.List;

public class FilaClima {
	
	private List<String> imagenes = new ArrayList<>();

	public List<String> getImagenes() {
		return imagenes;
	}

	public void agregarImagen(String imagen) {
		imagenes.add(imagen);
	}
	
	

}
