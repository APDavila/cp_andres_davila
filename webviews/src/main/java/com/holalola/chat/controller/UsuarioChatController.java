package com.holalola.chat.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.componente_practico.universidad.facebook.controller.GeneralController;
import com.holalola.comida.pedido.ejb.dao.OperadorProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;
import com.holalola.general.ejb.dao.RolMenuDao;
import com.holalola.general.ejb.modelo.RolMenu;
import com.holalola.util.PedidoUtil;


@ManagedBean(name = "usuarioChatController")
@SessionScoped
@SuppressWarnings("serial")
public class UsuarioChatController extends GeneralController {

	@EJB
	OperadorProveedorDao operadorProveedorDao;
	
	/*@EJB
	private SessionUtils session;*/
	
	private OperadorProveedor operadorProveedor;
	
	private String gs_usuario;
	final private String gs_imgLola = "https://lola.nuo.com.ec/images/hl_images/lola.png";
	private boolean interno;
	
	boolean lb_menuPedidosComina = false;
	boolean lb_menuChat = false;
	boolean lb_menubroadcast = false;
	
	@EJB
	private RolMenuDao rolMenuDao;
	
	private void validaRol() {
		try
		{
			 
			 lb_menuPedidosComina = false;
		     lb_menuChat = false;
			 lb_menubroadcast = false;
			 
			 List<RolMenu> listaRolMenu = rolMenuDao.obtenerPorSupercinesId(operadorProveedor.getRol());
			 
			 for (RolMenu rolMenu : listaRolMenu) {
 
				 lb_menuPedidosComina = (!lb_menuPedidosComina ? rolMenu.getMenu().contains("Pedidos") : lb_menuPedidosComina);
			     lb_menuChat = (!lb_menuChat ? rolMenu.getMenu().contains("Chat") : lb_menuChat);
				 lb_menubroadcast = (!lb_menubroadcast ? rolMenu.getMenu().contains("Broadcast") : lb_menubroadcast);;
			}
		}
		catch(Exception er)
		{
			 System.out.println("--------Error----------"+er.getMessage());
			er.printStackTrace();
		}
	}
	
	public boolean isLb_menuPedidosComina() {
		return lb_menuPedidosComina;
	}

	public void setLb_menuPedidosComina(boolean lb_menuPedidosComina) {
		this.lb_menuPedidosComina = lb_menuPedidosComina;
	}

	public boolean isLb_menuChat() {
		return lb_menuChat;
	}

	public void setLb_menuChat(boolean lb_menuChat) {
		this.lb_menuChat = lb_menuChat;
	}


	public boolean isLb_menubroadcast() {
		return lb_menubroadcast;
	}


	public void setLb_menubroadcast(boolean lb_menubroadcast) {
		this.lb_menubroadcast = lb_menubroadcast;
	}
	
	public String getUrlPedido() {
		return PedidoUtil.urlServidor().trim() + "pedidosComida/interno/pedidos.jsf";
	}
	
	public String getUrlChat() {
		return PedidoUtil.urlServidor().trim() + "webviews/facebook/chat/chat.jsf";
	}
	
	public String getUrlBroadcast() {
		return PedidoUtil.urlServidor().trim() + "webviews/broadcast/mensajesbc.jsf";
	}
	
	public String getUrlInicio() {
		return PedidoUtil.urlServidor().trim() + "pedidosComida/interno/ingreso.jsf";
	}
	
	public String getUrlHome() {
		return PedidoUtil.urlServidor().trim() + "pedidosComida/interno/menu.jsf";
	}


	@PostConstruct
	public void inicializar() {
			
		// TODO: Esto hacer cuando ya este implementado el login
		if(gs_usuario != null && !"".equals(gs_usuario))
		{
			try
			{
				operadorProveedor = operadorProveedorDao.obtenerPorUsername(gs_usuario);
				validaRol();
			}
			catch(Exception err)
			{
				operadorProveedor = null;
			}
		}
	}
	
	
	
	public boolean isInterno() {
		return interno;
	}



	public void setInterno(boolean interno) {
		this.interno = interno;
	}



	public String getGs_usuario() {
		return gs_usuario;
	}



	public void setOperadorProveedor(OperadorProveedor operadorProveedor) {
		this.operadorProveedor = operadorProveedor;
		if(operadorProveedor != null)
		{
			try
			{
				gs_usuario = operadorProveedor.getUsername();
			}
			catch(Exception err)
			{
				gs_usuario = "";
			}
		}
	}



	public OperadorProveedor getOperadorProveedor() {
		return operadorProveedor;
	}



	public String getUsername() {
		
		if(interno)
			return "Lola";
		
		if(operadorProveedor != null)
			return operadorProveedor.getUsername();
		else
		{
			if(gs_usuario != null && !"".equals(gs_usuario))
				operadorProveedor = operadorProveedorDao.obtenerPorUsername(gs_usuario);
			if(operadorProveedor != null)
				return operadorProveedor.getUsername();
			else
				return "Lola";
		}
	}
	
	public Long getProveedorId() {
		if(operadorProveedor != null)
			return operadorProveedor.getProveedor().getId();
		else
			if(gs_usuario != null && !"".equals(gs_usuario))
				operadorProveedor = operadorProveedorDao.obtenerPorUsername(gs_usuario);
			if(operadorProveedor != null)
				return operadorProveedor.getProveedor().getId();
			else
			return (long) 0;
	}
	
	public String getLogo() {
		if(interno)
			return gs_imgLola;
		
		if(operadorProveedor != null)
		{
			//System.out.println("\n\n-------------carga : "+operadorProveedor.getProveedor().getLogo()+"-----------------");
			return operadorProveedor.getProveedor().getLogo();
		}
		else
			if(gs_usuario != null && !"".equals(gs_usuario))
				operadorProveedor = operadorProveedorDao.obtenerPorUsername(gs_usuario);
			if(operadorProveedor != null)
			{
				//System.out.println("\n\n-------------carga : "+operadorProveedor.getProveedor().getLogo()+"-----------------");
				return operadorProveedor.getProveedor().getLogo();
			}
			else
				return gs_imgLola;
	}
}
