package com.ah.util;

public class EnumConstUtil {
	//
	public static final short SERVER_NAME = 1;

	public static final short IP_ADDRESS_NAME = 2;

	public static EnumItem[] enumIpAddressAndName = MgrUtil.enumItems(
			"enum.ipAddressAndName.",
			new int[] { SERVER_NAME, IP_ADDRESS_NAME });

	// logging
	public static final short ENABLE = 1;

	public static final short DISABLE = 2;

	public static EnumItem[] enumLogging = MgrUtil.enumItems("enum.logging.",
			new int[] { ENABLE, DISABLE });

	// qos class
	public static final short QOS_CLASS_BACKGROUND = 0;

	public static final short QOS_CLASS_BEST_EFFORT_2 = 1;

	public static final short QOS_CLASS_BEST_EFFORT_1 = 2;

	public static final short QOS_CLASS_EXCELLENT_EFFORT = 3;

	public static final short QOS_CLASS_CONTROLLED_LOAD = 4;

	public static final short QOS_CLASS_VIDEO = 5;

	public static final short QOS_CLASS_VOICE = 6;

	public static final short QOS_CLASS_NETWORK_CONTROL = 7;

	public static EnumItem[] ENUM_QOS_CLASS = MgrUtil.enumItems(
			"enum.qosClass.", new int[] { QOS_CLASS_NETWORK_CONTROL,
					QOS_CLASS_VOICE, QOS_CLASS_VIDEO,
					QOS_CLASS_CONTROLLED_LOAD, QOS_CLASS_EXCELLENT_EFFORT,
					QOS_CLASS_BEST_EFFORT_1, QOS_CLASS_BEST_EFFORT_2,
					QOS_CLASS_BACKGROUND });

	// show interface command used.
	public static final short INTERFACE_ITEM_ALL = 1;

	public static final short INTERFACE_ITEM_WIFI0 = 2;

	public static final short INTERFACE_ITEM_WIFI1 = 3;

	public static final short INTERFACE_ITEM_ETH0 = 4;

	public static final short INTERFACE_ITEM_MGT0 = 5;

	public static final short INTERFACE_ITEM_WIFI01 = 6;

	public static final short INTERFACE_ITEM_WIFI02 = 7;

	public static final short INTERFACE_ITEM_WIFI03 = 8;

	public static final short INTERFACE_ITEM_WIFI04 = 9;

	public static final short INTERFACE_ITEM_WIFI05 = 10;

	public static final short INTERFACE_ITEM_WIFI06 = 11;

	public static final short INTERFACE_ITEM_WIFI07 = 12;

	public static final short INTERFACE_ITEM_WIFI11 = 13;

	public static final short INTERFACE_ITEM_WIFI12 = 14;

	public static final short INTERFACE_ITEM_WIFI13 = 15;

	public static final short INTERFACE_ITEM_WIFI14 = 16;

	public static final short INTERFACE_ITEM_WIFI15 = 17;

	public static final short INTERFACE_ITEM_WIFI16 = 18;

	public static final short INTERFACE_ITEM_WIFI17 = 19;
	
	public static final short INTERFACE_ITEM_WIFI08 = 21;
	
	public static final short INTERFACE_ITEM_WIFI09 = 22;
	
	public static final short INTERFACE_ITEM_WIFI010 = 23;
	
	public static final short INTERFACE_ITEM_WIFI011 = 24;
	
	public static final short INTERFACE_ITEM_WIFI012 = 25;
	
	public static final short INTERFACE_ITEM_WIFI013 = 26;
	
	public static final short INTERFACE_ITEM_WIFI014 = 27;
	
	public static final short INTERFACE_ITEM_WIFI015 = 28;
	
	public static final short INTERFACE_ITEM_WIFI016 = 29;
	
	public static final short INTERFACE_ITEM_WIFI18 = 30;
	
	public static final short INTERFACE_ITEM_WIFI19 = 31;
	
	public static final short INTERFACE_ITEM_WIFI110 = 32;
	
	public static final short INTERFACE_ITEM_WIFI111 = 33;
	
	public static final short INTERFACE_ITEM_WIFI112 = 34;
	
	public static final short INTERFACE_ITEM_WIFI113 = 35;
	
	public static final short INTERFACE_ITEM_WIFI114 = 36;
	
	public static final short INTERFACE_ITEM_WIFI115 = 37;
	
	public static final short INTERFACE_ITEM_WIFI116 = 38;

