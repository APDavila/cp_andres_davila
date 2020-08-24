package com.componente_practico.general.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "logs")
@SuppressWarnings("serial")
public class Logs extends EntityGeneral {
	
	@Column(name = "metodo")
	private String metodo;
	
	@Column(name = "fecha")
	private Date fecha;
	
	@Column(name = "parametros")
	private String parametros;
	
	@Column(name = "fechafin")
	private Date fechaFin;
	
	@Column(name = "mensaje")
	private String mensaje;
	
	@Column(name = "esdelola")
	private int esDeLola;
		
	
	public Logs(String metodo, Date fecha, String parametros, Date fechaFin, String mensaje, int esDeLola) {
		this.metodo = metodo;
		this.fecha = fecha;
		this.parametros = parametros;
		this.fechaFin = fechaFin;
		this.mensaje = mensaje;
		this.esDeLola = esDeLola;
	}

	@Override
    public String toString() {
        return "Log (" + (esDeLola == 1 ? "Lola" : "Externo") + "):"+fecha.toString()+" || metodo: "+metodo+" || "+" || Parametros: "+parametros+" || Fin de Proceso: "+
        		fechaFin + " || mensaje: "+mensaje;
    }

	public String getMetodo() {
		return metodo;
	}

	public void setMetodo(String metodo) {
		this.metodo = metodo;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getParametros() {
		return parametros;
	}

	public void setParametros(String parametros) {
		this.parametros = parametros;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public int getEsDeLola() {
		return esDeLola;
	}

	public void setEsDeLola(int esDeLola) {
		this.esDeLola = esDeLola;
	}
	
	
	
}
