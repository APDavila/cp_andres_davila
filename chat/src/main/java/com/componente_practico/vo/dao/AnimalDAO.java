package com.componente_practico.vo.dao;





import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.ejb.modelo.Animal;

@Stateless
public class AnimalDAO {

	@PersistenceContext
	EntityManager em;
	
	public void crear(Animal animal)
	{
		em.persist(animal);
	}
}
