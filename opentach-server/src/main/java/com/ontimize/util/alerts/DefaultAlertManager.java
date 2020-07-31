package com.ontimize.util.alerts;

import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.db.Entity;
import com.ontimize.db.EntityResult;
import com.ontimize.db.NullValue;
import com.ontimize.db.TableEntity;
import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.locator.SecureReferenceLocator;
import com.ontimize.util.notice.INoticeSystem;
import com.ontimize.util.quartz.DefaultQuartzManager;
import com.utilmize.tools.classpath.ClassPathSeeker;
import com.utilmize.tools.classpath.IClassPathEntryChecker;

public class DefaultAlertManager implements AlertManager {

	private static final Logger				logger							= LoggerFactory.getLogger(DefaultAlertManager.class);

	private static DefaultAlertManager		instance;

	private static EntityReferenceLocator	buscadorReferencias;
	private static DefaultQuartzManager		defaultQuartzManager;

	protected static final int				INSERT_CONFIGURATION_OPERATION	= 0;
	protected static final int				UPDATE_CONFIGURATION_OPERATION	= 1;

	public static void initializeManager(EntityReferenceLocator buscadorReferencias) throws Exception {

		try {
			if (DefaultAlertManager.instance == null) {
				DefaultAlertManager.instance = new DefaultAlertManager(buscadorReferencias);
			}
			DefaultAlertManager.loadAlerts();
		} catch (SchedulerException e) {
			DefaultAlertManager.logger.error(null, e);
		}
	}

	public static AlertManager getAlertManager() {
		return DefaultAlertManager.instance;
	}

	private DefaultAlertManager(EntityReferenceLocator buscadorReferencias) throws Exception {
		DefaultAlertManager.buscadorReferencias = buscadorReferencias;
		DefaultQuartzManager.initializeManager(DefaultAlertManager.buscadorReferencias);
		DefaultAlertManager.defaultQuartzManager = (DefaultQuartzManager) DefaultQuartzManager.getQuartzManager();
		DefaultAlertManager.getAllFileTaskConfigurations();
	}

