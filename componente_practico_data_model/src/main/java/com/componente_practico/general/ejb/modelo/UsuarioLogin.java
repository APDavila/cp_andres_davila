package com.componente_practico.general.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.holalola.comida.pedido.ejb.modelo.Proveedor;

@Entity
@Table(name = "usuario_login")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "usuario_login.activoPorIdUsuario", query="select o from UsuarioLogin o where o.usuario = :usuario and proveedor = :proveedor and o.activo = true"),
	@NamedQuery(name = "usuario_login.ultimoLoguin", query="select o from UsuarioLogin o where o.usuario = :usuario and proveedor = :proveedor and fecha >= :fecha order by fecha desc ")
})
public class UsuarioLogin extends EntityGeneral {

	@ManyToOne
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	
	@ManyToOne
	@JoinColumn(name = "proveedor_id")
	private Proveedor proveedor;
	
	@Column(name = "json")
	private String json;
	
	@Column(name = "activo")
	private boolean activo;
	
	@Column(name = "fecha")
	private Date fecha;
	
	protected UsuarioLogin() {}
	
	public UsuarioLogin(Usuario usuario, Proveedor proveedor, String json, boolean activo, Date fecha) {
		this.usuario = usuario;
		this.proveedor = proveedor;
		this.json = json;
		this.activo = activo;
		this.fecha = fecha;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	
	
}
