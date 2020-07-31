package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.Form;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.certificadoActividades.ICertificadoActividadesService;
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
		this.updateUser();
		this.updateConductor();
		this.generateTemplate();
	}

	private void generateTemplate() {
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {

				Hashtable<String, String> params = new Hashtable<String, String> ();
				UReferenceDataField cif = (UReferenceDataField)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("CIF");
				cif.getCodeValues(cif.getValue());
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"CIF", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CIF"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"NOMB", (String)cif.getCodeValues(cif.getValue()).get("NOMB"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"TELF", (String)cif.getCodeValues(cif.getValue()).get("TELF"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"FAX", (String)cif.getCodeValues(cif.getValue()).get("FAX"));

				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"EMAIL", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("EMAIL"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"DIRECCION", (String)cif.getCodeValues(cif.getValue()).get("DIRECCION"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"POBL",(String) cif.getCodeValues(cif.getValue()).get("POBL"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"PAIS",(String) cif.getCodeValues(cif.getValue()).get("PAIS"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"CG_POSTAL", (String)cif.getCodeValues(cif.getValue()).get("CG_POSTAL"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"APELLIDOS_FIRMA", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("APELLIDOS_FIRMA"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"NOMBRE_FIRMA", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("NOMBRE_FIRMA"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"CARGO", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("CARGO"));

				UReferenceDataField idconductor = (UReferenceDataField)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldReference("IDCONDUCTOR");
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"IDCONDUCTOR", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("IDCONDUCTOR"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"NOMBRE", (String) idconductor.getCodeValues(idconductor.getValue()).get("NOMBRE"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"APELLIDOS", (String) idconductor.getCodeValues(idconductor.getValue()).get("APELLIDOS"));

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

				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"LUGAR", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("LUGAR"));
				IMCartaCertificateActividadesGenerateReportListener.this.safePutFormField(params,"LUGAR2", (String) IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("LUGAR2"));


				Date fecha = (Date)IMCartaCertificateActividadesGenerateReportListener.this.getForm().getDataFieldValue("FECHA");
				if (fecha!=null) {
					params.put("FECHA", new SimpleDateFormat("dd/MM/yyyy").format(fecha));
				}else {
					params.put("FECHA", "");
				}

				BytesBlock bb = ((OpentachClientLocator) IMCartaCertificateActividadesGenerateReportListener.this.getReferenceLocator()).getRemoteService(ICertificadoActividadesService.class)
						.generateCertificadoActividadesServiceReport(params, IMCartaCertificateActividadesGenerateReportListener.this.getForm().getLocale(), IMCartaCertificateActividadesGenerateReportListener.this.getSessionId());
				File file = File.createTempFile("InformeCertificadoActividades", ".pdf");
				FileTools.copyFile(new ByteArrayInputStream(bb.getBytes()), file);
				return file;
			}

			@Override
			protected void done() {
				try {
					File file = this.uget();
					Desktop.getDesktop().open(file);
				} catch (Throwable error) {
					MessageManager.getMessageManager().showExceptionMessage(new Exception("M_PROCESO_INCORRECTO_GENERANDO_INFORME", error),
							IMCartaCertificateActividadesGenerateReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());



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
