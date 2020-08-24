package com.holalola.chat.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.cine.ejb.servicio.UsuarioOperadorServicio;
import com.holalola.cine.ejb.servicio.UsuarioServicio;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.general.ejb.modelo.UsuarioOperador;
import com.holalola.util.PedidoUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.dao.ServicioDao;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class PedidosChatController extends GeneralController {

	@ManagedProperty("#{usuarioChatController}")
	UsuarioChatController usuarioChatController;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private UsuarioServicio usuarioServicio;
	
	@EJB
	private RespuestaDao respuestaDao;
	
	@EJB
	private ServicioDao servicioDao;

	@EJB
	private UsuarioOperadorServicio usuarioOperadorServicio;

	private Usuario usuarioSeleccionado;
	private List<Usuario> usuariosChat;
	private List<UsuarioOperador> listaChatUsuario;
	private Date ultimaActualizacion;
	private String texto;
	final private String patSonido = System.getProperty("lola.alerta");
	
	public String getSonido() {
		return patSonido;
	}

	@PostConstruct
	public void inicializar() {
		
		usuarioSeleccionado = null;
		actualizarChat(false);
		//fn_validaSesion();
		ultimaActualizacion = new Date();

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
	
	 public void buttonAction(ActionEvent actionEvent) {
			if(usuarioSeleccionado != null && texto != null && texto.trim().length() > 0 )
			{
				if(!usuarioSeleccionado.getChatActivo())
				{
					System.out.println("Enviado por : "+usuarioSeleccionado.getNombreFacebook());
					
					UsuarioOperador usuarioOperador = new UsuarioOperador(usuarioSeleccionado, usuarioChatController.getOperadorProveedor(), new Date(), texto.trim(), true);
					
					usuarioOperadorServicio.insertar(usuarioOperador);
					
					final TextMessage mensaje = new TextMessage(texto.trim());
					ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuarioSeleccionado.getIdFacebook(), mensaje),
							propiedadesLola.facebookToken);
					
					actualizarChat(false);
					texto = "";
				}
				else
				{
					Mensaje("El chat para el usuario " + usuarioSeleccionado.getNombreFacebook() + " esta activo solo para Lola." , false, false);
				}
			}
	   }
	 
	 public void regresaALolaAction(ActionEvent actionEvent) {
			if(usuarioSeleccionado != null)
			{
				if(!usuarioSeleccionado.getChatActivo())
				{
					usuarioSeleccionado.setChatActivo(true);
					usuarioSeleccionado.setNoEntiendo(0);
					usuarioServicio.modificar(usuarioSeleccionado);
					
					final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.MOSTRAR_SERVICIOS);
					
					final TextMessage mensajeRespuesta = new TextMessage(servicios.getTexto());

					 
						final List<QuickReplyGeneral> quickReplies = servicioDao.obtenerActivos().stream().map(s -> {
							return new TextQuickReply(s.getNombre(), s.getPayload());
						}).collect(Collectors.toList());

						mensajeRespuesta.setQuick_replies(quickReplies);
					 
					
					 
					ConsultarFacebook.postToFacebook(new MensajeParaFacebook(usuarioSeleccionado.getIdFacebook(), mensajeRespuesta),
							propiedadesLola.facebookToken);
					
					actualizarChat(false);
					texto = "";
					
					Mensaje("Se reactiva Lola para el usuario " + usuarioSeleccionado.getNombreFacebook() , false, false);
					usuarioSeleccionado = null;
					obtenerChatUsuario(false);
				}
				else
				{
					usuarioSeleccionado = null;
					obtenerChatUsuario(false);
					Mensaje("El chat para el usuario " + usuarioSeleccionado.getNombreFacebook() + " esta activo solo para Lola." , false, false);
				}
			}
	    }

	void fn_validaSesion() {
		try {
			usuarioChatController.isInterno();
		} catch (Exception err) {
			try {
				FacesContext contex = FacesContext.getCurrentInstance();
				contex.getExternalContext()
						.redirect(PedidoUtil.urlServidor().trim() + "facebook/chat/ingreso.jsf");
			} catch (Exception e) {

			}
		}
	}
	
	public void obtenerChatUsuario() {
		obtenerChatUsuario(true);
	}

	public void obtenerChatUsuario(boolean ab_alarma) {
	
		if(usuarioSeleccionado != null)
		{
			int numeroPedidosPendientes = listaChatUsuario != null ? listaChatUsuario.size() : 0;
			listaChatUsuario = usuarioOperadorServicio.obtenerChat(usuarioChatController.getOperadorProveedor(), usuarioSeleccionado);
			
			if ((listaChatUsuario != null ? listaChatUsuario.size() : 0) > numeroPedidosPendientes && ab_alarma) {
				try {

					RequestContext.getCurrentInstance().execute("playVid();");
					RequestContext.getCurrentInstance().execute("playVid2();");
					RequestContext.getCurrentInstance().execute("myAudio.play();");
				} catch (Exception er) {

				}
			}
		}
		else
			listaChatUsuario = new ArrayList<UsuarioOperador>();
		
		
		/*  if (proveedorSeleccionado != null && proveedorSeleccionado.getId() != null) {
		  pedidosPorRevisar = pedidoServicio.obtenerRechazados(proveedorSeleccionado);
		  }
		 */
		fn_validaSesion();
	}

	public void actualizarChat() {
		actualizarChat(true);
	}
	
	public void actualizarChat(boolean ab_alarma) {
		usuariosChat = usuarioOperadorServicio.obtenerPorOperadorProveedor(usuarioChatController.getOperadorProveedor());
		ultimaActualizacion = new Date();
		obtenerChatUsuario(ab_alarma);
		fn_validaSesion();
	}


	public Usuario getUsuario() {
		if (usuariosChat != null && usuariosChat.size() > 0)
			return usuariosChat.get(0);
		else
			return null;
	}

	
	public Date getUltimaActualizacion() {
		return ultimaActualizacion;
	}

	public void setUltimaActualizacion(Date ultimaActualizacion) {
		this.ultimaActualizacion = ultimaActualizacion;
	}

	public List<Usuario> getUsuariosChat() {
		actualizarChat(true);
		return usuariosChat;
	}

	public void setUsuariosChat(List<Usuario> usuariosChat) {
		this.usuariosChat = usuariosChat;
	}

	public Usuario getUsuarioSeleccionado() {
		return usuarioSeleccionado;
	}

	public void setUsuarioSeleccionado(Usuario usuarioSeleccionado) {
		this.usuarioSeleccionado = usuarioSeleccionado;
	}

	public UsuarioChatController getUsuarioChatController() {
		return usuarioChatController;
	}

	public void setUsuarioChatController(UsuarioChatController usuarioChatController) {
		this.usuarioChatController = usuarioChatController;
	}

	public List<UsuarioOperador> getListaChatUsuario() {
		return listaChatUsuario;
	}
  

	public void setListaChatUsuario(List<UsuarioOperador> listaChatUsuario) {
		this.listaChatUsuario = listaChatUsuario;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	

}
