package com.ah.be.ts.hiveap.monitor.client;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugState;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification.Stage;

public interface ClientMonitorMgmt<R extends ClientMonitor, N extends ClientMonitorNotification> {

	int getMaxClients();

	void setMaxClients(int maxMonitorClients);

	R addRequest(R request);

	boolean addNotification(N notification);
	
	boolean addNotification(N notification, boolean performance);

	Collection<R> getRequests();

	Collection<R> getRequests(String domainName);

	Collection<R> getRequests(String domainName, String clientMac);

	List<N> getNotifications();

	List<N> getNotifications(boolean caching);

	List<N> getNotifications(String domainName);

	List<N> getNotifications(String domainName, boolean caching);

	List<N> getNotifications(String domainName, String clientMac);

	List<N> getNotifications(String domainName, String clientMac, boolean caching);

	List<N> getNotifications(String domainName, String clientMac, Collection<Stage> includedStages);

	List<N> getNotifications(String domainName, String clientMac, Collection<Stage> includedStages, boolean caching);

	List<N> getNotifications(String domainName, Collection<String> clientMacs);

	List<N> getNotifications(String domainName, Collection<String> clientMacs, boolean caching);

	List<N> getNotifications(String domainName, Collection<String> clientMacs, Collection<Stage> includedStages);

	List<N> getNotifications(String domainName, Collection<String> clientMacs, Collection<Stage> includedStages, boolean caching);
	
	Collection<ClientMonitor> initiateRequests();

	Collection<ClientMonitor> initiateRequests(String domainName);

	Collection<ClientMonitor> initiateRequests(String domainName, String clientMac);

	Collection<ClientMonitor> initiateRequests(String domainName, Collection<String> clientMacs);

	Collection<R> terminateRequests();

	Collection<R> terminateRequests(String domainName);

	Collection<R> terminateRequests(String domainName, String clientMac);

	Collection<R> terminateRequests(String domainName, Collection<String> clientMacs);

	Collection<R> removeRequests();

	Collection<R> removeRequests(String domainName);

	Collection<R> removeRequests(String domainName, String clientMac);

	Collection<R> removeRequests(String domainName, Collection<String> clientMacs);

	String exportClientLog(String domainName, String clientMac) throws IOException;

	String exportClientLogs(String domainName) throws IOException;

	String exportClientLogs(String domainName, Collection<String> clientMacs) throws IOException;

	void clearNotifications();

	void clearNotifications(boolean caching);

	void clearNotifications(String domainName);

	void clearNotifications(String domainName, boolean caching);

	void clearNotifications(String domainName, String clientMac);

	void clearNotifications(String domainName, String clientMac, boolean caching);

	void clearNotifications(String domainName, Collection<String> clientMacs);

	void clearNotifications(String domainName, Collection<String> clientMacs, boolean caching);

	void recoverRequests(String hiveApMac);

	List<String> getClientsWithLog(String domainName);

	List<String> getUnmonitoredClientsWithLog(String domainName);
	
	void deleteClientLog(String domainName, String clientMac) throws DebugException;

	void deleteClientLogs(String domainName, Collection<String> clientMacs) throws DebugException;

	void changeState(DebugState newState, int cookieId);

	void changeState(DebugState newState, String hiveApMac);
	
}