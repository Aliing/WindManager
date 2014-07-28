package com.ah.bo.performance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "HM_NEIGHBOR")
@org.hibernate.annotations.Table(appliesTo = "HM_NEIGHBOR", indexes = {
		@Index(name = "NEIGHBOR_OWNER", columnNames = { "OWNER" })
		})
public class AhNeighbor implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 64)
	private String apName;

	private String apMac;

	@Column(length = 14)
	private String apSerialNumber;

//	private Date statTime;

	private int ifIndex;

	@Column(length = 48)
	private String neighborAPID;

	private long linkCost;

	private int rssi;

	private long linkUpTime;

	public static final byte LINKTYPE_ETHLINK = 0;

	public static final byte LINKTYPE_WIRELESSLINK = 1;

	public static EnumItem[] LINK_TYPE = MgrUtil.enumItems(
			"enum.snmp.mrp.linkType.", new int[] { LINKTYPE_ETHLINK,
					LINKTYPE_WIRELESSLINK });

	private byte linkType;

	private long rxDataFrames;

	private long rxDataOctets;

	private long rxMgtFrames;

	private long rxUnicastFrames;

	private long rxMulticastFrames;

	private long rxBroadcastFrames;

	private long txDataFrames;

	private long txBeDataFrames;

	private long txBgDataFrames;

	private long txViDataFrames;

	private long txVoDataFrames;

	private long txMgtFrames;

	private long txDataOctets;

	private long txUnicastFrames;

	private long txMulticastFrames;

	private long txBroadcastFrames;
	
	private HmTimeStamp	timeStamp = HmTimeStamp.CURRENT_TIMESTAMP;

	@Override
	public Long getId() {
		return id;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public long getLinkCost() {
		return linkCost;
	}

	public void setLinkCost(long linkCost) {
		this.linkCost = linkCost;
	}

	public byte getLinkType() {
		return linkType;
	}

	public void setLinkType(byte linkType) {
		this.linkType = linkType;
	}

	public long getLinkUpTime() {
		return linkUpTime;
	}

	public void setLinkUpTime(long linkUpTime) {
		this.linkUpTime = linkUpTime;
	}

	public String getNeighborAPID() {
		return neighborAPID;
	}

	public void setNeighborAPID(String neighborAPID) {
		this.neighborAPID = neighborAPID;
	}

	public int getRssi() {
		return rssi;
	}

	public void setRssi(int rssi) {
		this.rssi = rssi;
	}

	/*
	 * Atheros specific translation
	 */
	public String getRssiDbm() {
		if (linkType == LINKTYPE_ETHLINK) {
			return "";
		} else {
			return (rssi - 95) + " dBm";
		}
	}

	public long getRxDataFrames() {
		return rxDataFrames;
	}

	public void setRxDataFrames(long rxDataFrames) {
		this.rxDataFrames = rxDataFrames;
	}

	public long getRxDataOctets() {
		return rxDataOctets;
	}

	public void setRxDataOctets(long dataOctets) {
		rxDataOctets = dataOctets;
	}

	public long getRxMgtFrames() {
		return rxMgtFrames;
	}

	public void setRxMgtFrames(long rxMgtFrames) {
		this.rxMgtFrames = rxMgtFrames;
	}

	public long getRxBroadcastFrames() {
		return rxBroadcastFrames;
	}

	public void setRxBroadcastFrames(long rxBroadcastFrames) {
		this.rxBroadcastFrames = rxBroadcastFrames;
	}

	public long getTxBroadcastFrames() {
		return txBroadcastFrames;
	}

	public void setTxBroadcastFrames(long txBroadcastFrames) {
		this.txBroadcastFrames = txBroadcastFrames;
	}

	public long getTxMulticastFrames() {
		return txMulticastFrames;
	}

	public void setTxMulticastFrames(long txMulticastFrames) {
		this.txMulticastFrames = txMulticastFrames;
	}

	public long getTxUnicastFrames() {
		return txUnicastFrames;
	}

	public void setTxUnicastFrames(long txUnicastFrames) {
		this.txUnicastFrames = txUnicastFrames;
	}

	public void setRxMulticastFrames(long rxMulticastFrames) {
		this.rxMulticastFrames = rxMulticastFrames;
	}

	public long getRxMulticastFrames() {
		return rxMulticastFrames;
	}

	public long getRxUnicastFrames() {
		return rxUnicastFrames;
	}

	public void setRxUnicastFrames(long rxUnicastFrames) {
		this.rxUnicastFrames = rxUnicastFrames;
	}

//	public String getStatTime() {
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		return formatter.format(statTime);
//	}
//
//	public Date getStatTimeValue() {
//		return statTime;
//	}
//
//	public void setStatTime(Date statTime) {
//		this.statTime = statTime;
//	}

	public long getTxDataFrames() {
		return txDataFrames;
	}

	public void setTxDataFrames(long txDataFrames) {
		this.txDataFrames = txDataFrames;
	}

	public long getTxDataOctets() {
		return txDataOctets;
	}

	public void setTxDataOctets(long txDataOctets) {
		this.txDataOctets = txDataOctets;
	}

	public long getTxMgtFrames() {
		return txMgtFrames;
	}

	public void setTxMgtFrames(long txMgtFrames) {
		this.txMgtFrames = txMgtFrames;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApSerialNumber() {
		return apSerialNumber;
	}

	public void setApSerialNumber(String apSerialNumber) {
		this.apSerialNumber = apSerialNumber;
	}

	// --------implement interface function--------

	// Label used in error messages
	@Override
	public String getLabel() {
		return "ahNeightbor";
	}

	// For multi page selection
	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/*
	 * statistic objects have no owner or version,because it created by system,
	 * it can't be updated and 'statTime' keep track of when create it.
	 */
	/**
	 * modify mark: add owner field for VHM
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Transient
	public String getLinkTypeString() {
		switch (linkType) {
		case LINKTYPE_ETHLINK:
		case LINKTYPE_WIRELESSLINK:
			return MgrUtil.getEnumString("enum.snmp.mrp.linkType." + linkType);
		default:
			return "Unknown";
		}
	}

	@Transient
	private String hostName;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	@Transient
	public String getLinkUpTimeString() {
		return NmsUtil.transformTime((int) linkUpTime);
	}

	public long getTxBeDataFrames() {
		return txBeDataFrames;
	}

	public void setTxBeDataFrames(long txBeDataFrames) {
		this.txBeDataFrames = txBeDataFrames;
	}

	public long getTxBgDataFrames() {
		return txBgDataFrames;
	}

	public void setTxBgDataFrames(long txBgDataFrames) {
		this.txBgDataFrames = txBgDataFrames;
	}

	public long getTxViDataFrames() {
		return txViDataFrames;
	}

	public void setTxViDataFrames(long txViDataFrames) {
		this.txViDataFrames = txViDataFrames;
	}

	public long getTxVoDataFrames() {
		return txVoDataFrames;
	}

	public void setTxVoDataFrames(long txVoDataFrames) {
		this.txVoDataFrames = txVoDataFrames;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

}