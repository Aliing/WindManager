package com.ah.bo.performance;

import java.sql.Timestamp;
import java.util.TimeZone;

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

import com.ah.bo.admin.HmDomain;
import com.ah.util.datetime.AhDateTimeUtil;

@Entity
@Table(name = "hm_interface_stats")
@org.hibernate.annotations.Table(appliesTo = "hm_interface_stats", indexes = {
		@Index(name = "IDX_INTERFACESTATS_TIMESTAMP", columnNames = {"timeStamp","apMac","OWNER"}),
		@Index(name = "IDX_INTERFACESTATS_OWNER", columnNames = {"OWNER"})
		})
public class AhInterfaceStats implements AhInterfaceStatsInterf {

	private static final long serialVersionUID = 1L;

	// bit 1
	public static final int	ALARMFLAG_CRCERROR				= 1;

	// bit 2
	public static final int	ALARMFLAG_TXDROP				= 2;

	// bit 3
	public static final int	ALARMFLAG_RXDROP				= 4;

	// bit 4
	public static final int	ALARMFLAG_TXRETRY				= 8;

	// bit 5
	public static final int	ALARMFLAG_AIRTIMECONSUMPTION	= 16;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long			id;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String				apName;

	@Column(length = MAC_ADDRESS_LENGTH)
	private String				apMac;

	private long			timeStamp;

	private short			collectPeriod;

	private int				ifIndex;

	private String			ifName;
	
	public static final int RADIOTYPE_24G=1;
	public static final int RADIOTYPE_5G=2;
	public static final int RADIOTYPE_OTHER=3;
	
	private int 			radioType=RADIOTYPE_24G;

	private long			txDrops;

	private long			rxDrops;

	private byte			crcErrorRate;
	
	private byte			txRetryRate;

	private byte			rxRetryRate;

	private long			uniTxFrameCount;

	private long			uniRxFrameCount;

	private long			bcastTxFrameCount;

	private long			bcastRxFrameCount;

	private byte			totalChannelUtilization;

	private byte			interferenceUtilization;

	private byte			txUtilization;

	private byte			rxUtilization;

	private short			noiseFloor;

	private byte			txAirTime;

	private byte			rxAirTime;

	/**
	 * {Tx rate,Tx bit rate distribution,Tx bit rate success distribution};{Same with previous
	 * one};....
	 */
	@Column(length = 1000)
	private String			txRateInfo;

	@Column(length = 1000)
	private String			rxRateInfo;

	private int				alarmFlag;

	private int				bandSteerSuppressCount;

	private int				loadBalanceSuppressCount;

	private int				weakSnrSuppressCount;

	private int				safetyNetAnswerCount;

	private int				probeRequestSuppressCount;

	private int				authRequestSuppressCount;

	private long 			txByteCount;
	
	private long 			rxByteCount;

	private byte 			totalTxBitSuccessRate;
	
	private byte 			totalRxBitSuccessRate;
	
	private long crcerrorframe;
	private long txretryframe;
	private long rxretryframe;
	private long txbcastbytecount;
	private long rxbcastbytecount;
	private long txdropframebysw;
	private long rxdropframebysw;
	private long txdropframebyhw;


	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain		owner;

	@Transient
	private TimeZone		tz;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public Timestamp getVersion() {
		return null;
	}

	@Override
	public String getLabel() {
		return "interface_stats";
	}

