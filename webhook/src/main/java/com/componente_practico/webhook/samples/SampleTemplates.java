package com.componente_practico.webhook.samples;

import java.util.Arrays;
import java.util.List;

import com.componente_practico.webhook.api.ai.Data;
import com.holalola.webhook.facebook.payload.ButtonTemplatePayload;
import com.holalola.webhook.facebook.payload.GenericTemplatePayload;
import com.holalola.webhook.facebook.payload.ListTemplatePayload;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.payload.ReceiptTemplatePayload;
import com.holalola.webhook.facebook.response.InformacionUsuarioFacebook;
import com.holalola.webhook.facebook.templates.Address;
import com.holalola.webhook.facebook.templates.Adjustment;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.ButtonGeneral;
import com.holalola.webhook.facebook.templates.Element;
import com.holalola.webhook.facebook.templates.PostbackButton;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.ReceiptElement;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.Summary;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;
import com.holalola.webhook.facebook.templates.WebUrlButton;

public class SampleTemplates {
	
	public Data generarMensajeTexto(InformacionUsuarioFacebook usuario) {
		final List<QuickReplyGeneral> quickReplies = Arrays.asList(new TextQuickReply("Respuesta 1", "Payload respuesta 1", "https://tctechcrunch2011.files.wordpress.com/2016/04/facebook-chatbots.png?w=738"));
		return new Data(new TextMessage("Hola " + usuario.getFirst_name(), quickReplies));
	}
	
	public Data generarVideoTemplate() {
		return new Data(new RichMessage(new Attachment(Attachment.VIDEO, new MediaPayload("https://www.youtube.com/watch?v=P4Z8Sevz5GY"))));
	}
	
