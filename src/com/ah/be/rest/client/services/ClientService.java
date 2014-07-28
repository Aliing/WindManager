package com.ah.be.rest.client.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.rest.client.models.RequestMethod;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.be.rest.client.utils.HttpToolkit;
import com.ah.be.rest.client.utils.JsonToolkit;
import com.ah.util.Tracer;

/**
 * @author slu
 * @since  2012-04-12
 *
 */
public abstract class ClientService{
	private static final Tracer log = new Tracer(ClientService.class.getSimpleName());
                  private static final String HTTP_SHEMA="https://";
	private static final String URI_PREFIX="/rest";
	protected String baseUrl = NmsUtil.getPortalServiceURL()+URI_PREFIX;
	private String hostName = baseUrl.substring(8, baseUrl.indexOf(":443"));
	
	protected String getBaseUrl() {
		return baseUrl;
	}
	
	protected void setBaseUrl(String host,int port,String context) {
		if(!StringUtils.isEmpty(host))
		{
			StringBuilder url=new StringBuilder();
			if(host.indexOf(HTTP_SHEMA)==-1)
			{
				url.append(HTTP_SHEMA);
			}
			url.append(host);
			String urlStr = url.toString();
			if (urlStr.lastIndexOf(":") > 8) {
				hostName = urlStr.substring(8, urlStr.lastIndexOf(":"));
			} else {
				hostName = urlStr.substring(8);
			}
			if(port>0&&port!=80)
			{
				url.append(":").append(port);
			}
			if(!StringUtils.isEmpty(context))
			{
				url.append("/").append(context);
			}
			url.append(URI_PREFIX);
			this.baseUrl = url.toString();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	protected  <T> T getEntityFromHttpRequest (RequestMethod method,String resource,Class<T> resultType,Map<String,String> params) {
		if(method==null||resource==null)
		{
			return null;
		}
		String requestUrl=parseUrl(resource,null);
		ResponseModel responseObj=sendHttpRequest(method,requestUrl,params);
		T result=null;
		if(responseObj!=null)
		{
			if(responseObj.getResponseCode()==200)
			{
				result=(T)JsonToolkit.getObjectFromJsonString(responseObj.getResponseText(),resultType,null);
			}
			else
             {
                 String msg="request fails,detail info:requestUrl="+requestUrl
                 		+"responseCode="+responseObj.getResponseCode()
                 		+",responseMessage="+responseObj.getResponseText();
                 log.error("getEntityFromHttpRequest",msg);
             }
		}
		else{
			String msg="request fails,detail info:requestUrl="+requestUrl
					+",response result is null";
			log.error("getEntityFromHttpRequest",msg);
			try {
				InetAddress address = InetAddress.getByName(hostName);
	            if (null != address) {
	            	log.error("getEntityFromHttpRequest", "get the baseUrl IP: "+address.getHostAddress());
	            }
			} catch (UnknownHostException uhe) {
				log.error("getEntityFromHttpRequest", "get the baseUrl IP exception: "+uhe.getMessage());
			}	
		}
		return result;
	}
	
	protected  DynaBean getBeanFromHttpRequest(RequestMethod method,String resource,Map<String,String> params) {
		if(method==null||resource==null)
		{
			return null;
		}
		String requestUrl=parseUrl(resource,null);
		ResponseModel responseObj=sendHttpRequest(method,requestUrl,params);
		DynaBean result=null;
		if(responseObj!=null)
		{
			result=JsonToolkit.getObjectFromJsonString(responseObj.getResponseText(),null);
		}
		return result;
	}
	
	
	private ResponseModel sendHttpRequest(RequestMethod method,String requestUrl,Map<String, String> params)
	{
		ResponseModel responseObj=null;
		switch(method)
		{
		case GET :
			responseObj=HttpToolkit.doGet(requestUrl, params);
			break;
		case POST :
			responseObj=HttpToolkit.doPost(requestUrl, params);
			break;
		case PUT :
			responseObj=HttpToolkit.doPut(requestUrl, params);
			break;
		case DELETE :
			responseObj=HttpToolkit.doDelete(requestUrl, params);
			break;
		}
		return responseObj;
	}
	
	
	private String parseUrl(String resource,String id)
	{
		StringBuilder sb=new StringBuilder();
		sb.append(baseUrl);
		sb.append(StringUtils.lowerCase(resource));
		if(!StringUtils.isEmpty(id))
		{
			String pattern="[^\\w]|\\s|\t|\r|\n";
			if(validateStr(id,pattern))
			{
				return null;
			}
			sb.append("/");
			sb.append(id);
		}
		sb.append(".json");
		return sb.toString();
		
	}


//	private Map<String, String> parseParamsFromObj(Object obj) {
//		Map<String, String> params=null;
//		Field[] fields = obj.getClass().getDeclaredFields();
//		if (fields.length > 0) {
//			params = new HashMap<String, String>();
//			try {
//				for (Field field : fields) {
//					field.setAccessible(true);
//					if (field.get(obj) != null) {
//						if (!String.valueOf(field.get(obj)).equals("")) {
//							params.put(field.getName(),
//									String.valueOf(field.get(obj)));
//						}
//					}
//
//				}
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return params;
//	}
	
	private boolean validateStr(String input,String pattern)
	{	
		if(StringUtils.isEmpty(input)||StringUtils.isEmpty(pattern))
		{
			return false;
		}
		Pattern p = Pattern.compile(pattern);
		Matcher m=p.matcher(input);
		if(m.find())
		{
			return false;
		}
		return true;
	}
}