package com.componente_practico.general.ejb.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.eventos.ejb.modelo.Entradas;
import com.componente_practico.general.ejb.modelo.ReporteCalificacion;
import com.componente_practico.general.ejb.modelo.Usuario;

@Stateless
public class UsuarioDao {
	
	@PersistenceContext
	private EntityManager em;
	
	public Usuario insertar(Usuario usuario) {
		em.persist(usuario);
		return usuario;
	}
	
	public void modificar(Usuario usuario) {
		em.merge(usuario);
	}
	
	public Usuario modificarUsuario(Usuario usuario) {
		return em.merge(usuario);
	}
	
	public void modificar2(Usuario usuario) {
		em.merge(usuario);
	}
	
	public Usuario obtenerPorId(long id) {
		return em.find(Usuario.class, id);
	}
	
	public Usuario obtenerPorIdFacebook(String idUsuarioFacebook) {
		final List<Usuario> result = em.createNamedQuery("usuario.porIdFacebook", Usuario.class).setParameter("idFacebook", idUsuarioFacebook).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	public List<Usuario> obtenerporFiltros(String nombre, String sexo) {
		final List<Usuario> result = em.createNamedQuery("usuario.porFiltros", Usuario.class).setParameter("nombre", nombre).setParameter("sexo", sexo).getResultList();
		return result;
	}
	
	public List<Usuario> obtenerporFiltros(String nombre, String sexo, Date fechaNacimiento) {
		final List<Usuario> result = em.createNamedQuery("usuario.porFiltrosConFecNac", Usuario.class).setParameter("nombre", nombre).setParameter("sexo", sexo).setParameter("fechaNacimiento", fechaNacimiento).getResultList();
		return result;
	}
	
	public void activaChatDeUsuarios() {
		try
		{
			em.createNativeQuery("{call SP_ACTIVACHATUSUARIOS()}").getResultList();
		}
		catch(Exception err)
		{
			 err.printStackTrace();
		}
	}

	 public Usuario obtenerPorIdentificacion(String identificacionUsuario) {
		  final List<Usuario> result = em.createNamedQuery("usuario.porIdentificacion", Usuario.class).setParameter("numeroIdentificacion", identificacionUsuario).getResultList();
		  return result.isEmpty() ? null : result.get(0);
	 }
	 
	@SuppressWarnings("unchecked")
	public List<ReporteCalificacion> reporteCalificaciones(Long al_idProveedor, Date ad_fechaInicio, Date ad_fechaFin, Long al_idCalificacion) {
		List<Object[]> listaTemp = null;
		try
		{
			
			SimpleDateFormat sdfr = new SimpleDateFormat("yyyy-MM-dd");
			
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
		}
		catch(Exception err)
		{
			System.out.println("--------Error Reporte Calificaciones---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<ReporteCalificacion>();
		}
	}
	 
	//Alex inicio
		
		public List<Usuario> obtenerPorCalificacion(Long calificacionUsuario, String nombre, String sexo) {
			if (calificacionUsuario==null || calificacionUsuario<=0) {
				return em.createNamedQuery("usuario.porCalificacionTodas", Usuario.class)
						.setParameter("nombre", nombre)
						.setParameter("sexo", sexo)
						.getResultList();
			} else {
				return em.createNamedQuery("usuario.porCalificacion", Usuario.class)
						.setParameter("nombre", nombre)
						.setParameter("calificacionUsuario", calificacionUsuario)
						.setParameter("sexo", sexo)
						.getResultList();
			}
			
		}
		
    //Alex fin
	 
}
