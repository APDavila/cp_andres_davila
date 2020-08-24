package com.componente_practico.webhook.ejb.modelo;

public class DatosClienteEntrada {
	
	private Long idUsuario;
	private Long idCliente;
	private String nombre;
	private String apellido;
	private String email;
	private String celular;
	private String direccion;
	
	
	
	public DatosClienteEntrada() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	public DatosClienteEntrada(Long idUsuario, Long idCliente, String nombre, String apellido, String email, String celular,
			String direccion) {
		super();
		this.idUsuario = idUsuario;
		this.idCliente = idCliente;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.celular = celular;
		this.direccion = direccion;
	}

	public Long getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Long getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(Long idCliente) {
		this.idCliente = idCliente;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido() {
		return apellido;
	}
	public void setApellido(String apellido) {
		this.apellido = apellido;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCelular() {
		return celular;
	}
	public void setCelular(String celular) {
		this.celular = celular;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
}
