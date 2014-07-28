package com.ah.bo.hiveap;

import java.io.Serializable;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.xml.deviceProperties.DeviceObj;
import com.ah.xml.deviceProperties.DevicePropertyAttrObj;
import com.ah.xml.deviceProperties.DevicePropertyAttrOptionObj;

/*
 * modification history
 *
 * add constants for ethernet bind interface and binding role
 * joseph chen , 04/10/2008
 */

public interface AhInterface extends Serializable {
	public short getOperationMode();

	public void setOperationMode(short operationMode);

	public short getAdminState();

	public void setAdminState(short adminState);

	public static final short DEVICE_IF_TYPE_WIFI0 = 0;
	public static final short DEVICE_IF_TYPE_WIFI1 = 1;
	public static final short DEVICE_IF_TYPE_AGG0 = 2;
	public static final short DEVICE_IF_TYPE_RED0 = 3;

	public static final short DEVICE_IF_TYPE_USB = 3000;
	public static final short DEVICE_IF_TYPE_ETH0 = 4;
	public static final short DEVICE_IF_TYPE_ETH1 = 5;
	public static final short DEVICE_IF_TYPE_ETH2 = 6;
	public static final short DEVICE_IF_TYPE_ETH3 = 7;
	public static final short DEVICE_IF_TYPE_ETH4 = 8;
	public static final short DEVICE_IF_TYPE_ETH5 = 9;
	public static final short DEVICE_IF_TYPE_ETH6 = 10;
	public static final short DEVICE_IF_TYPE_ETH7 = 11;
	public static final short DEVICE_IF_TYPE_ETH8 = 12;
	public static final short DEVICE_IF_TYPE_ETH9 = 13;
	public static final short DEVICE_IF_TYPE_ETH10 = 14;
	public static final short DEVICE_IF_TYPE_ETH11 = 15;
	public static final short DEVICE_IF_TYPE_ETH12 = 16;
	public static final short DEVICE_IF_TYPE_ETH13 = 17;
	public static final short DEVICE_IF_TYPE_ETH14 = 18;
	public static final short DEVICE_IF_TYPE_ETH15 = 19;
	public static final short DEVICE_IF_TYPE_ETH16 = 20;
	public static final short DEVICE_IF_TYPE_ETH17 = 21;
	public static final short DEVICE_IF_TYPE_ETH18 = 22;
	public static final short DEVICE_IF_TYPE_ETH19 = 23;
	public static final short DEVICE_IF_TYPE_ETH20 = 24;
	public static final short DEVICE_IF_TYPE_ETH21 = 25;
	public static final short DEVICE_IF_TYPE_ETH22 = 26;
	public static final short DEVICE_IF_TYPE_ETH23 = 27;
	public static final short DEVICE_IF_TYPE_ETH24 = 28;
	public static final short DEVICE_IF_TYPE_ETH25 = 29;
	public static final short DEVICE_IF_TYPE_ETH26 = 30;
	public static final short DEVICE_IF_TYPE_ETH27 = 31;
	public static final short DEVICE_IF_TYPE_ETH28 = 32;
	public static final short DEVICE_IF_TYPE_ETH29 = 33;
	public static final short DEVICE_IF_TYPE_ETH30 = 34;
	public static final short DEVICE_IF_TYPE_ETH31 = 35;
	public static final short DEVICE_IF_TYPE_ETH32 = 36;
	public static final short DEVICE_IF_TYPE_ETH33 = 37;
	public static final short DEVICE_IF_TYPE_ETH34 = 38;
	public static final short DEVICE_IF_TYPE_ETH35 = 39;
	public static final short DEVICE_IF_TYPE_ETH36 = 40;
	public static final short DEVICE_IF_TYPE_ETH37 = 41;
	public static final short DEVICE_IF_TYPE_ETH38 = 42;
	public static final short DEVICE_IF_TYPE_ETH39 = 43;
	public static final short DEVICE_IF_TYPE_ETH40 = 44;
	public static final short DEVICE_IF_TYPE_ETH41 = 45;
	public static final short DEVICE_IF_TYPE_ETH42 = 46;
	public static final short DEVICE_IF_TYPE_ETH43 = 47;
	public static final short DEVICE_IF_TYPE_ETH44 = 48;
	public static final short DEVICE_IF_TYPE_ETH45 = 49;
	public static final short DEVICE_IF_TYPE_ETH46 = 50;
	public static final short DEVICE_IF_TYPE_ETH47 = 51;
	public static final short DEVICE_IF_TYPE_ETH48 = 52;
	public static final short DEVICE_IF_TYPE_ETH49 = 53;
	public static final short DEVICE_IF_TYPE_ETH50 = 54;
	public static final short DEVICE_IF_TYPE_ETH51 = 55;
	public static final short DEVICE_IF_TYPE_ETH52 = 56;
	
//	public static final short DEVICE_IF_TYPE_SFP1 = 1001;
//	public static final short DEVICE_IF_TYPE_SFP2 = 1002;
//	public static final short DEVICE_IF_TYPE_SFP3 = 1003;
//	public static final short DEVICE_IF_TYPE_SFP4 = 1004;

