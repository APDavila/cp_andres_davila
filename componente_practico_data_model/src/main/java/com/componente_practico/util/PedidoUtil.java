package com.componente_practico.util;

import static com.componente_practico.util.TextoUtil.esVacio;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.componente_practico.general.ejb.modelo.UbicacionUsuario;
import com.holalola.comida.pedido.ejb.dao.DetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao.AuxTextoPromoObsequio;
import com.holalola.comida.pedido.ejb.dao.DetallePedidoDao.ProductoXDetalle;
 
import com.holalola.comida.pedido.ejb.modelo.DetallePedido;
 
import com.holalola.comida.pedido.ejb.modelo.Pedido;

@Stateless
public class PedidoUtil {
	
	
	private static final String baseUrl = System.getProperty("lola.base.url");
	private static final String baseUrlImg = System.getProperty("lola.base.urlimg");
	public static final String localImagesUrl = baseUrlImg + "images/";
	private static final String gs_sesionExterna = "hl_lg_ex";
    private static final String gs_sesionLola = "hl_lg_lola";
    private static final String gs_sesionProvExt = "hl_pr_ex";
    private static final String gs_sesionProvLola = "hl_pr_lola";
    private static final String gs_sesionUsuarioWeb = "S_US_OB";
    
    public static String getGsSesionusuarioweb() {
		return gs_sesionUsuarioWeb;
	}

	public static String gs_SesionProveedorLola()
	{
		return gs_sesionProvLola;
	}
	
	public static String gs_SesionProveedorExterno()
	{
		return gs_sesionProvExt;
	}
	
	public static String gs_SesionLola()
	{
		return gs_sesionLola;
	}
	
	public static String gs_SesionExterna()
	{
		return gs_sesionExterna;
	}
	
	public static String urlServidor()
	{
		return baseUrl;
	}
	
	public static String urlImagenesCine()
	{
		return localImagesUrl + "cine/";
	}
	
	public static String armarUrlPuestosPelicula(long pedidoId, String token) {
		return baseUrl + "webviews/cine/index.jsf?pedido="+pedidoId+"&token="+token;
	}
	
	public static void calcularTotalesPedido(Pedido pedido, List<DetallePedido> detallesPedido) {
		calcularSubTotal(pedido, detallesPedido);
		calcularTotal(pedido);
	}
	
	public static void calcularTotal(Pedido pedido) {
		pedido.setTotal(pedido.getSubtotal().add(pedido.getValorAdicionalDomicilio()));
		
		
		
	}
	
