package com.componente_practico.webhook.ejb.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.MenuFavoritos;

@Stateless
public class MenuFavoritosDao {

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<MenuFavoritos> obtenerMenuFavoritos(Long ai_idUsuario) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_PRODUCTOSFAVORITOS(?)}").
					setParameter(1, ai_idUsuario).getResultList();

			if (listaResultado != null )
			{
				List<MenuFavoritos> listaTemporal = new ArrayList<MenuFavoritos>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new MenuFavoritos(((BigInteger) record[0]).intValue(), 
			        									((BigInteger) record[1]).longValue(), 
			        									((BigInteger) record[2]).longValue(),
			        									((BigInteger) record[3]).longValue(), 
			        									((Integer) record[4]).intValue() > 0,
			        									record[5].toString(),
			        									record[6].toString(),
			        									record[7].toString(),
			        									((Integer) record[8]).intValue() > 0,
			        									record[9].toString(),
			        									record[10].toString(),
			        									record[11].toString(),
			        									((Boolean) record[12]),
			        									record[13].toString().trim(),
			        									record[14].toString().trim()
			        									));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList<MenuFavoritos>();
		}
		catch(Exception err)
		{
			System.out.println("--------Error obtenerMenuFavoritos---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<MenuFavoritos>();
		}
	}
}
