package com.componente_practico.universidad.facebook.controller;

import static com.holalola.webhook.PayloadConstantes.PAYLOAD_SELECCIONAR_FORMA_PAGO;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.componente_practico.directorio.AccionNoExisteException;
import com.componente_practico.ejb.config.PropiedadesLola;
import com.componente_practico.ejb.general.servicio.VerPerfilServicio;
import com.componente_practico.general.ejb.servicio.UbicacionUsuarioServicio;
import com.componente_practico.webhook.WebhookApiAiV2;
import com.componente_practico.webhook.acciones.ManejarRestaurantes;
import com.componente_practico.webhook.api.ai.v2.OutputContexts;
import com.componente_practico.webhook.api.ai.v2.ResponseMessageApiAiV2;
import com.componente_practico.webhook.v2.Data;
import com.componente_practico.webhook.v2.Intent;
import com.componente_practico.webhook.v2.OriginalDetectIntentRequest;
import com.componente_practico.webhook.v2.Payload;
import com.componente_practico.webhook.v2.QueryResult;
import com.componente_practico.webhook.v2.Request;
import com.google.gson.Gson;
import com.holalola.comida.facebook.controller.GeneralController;
import com.holalola.comida.pedido.ejb.dao.DetallePedidoDao;
import com.holalola.comida.pedido.ejb.dao.PedidoDao;
import com.holalola.comida.pedido.ejb.dao.ProveedorDao;
import com.holalola.comida.pedido.ejb.modelo.Pedido;
import com.holalola.comida.pedido.ejb.modelo.Proveedor;
import com.holalola.general.ejb.modelo.UbicacionUsuario;
import com.holalola.general.ejb.modelo.Usuario;
import com.holalola.util.GoogleMapsUtil;
import com.holalola.webhook.acciones.ConsultarFacebook;
import com.holalola.webhook.facebook.MensajeParaFacebook;
import com.holalola.webhook.facebook.templates.QuickReplyGeneral;
import com.holalola.webhook.facebook.templates.TextMessage;
import com.holalola.webhook.facebook.templates.TextQuickReply;

