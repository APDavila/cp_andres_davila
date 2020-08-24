package com.componente_practico.general.ejb.modelo;

import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.holalola.comida.pedido.ejb.modelo.Proveedor;

@Entity
@Table(name = "confirma_compra")
@SuppressWarnings("serial")
public class ConfirmaCompra extends EntityGeneral {

	@ManyToOne
	@JoinColumn(name="proveedor_id")
	private Proveedor proveedor;
	
	@Column(name = "confirmacion")
	private String confirmacion;
	
	@Column(name = "valor")
	private Double valor;
	
	@Column(name = "fecha")
	private Date fecha;
	
	@Column(name = "cobrado")
	private int cobrado;
	
	@Column(name = "fecha_cobro")
	private Date fechaCobro;
	
	@Column(name = "observacion_cobro")
	private String observacionCobro;
	
	@Override
    public String toString() {
		DecimalFormat df = new DecimalFormat("#.00");
	    String sl_valor = df.format(valor);
        return "Compra (" + (cobrado == 1 ? " cobrada " : " pendeinte de pago ") + "): $ "+sl_valor + (cobrado == 1 && observacionCobro != null && "".equals(observacionCobro.trim()) ? " Observación: " +observacionCobro.trim() : "");
    }

	public ConfirmaCompra(Proveedor proveedor, String confirmacion, Double valor, Date fecha, int cobrado,
			Date fechaCobro, String observacionCobro) {
		super();
		this.proveedor = proveedor;
		this.confirmacion = confirmacion;
		this.valor = valor;
		this.fecha = fecha;
		this.cobrado = cobrado;
		this.fechaCobro = fechaCobro;
		this.observacionCobro = observacionCobro;
	}
		
}
