package com.holalola.comida.facebook.controller;

import static com.holalola.webhook.PayloadConstantes.PAYLOAD_SELECCIONAR_FORMA_PAGO;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.ejb.general.servicio.VerPerfilServicio;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.comida.pedido.ejb.dao.AcompananteDetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.AcompananteDetallePedidoDao.AcompanantesPedido;
import com.holalola.comida.pedido.ejb.dao.DetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao.AuxPedido;
import com.holalola.comida.pedido.ejb.dao.PedidoDao.DetallePromocion;
import com.holalola.comida.pedido.ejb.dao.PedidoDao.ProductosPedido;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.DetallePedido;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.ejb.general.servicio.UbicacionUsuarioServicio;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.util.PedidoUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ch.qos.logback.classic.net.SimpleSocketServer;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class CarritoComprasController extends GeneralController {

	@EJB
	private ProveedorDao proveedorDao;

	@EJB
	private PedidoDao pedidoDao;

	@EJB
	private DetallePedidoDao detallePedidoDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	VerPerfilServicio verPerfilServicio;

	@EJB
	private PropiedadesLola propiedadesLola;

	@SuppressWarnings("unused")
	private static final String baseUrl = System.getProperty("lola.base.url");
	private static final String PAYLOAD_MOSTRAR_MENU_PRINCIPAL = "COMIDA_MENU_PRINCIPAL ";
	public static final String CONFIRMAR_NUMERO_PERSONAS = "CONFIRMAR_NUM_PERSONA ";
	private static final String PAYLOAD_AGREGAR_NOTA_PEDIDO = "AGREGAR_NOTA_PEDIDO ";
	private static final String PAYLOAD_ELIMINAR_PEDIDO_COMIDA = "ELIMINAR_PEDIDO_COMIDA ";
	private Usuario usuario;
	private static final String PAYLOAD_COMPLETAR_DIRECCION_PEDIDO = "COMPLETAR_DIRECCION_PEDIDO ";
	public static final String signoMas = "https://cdn.pixabay.com/photo/2013/03/29/13/39/add-97617__340.png";
	public static final String lapiz = "https://cdn.pixabay.com/photo/2013/10/04/11/48/pencil-190586__340.png";
	public static final String checkbox = "https://cdn.pixabay.com/photo/2014/04/02/10/12/checkbox-303113__340.png";
	public static final String cancelar = "https://cdn.pixabay.com/photo/2016/03/31/18/31/cancel-1294426__340.png";
	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	public String idProveedor, payload,mensajePromocion;
	public String idUsuario, idProducto, linkRegresar;
	private List<Pedido> pedido = new ArrayList<Pedido>();
	Proveedor proveedorSeleccionado;
	private List<ProductosPedido> listaProductosPedido = new ArrayList<ProductosPedido>();
	private boolean lb_existePedido, lb_tieneAcompanantes;
	private String subTotal, costoDomicilio, costoTotal, descuento,mensajeVisible="none";
	private List<DetallePromocion> listaPromociones =new ArrayList<DetallePromocion>();

	@PostConstruct
	public void inicializar() {
		params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idUsuario = params.get("usu");
		idProveedor = params.get("pro");
		payload = params.get("pl");
		proveedorSeleccionado = proveedorDao.obtenerPorId(Long.parseLong(idProveedor));
		linkRegresar = baseUrl + "webviews/facebook/iniciarCompra.jsf?iu=" + idUsuario + "&ir=" + idProveedor + "&pl="
				+ payload;
		usuario = verPerfilServicio.obtenerPorId(Long.parseLong(idUsuario));
		pedido = pedidoDao.obtenerPendientePorUsuarioConIds(Long.parseLong(idUsuario), Long.parseLong(idProveedor));
		obtenerDetalleProductos();


	}

	public void obtenerDetalleProductos() {
		listaProductosPedido = pedidoDao.devuelveProductosPedido(Long.parseLong(idUsuario),
				Long.parseLong(idProveedor));
		if (listaProductosPedido != null) {
			lb_existePedido = true;
			if (listaProductosPedido.size() == 0) {
				subTotal = "0.00";
				costoDomicilio = "0.00";
				costoTotal = "0.00";
				descuento = "0.00";
			} else {
				if (pedido.size() == 0) {
				} else {
					listaPromociones = pedidoDao.devuelvePromociones(pedido.get(0).getId());
					if (listaPromociones.size() == 0) {
						subTotal = listaProductosPedido.get(0).getSubtotal();
						costoDomicilio = listaProductosPedido.get(0).getValorDomicilio();
						costoTotal = listaProductosPedido.get(0).getCostoTotal();
						descuento = "0.00";
					} else {
						DecimalFormat df = new DecimalFormat("#.00");
						subTotal = df.format(listaPromociones.get(0).getSubtotal().doubleValue());						
						descuento = "-"+String.valueOf(df.format(listaPromociones.get(0).getSubtotal().doubleValue()
								- Double.valueOf(listaProductosPedido.get(0).getSubtotal())));
						costoDomicilio = listaProductosPedido.get(0).getValorDomicilio();
						costoTotal = listaProductosPedido.get(0).getCostoTotal();
					}

				}
			}
		} else {
			lb_existePedido = false;
		}
		if (listaPromociones.size() != 0)
		{
			mensajePromocion="Estimado "+ usuario.getNombreFacebook()+", "+proveedorSeleccionado.getNombre()+" y Hola Lola te regalan un "+listaPromociones.get(0).getPorcentaje().toString()+"% de descuento en tu compra de "+listaPromociones.get(0).getSubtotal();
			mensajeVisible="contents";
		}
		else
			mensajeVisible="none";
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

	public void eliminarPedido() {
		boolean existePedido;
		idUsuario = params.get("usu");
		idProveedor = params.get("pro");
		existePedido = pedidoDao.cancelarPedido(Long.parseLong(idUsuario), Long.parseLong(idProveedor));
		if (existePedido)
			Mensaje("Pedido Cancelado", false, false);
		else
			Mensaje("No tiene productos en su carrito", false, false);
		FacesContext contex = FacesContext.getCurrentInstance();
		try {
			contex.getExternalContext().redirect(baseUrl + "webviews/facebook/carritoCompras.jsf?pro=" + idProveedor
					+ "&usu=" + idUsuario + "&pl=" + payload);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	public void regresarChat() throws IOException {
		List<QuickReplyGeneral> buttonsElemt = new ArrayList<QuickReplyGeneral>();
		pedido = pedidoDao.obtenerPendientePorUsuarioConIds(Long.parseLong(idUsuario), Long.parseLong(idProveedor));
		if (pedido.size() == 0) {
			Mensaje("No tienes ningun producto en tu carrito", true, false);
			return;
		} else {
			if (proveedorSeleccionado.getCompraMinima() > Float.parseFloat(costoTotal)) {
				Mensaje(String.format("Estimado cliente su compra minima debe ser de $ %s",
						proveedorSeleccionado.getCompraMinima()), true, false);
				return;
			}

			UbicacionUsuario ultimaUbicacion = ubicacionUsuarioServicio.obtenerUltimaUsuario(usuario);
			String payloadComprar = ultimaUbicacion.isConfirmadoUsuario()
					? PAYLOAD_SELECCIONAR_FORMA_PAGO + pedido.get(0).getId()
					: PAYLOAD_COMPLETAR_DIRECCION_PEDIDO + pedido.get(0).getId();
			buttonsElemt.add(new TextQuickReply("Pagar", payloadComprar, checkbox));
		}

		String speech = String.format("Listo %s, continuemos con tu pago..!!", usuario.getNombreFacebook());
		
		ConsultarFacebook.postToFacebook(
				new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech.toString(), buttonsElemt)),
				propiedadesLola.facebookToken);
		RequestContext.getCurrentInstance().execute("closeView();");
	}

	public void eliminarProducto(ProductosPedido productoPedido) {
		detallePedidoDao.inactivarProductoPedido(productoPedido.getIdDetallePedido(), productoPedido.getTotal(),
				pedido.get(0).getId());
		FacesContext contex = FacesContext.getCurrentInstance();
		try {
			contex.getExternalContext().redirect(
					baseUrl + "webviews/facebook/carritoCompras.jsf?pro=" + idProveedor + "&usu=" + idUsuario);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void incrementarCantidad(long idDetallePedido, String cantidad) {
		if (listaPromociones.size() != 0)
		{
			detallePedidoDao.editarCantidad(pedido.get(0).getId(), idDetallePedido, (Integer.parseInt(cantidad) + 1),true,listaPromociones.get(0).getPorcentaje());
		}
		else
			detallePedidoDao.editarCantidad(pedido.get(0).getId(), idDetallePedido, (Integer.parseInt(cantidad) + 1),false,BigDecimal.ZERO);
		obtenerDetalleProductos();
	}

	public void decrementarCantidad(long idDetallePedido, String cantidad) {
		if (Integer.parseInt(cantidad) == 1)
			Mensaje("Solo te queda uno si ya no deseas este producto eliminalo de tu carrito", true, false);
		else {
			if (listaPromociones.size() != 0)
			{
				detallePedidoDao.editarCantidad(pedido.get(0).getId(), idDetallePedido, (Integer.parseInt(cantidad) - 1),true,listaPromociones.get(0).getPorcentaje());
			}
			else
				detallePedidoDao.editarCantidad(pedido.get(0).getId(), idDetallePedido, (Integer.parseInt(cantidad) - 1),false,BigDecimal.ZERO);
			obtenerDetalleProductos();
		}

	}

	public void seguirComprando() {
		FacesContext contex = FacesContext.getCurrentInstance();
		try {
			contex.getExternalContext().redirect(baseUrl + "webviews/facebook/iniciarCompra.jsf?iu=" + idUsuario
					+ "&ir=" + idProveedor + "&pl=" + payload);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String traerArchivoImagen(String nombreArchivo) {
		if (nombreArchivo.equals(null) || nombreArchivo.trim().length() == 0)
			nombreArchivo = System.getProperty("lola.base.url") + "/images/emoji ejemplo.jpg";
		return nombreArchivo;
	}

	public List<ProductosPedido> getListaProductosPedido() {
		return listaProductosPedido;
	}

	public void setListaProductosPedido(List<ProductosPedido> listaProductosPedido) {
		this.listaProductosPedido = listaProductosPedido;
	}

	public boolean isLb_existePedido() {
		return lb_existePedido;
	}

	public void setLb_existePedido(boolean lb_existePedido) {
		this.lb_existePedido = lb_existePedido;
	}

	public String getCostoDomicilio() {
		return costoDomicilio;
	}

	public void setCostoDomicilio(String costoDomicilio) {
		this.costoDomicilio = costoDomicilio;
	}

	public String getCostoTotal() {
		return costoTotal;
	}

	public void setCostoTotal(String costoTotal) {
		this.costoTotal = costoTotal;
	}

	public String getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}

	public String getLinkRegresar() {
		return linkRegresar;
	}

	public void setLinkRegresar(String linkRegresar) {
		this.linkRegresar = linkRegresar;
	}

	public String getDescuento() {
		return descuento;
	}

	public void setDescuento(String descuento) {
		this.descuento = descuento;
	}

	public String getMensajePromocion() {
		return mensajePromocion;
	}

	public void setMensajePromocion(String mensajePromocion) {
		this.mensajePromocion = mensajePromocion;
	}

	public String getMensajeVisible() {
		return mensajeVisible;
	}

	public void setMensajeVisible(String mensajeVisible) {
		this.mensajeVisible = mensajeVisible;
	}

	

}