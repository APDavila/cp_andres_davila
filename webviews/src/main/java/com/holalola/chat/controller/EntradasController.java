package com.holalola.chat.controller;

import static com.holalola.util.TextoUtil.esVacio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holalola.comida.pedido.ejb.modelo.AcompananteProducto;
import com.holalola.comida.pedido.ejb.modelo.Producto;
import com.holalola.ejb.general.servicio.AcompananteProductoServicio;
import com.holalola.ejb.general.servicio.ProductoServicio;
import com.holalola.ejb.general.servicio.UsuarioEntradasServicio;
import com.holalola.general.ejb.dao.UsuarioClienteCompraDao;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioClienteCompra;
import com.holalola.webhook.ejb.dao.DatosClienteEntradaDao;
import com.holalola.webhook.ejb.modelo.DatosClienteEntrada;


@ManagedBean
@SuppressWarnings("serial")
@SessionScoped
public class EntradasController extends GeneralController {
	private static final DateFormat fmtJavascript = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	final private String pathImagenesQRReal = System.getProperty("lola.images.path")+"AFNA/Boletos/";
	
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
	
	public Date ad_fecha;
	public String opcionPartido;
	public String localidadPartido;
	public String tipoIdentificacion = "C";
	public String identificacion;
	public String nombres;
	public String apellidos;
	public String direccion;
	public String telefono;
	public String email;
    public String nombreTest;
    public Long id_UsuarioResponsable;
    public Long id_ClienteResponsable;
    public Long id_UsuarioLoguedo = (long) 82;
    
    public boolean verListo=false;
    public int numeroEntradas;
    public boolean verFactura=false;
    public boolean estado=false;
    public boolean visible=false;
    public boolean ab_estadoEncuentraBase=false;
    public String acompananteProductoString;
    public static String productoString;
    public List<AcompananteProducto> acompananteProducto=new ArrayList<AcompananteProducto>();
    public List<Producto> productoLista=new ArrayList<Producto>();
    UsuarioClienteCompra usuarioClienteResponsable;
    public Usuario usuario;
   
    public static String evento;
    public static String descripcion;
    
     
    private StreamedContent graphicText;
        
    public boolean isVerListo() {
		return verListo;
	}

	public void setVerListo(boolean verListo) {
		this.verListo = verListo;
	}

