package com.ah.be.performance;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhPortAvailability;
import com.ah.bo.performance.AhReport;
import com.ah.bo.performance.AhStatsAvailabilityHigh;
import com.ah.bo.performance.AhStatsAvailabilityLow;
import com.ah.bo.performance.AhStatsLatencyHigh;
import com.ah.bo.performance.AhStatsLatencyLow;
import com.ah.bo.performance.AhStatsThroughputHigh;
import com.ah.bo.performance.AhStatsThroughputLow;
import com.ah.bo.performance.AhStatsVpnStatusHigh;
import com.ah.bo.performance.AhStatsVpnStatusLow;

public class BeInterfaceReportProcessor {
	class TimeFilter implements Predicate {
		private List<String> keys = new ArrayList<String>();

		@Override
		public boolean evaluate(Object arg0) {
			String key = "";
			if (arg0 instanceof AhStatsThroughputHigh) {
				key = ((AhStatsThroughputHigh) arg0).getKey();
			} else if (arg0 instanceof AhStatsLatencyHigh) {
				key = ((AhStatsLatencyHigh) arg0).getKey();
			} else if (arg0 instanceof AhStatsAvailabilityHigh) {
				key = ((AhStatsAvailabilityHigh) arg0).getKey();
			} else if (arg0 instanceof AhStatsVpnStatusHigh) {
				key = ((AhStatsVpnStatusHigh) arg0).getKey();
			} else if (arg0 instanceof AhStatsThroughputLow) {
				key = ((AhStatsThroughputLow) arg0).getKey();
			} else if (arg0 instanceof AhStatsLatencyLow) {
				key = ((AhStatsLatencyLow) arg0).getKey();
			} else if (arg0 instanceof AhStatsAvailabilityLow) {
				key = ((AhStatsAvailabilityLow) arg0).getKey();
			} else if (arg0 instanceof AhStatsVpnStatusLow) {
				key = ((AhStatsVpnStatusLow) arg0).getKey();
			} else {
				return true;
			}

			if (keys.contains(key)) {
				return false;
			} else {
				keys.add(key);
				return true;
			}
		}
	}

	private static final long ONE_MINUTE = 60 * 1000;
	private static final long TEN_MINUTES = 10 * 60 * 1000;

	public static final long QUERY_OK 			= 0;
	public static final long QUERY_DOING 		= 1;
	public static final long QUERY_DONE 		= 2;
	public static final long ERROR_INIT 		= -1;
	public static final long ERROR_NODEVICE   	= -2;
	public static final long ERROR_NOTMANAGED 	= -3;
	public static final long ERROR_CLI 			= -4;
	public static final long ERROR_FILE 		= -5;
	public static final long ERROR_DB 			= -6;
	public static final long ERROR_DISCONNECTED = -7;

	// use mac as key and value is [previous query time, query type, query status]
	private final Map<String, long[]> queryHighStatus = new ConcurrentHashMap<String, long[]>();
	private final Map<String, long[]> queryLowStatus = new ConcurrentHashMap<String, long[]>();

	public BeInterfaceReportProcessor() {
	}

	public void startTask() {
		queryHighStatus.clear();
		queryLowStatus.clear();
	}

	public boolean shutdown() {
		queryHighStatus.clear();
		queryLowStatus.clear();
		return true;
	}

	public long fetchReportData(String mac, int period) {
		mac = mac.toUpperCase();
		long result = queryVpnReportData(mac, period);

		// update status
		if (result == QUERY_OK || result < 0) {
			updateStatus(mac, period, result == QUERY_OK ? QUERY_DONE : result);
		}
		if (result < 0) clearLatestData(mac, period);

		return result;
	}

