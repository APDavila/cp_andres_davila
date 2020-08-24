package com.holalola.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.util.PedidoUtil;
import com.holalola.web.dao.Gn_PerfilDao;
import com.holalola.web.modelo.Gn_Perfil;
 

@SessionScoped
@ManagedBean(name = "perfilController")
@SuppressWarnings("serial")
public class PerfilController extends GeneralController {

	private void Mensaje(String ls_mensaje, Boolean bl_warning, Boolean bl_error) {
		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, "", ls_mensaje);
		if (bl_warning)
			success = new FacesMessage(FacesMessage.SEVERITY_WARN, "", ls_mensaje);
		else {
			if (bl_error)
				success = new FacesMessage(FacesMessage.SEVERITY_ERROR, "", ls_mensaje);
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

	@EJB
	Gn_PerfilDao gn_PerfilDao;
	
	@ManagedProperty(value = "#{ingresoController}")
	IngresoController ingresoController;
	boolean lb_verFiltros = true;

	public IngresoController getIngresoController() {
		return ingresoController;
	}

	public void setIngresoController(IngresoController ingresoController) {
		this.ingresoController = ingresoController;
	}

	Date dt_fecha = new Date();
	
	public Date getDt_fecha() {
		return dt_fecha;
	}

	public void setDt_fecha(Date dt_fecha) {
		this.dt_fecha = dt_fecha;
	}

	String ls_busNombre = "";
	Boolean lb_buseEstaActivo = true;
	List<Gn_Perfil> listaPerfiles = new ArrayList();
	String ls_tituloCrea = "";
	Gn_Perfil perfilSeleccionado = new Gn_Perfil();
	
	 

	@PostConstruct
	public void inicializar() {
		try {

			if (!ingresoController.getListaPaginas().contains("perfil.jsf")) {
				fn_volverLogin();
			}

			buscar();
			
		} catch (Exception err) {
			System.out.println("Error al inicializar PerfilController");
			err.printStackTrace();
			fn_volverLogin();
		}
	}
	
	public void nuevo()
	{
		try {

			Mensaje("La Fecha es: " + dt_fecha.toString() ,false,true);
			
			perfilSeleccionado = new Gn_Perfil();
			perfilSeleccionado.setEstaActivo(true);
			lb_verFiltros = false;
			ls_tituloCrea = "Nuevo Perfil";
			
		} catch (Exception err) {
			Mensaje("Error al tratar crear un Nuevo Perfil.",false,true);
			System.out.println("Error en Nuevo PerfilController");
			err.printStackTrace();
		}
	}
	
	public void editar(Gn_Perfil ae_pefil)
	{
		try {

			perfilSeleccionado = ae_pefil;
			lb_verFiltros = false;
			ls_tituloCrea = "Perfil " + ae_pefil.getNombre();
			
		} catch (Exception err) {
			Mensaje("Error al tratar crear un Nuevo Perfil.",false,true);
			System.out.println("Error en Nuevo PerfilController");
			err.printStackTrace();
		}
	}

	public void cancelar()
	{
		lb_verFiltros = true;
	}
	
	public void guardar()
	{
		try {
			
			lb_verFiltros = false;
			
			if(perfilSeleccionado == null)
			{
				Mensaje("No se reconoce el perfil.",false,true);
				System.out.println("No se reconoce el perfil");
				return;
			}
			
			if(perfilSeleccionado.getNombre() == null || perfilSeleccionado.getNombre().trim().length() <= 0)
			{
				Mensaje("El nombre es requerido para el Perfil.",false,true);
				System.out.println("El nombre es requerido para el Perfil.");
				return;
			}
			
			perfilSeleccionado.setNombre(perfilSeleccionado.getNombre().trim().toUpperCase());
			 
			if(perfilSeleccionado.getId() == null || perfilSeleccionado.getId() <= 0)
			{
				gn_PerfilDao.insertar(perfilSeleccionado);
			}
			else
			{
				gn_PerfilDao.modificar(perfilSeleccionado);
			}
			
			buscar();
			
			Mensaje("Datos almacenados con Éxito.",false,false);
			//System.out.println("Datos almacenados con Éxito");
			lb_verFiltros = true;
			
		} catch (Exception err) {
			Mensaje("Error al tratar de Editar.",false,true);
			System.out.println("Error al Etitar PerfilController");
			err.printStackTrace();
		}
	}
	
	public void buscar() {
		try {
			lb_verFiltros = true;
			listaPerfiles = gn_PerfilDao.devuelveListaPerfiles(ls_busNombre, lb_buseEstaActivo);

		} catch (Exception err) {
			Mensaje("Error al tratar de realizar la Búsqueda.",false,true);
			
			System.out.println("Error al buscar PerfilController");
			err.printStackTrace();
		}
	}

	public String getLs_busNombre() {
		return ls_busNombre;
	}

	public void setLs_busNombre(String ls_busNombre) {
		this.ls_busNombre = ls_busNombre;
	}

	public Boolean getLb_buseEstaActivo() {
		return lb_buseEstaActivo;
	}

	public void setLb_buseEstaActivo(Boolean lb_buseEstaActivo) {
		this.lb_buseEstaActivo = lb_buseEstaActivo;
	}

	public List<Gn_Perfil> getListaPerfiles() {
		return listaPerfiles;
	}

	public void setListaPerfiles(List<Gn_Perfil> listaPerfiles) {
		this.listaPerfiles = listaPerfiles;
	}

	public boolean isLb_verFiltros() {
		return lb_verFiltros;
	}

	public void setLb_verFiltros(boolean lb_verFiltros) {
		this.lb_verFiltros = lb_verFiltros;
	}

	public String getLs_tituloCrea() {
		return ls_tituloCrea;
	}

	public void setLs_tituloCrea(String ls_tituloCrea) {
		this.ls_tituloCrea = ls_tituloCrea;
	}

	public Gn_Perfil getPerfilSeleccionado() {
		return perfilSeleccionado;
	}

	public void setPerfilSeleccionado(Gn_Perfil perfilSeleccionado) {
		this.perfilSeleccionado = perfilSeleccionado;
	}

	
	
}
