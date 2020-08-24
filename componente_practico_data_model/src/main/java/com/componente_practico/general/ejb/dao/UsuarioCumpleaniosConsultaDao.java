package com.componente_practico.general.ejb.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.UsuarioCumpleaniosConsulta;
import com.componente_practico.webhook.ejb.modelo.CalificacionPedido;

@Stateless
public class UsuarioCumpleaniosConsultaDao {
	
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<UsuarioCumpleaniosConsulta> obtenerUsuariosCumpleanios() {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_USUARIOS_CUMPLEANIOS}").getResultList();
			
			if (listaResultado != null )
			{
				List<UsuarioCumpleaniosConsulta> listaTemporal = new ArrayList<UsuarioCumpleaniosConsulta>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new UsuarioCumpleaniosConsulta(((BigInteger) record[0]).longValue(),  
			        									record[1].toString(),
			        									record[2].toString()
			        									));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList<UsuarioCumpleaniosConsulta>();
			
		}
		catch(Exception err)
		{
			
			err.printStackTrace();
			return new ArrayList<UsuarioCumpleaniosConsulta>();
		}
	}

}
