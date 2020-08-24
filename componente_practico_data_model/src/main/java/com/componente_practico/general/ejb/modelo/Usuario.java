package com.componente_practico.general.ejb.modelo;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.holalola.comida.pedido.ejb.modelo.OperadorProveedor;

@Entity
@NamedQueries({
		@NamedQuery(name = "usuario.porIdFacebook", query = "select o from Usuario o where o.idFacebook = :idFacebook"),
		@NamedQuery(name = "usuario.porId", query = "select o from Usuario o where o.id = :id"),
		@NamedQuery(name = "usuario.porOperador", query = "select o from Usuario o where o.operadorProveedor = :operadorProveedor"),
		@NamedQuery(name = "usuario.porFiltros", query = "select o from Usuario o where (COALESCE(o.nombreFacebook,'') || COALESCE(o.apellidoFacebook,'') like :nombre or COALESCE(o.nombres,'') || COALESCE(o.apellidos, '') like :nombre) and sexo like :sexo  "),
		@NamedQuery(name = "usuario.porFiltrosConFecNac", query = "select o from Usuario o where (COALESCE(o.nombreFacebook,'') || COALESCE(o.apellidoFacebook,'') like :nombre or COALESCE(o.nombres,'') || COALESCE(o.apellidos, '') like :nombre) and sexo like :sexo and fechaNacimiento = :fechaNacimiento"),
		@NamedQuery(name = "usuario.porIdentificacion", query= "select o from Usuario o where o.numeroIdentificacion = :numeroIdentificacion"),
		@NamedQuery(name = "usuario.porCalificacion", query= "select o from Usuario o where (o.nombreFacebook like :nombre or o.apellidoFacebook like :nombre or o.nombres like :nombre or o.apellidos like :nombre) and o.calificacionUsuario.id like :calificacionUsuario and sexo like :sexo"),
		@NamedQuery(name = "usuario.porCalificacionTodas", query= "select o from Usuario o where (o.nombreFacebook like :nombre or o.apellidoFacebook like :nombre or o.nombres like :nombre or o.apellidos like :nombre) and sexo like :sexo")
		})

@SuppressWarnings("serial")
public class Usuario extends EntityGeneral {

	@Column(name = "operador_id")
	private OperadorProveedor operadorProveedor;
	
	@Column(name = "nombre_facebook")
	private String nombreFacebook;

	@Column(name = "apellido_facebook")
	private String apellidoFacebook;

	@Column(name = "id_facebook")
	private String idFacebook;

	@Column(name = "fecha_verificacion")
	@Temporal(TemporalType.DATE)
	private Date fechaVerificacion;

	@Column(name = "url_foto_perfil")
	private String urlFotoPerfil;

	@Column(name = "locale")
	private String locale;

	@Column(name = "zona_horaria")
	private Integer zonaHoraria;

	@Column(name = "numero_identificacion")
	private String numeroIdentificacion;
	
	@Column(name = "tipo_identificacion")
	private String tipoIdentificacion;
	
	@Column(name = "celular_payphone")
	private String celularPayphone;
	
	@Column(name = "chat_activo")
	private Boolean chatActivo;
	
	@Column(name = "fecha_inactiva")
	private Date fechaInactivacion;
	
	@Column(name = "no_entiendo")
	private Integer noEntiendo;
	
	private String apellidos;
	private String nombres;
	private String email;
	private String sexo;
	
	@Column(name = "fechanacimiento")
	private Date fechaNacimiento;
	
	@ManyToOne
	@JoinColumn(name = "calificacion_id")
	private CalificacionUsuario calificacionUsuario;
	
	@Column(name = "bloqueado")
	private Boolean bloqueado;

	
	public Usuario() {}

	public Usuario(String nombreFacebook, String apellidoFacebook, String idFacebook, String urlFotoPerfil,
			String locale, Integer zonaHoraria, String sexo, Boolean chatActivo, Date fechaInactivacion, Date fechaNacimiento, Boolean bloqueado) {

		this.nombreFacebook = nombreFacebook;
		this.apellidoFacebook = apellidoFacebook;
		this.idFacebook = idFacebook;
		this.fechaVerificacion = new Date();
		this.urlFotoPerfil = urlFotoPerfil;
		this.locale = locale;
		this.zonaHoraria = zonaHoraria;
		this.sexo = sexo;
		this.chatActivo = chatActivo;
		this.fechaInactivacion = fechaInactivacion;
		this.bloqueado = bloqueado;
		if(fechaNacimiento != null)
			this.fechaNacimiento = fechaNacimiento;
	}
	
