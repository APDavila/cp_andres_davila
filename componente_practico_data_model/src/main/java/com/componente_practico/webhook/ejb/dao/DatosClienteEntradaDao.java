package com.componente_practico.webhook.ejb.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.DatosClienteEntrada;

@Stateless
public class DatosClienteEntradaDao {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public DatosClienteEntrada obtenerCliente(String as_identificacion) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_DATOSCLIENTE_ENTRADA(?)}").
					setParameter(1, as_identificacion.trim()).getResultList();

			if (listaResultado != null )
			{
				List<DatosClienteEntrada> listaTemporal = new ArrayList<DatosClienteEntrada>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new DatosClienteEntrada(((BigInteger) record[0]).longValue(), 
			        									((BigInteger) record[1]).longValue(), 
			        									record[2].toString(),
			        									record[3].toString(),
			        									record[4].toString(),
			        									record[5].toString(),
			        									record[6].toString()
			        									));
			    });
				
				return listaTemporal != null && listaTemporal.size() > 0 ? listaTemporal.get(0) : null;
			}
			else
				return null;
		}
		catch(Exception err)
		{
			System.out.println("--------Error obtenerMenuFavoritos---------------"+err.getMessage());
			err.printStackTrace();
			return null;
		}
	}
}
