package com.ah.ui.actions.tools;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAAATestEvent;
import com.ah.be.communication.event.BeAAATestResultEvent;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.config.hiveap.UpdateUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class AdLdapTestAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(AdLdapTestAction.class
			.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("test".equals(operation)) {
				log.info("execute", "operation:" + operation + ", option:"
						+ testType);
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
		setSelectedL2Feature(L2_FEATURE_ADLDAP_TEST);
	}

	private void prepareDependentObjects() {
		prepareServers();
	}

	private void prepareServers() {
		servers = RadisuTestAction.getRadiusServersOrProxies(domainId, true);
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
		switch (testType) {
		case TEST_TYPE_AD:
			request.setTestType(BeAAATestEvent.TESTTYPE_NTLM_AUTH);
			request.setUserName(adUsername);
			request.setPassword(adPassword);
			request.setDomain(adDomain);
			break;
		case TEST_TYPE_LDAP:
			request.setTestType(BeAAATestEvent.TESTTYPE_LDAP_SEARCH);
			request.setUserName(ldapUsername);
			request.setDomain(ldapAdDomain);
			request.setBaseDN(ldapBaseDn);
			break;
		case TEST_TYPE_JOIN_DOMAIN:
			request.setTestType(BeAAATestEvent.TESTTYPE_NET_JOIN);
			request.setDomainType(getDomainType(joinDomainAdDomain));
			request.setUserName(joinDomainUsername);
			request.setPassword(joinDomainPassword);
			break;
		default:
			request = null;
		}
		if (null != request) {
			try {
				request.buildPacket();
			} catch (Exception e) {
				log.error("generateRequest", "generate request error.", e);
			}
		}
		return request;
	}

	public static byte getDomainType(String joinDomainAdDomain) {
		if ("primary".equals(joinDomainAdDomain)) {
			return BeAAATestEvent.DOMAINTYPE_PRIMARY;
		} else if ("backup1".equals(joinDomainAdDomain)) {
			return BeAAATestEvent.DOMAINTYPE_BACKUP1;
		} else if ("backup2".equals(joinDomainAdDomain)) {
			return BeAAATestEvent.DOMAINTYPE_BACKUP2;
		} else if ("backup3".equals(joinDomainAdDomain)) {
			return BeAAATestEvent.DOMAINTYPE_BACKUP3;
		} else {
			return BeAAATestEvent.DOMAINTYPE_DEFAULT;
		}
	}

	public static final int TEST_TYPE_AD = 1;
	public static final int TEST_TYPE_LDAP = 2;
	public static final int TEST_TYPE_JOIN_DOMAIN = 3;

	public EnumItem[] getTestType1() {
		return new EnumItem[] { new EnumItem(TEST_TYPE_AD,
				getText("hm.tool.test.aaa.option.ad")) };
	}

	public EnumItem[] getTestType2() {
		return new EnumItem[] { new EnumItem(TEST_TYPE_LDAP,
				getText("hm.tool.test.aaa.option.ldap")) };
	}

	public EnumItem[] getTestType3() {
		return new EnumItem[] { new EnumItem(TEST_TYPE_JOIN_DOMAIN,
				getText("hm.tool.test.aaa.option.joinDomain")) };
	}

	public TextItem[] getJoinDomainAdDomains() {
		return new TextItem[] { new TextItem("", ""),
				new TextItem("primary", "Primary"),
				new TextItem("backup1", "Backup1"),
				new TextItem("backup2", "Backup2"),
				new TextItem("backup3", "Backup3"), };
	}

	private List<TextItem> servers;

	private int testType = TEST_TYPE_AD;
	private String server;
	private String adUsername;
	private String adPassword;
	private String adDomain;
	private String ldapUsername;
	private String ldapAdDomain;
	private String ldapBaseDn;
	private String joinDomainAdDomain;
	private String joinDomainUsername;
	private String joinDomainPassword;

	public List<TextItem> getServers() {
		return servers;
	}

	public int getTestType() {
		return testType;
	}

	public void setTestType(int testType) {
		this.testType = testType;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getAdUsername() {
		return adUsername;
	}

	public void setAdUsername(String adUsername) {
		this.adUsername = adUsername;
	}

	public String getAdPassword() {
		return adPassword;
	}

	public void setAdPassword(String adPassword) {
		this.adPassword = adPassword;
	}

	public String getAdDomain() {
		return adDomain;
	}

	public void setAdDomain(String adDomain) {
		this.adDomain = adDomain;
	}

	public String getLdapUsername() {
		return ldapUsername;
	}

	public void setLdapUsername(String ldapUsername) {
		this.ldapUsername = ldapUsername;
	}

	public String getLdapAdDomain() {
		return ldapAdDomain;
	}

	public void setLdapAdDomain(String ldapAdDomain) {
		this.ldapAdDomain = ldapAdDomain;
	}

	public String getLdapBaseDn() {
		return ldapBaseDn;
	}

	public void setLdapBaseDn(String ldapBaseDn) {
		this.ldapBaseDn = ldapBaseDn;
	}

	public String getJoinDomainAdDomain() {
		return joinDomainAdDomain;
	}

	public void setJoinDomainAdDomain(String joinDomainAdDomain) {
		this.joinDomainAdDomain = joinDomainAdDomain;
	}

	public String getJoinDomainUsername() {
		return joinDomainUsername;
	}

	public void setJoinDomainUsername(String joinDomainUsername) {
		this.joinDomainUsername = joinDomainUsername;
	}

	public String getJoinDomainPassword() {
		return joinDomainPassword;
	}

	public void setJoinDomainPassword(String joinDomainPassword) {
		this.joinDomainPassword = joinDomainPassword;
	}

	public static String requestAAATest(BeAAATestEvent req) {
		if (NmsUtil.compareSoftwareVersion(req.getSimpleHiveAp().getSoftVer(),
				"3.5.1.0") < 0) {
			return MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", MgrUtil
							.getHiveOSDisplayVersion("3.5.1.0"));
		}
		BeCommunicationEvent ev = HmBeCommunicationUtil
				.sendSyncRequest(req, 60);
		return parseAAATestResult(ev);
	}

	public static String parseAAATestResult(BeCommunicationEvent event) {
		if (null == event) {
			// error.
			log.error("parseAAATestResult", "the parameter event is null!!");
			return "Unknow error.";
		}
		int msgType = event.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
			BeCapwapClientEvent response = (BeCapwapClientEvent) event;
			short queryType = response.getQueryType();
			if (queryType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST) {
				BeAAATestEvent res = (BeAAATestEvent) response;
				byte r = res.getResult();
				log.info("parseAAATestResult",
						"receive AAA test response, result:" + r);
				String msg = "";
				if (BeCommunicationConstant.RESULTTYPE_NOFSM == r) {
					msg = MgrUtil.getUserMessage("error.capwap.server.nofsm.ap.radius.test"); // change message for US943
				} else {
					msg = UpdateUtil.getCommonResponseMessage(r);
					if (null == msg || "".equals(msg)) {
						return MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
					msg =  "The request is failure, error message: " + msg;
				}
				return msg;
			}
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
			short resultType = result.getResultType();
			if (resultType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST) {
				BeAAATestResultEvent rlt = (BeAAATestResultEvent) result;
				byte r = rlt.getResultCode();
				String msg = rlt.getMessage();
				if (null == msg || "".equals(msg)) {
					if (r == BeAAATestResultEvent.RESULTCODE_SUCCESS) {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.success.noMessage");
					} else {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
				}
				return msg;
			}
		}
		return "Unknow error.";
	}

}
