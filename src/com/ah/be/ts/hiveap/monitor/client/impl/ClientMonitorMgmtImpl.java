package com.ah.be.ts.hiveap.monitor.client.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.os.FileManager;
import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugState;
import com.ah.be.ts.hiveap.DebugState.State;
import com.ah.be.ts.hiveap.impl.HiveApDebugMgmtImpl;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitor;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorFilterParams;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorMgmt;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortImpl;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortParams;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorFilterParams.LogLevel;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification.Stage;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortParams.SortType;
import com.ah.be.ts.hiveap.monitor.client.cache.ClientMonitorCache;
import com.ah.be.ts.hiveap.monitor.client.cache.ClientMonitorCacheMgmt;
import com.ah.be.ts.hiveap.monitor.client.cache.impl.ClientMonitorCacheMgmtImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.compress.tar.AhTar;

public class ClientMonitorMgmtImpl implements ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> {

	private static final Tracer log = new Tracer(ClientMonitorMgmtImpl.class.getSimpleName());

	public static final String CLIENT_LOG_NAME_PREFIX = "CM_";

	public static final String CLIENT_LOG_NAME_SUFFIX = ".log";

	public static final String CLIENT_LOG_ARCHIVE_DIR_NAME_PREFIX = "CM";

	private final HiveApDebugMgmtImpl hiveApDebugMgmt;

	/* Request holder */
	private final Map<ClientMonitor, List<ClientMonitorNotification>> reqsHolder;

	private final ClientMonitorCacheMgmt<ClientMonitorCache, ClientMonitorNotification> cacheMgmt;

	private static final int DEFAULT_MAX_LOG_NUM;

	private static final int DEFAULT_MAX_LOG_LINE;

	private static final int DEFAULT_MAX_WRITE_LINE;

	private static final int DEFAULT_MAX_MONITOR_CLIENTS;

	private static final int DEFAULT_MAX_LOG_SIZE;

	static {
		String propValue = System.getProperty("client.monitor.max.log.num");
		int logNum = 5;

		if (propValue != null) {
			try {
				logNum = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				log.warn("init",
						"Client monitor max log number parsing failed, using "
								+ logNum + " instead.", nfe);
			}
		}

		DEFAULT_MAX_LOG_NUM = logNum;

		propValue = System.getProperty("client.monitor.max.log.line");
		int logLine = 10000;

		if (propValue != null) {
			try {
				logLine = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				log.warn("init",
						"Client monitor max log line parsing failed, using "
								+ logLine + " instead.", nfe);
			}
		}

		DEFAULT_MAX_LOG_LINE = logLine;

		propValue = System.getProperty("client.monitor.max.log.size");
		int logSize = 1000000;

		if (propValue != null) {
			try {
				logSize = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				log.warn("init",
						"Client monitor max log size parsing failed, using "
								+ logSize + " instead.", nfe);
			}
		}

		DEFAULT_MAX_LOG_SIZE = logSize;

		propValue = System.getProperty("client.monitor.max.write.line");
		int writeLine = 500;

		if (propValue != null) {
			try {
				writeLine = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				log.warn("init",
						"Client monitor max write line parsing failed, using "
								+ writeLine + " instead.", nfe);
			}
		}

		DEFAULT_MAX_WRITE_LINE = writeLine;

		propValue = System.getProperty("client.monitor.max.client.num");
		int monitorClients = 100;

		if (propValue != null) {
			try {
				monitorClients = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				log.warn("init",
						"Client monitor max monitor clients parsing failed, using "
								+ monitorClients + " instead.", nfe);
			}
		}

		DEFAULT_MAX_MONITOR_CLIENTS = monitorClients;
	}

	/* Maximum number of log files for one client */
	private int maxLogNum = DEFAULT_MAX_LOG_NUM;

	/* Maximum number of lines can be saved in a log */
	private int maxLogLine = DEFAULT_MAX_LOG_LINE;

	/* Maximum number of bytes for per log */
	private int maxLogSize = DEFAULT_MAX_LOG_SIZE;

	/* Maximum number of lines to write into log at one time */
	private int maxWriteLine = DEFAULT_MAX_WRITE_LINE;

	/* Maximum number of clients can be monitored by the whole HM */
	private int maxClients = DEFAULT_MAX_MONITOR_CLIENTS;

	public ClientMonitorMgmtImpl(HiveApDebugMgmtImpl hiveApDebugMgmt) {
		reqsHolder = Collections.synchronizedMap(new LinkedHashMap<ClientMonitor, List<ClientMonitorNotification>>(5000));
		this.hiveApDebugMgmt = hiveApDebugMgmt;
		this.cacheMgmt = new ClientMonitorCacheMgmtImpl();
	}

	public int getMaxLogNum() {
		return maxLogNum;
	}

	public void setMaxLogNum(int maxLogNum) {
		this.maxLogNum = maxLogNum;
	}

	public int getMaxLogLine() {
		return maxLogLine;
	}

	public void setMaxLogLine(int maxLogLine) {
		if (maxLogLine > 0) {
			this.maxLogLine = maxLogLine;
		}
	}

	public int getMaxLogSize() {
		return maxLogSize;
	}

	public void setMaxLogSize(int maxLogSize) {
		this.maxLogSize = maxLogSize;
	}

	public int getMaxWriteLine() {
		return maxWriteLine;
	}

	public void setMaxWriteLine(int maxWriteLine) {
		if (maxWriteLine > 0 && maxWriteLine < maxLogLine) {
			this.maxWriteLine = maxWriteLine;
		}		
	}

	@Override
	public int getMaxClients() {
		return maxClients;
	}

	@Override
	public void setMaxClients(int maxClients) {
		this.maxClients = maxClients;
	}

