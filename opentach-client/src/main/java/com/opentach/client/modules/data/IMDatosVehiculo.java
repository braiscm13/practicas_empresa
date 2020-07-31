package com.opentach.client.modules.data;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.BasicInteractionManager;
import com.ontimize.gui.ExtendedJPopupMenu;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.table.DateCellRenderer;
import com.ontimize.gui.table.Table;
import com.ontimize.gui.table.TableButton;
import com.ontimize.gui.tree.Tree;
import com.opentach.client.comp.action.AbstractDownloadFileActionListener;
import com.opentach.client.comp.render.MinutesCellRender;
import com.opentach.client.modules.IMDataRoot;
import com.opentach.client.modules.chart.IMGraficaFicheros;
import com.opentach.client.util.SpeedChartRender;
import com.opentach.client.util.UserTools;
import com.opentach.common.OpentachFieldNames;

public class IMDatosVehiculo extends IMDataRoot {

	protected JDialog	reportdlg			= null;
	protected Form		reportform			= null;

	private Form		FEliminar			= null;
	private JDialog		JEliminar			= null;

	protected JDialog	chartFicherosDlg	= null;
	protected Form		chartFicherosForm	= null;

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		AbstractDownloadFileActionListener.installListener(this.managedForm);
		this.fieldsChain.clear();
		// this.chartEntity = "EUsoVehiculo";
		this.setChartEntity("EInformeUsoVehiculoVehiculo");
		this.setDateEntity("EUFechaVehi");
		this.setTableParentKeys(Arrays.asList(new String[] { OpentachFieldNames.CIF_FIELD, OpentachFieldNames.MATRICULA_FIELD }));
		this.buildBtnShowSpeed();
		// addDateTags(new TimeStampDateTags("FEC_PROXIMO"));
		this.addDateTags(new TimeStampDateTags("FEC_HORA"));
		this.addDateTags(new TimeStampDateTags(OpentachFieldNames.FECINI_FIELD));
		this.managedForm.setModifiable(OpentachFieldNames.IDORIGEN_FIELD, false);
		DataField cond = (DataField) this.managedForm.getDataFieldReference(OpentachFieldNames.MATRICULA_FIELD);
		if (cond != null) {
			cond.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					IMDatosVehiculo.this.managedForm.setDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD, e.getNewValue());
				}
			});
		}

		// ///////

		this.removeDeleteListener();
		this.deleteListener = new BasicInteractionManager.DeleteListener() {
			@Override
			public void actionPerformed(ActionEvent evento) {
				if (IMDatosVehiculo.this.JEliminar == null) {
					IMDatosVehiculo.this.FEliminar = IMDatosVehiculo.this.formManager.getFormCopy("formEliminarVehiculo.xml");
					IMDatosVehiculo.this.FEliminar.getInteractionManager().setInitialState();
					IMDatosVehiculo.this.JEliminar = IMDatosVehiculo.this.FEliminar.putInModalDialog();
				}
				IMDatosVehiculo.this.FEliminar.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
						IMDatosVehiculo.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
				IMDatosVehiculo.this.FEliminar.setDataFieldValue(OpentachFieldNames.CIF_FIELD,
						IMDatosVehiculo.this.managedForm.getDataFieldValue(OpentachFieldNames.CIF_FIELD));
				IMDatosVehiculo.this.FEliminar.setDataFieldValue(OpentachFieldNames.MATRICULA_FIELD,
						IMDatosVehiculo.this.managedForm.getDataFieldValue(OpentachFieldNames.MATRICULA_FIELD));
				IMDatosVehiculo.this.JEliminar.setVisible(true);
				IMDatosVehiculo.this.managedForm.refreshCurrentDataRecord();
			}

		};
		this.managedForm.getButton(InteractionManager.DELETE_KEY).addActionListener(this.deleteListener);

		// //

		Button btn = f.getButton("btnDownload");
		if (btn != null) {
			// Saco el boton del formulario y lo coloco en la tabla de
			// actividades.
			Table tblFicheros = (Table) this.managedForm.getDataFieldReference(AbstractDownloadFileActionListener.TBLFICHEROS);
			if (tblFicheros != null) {
				f.remove(btn);
				tblFicheros.addComponentToControls(btn);
			}
		}

		// Boton de grafica de ficheros.
		Button btng = this.managedForm.getButton("btnGraficaFicheros");
		if (btng != null) {
			btng.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (((Hashtable) IMDatosVehiculo.this.managedForm.getDataFieldValue("EFicherosSubidos")).isEmpty()) {
						IMDatosVehiculo.this.managedForm.message(ApplicationManager.getTranslation("M_GRAFICA_SIN_DATOS"), Form.WARNING_MESSAGE);
						return;
					}
					if (IMDatosVehiculo.this.chartFicherosDlg == null) {
						IMDatosVehiculo.this.chartFicherosForm = ApplicationManager.getApplication().getFormManager("managergraficaficheros")
								.getFormCopy("formGraficaFicheros.xml");
						IMDatosVehiculo.this.chartFicherosDlg = IMDatosVehiculo.this.getFormDialog(IMDatosVehiculo.this.chartFicherosForm, false);
						IMDatosVehiculo.this.chartFicherosDlg.setSize(800, 600);
						IMDatosVehiculo.this.chartFicherosDlg.pack();
					}
					IMDatosVehiculo.this.chartFicherosForm.getInteractionManager().setInitialState();
					// Inserto los valors de las claves y desactivo elcampo.

					for (Iterator iter = IMDatosVehiculo.this.keys.iterator(); iter.hasNext();) {
						String ck = (String) iter.next();
						Object vk = IMDatosVehiculo.this.managedForm.getDataFieldValue(ck);
						IMDatosVehiculo.this.chartFicherosForm.setDataFieldValue(ck, vk);
						IMDatosVehiculo.this.chartFicherosForm.disableDataField(ck);
						if (ck.equals(OpentachFieldNames.MATRICULA_FIELD)) {
							// ck = ck + "2";
							IMDatosVehiculo.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.IDORIGEN_FIELD, vk);
							IMDatosVehiculo.this.chartFicherosForm.disableDataField(OpentachFieldNames.IDORIGEN_FIELD);
							// chartFicherosForm.setValorCampo(ck, vk);
						}

					}
					// coloco los valores de los filtros.
					DateDataField cfi = (DateDataField) IMDatosVehiculo.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECINI);
					DateDataField cff = (DateDataField) IMDatosVehiculo.this.managedForm.getDataFieldReference(OpentachFieldNames.FILTERFECFIN);
					Date fecIni = (Date) cfi.getDateValue();
					Date fecFin = (Date) cff.getDateValue();

					IMDatosVehiculo.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.FILTERFECINI, fecIni);
					IMDatosVehiculo.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.FILTERFECFIN, fecFin);
					IMDatosVehiculo.this.chartFicherosForm.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
							IMDatosVehiculo.this.managedForm.getDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD));
					// Borro las infracciones.
					((IMGraficaFicheros) IMDatosVehiculo.this.chartFicherosForm.getInteractionManager()).refreshTables();
					IMDatosVehiculo.this.chartFicherosDlg.setVisible(true);
				}
			});
		}
		Table tblficheros = (Table) this.managedForm.getDataFieldReference("EFicherosSubidos");
		if (tblficheros != null) {
			f.remove(btng);
			tblficheros.addComponentToControls(btng);
		}
		Table tbFallos = (Table) this.managedForm.getDataFieldReference("EFallos");
		if (tbFallos != null) {
			MinutesCellRender mincr = new MinutesCellRender("DURACION_SEGUNDOS");
			mincr.setSecondsResolution(true);
			tbFallos.setRendererForColumn("DURACION_SEGUNDOS", mincr);
		}
		Table tblRegKM = (Table) this.managedForm.getDataFieldReference("EKmVehiculo");
		if (tblRegKM != null) {
			final DateCellRenderer dcr = new DateCellRenderer(true, false);
			tblRegKM.setRendererForColumn("FECHA", dcr);
		}
	}

	protected void buildBtnShowSpeed() {
		try {
			// Botón
			Table tblVelocidad = (Table) this.managedForm.getElementReference("EVelocidad");
			if (tblVelocidad != null) {
				URL urlCheck = this.getClass().getClassLoader().getResource("com/ontimize/gui/images/iOk.png");
				Icon iconoCheck = new ImageIcon(urlCheck);
				TableButton tbCheck = new TableButton(iconoCheck) {

					@Override
					public Dimension getPreferredSize() {
						return new Dimension(160, 30);
					}

					@Override
					public Dimension getMinimumSize() {
						return new Dimension(160, 30);
					}
				};
				tblVelocidad.addButtonToControls(tbCheck);
				tbCheck.setEnabled(false);
				tbCheck.setToolTipText("Muestra el detalle de velocidades");
				tbCheck.setText("Ver velocidades");
				tbCheck.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Table tbl = (Table) IMDatosVehiculo.this.managedForm.getElementReference("EVelocidad");
						int[] sr = tbl.getJTable().getSelectedRows();
						if (sr.length >= 1) {
							Date min = (Date) tbl.getRowValue(sr[0], OpentachFieldNames.FECINI_FIELD);
							String smin = new SimpleDateFormat("dd/MM/yy HH:mm").format(min);
							String matricula = (String) tbl.getRowValue(sr[0], OpentachFieldNames.MATRICULA_FIELD);
							ByteArrayOutputStream out = new ByteArrayOutputStream();
							try {
								for (int i = 0; (sr != null) && (i < sr.length); i++) {
									byte[] b = (byte[]) tbl.getRowValue(sr[i], "VELOCIDAD");
									out.write(b);
								}
								SpeedChartRender spc = new SpeedChartRender();
								spc.setData(out.toByteArray());
								spc.setLabelsVisible(true);
								spc.showChart("Velocidades desde  " + smin + " [" + matricula + "]");
							} catch (Exception ex) {

							} finally {
								try {
									out.close();
								} catch (IOException e1) {
								}
							}

						} else {
							IMDatosVehiculo.this.managedForm.message("Debe seleccionar las filas que desea consultar", Form.INFORMATION_MESSAGE);
						}
					}
				});
			}

		} catch (Exception ex) {
		}
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		if (UserTools.isOperador() || UserTools.isEmpresa() || UserTools.isEmpresaGF()) {
			Tree arbol = ((FormManager) this.managedForm.getFormManager()).getTree();
			if (arbol.getComponentCount() > 1) {
				ExtendedJPopupMenu jmi = (ExtendedJPopupMenu) arbol.getComponent(1);
				if (jmi.getComponentCount() > 4) {
					JMenuItem j = ((JMenuItem) ((ExtendedJPopupMenu) arbol.getComponent(1)).getComponent(5));
					j.setVisible(false);
				}
			}
		}
	}
}
