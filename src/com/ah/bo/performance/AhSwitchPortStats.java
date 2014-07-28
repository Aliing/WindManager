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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Index;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_SWITCH_PORT_STATS")
@org.hibernate.annotations.Table(appliesTo = "HM_SWITCH_PORT_STATS", indexes = {
		@Index(name = "idx_switch_port_stats_owner", columnNames = { "OWNER"}),
		@Index(name = "idx_switch_port_stats_mac", columnNames = { "MAC"})
		})
public class AhSwitchPortStats implements HmBo {

	private static final long serialVersionUID = 1L;
	private static final float PORT_IS_ERROR_COUNT_RATIO = 0.01f;
	public static final String BR = "<br>";

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	private String mac;

	@Column(length = 128)
	private String portName;

	private long txPacketCount;

	private long rxPacketCount;

	private long txBytesCount;
	
	private long rxBytesCount;

	private long txUnicastPackets;

	private long rxUnicastPackets;
	
	private long txMuticastPackets;
	
	private long rxMuticastPackets;

	private long txBroadcastPackets;
	
	private long rxBroadcastPackets;
	
	private long rxBadPauseFrames;
	private long rxUnreogMacFrames;
	private long rxFragmentFrames;
	private long rxJabberFrames;
	private long rxMACErrorFrames;
	private long rxCollisionsFrames;
	private long rxLateCollisionFrames;
	private long rxBadOctetsFrames;
	private long rxBadCRCFrames;
	private long rxErrorFrames;
	private long rxUndersizeFrames;
	private long rxOversizeFrames;
	private long rxOverrunFrames;
	private long txExcessiveCollisionFrames;
	private long txMACTransmitErrorFrames;


	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "AhSwitchPortStats";
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

