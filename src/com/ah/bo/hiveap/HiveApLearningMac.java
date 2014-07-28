package com.ah.bo.hiveap;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.ah.bo.network.MacOrOui;

@Embeddable
public class HiveApLearningMac implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final short LEARNING_MAC_ETH0 = 1;
	public static final short LEARNING_MAC_ETH1 = 2;
	public static final short LEARNING_MAC_AGG0 = 3;
	public static final short LEARNING_MAC_RED0 = 4;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LEARNING_MAC_ID", nullable = true)
	private MacOrOui mac;

	private short learningMacType;

	public MacOrOui getMac() {
		return mac;
	}

	public void setMac(MacOrOui mac) {
		this.mac = mac;
	}

	public short getLearningMacType() {
		return learningMacType;
	}

	public void setLearningMacType(short learningMacType) {
		this.learningMacType = learningMacType;
	}

}