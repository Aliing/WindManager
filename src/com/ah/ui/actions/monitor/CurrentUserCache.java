package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeTopoUtil;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.performance.BePerformModule;
import com.ah.be.ts.TsModule;
import com.ah.be.ts.hiveap.impl.HiveApDebugMgmtImpl;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbe;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeMgmt;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeNotification;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAdminLoginSession;
import com.ah.bo.performance.AhUserLoginSession;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.Tracer;
import com.ericdaugherty.sshwebproxy.SshSession;

/*
 * @author Fisher
 */
public class CurrentUserCache implements HttpSessionBindingListener, QueryBo {

	private static final Tracer log = new Tracer(CurrentUserCache.class.getSimpleName());

	private final ConcurrentMap<String, HttpSession> activeSessions;

	private final ConcurrentMap<String, Long> sessionStartTimeMap;

	private static CurrentUserCache currentUserCache;

	private CurrentUserCache() {
		activeSessions = new ConcurrentHashMap<>();
		sessionStartTimeMap = new ConcurrentHashMap<>();
	}

	public synchronized static CurrentUserCache getInstance() {
		if (currentUserCache == null) {
			currentUserCache = new CurrentUserCache();
		}

		return currentUserCache;
	}

	public Collection<HttpSession> getActiveSessions() {
		return activeSessions.values();
	}

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		log.info("valueBound", "Bound event: " + event.getName());
		HttpSession session = event.getSession();
		String sessionId = session.getId();
		long loginTime = System.currentTimeMillis();

//		try {
//			HmUser sessionUser = QueryUtil.findBoById(HmUser.class, Long.parseLong(event.getName()));
//
//			if (sessionUser != null) {
//				sessionUser.setLoginCount(sessionUser.getLoginCount()+1);
//				sessionUser.setLastLoginTime(sysTime);
//				QueryUtil.updateBo(sessionUser);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		sessionStartTimeMap.put(sessionId, loginTime);
		activeSessions.put(sessionId, session);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		log.info("valueUnbound", "Unbound event: " + event.getName());
		HttpSession session = event.getSession();
		String sessionId = session.getId();
		Long loginTime = sessionStartTimeMap.remove(sessionId);
		activeSessions.remove(sessionId);
		boolean isSlave = HAUtil.isSlave();

