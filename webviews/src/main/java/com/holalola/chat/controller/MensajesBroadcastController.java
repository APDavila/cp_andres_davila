package com.holalola.chat.controller;

import static com.holalola.util.TextoUtil.esVacio;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.broadcast.dao.FiltroMensajeOpcionBroadcastDao;
import com.holalola.broadcast.dao.MensajesBroadcastDao;
import com.holalola.broadcast.dao.OpcionesBroadcastDao;
import com.holalola.broadcast.dao.UsuarioOpcionesBroadcastDao;
import com.holalola.broadcast.entidad.FiltroMensajeOpcionBroadcast;
import com.holalola.broadcast.entidad.MensajesBroadcast;
import com.holalola.broadcast.entidad.OpcionesBroadcast;
import com.holalola.broadcast.entidad.UsuarioOpcionesBroadcast;
import com.holalola.cine.ejb.servicio.SupercinesServicio;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.response.message.Attachment;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

//<img src="#{mensajesBroadcastController.mensajesBroadcast.urlImagen}" style="width: 25px; height: 25px;" /> 

@ViewScoped
@ManagedBean
@SuppressWarnings("serial")
public class MensajesBroadcastController  extends GeneralController {
	
	private static final DateFormat fmtJavascript = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	
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
	
	boolean lb_activos; 
	String ls_filtrarSexo;
	String ls_filtrarNombre;
	Date lt_horaNueva;
	String ls_mensaje;
	private Date ad_fechaInicio;
    private Date ad_fechaFin;
    List<MensajesBroadcast> ls_mensajesBroadcast;
    MensajesBroadcast mensajesBroadcast;
    boolean ab_verDetalle = false;
    SimpleDateFormat hora = new SimpleDateFormat("HH:mm");
    boolean ab_enviarSoloNuevos = true;
    UploadedFile file;
    
    public UploadedFile getFile() {
        return file;
    }



    public void setFile(UploadedFile file) {
        this.file = file;
    }
    
    private List<SelectItem> opciones;
    
    private List<FiltroMensajeOpcionBroadcast> listaFiltroMensajeOpcionBroadcast;
    
    List<OpcionesBroadcast> ls_opcionesBroadcast;
    OpcionesBroadcast opcionesBroadcast;
    boolean lb_guardarOpcion = false;
    List<OpcionesBroadcast> ls_opcionesBroadcastProcesados;
    
    private String[] opcionesSeleccionadas;
	
	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private SupercinesServicio supercinesServicio;
	
	@EJB
	private MensajesBroadcastDao mensajesBroadcastDao;
	
	@EJB
	private OpcionesBroadcastDao opcionesBroadcastDao;
	
	@EJB
	private UsuarioDao usuarioDao;
	
	@EJB
	private FiltroMensajeOpcionBroadcastDao filtroMensajeOpcionBroadcastDao;
	
	@EJB
	private UsuarioOpcionesBroadcastDao usuarioOpcionesBroadcastDao;
	
	@PostConstruct
	public void inicializar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();

		try {
			ab_verDetalle = false;
			lb_activos = true;
			//hora.setTimeZone(TimeZone.getTimeZone("UTC"));
			mensajesBroadcast = new MensajesBroadcast("", "", true, new Date(), new Date(), hora.format(new Date().getTime()), "", "", new Date(), false, "", true,"");
			ls_opcionesBroadcast = new ArrayList<>();
		    opcionesBroadcast = new OpcionesBroadcast();
			
		} catch (Exception e) {
			logger.info("\n--------\n Error - MensajesBroadcastController");
			e.printStackTrace();
			supercinesServicio.crearLogExc("MensajesBroadcastController-inicializar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			Mensaje(e.getMessage(), false, true);
		}
	}
	
