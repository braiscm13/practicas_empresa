package com.opentach.common.mailmanager.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public class MailAccount implements Serializable {
	private BigDecimal	macId;
	private String		usuario;
	private String		macName;

	public MailAccount(BigDecimal macId, String usuario, String macName) {
		super();
		this.macId = macId;
		this.usuario = usuario;
		this.macName = macName;
	}

	public MailAccount() {
		super();
	}

	public BigDecimal getMacId() {
		return this.macId;
	}

	public void setMacId(BigDecimal macId) {
		this.macId = macId;
	}

	public String getUsuario() {
		return this.usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getMacName() {
		return this.macName;
	}

	public void setMacName(String macName) {
		this.macName = macName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.macId == null) ? 0 : this.macId.hashCode());
		result = (prime * result) + ((this.macName == null) ? 0 : this.macName.hashCode());
		result = (prime * result) + ((this.usuario == null) ? 0 : this.usuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		MailAccount other = (MailAccount) obj;
		if (this.macId == null) {
			if (other.macId != null) {
				return false;
			}
		} else if (!this.macId.equals(other.macId)) {
			return false;
		}
		if (this.macName == null) {
			if (other.macName != null) {
				return false;
			}
		} else if (!this.macName.equals(other.macName)) {
			return false;
		}
		if (this.usuario == null) {
			if (other.usuario != null) {
				return false;
			}
		} else if (!this.usuario.equals(other.usuario)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MailAccount [macId=" + this.macId + ", usuario=" + this.usuario + ", macName=" + this.macName + "]";
	}
}
