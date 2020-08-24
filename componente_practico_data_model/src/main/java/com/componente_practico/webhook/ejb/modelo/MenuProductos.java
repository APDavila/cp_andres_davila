package com.componente_practico.webhook.ejb.modelo;

import java.math.BigDecimal;

public class MenuProductos {
	private Long id;
	private String nombre_producto;
	
	private Boolean ocultar_de_menu;
	private String descripcion;
	private BigDecimal precio; 
	public MenuProductos() {
		super();
	}
	public MenuProductos(Long id, String nombre_producto, Boolean ocultar_de_menu, String descripcion, BigDecimal precio) {
		super();
		this.id = id;
		this.nombre_producto = nombre_producto;
		
		this.ocultar_de_menu = ocultar_de_menu;
		this.descripcion = descripcion;
		this.precio = precio;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNombre_producto() {
		return nombre_producto;
	}
	public void setNombre_producto(String nombre_producto) {
		this.nombre_producto = nombre_producto;
	}
	public Boolean getOcultar_de_menu() {
		return ocultar_de_menu;
	}
	public void setOcultar_de_menu(Boolean ocultar_de_menu) {
		this.ocultar_de_menu = ocultar_de_menu;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getPrecio() {
		return precio;
	}
	public void setPrecio(BigDecimal precio) {
		this.precio = precio;
	}
	
	
}
