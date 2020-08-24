package com.componente_practico.webhook.acciones;

import static com.componente_practico.util.UrlUtil.armarUrlImagenDia;
import static com.holalola.util.TextoUtil.esVacio;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.ejb.servicio.EventoAgendadoServicio;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.util.FechaUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.ejb.dao.OpcionAgendaDao;
import com.holalola.webhook.ejb.dao.OpcionTiempoAgendaDao;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.modelo.EventoAgendado;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.ConstantesFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.payload.GenericTemplatePayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.Facebook;
import com.holalola.webhook.facebook.templates.FacebookRequestGeneral;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.RichMessageV2;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.model.Result;

@Stateless
public class ManejarAgenda {
	
	static final Logger log = LoggerFactory.getLogger(ManejarAgenda.class);
	
	private static final Locale localeES = new Locale("es");
	private static final DateFormat formatoFechaHoraAgendaJson = new SimpleDateFormat("yyyy-MM-dd HH:mm", localeES);
	private static final DateFormat formatoFechaHoraAgendaJsonISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-'HH:mm");
	private static final DateFormat formatoFechaHoraAgendaJsonNormal = new SimpleDateFormat("yyyy-MM-dd");
	//DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'-'HH:mm");
	private static final DateFormat formatoFechaAgendaJson = new SimpleDateFormat("yyyy-MM-dd", localeES);
	private static final DateFormat formatoHora = new SimpleDateFormat("HH:mm");
	private static final DateFormat formatoFechaMostrar = new SimpleDateFormat("EEEEE, dd 'de' MMMMM", localeES);
	private static final DateFormat formatoFechaHoraMostrar = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'a las' HH:mm", localeES);
	private static final String source = "manejarAgenda";
	private String txtFecha,txtHora,evento;
	private static final String PARAM_FECHA = "date";
	private static final String PARAM_HORA = "time.original";
	private static final String PARAM_EVENTO = "evento.original";
	private static final String PARAM_TIEMPO_RECORDATORIO = "tiempoAvisoRecordatorio.original";
	
	private static final String CONTEXTO_CREAR_RECORDATORIO = "crearRecordatorio";

	@EJB
	private EventoAgendadoServicio eventoAgendadoServicio;
	
	@EJB
	private RespuestaDao respuestaDao;
	
	@EJB
	private OpcionAgendaDao opcionAgendaDao;
	
	@EJB
	private OpcionTiempoAgendaDao opcionTiempoAgendaDao;
	
	@EJB
	private PropiedadesLola propiedadesLola;
	
	public ResponseMessageApiAi mostrarOpcionesAgenda() {
		Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.INICIO_AGENDA);
		TextMessage mensaje = new TextMessage(respuesta.getTexto());
		
		if (respuesta.isMostrarOpciones()) {
			final List<QuickReplyGeneral> quickReplies = opcionAgendaDao.obtenerActivas().stream().map(o -> {
				return new TextQuickReply(o.getNombre(), o.getPayload());
			}).collect(Collectors.toList());
			
			mensaje.setQuick_replies(quickReplies);
		}
		
