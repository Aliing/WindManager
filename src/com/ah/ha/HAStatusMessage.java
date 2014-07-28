/**
 * @filename			HAStatusMessage.java
 * @version
 * @author				Administrator
 * @since
 * 
 * Copyright (c) 2006-2009 Aerohive Co., Ltd. 
 * All right reserved.
 */
package com.ah.ha;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class HAStatusMessage {
	
	public static final int	MESSAGE_LENGTH = 12;
	
	public static final int	TYPE_UPDATE	= 1;
	
	public static final int TYPE_QUERY = 2;
	
	public static final int	RESPONSE_SUCCESS = 0;
	
	public static final int	RESPONSE_ERROR = 1;

	private int	type;

	private int	status;

	private int	response;
	
	public HAStatusMessage() {
		
	}
	
	public HAStatusMessage(int status) {
		this.status = status;
	}
	
	public HAStatusMessage(int type, int status, int response) {
		this.type = type;
		this.status = status;
		this.response = response;
	}

	/**
	 * getter of type
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * setter of type
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
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

	/**
	 * getter of response
	 * @return the response
	 */
	public int getResponse() {
		return response;
	}

	/**
	 * setter of response
	 * @param response the response to set
	 */
	public void setResponse(int response) {
		this.response = response;
	}

	public ByteBuffer getBytes() {
		ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_LENGTH);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		buffer.putInt(type);
		buffer.putInt(status);
		buffer.putInt(response);
		buffer.flip();

		return buffer;
	}

	@Override
	public String toString() {
		return new StringBuilder("Message Type: ").append(type)
				.append("; HA Status: ").append(status)
				.append("; Response Code: ").append(response).toString();
	}

}