package com.componente_practico.general.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.componente_practico.general.ejb.modelo.UbicacionUsuario;
import com.componente_practico.general.ejb.modelo.Usuario;

@Stateless
public class UbicacionUsuarioDao {

	@PersistenceContext
	private EntityManager em;
	 
	public UbicacionUsuario insertar(UbicacionUsuario ubicacionUsuario) {
		em.persist(ubicacionUsuario);
		return ubicacionUsuario;
	}
	
	public List<UbicacionUsuario> listaUbicaciones(Usuario usuario) {
		return em.createNamedQuery("ubicacionUsuario.listaUbicaciones", UbicacionUsuario.class).setParameter("usuario", usuario).setMaxResults(4).getResultList();
	}
	
	public List<UbicacionUsuario> obtenerPrincipalUsuario(Usuario usuario) {
		return em.createNamedQuery("ubicacionUsuario.principalUsuario", UbicacionUsuario.class).setParameter("usuario", usuario).getResultList();
	}
	
	public List<UbicacionUsuario> obtenerporAlias(Usuario usuario, String as_alias) {
		return em.createNamedQuery("ubicacionUsuario.porAlias", UbicacionUsuario.class).setParameter("usuario", usuario).setParameter("alias", as_alias).setMaxResults(2).getResultList();
	}
	
	public List<UbicacionUsuario> obtenerUtlimaUsuario(Usuario usuario) {
		return em.createNamedQuery("ubicacionUsuario.ultimaUsuario", UbicacionUsuario.class).setParameter("usuario", usuario).getResultList();
	}
	
	public List<String> obtenerAlias(Usuario usuario) {
		return em.createNamedQuery("ubicacionUsuario.alias", String.class).setParameter("usuario", usuario).setMaxResults(4).getResultList();
	}
	
	public void modificar(UbicacionUsuario ubicacionUsuario) {
		em.merge(ubicacionUsuario);
	}
	
	public UbicacionUsuario obtenerPorId(long ubicacionUsuarioId) {
		return em.find(UbicacionUsuario.class, ubicacionUsuarioId);
	}
	
	//Andres Davila
	
	public UbicacionUsuario obtenerUtlimaUsuarioDefinido(Usuario usuario) {
		return (em.createNamedQuery("ubicacionUsuario.ultimaUsuario", UbicacionUsuario.class).setParameter("usuario", usuario).getResultList()).get(0);
	}
	
	//Andres Davila
}
