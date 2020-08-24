package com.holalola.ecommerce.facebook.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.behavior.AjaxBehavior;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.MenuModel;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.ejb.general.servicio.VerPerfilServicio;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.ProductoDao;
import com.holalola.comida.pedido.ejb.dao.ProductoDao.DetallesProducto;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.dao.SubmenuProveedorDao;
import com.holalola.comida.pedido.ejb.dao.SubmenuProveedorDao.DetalleSubMenu;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.comida.pizzahut.ejb.dao.CategoriaDao;
import com.holalola.comida.pizzahut.ejb.dao.DetalleProductoDao;
import com.holalola.comida.pizzahut.ejb.dao.DetalleProductoDao.Categorias;
import com.holalola.comida.pizzahut.ejb.dao.DetalleProductoDao.DetalleProductoDatos;
import com.holalola.ecommerce.client.EcommerceService;
import com.holalola.ecommerce.client.vo.GetByMailResponse;
import com.holalola.ejb.general.servicio.UbicacionUsuarioServicio;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.dao.VerPerfilDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import static com.holalola.webhook.PayloadConstantes.PAYLOAD_SELECCIONAR_FORMA_PAGO;

@ManagedBean(name = "mallaCurricularController")
@ViewScoped
@SuppressWarnings("serial")
public class MallaController extends GeneralController {

	@EJB
	UsuarioDao usuarioDao = new UsuarioDao();
	
	@EJB
	ProveedorDao carreraDao = new ProveedorDao();		
	
	 
	private static final String URL_IMAGES_ECOMMERCE = System.getProperty("lola.base.url")+"/images/E-commerce/";
	private Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	private Usuario usuarioSeleccionado;
	private Proveedor carreraSeleccionada;
	private String urlEcommerce;
	private String urlMalla;
	
	@PostConstruct
	public void inicializar() {
		usuarioSeleccionado = new Usuario();
		carreraSeleccionada = new Proveedor();
		params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		usuarioSeleccionado = usuarioDao.obtenerPorId(Long.parseLong(params.get("iu")));
		carreraSeleccionada = carreraDao .obtenerPorId(Long.parseLong(params.get("ip")));
		urlMalla = carreraSeleccionada.getUrl();
		
	}

	public void logueoConFacebook(String correoFacebook) {
		
	}

	public void registroNuevoCorreo() {
			//Activar botones de registro
	}
	
	
	
	public void iniciarFacebook() {
		RequestContext.getCurrentInstance().execute("fbLogin();");	
	}
	
	public Usuario getUsuarioSeleccionado() {
		return usuarioSeleccionado;
	}

	public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) {
		this.usuarioSeleccionado = usuarioSeleccionado;
	}

	public String getUrlEcommerce() {
		return urlEcommerce;
	}

	public void setUrlEcommerce(String urlEcommerce) {
		this.urlEcommerce = urlEcommerce;
	}

	public UsuarioDao getUsuarioDao() {
		return usuarioDao;
	}

	public void setUsuarioDao(UsuarioDao usuarioDao) {
		this.usuarioDao = usuarioDao;
	}

	public ProveedorDao getCarreraDao() {
		return carreraDao;
	}

	public void setCarreraDao(ProveedorDao carreraDao) {
		this.carreraDao = carreraDao;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public Proveedor getCarreraSeleccionada() {
		return carreraSeleccionada;
	}

	public void setCarreraSeleccionada(Proveedor carreraSeleccionada) {
		this.carreraSeleccionada = carreraSeleccionada;
	}

	public String getUrlMalla() {
		return urlMalla;
	}

	public void setUrlMalla(String urlMalla) {
		this.urlMalla = urlMalla;
	}
	
	
	


	
}