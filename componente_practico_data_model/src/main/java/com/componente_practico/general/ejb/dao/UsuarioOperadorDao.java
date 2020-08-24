package com.componente_practico.general.ejb.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.Usuario;
import com.componente_practico.general.ejb.modelo.UsuarioOperador;
import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;


@Stateless
public class UsuarioOperadorDao {

	@PersistenceContext
	private EntityManager em;
	
	public UsuarioOperador insertar(UsuarioOperador usuarioOperador) {
		em.persist(usuarioOperador);
		return usuarioOperador;
	}
	
	public void modificar(UsuarioOperador usuarioOperador) {
		em.merge(usuarioOperador);
	}
	
	public List<Usuario> obtenerPorOperadorProveedor(OperadorProveedor operadorProveedor) {
		return em.createNamedQuery("usuarioOperador.porOperador", Usuario.class).setParameter("operadorProveedor", operadorProveedor).getResultList();
	}
	
	public List<UsuarioOperador> obtenerChat(OperadorProveedor operadorProveedor, Usuario usuario) {
		return em.createNamedQuery("usuarioOperador.chat", UsuarioOperador.class).setParameter("usuario", usuario).setParameter("operadorProveedor", operadorProveedor).getResultList();
	}
	
	public UsuarioOperador obtenerUltimoOperador(Usuario usuario) {
		UsuarioOperador usuarioOperador = null;
		try
		{
			List<UsuarioOperador> listaUsuario = em.createNamedQuery("usuarioOperador.chatUsuario", UsuarioOperador.class).setParameter("usuario", usuario).setMaxResults(1).getResultList();
			
			if(listaUsuario != null && listaUsuario.size() > 0)
				usuarioOperador = listaUsuario.get(0);
			else
				usuarioOperador = null;
		}
		catch(Exception e)
		{
			usuarioOperador = null;
		}
		
		return usuarioOperador;
	}	
}
