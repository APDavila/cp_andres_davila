package com.componente_practico.webhook.ejb.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.ProductosPromocion;

@Stateless
public class ProductosPromocionDao {
	
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<ProductosPromocion> obtenerMenuFavoritos(Long ai_idUsuario) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_PRODUCTOSFAVORITOS(?)}").
					setParameter(1, ai_idUsuario).getResultList();

			if (listaResultado != null )
			{
				List<ProductosPromocion> listaTemporal = new ArrayList<ProductosPromocion>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new ProductosPromocion(((BigInteger) record[0]).longValue(), 
			        									((BigInteger) record[1]).longValue(), 
			        									((BigInteger) record[2]).longValue(),
			        									new Date()
			        									));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList<ProductosPromocion>();
		}
		catch(Exception err)
		{
			System.out.println("--------Error obtenerProductosPromocion--------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<ProductosPromocion>();
		}
	}

}
