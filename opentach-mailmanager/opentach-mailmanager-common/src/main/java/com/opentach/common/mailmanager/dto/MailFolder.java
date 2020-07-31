package com.opentach.common.mailmanager.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MailFolder implements Serializable {
	private BigDecimal				mfdId;
	private BigDecimal				macId;
	private String					mfdName;
	private List<MailFolder>	children;
	private MailFolder			parent;

	public MailFolder(BigDecimal mfdId, BigDecimal macId, String mfdName, List<MailFolder> children, MailFolder parent) {
		super();
		this.mfdId = mfdId;
		this.macId = macId;
		this.mfdName = mfdName;
		this.parent = parent;
		this.setChildren(children);
	}

	public MailFolder() {
		super();
	}

	public BigDecimal getMfdId() {
		return this.mfdId;
	}

	public BigDecimal getMacId() {
		return this.macId;
	}

	public String getMfdName() {
		return this.mfdName;
	}

	public List<MailFolder> getChildren() {
		return this.children;
	}

	public void setMfdId(BigDecimal mfdId) {
		this.mfdId = mfdId;
	}

	public void setMacId(BigDecimal macId) {
		this.macId = macId;
	}

	public void setMfdName(String mfdName) {
		this.mfdName = mfdName;
	}

	public MailFolder getParent() {
		return this.parent;
	}

	public void setParent(MailFolder parent) {
		this.parent = parent;
	}

	public void setChildren(List<MailFolder> children) {
		this.children = children;
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
	}

	public void addChild(MailFolder child) {
		this.children.add(child);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.children == null) ? 0 : this.children.hashCode());
		result = (prime * result) + ((this.macId == null) ? 0 : this.macId.hashCode());
		result = (prime * result) + ((this.mfdId == null) ? 0 : this.mfdId.hashCode());
		result = (prime * result) + ((this.mfdName == null) ? 0 : this.mfdName.hashCode());
		result = (prime * result) + ((this.parent == null) ? 0 : this.parent.hashCode());
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
		MailFolder other = (MailFolder) obj;
		if (this.children == null) {
			if (other.children != null) {
				return false;
			}
		} else if (!this.children.equals(other.children)) {
			return false;
		}
		if (this.macId == null) {
			if (other.macId != null) {
				return false;
			}
		} else if (!this.macId.equals(other.macId)) {
			return false;
		}
		if (this.mfdId == null) {
			if (other.mfdId != null) {
				return false;
			}
		} else if (!this.mfdId.equals(other.mfdId)) {
			return false;
		}
		if (this.mfdName == null) {
			if (other.mfdName != null) {
				return false;
			}
		} else if (!this.mfdName.equals(other.mfdName)) {
			return false;
		}
		if (this.parent == null) {
			if (other.parent != null) {
				return false;
			}
		} else if (!this.parent.equals(other.parent)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MailFolder [mfdId=" + this.mfdId + ", macId=" + this.macId + ", mfdName=" + this.mfdName + ", children=" + this.children + ", parent=" + this.parent + "]";
	}



}
