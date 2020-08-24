package com.componente_practico.general.ejb.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.holalola.googlemaps.vo.DireccionGoogleMaps;

@Entity
@Table(name = "ubicacion_usuario")
@SuppressWarnings("serial")

@NamedQueries({
	@NamedQuery(name = "ubicacionUsuario.listaUbicaciones", query = "select o from UbicacionUsuario o where o.usuario = :usuario and o.alias is not null and confirmadoUsuario = 1 order by id desc"),
	@NamedQuery(name = "ubicacionUsuario.principalUsuario", query = "select o from UbicacionUsuario o where o.esPrincipal = true and o.usuario = :usuario"),
	@NamedQuery(name = "ubicacionUsuario.ultimaUsuario", query = "select o from UbicacionUsuario o where o.esUltima = true and o.usuario = :usuario order by o.id DESC"),
	@NamedQuery(name = "ubicacionUsuario.alias", query="select distinct(o.alias) from UbicacionUsuario o where o.usuario = :usuario and o.alias is not null and confirmadoUsuario = 1 and esPrincipal = 0 order by id desc"),
	@NamedQuery(name = "ubicacionUsuario.porAlias", query="select o from UbicacionUsuario o where o.usuario = :usuario and o.alias = :alias  and confirmadoUsuario = 1 order by id desc")
})
public class UbicacionUsuario extends EntityGeneral {

	@ManyToOne
	private Usuario usuario;

	@Column(name = "es_principal")
	private boolean esPrincipal;

	@Column(name = "calle_principal_calculada")
	private String callePrincipalCalculada;

	@Column(name = "barrio_calculado")
	private String barrioCalculado;

	@Column(name = "parroquia_calculada")
	private String parroquiaCalculada;

	@Column(name = "codigo_postal_calculado")
	private String codigoPostalCalculado;

	@Column(name = "calle_principal")
	private String callePrincipal;

	@Column(name = "calle_secundaria")
	private String calleSecundaria;

	@Column(name = "referencia_ubicacion")
	private String referenciaUbicacion;
	
	@Column(name = "es_ultima")
	private boolean esUltima;
	
	@Column(name = "confirmado_usuario")
	private boolean confirmadoUsuario;

	private double latitud;
	private double longitud;
	private String ciudad;
	private String provincia;
	private String pais;
	private String numeracion;
	private String telefono;
	private String celular;
	private String token;
	private String alias;
	
	
	
	public UbicacionUsuario() {}
	
	public UbicacionUsuario(DireccionGoogleMaps direccionCalculada, Usuario usuario) {
		this.usuario = usuario;
		this.latitud = direccionCalculada.getLatitud();
		this.longitud = direccionCalculada.getLongitud();
		this.callePrincipalCalculada = direccionCalculada.getCallePrincipal();
		this.barrioCalculado = direccionCalculada.getBarrio();
		this.parroquiaCalculada = direccionCalculada.getParroquia();
		this.ciudad = direccionCalculada.getCiudad();
		this.codigoPostalCalculado = direccionCalculada.getCodigoPostal();
		this.provincia = direccionCalculada.getProvincia();
		this.pais = direccionCalculada.getPais();
		this.esUltima = true;
		this.alias = "";
	}
	
	private UbicacionUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
	public static UbicacionUsuario getInstanciaVacia() {
		return new UbicacionUsuario(Usuario.getInstanciaVacia());
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isEsPrincipal() {
		return esPrincipal;
	}

	public void setEsPrincipal(boolean esPrincipal) {
		this.esPrincipal = esPrincipal;
	}

	public String getCallePrincipalCalculada() {
		return callePrincipalCalculada;
	}

	public void setCallePrincipalCalculada(String callePrincipalCalculada) {
		this.callePrincipalCalculada = callePrincipalCalculada;
	}

	public String getBarrioCalculado() {
		return barrioCalculado;
	}

	public void setBarrioCalculado(String barrioCalculado) {
		this.barrioCalculado = barrioCalculado;
	}

	public String getParroquiaCalculada() {
		return parroquiaCalculada;
	}

	public void setParroquiaCalculada(String parroquiaCalculada) {
		this.parroquiaCalculada = parroquiaCalculada;
	}

	public String getCodigoPostalCalculado() {
		return codigoPostalCalculado;
	}

	public void setCodigoPostalCalculado(String codigoPostalCalculado) {
		this.codigoPostalCalculado = codigoPostalCalculado;
	}

	public String getCallePrincipal() {
		return callePrincipal;
	}

	public void setCallePrincipal(String callePrincipal) {
		this.callePrincipal = callePrincipal;
	}

	public String getCalleSecundaria() {
		return calleSecundaria;
	}

	public void setCalleSecundaria(String calleSecundaria) {
		this.calleSecundaria = calleSecundaria;
	}

	public String getReferenciaUbicacion() {
		return referenciaUbicacion;
	}

	public void setReferenciaUbicacion(String referenciaUbicacion) {
		this.referenciaUbicacion = referenciaUbicacion;
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

	public String getCiudad() {
		return ciudad;
	}

	public void setCiudad(String ciudad) {
		this.ciudad = ciudad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getPais() {
		return pais;
	}

	public void setPais(String pais) {
		this.pais = pais;
	}

	public String getNumeracion() {
		return numeracion;
	}

	public void setNumeracion(String numeracion) {
		this.numeracion = numeracion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCelular() {
		return celular;
	}

	public void setCelular(String celular) {
		this.celular = celular;
	}

	public boolean isEsUltima() {
		return esUltima;
	}

	public void setEsUltima(boolean esUltima) {
		this.esUltima = esUltima;
	}

	public boolean isConfirmadoUsuario() {
		return confirmadoUsuario;
	}

	public void setConfirmadoUsuario(boolean confirmadoUsuario) {
		this.confirmadoUsuario = confirmadoUsuario;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	
}
