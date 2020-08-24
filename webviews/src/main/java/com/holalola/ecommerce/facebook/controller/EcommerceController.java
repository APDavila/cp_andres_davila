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

@ManagedBean(name = "ecommerceController")
@ViewScoped
@SuppressWarnings("serial")
public class EcommerceController extends GeneralController {

	@EJB
	UsuarioDao usuarioDao = new UsuarioDao();
	
	@EJB
	ProveedorDao proveedorDao = new ProveedorDao();
	
	@EJB
	EcommerceService ecommerceService;
	
	 
	private static final String URL_IMAGES_ECOMMERCE = System.getProperty("lola.base.url")+"/images/E-commerce/";
	private Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	private Usuario usuarioSeleccionado;
	private Proveedor proveedorSeleccionado ;
	private String urlEcommerce;
	
	@PostConstruct
	public void inicializar() {
		usuarioSeleccionado = new Usuario();
		proveedorSeleccionado = new Proveedor();
		params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		usuarioSeleccionado = usuarioDao.obtenerPorId(Long.parseLong(params.get("iu")));
		proveedorSeleccionado = proveedorDao.obtenerPorId(Long.parseLong(params.get("ip")));
//		logoCompania =  URL_IMAGES_ECOMMERCE + "Proveedores/" +new StringTokenizer(proveedorSeleccionado.getMenuPrincipal(),"_").nextToken()+"logo.png"; 
		urlEcommerce =  URL_IMAGES_ECOMMERCE +"ecommerce.png";
		
	}

	public void logueoConFacebook(String correoFacebook) {
		GetByMailResponse response = ecommerceService.findByEmail(correoFacebook);
		if(response.getCodigo()==0) {
			System.out.println("LOGEO CORRECTO");
			
		}
		else {
			System.out.println("LOGEO INCORRECTO");
		}
	}

	public void registroNuevoCorreo() {
			//Activar botones de registro
	}
	
	public void logueoOtroCorreo() {
		GetByMailResponse response = ecommerceService.findByEmail(usuarioSeleccionado.getEmail());
		System.out.println("****************************************************");
		System.out.println("****************************************************");
		System.out.println(response.getCodigo());
		System.out.println("****************************************************");
		if(response.getCodigo()==0) {
			System.out.println("LOGEO CORRECTO");
			
		}
		else {
			System.out.println("LOGEO INCORRECTO");
		}
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
	
	
	


	
}