	private long queryVpnReportData(String mac, int period) {
		long status = getStatus(mac, period);
		if (status == QUERY_OK) {
			String path = initFolder(mac, period);
			if (path != null) {
				List<HiveAp> devices = QueryUtil.executeQuery(HiveAp.class,
						null, new FilterParams("macaddress", mac));

				if (devices == null || devices.size() <= 0) return ERROR_NODEVICE;
				HiveAp device = devices.get(0);

				if (device.getManageStatus() != HiveAp.STATUS_MANAGED) return ERROR_NOTMANAGED;

				if (!device.isConnected()) return ERROR_DISCONNECTED;

				if (pushCli(device, period, path)) {
					// clear latest data
					clearLatestData(mac, period);

					File[] xmls = checkFile(path);
					if (xmls != null && xmls.length > 0) {
						try {
							List<HmBo> throughput = new ArrayList<HmBo>();
							List<HmBo> latency = new ArrayList<HmBo>();
							List<HmBo> availability = new ArrayList<HmBo>();
							List<HmBo> vpnStatus = new ArrayList<HmBo>();

							for (File file : xmls) {
								List<HmBo>[] bos = parseXml(file, period, device.getOwner());
								if (bos == null) continue;

								throughput.addAll(bos[0]);
								latency.addAll(bos[1]);
								availability.addAll(bos[2]);
								vpnStatus.addAll(bos[3]);
							}

							// filter duplicate data
							CollectionUtils.filter(throughput, new TimeFilter());
							CollectionUtils.filter(latency, new TimeFilter());
							CollectionUtils.filter(availability, new TimeFilter());
							CollectionUtils.filter(vpnStatus, new TimeFilter());

								if (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsThroughputHigh.class, throughput);
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsLatencyHigh.class, latency);
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsAvailabilityHigh.class, availability);
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsVpnStatusHigh.class, vpnStatus);
								} else {
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsThroughputLow.class, throughput);
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsLatencyLow.class, latency);
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsAvailabilityLow.class, availability);
								BulkUpdateUtil.bulkInsertVPNReport(AhStatsVpnStatusLow.class, vpnStatus);
							}
						} catch (Exception e) {
							DebugUtil.performanceDebugError("Failed to bulk insert vpn report data into db.", e);
							return ERROR_DB;
						}
					} else {return ERROR_FILE;}
				} else {
					return ERROR_CLI;
				}

				return QUERY_OK;
			} else {
				return ERROR_INIT;
			}
		} else {
			return status;
		}
	}

	private void clearLatestData(String mac, int period) {
		try {
			if (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
				QueryUtil.bulkRemoveBos(AhStatsThroughputHigh.class, new FilterParams("mac = :s1", new Object[] { mac }));
				QueryUtil.bulkRemoveBos(AhStatsLatencyHigh.class, new FilterParams("mac = :s1", new Object[] { mac }));
				QueryUtil.bulkRemoveBos(AhStatsAvailabilityHigh.class, new FilterParams("mac = :s1", new Object[] { mac }));
				QueryUtil.bulkRemoveBos(AhStatsVpnStatusHigh.class, new FilterParams("mac = :s1", new Object[] { mac }));
			} else {
				QueryUtil.bulkRemoveBos(AhStatsThroughputLow.class, new FilterParams("mac = :s1", new Object[] { mac }));
				QueryUtil.bulkRemoveBos(AhStatsLatencyLow.class, new FilterParams("mac = :s1", new Object[] { mac }));
				QueryUtil.bulkRemoveBos(AhStatsAvailabilityLow.class, new FilterParams("mac = :s1", new Object[] { mac }));
				QueryUtil.bulkRemoveBos(AhStatsVpnStatusLow.class, new FilterParams("mac = :s1", new Object[] { mac }));
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeInterfaceReportProcessor.clearLatestData() Exception in processor thread", e);
		}
	}

	@SuppressWarnings("unchecked")
	public List<HmBo>[] parseXml(File file, int period, HmDomain owner) {
		SAXReader reader = new SAXReader();
		List<HmBo> throughput = new ArrayList<HmBo>();
		List<HmBo> latency = new ArrayList<HmBo>();
		List<HmBo> availability = new ArrayList<HmBo>();
		List<HmBo> vpnStatus = new ArrayList<HmBo>();
		try {
			Document xml = reader.read(file);
			Element body = xml.getRootElement();
			List<Element> stats = body.elements();
			for (Element el : stats) {
				String mac = null;
				String hostName = null;
				String sid = null;
				long time;
				List<Element> dataList = el.elements();
				for (Element data : dataList) {
					if (data.getName().equalsIgnoreCase("ident")) {
						mac = data.attributeValue("mac").toUpperCase();
						hostName = data.attributeValue("name");
					} else if (data.getName().equalsIgnoreCase("system")) {
						// ignore system element
					} else if (data.getName().equalsIgnoreCase("sequence")) {
						sid = data.attributeValue("id");
						time = Long.parseLong(data.attributeValue("time")) * 1000;

						List<Element> s1Data = data.elements();
						for (Element s1 : s1Data) {
							List<Element> s2Data = s1.elements();
							for (Element s2 : s2Data) {
								if (s2.getName().equalsIgnoreCase("throughput")) {
									List<Element> thpList = s2.elements();
									for (Element thp : thpList) {
										byte type = AhPortAvailability.INTERFACE_TYPE_VPN_STRING
												.equalsIgnoreCase(thp
														.attributeValue("type")) ? AhPortAvailability.INTERFACE_TYPE_VPN
												: AhPortAvailability.INTERFACE_TYPE_WAN;
										if (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
											AhStatsThroughputHigh bo = new AhStatsThroughputHigh();
											bo.setMac(mac);
											bo.setHostName(hostName);
											bo.setSid(sid);
											bo.setTime(time);
											bo.setInterfType(type);
											bo.setInterfName(thp.attributeValue("name"));
											bo.setInterfServer(thp.attributeValue("server"));
											bo.setRxPkts(Long.parseLong(thp.attributeValue("rx-pkts")));
											bo.setTxPkts(Long.parseLong(thp.attributeValue("tx-pkts")));
											bo.setRxBytes(Long.parseLong(thp.attributeValue("rx-bytes")));
											bo.setTxBytes(Long.parseLong(thp.attributeValue("tx-bytes")));
											bo.setOwner(owner);
											throughput.add(bo);
										} else {
											AhStatsThroughputLow bo = new AhStatsThroughputLow();
											bo.setMac(mac);
											bo.setHostName(hostName);
											bo.setSid(sid);
											bo.setTime(time);
											bo.setInterfType(type);
											bo.setInterfName(thp.attributeValue("name"));
											bo.setInterfServer(thp.attributeValue("server"));
											bo.setRxPkts(Long.parseLong(thp.attributeValue("rx-pkts")));
											bo.setTxPkts(Long.parseLong(thp.attributeValue("tx-pkts")));
											bo.setRxBytes(Long.parseLong(thp.attributeValue("rx-bytes")));
											bo.setTxBytes(Long.parseLong(thp.attributeValue("tx-bytes")));
											bo.setOwner(owner);
											throughput.add(bo);
										}
									}

								} else if (s2.getName().equalsIgnoreCase("latency")) {
									List<Element> ltyList = s2.elements();
									for (Element lty : ltyList) {
										byte type = AhPortAvailability.INTERFACE_TYPE_VPN_STRING
										.equalsIgnoreCase(lty
												.attributeValue("type")) ? AhPortAvailability.INTERFACE_TYPE_VPN
										: AhPortAvailability.INTERFACE_TYPE_WAN;
										String name = lty.attributeValue("name");
										String server = lty.attributeValue("server");

										List<Element> ltySubList = lty.elements();
										for (Element ltySub : ltySubList) {
											if (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
												AhStatsLatencyHigh bo = new AhStatsLatencyHigh();
												bo.setMac(mac);
												bo.setHostName(hostName);
												bo.setSid(sid);
												bo.setTime(time);
												bo.setInterfName(name);
												bo.setInterfType(type);
												bo.setInterfServer(server);
												bo.setName(ltySub.attributeValue("name"));
												bo.setRtt(Double.parseDouble(ltySub.attributeValue("rtt")));
												bo.setTargetStatus(Byte.parseByte(ltySub.attributeValue("target_status")));
												bo.setOwner(owner);
												latency.add(bo);
											} else {
												AhStatsLatencyLow bo = new AhStatsLatencyLow();
												bo.setMac(mac);
												bo.setHostName(hostName);
												bo.setSid(sid);
												bo.setTime(time);
												bo.setInterfName(name);
												bo.setInterfType(type);
												bo.setInterfServer(server);
												bo.setName(ltySub.attributeValue("name"));
												bo.setRtt(Double.parseDouble(ltySub.attributeValue("rtt")));
												bo.setTargetStatus(Byte.parseByte(ltySub.attributeValue("target_status")));
												bo.setOwner(owner);
												latency.add(bo);
											}
										}
									}
								} else if (s2.getName().equalsIgnoreCase("wan-status")) {
									List<Element> wanList = s2.elements();
									for (Element wan : wanList) {
										byte status = AhPortAvailability.INTERFACE_STATUS_DOWN_STRING
												.equalsIgnoreCase(wan
														.attributeValue("status")) ? AhPortAvailability.INTERFACE_STATUS_DOWN
												: AhPortAvailability.INTERFACE_STATUS_UP;
										if (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
											AhStatsAvailabilityHigh bo = new AhStatsAvailabilityHigh();
											bo.setMac(mac);
											bo.setHostName(hostName);
											bo.setSid(sid);
											bo.setTime(time);
											bo.setInterfName(wan.attributeValue("name"));
											bo.setInterfStatus(status);
											bo.setInterfActive(Byte.parseByte(wan.attributeValue("active")));
											bo.setOwner(owner);
											availability.add(bo);
										} else {
											AhStatsAvailabilityLow bo = new AhStatsAvailabilityLow();
											bo.setMac(mac);
											bo.setHostName(hostName);
											bo.setSid(sid);
											bo.setTime(time);
											bo.setInterfName(wan.attributeValue("name"));
											bo.setInterfStatus(status);
											bo.setInterfActive(Byte.parseByte(wan.attributeValue("active")));
											bo.setOwner(owner);
											availability.add(bo);
										}
									}
								} else if (s2.getName().equalsIgnoreCase("vpn")) {
									byte status = AhPortAvailability.INTERFACE_STATUS_DOWN_STRING
											.equalsIgnoreCase(s2
													.attributeValue("status")) ? AhPortAvailability.INTERFACE_STATUS_DOWN
											: AhPortAvailability.INTERFACE_STATUS_UP;

									if (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR) {
										AhStatsVpnStatusHigh bo = new AhStatsVpnStatusHigh();
										bo.setMac(mac);
										bo.setHostName(hostName);
										bo.setSid(sid);
										bo.setTime(time);
										bo.setVpnStatus(status);
										bo.setTunnelCount(Integer.parseInt(s2.attributeValue("tunnel-count")));
										bo.setOwner(owner);
										vpnStatus.add(bo);
									} else {
										AhStatsVpnStatusLow bo = new AhStatsVpnStatusLow();
										bo.setMac(mac);
										bo.setHostName(hostName);
										bo.setSid(sid);
										bo.setTime(time);
										bo.setVpnStatus(status);
										bo.setTunnelCount(Integer.parseInt(s2.attributeValue("tunnel-count")));
										bo.setOwner(owner);
										vpnStatus.add(bo);
									}
								}
							}
						}
					}
				}
			}
			return (new List[]{ throughput, latency, availability, vpnStatus });
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeInterfaceReportProcessor.parseXml() Exception in processor thread", e);
		}
		return null;
	}

	private File[] checkFile(String path) {
		try {
			File file = new File(path + "vpn_report.tgz");
			if (file.isFile() && HmBeOsUtil.execCommand("tar zxf " + file.getAbsolutePath() + " -C " + path, 1) == null &&
					HmBeOsUtil.execCommand("gzip -dq " + path + "*.xml.gz", 1) == null) {
				file.delete();

				// list all xml files
				File dir = new File(path);
				if (dir.isDirectory()) {
					return dir.listFiles(new FilenameFilter() {
						public boolean accept(File dir, String name) {
							if (name.endsWith(".xml")) {
								return true;
							} else return false;
						}
					});
				} else return null;
			} else return null;
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeInterfaceReportProcessor.checkFile() Exception in processor thread", e);
			return null;
		}
	}

	private boolean pushCli(HiveAp device, int period, String path) {
		String host = NmsUtil.getRunningCapwapServer(device);
		String userName = NmsUtil.getHMScpUser();
		String password = NmsUtil.getHMScpPsd();
		String cli;

		if (device.getTransferProtocol() == BeAPConnectEvent.TRANSFERMODE_UDP) {
			cli = AhCliFactory.getSaveVPNReportFileViaSSH(host, userName, password, period, path);
		} else {
			String proxy = device.getProxyName();
			int proxyPort = device.getProxyPort();
			String proxyLoginUser = device.getProxyUsername();
			String proxyLoginPwd = device.getProxyPassword();

			cli = AhCliFactory.getSaveVPNReportViaHTTPS(host, device.getMacAddress(), userName, password, proxy,
					proxyPort, proxyLoginUser, proxyLoginPwd, period);
		}

		// send cli event to device
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(device);
		cliEvent.setClis(new String[] { cli });
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());

		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("Failed to build request to send cli", e);
			return false;
		}

		BeCommunicationEvent resultEvent = HmBeCommunicationUtil.sendSyncRequest(cliEvent);

		if (resultEvent == null) {
			DebugUtil.performanceDebugWarn("Failed to get response of sent cli, cli:" + cli);
			return false;
		} else {
			int msgType = resultEvent.getMsgType();
			if (msgType == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
				return false;
			} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				try {
					resultEvent.parsePacket();
				} catch (BeCommunicationDecodeException e) {
					DebugUtil.performanceDebugError("Failed to pasre result from device:" + device.getMacAddress(), e);
					return false;
				}
				BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) resultEvent;

				return cliResult.isCliSuccessful();
			}
			return false;
		}
	}

	private String initFolder(String mac, int period) {
		String subDir = (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR ? "high" : "low");
		String xmlDir = AhDirTools.getInterfaceReportUploadDir(mac + File.separator + subDir);
		try {
			File dir = new File(xmlDir);
			if (dir.isDirectory()) {
				for (File file : dir.listFiles()) file.delete();
			}
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeInterfaceReportProcessor.initFolder() Exception in processor thread", e);
			return null;
		}
		return xmlDir;
	}

	private synchronized void updateStatus(String mac, int period, long status) {
		Map<String, long[]> queryStatus = (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR ? queryHighStatus
				: queryLowStatus);
		long[] qStatus = queryStatus.get(mac);

		if (qStatus != null) {
			qStatus[2] = status;
		}
	}

	private synchronized long getStatus(String mac, int period) {
		Map<String, long[]> queryStatus = (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR ? queryHighStatus
				: queryLowStatus);

		long[] qStatus = queryStatus.get(mac);
		long curTime = System.currentTimeMillis();
		if (qStatus == null) {
			qStatus = new long[] { curTime, period, QUERY_DOING };
			queryStatus.put(mac, qStatus);
			return QUERY_OK;
		}

		long cmpTime = (period == AhReport.REPORT_PERIOD_VPN_ONEHOUR ? ONE_MINUTE : TEN_MINUTES);

		if ((((curTime - qStatus[0]) >= cmpTime || (curTime - qStatus[0]) < 0 ||
				(period != AhReport.REPORT_PERIOD_VPN_ONEHOUR && period > qStatus[1])) &&
				qStatus[2] == QUERY_DONE) || qStatus[2] < 0) {
			qStatus[0] = curTime;
			qStatus[1] = period;
			qStatus[2] = QUERY_DOING;
			return QUERY_OK;
		} else {
			return qStatus[2];
		}
	}
}
