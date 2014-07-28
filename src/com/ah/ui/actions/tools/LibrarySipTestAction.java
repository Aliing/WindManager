package com.ah.ui.actions.tools;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAAATestEvent;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class LibrarySipTestAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(LibrarySipTestAction.class
			.getSimpleName());
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("test".equals(operation)) {
				log.info("LibrarySipTestAction execute", "operation:" + operation);
				testOperation();
				return "json";
			}
			prepareDependentObjects();
			return SUCCESS;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_LIBRARY_SIP_TEST);
	}
	
	public String globalForward() {
		return null;
	}
	
	private void prepareDependentObjects() {
		prepareServers();
	}

	private void prepareServers() {
		servers = RadisuTestAction.getRadiusServersOrProxies(domainId, true);
	}
	
	public TextItem[] getJoinDomainSipDomains() {
		return new TextItem[] {new TextItem("primary", "Primary")};
	}
	
	private List<TextItem> servers;
	
	public List<TextItem> getServers(){
		return this.servers;
	}
	
	private void testOperation() throws JSONException {
		jsonObject = new JSONObject();
		jsonObject.put("r", requestAAATest(generateRequest()));
	}
	
	private BeAAATestEvent generateRequest() {
		BeAAATestEvent request = new BeAAATestEvent();
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request
				.setSimpleHiveAp(CacheMgmt.getInstance()
						.getSimpleHiveAp(server));
		request.setTestType(BeAAATestEvent.TEST_TYPE_LIBSIP_TEST);
		request.setDomainType(AdLdapTestAction.getDomainType(joinDomainSipDomain));
		request.setUserName(joinDomainUsername);
		request.setPassword(joinDomainPassword);
		if (null != request) {
			try {
				request.buildPacket();
			} catch (Exception e) {
				log.error("generateRequest", "generate request error.", e);
			}
		}
		return request;
	}
	
	public static String requestAAATest(BeAAATestEvent req) {
		if (NmsUtil.compareSoftwareVersion(req.getSimpleHiveAp().getSoftVer(),
				"3.5.3.0") < 0) {
			return MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", MgrUtil
							.getHiveOSDisplayVersion("3.5.3.0"));
		}
		BeCommunicationEvent ev = HmBeCommunicationUtil
				.sendSyncRequest(req, 60);
		return AdLdapTestAction.parseAAATestResult(ev);
	}
	
	public String getLibrarySipComment(){
		return MgrUtil.getResourceString("hm.tool.test.librarySip.comment");
	}
	private String server;
	private String joinDomainSipDomain;
	private String joinDomainUsername;
	private String joinDomainPassword;
	
	public String getServer(){
		return this.server;
	}
	
	public void setServer(String server){
		this.server = server;
	}
	
	public String getJoinDomainSipDomain(){
		return this.joinDomainSipDomain;
	}
	
	public void setJoinDomainSipDomain(String joinDomainSipDomain){
		this.joinDomainSipDomain = joinDomainSipDomain;
	}
	
	public String getJoinDomainUsername(){
		return this.joinDomainUsername;
	}
	
	public void setJoinDomainUsername(String joinDomainUsername){
		this.joinDomainUsername = joinDomainUsername;
	}
	
	public String getJoinDomainPassword(){
		return this.joinDomainPassword;
	}
	
	public void setJoinDomainPassword(String joinDomainPassword){
		this.joinDomainPassword = joinDomainPassword;
	}
	
}
