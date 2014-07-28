package com.ah.ui.actions.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.communication.event.BeAAATestEvent;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.ui.actions.BaseAction;

import com.ah.util.EnumItem;
import com.ah.util.HiveApUtils;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class RadisuTestAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(RadisuTestAction.class
			.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("test".equals(operation)) {
				log.info("execute", "operation:" + operation);
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
		setSelectedL2Feature(L2_FEATURE_RADIUS_TEST);
	}

	private void testOperation() throws JSONException {
		jsonObject = new JSONObject();
		jsonObject.put("r", AdLdapTestAction.requestAAATest(generateRequest()));
	}

	private BeAAATestEvent generateRequest() {
		BeAAATestEvent request = new BeAAATestEvent();
		request.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		request
				.setSimpleHiveAp(CacheMgmt.getInstance()
						.getSimpleHiveAp(client));
		switch (testType) {
		case TEST_TYPE_AUTH:
			request.setTestType(BeAAATestEvent.TESTTYPE_RADIUS_TEST);
			request.setDomain(serverInput);
			request.setUserName(username);
			request.setPassword(password);
			request.setAccounting(BeAAATestEvent.ACCOUNTING_NOCHECK);
			break;
		case TEST_TYPE_ACCT:
			request.setTestType(BeAAATestEvent.TESTTYPE_RADIUS_TEST);
			request.setDomain(serverInput);
			request.setAccounting(BeAAATestEvent.ACCOUNTING_CHECK);
			break;
		default:
			request = null;
		}

		try {
			request.buildPacket();
		} catch (Exception e) {
			log.error("generateRequest", "generate request error.", e);
		}
		return request;
	}

	private void prepareDependentObjects() {
		prepareServers();
		prepareClients();
	}

	private void prepareServers() {
		servers = getRadiusServersOrProxies(domainId, false);
	}

	/**
	 * get devices can be used as RADIUS server or proxy.
	 * 
	 * @param domainId
	 * @param useDeviceMacAsCode : if true, TextItem's code is MAC address, else is IP address
	 * @return
	 */
	public static List<TextItem> getRadiusServersOrProxies(Long domainId, boolean useMacAsCode) {
		List<TextItem> servers = new ArrayList<TextItem>();
		Map<Long, String[]> map = new HashMap<Long, String[]>();
		String query = "select id, ipAddress, hostName, macAddress from "
				+ HiveAp.class.getCanonicalName();
		FilterParams proxyFilter = HiveApUtils.getRadiusProxyApFilter(true,
				false);
		List<?> proxyList = QueryUtil.executeQuery(query, null, proxyFilter,
				domainId);
		for (Object object : proxyList) {
			Object[] objects = (Object[]) object;
			Long id = (Long) objects[0];
			String ip = (String) objects[1];
			String hn = (String) objects[2];
			String mac = (String) objects[3];
			if (useMacAsCode) {
				if (!StringUtils.isEmpty(mac)) {
					map.put(id, new String[] { mac, hn });
				}
			} else if (!StringUtils.isEmpty(ip)) {
				map.put(id, new String[] { ip, hn });
			}
		}
		FilterParams serverFilter = HiveApUtils.getRadiusServerApFilter(true,
				false);
		List<?> serverList = QueryUtil.executeQuery(query, null, serverFilter,
				domainId);
		for (Object object : serverList) {
			Object[] objects = (Object[]) object;
			Long id = (Long) objects[0];
			String ip = (String) objects[1];
			String hn = (String) objects[2];
			String mac = (String) objects[3];
			if (useMacAsCode) {
				if (!StringUtils.isEmpty(mac)) {
					map.put(id, new String[] { mac, hn });
				}
			} else if (!StringUtils.isEmpty(ip)) {
				map.put(id, new String[] { ip, hn });
			}
		}
		for (String[] attrs : map.values()) {
			servers.add(new TextItem(attrs[0], attrs[1]));
		}
		Collections.sort(servers, new Comparator<TextItem>() {
			@Override
			public int compare(TextItem o1, TextItem o2) {
				return o1.getValue().compareToIgnoreCase(o2.getValue());
			}
		});
		return servers;
	}

	private void prepareClients() {
		String where = "manageStatus = :s1 and ipAddress is not null and deviceType!=:s2";
		Object[] values = new Object[] { HiveAp.STATUS_MANAGED, HiveAp.Device_TYPE_VPN_GATEWAY };
		List<?> list = QueryUtil.executeQuery(
				"select hostName, macAddress from "
						+ HiveAp.class.getSimpleName(), new SortParams(
						"hostName"), new FilterParams(where, values), domainId);
		clients = new ArrayList<TextItem>();
		for (Object obj : list) {
			Object[] o = (Object[]) obj;
			String mac = (String) o[1];
			String host = (String) o[0];
			clients.add(new TextItem(mac, host));
		}
	}

	public static final int TEST_TYPE_AUTH = 1;
	public static final int TEST_TYPE_ACCT = 2;

	public EnumItem[] getTestType1() {
		return new EnumItem[] { new EnumItem(TEST_TYPE_AUTH,
				getText("hm.tool.test.radius.option.auth")) };
	}

	public EnumItem[] getTestType2() {
		return new EnumItem[] { new EnumItem(TEST_TYPE_ACCT,
				getText("hm.tool.test.radius.option.acct")) };
	}

	private int testType = TEST_TYPE_AUTH;
	private List<TextItem> servers;
	private List<TextItem> clients;
	private String server;
	private String client;
	private String username;
	private String password;
	private String serverInput;

	public int getTestType() {
		return testType;
	}

	public void setTestType(int testType) {
		this.testType = testType;
	}

	public void setServerInput(String serverInput) {
		this.serverInput = serverInput;
	}

	public List<TextItem> getServers() {
		return servers;
	}

	public List<TextItem> getClients() {
		return clients;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
