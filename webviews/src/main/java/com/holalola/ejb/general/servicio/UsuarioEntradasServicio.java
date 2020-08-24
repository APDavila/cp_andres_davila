package com.holalola.ejb.general.servicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.comida.pedido.ejb.modelo.ProveedorSubtipoComida;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;

@Stateless
public class UsuarioEntradasServicio {
	@EJB
	UsuarioDao usuarioDao;

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Usuario obtenerUsuarioPorIdentificacion(String identificacionUsuario) {
		final Usuario usuario = usuarioDao.obtenerPorIdentificacion(identificacionUsuario);
		return usuario;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modificar(Usuario usuario) {
		usuarioDao.modificar(usuario);
	}
}
