package com.ah.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * @author fisher
 */
public class LinuxSystemInfoCollector implements Runnable {

	private static final Tracer log = new Tracer(LinuxSystemInfoCollector.class.getSimpleName());

	private long[] memoryInfos = new long[6];
	private float cpuUsage = 0;
	private long preuser = 0;
	private long prenice = 0;
	private long presys = 0;
	private long preidle = 0;
	private ScheduledExecutorService scheduler;

	private static LinuxSystemInfoCollector instance;

	private LinuxSystemInfoCollector() {
	}

	public synchronized static LinuxSystemInfoCollector getInstance() {
		if (instance == null) {
			instance = new LinuxSystemInfoCollector();
		}

		return instance;
	}
	
	public void start() {
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(this, 60, 5L, TimeUnit.SECONDS);
			log.info("start", "LinuxSystemInfoCollector started.");
		}
	}
	
	public long[] getMemInfo() {
		return memoryInfos;
	}
	
	public float getCpuInfo() {
		return cpuUsage;
	}

	@Override
	public void run() {
		try {
			memoryInfos = getMemReadInfo();
			cpuUsage = getCpuReadInfo();
		} catch (Exception e) {
			log.error("run", "LinuxSystemInfoCollector scheduler running error!!!");
		}
	}
	
	public void shutdown() throws InterruptedException {
		if (scheduler == null || scheduler.isShutdown()) {
			return;
		}

		// Disable new tasks from being submitted.
		scheduler.shutdown();

		// Wait a while for existing tasks to terminate.
		if (scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			return;
		}

		// Cancel currently executing tasks.
		scheduler.shutdownNow();

		// Wait a while for tasks to respond to being cancelled.
		if (!scheduler.awaitTermination(1L, TimeUnit.SECONDS)) {
			log.warn("shutdown", "LinuxSystemInfoCollector task was not terminated completely.");
		} else {
			log.info("shutdown", "LinuxSystemInfoCollector shutdown.");
		}
	}
	
	/**
	 * get memory by used info
	 * 
	 * @return int[] result
	 *         result.length==4;int[0]=MemTotal;int[1]=MemFree;int[2]=SwapTotal;int[3]=SwapFree;
	 * @throws IOException -
	 */
	public long[] getMemReadInfo() throws IOException {
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		long[] result = new long[6];

		try {
			File file = new File("/proc/meminfo");
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			String str;

			while ((str = br.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(str);
				if (!token.hasMoreTokens()) {
					continue;
				}
	
				str = token.nextToken();
				if (!token.hasMoreTokens()) {
					continue;
				}
	
				if (str.equalsIgnoreCase("MemTotal:")) {
					result[0] = Long.parseLong(token.nextToken());
				} else if (str.equalsIgnoreCase("MemFree:")) {
					result[1] = Long.parseLong(token.nextToken());
				} else if (str.equalsIgnoreCase("Buffers:")) {
					result[2] = Long.parseLong(token.nextToken());
				} else if (str.equalsIgnoreCase("Cached:")) {
					result[3] = Long.parseLong(token.nextToken());
				} else if (str.equalsIgnoreCase("SwapTotal:")) {
					result[4] = Long.parseLong(token.nextToken());
				} else if (str.equalsIgnoreCase("SwapFree:")) {
					result[5] = Long.parseLong(token.nextToken());
				}
			}
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}				
			}
		
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}				
			}
		}

		return result;
	}

	/**
	 * get memory by used info
	 * 
	 * @return float efficiency
	 * @throws IOException -
	 */
	public float getCpuReadInfo() throws IOException {
		long user1 = 0;
		long nice1 = 0;
		long sys1 = 0;
		long idle1 = 0;

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;

		try {
			File file = new File("/proc/stat");
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			StringTokenizer token = new StringTokenizer(br.readLine());
			token.nextToken();
			user1 = Long.parseLong(token.nextToken());
			nice1 = Long.parseLong(token.nextToken());
			sys1 = Long.parseLong(token.nextToken());
			idle1 = Long.parseLong(token.nextToken());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}				
			}
		
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}				
			}
		}
		if (preuser + prenice + presys + preidle ==0) {
			preuser = user1;
			prenice = nice1;
			presys = sys1;
			preidle = idle1;
			return 0;
		}
		if ((user1 + nice1 + sys1 + idle1) -(preuser + prenice + presys + preidle) == 0) {
			preuser = user1;
			prenice = nice1;
			presys = sys1;
			preidle = idle1;
			return 0;
		} else {
			float retValue = (float) ((user1 + sys1 + nice1) - (preuser + presys + prenice))
			/ (float) ((user1 + nice1 + sys1 + idle1) - (preuser + prenice + presys + preidle));
			
			preuser = user1;
			prenice = nice1;
			presys = sys1;
			preidle = idle1;
			
			return retValue;
		}
	}

}