	public static final short DEVICE_IF_TYPE_PORT_CHANNEL_0 = 2000;
	public static final short DEVICE_IF_TYPE_PORT_CHANNEL_999 = 2999;

	public static final short ADMIN_STATE_UP = 0;
	public static final short ADMIN_STATE_DOWM = 1;

	public static EnumItem[] ADMIN_STATE_TYPE = MgrUtil.enumItems(
			"enum.interface.adminState.", new int[] { ADMIN_STATE_UP,
					ADMIN_STATE_DOWM });

	public static final short CONNECTION_DHCP = 1;
	public static final short CONNECTION_STATICIP = 2;
	public static final short CONNECTION_PPPOE = 3;
	public static EnumItem[] CONNECTION_TYPE1 = MgrUtil.enumItems(
			"enum.interface.connection.type.", new int[] { CONNECTION_DHCP,
					CONNECTION_STATICIP, CONNECTION_PPPOE });
	public static EnumItem[] CONNECTION_TYPE2 = MgrUtil.enumItems(
			"enum.interface.connection.type.", new int[] { CONNECTION_DHCP,
					CONNECTION_STATICIP });

	public static final short PRIORITY_PRIMARY = 4;

	
	public static final short OPERATION_MODE_ACCESS = 1;
	public static final short OPERATION_MODE_BACKHAUL = 2;
	public static final short OPERATION_MODE_BRIDGE = 3;
	public static final short OPERATION_MODE_DUAL = 4;
	public static final short OPERATION_MODE_WAN= 5;
	public static final short OPERATION_MODE_WAN_ACCESS = 6;
	public static final short OPERATION_MODE_SENSOR= 7;

	public static final short CHANNEL_BG_AUTO = 0;
	public static final short CHANNEL_BG_1 = 1;
	public static final short CHANNEL_BG_2 = 2;
	public static final short CHANNEL_BG_3 = 3;
	public static final short CHANNEL_BG_4 = 4;
	public static final short CHANNEL_BG_5 = 5;
	public static final short CHANNEL_BG_6 = 6;
	public static final short CHANNEL_BG_7 = 7;
	public static final short CHANNEL_BG_8 = 8;
	public static final short CHANNEL_BG_9 = 9;
	public static final short CHANNEL_BG_10 = 10;
	public static final short CHANNEL_BG_11 = 11;
	public static final short CHANNEL_BG_12 = 12;
	public static final short CHANNEL_BG_13 = 13;

