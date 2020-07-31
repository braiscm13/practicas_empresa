package com.opentach.client;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpentachClientPrimafrio extends OpentachClientApplication{

	private static final Logger	logger			= LoggerFactory.getLogger(OpentachClientPrimafrio.class);

	public OpentachClientPrimafrio(Hashtable p) throws Exception {
		super(p);

	}

	@Override
	protected void init(Hashtable params) {
		super.init(params);
	}




}
