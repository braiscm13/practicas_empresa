package com.opentach.common.sessionstatus;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * The Class AbstractStatusDto.
 */
public abstract class AbstractStatusDto implements Serializable {

	/** The current date. */
	private Date	currentDate;

	/** The session id. */
	private int		sessionId;

	/** The startup time. */
	private Date	startupTime;

	/** The startup time. */
	private Date	pingDate;

	/** The user. */
	private String	user;

	/** The last access time. */
	private Date	lastAccessTime;

	/** The level. */
	private String	level;

	/** The companies. */
	private String	companies;

	/** The source address. */
	private String	sourceAddress;

	/** The saved. */
	private boolean	saved;

	private String	version;

	/**
	 * Instantiates a new abstract status dto.
	 */
	public AbstractStatusDto() {
		super();
		this.saved = false;
		this.pingDate = null;
		try {
			this.sourceAddress = String.valueOf(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			this.sourceAddress = null;
		}
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return this.version;
	}

	/**
	 * Checks if is saved.
	 *
	 * @return true, if is saved
	 */
	public boolean isSaved() {
		return this.saved;
	}

	/**
	 * Sets the saved.
	 *
	 * @param saved
	 *            the new saved
	 */
	public void setSaved(boolean saved) {
		this.saved = saved;
	}

	/**
	 * Gets the source address.
	 *
	 * @return the source address
	 */
	public String getSourceAddress() {
		return this.sourceAddress;
	}

	/**
	 * Gets the current date.
	 *
	 * @return the current date
	 */
	public Date getCurrentDate() {
		return this.currentDate;
	}

	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public int getSessionId() {
		return this.sessionId;
	}

	/**
	 * Gets the startup time.
	 *
	 * @return the startup time
	 */
	public Date getStartupTime() {
		return this.startupTime;
	}

	/**
	 * Gets the user.
	 *
	 * @return the user
	 */
	public String getUser() {
		return this.user;
	}

	/**
	 * Sets the current date.
	 *
	 * @param currentDate
	 *            the new current date
	 */
	public void setCurrentDate(Date currentDate) {
		this.currentDate = currentDate;
	}

	/**
	 * Sets the session id.
	 *
	 * @param sessionId
	 *            the new session id
	 */
	public void setSessionId(int sessionId) {
		this.sessionId = sessionId;
	}

	/**
	 * Sets the startup time.
	 *
	 * @param startupTime
	 *            the new startup time
	 */
	public void setStartupTime(Date startupTime) {
		this.startupTime = startupTime;
	}

	/**
	 * Sets the user.
	 *
	 * @param user
	 *            the new user
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Sets the last access time.
	 *
	 * @param time
	 *            the new last access time
	 */
	public void setLastAccessTime(Date time) {
		this.lastAccessTime = time;
	}

	/**
	 * Gets the last access time.
	 *
	 * @return the last access time
	 */
	public Date getLastAccessTime() {
		return this.lastAccessTime;
	}

	/**
	 * Sets the level.
	 *
	 * @param level
	 *            the new level
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * Gets the level.
	 *
	 * @return the level
	 */
	public String getLevel() {
		return this.level;
	}

	/**
	 * Sets the companies.
	 *
	 * @param companies
	 *            the new companies
	 */
	public void setCompanies(String companies) {
		this.companies = companies;
	}

	/**
	 * Gets the companies.
	 *
	 * @return the companies
	 */
	public String getCompanies() {
		return this.companies;
	}

	public abstract String getApp();

	public void setPingTime(Date date) {
		this.pingDate = date;
	}

	public Date getPingDate() {
		return this.pingDate;
	}
}
