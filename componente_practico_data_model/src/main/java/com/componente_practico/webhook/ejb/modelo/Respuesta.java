package com.componente_practico.webhook.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;
import com.componente_practico.webhook.enumeracion.Categoria;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;

@SuppressWarnings("serial")
@Entity
@Table(name = "respuesta")
@NamedQueries({
		@NamedQuery(name = "respuesta.activosCategoria", query = "select o from Respuesta o where o.activo = 1 and o.categoria = :categoria"),
		@NamedQuery(name = "respuesta.activosCategoriaProveedor", query = "select o from Respuesta o where o.activo = 1 and o.categoria = :categoria and o.proveedor = :proveedor"),		
})

public class Respuesta extends EntityGeneral {

	private String texto;

	@Column(name = "mostrar_opciones")
	private boolean mostrarOpciones;
	
	@Column(name = "mostrar_imagen")
	private boolean mostrarImagen;
	
	private boolean activo;

	@Enumerated(EnumType.STRING)
	private Categoria categoria;
	
	@ManyToOne
	private Proveedor proveedor;

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean isMostrarOpciones() {
		return mostrarOpciones;
	}

	public void setMostrarOpciones(boolean mostrarOpciones) {
		this.mostrarOpciones = mostrarOpciones;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public boolean isMostrarImagen() {
		return mostrarImagen;
	}

	public void setMostrarImagen(boolean mostrarImagen) {
		this.mostrarImagen = mostrarImagen;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

}