	@Override
	public void setId(Long id)
	{
		this.id=id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public long getTxPacketCount() {
		return txPacketCount;
	}

	public void setTxPacketCount(long txPacketCount) {
		this.txPacketCount = txPacketCount;
	}

	public long getTxBytesCount() {
		return txBytesCount;
	}

	public void setTxBytesCount(long txBytesCount) {
		this.txBytesCount = txBytesCount;
	}

	public long getRxPacketCount() {
		return rxPacketCount;
	}

	public void setRxPacketCount(long rxPacketCount) {
		this.rxPacketCount = rxPacketCount;
	}

	public long getRxBytesCount() {
		return rxBytesCount;
	}

	public void setRxBytesCount(long rxBytesCount) {
		this.rxBytesCount = rxBytesCount;
	}

	public long getTxUnicastPackets() {
		return txUnicastPackets;
	}

	public void setTxUnicastPackets(long txUnicastPackets) {
		this.txUnicastPackets = txUnicastPackets;
	}

	public long getRxUnicastPackets() {
		return rxUnicastPackets;
	}

	public void setRxUnicastPackets(long rxUnicastPackets) {
		this.rxUnicastPackets = rxUnicastPackets;
	}

	public long getTxMuticastPackets() {
		return txMuticastPackets;
	}

	public void setTxMuticastPackets(long txMuticastPackets) {
		this.txMuticastPackets = txMuticastPackets;
	}

	public long getRxMuticastPackets() {
		return rxMuticastPackets;
	}

	public void setRxMuticastPackets(long rxMuticastPackets) {
		this.rxMuticastPackets = rxMuticastPackets;
	}

	public long getTxBroadcastPackets() {
		return txBroadcastPackets;
	}

	public void setTxBroadcastPackets(long txBroadcastPackets) {
		this.txBroadcastPackets = txBroadcastPackets;
	}

	public long getRxBroadcastPackets() {
		return rxBroadcastPackets;
	}

	public void setRxBroadcastPackets(long rxBroadcastPackets) {
		this.rxBroadcastPackets = rxBroadcastPackets;
	}
	
	public long getRxBadPauseFrames() {
		return rxBadPauseFrames;
	}

	public void setRxBadPauseFrames(long rxBadPauseFrames) {
		this.rxBadPauseFrames = rxBadPauseFrames;
	}

	public long getRxUnreogMacFrames() {
		return rxUnreogMacFrames;
	}

	public void setRxUnreogMacFrames(long rxUnreogMacFrames) {
		this.rxUnreogMacFrames = rxUnreogMacFrames;
	}

	public long getRxFragmentFrames() {
		return rxFragmentFrames;
	}

	public void setRxFragmentFrames(long rxFragmentFrames) {
		this.rxFragmentFrames = rxFragmentFrames;
	}

	public long getRxJabberFrames() {
		return rxJabberFrames;
	}

	public void setRxJabberFrames(long rxJabberFrames) {
		this.rxJabberFrames = rxJabberFrames;
	}

	public long getRxMACErrorFrames() {
		return rxMACErrorFrames;
	}

	public void setRxMACErrorFrames(long rxMACErrorFrames) {
		this.rxMACErrorFrames = rxMACErrorFrames;
	}

	public long getRxCollisionsFrames() {
		return rxCollisionsFrames;
	}

	public void setRxCollisionsFrames(long rxCollisionsFrames) {
		this.rxCollisionsFrames = rxCollisionsFrames;
	}

	public long getRxLateCollisionFrames() {
		return rxLateCollisionFrames;
	}

	public void setRxLateCollisionFrames(long rxLateCollisionFrames) {
		this.rxLateCollisionFrames = rxLateCollisionFrames;
	}

	public long getRxBadOctetsFrames() {
		return rxBadOctetsFrames;
	}

	public void setRxBadOctetsFrames(long rxBadOctetsFrames) {
		this.rxBadOctetsFrames = rxBadOctetsFrames;
	}

	public long getRxBadCRCFrames() {
		return rxBadCRCFrames;
	}

	public void setRxBadCRCFrames(long rxBadCRCFrames) {
		this.rxBadCRCFrames = rxBadCRCFrames;
	}

	public long getRxErrorFrames() {
		return rxErrorFrames;
	}

	public void setRxErrorFrames(long rxErrorFrames) {
		this.rxErrorFrames = rxErrorFrames;
	}

	public long getRxUndersizeFrames() {
		return rxUndersizeFrames;
	}

	public void setRxUndersizeFrames(long rxUndersizeFrames) {
		this.rxUndersizeFrames = rxUndersizeFrames;
	}

	public long getRxOversizeFrames() {
		return rxOversizeFrames;
	}

	public void setRxOversizeFrames(long rxOversizeFrames) {
		this.rxOversizeFrames = rxOversizeFrames;
	}

	public long getRxOverrunFrames() {
		return rxOverrunFrames;
	}

	public void setRxOverrunFrames(long rxOverrunFrames) {
		this.rxOverrunFrames = rxOverrunFrames;
	}

	public long getTxExcessiveCollisionFrames() {
		return txExcessiveCollisionFrames;
	}

	public void setTxExcessiveCollisionFrames(long txExcessiveCollisionFrames) {
		this.txExcessiveCollisionFrames = txExcessiveCollisionFrames;
	}

	public long getTxMACTransmitErrorFrames() {
		return txMACTransmitErrorFrames;
	}

	public void setTxMACTransmitErrorFrames(long txMACTransmitErrorFrames) {
		this.txMACTransmitErrorFrames = txMACTransmitErrorFrames;
	}

	@Transient
	public long getUnicastPackets() {
		return txUnicastPackets + rxUnicastPackets;
	}
	
	@Transient
	public long getMuticastPackets() {
		return txMuticastPackets + rxMuticastPackets;
	}
	
	@Transient
	public long getBroadcastPackets() {
		return txBroadcastPackets + rxBroadcastPackets;
	}
	
	@Transient
	public String getTxPacketCountString(){
		return NmsUtil.formatNumberByComma(getTxPacketCount());
	}
	
	@Transient
	public String getTxBytesCountString(){
		return NmsUtil.formatNumberByComma(getTxBytesCount());
	}
	
	@Transient
	public String getRxPacketCountString(){
		return NmsUtil.formatNumberByComma(getRxPacketCount());
	}
	
	@Transient
	public String getRxBytesCountString(){
		return NmsUtil.formatNumberByComma(getRxBytesCount());
	}
	
	@Transient
	public String getUnicastPacketsString(){
		return NmsUtil.formatNumberByComma(getUnicastPackets());
	}
	
	@Transient
	public String getMuticastPacketsString(){
		return NmsUtil.formatNumberByComma(getMuticastPackets());
	}
	
	@Transient
	public String getBroadcastPacketsString(){
		return NmsUtil.formatNumberByComma(getBroadcastPackets());
	}
	
	//*************for port error ratio calculate********start**********
	@Transient
	public long getAllRxPackets() {
		return rxUnicastPackets + rxMuticastPackets + rxBroadcastPackets;
	}
	
	@Transient
	public long getAllTxPackets() {
		return txUnicastPackets + txMuticastPackets + txBroadcastPackets;
	}
	
	@Transient
	public float getRxBadPauseFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxBadPauseFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxUnreogMacFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxUnreogMacFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxFragmentFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxFragmentFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxJabberFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxJabberFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxMACErrorFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxMACErrorFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxCollisionsFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxCollisionsFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxLateCollisionFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxLateCollisionFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxBadOctetsFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxBadOctetsFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxBadCRCFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxBadCRCFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxErrorFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxErrorFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxUndersizeFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxUndersizeFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxOversizeFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxOversizeFrames / getAllRxPackets();
	}
	
