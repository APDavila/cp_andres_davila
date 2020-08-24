package com.holalola.cine.ejb.servicio;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;


@Stateless
public class UsuarioServicio {

	@EJB
	private UsuarioDao usuarioDao;
		
	public Usuario obtenerPorId(Long idUsuario) {
		Usuario result = usuarioDao.obtenerPorId(idUsuario); //em.createNamedQuery("usuario.porId", Usuario.class).setParameter("id", idUsuario).getResultList();
		return result;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modificar(Usuario usuario) {
		usuarioDao.modificar(usuario);
	}
	

}
