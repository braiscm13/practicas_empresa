package com.opentach.common.mailmanager.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class Mail implements Serializable {
	private BigDecimal	maiId;
	private BigDecimal	macId;
	private BigDecimal	mfdId;
	private String		maiSubject;
	private String		maiBody;
	private String		maiTo;
	private String		maiCc;
	private String		maiBcc;
	private Date		maiDate;
	private String		maiSent;
	private String		cif;

	public Mail() {
		super();
	}

	public BigDecimal getMaiId() {
		return this.maiId;
	}

	public void setMaiId(BigDecimal maiId) {
		this.maiId = maiId;
	}

	public BigDecimal getMacId() {
		return this.macId;
	}

	public void setMacId(BigDecimal macId) {
		this.macId = macId;
	}

	public BigDecimal getMfdId() {
		return this.mfdId;
	}

	public void setMfdId(BigDecimal mfdId) {
		this.mfdId = mfdId;
	}

	public String getMaiSubject() {
		return this.maiSubject;
	}

	public void setMaiSubject(String maiSubject) {
		this.maiSubject = maiSubject;
	}

	public String getMaiBody() {
		return this.maiBody;
	}

	public void setMaiBody(String maiBody) {
		this.maiBody = maiBody;
	}

	public String getMaiTo() {
		return this.maiTo;
	}

	public void setMaiTo(String maiTo) {
		this.maiTo = maiTo;
	}

	public String getMaiCc() {
		return this.maiCc;
	}

	public void setMaiCc(String maiCc) {
		this.maiCc = maiCc;
	}

	public String getMaiBcc() {
		return this.maiBcc;
	}

	public void setMaiBcc(String maiBcc) {
		this.maiBcc = maiBcc;
	}

	public Date getMaiDate() {
		return this.maiDate;
	}

	public void setMaiDate(Date maiDate) {
		this.maiDate = maiDate;
	}


	public String getMaiSent() {
		return this.maiSent;
	}

	public void setMaiSent(String maiSent) {
		this.maiSent = maiSent;
	}

	public String getCif() {
		return this.cif;
	}

	public void setCif(String cif) {
		this.cif = cif;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.cif == null) ? 0 : this.cif.hashCode());
		result = (prime * result) + ((this.macId == null) ? 0 : this.macId.hashCode());
		result = (prime * result) + ((this.maiBcc == null) ? 0 : this.maiBcc.hashCode());
		result = (prime * result) + ((this.maiBody == null) ? 0 : this.maiBody.hashCode());
		result = (prime * result) + ((this.maiCc == null) ? 0 : this.maiCc.hashCode());
		result = (prime * result) + ((this.maiDate == null) ? 0 : this.maiDate.hashCode());
		result = (prime * result) + ((this.maiId == null) ? 0 : this.maiId.hashCode());
		result = (prime * result) + ((this.maiSent == null) ? 0 : this.maiSent.hashCode());
		result = (prime * result) + ((this.maiSubject == null) ? 0 : this.maiSubject.hashCode());
		result = (prime * result) + ((this.maiTo == null) ? 0 : this.maiTo.hashCode());
		result = (prime * result) + ((this.mfdId == null) ? 0 : this.mfdId.hashCode());
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
		Mail other = (Mail) obj;
		if (this.cif == null) {
			if (other.cif != null) {
				return false;
			}
		} else if (!this.cif.equals(other.cif)) {
			return false;
		}
		if (this.macId == null) {
			if (other.macId != null) {
				return false;
			}
		} else if (!this.macId.equals(other.macId)) {
			return false;
		}
		if (this.maiBcc == null) {
			if (other.maiBcc != null) {
				return false;
			}
		} else if (!this.maiBcc.equals(other.maiBcc)) {
			return false;
		}
		if (this.maiBody == null) {
			if (other.maiBody != null) {
				return false;
			}
		} else if (!this.maiBody.equals(other.maiBody)) {
			return false;
		}
		if (this.maiCc == null) {
			if (other.maiCc != null) {
				return false;
			}
		} else if (!this.maiCc.equals(other.maiCc)) {
			return false;
		}
		if (this.maiDate == null) {
			if (other.maiDate != null) {
				return false;
			}
		} else if (!this.maiDate.equals(other.maiDate)) {
			return false;
		}
		if (this.maiId == null) {
			if (other.maiId != null) {
				return false;
			}
		} else if (!this.maiId.equals(other.maiId)) {
			return false;
		}
		if (this.maiSent == null) {
			if (other.maiSent != null) {
				return false;
			}
		} else if (!this.maiSent.equals(other.maiSent)) {
			return false;
		}
		if (this.maiSubject == null) {
			if (other.maiSubject != null) {
				return false;
			}
		} else if (!this.maiSubject.equals(other.maiSubject)) {
			return false;
		}
		if (this.maiTo == null) {
			if (other.maiTo != null) {
				return false;
			}
		} else if (!this.maiTo.equals(other.maiTo)) {
			return false;
		}
		if (this.mfdId == null) {
			if (other.mfdId != null) {
				return false;
			}
		} else if (!this.mfdId.equals(other.mfdId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Mail [maiId=" + this.maiId + ", macId=" + this.macId + ", mfdId=" + this.mfdId + ", maiSubject=" + this.maiSubject + ", maiBody=" + this.maiBody + ", maiTo=" + this.maiTo + ", maiCc=" + this.maiCc + ", maiBcc=" + this.maiBcc + ", maiDate=" + this.maiDate + ", maiSent=" + this.maiSent + ", cif=" + this.cif + "]";
	}

}