	public static final short CHANNEL_A_AUTO = 0;
	public static final short CHANNEL_A_8 = 8;
	public static final short CHANNEL_A_12 = 12;
	public static final short CHANNEL_A_16 = 16;
	public static final short CHANNEL_A_20 = 20;
	public static final short CHANNEL_A_30 = 30;
	public static final short CHANNEL_A_34 = 34;
	public static final short CHANNEL_A_36 = 36;
	public static final short CHANNEL_A_38 = 38;
	public static final short CHANNEL_A_40 = 40;
	public static final short CHANNEL_A_42 = 42;
	public static final short CHANNEL_A_44 = 44;
	public static final short CHANNEL_A_46 = 46;
	public static final short CHANNEL_A_48 = 48;
	public static final short CHANNEL_A_50 = 50;
	public static final short CHANNEL_A_52 = 52;// DFS Channel
	public static final short CHANNEL_A_56 = 56;// DFS Channel
	public static final short CHANNEL_A_60 = 60;// DFS Channel
	public static final short CHANNEL_A_64 = 64;// DFS Channel
	public static final short CHANNEL_A_70 = 70;
	public static final short CHANNEL_A_80 = 80;
	public static final short CHANNEL_A_100 = 100;// DFS Channel
	public static final short CHANNEL_A_104 = 104;// DFS Channel
	public static final short CHANNEL_A_108 = 108;// DFS Channel
	public static final short CHANNEL_A_112 = 112;// DFS Channel
	public static final short CHANNEL_A_116 = 116;// DFS Channel
	public static final short CHANNEL_A_120 = 120;// DFS Channel
	public static final short CHANNEL_A_124 = 124;// DFS Channel
	public static final short CHANNEL_A_128 = 128;// DFS Channel
	public static final short CHANNEL_A_132 = 132;// DFS Channel
	public static final short CHANNEL_A_136 = 136;// DFS Channel
	public static final short CHANNEL_A_140 = 140;// DFS Channel
	public static final short CHANNEL_A_149 = 149;
	public static final short CHANNEL_A_152 = 152;
	public static final short CHANNEL_A_153 = 153;
	public static final short CHANNEL_A_157 = 157;
	public static final short CHANNEL_A_160 = 160;
	public static final short CHANNEL_A_161 = 161;
	public static final short CHANNEL_A_165 = 165;
	public static final short CHANNEL_A_184 = 184;
	public static final short CHANNEL_A_188 = 188;
	public static final short CHANNEL_A_192 = 192;
	public static final short CHANNEL_A_196 = 196;
	
	public static final short CHANNEL_OFFSET_AUTO = -1;
	public static final short CHANNEL_OFFSET_0 = 0;
	public static final short CHANNEL_OFFSET_1 = 1;
	public static final short CHANNEL_OFFSET_2 = 2;
	public static final short CHANNEL_OFFSET_3 = 3;

	public static final short POWER_AUTO = 0;
	public static final short POWER_1 = 1;
	public static final short POWER_2 = 2;
	public static final short POWER_3 = 3;
	public static final short POWER_4 = 4;
	public static final short POWER_5 = 5;
	public static final short POWER_6 = 6;
	public static final short POWER_7 = 7;
	public static final short POWER_8 = 8;
	public static final short POWER_9 = 9;
	public static final short POWER_10 = 10;
	public static final short POWER_11 = 11;
	public static final short POWER_12 = 12;
	public static final short POWER_13 = 13;
	public static final short POWER_14 = 14;
	public static final short POWER_15 = 15;
	public static final short POWER_16 = 16;
	public static final short POWER_17 = 17;
	public static final short POWER_18 = 18;
	public static final short POWER_19 = 19;
	public static final short POWER_20 = 20;

	//default downstream bandwidth for BR100 like device
	public static final String ETH0_DEVICE_DOWNSTREAM_BANDWIDTH="1500 kbps";
	public static final String USB_DEVICE_DOWNSTREAM_BANDWIDTH="384 kbps";
	
	public static final int BR_MAX_ROUTE_COUNT = 127;

	public static EnumItem[] POWER_TYPE = MgrUtil.enumItems(
			"enum.interface.power.",
			new int[] { POWER_AUTO, POWER_1, POWER_2, POWER_3, POWER_4,
					POWER_5, POWER_6, POWER_7, POWER_8, POWER_9, POWER_10,
					POWER_11, POWER_12, POWER_13, POWER_14, POWER_15, POWER_16,
					POWER_17, POWER_18, POWER_19, POWER_20 });

