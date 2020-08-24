package com.holalola.comida.facebook.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import org.primefaces.context.RequestContext;

import com.componente_practico.ejb.general.servicio.VerPerfilServicio;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.Utilidades;
import com.holalola.ejb.general.servicio.UbicacionUsuarioServicio;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.VerPerfilDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.VerPerfilUbicacion;

import static com.holalola.util.TextoUtil.esVacio;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class VerPerfilController extends GeneralController {

	@EJB
	VerPerfilServicio verPerfilServicio;

	@EJB
	VerPerfilDao verPerfilDao;

	@EJB
	private UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	private UbicacionUsuarioDao ubicacionUsuarioDao;

	private boolean conFbExtensions;

	private boolean mostrarFormulario;

	private Usuario usuario;

	List<String> listaAliasUbicacion = new ArrayList<String>();
	
	List<UbicacionUsuario> listaUbicacionUsuario = new ArrayList<UbicacionUsuario>();
	
	

	private String alias;

	private List<UbicacionUsuario> listaUbicaciones = new ArrayList<UbicacionUsuario>();
	private UbicacionUsuario ubicacionUsuario;
	private boolean ab_estadoEditar;
	private boolean ab_renderEditar;
	private boolean ab_estadoEditarP;
	private String tipoIdentificacion="";
	private boolean caldisabled;
	private boolean solicitarApellidos;
	private boolean solicitarCallePrincipal;
	private boolean solicitarCalleSecundaria;
	private boolean solicitarNumeracion;
	private boolean solicitarTelefono;
	private boolean solicitarCelular;
	private boolean solicitarReferencia;
	private boolean solicitarAlias;
	private boolean solicitarEsPrincipal;
	private boolean edit;
	private String apellidos="",display;
	
	private String fechaNacimiento;
	
	
	
	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
			.getRequestParameterMap();
	String idUsuario = "";
	
	@PostConstruct
	public void inicializar() {
		
		params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		
		idUsuario = params.get("iu");
		
		System.out.println("-----------------------------inicializar-----armarUrlVerPerfil  "+idUsuario);
		
		conFbExtensions = true;
		
		usuario = verPerfilServicio.obtenerPorId(Long.parseLong(idUsuario));
		tipoIdentificacion=(usuario.getTipoIdentificacion()==null ? "":usuario.getTipoIdentificacion());
		listaUbicacionUsuario = ubicacionUsuarioDao.listaUbicaciones(usuario);
		ab_estadoEditar = true;
		ab_renderEditar = false;
		ab_estadoEditarP = false;
		edit = true;
		if (tipoIdentificacion.equals("R")) {
			solicitarApellidos=false;
			apellidos="No aplica";
		}
		else {
			solicitarApellidos=true;
			apellidos=(usuario.getTipoIdentificacion()==null ? "":usuario.getApellidos());
		}
		validarCamposPorSolicitar();
	}

	private void Mensaje(String ls_mensaje, Boolean bl_warning, Boolean bl_error) {
		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, ls_mensaje, "");
		if (bl_warning)
			success = new FacesMessage(FacesMessage.SEVERITY_WARN, ls_mensaje, "");
		else {
			if (bl_error)
				success = new FacesMessage(FacesMessage.SEVERITY_ERROR, ls_mensaje, "");
		}
		FacesContext.getCurrentInstance().addMessage(null, success);
	}

	public void obtenerUbicacionSeleccionada() {
		
		alias = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("ubicacionAlias");
		
		ubicacionUsuario = ubicacionUsuarioDao.obtenerporAlias(usuario, alias).size() != 0 ? ubicacionUsuarioDao.obtenerporAlias(usuario, alias).get(0) : new UbicacionUsuario(); 
		
		edit=false;
		ab_estadoEditarP = true;
		ab_estadoEditar = false;
		ab_renderEditar = true;
		solicitarCallePrincipal=true;
		solicitarCalleSecundaria= true;
		solicitarNumeracion= true;
		solicitarTelefono= true;
		solicitarCelular= true;
		solicitarReferencia= true;
		solicitarAlias= true;
		solicitarEsPrincipal= !ubicacionUsuario.isEsPrincipal();
		

	}

	public boolean modificarUbicacionUsuario() {

		if (ubicacionUsuario.getReferenciaUbicacion().trim().length() <= 0
				|| ubicacionUsuario.getReferenciaUbicacion() == null) {

			Mensaje("La referencia no puede ser vacia", true, false);
			return false;
		}

		if (ubicacionUsuario.getAlias().trim().length() <= 0 || ubicacionUsuario.getAlias() == null) {

			Mensaje("El alias no puede ser vacio", true, false);
			return false;
		}

		try {
			ubicacionUsuarioServicio.modificarUbicacionUsuario(ubicacionUsuario);
			Mensaje("Datos modificados correctamente", false, false);
			ab_estadoEditarP = false;
			ab_estadoEditar = true;
			
			listaUbicacionUsuario=ubicacionUsuarioDao.listaUbicaciones(usuario);

		} catch (Exception e) {
			System.out.println("Error" + e);
		}
		return true;
	}

	private void validarCamposPorSolicitar() {
		if (usuario != null) {
			
			
			solicitarCallePrincipal= false;
			solicitarCalleSecundaria= false;
			solicitarNumeracion= false;
			solicitarTelefono= false;
			solicitarCelular= false;
			solicitarReferencia= false;
			solicitarAlias= false;
			solicitarEsPrincipal= false;
			
//			try
//			{
//				if(usuario.getTipoIdentificacion().trim().equals("R")) {
//					solicitarApellidos = false;
//				}
//				if (usuario.getTipoIdentificacion().trim().equals("N") || usuario.getTipoIdentificacion().trim().equals("P") || usuario.getTipoIdentificacion().trim().equals("C")) {
//					solicitarApellidos = true;
//				}
//					
//			}
//			catch(Exception err)
//			{
//				
//			}
		}
	}
	public boolean modificarUsuario() {

		
		try
		{

			if(usuario.getFechaNacimiento() != null && usuario.getFechaNacimiento().toString().trim().length() > 0)
			{
			 
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String dateInString = usuario.getFechaNacimiento().toString().trim();
				Date fechaNacAux = sdf.parse(dateInString);
								 
				
				usuario.setFechaNacimiento(fechaNacAux);
			}
		}
		catch(Exception err)
		{
			
		}
		
		
		try {
			
		} catch (Exception e) {
			if (usuario.getFechaNacimiento().toString().length()<=0) {
				Mensaje("La fecha de nacimiento es incorrecta", true, false);
				return false;
			}
		}

		try {

			if ((usuario.getNombres().length() <= 0)) {

				Mensaje("El nombre supera el numero de caracteres permitidos", true, false);
				return false;
			}

		} catch (Exception e) {
			Mensaje(e.getMessage(), true, false);
		}

		if (tipoIdentificacion.trim().equals("P")) {
			try {
				if (usuario.getNumeroIdentificacion().length()<=4) {
					Mensaje("El pasaporte  es incorrecto", true, false);
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (apellidos == null || apellidos.trim().length() <= 0) {
				Mensaje("Los apellidos son requeridos", true, false);
				return false;
			}
		}

		if (tipoIdentificacion.trim().equals("N")) {
			
			try {
				if (usuario.getNumeroIdentificacion().length()<13) {
					Mensaje("El RUC  es incorrecto", true, false);
					return false;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (apellidos == null || apellidos.trim().length() <= 0) {
				Mensaje("Los apellidos son requeridos", true, false);
				return false;
			}
		}

		try {
			if (tipoIdentificacion.trim().equals("C")) {
				if (!Utilidades.validaCedula(usuario.getNumeroIdentificacion())) {
					Mensaje("La cédula es incorrecta", true, false);
					return false;
				} else if (apellidos == null || apellidos.trim().length() <= 0) {
					Mensaje("Los apellidos son requeridos", true, false);
					return false;
				}
			}
		} catch (Exception e) {
			Mensaje(e.getMessage(), true, false);
			return false;
		}

		try {
			if (tipoIdentificacion.trim().equals("R")) {
				
				apellidos="";

			}
		} catch (Exception e) {
			Mensaje(e.getMessage(), true, false);
			return false;
		}
		
		try {
			if (!Utilidades.validaEmail(usuario.getEmail())) {
				Mensaje("El correo es incorrecto", true, false);
				return false;
			}
			if (usuario.getEmail().toString().isEmpty()) {
				Mensaje("El correo es incorrecto", true, false);
				return false;
			}
			
		} catch (Exception e) {
			Mensaje(e.getMessage(), true, false);
		}
		try {
			if (usuario.getCelularPayphone().length()<10) {
				Mensaje("Número Incorrecto", true, false);
				return false;
			}
			if (usuario.getEmail().toString().isEmpty()) {
				Mensaje("El correo es incorrecto", true, false);
				return false;
			}
			
		} catch (Exception e) {
			Mensaje(e.getMessage(), true, false);
		}


		try {
			
			
			usuario.setApellidos(apellidos);
			usuario.setTipoIdentificacion(tipoIdentificacion);
			verPerfilDao.modificar(usuario);
			Mensaje("Datos modificados correctamente", false, false);
			RequestContext.getCurrentInstance().execute("closeView();");
		} catch (Exception e) {
			System.out.println("Error" + e);
		}
		
		
		return true;
	}

	public void validaCampos() {
		if (tipoIdentificacion.equals("R")) {
			solicitarApellidos=false;
			apellidos="No aplica";
			display="inherit";
		}
		else {
			solicitarApellidos=true;
			display="none";
			if(usuario.getApellidoFacebook().length()!=0 || usuario.getApellidoFacebook()!=null)
				apellidos=usuario.getApellidoFacebook();
			else
				apellidos="";
		}
	}
	
	public void irPanelVerDirecciones() {
		
		ab_estadoEditarP = false;
		ab_estadoEditar = true;
	}

	public void checkSelectedVal(ValueChangeEvent event) {
			   
		  
		
		String selectedVal = event.getNewValue().toString();
		if ("R".equalsIgnoreCase(selectedVal)) {	
			solicitarApellidos = false;
			
		} else if ("C".equalsIgnoreCase(selectedVal) || "P".equalsIgnoreCase(selectedVal)
				|| "N".equalsIgnoreCase(selectedVal)) {
			solicitarApellidos = true;
			
			
		}
	}
	
	

	public boolean isCaldisabled() {
		return caldisabled;
	}

	public void setCaldisabled(boolean caldisabled) {
		this.caldisabled = caldisabled;
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public void setConFbExtensions(boolean conFbExtensions) {
		this.conFbExtensions = conFbExtensions;
	}

	public boolean isMostrarFormulario() {
		return mostrarFormulario;
	}

	public void setMostrarFormulario(boolean mostrarFormulario) {
		this.mostrarFormulario = mostrarFormulario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<String> getListaAliasUbicacion() {
		return listaAliasUbicacion;
	}

	public void setListaAliasUbicacion(List<String> listaAliasUbicacion) {
		this.listaAliasUbicacion = listaAliasUbicacion;
	}

	public List<UbicacionUsuario> getListaUbicaciones() {
		return listaUbicaciones;
	}

	public void setListaUbicaciones(List<UbicacionUsuario> listaUbicaciones) {
		this.listaUbicaciones = listaUbicaciones;
	}

	public boolean isAb_estadoEditar() {
		return ab_estadoEditar;
	}

	public void setAb_estadoEditar(boolean ab_estadoEditar) {
		this.ab_estadoEditar = ab_estadoEditar;
	}

	public boolean isAb_renderEditar() {
		return ab_renderEditar;
	}

	public void setAb_renderEditar(boolean ab_renderEditar) {
		this.ab_renderEditar = ab_renderEditar;
	}

	public UbicacionUsuario getUbicacionUsuario() {
		return ubicacionUsuario;
	}

	public void setUbicacionUsuario(UbicacionUsuario ubicacionUsuario) {
		this.ubicacionUsuario = ubicacionUsuario;
	}

	public boolean isAb_estadoEditarP() {
		return ab_estadoEditarP;
	}

	public void setAb_estadoEditarP(boolean ab_estadoEditarP) {
		this.ab_estadoEditarP = ab_estadoEditarP;
	}

	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public boolean isSolicitarApellidos() {
		return solicitarApellidos;
	}

	public void setSolicitarApellidos(boolean solicitarApellidos) {
		this.solicitarApellidos = solicitarApellidos;
	}

	public boolean isSolicitarCallePrincipal() {
		return solicitarCallePrincipal;
	}

	public void setSolicitarCallePrincipal(boolean solicitarCallePrincipal) {
		this.solicitarCallePrincipal = solicitarCallePrincipal;
	}

	public boolean isSolicitarCalleSecundaria() {
		return solicitarCalleSecundaria;
	}

	public void setSolicitarCalleSecundaria(boolean solicitarCalleSecundaria) {
		this.solicitarCalleSecundaria = solicitarCalleSecundaria;
	}

	public boolean isSolicitarNumeracion() {
		return solicitarNumeracion;
	}

	public void setSolicitarNumeracion(boolean solicitarNumeracion) {
		this.solicitarNumeracion = solicitarNumeracion;
	}

	public boolean isSolicitarTelefono() {
		return solicitarTelefono;
	}

	public void setSolicitarTelefono(boolean solicitarTelefono) {
		this.solicitarTelefono = solicitarTelefono;
	}

	public boolean isSolicitarCelular() {
		return solicitarCelular;
	}

	public void setSolicitarCelular(boolean solicitarCelular) {
		this.solicitarCelular = solicitarCelular;
	}

	public boolean isSolicitarReferencia() {
		return solicitarReferencia;
	}

	public void setSolicitarReferencia(boolean solicitarReferencia) {
		this.solicitarReferencia = solicitarReferencia;
	}

	public boolean isSolicitarAlias() {
		return solicitarAlias;
	}

	public void setSolicitarAlias(boolean solicitarAlias) {
		this.solicitarAlias = solicitarAlias;
	}

	public boolean isSolicitarEsPrincipal() {
		return solicitarEsPrincipal;
	}

	public void setSolicitarEsPrincipal(boolean solicitarEsPrincipal) {
		this.solicitarEsPrincipal = solicitarEsPrincipal;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public List<UbicacionUsuario> getListaUbicacionUsuario() {
		return listaUbicacionUsuario;
	}

	public void setListaUbicacionUsuario(List<UbicacionUsuario> listaUbicacionUsuario) {
		this.listaUbicacionUsuario = listaUbicacionUsuario;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}
	
	
	

}