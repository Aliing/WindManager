package com.ah.be.admin.restoredb;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ah.be.admin.adminBackupUnit.AhBackupNewTool;
import com.ah.be.admin.adminOperateImpl.AhCleanLicenseHistory;
import com.ah.be.app.HmBeLicenseUtil;
import com.ah.be.license.AeroLicenseTimer;
import com.ah.be.license.HM_License;
import com.ah.be.log.BeLogTools;
import com.ah.bo.admin.DomainOrderKeyInfo;
import com.ah.bo.admin.LicenseHistoryInfo;
import com.ah.bo.admin.OrderHistoryInfo;
import com.ah.bo.admin.UserRegInfoForLs;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class AhCheckSystemId {
	
	private static final Tracer log = new Tracer(AhCleanLicenseHistory.class.getSimpleName());
	
	public AhCheckSystemId() {

	}
	
	public static void main(String[] args)
	{
		HM_License hm_license=HM_License.getInstance();
		String os_system_id=hm_license.get_system_id();
		String db_system_id=getDbSystemId();
		System.out.println("os:"+os_system_id);
		System.out.println("db:"+db_system_id);
		if(null!=os_system_id&&null!=db_system_id&&!os_system_id.equals(db_system_id)){
			try
			{
				log.warn("remove all license information begin");
				// license information
			    QueryUtil.bulkRemoveBos(LicenseHistoryInfo.class, null, null, null);
			    
			    // order key information
			    QueryUtil.removeBos(OrderHistoryInfo.class, null, null, null);
			    QueryUtil.bulkRemoveBos(DomainOrderKeyInfo.class, null, null, null);
			    QueryUtil.bulkRemoveBos(UserRegInfoForLs.class, null, null, null);
			    
			    // remove the values from memory
			    HmBeLicenseUtil.VHM_ORDERKEY_INFO.clear();
			    HmBeLicenseUtil.LICENSE_TIMER_OBJ.stopAllLicenseTimer();
			    HmBeLicenseUtil.LICENSE_TIMER_OBJ = new AeroLicenseTimer();
			    log.warn("remove all license information end");
			    System.out.println("success");
			}
			catch(Exception ex)
			{
				BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
			}
			
		}
		System.out.println("os:"+os_system_id);
		System.out.println("db:"+db_system_id);
	}
	
	private static String  getDbSystemId() {
		String result="";
		Connection con = null;

		ResultSet rsTable = null;

		Statement stTable = null;

		try {
			AhBackupNewTool oTool = new AhBackupNewTool();
			con = oTool.initCon();
			stTable = con.createStatement();
			String strSql;

			strSql = "select systemid from domain_order_key_info where domainName='home' and orderKey is not null";
			if(! AhBackupNewTool.isValidCoon(con))
			{
				con = oTool.initCon();
				stTable = con.createStatement();
			}			
			
			rsTable = stTable.executeQuery(strSql);
			
			while(rsTable.next()){
				if(rsTable.getString("systemid")!=null){
					result=rsTable.getString("systemid");
				}
			}
		} catch (Exception ex) {
			BeLogTools.restoreLog(BeLogTools.ERROR, ex.getMessage());
		}
		finally
		{
		    if(null != con)
		    {
		    	try
		    	{
		    	  con.close();
		    	}
		    	catch(Exception conex)
		    	{
		    	  BeLogTools.restoreLog(BeLogTools.ERROR, conex.getMessage());
		    	}
		    }
		    
		    if(null != rsTable)
		    {
		    	try
		    	{
		    		rsTable.close();
		    	}
		    	catch(Exception rsex)
		    	{
		    		BeLogTools.restoreLog(BeLogTools.ERROR, rsex.getMessage());
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
		    		BeLogTools.restoreLog(BeLogTools.ERROR, stex.getMessage());
		    	}
		    }
		}
		
		return result;
	}
	

}