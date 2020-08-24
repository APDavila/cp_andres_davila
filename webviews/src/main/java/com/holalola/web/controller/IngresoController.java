package com.holalola.web.controller;



import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.SessionUtils;
import com.holalola.util.PedidoUtil;
import com.holalola.web.dao.Gn_UsuarioDao;
import com.holalola.web.modelo.Gn_Usuario;

@ManagedBean(name = "ingresoController")
@SessionScoped
@SuppressWarnings("serial")
public class IngresoController  extends GeneralController {
	@EJB
	private SessionUtils session;
	
	@EJB
	private Gn_UsuarioDao nt_UsuarioDao;

	private String username;

	private String password;
	
	private List<String> listaPaginas = new ArrayList();
	
	Gn_Usuario nu_Usuario = null;
	
	
	public Gn_Usuario getNu_Usuario() {
		return nu_Usuario;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<String> getListaPaginas() {
		return listaPaginas;
	}

	public void setListaPaginas(List<String> listaPaginas) {
		this.listaPaginas = listaPaginas;
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

	public void login(ActionEvent event) {
		//Date fechaInicioProceso = Calendar.getInstance().getTime();
		String ls_clave =  password != null ? PedidoUtil.getMD5(password):"";
		try {
			
			System.out.println("  Verifica Clave:  " + ls_clave);
			
			boolean loggedIn = false;
			
			if (username != null && password != null) {
				nu_Usuario = nt_UsuarioDao.autentica(username, ls_clave);
				
				if(nu_Usuario != null)
				{
					nu_Usuario.setClave("*******");
				}
				
				if(nu_Usuario != null && nu_Usuario.getId() != null && nu_Usuario.getId() > 0)
				{
					session.add(PedidoUtil.getGsSesionusuarioweb(), nu_Usuario);
					loggedIn = true;
				}
				 
			}

			if (loggedIn) {
				Mensaje("!!Bienvenido!!", false, false);
				 
				try {
					FacesContext contex = FacesContext.getCurrentInstance();
					contex.getExternalContext().redirect(
							PedidoUtil.urlServidor().trim() + "webviews/web/perfil.jsf");
				} catch (Exception e) {

					Mensaje("No es posible ingresar al sistema", false, true);
				}
			} else {
				session.borrar(PedidoUtil.gs_SesionExterna());
				Mensaje("Usuario no Válido", false, true);
				/*pedidoServicio.crearLog("Login-Externo", fechaInicioProceso, "Usuario: " +username+" | Clave: "+password,
						Calendar.getInstance().getTime(), "Usuario no Válido", true);*/
			}
		} catch (Exception err) {
			err.printStackTrace();
			Mensaje("Error al Tratar de Procesar el Usuario", false, true);
			/*pedidoServicio.crearLogExc("Login-Externo", fechaInicioProceso, "Usuario: " +username+" | Clave: ******** | ENCR: "+ls_clave,
					Calendar.getInstance().getTime(), err, true);*/
		}

	}

	@PostConstruct
	public void inicializar() {
		//usuarioProveedorController.setOperadorProveedor(null);
		//usuarioProveedorController.setInterno(false);
		//session.borrar(PedidoUtil.gs_SesionExterna());
		//session.borrar(PedidoUtil.gs_SesionProveedorExterno());
	}
}
