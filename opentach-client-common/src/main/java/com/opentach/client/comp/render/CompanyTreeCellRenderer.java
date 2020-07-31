package com.opentach.client.comp.render;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.sql.Timestamp;
import java.util.Date;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.tree.BasicTreeCellRenderer;
import com.ontimize.gui.tree.OTreeNode;
import com.opentach.client.util.UserInfoProvider;
import com.opentach.common.company.naming.CompanyNaming;

public class CompanyTreeCellRenderer extends BasicTreeCellRenderer {

	private final static ImageIcon	driverIcon			= ApplicationManager.getIcon("com/opentach/client/rsc/dude16.png");
	private final static ImageIcon	busIcon				= ApplicationManager.getIcon("com/opentach/client/rsc/minibus_green16.png");
	private final static ImageIcon	busRemoteIcon		= ApplicationManager.getIcon("com/opentach/client/rsc/minibus_blue16.png");
	private final static ImageIcon	carIcon				= ApplicationManager.getIcon("com/opentach/client/rsc/truck_blue16.png");
	private final static ImageIcon	carRemoteIcon		= ApplicationManager.getIcon("com/opentach/client/rsc/truck_blue16BBB.png");
	private final static ImageIcon	iContratoError		= ApplicationManager.getIcon("com/opentach/client/rsc/scroll_error.png");
	private final static ImageIcon	iContratoOK			= ApplicationManager.getIcon("com/opentach/client/rsc/scroll.png");
	private final static ImageIcon	iContratoWarning	= ApplicationManager.getIcon("com/opentach/client/rsc/scroll_information.png");
	private final static ImageIcon	iContratoRun		= ApplicationManager.getIcon("com/opentach/client/rsc/scroll_run16.png");
	private final static ImageIcon	iEmpresa			= ApplicationManager.getIcon("com/opentach/client/rsc/factoryB16.png");

	private final static ImageIcon	driverDropped;
	private final static ImageIcon	carDropped;
	private final static ImageIcon	busDropped;
	//	private final static ImageIcon	carRemoteDropped;
	//	private final static ImageIcon	busRemoteDropped;

	static {
		ImageFilter filter = new GrayFilter(true, 60);
		Toolkit tk = Toolkit.getDefaultToolkit();
		ImageProducer producer = new FilteredImageSource(CompanyTreeCellRenderer.driverIcon.getImage().getSource(), filter);
		driverDropped = new ImageIcon(tk.createImage(producer));
		//		producer = new FilteredImageSource(CompanyTreeCellRenderer.carRemoteIcon.getImage().getSource(), filter);
		//		carRemoteDropped = new ImageIcon(tk.createImage(producer));
		//		producer = new FilteredImageSource(CompanyTreeCellRenderer.busRemoteIcon.getImage().getSource(), filter);
		//		busRemoteDropped = new ImageIcon(tk.createImage(producer));
		producer = new FilteredImageSource(CompanyTreeCellRenderer.carIcon.getImage().getSource(), filter);
		carDropped = new ImageIcon(tk.createImage(producer));
		producer = new FilteredImageSource(CompanyTreeCellRenderer.busIcon.getImage().getSource(), filter);
		busDropped = new ImageIcon(tk.createImage(producer));

	}

	public CompanyTreeCellRenderer() {
		super();
	}

	@Override
	public Component getTreeCellRendererComponent(JTree arbol, Object valor, boolean select, boolean expanded, boolean leaf, int fila,
			boolean tieneFoco) {
		Component comp = super.getTreeCellRendererComponent(arbol, valor, select, expanded, leaf, fila, tieneFoco);
		if (comp instanceof JLabel) {
			JLabel jlRender = ((JLabel) comp);
			TreePath path = arbol.getPathForRow(fila);
			if ((path != null) && (path.getLastPathComponent() != null) && (path.getLastPathComponent() instanceof OTreeNode)) {
				OTreeNode nodo = (OTreeNode) path.getLastPathComponent();
				if (!nodo.isOrganizational() && nodo.getEntityName().equals("EConductoresEmp")) {
					// Nodo de conductores
					Object fbaja = nodo.getValueForAttribute("F_BAJA");
					jlRender.setIcon((fbaja != null) && !fbaja.equals("") ? CompanyTreeCellRenderer.driverDropped : CompanyTreeCellRenderer.driverIcon);
					String durm = (String) nodo.getValueForAttribute("DURMIENTE");
					jlRender.setIcon("S".equals(durm) ? CompanyTreeCellRenderer.driverDropped : this.getIcon());

				} else if (!nodo.isOrganizational() && nodo.getEntityName().equals(CompanyNaming.ENTITY)) {
					// Nodo de empresas
					jlRender.setIcon(CompanyTreeCellRenderer.iEmpresa);

				} else if (!nodo.isOrganizational() && nodo.getEntityName().equals("EVehiculosEmp")) {
					// Nodo de vehículos
					Object fbaja = nodo.getValueForAttribute("F_BAJA");
					Object descargaRemota = nodo.getValueForAttribute("DESCARGA_REMOTA");
					String idVehicleType = (String) nodo.getValueForAttribute("IDVEHICLETYPE");
					String durm = (String) nodo.getValueForAttribute("DURMIENTE");

					if ("P".equals(idVehicleType)) {
						jlRender.setIcon(((descargaRemota == null) || "".equals(descargaRemota)) ? CompanyTreeCellRenderer.busIcon : CompanyTreeCellRenderer.busRemoteIcon);
						jlRender.setIcon("S".equals(durm) ? CompanyTreeCellRenderer.busDropped : this.getIcon());
						jlRender.setIcon((fbaja != null) && !fbaja.equals("") ? CompanyTreeCellRenderer.busDropped : this.getIcon());
					} else {
						jlRender.setIcon(((descargaRemota == null) || "".equals(descargaRemota)) ? CompanyTreeCellRenderer.carIcon : CompanyTreeCellRenderer.carRemoteIcon);
						jlRender.setIcon("S".equals(durm) ? CompanyTreeCellRenderer.carDropped : this.getIcon());
						jlRender.setIcon((fbaja != null) && !fbaja.equals("") ? CompanyTreeCellRenderer.carDropped : this.getIcon());
					}

				} else if (!nodo.isOrganizational() && nodo.getEntityName().equals("EContratoEmp")) {
					// Contratos

					Object fBaja = nodo.getValueForAttribute("F_BAJA");
					Object fFin = nodo.getValueForAttribute("FECFIN");
					String cgContrato = (String) nodo.getValueForAttribute("CG_CONTRATO");
					String CIF = (String) nodo.getValueForAttribute("CIF");
					boolean run = false;
					if ((cgContrato != null) && (CIF != null)) {
						try {
							String contratoActivo = ((UserInfoProvider) ApplicationManager.getApplication().getReferenceLocator()).getUserData()
									.getActiveContract(CIF);
							if ((contratoActivo != null) && contratoActivo.equals(cgContrato)) {
								jlRender.setIcon(CompanyTreeCellRenderer.iContratoRun);
								run = true;
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					if (run) {

					} else if ((fBaja != null) && !((fBaja != null) && (fBaja instanceof String) && (((String) fBaja).length() == 0))) {
						jlRender.setIcon(CompanyTreeCellRenderer.iContratoError);
					} else if (((fFin instanceof Date) || (fFin instanceof Timestamp)) && ((Date) fFin).before(new Date())) {
						jlRender.setIcon(CompanyTreeCellRenderer.iContratoWarning);
					} else {
						jlRender.setIcon(CompanyTreeCellRenderer.iContratoOK);
					}
				}
			}
		}
		return comp;
	}
}
