package com.componente_practico.webhook.ejb.modelo;

public class MenuFavoritos {
	
	private int contador;
	private Long id;
	private Long producto_id;
	private Long proveedor_id;
	private boolean genera_submenu;
	private String categoria;
	private String payload;
	private String categoriaPrecio;
	private boolean solicitar_acompanante;
	private String nombreProducto;
	private String urlImagenProducto;
	private String descripcionProducto;
	private boolean validarubicacion;
	private String imagenMenuPrincipal;
	private String textoMenuPrincipal;
	
	public MenuFavoritos() {
		super();
	}
	
	public MenuFavoritos(int contador, Long id, Long producto_id, Long proveedor_id, boolean genera_submenu,
			String categoria, String payload, String categoriaPrecio, boolean solicitar_acompanante,
			String nombreProducto, String urlImagenProducto, String descripcionProducto, boolean validarubicacion,
			String imagenMenuPrincipal, String textoMenuPrincipal) {
		super();
		this.contador = contador;
		this.id = id;
		this.producto_id = producto_id;
		this.proveedor_id = proveedor_id;
		this.genera_submenu = genera_submenu;
		this.categoria = categoria;
		this.payload = payload;
		this.categoriaPrecio = categoriaPrecio;
		this.solicitar_acompanante = solicitar_acompanante;
		this.nombreProducto = nombreProducto;
		this.urlImagenProducto = urlImagenProducto;
		this.descripcionProducto = descripcionProducto;
		this.validarubicacion = validarubicacion;
		this.imagenMenuPrincipal = imagenMenuPrincipal;
		this.textoMenuPrincipal = textoMenuPrincipal;
	}

	public int getContador() {
		return contador;
	}

	public void setContador(int contador) {
		this.contador = contador;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getProducto_id() {
		return producto_id;
	}

	public void setProducto_id(Long producto_id) {
		this.producto_id = producto_id;
	}

	public Long getProveedor_id() {
		return proveedor_id;
	}

	public void setProveedor_id(Long proveedor_id) {
		this.proveedor_id = proveedor_id;
	}

	public boolean isGenera_submenu() {
		return genera_submenu;
	}

	public void setGenera_submenu(boolean genera_submenu) {
		this.genera_submenu = genera_submenu;
	}

	public String getCategoria() {
		return categoria;
	}

	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getCategoriaPrecio() {
		return categoriaPrecio;
	}

	public void setCategoriaPrecio(String categoriaPrecio) {
		this.categoriaPrecio = categoriaPrecio;
	}

	public boolean isSolicitar_acompanante() {
		return solicitar_acompanante;
	}

	public void setSolicitar_acompanante(boolean solicitar_acompanante) {
		this.solicitar_acompanante = solicitar_acompanante;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getUrlImagenProducto() {
		return urlImagenProducto;
	}

	public void setUrlImagenProducto(String urlImagenProducto) {
		this.urlImagenProducto = urlImagenProducto;
	}

	public String getDescripcionProducto() {
		return descripcionProducto;
	}

	public void setDescripcionProducto(String descripcionProducto) {
		this.descripcionProducto = descripcionProducto;
	}

	public boolean isValidarubicacion() {
		return validarubicacion;
	}

	public void setValidarubicacion(boolean validarubicacion) {
		this.validarubicacion = validarubicacion;
	}

	public String getImagenMenuPrincipal() {
		return imagenMenuPrincipal;
	}

	public void setImagenMenuPrincipal(String imagenMenuPrincipal) {
		this.imagenMenuPrincipal = imagenMenuPrincipal;
	}

	public String getTextoMenuPrincipal() {
		return textoMenuPrincipal;
	}

	public void setTextoMenuPrincipal(String textoMenuPrincipal) {
		this.textoMenuPrincipal = textoMenuPrincipal;
	}

	
	
}
