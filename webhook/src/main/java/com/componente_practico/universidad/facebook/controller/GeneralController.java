/**
 * 
 */
package com.componente_practico.universidad.facebook.controller;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.holalola.comida.facebook.controller.GeneralController;

/**
 * @author esteban
 * 
 */
@SuppressWarnings("serial")
public class GeneralController implements Serializable {


	private static Logger log = Logger.getLogger(GeneralController.class.getName());
	
	private String loggedUser;
	private Integer anioActual;
	private Integer idleTimeout;
	protected final String formatoFecha = "dd/MM/yyyy";
	protected final String formatoMoneda = "###,###,##0.00";
	protected Calendar hoy;

	public GeneralController() {
		hoy = Calendar.getInstance();
		anioActual = hoy.get(Calendar.YEAR);

		idleTimeout = 25 * 60 * 1000; // 25 minutos
		completarDatos();
	}

	public void completarDatos() {
		if (getHttpRequest().getRemoteUser() != null) {
			HttpServletRequest req = getHttpRequest();
			loggedUser = req.getRemoteUser().toUpperCase();
		} else {
			loggedUser = "No conectado";
		}


	}

	public void idleListener() {
		log.info("Finalizando sesion de " + loggedUser + " por inactividad");
		((HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false)).invalidate();
	}
	
	protected int obtenerIndiceSeleccionado() {
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		return Integer.parseInt(params.get("indiceSeleccionado"));
	}

	/**
	 * @return
	 */
	protected HttpServletRequest getHttpRequest() {
		return (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	}

	/**
	 * @return
	 */
	protected HttpServletResponse getHttpResponse() {
		return (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}

	/**
	 * @return
	 */
	protected String getContextPath() {
		return ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getContextPath();
	}

	/**
	 * @param summary
	 * @param detail
	 */
	protected void addErrorMessage(final String summary, final String detail) {
		FacesMessage errorMessage = new FacesMessage();
		errorMessage.setSummary(summary);
		errorMessage.setDetail(detail);
		errorMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(summary, errorMessage);
	}

	/**
	 * @param summary
	 * @param detail
	 */
	protected void addInfoMessage(final String summary, final String detail) {
		FacesMessage infoMessage = new FacesMessage();
		infoMessage.setSummary(summary);
		infoMessage.setDetail(detail);
		infoMessage.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(summary, infoMessage);
	}

	/**
	 * @param summary
	 * @param detail
	 */
	protected void addWarningMessage(final String summary, final String detail) {
		FacesMessage warningMessage = new FacesMessage();
		warningMessage.setSummary(summary);
		warningMessage.setDetail(detail);
		warningMessage.setSeverity(FacesMessage.SEVERITY_WARN);
		FacesContext.getCurrentInstance().addMessage(null, warningMessage);
	}
	

	/**
	 * @return the loggedUser
	 */
	public String getLoggedUser() {
		return loggedUser;
	}

	/**
	 * @param loggedUser
	 *            the loggedUser to set
	 */
	public void setLoggedUser(String loggedUser) {
		this.loggedUser = loggedUser;
	}

	/**
	 * @return the anioActual
	 */
	public Integer getAnioActual() {
		return anioActual;
	}

	/**
	 * @param anioActual
	 *            the anioActual to set
	 */
	public void setAnioActual(Integer anioActual) {
		this.anioActual = anioActual;
	}

	/**
	 * @return the idleTimeout
	 */
	public Integer getIdleTimeout() {
		return idleTimeout;
	}

	/**
	 * @param idleTimeout
	 *            the idleTimeout to set
	 */
	public void setIdleTimeout(Integer idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	/**
	 * @return the formatoFecha
	 */
	public String getFormatoFecha() {
		return formatoFecha;
	}


	/**
	 * @return the formatoMonefa
	 */
	public String getFormatoMoneda() {
		return formatoMoneda;
	}

}
