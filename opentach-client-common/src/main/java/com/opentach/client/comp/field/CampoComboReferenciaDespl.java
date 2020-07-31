package com.opentach.client.comp.field;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class CampoComboReferenciaDespl extends UReferenceDataField {

	private static final Logger	logger	= LoggerFactory.getLogger(CampoComboReferenciaDespl.class);
	private boolean				singleSelectionMode;

	public CampoComboReferenciaDespl(Hashtable parameters) throws Exception {
		super(parameters);
		if (!this.singleSelectionMode) {
			super.setAdvancedQueryMode(true);
		}
	}

	@Override
	public void init(Hashtable parameters) {
		this.singleSelectionMode = ParseUtilsExtended.getBoolean(parameters.get("singleselectionmode"), false);
		super.init(parameters);
	}


	@Override
	public void setAdvancedQueryMode(boolean enabled) {
		if (!this.singleSelectionMode) {
			return;
		}
		super.setAdvancedQueryMode(enabled);
	}

}