	public static void calcularSubTotal(Pedido pedido, List<DetallePedido> detallesPedido) {
		pedido.setSubtotal(detallesPedido.stream().map(DetallePedido :: getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
		
		
	}
	
	public static String getMD5(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] messageDigest = md.digest(input.getBytes());
			BigInteger number = new BigInteger(1, messageDigest);
			String hashtext = number.toString(16);

			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}
			return hashtext;
		} catch (Exception e) {
			//logger.info("\n--------\n " + e.getMessage() + " \n-----------------\n");
		}
		return "";
	}
	
	public static StringBuffer obtenerPedido(DetallePedido d, DetallePedidoDao detallePedidoDao) {
		StringBuffer speech = new StringBuffer();
		
		String ls_nombreProducto = "S/N";
		String ls_nombreCategoria = "S/C";
		String ls_cantidad = "S/Ca";
		String ls_precio = "S/P";
		String ls_precioAcompa = "S/Pa";
		String ls_total = "S/T";
		
		ProductoXDetalle productoXDetalle = null;
		
		try
		{
			productoXDetalle = detallePedidoDao.obtenerProdutoPorDetallePedido(d);
		}
		catch(Exception err)
		{
			System.out.println("-----ProductoXDetalle-----"+err.getMessage());
			err.printStackTrace();
			productoXDetalle = null;
		}
		
		 
		
		try
		{
			if( productoXDetalle != null )
			{
				ls_nombreProducto = productoXDetalle.getLs_pradre().trim().length() > 0 ? productoXDetalle.getLs_pradre().trim()+"\n" : "";
				ls_nombreProducto = ls_nombreProducto + " - "+ productoXDetalle.getLs_nombre().trim();
			}
			else
				ls_nombreProducto = d.getNombreProducto();
				
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_nombreCategoria = productoXDetalle != null ? productoXDetalle.getLs_categoria() : d.getNombreCategoria();
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_cantidad = String.valueOf(d.getCantidad());
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_precio = String.valueOf(d.getPrecio());
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_precioAcompa = (d.getPrecioAcompanantes().compareTo(BigDecimal.ZERO) == 1) ? String.format(" + $%s", d.getPrecioAcompanantes()) : "";
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_total = String.valueOf(d.getTotal());
		}
		catch(Exception err)
		{
			
		}
		
		speech.append(
				String.format("%s %s: %s x $%s%s = $%s\n", 
						ls_nombreProducto,  
						ls_nombreCategoria, 
						ls_cantidad, 
						ls_precio,
						ls_precioAcompa,
						ls_total
						)
				);
		if (d.getAcompanantesDetallePedido() != null) {
				
			
			
				d.getAcompanantesDetallePedido().forEach(a -> {
					String ls_tipoAcompananteNombre = "S/N";
					String ls_nombreAcompananteProveedor = "S/C";
					String ls_precioA = "S/P";
					
					try
					{
						ls_tipoAcompananteNombre = a.getAcompananteProducto().getTipoAcompanante().getNombre();
					}
					catch(Exception err)
					{
						
					}
					
					try
					{
						ls_nombreAcompananteProveedor = a.getAcompananteProducto().getAcompananteProveedor().getNombre();
					}
					catch(Exception err)
					{
						
					}
					
					try
					{
						ls_precioA = a.getAcompananteProducto().getPrecio().compareTo(BigDecimal.ZERO) == 1 ? 
								  String.format(" (+ $ %s)", String.format("%.2f",a.getAcompananteProducto().getPrecio())) : "";
					}
					catch(Exception err)
					{
						
					}
					
					speech.append(ls_tipoAcompananteNombre);
					speech.append(": ");
					speech.append(ls_nombreAcompananteProveedor);
					speech.append(ls_precioA);
					speech.append("\n");
				});
		}
		
		return speech;
	}
	
	
	
	public static StringBuffer otenerTextoDetallePedido(DetallePedido d, DetallePedidoDao detallePedidoDao) {
		StringBuffer speech = new StringBuffer();
		
		String ls_nombreProducto = "S/N";
		String ls_nombreCategoria = "S/C";
		String ls_cantidad = "S/Ca";
		String ls_precio = "S/P";
		String ls_precioAcompa = "S/Pa";
		String ls_total = "S/T";
		
		ProductoXDetalle productoXDetalle = null;
		
		try
		{
			productoXDetalle = detallePedidoDao.obtenerProdutoPorDetallePedido(d);
		}
		catch(Exception err)
		{
			System.out.println("-----ProductoXDetalle-----"+err.getMessage());
			err.printStackTrace();
			productoXDetalle = null;
		}
		
		 
		
		try
		{
			if( productoXDetalle != null )
			{
				ls_nombreProducto = productoXDetalle.getLs_pradre().trim().length() > 0 ? productoXDetalle.getLs_pradre().trim()+"\n" : "";
				ls_nombreProducto = ls_nombreProducto + " - "+ productoXDetalle.getLs_nombre().trim();
			}
			else
				ls_nombreProducto = d.getNombreProducto();
				
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_nombreCategoria = productoXDetalle != null ? productoXDetalle.getLs_categoria() : d.getNombreCategoria();
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_cantidad = String.valueOf(d.getCantidad());
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_precio = String.valueOf(d.getPrecio());
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_precioAcompa = (d.getPrecioAcompanantes().compareTo(BigDecimal.ZERO) == 1) ? String.format(" + $%s", d.getPrecioAcompanantes()) : "";
		}
		catch(Exception err)
		{
			
		}
		
		try
		{
			ls_total = String.valueOf(d.getTotal());
		}
		catch(Exception err)
		{
			
		}
		
		speech.append(
				String.format("%s %s: %s x $%s%s = $%s\n", 
						ls_nombreProducto,  
						ls_nombreCategoria, 
						ls_cantidad, 
						ls_precio,
						ls_precioAcompa,
						ls_total
						)
				);
		if (d.getAcompanantesDetallePedido() != null) {
				
			
			
				d.getAcompanantesDetallePedido().forEach(a -> {
					String ls_tipoAcompananteNombre = "S/N";
					String ls_nombreAcompananteProveedor = "S/C";
					String ls_precioA = "S/P";
					
					try
					{
						ls_tipoAcompananteNombre = a.getAcompananteProducto().getTipoAcompanante().getNombre();
					}
					catch(Exception err)
					{
						
					}
					
					try
					{
						ls_nombreAcompananteProveedor = a.getAcompananteProducto().getAcompananteProveedor().getNombre();
					}
					catch(Exception err)
					{
						
					}
					
					try
					{
						ls_precioA = a.getAcompananteProducto().getPrecio().compareTo(BigDecimal.ZERO) == 1 ? 
								  String.format(" (+ $ %s)", String.format("%.2f",a.getAcompananteProducto().getPrecio())) : "";
					}
					catch(Exception err)
					{
						
					}
					
					speech.append(ls_tipoAcompananteNombre);
					speech.append(": ");
					speech.append(ls_nombreAcompananteProveedor);
					speech.append(ls_precioA);
					speech.append("\n");
				});
		}
		
		return speech;
	}
	
	public static StringBuffer obtenerTextoValorTotalPedido(Pedido pedido, String ls_textoAdicionalAntesSub, String ls_textoAdicionalDescpTot) {
		final StringBuffer speechValorTotal = new StringBuffer();
		
		//Cambiar IVA
		
		BigDecimal lf_impuesto = pedido.getImpuestoAplicado();
		
		lf_impuesto = (new BigDecimal("1")).add(lf_impuesto.divide(new BigDecimal("100"),2, BigDecimal.ROUND_HALF_UP));
		
		BigDecimal  lf_subtotal = pedido.getTotal().divide(lf_impuesto,2, BigDecimal.ROUND_HALF_UP) ;
		
		
		speechValorTotal.append(String.format("- Domicilio: $%s\n", pedido.getValorAdicionalDomicilio()));
		if(ls_textoAdicionalAntesSub != null && ls_textoAdicionalAntesSub.trim().length() > 0)
		{
			speechValorTotal.append(String.format(ls_textoAdicionalAntesSub));
			speechValorTotal.append("\n");
		}
		speechValorTotal.append("-----------\n");
		speechValorTotal.append(String.format("Subtotal: $%s\n", String.format("%.2f", lf_subtotal )));
		speechValorTotal.append(String.format("IVA: $%s\n", String.format("%.2f", (pedido.getTotal().doubleValue() - lf_subtotal.doubleValue()) ) ));
		speechValorTotal.append(String.format("Total: $%s", pedido.getTotal()));
		if(ls_textoAdicionalDescpTot != null && ls_textoAdicionalDescpTot.trim().length() > 0)
		{
			speechValorTotal.append("\n-----------\n");
			speechValorTotal.append(String.format(ls_textoAdicionalDescpTot));
		}

		
		/*speechValorTotal.append(String.format("Subtotal: %s\n", pedido.getSubtotal()));
		speechValorTotal.append(String.format("Domicilio: %s\n", pedido.getValorAdicionalDomicilio()));
		speechValorTotal.append(String.format("Total: %s", pedido.getTotal()));*/
		
		return speechValorTotal;
	}
	
	public static StringBuffer obtenerTextoDetallePedido(Pedido pedido, DetallePedidoDao detallePedidoDao, PedidoDao pedidoDao) {
		StringBuffer texto = new StringBuffer();
		
		texto.append(String.format("Pedido No. %s\n", pedido.getId()));
		texto.append("\n");
		
		for (DetallePedido detalle : pedido.getDetallesPedido()) {
			texto.append(otenerTextoDetallePedido(detalle, detallePedidoDao));
		}
		texto.append("\n");
		
		AuxTextoPromoObsequio auxTextoPromoObsequio = pedidoDao.obtenerTextoObsequiosYPromocion(pedido.getId());
		
		texto.append(obtenerTextoValorTotalPedido(pedido, 
												  (auxTextoPromoObsequio != null && auxTextoPromoObsequio.getLs_descuento() != null ? auxTextoPromoObsequio.getLs_descuento().trim() : ""),
												  (auxTextoPromoObsequio != null && auxTextoPromoObsequio.getLs_obsequio() != null ? auxTextoPromoObsequio.getLs_obsequio().trim() : "")));
		return texto;
	}
	
	public static StringBuffer obtenerTextoDireccion(UbicacionUsuario ae_ubicacionUsuario) {
		StringBuffer texto = new StringBuffer();
		texto.append(String.format("%s", ObtenerDireccion(null,ae_ubicacionUsuario)));//Sacar del pedido.ubicacionusuario
		texto.append(String.format("-GPS: %s\n", ObtenerDireccionGPS(null,ae_ubicacionUsuario)));
		return texto;
	}
	
    public static StringBuffer obtenerTextoPedido(Pedido pedido, DetallePedidoDao detallePedidoDao, PedidoDao pedidoDao) {
		DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		StringBuffer texto = new StringBuffer();
		
		/*String ls_textoPromo = "";
		
		try
		{
			ls_textoPromo = pedidoDao.obtenerTextoPromociones(pedido.getId());
		}
		catch (Exception err)
		{
			err.printStackTrace();
			ls_textoPromo = "";
		}*/
		
		texto.append(String.format("No DE PEDIDO \n"));
		texto.append(String.format("     %s\n", pedido.getId()));
		texto.append(String.format("Fecha: \n     %s\n", formatoFecha.format(pedido.getFecha())));
		
		String ls_local = "";
		
		if(pedido.getZonaGeneral() != null && pedido.getZonaGeneral().trim().length() > 0)
			ls_local = pedido.getZonaGeneral().trim();
		
		if(pedido.getZonaEspecifica() != null && pedido.getZonaEspecifica().trim().length() > 0 && pedido.getZonaEspecifica().trim() != ls_local.trim())
			ls_local = ls_local + " " + pedido.getZonaEspecifica().trim();
		
		if(ls_local != null && ls_local.trim().length() > 0)
			texto.append(String.format("LOCAL - %s\n", ls_local));
		
		texto.append("-------- DATOS DEL USUARIO -----");
		texto.append("\n");
		
		texto.append(String.format("Nombre:\n  %s \n", pedido.getUsuario().toString()));
		
		texto.append(String.format("Nom. Facebook: \n  %s %s \n", (pedido.getUsuario().getApellidoFacebook() != null ? pedido.getUsuario().getApellidoFacebook().trim(): ""), (pedido.getUsuario().getNombreFacebook() != null ? pedido.getUsuario().getNombreFacebook().trim(): "") ));
		
		texto.append(String.format("Factura a: \n     %s - %s %s\n", (pedido.getDatoFacturaPedido() != null && pedido.getDatoFacturaPedido().getNumeroIdentificacion() != null && pedido.getDatoFacturaPedido().getNumeroIdentificacion().trim().length() > 0 ? pedido.getDatoFacturaPedido().getNumeroIdentificacion() : ""),
															(pedido.getDatoFacturaPedido() != null && pedido.getDatoFacturaPedido().getApellidos()!= null && pedido.getDatoFacturaPedido().getApellidos() != null && pedido.getDatoFacturaPedido().getApellidos().trim().length() > 0 ? pedido.getDatoFacturaPedido().getApellidos() : ""), 
															(pedido.getDatoFacturaPedido() != null && pedido.getDatoFacturaPedido().getNombre() != null && pedido.getDatoFacturaPedido().getNombre() != null && pedido.getDatoFacturaPedido().getNombre().trim().length() > 0 ? pedido.getDatoFacturaPedido().getNombre() : "")));  //pedido.getUsuario().getNumeroIdentificacion() ,pedido.getUsuario().getNombres(), pedido.getUsuario().getApellidos()));
		
		texto.append(String.format("Ciudad:\n     %s\n", ObtenerCiudad(pedido)));
		
		texto.append(String.format("Dirección:\n     %s\n", ObtenerDireccion(pedido, pedido.getUbicacionUsuario())));//pedido.getDatoFacturaPedido() != null && pedido.getDatoFacturaPedido().getDireccion() != null && pedido.getDatoFacturaPedido().getDireccion().trim().length() > 0 ? pedido.getDatoFacturaPedido().getDireccion() : ""));//Sacar del pedido.ubicacionusuario
		
		texto.append(String.format("Dirección GPS:\n     %s\n", ObtenerDireccionGPS(pedido, null)));
		
		texto.append(String.format("Email:\n     %s\n", (pedido.getDatoFacturaPedido() != null && pedido.getDatoFacturaPedido().getEmail() != null && pedido.getDatoFacturaPedido().getEmail().trim().length() > 0 ? pedido.getDatoFacturaPedido().getEmail() : "S/E") ));//Sacar del pedido.ubicacionusuario
		 
		texto.append(String.format("Teléfono:\n     %s\n", ObtenerTelefono(pedido)));//Sacar del pedido.ubicacionusuario
				
		texto.append("\n");
		
		texto.append("------------- PEDIDO -----------");
		
		texto.append("\n");
		
		for (DetallePedido detalle : pedido.getDetallesPedido()) {
				texto.append(otenerTextoDetallePedido(detalle, detallePedidoDao));
		}
		
        AuxTextoPromoObsequio auxTextoPromoObsequio = pedidoDao.obtenerTextoObsequiosYPromocion(pedido.getId());
		
		texto.append(obtenerTextoValorTotalPedido(pedido, (auxTextoPromoObsequio != null && auxTextoPromoObsequio.getLs_descuento() != null ? auxTextoPromoObsequio.getLs_descuento().trim() : ""), ""));
		
		 
		String nota = "";
		if(pedido.getNota() != null && pedido.getNota().trim().length() > 0)
			nota = pedido.getNota().trim();
		
		try
		{
			if(pedido.getNumeroPersonas() > 0)
				nota = nota + (nota.trim().length() > 0 ? "\n   " : "   ") + "Utensilios para " + pedido.getNumeroPersonas() + (pedido.getNumeroPersonas() > 1 ? " Personas" : " Persona");
		}
		catch(Exception err)
		{
			
		}
		
		if(auxTextoPromoObsequio != null && auxTextoPromoObsequio.getLs_obsequio() != null && auxTextoPromoObsequio.getLs_obsequio().trim().length() > 0)
		{
			texto.append("\n");
			texto.append("********** PROMOCION ***********");
			texto.append(String.format("\n %s", auxTextoPromoObsequio.getLs_obsequio().trim() ));
			texto.append("\n");
			texto.append("********************************");
		}
		
		if(nota!= null && nota.trim().length() > 0)
		{
			texto.append("\n");
			texto.append("*********** NOTAS **************");
			texto.append(String.format("\nNota: %s", nota.trim() ));
			texto.append("\n");
			texto.append("********************************");
		}
		texto.append("\n");
		texto.append("********************************");
		texto.append("\n");
		texto.append("********************************");
		texto.append("\n");
		texto.append(String.format("Total:          $ %s", pedido.getTotal()));
		texto.append("\n");
		texto.append("--------------------------------");
		texto.append("\n");
		texto.append(String.format("Paga Con:       %s", pedido.getFormaPago().getNombre().trim()));
		
	  
		if(pedido.getValorEnEfectivo() != null && pedido.getValorEnEfectivo().trim().length() > 0 )
			texto.append(String.format("\nCambio de:      $ %s", pedido.getValorEnEfectivo().trim()));
		
		texto.append("\n\nGenerado por Lola \nTu Asistente Personal... ");
		return texto;
	}
	
	private static String ObtenerCiudad(Pedido pedido)
	{
		String ls_ciudad = "";
		UbicacionUsuario ubicacionUsuario;
		
		try
		{
			ubicacionUsuario = pedido.getUbicacionUsuario();
		}
		catch(Exception err)
		{
			return ls_ciudad;
		}
		
		try
		{
			ls_ciudad = ls_ciudad + (esVacio(ubicacionUsuario.getCiudad()) ? "" :ubicacionUsuario.getCiudad().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		try
		{
			ls_ciudad = ls_ciudad + " - " + (esVacio(ubicacionUsuario.getProvincia()) ? "" :ubicacionUsuario.getProvincia().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		return ls_ciudad;
	}

	public static String ObtenerTelefono(Pedido pedido)
	{
		String ls_telefono = "";
		UbicacionUsuario ubicacionUsuario;
		
		try
		{
			ubicacionUsuario = pedido.getUbicacionUsuario();
		}
		catch(Exception err)
		{
			return ls_telefono;
		}
		
		try
		{
			ls_telefono = ls_telefono + (esVacio(ubicacionUsuario.getCelular()) ? "" :  ubicacionUsuario.getCelular().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		try
		{
			ls_telefono = ls_telefono + (esVacio(ubicacionUsuario.getTelefono()) ? "" :  (ls_telefono.trim().length() > 0 ? " / " : "" )+ ubicacionUsuario.getTelefono().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		return ls_telefono;
	}

	
	private static String ObtenerDireccion(Pedido pedido, UbicacionUsuario ae_ubicacionUsuario)
	{
		String ls_direccion = "";
		UbicacionUsuario ubicacionUsuario;
		
		try
		{
			if(pedido != null)
				ubicacionUsuario = pedido.getUbicacionUsuario();
			else
				ubicacionUsuario = ae_ubicacionUsuario;
		}
		catch(Exception err)
		{
			return ls_direccion;
		}	
		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getCallePrincipal()) ? "" : "   Calle Princial: " + ubicacionUsuario.getCallePrincipal().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getCalleSecundaria()) ? "" : "\n   Calle Secundaria: "  + ubicacionUsuario.getCalleSecundaria().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
				
		try
		{
			ls_direccion = ls_direccion +  (esVacio(ubicacionUsuario.getNumeracion()) ? "" : "\n   N°:" + ubicacionUsuario.getNumeracion().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getReferenciaUbicacion()) ? "\n   Sin Referencia" : "\n   Referencia:" + ubicacionUsuario.getReferenciaUbicacion().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		return ls_direccion;
	}
	
	private static String ObtenerDireccionGPS(Pedido pedido, UbicacionUsuario ae_ubicacionUsuario)
	{
		String ls_direccion = "";
		UbicacionUsuario ubicacionUsuario;
		
		try
		{
			if(pedido != null)
				ubicacionUsuario = pedido.getUbicacionUsuario();
			else
				ubicacionUsuario = ae_ubicacionUsuario;
		}
		catch(Exception err)
		{
			return ls_direccion;
		}
		
		
		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getParroquiaCalculada()) ? "" :"   Parroqia:" + ubicacionUsuario.getParroquiaCalculada().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getBarrioCalculado()) ? "" :"\n   Barrio:" + ubicacionUsuario.getBarrioCalculado().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getCallePrincipalCalculada()) ? "" : "\n   Calles: " + ubicacionUsuario.getCallePrincipalCalculada().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}

		
		try
		{
			ls_direccion = ls_direccion + (esVacio(ubicacionUsuario.getCodigoPostalCalculado()) ? "" :"\n   Cod. Postal:" + ubicacionUsuario.getCodigoPostalCalculado().trim());
		}
		catch(Exception err)
		{
			//No se pudo procesar la direccion
		}
				
		
		
		return ls_direccion;
	}

}
