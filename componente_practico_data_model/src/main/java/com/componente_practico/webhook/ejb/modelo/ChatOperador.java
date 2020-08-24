package com.componente_practico.webhook.ejb.modelo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.componente_practico.general.ejb.modelo.EntityGeneral;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;

@Entity
@Table(name = "chat_operador")
@SuppressWarnings("serial")
@NamedQueries({
	@NamedQuery(name = "chatOperador.ultimosMensajes", query = "select o from ChatOperador o where o.nombreUsuarioChat = :nombreUsuarioChat ORDER BY o.fecha DESC")
})
public class ChatOperador extends EntityGeneral{
	

	@Column (name = "fecha")
	private Date fecha;
	@Column(name = "nombre_usuario_chat")
	private String nombreUsuarioChat;
	@Column(name = "mensaje")
	private String mensaje;
	
	
	public ChatOperador () {
		super();
	}
	
	public ChatOperador getInstanciaVacia() {
		return new ChatOperador();
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public String getNombreUsuarioChat() {
		return nombreUsuarioChat;
	}
	public void setNombreUsuarioChat(String nombreUsuarioChat) {
		this.nombreUsuarioChat = nombreUsuarioChat;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	
}
