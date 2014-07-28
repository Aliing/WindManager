package com.ah.util;

import javax.servlet.http.HttpServletRequest;

public class HmProxyUtil {
	
	/*
	 * @test for proxy
	 */
	 public static String getClientIp(HttpServletRequest req)
	 {
		 String ip = req.getHeader("x-forwarded-for");
		
		 if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		     ip = req.getHeader("Proxy-Client-IP");
		 }
		 
		 if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		     ip = req.getHeader("WL-Proxy-Client-IP");
		 }
		 
		 if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		     ip = req.getRemoteAddr();
		 }
		 
		 return ip;
	 }

}
