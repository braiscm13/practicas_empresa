package com.opentach.client.modules.chart;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.annotation.FormComponent;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.buttons.IUFormComponent;
import com.utilmize.client.gui.buttons.UButton;

public class IMGraficaCondPrimafrioReportBuilderActionListener extends IMGraficaCondReportBuilderActionListener {

	private static final Logger		logger	= LoggerFactory.getLogger(IMGraficaCondPrimafrioReportBuilderActionListener.class);

	@FormComponent(attr = "CIF_CERTIFICADO")
	private UReferenceDataField	cifCertificado;

	public IMGraficaCondPrimafrioReportBuilderActionListener() throws Exception {}

	public IMGraficaCondPrimafrioReportBuilderActionListener(AbstractButton button, IUFormComponent formComponent, Hashtable params) throws Exception {
		super(button, formComponent, params);
	}

	public IMGraficaCondPrimafrioReportBuilderActionListener(Hashtable params) throws Exception {
		super(params);
	}

	public IMGraficaCondPrimafrioReportBuilderActionListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected File generateReport() throws Exception {
		FileOutputStream fos = null;
		try {
			File ftemp = File.createTempFile("GRAFICA", ".pdf");
			fos = new FileOutputStream(ftemp);

			Map<String, Object> companyData = this.cif.getCodeValues(this.cifCertificado.getValue());
			Map<String, Object> driverData = this.dni.getCodeValues(this.dni.getValue());
			Map<String, Object> driverNumTarj = this.tarj.getValuesToCode(this.tarj.getValue());

			this.generateReport(fos, companyData, driverData, driverNumTarj);
			fos.close();
			return ftemp;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception ex) {
				}
			}
		}
	}
}
