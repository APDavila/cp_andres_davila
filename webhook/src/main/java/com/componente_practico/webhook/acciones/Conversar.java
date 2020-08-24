package com.componente_practico.webhook.acciones;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.componente_practico.util.ApiAiUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV3;
import com.componente_practico.webhook.v2.QueryResult;
import com.holalola.broadcast.dao.OpcionesBroadcastDao;
import com.holalola.broadcast.dao.UsuarioOpcionesBroadcastDao;
import com.holalola.broadcast.entidad.OpcionesBroadcast;
import com.holalola.broadcast.entidad.UsuarioOpcionesBroadcast;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.giphy.Giphy;
import com.holalola.webhook.ejb.dao.ImagenDao;
import com.holalola.webhook.ejb.dao.RespuestaDao;
import com.holalola.webhook.ejb.dao.ServicioDao;
import com.holalola.webhook.ejb.modelo.Imagen;
import com.holalola.webhook.ejb.modelo.Respuesta;
import com.holalola.webhook.enumeracion.Categoria;
import com.holalola.webhook.facebook.payload.ButtonTemplatePayload;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
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
public class Conversar {

	@EJB
	ImagenDao imagenDao;
	@EJB
	RespuestaDao respuestaDao;
	@EJB
	ServicioDao servicioDao;

	@EJB
	OpcionesBroadcastDao opcionesBroadcastDao;
	
	@EJB
	UsuarioOpcionesBroadcastDao usuarioOpcionesBroadcastDao;
	
	private static final String CONTEXTO_CONVERSACION_DUMMY = "conversacionDummy";
	private static final String PARAM_CATEGORIA = "categoria.original";
	private static final String PARAM_ID_OPCION = "idOpcion.original";

