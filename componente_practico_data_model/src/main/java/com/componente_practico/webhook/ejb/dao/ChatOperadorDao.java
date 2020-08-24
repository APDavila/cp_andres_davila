package com.componente_practico.webhook.ejb.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.webhook.ejb.modelo.ChatOperador;

import java.util.List;

@Stateless
public class ChatOperadorDao {
	@PersistenceContext
	private EntityManager em;
	
	public ChatOperador insertar(ChatOperador chatOperador) {
		em.persist(chatOperador);
		return chatOperador;
	}
	
	public List<ChatOperador> obtenerTodos(String nombreUsuarioChat) {
		//.setParameter("nombreUsuarioChat", nombreUsuarioChat)
		return em.createNamedQuery("chatOperador.ultimosMensajes",
				ChatOperador.class).setMaxResults(5).setParameter("nombreUsuarioChat", nombreUsuarioChat).getResultList();
	
	}
}
