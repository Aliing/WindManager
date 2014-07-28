package com.ah.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ah.test.HiveProfilesTest;
import com.ah.test.SsidProfileTest;
import com.ah.util.HibernateUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/*
 * @author Chris Scheers
 */
public class RunTest {
	private static final Tracer log = new Tracer(RunTest.class.getSimpleName());

	public static long runTaskInThreads(Executor executor, int nThreads,
			final Runnable task) throws InterruptedException {
		final CountDownLatch startGate = new CountDownLatch(1);
		final CountDownLatch endGate = new CountDownLatch(nThreads);
		for (int i = 0; i < nThreads; i++) {
			Runnable t = new Runnable() {
				public void run() {
					MgrUtil.setTimerName(this.getClass().getSimpleName());
					try {
						startGate.await();
						try {
							task.run();
						} finally {
							endGate.countDown();
						}
					} catch (InterruptedException e) {
						// ignored
					}
				}
			};
			executor.execute(t);
		}
		long start = System.currentTimeMillis();
		startGate.countDown();
		endGate.await();
		long end = System.currentTimeMillis();
		return end - start;
	}

	public static long runTest(int nThreads, HmTest hmTest) {
		ExecutorService executorService = Executors
				.newFixedThreadPool(nThreads);
		try {
			long miliTime = runTaskInThreads(executorService, nThreads, hmTest);
			log.error("main", "Test timing for " + nThreads + " threads: "
					+ miliTime);
			return miliTime;
		} catch (Exception e) {
			log.error("main", "Exception: ", e);
			return 0;
		} finally {
			log.error("main", "Shutting down executor.");
			executorService.shutdown();
		}
	}

	public static void main(String[] args) {
		log.info("main", "Entered RunTest.main");
		int nThreads = 3;
		log.info("main", "# arguments: " + args.length);
		MgrUtil.log(log, "Args: ", args);
		if (!args[0].startsWith("$")) {
			nThreads = Integer.parseInt(args[0]);
		}
		log.info("main", "# threads: " + nThreads);
		final HiveProfilesTest hiveProfilesTest = new HiveProfilesTest(
				"testCreate", nThreads, 3);
		runTest(nThreads, hiveProfilesTest);

		final SsidProfileTest ssidProfileTest = new SsidProfileTest();
		runTest(nThreads, ssidProfileTest);

		log.info("main", "Closing entity manager.");
		HibernateUtil.close();

		log.info("main", "Leaving RunTest.main");
	}
}
