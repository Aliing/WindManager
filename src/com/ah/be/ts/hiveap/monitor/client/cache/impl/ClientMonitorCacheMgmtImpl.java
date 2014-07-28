package com.ah.be.ts.hiveap.monitor.client.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification.Stage;
import com.ah.be.ts.hiveap.monitor.client.cache.ClientMonitorCache;
import com.ah.be.ts.hiveap.monitor.client.cache.ClientMonitorCacheMgmt;
import com.ah.util.Tracer;

public class ClientMonitorCacheMgmtImpl implements ClientMonitorCacheMgmt<ClientMonitorCache, ClientMonitorNotification> {

	private static final Tracer log = new Tracer(ClientMonitorCacheMgmtImpl.class.getSimpleName());

	private static final String KEY_SEPARATOR = "_";

	/* A map of ClientMonitorNotificationCaches, keyed by a combined string (domainName + KEY_SEPARATOR + clientMac) */
	private final Map<String, ClientMonitorCache> activeCaches;

	public ClientMonitorCacheMgmtImpl() {
		this.activeCaches = Collections.synchronizedMap(new HashMap<String, ClientMonitorCache>(300));
	}

	@Override
	public void add(String domainName, String clientMac, ClientMonitorNotification notification) {
		if (log.getLogger().isDebugEnabled()) {
			log.debug("add", "Adding client monitor notification into cache. Client: " + clientMac + "; vHM: " + domainName + "; HiveAP: " + notification.getHiveApMac());
		}

		String key = getKey(domainName, clientMac);

		synchronized (activeCaches) {
			ClientMonitorCache cache = activeCaches.get(key);

			if (cache == null) {
				cache = new ClientMonitorCache(domainName, clientMac);
				activeCaches.put(key, cache);
			}

			cache.add(notification);

			if (log.getLogger().isDebugEnabled()) {
				log.debug("add", "Client monitor notification was added into cache. Client: " + clientMac + "; vHM: " + domainName + "; HiveAP: " + notification.getHiveApMac());
			}
		}
	}

	@Override
	public List<ClientMonitorNotification> get() {
		List<ClientMonitorNotification> candidate = new ArrayList<ClientMonitorNotification>(300);

		for (ClientMonitorCache cache : activeCaches.values()) {
			List<ClientMonitorNotification> notifications = cache.get();

			if (notifications != null) {
				candidate.addAll(notifications);
			}
		}

		return candidate;
	}

	@Override
	public List<ClientMonitorNotification> get(String domainName) {
		List<ClientMonitorNotification> candidate = new ArrayList<ClientMonitorNotification>(300);

		for (ClientMonitorCache cache : activeCaches.values()) {
			String locatedDomain = cache.getDomainName();

			if (domainName.equals(locatedDomain)) {
				List<ClientMonitorNotification> notifications = cache.get();

				if (notifications != null) {
					candidate.addAll(notifications);
				}
			}
		}

		return candidate;
	}

	@Override
	public List<ClientMonitorNotification> get(String domainName, String clientMac) {
		String key = getKey(domainName, clientMac);
		ClientMonitorCache cache = activeCaches.get(key);

		return cache != null ? cache.get() : new ArrayList<ClientMonitorNotification>(0);
	}

	@Override
	public List<ClientMonitorNotification> get(String domainName, String clientMac, Collection<Stage> includedStages) {
		List<ClientMonitorNotification> candidate;

		if (includedStages != null && !includedStages.isEmpty()) {
			String key = getKey(domainName, clientMac);
			ClientMonitorCache cache = activeCaches.get(key);

			if (cache != null) {
				List<ClientMonitorNotification> notifications = cache.get();

				if (notifications != null) {
					candidate = new ArrayList<ClientMonitorNotification>(notifications.size());

					for (ClientMonitorNotification cmn : notifications) {
						Stage stage = cmn.getStage();

						if (includedStages.contains(stage)) {
							candidate.add(cmn);
						}
					}
				} else {
					candidate = new ArrayList<ClientMonitorNotification>(0);
				}
			} else {
				candidate = new ArrayList<ClientMonitorNotification>(0);
			}			
		} else {
			candidate = new ArrayList<ClientMonitorNotification>(0);
		}

		return candidate;
	}

	@Override
	public List<ClientMonitorNotification> get(String domainName, Collection<String> clientMacs) {
		List<ClientMonitorNotification> candidate = new ArrayList<ClientMonitorNotification>(300);

		for (ClientMonitorCache cache : activeCaches.values()) {
			String locatedDomain = cache.getDomainName();
			String monitoredClient = cache.getClientMac();

			if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
				List<ClientMonitorNotification> notifications = cache.get();

				if (notifications != null) {
					candidate.addAll(notifications);
				}
			}
		}

