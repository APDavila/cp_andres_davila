package com.componente_practico.web.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.web.modelo.Gn_Perfil;

@Stateless
public class Gn_PerfilDao {

	@PersistenceContext
	private EntityManager em;
	
	public Gn_Perfil insertar(Gn_Perfil perfil) {
		em.persist(perfil);
		return perfil;
	}
	
	public void modificar(Gn_Perfil perfil) {
		em.merge(perfil);
	}
	
	public Gn_Perfil obtenerPorId(long id) {
		return em.find(Gn_Perfil.class, id);
	}
	
	public List<Gn_Perfil> devuelveListaPerfiles(String as_nombre, Boolean ab_estaActivo) {
		List<Gn_Perfil> result = em.createNamedQuery("gn_perfil.listaDatos", Gn_Perfil.class).
				setParameter("nombre", "%"+as_nombre.trim()+"%").
				setParameter("estaActivo", ab_estaActivo).getResultList();
		if(result == null)
			result = new ArrayList();
		
		return result;
	}
	
}
