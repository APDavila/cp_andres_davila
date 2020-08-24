package com.holalola.comida.facebook.controller;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.holalola.reservas.dao.ReservasDao;
import com.holalola.reservas.modelo.Restaurante;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class InformacionRestauranteReservaController {
	
	@EJB
	ReservasDao reservasDao;
	
	private boolean conFbExtensions;
	public Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
			.getRequestParameterMap();
	public String idUsuario = params.get("iu");
	
	public String idRestaurante = params.get("ir");
	
	
	private boolean mostrarFormulario;
	
	public Restaurante restaurante;
	
	private String coordenadasCentrales;
	
	private MapModel modelo;
	
	@PostConstruct
	public void inicializar() {
		
		
		
		conFbExtensions = true;
		mostrarFormulario = true;
		restaurante=reservasDao.obtenerRestaurantePorId(Long.parseLong(idRestaurante));
		
		coordenadasCentrales= String.format("%s, %s", restaurante.getLatitudSucursal(), restaurante.getLongitudSucursal());
		
		
		modelo = new DefaultMapModel();
        LatLng coord1 = new LatLng(Double.parseDouble(String.valueOf(restaurante.getLatitudSucursal())), Double.parseDouble(String.valueOf(restaurante.getLongitudSucursal())));

        modelo.addOverlay(new Marker(coord1, restaurante.getNombre()));
		
	}

	public boolean isConFbExtensions() {
		return conFbExtensions;
	}

	public void setConFbExtensions(boolean conFbExtensions) {
		this.conFbExtensions = conFbExtensions;
	}

	public boolean isMostrarFormulario() {
		return mostrarFormulario;
	}

	public void setMostrarFormulario(boolean mostrarFormulario) {
		this.mostrarFormulario = mostrarFormulario;
	}

	public String getIdusuario() {
		return idUsuario;
	}

	public String getIdRestaurante() {
		return idRestaurante;
	}

	public Restaurante getRestaurante() {
		return restaurante;
	}

	public void setRestaurante(Restaurante restaurante) {
		this.restaurante = restaurante;
	}

	public String getCoordenadasCentrales() {
		return coordenadasCentrales;
	}

	public void setCoordenadasCentrales(String coordenadasCentrales) {
		this.coordenadasCentrales = coordenadasCentrales;
	}

	public MapModel getModelo() {
		return modelo;
	}

	public void setModelo(MapModel modelo) {
		this.modelo = modelo;
	}
	
	

}
