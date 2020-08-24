package com.holalola.ejb.general.servicio;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GenerarBoletoAFNA {

	public static final String DIR_FONDO = "AFNA/Fondo/";
	public static final String DIR_PARTIDO = "AFNA/Partidos/";
	public static final String DIR_BOLETOS = "AFNA/Boletos/";
	public static final String DIR_QR = "QR/";

	public static final int totalRegistrosClima = 5;

	//public static ResponseMessageApiAi generarImagen(Long idProducto, Long idUsuario) throws IOException {
	
	public static boolean generarBoleto(Long idProducto, Long idUsuario, String as_fechaHora, 
			String as_estadio, String as_nombreCedula, String as_entrada, String as_sector, 
			String as_fila, String as_asiento, String ls_nombrePartido, String as_nombreArchivo) throws IOException {

		try
		{
			File path = new File(System.getProperty("lola.images.path") + DIR_FONDO);
			File pathPartido = new File(System.getProperty("lola.images.path") + DIR_PARTIDO);
			File pathQR = new File(System.getProperty("lola.images.path") + DIR_QR);
	
			BufferedImage fondo = ImageIO.read(new File(path, "entrada.png"));
	
			int w = 1879;
			int h = 658;
	
			BufferedImage combined = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics g = combined.getGraphics();
			g.drawImage(fondo, 0, 0, null);
	
			int posY = 0;
			int index = 0;
			
			//Colocando Imagen de Cabecera de los equipos
			
			BufferedImage imagen = ImageIO.read(new File(pathPartido, idProducto + ".png"));
			g.drawImage(imagen, 334, 53, null);
			
			//Colocando Imagen de Pie de los equipos
			
			imagen = ImageIO.read(new File(pathPartido, idProducto+"HL2" + ".png"));
			g.drawImage(imagen, 1452, 480, null);
			
			
			//Colocando QR
			
			imagen = ImageIO.read(new File(pathQR, idUsuario + ".png"));
			g.drawImage(imagen, 1102, 193, null);
			
			
			
			int x1 = 120;
	        int y1 = 220;
	        
	        int ancho = 880;
	        int alto = 60;
			
			//Colocando Fecha y Hora
			g.setColor( Color.BLACK );
			g.setFont( new Font( "Tahoma", Font.BOLD, 35 ) );
			//g.drawString (as_fechaHora, 210, 254 );
			
			//Colocando Nombre Estadio
			FontMetrics fm = g.getFontMetrics();
	     
	        int x = x1 + (ancho - fm.stringWidth(as_fechaHora)) / 2;
	        int y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_fechaHora, x, y);
			
			//Colocando Nombre Estadio
	        y1 = y1 + alto;
	        x = x1 + (ancho - fm.stringWidth(as_estadio)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_estadio, x, y);
			
			//Colocando Nombre y Cedula
	        y1 = y1 + alto;
	        x = x1 + (ancho - fm.stringWidth(as_nombreCedula)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_nombreCedula, x, y);
	        
	      //Colocando Detalle ubiccion
	        String ls_localidad = "Entrada: " + as_entrada + "  Sector: " + as_sector + " Fila: " + as_fila + " Asiento: " + as_asiento;
	        y1 = y1 + alto;
	        x = x1 + (ancho - fm.stringWidth(ls_localidad)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(ls_localidad, x, y);
	        
	        
	        //Colocando Entrada
	        
	        x1 = 1455;
	        y1 = 364;
	        
	        ancho = 60;
	        alto = 35;
	        
	        g.setFont( new Font( "Tahoma", Font.BOLD, 25 ) );
			
	        x = x1 + (ancho - fm.stringWidth(as_entrada)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_entrada, x, y);
	        
	      //Colocando Sector
	        x1 = x1 + 100;
	        x = x1 + (ancho - fm.stringWidth(as_sector)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_sector, x, y);
	        
	        
	        //Colocando Fila
	        x1 = x1 + 90;
	        x = x1 + (ancho - fm.stringWidth(as_fila)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_fila, x, y);
	        
	        
	        //Colocando Asiento
	        x1 = x1 + 90;
	        x = x1 + (ancho - fm.stringWidth(as_asiento)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_asiento, x, y);
	        
	        
	       //Colocando Fecha Pequenia
	        
	        x1 = 1560;
	        y1 = 295;
	        
	        ancho = 365;
	        alto = 35;
	        
	        g.setFont( new Font( "Tahoma", Font.BOLD, 20 ) );
			
	        x = x1 + (ancho - fm.stringWidth(as_fechaHora)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(as_fechaHora, x, y);
	        
	        
	       //Colocando NombrePartido 
	        
	        x1 = 1412;
	        y1 = 157;
	        
	        ancho = 410;
	        alto = 87;
	        
	        g.setFont( new Font( "Tahoma", Font.BOLD, 30 ) );
			
	        x = x1 + (ancho - fm.stringWidth(ls_nombrePartido)) / 2;
	        y = y1 + ((alto - fm.getHeight()) / 2) + fm.getAscent();
	        g.drawString(ls_nombrePartido, x, y);
			
	      
			ImageIO.write(combined, "PNG", new File(System.getProperty("lola.images.path") + DIR_BOLETOS, as_nombreArchivo.trim() + ".png"));
			return true;
		}
		catch(Exception err)
		{
			return false;
		}
	}
}
