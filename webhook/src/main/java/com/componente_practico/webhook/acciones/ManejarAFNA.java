package com.componente_practico.webhook.acciones;

import static com.componente_practico.util.ApiAiUtil.obtenerValorParametro;
import static com.componente_practico.util.ApiAiUtil.obtenerValorParametroV2;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.collections4.ListUtils;

import com.chilkatsoft.CkCrypt2;
import com.componente_practico.cine.ejb.servicio.ChilkatServicio;
import com.componente_practico.comida.pedido.ejb.servicio.DetalleProductoServicio;
import com.componente_practico.comida.pedido.ejb.servicio.ProveedorServicio;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.general.ejb.servicio.UbicacionUsuarioServicio;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.comida.pedido.ejb.dao.AcompananteProductoDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.dao.SubmenuProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.AcompananteProducto;
import com.holalola.comida.pedido.ejb.modelo.DetalleProducto;
import com.holalola.comida.pedido.ejb.modelo.Producto;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.comida.pedido.ejb.modelo.SubmenuProveedor;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.util.CodigoQR;
import com.holalola.util.FechaUtil;
import com.holalola.util.MailUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.modelo.Servicio;
import com.holalola.webhook.ejb.modelo.Servicios;
import com.holalola.webhook.facebook.ConstantesFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.response.message.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.ButtonRichMessageV2;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;

import ai.api.model.AIOutputContext;
import ai.api.model.Result;

@Stateless
public class ManejarAFNA {

	ChilkatServicio chilkatServicio = new ChilkatServicio();
	private static final String CONTEXTO_AFNA = "afna";
	private static final String CONTEXTO_COMPLETA_AFNA = "completa_afna";

	private static final String PARAM_ID_PROVEEDOR = "idProveedor.original";
	private static final String PAYLOAD_HORARIO_ATENCION_PROVEEDOR = "MOSTRAR_HORARIO_ATENCION ";
	private static final String PARAM_ID_DETALLE_PRODUCTO = "idDetalleProducto.original";
	private static final String PARAM_NIVEL_SELECCIONADO = "nivelSeleccionado.original";
	private static final String PARAM_ORDEN = "orden.original";
	private static final String PARAM_ID_ACOMPANANTE_PRODUCTO = "idAcompananteProducto.original";

	private static final String LOGO_DINERO = "https://www.broomfield.org/ImageRepository/Document?documentID=16775";

	@EJB
	private UsuarioDao usuarioDao;
	
	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	ProveedorDao proveedorDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	ProveedorServicio proveedorServicio;

	@EJB
	DetalleProductoServicio detalleProductoServicio;

	@EJB
	SubmenuProveedorDao submenuProveedorDao;

	@EJB
	AcompananteProductoDao acompananteProductoDao;
	
	final private String pathImagenesQR = System.getProperty("lola.base.url") + "images/AFNA/Boletos/";
	final private String pathImagenesQRReal = System.getProperty("lola.images.path")+"AFNA/Boletos/";


	public ResponseMessageApiAi mostrarServicioAFNA(Result resultAi, Usuario usuario) {
		
		if(usuario.getEmail() == null || usuario.getEmail().trim().length() <= 0)
		{
			String speech = String.format("%s, necesito tu información completa. Por favor haz click en 'Completar' :)",
					usuario.getNombreFacebook());
			UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
			List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Completar",
					UrlUtil.armarUrlCompletarDireccion(ubicacionUsuario.getId(), ubicacionUsuario.getToken(), "AFNA", 0, 0), true));
			Data data = new Data(new ButtonRichMessage(speech, buttons));

