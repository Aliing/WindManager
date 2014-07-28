package com.ah.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.bo.hhm.ConnectivityTestResultItem;
import com.ah.bo.hhm.HmolConnTestResult;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;

public class HmolConnectivityTestTool {

	private static final Tracer tracer = new Tracer(
			HmolConnectivityTestTool.class.getSimpleName());
	private static Map<String, HmolConnTestResult> testResultMap = new HashMap<String, HmolConnTestResult>();
	private static final int MAX_APS_PER_TEST = 200;

	public static void doTest(String vhmId, Long domainId, String hmolServerIp,
			String serverDomainName) {

		HmolConnTestResult testResult = getTestResult(vhmId);
		if (testResult == null) {
			testResult = new HmolConnTestResult();
		}
		List<ConnectivityTestResultItem> noConnectDevicesList = new ArrayList<ConnectivityTestResultItem>();
		List<ConnectivityTestResultItem> failConnectDeviceList = new ArrayList<ConnectivityTestResultItem>();
		
		List<HiveAp> hiveAps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("owner.id = :s1 AND simulated = :s2 ", new Object[]{domainId, false}));
		
		if (hiveAps != null && !hiveAps.isEmpty()) {
			
			int apCounts = hiveAps.size();
			testResult.setDevicesCount(apCounts);
			tracer.debug("doTest", "count of HiveAPs from DB : " + hiveAps.size());
			
			// deal max 200 APs per test
			int loops = (int)apCounts / MAX_APS_PER_TEST + (apCounts % MAX_APS_PER_TEST > 0 ? 1 : 0);
			int toIndex = 0;
			List<HiveAp> subApList;
			for (int i = 0; i < loops; i++) {
				toIndex = MAX_APS_PER_TEST * (i + 1);
				toIndex = toIndex > apCounts ? apCounts : toIndex ;
				subApList = new ArrayList<HiveAp>(hiveAps.subList(MAX_APS_PER_TEST * i, toIndex));
				runTest(hmolServerIp, subApList, noConnectDevicesList, failConnectDeviceList, false);
			}
			testResult.setNoConnectDevicesList(noConnectDevicesList);
			testResult.setFailConnectDeviceList(failConnectDeviceList);
		}
		
		testResult.setDomainName(serverDomainName);
		testResult.setServiceAddress(hmolServerIp);
		
		// set result to MAP
		setTestResult(vhmId, testResult);
		
