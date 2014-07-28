package com.ah.be.rest.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.DeleteMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.lang.StringUtils;

import com.Ostermiller.util.Base64;
import com.ah.be.common.ConfigUtil;
import com.ah.be.rest.client.contrib.RestSSLSocketFactory;
import com.ah.be.rest.client.models.ResponseModel;
import com.ah.util.Tracer;

public final class HttpToolkit {
	private static final Tracer log = new Tracer(HttpToolkit.class.getSimpleName());
	private static final String DEFAULT_CHARSET = "UTF-8";

	public static ResponseModel doGet(String url,  Map<String, String> params, String charset) {
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			HttpMethod method = new GetMethod();
			setMethodParams(method,params);
			try {
				restResponse = sendRequest(url,method,charset);
			} catch (IOException e) {
				log.error("HttpToolkit,doGet()",e.getMessage());
			}catch (Exception e) {
				log.error("HttpToolkit,doGet()",e.getMessage());
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static HttpClient authenticate(String targetURL, String userName, String password){
		URL url = null;
		HttpClient client = new HttpClient();
		try {
			url = new URL(targetURL);
		} catch (MalformedURLException e) {
			 log.error("The target url is not valid ", e);
		}
		client.getState().setCredentials(
				new AuthScope(url.getHost(),url.getPort(), AuthScope.ANY_REALM),
				new UsernamePasswordCredentials(userName,password));
		return client;
	}
	
	public static ResponseModel doGet(String url,  Map<String, String> params){
		return doGet(url,params,DEFAULT_CHARSET);
	}
	
	public static ResponseModel doGet(String url, Map<String, String> params, HttpClient client) {
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			HttpMethod method = new GetMethod();
			setMethodParams(method,params);
			try {
				restResponse = sendRequest(url,method,client, DEFAULT_CHARSET);
			} catch (IOException e) {
				log.error("HttpToolkit,doGet()",e.getMessage());
			}catch (Exception e) {
				log.error("HttpToolkit,doGet()",e.getMessage());
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static ResponseModel doPostJson(String url, String jsonString, HttpClient client) {
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			PostMethod method = new PostMethod();
			RequestEntity requestEntity = new ByteArrayRequestEntity(jsonString.getBytes(), "application/json");
		    method.setRequestEntity(requestEntity);
			try {
				restResponse = sendRequest(url, method, client, DEFAULT_CHARSET);
			} catch (Exception e) {
				log.error("HttpToolkit,doPostJson()",e.getMessage());
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}

	public static ResponseModel doPost(String url, Map<String, String> params,
			String charset) {
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			HttpMethod method = new PostMethod();
			setMethodParams(method,params);
			try {
				restResponse = sendRequest(url,method,charset);
			} catch (Exception e) {
				log.error("HttpToolkit,doPost()",e.getMessage());
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	/**
	 * @description short for doPostXML with default charset
	 * @author huihe
	 */
	public static ResponseModel doPostXML(String url,  String StrParam, HttpClient client){
		return doPostXML(url,StrParam,client, DEFAULT_CHARSET);
	}
	
	/**
	 * @description short for doPostXML with default charset
	 * @author huihe
	 */
	public static ResponseModel doPostXML4ACMDefaultHead(String url,  String StrParam, HttpClient client){
		String basic = ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, 
                ConfigUtil.HM_ACM_USER) + ":" + 
				ConfigUtil.getConfigInfo(ConfigUtil.SECTION_AEROHIVE_MDM, 
    	                ConfigUtil.HM_ACM_PASSWORD);
		List<Header> headers = new ArrayList<Header>();
		headers.add(new Header("Accept","application/xml"));
		headers.add(new Header("Authorization", "Basic "+Base64.encode(basic)));
		return doPostXMLWithHeaders(url,StrParam,client,headers);
	}
	
	/**
	 * @description Post the XML data to the server
	 * @param url
	 * @param strParam
	 * @param client
	 * @param charset
	 * @return
	 * @author huihe
	 */
	public static ResponseModel doPostXML(String url, String strParam, HttpClient client, String charset){
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			PostMethod method = new PostMethod();
			try {
				if(strParam != null && strParam.length() > 0){
					RequestEntity strReqEntity=new StringRequestEntity(strParam,"text/xml","utf-8");
					method.setRequestEntity(strReqEntity);
//					method.setRequestBody(strParam);
				}
				
				restResponse = sendRequest(url,method,client, charset);
			} catch (Exception e) {
				log.error(e);
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static ResponseModel doPostXMLWithHeader(String url, String strParam, HttpClient client,Header header){
		List<Header> headers = new ArrayList<Header>();
		headers.add(header);
		return doPostXMLWithHeaders(url,strParam,client,headers);
	}
	
	public static ResponseModel doPostXMLWithHeaders(String url, String strParam, HttpClient client,List<Header> headers){
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			PostMethod method = new PostMethod();
			try {
				if(strParam != null){
					RequestEntity strReqEntity=new StringRequestEntity(strParam,"text/xml","utf-8");
					method.setRequestEntity(strReqEntity);
				}
				if(headers != null){
					for(Header header:headers){
						method.addRequestHeader(header);
					}
				}
				restResponse = sendRequest(url,method,client, DEFAULT_CHARSET);
			} catch (Exception e) {
				log.error(e);
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static ResponseModel doPutXML(String url, String strParam, HttpClient client){
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			PutMethod method = new PutMethod();
			try {
				if(strParam != null && strParam.length() > 0){
					RequestEntity strReqEntity=new StringRequestEntity(strParam,"text/xml",DEFAULT_CHARSET);
					method.setRequestEntity(strReqEntity);
				}
				
				restResponse = sendRequest(url,method,client, DEFAULT_CHARSET);
			} catch (Exception e) {
				log.error(e);
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	private static ResponseModel sendRequest(String url,HttpMethod method, HttpClient client, String charset)
			throws Exception {
		ResponseModel restResponse = null;
		if (null != method) {

			try {
				if(url.startsWith("https://"))
				{
					String host=url.substring(0,url.indexOf("/",8));
					url=url.substring(url.indexOf("/",8));
					supportSSL(host,client);
				}
				method.setPath(url);
				int responseCode=client.executeMethod(method);
				restResponse = new ResponseModel();
				restResponse.setResponseCode(responseCode);
				StringBuffer response = new StringBuffer();
				BufferedReader reader = null;
				if(StringUtils.isBlank(charset)){
					charset = DEFAULT_CHARSET;
				}
				try {
					reader = new BufferedReader(new InputStreamReader(
							method.getResponseBodyAsStream(), charset));
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line).append(System.getProperty("line.separator"));
					}
				} finally {
					if (null != reader) {
						reader.close();
					}
					restResponse.setResponseText(response.toString());
				}
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static ResponseModel doPost(String url, Map<String, String> params){
		return doPost(url,params,DEFAULT_CHARSET);
	}
	
	public static ResponseModel doDelete(String url, Map<String, String> params,
			String charset) {
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			HttpMethod method = new DeleteMethod();
			setMethodParams(method,params);
			try {
				restResponse = sendRequest(url,method,charset);
			} catch (Exception e) {
				log.error("HttpToolkit,doDelete()",e.getMessage());
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static ResponseModel doDelete(String url, Map<String, String> params){
		return doDelete(url,params,DEFAULT_CHARSET);
	}
	
	public static ResponseModel doPut(String url, Map<String, String> params,
			String charset) {
		ResponseModel restResponse = null;
		if(StringUtils.isNotBlank(url)){
			HttpMethod method = new PutMethod();
			setMethodParams(method,params);
			try {
				restResponse = sendRequest(url,method,charset);
			} catch (Exception e) {
				log.error("HttpToolkit,doPut()",e.getMessage());
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	
	public static ResponseModel doPut(String url, Map<String, String> params){
		return doPut(url,params,DEFAULT_CHARSET);
	}
	
	public static void setMethodParams(HttpMethod method,Map<String,String> map){
		if(null != map && !map.isEmpty()){
			NameValuePair[] data = new NameValuePair[map.size()];
			int i=0;
			for (Map.Entry<String, String> entry : map.entrySet()) {
				data[i++] = new NameValuePair(entry.getKey(), entry.getValue());
			}
			method.setQueryString(data);
		}
	}
	
	private static ResponseModel sendRequest(String url,HttpMethod method, String charset)
			throws Exception {
		ResponseModel restResponse = null;
		if (null != method) {
			
			try {
				HttpClient client = new HttpClient();
				if(url.startsWith("https://"))
				{
					String host=url.substring(0,url.indexOf("/",8));
					url=url.substring(url.indexOf("/",8));
					supportSSL(host,client);
				}
				method.setPath(url);
				int responseCode=client.executeMethod(method);
				restResponse = new ResponseModel();
				restResponse.setResponseCode(responseCode);
				StringBuffer response = new StringBuffer();
				BufferedReader reader = null;
				if(StringUtils.isBlank(charset)){
					charset = DEFAULT_CHARSET;
				}
				try {
					reader = new BufferedReader(new InputStreamReader(
							method.getResponseBodyAsStream(), charset));
					String line;
					while ((line = reader.readLine()) != null) {
						response.append(line).append(System.getProperty("line.separator"));
					}
				} finally {
					if (null != reader) {
						reader.close();
					}
					restResponse.setResponseText(response.toString());
				}
			} finally {
				method.releaseConnection();
			}
		}
		return restResponse;
	}
	public static void supportSSL(String url, HttpClient client) { 
        if(StringUtils.isBlank(url)) { 
            return; 
        } 
        String siteUrl = StringUtils.lowerCase(url); 
        if (!(siteUrl.startsWith("https"))) { 
            return; 
        } 
       
        try { 
            setSSLProtocol(siteUrl, client); 
        } catch (Exception e) { 
            log.error("setProtocol error ", e); 
        } 
//        Security.setProperty( "ssl.SocketFactory.provider", 
//                "com.ah.be.rest.client.contrib.RestSSLSocketFactory"); 
    } 

    private static void setSSLProtocol(String strUrl, HttpClient client) throws Exception { 
       
        URL url = new URL(strUrl); 
        String host = url.getHost(); 
        int port = url.getPort(); 

        if (port <= 0) { 
            port = 443; 
        } 
        ProtocolSocketFactory factory = new RestSSLSocketFactory(); 
        Protocol authhttps = new Protocol("https", factory, port); 
        Protocol.registerProtocol("https", authhttps); 
        // set https protocol 
        client.getHostConfiguration().setHost(host, port, authhttps); 
    }

}
