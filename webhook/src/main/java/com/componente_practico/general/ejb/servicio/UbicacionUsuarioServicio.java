package com.componente_practico.general.ejb.servicio;

import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ws.rs.Path;

import com.componente_practico.util.ValidadorLocalizacionUsuarioUtil;
import com.componente_practico.webhook.acciones.Conversar;
import com.componente_practico.webhook.acciones.ManejarRestaurantes;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.googlemaps.vo.DireccionGoogleMaps;
import com.holalola.util.GoogleMapsUtil;

@Stateless
@Path("/direccion")
public class UbicacionUsuarioServicio {
	
	private String gs_inAuxiliarParaDireccion = "NUO||OTR";
	
	public String getGs_inAuxiliarParaDireccion() {
		return gs_inAuxiliarParaDireccion;
	}

	@EJB
	ManejarRestaurantes manejarRestaurantes;
	
	@EJB
	private UbicacionUsuarioDao ubicacionUsuarioDao;
	
	@EJB
	private UsuarioDao usuarioDao;
	
	@EJB
	private Conversar conversarServicio;
	
	
	public List<String> obtenerAlias(Usuario usuario) {
		return ubicacionUsuarioDao.obtenerAlias(usuario);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public UbicacionUsuario insertarBasadoEnGoogleMaps(double latitud, double longitud, Usuario usuario) {
		
		List<UbicacionUsuario> ultima = ubicacionUsuarioDao.obtenerUtlimaUsuario(usuario);
		ultima.stream().forEach(u -> {u.setEsUltima(false);});
		
		DireccionGoogleMaps direccion = GoogleMapsUtil.obtenerDireccion(latitud, longitud);
		UbicacionUsuario ubicacionUsuario = new UbicacionUsuario(direccion, usuario);
		
		if(ultima == null || ultima.size() <= 0)
		{
			ubicacionUsuario.setEsPrincipal(true);
			ubicacionUsuario.setEsUltima(true);
		}
		else
		{
			ultima = ubicacionUsuarioDao.obtenerPrincipalUsuario(usuario);
			
			if(ultima.size() > 0)
			{
				if(ultima.get(0).getCallePrincipal() == null || ultima.get(0).getNumeracion() == null )
				{
					ultima.stream().forEach(u -> {u.setEsPrincipal(false);});
					
					ubicacionUsuario.setEsPrincipal(true);
					ubicacionUsuario.setEsUltima(true);
				}
			}
		}
		
		return ubicacionUsuarioDao.insertar(ubicacionUsuario);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void marcarUbicacionPrincipalComoUltima(Usuario usuario, String as_alias) {
		
		ubicacionUsuarioDao.obtenerUtlimaUsuario(usuario).forEach(u -> {
			u.setEsUltima(false);
		});
		
		UbicacionUsuario principal = null;
		if(as_alias != null && as_alias.trim().length() > 0)
		{
			try
			{
				//System.out.println("Seteo Princial as_alias "+as_alias);
				
				if(as_alias.charAt(as_alias.length()-1) == '-')
					as_alias = as_alias.substring(0, as_alias.length() - 1);
				
				//System.out.println("Seteo Princial as_alias "+as_alias);
				
				List<UbicacionUsuario> listaUbicaciones = ubicacionUsuarioDao.obtenerporAlias(usuario, as_alias);
				if(listaUbicaciones != null && listaUbicaciones.size() > 0)
				{
					//System.out.println("Seteo Princial etro con alias ");
					principal = listaUbicaciones.get(0);
					
				}
				
				
			}
			catch(Exception err)
			{
				principal = null;
			}
		}


		if(principal == null)
		{
			principal = obtenerPrincipalUsuario(usuario);
		}
		else
		{
			UbicacionUsuario principalTemp = obtenerPrincipalUsuario(usuario);
			if(principalTemp != null)
			{
				principalTemp.setEsPrincipal(false);
				ubicacionUsuarioDao.modificar(principalTemp);
			}
		}
		
		if (principal == null) {
			try
			{
				List<UbicacionUsuario> listaUbicaciones = ubicacionUsuarioDao.obtenerUtlimaUsuario(usuario);
				if(listaUbicaciones != null && listaUbicaciones.size() > 0)
					principal = listaUbicaciones.get(0);
			}
			catch(Exception err)
			{
				principal = null;
			}
		}
		
		if (principal != null) {
			principal.setEsUltima(true);
			principal.setEsPrincipal(true);
			ubicacionUsuarioDao.modificar(principal);
			//System.out.println("Seteo Princial "+principal.getAlias());
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void marcarUbicacionPrincipalComoNoPrincipal(Usuario usuario) {
		UbicacionUsuario principal = obtenerPrincipalUsuario(usuario);
		if (principal != null) {
			principal.setEsPrincipal(false);
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public UbicacionUsuario obtenerUltimaUsuarioConToken(Usuario usuario) {
		UbicacionUsuario ultima = obtenerUltimaUsuario(usuario);
		
		if(ultima != null)
			ultima.setToken(UUID.randomUUID().toString());
		
		return ultima;
	}
	
	public UbicacionUsuario obtenerUltimaUsuario(Usuario usuario) {
		List<UbicacionUsuario> listaUbicaciones = ubicacionUsuarioDao.obtenerUtlimaUsuario(usuario); 
		return listaUbicaciones != null && listaUbicaciones.size() > 0 ? listaUbicaciones.get(0) : null;
		
	}
	
	public UbicacionUsuario obtenerPrincipalUsuario(Usuario usuario) {
		final List<UbicacionUsuario> principal = ubicacionUsuarioDao.obtenerPrincipalUsuario(usuario);
		return principal.isEmpty() ? null : principal.get(0);
	}
	
	public UbicacionUsuario obtenerPorId(long ubicacionUsuarioId) {
		return ubicacionUsuarioDao.obtenerPorId(ubicacionUsuarioId);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modificar(UbicacionUsuario ubicacionUsuario) {
		ubicacionUsuarioDao.modificar(ubicacionUsuario);
	}
	
	public boolean tieneCoberturaConProveedor(Usuario usuario, long proveedorId) {
		return ValidadorLocalizacionUsuarioUtil.tieneCobertura(obtenerUltimaUsuario(usuario), proveedorId);
	}
}
