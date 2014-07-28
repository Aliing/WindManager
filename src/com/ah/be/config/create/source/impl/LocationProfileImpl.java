package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.be.config.create.source.LocationProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.LocationClientWatch;
import com.ah.bo.network.SingleTableItem;
import com.ah.bo.useraccess.LocationServer;
import com.ah.util.MgrUtil;

/**
 * @author zhang
 * @version 2007-12-18 18:01:27
 */

public class LocationProfileImpl implements LocationProfileInt {
	
	private HiveAp hiveAp;
	private LocationServer locationServer;
	private List<String> macList = new ArrayList<String>();
	private List<String> ouiList = new ArrayList<String>();

	public LocationProfileImpl(HiveAp hiveAp) throws CreateXMLException{
		this.hiveAp = hiveAp;
		
		locationServer = hiveAp.getConfigTemplate().getLocationServer();
		LocationClientWatch clientWatch = hiveAp.getConfigTemplate().getClientWatch();
		
		if(clientWatch != null && clientWatch.getItems() != null){
			for(SingleTableItem singItem : clientWatch.getItems()){
				if(CLICommonFunc.isRuleMatch(singItem, this.hiveAp)){
					if(this.isMacOui(singItem.getMacEntry())){
						ouiList.add(CLICommonFunc.transFormMacAddrOrOui(singItem.getMacEntry()));
					}else{
						macList.add(CLICommonFunc.transFormMacAddrOrOui(singItem.getMacEntry()));
					}
				}
			}
		}
	}
	
	public boolean isMacOui(String addr){
		return addr != null && addr.length()==6;
	}
	
//	private String generateMacOrOui(String macAddr){
//		if(macAddr == null){
//			return null;
//		}
//		if(macAddr.length() == 6){
//			return macAddr.substring(0, 2)+"-"+macAddr.substring(2, 4)+"-"+macAddr.substring(4, 6);
//		}else if(macAddr.length() == 12){
//			return macAddr.substring(0, 2)+"-"+macAddr.substring(2, 4)+"-"+macAddr.substring(4, 6)+
//				macAddr.substring(6, 8)+"-"+macAddr.substring(8, 10)+"-"+macAddr.substring(10, 12);
//		}else{
//			return null;
//		}
//	}
	
	public String getLocationGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.locationServer");
	}
	
	public String getLocationName(){
		if(locationServer != null){
			return locationServer.getName();
		}else{
			return null;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public boolean isConfigLocation(){
		return locationServer != null;
//		if(CLICommonFunc.HiveApVer.HiveOS_LOW.isEquals(getApVersion())){
//			return false;
//		}else if(CLICommonFunc.HiveApVer.HiveOS_HIGH.isEquals(getApVersion())){
//			return locationServer != null;
//		}else{
//			return false;
//		}
	}
	
	public String getUpdateTime(){
		List<Object> locationObj = new ArrayList<Object>();
		locationObj.add(hiveAp);
		locationObj.add(locationServer);
		if(locationServer != null){
			locationObj.add(locationServer.getServerIP());
		}
		return CLICommonFunc.getLastUpdateTime(locationObj);
	}
	
	public boolean isEnableLocationServer(){
		return locationServer.isEnableServer();
	}
	
	public boolean isEnableRogueAp(){
		return locationServer.isEnableRogue() && locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT;
	}
	
	public boolean isEnableStation(){
		return locationServer.isEnableStation() && locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT;
	}
	
	public boolean isEnableTag(){
		return (locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT && locationServer.isEnableTag()) ||
			locationServer.getServiceType() == LocationServer.SERVICETYPE_EKAHAU;
	}
	
	public String getLocationServer() throws CreateXMLException{
		return CLICommonFunc.getIpAddress(locationServer.getServerIP(), hiveAp).getIpAddress();
	}
	
	public boolean isConfigRateThreshold(){
//		return locationServer.isEnableRogue() || locationServer.isEnableStation() || locationServer.isEnableTag() || 
//			locationServer.getServiceType() == LocationServer.SERVICETYPE_EKAHAU;
		return this.isEnableTag() || this.isEnableStation() || this.isEnableRogueAp();
	}
	
	public int getRateThresholdTag(){
		if(locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT){
			return locationServer.getTagThreshold();
		}else{
			return locationServer.getEkahauTagThreshold();
		}
	}
	
	public int getRateThresholdStation(){
		return locationServer.getStationThreshold();
	}
	
	public int getRateThresholdRogue(){
		return locationServer.getRogueThreshold();
	}
	
	public boolean isLocationAerohive(){
		return locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROHIVE;
	}
	
	public boolean isLocationAeroscout(){
		return locationServer.getServiceType() == LocationServer.SERVICETYPE_AEROSCOUT;
	}
	
	public int getRssiUpdateThreshold(){
		return locationServer.getRssiChangeThreshold();
	}
	
	public int getRssiValidPeriod(){
		int period = locationServer.getRssiValidPeriod();
		if(period < 30 && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.3.0") < 0 ){
			return 30;
		}else{
			return period;
		}
	}
	
	public int getRssiHoldTime(){
		int time = locationServer.getRssiHoldCount();
		if(time < 1 && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.3.0") < 0 ){
			return 1;
		}else{
			return time;
		}
	}
	
	public int getReportInterval(){
		return locationServer.getLocationReportInterval();
	}
	
	public int getSuppressReport(){
		int report = locationServer.getReportSuppressCount();
		if(report < 1 && NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(), "3.4.3.0") < 0 ){
			return 1;
		}else{
			return report;
		}
	}
	
	public int getAerohiveMacSize(){
		return macList.size();
	}
	
	public String getAerohiveMacAddr(int index){
		return macList.get(index).toLowerCase();
	}
	
	public int getAerohiveOuiSize(){
		return ouiList.size();
	}
	
	public String getAerohiveOuiAddr(int index){
		return ouiList.get(index).toLowerCase();
	}
	
	public boolean isEnableListMatch(){
		return macList.size() > 0 || ouiList.size() > 0;
	}
	
	public boolean isConfigLocationEkahau(){
		return locationServer.getServiceType() == LocationServer.SERVICETYPE_EKAHAU && 
				locationServer.isEnableServer();
	}
	
	public String getMcastMac(){
		return CLICommonFunc.transFormMacAddrOrOui(locationServer.getEkahauMac());
	}
	
	public boolean isConfigEkahauServer(){
//		String ipAddr = locationServer.getEkahauIpAddress();
//		String domain = locationServer.getEkahauDomain();
//		return (ipAddr != null && !"".equals(ipAddr)) || (domain != null && !"".equals(domain));
		return locationServer.getServerIP() != null;
	}
	
	public String getEkahauServerValue() throws CreateXMLException{
//		if(locationServer.getEkahauServerType() == LocationServer.EKAHAU_SERVERTYPE_IP){
//			return locationServer.getEkahauIpAddress();
//		}else{
//			return locationServer.getEkahauDomain();
//		}
		return CLICommonFunc.getIpAddress(locationServer.getServerIP(), this.hiveAp).getIpAddress();
	}
	
	public int getEkahauServerPort(){
		return locationServer.getEkahauPort();
	}
}
