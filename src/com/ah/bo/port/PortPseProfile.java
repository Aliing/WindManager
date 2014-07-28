package com.ah.bo.port;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.network.PseProfile;
import com.ah.util.MgrUtil;

@Embeddable
public class PortPseProfile implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private short interfaceNum;
	
	private boolean enabelIfPse;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PSE_PROFILE_ID", nullable = true)
	private PseProfile pseProfile;

	@Transient
	public String getInterfaceName() {
		return MgrUtil.getEnumString("enum.switch.interface." + interfaceNum);
	}

	public PseProfile getPseProfile() {
		return pseProfile;
	}

	public void setPseProfile(PseProfile pseProfile) {
		this.pseProfile = pseProfile;
	}

	public short getInterfaceNum() {
		return interfaceNum;
	}

	public void setInterfaceNum(short interfaceNum) {
		this.interfaceNum = interfaceNum;
	}

	public boolean isEnabelIfPse() {
		return enabelIfPse;
	}

	public void setEnabelIfPse(boolean enabelIfPse) {
		this.enabelIfPse = enabelIfPse;
	}
	
}
