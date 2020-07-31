package com.opentach.server.mail;

import java.io.File;

public class MailInfo {

	protected String	mailServer;
	protected String	user;
	protected String	pass;
	protected boolean	authenticationRequired;
	protected boolean 	starttlsEnabled;
	protected String[]	to;
	protected String[]	cc;
	protected String[]	bcc;
	protected String	subject;
	protected String	content;
	protected String[]	attchNames;
	protected File[]	attchFiles;
	protected String	contentType;
	protected String	ackAddress;

	public MailInfo() {
		super();
	}

	public File[] getAttchFiles() {
		return this.attchFiles;
	}

	public void setAttchFiles(File[] attchFiles) {
		this.attchFiles = attchFiles;
	}

	public String getMailServer() {
		return this.mailServer;
	}

	public void setMailServer(String mailServer) {
		this.mailServer = mailServer;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return this.pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public boolean isAuthenticationRequired() {
		return this.authenticationRequired;
	}

	public void setAuthenticationRequired(boolean authenticationRequired) {
		this.authenticationRequired = authenticationRequired;
	}
	
	public boolean isStarttlsEnabled() {
		return this.starttlsEnabled;
	}

	public void setStarttlsEnabled(boolean stlsEnabled) {
		this.starttlsEnabled = stlsEnabled;
	}

	public String[] getTo() {
		return this.to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String[] getCc() {
		return this.cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String[] getBcc() {
		return this.bcc;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	public String getSubject() {
		return this.subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getAttchNames() {
		return this.attchNames;
	}

	public void setAttchNames(String[] attchNames) {
		this.attchNames = attchNames;
	}

	public String getContentType() {
		return this.contentType;
	}

	public void setContentType(String ctype) {
		this.contentType = ctype;
	}

	public String getAckAddress() {
		return this.ackAddress;
	}

	public void setAckAddress(String ackAddress) {
		this.ackAddress = ackAddress;
	}
}