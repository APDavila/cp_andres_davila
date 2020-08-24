package com.componente_practico.general.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.componente_practico.general.ejb.modelo.Ciudad;
import com.holalola.comida.pedido.ejb.modelo.Producto;

@Stateless
public class CiudadDao {

	@PersistenceContext
	private EntityManager em;
	
	
	@Transactional(value = TxType.REQUIRED)
	public Ciudad insertar(Ciudad ciudad) {
		em.persist(ciudad);
		return ciudad;
	}
	
	@Transactional(value = TxType.REQUIRED)
	public Ciudad modificar(Ciudad ciudad) {
		em.merge(ciudad);
		return ciudad;
	}
	
	public List<Ciudad> obtenerPorSupercinesId(Long supercinesId) {
		return em.createNamedQuery("ciudad.porSupercinesId", Ciudad.class).setParameter("supercinesId", supercinesId).getResultList();
	}
	
	public List<Ciudad> obtenerPorNombre(String as_nombre) {
		return em.createNamedQuery("ciudad.porNombre", Ciudad.class).setParameter("nombre", as_nombre).getResultList();
	}
	
	public List<Ciudad> obtenerCiudades() {
		return em.createNamedQuery("ciudad.todas", Ciudad.class).getResultList();
	}
	
	public Ciudad actualizarCiudades(String nombreCiudad){
		List<Ciudad> result= em.createNamedQuery("{call SP_ACTUALIZACIUDADES(?)}", Ciudad.class).
				setParameter(1,nombreCiudad).getResultList();
		return result.get(0);
	}
	
	public Ciudad obtenerPorId(long id) {
		return em.find(Ciudad.class, id);
	}

}
