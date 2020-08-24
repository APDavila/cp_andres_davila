package com.componente_practico.ejb.modelo.Servicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.componente_practico.ejb.modelo.Animal;
import com.componente_practico.vo.dao.AnimalDAO;


@Stateless
public class AnimalServicio {

	@EJB
	AnimalDAO animalDao;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Animal insertar(String nombre) {
		Animal animal = new Animal();
		animal.setNombre(nombre);
		animalDao.crear(animal);
		return (animal);
	}

	
}
