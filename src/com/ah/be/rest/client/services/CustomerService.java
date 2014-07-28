package com.ah.be.rest.client.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;


import com.ah.be.rest.client.models.CustomerModel;
import com.ah.be.rest.client.models.RequestMethod;
import com.ah.util.Tracer;

public class CustomerService extends ClientService{
	
	private static final Tracer	log	= new Tracer(CustomerService.class
			.getSimpleName());
	public CustomerService() {
		
	}
	
	/**
	 * @param host server ip  eg. 127.0.0.1
	 */
	public CustomerService(String host) {
		
		this(host,80,null);
	}
	
	/**
	 * @param host   server ip  eg. 127.0.0.1
	 * @param port   eg.  8443
	 * @param context  eg. myhive
	 */
	public CustomerService(String host,int port,String context) {
		
		setBaseUrl( host, port, context);
	}
	
	/**
	 * REST API for customer id retrieve. (client:HMOL,server:MYHIVE)
	 * @param vhmId
	 * @return  Customer entity
	 * short returnCode=result.getReturnCode();
	 * -1 : Exception 
	 * 	0 : success
	 *	1 : DB error
	 */
	public CustomerModel retrieveCustomerId(String vhmId)
	{
		String msg="";
		Map<String ,String> params=new HashMap<String,String>();
		CustomerModel result=null;
		params.put("vhmId", vhmId);
		try{
			if(StringUtils.isEmpty(vhmId))
			{
				msg="retrieveCustomerId error, request parameter vhmId is empty or null";
				log.error("retrieveCustomerId", msg);
				throw new Exception(msg);
			}
			 result=getEntityFromHttpRequest(RequestMethod.GET,"/myhive-customer",CustomerModel.class,params);
			 msg="retrieveCustomerId,request parameter vhmId:"+vhmId+",return code : "+result.getReturnCode()+",message :"+result.getMessage();
			 log.info(msg);
			
		}catch(Exception e)
		{
			
			result=new CustomerModel();
			result.setReturnCode((short)-1);
			result.setMessage(e.getMessage());
			log.error("retrieveCustomerId", e.getMessage());
		}
		return result;
	}
	
}
