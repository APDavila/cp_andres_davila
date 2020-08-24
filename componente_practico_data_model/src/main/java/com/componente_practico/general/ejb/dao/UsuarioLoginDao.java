package com.componente_practico.general.ejb.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.general.ejb.modelo.Usuario;
import com.componente_practico.general.ejb.modelo.UsuarioLogin;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;


@Stateless
public class UsuarioLoginDao {

	@PersistenceContext
	private EntityManager em;
	
	public UsuarioLogin insertar(UsuarioLogin usuarioLogin) {
		em.persist(usuarioLogin);
		return usuarioLogin;
	}
	
	public void modificar(UsuarioLogin usuarioLogin) {
		em.merge(usuarioLogin);
	}
	
	public UsuarioLogin obtenerPorId(long id) {
		return em.find(UsuarioLogin.class, id);
	}
	
	public UsuarioLogin obtenerSesionActivaPorIdUsuario(Usuario usuario, Proveedor proveedor) {
		final List<UsuarioLogin> result = em.createNamedQuery("usuario_login.activoPorIdUsuario", UsuarioLogin.class).setParameter("usuario", usuario).setParameter("proveedor", proveedor).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	public UsuarioLogin obtenerUltimaSesionPorIdUsuario(Usuario usuario, Proveedor proveedor) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); // Configuramos la fecha que se recibe
		calendar.add(Calendar.DAY_OF_YEAR, -30);  // numero de días a añadir, o restar en caso de días<0
		
		final List<UsuarioLogin> result = em.createNamedQuery("usuario_login.ultimoLoguin", UsuarioLogin.class).setParameter("usuario", usuario).setParameter("proveedor", proveedor).setParameter("fecha", calendar.getTime()).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
}
