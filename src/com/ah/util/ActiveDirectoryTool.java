package com.ah.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeConfigUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAAATestEvent;
import com.ah.be.communication.event.BeAAATestResultEvent;
import com.ah.be.communication.event.BeCapwapClientEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeLDAPTreeInfoEvent;
import com.ah.be.communication.event.BeLDAPTreeInfoResultEvent;
import com.ah.be.communication.event.BeQueryADInfoEvent;
import com.ah.be.communication.event.BeQueryADInfoResultEvent;
import com.ah.be.communication.event.BeRetrieveLDAPInfoEvent;
import com.ah.be.communication.event.BeRetrieveLDAPInfoResultEvent;
import com.ah.be.communication.mo.LDAPTreeInfo;
import com.ah.be.config.create.source.impl.ConfigLazyQueryBo;
import com.ah.be.config.hiveap.UpdateHiveAp;
import com.ah.be.config.hiveap.UpdateObject;
import com.ah.be.config.hiveap.UpdateObjectException;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.hiveap.UpdateUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.IpAddress;
import com.ah.bo.useraccess.ActiveDirectoryDomain;
import com.ah.bo.useraccess.MgmtServiceDnsInfo;
import com.ah.bo.useraccess.TreeNode;

public class ActiveDirectoryTool {
	
	private static final Tracer log = new Tracer(ActiveDirectoryTool.class.getSimpleName());
	
	public static final int TEST_TYPE_JOIN = 1;
	public static final int TEST_TYPE_AUTH = 2;
	public static final String HIVEOS_VERSION = "4.0.1.0";
	public static final String NO_OPTION_KEY = "-1";
	public static final String CHECK_AP_DNS = "1";
	public static final String CHECK_AP_NTP = "2";
	public static final String QUERY_AP_ID_FOR_KEY = "1";
	public static final String QUERY_AP_MAC_FOR_KEY = "2";
	public static final String NO_NODE = "0";
	public static final String HAS_NODE = "1";
	public static final String OBJECT_CLASS_GROUP_AD = "group";
	public static final String OBJECT_CLASS_GROUP_LDAP = "groupOfNames";
	public static final String OBJECT_CLASS_GROUP_OD = "apple-group";
	public static final String RESULT_CODE_SUCCESS = "0";
	public static final String RESULT_CODE_FAILURE = "1";
	private static final String MSG_CANT_CONTACT_LDAP_SERVER = "Can't contact LDAP server";
	
	public static JSONObject retrieveOperation(JSONObject jsonObject,
			String apMacAddress, String dnFullName, boolean dnsApplied) throws JSONException {
		
		if(null==jsonObject)
			jsonObject = new JSONObject();
		
		if (!dnsApplied && !checkApWithDnsOrNtp(apMacAddress, CHECK_AP_DNS)) {
			jsonObject.put("resCode", BeRetrieveLDAPInfoResultEvent.RESULTCODE_FAILURE);
			jsonObject.put("msg", MgrUtil
					.getUserMessage("error.config.hiveAp.have.no.dns"));
/*		} else if (!checkApWithDnsOrNtp(apMacAddress, CHECK_AP_NTP)) {
			jsonObject.put("resCode", BeRetrieveLDAPInfoResultEvent.RESULTCODE_FAILURE);
			jsonObject.put("msg", MgrUtil
					.getUserMessage("error.config.hiveAp.have.no.ntp"));*/
		} else {
			String[] result = requestRetrieve(generateRetrieveRequest(
					apMacAddress, dnFullName));
			jsonObject.put("resCode", result[0]);
			if (Integer.parseInt(result[0]) == 0) {
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.config.retrieveAd.success"));
			} else {
				jsonObject.put("msg", result[1]);
			}
			jsonObject.put("domainName", result[2]);
			jsonObject.put("adServer", result[3]);
			jsonObject.put("baseDN", result[4]);
		}
		
		return jsonObject;
	}

