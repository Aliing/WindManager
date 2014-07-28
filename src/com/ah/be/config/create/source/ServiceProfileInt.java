package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;
import com.ah.xml.be.config.AlgValue;

/**
 * 
 * @author zhang
 *
 */
public interface ServiceProfileInt {
	
	public enum ALG_TYPE{
		ftp, sip, tftp
	}
	
	public enum SERVICE_TYPE{
		svp, tcp, udp
	}
	
	//fix bug 26835
	public static final int MAX_SERVER = 71;
	
	public String getServiceGuiName();
	
	public String getApVersion();
	
	public boolean isConfigServices();

	public String getUpdateTime();
	
	public String getServiceName();
	
	public String getProtocolValue();
	
	public boolean isConfigServiceProtocol();
	
	public int getServicePort();
	
	public int getServiceTimeOut();
	
	public boolean isConfigAlg() throws CreateXMLException;
	
	public AlgValue getServiceAlgType();
	
	public boolean isSpecialDefValue();
	
	public boolean isConfigPort();
	
	public boolean isConfigAppid();
	
	public String getAppid();
}
