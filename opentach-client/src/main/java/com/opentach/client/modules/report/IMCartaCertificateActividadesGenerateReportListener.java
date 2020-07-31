package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.DataFile;
import com.ontimize.gui.Form;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.certificadoActividades.ICertificadoActividadesService;
import com.opentach.common.company.naming.CompanyNaming;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMCartaCertificateActividadesGenerateReportListener extends AbstractActionListenerButton {

	private static final Logger logger = LoggerFactory.getLogger(IMCartaCertificateActividadesGenerateReportListener.class);
	private OpentachClientLocator ocl = null;

	public IMCartaCertificateActividadesGenerateReportListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		this.ocl = (OpentachClientLocator) this.getReferenceLocator();
		if (this.getForm().existEmptyRequiredDataField()) {
			this.getForm().message("", Form.INFORMATION_MESSAGE);
			return;
		}
		if (IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("TRABAJODIST")!=null) {
			this.updateUser();
			this.updateConductor();
		}
		this.generateTemplate();
	}

	private void generateTemplate() {
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				Hashtable params = null;


				if (IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("TRABAJODIST")!=null) {
					params = IMCartaCertificateActividadesGenerateReportListener.this.getParams();
				} else {
					params = IMCartaCertificateActividadesGenerateReportListener.this.getParamsQuery();
				}


				//				JasperPrint bb = ((OpentachClientLocator) IMCartaCertificateActividadesGenerateReportListener.this.getReferenceLocator()).getRemoteService(ICertificadoActividadesService.class)
				//						.generateCertificadoActividadesServiceReport(params, IMCartaCertificateActividadesGenerateReportListener.this.getForm().getLocale(), IMCartaCertificateActividadesGenerateReportListener.this.getSessionId());
				//				return bb;


				BytesBlock bb = ((OpentachClientLocator) IMCartaCertificateActividadesGenerateReportListener.this.getReferenceLocator()).getRemoteService(ICertificadoActividadesService.class)
						.generateCertificadoActividadesServiceReport(params, IMCartaCertificateActividadesGenerateReportListener.this.getForm().getLocale(), IMCartaCertificateActividadesGenerateReportListener.this.getSessionId());
				File file = File.createTempFile("InformeCertificadoActividades", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(bb.getBytes()), file);


				//				final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(IMCartaCertificateActividadesGenerateReportListener.this.getForm());
				//				JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("Vista_previa"), jd, jp);
				//				jv.setVisible(true);



				Hashtable<String, Object> avCertif = new Hashtable<String, Object>();
				avCertif.put("NOMBRE", "certifActiv.pdf");
				DataFile df = new DataFile("certifActiv.pdf", bb);
				avCertif.put("FICH_CERTIF", df);
				avCertif.put(OpentachFieldNames.NUMREQ_FIELD, params.get("NUMREQ"));
				avCertif.put(OpentachFieldNames.IDCONDUCTOR_FIELD, params.get("IDCONDUCTOR"));
				if (params.get("OBSR") != null) {
					avCertif.put("OBSR", params.get("OBSR"));
				}
				EntityReferenceLocator loc =  IMCartaCertificateActividadesGenerateReportListener.this.getReferenceLocator();
				Entity ent = loc.getEntityReference("ECertifActividades");
				EntityResult result = ent.insert(avCertif, loc.getSessionId());
				if (result.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
					throw new Exception();
				}

				return file;
			}

			@Override
			protected void done() {
				try {
					File file = this.uget();
					//					final JFrame jd = (JFrame) SwingUtilities.getWindowAncestor(IMCartaCertificateActividadesGenerateReportListener.this.getForm());
					//					JRDialogViewer jv = JRDialogViewer.getJasperViewer(ApplicationManager.getTranslation("Informe"), jd, jp);
					//					jv.setVisible(true);
					Desktop.getDesktop().open(file);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(new Exception("M_PROCESO_INCORRECTO_GENERANDO_INFORME", error),
							IMCartaCertificateActividadesGenerateReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());



	}

	private Hashtable getParams () {


		Hashtable<String, String> params = new Hashtable<String, String> ();
		UReferenceDataField cif = (UReferenceDataField)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("CIF");
		cif.getCodeValues(cif.getValue());
		this.safePutFormField(params,"NUMREQ", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CG_CONTRATO"));
		this.safePutFormField(params,"CIF", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CIF"));
		this.safePutFormField(params,"NOMB", (String)cif.getCodeValues(cif.getValue()).get("NOMB"));
		if (cif.getCodeValues(cif.getValue()).get("PREFIJO")!=null) {
			if (cif.getCodeValues(cif.getValue()).get("TELF")!=null) {
				this.safePutFormField(params,"TELF", (String)cif.getCodeValues(cif.getValue()).get("PREFIJO") +""+(String)cif.getCodeValues(cif.getValue()).get("TELF"));
			}else {
				this.safePutFormField(params,"TELF", (String)cif.getCodeValues(cif.getValue()).get("TELF"));
			}
			if (cif.getCodeValues(cif.getValue()).get("FAX")!=null) {
				this.safePutFormField(params,"FAX", (String)cif.getCodeValues(cif.getValue()).get("PREFIJO") +""+(String)cif.getCodeValues(cif.getValue()).get("FAX"));
			}else {
				this.safePutFormField(params,"FAX", (String)cif.getCodeValues(cif.getValue()).get("FAX"));
			}
		}else {
			this.safePutFormField(params,"TELF", (String)cif.getCodeValues(cif.getValue()).get("TELF"));
			this.safePutFormField(params,"FAX", (String)cif.getCodeValues(cif.getValue()).get("FAX"));
		}
		this.safePutFormField(params,"EMAIL", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("EMAIL"));
		this.safePutFormField(params,"DIRECCION", (String)cif.getCodeValues(cif.getValue()).get("DIRECCION"));
		this.safePutFormField(params,"POBL",(String) cif.getCodeValues(cif.getValue()).get("POBL"));
		this.safePutFormField(params,"PAIS",(String) cif.getCodeValues(cif.getValue()).get("PAIS"));
		this.safePutFormField(params,"CG_POSTAL", (String)cif.getCodeValues(cif.getValue()).get("CG_POSTAL"));
		this.safePutFormField(params,"APELLIDOS_FIRMA", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("APELLIDOS_FIRMA"));
		this.safePutFormField(params,"NOMBRE_FIRMA", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("NOMBRE_FIRMA"));
		this.safePutFormField(params,"CARGO", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CARGO"));

		UReferenceDataField idconductor = (UReferenceDataField)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("IDCONDUCTOR");
		this.safePutFormField(params,"IDCONDUCTOR", (String) idconductor.getCodeValues(idconductor.getValue()).get("DNI"));
		this.safePutFormField(params,"NOMBRE", (String) idconductor.getCodeValues(idconductor.getValue()).get("NOMBRE"));
		this.safePutFormField(params,"APELLIDOS", (String) idconductor.getCodeValues(idconductor.getValue()).get("APELLIDOS"));

		Date f_nac = (Date)idconductor.getCodeValues(idconductor.getValue()).get("F_NAC");
		if (f_nac!=null) {
			params.put("F_NAC", new SimpleDateFormat("dd/MM/yyyy").format(f_nac));
		}else {
			params.put("F_NAC", "");
		}

		Date f_trabajo = (Date)idconductor.getCodeValues(idconductor.getValue()).get("F_TRABAJO");
		if (f_trabajo!=null) {
			params.put("F_TRABAJO", new SimpleDateFormat("dd/MM/yyyy").format(f_trabajo));
		}else {
			params.put("F_TRABAJO", "");
		}

		Date f_ini = (Date)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("F_INI");
		if (f_ini!=null) {
			params.put("F_INI", new SimpleDateFormat("HH:mm dd/MM/yyyy").format(f_ini));
		}else {
			params.put("F_INI", "");
		}

		Date f_fin = (Date)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("F_FIN");
		if (f_fin!=null) {
			params.put("F_FIN", new SimpleDateFormat("HH:mm dd/MM/yyyy").format(f_fin));
		}else {
			params.put("F_FIN", "");
		}

		if (1==((Integer)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("BAJAENFERMEDAD")).intValue()) {
			params.put("BAJAENFERMEDAD", "S");
		} else {
			params.put("BAJAENFERMEDAD", "N");
		}

		if (1==((Integer) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("VACACIONES")).intValue()) {
			params.put("VACACIONES", "S");
		} else {
			params.put("VACACIONES", "N");
		}

		if (1==((Integer) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("PERMISO")).intValue()) {
			params.put("PERMISO", "S");
		} else {
			params.put("PERMISO", "N");
		}

		if (1==((Integer) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CONDEXCLUIDO")).intValue()) {
			params.put("CONDEXCLUIDO", "S");
		} else {
			params.put("CONDEXCLUIDO", "N");
		}

		if (1==((Integer)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("TRABAJODIST")).intValue()) {
			params.put("TRABAJODIST", "S");
		} else {
			params.put("TRABAJODIST", "N");
		}

		if (1==((Integer)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("DISPONIBLE")).intValue()) {
			params.put("DISPONIBLE", "S");
		} else {
			params.put("DISPONIBLE", "N");
		}

		this.safePutFormField(params,"LUGAR", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("LUGAR"));
		this.safePutFormField(params,"LUGAR2", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("LUGAR2"));


		Date fecha = (Date)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("FECHA");
		if (fecha!=null) {
			params.put("FECHA", new SimpleDateFormat("dd/MM/yyyy").format(fecha));
		}else {
			params.put("FECHA", "");
		}

		this.safePutFormField(params,"OBSR", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("OBSR"));
		return params;
	}


	private Hashtable getParamsQuery() throws Exception {

		Hashtable<String, String> params = new Hashtable<String, String>();

		Date fini = null;
		Date ffin = null;

		Entity eDFemp = this.ocl.getEntityReference(CompanyNaming.ENTITY);
		UReferenceDataField cCif = (UReferenceDataField) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("CIF");;
		final String cif = (String) cCif.getValue();
		final Map htRow = cCif.getCodeValues(cif);
		final String empresa = (String) htRow.get("NOMB");
		this.safePutFormField(params,"NUMREQ", (String)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CG_CONTRATO"));
		this.safePutFormField(params,"CIF", cif);
		this.safePutFormField(params,"NOMB", empresa);
		this.safePutFormField(params,"OBSR", (String)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("OBSR"));

		String pais = (String) htRow.get("PAIS");


		this.safePutFormField(params,"DIRECCION", (String) htRow.get("DIRECCION"));
		this.safePutFormField(params,"POBL",(String) htRow.get("POBL"));
		this.safePutFormField(params,"PAIS",(String) htRow.get("PAIS"));
		this.safePutFormField(params,"CG_POSTAL", (String)htRow.get("CG_POSTAL"));

		String telef = (String) htRow.get("TELF");
		String prefijo = (String) htRow.get("PREFIJO");
		if (telef!=null) {
			if (prefijo != null) {
		
			if (!telef.startsWith("00")) {
				this.safePutFormField(params,"TELF",  prefijo + " " + telef);
			}else {
				this.safePutFormField(params,"TELF", telef);
			}
			}else {
				this.safePutFormField(params,"TELF", telef);
			}
		}else this.safePutFormField(params,"TELF", "");
		String fax = (String) htRow.get("FAX");
		if (fax!=null) {
			if (prefijo != null) {
				if (!fax.startsWith("00")) {
					this.safePutFormField(params,"FAX", prefijo + " " + fax);
				}else {
					this.safePutFormField(params,"FAX", fax);
				}
			}
			else {
				this.safePutFormField(params,"FAX", fax);
			}
		}else 
			this.safePutFormField(params,"FAX", telef);

		this.safePutFormField(params,"EMAIL",(String) htRow.get("EMAIL"));


		// Declara que el conductor
		UReferenceDataField cCond = (UReferenceDataField) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("IDCONDUCTOR");
		final String idCond = (String) cCond.getValue();
		final Map htFila = cCond.getCodeValues(idCond);
		this.safePutFormField(params,"IDCONDUCTOR", (String) htFila.get("IDCONDUCTOR"));
		this.safePutFormField(params,"NOMBRE", (String) htFila.get("NOMBRE"));
		this.safePutFormField(params,"APELLIDOS",(String)  htFila.get("APELLIDOS"));


		DateFormat datef = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
		if ("ESPAÑA".equals(pais)) {
			datef.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		} else if ("PORTUGAL".equals(pais)) {
			datef.setTimeZone(TimeZone.getTimeZone("Europe/Lisbon"));
		} else if ("RUMANIA".equals(pais)) {
			datef.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));
		} else if ("BULGARIA".equals(pais)) {
			datef.setTimeZone(TimeZone.getTimeZone("Europe/Sofia"));
		} else {
			datef.setTimeZone(TimeZone.getTimeZone("Europe/Madrid"));
		}

		DateFormat datefh = new SimpleDateFormat("dd/MM/yyyy");
		this.safePutFormField(params,"F_TRABAJO", htFila.get("F_ALTA")!=null ? datefh.format(htFila.get("F_ALTA")) : "");
		this.safePutFormField(params,"F_NAC", htFila.get("F_NAC")!=null ? datefh.format(htFila.get("F_NAC")) : "");




		Table tbIndef = (Table)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getElementReference("EInformeIndef");
		if (tbIndef!=null) {
			Hashtable<String, Object> avSelected = tbIndef.getSelectedRowData();
			fini = (Date) ((Vector) avSelected.get("FECINI")).get(0);
			ffin = (Date) ((Vector) avSelected.get("FECFIN")).get(0);
		}
		DateFormat datefhora = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		this.safePutFormField(params,"F_INI", fini!=null ? datefhora.format(fini) : "");
		this.safePutFormField(params,"F_FIN", ffin!=null ? datefhora.format(ffin) : "");


		UReferenceDataField nifFirmante = (UReferenceDataField) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("NIF");
		final Map hNifFirmante = nifFirmante.getCodeValues(nifFirmante.getValue());
		this.safePutFormField(params,"APELLIDOS_FIRMA", (String)hNifFirmante.get("APELLIDOS"));
		this.safePutFormField(params,"NOMBRE_FIRMA", (String)hNifFirmante.get("NOMBRE"));
		this.safePutFormField(params,"CARGO", (String)hNifFirmante.get("CARGO"));


		params.put("BAJAENFERMEDAD", "N");
		params.put("VACACIONES", "N");
		params.put("PERMISO", "N");
		params.put("CONDEXCLUIDO", "N");
		params.put("TRABAJODIST", "N");
		params.put("DISPONIBLE", "N");
		params.put("FECHA", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		this.safePutFormField(params,"LUGAR", (String) htRow.get("POBL"));
		this.safePutFormField(params,"LUGAR2", (String) htRow.get("POBL"));



		return params;
	}

	private void updateUser() {
		try {
			Entity e = this.ocl.getEntityReference("Usuario");
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put("USUARIO", this.ocl.getUser());
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("NOMBRE_FIRMANTE", IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("NOMBRE_FIRMA"));
			av.put("APELLIDOS_FIRMANTE", IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("APELLIDOS_FIRMA"));
			av.put("CARGO", IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CARGO"));
			EntityResult res = e.update(av, cv, this.ocl.getSessionId());
			res.getCode();
			this.ocl.getUserData();
		} catch (Exception e) {

		}

	}

	private void updateConductor() {
		try {
			Entity e = this.ocl.getEntityReference("EConductoresEmp");
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			UReferenceDataField ridconductor = (UReferenceDataField) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("IDCONDUCTOR");
			Object o = ridconductor.getValue();
			Map<String, Object> idcond = ridconductor.getCodeValues(o);
			cv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, idcond.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
			cv.put("CIF", IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CIF"));
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("F_NAC", IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("F_NAC"));
			av.put("F_ALTA", IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("F_TRABAJO"));
			e.update(av, cv, this.ocl.getSessionId());
		} catch (Exception e) {
		}
	}

	private void safePutFormField(Map<String, String> params, String key, String value) {
		MapTools.safePut(params, key, value == null ? "" : String.valueOf(value));
	}
}
