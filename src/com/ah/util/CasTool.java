package com.ah.util;

import com.ah.be.app.DebugUtil;
import com.ah.be.os.FileManager;



public class CasTool {
	
	public static String WEB_XML_CAS_LOGIN_URL = "casServerLoginUrl";
    public static String WEB_XML_CAS_PREFIX_URL = "casServerUrlPrefix";
	public static String WEB_XML_CAS_HTTPS     = "https://";
	public static String WEB_XML_CAS_LOGIN_TAIL = "/cas/login";
	public static String WEB_XML_CAS_PARAM_TAIL = "</param-value>";
	public static String WEB_XML_CAS_SERVER_NAMW = "serverName";
	public static String WEB_XML_FILE            = System.getenv("HM_ROOT")+"/WEB-INF/web.xml";
	
	public static String getCASServerIP() {
		try {
			String[] content = FileManager.getInstance().readFile(WEB_XML_FILE);
			for (int i = 0; i < content.length; i++) {
				String row = content[i];
				
				if(row.indexOf("casServerLoginUrl") > 0)
				{
					String server = content[i+1];
					server = server.substring(server.indexOf("://")+"://".length());
					server = server.substring(0, server.indexOf(":"));
					
					return server;
				}
			}
		} catch (Exception e) {
			DebugUtil.adminDebugWarn("CasTool.getCASServerIP() catch exception", e);
		}
		
		return null;
	}
	
	public static String getCASClientIP() {
		try {
			String[] content = FileManager.getInstance().readFile(WEB_XML_FILE);
			for (int i = 0; i < content.length; i++) {
				String row = content[i];
				
				if(row.indexOf("casServerLoginUrl") > 0)
				{
					String client = content[i+5];
					client = client.substring(client.indexOf("://")+"://".length());
					client = client.substring(0, client.indexOf(":"));
					
					return client;
				}
			}
		} catch (Exception e) {
			DebugUtil.adminDebugWarn("CasTool.getCASClientIP() catch exception", e);
		}
		
		return null;
	}
	
	public static void setCASServerIP(String newIP) {
		try
		{
			String[] content = FileManager.getInstance().readFile(WEB_XML_FILE);
			
			for(int i = 0; i < content.length; ++i)
			{
				String row = content[i];
				
				if(row.indexOf("casServerLoginUrl") > 0 || row.indexOf("casServerUrlPrefix") > 0)
				{
					String server = content[i+1];
					server = server.substring(server.indexOf("://")+"://".length());
					server = server.substring(0, server.indexOf(":"));
					
					content[i+1] = content[i+1].replace(server, newIP);
				}
			}
			
			FileManager.getInstance().writeFile(WEB_XML_FILE, content, false);
		}
		catch(Exception e)
		{
			DebugUtil.adminDebugWarn("CasTool.setCASServerIP() catch exception", e);
		}
	}
	
	public static void setCASClientIP(String newIP) {
		try
		{
			String[] content = FileManager.getInstance().readFile(WEB_XML_FILE);
			
			for(int i = 0; i < content.length; ++i)
			{
				String row = content[i];
				
				if(row.indexOf("casServerLoginUrl") > 0 || row.indexOf("casServerUrlPrefix") > 0)
				{
					String client = content[i+5];
					client = client.substring(client.indexOf("://")+"://".length());
					client = client.substring(0, client.indexOf(":"));
					
					content[i+5] = content[i+5].replace(client, newIP);
				}
			}
			
			FileManager.getInstance().writeFile(WEB_XML_FILE, content, false);
		}
		catch(Exception e)
		{
			DebugUtil.adminDebugWarn("CasTool.setCASClientIP() catch exception", e);
		}
	}
	
	public static boolean getCasServerAndClient(String strFile, String[] sRslt)
	{
		boolean bReturn = false;
		
		try
		{
			String[] content = FileManager.getInstance().readFile(strFile);	
			for (int i = 0; i < content.length; i++) {
				
				String row = content[i];
				
				if(row.indexOf(WEB_XML_CAS_LOGIN_URL) > 0)
				{
					String strTmp = "";
					
					strTmp = content[i+1];
					strTmp = strTmp.substring(strTmp.indexOf(WEB_XML_CAS_HTTPS), 
							strTmp.indexOf(WEB_XML_CAS_LOGIN_TAIL));
					
					sRslt[0] = strTmp;
					
					if(!(content[i+4].indexOf(WEB_XML_CAS_SERVER_NAMW)>0))
					{
						bReturn = false;
						break;
					}
					
					strTmp = content[i+5];
					
					strTmp = strTmp.substring(strTmp.indexOf(WEB_XML_CAS_HTTPS), 
							strTmp.indexOf(WEB_XML_CAS_PARAM_TAIL));
					sRslt[1] = strTmp;
					
					bReturn = true;
					break;
				}				
			}
			
			return bReturn;
		}
		catch(Exception ex)
		{
			//add logs
			DebugUtil.adminDebugWarn("get cas server info have error!", ex);
			return false;
		}		
	}
	
