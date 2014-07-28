package com.ah.hm.ws.platform.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.context.ServiceContext;
import org.apache.axis2.context.SessionContext;
import org.apache.axis2.service.Lifecycle;
import org.apache.axis2.transport.http.HTTPConstants;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.HmLogConst;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.admin.RemoteProcessCallSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.hm.ws.platform.bo.ap.DeviceGroup_type1;
import com.ah.hm.ws.platform.bo.ap.Model_type1;
import com.ah.hm.ws.platform.bo.ap.Type_type1;
import com.ah.hm.ws.platform.core.Passport;
import com.ah.hm.ws.platform.core.Session;
import com.ah.hm.ws.platform.service.HmService;
import com.ah.hm.ws.platform.service.SoapFault;
import com.ah.util.Tracer;
import com.ah.util.devices.impl.Device;

public class HmServiceImpl implements HmService, Lifecycle {

	private static final Tracer log = new Tracer(HmServiceImpl.class, HmLogConst.M_WS);

	public static final String CONTEXT_KEY_USER = "user";
	
	@Override
	public synchronized Session login(Passport passport) throws SoapFault {
		String remoteAddress = getRemoteAddress();
		log.info("login", "Received a login request from " + remoteAddress);
		MessageContext messageContext = MessageContext.getCurrentMessageContext();
		SessionContext sessionContext = messageContext.getSessionContext();
		Passport oldPassport = (Passport) sessionContext.getProperty(CONTEXT_KEY_USER);

		if (oldPassport == null) {
			if (passport == null) {
				throw new SoapFault("Invalid argument - " + passport);
			}

			// Authenticate client.
			RemoteProcessCallSettings config = authenticate(passport);
			log.info("login", "User {" + passport.getUserName() + "} logged in to HM.");

			// Set session timeout.
			int sessionTimeout = passport.getSessionTimeout();

			if (sessionTimeout < RemoteProcessCallSettings.MIN_OVERTIME * 60 || sessionTimeout > RemoteProcessCallSettings.MAX_OVERTIME * 60) {
				log.warn("login", "Invalid session timeout {" + sessionTimeout + "} given. Using default '" + (config.getTimeout() * 60) + "' instead.");
				sessionTimeout = config.getTimeout() * 60;
			}

			HttpServletRequest request = (HttpServletRequest) messageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
			HttpSession httpSession = request.getSession();
			httpSession.setMaxInactiveInterval(sessionTimeout);

			// Keep user information into this session.
			sessionContext.setProperty(CONTEXT_KEY_USER, passport);
		} else {
			log.info("login", "User must have accomplished a successful authentication previously using the same session.");
		}

		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
		String version = versionInfo.getMainVersion() + "r" + versionInfo.getSubVersion();
		Session session = new Session();
		session.setVersion(version);

		return session;
	}

	@Override
	public synchronized void logout() throws SoapFault {
		String remoteAddress = getRemoteAddress();
		log.info("logout", "Received a logout request from " + remoteAddress);
		MessageContext messageContext = MessageContext.getCurrentMessageContext();
		SessionContext sessionContext = messageContext.getSessionContext();
		Passport passport = (Passport) sessionContext.getProperty(CONTEXT_KEY_USER);

		if (passport != null) {
			String userName = passport.getUserName();
			sessionContext.removeProperty(CONTEXT_KEY_USER);
			log.info("logout", "User {" + userName + "} logged out of HM.");
		} else {
			log.warn("logout", "Session was invalid or user has already signed out.");
		}
	}

