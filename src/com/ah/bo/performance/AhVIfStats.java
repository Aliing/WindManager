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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "HM_VIFSTATS",uniqueConstraints = { @UniqueConstraint( columnNames = { "statTimeStamp", "apName", "ifIndex" } ) })
@org.hibernate.annotations.Table(appliesTo = "HM_VIFSTATS", indexes = {
		@Index(name = "VIF_STATS_OWNER", columnNames = { "OWNER"})
		})
public class AhVIfStats implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private XIfPK xifpk;

	// @OneToOne(mappedBy = "ahVIfStats", fetch = FetchType.EAGER)
	// private AhXIf xifInfo;

	private String apMac;

	@Column(length = 14)
	private String apSerialNumber;

	private long rxVIfDataFrames;

	private long rxVIfUnicastDataFrames;

	private long rxVIfMulticastDataFrames;

	private long rxVIfBroadcastDataFrames;

	private long rxVIfErrorFrames;

	private long rxVIfDroppedFrames;

	private long txVIfDataFrames;

	private long txVIfBeDataFrames;

	private long txVIfBgDataFrames;

	private long txVIfViDataFrames;

	private long txVIfVoDataFrames;

	private long txVIfUnicastDataFrames;

	private long txVIfMulticastDataFrames;

	private long txVIfBroadcastDataFrames;

	private long txVIfErrorFrames;

	private long txVIfDroppedFrames;

	private double txVifAirtime;

	private double rxVifAirtime;

	public long getRxVIfDataFrames() {
		return rxVIfDataFrames;
	}

	public void setRxVIfDataFrames(long rxVIfDataFrames) {
		this.rxVIfDataFrames = rxVIfDataFrames;
	}

	public long getRxVIfDroppedFrames() {
		return rxVIfDroppedFrames;
	}

	public void setRxVIfDroppedFrames(long rxVIfDroppedFrames) {
		this.rxVIfDroppedFrames = rxVIfDroppedFrames;
	}

	public long getRxVIfErrorFrames() {
		return rxVIfErrorFrames;
	}

	public void setRxVIfErrorFrames(long rxVIfErrorFrames) {
		this.rxVIfErrorFrames = rxVIfErrorFrames;
	}

	public long getTxVIfDataFrames() {
		return txVIfDataFrames;
	}

	public void setTxVIfDataFrames(long txVIfDataFrames) {
		this.txVIfDataFrames = txVIfDataFrames;
	}

	public long getTxVIfDroppedFrames() {
		return txVIfDroppedFrames;
	}

	public void setTxVIfDroppedFrames(long txVIfDroppedFrames) {
		this.txVIfDroppedFrames = txVIfDroppedFrames;
	}

	public long getTxVIfErrorFrames() {
		return txVIfErrorFrames;
	}

	public void setTxVIfErrorFrames(long txVIfErrorFrames) {
		this.txVIfErrorFrames = txVIfErrorFrames;
	}

	// public AhXIf getXifInfo() {
	// return xifInfo;
	// }
	//
	// public void setXifInfo(AhXIf xifInfo) {
	// this.xifInfo = xifInfo;
	// }

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

	public XIfPK getXifpk() {
		return xifpk;
	}

	public void setXifpk(XIfPK xifpk) {
		this.xifpk = xifpk;
	}

	public long getRxVIfBroadcastDataFrames() {
		return rxVIfBroadcastDataFrames;
	}

	public void setRxVIfBroadcastDataFrames(long rxVIfBroadcastDataFrames) {
		this.rxVIfBroadcastDataFrames = rxVIfBroadcastDataFrames;
	}

	public long getRxVIfMulticastDataFrames() {
		return rxVIfMulticastDataFrames;
	}

	public void setRxVIfMulticastDataFrames(long rxVIfMulticastDataFrames) {
		this.rxVIfMulticastDataFrames = rxVIfMulticastDataFrames;
	}

	public long getRxVIfUnicastDataFrames() {
		return rxVIfUnicastDataFrames;
	}

	public void setRxVIfUnicastDataFrames(long rxVIfUnicastDataFrames) {
		this.rxVIfUnicastDataFrames = rxVIfUnicastDataFrames;
	}

	public long getTxVIfBroadcastDataFrames() {
		return txVIfBroadcastDataFrames;
	}

	public void setTxVIfBroadcastDataFrames(long txVIfBroadcastDataFrames) {
		this.txVIfBroadcastDataFrames = txVIfBroadcastDataFrames;
	}

	public long getTxVIfMulticastDataFrames() {
		return txVIfMulticastDataFrames;
	}

	public void setTxVIfMulticastDataFrames(long txVIfMulticastDataFrames) {
		this.txVIfMulticastDataFrames = txVIfMulticastDataFrames;
	}

	public long getTxVIfUnicastDataFrames() {
		return txVIfUnicastDataFrames;
	}

	public void setTxVIfUnicastDataFrames(long txVIfUnicastDataFrames) {
		this.txVIfUnicastDataFrames = txVIfUnicastDataFrames;
	}

	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "AhVifStats";
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

	public long getTxVIfBeDataFrames() {
		return txVIfBeDataFrames;
	}

	public void setTxVIfBeDataFrames(long txVIfBeDataFrames) {
		this.txVIfBeDataFrames = txVIfBeDataFrames;
	}

	public long getTxVIfBgDataFrames() {
		return txVIfBgDataFrames;
	}

	public void setTxVIfBgDataFrames(long txVIfBgDataFrames) {
		this.txVIfBgDataFrames = txVIfBgDataFrames;
	}

	public long getTxVIfViDataFrames() {
		return txVIfViDataFrames;
	}

	public void setTxVIfViDataFrames(long txVIfViDataFrames) {
		this.txVIfViDataFrames = txVIfViDataFrames;
	}

	public long getTxVIfVoDataFrames() {
		return txVIfVoDataFrames;
	}

	public void setTxVIfVoDataFrames(long txVIfVoDataFrames) {
		this.txVIfVoDataFrames = txVIfVoDataFrames;
	}

	public double getRxVifAirtime() {
		return rxVifAirtime;
	}

	public void setRxVifAirtime(double rxVifAirtime) {
		this.rxVifAirtime = rxVifAirtime;
	}

	public double getTxVifAirtime() {
		return txVifAirtime;
	}

	public void setTxVifAirtime(double txVifAirtime) {
		this.txVifAirtime = txVifAirtime;
	}

	@Override
	public void setId(Long id)
	{
		this.id=id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

}