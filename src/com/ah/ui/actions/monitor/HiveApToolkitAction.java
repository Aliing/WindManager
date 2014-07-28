package com.ah.ui.actions.monitor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.AhAppContainer;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.cli.cliwindow.Command;
import com.ah.be.cli.cliwindow.CommandExecutorUtil;
import com.ah.be.cli.cliwindow.CommandStatus;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeShowCaptureStatusEvent;
import com.ah.be.communication.event.BeShowCaptureStatusResultEvent;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.performance.dataretention.ClientHistoryTracking;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.be.ts.hiveap.DebugException;
import com.ah.be.ts.hiveap.DebugState;
import com.ah.be.ts.hiveap.impl.HiveApDebugMgmtImpl;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitor;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorFilterParams;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorFilterParams.LogLevel;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorMgmt;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorNotification.Stage;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortImpl;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortParams;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortParams.SortType;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbe;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeMgmt;
import com.ah.be.ts.hiveap.probe.vlan.VlanProbeNotification;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhClientSessionHistory;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class HiveApToolkitAction extends BaseAction implements QueryBo {

	private static final String CURRENT_AP_MAPS_MAP_SESSION = HiveApToolkitAction.class.getSimpleName()+"_Current_AP_Maps";
    private static final String CURRENT_APS_SET_SESSION = HiveApToolkitAction.class.getSimpleName()+"_Current_APs";
    private static final String CURRENT_CLIENTS_SET_SESSION = HiveApToolkitAction.class.getSimpleName()+"_Current_Clients";

    private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(HiveApToolkitAction.class
			.getSimpleName());

	private final HiveApDebugMgmtImpl hiveApDebugMgmt = AhAppContainer.getBeTsModule()
			.getHiveApDebugMgmt();

	private final ClientMonitorMgmt<ClientMonitor, ClientMonitorNotification> clientMonitorMgmt = hiveApDebugMgmt.getClientMonitorMgmt();

	private final VlanProbeMgmt<VlanProbe, VlanProbeNotification> vlanProbeMgmt = hiveApDebugMgmt.getVlanProbeMgmt();

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("initSshTunnelPanel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId + ", leafNodeId:" + leafNodeId);
				HiveAp hiveAp = getSelectedHiveAp();
				if (null != hiveAp) {
					hiveApId = hiveAp.getId();
					stringForTitle = hiveAp.getHostName();
				}
				return "sshTunnel";
			} else if ("startTunnel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId);
				jsonObject = startSshTunnel();
				return "json";
			} else if ("stopTunnel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId);
				jsonObject = stopSshTunnel();
				return "json";
			} else if ("showTunnel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId);
				jsonObject = showSshTunnel();
				return "json";
			} else if ("initDebugClient".equals(operation)) {
				log.info("execute", "operation:"
						+ operation
						+ ", selected HiveAp size:"
						+ (getAllSelectedIds() == null ? "null"
								: getAllSelectedIds().size()));
				getDebugClientOfSelectedHiveAps(getAllSelectedIds());
				//stringForTitle = "Selected "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
				stringForTitle = "Selected Devices";
				return "clientDebug";
			} else if ("initDebugClientFromActiveclient".equals(operation)) {
				log.info("execute", "operation:"
						+ operation
						+ ", selected ActiveClient size:"
						+ (getAllSelectedIds() == null ? "null"
								: getAllSelectedIds().size()));
				setSelectedL2Feature(L2_FEATURE_CLIENTMONITOR);
				resetPermission();// reset permission after set feature node
				getDebugClientOfSelectedActiveClients(getAllSelectedIds());
				stringForTitle = "Selected Active Clients";
				return "clientDebug";
			} else if ("initDebugClientFromTopoFloor".equals(operation)) {
				log.info("execute", "operation:" + operation + ", leafNodeId:"
						+ leafNodeId + ", mapNodeId:" + mapNodeId);
				setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
				resetPermission();// reset permission after set feature node
				if (null != leafNodeId) {
					HiveAp hiveAp = getSelectedHiveAp();
					if (null != hiveAp) {
						Set<Long> ids = new HashSet<Long>(1);
						ids.add(hiveAp.getId());
						getDebugClientOfSelectedHiveAps(ids);
						stringForTitle = hiveAp.getHostName();
					}
				} else {
					getDebugClientOfSelectedMap(mapNodeId);
				}
				return "clientDebug";
			} else if ("toolDebugClient".equals(operation)) {
				log.info("execute", "operation:" + operation + ", clientMac:"
						+ clientMac);
				setSelectedL2Feature(L2_FEATURE_CLIENT_CONNECTION_MONITOR);
				isEnterFromTool = true;
				resetPermission();// reset permission after set feature node
				getDebugClientOfSelectedHiveAps(null);
				return "toolClientDebug";
			} else if ("fetchInitClientDebugData".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonArray = getClients();
				return "json";
			} else if ("pollClientDebugData".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", viewType:"
						+ viewType + ", filterType:" + filterType
						+ ", rowLimit:" + rowLimit + ", stagesName:" + stageNames
						+ ", clientMacs:" + clientMacs);
				jsonObject = getClientDebugData(viewType, filterType,
						getSelectedClient(clientMacs), getSelectedStages(stageNames), rowLimit);
				return "json";
			} else if ("initializeClientDebugs".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", clientMacs:"
						+ clientMacs + ", performance:"+enablePerformance);
				jsonArray = initializeClientDebugs(clientMacs, enablePerformance);
				return "json";
			} else if ("checkStartClientDebugs".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", clientMac:"
						+ clientMac + ", apMacs:" + apMacs);
				jsonObject = checkStartClientDebugs(clientMac);
				return "json";
			} else if ("startClientDebugs".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", clientMac:"
						+ clientMac + ", apMacs:" + apMacs);
				jsonArray = startClientDebugs(clientMac);
				return "json";
			} else if ("stopClientDebugs".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", clientMac:"
						+ clientMac + ", apMacs:" + apMacs + ", cookieIds:"
						+ cookieIds);
				jsonArray = stopClientDebugs(clientMac, apMacs, cookieIds);
				return "json";
			} else if ("export".equals(operation)) {
				log.info("execute", "operation:" + operation + ", viewType:"
						+ viewType + ", filterType:" + filterType
						+ ", clientMacs:" + clientMacs);
				setupLogMessage(getSelectedClient(clientMacs));
				return "download";
			} else if ("removeLogs".equals(operation)) {
				log.info("execute", "operation:" + operation + ", clientMacs:"
						+ clientMacs);
				jsonObject = removeLogMessage(getSelectedClient(clientMacs));
				return "json";
			} else if ("closeDebugClients".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", clientMacs:"
						+ clientMacs);
				closeDebugClients(getSelectedClient(clientMacs));
				return null;
			} else if ("clearDebugLogs".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId + ", viewType:"
						+ viewType + ", filterType:" + filterType
						+ ", rowLimit:" + rowLimit + ", clientMacs:"
						+ clientMacs);
				jsonObject = clearDebugLogs(viewType, filterType,
						getSelectedClient(clientMacs), getSelectedStages(stageNames), rowLimit);
				return "json";
			} else if ("excludeProbe".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", exclude probe:" + excludeProbe + ", clientMac:"
						+ clientMac);
				excludeProbe(clientMac, excludeProbe);
				return null;
			} else if ("initVlanProbePanel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId + ", leafNodeId:" + leafNodeId);
				HiveAp hiveAp = getSelectedHiveAp();
				if (null != hiveAp) {
					hiveApId = hiveAp.getId();
					stringForTitle = hiveAp.getHostName();
				}
				return "vlanProbe";
			} else if ("toolVlanProbe".equals(operation)) {
				log.info("execute", "operation:" + operation);
				setSelectedL2Feature(L2_FEATURE_VLAN_PROBE);
				isEnterFromTool = true;
				prepareAvaliableHiveAPs();
				resetPermission();// reset permission after set feature node
				
				return "toolVlanProbe";
			} else if ("startVlanProbe".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId + ", vlanProbeFrom:" + vlanProbeFrom
						+ ", vlanProbeTo:" + vlanProbeTo
						+ ", vlanProbeRetries:" + vlanProbeRetries
						+ ", vlanProbeTimeout:" + vlanProbeTimeout
						+ ",debugGroupId:" + debugGroupId);
				Object cookieId = MgrUtil.getSessionAttribute("vlanProbe_cookieId_"+hiveApId);
				if(null != cookieId){
					int iCookieId = (int)cookieId;
					JSONObject tempJsonObject = stopVlanProbe(iCookieId);
					if(!tempJsonObject.has("stop_failed") || !tempJsonObject.getBoolean("stop_failed")){
						MgrUtil.removeSessionAttribute("vlanProbe_cookieId_"+hiveApId);
					}
				}
				jsonObject = new JSONObject();
				JSONObject temp = startVlanProbe(hiveApId,
						vlanProbeFrom, vlanProbeTo, vlanProbeRetries,
						vlanProbeTimeout, debugGroupId);
				if(!temp.has("start_failed") || !temp.getBoolean("start_failed")){
					MgrUtil.setSessionAttribute("vlanProbe_cookieId_"+hiveApId,temp.has("cookieId") ? temp.get("cookieId"):null);
				}
				
				jsonObject.put("states", temp);
				return "json";
			} else if ("stopVlanProbe".equals(operation)) {
				log.info("execute", "operation:" + operation + ",debugGroupId:"
						+ debugGroupId + ", cookieId:" + cookieId);
				jsonObject = new JSONObject();
				JSONObject tempJsonObject = stopVlanProbe(cookieId);
				jsonObject.put("states", tempJsonObject);
				if(!tempJsonObject.has("stop_failed") || !tempJsonObject.getBoolean("stop_failed")){
					MgrUtil.removeSessionAttribute("vlanProbe_cookieId_"+hiveApId);
				}
				return "json";
			} else if ("pollVlanProbe".equals(operation)) {
				log.info("execute", "operation:" + operation + ",debugGroupId:"
						+ debugGroupId + ", cookieId:" + cookieId);
				jsonObject = pollVlanProbe(cookieId);
				return "json";
			} else if ("initPathProbePanel".equals(operation)) {
				log.info("execute", "operation:" + operation + ", hiveApId:"
						+ hiveApId + ", leafNodeId:" + leafNodeId);
				HiveAp hiveAp = getSelectedHiveAp();
				if (null != hiveAp) {
					hiveApId = hiveAp.getId();
					stringForTitle = hiveAp.getHostName();
				}
				return "pathProbe";
			} else if ("closeVlanGroup".equals(operation)) {
				log.info("execute", "operation:" + operation
						+ ", debugGroupId:" + debugGroupId);
				closeDebugGroup(debugGroupId);
				return null;
			} else if ("initPacketCapture".equals(operation)) {
				// for packet capture menu from map.
				HiveAp hiveAp = getSelectedHiveAp();
				if (null != hiveAp) {
					hiveApId = hiveAp.getId();
				}
				captureCount = 2000;
				promiscuousWhenCapture = true;
				captureTrafficType = 0;
				return "packetCapture";
			} else if ("toolPacketCapture".equals(operation)) {
				setSelectedL2Feature(L2_FEATURE_PACKET_CAPTURE);
				isEnterFromTool = true;
				resetPermission();// reset permission after set feature node
				captureCount = 2000;
				promiscuousWhenCapture = true;
				captureTrafficType = 0;
				prepareAvaliableHiveAPs();
				return "toolPacketCapture";
			} else if ("startCapture".equals(operation)) {
				boolean isSucc_wifi0 = false;
				boolean isSucc_wifi1 = false;

				try {
					HiveAp hiveAp = getSelectedHiveAp();
					String checkAPResult = MapNodeAction
							.validateSelectedHiveAp(hiveAp, "3.3.1.0");
					if (null == checkAPResult && !hiveAp.isSimulated()) {
						if (captureInterface == INTERFACE_WIFI0
								|| captureInterface == INTERFACE_BOTH) {
							isSucc_wifi0 = startCapture(hiveAp, INTERFACE_WIFI0);
						}

						if (captureInterface == INTERFACE_WIFI1
								|| captureInterface == INTERFACE_BOTH) {
							isSucc_wifi1 = startCapture(hiveAp, INTERFACE_WIFI1);
						}
					}
				} catch (Exception e) {
					log.error("execute", "start capture catch exception", e);
				}

				jsonObject = new JSONObject();
				jsonObject.put("result_wifi0", isSucc_wifi0);
				jsonObject.put("result_wifi1", isSucc_wifi1);
				jsonObject.put("wifi0", captureInterface == INTERFACE_BOTH
						|| captureInterface == INTERFACE_WIFI0);
				jsonObject.put("wifi1", captureInterface == INTERFACE_BOTH
						|| captureInterface == INTERFACE_WIFI1);

				return "json";
			} else if ("stopCapture".equals(operation)) {
				boolean isSuccess = false;
				String resultMsg;
				try {
					HiveAp hiveAp = getSelectedHiveAp();
					resultMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
							"3.3.1.0");
					if (null == resultMsg && !hiveAp.isSimulated()) {
						isSuccess = stopCapture(hiveAp);
					}
				} catch (Exception e) {
					log.error("execute", "stop capture catch exception", e);
					isSuccess = false;
				}

				jsonObject = new JSONObject();
				jsonObject.put("result", isSuccess);

				return "json";
			} else if ("pollWifi0CaptureStatus".equals(operation)) {
				BeShowCaptureStatusResultEvent resultEvent = null;
				String checkAPResult = null;

				try {
					HiveAp hiveAp = getSelectedHiveAp();
					checkAPResult = MapNodeAction.validateSelectedHiveAp(
							hiveAp, "3.3.1.0");
					if (null == checkAPResult) {
						resultEvent = pollCaptureStatus(hiveAp, INTERFACE_WIFI0);
					}
				} catch (Exception e) {
					log.error("execute", "poll Capture Status catch exception",
							e);
				}

				jsonObject = new JSONObject();
				jsonObject.put("result", resultEvent != null);
				if (checkAPResult != null) {
					jsonObject.put("checkAPResult", checkAPResult);
				}
				if (resultEvent != null) {
					jsonObject
							.put(
									"isFinished",
									resultEvent.getCapturing() == BeShowCaptureStatusResultEvent.CAPTURING_NOINPROGRESS);
					jsonObject.put("tx", resultEvent.getTxFramesCaptured());
					jsonObject.put("rx", resultEvent.getRxFramesCaptured());
					jsonObject.put("total", resultEvent
							.getTotalFramesCaptured());
				}

				return "json";
			} else if ("pollWifi1CaptureStatus".equals(operation)) {
				BeShowCaptureStatusResultEvent resultEvent = null;
				String checkAPResult = null;

				try {
					HiveAp hiveAp = getSelectedHiveAp();
					checkAPResult = MapNodeAction.validateSelectedHiveAp(
							hiveAp, "3.3.1.0");
					if (null == checkAPResult) {
						resultEvent = pollCaptureStatus(hiveAp, INTERFACE_WIFI1);
					}
				} catch (Exception e) {
					log.error("execute", "poll Capture Status catch exception",
							e);
				}

				jsonObject = new JSONObject();
				jsonObject.put("result", resultEvent != null);
				if (checkAPResult != null) {
					jsonObject.put("checkAPResult", checkAPResult);
				}
				if (resultEvent != null) {
					jsonObject
							.put(
									"isFinished",
									resultEvent.getCapturing() == BeShowCaptureStatusResultEvent.CAPTURING_NOINPROGRESS);
					jsonObject.put("tx", resultEvent.getTxFramesCaptured());
					jsonObject.put("rx", resultEvent.getRxFramesCaptured());
					jsonObject.put("total", resultEvent
							.getTotalFramesCaptured());
				}

				return "json";
			} else if ("checkDownloadCapture".equals(operation)) {
				jsonObject = new JSONObject();

				// tftp capture file at first
				boolean isSucc = tftpCaptureFile(getSelectedHiveAp());
				if (!isSucc) {

					jsonObject
							.put(
									"message",
									getText("error.hiveap.toolkit.capture.download"));
					return "json";
				}

				dumpFileName = getCapturedFileName(getSelectedHiveAp()
						.getHostName(), CLICommonFunc.getEnumItemValue(
						getWifiInterfaceList(), captureInterface));
				// inputPath = BeAdminCentOSTools.AH_NMS_CAPTURERESULT_DIR +
				// File.separator + dumpFileName;
				inputPath = AhDirTools.getDumpDir() + dumpFileName;
				File file = new File(inputPath);

				// check file exist
				if (!file.exists()) {
					jsonObject
							.put(
									"message",
									getText("error.hiveap.toolkit.capture.find"));
					return "json";
				}

				return "json";
			} else if ("downloadCapture".equals(operation)) {
				dumpFileName = getCapturedFileName(getSelectedHiveAp()
						.getHostName(), CLICommonFunc.getEnumItemValue(
						getWifiInterfaceList(), captureInterface));
				// inputPath = BeAdminCentOSTools.AH_NMS_CAPTURERESULT_DIR +
				// File.separator + dumpFileName;
				inputPath = AhDirTools.getDumpDir() + dumpFileName;

				return "downloadCapture";
			} else if ("tftpCapturedFile".equals(operation)) {
				jsonObject = new JSONObject();
				boolean isSucc = tftpCaptureFile(getSelectedHiveAp());
				jsonObject.put("result", isSucc);
				return "json";
			} else if ("initCLIWindow".equals(operation)) {
				initCLIWindow();
				return "cliWindow";
			} else if ("exeCommand".equals(operation)) {
				jsonObject = new JSONObject();
				jsonObject.put("status", executeCommand());
				return "json";
			}else if ("exportCommandResult".equals(operation)) {
				if (exportFileName == null) {
					exportFileName = "download.txt";
				}

				try {
					File file = new File(exportFileName);
					FileOutputStream outStream = new FileOutputStream(file);
					StringBuffer exportResult = new StringBuffer();
					Collection<CommandStatus> commands = CommandExecutorUtil
							.getCommands(request.getSession().getId());
					String separator = "========================================================";
					String br = "\r\n";
					
					if(hostNameString != null && hostNameString.length > 1){
						for(String hostName : hostNameString){
							for(CommandStatus command : commands){
								if(!hostName.isEmpty() && command.getSequence() != -1
										&& command.getDestination() != null
										&& hostName.equalsIgnoreCase(command.getDestination())){
									exportResult.append(separator + br);
									exportResult.append("Host Name : "
											+ command.getDestination() + br);
									
									if (command.getCommand() != null) {
										exportResult.append(separator + br);
										exportResult.append(command.getCommand() + br);

									}
									exportResult.append(separator + br);
									
									if (command.getResult() != null) {
										if (command.getCommand() == null) {
											exportResult.append("Status : ");
										}
										exportResult.append(command.getResult()
												.replace("&nbsp;", " ")
												.replace("&lt;", "<").replace("&gt;", ">")
												.replace("<br />", br)
												+ "\r\n");
										exportResult.append(separator + br);
									}
								}
							}
							
						}
					}else{
						for (CommandStatus command : commands) {
							/*
							 * add separate line
							 */
							if (command.getSequence() == -1) {
								continue;
							}
							
							if (command.getDestination() != null) {
								exportResult.append(separator + br);
								exportResult.append("Host Name : "
										+ command.getDestination() + br);
							}
							
							if (command.getCommand() != null) {
								exportResult.append(separator + br);
								exportResult.append(command.getCommand() + br);

							}
							exportResult.append(separator + br);
							
							if (command.getResult() != null) {
								if (command.getCommand() == null) {
									exportResult.append("Status : ");
								}
								exportResult.append(command.getResult()
										.replace("&nbsp;", " ")
										.replace("&lt;", "<").replace("&gt;", ">")
										.replace("<br />", br)
										+ "\r\n");
								exportResult.append(separator + br);
							}
						}
					}

					byte[] byteArry = new String(exportResult).getBytes();
					outStream.write(byteArry);
					outStream.flush();
					outStream.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "export";
			}else if ("pollCommandStatus".equals(operation)) {
				jsonObject = pollCLIExecution();
				return "json";
			} else if ("cancelCommand".equals(operation)) {
				cancelCommand();
				return null;
			} else if ("importCommand".equals(operation)) {
				importCommand();
				return "cliWindow";
			} else if ("initRemoteSniffer".equals(operation)) {
				this.stringForTitle = this.getSelectedHiveAp().getHostName();
				if (this.hiveApId == null) {
					this.hiveApId = this.getSelectedHiveAp().getId();
				}
				return "remoteSniffer";
			} else if ("setRemoteSniffer".equals(operation)) {
				jsonObject = setRemoteSniffer();
				return "json";
			} else if ("initFwPolicyRulePanel".equals(operation)) {
				return "fwPolicyRule";
			} else if ("fwPolicyRuleMgr".equals(operation)) {
				jsonObject = setFirewallPolicyRule();
				return "json";
			}

			return SUCCESS;
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	/* for SSH tunnel feature */
	private String sshServerUrl;
	private String sshServerPort = "22";
	private String sshServerUser;
	private String sshServerPsd;
	private String sshTunnelPort;
	private String sshTunnelTmt = "0";

	public void setSshServerUrl(String sshServerUrl) {
		this.sshServerUrl = sshServerUrl;
	}

	public void setSshServerPort(String sshServerPort) {
		this.sshServerPort = sshServerPort;
	}

	public void setSshServerUser(String sshServerUser) {
		this.sshServerUser = sshServerUser;
	}

	public void setSshServerPsd(String sshServerPsd) {
		this.sshServerPsd = sshServerPsd;
	}

	public void setSshTunnelTmt(String sshTunnelTmt) {
		this.sshTunnelTmt = sshTunnelTmt;
	}

	public void setSshTunnelPort(String sshTunnelPort) {
		this.sshTunnelPort = sshTunnelPort;
	}

	public String getSshServerPort() {
		return sshServerPort;
	}

	public String getSshTunnelTmt() {
		return sshTunnelTmt;
	}

	private JSONObject startSshTunnel() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			HiveAp hiveAp = getSelectedHiveAp();
			String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
					"3.2.0.0");
			if (null != errorMsg) {
				jsonObject.put("msg", errorMsg);
			} else {
				int port = (null == sshServerPort || "".equals(sshServerPort)) ? 22
						: Integer.parseInt(sshServerPort);
				int tunnelPort = (null == sshTunnelPort || ""
						.equals(sshTunnelPort)) ? 1025 : Integer
						.parseInt(sshTunnelPort);
				int timeout = (null == sshTunnelTmt || "".equals(sshTunnelTmt)) ? 0
						: Integer.parseInt(sshTunnelTmt);
				String cli = AhCliFactory.setupSshTunnel(sshServerUrl, port,
						tunnelPort, sshServerUser, sshServerPsd, timeout);
				BeCommunicationEvent result = BeTopoModuleUtil
						.sendSyncCliRequest(hiveAp, new String[] { cli },
								BeCliEvent.CLITYPE_NORMAL, 60);
				String msg = BeTopoModuleUtil.parseCliRequestResult(result);
				boolean isStarted = BeTopoModuleUtil.isCliExeSuccess(result);
				if (isStarted) {
					msg = MgrUtil.getUserMessage("info.cli.general.success");
				}
				if (null != msg) {
					jsonObject.put("msg", msg);
				}
			}
		} catch (Exception e) {
			log.error("startSshTunnel", "error occored.", e);
			jsonObject.put("msg", "Start SSH Tunnel error.");
		}
		return jsonObject;
	}

	private JSONObject stopSshTunnel() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			HiveAp hiveAp = getSelectedHiveAp();
			String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
					"3.2.0.0");
			if (null != errorMsg) {
				jsonObject.put("msg", errorMsg);
			} else {
				String cli = AhCliFactory.closeSshTunnel();
				BeCommunicationEvent result = BeTopoModuleUtil
						.sendSyncCliRequest(hiveAp, new String[] { cli },
								BeCliEvent.CLITYPE_NORMAL, 60);
				String msg = BeTopoModuleUtil.parseCliRequestResult(result);
				boolean isStoped = BeTopoModuleUtil.isCliExeSuccess(result);
				if (isStoped) {
					msg = MgrUtil.getUserMessage("info.cli.general.success");
				}
				if (null != msg) {
					jsonObject.put("msg", msg);
				}
			}
		} catch (Exception e) {
			log.error("stopSshTunnel", "error occored.", e);
			jsonObject.put("msg", "Stop SSH Tunnel error.");
		}
		return jsonObject;
	}

	private JSONObject showSshTunnel() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			HiveAp hiveAp = getSelectedHiveAp();
			String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
					"3.2.0.0");
			if (null != errorMsg) {
				jsonObject.put("msg", errorMsg);
			} else {
				String cli = AhCliFactory.showSshTunnel();
				BeCommunicationEvent result = BeTopoModuleUtil
						.sendSyncCliRequest(hiveAp, new String[] { cli },
								BeCliEvent.CLITYPE_NORMAL, 60);
				String msg = BeTopoModuleUtil.parseCliRequestResult(result);
				if (null != msg) {
					jsonObject.put("msg", msg);
				}
			}
		} catch (Exception e) {
			log.error("showSshTunnel", "error occored.", e);
			jsonObject.put("msg", "Show SSH Tunnel error.");
		}
		return jsonObject;
	}

	/* ssh tunnel end */

	/*
	 * One client debug window use one group ID.
	 */
	private int debugGroupId;
	private String clientMac;
	private int cookieId;
	private String viewType;
	private String filterType;
	private int rowLimit;
	private String stageNames;
	private String logFileName;
	// private String logMessage;
	private String cookieIds;
	private String clientMacs;
	private String apMacs;
	private boolean excludeProbe;
	
	private boolean enablePerformance;
	private String exportFileName;

	public String getExportFileName() {
		return exportFileName;
	}

	public void setExportFileName(String exportFileName) {
		this.exportFileName = exportFileName;
	}

	private final List<String> selectedMaps = new ArrayList<String>();
	private final List<TextItem> selectedHiveAps = new ArrayList<TextItem>();
	private final List<String> selectedClients = new ArrayList<String>();
	private final List<String> clientDebugInfos = new ArrayList<String>();

	public List<String> getClientDebugInfos() {
		if (null != clientDebugInfos) {
			// order by name
			Collections.sort(clientDebugInfos, new Comparator<String>() {
				@Override
				public int compare(String o1, String o2) {
					StringTokenizer token1 = new StringTokenizer(o1, "|");
					StringTokenizer token2 = new StringTokenizer(o2, "|");
					String n1 = "", n2 = "";
					while (token1.hasMoreTokens()) {
						n1 = token1.nextToken();
					}
					while (token2.hasMoreTokens()) {
						n2 = token2.nextToken();
					}
					return n1.compareToIgnoreCase(n2);
				}
			});
		}
		return clientDebugInfos;
	}

	public List<TextItem> getSelectedHiveAps() {
		if (null != selectedHiveAps) {
			TextItem all = null;
			for (int i = 0; i < selectedHiveAps.size(); i++) {
				if ("All".equals(selectedHiveAps.get(i).getKey())) {
					all = selectedHiveAps.remove(i);
					break;
				}
			}
			// order by name
			Collections.sort(selectedHiveAps, new Comparator<TextItem>() {
				@Override
				public int compare(TextItem o1, TextItem o2) {
					String n1 = o1.getValue();
					String n2 = o2.getValue();
					return n1.compareToIgnoreCase(n2);
				}
			});
			if (null != all) {
				selectedHiveAps.add(0, all);
			}
		}
		return selectedHiveAps;
	}

	public List<String> getSelectedClients() {
		return selectedClients;
	}

	public List<String> getSelectedMaps() {
		return selectedMaps;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public void setRowLimit(int rowLimit) {
		this.rowLimit = rowLimit;
	}

	public String getStageNames() {
		return stageNames;
	}

	public void setStageNames(String stageNames) {
		this.stageNames = stageNames;
	}

	public String getLogFileName() {
		return logFileName;
	}

	public int getDebugGroupId() {
		return NmsUtil.getDebugGroupId();
	}

	public void setDebugGroupId(int debugGroupId) {
		this.debugGroupId = debugGroupId;
	}

	public void setCookieId(int cookieId) {
		this.cookieId = cookieId;
	}

	public void setCookieIds(String cookieIds) {
		this.cookieIds = cookieIds;
	}

	public void setClientMacs(String clientMacs) {
		this.clientMacs = clientMacs;
	}

	public void setApMacs(String apMacs) {
		this.apMacs = apMacs;
	}

	public void setExcludeProbe(boolean excludeProbe) {
		this.excludeProbe = excludeProbe;
	}

	
	public InputStream getInputStream() throws Exception {
		if("exportCommandResult".equals(operation)) {
			exportFileName = "download.txt";
			File file = new File(exportFileName);
			exportFileName = file.getName();
			return new FileInputStream(file);
		}else{
			if (null == logFileName) {
				return new ByteArrayInputStream("get logs error...".getBytes());
			} else {
				File file = new File(logFileName);
				logFileName = file.getName();
				return new FileInputStream(file);
			}
		}
		
	}
	
	private final String clientMacLabel = MgrUtil
			.getUserMessage("debug.client.column.macAddress");
	private final String logtimeLabel = MgrUtil
			.getUserMessage("debug.client.log.time");
	private final String hiveApLabel = MgrUtil
			.getUserMessage("debug.client.column.hiveApMac");
	private final String filterBasicLabel = MgrUtil
			.getUserMessage("debug.client.filter.level.basic");
	private final String filterInfoLabel = MgrUtil
			.getUserMessage("debug.client.filter.level.info");
	private final String filterDetailLabel = MgrUtil
			.getUserMessage("debug.client.filter.level.detail");
	private final String logStatusLabel = MgrUtil
			.getUserMessage("debug.client.column.status");
	private final String excludeProbeLabel = MgrUtil
			.getUserMessage("debug.client.column.excludeProbe");

	private ClientMonitorSortParams[] convertViewType(String viewType) {
		if (clientMacLabel.equals(viewType)) {
			return new ClientMonitorSortParams[] {
					new ClientMonitorSortParams(SortType.CLIENT_MAC, true),
					new ClientMonitorSortParams(SortType.LOG_MSG_TIME, true),
					new ClientMonitorSortParams(SortType.AP_NODE_NAME, true),
					new ClientMonitorSortParams(SortType.MSG_SEQ_NUM, true) };
			// return SortType.CLIENT_MAC;
		} else if (hiveApLabel.equals(viewType)) {
			return new ClientMonitorSortParams[] {
					new ClientMonitorSortParams(SortType.AP_NODE_NAME, true),
					new ClientMonitorSortParams(SortType.CLIENT_MAC, true),
					new ClientMonitorSortParams(SortType.LOG_MSG_TIME, true),
					new ClientMonitorSortParams(SortType.MSG_SEQ_NUM, true) };
			// return SortType.AP_NODE_NAME;
		} else if (logtimeLabel.equals(viewType)) {
			return new ClientMonitorSortParams[] {
					new ClientMonitorSortParams(SortType.LOG_MSG_TIME, true),
					new ClientMonitorSortParams(SortType.CLIENT_MAC, true),
					new ClientMonitorSortParams(SortType.AP_NODE_NAME, true),
					new ClientMonitorSortParams(SortType.MSG_SEQ_NUM, true) };
			// return SortType.LOG_MSG_TIME;
		} else {
			return new ClientMonitorSortParams[] {
					new ClientMonitorSortParams(SortType.LOG_MSG_TIME, true),
					new ClientMonitorSortParams(SortType.CLIENT_MAC, true),
					new ClientMonitorSortParams(SortType.AP_NODE_NAME, true),
					new ClientMonitorSortParams(SortType.MSG_SEQ_NUM, true) };
			// return SortType.LOG_MSG_TIME;
		}
	}

	private ClientMonitorFilterParams convertFilterType(String filterType) {
		if (filterBasicLabel.equals(filterType)) {
			return new ClientMonitorFilterParams(LogLevel.BASIC);
		} else if (filterInfoLabel.equals(filterType)) {
			return new ClientMonitorFilterParams(LogLevel.INFO);
		} else if (filterDetailLabel.equals(filterType)) {
			return new ClientMonitorFilterParams(LogLevel.DETAIL);
		} else {
			return new ClientMonitorFilterParams(LogLevel.BASIC);
		}
	}

	private void closeDebugClients(Set<String> clients) {
		clientMonitorMgmt.removeRequests(getDomain().getDomainName(), clients);
	}

	private void excludeProbe(String client, boolean excludeProbe) {
		Collection<ClientMonitor> requests = clientMonitorMgmt.getRequests(
				getDomain().getDomainName(), client);
		if (null != requests) {
			for (ClientMonitor request : requests) {
				request.setFilteringProbeEvents(excludeProbe);
			}
		}
	}

	private void setupLogMessage(Set<String> clients) {
		/*-
		List<?> list = debugger.getGroupNotifications(debugGroupId);
		ClientMonitorSortImpl impl = new ClientMonitorSortImpl(
				convertViewType(viewType), convertFilterType(filterType));
		logMessage = debugger.getExportDebugMessages(
				(List<ClientMonitorNotification>) list, impl);
		 */
		try {
			if (clients.isEmpty()) {
				logFileName = clientMonitorMgmt.exportClientLogs(getDomain()
						.getDomainName());
			} else {
				logFileName = clientMonitorMgmt.exportClientLogs(getDomain()
						.getDomainName(), clients);
			}
		} catch (IOException e) {
			log.error("setupLogMessage", e);
		}
	}

	private JSONObject removeLogMessage(Set<String> clients)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			clientMonitorMgmt.deleteClientLogs(getDomain().getDomainName(),
					clients);
			jsonObject.put("suc", true);
		} catch (DebugException e) {
			log.error("removeLogMessage", e);
			jsonObject.put("msg", e.getMessage());
		} catch (Exception e1) {
			log.error("removeLogMessage", e1);
		}
		return jsonObject;
	}

	private void getDebugClientOfSelectedHiveAps(Set<Long> selectedIds) {
		Set<String> clientMacs = new HashSet<String>();
		if (null != selectedIds && !selectedIds.isEmpty()) {
			String locateMapName = null;
			// query hiveAP list
			List<?> hiveApInfos = QueryUtil.executeQuery(
					"select macAddress, mapContainer.mapName from "
							+ HiveAp.class.getSimpleName(), null,
					new FilterParams("id", selectedIds));
			if (!hiveApInfos.isEmpty()) {
				Set<String> apMacs = new HashSet<String>();
				for (Object object : hiveApInfos) {
					Object[] attrs = (Object[]) object;
					String mac = (String) attrs[0];
					locateMapName = (String) attrs[1];
					apMacs.add(mac);
				}
				// query debug client macs from client history table
				List<?> debugInfos = QueryUtil.executeQuery(
						"select bo.clientMac from "
								+ AhClientSessionHistory.class.getSimpleName()
								+ " bo", null,
						new FilterParams("apMac", apMacs));
				for (Object object : debugInfos) {
					clientMacs.add((String) object);
				}
				// query debug client macs from active client table
//				debugInfos = QueryUtil.executeQuery("select bo.clientMac from "
//						+ AhClientSession.class.getSimpleName() + " bo", null,
//						new FilterParams("apMac", apMacs));
				debugInfos = DBOperationUtil.executeQuery("select clientMac from ah_clientsession"
						, null,new FilterParams("apMac", apMacs));
				for (Object object : debugInfos) {
					clientMacs.add((String) object);
				}
				if (hiveApInfos.size() == 1 && null != locateMapName) {
					selMapName = locateMapName;
				}
			}
		}
		getDebugClientDatas(clientMacs);
	}

	private void getDebugClientOfSelectedActiveClients(Set<Long> selectedIds) {
		Set<String> clientMacs = new HashSet<String>();
		if (null != selectedIds && !selectedIds.isEmpty()) {
//			List<?> list = QueryUtil.executeQuery("select bo.clientMac from "
//					+ AhClientSession.class.getSimpleName() + " bo", null,
//					new FilterParams("id", selectedIds));
			List<?> list = DBOperationUtil.executeQuery("select clientMac from ah_clientsession", null,
					new FilterParams("id", selectedIds));
			for (Object object : list) {
				clientMacs.add((String) object);
			}
		}
		getDebugClientDatas(clientMacs);
	}

	private void getDebugClientOfSelectedMap(Long mapNodeId) {
		MapContainerNode bo = QueryUtil.findBoById(MapContainerNode.class,
				mapNodeId);
		if (null != bo) {
			// filled map name
			stringForTitle = "Location: " + bo.getMapName();
			selMapName = bo.getMapName();
			List<?> list = QueryUtil.executeQuery("select bo.id from "
					+ HiveAp.class.getSimpleName() + " bo", null,
					new FilterParams("bo.mapContainer.id", mapNodeId));
			if (!list.isEmpty()) {
				Set<Long> ids = new HashSet<Long>();
				for (Object object : list) {
					ids.add((Long) object);
				}
				getDebugClientOfSelectedHiveAps(ids);
				return;
			}
		}
		getDebugClientDatas(null);
	}

	private void getDebugClientDatas(Set<String> clientMacs) {
		String noMapKey = " ";
		Map<String, Set<String>> mapAndHiveAp = new HashMap<String, Set<String>>();
		Set<String> mapNames = new HashSet<String>();
		Set<String> apMacs = new HashSet<String>();
		Map<String, String> macAndHostname = new HashMap<String, String>();

		List<?> hiveAps = QueryUtil.executeQuery(
				"select macAddress, hostName from "
						+ HiveAp.class.getSimpleName(), null, new FilterParams(
						"manageStatus", HiveAp.STATUS_MANAGED), domainId);
		for (Object object : hiveAps) {
			Object[] attrs = (Object[]) object;
			String macAddress = (String) attrs[0];
			String hostname = (String) attrs[1];
			apMacs.add(macAddress);
			macAndHostname.put(macAddress, hostname);
		}

		List<?> list = QueryUtil.executeQuery(
				"select macAddress, mapContainer.mapName from "
						+ HiveAp.class.getSimpleName(), null, new FilterParams(
						"manageStatus", HiveAp.STATUS_MANAGED), domainId);
		Set<String> onMapAps = new HashSet<String>();
		for (Object object : list) {
			Object[] attrs = (Object[]) object;
			String macAddress = (String) attrs[0];
			String mapName = (String) attrs[1];
			// add into a temp set to store all on map aps
			onMapAps.add(macAddress);
			// add into map set
			mapNames.add(mapName);
			// add into mapAndHiveAp map
			Set<String> apOnMap = mapAndHiveAp.get(mapName);
			if (null == apOnMap) {
				apOnMap = new HashSet<String>();
				mapAndHiveAp.put(mapName, apOnMap);
			}
			apOnMap.add(macAddress);
		}
		for (String macAddress : apMacs) {
			if (!onMapAps.contains(macAddress)) {
				// add into mapAndHiveAp map (blank map)
				Set<String> apOnMap = mapAndHiveAp.get(noMapKey);
				if (null == apOnMap) {
					apOnMap = new HashSet<String>();
					mapAndHiveAp.put(noMapKey, apOnMap);
					mapNames.add(noMapKey);
				}
				apOnMap.add(macAddress);
			}
		}

		if (null == clientMacs) {
			clientMacs = new HashSet<String>();
		}
		prepareSelectionData(mapNames, apMacs, clientMacs, mapAndHiveAp,
				macAndHostname);
	}

	private void prepareSelectionData(Set<String> mapNames, Set<String> apMacs,
			Set<String> clientMacs, Map<String, Set<String>> mapAndHiveAp,
			Map<String, String> macAndHostname) {
		if (mapNames.size() > 1) {
			// filled map list
			selectedMaps.add("All");
			selectedMaps.addAll(mapNames);
		} else if (mapNames.size() == 1) {
			selectedMaps.addAll(mapNames);
		}
		if (apMacs.size() > 1) {
			// filled hiveAp list
			selectedHiveAps.add(new TextItem("All", "All"));
		}
		for (String mac : apMacs) {
			String hostname = macAndHostname.get(mac);
			selectedHiveAps.add(new TextItem(mac, hostname));
		}
		if (clientMacs.size() > 1) {
			// filled hiveAp list
			selectedClients.add("All");
			selectedClients.addAll(clientMacs);
		} else if (clientMacs.size() == 1) {
			selectedClients.addAll(clientMacs);
		}
		Set<String> maps = mapAndHiveAp.keySet();
		for (String mapName : maps) {
			Set<String> hiveApMacs = mapAndHiveAp.get(mapName);
			if (null == hiveApMacs || hiveApMacs.isEmpty()) {
				clientDebugInfos.add(mapName + "||");
			} else {
				for (String hiveApMac : hiveApMacs) {
					String hostname = macAndHostname.get(hiveApMac);
					if (null == hostname) {
						clientDebugInfos.add(mapName + "|" + hiveApMac + "|");
					} else {
						clientDebugInfos.add(mapName + "|" + hiveApMac + "|"
								+ hostname);
					}
				}
			}
		}
		
		MgrUtil.setSessionAttribute(CURRENT_CLIENTS_SET_SESSION, clientMacs);
		MgrUtil.setSessionAttribute(CURRENT_APS_SET_SESSION, apMacs);
		MgrUtil.setSessionAttribute(CURRENT_AP_MAPS_MAP_SESSION, mapAndHiveAp);
	}
	
	public static void clearSessions() {
	    MgrUtil.removeSessionAttribute(CURRENT_CLIENTS_SET_SESSION);
	    MgrUtil.removeSessionAttribute(CURRENT_APS_SET_SESSION);
	    MgrUtil.removeSessionAttribute(CURRENT_AP_MAPS_MAP_SESSION);
	}

	private Set<String> getSelectedClient(String clients) {
		return getStringSetSplitComma(clients);
	}
	
	private Set<String> getSelectedStages(String stages) {
		return getStringSetSplitComma(stages);
	}

	private Set<String> getStringSetSplitComma(String str) {
		Set<String> set = new HashSet<String>();
		if (StringUtils.isNotBlank(str)) {
			String[] clientArray = str.split(",");
			set.addAll(Arrays.asList(clientArray));
		}
		return set;
	}

	private JSONObject clearDebugLogs(String viewType, String filterType,
			Set<String> clients, Set<String> stages, int rowLimit) throws JSONException {
		JSONObject object = new JSONObject();
		// if (groupId != 0) {
		if (clients.isEmpty()) {
			clientMonitorMgmt
					.clearNotifications(getDomain().getDomainName(), true);
		} else {
			clientMonitorMgmt.clearNotifications(getDomain().getDomainName(),
					clients, true);
		}
		object.put("logs", getLogMessages(viewType, filterType, clients, stages,
				rowLimit));
		// }
		return object;
	}

	private JSONObject getClientDebugData(String viewType, String filterType,
			Set<String> clients, Set<String> stages, int rowLimit) {
		JSONObject object = new JSONObject();
		// if (groupId != 0) {
		try {
			object.put("clients", getClients());
			object.put("logs", getLogMessages(viewType, filterType, clients,
					stages, rowLimit));
		} catch (JSONException e) {
			log.error("getClientLogs", "get client debug error.", e);
		}
		// }
		return object;
	}

	private JSONArray getClients() throws JSONException {
		JSONArray array = new JSONArray();
		Collection<ClientMonitor> requests = clientMonitorMgmt
				.getRequests(getDomain().getDomainName());
		if (null == requests) {
			return array;
		}

		for (ClientMonitor req : requests) {
			JSONObject jsonClient = new JSONObject();
			String clientMac = req.getClientMac();
			String apMac = req.getHiveApMac();
			// int rate = req.getCompletionRate();
			int cookieId = req.getCookieId();
			boolean isActive = req.isActive();
			boolean excludeProbe = req.isFilteringProbeEvents();
			boolean enablePerformanceFlag = req.isEnableMonitorPerformance();
			String hostname = req.getHiveAp() != null ? req.getHiveAp()
					.getHostName() : null;
			ClientMonitorNotification recentInterestEvent = req
					.getRecentInterestEvent();
			String latestMessage = recentInterestEvent != null ? recentInterestEvent
					.getDescription()
					: null;
			if (null != latestMessage) {
				latestMessage = latestMessage.replace(" ", "&nbsp;").replace(
						"<", "&lt;").replace(">", "&gt;").replace("\n",
						"<br />");
			}
			setRequestState(jsonClient, req.getDebugState(), true);
			wrapJSONClient(jsonClient, clientMac, apMac, hostname,
					latestMessage, cookieId, isActive, enablePerformanceFlag);
			jsonClient.put(excludeProbeLabel, excludeProbe);
			array.put(jsonClient);
			//record state for report
			try {
				ClientHistoryTracking.monitored
				(
					Long.parseLong(clientMac, 16),
					getDomainId(),
					true
				);
			} catch (Exception e) {
				log.error("ClientHistoryTracking.monitored", "error occored.", e);
			}
		}
		return array;
	}

	private void wrapJSONClient(JSONObject jsonClient, String clientMac,
			String apMac, String hostname, String latestMessage, int cookieId,
			boolean isActive, boolean enablePerformanceFlag) throws JSONException {
		if (null == jsonClient) {
			jsonClient = new JSONObject();
		}
		if (null != clientMac) {
			jsonClient.put(clientMacLabel, clientMac);
		}
		if (null != apMac) {
			jsonClient.put(hiveApLabel, apMac);
		}
		if (null != hostname) {
			jsonClient.put("hostname", hostname);
		}
		// String status = "<div class=\"a0\"><div class=\"a1\" style=\"width:"
		// + rate + "px\">" + rate + "%</div></div>";
		// use latest message instead of complete rate
		if (null != latestMessage) {
			jsonClient.put(logStatusLabel, latestMessage);
		}

		if (cookieId > 0) {
			jsonClient.put("cookieId", cookieId);
		}
		if (isActive) {
			jsonClient.put("active", true);
		}
		jsonClient.put("perfOn", enablePerformanceFlag);
	}

	private JSONArray getLogMessages(String viewType, String filterType,
			Set<String> clients, Set<String> includedStages, int rowLimit) throws JSONException {
		JSONArray array = new JSONArray();
		List<ClientMonitorNotification> list = clients.isEmpty() ? clientMonitorMgmt
				.getNotifications(getDomain().getDomainName(), true)
				: clientMonitorMgmt.getNotifications(getDomain().getDomainName(),
						clients, true);
		if (null == list || list.isEmpty()) {
			return array;
		}
		
		List<ClientMonitorNotification> displayedNotifications = new ArrayList<ClientMonitorNotification>(list);
		
		// Get rid of the notifications whose stage is not contained in the 'includedStages'.
		for (Iterator<ClientMonitorNotification> notifIter = displayedNotifications.iterator(); notifIter.hasNext();) {
			ClientMonitorNotification cmn = notifIter.next();
			Stage stage = cmn.getStage();

			if (null != stage && !includedStages.contains(stage.getTextValue())) {
				notifIter.remove();
			}
		}

		if (displayedNotifications.isEmpty()) {
			return array;
		}

		ClientMonitorSortImpl impl = new ClientMonitorSortImpl(
				convertViewType(viewType), convertFilterType(filterType));
		List<ClientMonitorNotification> sortedList = impl.sort(displayedNotifications);
		int maxApNameLen = impl.getMaxApNameLen(sortedList);
		StringBuilder sb = new StringBuilder(impl.getMessageHeader(sortedList)
				.replace(" ", "&nbsp;").replace("<", "&lt;").replace(">",
						"&gt;").replace("\n", "<br />"));
		if (rowLimit > 0) {
			int fromIndex = sortedList.size() - rowLimit;
			// Truncate latest number of 'rowLimit' items if request size
			// is less than the actual size.
			if (fromIndex > 0) {
				sortedList = sortedList.subList(fromIndex, sortedList.size());
			}
		}
		for (ClientMonitorNotification notify : sortedList) {
		    String line = "";
		    if(notify.isPerformance()) {
		        line = notify.getPerformanceFormattedMessage(maxApNameLen,getDomain());
		    } else {
		        line = notify.getFormattedMessage(maxApNameLen,getDomain()).replace(" ",
					"&nbsp;").replace("<", "&lt;").replace(">", "&gt;");
		    }
			line = notify.isSuccess() ? line : "<span class='failedMsg'>"
					+ line + "</span>";
			sb.append(line).append("<br />");
		}
		JSONObject obj = new JSONObject();
		obj.put("v", sb.toString());
		array.put(obj);
		return array;
	}

	private JSONArray initializeClientDebugs(String clientMacs, boolean enalbePerf)
			throws JSONException {
		JSONArray jsonArray = new JSONArray();
		if (null == clientMacs) {
			return jsonArray;
		}
		String[] debugEntries = clientMacs.split(",");
		for (String debugEntry : debugEntries) {
			if (debugEntry.indexOf("|") > -1) {
				String[] client_ap = debugEntry.split("\\|");
				JSONObject object = addClientDebug(client_ap[1].toUpperCase(),
						client_ap[0].toUpperCase(), enalbePerf);
				jsonArray.put(object);
			}
		}
		handleAllOptions(jsonArray, debugEntries, enalbePerf);
		return jsonArray;
	}

    /**
     * Fixed Bug 16962
     * Handle the add client operation if any All option is selected to avoid the HTTP 414 error.<br>
     * The HTTP error will happen when the URL exceed the limit length of GET method.<br>
     * As previous operation concatenated the URL with the {ClientMAC|HiveAPMAC} pairs, the error will occur if the HiveAP more than 500. 
     * @author Yunzhi Lin
     * - Time: Apr 18, 2012 7:10:38 PM
     * @param jsonArray The JSON object should be empty but no NULL.
     * @param debugEntries The String array should be [ClientMAC|All, LocationName|All, HiveAPMAC|All] format (must contains the All keyword).
     * @param enalbePerf The flag of enable the Client Monitor Performance
     * @throws JSONException 
     */
    @SuppressWarnings("unchecked")
    private void handleAllOptions(JSONArray jsonArray, String[] debugEntries, boolean enalbePerf) throws JSONException {
        if(jsonArray.length() == 0 && debugEntries.length == 3) {
		    // Handle the Specific parameters
		    String client = debugEntries[0];
		    String location = debugEntries[1];
		    String device = debugEntries[2];
		    
            if (StringUtils.isNotBlank(client) && StringUtils.isNotBlank(device)) {
                final String keyword = "all";
                final Set<String> clients = (Set<String>) MgrUtil
                        .getSessionAttribute(CURRENT_CLIENTS_SET_SESSION);
                final Set<String> apMacs = (Set<String>) MgrUtil
                        .getSessionAttribute(CURRENT_APS_SET_SESSION);
                final Map<String, Set<String>> mapAndHiveAp = (Map<String, Set<String>>) MgrUtil
                        .getSessionAttribute(CURRENT_AP_MAPS_MAP_SESSION);
                
                if(client.equalsIgnoreCase(keyword) && device.equalsIgnoreCase(keyword)) {
                    // Client = All, HiveAp = All
                    if(null == clients) {
                        return;
                    }
                    if(location.equalsIgnoreCase(keyword)
                            ||StringUtils.isBlank(location)) {
                        // Location = All || Location = ''
                        if(null == apMacs) {
                            return;
                        }
                        for (String clientMac : clients) {
                            for (String hiveApMac : apMacs) {
                                JSONObject object = addClientDebug(hiveApMac.toUpperCase(),
                                        clientMac.toUpperCase(), enalbePerf);
                                jsonArray.put(object);
                            }
                        }
                    } else {
                        // Location != All
                        if(null == mapAndHiveAp) {
                            return;
                        }
                        Set<String> subApMacs = mapAndHiveAp.get(location);
                        if(null == subApMacs) {
                            return;
                        }
                        for (String clientMac : clients) {
                            for (String hiveApMac : subApMacs) {
                                JSONObject object = addClientDebug(hiveApMac.toUpperCase(),
                                        clientMac.toUpperCase(), enalbePerf);
                                jsonArray.put(object);
                            }
                        }
                    }
                } else if(client.equalsIgnoreCase(keyword)) {
                    // Client = All, HiveAp != All
                    if(location.equalsIgnoreCase(keyword)) {
                        // Location = All
                    } else {
                        // Location != All 
                    }
                    if(null == clients) {
                        return;
                    }
                    for (String clientMac : clients) {
                        JSONObject object = addClientDebug(device.toUpperCase(),
                                clientMac.toUpperCase(), enalbePerf);
                        jsonArray.put(object);
                    }
                } else if(device.equalsIgnoreCase(keyword)) {
                    // Client != All, HiveAp = All
                    if(location.equalsIgnoreCase(keyword) 
                            || StringUtils.isBlank(location)) {
                        // Location = All || Location = ''
                        if(null == apMacs) {
                            return;
                        }
                        for (String hiveApMac : apMacs) {
                            JSONObject object = addClientDebug(hiveApMac.toUpperCase(),
                                    client.toUpperCase(), enalbePerf);
                            jsonArray.put(object);
                        }
                    } else {
                        // Location != All
                        Set<String> subApMacs = mapAndHiveAp.get(location);
                        if(null == subApMacs) {
                            return;
                        }
                        for (String hiveApMac : subApMacs) {
                            JSONObject object = addClientDebug(hiveApMac.toUpperCase(),
                                    client.toUpperCase(), enalbePerf);
                            jsonArray.put(object);
                        }
                    }
                }
            }
		}
    }

	private JSONObject addClientDebug(String apMac, String clientMac, boolean enalbePerf)
			throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(clientMacLabel, clientMac);
			jsonObject.put(hiveApLabel, apMac);
			// send request to start debug;
			HiveAp hiveAp = QueryUtil.findBoByAttribute(HiveAp.class,
					"macAddress", apMac);
			String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
			        enalbePerf ? "4.1.4.0" : "3.3.1.0");
			if(null == errorMsg && enalbePerf) {
			    errorMsg = isExceedMonitorAmount(apMac, clientMac, errorMsg);
			}
			jsonObject.put("hostname", hiveAp.getHostName());
			if (null != errorMsg) {
				jsonObject.put("start_failed", true);
				jsonObject.put("msg", errorMsg);
			} else {
				ClientMonitor reqEvent = new ClientMonitor(hiveAp, request
						.getSession().getId(), clientMac, enalbePerf);
				reqEvent = clientMonitorMgmt.addRequest(reqEvent);
				if (reqEvent.getCookieId() > 0) {
					jsonObject.put("cookieId", reqEvent.getCookieId());
				}
				setRequestState(jsonObject, reqEvent.getDebugState(), true);
			}
		} catch (DebugException e) {
			log.error("startClientDebug", "startClientDebug error.", e);
			jsonObject.put("start_failed", true);
			jsonObject.put("msg", e.getMessage());
		} catch (Exception e) {
			log.error("startClientDebug", "startClientDebug error.", e);
			jsonObject.put("start_failed", true);
			jsonObject.put("msg", "Start client monitor error.");
		}
		return jsonObject;
	}

    /**
     * Limit the monitoring amount for performance.
     * 
     * @author Yunzhi Lin
     * - Time: Mar 19, 2012 12:24:04 PM
     * @param apMac HiveAp MAC
     * @param clientMac Client MAC
     * @param errorMsg Error message
     * @return new error message if exceed the limit.
     */
    private String isExceedMonitorAmount(String apMac, String clientMac, String errorMsg) {
        String domainName = getDomain().getDomainName();
        int performanceAmount = 5;
        Collection<ClientMonitor> clientRequests;
        if(null == domainName) {
            clientRequests = clientMonitorMgmt.getRequests();
        } else {
            clientRequests = clientMonitorMgmt.getRequests(domainName);
        }
        if(!clientRequests.isEmpty()) {
            int count = 0;
            for (ClientMonitor monitor : clientRequests) {
                if(apMac.equalsIgnoreCase(monitor.getHiveApMac())
                        && !clientMac.equalsIgnoreCase(monitor.getClientMac())) {
                    ++count;
                }
            }
            if(count >= performanceAmount) {
                errorMsg = "Current max support 5 clients for monitor performance.";
            }
        }
        return errorMsg;
    }
    
    private JSONObject checkStartClientDebugs(String clientMac) throws JSONException {
    	JSONObject jsonobj = new JSONObject();
    	if (null == clientMac) {
			return jsonobj;
		}
		List<AhClientSession> clientCount = DBOperationUtil.executeQuery(AhClientSession.class,null, new FilterParams(
				"connectstate=? and owner=? and clientMac=?",
				new Object[] {
						AhClientSession.CONNECT_STATE_UP,
						getDomainId(),
						clientMac
						}));
    	if (clientCount == null || clientCount.isEmpty()) {
    		jsonobj.put("t", true);
    		return jsonobj;
    	}
    	
    	for(AhClientSession item: clientCount) {
    		if (!item.isWirelessClient()) {
    			jsonobj.put("t", false);
        		return jsonobj;
    		}
    	}
    	jsonobj.put("t", true);
		return jsonobj;
    }

	private JSONArray startClientDebugs(String clientMac) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		if (null == clientMac) {
			return jsonArray;
		}
		clientMonitorMgmt.initiateRequests(getDomain().getDomainName(), clientMac);
		jsonArray = getClients();
		return jsonArray;
	}

	private JSONArray stopClientDebugs(String clientMac, String apMacs,
			String cookieIds) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		if (null == apMacs || null == cookieIds) {
			return jsonArray;
		}
		Collection<ClientMonitor> results = clientMonitorMgmt.terminateRequests(
				getDomain().getDomainName(), clientMac);

		String[] debugAps = apMacs.split(",");
		String[] strCookieIds = cookieIds.split(",");
		for (int i = 0; i < debugAps.length; i++) {
			String apMac = debugAps[i];
			ClientMonitor result = null;
			if (null != results) {
				for (ClientMonitor rst : results) {
					if (apMac.equals(rst.getHiveApMac())) {
						result = rst;
						break;
					}
				}
			}
			if (i < strCookieIds.length) {
				try {
					int cookieId = Integer.parseInt(strCookieIds[i]);
					JSONObject object = stopClientDebug(clientMac, apMac,
							cookieId, result);
					jsonArray.put(object);
					//record state for report
					ClientHistoryTracking.monitored
					(
						Long.parseLong(clientMac, 16),
						getDomainId(),
						false
					);
				} catch (NumberFormatException e) {
				}
			}
		}
		return jsonArray;
	}

	private JSONObject stopClientDebug(String clientMac, String apMac,
			int cookieId, ClientMonitor rstEvent) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(clientMacLabel, clientMac);
			jsonObject.put(hiveApLabel, apMac);
			jsonObject.put("cookieId", cookieId);

			if (null == rstEvent) {
				// the request is not exist
				jsonObject.put("stop_suc", true);
				jsonObject.put("msg", "The client monitor isn't started.");
			} else {
				setRequestState(jsonObject, rstEvent.getDebugState(), true);
			}
		} catch (JSONException e) {
			log.error("stopClientDebug", "stopClientDebug error.", e);
			jsonObject.put("stop_failed", true);
			jsonObject.put("msg", "Stop client monitor error.");
		}
		return jsonObject;
	}

	private JSONObject setRequestState(JSONObject jsonObject, DebugState debugState,
			boolean showSucMsg) throws JSONException {
		String str_state = null;
		String msg = debugState.getDescription();

		switch (debugState.getState()) {
		case INITIATION_REQUESTED:
			str_state = "starting";
			break;
		case INITIATION_RESPONSED:
			str_state = "start_suc";

			if (!showSucMsg) {
				msg = "";
			}
			break;
		case INITIATION_FAILED:
			str_state = "start_failed";
			break;
		case FINISHED:
			str_state = "finished";
			break;
		case ABORTED:
			str_state = "aborted";
			break;
		case STOPPED:
			str_state = "stop_suc";
			break;
		case UNINITIATED:
			str_state = "init";
		default:
			break;
		}

		if (null != str_state) {
			jsonObject.put(str_state, "true");
			jsonObject.put("msg", msg);
		}
		return jsonObject;
	}

	public int getMaxClientMonitoringCount() {
		int maxCount = clientMonitorMgmt.getMaxClients();
		// int vhmCount = CacheMgmt.getInstance().getCacheDomains().size();
		Collection<Integer> acceptedStatus = new ArrayList<Integer>(2);
		acceptedStatus.add(HmDomain.DOMAIN_DEFAULT_STATUS);
		acceptedStatus.add(HmDomain.DOMAIN_BACKUP_STATUS);

		int vhmCount = (int) QueryUtil.findRowCount(HmDomain.class,
				new FilterParams(
						"lower(domainName) != :s1 and runStatus in (:s2)",
						new Object[] { HmDomain.GLOBAL_DOMAIN.toLowerCase(),
								acceptedStatus }));
		log.info("getMaxClientMonitoringCount", "max client monitoring:"
				+ maxCount + ", vhm count:" + vhmCount);
		int assignedCount = maxCount / vhmCount;

		if (assignedCount < 1) {
			assignedCount = 1;
		}

		return assignedCount;
	}

	public List<String> getOutdatedClients() {
		List<String> clients = clientMonitorMgmt
				.getUnmonitoredClientsWithLog(getDomain().getDomainName());
		if (null == clients) {
			clients = new ArrayList<String>();
		}
		// if (clients.isEmpty()) {
		// clients = new ArrayList<String>();
		// clients.add("000000000000");
		// clients.add("222222222222");
		// clients.add("000000000000");
		// clients.add("222222222222");
		// clients.add("000000000000");
		// clients.add("222222222222");
		// clients.add("000000000000");
		// clients.add("222222222222");
		// clients.add("000000000000");
		// clients.add("222222222222");
		// }
		return clients;
	}

	/* client tracking end */

	/* VLAN probe */
	private final String vlanIdKey = MgrUtil
			.getUserMessage("monitor.hiveAp.vlan.probe.column.vlanId");
	private final String vlanAvailableKey = MgrUtil
			.getUserMessage("monitor.hiveAp.vlan.probe.column.available");
	private final String vlanSubnetKey = MgrUtil
			.getUserMessage("monitor.hiveAp.vlan.probe.column.subnet");
	private String vlanProbeFrom = "1";
	private String vlanProbeTo = "1";
	private String vlanProbeRetries = "1";
	private String vlanProbeTimeout = "3";
	private boolean isEnterFromTool = false;

	public void setVlanProbeFrom(String vlanProbeFrom) {
		this.vlanProbeFrom = vlanProbeFrom;
	}

	public void setVlanProbeTo(String vlanProbeTo) {
		this.vlanProbeTo = vlanProbeTo;
	}

	public void setVlanProbeRetries(String vlanProbeRetries) {
		this.vlanProbeRetries = vlanProbeRetries;
	}

	public void setVlanProbeTimeout(String vlanProbeTimeout) {
		this.vlanProbeTimeout = vlanProbeTimeout;
	}

	public String getVlanProbeFrom() {
		return vlanProbeFrom;
	}

	public String getVlanProbeTo() {
		return vlanProbeTo;
	}

	public String getVlanProbeRetries() {
		return vlanProbeRetries;
	}

	public String getVlanProbeTimeout() {
		return vlanProbeTimeout;
	}

	public boolean getIsEnterFromTool() {
		return isEnterFromTool;
	}

	public boolean getIsHideWifi1() {
		try {
			HiveAp hiveAp = getSelectedHiveAp();
			if (null != hiveAp) {
				return hiveAp.getHiveApModel() == HiveAp.HIVEAP_MODEL_110;
			}
		} catch (Exception e) {
			log.error("getIsHideWifi1", "catch exception", e);
		}

		return false;
	}

	private JSONObject startVlanProbe(Long hiveApId, String vlanProbeFrom2,
			String vlanProbeTo2, String vlanProbeRetries2,
			String vlanProbeTimeout2, int debugGroupId) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			// send request to start debug;
			HiveAp hiveAp = QueryUtil.findBoById(HiveAp.class, hiveApId);
			String errorMsg = MapNodeAction.validateSelectedHiveAp(hiveAp,
					"3.3.1.0");
			if (null != errorMsg) {
				jsonObject.put("start_failed", true);
				jsonObject.put("msg", errorMsg);
			} else {
				VlanProbe reqEvent = new VlanProbe(hiveAp, request.getSession()
						.getId(), debugGroupId);
				short from = (null == vlanProbeFrom2 || ""
						.equals(vlanProbeFrom2)) ? 0 : (short) Integer
						.parseInt(vlanProbeFrom2);
				short to = (null == vlanProbeTo2 || "".equals(vlanProbeTo2)) ? 0
						: (short) Integer.parseInt(vlanProbeTo2);
				reqEvent.setVlanFrom(from);
				reqEvent.setVlanTo(to);
				if (null != vlanProbeRetries2 && !"".equals(vlanProbeRetries2)) {
					reqEvent.setRetryTimes((short) Integer
							.parseInt(vlanProbeRetries2));
				}
				if (null != vlanProbeTimeout2 && !"".equals(vlanProbeTimeout2)) {
					reqEvent.setTimeout((short) Integer
							.parseInt(vlanProbeTimeout2));
				}

				// int cookieId = debugger.addDebug(reqEvent);
				reqEvent = vlanProbeMgmt.addRequest(reqEvent);
				int cookieId = reqEvent.getCookieId();

				if (cookieId > 0) {
					jsonObject.put("cookieId", cookieId);
				}
				setRequestState(jsonObject, reqEvent.getDebugState(), false);
			}
		} catch (DebugException e) {
			log.error("startVlanProbe", "startVlanProbe error.", e);
			jsonObject.put("start_failed", true);
			jsonObject.put("msg", e.getMessage());
		} catch (Exception e) {
			log.error("startVlanProbe", "startVlanProbe error.", e);
			jsonObject.put("start_failed", true);
			jsonObject.put("msg", "Start VLAN Probe error.");
		}
		return jsonObject;
	}

	private JSONObject stopVlanProbe(int cookieId) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("cookieId", cookieId);
			// send request to stop debug;
			VlanProbe rstEvent = vlanProbeMgmt.terminateRequest(cookieId);
			if (null == rstEvent) {
				// the request is not exist
				jsonObject.put("stop_failed", true);
				jsonObject.put("msg", "The VLAN Probe isn't started.");
			} else {
				setRequestState(jsonObject, rstEvent.getDebugState(), false);
			}
		} catch (JSONException e) {
			log.error("stopVlanProbe", "stopVlanProbe error.", e);
			jsonObject.put("stop_failed", true);
			jsonObject.put("msg", "Stop VLAN Probe error.");
		}
		return jsonObject;
	}

	private JSONObject pollVlanProbe(int cookieId) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("states", getVlanProbeState(cookieId));
		jsonObject.put("vlanProbes", getVlanProbes(cookieId));
		return jsonObject;
	}

	private JSONObject getVlanProbeState(int cookieId) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		VlanProbe probeReq = vlanProbeMgmt.getRequest(cookieId);
		if (null != probeReq) {
			setRequestState(jsonObject, probeReq.getDebugState(), false);
		}
		return jsonObject;
	}

	private JSONArray getVlanProbes(int cookieId) throws JSONException {
		JSONArray array = new JSONArray();
		List<VlanProbeNotification> list = vlanProbeMgmt.getNotifications(cookieId);

		if (list != null) {
			for (VlanProbeNotification notify : list) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put(vlanIdKey, notify.getVlan());
				String ip = notify.getIpAddress();
				if (null == ip || "".equals(ip) || "0.0.0.0".equals(ip)) {
					jsonObject.put(vlanAvailableKey, "No");
				} else {
					jsonObject.put(vlanAvailableKey, "Yes");
					jsonObject.put(vlanSubnetKey, notify.getIpAddress() + "/"
							+ String.valueOf(notify.getNetmask()));
				}
				array.put(jsonObject);
			}
		}

		return array;
	}

	/* VLAN probe end */

	private void closeDebugGroup(int debugGroupId) {
		vlanProbeMgmt.removeGroupRequests(debugGroupId);
	}

	/* CLI window begin ==================== */
	private final String hostNameKey = MgrUtil
			.getUserMessage("hiveap.tools.cliWindow.hostName");

	private final String statusKey = MgrUtil
			.getUserMessage("hiveap.tools.cliWindow.status");

	private String command = null;

	/**
	 * getter of command
	 * 
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * setter of command
	 * 
	 * @param command
	 *            the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	private File upload = null;
	private String uploadContentType = null;
	private String uploadFileName = null;

	/**
	 * getter of upload
	 * 
	 * @return the upload
	 */
	public File getUpload() {
		return upload;
	}

	/**
	 * setter of upload
	 * 
	 * @param upload
	 *            the upload to set
	 */
	public void setUpload(File upload) {
		this.upload = upload;
	}

	/**
	 * getter of uploadFileName
	 * 
	 * @return the uploadFileName
	 */
	public String getUploadFileName() {
		return uploadFileName;
	}

	/**
	 * setter of uploadFileName
	 * 
	 * @param uploadFileName
	 *            the uploadFileName to set
	 */
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	/**
	 * getter of uploadContentType
	 * 
	 * @return the uploadContentType
	 */
	public String getUploadContentType() {
		return uploadContentType;
	}

	/**
	 * setter of uploadContentType
	 * 
	 * @param uploadContentType
	 *            the uploadContentType to set
	 */
	public void setUploadContentType(String uploadContentType) {
		this.uploadContentType = uploadContentType;
	}

	private void initCLIWindow() {
		/*
		 * put selected HiveAps into command executor
		 */
		String source = request.getSession().getId();
		List<String> hosts = getSelectedAPHostNames();

		if (hosts == null) {
			return;
		}

		List<CommandStatus> commands = new ArrayList<CommandStatus>();

		for (String host : hosts) {
			CommandStatus command = new CommandStatus(host);
			commands.add(command);
		}

		if (hosts.size() == 1) {
			stringForTitle = hosts.get(0);
		} else {
			stringForTitle = "Selected "+NmsUtil.getOEMCustomer().getAccessPonitName()+"s";
		}

		CommandExecutorUtil.addCommands(source, commands);
	}

	private JSONObject executeCommand() throws JSONException {
		/*
		 * clear the existing commands in the pool
		 */
		CommandExecutorUtil.clearCommand(request.getSession().getId());

		/*
		 * execute the new command
		 */
		Command cmd = new Command(request.getSession().getId(), command);
		CommandExecutorUtil.executeCommand(cmd);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("running", true);

		return jsonObject;
	}

	private void importCommand() throws JSONException {
		if (upload == null || upload.length() == 0 || uploadFileName == null
				|| uploadFileName.length() == 0) {
			addActionError(MgrUtil
					.getUserMessage("error.hiveap.tools.cliWindow.import.fail"));
		}

		/*
		 * create a reader for the script
		 */
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(upload));
		} catch (FileNotFoundException e) {
			log
					.error(
							MgrUtil
									.getUserMessage("error.hiveap.tools.cliWindow.script.notFound"),
							e);
		}

		if (reader == null) {
			addActionError(MgrUtil
					.getUserMessage("error.hiveap.tools.cliWindow.import.fail"));
			return;
		}

		/*
		 * clear the existing commands in the pool
		 */
		CommandExecutorUtil.clearCommand(request.getSession().getId());

		/*
		 * read a command and execute
		 */
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0) {
					continue;
				}
				Command cmd = new Command(request.getSession().getId(), line);
				CommandExecutorUtil.executeCommand(cmd);
			}
		} catch (IOException e) {
			log
					.error(
							MgrUtil
									.getUserMessage("error.hiveap.tools.cliWindow.script.read"),
							e);
			addActionError(MgrUtil
					.getUserMessage("error.hiveap.tools.cliWindow.script.read"));
		}

		addActionMessage(MgrUtil
				.getUserMessage("error.hiveap.tools.cliWindow.import.ok"));
	}

	private JSONObject pollCLIExecution() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("status", getExecutionState());
		jsonObject.put("exeResult", getExecutionResult());

		return jsonObject;
	}

	private JSONObject getExecutionState() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		boolean running = false;
		Collection<CommandStatus> commands = CommandExecutorUtil
				.getCommands(request.getSession().getId());

		if (commands == null) {
			jsonObject.put("running", running);
			return jsonObject;
		}

		try {
			for (CommandStatus command : commands) {
				if (command.getSequence() == -1) {
					continue;
				}

				String result = command.getResult();

				if (Command.RESULT_PREPARE.equals(result)
						|| Command.RESULT_SENDED.equals(result)) {
					running = true;
					break;
				}
			}
		} catch (ConcurrentModificationException e) {
			running = true;
		}

		jsonObject.put("running", running);
		return jsonObject;
	}

	private JSONArray getExecutionResult() throws JSONException {
		JSONArray array = new JSONArray();
		Collection<CommandStatus> commands = CommandExecutorUtil
				.getCommands(request.getSession().getId());

		if (commands == null) {
			return array;
		}

		List<String> temp = new ArrayList<String>();

		try {
			for (CommandStatus command : commands) {
				if (command.getSequence() == -1) {
					continue;
				}

				if (temp.contains(command.getDestination())) {
					continue;
				} else {
					temp.add(command.getDestination());
				}

				StringBuffer hostname = new StringBuffer(
						getCustomHostname(command.getDestination()));
				String status = getCustomResult(command.getDestination(),
						commands);
				appendBR(hostname, status);

				JSONObject jsonObject = new JSONObject();
				jsonObject.put(hostNameKey, hostname.toString());
				jsonObject.put(statusKey, status);

				array.put(jsonObject);
			}
		} catch (ConcurrentModificationException e) {

		}

		return array;
	}

	private void appendBR(StringBuffer hostname, String status) {
		String sep = "<br />";
		String[] split = status.split(sep);

		for (int i = 0; i < split.length; i++) {
			hostname.append(sep);
		}
	}

	private String getCustomHostname(String hostname) {
		StringBuffer newHostname = new StringBuffer();

		newHostname.append("<font face=\"Courier New\">");
		newHostname.append(hostname);
		newHostname.append("</font>");

		return newHostname.toString();
	}

	private String getCustomResult(String destination,
			Collection<CommandStatus> commands) {
		if (destination == null || commands == null) {
			return null;
		}

		StringBuffer result = new StringBuffer();

		for (CommandStatus command : commands) {
			if (command.getSequence() == -1) {
				continue;
			}

			if (!destination.equals(command.getDestination())) {
				continue;
			}

			if (command.getCommand() != null) {
				result
						.append("<font face=\"Courier New\" size=3 color=green><b>");
				result.append("> ");
				result.append(command.getCommand());
				result.append("</b></font>");
				result.append("<br />");
				result.append("<font face=\"Courier New\">");

				/*
				 * add separate line
				 */
				for (int i = 0; i < command.getCommand().length() * 2; i++) {
					result.append("=");
				}

				result.append("<br />");
			}

			if (command.getResult() != null) {
				result.append(command.getResult());
			}

			result.append("</font>");
			result.append("<br /><br />");
		}

		return result.substring(0, result.length() - 12);
	}

	private List<String> getSelectedAPHostNames() {
		List<String> hosts = new ArrayList<String>();
		List<?> aps = null;

		if (allItemsSelected) {
			aps = QueryUtil.executeQuery("select hostname from "
					+ HiveAp.class.getSimpleName(), null, null, domainId);

		} else {
			Set<Long> apIds = this.getAllSelectedIds();

			if (apIds != null && !apIds.isEmpty()) {
				aps = QueryUtil.executeQuery("select hostName from "
						+ HiveAp.class.getSimpleName(), null, new FilterParams(
						"id", apIds), domainId);
			}
		}

		if (aps == null) {
			return hosts;
		}

		for (Object obj : aps) {
			hosts.add((String) obj);
		}

		return hosts;
	}

	private void cancelCommand() {
		String source = request.getSession().getId();
		CommandExecutorUtil.cancelCommand(source);
	}

	/* CLI window end ==================== */

	@Override
	public void prepare() throws Exception {
		super.prepare();
		//setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		String listTypeFromSession = (String) MgrUtil
				.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
		if("managedVPNGateways".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
		}else if( "managedRouters".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
		}else if( "managedSwitches".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_SWITCHES);
		}else if("managedDeviceAPs".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
		}else if("managedHiveAps".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}else{
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}
	}

	private HiveAp getSelectedHiveAp() throws Exception {
		HiveAp hiveAp = null;
		if (null != hiveApId) {
			hiveAp = QueryUtil.findBoById(HiveAp.class, hiveApId);
		} else {
			if(leafNodeId == null) {
				return null;
			}
			
			MapLeafNode leafNode = QueryUtil.findBoById(MapLeafNode.class,
					leafNodeId, this);
			if (null != leafNode) {
				hiveAp = leafNode.getHiveAp();
			}
		}
		return hiveAp;
	}

	private void prepareAvaliableHiveAPs() {
		hiveAps = getBoCheckItems("hostName", HiveAp.class, new FilterParams(
				"manageStatus", HiveAp.STATUS_MANAGED), new SortParams(
				"hostName"));
	}

	private Long hiveApId;
	private Long leafNodeId;
	private Long mapNodeId;
	private String stringForTitle;
	private String selMapName;
	private List<CheckItem> hiveAps;

	public String getSelMapName() {
		return selMapName;
	}

	public String getStringForTitle() {
		return stringForTitle;
	}

	public Long getHiveApId() {
		return hiveApId;
	}

	public void setHiveApId(Long hiveApId) {
		this.hiveApId = hiveApId;
	}

	public Long getLeafNodeId() {
		return leafNodeId;
	}

	public void setLeafNodeId(Long leafNodeId) {
		this.leafNodeId = leafNodeId;
	}

	public Long getMapNodeId() {
		return mapNodeId;
	}

	public void setMapNodeId(Long mapNodeId) {
		this.mapNodeId = mapNodeId;
	}

	public List<CheckItem> getHiveAps() {
		return hiveAps;
	}

	protected JSONArray jsonArray = null;
	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof MapLeafNode) {
			((MapLeafNode) bo).getHiveAp().getId();
		}
		return null;
	}

	public static List<String> getIncludedStages(){
		return Arrays.asList(getIncludedStagesValues());
	}
	
	public static String[] getAllIncludedStages(){
		return getIncludedStagesValues();
	}

	public static String[] getDefaultIncludedStages(){
		return getIncludedStagesValues();
	}
	
	private static String[] getIncludedStagesValues() {
		String[] stagesArray=new String[Stage.values().length];
		int index = 0;
		for (Stage stage : Stage.values()) {
			stagesArray[index++] = stage.getTextValue();
		}
		return stagesArray;
	}
	
	// packet capture ------------------START

	public static final int INTERFACE_WIFI0 = 2;
	public static final int INTERFACE_WIFI1 = 3;
	public static final int INTERFACE_BOTH = 20;

	// mark: not support 'both' interface item in wifi interface list for now.
	public static EnumItem[] getWifiInterfaceList() {
		return MgrUtil.enumItems("enum.interface.type.", new int[] {
				INTERFACE_WIFI0, INTERFACE_WIFI1 });
	}

	// static final int TRANSFER_PROTOCAL_SCP = 1;
	public static final int TRANSFER_PROTOCAL_LOCAL = 1;
	public static final int TRANSFER_PROTOCAL_TFTP = 2;

	public static EnumItem[] getTransferPtclList() {
		return MgrUtil.enumItems("enum.capture.transferPtcl.", new int[] {
				TRANSFER_PROTOCAL_LOCAL, TRANSFER_PROTOCAL_TFTP });
	}

	public static final int TRAFFIC_TYPE_ALL = 0;
	public static final int TRAFFIC_TYPE_CTL = 1;
	public static final int TRAFFIC_TYPE_DATA = 2;
	public static final int TRAFFIC_TYPE_MGMT = 3;

	public static EnumItem[] getTrafficTypeList() {
		return MgrUtil.enumItems("enum.capture.trafficType.", new int[] {
				TRAFFIC_TYPE_ALL, TRAFFIC_TYPE_CTL, TRAFFIC_TYPE_DATA,
				TRAFFIC_TYPE_MGMT });
	}

	public static EnumItem[] getSubTypeList() {
		return new EnumItem[0];
	}

	public static final int ERROR_CONDITION_ALL = 0;
	public static final int ERROR_CONDITION_CRC = 1;
	public static final int ERROR_CONDITION_DECRYPT = 2;
	public static final int ERROR_CONDITION_MIC = 3;
	public static final int ERROR_CONDITION_NO = 4;

	public static EnumItem[] getErrorConditionList() {
		return MgrUtil.enumItems("enum.capture.error.", new int[] {
				ERROR_CONDITION_ALL, ERROR_CONDITION_CRC,
				ERROR_CONDITION_DECRYPT, ERROR_CONDITION_MIC,
				ERROR_CONDITION_NO });
	}

	public static final int ETH_VALUE_ALL = 0;
	public static final int ETH_VALUE_ARP = 1;
	public static final int ETH_VALUE_IP = 2;
	public static final int ETH_VALUE_IPX = 3;
	public static final int ETH_VALUE_RARP = 4;

	public static EnumItem[] getEthValueList() {
		return MgrUtil.enumItems("enum.capture.eth.", new int[] {
				ETH_VALUE_ALL, ETH_VALUE_ARP, ETH_VALUE_IP, ETH_VALUE_IPX,
				ETH_VALUE_RARP });
	}

	private int captureInterface;

	private int captureCount;

	private boolean promiscuousWhenCapture;

	private boolean trafficFilterFlag;

	private int captureTrafficType;

	private int captureSubtype;

	private String captureBSSID;

	private String captureSrcMac;

	private String captureDestMac;

	private String captureTxMac;

	private String captureRxMac;

	private int captureErrorValue;

	private int captureEthType;

	private int wifi0TxNumber;

	private int wifi0RxNumber;

	private int wifi0TotalNumber;

	private int wifi1TxNumber;

	private int wifi1RxNumber;

	private int wifi1TotalNumber;

	// mark : always use filter id 1
	private boolean startCapture(HiveAp hiveAp, int wifiInterface)
			throws Exception {
		List<String> cliList = new LinkedList<String>();
		String filterID = "1";

		// traffic filter
		if (trafficFilterFlag) {
			String trafficType = CLICommonFunc.getEnumItemValue(MgrUtil
					.enumItems("enum.capture.trafficType.cliValue.", new int[] {
							TRAFFIC_TYPE_ALL, TRAFFIC_TYPE_CTL,
							TRAFFIC_TYPE_DATA, TRAFFIC_TYPE_MGMT }),
					captureTrafficType);
			String errorValue = CLICommonFunc.getEnumItemValue(
					getErrorConditionList(), captureErrorValue);
			String ethType = CLICommonFunc.getEnumItemValue(getEthValueList(),
					captureEthType);
			ethType = ethType.substring(ethType.indexOf(":") + 1);

			// subtype is null when traffic type is all or sub type is all
			String subType = (captureSubtype == -1 || captureTrafficType == TRAFFIC_TYPE_ALL) ? null
					: Integer.toHexString(captureSubtype);
			String cli_addFilter = AhCliFactory.getAddFilterCli(filterID,
					trafficType, subType, captureBSSID, captureSrcMac,
					captureDestMac, captureTxMac, captureRxMac, errorValue,
					ethType);

			// we need remove filter from ap at first, because 'filter' cli
			// support
			// increase filter or
			// modify, but can't remove
			String cli_removeFilter = AhCliFactory.getRemoveFilterCli(filterID);

			cliList.add(cli_removeFilter);
			cliList.add(cli_addFilter);
		}

		// save capture
		HmBeAdminUtil.setTftpEnable(true);
		// mark: get hivemanager ip from ap configure.
		// String tftpServer = HmBeOsUtil.getHiveManagerIPAddr();
		String tftpServer = hiveAp.getCapwapLinkIp();
		String wifi = CLICommonFunc.getEnumItemValue(getWifiInterfaceList(),
				wifiInterface);
		final String capturePathName = "/dump/"
				+ getCapturedFileName(hiveAp.getHostName(), wifi);

		// mark: save capture local, we need know local name in ap
		String cli_saveCapture = AhCliFactory.getSaveCaptureCli(wifi,
				CLICommonFunc.getEnumItemValue(getTransferPtclList(),
						TRANSFER_PROTOCAL_LOCAL), tftpServer, capturePathName,
				getCapturedFileName(hiveAp.getHostName(), wifi));
		cliList.add(cli_saveCapture);

		// start capture
		String count = String.valueOf(captureCount);

		String cli_startCapture = AhCliFactory.getStartCaptureCli(wifi, count,
				trafficFilterFlag, filterID, promiscuousWhenCapture);
		cliList.add(cli_startCapture);

		return sendCliRequestSync(hiveAp, cliList.toArray(new String[cliList
				.size()]));
	}

	// get packet capture file name
	private String getCapturedFileName(String apName, String wifi) {
		return apName + "_" + wifi + ".cap";
	}

	private boolean stopCapture(HiveAp hiveAp) throws Exception {
		String wifi = CLICommonFunc.getEnumItemValue(getWifiInterfaceList(),
				captureInterface);

		String cli = AhCliFactory.getStopCaptureCli(wifi);

		return sendCliRequestSync(hiveAp, new String[] { cli });
	}

	/**
	 * download capture file from ap to hm
	 * 
	 * @param hiveAp
	 *            -
	 * @throws Exception
	 *             -
	 * @return -
	 */
	private boolean tftpCaptureFile(HiveAp hiveAp) throws Exception {
		String host = NmsUtil.getRunningCapwapServer(hiveAp);
		String wifi = CLICommonFunc.getEnumItemValue(getWifiInterfaceList(),
				captureInterface);
		String captureFileName = getCapturedFileName(hiveAp.getHostName(), wifi);
		String cli;

		// Use HTTPS instead of TFTP for packet capture upload as of HM3.5.x
		if (NmsUtil.compareSoftwareVersion("3.5.0.0", hiveAp.getSoftVer()) > 0) {
			// enable tftp at first
			HmBeAdminUtil.setTftpEnable(true);
			cli = AhCliFactory.uploadCapture(captureFileName, "tftp://" + host
					+ ":/dump/" + captureFileName);
		} else {
			String webServerLoginUser = NmsUtil.getHMScpUser();
			String webServerLoginPwd = NmsUtil.getHMScpPsd();
			String proxy = hiveAp.getProxyName();
			int proxyPort = hiveAp.getProxyPort();
			String proxyLoginUser = hiveAp.getProxyUsername();
			String proxyLoginPwd = hiveAp.getProxyPassword();
			cli = AhCliFactory.uploadCaptureViaHttp(host, captureFileName,
					webServerLoginUser, webServerLoginPwd, proxy, proxyPort,
					proxyLoginUser, proxyLoginPwd);
		}

		// because download capture file maybe cost long time ,we set time out
		// value same as save image's.
		return sendCliRequestSync(hiveAp, new String[] { cli }, 15 * 60);
	}

	private boolean sendCliRequestSync(HiveAp ap, String[] clis) {
		BeCommunicationEvent response = BeTopoModuleUtil.sendSyncCliRequest(ap,
				clis, BeCliEvent.CLITYPE_NORMAL, 35);
		try {
			return BeTopoModuleUtil.isCliExeSuccess(response);
		} catch (BeCommunicationDecodeException e) {
			log.error("sendCliRequestSync", "sendCli request failed.", e);
			return false;
		}
	}

	private boolean sendCliRequestSync(HiveAp ap, String[] clis, int timeout) {
		BeCommunicationEvent response = BeTopoModuleUtil.sendSyncCliRequest(ap,
				clis, BeCliEvent.CLITYPE_NORMAL, timeout);
		try {
			return BeTopoModuleUtil.isCliExeSuccess(response);
		} catch (BeCommunicationDecodeException e) {
			log.error("sendCliRequestSync", "sendCli request failed.", e);
			return false;
		}
	}

	/**
	 * poll capture status
	 * 
	 * @param hiveAp
	 *            -
	 * @param wifi
	 *            -
	 * @throws Exception
	 *             -
	 * @return -
	 */
	private BeShowCaptureStatusResultEvent pollCaptureStatus(HiveAp hiveAp,
			int wifi) throws Exception {
		BeShowCaptureStatusEvent showCaptureStatusEvent = new BeShowCaptureStatusEvent();
		showCaptureStatusEvent.setAp(hiveAp);
		showCaptureStatusEvent
				.setWifiInterface(wifi == INTERFACE_WIFI0 ? BeShowCaptureStatusEvent.INTERFACE_WIFI0
						: BeShowCaptureStatusEvent.INTERFACE_WIFI1);
		showCaptureStatusEvent.setSequenceNum(HmBeCommunicationUtil
				.getSequenceNumber());
		showCaptureStatusEvent.buildPacket();
		BeCommunicationEvent resultEvent = HmBeCommunicationUtil
				.sendSyncRequest(showCaptureStatusEvent, 35);
		if (resultEvent == null) {

			log.warning("pollCaptureStatus",
					"failed to poll capture status, hiveap="
							+ hiveAp.getHostName()
							+ ",sync request API return null");

		} else if (resultEvent.getMsgType() == BeCommunicationConstant.MESSAGETYPE_CAPWAPCLIENTEVENTRSP) {

			log.warning("pollCaptureStatus",
					"failed to poll capture status, hiveap="
							+ hiveAp.getHostName() + ",result="
							+ resultEvent.getResult());

		} else if (resultEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
			BeCapwapClientResultEvent capwapClientResultEvent = (BeCapwapClientResultEvent) resultEvent;
			if (capwapClientResultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SHOWCAPTURESTATUS) {
				return (BeShowCaptureStatusResultEvent) capwapClientResultEvent;
			}
		}

		return null;
	}

	public boolean isTrafficFilterFlag() {
		return trafficFilterFlag;
	}

	public void setTrafficFilterFlag(boolean trafficFilterFlag) {
		this.trafficFilterFlag = trafficFilterFlag;
	}

	public void setCaptureSubtype(int captureSubtype) {
		this.captureSubtype = captureSubtype;
	}

	public int getCaptureSubtype() {
		return captureSubtype;
	}

	public int getWifi0RxNumber() {
		return wifi0RxNumber;
	}

	public void setWifi0RxNumber(int wifi0RxNumber) {
		this.wifi0RxNumber = wifi0RxNumber;
	}

	public int getWifi0TotalNumber() {
		return wifi0TotalNumber;
	}

	public void setWifi0TotalNumber(int wifi0TotalNumber) {
		this.wifi0TotalNumber = wifi0TotalNumber;
	}

	public int getWifi0TxNumber() {
		return wifi0TxNumber;
	}

	public void setWifi0TxNumber(int wifi0TxNumber) {
		this.wifi0TxNumber = wifi0TxNumber;
	}

	public int getWifi1RxNumber() {
		return wifi1RxNumber;
	}

	public void setWifi1RxNumber(int wifi1RxNumber) {
		this.wifi1RxNumber = wifi1RxNumber;
	}

	public int getWifi1TotalNumber() {
		return wifi1TotalNumber;
	}

	public void setWifi1TotalNumber(int wifi1TotalNumber) {
		this.wifi1TotalNumber = wifi1TotalNumber;
	}

	public int getWifi1TxNumber() {
		return wifi1TxNumber;
	}

	public void setWifi1TxNumber(int wifi1TxNumber) {
		this.wifi1TxNumber = wifi1TxNumber;
	}

	public String getCaptureBSSID() {
		return captureBSSID;
	}

	public void setCaptureBSSID(String captureBSSID) {
		this.captureBSSID = captureBSSID;
	}

	public String getCaptureDestMac() {
		return captureDestMac;
	}

	public void setCaptureDestMac(String captureDestMac) {
		this.captureDestMac = captureDestMac;
	}

	public int getCaptureErrorValue() {
		return captureErrorValue;
	}

	public void setCaptureErrorValue(int captureErrorValue) {
		this.captureErrorValue = captureErrorValue;
	}

	public int getCaptureEthType() {
		return captureEthType;
	}

	public void setCaptureEthType(int captureEthType) {
		this.captureEthType = captureEthType;
	}

	public String getCaptureRxMac() {
		return captureRxMac;
	}

	public void setCaptureRxMac(String captureRxMac) {
		this.captureRxMac = captureRxMac;
	}

	public String getCaptureSrcMac() {
		return captureSrcMac;
	}

	public void setCaptureSrcMac(String captureSrcMac) {
		this.captureSrcMac = captureSrcMac;
	}

	public int getCaptureTrafficType() {
		return captureTrafficType;
	}

	public void setCaptureTrafficType(int captureTrafficType) {
		this.captureTrafficType = captureTrafficType;
	}

	public String getCaptureTxMac() {
		return captureTxMac;
	}

	public void setCaptureTxMac(String captureTxMac) {
		this.captureTxMac = captureTxMac;
	}

	public int getCaptureCount() {
		return captureCount;
	}

	public void setCaptureCount(int captureCount) {
		this.captureCount = captureCount;
	}

	public int getCaptureInterface() {
		return captureInterface;
	}

	public void setCaptureInterface(int captureInterface) {
		this.captureInterface = captureInterface;
	}

	public boolean isPromiscuousWhenCapture() {
		return promiscuousWhenCapture;
	}

	public void setPromiscuousWhenCapture(boolean promiscuousWhenCapture) {
		this.promiscuousWhenCapture = promiscuousWhenCapture;
	}

	private String dumpFileName;
	private String inputPath;

	public InputStream getCaptureInputStream() throws Exception {
		return new FileInputStream(inputPath);
	}

	public String getDumpFileName() {
		return dumpFileName;
	}
	
	public TextItem[] getPromiscuousWhenCaptureList() {
		return new TextItem[] {new TextItem("false", 
				"Capture traffic to or from BSSIDs on this "+NmsUtil.getOEMCustomer().getAccessPonitName())};
	}

	/* CLI window begin ==================== */
	private String userName;
	private String password;
	private String port;
	private String host;
	private boolean enableRemoteSniffer;
	private boolean promiscuous;
	private String[] hostNameString;

	public String[] getHostNameString() {
		return hostNameString;
	}

	public void setHostNameString(String[] hostNameString) {
		this.hostNameString = hostNameString;
	}

	/**
	 * getter of userName
	 * 
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * setter of userName
	 * 
	 * @param userName
	 *            the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * getter of password
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * setter of password
	 * 
	 * @param password
	 *            the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * getter of port
	 * 
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * setter of port
	 * 
	 * @param port
	 *            the port to set
	 */
	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * getter of host
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * setter of host
	 * 
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * getter of enableRemoteSniffer
	 * 
	 * @return the enableRemoteSniffer
	 */
	public boolean isEnableRemoteSniffer() {
		return enableRemoteSniffer;
	}

	/**
	 * setter of enableRemoteSniffer
	 * 
	 * @param enableRemoteSniffer
	 *            the enableRemoteSniffer to set
	 */
	public void setEnableRemoteSniffer(boolean enableRemoteSniffer) {
		this.enableRemoteSniffer = enableRemoteSniffer;
	}

	/**
	 * getter of promiscuous
	 * 
	 * @return the promiscuous
	 */
	public boolean isPromiscuous() {
		return promiscuous;
	}

	/**
	 * setter of promiscuous
	 * 
	 * @param promiscuous
	 *            the promiscuous to set
	 */
	public void setPromiscuous(boolean promiscuous) {
		this.promiscuous = promiscuous;
	}

	private JSONObject setRemoteSniffer() throws Exception {
		JSONObject json = new JSONObject();

		/*
		 * get HiveAP object from database
		 */
		HiveAp ap = getSelectedHiveAp();

		if (ap == null) {
			json.put("r", false);
			json.put("m", "Failed to find the "+NmsUtil.getOEMCustomer().getAccessPonitName()+" object in system.");
			return json;
		}

		/*
		 * spell CLI for remote sniffer
		 */
		String cliGenerated = spellRemoteSnifferCLI();
		String[] cliSent;

		if (cliGenerated.startsWith("no")) {
			cliSent = new String[1];
			cliSent[0] = cliGenerated;
		} else {
			cliSent = new String[2];
			cliSent[0] = "no exec capture remote-sniffer\n";
			cliSent[1] = cliGenerated;
		}

		/*
		 * send event to AP
		 */
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(ap);
		cliEvent.setClis(cliSent);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		cliEvent.buildPacket();

		BeCommunicationEvent result = BeTopoModuleUtil.sendSyncCliRequest(ap,
				cliSent, BeCliEvent.CLITYPE_NORMAL, 60);

		boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);

		if (isSuccess) {
			json.put("r", true);
			json.put("m",
					"Updated remote sniffer settings to "+NmsUtil.getOEMCustomer().getAccessPonitName()+" successfully.");
		} else {
			json.put("r", false);
			String msg = BeTopoModuleUtil.parseCliRequestResult(result);
			json.put("m", msg);
		}

		return json;
	}
	
	private JSONObject setFirewallPolicyRule() throws Exception {
		JSONObject json = new JSONObject();

		/*
		 * get HiveAP object from database
		 */
		HiveAp ap = getSelectedHiveAp();

		if (ap == null) {
			json.put("r", false);
			json.put("m", "Failed to find the "+NmsUtil.getOEMCustomer().getAccessPonitName()+" object in system.");
			return json;
		}

		String cliGenerated = spellFirewallPolicyRuleCLI();
		String[] cliSent;

		cliSent = new String[1];
		cliSent[0] = cliGenerated;

		/*
		 * send event to AP
		 */
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(ap);
		cliEvent.setClis(cliSent);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
		cliEvent.buildPacket();

		BeCommunicationEvent result = BeTopoModuleUtil.sendSyncCliRequest(ap,
				cliSent, BeCliEvent.CLITYPE_NORMAL, 60);

		boolean isSuccess = BeTopoModuleUtil.isCliExeSuccess(result);

		if (isSuccess) {
			json.put("r", true);
			json.put("m",
					"Updated firewall policy rules settings to Device successfully.");
		} else {
			json.put("r", false);
			String msg = BeTopoModuleUtil.parseCliRequestResult(result);
			json.put("m", msg);
		}

		return json;
	}
	
	private String spellFirewallPolicyRuleCLI() {
		StringBuffer cli = new StringBuffer();
		if ("disable".equals(fwPolicyRuleOpertion)) {
			cli.append("exec bypass-wan-hardening\n");
			return cli.toString();
		} else if ("enable".equals(fwPolicyRuleOpertion)) {
			cli.append("no exec bypass-wan-hardening\n");
			return cli.toString();
		}
		return null;
	}

	private String spellRemoteSnifferCLI() {
		StringBuffer cli = new StringBuffer();

		if (!this.enableRemoteSniffer) { // Remote Sniffer is disabled
			cli.append("no exec capture remote-sniffer\n");
			return cli.toString();
		}

		cli.append("exec capture remote-sniffer ");

		if (this.userName != null && this.userName.length() > 0) {
			cli.append("user ").append(this.userName).append(" ").append(
					this.password).append(" ");
		}

		if (this.host != null && this.host.length() > 0) {
			cli.append("host-allowed ").append(this.host).append(" ");
		}

		if (this.port != null && this.port.length() > 0) {
			cli.append("local-port ").append(this.port).append(" ");
		}

		if (this.promiscuous) {
			cli.append("promiscuous");
		}

		return cli.append("\n").toString();
	}

	/* CLI window end ==================== */
	
	private String fwPolicyRuleOpertion = "disable";

	public String getFwPolicyRuleOpertion() {
		return fwPolicyRuleOpertion;
	}

	public void setFwPolicyRuleOpertion(String fwPolicyRuleOpertion) {
		this.fwPolicyRuleOpertion = fwPolicyRuleOpertion;
	}
	
	public TextItem[] getFwPolicyRuleDisableOption() {
		return new TextItem[] {
				new TextItem("disable", MgrUtil.getUserMessage("topology.menu.firewall.policy.disable"))
		};
	}
	
	public TextItem[] getFwPolicyRuleEnableOption() {
		return new TextItem[] {
				new TextItem("enable", MgrUtil.getUserMessage("topology.menu.firewall.policy.enable"))
		};
	}

    public boolean isEnablePerformance() {
        return enablePerformance;
    }

    public void setEnablePerformance(boolean enablePerformance) {
        this.enablePerformance = enablePerformance;
    }

}