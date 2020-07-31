package com.opentach.client.comp.action;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.jee.common.tools.ObjectTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.modules.IMDataRoot;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public abstract class AbstractAutoSelectDriverVehicleValueChangeListener implements ValueChangeListener {

	private static final Logger	logger	= LoggerFactory.getLogger(AbstractAutoSelectDriverVehicleValueChangeListener.class);

	private final IMDataRoot	interactionManager;

	public AbstractAutoSelectDriverVehicleValueChangeListener(IMDataRoot im) {
		super();
		this.interactionManager = im;
	}

	@Override
	public void valueChanged(ValueEvent e) {
		try {
			if ((e.getType() == ValueEvent.USER_CHANGE) && this.interactionManager.checkRequiredVisibleDataFields(false)) {
				this.interactionManager.doOnQuery();
			} else if ((e.getNewValue() != e.getOldValue()) && (e.getNewValue() != null)) {
				for (String attr : new String[] { "IDCONDUCTOR", "MATRICULA" }) {
					UReferenceDataField ccrd = (UReferenceDataField) this.interactionManager.managedForm.getDataFieldReference(attr);
					if (ccrd != null) {
						ccrd.setValue(null);
						EntityResult res = ccrd.getCacheHelper().queryBy("CG_CONTRATO", e.getNewValue());
						if (res.calculateRecordNumber() == 1) {
							Map record = res.getRecordValues(0);
							Object value = record.get(attr);
							if (value == null) {
								value = ObjectTools.coalesce(record.get("IDORIGEN"), record.get("MATRICULA"), record.get("IDCONDUCTOR"));
							}
							ccrd.setValue(value);
							this.onChange();
						}
					}
				}
			}

		} catch (Exception ex) {
			MessageManager.getMessageManager().showExceptionMessage(ex, AbstractAutoSelectDriverVehicleValueChangeListener.logger);
		}
	}

	protected abstract void onChange() throws Exception;

}
