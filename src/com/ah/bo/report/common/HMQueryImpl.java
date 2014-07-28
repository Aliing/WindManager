package com.ah.bo.report.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.bo.admin.HmUser;
import com.ah.bo.performance.AhAssociation;
import com.ah.nms.worker.report.HMQuery;
import com.ah.nms.worker.report.SystemInfoProvider;
import com.ah.nms.worker.report.axis.by.top.LoginListAxis;
import com.ah.ui.actions.monitor.CurrentUserCache;
import com.ah.util.MgrUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.AhEvent;

public class HMQueryImpl implements HMQuery{
    
    	public static final int STATUS_SUCCESS = 0;

	public static final int STATUS_FAILURE = 1;

	public static final int STATUS_EXECUTE = 2;
	
	private static byte CYCLE_APNUM = 10;
	
	private static final long MILI_SECONDS = 1000;
	
	SystemInfoQueryImpl systemInfoQueryImpl =new SystemInfoQueryImpl();
	public List<?> executeQuery(String sql){
		return DBOperationUtil.executeQuery(sql);
	}
	
	public String getVendorByMac(String mac){
		return AhConstantUtil.getMacOuiComName(mac);
	}
	
	
	public String getOSName(long domainId,String clientOSinfo){
		String s= BeOsInfoProcessor.getInstance().getOsName(domainId, clientOSinfo);
		if( s==null){
			return "unknown";
		}
		return s;
	}
	public String getClientAuthMethodString(byte clientAuthMethod){
		switch (clientAuthMethod) {
		case AhAssociation.CLIENTAUTHMETHOD_OPEN:
		case AhAssociation.CLIENTAUTHMETHOD_WEPOPEN:
		case AhAssociation.CLIENTAUTHMETHOD_WEPSHARED:
		case AhAssociation.CLIENTAUTHMETHOD_WPAPSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPA2PSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPA8021X:
		case AhAssociation.CLIENTAUTHMETHOD_WPA28021X:
		case AhAssociation.CLIENTAUTHMETHOD_WPAAUTOPSK:
		case AhAssociation.CLIENTAUTHMETHOD_WPAAUTO8021X:
		case AhAssociation.CLIENTAUTHMETHOD_DYNAMICWEP:
		case AhAssociation.CLIENTAUTHMETHOD_8021X:
	         return MgrUtil.getEnumString("enum.snmp.association.authentication." + clientAuthMethod);
		default:
	         return "Unknown";
		}
		
	}	
	
	public String getClientMacPtlString(byte clientMACProtocol) {
		if (clientMACProtocol<=0) {
			return "Ethernet";
		}
		return AhEvent.getClientMacProtocolString( clientMACProtocol );
	}
	
        public String getLatestAuditString( int statusType ) {
        	switch ( statusType ) {
        	case STATUS_SUCCESS:
        	    return "SUCCESS";
        	case STATUS_FAILURE:
        	    return "FAILURE";
        	case STATUS_EXECUTE:
        	    return "EXECUTED";
        	default:
        	    return "N/A";
        	}
        }
	
	public SystemInfoProvider getSystemInfoProvider(){
		return systemInfoQueryImpl;
	}
	
	public String getSeverityString(int severity){
	    if (severity < AhAlarm.AH_SEVERITY_UNDETERMINED || severity > AhAlarm.AH_SEVERITY_CRITICAL) {
		return "";
	} else {
		return MgrUtil.getEnumString("enum.severity." + severity);
	}
	}
	
	public List<Map> getActiveUsers(){
		List<Map> users = new ArrayList<Map>();
		for (HttpSession activeUser : CurrentUserCache.getInstance().getActiveSessions()) {
			try {
				HmUser sessionUser = (HmUser) activeUser.getAttribute(USER_CONTEXT);
				if (sessionUser != null) {
					Map user = new HashMap();
					user.put(LoginListAxis.IP, sessionUser.getUserIpAddress());
					user.put(LoginListAxis.USERNAME, sessionUser.getUserName());
					user.put(LoginListAxis.TIME, activeUser.getCreationTime());
					user.put(LoginListAxis.SECONDS, (System.currentTimeMillis()-activeUser.getCreationTime())/1000);
					user.put("@bkValue", activeUser.getId( ));
					users.add(user);
				}
				
			}catch(Exception e){
				//write log
			}
		}
		return users;		
	}
	
	public long getAPUploadDataTimeMiliSeconds(){
	    // default use 1 minute
	    long retMiliSeconds = MILI_SECONDS*1;
	    //get polling numbers per second
	    CYCLE_APNUM = Byte.parseByte(ConfigUtil.getConfigInfo(
			ConfigUtil.SECTION_PERFORMANCE,
			ConfigUtil.KEY_POLLING_DEVICE_NUMBER_PER_SEC, "10"));
	    //get total APs number
	    List<SimpleHiveAp> apList = CacheMgmt.getInstance()
			.getManagedApList();
	    long totalAPNum = apList.size( );
	    //calculate the seconds when total AP upload data  
	    if(CYCLE_APNUM > 0 && totalAPNum >CYCLE_APNUM){
		retMiliSeconds = (totalAPNum/CYCLE_APNUM ) * MILI_SECONDS;
	    }
	    return retMiliSeconds;
	}
	
}
