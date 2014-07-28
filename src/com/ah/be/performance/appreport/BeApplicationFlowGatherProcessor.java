package com.ah.be.performance.appreport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.common.cache.AppFlowCacheMgmt;
import com.ah.be.common.db.BulkUpdateUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhAppFlowDay;
import com.ah.bo.performance.AhAppFlowLog;
import com.ah.bo.performance.AhAppFlowMonth;
import com.ah.util.HibernateUtil;


public class BeApplicationFlowGatherProcessor {
			
	private boolean isContinue = true;
	
	private ScheduledExecutorService scheduler;
					
	public void shutDown () {
		isContinue = false;
		if (scheduler == null || !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeApplicationFlowGatherProcessor execute shutDown");
	}
	
	public void startTask () {
		isContinue = true;
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(new AppFlowGatherThread(), AppFlowHelper.getAppFlowGatherDelaySeconds(), AppFlowHelper.getAppFlowGatherPeriodSeconds(), TimeUnit.SECONDS);
		}
		
	}
	
	public void startTaskForDebug() {
		isContinue = true;
		AppFlowGatherThread thread = new AppFlowGatherThread();
		thread.start();
	}
	
	public class AppFlowGatherThread extends Thread {				
		public void run() {
			if (isContinue) {
				try {
					long timestamp = System.currentTimeMillis();
					List<AhAppFlowDay> appFlowDayList = getLastAppDayList(timestamp);
					//QueryUtil.bulkCreateBos(appFlowDayList);
					BulkUpdateUtil.bulkInsert(AhAppFlowDay.class, appFlowDayList);
					saveLastAppMonthList(timestamp);
					AppFlowCacheMgmt.getInstance().cleanFlowData();
					BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeApplicationFlowGatherProcessor execute success...... ");				
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeApplicationFlowGatherProcessor execute error, error msg = " + e.getMessage(), e);
				}
			}
		
	    }
	
	}
	
	private void saveLastAppMonthList(long timestamp) throws Exception {
		QueryUtil.executeNativeUpdate("truncate table ah_app_flow_month");
		
		List<AhAppFlowMonth> createList = new ArrayList<AhAppFlowMonth>();
		//List<AhAppFlowMonth> updateList = new ArrayList<AhAppFlowMonth>();
		//Object[] result = new Object[] {createList, updateList};
		
		Calendar c = Calendar.getInstance();
		long endTime = c.getTimeInMillis();
		c.add(Calendar.DATE, -30);
		long startTime = c.getTimeInMillis();
		
		String sql = "select owner, appcode, sum(bytenum) sum1, sum(packetnum) sum2  from ah_app_flow_day " +
	             " where created_at >= " + startTime + " and created_at < " + endTime + 
                 " group by owner, appcode";
	
		List<?> list = QueryUtil.executeNativeQuery(sql);
		for (Object object : list) {
			Object[] objects = (Object[]) object;
			//boolean isCreate = false;
			Long ownerId = ((Number) objects[0]).longValue();
			HmDomain owner = QueryUtil.findBoById(HmDomain.class, ownerId);
			AhAppFlowMonth bean = new AhAppFlowMonth();
			bean.setOwner(owner);
			bean.setAppCode(((Number) objects[1]).intValue());
			bean.setByteNum(((Number) objects[2]).longValue());
			bean.setPacketNum(((Number) objects[3]).longValue());
			bean.setCreatedAt(timestamp);
			createList.add(bean);
		}
		QueryUtil.bulkCreateBos(createList);
	}
	
	private List<AhAppFlowDay> getLastAppDayList(long timestamp) throws Exception {
		List<AhAppFlowDay> resultList = new ArrayList<AhAppFlowDay>();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar c = Calendar.getInstance();
		
		String endTimeStr = df.format(new Date());
		c.setTime(df.parse(endTimeStr));
		long endTime = c.getTimeInMillis();
		
		c.add(Calendar.DATE, -1);
		long startTime = c.getTimeInMillis();
		
		String sql = "select owner, appcode, sum(bytenum) sum1, sum(packetnum) sum2 from ah_app_flow_log " +
		             " where timestamp >= " + startTime + " and timestamp < " + endTime + 
                     " group by owner,appcode";
//		List<?> list = QueryUtil.executeNativeQuery(sql);
//		for (Object object : list) {
//			AhAppFlowDay bean = new AhAppFlowDay();
//			Object[] objects = (Object[]) object;
//			long ownerId = ((Number) objects[0]).longValue();
//			HmDomain owner = QueryUtil.findBoById(HmDomain.class, ownerId);
//			bean.setOwner(owner);
//			bean.setAppCode(((Number) objects[1]).intValue());
//			bean.setByteNum(((Number) objects[2]).longValue());
//			bean.setPacketNum(((Number) objects[3]).longValue());
//			bean.setCreatedAt(timestamp);
//			resultList.add(bean);
//		}
		
		Map<Long, Map<Integer, AhAppFlowLog>> flowDataMap = AppFlowCacheMgmt.getInstance().getAllFlowData();
		for (Iterator<Long> iter = flowDataMap.keySet().iterator(); iter.hasNext();) {
			Long ownerId = iter.next();
			Map<Integer, AhAppFlowLog> singleVhmMap = flowDataMap.get(ownerId);
			HmDomain owner = QueryUtil.findBoById(HmDomain.class, ownerId);
			for (Iterator<Integer> inner = singleVhmMap.keySet().iterator(); inner.hasNext();) {
				Integer appCode = inner.next();
				AhAppFlowLog log = singleVhmMap.get(appCode);
				AhAppFlowDay bean = new AhAppFlowDay();
				bean.setOwner(owner);
				bean.setAppCode(appCode);
				bean.setByteNum(log.getBytes());
				bean.setPacketNum(log.getPackets());
				bean.setCreatedAt(timestamp);
				resultList.add(bean);
				
			}
		}
		return resultList;
	}
	
	
	public static void main(String[] args) throws Exception {
		HibernateUtil.init(false);
//		String[] sqls = {
//				"insert into ah_app_flow_log (id,apmac,appcode,bytes,packets,TIMESTAMP,owner) values(1,'111111111111', 1, 100,100, 1357574400000, 2);",
//				"insert into ah_app_flow_log (id,apmac,appcode,bytes,packets,TIMESTAMP,owner) values(2,'111111111111', 1, 100,100, 1357574400000, 2);",
//				"insert into ah_app_flow_log (id,apmac,appcode,bytes,packets,TIMESTAMP,owner) values(3,'111111111111', 2, 100,100, 1357574400000, 2);",
//				"insert into ah_app_flow_log (id,apmac,appcode,bytes,packets,TIMESTAMP,owner) values(4,'222222222222', 1, 200,200, 1357574400000, 2);",
//				"insert into ah_app_flow_log (id,apmac,appcode,bytes,packets,TIMESTAMP,owner) values(5,'222222222222', 2, 200,200, 1357574400000, 2);"
//		};
		BeApplicationFlowGatherProcessor processor = new BeApplicationFlowGatherProcessor();
		processor.startTask();
	}

}