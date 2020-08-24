package com.holalola.comida.facebook.controller;



import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.holalola.webhook.ejb.dao.ChatOperadorDao;
import com.holalola.webhook.ejb.modelo.ChatOperador;


@ManagedBean(name = "chatOperadorController")
@SessionScoped
@SuppressWarnings("serial")
public class ChatOperadorController {
	
	private List<ChatOperador> lMensajes;
	
	private String x;
	
	
	@EJB
	private ChatOperadorDao chatOperadorDao;
	
	@PostConstruct
	public void inicializar() {

		//obtenerUltimosMensajes();

	}
	
	public void obtenerUltimosMensajes() {
		lMensajes = chatOperadorDao.obtenerTodos(x);

	}

	public List<ChatOperador> getlMensajes() {
		return lMensajes;
	}

	public void setlMensajes(List<ChatOperador> lMensajes) {
		this.lMensajes = lMensajes;
	}
	
	public void kk() {
		
		System.out.println("Valoor de la ex -------------"+x);
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}
	
	
	
	
}