	@Override
	public ClientMonitor addRequest(ClientMonitor newRequest) {
		if (newRequest == null) {
			throw new IllegalArgumentException("Invalid argument - " + newRequest);
		}

		HiveAp hiveAp = newRequest.getHiveAp();

		if (hiveAp == null) {
			throw new IllegalArgumentException("Invalid " + NmsUtil.getOEMCustomer().getAccessPonitName() + " - " + hiveAp);
		}

		String clientMac = newRequest.getClientMac();

		if (clientMac == null || clientMac.trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid client - " + clientMac);
		}

		synchronized (reqsHolder) {
			ClientMonitor candidate = null;

			for (ClientMonitor request : reqsHolder.keySet()) {
				if (clientMac.equals(request.getClientMac()) && hiveAp.getMacAddress().equals(request.getHiveApMac())) {
					if (log.getLogger().isDebugEnabled()) {
						log.debug("addRequest", "The client monitor process '" + request + "' has already existed." );
					}

					candidate = request;
					break;
				}
			}

			if (candidate == null) {			
				List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);
				reqsHolder.put(newRequest, notifications);
				candidate = newRequest;
				log.info("addRequest", "Added a new client monitor process - " + newRequest + ". The current number of client monitor processes: " + reqsHolder.size());
			}

			boolean autoInit = requireAutoInit(candidate);

			if (autoInit) {
				initiate(candidate);
			}

			return candidate;
		}
	}

	@Override
	public boolean addNotification(ClientMonitorNotification notification) {
        if (notification == null) {
            log.warn("addNotification", "Invalid argument - " + notification);
            return false;
        }
        
        int cookieId = notification.getCookieId();
        
        if (log.getLogger().isDebugEnabled()) {
            log.debug("addNotification", "Received a new client monitor notification - " + notification);
        }
        
        
        synchronized (reqsHolder) {
            for (ClientMonitor request : reqsHolder.keySet()) {
                        
                if (request.getCookieId() == cookieId) {
                    if (request.getHiveApMac().equalsIgnoreCase(notification.getHiveApMac())) {
                        if (!State.STOPPED.equals(request.getDebugState().getState())) {
                            HiveAp hiveAp = request.getHiveAp();
                            
                            // Set HiveAp object for sorting.
                            notification.setHiveAp(hiveAp);
                            
                            if (notification.isInterestEvent()) {
                                ClientMonitorNotification recentInterestEvent = request.getRecentInterestEvent();
                                
                                if (recentInterestEvent == null || (notification.getTimstamp() > recentInterestEvent.getTimstamp() || (notification.getTimstamp() == recentInterestEvent.getTimstamp() && notification.getMsgSeqNum() > recentInterestEvent.getMsgSeqNum()))) {
                                    // Update activity status and recent event of interest.
                                    updateActivityStatus(notification);
                                }
                            } else if (request.isFilteringProbeEvents()) {
                                if (log.getLogger().isDebugEnabled()) {
                                    log.debug("addNotification", "Dropped an uninterested client monitor notification - " + notification);
                                }
                                
                                return false;
                            }
                            
                            // Add into notification list.
                            List<ClientMonitorNotification> notifications = reqsHolder.get(request);
                            notifications.add(notification);
                            
                            // Sort by default.
                            //  Collections.sort(notifications);
                            
                            String domainName = hiveAp.getOwner().getDomainName();
                            String clientMac = request.getClientMac();
                            List<ClientMonitorNotification> cmns = getNotifications(domainName, clientMac);
                            
                            // Write log if reaching a certain amount of size.
                            if (cmns.size() >= maxWriteLine) {
                                try {
                                    // Write notifications into log file.
                                    writeLog(domainName, clientMac, cmns);
                                    
                                    // Clear the notifications to be written into log file.
                                    clearNotifications(domainName, clientMac);
                                } catch (Exception e) {
                                    log.error("addNotification", "Write client log failed.", e);
                                }
                            }
                            
                            // Add into cache for display.
                            cacheMgmt.add(domainName, clientMac, notification);
                            
                            if (log.getLogger().isDebugEnabled()) {
                                log.debug("addNotification", "Added a new client monitor notification - " + notification);
                            }
                            
                            return true;
                        } else {
                            log.warn("addNotification", notification + " - The client monitor process corresponding to this notification had been stopped. Dropped this notification and attempted to terminate request - " + request);
                            break;
                        }
                    } else {
                        log.warn("addNotification", "The cookie " + cookieId + " for the client monitor notification " + notification + " is being used by another request " + request + ". ***** Why did this situation take place? *****");
                    }
                }
            }
        }

        // Try to terminate an in existent debug relative to the debug notification supplied.
        // log.warn("addNotification", "The HiveAP debug request with cookie " + cookieId + " doesn't exist. Trying to notify HiveAP " + notification.getHiveApMac() + " terminating reporting " + notification.getCategory() + " notifications with this cookie.");
        
        try {
            hiveApDebugMgmt.terminatePseudoRequest(notification);
            
            // Remove all of cached notification events holding the same cookie to protect them from being handled in any places.
            hiveApDebugMgmt.getNotificationProcessor().removeEvents(cookieId);
        } catch (Exception e) {
            log.error("addNotification", "Failed to terminate a pseudo client monitor process - " + notification, e);
        }
        
        return false;
    }
	
	@Override
	public boolean addNotification(ClientMonitorNotification notification, boolean performance) {
	    if (notification == null) {
	        log.warn("addNotification", "Invalid argument - " + notification);
	        return false;
	    }
	    
	    int cookieId = notification.getCookieId();
	    
	    if (log.getLogger().isDebugEnabled()) {
	        log.debug("addNotification", "Received a new client monitor notification - " + notification);
	    }
	    
	    
	    synchronized (reqsHolder) {
	        for (ClientMonitor request : reqsHolder.keySet()) {
	            
	            // filter the Client Mac Address for the performance will response the different clients information to the same cookie
                boolean flag = performance ? request.getClientMac().equalsIgnoreCase(
                        notification.getClientMac()) : true;
                        
	            if (request.getCookieId() == cookieId && flag) {
	                if (request.getHiveApMac().equalsIgnoreCase(notification.getHiveApMac())) {
	                    if (!State.STOPPED.equals(request.getDebugState().getState())) {
	                        HiveAp hiveAp = request.getHiveAp();
	                        
	                        // Set HiveAp object for sorting.
	                        notification.setHiveAp(hiveAp);
	                        
	                        if (notification.isInterestEvent()) {
	                            ClientMonitorNotification recentInterestEvent = request.getRecentInterestEvent();
	                            
	                            if (recentInterestEvent == null || (notification.getTimstamp() > recentInterestEvent.getTimstamp() || (notification.getTimstamp() == recentInterestEvent.getTimstamp() && notification.getMsgSeqNum() > recentInterestEvent.getMsgSeqNum()))) {
	                                // Update activity status and recent event of interest.
	                                updateActivityStatus(notification);
	                            }
	                        } else if (request.isFilteringProbeEvents()) {
	                            if (log.getLogger().isDebugEnabled()) {
	                                log.debug("addNotification", "Dropped an uninterested client monitor notification - " + notification);
	                            }
	                            
	                            return false;
	                        }
	                        
	                        // Add into notification list.
	                        List<ClientMonitorNotification> notifications = reqsHolder.get(request);
	                        notifications.add(notification);
	                        
	                        // Sort by default.
	                        //	Collections.sort(notifications);
	                        
	                        String domainName = hiveAp.getOwner().getDomainName();
	                        String clientMac = request.getClientMac();
	                        List<ClientMonitorNotification> cmns = getNotifications(domainName, clientMac);
	                        
	                        // Write log if reaching a certain amount of size.
	                        if (cmns.size() >= maxWriteLine) {
	                            try {
	                                // Write notifications into log file.
	                                writeLog(domainName, clientMac, cmns);
	                                
	                                // Clear the notifications to be written into log file.
	                                clearNotifications(domainName, clientMac);
	                            } catch (Exception e) {
	                                log.error("addNotification", "Write client log failed.", e);
	                            }
	                        }
	                        
	                        // Add into cache for display.
	                        cacheMgmt.add(domainName, clientMac, notification);
	                        
	                        if (log.getLogger().isDebugEnabled()) {
	                            log.debug("addNotification", "Added a new client monitor notification - " + notification);
	                        }
	                        
	                        return true;
	                    } else {
	                        log.warn("addNotification", notification + " - The client monitor process corresponding to this notification had been stopped. Dropped this notification and attempted to terminate request - " + request);
	                        break;
	                    }
	                } else {
	                    log.warn("addNotification", "The cookie " + cookieId + " for the client monitor notification " + notification + " is being used by another request " + request + ". ***** Why did this situation take place? *****");
	                }
	            }
	        }
	    }

	    return false;
	}

	@Override
	public Collection<ClientMonitor> getRequests() {
		return new ArrayList<ClientMonitor>(reqsHolder.keySet());
	}

	@Override
	public Collection<ClientMonitor> getRequests(String domainName) {
		Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();

			if (domainName.equals(locatedDomain)) {
				requests.add(request);
			}
		}

		return requests;
	}

	@Override
	public Collection<ClientMonitor> getRequests(String domainName, String clientMac) {
		Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();
			String monitoredClient = request.getClientMac();

			if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
				requests.add(request);
			}
		}

		return requests;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications() {
		List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

		for (ClientMonitor request : reqsHolder.keySet()) {
			notifications.addAll(reqsHolder.get(request));
		}

		return notifications;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(boolean caching) {
		return caching ? cacheMgmt.get() : getNotifications();
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName) {
		List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();

			if (domainName.equals(locatedDomain)) {
				notifications.addAll(reqsHolder.get(request));
			}
		}

		return notifications;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, boolean caching) {
		return caching ? cacheMgmt.get(domainName) : getNotifications(domainName);
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, String clientMac) {
		List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();
			String monitoredClient = request.getClientMac();

			if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
				notifications.addAll(reqsHolder.get(request));
			}
		}

		return notifications;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, String clientMac, boolean caching) {
		return caching ? cacheMgmt.get(domainName, clientMac) : getNotifications(domainName, clientMac);
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, String clientMac, Collection<Stage> includedStages) {
		List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

		if (includedStages != null && !includedStages.isEmpty()) {
			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
					for (ClientMonitorNotification notification : reqsHolder.get(request)) {
						Stage stage = notification.getStage();

						if (includedStages.contains(stage)) {
							notifications.add(notification);
						}
					}
				}
			}
		}

		return notifications;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, String clientMac, Collection<Stage> includedStages, boolean caching) {
		return caching ? cacheMgmt.get(domainName, clientMac, includedStages) : getNotifications(domainName, clientMac, includedStages);
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, Collection<String> clientMacs) {
		List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();
			String monitoredClient = request.getClientMac();

			if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
				notifications.addAll(reqsHolder.get(request));
			}
		}

		return notifications;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, Collection<String> clientMacs, boolean caching) {
		return caching ? cacheMgmt.get(domainName, clientMacs) : getNotifications(domainName, clientMacs);
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, Collection<String> clientMacs, Collection<Stage> includedStages) {
		List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

		if (includedStages != null && !includedStages.isEmpty()) {
			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
					for (ClientMonitorNotification notification : reqsHolder.get(request)) {
						Stage stage = notification.getStage();

						if (includedStages.contains(stage)) {
							notifications.add(notification);
						}
					}
				}
			}
		}

		return notifications;
	}

	@Override
	public List<ClientMonitorNotification> getNotifications(String domainName, Collection<String> clientMacs, Collection<Stage> includedStages, boolean caching) {
		return caching ? cacheMgmt.get(domainName, clientMacs, includedStages) : getNotifications(domainName, clientMacs, includedStages);
	}

	@Override
	public Collection<ClientMonitor> initiateRequests() {
		log.info("initiateRequests", "Initiating overall client monitor processes.");

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				initiate(request);
				requests.add(request);
			}

			log.info("initiateRequests", "Initiated number of " + requests.size() + " client monitor processes. The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> initiateRequests(String domainName) {
		log.info("initiateRequests", "Initiating overall client monitor processes in vHM " + domainName);

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();

				if (domainName.equals(locatedDomain)) {
					initiate(request);
					requests.add(request);
				}
			}

			log.info("initiateRequests", "Initiated number of " + requests.size() + " client monitor processes in vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> initiateRequests(String domainName, String clientMac) {
		log.info("initiateRequests", "Initiating monitor processes for client " + clientMac + " in vHM " + domainName);

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
					initiate(request);
					requests.add(request);
				}
			}

			log.info("initiateRequests", "Initiated number of " + requests.size() + " monitor processes for client " + clientMac + " in vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> initiateRequests(String domainName, Collection<String> clientMacs) {
		log.info("initiateRequests", "Initiating monitor processes for clients " + clientMacs + " in vHM " + domainName);

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
					initiate(request);
					requests.add(request);
				}
			}

			log.info("initiateRequests", "Initiated number of " + requests.size() + " monitor processes for clients " + clientMacs + " in vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> terminateRequests() {
		log.info("terminateRequests", "Terminating overall client monitor processes.");

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				terminate(request);
				requests.add(request);
			}

			log.info("terminateRequests", "Terminated number of " + requests.size() + " client monitor processes. The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> terminateRequests(String domainName) {
		log.info("terminateRequests", "Terminating overall client monitor processes in vHM " + domainName);

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();

				if (domainName.equals(locatedDomain)) {
					terminate(request);
					requests.add(request);
				}
			}

			log.info("terminateRequests", "Terminated number of " + requests.size() + " client monitor processes in vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> terminateRequests(String domainName, String clientMac) {
		log.info("terminateRequests", "Terminating monitor processes for client " + clientMac + " in vHM " + domainName);

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
					terminate(request);
					requests.add(request);
				}
			}

			log.info("terminateRequests", "Terminated number of " + requests.size() + " monitor processes for client " + clientMac + " in vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> terminateRequests(String domainName, Collection<String> clientMacs) {
		log.info("terminateRequests", "Terminating monitor processes for clients " + clientMacs + " in vHM " + domainName);

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			for (ClientMonitor request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
					terminate(request);
					requests.add(request);
				}
			}

			log.info("terminateRequests", "Terminated number of " + requests.size() + " monitor processes for clients " + clientMacs + " in vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> removeRequests() {
		log.info("removeRequests", "Removing overall client monitor processes.");

		/**
		 * Work Flow
		 *
		 * 1. Terminate debug.
		 * 2. Remove debug.
		 * 3. Write log.
		 * 4. Remove cache.
		 */

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			// A map of clients, keyed by domain name and value is another map keyed by client MAC and value is a list of client monitor notifications.
			Map<String, Map<String, List<ClientMonitorNotification>>> domainClientMap = new HashMap<String, Map<String, List<ClientMonitorNotification>>>(maxClients);

			for (Iterator<ClientMonitor> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				ClientMonitor request = reqIter.next();
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				// Terminate request.
				terminate(request);

				Map<String, List<ClientMonitorNotification>> clientNotificationsMap = domainClientMap.get(locatedDomain);

				if (clientNotificationsMap == null) {
					clientNotificationsMap = new HashMap<String, List<ClientMonitorNotification>>(maxClients);
					domainClientMap.put(locatedDomain, clientNotificationsMap);
				}

				List<ClientMonitorNotification> notifications = clientNotificationsMap.get(monitoredClient);

				if (notifications == null) {
					notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);
					clientNotificationsMap.put(monitoredClient, notifications);
				}

				notifications.addAll(reqsHolder.get(request));

				// Remove request.
				reqIter.remove();
				log.info("removeRequests", "Removed a client monitor process - " + request);
				requests.add(request);
			}

			log.info("removeRequests", "Removed number of " + requests.size() + " client monitor processes. The rest number of client monitor processes: " + reqsHolder.size());

			// Write log.
			for (String domainName : domainClientMap.keySet()) {
				Map<String, List<ClientMonitorNotification>> clientNotificationsMap = domainClientMap.get(domainName);

				for (String clientMac : clientNotificationsMap.keySet()) {
					try {
						writeLog(domainName, clientMac, clientNotificationsMap.get(clientMac));
					} catch (IOException e) {
						log.error("removeRequests", "Write client log failed.", e);
					}
				}
			}
			
			// Remove cache.
			cacheMgmt.remove();

			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> removeRequests(String domainName) {
		log.info("removeRequests", "Removing overall client monitor processes for vHM " + domainName);

		/**
		 * Work Flow
		 *
		 * 1. Terminate debug.
		 * 2. Remove debug.
		 * 3. Write log.
		 * 4. Remove cache.
		 */

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			// A map of notifications, keyed by client MAC and value is a list of client monitor notifications.
			Map<String, List<ClientMonitorNotification>> clientNotificationsMap = new HashMap<String, List<ClientMonitorNotification>>(maxClients);

			for (Iterator<ClientMonitor> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				ClientMonitor request = reqIter.next();
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();

				if (domainName.equals(locatedDomain)) {
					// Terminate request.
					terminate(request);

					String monitoredClient = request.getClientMac();
					List<ClientMonitorNotification> notifications = clientNotificationsMap.get(monitoredClient);

					if (notifications == null) {
						notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);
						clientNotificationsMap.put(monitoredClient, notifications);
					}

					notifications.addAll(reqsHolder.get(request));

					// Remove request.
					reqIter.remove();
					log.info("removeRequests", "Removed a client monitor process - " + request);
					requests.add(request);
				}
			}

			log.info("removeRequests", "Removed number of " + requests.size() + " client monitor processes for vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());

			// Write log.
			for (String clientMac : clientNotificationsMap.keySet()) {
				try {
					writeLog(domainName, clientMac, clientNotificationsMap.get(clientMac));
				} catch (IOException e) {
					log.error("removeRequests", "Write client log failed.", e);
				}
			}

			// Remove cache.
			cacheMgmt.remove(domainName);

			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> removeRequests(String domainName, String clientMac) {
		log.info("removeRequests", "Removing monitor processes for client " + clientMac + " to vHM " + domainName);

		/**
		 * Work Flow
		 *
		 * 1. Terminate debug.
		 * 2. Remove debug.
		 * 3. Write log.
		 * 4. Remove cache.
		 */

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());
			List<ClientMonitorNotification> notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);

			for (Iterator<ClientMonitor> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				ClientMonitor request = reqIter.next();
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
					// Terminate request.
					terminate(request);

					notifications.addAll(reqsHolder.get(request));

					// Remove request.
					reqIter.remove();
					log.info("removeRequests", "Removed a client monitor process - " + request);
					requests.add(request);
				}
			}

			log.info("removeRequests", "Removed number of " + requests.size() + " monitor processes for client " + clientMac + " to vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());

			// Write log.
			try {
				writeLog(domainName, clientMac, notifications);
			} catch (IOException e) {
				log.error("removeRequests", "Write client log failed.");
			}

			// Remove cache.
			cacheMgmt.remove(domainName, clientMac);

			return requests;
		}
	}

	@Override
	public Collection<ClientMonitor> removeRequests(String domainName, Collection<String> clientMacs) {
		log.info("removeRequests", "Removing monitor processes for clients " + clientMacs + " to vHM " + domainName);

		/**
		 * Work Flow
		 *
		 * 1. Terminate debug.
		 * 2. Remove debug.
		 * 3. Write log.
		 * 4. Remove cache.
		 */

		synchronized (reqsHolder) {
			Collection<ClientMonitor> requests = new ArrayList<ClientMonitor>(reqsHolder.size());

			// A map of notifications, keyed by client MAC and value is a list of client monitor notifications.
			Map<String, List<ClientMonitorNotification>> clientNotificationsMap = new HashMap<String, List<ClientMonitorNotification>>();

			for (Iterator<ClientMonitor> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				ClientMonitor request = reqIter.next();
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();
				String monitoredClient = request.getClientMac();

				if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
					// Terminate request.
					terminate(request);

					List<ClientMonitorNotification> notifications = clientNotificationsMap.get(monitoredClient);

					if (notifications == null) {
						notifications = new ArrayList<ClientMonitorNotification>(maxWriteLine);
						clientNotificationsMap.put(monitoredClient, notifications);
					}

					notifications.addAll(reqsHolder.get(request));

					// Remove request.
					reqIter.remove();
					log.info("removeRequests", "Removed a client monitor process - " + request);
					requests.add(request);
				}
			}

			log.info("removeRequests", "Removed number of " + requests.size() + " monitor processes for clients " + clientMacs + " to vHM " + domainName + ". The rest number of client monitor processes: " + reqsHolder.size());

			// Write log.
			for (String clientMac : clientNotificationsMap.keySet()) {
				try {
					writeLog(domainName, clientMac, clientNotificationsMap.get(clientMac));
				} catch (IOException e) {
					log.error("removeRequests", "Write client log failed.", e);
				}
			}

			// Remove cache.
			cacheMgmt.remove(domainName, clientMacs);

			return requests;
		}
	}

	/**
	 * Exports client log associated with the <tt>clientMac</tt> given in specified vHM <tt>domainName</tt> and returns the path of the log.
	 *
	 * @param domainName the name of vHM.
	 * @param clientMac client MAC address.
	 * @return path of the mast client log.
	 * @throws IOException if any I/O error occurs.
	 */
	@Override
	public String exportClientLog(String domainName, String clientMac) throws IOException {
		log.info("exportClientLog", "Exporting log for client " + clientMac + " from vHM " + domainName);

		synchronized (reqsHolder) {
			List<ClientMonitorNotification> notifications = getNotifications(domainName, clientMac);

			// Write notifications into log.
			String mastClientLogPath = writeLog(domainName, clientMac, notifications);

			// Clear the notifications which were just written into log.
			clearNotifications(domainName, clientMac);

			return mastClientLogPath;
		}
	}

	/**
	 * Exports overall client logs for specified vHM <tt>domainName</tt> that are encapsulated into an archive of which the path is used as result to return.
	 *
	 * @param domainName the name of vHM.
	 * @return path of the archive encapsulates associated client logs.
	 * @throws IOException if any I/O error occurs.
	 */
	@Override
	public String exportClientLogs(String domainName) throws IOException {
		log.info("exportClientLogs", "Exporting client logs for vHM " + domainName);

		// A set of clients monitored within a specified vHM
		Collection<String> clientMacs = new HashSet<String>();

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();

			if (domainName.equals(locatedDomain)) {
				String monitoredClient = request.getClientMac();
				clientMacs.add(monitoredClient);
			}
		}

		return exportClientLogs(domainName, clientMacs);
	}

	/**
	 * Exports a set of client logs associated with <tt>clientMacs</tt> given in specified vHM <tt>domainName</tt> that are encapsulated into an archive of which the path is used as result to return.
	 *
	 * @param domainName the name of vHM.
	 * @param clientMacs a set of client MAC addresses.
	 * @return path of the archive encapsulates associated client logs.
	 * @throws IOException if any I/O error occurs.
	 */
	@Override
	public String exportClientLogs(String domainName, Collection<String> clientMacs) throws IOException {
		log.info("exportClientLogs", "Exporting logs for clients " + clientMacs + " from vHM " + domainName);

		// A map of client log paths, keyed by client MAC and value is path of the client log to be archived.
		Map<String, String> mastClientLogPaths = new HashMap<String, String>(clientMacs.size());

		// Write notifications collected into log before archiving.
		for (String clientMac : clientMacs) {
			String mastClientLogPath = exportClientLog(domainName, clientMac);
			mastClientLogPaths.put(clientMac, mastClientLogPath);
		}

		FileManager fileUtil = FileManager.getInstance();

		// Collect associated client logs into a temporary directory to be tared into an archive.
		String archiveDirPath = mkArchiveDir();

		// Copy client logs into the temporary directory to archive.
		for (String clientMac : mastClientLogPaths.keySet()) {
			String mastClientLogPath = mastClientLogPaths.get(clientMac);

			// Client log may include multiple files.
			for (int i = 0; i < maxLogNum; i++) {
				String clientLogPath = getClientLogPath(mastClientLogPath, i);
				File clientLog = new File(clientLogPath);

				if (clientLog.exists()) {
					String dupClientLogPath = archiveDirPath + clientLog.getName();
					fileUtil.copyFile(clientLogPath, dupClientLogPath, false);
				}
			}
		}

		// Tar the temporary directory to be an archive and return the archive's path.
		AhTar tar = new AhTar();
		boolean debug = log.getLogger().isDebugEnabled();
		tar.setDebug(debug);

		return tar.tarArchive(archiveDirPath, true);
	}

	@Override
	public void clearNotifications() {
		for (ClientMonitor request : reqsHolder.keySet()) {
			List<ClientMonitorNotification> notifications = reqsHolder.get(request);
			notifications.clear();
		}
	}

	@Override
	public void clearNotifications(boolean caching) {
		if (caching) {
			cacheMgmt.clear();
		} else {
			clearNotifications();
		}
	}

	@Override
	public void clearNotifications(String domainName) {
		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();

			if (domainName.equals(locatedDomain)) {
				List<ClientMonitorNotification> notifications = reqsHolder.get(request);
				notifications.clear();
			}
		}
	}

	@Override
	public void clearNotifications(String domainName, boolean caching) {
		if (caching) {
			cacheMgmt.clear(domainName);
		} else {
			clearNotifications(domainName);
		}
	}

	@Override
	public void clearNotifications(String domainName, String clientMac) {
		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();
			String monitoredClient = request.getClientMac();

			if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient)) {
				List<ClientMonitorNotification> notifications = reqsHolder.get(request);
				notifications.clear();
			}
		}
	}

	@Override
	public void clearNotifications(String domainName, String clientMac, boolean caching) {
		if (caching) {
			cacheMgmt.clear(domainName, clientMac);
		} else {
			clearNotifications(domainName, clientMac);
		}
	}

	@Override
	public void clearNotifications(String domainName, Collection<String> clientMacs) {
		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();
			String monitoredClient = request.getClientMac();

			if (domainName.equals(locatedDomain) && clientMacs.contains(monitoredClient)) {
				List<ClientMonitorNotification> notifications = reqsHolder.get(request);
				notifications.clear();
			}
		}
	}

	@Override
	public void clearNotifications(String domainName, Collection<String> clientMacs, boolean caching) {
		if (caching) {
			cacheMgmt.clear(domainName, clientMacs);
		} else {
			clearNotifications(domainName, clientMacs);
		}
	}

	@Override
	public void recoverRequests(String hiveApMac) {
		log.info("recoverRequests", "Recovering client monitor processes over HiveAP " + hiveApMac);

		synchronized (reqsHolder) {
			for (ClientMonitor request : reqsHolder.keySet()) {
				if (request.getHiveApMac().equalsIgnoreCase(hiveApMac)) {
					switch (request.getDebugState().getState()) {
						case INITIATION_REQUESTED: // User has already started monitor process.
						case INITIATION_RESPONSED:
						case INITIATION_FAILED:
						case ABORTED:
							log.info("recoverRequests", "Attempted to recover client monitor process - " + request);

							try {
								request.initiate();
							} catch (DebugException e) {
								log.error("recoverRequests", "Failed to recover client monitor process - " + request, e);
							}
							break;
						case UNINITIATED: // User hasn't started monitor process yet.
						case STOPPED:
						case FINISHED:
						default:
							break;
					}
				}
			}
		}
	}

	@Override
	public List<String> getClientsWithLog(String domainName) {
		String clientLogDirPath = AhDirTools.getCmDir(domainName);
		File clientLogDir = new File(clientLogDirPath);
		String[] clientLogs = clientLogDir.list(new MastLogNameFilter());
		List<String> clients = null;

		if (clientLogs != null) {
			clients = new ArrayList<String>(clientLogs.length);

			for (String clientLog : clientLogs) {
				int clientLogPrefixLen = CLIENT_LOG_NAME_PREFIX.length();
				String client = clientLog.substring(clientLogPrefixLen, clientLogPrefixLen + 12);
				clients.add(client);
			}
		}

		return clients;
	}

	@Override
	public List<String> getUnmonitoredClientsWithLog(String domainName) {
		List<String> unmonitoredClients = getClientsWithLog(domainName);

		if (unmonitoredClients != null && !unmonitoredClients.isEmpty()) {
			Collection<ClientMonitor> clientMonitors = getRequests(domainName);

			if (clientMonitors != null) {
				for (ClientMonitor clientMonitor : clientMonitors) {
					String client = clientMonitor.getClientMac();
					unmonitoredClients.remove(client);
				}
			}
		}

		return unmonitoredClients;
	}

	@Override
	public synchronized void deleteClientLog(String domainName, String clientMac) throws DebugException {
		log.info("deleteClientLog", "Deleting log for client " + clientMac + " from vHM " + domainName);
		Collection<ClientMonitor> requests = getRequests(domainName, clientMac);

		if (requests != null && !requests.isEmpty()) {
			throw new DebugException(MgrUtil.getUserMessage("warn.debug.log.deletion.prohibit", clientMac));
		}

		String clientLogDirPath = AhDirTools.getCmDir(domainName);
		File clientLogDir = new File(clientLogDirPath);
		File[] clientLogs = clientLogDir.listFiles(new LogNameFilter());

		if (clientLogs != null) {
			for (File clientLog : clientLogs) {
				String clientLogName = clientLog.getName();
				int logNamePrefixLen = CLIENT_LOG_NAME_PREFIX.length();
				String associatedClient = clientLogName.substring(logNamePrefixLen, logNamePrefixLen + 12);

				if (associatedClient.equals(clientMac)) {
					boolean deleted = clientLog.delete();

					if (log.getLogger().isDebugEnabled()) {
						log.debug("deleteClientLog", "Client log '" + clientLog.getPath() +  "' was " + (deleted ? "deleted." : "not deleted."));
					}
				}
			}
		}
	}

	@Override
	public void deleteClientLogs(String domainName, Collection<String> clientMacs) throws DebugException {
		log.info("deleteClientLogs", "Deleting logs for clients " + clientMacs + " from vHM " + domainName);

		for (String clientMac : clientMacs) {
			deleteClientLog(domainName, clientMac);
		}
	}

	@Override
	public void changeState(DebugState newState, int cookieId) {
		synchronized (reqsHolder) {
			for (ClientMonitor request : reqsHolder.keySet()) {
				if (request.getCookieId() == cookieId) {
					request.changeState(newState);
					return;
				}
			}
		}

		log.warn("changeState", "Could not find out a client monitor process with cookie " + cookieId);
	}

	@Override
	public void changeState(DebugState newState, String hiveApMac) {
		synchronized (reqsHolder) {
			for (ClientMonitor request : reqsHolder.keySet()) {
				if (request.getHiveApMac().equalsIgnoreCase(hiveApMac)) {
					request.changeState(newState);
				}
			}
		}
	}

	public Map<ClientMonitor, List<ClientMonitorNotification>> getReqsHolder() {
		return reqsHolder;
	}

	class MastLogNameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(CLIENT_LOG_NAME_PREFIX) && name.endsWith(CLIENT_LOG_NAME_SUFFIX) && name.length() == CLIENT_LOG_NAME_PREFIX.length() + 12 + CLIENT_LOG_NAME_SUFFIX.length();
		}
	}

	class LogNameFilter implements FilenameFilter {
		@Override
		public boolean accept(File dir, String name) {
			return name.startsWith(CLIENT_LOG_NAME_PREFIX) && name.length() >= CLIENT_LOG_NAME_PREFIX.length() + 12 + CLIENT_LOG_NAME_SUFFIX.length();
		}
	}

	private void updateActivityStatus(ClientMonitorNotification notification) {
		String clientMac = notification.getClientMac();
		String hiveApMac = notification.getHiveApMac();
		int cookieId = notification.getCookieId();

		if (log.getLogger().isDebugEnabled()) {
			log.debug("updateActivityStatus", "Updating activity status. Client: " + clientMac + "; HiveAP: " + hiveApMac + "; Cookie: " + cookieId);
		}

		for (ClientMonitor request : reqsHolder.keySet()) {
			String monitoredClientMac = request.getClientMac();

			if (clientMac.equals(monitoredClientMac)) {
				// Update activity status.
				boolean activated = cookieId == request.getCookieId();
				request.setActive(activated);

				if (activated) {
					// Update recent event of interest.
					request.setRecentInterestEvent(notification);
				}

				if (log.getLogger().isDebugEnabled()) {
					log.debug("updateActivityStatus", request + " - Activity Status: " + activated);
				}
			}
		}
	}

	/**
	 * Returns path of the log to write.
	 *
	 * @param domainName the name of vHM.
	 * @param clientMac client MAC address associated with the log to write.
	 * @param notifications a list of client monitor notifications for building the messages to write.
	 * @return path of the log to write.
	 * @throws IOException if any I/O error occurs.
	 */
	private String writeLog(String domainName, String clientMac, List<ClientMonitorNotification> notifications) throws IOException {
		String loggedMsgs = "";

		if (notifications != null && !notifications.isEmpty()) {
			ClientMonitorSortImpl sort = getDefaultSorting();
			loggedMsgs = sort.getFormattedMessages(notifications, false, domainName);
		}

		return writeLog(domainName, clientMac, loggedMsgs);
	}

	/**
	 * Returns path of the log to write.
	 *
	 * @param domainName the name of vHM.
	 * @param clientMac client MAC address associated with the log to write.
	 * @param newMsgs new messages to write.
	 * @return path of the log to write.
	 * @throws IOException if any I/O error occurs.
	 */
	/*-
	private String writeLog(String domainName, String clientMac, String newMsgs) throws IOException {
		String mastClientLogPath = getMastClientLogPath(domainName, clientMac);

		if (newMsgs != null && !newMsgs.trim().isEmpty()) {
			FileManager fileUtil = FileManager.getInstance();
			File mastClientLogFile = new File(mastClientLogPath);

			if (mastClientLogFile.exists()) {			
				String oriMsgs = fileUtil.readFromFile(mastClientLogPath);
				String totalMsgs = oriMsgs + newMsgs;
				String[] splitedMsgs = totalMsgs.split("\n");

				if (splitedMsgs.length > maxLogLine) {
					if (maxLogNum > 1) {
						boolean logRotated = rotateLogs(mastClientLogPath);

						if (logRotated) {
							// Write the top 'maxLogLine' line of messages into xxx.log.1
							StringBuilder logBuf = new StringBuilder();

							for (int i = 0; i < maxLogLine; i++) {
								logBuf.append(splitedMsgs[i]).append("\n");
							}

							String subClientLog1Path = getClientLogPath(mastClientLogPath, 1);
							fileUtil.writeFile(subClientLog1Path, logBuf.toString(), false);

							if (log.getLogger().isDebugEnabled()) {
								log.debug("writeLog", "Wrote number of " + maxLogLine + " messages into log " + subClientLog1Path);
                            }

							// Write the rest of messages into xxx.log
							logBuf = new StringBuilder();

							for (int i = maxLogLine; i < splitedMsgs.length; i++) {
								logBuf.append(splitedMsgs[i]).append("\n");
							}

							fileUtil.writeFile(mastClientLogPath, logBuf.toString(), false);

							if (log.getLogger().isDebugEnabled()) {
								log.debug("writeLog", "Wrote number of " + (splitedMsgs.length - maxLogLine) + " messages into log " + mastClientLogPath);
							}
						} else {
							log.warn("writeLog", mastClientLogPath + " - Log rotation failed. Messages '" + newMsgs + "' were dropped.");
						}
					} else {
						// Log rotation is performed within the mast log file.
						StringBuilder logBuf = new StringBuilder();

						for (int i = splitedMsgs.length - maxLogLine; i < splitedMsgs.length; i++) {
							logBuf.append(splitedMsgs[i]).append("\n");
						}

						// Override into an existing log.
						fileUtil.writeFile(mastClientLogPath, logBuf.toString(), false);

						if (log.getLogger().isDebugEnabled()) {
							log.debug("writeLog", "Wrote number of " + maxLogLine + " messages into log " + mastClientLogPath);
						}
					}
				} else {
					// Append into an existing log.
					fileUtil.writeFile(mastClientLogPath, newMsgs, true);

					if (log.getLogger().isDebugEnabled()) {
						log.debug("writeLog", "Appended messages into log " + mastClientLogPath);
					}
				}
			} else {
				// Write into a new log.
				fileUtil.writeFile(mastClientLogPath, newMsgs, false);

				if (log.getLogger().isDebugEnabled()) {
					log.debug("writeLog", "Wrote messages into a new log " + mastClientLogPath);
				}
			}
		}

		return mastClientLogPath;
	}
	
	private boolean rotateLogs(String mastLogPath) {
		for (int i = maxLogNum - 1; i > 0; i--) {
			String subLogPath = getClientLogPath(mastLogPath, i);
			File subLogFile = new File(subLogPath);

			if (subLogFile.exists() && subLogFile.isFile()) {
				if (i == maxLogNum - 1) {
					// Remove the last log.
					boolean deleted = subLogFile.delete();

					if (!deleted) {
						log.warn("rotateLogs", "Log rotation failed because the log '" + subLogFile.getPath() + "' wasn't deleted.");
						return false;
					}
				} else {
					// Rename log from xxx.log.i to xxx.log.(i + 1)
					String renamedLogPath = getClientLogPath(mastLogPath, i + 1);
					File renamedLogFile = new File(renamedLogPath);
					boolean renamed = subLogFile.renameTo(renamedLogFile);

					if (!renamed) {
						log.warn("rotateLogs", "Log rotation failed because the log '" + subLogFile.getPath() + "' wasn't renamed to " + renamedLogFile.getPath());
						return false;
					}
				}
			}
		}

		return true;
	}*/

	private String writeLog(String domainName, String clientMac, String newMsgs) throws IOException {
		String mastClientLogPath = getMastClientLogPath(domainName, clientMac);

		if (newMsgs != null && !newMsgs.trim().isEmpty()) {
			FileManager fileUtil = FileManager.getInstance();
			File mastClientLogFile = new File(mastClientLogPath);

			if (mastClientLogFile.exists()) {
				if (mastClientLogFile.length() >= maxLogSize) {
					if (maxLogNum > 1) {
						boolean logRotated = rotateLogs(mastClientLogPath);

						if (logRotated) {
							// Write into a new mast log.
							fileUtil.writeFile(mastClientLogPath, newMsgs, false);

							if (log.getLogger().isDebugEnabled()) {
								log.debug("writeLog", "Wrote messages into log " + mastClientLogPath);
							}
						} else {
							log.warn("writeLog", mastClientLogPath + " - Log rotation failed. Messages '" + newMsgs + "' were dropped.");
						}
					} else {
						String oriMsgs = fileUtil.readFromFile(mastClientLogPath);
						String totalMsgs = oriMsgs + newMsgs;
						String[] splitedMsgs = totalMsgs.split("\n");

						// Log rotation is performed within the mast log.
						int writeLineNum;
						StringBuilder logBuf = new StringBuilder();

						if (splitedMsgs.length >= maxLogLine) {
							for (int i = splitedMsgs.length - maxLogLine; i < splitedMsgs.length; i++) {
								logBuf.append(splitedMsgs[i]).append("\n");
							}

							writeLineNum = maxLogLine;
						} else {
							for (String msg : splitedMsgs) {
								logBuf.append(msg).append("\n");
							}

							writeLineNum = splitedMsgs.length;
						}

						// Rewrite the mast log.
						fileUtil.writeFile(mastClientLogPath, logBuf.toString(), false);

						if (log.getLogger().isDebugEnabled()) {
							log.debug("writeLog", "Wrote number of " + writeLineNum + " messages into log " + mastClientLogPath);
						}
					}
				} else {
					// Append into an existing log.
					fileUtil.writeFile(mastClientLogPath, newMsgs, true);

					if (log.getLogger().isDebugEnabled()) {
						log.debug("writeLog", "Appended messages into log " + mastClientLogPath);
					}
				}
			} else {
				// Write into a new mast log.
				fileUtil.writeFile(mastClientLogPath, newMsgs, false);

				if (log.getLogger().isDebugEnabled()) {
					log.debug("writeLog", "Wrote messages into a new log " + mastClientLogPath);
				}
			}
		}

		return mastClientLogPath;
	}

	private boolean rotateLogs(String mastLogPath) {
		for (int i = maxLogNum - 1; i >= 0; i--) {
			String subLogPath = getClientLogPath(mastLogPath, i);
			File subLogFile = new File(subLogPath);

			if (subLogFile.exists() && subLogFile.isFile()) {
				if (i == maxLogNum - 1) {
					// Remove the last log.
					boolean deleted = subLogFile.delete();

					if (!deleted) {
						log.warn("rotateLogs", "Log rotation failed because the log '" + subLogFile.getPath() + "' wasn't deleted.");
						return false;
					}
				} else {
					// Rename log from xxx.log.i to xxx.log.(i + 1)
					String renamedLogPath = getClientLogPath(mastLogPath, i + 1);
					File renamedLogFile = new File(renamedLogPath);
					boolean renamed = subLogFile.renameTo(renamedLogFile);

					if (!renamed) {
						log.warn("rotateLogs", "Log rotation failed because the log '" + subLogFile.getPath() + "' wasn't renamed to " + renamedLogFile.getPath());
						return false;
					}
				}
			}
		}

		return true;
	}

	private String getMastClientLogName(String clientMac) {
		return CLIENT_LOG_NAME_PREFIX + clientMac + CLIENT_LOG_NAME_SUFFIX;
	}

	private String getMastClientLogPath(String domainName, String clientMac) {
		return AhDirTools.getCmDir(domainName) + getMastClientLogName(clientMac);
	}

	private String getClientLogPath(String mastClientLogPath, int index) {
		String clientLogPath = mastClientLogPath;

		if (index > 0 && index < maxLogNum) {
			clientLogPath += "." + index;
		}

		return clientLogPath;
	}

	private String mkArchiveDir() {
		String archiveDirName = CLIENT_LOG_ARCHIVE_DIR_NAME_PREFIX + System.currentTimeMillis();
		String archiveDirPath = AhDirTools.getCmTempFileDir() + archiveDirName + File.separator;
		File archiveDir = new File(archiveDirPath);

		if (archiveDir.exists()) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException ie) {
				log.error("mkArchiveDir", "Thread sleep failed.");
			}

			return mkArchiveDir();
		} else {
			boolean created = archiveDir.mkdir();

			if (created) {
				if (log.getLogger().isDebugEnabled()) {
					log.debug("mkArchiveDir", "Archive directory created: " + archiveDirPath);
				}

				return archiveDirPath;
			} else {
				return mkArchiveDir();
			}
		}
	}

	private ClientMonitorSortImpl getDefaultSorting() {
		ClientMonitorSortParams[] sortParams = new ClientMonitorSortParams[] {
			new ClientMonitorSortParams(SortType.LOG_MSG_TIME, true),
			new ClientMonitorSortParams(SortType.AP_NODE_NAME, true),
			new ClientMonitorSortParams(SortType.MSG_SEQ_NUM, true) };
		ClientMonitorFilterParams filterParam = new ClientMonitorFilterParams(LogLevel.DETAIL);

		return new ClientMonitorSortImpl(sortParams, filterParam);
	}

	private boolean requireAutoInit(ClientMonitor newRequest) {
		String domainName = newRequest.getHiveAp().getOwner().getDomainName();
		String clientMac = newRequest.getClientMac();
		int newRequestCookieId = newRequest.getCookieId();
		boolean filterSettingDone = false;

		for (ClientMonitor request : reqsHolder.keySet()) {
			HiveAp hiveAp = request.getHiveAp();
			String locatedDomain = hiveAp.getOwner().getDomainName();
			String monitoredClient = request.getClientMac();
			int cookieId = request.getCookieId();

			if (domainName.equals(locatedDomain) && clientMac.equals(monitoredClient) && newRequestCookieId != cookieId) {
				// The filtering indication for the new request should be identical with those of existing requests.
				if (!filterSettingDone) {
					newRequest.setFilteringProbeEvents(request.isFilteringProbeEvents());
					filterSettingDone = true;
				}

				switch (request.getDebugState().getState()) {
					case INITIATION_REQUESTED:
					case INITIATION_RESPONSED:
					case INITIATION_FAILED:
					case ABORTED:
						return true;
					case UNINITIATED:
					case FINISHED:
					case STOPPED:
					default:
						break;
				}
			}
		}

		return false;
	}

	private void initiate(ClientMonitor request) {
		switch (request.getDebugState().getState()) {
			case UNINITIATED:
			case INITIATION_REQUESTED:
			case INITIATION_FAILED:
			case ABORTED:
			case STOPPED:
			case FINISHED:
				try {
					request.initiate();
				} catch (DebugException e) {
					log.error("initiate", "Failed to initiate a client monitor process - " + request, e);
				}
				break;
			case INITIATION_RESPONSED:			
			default:
				break;
		}
	}

	private void terminate(ClientMonitor request) {
		hiveApDebugMgmt.terminate(request);
	}

}