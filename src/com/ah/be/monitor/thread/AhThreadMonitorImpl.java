package com.ah.be.monitor.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AhThreadMonitorImpl implements Runnable, AhThreadMonitoring {

	private static final Tracer log = new Tracer(AhThreadMonitorImpl.class.getSimpleName());

	private final long initialDelay;

	private final long period;

	private final long nanoCycle;

	private final TimeUnit timeUnit;

	private final ThreadMXBean threadMXBean;

	private final List<ThreadCpuUsage> threadListeners;

	private ScheduledExecutorService scheduler;

	private ScheduledFuture<?> scheduledTask;

	public AhThreadMonitorImpl(long initialDelay, long period, TimeUnit timeUnit) {
		this.initialDelay = initialDelay;
		this.period = period;
		this.timeUnit = timeUnit;
		this.nanoCycle = timeUnit.toNanos(period);
		this.threadMXBean = ManagementFactory.getThreadMXBean();
		this.threadListeners = Collections.synchronizedList(new LinkedList<ThreadCpuUsage>());
	}

	public long getInitialDelay() {
		return initialDelay;
	}

	public long getPeriod() {
		return period;
	}

	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	@Override
	public void run() {
		MgrUtil.setTimerName(this.getClass().getSimpleName());
//		System.out.println("Thread CPU Utilization within recent " + period + " " + timeUnit.toString().toLowerCase());
		log.info("run", "Thread CPU Utilization within recent " + period + " " + timeUnit.toString().toLowerCase());
		StringBuilder infoBuf = new StringBuilder();

		for (ThreadCpuUsage tcu : threadListeners) {
			Thread t = tcu.thread;
			long id = t.getId();
			long currentThreadCpuTime = threadMXBean.getThreadCpuTime(id);
			long currentThreadUserTime = threadMXBean.getThreadUserTime(id);
			long totalCpuUtil = (currentThreadCpuTime - tcu.lastThreadCpuTime) * 100 / nanoCycle;
			long userLevelCpuUtil = (currentThreadUserTime - tcu.lastThreadUserTime) * 100 / nanoCycle;

//			System.out.println("threadCpuTime :" + threadCpuTime + "; threadUserTime: " + threadUserTime + "; lastThreadCpuTime: " + tcu.lastThreadUserTime + "; lastThreadUserTime: " + tcu.lastThreadCpuTime);

			infoBuf.append("\n\t--- Thread {ID / Name}: {").append(t.getId()).append(" / ").append(t.getName()).append("}")
				   .append("\n\t------ Total CPU Utilization: ").append(totalCpuUtil).append("%")
				   .append("\n\t--------- User Level CPU Utilization: ").append(userLevelCpuUtil).append("%");

			tcu.lastThreadCpuTime = currentThreadCpuTime;
			tcu.lastThreadUserTime = currentThreadUserTime;
		}

		log.info("run", infoBuf.toString());
//		System.out.println(infoBuf.toString());
	}

	/* (non-Javadoc)
	 * @see com.ah.monitor.thread.AhThreadMonitorable#register(java.lang.Thread)
	 */
	@Override
	public void register(Thread thread) {
		if (thread == null) {
			log.warn("register", "Thread is required.");
			return;
		}

		log.info("register", "Registering new thread {" + thread.getId() + " / " +thread.getName() + "}.");
		boolean isExist = false;

		synchronized (threadListeners) {
			for (ThreadCpuUsage tcu : threadListeners) {
				if (tcu.thread.getId() == thread.getId()) {
					isExist = true;
					break;
				}
			}

			if (isExist) {
				log.warn("register", "Thread {" + thread.getId() + " / " + thread.getName() + "} has already been registered. Ignore.");
			} else {
				threadListeners.add(new ThreadCpuUsage(thread));
				log.info("register", "Thread {" + thread.getId() + " / " + thread.getName() + "} was successfully registered.");
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.monitor.thread.AhThreadMonitorable#deregister(java.lang.Thread)
	 */
	@Override
	public void deregister(Thread thread) {
		if (thread == null) {
			return;
		}

		log.info("deregister", "Deregistering thread {" + thread.getId() + " / " +thread.getName() + "}.");

		deregister(thread.getId());
	}

	/* (non-Javadoc)
	 * @see com.ah.monitor.thread.AhThreadMonitorable#start()
	 */
	@Override
	public void start() {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduledTask = scheduler.scheduleWithFixedDelay(this, initialDelay, period, timeUnit);
		}
	}

	/* (non-Javadoc)
	 * @see com.ah.monitor.thread.AhThreadMonitorable#stop()
	 */
	@Override
	public void stop() {
		threadListeners.clear();

		try {
			shutdownScheduler();
		} catch (InterruptedException ie) {
			log.error("stop", "Failed to shut down thread monitoring scheduler.", ie);
		}
	}

	private void deregister(long threadId) {
		synchronized (threadListeners) {
			for (Iterator<ThreadCpuUsage> iter = threadListeners.iterator(); iter.hasNext();) {
				ThreadCpuUsage tcu = iter.next();
				Thread t = tcu.thread;

				if (t.getId() == threadId) {
					iter.remove();
					log.info("deregister", "Thread {" + t.getId() + " / " + t.getName() + "} was successfully deregistered.");
					break;
				}
			}
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

	class ThreadCpuUsage {
		private final Thread thread;

		private long lastThreadCpuTime;

		private long lastThreadUserTime;

		public ThreadCpuUsage(Thread thread) {
			this.thread = thread;
		}
	}

	/*-
	public static void main(String[] args) {
		Thread t = new Thread() {
			public void run() {
				while (true) {
					for (int i = 0; i < Long.MAX_VALUE; i++) {
						if (i % 5000 == 0) {
							System.err.println(i);
						}
					}

					break;
				}
			}
		};

		t.setName("HiveAP Discovery");
		t.start();

//		Thread t2 = new Thread() {
//			public void run() {
//
//			}
//		};
//
//		t2.setName("HiveAP Config Generation");

		AhThreadMonitoring threadMonitor = new AhThreadMonitorImpl(0L, 1L,TimeUnit.SECONDS);
		threadMonitor.register(t);
//		threadMonitor.register(t2);
		threadMonitor.start();
	}*/

}