package com.componente_practico.general.ejb.modelo;



import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import com.componente_practico.general.ejb.dao.CiudadDao;

@ManagedBean
public class SelectOneCiudad {
	
	    private Ciudad ciudad; 
	    private List<Ciudad> ciudades;
	    @EJB
		private CiudadDao ciudadDao;
	     
	    public void Inicializa() {
	    	ciudades = ciudadDao.obtenerCiudades();
	    }
	 
	    public Ciudad getCiudad() {
	        return ciudad;
	    }
	 
	    public void setCiudad(Ciudad ciudad) {
	        this.ciudad = ciudad;
	    }
	 
	    public List<Ciudad> getCiudades() {
	        return ciudades;
	    }
}
