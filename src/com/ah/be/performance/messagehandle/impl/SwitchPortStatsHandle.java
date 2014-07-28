package com.ah.be.performance.messagehandle.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.communication.event.BeSwitchPortStatsReportResultEvent;
import com.ah.be.communication.event.BeSwitchPortStatsResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhSwitchPortPeriodStats;
import com.ah.bo.performance.AhSwitchPortStats;

public class SwitchPortStatsHandle implements MessageHandleInterface {
	static private SwitchPortStatsHandle				instance = null; 
	
	private Map<String,SwitchPortPeriodStatsInfo>		portPeriodStatsMap = Collections.synchronizedMap(new HashMap<String,SwitchPortPeriodStatsInfo>());;
	
	private final	String		AGGX_PORTNAME_PREFIX 	= "agg";
	
	public static SwitchPortStatsHandle getInstance() {
		if(instance == null) {
			instance = new SwitchPortStatsHandle();
		}
		return instance;
	}
	
	public void handleMessage(BeBaseEvent event) {
		if(event.getClass() == BeSwitchPortStatsResultEvent.class) {
			handlePortStatsResultEvent(event);
		} else if(event.getClass() == BeSwitchPortStatsReportResultEvent.class) {
			handlePortStatsReportResultEvent(event);
		}
	}
	
	private void handlePortStatsResultEvent(BeBaseEvent event) {
		BeSwitchPortStatsResultEvent resultEvent = (BeSwitchPortStatsResultEvent)event;
		
		//omit if Device is not exist or is not managed
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
		
		for(AhSwitchPortStats portInfo:resultEvent.getPortStatsList()) {
			portInfo.setOwner(owner);
		}

		
		if(resultEvent.getSequenceNum() != 0) {
			//refresh all port stats
			//delete by mac address
			StringBuffer sql = new StringBuffer();
			sql.append("delete from hm_switch_port_stats where mac = '").append(resultEvent.getApMac()).append("'");
			try {
				QueryUtil.executeNativeUpdate(sql.toString());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortStatsHandle:Exception when delete sql:"+sql.toString(), e);
			}
			
			//insert port info
			try {
				QueryUtil.bulkCreateBos(resultEvent.getPortStatsList());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortStatsHandle,Exception when bulk create port stats", e);
			}
			
			if (NmsUtil.compareSoftwareVersion(simpleHiveAp.getSoftVer(),"6.1.1.0") < 0) {
				//collect port period stats
				SwitchPortPeriodStatsInfo info = portPeriodStatsMap.get(simpleHiveAp.getMacAddress());
				if(info == null) {
					//there has no port period stats, only save into memory
					info = new SwitchPortPeriodStatsInfo();
					info.setTimestamp(System.currentTimeMillis());
					for(AhSwitchPortStats portStats: resultEvent.getPortStatsList()) {
						if(portStats.getPortName() != null && !portStats.getPortName().equalsIgnoreCase(""))
							info.getStatsMap().put(portStats.getPortName(), portStats);
					}
					portPeriodStatsMap.put(simpleHiveAp.getMacAddress(), info);
				} else {
					long timestamp = System.currentTimeMillis();
					List<AhSwitchPortPeriodStats> statsList = new ArrayList<AhSwitchPortPeriodStats>(resultEvent.getPortStatsList().size());
					for(AhSwitchPortStats portStats: resultEvent.getPortStatsList()) {
						if(portStats.getPortName() != null && !portStats.getPortName().equalsIgnoreCase("")) {
							AhSwitchPortStats previousPortStats = info.getStatsMap().get(portStats.getPortName());
							if(previousPortStats != null) {
								AhSwitchPortPeriodStats portPeriodstats = convertPortStatsToPortPeriodStats(simpleHiveAp,portStats,previousPortStats,timestamp-info.getTimestamp());
								if(portPeriodstats.getTxBytesCount() >= 0 
										&& portPeriodstats.getRxBytesCount() >= 0
										&& !portPeriodstats.getPortName().startsWith(AGGX_PORTNAME_PREFIX) )
									statsList.add(portPeriodstats);
							}
							info.getStatsMap().put(portStats.getPortName(), portStats);
						}
					}
					info.setTimestamp(System.currentTimeMillis());
					
					//insert port info
					try {
						BulkUpdateUtil.bulkInsert(AhSwitchPortPeriodStats.class, statsList);
					} catch (Exception e) {
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortStatsHandle,Exception when bulk create port period stats", e);
					}
				}
			}
		}
	}
	
