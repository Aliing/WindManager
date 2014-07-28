package com.ah.be.config.create.source.impl;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.AhDayLightSavingUtil;
import com.ah.be.config.create.CLICommonFunc;
import com.ah.be.config.create.source.ClockProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.useraccess.MgmtServiceTime;
import com.ah.util.MgrUtil;

/**
 * 
 * @author zhang
 * 
 */
public class ClockProfileImpl implements ClockProfileInt {

	private MgmtServiceTime mgmtServiceTime;

	private AhDayLightSavingUtil dayLightTime;

	private HiveAp hiveAp;

	public ClockProfileImpl(HiveAp hiveAp) {
		mgmtServiceTime = hiveAp.getConfigTemplate().getMgmtServiceTime();
		this.hiveAp = hiveAp;
	}
	
	public String getMgmtServiceTimeGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.mgmtServiceTime");
	}
	
	public String getMgmtServiceTimeName(){
		if(mgmtServiceTime != null){
			return mgmtServiceTime.getMgmtName();
		}else{
			return null;
		}
	}

	public String getApVersion() {
		return hiveAp.getSoftVer();
	}
	
	public String getUpdateTime() {
		List<Object> clockObj = new ArrayList<Object>();
		clockObj.add(mgmtServiceTime);
		clockObj.add(dayLightTime);
		return CLICommonFunc.getLastUpdateTime(clockObj);
	}
	
	public int getTimeZone() {
		if (mgmtServiceTime != null) {
			String timeZone = hiveAp.getDownloadInfo().getTimeZoneOffSet();
//			String timeZone = mgmtServiceTime.getTimeZoneOffSet();
			return Integer.valueOf(timeZone.substring(0, timeZone.indexOf(":")));
		} else {
			return 0;
		}
	}
	
	public String getTimeZoneMin(){
		if(mgmtServiceTime != null){
			String timeZone = hiveAp.getDownloadInfo().getTimeZoneOffSet();
//			String timeZone = mgmtServiceTime.getTimeZoneOffSet();
			String min = timeZone.substring(timeZone.indexOf(":")+1);
			if(Integer.valueOf(min) > 0){
				return min;
			}else{
				return "";
			}
		}else{
			return "";
		}
	}

	public boolean isConfigureDaylightTime() {
		if(mgmtServiceTime != null){
//			dayLightTime = new AhDayLightSavingUtil(mgmtServiceTime
//					.getTimeZoneString());
//			dayLightTime = new AhDayLightSavingUtil(hiveAp.getDownloadInfo().getTimeZoneString());
//			return dayLightTime.isUseDayLightSaving();
			return hiveAp.getDownloadInfo().isUseDayLightSaving();
		}else{
			return false;
		}
		
	}

	public String getDayLightTime() {
		return hiveAp.getDownloadInfo().getDayLightTime();
	}

}