	protected static void loadAlerts() {

		try {
			Hashtable<Object, Object> databaseConfigs = DefaultAlertManager.getAllTableTaskConfigurations();

			Enumeration<Object> keys = databaseConfigs.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();

				Hashtable<String, Object> taskConfig = (Hashtable<String, Object>) databaseConfigs.get(key);
				if (taskConfig != null) {
					DefaultAlertManager.loadTask(taskConfig);
				}
			}

			Hashtable<Object, Object> fileConfigurations = DefaultAlertManager.getAllFileTaskConfigurations();
			keys = fileConfigurations.keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();

				Hashtable<String, Object> taskConfig = (Hashtable<String, Object>) databaseConfigs.get(key);
				// Si existe una configuracion previa en la tabla, no se carga
				// la de los ficheros properties
				if (taskConfig == null) {
					taskConfig = (Hashtable<String, Object>) fileConfigurations.get(key);
					if (taskConfig != null) {
						DefaultAlertManager.loadTask(taskConfig);
						DefaultAlertManager.saveTableTaskConfiguration(taskConfig, DefaultAlertManager.INSERT_CONFIGURATION_OPERATION);
					}
				}
			}

		} catch (Exception e) {
			DefaultAlertManager.logger.error(null, e);
		}
	}

	protected static void loadTask(Hashtable<String, Object> taskConfig) throws Exception {

		String taskName = (String) taskConfig.get(IAlertSystem.TASK_NAME_FIELD);
		if (taskName == null) {
			throw new Exception("LOAD TASK: TASK NAME no puede ser null");
		}

		String taskGroup = (String) taskConfig.get(IAlertSystem.TASK_GROUP_FIELD);

		String taskClass = (String) taskConfig.get(IAlertSystem.TASK_CLASS_FIELD);
		if (taskClass == null) {
			throw new Exception("LOAD TASK: TASK CLASS no puede ser null");
		}

		String cronName = IAlertSystem.TASK_CRONNAME_PREFIX + taskName;

		String cronGroup = IAlertSystem.TASK_CRONGROUP_PREFIX + taskGroup;

		String cron = (String) taskConfig.get(IAlertSystem.TASK_CRON);
		if (cron == null) {
			throw new Exception("LOAD TASK: TASK CRON no puede ser null");
		}

		taskConfig.put(IAlertSystem.NOTICE_FROM_PARAMETER, IAlertSystem.ALERT_SYSTEM);
		taskConfig.put(INoticeSystem.NOTICE_FROM_PARAMETER, IAlertSystem.ALERT_SYSTEM);

		Object subject = taskConfig.get(IAlertSystem.NOTICE_SUBJECT);
		if (subject != null) {
			taskConfig.put(INoticeSystem.NOTICE_SUBJECT, subject);
		}

		Object content = taskConfig.get(IAlertSystem.NOTICE_CONTENT);
		if (content != null) {
			taskConfig.put(INoticeSystem.NOTICE_CONTENT, content);
		}

		Vector<Object> v = new Vector<Object>();
		String noticeToValue = (String) taskConfig.get(IAlertSystem.NOTICE_TO_PARAMETER);
		if (noticeToValue != null) {
			StringTokenizer stk = new StringTokenizer(noticeToValue, ";");
			while (stk.hasMoreTokens()) {
				String token = stk.nextToken();
				v.add(token);
			}
		}
		taskConfig.put(IAlertSystem.NOTICE_TO_PARAMETER, v);
		taskConfig.put(INoticeSystem.NOTICE_TO_PARAMETER, v);

		// Se obtienen los datos de consulta a traves del método estático de la
		// clase de la tarea
		Class alertClass = Class.forName(taskClass);
		Method m = alertClass.getMethod("getQueryData", new Class[] {});
		Hashtable<String, Object> queryData = (Hashtable<String, Object>) m.invoke(null, (Object[]) null);

		DefaultAlertManager.defaultQuartzManager.addTask(taskName, taskGroup, taskClass, cronName, cronGroup, cron, null, taskConfig, queryData);

		// Si el estado no es nulo, es una tarea que ya esta configurada en la
		// tabla y por lo tanto habrá que ponerla en el
		// estado que estaba anteriormente
		String state = (String) taskConfig.get(IAlertSystem.TASK_STATE);
		if (state != null) {
			if (state != null) {
				if (state.equals(IAlertSystem.STATE_NORMAL)) {
					// Hay que reanudar la tarea
					DefaultAlertManager.defaultQuartzManager.resumeTrigger(cronName, cronGroup);
				}

				if (state.equals(IAlertSystem.STATE_PAUSED)) {
					// Hay que pausar la tarea
					DefaultAlertManager.defaultQuartzManager.pauseTrigger(cronName, cronGroup);
				}
			}
		}

	}

	// Recupera de la tabla de configuracion, las configuraciones de las tareas
	// que existen.
	protected static Hashtable<Object, Object> getAllTableTaskConfigurations() throws Exception {
		Hashtable<Object, Object> tasksConfigurations = new Hashtable<Object, Object>();

		Entity entity = ((SecureReferenceLocator) DefaultAlertManager.buscadorReferencias)
				.getEntityReferenceFromServer(AlertManager.ALERT_CONFIG_ENTITY);
		if (entity != null) {
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			Vector<Object> av = new Vector<Object>();

			int pId = TableEntity.getEntityPrivilegedId(entity);
			EntityResult result = entity.query(cv, av, pId);
			if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				int numReg = result.calculateRecordNumber();
				for (int i = 0; i < numReg; i++) {
					Hashtable<Object, Object> taskConfig = result.getRecordValues(i);
					Object taskName = taskConfig.get(IAlertSystem.TASK_NAME_FIELD);
					tasksConfigurations.put(taskName, taskConfig);
				}
			}
		}

		return tasksConfigurations;
	}

	protected static Hashtable<String, Object> getTableTaskConfiguration(String name, String group) throws Exception {
		Hashtable<String, Object> tasksConfiguration = null;

		Entity entity = ((SecureReferenceLocator) DefaultAlertManager.buscadorReferencias)
				.getEntityReferenceFromServer(AlertManager.ALERT_CONFIG_ENTITY);
		if (entity != null) {
			Hashtable<String, Object> cv = new Hashtable<String, Object>();
			cv.put(IAlertSystem.TASK_NAME_FIELD, name);
			cv.put(IAlertSystem.TASK_GROUP_FIELD, group);
			Vector<Object> av = new Vector<Object>();

			int pId = TableEntity.getEntityPrivilegedId(entity);
			EntityResult result = entity.query(cv, av, pId);
			if (result.getCode() == EntityResult.OPERATION_SUCCESSFUL) {
				int numReg = result.calculateRecordNumber();
				if (numReg > 0) {
					tasksConfiguration = result.getRecordValues(0);
				}
			}
		}

		return tasksConfiguration;
	}

	protected static void saveTableTaskConfiguration(Hashtable<String, Object> taskConfiguration, int op) throws Exception {

		Entity entity = ((SecureReferenceLocator) DefaultAlertManager.buscadorReferencias)
				.getEntityReferenceFromServer(AlertManager.ALERT_CONFIG_ENTITY);
		if (entity != null) {

			int pId = TableEntity.getEntityPrivilegedId(entity);

			// Se parsean los destinatarios del aviso
			Object obj = taskConfiguration.get(IAlertSystem.NOTICE_TO_PARAMETER);
			if (obj != null) {
				if (obj instanceof Vector) {
					Vector<Object> notifyTo = (Vector<Object>) taskConfiguration.get(IAlertSystem.NOTICE_TO_PARAMETER);
					String pValue = "";
					for (int i = 0; i < notifyTo.size(); i++) {
						pValue = pValue + notifyTo.elementAt(i).toString() + ";";
					}
					pValue = pValue.substring(0, pValue.length() - 1);
					taskConfiguration.put(IAlertSystem.NOTICE_TO_PARAMETER, pValue);

					// Se pone a nulo en NOTICE_MAILTO_PARAMETER ya que no
					// pueden coexistir los dos
					NullValue vn = new NullValue(Types.LONGVARCHAR);
					taskConfiguration.put(IAlertSystem.NOTICE_MAILTO_PARAMETER, vn);

				} else {
					if (obj instanceof String) {
						taskConfiguration.put(IAlertSystem.NOTICE_TO_PARAMETER, obj);

						// Se pone a nulo en NOTICE_MAILTO_PARAMETER ya que no
						// pueden coexistir los dos
						NullValue vn = new NullValue(Types.LONGVARCHAR);
						taskConfiguration.put(IAlertSystem.NOTICE_MAILTO_PARAMETER, vn);
					}
				}
			}

			obj = taskConfiguration.get(IAlertSystem.NOTICE_MAILTO_PARAMETER);
			if (obj != null) {
				if (obj instanceof Vector) {
					Vector<Object> notifyTo = (Vector<Object>) taskConfiguration.get(IAlertSystem.NOTICE_MAILTO_PARAMETER);
					String pValue = "";
					for (int i = 0; i < notifyTo.size(); i++) {
						pValue = pValue + notifyTo.elementAt(i).toString() + ";";
					}
					pValue = pValue.substring(0, pValue.length() - 1);
					taskConfiguration.put(IAlertSystem.NOTICE_MAILTO_PARAMETER, pValue);

					// Se pone a nulo en NOTICE_TO_PARAMETER ya que no pueden
					// coexistir los dos
					NullValue vn = new NullValue(Types.LONGVARCHAR);
					taskConfiguration.put(IAlertSystem.NOTICE_TO_PARAMETER, vn);
				} else {
					if (obj instanceof String) {
						taskConfiguration.put(IAlertSystem.NOTICE_MAILTO_PARAMETER, obj);
						// Se pone a nulo en NOTICE_TO_PARAMETER ya que no
						// pueden coexistir los dos
						NullValue vn = new NullValue(Types.LONGVARCHAR);
						taskConfiguration.put(IAlertSystem.NOTICE_TO_PARAMETER, vn);
					}
				}
			}

			obj = taskConfiguration.get(IAlertSystem.NOTICE_FORCE_READ);
			if (obj instanceof String) {
				String value = (String) taskConfiguration.get(IAlertSystem.NOTICE_FORCE_READ);
				if ((value != null) && value.trim().equalsIgnoreCase("yes")) {
					taskConfiguration.put(IAlertSystem.NOTICE_FORCE_READ, Integer.valueOf(1));
				} else {
					taskConfiguration.put(IAlertSystem.NOTICE_FORCE_READ, Integer.valueOf(0));
				}
			} else {
				if (obj instanceof Integer) {
					taskConfiguration.put(IAlertSystem.NOTICE_FORCE_READ, obj);
				}
			}

			obj = taskConfiguration.get(IAlertSystem.NOTICE_RESPONSE_REQUEST);
			if (obj instanceof String) {
				String value = (String) taskConfiguration.get(IAlertSystem.NOTICE_RESPONSE_REQUEST);
				if ((value != null) && value.trim().equalsIgnoreCase("yes")) {
					taskConfiguration.put(IAlertSystem.NOTICE_RESPONSE_REQUEST, Integer.valueOf(1));
				} else {
					taskConfiguration.put(IAlertSystem.NOTICE_RESPONSE_REQUEST, Integer.valueOf(0));
				}
			} else {
				if (obj instanceof Integer) {
					taskConfiguration.put(IAlertSystem.NOTICE_RESPONSE_REQUEST, obj);
				}
			}

			obj = taskConfiguration.get(IAlertSystem.NOTICE_SEND_MAIL);
			if (obj instanceof String) {
				String value = (String) taskConfiguration.get(IAlertSystem.NOTICE_SEND_MAIL);
				if ((value != null) && value.equalsIgnoreCase("yes")) {
					taskConfiguration.put(IAlertSystem.NOTICE_SEND_MAIL, Integer.valueOf(1));
				} else {
					taskConfiguration.put(IAlertSystem.NOTICE_SEND_MAIL, Integer.valueOf(0));
				}
			} else {
				if (obj instanceof Integer) {
					taskConfiguration.put(IAlertSystem.NOTICE_SEND_MAIL, obj);
				}
			}

			Object taskName = taskConfiguration.get(IAlertSystem.TASK_NAME_FIELD);
			Object taskGroup = taskConfiguration.get(IAlertSystem.TASK_GROUP_FIELD);

			if (op == DefaultAlertManager.INSERT_CONFIGURATION_OPERATION) {
				EntityResult result = entity.insert(taskConfiguration, pId);
				if (result.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
					throw new Exception(result.getMessage());
				}
			}

			if (op == DefaultAlertManager.UPDATE_CONFIGURATION_OPERATION) {

				if (taskName != null) {
					// Si se ha cambiado el estado de la tarea se procede a
					// cambiarlo
					String state = (String) taskConfiguration.get(IAlertSystem.TASK_STATE);
					if (state != null) {
						// Se obtienen el nombre del cron y el grupo del cron
						// para cambiar su estado
						String cronName = IAlertSystem.TASK_CRONNAME_PREFIX + taskName;
						String cronGroup = null;
						if (taskGroup != null) {
							cronGroup = IAlertSystem.TASK_CRONGROUP_PREFIX + taskGroup;
						}

						if (state.equals(IAlertSystem.STATE_NORMAL)) {
							// Hay que reanudar la tarea
							DefaultAlertManager.defaultQuartzManager.resumeTrigger(cronName, cronGroup);
						}

						if (state.equals(IAlertSystem.STATE_PAUSED)) {
							// Hay que pausar la tarea
							DefaultAlertManager.defaultQuartzManager.pauseTrigger(cronName, cronGroup);
						}
					}

					// Se actualiza la tabla con la configuracion
					Hashtable<String, Object> cv = new Hashtable<String, Object>();
					cv.put(IAlertSystem.TASK_NAME_FIELD, taskName);
					EntityResult result = entity.update(taskConfiguration, cv, pId);
					if (result.getCode() != EntityResult.OPERATION_SUCCESSFUL) {
						throw new Exception(result.getMessage());
					}
				} else {
					throw new Exception("M_TASKNAME_NEEDED_ON_UPDATE_CONFIG");
				}
			}
		}
	}

	protected static Hashtable<Object, Object> getAllFileTaskConfigurations() throws Exception {
		final Hashtable<Object, Object> tasksConfigurations = new Hashtable<Object, Object>();
		//		String[] ext = new String[] { "properties" };
		//		PropertiesLoader loader = new PropertiesLoader(ext);

		ClassPathSeeker.find(AlertManager.PROPERTIES_PATH, new IClassPathEntryChecker<Void>() {
			@Override
			public Void check(String pckgName, String file) {
				if (file.endsWith("properties")) {
					InputStream is = null;
					try {
						is = Thread.currentThread().getContextClassLoader().getResourceAsStream(pckgName + "/" + file);
						if (is != null) {
							Properties taskProperties = new Properties();
							taskProperties.load(is);
							Object taskName = taskProperties.get(IAlertSystem.TASK_NAME_FIELD);
							if (taskName != null) {
								tasksConfigurations.put(taskName, taskProperties);
							}
						}
					} catch (Exception e) {
						DefaultAlertManager.logger.error(null, e);
					} finally {
						if (is != null) {
							try {
								is.close();
							} catch (Exception ex) {
								// do nothing
							}
						}
					}
				}
				return null;
			}
		});
		return tasksConfigurations;
	}

	@Override
	public void updateAlertConfiguration(Hashtable<String,Object> alertConfiguration) {

		try {
			DefaultAlertManager.saveTableTaskConfiguration(alertConfiguration, DefaultAlertManager.UPDATE_CONFIGURATION_OPERATION);
		} catch (Exception e) {
			DefaultAlertManager.logger.error(null, e);
		}
	}

	@Override
	public String[] getAlertGroups() {

		return DefaultAlertManager.defaultQuartzManager.getTaskGroupNames();
	}

	@Override
	public EntityResult getAlertsGroupData(String group) {

		Vector<Object> data = DefaultAlertManager.defaultQuartzManager.getGroupTriggersData(group);
		EntityResult resultEntidad = new EntityResult();

		for (int i = 0; i < data.size(); i++) {
			Hashtable<String, Object> keyData = (Hashtable<String, Object>) data.elementAt(i);
			resultEntidad.addRecord(keyData);
		}

		return resultEntidad;
	}

	@Override
	public EntityResult getAllAlertsData() {

		EntityResult resultEntidad = new EntityResult();
		String[] groups = DefaultAlertManager.defaultQuartzManager.getTaskGroupNames();

		for (int i = 0; i < groups.length; i++) {
			String group = groups[i];
			Vector<Object> data = DefaultAlertManager.defaultQuartzManager.getGroupTriggersData(group);
			for (int j = 0; j < data.size(); j++) {
				Hashtable<String, Object> keyData = (Hashtable<String, Object>) data.elementAt(j);
				resultEntidad.addRecord(keyData);
			}
		}

		return resultEntidad;
	}

	@Override
	public EntityResult getAlertData(String alertName, String alertGroup, String cronName, String cronGroup) {

		EntityResult resultEntidad = new EntityResult();

		try {
			Hashtable<String, Object> triggerData = DefaultAlertManager.defaultQuartzManager.getTriggerData(alertName, alertGroup, cronName, cronGroup);

			Hashtable<String, Object> taskConfig = DefaultAlertManager.getTableTaskConfiguration(alertName, alertGroup);

			Object valueConfig = taskConfig.get(IAlertSystem.NOTICE_FORCE_READ);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_FORCE_READ, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.NOTICE_RESPONSE_REQUEST);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_RESPONSE_REQUEST, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.NOTICE_SEND_MAIL);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_SEND_MAIL, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.NOTICE_TO_PARAMETER);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_TO_PARAMETER, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.NOTICE_SUBJECT);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_SUBJECT, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.NOTICE_CONTENT);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_CONTENT, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.NOTICE_MAILTO_PARAMETER);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.NOTICE_MAILTO_PARAMETER, valueConfig);
			}

			valueConfig = taskConfig.get(IAlertSystem.TASK_CRON);
			if (valueConfig != null) {
				triggerData.put(IAlertSystem.TASK_CRON, valueConfig);
			}

			resultEntidad.addRecord(triggerData);
		} catch (Exception e) {
			DefaultAlertManager.logger.error(null, e);
		}

		return resultEntidad;
	}

	@Override
	public boolean pauseAlert(String name, String group) {
		return DefaultAlertManager.defaultQuartzManager.pauseTrigger(name, group);
	}

	@Override
	public boolean resumeAlert(String name, String group) {
		return DefaultAlertManager.defaultQuartzManager.resumeTrigger(name, group);
	}

	static class PropertiesLoader implements FilenameFilter {

		protected Set<Object>	extensionsSet;

		public PropertiesLoader(String[] extensions) {
			this.extensionsSet = new TreeSet<Object>();

			for (Iterator<String> ext = Arrays.asList(extensions).iterator(); ext.hasNext();) {
				this.extensionsSet.add(ext.next().toString().toLowerCase().trim());
			}
			this.extensionsSet.remove("");
		}

		@Override
		public boolean accept(File dir, String name) {
			final Iterator<Object> exts = this.extensionsSet.iterator();
			while (exts.hasNext()) {
				if (name.toLowerCase().endsWith(exts.next().toString())) {
					return true;
				}
			}
			return false;
		}

		public File[] getFiles(File dir) {

			return dir.listFiles(this);
		}
	}

}
