package com.componente_practico.webhook.ejb.modelo;

import java.util.Date;

public class ProductosPromocion {
	
	private Long id;
	private Long usuario_id;
	private Long producto_id;
	private Date fechaCreacion;

	
	public ProductosPromocion() {
		super();
	}
	
	public ProductosPromocion(Long id, Long usuario_id, Long producto_id, Date fechaCreacion) {
		super();
		this.id = id;
		this.usuario_id = usuario_id;
		this.producto_id = producto_id;
		this.fechaCreacion = fechaCreacion;
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
	public Long getProducto_id() {
		return producto_id;
	}
	public void setProducto_id(Long producto_id) {
		this.producto_id = producto_id;
	}
	public Date getFechaCreacion() {
		return fechaCreacion;
	}
	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	

}
