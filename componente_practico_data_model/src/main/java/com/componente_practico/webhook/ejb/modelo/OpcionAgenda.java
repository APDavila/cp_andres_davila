package com.componente_practico.webhook.ejb.modelo;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.CatalogoGeneralFB;

@Entity
@Table(name = "opcion_agenda")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "opcionAgenda.activas", query = "select o from OpcionAgenda o where o.activo = true")
	 })

public class OpcionAgenda extends CatalogoGeneralFB {

}
