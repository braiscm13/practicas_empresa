package com.opentach.server.util.dbaspintegration;

import java.util.Map;

public interface IDbFunction<T> {
	public T execute(Map<String, Object> parameters);
}
