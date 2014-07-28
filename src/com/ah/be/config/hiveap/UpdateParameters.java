package com.ah.be.config.hiveap;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public interface UpdateParameters {

	public static final int CAPWAP_CONNECT_TIMEOUT = 30; // second
	public static final int DEFAULT_TIMEOUT_MAX = 15 * 60 * 1000; // ms
	public static final int SCRIPT_TIMEOUT_MAX = 15 * 60 * 1000; // ms
	public static final int PSK_TIMEOUT_MAX = 15 * 60 * 1000; // ms
	public static final int BOOTSTRAP_TIMEOUT_MAX = 5 * 60 * 1000; // ms
	public static final int DTLS_TIMEOUT_MAX = 5 * 60 * 1000; // ms
	public static final int IMAGE_TIMEOUT_MAX = 10 * 60 * 1000; // ms
	public static final int CWP_TIMEOUT_MAX = 15 * 60 * 1000; // ms
	public static final int CERTIFICATE_TIMEOUT_MAX = 15 * 60 * 1000;// ms
	public static final int COUNTRYCODE_TIMEOUT_MAX = 5 * 60 * 1000;// ms
	public static final int POE_TIMEOUT_MAX = 5 * 60 * 1000;// ms
	public static final int NETDUMP_TIMEOUT_MAX = 10 * 60 * 1000;// ms
	public static final int IP_DNS_TIMEOUT_MAX = 2 * 60 * 1000;// ms
	public static final int OS_DETECTION_TIMEOUT_MAX = 15 * 60 * 1000; // ms
	public static final int L7_SIGNATURE_TIMEOUT_MAX = 10 * 60 * 1000; // ms
	// clear timer parameters
	public static final int TIMER_DELAY = 10000;
	public static final int TIMER_INTERVAL = 5000; // ms
	public static final int TIMER_DELAY_10MIN = 1000 * 60 * 10;

	// Download script Type
	public static final short COMPLETE_SCRIPT = 0;
	public static final short DELTA_SCRIPT_LAST = 1;
	public static final short DELTA_SCRIPT_RUNNING = 2;

//	public static final int UPDATE_MAX_COUNT = 10;

	// Download Type
	public static final short AH_DOWNLOAD_SCRIPT_WIZARD = 0;
	public static final short AH_DOWNLOAD_IMAGE = 1;
	public static final short AH_DOWNLOAD_SCRIPT = 2;
	public static final short AH_DOWNLOAD_BOOTSTRAP = 3;
	public static final short AH_DOWNLOAD_CWP = 4;
	public static final short AH_DOWNLOAD_RADIUS_CERTIFICATE = 5;
	public static final short AH_DOWNLOAD_COUNTRY_CODE = 6;
	public static final short AH_DOWNLOAD_PSK = 8;
	public static final short AH_DOWNLOAD_POE = 9;
	public static final short AH_DOWNLOAD_VPN_CERTIFICATE = 10;
	public static final short AH_DOWNLOAD_NET_DUMP = 11;
	public static final short AH_DOWNLOAD_IP_NETMASK_GATEWAY_DNS = 12;
	public static final short AH_DOWNLOAD_OUTDOORSTTINGS = 13;
	public static final short AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE = 14;
	public static final short AH_DOWNLOAD_OS_DETECTION = 15;

	public static final short AH_DOWNLOAD_DS_CONFIG = 16;
	public static final short AH_DOWNLOAD_DS_USER_CONFIG = 17;
	public static final short AH_DOWNLOAD_DS_AUDIT_CONFIG = 18;
	public static final short AH_DOWNLOAD_L7_SIGNATURE = 19;

	public static final short AH_DOWNLOAD_REBOOT = 20;
	public static final short AH_DOWNLOAD_CLEAR_INTERFACE_COUNTERS = 21;

	public static EnumItem[] UPDATE_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.update.", new int[] { AH_DOWNLOAD_SCRIPT_WIZARD,
					AH_DOWNLOAD_IMAGE, AH_DOWNLOAD_SCRIPT,
					AH_DOWNLOAD_BOOTSTRAP, AH_DOWNLOAD_CWP,
					AH_DOWNLOAD_RADIUS_CERTIFICATE, AH_DOWNLOAD_COUNTRY_CODE,
					AH_DOWNLOAD_PSK, AH_DOWNLOAD_POE,
					AH_DOWNLOAD_VPN_CERTIFICATE,AH_DOWNLOAD_NET_DUMP,
					AH_DOWNLOAD_IP_NETMASK_GATEWAY_DNS,AH_DOWNLOAD_OUTDOORSTTINGS,
					AH_DOWNLOAD_CLOUDAUTH_CERTIFICATE,AH_DOWNLOAD_OS_DETECTION,
					AH_DOWNLOAD_REBOOT, AH_DOWNLOAD_CLEAR_INTERFACE_COUNTERS});

	public static final short PROCESS_NOT_START = 1;
	public static final short PROCESS_LOADING = 2;
	public static final short PROCESS_EXTRACTING = 3;
	public static final short PROCESS_RETRIEVING = 4;
	public static final short PROCESS_GENERATING = 5;
	public static final short PROCESS_COMPARING = 6;

	public static EnumItem[] PROCESS_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.update.process.", new int[] { PROCESS_NOT_START,
					PROCESS_LOADING, PROCESS_EXTRACTING, PROCESS_RETRIEVING,
					PROCESS_GENERATING, PROCESS_COMPARING });

	public static final short UPDATE_FAILED = 1;
	public static final short UPDATE_SUCCESSFUL = 2;
	public static final short UPDATE_TIMEOUT = 3;
	public static final short UPDATE_ABORT = 4;
	public static final short UPDATE_CANCELED = 5;
	public static final short UPDATE_STAGED = 6;
	public static final short REBOOTING = 7;
	public static final short WARNING = 8;
	public static final short REBOOT_SUCCESSFUL = 9;

	public static EnumItem[] UPDATE_RESULT_TYPE = MgrUtil.enumItems(
			"enum.hiveAp.update.result.", new int[] { UPDATE_FAILED,
					UPDATE_SUCCESSFUL, UPDATE_TIMEOUT, UPDATE_ABORT,
					UPDATE_CANCELED });

	public static final short ACTION_CANCEL = 1;
	public static final short ACTION_REBOOT = 2;
	public static final short ACTION_RETRY = 3;

	// image level settings
	public static final short LEVEL_IMAGE_NO = 0;
	public static final short LEVEL_IMAGE_YES = 1;
	public static final short LEVEL_IMAGE_RISK = 2;

	public static final String SWITCH_IMAGE_REVISION_02 = "02";

	public static final short UPLOAD_TYPE_COMPLETE_CONFIG = 1;
	public static final short UPLOAD_TYPE_COMPLETE_USER = 2;
	public static final short UPLOAD_TYPE_AUDIT_CONFIG = 3;
	public static final short UPLOAD_TYPE_AUDIT_USER = 4;
	public static final short UPLOAD_TYPE_AUDIT_ALL = 5;
}
