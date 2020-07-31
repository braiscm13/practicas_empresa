package com.opentach.client.modules.files;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.imatia.tacho.model.GenerationEnum;
import com.imatia.tacho.model.IElementaryFile;
import com.imatia.tacho.model.TachoFile;
import com.imatia.tacho.model.tc.MultiGenData;
import com.imatia.tacho.model.tc.TCFile;
import com.imatia.tacho.model.vu.VUElementActivities;
import com.imatia.tacho.model.vu.VUFile;
import com.ontimize.annotation.FormComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ExtendedOperationThread;
import com.ontimize.gui.Form;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.util.FileUtils;
import com.opentach.client.AbstractOpentachClientLocator;
import com.opentach.client.MonitorProvider;
import com.opentach.client.OpentachClientLocator;
import com.opentach.client.comp.activitychart.ActivityChartControlPanel;
import com.opentach.client.comp.activitychart.BarChartDataRender;
import com.opentach.client.comp.activitychart.BasicChartDataSet;
import com.opentach.client.comp.activitychart.ChartDataField;
import com.opentach.client.comp.activitychart.ChartDataRender;
import com.opentach.client.comp.activitychart.ChartDataSet;
import com.opentach.client.comp.activitychart.Task;
import com.opentach.client.comp.activitychart.taskwrapper.ActividadesTaskWrapper;
import com.opentach.client.comp.activitychart.taskwrapper.RulerTaskWrapper;
import com.opentach.client.modules.IMRoot;
import com.opentach.client.modules.chart.ActivityRulerMouseListener;
import com.opentach.client.modules.chart.PaisesMouseListener;
import com.opentach.client.util.SoundManager;
import com.opentach.client.util.TGDFileInfo;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.client.util.upload.UploadMonitor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.companies.IContractService;
import com.opentach.common.tacho.FileParser;
import com.opentach.common.tacho.IFileParser;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.tacho.TcFileParser;
import com.opentach.common.tacho.VuFileParser;
import com.opentach.common.tacho.data.AbstractData;
import com.opentach.common.tacho.data.Actividad;
import com.opentach.common.tacho.data.Conductor;
import com.opentach.common.tacho.data.VehiculoInfrac;
import com.opentach.common.user.IUserData;
import com.opentach.model.comm.vu.TachoReadMonitor;
import com.opentach.model.scard.SmartCardMonitor;

public class IMDescargaTGD extends IMRoot {
	private final static Logger			logger	= LoggerFactory.getLogger(IMDescargaTGD.class);

	protected TextDataField				repository;
	protected ChartDataField			chartwpp;

	private JFileChooser				jfcOpen;
	private JFileChooser				jfcSave;
	private File						currenttgdfile;
	@FormComponent(attr = "chartControl")
	protected ActivityChartControlPanel	chartControl;

