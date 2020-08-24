package com.componente_practico.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Bi4WebUtil {
	
	private static String wsURL = System.getProperty("lola.wsEmail");

	/*
	 al_tipoDato: D => Fecha y debe ser con Formato obligatorio dia/mes/año
	 */
	public static String reporte(String as_reporte, List<String> al_nombreVariable, List<String> al_valorVariable, List<String> al_tipoDato) throws Exception {
		try {
			// Code to make a webservice HTTP request
			String responseString = "";
			String outputString = "";
			String ls_variables = "<al_nombreVariable>";
			
			if(al_nombreVariable != null && al_nombreVariable.size() > 0)
			{
				for (String boton : al_nombreVariable) {
					ls_variables = ls_variables + "<string>" + boton.trim() + "</string>";
				}
			}
			
			ls_variables = ls_variables + "</al_nombreVariable>";
			
			String ls_valores = "<al_valorVariable>";
			
			if(al_valorVariable != null && al_valorVariable.size() > 0)
			{				
				for (String boton : al_valorVariable) {
					ls_valores = ls_valores + "<string>" + boton.trim() + "</string>";
				}
			}
			
			ls_valores = ls_valores + "</al_valorVariable>";
			
			String ls_formatos = "<al_tipoDato>";
			
			if(al_tipoDato != null && al_tipoDato.size() > 0)
			{				
				for (String boton : al_tipoDato) {
					ls_formatos = ls_formatos + "<string>" + boton.trim() + "</string>";
				}
			}
			
			
			ls_formatos = ls_formatos + "</al_tipoDato>";

			URL url = new URL(wsURL);
			URLConnection connection = url.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) connection;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			String xmlInput = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
					"<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
					"  <soap:Body>\r\n" + 
					"    <reporteBI4Web xmlns=\"http://tempuri.org/\">\r\n" + 
					"      <as_reporte>"+as_reporte.trim()+"</as_reporte>\r\n" + 
					ls_variables + " \r\n" + 
					ls_valores + " \r\n" + 
					ls_formatos + " \r\n" + 
					"    </reporteBI4Web>\r\n" + 
					"  </soap:Body>\r\n" + 
					"</soap:Envelope>";
			
			byte[] buffer = new byte[xmlInput.length()];
			buffer = xmlInput.getBytes();
			bout.write(buffer);
			byte[] b = bout.toByteArray();
			String SOAPAction = "http://tempuri.org/reporteBI4Web";
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
			InputStreamReader isr = new InputStreamReader(httpConn.getInputStream(), StandardCharsets.UTF_8);
			BufferedReader in = new BufferedReader(isr);

			// Write the SOAP message response to a String.
			while ((responseString = in.readLine()) != null) {
				outputString = outputString + responseString;
			}
			try {
				return reemplazarCaractresEspeciales(outputString.split("~")[1].trim());
			} catch (Exception me) {
				return outputString.trim();
			}

		} catch (Exception me) {
			throw me;
		}
	}
	
	private static String reemplazarCaractresEspeciales(String as_texto)
	{
		return as_texto != null ? as_texto.replace("&amp;", "&") : "";
	}
}