		// unlock flag for one time one VHM only has one connectivity test.
		setVhmDoingTestFlag(vhmId, false);
	}
	
	/**
	 * all APs should first do UDP, if UDP failed, then do TCP test,
	 * only both UDP and TCP failed, this AP is judged as : can not connect to destination HMOL.
	 * 
	 * @param hmolServerIp
	 * @param hiveAps
	 * @param failTestDeviceList
	 * @param forceTcpTest
	 */
	private static void runTest(String hmolServerIp, List<HiveAp> hiveAps,
			List<ConnectivityTestResultItem> noConnectDevicesList,
			List<ConnectivityTestResultItem> failTestDeviceList,
			boolean forceTcpTest) {

		List<BeCommunicationEvent> requests = new ArrayList<BeCommunicationEvent>();
		BeCliEvent req;
		int reqSeqNum;
		for (HiveAp hiveAp : hiveAps) {
			tracer.debug(
					"runTest",
					"====current HiveAP's MAC address====> : "
							+ hiveAp.getMacAddress() + ", Is connected: " + hiveAp.isConnected());
			if (!hiveAp.isConnected()) {
				
				// disconnected AP
				noConnectDevicesList.add(createTestResultItem(hiveAp));
				
			} else {
				// ( connected & not simulate )AP 
				
				String[] exeClis = new String[2];
				if (forceTcpTest /*|| BeAPConnectEvent.TRANSFERMODE_TCP == hiveAp
						.getTransferProtocol()*/){
					// TCP
					exeClis[0] = AhCliFactory.getTcpTestCli(hmolServerIp, 80, 5); // test TCP
					exeClis[1] = AhCliFactory.getTcpTestCli(hmolServerIp, 443, 5);   // test HTTPS

				} else /*if (BeAPConnectEvent.TRANSFERMODE_UDP == hiveAp
						.getTransferProtocol()) */{
					// UDP
					exeClis[0] = AhCliFactory.getTcpTestCli(hmolServerIp, 22, 5);   // test SCP
					exeClis[1] = AhCliFactory.getUdpTestCli(hmolServerIp, 12222, 5); // test UDP

				}

				reqSeqNum = AhAppContainer.getBeCommunicationModule()
						.getSequenceNumber();
				req = new BeCliEvent();
				req.setAp(hiveAp);
				req.setClis(exeClis);
				req.setSequenceNum(reqSeqNum);
				try {
					req.buildPacket();
				} catch (BeCommunicationEncodeException e) {
					tracer.error("closeSshTunnel",
							"Failed to build Hmol Connectivity Test request for HiveAP '"
									+ hiveAp.getMacAddress() + "'.", e);
				}
				requests.add(req);
			}
		}
		
		// if all APs are disconnected, do not send connection test request.
		if (requests.isEmpty()) {
			return;
		}
		
		// send test request
		List<BeCommunicationEvent> results = HmBeCommunicationUtil.sendSyncGroupRequest(requests);
		if (null != results) {
			HiveAp hiveAp;
			List<HiveAp> udpFailedHiveApList = new ArrayList<HiveAp>();
			for (BeCommunicationEvent result : results) {
				BeCapwapCliResultEvent eventResult = getCapwapCliResult(result);
				hiveAp = result.getAp();
				if (eventResult == null
						|| eventResult != null
						&& BeCommunicationConstant.RESULTTYPE_SUCCESS != eventResult
								.getCliResult()) {
					// failed
					if (!forceTcpTest /*&& BeAPConnectEvent.TRANSFERMODE_UDP == hiveAp
							.getTransferProtocol()*/) {
						// if HiveAP which failed in UDP test,
						// should do one more TCP test
						udpFailedHiveApList.add(hiveAp);
						tracer.debug(
								"runTest",
								"====HiveAP " + hiveAp.getMacAddress() + " failed in UDP test====>then do TCP test");
					} else {
						failTestDeviceList.add(createTestResultItem(hiveAp));
						tracer.debug(
								"runTest",
								"====HiveAP " + hiveAp.getMacAddress() + " failed in TCP test====>");
					}
				} else {
					
					// when HiveAP version is under 5.0r2 and UDP test,if resultMsg contain "100.00% packet loss" ,test fail,else test true
					boolean isSupportVersion = hiveAp.getSoftVer() != null && (NmsUtil.compareSoftwareVersion("5.0.3.0", hiveAp.getSoftVer()) > 0);
					if (isSupportVersion && (!forceTcpTest)/* && BeAPConnectEvent.TRANSFERMODE_UDP == hiveAp
							.getTransferProtocol()*/) {
						if(eventResult != null && eventResult.getCliSucceedMessage()!= null
								&& eventResult.getCliSucceedMessage().contains("100.00% packet loss")){
							// if HiveAP which failed in UDP test,
							// should do one more TCP test
							udpFailedHiveApList.add(hiveAp);
							tracer.debug(
									"runTest",
									"====HiveAP " + hiveAp.getMacAddress() + " failed in UDP test====>then do TCP test");
						} else {
							tracer.debug(
									"runTest",
									"====HiveAP " + hiveAp.getMacAddress() + " successed in UDP/TCP test====>");
						}
					} else {
						tracer.debug(
								"runTest",
								"====HiveAP " + hiveAp.getMacAddress() + " successed in UDP/TCP test====>");
					}
					
				}
			}
			
			// if has HiveAP failed in UDP test, do TCP test again. 
			if (!udpFailedHiveApList.isEmpty()) {
				tracer.debug("doTest", "count of HiveAPs failed in UDP test : " + hiveAps.size());
				runTest(hmolServerIp, udpFailedHiveApList, noConnectDevicesList, failTestDeviceList, true);
			}
		}
	}
	
	private static ConnectivityTestResultItem createTestResultItem(HiveAp hiveAp) {
	
		ConnectivityTestResultItem testResultItem = new ConnectivityTestResultItem();
		testResultItem.setHostName(hiveAp.getHostName());
		testResultItem.setIpAddress(hiveAp.getIpAddress());
		testResultItem.setHwModel(hiveAp.getHiveApModelString());
		return testResultItem;
	}

	private static BeCapwapCliResultEvent getCapwapCliResult(BeCommunicationEvent c_event) {
		try {
			int msgType = c_event.getMsgType();
			int result = c_event.getResult();
			if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				return (BeCapwapCliResultEvent) c_event;
			} else {
				tracer.error(
						"getCapwapCliResult",
						"cannot get Capwap Cli result of HiveAp:"
								+ c_event.getApMac() + ", msgType:" + msgType
								+ ", result:" + result);
			}
		} catch (Exception e) {
			tracer.error("getCapwapCliResult", e);
		}
		return null;
	}

	/*
	 * contains VHMID: return true, else false
	 */
	public static boolean isVhmDoingTest(String vhmId) {
		HmolConnTestResult connTestResult = getTestResult(vhmId);
		if (connTestResult == null) {
			return false;
		} else {
			return connTestResult.isVhmDoingTestFlag();
		}
	}
	
	public static void setVhmDoingTestFlag(String vhmId, boolean isOrNotDoing) {
		HmolConnTestResult connTestResult = getTestResult(vhmId);
		if (connTestResult == null) {
			connTestResult = new HmolConnTestResult();
		}
		connTestResult.setVhmDoingTestFlag(isOrNotDoing);
		setTestResult(vhmId, connTestResult);
	}
	
	public static HmolConnTestResult getTestResult(String vhmId) {
		return testResultMap.get(vhmId);
	}
	
	public static HmolConnTestResult setTestResult(String vhmId, HmolConnTestResult result) {
		return testResultMap.put(vhmId, result);
	}
	
	public static void removeVhmTestResult(String vhmId){
		testResultMap.remove(vhmId);
	}
}
