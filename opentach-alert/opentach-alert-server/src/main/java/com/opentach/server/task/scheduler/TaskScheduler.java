package com.opentach.server.task.scheduler;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ontimize.jee.common.tools.ListTools;
import com.ontimize.jee.common.tools.ReflectionTools;
import com.opentach.common.util.concurrent.PoolExecutors;
import com.opentach.common.util.concurrent.PriorityThreadPoolExecutor;
import com.opentach.server.task.interfaces.ITask;

@EnableScheduling
@Component
public class TaskScheduler {

	/** The CONSTANT logger */
	private static final Logger logger = LoggerFactory.getLogger(TaskScheduler.class);

	/** The executor. */
	private final PriorityThreadPoolExecutor	executor;

	public TaskScheduler() {
		this.executor = PoolExecutors.newPriorityPoolExecutor("TaskSchedulerExecutor", 20, 10, TimeUnit.SECONDS);
	}

	@Scheduled(cron = "0 * * * * *") // For each minute
	protected void checkTasks() {
		TaskScheduler.logger.info("checkTasks");
		Date dispatchDate = new Date();
		try {
			List<ITask> allTasks = this.getAllTasks();
			TaskScheduler.logger.info("\tThere are {} tasks defined.", allTasks.size());

			for (ITask task : allTasks) {
				this.processSingleTaskSafe(task, dispatchDate);
			}
			TaskScheduler.logger.debug("checkTasks...FINISHED");
		} catch (Exception err) {
			TaskScheduler.logger.error("checkTasks...ERROR", err);
		}
	}

	private void processSingleTaskSafe(ITask task, Date dispatchDate) {
		try {
			this.processSingleTask(task, true, dispatchDate);
		} catch (Exception err) {
			// Do nothing, silent because logged into
		}
	}

	public void processSingleTask(ITask task, boolean checkValidExecutionTime) throws TaskException {
		this.processSingleTask(task, checkValidExecutionTime, new Date());
	}

	public void processSingleTask(ITask task, boolean checkValidExecutionTime, Date dispatchDate) throws TaskException {
		try {
			if (!checkValidExecutionTime || task.isValidToExecute(dispatchDate)) {
				TaskScheduler.logger.debug("\tprocessSingleTask: {} SUBMIT", task);
				this.executor.submit(new Runnable() {
					@Override
					public void run() {
						task.execute();
						TaskScheduler.logger.debug("\t\tprocessSingleTask: {}  DONE", task);
					}
				});
			} else {
				TaskScheduler.logger.debug("\tprocessSingleTask: {} IGNORE", task);
			}
		} catch (Exception err) {
			TaskScheduler.logger.error("\tprocessSingleTask: {}  ERROR", task, err);
			throw new TaskException(err);
		}
	}

	private List<ITask> getAllTasks() {
		final List<ITask> tasks = new ArrayList<ITask>();

		try {
			Enumeration<URL> taskFiles = Thread.currentThread().getContextClassLoader().getResources("META-INF/tasks.properties");
			while (taskFiles.hasMoreElements()) {
				tasks.addAll(this.parseTaskFile(taskFiles.nextElement()));
			}
		} catch (Exception err) {
			TaskScheduler.logger.error("E_GETTING_TASK_INSTANCES", err);
		}

		return tasks;
	}

	private Collection<? extends ITask> parseTaskFile(URL taskFile) {
		final List<ITask> tasks = new ArrayList<ITask>();
		try (InputStream stream = taskFile.openStream()) {
			Properties prop = new Properties();
			prop.load(stream);
			List<ITask> collected = prop.keySet().stream().map(s -> {
				try {
					return (ITask) ReflectionTools.newInstance((String) prop.get(s));
				} catch (Exception err) {
					TaskScheduler.logger.error("E_GETTING_TASK_INSTANCE", err);
					return null;
				}
			}).collect(Collectors.toList());
			collected = ListTools.removeDuplicates(collected);
			collected.remove(null);
			tasks.addAll(collected);
		} catch (Exception err) {
			TaskScheduler.logger.error("E_GETTING_TASK_INSTANCES", err);
		}
		return tasks;
	}
}