	@Override
	public void keepSessionAlive() throws SoapFault {
		checkSessionValidation();

		String remoteAddress = getRemoteAddress();
		log.info("keepSessionAlive", "Received a session keep-alive request from " + remoteAddress);

		MessageContext messageContext = MessageContext.getCurrentMessageContext();
		SessionContext sessionContext = messageContext.getSessionContext();
 		Passport passport = (Passport) sessionContext.getProperty(CONTEXT_KEY_USER);

		if (passport != null) {
			String userName = passport.getUserName();
			log.info("keepSessionAlive", "User {" + userName + "} kept session alive.");
		}
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchAllHiveAps() throws SoapFault {
		checkSessionValidation();

		long startTime = System.currentTimeMillis();
		String remoteAddress = getRemoteAddress();
		log.info("fetchAllHiveAps", "[" + remoteAddress + "]Fetching all HiveAPs.");

		try {
			List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("simulated = :s1 and hiveApModel in (:s2)", new Object[] { false, AhConstantUtil.getTeacherViewSupportDevices() }));
			Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> returnedAps = new ArrayList<com.ah.hm.ws.platform.bo.ap.HiveAp>(hiveAps.size());

			for (HiveAp hiveAp : hiveAps) {
				com.ah.hm.ws.platform.bo.ap.HiveAp returnedAp = createAp(hiveAp);
				returnedAps.add(returnedAp);
			}

			long endTime = System.currentTimeMillis();
			log.info("fetchAllHiveAps", "[" + remoteAddress + "]It took " + (endTime - startTime) + "ms to fetch all HiveAPs.");
			return returnedAps.toArray(new com.ah.hm.ws.platform.bo.ap.HiveAp[returnedAps.size()]);
		} catch (Exception e) {
			log.error("fetchAllHiveAps", "Failed to fetch all HiveAPs.", e);
			throw new SoapFault("Due to an internal error, HiveManager was unable to process the operation submitted.");
		}
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByNodeIds(String[] nodeIds) throws SoapFault {
		checkSessionValidation();

		if (nodeIds == null || nodeIds.length == 0) {
			throw new SoapFault("Invalid argument.");
		}

		Collection<String> nodeIdList = new ArrayList<String>(nodeIds.length);

		for (String nodeId : nodeIds) {
			if (nodeId == null || nodeId.trim().isEmpty()) {
				throw new SoapFault("One of HiveAP node IDs in the argument is invalid.");
			}

			nodeIdList.add(nodeId.toUpperCase());
		}

		long startTime = System.currentTimeMillis();
		String remoteAddress = getRemoteAddress();
		log.info("fetchHiveApsByNodeIds", "[" + remoteAddress + "]Fetching HiveAPs based on node IDs - " + nodeIdList);

		try {
			Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> hiveAps = getHiveApsByNodeIds(nodeIdList);
			long endTime = System.currentTimeMillis();
			log.info("fetchHiveApsByNodeIds", "[" + remoteAddress + "]It took " + (endTime - startTime) + "ms to fetch specified HiveAPs.");
			return hiveAps.toArray(new com.ah.hm.ws.platform.bo.ap.HiveAp[hiveAps.size()]);
		} catch (Exception e) {
			log.error("fetchHiveApsByNodeIds", "Failed to fetch specified HiveAPs.", e);
			throw new SoapFault("Due to an internal error, HiveManager was unable to process the operation submitted.");
		}
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp fetchHiveApByNodeId(String nodeId) throws SoapFault {
		com.ah.hm.ws.platform.bo.ap.HiveAp[] hiveAps = fetchHiveApsByNodeIds(new String[] { nodeId } );
		return hiveAps != null && hiveAps.length > 0 ? hiveAps[0] : null;
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByClientMacs(String[] clientMacs) throws SoapFault {
		checkSessionValidation();

		if (clientMacs == null || clientMacs.length == 0) {
			throw new SoapFault("Invalid argument.");
		}

		Collection<String> clientMacList = new ArrayList<String>(clientMacs.length);

		for (String clientMac : clientMacs) {
			if (clientMac == null || clientMac.trim().isEmpty()) {
				throw new SoapFault("One of client MACs in the argument is invalid.");
			}

			clientMacList.add(clientMac.toUpperCase());
		}

		long startTime = System.currentTimeMillis();
		String remoteAddress = getRemoteAddress();
		log.info("fetchHiveApsByClientMacs", "[" + remoteAddress + "]Fetching HiveAPs based on client MACs - " + clientMacList);

		StringBuilder where = new StringBuilder();
		where.append("clientMac in (");

		for (int i = 0; i < clientMacs.length; i++) {
			where.append("?");

			if (i != clientMacs.length - 1) {
				where.append(",");
			}
		}

		where.append(") and connectstate = ? and simulated = ?");

		Collection<Object> params = new ArrayList<Object>(3);
		params.addAll(clientMacList);
		params.add(AhClientSession.CONNECT_STATE_UP);
		params.add(false);

		try {
			List<?> activeClientAttrList = DBOperationUtil.executeQuery("select clientMac, apMac from ah_clientsession", null, new FilterParams(where.toString(), params.toArray()));
			Map<String, com.ah.hm.ws.platform.bo.ap.HiveAp> clientMacAndApMap = new HashMap<String, com.ah.hm.ws.platform.bo.ap.HiveAp>(activeClientAttrList.size());

			if (!activeClientAttrList.isEmpty()) {
				Collection<String> nodeIds = new ArrayList<String>(activeClientAttrList.size());
				Map<String, String> clientMacAndNodeIdMap = new HashMap<String, String>(activeClientAttrList.size());

				for (Object obj : activeClientAttrList) {
					Object[] activeClientAttrs = (Object[]) obj;
					String clientMac = (String) activeClientAttrs[0];
					String nodeId = (String) activeClientAttrs[1];

					if (!nodeIds.contains(nodeId)) {
						nodeIds.add(nodeId);
					}

					clientMacAndNodeIdMap.put(clientMac, nodeId);
				}

				Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> hiveAps = getHiveApsByNodeIds(nodeIds);
				Map<String, com.ah.hm.ws.platform.bo.ap.HiveAp> nodeIdAndApMap = new HashMap<String, com.ah.hm.ws.platform.bo.ap.HiveAp>(hiveAps.size());

				for (com.ah.hm.ws.platform.bo.ap.HiveAp hiveAp : hiveAps) {
					String nodeId = hiveAp.getNodeId();
					nodeIdAndApMap.put(nodeId, hiveAp);
				}

				for (String clientMac : clientMacAndNodeIdMap.keySet()) {
					String nodeId = clientMacAndNodeIdMap.get(clientMac);
					com.ah.hm.ws.platform.bo.ap.HiveAp hiveAp = nodeIdAndApMap.get(nodeId);

					if (hiveAp != null) {
						clientMacAndApMap.put(clientMac, hiveAp);
					}
				}
			}

			Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> returnedHiveAps = new ArrayList<com.ah.hm.ws.platform.bo.ap.HiveAp>(clientMacList.size());

			for (String clientMac : clientMacList) {
				com.ah.hm.ws.platform.bo.ap.HiveAp hiveAp = clientMacAndApMap.get(clientMac);
				returnedHiveAps.add(hiveAp);
			}

			long endTime = System.currentTimeMillis();
			log.info("fetchHiveApsByClientMacs", "[" + remoteAddress + "]It took " + (endTime - startTime) + "ms to fetch specified HiveAPs.");
			return returnedHiveAps.toArray(new com.ah.hm.ws.platform.bo.ap.HiveAp[returnedHiveAps.size()]);
		} catch (Exception e) {
			log.error("fetchHiveApsByClientMacs", "Failed to fetch specified HiveAPs.", e);
			throw new SoapFault("Due to an internal error, HiveManager was unable to process the operation submitted.");
		}
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp fetchHiveApByClientMac(String clientMac) throws SoapFault {
		com.ah.hm.ws.platform.bo.ap.HiveAp[] hiveAps = fetchHiveApsByClientMacs(new String[] { clientMac } );
		return hiveAps != null && hiveAps.length > 0 ? hiveAps[0] : null;
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByClientNames(String[] clientNames) throws SoapFault {
		return fetchHiveApsByClientNamesWithCase(clientNames, false);
	}


	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp[] fetchHiveApsByClientNamesWithCase(String[] clientNames, boolean caseSensitive) throws SoapFault {
		checkSessionValidation();

		if (clientNames == null || clientNames.length == 0) {
			throw new SoapFault("Invalid argument.");
		}
		
		List<String> destClientNames = new ArrayList<String>();
		for(String clientName : clientNames){
			if(caseSensitive){
				destClientNames.add(clientName);
			}else{
				destClientNames.add(clientName.toLowerCase());
			}
		}

		long startTime = System.currentTimeMillis();
		String remoteAddress = getRemoteAddress();
		log.info("fetchHiveApsByClientNames", "[" + remoteAddress + "]Fetching HiveAPs based on client names - " + destClientNames);

		StringBuilder where = new StringBuilder();
		if(caseSensitive){
			where.append("clientUsername in (");
		}else{
			where.append("lower(clientUsername) in (");
		}
		

		for (int i = 0; i < destClientNames.size(); i++) {
			where.append("?");

			if (i != destClientNames.size() - 1) {
				where.append(",");
			}
		}

		where.append(") and connectstate = ? and simulated = ?");

		Collection<Object> params = new ArrayList<Object>(3);
		params.addAll(destClientNames);
		params.add(AhClientSession.CONNECT_STATE_UP);
		params.add(false);

		try {
			String querySql = "select lower(clientUsername), apMac from ah_clientsession";
			if( caseSensitive ){
				querySql = "select clientUsername, apMac from ah_clientsession";
			}
			
			List<?> activeClientAttrList = DBOperationUtil.executeQuery(querySql, null, new FilterParams(where.toString(), params.toArray()));
			Map<String, com.ah.hm.ws.platform.bo.ap.HiveAp> clientNameAndApMap = new HashMap<String, com.ah.hm.ws.platform.bo.ap.HiveAp>(activeClientAttrList.size());

			if (!activeClientAttrList.isEmpty()) {
				Collection<String> nodeIds = new ArrayList<String>(activeClientAttrList.size());
				Map<String, String> clientNameAndNodeIdMap = new HashMap<String, String>(activeClientAttrList.size());

				for (Object obj : activeClientAttrList) {
					Object[] activeClientAttrs = (Object[]) obj;
					String clientName = ((String) activeClientAttrs[0]);
					String nodeId = (String) activeClientAttrs[1];

					if (!nodeIds.contains(nodeId)) {
						nodeIds.add(nodeId);
					}

					clientNameAndNodeIdMap.put(clientName, nodeId);
				}

				Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> hiveAps = getHiveApsByNodeIds(nodeIds);
				Map<String, com.ah.hm.ws.platform.bo.ap.HiveAp> nodeIdAndApMap = new HashMap<String, com.ah.hm.ws.platform.bo.ap.HiveAp>(hiveAps.size());

				for (com.ah.hm.ws.platform.bo.ap.HiveAp hiveAp : hiveAps) {
					String nodeId = hiveAp.getNodeId();
					nodeIdAndApMap.put(nodeId, hiveAp);
				}

				for (String clientName : clientNameAndNodeIdMap.keySet()) {
					String nodeId = clientNameAndNodeIdMap.get(clientName);
					com.ah.hm.ws.platform.bo.ap.HiveAp hiveAp = nodeIdAndApMap.get(nodeId);

					if (hiveAp != null) {
						clientNameAndApMap.put(clientName, hiveAp);
					}
				}
			}

			Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> returnedHiveAps = new ArrayList<com.ah.hm.ws.platform.bo.ap.HiveAp>(destClientNames.size());

			for (String clientName : destClientNames) {
				com.ah.hm.ws.platform.bo.ap.HiveAp hiveAp = clientNameAndApMap.get(clientName);
				returnedHiveAps.add(hiveAp);
			}

			long endTime = System.currentTimeMillis();
			log.info("fetchHiveApsByClientNames", "[" + remoteAddress + "]It took " + (endTime - startTime) + "ms to fetch specified HiveAPs.");
			return returnedHiveAps.toArray(new com.ah.hm.ws.platform.bo.ap.HiveAp[returnedHiveAps.size()]);
		} catch (Exception e) {
			log.error("fetchHiveApsByClientNames", "Failed to fetch specified HiveAPs.", e);
			throw new SoapFault("Due to an internal error, HiveManager was unable to process the operation submitted.");
		}
	}

	@Override
	public com.ah.hm.ws.platform.bo.ap.HiveAp fetchHiveApByClientName(String clientName) throws SoapFault {
		com.ah.hm.ws.platform.bo.ap.HiveAp[] hiveAps = fetchHiveApsByClientNames(new String[] { clientName } );
		return hiveAps != null && hiveAps.length > 0 ? hiveAps[0] : null;
	}

	@Override
	public String[] getVendorNamesByMacOuis(String[] macOuis) throws SoapFault {
		if (macOuis == null || macOuis.length == 0) {
			throw new SoapFault("Invalid argument.");
		}

		Collection<String> vendorNames = new ArrayList<String>(macOuis.length);

		for (String macOui : macOuis) {
			if (macOui == null || macOui.trim().isEmpty()) {
				throw new SoapFault("One of MAC OUI in the argument is invalid.");
			}

			String vendorName = AhConstantUtil.getMacOuiComName(macOui.toUpperCase());
			vendorName = macOui + (vendorName != null ? vendorName : "");
			vendorNames.add(vendorName);
		}

		return vendorNames.toArray(new String[vendorNames.size()]);
	}

	@Override
	public String getVendorNameByMacOui(String macOui) throws SoapFault {
		String[] vendorNames = getVendorNamesByMacOuis(new String[] { macOui } );
		return vendorNames != null && vendorNames.length > 0 ? vendorNames[0] : null;
	}

	// This method will be called when a session start.
	@Override
	public void init(ServiceContext serviceContext) throws AxisFault {
		if (log.getLogger().isDebugEnabled()) {
			log.debug("init", "Initialized a new session for web service. EPR Address: " + serviceContext.getMyEPR(Constants.TRANSPORT_HTTPS).getAddress() + "; Name: " + serviceContext.getName());
		}
	}

	// This method will be called when a session finishes.
	@Override
	public void destroy(ServiceContext serviceContext) {
		if (log.getLogger().isDebugEnabled()) {
			try {
				log.debug("destroy", "Destroyed a session for web service. EPR Address: " + serviceContext.getMyEPR(Constants.TRANSPORT_HTTPS).getAddress() + "; Name: " + serviceContext.getName());
			} catch (AxisFault axisFault) {
				log.error("destroy", "Error occurred in getting EPR when destroying a session.", axisFault);
			}
		}
	}

	protected RemoteProcessCallSettings authenticate(Passport passport) throws SoapFault {
		RemoteProcessCallSettings config;

		try {
			config = QueryUtil.findBoByAttribute(RemoteProcessCallSettings.class, "enabled", true);
		} catch (Exception e) {
			log.error("authenticate", "WS authentication failed.", e);
			throw new SoapFault("Due to an internal error, HiveManager was unable to process the operation submitted.");
		}

		if (config == null || !config.isEnabled()) {
			throw new SoapFault("HiveManager web service has not been enabled yet.");
		}

		if (!config.getUserName().equalsIgnoreCase(passport.getUserName())) {
			throw new SoapFault("Invalid account.");
		}

		if (!config.getPassword().equalsIgnoreCase(passport.getPassword())) {
			throw new SoapFault("Invalid credential.");
		}

		return config;
	}

	protected void checkSessionValidation() throws SoapFault {
		MessageContext messageContext = MessageContext.getCurrentMessageContext();
		SessionContext sessionContext = messageContext.getSessionContext();
		Passport passport = (Passport) sessionContext.getProperty(CONTEXT_KEY_USER);

		if (passport == null) {
			throw new SoapFault("Session was invalid or might have already expired.");
		}
	}

	protected String getRemoteAddress(HttpServletRequest request) {
		String remoteAddress = request.getHeader("x-forwarded-for");

		if (remoteAddress == null || remoteAddress.isEmpty() || "unknown".equalsIgnoreCase(remoteAddress)) {
			remoteAddress = request.getRemoteAddr();
		}

		return remoteAddress;
	}

	protected String getRemoteAddress() {
		MessageContext messageContext = MessageContext.getCurrentMessageContext();
		HttpServletRequest request = (HttpServletRequest) messageContext.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);

		return request != null ? getRemoteAddress(request) : null;
	}

	private Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> getHiveApsByNodeIds(Collection<String> nodeIds) {
		Collection<com.ah.hm.ws.platform.bo.ap.HiveAp> wsHiveAps;

		if (!nodeIds.isEmpty()) {
			List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null, new FilterParams("macAddress in (:s1) and simulated = :s2 and hiveApModel in (:s3)", new Object[] { nodeIds, false, AhConstantUtil.getTeacherViewSupportDevices()}));
	        wsHiveAps = new ArrayList<com.ah.hm.ws.platform.bo.ap.HiveAp>(hiveAps.size());

			for (HiveAp hiveAp : hiveAps) {
				com.ah.hm.ws.platform.bo.ap.HiveAp wsHiveAp = createAp(hiveAp);
				wsHiveAps.add(wsHiveAp);
			}
		} else {
			wsHiveAps = new ArrayList<com.ah.hm.ws.platform.bo.ap.HiveAp>(0);
		}

		return wsHiveAps;
	}

	private com.ah.hm.ws.platform.bo.ap.HiveAp createAp(HiveAp hiveAp) {
		com.ah.hm.ws.platform.bo.ap.HiveAp ap = new com.ah.hm.ws.platform.bo.ap.HiveAp();
		ap.setNodeId(hiveAp.getMacAddress());
		ap.setSerialNumber(hiveAp.getSerialNumber());
		ap.setHostName(hiveAp.getHostName());
		ap.setIpAddress(hiveAp.getIpAddress());
		ap.setNetmask(hiveAp.getNetmask());
		ap.setGateway(hiveAp.getGateway());
		ap.setSoftwareVersion(hiveAp.getSoftVer());
		ap.setNativeVlan(hiveAp.getNativeVlan());
		ap.setMgtVlan(hiveAp.getMgtVlan());
		ap.setActive(hiveAp.isConnected());

		DeviceGroup_type1 deviceGroup;

		switch (hiveAp.getManageStatus()) {
			case HiveAp.STATUS_MANAGED:
				deviceGroup = DeviceGroup_type1.MANAGED;
				break;
			case HiveAp.STATUS_NEW:
			default:
				deviceGroup = DeviceGroup_type1.NEW;
				break;
		}

		ap.setDeviceGroup(deviceGroup);

		Model_type1 model;

		switch (hiveAp.getHiveApModel()) {
			case HiveAp.HIVEAP_MODEL_20:
				model = Model_type1.AP_20;
				break;
			case HiveAp.HIVEAP_MODEL_28:
				model = Model_type1.AP_28;
				break;
			case HiveAp.HIVEAP_MODEL_110:
				model = Model_type1.AP_110;
				break;
			/*
			case HiveAp.HIVEAP_MODEL_121:
				model = Model_type1.AP_121;
				break;
			case HiveAp.HIVEAP_MODEL_141:
				model = Model_type1.AP_141;
				break;
			case HiveAp.HIVEAP_MODEL_170:
				model = Model_type1.AP_170;
				break;*/
			case HiveAp.HIVEAP_MODEL_320:
				model = Model_type1.AP_320;
				break;
			case HiveAp.HIVEAP_MODEL_330:
				model = Model_type1.AP_330;
				break;
			case HiveAp.HIVEAP_MODEL_340:
				model = Model_type1.AP_340;
				break;
			case HiveAp.HIVEAP_MODEL_350:
				model = Model_type1.AP_350;
				break;
			case HiveAp.HIVEAP_MODEL_380:
				model = Model_type1.AP_380;
				break;
		//	case HiveAp.HIVEAP_MODEL_BR100:
		//		model = Model_type1.BR_100;
		//		break;
		//	case HiveAp.HIVEAP_MODEL_BR200:
		//		model = Model_type1.BR_200;
		//		break;
		//	case HiveAp.HIVEAP_MODEL_BR200_WP:
		//		model = Model_type1.BR_200_WP;
		//		break;
			case HiveAp.HIVEAP_MODEL_120:
			default:
				model = Model_type1.AP_120;
				break;
		}

		ap.setModel(model);

		Type_type1 type;

		switch (hiveAp.getHiveApType()) {
			case HiveAp.HIVEAP_TYPE_MP:
				type = Type_type1.MESH_POINT;
				break;
			case HiveAp.HIVEAP_TYPE_PORTAL:
			default:
				type = Type_type1.PORTAL;
				break;
		}

		ap.setType(type);

		return ap;
	}

}