	 public void handleFileUpload(FileUploadEvent event) {
		 try {
	            file = event.getFile();
	            
	            File fileImage=new File(System.getProperty("lola.images.pathImg")+"Broadcast\\"+file.getFileName().trim());
	            InputStream inputStream = file.getInputstream();
	            SaveImage(inputStream,fileImage);
	            
	            if(mensajesBroadcast != null)
	                 mensajesBroadcast.setUrlImagen(System.getProperty("lola.images.urlImghl")+"Broadcast/"+file.getFileName().trim());
	
	            Mensaje("Imagen procesada correctamente", false, false);
		 } catch (Exception e) {
				logger.info("\n--------\n Error - MensajesBroadcastController");
				e.printStackTrace();
				
				Mensaje(e.getMessage(), false, true);
			}
	    }
	
	public void SaveImage(InputStream inputStream, File ImageFile) throws IOException {
		InputStream is = inputStream;
		OutputStream os = new FileOutputStream(ImageFile);

		byte[] b = new byte[2048];
		int length;

		while ((length = is.read(b)) != -1) {
			os.write(b, 0, length);
		}

		is.close();
		os.close();
     }
	
	 public void buscar(ActionEvent ae) {  
		 Date fechaInicioProceso = Calendar.getInstance().getTime(); 
		 try {
			 ab_verDetalle = false;
			 ls_opcionesBroadcast = new ArrayList<>();
			 lb_guardarOpcion = false;
			 //hora.setTimeZone(TimeZone.getTimeZone("UTC"));
			 
			 if(ad_fechaInicio == null && ad_fechaFin == null)
			 {
				 ls_mensajesBroadcast = mensajesBroadcastDao.buscar(lb_activos, (ls_filtrarSexo == null || ls_filtrarSexo.trim().length() <= 0 ? "%" : "%"+ls_filtrarSexo.trim()+"%") , 
						 														(ls_filtrarNombre == null || ls_filtrarNombre.trim().length() <= 0 ? "%" : "%"+ls_filtrarNombre.trim()+"%"), 
						 														(ls_mensaje == null || ls_mensaje.trim().length() <= 0 ? "%" : "%"+ls_mensaje.trim()+"%"));
			 }
			 else
			 {
				 
				 if(ad_fechaInicio == null && ad_fechaFin != null)
					 ad_fechaInicio = ad_fechaFin;
				 
				 if(ad_fechaInicio != null && ad_fechaFin == null)
					 ad_fechaFin = ad_fechaInicio;
				 
				 ls_mensajesBroadcast = mensajesBroadcastDao.buscarConFechas(lb_activos, (ls_filtrarSexo == null || ls_filtrarSexo.trim().length() <= 0 ? "%" : "%"+ls_filtrarSexo.trim()+"%") , 
							(ls_filtrarNombre == null || ls_filtrarNombre.trim().length() <= 0 ? "%" : "%"+ls_filtrarNombre.trim()+"%"), 
							(ls_mensaje == null || ls_mensaje.trim().length() <= 0 ? "%" : "%"+ls_mensaje.trim()+"%"),
							ad_fechaInicio, ad_fechaFin);
			 }
			 
			 if(ls_mensajesBroadcast == null)
				 ls_mensajesBroadcast = new ArrayList<>();
			 
			

			} catch (Exception e) {
				logger.info("\n--------\n Error - MensajesBroadcastController");
				e.printStackTrace();
				supercinesServicio.crearLogExc("MensajesBroadcastController-inicializar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
						e, true);

				Mensaje(e.getMessage(), false, true);
			}
	 } 
	 
	 public void nuevo(ActionEvent ae) {  
		 try {
			 ab_verDetalle = true;
			 //hora.setTimeZone(TimeZone.getTimeZone("UTC"));
			 
			 lt_horaNueva = new Date();
			 mensajesBroadcast = new MensajesBroadcast("", "", true, new Date(), new Date(), hora.format(new Date().getTime()), "", "", new Date(), false, "", true,"");
			 obtenerOpciones();
		 } catch (Exception e) {
				logger.info("\n--------\n Error - MensajesBroadcastController");
				e.printStackTrace();
				Mensaje(e.getMessage(), false, true);
		}
	 } 
	 