	@Transient
	public float getRxOverrunFramesRatio() {
		if (getAllRxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return rxOverrunFrames / getAllRxPackets();
	}
	
	@Transient
	public float getTxExcessiveCollisionFramesRatio() {
		if (getAllTxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return txExcessiveCollisionFrames / getAllTxPackets();
	}
	
	@Transient
	public float getTxMACTransmitErrorFramesRatio() {
		if (getAllTxPackets() <= 0) {
			// avoid ArithmeticException: / by zero
			return 0;
		}
		return txMACTransmitErrorFrames / getAllTxPackets();
	}
	
	private static String rxBadPauseName = "Bad Pause Frames";
	private static String rxUnreogMacName = "Unrecognized MAC Count Frames";
	private static String rxFragmentName = "Fragment Frames";
	private static String rxJabberName = "Jabber Frames";
	private static String rxMACErrorName = "MAC Receive Error Frames";
	private static String rxCollisionsName = "Collision Frames";
	private static String rxLateCollisionName = "Late Collision Frames";
	private static String rxBadOctetsName = "Bad Octet Frames";
	private static String rxBadCRCName = "Bad CRC Frames";
	private static String rxErrorName = "Receive Error Frames";
	private static String rxUndersizeName = "Undersize Frames";
	private static String rxOversizeName = "Oversize Frames";
	private static String rxOverrunName = "Overrun Frames";
	private static String txExcessiveCollisionName = "Excessive Collision Frames";
	private static String txMACTransmitErrorName = "MAC Transmit Error Frames";

	/**
	 * 
	 * if error counts > 1% of (all frames count), show if in port overview pop up window
	 * 
	 * @return
	 */
	@Transient
	public String getRxRatioOutOfLimits() {
		StringBuffer rxErrors = new StringBuffer();
		if (getRxBadPauseFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxBadPauseName);
			rxErrors.append(BR);
		}
		if (getRxUnreogMacFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxUnreogMacName);
			rxErrors.append(BR);
		}
		if (getRxFragmentFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxFragmentName);
			rxErrors.append(BR);
		}
		if (getRxJabberFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxJabberName);
			rxErrors.append(BR);
		}
		if (getRxMACErrorFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxMACErrorName);
			rxErrors.append(BR);
		}
		if (getRxCollisionsFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxCollisionsName);
			rxErrors.append(BR);
		}
		if (getRxLateCollisionFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxLateCollisionName);
			rxErrors.append(BR);
		}
		if (getRxBadOctetsFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxBadOctetsName);
			rxErrors.append(BR);
		}
		if (getRxBadCRCFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxBadCRCName);
			rxErrors.append(BR);
		}
		if (getRxErrorFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxErrorName);
			rxErrors.append(BR);
		}
		if (getRxUndersizeFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxUndersizeName);
			rxErrors.append(BR);
		}
		if (getRxOversizeFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxOversizeName);
			rxErrors.append(BR);
		}
		if (getRxOverrunFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			rxErrors.append(rxOverrunName);
			rxErrors.append(BR);
		}
			
		return rxErrors.toString();
	}

	/**
	 * 
	 * if error counts > 1% of (all frames count), show if in port overview pop up window
	 * 
	 * @return
	 */
	@Transient
	public String getTxRatioOutOfLimits() {
		StringBuffer txErrors = new StringBuffer();
		if (getTxExcessiveCollisionFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			txErrors.append(txExcessiveCollisionName);
			txErrors.append(BR);
		}
		if (getTxMACTransmitErrorFramesRatio() > PORT_IS_ERROR_COUNT_RATIO) {
			txErrors.append(txMACTransmitErrorName);
			txErrors.append(BR);
		}
			
		return txErrors.toString();
	}
	
	@Transient
	public boolean isPortError() {
		if (!StringUtils.isEmpty(getRxRatioOutOfLimits()) 
				|| !StringUtils.isEmpty(getTxRatioOutOfLimits())) {
			return true;
		}
		return false;
	}
	//*************for port error proportion calculate********start**********
}