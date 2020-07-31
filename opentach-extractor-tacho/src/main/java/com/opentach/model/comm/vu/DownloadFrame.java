package com.opentach.model.comm.vu;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.OperationThread;
import com.ontimize.gui.field.DateDataField;
import com.opentach.client.comp.ExtendedApplicationManager;

public class DownloadFrame extends JPanel {

	private final JCheckBox	jcResumen		= new JCheckBox("Resumen");
	private final JCheckBox	jcIncidentes	= new JCheckBox("Incidentes");
	private final JCheckBox	jcDatosV		= new JCheckBox("Velocidad");
	private final JCheckBox	jcDatosT		= new JCheckBox("Tecnicos");
	private final JCheckBox	jcLog			= new JCheckBox("PktLog");
	// private JLabel labelAct = new JLabel("Actividades");
	private final JLabel	lIni			= new JLabel("Inicio");
	private final JLabel	lFin			= new JLabel("Fin");
	private final JLabel	jldes			= new JLabel("Descarga");
	private TachoRead		tr				= null;
	private JComboBox		com				= null;
	private final JComboBox	op				= new JComboBox(new String[] { "- ", "VU", "TC" });
	private DateDataField	cfi				= null;
	private DateDataField	cff				= null;
	private final JButton	go				= new JButton("Descargar");
	private final JButton	sf				= new JButton("Fichero");
	public JTextField		text			= new JTextField();
	private final JTextArea	log				= new JTextArea(15, 40);
	// private Console c = null;
	private DownloadThread	dt				= null;
	// private JPanel p3 = new JPanel();
	private JScrollPane		js				= null;

	private void noneSelected(boolean t) {
		this.jcResumen.setSelected(false);
		this.jcIncidentes.setEnabled(false);
		this.jcIncidentes.setSelected(false);
		this.jcDatosV.setEnabled(false);
		this.jcDatosV.setSelected(false);
		this.jcDatosT.setEnabled(false);
		this.jcDatosT.setSelected(false);
		this.cfi.setEnabled(false);
		this.cff.setEnabled(false);
		this.lIni.setEnabled(false);
		this.lFin.setEnabled(false);
		this.go.setEnabled(false);
		this.sf.setEnabled(false);
		if (t) {
			this.op.setEnabled(false);
			this.jldes.setEnabled(false);

		}
	}

