/**
 * @filename			SearchParameter.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.be.search;

import com.ah.bo.admin.HmUser;

public class SearchParameter {

	public static final int	TYPE_CONFIGURATION	= 1;

	public static final int	TYPE_HIVEAP			= 2;

	public static final int	TYPE_CLIENT			= 3;

	public static final int	TYPE_FAULT			= 5;

	public static final int	TYPE_ADMIN			= 4;

	public static final int	TYPE_ONLY_MAC		= 6;

	public static final int	TYPE_ONLY_IP		= 7;

	public static final int	TYPE_TOPO			= 8;
	
	public static final int	TYPE_TOOL			= 9;
	
	private String keyword;
	
	private boolean isConfiguration = false;
	
	private boolean isHiveAP = false;
	
	private boolean isClient = false;
	
	private boolean isFault = false;
	
	private boolean isAdmin = false;
	
	private boolean isMac = false;
	
	private boolean isIp = false;
	
	private boolean isTopo = false;
	
	private boolean isFullMode = false;
	
	private boolean isTool = false;
	
	private HmUser userContext;
	
	public SearchParameter() {
		
	}

	/**
	 * getter of keyword
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}

	/**
	 * setter of keyword
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * getter of isConfiguration
	 * @return the isConfiguration
	 */
	public boolean isConfiguration() {
		return isConfiguration;
	}

	/**
	 * setter of isConfiguration
	 * @param isConfiguration the isConfiguration to set
	 */
	public void setConfiguration(boolean isConfiguration) {
		this.isConfiguration = isConfiguration;
	}

	/**
	 * getter of isHiveAP
	 * @return the isHiveAP
	 */
	public boolean isHiveAP() {
		return isHiveAP;
	}

	/**
	 * setter of isHiveAP
	 * @param isHiveAP the isHiveAP to set
	 */
	public void setHiveAP(boolean isHiveAP) {
		this.isHiveAP = isHiveAP;
	}

	/**
	 * getter of isClient
	 * @return the isClient
	 */
	public boolean isClient() {
		return isClient;
	}

	/**
	 * setter of isClient
	 * @param isClient the isClient to set
	 */
	public void setClient(boolean isClient) {
		this.isClient = isClient;
	}

	/**
	 * getter of isFualt
	 * @return the isFualt
	 */
	public boolean isFault() {
		return isFault;
	}

	/**
	 * setter of isFualt
	 * @param isFualt the isFualt to set
	 */
	public void setFault(boolean isFault) {
		this.isFault = isFault;
	}

	/**
	 * getter of isMac
	 * @return the isMac
	 */
	public boolean isMac() {
		return isMac;
	}

	/**
	 * setter of isMac
	 * @param isMac the isMac to set
	 */
	public void setMac(boolean isMac) {
		this.isMac = isMac;
	}

	/**
	 * getter of userContext
	 * @return the userContext
	 */
	public HmUser getUserContext() {
		return userContext;
	}

	/**
	 * setter of userContext
	 * @param userContext the userContext to set
	 */
	public void setUserContext(HmUser userContext) {
		this.userContext = userContext;
	}
	
	public static int getTypeFromString(String type) {
		if(type == null) {
			return 0;
		}
		
		if(type.equalsIgnoreCase("configuration")) {
			return TYPE_CONFIGURATION;
		} else if(type.equalsIgnoreCase("hiveap")) {
			return TYPE_HIVEAP;
		} else if(type.equalsIgnoreCase("client")) {
			return TYPE_CLIENT;
		} else if(type.equalsIgnoreCase("fault")) {
			return TYPE_FAULT;
		} else if(type.equalsIgnoreCase("admin")) {
			return TYPE_ADMIN;
		}  else if(type.equalsIgnoreCase("topo")) {
			return TYPE_TOPO;
		} else if(type.equalsIgnoreCase("tool")) {
			return TYPE_TOOL;
		} else {
			return 0;
		}
	}

	/**
	 * getter of isAdmin
	 * @return the isAdmin
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

	/**
	 * setter of isAdmin
	 * @param isAdmin the isAdmin to set
	 */
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	/**
	 * getter of isIp
	 * @return the isIp
	 */
	public boolean isIp() {
		return isIp;
	}

	/**
	 * setter of isIp
	 * @param isIp the isIp to set
	 */
	public void setIp(boolean isIp) {
		this.isIp = isIp;
	}

	public boolean isTopo() {
		return isTopo;
	}

	public void setTopo(boolean isTopo) {
		this.isTopo = isTopo;
	}

	/**
	 * getter of isFullMode
	 * @return the isFullMode
	 */
	public boolean isFullMode() {
		return isFullMode;
	}

	/**
	 * setter of isFullMode
	 * @param isFullMode the isFullMode to set
	 */
	public void setFullMode(boolean isFullMode) {
		this.isFullMode = isFullMode;
	}

	/**
	 * getter of isTool
	 * @return the isTool
	 */
	public boolean isTool() {
		return isTool;
	}

	/**
	 * setter of isTool
	 * @param isTool the isTool to set
	 */
	public void setTool(boolean isTool) {
		this.isTool = isTool;
	}


	
}