			return new ResponseMessageApiAi(speech, speech, data, null, "validadorUsuario");
		}
		
		String mensaje = "%s, aquí puedes encontrar lo que necesitas ;)";
		return armarGaleriaPorServicio(mensaje, Servicios.AFNA.getServicio(), usuario, "Ver");
	}

	public ResponseMessageApiAi mostrarMenuPrincipal(Result resultAi, Usuario usuario) {
		long proveedorId = obtenerPametroIdProveedor(resultAi);
		return armarMenuPrincipal(usuario, proveedorId);
	}

	public ResponseMessageApiAi armarMenuPrincipal(Usuario usuario, long proveedorId) {
		enviarMensajeInicio(usuario, proveedorId);
		Proveedor proveedor = proveedorServicio.obtenerPorId(proveedorId);
		return armarMenu(usuario.getId(), proveedor, proveedorId, proveedorServicio.obtenerPorId(proveedorId).getMenuPrincipal(), -1);
	}

	public ResponseMessageApiAi completarAcompananteProducto(Result resultAi, Usuario usuario) {
		long detalleProductoId = Long
				.valueOf(obtenerValorParametro(resultAi, PARAM_ID_DETALLE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
		int orden = Integer.parseInt(obtenerValorParametro(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));
		int nivelOpcionSeleccionado = Integer
				.parseInt(obtenerValorParametro(resultAi, PARAM_NIVEL_SELECCIONADO, CONTEXTO_COMPLETA_AFNA));

		if (nivelOpcionSeleccionado == -1) {
			nivelOpcionSeleccionado = 0;
		}

		DetalleProducto detalleProducto = detalleProductoServicio.obtenerPorId(detalleProductoId);
		List<AcompananteProducto> acompanantesProducto = acompananteProductoDao
				.obtenerAcompanantesProducto(detalleProducto.getProducto(), orden);

		return completarAcompananteProducto(acompanantesProducto, orden, nivelOpcionSeleccionado, detalleProductoId);
	}

	private ResponseMessageApiAi completarAcompananteProducto(List<AcompananteProducto> acompanantesProducto, int orden,
			int nivelOpcionSeleccionado, long detalleProductoId) {

		String speech = acompanantesProducto.get(0).getTipoAcompanante().getMensaje();
		List<QuickReplyGeneral> totalOpciones = acompanantesProducto.stream().map(a -> {
			return new TextQuickReply(a.getAcompananteProveedor().getNombre(),
					String.format("ENVIAR_NUMENTRA_AFNA %s %s", a.getId(), (orden + 1)),
					(a.getPrecio().compareTo(BigDecimal.ZERO) == 1) ? LOGO_DINERO : null);
		}).collect(Collectors.toList());

		List<List<QuickReplyGeneral>> opciones = ListUtils.partition(totalOpciones, 9);
		List<QuickReplyGeneral> opcionesNivel = new ArrayList<>(opciones.get(nivelOpcionSeleccionado));

		int niveles = opciones.size();
		if (niveles > 1) {
			int siguienteNivel = niveles == (nivelOpcionSeleccionado + 1) ? 0 : nivelOpcionSeleccionado + 1;
			opcionesNivel.add(new TextQuickReply("Ver Más",
					String.format("ACOMPANANTE_PRODUCTO_AFNA %s %s %s", detalleProductoId, orden, siguienteNivel)));
		}

		return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "completarAcompananteProducto", opcionesNivel);
	}

	private boolean generacodigoQR(Usuario ao_usuario, String as_texto) {

		Random r = new Random();
		int randomInt = r.nextInt(9999) + 1;
		
		
		
		 CkCrypt2 crypt = new CkCrypt2();

		    boolean success = crypt.UnlockComponent(ChilkatServicio.getIdAcceso());
		    if (success != true) {
		        return false;
		    }
		    
		    String  password = CodigoQR.getValidadorQR();

		    crypt.put_CryptAlgorithm("pbes1");
		    
		    crypt.put_PbesPassword(password);

		    crypt.put_PbesAlgorithm("rc2");
		    crypt.put_KeyLength(64);

		    crypt.SetEncodedSalt("0102030405060708","hex");

		    crypt.put_IterationCount(1024);

		    crypt.put_HashAlgorithm("sha1");

		    crypt.put_EncodingMode("hex");

		    as_texto = ChilkatServicio.Encriptar(as_texto);
		    
		    as_texto = crypt.encryptStringENC(as_texto);
		    
		    as_texto = as_texto.trim() + String.valueOf(randomInt).trim() + String.valueOf(randomInt).trim().length();
		
		    CodigoQR codigo = new CodigoQR();
		    codigo.GeneraCodigoQR(as_texto.trim(), ao_usuario.getId().toString().trim());
		

		return true;
	}
	
	public ResponseMessageApiAi enviarMensaje1(Result resultAi, Usuario usuario) {
		long idAcompananteProducto = Long
				.parseLong(obtenerValorParametro(resultAi, PARAM_ID_ACOMPANANTE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
		int orden = Integer.parseInt(obtenerValorParametro(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));

		final AcompananteProducto acompanante = acompananteProductoDao.obtenerPorId(idAcompananteProducto);

		List<AcompananteProducto> acompanantesProducto = acompananteProductoDao
				.obtenerAcompanantesProducto(acompanante.getProducto(), orden);
		if (acompanantesProducto.isEmpty()) {

			if(usuario.getEmail() != null && usuario.getEmail().trim().length() > 0)
			{
				String ls_emails = usuario.getEmail();
				try
				{
					final TextMessage mensaje = new TextMessage(
							String.format("%s, se está enviando un email con el código de acceso para ir a ver el partido " + acompanante.getProducto().getNombre()+" en la localidad "+acompanante.getAcompananteProveedor().getNombre(), usuario.getNombreFacebook()));

					ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje),
							propiedadesLola.facebookToken);
					
					Random r = new Random();
					int sector = r.nextInt(7) + 1;
					
					r = new Random();
					int fila = r.nextInt(20) + 1;
					
					r = new Random();
					int Asiento = r.nextInt(30) + 1;
					
					
					String ls_codifiar = CodigoQR.getValidadorinicioQR() + " Comprador: " + 
							(usuario.toString() != null ? usuario.toString().trim() : "S/AP") + ";" +
	             			" Identificacion: " + 
				            (usuario.getNumeroIdentificacion() != null ? usuario.getNumeroIdentificacion().trim() : "S/ID") + ";" +
			                " Partido: " + 
			                acompanante.getProducto().getNombre().trim() + ";" +
			                " Localidad: " + 
			                acompanante.getAcompananteProveedor().getNombre().trim()+ ";" +
			                " Sector: " + 
			                sector + ";" +
			                " Fila: " + 
			                fila+ ";" +
			                " Asiento: " + 
			                Asiento
				             ;
					String nombreArchivo = "";
					boolean lb_genoeroCodigo = false;
					try
					{
						lb_genoeroCodigo = generacodigoQR(usuario, ls_codifiar);
					
						if(lb_genoeroCodigo)
						{
							final Date hoy = new Date();
							final Date manana =  new Date(hoy.getTime() + (1000 * 60 * 60 * 24));
							final Date pasadomanana = new Date(manana.getTime() + (1000 * 60 * 60 * 24));
							final Date pasadopasadomanana = new Date(pasadomanana.getTime() + (1000 * 60 * 60 * 24));
							
							SimpleDateFormat dt1 = new SimpleDateFormat("dd - MMMM - yyyy  HH:mm");
							SimpleDateFormat dt2 = new SimpleDateFormat("ddMMyyyyHHmmss");
							
							nombreArchivo = usuario.getId().toString().trim()+dt2.format(pasadopasadomanana).trim();
						
							lb_genoeroCodigo = GenerarBoletoAFNA.generarBoleto( acompanante.getProducto().getId(), usuario.getId(), CodigoQR.DiaDeSemana(pasadopasadomanana).trim() + "  " + dt1.format(pasadopasadomanana), 
							    acompanante.getProducto().getObservacion().trim(), 
								(usuario.getNumeroIdentificacion() != null ? usuario.getNumeroIdentificacion().trim() : "S/ID") + " - " + (usuario.toString() != null ? usuario.toString().trim() : "S/AP") , 
				             	acompanante.getAcompananteProveedor().getPayload().trim(), 
				             	String.format("%02d", sector), String.format("%02d", fila), 
				             	String.format("%02d", Asiento),
				             	(acompanante.getProducto().getNombre().trim().length() > 25 ? acompanante.getProducto().getNombre().trim().substring(0, 25) : acompanante.getProducto().getNombre().trim()),
				             	nombreArchivo.trim());
						}					
					}
					catch(Exception err)
					{
						err.printStackTrace();
						lb_genoeroCodigo = false;
					}
					
					if(!lb_genoeroCodigo)
					{
						return new ResponseMessageApiAi(":( Lo siento no pude generar tu entrada, por favor intentalo nuevamente.",
								":( Lo siento no pude generar tu entrada, por favor intentalo nuevamente.", null, null, "afna");
					}
					
					String ls_mensaje = MailUtil.enviarMail(ls_emails,"AFNA - Hola Lola", "Se solicitó ir a ver el partido " + acompanante.getProducto().getNombre()+" para la localidad "+acompanante.getAcompananteProveedor().getNombre()+
																		" Sector: " + 
														                sector + "," +
														                " Fila: " + 
														                fila+ "," +
														                " Asiento: " + 
														                Asiento +
							                                          ", adjuntamos la entrada que disfrutes del partido, suerte...", true,
							                                          pathImagenesQRReal+nombreArchivo.trim()+".png"
																	   );
		
					if (ls_mensaje != null && ls_mensaje.trim().length() > 0) {
						//System.out.println("N-----------------------------------------------------------Email Enviado ---- "+patSonido.replace("sonido/alarma.mp3", "imgBD.jpg"));
						
						final TextMessage mensaje1 = new TextMessage(
								String.format("%s, adjunto tu entrada, disfruta del partido (Y) mucha suerte: ", usuario.getNombreFacebook()));

						ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje1),
								propiedadesLola.facebookToken);
						Thread.sleep(500l);
						ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.IMAGE, new MediaPayload(pathImagenesQR.trim()+nombreArchivo.trim()+".png")))),
								propiedadesLola.facebookToken);
						
				  
						return new ResponseMessageApiAi("Listo "+usuario.getNombreFacebook()+" suerte en el partido.",
								"Listo "+usuario.getNombreFacebook()+" suerte en el partido.", null, null, "afna");
					} else {
						System.out.println("No se pudo enviar el mensaje ");
						return new ResponseMessageApiAi(":( Lo siento no pude enviar la inforamción a tu email.",
								":( Lo siento no pude enviar la inforamción a tu email.", null, null, "afna");
					}
				}
				catch(Exception err)
				{
					System.out.println("No se pudo enviar el mensaje "+err);
					return new ResponseMessageApiAi(":( Lo siento no pude enviar la inforamción a tu email.",
							":( Lo siento no pude enviar la inforamción a tu email.", null, null, "afna");
				}
			}
			else
			{
				return new ResponseMessageApiAi("No posees un email relacionado para poderte enviar la inforamción.",
						"No posees un email relacionado para poderte enviar la inforamción.", null, null, "afna");
			}
		}

		long detalleProductoId = Long
				.valueOf(obtenerValorParametro(resultAi, PARAM_ID_DETALLE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));

		return completarAcompananteProducto(acompanantesProducto, orden, 0, detalleProductoId);
	}
	
	public ResponseMessageApiAi mostrarNumeroAsientosAFNA(Result resultAi, Usuario usuario) {
		
		long idAcompananteProducto = Long
				.parseLong(obtenerValorParametro(resultAi, PARAM_ID_ACOMPANANTE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
		int orden = Integer.parseInt(obtenerValorParametro(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));
		
		
			String speech = String.format("%s, cuantas entradas necesitas?",
					usuario.getNombreFacebook());
			
			List<QuickReplyGeneral> listaBoletos = new ArrayList<QuickReplyGeneral>();
			
		
			for (int i = 1; i <= 10; i++) { 
				listaBoletos.add(new TextQuickReply(String.valueOf(i), String.format("ENVIAR_MENSAJE_AFNA %s %s  %s", idAcompananteProducto, orden, i)));
			}
			
			return ApiAiUtil.armarRespuestaTextMessageConQuickReply(speech, "completarAcompananteProducto",
					listaBoletos);
	}
	
	
	public ResponseMessageApiAi enviarMensaje(Result resultAi, Usuario usuario) {
		long idAcompananteProducto = Long
				.parseLong(obtenerValorParametro(resultAi, PARAM_ID_ACOMPANANTE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
		int orden = Integer.parseInt(obtenerValorParametro(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));
		
		int asientos = Integer.parseInt(obtenerValorParametro(resultAi, "totalAsientos", CONTEXTO_COMPLETA_AFNA));

		final AcompananteProducto acompanante = acompananteProductoDao.obtenerPorId(idAcompananteProducto);

		List<AcompananteProducto> acompanantesProducto = acompananteProductoDao
				.obtenerAcompanantesProducto(acompanante.getProducto(), orden);
		
		System.out.println("--------------va a URL------------------");
				
		if (acompanantesProducto.isEmpty()) {
	 
			System.out.println("--------------URL------------------"+UrlUtil.armarUrlGeneraEntradas(acompanante.getProducto().getId(), 
																				acompanante.getAcompananteProveedor().getNombre(), 
																				asientos, 
																				usuario.getId(), 
																				false));
			
			final List<ButtonGeneral> buttons = Arrays.asList(
					new WebUrlButton("Entradas", UrlUtil.armarUrlGeneraEntradas(acompanante.getProducto().getId(), 
																				acompanante.getAcompananteProveedor().getNombre(), 
																				asientos, 
																				usuario.getId(), 
																				false), 
												1));

			return ApiAiUtil.armarRespuestaConButton("Listo "+usuario.getNombreFacebook()+", vamos por tus entradas.... ", buttons, "afna");
		}
		else
		{
			System.out.println("--------------No va a URL------------------");
			
			return new ResponseMessageApiAi(usuario.getNombreFacebook()+", No puedo procesar el evento, disculpa :( ",
					usuario.getNombreFacebook()+", No puedo procesar el evento, disculpa :( ", null, null, "afna");
		}
	}

	private void enviarMensajeInicio(Usuario usuario, long proveedorId) {

		final TextMessage mensaje = new TextMessage(
				String.format("%s, que partido nos vamos a ver? ;)", usuario.getNombreFacebook()));

		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), mensaje),
				propiedadesLola.facebookToken);
	}

	public ResponseMessageApiAi armarMenuPrincipal(Proveedor proveedor, Usuario usuario, long proveedorId, int nivel) {

		if (nivel < 0)
			enviarMensajeInicio(usuario, proveedorId);
		return armarMenu(usuario.getId(),proveedor, proveedorId, proveedorServicio.obtenerPorId(proveedorId).getMenuPrincipal(), nivel);
	}

	public ResponseMessageApiAi armarMenu(long idUsuario,Proveedor proveedor, long idProveedor, String menuPorMostrar, int nivel) {
		String source = String.format("armarMenu %s %s", idProveedor, menuPorMostrar);
		try {

			String payloadMenu = menuPorMostrar.replaceAll(" ", "_");
			// log.info("\n\n\n\n\n=============== MENU {}", payloadMenu);
			Map<Producto, List<DetalleProducto>> productos = detalleProductoServicio
					.obtenerActivosProveedorDiaActual(idUsuario, idProveedor, payloadMenu);
			SubmenuProveedor submenuProveedor = submenuProveedorDao.obtenerPorId(payloadMenu);

			List<Element> elements = new ArrayList<>();

			for (Producto producto : productos.keySet()) {

				List<ButtonGeneral> buttons = productos.get(producto).stream()
						.limit(ConstantesFacebook.BUTTONS_IN_LIST).map(detalle -> {
							return armarPostbackButtonMenu(detalle);
						}).collect(Collectors.toList());

				elements.add(
						new Element(producto.getNombre(), producto.getUrlImagen(), producto.getDescripcion(), buttons));

			}

			List<List<Element>> elementos = ListUtils.partition(elements, 9);

			int niveles = elementos.size();

			List<Element> elementosVer = new ArrayList<>();

			if (niveles > 0)
				elementosVer = new ArrayList<>(elementos.get(nivel < 0 ? 0 : nivel));

			if (niveles > 1) {

				int siguienteNivel = niveles == (nivel + 1) ? 0 : nivel + 1;

				elementosVer = new ArrayList<>(elementos.get(siguienteNivel));

				final List<ButtonGeneral> buttons = new ArrayList<>();
				buttons.add(new PostbackButton(String.format("Más %s", submenuProveedor.getDescripcion()),
						String.format("%s %s %s %s", CONTEXTO_AFNA, menuPorMostrar, idProveedor, siguienteNivel)));

				buttons.add(new PostbackButton(String.format("%s", proveedor.getNombre()),
						String.format("%s %s %s %s", CONTEXTO_AFNA, proveedor.getPayloadValidarUbicacion(), idProveedor, -1)));

				if (submenuProveedor.isEsPrincipal())
					buttons.add(new PostbackButton("Otros AFNA", "AFNA"));

				elementosVer.add(new Element("Ver Más", proveedor.getLogo(), "", buttons));
			} else {
				final List<ButtonGeneral> buttons = new ArrayList<>();
				buttons.add(new PostbackButton(String.format("%s", proveedor.getNombre()),
						String.format("%s %s %s %s", CONTEXTO_AFNA, proveedor.getPayloadValidarUbicacion(), idProveedor, -1)));
				buttons.add(new PostbackButton("Otros AFNA", "AFNA"));
				elementosVer.add(new Element("Ver Más", proveedor.getLogo(), "", buttons));
			}

			final String speech = "Mas opciones para ti...";

			return ApiAiUtil.armarRespuestaGenericTemplate(elementosVer, speech, source);

		} catch (Exception e) {
			// log.error("No se pudo obtener menu de comida", e);
			return ApiAiUtil.armarRespuestaTextMessage("Ooops algo inesperado. Discúlpame :(", source);
		}
	}

	private PostbackButton armarPostbackButtonMenu(DetalleProducto detalle) {

		String label;
		String payload;

		if (detalle.isGeneraSubmenu()) {
			label = detalle.getNombreCategoria();
			payload = String.format("%s %s -1", detalle.getPayload(), detalle.getProveedorId());

		} else if (detalle.isSolicitarAcompanante()) {
			label = detalle.getPrecioCategoria();
			payload = String.format("ACOMPANANTE_PRODUCTO_AFNA %s 1 -1", detalle.getId());

		} else {
			label = detalle.getPrecioCategoria();
			payload = String.format("%s %s -1", detalle.getPayload(), detalle.getId());
		}

		return new PostbackButton(label, payload);
	}

	private long obtenerPametroIdProveedor(Result resultAi) {

		try {
			// System.out.println(resultAi);

			String valorParametro = null;

			try {

				AIOutputContext ctx;
				if ((ctx = resultAi.getContext(CONTEXTO_AFNA)) != null) {
					valorParametro = ctx.getParameters().get(PARAM_ID_PROVEEDOR).getAsString();
				}

				if (valorParametro == null) {
					valorParametro = resultAi.getParameters().get(PARAM_ID_PROVEEDOR).getAsString();
				}

			} catch (Exception e) {
				System.out.println(e);
				// log.error("No se pudo obtener el parametro {} ", PARAM_ID_PROVEEDOR);
				// //PARAM_ID_PEDIDO
			}
		} catch (Exception err) {
			System.out.println(err);
		}

		return Long.parseLong(obtenerValorParametro(resultAi, PARAM_ID_PROVEEDOR, CONTEXTO_AFNA));
	}

	private ResponseMessageApiAi armarGaleriaPorServicio(String mensaje, Servicio servicio, Usuario usuario,
			String as_textoVerMenu) {
		String speech = String.format(mensaje, usuario.getNombreFacebook());
		ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
				propiedadesLola.facebookToken);
		return armarGaleriaServicios(usuario, proveedorDao.obtenerActivosPorServicio(servicio), as_textoVerMenu);
	}

	private ResponseMessageApiAi armarGaleriaServicios(Usuario usuario, List<Proveedor> proveedores,
			String as_textoVerMenu) {

		final Date horaActual = new Date();
		
		final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0
				? as_textoVerMenu.trim()
				: "Ver";

		List<Element> elementosRestaurantes = proveedores.stream().map(p -> {
			ButtonGeneral button;
			String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0
					? p.getDescripcion().trim()
					: "";
			

			button = new PostbackButton(al_textoVerMenu,
					String.format("%s %s", p.getPayloadValidarUbicacion(), p.getId()));
			
			return new Element(p.getNombre(), p.getLogo(), ls_decripcion, Arrays.asList(button));
			
		}).collect(Collectors.toList());

		return ApiAiUtil.armarRespuestaGenericTemplate(elementosRestaurantes, "Galeria de restaurantes",
				"mostrarRestaurantes");
	}

	private final int estaDentroDeHorario(Proveedor proveedor, Date horaActual, Usuario usuario, String ls_ciudad,
			String ls_local) {

		if (!proveedor.isValidarUbicacion()) {
			Date horaMinima = FechaUtil.armarHoraHoy(proveedor.getHoraMinimaPedidos(),
					proveedor.getMinutoMinimoPedidos());
			Date horaMaxima = FechaUtil.armarHoraHoy(proveedor.getHoraMaximaPedidos(),
					proveedor.getMinutoMaximoPedidos());

			return horaMinima.before(horaActual) && horaActual.before(horaMaxima) ? 1 : 0;
		} else {
			return proveedorDao.validaHorario(proveedor.getId(), usuario, ls_ciudad, ls_local);
		}
	}
