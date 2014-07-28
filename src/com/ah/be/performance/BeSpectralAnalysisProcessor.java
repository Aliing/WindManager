package com.ah.be.performance;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeAPConnectEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.communication.event.BeSpectralAnalysisEvent;
import com.ah.be.communication.mo.SpectralAnalysisData;
import com.ah.be.communication.mo.SpectralAnalysisDataSample;
import com.ah.be.communication.mo.SpectralAnalysisInterference;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.sa3party.Sa3Infc;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhCustomReport;
import com.ah.bo.performance.AhSpectralAnalysis;
import com.ah.bo.wlan.RadioProfile;
import com.ah.util.CountryCode;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;
import com.ah.util.devices.impl.Device;

public class BeSpectralAnalysisProcessor implements QueryBo {

	private final BlockingQueue<BeBaseEvent>			eventQueue;

	public static final short								STATUS_SUCCESS		= 0;
	public static final short								STATUS_DOING		= 1;
	public static final short								STATUS_NOAP			= -1;
	public static final short								STATUS_SIMULATED	= -2;
	public static final short								STATUS_MAXAP		= -3;
	public static final short								STATUS_ERROR_DB		= -4;
	public static final short								STATUS_ERROR_CLI	= -5;
	public static final short								STATUS_ERROR_CHANNEL= -6;
	public static final short								STATUS_ERROR_RADIO	= -7;
	public static final short								STATUS_ERROR_3RD	= -8;

	private static final int							eventQueueSize		= 10000;

	private static final int							MAX_SAAP_SIZE       = 30;

	private static final int							MAX_QUEUE_SIZE      = 200;

	private final ConcurrentMap<SimpleHiveAp, SAApInfo>			runningAps			= new ConcurrentHashMap<SimpleHiveAp, SAApInfo>(MAX_SAAP_SIZE);

	private final ConcurrentMap<SimpleHiveAp, Integer>			illegalEventCounts	= new ConcurrentHashMap<SimpleHiveAp, Integer>();

	private final ConcurrentMap<CacheKey, Queue<SpectralAnalysisDataSample>>	dataCache;
	private final ConcurrentMap<CacheKey, SpectralAnalysisDataSample>			dataCacheOne;

	private final ConcurrentMap<CacheKey, Queue<SpectralAnalysisDataSample>>	dataCacheMaxHoldSample;
	private final ConcurrentMap<CacheKey, SpectralAnalysisDataSample>			dataCacheMaxHoldSampleOne;

	private final ConcurrentMap<CacheKey, Map<Short, SpectralAnalysisData>>		dataCacheData;

	private final ConcurrentMap<CacheKey, Map<Short, SpectralAnalysisData>>		dataCacheMaxHoldData;

	private final ConcurrentMap<CacheKey, Queue<SpectralAnalysisInterference>>	interfereCache;

	private boolean										isContinue			= true;

	private SAProcessorThread 							saProcessor;

	private ScheduledExecutorService					scheduler;

	public BeSpectralAnalysisProcessor() {
		eventQueue = new LinkedBlockingQueue<BeBaseEvent>(eventQueueSize);
		dataCache = new ConcurrentHashMap<CacheKey, Queue<SpectralAnalysisDataSample>>(MAX_SAAP_SIZE);
		dataCacheOne = new ConcurrentHashMap<CacheKey, SpectralAnalysisDataSample>(MAX_SAAP_SIZE);
		dataCacheMaxHoldSample = new ConcurrentHashMap<CacheKey, Queue<SpectralAnalysisDataSample>>(MAX_SAAP_SIZE);
		dataCacheMaxHoldSampleOne = new ConcurrentHashMap<CacheKey, SpectralAnalysisDataSample>(MAX_SAAP_SIZE);
		dataCacheData = new ConcurrentHashMap<CacheKey, Map<Short, SpectralAnalysisData>>(MAX_SAAP_SIZE);
		dataCacheMaxHoldData = new ConcurrentHashMap<CacheKey, Map<Short,SpectralAnalysisData>>(MAX_SAAP_SIZE);
		interfereCache = new ConcurrentHashMap<CacheKey, Queue<SpectralAnalysisInterference>>(MAX_SAAP_SIZE);
	}

	public void addEvent(BeBaseEvent event) {
		try {
			eventQueue.offer(event);
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeSpectralAnalysisProcessor.addEvent(): Exception while add event to queue", e);
		}
	}

