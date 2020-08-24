package com.holalola.comida.facebook.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.behavior.AjaxBehavior;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

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
import com.holalola.ejb.general.servicio.UbicacionUsuarioServicio;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.VerPerfilDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import static com.holalola.webhook.PayloadConstantes.PAYLOAD_SELECCIONAR_FORMA_PAGO;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class IniciarCompraController extends GeneralController {

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
	private PropiedadesLola propiedadesLola;

	@EJB
	private PedidoDao pedidoDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	DetalleProductoDao detalleProductoDao;

	@EJB
	CategoriaDao categoriaDao;

	private static final String baseUrl = System.getProperty("lola.base.url");
	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	List<UbicacionUsuario> listaUbicacionUsuario = new ArrayList<UbicacionUsuario>();
	List<DetallesProducto> listaProductos = new ArrayList<DetallesProducto>();
	private List<DetalleSubMenu> listaSubMenus = new ArrayList<DetalleSubMenu>();
	public String idUsuario = params.get("iu");
	public static final String PAYLOAD_CARRITO_COMPRAS = "CARRITO_COMPRAS ";
	private static final String PAYLOAD_MOSTRAR_MENU_PRINCIPAL = "COMIDA_MENU_PRINCIPAL ";
	public String idProveedor = params.get("ir");
	private Usuario usuario;
	private String urlImagenProveedor;
	private List<Pedido> pedido = new ArrayList<Pedido>();
	private String tituloProveedor, linkCarrito, payload, as_busqueda;
	private List<Categorias> listaPrincipal = new ArrayList<Categorias>();
	private List<String> listaBusqueda=new ArrayList<String>();
	private Proveedor proveedorActual;
	private String asBusqueda;
	List<DetalleProductoDatos> listaFiltrados= new ArrayList<DetalleProductoDatos>();
	private List<Detalles> listaMostrar = new ArrayList<Detalles>();
	private boolean ab_tienePedidosenProceso;
	public static final String CONFIRMAR_NUMERO_PERSONAS = "CONFIRMAR_NUM_PERSONA ";
	private static final String PAYLOAD_AGREGAR_NOTA_PEDIDO = "AGREGAR_NOTA_PEDIDO ";
	private static final String PAYLOAD_ELIMINAR_PEDIDO_COMIDA = "ELIMINAR_PEDIDO_COMIDA ";
	private static final String PAYLOAD_COMPLETAR_DIRECCION_PEDIDO = "COMPLETAR_DIRECCION_PEDIDO ";
	public static final String signoMas = "https://cdn.pixabay.com/photo/2013/03/29/13/39/add-97617__340.png";
	public static final String lapiz = "https://cdn.pixabay.com/photo/2013/10/04/11/48/pencil-190586__340.png";
	public static final String cancelar = "https://cdn.pixabay.com/photo/2016/03/31/18/31/cancel-1294426__340.png";
	private String filtroBusqueda;
	private MenuModel modeloMenu;

	@PostConstruct
	public void inicializar() {
		System.out.println("DENTRO DEL INICIALIZAR");
		params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idUsuario = params.get("iu");
		payload = params.get("pl");
		ab_tienePedidosenProceso = pedidoDao.VerificarPedidosPendientes(Long.parseLong(idUsuario),
				Long.parseLong(idProveedor));
		proveedorActual = proveedorDao.obtenerPorId(Long.parseLong(idProveedor));
		usuario = verPerfilServicio.obtenerPorId(Long.parseLong(idUsuario));
		listaUbicacionUsuario = ubicacionUsuarioDao.listaUbicaciones(usuario);
		obtenerDatos("");
		
	}
	
	public void obtenerDatos(String nombre){
		listaBusqueda = new ArrayList<String>();
		modeloMenu = new DefaultMenuModel();
		listaPrincipal = detalleProductoDao.obtenerProductosPorProveedorPopUp(Long.parseLong(idProveedor), idUsuario,
				payload,nombre);
		int i = -1;
		for (int a =0;a<listaPrincipal.size();a++){
			if (listaPrincipal.get(a).getTieneProductos().equals("true") || a == 0 ) {
				listaBusqueda.add(listaPrincipal.get(a).getNombreCategoria());
				for(DetalleProductoDatos lista : listaPrincipal.get(a).getListaProductos()) {
					listaBusqueda.add(lista.getNombrePro());
				}
				DefaultMenuItem menuItem = new DefaultMenuItem();
				menuItem.setValue(listaPrincipal.get(a).getVerCategoria());
				menuItem.setHref("#" + listaPrincipal.get(a).getNombreCategoria());
//				menuItem.setUrl("#" + listaPrincipal.get(a).getNombreCategoria());
				modeloMenu.addElement(menuItem);
				i++;
			}
		}
		if(listaBusqueda.size()==0)
			Mensaje("No existen productos con ese nombre", true, false);
		tituloProveedor = proveedorActual.getNombre();
		urlImagenProveedor = proveedorActual.getLogo();
		linkCarrito = baseUrl + "webviews/facebook/carritoCompras.jsf?pro=" + idProveedor + "&usu=" + idUsuario + "&pl="
				+ payload;
		if (ab_tienePedidosenProceso) {
		}
	}

	@SuppressWarnings("unused")
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

	
	public List<String> completarTexto(String query) {
		List<String> results = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			results.add(query + i);
		}

		return results;
	}

	public List<String> autoCompletar(String nombre) {
		 List<String> filtrados = new ArrayList<String>();
		 for(String item : listaBusqueda) {
			if(item.contains(nombre.toUpperCase()))
				filtrados.add(item);
		 }
		 for(String item : listaBusqueda) {
				if(item.contains(nombre))
					filtrados.add(item);
		 }
		 if(filtrados.size()==0)
			 Mensaje("No existen productos con ese nombre", true, false);
		 return filtrados;
	}
	public void busqueda(String nombre) {
		obtenerDatos(nombre);
	}

	
	public String traerArchivoImagen(String nombreArchivo) {
		if (nombreArchivo.equals(null) || nombreArchivo.trim().length() == 0)
			nombreArchivo = System.getProperty("lola.base.url") + "/images/emoji ejemplo.jpg";
		return nombreArchivo;
	}

	public void categorias() {
		try {
			FacesContext contex = FacesContext.getCurrentInstance();
			contex.getExternalContext().redirect(linkCarrito = baseUrl + "webviews/facebook/carritoCompras.jsf?pro="
					+ idProveedor + "&usu=" + idUsuario + "&pl=" + payload);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	final public class Detalles {
		String nombre;
		String detalles;
		String precio;
		String urlImagen;
		String url;

		public Detalles() {
			super();
		}

		public Detalles(String nombre, String detalles, String precio, String urlImagen, String url) {
			super();
			this.nombre = nombre;
			this.detalles = detalles;
			this.precio = precio;
			this.urlImagen = urlImagen;
			this.url = url;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

		public String getDetalles() {
			return detalles;
		}

		public void setDetalles(String detalles) {
			this.detalles = detalles;
		}

		public String getPrecio() {
			return precio;
		}

		public void setPrecio(String precio) {
			this.precio = precio;
		}

		public String getUrlImagen() {
			return urlImagen;
		}

		public void setUrlImagen(String urlImagen) {
			this.urlImagen = urlImagen;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

	public List<DetallesProducto> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<DetallesProducto> listaProductos) {
		this.listaProductos = listaProductos;
	}

	public List<DetalleSubMenu> getListaSubMenus() {
		return listaSubMenus;
	}

	public void setListaSubMenus(List<DetalleSubMenu> listaSubMenus) {
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

	public List<Detalles> getListaMostrar() {
		return listaMostrar;
	}

	public void setListaMostrar(List<Detalles> listaMostrar) {
		this.listaMostrar = listaMostrar;
	}

	public List<Categorias> getListaPrincipal() {
		return listaPrincipal;
	}

	public void setListaPrincipal(List<Categorias> listaPrincipal) {
		this.listaPrincipal = listaPrincipal;
	}

	public MenuModel getModeloMenu() {
		return modeloMenu;
	}

	public void setModeloMenu(MenuModel modeloMenu) {
		this.modeloMenu = modeloMenu;
	}

	public String getAs_busqueda() {
		return as_busqueda;
	}

	public void setAs_busqueda(String as_busqueda) {
		this.as_busqueda = as_busqueda;
	}

	public List<String> getListaBusqueda() {
		return listaBusqueda;
	}

	public void setListaBusqueda(List<String> listaBusqueda) {
		this.listaBusqueda = listaBusqueda;
	}

	public String getAsBusqueda() {
		return asBusqueda;
	}

	public void setAsBusqueda(String asBusqueda) {
		this.asBusqueda = asBusqueda;
	}

	public List<DetalleProductoDatos> getListaFiltrados() {
		return listaFiltrados;
	}

	public void setListaFiltrados(List<DetalleProductoDatos> listaFiltrados) {
		this.listaFiltrados = listaFiltrados;
	}

	public String getFiltroBusqueda() {
		return filtroBusqueda;
	}

	public void setFiltroBusqueda(String filtroBusqueda) {
		this.filtroBusqueda = filtroBusqueda;
	}

	



	
}