	public static final short RADIO_MODE_BG = 1;
	public static final short RADIO_MODE_A = 2;
	public static final short RADIO_MODE_NA = 4;
	public static final short RADIO_MODE_NG = 5;
	public static final short RADIO_MODE_AC = 6;

	public static EnumItem[] RADIO_MODE_TYPE = MgrUtil.enumItems(
			"enum.interface.radio.mode.", new int[] { RADIO_MODE_BG,
					RADIO_MODE_A });

	public static final short ETH_SPEED_AUTO = 0;
	public static final short ETH_SPEED_10M = 1;
	public static final short ETH_SPEED_100M = 2;
	public static final short ETH_SPEED_1000M = 3;
	public static final short ETH_SPEED_10000M = 4;

	public static final short ETH_PSE_SHUTDOWN = 0;
	public static final short ETH_PSE_8023af = 1;
	public static final short ETH_PSE_8023at = 2;
	public static final short ETH_PSE_8023af_EXTENDED = 3;
	
	public static final String ETH_PSE_PRIORITY_ETH1="0";
	public static final String ETH_PSE_PRIORITY_ETH2="1";
	
	public static final short ROLE_LAN = 0;
	public static final short ROLE_PRIMARY = 1;
	public static final short ROLE_BACKUP = 2;
	public static final short ROLE_WAN = 3;
	
	public static EnumItem[] ROLE_TYPE = MgrUtil.enumItems(
			"enum.interface.role.", new int[] { ROLE_PRIMARY, ROLE_BACKUP });

	public static EnumItem[] ROLE_TYPE_WAN_LAN = MgrUtil.enumItems(
			"enum.interface.role.", new int[] { ROLE_LAN, ROLE_WAN });

