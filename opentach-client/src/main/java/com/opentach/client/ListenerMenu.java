package com.opentach.client;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.ApplicationMenuBar;
import com.ontimize.gui.CheckMenuItem;
import com.ontimize.gui.DefaultActionMenuListener;
import com.ontimize.gui.Form;
import com.ontimize.gui.FormManager;
import com.ontimize.gui.InteractionManager;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.WWWDataField;
import com.ontimize.gui.manager.IFormManager;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;
import com.opentach.client.comp.ExtendedApplicationManager;
import com.opentach.client.comp.JMenuAppMode;
import com.opentach.client.comp.JMenuAppMode.APP_MODE;
import com.opentach.client.comp.OpentachNavigatorMenuGUI;
import com.opentach.client.modules.IMReportRoot;
import com.opentach.client.util.password.PasswordChanger;
import com.opentach.client.util.usbkey.USBKeyMonitor;
import com.opentach.common.OpentachFieldNames;
import com.opentach.common.tacho.TachoFileStore;
import com.opentach.common.user.IUserData;
import com.opentach.common.util.DateUtil;
import com.opentach.model.comm.vu.DownloadLauncher;
import com.opentach.model.scard.SmartCardMonitor;
import com.utilmize.client.UClientApplication;

public class ListenerMenu extends DefaultActionMenuListener implements OpentachFieldNames {

	private static final Logger						logger			= LoggerFactory.getLogger(ListenerMenu.class);
	public static final boolean						DEBUG			= false;

	private static final String						SUPPORTURL		= "http://www.opentach.com/soporte";
	private static final String						LEXEDUCAURL		= "http://www.lexeduca.es/lexeduca/cursodetalle/all";
	private static final String						CAPURL			= "http://www.lextransport.com/formacion/cap/";

	private final Hashtable<String, MenuRegister>	dialogs			= new Hashtable<String, MenuRegister>();
	private final List<String>						directReport	= Arrays.asList("managerinformeinfraccondalarmas",
			"managerinformeeventostaco", "managerinformeeventostaco");

	private class MenuRegister {
		protected String	manager;
		protected String	form;
		protected String	title;
		protected Form		f;
		protected JDialog	dlg;

		protected boolean	resize		= false;
		protected boolean	cleanfields	= false;

		public MenuRegister(String manager, String form, String title) {
			this(manager, form, title, false, false);
		}

		public MenuRegister(String manager, String form, String title, boolean resize) {
			this(manager, form, title, resize, false);
		}

		public MenuRegister(String manager, String form, String title, boolean resize, boolean cleanfields) {
			this.manager = manager;
			this.form = form;
			this.title = title;
			this.resize = resize;
			this.cleanfields = cleanfields;
		}
	}

	public ListenerMenu() {
		super();
		this.dialogs.put("managersolicitudes", new MenuRegister("managersolicitudes", "formSolicitudCondVeh.xml", "managersolicitudes", true));
		this.dialogs.put("manageracercade", new MenuRegister("manageracercade", "formAbout.xml", "manageracercade"));
		this.dialogs.put("managercargaficheros", new MenuRegister("managercargaficheros", "formCargaFicheros.xml", "M_ENVIO_FICHEROS_CSD", true));
		this.dialogs.put("managercfgservicios", new MenuRegister("managercfgservicios", "formCfgServicios.xml", "CONFIGURACION_SERVICIOS", true));
		this.dialogs.put("macroinf_activ", new MenuRegister("macroinf_activ", "formInformeActivResumen.xml", "INFORME_ACTIVIDADES_RESUMEN", true));
		this.dialogs.put("macroinf_selec", new MenuRegister("macroinf_selec", "formInformeSelec.xml", "FORM_INFORME_SELEC", true));
		// this.dialogs.put("managerinformedescargas",
		// new MenuRegister("managerinformedescargas", "formInformeDescargas.xml", "FORM_INFORME_DESCARGAS", true));
		this.dialogs.put("macroinf_gestor", new MenuRegister("macroinf_gestor", "formInformeGestor.xml", "FORM_INFORME_GESTOR", true));
		this.dialogs.put("express_cond", new MenuRegister("express_cond", "formInformeExpressCond.xml", "FORM_INFORME_EXPRESS", true));
		this.dialogs.put("express_veh", new MenuRegister("express_veh", "formInformeExpressVeh.xml", "FORM_INFORME_EXPRESS", true));
		this.dialogs.put("macroinf_incidencias",
				new MenuRegister("macroinf_incidencias", "formInformeIncidResumen.xml", "INFORME_INCIDENCIAS_RESUMEN", true));
		this.dialogs.put("managercreatenotice", new MenuRegister("managercreatenotice", "formcreatenotice.xml", "M_CREATE_NOTICE", true, false));
		this.dialogs.put("managerinformealertas",
				new MenuRegister("managerinformealertas", "formInformeAlertas.xml", "managerinformealertas", true, false));
	}

