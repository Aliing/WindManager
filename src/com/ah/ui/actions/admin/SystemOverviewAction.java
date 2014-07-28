package com.ah.ui.actions.admin;

/*
 * @author Chris Scheers
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpSession;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.performance.CurrentLoadCache;
import com.ah.bo.admin.HASettings;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhClientSession;
import com.ah.ui.actions.monitor.ActiveUserInfo;
import com.ah.ui.actions.monitor.AhTrapsAction;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.LinuxSystemInfoCollector;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class SystemOverviewAction extends AhTrapsAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			SystemOverviewAction.class.getSimpleName());

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}

		try {
			if ("systemOverview".equals(operation)) {
				log.info("execute", "systemOverview report.");
				return prepareSystemOverview();
			} else if ("systemOverviewData".equals(operation)) {
				log.info("execute", "systemOverview data.");
				prepareSystemOverviewData();
				return "systemOverviewData";
			} else if ("removeSession".equals(operation)) {
				removeActiveSession();
				prepareSystemOverviewData();
				return prepareSystemOverview();
			} else {
				log.info("execute", "Default report.");
				return prepareSystemOverview();
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();

		if (getUserContext() == null) {
			// Need to redirect to login.
			return;
		}

		MgrUtil.removeSessionAttribute("lstTitle");
		MgrUtil.removeSessionAttribute("lstTabId");
		MgrUtil.removeSessionAttribute("lstForward");
	}

	protected String prepareSystemOverview() {
		setSelectedL2Feature(L2_FEATURE_SYSTEM_OVERVIEW);
		if (isHMOnline()) {
			swf = "systemOverviewOL";
			application = "systemOverviewOL";
		} else {
			swf = "systemOverview";
			application = "systemOverview";
		}

		width = "100%";
		height = "550";

		bgcolor = "ffffff";
		return "systemOverview";
	}

	private String swf, width, height, application, bgcolor;

	public String getApplication() {
		return application;
	}

	public String getBgcolor() {
		return bgcolor;
	}

	public String getHeight() {
		return height;
	}

	public String getSwf() {
		return swf;
	}

	public String getWidth() {
		return width;
	}

	protected void prepareSystemOverviewData() {
		log.info("prepareSystemOverviewData", "begin get data.");
		setValueHmHostname();
		setValueHmVersionAndBuildTime();
		setValueSystemUpTime();
		setValueHmModel();
		log.info("prepareSystemOverviewData", " 1 end.");
		setValueNumberOfLogin();
		setValueLoginUsers();
		setValueCpuUse();
		setValueMemoryUse();
		log.info("prepareSystemOverviewData", " 2 end.");
		setInterfacesValue();
		log.info("prepareSystemOverviewData", " 3 end.");
		initHAStatus();
		log.info("prepareSystemOverviewData", " 4 end.");
		if (isHMOnline()) {
			setSystemPerformaceInfo();
		}
		log.info("prepareSystemOverviewData", " finished.");
	}

	private String haStatus;

	private String replicateStatus;

	// row height of replicate status
	private String showReplicateStatus = "0";

	private void initHAStatus() {
		try {
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class,
					null, null);
			if (list.isEmpty()) {
				haStatus = "unknown.";
				return;
			}

			HASettings haSettings = list.get(0);
			if (haSettings.getHaStatus() != HASettings.HASTATUS_ENABLE) {
				haStatus = "Stand Alone.";
				return;
			} else {
				haStatus = "HA is running.";
			}

			// int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
			// + "check_heartbeat_running.sh");
			// if (exitValue != 0) {
			// haStatus = "HA is abnormal running.";
			// return;
			// }
			//
			// haStatus = "HA is running.";
			//
			// String masterIP = HmBeOsUtil.getIP_eth0();
			// String slaveIP = masterIP.equals(haSettings.getPrimaryMGTIP()) ?
			// haSettings
			// .getSecondaryMGTIP() : haSettings.getPrimaryMGTIP();
			//
			// exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() +
			// "check_master_online.sh");
			// haStatus += "  Active node (" + masterIP + ") "
			// + (exitValue == 0 ? "online" : "offline");
			// exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() +
			// "check_slave_online.sh");
			// haStatus += " | Passive node (" + slaveIP + ") "
			// + (exitValue == 0 ? "online." : "offline.");
			// if (exitValue != 0) {
			// return;
			// }

			showReplicateStatus = "25";
			// replicateStatus = getSlaveReplicateStatus();
		} catch (Exception e) {
			haStatus = "unknown.";
			showReplicateStatus = "0";
		}
	}

	/**
	 * query slave node sync status
	 * 
	 * @return -
	 */
	// private String getSlaveReplicateStatus() {
	// try {
	// // List<String> results =
	// execCommandOutResult("cat /HiveManager/ha/opt/ha_node_num");
	// // if (results.size() == 0) {
	// // return "Error getting node number of passive node.";
	// // }
	// //
	// // String nodeNum = results.get(0);
	// // if (nodeNum.equals("2")) {
	// // nodeNum = "1";
	// // } else if (nodeNum.equals("1")) {
	// // nodeNum = "2";
	// // }
	// // String slaveVPNIP = "172.16.0." + nodeNum;
	// //
	// // results =
	// execCommandOutResult("/HiveManager/ha/ha-d/ha-d-slave -c 13 -t " +
	// slaveVPNIP);
	// List<String> results =
	// execCommandOutResult("/HiveManager/ha/scripts/get_replication_status.sh");
	// String replicateStatus = "";
	// for (String out : results) {
	// if (out.toLowerCase().contains("replication status")) {
	// replicateStatus = out.substring(out.indexOf("=") + 1);
	// break;
	// }
	// }
	//
	// if (!replicateStatus.trim().equalsIgnoreCase("running")) {
	// return "Replication not running.";
	// }
	//
	// results =
	// execCommandOutResult("/HiveManager/ha/scripts/get_replication_event.sh ");
	// String num_replicated = "";
	// String num_left = "";
	// for (String out : results) {
	// if (out.toLowerCase().contains("sync status")) {
	// if (!out.contains("#")) {
	// return "Replication running but " + out.substring(out.indexOf("=") + 1);
	// }
	//
	// num_replicated = out.substring(out.indexOf("#") + 1);
	// }
	//
	// if (out.toLowerCase().contains("replication event")) {
	// num_left = out.substring(out.indexOf("=") + 1);
	// }
	// }
	//
	// return "Replicating data: " + num_replicated
	// + ((Integer.valueOf(num_replicated) > 1) ? " events" : " event")
	// + " replicated; " + num_left
	// + ((Integer.valueOf(num_left) > 1) ? " events" : " event") +
	// " remaining.";
	// } catch (Exception e) {
	// log.error("getSlaveReplicateStatus", "catch exception", e);
	// return "Error getting sync status.";
	// }
	// }

	// private int execCommand(String cmd) {
	// try {
	// String string_Path_Array[] = new String[3];
	// string_Path_Array[0] = "bash";
	// string_Path_Array[1] = "-c";
	// string_Path_Array[2] = cmd;
	//
	// Process p = Runtime.getRuntime().exec(string_Path_Array);
	//
	// p.waitFor();
	//
	// return p.exitValue();
	// } catch (Exception e) {
	// log.error("execCommand", "catch exception", e);
	// return 255;
	// }
	// }
	//
	// private List<String> execCommandOutResult(String cmd) {
	// List<String> resultList = new ArrayList<String>();
	//
	// try {
	// String cmds[] = new String[3];
	// cmds[0] = "bash";
	// cmds[1] = "-c";
	// cmds[2] = cmd;
	//
	// Process proc = Runtime.getRuntime().exec(cmds);
	// InputStream inputStream = proc.getInputStream();
	// BufferedReader br = new BufferedReader(new
	// InputStreamReader(inputStream), 2048);
	//
	// String line;
	// while ((line = br.readLine()) != null) {
	// resultList.add(line);
	// }
	//
	// inputStream.close();
	// br.close();
	//
	// return resultList;
	// } catch (Exception ex) {
	// log.error("execCommandOutResult", "catch exception", ex);
	//
	// return resultList;
	// }
	// }

	public void setValueHmHostname() {
		try {
			hmHostname = HmBeOsUtil.getHostName();
		} catch (Exception e) {
			hmHostname = "";
		}
	}

	public void setValueHmModel() {
		try {
			if (hmModel == null || hmModel.equals("")) {
				hmModel = HmBeAdminUtil.getHmKernelModel();
			}
		} catch (Exception e) {
			log.error("setValueHmModel", "catch exception", e);
			hmModel = "";
		}
	}

	public void setSystemPerformaceInfo() {
		numPackage = CurrentLoadCache.getInstance().getResultOfCAPWAP();
		numEvent = CurrentLoadCache.getInstance().getResultOfEvent();
		numAlarm = CurrentLoadCache.getInstance().getResultOfAlarm();
		// numActiveClient = QueryUtil.findRowCount(AhClientSession.class,
		// new FilterParams("connectstate",AhClientSession.CONNECT_STATE_UP));
		numActiveClient = DBOperationUtil.findRowCount(AhClientSession.class,
				new FilterParams("connectstate",
						AhClientSession.CONNECT_STATE_UP));
		numBackup = CurrentLoadCache.getInstance()
				.getMaxNumberOfBackupRunning();
		numRestore = CurrentLoadCache.getInstance()
				.getMaxNumberOfRestoreRunning();
		numDelat = CurrentLoadCache.getInstance().getMaxNumberOfConfigRunning();// Running
																				// thread
																				// number.
		numAuditRequest = CurrentLoadCache.getInstance()
				.getMaxNumberOfConfigRequest();
		numUpgrade = CurrentLoadCache.getInstance()
				.getMaxNumberOfUpgradeRunning();
	}

	public void setInterfacesValue() {
		try {
			List<String> mgtInfos = HmBeAdminUtil.getEthInfo("eth0");

			mgtState = mgtInfos.get(1).replace("Mb/s", "Mbps") + "   "
					+ mgtInfos.get(2);

			if (!isHMOnline()) {
				List<String> lanInfos = HmBeAdminUtil.getEthInfo("eth1");
				if (lanInfos.get(0).equalsIgnoreCase("off")) {
					lanState = "off";
				} else {
					lanState = lanInfos.get(1).replace("Mb/s", "Mbps") + "   "
							+ lanInfos.get(2);
				}
			} else {
				lanState = "off";
			}

			if (mgtState.trim().length() == 0) {
				mgtState = "unknown";
			}

			if (lanState.trim().length() == 0) {
				lanState = "unknown";
			}
		} catch (Exception e) {
			log.error("setInterfacesValue", "catch exception", e);
			mgtState = "unknown";
			lanState = "unknown";
		}
	}

	public void setValueHmVersionAndBuildTime() {
		try {
			if (hmVersion == null || hmVersion.equals("")) {
				BeVersionInfo versionInfo = getSessionVersionInfo();
				hmVersion = versionInfo.getMainVersion() + "r"
						+ versionInfo.getSubVersion();
				buildTime = versionInfo.getBuildTime();
			}
			if (buildTime == null || buildTime.equals("")) {
				BeVersionInfo versionInfo = getSessionVersionInfo();
				buildTime = versionInfo.getBuildTime();
			}
		} catch (Exception e) {
			hmVersion = "";
			buildTime = "";
		}
	}

	public void setValueSystemUpTime() {
		try {
			if (sysUp == 0) {
				long longUptime = HmBeOsUtil.getStartupTime();
				sysUp = System.currentTimeMillis() - longUptime * 1000;
				systemUpTime = NmsUtil.transformTime((int) sysUp).replace(
						" 0 Secs", "");
			} else {
				systemUpTime = NmsUtil.transformTime(
						(int) ((System.currentTimeMillis() - sysUp) / 1000))
						.replace(" 0 Secs", "");
			}
		} catch (Exception e) {
			systemUpTime = "";
		}
	}

	public void setValueNumberOfLogin() {
		try {
			numberOfLogin = String.valueOf(CurrentUserCache.getInstance()
					.getActiveSessions().size());
		} catch (Exception e) {
			numberOfLogin = "";
		}
	}

	public void setValueLoginUsers() {
		loginUsers = new ArrayList<ActiveUserInfo>();
		try {
			for (HttpSession activeUser : CurrentUserCache.getInstance()
					.getActiveSessions()) {
				ActiveUserInfo tmpUser = new ActiveUserInfo();
				HmUser sessionUser = (HmUser) activeUser
						.getAttribute(USER_CONTEXT);
				if (sessionUser != null) {
					tmpUser.setUserIpAddress(sessionUser.getUserIpAddress());
					tmpUser.setUserName(sessionUser.getUserName());
				}
				tmpUser.setUserSessionTotalTime(NmsUtil.transformTime(
						(int) (System.currentTimeMillis() - activeUser
								.getCreationTime()) / 1000).replace(" 0 Secs",
						""));
				tmpUser.setSessionId(activeUser.getId());
				loginUsers.add(tmpUser);
			}
		} catch (Exception e) {
			log.error("setValueLoginUsers", "", e);
		}
	}

	public void setValueCpuUse() {
		if (cpuUse.size() < 20) {
			for (int i = 0; i < 20; i++) {
				cpuUse.add("0");
			}
		}
		for (int i = 0; i < 20; i++) {
			if (i != 19) {
				cpuUse.set(i, cpuUse.get(i + 1));
			} else {
				String count = "0";
				try {
					count = String.valueOf((int) (LinuxSystemInfoCollector
							.getInstance().getCpuInfo() * 100));
				} catch (Exception e) {
					// e.printStackTrace();
					// } catch (InterruptedException e) {
					// e.printStackTrace();
				}
				cpuUse.set(i, count);
			}
		}
	}

	public void removeActiveSession() {
		try {
			CurrentUserCache.getInstance().invalidateSession(removeSessionId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setValueMemoryUse() {
		if (memoryUse.size() < 20) {
			for (int i = 0; i < 20; i++) {
				memoryUse.add("0");
			}
		}
		for (int i = 0; i < 20; i++) {
			if (i != 19) {
				memoryUse.set(i, memoryUse.get(i + 1));
			} else {
				String percent = "0";
				try {
					long count[] = LinuxSystemInfoCollector.getInstance()
							.getMemInfo();
					totalMemo = String.valueOf(count[0]);
					freeMemo = String.valueOf(count[1] + count[2] + count[3]);
					usageMemo = String.valueOf(count[0] - count[1] - count[2]
							- count[3]);
					if (count[0] != 0) {
						percent = String.valueOf((count[0] - count[1]
								- count[2] - count[3])
								* 100 / count[0]);
					}
				} catch (Exception e) {
					// e.printStackTrace();
					// } catch (InterruptedException e) {
					// e.printStackTrace();
				}
				memoryUse.set(i, percent);
			}
		}
	}

	private static String hmVersion, buildTime, hmModel;
	private static long sysUp;
	private String hmHostname, systemUpTime, numberOfLogin, lanState, mgtState;
	// private String hmHostname, hmVersion, buildTime, systemUpTime,
	// numberOfLogin, hmModel,
	// lanState, mgtState;
	private String totalMemo = "0";
	private String freeMemo = "0";
	private String usageMemo = "0";
	private String removeSessionId = "";

	private long numPackage, numEvent, numAlarm, numActiveClient, numDelat,
			numAuditRequest, numBackup, numRestore, numUpgrade;
	private List<ActiveUserInfo> loginUsers;

	public static final Vector<String> cpuUse = new Vector<String>();
	public static final Vector<String> memoryUse = new Vector<String>();

	public String getHmVersion() {
		return hmVersion;
	}

	public String getBuildTime() {
		return buildTime;
	}

	public String getSystemUpTime() {
		return systemUpTime;
	}

	public String getNumberOfLogin() {
		return numberOfLogin;
	}

	public static Vector<String> getMemoryUse() {
		return memoryUse;
	}

	public static Vector<String> getCpuUse() {
		return cpuUse;
	}

	public String getTotalMemo() {
		return totalMemo;
	}

	public String getFreeMemo() {
		return freeMemo;
	}

	public String getUsageMemo() {
		return usageMemo;
	}

	public List<ActiveUserInfo> getLoginUsers() {
		return loginUsers;
	}

	public String getHmHostname() {
		return hmHostname;
	}

	public String getRemoveSessionId() {
		return removeSessionId;
	}

	public void setRemoveSessionId(String removeSessionId) {
		this.removeSessionId = removeSessionId;
	}

	public String getHmModel() {
		return hmModel;
	}

	public String getLanState() {
		return lanState;
	}

	public void setLanState(String lanState) {
		this.lanState = lanState;
	}

	public String getMgtState() {
		return mgtState;
	}

	public void setMgtState(String mgtState) {
		this.mgtState = mgtState;
	}

	public String getHaStatus() {
		return haStatus;
	}

	public void setHaStatus(String haStatus) {
		this.haStatus = haStatus;
	}

	public String getShowReplicateStatus() {
		return showReplicateStatus;
	}

	public void setShowReplicateStatus(String showHANodeStatus) {
		this.showReplicateStatus = showHANodeStatus;
	}

	public String getReplicateStatus() {
		return replicateStatus;
	}

	public void setReplicateStatus(String replicateStatus) {
		this.replicateStatus = replicateStatus;
	}

	public long getNumPackage() {
		return numPackage;
	}

	public long getNumEvent() {
		return numEvent;
	}

	public long getNumAlarm() {
		return numAlarm;
	}

	public long getNumActiveClient() {
		return numActiveClient;
	}

	public long getNumDelat() {
		return numDelat;
	}

	public long getNumAuditRequest() {
		return numAuditRequest;
	}

	public long getNumRestore() {
		return numRestore;
	}

	public long getNumBackup() {
		return numBackup;
	}

	public long getNumUpgrade() {
		return numUpgrade;
	}

	public void setNumUpgrade(long numUpgrade) {
		this.numUpgrade = numUpgrade;
	}

	/**
	 * @return the sysUp
	 */
	public static long getSysUp() {
		return sysUp;
	}

}