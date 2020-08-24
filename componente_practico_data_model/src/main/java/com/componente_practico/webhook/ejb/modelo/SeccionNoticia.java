package com.componente_practico.webhook.ejb.modelo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.CatalogoGeneralFB;

@Entity
@Table (name = "seccion_noticia")
@SuppressWarnings("serial")
public class SeccionNoticia extends CatalogoGeneralFB implements Serializable{

}
