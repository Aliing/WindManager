package com.ah.be.ts.hiveap.monitor.client.cache;

import java.util.Collection;
import java.util.List;

import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification.Stage;

public interface ClientMonitorCacheMgmt<C extends ClientMonitorCache, N extends ClientMonitorNotification> {

	void add(String domainName, String clientMac, N notification);

	List<N> get();

	List<N> get(String domainName);

	List<N> get(String domainName, String clientMac);

	List<N> get(String domainName, String clientMac, Collection<Stage> includedStages);

	List<N> get(String domainName, Collection<String> clientMacs);

	List<N> get(String domainName, Collection<String> clientMacs, Collection<Stage> includedStages);

	Collection<C> remove();

	Collection<C> remove(String domainName);

	C remove(String domainName, String clientMac);

	Collection<C> remove(String domainName, Collection<String> clientMacs);

	void clear();

	void clear(String domainName);

	void clear(String domainName, String clientMac);

	void clear(String domainName, Collection<String> clientMacs);

}