package com.componente_practico.util;

import java.text.SimpleDateFormat;

import com.holalola.reservas.modelo.PedidosReservas;

public class ReservasUtil {
	
	
	public static StringBuffer obtenerTextoDetallePedido(PedidosReservas pedidosReservas) {
		StringBuffer texto = new StringBuffer();
		SimpleDateFormat formateador = new SimpleDateFormat("dd/MM/yyyy");
		String estado="Sin Proceso";
		if (pedidosReservas.getEstado() == pedidosReservas.CANCELADO) {
			estado= "Cancelado";
		} 
		else
		{
			if (pedidosReservas.getEstado() == pedidosReservas.ELIMINADO) {
				estado= "Eliminado";
			} 
			else
			{
				if (pedidosReservas.getEstado() == pedidosReservas.PAGADO) {
					estado= "Pagado";
				} 
				else
				{
					if (pedidosReservas.getEstado() == pedidosReservas.PENDIENTE) {
						estado= "Pendiente";
					} 
					else
					{
						if (pedidosReservas.getEstado() == pedidosReservas.PROCESADO) {
							estado= "Procesado";
						} 
					}
				}
			}
		}
		
		texto.append(String.format("Reserva No. %s\n", pedidosReservas.getId()));
		texto.append("\n");
		
		texto.append(String.format("- A nombre de: %s\n", (pedidosReservas.getApellidosReserva() != null ? pedidosReservas.getApellidosReserva().trim() : "")+" "+(pedidosReservas.getNombresReserva() != null ? pedidosReservas.getNombresReserva().trim() : "")));
		
		texto.append(String.format("- Fecha: %s\n", formateador.format(pedidosReservas.getFechaReserva())));
 
		texto.append(String.format("- Hora: %s\n", pedidosReservas.getHoraReserva()));
 
		texto.append(String.format("- Número de personas: %s\n", pedidosReservas.getNumeroPersonas()));
 
		texto.append(String.format("- Total: $%s\n", pedidosReservas.getTotal()));
 
		texto.append(String.format("- Estado: %s\n", estado));
 
		return texto;
	}
}
