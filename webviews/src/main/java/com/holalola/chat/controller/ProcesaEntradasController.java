package com.holalola.chat.controller;


import static com.holalola.util.TextoUtil.esVacio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chilkatsoft.CkCrypt2;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.cine.ejb.servicio.ChilkatServicio;
import com.holalola.comida.pedido.ejb.dao.ProductoDao;
import com.holalola.comida.pedido.ejb.modelo.Producto;
import com.holalola.ejb.general.servicio.GenerarBoletoAFNA;
import com.holalola.eventos.ejb.dao.EntradasDao;
import com.holalola.eventos.ejb.modelo.Entradas;
import com.holalola.general.ejb.dao.UsuarioClienteCompraDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioClienteCompra;
import com.holalola.util.CodigoQR;
import com.holalola.util.MailUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.DatosClienteEntradaDao;
import com.holalola.webhook.ejb.modelo.DatosClienteEntrada;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.response.message.Attachment;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;

@ManagedBean
@SuppressWarnings("serial")
@ViewScoped
public class ProcesaEntradasController extends GeneralController {
	
	ChilkatServicio chilkatServicio = new ChilkatServicio();
	private static final DateFormat fmtJavascript = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	
	final private String pathImagenesQR = System.getProperty("lola.base.url") + "images/AFNA/Boletos/";
	final private String pathImagenesQRReal = System.getProperty("lola.images.path")+"AFNA/Boletos/";
	
	public boolean isAb_verImprimir() {
		return ab_verImprimir;
	}

	public void setAb_verImprimir(boolean ab_verImprimir) {
		this.ab_verImprimir = ab_verImprimir;
	}

	private void Mensaje(String ls_mensaje, Boolean bl_warning, Boolean bl_error) {

		if (esVacio(ls_mensaje))
			return;

		FacesMessage success = new FacesMessage(FacesMessage.SEVERITY_INFO, ls_mensaje, "");
		if (bl_warning)
			success = new FacesMessage(FacesMessage.SEVERITY_WARN, ls_mensaje, "");
		else {
			if (bl_error)
				success = new FacesMessage(FacesMessage.SEVERITY_ERROR, ls_mensaje, "");
		}
		FacesContext.getCurrentInstance().addMessage(null, success);
	}
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	@EJB
	private ProductoDao productoDao;
	
	@EJB
	private UsuarioDao usuarioDao;
	
	@EJB
	private DatosClienteEntradaDao datosClienteEntradaDao;
	
	@EJB
	private EntradasDao entradaDao;
	
	@EJB
	private UsuarioClienteCompraDao usuarioClienteCompraDao;
	
	private String ls_textoFinal = "";
	private String urlIMprime;
	private Producto pr_evento;
	private String ls_localidad;
	private int li_totalAsientos;
	private Usuario usuarioProceso;
	private DatosClienteEntrada datosUsuarioOrigen;
	private UsuarioClienteCompra usuarioClienteCompra;
	private List<Entradas> listaEntradas;
	private int li_asiento = 0;
	private boolean ab_verGuardar = true;
	private boolean ab_verSalir = true;
	private boolean ab_verImprimir = false;
	private Long idClienteResponsable;

