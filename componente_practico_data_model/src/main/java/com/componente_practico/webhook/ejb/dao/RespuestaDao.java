package com.componente_practico.webhook.ejb.dao;

import java.util.List;
import java.util.Random;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.Respuesta;
import com.componente_practico.webhook.enumeracion.Categoria;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;

@Stateless
public class RespuestaDao {

	@PersistenceContext
	private EntityManager em;
	
	public List<Respuesta> obtenerActivos(Categoria categoria) {
		return em.createNamedQuery("respuesta.activosCategoria", Respuesta.class)
				.setParameter("categoria", categoria)
				.getResultList();
	}
	
	public List<Respuesta> obtenerActivosCategoriaProveedor(Categoria categoria, Proveedor proveedor) {
		return em.createNamedQuery("respuesta.activosCategoriaProveedor", Respuesta.class)
				.setParameter("categoria", categoria)
				.setParameter("proveedor", proveedor)
				.getResultList();
	}
	
	public Respuesta obtenerUnoPorCategoria(Categoria categoria) {
		try
		{
			List<Respuesta> respuesta = obtenerActivos(categoria);
			int index = new Random().nextInt(respuesta.size());
	
			return respuesta.get(index);
		}
		catch(Exception err)
		{
			return null;
		}
	}
	
	public Respuesta obtenerUnoPorCategoriaProveedor(Categoria categoria, Proveedor proveedor) {
		
		List<Respuesta> respuesta = obtenerActivosCategoriaProveedor(categoria, proveedor);
		int index = new Random().nextInt(respuesta.size());

		return respuesta.get(index);		
	}	
}
