package com.ah.be.ts.hiveap.monitor.client.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.util.Tracer;

public class ClientMonitorCache {

	private static final Tracer log = new Tracer(ClientMonitorCache.class.getSimpleName());

	private static final int DEFAULT_MAX_CACHE_SIZE;

	static {
		String propValue = System.getProperty("client.monitor.log.cache.size");
		int cacheSize = 500;

		if (propValue != null) {
			try {
				cacheSize = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				log.warn("init",
						"Client monitor log cache size parsing failed, using "
								+ cacheSize + " instead.", nfe);
			}
		}

		DEFAULT_MAX_CACHE_SIZE = cacheSize;
	}

	private final String domainName;

	private final String clientMac;

	private final List<ClientMonitorNotification> cache;

	private int maxCacheSize = DEFAULT_MAX_CACHE_SIZE;

	public ClientMonitorCache(String domainName, String clientMac) {
		this.domainName = domainName;
		this.clientMac = clientMac;
		this.cache = Collections.synchronizedList(new ArrayList<ClientMonitorNotification>(maxCacheSize));
	}

	public String getDomainName() {
		return domainName;
	}

	public String getClientMac() {
		return clientMac;
	}

	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	public void setMaxCacheSize(int cacheSize) {
		if (cacheSize <= 0) {
			log.warn("setMaxCacheSize", "Illegal cache size " + cacheSize + ". Ignoring.");
			return;
		}

		log.info("setMaxCacheSize", "Setting max client monitor notification cache size to " + cacheSize);
		this.maxCacheSize = cacheSize;
		int removedNum = cache.size() - cacheSize;

		if (removedNum > 0) {
			log.info("setMaxCacheSize", "Removing top of " + removedNum + " items from client monitor notification cache.");

			synchronized (cache) {
				for (int i = 0; i < removedNum; i++) {
					cache.remove(0);
				}
			}
		}
	}

	public void add(ClientMonitorNotification notification) {
		synchronized (cache) {
			int removedNum = cache.size() - maxCacheSize + 1;

			for (int i = 0; i < removedNum; i++) {
				cache.remove(0);
			}

			cache.add(notification);
		}
	}

	public List<ClientMonitorNotification> get() {
		return cache;
	}

	public void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}

	@Override
	public String toString() {
		return domainName + "_" + clientMac + "_" + maxCacheSize;
	}

}