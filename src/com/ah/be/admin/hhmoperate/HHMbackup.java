package com.ah.be.admin.hhmoperate;

import java.io.File;
import java.util.List;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.DebugUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.os.FileManager;
import com.ah.bo.admin.HmDomain;
import com.ah.util.NetTool;

public class HHMbackup {
	
	public static final String NEED_LICENSE_FLAG_FILE = "need_backup_domain_licnese";
	
	public static BackupInfo backupHHM(HmDomain oDomain, int iContent, boolean isNeedLicense)
	{		
		BackupInfo oReturnInfo = new BackupInfo();
		
		long lTmp = System.currentTimeMillis();
		
		String strTmp = String.valueOf(lTmp);
		
		String Host = NetTool.getHostName();
		
		String strFileName = "backup_"+Host+"_"+oDomain.getDomainName()+"_"+strTmp+".tar.gz";
		
		String strBackupHome = HHMConstant.BACKUP_DOWNLOAD_HOME+"/"+oDomain.getDomainName();
		
		DebugUtil.adminDebugInfo("HHMmove:: transmit the backup data begin...");
		DebugUtil.adminDebugInfo("HHMmove:: db_package_name: " + strFileName + " db_package_path: " + strBackupHome);
		
		File oFile = new File(strBackupHome);
		
		try
		{
			if(oFile.exists())
			{
				if(oFile.isDirectory())
				{
					FileManager.getInstance().deleteDirectory(strBackupHome);
				}
				else
				{
					FileManager.getInstance().deletefile(strBackupHome);
				}
			}
			
			oFile.mkdirs();
			
			String strBackupTmp = strBackupHome+"/"+strTmp;
			
			oFile = new File(strBackupTmp);
			
			oFile.mkdirs();
			
			//if need backup license touch a flag file
			if(isNeedLicense)
			{
				//create the flag file
				String strFlagFile = strBackupTmp +"/"+ NEED_LICENSE_FLAG_FILE;
				
				oFile = new File(strFlagFile);
				oFile.createNewFile();
			}
			
			if(backupHHMData(oDomain.getId(),oDomain.getDomainName(),
					       iContent,strBackupTmp,strFileName))
			{
				oReturnInfo.setResult(true);
				oReturnInfo.setFilePath(strBackupTmp);
				oReturnInfo.setFileName(strFileName);
			}
			else
			{
				oReturnInfo.setResult(false);
				oReturnInfo.setErrorMsg("backup up have some error!");
			}
			
			File tarFile = new File(strBackupTmp + "/" + strFileName);
			if(!tarFile.exists()){
				oReturnInfo.setResult(false);
				oReturnInfo.setErrorMsg("backup up have some error!");
				BeLogTools.debug(HmLogConst.M_COMMON,"HHMmove: backup up have some error!");
			}
		}
		catch(Exception ex)
		{
			oReturnInfo.setResult(false);
			oReturnInfo.setErrorMsg(ex.getMessage());
			//add log
			DebugUtil.adminDebugWarn("HHMbackup::backupHHM, "+ex.getMessage());
		}
		
		DebugUtil.adminDebugInfo("HHMmove:: transmit the backup data end...");
		return oReturnInfo;								
	}
	
	private static boolean backupHHMData(long ldomainid,String strDomainName,
			               int icontent, String strPath, String strFileName)
	{		
		String strShFile = "backupHHMDomainData.sh";
		
		String[] strCmds={"sh",BeAdminCentOSTools.ahShellRoot+ "/"+strShFile,
				          String.valueOf(ldomainid),strDomainName,String.valueOf(icontent),
				          strPath, strFileName};
		 
		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmds);
	     
	    if(null == strRsltList || 0 == strRsltList.size())
    	{
        	//add log
		    DebugUtil.adminDebugWarn("HHMbackup::backupHHMData, " +
		    		"exec backupHHMDomainData.sh cannot get return list");
		    return false;
    	}
        
        String strRslt = strRsltList.get(0);
        
        int iRslt;
    	
    	try
    	{    	
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		//add log
    		DebugUtil.adminDebugWarn("HHMbackup::backupHHMData, " +
    		"exec backupHHMDomainData.sh cannot get right formate list.");
    		
    		return false;
    	}    	
    	
    	if(0 != iRslt )
    	{    		
    		if( 2 <= strRsltList.size())
    		{
    			//add log
        		DebugUtil.adminDebugWarn("HHMbackup::backupHHMData, " +
        		"exec backupHHMDomainData.sh cannot get right size list."+strRsltList.get(1));
        		
    		} 	
    		
    		return false;
    	}			
	
		return true;
	}

}