	 public void editar(ActionEvent ae) {  
		 ab_verDetalle = mensajesBroadcast != null;
		 try
		 {
			 lt_horaNueva = new Date();
			 if( mensajesBroadcast != null)
			 {
				 String string = mensajesBroadcast.getHora();
				 DateFormat format = new SimpleDateFormat("HH:mm");
				 lt_horaNueva = format.parse(string);
			 }
		 } catch (Exception e) {
			 lt_horaNueva = new Date();
		 }
		 obtenerOpciones();
	 }
	 
	 public void editarAction(MensajesBroadcast ae_mensajesBroadcast) {  
		 ab_verDetalle = ae_mensajesBroadcast != null;
		 try
		 {
			 lt_horaNueva = new Date();
			 if( ae_mensajesBroadcast != null)
			 {
				 String string = ae_mensajesBroadcast.getHora();
				 DateFormat format = new SimpleDateFormat("HH:mm");
				 lt_horaNueva = format.parse(string);
				 mensajesBroadcast = ae_mensajesBroadcast;
			 }
		 } catch (Exception e) {
			 lt_horaNueva = new Date();
		 }
		 obtenerOpciones();
	 }
	 
	 public void guardar(ActionEvent ae) {  
		 Date fechaInicioProceso = Calendar.getInstance().getTime(); 
		 try {
			 
			 mensajesBroadcast.setHora(hora.format(lt_horaNueva.getTime()));
			 
			 if(mensajesBroadcast == null)
			 {
				 Mensaje("No se reconoce el mensaje.", false, true);
				 return;
			 }
			 
			 if(mensajesBroadcast.getMensaje() == null && mensajesBroadcast.getUrlImagen() == null)
			 {
				 Mensaje("No se reconoce el texto ni la imagen del mensaje.", false, true);
				 return;
			 }
			 
			 if((mensajesBroadcast.getMensaje() != null && mensajesBroadcast.getMensaje().trim().length() <= 0) && 
				(mensajesBroadcast.getUrlImagen() != null && mensajesBroadcast.getUrlImagen().trim().length() <= 0))
			 {
				 Mensaje("No se reconoce el texto ni la imagen del mensaje.", false, true);
				 return;
			 }
			 
			 if((mensajesBroadcast.getMensaje() != null && mensajesBroadcast.getMensaje().trim().length() <= 0) && 
						(mensajesBroadcast.getUrlImagen() == null ))
			 {
				 Mensaje("No se reconoce el texto ni la imagen del mensaje.", false, true);
				 return;
			 }
			 
			 if((mensajesBroadcast.getMensaje() == null ) && 
						(mensajesBroadcast.getUrlImagen() != null && mensajesBroadcast.getUrlImagen().trim().length() <= 0))
			 {
				 Mensaje("No se reconoce el texto ni la imagen del mensaje.", false, true);
				 return;
			 }
			 
			 if(mensajesBroadcast.getFechaInicio() != null || mensajesBroadcast.getFechaFin() != null)
			 {
				 if(mensajesBroadcast.getFechaInicio() == null)
				 {
					 Mensaje("No se reconoce la fecha de Inicio del mensaje.", false, true);
					 return;
				 }
				 
				 if(mensajesBroadcast.getFechaFin() == null)
				 {
					 Mensaje("No se reconoce la fecha Fin del mensaje.", false, true);
					 return;
				 }
				 
				 if(mensajesBroadcast.getFechaInicio().compareTo(mensajesBroadcast.getFechaFin()) > 0)
				 {
					 Mensaje("No se reconoce la fecha de Inicio no puede ser mayor a la fecha Final.", false, true);
					 return;
				 }
			 }
			 
			 mensajesBroadcast.setHora(hora.format(lt_horaNueva.getTime()));
			 
			 if(mensajesBroadcast.getFiltrarSexo() == null)
				 mensajesBroadcast.setFiltrarSexo("");
			 
			 if(mensajesBroadcast.getId() == null || mensajesBroadcast.getId() <= 0)
				 mensajesBroadcast = mensajesBroadcastDao.insertar(mensajesBroadcast);
			 else
				 mensajesBroadcastDao.modificar(mensajesBroadcast);
			 
			 List<FiltroMensajeOpcionBroadcast> listaFiltroMensajeOpcionBroadcastTemporal = filtroMensajeOpcionBroadcastDao.obtenerPorMensajesBroadcastTodos(mensajesBroadcast);
			 
			 if(listaFiltroMensajeOpcionBroadcastTemporal != null && listaFiltroMensajeOpcionBroadcastTemporal.size() > 0)
			 {
				 for (FiltroMensajeOpcionBroadcast itemOp : listaFiltroMensajeOpcionBroadcastTemporal) {
					 itemOp.setEstaActivo(false);
					 filtroMensajeOpcionBroadcastDao.modificar(itemOp);
				 }
			 }
			 
			 if(opcionesSeleccionadas != null && opcionesSeleccionadas.length > 0)
			 {
					
				 FiltroMensajeOpcionBroadcast temporal = null;
				 for (String item : opcionesSeleccionadas) {
					 temporal = null;
					 if(listaFiltroMensajeOpcionBroadcastTemporal != null && listaFiltroMensajeOpcionBroadcastTemporal.size() > 0)
					 {
						 for (FiltroMensajeOpcionBroadcast itemOp : listaFiltroMensajeOpcionBroadcastTemporal) {
							 if( item.trim().equals(itemOp.getOpcionesBroadcast().getId().toString().trim()))
							 {
								 temporal = itemOp;
								 break;
							 }
						  }
					 }
					 
					if(temporal != null)
					{
						 
						temporal.setEstaActivo(true);
						filtroMensajeOpcionBroadcastDao.modificar(temporal);
					}
					else
					{
						OpcionesBroadcast opcionesBroadcastTemp = null;
						
						 for (OpcionesBroadcast itemOp : ls_opcionesBroadcastProcesados) {
							 if( item.trim().equals(itemOp.getId().toString().trim()))
							 {
								 opcionesBroadcastTemp = itemOp;
								 break;
							 }
						  }
						
						 if(opcionesBroadcastTemp != null)
						 {
							temporal = new FiltroMensajeOpcionBroadcast(mensajesBroadcast, opcionesBroadcastTemp, true, new Date());
							filtroMensajeOpcionBroadcastDao.insertar(temporal);
						 }
					}
				 }
			 }
			 
			 obtenerOpciones();
			 Mensaje("Guardado Correctamente.", false, false);
			 
			} catch (Exception e) {
				logger.info("\n--------\n Error - MensajesBroadcastController");
				e.printStackTrace();
				supercinesServicio.crearLogExc("MensajesBroadcastController-inicializar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
						e, true);

				Mensaje(e.getMessage(), false, true);
			} 
	 } 
	 
