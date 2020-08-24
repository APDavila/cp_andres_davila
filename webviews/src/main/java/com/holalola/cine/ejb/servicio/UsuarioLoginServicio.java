package com.holalola.cine.ejb.servicio;

import java.util.Calendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.dao.UsuarioLoginDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioLogin;
import com.holalola.supercines.client.vo.usuarioLogin;
import com.holalola.supercines.cliente.mov.vo.UsuarioLoginMV;

@Stateless
public class UsuarioLoginServicio {

	@EJB
	private UsuarioLoginDao usuarioLoginDao;
	
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

	public UsuarioLogin obtenerSesionActivaPorIdUsuario(usuarioLogin ao_usuarioLogin, Usuario usuario, Proveedor proveedor) {
		UsuarioLogin usuarioLogin = usuarioLoginDao.obtenerSesionActivaPorIdUsuario(usuario, proveedor);
		if (usuarioLogin == null) {
			usuarioLogin = new UsuarioLogin(usuario, proveedor, GSON.toJson(ao_usuarioLogin), true, Calendar.getInstance().getTime());
			usuarioLogin = insertar(usuarioLogin);
		}
		else
		{
			usuarioLogin.setJson(GSON.toJson(ao_usuarioLogin));
			modificar(usuarioLogin);
		}
		return usuarioLogin;
	}
	
	public UsuarioLogin obtenerSesionActivaPorIdUsuarioMV(UsuarioLoginMV ao_usuarioLogin, Usuario usuario, Proveedor proveedor) {
		UsuarioLogin usuarioLogin = usuarioLoginDao.obtenerSesionActivaPorIdUsuario(usuario, proveedor);
		if (usuarioLogin == null) {
			usuarioLogin = new UsuarioLogin(usuario, proveedor, GSON.toJson(ao_usuarioLogin), true, Calendar.getInstance().getTime());
			usuarioLogin = insertar(usuarioLogin);
		}
		else
		{
			usuarioLogin.setJson(GSON.toJson(ao_usuarioLogin));
			usuarioLogin.setFecha(Calendar.getInstance().getTime());
			modificar(usuarioLogin);
		}
		return usuarioLogin;
	}
	
	public UsuarioLogin obtenerSesionActivaUsuario(Usuario usuario, Proveedor proveedor) {
		try
		{
			UsuarioLogin usuarioLogin = usuarioLoginDao.obtenerSesionActivaPorIdUsuario(usuario, proveedor);
			return usuarioLogin;
		}
		catch(Exception err)
		{
			return null;
		}
	}
	
	public UsuarioLogin obtenerUltimaSesionActivaPorIdUsuario(Usuario usuario, Proveedor proveedor) {
		UsuarioLogin usuarioLogin = usuarioLoginDao.obtenerUltimaSesionPorIdUsuario(usuario, proveedor);
		return usuarioLogin;
	}

	public UsuarioLogin insertar(UsuarioLogin usuarioLogin) {
		final UsuarioLogin result = usuarioLoginDao.insertar(usuarioLogin);
		return result;
	}
	
	public UsuarioLogin obtenerPorId(Long id) {
		final UsuarioLogin result = usuarioLoginDao.obtenerPorId(id);
		return result;
	}
	
	public void modificar(UsuarioLogin usuarioLogin) {
		usuarioLoginDao.modificar(usuarioLogin);
	}

}
