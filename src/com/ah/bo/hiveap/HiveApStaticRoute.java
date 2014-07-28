package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Embeddable
public class HiveApStaticRoute implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String destinationMac;
	private short interfaceType;
	private String nextHopMac;

	public static final short STATIC_ROUTE_IF_ETH = 1;
	public static final short STATIC_ROUTE_IF_WIFI0 = 2;
	public static final short STATIC_ROUTE_IF_WIFI1 = 3;

	/*
	 * interface type for box 11n joseph chen, 07/07/2008
	 */
	public static final short STATIC_ROUTE_IF_ETH1 = 4;
	public static final short STATIC_ROUTE_IF_RED0 = 5;
	public static final short STATIC_ROUTE_IF_AGG0 = 6;

	public static EnumItem[] STATIC_ROUTE_IF_TYPE = MgrUtil.enumItems(
			"enum.staticRoute.interface.", new int[] { STATIC_ROUTE_IF_ETH,
					STATIC_ROUTE_IF_WIFI0, STATIC_ROUTE_IF_WIFI1 });

	public static EnumItem[] STATIC_ROUTE_IF_TYPE_DUAL = MgrUtil.enumItems(
			"enum.staticRoute.interface.11n.", new int[] { STATIC_ROUTE_IF_ETH,
					STATIC_ROUTE_IF_ETH1, STATIC_ROUTE_IF_RED0,
					STATIC_ROUTE_IF_AGG0, STATIC_ROUTE_IF_WIFI0,
					STATIC_ROUTE_IF_WIFI1 });

	public static EnumItem[] STATIC_ROUTE_IF_TYPE_SINGLE= MgrUtil
			.enumItems("enum.staticRoute.interface.11n.", new int[] {
					STATIC_ROUTE_IF_ETH, STATIC_ROUTE_IF_WIFI0 });

	public String getDestinationMac() {
		return destinationMac;
	}

	public void setDestinationMac(String destinationMac) {
		this.destinationMac = destinationMac;
	}

	public short getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(short interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getNextHopMac() {
		return nextHopMac;
	}

	public void setNextHopMac(String nextHopMac) {
		this.nextHopMac = nextHopMac;
	}

}