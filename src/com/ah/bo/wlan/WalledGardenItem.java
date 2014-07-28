/**
 * @filename			WalledGardenItem.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.4R2
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.bo.wlan;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.Range;

import com.ah.bo.network.IpAddress;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * Walled Garden Item
 * 
 * owned by WalledGarden.java
 */
@Embeddable
public class WalledGardenItem {

	private int itemId;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "WALLED_GARDEN_ITEM_SERVER_ID", nullable = true)
	private IpAddress server;
	
	public static final byte SERVICE_ALL = 1;

	public static final byte SERVICE_WEB = 2;

	public static final byte SERVICE_PROTOCOL = 3;
	
	public static EnumItem[] ENUM_SERVICE = MgrUtil
			.enumItems("enum.cwp.walledGarden.service.", new int[] { SERVICE_ALL,
					SERVICE_WEB,
					SERVICE_PROTOCOL});

	public static final short MAX_SERVICE_COUNT = 8;
	
	public static final short MAX_SERVER_COUNT = 63;
	
	private byte service = SERVICE_ALL;
	
	@Range(min=0, max=255)
	private int protocol = -1;
	
	@Range(min=1, max=65535)
	private int port = -1;

	/**
	 * getter of server
	 * @return the server
	 */
	public IpAddress getServer() {
		return server;
	}

	/**
	 * setter of server
	 * @param server the server to set
	 */
	public void setServer(IpAddress server) {
		this.server = server;
	}

	/**
	 * getter of service
	 * @return the service
	 */
	public byte getService() {
		return service;
	}

	/**
	 * setter of service
	 * @param service the service to set
	 */
	public void setService(byte service) {
		this.service = service;
	}

	/**
	 * getter of protocol
	 * @return the protocol
	 */
	public int getProtocol() {
		return protocol;
	}

	/**
	 * setter of protocol
	 * @param protocol the protocol to set
	 */
	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	/**
	 * getter of port
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * setter of port
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * getter of itemId
	 * @return the itemId
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * setter of itemId
	 * @param itemId the itemId to set
	 */
	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
	
	@Transient
	public String getServiceName() {
		for(EnumItem item : ENUM_SERVICE) {
			if(this.service == item.getKey()) {
				return item.getValue();
			}
		}
		
		return "Unknown";
	}
	
	@Transient
	public String getProtocolValue() {
		if(this.protocol == -1) {
			return "&nbsp;";
		} else {
			return String.valueOf(this.protocol);
		}
	}
	
	@Transient
	public String getPortValue() {
		if(this.port == -1) {
			return "&nbsp;";
		} else {
			return String.valueOf(this.port);
		}
	}
	
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(!(obj instanceof WalledGardenItem)) {
			return false;
		}
		
		WalledGardenItem item = (WalledGardenItem)obj;
		
		/*
		 * server
		 */
		if(!item.getServer().equals(this.getServer())) {
			return false;
		}
		
		/*
		 * service
		 */
		if(item.getService() != this.getService()) {
			return false;
		}
		
		/*
		 * protocol
		 */
		if(item.getProtocol() != this.getProtocol()) {
			return false;
		}
		
		/*
		 * port
		 */
		if(item.getPort() != this.getPort()) {
			return false;
		}
		
		return true;
	}
	
	@Transient
	private String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}
	
	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}

}