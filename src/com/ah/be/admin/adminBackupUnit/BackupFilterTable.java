package com.ah.be.admin.adminBackupUnit;

public class BackupFilterTable {
	/**
	 * The table data need not be backup when hm upgrade or backup database.
	 */
	public static final String[] NO_NEED_BACKUP_FILTER_TABLE = {
		"LICENSE_HISTORY_INFO", "AH_CLIENTSESSION", "hive_ap_update_result", "hive_ap_update_result_item", "ACTIVATION_KEY_INFO",
		"HA_SETTINGS", "DOMAIN_ORDER_KEY_INFO", "order_history_info", "order_ap_info", "HM_LATESTACSPNEIGHBOR",
		"HM_LATESTINTERFERENCESTATS", "HM_LATESTNEIGHBOR", "HM_LATESTRADIOATTRIBUTE", "HM_LATESTXIF","acm_entitle_key_history_info"
	};
	/**
	 * when not in the full backup model, no need backup data table
	 */
	public static final String[] NO_FULL_BACKUP_MODEL_FILTER_TABLE = {
		"AH_ALARM", "AH_EVENT", "HM_ASSOCIATION", "AH_CLIENTSESSION_HISTORY", "HM_NEIGHBOR",
		"HM_RADIOATTRIBUTE", "HM_RADIOSTATS", "HM_VIFSTATS", "HM_XIF", "HM_ACSPNEIGHBOR",
		"HM_BANDWIDTHSENTINEL_HISTORY", "HM_INTERFERENCESTATS", "HM_PCIDATA", "HM_VPNSTATUS", "IDP",
		"hm_client_stats", "hm_interface_stats", "hm_device_stats",
		"hm_device_stats","HM_REPO_APCPUMEM_HOUR","HM_REPO_APCPUMEM_DATE","HM_REPO_APCPUMEM_WEEK","HM_REPO_APCPUMEM_MONTH",
		"hm_repo_app_data_all","hm_repo_app_data_all_last_week","hm_repo_app_data","HM_REPO_APP_DATA_HOUR","HM_REPO_APP_DATA_DATE","HM_REPO_APP_DATA_WEEK","HM_REPO_APP_DATA_MONTH",
		"hm_client_stats","HM_REPO_CLIENT_DATA_HOUR","HM_REPO_CLIENT_DATA_DATE","HM_REPO_CLIENT_DATA_WEEK","HM_REPO_CLIENT_DATA_MONTH",
		"hm_cpu_memory_usage","HM_REPO_HMCPUMEM_HOUR","HM_REPO_HMCPUMEM_DATE","HM_REPO_HMCPUMEM_WEEK","HM_REPO_HMCPUMEM_MONTH",
		"hm_interface_stats","HM_REPO_NETWORK_INTERFACE_HOUR","HM_REPO_NETWORK_INTERFACE_DATE","HM_REPO_NETWORK_INTERFACE_WEEK","HM_REPO_NETWORK_INTERFACE_MONTH",
		"ah_new_sla_stats","HM_REPO_NEWSLA_STATS_HOUR","HM_REPO_NEWSLA_STATS_DATE","HM_REPO_NEWSLA_STATS_WEEK","HM_REPO_NEWSLA_STATS_MONTH",
		"hm_interface_stats","HM_REPO_PORT_INTERFACE_HOUR","HM_REPO_PORT_INTERFACE_DATE","HM_REPO_PORT_INTERFACE_WEEK","HM_REPO_PORT_INTERFACE_MONTH",
		"ssid_clients_count","HM_REPO_SSID_COUNT_DATE","HM_REPO_SSID_COUNT_WEEK","HM_REPO_SSID_COUNT_MONTH",
		"HM_REPO_CLIENT_COUNT_HOUR","HM_REPO_CLIENT_COUNT_DATE","HM_REPO_CLIENT_COUNT_WEEK","HM_REPO_CLIENT_COUNT_MONTH",
		"hm_switch_port_period_stats","HM_REPO_SWITCH_PERIOD_HOUR","HM_REPO_SWITCH_PERIOD_DATE","HM_REPO_SWITCH_PERIOD_WEEK","HM_REPO_SWITCH_PERIOD_MONTH"
	};
	
	/**
	 * when in the full backup model, need backup data table 
	 */
	public static final String[] FULL_BACKUP_MODEL_BACKUP_TABLE = {
		"AH_ALARM", "AH_EVENT", "HM_ASSOCIATION", "AH_CLIENTSESSION_HISTORY", "HM_NEIGHBOR",
		"IDP", "hm_client_stats", "HM_XIF", "HM_RADIOATTRIBUTE", "HM_RADIOSTATS",
		"HM_VIFSTATS", "hm_interface_stats", "hm_device_stats"
	};
	
}
