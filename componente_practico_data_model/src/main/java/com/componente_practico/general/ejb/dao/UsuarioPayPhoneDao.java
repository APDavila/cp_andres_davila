package com.componente_practico.general.ejb.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class UsuarioPayPhoneDao {

	@PersistenceContext
	private EntityManager em;
	
	public boolean insertaActualizaTarjeta(Long as_codigoUsuario, String as_tarjetaEncriptada, String as_token, String as_csv) {

		List<Object> listaResultado;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_INSERTAACTUALIZATARJETA_PAYPHONE(?,?,?,?)}").
					setParameter(1, as_codigoUsuario)
					.setParameter(2, as_tarjetaEncriptada)
					.setParameter(3, as_token)
					.setParameter(4, as_csv).getResultList();

			 
				return listaResultado != null &&  listaResultado.size() > 0;
			 
		}
		catch(Exception err)
		{
			System.out.println("Error insertaActualizaTarjetaPayPhone " + err.getMessage());
			err.printStackTrace();
			return false;
		}
	}
	
	public List<TarjetasPayPhone> dameTarjetas(Long as_codigoUsuario) {

		try
		{
			List<Object[]> listaResultado = null;
			listaResultado = em.createNativeQuery("{call SP_DEVUELVETARJETAS_PAYPHONE(?)}").
					setParameter(1, as_codigoUsuario).getResultList();

			
			
			List<TarjetasPayPhone> listaTemporal = new ArrayList<TarjetasPayPhone>();
			
			listaResultado.stream().forEach((record) -> {
		        listaTemporal.add(new TarjetasPayPhone(record[0].toString(), 
		        									record[1].toString(),
		        									record[2].toString()
		        									));
		    });
			
			return listaTemporal;
			 
		}
		catch(Exception err)
		{
			return new ArrayList<TarjetasPayPhone>();
		}
	}
	
	public List<TarjetasPayPhone> eliminarTarjeta(Long as_codigoUsuario, String tokenTarjeta, String CSV) {

		try
		{
			List<Object[]> listaResultado = null;
			listaResultado = em.createNativeQuery("{call SP_ELIMINA_TARJETA_PAYPHONE(?,?,?)}").
					setParameter(1, as_codigoUsuario).
					setParameter(2, tokenTarjeta).
					setParameter(3, CSV).getResultList();
			
			List<TarjetasPayPhone> listaTemporal = new ArrayList<TarjetasPayPhone>();
			
			listaResultado.stream().forEach((record) -> {
		        listaTemporal.add(new TarjetasPayPhone(record[0].toString(), 
		        									record[1].toString(),
		        									record[2].toString()
		        									));
		    });
			
			return listaTemporal;
			 
		}
		catch(Exception err)
		{
			System.out.println("\n Error en eliminarTarjeta "+err.getMessage());
			err.printStackTrace();
			return new ArrayList<TarjetasPayPhone>();
		}
	}
	
	final public class TarjetasPayPhone
	{
		String tarjetaEncriptada;
		String token;
		String csv;
		
		public TarjetasPayPhone() {
			super();			
		}
		
		public TarjetasPayPhone(String tarjetaEncriptada, String token, String csv) {
			super();
			this.tarjetaEncriptada = tarjetaEncriptada;
			this.token = token;
			this.csv = csv;
		}
		
		public String getTarjetaEncriptada() {
			return tarjetaEncriptada;
		}
		public void setTarjetaEncriptada(String tarjetaEncriptada) {
			this.tarjetaEncriptada = tarjetaEncriptada;
		}
		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getCsv() {
			return csv;
		}
		public void setCsv(String csv) {
			this.csv = csv;
		}
		
	}
}
