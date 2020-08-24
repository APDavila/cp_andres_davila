package com.componente_practico.web.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class GnUsuarioPerfilDao {

	public class UsuarioPerfilWeb {
		Long idPagina;
		String pagina;
		String url;
		String padre;
		Long contador;
		
		public UsuarioPerfilWeb(Long idPagina, String pagina, String url, String padre, Long contador) {
			super();
			this.idPagina = idPagina;
			this.pagina = pagina;
			this.url = url;
			this.padre = padre;
			this.contador = contador;
		}
		
		public Long getIdPagina() {
			return idPagina;
		}

		public void setIdPagina(Long idPagina) {
			this.idPagina = idPagina;
		}

		public String getPagina() {
			return pagina;
		}

		public void setPagina(String pagina) {
			this.pagina = pagina;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getPadre() {
			return padre;
		}

		public void setPadre(String padre) {
			this.padre = padre;
		}

		public Long getContador() {
			return contador;
		}

		public void setContador(Long contador) {
			this.contador = contador;
		}

		
	}

	@PersistenceContext
	private EntityManager em;
	
	@SuppressWarnings("unchecked")
	public List<UsuarioPerfilWeb> obtenerProdutoPorDetallePedido(Long idUsuarioWeb) {

		List<Object[]> listaResultado = null;
		try
		{
			listaResultado = em.createNativeQuery("{call SP_DEVUELVE_MENUXUSARIO(?)}").setParameter(1, idUsuarioWeb).getResultList();
			
			if (listaResultado != null )
			{
				List<UsuarioPerfilWeb> listaTemporal = new ArrayList<UsuarioPerfilWeb>();
				
				listaResultado.stream().forEach((record) -> {
			        listaTemporal.add(new UsuarioPerfilWeb(((BigInteger) record[0]).longValue(), 
				        									record[1].toString(),
				        									record[2].toString(),
				        									record[3].toString(),
				        									((BigInteger) record[4]).longValue()
				        									));
			    });
				
				return listaTemporal;
			}
			else
				return new ArrayList();
			
		}
		catch(Exception err)
		{
			err.printStackTrace();
			return new ArrayList();
		}
	}
	
	
}
