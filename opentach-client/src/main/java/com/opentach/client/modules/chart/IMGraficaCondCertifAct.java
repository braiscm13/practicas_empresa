package com.opentach.client.modules.chart;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.DataFile;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;
import com.ontimize.util.pdf.PdfFiller;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMGraficaCondCertifAct extends IMGraficaCond {

	private OpentachClientLocator	ocl;


	private static final Logger		logger		= LoggerFactory.getLogger(IMGraficaCondCertifAct.class);
	private static final String		CERTPDFPATH	= "com/opentach/client/rsc/pdf/certificadoactividades.pdf";

	private static final String		M1			= "1 Nombre de la empresa";
	private static final String		M2			= "2 Dirección código postal ciudad país";
	private static final String		M3			= "3 Número de teléfono incluido el prefijo internacional";
	private static final String		M4			= "4 Número de fax incluido el prefijo internacional";
	private static final String		M5			= "5 Correo electrónico";
	private static final String		M8			= "8 Apellidos y nombre";
	private static final String		M9			= "9 Fecha de nacimiento día mes año";
	private static final String		M10			= "10 Número de permiso de conducción de documento de identidad o de pasaporte";
	private static final String		M11			= "11 que empezó a trabajar en la empresa el día mes año";
	private static final String		M12			= "12 desde hora día mes año";
	private static final String		M13			= "13 hasta hora día mes año";
	private Button					bGenTemplate;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();

		Button bInformeCAP = f.getButton("btnInformeCAP");
		if (bInformeCAP != null) {
			bInformeCAP.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (IMGraficaCondCertifAct.this.checkRequiredVisibleDataFields(true)) {
						// updateUser();
						// updateConductor();
						IMGraficaCondCertifAct.this.generateTemplate();
					}
				}
			});
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		this.managedForm.getButton("btnInformeCAP").setEnabled(true);
		try {
			IUserData ud = this.ocl.getUserData();
			this.managedForm.setDataFieldValue("NOMBRE_FIRMA", ud.getNfirmante());
			this.managedForm.setDataFieldValue("APELLIDOS_FIRMA", ud.getAfirmante());
			this.managedForm.setDataFieldValue("CARGO", ud.getCargo());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.bGenTemplate != null) {
			this.bGenTemplate.setEnabled(true);
		}
	}

	private void generateTemplate() {

		OperationThread opth = new OperationThread() {
			@Override
			public void run() {
				this.hasStarted = true;

				InputStream is = null;
				OutputStream os = null;
				try {

					ByteArrayOutputStream baos = new ByteArrayOutputStream(140000);
					final byte[] barray;
					final String name;
					is = this.getClass().getClassLoader().getResourceAsStream(IMGraficaCondCertifAct.CERTPDFPATH);
					Hashtable params = IMGraficaCondCertifAct.this.getParams();
					if (params == null) {
						IMGraficaCondCertifAct.this.managedForm.message("M_FALTAN_PARAMETROS", Form.ERROR_MESSAGE);
						return;
					}
					name = "certifActiv";

					File fTemporal = File.createTempFile(name, ".pdf");
					PdfFiller.fillFields(is, baos, params, false);
					barray = baos.toByteArray();
					baos = null;
					os = new FileOutputStream(fTemporal);
					os.write(barray);
					os.close();

					final String obsr = (String) IMGraficaCondCertifAct.this.managedForm.getDataFieldValue("OBSR");
					Hashtable<String, Object> avCertif = new Hashtable<String, Object>();
					avCertif.put("NOMBRE", name);
					DataFile df = new DataFile(name, new BytesBlock(barray));
					avCertif.put("FICH_CERTIF", df);
					avCertif.put(OpentachFieldNames.NUMREQ_FIELD, IMGraficaCondCertifAct.this.managedForm.getDataFieldValue("CG_CONTRATO"));
					avCertif.put(OpentachFieldNames.IDCONDUCTOR_FIELD, IMGraficaCondCertifAct.this.managedForm.getDataFieldValue("IDCONDUCTOR"));
					if (obsr != null) {
						avCertif.put("OBSR", obsr);
					}
					EntityReferenceLocator loc = IMGraficaCondCertifAct.this.formManager.getReferenceLocator();
					Entity ent = loc.getEntityReference("ECertifActividades");
					EntityResult result = ent.insert(avCertif, loc.getSessionId());
					if (result.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
						throw new Exception();
					}
					Desktop.getDesktop().open(fTemporal);
				} catch (Exception e) {
					IMGraficaCondCertifAct.logger.error(null, e);
					this.res = e;
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception e) {
						}
					}
					if (os != null) {
						try {
							os.close();
						} catch (Exception e) {
						}
					}
					this.hasFinished = true;
				}
			}
		};
		JFrame dParent = (JFrame) SwingUtilities.getWindowAncestor(this.managedForm);
		ExtendedApplicationManager.proccessOperation(dParent, opth, 1000);
		Object res = opth.getResult();
		if (res instanceof Exception) {
			this.managedForm.message("M_PROCESO_INCORRECTO_BD", Form.INFORMATION_MESSAGE);
		}
	}

	private Hashtable getParams() throws Exception {
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		// Parte que debe rellenar la empresa
		ReferenceLocator b = (ReferenceLocator) this.formManager.getReferenceLocator();

		Date fini = null;
		Date ffin = null;

		MouseListener[] ml = this.chartwpp.getChart().getMouseListeners();
		for (int i = 0; i < ml.length; i++) {
			if (ml[i] instanceof ActivityRulerMouseListener) {
				ActivityRulerMouseListener ruler = (ActivityRulerMouseListener) ml[i];
				if ((ruler.firstStartDate != null) && (ruler.secondStartDate != null)) {
					long mouseStartTime = ruler.firstStartDate.getDate().getTime();
					mouseStartTime += ruler.firstStartDate.getDuracion() * 60 * 1000l * ruler.firstStartDate.getPercent();
					fini = new Date(mouseStartTime);
					long mouseEndTime = ruler.secondStartDate.getDate().getTime();
					mouseEndTime += ruler.secondStartDate.getDuracion() * 60 * 1000l * ruler.secondStartDate.getPercent();
					ffin = new Date(mouseEndTime);
				}
			}
		}
		if ((fini == null) && (ffin == null)) {
			return null;
		}

		Entity eDFemp = b.getEntityReference(CompanyNaming.ENTITY);
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		final String cif = (String) cCif.getValue();
		final Map htRow = cCif.getCodeValues(cif);
		final String empresa = (String) htRow.get("NOMB");
		params.put(IMGraficaCondCertifAct.M1, empresa);
		Vector<String> v = new Vector<String>();
		v.add("DIRECCION");
		v.add("CG_POSTAL");
		v.add("POBL");
		v.add("PAIS");
		v.add("TELF");
		v.add("FAX");
		v.add("EMAIL");
		v.add("PREFIJO");
		String pais = "";
		EntityResult res = eDFemp.query(new Hashtable<>(htRow), v, b.getSessionId());
		if (res.getCode() != EntityResult.OPERATION_WRONG) {
			Hashtable htFila = res.getRecordValues(0);
			StringBuffer sb = new StringBuffer();
			String direcc = (String) htFila.get("DIRECCION");
			if (direcc != null) {
				sb.append(direcc);
			}
			String postal = (String) htFila.get("CG_POSTAL");
			if (postal != null) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(postal);
			}
			String pobl = (String) htFila.get("POBL");
			if (pobl != null) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(pobl);
			}

			pais = (String) htFila.get("PAIS");
			if (pais != null) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(pais);
			}
			params.put(IMGraficaCondCertifAct.M2, sb.toString());
			String telef = (String) htFila.get("TELF");
			if (telef == null) {
				params.put(IMGraficaCondCertifAct.M3, "");
			} else {
				String prefijo = (String) htFila.get("PREFIJO");
				if (prefijo != null) {
					if (!telef.startsWith("00")) {
						telef = prefijo + " " + telef;
					}
				}
				params.put(IMGraficaCondCertifAct.M3, telef);
			}
			String fax = (String) htFila.get("FAX");
			if (fax == null) {
				params.put(IMGraficaCondCertifAct.M4, "");
			} else {
				String prefijo = (String) htFila.get("PREFIJO");
				if (prefijo != null) {
					if (!fax.startsWith("00")) {
						fax = prefijo + " " + fax;
					}
				}
				params.put(IMGraficaCondCertifAct.M4, fax);
			}

			if (htFila.get("EMAIL") == null) {
				params.put(IMGraficaCondCertifAct.M5, "");
			} else {
				params.put(IMGraficaCondCertifAct.M5, htFila.get("EMAIL"));
			}
		}

		// Declara que el conductor
		UReferenceDataField cCond = (UReferenceDataField) this.managedForm.getDataFieldReference("IDCONDUCTOR");
		final String idCond = (String) cCond.getValue();
		final Map htFila = cCond.getCodeValues(idCond);
		String name = htFila.get("APELLIDOS") + " " + htFila.get("NOMBRE");
		params.put(IMGraficaCondCertifAct.M8, name);
		params.put(IMGraficaCondCertifAct.M10, htFila.get("DNI"));

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

		if (htFila.get("F_NAC") != null) {
			params.put(IMGraficaCondCertifAct.M9, datefh.format(htFila.get("F_NAC")));
		}
		if (htFila.get("F_ALTA") != null) {
			params.put(IMGraficaCondCertifAct.M11, datefh.format(htFila.get("F_ALTA")));
		}

		// Durante el período

		if (fini != null) {
			params.put(IMGraficaCondCertifAct.M12, "   " + datef.format(fini));
		}

		if (ffin != null) {
			params.put(IMGraficaCondCertifAct.M13, "   " + datef.format(ffin));
		}
		return params;
	}

}