//**********************************DIALOGFLOW V2******************************************************************
public ResponseMessageApiAiV2 mostrarServicioAFNAV2(QueryResult resultAi, Usuario usuario) {
		
		if(usuario.getEmail() == null || usuario.getEmail().trim().length() <= 0)
		{
			String speech = String.format("%s, necesito tu información completa. Por favor haz click en 'Completar' :)",
					usuario.getNombreFacebook());
			UbicacionUsuario ubicacionUsuario = ubicacionUsuarioServicio.obtenerUltimaUsuarioConToken(usuario);
			List<ButtonGeneral> buttons = Arrays.asList(new WebUrlButton("Completar",
					UrlUtil.armarUrlCompletarDireccion(ubicacionUsuario.getId(), ubicacionUsuario.getToken(), "AFNA", 0, 0), true));
			PayloadResponse data = new PayloadResponse(new ButtonRichMessageV2(speech, buttons));

			return new ResponseMessageApiAiV2(speech, "validadorUsuario", data, null);
		}
		
		String mensaje = "%s, aquí puedes encontrar lo que necesitas ;)";
		return armarGaleriaPorServicioV2(mensaje, Servicios.AFNA.getServicio(), usuario, "Ver");
	}
private ResponseMessageApiAiV2 armarGaleriaPorServicioV2(String mensaje, Servicio servicio, Usuario usuario,
		String as_textoVerMenu) {
	String speech = String.format(mensaje, usuario.getNombreFacebook());
	ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
			propiedadesLola.facebookToken);
	return armarGaleriaServiciosV2(usuario, proveedorDao.obtenerActivosPorServicio(servicio), as_textoVerMenu);
}

