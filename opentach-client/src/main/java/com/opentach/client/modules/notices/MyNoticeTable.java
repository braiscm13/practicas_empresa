package com.opentach.client.modules.notices;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.gui.table.TableComponent;
import com.ontimize.util.alerts.IAlertSystem;
import com.ontimize.util.notice.INoticeSystem;
import com.ontimize.util.notice.NoticeTable;

public class MyNoticeTable extends NoticeTable {

	protected static final String ALERT_SYSTEM_NOTICES = "alertsystemnotices";

	protected static final String	ALERT_SYSTEM_NOTICES_INCLUDED	= "included";
	protected static final String	ALERT_SYSTEM_NOTICES_EXCLUDED	= "excluded";
	protected static final String	ALERT_SYSTEM_NOTICES_ONLY		= "only";
	public static String			TABLE_BUTTON_NOTICE_ICON		= "images/table/notice24.png";
	public static String			TABLE_BUTTON_READED_ICON		= "images/table/readed.png";
	public static String			TABLE_BUTTON_UNREADED_ICON		= "images/table/unreaded24.png";

	protected String alertSystemNoticesValue = null;

	public MyNoticeTable(Hashtable params) throws Exception {
		super(params);

		this.alertSystemNoticesValue = (String) params.get(MyNoticeTable.ALERT_SYSTEM_NOTICES);
		if (this.alertSystemNoticesValue == null) {
			this.alertSystemNoticesValue = MyNoticeTable.ALERT_SYSTEM_NOTICES_INCLUDED;
		}
		String[] attrs = new String[] { "copybutton", "grouptablekey", "filtersavebutton", "visiblecolsbutton", "excelexportbutton", "reportbutton", "pivottablebutton", "changeviewbutton", "sumrowbutton", "calculedcolsbutton", "htmlexportbutton", "insertbutton", "chartbutton", "defaultchartbutton", "printingbutton" };
		for (String attr : attrs) {
			TableComponent tableComponentReference = this.getTableComponentReference(attr);
			if (tableComponentReference != null) {
				this.controlsPanel.remove((Component) tableComponentReference);
			}
		}
	}

	//
	// @Override
	// protected ActionListener getSelectedVisiblesNoticesListener(boolean
	// unsend, boolean unread, boolean deleted) {
	// return new MySelectedNoticesListener(unsend, unread, deleted);
	// }

	/**
	 * Esta clase establece el valor de la tabla cuando se pulsa en alguno de los botones añadidos <br> Muestra todos los mensajes o sólo los no
	 * leídos en función de los parámetros establecidos en el constructor
	 */
	class MySelectedNoticesListener implements ActionListener {

		boolean unsend;

		boolean unread;

		boolean deleted;

		MySelectedNoticesListener(boolean unsend, boolean unread, boolean delted) {
			this.unread = unread;
			this.unsend = unsend;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (MyNoticeTable.this.locator instanceof INoticeSystem) {
				try {
					EntityResult res = ((INoticeSystem) MyNoticeTable.this.locator).getInternalNotices(MyNoticeTable.this.locator.getSessionId(),
							this.unsend, this.unread, this.deleted);

					// Si el parametro alertsystemnotices tiene el valor
					// included se muestran todos los avisos
					if (MyNoticeTable.this.alertSystemNoticesValue.equalsIgnoreCase(MyNoticeTable.ALERT_SYSTEM_NOTICES_INCLUDED)) {
						MyNoticeTable.this.setValue(res);
					} else {
						if (res != null) {
							EntityResult finalRes = new EntityResult();
							int numReg = res.calculateRecordNumber();

							// Si el parametro alertsystemnotices tiene el valor
							// excluded solo se muestran los avisos que NO son
							// del sistema de alertas
							if (MyNoticeTable.this.alertSystemNoticesValue.equalsIgnoreCase(MyNoticeTable.ALERT_SYSTEM_NOTICES_EXCLUDED)) {
								for (int i = 0; i < numReg; i++) {
									Hashtable regData = res.getRecordValues(i);
									Object val = regData.get(INoticeSystem.NOTICE_FROM_PARAMETER);
									if (val != null) {
										if (!val.equals(IAlertSystem.ALERT_SYSTEM)) {
											finalRes.addRecord(regData);
										}
									}
								}
								MyNoticeTable.this.setValue(finalRes);
							} else {
								// Si el parametro alertsystemnotices tiene el
								// valor only solo se muestran los avisos que SI
								// son del sistema de alertas
								if (MyNoticeTable.this.alertSystemNoticesValue.equalsIgnoreCase(MyNoticeTable.ALERT_SYSTEM_NOTICES_ONLY)) {
									for (int i = 0; i < numReg; i++) {
										Hashtable regData = res.getRecordValues(i);
										Object val = regData.get(INoticeSystem.NOTICE_FROM_PARAMETER);
										if (val != null) {
											if (val.equals(IAlertSystem.ALERT_SYSTEM)) {
												finalRes.addRecord(regData);
											}
										}
									}
									MyNoticeTable.this.setValue(finalRes);
								} else {
									// Si el parametro sabe dios lo que tiene!!
									// se muestran todos los avisos
									MyNoticeTable.this.setValue(res);
								}
							}
						} else {
							MyNoticeTable.this.setValue(res);
						}
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			MyNoticeTable.this.updateNoticeWindow(false);
			MyNoticeTable.this.enabledButtonsRowsSelected();
		}
	}

	@Override
	public void addFilterControls() throws Exception {
		super.addFilterControls();
		// String style = StyleUtil.getProperty("Constants", "style", "");
		this.bAllNotices.setIcon(ImageManager.getIcon("common/" + MyNoticeTable.TABLE_BUTTON_NOTICE_ICON));
		this.bUnreadNotices.setIcon(ImageManager.getIcon("common/" + MyNoticeTable.TABLE_BUTTON_UNREADED_ICON));
		this.bCheckRead.setIcon(ImageManager.getIcon("common/" + MyNoticeTable.TABLE_BUTTON_READED_ICON));
	}

}
