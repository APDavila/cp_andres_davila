package com.holalola.ecommerce.client.vo;

public class GetByMailResponse {

	private String fechaHora;
	private int codigo;
	private String mensaje;
	private int statusCodeHttp;
	private ListaObjetos listaObjetos;
	
	
	
	public GetByMailResponse() {
		super();
	}

	protected GetByMailResponse(String fechaHora, int codigo, String mensaje, int statusCodeHttp,
			ListaObjetos listaObjetos) {
		super();
		this.fechaHora = fechaHora;
		this.codigo = codigo;
		this.mensaje = mensaje;
		this.statusCodeHttp = statusCodeHttp;
		this.listaObjetos = listaObjetos;
	}

	public String getFechaHora() {
		return fechaHora;
	}

	public void setFechaHora(String fechaHora) {
		this.fechaHora = fechaHora;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public int getStatusCodeHttp() {
		return statusCodeHttp;
	}

	public void setStatusCodeHttp(int statusCodeHttp) {
		this.statusCodeHttp = statusCodeHttp;
	}

	public ListaObjetos getListaObjetos() {
		return listaObjetos;
	}

	public void setListaObjetos(ListaObjetos listaObjetos) {
		this.listaObjetos = listaObjetos;
	}

	public class ListaObjetos{
		private int idCliente;
		private String mail;
		private String password;
		private String nombreCompleto;
		private String telefono;
		private int estado;
		private String fechaCreacion;
		private String fechaActualizacion;
		private String idFb;
		private Canal canal;
		protected ListaObjetos() {
			super();
		}
		protected ListaObjetos(int idCliente, String mail, String password, String nombreCompleto, String telefono,
				int estado, String fechaCreacion, String fechaActualizacion, String idFb, Canal canal) {
			super();
			this.idCliente = idCliente;
			this.mail = mail;
			this.password = password;
			this.nombreCompleto = nombreCompleto;
			this.telefono = telefono;
			this.estado = estado;
			this.fechaCreacion = fechaCreacion;
			this.fechaActualizacion = fechaActualizacion;
			this.idFb = idFb;
			this.canal = canal;
		}
		public int getIdCliente() {
			return idCliente;
		}
		public void setIdCliente(int idCliente) {
			this.idCliente = idCliente;
		}
		public String getMail() {
			return mail;
		}
		public void setMail(String mail) {
			this.mail = mail;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getNombreCompleto() {
			return nombreCompleto;
		}
		public void setNombreCompleto(String nombreCompleto) {
			this.nombreCompleto = nombreCompleto;
		}
		public String getTelefono() {
			return telefono;
		}
		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}
		public int getEstado() {
			return estado;
		}
		public void setEstado(int estado) {
			this.estado = estado;
		}
		public String getFechaCreacion() {
			return fechaCreacion;
		}
		public void setFechaCreacion(String fechaCreacion) {
			this.fechaCreacion = fechaCreacion;
		}
		public String getFechaActualizacion() {
			return fechaActualizacion;
		}
		public void setFechaActualizacion(String fechaActualizacion) {
			this.fechaActualizacion = fechaActualizacion;
		}
		public String getIdFb() {
			return idFb;
		}
		public void setIdFb(String idFb) {
			this.idFb = idFb;
		}
		public Canal getCanal() {
			return canal;
		}
		public void setCanal(Canal canal) {
			this.canal = canal;
		}
		
		
	}
	
	public class Canal{
		private int idCanal;
		private String nombre;
		private int estado;
		private String fechaCreacion;
		protected Canal() {
			super();
		}
		protected Canal(int idCanal, String nombre, int estado, String fechaCreacion) {
			super();
			this.idCanal = idCanal;
			this.nombre = nombre;
			this.estado = estado;
			this.fechaCreacion = fechaCreacion;
		}
		public int getIdCanal() {
			return idCanal;
		}
		public void setIdCanal(int idCanal) {
			this.idCanal = idCanal;
		}
		public String getNombre() {
			return nombre;
		}
		public void setNombre(String nombre) {
			this.nombre = nombre;
		}
		public int getEstado() {
			return estado;
		}
		public void setEstado(int estado) {
			this.estado = estado;
		}
		public String getFechaCreacion() {
			return fechaCreacion;
		}
		public void setFechaCreacion(String fechaCreacion) {
			this.fechaCreacion = fechaCreacion;
		}
		
	}
	
}