	private JPanel creaPanelPpal() {
		JPanel p = new JPanel();
		JPanel p1 = new JPanel();

		p1.setLayout(new FlowLayout());
		p1.add(new JLabel("Puerto"));
		this.com = new JComboBox(PuertoSerie.porSerialList());
		this.com.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (DownloadFrame.this.tr != null) {
						DownloadFrame.this.tr.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				if (!((String) DownloadFrame.this.com.getSelectedItem()).equals("- ")) {
					DownloadFrame.this.tr = new TachoRead((String) DownloadFrame.this.com.getSelectedItem(), DownloadFrame.this.log);
					DownloadFrame.this.op.setEnabled(true);
					DownloadFrame.this.op.setSelectedIndex(0);
					DownloadFrame.this.jldes.setEnabled(true);
				} else {
					DownloadFrame.this.noneSelected(true);
				}
			}
		});

		if ((this.com == null) || (this.com.getItemCount() == 0)) {
			MessageDialog.showErrorMessage(null, "NO_SERIAL_PORTS_DETECTED");
		}

		p1.add(this.com);
		p1.add(new JLabel(" "));
		p1.add(this.jldes);
		p1.add(this.op);

		this.jcLog.setVisible(false);

		JPanel p2 = new JPanel();
		p2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		p2.setPreferredSize(new Dimension(40, 45));

		p.setLayout(new GridBagLayout());

		p.add(p1, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

		p.add(p2, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0));

		return p;
	}

	private JPanel creaPanelActividades() {
		JPanel p = new JPanel();
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("labelsize", "5");
		h.put("editable", "no");
		h.put("labelvisible", "no");
		h.put("size", "8");
		h.put("align", "left");

		this.cfi = new DateDataField(h);
		this.cff = new DateDataField(h);

		p.setLayout(new FlowLayout(FlowLayout.LEFT));
		p.add(this.lIni);
		p.add(this.cfi);
		p.add(new JLabel(" "));
		p.add(this.lFin);
		p.add(this.cff);
		return p;
	}

	private JPanel createVUPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		this.jcResumen.setEnabled(false);

		p.add(this.jcResumen,
				new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 4), 0, 0));

		p.add(this.jcDatosT, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));

		p.add(this.jcIncidentes, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4),
				0, 0));

		p.add(this.jcDatosV, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 4, 2, 4), 0, 0));

		p.add(this.creaPanelActividades(), new GridBagConstraints(0, 1, 4, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2,
				4, 2, 4), 0, 0));

		p.setBorder(new TitledBorder("VU"));
		return p;
	}

	private JPanel outputPannel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());
		p.add(this.sf, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		p.add(this.text, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));
		return p;
	}

	private JPanel logPanel() {
		JPanel p = new JPanel();
		p.setLayout(new GridBagLayout());

		this.js = new JScrollPane(this.log);

		p.add(this.js, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2, 2), 0, 0));

		p.setBorder(new TitledBorder("Salida"));

		this.log.setWrapStyleWord(true);
		this.log.setLineWrap(true);
		// System.setOut(ApplicationManager.getPrintStreamOnTextComponent(log,100000));
		return p;
	}

	private void selectedVU() {
		this.jcResumen.setSelected(true);
		this.jcIncidentes.setEnabled(true);
		this.jcIncidentes.setSelected(true);
		this.jcDatosV.setEnabled(true);
		this.jcDatosV.setSelected(true);
		this.jcDatosT.setEnabled(true);
		this.jcDatosT.setSelected(true);
		this.cfi.setEnabled(true);
		this.cfi.setValue(null);
		this.cff.setEnabled(true);
		this.cff.setValue(null);
		this.lIni.setEnabled(true);
		this.lFin.setEnabled(true);
		this.go.setEnabled(true);
		this.sf.setEnabled(true);
	}

	private void selectedTC() {
		this.jcResumen.setSelected(false);
		this.jcIncidentes.setEnabled(false);
		this.jcIncidentes.setSelected(false);
		this.jcDatosV.setEnabled(false);
		this.jcDatosV.setSelected(false);
		this.jcDatosT.setEnabled(false);
		this.jcDatosT.setSelected(false);
		this.cfi.setValue(null);
		this.cfi.setEnabled(false);
		this.cff.setValue(null);
		this.cff.setEnabled(false);
		this.lIni.setEnabled(false);
		this.lFin.setEnabled(false);
		this.go.setEnabled(true);
		this.sf.setEnabled(true);
	}

	private Calendar onlyDays(Date d) {
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c;
	}

	private static byte[] intToBytes(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i >> 24);
		b[1] = (byte) (i >> 16);
		b[2] = (byte) (i >> 8);
		b[3] = (byte) i;

		return b;
	}

	private Vector<Object> createDatesBetween(Date ini, Date fin) {
		Vector<Object> v = new Vector<Object>();

		Calendar c1 = this.onlyDays(ini);
		c1.add(Calendar.DAY_OF_YEAR, 1);
		Calendar c2 = this.onlyDays(fin);
		c2.add(Calendar.DAY_OF_YEAR, 1);
		do {
			v.add(DownloadFrame.intToBytes((int) (c1.getTime().getTime() / 1000)));
			c1.add(Calendar.DAY_OF_YEAR, 1);
		} while (c1.getTime().getTime() <= c2.getTime().getTime());
		return v;
	}

	private void pulsoGO() {

		if (this.com.getSelectedIndex() == -1) {
			MessageDialog.showErrorMessage(null, "SIN PUERTOS SERIE");
			return;
		}

		if (this.tr == null) {
			MessageDialog.showErrorMessage(null, "PRIMERO ELIJE PUERTO SERIE");
			return;
		}

		if ((this.text.getText() == null) || (this.text.getText().length() == 0)) {
			MessageDialog.showErrorMessage(null, "NO HAY FICHERO SELECCIONADO");
			return;
		}

		File f = new File(this.text.getText());
		if (f.exists()) {
			int i = JOptionPane.showConfirmDialog(null, "EL FICHERO DE SALIDA EXISTE, ¿SOBREESCRIBIR?", "Question", JOptionPane.YES_NO_OPTION);
			if (i != JOptionPane.OK_OPTION) {
				return;
			}
		}

		Vector<Object> vdates = null;

		if ((this.cfi.getDateValue() != null) && (this.cff.getDateValue() == null)) {
			this.cff.setValue(this.cfi.getDateValue());
		}
		if ((this.cff.getDateValue() != null) && (this.cfi.getDateValue() == null)) {
			this.cfi.setValue(this.cff.getDateValue());
		}

		if (this.cfi.getDateValue() != null) {

			if (this.cff.getDateValue() == null) {
				MessageDialog.showErrorMessage(null, "NO EXISTE FECHA FIN");
				return;
			}
			if (((java.util.Date) this.cfi.getDateValue()).after((java.util.Date) this.cff.getDateValue())) {
				MessageDialog.showErrorMessage(null, "FECHA FIN PREVIA A FECHA INICIO");
				return;
			}

			if (new Date().before((Date) this.cff.getDateValue())) {
				MessageDialog.showErrorMessage(null, "FECHA FIN POSTERIOR A LA ACTUAL");
				return;
			}

			vdates = this.createDatesBetween((Date) this.cfi.getDateValue(), (Date) this.cff.getDateValue());
		}

		Vector<Object> v = null;
		// VU
		if (this.op.getSelectedIndex() == 1) {
			v = new Vector<Object>();
			if (this.jcResumen.isSelected()) {
				v.add(new byte[] { TachoRead.TREP_RESUMEN });
			}
			if (this.jcIncidentes.isSelected()) {
				v.add(new byte[] { TachoRead.TREP_INCIDENTES });
			}
			if (this.jcDatosV.isSelected()) {
				v.add(new byte[] { TachoRead.TREP_VELOCIDAD });
			}
			if (this.jcDatosT.isSelected()) {
				v.add(new byte[] { TachoRead.TREP_DATOST });
			}
			if (vdates != null) {
				v.add(new byte[] { TachoRead.TREP_ACTIVIDADES });
			}
		}

		// TC
		if (this.op.getSelectedIndex() == 2) {
			v = new Vector<Object>();
			v.add(new byte[] { TachoRead.TREP_TRANSFERENCIA });
		}

		if ((this.op.getSelectedIndex() == 2) || (this.op.getSelectedIndex() == 1)) {
			this.dt = new DownloadThread();
			this.dt.initValues(f, v, vdates, this.tr);
			ExtendedApplicationManager.proccessOperation((Frame) SwingUtilities.getWindowAncestor(this), (OperationThread) this.dt, 100);
		}
	}

	public DownloadFrame() {
		this.text.setEnabled(false);
		this.log.setEnabled(false);
		this.op.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (DownloadFrame.this.op.getSelectedIndex()) {
				case 0:
					DownloadFrame.this.noneSelected(false);
					break;
				case 1:
					DownloadFrame.this.selectedVU();
					break;
				case 2:
					DownloadFrame.this.selectedTC();
					break;
				}
			}
		});

		this.sf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				JFileChooser jfc = new JFileChooser();
				jfc.setSize(400, 300);
				jfc.setMultiSelectionEnabled(false);
				jfc.setLocale(ApplicationManager.getLocale());
				jfc.updateUI();
				int choice = jfc.showDialog(null, "New");
				if (choice == JFileChooser.APPROVE_OPTION) {
					DownloadFrame.this.text.setText(jfc.getSelectedFile().getAbsolutePath());
				} else {
					DownloadFrame.this.text.setText("");
				}
			}
		});

		this.go.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DownloadFrame.this.pulsoGO();
			}
		});

		this.setLayout(new GridBagLayout());

		this.add(this.creaPanelPpal(), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2,
				2), 0, 0));

		this.add(this.createVUPanel(), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(2, 2, 2,
				2), 0, 0));

		this.add(this.outputPannel(), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(2, 2, 2,
				2), 0, 0));

		this.add(this.go, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(2, 2, 2, 2), 0, 0));

		this.add(this.logPanel(), new GridBagConstraints(0, 4, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(2, 2, 2, 2), 0, 0));
		this.noneSelected(true);
	}

}
