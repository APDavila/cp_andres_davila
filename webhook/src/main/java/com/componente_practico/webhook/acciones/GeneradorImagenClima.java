package com.componente_practico.webhook.acciones;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.componente_practico.util.UrlUtil;
import com.componente_practico.webhook.api.ai.Data;
import com.componente_practico.webhook.api.ai.ResponseMessageApiAi;
import com.componente_practico.webhook.api.ai.v2.PayloadResponse;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.vo.ComposicionImagenClima;
import com.componente_practico.webhook.vo.FilaClima;
import com.holalola.webhook.facebook.payload.MediaPayload;
import com.holalola.webhook.facebook.templates.Attachment;
import com.holalola.webhook.facebook.templates.RichMessage;
import com.holalola.webhook.facebook.templates.RichMessageV2;

public class GeneradorImagenClima {
	
	public static final String DIR_FONDO = "fondos/";
	public static final String DIR_FIGURA = "figuras/";
	public static final String DIR_TEMPERATURA = "temps/";
	public static final String DIR_TEMPERATURA_MINIMA = "temps/min/";
	public static final String DIR_HORAS = "horas/";
	public static final String DIR_DIAS = "dias/";

	public static final int totalRegistrosClima = 5;

	public static ResponseMessageApiAi generarImagen(ComposicionImagenClima composicion) throws IOException {

		File path = new File(System.getProperty("clima.imagenes.path"));

		BufferedImage fondo = ImageIO.read(new File(path, composicion.getFondo()));

		int w = 689;
		int h = 555;

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = combined.getGraphics();
		g.drawImage(fondo, 0, 0, null);

		int posY = 0;
		int index = 0;

		for (FilaClima fila : composicion.getFilas()) {
			for (String pathImagen : fila.getImagenes()) {
				BufferedImage imagen = ImageIO.read(new File(path, pathImagen));
				g.drawImage(imagen, 0, posY, null);
			}

			posY += 112;
			index++;
			if (index == totalRegistrosClima)
				break;
		}

		ImageIO.write(combined, "PNG", new File(System.getProperty("clima.imagencompuesta.path"), composicion.getImagenCompuesta()));
		
		// TODO: Esto sacar de properties
		String urlImagen =  UrlUtil.armarUrlImagenClima(composicion.getImagenCompuesta());
		
		final Data weatherData = new Data(new RichMessage(new Attachment(Attachment.IMAGE, new MediaPayload(urlImagen))));
		final String speech = String.format("El clima de %s", composicion.getSource());
		return new ResponseMessageApiAi(speech, speech, weatherData, null, composicion.getSource());

	}
	
	public static ResponseMessageApiAiV2 generarImagenV2(ComposicionImagenClima composicion) throws IOException {

		File path = new File(System.getProperty("clima.imagenes.path"));

		BufferedImage fondo = ImageIO.read(new File(path, composicion.getFondo()));

		int w = 689;
		int h = 555;

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics g = combined.getGraphics();
		g.drawImage(fondo, 0, 0, null);

		int posY = 0;
		int index = 0;

		for (FilaClima fila : composicion.getFilas()) {
			for (String pathImagen : fila.getImagenes()) {
				BufferedImage imagen = ImageIO.read(new File(path, pathImagen));
				g.drawImage(imagen, 0, posY, null);
			}

			posY += 112;
			index++;
			if (index == totalRegistrosClima)
				break;
		}

		ImageIO.write(combined, "PNG", new File(System.getProperty("clima.imagencompuesta.path"), composicion.getImagenCompuesta()));
		
		// TODO: Esto sacar de properties
		String urlImagen =  UrlUtil.armarUrlImagenClima(composicion.getImagenCompuesta());
		
		final PayloadResponse weatherData = new PayloadResponse(new RichMessageV2(new Attachment(Attachment.IMAGE, new MediaPayload(urlImagen))));
		final String speech = String.format("El clima de %s", composicion.getSource());
		return new ResponseMessageApiAiV2(speech, composicion.getSource(), weatherData, null);

	}

}
