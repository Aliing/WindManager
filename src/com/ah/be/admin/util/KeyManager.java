package com.ah.be.admin.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.admin.BeAdminCentOSTools;
import com.ah.be.app.DebugUtil;
import com.ah.be.communication.event.BeHostIdentificationKeyEvent;
import com.ah.be.os.FileManager;

public class KeyManager implements AhSshKeyMgmt {
	
	private static final String strLoginKeyFile = "/HiveManager/ssh_key/ssh_login_key";
	
	private static final String strHostIdKeyFile = "/HiveManager/ssh_key/ssh_host_id_key";
	
	private String strLoginKeys;
	
	private String strHostIdKeys;
	
	private final Object sync_lock = new Object();
	
	public KeyManager()
	{
		initKeys();
	}
	
	public Map<Byte, String> getKeys()
	{
		synchronized(sync_lock)
		{
		    if(null == strLoginKeys || null == strHostIdKeys )
		    {
		    	initKeys();
		    
			    if(null == strLoginKeys || null == strHostIdKeys )
			    {
			    	return null;
			    }		    
		    }
		  
		    Map<Byte, String> oMap = new HashMap<Byte, String>();
			
		    oMap.put(BeHostIdentificationKeyEvent.KEYTYPE_PRIVATEKEY, strLoginKeys);
			oMap.put(BeHostIdentificationKeyEvent.KEYTYPE_PUBLICKEY, strHostIdKeys);
			
			return oMap;
		}
	}
	
	private void setKeystonull()
	{
		strLoginKeys = null;
		
		strHostIdKeys = null;
	}
	
	private void initKeys()
	{
		try
		{
			String[] strLoginContet;
			//String strLoginKey;
			
			String[] strHostContent;
			//String strHostkey;
			
			strLoginContet=FileManager.getInstance().readFile(strLoginKeyFile);
			strHostContent=FileManager.getInstance().readFile(strHostIdKeyFile);
			
			if(null == strLoginContet || 0 == strLoginContet.length || null == strHostContent || 0 == strHostContent.length )
			{
				setKeystonull();
				
				return;
			}
			
			strLoginKeys=strLoginContet[0];
			
			for(int i=1; i < strLoginContet.length; ++i)
			{
				strLoginKeys=strLoginKeys+"\n"+strLoginContet[i];
			}

			strHostIdKeys=strHostContent[0];
			
			for(int i=1; i < strHostContent.length; ++i)
			{
				strHostIdKeys=strHostIdKeys+"\n"+strHostContent[i];
			}							
		}catch(Exception ex)
		{
			setKeystonull();
		}				
	}
	
	public boolean generateKeys(String strType)
	{ 
		if(null == strType)
		{
			return false;
		}
		
		strType = strType.toLowerCase();
		
		synchronized(sync_lock)
		{
			String strCmd = "sh " + BeAdminCentOSTools.ahShellRoot
			+ "/generateSshkey.sh"+" "+strType;
			

	        List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strCmd);
	    	
	    	if(null == strRsltList || 0 == strRsltList.size())
	    	{
	    		DebugUtil.adminDebugWarn(
				"KeyManager.generatekeys() no return could not charge");
	    		
	    		setKeystonull();
	    		
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
	    		DebugUtil.adminDebugWarn(
				"KeyManager.generatekeys() not get the integer result");
	    		
	    		setKeystonull();
	    			    		
	    	    return false;
	    	}
	    	
	    	setKeystonull();
	    	
	    	switch (iRslt)
	    	{
	    	case 0: 	    		
	    		return true; 
	    	case 1:
	    		return false;
	    	default:
	    		return true;
	    	}
		}
	}

}