package com.componente_practico.general.ejb.modelo;

public class KPI_Cine {

	int procesado;
	int pendientes;
	int cancelados;
	
	public KPI_Cine() {
		super();
		this.procesado = 0;
		this.pendientes = 0;
		this.cancelados = 0;
	}
	
	public KPI_Cine(int procesado, int pendientes, int cancelados) {
		super();
		this.procesado = procesado;
		this.pendientes = pendientes;
		this.cancelados = cancelados;
	}
	
	public int getProcesado() {
		return procesado;
	}
	public void setProcesado(int procesado) {
		this.procesado = procesado;
	}
	public int getPendientes() {
		return pendientes;
	}
	public void setPendientes(int pendientes) {
		this.pendientes = pendientes;
	}
	public int getCancelados() {
		return cancelados;
	}
	public void setCancelados(int cancelados) {
		this.cancelados = cancelados;
	}
}
