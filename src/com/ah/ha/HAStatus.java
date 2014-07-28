/**
 * @filename			HAStatus.java
 * @version				1.0
 * @author				Joseph Chen
 * @since				3.3
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha;

public class HAStatus {

	public static final short STATUS_UNKNOWN			=	0;
	
	public static final short STATUS_STAND_ALONG		=	1;
	
	public static final short STATUS_HA_MASTER			=	2;
	
	public static final short STATUS_HA_SLAVE			=	3;
	
	public static final short STATUS_SHUT_DOWN			=	10000;
	
	private int status;
	
	public HAStatus() {
		
	}
	
	public HAStatus(int status) {
		this.status = status;
	}
	
	/**
	 * getter of status
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * setter of status
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof HAStatus && ((HAStatus) other).getStatus() == status;
	}
	
	@Override
	public String toString() {
		return String.valueOf(status);
	}
	
	public static String convert(int status) {
		String terms;

		switch (status) {
			case STATUS_STAND_ALONG:
				terms = "Standalone";
				break;
			case STATUS_HA_MASTER:
				terms = "Master";
				break;
			case STATUS_HA_SLAVE:
				terms = "Slave";
				break;
			case STATUS_SHUT_DOWN:
				terms = "Shutdown";
				break;
			case STATUS_UNKNOWN:
			default:
				terms = "Unknown";
				break;
		}
		
		return terms;
	}

}