		return new ResponseMessageApiAi(respuesta.getTexto(), respuesta.getTexto(), new Data(mensaje), null, "inicioAgenda");
	}
	
	public ResponseMessageApiAi obtenerTiemposRecordatorio(Result resultAi, Usuario usuario) {
		
		try {
			String txtFecha = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_FECHA, CONTEXTO_CREAR_RECORDATORIO);
			final String txtHora = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_HORA, CONTEXTO_CREAR_RECORDATORIO);
			final String evento = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_EVENTO, CONTEXTO_CREAR_RECORDATORIO);
			
			final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.TIEMPO_RECORDATORIO);
			
			/*if(!esVacio(txtFecha) && txtFecha.trim().length() == 8)
			{
				txtFecha = (txtFecha.split("//")[0] + txtFecha.split("//")[1]+ (String.valueOf(DateTime.now().getYear()).substring(0, 2)) + txtFecha.split("//")[2]);
				log.error("\n--------- Fecha: "+txtFecha+"\n------------");
			}*/
			
			final EventoAgendado eventoAgendado = armarEeventoAgendado(txtFecha, txtHora, evento, "0", usuario);
			
			final String speech = String.format(respuesta.getTexto(), usuario.getNombreFacebook(), eventoAgendado.getDescripcion(),  formatoFechaHoraMostrar.format(eventoAgendado.getFecha()));
			
			TextMessage mensaje = new TextMessage(speech);
			
			if (respuesta.isMostrarOpciones()) {
				final List<QuickReplyGeneral> quickReplies = opcionTiempoAgendaDao.obtenerActivas().stream().map(o -> {
					return new TextQuickReply(o.getNombre(), o.getPayload());
				}).collect(Collectors.toList());
				
				mensaje.setQuick_replies(quickReplies);
			}
			
			return new ResponseMessageApiAi(respuesta.getTexto(), respuesta.getTexto(), new Data(mensaje), null, "tiemposRecordatorio");
		
		} catch (Exception e) {
			final String speech = "Ay que pena, creo que me he quedado dormida y no pude apuntarlo.\nInt\u00e9ntalo nuevamte por favor :(";
			log.error("No se pudo agendar evento.", e);
			return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, "tiemposRecordatorio");
		}
	}
	
	
	public ResponseMessageApiAi agendarEvento(Result resultAi, Usuario usuario) {
		
		try {
			final String txtFecha = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_FECHA, CONTEXTO_CREAR_RECORDATORIO);
			final String txtHora = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_HORA, CONTEXTO_CREAR_RECORDATORIO);
			final String evento = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_EVENTO, CONTEXTO_CREAR_RECORDATORIO);
			final String txtTiempoRecordatorio = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_TIEMPO_RECORDATORIO, CONTEXTO_CREAR_RECORDATORIO);
			
			final EventoAgendado eventoAgendado = armarEeventoAgendado(txtFecha, txtHora, evento, txtTiempoRecordatorio, usuario);
			eventoAgendadoServicio.guardar(eventoAgendado);
			return armarRespuestaAgendarEvento(eventoAgendado);
		
		} catch (Exception e) {
			final String speech = "Ay que pena, creo que me he quedado dormida y no pude apuntarlo.\nInt\u00e9ntalo nuevamte por favor :(";
			log.error("No se pudo agendar evento.", e);
			return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, source);
		}
	}
	
	public ResponseMessageApiAi obtenerAgendaUsuario(Result resultAi, Usuario usuario) {
		try {
			String txtFecha = resultAi.getParameters().get(PARAM_FECHA).getAsString();
			
			final Date fechaEvento = esVacio(txtFecha) ? new Date() : formatoFechaAgendaJson.parse(txtFecha);
			
			return armarListadoAgenda(fechaEvento, usuario);
		
		} catch (Exception e) {
			final String speech = "Ay ay ay... no pude obtener tu Agenda.. Intenta de nuevo por favor.";
			log.error("No se pudo obtener la agendar del usuario", e);
			return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, source);
		}
	}
	
	public ResponseMessageApiAi obtenerAgendaSemanalUsuario(Usuario usuario) {
		
		final List<EventoAgendado> eventos = eventoAgendadoServicio.obtenerEventosFuturosUsuario(usuario);
		final String speech = "Agenda Semanal";
		
		List<Element> elements = eventos.stream().map(e -> {
			return FechaUtil.inicioDeDia(e.getFecha());
		}).distinct().map(c -> {
			final String fecha = formatoFechaMostrar.format(c.getTime());
			return new Element(fecha, armarUrlImagenDia(c.get(Calendar.DATE)), Arrays.asList(new PostbackButton("Ver este dia", "Ver agenda " + fecha)));
		}).limit(ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE)
				.collect(Collectors.toList());
		
		FacebookRequestGeneral mensajeParaFacebook = elements.isEmpty() ? 
													 new TextMessage("Fabuloso, est\u00e1s libre estos dias! :)") :
												     new RichMessage(new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elements)));	 
		
		return new ResponseMessageApiAi(speech, speech, new Data(mensajeParaFacebook), null, source);
	}
	
	private EventoAgendado armarEeventoAgendado(String txtFecha, String txtHora, String evento, String txtTiempoRecordatorio, Usuario usuario) throws ParseException {
		final Date esteMomento = new Date();
		Date fechaISO = formatoFechaHoraAgendaJsonISO.parse(txtFecha);
		final String txtFechaFormatoNormal= formatoFechaHoraAgendaJsonNormal.format(fechaISO);
		System.out.println("en armarEventoAgendado 210.........!!!!!!!!");
		System.out.println("txtFechaFormatoNormal = "+txtFechaFormatoNormal);
		System.out.println("txtHora = "+txtHora);
		Date andres=new Date();
		System.out.println("Date = "+andres);
		final Date fechaTentativa = formatoFechaHoraAgendaJson.parse(String.format("%s %s es", txtFechaFormatoNormal, txtHora));
//		final Date fechaTentativa = formatoFechaHoraAgendaJson.parse("2019-05-18 15:30 es");
		Calendar fechaDefinitiva = Calendar.getInstance();
		fechaDefinitiva.setTime(fechaTentativa);
		if (esteMomento.after(fechaTentativa)) {
			fechaDefinitiva.add(Calendar.HOUR_OF_DAY, 12);
		}
		
		return new EventoAgendado(evento, fechaDefinitiva.getTime(), usuario, Integer.parseInt(txtTiempoRecordatorio));
	}
	
	
	private ResponseMessageApiAi armarRespuestaAgendarEvento(EventoAgendado eventoAgendado) {
		
		final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.AGENDAR);
		final String speech = String.format(respuesta.getTexto(), eventoAgendado.getUsuario().getNombreFacebook(), eventoAgendado.getDescripcion(),  formatoFechaHoraMostrar.format(eventoAgendado.getFecha()));
		
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, source);
	}
	
	
	public void enviarRecodatorios() {
		final List<EventoAgendado> eventos = eventoAgendadoServicio.obtenerEventosParaRecordar();
		
		for (EventoAgendado evento : eventos) {
			final MensajeParaFacebook mensaje = new MensajeParaFacebook(
					evento.getUsuario().getIdFacebook(),
					new TextMessage(String.format("%s, no te olvides: %s a las %s", evento.getUsuario().getNombreFacebook(), evento.getDescripcion(), formatoHora.format(evento.getFecha())))
					);
			
			enviarRecordatorioAFacebook(mensaje, evento);
		}
	}
	
	private ResponseMessageApiAi armarListadoAgenda(Date fechaEvento, Usuario usuario) {
		final List<EventoAgendado> eventos = eventoAgendadoServicio.obtenerEventosPorDiaUsuario(fechaEvento, usuario);
				
		
		String speech = "Que bien! No tienes nada pendiente";
		if (eventos.isEmpty()) return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(speech)), null, source);
		
		speech = String.format("Tienes %s eventos", eventos.size());
		
		final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.VER_AGENDA);
		StringBuffer mensaje = new StringBuffer(String.format(respuesta.getTexto()+"\n\n", formatoFechaMostrar.format(fechaEvento)));
		
		for (EventoAgendado evento : eventos) {
			mensaje.append(String.format("%s %s: %s\n", "\u2022", formatoHora.format(evento.getFecha()), evento.getDescripcion()));
		}
		
		return new ResponseMessageApiAi(speech, speech, new Data(new TextMessage(mensaje.toString())), null, source);
		
	}
	
	@Asynchronous
	public void enviarRecordatorioAFacebook(MensajeParaFacebook mensaje, EventoAgendado evento) {
		final int status = ConsultarFacebook.postToFacebook(mensaje, propiedadesLola.facebookToken);
		if (status == 200) {
			evento.setNotificado(true);
			eventoAgendadoServicio.modificar(evento);
		}
	}
	
	//*********************************DAILOG V2**********************************
