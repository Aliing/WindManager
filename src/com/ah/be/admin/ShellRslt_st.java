package com.ah.be.admin;

import java.util.List;

public class ShellRslt_st {
	
	private boolean bResult = false;
	
	private List<String> lRslt;
	
	public void setResult(boolean bFlag)
	{
		bResult = bFlag;
	}
	
	public boolean getResult()
	{
		return bResult;
	}
	
	public boolean is_content()
	{
		if(null == lRslt || lRslt.isEmpty())
		{
			return false;
		}
		
		return true;
	}
	
	public void setContent(List<String> lContent)
	{
		lRslt = lContent;
	}
	
	public List<String> getContent()
	{
		return lRslt;
	}

}
