package com.ah.be.performance.dataretention;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;

import com.ah.be.admin.adminBackupUnit.AhBackupNewTool;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;

public class NetworkDeviceConfigTracking {
	
	public enum InterfaceType{
		mgt0, mgt0_1, mgt0_2, mgt0_3, mgt0_4, mgt0_5, mgt0_6, mgt0_7, mgt0_8, mgt0_9,
		mgt0_10, mgt0_11, mgt0_12, mgt0_13, mgt0_14, mgt0_15, mgt0_16
	}

  /** Stores into <a hRef=HTTP://10.16.134.214/report/application/#context>table "NetworkDeviceHistory"</a>:
    null "endTimeStamp" of same "MAC" has to be updated w/ the new "beginTimeStamp",
    whose info including <a hRef=HTTP://10.16.134.214/report/application/#UserProfilesHistory>"UserProfilesHistory"</a>
    are copied over except for "MilliSeconds2GMT" &amp; "TopologyGroup".
  */
	public static void topologyChanged(Calendar timeStampWithDeviceTimeZone, Long vHMdomain,
			String deviceMAC,byte hours2GMT, long[] topologyGroupPkFromTopToBottom,String[] tags) {
		
		DeviceHistory deviceHistory = new DeviceHistory();
		deviceHistory.setTimeStampWithDeviceTimeZone(timeStampWithDeviceTimeZone);
		deviceHistory.setvHMdomain(vHMdomain);
		deviceHistory.setDeviceMAC(deviceMAC);
		deviceHistory.setHours2GMT(hours2GMT);
		deviceHistory.setTopologyGroupPkFromTopToBottom(topologyGroupPkFromTopToBottom);
		deviceHistory.setTags(tags);
		deviceHistory.setType(DeviceHistory.DEVICE_HISTORY_TOPOLOGY_CHANGE);
		NetworkDeviceHistoryProcessor.addEvent(deviceHistory);
		
	}
		
  /** Stores into <a hRef=HTTP://10.16.134.214/report/application/#context>table "NetworkDeviceHistory"</a>:
    null "endTimeStamp" of same "MAC" has to be updated w/ the new "beginTimeStamp",
    whose info including <a hRef=HTTP://10.16.134.214/report/application/#UserProfilesHistory>"UserProfilesHistory"</a>
    are copied over except for "MilliSeconds2GMT" &amp; "TopologyGroup" starting w/ topologyGroupPK.
  */
	public static void topologyGroupChanged(
			Calendar timeStampWithDevicesTimeZoneOrUtcIfDifferent, Object vHMdomain,
			long topologyGroupPK, long topologyContainerPK) {
		
		DeviceHistory deviceHistory = new DeviceHistory();
		deviceHistory.setTimeStampWithDeviceTimeZone(timeStampWithDevicesTimeZoneOrUtcIfDifferent);
		deviceHistory.setvHMdomain((Long)vHMdomain);
		deviceHistory.setTopologyGroupPK(topologyGroupPK);
		deviceHistory.setTopologyContainerPK(topologyContainerPK);
		deviceHistory.setType(DeviceHistory.DEVICE_HISTORY_TOPOLOGYGROUP_CHANGE);
		NetworkDeviceHistoryProcessor.addEvent(deviceHistory);
				
	}

