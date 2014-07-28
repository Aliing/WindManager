package com.ah.be.performance.appreport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.httpclient.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.common.ConfigUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.appreport.BeApplicationFlowGatherProcessor.AppFlowGatherThread;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.HibernateUtil;


public class BeCopServerSendDataProcessor {
	
	private static final int RETRY_COUNT = 3;
	
	private static final int RETRY_WAITING_TIME = 60 * 1000; //1 minute
		
	private boolean isContinue = true;
	
	private ScheduledExecutorService scheduler;
					
	public void shutDown () {
		isContinue = false;
		if (scheduler == null || !scheduler.isShutdown()) {
			scheduler.shutdown();
		}
		BeLogTools.info(HmLogConst.M_PERFORMANCE, "BeCopServerSendDataProcessor execute shutDown and will stoping to sending app datas to copserver");
	}
	
	public void startTask () {
		isContinue = true;
		if (scheduler == null || scheduler.isShutdown()) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			String intervalStr = ConfigUtil.getConfigInfo("vineyard_cop_server", "copserver_interval_day", "7"); 
			int interval = 0;
			try {
				interval = Integer.parseInt(intervalStr);
			} catch(NumberFormatException e) {
				interval = 7;
			}
			scheduler.scheduleWithFixedDelay(new SendAppDataThread(), 1, interval, TimeUnit.DAYS);
		}
		
	}
	
	public void startTaskForDebug() {
		isContinue = true;
		SendAppDataThread thread = new SendAppDataThread();
		thread.start();
	}
	
	public class SendAppDataThread extends Thread {
		private String copserverUrl;
		private String getUrl;
		private String postUrl;
		
		public SendAppDataThread() {
			copserverUrl = ConfigUtil.getConfigInfo("vineyard_cop_server", "copserver_url", "https://copserver.aerohive.com:4430");
			getUrl = copserverUrl + "/echo?arg=value";
			postUrl = copserverUrl + "/savedata";
		}
				
		public void run() {
			if (isContinue) {
				try {
					BeLogTools.warn(HmLogConst.M_PERFORMANCE, "[BeCopServerSendDataProcessor] starting collect app data and send to CopServer");
					String owners = findEnableCollectAppDataOwners();
					if (owners == null) {
						BeLogTools.error(HmLogConst.M_PERFORMANCE, "[BeCopServerSendDataProcessor] Don't send app datas for these is no vhm enable collect app data.");
						return;
					}
					JSONObject jsonData = getAppJsonDatas(owners);
					HttpClient client = new HttpClient();
					client.getHttpConnectionManager().getParams().setConnectionTimeout(10 * 1000);
					client.getHttpConnectionManager().getParams().setSoTimeout(10 * 1000);
					boolean sendFlag = false;
					for (int i = 0; i < RETRY_COUNT; i++) {
						ResponseModel response = HttpToolkit.doGet(getUrl, null, client);
						if (response == null || response.getResponseCode() != 200) {
							BeLogTools.error(HmLogConst.M_PERFORMANCE, "[BeCopServerSendDataProcessor] can't visit copserver url, pls check copserver address.");
						}
						else {
							response = HttpToolkit.doPostJson(postUrl, jsonData.toString(), client);
							if (response != null && response.getResponseCode() == 200) {
								sendFlag = true;
								break;
							}
						}
						if (!isContinue) {
							throw new Exception("BeCopServerSendDataProcessor has been shutDown. exit send app data thread");
						}
						Thread.sleep(RETRY_WAITING_TIME);
					}
					
					recordSendLog(sendFlag, jsonData);
									
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "BeCopServerSendDataProcessor execute error, error msg = " + e.getMessage(), e);
				}
			}
		
	    }
	
	}
	
	private void recordSendLog(boolean sendFlag, JSONObject jsonData) throws Exception {
		String startTime = (String) jsonData.get("start_time");
		String endTime = (String) jsonData.get("end_time");
		HmSystemLog systemLog = new HmSystemLog();
		systemLog.setLevel(HmSystemLog.LEVEL_MINOR);
		systemLog.setSource("Vineyard COP Server");
		String msg = String.format("send app datas [from %s to %s] to cop server %s", startTime, endTime, sendFlag ? "successfully" : "failured");
		systemLog.setSystemComment(msg);
		HmDomain homeDomain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN);
		systemLog.setOwner(homeDomain);
		systemLog.setLogTimeStamp(System.currentTimeMillis());
		//QueryUtil.createBo(systemLog);
		BeLogTools.error(HmLogConst.M_PERFORMANCE, msg);
		
	}
	
	private JSONObject getAppJsonDatas(String owners) throws Exception {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		
		String endTimeStr = df.format(new Date());
		c.setTime(df.parse(endTimeStr));
		long endTime = c.getTimeInMillis();
		
		c.add(Calendar.DATE, -7);
		long startTime = c.getTimeInMillis();
	    String startTimeStr = df.format(c.getTime());
	    
	    startTimeStr += " 00:00:00";
	    endTimeStr += " 00:00:00";
	    
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("vendor_id", "Aerohive Networks, Inc.");
		jsonObject.put("device_type", "Aerohive HiveOS Device");
		jsonObject.put("start_time", startTimeStr);
		jsonObject.put("end_time", endTimeStr);
		
		String sql = "select appname, sum(bytes) sum1, sum(0) sum2 " +
				" from hm_repo_app_data_date t1 inner join application t2 on t1.application = t2.appcode " +
				" where t1.timestamp >= " + startTime + " and t1.timestamp < " + endTime +
				" and t1.owner in " + owners +
				" group by appname";
		
//		String sql = "select appname, (sum(rx24bytecount) + sum(rx5bytecount) + sum(rxwiredbytecount) + sum(tx24bytecount) + sum(tx5bytecount) + sum(txwiredbytecount)) sum1, " +
//				" (sum(rx24framecount) + sum(rx5framecount) + sum(rxwiredframecount) + sum(tx24framecount) + sum(tx5framecount) + sum(txwiredframecount)) sum2 " +
//				" from hm_repo_app_data_date t1 inner join application t2 on t1.application = t2.appcode " +
//				" where t1.timestamp >= " + startTime + " and t1.timestamp < " + endTime +
//				" and t1.owner in " + owners +
//				" group by appname";
		
		//System.out.println(sql);
		BeLogTools.warn(HmLogConst.M_PERFORMANCE, "BeCopServerSendDataProcessor fetch hm side application data sql:" + sql);
	
		JSONArray array = new JSONArray();
		List<?> list = QueryUtil.executeNativeQuery(sql);
		for (Object object : list) {
			Object[] objects = (Object[]) object;
			JSONObject appObj = new JSONObject();
			appObj.put("application_id", (String) objects[0]);
			appObj.put("bytes", ((Number) objects[1]).longValue());
			//appObj.put("flows", 0);
			appObj.put("packets", ((Number) objects[2]).longValue());
			array.put(appObj);
		}
		jsonObject.put("application_data", array);
		return jsonObject;
	}
	
	private String findEnableCollectAppDataOwners() {
		List<HMServicesSettings> list = QueryUtil.executeQuery(HMServicesSettings.class, null, new FilterParams("enableCollectAppData = :s1", new Object[] {true}));
		if (list == null || list.size() == 0) {
			return null;
		}
		StringBuffer sb = new StringBuffer("(");
		for (int i = 0; i < list.size(); i++) {
			HMServicesSettings setting = list.get(i);
			if (i == (list.size() - 1)) {
				sb.append(setting.getOwner().getId() + ")");
			} else {
				sb.append(setting.getOwner().getId() + ",");
			}
		}
		return sb.toString();
	}

	
	public static void main(String[] args) throws Exception {
		HibernateUtil.init(false);
//		insert into hm_repo_app_data_date (application, timestamp, owner, seconds, vlan, rx24bytecount,rx5bytecount, rxwiredbytecount,tx24bytecount,tx5bytecount,txwiredbytecount,
//		rx24framecount,rx5framecount,rxwiredframecount,tx24framecount,tx5framecount,txwiredframecount)
//		VALUES (2, '1350489600000', 2, 3600, 1,  1,1,1,1,1,1,1,1,1,1,1,1);
		BeCopServerSendDataProcessor processor = new BeCopServerSendDataProcessor();
		processor.startTask();
		Thread.sleep(10000);
		processor.shutDown();
	}

}