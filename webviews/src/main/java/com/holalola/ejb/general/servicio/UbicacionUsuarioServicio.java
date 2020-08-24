package com.holalola.ejb.general.servicio;

import static com.holalola.util.TextoUtil.esVacio;

import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.util.GoogleMapsUtil;

@Stateless
public class UbicacionUsuarioServicio {

	@EJB
	UbicacionUsuarioDao ubicacionUsuarioDao;
	@EJB
	UsuarioDao usuarioDao;

	public UbicacionUsuario obtenerPorId(long ubicacionUsuarioId) {
		return ubicacionUsuarioDao.obtenerPorId(ubicacionUsuarioId);
	}

	public List<String> obtenerAlias(Usuario usuario) {
		return ubicacionUsuarioDao.obtenerAlias(usuario);
	}


	@Transactional(value = TxType.REQUIRED)
	public void completarUbicacionUsuario(UbicacionUsuario ubicacionUsuario) {
		Usuario usuario = ubicacionUsuario.getUsuario();
		if (ubicacionUsuario.isEsPrincipal()) {
			marcarUbicacionPrincipalComoNoPrincipal(usuario);
		}

		ubicacionUsuario.setConfirmadoUsuario(true);
		modificar(ubicacionUsuario);
		usuarioDao.modificar(usuario);
	}

	private void marcarUbicacionPrincipalComoNoPrincipal(Usuario usuario) {
		UbicacionUsuario principal = obtenerPrincipalUsuario(usuario);
		if (principal != null) {
			principal.setEsPrincipal(false);
		}
	}

	@Transactional(value = TxType.REQUIRED)
	public void modificar(UbicacionUsuario ubicacionUsuario) {
		ubicacionUsuarioDao.modificar(ubicacionUsuario);
	}

	public UbicacionUsuario obtenerPrincipalUsuario(Usuario usuario) {
		final List<UbicacionUsuario> principal = ubicacionUsuarioDao.obtenerPrincipalUsuario(usuario);
		return principal.isEmpty() ? null : principal.get(0);
	}

	public UbicacionUsuario obtenerUltimaUsuario(Usuario usuario) {
		List<UbicacionUsuario> listaUbicaciones = ubicacionUsuarioDao.obtenerUtlimaUsuario(usuario);
		return listaUbicaciones != null && listaUbicaciones.size() > 0 ? listaUbicaciones.get(0) : null;

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public UbicacionUsuario obtenerUltimaUsuarioConToken(Usuario usuario) {
		UbicacionUsuario ultima = obtenerUltimaUsuario(usuario);

		if (ultima != null)
			ultima.setToken(UUID.randomUUID().toString());

		return ultima;
	}

	// Alex ver perfil

	@Transactional(value = TxType.REQUIRED)
	public void modificarUbicacionUsuario(UbicacionUsuario ubicacionUsuario) {
		Usuario usuario = ubicacionUsuario.getUsuario();
		if (ubicacionUsuario.isEsPrincipal()) {
			marcarUbicacionPrincipalComoNoPrincipal(usuario);
		}

		ubicacionUsuario.setConfirmadoUsuario(true);
		modificar(ubicacionUsuario);
		usuarioDao.modificar(usuario);
	}

}
