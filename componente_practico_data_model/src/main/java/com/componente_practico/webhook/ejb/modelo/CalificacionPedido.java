package com.componente_practico.webhook.ejb.modelo;

public class CalificacionPedido {
	private Long id;
	private Long usuario_id;
	private String nombre_facebook;
	private String id_facebook;
	private String nombreProveedor;
	
	public CalificacionPedido() {
		super();
	}

	public CalificacionPedido(Long id, Long usuario_id, String nombre_facebook, String id_facebook, String nombreProveedor) {
		super();
		this.id = id;
		this.usuario_id = usuario_id;
		this.nombre_facebook = nombre_facebook;
		this.id_facebook = id_facebook;
		this.nombreProveedor = nombreProveedor;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUsuario_id() {
		return usuario_id;
	}

	public void setUsuario_id(Long usuario_id) {
		this.usuario_id = usuario_id;
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

	public String getNombreProveedor() {
		return nombreProveedor;
	}

	public void setNombreProveedor(String nombreProveedor) {
		this.nombreProveedor = nombreProveedor;
	}
	
	
}
