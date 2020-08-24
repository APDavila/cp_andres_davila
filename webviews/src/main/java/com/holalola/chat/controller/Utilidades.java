package com.holalola.chat.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilidades {
	
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

	public static boolean validaCedula(String as_identificacion) throws Exception {
		try {

			boolean as_identificacionCorrecta = false;

			try {

				if (as_identificacion.length() == 10) // ConstantesApp.Longitudas_identificacion
				{
					int tercerDigito = Integer.parseInt(as_identificacion.substring(2, 3));
					if (tercerDigito < 6) {
						// Coeficientes de validación cédula
						// El decimo digito se lo considera dígito verificador
						int[] coefValas_identificacion = { 2, 1, 2, 1, 2, 1, 2, 1, 2 };
						int verificador = Integer.parseInt(as_identificacion.substring(9, 10));
						int suma = 0;
						int digito = 0;
						for (int i = 0; i < (as_identificacion.length() - 1); i++) {
							digito = Integer.parseInt(as_identificacion.substring(i, i + 1))
									* coefValas_identificacion[i];
							suma += ((digito % 10) + (digito / 10));
						}

						if ((suma % 10 == 0) && (suma % 10 == verificador)) {
							as_identificacionCorrecta = true;
						} else if ((10 - (suma % 10)) == verificador) {
							as_identificacionCorrecta = true;
						} else {
							as_identificacionCorrecta = false;
						}
					} else {
						as_identificacionCorrecta = false;
					}
				} else {
					as_identificacionCorrecta = false;
				}
			} catch (NumberFormatException nfe) {
				as_identificacionCorrecta = false;
			} catch (Exception err) {
				// System.out.println("Una excepcion ocurrio en el proceso de validadcion");
				as_identificacionCorrecta = false;
			}

			/*
			 * if (!as_identificacionCorrecta) {
			 * System.out.println("La Cédula ingresada es Incorrecta"); }
			 */
			return as_identificacionCorrecta;

		} catch (Exception e) {

		}
		return false;
	}

	private static final int NUM_PROVINCIAS = 24;
	private static int[] coeficientes = { 4, 3, 2, 7, 6, 5, 4, 3, 2 };
	private static int constante = 11;
	
	public static boolean validaRuc(String as_ruc) throws Exception {
		try {

			boolean resp_dato = false;
			final int prov = Integer.parseInt(as_ruc.substring(0, 2));
			if (!((prov > 0) && (prov <= NUM_PROVINCIAS))) {
				resp_dato = false;
			}
	 
			int[] d = new int[10];
			int suma = 0;
	 
			for (int i = 0; i < d.length; i++) {
				d[i] = Integer.parseInt(as_ruc.charAt(i) + "");
			}
	 
			for (int i = 0; i < d.length - 1; i++) {
				d[i] = d[i] * coeficientes[i];
				suma += d[i];
			}
	 
			int aux, resp;
	 
			aux = suma % constante;
			resp = constante - aux;
	 
			resp = (aux == 0) ? 0 : resp;
	 
			if (resp == d[9]) {
				resp_dato = true;
			} else {
				resp_dato = false;
			}
			return resp_dato;

		} catch (Exception e) {

		}
		return false;
	}

}
