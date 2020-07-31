package com.opentach.client.modules.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFileChooser;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ValueChangeListener;
import com.ontimize.gui.ValueEvent;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.TextComboDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.gui.preferences.ApplicationPreferences;
import com.opentach.client.MonitorProvider;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.model.comm.vu.PuertoSerie;
import com.opentach.model.comm.vu.TachoConfig;
import com.opentach.model.comm.vu.TachoReadMonitor;
import com.opentach.model.scard.SmartCardMonitor;

public class IMCfgServicios extends IMRoot {

	private static final String	TCPREVIEW				= "TCpreview";
	private static final String	VUPREVIEW				= "VUpreview";

	private static final String	AUTODOWNLOAD_TC			= "autodownloadTC";
	private static final String	AUTODOWNLOAD_VU			= "autodownloadVU";

	private static final String	VU_PORT					= "VUport";
	private static final String	VU_PERIOD				= "VUperiod";

	private static final String	VU_DAY_FROM				= "VUdayfrom";
	private static final String	VU_DAY_TO				= "VUdayto";

	private static final String	TGD_STORE				= "tgdstore";

	private final String[]		periodLUT				= { "RANGO_FECHAS", "ULTIMO_MES", "ULTIMOS_15_DIAS", "ULTIMA_SEMANA" };

	private JFileChooser		chooserTC				= null;

	private static final String	SELECT_TGD_DIR			= "selectTGDdir";
	// Descarga al almacen de forma predeterminada.
	private static final String	SAVE2STORE				= "save2store";
	// Descarga automatica ( solo valido si esta activo download2store)
	// No opeartivo segun acorddo en reunión, la sincronizacion debe ser respo.
	// del usuario.
	// private static final String AUTODOWNLOAD = "autodownload";

	private static final String	AUTOSENDRECENT			= "autosendrecent";
	private static final String	AUTOSENDRECENT_INTERVAL	= "recentchecktime";

	private final String[]		periodUploadLUT			= { "MEDIA_HORA", "HORA", "4_HORAS", "DIA", "SEMANA" };

