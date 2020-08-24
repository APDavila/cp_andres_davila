package com.holalola.chat.controller;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.holalola.comida.pedido.ejb.dao.OperadorProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;
import com.holalola.ejb.pedidos.servicio.PedidoServicio;
import com.holalola.util.PedidoUtil;


@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class LoginChatController {
	@EJB
	private SessionUtils session;

	@EJB
	private OperadorProveedorDao operadorProveedorDao;
	
	@ManagedProperty("#{usuarioChatController}")
	UsuarioChatController usuarioChatController;

	@EJB
	PedidoServicio pedidoServicio;
	
	private String username;

	private String password;

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
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		String ls_clave =  password != null ? PedidoUtil.getMD5(password):"";
		try {
			
			boolean loggedIn = false;
			OperadorProveedor operadorProveedor = null;
			if (username != null && password != null) {
				operadorProveedor = operadorProveedorDao.loguinChat(username, ls_clave);
				loggedIn = operadorProveedor != null;
			}

			if (loggedIn) {
				Mensaje("!!Bienvenido!!", false, false);
				session.borrar(PedidoUtil.gs_SesionExterna());
				session.borrar(PedidoUtil.gs_SesionProveedorExterno());
				session.add(PedidoUtil.gs_SesionExterna(), operadorProveedor.getUsername());
				session.add(PedidoUtil.gs_SesionProveedorExterno(), operadorProveedor.getProveedor().getId());
				usuarioChatController.setOperadorProveedor(operadorProveedor);
				
				try {
					FacesContext contex = FacesContext.getCurrentInstance();
					contex.getExternalContext().redirect(
							PedidoUtil.urlServidor().trim() + "webviews/facebook/chat/chat.jsf");
				} catch (Exception e) {

					pedidoServicio.crearLogExc("Login-Chat-Externo", fechaInicioProceso, "Usuario: " +username+" | Clave: "+password +" | Interno : 1 | ENCR: "+ls_clave,
							Calendar.getInstance().getTime(), e, true);

					Mensaje("No pude redireccionar", false, true);
				}
			} else {
				session.borrar(PedidoUtil.gs_SesionExterna());
				Mensaje("Usuario no Válido", false, true);
				pedidoServicio.crearLog("Login-Externo", fechaInicioProceso, "Usuario: " +username+" | Clave: "+password,
						Calendar.getInstance().getTime(), "Usuario no Válido", true);
			}
		} catch (Exception err) {
			Mensaje("Error al Tratar de Procesar el Usuario", false, true);
			pedidoServicio.crearLogExc("Login-Externo", fechaInicioProceso, "Usuario: " +username+" | Clave: "+password + " | ENCR: "+ls_clave,
					Calendar.getInstance().getTime(), err, true);
		}

	}

	@PostConstruct
	public void inicializar() {
		session.borrar(PedidoUtil.gs_SesionExterna());
		session.borrar(PedidoUtil.gs_SesionProveedorExterno());
	}

	public UsuarioChatController getUsuarioChatController() {
		return usuarioChatController;
	}

	public void setUsuarioChatController(UsuarioChatController usuarioChatController) {
		this.usuarioChatController = usuarioChatController;
	}
	
	
}
