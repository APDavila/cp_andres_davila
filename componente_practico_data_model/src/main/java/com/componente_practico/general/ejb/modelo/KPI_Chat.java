package com.componente_practico.general.ejb.modelo;

public class KPI_Chat {

	int enSoporte;
	int atendidos;
 
	public KPI_Chat() {
		super();
		this.enSoporte = 0;
		this.atendidos = 0;
	}
	
	public KPI_Chat(int procesado, int atendidos) {
		super();
		this.enSoporte = procesado;
		this.atendidos = atendidos;
	}

	public int getEnSoporte() {
		return enSoporte;
	}

	public void setEnSoporte(int enSoporte) {
		this.enSoporte = enSoporte;
	}

	public int getAtendidos() {
		return atendidos;
	}

	public void setAtendidos(int atendidos) {
		this.atendidos = atendidos;
	}
	
	
	 
}
