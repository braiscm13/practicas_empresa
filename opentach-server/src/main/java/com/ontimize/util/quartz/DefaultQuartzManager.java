package com.ontimize.util.quartz;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.locator.EntityReferenceLocator;
import com.ontimize.util.alerts.IAlertSystem;

public final class DefaultQuartzManager implements QuartzManager {

	private static final Logger				logger	= LoggerFactory.getLogger(DefaultQuartzManager.class);

	// private static Log log = LogFactory.getLog(DefaultQuartzManager.class);

	private static DefaultQuartzManager		instance;

	// private static long uniqueTriggerId;

	private final EntityReferenceLocator	locator;

	private final SchedulerFactory			schedulerFactory;

	private final Scheduler					scheduler;

	/**
	 * Inicializa la clase con los parámetros de configuración por defecto
	 *
	 * @param buscadorReferencias
	 */
	public static void initializeManager(EntityReferenceLocator buscadorReferencias) {

		try {
			if (DefaultQuartzManager.instance == null) {
				DefaultQuartzManager.instance = new DefaultQuartzManager(buscadorReferencias);
			}

		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}
	}

	/**
	 * Inicializa la clase utilizando las propiedades indicadas en el fichero de
	 * configuración
	 *
	 * @param buscadorReferencias
	 * @param properties
	 *            Fichero de configuración
	 */
	public static void initializeManager(EntityReferenceLocator buscadorReferencias, Properties properties) {

		try {
			if (DefaultQuartzManager.instance == null) {
				DefaultQuartzManager.instance = new DefaultQuartzManager(buscadorReferencias, properties);
			}
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}
	}

	/**
	 * Devuelve la instancia del QuartzManager
	 *
	 * @return
	 */
	public static QuartzManager getQuartzManager() {
		return DefaultQuartzManager.instance;
	}

	private DefaultQuartzManager(EntityReferenceLocator buscadorReferencias) throws SchedulerException {
		// log.debug(" ****** Inizializando manager de quartz ******");

		this.locator = buscadorReferencias;

		Properties properties = new Properties();
		URL url = this.getClass().getClassLoader().getResource("com/ontimize/util/quartz/schedulerConfig.properties");

		try {
			properties.load(url.openStream());
		} catch (IOException e) {
			DefaultQuartzManager.logger.error(null, e);
		}

		this.schedulerFactory = new StdSchedulerFactory(properties);
		this.scheduler = this.schedulerFactory.getScheduler();

		this.scheduler.start();
		// log.debug(" ****** Manager de quartz inicializado ******");
	}

	/**
	 *
	 * @param buscadorReferencias
	 * @param properties
	 * @throws SchedulerException
	 */
	private DefaultQuartzManager(EntityReferenceLocator buscadorReferencias, Properties properties) throws SchedulerException {
		// log.debug(" ****** Inizializando manager de quartz ******");

		this.locator = buscadorReferencias;

		this.schedulerFactory = new StdSchedulerFactory(properties);
		this.scheduler = this.schedulerFactory.getScheduler();

		this.scheduler.start();
		// log.debug(" ****** Manager de quartz inicializado ******");
	}

