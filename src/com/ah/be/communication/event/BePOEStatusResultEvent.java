package com.ah.be.communication.event;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * 
 *@filename		BePOEStatusResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-4-28 10:23:59
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BePOEStatusResultEvent extends BeCapwapClientResultEvent {

	public static final int POWERSOURCE_ADOPTER = 0;
	public static final int POWERSOURCE_POE = 1;

	public static EnumItem[] SOURCE = MgrUtil.enumItems("enum.poe.source.",
			new int[] { POWERSOURCE_ADOPTER, POWERSOURCE_POE });

	public static final int ETH_SPEED_LINK_DOWN = 1;
	public static final int ETH_SPEED_10M = 2;
	public static final int ETH_SPEED_100M = 3;
	public static final int ETH_SPEED_1000M = 4;
	public static EnumItem[] ETH_SPEED = MgrUtil.enumItems(
			"enum.poe.eth.speed.", new int[] { ETH_SPEED_LINK_DOWN,
					ETH_SPEED_10M, ETH_SPEED_100M, ETH_SPEED_1000M });

	public static final int WIFI_SETTING_INVALID = 0;
	public static final int WIFI_SETTING_LINK_DOWN = 1;
	public static final int WIFI_SETTING_CONFIG = 2;
	public static final int WIFI_SETTING_TX2RX3 = 3;
	public static EnumItem[] WIFI_SETTING = MgrUtil.enumItems(
			"enum.poe.wifi.setting.", new int[] { WIFI_SETTING_INVALID,
					WIFI_SETTING_LINK_DOWN, WIFI_SETTING_CONFIG,
					WIFI_SETTING_TX2RX3 });

	public static final int ETH_STATE_DOWN = 0;
	public static final int ETH_STATE_UP = 1;
	public static EnumItem[] POWER = MgrUtil.enumItems("enum.poe.eth.state.",
			new int[] { ETH_STATE_DOWN, ETH_STATE_UP });

	public String getPowerSourceString() {
		switch (powerSource) {
		case POWERSOURCE_ADOPTER:
		case POWERSOURCE_POE:
			return MgrUtil.getEnumString("enum.poe.source." + powerSource);
		default:
			return "";
		}
	}

	public String getEthSpeedString(int speed) {
		switch (speed) {
		case ETH_SPEED_LINK_DOWN:
		case ETH_SPEED_10M:
		case ETH_SPEED_100M:
		case ETH_SPEED_1000M:
			return MgrUtil.getEnumString("enum.poe.eth.speed." + speed);
		default:
			return "";
		}
	}

	public String getWifiSettingString(int setting) {
		switch (setting) {
		case WIFI_SETTING_INVALID:
		case WIFI_SETTING_LINK_DOWN:
		case WIFI_SETTING_CONFIG:
		case WIFI_SETTING_TX2RX3:
			return MgrUtil.getEnumString("enum.poe.wifi.setting." + setting);
		default:
			return "";
		}
	}

	public String getEthPowerString(int power) {
		return power / 10f + " watts";
	}

	public String getEthState(boolean isUp) {
		return isUp ? MgrUtil.getEnumString("enum.poe.eth.state."
				+ ETH_STATE_UP) : MgrUtil.getEnumString("enum.poe.eth.state."
				+ ETH_STATE_DOWN);
	}

	private int powerSource = POWERSOURCE_ADOPTER;

	private boolean eth0Up;

	private int eth0PowerLevel;

	private int eth0MaxSpeed;

	private int wifi0Setting;

	private boolean eth1Up;

	private int eth1PowerLevel;

	private int eth1MaxSpeed;

	private int wifi1Setting;

	private int wifi2Setting;

	public BePOEStatusResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_POESTATUS;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data
	 *            -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);

			powerSource = buf.getInt();
			eth0Up = buf.getInt() == ETH_STATE_UP;
			eth0PowerLevel = buf.getInt();
			eth0MaxSpeed = buf.getInt();
			wifi0Setting = buf.getInt();
			eth1Up = buf.getInt() == ETH_STATE_UP;
			eth1PowerLevel = buf.getInt();
			eth1MaxSpeed = buf.getInt();
			wifi1Setting = buf.getInt();
			wifi2Setting = buf.getInt();
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BePOEStatusResultEvent.parsePacket() catch exception", e);
		}
	}

	public int getEth0MaxSpeed() {
		return eth0MaxSpeed;
	}

	public void setEth0MaxSpeed(int eth0MaxSpeed) {
		this.eth0MaxSpeed = eth0MaxSpeed;
	}

	public int getEth0PowerLevel() {
		return eth0PowerLevel;
	}

	public void setEth0PowerLevel(int eth0PowerLevel) {
		this.eth0PowerLevel = eth0PowerLevel;
	}

	public boolean isEth0Up() {
		return eth0Up;
	}

	public void setEth0Up(boolean eth0Up) {
		this.eth0Up = eth0Up;
	}

	public int getEth1MaxSpeed() {
		return eth1MaxSpeed;
	}

	public void setEth1MaxSpeed(int eth1MaxSpeed) {
		this.eth1MaxSpeed = eth1MaxSpeed;
	}

	public int getEth1PowerLevel() {
		return eth1PowerLevel;
	}

	public void setEth1PowerLevel(int eth1PowerLevel) {
		this.eth1PowerLevel = eth1PowerLevel;
	}

	public boolean isEth1Up() {
		return eth1Up;
	}

	public void setEth1Up(boolean eth1Up) {
		this.eth1Up = eth1Up;
	}

	public int getPowerSource() {
		return powerSource;
	}

	public void setPowerSource(int powerSource) {
		this.powerSource = powerSource;
	}

	public int getWifi0Setting() {
		return wifi0Setting;
	}

	public void setWifi0Setting(int wifi0Setting) {
		this.wifi0Setting = wifi0Setting;
	}

	public int getWifi1Setting() {
		return wifi1Setting;
	}

	public void setWifi1Setting(int wifi1Setting) {
		this.wifi1Setting = wifi1Setting;
	}

	public int getWifi2Setting() {
		return wifi2Setting;
	}

	public void setWifi2Setting(int wifi2Setting) {
		this.wifi2Setting = wifi2Setting;
	}
}
