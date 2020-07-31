package com.opentach.client.modules.report;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.gui.DataFile;
import com.ontimize.gui.Form;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.CheckDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.HourDateDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.field.WWWDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.ReferenceLocator;
import com.ontimize.util.pdf.PdfFiller;
import com.ontimize.util.remote.BytesBlock;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.company.naming.CompanyNaming;
import com.opentach.common.user.IUserData;
import com.utilmize.client.gui.buttons.UButton;
import com.utilmize.client.gui.field.reference.UReferenceDataField;

public class IMCertifActiv extends IMReportRoot {

	private OpentachClientLocator	ocl;

	private static final Logger		logger				= LoggerFactory.getLogger(IMCertifActiv.class);
	private static final String		CERTPDFPATH			= "com/opentach/client/rsc/pdf/certificadoactividades.pdf";
	private static final String		CERT_BASE_PDFPATH	= "com/opentach/client/rsc/i18n/";
	private static final String		CERT_PDF_NAME		= "certificadoactividades.pdf";

	private static final String		M1					= "1 Nombre de la empresa";
	private static final String		M2					= "2 Dirección código postal ciudad país";
	private static final String		M3					= "3 Número de teléfono incluido el prefijo internacional";
	private static final String		M4					= "4 Número de fax incluido el prefijo internacional";
	private static final String		M5					= "5 Correo electrónico";
	private static final String		M6					= "6 Apellidos y nombre";
	private static final String		M7					= "7 Cargo en la empresa";
	private static final String		M8					= "8 Apellidos y nombre";
	private static final String		M9					= "9 Fecha de nacimiento día mes año";
	private static final String		M10					= "10 Número de permiso de conducción de documento de identidad o de pasaporte";
	private static final String		M11					= "11 que empezó a trabajar en la empresa el día mes año";
	private static final String		M12					= "12 desde hora día mes año";
	private static final String		M13					= "13 hasta hora día mes año";
	private static final String		M14					= "estuvo de baja por enfermedad";
	private static final String		M15					= "estuvo de vacaciones";
	private static final String		M16					= "estuvo de permiso o descanso";
	private static final String		M17					= "condujo un vehículo exento";
	private static final String		M18					= "efectuo trabajo distinto a conduccion";
	private static final String		M19					= "estuvo disponible";
	private static final String		M20					= "20 Lugar firma empresa";
	private static final String		M21					= "Fecha firma empresa";
	private static final String		M22					= "22. Lugar firma conductor";
	private static final String		M23					= "Fecha firma conductor";

	private Button					bGenTemplate;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {

		super.registerInteractionManager(f, gf);
		this.ocl = (OpentachClientLocator) this.formManager.getReferenceLocator();
		this.bGenTemplate = f.getButton("btnGenerar");
		if (this.bGenTemplate != null) {
			this.bGenTemplate.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Locale l = IMCertifActiv.this.managedForm.getLocale();

					// para la versión en español se mantiene el pdf que permite editar los campos, ya que necesitan que sea editable :(

					if (!l.getLanguage().equals("it")) {
						if (IMCertifActiv.this.checkRequiredVisibleDataFields(true)) {
							IMCertifActiv.this.updateUser();
							IMCertifActiv.this.updateConductor();
							IMCertifActiv.this.generateTemplate();
						}
					}else {
						try {
							ActionListener al = new IMCartaCertificateActividadesGenerateReportListener((UButton) IMCertifActiv.this.bGenTemplate, new Hashtable());
							al.actionPerformed(e);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
			});
		}

	}