	@Override
	public void setInitialState() {
		super.setInitialState();
	}

	protected void doOnSaveVUFile() {
		MonitorProvider mp = (MonitorProvider) this.application.getReferenceLocator();
		if (mp.getTachoReadMonitor().isTachoConnected()) {
			TachoFileStore store = mp.getFileStore();
			JFileChooser chooser = new JFileChooser(store.getTGDStore());
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setMultiSelectionEnabled(true);
			chooser.setDialogTitle(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
			chooser.setToolTipText(ApplicationManager.getTranslation("M_SELECCIONE_DIRECTORIO"));
			chooser.setApproveButtonText(ApplicationManager.getTranslation("GUARDAR", ApplicationManager.getApplication().getResourceBundle()));
			chooser.setLocale(ApplicationManager.getLocale());
			chooser.updateUI();
			File dir = null;
			int returnVal = chooser.showSaveDialog(ApplicationManager.getApplication().getFrame());
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				dir = chooser.getSelectedFile();
			}
			if (dir != null) {
				mp.getTachoReadMonitor().descargarFicheroVU(dir);
			}
		} else {
			JOptionPane.showMessageDialog(ApplicationManager.getApplication().getFrame(),
					ApplicationManager.getTranslation("M_TACOGRAFO_NO_DETECTADO"), ApplicationManager.getTranslation("M_TACOGRAFO_NO_DETECTADO_2"),
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		final String com = e.getActionCommand();
		IFormManager gf = this.application.getFormManager(com);
		OpentachClientLocator brefs = null;

		if (com.equals("managerSoporte") || com.equals("managerSoporteBasic")) {
			WWWDataField.processURL(ListenerMenu.SUPPORTURL);
			return;
		} else if (com.equals("managerLexEduca")) {
			WWWDataField.processURL(ListenerMenu.LEXEDUCAURL);
			return;
		} else if (com.equals("managerCAP")) {
			WWWDataField.processURL(ListenerMenu.CAPURL);
			return;
		} else if (com.equals("ArrangeMenuGroups")) {
			final IFormManager gfMenu = this.application.getFormManager("manageracercade2");
			final Form f = gfMenu.getFormReference("formAcercaDe.xml");
			if (f != null) {
				OpentachNavigatorMenuGUI menu = (OpentachNavigatorMenuGUI) f.getElementReference("menu");
				if (menu != null) {
					// menu.arrangeMenus();
				}
			}
			return;
		} else if (com.equals("managerdescargaTGDA")) {
			// cambios david: se fuerza la lectura de la tarjeta, no se muestra
			// formulario
			((AbstractOpentachClientLocator) ApplicationManager.getApplication().getReferenceLocator()).getLocalService(SmartCardMonitor.class).extractDriverCardFiles(null);
			return;
		} else if (com.equals("managerdescargaUSBKey")) {
			new Thread("usb download") {
				@Override
				public void run() {
					MonitorProvider mp = (MonitorProvider) ListenerMenu.this.application.getReferenceLocator();
					USBKeyMonitor usbmon = mp.getUSBKeyMonitor();
					usbmon.downloadUSBKeyFiles(null);
				}
			}.start();
			return;
		} else if (com.equals("managerdescargaTGD2")) {
			this.doOnSaveVUFile();
			return;
		}
		MenuRegister mr = this.dialogs.get(com);
		if (mr != null) {
			JDialog dialog = mr.dlg;
			try {
				if (dialog == null) {
					Form f = this.application.getFormManager(mr.manager).getFormCopy(mr.form);
					dialog = this.getFormDialog(f);
					// f.getGestorInteraccion().estableceEstadoInicial();
					dialog.validate();
					dialog.setResizable(mr.resize);
					mr.dlg = dialog;
					mr.f = f;
				}
			} catch (Exception ex) {
				ListenerMenu.logger.error(null, ex);
			}
			if (mr.cleanfields) {
				mr.f.deleteDataFields();
			}
			// Desactivo la descarga automatica cuando se muestra el dialog de
			// descarga de Tarjeta Conductor. TC.
			brefs = (OpentachClientLocator) mr.f.getFormManager().getReferenceLocator();
			if (brefs != null) {
				this.updateCgContrato(brefs, mr.f);
			}
			mr.f.setLocale(ApplicationManager.getLocale());
			mr.f.setResourceBundle(ApplicationManager.getApplicationBundle());
			dialog.setTitle(ApplicationManager.getTranslation(mr.title, this.application.getFormManager(mr.manager).getResourceBundle()));
			dialog.setVisible(true);
		} else if (com.equals("managerdescargaVU")) {
			DownloadLauncher vudownload = new DownloadLauncher();
			vudownload.run();
		} else if ("menudemovideo".equals(com)) {
			try {
				Desktop desktop = java.awt.Desktop.getDesktop();
				URI uri = new java.net.URI("http://212.89.13.60:8080/openservices/index_videos.html");
				desktop.browse(uri);
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(ApplicationManager.getApplication().getFrame(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		} else if (com.equalsIgnoreCase("managerpassword")) {
			try {
				PasswordChanger.doChangePassword(true, ApplicationManager.getApplication().getFrame());
			} catch (Exception e1) {
				MessageManager.getMessageManager().showExceptionMessage(e1, ListenerMenu.logger);
			}
		} else if (this.directReport.contains(com)) {
			boolean rtn = this.generateReport(com);
			if (!rtn) {
				if ((gf != null) && !com.equals("managercontratoemp")) {
					brefs = (OpentachClientLocator) gf.getReferenceLocator();
					InteractionManager gi = gf.getActiveForm().getInteractionManager();
					Form form = gi.managedForm;
					this.updateCgContrato(brefs, form);
				}
				super.actionPerformed(e);
			}
		} else if (com.equals("ArrancarMinimizado")) {
			Object oitm = e.getSource();
			if ((oitm != null) && (oitm instanceof CheckMenuItem)) {
				CheckMenuItem itm = (CheckMenuItem) oitm;
				JMenuAppMode.setMinimizedStartPreference(itm.isSelected());
			}
		} else if (com.startsWith("AppMode.")) {
			Object oitm = e.getSource();
			if ((oitm != null) && (oitm instanceof CheckMenuItem)) {
				CheckMenuItem itm = (CheckMenuItem) oitm;

				APP_MODE mode = APP_MODE.NAVIGATOR;
				if ("AppMode.LaunchPad".equals(itm.getAttribute())) {
					mode = APP_MODE.LAUNCHPAD;
				} else if ("AppMode.Navigator".equals(itm.getAttribute())) {
					mode = APP_MODE.NAVIGATOR;
				} else {
					mode = APP_MODE.COMBINED;
				}
				JMenuAppMode.setAppModePreference(mode);
			}
		} else if (com.startsWith("laf_")) {
			String laf = com.substring("laf_".length());
			this.changeLAF(laf);
		} else {
			if ((gf != null) && !com.equals("managercontratoemp")) {
				brefs = (OpentachClientLocator) gf.getReferenceLocator();
				InteractionManager gi = gf.getActiveForm().getInteractionManager();
				if (gi != null) {
					Form form = gi.managedForm;
					this.updateCgContrato(brefs, form);
				}
			}
			super.actionPerformed(e);
		}
	}

	private void changeLAF(String laf) {
		// Por ahora vamos a invocar al jnlp que toque y cerramos la aplicación.
		if (UClientApplication.getCurrentActiveForm().question("M_CHANGE_LAF")) {
			String jnlp = "start.jnlp";
			if ("white".equals(laf)) {
				jnlp = "start_white.jnlp";
			}
			String uri = System.getProperty("BASE_JNLP_URI");
			uri += (uri.endsWith("/") ? "" : "/") + jnlp;
			try {
				Desktop.getDesktop().browse(new URI(uri));
				ApplicationManager.getApplication().exit();
			} catch (Exception e) {
				e.printStackTrace();
				UClientApplication.getCurrentActiveForm().message("M_E_OPEN_LAF_URL", Form.ERROR_MESSAGE);
			}
		}
	}

	protected void updateCgContrato(OpentachClientLocator brefs, Form f) {
		try {
			DataField field = (DataField) f.getDataFieldReference(OpentachFieldNames.CG_CONTRATO_FIELD);
			if (field != null) {
				if (field.isModifiable()) {
					f.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
							brefs.getUserData().getActiveContract((String) f.getDataFieldValue(OpentachFieldNames.CIF_FIELD)));
				} else {
					field.setModifiable(true);
					f.setDataFieldValue(OpentachFieldNames.CG_CONTRATO_FIELD,
							brefs.getUserData().getActiveContract((String) f.getDataFieldValue(OpentachFieldNames.CIF_FIELD)));
					field.setModifiable(false);
				}
			}
		} catch (Exception e) {
			ListenerMenu.logger.error(null, e);
		}
	}

	@Override
	public void addMenuToListenFor(JMenuBar barraMenu) {
		ApplicationMenuBar barra = (ApplicationMenuBar) barraMenu;
		super.addMenuToListenFor(barra);
	}

	public JDialog getFormDialog(Form f) {
		JDialog dlg = null;
		try {
			Window wancestor = ApplicationManager.getApplication().getFrame();
			dlg = f.putInModalDialog(wancestor);
			// dlg.validate();
			dlg.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
			ApplicationManager.center(dlg);
		} catch (Exception e) {
			ListenerMenu.logger.error(null, e);
		}
		return dlg;
	}

	/**
	 * Generación directa de los informes de alarma. Al pulsar sobre el menu o dialogo se generará el informe de forma automatica si el usuario es un
	 * usuario de un cliente.
	 *
	 * @param reportname
	 */
	protected boolean generateReport(String gfname) {
		try {
			IFormManager gf = this.application.getFormManager(gfname);
			IUserData user = ((OpentachClientLocator) gf.getReferenceLocator()).getUserData();
			if (user.getAllCompaniesList().size()>1){ // si el usuario tiene asociado más de una empresa hay que mostrar el formulario para que seleccione la empresa
				return false;
			}
			final String cif = user.getCIF();
			final String name = gfname;
			if (user.getCIF() != null) {
				OperationThread opth = new OperationThread(ApplicationManager.getTranslation("Informe_alarmas_")) {
					@Override
					public void run() {
						try {
							this.hasStarted = true;
							IFormManager gf = ListenerMenu.this.application.getFormManager(name);
							Form form = gf.getFormReference(((FormManager) gf).getCurrentForm());
							IMReportRoot gif = (IMReportRoot) form.getInteractionManager();
							gif.setInitialState();
							form.setDataFieldValue(OpentachFieldNames.CIF_FIELD, cif);
							form.setDataFieldValue("SHOW_DETALLES", Integer.valueOf(1));
							try {
								DateDataField cf = (DateDataField) form.getDataFieldReference("FILTERFECINI");
								Date d = new Date();
								cf.setValue(DateUtil.addDays(d, -28));
								cf = (DateDataField) form.getDataFieldReference("FILTERFECFIN");
								cf.setValue(d);
							} catch (Exception cex) {
							}
							gif.doOnQuery();
							this.res = gif;
						} catch (Exception e) {
							ListenerMenu.logger.error(null, e);
						} finally {
							this.hasFinished = true;
						}
					}
				};
				ExtendedApplicationManager.proccessOperation((Frame) SwingUtilities.getWindowAncestor(gf.getActiveForm().getParent()), opth, 50);
				Object result = opth.getResult();
				if ((result != null) && (result instanceof IMReportRoot)) {
					gf.getActiveForm().getButton("btnInforme2").doClick();
				}
				return true;
			}
		} catch (Exception e) {
			ListenerMenu.logger.error(null, e);
		}
		return false;
	}
}
