package com.holalola.comida.facebook.controller;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.cine.ejb.dao.PeliculaDao;
import com.holalola.cine.ejb.modelo.Pelicula;

@ViewScoped
@ManagedBean
@SuppressWarnings("serial")
public class TrailerCineController extends GeneralController {
	
	private String urlTrailer;
	private boolean conFbExtensions;
	
	@EJB
	PeliculaDao peliculaDao;
	
	@PostConstruct
	public void inicializar() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String movieToken = params.get("token");
		conFbExtensions = true;
		
		final List<Pelicula> pelicula = peliculaDao.obtenerPorSupercinesId(movieToken,4L);
		if (pelicula.isEmpty()) return; //TODO: Mostrar mensaje de error
		
		urlTrailer = pelicula.get(0).getUrlTailer();
		
		
	}
	
	public String getUrlTrailer() {
		return urlTrailer;
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}
}
