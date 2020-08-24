package com.componente_practico.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImagenPeliculaUtil {
	
	private static final String pathImagenesCine = System.getProperty("clima.imagenes.path") + "cine";

	public static String armarImagenCuadrada(String urlOriginal, String movieToken) {
		
		try {
		File path = new File(pathImagenesCine);

		BufferedImage fondo = ImageIO.read(new File(path, "fondoPelicula.jpg"));

		int w = 450;
		int h = 450;

		BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g = combined.getGraphics();
		g.drawImage(fondo, 0, 0, null);

		BufferedImage imagen = ImageIO.read(new URL(urlOriginal));
		g.drawImage(imagen, 75, 0, null);

		String imagenFinal = String.format("%s/supercines/%s.jpg", pathImagenesCine, movieToken);		
		ImageIO.write(combined, "JPEG", new File(imagenFinal));
		
		String urlFinal = String.format("supercines/%s.jpg", movieToken);
		return urlFinal;

		} catch (IOException e) {
			return null;
		}
	}
}
