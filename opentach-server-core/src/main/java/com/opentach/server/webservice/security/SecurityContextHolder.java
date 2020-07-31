package com.opentach.server.webservice.security;

public class SecurityContextHolder {
	// ~ Static fields/initializers
	// =====================================================================================

	private static ThreadLocalSecurityContextHolderStrategy	strategy;
	private static int										initializeCount	= 0;

	static {
		SecurityContextHolder.initialize();
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * Explicitly clears the context value from the current thread.
	 */
	public static void clearContext() {
		SecurityContextHolder.strategy.clearContext();
	}

	/**
	 * Obtain the current <code>SecurityContext</code>.
	 *
	 * @return the security context (never <code>null</code>)
	 */
	public static SecurityContextImpl getContext() {
		return SecurityContextHolder.strategy.getContext();
	}

	/**
	 * Primarily for troubleshooting purposes, this method shows how many times the class has re-initialized its <code>SecurityContextHolderStrategy</code>.
	 *
	 * @return the count (should be one unless you've called {@link #setStrategyName(String)} to switch to an alternate strategy.
	 */
	public static int getInitializeCount() {
		return SecurityContextHolder.initializeCount;
	}

	private static void initialize() {
		SecurityContextHolder.strategy = new ThreadLocalSecurityContextHolderStrategy();
		SecurityContextHolder.initializeCount++;
	}

	/**
	 * Associates a new <code>SecurityContext</code> with the current thread of execution.
	 *
	 * @param context
	 *            the new <code>SecurityContext</code> (may not be <code>null</code>)
	 */
	public static void setContext(SecurityContextImpl context) {
		SecurityContextHolder.strategy.setContext(context);
	}


	/**
	 * Allows retrieval of the context strategy. See SEC-1188.
	 *
	 * @return the configured strategy for storing the security context.
	 */
	public static ThreadLocalSecurityContextHolderStrategy getContextHolderStrategy() {
		return SecurityContextHolder.strategy;
	}

	/**
	 * Delegates the creation of a new, empty context to the configured strategy.
	 */
	public static SecurityContextImpl createEmptyContext() {
		return SecurityContextHolder.strategy.createEmptyContext();
	}

	@Override
	public String toString() {
		return "SecurityContextHolder[strategy='" + SecurityContextHolder.strategy + "'; initializeCount=" + SecurityContextHolder.initializeCount + "]";
	}
}