private ResponseMessageApiAiV2 armarGaleriaServiciosV2(Usuario usuario, List<Proveedor> proveedores,
		String as_textoVerMenu) {

	final Date horaActual = new Date();
	
	final String al_textoVerMenu = as_textoVerMenu != null && as_textoVerMenu.trim().length() > 0
			? as_textoVerMenu.trim()
			: "Ver";

	List<Element> elementosRestaurantes = proveedores.stream().map(p -> {
		ButtonGeneral button;
		String ls_decripcion = p.getDescripcion() != null && p.getDescripcion().length() > 0
				? p.getDescripcion().trim()
				: "";
		

		button = new PostbackButton(al_textoVerMenu,
				String.format("%s %s", p.getPayloadValidarUbicacion(), p.getId()));
		
		return new Element(p.getNombre(), p.getLogo(), ls_decripcion, Arrays.asList(button));
		
	}).collect(Collectors.toList());

	return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosRestaurantes, "Galeria de restaurantes",
			"mostrarRestaurantes");
}

public ResponseMessageApiAiV2 mostrarMenuPrincipalV2(QueryResult resultAi, Usuario usuario) {
	long proveedorId = obtenerPametroIdProveedorV2(resultAi);
	return armarMenuPrincipalV2(usuario, proveedorId);
}

