package com.holalola.cine.ejb.servicio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;
import com.holalola.general.ejb.dao.UsuarioOperadorDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioOperador;

@Stateless
public class UsuarioOperadorServicio {

	@EJB
	private UsuarioOperadorDao usuarioOperadorDao;
	
	public  List<UsuarioOperador> obtenerChat(OperadorProveedor operadorProveedor, Usuario usuario) {
		return usuarioOperadorDao.obtenerChat(operadorProveedor, usuario); 
	}
	
	public  List<Usuario> obtenerPorOperadorProveedor(OperadorProveedor operadorProveedor) {
		return usuarioOperadorDao.obtenerPorOperadorProveedor(operadorProveedor); 
	}
	
	public UsuarioOperador insertar(UsuarioOperador usuarioOperador) {
		usuarioOperadorDao.insertar(usuarioOperador);
		return usuarioOperador;
	}
	
	public void modificar(UsuarioOperador usuarioOperador) {
		usuarioOperadorDao.modificar(usuarioOperador);
	}
}
