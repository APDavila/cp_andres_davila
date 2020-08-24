package com.holalola.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.util.PedidoUtil;
import com.holalola.web.dao.GnUsuarioPerfilDao;
import com.holalola.web.dao.GnUsuarioPerfilDao.UsuarioPerfilWeb;
import com.holalola.web.modelo.Gn_Usuario;


@RequestScoped
@ManagedBean(name = "menuWebController")
@SuppressWarnings("serial")
public class MenuWebController extends GeneralController {

	private MenuModel model = new DefaultMenuModel();
	private String ls_nombrePalicacion = "NUO-Tickets";

	public String getLs_nombrePalicacion() {
		return ls_nombrePalicacion;
	}

	public void setLs_nombrePalicacion(String ls_nombrePalicacion) {
		this.ls_nombrePalicacion = ls_nombrePalicacion;
	}

	@EJB
	GnUsuarioPerfilDao gnUsuarioPerfilDao;

	private Gn_Usuario gn_Usuario = null;
	private String ls_nombreUsuario = "";

	@ManagedProperty(value = "#{ingresoController}")
	IngresoController ingresoController;

	public IngresoController getIngresoController() {
		return ingresoController;
	}

	public void setIngresoController(IngresoController ingresoController) {
		this.ingresoController = ingresoController;
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

	private void fn_volverLogin() {
		try {
			FacesContext contex = FacesContext.getCurrentInstance();
			contex.getExternalContext().redirect(PedidoUtil.urlServidor().trim() + "webviews/web/login.jsf");
		} catch (Exception e) {
			Mensaje("No pude Iniciar el Sistema", false, true);
		}
	}

	@PostConstruct
	public void inicializar() {
		try {

			gn_Usuario = ingresoController.nu_Usuario;

			if (gn_Usuario == null) {
				fn_volverLogin();
			}
			ls_nombreUsuario = "Usuario: " + (gn_Usuario.getApellido() != null ? gn_Usuario.getApellido() : "").trim()
					+ " " + (gn_Usuario.getNombre() != null ? gn_Usuario.getNombre() : "").trim();
			CargarMenu();
		} catch (Exception err) {
			System.out.println("Error al inicializar MenuWebController");
			err.printStackTrace();
			fn_volverLogin();
		}
	}

	public void CargarMenu() {

		List<String> listaPaginas = new ArrayList();
		try {
			List<UsuarioPerfilWeb> lista = gnUsuarioPerfilDao.obtenerProdutoPorDetallePedido(gn_Usuario.getId());
			String ls_padre = "";
			DefaultSubMenu parentMenu = null;

			for (UsuarioPerfilWeb item : lista) {

				listaPaginas.add(item.getUrl());

				if (!ls_padre.trim().equals(item.getPadre().trim())) {

					ls_padre = item.getPadre();
					if (parentMenu != null) {
						model.addElement(parentMenu);
					}
					if (item.getContador() > 1 && item.getPadre().trim().length() > 0) {
						parentMenu = new DefaultSubMenu(item.getPadre());
					} else
						parentMenu = null;
				}

				// DefaultSubMenu theMenu = new DefaultSubMenu(item.getPagina());

				DefaultMenuItem mi = new DefaultMenuItem(item.getPagina());
				mi.setUrl(item.getUrl());
				// theMenu.addElement(mi);

				if (parentMenu == null) {
					model.addElement(mi);
				} else {
					parentMenu.addElement(mi);
				}
			}
		} catch (Exception err) {

		}
		ingresoController.setListaPaginas(listaPaginas);
	}

	public MenuModel getMenuModel() {
		return model;
	}

	public String getLs_nombreUsuario() {
		return ls_nombreUsuario;
	}
}
