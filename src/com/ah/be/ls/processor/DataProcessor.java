/**
 *@filename		DataProcessor.java
 *@version
 *@author		xiaolanbao
 *@createtime	2009-4-7 09:37:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */


package com.ah.be.ls.processor;

public interface DataProcessor {
	
	public boolean is_need_response();
	
	public void init_send_data(Object obj);
	
	public int do_build_packet(byte[] bOut);
	
	public int do_parse_packet(byte[] bInput);
	
	public byte get_response_type();
	
	public Object get_response();
	
	public byte get_packet_type();

}