	public static boolean overwriteCasInfo(String strFile, String[] oldInfo, String[] newInfo)
	{
		boolean bRslt = false;
		
		try
		{
			String[] content = FileManager.getInstance().readFile(strFile);
			
			for(int i = 0; i < content.length; ++i)
			{
				String row = content[i];
				
				if(row.indexOf(WEB_XML_CAS_LOGIN_URL) > 0)
				{
					content[i+1] = content[i+1].replace(oldInfo[0], newInfo[0]);
					
					if(!(content[i+4].indexOf(WEB_XML_CAS_SERVER_NAMW)>0))
					{
						bRslt = false;
						break;
					}
					
					content[i+5] = content[i+5].replace(oldInfo[1], newInfo[1]);
					
					bRslt = true;
				}
				
				if(row.indexOf(WEB_XML_CAS_PREFIX_URL) > 0)
				{
					content[i+1] = content[i+1].replace(oldInfo[0], newInfo[0]);
					
					if(!(content[i+4].indexOf(WEB_XML_CAS_SERVER_NAMW)>0))
					{
						bRslt = false;
						break;
					}
					
					content[i+5] = content[i+5].replace(oldInfo[1], newInfo[1]);
					
					bRslt = true;	
				}				
			}
			
			//write file
			FileManager.getInstance().writeFile(strFile, content, false);
			
			return bRslt;
		}
		catch(Exception ex)
		{
			//add some log
			return false;
		}
	}
	
	public static boolean overwriteCasServer(String strFile,String strOldServer, String strNewServer)
	{
		boolean bRslt = false;
		
		try
		{
			String[] content = FileManager.getInstance().readFile(strFile);
			
			for(int i = 0; i < content.length; ++i)
			{
				String row = content[i];
				
				if(row.indexOf(WEB_XML_CAS_LOGIN_URL) > 0 || row.indexOf(WEB_XML_CAS_PREFIX_URL) > 0)
				{
					content[i+1] = content[i+1].replace(strOldServer, strNewServer);
					
					bRslt = true;
				}
			}
			
			FileManager.getInstance().writeFile(strFile, content, false);
			
			return bRslt;
		}
		catch(Exception ex)
		{
			//add log
			return false;
		}
	}
	
	public static boolean overwriteCasClient(String strFile,String strOldClient, String strNewClient)
	{
        boolean bRslt = false;
		
		try
		{
			String[] content = FileManager.getInstance().readFile(strFile);
			
			for(int i = 0; i < content.length; ++i)
			{
				String row = content[i];
				
				if(row.indexOf(WEB_XML_CAS_LOGIN_URL) > 0)
				{
					if(!(content[i+4].indexOf(WEB_XML_CAS_SERVER_NAMW)>0))
					{
						bRslt = false;
						break;
					}
					
					content[i+5] = content[i+5].replace(strOldClient, strNewClient);
					
					bRslt = true;
				}
				
				if(row.indexOf(WEB_XML_CAS_PREFIX_URL) > 0)
				{
					if(!(content[i+4].indexOf(WEB_XML_CAS_SERVER_NAMW)>0))
					{
						bRslt = false;
						break;
					}
					
					content[i+5] = content[i+5].replace(strOldClient, strNewClient);
					
					bRslt = true;
				}				
			}
			
            FileManager.getInstance().writeFile(strFile, content, false);
			
			return bRslt;
		}
		catch(Exception ex)
		{
			//add log
			return false;
		}
	}
	
	
	public static String getCasServer()
	{
		return getCasServer(WEB_XML_FILE);
	}
	
	public static String getCasClient()
	{
		return getCasClient(WEB_XML_FILE);
	}
	
	public static String getCasServer(String strFile)
	{
		String[] strContent = new String[2];
		
		if(!getCasServerAndClient(strFile, strContent))
		{
			return null;
		}
		
		return strContent[0];
	}
	
	public static String getCasClient(String strFile)
	{
        String[] strContent = new String[2];
		
		if(!getCasServerAndClient(strFile, strContent))
		{
			return null;
		}
		
		return strContent[1];
	}

	public static boolean copyCasInfo(String strSrcFile, String strDestFile)
	{
		boolean bRslt = false;
		String[] sSrc = new String[2];
		String[] sDes = new String[2];
		
		if(!getCasServerAndClient(strSrcFile, sSrc))
		{
			bRslt = false;
			return bRslt;
		}
		
		if(!getCasServerAndClient(strDestFile, sDes))
		{
	        bRslt = false;
			return bRslt;
		}
		
		if(!overwriteCasInfo(strDestFile, sDes, sSrc))
		{
			bRslt = false;
			return bRslt;
		}
		
		bRslt = true;
		return bRslt;
	}
}
