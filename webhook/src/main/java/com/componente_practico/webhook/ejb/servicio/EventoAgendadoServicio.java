package com.componente_practico.webhook.ejb.servicio;

import static com.holalola.util.FechaUtil.inicioDeDia;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.webhook.ejb.dao.EventoAgendadoDao;
import com.holalola.webhook.ejb.modelo.EventoAgendado;

@Stateless
public class EventoAgendadoServicio {

	@EJB
	private EventoAgendadoDao eventoAgendadoDao;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void guardar(EventoAgendado eventoAgendado) {
		eventoAgendadoDao.guardar(eventoAgendado);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void modificar(EventoAgendado eventoAgendado) {
		eventoAgendadoDao.modificar(eventoAgendado);
	}
	
	public List<EventoAgendado> obtenerEventosPorDiaUsuario(Date fecha, Usuario usuario) {
		
		long casiUnDia = 86399999;
		Calendar fechaEvento = inicioDeDia(fecha);
		Date fechaFin = new Date((fechaEvento.getTimeInMillis() + casiUnDia));
		
		return eventoAgendadoDao.obtenerEventosPorDiaUsuario(fechaEvento.getTime(), fechaFin, usuario);
	}
	
	public List<EventoAgendado> obtenerEventosFuturosUsuario(Usuario usuario) {
		Calendar fechaInicio = inicioDeDia();
		Calendar fechaFin = Calendar.getInstance();
		fechaFin.add(Calendar.YEAR, 1);
		
		return eventoAgendadoDao.obtenerEventosPorDiaUsuario(fechaInicio.getTime(), fechaFin.getTime(), usuario);
	}
	
	public List<EventoAgendado> obtenerEventosParaRecordar() {
		Calendar fecha = Calendar.getInstance();
		fecha.add(Calendar.MINUTE, 1);
		
		return eventoAgendadoDao.obtenerParaNotificar(fecha.getTime());
	}
}
