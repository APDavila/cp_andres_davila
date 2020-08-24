package com.componente_practico.webhook.ejb.modelo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.CatalogoGeneralFB;

@Entity
@Table(name = "fuente_noticia")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "fuenteNoticia.porPayload", query = "select o from FuenteNoticia o where o.payload = :payload and o.activo = true")
})
public class FuenteNoticia extends CatalogoGeneralFB implements Serializable {

	private String imagen;

	public String getImagen() {
		return imagen;
	}

	public void setImagen(String imagen) {
		this.imagen = imagen;
	}

}
