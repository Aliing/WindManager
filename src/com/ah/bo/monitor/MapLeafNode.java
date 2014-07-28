package com.ah.bo.monitor;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;

import com.ah.bo.hiveap.HiveAp;

/*
 * @author Chris Scheers
 */

@Entity
@DiscriminatorValue("LN")
public class MapLeafNode extends MapNode {

	private static final long serialVersionUID = 1L;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HIVE_AP_ID", nullable = true)
	private HiveAp hiveAp;

	@Column(length = MAC_ADDRESS_LENGTH)
	@Index(name = "map_apId")
	private String apId;

	@Column(length = DEFAULT_STRING_LENGTH)
	private String apName;

	private String ethId;

	// the default value should be true; otherwise, when it polling new
	// links(just have Ethernet link case), it will not notify mapAlarmCache
	// to do refresh.
	private boolean fetchLinksTimeout = true;

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public String getApId() {
		return apId;
	}

	public void setApId(String apId) {
		this.apId = apId;
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public void setHiveAp(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}

	public String getEthId() {
		return ethId;
	}

	public void setEthId(String ethId) {
		this.ethId = ethId;
	}

	public boolean isFetchLinksTimeout() {
		return fetchLinksTimeout;
	}

	public void setFetchLinksTimeout(boolean fetchLinksTimeout) {
		this.fetchLinksTimeout = fetchLinksTimeout;
	}

	public boolean isLeafNode() {
		return true;
	}

	public String getLabel() {
		return apId;
	}

	@Transient
	private short radioChannelA = 0, radioChannelBG = 0, radioTxPowerA = 0,
			radioTxPowerBG = 0, radioInterferenceA = 1,
			radioInterferenceBG = 1;
	@Transient
	private float radioEirpA = 0, radioEirpBG = 0;

	@Transient
	private boolean autoChannelA, autoChannelBG, autoTxPowerA, autoTxPowerBG;

	@Transient
	double xm, ym;

	public double getXm() {
		return xm;
	}

	public void setXm(double xm) {
		this.xm = xm;
	}

	public double getYm() {
		return ym;
	}

	public void setYm(double ym) {
		this.ym = ym;
	}

	public boolean isAutoChannelA() {
		return autoChannelA;
	}

	public void setAutoChannelA(boolean autoChannelA) {
		this.autoChannelA = autoChannelA;
	}

	public boolean isAutoChannelBG() {
		return autoChannelBG;
	}

	public void setAutoChannelBG(boolean autoChannelBG) {
		this.autoChannelBG = autoChannelBG;
	}

	public boolean isAutoTxPowerA() {
		return autoTxPowerA;
	}

	public void setAutoTxPowerA(boolean autoTxPowerA) {
		this.autoTxPowerA = autoTxPowerA;
	}

	public boolean isAutoTxPowerBG() {
		return autoTxPowerBG;
	}

	public void setAutoTxPowerBG(boolean autoTxPowerBG) {
		this.autoTxPowerBG = autoTxPowerBG;
	}

	public short getRadioChannelA() {
		return radioChannelA;
	}

	public void setRadioChannelA(short radioChannelA) {
		this.radioChannelA = radioChannelA;
	}

	public short getRadioChannelBG() {
		return radioChannelBG;
	}

	public void setRadioChannelBG(short radioChannelBG) {
		this.radioChannelBG = radioChannelBG;
	}

	public short getRadioTxPowerA() {
		return radioTxPowerA;
	}

	public void setRadioTxPowerA(short radioTxPowerA) {
		this.radioTxPowerA = radioTxPowerA;
	}

	public short getRadioTxPowerBG() {
		return radioTxPowerBG;
	}

	public void setRadioTxPowerBG(short radioTxPowerBG) {
		this.radioTxPowerBG = radioTxPowerBG;
	}

	public float getRadioEirpA() {
		return radioEirpA;
	}

	public void setRadioEirpA(float radioEirpA) {
		this.radioEirpA = radioEirpA;
	}

	public float getRadioEirpBG() {
		return radioEirpBG;
	}

	public void setRadioEirpBG(float radioEirpBG) {
		this.radioEirpBG = radioEirpBG;
	}

	public short getRadioInterferenceA() {
		return radioInterferenceA;
	}

	public void setRadioInterferenceA(short radioInterferenceA) {
		this.radioInterferenceA = radioInterferenceA;
	}

	public short getRadioInterferenceBG() {
		return radioInterferenceBG;
	}

	public void setRadioInterferenceBG(short radioInterferenceBG) {
		this.radioInterferenceBG = radioInterferenceBG;
	}

}