	@Override
	public void registerInteractionManager(Form f, IFormManager gf) {
		super.registerInteractionManager(f, gf);
		TextComboDataField com = (TextComboDataField) f.getDataFieldReference(IMCfgServicios.AUTOSENDRECENT_INTERVAL);
		if (com != null) {
			com.setValues(new Vector(Arrays.asList(this.periodUploadLUT)));
			com.setEditable(false);
		}
		DataField tgdstore = (DataField) f.getDataFieldReference("tgdstore");
		if (tgdstore != null) {
			tgdstore.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent valueevent) {
					if (valueevent.getNewValue() != null) {
						IMCfgServicios.this.managedForm.enableDataField(IMCfgServicios.SAVE2STORE);
						IMCfgServicios.this.managedForm.enableDataField(IMCfgServicios.AUTOSENDRECENT);
						IMCfgServicios.this.managedForm.enableDataField(IMCfgServicios.AUTOSENDRECENT_INTERVAL);
					} else {
						IMCfgServicios.this.managedForm.disableDataField(IMCfgServicios.SAVE2STORE);
						IMCfgServicios.this.managedForm.disableDataField(IMCfgServicios.AUTOSENDRECENT);
						IMCfgServicios.this.managedForm.disableDataField(IMCfgServicios.AUTOSENDRECENT_INTERVAL);
					}
				}
			});
		}
		ActionListener dirlist = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Button src = (Button) e.getSource();
				String dirnow = null;
				if (src.getAttribute().equals(IMCfgServicios.SELECT_TGD_DIR)) {
					dirnow = (String) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.TGD_STORE);
				}
				if (dirnow == null) {
					dirnow = System.getProperty("user.home");
				}

				if (IMCfgServicios.this.chooserTC == null) {
					IMCfgServicios.this.chooserTC = new JFileChooser(dirnow);
					IMCfgServicios.this.chooserTC.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				}
				IMCfgServicios.this.chooserTC.setLocale(ApplicationManager.getLocale());
				IMCfgServicios.this.chooserTC.updateUI();
				IMCfgServicios.this.chooserTC.setDialogTitle(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO", ApplicationManager
						.getApplication().getResourceBundle()));
				IMCfgServicios.this.chooserTC.setToolTipText(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO", ApplicationManager
						.getApplication().getResourceBundle()));
				IMCfgServicios.this.chooserTC.setApproveButtonText(ApplicationManager.getTranslation("Seleccionar", ApplicationManager
						.getApplication().getResourceBundle()));
				File file = null;
				int returnVal = IMCfgServicios.this.chooserTC.showSaveDialog(ApplicationManager.getApplication().getFrame());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = IMCfgServicios.this.chooserTC.getSelectedFile();
					try {
						String dir = file.getAbsolutePath();
						if (src.getAttribute().equals(IMCfgServicios.SELECT_TGD_DIR)) {
							IMCfgServicios.this.managedForm.setDataFieldValue(IMCfgServicios.TGD_STORE, dir);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		};

		Button btndirtc = f.getButton(IMCfgServicios.SELECT_TGD_DIR);
		if (btndirtc != null) {
			btndirtc.addActionListener(dirlist);
		}
		com = (TextComboDataField) f.getDataFieldReference("portlist");
		if (com != null) {
			com.setValues(PuertoSerie.porSerialList());
			com.setEditable(false);
		}
		com = (TextComboDataField) f.getDataFieldReference(IMCfgServicios.VU_PERIOD);
		if (com != null) {
			com.setEditable(false);
			com.addValueChangeListener(new ValueChangeListener() {
				@Override
				public void valueChanged(ValueEvent e) {
					// System.err.println("" + e.getValorAntiguo() + " -> " +
					// e.getValorNuevo());
					// Thread.dumpStack();
					if (e.getType() == ValueEvent.USER_CHANGE) {
						IMCfgServicios.this.managedForm.disableDataField(IMCfgServicios.VU_DAY_FROM);
						IMCfgServicios.this.managedForm.disableDataField(IMCfgServicios.VU_DAY_TO);
						if (e.getNewValue() != null) {
							for (int i = 0; (IMCfgServicios.this.periodLUT != null) && (i < IMCfgServicios.this.periodLUT.length); i++) {
								if (e.getNewValue().equals(IMCfgServicios.this.periodLUT[i]) && (i == TachoConfig.PERIOD_DATE_RANGE)) {
									IMCfgServicios.this.managedForm.enableDataField(IMCfgServicios.VU_DAY_FROM);
									IMCfgServicios.this.managedForm.enableDataField(IMCfgServicios.VU_DAY_TO);
								}
							}
						}
					}
				}
			});
		}

		Button btn = f.getButton("aceptar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						DataField tgdstore = (DataField) IMCfgServicios.this.managedForm.getDataFieldReference("tgdstore");
						if (tgdstore != null) {
							String value = (String) tgdstore.getValue();
							if ((value == null) || (value.trim().length() == 0)) {
								IMCfgServicios.this.managedForm.message("M_NECESARIO_ESPECIFICAR_UN_ALMACEN", Form.ERROR_MESSAGE);
								return;
							}
						}
						final OpentachClientLocator bref = (OpentachClientLocator) IMCfgServicios.this.managedForm.getFormManager()
								.getReferenceLocator();
						final MonitorProvider mp = bref;
						final TachoReadMonitor trm = mp.getTachoReadMonitor();
						final UploadMonitor upm = mp.getUploadMonitor();
						final SmartCardMonitor dcm = bref.getLocalService(SmartCardMonitor.class);
						final String user = bref.getUser();
						final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
						// Autodescarga TC
						Integer val = (Integer) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.AUTODOWNLOAD_TC);
						prefs.setPreference(user, SmartCardMonitor.SMARTCARD_AUTODOWNLOAD, val == null ? "1" : val.toString());
						dcm.setAutoDownload(val != null ? (val.intValue() == 0 ? false : true) : true);
						// Show Preview TC
						val = (Integer) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.TCPREVIEW);
						// Autodescarga VU
						val = (Integer) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.AUTODOWNLOAD_VU);
						prefs.setPreference(user, TachoConfig.TACHO_AUTODOWNLOAD, val == null ? "1" : val.toString());
						trm.setEnable(val != null ? (val.intValue() == 0 ? false : true) : true);
						// Show Preview VU
						val = (Integer) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.VUPREVIEW);
						prefs.setPreference(user, TachoConfig.TACHO_NOTIFYONFINISH, val == null ? "1" : val.toString());
						trm.setNotifyOnFinish(val != null ? (val.intValue() == 0 ? false : true) : true);
						// VU PORT
						String sval = (String) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.VU_PORT);
						prefs.setPreference(user, TachoConfig.TACHO_PORT, sval == null ? "" : sval);
						trm.setPort(sval);
						// }
						val = (Integer) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.SAVE2STORE);
						String storename = (String) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.TGD_STORE);
						bref.changeTGDstore(new File(storename), val == null ? true : val.intValue() != 0);
						// Intervalo de comprobación de ficheros recientes.
						String interval = (String) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.AUTOSENDRECENT_INTERVAL);
						int time = 0;
						if (interval != null) {
							if (interval.equals(IMCfgServicios.this.periodUploadLUT[0])) {
								time = 30 * 60 * 1000;
							} else if (interval.equals(IMCfgServicios.this.periodUploadLUT[1])) {
								time = 60 * 60 * 1000;
							} else if (interval.equals(IMCfgServicios.this.periodUploadLUT[2])) {
								time = 4 * 60 * 60 * 1000;
							} else if (interval.equals(IMCfgServicios.this.periodUploadLUT[3])) {
								time = 24 * 60 * 60 * 1000;
							} else if (interval.equals(IMCfgServicios.this.periodUploadLUT[4])) {
								time = 7 * 24 * 60 * 60 * 1000;
							}
						} else {
							time = 60 * 60 * 1000;
						}
						// if (val != null) {
						prefs.setPreference(user, UploadMonitor.INTERVAL_TAG, "" + time);

						upm.setTimeout(time);
						// }

						// Autosubida.. Enviar automaticamente al CSD ficheros recientes.
						val = (Integer) IMCfgServicios.this.managedForm.getDataFieldValue(IMCfgServicios.AUTOSENDRECENT);
						if (val == null) {
							val = Integer.valueOf(0);
						}
						prefs.setPreference(bref.getUser(), UploadMonitor.RUNNING_TAG, val.toString());
						if ((val != null) && (val.intValue() != 0)) {
							upm.restart();
						} else {
							upm.pause();
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					if (IMCfgServicios.this.managedForm.getJDialog() != null) {
						IMCfgServicios.this.managedForm.getJDialog().setVisible(false);
					}
				}
			});
		}

		btn = this.managedForm.getButton("cancelar");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMCfgServicios.this.readConfiguration();
					IMCfgServicios.this.managedForm.getJDialog().setVisible(false);
				}
			});
		}
		btn = this.managedForm.getButton("defecto");
		if (btn != null) {
			btn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMCfgServicios.this.accBotDefecto();
				}
			});
		}
	}

	private void accBotDefecto() {
		int q = this.managedForm.message("M_DESEA_RESTAURAR_CONFIGURACION_POR_DEFECTO", Form.QUESTION_MESSAGE);
		if (q == 0) {
			final OpentachClientLocator bref = (OpentachClientLocator) this.managedForm.getFormManager().getReferenceLocator();
			final TachoReadMonitor trm = bref.getTachoReadMonitor();
			final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
			final String user = bref.getUser();
			prefs.setPreference(user, SmartCardMonitor.SMARTCARD_AUTODOWNLOAD, "1");
			prefs.setPreference(user, SmartCardMonitor.SMARTCARD_NOTIFYONFINISH, "1");
			prefs.setPreference(user, TachoConfig.TACHO_AUTODOWNLOAD, "1");
			prefs.setPreference(user, TachoConfig.TACHO_NOTIFYONFINISH, "1");
			prefs.setPreference(user, TachoConfig.TACHO_PORT, "");
			prefs.setPreference(user, UploadMonitor.INTERVAL_TAG, "" + (1000 * 60 * 60 * 24));
			prefs.setPreference(user, UploadMonitor.RUNNING_TAG, "1");
			prefs.setPreference(user, TachoFileStore.TACHOFILE_STORE, System.getProperty("user.home") + File.separator + "OPENTACH_ALMACEN_FICHEROS"
					+ File.separator);
			prefs.setPreference(user, TachoFileStore.TACHOFILE_SAVE2STORE, "1");
			prefs.setPreference(user, TachoConfig.TACHO_DOWLOAD_PERIOD, "2");
			trm.setDownloadPeriod(2, null, null);
			this.readConfiguration();
		}
	}

	@Override
	public void setQueryInsertMode() {
		super.setQueryInsertMode();
		this.managedForm.enableButtons();
		this.managedForm.enableDataFields();
		this.readConfiguration();
	}

	private void readConfiguration() {
		OpentachClientLocator bref = (OpentachClientLocator) this.formManager.getReferenceLocator();
		final ApplicationPreferences prefs = ApplicationManager.getApplication().getPreferences();
		final String user = bref.getUser();
		try {
			String store = prefs.getPreference(user, TachoFileStore.TACHOFILE_STORE);
			this.managedForm.setDataFieldValue(IMCfgServicios.TGD_STORE, store);
			this.managedForm.setModifiable(IMCfgServicios.TGD_STORE, false);

			String pref = prefs.getPreference(user, UploadMonitor.RUNNING_TAG);
			this.managedForm.setDataFieldValue(IMCfgServicios.AUTOSENDRECENT, pref != null ? Integer.valueOf(pref) : Integer.valueOf(0));
			this.managedForm.setModifiable(IMCfgServicios.AUTOSENDRECENT, false);

			pref = prefs.getPreference(user, UploadMonitor.INTERVAL_TAG);
			if (pref != null) {
				if (pref.equals("" + (30 * 60 * 1000))) {
					pref = this.periodUploadLUT[0];
				} else if (pref.equals("" + (60 * 60 * 1000))) {
					pref = this.periodUploadLUT[1];
				} else if (pref.equals("" + (4 * 60 * 60 * 1000))) {
					pref = this.periodUploadLUT[2];
				} else if (pref.equals("" + (24 * 60 * 60 * 1000))) {
					pref = this.periodUploadLUT[3];
				} else if (pref.equals("" + (7 * 24 * 60 * 60 * 1000))) {
					pref = this.periodUploadLUT[4];
				}
			} else {
				pref = this.periodUploadLUT[2];
			}
			this.managedForm.setDataFieldValue(IMCfgServicios.AUTOSENDRECENT_INTERVAL, pref);

			this.managedForm.setModifiable(IMCfgServicios.AUTOSENDRECENT_INTERVAL, false);
			pref = prefs.getPreference(user, TachoFileStore.TACHOFILE_SAVE2STORE);
			this.managedForm.setDataFieldValue(IMCfgServicios.SAVE2STORE, pref != null ? Integer.valueOf(pref) : Integer.valueOf(0));
			this.managedForm.setModifiable(IMCfgServicios.SAVE2STORE, false);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		// Parametros configuración descarga TGD.
		try {
			String pref = prefs.getPreference(user, SmartCardMonitor.SMARTCARD_AUTODOWNLOAD);
			this.managedForm.setDataFieldValue(IMCfgServicios.AUTODOWNLOAD_TC, pref != null ? Integer.valueOf(pref) : Integer.valueOf(0));
			this.managedForm.setModifiable(IMCfgServicios.AUTODOWNLOAD_TC, false);

			pref = prefs.getPreference(user, TachoConfig.TACHO_AUTODOWNLOAD);
			this.managedForm.setDataFieldValue(IMCfgServicios.AUTODOWNLOAD_VU, pref != null ? Integer.valueOf(pref) : Integer.valueOf(0));
			this.managedForm.setModifiable(IMCfgServicios.AUTODOWNLOAD_VU, false);

			pref = prefs.getPreference(user, SmartCardMonitor.SMARTCARD_NOTIFYONFINISH);
			this.managedForm.setDataFieldValue(IMCfgServicios.TCPREVIEW, pref != null ? Integer.valueOf(pref) : Integer.valueOf(0));
			this.managedForm.setModifiable(IMCfgServicios.TCPREVIEW, false);

			TextComboDataField cVUPERIOD = (TextComboDataField) this.managedForm.getDataFieldReference(IMCfgServicios.VU_PERIOD);
			if (cVUPERIOD != null) {
				Vector v = cVUPERIOD.getValues();
				if (v != null) {
					MonitorProvider mp = (MonitorProvider) this.formManager.getReferenceLocator();
					String key = this.periodLUT[mp.getTachoReadMonitor().getDownloadPeriod()];
					int i = v.indexOf(key);
					if (i >= 0) {
						cVUPERIOD.setSelected(i);
						if (i == TachoConfig.PERIOD_DATE_RANGE) {
							this.managedForm.enableDataField(IMCfgServicios.VU_DAY_FROM);
							this.managedForm.enableDataField(IMCfgServicios.VU_DAY_TO);
						} else {
							this.managedForm.disableDataField(IMCfgServicios.VU_DAY_FROM);
							this.managedForm.disableDataField(IMCfgServicios.VU_DAY_TO);
						}
					}
				}
			}
			String str = prefs.getPreference(user, TachoConfig.TACHO_DOWNLOAD_DAYFROM);
			if (str != null) {
				Date date = DateFormat.getDateInstance().parse(str);
				this.managedForm.setDataFieldValue(IMCfgServicios.VU_DAY_FROM, date);
			}
			str = prefs.getPreference(user, TachoConfig.TACHO_DOWNLOAD_DAYTO);
			if (str != null) {
				Date date = DateFormat.getDateInstance().parse(str);
				this.managedForm.setDataFieldValue(IMCfgServicios.VU_DAY_TO, date);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
