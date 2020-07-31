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

import com.ontimize.gui.InteractionManagerModeEvent;
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

public class IMCartasPorteGenerateReportListener extends AbstractActionListenerButton {

	private static final Logger	logger				= LoggerFactory.getLogger(IMCartasPorteGenerateReportListener.class);

//	private static final String	CERTPDFPATH			= "com/opentach/client/rsc/pdf/cartaporte.pdf";
//	private static final String	CERT_BASE_PDFPATH	= "com/opentach/client/rsc/i18n/";
//	private static final String	CERT_PDF_NAME		= "cartaporte.pdf";

	private static final String	M1					= "1 Nombre cargador";
	private static final String	M2					= "2 Dirección cargador";
	private static final String	M3					= "3 Población cargador";
	private static final String	M4					= "4 CIF cargador";
	private static final String	M5					= "5 Nombre transportista";
	private static final String	M6					= "6 Dirección transportista";
	private static final String	M7					= "7 Población transportista";
	private static final String	M8					= "8 CIF transportista";
	private static final String	M9					= "9 Origen";
	private static final String	M10					= "10 Destino";
	private static final String	M11					= "11 Naturaleza";
	private static final String	M12					= "12 Peso";
	private static final String	M13					= "13 Fecha";
	private static final String	M14					= "14 Matrícula tractor";
	private static final String	M15					= "15 Matrícula semiremolque";
	private static final String	M16					= "16 Matrícula remolque";
	private static final String	M17					= "17 Matrícula cambio tractor";
	private static final String	M18					= "18 Matrícula cambio semiremolque";
	private static final String	M19					= "19 Matrícula cambio remolque";
	private static final String	M20					= "20 Observaciones";

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
	
	
	public IMCartasPorteGenerateReportListener(UButton button, Hashtable params) throws Exception {
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

	@Override
	public void interactionManagerModeChanged(InteractionManagerModeEvent interactionmanagermodeevent) {
		super.interactionManagerModeChanged(interactionmanagermodeevent);
		this.getButton().setEnabled(true);
	}

	@Override
	protected void considerToEnableButton() {
		this.getButton().setEnabled(true);
	}

	private Hashtable<String, String> getParams() throws Exception {

		Hashtable<String, String> params = new Hashtable<String, String>();

		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M1, "NOMBRE_CARGADOR");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M2, "DIRECCION_CARGADOR");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M3, "POBRACION_CARGADOR");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M4, "NIF_CIF_CARGADOR");

		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M5, "NOMBRE_TRANSPORTISTA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M6, "DIRECCION_TRANSPORTISTA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M7, "POBRACION_TRANSPORTISTA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M8, "NIF_CIF_TRANSPORTISTA");

		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M9, "LUGAR_ORIGEN");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M10, "LUGAR_DESTINO");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M11, "NATURALEZA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M12, "PESO");

		DateFormat datefh = new SimpleDateFormat("dd/MM/yyyy");
		DateDataField fini = (DateDataField) this.getForm().getDataFieldReference("FECHA_ENVIO");
		String dateIni = ((JTextField) fini.getDataField()).getText();
		if ((dateIni != null) && (dateIni.length() > 0)) {
			if (fini.getValue() != null) {
				params.put(IMCartasPorteGenerateReportListener.M13, "   " + datefh.format(fini.getValue()));
			}
		} else if (dateIni != null && !("".equals(dateIni))) {
			try {
				datefh.parse(dateIni);
				params.put(IMCartasPorteGenerateReportListener.M13, "   " + dateIni);
			} catch (ParseException e) {
			}
		}else {
			params.put(IMCartasPorteGenerateReportListener.M13, "   ");
		}

		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M14, "MATRICULA_TRACTOR");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M15, "MATRICULA_SEMIREMOLQUE");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M16, "MATRICULA_REMOLQUE");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M17, "MATRICULA_CAMBIO_TRACTOR");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M18, "MATRICULA_CAMBIO_SEMIREMOLQUE");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M19, "MATRICULA_CAMBIO_REMOLQUE");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M20, "OBSR");

		
		
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M21, "DOMICILIO_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M22, "PROVINCIA_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M23, "TIPO_BULTOS");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M24, "N_BULTOS");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M25, "DNI_TRANSPORTISTA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M26, "FECHA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M27, "LOCALIDAD_EXPEDIDOR");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M28, "LOCALIDAD_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M29, "DNI_DESTINATARIO");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M30, "ID_MATERIA");
		this.safePutFormField(params, IMCartasPorteGenerateReportListener.M31, "PESO_BRUTO");
		
		return params;
	}

	private void safePutFormField(Map<String, String> params, String key, String formAttr) {
		Object value = this.getForm().getDataFieldValue(formAttr);
		MapTools.safePut(params, key, value == null ? "" : String.valueOf(value));
	}

//	private InputStream getTemplateStream() {
//		InputStream is = null;
//		String path = IMCartasPorteGenerateReportListener.CERT_BASE_PDFPATH + IMCartasPorteGenerateReportListener.this.getForm()
//		.getLocale() + "/" + IMCartasPorteGenerateReportListener.CERT_PDF_NAME;
//		try {
//			is = this.getClass().getClassLoader().getResourceAsStream(path);
//		} catch (Exception ex) {
//			is = this.getClass().getClassLoader().getResourceAsStream(IMCartasPorteGenerateReportListener.CERTPDFPATH);
//		}
//		return is;
//	}

	private void generateTemplate() {
		
		
		new USwingWorker<File, Void>() {

			@Override
			protected File doInBackground() throws Exception {
				Hashtable<String, String> params = IMCartasPorteGenerateReportListener.this.getParams();
				String cif = (String) IMCartasPorteGenerateReportListener.this.getForm().getDataFieldValue("CIF");
				BytesBlock bb = ((OpentachClientLocator) IMCartasPorteGenerateReportListener.this.getReferenceLocator()).getRemoteService(IWaybillService.class).generateWaybill(
						IMCartasPorteGenerateReportListener.this.type, params, cif, IMCartasPorteGenerateReportListener.this.getForm().getLocale(),
						IMCartasPorteGenerateReportListener.this.getSessionId());
				File file = File.createTempFile("InformeAdr", ".pdf");
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
							IMCartasPorteGenerateReportListener.logger);
				}
			}
		}.executeOperation(this.getForm());
		
		

//		OperationThread opth = new OperationThread() {
//			@Override
//			public void run() {
//				this.hasStarted = true;
//
//				try (ByteArrayOutputStream baos = new ByteArrayOutputStream(140000); InputStream is = IMCartasPorteGenerateReportListener.this.getTemplateStream();) {
//					File fTemporal = File.createTempFile("cartasporte", ".pdf");
//					try (OutputStream os = new FileOutputStream(fTemporal);) {
//
//						final byte[] barray;
//						Hashtable<String, Object> params = IMCartasPorteGenerateReportListener.this.getParams();
//						PdfFiller.fillFields(is, baos, params, false);
//						barray = baos.toByteArray();
//						os.write(barray);
//						os.close();
//						Desktop.getDesktop().open(fTemporal);
//					}
//				} catch (Exception e) {
//					IMCartasPorteGenerateReportListener.logger.error(null, e);
//					this.res = e;
//				} finally {
//					this.hasFinished = true;
//				}
//			}
//
//		};
//		ExtendedApplicationManager.proccessOperation(SwingUtilities.getWindowAncestor(this.getForm()), opth, 1000);
//		Object res = opth.getResult();
//		if (res instanceof Exception) {
//			this.getForm().message("M_PROCESO_INCORRECTO_BD", Form.INFORMATION_MESSAGE);
//		}
}
}
