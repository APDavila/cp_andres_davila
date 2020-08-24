package com.holalola.comida.facebook.controller;

import static com.holalola.util.TextoUtil.esVacio;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.chat.controller.Utilidades;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoTarjetaDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.FormaPago;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.PedidoTarjeta;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.ejb.general.servicio.UbicacionUsuarioServicio;
import com.holalola.ejb.pedidos.servicio.FormaPagoProveedorServicio;
import com.holalola.ejb.pedidos.servicio.PedidoServicio;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.reservas.dao.ReservasDao;
import com.holalola.reservas.modelo.PedidosReservas;
import com.holalola.supercines.client.vo.usuarioLogin;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.TextMessage;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class CompletarDireccionController extends GeneralController {

	@EJB
	FormaPagoProveedorServicio formaPagoProveedorServicio;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	PedidoServicio pedidoServicio;
	
	@EJB
	PedidoDao pedidoDao;	
	
	@EJB
	ReservasDao reservasDao;
	
	@EJB
	PedidoTarjetaDao pedidoTarjetaDao;
	
	@EJB
	ProveedorDao proveedorDao;

	@EJB
	private PropiedadesLola propiedadesLola;

	private UbicacionUsuario ubicacionUsuario;
	private boolean mostrarFormulario;

	private boolean solicitarNombres;
	private boolean solicitarCiudad;
	private boolean solicitarApellidos;
	private boolean solicitarFechaNacimiento;
	private boolean solicitarTipoIdentificacion;
	private boolean solicitarNumeroIdentificacion;
	private boolean solicitarEmail;
	private boolean conFbExtensions;
	private List<String> listaAliasUbicacion = new ArrayList<String>();
	private String alias;
	private String tipoPago = "";
	private Proveedor proveedor;
	private boolean caldisabled;
	Long lg_idPedido = (long) 0;
	
	private String fechaNacimiento;
	
	//Alex
	private UIComponent btnAceptar;

	@PostConstruct
	public void inicializar() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
				.getRequestParameterMap();
		String ubicacionUsuarioId = params.get("dir");
		String token = params.get("token");
		tipoPago = params.get("tar");
		String ext = params.get("ext");
		String idPedido = params.get("idPed");
		
		try
		{
			lg_idPedido = Long.parseLong(idPedido);
		}
		catch(Exception err)
		{
			lg_idPedido = (long) 0;
		}
		
		try
		{
			String proveedorId = params.get("id_prov");
			proveedor = proveedorDao.obtenerPorId(Long.parseLong(proveedorId));
		}
		catch(Exception err)
		{
			proveedor = null;
		}
		
		conFbExtensions = true;
		
		if(esVacio(tipoPago))
			tipoPago = "";

		if ((esVacio(ubicacionUsuarioId) || esVacio(token)) && tipoPago.trim().equals("")) {
			mostrarFormulario = false;
			return;
		}

		ubicacionUsuario = ubicacionUsuarioServicio.obtenerPorId(Long.parseLong(ubicacionUsuarioId));
	
		alias = ubicacionUsuario != null && ubicacionUsuario.getAlias() != null ? ubicacionUsuario.getAlias().trim()
				: "";
		
		try
		{
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			
			fechaNacimiento = ubicacionUsuario != null && ubicacionUsuario.getUsuario() != null && ubicacionUsuario.getUsuario().getFechaNacimiento() != null ?   df.format(ubicacionUsuario.getUsuario().getFechaNacimiento())
							: "";
		}catch(Exception err)
		{
			
		}

		if(tipoPago.trim().equals(""))
		{
			mostrarFormulario = ubicacionUsuario != null && !ubicacionUsuario.isConfirmadoUsuario()
					&& token.equals(ubicacionUsuario.getToken());
		}
		else
			mostrarFormulario = true;
		
		validarCamposPorSolicitar();
	}

	
	

	public UIComponent getBtnAceptar() {
		return btnAceptar;
	}



	public void setBtnAceptar(UIComponent btnAceptar) {
		this.btnAceptar = btnAceptar;
	}



	@SuppressWarnings("deprecation")
	public void completarUbicacion() {
		
		final Usuario usuario = ubicacionUsuario.getUsuario();
		
		try
		{
			ubicacionUsuario.setAlias(alias != null ? alias.trim() : "Nulo");
			
			try
			{

				if(fechaNacimiento != null && fechaNacimiento.trim().length() > 0)
				{
				 
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String dateInString = fechaNacimiento.trim();
					Date fechaNacAux = sdf.parse(dateInString);
									 
					
					ubicacionUsuario.getUsuario().setFechaNacimiento(fechaNacAux);
				}
			}
			catch(Exception err)
			{
				
			}
			
			String ls_telefono = ubicacionUsuario.getUsuario().getCelularPayphone();
			
			if(ls_telefono == null || ls_telefono.trim().length() <= 0 && ubicacionUsuario.getCelular().trim().length() > 0)
			{
				ubicacionUsuario.getUsuario().setCelularPayphone(ubicacionUsuario.getCelular().trim());
			}
						
			try {
				if (ubicacionUsuario.getUsuario().getTipoIdentificacion().trim().equals("R")) {
					usuario.setApellidos(" ");		
				}
			} catch (Exception e) {
		 
			}
			
			ubicacionUsuarioServicio.completarUbicacionUsuario(ubicacionUsuario);
			mostrarFormulario = false;
	
			solicitarFormaPago();
		}
		catch(Exception err)
		{
			//System.out.println("-----------------------------------------------"+err);
		}
				
	}
	
	//Alex inicio
	  // with getter and setter

	   public void checkSelectedVal(ValueChangeEvent event){

		   try
		   {
			   final Usuario usuario = ubicacionUsuario.getUsuario();
			   
			   solicitarApellidos = esVacio(usuario.getApellidos());
		   }
		   catch(Exception er)
		   {
			   
		   }
	      String selectedVal=event.getNewValue().toString();
	      if("R".equalsIgnoreCase(selectedVal)){
	         caldisabled=true;
	         solicitarApellidos = false;
	      } else if("R".equalsIgnoreCase(selectedVal) || "C".equalsIgnoreCase(selectedVal) || "P".equalsIgnoreCase(selectedVal) || "N".equalsIgnoreCase(selectedVal)){
	        caldisabled=false;
	      }
	}
	   
	public boolean isCaldisabled() {
		return caldisabled;
	}
	public void setCaldisabled(boolean caldisabled) {
		this.caldisabled = caldisabled;
	}
	//Alex fin

	private void solicitarFormaPago() {
		
		final Usuario usuario = ubicacionUsuario.getUsuario();
		
		if(tipoPago.trim().equals("Reservas"))
		{
			try
			{
				PedidosReservas pedidosReservas = reservasDao.damePedidosReservasPorId(lg_idPedido);
				
				List<FormaPago> buttonsFinal = new ArrayList<FormaPago>();
				if(!tipoPago.trim().equals(""))
				{
					List<FormaPago> buttonsTemp = formaPagoProveedorServicio
							.obtenerFormasPagoPorProveedor(pedidosReservas.getRestaurante().getCiudades().getProveedor());
					
					if(buttonsTemp != null && buttonsTemp.size() > 0)
					{
						float lf_valorMaximo = pedidosReservas.getRestaurante().getCiudades().getProveedor().getCompraMaxima();
						boolean lb_cargarFormaPago = true;
						for (FormaPago formaPago : buttonsTemp) {
							lb_cargarFormaPago = true;
							if(formaPago.isEsConValorMaximo())
							{
								lb_cargarFormaPago = pedidosReservas.getTotal().floatValue() <= lf_valorMaximo;
							}
								
							
							if(lb_cargarFormaPago && formaPago.getPayload().contains(tipoPago.trim()))
								buttonsFinal.add(formaPago);
						}
					}
				}
				
				final String speech = String.format("Listo %s, por favor sigamos con el pago de $ "+pedidosReservas.getTotal()+" para finalizar tu reserva ;) ", usuario.getNombreFacebook());
				
				// TODO: Ver como se hace cuando no todos los botones son postback
				final List<ButtonGeneral> buttons = buttonsFinal == null || buttonsFinal.size() <= 0 ? formaPagoProveedorServicio
						.obtenerFormasPagoPorProveedor(pedidosReservas.getRestaurante().getCiudades().getProveedor()).stream().map(f -> {
							return new PostbackButton(f.getNombre(), f.getPayload() + " " + pedidosReservas.getId()+ " "+PropiedadesLola.SERVICIO_RESERVA.trim());
						}).collect(Collectors.toList()) 
					:
						buttonsFinal.stream().map(f -> {
							return new PostbackButton(f.getNombre(), f.getPayload() + " " + pedidosReservas.getId()+ " "+PropiedadesLola.SERVICIO_RESERVA.trim());
						}).collect(Collectors.toList());	
		
				ConsultarFacebook.postToFacebook(
						new MensajeParaFacebook(usuario.getIdFacebook(), new ButtonRichMessage(speech, buttons)),
						propiedadesLola.facebookToken);
			}
			catch(Exception err)
			{
				err.printStackTrace();
			}
			
			return;
		}
		
		
		if(!tipoPago.trim().equals("AFNA") && !tipoPago.trim().equals("Reservas"))
		{
			
			final Pedido pedido = proveedor != null ? pedidoServicio.obtenerPendienteUsuario(usuario, proveedor) : pedidoServicio.obtenerUnPendienteUsuario(usuario) ;
			
			List<FormaPago> buttonsFinal = new ArrayList<FormaPago>();
			float lf_valorMaximo = pedido.getProveedor().getCompraMaxima();
			String ls_pagosNoValidos = "";
			
			if(!tipoPago.trim().equals(""))
			{
				List<FormaPago> buttonsTemp = formaPagoProveedorServicio
						.obtenerFormasPagoPorProveedor(pedido.getProveedor());
				
				if(buttonsTemp != null && buttonsTemp.size() > 0)
				{
					
					boolean lb_cargarFormaPago = true;
					 
					for (FormaPago formaPago : buttonsTemp) {
						lb_cargarFormaPago = true;
						if(formaPago.isEsConValorMaximo())
						{
							lb_cargarFormaPago = pedido.getTotal().floatValue() <= lf_valorMaximo;
						}
							
						
						if(lb_cargarFormaPago && formaPago.getPayload().contains(tipoPago.trim()))
							buttonsFinal.add(formaPago);
						else
						{
							if(ls_pagosNoValidos.trim().length() > 0)
								ls_pagosNoValidos = ls_pagosNoValidos.trim() + ", ";
							
							ls_pagosNoValidos = ls_pagosNoValidos.trim() + formaPago.getNombre().trim();
						}
						
					}
				}
			}
			else
			{
				List<FormaPago> buttonsTemp = formaPagoProveedorServicio
						.obtenerFormasPagoPorProveedor(pedido.getProveedor());
				
				if(buttonsTemp != null && buttonsTemp.size() > 0)
				{
					
					boolean lb_cargarFormaPago = true;
					 
					for (FormaPago formaPago : buttonsTemp) {
						lb_cargarFormaPago = true;
						if(formaPago.isEsConValorMaximo())
						{
							lb_cargarFormaPago = pedido.getTotal().floatValue() <= lf_valorMaximo;
						}
						
						if(lb_cargarFormaPago)
							buttonsFinal.add(formaPago);
						else
						{
							if(ls_pagosNoValidos.trim().length() > 0)
								ls_pagosNoValidos = ls_pagosNoValidos.trim() + ", ";
							
							ls_pagosNoValidos = ls_pagosNoValidos.trim() + formaPago.getNombre().trim();
						}
						
					}
				}
			}
			
			
				if(pedido.getTiempoEntrega() != Pedido.PENDIENTE)
				{
					String speech = String.format(
							"%s, este pedido ya fue procesado, puedo ayudarte en algo mas?",
							usuario.getNombreFacebook());
					
					final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Servicios", "MOSTRAR_SERVICIOS"));
					
					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
							propiedadesLola.facebookToken);
					return;
				}
				
				List<PedidoTarjeta> listaPedidoTarjeta = pedidoTarjetaDao.obtenerPorPedido(pedido);
				PedidoTarjeta pedidoTarjeta = listaPedidoTarjeta != null && listaPedidoTarjeta.size() > 0
						? listaPedidoTarjeta.get(0)
						: null;
				
		       if (pedidoTarjeta != null && pedidoTarjeta.getConfirmado() && !pedidoTarjeta.getReversado() && pedido.getAutorizacionPago() != null && pedido.getAutorizacionPago().trim().length() > 0 ) {
					
		    	   String speech = String.format(
							"%s, este pedido ya fue pagado, por favor dame unos minutos para validarlo con " + pedido.getProveedor().getNombre(),
							usuario.getNombreFacebook());
					
					final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Servicios", "MOSTRAR_SERVICIOS"));
					
					ConsultarFacebook.postToFacebook(
							new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech)),
							propiedadesLola.facebookToken);
					return;
				} 
			
			
			
			if(buttonsFinal == null || buttonsFinal.size() <= 0)
			{
				ConsultarFacebook.postToFacebook(
						new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(
								String.format("%s, el establecimiento no cuenta con formas de pago activas, disculpa :( ...", usuario.getNombreFacebook()))),
						propiedadesLola.facebookToken);
				return;
			}
			
			final String speech =  ls_pagosNoValidos.trim().length() <= 0 ? String.format("%s, se ha validado tu pedido, ahora puedes seguir con tu pago ;) ", usuario.getNombreFacebook()) : 
												String.format("Ups! %s el costo total de tu compra supera los $ %s, que es el valor permitido para pagos en %s, por lo que el pago lo puedes hacer con: ", usuario.getNombreFacebook(), lf_valorMaximo, ls_pagosNoValidos.trim()) ;
			
			// TODO: Ver como se hace cuando no todos los botones son postback
			final List<ButtonGeneral> buttons = buttonsFinal.stream().map(f -> {
						return new PostbackButton(f.getNombre(), f.getPayload() + " " + pedido.getId());
					}).collect(Collectors.toList());	
	
			ConsultarFacebook.postToFacebook(
					new MensajeParaFacebook(usuario.getIdFacebook(), new ButtonRichMessage(speech, buttons)),
					propiedadesLola.facebookToken);
		}
		else
		{
			final String speech = String.format("Listo %s, sigamos :) ", usuario.getNombreFacebook());

				// TODO: Ver como se hace cuando no todos los botones son postback
				final List<ButtonGeneral> buttons = new ArrayList<ButtonGeneral>();
				buttons.add(new PostbackButton(tipoPago, tipoPago));
				
				ConsultarFacebook.postToFacebook(
				new MensajeParaFacebook(usuario.getIdFacebook(), new ButtonRichMessage(speech, buttons)),
				propiedadesLola.facebookToken);
		}
		
	}


	private void validarCamposPorSolicitar() {
		if (ubicacionUsuario != null) {
			final Usuario usuario = ubicacionUsuario.getUsuario();
			solicitarNombres = esVacio(usuario.getNombres());
			solicitarCiudad = (ubicacionUsuario == null || ubicacionUsuario.getCiudad() == null || ubicacionUsuario.getCiudad().trim().length() <= 0 ? true : false);
			solicitarApellidos = esVacio(usuario.getApellidos());
			solicitarTipoIdentificacion = esVacio(usuario.getTipoIdentificacion());
			solicitarNumeroIdentificacion = esVacio(usuario.getNumeroIdentificacion());
			solicitarEmail = esVacio(usuario.getEmail());
			//solicitarFechaNacimiento = (usuario.getFechaNacimiento().toString().isEmpty());
			try
			{
				if(usuario.getTipoIdentificacion().trim().equals("R"))
					solicitarApellidos = false;
			}
			catch(Exception err)
			{
				
			}
		}
	}

	public UbicacionUsuario getUbicacionUsuario() {
		return ubicacionUsuario;
	}

	public boolean isMostrarFormulario() {
		return mostrarFormulario;
	}

	public boolean isSolicitarNombres() {
		return solicitarNombres;
	}

	public boolean isSolicitarApellidos() {
		return solicitarApellidos;
	}

	public boolean isSolicitarTipoIdentificacion() {
		return solicitarTipoIdentificacion;
	}

	public boolean isSolicitarNumeroIdentificacion() {
		return solicitarNumeroIdentificacion;
	}

	public boolean isSolicitarEmail() {
		return solicitarEmail;
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public List<String> getListaAliasUbicacion() {
		return listaAliasUbicacion;
	}

	public void setListaAliasUbicacion(List<String> listaAliasUbicacion) {
		this.listaAliasUbicacion = listaAliasUbicacion;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public boolean isSolicitarFechaNacimiento() {
		return solicitarFechaNacimiento;
	}

	public void setSolicitarFechaNacimiento(boolean solicitarFechaNacimiento) {
		this.solicitarFechaNacimiento = solicitarFechaNacimiento;
	}

	public String getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(String fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public boolean isSolicitarCiudad() {
		return solicitarCiudad;
	}

	public void setSolicitarCiudad(boolean solicitarCiudad) {
		this.solicitarCiudad = solicitarCiudad;
	}
	
	

}
