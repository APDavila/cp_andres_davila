package com.componente_practico.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextoUtil {
	
	public static final String mascaraCelular = "###xxxxx##";
	
	private static final String PATTERN_EMAIL = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	/**
     * Validate given email with regular expression.
     * 
     * @param email
     *            email for validation
     * @return true valid email, otherwise false
     */
    public static boolean validaEmail(String email) {
 
        // Compiles the given regular expression into a pattern.
        Pattern pattern = Pattern.compile(PATTERN_EMAIL);
 
        // Match the given input against this pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
 
    }
    
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
