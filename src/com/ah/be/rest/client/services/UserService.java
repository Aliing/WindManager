package com.ah.be.rest.client.services;

import java.util.HashMap;
import java.util.Map;

import com.ah.be.rest.client.models.RequestMethod;
import com.ah.be.rest.client.models.UserModel;

public class UserService extends ClientService{
	
	
	public UserService() {
		
	}
	
	/**
	 * @param host server ip  eg. 127.0.0.1
	 */
	public UserService(String host) {
		
		this(host,80,null);
	}
	
	/**
	 * @param host   server ip  eg. 127.0.0.1
	 * @param port   eg.  8443
	 * @param context  eg. myhive
	 */
	public UserService(String host,int port,String context) {
		
		setBaseUrl( host, port, context);
	}
	
	
	/**
	 * REST API for user group sync. (client:cloudauth,HMOL. server:myhive)
	 * @param customerId
	 * @param productId   Includes VHM-ID OPR-ID VCA-ID
	 * @param groupName
	 * @param syncAction      0 add  1 delete 
	 * @return UserModel which contains two fields resultCode & message
	 * 
	 * short returnCode=result.getReturnCode();
	 * 	0 : success
	 *	1 : groupName already exist
	 *	2 : DB error
	 */
	public UserModel syncUserGroup(String customerId,String productId,String groupName,short syncAction)
	{
		Map<String ,String> params=new HashMap<String,String>();
		params.put("customerId", customerId);
		params.put("productId", productId);
		params.put("groupName", groupName);
		params.put("syncAction", String.valueOf(syncAction));
		//1:hmonline  6:cloudauth  4:Redirector(Staging)
		params.put("server",String.valueOf(1));
		UserModel result=getEntityFromHttpRequest(RequestMethod.POST,"/user/sync",UserModel.class,params);
		return result;
		
	}
	
	/**
	 * REST API for user info retrieve when do SSO. (client:cloudauth,HMOL,redirector server myhive)
	 * @param userName
	 * @return  User entity
	 * short returnCode=result.getReturnCode();
	 * 	0 : success
	 *	1 : groupName already exist
	 *	2 : DB error
	 */
	public UserModel retrieveUserInfo(String userName, String productId)
	{
		Map<String ,String> params=new HashMap<String,String>();
		params.put("userName", userName);
		params.put("productId", productId);
		//1:hmonline  6:cloudauth  4:Redirector(Staging)
		params.put("server", String.valueOf(1));
		UserModel result=getEntityFromHttpRequest(RequestMethod.GET,"/user",UserModel.class,params);
		return result;
		
	}
	
}
