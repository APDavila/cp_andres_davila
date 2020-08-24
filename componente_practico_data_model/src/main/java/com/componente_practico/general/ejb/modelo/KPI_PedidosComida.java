package com.componente_practico.general.ejb.modelo;

public class KPI_PedidosComida {

	String formaPago;
	int atendidos;
	int pendientesComercio;
	int pendientesUsuario;
	int cancelados;
	
	public KPI_PedidosComida() {
		super();
		this.formaPago = "";
		this.atendidos = 0;
		this.pendientesComercio = 0;
		this.pendientesUsuario = 0;
		this.cancelados = 0;
	}
	
	public KPI_PedidosComida(String formaPago, int atendidos, int pendientesComercio, int pendientesUsuario,
			int cancelados) {
		super();
		this.formaPago = formaPago;
		this.atendidos = atendidos;
		this.pendientesComercio = pendientesComercio;
		this.pendientesUsuario = pendientesUsuario;
		this.cancelados = cancelados;
	}
	
	public String getFormaPago() {
		return formaPago;
	}
	public void setFormaPago(String formaPago) {
		this.formaPago = formaPago;
	}
	public int getAtendidos() {
		return atendidos;
	}
	public void setAtendidos(int atendidos) {
		this.atendidos = atendidos;
	}
	public int getPendientesComercio() {
		return pendientesComercio;
	}
	public void setPendientesComercio(int pendientesComercio) {
		this.pendientesComercio = pendientesComercio;
	}
	public int getPendientesUsuario() {
		return pendientesUsuario;
	}
	public void setPendientesUsuario(int pendientesUsuario) {
		this.pendientesUsuario = pendientesUsuario;
	}
	public int getCancelados() {
		return cancelados;
	}
	public void setCancelados(int cancelados) {
		this.cancelados = cancelados;
	}
	
	
	
}
