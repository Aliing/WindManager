package com.ah.be.admin;

import java.util.List;

import com.ah.be.app.DebugUtil;

public class BeAdminShellTool {
	
	public static ShellRslt_st exe_shell_with_rslt(String[] strShellCmds)
	{
        ShellRslt_st oRslt = new ShellRslt_st();
		
		List<String> strRsltList =  BeAdminCentOSTools.getOutStreamsExecCmd(strShellCmds);
		
		if(null == strRsltList || 0 == strRsltList.size())
    	{
    		DebugUtil.adminDebugWarn(
			"BeAdminShellTool.ShellRslt_st() no return could not charge");
    
    		oRslt.setResult(false);
    		strRsltList.add("Can not get any results");
    		oRslt.setContent(strRsltList);
    		return oRslt;
    	}
		
		int i = strRsltList.size();
        
        String strRslt = strRsltList.get(i-1);
        
        int iRslt;
    	
    	try
    	{    	
    	    iRslt = Integer.parseInt(strRslt);
    	}
    	catch(Exception ex)
    	{
    		DebugUtil.adminDebugWarn(
			"BeAdminShellTool.ShellRslt_st() not get the integer result");
    		
    		oRslt.setResult(false);
    		strRsltList.clear();
    		strRsltList.add("Can not parse the results");
    		oRslt.setContent(strRsltList);
    		return oRslt;
    	}
    	
    	if(0 != iRslt)
    	{
    		oRslt.setResult(false);
    		strRsltList.remove(i-1);
    		oRslt.setContent(strRsltList);
    		return oRslt;
    	}  
    	
    	oRslt.setResult(true);
    	strRsltList.remove(i-1);
    	oRslt.setContent(strRsltList);
    	return oRslt;
	}

}
