package com.componente_practico.general.ejb.modelo;

public class UsuarioCumpleaniosConsulta {
	private Long id;
	private String nombre_facebook;
	private String id_facebook;
	
	
	public UsuarioCumpleaniosConsulta() {
		super();
	}

	public UsuarioCumpleaniosConsulta(Long id, String nombre_facebook, String id_facebook) {
		super();
		this.id = id;
		this.nombre_facebook = nombre_facebook;
		this.id_facebook = id_facebook;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNombre_facebook() {
		return nombre_facebook;
	}

	public void setNombre_facebook(String nombre_facebook) {
		this.nombre_facebook = nombre_facebook;
	}

	public String getId_facebook() {
		return id_facebook;
	}

	public void setId_facebook(String id_facebook) {
		this.id_facebook = id_facebook;
	}
	
	
	
}
