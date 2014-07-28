package com.ah.be.performance.appreport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;

public class AppFlowHelper {
			
	public final static int APP_FLOW_REQUEST_START_HOUR = 2;
	
	public final static int	APP_FLOW_REQUEST_PERIOD_HOUR = 24;
	
	public final static int APP_FLOW_GATHER_START_HOUR = 4;
	
	public final static int	APP_FLOW_GATHER_PERIOD_HOUR = 24;  
	
	//according every vhm's timezone, the request time is 00:01
	public static List<Long> getRequestVhmIdList(boolean debugFlag) {
		List<Long> list = new ArrayList<Long>();
		List<HmDomain> allList = QueryUtil.executeQuery(HmDomain.class, null, null);
		if (debugFlag) {
			for (HmDomain owner : allList) {
				list.add(owner.getId());
			}
			return list;
		}
		Calendar c = Calendar.getInstance();
		for (HmDomain owner : allList) {
			c.setTimeZone(owner.getTimeZone());
			if (c.get(Calendar.HOUR_OF_DAY) == 0) {
				list.add(owner.getId());
			}
		}
		return list;
	}
	
	public static long convertAppFlowTimeStamp(long timestamp) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timestamp);
		c.add(Calendar.HOUR_OF_DAY, -24);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	public static int getAppFlowRequestDelaySeconds() {
		Calendar c = Calendar.getInstance();
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		if (hour <= APP_FLOW_REQUEST_START_HOUR) {
//			return (APP_FLOW_REQUEST_START_HOUR - hour) * 3600;
//		} else {
//			return (24 + APP_FLOW_REQUEST_START_HOUR - hour) * 3600;
//		}
		int minute = c.get(Calendar.MINUTE);
		if (minute <= 1) {
			return (1 - minute) * 60;
		} else {
			return (60 + 1 - minute) * 60;
		}
	}
	
	public static int getAppFlowRequestPeriodSeconds() {
		//return APP_FLOW_REQUEST_PERIOD_HOUR * 3600;
		return 3600; //1 hour
	}
	
	public static int getAppFlowGatherDelaySeconds() {
		Calendar c = Calendar.getInstance();
//		int hour = c.get(Calendar.HOUR_OF_DAY);
//		if (hour <= APP_FLOW_GATHER_START_HOUR) {
//			return (APP_FLOW_GATHER_START_HOUR - hour) * 3600;
//		} else {
//			return (24 + APP_FLOW_GATHER_START_HOUR - hour) * 3600;
//		}
		int minute = c.get(Calendar.MINUTE);
		return (59 - minute) * 60;
	}
	
	public static int getAppFlowGatherPeriodSeconds() {
		//return APP_FLOW_GATHER_PERIOD_HOUR * 3600;
		return 3600;
	}
	
}