public ResponseMessageApiAiV2 agendarEventoV2(QueryResult resultAi, Usuario usuario) {
		
		try {
			String txtFecha = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_FECHA, CONTEXTO_CREAR_RECORDATORIO);
			final String txtHora = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_HORA, CONTEXTO_CREAR_RECORDATORIO);
			final String evento = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_EVENTO, CONTEXTO_CREAR_RECORDATORIO);
			final String txtTiempoRecordatorio = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_TIEMPO_RECORDATORIO, CONTEXTO_CREAR_RECORDATORIO);
			final EventoAgendado eventoAgendado = armarEeventoAgendado(txtFecha, txtHora.substring(11,16), evento, txtTiempoRecordatorio, usuario);
			eventoAgendadoServicio.guardar(eventoAgendado);
			return armarRespuestaAgendarEventoV2(eventoAgendado);
			
		} catch (Exception e) {
			final String speech = "Ay que pena, creo que me he quedado dormida y no pude apuntarlo.\nInt\u00e9ntalo nuevamente por favor :(";
			log.error("No se pudo agendar evento.", e);
			return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech)), null);
		}
	}
private ResponseMessageApiAiV2 armarRespuestaAgendarEventoV2(EventoAgendado eventoAgendado) {
	
	final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.AGENDAR);
	final String speech = String.format(respuesta.getTexto(), eventoAgendado.getUsuario().getNombreFacebook(), eventoAgendado.getDescripcion(),  formatoFechaHoraMostrar.format(eventoAgendado.getFecha()));
	
	return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech)), null);
}

