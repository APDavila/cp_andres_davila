package com.componente_practico.webhook.ejb.servicio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.holalola.webhook.ejb.dao.NoticiaDao;
import com.holalola.webhook.ejb.modelo.Noticia;

@Stateless
public class NoticiaServicio {

	@EJB
	private NoticiaDao noticiaDao;
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void guardar(Noticia noticia) {
		noticiaDao.guardar(noticia);
	}
	
	public Noticia obtenerPorId(String txtNotificiaId) {
		return noticiaDao.obtenerPorId(Long.valueOf(txtNotificiaId));
	}
	
	public List<Noticia> obtenerUltimasFuente(String fuente, String seccion) {
		return noticiaDao.obtenerUtlimas(fuente, seccion);
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Noticia> validarNoticias(List<Noticia> noticias, String fuente, String seccion) {
		for(Noticia noticia : noticias) {
			if (existeNoticia(fuente, noticia.getTitulo())) break;
			
			guardar(noticia);
		}
		
		return obtenerUltimasFuente(fuente, seccion);
	}
	
	private boolean existeNoticia(String fuente, String titulo) {
		return !noticiaDao.obtenerPorTituloFuente(titulo, fuente).isEmpty();
	}
}