private long obtenerPametroIdProveedorV2(QueryResult resultAi) {

	try {
		// System.out.println(resultAi);

		String valorParametro = null;

		try {

			OutputContexts ctx;
			if ((ctx = resultAi.getContext(CONTEXTO_AFNA)) != null) {
				valorParametro = ctx.getParameters().get(PARAM_ID_PROVEEDOR).toString();
			}

			if (valorParametro == null) {
				valorParametro = resultAi.getParameters().get(PARAM_ID_PROVEEDOR).toString();
			}

		} catch (Exception e) {
			System.out.println(e);
			// log.error("No se pudo obtener el parametro {} ", PARAM_ID_PROVEEDOR);
			// //PARAM_ID_PEDIDO
		}
	} catch (Exception err) {
		System.out.println(err);
	}

	return Long.parseLong(obtenerValorParametroV2(resultAi, PARAM_ID_PROVEEDOR, CONTEXTO_AFNA));
}

public ResponseMessageApiAiV2 armarMenuPrincipalV2(Usuario usuario, long proveedorId) {
	enviarMensajeInicio(usuario, proveedorId);
	Proveedor proveedor = proveedorServicio.obtenerPorId(proveedorId);
	return armarMenuV2(usuario.getId(), proveedor, proveedorId, proveedorServicio.obtenerPorId(proveedorId).getMenuPrincipal(), -1);
}

