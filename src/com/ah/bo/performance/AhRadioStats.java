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
@Table(name = "HM_RADIOSTATS", uniqueConstraints = { @UniqueConstraint( columnNames = { "statTimeStamp", "apName", "ifIndex" } ) })
@org.hibernate.annotations.Table(appliesTo = "HM_RADIOSTATS", indexes = {
		@Index(name = "RADIO_STATS_OWNER", columnNames = { "OWNER"})
		})
public class AhRadioStats implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private XIfPK	xifpk;

	// @OneToOne(mappedBy = "ahRadioStats", fetch = FetchType.EAGER)
	// private AhXIf xifInfo;

	@Column(length = 14)
	private String	apSerialNumber;

	private String	apMac;

	private long	radioTxDataFrames;

	private long	radioTxBeDataFrames;

	private long	radioTxBgDataFrames;

	private long	radioTxViDataFrames;

	private long	radioTxVoDataFrames;

	private long	radioTxNonBeaconMgtFrames;

	private long	radioTxUnicastDataFrames;

	private long	radioTxMulticastDataFrames;

	private long	radioTxBroadcastDataFrames;

	private long	radioTxBeaconFrames;

	private long	radioTxTotalRetries;

	private long	radioTxTotalFramesDropped;

	private long	radioTxTotalFrameErrors;

	private long	radioTxFEForExcessiveHWRetries;

	private long	radioTXRTSFailures;

	private long	radioRxTotalDataFrames;

	private long	radioRxUnicastDataFrames;

	private long	radioRxMulticastDataFrames;

	private long	radioRxBroadcastDataFrames;

	private long	radioRxMgtFrames;

	private long	radioRxTotalFrameDropped;

	private double	radioTxAirtime;

	private double	radioRxAirtime;

	// unit is Kpbs
	private int		bandWidth;

	public long getRadioRxMgtFrames() {
		return radioRxMgtFrames;
	}

	public void setRadioRxMgtFrames(long radioRxMgtFrames) {
		this.radioRxMgtFrames = radioRxMgtFrames;
	}

	public long getRadioRxTotalDataFrames() {
		return radioRxTotalDataFrames;
	}

	public void setRadioRxTotalDataFrames(long radioRxTotalDataFrames) {
		this.radioRxTotalDataFrames = radioRxTotalDataFrames;
	}

	public long getRadioRxTotalFrameDropped() {
		return radioRxTotalFrameDropped;
	}

	public void setRadioRxTotalFrameDropped(long radioRxTotalFrameDropped) {
		this.radioRxTotalFrameDropped = radioRxTotalFrameDropped;
	}

	public long getRadioTxBeaconFrames() {
		return radioTxBeaconFrames;
	}

	public void setRadioTxBeaconFrames(long radioTxBeaconFrames) {
		this.radioTxBeaconFrames = radioTxBeaconFrames;
	}

	public long getRadioTxFEForExcessiveHWRetries() {
		return radioTxFEForExcessiveHWRetries;
	}

	public void setRadioTxFEForExcessiveHWRetries(long radioTxFEForExcessiveHWRetries) {
		this.radioTxFEForExcessiveHWRetries = radioTxFEForExcessiveHWRetries;
	}

	public long getRadioTxDataFrames() {
		return radioTxDataFrames;
	}

	public void setRadioTxDataFrames(long radioTxFrames) {
		this.radioTxDataFrames = radioTxFrames;
	}

	public long getRadioTxNonBeaconMgtFrames() {
		return radioTxNonBeaconMgtFrames;
	}

	public void setRadioTxNonBeaconMgtFrames(long radioTxNonBeaconMgtFrames) {
		this.radioTxNonBeaconMgtFrames = radioTxNonBeaconMgtFrames;
	}

	public long getRadioRxBroadcastDataFrames() {
		return radioRxBroadcastDataFrames;
	}

	public void setRadioRxBroadcastDataFrames(long radioRxBroadcastDataFrames) {
		this.radioRxBroadcastDataFrames = radioRxBroadcastDataFrames;
	}

	public long getRadioRxMulticastDataFrames() {
		return radioRxMulticastDataFrames;
	}

	public void setRadioRxMulticastDataFrames(long radioRxMulticastDataFrames) {
		this.radioRxMulticastDataFrames = radioRxMulticastDataFrames;
	}

	public long getRadioRxUnicastDataFrames() {
		return radioRxUnicastDataFrames;
	}

	public void setRadioRxUnicastDataFrames(long radioRxUnicastDataFrames) {
		this.radioRxUnicastDataFrames = radioRxUnicastDataFrames;
	}

	public long getRadioTxBroadcastDataFrames() {
		return radioTxBroadcastDataFrames;
	}

	public void setRadioTxBroadcastDataFrames(long radioTxBroadcastDataFrames) {
		this.radioTxBroadcastDataFrames = radioTxBroadcastDataFrames;
	}

	public long getRadioTxMulticastDataFrames() {
		return radioTxMulticastDataFrames;
	}

	public void setRadioTxMulticastDataFrames(long radioTxMulticastDataFrames) {
		this.radioTxMulticastDataFrames = radioTxMulticastDataFrames;
	}

	public long getRadioTxUnicastDataFrames() {
		return radioTxUnicastDataFrames;
	}

	public void setRadioTxUnicastDataFrames(long radioTxUnicastDataFrames) {
		this.radioTxUnicastDataFrames = radioTxUnicastDataFrames;
	}

	public long getRadioTxTotalFrameErrors() {
		return radioTxTotalFrameErrors;
	}

	public void setRadioTxTotalFrameErrors(long radioTxTotalFrameErrors) {
		this.radioTxTotalFrameErrors = radioTxTotalFrameErrors;
	}

	public long getRadioTxTotalFramesDropped() {
		return radioTxTotalFramesDropped;
	}

	public void setRadioTxTotalFramesDropped(long radioTxTotalFramesDropped) {
		this.radioTxTotalFramesDropped = radioTxTotalFramesDropped;
	}

	public long getRadioTxTotalRetries() {
		return radioTxTotalRetries;
	}

	public void setRadioTxTotalRetries(long radioTxTotalRetries) {
		this.radioTxTotalRetries = radioTxTotalRetries;
	}

	// --------implement interface function--------
	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "ahRadioStats";
	}

	// For multi page selection
	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	/*
	 * statistic objects have no owner or version,because it created by system, it can't be updated
	 * and 'statTime' keep track of when create it.
	 */
	/**
	 * modify mark: add owner field for VHM
	 */

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain	owner;

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

	// public AhXIf getXifInfo() {
	// return xifInfo;
	// }
	//
	// public void setXifInfo(AhXIf xifInfo) {
	// this.xifInfo = xifInfo;
	// }

	public XIfPK getXifpk() {
		return xifpk;
	}

	public void setXifpk(XIfPK xifpk) {
		this.xifpk = xifpk;
	}

	public long getRadioTxBeDataFrames() {
		return radioTxBeDataFrames;
	}

	public void setRadioTxBeDataFrames(long radioTxBeDataFrames) {
		this.radioTxBeDataFrames = radioTxBeDataFrames;
	}

	public long getRadioTxBgDataFrames() {
		return radioTxBgDataFrames;
	}

	public void setRadioTxBgDataFrames(long radioTxBgDataFrames) {
		this.radioTxBgDataFrames = radioTxBgDataFrames;
	}

	public long getRadioTxViDataFrames() {
		return radioTxViDataFrames;
	}

	public void setRadioTxViDataFrames(long radioTxViDataFrames) {
		this.radioTxViDataFrames = radioTxViDataFrames;
	}

	public long getRadioTxVoDataFrames() {
		return radioTxVoDataFrames;
	}

	public void setRadioTxVoDataFrames(long radioTxVoDataFrames) {
		this.radioTxVoDataFrames = radioTxVoDataFrames;
	}

	public long getRadioTXRTSFailures() {
		return radioTXRTSFailures;
	}

	public void setRadioTXRTSFailures(long radioTXRTSFailures) {
		this.radioTXRTSFailures = radioTXRTSFailures;
	}

	public double getRadioRxAirtime() {
		return radioRxAirtime;
	}

	public void setRadioRxAirtime(double radioRxAirtime) {
		this.radioRxAirtime = radioRxAirtime;
	}

	public double getRadioTxAirtime() {
		return radioTxAirtime;
	}

	public void setRadioTxAirtime(double radioTxAirtime) {
		this.radioTxAirtime = radioTxAirtime;
	}

	@Override
	public void setId(Long id) {
		this.id=id;
	}

	@Override
	public void setVersion(Timestamp version) {
	}

	public int getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(int bindWidth) {
		this.bandWidth = bindWidth;
	}

}