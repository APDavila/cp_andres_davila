package com.componente_practico.general.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.holalola.cine.ejb.modelo.CiudadProveedor;

@Entity
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "ciudad.porSupercinesId", query = "select o from Ciudad o where o.supercinesId = :supercinesId"),
	@NamedQuery(name = "ciudad.porNombre", query = "select o from Ciudad o where o.nombre = :nombre"),
	@NamedQuery(name = "ciudad.todas", query = "select o from Ciudad o where o.activo = 1")
})
public class Ciudad extends CatalogoGeneral {
	
	@Column(name = "supercines_id")
	private Long supercinesId;
	
	protected Ciudad() {}
	
	public Ciudad(String nombre, Long supercinesId) {
		this.nombre = nombre;
		this.supercinesId = supercinesId;
		this.activo = true;
	}

	public static Ciudad getInstanciaVacia() {
		return new Ciudad();
	}
	
	public Ciudad(Long id) {
		this.id = id;
	}

	public Long getSupercinesId() {
		return supercinesId;
	}

	public void setSupercinesId(Long supercinesId) {
		this.supercinesId = supercinesId;
	}
	

	@Override
    public String toString() {
        return nombre;
    }
}
