package com.ah.be.rest.client.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.be.rest.client.models.RequestMethod;
import com.ah.be.rest.client.models.SettingsModel;
import com.ah.util.Tracer;

public class SettingsService extends ClientService{
	
	private static final Tracer	log	= new Tracer(SettingsService.class
			.getSimpleName());
	public SettingsService() {
		
	}
	
	/**
	 * @param host server ip  eg. 127.0.0.1
	 */
	public SettingsService(String host) {
		
		this(host,80,null);
	}
	
	/**
	 * @param host   server ip  eg. 127.0.0.1
	 * @param port   eg.  8443
	 * @param context  eg. myhive
	 */
	public SettingsService(String host,int port,String context) {
		
		setBaseUrl( host, port, context);
	}
	

	/**
	 * @param customerID
	 * @return start settings includes hiveName,modeType,ssidPwd,timeZone;

	 */
	public SettingsModel getStartSettings(String customerID)
	{
		String msg="";
		SettingsModel result=null;
		try{
			if(StringUtils.isEmpty(customerID))
			{
				msg="getStartSettings error, request parameter customerID is empty or null";
				log.error("getStartSettings", msg);
				throw new RuntimeException(msg);
			}
			Map<String ,String> params=new HashMap<String,String>();
			
			params.put("customerID", customerID);
			 result=getEntityFromHttpRequest(RequestMethod.GET,"/newsettings",SettingsModel.class,params);
			 if(result!=null)
			 {
				 if(result.getReturnCode()!=-1)
					 {
						 msg="getStartSettings,request parameter customerID:"+customerID+",return code : "+result.getReturnCode()+",message :"+result.getMessage();
						 log.info(msg);
					 }
					 else
					 {
						 msg="get start settings failed,request parameter customerID:"+customerID+",return code : "+result.getReturnCode()+",message :"+result.getMessage();
						 log.error("getStartSettings",msg);
					 }
			 }
			 else{
				 msg="get start settings failed,response result is null";
				 log.error("getStartSettings",msg);
			 }
			
		}catch(Exception e)
		{
			
			result=new SettingsModel();
			result.setReturnCode((short)-1);
			result.setMessage(e.getMessage());
			log.error("getStartSettings", e.getMessage());
		}
		return result;
	}
	
}
