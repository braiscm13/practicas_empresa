package com.opentach.server.http.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

public class SessionDisableFilter implements Filter {

	public SessionDisableFilter() {
		super();
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) request) {
			@Override
			public HttpSession getSession() {
				return null;
			}

			@Override
			public HttpSession getSession(boolean create) {
				return null;
			}
		}, response);

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
