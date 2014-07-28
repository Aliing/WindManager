package com.ah.be.config.create.source.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApL3cfgNeighbor;
import com.ah.bo.mobility.HiveProfile;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.RoamingProfileInt;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * 
 * @author zhang
 * 
 */
public class RoamingProfileImpl implements RoamingProfileInt {

	private static final Tracer log = new Tracer(RoamingProfileImpl.class
			.getSimpleName());
	private HiveProfile hiveProfile;
	private HiveAp hiveAp;
	private List<HiveApL3cfgNeighbor> includeNeighbors = new LinkedList<HiveApL3cfgNeighbor>();
	private List<HiveApL3cfgNeighbor> excludeNeighbors = new LinkedList<HiveApL3cfgNeighbor>();

	public RoamingProfileImpl(HiveAp hiveAp) {

		hiveProfile = hiveAp.getConfigTemplate().getHiveProfile();
		this.hiveAp = hiveAp;

		for (HiveApL3cfgNeighbor cfgNeighbor : hiveAp.getL3Neighbors()) {
			String ipAddress, netmask;
			ipAddress = cfgNeighbor.getCfgIpAddress();
			netmask = cfgNeighbor.getCfgNetMask();
			if(ipAddress == null || "".equals(ipAddress)){
				ipAddress = cfgNeighbor.getIpAddress();
			}
			if(netmask == null || "".equals(netmask)){
				netmask = cfgNeighbor.getNetMask();
			}
			if (ipAddress != null && !"".equals(ipAddress)) {
				if (cfgNeighbor.getNeighborType() == HiveApL3cfgNeighbor.NEIGHBOR_TYPE_INCLUDED) {
					if (netmask != null && !"".equals(netmask)) {
						includeNeighbors.add(cfgNeighbor);
					} else {
						String[] warnParam = { hiveAp.getHostName(),
								cfgNeighbor.getHostname(),
								"NetMask" };
						String warnMsg = NmsUtil.getUserMessage(
								"info.be.config.create.neighborApNoIpOrMask",
								warnParam);
						log.warning("RoamingProfileImpl", warnMsg);
					}
				} else {
					excludeNeighbors.add(cfgNeighbor);
				}
			} else {
				String[] warnParam = { hiveAp.getHostName(),
						cfgNeighbor.getHostname(), "IpAddress" };
				String warnMsg = NmsUtil
						.getUserMessage(
								"info.be.config.create.neighborApNoIpOrMask",
								warnParam);
				log.warning("RoamingProfileImpl", warnMsg);
			}
		}
	}
	
	public String getHiveProfileGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.hiveProfiles");
	}
	
	public String getHiveProfileName(){
		if(hiveProfile != null){
			return hiveProfile.getHiveName();
		}else{
			return null;
		}
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}

	public String getUpdateTime() {
		// List<Object> roamingList = new ArrayList<Object>();
		// roamingList.add(hiveProfile);
		// roamingList.add(interRoaming);
		// roamingList.add(hiveAp);
		// if(includeNeighbors != null){
		// for(HiveApL3cfgNeighbor apNeib : includeNeighbors){
		// roamingList.add(apNeib);
		// roamingList.add(apNeib.getNeighbor());
		// }
		// }
		// if(excludeNeighbors != null){
		// for(HiveApL3cfgNeighbor apNeib : excludeNeighbors){
		// roamingList.add(apNeib);
		// roamingList.add(apNeib.getNeighbor());
		// }
		// }
		// return CLICommonFunc.getLastUpdateTime(roamingList);
		return CLICommonFunc.getLastUpdateTime(null);
	}

	public int getRoamingPort() {
		return hiveProfile.getL3TrafficPort();
	}

	public boolean isEnabledL3Setting() {
		return hiveProfile.getEnabledL3Setting();
	}

	public boolean isConfigureNeighbor() {
		return (includeNeighbors != null && includeNeighbors.size() > 0)
				|| (excludeNeighbors != null && excludeNeighbors.size() > 0)
				|| isEnabledL3Setting();
	}

	public int getUpdateInterval() {
		return hiveProfile.getUpdateInterval();
	}

	public int getCacheAgeout() {
		return hiveProfile.getUpdateAgeout();
	}

	public int getNeighborQueryInteval() {
		return hiveProfile.getKeepAliveInterval();
	}

	public int getNeighborQueryTime() {
		return hiveProfile.getKeepAliveAgeout();
	}

	public int getNeighborIncludeSize() {
		return includeNeighbors.size();
	}

	public int getNeighborExcludeSize() {
		return excludeNeighbors.size();
	}

	public boolean isConfigNeighborQuery() {
		return isEnabledL3Setting();
	}

	public boolean isConfigureNeighborInclude() {
		return this.getNeighborIncludeSize() > 0;
	}

	public boolean isConfigureNeighborExclude() {
		return this.getNeighborExcludeSize() > 0;
	}

	public String getNeighborIncludeIp(int index) {
		HiveApL3cfgNeighbor l3Neighbor = includeNeighbors.get(index);
		String ipAddr = l3Neighbor.isDhcp() ? l3Neighbor.getIpAddress() : l3Neighbor.getCfgIpAddress();
		if(StringUtils.isEmpty(ipAddr)){
			ipAddr = l3Neighbor.getIpAddress();
		}
		return ipAddr;
		// return includeNeighbors.get(index).getNeighbor().getCfgIpAddress();
	}

	public String getNeighborIncludeNetMask(int index) {
		HiveApL3cfgNeighbor l3Neighbor = includeNeighbors.get(index);
		String maskStr = l3Neighbor.isDhcp() ? l3Neighbor.getNetMask() : l3Neighbor.getCfgNetMask();
		if (StringUtils.isEmpty(maskStr)) {
			maskStr = l3Neighbor.getNetMask();
		}
		return maskStr;
		// return includeNeighbors.get(index).getNeighbor().getCfgNetmask();
	}

	public String getNeighborExcludeIp(int index) {
		HiveApL3cfgNeighbor l3Neighbor = excludeNeighbors.get(index);
		if (l3Neighbor.isDhcp()) {
			return l3Neighbor.getIpAddress();
		} else {
			return l3Neighbor.getCfgIpAddress();
		}
		// return excludeNeighbors.get(index).getNeighbor().getCfgIpAddress();
	}
	
	public boolean isRoamingCacheAccessEnable(){
		return hiveProfile.getNeighborTypeAccess();
	}
	
	public boolean isRoamingCacheBackhaulEnable(){
		return hiveProfile.getNeighborTypeBack();
	}

}