import ai.api.GsonFactory;
import ai.api.model.Recipient;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class GeoreferenciaController extends GeneralController {

	@EJB
	private ProveedorDao proveedorDao;

	@EJB
	private ManejarRestaurantes manejarRestaurantes;
	
	@EJB
	private PedidoDao pedidoDao;

	@EJB
	private DetallePedidoDao detallePedidoDao;

	@EJB
	UbicacionUsuarioServicio ubicacionUsuarioServicio;

	@EJB
	VerPerfilServicio verPerfilServicio;

	@EJB
	private PropiedadesLola propiedadesLola;

	@EJB
	WebhookApiAiV2 webhookApiAiV2;

	@SuppressWarnings("unused")
	private String mapsJavaScriptAPIKey = System.getProperty("lola.MapsJavaScriptAPIKey");
	private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();
	private Usuario usuario;
	Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
	private double latitud, longitud, latActual, longActual;
	public String idUsuario, idProveedor, payload;
	private List<Pedido> pedido = new ArrayList<Pedido>();
	Proveedor proveedorSeleccionado;
	private MapModel emptyModel;
	private String title,longitudActual,latitudActual;
	LatLng coord1;
	private MapModel draggableModel;
	private Marker marker;

	@PostConstruct
	public void inicializar() {
		draggableModel = new DefaultMapModel();
		params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		idUsuario = params.get("usu");
		latitudActual= params.get("lat");
		longitudActual= params.get("lng");
		latitud = Double.valueOf(latitudActual);
		longitud=Double.valueOf(longitudActual);
		payload= params.get("pld");
		coord1 = new LatLng(Double.valueOf(latitudActual), Double.valueOf(longitudActual));
		usuario = verPerfilServicio.obtenerPorId(Long.parseLong(idUsuario));
		emptyModel = new DefaultMapModel();
		draggableModel.addOverlay(new Marker(coord1, "Tu Ubicación"));
		
		for (Marker premarker : draggableModel.getMarkers()) {
			premarker.setDraggable(true);
		}
	}


	@SuppressWarnings("deprecation")
	public void confirmarDireccion() {
		/*DETERMINANDO LA DIRECCIÓN DE LA LATITUD Y LONGITUD ENVIADA */ 
		UbicacionUsuario ubicaciónSeleccioanda = ubicacionUsuarioServicio.insertarBasadoEnGoogleMaps(latitud, longitud, usuario);
		String speech = String.format(
				"Muy bien %s, de acuerdo a lo que me indicas tú te encuentras por la calle %s. :)",
				usuario.getNombreFacebook(), ubicaciónSeleccioanda.getCallePrincipalCalculada());
		/*CREANDO BOTONES PARA CONFIRMAR DIRECCION Y PARA REGRESAR AL MÉTODO DE UBICACIÓN*/
		List<QuickReplyGeneral> buttonsElemt = new ArrayList<QuickReplyGeneral>();
		buttonsElemt.add(new TextQuickReply("Sí, esa dirección", "USAR_DIRECCION_PRINCIPAL -1"));
		buttonsElemt.add(new TextQuickReply("No, otra dirección", "SOLICITAR_NUEVA_DIRECCION_USUARIO"));
		ConsultarFacebook.postToFacebook(
				new MensajeParaFacebook(usuario.getIdFacebook(), new TextMessage(speech.toString(), buttonsElemt)),
				propiedadesLola.facebookToken);
		RequestContext.getCurrentInstance().execute("closeView();");
	}

	public void puntoSeleccionado(PointSelectEvent event) {
		LatLng latlng = event.getLatLng();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Punto Seleccionado", "Lat:" + latlng.getLat() + ", Lng:" + latlng.getLng()));
		latitud = latlng.getLat();
		longitud = latlng.getLng();

	}

	public void addMarker() {
		Marker marker = new Marker(new LatLng(latitud, longitud), title);
		emptyModel.addOverlay(marker);

		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", "Lat:" + latitud + ", Lng:" + longitud));
	}

	public MapModel getDraggableModel() {
		return draggableModel;
	}

	public void onMarkerDrag(MarkerDragEvent event) {
		marker = event.getMarker();

		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Marker Dragged", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
		latitud = marker.getLatlng().getLat();
		longitud = marker.getLatlng().getLng();
	}

	public void addMessage(FacesMessage message) {
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public String getMapsJavaScriptAPIKey() {
		return mapsJavaScriptAPIKey;
	}

	public void setMapsJavaScriptAPIKey(String mapsJavaScriptAPIKey) {
		this.mapsJavaScriptAPIKey = mapsJavaScriptAPIKey;
	}

	public double getLatActual() {
		return latActual;
	}

	public void setLatActual(double latActual) {
		this.latActual = latActual;
	}

	public double getLongActual() {
		return longActual;
	}

	public void setLongActual(double longActual) {
		this.longActual = longActual;
	}

	public ProveedorDao getProveedorDao() {
		return proveedorDao;
	}

	public void setProveedorDao(ProveedorDao proveedorDao) {
		this.proveedorDao = proveedorDao;
	}

	public PedidoDao getPedidoDao() {
		return pedidoDao;
	}

	public void setPedidoDao(PedidoDao pedidoDao) {
		this.pedidoDao = pedidoDao;
	}

	public DetallePedidoDao getDetallePedidoDao() {
		return detallePedidoDao;
	}

	public void setDetallePedidoDao(DetallePedidoDao detallePedidoDao) {
		this.detallePedidoDao = detallePedidoDao;
	}

	public UbicacionUsuarioServicio getUbicacionUsuarioServicio() {
		return ubicacionUsuarioServicio;
	}

	public void setUbicacionUsuarioServicio(UbicacionUsuarioServicio ubicacionUsuarioServicio) {
		this.ubicacionUsuarioServicio = ubicacionUsuarioServicio;
	}

	public VerPerfilServicio getVerPerfilServicio() {
		return verPerfilServicio;
	}

	public void setVerPerfilServicio(VerPerfilServicio verPerfilServicio) {
		this.verPerfilServicio = verPerfilServicio;
	}

	public PropiedadesLola getPropiedadesLola() {
		return propiedadesLola;
	}

	public void setPropiedadesLola(PropiedadesLola propiedadesLola) {
		this.propiedadesLola = propiedadesLola;
	}

	public WebhookApiAiV2 getWebhookApiAiV2() {
		return webhookApiAiV2;
	}

	public void setWebhookApiAiV2(WebhookApiAiV2 webhookApiAiV2) {
		this.webhookApiAiV2 = webhookApiAiV2;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Map<String, String> getParams() {
		return params;
	}

	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	public String getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getIdProveedor() {
		return idProveedor;
	}

	public void setIdProveedor(String idProveedor) {
		this.idProveedor = idProveedor;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public List<Pedido> getPedido() {
		return pedido;
	}

	public void setPedido(List<Pedido> pedido) {
		this.pedido = pedido;
	}

	public Proveedor getProveedorSeleccionado() {
		return proveedorSeleccionado;
	}

	public void setProveedorSeleccionado(Proveedor proveedorSeleccionado) {
		this.proveedorSeleccionado = proveedorSeleccionado;
	}

	public MapModel getEmptyModel() {
		return emptyModel;
	}

	public void setEmptyModel(MapModel emptyModel) {
		this.emptyModel = emptyModel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static Gson getGson() {
		return GSON;
	}

	public String getLongitudActual() {
		return longitudActual;
	}

	public void setLongitudActual(String longitudActual) {
		this.longitudActual = longitudActual;
	}

	public String getLatitudActual() {
		return latitudActual;
	}

	public void setLatitudActual(String latitudActual) {
		this.latitudActual = latitudActual;
	}

	public LatLng getCoord1() {
		return coord1;
	}

	public void setCoord1(LatLng coord1) {
		this.coord1 = coord1;
	}

	public Marker getMarker() {
		return marker;
	}

	public void setMarker(Marker marker) {
		this.marker = marker;
	}

	public void setDraggableModel(MapModel draggableModel) {
		this.draggableModel = draggableModel;
	}

	
}