	public static EnumItem[] ETH_SPEED_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.speed.", new int[] { ETH_SPEED_AUTO,
					ETH_SPEED_10M, ETH_SPEED_100M, ETH_SPEED_1000M });

	public static EnumItem[] ETH_SPEED_SFP_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.speed.", new int[] { ETH_SPEED_AUTO,  ETH_SPEED_1000M });
	
	public static EnumItem[] ETH_SPEED_10G_ONLY_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.speed.", new int[] {ETH_SPEED_10000M });
	
	public static EnumItem[] ETH_SPEED_ONLY_AUTO_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.speed.", new int[] {ETH_SPEED_AUTO});

	public static EnumItem[] ETH_PSE_TYPE = MgrUtil.enumItems(
			"enum.interface.eth.pse.", new int[] { ETH_PSE_SHUTDOWN,
					ETH_PSE_8023af, ETH_PSE_8023at });
	
	public static EnumItem[] ETH_PSE_TYPE_ONLY_TYPE = MgrUtil.enumItems(
				"enum.interface.eth.pse.", new int[] {
						AhInterface.ETH_PSE_8023af, AhInterface.ETH_PSE_8023af_EXTENDED, AhInterface.ETH_PSE_8023at });
	
	public static EnumItem[] ETH_PSE_WITHOUT_AT_TYPE_ONLY_TYPE = MgrUtil.enumItems(
			"enum.interface.eth.pse.", new int[] {
					AhInterface.ETH_PSE_8023af, AhInterface.ETH_PSE_8023af_EXTENDED});

	public static final short ETH_DUPLEX_AUTO = 0;
	public static final short ETH_DUPLEX_HALF = 1;
	public static final short ETH_DUPLEX_FULL = 2;
	public static EnumItem[] ETH_DUPLEX_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.duplex.", new int[] { ETH_DUPLEX_AUTO,
					ETH_DUPLEX_HALF, ETH_DUPLEX_FULL });
	
	public static EnumItem[] ETH_DUPLEX_SFP_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.duplex.", new int[] { ETH_DUPLEX_AUTO,ETH_DUPLEX_FULL });
	
	public static EnumItem[] ETH_DUPLEX_FULL_ONLY_TYPE = MgrUtil.enumItems(
			"enum.interface.eth0.duplex.", new int[] {ETH_DUPLEX_FULL});

	public static final short ETH_BIND_IF_NULL = 0;
	public static final short ETH_BIND_IF_RED0 = 1;
	public static final short ETH_BIND_IF_AGG0 = 2;
	public static EnumItem[] ETH_BIND_IF = MgrUtil.enumItems(
			"enum.interface.bind.if.", new int[] { ETH_BIND_IF_NULL,
					ETH_BIND_IF_RED0, ETH_BIND_IF_AGG0 });

	public static final short ETH_BIND_ROLE_NULL = 0;
	public static final short ETH_BIND_ROLE_PRI = 1;
	public static EnumItem[] ETH_BIND_ROLE = MgrUtil.enumItems(
			"enum.interface.bind.role.", new int[] { ETH_BIND_ROLE_NULL,
					ETH_BIND_ROLE_PRI });

	public static final short ETHX_DEVICE_INTERFACE_ROLE_LAN = 0;
	public static final short ETHX_DEVICE_INTERFACE_ROLE_WAN_PRIMARY = 1;
	public static final short ETHX_DEVICE_INTERFACE_ROLE_WAN_BACKUP1 = 2;
	public static EnumItem[] ETHX_DEVICE_INTERFACE_ROLE = MgrUtil.enumItems(
			"enum.interface.ethx.device.interface.role.", new int[] { ETHX_DEVICE_INTERFACE_ROLE_LAN
					});
	
	// types of pse status
	public static final byte PSE_STATUS_DISABLED = 0;
	public static final byte PSE_STATUS_SEARCHING = 1;
	public static final byte PSE_STATUS_DELIVERING = 2;
	public static final byte PSE_STATUS_TEST = 3;
	public static final byte PSE_STATUS_FAULT = 4;
	public static final byte PSE_STATUS_OTHER_FAULT = 5;
	public static final byte PSE_STATUS_REQUESTING = 6;
	public static final byte PSE_STATUS_TURN_OFF_BY_PM = 7;
	
	// types of pse pdType
	public static final byte PSE_PDTYPE_NONE = 0;
	public static final byte PSE_PDTYPE_8023AF = 1;
	public static final byte PSE_PDTYPE_8023AT = 2;
	public static final byte PSE_PDTYPE_INVALID = 3;
	
	// PSE powered cutoff priority
	public static final byte PSE_POWERED_CUTOFF_PRIORITY_LOW = 0;
	public static final byte PSE_POWERED_CUTOFF_PRIORITY_HIGH = 1;
	public static final byte PSE_POWERED_CUTOFF_PRIORITY_NORMAL = 2;
	public static final byte PSE_POWERED_CUTOFF_PRIORITY_CRITICAL = 3;
	
