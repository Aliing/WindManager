package com.ah.be.admin.hhmoperate;

import java.io.File;

import com.ah.apiengine.agent.HmApiEngineException;
import com.ah.be.app.DebugUtil;
import com.ah.be.protocol.ssh.scp.AhScpMgmt;
import com.ah.be.protocol.ssh.scp.AhScpMgmtImpl;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;

public class HHMngineOperate {
	
    public static void backupForEgine(String strDomainName, String strSSHServer,
    		            int iSSHPport, String strSSHUsr, String strSSHPsd,String strSSHPath) throws HmApiEngineException
    {
    	//do backup
    	HmDomain oDomain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)",
    			strDomainName.toLowerCase());
    	if(null == oDomain)
    	{
    		throw new HmApiEngineException("There is not the domain with name is: "+strDomainName);
    	}

		BackupInfo oReturnInfo = HHMoperate.backupOperation(oDomain, 0, false);
    	
    	if(!oReturnInfo.getResult())
    	{
    		throw new HmApiEngineException(oReturnInfo.getErrorMsg());
    	}
    	
    	//do transfer through scp
    	AhScpMgmt fileTranser = null;
    	
    	try
    	{
    		fileTranser = new AhScpMgmtImpl(strSSHServer,iSSHPport,strSSHUsr,strSSHPsd);
    		
    		fileTranser.scpPut(oReturnInfo.getFilePath() + File.separator + oReturnInfo.getFileName(), strSSHPath);
    	}catch(Exception ex)
    	{
    		DebugUtil.adminDebugError(ex.getMessage());
    		
    		throw new HmApiEngineException(ex.getMessage());
    	}
        finally 
        {
			if (fileTranser != null) {
				fileTranser.close();
			}
	    }
    }
    
    public static void restoreForEgine(String strDomainName, String strSSHServer,
            int iSSHPport, String strSSHUsr, String strSSHPsd,String strSSHPath, 
            String strSSHFileName) throws HmApiEngineException
    {
    	
    	//get file through scp
    	String strLocalDir = HHMrestore.getUploadDir(strDomainName);
    	
    	if(null == strLocalDir || "".equalsIgnoreCase(strLocalDir))
    	{
    		throw new HmApiEngineException("Create local dir error!");
    	}
    	
    	AhScpMgmt fileTranser = null;
    	
    	try
    	{
    		fileTranser = new AhScpMgmtImpl(strSSHServer,iSSHPport,strSSHUsr,strSSHPsd);
    		
    		fileTranser.scpGet(strSSHPath + File.separator + strSSHFileName, strLocalDir);
    	}catch(Exception ex)
    	{
    		DebugUtil.adminDebugError(ex.getMessage());
    		
    		throw new HmApiEngineException(ex.getMessage());
    	}
        finally 
        {
			if (fileTranser != null) {
				fileTranser.close();
			}
	    }    	
    	
        HmDomain oDomain = QueryUtil.findBoByAttribute(HmDomain.class, "lower(domainName)",
    			strDomainName.toLowerCase());
        
        if(null == oDomain)
    	{
    		throw new HmApiEngineException("There is not the domain with name is: "+strDomainName);
    	}
        
    	//do restore
        RestoreInfo oReturnInfo = HHMoperate.restoreOperation(oDomain, strLocalDir, strSSHFileName);
        
        if(!oReturnInfo.getResult())
        {
        	throw new HmApiEngineException(oReturnInfo.getErrorMsg());
        }
    }

}