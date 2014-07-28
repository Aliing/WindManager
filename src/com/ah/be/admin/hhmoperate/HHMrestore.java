package com.ah.be.admin.hhmoperate;

import java.io.File;
import java.util.List;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.admin.adminOperateImpl.BeOperateHMCentOSImpl;
import com.ah.be.admin.restoredb.AhRestoreDBTools;
import com.ah.be.admin.restoredb.AhRestoreGetXML;
import com.ah.be.admin.restoredb.RestoreAdmin;
import com.ah.be.app.DebugUtil;
import com.ah.be.os.FileManager;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmStartConfig;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ws.rest.client.utils.DeviceImpUtils;

public class HHMrestore {
	
	public static String getUploadDir(String strDomainName)
	{
        long lTmp = System.currentTimeMillis();
		
		String strTmp = String.valueOf(lTmp);
		
        String strRestoreHome = HHMConstant.RESTORE_UPLOAD_HOME+"/"+strDomainName;
		
		File oFile = new File(strRestoreHome);
		
		try
		{
			if(oFile.exists())
			{
				if(oFile.isDirectory())
				{
					FileManager.getInstance().deleteDirectory(strRestoreHome);
				}
				else
				{
					FileManager.getInstance().deletefile(strRestoreHome);
				}
			}
			
			oFile.mkdirs();
			
			String strRestoreTmp = strRestoreHome+"/"+strTmp;
			
			oFile = new File(strRestoreTmp);
			
			oFile.mkdirs();
			
			return strRestoreTmp;
		}		
		catch(Exception ex)
		{
			//add log
			DebugUtil.adminDebugWarn("HHMrestore::getUploadDir, "+ex.getMessage());
			
			return "";
		}
	}
	
	public static String restoreData(String strPath, String strFileName, HmDomain oDomain)
	{
		File oFile = new File(strPath+"/"+strFileName);
		
		if(!oFile.exists()||!oFile.isFile())
		{
			DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
			"could not find restore tar file "+strPath+"/"+strFileName);
			
			return "Could not find restore tar file "+strPath+"/"+strFileName;
		}
		
		//unzip file
		String strShFile = "unzipRestoreHHMFile.sh";
		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/"+strShFile,
		                 strPath, strFileName};
		
		if(!BeOperateHMCentOSImpl.isRslt_0(strCmds))
		{
			//add log
			DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
					"run unzipRestoreHHMFile.sh have some error");
			return "Run unzipRestoreHHMFile.sh have some error";
		}
    	
    	String strRestoreDBHome = strPath+"/"+"dbxmlfile";
    	
    	String strVersionFile = strRestoreDBHome + "/"+ "hivemanager.ver";

        File oVersionFile = new File(strVersionFile);
        
        if(!oVersionFile.exists())
        {
        	//add log
			DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
					"the restore packet does not include hivemanager.ver file");
			return "The restore packet does not include hivemanager.ver file";
        }
        
		//do some charge
        String strDomainFile = strRestoreDBHome + "/"+ "hm_domain.xml".toLowerCase();
        File oDomainFile = new File(strDomainFile);
        if(!oDomainFile.exists())
        {
        	//add log
			DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
					"the restore packet does not include hm_domain.xml file");
			return "The restore packet does not include hm_domain.xml file";
        }
		
        String strRestoreDomainName = HHMrestore.getResoteDomainName(strRestoreDBHome);
        if ((null == strRestoreDomainName) || "".equalsIgnoreCase(strRestoreDomainName))
        {
        	//add log
			DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
					"the restore packet does not get the old VHM name");
			return "The restore packet does not get the old VHM name";
        }
        
        //get the src domain mode
        HmStartConfig oStartConfig = getSrcDomainMode(oDomain.getId());
        
        if(null != oStartConfig)
        {
        	if(oStartConfig.getModeType() == HmStartConfig.HM_MODE_EASY && oStartConfig.isAdminUserLogin())
            {
            	int iDestMode= getDestDomainModel(strRestoreDBHome+"/"+strRestoreDomainName+"/", oDomain);
            	
            	if (iDestMode == HmStartConfig.HM_MODE_FULL)
            	{
            		DebugUtil.adminDebugWarn("HHMrestore::restoreData, the vhm mode type is incorrect");
            		return "The VHM mode type is incorrect";
            	}
            }     
        } 
        
        try
        {
        	//remove domain data
            BoMgmt.getDomainMgmt().removeDomain(oDomain.getId(), false);
        }
        catch(Exception ex)
        {
        	//add log
        	DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
			"remove domain have some error", ex);
        	return "Remove domain have some error";
        }
				
		//restore domain
        String[] strCmdss={"sh",BeAdminCentOSTools.ahShellRoot+ "/restoreHHMDomainData.sh",
        		oDomain.getDomainName(),String.valueOf(oDomain.getId()),
        		strRestoreDomainName, strPath};
        
        
        if(!BeOperateHMCentOSImpl.isRslt_0(strCmdss))
		{
			//add log
			DebugUtil.adminDebugWarn("HHMrestore::restoreData, " +
					"run restoreHHMDomainData.sh have some error");
			return "Run restoreHHMDomainData.sh have some error";
		}
        
        // some additional operations will be taken after restore certain domain is successful, e.g. sync serail number with Redirector
        doAdditionalOperationsAfterDomainRestore(oDomain);
    	
        return "";
	}
	private static boolean doAdditionalOperationsAfterDomainRestore(HmDomain oDomain) {
		return DeviceImpUtils.getInstance().syncDeviceInventoriesWithRedirector(oDomain);
	}
	
	
	private static String getResoteDomainName(String strPath) {
		AhRestoreDBTools.HM_XML_TABLE_PATH = strPath+ File.separatorChar;

		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		boolean bRslt = xmlParser.readXMLFile("hm_domain");

		if (!bRslt) {
			DebugUtil.adminDebugError("could not read the file hm_domain.xml");

			return "";
		}

		String colName = "domainname";

		try {
			bRslt = xmlParser.checkColExist(colName);

			if (!bRslt) {
				DebugUtil
						.adminDebugError("could not find the field domainname in the file hm_domain.xml");

				return "";
			}

			return xmlParser.getColVal(0, colName);
		} catch (Exception ex) {
			DebugUtil.adminDebugError(
					"BeOperateHMCentOSImpl.isSameDomain is error", ex);

			return "";
		}
	}
	
	private static int getDestDomainModel(String strPath, HmDomain oDomain)
	{
		int iReturn = HmStartConfig.HM_MODE_FULL;
		
		//AhRestoreDBTools.HM_RESTORE_DOMAIN = oDomain;
		
		AhRestoreDBTools.HM_XML_TABLE_PATH = strPath+ File.separatorChar;
		
		List<HmStartConfig> HmConfs = RestoreAdmin.getHmStartConfigModel();
		
		if(null == HmConfs || HmConfs.isEmpty())
		{
			return iReturn;
		}	
		
		HmStartConfig hmConf = HmConfs.get(0);
	    iReturn = hmConf.getModeType();		
		return iReturn;		
	}
	
	private static HmStartConfig getSrcDomainMode(long lDomainid)
	{		
		List<HmStartConfig> lhmConf = QueryUtil.executeQuery(HmStartConfig.class, null, null, lDomainid);

		return lhmConf.isEmpty() ? null : lhmConf.get(0);
	}

}