package com.componente_practico.util;


public class TextoUtil {
	
	public static final String mascaraCelular = "###xxxxx##";
	
	public static boolean esVacio(String texto) {
		return texto == null || "".equals(texto.trim());
	}
	
	public static String nuloAVacio(String texto) {
		return texto == null ? "" : texto.trim();
	}
	
	
	
	public static String enmascarar(String numero, String mascara) {

	    int index = 0;
	    StringBuilder maskedNumber = new StringBuilder();
	    for (int i = 0; i < mascara.length(); i++) {
	        char c = mascara.charAt(i);
	        if (c == '#') {
	            maskedNumber.append(numero.charAt(index));
	            index++;
	        } else if (c == 'x') {
	            maskedNumber.append(c);
	            index++;
	        } else {
	            maskedNumber.append(c);
	        }
	    }

	    return maskedNumber.toString();
	}

}
