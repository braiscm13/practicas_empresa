package com.opentach.client.modules.data;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.Table;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.render.PeriodosCellRenderer;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.client.modules.chart.IMGraficaCond;
import com.opentach.client.modules.chart.IMGraficaFicheros;
import com.opentach.client.util.DriverUtil;
import com.opentach.common.OpentachFieldNames;

public class IMDatosConductor extends IMDataRoot {

	private static final Logger	logger				= LoggerFactory.getLogger(IMDatosConductor.class);
	protected JDialog			reportdlg			= null;
	protected Form				reportform			= null;

	protected JDialog			chartdlg			= null;
	protected Form				chartform			= null;

	protected JDialog			chartFicherosDlg	= null;
	protected Form				chartFicherosForm	= null;

	private Form				FEliminar			= null;
	private JDialog				JEliminar			= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		this.fieldsChain.clear();
		this.setTableParentKeys(Arrays
				.asList(new String[] { OpentachFieldNames.CIF_FIELD, OpentachFieldNames.IDCONDUCTOR_FIELD, OpentachFieldNames.IDORIGEN_FIELD }));
		this.addDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
		this.addDateTags(new TimeStampDateTags("F_DESCARGA_DATOS"));
		this.addDateTags(new TimeStampDateTags("FEC_INS"));
		this.setChartEntity("EInformeActivCond");
		this.setDateEntity("EUFecha");

