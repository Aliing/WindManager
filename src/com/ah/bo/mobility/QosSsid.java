package com.ah.bo.mobility;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import com.ah.bo.wlan.SsidProfile;

/*
@Entity
@Table(name="QOS_CLASSIFICATION_MAC_OUI")*/
@Embeddable
public class QosSsid implements Serializable {
	
	private static final long serialVersionUID = 1L;

//	@Id
//	@GeneratedValue
//	private Long id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="SSID_ID",nullable=false)
	private SsidProfile ssid;
		
	private short qosClassSsids;

	public SsidProfile getSsid() {
		return ssid;
	}

	public void setSsid(SsidProfile ssid) {
		this.ssid = ssid;
	}

	public short getQosClassSsids() {
		return qosClassSsids;
	}

	public void setQosClassSsids(short qosClassSsids) {
		this.qosClassSsids = qosClassSsids;
	}

	@Transient
	public String[] getFieldValues(){
		return new String[] {"QOS_CLASSIFICATION_ID","SSID_ID","qosClassSsids"};
	}

}