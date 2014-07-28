package com.ah.ui.actions;

/*
 * @author Chris Scheers
 */
public interface SessionKeys {
	/*
	 * User context has username of currently logged in user
	 */
	public static final String USER_CONTEXT = "userContext";

	public static final String FULL_SCREEN_MODE = "fullScreenMode";

	/*
	 * the current version of HiveManager
	 */
	public static final String HIVEMANAGER_VERSION = "hivemanagerVersion";

	/*
	 * For all the list pages, use this for sort params if none is specified in
	 * the sort tag.
	 */
	public static final String PAGE_SORTING = "pageSorting";

	/*
	 * Map Container Node being monitored for alarms.
	 */
	public static final String MAP_ALARMS_CACHE = "MapAlarmsCache";

	/*
	 * Map Container Node being monitored for alarms.
	 */
	public static final String MAP_FLOOR_CACHE = "MapFloorCache";

	/*
	 * Cache for paged Alarms/Events list refresh
	 */
	public static final String ALARMS_PAGING_CACHE = "AlarmsPagingCache";

	public static final String EVENTS_PAGING_CACHE = "EventsPagingCache";

	/*
	 * Cache for paged HiveAp list refresh
	 */
	public static final String HIVEAP_PAGING_CACHE = "HiveApPagingCache";

	/*
	 * Cache for paged active client list refresh
	 */
	public static final String CLIENT_PAGING_CACHE = "ClientPagingCache";

	/*
	 * Cache for paged VPN topology refresh
	 */
	public static final String VPN_TOPOLOGY_CACHE = "VpnTopologyCache";

	/*
	 * For context line
	 */
	public static final String UPDATE_CONTEXT = "UPDATE_CONTEXT";

	/*
	 * For selected topology map
	 */
	public static final String SELECTED_MAP_ID = "SELECTED_MAP_ID";

	/*
	 * For heatmaps
	 */
	public static final String MR_HEAT_MAP = "MR_HEAT_MAP";

	/*
	 * For Selected RSSI Threshold
	 */
	public static final String SELECTED_RSSI_THRESHOLD = "SELECTED_RSSI_THRESHOLD";

	/*
	 * For Selected SNR Threshold
	 */
	public static final String SELECTED_SNR_THRESHOLD = "SELECTED_SNR_THRESHOLD";

	/*
	 * For Selected Layers
	 */
	public static final String SELECTED_LAYERS = "SELECTED_LAYERS";

	/*
	 * For selected IDP List View
	 */
	public static final String IDP_SELECTED_LIST_VIEW = "IDP_LIST_VIEW";

	/*
	 * For Rogue AP Category View
	 */
	public static final String IDP_ROGUE_CATEGORY_VIEW = "IDP_ROGUE_CATEGORY_VIEW";

	public static final String IDP_PAGING_CACHE = "IdpPagingCache";

	/*
	 * For IDP List View filters
	 */
	public static final String IDP_LIST_VIEW_FILTERS = "IDP_LIST_VIEW_FILTERS";

	public static final String IDP_LIST_VIEW_CURRENT_FILTERS = "IDP_LIST_VIEW_CURRENT_FILTERS";

	/*
	 * For Spectrum Analysis
	 */
	public static final String SPECTRUM_ANALYSIS_DASHBOARD = "SPECTRUM_ANALYSIS_DASHBOARD";
	
	/*
	 * License Information in title area
	 */
	public static final String LICENSE_INFO_IN_TITLE_AREA = "LICENSE_INFO_IN_TITLE_AREA";
	
	/*
	 * hidden Critical Alarm information pane
	 */
	public static final String CLOSE_CRITICAL_ALARM_MSG= "CLOSE_CRITICAL_ALARM_MSG";
	public static final String DOMAIN_SESSION_KEY = "domain_session_key";
	
	/*
	 * Guided config warning message in express mode 
	 */
	public static final String GUIDED_CONFIG_WARNING_MSG = "GUIDED_CONFIG_WARNING_MSG"; 
	
	/*
	 * Guided config SSID form changed
	 */
	public static final String GUIDED_CONFIG_FORM_CHANGE = "GUIDED_CONFIG_FORM_CHANGE";
	
	/*
	 * Manual mode rogue ap mitigation
	 */
	public static final String MONITOR_MANUAL_ROGUE_AP_MITIGATE = "MONITOR_MANUAL_ROGUE_AP_MITIGATE";
	//-------- For Notification Message ---start--//
	public static final String NOTIFICATION_MESSAGE_POOL = "NOTIFICATION_MESSAGE_POOl";
	
	public static final String PREV_MENU_OPERATION = "PREV_MENU_OPERATION";
	
	public static final String NOTIFICATION_MESSAGE_DISPLAY_NUM = "NOTIFICATION_MESSAGE_DISPLAY_NUM";
	
	public static final String NOTIFICATION_MESSAGE_ENABLEACM = "NOTIFICATION_MESSAGE_ENABLEACM";
	//-------- For Notification Message ---end--//
	
	public static final String UPLOAD_CONFIG_IDM_KEY = "uploadConfig_IDM";
	
	// For vHM customer information
	public static final String VHM_CUSTOMER_INFO_KEY = "VHM_CUSTOMER_INFO_KEY";
	// For IDM trial settings
	public static final String IDM_TRIAL_INFO_KEY = "IDM_TRIAL_INFO_KEY";
	
	//For the CSRF attack
	public static final String SESSION_TOKEN_FOR_CSRF_ATTACK = "SESSION_TOKEN_FOR_CSRF_ATTACK";
}
