package com.opentach.server.webservice.security;

import com.ontimize.jee.common.tools.CheckingTools;

/**
 * A <code>ThreadLocal</code>-based implementation of
 * {@link SecurityContextHolderStrategy}.
 *
 * @author Ben Alex
 *
 * @see java.lang.ThreadLocal
 * @see org.springframework.security.core.context.web.SecurityContextPersistenceFilter
 */
final class ThreadLocalSecurityContextHolderStrategy {
	// ~ Static fields/initializers
	// =====================================================================================

	private static final ThreadLocal<SecurityContextImpl> contextHolder = new ThreadLocal<SecurityContextImpl>();

	// ~ Methods
	// ========================================================================================================

	public void clearContext() {
		ThreadLocalSecurityContextHolderStrategy.contextHolder.remove();
	}

	public SecurityContextImpl getContext() {
		SecurityContextImpl ctx = ThreadLocalSecurityContextHolderStrategy.contextHolder.get();

		if (ctx == null) {
			ctx = this.createEmptyContext();
			ThreadLocalSecurityContextHolderStrategy.contextHolder.set(ctx);
		}

		return ctx;
	}

	public void setContext(SecurityContextImpl context) {
		CheckingTools.failIf(context == null, "Only non-null SecurityContext instances are permitted");
		ThreadLocalSecurityContextHolderStrategy.contextHolder.set(context);
	}

	public SecurityContextImpl createEmptyContext() {
		return new SecurityContextImpl();
	}
}
