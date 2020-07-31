package com.opentach.server.process;

import java.util.Date;
import java.util.Set;

/**
 * Stores info about a File and its related contracts.
 *
 * @author rafael.lopez
 */
public class FileInfo {

	/** The id file. */
	private final Number		idFile;

	/** The file type. */
	private final String		fileType;

	/** The s contracts. */
	private final Set<String>	sContracts;

	/** The email. */
	private String	email;

	/** The analizar. */
	private boolean	analizar;

	/** The d extract. */
	private Date		dExtract;

	/** The id source. */
	private String	idSource;

	/** The s nomb procesado. */
	private String	sNombProcesado;

	/** The notif url. */
	private String	notifUrl;

	/** The is movil. */
	private boolean isMovil;

	/** The is already processed. */
	private boolean isAlreadyProcessed;

	private final int			priority;


	/**
	 * Instantiates a new file info.
	 *
	 * @param idFile
	 *            the id file
	 * @param fileType
	 *            the file type
	 * @param sContracts
	 *            the s contracts
	 */
	public FileInfo(Number idFile, String fileType, Set<String> sContracts, int priority) {
		this.priority = priority;
		this.idFile = idFile;
		this.sContracts = sContracts;
		this.fileType = fileType;
	}

	public int getPriority() {
		return this.priority;
	}

	/**
	 * Gets the id file.
	 *
	 * @return the id file
	 */
	public Number getIdFile() {
		return this.idFile;
	}

	/**
	 * Gets the contracts.
	 *
	 * @return the contracts
	 */
	public Set<String> getContracts() {
		return this.sContracts;
	}

	/**
	 * Gets the file type.
	 *
	 * @return the file type
	 */
	public String getFileType() {
		return this.fileType;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("FileInfo: [");
		sb.append(this.idFile.intValue()).append(']');
		if ((this.sContracts != null) && (this.sContracts.size() > 0)) {
			sb.append('@');
			sb.append(this.sContracts.toString());
		}
		sb.append(" e-mail = ").append(this.email).append("; Analizar = ").append(this.analizar);
		sb.append("; isMovil = ").append(this.isMovil);
		sb.append("; isAlreadyProcessed = ").append(this.isAlreadyProcessed);
		return sb.toString();
	}

	// compare only by idFile
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof FileInfo) {
			return this.idFile.equals(((FileInfo) obj).idFile);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		if (this.idFile == null) {
			return 0;
		}
		return this.idFile.hashCode();
	}

	/**
	 * Gets the nomb procesado.
	 *
	 * @return the nomb procesado
	 */
	public String getNombProcesado() {
		return this.sNombProcesado;
	}

	/**
	 * Sets the nomb procesado.
	 *
	 * @param nombProcesado
	 *            the new nomb procesado
	 */
	public void setNombProcesado(String nombProcesado) {
		this.sNombProcesado = nombProcesado;
	}

	/**
	 * Gets the d extract.
	 *
	 * @return the d extract
	 */
	public Date getDExtract() {
		return this.dExtract;
	}

	/**
	 * Sets the d extract.
	 *
	 * @param extract
	 *            the new d extract
	 */
	public void setDExtract(Date extract) {
		this.dExtract = extract;
	}

	/**
	 * Gets the email.
	 *
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}

	/**
	 * Sets the email.
	 *
	 * @param email
	 *            the new email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Checks if is analizar.
	 *
	 * @return true, if is analizar
	 */
	public boolean isAnalizar() {
		return this.analizar;
	}

	/**
	 * Sets the analizar.
	 *
	 * @param analizar
	 *            the new analizar
	 */
	public void setAnalizar(boolean analizar) {
		this.analizar = analizar;
	}

	/**
	 * Gets the id source.
	 *
	 * @return the id source
	 */
	public String getIdSource() {
		return this.idSource;
	}

	/**
	 * Sets the id source.
	 *
	 * @param idSource
	 *            the new id source
	 */
	public void setIdSource(String idSource) {
		this.idSource = idSource;
	}

	/**
	 * Sets the notif url.
	 *
	 * @param notifUrl
	 *            the new notif url
	 */
	public void setNotifUrl(String notifUrl) {
		this.notifUrl = notifUrl;
	}

	/**
	 * Gets the notif url.
	 *
	 * @return the notif url
	 */
	public String getNotifUrl() {
		return this.notifUrl;
	}

	/**
	 * Checks if is send notification by mail.
	 *
	 * @return true, if is send notification by mail
	 */
	public boolean isSendNotificationByMail() {
		return (this.getNotifUrl() != null) && this.getNotifUrl().contains("@");
	}



	/**
	 * Sets the already processed.
	 *
	 * @param isAlreadyProcessed
	 *            the new already processed
	 */
	public void setAlreadyProcessed(boolean isAlreadyProcessed) {
		this.isAlreadyProcessed = isAlreadyProcessed;
	}

	/**
	 * Sets the movil.
	 *
	 * @param isMovil
	 *            the new movil
	 */
	public void setMovil(boolean isMovil) {
		this.isMovil = isMovil;
	}

	/**
	 * Checks if is already processed.
	 *
	 * @return true, if is already processed
	 */
	public boolean isAlreadyProcessed() {
		return this.isAlreadyProcessed;
	}

	/**
	 * Checks if is movil.
	 *
	 * @return true, if is movil
	 */
	public boolean isMovil() {
		return this.isMovil;
	}
}