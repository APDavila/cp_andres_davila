package com.holalola.comida.facebook.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.swing.plaf.synth.SynthSpinnerUI;

import com.componente_practico.ejb.general.servicio.VerPerfilServicio;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.Utilidades;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.ProductoDao;
import com.holalola.comida.pedido.ejb.dao.ProductoDao.DetallesProducto;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.dao.SubmenuProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.Producto;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.comida.pedido.ejb.modelo.SubmenuProveedor;
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
public class CategoriaController extends GeneralController {

	@EJB
	VerPerfilServicio verPerfilServicio;

	@EJB
	VerPerfilDao verPerfilDao;

	@EJB
	private UbicacionUsuarioDao ubicacionUsuarioDao;
	
	@EJB
	private ProductoDao productoDao;
	
	@EJB
	private SubmenuProveedorDao submenuProveedorDao;
	
	@EJB
	private ProveedorDao proveedorDao;
	

	@EJB
	private PedidoDao pedidoDao;
	
	private static final String baseUrl = System.getProperty("lola.base.url");
	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
			.getRequestParameterMap();
	List<UbicacionUsuario> listaUbicacionUsuario = new ArrayList<UbicacionUsuario>();
	List<DetallesProducto> listaProductos = new ArrayList<DetallesProducto>();
	private List<SubmenuProveedor> listaSubMenus = new ArrayList<SubmenuProveedor>();
	public String idUsuario = params.get("usu");
	public String idProveedor = params.get("pro");
	public String idSubeMenuProveedor = params.get("cat");
	private Usuario usuario;
	private String urlImagenProveedor;
	private String tituloProveedor,linkCarrito;
	private Proveedor proveedorActual;
	private boolean ab_tienePedidosenProceso;
	
	@PostConstruct
	public void inicializar() {
		params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		
		idUsuario = params.get("usu");
		
		System.out.println("-----------------------------inicializar-----armarUrlVerPerfil  "+idUsuario);
		ab_tienePedidosenProceso=pedidoDao.VerificarPedidosPendientes(Long.parseLong(idUsuario),Long.parseLong(idProveedor));
		proveedorActual=proveedorDao.obtenerPorId(Long.parseLong(idProveedor));
		usuario = verPerfilServicio.obtenerPorId(Long.parseLong(idUsuario));
		listaUbicacionUsuario = ubicacionUsuarioDao.listaUbicaciones(usuario);
		listaSubMenus= submenuProveedorDao.obtenerSubmenuPorProveedor(Long.parseLong(idProveedor), true,"");
		listaProductos= productoDao.obtenerProductoProveedor(idUsuario,Long.parseLong(idProveedor),idSubeMenuProveedor , true, "", false);
		tituloProveedor=proveedorActual.getNombre();
		urlImagenProveedor=proveedorActual.getLogo();
		linkCarrito=baseUrl + "webviews/facebook/carritoCompras.jsf?pro="+idProveedor+"&usu="+idUsuario;
		if(ab_tienePedidosenProceso) {
			
		}
		
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

		
	public String traerArchivoImagen(String nombreArchivo) {
		if (nombreArchivo.equals(null) || nombreArchivo.trim().length() == 0)
			nombreArchivo = System.getProperty("lola.base.url") + "/images/emoji ejemplo.jpg";
		return nombreArchivo;
	}

	

	public List<DetallesProducto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<DetallesProducto> listaProductos) {
		this.listaProductos = listaProductos;
	}

	public List<SubmenuProveedor> getListaSubMenus() {
		return listaSubMenus;
	}

	public void setListaSubMenus(List<SubmenuProveedor> listaSubMenus) {
		this.listaSubMenus = listaSubMenus;
	}

	public String getUrlImagenProveedor() {
		return urlImagenProveedor;
	}

	public void setUrlImagenProveedor(String urlImagenProveedor) {
		this.urlImagenProveedor = urlImagenProveedor;
	}

	public String getTituloProveedor() {
		return tituloProveedor;
	}

	public void setTituloProveedor(String tituloProveedor) {
		this.tituloProveedor = tituloProveedor;
	}

	public String getLinkCarrito() {
		return linkCarrito;
	}

	public void setLinkCarrito(String linkCarrito) {
		this.linkCarrito = linkCarrito;
	}

	
}