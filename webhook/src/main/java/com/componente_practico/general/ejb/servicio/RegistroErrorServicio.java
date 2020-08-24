package com.componente_practico.general.ejb.servicio;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.holalola.general.ejb.dao.RegistroErrorDao;
import com.holalola.general.ejb.modelo.RegistroError;

import ai.api.model.Result;

@Stateless
public class RegistroErrorServicio {

	@EJB
	RegistroErrorDao registroErrorDao;
	
	public void registarError(Result resultAi, String requestBody) {
		registroErrorDao.registrarError(new RegistroError(resultAi.getResolvedQuery(), requestBody));
	}
}
