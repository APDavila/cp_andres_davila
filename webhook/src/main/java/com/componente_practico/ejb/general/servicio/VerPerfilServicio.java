package com.componente_practico.ejb.general.servicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.holalola.general.ejb.dao.VerPerfilDao;
import com.holalola.general.ejb.modelo.Usuario;

@Stateless
public class VerPerfilServicio {
	@EJB
	VerPerfilDao verPerfilDao;
	
	public Usuario obtenerPorId(long idUsuario) {
		return verPerfilDao.obtenerPorId(idUsuario);
	}
}
