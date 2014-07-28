package com.ah.bo.network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * 
 * @author xcwang
 *
 */
@Embeddable
public class PortForwarding implements Serializable, Cloneable
{
	private static final long	serialVersionUID	= 1L;
	
	private String destinationPortNumber;
	
	private String internalHostIPAddress;
	
	private String internalHostPortNumber;
	
	private int key;
	
	private int protocol;
	
	private int positionId;
	
	public static final int PROTOCOL_ANY = 1;
	
	public static final int PROTOCOL_TCP = 2;
	
	public static final int PROTOCOL_UDP = 3;
	
	@Transient
	public static EnumItem[] ENUM_PROTOCOL = MgrUtil.enumItems(
			"enum.portForwarding.protocol.", new int[] { PROTOCOL_ANY, PROTOCOL_TCP, PROTOCOL_UDP});
   @Transient
   public static List<EnumItem> ENUM_POSITION (){
	   List<EnumItem> enumItemList=new ArrayList<EnumItem>();
		EnumItem enumItem=null;
		for(int i=1;i<=50;i++){
			enumItem=new EnumItem(i,String.valueOf(i));
			enumItemList.add(enumItem);
		}
	     return enumItemList;
   }
	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}
	
    public int getPositionId() {
		return positionId;
	}

	public void setPositionId(int positionId) {
		this.positionId = positionId;
	}

	@Transient
	public String getProtocolType() {
		return MgrUtil.getEnumString("enum.portForwarding.protocol."
				+ protocol);
	}

	public String getDestinationPortNumber() {
		return destinationPortNumber;
	}

	public void setDestinationPortNumber(String destinationPortNumber) {
		this.destinationPortNumber = destinationPortNumber;
	}

	public String getInternalHostIPAddress() {
		return internalHostIPAddress;
	}

	public void setInternalHostIPAddress(String internalHostIPAddress) {
		this.internalHostIPAddress = internalHostIPAddress;
	}

	public String getInternalHostPortNumber() {
		return internalHostPortNumber;
	}

	public void setInternalHostPortNumber(String internalHostPortNumber) {
		this.internalHostPortNumber = internalHostPortNumber;
	}
	
	@Transient
	public String getPolicyName() {
		String policyName = (internalHostIPAddress + "-" + destinationPortNumber).replace(".", "-").replace("/", "-");
		if(PROTOCOL_TCP == protocol) {
			policyName = policyName + "_tcp";
		} else if (PROTOCOL_UDP == protocol){
			policyName = policyName + "_udp";
		}
		return policyName;
	}

	@Override
	public PortForwarding clone() {
		try {
			return (PortForwarding) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
	
}
