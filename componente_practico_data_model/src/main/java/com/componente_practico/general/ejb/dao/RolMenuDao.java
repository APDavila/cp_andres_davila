package com.componente_practico.general.ejb.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.RolMenu;

@Stateless
public class RolMenuDao {

	@PersistenceContext
	private EntityManager em;
	
	
	public List<RolMenu> obtenerPorSupercinesId(String rol) {
		try
		{
			return em.createNamedQuery("rolMenu.porRol", RolMenu.class).setParameter("rol", rol).getResultList();
		}
		catch(Exception err)
		{
			return new ArrayList<RolMenu>();
		}
	}

}