	private void updateUser() {
		try {
			Entity e = this.ocl.getEntityReference("Usuario");
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put("USUARIO", this.ocl.getUser());
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("NOMBRE_FIRMANTE", this.managedForm.getDataFieldValue("NOMBRE_FIRMA"));
			av.put("APELLIDOS_FIRMANTE", this.managedForm.getDataFieldValue("APELLIDOS_FIRMA"));
			av.put("CARGO", this.managedForm.getDataFieldValue("CARGO"));
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
			UReferenceDataField ridconductor = (UReferenceDataField) this.managedForm.getDataFieldReference("IDCONDUCTOR");
			Object o = ridconductor.getValue();
			Map<String, Object> idcond = ridconductor.getCodeValues(o);
			cv.put(OpentachFieldNames.IDCONDUCTOR_FIELD, idcond.get(OpentachFieldNames.IDCONDUCTOR_FIELD));
			cv.put("CIF", this.managedForm.getDataFieldValue("CIF"));
			Hashtable<String, Object> av = new Hashtable<String, Object>();
			av.put("F_NAC", this.managedForm.getDataFieldValue("F_NAC"));
			av.put("F_ALTA", this.managedForm.getDataFieldValue("F_TRABAJO"));
			e.update(av, cv, this.ocl.getSessionId());
		} catch (Exception e) {
		}
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
		try {
			IUserData ud = this.ocl.getUserData();
			this.managedForm.setDataFieldValue("NOMBRE_FIRMA", ud.getNfirmante());
			this.managedForm.setDataFieldValue("APELLIDOS_FIRMA", ud.getAfirmante());
			this.managedForm.setDataFieldValue("CARGO", ud.getCargo());
			if ((ud.getlGrupos() != null) && (ud.getlGrupos().size() > 0)) {
				this.managedForm.setDataFieldValue("IDGRUPO", ud.getlGrupos().get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.updateChainStatus("CG_CONTRATO", this.managedForm.getDataFieldValue("CG_CONTRATO") == null ? true : false);
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (this.bGenTemplate != null) {
			this.bGenTemplate.setEnabled(true);
		}
	}

	@SuppressWarnings("unchecked")
	private Hashtable<String, Object> getParams() throws Exception {
		Hashtable<String, Object> params = new Hashtable<String, Object>();
		// Parte que debe rellenar la empresa
		ReferenceLocator b = (ReferenceLocator) this.formManager.getReferenceLocator();
		Entity eDFemp = b.getEntityReference(CompanyNaming.ENTITY);
		UReferenceDataField cCif = (UReferenceDataField) this.CIF;
		final String cif = (String) cCif.getValue();
		final Map<String, Object> htRow = cCif.getCodeValues(cif);
		final String empresa = (String) htRow.get("NOMB");
		params.put(IMCertifActiv.M1, empresa);
		Vector<Object> v = new Vector<Object>(1);
		v.add("DIRECCION");
		v.add("CG_POSTAL");
		v.add("POBL");
		v.add("PAIS");
		v.add("TELF");
		v.add("FAX");
		v.add("EMAIL");
		v.add("PREFIJO");
		EntityResult res = eDFemp.query(new Hashtable<>(htRow), v, b.getSessionId());
		if (res.getCode() != EntityResult.OPERATION_WRONG) {
			Hashtable<String, Object> htFila = res.getRecordValues(0);
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
			String pais = (String) htFila.get("PAIS");
			if (pais != null) {
				if (sb.length() > 0) {
					sb.append(',');
				}
				sb.append(pais);
			}
			params.put(IMCertifActiv.M2, sb.toString());
			String telef = (String) htFila.get("TELF");
			if (telef == null) {
				params.put(IMCertifActiv.M3, "");
			} else {
				String prefijo = (String) htFila.get("PREFIJO");
				if (prefijo != null) {
					if (!telef.startsWith("00")) {
						telef = prefijo + " " + telef;
					}
				}
				params.put(IMCertifActiv.M3, telef);
			}
			String fax = (String) htFila.get("FAX");
			if (fax == null) {
				params.put(IMCertifActiv.M4, "");
			} else {
				String prefijo = (String) htFila.get("PREFIJO");
				if (prefijo != null) {
					if (!fax.startsWith("00")) {
						fax = prefijo + " " + fax;
					}
				}
				params.put(IMCertifActiv.M4, fax);
			}

			if (htFila.get("EMAIL") == null) {
				params.put(IMCertifActiv.M5, "");
			} else {
				params.put(IMCertifActiv.M5, htFila.get("EMAIL"));
			}
		}

		// Declara que el conductor
		UReferenceDataField cCond = (UReferenceDataField) this.managedForm.getDataFieldReference("IDCONDUCTOR");
		final String idCond = (String) cCond.getValue();
		final Map<String, Object> htFila = cCond.getCodeValues(idCond);
		String name = htFila.get("APELLIDOS") + " " + htFila.get("NOMBRE");
		params.put(IMCertifActiv.M8, name);
		params.put(IMCertifActiv.M10, htFila.get("DNI"));

		// Durante el período
		DateFormat datef = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy");
		DateFormat datefh = new SimpleDateFormat("dd/MM/yyyy");
		HourDateDataField fini = (HourDateDataField) this.managedForm.getDataFieldReference("F_INI");
		String dateIni = ((JTextField) fini.getDataField()).getText();
		String hourIni = fini.getHourField().getText();
		if ((dateIni != null) && (hourIni != null) && (dateIni.length() > 0) && (hourIni.length() > 0)) {
			if (fini.getValue() != null) {
				params.put(IMCertifActiv.M12, "   " + datef.format(fini.getValue()));
			}
		} else if ((dateIni != null) && (hourIni != null) && (hourIni.length() == 0)) {
			try {
				datefh.parse(dateIni);
				params.put(IMCertifActiv.M12, "   " + dateIni);
			} catch (ParseException e) {
			}
		}
		HourDateDataField ffin = (HourDateDataField) this.managedForm.getDataFieldReference("F_FIN");
		String dateFin = ((JTextField) ffin.getDataField()).getText();
		String hourFin = ffin.getHourField().getText();

		if ((dateFin != null) && (hourFin != null) && (dateFin.length() > 0) && (hourFin.length() > 0)) {
			if (ffin.getValue() != null) {
				params.put(IMCertifActiv.M13, "   " + datef.format(ffin.getValue()));
			}
		} else if ((dateFin != null) && (hourFin != null) && (hourFin.length() == 0)) {
			try {
				datefh.parse(dateFin);
				params.put(IMCertifActiv.M13, "   " + dateFin);
			} catch (ParseException e) {
			}
		}
		// Checks
		CheckDataField bajaenfermedad = (CheckDataField) this.managedForm.getElementReference("BAJAENFERMEDAD");
		if (bajaenfermedad.isSelected()) {
			params.put(IMCertifActiv.M14, "9");
		}
		CheckDataField vacaciones = (CheckDataField) this.managedForm.getElementReference("VACACIONES");
		if (vacaciones.isSelected()) {
			params.put(IMCertifActiv.M15, "9");
		}
		CheckDataField permiso = (CheckDataField) this.managedForm.getElementReference("PERMISO");
		if (permiso.isSelected()) {
			params.put(IMCertifActiv.M16, "9");
		}
		CheckDataField condexcluido = (CheckDataField) this.managedForm.getElementReference("CONDEXCLUIDO");
		if (condexcluido.isSelected()) {
			params.put(IMCertifActiv.M17, "9");
		}
		CheckDataField trabajodist = (CheckDataField) this.managedForm.getElementReference("TRABAJODIST");
		if (trabajodist.isSelected()) {
			params.put(IMCertifActiv.M18, "9");
		}
		CheckDataField disponible = (CheckDataField) this.managedForm.getElementReference("DISPONIBLE");
		if (disponible.isSelected()) {
			params.put(IMCertifActiv.M19, "9");
		}
		DataField lugar = (DataField) this.managedForm.getDataFieldReference("LUGAR");
		if (lugar.getValue() != null) {
			params.put(IMCertifActiv.M20, lugar.getValue());
		}

		DataField lugar2 = (DataField) this.managedForm.getDataFieldReference("LUGAR2");
		if (lugar2.getValue() != null) {
			params.put(IMCertifActiv.M22, lugar2.getValue());
		}

		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		DateDataField fecha = (DateDataField) this.managedForm.getDataFieldReference("FECHA");
		if (fecha.getValue() != null) {
			params.put(IMCertifActiv.M21, df.format(fecha.getValue()));
			params.put(IMCertifActiv.M23, df.format(fecha.getValue()));
		}

		// Nuevos campos
		WWWDataField email = (WWWDataField) this.managedForm.getDataFieldReference("EMAIL");
		if (email.getValue() != null) {
			params.put(IMCertifActiv.M5, email.getValue());
		}
		TextDataField apellidos_firma = (TextDataField) this.managedForm.getDataFieldReference("APELLIDOS_FIRMA");
		TextDataField nombre_firma = (TextDataField) this.managedForm.getDataFieldReference("NOMBRE_FIRMA");
		String firmante = null;
		if (nombre_firma.getValue() != null) {
			if (apellidos_firma.getValue() != null) {
				firmante = apellidos_firma.getValue() + " " + nombre_firma.getValue();
			} else {
				firmante = (String) nombre_firma.getValue();
			}
		}
		if (firmante != null) {
			params.put(IMCertifActiv.M6, firmante);
		}
		TextDataField cargo = (TextDataField) this.managedForm.getDataFieldReference("CARGO");
		if (cargo.getValue() != null) {
			params.put(IMCertifActiv.M7, cargo.getValue());
		}
		DateDataField fecha_nac = (DateDataField) this.managedForm.getDataFieldReference("F_NAC");
		if (fecha_nac.getValue() != null) {
			params.put(IMCertifActiv.M9, df.format(fecha_nac.getValue()));
		}
		DateDataField fecha_trabajo = (DateDataField) this.managedForm.getDataFieldReference("F_TRABAJO");
		if (fecha_trabajo.getValue() != null) {
			params.put(IMCertifActiv.M11, df.format(fecha_trabajo.getValue()));
		}
		return params;
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
					System.out.println("");
					String path = IMCertifActiv.CERT_BASE_PDFPATH + IMCertifActiv.this.managedForm.getLocale() + "/" + IMCertifActiv.CERT_PDF_NAME;
					try {
						is = this.getClass().getClassLoader().getResourceAsStream(path);
					} catch (Exception ex) {
						is = this.getClass().getClassLoader().getResourceAsStream(IMCertifActiv.CERTPDFPATH);
					}

					Hashtable<String, Object> params = IMCertifActiv.this.getParams();
					name = "certifActiv";

					File fTemporal = File.createTempFile(name, ".pdf");
					PdfFiller.fillFields(is, baos, params, false);
					barray = baos.toByteArray();
					baos = null;
					os = new FileOutputStream(fTemporal);
					os.write(barray);
					os.close();

					final String obsr = (String) IMCertifActiv.this.managedForm.getDataFieldValue("OBSR");
					Hashtable<String, Object> avCertif = new Hashtable<String, Object>();
					avCertif.put("NOMBRE", name);
					DataFile df = new DataFile(name, new BytesBlock(barray));
					avCertif.put("FICH_CERTIF", df);
					avCertif.put(OpentachFieldNames.NUMREQ_FIELD, IMCertifActiv.this.managedForm.getDataFieldValue("CG_CONTRATO"));
					avCertif.put(OpentachFieldNames.IDCONDUCTOR_FIELD, IMCertifActiv.this.managedForm.getDataFieldValue("IDCONDUCTOR"));
					if (obsr != null) {
						avCertif.put("OBSR", obsr);
					}
					EntityReferenceLocator loc = IMCertifActiv.this.formManager.getReferenceLocator();
					Entity ent = loc.getEntityReference("ECertifActividades");
					EntityResult result = ent.insert(avCertif, loc.getSessionId());
					if (result.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
						throw new Exception();
					}
					Desktop.getDesktop().open(fTemporal);
				} catch (Exception e) {
					IMCertifActiv.logger.error(null, e);
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
		ExtendedApplicationManager.proccessOperation(SwingUtilities.getWindowAncestor(this.managedForm), opth, 1000);
		Object res = opth.getResult();
		if (res instanceof Exception) {
			this.managedForm.message("M_PROCESO_INCORRECTO_BD", Form.INFORMATION_MESSAGE);
		}
		// else{
		// managedForm.message("M_PROCESO_CORRECTO", Form.INFORMATION_MESSAGE);
		// }
		// }catch (Exception e) {
		// logger.log(Level.SEVERE, e.getMessage(), e);
		// managedForm.message("M_PROCESO_INCORRECTO", Form.ERROR_MESSAGE);
		// }finally{
		// if (is != null) try{ is.close();}catch(Exception e){}
		// if (os != null) try{ os.close();}catch(Exception e){}
		// }
	}
}