	public Long getId_UsuarioLoguedo() {
		return id_UsuarioLoguedo;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public StreamedContent getGraphicText() {
		return graphicText;
	}

	public void setGraphicText(StreamedContent graphicText) {
		this.graphicText = graphicText;
	}



	public String getPathImagenesQRReal() {
		return pathImagenesQRReal;
	}

	public String getNombreTest() {
		return nombreTest;
	}

	public void setNombreTest(String nombreTest) {
		this.nombreTest = nombreTest;
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getProductoString() {
		return productoString;
	}

	public void setProductoString(String productoString) {
		this.productoString = productoString;
	}

	

	public List<Producto> getProductoLista() {
		return productoLista;
	}

	public void setProductoLista(List<Producto> productoLista) {
		this.productoLista = productoLista;
	}

	public String getAcompananteProductoString() {
		return acompananteProductoString;
	}

	public void setAcompananteProductoString(String acompananteProductoString) {
		this.acompananteProductoString = acompananteProductoString;
	}
    
    public List<AcompananteProducto> getAcompananteProducto() {
		return acompananteProducto;
	}

	public void setAcompananteProducto(List<AcompananteProducto> acompananteProducto) {
		this.acompananteProducto = acompananteProducto;
	}

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}

    
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	@EJB
	UsuarioEntradasServicio usuarioServicio;
	
	@EJB
	AcompananteProductoServicio acompananteProductoServicio;
	
	@EJB
	ProductoServicio productoServicio;
	
	@EJB
	UsuarioClienteCompraDao usuarioClienteCompDao;
	
	@EJB
	DatosClienteEntradaDao datosClienteEntradaDao;
	
	@EJB
	UsuarioClienteCompraDao usuarioClienteCompraDao;
	
	@EJB 
	UsuarioDao usuarioDao; 


	@PostConstruct
	public void inicializar() {
		
		visible=false;
		verFactura= false;
		//graphicText = new DefaultStreamedContent();
		
		obtenerProducto();
		Date fechaInicioProceso = Calendar.getInstance().getTime();

		try {
			if(opcionPartido == null)
			{
				ad_fecha = new Date();
			    opcionPartido = "";
			    localidadPartido = "";
			    tipoIdentificacion = "";
			    identificacion = "";
			    nombres = "";
			    direccion = "";
			    telefono = "";
			    email = "";
			    numeroEntradas = 0;
			    ulrEntradas = "";
			    verFactura = false;
			}
			
		} catch (Exception e) {
			logger.info("\n--------\n Error - MensajesBroadcastController");
			e.printStackTrace();
			Mensaje(e.getMessage(), false, true);
		}
	}
	
	Date ld_fechaProceso = new Date();
	
	 public void Limpiar() {  
		 
		 //logger.info("\n--------\n Error - Limpiar");
		 
		 ad_fecha = new Date();
		    opcionPartido = "";
		    localidadPartido = "";
		    tipoIdentificacion = "";
		    identificacion = "";
		    nombres = "";
		    apellidos = "";
		    direccion = "";
		    telefono = "000000000";
		    email = "";
		    numeroEntradas = 0;
		    verFactura = false;
		    
		    ulrEntradas = "";
			numeroEntradas = 0;
			ld_fechaProceso = new Date();
			verListo = false;
			
			/*RequestContext context = RequestContext.getCurrentInstance();
			context.execute(" location.reload();");*/
		  
	 } 
	 
	 public void imprimir(ActionEvent ae) {
		 
		 try
		 {
		 if(identificacion != null && identificacion.trim().length() > 0){
	        	DatosClienteEntrada datosClienteEntradaTemporal = null;
	        	try
				{
	        		datosClienteEntradaTemporal = datosClienteEntradaDao.obtenerCliente(identificacion);
				}
				catch(Exception err)
				{
					datosClienteEntradaTemporal = null;
				}
	        	
	        	
	        	if(id_ClienteResponsable != null && id_ClienteResponsable > 0)
	        		usuarioClienteResponsable = usuarioClienteCompraDao.obtenerPorId(id_ClienteResponsable);
	        	else
	        		usuarioClienteResponsable = new UsuarioClienteCompra();
	        	
	        	usuarioClienteResponsable.setTipoIdentificacion(tipoIdentificacion);
	        	usuarioClienteResponsable.setApellido(apellidos);
	        	usuarioClienteResponsable.setCelular(telefono);
	        	usuarioClienteResponsable.setDireccion(direccion);
	        	usuarioClienteResponsable.setEmail(email);
	        	usuarioClienteResponsable.setIdentificacion(identificacion);
	        	usuarioClienteResponsable.setNombre(nombres);
	        	
	        	if(usuarioClienteResponsable.getId() == null || usuarioClienteResponsable.getId() <= 0) 
	        		usuarioClienteResponsable = usuarioClienteCompraDao.insertar(usuarioClienteResponsable);
	        	else
	        		usuarioClienteCompraDao.modificar(usuarioClienteResponsable);
	        	
	        	id_ClienteResponsable = usuarioClienteResponsable.getId(); 
	        	
	        }
		 }
		 catch(Exception err)
		 {
			 
		 }
		 irAImpresion();	
	}
	 
	 public String armarUrlGeneraEntradas() {
		 Long al_idEvento = Long.parseLong(productoString);
		 String as_localidad = acompananteProductoString;
		 int ai_totalAsientos = numeroEntradas;
		 Long al_idUsuario = id_UsuarioResponsable;
		 boolean ab_esWeb = true;
		 
			SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
			String ls_url = baseUrl + "webviews/facturas/procesaEntradas.jsf?iev="+ al_idEvento +"&iloc="+as_localidad.trim()+"&numasie="+ai_totalAsientos+"&token="+al_idUsuario+"&web="+(ab_esWeb ? 1 : 0) + 
					"&cliResp="+(id_ClienteResponsable != null ? id_ClienteResponsable : 0)+"&tokensg="+formatter.format(ld_fechaProceso);
			
			//formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
			
			System.out.println("------------gen----------------------Ingreso "+formatter.format(ld_fechaProceso));
			return ls_url;
		}
	 
	 public void obtenerDatosForm() {
		// System.out.println("--------Producto id metodo obtener datos form--------"+productoString);
		 
		 for (Producto objetoSacado : productoLista) {
			// System.out.println("--------id--------"+objetoSacado.getId());
			 	if (objetoSacado.getId().equals(new Long(productoString))) {
			 		 evento=objetoSacado.getNombre();
			 		 descripcion=objetoSacado.getObservacion();
			 	}
	        }
		 
		
		// System.out.println("--------Producto nombre--------"+evento);
		// System.out.println("--------Producto nombre--------"+descripcion);
	 }
	 
	 public void nuevo(ActionEvent ae) {  
		 
		 try
		 {
			 Limpiar();
			 
		 } catch (Exception e) {
			e.printStackTrace();
		 }
	 }
	 

	 public void obtenerDatosUsuario () {
 
		 nombres = "";
 		 apellidos = "";
 		 direccion = "";
 		 telefono = "";
 		 email ="";
 		id_UsuarioResponsable = (long) 0;
 		id_ClienteResponsable = (long) 0;
 		
 		tipoIdentificacion = tipoIdentificacion == null || tipoIdentificacion.trim().length() <= 0 ? "C" : tipoIdentificacion;
 		ld_fechaProceso = new Date();	
		 if(identificacion != null && identificacion.trim().length() > 0){
	        	DatosClienteEntrada datosClienteEntradaTemporal = null;
	        	try
				{
	        		datosClienteEntradaTemporal = datosClienteEntradaDao.obtenerCliente(identificacion);
				}
				catch(Exception err)
				{
					datosClienteEntradaTemporal = null;
				}
	        	
	        	if(datosClienteEntradaTemporal != null)
	        	{
	        		nombres = datosClienteEntradaTemporal.getNombre();
	        		apellidos = datosClienteEntradaTemporal.getApellido();
	        		direccion = datosClienteEntradaTemporal.getDireccion();
	        		telefono = datosClienteEntradaTemporal.getCelular();
	        		email = datosClienteEntradaTemporal.getEmail();
	        		id_UsuarioResponsable = datosClienteEntradaTemporal.getIdUsuario();
	        		id_ClienteResponsable = datosClienteEntradaTemporal.getIdCliente();
	        	}
	        	
	        	if(id_ClienteResponsable != null && id_ClienteResponsable > 0)
	        		usuarioClienteResponsable = usuarioClienteCompraDao.obtenerPorId(id_ClienteResponsable);
	        	else
	        		usuarioClienteResponsable = new UsuarioClienteCompra();
	        	
	        	usuarioClienteResponsable.setTipoIdentificacion(tipoIdentificacion);
	        	usuarioClienteResponsable.setApellido(apellidos);
	        	usuarioClienteResponsable.setCelular(telefono);
	        	usuarioClienteResponsable.setDireccion(direccion);
	        	usuarioClienteResponsable.setEmail(email);
	        	usuarioClienteResponsable.setIdentificacion(identificacion);
	        	usuarioClienteResponsable.setNombre(nombres);
	        	
	        	
	        	if(usuarioClienteResponsable.getId() == null || usuarioClienteResponsable.getId() <= 0) 
	        		usuarioClienteResponsable = usuarioClienteCompraDao.insertar(usuarioClienteResponsable);
	        	else
	        		usuarioClienteCompraDao.modificar(usuarioClienteResponsable);
	        	
	        	id_ClienteResponsable = usuarioClienteResponsable.getId(); 
	        	
	        }
			
		}
	 
	 public void obtenerUsuario() {
		obtenerDatosUsuario();
		 
	 }
	 
	 // Alex Inicio
	 public void ingresarActualizarDatosUsuClienteCom () {
		 if(id_ClienteResponsable != null && id_ClienteResponsable > 0)
     		usuarioClienteResponsable = usuarioClienteCompraDao.obtenerPorId(id_ClienteResponsable);
     	else
     		usuarioClienteResponsable = new UsuarioClienteCompra();
     	
     		usuarioClienteResponsable.setTipoIdentificacion(tipoIdentificacion);
     		usuarioClienteResponsable.setApellido(apellidos);
     		usuarioClienteResponsable.setCelular(telefono);
     		usuarioClienteResponsable.setDireccion(direccion);
     		usuarioClienteResponsable.setEmail(email);
     		usuarioClienteResponsable.setIdentificacion(identificacion);
     		usuarioClienteResponsable.setNombre(nombres);
     	
     	if(usuarioClienteResponsable.getId() == null || usuarioClienteResponsable.getId() <= 0)
     		usuarioClienteResponsable = usuarioClienteCompraDao.insertar(usuarioClienteResponsable);
     	else
     		
     		usuario = new Usuario();
     		usuario.setTipoIdentificacion(tipoIdentificacion);
     		usuario.setApellidos(apellidos);
     		usuario.setNombres(nombres);
     		usuario.setNumeroIdentificacion(identificacion);
     		usuario.setCelularPayphone(telefono);
     		usuario.setEmail(email);
     		
     		usuarioDao.insertar(usuario);
     		usuarioClienteCompraDao.modificar(usuarioClienteResponsable);
     	
     		id_UsuarioResponsable = usuario.getId(); 
	 }
	 
	 //Alex Fin
	 
	 public void obtenerAcompananteProducto() {
		 
		 acompananteProducto= acompananteProductoServicio.obtenerAcompananteProductoPorTipoAffna(Long.parseLong(productoString));
		 obtenerDatosForm();
		// System.out.println("--------------Tamano----------"+acompananteProducto.size());
	 }
	 
	 Long ll_idProveedorAfna = (long) 13;
	 public void obtenerProducto() {
		  productoLista=productoServicio.obtenerProductoActivo(ll_idProveedorAfna);
		 
		// System.out.println("--------------Tamano----------"+productoLista.size());
	 }
	 //Alex FIn
	 
	 

	public Date getAd_fecha() {
		return ad_fecha;
	}

	

	public void setAd_fecha(Date ad_fecha) {
		this.ad_fecha = ad_fecha;
	}

	public String getOpcionPartido() {
		return opcionPartido;
	}

	public void setOpcionPartido(String opcionPartido) {
		this.opcionPartido = opcionPartido;
	}

	public String getLocalidadPartido() {
		return localidadPartido;
	}

	public void setLocalidadPartido(String localidadPartido) {
		this.localidadPartido = localidadPartido;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getIdentificacion() {
		return identificacion;
	}

	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}
	
	public String getTotal() {
		return String.format("%.2f", numeroEntradas * 5.71);
	}
	
	public String getImpuesto() {
		return String.format("%.2f", ((numeroEntradas * 5.71) * 1.05) - (numeroEntradas * 5.71));
	}
	
	public String getTotalConImp() {
		return String.format("%.2f", ((numeroEntradas * 5.71) * 1.05));
	}
	
	private static final String baseUrl = System.getProperty("lola.base.url");
	private final String ulrEnvio =  baseUrl + "webviews/facturas/impFactura.jsf";
	String ulrEntradas = "";
	
	
	
	public String getUlrEntradas() {
		 //logger.info("\n--------\nURL Retorno " + ulrEntradas);
		return ulrEntradas;
	}

	public String getUrlIMprime() {
		return ulrEnvio;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getNumeroEntradas() {
		return numeroEntradas;
	}

	public void setNumeroEntradas(int numeroEntradas) {
		this.numeroEntradas = numeroEntradas;
	}

	public boolean isVerFactura() {
		return verFactura;
	}

	public void setVerFactura(boolean verFactura) {
		this.verFactura = verFactura;
	}
	
	SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
	public String getFecha() {
		return formateador.format(ad_fecha);
	}
	
	public String getTelefonoSimple() {
		return telefono.replace("(", "").replace(")", "").replace("-", "");
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	private String ls_boleto;
	
	public String getLs_boleto() {
		return ls_boleto;
	}

	public void setLs_boleto(String ls_boleto) {
		this.ls_boleto = ls_boleto;
	}

	public String getBoleto() {
		try
		{
			if(ls_boleto != null)
			{
				return ls_boleto;
			}
			return "";
		}
		catch(Exception err)
		{
			return "";
		}
	}
	
	@SuppressWarnings("deprecation")
	public void cerrarBoletos() {
		Limpiar();
	}
	
	public void irAImpresion() {
		
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {
			
			ulrEntradas = "";
			if(productoString == null || productoString.trim().length() <= 0)
			{
				Mensaje("Disculpa no reconozco el evento...", true, false);
				return;
			}
			 
			if(tipoIdentificacion == null || tipoIdentificacion.trim().length() <= 0)
			{
				Mensaje("Por favor selecciona un tipo de identificación", true, false);
				return;
			}
			 
			if(identificacion == null || identificacion.trim().length() <= 0)
			{
				Mensaje("Por favor ingresar un número de identificación", true, false);
				return;
			}
		 
			if(tipoIdentificacion.trim().equals("C") && !Utilidades.validaCedula(identificacion))
			{
				Mensaje("Por favor ingresar un número de Cédula válido", true, false);
				return;
			}
			else
			{
				if(tipoIdentificacion.trim().equals("R") && !Utilidades.validaRuc(identificacion))
				{
					Mensaje("Por favor ingresar un número de RUC válido", true, false);
					return;
				}
			}
			 
			if(nombres == null || nombres.trim().length() <= 2)
			{
				Mensaje("Por favor ingresar un nombre válido.", true, false);
				return;
			}
			 
			if(email != null && !Utilidades.validaEmail(email))
			{
				Mensaje("Por favor ingresar un E-mail válido.", true, false);
				return;
			}
			
			try
			{
				if(Long.parseLong(productoString) <= 0)
				{
					Mensaje("Por favor Seleccione un Evento.", true, false);
					return;
				}
			}
			catch(Exception err)
			{
				Mensaje("Por favor Seleccione un Evento.", true, false);
				return;
			}
			
			if(acompananteProductoString == null || acompananteProductoString.trim().length() <= 0)
			{
				Mensaje("Por favor Seleccione una Localidad.", true, false);
				return;
			}
			 
			try
			{
				if(numeroEntradas <= 0)
				{
					Mensaje("Por favor Seleccione un número de asientos.", true, false);
					return;
				}
			}
			catch(Exception err)
			{
				Mensaje("Por favor Seleccione un número de asientos.", true, false);
				return;
			}
			
			//Alex Inicio
			ingresarActualizarDatosUsuClienteCom();
			//Alex Fin
			
			ulrEntradas = armarUrlGeneraEntradas();
			
			verListo = true;
			
			/*RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlg2').show();");*/
			
		} catch (Exception e) {
			logger.info("\n--------\nError\n" + e.getMessage());
		    e.printStackTrace();
		
		}
	}
	
	public boolean isVerImpresion() {		
		return (ulrEntradas != null && ulrEntradas.trim().length() > 0);
	}
	
	
	public void numeroEntradasCambio() {
		 ld_fechaProceso = new Date();
		 //logger.info("\n--------lrEntradas------------" + ulrEntradas);
	}
	
	
}
