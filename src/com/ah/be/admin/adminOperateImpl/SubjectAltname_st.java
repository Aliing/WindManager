package com.ah.be.admin.adminOperateImpl;

import java.util.List;
import java.util.ArrayList;

public class SubjectAltname_st {
	
	public static final String sub_email = "email";
	public static final String sub_dns   = "DNS";
	public static final String sub_ip    = "IP";
	public static final String subject   = "Subject";
	
    private boolean bResult = false;    
	
	private List<String>  lRslt  = null;
	private List<String>  lIP    = new ArrayList<String>();
	private List<String>  lDns   = new ArrayList<String>();
	private List<String>  lEmail = new ArrayList<String>();
	private String		  lAsn1dn= null;
	
	public void setResult(boolean bFlag)
	{
		bResult = bFlag;
	}
	
	public boolean is_ok()
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
		
	public List<String> getIpRslt()
	{
		return lIP;
	}
	
	public List<String>  getDnsRslt()
	{
		return lDns;
	}
	
	public List<String> getEmailRslt()
	{
		return lEmail;
	}
	
	public String getAsn1dn() {
		
		if( null != lAsn1dn && lAsn1dn.length() >2  && "/".equals(lAsn1dn.substring(0, 1)))
		{
			lAsn1dn = lAsn1dn.substring(1);
		}
		
		return lAsn1dn;
	}

	public String getErrorMsg()
	{
		if(!is_content())
		{
			return "Can not find error message";
		}
		
		return lRslt.get(lRslt.size()-1);
	}

	public void parseResult()
	{
		if(!is_content())
		{
			return ;
		}
		
		if(null != lEmail)
		{
			lEmail.clear();
		}
		
		if(null != lDns)
		{
			lDns.clear();
		}
		
		if(null != lIP)
		{
			lIP.clear();
		}
		
		for(int i=0; i < lRslt.size(); ++i)
		{
			String strTmp = lRslt.get(i);
			
			int index = 0;
			
			if((index=strTmp.indexOf(subject)) != -1)
			{
				lAsn1dn = strTmp.substring(index+subject.length()+1);
				continue;
			}
			
			if((index=strTmp.indexOf(sub_email)) != -1)
			{
				lEmail.add(strTmp.substring(index+sub_email.length()+1));
				continue;
			}
			
			if((index=strTmp.indexOf(sub_dns)) != -1)
			{
				lDns.add(strTmp.substring(index+sub_dns.length()+1));
				continue;
			}
			
			if((index=strTmp.indexOf(sub_ip)) != -1)
			{
				lIP.add(strTmp.substring(index+sub_ip.length()+1));
				continue;
			}
		}
	}	
	
	public static void main(String[] str)
	{
		
	}
}
