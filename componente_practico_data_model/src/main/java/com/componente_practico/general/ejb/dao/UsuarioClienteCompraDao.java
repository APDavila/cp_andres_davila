package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.UsuarioClienteCompra;

@Stateless
public class UsuarioClienteCompraDao {

	@PersistenceContext
	private EntityManager em;
	
	public UsuarioClienteCompra insertar(UsuarioClienteCompra usuarioClienteCompra) {
		em.persist(usuarioClienteCompra);
		return usuarioClienteCompra;
	}
	
	public void modificar(UsuarioClienteCompra usuarioClienteCompra) {
		em.merge(usuarioClienteCompra);
	}
	
	public UsuarioClienteCompra obtenerPorId(long id) {
		return em.find(UsuarioClienteCompra.class, id);
	}
	
}
