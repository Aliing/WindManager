package com.ah.bo.network;

import javax.persistence.Embeddable;

@Embeddable
public class SubnetworkDHCPCustom extends DhcpServerOptionsCustom implements Cloneable{

	private static final long serialVersionUID = 5755677312431848054L;

	private int key;

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	@Override
    public SubnetworkDHCPCustom clone() {
	    try {
            return (SubnetworkDHCPCustom) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
	}
}
