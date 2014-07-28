package com.ah.be.performance.appreport;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.ah.be.common.AhDirTools;
import com.ah.be.common.ConfigUtil;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.performance.AhClientSession;

public class ReportHelper implements AppReportConstants {
	
	private static boolean is_local_debug = false;
	
	private static boolean needBackupReportingFile = false;
	
	static {
		String enableReserverFile = ConfigUtil.getConfigInfo("performance", "enable_reserve_reporting_file");
		if (StringUtils.isNotBlank(enableReserverFile) && enableReserverFile.equals("1")) {
			needBackupReportingFile = true;
		} else {
			needBackupReportingFile = false;
		}
	}
		
	public static boolean handleReportingFile(File uploadFile) throws IOException {
		if (needBackupReportingFile) {
			File backupFile = new File(getBackupReportFilePath() + uploadFile.getName());
			FileUtils.copyFile(uploadFile, backupFile);
		} 
		return uploadFile.delete();		
	}
			
	public static long getOwnerIdByApMac(String apMac) {
		long ownerId = 0;
		try {
			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp != null) {
				ownerId = simpleHiveAp.getDomainId();
			}
		} catch(Exception e) {
			BeLogTools.debug(HmLogConst.M_PERFORMANCE, "get owner id error.");
		}
		return ownerId;
	}
	
	public static AhClientSession queryClientSession(String clientMac) {
		AhClientSession client = null;
		String sql = "select t.clientusername, t.userprofilename, t.clientvlan, t.clientssid, t.clienthostname," +
	                 "t.clientosinfo from ah_clientsession t where t.clientmac = '" + clientMac + "'";
		List<?> clientList = DBOperationUtil.executeQuery(sql, null, null);
		if (clientList != null && clientList.size() > 0) {
			Object[] object = (Object[]) clientList.get(0);
			client = new AhClientSession();
			client.setClientUsername((String) object[0]);
			//client.setClientUserProfId((int) object[1]);
			client.setUserProfileName((String) object[1]);
			client.setClientVLAN((int) object[2]);
			client.setClientSSID((String) object[3]);
			client.setClientHostname((String) object[4]);
			client.setClientOsInfo((String) object[5]);
		}
		return client;
	}
	
	public static String getReportFilePath() {
		return AhDirTools.getApplicationReportUploadDir();
	}
	
	public static String getBackupReportFilePath() {
		return AhDirTools.getApplicationReportBackupUploadDir();
	}
	
	public static short getReportTypeByFileName(String fileName) {
		short result = 0;
		if (fileName.endsWith(".hpr")) {
			result = FILE_TYPE_SECOND;
		}
		else if (fileName.endsWith(".lpr")) {
			result = FILE_TYPE_HOUR;
		}
		return result;
	}
	
	public static String getApMacByFileName(String fileName) {
		String result = "";
		if (fileName.length() > 12) {
			result = fileName.substring(0, 12);
		}
		return result;
	}
	
	public static String asString(String str) {
		if (StringUtils.isBlank(str)) {
			return UNKNOWN;
		}
		return str;
	}
	
	public static AppDataCollectorHandler getAppDataCollectorHandler(short fileType) {
		if (fileType == FILE_TYPE_HOUR) {
			return new AppHourDataCollectorHandler();
		}
		else if (fileType == FILE_TYPE_SECOND) {
			return new AppSecondDataCollectorHandler();
		}
		else {
			return new BaseAppDataCollectorHandler();
		}
	}
	
	public static int getReportMinute(long timestamp) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		c.setTimeInMillis(timestamp);
		return c.get(Calendar.MINUTE);
	}
	
	public static long getPureHourTime(long timestamp) {
		Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+0:00"));
		c.setTimeInMillis(timestamp);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTimeInMillis();
	}
	
	public static void mockData(boolean needsMockFile, boolean needsMockMemDB) {
		if (!is_local_debug) {
			return;
		}
		if (needsMockFile) {
			try {
				//FileUtils.copyFile(new File("d:/temp/FFEEDDCCBBAA0000.hpr"), new File("d:/temp/ap_upload_file/FFEEDDCCBBAA0000.hpr"));
				//FileUtils.copyFile(new File("d:/temp/FFEEDDCCBBAA0000.lpr"), new File("d:/temp/ap_upload_file/FFEEDDCCBBAA0000.lpr"));
				FileUtils.copyFile(new File("d:/temp/001977042F400009.hpr"), new File("d:/temp/ap_upload_file/001977042F400009.hpr"));
				
			} catch (IOException e) {
               
			}
		}
		if (needsMockMemDB) {
			//DBOperationUtil.init();
			String[] clientMacArray = new String[]{"112233445566", "112233445567", "112233445568", "112233445569","11223344556A",
												   "11223344556B", "11223344556C", "11223344556D", "11223344556E","11223344556F"};
			for (String clientMac : clientMacArray) {
//				DBOperationUtil.executeUpdate("insert into ah_clientsession(clientmac,clientusername,clientuserprofid, userprofilename,clientvlan,clientssid,clienthostname,clientosinfo," +
//				          "applicationhealthscore,bandwidthsentinelstatus,clientauthmethod,clientcwpused,clientChannel,clientencryptionmethod," +
//				          "clientmacprotocol,clientrssi,connectstate,endtimestamp,ifindex,ipnetworkconnectivityscore,overallclienthealthscore," + 
//				          "simulated, slaconnectscore,starttimestamp,wirelessclient,owner) " +
//				          "values('" + clientMac + "','shenglj',1,'profile1',1,'abc','4','windows',1,2,3,4,5,6,7,8,9,10,11,12,13,true,14,15,true,2)");
				ClientInfoBean bean = new ClientInfoBean();
				bean.setHostName("aaa");
				bean.setOsInfo("windows");
				bean.setProfileName("bbb");
				bean.setSsid("ccc");
				bean.setUserName("ddd");
				bean.setVlan(1);
				bean.setOnline(true);
				//bean.setTimeout(System.currentTimeMillis() + ReportCacheMgmt.TIMEOUT_SECOND);				
				ReportCacheMgmt.getInstance().saveClientInfo(clientMac, bean);
			}
		}
	}
	
	public static void setLocalDebug(boolean b) {
		is_local_debug = b;
	}
	
	public static void main(String[] args) {
		System.out.println(ReportHelper.getPureHourTime(System.currentTimeMillis()));
		System.out.println(ReportHelper.getReportMinute(System.currentTimeMillis()));
	}
	

}
