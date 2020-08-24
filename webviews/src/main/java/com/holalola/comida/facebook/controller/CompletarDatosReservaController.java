package com.holalola.comida.facebook.controller;

import static com.holalola.util.TextoUtil.esVacio;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.FormaPago;
import com.holalola.comida.pedido.ejb.modelo.Producto;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.ejb.pedidos.servicio.FormaPagoProveedorServicio;
import com.holalola.ejb.reservas.servicio.PedidoReservasServicio;
import com.holalola.general.ejb.dao.UsuarioDao;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.pagos.ReservasProcesos;
import com.holalola.reservas.dao.ReservasDao;
import com.holalola.reservas.dao.ReservasHorariosDao;
import com.holalola.reservas.modelo.FechasReserva;
import com.holalola.reservas.modelo.HorariosRestaurante;
import com.holalola.reservas.modelo.PedidosReservas;
import com.holalola.reservas.modelo.Restaurante;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.ButtonRichMessage;
import com.holalola.webhook.facebook.templates.PostbackButton;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class CompletarDatosReservaController extends GeneralController {
	@EJB
	ReservasDao reservasDao;

	@EJB
	ReservasProcesos reservasProcesos;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	private UsuarioDao usuarioDao;

	@EJB
	private PedidoDao pedidoDao;

	@EJB
	ProveedorDao proveedorDao;

	@EJB
	FormaPagoProveedorServicio formaPagoProveedorServicio;

	@EJB
	PedidoReservasServicio pedidoReservasServicio;

	@EJB
	ReservasHorariosDao reservasHorariosDao;

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

	private boolean conFbExtensions;
	public Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	public String idUsuario = params.get("iu");
	public String idCodigoRestaurante = params.get("ir");
	public String idReservasCodigoRestaurante = params.get("irr");

	private boolean mostrarFormulario;

	public String horaString;
	public String fechaString;
	public List<Producto> fechasLista = new ArrayList<Producto>();
	public List<FechasReserva> lfechasReservas = new ArrayList<FechasReserva>();

	public List<FechasReserva> listafechasReserva = new ArrayList<>();

	public List<HorariosRestaurante> listaHorariosRestaurante = new ArrayList<HorariosRestaurante>();

	public List<String> listaHorariosRestaurantesTodos = new ArrayList<String>();

	public String observaciones;

	public String numeroPersonas;

	// Alex 10/10/10/2018

	public String nombresReserva;

	public String apellidosReserva;

	public String emailReserva;

	public String telefonoReserva;

	// Fin

	public Usuario usuario;

	public PedidosReservas pedidosReservas;

	public Restaurante restaurante;

	public FormaPago formaPago;

	public List<PedidosReservas> listaPedidosReservas = new ArrayList<PedidosReservas>();

	private Proveedor proveedor;

	private String tipoPago = "";

	@PostConstruct
	public void inicializar() {

		proveedor = new Proveedor();

		usuario = usuarioDao.obtenerPorId(new Long(idUsuario));

		nombresReserva = usuario.getNombres() != null ? usuario.getNombres().trim() : "";
		apellidosReserva = usuario.getApellidos() != null ? usuario.getApellidos().trim() : "";
		emailReserva = usuario.getEmail() != null ? usuario.getEmail().trim() : "";
		telefonoReserva = usuario.getCelularPayphone() != null ? usuario.getCelularPayphone().trim() : "";

		conFbExtensions = true;
		mostrarFormulario = true;
		cargarFechas();
		horaString = null;
		proveedor = proveedorDao.obtenerPorId(reservasDao.obtenerRestaurantePorId(new Long(idCodigoRestaurante))
				.getCiudades().getProveedor().getId());
		tipoPago = "PLACETOPAY";

	}

	public void cargarFechas() {

		List<Date> listafechas = new ArrayList<>();
		listafechas = getListaEntreFechas();

		String[] dias = { "Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado" };
		String[] meses = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };

		for (Iterator<Date> it = listafechas.iterator(); it.hasNext();) {

			FechasReserva fechasReserva = new FechasReserva();
			Calendar fecha = Calendar.getInstance();
			Date date = it.next();
			fecha.setTime(date);
			int anio = fecha.get(Calendar.YEAR);
			int mes = fecha.get(Calendar.MONTH) + 1;
			int dia = fecha.get(Calendar.DAY_OF_MONTH);
			int numeroDia = fecha.get(Calendar.DAY_OF_WEEK);

			fechasReserva.setNumeroDia(String.valueOf(dia));
			fechasReserva.setNombreDia(dias[numeroDia - 1]);
			fechasReserva.setNumeroMes(String.valueOf(mes));
			fechasReserva.setNombreMes(meses[mes - 1]);
			fechasReserva.setNumeroAnio(String.valueOf(anio));
			listafechasReserva.add(fechasReserva);

		}

		for (Iterator<FechasReserva> it = listafechasReserva.iterator(); it.hasNext();) {
			FechasReserva date1 = it.next();
		}

	}

   public List<Date> getListaEntreFechas() {
    	
    	int[] dias={6,0,1,2,3,4,5};
    	
    	Date fechaInicio=new Date();
    	Date fechaFin=new Date();
    	
        Calendar c1 = Calendar.getInstance();
        c1.setTime(fechaInicio);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(fechaFin);
        c2.add(Calendar.DAY_OF_MONTH, 25);
        List<Date> listaFechas = new ArrayList<Date>();
        
        
        
        while (!c1.after(c2)) {
        	
        	if (reservasHorariosDao.obtenerPendienteUsuarioReservas(dias[(c1.get(Calendar.DAY_OF_WEEK))-1], new Long(idCodigoRestaurante))!=null && reservasHorariosDao.obtenerPendienteUsuarioReservas(dias[(c1.get(Calendar.DAY_OF_WEEK))-1], new Long(idCodigoRestaurante)).size()>0) {
         		
        		listaFechas.add(c1.getTime());
        	}
            c1.add(Calendar.DAY_OF_MONTH, 1);
            
        }
        return listaFechas;
    }

	public void obtenerHorariosRestaurante() {

		int minutoInicioReserva=0;
        int horaInicioReserva=0;
        
        int diferenciaHoras=0;
        int diferenciaMinutos=0;
        int diferenciaHorasEnMinutos=0;
        int valorHoras=0;
        int valorMinutos=0;
        int diferenciaTotal=0;
        
        String fechaVerHorarios=fechaString;
        String[] parts = fechaVerHorarios.split("/");
		String part1 = parts[0];
		String part2 = parts[1];
		String part3 = parts[2];
		String part4 = parts[3];
		
        listaHorariosRestaurante= new ArrayList<>();
        listaHorariosRestaurantesTodos= new ArrayList<>();
        
        
    	listaHorariosRestaurante= (reservasDao.obtenerHorariosRestaurante(new Long(idCodigoRestaurante),part4).size()>0)?reservasDao.obtenerHorariosRestaurante(new Long(idCodigoRestaurante),part4):null;
    	
    	
    	if (listaHorariosRestaurante!=null) {
    	for (int s=0; s<listaHorariosRestaurante.size();s++) {
    	if (listaHorariosRestaurante.get(s).getHoraFin()>listaHorariosRestaurante.get(s).getHoraInicio()) {
    	diferenciaHoras=listaHorariosRestaurante.get(s).getHoraFin()-listaHorariosRestaurante.get(s).getHoraInicio();
        diferenciaMinutos=listaHorariosRestaurante.get(s).getMinutoFin()-listaHorariosRestaurante.get(s).getMinutoInicio();
        
        diferenciaHorasEnMinutos=diferenciaHoras*60;
        valorHoras=diferenciaHorasEnMinutos/15;
        valorMinutos=diferenciaMinutos/15;
        diferenciaTotal=valorHoras+valorMinutos;
        
        minutoInicioReserva=listaHorariosRestaurante.get(s).getMinutoInicio();
        horaInicioReserva=listaHorariosRestaurante.get(s).getHoraInicio();
        
        for (int i=0; i<diferenciaTotal ; i++) {
        	minutoInicioReserva=minutoInicioReserva+15;
        	if (minutoInicioReserva == 60) {
        		minutoInicioReserva = 0;
        		horaInicioReserva=horaInicioReserva+1;
        	}
        	
        	
        	
        	listaHorariosRestaurantesTodos.add(((String.valueOf(horaInicioReserva)).trim().length() == 1 ? "0"+String.valueOf(horaInicioReserva) : String.valueOf(horaInicioReserva))+":"+((String.valueOf(minutoInicioReserva)).trim().length() == 1 ? "0"+String.valueOf(minutoInicioReserva) : String.valueOf(minutoInicioReserva)));
        }
        } else if (listaHorariosRestaurante.get(s).getHoraFin()<listaHorariosRestaurante.get(s).getHoraInicio()) {
        	diferenciaHoras=(listaHorariosRestaurante.get(s).getHoraFin()+24)-listaHorariosRestaurante.get(s).getHoraInicio();
            diferenciaMinutos=listaHorariosRestaurante.get(s).getMinutoFin()-listaHorariosRestaurante.get(s).getMinutoInicio();
            
            diferenciaHorasEnMinutos=diferenciaHoras*60;
            valorHoras=diferenciaHorasEnMinutos/15;
            valorMinutos=diferenciaMinutos/15;
            diferenciaTotal=valorHoras+valorMinutos;
            
            minutoInicioReserva=listaHorariosRestaurante.get(s).getMinutoInicio();
            horaInicioReserva=listaHorariosRestaurante.get(s).getHoraInicio();
            
            for (int i=0; i<diferenciaTotal ; i++) {
            	minutoInicioReserva=minutoInicioReserva+15;
            	if (minutoInicioReserva == 60) {
            		minutoInicioReserva = 0;
            		horaInicioReserva=horaInicioReserva+1;
            	}
            	if (horaInicioReserva==24) {
            		horaInicioReserva=0;
            	}
            	listaHorariosRestaurantesTodos.add(((String.valueOf(horaInicioReserva)).trim().length() == 1 ? "0"+String.valueOf(horaInicioReserva) : String.valueOf(horaInicioReserva))+":"+((String.valueOf(minutoInicioReserva)).trim().length() == 1 ? "0"+String.valueOf(minutoInicioReserva) : String.valueOf(minutoInicioReserva)));
            }
        }  
    	}
		}
	}

	@SuppressWarnings("deprecation")
	public void completarDatosReserva() {

		pedidosReservas=new PedidosReservas();
    	restaurante= new Restaurante();
    	formaPago= new FormaPago();
    	
    	Date fechaInicioProceso = Calendar.getInstance().getTime();
    	
    	
    	String fechaVerHorarios=fechaString;
        String[] parts = fechaVerHorarios.split("/");
		String part1 = parts[0]; // 123
		String part2 = parts[1];
		String part3 = parts[2]; // 123
		String part4 = parts[3];
		String fechaCompletarDatos=part1+"/"+part2+"/"+part3;
		
		//String fechaReservaString= part3+"-"+(part1.trim().length() == 1 ? "0"+part1.trim() : part1.trim() )+"-"+(part2.trim().length() == 1 ? "0"+part2.trim() : part2.trim() );
		String fechaReservaString= (part1.trim().length() == 1 ? "0"+part1.trim() : part1.trim() )+"-"+(part2.trim().length() == 1 ? "0"+part2.trim() : part2.trim() )+"-"+part3;
		
	     SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd-MM-yyyy");
	     Date fechaReserva = null;
	     try {
	         fechaReserva = formatoDelTexto.parse(fechaReservaString);
	     } catch (Exception ex) {

	         ex.printStackTrace();

	     }
	     
	     
	     SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	     long ms;
	     Time horaReserva= null;
	     try {
			ms = sdf.parse(horaString).getTime();
			horaReserva = new Time(ms);
	     } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
	     }
	    
    	int valorRetorno=reservasProcesos.consultaDisponibilidadRestaurante(new Integer(idReservasCodigoRestaurante), fechaCompletarDatos, horaString, new Integer(numeroPersonas));
    	
    	restaurante.setId(Long.parseLong((idCodigoRestaurante)));
    	BigDecimal valorAPagarPorPersona= reservasDao.obtenerRestaurantePorId(Long.parseLong((idCodigoRestaurante))).getValorPersona();
    	BigDecimal valorPagarTotal = valorAPagarPorPersona.multiply(new BigDecimal(numeroPersonas));
 
    	
    	pedidosReservas.setUsuario(usuario);
    	pedidosReservas.setTotal(valorPagarTotal);
    	pedidosReservas.setFecha(fechaInicioProceso);
    	pedidosReservas.setNota(observaciones);
    	pedidosReservas.setFormaPago(null);
    	pedidosReservas.setEnproceso(false);
    	pedidosReservas.setRestaurante(restaurante);
    	pedidosReservas.setAutorizacionpago("");
    	pedidosReservas.setNumeroPersonas(Integer.parseInt(numeroPersonas));
    	pedidosReservas.setFechaReserva(fechaReserva);
    	pedidosReservas.setHoraReserva(horaString);
		pedidosReservas.setImpuestoAplicado(pedidoDao.obtenerImpuesto());
    	
 
    	pedidosReservas.setNombresReserva(nombresReserva);
    	pedidosReservas.setApellidosReserva(apellidosReserva);
    	pedidosReservas.setEmailReserva(emailReserva);
    	pedidosReservas.setTelefonoReserva(telefonoReserva);
    	 
    	
    	
    	if (valorRetorno>0) {
			reservasDao.insertar(pedidosReservas);
    		mostrarFormulario = false;
    		solicitarFormaPago(pedidosReservas);
		} else {
			Mensaje("La hora o la fecha de la reserva no estan disponibles, por favor seleccione otras", true, false);
			
		}

	}

	private void solicitarFormaPago(PedidosReservas ae_pedidosReservas) {

		restaurante = new Restaurante();
		restaurante.setId(Long.parseLong((idCodigoRestaurante)));

		pedidosReservas = reservasDao.damePedidosReservasPorId(ae_pedidosReservas.getId());

		List<FormaPago> buttonsFinal = new ArrayList<FormaPago>();
		if (!tipoPago.trim().equals("")) {
			List<FormaPago> buttonsTemp = formaPagoProveedorServicio
					.obtenerFormasPagoPorProveedor(pedidosReservas.getRestaurante().getCiudades().getProveedor());

			if (buttonsTemp != null && buttonsTemp.size() > 0) {
				float lf_valorMaximo = pedidosReservas.getRestaurante().getCiudades().getProveedor().getCompraMaxima();
				boolean lb_cargarFormaPago = true;
				for (FormaPago formaPago : buttonsTemp) {
					lb_cargarFormaPago = true;
					if (formaPago.isEsConValorMaximo()) {
						lb_cargarFormaPago = pedidosReservas.getTotal().floatValue() <= lf_valorMaximo;
					}

					if (lb_cargarFormaPago && formaPago.getPayload().contains(tipoPago.trim()))
						buttonsFinal.add(formaPago);
				}
			}
		}

		final String speech = String.format("Listo %s, si existe disponibilidad, por favor sigamos con el pago de $ "
				+ pedidosReservas.getTotal() + " para finalizar tu reserva ;) ", usuario.getNombreFacebook());

		// TODO: Ver como se hace cuando no todos los botones son postback
		final List<ButtonGeneral> buttons = buttonsFinal == null || buttonsFinal.size() <= 0
				? formaPagoProveedorServicio
						.obtenerFormasPagoPorProveedor(pedidosReservas.getRestaurante().getCiudades().getProveedor())
						.stream().map(f -> {
							return new PostbackButton(f.getNombre(), f.getPayload() + " " + pedidosReservas.getId()
									+ " " + PropiedadesLola.SERVICIO_RESERVA.trim());
						}).collect(Collectors.toList())
				: buttonsFinal.stream().map(f -> {
					return new PostbackButton(f.getNombre(), f.getPayload() + " " + pedidosReservas.getId() + " "
							+ PropiedadesLola.SERVICIO_RESERVA.trim());
				}).collect(Collectors.toList());

		ConsultarFacebook.postToFacebook(
				new MensajeParaFacebook(usuario.getIdFacebook(), new ButtonRichMessage(speech, buttons)),
				propiedadesLola.facebookToken);

	}

	public void Limpiar() {

		// logger.info("\n--------\n Error - Limpiar");

		/*
		 * RequestContext context = RequestContext.getCurrentInstance();
		 * context.execute(" location.reload();");
		 */

	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public void setConFbExtensions(boolean conFbExtensions) {
		this.conFbExtensions = conFbExtensions;
	}

	public boolean isMostrarFormulario() {
		return mostrarFormulario;
	}

	public void setMostrarFormulario(boolean mostrarFormulario) {
		this.mostrarFormulario = mostrarFormulario;
	}

	public String getHoraString() {
		return horaString;
	}

	public void setHoraString(String horaString) {
		this.horaString = horaString;
	}

	public String getFechaString() {
		return fechaString;
	}

	public void setFechaString(String fechaString) {
		this.fechaString = fechaString;
	}

	public List<FechasReserva> getListafechasReserva() {
		return listafechasReserva;
	}

	public void setListafechasReserva(List<FechasReserva> listafechasReserva) {
		this.listafechasReserva = listafechasReserva;
	}

	public List<HorariosRestaurante> getListaHorariosRestaurante() {
		return listaHorariosRestaurante;
	}

	public void setListaHorariosRestaurante(List<HorariosRestaurante> listaHorariosRestaurante) {
		this.listaHorariosRestaurante = listaHorariosRestaurante;
	}

	public List<String> getListaHorariosRestaurantesTodos() {
		return listaHorariosRestaurantesTodos;
	}

	public void setListaHorariosRestaurantesTodos(List<String> listaHorariosRestaurantesTodos) {
		this.listaHorariosRestaurantesTodos = listaHorariosRestaurantesTodos;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getNumeroPersonas() {
		return numeroPersonas;
	}

	public void setNumeroPersonas(String numeroPersonas) {
		this.numeroPersonas = numeroPersonas;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	// Alex 10/10/2018

	public String getNombresReserva() {
		return nombresReserva;
	}

	public void setNombresReserva(String nombresReserva) {
		this.nombresReserva = nombresReserva;
	}

	public String getApellidosReserva() {
		return apellidosReserva;
	}

	public void setApellidosReserva(String apellidosReserva) {
		this.apellidosReserva = apellidosReserva;
	}

	public String getEmailReserva() {
		return emailReserva;
	}

	public void setEmailReserva(String emailReserva) {
		this.emailReserva = emailReserva;
	}

	public String getTelefonoReserva() {
		return telefonoReserva;
	}

	public void setTelefonoReserva(String telefonoReserva) {
		this.telefonoReserva = telefonoReserva;
	}

	// Fin

}