	 private void obtenerOpciones()
	 {
		 Date fechaInicioProceso = Calendar.getInstance().getTime(); 
		 try
		 {
			 if(mensajesBroadcast != null && mensajesBroadcast.getId() != null && mensajesBroadcast.getId() > 0)
			 {
				 ls_opcionesBroadcast = opcionesBroadcastDao.buscar(mensajesBroadcast);
				 obtenerOpcionesProcesadas();
				 listaFiltroMensajeOpcionBroadcast = filtroMensajeOpcionBroadcastDao.obtenerPorMensajesBroadcastActivos(mensajesBroadcast);
				 List<String> listaOpcionesSeleciconadas = new ArrayList<>();
				 if(listaFiltroMensajeOpcionBroadcast != null && listaFiltroMensajeOpcionBroadcast.size() > 0)
				 {
					 for (FiltroMensajeOpcionBroadcast item : listaFiltroMensajeOpcionBroadcast) {
							 listaOpcionesSeleciconadas.add(item.getOpcionesBroadcast().getId().toString());
					  }
				 }
				 
				 String[] opcionesTemporales = new String[listaOpcionesSeleciconadas.size()];
				opcionesTemporales= listaOpcionesSeleciconadas.toArray(opcionesTemporales);  
					
				 opcionesSeleccionadas = opcionesTemporales;
			 }
			 else
			 {
				 ls_opcionesBroadcast = new ArrayList<>();
			 }
			 
			 lb_guardarOpcion = false;
		 
	 	 } catch (Exception e) {
			logger.info("\n--------\n Error - MensajesBroadcastController");
			e.printStackTrace();
			supercinesServicio.crearLogExc("MensajesBroadcastController-obtenerOpciones", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			Mensaje(e.getMessage(), false, true);
		} 
	 }
	 
	 private void obtenerOpcionesProcesadas()
	 {
		 Date fechaInicioProceso = Calendar.getInstance().getTime(); 
		 try
		 {
			 opciones = new ArrayList<SelectItem>();
			 if(mensajesBroadcast != null && mensajesBroadcast.getId() != null && mensajesBroadcast.getId() > 0)
			 {
				ls_opcionesBroadcastProcesados = opcionesBroadcastDao.buscarProcesados();
				

				String ls_Alias = "";
				
				if(ls_opcionesBroadcastProcesados != null && ls_opcionesBroadcastProcesados.size() > 0)
				{
					boolean ab_cargar = false;
					for (OpcionesBroadcast opcionesBroadcast : ls_opcionesBroadcastProcesados) {
						
						ls_Alias = (opcionesBroadcast.getMensajesBroadcast().getAlias() != null && opcionesBroadcast.getMensajesBroadcast().getAlias().trim().length() > 0 ? opcionesBroadcast.getMensajesBroadcast().getAlias().trim() : opcionesBroadcast.getMensajesBroadcast().getId().toString());
						
						 ab_cargar = true;
						 if(ls_opcionesBroadcast != null && ls_opcionesBroadcast.size() > 0)
						 {
							 for (OpcionesBroadcast itemOp : ls_opcionesBroadcast) {
								 if( opcionesBroadcast.getId().equals(itemOp.getId()))
								 {
									 ab_cargar = false;
									 break;
								 }
							  }
						 }
						 if(ab_cargar)
						 {
							 opciones.add( new SelectItem(opcionesBroadcast.getId().toString(), ls_Alias.trim()+" - "+opcionesBroadcast.getOpcion().trim()));
						 }
					}
				}
			 }
			 else
			 {
				 ls_opcionesBroadcast = new ArrayList<>();
			 }
			 
			 lb_guardarOpcion = false;
		 
	 	 } catch (Exception e) {
			logger.info("\n--------\n Error - MensajesBroadcastController");
			e.printStackTrace();
			supercinesServicio.crearLogExc("MensajesBroadcastController-obtenerOpciones", fechaInicioProceso, "", Calendar.getInstance().getTime(),
					e, true);

			Mensaje(e.getMessage(), false, true);
		} 
	 }
	 
	 
	 
	 public void actualizarOpcion(ActionEvent ae) {  
		 obtenerOpciones();
	 }
	 
	 
	 
	 public void nuevaOpcion(ActionEvent ae) {  
	  try {
			 lb_guardarOpcion = false;
 
			 if(mensajesBroadcast == null || mensajesBroadcast.getId() == null || mensajesBroadcast.getId() <= 0)
			 {
				 Mensaje("No se reconoce el Mensaje Broadcast, por favor guardarlo.", false, true);
				 return;
			 }
	 
	 
			 opcionesBroadcast = new OpcionesBroadcast();
			 opcionesBroadcast.setMensajesBroadcast(mensajesBroadcast);
			 opcionesBroadcast.setEstaActivo(true);
			 obtenerOpciones();
			 lb_guardarOpcion = true;
 
			 
		 } catch (Exception e) {
				logger.info("\n--------\n Error - MensajesBroadcastController");
				e.printStackTrace();
				Mensaje(e.getMessage(), false, true);
		}
	 } 
	 
	 public void editarOpcion(ActionEvent ae) {  
		 
		 obtenerOpciones();
		 lb_guardarOpcion = opcionesBroadcast != null;
	 }
	 
	 public void salirOpcion(ActionEvent ae) {  
		 lb_guardarOpcion = false;
	 }
	 
	 public void guardarOpcion(ActionEvent ae) {  
		 Date fechaInicioProceso = Calendar.getInstance().getTime(); 
		 try {
			 
			 if(!lb_guardarOpcion)
				 return;
			 
			 if(mensajesBroadcast == null)
			 {
				 Mensaje("No se reconoce el Mensaje Broadcast, por favor guardarlo.", false, true);
				 return;
			 }
			 
			 if(opcionesBroadcast == null)
			 {
				 Mensaje("No se reconoce la Opción.", false, true);
				 return;
			 }
			 
			 if(opcionesBroadcast.getMensajesBroadcast() == null)
				 opcionesBroadcast.setMensajesBroadcast(mensajesBroadcast);
			 
			 if(opcionesBroadcast.getOpcion() == null && opcionesBroadcast.getOpcion() == null)
			 {
				 Mensaje("Por favor ingresar una opción válida.", false, true);
				 return;
			 }
			 

			 if(opcionesBroadcast.getId() == null || opcionesBroadcast.getId() <= 0)
				 opcionesBroadcast = opcionesBroadcastDao.insertar(opcionesBroadcast);
			 else
				 opcionesBroadcastDao.modificar(opcionesBroadcast);
			 
			 obtenerOpciones();
			 Mensaje("Guardado Correctamente.", false, false);
			 lb_guardarOpcion = false;
			} catch (Exception e) {
				logger.info("\n--------\n Error - MensajesBroadcastController");
				e.printStackTrace();
				supercinesServicio.crearLogExc("MensajesBroadcastController-inicializar", fechaInicioProceso, "", Calendar.getInstance().getTime(),
						e, true);

				Mensaje(e.getMessage(), false, true);
			} 
	 } 
	 
	 
	 public void enviarMensaje(ActionEvent ae) {  
		 
		 try
		 {
			 if(mensajesBroadcast != null)
			 {
				List<Long> listaUsuariosEvnviados =  new ArrayList();
				Usuario usuarioTemp = null;

				List<BigInteger> ls_usuario = mensajesBroadcastDao.dameUsuariosPorMensajeBroadCast(mensajesBroadcast, mensajesBroadcast.getFiltrarNombre(), mensajesBroadcast.getFiltrarSexo(), false);
				if(ls_usuario != null && ls_usuario.size() > 0)
				{
					for (BigInteger id_usuario : ls_usuario) {
						if(!listaUsuariosEvnviados.contains(id_usuario))
						{
							listaUsuariosEvnviados.add(id_usuario.longValue());
						}
					}
				}

				List<FiltroMensajeOpcionBroadcast> listaFiltroMensajeOpcionBroadcastTemporal = filtroMensajeOpcionBroadcastDao.obtenerPorMensajesBroadcastActivos(mensajesBroadcast);
				
				boolean lb_filtrarPorOpciones = listaFiltroMensajeOpcionBroadcastTemporal != null && listaFiltroMensajeOpcionBroadcastTemporal.size() > 0;
				
				List<Usuario> ls_usuarios = new ArrayList<>();
				if(!lb_filtrarPorOpciones)
					ls_usuarios = usuarioDao.obtenerporFiltros((mensajesBroadcast.getFiltrarNombre() == null || mensajesBroadcast.getFiltrarNombre().trim().length() <= 0 ? "%" : "%"+mensajesBroadcast.getFiltrarNombre().trim()+"%"), (mensajesBroadcast.getFiltrarSexo() == null || mensajesBroadcast.getFiltrarSexo().trim().length() <= 0 ? "%" : "%"+mensajesBroadcast.getFiltrarSexo().trim()+"%"));
				else
				{
					usuarioTemp = null;
					Boolean ab_cargar = false;
					List<Long> listaUsuarios =  new ArrayList();
					
					
					for (FiltroMensajeOpcionBroadcast flmMens : listaFiltroMensajeOpcionBroadcastTemporal) {
						List<UsuarioOpcionesBroadcast> lista_usuarioOpcionesBroadcast = usuarioOpcionesBroadcastDao.obtenerPorMensajesBroadcastActivos(flmMens.getOpcionesBroadcast());
						if(lista_usuarioOpcionesBroadcast != null && lista_usuarioOpcionesBroadcast.size() > 0)
						{
							ls_usuarios = new ArrayList<>();
							
							for (UsuarioOpcionesBroadcast usOpcionBrad : lista_usuarioOpcionesBroadcast) {
								usuarioTemp =  usuarioDao.obtenerPorId(usOpcionBrad.getUsuario());
								
								ab_cargar = true;
								if(mensajesBroadcast.getFiltrarNombre() != null && mensajesBroadcast.getFiltrarNombre().trim().length() > 0)
								{
									if((usuarioTemp.getNombreFacebook() != null && usuarioTemp.getNombreFacebook().contains(mensajesBroadcast.getFiltrarNombre().trim())) || 
									   (usuarioTemp.getApellidoFacebook() != null && usuarioTemp.getApellidoFacebook().contains(mensajesBroadcast.getFiltrarNombre().trim())))
										ab_cargar = true;	
									else
										ab_cargar = false;
								}
								
								if(ab_cargar)
								{
									if(mensajesBroadcast.getFiltrarSexo() != null && mensajesBroadcast.getFiltrarSexo().trim().length() > 0)
									{
										if((usuarioTemp.getSexo() != null && usuarioTemp.getSexo().contains(mensajesBroadcast.getFiltrarSexo().trim())))
											ab_cargar = true;
										else
											ab_cargar = false;
									}
								}
								
								if(ab_cargar && !listaUsuarios.contains(usuarioTemp.getId()))
								{
									ls_usuarios.add(usuarioTemp);
									listaUsuarios.add(usuarioTemp.getId());
								}
							}
						}
					}
				}
				
				
				obtenerOpciones();
				
				
				if(ls_usuarios != null && ls_usuarios.size() > 0)
				{
					
					for (Usuario usuario : ls_usuarios) {
						
						if(ab_enviarSoloNuevos && listaUsuariosEvnviados.contains(usuario.getId()))
							continue;
							
						if(mensajesBroadcast.getMensaje() != null && mensajesBroadcast.getMensaje().trim().length() > 0 && !(ls_opcionesBroadcast != null && ls_opcionesBroadcast.size() > 0))
						{
							Thread.sleep(500l);
							ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new TextMessage(String.format(mensajesBroadcast.getMensaje().trim(), usuario.getNombreFacebook()))),
									propiedadesLola.facebookToken);
						}
						
						if(mensajesBroadcast.getUrlImagen() != null && mensajesBroadcast.getUrlImagen().trim().length() > 0)
						{
							Thread.sleep(500l);
							ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new RichMessage( new com.holalola.webhook.facebook.templates.Attachment(Attachment.IMAGE, new MediaPayload(mensajesBroadcast.getUrlImagen().trim())))),
									propiedadesLola.facebookToken);
						}
						
						if(ls_opcionesBroadcast != null && ls_opcionesBroadcast.size() > 0)
						{			
					
							 List<QuickReplyGeneral> buttons = ls_opcionesBroadcast.stream().map(c -> {
											return new TextQuickReply(c.getOpcion(),
													String.format("PROCESA_MES_BROAD %s", c.getId()));
										}).collect(Collectors.toList());
							 String ls_texto = "";
							 if(mensajesBroadcast.getMensaje() != null && mensajesBroadcast.getMensaje().trim().length() > 0)
							{
								 ls_texto = String.format(mensajesBroadcast.getMensaje().trim(), usuario.getNombreFacebook());	
							}
							 Thread.sleep(500l);
							 ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuario.getIdFacebook(),new TextMessage(ls_texto, buttons)),
										propiedadesLola.facebookToken);
						}
					}
					
					Mensaje("Envio Finalizado Correctamente.", false, false);
				}
				
				mensajesBroadcast.setEnviado(true);
				mensajesBroadcastDao.modificar(mensajesBroadcast);
			 }
		 } catch (Exception e) {
			e.printStackTrace();
		 }
	 }
	 
	 public Date getLt_horaNueva() {
		return lt_horaNueva;
	}

	public void setLt_horaNueva(Date lt_horaNueva) {
		this.lt_horaNueva = lt_horaNueva;
	}

	public void salir(ActionEvent ae) {  
		 ab_verDetalle = false;
		 mensajesBroadcast = new MensajesBroadcast("", "", true, new Date(), new Date(), hora.format(new Date().getTime()), "", "", new Date(), false, "", true,"");
	 } 

	public boolean isLb_activos() {
		return lb_activos;
	}
	
	public boolean isVerGrilla() {
		return  ls_mensajesBroadcast != null && ls_mensajesBroadcast.size() > 0;
	}

	public void setLb_activos(boolean lb_activos) {
		this.lb_activos = lb_activos;
	}

	public String getLs_filtrarSexo() {
		return ls_filtrarSexo;
	}

	public void setLs_filtrarSexo(String ls_filtrarSexo) {
		this.ls_filtrarSexo = ls_filtrarSexo;
	}

	public String getLs_filtrarNombre() {
		return ls_filtrarNombre;
	}

	public void setLs_filtrarNombre(String ls_filtrarNombre) {
		this.ls_filtrarNombre = ls_filtrarNombre;
	}

	public String getLs_mensaje() {
		return ls_mensaje;
	}

	public void setLs_mensaje(String ls_mensaje) {
		this.ls_mensaje = ls_mensaje;
	}

	public Date getAd_fechaInicio() {
		return ad_fechaInicio;
	}

	public void setAd_fechaInicio(Date ad_fechaInicio) {
		this.ad_fechaInicio = ad_fechaInicio;
	}

	public Date getAd_fechaFin() {
		return ad_fechaFin;
	}

	public void setAd_fechaFin(Date ad_fechaFin) {
		this.ad_fechaFin = ad_fechaFin;
	}

	public List<MensajesBroadcast> getLs_mensajesBroadcast() {
		return ls_mensajesBroadcast;
	}

	public void setLs_mensajesBroadcast(List<MensajesBroadcast> ls_mensajesBroadcast) {
		this.ls_mensajesBroadcast = ls_mensajesBroadcast;
	}

	public MensajesBroadcast getMensajesBroadcast() {
		return mensajesBroadcast;
	}

	public void setMensajesBroadcast(MensajesBroadcast mensajesBroadcast) {
		this.mensajesBroadcast = mensajesBroadcast;
	}

	public boolean isAb_verDetalle() {
		return ab_verDetalle;
	}

	public void setAb_verDetalle(boolean ab_verDetalle) {
		this.ab_verDetalle = ab_verDetalle;
	}

	public List<OpcionesBroadcast> getLs_opcionesBroadcast() {
		return ls_opcionesBroadcast;
	}

	public void setLs_opcionesBroadcast(List<OpcionesBroadcast> ls_opcionesBroadcast) {
		this.ls_opcionesBroadcast = ls_opcionesBroadcast;
	}

	public OpcionesBroadcast getOpcionesBroadcast() {
		return opcionesBroadcast;
	}

	public void setOpcionesBroadcast(OpcionesBroadcast opcionesBroadcast) {
		this.opcionesBroadcast = opcionesBroadcast;
	}

	public boolean isLb_guardarOpcion() {
		return lb_guardarOpcion;
	}

	public void setLb_guardarOpcion(boolean lb_guardarOpcion) {
		this.lb_guardarOpcion = lb_guardarOpcion;
	}

	public List<SelectItem> getOpciones() {
		return opciones;
	}

	public void setOpciones(List<SelectItem> opciones) {
		this.opciones = opciones;
	}

	public String[] getOpcionesSeleccionadas() {
		return opcionesSeleccionadas;
	}

	public void setOpcionesSeleccionadas(String[] opcionesSeleccionadas) {
		this.opcionesSeleccionadas = opcionesSeleccionadas;
	}

	public boolean isAb_enviarSoloNuevos() {
		return ab_enviarSoloNuevos;
	}

	public void setAb_enviarSoloNuevos(boolean ab_enviarSoloNuevos) {
		this.ab_enviarSoloNuevos = ab_enviarSoloNuevos;
	}	
}