  /** Stores into table <a hRef=HTTP://10.16.134.214/report/application/#context>"NetworkDeviceHistory"</a>
      &amp; <a hRef=HTTP://10.16.134.214/report/application/#UserProfilesHistory>"UserProfilesHistory"</a>:
    null "endTimeStamp" of same "MAC" has to be updated w/ the new "beginTimeStamp",
    whose "MilliSeconds2GMT" &amp; "TopologyGroup" are copied over.
    {@link InterfaceType} {@link InterfaceType#mgt0_1}-{@link InterfaceType#mgt0_16} store as "vLAN"[1-16],
    and {@link InterfaceType#mgt0} stores as "vLAN"[0].
  */
	public static void policyChanged(Calendar timeStampWithDeviceTimeZone, Object vHMdomain,
			String deviceMAC, boolean wiFi0is5GHz, long networkPolicyPK,
			String[] SSIDs, long[] userProfilePK, VlanObj[] vLanId ,int milliSeconds2GMT ,String[] tags) {
		
		DeviceHistory deviceHistory = new DeviceHistory();
		deviceHistory.setTimeStampWithDeviceTimeZone(timeStampWithDeviceTimeZone);
		deviceHistory.setvHMdomain((Long)vHMdomain);
		deviceHistory.setDeviceMAC(deviceMAC);
		deviceHistory.setWiFi0is5GHz(wiFi0is5GHz);
		deviceHistory.setNetworkPolicyPK(networkPolicyPK);
		deviceHistory.setSSIDs(SSIDs);
		deviceHistory.setUserProfilePK(userProfilePK);
		deviceHistory.setvLanId(vLanId);
		deviceHistory.setMilliSeconds2GMT(milliSeconds2GMT);
		deviceHistory.setTags(tags);
		deviceHistory.setType(DeviceHistory.DEVICE_HISTORY_POLICY_CHANGE);
		NetworkDeviceHistoryProcessor.addEvent(deviceHistory);
		
	}
	/**
	 * 
	 * Description: device tags changed
	 * Date:Jan 21, 2013
	 * @author Shaohua Zhou
	 * @param deviceMAC
	 * @param tags
	 * @return void
	 */
	public static void tagsChanged(String[] tags, String deviceMAC) {
		
		DeviceHistory deviceHistory = new DeviceHistory();
		deviceHistory.setDeviceMAC(deviceMAC);
		deviceHistory.setTags(tags);
		deviceHistory.setType(DeviceHistory.DEVICE_HISTORY_TAGS_CHANGE);
		NetworkDeviceHistoryProcessor.addEvent(deviceHistory);
		
	}

	public static Long[] reverseArray(Long[] array){
		if(null == array || array.length == 0)
			return null;
		
		Long[] temp = new Long[array.length];;
		if(temp.length > 0){
			for(int i=0, j= array.length -1; i <= j; i++, j--){
				temp[i] = array[j];
				temp[j] = array[i];
			}
		}
		return temp;
	}
	
