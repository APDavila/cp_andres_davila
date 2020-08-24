package com.componente_practico.webhook.ejb.dao;

import java.util.List;
import java.util.Random;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.Imagen;
import com.componente_practico.webhook.enumeracion.Categoria;

@Stateless
public class ImagenDao {

	@PersistenceContext
	private EntityManager em;

	public List<Imagen> obtenerActivosCategoria(Categoria categoria) {
		return em.createNamedQuery("imagen.activosCategoria", Imagen.class)
				.setParameter("categoria", categoria)
				.getResultList();
	}

	public Imagen obtenerUnoPorCategoria(Categoria categoria) {

		List<Imagen> imagen = obtenerActivosCategoria(categoria);
		int index = new Random().nextInt(imagen.size());

		return imagen.get(index);

	}
}
