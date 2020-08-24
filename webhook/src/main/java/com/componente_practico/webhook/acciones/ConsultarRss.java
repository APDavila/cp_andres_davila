package com.componente_practico.webhook.acciones;

import static com.componente_practico.util.TextoUtilExt.quitarHtml;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.ejb.servicio.NoticiaServicio;
import com.holalola.webhook.ejb.dao.FuenteNoticiaDao;
import com.holalola.webhook.ejb.modelo.Noticia;
import com.holalola.webhook.facebook.ConstantesFacebook;
import com.holalola.webhook.facebook.payload.GenericTemplatePayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.RichMessageV2;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextMessageV2;
import com.holalola.webhook.facebook.templates.WebUrlButton;
import com.rometools.modules.mediarss.MediaEntryModule;
import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

@Stateless
public class ConsultarRss {
	
	final static Logger log = LoggerFactory.getLogger(ConsultarRss.class);
	
	@EJB
	private FuenteNoticiaDao fuenteNoticiaDao;
	@EJB
	private NoticiaServicio noticiaServicio;
	
	private static final DateFormat formatoFecha = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm", new Locale("es"));
	
	@SuppressWarnings("rawtypes")
	public ResponseMessageApiAi consultarRss(String urlRss, String fuente, String seccion) {
		
		try {
			URL url = new URL(urlRss);
			HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
			// Reading the feed
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(httpcon));
			List entries = feed.getEntries();
			Iterator itEntries = entries.iterator();
			
			List<Noticia> noticias = new ArrayList<>();
			int i = 0;
 			
			while (itEntries.hasNext()) {
				SyndEntry entry = (SyndEntry)itEntries.next();
				i++;
				
				noticias.add(new Noticia(fuente,
										seccion,		
										entry.getTitle(),
										obtenerDescripcion(entry),
										entry.getPublishedDate(),
										obtenerUrlImagen(entry, fuente),
										entry.getLink()
						));
				
				if (i == ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE) break;
			}
			
			return armarMensajeRespuesta(noticiaServicio.validarNoticias(noticias, fuente, seccion));
		
		} catch (Exception e) {
			log.error("No se pudo leer RSS", e);
			final String speech = String.format("No se pudieron obtener las noticias de %s %s", fuente, seccion);
			return new ResponseMessageApiAi(speech , speech, new Data(new TextMessage(speech)), null, "ConsultarRss");
		}
		
		
	}
	
	private  String obtenerUrlImagen(SyndEntry entry, String fuente) throws UnsupportedEncodingException {
		if (entry.getEnclosures() != null &&  !entry.getEnclosures().isEmpty())
			return ((SyndEnclosure)entry.getEnclosures().get(0)).getUrl();
		
		MediaEntryModule m;
		if ((m = (MediaEntryModule)entry.getModule(MediaEntryModule.URI))!= null) {
			if (m.getMediaGroups() != null && m.getMediaGroups().length > 0) {
				if (m.getMediaGroups()[0] != null && m.getMediaGroups()[0].getContents().length > 0) {
					return m.getMediaGroups()[0].getContents()[0].getReference().toString();
				}
			}
		}
       return fuenteNoticiaDao.obtenerPorPayload(fuente).getImagen(); 
	}
	
	private  String obtenerDescripcion(SyndEntry entry) {
		if (entry.getDescription() == null) return "";
		
		return quitarHtml(entry.getDescription().getValue());
	}
	
	private ResponseMessageApiAi armarMensajeRespuesta(List<Noticia> noticias) {
		List<Element> elements = new ArrayList<>();
		for (Noticia noticia : noticias) {
			
			String descripcion = String.format("%s\n%s", formatoFecha.format(noticia.getFecha()), noticia.getResumen());  
			//DefaultAction defaultAction = new DefaultAction(noticia.getUrlNoticia(), false, null, null);
			List<ButtonGeneral> buttons = Arrays.asList(
					new WebUrlButton("Ver Noticia", noticia.getUrlNoticia()),
					new PostbackButton("Ver Resumen", "VER_RESUMEN_NOTICIA " + noticia.getId())
					);
			
			elements.add(new Element(noticia.getTitulo(), noticia.getUrlImagen(), descripcion, null, buttons));
		}
		
		Data data = new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elements))));
		
		final String speech = String.format("Existen %d noticias", noticias.size());
		return new ResponseMessageApiAi(speech , speech, data, null, "ConsultarRss");
		
	}

	//****************************************************************DIALOG FLOW V2************************************
	
	@SuppressWarnings("rawtypes")
	public ResponseMessageApiAiV2 consultarRssV2(String urlRss, String fuente, String seccion) {
		
		try {
			URL url = new URL(urlRss);
			HttpURLConnection httpcon = (HttpURLConnection)url.openConnection();
			// Reading the feed
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed feed = input.build(new XmlReader(httpcon));
			List entries = feed.getEntries();
			Iterator itEntries = entries.iterator();
			
			List<Noticia> noticias = new ArrayList<>();
			int i = 0;
 			
			while (itEntries.hasNext()) {
				SyndEntry entry = (SyndEntry)itEntries.next();
				i++;
				
				noticias.add(new Noticia(fuente,
										seccion,		
										entry.getTitle(),
										obtenerDescripcion(entry),
										entry.getPublishedDate(),
										obtenerUrlImagen(entry, fuente),
										entry.getLink()
						));
				
				if (i == ConstantesFacebook.ELEMENTS_IN_GENERIC_TEMPLATE) break;
			}
			
			return armarMensajeRespuestaV2(noticiaServicio.validarNoticias(noticias, fuente, seccion));
		
		} catch (Exception e) {
			log.error("No se pudo leer RSS", e);
			final String speech = String.format("No se pudieron obtener las noticias de %s %s", fuente, seccion);
			return new ResponseMessageApiAiV2(speech , "ConsultarRss", new PayloadResponse(new TextMessageV2(speech)), null);
		}
		
		
	}
	
	private ResponseMessageApiAiV2 armarMensajeRespuestaV2(List<Noticia> noticias) {
		List<Element> elements = new ArrayList<>();
		for (Noticia noticia : noticias) {
			
			String descripcion = String.format("%s\n%s", formatoFecha.format(noticia.getFecha()), noticia.getResumen());  
			//DefaultAction defaultAction = new DefaultAction(noticia.getUrlNoticia(), false, null, null);
			List<ButtonGeneral> buttons = Arrays.asList(
					new WebUrlButton("Ver Noticia", noticia.getUrlNoticia()),
					new PostbackButton("Ver Resumen", "VER_RESUMEN_NOTICIA " + noticia.getId())
					);
			
			elements.add(new Element(noticia.getTitulo(), noticia.getUrlImagen(), descripcion, null, buttons));
		}
		
		PayloadResponse data = new PayloadResponse(new RichMessageV2(new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elements))));
		
		final String speech = String.format("Existen %d noticias", noticias.size());
		return new ResponseMessageApiAiV2(speech , "ConsultarRss", data, null);
		
	}

}
