package com.ontimize.util.alerts;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JToggleButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.ontimize.db.EntityResult;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.DetailForm;
import com.ontimize.gui.FormExt;
import com.utilmize.client.gui.field.table.UTable;


public class AlertTable  extends UTable {



	protected JButton bPauseAlert;
	protected JButton bResumeAlert;
	protected JComboBox comboGroups;
	protected RefreshThread refreshThread;


	protected String insertFormName;
	protected DetailForm insertForm;


	protected String queryFormName;
	protected FormExt queryForm;
	protected JDialog queryFormDialog;

	public AlertTable(Hashtable arg0) throws Exception {
		super(arg0);

		this.addGroupsControl();
		this.addPauseAlertsButton();
		this.addResumeAlertsButton();


		this.getJTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				AlertTable.this.enabledButtonsRowsSelected();
			}
		});

		this.refreshGroupsControl();
		this.refreshTable();

		this.refreshThread=new RefreshThread();
		this.refreshThread.start();

	}



	@Override
	public void init(Hashtable ht) throws Exception{

		super.init(ht);
		if(ht.containsKey("insertform")) {
			this.insertFormName = (String)ht.get("insertform");
		}
		this.queryFormName = this.formName;
	}


	@Override
	public void openInsertDetailForm() {
		if(this.insertFormName != null){
			this.formName = this.insertFormName;
			this.detailForm = this.insertForm;
		}

		super.openInsertDetailForm();
		if(this.insertForm == null) {
			this.insertForm = this.detailForm;
		}
	}





	@Override
	public void openDetailForm(int arg0){

		if(this.queryFormName!=null){

			if(this.queryForm==null) {
				this.queryForm=(FormExt)this.parentForm.getFormManager().getFormCopy(this.queryFormName);
			}
			if(this.queryFormDialog==null) {
				this.queryFormDialog=this.queryForm.putInModalDialog();
			}
			this.queryForm.deleteDataFields();

			Hashtable datosFila=this.getRowData(arg0);

			String taskName=(String)datosFila.get("TASK_NAME");
			String taskGroup=(String)datosFila.get("TASK_GROUP");
			String cronName=(String)datosFila.get("CRON_NAME");
			String cronGroup=(String)datosFila.get("CRON_GROUP");
			if(taskName!=null){
				try {
					EntityResult taskData   = ((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).getAlertData(taskName,taskGroup,cronName,cronGroup);

					Hashtable datosRegistro = taskData.getRecordValues(0);
					this.queryForm.setDataFieldValues(datosRegistro);



					//					Object data=taskData.get(IAlertSystem.TASK_NAME_FIELD);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.TASK_NAME_FIELD,data);
					//
					//					data=taskData.get(IAlertSystem.TASK_GROUP_FIELD);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.TASK_GROUP_FIELD,data);
					//
					//					data=taskData.get(IAlertSystem.TASK_CRON);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.TASK_CRON,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_TO_PARAMETER);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_TO_PARAMETER,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_FROM_PARAMETER);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_FROM_PARAMETER,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_SUBJECT);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_SUBJECT,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_CONTENT);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_CONTENT,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_RESPONSE_REQUEST);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_RESPONSE_REQUEST,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_FORCE_READ);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_FORCE_READ,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_SEND_MAIL);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_SEND_MAIL,data);
					//
					//					data=taskData.get(IAlertSystem.NOTICE_MAILTO_PARAMETER);
					//					if(data!=null)
					//						queryForm.setValorCampo(IAlertSystem.NOTICE_MAILTO_PARAMETER,data);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			this.queryForm.getInteractionManager().setUpdateMode();
			this.queryFormDialog.setTitle(ApplicationManager.getTranslation("alertConfiguration",this.parentForm.getResourceBundle()));
			this.queryFormDialog.setVisible(true);
		}
	}






	public void addGroupsControl(){
		try {
			String[] groups=((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).getAlertGroups();
			this.comboGroups=new JComboBox(groups);
			this.comboGroups.insertItemAt(null,0);
			this.comboGroups.setPreferredSize(new Dimension(200,20));
			this.comboGroups.addActionListener(new ComboGroupsListener());
			this.addComponentToControls(this.comboGroups);
			this.comboGroups.setSelectedItem(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	public void addPauseAlertsButton(){
		try {
			this.bPauseAlert = new JButton(ApplicationManager.getIcon("com/ontimize/util/alerts/images/clock_pause.png"));
			this.bPauseAlert.setToolTipText(ApplicationManager.getTranslation("bPauseAlert", ApplicationManager.getApplication().getResourceBundle()));
			this.bPauseAlert.addMouseListener(new RollOverListener(this.bPauseAlert));
			this.bPauseAlert.addActionListener(new PauseAlerts());
			this.addButtonToControls(this.bPauseAlert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void addResumeAlertsButton(){
		try {
			this.bResumeAlert = new JButton(ApplicationManager.getIcon("com/ontimize/util/alerts/images/clock_run.png"));
			this.bResumeAlert.setToolTipText(ApplicationManager.getTranslation("bResumeAlert", ApplicationManager.getApplication().getResourceBundle()));
			this.bResumeAlert.addMouseListener(new RollOverListener(this.bResumeAlert));
			this.bResumeAlert.addActionListener(new ResumeAlerts());
			this.addButtonToControls(this.bResumeAlert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 * Cuando se activa la tabla sólo se activa el botón bCheckAsRead si está seleccionado bUnreadNotices
	 */
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		this.enabledButtonsRowsSelected();
	}


	protected void enabledButtonsRowsSelected() {
		if ((this.getSelectedRows() != null) && (this.getSelectedRows().length > 0)) {
			if (this.bPauseAlert != null) {
				this.bPauseAlert.setEnabled(true);
			}
			if (this.bResumeAlert != null) {
				this.bResumeAlert.setEnabled(true);
			}
		} else {
			if (this.bPauseAlert != null) {
				this.bPauseAlert.setEnabled(false);
			}
			if (this.bResumeAlert != null) {
				this.bResumeAlert.setEnabled(false);
			}
		}
	}





	class RollOverListener extends MouseAdapter {
		private final AbstractButton boton;

		public RollOverListener(AbstractButton b) {
			this.boton = b;
			if (!((this.boton instanceof JToggleButton) && (((JToggleButton) this.boton).isSelected()))) {
				this.boton.setBorderPainted(false);
			}

		}

		public void setRollover(boolean rollover) {
			if (!((this.boton instanceof JToggleButton) && (((JToggleButton) this.boton).isSelected()))) {
				this.boton.setBorderPainted(false);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			this.boton.setBorderPainted(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			if (!((this.boton instanceof JToggleButton) && (((JToggleButton) this.boton).isSelected()))) {
				this.boton.setBorderPainted(false);
			}
		}
	}



	class PauseAlerts implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {

			int[] selectedRows=AlertTable.this.getSelectedRows();
			for(int i=0;i<selectedRows.length;i++){
				int index=selectedRows[i];

				Hashtable rowData=AlertTable.this.getRowData(index);
				if(rowData!=null){
					Object taskName=rowData.get(IAlertSystem.TASK_NAME_FIELD);
					Object taskGroup=rowData.get(IAlertSystem.TASK_GROUP_FIELD);
					Object name=rowData.get(IAlertSystem.CRON_NAME_FIELD);
					Object group=rowData.get(IAlertSystem.CRON_GROUP_FIELD);
					Object state=rowData.get(IAlertSystem.STATE_FIELD);

					if(((String)state).equals(IAlertSystem.STATE_NORMAL)) {
						try {
							boolean success=((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).pauseAlert((String)name,(String)group);
							if(success){
								Hashtable modifiedConfig=new Hashtable();
								modifiedConfig.put(IAlertSystem.TASK_NAME_FIELD,taskName);
								modifiedConfig.put(IAlertSystem.TASK_GROUP_FIELD,taskGroup);
								modifiedConfig.put(IAlertSystem.TASK_STATE,IAlertSystem.STATE_PAUSED);

								((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).updateAlertConfiguration(modifiedConfig);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(selectedRows.length>0) {
				AlertTable.this.refreshTable();
			}
		}
	}


	class ResumeAlerts implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			int[] selectedRows=AlertTable.this.getSelectedRows();
			for(int i=0;i<selectedRows.length;i++){
				int index=selectedRows[i];

				Hashtable rowData=AlertTable.this.getRowData(index);
				if(rowData!=null){
					Object taskName=rowData.get(IAlertSystem.TASK_NAME_FIELD);
					Object taskGroup=rowData.get(IAlertSystem.TASK_GROUP_FIELD);
					Object name=rowData.get(IAlertSystem.CRON_NAME_FIELD);
					Object group=rowData.get(IAlertSystem.CRON_GROUP_FIELD);
					Object state=rowData.get(IAlertSystem.STATE_FIELD);

					if(((String)state).equals(IAlertSystem.STATE_PAUSED)) {
						try {
							boolean success=((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).resumeAlert((String)name,(String)group);
							if(success){
								Hashtable modifiedConfig=new Hashtable();
								modifiedConfig.put(IAlertSystem.TASK_NAME_FIELD,taskName);
								modifiedConfig.put(IAlertSystem.TASK_GROUP_FIELD,taskGroup);
								modifiedConfig.put(IAlertSystem.TASK_STATE,IAlertSystem.STATE_NORMAL);

								((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).updateAlertConfiguration(modifiedConfig);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}

			if(selectedRows.length>0) {
				AlertTable.this.refreshTable();
			}
		}

	}



	class ComboGroupsListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			AlertTable.this.refreshTable();
		}
	}



	@Override
	public void refresh() {

		this.refreshTable();
	}




	protected void refreshGroupsControl(){
		try {
			Object selectedObject=this.comboGroups.getSelectedItem();
			String[] groups=((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).getAlertGroups();
			this.comboGroups.setModel(new DefaultComboBoxModel(groups));
			this.comboGroups.insertItemAt(null,0);
			this.comboGroups.setSelectedItem(selectedObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	protected void refreshTable(){
		try {
			Object groupSelected=this.comboGroups.getSelectedItem();
			if(groupSelected!=null){
				this.setValue(((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).getAlertsGroupData((String)groupSelected));
			}else{
				this.setValue(((IAlertSystem)ApplicationManager.getApplication().getReferenceLocator()).getAllAlertsData());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	class RefreshThread extends Thread{

		public RefreshThread() {
		}

		@Override
		public void run() {

			while(!this.isInterrupted()){
				try {
					Thread.sleep(15000);

					AlertTable.this.refreshGroupsControl();
					AlertTable.this.refreshTable();
				}
				catch (InterruptedException ex) {
					System.out.println(">>>> HILO ACTUALIZADOR de TABLA DE ALERTAS detenido: "+ex.getMessage());
				}
				catch (Exception ex) {
					System.out.println(">>>> Error en HILO ACTUALIZADOR de TABLA DE ALERTAS: "+ex.getMessage());
					this.interrupt();
				}
			}
			System.out.println(">>>> El HILO ACTUALIZADOR de TABLA DE ALERTAS se ha detenido...");
		}


	}


}
