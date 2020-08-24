package com.componente_practico.general.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.Usuario;
import com.componente_practico.general.ejb.modelo.UsuarioCumpleanios;

@Stateless
public class UsuarioCumpleaniosDao {
	
	@PersistenceContext
	private EntityManager em;
	
	public UsuarioCumpleanios insertar(UsuarioCumpleanios usuarioCumpleanios) {
		em.persist(usuarioCumpleanios);
		return usuarioCumpleanios;
	}
	
	public void modificar(UsuarioCumpleanios usuarioCumpleanios) {
		em.merge(usuarioCumpleanios);
	}
}
