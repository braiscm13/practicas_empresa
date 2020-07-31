package com.opentach.common.mailmanager.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

public class MailAttachment implements Serializable {
	private BigDecimal	matId;
	private BigDecimal	maiId;
	private byte[]		matContent;
	private String		matName;
	private BigDecimal	matSize;

	public MailAttachment() {
		super();
	}

	public BigDecimal getMatId() {
		return this.matId;
	}

	public void setMatId(BigDecimal matId) {
		this.matId = matId;
	}

	public BigDecimal getMaiId() {
		return this.maiId;
	}

	public void setMaiId(BigDecimal maiId) {
		this.maiId = maiId;
	}

	public byte[] getMatContent() {
		return this.matContent;
	}

	public void setMatContent(byte[] matContent) {
		this.matContent = matContent;
	}

	public String getMatName() {
		return this.matName;
	}

	public void setMatName(String matName) {
		this.matName = matName;
	}

	public BigDecimal getMatSize() {
		return this.matSize;
	}

	public void setMatSize(BigDecimal matSize) {
		this.matSize = matSize;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.maiId == null) ? 0 : this.maiId.hashCode());
		result = (prime * result) + Arrays.hashCode(this.matContent);
		result = (prime * result) + ((this.matId == null) ? 0 : this.matId.hashCode());
		result = (prime * result) + ((this.matName == null) ? 0 : this.matName.hashCode());
		result = (prime * result) + ((this.matSize == null) ? 0 : this.matSize.hashCode());
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
		MailAttachment other = (MailAttachment) obj;
		if (this.maiId == null) {
			if (other.maiId != null) {
				return false;
			}
		} else if (!this.maiId.equals(other.maiId)) {
			return false;
		}
		if (!Arrays.equals(this.matContent, other.matContent)) {
			return false;
		}
		if (this.matId == null) {
			if (other.matId != null) {
				return false;
			}
		} else if (!this.matId.equals(other.matId)) {
			return false;
		}
		if (this.matName == null) {
			if (other.matName != null) {
				return false;
			}
		} else if (!this.matName.equals(other.matName)) {
			return false;
		}
		if (this.matSize == null) {
			if (other.matSize != null) {
				return false;
			}
		} else if (!this.matSize.equals(other.matSize)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MailAttachment [matId=" + this.matId + ", maiId=" + this.maiId + ", matContent=" + Arrays
				.toString(this.matContent) + ", matName=" + this.matName + ", matSize=" + this.matSize + "]";
	}

}
