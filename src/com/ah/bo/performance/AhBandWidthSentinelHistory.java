package com.ah.bo.performance;

import java.sql.Timestamp;

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

import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.util.MgrUtil;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_BANDWIDTHSENTINEL_HISTORY")
@org.hibernate.annotations.Table(appliesTo = "HM_BANDWIDTHSENTINEL_HISTORY", indexes = {
		@Index(name = "BANDWIDTH_SENTINEL_HISTORY_OWNER", columnNames = { "OWNER" }),
		@Index(name = "BANDWIDTH_SENTINEL_HISTORY_TIME", columnNames = { "time" })
		})
public class AhBandWidthSentinelHistory implements HmBo  {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long			id;

	private String			apMac;
	
	private String			apName;

	private String			clientMac;

	private int				ifIndex;

	public static final int	STATUS_ALERT	= 0;
	public static final int	STATUS_CLEAR	= 1;
	public static final int	STATUS_BAD		= 2;
	//just used in client session
	public static final int	STATUS_ACTION	= 3;

	private int				bandWidthSentinelStatus = STATUS_CLEAR;

	// Kbps
	private int				guaranteedBandWidth;

	// Kbps
	private int				actualBandWidth;
	
	private byte			channelUltil=-1;
	
	private byte			interferenceUltil=-1;
	
	private byte 			txUltil=-1;
	
	private byte			rxUltil=-1;
	// 0: no log, no boost
	// 1: log, no boost
	// 2: no log, boost
	// 3: log, boost
	public static final int ACTION_STATUS_LOG_NO = 0;
	public static final int ACTION_STATUS_LOG_YES = 1;
	public static final int ACTION_STATUS_BOOST_NO = 2;
	public static final int ACTION_STATUS_BOOST_YES = 3;
	private int				action;

	private HmTimeStamp		timeStamp = HmTimeStamp.CURRENT_TIMESTAMP;

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return "Bandwidth sentinel History";
	}

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

	@ManyToOne(fetch = FetchType.EAGER)
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

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	public int getActualBandWidth() {
		return actualBandWidth;
	}

	public void setActualBandWidth(int actualBandWidth) {
		this.actualBandWidth = actualBandWidth;
	}

	public byte getChannelUltil() {
		return channelUltil;
	}

	public void setChannelUltil(byte channelUltil) {
		this.channelUltil = channelUltil;
	}

	public byte getInterferenceUltil() {
		return interferenceUltil;
	}

	public void setInterferenceUltil(byte interferenceUltil) {
		this.interferenceUltil = interferenceUltil;
	}

	public byte getTxUltil() {
		return txUltil;
	}

	public void setTxUltil(byte txUltil) {
		this.txUltil = txUltil;
	}

	public byte getRxUltil() {
		return rxUltil;
	}

	public void setRxUltil(byte rxUltil) {
		this.rxUltil = rxUltil;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public int getBandWidthSentinelStatus() {
		return bandWidthSentinelStatus;
	}

	public void setBandWidthSentinelStatus(int bandWidthSentinel) {
		this.bandWidthSentinelStatus = bandWidthSentinel;
	}

	public String getClientMac() {
		return clientMac;
	}

	public void setClientMac(String clientMac) {
		this.clientMac = clientMac;
	}

	public int getGuaranteedBandWidth() {
		return guaranteedBandWidth;
	}

	public void setGuaranteedBandWidth(int guaranteedBandWidth) {
		this.guaranteedBandWidth = guaranteedBandWidth;
	}
	
	public String getBandWidthSentinelStatusString(){
		if (bandWidthSentinelStatus == STATUS_BAD){
			return MgrUtil.getUserMessage("report.reportList.sla.status.bad");
		} else if (bandWidthSentinelStatus == STATUS_ALERT){
			return MgrUtil.getUserMessage("report.reportList.sla.status.alert");
		} else {
			if (action ==ACTION_STATUS_BOOST_YES) {
				return MgrUtil.getUserMessage("report.reportList.sla.status.action");
			} else {
				return MgrUtil.getUserMessage("report.reportList.sla.status.clear");
			}
		}
	}
	
	public String getDomainTimeString(){
		return AhDateTimeUtil.getSpecifyDateTimeReport(timeStamp.getTime(), owner.getTimeZone());
	}
	
	public String getTimeString(){
		return AhDateTimeUtil.getFormattedDateTimeReport(timeStamp);
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

}