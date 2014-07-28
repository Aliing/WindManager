package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.IpAddress;
import com.ah.bo.network.VpnService;
import com.ah.bo.useraccess.MgmtServiceSyslog;
import com.ah.bo.useraccess.MgmtServiceSyslogInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.LogingProfileInt;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.CreateXMLException;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.xml.be.config.FacilityValue;

/**
 * 
 * @author zhang
 *
 */
public class LogingProfileImpl implements LogingProfileInt {
	
	private static final Tracer log = new Tracer(LogingProfileImpl.class
			.getSimpleName());
	
	private final HiveAp hiveAp;
	
	private final MgmtServiceSyslog mgmtServiceSyslog;
	private List<MgmtServiceSyslogInfo> syslogInfo;

	public LogingProfileImpl(HiveAp hiveAp){
		this.hiveAp = hiveAp;
		
		mgmtServiceSyslog = hiveAp.getConfigTemplate().getMgmtServiceSyslog();
		if(mgmtServiceSyslog != null){
			syslogInfo = mgmtServiceSyslog.getSyslogInfo();
		}
	}
	
	public String getLoggingGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceSyslog");
	}
	
	public String getLoggingName(){
		if(mgmtServiceSyslog != null){
			return mgmtServiceSyslog.getMgmtName();
		}else{
			return null;
		}
	}
	
	public String getApVersion(){
		return hiveAp.getSoftVer();
	}
	
	public boolean isConfigureLoging(){
		return mgmtServiceSyslog != null;
	}
	
	public String getUpdateTime(){
		List<Object> loggingTimeList = new ArrayList<Object>();
		loggingTimeList.add(mgmtServiceSyslog);
		loggingTimeList.add(hiveAp);
		if(syslogInfo != null){
			for(MgmtServiceSyslogInfo logInfo : syslogInfo){
				if(logInfo != null){
					loggingTimeList.add(logInfo.getIpAddress());
				}
			}
		}
		return CLICommonFunc.getLastUpdateTime(loggingTimeList);
	}
	
	public FacilityValue getLoggingFacilityValue(){
		FacilityValue resFac=null;
		
		switch(mgmtServiceSyslog.getFacility()){
		case 0:
			resFac=FacilityValue.AUTH;
			break;
		case 1:
			resFac=FacilityValue.AUTHPRIV;
			break;
		case 2:
			resFac=FacilityValue.SECURITY;
			break;
		case 3:
			resFac=FacilityValue.USER;
			break;
		case 4:
			resFac=FacilityValue.LOCAL_0;
			break;
		case 5:
			resFac=FacilityValue.LOCAL_1;
			break;
		case 6:
			resFac=FacilityValue.LOCAL_2;
			break;
		case 7:
			resFac=FacilityValue.LOCAL_3;
			break;
		case 8:
			resFac=FacilityValue.LOCAL_4;
			break;
		case 9:
			resFac=FacilityValue.LOCAL_5;
			break;
		case 10:
			resFac=FacilityValue.LOCAL_6;
			break;
		case 11:
			resFac=FacilityValue.LOCAL_7;
			break;
		default:
			break;
		}
		return resFac;
	}
	
	public int getLoggingServerSize(){
		if(syslogInfo == null){
			return 0;
		}else{
			return syslogInfo.size();
		}
	}
	
	public String getLoggingServerName(int index) throws CreateXMLException{
		MgmtServiceSyslogInfo sysLogInfo = syslogInfo.get(index);
		
		return CLICommonFunc.getIpAddress(sysLogInfo.getIpAddress(), hiveAp).getIpAddress();
	}
	
	public String getLoggingServerLevel(int index){
		MgmtServiceSyslogInfo sysLogInfo = syslogInfo.get(index);
		short level = sysLogInfo.getSeverity();
		String result = "";
		switch(level){
			case 0 : result = LogingProfileInt.SERVER_LEVEL_EMERGENCY; break;
			case 1 : result = LogingProfileInt.SERVER_LEVEL_ALERT; break;
			case 2 : result = LogingProfileInt.SERVER_LEVEL_CRITICAL; break;
			case 3 : result = LogingProfileInt.SERVER_LEVEL_ERROR; break;
			case 4 : result = LogingProfileInt.SERVER_LEVEL_WARNING; break;
			case 5 : result = LogingProfileInt.SERVER_LEVEL_NOTIFICATION; break;
			case 6 : result = LogingProfileInt.SERVER_LEVEL_INFO; break;
			case 7 : result = LogingProfileInt.SERVER_LEVEL_DEBUG; break;
		}
		return result;
	}
	
	public boolean isEnableVpnTunnel(int index) throws CreateXMLException{
		if(hiveAp.getVpnMark() == HiveAp.VPN_MARK_CLIENT){
			VpnService vpnClient = hiveAp.getConfigTemplate().getVpnService();
			if(vpnClient == null){
				return false;
			}else{
				if(vpnClient.isLogThroughTunnel()){
					if(syslogInfo.get(index).getIpAddress().getTypeFlag() == IpAddress.TYPE_HOST_NAME){
						String errMsg = NmsUtil.getUserMessage("error.be.config.create.VPNPermitIp", new String[]{"Logging"});
						log.error("isEnableVpnTunnel", errMsg);
						throw new CreateXMLException(errMsg);
					}
					return true;
				}else{
					return false;
				}
			}
		}else{
			return false;
		}
	}
	
//	public boolean isConfigLogTunnel(int index){
//		if(hiveAp.isVpnServer()){
//			return false;
//		}
//		return syslogInfo.get(index).getIpAddress().getTypeFlag() == IpAddress.TYPE_IP_ADDRESS;
//	}

}