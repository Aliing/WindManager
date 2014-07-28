/**
 *@filename		HASendEmailAlertTimer.java
 *@version
 *@author		Fiona
 *@createtime	Feb 21, 2012 11:17:36 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.ha.alert;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.event.portal.BeHAStatusInfoEvent;
import com.ah.bo.admin.CapwapClient;
import com.ah.bo.admin.HASettings;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.util.Tracer;

/**
 * @author		Fiona
 * @version		V1.0.0.0 
 */
public class HAAlertor {

	private static final Tracer log = new Tracer(HAAlertor.class.getSimpleName());

	private ScheduledExecutorService sendInfoTimer;
	
	private ScheduledFuture<?> sendInfoFuture;
	
	private int SEND_EMAIL_COUNT;
	
	private static HAAlertor instance;

	public synchronized static HAAlertor getInstance() {
		if (instance == null) {
			instance = new HAAlertor();
		}

		return instance;
	}

	public synchronized void start() {
		log.info("start", "Starting HA alertor...");

		try {
			SEND_EMAIL_COUNT = 0;
			SendInfoTask taskEvalue = new SendInfoTask();
			if (null == sendInfoTimer || sendInfoTimer.isShutdown()) {
				sendInfoTimer = Executors.newSingleThreadScheduledExecutor();
				sendInfoFuture = sendInfoTimer.scheduleWithFixedDelay(taskEvalue, 60, 60, TimeUnit.SECONDS);
				log.info("start", "HA alertor started.");
			} else {
				log.info("start", "HA alertor has already been started.");
			}
		} catch (Exception e) {
			log.error("start", "Start HA alertor error.", e);
		}
	}

	public synchronized void stop() {
		log.info("stop", "Stopping HA alertor...");

		try {
			if (null != sendInfoTimer && !sendInfoTimer.isShutdown()) {
				if (null != sendInfoFuture) {
					sendInfoFuture.cancel(false);
				}

				// Disable new tasks from being submitted.
				sendInfoTimer.shutdown();
				try {
					// Wait a while for existing tasks to terminate.
					if (!sendInfoTimer.awaitTermination(5, TimeUnit.SECONDS)) {
						// Cancel currently executing tasks.
						sendInfoTimer.shutdownNow();

						// Wait a while for tasks to respond to being canceled.
						if (!sendInfoTimer.awaitTermination(5, TimeUnit.SECONDS)) {
							log.warn("stop", "The send HA alert email timer does not terminate.");
						}
					}
				} catch (InterruptedException ie) {
					// (Re-)Cancel if current thread also interrupted.
					//arg_Timer.shutdownNow();
				}

				log.info("stop", "HA alertor stopped.");
			} else {
				log.info("stop", "HA alertor has already been stopped.");
			}
		} catch (Exception e) {
			log.error("stop", "Stop HA alertor error.", e);
		}
	}
	
	/**
	 * Do the send alert email to HA notify.
	 * @author		Fiona
	 * @version		V1.0.0.0
	 */
	private class SendInfoTask implements Runnable {
		@Override
		public void run() {
			try {
				List<HASettings> list = QueryUtil.executeQuery(HASettings.class, null, new FilterParams("haStatus", HASettings.HASTATUS_ENABLE), 1);
				if (!list.isEmpty()) {
					HASettings haSet = list.get(0);
					String sysIp = HmBeOsUtil.getIP_eth0();
					StringBuilder strBuf = new StringBuilder();
					boolean isSlave = HAUtil.isSlave();
					// system is passive node
					String nodeStr = isSlave ? "Active node " : "Passive node ";
					// system ip is the primary
					boolean useExt = haSet.isUseExternalIPHostname();
					if (sysIp.equals(haSet.getPrimaryMGTIP())) {
						String pingIp = useExt ? haSet.getSecondaryExternalIPHostname() : haSet.getSecondaryMGTIP();
						if (!HmBeOsUtil.pingSystemToCheckActive(pingIp)) {
							strBuf.append(nodeStr).append(pingIp).append(" is dead or lost connection.");
						}
					} else {
						String pingIp = useExt ? haSet.getPrimaryExternalIPHostname() : haSet.getPrimaryMGTIP();
						if (!HmBeOsUtil.pingSystemToCheckActive(pingIp)) {
							strBuf.append(nodeStr).append(pingIp).append(" is dead or lost connection.");
						}
					}
					
					// system is HMOL, need add more information
					if (NmsUtil.isHostedHMApplication()) {
						String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot + "/ahCheckHMStatus.sh";
						List<String> statusList = BeAdminCentOSTools.getOutStreamsExecCmd(strCmd);
						if (statusList.size() != 3) {
							log.error("run", "'" + strCmd + "' executed return error.");
						} else {
							int capwapStatus = BeHAStatusInfoEvent.FAILED;
							int tomcatStatus = BeHAStatusInfoEvent.FAILED;
							int dbConnectionStatus = BeHAStatusInfoEvent.FAILED;
							boolean hasException = false;
							try {
								capwapStatus = Integer.parseInt(statusList.get(0));
								tomcatStatus = Integer.parseInt(statusList.get(1));
								dbConnectionStatus = Integer.parseInt(statusList.get(2));
							} catch (Exception e) {
								log.error("run", "'" + strCmd + "' executed return non-numeric value.", e);
								hasException = true;
							}

							if (!hasException) {
								try {
									// send HMOL status report to myhive
									BeHAStatusInfoEvent status = new BeHAStatusInfoEvent();
									status.setApMac(HmBeCommunicationUtil.getPortalMac());
									status.setHaInfoType(BeHAStatusInfoEvent.TYPE_HMOLSTATUS_REPORT);
									status.setSuccess(!hasException);
									status.setCapwapStatus(capwapStatus);
									status.setTomcatStatus(tomcatStatus);
									status.setDbConnectionStatus(dbConnectionStatus);
									status.buildPacket();
									HmBeCommunicationUtil.sendResponse(status);
								} catch (Exception e) {
									log.error("run", "Send HMOL status info error.", e);
								}
								// active node
								if (!isSlave) {
									// tomcat status
									if (BeHAStatusInfoEvent.FAILED == tomcatStatus) {
										strBuf.append("<br>There is something wrong with the Tomcat.");
									}
									// capwap status
									if (BeHAStatusInfoEvent.FAILED == capwapStatus) {
										strBuf.append("<br>There is something wrong with the CAPWAP.");
									}
									// db status
									if (BeHAStatusInfoEvent.FAILED == dbConnectionStatus) {
										strBuf.append("<br>DB is lost connection.");
									}
								}
							}
						}
						// myhive status
						CapwapClient setting = QueryUtil.findBoByAttribute(CapwapClient.class, "serverType",
								CapwapClient.SERVERTYPE_PORTAL);
						if (null != setting) {
							if (!HmBeOsUtil.pingSystemToCheckActive(setting.getPrimaryCapwapIP())) {
								strBuf.append("<br>MyHive is dead or lost connection.");
							}
						}
					}
					if (strBuf.length() > 0) {
						SEND_EMAIL_COUNT++;
					} else {
						SEND_EMAIL_COUNT=0;
					}
					// every 3 days
					if (strBuf.length() > 0 && SEND_EMAIL_COUNT == 1) {
						NmsUtil.sendMailToAdminUser("HA node status alert", "This node is "+sysIp+".<br>"+strBuf.toString());
					}
					if (SEND_EMAIL_COUNT == 865) {
						SEND_EMAIL_COUNT = 0;
					}
				}
			} catch (Exception e) {
				log.error("run", "Send HA alert email error.", e);
			}
		}		
	}

}