	public static EnumItem[] ENUM_INTERFACE_TYPE = MgrUtil.enumItems(
			"enum.interface.type.", new int[] { INTERFACE_ITEM_ALL,
					INTERFACE_ITEM_WIFI0, INTERFACE_ITEM_WIFI1,
					INTERFACE_ITEM_ETH0, INTERFACE_ITEM_MGT0,
					INTERFACE_ITEM_WIFI01, INTERFACE_ITEM_WIFI02,
					INTERFACE_ITEM_WIFI03, INTERFACE_ITEM_WIFI04,
					INTERFACE_ITEM_WIFI05, INTERFACE_ITEM_WIFI06,
					INTERFACE_ITEM_WIFI07, INTERFACE_ITEM_WIFI08,
					INTERFACE_ITEM_WIFI09, INTERFACE_ITEM_WIFI010,
					INTERFACE_ITEM_WIFI011, INTERFACE_ITEM_WIFI012,
					INTERFACE_ITEM_WIFI013, INTERFACE_ITEM_WIFI014,
					INTERFACE_ITEM_WIFI015, INTERFACE_ITEM_WIFI016,
					INTERFACE_ITEM_WIFI11,
					INTERFACE_ITEM_WIFI12, INTERFACE_ITEM_WIFI13,
					INTERFACE_ITEM_WIFI14, INTERFACE_ITEM_WIFI15,
					INTERFACE_ITEM_WIFI16, INTERFACE_ITEM_WIFI17,
					
					INTERFACE_ITEM_WIFI18, INTERFACE_ITEM_WIFI19,
					INTERFACE_ITEM_WIFI110, INTERFACE_ITEM_WIFI111,
					INTERFACE_ITEM_WIFI112, INTERFACE_ITEM_WIFI113,
					INTERFACE_ITEM_WIFI114, INTERFACE_ITEM_WIFI115,
					INTERFACE_ITEM_WIFI116
			});
	
	public static final short ADMIN_USER_AUTHENTICATION_LOCAL = 1;

	public static final short ADMIN_USER_AUTHENTICATION_RADIUS = 2;

	public static final short ADMIN_USER_AUTHENTICATION_BOTH = 3;

	public static EnumItem[] ENUM_ADMIN_USER_AUTH_TYPE = MgrUtil.enumItems(
			"enum.userAuth.type.", new int[] { ADMIN_USER_AUTHENTICATION_LOCAL,
					ADMIN_USER_AUTHENTICATION_RADIUS,
					ADMIN_USER_AUTHENTICATION_BOTH });

	public static final int MAP_ENV_AUTO = -1;

	public static final int MAP_ENV_INDOOR_LINE_OF_SIGHT = 0;

	public static final int MAP_ENV_OUTDOOR_FREE_SPACE = 1;

	public static final int MAP_ENV_OUTDOOR_SUBURBAN = 2;

	public static final int MAP_ENV_WAREHOUSE = 3;

	public static final int MAP_ENV_ENTERPRISE = 4;

	public static final int MAP_ENV_HOSPITAL = 5;

	public static final int MAP_ENV_INDOOR = 6;

	public static final int MAP_ENV_OUTDOOR_URBAN = 7;

	public static EnumItem[] ENUM_MAP_ENV = MgrUtil.enumItems("enum.mapEnv.",
			new int[] { MAP_ENV_AUTO, MAP_ENV_OUTDOOR_FREE_SPACE,
					MAP_ENV_OUTDOOR_SUBURBAN, MAP_ENV_OUTDOOR_URBAN,
					MAP_ENV_ENTERPRISE, MAP_ENV_WAREHOUSE, MAP_ENV_HOSPITAL });

	public static EnumItem[] ENUM_MAP_ENV_PLANNING = MgrUtil.enumItems(
			"enum.mapEnv.", new int[] { MAP_ENV_OUTDOOR_FREE_SPACE,
					MAP_ENV_OUTDOOR_SUBURBAN, MAP_ENV_OUTDOOR_URBAN,
					MAP_ENV_ENTERPRISE, MAP_ENV_WAREHOUSE, MAP_ENV_HOSPITAL });

	public static final short RESTORE_PROTOCOL_LOCAL = 1;

	public static final short RESTORE_PROTOCOL_SCP = 2;

	public static final short RESTORE_PROTOCOL_FTP = 3;

	public static EnumItem[] ENUM_RESTORE_PROTOCOL = MgrUtil.enumItems(
			"enum.restore.protocol.", new int[] { RESTORE_PROTOCOL_LOCAL,
					RESTORE_PROTOCOL_SCP, RESTORE_PROTOCOL_FTP });
	
	public static final short SERVICE_SELECT_OPTION_DEFAULT = 0;
	public static final short SERVICE_SELECT_OPTION_NETWORK = 1;
	public static final short SERVICE_SELECT_OPTION_APPLICATION = 2;
	public static EnumItem[] SERVICE_SELECT_OPTION = MgrUtil.enumItems("enum.service.select.option.", new int[]{
			SERVICE_SELECT_OPTION_DEFAULT,SERVICE_SELECT_OPTION_NETWORK,SERVICE_SELECT_OPTION_APPLICATION});
	
	
}
