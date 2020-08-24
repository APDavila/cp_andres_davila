package com.componente_practico.web.dao;

import java.util.ArrayList;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.web.modelo.Gn_Usuario;

@Stateless
public class Gn_UsuarioDao {
	
	@PersistenceContext
	private EntityManager em;
	
	public Gn_Usuario insertar(Gn_Usuario usuario) {
		em.persist(usuario);
		return usuario;
	}
	
	public void modificar(Gn_Usuario usuario) {
		em.merge(usuario);
	}
	
	public Gn_Usuario obtenerPorId(long id) {
		return em.find(Gn_Usuario.class, id);
	}
	
	public Gn_Usuario autentica(String as_login, String as_clave) {
		final List<Gn_Usuario> result = em.createNamedQuery("ntUsuario.autentica", Gn_Usuario.class).setParameter("login", as_login).setParameter("clave", as_clave).getResultList();
		return result != null ? (result.size() > 0 ? result.get(0) : null) : null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Gn_Usuario> listaUsuariosPorFiltro(String as_nombre, String as_login, boolean ab_estaActivo) {
		List<Object[]> listaTemp = null;
		try
		{
			/*
			listaTemp = em.createNativeQuery("{call SPR_CALIFICACION(?,?,?,?)}").
					setParameter(1, al_idProveedor).
					setParameter(2, sdfr.format( ad_fechaInicio )).
					setParameter(3, sdfr.format( ad_fechaFin )).
					setParameter(4, al_idCalificacion).getResultList();
			
		 
			if (listaTemp != null )
			{
				List<ReporteCalificacion> listaTemporal = new ArrayList<ReporteCalificacion>();
				
				listaTemp.stream().forEach((record) -> {
			        listaTemporal.add(new ReporteCalificacion((record[0] == null ? "" : record[0].toString()).toString(),
			        										  (record[1] == null ? "" : record[1].toString()).toString(),
			        										  (record[2] == null ? "" : record[2].toString()).toString(),
			        										  (record[3] == null ? "" : record[3].toString()).toString(),
			        										  (record[4] == null ? "" : record[4].toString()).toString(),
			        										  (record[5] == null ? 0 : ((BigInteger) record[5]).intValue()), 
			        										  (record[6] == null ? "" : record[6].toString()).toString(),
			        										  (record[7] == null ? "" : record[7].toString()).toString(),
			        										  (record[8] == null ? "" : record[8].toString()).toString(),
			        										  (record[9] == null ? (float) 0 : ((BigDecimal) record[9]).floatValue()), 
			        										  (record[10] == null ? (float) 0 : ((BigDecimal) record[10]).floatValue())
			        									));
			    });
				
				return listaTemporal != null && listaTemporal.size() > 0 ? listaTemporal : new ArrayList<ReporteCalificacion>();
			}
			else
				return new ArrayList<ReporteCalificacion>();
				*/
			
			return new ArrayList<Gn_Usuario>();
		}
		catch(Exception err)
		{
			System.out.println("--------Error Lista NU_Usuario---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<Gn_Usuario>();
		}
	}

}