		// Pareja Tabla-Formulario que genera el informe.
		Map<String, String> tab_reportform = new HashMap<String, String>();
		tab_reportform.put("EInformeActivCond", "formInformeActivCond.xml");
		tab_reportform.put("EInformeInfrac", "formInformeInfracCond.xml");
		tab_reportform.put("EInformeFicherosTGD", "formInformeFicherosTGD.xml");
		this.setTableReportMap(tab_reportform);
		this.removeDeleteListener();
		this.deleteListener = new BasicInteractionManager.DeleteListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				if (IMDatosConductor.this.JEliminar == null) {
					IMDatosConductor.this.FEliminar = IMDatosConductor.this.formManager.getFormCopy("formEliminarConductor.xml");
					IMDatosConductor.this.FEliminar.getInteractionManager().setInitialState();
					IMDatosConductor.this.JEliminar = IMDatosConductor.this.FEliminar.putInModalDialog();
				}
				IMDatosConductor.this.FEliminar.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
						IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
				IMDatosConductor.this.FEliminar.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD,
						IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD));
				IMDatosConductor.this.FEliminar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
						IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
				IMDatosConductor.this.JEliminar.setVisible(true);
				IMDatosConductor.this.managedForm.refreshCurrentDataRecord();
			}
		};
		this.managedForm.getButton(InteractionManager.DELETE_KEY).addActionListener(this.deleteListener);

		Button btng = this.managedForm.getButton("btnGrafica");
		if (btng != null) {
			btng.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (((Hashtable) IMDatosConductor.this.managedForm.getDataFieldValue("EInformeActivCond")).isEmpty()) {
							IMDatosConductor.this.managedForm.message(ApplicationManager.getTranslation("M_GRAFICA_SIN_DATOS"), Form.WARNING_MESSAGE);
							return;
						}
						if (IMDatosConductor.this.chartdlg == null) {
							IMDatosConductor.this.chartform = ApplicationManager.getApplication().getFormManager("managergraficacond")
									.getFormCopy("formGraficaCond.xml");
							IMDatosConductor.this.chartdlg = IMDatosConductor.this.getFormDialog(IMDatosConductor.this.chartform, false);
							Frame fApp = ApplicationManager.getApplication().getFrame();
							Dimension dSize = fApp.getSize();
							IMDatosConductor.this.chartdlg.setSize((dSize.width * 4) / 5, (dSize.height * 4) / 5);
							IMDatosConductor.this.chartdlg.setLocationRelativeTo(fApp);
						}
						IMDatosConductor.this.chartform.getInteractionManager().setInitialState();
						// Inserto los valors de las claves y desactivo elcampo.

						for (Iterator iter = IMDatosConductor.this.keys.iterator(); iter.hasNext();) {
							String ck = (String) iter.next();
							Object vk = IMDatosConductor.this.managedForm.getDataFieldValue(ck);
							IMDatosConductor.this.chartform.setDataFieldValue(ck, vk);
							IMDatosConductor.this.chartform.disableDataField(ck);
							if (ck.equals(OpentachFieldNames.IDCONDUCTOR_FIELD)) {
								ck = ck + "2";
								IMDatosConductor.this.chartform.setDataFieldValue(ck, vk);
							}

						}
						// coloco los valores de los filtros.
						DateDataField cfi = (DateDataField) IMDatosConductor.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
						DateDataField cff = (DateDataField) IMDatosConductor.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
						Date fecIni = (Date) cfi.getDateValue();
						Date fecFin = (Date) cff.getDateValue();

						IMDatosConductor.this.chartform.setDataFieldValue(OpentachFieldNames.FILTERFECINI, fecIni);
						IMDatosConductor.this.chartform.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, fecFin);
						IMDatosConductor.this.chartform.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
								IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));

						// TODO: IDCONDUCTOR-DNI
						IMDatosConductor.this.chartform.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD,
								IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD));
						// IMDatosConductor.this.chartform.setDataFieldValue(OpentachFieldNames.DNI_FIELD,
						// IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD));

						// Borro las infracciones.
						((IMGraficaCond) IMDatosConductor.this.chartform.getInteractionManager()).setInfracciones(new EntityResult());
						((IMGraficaCond) IMDatosConductor.this.chartform.getInteractionManager()).doOnQuery();
						IMDatosConductor.this.chartdlg.setVisible(true);

					} catch (Exception ex) {
						MessageManager.getMessageManager().showExceptionMessage(ex, IMDatosConductor.logger);
					}
				}
			});
		}
		Table tblactiv = (Table) this.managedForm.getDataFieldReference("EInformeActivCond");
		// Saco el boton del formulario y lo coloco en la tabla de actividades.
		f.remove(btng);
		tblactiv.addComponentToControls(btng);


		// Boton de grafica de ficheros.
		btng = this.managedForm.getButton("btnGraficaFicheros");
		if (btng != null) {
			btng.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((Hashtable) IMDatosConductor.this.managedForm.getDataFieldValue("EFicherosSubidos")).isEmpty()) {
						IMDatosConductor.this.managedForm.message(ApplicationManager.getTranslation("M_GRAFICA_SIN_DATOS"), Form.WARNING_MESSAGE);
						return;
					}
					if (IMDatosConductor.this.chartFicherosDlg == null) {
						IMDatosConductor.this.chartFicherosForm = ApplicationManager.getApplication().getFormManager("managergraficaficheros")
								.getFormCopy("formGraficaFicheros.xml");
						IMDatosConductor.this.chartFicherosDlg = IMDatosConductor.this.getFormDialog(IMDatosConductor.this.chartFicherosForm, true);
						Frame fApp = ApplicationManager.getApplication().getFrame();
						Dimension dSize = fApp.getSize();
						IMDatosConductor.this.chartFicherosDlg.setSize(dSize);
						IMDatosConductor.this.chartFicherosDlg.setLocationRelativeTo(fApp);
					}
					IMDatosConductor.this.chartFicherosForm.getInteractionManager().setInitialState();
					// Inserto los valors de las claves y desactivo elcampo.

					for (Iterator iter = IMDatosConductor.this.keys.iterator(); iter.hasNext();) {
						String ck = (String) iter.next();
						Object vk = IMDatosConductor.this.managedForm.getDataFieldValue(ck);
						IMDatosConductor.this.chartFicherosForm.setDataFieldValue(ck, vk);
						IMDatosConductor.this.chartFicherosForm.disableDataField(ck);
						if (ck.equals(OpentachFieldNames.IDCONDUCTOR_FIELD)) {
							// ck = ck + "2";
							IMDatosConductor.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD, vk);
							IMDatosConductor.this.chartFicherosForm.disableDataField(OpentachFieldNames.IDORIGEN_FIELD);
							// chartFicherosForm.setValorCampo(ck, vk);
						}

					}
					// coloco los valores de los filtros.
					DateDataField cfi = (DateDataField) IMDatosConductor.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
					DateDataField cff = (DateDataField) IMDatosConductor.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
					Date fecIni = (Date) cfi.getDateValue();
					Date fecFin = (Date) cff.getDateValue();

					IMDatosConductor.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, fecIni);
					IMDatosConductor.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, fecFin);
					IMDatosConductor.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
							IMDatosConductor.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
					// Borro las infracciones.
					((IMGraficaFicheros) IMDatosConductor.this.chartFicherosForm.getInteractionManager()).refreshTables();
					IMDatosConductor.this.chartFicherosDlg.setVisible(true);
				}
			});
		}
		Table tblficheros = (Table) this.managedForm.getDataFieldReference("EFicherosSubidos");
		// Saco el boton del formulario y lo coloco en la tabla de actividades.
		if (tblficheros != null) {
			f.remove(btng);
			tblficheros.addComponentToControls(btng);
		}

		this.managedForm.setModifiable(OpentachFieldNames.IDORIGEN_FIELD, false);
		DataField cond = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.IDCONDUCTOR_FIELD);
		if (cond != null) {
			cond.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					IMDatosConductor.this.managedForm.setDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD, e.getNewValue());
				}
			});
		}

		DataField cmpDNI = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.DNI_FIELD);
		cmpDNI.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChanged(ValueEvent e) {
				if (e.getType() == ValueEvent.USER_CHANGE) {
					String nv = (String) e.getNewValue();
					if ((nv != null) && (nv.length() > 0)) {
						IMDatosConductor.this.managedForm.enableButton("calculaletranif");
						if (IMDatosConductor.this.getCurrentMode() == InteractionManager.INSERT) {
							if (DriverUtil.checkValidCIFNIF(IMDatosConductor.this.managedForm, nv, false) && (IMDatosConductor.this.managedForm
									.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD) == null)) {
								IMDatosConductor.this.managedForm.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD, "E" + nv + "0000");
							}
						}
					} else {
						IMDatosConductor.this.managedForm.disableButton("calculaletranif");
					}
				}
			}
		});
		Table tbPeriodos = (Table) f.getElementReference("EPeriodosTrabajo");
		if (tbPeriodos != null) {
			tbPeriodos.setCellRendererColorManager(new PeriodosCellRenderer());
		}
	}

	@Override
	public boolean checkUpdate() {
		try {
			if (DriverUtil.checkValidCIFNIF(this.managedForm, (String) this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD), false)) {
				return super.checkUpdate();
			} else {
				String msg = ApplicationManager.getTranslation("VERIFIQUE_DATOS_CONDUCTOR", this.managedForm.getResourceBundle());
				int rtn = JOptionPane.showConfirmDialog(this.managedForm.getParent(), msg);
				if (rtn == JOptionPane.YES_OPTION) {
					return super.checkUpdate();
				} else {
					return false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean checkInsert() {
		try {
			if (DriverUtil.checkValidCIFNIF(this.managedForm, (String) this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD), false)) {
				if (this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD) == null) {
					this.managedForm.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD,
							"E" + this.managedForm.getDataFieldValue(OpentachFieldNames.DNI_FIELD) + "0000");
				}
				return super.checkInsert();
			} else {
				String msg = ApplicationManager.getTranslation("VERIFIQUE_DATOS_CONDUCTOR", this.managedForm.getResourceBundle());
				int rtn = JOptionPane.showConfirmDialog(this.managedForm.getParent(), msg);
				if (rtn == JOptionPane.YES_OPTION) {
					return super.checkInsert();
				} else {
					return false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableButton("btnGrafica");
		this.managedForm.getDataFieldReference("IDCONDUCTOR").setEnabled(false);
	}

}
