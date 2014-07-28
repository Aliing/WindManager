package com.ah.be.communication.event;

import java.util.List;

import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.event.BeBaseEvent;

public class BeBonjourGatewayInnerEvent extends BeBaseEvent{
	
	private static final long serialVersionUID = 1L;
	
	private boolean isBdd;
	private SimpleHiveAp ap;
	private String realmName="";
	private List<String> neighbors = null;
	private String hiveName = "";
	
	public SimpleHiveAp getAp() {
		return ap;
	}

	public void setAp(SimpleHiveAp ap) {
		this.ap = ap;
	}

	public boolean isBdd() {
		return isBdd;
	}

	public void setBdd(boolean isBdd) {
		this.isBdd = isBdd;
	}

	public String getRealmName() {
		return realmName;
	}

	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}

	public List<String> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(List<String> neighbors) {
		this.neighbors = neighbors;
	}

	public String getHiveName() {
		return hiveName;
	}

	public void setHiveName(String hiveName) {
		this.hiveName = hiveName;
	}

}
