package com.ah.be.ts.hiveap.probe.vlan;

import java.util.Collection;
import java.util.List;

import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugState;

public interface VlanProbeMgmt<R extends VlanProbe, N extends VlanProbeNotification> {

	R addRequest(R request) throws DebugException;

	boolean addNotification(N notification);

	Collection<R> getRequests();

	R getRequest(int cookieId);

	Collection<R> getGroupRequests(int groupId);

	List<N> getNotifications(int cookieId);

	List<N> getGroupNotifications(int groupId);

	Collection<R> terminateRequests();

	R terminateRequest(int cookieId);

	Collection<R> terminateRequests(String sessionId);

	Collection<R> terminateGroupRequests(int groupId);

	Collection<R> terminateDomainRequests(String domainName);

	Collection<R> removeRequests();

	R removeRequest(int cookieId);

	Collection<R> removeRequests(String sessionId);

	Collection<R> removeGroupRequests(int groupId);

	Collection<R> removeDomainRequests(String domainName);

	void changeState(DebugState newState, int cookieId);

	void changeState(DebugState newState, String hiveApMac);

}