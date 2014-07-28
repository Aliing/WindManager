/**
 *@filename		HaMonitoringAction.java
 *@version
 *@author		Fiona
 *@createtime	Feb 18, 2012 3:58:46 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.actions.home;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.app.HmBeOsUtil;
import com.ah.bo.admin.HASettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.datetime.AhDateTimeUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HaMonitoringAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private static final Tracer	log = new Tracer(HaMonitoringAction.class.getSimpleName());
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, new FilterParams("haStatus", HASettings.HASTATUS_ENABLE));
			if (!list.isEmpty()) {
				HASettings haSettings = list.get(0);
				int exitValue = execCommand(HmBeOsUtil.getHAScriptsPath()
						+ "check_heartbeat_running.sh");
				inHaStatus = true;
				if (exitValue != 0) {
					runMessage = MgrUtil.getUserMessage("home.ha.monitoring.ha.hm.abnormal.running");
				} else {
					runMessage = MgrUtil.getUserMessage("home.ha.monitoring.ha.hm.normal.running");

					boolean mgtPort = HASettings.HAPORT_MGT == haSettings.getHaPort();
					String masterIP = mgtPort ? HmBeOsUtil.getIP_eth0() : HmBeOsUtil.getIP_eth1();
					String slaveIP = masterIP;
					boolean usePrimary = true;
							
					if (mgtPort) {
						usePrimary = masterIP.equals(haSettings.getPrimaryMGTIP());
						slaveIP = usePrimary ? haSettings.getSecondaryMGTIP() : haSettings.getPrimaryMGTIP();
					} else {
						usePrimary = masterIP.equals(haSettings.getPrimaryLANIP());
						slaveIP = usePrimary ? haSettings.getSecondaryLANIP() : haSettings.getPrimaryLANIP();
					}

					exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "check_master_online.sh");
					String actMsg = "Active (" + (exitValue == 0 ? "Online)" : "Offline)");
					node1Ip = masterIP;
					exitValue = execCommand(HmBeOsUtil.getHAScriptsPath() + "check_slave_online.sh");
					String pasMsg = "Passive (" + (exitValue == 0 ? "Online)" : "Offline)");
					node2Ip = slaveIP;
					node1Status = HAUtil.isSlave() ? pasMsg : actMsg;
					node2Status = HAUtil.isSlave() ? actMsg : pasMsg;
					
					if (usePrimary) {
						if (haSettings.getPrimaryUpTime() > 0) {
							node1Time = changeTimeFromLongToStr(System.currentTimeMillis() - haSettings.getPrimaryUpTime());
						} else {
							node1Time = "N/A";
						}
						
						if (haSettings.getSecondaryUpTime() > 0) {
							node2Time = changeTimeFromLongToStr(System.currentTimeMillis() - haSettings.getSecondaryUpTime());
						} else {
							node2Time = "N/A";
						}
					} else {
						if (haSettings.getPrimaryUpTime() > 0) {
							node2Time = changeTimeFromLongToStr(System.currentTimeMillis() - haSettings.getPrimaryUpTime());
						} else {
							node2Time = "N/A";
						}
						
						if (haSettings.getSecondaryUpTime() > 0) {
							node1Time = changeTimeFromLongToStr(System.currentTimeMillis() - haSettings.getSecondaryUpTime());
						} else {
							node1Time = "N/A";
						}
					}
					if (haSettings.getLastSwitchOverTime() > 0) {
						lastSwitchover = "Last Switchover Time: " + AhDateTimeUtil.getDateStrFromLong(haSettings.getLastSwitchOverTime(),
							AhDateTimeUtil.REPORT_FORMATTER);
					} else {
						lastSwitchover = "Last Switchover Time: N/A";
					}
					// db status and lag time
					List<String> results = execCommandOutResult("cd /HiveManager/PGPool/script; ./getLagTime.sh 2>&1");
					// example
					// master db is 10.155.34.80, slave db is 10.155.34.81
					// LAG_INFO: 0 bytes, 0 milliseconds
					String msg1 = "active unknown, ";
					String msg2 = "passive unknown";
					String msg3 = "0 bytes, 0 milliseconds";
					String repRunMsg = "Replication is running";
					if (results.size() == 2) {
						for (String result : results) {
							String[] resultList = result.split(" ");
							// lag info, lag time
							if (result.startsWith("LAG_INFO")) {
								msg3 = result.substring(result.indexOf(" ")+1);
							// node info
							} else if (resultList.length == 8) {
								msg1 = resultList[3].substring(0, resultList[3].length()-1)+" is active, ";
								msg2 = resultList[7]+" is passive";
							}
						}
						dbRunMessage = "DB Status: " + msg1 + msg2 + "<br>" + repRunMsg + "<br>DB Replication Lagging: " + msg3;
					// db status error
					} else {
						results = execCommandOutResult("cd /HiveManager/PGPool/script; ./getNodeStatus.sh 2>&1");
						// example
						// 0 10.16.134.91 5432 2 0.500000 master
						// 1 10.16.134.92 5432 3 0.500000 slave
						msg1 = "";
						msg2 = "";
						msg3 = "";
						if (results.size() == 2) {
							for (String result : results) {
								String[] resultList = result.split(" ");
								if (resultList.length > 2) {
									// active node
									if (result.endsWith("master")) {
										msg1 = resultList[1]+" is active, ";
									// passive node
									} else if (result.endsWith("slave")) {
										msg2 = resultList[1]+" is passive ";
									
									// unknown node
									} else if (result.endsWith("unknown")) {
										msg3 += resultList[1]+" is unknown ";
										repRunMsg = "Replication is not running";
									}
								}
							}
							dbRunMessage = "DB Status: " + (msg1 + msg2 + msg3).trim() + "<br>" + repRunMsg;
						} else {
							dbRunMessage += "<br>Replication is not running";
						}
					}
				}
			}
			return SUCCESS;
		} catch (Exception e) {
			addActionError(e.getMessage());
			log.error("execute", "catch exception", e);
			return ERROR;
		}
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HA_MONITORING);
	}
	
	private boolean inHaStatus = false;
	
	private String runMessage = MgrUtil.getUserMessage("home.ha.monitoring.standalone.hm.running");
	
	private String node1Ip;
	
	private String node1Status;
	
	private String node1Time;
	
	private String node2Ip;
	
	private String node2Status;
	
	private String node2Time;
	
	private String lastFailover = "Last Failover Time: 2012-02-12 12:34:56";
	
	private String lastSwitchover = "Last Switchover Time: 2012-01-12 12:34:56";
	
	private String dbRunMessage = "DB Status: Unknown";

	public String getDbRunMessage() {
		return dbRunMessage;
	}

	public void setDbRunMessage(String dbRunMessage) {
		this.dbRunMessage = dbRunMessage;
	}

	public String getNode1Ip() {
		return node1Ip;
	}

	public void setNode1Ip(String node1Ip) {
		this.node1Ip = node1Ip;
	}

	public String getNode1Status() {
		return node1Status;
	}

	public void setNode1Status(String node1Status) {
		this.node1Status = node1Status;
	}

	public String getNode1Time() {
		return node1Time;
	}

	public void setNode1Time(String node1Time) {
		this.node1Time = node1Time;
	}

	public String getNode2Ip() {
		return node2Ip;
	}

	public void setNode2Ip(String node2Ip) {
		this.node2Ip = node2Ip;
	}

	public String getNode2Status() {
		return node2Status;
	}

	public void setNode2Status(String node2Status) {
		this.node2Status = node2Status;
	}

	public String getNode2Time() {
		return node2Time;
	}

	public void setNode2Time(String node2Time) {
		this.node2Time = node2Time;
	}
	
	private List<String> execCommandOutResult(String cmd) {
		List<String> resultList = new ArrayList<String>();

		try {
			String cmds[] = new String[3];
			cmds[0] = "bash";
			cmds[1] = "-c";
			cmds[2] = cmd;

			Process proc = Runtime.getRuntime().exec(cmds);
			InputStream inputStream = proc.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream), 4096);

			String line;
			while ((line = br.readLine()) != null) {
				resultList.add(line);
			}

			inputStream.close();
			br.close();

			return resultList;
		} catch (Exception ex) {
			log.error("execCommandOutResult", "catch exception", ex);

			return resultList;
		}
	}
	
	private int execCommand(String cmd) {
		try {
			String string_Path_Array[] = new String[3];
			string_Path_Array[0] = "bash";
			string_Path_Array[1] = "-c";
			string_Path_Array[2] = cmd;

			Process p = Runtime.getRuntime().exec(string_Path_Array);

			p.waitFor();

			return p.exitValue();
		} catch (Exception e) {
			log.error("execCommand", "catch exception", e);
			return 255;
		}
	}

	public boolean isInHaStatus() {
		return inHaStatus;
	}

	public void setInHaStatus(boolean inHaStatus) {
		this.inHaStatus = inHaStatus;
	}

	public String getRunMessage() {
		return runMessage;
	}

	public void setRunMessage(String runMessage) {
		this.runMessage = runMessage;
	}

	public String getLastFailover() {
		return lastFailover;
	}

	public void setLastFailover(String lastFailover) {
		this.lastFailover = lastFailover;
	}

	public String getLastSwitchover() {
		return lastSwitchover;
	}

	public void setLastSwitchover(String lastSwitchover) {
		this.lastSwitchover = lastSwitchover;
	}
	
	private String changeTimeFromLongToStr(long timeLong) {
		if (timeLong > 0) {
			long day = timeLong/(1000*60*60*24l);
			long leftSen1 = timeLong%(1000*60*60*24l);
			long hour = leftSen1/(1000*60*60l);
			long leftSen2 = leftSen1%(1000*60*60l);
			long minute = leftSen2/(1000*60l);
			StringBuffer strBuf = new StringBuffer();
			if (day > 0) {
				strBuf.append(day+(day > 1 ? " days " : " day "));
			}
			if (hour > 0) {
				strBuf.append(hour+(hour > 1 ? " hours " : " hour "));
			}
			if (minute > 0) {
				strBuf.append(minute+(minute > 1 ? " minutes " : " minute "));
			}
			if (strBuf.length() > 0) {
				return strBuf.toString().trim();
			}
			return "N/A";
		} else {
			return "N/A";
		}
	}
}
