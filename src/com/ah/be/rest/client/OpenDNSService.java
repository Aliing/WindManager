package com.ah.be.rest.client;

import java.net.URI;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONObject;

import com.ah.be.common.ConfigUtil;
import com.ah.be.rest.client.models.opendns.OpenDNSModel;
import com.ah.util.Tracer;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class OpenDNSService {
	
	private static String api_key;
	private static String rest_url;
	private static String METHOD_LOGIN = "account_signin";
	private static String METHOD_CREATE_DID = "device_create";
	private static String METHOD_GET_DID = "device_get";
	
	private static final Tracer log = new Tracer(OpenDNSService.class.getSimpleName());
	
	static{
		api_key = ConfigUtil.getConfigInfo(ConfigUtil.OPENDNS_SECTION, ConfigUtil.OPENDNS_API_KEY, "");
		rest_url = ConfigUtil.getConfigInfo(ConfigUtil.OPENDNS_SECTION, ConfigUtil.OPENDNS_URL, "");
	}
	
	public static OpenDNSModel login(String userName, String password){
		OpenDNSModel openDNSModel = new OpenDNSModel();
		try{
			ClientConfig config = new DefaultClientConfig();  
	        Client client = Client.create(config);  
	        WebResource service = client.resource(getBaseURI());  
	          
	        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();  
	        formData.add("api_key", api_key);  
	        formData.add("method", METHOD_LOGIN);  
	        formData.add("email", userName);  
	        formData.add("password", password);  
	        String result = service.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, formData);  
	        
			JSONObject jsonObj = new JSONObject(result);
			openDNSModel = openDNSModel.tranformJsonToLoginModel(jsonObj);
		}catch(Exception ex){
			log.error("Get error when login OpenDNS", ex);
			openDNSModel.setSuccessFlag(false);
			openDNSModel.setError_message(OpenDNSModel.RESULT_INTERNAL_SERVER_ERROR);
		}
		return openDNSModel;
	}
	
	public static OpenDNSModel createDevice(String token, String devicekey, String label){
		OpenDNSModel openDNSModel = new OpenDNSModel();
		openDNSModel.setDeviceKey(devicekey);
		openDNSModel.setDeviceLabel(label);
		try{
			ClientConfig config = new DefaultClientConfig();  
	        Client client = Client.create(config);  
	        WebResource service = client.resource(getBaseURI());  
	          
	        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();  
	        formData.add("api_key", api_key);  
	        formData.add("token", token);  
	        formData.add("method", METHOD_CREATE_DID);  
	        formData.add("device_key", devicekey);  
	        formData.add("label", label);  
	        String result = service.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, formData);  
			JSONObject jsonObj = new JSONObject(result);
			openDNSModel = openDNSModel.tranformJsonToDeviceModel(jsonObj);
		}catch(Exception ex){
			log.error("Get error when create device in OpenDNS", ex);
			openDNSModel.setSuccessFlag(false);
			openDNSModel.setError_message(OpenDNSModel.RESULT_INTERNAL_SERVER_ERROR);
		}
		return openDNSModel;
	}
	
	
	public static OpenDNSModel fetchDevice(String token, String devicekey){
		OpenDNSModel openDNSModel = new OpenDNSModel();
		openDNSModel.setDeviceKey(devicekey);
		try{
			ClientConfig config = new DefaultClientConfig();  
	        Client client = Client.create(config);  
	        WebResource service = client.resource(getBaseURI());  
	          
	        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();  
	        formData.add("api_key", api_key);  
	        formData.add("token", token);  
	        formData.add("method", METHOD_GET_DID);  
	        formData.add("device_key", devicekey);  
	        String result = service.type(MediaType.APPLICATION_FORM_URLENCODED).post(String.class, formData);  
			JSONObject jsonObj = new JSONObject(result);
			openDNSModel = openDNSModel.tranformJsonToDeviceModel(jsonObj);
		}catch(Exception ex){
			log.error("Get error when create device in OpenDNS", ex);
			openDNSModel.setSuccessFlag(false);
			openDNSModel.setError_message(OpenDNSModel.RESULT_INTERNAL_SERVER_ERROR);
		}
		return openDNSModel;
	}
	
	private static URI getBaseURI() {  
        return UriBuilder.fromUri(rest_url).build();  
    }  
}
