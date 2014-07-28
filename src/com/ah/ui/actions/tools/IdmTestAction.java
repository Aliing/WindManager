package com.ah.ui.actions.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class IdmTestAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(LibrarySipTestAction.class
			.getSimpleName());
	private static String IDM_RADSEC_MODE = "radsec";
	private static String IDM_AUTH_MODE = "auth";
	private String proxyMode = IDM_RADSEC_MODE;
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("test".equals(operation)) {
				jsonObject = new JSONObject();
				log.info("IdmTestAction execute", "operation:" + operation);
				jsonObject.put("r", testOperation());
				return "json";
			}else if("updateProxyMode".equals(operation)){
				jsonObject = new JSONObject();
				log.info("IdmTestAction execute", "operation:" + operation);
				jsonObject.put("result", prepareServersJson(proxyMode));
				return "json";
			}else{
				prepareDependentObjects(proxyMode);
				return SUCCESS;
			}
			
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_IDM_TEST);
	}
	
	public String globalForward() {
		return null;
	}
	
	private void prepareDependentObjects(String proxyMode) {
		prepareServers(proxyMode);
	}

	private void prepareServers(String proxyMode) {
		
		final SortParams orderParam = getOrderParam();
        if(proxyMode.equalsIgnoreCase(IDM_RADSEC_MODE)){
			String where = "IDMProxy = :s1 and connected = :s2 and ipAddress is not null and managestatus = :s3";
			Object[] values = new Object[] {true , true , HiveAp.STATUS_MANAGED};
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class,  orderParam,
					new FilterParams(where, values), domainId);
			servers = new ArrayList<TextItem>();
			for (HiveAp hiveAp : list) {
				servers.add(new TextItem(hiveAp.getMacAddress(), hiveAp
						.getHostName()));
			}
		}else if(proxyMode.equalsIgnoreCase(IDM_AUTH_MODE)){
			FilterParams filter = getRadiusServerCachingApFilter(true,true,null,null);
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, orderParam,
					filter, domainId);
			for (HiveAp hiveAp : list) {
				if(!hiveAp.isEnableIDMAuthProxy()){
					continue;
				}
				servers.add(new TextItem(hiveAp.getMacAddress(), hiveAp
						.getHostName()));
			}
		}
	}

    private SortParams getOrderParam() {
        return new SortParams("hostName");
    }
	
	private List<JSONObject> prepareServersJson(String proxyMode) throws JSONException {
		JSONObject json = null;
		serversJson = new ArrayList<JSONObject>();
		final SortParams orderParam = getOrderParam();
        if(proxyMode.equalsIgnoreCase(IDM_RADSEC_MODE)){
			String where = "IDMProxy = :s1 and connected = :s2 and ipAddress is not null and managestatus = :s3";
			Object[] values = new Object[] {true , true , HiveAp.STATUS_MANAGED};
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, orderParam,
					new FilterParams(where, values), domainId);
			for (HiveAp hiveAp : list) {
				json = new JSONObject();
				json.put("key", hiveAp.getMacAddress());
				json.put("value", hiveAp.getHostName());
				serversJson.add(json);
			}
		}else if(proxyMode.equalsIgnoreCase(IDM_AUTH_MODE)){
			FilterParams filter = getRadiusServerCachingApFilter(true,true,null,null);
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, orderParam,
					filter, domainId);
			for (HiveAp hiveAp : list) {
				if(!hiveAp.isEnableIDMAuthProxy()){
					continue;
				}
				json = new JSONObject();
				json.put("key", hiveAp.getMacAddress());
				json.put("value", hiveAp.getHostName());
				serversJson.add(json);
			}
		}
		return serversJson;
	}
	
	private List<TextItem> servers;
	private List<JSONObject> serversJson;
	
	private String serverMacAddress;
	
	public String getServerMacAddress() {
		return serverMacAddress;
	}

	public void setServerMacAddress(String serverMacAddress) {
		this.serverMacAddress = serverMacAddress;
	}

	public List<TextItem> getServers(){
		return this.servers;
	}
	
	private String testOperation() throws JSONException {
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(serverMacAddress);
		HiveAp hiveAp = QueryUtil.findBoByAttribute(
				HiveAp.class, "macAddress", serverMacAddress,domainId);
		if (NmsUtil.compareSoftwareVersion(simpleHiveAp.getSoftVer(),"5.1.3.0") >= 0) {
			BeCommunicationEvent response = BeTopoModuleUtil.sendSyncCliRequest(hiveAp,
					new String[] {AhCliFactory.sendIdmTestRequest(proxyMode)}, BeCliEvent.CLITYPE_NORMAL, 35);
			return parseIdmTestResult(response);
		}else {
			return MgrUtil.getUserMessage(
			"error.hiveAp.feature.support.version", MgrUtil
					.getHiveOSDisplayVersion("5.1.3.0"));
		}
	}
	
	public String getProxyMode(){
		return proxyMode;
	}
	
	public void setProxyMode(String proxyMode) {
		this.proxyMode = proxyMode;
	}
	
	public static String parseIdmTestResult(BeCommunicationEvent event) {
		if (null == event) {
			// error.
			log.error("parseIdmTestResult", "the parameter event is null!!");
			return "The device temporarily no response.";
		}
		int msgType = event.getMsgType();
		String msg = "";
		if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT){
			BeCapwapCliResultEvent response = (BeCapwapCliResultEvent) event;
			byte r = response.getResult();

			log.info("parseIdmTestResult","receive IDM test response, result:" + r);
			msg = response.getCliSucceedMessage();
			
			if (null == msg || "".equals(msg)) {
				msg = response.getHiveOSErrorMessage();
			}
			return msg;
			
		}else if (msgType == BeCommunicationConstant.MESSAGETYPE_CLIRSP){
			
			try {
				msg = BeTopoModuleUtil.parseCliRequestResult(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.info("parseIdmTestResult",e);
			}
			return msg;
		}
		
		return "The device temporarily no response.";
	}
	
	public FilterParams getRadiusServerCachingApFilter(
			boolean filterNewAp, boolean filterSimulateAp, Set<Long> apIdScope,
			Set<Long> profileIdScope) {
		List<Short> apModels = new ArrayList<Short>();
		apModels.add(HiveAp.HIVEAP_MODEL_330);
		apModels.add(HiveAp.HIVEAP_MODEL_350);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200);
		apModels.add(HiveAp.HIVEAP_MODEL_BR200_WP);
		List<Short> apStatuses = new ArrayList<Short>();
		apStatuses.add(HiveAp.STATUS_MANAGED);
		if (!filterNewAp) {
			apStatuses.add(HiveAp.STATUS_NEW);
		}
		List<Boolean> apSimulated = new ArrayList<Boolean>();
		apSimulated.add(false);
		if (!filterSimulateAp) {
			apSimulated.add(true);
		}
		List<Short> deviceTypes = new ArrayList<Short>();
		deviceTypes.add(HiveAp.Device_TYPE_BRANCH_ROUTER);

		String where;
		Object[] values;
		if (null == apIdScope || apIdScope.isEmpty()) {
			if (null == profileIdScope || profileIdScope.isEmpty()) {
			where = "manageStatus in (:s1) AND simulated in (:s2) AND ( (enabledBrAsRadiusServer=:s3 "
					+ "AND deviceType in (:s4) AND hiveApModel in (:s5) AND "
					+ "(radiusServerProfile != null OR configTemplate.radiusServerProfile != null)) "
					+ "OR (deviceType=:s6 AND radiusServerProfile != null)  ) AND connected = :s7";
			values = new Object[7];
			values[0] = apStatuses;
			values[1] = apSimulated;
			values[2] = true;
			values[3] = deviceTypes;
			values[4] = apModels;
			values[5] = HiveAp.Device_TYPE_HIVEAP;
			values[6] = true;
		} else {
				where = "manageStatus in (:s1) AND simulated in (:s2) AND ( (enabledBrAsRadiusServer=:s3 "
						+ "AND deviceType in (:s4) AND hiveApModel in (:s5) AND "
						+ "(radiusServerProfile.id in (:s7) OR configTemplate.radiusServerProfile.id in (:s7))) "
						+ "OR (deviceType=:s6 AND radiusServerProfile.id in (:s7))  ) AND connected = :s8";
				values = new Object[8];
				values[0] = apStatuses;
				values[1] = apSimulated;
				values[2] = true;
				values[3] = deviceTypes;
				values[4] = apModels;
				values[5] = HiveAp.Device_TYPE_HIVEAP;
				values[6] = profileIdScope;
				values[7] = true;
			}
		} else {
			if (null == profileIdScope || profileIdScope.isEmpty()) {
			where = "manageStatus in (:s1) AND simulated in (:s2) AND id in (:s3) AND ( (enabledBrAsRadiusServer=:s4 "
					+ "AND deviceType in (:s5) AND hiveApModel in (:s6) AND "
					+ "(radiusServerProfile != null OR configTemplate.radiusServerProfile != null)) "
					+ "OR (deviceType=:s7 AND radiusServerProfile != null) ) AND connected = :s8";
			values = new Object[8];
			values[0] = apStatuses;
			values[1] = apSimulated;
				values[2] = apIdScope;
				values[3] = true;
				values[4] = deviceTypes;
				values[5] = apModels;
				values[6] = HiveAp.Device_TYPE_HIVEAP;
				values[7] = true;
			} else {
				where = "manageStatus in (:s1) AND simulated in (:s2) AND id in (:s3) AND ( (enabledBrAsRadiusServer=:s4 "
						+ "AND deviceType in (:s5) AND hiveApModel in (:s6) AND "
						+ "(radiusServerProfile.id in (:s8) OR configTemplate.radiusServerProfile.id in (:s8))) "
						+ "OR (deviceType=:s7 AND radiusServerProfile.id in (:s8)) ) AND connected = :s9";
				values = new Object[9];
				values[0] = apStatuses;
				values[1] = apSimulated;
				values[2] = apIdScope;
			values[3] = true;
			values[4] = deviceTypes;
			values[5] = apModels;
			values[6] = HiveAp.Device_TYPE_HIVEAP;
				values[7] = profileIdScope;
				values[8] = true;
			}
		}
		return new FilterParams(where, values);
	}
}
