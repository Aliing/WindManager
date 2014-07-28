package com.ah.util;

import com.ah.be.os.LinuxNetConfigImpl;

public class setDefaultCasClient {
	
	public static void main(String[] args)
	{
	    String strClientDefaultIp = CasTool.getCASClientIP();
	    
	    if(strClientDefaultIp.equalsIgnoreCase("localhost"))
	    {
	    	 String strIp = new LinuxNetConfigImpl().getHiveManagerIPAddr();
	    	 
	    	 if(null == strIp || "".equalsIgnoreCase(strIp) 
	    			 || " ".equalsIgnoreCase(strIp) || "0.0.0.0".equalsIgnoreCase(strIp))
	    	 {
	    		 return ;
	    	 }
	    	 
	    	 CasTool.setCASClientIP(strIp);
	    }
	}

}