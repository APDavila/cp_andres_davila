package com.componente_practico.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class MailUtil {

	private static String wsURL = System.getProperty("lola.wsEmail");
	
	private static String CambiarTildes(String sl_texto)
	{
		return sl_texto.replace("Á", "-LOLA-A")
				.replace("É", "-LOLA-E")
				.replace("Í", "-LOLA-I")
				.replace("Ó", "-LOLA-O")
				.replace("Ú", "-LOLA-U")
				.replace("á", "-LOLA-a")
				.replace("é", "-LOLA-e")
				.replace("í", "-LOLA-i")
				.replace("ó", "-LOLA-o")
				.replace("ú", "-LOLA-u")
				.replace("°", "-LOLA-o")
				.replace("!", "-LOLA-|.-")
				.replace("¡", "-LOLA-|-.");
		
	}

	public static String enviarMail(String destinatario, String asunto, String cuerpo, boolean ab_adjuntar, String as_adjuntar) throws Exception {
		try {
			// Code to make a webservice HTTP request
			String responseString = "";
			String outputString = "";

			URL url = new URL(wsURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?> <soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n"
					+ "  <soap:Body>\r\n" + "    <enviaMail xmlns=\"http://tempuri.org/\">\r\n"
					+ "      <as_destinatario>" + destinatario.trim() + "</as_destinatario>\r\n"
				    + "      <ai_adjuntar>" + (ab_adjuntar ? "1" : "0") + "</ai_adjuntar>\r\n"
					+ "      <as_informacion>" + CambiarTildes(cuerpo.trim()).trim() + "</as_informacion>\r\n" + "      <as_asunto>"
					+ asunto.trim() + "</as_asunto>\r\n" 
				    + "      <as_adjuntar>" + as_adjuntar + "</as_adjuntar>\r\n"
					+ "    </enviaMail>\r\n" + "  </soap:Body>\r\n"
					+ "</soap:Envelope>";
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			String SOAPAction = "http://tempuri.org/enviaMail";
			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			
		
			OutputStream out = httpConn.getOutputStream();
			// Write the content of the request to the outputstream of the HTTP Connection.
			out.write(b);
			out.close();
			// Ready with sending the request.

			// Read the response.
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			// Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			try {
				return outputString.split("~")[1].trim();
			} catch (Exception me) {
				return outputString.trim();
			}

		} catch (Exception me) {
			throw me;
		}
	}

	
	public static String enviarMail(String destinatario, String asunto, String cuerpo, boolean ab_adjuntar, List<String> al_botones, String as_adjuntar) throws Exception {
		try {
			// Code to make a webservice HTTP request
			String responseString = "";
			String outputString = "";
			String ls_botones = "<al_botones>";
			if(al_botones != null && al_botones.size() > 0)
			{
				for (String boton : al_botones) {
					if(boton != null && boton.trim().length() > 0 && boton.split("|NUO|").length > 1)
					ls_botones = ls_botones + "<string>" + boton.trim() + "</string>";
				}
			}
			
			ls_botones = ls_botones + "</al_botones>";

			URL url = new URL(wsURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
					"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
					"  <soap:Body>\r\n" + 
					"    <enviaMailBotones xmlns=\"http://tempuri.org/\">\r\n"
					+ "      <as_destinatario>" + destinatario.trim() + "</as_destinatario>\r\n"
				    + "      <ai_adjuntar>" + (ab_adjuntar ? "1" : "0") + "</ai_adjuntar>\r\n"
				    + ls_botones + " \r\n"
					+ "      <as_informacion>" + CambiarTildes(cuerpo.trim()).trim() + "</as_informacion>\r\n" + "      <as_asunto>"
					+ asunto.trim() + "</as_asunto>\r\n" 
				    + "      <as_adjuntar>" + as_adjuntar + "</as_adjuntar>\r\n"
					+ "  </enviaMailBotones>\r\n" + 
							"  </soap:Body>\r\n" + 
							"</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			String SOAPAction = "http://tempuri.org/enviaMailBotones";
			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			
		
			OutputStream out = httpConn.getOutputStream();
			// Write the content of the request to the outputstream of the HTTP Connection.
			out.write(b);
			out.close();
			// Ready with sending the request.

			// Read the response.
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			// Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			try {
				return outputString.split("~")[1].trim();
			} catch (Exception me) {
				return outputString.trim();
			}

		} catch (Exception me) {
			throw me;
		}
	}
	
	
	public static String enviarMail(String destinatario, String asunto, String cuerpo, List<String> al_html) throws Exception {
		try {
			// Code to make a webservice HTTP request
			String responseString = "";
			String outputString = "";
			String ls_html = "<al_html>";
			if(al_html != null && al_html.size() > 0)
			{
				for (String boton : al_html) {
					if(boton != null && boton.trim().length() > 0)
						ls_html = ls_html + "<string>" + boton.trim() + "</string>";
				}
			}
			
			ls_html = ls_html + "</al_html>";

			URL url = new URL(wsURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
					"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
					"  <soap:Body>\r\n" + 
					"    <enviaMailHtml xmlns=\"http://tempuri.org/\">\r\n"
					+ "      <as_destinatario>" + destinatario.trim() + "</as_destinatario>\r\n"
					+ "      <as_informacion>" + CambiarTildes(cuerpo.trim()).trim() + "</as_informacion>\r\n" + "      <as_asunto>"
					+ asunto.trim() + "</as_asunto>\r\n" 
					 + CambiarTildes(ls_html.trim()).trim() + " \r\n"
					+ "  </enviaMailHtml>\r\n" + 
							"  </soap:Body>\r\n" + 
							"</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			String SOAPAction = "http://tempuri.org/enviaMailHtml";
			// Set the appropriate HTTP parameters.
			httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
			httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			httpConn.setRequestProperty("SOAPAction", SOAPAction);
			httpConn.setRequestMethod("POST");
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			
		
			OutputStream out = httpConn.getOutputStream();
			// Write the content of the request to the outputstream of the HTTP Connection.
			out.write(b);
			out.close();
			// Ready with sending the request.

			// Read the response.
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
			BufferedReader in = new BufferedReader(isr);

			// Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			try {
				return outputString.split("~")[1].trim();
			} catch (Exception me) {
				return outputString.trim();
			}

		} catch (Exception me) {
			throw me;
		}
	}
	
	/*
	 * public static void enviarConGMail(String destinatario, String asunto, String
	 * cuerpo) throws Exception { Properties props = System.getProperties();
	 * props.put("mail.smtp.host", servidorCorreo); // El servidor SMTP de Google
	 * props.put("mail.smtp.user", email); props.put("mail.smtp.clave",
	 * "miClaveDeGMail"); // La clave de la cuenta props.put("mail.smtp.auth",
	 * "true"); // Usar autenticación mediante usuario y clave
	 * props.put("mail.smtp.starttls.enable",
	 * ssl.trim().toLowerCase().equals("true")); // Para conectar de manera //
	 * segura al servidor SMTP props.put("mail.smtp.port", puertoCorreo); // El
	 * puerto SMTP seguro de Google
	 * 
	 * Session session = Session.getDefaultInstance(props); MimeMessage message =
	 * new MimeMessage(session);
	 * 
	 * try { message.setFrom(new InternetAddress(email));
	 * message.addRecipients(Message.RecipientType.TO, destinatario); // Se podrían
	 * añadir varios de la misma // manera
	 * 
	 * cuerpo =
	 * "<font size='2' color='#1F497D' face='Arial,sans-serif'> <p>Estimad@... </> <p>"
	 * + cuerpo + "<p/> " + " </font>" + fn_pieEMail();
	 * 
	 * message.setSubject(asunto);
	 * 
	 * message.setHeader("Content-Type", "text/html");
	 * 
	 * // message.setText(cuerpo);
	 * 
	 * message.setContent( String.format(
	 * "<html><head><title>%s</title></head><body><p>%s</body></html>", asunto,
	 * cuerpo), "text/html");
	 * 
	 * Transport transport = session.getTransport("smtp");
	 * transport.connect(servidorCorreo, email, clave);
	 * transport.sendMessage(message, message.getAllRecipients());
	 * transport.close(); } catch (Exception me) { throw me; } }
	 */

	private static String fn_pieEMail() {
		return " <div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='2' color='#1F497D' face='Arial,sans-serif'><span style='font-size:9pt;' lang='es'>Saludos,</span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='2' color='#1F497D' face='Arial,sans-serif'><span style='font-size:9pt;' lang='es'>&nbsp;</span></font></span></font></div> "
				+ " <font size='2' color='#1F497D' face='Arial,sans-serif'><span style='font-size:9pt;' lang='es'><b>Administrador Hola Lola</b></span></font>  "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='2' color='#1F497D' face='Arial,sans-serif'><span style='font-size:9pt;' lang='es'><b> </b></span></font><font size='2' color='#1F497D' face='Arial,sans-serif'><span style='font-size:9pt;' lang='es'> "
				+ " </span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='2' face='Calibri,sans-serif'><span style='font-size:11pt;' lang='es'><br> "
				+ "  "
				+ " </span></font><a href='' target='_blank'><img src='cid:imagen' width='305' height='82' border='0'/></a></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='2' face='Calibri,sans-serif'><span style='font-size:11pt;' lang='es'>&nbsp;</span></font></span></font></div> "
				+

				" <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='1' color='#606060' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'>&nbsp;</span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='1' color='#606060' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'><b>COMUNICACIÓN CONFIDENCIAL Y PRIVILEGIADA.&nbsp;</b></span></font><font size='1' color='#606060' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'>Si "
				+ " usted no es la persona a quien se dirige esta comunicación, favor notificar por e-mail y elimine todas las copias del mensaje.</span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='1' color='#606060' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'>&nbsp;</span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='1' color='#606060' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'><b>CONFIDENTIAL AND PRIVILEGED COMUNICATION</b></span></font><font size='1' color='#606060' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'>. "
				+ " If you have received this message in error, please notify me&nbsp;&nbsp;by returm e-mail, and destroy copies (electronic or otherwise) of this mewling.</span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='1' color='#4F7A28' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'>&nbsp;</span></font></span></font></div> "
				+ " <div style='margin:0;'><font size='3' face='Times New Roman,serif'><span style='font-size:12pt;'><font size='1' color='#4F7A28' face='Arial,sans-serif'><span style='font-size:6pt;' lang='es'>Antes de imprimir este e-mail piensa bien si es necesario hacerlo. "
				+ " El medio ambiente es responsabilidad de todos.</span></font></span></font></div> " + " </div>";
	}

}
