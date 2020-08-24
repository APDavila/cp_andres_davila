package com.holalola.chat.controller;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.faces.context.FacesContext;

@Stateless
public class SessionUtils implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void add(String key, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(key, value);
	}

	public Object get(String key) {
		try {
			return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(key).toString();
		} catch (Exception err) {
			return "";
		}
	}
	
	public void borrar(String key) {
		try {
			FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(key);
		} catch (Exception err) {

		}
	}
}
