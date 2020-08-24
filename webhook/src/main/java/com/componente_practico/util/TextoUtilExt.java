package com.componente_practico.util;

import org.jsoup.Jsoup;

import com.holalola.util.TextoUtil;

public class TextoUtilExt extends TextoUtil {

	public static String quitarHtml(String texto) {
		 return Jsoup.parse(texto).text();
	}
}