	public static Usuario getInstanciaVacia() {
		return new Usuario();
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, idFacebook);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Usuario)) return false;
			
		Usuario other = (Usuario) obj;
		return Objects.equals(this.id, other.id) && Objects.equals(this.idFacebook, other.idFacebook);
	}
	
	@Override
	public String toString() {
		if(this == null)
			return "";
		else
		{
			if((this.apellidos != null && this.apellidos.trim().length() > 0) || (this.nombres != null && this.nombres.trim().length() > 0))
				return (this.apellidos != null && this.apellidos.trim().length() > 0 ? this.apellidos.trim() : "") + " " + (this.nombres != null && this.nombres.trim().length() > 0 ? this.nombres.trim() : "");  
			else
				return (this.apellidoFacebook != null && this.apellidoFacebook.trim().length() > 0 ? this.apellidoFacebook.trim() : "") +" " + (this.nombreFacebook != null && this.nombreFacebook.trim().length() > 0 ? this.nombreFacebook.trim() : "");
		}
	}

	public String getNombreFacebook() {
		return nombreFacebook;
	}

	public void setNombreFacebook(String nombreFacebook) {
		this.nombreFacebook = nombreFacebook;
	}

	public String getApellidoFacebook() {
		return apellidoFacebook;
	}

	public void setApellidoFacebook(String apellidoFacebook) {
		this.apellidoFacebook = apellidoFacebook;
	}

	public String getIdFacebook() {
		return idFacebook;
	}

	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
	}

	public Date getFechaVerificacion() {
		return fechaVerificacion;
	}

	public void setFechaVerificacion(Date fechaVerificacion) {
		this.fechaVerificacion = fechaVerificacion;
	}

	public String getUrlFotoPerfil() {
		return urlFotoPerfil;
	}

	public void setUrlFotoPerfil(String urlFotoPerfil) {
		this.urlFotoPerfil = urlFotoPerfil;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public Integer getZonaHoraria() {
		return zonaHoraria;
	}

	public void setZonaHoraria(Integer zonaHoraria) {
		this.zonaHoraria = zonaHoraria;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}

	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}

	public String getTipoIdentificacion() {
		return tipoIdentificacion;
	}

	public void setTipoIdentificacion(String tipoIdentificacion) {
		this.tipoIdentificacion = tipoIdentificacion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCelularPayphone() {
		return celularPayphone;
	}

	public void setCelularPayphone(String celularPayphone) {
		this.celularPayphone = celularPayphone;
	}

	public Boolean getChatActivo() {
		return chatActivo;
	}

	public void setChatActivo(Boolean chatActivo) {
		this.chatActivo = chatActivo;
	}

	public Date getFechaInactivacion() {
		return fechaInactivacion;
	}

	public void setFechaInactivacion(Date fechaInactivacion) {
		this.fechaInactivacion = fechaInactivacion;
	}

	public Integer getNoEntiendo() {
		return noEntiendo;
	}

	public void setNoEntiendo(Integer noEntiendo) {
		this.noEntiendo = noEntiendo;
	}

	public OperadorProveedor getOperadorProveedor() {
		return operadorProveedor;
	}

	public void setOperadorProveedor(OperadorProveedor operadorProveedor) {
		this.operadorProveedor = operadorProveedor;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public CalificacionUsuario getCalificacionUsuario() {
		return calificacionUsuario;
	}

	public void setCalificacionUsuario(CalificacionUsuario calificacionUsuario) {
		this.calificacionUsuario = calificacionUsuario;
	}

	public Boolean getBloqueado() {
		return bloqueado;
	}

	public void setBloqueado(Boolean bloqueado) {
		this.bloqueado = bloqueado;
	}
	
	
	
}
