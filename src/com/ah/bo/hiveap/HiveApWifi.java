package com.ah.bo.hiveap;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.MgrUtil;

@Embeddable
public class HiveApWifi implements AhInterface {

	private static final long serialVersionUID = 1L;

	private short operationMode;

	private short adminState;

	private int channel;

	private int power;

	private short radioMode;

	public int getChannel() {
		return channel;
	}

	public void setChannel(int channel) {
		this.channel = channel;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}

	public short getRadioMode() {
		return radioMode;
	}

	public void setRadioMode(short radioMode) {
		this.radioMode = radioMode;
	}

	public short getOperationMode() {
		return operationMode;
	}

	public void setOperationMode(short operationMode) {
		this.operationMode = operationMode;
	}

	public short getAdminState() {
		return adminState;
	}

	public void setAdminState(short adminState) {
		this.adminState = adminState;
	}

	@Transient
	private String memo;

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getChannelStr() {
		String channelStr;
		if (radioMode == RADIO_MODE_BG) {
			channelStr = MgrUtil.getEnumString("enum.interface.channel."
					+ channel);
			if (null == channelStr) {
				channelStr = MgrUtil.getEnumString("enum.interface.channel."
						+ CHANNEL_BG_AUTO);
			}
			return channelStr == null ? "" : channelStr;
		} else if (radioMode == RADIO_MODE_A) {
			channelStr = MgrUtil.getEnumString("enum.interface.channel."
					+ channel);
			if (null == channelStr) {
				channelStr = MgrUtil.getEnumString("enum.interface.channel."
						+ CHANNEL_A_AUTO);
			}
			return channelStr == null ? "" : channelStr;
		} else if (radioMode == RADIO_MODE_NG) {
			channelStr = MgrUtil.getEnumString("enum.interface.channel."
					+ channel);
			if (null == channelStr) {
				channelStr = MgrUtil.getEnumString("enum.interface.channel."
						+ CHANNEL_BG_AUTO);
			}
			return channelStr == null ? "" : channelStr;
		} else if (radioMode == RADIO_MODE_NA || radioMode == RADIO_MODE_AC) {
			channelStr = MgrUtil.getEnumString("enum.interface.channel."
					+ channel);
			if (null == channelStr) {
				channelStr = MgrUtil.getEnumString("enum.interface.channel."
						+ CHANNEL_A_AUTO);
			}
			return channelStr == null ? "" : channelStr;
		} else {
			return "";
		}
	}

	public String getPowerStr() {
		String powerStr = MgrUtil
				.getEnumString("enum.interface.power." + power);
		if (null == powerStr) {
			MgrUtil.getEnumString("enum.interface.power." + CHANNEL_A_AUTO);
		}
		return null == powerStr ? "" : powerStr;
	}

	@Transient
	private String runningChannel = "-";

	@Transient
	private String runningPower = "-";

	public String getRunningChannel() {
		if(operationMode==AhInterface.OPERATION_MODE_SENSOR){
			return "N/A";
		}
		return runningChannel;
	}

	public void setRunningChannel(String runningChannel) {
		this.runningChannel = runningChannel;
	}

	public String getRunningPower() {
		return runningPower;
	}

	public void setRunningPower(String runningPower) {
		this.runningPower = runningPower;
	}

}