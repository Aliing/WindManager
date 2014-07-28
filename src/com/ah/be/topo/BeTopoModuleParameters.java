package com.ah.be.topo;

public class BeTopoModuleParameters {

	// map refresh task run delay value
	public static final int MAP_REFRESH_TASK_DELAY = 10;// second

	// map refresh task run interval value
	public static final int MAP_REFRESH_TASK_INTERVAL = 5;// second

	// default map refresh interval in seconds
	public static final int DEFAULT_MAP_REFRESH_INTERVAL = 120;

	// cli request, response manager timer delay
	public static final int CLI_MANAGER_TIMER_DELAY = 1000;
	public static final int CLI_MANAGER_TIMER_INTERVAL = 4000;
	public static final int CAPWAP_CONNECT_TIMEOUT = 20; // second
	public static final int DEFAULT_CLI_TIMEOUT_MAX = 45 * 1000;// second
	public static final int SHOW_LOG_TIMEOUT_MAX = 65 * 1000;// second
	public static final int SHOW_RUNNING_CFG_MAX = 65 * 1000;// second

	// cli request returned value;
	public static final int CAPWAP_SERVER_CLOSED = -1;
	public static final int INVALID_REQUEST_OBJECT = -2;
	public static final int REQUEST_OBJECT_IN_USING = -3;
	public static final int BUILD_REQUEST_FAILED = -4;
	public static final int REQUEST_OBJECT_IN_UPDATE = -5;

	public static final int POLLING_VIA_CAPWAP_TIMEOUT = 30; // second
	/**
	 * time offset delay default value. It is because the configuration via
	 * CAPWAP, and the result event should be send before device reboot.
	 *
	 * Note: range (10 - 59)
	 */
	public static final int DEFAULT_REBOOT_DELAY = 10; // second
	public static final String DEFAULT_REBOOT_OFFSET = "00:00:10";
}