	@PostConstruct
	public void inicializar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		try {
			
			String ls_idEvento = params.get("iev");
			ls_localidad = params.get("iloc");
			try
			{
				li_totalAsientos = Integer.parseInt(params.get("numasie"));
			}
			catch(Exception err)
			{
				li_totalAsientos = 0;
			}
			String ls_usuario = params.get("token");
			String ls_esWeb = params.get("web");
			String ls_fecha = params.get("tokensg");
			
			try
			{
				ab_verSalir = ls_esWeb != null ? ls_esWeb.equals("0") : true;
			}
			catch(Exception err)
			{
				ab_verSalir = true;
			}
			
			try
			{
				idClienteResponsable = Long.parseLong(params.get("cliResp"));
			}
			catch(Exception err)
			{
				idClienteResponsable = (long) 0;
			}
			
			Date fechaPagina = new Date();
			
			try
			{
				SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
				String lsFecha = ls_fecha;//ChilkatServicio.Desencriptar(ls_fecha);
				
				System.out.println("------------------------Desencriptar------------"+ls_fecha+"------------------ "+lsFecha);
				
				 fechaPagina =  formatter.parse(lsFecha);
				
			      Calendar calendar = Calendar.getInstance();
			      calendar.setTime(new Date()); // Configuramos la fecha que se recibe
                  calendar.add(Calendar.MINUTE, -5);
                  
                 // System.out.println("----------Desencriptar-----------fechaPagina.before(calendar.getTime())----------------- "+calendar.getTime());
				
				if(fechaPagina.before(calendar.getTime()))
				{
					System.out.println("----------Desencriptar-------no4ooo--");
					Mensaje("Sesión finalizada.", false, false);
					ls_textoFinal = "Sesión finalizada.";
					ab_verGuardar = false;
					li_totalAsientos = 0;
					return;
				}
			}
			catch(Exception errf)
			{
				System.out.println("----------Exception---"+errf.getMessage());
			
				Mensaje("Sesión finalizada.", false, false);
				ls_textoFinal = "Sesión finalizada.";
				ab_verGuardar = false;
				li_totalAsientos = 0;
				return;
			}
			
			limpiarClienteCompra();
			try
			{
				pr_evento = productoDao.obtenerPorId(Long.parseLong(ls_idEvento));
			}
			catch(Exception errf)
			{
				System.out.println("----------Desencriptar-------noo3oo--");
				Mensaje("Sesión finalizada.", false, false);
				ls_textoFinal = "Sesión finalizada.";
				ab_verGuardar = false;
				li_totalAsientos = 0;
				return;
			}
			
			try
			{
				usuarioProceso = usuarioDao.obtenerPorId(Long.parseLong(ls_usuario));
			}
			catch(Exception errf)
			{
				// System.out.println("----------Desencriptar-------noo1oo--");
				Mensaje("Sesión finalizada.", false, false);
				ls_textoFinal = "Sesión finalizada.";
				ab_verGuardar = false;
				li_totalAsientos = 0;
				return;
			}
			
			try
			{
				//System.out.println("----------Desencriptar-------noo2oo--"+usuarioProceso.getId()+" ---  "+li_totalAsientos+" --- "+pr_evento.getNombre()+" ---- "+ls_localidad+" ----- "+fechaPagina);
				
				listaEntradas = entradaDao.reservarEntradas(usuarioProceso.getId(), usuarioProceso.getId(), li_totalAsientos, pr_evento.getNombre(), ls_localidad, fechaPagina);
				li_asiento = 0;
			}
			catch(Exception err)
			{
				listaEntradas = new ArrayList<Entradas>();
			}
			
			if(listaEntradas.size() <= 0)
			{
				System.out.println("----------Desencriptar-------noo2oo--");
				ls_textoFinal = "No posees entradas pendientes de impresión";
				ab_verGuardar = false;
				li_totalAsientos = 0;
				return;
			}
			
			//System.out.println("----------listaEntradas---"+listaEntradas.size());
			
			try
			{
				datosUsuarioOrigen = datosClienteEntradaDao.obtenerCliente(usuarioProceso.getNumeroIdentificacion());
			}
			catch(Exception err)
			{
				datosUsuarioOrigen = null;
			}
			
			if(listaEntradas.size() != li_totalAsientos)
			{
				li_totalAsientos = listaEntradas.size();
				Mensaje("Solo hay disponible "+li_totalAsientos+" puesto(s).", false, true);
			}
			
		} catch (Exception e) {
			logger.info("\n--------\n Error - ProcesEntradasController");
			e.printStackTrace();
			Mensaje(e.getMessage(), false, true);
		}
	}
	
	private void limpiarClienteCompra()
	{
		usuarioClienteCompra = new UsuarioClienteCompra("C", "", "", "", "", "", "");
	}
	
	public void validaIdentificacion(){
        if(usuarioClienteCompra != null && usuarioClienteCompra.getIdentificacion() != null && usuarioClienteCompra.getIdentificacion().trim().length() > 0){
        	DatosClienteEntrada datosClienteEntradaTemporal = null;
        	try
			{
        		datosClienteEntradaTemporal = datosClienteEntradaDao.obtenerCliente(usuarioClienteCompra.getIdentificacion());
			}
			catch(Exception err)
			{
				datosClienteEntradaTemporal = null;
			}
        	
        	if(datosClienteEntradaTemporal != null)
        	{
        		listaEntradas.get(li_asiento).setIdClienteAsignaEntrada(datosClienteEntradaTemporal.getIdCliente());
        		listaEntradas.get(li_asiento).setIdUsuarioAsignaEntrada(datosClienteEntradaTemporal.getIdUsuario());
        		
        		if(datosClienteEntradaTemporal != null && datosClienteEntradaTemporal.getIdCliente() != null && datosClienteEntradaTemporal.getIdCliente() > 0)
        			usuarioClienteCompra = usuarioClienteCompraDao.obtenerPorId(datosClienteEntradaTemporal.getIdCliente());
        		else
        		{
	        		usuarioClienteCompra.setNombre(datosClienteEntradaTemporal.getNombre() != null ? datosClienteEntradaTemporal.getNombre() : "");
	        		usuarioClienteCompra.setApellido(datosClienteEntradaTemporal.getApellido() != null ? datosClienteEntradaTemporal.getApellido() : "");
	        		usuarioClienteCompra.setEmail(datosClienteEntradaTemporal.getEmail() != null ? datosClienteEntradaTemporal.getEmail() : "");
	        		usuarioClienteCompra.setDireccion(datosClienteEntradaTemporal.getDireccion() != null ? datosClienteEntradaTemporal.getDireccion() : "");
	        		usuarioClienteCompra.setCelular(datosClienteEntradaTemporal.getCelular() != null ? datosClienteEntradaTemporal.getCelular() : "");
        		}
        	}
        	
        }
   }
	
	@SuppressWarnings("deprecation")
	public void cerrarDialogo() {
		
		System.out.println("--------------------------cerrar dialogo -----------------------------");
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("PF('dlg2').hide();");
	}


	@SuppressWarnings("deprecation")
	public void guardaEntrada() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {
			
			 
			if(usuarioClienteCompra == null )
			{
				Mensaje("Disculpa no reconozco a la persona...", true, false);
				return;
			}
			 
			if(usuarioClienteCompra.getTipoIdentificacion() == null || usuarioClienteCompra.getTipoIdentificacion().trim().length() <= 0)
			{
				Mensaje("Por favor selecciona un tipo de identificación", true, false);
				return;
			}
			 
			if(usuarioClienteCompra.getIdentificacion() == null || usuarioClienteCompra.getIdentificacion().trim().length() <= 0)
			{
				Mensaje("Por favor ingresar un número de identificación", true, false);
				return;
			}
		 
			if(usuarioClienteCompra.getTipoIdentificacion().trim().equals("C") && !Utilidades.validaCedula(usuarioClienteCompra.getIdentificacion()))
			{
				Mensaje("Por favor ingresar un número de Cédula válido", true, false);
				return;
			}
			else
			{
				if(usuarioClienteCompra.getTipoIdentificacion().trim().equals("R") && !Utilidades.validaRuc(usuarioClienteCompra.getIdentificacion()))
				{
					Mensaje("Por favor ingresar un número de RUC válido", true, false);
					return;
				}
			}
			 
			if(usuarioClienteCompra.getNombre() == null || usuarioClienteCompra.getNombre().trim().length() <= 2)
			{
				Mensaje("Por favor ingresar un nombre válido.", true, false);
				return;
			}
			 
			if(usuarioClienteCompra.getEmail() != null && !Utilidades.validaEmail(usuarioClienteCompra.getEmail()))
			{
				Mensaje("Por favor ingresar un E-mail válido.", true, false);
				return;
			}
			 
			if(usuarioClienteCompra.getId() != null && usuarioClienteCompra.getId() > 0)
				usuarioClienteCompraDao.modificar(usuarioClienteCompra);
			else
				usuarioClienteCompra = usuarioClienteCompraDao.insertar(usuarioClienteCompra);
 
			if(idClienteResponsable > 0)
				listaEntradas.get(li_asiento).setIdClienterResponsableEntrada(idClienteResponsable);
			
			listaEntradas.get(li_asiento).setReservado(false);
			listaEntradas.get(li_asiento).setIdClienteAsignaEntrada(usuarioClienteCompra.getId());
			entradaDao.modificar(listaEntradas.get(li_asiento));

			
			
			if(enviarMensaje())
			{
				li_asiento++;
				ab_verImprimir = true && !ab_verSalir;
			}
			
			 
			
			limpiarClienteCompra();
			ab_verGuardar = (li_asiento + 1) <=  li_totalAsientos;
			
			if(!ab_verGuardar)
				ls_textoFinal = "Listo, se procesaron todas las entradas";
			
		} catch (Exception e) {
			logger.info("\n--------\nError\n" + e.getMessage());
		    e.printStackTrace();
		
		}
	}
	
	public boolean enviarMensaje() {
		//pr_evento
	   {
		   if(usuarioClienteCompra == null)
		   {
			   Mensaje("Lo siento no reconozco al cliente para la venta.", true, false);
				return false;
		   }

			
			{
				String ls_emails = usuarioProceso.getEmail();
				try
				{
					final TextMessage mensaje = new TextMessage(
							String.format("%s, se está enviando un email con el código de acceso para ir a ver el partido " + pr_evento.getNombre()+" en la localidad "+ls_localidad, usuarioProceso.getNombreFacebook()));

					/*ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuarioProceso.getIdFacebook(), mensaje),
							propiedadesLola.facebookToken);*/
					
					Random r = new Random();
					int sector = r.nextInt(7) + 1;
					
					int puertaEntrada = r.nextInt(3) + 1;
					
					 
					int fila = listaEntradas.get(li_asiento).getFila();

					int Asiento = listaEntradas.get(li_asiento).getAsiento();
					
					ls_emails = usuarioClienteCompra != null && usuarioClienteCompra.getEmail() != null ? usuarioClienteCompra.getEmail() : ls_emails;
					
					/*
					String ls_codifiar = CodigoQR.getValidadorinicioQR()
							+ " Orden: " + listaEntradas.get(li_asiento).getId()+ ";"
							+ " Comprador: " + 
							(usuarioClienteCompra.toString() != null ? usuarioClienteCompra.toString().trim() : "S/AP") + ";" +
	             			" Identificacion: " + 
				            (usuarioClienteCompra.getIdentificacion() != null ? usuarioClienteCompra.getIdentificacion().trim() : "S/ID") + ";" +
			                " Partido: " + 
			                pr_evento.getNombre().trim() + ";" +
			                " Localidad: " + 
			                ls_localidad.trim()+ ";" +
			                " Sector: " + 
			                sector + ";" +
			                " Fila: " + 
			                fila+ ";" +
			                " Asiento: " + 
			                Asiento + ";" +
			                " Puerta: " + 
			                puertaEntrada + ";" +
			                " Generado por: " + 
			                usuarioProceso.toString()
				             ;
				             */
					
					String ls_codifiar = CodigoQR.getValidadorinicioQR() +
							listaEntradas.get(li_asiento).getId();
					
					String nombreArchivo = "";
					boolean lb_genoeroCodigo = false;
					try
					{
						//Cambiar el nombre del archivo de codigo deberia ser el id de la entrada
						lb_genoeroCodigo = generacodigoQR(usuarioProceso, ls_codifiar);
					
						if(lb_genoeroCodigo)
						{
							final Date hoy = new Date();
							final Date manana =  new Date(hoy.getTime() + (1000 * 60 * 60 * 24));
							final Date pasadomanana = new Date(manana.getTime() + (1000 * 60 * 60 * 24));
							final Date pasadopasadomanana = new Date(pasadomanana.getTime() + (1000 * 60 * 60 * 24));
							
							SimpleDateFormat dt1 = new SimpleDateFormat("dd - MMMM - yyyy  HH:mm");
							SimpleDateFormat dt2 = new SimpleDateFormat("ddMMyyyyHHmmss");
							
							nombreArchivo = listaEntradas.get(li_asiento).getId().toString(); //usuarioProceso.getId().toString().trim()+dt2.format(pasadopasadomanana).trim();
							
						
							lb_genoeroCodigo = GenerarBoletoAFNA.generarBoleto(pr_evento.getId(), usuarioProceso.getId(), CodigoQR.DiaDeSemana(pasadopasadomanana).trim() + "  " + dt1.format(pasadopasadomanana), 
									pr_evento.getObservacion().trim(), 
								(usuarioClienteCompra.getIdentificacion() != null ? usuarioClienteCompra.getIdentificacion().trim() : "S/ID") + " - " + (usuarioClienteCompra.toString() != null ? usuarioClienteCompra.toString().trim() : "S/AP") , 
								String.format("%02d", puertaEntrada), //Entrada 
				             	String.format("%02d", sector), 
				             	String.format("%02d", fila), 
				             	String.format("%02d", Asiento),
				             	(pr_evento.getNombre().trim().length() > 25 ? pr_evento.getNombre().trim().substring(0, 25) : pr_evento.getNombre().trim()),
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
						Mensaje("Lo siento no pude generar la entrada, por favor intentalo nuevamente.", true, false);
						return false;
						/*return new ResponseMessageApiAi(":( Lo siento no pude generar tu entrada, por favor intentalo nuevamente.",
								":( Lo siento no pude generar tu entrada, por favor intentalo nuevamente.", null, null, "afna");*/
					}
					
					String ls_mensaje = "";
					
					urlIMprime = pathImagenesQR.trim()+nombreArchivo.trim()+".png";
					
					
					//System.out.println("--------------------------"+urlIMprime);
					
					
					if(usuarioClienteCompra.getEmail() != null && usuarioClienteCompra.getEmail().trim().length() > 0)
					{
						ls_emails = usuarioClienteCompra.getEmail();
						 ls_mensaje = MailUtil.enviarMail(ls_emails,"AFNA - Hola Lola", "Se solicitó ir a ver el partido " + pr_evento.getNombre() +" para la localidad "+ ls_localidad +
																			" Sector: " + 
															                sector + "," +
															                " Fila: " + 
															                fila+ "," +
															                " Asiento: " + 
															                Asiento +
															                 " Puerta: " + 
															                 puertaEntrada +
								                                          ", adjuntamos la entrada que disfrutes del partido, suerte...", true,
								                                          pathImagenesQRReal+nombreArchivo.trim()+".png"
																		   );
					}
					
					if (ls_mensaje != null && ls_mensaje.trim().length() > 0) {
						//System.out.println("N-----------------------------------------------------------Email Enviado ---- "+patSonido.replace("sonido/alarma.mp3", "imgBD.jpg"));
						
						final TextMessage mensaje1 = new TextMessage(
								String.format("%s, adjunto la entrada de "+usuarioClienteCompra.toString()+", disfruten del partido (Y) mucha suerte: ", usuarioProceso.getNombreFacebook()));

						ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuarioProceso.getIdFacebook(), mensaje1),
								propiedadesLola.facebookToken);
						Thread.sleep(500l);
						ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuarioProceso.getIdFacebook(),new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.IMAGE, new MediaPayload(pathImagenesQR.trim()+nombreArchivo.trim()+".png")))),
								propiedadesLola.facebookToken);
						
						return true;
						
					} else {
						System.out.println("No se pudo enviar el mensaje ");
						
						Mensaje("Lo siento no puedo enviar la inforamción a tu email.", true, false);
						return false;
						
						/*final TextMessage mensaje1 = new TextMessage(
								String.format("%s, :( Lo siento no pude enviar la inforamción a tu email. ", usuarioProceso.getNombreFacebook()));

						ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuarioProceso.getIdFacebook(), mensaje1),
								propiedadesLola.facebookToken);
								*/

					}
					
				}
				catch(Exception err)
				{
					System.out.println("No se pudo enviar el mensaje "+err);
					Mensaje("Lo siento no pude generar tu entrada, por favor intentalo nuevamente.", true, false);
					return false;
					/*return new ResponseMessageApiAi(":( Lo siento no pude enviar la inforamción a tu email.",
							":( Lo siento no pude enviar la inforamción a tu email.", null, null, "afna");*/
				}
			}
			/*else
			{
				return new ResponseMessageApiAi("No posees un email relacionado para poderte enviar la inforamción.",
						"No posees un email relacionado para poderte enviar la inforamción.", null, null, "afna");
			}*/
		}

		/*long detalleProductoId = Long
				.valueOf(obtenerValorParametro(resultAi, PARAM_ID_DETALLE_PRODUCTO, CONTEXTO_COMPLETA_AFNA));

		return completarAcompananteProducto(acompanantesProducto, orden, 0, detalleProductoId);*/
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

	
	public Producto getPr_evento() {
		return pr_evento;
	}

	public void setPr_evento(Producto pr_evento) {
		this.pr_evento = pr_evento;
	}

	public String getLs_localidad() {
		return ls_localidad;
	}

	public void setLs_localidad(String ls_localidad) {
		this.ls_localidad = ls_localidad;
	}

	public int getLi_totalAsientos() {
		return li_totalAsientos;
	}

	public void setLi_totalAsientos(int li_totalAsientos) {
		this.li_totalAsientos = li_totalAsientos;
	}

	public Usuario getUsuarioProceso() {
		return usuarioProceso;
	}

	public void setUsuarioProceso(Usuario usuarioProceso) {
		this.usuarioProceso = usuarioProceso;
	}

	public UsuarioClienteCompra getUsuarioClienteCompra() {
		return usuarioClienteCompra;
	}

	public void setUsuarioClienteCompra(UsuarioClienteCompra usuarioClienteCompra) {
		this.usuarioClienteCompra = usuarioClienteCompra;
	}

	public int getLi_asiento() {
		return li_asiento + 1;
	}

	public boolean isAb_verGuardar() {
		return ab_verGuardar;
	}

	public String getUrlIMprime() {
		return urlIMprime;
	}
	
	public String getVisible()
	{
		return ab_verGuardar ? "visible" : "hidden";
	}

	public boolean isAb_verSalir() {
		return ab_verSalir;
	}

	public String getLs_textoFinal() {
		return ls_textoFinal;
	}
	
	
}
