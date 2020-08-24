package com.componente_practico.main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageMergerMain {

	public static void main(String[] args) throws IOException {
		
		
		Calendar c = Calendar.getInstance();
		c.set(2016, 0, 10, 18, 0, 0);
		
		System.out.println(c.getTime());
		System.out.println(c.getTimeInMillis());
		                            
		System.out.println(new Date(1452466800382l));
		System.out.println(new Date(1486749600l * 1000));
		System.out.println(new Date(1486760400l * 1000));
		
		System.out.println(new Date(1486803600l * 1000));

		File path = new File("D:\\Documentos\\personal\\hola lola\\pruebaClima2\\");

				// load source images
		BufferedImage image = ImageIO.read(new File(path, "fondos\\fondoGrande.png"));
		BufferedImage image2 = ImageIO.read(new File(path, "horas\\hora10.png"));
		BufferedImage image3 = ImageIO.read(new File(path, "figuras\\lluvias.png"));
		BufferedImage image4 = ImageIO.read(new File(path, "temps\\18.png"));
		
				

				// create the new image, canvas size is the max. of both image sizes
				int w = 689;
				int h = 555;
				BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

				// paint both images, preserving the alpha channels
				Graphics g = combined.getGraphics();
				int posY = 0;
				g.drawImage(image, 0, 0, null);
				for (int i = 0; i < 5; ++i) {
				g.drawImage(image2, 0, posY, null);
				g.drawImage(image3, 0, posY, null);
				g.drawImage(image4, 0, posY, null);
				posY += 112;
				}

				// Save as new image
				ImageIO.write(combined, "PNG", new File(path, "combined.png"));

	}

}
