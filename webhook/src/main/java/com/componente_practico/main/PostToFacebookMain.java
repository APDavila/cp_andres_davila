package com.componente_practico.main;

import java.util.ArrayList;
import java.util.List;

import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.Recipient;
import com.holalola.webhook.facebook.payload.ListTemplatePayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.RichMessage;

public class PostToFacebookMain {

	public static void main(String[] args) {
		
		//String url = "https://www.dropbox.com/sh/tyxtz6ecbihmqfq/AAB2CxYEMFupTNX6jdNlrhXya/cineTest/trailers/La%20Mucama%20Siniestra%20-%20Trailer.mp4?dl=1";
		//String resultado = ConsultarFacebook.uploadToFacebook(Attachment.VIDEO, url, "xxxx facebook token xxx");
		//System.out.println(resultado);

	}
	
	public static void postMessage() {
		MensajeParaFacebook m = new MensajeParaFacebook();
		m.setRecipient(new Recipient("1251016074991359"));
				//m.setMessage(new SampleTemplates().generarListTemplate().getFacebook());
		
		final List<Element> elements = new ArrayList<>();
		elements.add(new Element("Agenda",  "XXX"));
		for (int i = 1; i < 4; ++i) {
			elements.add(new Element(""+i,  "Desc"+i));
		}
			
			m.setMessage(new RichMessage(new Attachment(Attachment.TEMPLATE, new ListTemplatePayload(false, elements, null))));
		
		ConsultarFacebook.postToFacebook(m, "xxx facebook token xxx");
	}

}
