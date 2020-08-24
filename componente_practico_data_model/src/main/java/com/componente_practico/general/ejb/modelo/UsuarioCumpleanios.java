package com.componente_practico.general.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;

@Entity
@Table(name = "usuario_cumpleanios")
@SuppressWarnings("serial")
public class UsuarioCumpleanios extends EntityGeneral {
	
	@ManyToOne
	@JoinColumn(name = "id_usuario")
	private Usuario usuario;
	
	@Column(name = "fecha_envio")
	private Date fechaEnvio;

	public UsuarioCumpleanios() {
		super();
	}

	public UsuarioCumpleanios(Usuario usuario, Date fechaEnvio) {
		super();
		this.usuario = usuario;
		this.fechaEnvio = fechaEnvio;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}
	
	

}
