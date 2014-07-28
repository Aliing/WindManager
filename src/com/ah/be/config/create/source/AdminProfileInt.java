package com.ah.be.config.create.source;

import com.ah.be.config.create.CreateXMLException;

/**
 * 
 * @author zhang
 *
 */
public interface AdminProfileInt {
	
	public String getHiveApGuiName();
	
	public String getHiveApName();
	
	public String getApVersion();

	public boolean isConfigureAdmin();
	
	public boolean isConfigureReaderOnly();
	
	public boolean isConfigureRootAdmin();
	
	public boolean isConfigureManageIp();
	
	public String getUpdateTime();
	
	public int getReaderOnlySize();
	
	public String getReaderOnlyUser(int index);
	
	public String getReaderOnlyPassword(int index);
	
	public String getRootAdminUser();
	
	public String getRootAdminPassword();
	
	public int getManageIpSize() throws CreateXMLException;
	
	public String getManageIpAndMask(int index) throws CreateXMLException;
	
	public boolean isConfigAuthTypeLocal();
	
	public boolean isConfigAuthTypeBoth();
	
	public boolean isConfigAuthTypeRadius();
	
	public boolean isConfigAdminAuth();
	
	public boolean isAuthTypePap();
	
	public boolean isAuthTypeChap();
	
	public boolean isAuthTypeMschapv2();
}