		return candidate;
	}

	@Override
	public List<ClientMonitorNotification> get(String domainName, Collection<String> clientMacs, Collection<Stage> includedStages) {
		List<ClientMonitorNotification> candidate = new ArrayList<ClientMonitorNotification>(300);

		if (includedStages != null && !includedStages.isEmpty()) {
			for (ClientMonitorCache cache : activeCaches.values()) {
				String locatedDomain = cache.getDomainName();
				String monitoredClient = cache.getClientMac();

				if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
					List<ClientMonitorNotification> notifications = cache.get();

					if (notifications != null) {
						for (ClientMonitorNotification cmn : notifications) {
							Stage stage = cmn.getStage();

							if (includedStages.contains(stage)) {
								candidate.add(cmn);
							}
						}
					}
				}
			}
		}

		return candidate;
	}

	@Override
	public Collection<ClientMonitorCache> remove() {
		log.info("remove", "Removing overall client monitor notification caches.");

		synchronized (activeCaches) {
			Collection<ClientMonitorCache> removedCaches = new ArrayList<ClientMonitorCache>(activeCaches.size());

			for (Iterator<ClientMonitorCache> cacheIter = activeCaches.values().iterator(); cacheIter.hasNext();) {
				ClientMonitorCache cache = cacheIter.next();
				log.info("remove", "Removing client monitor notification cache '" + cache.toString() + "'.");
				cacheIter.remove();
				log.info("remove", "Client monitor notification cache '" + cache.toString() + "' was removed.");
				removedCaches.add(cache);
			}

			return removedCaches;
		}
	}

	@Override
	public Collection<ClientMonitorCache> remove(String domainName)  {
		log.info("remove", "Removing client monitor notification caches from vHM " + domainName);

		synchronized (activeCaches) {
			Collection<ClientMonitorCache> removedCaches = new ArrayList<ClientMonitorCache>(activeCaches.size());

			for (Iterator<ClientMonitorCache> cacheIter = activeCaches.values().iterator(); cacheIter.hasNext();) {
				ClientMonitorCache cache = cacheIter.next();
				String locatedDomain = cache.getDomainName();

				if (domainName.equals(locatedDomain)) {
					log.info("remove", "Removing client monitor notification cache '" + cache.toString() + "'");
					cacheIter.remove();
					log.info("remove", "Client monitor notification cache '" + cache.toString() + "' was removed.");
					removedCaches.add(cache);
				}
			}

			return removedCaches;
		}
	}

	@Override
	public ClientMonitorCache remove(String domainName, String clientMac) {
		log.info("remove", "Removing client monitor notification cache for client " + clientMac + " from vHM " + domainName);
		String key = getKey(domainName, clientMac);

		synchronized (activeCaches) {
			ClientMonitorCache cache = activeCaches.remove(key);

			if (cache != null) {
				log.info("remove", "Client monitor notification cache '" + cache.toString() + "' was removed.");
			}

			return cache;
		}
	}

	@Override
	public Collection<ClientMonitorCache> remove(String domainName, Collection<String> clientMacs)  {
		log.info("remove", "Removing a set of client monitor notification caches from vHM " + domainName);

		synchronized (activeCaches) {
			Collection<ClientMonitorCache> removedCaches = new ArrayList<ClientMonitorCache>(activeCaches.size());

			for (Iterator<ClientMonitorCache> cacheIter = activeCaches.values().iterator(); cacheIter.hasNext();) {
				ClientMonitorCache cache = cacheIter.next();
				String locatedDomain = cache.getDomainName();
				String monitoredClient = cache.getClientMac();

				if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
					log.info("remove", "Removing client monitor notification cache '" + cache.toString() + "'");
					cacheIter.remove();
					log.info("remove", "Client monitor notification cache '" + cache.toString() + "' was removed.");
					removedCaches.add(cache);
				}
			}

			return removedCaches;
		}
	}

	@Override
	public void clear() {
		log.info("clear", "Clearing overall cached client monitor notifications.");

		synchronized (activeCaches) {
			for (String key : activeCaches.keySet()) {
				ClientMonitorCache cache = activeCaches.get(key);

				if (cache != null) {
					log.info("clear", key + " - Clearing cached client monitor notifications.");
					cache.clear();
					log.info("clear", key + " - Cached client monitor notifications was cleared.");
				}
			}
		}
	}

	@Override
	public void clear(String domainName) {
		log.info("clear", "Clearing cached client monitor notifications for vHM " + domainName);

		synchronized (activeCaches) {
			for (String key : activeCaches.keySet()) {
				ClientMonitorCache cache = activeCaches.get(key);

				if (cache != null && domainName.equals(cache.getDomainName())) {
					log.info("clear", key + " - Clearing cached client monitor notifications.");
					cache.clear();
					log.info("clear", key + " - Cached client monitor notifications was cleared.");
				}
			}
		}
	}

	@Override
	public void clear(String domainName, String clientMac) {
		String key = getKey(domainName, clientMac);
		log.info("clear", key + " - Clearing cached client monitor notifications.");

		synchronized (activeCaches) {
			ClientMonitorCache cache = activeCaches.get(key);

			if (cache != null) {
				cache.clear();
				log.info("clear", key + " - Cached client monitor notifications was cleared.");
			}
		}
	}

	@Override
	public void clear(String domainName, Collection<String> clientMacs) {
		log.info("clear", "Clearing a set of cached client monitor notifications for vHM " + domainName);

		synchronized (activeCaches) {
			for (String key : activeCaches.keySet()) {
				ClientMonitorCache cache = activeCaches.get(key);

				if (cache != null && domainName.equals(cache.getDomainName()) && clientMacs.contains(cache.getClientMac())) {
					log.info("clear", key + " - Clearing cached client monitor notifications.");
					cache.clear();
					log.info("clear", key + " - Cached client monitor notifications was cleared.");
				}
			}
		}
	}

	private String getKey(String domainName, String clientMac) {
		return domainName + KEY_SEPARATOR + clientMac;
	}

}