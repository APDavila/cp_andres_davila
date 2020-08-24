package com.componente_practico.general.ejb.modelo;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;

@Entity
@Table(name = "calificacion_usuario")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "calificacionUsuario.todas", query = "select o from CalificacionUsuario o ")
})

public class CalificacionUsuario extends EntityGeneral {
	@Column(name = "calificacion")
	private String calificacion;
	
	@Column(name = "uso_minimo")
	private Integer usoMinimo;
	
	@Column(name = "uso_maximo")
	private Integer usoMaximo;
	
	@Column(name = "compras_minimas")
	private Integer comprasMinimas;
	
	@Column(name = "compras_maximas")
	private Integer comprasMaximas;
	
	@Column(name = "valorcompra_minimmo")
	private BigDecimal valorCompraMinimo;
	
	@Column(name = "valorcompra_maximo")
	private BigDecimal valorCompraMaximo;
	
	@Column(name = "meses_atras")
	private Integer mesesAtras;
	
	public CalificacionUsuario() {
		
	}

	public String getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(String calificacion) {
		this.calificacion = calificacion;
	}

	public Integer getUsoMinimo() {
		return usoMinimo;
	}

	public void setUsoMinimo(Integer usoMinimo) {
		this.usoMinimo = usoMinimo;
	}

	public Integer getUsoMaximo() {
		return usoMaximo;
	}

	public void setUsoMaximo(Integer usoMaximo) {
		this.usoMaximo = usoMaximo;
	}

	public Integer getComprasMinimas() {
		return comprasMinimas;
	}

	public void setComprasMinimas(Integer comprasMinimas) {
		this.comprasMinimas = comprasMinimas;
	}

	public Integer getComprasMaximas() {
		return comprasMaximas;
	}

	public void setComprasMaximas(Integer comprasMaximas) {
		this.comprasMaximas = comprasMaximas;
	}

	public BigDecimal getValorCompraMinimo() {
		return valorCompraMinimo;
	}

	public void setValorCompraMinimo(BigDecimal valorCompraMinimo) {
		this.valorCompraMinimo = valorCompraMinimo;
	}

	public BigDecimal getValorCompraMaximo() {
		return valorCompraMaximo;
	}

	public void setValorCompraMaximo(BigDecimal valorCompraMaximo) {
		this.valorCompraMaximo = valorCompraMaximo;
	}

	public Integer getMesesAtras() {
		return mesesAtras;
	}

	public void setMesesAtras(Integer mesesAtras) {
		this.mesesAtras = mesesAtras;
	}
	
	
}
