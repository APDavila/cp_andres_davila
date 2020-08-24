package com.componente_practico.general.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;

@Entity
@NamedQueries({
		@NamedQuery(name = "usuarioOperador.porOperador", query = "select distinct o.usuario from UsuarioOperador o where o.usuario.chatActivo = 0 and o.operador = :operadorProveedor order by o.id desc"),
		@NamedQuery(name = "usuarioOperador.chat", query = "select o from UsuarioOperador o where o.usuario = :usuario and o.operador = :operadorProveedor order by o.id"),
		@NamedQuery(name = "usuarioOperador.chatUsuario", query = "select o from UsuarioOperador o where o.usuario = :usuario order by o.id desc")
		})

@SuppressWarnings("serial")
@Table(name = "usuario_operador")
public class UsuarioOperador extends EntityGeneral {

	@ManyToOne
	//@Column(name = "usuario_id")
	private Usuario usuario;
	
	@ManyToOne
	//@Column(name = "operador_id")
	private OperadorProveedor operador;
	
	@Column(name = "fechacreacion")
	private Date fechaCreacion;
	
	@Column(name = "texto")
	private String texto;
	
	@Column(name = "esoperador")
	private boolean esOperador;
	
	
	public UsuarioOperador()
	{
		
	}

	public UsuarioOperador(Usuario usuario, OperadorProveedor operador, Date fechaCreacion, String texto, boolean esOperador) {
		super();
		this.usuario = usuario;
		this.operador = operador;
		this.fechaCreacion = fechaCreacion;
		this.texto = texto;
		this.esOperador = esOperador;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public OperadorProveedor getOperador() {
		return operador;
	}

	public void setOperador(OperadorProveedor operador) {
		this.operador = operador;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public boolean isEsOperador() {
		return esOperador;
	}

	public void setEsOperador(boolean esOperador) {
		this.esOperador = esOperador;
	}	
	
	
	
}