	public ResponseMessageApiAi iniciarConversacion(Usuario usuario) {

		final Respuesta inicio = respuestaDao.obtenerUnoPorCategoria(Categoria.INICIO);
		final String speech = String.format(inicio.getTexto(), usuario.getNombreFacebook());

		final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Servicios", "MOSTRAR_SERVICIOS"));

		final Data data = new Data(
				new RichMessage(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons))));

		return new ResponseMessageApiAi(speech, speech, data, null, "iniciarConversacion");
	}

	public ResponseMessageApiAi saludar(Usuario usuario) {

		final Respuesta saludo = respuestaDao.obtenerUnoPorCategoria(Categoria.SALUDO);
		final String speech = String.format(saludo.getTexto(), usuario.getNombreFacebook());

		return armarMensajeRespuestaConServicios(speech, saludo.isMostrarOpciones());
	}

	public ResponseMessageApiAi mostrarServicios() {

		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.MOSTRAR_SERVICIOS);
		return armarMensajeRespuestaConServicios(servicios.getTexto(), servicios.isMostrarOpciones());
	}

	private ResponseMessageApiAi armarMensajeRespuestaConServicios(String speech, boolean mostrarServicios) {

		final TextMessage mensajeRespuesta = new TextMessage(speech);

		if (mostrarServicios) {
			final List<QuickReplyGeneral> quickReplies = servicioDao.obtenerActivos().stream().map(s -> {
				return new TextQuickReply(s.getNombre(), s.getPayload());
			}).collect(Collectors.toList());

			mensajeRespuesta.setQuick_replies(quickReplies);
		}

		return new ResponseMessageApiAi(speech, speech, new Data(mensajeRespuesta), null, "saludar");
	}

	private String buscaGif(Categoria ao_categoria) {
		String buscar = "";

		if (ao_categoria == Categoria.AMOR) {
				buscar = "love";
		}

		if (ao_categoria == Categoria.ANIMO_POSITIVO) {
				buscar = "happy";
		}

		if (ao_categoria == Categoria.ANIMO_NEGATIVO) {
				buscar = "sadness";
		}
		return buscar;
	}

	public ResponseMessageApiAi conversarDummy(Result resultAi, Usuario usuario) {
		final String paramTipoConversacion = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_CATEGORIA,
				CONTEXTO_CONVERSACION_DUMMY);
		Categoria categoria = null;
		
	
			categoria = Categoria.valueOf(paramTipoConversacion);
			
			final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(categoria);

			String speech = respuesta.getTexto();
			
			FacebookRequestGeneral mensajeRespuesta = new TextMessage(speech);
			

			if (respuesta.isMostrarImagen() && new Random().nextBoolean()) {

				String url = (new Random().nextBoolean() ? Giphy.consultaGif(buscaGif(categoria), 0) : "");//(int) (Math.random() * 49 + 1));

				if (url == null || url.trim().length() <= 0) {
					Imagen imagen = imagenDao.obtenerUnoPorCategoria(categoria);
					url = imagen.getUrl();
				}
				mensajeRespuesta = new RichMessage(new Attachment(Attachment.IMAGE, new MediaPayload(url)));// imagen.getUrl())));
			}
			
			return new ResponseMessageApiAi(speech, speech, new Data(mensajeRespuesta), null,
					"conversarDummy" + paramTipoConversacion);
		
	}
	
	public ResponseMessageApiAi registrarEncuesta(Result resultAi, Usuario usuario) {
		final String idOpcion = ApiAiUtil.obtenerValorParametro(resultAi, PARAM_ID_OPCION,
				CONTEXTO_CONVERSACION_DUMMY);
		
		Long ll_idOpcion = (long) 0;
		
		Respuesta respuesta = null;
		
		try
		{
			respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.ENCUESTA);
		}
		catch(Exception err)
		{
			respuesta = null;
		}
		
		String ls_respuesta = String.format((respuesta != null ? respuesta.getTexto() : "Listo %s, gracias por tu valiosa respuesta ;)"), usuario.getNombreFacebook());
		try
		{
			ll_idOpcion = Long.parseLong(idOpcion);
			final OpcionesBroadcast opcionesBroadcast = opcionesBroadcastDao.obtenerPorId(ll_idOpcion);
			
			usuarioOpcionesBroadcastDao.insertar(new UsuarioOpcionesBroadcast(usuario.getId(), opcionesBroadcast.getId(), true, new Date()));
			try
			{
				if(opcionesBroadcast.getMensajesBroadcast().getMensajerRespuesta() != null && opcionesBroadcast.getMensajesBroadcast().getMensajerRespuesta().trim().length() > 0)
					ls_respuesta =  String.format(opcionesBroadcast.getMensajesBroadcast().getMensajerRespuesta().trim(), usuario.getNombreFacebook());
			}
			catch(Exception err1)
			{
			}
		}
		catch(Exception err)
		{
			ls_respuesta = String.format((respuesta != null ? respuesta.getTexto() : ":( %s, no pude almacenar tu urespuesta."), usuario.getNombreFacebook());
			ll_idOpcion = (long) 0;
		}
		

		String speech = ls_respuesta;
		FacebookRequestGeneral mensajeRespuesta = new TextMessage(speech);

		return new ResponseMessageApiAi(speech, speech, new Data(mensajeRespuesta), null,
				"conversarDummy");

	}
	
	//************************************************DialogFlow V2****************************************************
	public ResponseMessageApiAiV3 mostrarServiciosv2() {

		final Respuesta servicios = respuestaDao.obtenerUnoPorCategoria(Categoria.MOSTRAR_SERVICIOS);
		return armarMensajeRespuestaConServiciosv2(servicios.getTexto(), servicios.isMostrarOpciones());
	}

	private ResponseMessageApiAiV3 armarMensajeRespuestaConServiciosv2(String speech, boolean mostrarServicios) {

		final TextMessageV2 mensajeRespuesta = new TextMessageV2(speech);

		if (mostrarServicios) {
			final List<QuickReplyGeneral> quickReplies = servicioDao.obtenerActivos().stream().map(s -> {
				return new TextQuickReply(s.getNombre(), s.getPayload());
			}).collect(Collectors.toList());

			mensajeRespuesta.setQuick_replies(quickReplies);
		}

		return new ResponseMessageApiAiV3(speech, "saludar", mensajeRespuesta, null);
	}
	public ResponseMessageApiAiV2 registrarEncuestaV2(QueryResult resultAi, Usuario usuario) {
		final String idOpcion = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_ID_OPCION,
				CONTEXTO_CONVERSACION_DUMMY);
		
		Long ll_idOpcion = (long) 0;
		
		Respuesta respuesta = null;
		
		try
		{
			respuesta = respuestaDao.obtenerUnoPorCategoria(Categoria.ENCUESTA);
		}
		catch(Exception err)
		{
			respuesta = null;
		}
		
		String ls_respuesta = String.format((respuesta != null ? respuesta.getTexto() : "Listo %s, gracias por tu valiosa respuesta ;)"), usuario.getNombreFacebook());
		try
		{
			ll_idOpcion = Long.parseLong(idOpcion);
			final OpcionesBroadcast opcionesBroadcast = opcionesBroadcastDao.obtenerPorId(ll_idOpcion);
			
			usuarioOpcionesBroadcastDao.insertar(new UsuarioOpcionesBroadcast(usuario.getId(), opcionesBroadcast.getId(), true, new Date()));
			try
			{
				if(opcionesBroadcast.getMensajesBroadcast().getMensajerRespuesta() != null && opcionesBroadcast.getMensajesBroadcast().getMensajerRespuesta().trim().length() > 0)
					ls_respuesta =  String.format(opcionesBroadcast.getMensajesBroadcast().getMensajerRespuesta().trim(), usuario.getNombreFacebook());
			}
			catch(Exception err1)
			{
			}
		}
		catch(Exception err)
		{
			ls_respuesta = String.format((respuesta != null ? respuesta.getTexto() : ":( %s, no pude almacenar tu urespuesta."), usuario.getNombreFacebook());
			ll_idOpcion = (long) 0;
		}
		

		String speech = ls_respuesta;
		Facebook mensajeRespuesta = new TextMessageV2(speech);

		return new ResponseMessageApiAiV2(speech, "conversarDummy", new PayloadResponse(mensajeRespuesta), null);

	}
	
	public ResponseMessageApiAiV2 saludarV2(Usuario usuario) {

		final Respuesta saludo = respuestaDao.obtenerUnoPorCategoria(Categoria.SALUDO);
		final String speech = String.format(saludo.getTexto(), usuario.getNombreFacebook());

		return armarMensajeRespuestaConServiciosV2(speech, saludo.isMostrarOpciones());
	}
	
	private ResponseMessageApiAiV2 armarMensajeRespuestaConServiciosV2(String speech, boolean mostrarServicios) {

		final TextMessageV2 mensajeRespuesta = new TextMessageV2(speech);

		if (mostrarServicios) {
			final List<QuickReplyGeneral> quickReplies = servicioDao.obtenerActivos().stream().map(s -> {
				return new TextQuickReply(s.getNombre(), s.getPayload());
			}).collect(Collectors.toList());

			mensajeRespuesta.setQuick_replies(quickReplies);
		}

		return new ResponseMessageApiAiV2(speech, "saludar", new PayloadResponse(mensajeRespuesta), null);
	}
	
	public ResponseMessageApiAiV2 conversarDummyV2(QueryResult resultAi, Usuario usuario) {
		final String paramTipoConversacion = ApiAiUtil.obtenerValorParametroV2(resultAi, PARAM_CATEGORIA,
				CONTEXTO_CONVERSACION_DUMMY);
		Categoria categoria = null;
		
	
			categoria = Categoria.valueOf(paramTipoConversacion);
			
			final Respuesta respuesta = respuestaDao.obtenerUnoPorCategoria(categoria);

			String speech = respuesta.getTexto();
			
			Facebook mensajeRespuesta = new TextMessageV2(speech);
			

			if (respuesta.isMostrarImagen() && new Random().nextBoolean()) {

				String url = (new Random().nextBoolean() ? Giphy.consultaGif(buscaGif(categoria), 0) : "");//(int) (Math.random() * 49 + 1));

				if (url == null || url.trim().length() <= 0) {
					Imagen imagen = imagenDao.obtenerUnoPorCategoria(categoria);
					url = imagen.getUrl();
				}
				mensajeRespuesta = new RichMessageV2(new Attachment(Attachment.IMAGE, new MediaPayload(url)));// imagen.getUrl())));
			}
			
			return new ResponseMessageApiAiV2(speech, "conversarDummy" + paramTipoConversacion, new PayloadResponse(mensajeRespuesta), null);
		
	}
	
	public ResponseMessageApiAiV2 iniciarConversacionV2(Usuario usuario) {

		final Respuesta inicio = respuestaDao.obtenerUnoPorCategoria(Categoria.INICIO);
		final String speech = String.format(inicio.getTexto(), usuario.getNombreFacebook());

		final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Servicios", "MOSTRAR_SERVICIOS"));

		final PayloadResponse data = new PayloadResponse(
				new RichMessageV2(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons))));

		return new ResponseMessageApiAiV2(speech, "iniciarConversacion", data, null);
	}

	public ResponseMessageApiAiV2 mostrarContacto(Usuario usuario) {
		
		final String speech = usuario.getNombreFacebook()+ ", aquí tienes información de contacto \n"+
				"Admisiones\r\n" + 
				"\r\n" + 
				"Campus Matriz\r\n" + 
				"\r\n" + 
				"Campus Centro (Zona 1)\r\n" + 
				"\r\n" + 
				"Francisco Pizarro E4-142 y Marieta de Veintimilla\r\n" + 
				"\r\n" + 
				"Quito, Ecuador.\r\n" + 
				"\r\n" + 
				"Teléfono: (593) 2 255-5741, Ext. 134\r\n" + 
				"\r\n" + 
				"    099 648 3605   (WhatsApp) "; 

		final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Servicios", "MOSTRAR_SERVICIOS"));
		PostbackButton but =new PostbackButton("Llenar Inscripción", "INSCRIBIRSE");
		final PayloadResponse data = new PayloadResponse(
				new RichMessageV2(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons))));

		return new ResponseMessageApiAiV2(speech, "iniciarConversacion", data, null);
	}
	
	public ResponseMessageApiAiV2 mostrarRequisitos(Usuario usuario) {
		
		final String speech = usuario.getNombreFacebook()+ ", al momento de inscribirte puedes ingresar opcionalmente al curso de nivelación para el examen de admisión. Este curso no tiene costo adicional.\r\n" + 
				" \r\n" + 
				"Las fechas establecidas para nuestros cursos de nivelación son:\r\n" + 
				"📅 1er grupo: del 10 al 22 de agosto\r\n" + 
				"📅 2do grupo: del 24 de agosto al 05 de septiembre\r\n" + 
				"📅 3er grupo: del 07 al 19 de septiembre\r\n" + 
				" \r\n" + 
				"Todos nuestros cursos finalizan con el examen de admisión."; 

		final List<ButtonGeneral> buttons = Arrays.asList(new PostbackButton("Servicios", "MOSTRAR_SERVICIOS"));
		PostbackButton but =new PostbackButton("Llenar Inscripción", "INSCRIBIRSE");
		final PayloadResponse data = new PayloadResponse(
				new RichMessageV2(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload(speech, buttons))));

		return new ResponseMessageApiAiV2(speech, "iniciarConversacion", data, null);
	}
}