		if (loginTime != null && !isSlave) {
			long logoutTime = System.currentTimeMillis();
			long durationTime = logoutTime - loginTime;

			if (durationTime >= 0) {
				try {
					/*-
					 HmUser sessionUser = QueryUtil.findBoById(HmUser.class, Long.parseLong(event.getName()));

					 if (sessionUser.getDefaultFlag()) {
						 logoutSession.setTimeZone(sessionUser.getTimeZone());
						 logoutSession.setEmailAddress(sessionUser.getEmailAddress());
						 logoutSession.setLoginTime(sessionStartTimeMap.get(session.getId()));
						 logoutSession.setLogoutTime(sysTime);
						 logoutSession.setUserName(sessionUser.getUserName());
						 logoutSession.setUserFullName(sessionUser.getUserFullName());
						 logoutSession.setOwner(sessionUser.getOwner());
						 logoutSession.setTotalLoginTime(sysTime - sessionStartTimeMap.get(session.getId()));

						 long apCount = QueryUtil.findRowCount(HiveAp.class, new FilterParams(
								 "manageStatus = :s1 and owner.id=:s2",
								 new Object[] { HiveAp.STATUS_MANAGED, sessionUser.getOwner().getId() }));
						 logoutSession.setApCount(apCount);
						 QueryUtil.createBo(logoutSession);
					 }*/

					List<?> logoutUsers = QueryUtil.executeQuery("select bo.defaultFlag, bo.owner, bo.userName, bo.userFullName, bo.emailAddress, bo.timeZone from " + HmUser.class.getSimpleName() + " as bo", null, new FilterParams("bo.id", Long.parseLong(event.getName())), 1);

					if (!logoutUsers.isEmpty()) {
						Object[] loguserAttrs = (Object[]) logoutUsers.get(0);
						Boolean isDefault = (Boolean) loguserAttrs[0];
						HmDomain hmDomain = (HmDomain) loguserAttrs[1];
						String userName = (String) loguserAttrs[2];
						String userFullName = (String) loguserAttrs[3];
						String emailAddress = (String) loguserAttrs[4];
						String timeZone = (String) loguserAttrs[5];

						if (isDefault) {
							AhAdminLoginSession logoutSession = new AhAdminLoginSession();
							logoutSession.setOwner(hmDomain);
							logoutSession.setUserName(userName);
							logoutSession.setUserFullName(userFullName);
							logoutSession.setEmailAddress(emailAddress);
							logoutSession.setTimeZone(timeZone);
							logoutSession.setLoginTime(loginTime);
							logoutSession.setLogoutTime(logoutTime);
							logoutSession.setTotalLoginTime(durationTime);

							long managedApCount = QueryUtil.findRowCount(HiveAp.class, new FilterParams(
									"manageStatus = :s1 and owner = :s2 and simulated = :s3",
									new Object[] { HiveAp.STATUS_MANAGED, hmDomain, false }));
							logoutSession.setApCount(managedApCount);
						//	QueryUtil.createBo(logoutSession);
							List<AhAdminLoginSession> boList = new ArrayList<>(1);
							boList.add(logoutSession);
							BulkUpdateUtil.bulkInsertForAdminLoginSession(boList);
						}
						// add all user login info to database
						AhUserLoginSession logoutSession = new AhUserLoginSession();
						logoutSession.setOwner(hmDomain);
						logoutSession.setUserName(userName);
						logoutSession.setUserFullName(userFullName);
						logoutSession.setEmailAddress(emailAddress);
						logoutSession.setTimeZone(timeZone);
						logoutSession.setLoginTime(loginTime);
						logoutSession.setLogoutTime(logoutTime);
						logoutSession.setTotalLoginTime(durationTime);
					//	QueryUtil.createBo(logoutSession);
						List<AhUserLoginSession> boList = new ArrayList<>(1);
						boList.add(logoutSession);
						BulkUpdateUtil.bulkInsertForUserLoginSession(boList);
					}
				} catch (Exception e) {
					log.error("valueUnbound", "Create logout information error.", e);
				}
			}
		}

		if (HmBeTopoUtil.getPollingController() != null) {
		    HmBeTopoUtil.getPollingController().removeContainer(session);
		}

		// Close SSH channels opened from this session.
		closeSshChannels(session);

