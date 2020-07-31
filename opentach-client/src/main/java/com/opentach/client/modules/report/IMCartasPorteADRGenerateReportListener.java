package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.field.DateDataField;
import com.ontimize.jee.common.tools.FileTools;
import com.ontimize.jee.common.tools.MapTools;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.IMRoot;
import com.opentach.common.waybill.IWaybillService;
import com.opentach.common.waybill.WaybillType;
import com.utilmize.client.gui.buttons.AbstractActionListenerButton;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.tasks.USwingWorker;

public class IMCartasPorteADRGenerateReportListener extends AbstractActionListenerButton {

	private static final Logger	logger	= LoggerFactory.getLogger(IMCartasPorteADRGenerateReportListener.class);

	private static final String	M1		= "1 Expedidor";
	private static final String	M2		= "2 Cargador";
	private static final String	M3		= "3 Destinatario";
	private static final String	M4		= "4 Designación";
	private static final String	M5		= "5 Etiquetas";
	private static final String	M6		= "6 Grupo Embalaje";
	private static final String	M7		= "7 Cantidad";
	private static final String	M8		= "8 Fecha Exp";
	private static final String	M9		= "9 Código Restricción Túneles";
	private static final String	M10		= "10 Grado Máx Llenado";
	private static final String	M11		= "11 N Identif Peligro";
	private static final String	M12		= "12 UN";
	private static final String	M13		= "13 Transportista Efectivo";
	private static final String	M14		= "14 Matrícula tractora";
	private static final String	M15		= "15 Matrícula remolque";
	private static final String	M16		= "16 Nombre y apellidos";
	private static final String	M17		= "17 DNI";
	private static final String	M18		= "18 Dir Expedidor";
	private static final String	M19		= "19 NIF Expedidor";
	private static final String	M20		= "20 Prov Expedidor";
	private static final String	M21		= "21 Dir Destinatario";
	private static final String	M22		= "22 Prov Destinatario";
	private static final String	M23		= "23 Tipo Bulto";
	private static final String	M24		= "24 Num Bultos";
	private static final String	M25		= "25 NIF Transportista";
	private static final String	M26		= "26 Fecha Transporte";
	private static final String	M27		= "27 Loc Expedidor";
	private static final String	M28		= "28 Loc Destinatario";
	private static final String	M29		= "29 NIF Destinatario";
	private static final String	M30		= "30 N Materia";
	private static final String	M31		= "31 Peso Bruto";

	private WaybillType			type;

	public IMCartasPorteADRGenerateReportListener(UButton button, Hashtable params) throws Exception {
		super(button, params);
	}

	@Override
	protected void init(Map<?, ?> params) throws Exception {
		super.init(params);
		this.type = WaybillType.valueOf((String) params.get("type"));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (((IMRoot) this.getInteractionManager()).checkRequiredVisibleDataFields(true)) {
			this.generateTemplate();
		}
	}

	private Hashtable<String, String> getParams() throws Exception {

		Hashtable<String, String> params = new Hashtable<>();
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M1, "EXPEDIDOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M2, "CARGADOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M3, "DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M4, "DESIGNACION");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M5, "ETIQUETAS");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M6, "GRUPO_EMBALAJE");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M7, "CANTIDAD");
		this.safePutDateFormField(params, IMCartasPorteADRGenerateReportListener.M8, "FECHA_EXP");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M9, "RESTRICCION_TUNELES");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M10, "MAX_LLENADO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M11, "ID_PELIGRO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M12, "UN");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M13, "TRANSPORTISTA");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M14, "MATRICULA_TRACTORA");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M15, "MATRICULA_REMOLQUE");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M16, "NOMBRE_CONDUCTOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M17, "DNI_CONDUCTOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M18, "DOMICILIO_EXPEDIDOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M19, "DNI_EXPEDIDOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M20, "PROVINCIA_EXPEDIDOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M21, "DOMICILIO_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M22, "PROVINCIA_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M23, "TIPO_BULTOS");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M24, "N_BULTOS");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M25, "DNI_TRANSPORTISTA");
		this.safePutDateFormField(params, IMCartasPorteADRGenerateReportListener.M26, "FECHA");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M27, "LOCALIDAD_EXPEDIDOR");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M28, "LOCALIDAD_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M29, "DNI_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M30, "ID_MATERIA");
		this.safePutFormField(params, IMCartasPorteADRGenerateReportListener.M31, "PESO_BRUTO");
		return params;
	}

	private void generateTemplate() {
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				Hashtable<String, String> params = IMCartasPorteADRGenerateReportListener.this.getParams();
				String cif = (String) IMCartasPorteADRGenerateReportListener.this.getForm().getDataFieldValue("CIF");
				BytesBlock bb = ((OpentachClientLocator) IMCartasPorteADRGenerateReportListener.this.getReferenceLocator()).getRemoteService(IWaybillService.class).generateWaybill(
						IMCartasPorteADRGenerateReportListener.this.type, params, cif, IMCartasPorteADRGenerateReportListener.this.getForm().getLocale(),
						IMCartasPorteADRGenerateReportListener.this.getSessionId());
				File file = File.createTempFile("Informe " + IMCartasPorteADRGenerateReportListener.this.type, ".pdf");
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
							IMCartasPorteADRGenerateReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());
	}

	private void safePutFormField(Map<String, String> params, String key, String formAttr) {
		Object value = this.getForm().getDataFieldValue(formAttr);
		MapTools.safePut(params, key, value == null ? "" : String.valueOf(value));
	}

	private void safePutDateFormField(Map<String, String> params, String key, String formAttr) {
		DateFormat datefh = new SimpleDateFormat("dd/MM/yyyy");
		DateDataField dateField = (DateDataField) this.getForm().getDataFieldReference(formAttr);
		String dateStr = ((JTextField) dateField.getDataField()).getText();
		if ((dateStr != null) && (dateStr.length() > 0)) {
			if (dateField.getValue() != null) {
				params.put(key, "   " + datefh.format(dateField.getValue()));
			}
		} else if (dateStr != null  && !("".equals(dateStr))) {
			try {
				datefh.parse(dateStr);
				params.put(key, "   " + dateStr);
			} catch (ParseException e) {
			}
		}else {
			params.put(key, "   ");
		}
	}
}
