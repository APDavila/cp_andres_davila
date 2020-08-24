package com.holalola.comida.facebook.controller;

import static com.holalola.util.TextoUtil.esVacio;
import static com.holalola.webhook.PayloadConstantes.PAYLOAD_CONFIRMAR_PEDIDO;

import java.util.Arrays;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.Utilidades;
import com.holalola.comida.pedido.ejb.modelo.DatoFacturaPedido;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.ejb.pedidos.servicio.DatoFacturaPedidoServicio;
import com.holalola.ejb.pedidos.servicio.PedidoServicio;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.PostbackButton;

@ViewScoped
@ManagedBean
@SuppressWarnings("serial")
public class DatosFacturaController extends GeneralController {

	@EJB
	PedidoServicio pedidoServicio;

	@EJB
	UsuarioDao usuarioDao;
	
	@EJB
	DatoFacturaPedidoServicio datoFacturaPedidoServicio;

	@EJB
	private PropiedadesLola propiedadesLola;

	private DatoFacturaPedido datoFactura;
	private boolean mostrarFormulario;
	private boolean conFbExtensions;
	private boolean predeterminadoFactura;
	private String nombres;
	private String apellidos;
	private boolean caldisabled;
	private Pedido pedido;

	@PostConstruct
	public void inicializar() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String pedidoId = params.get("pedido");
		String token = params.get("token");
		String ext = params.get("ext");
		// conFbExtensions = !esVacio(ext) && Integer.parseInt(ext) == 1;
		conFbExtensions = true;

		if (esVacio(token) || esVacio(pedidoId)) {
			mostrarFormulario = false;
			return;
		}
		
		caldisabled=true;
		
		pedido = pedidoServicio.obtenerPedidoComidaPorId(Long.parseLong(pedidoId));
		mostrarFormulario = pedido != null && pedido.getDatoFacturaPedido() == null && token.equals(pedido.getToken());

		datoFactura = DatoFacturaPedido.getInstanciaVaciaSinPedido();
		datoFactura.setPedido(pedido);
	}
	//Alex inicio
	  // with getter and setter

	   public void checkSelectedVal(ValueChangeEvent event){

	      String selectedVal=event.getNewValue().toString();
	      if("R".equalsIgnoreCase(selectedVal)){
	         caldisabled=false;
	      } else if("C".equalsIgnoreCase(selectedVal) || "P".equalsIgnoreCase(selectedVal) || "N".equalsIgnoreCase(selectedVal)){
	        caldisabled=true;
	      }
	}
	   
	public boolean isCaldisabled() {
		return caldisabled;
	}
	public void setCaldisabled(boolean caldisabled) {
		this.caldisabled = caldisabled;
	}
	//Alex fin
	public void guardar() {

		/*if (datoFactura.getTipoIdentificacion() == null || datoFactura.getTipoIdentificacion().trim().length() <= 0) {
			addWarningMessage("El tipo de identificación es requerido", "");
			return;
		}

		if (datoFactura.getNumeroIdentificacion() == null
				|| datoFactura.getNumeroIdentificacion().trim().length() <= 0) {
			addWarningMessage("La identificación es requerida", "");
			return;
		}
		
	
		
		if (datoFactura.getTipoIdentificacion().trim().equals("P")) {
			if (apellidos == null
					|| apellidos.trim().length() <= 0) {
				addErrorMessage("Los apellidos son requeridos", "");
				return;
			}
		}
		
		if (datoFactura.getTipoIdentificacion().trim().equals("N")) {
			if (apellidos == null
					|| apellidos.trim().length() <= 0) {
				addErrorMessage("Los apellidos son requeridos", "");
				return;
			}
		}
		
		if (datoFactura.getTipoIdentificacion().trim().equals("C")) {
			if (apellidos == null
					|| apellidos.trim().length() <= 0) {
				addErrorMessage("Los apellidos son requeridos", "");
				return;
			}
		}
		
		try {
			if (datoFactura.getTipoIdentificacion().trim().equals("C")) {
				if (!Utilidades.validaCedula(datoFactura.getNumeroIdentificacion())) {
					addErrorMessage("Cédula no válida", "");
					return;
				}
				else if (apellidos == null
						|| apellidos.trim().length() <= 0) {
					addErrorMessage("Los apellidos son requeridos", "");
					return;
				}
			}
		} catch (Exception e) {
			addErrorMessage("Cédula no válida", "");
			return;
		}
		*/
		try {
			if (datoFactura.getTipoIdentificacion().trim().equals("R")) {
				apellidos= " ";
		
			}
		} catch (Exception e) {
			addErrorMessage("RUC no válido", "");
			return;
		}

		datoFactura.setNombre(nombres.trim());
		
		datoFactura.setApellidos(apellidos.trim());
		
		datoFacturaPedidoServicio.insertar(datoFactura);
		
		if(predeterminadoFactura)
		{
			Usuario usuario = pedido.getUsuario();
			usuario.setNombres(nombres.trim());
			usuario.setApellidos(apellidos.trim());
			usuario.setTipoIdentificacion(datoFactura.getTipoIdentificacion());
			usuario.setNumeroIdentificacion(datoFactura.getNumeroIdentificacion());
			usuario.setEmail(datoFactura.getEmail());
			usuarioDao.modificar(usuario);
		}
		mostrarFormulario = false;
		addInfoMessage("Los datos para la factura se registraron exitosamente", "");
		solicitarConfirmacionPedido();
	}

	private void solicitarConfirmacionPedido() {
		final Usuario usuario = pedido.getUsuario();
		String speech = "Excelente, para finalizar por favor haz click en 'Enviar pedido'";
		ConsultarFacebook
				.postToFacebook(
						new MensajeParaFacebook(usuario.getIdFacebook(),
								new ButtonRichMessage(speech,
										Arrays.asList(new PostbackButton("Enviar Pedido",
												PAYLOAD_CONFIRMAR_PEDIDO + pedido.getId())))),
						propiedadesLola.facebookToken);
	}

	public DatoFacturaPedido getDatoFactura() {
		return datoFactura;
	}

	public boolean isMostrarFormulario() {
		return mostrarFormulario;
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public boolean isPredeterminadoFactura() {
		return predeterminadoFactura;
	}

	public void setPredeterminadoFactura(boolean predeterminadoFactura) {
		this.predeterminadoFactura = predeterminadoFactura;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	
	
}
