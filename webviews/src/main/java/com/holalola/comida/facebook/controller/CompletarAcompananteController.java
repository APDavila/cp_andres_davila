package com.holalola.comida.facebook.controller;

import java.io.IOException;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.primefaces.context.RequestContext;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.ejb.general.servicio.VerPerfilServicio;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.Utilidades;
import com.holalola.comida.pedido.ejb.dao.AcompananteDetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.AcompananteProductoDao;
import com.holalola.comida.pedido.ejb.dao.AcompananteProductoDao.PreguntasAcompanantes;
import com.holalola.comida.pedido.ejb.dao.AcompananteProveedorDao;
import com.holalola.comida.pedido.ejb.dao.AcompananteProveedorDao.DatosAcompanante;
import com.holalola.comida.pedido.ejb.dao.DetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao.AcompanantesProducto;
import com.holalola.comida.pedido.ejb.dao.PedidoDao.DetallePromocion;
import com.holalola.comida.pedido.ejb.dao.PedidoTarjetaDao;
import com.holalola.comida.pedido.ejb.dao.ProductoDao;
import com.holalola.comida.pedido.ejb.dao.ProductoDao.DetallesProducto;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao.RetornoUbicacion;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.dao.SubmenuProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.AcompananteProveedor;
import com.holalola.comida.pedido.ejb.modelo.DetallePedido;
import com.holalola.comida.pedido.ejb.modelo.DetalleProducto;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.PedidoTarjeta;
import com.holalola.comida.pedido.ejb.modelo.Producto;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.comida.pedido.ejb.modelo.SubmenuProveedor;
import com.holalola.comida.pizzahut.ejb.dao.DetalleProductoDao;
import com.holalola.ejb.general.servicio.UbicacionUsuarioServicio;
import com.holalola.general.ejb.dao.UbicacionUsuarioDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.dao.VerPerfilDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.VerPerfilUbicacion;
import com.holalola.localizador.client.ValidadorLocalizacionUsuario;
import com.holalola.localizador.client.vo.ZonaVo;
import com.holalola.pagos.GacelaProcesos;
import com.holalola.pagos.GacelaProcesos.Retorno;
import com.holalola.util.PedidoUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeExternoParaFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import static com.holalola.util.FechaUtil.isHanPasadoHoras;
import static com.holalola.util.TextoUtil.esVacio;
import static com.holalola.webhook.PayloadConstantes.PAYLOAD_SELECCIONAR_FORMA_PAGO;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class CompletarAcompananteController extends GeneralController {

	@EJB
	VerPerfilServicio verPerfilServicio;

	@EJB
	VerPerfilDao verPerfilDao;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	AcompananteProductoDao acompananteProductoDao;

	@EJB
	AcompananteProveedorDao acompananteProveedorDao;

	@EJB
	private UbicacionUsuarioDao ubicacionUsuarioDao;

	@EJB
	private ProductoDao productoDao;

	@EJB
	private PedidoDao pedidoDao;

	@EJB
	private UsuarioDao usuarioDao;

	@EJB
	private SubmenuProveedorDao submenuProveedorDao;

	@EJB
	private ProveedorDao proveedorDao;

	@EJB
	DetalleProductoDao detalleProductoDao;

	@EJB
	DetallePedidoDao detallePedidoDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	PedidoTarjetaDao pedidoTarjetaDao;

	@EJB
	AcompananteDetallePedidoDao acompananteDetallePedidoDao;

	private static final String PARAM_ID_DETALLE_PRODUCTO = "idDetalleProducto.original";
	public static final String CONTEXTO_ACOMPANANTE_PRODUCTO = "acompanante_producto";
	private static final String baseUrl = System.getProperty("lola.base.url");
	private static final String PAYLOAD_AGREGAR_NOTA_PEDIDO = "AGREGAR_NOTA_PEDIDO ";
	private static final String PAYLOAD_ELIMINAR_PEDIDO_COMIDA = "ELIMINAR_PEDIDO_COMIDA ";
	private static final String PAYLOAD_COMPLETAR_DIRECCION_PEDIDO = "COMPLETAR_DIRECCION_PEDIDO ";
	private static final String PAYLOAD_MOSTRAR_MENU_PRINCIPAL = "COMIDA_MENU_PRINCIPAL ";
	public static final String signoMas = "https://cdn.pixabay.com/photo/2013/03/29/13/39/add-97617__340.png";
	public static final String lapiz = "https://cdn.pixabay.com/photo/2013/10/04/11/48/pencil-190586__340.png";
	public static final String cancelar = "https://cdn.pixabay.com/photo/2016/03/31/18/31/cancel-1294426__340.png";

	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	List<UbicacionUsuario> listaUbicacionUsuario = new ArrayList<UbicacionUsuario>();
	List<DetallesProducto> listaProductos = new ArrayList<DetallesProducto>();
	List<PreguntasAcompanantes> listaPreguntas = new ArrayList<PreguntasAcompanantes>();
	public String idProveedor;
	private List<SubmenuProveedor> listaSubMenus = new ArrayList<SubmenuProveedor>();
	public String idUsuario, idProducto, idDetalleProducto, linkCarro;
	private Producto productoActivo;
	private String urlImagenProducto, nombreProducto, detallesProducto;
	private String tituloProveedor, linkMenu, linkCarrito, imagenCarrito, payload;
	private boolean lb_tieneAcompanantes, ab_tienePedidosenProceso, lb_existeProductoEnPedido;
	private int cantidadProducto;
	private Usuario usuario;
	private List<Pedido> pedido = new ArrayList<Pedido>();
	private int ai_acompanantes = 0, indice;
	private long al_idAcompananteProducto;
	private long seleccionAcompanante;
	private int ind = 0;
	private List<AuxiliarAcompanante> listaAcompanantes = new ArrayList<AuxiliarAcompanante>();
	public static final String CONFIRMAR_NUMERO_PERSONAS = "CONFIRMAR_NUM_PERSONA ";
	private List<DetallePromocion> listaPromociones =new ArrayList<DetallePromocion>();
	
	@PostConstruct
	public void inicializar() {
		listaAcompanantes = new ArrayList<AuxiliarAcompanante>();
		cantidadProducto = 1;
		params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idUsuario = params.get("usu");
		usuario = verPerfilServicio.obtenerPorId(Long.parseLong(idUsuario));
		idProveedor = params.get("pro");
		idProducto = params.get("iu");
		payload = params.get("pl");
		idDetalleProducto = params.get("det");
		imagenCarrito = "https://lola.nuo.com.ec/images/hl_images/carrito.jpg";
		linkCarrito = baseUrl + "webviews/facebook/iniciarCompra.jsf?iu=" + idUsuario + "&ir=" + idProveedor + "&pl="
				+ payload;
		linkCarro = baseUrl + "webviews/facebook/carritoCompras.jsf?pro=" + idProveedor + "&usu=" + idUsuario + "&pl="
				+ payload;
//				baseUrl + "webviews/facebook/iniciarCompra.jsf?pro=" + idProveedor + "&usu=" + idUsuario + "&pl="
//				+ payload;
		productoActivo = productoDao.obtenerPorId(Long.parseLong(idProducto));
		urlImagenProducto = productoActivo.getUrlImagen();
		nombreProducto = productoActivo.getNombre();
		detallesProducto = productoActivo.getDescripcion();
		linkMenu = baseUrl + "webviews/facebook/iniciarCompra.jsf?iu=" + idUsuario + "&ir=" + idProveedor + "&pl="
				+ payload;
		listaPreguntas = acompananteProductoDao.devuelvePreguntasAcompanantes(Long.parseLong(idProducto),
				Long.parseLong(idProveedor));
		if (listaPreguntas.size() == 0) {
			lb_tieneAcompanantes = false;
		} else {
			lb_tieneAcompanantes = true;
		}

	}

	public void regresarChat() {
		List<QuickReplyGeneral> buttonsElemt = new ArrayList<QuickReplyGeneral>();
		pedido = pedidoDao.obtenerPendientePorUsuarioConIds(Long.parseLong(idUsuario), Long.parseLong(idProveedor));
		if (pedido.size() == 0) {
			return;
		} else {
			UbicacionUsuario ultimaUbicacion = ubicacionUsuarioServicio.obtenerUltimaUsuario(usuario);
			@SuppressWarnings("unused")
			String payloadComprar = ultimaUbicacion.isConfirmadoUsuario()
					? PAYLOAD_SELECCIONAR_FORMA_PAGO + pedido.get(0).getId()
					: PAYLOAD_COMPLETAR_DIRECCION_PEDIDO + pedido.get(0).getId();
		}
		FacesContext fc = FacesContext.getCurrentInstance();
		fc.getExternalContext().setClientWindow(null);
		String speech = String.format("Listo %s, elige lo que deseas hacer?", usuario.getNombreFacebook());
		RequestContext.getCurrentInstance().execute("closeView();");
		ConsultarFacebook.postToFacebook(
				new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech, buttonsElemt)),
				propiedadesLola.facebookToken);
	}

	@SuppressWarnings("unused")
	public void anadirCarrito() {
		pedido = pedidoDao.obtenerPendientePorUsuarioConIds(Long.parseLong(idUsuario), Long.parseLong(idProveedor));
		FacesContext contex = FacesContext.getCurrentInstance();
		if (cantidadProducto <= 0) {
			Mensaje("Debes seleccionar una cantidad válida", false, true);
			return;
		}
		Usuario usuario = usuarioDao.obtenerPorId(Long.parseLong(idUsuario));
		if (pedido.size() == 0) {
			// insert si el pedido no existe
			if (!lb_tieneAcompanantes) {
				final Pedido pedidoInsertado = agregarProductoAPedido(usuario, Long.parseLong(idDetalleProducto),
						cantidadProducto, null);
				Mensaje("Producto agregado con exito.!!!", false, false);
				try {
					contex.getExternalContext().redirect(linkCarrito);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				if (listaAcompanantes.size() == listaPreguntas.size()) {
					ab_tienePedidosenProceso = pedidoDao.VerificarPedidosPendientes(Long.parseLong(idUsuario),
							Long.parseLong(idProveedor));
					final Pedido pedidoInsertado = agregarProductoAPedido(usuario, Long.parseLong(idDetalleProducto),
							cantidadProducto, null);
					List<DetallePedido> detallePedido = detallePedidoDao.obtenerPorPedido(pedidoInsertado);
					for (int ai_i = 0; ai_i < listaAcompanantes.size(); ai_i++) {
						acompananteDetallePedidoDao.insertarAcompanantesTresTablas(
								detallePedido.get(detallePedido.size() - 1).getId(),
								listaAcompanantes.get(ai_i).getIdAcompananteProducto(), pedidoInsertado.getId(),
								listaAcompanantes.get(ai_i).getPrecio(), cantidadProducto);
					}
					try {
						contex.getExternalContext().redirect(linkCarrito);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Mensaje("Producto agregado con exito.!!!", false, false);
					if (ab_tienePedidosenProceso) {

					}
				} else {
					Mensaje("Debes seleccionar todos los acompañantes", false, true);
					return;
				}
			}
		} // si el pedido ya existe
		else {
			List<DetallePedido> detallePedidoRepetidos = detallePedidoDao.obtenerPorPedido(pedido.get(0));
			if (!lb_tieneAcompanantes) {
				listaPromociones = pedidoDao.devuelvePromociones(pedido.get(0).getId());
				if (listaPromociones.size() == 0) {
					lb_existeProductoEnPedido = pedidoDao.verificarInsertarProductoExistentePedido(
							Long.parseLong(idDetalleProducto), pedido.get(0).getId(), cantidadProducto,false,BigDecimal.ZERO,0L);
				}else {
					lb_existeProductoEnPedido = pedidoDao.verificarInsertarProductoExistentePedido(
							Long.parseLong(idDetalleProducto), pedido.get(0).getId(), cantidadProducto,true,listaPromociones.get(0).getPorcentaje(),listaPromociones.get(0).getIdPedidoProveedorPromoAplic());
				}
					
				if (!lb_existeProductoEnPedido) {
					final Pedido pedidoInsertado = agregarProductoAPedido(usuario, Long.parseLong(idDetalleProducto),
							cantidadProducto, null);
					Mensaje("Producto agregado con exito.!!!", false, false);
					try {
						contex.getExternalContext().redirect(linkCarrito);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Mensaje("Producto agregado con exito.!!!", false, false);
					try {
						contex.getExternalContext().redirect(linkCarrito);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} else {
				if (listaAcompanantes.size() == listaPreguntas.size()) {
					ab_tienePedidosenProceso = pedidoDao.VerificarPedidosPendientes(Long.parseLong(idUsuario),
							Long.parseLong(idProveedor));
					List<AcompanantesProducto> verificadorAcompanantes= pedidoDao.DevuelveIdsAcompanantesProducto(pedido.get(0).getId(), Long.parseLong(idDetalleProducto))	;
					int ai_compa=0;
					int ai_mismosAcompanantes=0;
					int index=0;
					while(verificadorAcompanantes.size()!=ai_compa) {
						for (int ai_i = 0; ai_i < listaAcompanantes.size(); ai_i++) {
							if(listaAcompanantes.get(ai_i).getIdAcompananteProducto()==verificadorAcompanantes.get(ai_compa).getIdAcompananteProducto()) {
								ai_mismosAcompanantes++;
								ai_compa++;
								if(ai_mismosAcompanantes==listaAcompanantes.size()) {
									index=ai_compa-1;
									ai_compa=verificadorAcompanantes.size();
									break;
								}
							}							
							else {
								ai_compa=ai_compa+(listaAcompanantes.size()-ai_i);
								if(ai_compa>verificadorAcompanantes.size())
									ai_compa=verificadorAcompanantes.size();
								break;
							}
								
						}	
					}
					if(ai_mismosAcompanantes==listaAcompanantes.size()) {
						listaPromociones = pedidoDao.devuelvePromociones(pedido.get(0).getId());
						if (listaPromociones.size() == 0) {
							detallePedidoDao.editarCantidad(pedido.get(0).getId(), verificadorAcompanantes.get(index).getIdDetallePedido(), verificadorAcompanantes.get(index).getCantidadProducto()+cantidadProducto,false,BigDecimal.ZERO);
						}else
							detallePedidoDao.editarCantidad(pedido.get(0).getId(), verificadorAcompanantes.get(index).getIdDetallePedido(), verificadorAcompanantes.get(index).getCantidadProducto()+cantidadProducto,true,listaPromociones.get(0).getPorcentaje());
					}else {
						final Pedido pedidoInsertado = agregarProductoAPedido(usuario,
								Long.parseLong(idDetalleProducto), cantidadProducto, null);
						List<DetallePedido> detallePedido = detallePedidoDao.obtenerPorPedido(pedidoInsertado);
						for (int ai_i = 0; ai_i < listaAcompanantes.size(); ai_i++) {
							acompananteDetallePedidoDao.insertarAcompanantesTresTablas(
									detallePedido.get(detallePedido.size() - 1).getId(),
									listaAcompanantes.get(ai_i).getIdAcompananteProducto(), pedidoInsertado.getId(),
									listaAcompanantes.get(ai_i).getPrecio(), cantidadProducto);
						}
					}
						try {
							contex.getExternalContext().redirect(linkCarrito);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					Mensaje("Producto agregado con exito.!!!", false, false);
					if (ab_tienePedidosenProceso) {

					}
				} else {
					Mensaje("Debes seleccionar todos los acompañantes", false, true);
					return;
				}
			}

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

	public void seleccionandoAcompanante(long idAuxiliarParaLista) {
		boolean lb_flag = false;
		String precio = "";
		int ai_f = 0;
		for (PreguntasAcompanantes item : listaPreguntas) {
			if (item.getId() == idAuxiliarParaLista) {
				for (DatosAcompanante itema : listaPreguntas.get(ai_f).getListaAcompanante()) {
					if (itema.getIdAcompananteProducto() == al_idAcompananteProducto) {
						precio = itema.getPrecio();
						break;
					}
				}
				break;
			}
			ai_f++;
		}
		if (listaAcompanantes.size() == 0) {
			AuxiliarAcompanante acompanantes = new AuxiliarAcompanante();
			acompanantes.setId(idAuxiliarParaLista);
			acompanantes.setIdAcompananteProducto(al_idAcompananteProducto);
			acompanantes.setPrecio(precio);
			listaAcompanantes.add(acompanantes);
		} else {
			int i = 0;
			for (AuxiliarAcompanante item : listaAcompanantes) {
				if (item.getId() == idAuxiliarParaLista) {
					item.setIdAcompananteProducto(al_idAcompananteProducto);
					item.setPrecio(precio);
					listaAcompanantes.set(i, item);
					lb_flag = true;
				}
				i++;
			}
			if (!lb_flag) {
				AuxiliarAcompanante acompanantes = new AuxiliarAcompanante();
				acompanantes.setId(idAuxiliarParaLista);
				acompanantes.setIdAcompananteProducto(al_idAcompananteProducto);
				acompanantes.setPrecio(precio);
				listaAcompanantes.add(acompanantes);
			}
		}

	}

	private Pedido agregarProductoAPedido(Usuario usuario, Long idDetalleProducto, int cantidad, String observaciones) {
		return validarProductoAPedido(usuario, idDetalleProducto, cantidad, observaciones);
	}

	public Pedido validarProductoAPedido(Usuario usuario, long idDetalleProducto, int cantidad, String observaciones) {

		final DetalleProducto detalleProducto = detalleProductoDao.obtenerPorId(idDetalleProducto);

		final Pedido pedido = verificarNuevoPedido(usuario, detalleProducto.getProducto().getProveedor());

		if (pedido == null)
			return pedido;

		final List<DetallePedido> detallesPedido = new ArrayList<>(detallePedidoDao.obtenerPorPedido(pedido));

		DetallePedido detallePedido = agregarDetalleProductoADetallePedido(pedido, detalleProducto, cantidad,
				observaciones);

		// Puede estar en nulo si el pedido esta en pendiente
		if (detallePedido != null) {
			detallesPedido.add(detallePedido);
			pedido.setFecha(new Date());
			PedidoUtil.calcularTotalesPedido(pedido, detallesPedido);
			pedidoDao.modificar(pedido);
			pedidoDao.aplicaPromocionesProveedor(pedido.getId());

			pedido.setDetallesPedido(detallesPedido);
		}

		return pedido;
	}

	private DetallePedido agregarDetalleProductoADetallePedido(Pedido pedido, DetalleProducto detalleProducto,
			int cantidad, String observaciones) {

		if (pedido.isEnproceso())
			return null;

		return detallePedidoDao.insertar(new DetallePedido(pedido, detalleProducto, cantidad, observaciones));
	}

	public Pedido verificarNuevoPedido(Usuario usuario, Proveedor proveedorProducto) {

		try {
			final List<Pedido> pedidosTot = pedidoDao.listaPendientePorUsuario(usuario);

			for (Pedido pedido : pedidosTot) {
				if (pedido.isEnproceso()) {
					return null;
				} else {
					List<PedidoTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
					PedidoTarjeta pedidoTarjeta = listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0
							? listaPedidoTarjeta.get(0)
							: null;

					if (pedidoTarjeta != null && pedidoTarjeta.getConfirmado() && !pedidoTarjeta.getReversado()
							&& pedido.getAutorizacionPago() != null
							&& pedido.getAutorizacionPago().trim().length() > 0) {
						return null;
					}
				}
			}
		} catch (Exception err) {

		}

		final List<Pedido> pedidos = obtenerPedidoPendienteUsuario(usuario, proveedorProducto);

		if (pedidos.isEmpty()) {
			// Pedido nuevo
			return crearNuevoPedido(usuario, proveedorProducto);

		} else if (pedidos.get(0).getProveedor().equals(proveedorProducto)
				&& !isHanPasadoHoras(1, pedidos.get(0).getFecha())) {
			// Es un pedido que esta en proceso
			return pedidos.get(0);

		} else {
			// Existe un pedido pero es de otro proveedor o ya es muy antiguo y hay que
			// hacer uno nuevo
			return pedidos.get(0);
//			eliminarPedido(pedidos.get(0));
//			return crearNuevoPedido(usuario, proveedorProducto);
		}
	}

	private void eliminarPedido(Pedido pedido) {
		// detallePedidoDao.eliminarTodos(pedido);
		// datoFacturaPedidoDao.eliminarTodosPedido(pedido);
		// detallePedidoCineDao.eliminarTodosPedido(pedido);
		if (!pedido.isEnproceso()) {
			List<PedidoTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
			PedidoTarjeta pedidoTarjeta = listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0
					? listaPedidoTarjeta.get(0)
					: null;

			boolean eliminar = true;
			if (pedidoTarjeta != null && pedidoTarjeta.getConfirmado() && !pedidoTarjeta.getReversado()
					&& pedido.getAutorizacionPago() != null && pedido.getAutorizacionPago().trim().length() > 0) {
				eliminar = false;
			}
			if (eliminar)
				pedidoDao.eliminar(pedido);
		}
	}
	
//	public static boolean tieneCobertura(UbicacionUsuario ubicacionUsuario, long proveedorId) {
//		return ValidadorLocalizacionUsuario.tieneCobertura(ubicacionUsuario.getLatitud(),
//				ubicacionUsuario.getLongitud(), proveedorId);
//	}

	private Pedido crearNuevoPedido(Usuario usuario, Proveedor proveedorProducto) {
		Long li_establecimiento = (long) 0;
		BigDecimal bd_valorADomimciolo = proveedorProducto.getValorAdicionalDomicilio();

		Retorno retorno = null;

//REVISAR COMENTARIOS EN COMPLETAR ACOMPANANTECONTROLLER
		if (!proveedorProducto.isPagoGacela()) {
			try {
				final UbicacionUsuario ultimaUbicacion = ubicacionUsuarioServicio.obtenerUltimaUsuario(usuario);
				
				ZonaVo zona = ValidadorLocalizacionUsuario.obtenerZonaCobertura(ultimaUbicacion.getLatitud(),
						ultimaUbicacion.getLongitud(), proveedorProducto.getId());
				
				if (proveedorProducto.getDividirPedidoPorLocales()) {
					
					li_establecimiento = proveedorDao.dameIdLocalAtencion(proveedorProducto, ultimaUbicacion.getCiudad(),
							zona.getNombreGeneral());
				}
				
				bd_valorADomimciolo = proveedorDao.dameValorEntregaDomProveedor(proveedorProducto, ultimaUbicacion.getCiudad(),
						zona.getNombreGeneral());

			} catch (Exception err) {
				System.out.println("-----------------------------bd_valorADomimciolo-------" + err + "--");
			}
		} else {
			try {

				final UbicacionUsuario ultimaUbicacion = ubicacionUsuarioServicio.obtenerUltimaUsuario(usuario);
				ZonaVo zona = ValidadorLocalizacionUsuario.obtenerZonaCobertura(ultimaUbicacion.getLatitud(),
						ultimaUbicacion.getLongitud(), proveedorProducto.getId());
				if (proveedorProducto.getDividirPedidoPorLocales()) {
					
					li_establecimiento = proveedorDao.dameIdLocalAtencion(proveedorProducto, ultimaUbicacion.getCiudad(),
							zona.getNombreGeneral());
				}
				RetornoUbicacion retornoUbicacion = proveedorDao.dameUbicacionProveedor(proveedorProducto, ultimaUbicacion.getCiudad(),
						zona.getNombreGeneral());
				if(retornoUbicacion == null)
				{
					System.out.println("----------------------retornoUbicacion---  No se puede crear el pedido ----No se puede cargar ubicacion de -------"+
										proveedorProducto.getNombre().trim()+"-------- ciudad ----- "+(ultimaUbicacion == null ? "Sin Ciudad" : ultimaUbicacion.getCiudad())
										+"-------- zona ----- "+(zona == null ? "Sin zona" : zona.getNombreGeneral()));					
					return null;
				}
				else
				{
					System.out.println("*****************************************************");
					System.out.println("DENTRO DE GASCELA PROCESOS");
					System.out.println("*****************************************************");
				
					retorno = GacelaProcesos.consultarValor(String.valueOf(retornoUbicacion.getLatitud()).replaceAll(",", "."), 
												  String.valueOf(retornoUbicacion.getLongitud()).replaceAll(",", "."),
												  String.valueOf(ultimaUbicacion.getLatitud()).replaceAll(",", "."),
												  String.valueOf(ultimaUbicacion.getLongitud()).replaceAll(",", "."));
					
					if(retorno != null)
						bd_valorADomimciolo = BigDecimal.valueOf(retorno.getValor());
					else
					{
						System.out.println("-----No retorna valor--GacelaProcesos.consultarValor--- as_latitudOrigen--"+
																					String.valueOf(retornoUbicacion.getLatitud()).replaceAll(",", ".") +
																					" -- as_longitudOrigen --"+
																					String.valueOf(retornoUbicacion.getLongitud()).replaceAll(",", ".") +
																					" -- as_latitudDestino --"+
																					String.valueOf(ultimaUbicacion.getLatitud()).replaceAll(",", ".") +
																					" -- as_longitudDestino --"+
																					String.valueOf(ultimaUbicacion.getLongitud()).replaceAll(",", ".") 
																					);					
						return null;
					}
					
					System.out.println("*****************************************************");
					System.out.println("VALOR A DOMICILIO" +bd_valorADomimciolo);
					System.out.println("*****************************************************");
				
				}

			} catch (Exception err) {
				System.out.println("-----------------------------bd_valorADomimciolo-------" + err + "--");
				err.printStackTrace();
			}
		}

		Pedido pedido = pedidoDao.insertar(new Pedido(proveedorProducto, usuario, UUID.randomUUID().toString(),
				li_establecimiento, bd_valorADomimciolo, false, 0, 0, 0, "", pedidoDao.obtenerImpuesto()));

		if (proveedorProducto.isPagoGacela())
			proveedorDao.guadaProcesoGacela(pedido.getId(), retorno.getRetorno(), "", 1, 0, 0);

		return pedido;
	}

	public final class AuxiliarAcompanante {
		long id;
		long idAcompananteProducto;
		String precio;

		public AuxiliarAcompanante(long id, long idAcompananteProducto, String precio) {
			super();
			this.id = id;
			this.idAcompananteProducto = idAcompananteProducto;
			this.precio = precio;
		}

		public AuxiliarAcompanante() {
			super();
		}

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public long getIdAcompananteProducto() {
			return idAcompananteProducto;
		}

		public void setIdAcompananteProducto(long idAcompananteProducto) {
			this.idAcompananteProducto = idAcompananteProducto;
		}

		public String getPrecio() {
			return precio;
		}

		public void setPrecio(String precio) {
			this.precio = precio;
		}

	}

	public List<Pedido> obtenerPedidoPendienteUsuario(Usuario usuario, Proveedor proveedor) {
		return pedidoDao.obtenerPendientePorUsuario(usuario, proveedor);
	}

	public void incrementarCantidad() {
		
		cantidadProducto++;
	}

	public void decrementarCantidad() {
		if (cantidadProducto > 1)
			cantidadProducto--;
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

	public String getUrlImagenProducto() {
		return urlImagenProducto;
	}

	public void setUrlImagenProducto(String urlImagenProducto) {
		this.urlImagenProducto = urlImagenProducto;
	}

	public String getNombreProducto() {
		return nombreProducto;
	}

	public void setNombreProducto(String nombreProducto) {
		this.nombreProducto = nombreProducto;
	}

	public String getDetallesProducto() {
		return detallesProducto;
	}

	public void setDetallesProducto(String detallesProducto) {
		this.detallesProducto = detallesProducto;
	}

	public String getTituloProveedor() {
		return tituloProveedor;
	}

	public void setTituloProveedor(String tituloProveedor) {
		this.tituloProveedor = tituloProveedor;
	}

	public List<PreguntasAcompanantes> getListaPreguntas() {
		return listaPreguntas;
	}

	public void setListaPreguntas(List<PreguntasAcompanantes> listaPreguntas) {
		this.listaPreguntas = listaPreguntas;
	}

	public boolean isLb_tieneAcompanantes() {
		return lb_tieneAcompanantes;
	}

	public void setLb_tieneAcompanantes(boolean lb_tieneAcompanantes) {
		this.lb_tieneAcompanantes = lb_tieneAcompanantes;
	}

	public int getCantidadProducto() {
		return cantidadProducto;
	}

	public void setCantidadProducto(int cantidadProducto) {
		this.cantidadProducto = cantidadProducto;
	}

	public long getSeleccionAcompanante() {
		return seleccionAcompanante;
	}

	public void setSeleccionAcompanante(long seleccionAcompanante) {
		this.seleccionAcompanante = seleccionAcompanante;
	}

	public String getLinkCarrito() {
		return linkCarrito;
	}

	public void setLinkCarrito(String linkCarrito) {
		this.linkCarrito = linkCarrito;
	}



	public long getAl_idAcompananteProducto() {
		return al_idAcompananteProducto;
	}

	public void setAl_idAcompananteProducto(long al_idAcompananteProducto) {
		this.al_idAcompananteProducto = al_idAcompananteProducto;
	}

	public String getImagenCarrito() {
		return imagenCarrito;
	}

	public void setImagenCarrito(String imagenCarrito) {
		this.imagenCarrito = imagenCarrito;
	}

	public int getInd() {
		return ind;
	}

	public void setInd(int ind) {
		this.ind = ind;
	}

	public String getLinkCarro() {
		return linkCarro;
	}

	public void setLinkCarro(String linkCarro) {
		this.linkCarro = linkCarro;
	}

	public String getLinkMenu() {
		return linkMenu;
	}

	public void setLinkMenu(String linkMenu) {
		this.linkMenu = linkMenu;
	}

}