	public static Array arrayCovert(Object[] object,Connection connection){
		Array param = null;
		if (object instanceof java.lang.String[]){
			if (object.length > 0) {
				try {
					param = connection.createArrayOf("varchar",object);
				} catch (SQLException e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"varchar array covert failure: "+e.getMessage(),e);
					param = null;
					return param;
				}
		    }
		}else if(object instanceof Long[]){
			if (object.length > 0) {
				try {
					param = connection.createArrayOf("bigint",object);
				} catch (SQLException e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"bigint array covert failure: "+e.getMessage(),e);
					param = null;
					return param;
				}
		    }
		}else if(object instanceof VlanObj[]){
			VlanObj[] obj =( VlanObj[]) object ;
			Short[] temp = new Short[obj.length];
	    	if (obj.length > 0) {
	    		for(int i=0; i<obj.length; i++){
	    			temp[i] = (short)obj[i].getVlan();
	    		}
	    		try {
					param = connection.createArrayOf("smallint",temp);
				} catch (SQLException e) {
					BeLogTools.error(HmLogConst.M_RESTORE,"smallint array covert failure: "+e.getMessage(),e);
					param = null;
					return param;
				}
		    }
		}
		return param;
	}
	
	
	public static Long[] rsToLongArray(ResultSet rs ) throws Exception{ 
		Long[] relong = null;
		int rowcount=0;
		if ( null != rs){
			rs.last();
			rowcount =rs.getRow();
			relong = new Long[rowcount];
			rs.beforeFirst();
			while (rs.next()) {
				relong[rs.getRow()-1]=rs.getLong(1);
		    }
		}
		return relong;
	}
	
	private static final String db_driver         = "org.postgresql.Driver";
	private static        String db_url            = "jdbc:postgresql://localhost:5432/hm";
	private static  String db_usr            = "hivemanager";
	private static  String db_psd            = "aerohive";
	
	public static Connection initConn()
	{
		String strCfgFile = System.getenv("HM_ROOT")+"/WEB-INF/classes/hibernate.cfg.xml";
		db_url = AhBackupNewTool.getUrlFromFile(strCfgFile);
		String strProfile = System.getenv("HM_ROOT")+"/WEB-INF/classes/resources/hmConfig.properties";
		String[] strUserPsd = AhBackupNewTool.getDBUserAndPsd(strProfile);
		db_usr = strUserPsd[0];
		db_psd = strUserPsd[1];
		Connection con = null;

		try {
			Class.forName(db_driver).newInstance();
			Properties pros = new Properties();
			pros.setProperty("user", db_usr);
			pros.setProperty("password", db_psd);
			pros.setProperty("tcpKeepAlive", "true");
			
			con = DriverManager
			.getConnection(db_url, pros);
			
		} catch (Exception e1) {
			BeLogTools.error(HmLogConst.M_RESTORE, "Migration get the database connection failure: "+e1.getMessage(),e1);
			BeLogTools.error(HmLogConst.M_PERFORMANCE,e1);
		}

		return con;
	}
	
	public static void  free(ResultSet rsTable, Statement stTable)
	{

	    if(null != rsTable)
	    {
	    	try
	    	{
	    		rsTable.close();
	    	}
	    	catch(Exception rsex)
	    	{
	    		BeLogTools.error(HmLogConst.M_PERFORMANCE, rsex.getMessage());
	    	}
	    }

	    if(null != stTable)
	    {
	    	try
	    	{
	    		stTable.close();
	    	}
	    	catch(Exception stex)
	    	{
	    		BeLogTools.error(HmLogConst.M_PERFORMANCE, stex.getMessage(),stex);
	    	}
	    }
	}
	
	public static void  freePSMT(PreparedStatement stTable)
	{

	    if(null != stTable)
	    {
	    	try
	    	{
	    		stTable.close();
	    	}
	    	catch(Exception stex)
	    	{
	    		BeLogTools.error(HmLogConst.M_PERFORMANCE, stex.getMessage(),stex);
	    	}
	    }
	}
	
	public static void freeCon(Connection con)
	{
		try {
			if(null != con)
				con.close();
		} catch (Exception ex) {
			BeLogTools.error(HmLogConst.M_PERFORMANCE,ex);
		}
	}
	
	public static boolean isValidCoon(Connection con)
	{
		try
		{
			Statement stTable = con.createStatement();
			stTable.close();
			return true;
		}
		catch(Exception ex)
		{
			BeLogTools.error(HmLogConst.M_PERFORMANCE, ex.getMessage(),ex);
			return false;
		}
	}
	/**
	 * 
	 * Description: check database table whether exist.
	 * Date:Sep 28, 2012
	 * @author Shaohua Zhou
	 * @return boolean
	 * @return boolean
	 */
	public static boolean checkDBTableExist(String tablename){
		Connection db = null;
		Statement st = null;
		boolean boo = false;
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String checkSql = "/*NO LOAD BALANCE*/select tablename from pg_tables where "
					+ "tableowner='hivemanager' and schemaname='public' " +
					"and tablename ='" + tablename + "'";
			db = QueryUtil.getConnection();
			st = db.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ResultSet rs = st.executeQuery(checkSql);
			boo = rs.last();
		} catch (SQLException e) {
			BeLogTools.error(HmLogConst.M_RESTORE, "check table failed!", e);
			BeLogTools.debug(HmLogConst.M_PERFORMANCE, "check table failed!");
		} finally{
			try {
				st.close();
			}catch(Exception stex)
	    	{
	    		BeLogTools.debug(HmLogConst.M_PERFORMANCE, stex.getMessage(),stex);
	    	}
			try {
				db.close();
			} catch(Exception stex)
	    	{
	    		BeLogTools.debug(HmLogConst.M_PERFORMANCE, stex.getMessage(),stex);
	    	}
		}
		return boo;
	}
	
	
	public static class VlanObj{
		
		private InterfaceType intVlan;
		
		private int vlan;
		
		public VlanObj(){
			
		}
		
		public VlanObj(InterfaceType intVlan, int vlan){
			this.intVlan = intVlan;
			this.vlan = vlan;
		}

		public InterfaceType getIntVlan() {
			return intVlan;
		}

		public void setIntVlan(InterfaceType intVlan) {
			this.intVlan = intVlan;
		}

		public int getVlan() {
			return vlan;
		}

		public void setVlan(int vlan) {
			this.vlan = vlan;
		}
	}
	
	
}