	public void startTask() {
		isContinue = true;

		// timeout scheduler
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();

			scheduler.scheduleWithFixedDelay(new RunningTimer(), 0, 1, TimeUnit.SECONDS);
		}

		// spectral analysis process thread
		saProcessor = new SAProcessorThread();
		saProcessor.setName("spectralAnalysisThread");
		saProcessor.start();

		BeLogTools
		.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Spectral Analysis processor - scheduler for refresh cache is running...");
	}

	public boolean shutdown() {
		if (scheduler != null && !scheduler.isShutdown()) {
			scheduler.shutdown();
		}

		isContinue = false;

		BeBaseEvent stopThreadEvent = new BeBaseEvent();
		eventQueue.clear();
		eventQueue.offer(stopThreadEvent);

		BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
				"<BE Thread> Spectral Analysis processor - scheduler for refresh cache is shutdown");
		return true;
	}

	public boolean isRunningAp(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				return true;
			}
		}
		return false;
	}

	public int getRunningApInfoIf(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				return runningAps.get(singleAP).Interf;
			}
		}
		return 1;
	}

	public String getRunningApInfoRemainTime(Long apId) {
		if (apId==null) return "Time Remaining: 00:00:00";
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				StringBuilder sb = new StringBuilder();
				int remainTime= runningAps.get(singleAP).RunningTimer;
				int rHour=remainTime/60/60;
				int rMin=(remainTime-rHour*60*60)/60;
				int rSec=remainTime-rHour*60*60 - rMin*60;
				sb.append("Time Remaining: " + (rHour>9?rHour:"0" + rHour) + ":"
						+ (rMin>9?rMin:"0" + rMin) + ":" + (rSec>9?rSec:"0" + rSec));
				return sb.toString();
			}
		}
		return "Time Remaining: 00:00:00";
	}

	public String getRunningApInfoCh0(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				return runningAps.get(singleAP).Channel0;
			}
		}
		return "";
	}

	public String getRunningApInfoCh1(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				return runningAps.get(singleAP).Channel1;
			}
		}
		return "";
	}

	public int getRunningApInfoTime(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				return runningAps.get(singleAP).Timer/60;
			}
		}
		return 5;
	}

	public int getRunningApInfoInterval(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				return runningAps.get(singleAP).Interval;
			}
		}
		return 1;
	}

	public short getRunningApInterfaceInfo(Long apId) {
		for(SimpleHiveAp singleAP : runningAps.keySet()){
			if (singleAP.getId().equals(apId)){
				SAApInfo apInfo= runningAps.get(singleAP);
				return calculateInitBandWidth(apInfo.Interf, apInfo.Channels0, apInfo.Channels1);

			}
		}
		return 0;
	}

	public short calculateInitBandWidth(byte interf, Short[] channels0, Short[] channels1) {
		short minValue;
		if (interf==AhCustomReport.REPORT_INTERFACE_WIFI0 ) {
			minValue = calculateMinValue(channels0);
		} else if (interf==AhCustomReport.REPORT_INTERFACE_WIFI1) {
			minValue = calculateMinValue(channels1);
		} else {
			minValue = calculateMinValue(channels0);
			short minValue2 = calculateMinValue(channels1);
			if (minValue>minValue2) {
				minValue = minValue2;
			}
		}
		if (minValue <= 34) {
			 return 2400;
		 } else if (minValue<=99){
			 return 5150;
	 	} else if (minValue<=148){
	 		return 5470;
		} else {
			return 5725;
		}
	}

	public short calculateMinValue(Short[] channels) {
		if (channels==null) return 0;
		short retValue= channels[0];
		for(Short oneValue :channels) {
			if (retValue>oneValue) {
				retValue=oneValue;
			}
		}
		return retValue;
	}

	public Set<SimpleHiveAp> getRunningAp() {
		return runningAps.keySet();
	}

	class SAProcessorThread extends Thread {

		@Override
		public void run() {
			MgrUtil.setTimerName(this.getClass().getSimpleName());
			BeLogTools.info(HmLogConst.M_TRACER | HmLogConst.M_PERFORMANCE,
					"<BE Thread> Spectral Analysis processor - spectral analysis processor is running...");

			while (isContinue) {
				try {
					// take() method blocks
					BeBaseEvent event = eventQueue.take();
					if (null == event)
						continue;

					if (event.getEventType() == BeEventConst.COMMUNICATIONEVENTTYPE) {
						BeCommunicationEvent communicationEvent = (BeCommunicationEvent) event;
						if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
							BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) communicationEvent;
							if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_SPECTRALANALYSIS) {
								SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
								if (ap == null || ap.getManageStatus() != HiveAp.STATUS_MANAGED ||
										ap.isSimulated() || !runningAps.containsKey(ap)) {
									if (ap != null) {
										SAApInfo nullInfo = new SAApInfo();
										nullInfo.Interf = AhCustomReport.REPORT_INTERFACE_BOTH;
										BeLogTools.warn(HmLogConst.M_TRACER, "received one illegal spectral analysis event: " + ap.getMacAddress());

										int count = 1;
										if (illegalEventCounts.containsKey(ap)) {
											count = illegalEventCounts.get(ap);
											illegalEventCounts.put(ap, count + 1);
										} else {
											illegalEventCounts.put(ap, count);
										}

										if (count > MAX_QUEUE_SIZE) {
											BeLogTools.warn(HmLogConst.M_TRACER, "received one illegal spectral analysis event: " + ap.getMacAddress() + ", send stop spectral analysis command!");
											removeRunningAp(ap, nullInfo, false);
										}
									}
									continue;
								}

								BeSpectralAnalysisEvent saEvent = (BeSpectralAnalysisEvent)resultEvent;
								// clear illegal event counts
								illegalEventCounts.remove(ap);

								// add data to cache
								SAApInfo info = runningAps.get(ap);
								if (info != null && info.sa3Infc == null) {
									// spectrum analysis data
									Map<Short, SpectralAnalysisData> saData = dataCacheData.get(info.CacheKey);
									if (saData == null) {
										saData = new HashMap<Short, SpectralAnalysisData>();
										dataCacheData.put(info.CacheKey, saData);
									}
									saEvent.fillData(saData, saEvent.getSample());
									SpectralAnalysisData ts = saData.get(Short.valueOf("0"));
									if (ts == null) {
										ts = new SpectralAnalysisData();
										saData.put(Short.valueOf("0"), ts);
									}
									ts.setTimeStamp(saEvent.getSample().getTimeStamp());

									// sweep data queue(size 200)
									Queue<SpectralAnalysisDataSample> data = dataCache.get(info.CacheKey);
									SpectralAnalysisDataSample dataAll = dataCacheOne.get(info.CacheKey);
									if (data == null) {
										data = new LinkedList<SpectralAnalysisDataSample>();
										dataCache.put(info.CacheKey, data);
									}
									if (dataAll == null) {
										dataAll = new SpectralAnalysisDataSample();
										dataCacheOne.put(info.CacheKey, dataAll);
									}
									saEvent.fillData(dataAll);
									data.add(dataAll.copy());

									if (data.size() > MAX_QUEUE_SIZE) {
										data.poll();
									}

									// sweep data queue max hold
									Queue<SpectralAnalysisDataSample> dataSampleHold = dataCacheMaxHoldSample.get(info.CacheKey);
									SpectralAnalysisDataSample dataOne = dataCacheMaxHoldSampleOne.get(info.CacheKey);
									if (dataSampleHold == null) {
										dataSampleHold = new LinkedList<SpectralAnalysisDataSample>();
										dataCacheMaxHoldSample.put(info.CacheKey, dataSampleHold);
									}
									if (dataOne == null) {
										dataOne = new SpectralAnalysisDataSample();
										dataCacheMaxHoldSampleOne.put(info.CacheKey, dataOne);
									}
									saEvent.fillMaxHold(dataOne);
									dataSampleHold.add(dataOne.copy());

									if (dataSampleHold.size() > MAX_QUEUE_SIZE) {
										dataSampleHold.poll();
									}

									// spectrum analysis data max hold
									Map<Short, SpectralAnalysisData> saDataHold = dataCacheMaxHoldData.get(info.CacheKey);
									if (saDataHold == null) {
										saDataHold = new HashMap<Short, SpectralAnalysisData>();
										dataCacheMaxHoldData.put(info.CacheKey, saDataHold);
									}
									saEvent.fillMaxHold(saDataHold);

									// interference data
									Queue<SpectralAnalysisInterference> interfMaps = interfereCache.get(info.CacheKey);
									if (interfMaps == null) {
										interfMaps = new LinkedList<SpectralAnalysisInterference>();
										interfereCache.put(info.CacheKey, interfMaps);
									}
									saEvent.fillInterference(interfMaps);
								}
							}
						} else if (communicationEvent.getMsgType() == BeCommunicationConstant.MESSAGETYPE_APCONNECT) {
							BeAPConnectEvent connectEvent =	(BeAPConnectEvent)communicationEvent;
							if (connectEvent.isConnectState()) {
								// resend spectral analysis clis after ap reconnected
								resendClis(CacheMgmt.getInstance().getSimpleHiveAp(connectEvent.getApMac()));
							}
						}
					}
				} catch (Exception e) {
					DebugUtil.performanceDebugWarn(
							"BeSpectralAnalysisProcessor.SAProcessorThread.run() Exception in processor thread", e);
				} catch (Error e) {
					DebugUtil.performanceDebugWarn(
							"BeSpectralAnalysisProcessor.SAProcessorThread.run() Error in processor thread", e);
				}
			}
		}
	}

	class RunningTimer implements Runnable {
		@Override
		public void run() {
			try {
				Set<SimpleHiveAp> set = runningAps.keySet();
				for (SimpleHiveAp ap : set) {
					if (runningAps.containsKey(ap)) {
						SAApInfo info = runningAps.get(ap);
						if (info != null) {
							info.RunningTimer -= 1;
							if (info.RunningTimer <= 0) {
								removeRunningAp(ap, info, false);
							}
						}
					}
				}
			} catch (Exception e) {
				DebugUtil.performanceDebugError("RunningTimer.run() catch exception", e);
			} catch (Error e) {
				DebugUtil.performanceDebugError("RunningTimer.run() catch error", e);
			}
		}
	}

	private int removeRunningAp(SimpleHiveAp ap, SAApInfo info, boolean updateFlag) {
		if (!updateFlag) {
			synchronized (runningAps) {
				runningAps.remove(ap);
			}
		}

		if (info.CacheKey != null) {
			dataCache.remove(info.CacheKey);
			dataCacheOne.remove(info.CacheKey);
			dataCacheMaxHoldSample.remove(info.CacheKey);
			dataCacheMaxHoldSampleOne.remove(info.CacheKey);
			dataCacheData.remove(info.CacheKey);
			dataCacheMaxHoldData.remove(info.CacheKey);
			interfereCache.remove(info.CacheKey);
		}
		// get ap from db
		List<HiveAp> aps = QueryUtil.executeQuery(HiveAp.class, null,
				new FilterParams("macaddress", ap.getMacAddress()), ap.getDomainId(), this);

		if (aps == null || aps.size() == 0) {
			DebugUtil.performanceDebugWarn("Cannot find HiveAp objects for spectral analysis");
			return STATUS_NOAP;
		}
		HiveAp hiveAp = aps.get(0);

		List<String> settingClis = parseClis(hiveAp, info.Interf, info.Channels0, info.Channels1, (short)0, true);
		if (settingClis == null) return STATUS_ERROR_CLI;

		String[] clis = settingClis.toArray(new String[settingClis.size()]);

		int cliret = pushClis(hiveAp, clis);
		if (cliret != STATUS_SUCCESS)return cliret;

		BeLogTools.info(HmLogConst.M_TRACER, "AP: " + ap.getMacAddress() + " Spectrum Analysis stoped...");
		return STATUS_SUCCESS;
	}

	public int startSpectralAnalysis(String strMac, Map<String, int[]> channels, short interval, int seconds, Sa3Infc infc) {
		if (infc == null) return STATUS_ERROR_3RD;
		SimpleHiveAp ap = CacheMgmt.getInstance().getSimpleHiveAp(strMac);
		if (ap == null) return STATUS_NOAP;

		if (channels == null) return STATUS_ERROR_CHANNEL;

		String channel0 = "", channel1 = "";
		byte interf = AhCustomReport.REPORT_INTERFACE_WIFI0;
		for (String channel : channels.keySet()) {
			if ("wifi0".equalsIgnoreCase(channel) && channel0.isEmpty()) {
				int[] chns = channels.get(channel);

				if (chns == null || chns.length == 0) continue;
				for (int i : chns) {
					channel0 += i + ",";
				}
				if (channel0.length() > 0)channel0 = channel0.substring(0, channel0.length() - 1);

				continue;
			}
			if ("wifi1".equalsIgnoreCase(channel) && channel1.isEmpty()) {
				int[] chns = channels.get(channel);

				if (chns == null || chns.length == 0) continue;
				for (int i : chns) {
					channel1 += i + ",";
				}
				if (channel1.length() > 0)channel1 = channel1.substring(0, channel1.length() - 1);
			}
		}
		if (channel0.length() > 0 && channel1.length() > 0) {
			interf = AhCustomReport.REPORT_INTERFACE_BOTH;
		} else if (channel0.length() > 0) {
			interf = AhCustomReport.REPORT_INTERFACE_WIFI0;
		} else if (channel1.length() > 0) {
			interf = AhCustomReport.REPORT_INTERFACE_WIFI1;
		}

		int ret =  startSpectralAnalysis(ap, interf, channel0, channel1 ,interval, seconds, infc);
		if (ret != STATUS_SUCCESS) return ret;

		return STATUS_SUCCESS;
	}

	public int startSpectralAnalysis(SimpleHiveAp ap, byte interf, String channel0, String channel1, short interval, int seconds, Sa3Infc... infc) {
		if (ap == null) {
			return STATUS_NOAP;
		} else if (runningAps.containsKey(ap)) {
			SAApInfo info = runningAps.get(ap);
			if (info != null && info.sa3Infc == null) {
				int result = removeRunningAp(ap, info, true);
				if (result != STATUS_SUCCESS) return result;
			} else if (info.sa3Infc != null){
				return STATUS_DOING;
			}
		} else if (ap.isSimulated()) {
			return STATUS_SIMULATED;
		}

		int size = runningAps.size();
		if (size < MAX_SAAP_SIZE) {

			// get channels
			Short[] channels0 = NmsUtil.getNumbersFromRange(channel0);
			Short[] channels1 = NmsUtil.getNumbersFromRange(channel1);
			if (channels0 == null && channels1 == null) return STATUS_ERROR_CHANNEL;

			if (insertSA(ap, interf, channel0, channel1, interval, seconds)) {
				// get ap from db
				List<HiveAp> aps = QueryUtil.executeQuery(HiveAp.class, null,
						new FilterParams("macaddress", ap.getMacAddress()), ap.getDomainId(), this);

				if (aps == null || aps.size() == 0) {
					DebugUtil.performanceDebugWarn("Cannot find HiveAp objects for spectral analysis");
					return STATUS_NOAP;
				}
				HiveAp hiveAp = aps.get(0);

				List<String> settingClis = parseClis(hiveAp, interf, channels0, channels1, interval, false);
				if (settingClis == null) return STATUS_ERROR_CLI;
				if (settingClis.size() <= 0) return STATUS_ERROR_CHANNEL;

				String[] clis = settingClis.toArray(new String[settingClis.size()]);

				int cliret = pushClis(hiveAp, clis);
				if (cliret != STATUS_SUCCESS)return cliret;

				// add to running aps
				SAApInfo info = new SAApInfo();
				info.CacheKey = new CacheKey(size);
				info.RunningTimer = seconds;
				info.Timer = seconds;
				info.Interf = interf;
				info.Interval = interval;
				info.Channel0 = channel0;
				info.Channel1 = channel1;
				info.Channels0 = channels0;
				info.Channels1 = channels1;
				if (infc.length > 0) info.sa3Infc = infc[0];

				synchronized (runningAps) {
					runningAps.put(ap, info);
				}

				BeLogTools.info(HmLogConst.M_TRACER, "AP: " + ap.getMacAddress() + " Interface:" + interf +
						" Channel_0:" + channel0 + " Channel_1:" + channel1 + " Run time:" + seconds +
						" Spectrum Analysis has started...");
				return STATUS_SUCCESS;
			} else {
				return STATUS_ERROR_DB;
			}
		} else {
			return STATUS_MAXAP;
		}
	}

	private List<String> parseClis(HiveAp ap, byte interf, Short[] channels0, Short[] channels1, short interval, boolean isNo) {
		List<String> clis = null;

		// wifi0
		if (interf == AhCustomReport.REPORT_INTERFACE_WIFI0 || interf == AhCustomReport.REPORT_INTERFACE_BOTH) {
			clis = new ArrayList<String>();
			clis.add(AhCliFactory.getSpectralScanIntervalCli("wifi0", interval, isNo));
			if (channels0 != null) {
				for (Short channel : channels0) {
					clis.add(AhCliFactory.getSpectralScanChannelCli("wifi0", channel, isNo));
				}
			} else {
				clis.add(AhCliFactory.getSpectralScanChannelCli("wifi0", null, isNo));
			}

			clis.add(AhCliFactory.getExecScanCli("wifi0", isNo ? "stop" : "start"));
		}

		// wifi1
		if (interf == AhCustomReport.REPORT_INTERFACE_WIFI1 || interf == AhCustomReport.REPORT_INTERFACE_BOTH) {
			boolean hasValid = false;
			if (clis == null)clis = new ArrayList<String>();
			String wifi1String = ((AhConstantUtil.isTrueAll(Device.IS_DUALBAND, ap.getHiveApModel()) && channels1 != null) ? "wifi0" : "wifi1");
			RadioProfile wifi1Profile = (AhConstantUtil.isTrueAll(Device.IS_DUALBAND, ap.getHiveApModel()) ? ap.getWifi0RadioProfile() : ap.getWifi1RadioProfile());
			short wifi1ChannelWidth = RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20;
			boolean isEnableDfs = false;
			if (wifi1Profile != null) {
				//wifi1ChannelWidth = wifi1Profile.getChannelWidth();
				isEnableDfs = wifi1Profile.isEnableDfs();
			}
			int[] vaild_wifi1 = CountryCode.getChannelList_5GHz(ap.getCountryCode(), wifi1ChannelWidth,
					isEnableDfs, false, ap.getHiveApModel(), ap.getIsOutdoor());
			StringBuilder vaild_wifi1String = new StringBuilder();
			if (vaild_wifi1 != null) {
				for (int i : vaild_wifi1) {
					vaild_wifi1String.append(":" + i + ":");
				}
			}

			clis.add(AhCliFactory.getSpectralScanIntervalCli(wifi1String, interval, isNo));
			if (channels1 != null) {
				for (Short channel : channels1) {
					if (vaild_wifi1String.indexOf(":" + channel + ":") > -1) {
						if (!hasValid) hasValid = true;
					} else continue;
					clis.add(AhCliFactory.getSpectralScanChannelCli(wifi1String, channel, isNo));
				}
			} else {
				clis.add(AhCliFactory.getSpectralScanChannelCli(wifi1String, null, isNo));
			}
			clis.add(AhCliFactory.getExecScanCli(wifi1String, isNo ? "stop" : "start"));
			if (!hasValid && channels1 != null) clis.clear();
		}

		return clis;
	}

	private int pushClis(HiveAp hiveAp, String[] cmdLines) {
		boolean sent = false;

		// send cli event to ap
		BeCliEvent cliEvent = new BeCliEvent();
		cliEvent.setAp(hiveAp);
		cliEvent.setClis(cmdLines);
		cliEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());

		try {
			cliEvent.buildPacket();
		} catch (BeCommunicationEncodeException e) {
			DebugUtil.performanceDebugError("Failed to build request to send clis", e);
			return STATUS_ERROR_CLI;
		}

		BeCommunicationEvent resultEvent = HmBeCommunicationUtil.sendSyncRequest(cliEvent);

		if (resultEvent == null) {
			DebugUtil.performanceDebugWarn("Failed to get response of sent clis, clis:" + cmdLines);
			return STATUS_ERROR_CLI;
		} else {
			int msgType = resultEvent.getMsgType();
			if (msgType == BeCommunicationConstant.MESSAGETYPE_CLIRSP) {
				return STATUS_ERROR_CLI;
			} else if (msgType == BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT) {
				try {
					resultEvent.parsePacket();
				} catch (BeCommunicationDecodeException e) {
					DebugUtil.performanceDebugError("Failed to pasre result from ap:" + hiveAp.getMacAddress(), e);
					return STATUS_ERROR_CLI;
				}
				BeCapwapCliResultEvent cliResult = (BeCapwapCliResultEvent) resultEvent;

				if (!cliResult.isCliSuccessful()) {
//					return STATUS_ERROR_CLI;
					return cliResult.getErrorCode();
				} else {
					sent = true;
				}
			}
		}
		if (sent) return STATUS_SUCCESS;
		return STATUS_ERROR_CLI;
	}

	private boolean insertSA(SimpleHiveAp ap, byte interf, String channel0, String channel1,
			short interval, int seconds) {

		HmDomain domain = QueryUtil.findBoById(
				HmDomain.class, ap.getDomainId());
		if (domain == null)return false;

		long timeStamp = System.currentTimeMillis();
		String timeZone = domain.getTimeZoneString();

		//String fileName = createBinaryFile(domain, ap.getMacAddress(), timeStamp);

		AhSpectralAnalysis bo = new AhSpectralAnalysis();
		bo.setApMac(ap.getMacAddress());
		bo.setApName(ap.getHostname());
		bo.setChannel0(channel0);
		bo.setChannel1(channel1);
		bo.setInterf(interf);
		bo.setInterval(interval);
		bo.setDataFile(null);
		bo.setRunTime(seconds);
		bo.setTimeStamp(timeStamp);
		bo.setTimeZone(timeZone);
		bo.setOwner(domain);

		try {
			QueryUtil.createBo(bo);
			return true;
		} catch (Exception e) {
			DebugUtil.performanceDebugError(
					"BeSpectralAnalysisProcessor.insertSA() catch exception", e);
		}

		return false;
	}

	@SuppressWarnings("unused")
	private String createBinaryFile(HmDomain domain, String macAddress, long timeStamp) {
		if (domain == null || domain.getDomainName() == null || domain.getDomainName().length() == 0)return null;
		if (macAddress == null || macAddress.length() == 0)return null;

		String fileName;
		try {
			String dateTimeString = AhDateTimeUtil.getDateStrFromLong(
					timeStamp,
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ"),
					domain.getTimeZoneString());

			fileName = AhDirTools.getSADataDir(domain.getDomainName(),
					macAddress) + dateTimeString + ".bin";

			File file = new File(fileName);
			if (!file.exists()) {
				FileOutputStream fos = new FileOutputStream(file);
				fos.flush();
				fos.close();
			}
		} catch (Exception e) {
			DebugUtil
					.performanceDebugError(
							"BeSpectralAnalysisProcessor.createBinaryFile() catch exception",
							e);
			return null;
		}
		return fileName;
	}

	@SuppressWarnings("unused")
	private boolean updateTimer(SimpleHiveAp ap, int seconds) {
		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			synchronized (info) {
				info.RunningTimer = seconds;
			}
			return true;
		} else {
			return false;
		}
	}

	public int stopFrom3rd(long id) {
		for (SimpleHiveAp ap : runningAps.keySet()) {
			SAApInfo info = runningAps.get(ap);
			if (info != null && info.sa3Infc != null && info.sa3Infc.getChannelID() == id) {
				return stopSpectralAnalysis(ap);
			}
		}
		return STATUS_SUCCESS;
	}

	public int stopSpectralAnalysis(String apMac, String[] wifiNames) {
		for (SimpleHiveAp ap : runningAps.keySet()) {
			if (ap.getMacAddress().equalsIgnoreCase(apMac)) {
				SAApInfo info = runningAps.get(ap);
				if (info != null && info.sa3Infc != null) {
					boolean wifi0 = false;
					boolean wifi1 = false;
					for (String name : wifiNames) {
						if (name.equalsIgnoreCase("wifi0") && !wifi0) {
							wifi0 = true;
						} else if (name.equalsIgnoreCase("wifi1") && !wifi1) wifi1 = true;
					}

					try {
						SAApInfo infoCopy = info.clone();
						int ret = STATUS_SUCCESS;
						if (wifi0 && wifi1) {
							ret = removeRunningAp(ap, infoCopy, false);
						} else if (wifi0 && !wifi1) {
							infoCopy.Interf = AhCustomReport.REPORT_INTERFACE_WIFI0;
							ret = removeRunningAp(ap, infoCopy, !(info.Interf == AhCustomReport.REPORT_INTERFACE_WIFI0));
						} else if (!wifi0 && wifi1) {
							infoCopy.Interf = AhCustomReport.REPORT_INTERFACE_WIFI1;
							ret = removeRunningAp(ap, infoCopy, !(info.Interf == AhCustomReport.REPORT_INTERFACE_WIFI1));
						}
						return ret;
					} catch (Exception e) {
						DebugUtil.performanceDebugError(
								"BeSpectralAnalysisProcessor.stopSpectralAnalysis() catch exception(call by 3rd)",
								e);
					}
				}
			}
		}
		return STATUS_SUCCESS;
	}

	public int stopSpectralAnalysis(SimpleHiveAp ap) {
		if (ap == null || ap.isSimulated()) {
			return STATUS_SUCCESS;
		} else {
			SAApInfo info = runningAps.get(ap);
			if (info != null) {
				return removeRunningAp(ap, info, false);
			}
		}
		return STATUS_SUCCESS;
	}

	public Map<Short, SpectralAnalysisData> fetchFFTData(SimpleHiveAp ap) {
		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			Map<Short, SpectralAnalysisData> samples = dataCacheData.get(info.CacheKey);
			if (samples != null) return samples;
		}
		return null;
	}

	public Map<Short, SpectralAnalysisData> fetchMaxHoldData(SimpleHiveAp ap) {
		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			Map<Short, SpectralAnalysisData> dataHold = dataCacheMaxHoldData.get(info.CacheKey);
			if (dataHold != null) return dataHold;
		}
		return null;
	}

	public SpectralAnalysisDataSample[] fetchFFTDatas(SimpleHiveAp ap) {
		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			Queue<SpectralAnalysisDataSample> samples = dataCache.get(info.CacheKey);
			if (samples != null && samples.size() > 0) {
				return samples.toArray(new SpectralAnalysisDataSample[samples.size()]);
			}
		}
		return null;
	}

	public SpectralAnalysisDataSample[] fetchMaxHoldDatas(SimpleHiveAp ap) {
		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			Queue<SpectralAnalysisDataSample> samples = dataCacheMaxHoldSample.get(info.CacheKey);
			if (samples != null && samples.size() > 0) {
				return samples.toArray(new SpectralAnalysisDataSample[samples.size()]);
			}
		}
		return null;
	}

	public List<SpectralAnalysisInterference> fetchInterference(SimpleHiveAp ap) {
		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			Queue<SpectralAnalysisInterference> interf = interfereCache.get(info.CacheKey);
			if (interf != null) {
				List<SpectralAnalysisInterference> listInterf = new ArrayList<SpectralAnalysisInterference>();
				listInterf.addAll(interf);
				Collections.sort(listInterf);

				return listInterf;
			}
		}
		return new ArrayList<SpectralAnalysisInterference>();
	}

	private class SAApInfo implements Cloneable {
		public CacheKey CacheKey;
		public int		RunningTimer = 0;
		public int		Timer;
		public byte 	Interf;
		public short	Interval;
		public String	Channel0;
		public String	Channel1;
		public Short[] 	Channels0;
		public Short[] 	Channels1;
		public Sa3Infc	sa3Infc;

		@Override
		public SAApInfo clone() throws CloneNotSupportedException {
			return (SAApInfo)super.clone();
		}
	}

	@SuppressWarnings("unused")
	private class CacheKey {
		public CacheKey(int index) {
			this.Index = index;
		}
		public final int Index;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HiveAp) {
			HiveAp hiveAp = (HiveAp)bo;
			if (hiveAp.getWifi0RadioProfile() != null) {
				hiveAp.getWifi0RadioProfile().getRadioName();
			}
			if (hiveAp.getWifi1RadioProfile() != null) {
				hiveAp.getWifi1RadioProfile().getRadioName();
			}
		}
		return null;
	}

	public void resendClis(SimpleHiveAp ap) {
		if (ap == null) return;

		SAApInfo info = runningAps.get(ap);
		if (info != null) {
			// get ap from db
			List<HiveAp> aps = QueryUtil.executeQuery(HiveAp.class, null,
					new FilterParams("macaddress", ap.getMacAddress()), ap.getDomainId(), this);

			if (aps == null || aps.size() == 0) {
				DebugUtil.performanceDebugWarn("Cannot find HiveAp objects for spectral analysis");
				return;
			}
			HiveAp hiveAp = aps.get(0);

			List<String> settingClis = parseClis(hiveAp, info.Interf, info.Channels0, info.Channels1, info.Interval, false);
			if (settingClis == null || settingClis.isEmpty()) return;

			String[] clis = settingClis.toArray(new String[settingClis.size()]);
			if (info.RunningTimer <= 0)return;

			int cliret = pushClis(hiveAp, clis);
			if (cliret != STATUS_SUCCESS) {
			}
		} else {
		}
	}

	public boolean filteredBy3rd(String apMac, byte[] data) {
		for (SimpleHiveAp ap : runningAps.keySet()) {
			if (apMac.equalsIgnoreCase(ap.getMacAddress())) {
				SAApInfo info = runningAps.get(ap);
				if (info != null && info.sa3Infc != null) {
					info.sa3Infc.sendSAdata(apMac, data);
					return true;
				}
				break;
			}
		}
		return false;
	}

}