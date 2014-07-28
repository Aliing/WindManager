package com.ah.be.ts.hiveap.probe.vlan.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.NmsUtil;
import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugState;
import com.ah.be.ts.hiveap.DebugState.State;
import com.ah.be.ts.hiveap.impl.HiveApDebugMgmtImpl;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbe;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeMgmt;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeNotification;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class VlanProbeMgmtImpl implements VlanProbeMgmt<VlanProbe, VlanProbeNotification> {

	private static final Tracer log = new Tracer(VlanProbeMgmtImpl.class.getSimpleName());

	/* Request holder */
	private final Map<VlanProbe, List<VlanProbeNotification>> reqsHolder;

	private final HiveApDebugMgmtImpl hiveApDebugMgmt;

	public VlanProbeMgmtImpl(HiveApDebugMgmtImpl hiveApDebugMgmt) {
		reqsHolder = Collections.synchronizedMap(new LinkedHashMap<VlanProbe, List<VlanProbeNotification>>(5000));
		this.hiveApDebugMgmt = hiveApDebugMgmt;
	}

	@Override
	public VlanProbe addRequest(VlanProbe request) throws DebugException {
		if (request == null) {
			throw new IllegalArgumentException("Invalid argument - " + request);
		}

		String hiveApMac = request.getHiveApMac();

		if (hiveApMac == null || hiveApMac.trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid " + NmsUtil.getOEMCustomer().getAccessPonitName() + " - " + hiveApMac);
		}

		String sessionId = request.getSessionId();

		if (sessionId == null || sessionId.trim().isEmpty()) {
			throw new IllegalArgumentException("Invalid session - " + sessionId);
		}

		synchronized (reqsHolder) {
//			if (!request.isInProcess()) {
				// Need to initiate a debug process first if it hasn't been done yet.
				int cookieId = request.initiate();
//			}

			if (reqsHolder.containsKey(request)) {
				log.error("addRequest", "The VLAN probe process '" + request + "' has already existed.");
				throw new DebugException(MgrUtil.getUserMessage("warn.debug.alreadyExists", request.getName()));
			}

			List<VlanProbeNotification> notifications = new ArrayList<VlanProbeNotification>(500);
			reqsHolder.put(request, notifications);
			log.info("addRequest", "Added a new VLAN probe process - " + request + "; Cookie: " + cookieId + ". The current number of VLAN probe processes: " + reqsHolder.size());
			return request;
		}
	}

	@Override
	public boolean addNotification(VlanProbeNotification notification) {
		if (notification == null) {
			log.warn("addNotification", "Invalid argument - " + notification);
			return false;
		}

		int cookieId = notification.getCookieId();

		if (log.getLogger().isDebugEnabled()) {
			log.debug("addNotification", "Received a new VLAN probe notification - " + notification);
		}

		synchronized (reqsHolder) {
			for (VlanProbe request : reqsHolder.keySet()) {
				if (request.getCookieId() == cookieId) {
					if (request.getHiveApMac().equalsIgnoreCase(notification.getHiveApMac())) {
						if (!State.STOPPED.equals(request.getDebugState().getState())) {
							// Set HiveAp object for sorting.
							notification.setHiveAp(request.getHiveAp());

							// Conserve this notification received.
							List<VlanProbeNotification> notifications = reqsHolder.get(request);
							notifications.add(notification);

							// Sort by default.
						//	Collections.sort(notifications);

							if (log.getLogger().isDebugEnabled()) {
								log.debug("addNotification", "Added a VLAN probe notification - " + notification);
							}

							return true;
						} else {
							log.warn("addNotification", notification + " - The VLAN probe process corresponding to this notification had been stopped. Dropped this notification and attempted to terminate request - " + request);
							break;
						}
					} else {
						log.warn("addNotification", "The cookie " + cookieId + " for the VLAN probe notification " + notification + " is being used by another request " + request + ". ***** Why did this situation take place? *****");
					}
				}
			}
		}

		// Try to terminate an in existent debug relative to the debug notification situation.
//		log.warn("addNotification", "The HiveAP debug request with cookie " + cookieId + " doesn't exist. Trying to notify HiveAP " + notification.getHiveApMac() + " terminating reporting " + notification.getCategory() + " notifications with this cookie.");

		try {
			hiveApDebugMgmt.terminatePseudoRequest(notification);

			// Remove all of cached notification events with the same cookie so that protect them from being handled in any places.
			hiveApDebugMgmt.getNotificationProcessor().removeEvents(cookieId);
		} catch (Exception e) {
			log.error("addNotification", "Failed to terminate a pseudo VLAN probe process - " + notification, e);
		}

		return false;
	}

	@Override
	public Collection<VlanProbe> getRequests() {
		return new ArrayList<VlanProbe>(reqsHolder.keySet());
	}

	@Override
	public VlanProbe getRequest(int cookieId) {
		VlanProbe candidate = null;

		for (VlanProbe request : reqsHolder.keySet()) {
			if (request.getCookieId() == cookieId) {
				candidate = request;
				break;
			}
		}

		return candidate;
	}

	@Override
	public Collection<VlanProbe> getGroupRequests(int groupId) {
		Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

		for (VlanProbe request : reqsHolder.keySet()) {
			if (request.getGroupId() == groupId) {
				requests.add(request);
			}
		}

		return requests;
	}

	@Override
	public List<VlanProbeNotification> getNotifications(int cookieId) {
		for (VlanProbe request : reqsHolder.keySet()) {
			if (request.getCookieId() == cookieId) {
				List<VlanProbeNotification> notifications = new ArrayList<VlanProbeNotification>(reqsHolder.get(request));
				Collections.sort(notifications);

				// Return a sorted list of notifications.
				return notifications;
			}
		}

		return new ArrayList<VlanProbeNotification>(0);
	}

	@Override
	public List<VlanProbeNotification> getGroupNotifications(int groupId) {
		List<VlanProbeNotification> notifications = new ArrayList<VlanProbeNotification>();

		for (VlanProbe request : reqsHolder.keySet()) {
			if (request.getGroupId() == groupId) {
				notifications.addAll(reqsHolder.get(request));
			}
		}

		return notifications;
	}

	@Override
	public Collection<VlanProbe> terminateRequests() {
		log.info("terminateRequests", "Terminating overall VLAN probe processes.");

		synchronized (reqsHolder) {
			Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

			for (VlanProbe request : reqsHolder.keySet()) {
				hiveApDebugMgmt.terminate(request);
				requests.add(request);
			}

			log.info("terminateRequests", "Terminated number of " + requests.size() + " VLAN probe processes. The rest number of VLAN probe processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public VlanProbe terminateRequest(int cookieId) {
		log.info("terminateRequest", "Terminating a VLAN probe process with cookie " + cookieId);
		VlanProbe candidate = null;

		synchronized (reqsHolder) {
			for (VlanProbe request : reqsHolder.keySet()) {
				if (request.getCookieId() == cookieId) {
					hiveApDebugMgmt.terminate(request);
					candidate = request;
					break;
				}
			}

			if (candidate != null) {
				log.info("terminateRequest", "Terminated a VLAN probe process - " + candidate + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			} else {
				log.warn("terminateRequest", "Terminated a nonexistent VLAN probe process with cookie " + cookieId + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			}

			return candidate;
		}
	}

	@Override
	public Collection<VlanProbe> terminateRequests(String sessionId) {
		log.info("terminateRequests", "Terminating VLAN probe processes for session " + sessionId);
		Collection<VlanProbe> requests;

		if (sessionId != null && !sessionId.trim().isEmpty()) {
			synchronized (reqsHolder) {
				requests = new ArrayList<VlanProbe>(reqsHolder.size());

				for (VlanProbe request : reqsHolder.keySet()) {
					if (sessionId.equals(request.getSessionId())) {
						hiveApDebugMgmt.terminate(request);
						requests.add(request);
					}
				}

				log.info("terminateRequests", "Terminated number of " + requests.size() + " VLAN probe processes for session " + sessionId + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			}
		} else {
			requests = new ArrayList<VlanProbe>(0);
		}

		return requests;
	}

	@Override
	public Collection<VlanProbe> terminateGroupRequests(int groupId) {
		log.info("terminateGroupRequests", "Terminating VLAN probe processes for group " + groupId);

		synchronized (reqsHolder) {
			Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

			for (VlanProbe request : reqsHolder.keySet()) {
				if (request.getGroupId() == groupId) {
					hiveApDebugMgmt.terminate(request);
					requests.add(request);
				}
			}

			log.info("terminateGroupRequests", "Terminated number of " + requests.size() + " VLAN probe processes for group " + groupId + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<VlanProbe> terminateDomainRequests(String domainName) {
		log.info("terminateDomainRequests", "Terminating VLAN probe processes for domain " + domainName);

		synchronized (reqsHolder) {
			Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

			for (VlanProbe request : reqsHolder.keySet()) {
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();

				if (domainName.equals(locatedDomain)) {
					hiveApDebugMgmt.terminate(request);
					requests.add(request);
				}
			}

			log.info("terminateDomainRequests", "Terminated number of " + requests.size() + " VLAN probe processes for domain " + domainName + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<VlanProbe> removeRequests() {
		log.info("removeRequests", "Removing overall VLAN probe processes.");

		synchronized (reqsHolder) {
			Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

			for (Iterator<VlanProbe> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				VlanProbe request = reqIter.next();
				hiveApDebugMgmt.terminate(request);
				reqIter.remove();
				requests.add(request);
			}

			log.info("removeRequests", "Removed number of " + requests.size() + " VLAN probe processes. The rest number of VLAN probe processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public VlanProbe removeRequest(int cookieId) {
		log.info("removeRequest", "Removing a VLAN probe process with cookie " + cookieId);
		VlanProbe candidate = null;

		synchronized (reqsHolder) {
			for (Iterator<VlanProbe> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				VlanProbe request = reqIter.next();

				if (request.getCookieId() == cookieId) {
					hiveApDebugMgmt.terminate(request);
					reqIter.remove();
					candidate = request;
					break;
				}
			}

			if (candidate != null) {
				log.info("removeRequest", "Removed a VLAN probe process - " + candidate + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			} else {
				log.warn("removeRequest", "Removed a nonexistent VLAN probe process with cookie " + cookieId + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			}

			return candidate;
		}
	}

	@Override
	public Collection<VlanProbe> removeRequests(String sessionId) {
		log.info("removeRequests", "Removing VLAN probe processes for session " + sessionId);
		Collection<VlanProbe> requests;

		if (sessionId != null && !sessionId.trim().isEmpty()) {
			synchronized (reqsHolder) {
				requests = new ArrayList<VlanProbe>(reqsHolder.size());

				for (Iterator<VlanProbe> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
					VlanProbe request = reqIter.next();

					if (sessionId.equals(request.getSessionId())) {
						hiveApDebugMgmt.terminate(request);
						reqIter.remove();
						requests.add(request);
					}
				}

				log.info("removeRequests", "Removed number of " + requests.size() + " VLAN probe processes for session " + sessionId + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			}
		} else {
			requests = new ArrayList<VlanProbe>(0);
		}

		return requests;
	}

	@Override
	public Collection<VlanProbe> removeGroupRequests(int groupId) {
		log.info("removeGroupRequests", "Removing VLAN probe processes for group " + groupId);

		synchronized (reqsHolder) {
			Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

			for (Iterator<VlanProbe> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				VlanProbe request = reqIter.next();

				if (request.getGroupId() == groupId) {
					hiveApDebugMgmt.terminate(request);
					reqIter.remove();
					requests.add(request);
				}
			}

			log.info("removeGroupRequests", "Removed number of " + requests.size() + " VLAN probe processes for group " + groupId + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public Collection<VlanProbe> removeDomainRequests(String domainName) {
		log.info("removeDomainRequests", "Removing VLAN probe processes for domain " + domainName);

		synchronized (reqsHolder) {
			Collection<VlanProbe> requests = new ArrayList<VlanProbe>(reqsHolder.size());

			for (Iterator<VlanProbe> reqIter = reqsHolder.keySet().iterator(); reqIter.hasNext();) {
				VlanProbe request = reqIter.next();
				HiveAp hiveAp = request.getHiveAp();
				String locatedDomain = hiveAp.getOwner().getDomainName();

				if (domainName.equals(locatedDomain)) {
					hiveApDebugMgmt.terminate(request);
					reqIter.remove();
					requests.add(request);
				}
			}

			log.info("removeDomainRequests", "Removed number of " + requests.size() + " VLAN probe processes for domain " + domainName + ". The rest number of VLAN probe processes: " + reqsHolder.size());
			return requests;
		}
	}

	@Override
	public void changeState(DebugState newState, int cookieId) {
		synchronized (reqsHolder) {
			for (VlanProbe request : reqsHolder.keySet()) {
				if (request.getCookieId() == cookieId) {
					request.changeState(newState);
					return;
				}
			}
		}

		log.warn("changeState", "Could not find out a VLAN probe process with cookie " + cookieId);
	}

	@Override
	public void changeState(DebugState newState, String hiveApMac) {
		synchronized (reqsHolder) {
			for (VlanProbe request : reqsHolder.keySet()) {
				if (request.getHiveApMac().equalsIgnoreCase(hiveApMac)) {
					request.changeState(newState);
				}
			}
		}
	}

	public Map<VlanProbe, List<VlanProbeNotification>> getReqsHolder() {
		return reqsHolder;
	}

}