	private AhSwitchPortPeriodStats convertPortStatsToPortPeriodStats(SimpleHiveAp simpleHivAp,AhSwitchPortStats portStats,
			AhSwitchPortStats previousPortStats,long collectPeriod) {
		AhSwitchPortPeriodStats portPeriodStats = new AhSwitchPortPeriodStats();
		
		portPeriodStats.setApmac(portStats.getMac());
		portPeriodStats.setHostname(simpleHivAp.getHostname());
		portPeriodStats.setTimestamp(System.currentTimeMillis());
		portPeriodStats.setCollectPeriod(collectPeriod/1000);
		portPeriodStats.setPortName(portStats.getPortName());
		
		portPeriodStats.setTxPacketCount(portStats.getTxPacketCount()-previousPortStats.getTxPacketCount());
		portPeriodStats.setRxPacketCount(portStats.getRxPacketCount()-previousPortStats.getRxPacketCount());
		portPeriodStats.setTxBytesCount(portStats.getTxBytesCount()-previousPortStats.getTxBytesCount());
		portPeriodStats.setRxBytesCount(portStats.getRxBytesCount()-previousPortStats.getRxBytesCount());
		portPeriodStats.setTxMuticastPackets(portStats.getTxMuticastPackets()-previousPortStats.getTxMuticastPackets());
		portPeriodStats.setRxMuticastPackets(portStats.getRxMuticastPackets()-previousPortStats.getRxMuticastPackets());
		portPeriodStats.setTxUnicastPackets(portStats.getTxUnicastPackets()-previousPortStats.getTxUnicastPackets());
		portPeriodStats.setRxUnicastPackets(portStats.getRxUnicastPackets()-previousPortStats.getRxUnicastPackets());
		portPeriodStats.setTxBroadcastPackets(portStats.getTxBroadcastPackets()-previousPortStats.getTxBroadcastPackets());
		portPeriodStats.setRxBroadcastPackets(portStats.getRxBroadcastPackets()-previousPortStats.getRxBroadcastPackets());
		
		portPeriodStats.setOwner(portStats.getOwner());
		
		return portPeriodStats;
	}
	
	private void handlePortStatsReportResultEvent(BeBaseEvent event) {
		BeSwitchPortStatsReportResultEvent resultEvent = (BeSwitchPortStatsReportResultEvent)event;
		
		//omit if Device is not exist or is not managed
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
		
		List<AhSwitchPortPeriodStats> dataList = new ArrayList<AhSwitchPortPeriodStats>();
		for(AhSwitchPortPeriodStats portStats:resultEvent.getPortStatsList()) {
			portStats.setOwner(owner);
			portStats.setHostname(simpleHiveAp.getHostname());
			if(!portStats.getPortName().startsWith(AGGX_PORTNAME_PREFIX)) {
				dataList.add(portStats);
			}
		}

		
		if(resultEvent.getSequenceNum() != 0) {
			//insert port info
			try {
				BulkUpdateUtil.bulkInsert(AhSwitchPortPeriodStats.class, dataList);
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortStatsHandle,Exception when bulk create port stats report", e);
			}
		}
	}
}

class SwitchPortPeriodStatsInfo {
	long timestamp;
	
	Map<String,AhSwitchPortStats>	statsMap = null;
	
	SwitchPortPeriodStatsInfo() {
		statsMap = Collections.synchronizedMap(new HashMap<String,AhSwitchPortStats>());;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}


	public Map<String, AhSwitchPortStats> getStatsMap() {
		return statsMap;
	}
	
}