	@Override
	public void registerInteractionManager(Form form, IFormManager gf) {
		super.registerInteractionManager(form, gf);
		this.chartwpp = (ChartDataField) form.getElementReference("chart");
		PaisesMouseListener paisesMouseListener = new PaisesMouseListener(this.chartwpp);
		this.chartwpp.getChart().addMouseListener(paisesMouseListener);
		this.chartwpp.getChart().addMouseMotionListener(paisesMouseListener);
		this.chartwpp.getChart().addMouseListener(new ActivityRulerMouseListener(this.chartwpp));

		Button btndescargarTC = form.getButton("descargarTC");
		if (btndescargarTC != null) {
			btndescargarTC.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMDescargaTGD.this.doOnSaveTCFile();
				}
			});
		}

		Button btndescargarVU = form.getButton("descargarVU");
		if (btndescargarVU != null) {
			btndescargarVU.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					IMDescargaTGD.this.doOnSaveVUFile();
				}
			});
		}

		Button btnguardar = form.getButton("guardarfichero");
		if (btnguardar != null) {
			btnguardar.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if (IMDescargaTGD.this.currenttgdfile != null) {
							if (IMDescargaTGD.this.jfcSave == null) {
								TachoFileStore store = ((MonitorProvider) IMDescargaTGD.this.formManager.getReferenceLocator()).getFileStore();
								IMDescargaTGD.this.jfcSave = new JFileChooser(store.getTGDStore());
								IMDescargaTGD.this.jfcSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							}
							IMDescargaTGD.this.jfcSave.setLocale(ApplicationManager.getLocale());
							IMDescargaTGD.this.jfcSave.updateUI();
							IMDescargaTGD.this.jfcSave.setDialogTitle(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
							IMDescargaTGD.this.jfcSave.setToolTipText(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
							IMDescargaTGD.this.jfcSave.setApproveButtonText(ApplicationManager.getTranslation("T_GUARDAR"));
							IMDescargaTGD.this.jfcSave.setMultiSelectionEnabled(false);
							File dir = null;
							int returnVal = IMDescargaTGD.this.jfcSave.showSaveDialog(ApplicationManager.getApplication().getFrame());
							if (returnVal == JFileChooser.APPROVE_OPTION) {
								dir = IMDescargaTGD.this.jfcSave.getSelectedFile();
								if ((dir != null) && dir.isDirectory()) {
									if (IMDescargaTGD.this.currenttgdfile != null) {
										File outfile = new File(dir.getAbsolutePath() + File.separator + IMDescargaTGD.this.currenttgdfile.getName());
										FileUtils.copyFile(IMDescargaTGD.this.currenttgdfile, outfile);
									}
								}
							}
						} else {
							IMDescargaTGD.this.managedForm.message("M_NO_EXISTE_FICHERO_TGD", Form.ERROR_MESSAGE);
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			});
		}

		Button btnabrir = this.managedForm.getButton("abrirfichero");
		if (btnabrir != null) {
			btnabrir.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (IMDescargaTGD.this.jfcOpen == null) {
						TachoFileStore store = ((MonitorProvider) IMDescargaTGD.this.formManager.getReferenceLocator()).getFileStore();
						IMDescargaTGD.this.jfcOpen = new JFileChooser(store.getTGDStore());
						IMDescargaTGD.this.jfcOpen.setFileSelectionMode(JFileChooser.FILES_ONLY);
					}
					IMDescargaTGD.this.jfcOpen.setLocale(ApplicationManager.getLocale());
					IMDescargaTGD.this.jfcOpen.updateUI();
					IMDescargaTGD.this.jfcOpen.setDialogTitle(ApplicationManager.getTranslation("M_SELECCIONE_FICHERO"));
					IMDescargaTGD.this.jfcOpen.setToolTipText(ApplicationManager.getTranslation("M_SELECCIONE_FICHERO"));
					IMDescargaTGD.this.jfcOpen.setMultiSelectionEnabled(false);
					int returnVal = IMDescargaTGD.this.jfcOpen.showOpenDialog(ApplicationManager.getApplication().getFrame());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						final File file = IMDescargaTGD.this.jfcOpen.getSelectedFile();
						if (file != null) {
							ExtendedOperationThread exop = new ExtendedOperationThread(ApplicationManager.getTranslation("M_PROCESANDO_DATOS")) {
								@Override
								public void run() {
									this.hasStarted = true;
									IMDescargaTGD.this.processFile(file);
									this.hasFinished = true;
								}
							};
							ApplicationManager.proccessOperation(ApplicationManager.getApplication().getFrame(), exop, 50);
						}
					}
				}

			});
		}

	}

	@Override
	public void setInitialState() {
		super.setInitialState();
		this.setUpdateMode();
	}

	@Override
	public void setUpdateMode() {
		super.setUpdateMode();
		this.managedForm.enableButtons();
	}

	public void update(Observable o, Object arg) {
		String filename = (String) arg;
		IFormManager gf = this.managedForm.getFormManager();
		OpentachClientLocator bref = (OpentachClientLocator) gf.getReferenceLocator();
		UploadMonitor upm = bref.getUploadMonitor();
		if ((filename == null) || (filename.indexOf("ERROR") != -1)) {
			this.managedForm.message("M_ERROR_DESCARGANDO_DATOS", Form.ERROR_MESSAGE);
		} else {
			File tgdfile = new File(filename);
			this.processFile(tgdfile);

			// Una vez descargado envio el fichero al CSD con el codigo del
			// origen
			Hashtable cv = new Hashtable();
			try {
				IUserData ds = bref.getUserData();
				// String cgContratoVigente = bref.getContratoVigente(cif);
				if (ds.getCIF() != null) {
					cv.put(OpentachFieldNames.CIF_FIELD, ds.getCIF());
				}
				// La descarga unicamente se permite de ficheros del contrato
				// vigente.

				String cgContrato = bref.getRemoteService(IContractService.class).getContratoVigente(ds.getCIF(), bref.getSessionId());

				if (cgContrato != null) {
					cv.put(OpentachFieldNames.CG_CONTRATO_FIELD, cgContrato);
				}
			} catch (Exception e) {
				System.err.println("" + e.getMessage());
			}
			if (this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD) != null) {
				cv.put(OpentachFieldNames.IDORIGEN_FIELD, this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD));
			} else if (this.managedForm.getDataFieldValue(OpentachFieldNames.MATRICULA_FIELD) != null) {
				cv.put(OpentachFieldNames.IDORIGEN_FIELD, this.managedForm.getDataFieldValue(OpentachFieldNames.MATRICULA_FIELD));
			}
			upm.sendTGDFiles(Arrays.asList(new TGDFileInfo(tgdfile)), "FICHERO", cv, this.managedForm, true, SoundManager.uriSonido);
		}
	}

	public void processFile(File file) {
		try {
			TachoFile tgdfile = TachoFile.readTachoFile(file);
			this.currenttgdfile = file;
			this.managedForm.deleteDataFields();
			IMDescargaTGD.logger.debug("Procesando fichero: {}", file.getPath());
			Map<String, List<? extends AbstractData>> taskMap = new HashMap<>();
			IFileParser parser = FileParser.createParserFor(tgdfile);
			this.dummyVerifyFile(tgdfile);
			taskMap.put(Conductor.class.getName(), parser.getConductores());
			taskMap.put(Actividad.class.getName(), parser.getActividades());
			List<Actividad> activs = (List<Actividad>) taskMap.get(Actividad.class.getName());
			this.setActivities(activs);
			this.setTGDFileInfo(IMDescargaTGD.getOwnerObject(parser));
		} catch (Exception err) {
			IMDescargaTGD.logger.error(null, err);
			this.managedForm.message(err.getMessage(), Form.ERROR_MESSAGE);
		}
	}

	private void dummyVerifyFile(TachoFile tgdfile) {
		tgdfile.establishVerificationResult(true, Collections.EMPTY_LIST);
		List<IElementaryFile> efs = new ArrayList<>();
		if (tgdfile instanceof TCFile) {
			List<MultiGenData<? extends IElementaryFile>> tmp = new ArrayList<>();
			tmp.add(((TCFile) tgdfile).getEfApplicationIdentification());
			tmp.add(((TCFile) tgdfile).getEfCACertificate());
			tmp.add(((TCFile) tgdfile).getEfCardCertificate());
			tmp.add(((TCFile) tgdfile).getEfCardDownload());
			tmp.add(((TCFile) tgdfile).getEfCardSignCertificate());
			tmp.add(((TCFile) tgdfile).getEfControlActivityData());
			tmp.add(((TCFile) tgdfile).getEfCurrentUsage());
			tmp.add(((TCFile) tgdfile).getEfDriverActivityData());
			tmp.add(((TCFile) tgdfile).getEfDrivingLicenseInfo());
			tmp.add(((TCFile) tgdfile).getEfEventsData());
			tmp.add(((TCFile) tgdfile).getEfFaultsData());
			tmp.add(((TCFile) tgdfile).getEfGNSSPlaces());
			tmp.add(((TCFile) tgdfile).getEfIdentification());
			tmp.add(((TCFile) tgdfile).getEfLinkCertificate());
			tmp.add(((TCFile) tgdfile).getEfPlaces());
			tmp.add(((TCFile) tgdfile).getEfSpecificConditions());
			tmp.add(((TCFile) tgdfile).getEfVehiclesUsed());
			tmp.add(((TCFile) tgdfile).getEfVehicleUnitsUsed());
			efs.add(((TCFile) tgdfile).getEfIc());
			efs.add(((TCFile) tgdfile).getEfIcc());
			for (GenerationEnum en : new GenerationEnum[] { GenerationEnum.FIRST, GenerationEnum.SECOND }) {
				for (MultiGenData<? extends IElementaryFile> data : tmp) {
					if (data != null) {
						efs.add(data.getData(en));
					}
				}
			}
		} else if (tgdfile instanceof VUFile) {
			List<VUElementActivities> activities = ((VUFile) tgdfile).getActivities();
			if (activities != null) {
				for (VUElementActivities activ : activities) {
					efs.add(activ);
				}
			}
			efs.add(((VUFile) tgdfile).getIncidencesAndFails());
			efs.add(((VUFile) tgdfile).getSpeed());
			efs.add(((VUFile) tgdfile).getSummary());
			efs.add(((VUFile) tgdfile).getTechnicalData());
		}
		for (IElementaryFile ef : efs) {
			if (ef != null) {
				ef.establishVerificationResult(true);
			}
		}
	}

	public static Object getOwnerObject(IFileParser parser) {
		if (parser instanceof VuFileParser) {
			return (parser.getVehiculoInfrac().get(0));
		} else if (parser instanceof TcFileParser) {
			return (parser.getConductores().get(0));
		}
		return null;
	}

	private void setActivities(List<Actividad> activs) {
		if ((this.chartwpp != null) && (this.chartwpp.getChart() != null)) {
			this.chartwpp.getChart().removeAllData();
		}
		if ((activs != null) && !activs.isEmpty()) {
			// Paso actividades a activitytaskwrapper
			ArrayList<Task> tresultact = new ArrayList<>();
			ArrayList<Task> tresultruler = new ArrayList<>();
			for (Actividad act : activs) {
				if ((act.fecComienzo != null) && (act.fecFin != null)) {
					ActividadesTaskWrapper task = new ActividadesTaskWrapper(act);
					tresultact.add(task);
				}
			}

			ActividadesTaskWrapper actfin = (ActividadesTaskWrapper) tresultact.get(activs.size() - 1);
			Date dfin = actfin.getEnd();
			ActividadesTaskWrapper actIni = (ActividadesTaskWrapper) tresultact.get(0);
			Date dini = actIni.getStart();

			this.chartControl.setDateRange(dini, dfin);

			Calendar cal = Calendar.getInstance();
			cal.setTime(dini);
			while (cal.getTime().before(dfin)) {
				tresultruler.add(new RulerTaskWrapper(cal.getTime()));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}

			this.chartwpp.configurarChart(this.chartControl.getChartStartDate(), this.chartControl.getChartEndDate(), 7, "FERRY/OUT_TRAIN", "INFRACCIONES");

			ArrayList<ChartDataSet> datalist = new ArrayList<>();

			if (tresultact.size() > 0) {
				BasicChartDataSet bardataset = new BasicChartDataSet(ActividadesTaskWrapper.class.getName(), tresultact);
				BarChartDataRender render = (BarChartDataRender) actfin.getChartDataRender();
				bardataset.setChartDataRender(render);
				datalist.add(bardataset);

				BasicChartDataSet rulerDataSet = new BasicChartDataSet(RulerTaskWrapper.class.getName(), tresultruler);
				ChartDataRender renderruler = ((RulerTaskWrapper) tresultruler.get(0)).getChartDataRender();
				rulerDataSet.setChartDataRender(renderruler);
				datalist.add(rulerDataSet);
			}
			this.chartwpp.getChart().setData(datalist);
			// chartwpp.chart.repaint();
			this.refreshChart();
		}
	}

	public void refreshChart() {
		try {
			if (this.chartwpp == null) {
				return;
			}
			if ((this.chartControl.getStartDate() != null) && (this.chartControl.getEndDate() != null) && !this.chartControl.getStartDate()
					.before(this.chartControl.getEndDate())) {
				if (this.chartwpp.getChart() != null) {
					this.chartwpp.getChart().removeAllData();
				}
			} else {
				Date fecIni = this.chartControl.getChartStartDate();
				Date fecFin = this.chartControl.getChartEndDate();
				this.chartControl.checkButtonStatus();
				this.chartControl.setTitle(fecIni, fecFin);

				if ((fecIni != null) && (fecFin != null) && fecIni.before(fecFin)) {
					String idConductor = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD);
					String matricula = "";
					if (this.managedForm.getDataFieldReference(OpentachFieldNames.MATRICULA_FIELD)!=null) {
						matricula = (String) this.managedForm.getDataFieldValue(OpentachFieldNames.MATRICULA_FIELD);
					}
					if ((fecIni == null) || (fecFin == null) || ((idConductor == null) && matricula== null)) {
						return;
					}

					if ((this.chartwpp != null) && this.chartwpp.isVisible()) {
						this.chartwpp.configurarChart(fecIni, fecFin, 7, "FERRY/OUT_TRAIN", "INFRACCIONES");
					}
				} else if (this.chartwpp.getChart() != null) {
					this.chartwpp.getChart().removeAllData();
				}
			}
			this.chartwpp.getChart().refresh();
		} catch (Exception ex) {
			ex.printStackTrace();
			this.managedForm.message(ex.getMessage(), Form.ERROR_MESSAGE);
		}
	}

	/**
	 * Descarga fichero de tarjeta: Del lector si existe tarjeta insertada. Del tacografo si éste está conectado al pc.
	 */
	protected void doOnSaveTCFile() {
		if (this.jfcSave == null) {
			TachoFileStore store = ((MonitorProvider) this.formManager.getReferenceLocator()).getFileStore();
			this.jfcSave = new JFileChooser(store.getTGDStore());
			this.jfcSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		this.jfcSave.setLocale(ApplicationManager.getLocale());
		this.jfcSave.updateUI();
		this.jfcSave.setDialogTitle(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
		this.jfcSave.setToolTipText(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
		this.jfcSave.setApproveButtonText(ApplicationManager.getTranslation("GUARDAR", ApplicationManager.getApplication().getResourceBundle()));
		this.jfcSave.setMultiSelectionEnabled(true);
		File dir = null;
		SmartCardMonitor dcm = ((UserInfoProvider) this.formManager.getReferenceLocator()).getLocalService(SmartCardMonitor.class);
		TachoReadMonitor tcm = ((MonitorProvider) this.formManager.getReferenceLocator()).getTachoReadMonitor();
		if (dcm.isDriverCardInserted()) {
			int returnVal = this.jfcSave.showSaveDialog(ApplicationManager.getApplication().getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dir = this.jfcSave.getSelectedFile();
			}

			if (dir != null) {
				((AbstractOpentachClientLocator) this.getReferenceLocator()).getLocalService(SmartCardMonitor.class).extractDriverCardFiles(dir);
			}
		}
		// Miro si la tarjeta esta en el tacografo.
		else if (tcm.isTachoConnected()) {
			int returnVal = this.jfcSave.showSaveDialog(ApplicationManager.getApplication().getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dir = this.jfcSave.getSelectedFile();
			}
			if (dir != null) {
				tcm.descargarFicheroTC(dir);
			}
		} else {
			this.managedForm.message("M_TARJETA_NO_DETECTADA", Form.MESSAGE);
		}
	}

	protected void doOnSaveVUFile() {
		TachoReadMonitor tcm = ((MonitorProvider) this.formManager.getReferenceLocator()).getTachoReadMonitor();
		if (tcm.isTachoConnected()) {
			if (this.jfcSave == null) {
				TachoFileStore store = ((MonitorProvider) this.formManager.getReferenceLocator()).getFileStore();
				this.jfcSave = new JFileChooser(store.getTGDStore());
				this.jfcSave.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			}
			this.jfcSave.setLocale(ApplicationManager.getLocale());
			this.jfcSave.updateUI();
			this.jfcSave.setDialogTitle(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
			this.jfcSave.setToolTipText(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
			this.jfcSave.setApproveButtonText(ApplicationManager.getTranslation("GUARDAR", ApplicationManager.getApplication().getResourceBundle()));
			this.jfcSave.setMultiSelectionEnabled(true);
			File dir = null;
			int returnVal = this.jfcSave.showSaveDialog(ApplicationManager.getApplication().getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dir = this.jfcSave.getSelectedFile();
			}

			if (dir != null) {
				tcm.descargarFicheroVU(dir);
			}
		} else {
			this.managedForm.message("M_TACOGRAFO_NO_DETECTADO", Form.MESSAGE);
		}
	}

	protected void setTGDFileInfo(Object src) throws Exception {
		if (src == null) {
			return;
		}
		if (src instanceof VehiculoInfrac) {
			VehiculoInfrac v = (VehiculoInfrac) src;
			this.managedForm.setDataFieldValue("MATRICULA", v.matricula);
			this.managedForm.deleteDataField(OpentachFieldNames.IDCONDUCTOR_FIELD);
			this.managedForm.deleteDataField(OpentachFieldNames.NAME_FIELD);
			this.managedForm.deleteDataField(OpentachFieldNames.SURNAME_FIELD);
			this.managedForm.deleteDataField("NUM_TRJ_CONDU");
		} else if (src instanceof Conductor) {
			Conductor c = (Conductor) src;
			this.managedForm.setDataFieldValue(OpentachFieldNames.IDCONDUCTOR_FIELD, c.IdConductor);
			this.managedForm.setDataFieldValue(OpentachFieldNames.NAME_FIELD, c.Nombre);
			this.managedForm.setDataFieldValue(OpentachFieldNames.SURNAME_FIELD, c.Apellidos);
			this.managedForm.setDataFieldValue("NUM_TRJ_CONDU", c.NumTrjCondu);
			this.managedForm.deleteDataField("MATRICULA");
		} else {
			throw new Exception("FICHERO_NO_VALIDO");
		}
	}

}
