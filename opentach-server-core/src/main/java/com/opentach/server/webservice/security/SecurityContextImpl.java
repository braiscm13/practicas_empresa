package com.opentach.server.webservice.security;

public class SecurityContextImpl {

	private static final long	serialVersionUID	= 1;

	// ~ Instance fields
	// ================================================================================================

	private Integer				sessionId;

	// ~ Methods
	// ========================================================================================================

	public SecurityContextImpl() {
		this(-1);
	}

	public SecurityContextImpl(int startSession) {
		super();
		this.sessionId = startSession;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SecurityContextImpl) {
			SecurityContextImpl test = (SecurityContextImpl) obj;

			if ((this.getSessionId() == null) && (test.getSessionId() == null)) {
				return true;
			}

			if ((this.getSessionId() != null) && (test.getSessionId() != null) && this.getSessionId().equals(test.getSessionId())) {
				return true;
			}
		}

		return false;
	}

	public Integer getSessionId() {
		return this.sessionId;
	}

	@Override
	public int hashCode() {
		if (this.getSessionId() == null) {
			return -1;
		}
		return this.getSessionId().hashCode();
	}

	public void setAuthentication(int sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());

		if (this.getSessionId() == null) {
			sb.append(": Null authentication");
		} else {
			sb.append(": Authentication: ").append(this.getSessionId());
		}

		return sb.toString();
	}
}
