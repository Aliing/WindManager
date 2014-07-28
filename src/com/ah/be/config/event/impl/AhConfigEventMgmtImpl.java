package com.ah.be.config.event.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.ah.be.app.AhAppContainer;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.event.AhEventMgmt;
import com.ah.be.event.AhShutdownEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.util.Tracer;

public final class AhConfigEventMgmtImpl implements AhEventMgmt<BeBaseEvent> {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			AhConfigEventMgmtImpl.class.getSimpleName());

	private static final String CONFIG_GENERATOR_NAME_PREFIX = "Config Generator";

	protected final BlockingQueue<BeBaseEvent> eventQueue;

	// Performance statistics utility
	protected final CurrentLoadCache perfStatUtil = CurrentLoadCache
			.getInstance();

	private final Collection<ConfigGenerator> configGenerators;
	
	private ConfigThreadProtectTimer protectTimer;

	private final AtomicInteger lostEventCount;

	private boolean stopping;

	public AhConfigEventMgmtImpl() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(5000);
		configGenerators = Collections
				.synchronizedList(new ArrayList<ConfigGenerator>(
						HMServicesSettings.MAX_CONCURRENT_CONFIG_GEN_NUM));
		lostEventCount = new AtomicInteger(0);
	}

	@Override
	public void start() {
		start(HMServicesSettings.DEFAULT_CONCURRENT_CONFIG_GEN_NUM);
	}

	public synchronized void start(int generatorNum) {
		stopping = false;
		addGenerators(generatorNum);
		log.info("start", "Started number of " + generatorNum
				+ " config generators.");
		
		protectTimer = new ConfigThreadProtectTimer(this);
		protectTimer.start();
	}

	@Override
	public boolean isStarted() {
		return !configGenerators.isEmpty();
	}

	@Override
	public synchronized void stop() {
		log.info("stop", "Closing overall config generators.");
		
		//stop protect timer
		protectTimer.stop();

		// Clear queue before adding shutdown events.
		eventQueue.clear();
		addShutdownEvents(configGenerators.size());
		stopping = true;

		log.info("stop", "Events lost: " + lostEventCount.intValue());
	}

	/*
	 * Add config generated event. BlockingQueue by itself is thread safe, but
	 * in case offer() fails, we want to be able to remove the head of the queue
	 * and re-try inserting the new event in the queue. Therefore the
	 * synchronization.
	 */
	@Override
	public synchronized void add(BeBaseEvent event) {
		if (!eventQueue.offer(event)) {
			lostEventCount.incrementAndGet();
			log.warn("add",
					"Config queue was full, " + lostEventCount.intValue()
							+ " events lost.");

			// New event is more important, so remove the head of queue in order
			// to add new event into the FIFO queue.
			BeBaseEvent lostEvent = eventQueue.poll();

			if (lostEvent != null) {
				log.warn("add", "Discard a config generation event.");

				// Remove an old config generation request.
				perfStatUtil.decreaseNumberOfConfigRequest();
			}

			if (!eventQueue.offer(event)) {
				log.warn("add",
						"Config queue was full even after removing the head of queue.");
			} else {
				// Add a new config generation request.
				perfStatUtil.increaseNumberOfConfigRequest();
			}
		} else {
			// Add a new config generation request.
			perfStatUtil.increaseNumberOfConfigRequest();
		}
	}

	@Override
	public void notify(BeBaseEvent event) {
		AhAppContainer.getBeEventListener().eventGenerated(event);
	}

	@Override
	public int getEventQueueSize() {
		return eventQueue.size();
	}

	@Override
	public Thread[] getEventProcessThreads() {
		return configGenerators.toArray(new Thread[configGenerators.size()]);
	}

	public synchronized void adjustConfigGeneratorNumber(int newNum) {
		if (stopping) {
			// It is useless to throw an exception here since the HM service is
			// being shut down at this period of time.
			log.warn("The config generation process is being or has been shut down, new number adjustment for concurrent config generation was ignored.");
			return;
		}

		if (newNum < HMServicesSettings.MIN_CONCURRENT_CONFIG_GEN_NUM
				|| newNum > HMServicesSettings.MAX_CONCURRENT_CONFIG_GEN_NUM) {
			throw new IllegalArgumentException("The provided number '" + newNum
					+ "' was invalid. It must be >= "
					+ HMServicesSettings.MIN_CONCURRENT_CONFIG_GEN_NUM
					+ " & <= "
					+ HMServicesSettings.MAX_CONCURRENT_CONFIG_GEN_NUM);
		}

		// Make sure there is enough time to remove the generators being closed
		// from the generator holder.
		try {
			Thread.sleep(500L);
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}

		int pendingClosedNum = 0;

		for (BeBaseEvent event : eventQueue) {
			if (event.getEventType() == BeEventConst.AH_SHUTDOWN_EVENT) {
				pendingClosedNum++;
			}
		}

		int existingNum = configGenerators.size();
		int actualNum = existingNum - pendingClosedNum;
		int diffNum = newNum - actualNum;

		if (diffNum > 0) { // Add 'diffNum' new config generators.
			log.info("adjustConfigGeneratorNumber", "Attempting to add "
					+ diffNum + " new config "
					+ (diffNum > 1 ? "generators" : "generator") + ".");
			addGenerators(diffNum);
		} else if (diffNum == 0) { // Nothing needs to do.
			log.warn(
					"adjustConfigGeneratorNumber",
					"The new config generator number was equal to the existing number. No need to adjust.");
		} else { // Close ('diffNum' * -1) existing config generators.
			int extraClosedNum = diffNum * -1;
			log.info("adjustConfigGeneratorNumber", "Attempting to close "
					+ extraClosedNum + " existing config "
					+ (extraClosedNum > 1 ? "generators" : "generator") + ".");
			addShutdownEvents(extraClosedNum);
		}
	}

	protected boolean removeGenerator(ConfigGenerator generator) {
		boolean removed;

		synchronized (configGenerators) {
			removed = configGenerators.remove(generator);

			if (removed) {
				log.info(
						"removeGenerator",
						"Removed an existing config generator - "
								+ generator.getName());
			} else {
				log.info("removeGenerator",
						"The config generator " + generator.getName()
								+ " might have been removed.");
			}

			log.info("removeGenerator",
					"The current number of config generators is "
							+ configGenerators.size());
		}

		return removed;
	}

	private void addGenerators(int generatorNum) {
		synchronized (configGenerators) {
			for (int i = 0; i < generatorNum; i++) {
				ConfigGenerator generator = new ConfigGenerator(this);
				String generatorName = CONFIG_GENERATOR_NAME_PREFIX + "<"
						+ generator.getId() + ">";
				generator.setName(generatorName);
				generator.start();
				configGenerators.add(generator);
				log.info(
						"addGenerators",
						"Added & started a new config generator - "
								+ generator.getName());
			}

			log.info("addGenerators",
					"The current number of config generators is "
							+ configGenerators.size());
		}
	}

	private void addShutdownEvents(int eventNum) {
		BeBaseEvent shutdownEvent = new AhShutdownEvent();

		for (int i = 0; i < eventNum; i++) {
			add(shutdownEvent);
		}
	}
	
	public static class ConfigThreadProtectTimer implements Runnable {
		
		private AhConfigEventMgmtImpl configImpl;
		
		private ScheduledExecutorService timer;
		public static final int TIMER_INTERVAL = 1000 * 60;
		
		public ConfigThreadProtectTimer(AhConfigEventMgmtImpl configImpl){
			this.configImpl = configImpl;
		}

		public void start() {
			if (timer == null || timer.isShutdown()) {
				timer = Executors.newSingleThreadScheduledExecutor();
				timer.scheduleWithFixedDelay(this, UpdateParameters.TIMER_DELAY,
						TIMER_INTERVAL, TimeUnit.MILLISECONDS);
			}
		}

		public void stop() {
			if (timer != null && !timer.isShutdown()) {
				timer.shutdown();
			}
		}

		public boolean isStart() {
			return timer != null && !timer.isShutdown();
		}

		@Override
		public void run() {
			try{
				Collection<ConfigGenerator> threads = configImpl.configGenerators;
				for(ConfigGenerator thd : threads){
					if(!thd.isAlive()){
						log.info("ConfigThreadProtectTimer",
								"The thread of config generation has been shut down by some unknown cause, restart it.");
						thd.start();
					}
				}
			}catch(Throwable t){
				log.error("ConfigThreadProtectTimer error.", t);
			}
		}
		
	}

}