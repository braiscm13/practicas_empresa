package com.opentach.server.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ontimize.db.TableEntity;
import com.ontimize.jee.common.exceptions.OntimizeJEEException;
import com.ontimize.jee.common.settings.AbstractSettingsHelper;
import com.opentach.server.entities.EPreferenciasServidor;
import com.opentach.server.entities.EPreferenciasServidor.IPreferenceChangeListener;
import com.opentach.server.util.spring.OpentachLocatorReferencer;

@Service("OpentachSettingsService")
public class OpentachSettingsServiceImpl extends AbstractSettingsHelper {

	@Autowired
	private OpentachLocatorReferencer locator;

	@Override
	protected String query(String key) throws OntimizeJEEException {
		EPreferenciasServidor ePreferencias = (EPreferenciasServidor) this.locator.getLocator().getEntityReferenceFromServer("EPreferenciasServidor");
		return ePreferencias.getValue(key, TableEntity.getEntityPrivilegedId(ePreferencias));
	}

	public void addPropertyChangeListener(IPreferenceChangeListener listener) {
		EPreferenciasServidor ePreferencias = (EPreferenciasServidor) this.locator.getLocator().getEntityReferenceFromServer("EPreferenciasServidor");
		ePreferencias.addPropertyChangeListener(listener);
	}

}