	public Data generarGenericTemplate() {
		final List<Element> elements = Arrays.asList(armarElement1ConBotones(), armarElement2ConBotones(), armarElement3ConBotones());
		return new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, new GenericTemplatePayload(elements))));
	}
	
	public Data generarButtonTemplate() {
		final List<ButtonGeneral> buttons = Arrays.asList(
				new WebUrlButton("Ir a google", "https://www.google.com/", false),
				new WebUrlButton("Ver F1", "http://www.espn.co.uk/f1", false),
				new PostbackButton("PostbackButton", "Postback"));
		
		
		return new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, new ButtonTemplatePayload("Probando botones", buttons))));
	}
	
	public Data generarListTemplate() {
		
		Element e1 = armarElement1(Arrays.asList(new PostbackButton("PostbackButton", "Postback")));
		Element e2 = armarElement2(null);
		Element e3 = armarElement3(null);
		
		
		final List<Element> elements = Arrays.asList(e1, e2, e3);
		return new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, new ListTemplatePayload(false, elements, new PostbackButton("PostbackButton", "Postback")))));
	}
	
	public Data generarReceiptTemplate() {
		
		
		final Summary summary = new Summary(200.00, 180.00, 5.00, 15.00);
		//final ReceiptTemplatePayload receiptPayload = new ReceiptTemplatePayload("Juan Francisco Perez", "12345678", "Visa XX8294", summary);
		final ReceiptTemplatePayload receiptPayload = new ReceiptTemplatePayload(
				"Juan Francisco Perez",
				"12345678",
				"Visa XX8294",
				summary,
				null,//String.valueOf(System.currentTimeMillis()),
				"Merchant name",
				"https://www.google.com",
				Arrays.asList(new ReceiptElement("Camiseta", 20.00, "De colores", 10, "USD", "https://tctechcrunch2011.files.wordpress.com/2016/04/facebook-chatbots.png?w=738")),
				new Address("Calle", "Inter", "Quito", "00000", "PI", "EC"), 
				Arrays.asList(new Adjustment("Descuento", 12.0))
						);
		
		return new Data(new RichMessage(new Attachment(Attachment.TEMPLATE, receiptPayload)));
	}

	private Element armarElement1ConBotones() {

		final List<ButtonGeneral> buttons = Arrays.asList(
				new WebUrlButton("FB Chatbot Group", "https://www.facebook.com/groups/aichatbots/", false),
				new WebUrlButton("Chatbots on Reddit", "https://www.reddit.com/r/Chat_Bots/", false),
				new WebUrlButton("Chatbots on Twitter", "https://twitter.com/aichatbots", false));

		
		return armarElement1(buttons);
	}
	
	private Element armarElement1(List<ButtonGeneral> buttons) {
		return new Element("Ai Chat Bot Communities",
				"http://1u88jj3r4db2x4txp44yqfj1.wpengine.netdna-cdn.com/wp-content/uploads/2016/04/chatbot-930x659.jpg",
				"Communities to Follow", null, // default_action
				buttons);
	}

	private Element armarElement2ConBotones() {

		final List<ButtonGeneral> buttons = Arrays.asList(
				new PostbackButton("What's the benefit?", "Chatbots make content interactive instead of static"),
				new PostbackButton("What can Chatbots do",
						"One day Chatbots will control the Internet of Things! You will be able to control your homes temperature with a text"),
				new PostbackButton("The Future", "Chatbots are fun! One day your BFF might be a Chatbot"));

		return armarElement2(buttons);
	}
	
	private Element armarElement2(List<ButtonGeneral> buttons) {
		return new Element("Chatbots FAQ",
				"https://tctechcrunch2011.files.wordpress.com/2016/04/facebook-chatbots.png?w=738",
				"Aking the Deep Questions", null, // default_action
				buttons);
	}

	private Element armarElement3ConBotones() {

		final List<ButtonGeneral> buttons = Arrays.asList(
				new PostbackButton("AIML",
						"Checkout Artificial Intelligence Mark Up Language. Its easier than you think!"),
				new PostbackButton("Machine Learning", "Use python to teach your maching in 16D space in 15min"),
				new PostbackButton("Communities",
						"Online communities & Meetups are the best way to stay ahead of the curve!"));

		return armarElement3(buttons);
	}
	
	private Element armarElement3(List<ButtonGeneral> buttons) {
		return new Element("Learning More",
				"http://www.brandknewmag.com/wp-content/uploads/2015/12/cortana.jpg", "Aking the Deep Questions", null, // default_action
				buttons);
	}

	private StringBuffer generarTestAttachment() {
		StringBuffer attachment = new StringBuffer();

		if (1 != 1) {
			attachment.append("{\"text\": \"Esto va en la seccion de facebook como text\"}");
			return attachment;
		}

		// attachment.append("{\"recipient\": {\"id\":"+facebookSenderId+"},
		// \"message\": {");
		attachment.append("{");
		attachment.append("   \"attachment\": { ");
		attachment.append("        \"type\": \"template\",");
		attachment.append("        \"payload\": {");
		attachment.append("            \"template_type\": \"generic\",");
		attachment.append("            \"elements\": [{");
		attachment.append("                 \"title\": \"Ai Chat Bot Communities\",");
		attachment.append("                 \"subtitle\": \"Communities to Follow\",");
		attachment.append(
				"                \"image_url\": \"http://1u88jj3r4db2x4txp44yqfj1.wpengine.netdna-cdn.com/wp-content/uploads/2016/04/chatbot-930x659.jpg\",");
		attachment.append("\"buttons\": [{");
		attachment.append("\"type\": \"web_url\",");
		attachment.append("\"url\": \"https://www.facebook.com/groups/aichatbots/\",");
		attachment.append("\"title\": \"FB Chatbot Group\"");
		attachment.append("}, {");
		attachment.append("\"type\": \"web_url\",");
		attachment.append("\"url\": \"https://www.reddit.com/r/Chat_Bots/\",");
		attachment.append("\"title\": \"Chatbots on Reddit\"");
		attachment.append("},{");
		attachment.append("\"type\": \"web_url\",");
		attachment.append("\"url\": \"https://twitter.com/aichatbots\",");
		attachment.append("\"title\": \"Chatbots on Twitter\"");
		attachment.append("}]");
		attachment.append("}, {");
		attachment.append("\"title\": \"Chatbots FAQ\",");
		attachment.append("\"subtitle\": \"Aking the Deep Questions\",");
		attachment.append(
				"\"image_url\": \"https://tctechcrunch2011.files.wordpress.com/2016/04/facebook-chatbots.png?w=738\",");
		attachment.append("\"buttons\": [{");
		attachment.append("\"type\": \"postback\",");
		attachment.append("\"title\": \"What's the benefit?\",");
		attachment.append("\"payload\": \"Chatbots make content interactive instead of static\"");
		attachment.append("},{");
		attachment.append("\"type\": \"postback\",");
		attachment.append("\"title\": \"What can Chatbots do\",");
		attachment.append(
				"\"payload\": \"One day Chatbots will control the Internet of Things! You will be able to control your homes temperature with a text\"");
		attachment.append("}, {");
		attachment.append("\"type\": \"postback\",");
		attachment.append("\"title\": \"The Future\",");
		attachment.append("\"payload\": \"Chatbots are fun! One day your BFF might be a Chatbot\"");
		attachment.append("}]");
		attachment.append("},  {");
		attachment.append("\"title\": \"Learning More\",");
		attachment.append("\"subtitle\": \"Aking the Deep Questions\",");
		attachment.append("\"image_url\": \"http://www.brandknewmag.com/wp-content/uploads/2015/12/cortana.jpg\",");
		attachment.append("\"buttons\": [{");
		attachment.append("\"type\": \"postback\",");
		attachment.append("\"title\": \"AIML\",");
		attachment.append(
				"\"payload\": \"Checkout Artificial Intelligence Mark Up Language. Its easier than you think!\"");
		attachment.append("},{");
		attachment.append("\"type\": \"postback\",");
		attachment.append("\"title\": \"Machine Learning\",");
		attachment.append("\"payload\": \"Use python to teach your maching in 16D space in 15min\"");
		attachment.append("}, {");
		attachment.append("\"type\": \"postback\",");
		attachment.append("\"title\": \"Communities\",");
		attachment.append("\"payload\": \"Online communities & Meetups are the best way to stay ahead of the curve!\"");
		attachment.append("}]");
		attachment.append("}]  ");
		attachment.append("} ");
		attachment.append("}");
		attachment.append("}");

		return attachment;
	}

}