//	public static final byte PSE_POWER_THRESHOLD_CLASSBASE=1;
//	public static final byte PSE_POWER_THRESHOLD_USERDEFINE=2;
	
	//type of auto refresh interval
	public static final short AUTO_REFRESH_INTERVAL_5SECONDS = 5;
	public static final short AUTO_REFRESH_INTERVAL_10SECONDS = 10;
	public static final short AUTO_REFRESH_INTERVAL_30SECONDS = 30;
	public static final short AUTO_REFRESH_INTERVAL_1MINUTES = 60;
	public static final short AUTO_REFRESH_INTERVAL_2MINUTES = 120;
	public static final short AUTO_REFRESH_INTERVAL_5MINUTES = 300;
	
	public static EnumItem[] AUTO_REFRESH_INTERVAL = MgrUtil.enumItems(
			"enum.config.refresh.interval.", new int[] {AUTO_REFRESH_INTERVAL_5SECONDS,
					AUTO_REFRESH_INTERVAL_10SECONDS,AUTO_REFRESH_INTERVAL_30SECONDS,
					AUTO_REFRESH_INTERVAL_1MINUTES,AUTO_REFRESH_INTERVAL_2MINUTES,
					AUTO_REFRESH_INTERVAL_5MINUTES
					});
	
	public static final short FLOW_CONTROL_STATUS_AUTO				=1;
	public static final short FLOW_CONTROL_STATUS_ENABLE 			=2;
	public static final short FLOW_CONTROL_STATUS_DISABLE 			=3;
	public static EnumItem[] FLOW_CONTROL_TYPE = MgrUtil.enumItems(
			"enum.switch.interface.flowControl.", new int[] {FLOW_CONTROL_STATUS_DISABLE, 
					FLOW_CONTROL_STATUS_ENABLE, FLOW_CONTROL_STATUS_AUTO
	});
	
	public static EnumItem[] FLOW_CONTROL_TYPE_SFP = MgrUtil.enumItems(
			"enum.switch.interface.flowControl.", new int[] {
					FLOW_CONTROL_STATUS_DISABLE, FLOW_CONTROL_STATUS_ENABLE
	});
	
	public static class DeviceInfUnionType{
		private DeviceInfType deviceInfType;
		private int index;
		
		public DeviceInfType getDeviceInfType() {
			return deviceInfType;
		}
		public void setDeviceInfType(DeviceInfType deviceInfType) {
			this.deviceInfType = deviceInfType;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		
		public String getCLIName(short hiveApModel){
			return deviceInfType.getCLIName(index, hiveApModel);
		}
	}
	
	public static enum DeviceInfType{
		Gigabit, SFP, PortChannel, USB;
		
		public String getCLIName(int index, short hiveApModel){
			if(this == Gigabit){
				if(NmsUtil.getDeviceInfo(hiveApModel).isSptEthernetMore_24()){
					return "eth1/"+index;
				}else{
					return "eth"+index;
				}
			}else if(this == SFP){
				if(index > 0 && index < 5){
					int[] sfpIndexs = getSfpIndexArray(hiveApModel);
					return "eth1/"+sfpIndexs[index - 1];
				}else{
					return "eth1/"+index;
				}
			}else if(this == USB){
				return "usbnet"+index;
			}else if(this == PortChannel){
				return "agg"+index;
			}else{
				return null;
			}
		}

		public short getFinalValue(int index, short hiveApModel) {
			if(this == Gigabit){
				switch(index){
				case 1:
					return AhInterface.DEVICE_IF_TYPE_ETH1;
				case 2:
					return AhInterface.DEVICE_IF_TYPE_ETH2;
				case 3:
					return AhInterface.DEVICE_IF_TYPE_ETH3;
				case 4:
					return AhInterface.DEVICE_IF_TYPE_ETH4;
				case 5:
					return AhInterface.DEVICE_IF_TYPE_ETH5;
				case 6:
					return AhInterface.DEVICE_IF_TYPE_ETH6;
				case 7:
					return AhInterface.DEVICE_IF_TYPE_ETH7;
				case 8:
					return AhInterface.DEVICE_IF_TYPE_ETH8;
				case 9:
					return AhInterface.DEVICE_IF_TYPE_ETH9;
				case 10:
					return AhInterface.DEVICE_IF_TYPE_ETH10;
				case 11:
					return AhInterface.DEVICE_IF_TYPE_ETH11;
				case 12:
					return AhInterface.DEVICE_IF_TYPE_ETH12;
				case 13:
					return AhInterface.DEVICE_IF_TYPE_ETH13;
				case 14:
					return AhInterface.DEVICE_IF_TYPE_ETH14;
				case 15:
					return AhInterface.DEVICE_IF_TYPE_ETH15;
				case 16:
					return AhInterface.DEVICE_IF_TYPE_ETH16;
				case 17:
					return AhInterface.DEVICE_IF_TYPE_ETH17;
				case 18:
					return AhInterface.DEVICE_IF_TYPE_ETH18;
				case 19:
					return AhInterface.DEVICE_IF_TYPE_ETH19;
				case 20:
					return AhInterface.DEVICE_IF_TYPE_ETH20;
				case 21:
					return AhInterface.DEVICE_IF_TYPE_ETH21;
				case 22:
					return AhInterface.DEVICE_IF_TYPE_ETH22;
				case 23:
					return AhInterface.DEVICE_IF_TYPE_ETH23;
				case 24:
					return AhInterface.DEVICE_IF_TYPE_ETH24;
				case 25:
					return AhInterface.DEVICE_IF_TYPE_ETH25;
				case 26:
					return AhInterface.DEVICE_IF_TYPE_ETH26;
				case 27:
					return AhInterface.DEVICE_IF_TYPE_ETH27;
				case 28:
					return AhInterface.DEVICE_IF_TYPE_ETH28;
				case 29:
					return AhInterface.DEVICE_IF_TYPE_ETH29;
				case 30:
					return AhInterface.DEVICE_IF_TYPE_ETH30;
				case 31:
					return AhInterface.DEVICE_IF_TYPE_ETH31;
				case 32:
					return AhInterface.DEVICE_IF_TYPE_ETH32;
				case 33:
					return AhInterface.DEVICE_IF_TYPE_ETH33;
				case 34:
					return AhInterface.DEVICE_IF_TYPE_ETH34;
				case 35:
					return AhInterface.DEVICE_IF_TYPE_ETH35;
				case 36:
					return AhInterface.DEVICE_IF_TYPE_ETH36;
				case 37:
					return AhInterface.DEVICE_IF_TYPE_ETH37;
				case 38:
					return AhInterface.DEVICE_IF_TYPE_ETH38;
				case 39:
					return AhInterface.DEVICE_IF_TYPE_ETH39;
				case 40:
					return AhInterface.DEVICE_IF_TYPE_ETH40;
				case 41:
					return AhInterface.DEVICE_IF_TYPE_ETH41;
				case 42:
					return AhInterface.DEVICE_IF_TYPE_ETH42;
				case 43:
					return AhInterface.DEVICE_IF_TYPE_ETH43;
				case 44:
					return AhInterface.DEVICE_IF_TYPE_ETH44;
				case 45:
					return AhInterface.DEVICE_IF_TYPE_ETH45;
				case 46:
					return AhInterface.DEVICE_IF_TYPE_ETH46;
				case 47:
					return AhInterface.DEVICE_IF_TYPE_ETH47;
				case 48:
					return AhInterface.DEVICE_IF_TYPE_ETH48;
				}
			}else if(this == SFP){
				int portIndex = index;
				if(index > 0 && index < 5){
					int[] sfpIndexs = getSfpIndexArray(hiveApModel);
					portIndex = sfpIndexs[index - 1];
				}
				return (short)(portIndex + DEVICE_IF_TYPE_ETH0);
			}else if(this == USB){
				return AhInterface.DEVICE_IF_TYPE_USB;
			}else if(this == PortChannel){
				return (short)(AhInterface.DEVICE_IF_TYPE_PORT_CHANNEL_0 + index);
			}
			return -1;
		}
		
		public static int[] getSfpIndexArray(short hiveApModel){
			int[] sfpIndexArg = null;
			
			DeviceObj property = DevicePropertyManage.getInstance().getDeviceProperty(hiveApModel);
			if(property.getProperty() == null || property.getProperty().getAttribute() == null){
				return null;
			}
			
			List<DevicePropertyAttrOptionObj> options = null;
			for(DevicePropertyAttrObj attrObj : property.getProperty().getAttribute()){
				if(DeviceInfo.SPT_SFP_INDEX.equals(attrObj.getKey())){
					options = attrObj.getOption();
					break;
				}
			}
			
			if(options == null){
				return null;
			}
			
			sfpIndexArg = new int[options.size()];
			int index = 0;
			for(DevicePropertyAttrOptionObj option : options){
				sfpIndexArg[index++] = Integer.valueOf(option.getValue());
			}
			
			return sfpIndexArg;
		}
		
		public static boolean isSfpPort(int portIndex, short hiveApModel){
			int[] sfpIndexArg = getSfpIndexArray(hiveApModel);
			if(sfpIndexArg == null || sfpIndexArg.length == 0){
				return false;
			}
			for(int i=0; i<sfpIndexArg.length; i++){
				if(sfpIndexArg[i] == portIndex){
					return true;
				}
			}
			return false;
		}
		
		public static DeviceInfUnionType getInstance(short finalValue, short hiveApModel){
			DeviceInfUnionType resType = new DeviceInfUnionType();
			switch(finalValue){
			case DEVICE_IF_TYPE_ETH0:
			case DEVICE_IF_TYPE_ETH1:
			case DEVICE_IF_TYPE_ETH2:
			case DEVICE_IF_TYPE_ETH3:
			case DEVICE_IF_TYPE_ETH4:
			case DEVICE_IF_TYPE_ETH5:
			case DEVICE_IF_TYPE_ETH6:
			case DEVICE_IF_TYPE_ETH7:
			case DEVICE_IF_TYPE_ETH8:
			case DEVICE_IF_TYPE_ETH9:
			case DEVICE_IF_TYPE_ETH10:
			case DEVICE_IF_TYPE_ETH11:
			case DEVICE_IF_TYPE_ETH12:
			case DEVICE_IF_TYPE_ETH13:
			case DEVICE_IF_TYPE_ETH14:
			case DEVICE_IF_TYPE_ETH15:
			case DEVICE_IF_TYPE_ETH16:
			case DEVICE_IF_TYPE_ETH17:
			case DEVICE_IF_TYPE_ETH18:
			case DEVICE_IF_TYPE_ETH19:
			case DEVICE_IF_TYPE_ETH20:
			case DEVICE_IF_TYPE_ETH21:
			case DEVICE_IF_TYPE_ETH22:
			case DEVICE_IF_TYPE_ETH23:
			case DEVICE_IF_TYPE_ETH24:
			case DEVICE_IF_TYPE_ETH25:
			case DEVICE_IF_TYPE_ETH26:
			case DEVICE_IF_TYPE_ETH27:
			case DEVICE_IF_TYPE_ETH28:
			case DEVICE_IF_TYPE_ETH29:
			case DEVICE_IF_TYPE_ETH30:
			case DEVICE_IF_TYPE_ETH31:
			case DEVICE_IF_TYPE_ETH32:
			case DEVICE_IF_TYPE_ETH33:
			case DEVICE_IF_TYPE_ETH34:
			case DEVICE_IF_TYPE_ETH35:
			case DEVICE_IF_TYPE_ETH36:
			case DEVICE_IF_TYPE_ETH37:
			case DEVICE_IF_TYPE_ETH38:
			case DEVICE_IF_TYPE_ETH39:
			case DEVICE_IF_TYPE_ETH40:
			case DEVICE_IF_TYPE_ETH41:
			case DEVICE_IF_TYPE_ETH42:
			case DEVICE_IF_TYPE_ETH43:
			case DEVICE_IF_TYPE_ETH44:
			case DEVICE_IF_TYPE_ETH45:
			case DEVICE_IF_TYPE_ETH46:
			case DEVICE_IF_TYPE_ETH47:
			case DEVICE_IF_TYPE_ETH48:
			case DEVICE_IF_TYPE_ETH49:
			case DEVICE_IF_TYPE_ETH50:
			case DEVICE_IF_TYPE_ETH51:
			case DEVICE_IF_TYPE_ETH52:
				int portIndex = finalValue - DEVICE_IF_TYPE_ETH0;
				if(isSfpPort(portIndex, hiveApModel)){
//					int[] sfpPorts = getSfpIndexArray(hiveApModel);
//					for(int i=0; i<sfpPorts.length; i++){
//						if(portIndex == sfpPorts[i]){
//							resType.setIndex(i + 1);
//							break;
//						}
//					}
					resType.setIndex(portIndex);
					resType.setDeviceInfType(SFP);
				}else{
					resType.setIndex(portIndex);
					resType.setDeviceInfType(Gigabit);
				}
				break;
			case DEVICE_IF_TYPE_USB:
				resType.setDeviceInfType(USB);
				resType.setIndex(0);
				break;
			}
			if(resType != null && finalValue >= DEVICE_IF_TYPE_PORT_CHANNEL_0 && finalValue <= DEVICE_IF_TYPE_PORT_CHANNEL_999){
				resType.setDeviceInfType(PortChannel);
				resType.setIndex(finalValue - DEVICE_IF_TYPE_PORT_CHANNEL_0);
			}
			return resType;
		}
	}
}
