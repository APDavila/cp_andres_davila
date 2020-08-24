package com.componente_practico.general.ejb.modelo;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;

@Entity
@NamedQueries({
		})

@SuppressWarnings("serial")
public class UsuarioPrueba extends EntityGeneral {

	@Column(name = "id_facebook")
	private String idFacebook;

	public String getIdFacebook() {
		return idFacebook;
	}

	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
	}

	protected UsuarioPrueba(String idFacebook) {
		super();
		this.idFacebook = idFacebook;
	}

	protected UsuarioPrueba() {
		super();
	}

	
	
}
