package com.componente_practico.broadcast.entidad;

 
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;

@Entity
@Table(name = "mens_broadcast")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "mensajesBroadcast.buscar", query="select o from MensajesBroadcast o where o.estaActivo = :estaActivo and o.filtrarSexo like :filtrarSexo and o.filtrarNombre like :filtrarNombre and o.mensaje like :mensaje"),
	@NamedQuery(name = "mensajesBroadcast.buscarConFechas", query="select o from MensajesBroadcast o where o.estaActivo = :estaActivo and o.filtrarSexo like :filtrarSexo and o.filtrarNombre like :filtrarNombre and o.mensaje like :mensaje and fechaInicio >= :fechaInicio and fechaFin <= :fechaFin")
})
public class MensajesBroadcast extends EntityGeneral {
	
	private String mensaje;
	
	@Column(name = "urlimagen")
	private String urlImagen;
	
	@Column(name = "estaactivo")
	private boolean estaActivo;
	
	@Column(name = "fechainicio")
	private Date fechaInicio;
	
	@Column(name = "fechafin")
	private Date fechaFin;
	
	private String hora;
	
	@Column(name = "filsexo")
	private String filtrarSexo;
	
	@Column(name = "filnombre")
	private String filtrarNombre;
	
	@Column(name = "fechacreacion")
	private Date fechaCreacion;
	
	@Column(name = "enviado")
	private Boolean enviado;
	
	@Column(name = "alias")
	private String alias;
	
	@Column(name = "activoparafiltro")
	private Boolean activoParaFiltro;
	
	@Column(name = "mensajerespuesta")
	private String mensajerRespuesta;
	
	public MensajesBroadcast() {
		super();
		this.id = (long) 0;
		this.mensaje = "";
		this.urlImagen = "";
		this.estaActivo = true;
		this.fechaInicio = new Date();
		this.fechaFin = new Date();
		this.hora = "";
		this.filtrarSexo = "";
		this.filtrarNombre = "";
		this.fechaCreacion = new Date();
		this.enviado = false;
		this.alias = "";
		this.activoParaFiltro = true;
		this.mensajerRespuesta = "";
	}

	public MensajesBroadcast(String mensaje, String urlImagen, boolean estaActivo, Date fechaInicio, Date fechaFin,
			String hora, String filtrarSexo, String filtrarNombre, Date fechaCreacion, boolean enviado, String alias, boolean activoParaFiltro,
			String mensajerRespuesta) {
		super();
		this.id = (long) 0;
		this.mensaje = mensaje;
		this.urlImagen = urlImagen;
		this.estaActivo = estaActivo;
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
		this.hora = hora;
		this.filtrarSexo = filtrarSexo;
		this.filtrarNombre = filtrarNombre;
		this.fechaCreacion = fechaCreacion;
		this.enviado = enviado;
		this.alias = alias;
		this.activoParaFiltro = activoParaFiltro;
		this.mensajerRespuesta = mensajerRespuesta;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getUrlImagen() {
		return urlImagen;
	}

	public void setUrlImagen(String urlImagen) {
		this.urlImagen = urlImagen;
	}

	public boolean isEstaActivo() {
		return estaActivo;
	}

	public void setEstaActivo(boolean estaActivo) {
		this.estaActivo = estaActivo;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getFiltrarSexo() {
		return filtrarSexo;
	}

	public void setFiltrarSexo(String filtrarSexo) {
		this.filtrarSexo = filtrarSexo;
	}

	public String getFiltrarNombre() {
		return filtrarNombre;
	}

	public void setFiltrarNombre(String filtrarNombre) {
		this.filtrarNombre = filtrarNombre;
	}

	public Date getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Date fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Boolean getEnviado() {
		return enviado;
	}

	public void setEnviado(Boolean enviado) {
		this.enviado = enviado;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Boolean getActivoParaFiltro() {
		return activoParaFiltro;
	}

	public void setActivoParaFiltro(Boolean activoParaFiltro) {
		this.activoParaFiltro = activoParaFiltro;
	}

	public String getMensajerRespuesta() {
		return mensajerRespuesta;
	}

	public void setMensajerRespuesta(String mensajerRespuesta) {
		this.mensajerRespuesta = mensajerRespuesta;
	}
	
	
}
