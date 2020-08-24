package com.holalola.comida.facebook.controller;

import static com.holalola.util.TextoUtil.esVacio;
import static com.holalola.util.TextoUtil.validaEmail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chilkatsoft.CkPublicKey;
import com.chilkatsoft.CkRsa;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.holalola.cine.ejb.servicio.ChilkatServicio;
import com.holalola.cine.ejb.servicio.CiudadServicio;
import com.holalola.cine.ejb.servicio.ProveedorServicio;
import com.holalola.cine.ejb.servicio.SupercinesServicio;
import com.holalola.cine.ejb.servicio.UsuarioLoginServicio;
import com.holalola.cine.ejb.servicio.UsuarioServicio;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.modelo.Ciudad;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioLogin;
import com.holalola.supercines.client.SupercinesApi;
import com.holalola.supercines.client.vo.usuarioLogin;
import com.holalola.supercines.cliente.mov.vo.RespuestaRegistro;
import com.holalola.supercines.cliente.mov.vo.UsuarioLoginMV;
import com.holalola.util.PedidoUtil;

@ManagedBean(name = "loginController")
@ViewScoped
@SuppressWarnings("serial")
public class LoginController extends GeneralController {

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private UsuarioLoginServicio usuarioLoginServicio;

	@EJB
	private CiudadServicio ciudadServicio;
	

	private String ls_usuario = "";
	private String ls_apellido = "";
	private String ls_clave = "";

	private String ls_usuarioReg = "";
	private String ls_claveReg = "";

	private String ls_respuesta = "";
	private String ls_email = "";
	private String ls_identificacion = "";
	private String ls_claveConf = "";

	private boolean solicitarRegistro;
	private boolean solicitarUsuario;
	private boolean solicitarClave;
	private boolean conFbExtensions;
	private boolean mostrarFormulario;
	private boolean ab_loguin = true;

	private Long ciudad;
	private List<Ciudad> ciudades = new ArrayList<Ciudad>();
	private String id_usuario = "";
	private String id_proveedor = "";
	private Usuario usuario;
	private Proveedor proveedor;
	private UsuarioLogin sesionActiva = null;

	private final static String sourceClass = BatchRuntime.class.getName();
	private final static Logger logger = LoggerFactory.getLogger(sourceClass);
	private final static Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

	@EJB
	private UsuarioServicio usuarioServicio;

	@EJB
	private ProveedorServicio proveedorServicio;
	
	@EJB
	private SupercinesServicio supercinesServicio;

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

	@PostConstruct
	public void inicializar() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		try {
			id_usuario = params.get("iu");
			id_proveedor = params.get("ip");
			
			usuario = usuarioServicio.obtenerPorId(Long.parseLong(id_usuario));
			proveedor = proveedorServicio.obtenerPorId(Long.parseLong(id_proveedor));

			if (usuario == null) {
				Mensaje("Disculpa no puedo reconocer tu usuario, por favor reingresa a la opción", false, true);
				return;
			}

			if (proveedor == null) {
				Mensaje("Disculpa no puedo reconocer el proveedor, por favor reingresa a la opción", false, true);
				return;
			}

		} catch (Exception e) {
			
			supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
					Calendar.getInstance().getTime(), e, true);
			
