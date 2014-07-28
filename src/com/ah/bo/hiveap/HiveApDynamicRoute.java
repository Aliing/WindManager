package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class HiveApDynamicRoute implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String neighborMac;
	
	private int routeMinimun;
	
	private int routeMaximun;

	public String getNeighborMac() {
		return neighborMac;
	}
	public void setNeighborMac(String neighborMac) {
		this.neighborMac = neighborMac;
	}
	public int getRouteMinimun() {
		return routeMinimun;
	}
	public void setRouteMinimun(int routeMinimun) {
		this.routeMinimun = routeMinimun;
	}
	public int getRouteMaximun() {
		return routeMaximun;
	}
	public void setRouteMaximun(int routeMaximun) {
		this.routeMaximun = routeMaximun;
	}

}