public ResponseMessageApiAiV2 obtenerTiemposRecordatorioV2(QueryResult resultAi, Usuario usuario) {
	
	try {
		String txtFecha = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_FECHA, CONTEXTO_CREAR_RECORDATORIO);
		final String txtHora = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_HORA, CONTEXTO_CREAR_RECORDATORIO);
		final String evento = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_EVENTO, CONTEXTO_CREAR_RECORDATORIO);
		
		final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.TIEMPO_RECORDATORIO);
		/*if(!esVacio(txtFecha) && txtFecha.trim().length() == 8)
		{
			txtFecha = (txtFecha.split("//")[0] + txtFecha.split("//")[1]+ (String.valueOf(DateTime.now().getYear()).substring(0, 2)) + txtFecha.split("//")[2]);
			log.error("\n--------- Fecha: "+txtFecha+"\n------------");
		}*/
		final EventoAgendado eventoAgendado = armarEeventoAgendado(txtFecha, txtHora.substring(11,16), evento, "0", usuario);
		final String speech = String.format(respuesta.getTexto(), usuario.getNombreFacebook(), eventoAgendado.getDescripcion(),  formatoFechaHoraMostrar.format(eventoAgendado.getFecha()));
		
		TextMessageV2 mensaje = new TextMessageV2(speech);
		if (respuesta.isMostrarOpciones()) {
			final List<QuickReplyGeneral> quickReplies = opcionTiempoAgendaDao.obtenerActivas().stream().map(o -> {
				return new TextQuickReply(o.getNombre(), o.getPayload());
			}).collect(Collectors.toList());
			mensaje.setQuick_replies(quickReplies);
		}
		return new ResponseMessageApiAiV2(respuesta.getTexto(), "tiemposRecordatorio", new PayloadResponse(mensaje), null);
	
	} catch (Exception e) {
		final String speech = "Ay que pena, creo que me he quedado dormida y no pude apuntarlo.\nInt\u00e9ntalo nuevamente por favor :(";
		log.error("No se pudo agendar evento.", e);
		return new ResponseMessageApiAiV2(speech, "tiemposRecordatorio", new PayloadResponse(new TextMessageV2(speech)), null);
	}
}

public ResponseMessageApiAiV2 obtenerAgendaUsuarioV2(QueryResult resultAi, Usuario usuario) {
	try {
		String txtFecha = resultAi.getParameters().get(PARAM_FECHA).toString();
		
		final Date fechaEvento = esVacio(txtFecha) ? new Date() : formatoFechaAgendaJson.parse(txtFecha);
		
		return armarListadoAgendaV2(fechaEvento, usuario);
	
	} catch (Exception e) {
		final String speech = "Ay ay ay... no pude obtener tu Agenda.. Intenta de nuevo por favor.";
		log.error("No se pudo obtener la agendar del usuario", e);
		return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech)), null);
	}
}

private ResponseMessageApiAiV2 armarListadoAgendaV2(Date fechaEvento, Usuario usuario) {
	final List<EventoAgendado> eventos = eventoAgendadoServicio.obtenerEventosPorDiaUsuario(fechaEvento, usuario);
			
	
	String speech = "Que bien! No tienes nada pendiente";
	if (eventos.isEmpty()) return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(speech)), null);
	
	speech = String.format("Tienes %s eventos", eventos.size());
	
	final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.VER_AGENDA);
	StringBuffer mensaje = new StringBuffer(String.format(respuesta.getTexto()+"\n\n", formatoFechaMostrar.format(fechaEvento)));
	
	for (EventoAgendado evento : eventos) {
		mensaje.append(String.format("%s %s: %s\n", "\u2022", formatoHora.format(evento.getFecha()), evento.getDescripcion()));
	}
	
	return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(new TextMessageV2(mensaje.toString())), null);
	
}
public ResponseMessageApiAiV2 obtenerAgendaSemanalUsuarioV2(Usuario usuario) {
	
	final List<EventoAgendado> eventos = eventoAgendadoServicio.obtenerEventosFuturosUsuario(usuario);
	final String speech = "Agenda Semanal";
	
	List<Element> elements = eventos.stream().map(e -> {
		return FechaUtil.inicioDeDia(e.getFecha());
	}).distinct().map(c -> {
		final String fecha = formatoFechaMostrar.format(c.getTime());
		return new Element(fecha, armarUrlImagenDia(c.get(Calendar.DATE)), Arrays.asList(new PostbackButton("Ver este dia", "Ver agenda " + fecha)));
	}).limit(ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE)
			.collect(Collectors.toList());
	
	Facebook mensajeParaFacebook = elements.isEmpty() ? 
												 new TextMessageV2("Fabuloso, est\u00e1s libre estos dias! :)") :
											     new RichMessageV2(new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elements)));	 
	
	return new ResponseMessageApiAiV2(speech, source, new PayloadResponse(mensajeParaFacebook), null);
}
public ResponseMessageApiAiV2 mostrarOpcionesAgendaV2() {
	Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.INICIO_AGENDA);
	TextMessageV2 mensaje = new TextMessageV2(respuesta.getTexto());
	
	if (respuesta.isMostrarOpciones()) {
		final List<QuickReplyGeneral> quickReplies = opcionAgendaDao.obtenerActivas().stream().map(o -> {
			return new TextQuickReply(o.getNombre(), o.getPayload());
		}).collect(Collectors.toList());
		
		mensaje.setQuick_replies(quickReplies);
	}
	
	return new ResponseMessageApiAiV2(respuesta.getTexto(), "inicioAgenda", new PayloadResponse(mensaje), null);
}
}