			//logger.info("\n--------\nError\n" + e + "\n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
			return;
		}

		try {

			ciudades = ciudadServicio.obtenerCiudades();

			if (ciudades.size() > 0)
				ciudad = ciudades.get(0).getId();

			UsuarioLogin usuarioLogin = usuarioLoginServicio.obtenerUltimaSesionActivaPorIdUsuario(usuario, proveedor);

			if (usuarioLogin != null) {
				UsuarioLoginMV usrLogin = GSON.fromJson(usuarioLogin.getJson(), UsuarioLoginMV.class);
				if(usrLogin != null)
					ls_usuario = usrLogin.getContent().getEmail();
			}

		} catch (Exception e) {
			
			supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
					Calendar.getInstance().getTime(), e, true);
			
			logger.info("\n--------\nError\n" + e + "\n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}

		conFbExtensions = true;
		mostrarFormulario = true;
		validarCamposPorSolicitar();
	}

	 
	public void solicitarDatosRegistro() {
		solicitarRegistro = true;
		ab_loguin = false;
	}

	public void validarUsuario() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {

			solicitarRegistro = false;
			sesionActiva = null;
			if (esVacio(ls_usuario)) {
				ls_respuesta = "Por favor ingresar un Usuario.";
				Mensaje(ls_respuesta, true, false);
				return;
			}

			if (esVacio(ls_clave)) {
				ls_respuesta = "Por favor ingresar una Clave.";
				Mensaje(ls_respuesta, true, false);
				return;
			}

			//Anterior
			/*String ls_data = Encriptar(CrearJsonParaEncriptar(), ChilkatServicio.getPemLoguin());

			usuarioLogin usrLogin = SupercinesApi.validarUsuario(ls_data);
			*/
			
			String ls_data = CrearJsonParaEncriptarMV();
			
			//System.out.println(ls_data);

			UsuarioLoginMV usrLogin = SupercinesApi.validarUsuario(ls_usuario, ChilkatServicio.getMD5(ls_clave));
			
			sesionActiva = null;

			ls_respuesta = "No se reconoce el Usuario";
			boolean ab_advertencia = false;
			if (usrLogin != null) {
				if (usrLogin.getResponseType() ==null || usrLogin.getResponseType().trim().toUpperCase().equals("ERROR")) {
					ab_advertencia = true;
					ls_respuesta = "Usuario no Registrado, o clave no válida";
				} else {
					if (!esVacio(usrLogin.getContent().getNombres())) {
						ls_respuesta = "Bienvenido " + usrLogin.getContent().getNombres();
						
						//logger.info("\n----ls_respuesta----\n"+ls_respuesta+ "\n-----------------\n");
						
						try {
							sesionActiva = usuarioLoginServicio.obtenerSesionActivaPorIdUsuarioMV(usrLogin, usuario,
									proveedor);
							
							try{
					            FacesContext contex = FacesContext.getCurrentInstance();
					            contex.getExternalContext().redirect(PedidoUtil.urlServidor().trim() + "webviews/cine/reserva.jsf?ids=" + sesionActiva.getId() );
							} catch (Exception e) {
								
								supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
										Calendar.getInstance().getTime(), e, true);
								
								Mensaje("No pude redireccionar", false, true);
							}
							
						} catch (Exception e) {
							
							supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
									Calendar.getInstance().getTime(), e, true);
							
							logger.info("\n--------\nError al tratar de guardar la sesión de usuario\n"
									+ e.getStackTrace() + "\n-----------------\n");
							if (!e.getClass().toString().contains("lola"))
								Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
							else
								Mensaje(e.getMessage(), false, true);
							return;
						}
					}
				}
			}
			
			/*//Anterior Funcional
			 if (usrLogin != null) {
				if (usrLogin.getStatus() < 0) {
					ab_advertencia = true;
					ls_respuesta = usrLogin.getMessage();
				} else {
					if (!esVacio(usrLogin.getUsername())) {
						ls_respuesta = "Bienvenido " + usrLogin.getNames();
						
						//logger.info("\n--------\n"+usrLogin.getIdentityCMB()+ "\n-----------------\n");
						
						try {
							sesionActiva = usuarioLoginServicio.obtenerSesionActivaPorIdUsuario(usrLogin, usuario,
									proveedor);
							
							try{
					            FacesContext contex = FacesContext.getCurrentInstance();
					            contex.getExternalContext().redirect(PedidoUtil.urlServidor().trim() + "webviews/cine/reserva.jsf?ids=" + sesionActiva.getId() );
							} catch (Exception e) {
								
								supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
										Calendar.getInstance().getTime(), e, true);
								
								Mensaje("No pude redireccionar", false, true);
							}
							
						} catch (Exception e) {
							
							supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
									Calendar.getInstance().getTime(), e, true);
							
							logger.info("\n--------\nError al tratar de guardar la sesión de usuario\n"
									+ e.getStackTrace() + "\n-----------------\n");
							if (!e.getClass().toString().contains("lola"))
								Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
							else
								Mensaje(e.getMessage(), false, true);
							return;
						}
					}
				}
			}
			 */

			Mensaje(ls_respuesta, ab_advertencia, false);

		} catch (Exception e) {
			
			supercinesServicio.crearLogExc("Login-inicializar", fechaInicioProceso, "", 
					Calendar.getInstance().getTime(), e, true);
			
			logger.info("\n--------\nError\n" + e + "\n-----------------\n");
			
			e.printStackTrace();
			
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
	}
	
	public boolean regresarLoguin() {
		ab_loguin = true;
		return true;
	}

	public boolean registrarUsuario() {
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		try {
			ls_respuesta = "Listo";
			if (esVacio(ls_usuarioReg)) {
				ls_respuesta = "Por favor ingresar un Usuario.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (esVacio(ls_identificacion)) {
				ls_respuesta = "Por favor ingresar una Identificación.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (esVacio(ls_email)) {
				ls_respuesta = "Por favor ingresar un Email.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (!validaEmail(ls_email)) {
				ls_respuesta = "Por favor ingresar un Email válido.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (ciudad <= 0) {
				ls_respuesta = "Por favor selecciona una Ciudad.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (esVacio(ls_claveReg)) {
				ls_respuesta = "Por favor ingresar una Clave.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (esVacio(ls_claveConf)) {
				ls_respuesta = "Por favor ingresar la Confiramción de la Clave.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			if (!ls_claveConf.equals(ls_claveReg)) {
				ls_respuesta = "La Confirmación de la Clave no es Correcta.";
				Mensaje(ls_respuesta, true, false);
				return false;
			}

			/*RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlg1').hide();");
*/
			/* Anterior Funcional 
			 String ls_data = Encriptar(CrearJsonParaEncriptarRegistro(), ChilkatServicio.getPemLoguin());
			 

			retornoRegistro usrLogin = SupercinesApi.registrarUsuario(ls_data);
			
			*/
			
			//String ls_data = CrearJsonParaEncriptarRegistroMV(Encriptar(ls_claveReg, ChilkatServicio.getPemLoguin()));
			

			RespuestaRegistro usrLogin = SupercinesApi.registrarUsuario(ls_usuarioReg.trim() + " " + (ls_apellido == null || "".equals(ls_apellido.trim()) ? "" :ls_apellido.trim()) ,
					ls_identificacion, ls_email, ciudad, Encriptar(ls_claveReg, ChilkatServicio.getPemLoguin()));
						
			boolean ab_advertencia = false;
			if (usrLogin != null) {
				if (usrLogin.getResponseType().trim().toUpperCase().equals("ERROR")) {
					ls_respuesta = "No se pudo registrar el Usuario.";//usrLogin.getMessage();
					ab_advertencia = true;
				} else {
					ab_advertencia = !usrLogin.getContent().isResponse();
						
					ls_respuesta = usrLogin.getContent().getMessage();
				}
			} else {
				ls_respuesta = "Lo siento, no puedo procesar la información";
				ab_advertencia = true;
			}

			// logger.info("\n--------\n" + ls_respuesta + "\n-----------------\n");

			Mensaje(ls_respuesta, ab_advertencia, false);
			if (!ab_advertencia)
			{
				Mensaje("!!Bienvenido!!", ab_advertencia, false);
				ab_loguin = true;
			}

		} catch (Exception e) {
			
			supercinesServicio.crearLogExc("Login-registrarUsuario", fechaInicioProceso, "", 
					Calendar.getInstance().getTime(), e, true);
			
			logger.info("\n--------\nError\n" + e + "\n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
		return true;
	}

	public String Encriptar(String as_cadena, String as_keyPem) {
		
		Date fechaInicioProceso = Calendar.getInstance().getTime();
		
		try {

			CkPublicKey piblicKey = new CkPublicKey();

			boolean success = piblicKey.LoadOpenSslPemFile(propiedadesLola.certificados + "\\" + as_keyPem);// "\\login_and_register_public_key.pem");;

			if (success != true) {
				System.out.println(piblicKey.lastErrorText());
				return "";
			}

			CkRsa rsa = new CkRsa();

			success = rsa.UnlockComponent(ChilkatServicio.getIdAcceso());
			if (success != true) {
				System.out.println(rsa.lastErrorText());
				return "";
			}

			String publicKeyXml = piblicKey.getXml();
			success = rsa.ImportPrivateKey(publicKeyXml);
			if (success != true) {
				System.out.println(rsa.lastErrorText());
				return "";
			}

			rsa.put_EncodingMode("hex");

			boolean usePrivateKey = false;
			String encryptedStr = rsa.encryptStringENC(as_cadena, usePrivateKey);

			// System.out.println(encryptedStr);

			return encryptedStr;

		} catch (Exception e) {
			supercinesServicio.crearLogExc("Login-Encriptar", fechaInicioProceso, "", 
					Calendar.getInstance().getTime(), e, true);
			
			logger.info("\n--------ERROR - - NUO \n " + e + " \n-----------------\n");
			if (!e.getClass().toString().contains("lola"))
				Mensaje("Disculpa perdí mi conexión, reingresa a la opción", false, true);
			else
				Mensaje(e.getMessage(), false, true);
		}
		return "";
	}
	
	private String CrearJsonParaEncriptarMV() {
		String ls_claveEncriptada = ChilkatServicio.getMD5(ls_clave);
		return "{ \"Username\": \"" + ls_usuario + "\", \"Password\": \"" + ls_claveEncriptada + "\" }";
	}

	private String CrearJsonParaEncriptar() {
		String ls_claveEncriptada = ChilkatServicio.getMD5(ls_clave);
		return "{ \"email\": \"" + ls_usuario + "\", \"password\": \"" + ls_claveEncriptada + "\" }";
	}

	private String CrearJsonParaEncriptarRegistro() {
		return "{ \"names\": \"" + ls_usuarioReg.trim() + " " + (ls_apellido == null || "".equals(ls_apellido.trim()) ? "" :ls_apellido.trim())  + "\", \"identity\": \"" + ls_identificacion + "\", \"email\":\""
				+ ls_email + "\", \"city\":\"" + ciudad + "\", \"password\":\"" + ls_claveReg + "\" }";
	}
	
	private String CrearJsonParaEncriptarRegistroMV(String claveEncriptada) {
		return "{ \"Name\": \"" + ls_usuarioReg.trim() + " " + (ls_apellido == null || "".equals(ls_apellido.trim()) ? "" :ls_apellido.trim())  + "\", \"Dni\": \"" + ls_identificacion + "\", \"Email\":\""
				+ ls_email + "\", \"Location\":\"" + ciudad + "\", \"Password\":\"" + claveEncriptada + "\" }";
	}

	public void setLs_respuesta(String as_respuesta) {
		this.ls_respuesta = as_respuesta;
	}

	public String getLs_respuesta() {
		return ls_respuesta;
	}

	public boolean isMostrarFormulario() {

		return mostrarFormulario;
	}

	private void validarCamposPorSolicitar() {
		solicitarUsuario = esVacio(ls_usuario);
		solicitarClave = esVacio(ls_clave);
	}

	public boolean isSolicitarUsuario() {
		return solicitarUsuario;
	}

	public boolean isSolicitarClave() {
		return solicitarClave;
	}
	

	public String getLs_apellido() {
		return ls_apellido;
	}

	public void setLs_apellido(String ls_apellido) {
		this.ls_apellido = ls_apellido;
	}

	public void setLs_usuario(String as_usuario) {
		this.ls_usuario = as_usuario;
	}

	public void setLs_claveConf(String as_claveConf) {
		this.ls_claveConf = as_claveConf;
	}

	public String getLs_claveConf() {
		return ls_claveConf;
	}

	public void setLs_clave(String as_clave) {
		this.ls_clave = as_clave;
	}

	public String getLs_usuario() {
		return ls_usuario;
	}

	public String getLs_claveReg() {
		return ls_claveReg;
	}

	public void setLs_usuarioReg(String as_usuario) {
		this.ls_usuarioReg = as_usuario;
	}

	public void setLs_claveReg(String as_clave) {
		this.ls_claveReg = as_clave;
	}

	public String getLs_usuarioReg() {
		return ls_usuarioReg;
	}

	public String getLs_clave() {
		return ls_clave;
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public String getLs_email() {
		return ls_email;
	}

	public void setLs_email(String ls_email) {
		this.ls_email = ls_email;
	}

	public String getLs_identificacion() {
		return ls_identificacion;
	}

	public void setLs_identificacion(String ls_identificacion) {
		this.ls_identificacion = ls_identificacion;
	}

	public Long getCiudad() {
		return ciudad;
	}

	public void setCiudad(Long ciudad) {
		this.ciudad = ciudad;
	}

	public List<Ciudad> getCiudades() {
		return ciudades;
	}

	public void setSolicitarRegistro(Boolean solicitarRegistro) {
		this.solicitarRegistro = solicitarRegistro;
	}

	public Boolean getSolicitarRegistro() {
		return solicitarRegistro;
	}

	public boolean isAb_loguin() {
		return ab_loguin;
	}

	public void setAb_loguin(boolean ab_loguin) {
		this.ab_loguin = ab_loguin;
	}
	
	

}
