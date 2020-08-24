package com.componente_practico.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

public class CodigoQR {
	
	private static final String pathImagenesQR = System.getProperty("lola.images.pathQR");
	
	private static final String pathImagenesQRSupercines = System.getProperty("lola.images.pathQRSPC");
	
	private static final String validadorQR = "HOLANUOLOLA.MABC0011008_NA_ABLI9joX2AnZGER123";
	
	private static final String validadorInicioQR = "HolaLola -";
	
	public static String getValidadorinicioQR() {
		return validadorInicioQR;
	}

	public static String getValidadorQR() {
		return validadorQR;
	}
	
	
	public static String DiaDeSemana(Date d){
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		
		if(cal.get(Calendar.DAY_OF_WEEK) == 1)
			return "Domingo";
		
		if(cal.get(Calendar.DAY_OF_WEEK) == 2)
			return "Lunes";
		
		if(cal.get(Calendar.DAY_OF_WEEK) == 3)
			return "Martes";

		if(cal.get(Calendar.DAY_OF_WEEK) == 4)
			return "Miércoles";
		
		if(cal.get(Calendar.DAY_OF_WEEK) == 5)
			return "Jueves";
		
		if(cal.get(Calendar.DAY_OF_WEEK) == 6)
			return "Viernes";
		
		if(cal.get(Calendar.DAY_OF_WEEK) == 7)
			return "Sábado";
		
		return "";
	}

	public void GeneraCodigoQR(String ls_texto, String ls_nombreArchivo) {
		 
        File f = new File(pathImagenesQR.trim()+ls_nombreArchivo.trim() + ".png");
 
        try {
        	
        	//System.out.println("QRCode ls_texto: " + ls_texto);
 
        	generaQR(f, ls_texto, 300, 300);
            //System.out.println("QRCode Generated: " + f.getAbsolutePath());
 
            String qrString = decodifica(f);
            //System.out.println("Text QRCode: " + qrString);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
	
	public void GeneraCodigoQRSuperCines(String ls_texto, String ls_nombreArchivo) {
		 
        File f = new File(pathImagenesQRSupercines.trim()+ls_nombreArchivo.trim() + ".png");
 
        try {
        	
        	//System.out.println("QRCode ls_texto: " + ls_texto);
 
        	generaQR(f, ls_texto, 300, 300);
            //System.out.println("QRCode Generated: " + f.getAbsolutePath());
 
            String qrString = decodifica(f);
            //System.out.println("Text QRCode: " + qrString);
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }

	 private File generaQR(File file, String text, int h, int w) throws Exception {
		 
	        QRCodeWriter writer = new QRCodeWriter();
	        BitMatrix matrix = writer.encode(text, com.google.zxing.BarcodeFormat.QR_CODE, w, h);
	 
	        BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
	        image.createGraphics();
	 
	        Graphics2D graphics = (Graphics2D) image.getGraphics();
	        graphics.setColor(Color.WHITE);
	        graphics.fillRect(0, 0, matrix.getWidth(), matrix.getHeight());
	        graphics.setColor(Color.BLACK);
	 
	        for (int i = 0; i < matrix.getWidth(); i++) {
	            for (int j = 0; j < matrix.getHeight(); j++) {
	                if (matrix.get(i, j)) {
	                    graphics.fillRect(i, j, 1, 1);
	                }
	            }
	        }
	 
	        ImageIO.write(image, "png", file);
	 
	        return file;
	 
	    }
	 
	    private String decodifica(File file) throws Exception {
	 
	        FileInputStream inputStream = new FileInputStream(file);
	 
	        BufferedImage image = ImageIO.read(inputStream);
	 
	        int width = image.getWidth();
	        int height = image.getHeight();
	        int[] pixels = new int[width * height];
	 
	        LuminanceSource source = new BufferedImageLuminanceSource(image);
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	 
	        // decode the barcode
	        QRCodeReader reader = new QRCodeReader();
	        Result result = reader.decode(bitmap);
	        return new String(result.getText());
	    }
}
