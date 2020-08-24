package com.componente_practico.general.ejb.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.componente_practico.eventos.ejb.modelo.Entradas;
import com.componente_practico.general.ejb.modelo.ReporteCalificacion;
import com.componente_practico.general.ejb.modelo.Usuario;
import com.componente_practico.general.ejb.modelo.UsuarioPrueba;

@SuppressWarnings("unused")
@Stateless
public class UsuarioPruebaDao {
	
	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<UsuarioPrueba> obtenerTodos(){
		return em.createNativeQuery("call SP_USUARIOSPRUEBA()", UsuarioPrueba.class).getResultList();			
	}
}