	/**
	 * 
	 * @param jsonObject
	 * @param macAddress
	 * @param testType
	 * 					- <code>TEST_TYPE_JOIN</code> or <code>TEST_TYPE_AUTH</code>
	 * @param args
	 * <li>TEST_TYPE_JOIN: adminName,adminPasswd,domName,fullName,Server,baseDN,computer OU,client LDAP SASL Wrapping</li>
	 * <li>TEST_TYPE_AUTH: bindDnName,bindDnPasswd,domName,fullName,Server,baseDN</li>
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject testAAAOperation(JSONObject jsonObject,
			String apMacAddress, int testType, String... args)
			throws JSONException {
		if (null == jsonObject)
			jsonObject = new JSONObject();
		String[] result = requestAAATest(generateTestRequest(
				apMacAddress, testType, args));
		jsonObject.put("resCode", result[0]);
		if (Integer.parseInt(result[0]) == 0) {
			if (ActiveDirectoryTool.TEST_TYPE_JOIN == testType) {
				// join
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.config.testJoin.success"));
			} else {
				// AUTH
				jsonObject.put("msg", MgrUtil
						.getUserMessage("info.config.testAuth.success"));
			}
		} else {
			jsonObject.put("msg", result[1]);
		}

		return jsonObject;
	}
	
	/**
	 * 
	 * @param jsonObject
	 * @param macAddress
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject queryApOperation(JSONObject jsonObject,
			String apMacAddress) throws JSONException {
		if(null==jsonObject)
			jsonObject = new JSONObject();
		String[] result = requestQueryAp(generateQueryApRequest(apMacAddress));
		jsonObject.put("resCode", result[0]);
		jsonObject.put("msg", result[1]);
		jsonObject.put("fullDomainName", result[2]);
		
		return jsonObject;
	}

	private static BeRetrieveLDAPInfoEvent generateRetrieveRequest(String macAddress,String dnFullName) {
		log.info("generateRetrieveRequest",
				"capwap param: [HiveAp mac Address:" + macAddress
						+ "][FullName:" + dnFullName + "]");
		BeRetrieveLDAPInfoEvent request = new BeRetrieveLDAPInfoEvent();
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request.setSimpleHiveAp(CacheMgmt.getInstance().getSimpleHiveAp(macAddress));
		request.setFullName(dnFullName);
		if (null != request) {
			try {
				request.buildPacket();
			} catch (Exception e) {
				log.error("generateRequest", "generate request error.", e);
			}
		}
		return request;
	}

	private static String[] requestRetrieve(BeRetrieveLDAPInfoEvent req) {
		String[] resultArr = new String[] {
				String.valueOf(BeRetrieveLDAPInfoResultEvent.RESULTCODE_FAILURE), "", "", "", ""};
		if (NmsUtil.compareSoftwareVersion(req.getSimpleHiveAp().getSoftVer(),
				HIVEOS_VERSION) < 0) {
			resultArr[1] = MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", MgrUtil
							.getHiveOSDisplayVersion(HIVEOS_VERSION));
			return resultArr;
		}
		BeCommunicationEvent ev = HmBeCommunicationUtil
				.sendSyncRequest(req, 35);
		parseRetrieveLDAPInfoResult(ev, resultArr);
		return resultArr;
	}

	private static void parseRetrieveLDAPInfoResult(BeCommunicationEvent event,
			String[] resultArr) {
		if (null == event) {
			// error.
			log.error("parseRetrieveLDAPInfoResult", "the parameter event is null!!");
			resultArr[1] = "Unknow error.";
			return;
		}
		int msgType = event.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
			// failure
			BeCapwapClientEvent response = (BeCapwapClientEvent) event;
			short queryType = response.getQueryType();
			if (queryType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO) {
				BeRetrieveLDAPInfoEvent res = (BeRetrieveLDAPInfoEvent) response;
				byte r = res.getResult();
				String msg = "";
				if (BeCommunicationConstant.RESULTTYPE_NOFSM == r
						|| BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT == r
						|| BeCommunicationConstant.RESULTTYPE_TIMEOUT == r) {
					msg = MgrUtil.getUserMessage("error.capwap.server.nofsm.ap.disconnected"); // fix bug 14334
				} else {
					msg = UpdateUtil.getCommonResponseMessage(r);
					log.info("parseRetrieveLDAPInfoResult",
							"receive retrieve LDAP response, result:" + r);
					if (null == msg || "".equals(msg)) {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
					msg = "The request is failure, error message: " + msg;
				}
				resultArr[1] = msg;
				return;
			}
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
			short resultType = result.getResultType();
			if (resultType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RETRIEVELDAPINFO) {
				BeRetrieveLDAPInfoResultEvent rlt = (BeRetrieveLDAPInfoResultEvent) result;
				byte r = (byte)rlt.getResultCode();
				if (r == BeRetrieveLDAPInfoResultEvent.RESULTCODE_SUCCESS) {
					// success
					resultArr[0] = String
							.valueOf(BeRetrieveLDAPInfoResultEvent.RESULTCODE_SUCCESS);
					resultArr[2] = rlt.getDomain();
					resultArr[3] = rlt.getServerIp();
					resultArr[4] = rlt.getBaseDn();
				}
				String msg = ErrorMessageUtil.convertErrorCodeToMessage(rlt.getResultCode(), rlt.getMessage());
				if (null == msg || "".equals(msg)) {
					if (r == BeRetrieveLDAPInfoResultEvent.RESULTCODE_SUCCESS) {
						msg = MgrUtil
								.getUserMessage("info.config.retrieveAd.success");
					} else {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
				}
				resultArr[1] = msg;
				return;
			}
		}
		resultArr[1] = "Unknow error.";
		return;
	}

	private static BeAAATestEvent generateTestRequest(String macAddress,int testType,String... args) {
		String params = "capwap param:" + " [User Name:" + args[0]
				+ "][Password:" + args[1] + "][Domain:" + args[2]
				+ "][Full Name:" + args[3] + "][Server Ip:" + args[4]
				+ "][Base DN:" + args[5] + "]";
		if (args.length > 6 && args[6] != null) {
			params += "[Computer OU:" + args[6] + "]";
		}
		log.info("generateTestRequest", params);
		BeAAATestEvent request = new BeAAATestEvent();
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request.setSimpleHiveAp(CacheMgmt.getInstance().getSimpleHiveAp(macAddress));
		switch (testType) {
		case TEST_TYPE_JOIN:
			request.setTestType(BeAAATestEvent.TEST_TYPE_NETJOIN_TEST);
			request.setUserName(args[0]);
			request.setPassword(args[1]);
			request.setDomain(args[2]);
			request.setRealm(args[3]);
			request.setServer(args[4]);
			request.setBaseDN(args[5]);
			if (args.length > 7 && args[7] != null) {
				request.setCou(args[6]); // computer OU
				request.setLdapSaslWrapping(args[7]); // client LDAP SASL Wrapping
			}
			break;
		case TEST_TYPE_AUTH:
			request.setTestType(BeAAATestEvent.TEST_TYPE_NTLM_AUTH_TEST);
			request.setUserName(getDomainUserWithoutFullName(args[0]));
			request.setPassword(args[1]);
			request.setDomain(args[2]);
			request.setRealm(args[3]);
			request.setServer(args[4]);
			request.setBaseDN(args[5]);
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

	private static String[] requestAAATest(BeAAATestEvent req) {
		String[] resultArr = new String[] {
				String.valueOf(BeAAATestResultEvent.RESULTCODE_FAILURE), "" };
		if (NmsUtil.compareSoftwareVersion(req.getSimpleHiveAp().getSoftVer(),
				HIVEOS_VERSION) < 0) {
			resultArr[1] = MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", MgrUtil
							.getHiveOSDisplayVersion(HIVEOS_VERSION));
			return resultArr;
		}
		BeCommunicationEvent ev = HmBeCommunicationUtil.sendSyncRequest(req, 35);
		parseAAATestResult(ev, resultArr);
		return resultArr;
	}

	private static void parseAAATestResult(BeCommunicationEvent event,
			String[] resultArr) {
		if (null == event) {
			// error.
			log.error("parseAAATestResult", "the parameter event is null!!");
			resultArr[1] = "Unknow error.";
			return;
		}
		int msgType = event.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
			// failure
			BeCapwapClientEvent response = (BeCapwapClientEvent) event;
			short queryType = response.getQueryType();
			if (queryType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST) {
				BeAAATestEvent res = (BeAAATestEvent) response;
				byte r = res.getResult();
				log.info("parseAAATestResult",
						"receive AAA test response, result:" + r);
				String msg = "";
				String operation = "join";
				if (BeAAATestEvent.TEST_TYPE_NTLM_AUTH_TEST == res.getTestType()) {
					// validate user
					operation = "validate";
				}
				if (BeCommunicationConstant.RESULTTYPE_NOFSM == r) {
					msg = MgrUtil.getUserMessage("error.capwap.server.nofsm.ap.disconnected.join.domain", operation); // modify HiveAP disconnected error
				} else if (BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT == r) {
					msg = MgrUtil.getUserMessage("error.hiveAp.update.timeout.noresult.join.domain", operation); // time out
				} else {
					msg = UpdateUtil.getCommonResponseMessage(r);
					if (null == msg || "".equals(msg)) {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
					msg = "The request is failure, error message: " + msg;
				}
				resultArr[1] = msg;
				return;
			}
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
			short resultType = result.getResultType();
			if (resultType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_AAATEST) {
				BeAAATestResultEvent rlt = (BeAAATestResultEvent) result;
				byte r = rlt.getResultCode();
				if (r == BeAAATestResultEvent.RESULTCODE_SUCCESS) {
					// success
					resultArr[0] = String
							.valueOf(BeAAATestResultEvent.RESULTCODE_SUCCESS);
				}
				String msg = ErrorMessageUtil.convertErrorCodeToMessage(rlt.getErrorCode(), rlt.getMessage());
				if (null == msg || "".equals(msg)) {
					if (r == BeAAATestResultEvent.RESULTCODE_SUCCESS) {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.success.noMessage");
					} else {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
				}
				resultArr[1] = msg;
				return;
			}
		}
		resultArr[1] = "Unknow error.";
		return;
	}

	private static BeQueryADInfoEvent generateQueryApRequest(String macAddress) {
		log.info("generateQueryApRequest",
				"capwap param: [HiveAp mac Address:" + macAddress + "]");
		BeQueryADInfoEvent request = new BeQueryADInfoEvent();
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request.setSimpleHiveAp(CacheMgmt.getInstance().getSimpleHiveAp(macAddress));
		if (null != request) {
			try {
				request.buildPacket();
			} catch (Exception e) {
				log.error("generateRequest", "generate request error.", e);
			}
		}
		return request;
	}

	private static String[] requestQueryAp(BeQueryADInfoEvent req) {
		String[] resultArr = new String[] {
				String.valueOf(BeQueryADInfoResultEvent.RESULTCODE_FAILURE), "", ""};
		if (req.getSimpleHiveAp() == null || NmsUtil.compareSoftwareVersion(req.getSimpleHiveAp().getSoftVer(),
				HIVEOS_VERSION) < 0) {
			resultArr[1] = MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", MgrUtil
							.getHiveOSDisplayVersion(HIVEOS_VERSION));
			return resultArr;
		}
		BeCommunicationEvent ev = HmBeCommunicationUtil
				.sendSyncRequest(req, 35);
		parseQueryApResult(ev, resultArr);
		return resultArr;
	}

	private static void parseQueryApResult(BeCommunicationEvent event,
			String[] resultArr) {
		if (null == event) {
			// error.
			log.error("parseQueryApResult", "the parameter event is null!!");
			resultArr[1] = "Unknow error.";
			return;
		}
		int msgType = event.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
			// failure
			BeCapwapClientEvent response = (BeCapwapClientEvent) event;
			short queryType = response.getQueryType();
			if (queryType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_QUERYADINFO) {
				BeQueryADInfoEvent res = (BeQueryADInfoEvent) response;
				byte r = res.getResult();
				String msg = UpdateUtil.getCommonResponseMessage(r);
				log.info("parseQueryApResult",
						"receive query ap response, result:" + r);
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil
							.getUserMessage("info.aaa.test.failed.noMessage");
				}
				resultArr[1] = "The request is failure, error message: " + msg;
				return;
			}
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
			short resultType = result.getResultType();
			if (resultType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_QUERYADINFO) {
				BeQueryADInfoResultEvent rlt = (BeQueryADInfoResultEvent) result;
				byte r = rlt.getResultCode();
				if (r == BeQueryADInfoResultEvent.RESULTCODE_SUCCESS) {
					// success
					resultArr[0] = String
							.valueOf(BeQueryADInfoResultEvent.RESULTCODE_SUCCESS);
					resultArr[2] = rlt.getDomain();
				}
				String msg = rlt.getMessage();
				if (null == msg || "".equals(msg)) {
					if (r == BeQueryADInfoResultEvent.RESULTCODE_SUCCESS) {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.success.noMessage");
					} else {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
				}
				resultArr[1] = msg;
				return;
			}
		}
		resultArr[1] = "Unknow error.";
		return;
	}

	private static final int SEARCH_TYPE_SUBDIR = 1;
	private static final int SEARCH_TYPE_ATTR = 2;
	
	public static JSONArray expandOperation(JSONArray jsonArray,
			JSONObject jsonObject, Long serverId, String dn, Long domainId,
			int selectedDomainId, List<ActiveDirectoryDomain> domainsForTree,
			TreeNode treeInfos, String apMacAddress) throws JSONException {

		String[] serverInfo = getServerInfo(selectedDomainId, domainsForTree);
		Object[] result = requestLdapTreeInfo(generateTreeRequest(
				SEARCH_TYPE_SUBDIR, replaceSpecialChar(dn, HTML_TO_CHAR), null, domainId, serverInfo, apMacAddress));
		if (null == jsonArray) 
			jsonArray = new JSONArray();
		if (null == jsonObject)
			jsonObject = new JSONObject();
		jsonObject.put("resCode", result[0]); // 0:success 1:failure
		jsonObject.put("msg", result[1]);
		jsonArray.put(jsonObject);
		
		if (treeInfos == null)
			treeInfos = new TreeNode();
		TreeNode treeNode = null;
		if (result[2] != null) {
			LDAPTreeInfo treeInfo = (LDAPTreeInfo)result[2];
			if (treeInfo != null && treeInfo.getrDns() != null && !treeInfo.getrDns().isEmpty()){
				int i = 0;
//				dn = replaceSpecialChar(dn, CHAR_TO_HTML); // fix CFD-552
				for (String rdn : treeInfo.getrDns()) {
					rdn = replaceSpecialChar(rdn, CHAR_TO_HTML); // fix CFD-552
					
					jsonObject = new JSONObject();
					jsonObject.put("label", rdn);
					jsonObject.put("serverId", serverId);
					jsonObject.put("dn", rdn + "," + dn);
					jsonArray.put(jsonObject);
					
					// keep tree info
					treeNode = new TreeNode();
					treeNode.setServerId(serverId);
					treeNode.setParentDn(dn);
					treeNode.setLabel(rdn);
					treeNode.setDn(rdn + "," + dn);
					treeNode.setNodeId(treeInfos.getNodeCount());
					treeInfos.setNodeCount(treeInfos.getNodeCount() + 1);
					addNodeToTree(treeInfos, treeNode, i);
					i++;
				}
			}
		}
		return jsonArray;
	}
	
	public static JSONObject getAttributeOperation(JSONObject jsonObject,
			Long serverId, String dn, String attrName, Long domainId,
			int selectedDomainId, List<ActiveDirectoryDomain> domainsForTree, String apMacAddress)
			throws JSONException {
		if (null == jsonObject) 
			jsonObject = new JSONObject();
		String[] serverInfo = getServerInfo(selectedDomainId, domainsForTree);
		Object[] result = requestLdapTreeInfo(generateTreeRequest(
				SEARCH_TYPE_ATTR, replaceSpecialChar(dn, HTML_TO_CHAR), attrName, domainId, serverInfo, apMacAddress));
		jsonObject.put("resCode", result[0]); // 0:success 1:failure
		jsonObject.put("msg", result[1]);
		String attribute = "";
		String objectClass = "";
		boolean isGroupSeleted = false;
		if (result[2] != null) {
			LDAPTreeInfo treeInfo = (LDAPTreeInfo)result[2];
			
			// if group be selected
			List<String> valueList = treeInfo.getAttrValuesByName("objectClass");
			if (valueList != null && !valueList.isEmpty()) {
				String value = "";
				for (int i = 0; i < valueList.size(); i++) {
					value = valueList.get(i);
					if (OBJECT_CLASS_GROUP_AD.equals(value) ||
							OBJECT_CLASS_GROUP_LDAP.equals(value) ||
							OBJECT_CLASS_GROUP_OD.equals(value)) {
						objectClass = value;
						isGroupSeleted = true;
						break;
					}
				}
			}
			
			// if not group be selected
			if (!isGroupSeleted) {
				valueList = treeInfo.getAttrValuesByName(attrName);
				if (valueList != null && !valueList.isEmpty()) {
					for (int i = 0; i < valueList.size(); i++) {
						attribute += valueList.get(i);
						if (i < valueList.size() - 1) {
							attribute += ";";
						}
					}
				}
			}
		}
		
		jsonObject.put("groupAttributeValue", attribute);
		jsonObject.put("objectClassOfGroup", objectClass);
		jsonObject.put("hasNode", result[3]);
		return jsonObject;
	}

	private static BeLDAPTreeInfoEvent generateTreeRequest(int searchType,
			String dn, String attrName, Long domainId, String[] serverInfo, String apMacAddress) {
		String params = "capwap param:" + " [apMacAddress:" + apMacAddress
				+ "][serverAddress:" + serverInfo[1] + "][baseDn:"
				+ serverInfo[2] + "][bindDn:" + serverInfo[3] + "][password:"
				+ serverInfo[4] + "]";
		if (searchType == SEARCH_TYPE_ATTR) {
			params += "[attrName:" + attrName + "]";
		}
		log.info("generateTreeRequest", params);
		BeLDAPTreeInfoEvent request = new BeLDAPTreeInfoEvent();
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request.setSimpleHiveAp(CacheMgmt.getInstance().getSimpleHiveAp(apMacAddress));
		
		// query the information of server
		if(serverInfo == null) {
			log.error("generateRequest", "generate tree request error(Can't retrieve server info from DB).");
			return request;
		}
		String serverAddress = serverInfo[1];
		String baseDn = serverInfo[2];
		String bindDn = serverInfo[3];
		String password = serverInfo[4];
		
		if (dn != null && !"".equals(dn)) {
			// set current node's DN as the baseDn
			baseDn = dn;
		}
		
		request.setBaseDn(baseDn);
		request.setServer(serverAddress);
		request.setBindDn(bindDn);
		request.setPasswd(password);
		switch (searchType) {
		case SEARCH_TYPE_SUBDIR:
			request.setSearchType(BeLDAPTreeInfoEvent.SEARCH_TYPE_SUBDIRECTORY);
			break;
		case SEARCH_TYPE_ATTR:
			request.setSearchType(BeLDAPTreeInfoEvent.SEARCH_TYPE_ATTRIBUTES);
			request.setAttribute(attrName);
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

	public static Object[] requestLdapTreeInfo(BeLDAPTreeInfoEvent req) {
		Object[] resultArr = new Object[] {
				String.valueOf(BeLDAPTreeInfoResultEvent.RESULTCODE_FAILURE), "", null, "", "", ""};
		if (req.getSimpleHiveAp() == null) {
			resultArr[1] = MgrUtil.getUserMessage("info.config.no.hiveAP.radius.server");
			return resultArr;
		}
		if (NmsUtil.compareSoftwareVersion(req.getSimpleHiveAp().getSoftVer(),
				HIVEOS_VERSION) < 0) {
			resultArr[1] = MgrUtil.getUserMessage(
					"error.hiveAp.feature.support.version", MgrUtil
							.getHiveOSDisplayVersion(HIVEOS_VERSION));
			return resultArr;
		}
		BeCommunicationEvent ev = HmBeCommunicationUtil
				.sendSyncRequest(req, 35);
		parseTreeResult(ev, resultArr);
		return resultArr;
	}

	public static void parseTreeResult(BeCommunicationEvent event,
			Object[] resultArr) {
		if (null == event) {
			// error.
			log.error("parseTreeResult", "the parameter event is null!!");
			resultArr[1] = "Unknow error.";
			return;
		}
		int msgType = event.getMsgType();
		if (msgType == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {
			// failure
			BeCapwapClientEvent response = (BeCapwapClientEvent) event;
//			short queryType = response.getQueryType();
//			if (queryType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO) {
//				BeLDAPTreeInfoEvent res = (BeLDAPTreeInfoEvent) response;
//				byte r = res.getResult(); // fix bug 18278
			if (response != null) {
				byte r = response.getResult();
				String msg = UpdateUtil.getCommonResponseMessage(r);
				log.info("parseTreeResult",
						"receive retrieve LDAP response, result:" + r);
				if (null == msg || "".equals(msg)) {
					msg = MgrUtil
							.getUserMessage("info.aaa.test.failed.noMessage");
				}
				resultArr[1] = "The request is failure, error message: " + msg;
				return;
			}
		} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent result = (BeCapwapClientResultEvent) event;
			short resultType = result.getResultType();
			if (resultType == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LDAPTREEINFO) {
				BeLDAPTreeInfoResultEvent rlt = (BeLDAPTreeInfoResultEvent) result;
				byte r = rlt.getResultCode();
				if (r == BeLDAPTreeInfoResultEvent.RESULTCODE_SUCCESS) {
					// success
					resultArr[0] = String
							.valueOf(BeLDAPTreeInfoResultEvent.RESULTCODE_SUCCESS);
					resultArr[2] = rlt.getTreeInfo();
					
					// for tree node refresh, node exist on the server
					resultArr[3] = HAS_NODE;
				} else {
					// for tree node refresh, node not exist on the server, so will be delete from the page
					resultArr[3] = NO_NODE;
				}
				String msg = rlt.getMessage();
				if (null == msg || "".equals(msg)) {
					if (r == BeLDAPTreeInfoResultEvent.RESULTCODE_SUCCESS) {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.success.noMessage");
					} else {
						msg = MgrUtil
								.getUserMessage("info.aaa.test.failed.noMessage");
					}
				} else if (msg.indexOf(MSG_CANT_CONTACT_LDAP_SERVER) != -1) {
					// redefine the msg
					msg = MgrUtil.getUserMessage("error.cannot.contact.ad.ldap.directory.server");
				} else if (msg.indexOf("4 Size limit exceeded") != -1) {
					resultArr[0] = -2;
					msg = MgrUtil.getUserMessage("error.config.tree.node.users.count.exceeded");
				}
				resultArr[1] = msg;
				return;
			}
		}
		resultArr[1] = "Unknow error.";
		return;
	}

	public static List<TextItem> getApList(Long domainId, String keyType, String filter) {
		
		String where = "simulated = :s1 and connected = :s2 " +
				" and hiveApModel != :s3 " +
				" and (deviceType = :s4 or deviceType = :s5 or deviceType = :s6 or deviceType = :s7) ";
		Object[] values = new Object[] {false, true, HiveAp.HIVEAP_MODEL_BR100,
				HiveAp.Device_TYPE_HIVEAP, 
				HiveAp.Device_TYPE_BRANCH_ROUTER, 
				HiveAp.Device_TYPE_VPN_BR,
				HiveAp.Device_TYPE_SWITCH};
		if (null != filter && !"".equals(filter)) {
			where += " and hostName like :s8";
			values = new Object[] {false, true, HiveAp.HIVEAP_MODEL_BR100,
					HiveAp.Device_TYPE_HIVEAP, 
					HiveAp.Device_TYPE_BRANCH_ROUTER, 
					HiveAp.Device_TYPE_VPN_BR, 
					HiveAp.Device_TYPE_SWITCH,
					filter+"%"};
		}
		SortParams sort = new SortParams("hostName");
		List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, sort,
				new FilterParams(where, values), domainId);

		List<TextItem> servers = new ArrayList<TextItem>();
		for (HiveAp hiveAp : list) {
			if (NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),
					HIVEOS_VERSION) >= 0) {
				if (QUERY_AP_ID_FOR_KEY.equals(keyType)) {
					servers.add(new TextItem(hiveAp.getId().toString(), hiveAp
							.getHostName()));
				} else if (QUERY_AP_MAC_FOR_KEY.equals(keyType)) {
					servers.add(new TextItem(hiveAp.getMacAddress(), hiveAp
							.getHostName()));
				}
			}
		}
		
		// None available
		if (servers.size() == 0) {
			servers.add(new TextItem(NO_OPTION_KEY, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		} else {
			//servers.add(0, new TextItem("-1", ""));
		}
		
		return servers;
	}
	
	public static String getApListString(Long domainId, String keyType, String filter) {
		List<TextItem> getApList = getApList(domainId, keyType, filter);
		
		StringBuffer result = new StringBuffer();
		TextItem ti = null;
		for (int i = 0; i < getApList.size(); i++) {
			ti = getApList.get(i);
			result.append("{");
			result.append("\"name\":\"");
			result.append(ti.getValue());
			result.append("\",");
			result.append("\"id\":\"");
			result.append(ti.getKey());
			result.append("\"");
			result.append("},");
		}
		result.deleteCharAt(result.length() - 1); // delete the last ','
		return result.toString();
	}
	
	public static String getApMacAddress(Long id) {
		String apMacAddress = null;
		HiveAp ap = QueryUtil.findBoById(HiveAp.class, id,
				new ConfigLazyQueryBo());
		if (ap != null) {
			apMacAddress = ap.getMacAddress();
		}
		return apMacAddress;
	}
	
	public static boolean checkApWithDnsOrNtp(String macAddress, String checkType) {
		/*HiveAp ap = QueryUtil.findBoById(HiveAp.class, id,
				new ConfigLazyQueryBo());*/
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", macAddress,
				new ConfigLazyQueryBo());
		if (ap.getConfigTemplate() != null) {
			ConfigTemplate configTemplate = QueryUtil.findBoById(
					ConfigTemplate.class, ap.getConfigTemplate().getId(),
					new ConfigLazyQueryBo());
			if (CHECK_AP_DNS.equals(checkType) && configTemplate.getMgmtServiceDns() != null) {
				return true;
			} else if (CHECK_AP_NTP.equals(checkType) && configTemplate.getMgmtServiceTime() != null) {
				return true;
			}
		}

		return false;
	}