public ResponseMessageApiAiV2 armarMenuV2(long idUsuario,Proveedor proveedor, long idProveedor, String menuPorMostrar, int nivel) {
	String source = String.format("armarMenu %s %s", idProveedor, menuPorMostrar);
	try {

		String payloadMenu = menuPorMostrar.replaceAll(" ", "_");
		// log.info("\n\n\n\n\n=============== MENU {}", payloadMenu);
		Map<Producto, List<DetalleProducto>> productos = detalleProductoServicio
				.obtenerActivosProveedorDiaActual(idUsuario, idProveedor, payloadMenu);
		SubmenuProveedor submenuProveedor = submenuProveedorDao.obtenerPorId(payloadMenu);

		List<Element> elements = new ArrayList<>();

		for (Producto producto : productos.keySet()) {

			List<ButtonGeneral> buttons = productos.get(producto).stream()
					.limit(ConstantesFacebook.BUTTONS_IN_LIST).map(detalle -> {
						return armarPostbackButtonMenu(detalle);
					}).collect(Collectors.toList());

			elements.add(
					new Element(producto.getNombre(), producto.getUrlImagen(), producto.getDescripcion(), buttons));

		}

		List<List<Element>> elementos = ListUtils.partition(elements, 9);

		int niveles = elementos.size();

		List<Element> elementosVer = new ArrayList<>();

		if (niveles > 0)
			elementosVer = new ArrayList<>(elementos.get(nivel < 0 ? 0 : nivel));

		if (niveles > 1) {

			int siguienteNivel = niveles == (nivel + 1) ? 0 : nivel + 1;

			elementosVer = new ArrayList<>(elementos.get(siguienteNivel));

			final List<ButtonGeneral> buttons = new ArrayList<>();
			buttons.add(new PostbackButton(String.format("Más %s", submenuProveedor.getDescripcion()),
					String.format("%s %s %s %s", CONTEXTO_AFNA, menuPorMostrar, idProveedor, siguienteNivel)));

			buttons.add(new PostbackButton(String.format("%s", proveedor.getNombre()),
					String.format("%s %s %s %s", CONTEXTO_AFNA, proveedor.getPayloadValidarUbicacion(), idProveedor, -1)));

			if (submenuProveedor.isEsPrincipal())
				buttons.add(new PostbackButton("Otros AFNA", "AFNA"));

			elementosVer.add(new Element("Ver Más", proveedor.getLogo(), "", buttons));
		} else {
			final List<ButtonGeneral> buttons = new ArrayList<>();
			buttons.add(new PostbackButton(String.format("%s", proveedor.getNombre()),
					String.format("%s %s %s %s", CONTEXTO_AFNA, proveedor.getPayloadValidarUbicacion(), idProveedor, -1)));
			buttons.add(new PostbackButton("Otros AFNA", "AFNA"));
			elementosVer.add(new Element("Ver Más", proveedor.getLogo(), "", buttons));
		}

		final String speech = "Mas opciones para ti...";

		return ApiAiUtil.armarRespuestaGenericTemplateV2(elementosVer, speech, source);

	} catch (Exception e) {
		// log.error("No se pudo obtener menu de comida", e);
		return ApiAiUtil.armarRespuestaTextMessageV2("Ooops algo inesperado. Discúlpame :(", source);
	}
}


