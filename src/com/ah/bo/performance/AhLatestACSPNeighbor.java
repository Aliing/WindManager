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
import com.ah.ui.actions.BaseAction;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "HM_LATESTACSPNEIGHBOR")
@org.hibernate.annotations.Table(appliesTo = "HM_LATESTACSPNEIGHBOR", indexes = {
		@Index(name = "LATEST_ACSP_NEIGHBOR_OWNER", columnNames = { "OWNER" }),
		@Index(name = "LATEST_ACSP_NEIGHBOR_AP_MAC", columnNames = { "APMAC" })
		})
public class AhLatestACSPNeighbor implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long		id;

	private String		apMac;

	private int			ifIndex;

	private String		neighborMac;

	private String		neighborRadioMac;

	private long		lastSeen;

	private int			channelNumber;

	private byte		txPower;

	private byte		rssi;

	private String		ssid;

	// private Date statTime;

	private String		bssid;

	private HmTimeStamp	timeStamp = HmTimeStamp.CURRENT_TIMESTAMP;

	@Override
	public Long getId() {
		return id;
	}

	// Label used in error messages
	@Override
	public String getLabel() {
		return apMac + "_acspNeighbor";
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

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public void setChannelNumber(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	public long getLastSeen() {
		return lastSeen;
	}

	public void setLastSeen(long lastSeen) {
		this.lastSeen = lastSeen;
	}

	public String getNeighborMac() {
		return neighborMac;
	}

	public void setNeighborMac(String neighborMac) {
		this.neighborMac = neighborMac;
	}

	public byte getRssi() {
		return rssi;
	}

	public void setRssi(byte rssi) {
		this.rssi = rssi;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public byte getTxPower() {
		return txPower;
	}

	public void setTxPower(byte txPower) {
		this.txPower = txPower;
	}

	// public Date getStatTime() {
	// return statTime;
	// }
	//
	// public void setStatTime(Date statTime) {
	// this.statTime = statTime;
	// }

	public String getNeighborRadioMac() {
		return neighborRadioMac;
	}

	public void setNeighborRadioMac(String neighborRadioMac) {
		this.neighborRadioMac = neighborRadioMac;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	@Transient
	public String getReportTimeString() {
		// if (null != statTime && statTime.getTime() > 0) {
		// try {
		// SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// return sf.format(statTime);
		// } catch (Exception e) {
		// }
		// }
		// return "";

		if(BaseAction.getSessionUserContext() !=null && BaseAction.getSessionUserContext().getDomain() != null){
			loginUser = BaseAction.getSessionUserContext().getSwitchDomain() == null ? BaseAction.getSessionUserContext().getDomain()
					: BaseAction.getSessionUserContext().getSwitchDomain();
			return AhDateTimeUtil.getFormattedDateTime(timeStamp, loginUser != null ? loginUser : owner);
		}else{
			return AhDateTimeUtil.getFormattedDateTimeReport(timeStamp);
		}
	}

	@Transient
	public String getRssiDbm() {
		return (rssi - 95) + " dBm";
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public HmTimeStamp getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(HmTimeStamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Transient
	private HmDomain loginUser;
	
	public void setLoginUser(HmDomain loginUser){
		this.loginUser = loginUser;
	}
}