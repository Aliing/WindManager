package com.ah.be.rest.ahmdm.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;

import com.Ostermiller.util.Base64;
import com.ah.be.common.ConfigUtil;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.util.Tracer;

public class ResponseFromMDMImpl implements IResponseFromMDM{
	
	private static final Tracer log = new Tracer(ResponseFromMDMImpl.class.getSimpleName());
	private HttpClient client = null;
	
	public static final String ACM_USER_NAME = "acmuser";
	public static final String ACM_PASSWORD = "Aer0Hive!";
	
	private ResponseModel responseModel;
	
	public ResponseModel sendInfoToMDM(String uri,String XMLString) throws Exception{
		return sendInfoToAcMWithAuth(uri,XMLString);
	}
	
	public ResponseModel sendInfoToMDM(String uri,String XMLString, List<Header> headers) throws Exception{
		try{
			client = new HttpClient();
			responseModel = HttpToolkit.doPostXMLWithHeaders(uri,XMLString,client, headers);
			if(responseModel == null){
				throw new IOException();
			}
		}catch(IOException e){
			log.error(ResponseFromMDMImpl.class.getSimpleName(),"No response from MDM ",e);
			throw new IOException();
		}
		return responseModel;
	}
	
	public ResponseModel sendInfoToAcMWithAuth(String uri,String XMLString) throws Exception{
		try{
			String basic = ACM_USER_NAME + ":" + ACM_PASSWORD;
			client = new HttpClient();
			List<Header> headers = new ArrayList<Header>();
			headers.add(new Header("Accept","application/xml"));
			headers.add(new Header("Authorization", "Basic "+Base64.encode(basic)));
			responseModel = HttpToolkit.doPostXMLWithHeaders(uri,XMLString,client,headers);
			if(responseModel == null){
				log.error(ResponseFromMDMImpl.class.getSimpleName(),"No response from ACM");
				throw new Exception("No response from Client Management server.");
			}
		}catch(Exception e){
			throw e;
		}
		return responseModel;
	}

}