public ResponseMessageApiAiV2 completarAcompananteProductoV2(QueryResult resultAi, Usuario usuario) {
	long detalleProductoId = Long
			.valueOf(obtenerValorParametroV2(resultAi, PARAM_ID_DETALLE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
	int orden = Integer.parseInt(obtenerValorParametroV2(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));
	int nivelOpcionSeleccionado = Integer
			.parseInt(obtenerValorParametroV2(resultAi, PARAM_NIVEL_SELECCIONADO, CONTEXTO_COMPLETA_AFNA));

	if (nivelOpcionSeleccionado == -1) {
		nivelOpcionSeleccionado = 0;
	}

	DetalleProducto detalleProducto = detalleProductoServicio.obtenerPorId(detalleProductoId);
	List<AcompananteProducto> acompanantesProducto = acompananteProductoDao
			.obtenerAcompanantesProducto(detalleProducto.getProducto(), orden);

	return completarAcompananteProductoV2(acompanantesProducto, orden, nivelOpcionSeleccionado, detalleProductoId);
}

private ResponseMessageApiAiV2 completarAcompananteProductoV2(List<AcompananteProducto> acompanantesProducto, int orden,
		int nivelOpcionSeleccionado, long detalleProductoId) {

	String speech = acompanantesProducto.get(0).getTipoAcompanante().getMensaje();
	List<QuickReplyGeneral> totalOpciones = acompanantesProducto.stream().map(a -> {
		return new TextQuickReply(a.getAcompananteProveedor().getNombre(),
				String.format("ENVIAR_NUMENTRA_AFNA %s %s", a.getId(), (orden + 1)),
				(a.getPrecio().compareTo(BigDecimal.ZERO) == 1) ? LOGO_DINERO : null);
	}).collect(Collectors.toList());

	List<List<QuickReplyGeneral>> opciones = ListUtils.partition(totalOpciones, 9);
	List<QuickReplyGeneral> opcionesNivel = new ArrayList<>(opciones.get(nivelOpcionSeleccionado));

	int niveles = opciones.size();
	if (niveles > 1) {
		int siguienteNivel = niveles == (nivelOpcionSeleccionado + 1) ? 0 : nivelOpcionSeleccionado + 1;
		opcionesNivel.add(new TextQuickReply("Ver Más",
				String.format("ACOMPANANTE_PRODUCTO_AFNA %s %s %s", detalleProductoId, orden, siguienteNivel)));
	}

	return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "completarAcompananteProducto", opcionesNivel);
}

public ResponseMessageApiAiV2 mostrarNumeroAsientosAFNAV2(QueryResult resultAi, Usuario usuario) {
	
	long idAcompananteProducto = Long
			.parseLong(obtenerValorParametroV2(resultAi, PARAM_ID_ACOMPANANTE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
	int orden = Integer.parseInt(obtenerValorParametroV2(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));
	
	
		String speech = String.format("%s, cuantas entradas necesitas?",
				usuario.getNombreFacebook());
		
		List<QuickReplyGeneral> listaBoletos = new ArrayList<QuickReplyGeneral>();
		
	
		for (int i = 1; i <= 10; i++) { 
			listaBoletos.add(new TextQuickReply(String.valueOf(i), String.format("ENVIAR_MENSAJE_AFNA %s %s  %s", idAcompananteProducto, orden, i)));
		}
		
		return ApiAiUtil.armarRespuestaTextMessageConQuickReplyV2(speech, "completarAcompananteProducto",
				listaBoletos);
}

public ResponseMessageApiAiV2 enviarMensajeV2(QueryResult resultAi, Usuario usuario) {
	long idAcompananteProducto = Long
			.parseLong(obtenerValorParametroV2(resultAi, PARAM_ID_ACOMPANANTE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));
	int orden = Integer.parseInt(obtenerValorParametroV2(resultAi, PARAM_ORDEN, CONTEXTO_COMPLETA_AFNA));
	
	int asientos = Integer.parseInt(obtenerValorParametroV2(resultAi, "totalAsientos.original", CONTEXTO_COMPLETA_AFNA));

	final AcompananteProducto acompanante = acompananteProductoDao.obtenerPorId(idAcompananteProducto);

	List<AcompananteProducto> acompanantesProducto = acompananteProductoDao
			.obtenerAcompanantesProducto(acompanante.getProducto(), orden);
	
	System.out.println("--------------va a URL------------------");
			
	if (acompanantesProducto.isEmpty()) {
 
		System.out.println("--------------URL------------------"+UrlUtil.armarUrlGeneraEntradas(acompanante.getProducto().getId(), 
																			acompanante.getAcompananteProveedor().getNombre(), 
																			asientos, 
																			usuario.getId(), 
																			false));
		
		final List<ButtonGeneral> buttons = Arrays.asList(
				new WebUrlButton("Entradas", UrlUtil.armarUrlGeneraEntradas(acompanante.getProducto().getId(), 
																			acompanante.getAcompananteProveedor().getNombre(), 
																			asientos, 
																			usuario.getId(), 
																			false), 
											1));

		return ApiAiUtil.armarRespuestaConButtonV2("Listo "+usuario.getNombreFacebook()+", vamos por tus entradas.... ", buttons, "afna");
	}
	else
	{
		System.out.println("--------------No va a URL------------------");
		
		return new ResponseMessageApiAiV2(usuario.getNombreFacebook()+", No puedo procesar el evento, disculpa :( ",
				"afna", null, null);
	}
}

}