		// Remove device trouble shooting processes created from this session.
		removeDeviceTsProcesses(session);
		// Remove presence tracking sensor for this session.
		removePresenceTrackingSensor(session);
	//	DashboardAction.removeUnusedMonitorPage();
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmUserGroup) {
			HmUserGroup userGroup = (HmUserGroup) bo;

			if (userGroup.getFeaturePermissions() != null) {
				userGroup.getFeaturePermissions().size();
			}

			if (userGroup.getInstancePermissions() != null) {
				userGroup.getInstancePermissions().size();
			}
		}

		return null;
	}

	public void invalidateSession(String sessionId) {
		log.info("invalidateSession", "Invalidating session by ID - " + sessionId);
		HttpSession session = activeSessions.remove(sessionId);

		if (session != null) {
			try {
				session.invalidate();
				log.info("invalidateSession", "Session [" + session + "] invalidated.");
			} catch (IllegalStateException ise) {
				log.error("invalidateSession", "Session [" + session + "] already invalidated.", ise);
			}
		}
	}

	public void invalidateSessions(Collection<Long> userIds) {
		for (Long userId : userIds) {
			for (Iterator<HttpSession> sessionIter = activeSessions.values().iterator(); sessionIter.hasNext();) {
				HttpSession session = sessionIter.next();
				HmUser hmUser;

				try {
					hmUser = (HmUser) session.getAttribute(SessionKeys.USER_CONTEXT);
				} catch (Exception e) {
					log.error("invalidateSessions", "Session [" + session + "] already invalidated.", e);
					sessionIter.remove();
					continue;
				}

				if (hmUser != null && hmUser.getId().equals(userId)) {
					// Invalidate session containing the HM user was removed.
					sessionIter.remove();

					try {
						session.invalidate();
					} catch (IllegalStateException ise) {
						log.error("invalidateSessions", "Session [" + session + "] already invalidated.", ise);
					}

					break;
				}
			}
		}
	}

	public void invalidateAllSessions() {
		log.info("invalidateSessions", "Invalidating all sessions.");

		for (Iterator<HttpSession> sessions = activeSessions.values().iterator(); sessions.hasNext();) {
			HttpSession session = sessions.next();
			sessions.remove();

			try {
				session.invalidate();
			} catch (IllegalStateException ise) {
				log.error("invalidateAllSessions", "Session [" + session + "] already invalidated.", ise);
			}
		}

		log.info("invalidateSessions", "All sessions invalidated.");
	}

	public void updateSessionAttribute(Long domainId) {
		//Collection<Long> uids = new ArrayList<>(activeSessions.size());
		Set<Long> userGroupIds = new HashSet<Long>();
		/*-
		for (HttpSession session : activeSessions) {
			HmUser user = (HmUser) session.getAttribute(SessionKeys.USER_CONTEXT);

			if (user != null && user.getOwner().getId().equals(domainId)) {
				List<?> userGroups = QueryUtil.executeQuery("select bo.userGroup from " + HmUser.class.getSimpleName() + " as bo", null, new FilterParams("bo.id", user.getId()));

				if (!userGroups.isEmpty()) {
					HmUserGroup userGroup = (HmUserGroup) userGroups.get(0);
					user.setUserGroup(userGroup);
				}
			}
		}*/

		for (Iterator<HttpSession> sessionIter = activeSessions.values().iterator(); sessionIter.hasNext();) {
			HttpSession session = sessionIter.next();
			HmUser user;

			try {
				user = (HmUser) session.getAttribute(SessionKeys.USER_CONTEXT);
			} catch (Exception e) {
				log.error("updateSessionAttribute", "Session [" + session + "] already invalidated.", e);
			//	activeSessions.remove(session);
				sessionIter.remove();
				continue;
			}

			if (user != null && user.getOwner().getId().equals(domainId)) {
				/*-
				Long uid = user.getId();

				if (!uids.contains(uid)) {
					uids.add(uid);
				}*/
				HmUserGroup userGroup = user.getUserGroup();
				if(null == userGroup){
					log.error(String.format("login user %s has no user group, why???, so cannot update its map permission ", user.getEmailAddress()));
					continue;
				}
				userGroupIds.add(userGroup.getId());
			}
		}

		/*-
		if (!uids.isEmpty()) {
			StringBuilder inClauseBufForUids = new StringBuilder();

			for (Iterator<Long> uidIter = uids.iterator(); uidIter.hasNext();) {
				Long uid = uidIter.next();
				inClauseBufForUids.append(uid);

				if (uidIter.hasNext()) {
					inClauseBufForUids.append(",");
				}
			}

			List<?> hmUserAttrs = QueryUtil.executeNativeQuery("select id, group_id from hm_user where id in (" + inClauseBufForUids.toString() + ")");

			if (!hmUserAttrs.isEmpty()) {
				Map<Long, Long> uidAndGidMap = new HashMap<>(hmUserAttrs.size());
				Collection<Long> gids = new ArrayList<>(hmUserAttrs.size());

				for (Object obj : hmUserAttrs) {
					Object[] userAttrs = (Object[]) obj;
					Long uid = ((BigInteger) userAttrs[0]).longValue();
					Long gid = ((BigInteger) userAttrs[1]).longValue();
					uidAndGidMap.put(uid, gid);

					// Cumulative user group ids.
					if (!gids.contains(gid)) {
						gids.add(gid);
					}
				}

				Map<Long, HmUserGroup> uidAndGroupMap = new HashMap<>(uidAndGidMap.size());
				List<HmUserGroup> queriedUserGroups = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams("id", gids), null, this);

				for (HmUserGroup queriedUserGroup : queriedUserGroups) {
					Long queriedGid = queriedUserGroup.getId();

					for (Long uid : uidAndGidMap.keySet()) {
						Long cachedGid = uidAndGidMap.get(uid);

						if (queriedGid.equals(cachedGid)) {
							uidAndGroupMap.put(uid, queriedUserGroup);
						}
					}
				}

				for (Iterator<HttpSession> sessionIter = activeSessions.values().iterator(); sessionIter.hasNext();) {
					HttpSession session = sessionIter.next();
					HmUser user;

					try {
						user = (HmUser) session.getAttribute(SessionKeys.USER_CONTEXT);
					} catch (Exception e) {
						log.error("updateSessionAttribute", "Session [" + session + "] already invalidated.", e);
						sessionIter.remove();
						continue;
					}

					if (user != null && user.getOwner().getId().equals(domainId)) {
						Long uid = user.getId();
						HmUserGroup updatedUserGroup = uidAndGroupMap.get(uid);

						if (updatedUserGroup != null) {
							user.setUserGroup(updatedUserGroup);
						}
					}
				}
			}
		}*/
		
		if(!userGroupIds.isEmpty()){
			Map<Long, HmUserGroup> groupMap = new HashMap<Long, HmUserGroup>();
			List<HmUserGroup> queriedUserGroups = QueryUtil.executeQuery(HmUserGroup.class, null, new FilterParams("id", userGroupIds), null, this);
			for (HmUserGroup queriedUserGroup : queriedUserGroups) {
				groupMap.put(queriedUserGroup.getId(), queriedUserGroup);
			}
			for (Iterator<HttpSession> sessionIter = activeSessions.values().iterator(); sessionIter.hasNext();) {
				HttpSession session = sessionIter.next();
				HmUser user;

				try {
					user = (HmUser) session.getAttribute(SessionKeys.USER_CONTEXT);
				} catch (Exception e) {
					log.error("updateSessionAttribute", "Session [" + session + "] already invalidated.", e);
					sessionIter.remove();
					continue;
				}

				if (user != null && user.getOwner().getId().equals(domainId)) {
					HmUserGroup userGroup = user.getUserGroup();
					if(null != userGroup){
						HmUserGroup updatedUserGroup = groupMap.get(userGroup.getId());
						if (updatedUserGroup != null) {
							user.setUserGroup(updatedUserGroup);
						}
					}
				}
			}
		}
	}

	private void closeSshChannels(HttpSession session) {
		SshSession sshSession = new SshSession(session);
		sshSession.closeSshChannels();
		sshSession.removeChannelMap();
	}

	private void removeDeviceTsProcesses(HttpSession session) {
		String sessionId = session.getId();
		log.info("removeDeviceTsProcesses", "Removing device trouble shooting processes for session " + sessionId);
		TsModule tsModule = AhAppContainer.getBeTsModule();

		if (tsModule != null) {
			HiveApDebugMgmtImpl deviceTsMgmt = tsModule.getHiveApDebugMgmt();

			if (deviceTsMgmt != null) {
				VlanProbeMgmt<VlanProbe, VlanProbeNotification> vlanProbeMgmt = deviceTsMgmt.getVlanProbeMgmt();

				if (vlanProbeMgmt != null) {
					vlanProbeMgmt.removeRequests(sessionId);
				}
			}
		}
	}
	
	private void removePresenceTrackingSensor(HttpSession session) {
		String sessionId = session.getId();
		log.info("removePresenceTrackingSensor",
				"Removing presence tracking sensor for session " + sessionId);
		BePerformModule pfModule = AhAppContainer.getBePerformModule();

		if (null != pfModule) {
			pfModule.getBePresenceProcessor().removeSensorDataListener(
					sessionId);
		}
	}

}