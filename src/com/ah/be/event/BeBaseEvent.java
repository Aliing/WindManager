/**
 *@filename		BeBaseEvent.java
 *@version
 *@author		Steven
 *@createtime	2007-9-4 10:26:08 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

import java.io.Serializable;

import com.ah.be.app.BaseModule;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeBaseEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * indicae which module generate this event detail module id table place in
	 * class BaseModule
	 */
	private int ModuleId;

	private int EventType;

	public int getMouleId() {
		return ModuleId;
	}

	public void setModuleId(int arg_Id) {
		ModuleId = arg_Id;
	}

	public int getEventType() {
		return EventType;
	}

	public void setEventType(int arg_Type) {
		EventType = arg_Type;
	}

	public boolean isShutdownRequestEvent() {
		return this.ModuleId == BaseModule.ModuleID_BeApp
				&& this.EventType == BeEventConst.Be_App_Shutdown_Request;
	}

	public boolean isEventProcessShutdown() {
		return this.ModuleId == BaseModule.ModuleID_Event
				&& this.EventType == BeEventConst.Be_Event_ShutDown;
	}

	public boolean isFaultProcessShutdown() {
		return this.ModuleId == BaseModule.ModuleID_Fault
				&& this.EventType == BeEventConst.Be_Fault_ShutDown;
	}

	public boolean isTrapMailShutdownEvent() {
		return this.ModuleId == BaseModule.ModuleID_Fault
				&& this.EventType == BeEventConst.Be_TrapMail_ShutDown;
	}

}