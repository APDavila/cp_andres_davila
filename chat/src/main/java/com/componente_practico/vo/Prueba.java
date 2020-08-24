package com.componente_practico.vo;

import java.util.Date;

public class Prueba {

	String valor1;
	String valor2;
	Date fecha;
	public Prueba(String valor1, String valor2, Date fecha) {
		super();
		this.valor1 = valor1;
		this.valor2 = valor2;
		this.fecha = fecha;
	}
	public String getValor1() {
		return valor1;
	}
	public void setValor1(String valor1) {
		this.valor1 = valor1;
	}
	public String getValor2() {
		return valor2;
	}
	public void setValor2(String valor2) {
		this.valor2 = valor2;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	
	
}
