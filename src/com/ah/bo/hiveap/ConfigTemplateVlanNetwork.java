package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.ah.bo.network.Vlan;
import com.ah.bo.network.VpnNetwork;


@Embeddable
@SuppressWarnings("serial")
public class ConfigTemplateVlanNetwork implements Serializable {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VLAN_ID")
	private Vlan vlan;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VPN_NETWORK_ID")
	private VpnNetwork networkObj;
	
	private boolean blnUserAdd=false;
	
	private boolean blnRemoved=false;
	
	@Transient
	private boolean blnMgtVlan=false;

	public Vlan getVlan() {
		return vlan;
	}

	public void setVlan(Vlan vlan) {
		this.vlan = vlan;
	}

	public VpnNetwork getNetworkObj() {
		return networkObj;
	}

	public void setNetworkObj(VpnNetwork networkObj) {
		this.networkObj = networkObj;
	}
	
	@Override
	public boolean equals(Object obj) {
		return new EqualsBuilder().append(this.vlan.getId(), ((ConfigTemplateVlanNetwork) obj).getVlan().getId())
				.append(this.networkObj==null? "-1": this.networkObj.getId(), 
						((ConfigTemplateVlanNetwork) obj).getNetworkObj() ==null? "-1": ((ConfigTemplateVlanNetwork) obj).getNetworkObj().getId())
						.append(this.isBlnUserAdd(), ((ConfigTemplateVlanNetwork) obj).isBlnUserAdd())
						.append(this.isBlnRemoved(), ((ConfigTemplateVlanNetwork) obj).isBlnRemoved())
						.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.vlan.getId()).append(this.networkObj == null? "-1": this.networkObj.getId()).toHashCode();
	}

	public boolean isBlnUserAdd() {
		return blnUserAdd;
	}

	public void setBlnUserAdd(boolean blnUserAdd) {
		this.blnUserAdd = blnUserAdd;
	}
	
	public boolean isBlnRemoved() {
		return blnRemoved;
	}

	public void setBlnRemoved(boolean blnRemoved) {
		this.blnRemoved = blnRemoved;
}
	public boolean isBlnMgtVlan() {
		return blnMgtVlan;
	}

	public void setBlnMgtVlan(boolean blnMgtVlan) {
		this.blnMgtVlan = blnMgtVlan;
	}
	
}
