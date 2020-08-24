package com.componente_practico.webhook.ejb.modelo;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.CatalogoGeneralFB;

@Entity
@Table(name = "opcion_tiempo_agenda")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "opcionTiempoAgenda.activas", query = "select o from OpcionTiempoAgenda o where o.activo = true")
	 })

public class OpcionTiempoAgenda extends CatalogoGeneralFB {

}
