package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
@SuppressWarnings("serial")
public class HiveAPVirtualConnection implements Serializable {

	public static final byte	ACTION_PASS					= 1;

	public static final byte	ACTION_DROP					= 2;

	public static final byte	INTERFACE_ETH0				= 1;

	public static final byte	INTERFACE_ETH1				= 2;

	public static final byte	INTERFACE_AGG0				= 3;

	public static final byte	INTERFACE_RED0				= 4;

	public static final byte	INTERFACE_WIFI0				= 5;

	public static final byte	INTERFACE_WIFI1				= 6;

	public static EnumItem[]	VIRTUALCONNECT_ACTIONS		= MgrUtil
																	.enumItems(
																			"enum.hiveAp.virtualConnect.action.",
																			new int[] {
			ACTION_PASS, ACTION_DROP										});

	public static EnumItem[]	VIRTUALCONNECT_INTERFACES	= MgrUtil
																	.enumItems(
																			"enum.hiveAp.virtualConnect.interface.",
																			new int[] {
			INTERFACE_ETH0, INTERFACE_ETH1, INTERFACE_AGG0, INTERFACE_RED0, INTERFACE_WIFI0,
			INTERFACE_WIFI1												});

	private String				forwardName;

	private byte				forwardAction;

	/**
	 * eth0,eth1,agg0,red0,wifi0(wifi0.1),wifi1(wifi1.1)
	 */
	private byte				interface_in;

	private byte				interface_out;

	private String				sourceMac;

	private String				destMac;

	private String				txMac;

	private String				rxMac;

	public String getRxMac() {
		return rxMac;
	}

	public void setRxMac(String rxMac) {
		this.rxMac = rxMac;
	}

	public String getForwardName() {
		return forwardName;
	}

	public void setForwardName(String forwardName) {
		this.forwardName = forwardName;
	}

	public byte getForwardAction() {
		return forwardAction;
	}
	
	public String getForwardAction4Display() {
		return MgrUtil.getEnumString("enum.hiveAp.virtualConnect.action." + forwardAction);
	}

	public void setForwardAction(byte forwardAction) {
		this.forwardAction = forwardAction;
	}

	public byte getInterface_in() {
		return interface_in;
	}
	
	public String getInterface_in4Display() {
		if (interface_in == 0) {
			return "";
		}
		
		return MgrUtil.getEnumString("enum.hiveAp.virtualConnect.interface." + interface_in);
	}

	public void setInterface_in(byte interfaceIn) {
		interface_in = interfaceIn;
	}

	public byte getInterface_out() {
		return interface_out;
	}
	
	public String getInterface_out4Display() {
		if (interface_out == 0) {
			return "";
		}
		
		return MgrUtil.getEnumString("enum.hiveAp.virtualConnect.interface." + interface_out);
	}

	public void setInterface_out(byte interfaceOut) {
		interface_out = interfaceOut;
	}

	public String getSourceMac() {
		return sourceMac;
	}

	public void setSourceMac(String sourceMac) {
		this.sourceMac = sourceMac;
	}

	public String getDestMac() {
		return destMac;
	}

	public void setDestMac(String destMac) {
		this.destMac = destMac;
	}

	public String getTxMac() {
		return txMac;
	}

	public void setTxMac(String txMac) {
		this.txMac = txMac;
	}

}