/*TODO	
	public static void setServerInfo(int selectedDomainId,
			List<ActiveDirectoryDomain> domainsForTree, Object[] result) {
		
		for (ActiveDirectoryDomain domain : domainsForTree) {
			if (selectedDomainId == domain.getDomainId()) {
				domain.setFullName(result[4] != null ? result[4].toString() : "");
				domain.setBasedN(result[5] != null ? result[5].toString() : "");
				
				String bindDn = domain.getBindDnName(); // bindDn
				if (!"".equals(domain.getFullName())
						&& bindDn.indexOf(domain.getFullName()) == -1) {
					bindDn += "@";
					bindDn += domain.getFullName();
				}
				domain.setBindDnName(bindDn);
				break;
			}
		}
	}*/
	
	public static String[] getServerInfo(int selectedDomainId,
			List<ActiveDirectoryDomain> domainsForTree) {
		String[] retValues = null;
		for (ActiveDirectoryDomain domain : domainsForTree) {
			if (selectedDomainId == domain.getDomainId()) {
				retValues = new String[5];
				retValues[1] = domain.getServer(); // serverAddress
				retValues[2] = domain.getBasedN(); // baseDn
				retValues[3] = domain.getBindDnName(); // bindDn
				retValues[4] = domain.getBindDnPass(); // bindDn password
				
				break;
			}
		}
		return retValues;
	}
	
	private static void addNodeToTree(TreeNode treeInfos, TreeNode newNode, int i) {
		for (TreeNode treeNode : treeInfos.getTreeNodes()) {
			if (!newNode.getServerId().equals(treeNode.getServerId())) {
				continue;
			}
			if (newNode.getParentDn().equals(treeNode.getDn())) {
				// do this only when the first child node come
				if (i == 0) {
					// set parent node status to be load complete.
					treeNode.setDynamicLoadComplete(true);
					// delete old children
					treeNode.setTreeNodes(new ArrayList<TreeNode>());
				}
				newNode.setParentId(treeNode.getNodeId());
				treeNode.getTreeNodes().add(newNode);
				return;
			}
			if (treeNode.getTreeNodes() != null && treeNode.getTreeNodes().size() > 0) {
				addNodeToTree(treeNode, newNode, i);
			}
		}
	}
	
	public static void removeNode(Long serverId,
			String dn, TreeNode treeInfos, boolean removeNodeSelf) {
		for (TreeNode treeNode : treeInfos.getTreeNodes()) {
			if (!serverId.equals(treeNode.getServerId())) {
				continue;
			}
			if (dn.equals(treeNode.getDn())) {
				
				// set parent node status to be load complete.
				treeNode.setDynamicLoadComplete(false);
				// delete children
				treeNode.setTreeNodes(new ArrayList<TreeNode>());
				if (removeNodeSelf) {
					//delete node self
					treeInfos.getTreeNodes().remove(treeNode);
				}
				return;
			}
			if (treeNode.getTreeNodes() != null
					&& treeNode.getTreeNodes().size() > 0) {
				removeNode(serverId, dn, treeNode, removeNodeSelf);
			}
		}
	}
	
	public static JSONObject removeNodeFromTree(JSONObject jsonObject,
			Long serverId, String dn, Long domainId, int selectedDomainId,
			List<ActiveDirectoryDomain> domainsForTree, TreeNode treeInfos, String apMacAddress)
			throws JSONException {

		boolean removeNodeSelf = false;
		jsonObject = getAttributeOperation(jsonObject, serverId, dn, "",
				domainId, selectedDomainId, domainsForTree, apMacAddress);
		String hasNode = jsonObject.get("hasNode") != null ? jsonObject
				.get("hasNode").toString() : null;
		if (NO_NODE.equals(hasNode)) {
			removeNodeSelf = true;
		}
		jsonObject.put("removeNodeSelf", removeNodeSelf);
		dn = replaceSpecialChar(dn, CHAR_TO_HTML);
		removeNode(serverId, dn, treeInfos, removeNodeSelf);
		return jsonObject;
	}
	
	public static String addFullNameToBindDn(String bindDn, String fullName) {
		// if bindDn is not the format like : HE@WORLD.COM HE\\WORLD, append @WORLD.COM to it
		if (bindDn.indexOf(fullName) == -1 && bindDn.indexOf("\\") == -1) {
			bindDn += "@";
			bindDn += fullName;
		}
		return bindDn;
	}
	
	public static String getDomainUserWithoutFullName(String bindDn) {
		// if bindDn is end with full name, delete the full name
		if (bindDn != null && bindDn.indexOf("@") != -1) {
			bindDn = bindDn.substring(0, bindDn.indexOf("@"));
		}
		return bindDn;
	}
	
	public static String getBaseDnFromFullName(String fullName) {
		String baseDn = null;
		if(fullName != null) {
			if (fullName.indexOf("DC=") != -1 || fullName.indexOf("dc=") != -1) {
				baseDn = fullName;
			} else {
				String[] baseDnArr = fullName.split("\\.");
				if(baseDnArr != null && baseDnArr.length > 0){
					StringBuffer baseDnBuf = new StringBuffer();
					for(int i = 0; i < baseDnArr.length; i++) {
						if (!"".equals(baseDnArr[i])) {
							if (i > 0) {
								baseDnBuf.append(",");
							}
							baseDnBuf.append("dc=");
							baseDnBuf.append(baseDnArr[i]);
						}
					}
					baseDn = baseDnBuf.toString();
				}
			}
		}
		return baseDn;
	}
	
	public final static short CHAR_TO_HTML = 1;
	public final static short HTML_TO_CHAR = 2;

	/**
	 * escape special char returned AP
	 * char returned from AP: ["] in fact is [\\"], [\] in fact is [\\\\]
	 * 
	 *            char returned from AP
	 * # -> &#35  \\#
	 * ; -> &#59  \\;
	 * ' -> &#39  '
	 * " -> &#34  \\"
	 * \ -> &#92  \\\\
	 * < -> &#60  \\<
	 * > -> &#62  \\>
	 * + -> &#43  \\+
	 * = -> &#61  \\=
	 * , -> &#44  \\,
	 * 
	 * @param src
	 * @return
	 */
	public static String replaceSpecialChar(String src, short type) {
		
		if (!StringUtils.isBlank(src)) {
			if (CHAR_TO_HTML == type) { // escape
				if (src.indexOf("\\#") >= 0) {
					src = src.replaceAll("\\\\#", "&#35;"); // must put as the first one
				}
				if (src.indexOf("\\;") >= 0) {
					src = src.replaceAll("\\\\;", "&#59;");
				}
				if (src.indexOf("'") >= 0) {
					src = src.replaceAll("'", "&#39;");
				}
				if (src.indexOf("\"") >= 0) {
					src = src.replaceAll("\\\\\"", "&#34;");
				}
				if (src.indexOf("\\\\") >= 0) {
					src = src.replaceAll("\\\\\\\\", "&#92;");
				}
				if (src.indexOf("\\<") >= 0) {
					src = src.replaceAll("\\\\<", "&#60;");
				}
				if (src.indexOf("\\>") >= 0) {
					src = src.replaceAll("\\\\>", "&#62;");
				}
				if (src.indexOf("\\+") >= 0) {
					src = src.replaceAll("\\\\\\+", "&#43;");
				}
				if (src.indexOf("\\=") >= 0) {
					src = src.replaceAll("\\\\=", "&#61;");
				}
				if (src.indexOf("\\,") >= 0) {
					src = src.replaceAll("\\\\,", "&#44;");
				}
			} else {
				if (src.indexOf("&#35;") >= 0) {
					src = src.replaceAll("&#35;", "\\\\#");
				}
				if (src.indexOf("&#59;") >= 0) {
					src = src.replaceAll("&#59;", "\\\\;");
				}
				if (src.indexOf("&#39;") >= 0) {
					src = src.replaceAll("&#39;", "'");
				}
				if (src.indexOf("&#34;") >= 0) {
					src = src.replaceAll("&#34;", "\\\\\"");
				}
				if (src.indexOf("&#92;") >= 0) {
					src = src.replaceAll("&#92;", "\\\\\\\\");
				}
				if (src.indexOf("&#60;") >= 0) {
					src = src.replaceAll("&#60;", "\\\\<");
				}
				if (src.indexOf("&#62;") >= 0) {
					src = src.replaceAll("&#62;", "\\\\>");
				}
				if (src.indexOf("&#43;") >= 0) {
					src = src.replaceAll("&#43;", "\\\\+");
				}
				if (src.indexOf("&#61;") >= 0) {
					src = src.replaceAll("&#61;", "\\\\=");
				}
				if (src.indexOf("&#44;") >= 0) {
					src = src.replaceAll("&#44;", "\\\\,");
				}
			}
		}
		return src;
	}
	
	public static Object[] getApIpDns(String apMac){
		Object[] apInfo = new Object[5];
		
		HiveAp ap = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", apMac,
				new ConfigLazyQueryBo());
		if (ap != null) {
			apInfo[0] = ap.isDhcp();
			if (ap.isDhcp()) {
				apInfo[1] = ap.getIpAddress();
				apInfo[2] = ap.getNetmask();
				apInfo[3] = ap.getGateway();
			} else {
				apInfo[1] = ap.getCfgIpAddress();
				apInfo[2] = ap.getCfgNetmask();
				apInfo[3] = ap.getCfgGateway();
			}
			if (ap.getConfigTemplate() != null) {
				ConfigTemplate configTemplate = QueryUtil.findBoById(
						ConfigTemplate.class, ap.getConfigTemplate().getId(),
						new ConfigLazyQueryBo());
				if (configTemplate.getMgmtServiceDns() != null) {
					List<MgmtServiceDnsInfo> dnsInfo = configTemplate.getMgmtServiceDns().getDnsInfo();
					if (dnsInfo != null && !dnsInfo.isEmpty()) {
						IpAddress ip = dnsInfo.get(0).getIpAddress();
						if (ip != null && ip.getItems() != null && !ip.getItems().isEmpty()) {
							apInfo[4] = ip.getItems().get(0).getIpAddress();
						}
					}
				}
			}
		}
		
		return apInfo;
	}
	
	public static String objToString(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}
	

	/* pushConfigToAp start */
	/**
	 * 
	 * @param jsonObject
	 * @param domainId
	 * @param args
	 *            : parameters specified like this:
	 *            arg[0]-apMac,arg[1]-ipAddress,
	 *            arg[2]-netmask,arg[3]-gateway,arg[4]-dnsServerIp
	 * @return
	 * @throws Exception
	 */
	public static JSONObject pushConfigToAp(JSONObject jsonObject,
			Long domainId, String... args) throws Exception {
		
		if(jsonObject == null) {
			jsonObject = new JSONObject();
		}
		
		String[] ret = generateUpdateList(domainId, args);
		if (RESULT_CODE_SUCCESS.equals(ret[0])) {
			jsonObject.put("resCode", RESULT_CODE_SUCCESS);
		} else {
			jsonObject.put("resCode", RESULT_CODE_FAILURE);
//			jsonObject.put("msg", ret[1]);
			jsonObject.put("msg", MgrUtil.getUserMessage("error.config.hiveAp.with.staticip.dns.failed"));
		}
		
		return jsonObject;
	}
	
	/**
	 * 
	 * @param domainId
	 * @param args
	 * @return
	 * @throws Exception
	 */
	private static String[] generateUpdateList(Long domainId, String... args) throws Exception {
		String[] ret = new String[]{RESULT_CODE_SUCCESS, ""};
		
		/*
		 * generate update HIVEAP
		 */
		HiveAp currentAp = QueryUtil.findBoByAttribute(HiveAp.class, "macAddress", args[0],
					new ConfigLazyQueryBo());
		UpdateHiveAp updHiveAp = null;
		try {
			UpdateObject updObj = HmBeConfigUtil.getUpdateObjectBuilder()
					.getApIpDnsUpdateObject(currentAp, args[1], args[2],
							args[3], args[4]);
			updHiveAp = new UpdateHiveAp();
			updHiveAp.setHiveAp(currentAp);
			updHiveAp.setUpdateType(UpdateParameters.AH_DOWNLOAD_IP_NETMASK_GATEWAY_DNS);
			updHiveAp.setWithReboot(false);
			updHiveAp.setAutoProvision(false);
			updHiveAp.addUpdateObject(updObj);
		} catch (UpdateObjectException e) {
			ret[0] = RESULT_CODE_FAILURE;
			ret[1] = e.getMessage();
			return ret;
		}
		if (updHiveAp.getRemainUpdateObjectCount() == 0) {
			ret[0] = RESULT_CODE_FAILURE;
			ret[1] = MgrUtil.getUserMessage("error.hiveap.config.item.notfound");
			return ret;
		}
		
		/*
		 * add update HiveAp into queue
		 */
		if (updHiveAp != null) {
			if (updHiveAp.getHiveAp().getManageStatus() == HiveAp.STATUS_NEW) {
				if (updHiveAp.getHiveAp().isSimulated()) {
					String error = BeTopoModuleUtil.isDomainAllowManageSimHiveAP(
							domainId, 1);
					if (null != error && !"".equals(error.trim())) {
						ret[0] = RESULT_CODE_FAILURE;
						ret[1] = error;
						return ret;
					}
				} else {
					String error = BeTopoModuleUtil.isDomainAllowManageRealHiveAP(
							domainId, 1, updHiveAp.getHiveAp().getHiveApModel() == HiveAp.HIVEAP_MODEL_VPN_GATEWAY_VA);
					if (null != error && !"".equals(error.trim())) {
						ret[0] = RESULT_CODE_FAILURE;
						ret[1] = error;
						return ret;
					}
				}
			}
			List<UpdateHiveAp> updateHiveAps = new ArrayList<UpdateHiveAp>(1);
			updateHiveAps.add(updHiveAp);
			List<String[]> errList = HmBeConfigUtil.getUpdateManager().addUpdateObjects(
					updateHiveAps);
			if (null != errList && errList.size() > 0) {
				ret[0] = RESULT_CODE_FAILURE;
				ret[1] = errList.get(0)[1];
				return ret;
			}
		}
		
		return ret;
	}
	/* pushConfigToAp end */
	
	public static void main(String[] args) {
		
		/**
		 * escape special char
		 * 
		 * # -> &#35  \\#
		 * ; -> &#59  \\;
		 * ' -> &#39  '
		 * " -> &#34  \\"
		 * \ -> &#92  \\\\
		 * < -> &#60  \\<
		 * > -> &#62  \\>
		 * + -> &#43  \\+
		 * = -> &#61  \\=
		 * , -> &#44  \\,
		 * 
		 */
		String specialChar = "";
		specialChar = "\\#\\;'\\\"\\\\000\\<\\>\\+\\=\\,";
		System.out.println(specialChar);
		specialChar = replaceSpecialChar(specialChar, CHAR_TO_HTML);
		System.out.println(specialChar);
		specialChar = replaceSpecialChar(specialChar, HTML_TO_CHAR);
		System.out.println(specialChar);
		
	}
}
