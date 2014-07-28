package com.ah.be.db.configuration;

import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.util.MgrUtil;

public class ConfigurationResources {

	/*
	 * define Configuration indication pending situations
	 */
	public static final int CONFIG_HIVEAP_INITIAL = 1;
	// Configuration Profiles changes via UI
	public static final int CONFIGURATION_CHANGE = 2;
	// Configuration upload to HiveAP failed (a configuration may be partially
	// effective in delta configuration case)
	public static final int CONFIGURATION_UPLOAD_FAILED = 3;
	// Configuration upload to HiveAP successfully but not effective
	public static final int CONFIGURATION_INEFFICACIOUS = 4;
	// config version mismatch between HM record and HiveAP CAPWAP message
	public static final int CONFIG_VERSION_MISMATCH = 5;
	// Configuration Audit mismatch.
	public static final int CONFIGURATION_AUDIT_MISMATCH = 6;

	public static final int CONFIG_HIVEAP_CHANGE = 7;
	public static final int CONFIG_HIVEAP_NEIGHBOR_CHANGE = 10;

	// Only for LocalUser add/remove/revoke used
	public static final int CONFIGURATION_CREATE = 8;
	public static final int CONFIGURATION_REMOVE = 9;
	public static final int CONFIGURATION_REVOKE = 11;
	
	// for the network resources recycle
	public static final int CONFIG_HIVEAP_NETWORK_RESOURCE_RECYCLE = 12;
	
	//for HM upgrade
	public static final int CONFIG_HIVEAP_HM_UPGRADE = 13;

	public static final int CONFIGURATION_AUDIT_INTERVAL = 10; // in minutes
	public static final int CONFIGURATION_AUDIT_INIT = 1; // in minutes;

	public static final String INDEX_CONFIG_UNKNOWN = "configuration.pending.000";
	public static final String INDEX_CONFIG_HIVEAP_INITIAL = "configuration.pending.001";
	public static final String INDEX_CONFIGURATION_CHANGE = "configuration.pending.002";
	public static final String INDEX_CONFIGURATION_UPLOAD_FAILED = "configuration.pending.003";
	public static final String INDEX_CONFIGURATION_INEFFICACIOUS = "configuration.pending.004";
	public static final String INDEX_CONFIG_VERSION_MISMATCH = "configuration.pending.005";
	public static final String INDEX_CONFIGURATION_AUDIT_MISMATCH = "configuration.pending.006";
	public static final String INDEX_CONFIG_HIVEAP_CHANGE = "configuration.pending.007";
	public static final String INDEX_CONFIGURATION_CREATE = "configuration.pending.008";
	public static final String INDEX_CONFIGURATION_REMOVE = "configuration.pending.009";
	public static final String INDEX_CONFIG_HIVEAP_NEIGHBOR_CHANGE = "configuration.pending.010";
	public static final String INDEX_CONFIGURATION_REVOKE = "configuration.pending.011";
	public static final String INDEX_CONFIG_HIVEAP_NETWORK_RESOURCE_RECYCLE = "configuration.pending.012";
	public static final String INDEX_CONFIG_HIVEAP_HM_UPGRADE = "configuration.pending.013";

	public static final String INDEX_USERDATABASE_HIVEAP_INITIAL = "userdatabase.pending.001";
	public static final String INDEX_USERDATABASE_UPLOAD_FAILED = "userdatabase.pending.003";
	public static final String INDEX_USERDATABASE_AUDIT_MISMATCH = "userdatabase.pending.006";

	public static String getMismatchMessage(int pendingIndex,
			String pendingMsg, ConfigurationType type) {
		String desc = "";
		switch (pendingIndex) {
		case CONFIG_HIVEAP_INITIAL:
			if (type == ConfigurationType.Configuration) {
				desc = MgrUtil.getUserMessage(INDEX_CONFIG_HIVEAP_INITIAL);
			} else if (type == ConfigurationType.UserDatabase) {
				desc = MgrUtil
						.getUserMessage(INDEX_USERDATABASE_HIVEAP_INITIAL);
			}
			break;
		case CONFIGURATION_CHANGE:
			desc = MgrUtil.getUserMessage(INDEX_CONFIGURATION_CHANGE,
					pendingMsg);
			break;
		case CONFIGURATION_UPLOAD_FAILED:
			if (type == ConfigurationType.Configuration) {
				desc = MgrUtil
						.getUserMessage(INDEX_CONFIGURATION_UPLOAD_FAILED);
			} else if (type == ConfigurationType.UserDatabase) {
				desc = MgrUtil.getUserMessage(INDEX_USERDATABASE_UPLOAD_FAILED);
			}
			break;
		case CONFIGURATION_INEFFICACIOUS:
			desc = MgrUtil.getUserMessage(INDEX_CONFIGURATION_INEFFICACIOUS);
			break;
		case CONFIG_VERSION_MISMATCH:
			desc = MgrUtil.getUserMessage(INDEX_CONFIG_VERSION_MISMATCH);
			break;
		case CONFIGURATION_AUDIT_MISMATCH:
			if (type == ConfigurationType.Configuration) {
				desc = MgrUtil
						.getUserMessage(INDEX_CONFIGURATION_AUDIT_MISMATCH);
			} else if (type == ConfigurationType.UserDatabase) {
				desc = MgrUtil
						.getUserMessage(INDEX_USERDATABASE_AUDIT_MISMATCH);
			}
			break;
		case CONFIG_HIVEAP_CHANGE:
			desc = MgrUtil.getUserMessage(INDEX_CONFIG_HIVEAP_CHANGE);
			break;
		case CONFIGURATION_CREATE:
			desc = MgrUtil.getUserMessage(INDEX_CONFIGURATION_CREATE,
					pendingMsg);
			break;
		case CONFIGURATION_REMOVE:
			desc = MgrUtil.getUserMessage(INDEX_CONFIGURATION_REMOVE,
					pendingMsg);
			break;
		case CONFIGURATION_REVOKE:
			desc = MgrUtil.getUserMessage(INDEX_CONFIGURATION_REVOKE,
					pendingMsg);
			break;
		case CONFIG_HIVEAP_NEIGHBOR_CHANGE:
			desc = MgrUtil.getUserMessage(INDEX_CONFIG_HIVEAP_NEIGHBOR_CHANGE);
			break;
        case CONFIG_HIVEAP_NETWORK_RESOURCE_RECYCLE:
            desc = MgrUtil.getUserMessage(INDEX_CONFIG_HIVEAP_NETWORK_RESOURCE_RECYCLE, pendingMsg);
            break;
        case CONFIG_HIVEAP_HM_UPGRADE:
            desc = MgrUtil.getUserMessage(INDEX_CONFIG_HIVEAP_HM_UPGRADE);
            break;
		default:
			desc = MgrUtil.getUserMessage(INDEX_CONFIG_UNKNOWN);
		}
		return desc;
	}
}
