package com.componente_practico.general.ejb.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.KPI_Chat;
import com.componente_practico.general.ejb.modelo.KPI_Cine;
import com.componente_practico.general.ejb.modelo.KPI_PedidosComida;

@Stateless
public class KPIs {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<KPI_PedidosComida> obtenerKPIComida(String as_filtro) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_KPI_PED_COMIDA(?)}").
					setParameter(1, as_filtro).getResultList();

			if (listaResultado != null )
			{
				List<KPI_PedidosComida> listaTemporal = new ArrayList<KPI_PedidosComida>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new KPI_PedidosComida((String) record[0], ((BigDecimal) record[1]).intValue() , ((BigDecimal) record[2]).intValue(), ((BigDecimal) record[3]).intValue(), ((BigDecimal) record[4]).intValue()));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList<KPI_PedidosComida>();
		}
		catch(Exception err)
		{
			System.out.println("--------Error KPI Comida---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<KPI_PedidosComida>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<KPI_Cine> obtenerKPICine(String as_filtro) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_KPI_CINE(?)}").
					setParameter(1, as_filtro).getResultList();

			if (listaResultado != null )
			{
				List<KPI_Cine> listaTemporal = new ArrayList<KPI_Cine>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new KPI_Cine(((BigDecimal) record[0]).intValue(), ((BigDecimal) record[1]).intValue(), ((BigDecimal) record[2]).intValue()));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList<KPI_Cine>();
		}
		catch(Exception err)
		{
			System.out.println("--------Error KPI Cine ---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<KPI_Cine>();
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<KPI_Chat> obtenerKPIChat(String as_filtro) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_KPI_CHAT(?)}").
					setParameter(1, as_filtro).getResultList();

			if (listaResultado != null )
			{
				List<KPI_Chat> listaTemporal = new ArrayList<KPI_Chat>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new KPI_Chat(((BigDecimal) record[0]).intValue(), ((BigDecimal) record[1]).intValue()));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList<KPI_Chat>();
		}
		catch(Exception err)
		{
			System.out.println("--------Error KPI Chat ---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<KPI_Chat>();
		}
	}
}
