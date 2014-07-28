/**
 *@filename		BeEventConst.java
 *@version
 *@author		Steven
 *@createtime	2007-9-11 08:34:25
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.event;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class BeEventConst {

	/*
	 * Warnning!!!! Event type 0--30 is resvered for module internal use pls
	 * define you own moudle event from 31
	 */

	/*
	 * common event for every module
	 */
	public static final int Be_Module_ChangeToInitFinishedStatus = 1;

	public static final int Be_Module_ChangeToIdleStatus = 2;

	public static final int Be_Module_ChangeToBusyStatus = 3;

	public static final int Be_Module_ChangeToErrorStatus = 4;

	public static final int Be_Module_ChangeToShutdownStatus = 5;

	/*
	 * Module Name--HmBeApp EventType Description 1 Application shutdown
	 */
	public static final int Be_App_Shutdown_Request = 31;

	/*
	 * Module Name--BeSnmpModule EventType Description
	 */
	public static final int Be_Snmp_RecTrap = 31;

	/*
	 * Module Name--BeEventModule EventType Description
	 */

	public static final int Be_Event_ShutDown = 31;

	/*
	 * Module Name--BeFaultModule EventType Description
	 */

	public static final int Be_Fault_ShutDown = 31;
	public static final int Be_TrapMail_ShutDown = 32;

	// communication event
	/**
	 * IF event type is it, you should convert obj'class to
	 * BeCommunicationEvent. it's special, so let's define it's value zero.
	 */
	public static final int COMMUNICATIONEVENTTYPE = 33;

	/**
	 * notify other module that ip have changed.
	 */
//	public static final int AH_HM_IP_CHANGE = 34;
	//
	// public static final int MAP_REFRESH_INTERVAL_CHANGED = 35;

	public static final int HIVE_AP_MANAGE_STATUS_CHANGED = 36;

	// public static final int AH_CONFIG_VERSION_CHANGE_EVENT = 37;

	public static final int AH_CONFIGURATION_CHANGE_EVENT = 38;

	public static final int AH_IMAGE_EVENT = 50;

	public static final int AH_IMAGE_UPDATE_EVENT = 51;
	
	public static final int AH_CLOUD_AUTH_UPDATE_EVENT = 52;

	public static final int AH_CONFIG_EVENT = 60;

	public static final int AH_CONFIG_GENERATED_EVENT = 61;

	public static final int AH_DELTA_CONFIG_GENERATED_EVENT = 62;

	public static final int AH_BOOTSTRAP_GENERATED_EVENT = 63;

	public static final int AH_CONFIG_UPDATED_EVENT = 64;

	public static final int AH_CONFIG_GENERATION_PROGRESS_EVENT = 65;

	public static final int AH_DISCOVERY_EVENT = 70;

	public static final int BE_SYSTEM_LOG_EVENT = 88;

	public static final int AH_TIMEOUT_EVENT = 99;

	public static final int AH_SHUTDOWN_EVENT = 100;

	public static final int AH_IDP_MITIGATE_EVENT = 101;
	
	public static final int AH_DEVICE_REBOOT_RESULT_EVENT = 102;

	/**
	 * for client session
	 */
	public static final int AH_CLIENT_SESSION_REMOVE_EVENT = 200;

}