	@Transient
	private boolean	selected;

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {

	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public long getBcastRxFrameCount() {
		return bcastRxFrameCount;
	}

	public void setBcastRxFrameCount(long bcastRxFrameCount) {
		this.bcastRxFrameCount = bcastRxFrameCount;
	}

	public long getBcastTxFrameCount() {
		return bcastTxFrameCount;
	}

	public void setBcastTxFrameCount(long bcastTxFrameCount) {
		this.bcastTxFrameCount = bcastTxFrameCount;
	}

	public short getCollectPeriod() {
		return collectPeriod;
	}

	public void setCollectPeriod(short collectPeriod) {
		this.collectPeriod = collectPeriod;
	}

	public String getTimeStampString() {
		return AhDateTimeUtil.getSpecifyDateTime(timeStamp, tz,owner);
	}

	public byte getCrcErrorRate() {
		return crcErrorRate;
	}

	public void setCrcErrorRate(byte crcErrorRate) {
		this.crcErrorRate = crcErrorRate;
	}

	public int getIfIndex() {
		return ifIndex;
	}

	public void setIfIndex(int ifIndex) {
		this.ifIndex = ifIndex;
	}

	public String getIfName() {
		return ifName;
	}

	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	public byte getInterferenceUtilization() {
		return interferenceUtilization;
	}

	public void setInterferenceUtilization(byte interferenceUtilization) {
		this.interferenceUtilization = interferenceUtilization;
	}

	public short getNoiseFloor() {
		return noiseFloor;
	}

	public void setNoiseFloor(short noiseFloor) {
		this.noiseFloor = noiseFloor;
	}

	public byte getRxAirTime() {
		return rxAirTime;
	}

	public void setRxAirTime(byte rxAirTime) {
		this.rxAirTime = rxAirTime;
	}

	public long getRxDrops() {
		return rxDrops;
	}

	public void setRxDrops(long rxDrops) {
		this.rxDrops = rxDrops;
	}

	public byte getRxRetryRate() {
		return rxRetryRate;
	}

	public void setRxRetryRate(byte rxRetryRate) {
		this.rxRetryRate = rxRetryRate;
	}

	public byte getRxUtilization() {
		return rxUtilization;
	}

	public void setRxUtilization(byte rxUtilization) {
		this.rxUtilization = rxUtilization;
	}

	public byte getTotalChannelUtilization() {
		return totalChannelUtilization;
	}

	public void setTotalChannelUtilization(byte totalChannelUtilization) {
		this.totalChannelUtilization = totalChannelUtilization;
	}

	public byte getTxAirTime() {
		return txAirTime;
	}

	public void setTxAirTime(byte txAirTime) {
		this.txAirTime = txAirTime;
	}

	public long getTxDrops() {
		return txDrops;
	}

	public void setTxDrops(long txDrops) {
		this.txDrops = txDrops;
	}

	public byte getTxRetryRate() {
		return txRetryRate;
	}

	public void setTxRetryRate(byte txRetryRate) {
		this.txRetryRate = txRetryRate;
	}

	public byte getTxUtilization() {
		return txUtilization;
	}

	public void setTxUtilization(byte txUtilization) {
		this.txUtilization = txUtilization;
	}

	public long getUniRxFrameCount() {
		return uniRxFrameCount;
	}

	public void setUniRxFrameCount(long uniRxFrameCount) {
		this.uniRxFrameCount = uniRxFrameCount;
	}

	public long getUniTxFrameCount() {
		return uniTxFrameCount;
	}

	public void setUniTxFrameCount(long uniTxFrameCount) {
		this.uniTxFrameCount = uniTxFrameCount;
	}

	public String getRxRateInfo() {
		return rxRateInfo;
	}

	public void setRxRateInfo(String rxRateInfo) {
		this.rxRateInfo = rxRateInfo;
	}

	public String getTxRateInfo() {
		return txRateInfo;
	}

	public void setTxRateInfo(String txRateInfo) {
		this.txRateInfo = txRateInfo;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public TimeZone getTz() {
		return tz;
	}

	public void setTz(TimeZone tz) {
		this.tz = tz;
	}

	public int getAlarmFlag() {
		return alarmFlag;
	}

	public void setAlarmFlag(int alarmFlag) {
		this.alarmFlag = alarmFlag;
	}

	public int getBandSteerSuppressCount() {
		return bandSteerSuppressCount;
	}

	public void setBandSteerSuppressCount(int bandSteerSuppressCount) {
		this.bandSteerSuppressCount = bandSteerSuppressCount;
	}

	public int getLoadBalanceSuppressCount() {
		return loadBalanceSuppressCount;
	}

	public void setLoadBalanceSuppressCount(int loadBalanceSuppressCount) {
		this.loadBalanceSuppressCount = loadBalanceSuppressCount;
	}

	public int getWeakSnrSuppressCount() {
		return weakSnrSuppressCount;
	}

	public void setWeakSnrSuppressCount(int weakSnrSuppressCount) {
		this.weakSnrSuppressCount = weakSnrSuppressCount;
	}

	public int getSafetyNetAnswerCount() {
		return safetyNetAnswerCount;
	}

	public void setSafetyNetAnswerCount(int safetyNetAnswerCount) {
		this.safetyNetAnswerCount = safetyNetAnswerCount;
	}

	public int getProbeRequestSuppressCount() {
		return probeRequestSuppressCount;
	}

	public void setProbeRequestSuppressCount(int probeRequestSuppressCount) {
		this.probeRequestSuppressCount = probeRequestSuppressCount;
	}

	public int getAuthRequestSuppressCount() {
		return authRequestSuppressCount;
	}

	public void setAuthRequestSuppressCount(int authRequestSuppressCount) {
		this.authRequestSuppressCount = authRequestSuppressCount;
	}

	public long getTxByteCount() {
		return txByteCount;
	}

	public void setTxByteCount(long txByteCount) {
		this.txByteCount = txByteCount;
	}

	public long getRxByteCount() {
		return rxByteCount;
	}

	public void setRxByteCount(long rxByteCount) {
		this.rxByteCount = rxByteCount;
	}

	public byte getTotalTxBitSuccessRate() {
		return totalTxBitSuccessRate;
	}

	public void setTotalTxBitSuccessRate(byte totalTxBitSuccessRate) {
		this.totalTxBitSuccessRate = totalTxBitSuccessRate;
	}

	public byte getTotalRxBitSuccessRate() {
		return totalRxBitSuccessRate;
	}

	public void setTotalRxBitSuccessRate(byte totalRxBitSuccessRate) {
		this.totalRxBitSuccessRate = totalRxBitSuccessRate;
	}

	public int getRadioType() {
		return radioType;
	}

	public void setRadioType(int radioType) {
		this.radioType = radioType;
	}

	public long getCrcerrorframe() {
		return crcerrorframe;
	}

	public void setCrcerrorframe(long crcerrorframe) {
		this.crcerrorframe = crcerrorframe;
	}

	public long getRxretryframe() {
		return rxretryframe;
	}

	public void setRxretryframe(long rxretryframe) {
		this.rxretryframe = rxretryframe;
	}

	public long getTxbcastbytecount() {
		return txbcastbytecount;
	}

	public void setTxbcastbytecount(long txbcastbytecount) {
		this.txbcastbytecount = txbcastbytecount;
	}

	public long getRxbcastbytecount() {
		return rxbcastbytecount;
	}

	public void setRxbcastbytecount(long rxbcastbytecount) {
		this.rxbcastbytecount = rxbcastbytecount;
	}

	public long getTxdropframebysw() {
		return txdropframebysw;
	}

	public void setTxdropframebysw(long txdropframebysw) {
		this.txdropframebysw = txdropframebysw;
	}

	public long getRxdropframebysw() {
		return rxdropframebysw;
	}

	public void setRxdropframebysw(long rxdropframebysw) {
		this.rxdropframebysw = rxdropframebysw;
	}

	public long getTxdropframebyhw() {
		return txdropframebyhw;
	}

	public void setTxdropframebyhw(long txdropframebyhw) {
		this.txdropframebyhw = txdropframebyhw;
	}

	public long getTxretryframe() {
		return txretryframe;
	}

	public void setTxretryframe(long txretryframe) {
		this.txretryframe = txretryframe;
	}

}