package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.UbicacionUsuario;
import com.componente_practico.general.ejb.modelo.Usuario;

@Stateless
public class VerPerfilDao {
	@PersistenceContext
	private EntityManager em;
	
	
	public Usuario obtenerPorId(long idUsuario) {
		return em.find(Usuario.class, idUsuario);
	}
	
	public Usuario modificar(Usuario usuario) {
		
		return em.merge(usuario);
	}
}
