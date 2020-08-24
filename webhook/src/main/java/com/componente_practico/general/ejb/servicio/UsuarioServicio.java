package com.componente_practico.general.ejb.servicio;

import static com.holalola.util.TextoUtil.enmascarar;
import static com.holalola.util.TextoUtil.esVacio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.joda.time.DateTime;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.util.TextoUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.response.InformacionUsuarioFacebook;

@Stateless
public class UsuarioServicio {

	@EJB
	private UsuarioDao usuarioDao;
	
	@EJB
	private UbicacionUsuarioDao ubicacionUsuarioDao;
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Usuario insertar(Usuario usuario) {
		return usuarioDao.insertar(usuario);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modificar(Usuario usuario) {
		usuarioDao.modificar(usuario);
	}
	
	public Usuario modificarDatosVacios(Usuario usuario) {
		return usuarioDao.modificarUsuario(usuario);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Usuario validarUsuarioFacebook(String idUsuarioFacebook) {
		Usuario usuario;	
		if ((usuario = usuarioDao.obtenerPorIdFacebook(idUsuarioFacebook)) != null) {
			if(usuario.getNombreFacebook().trim().length()!=0) {
				return usuario;
			}
		}
		
		try
		{
		
			// TODO: Tal vez acutalizar cada mes la informacion del usuario?
			
			final InformacionUsuarioFacebook fb = ConsultarFacebook.obtenerInformacionDeUsuario(idUsuarioFacebook, propiedadesLola.facebookToken);
			DateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date(Long.MIN_VALUE); 
			
			try {
				date = format.parse(fb.getBirthday());
			}catch(Exception err)
			{
				date = null; 
			}
			if((usuario = usuarioDao.obtenerPorIdFacebook(idUsuarioFacebook)) != null) {
				usuario.setNombres(fb.getFirst_name());
				usuario.setNombreFacebook(fb.getFirst_name());
				usuario.setApellidos(fb.getLast_name());
				usuario.setApellidoFacebook(fb.getLast_name());
				usuario.setIdFacebook(idUsuarioFacebook);		
				usuario.setUrlFotoPerfil(fb.getProfile_pic());
				usuario.setLocale(fb.getLocale());
				usuario.setZonaHoraria(fb.getTimezone());
				usuario.setSexo(fb.getGender());
				usuario.setFechaVerificacion(DateTime.now().toDate());
				usuario.setChatActivo(true);
				usuario.setBloqueado(false);
				usuario.setFechaInactivacion(date);
				usuario.setId(usuario.getId());
				return modificarDatosVacios(usuario);
			}
			else
				return insertar(new Usuario(fb.getFirst_name(), fb.getLast_name(), idUsuarioFacebook, fb.getProfile_pic(), fb.getLocale(), fb.getTimezone(), fb.getGender(),true, DateTime.now().toDate(), date, false));
		}
		catch(Exception err1)
		{
			return null;
		}
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public String obtenerCelularPayphoneEnmascaradoPedido(Usuario usuario) {
		
		String celular = usuario.getCelularPayphone();
		if (esVacio(celular)) {
				celular = ubicacionUsuarioDao.obtenerPrincipalUsuario(usuario).get(0).getCelular();
				usuario.setCelularPayphone(celular);
				usuarioDao.modificar(usuario);
		}
		return enmascarar(celular, TextoUtil.mascaraCelular);
	}
	
	public Usuario obtenerPorId(Long idUsuario) {
		Usuario result = usuarioDao.obtenerPorId(idUsuario); //em.createNamedQuery("usuario.porId", Usuario.class).setParameter("id", idUsuario).getResultList();
		return result;
	}
	
}
