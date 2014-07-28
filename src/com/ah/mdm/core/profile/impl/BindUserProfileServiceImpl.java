package com.ah.mdm.core.profile.impl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;

import com.ah.be.rest.ahmdm.client.ResponseFromMDMImpl;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.mdm.core.profile.service.BindUserProfileService;
import com.ah.ui.actions.home.clientManagement.service.CertificateGenSV;
import com.ah.util.Tracer;

public class BindUserProfileServiceImpl implements BindUserProfileService{

	private static final Tracer logger = new Tracer(BindUserProfileServiceImpl.class.getSimpleName());
	private HttpClient client = null;
	
	public static final String BIND_URI = "/api/hm/profiles/bind";
	public static final String UNBIND_URI = "/api/hm/profiles/unbind";
	

	@Override
	public boolean bindUserProfileSuccess(String upid,String profileName,String customId) {
		boolean result = false;
			try{
				String uri=CertificateGenSV.getUrl()+BIND_URI;
				client = new HttpClient();
				ResponseModel responseModel = HttpToolkit.doPostXML(uri,getBindPostStr(upid,profileName,customId),client);
				if(responseModel == null){
					throw new Exception();
				}
				if(HttpStatus.SC_OK == responseModel.getResponseCode()){
					result = true;
				}
				if(HttpStatus.SC_SERVICE_UNAVAILABLE == responseModel.getResponseCode()){
						logger.error("Can't find the server");
					
				}
			}catch(Exception e){
				logger.error(BindUserProfileServiceImpl.class.getSimpleName(), "Failed response from MDM");
			}
		return result;
	}
	
	private String getVersion(){
		//String hostName = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, ConfigUtil.KEY_URL_ROOT_PATH);
		return "1.0";//TODO
	}
	
	private String getBindPostStr(String upid,String profileName,String customId){
		String postStr = "<content version=\""+ this.getVersion()+"\">";
		postStr = postStr +"<CustomerId>"+customId+"</CustomerId>";
		postStr = postStr + "<UPID>"+upid +"</UPID>";
		postStr = postStr + "<Profiles>";
		postStr = postStr + "<ProfileName>"+profileName+"</ProfileName>";
		postStr = postStr + "</Profiles>";
		postStr = postStr + "</content>";
		
		return postStr;
	
	}

	private String getUnBindPostStr(String upid,String profileName,String customId){
		String postStr = "<content version=\""+ this.getVersion()+"\">";
		postStr = postStr + "<UPID>"+upid +"</UPID>";
		postStr = postStr +"<CustomerId>"+customId+"</CustomerId>";
		postStr = postStr + "<Profiles>";
		postStr = postStr + "<ProfileName>"+profileName+"</ProfileName>";
		postStr = postStr + "<Profiles>";
		postStr = postStr + "</content>";
		
		return postStr;
	
	}

	
	@Override
	public boolean unBindUserProfileSuccess(String upid,String profileName,String customId) {
		boolean result = false;
		try{
			String uri=CertificateGenSV.getUrl()+UNBIND_URI;
			client = new HttpClient();
			ResponseModel responseModel = HttpToolkit.doPostXML(uri,getUnBindPostStr(upid,profileName,customId),client);
			if(responseModel == null){
				return false;
			}
			if(HttpStatus.SC_OK == responseModel.getResponseCode()){
				result = true;
			}
			if(HttpStatus.SC_SERVICE_UNAVAILABLE == responseModel.getResponseCode()){
					logger.error("Can't find the server");
				
			}
		}catch(Exception e){
			logger.error(BindUserProfileServiceImpl.class.getSimpleName(), "Failed response from MDM");
		}
	return result;
	}

}
