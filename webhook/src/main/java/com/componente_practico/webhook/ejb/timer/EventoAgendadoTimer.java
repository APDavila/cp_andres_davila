package com.componente_practico.webhook.ejb.timer;

import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;

import com.componente_practico.webhook.acciones.ManejarAgenda;

@Singleton
@Lock(LockType.READ)
public class EventoAgendadoTimer {

	@EJB
	private ManejarAgenda manejarAgenda;
	
	// 0 0/5 * 1/1 * ? *
	@Schedule(second="0", minute="0/1",hour="*", persistent=false)
	public void recordarEventos() {
		manejarAgenda.enviarRecodatorios();
	}
	
}