	/**
	 * Introduce una nueva tarea programada en el programador del Quartz
	 *
	 * @param taskName
	 *            Nombre de la nueva tarea, que debe ser único
	 * @param taskGroup
	 *            Nombre del grupo de tareas en el que se incluirá, puede ser
	 *            null
	 * @param taskClassName
	 *            Nombre de la clase que se ejecutará la tarea, esta clase debe
	 *            implementar la interfaz Task
	 * @param cronName
	 *            Nombre del hilo en el que se incorporará la tarea, que debe
	 *            ser único
	 * @param cronGroup
	 *            Nombre del grupo de hilos al que se añadirá, puede ser null
	 * @param cronExpression
	 *            Expresión que indica la secuencia temporal con la que se
	 *            deberá ejecutar la tarea<br>
	 *            Se trata de una cadena con 7 parametros separados por espacio
	 *            que se corresponden con: <br>
	 *            segundos(0-59), minutos(0-59), horas(0-23), dia del mes
	 *            (1-31), mes(1-12 o JAN-DEC), dia de la semana(1-7 o SUN-SAT),
	 *            año (opcional, 1970-2099)
	 * @param endTime
	 *            Fecha de parada o finalización
	 * @param actionParametersMap
	 *            Parámtros necesarios para ejectuar la acción
	 * @param queryParametersMap
	 *            Parámetros necesarios para realizar una consula que permita
	 *            decidir si la acción se ejecuta o no
	 */
	@Override
	public void addTask(String taskName, String taskGroup, String taskClassName, String cronName, String cronGroup, String cronExpression,
			Date endTime, Map actionParametersMap, Map queryParametersMap) {

		try {
			if (taskClassName == null) {
				throw new Exception("El nombre de la clase no puede ser nulo");
			}
			Class taskClass = Class.forName(taskClassName);
			JobDetail jobDetail = new JobDetail(taskName, taskGroup, taskClass);

			if ((actionParametersMap != null) || (queryParametersMap != null)) {
				Hashtable<String, Object> h = new Hashtable<String, Object>();
				if (actionParametersMap != null) {
					h.put(Task.ACTION_PARAMETERS_KEY, actionParametersMap);
				}
				if (queryParametersMap != null) {
					h.put(Task.QUERY_PARAMETERS_KEY, queryParametersMap);
				}
				jobDetail.setJobDataMap(new JobDataMap(h));
			}

			Trigger trigger = new CronTrigger(cronName, cronGroup, cronExpression);

			if (endTime != null) {
				trigger.setEndTime(endTime);
			}
			this.scheduler.scheduleJob(jobDetail, trigger);

		} catch (ClassNotFoundException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (ParseException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (Exception e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		}
	}

	/**
	 * Incorpora una nueva tarea
	 *
	 * @param taskName
	 *            Nombre de la tarea, que debe ser único
	 * @param taskGroup
	 *            Nombre del grupo de tareas, puede ser null
	 * @param taskClassName
	 *            Nombre de la clase (Task) que se ejecutará
	 * @param cronName
	 *            Nombre del hijo que se ejecutará, que debe ser único
	 * @param cronGroup
	 *            Nombre del grupo de hilos, que puede ser null
	 * @param cronExpression
	 *            Expresión temporal para configurar el hilo
	 * @param endTime
	 *            Fecha de finalización
	 */
	@Override
	public void addTask(String taskName, String taskGroup, String taskClassName, String cronName, String cronGroup, String cronExpression,
			Date endTime) {

		try {
			if (taskClassName == null) {
				throw new Exception("El nombre de la clase no puede ser nulo");
			}
			Class taskClass = Class.forName(taskClassName);
			JobDetail jobDetail = new JobDetail(taskName, taskGroup, taskClass);
			Trigger trigger = new CronTrigger(cronName, cronGroup, cronExpression);
			if (endTime != null) {
				trigger.setEndTime(endTime);
			}
			this.scheduler.scheduleJob(jobDetail, trigger);

		} catch (ClassNotFoundException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (ParseException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (Exception e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		}
	}

	/**
	 * Inserción de una nueva tarea
	 *
	 * @param taskName
	 *            Nombre de la tarea, que debe ser único
	 * @param taskGroup
	 *            Nombre del grupo de tareas, que puede ser null
	 * @param taskClassName
	 *            Nombre de la clase (Task) que se ejecutará
	 * @param triggerName
	 *            Nombre del hilo, que debe ser único
	 * @param triggerGroup
	 *            Nombre del grupo de hilos que puede ser null
	 * @param startTime
	 *            Fecha de la primera ejecución
	 * @param endTime
	 *            Fecha máxima para la finalización
	 * @param repeatCount
	 *            Número de veces que se desea repetir la ejecución
	 * @param repeatInterval
	 *            Intervalo de repetición
	 * @param actionParametersMap
	 *            Parámetros necesarios para la realización de la acción
	 * @param queryParametersMap
	 *            Parámetros necesarios para realizar una consulta que indicará
	 *            si la acción debe o no ejecutarse
	 */
	@Override
	public void addTask(String taskName, String taskGroup, String taskClassName, String triggerName, String triggerGroup, Date startTime,
			Date endTime, int repeatCount, long repeatInterval, Map actionParametersMap, Map queryParametersMap) {

		try {
			if (taskClassName == null) {
				throw new Exception("El nombre de la clase no puede ser nulo");
			}
			Class taskClass = Class.forName(taskClassName);
			JobDetail jobDetail = new JobDetail(taskName, taskGroup, taskClass);
			if ((actionParametersMap != null) || (queryParametersMap != null)) {
				Hashtable<String, Object> h = new Hashtable<String, Object>();
				if (actionParametersMap != null) {
					h.put(Task.ACTION_PARAMETERS_KEY, actionParametersMap);
				}
				if (queryParametersMap != null) {
					h.put(Task.QUERY_PARAMETERS_KEY, queryParametersMap);
				}
				jobDetail.setJobDataMap(new JobDataMap(h));
			}
			Trigger trigger = new SimpleTrigger(triggerName, triggerGroup, startTime, endTime, repeatCount, repeatInterval);
			this.scheduler.scheduleJob(jobDetail, trigger);
		} catch (ClassNotFoundException e) {
			DefaultQuartzManager.logger.error(null, e);
		} catch (ParseException e) {
			DefaultQuartzManager.logger.error(null, e);
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		} catch (Exception e) {
			DefaultQuartzManager.logger.error(null, e);
		}
	}

	/**
	 * Inserta una nueva tarea
	 *
	 * @param taskProperties
	 *            Fichero de propiedades que debe tener: <br>
	 *            Nombre de la tarea, grupo de tareas, nombre del hilo, grupo de
	 *            hilos, expresión temporal y clase que se ejecutará
	 */
	@Override
	public void addTask(Properties taskProperties) {

		String taskName = taskProperties.getProperty(Task.TASK_NAME_KEY);
		String taskGroup = taskProperties.getProperty(Task.TASK_GROUP_KEY);
		String taskClassName = taskProperties.getProperty(Task.TASK_CLASS_KEY);
		String cronExpression = taskProperties.getProperty(Task.CRON_EXPRESSION_KEY);
		String cronName = taskProperties.getProperty(Task.CRON_NAME_KEY);
		String cronGroup = taskProperties.getProperty(Task.CRON_GROUP_KEY);

		this.addTask(taskName, taskGroup, taskClassName, cronName, cronGroup, cronExpression, null);
	}

	/**
	 * Inserta una nueva tarea
	 *
	 * @param propertiesFile
	 *            Ruta del fichero de configuración de la tarea
	 */
	@Override
	public void addTask(String propertiesFile) {
		try {
			Properties taskProperties = null;
			ClassLoader cl = this.getClass().getClassLoader();
			taskProperties = new Properties();
			taskProperties.load(cl.getResourceAsStream(propertiesFile));
			this.addTask(taskProperties);
		} catch (IOException e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		} catch (Exception e) {
			DefaultQuartzManager.logger.error(null, e);
			// log.error(e.getMessage(), e);
		}
	}

	/**
	 * Elimina una tarea que estuviera configurada
	 *
	 * @param taskName
	 *            Nombre de la tarea que se quiere eliminar
	 * @param taskGroup
	 *            Nombre del grupo de tareas en que se incluia
	 * @return Indica si se ha eliminado correctamente o no
	 */
	@Override
	public boolean removeTask(String taskName, String taskGroup) {
		try {
			if (this.scheduler.getJobDetail(taskName, taskGroup) != null) {
				this.scheduler.deleteJob(taskName, taskGroup);
				return true;
			} else {
				return false;
			}
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}
		return false;
	}

	/**
	 * Detiene temporalmente la ejecución de un hilo
	 *
	 * @param cronName
	 *            Nombre del hilo que se desea detener
	 * @param cronGroup
	 *            Nombre del grupo de hilos
	 * @return Indica si se ha detenido correctamente o no
	 */
	public boolean pauseTrigger(String cronName, String cronGroup) {
		try {
			if (this.scheduler.getTrigger(cronName, cronGroup) != null) {
				this.scheduler.pauseTrigger(cronName, cronGroup);
				return true;
			} else {
				return false;
			}
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}
		return false;
	}

	/**
	 * Reinicia la ejecución de un hilo que estuviera en pausa
	 *
	 * @param cronName
	 *            Nombre del hilo
	 * @param cronGroup
	 *            Nombre del grupo de hilos al que pertenece
	 * @return
	 */
	public boolean resumeTrigger(String cronName, String cronGroup) {
		try {
			if (this.scheduler.getTrigger(cronName, cronGroup) != null) {
				this.scheduler.resumeTrigger(cronName, cronGroup);
				return true;
			} else {
				return false;
			}
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}
		return false;
	}

	public String[] getTaskGroupNames() {

		String[] groupNames = new String[0];

		try {
			groupNames = this.scheduler.getJobGroupNames();
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}

		return groupNames;
	}

	public Vector<Object> getGroupTriggersData(String task_group) {

		Vector<Object> triggersData = new Vector<Object>();

		try {
			String[] taskNames = this.scheduler.getJobNames(task_group);
			if (taskNames != null) {
				for (int i = 0; i < taskNames.length; i++) {
					String task_name = taskNames[i];
					Vector<Object> taskTriggersData = this.getTaskTriggersData(task_name, task_group);
					triggersData.addAll(taskTriggersData);
				}
			}

		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}

		return triggersData;
	}

	public Hashtable<String, Object> getTriggerData(String task_name, String task_group, String triggerName, String triggerGroup) {

		Hashtable<String, Object> triggerData = new Hashtable<String, Object>();

		try {
			Trigger[] triggers = this.scheduler.getTriggersOfJob(task_name, task_group);

			Trigger trigger = null;
			boolean finded = false;
			for (int i = 0; i < triggers.length; i++) {
				trigger = triggers[i];

				if (trigger.getName().equals(triggerName) && trigger.getGroup().equals(triggerGroup)) {
					finded = true;
					break;
				}
			}

			if (finded && (trigger != null)) {
				Object obj = trigger.getJobName();
				if (obj != null) {
					triggerData.put(IAlertSystem.TASK_NAME_FIELD, obj);
				}

				obj = trigger.getJobGroup();
				if (obj != null) {
					triggerData.put(IAlertSystem.TASK_GROUP_FIELD, obj);
				}

				obj = trigger.getName();
				if (obj != null) {
					triggerData.put(IAlertSystem.CRON_NAME_FIELD, obj);
				}

				obj = trigger.getGroup();
				if (obj != null) {
					triggerData.put(IAlertSystem.CRON_GROUP_FIELD, obj);
				}

				obj = trigger.getStartTime();
				if (obj != null) {
					triggerData.put(IAlertSystem.STARTTIME_FIELD, obj);
				}

				obj = trigger.getEndTime();
				if (obj != null) {
					triggerData.put(IAlertSystem.ENDTIME_FIELD, obj);
				}

				obj = trigger.getNextFireTime();
				if (obj != null) {
					trigger.getNextFireTime();
				}
				triggerData.put(IAlertSystem.NEXTTIMEFIRE_FIELD, obj);

				int state = this.scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
				triggerData.put(IAlertSystem.STATE_FIELD, this.getStateString(state));
			}

		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}

		return triggerData;
	}

	public Vector<Object> getTaskTriggersData(String task_name, String task_group) {

		Vector<Object> triggersData = new Vector<Object>();

		try {
			Trigger[] triggers = this.scheduler.getTriggersOfJob(task_name, task_group);

			for (int i = 0; i < triggers.length; i++) {
				Trigger trigger = triggers[i];

				if (trigger != null) {
					Hashtable<String, Object> triggerData = new Hashtable<String, Object>();

					triggerData.put(IAlertSystem.TASK_NAME_FIELD, task_name);
					triggerData.put(IAlertSystem.TASK_GROUP_FIELD, task_group);

					Object obj = trigger.getName();
					if (obj != null) {
						triggerData.put(IAlertSystem.CRON_NAME_FIELD, obj);
					}

					obj = trigger.getGroup();
					if (obj != null) {
						triggerData.put(IAlertSystem.CRON_GROUP_FIELD, obj);
					}

					obj = trigger.getStartTime();
					if (obj != null) {
						triggerData.put(IAlertSystem.STARTTIME_FIELD, obj);
					}

					obj = trigger.getEndTime();
					if (obj != null) {
						triggerData.put(IAlertSystem.ENDTIME_FIELD, obj);
					}

					obj = trigger.getNextFireTime();
					if (obj != null) {
						trigger.getNextFireTime();
					}
					triggerData.put(IAlertSystem.NEXTTIMEFIRE_FIELD, obj);

					int state = this.scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
					triggerData.put(IAlertSystem.STATE_FIELD, this.getStateString(state));

					triggersData.add(triggerData);
				}
			}
		} catch (SchedulerException e) {
			DefaultQuartzManager.logger.error(null, e);
		}

		return triggersData;
	}

	@Override
	public EntityReferenceLocator getReferenceLocator() {
		return this.locator;
	}

	/**
	 * Devuelve el programador en el que están programadas las tareas a ejecutar
	 *
	 * @return
	 */
	public Scheduler getScheduler() {
		return this.scheduler;
	}

	private String getStateString(int state) {

		String stateString = "";

		switch (state) {
			case Trigger.STATE_NORMAL:
				stateString = "STATE_NORMAL";
				break;

			case Trigger.STATE_PAUSED:
				stateString = "STATE_PAUSED";
				break;

			case Trigger.STATE_BLOCKED:
				stateString = "STATE_BLOCKED";
				break;

			case Trigger.STATE_COMPLETE:
				stateString = "STATE_COMPLETE";
				break;

			case Trigger.STATE_ERROR:
				stateString = "STATE_ERROR";
				break;

			case Trigger.STATE_NONE:
				stateString = "STATE_NONE";
				break;
		}

		return stateString;
	}

}
