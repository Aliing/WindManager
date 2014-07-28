package com.ah.bo.network;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.bo.hiveap.HiveAp;

@Embeddable
public class VpnGatewaySetting implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final short IP_ADDRESS_LENGTH = 15;

	@Transient
	private HiveAp hiveAP;

	@Column(nullable = false)
	private Long hiveApId;

	@Column(length = IP_ADDRESS_LENGTH)
	private String externalIpAddress;
	
	@Transient
	private int reorder;

	@Transient
	public String getLanIpAddress() {
		if (hiveAP != null) {
			if (hiveAP.getEth1Interface() != null) {
				return hiveAP.getEth1Interface().getIpAddress();
			}else{
				return "N/A";
			}
		}
		return "N/A";
	}
	
	@Transient
	public String getWanIpAddress() {
		if (hiveAP != null) {
			if (hiveAP.getEth0Interface() != null) {
				return hiveAP.getEth0Interface().getIpAddress();
			}else{
				return null;
			}
		}
		return null;
	}
	
	
	@Transient
	public String getDynamicRoutStringStr(){
		if(hiveAP != null){
			if(hiveAP.getRoutingProfile() != null && hiveAP.getRoutingProfile().isEnableDynamicRouting()){
				switch (hiveAP.getRoutingProfile().getTypeFlag()) {
				case RoutingProfile.ENABLE_DRP_RIPV2:
					return "RIPv2";
				case RoutingProfile.ENABLE_DRP_OSPF:
					return "OSPF";
				case RoutingProfile.ENABLE_DRP_BGP:
					return "BGP";
				case RoutingProfile.ENABLE_DRP_NONE:
					return "None";
				default:
					return "None";
				}
			}
		}
		return "None";
	}
	
	public HiveAp getHiveAP() {
		return hiveAP;
	}

	public void setHiveAP(HiveAp hiveAP) {
		this.hiveAP = hiveAP;
	}

	public Long getApId() {
		return hiveApId;
	}

	public void setApId(Long apId) {
		this.hiveApId = apId;
	}

	public String getExternalIpAddress() {
		return externalIpAddress;
	}

	public void setExternalIpAddress(String externalIpAddress) {
		this.externalIpAddress = externalIpAddress;
	}

	public int getReorder() {
		return reorder;
	}

	public void setReorder(int reorder) {
		this.reorder = reorder;
	}

}