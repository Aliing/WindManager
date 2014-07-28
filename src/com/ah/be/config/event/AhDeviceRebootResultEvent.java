package com.ah.be.config.event;

import com.ah.be.event.BeBaseEvent;
import com.ah.be.event.BeEventConst;

public class AhDeviceRebootResultEvent extends BeBaseEvent {

	private static final long serialVersionUID = 1L;
	
	public static final short RESULT_TYPE_SUCCESSFUL				= 0;
	public static final short RESULT_TYPE_CLI_ERROR					= 1;
	public static final short RESULT_TYPE_CONFIG_ROLLBACK			= 2;
	public static final short RESULT_TYPE_IMAGE_ROLLBACK			= 3;
	
	public static final short OPERATION_UPDATE_RESULT				= 0;
	public static final short OPERATION_UPDATE_MESSAGE				= 1;
	
	private String deviceMac;
	
	private short operation;
	
	private short resultType;
	
	private String message;
	
	public AhDeviceRebootResultEvent(){
		super.setEventType(BeEventConst.AH_DEVICE_REBOOT_RESULT_EVENT);
	}

	public String getDeviceMac() {
		return deviceMac;
	}

	public void setDeviceMac(String deviceMac) {
		this.deviceMac = deviceMac;
	}

	public short getOperation() {
		return operation;
	}

	public void setOperation(short operation) {
		this.operation = operation;
	}

	public short getResultType() {
		return resultType;
	}

	public void setResultType(short resultType) {
		this.resultType = resultType;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
