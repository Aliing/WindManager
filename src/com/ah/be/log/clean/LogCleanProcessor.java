package com.ah.be.log.clean;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class LogCleanProcessor implements Runnable {

	private static final Tracer log = new Tracer(LogCleanProcessor.class.getSimpleName());

	/* Used to hold overall log cleaning tasks to be executed */
	private final Map<String, LogCleanTaskable> logCleanTasks;

	/* Automatic log cleaning scheduler */
	private final ScheduledExecutorService scheduler;

	/* Automatic log cleaning scheduled task */
	private ScheduledFuture<?> scheduledTask;

	public LogCleanProcessor() {
		logCleanTasks = Collections.synchronizedMap(new HashMap<String, LogCleanTaskable>());
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}
	
	@Override
	public void run() {
		// Execute log cleaning tasks one by one periodically.
		log.info("Executing log cleaning tasks.");
		MgrUtil.setTimerName(this.getClass().getSimpleName());

		for (LogCleanTaskable logCleanTask : logCleanTasks.values()) {
			try {
				long startTime = System.currentTimeMillis();
				boolean result = logCleanTask.clean();
				long endTime = System.currentTimeMillis();
				log.info("run", "It took " + (endTime - startTime) + "ms to execute a " + logCleanTask.getTaskName().toLowerCase() + ". Execution Result: " + (result ? "Success" : "Fail"));
			} catch (Exception e) {
				log.error("run", "Error occurred while executing " + logCleanTask.getTaskName().toLowerCase());
			}
		}
	}

	public synchronized void start(long initialDelay, long period, TimeUnit unit) {
		log.info("start", "Starting log cleaning processor...");
		scheduledTask = scheduler.scheduleWithFixedDelay(this, initialDelay, period, unit);
		log.info("start", "Log cleaning processor was started.");
	}

	public synchronized void stop() {
		log.info("stop", "Stopping log cleaning processor...");

		try {
			// Shutdown scheduler.
			shutdownScheduler();
			log.info("stop", "Log cleaning processor was stopped.");
		} catch (InterruptedException ie) {
			log.error("stop", "Interrupt occurred while stopping cleaning processor.", ie);
		}
	}

	public boolean addTask(LogCleanTaskable task) {
		boolean taskAdded = false;

		if (task != null && task.getTaskName() != null && !task.getTaskName().trim().isEmpty()) {
			synchronized (logCleanTasks) {
				logCleanTasks.put(task.getTaskName().toLowerCase(), task);
			}

			log.info("addTask", "Added " + task.getTaskName().toLowerCase() + " to log cleaning processor.");
			taskAdded = true;
		}

		return taskAdded;
	}

	public LogCleanTaskable removeTask(String taskName) {
		if (taskName == null || taskName.trim().isEmpty()) {
			return null;
		}

		synchronized (logCleanTasks) {
			return logCleanTasks.remove(taskName.toLowerCase());
		}
	}

	private void shutdownScheduler() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Cancel scheduled task.
		if (scheduledTask != null) {
			scheduledTask.cancel(false);
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(3L, TimeUnit.SECONDS)) {
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being canceled.
		if (!scheduler.awaitTermination(3L, TimeUnit.SECONDS)) {
			log.warning("shutdownScheduler", "Scheduler was not terminated completely.");
		}
	}

}