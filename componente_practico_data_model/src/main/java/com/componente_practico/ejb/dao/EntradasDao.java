package com.componente_practico.ejb.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.eventos.ejb.modelo.Entradas;

@Stateless
public class EntradasDao {

	@PersistenceContext
	private EntityManager em;
	
	public Entradas insertar(Entradas entradas) {
		em.persist(entradas);
		return entradas;
	}
	
	public Entradas modificar(Entradas entradas) {
		return em.merge(entradas);
	}
	
	public Entradas damePorId(Long id) {
		return em.find(Entradas.class, id);
	}
	
	@SuppressWarnings("unchecked")
	public List<Entradas> reservarEntradas(Long al_idUsuarioCrea, Long al_idUsuarioResponsable, int ai_totalPuestos, String as_evento, String as_seccion, Date ad_fechaProceso) {

		List<Entradas> listaResultado = new ArrayList<Entradas>();
		try
		{
			listaResultado = em.createNativeQuery("{call SP_RESERVAPUESTOS(?,?,?,?,?,?)}", Entradas.class).
					setParameter(1, al_idUsuarioResponsable).
					setParameter(2, al_idUsuarioCrea).
					setParameter(3, ai_totalPuestos).
					setParameter(4, as_evento).
					setParameter(5, as_seccion).
					setParameter(6, ad_fechaProceso).getResultList();
			
			return listaResultado != null && listaResultado.size() > 0 ? listaResultado : new ArrayList<Entradas>();

			/*if (listaResultado != null )
			{
				List<Entradas> listaTemporal = new ArrayList<Entradas>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new Entradas(record[0] == null ? null : ((BigInteger) record[0]).longValue(), 
			        							   record[1] == null ? null : ((BigInteger) record[1]).longValue(), 
			        							   record[2] == null ? null :((BigInteger) record[2]).longValue(),
			        							   record[3] == null ? null : ((BigInteger) record[3]).longValue(),
		        									record[2].(),
		        									record[3].toString(),
		        									record[4].toString(),
		        									record[5].toString(),
		        									record[6].toString()
			        									));
			    });
				
				return listaTemporal != null && listaTemporal.size() > 0 ? listaTemporal : new ArrayList<Entradas>();
			}
			else
				return new ArrayList<Entradas>();*/
		}
		catch(Exception err)
		{
			System.out.println("--------Error reservarEntradas---------------"+err.getMessage());
			err.printStackTrace();
			return new ArrayList<Entradas>();
		}
	}
	
}
