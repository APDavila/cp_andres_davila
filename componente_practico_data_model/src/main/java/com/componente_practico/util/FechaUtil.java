package com.componente_practico.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FechaUtil {
	
	private static final DateFormat fechaSimple = new SimpleDateFormat("dd/MM/yyyy");
	
	public static Calendar inicioDeDia(Date fecha) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
	
	public static Calendar inicioDeDia() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal;
	}
	
	public static Calendar finDeDia(Date fecha){
		Calendar cal = Calendar.getInstance();
		cal.setTime(fecha);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		
		return cal;
	}
	
	public static boolean isHanPasadoHoras(int cantidadHoras, Date fechaDesde) {
		return isHanPasadoMinutos((cantidadHoras * 60), fechaDesde);
	}
	
	public static boolean isHanPasadoMinutos(int cantidadMinutos, Date fechaDesde) {
		Calendar esteMomento = Calendar.getInstance();
		esteMomento.add(Calendar.MINUTE, - cantidadMinutos);
		
		return esteMomento.getTime().after(fechaDesde);
	}
	
	public static Date armarHoraHoy(int hora, int minuto) {
		Calendar hoy = Calendar.getInstance();
		hoy.set(Calendar.HOUR_OF_DAY, hora);
		hoy.set(Calendar.MINUTE, minuto);
		hoy.set(Calendar.SECOND, 0);
		
		return hoy.getTime();
	}
	
	public static boolean esHoy(Date fecha) {
		return fechaSimple.format(fecha).equals(fechaSimple.format(new Date()));
	}
	
	public static boolean esManana(Date fecha) {
		Calendar manana = Calendar.getInstance();
		manana.add(Calendar.DATE, 1);
		
		return fechaSimple.format(fecha).equals(fechaSimple.format(manana.getTime()));
	}

}
