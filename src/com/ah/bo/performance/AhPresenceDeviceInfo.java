package com.ah.bo.performance;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.ah.util.coder.encoder.AESEncrypt;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("DeviceInfo")
public class AhPresenceDeviceInfo {
	@XStreamAlias("si")
	private String stationIdEncrypt;
	@XStreamOmitField
	private String stationIdUnEncrypt;
	@XStreamAlias("bi")
	private String bssIdEncrypt;
	@XStreamOmitField
	private String bssIdUnEncrypt;
	@XStreamAlias("sm")
	private String stationOui;
	@XStreamAlias("ap")
	private short isAp;
	@XStreamAlias("cn")
	private long frameNumber;
	@XStreamAlias("ot")
	private long firstFrameTimeStamp;
	@XStreamAlias("ct")
	private long lastFrameTimeStamp;
	@XStreamAlias("cf")
	private int frequency;
	@XStreamAlias("is")
	private long intervalSum;
	@XStreamAlias("i2")
	private long intervalSquaredSum;
	@XStreamAlias("i3")
	private long intervalCubedSum;
	@XStreamAlias("il")
	private long minInterval;
	@XStreamAlias("ih")
	private long maxInterval;
	@XStreamAlias("ss")
	private int sigStrength;
	@XStreamAlias("s2")
	private long sigStrenghSquared;
	@XStreamAlias("s3")
	private long sigStrenghCubed;
	@XStreamAlias("sl")
	private byte minSigStrength;
	@XStreamAlias("sh")
	private byte maxSigStrength;
	@XStreamAlias("so")
	private byte firstSigStrength;
	@XStreamAlias("sc")
	private byte lastSigStrength;


	public String getStationIdEncrypt() {
		return stationIdEncrypt;
	}
	public void setStationIdEncrypt(String stationId) {
		try {
			this.stationIdUnEncrypt = stationId;
			this.stationIdEncrypt = AESEncrypt.encrypt(stationId);
		}catch(Exception e) {
			this.stationIdEncrypt = stationId;
		}
	}
	public String getStationIdUnEncrypt() {
		return stationIdUnEncrypt;
	}
	public void setStationIdUnEncrypt(String stationIdUnEncrypt) {
		this.stationIdUnEncrypt = stationIdUnEncrypt;
	}
	public String getBssIdEncrypt() {
		return bssIdEncrypt;
	}
	public void setBssIdEncrypt(String bssId) {
		try {
			this.bssIdUnEncrypt = bssId;
			if (null != bssId && !bssId.equalsIgnoreCase("FF:FF:FF:FF:FF:FF")) {
				this.bssIdEncrypt = AESEncrypt.encrypt(bssId);
			}
		} catch (Exception e) {
			this.bssIdEncrypt = bssId;
		}
	}
	public String getBssIdUnEncrypt() {
		return bssIdUnEncrypt;
	}
	public void setBssIdUnEncrypt(String bssIdUnEncrypt) {
		this.bssIdUnEncrypt = bssIdUnEncrypt;
	}
	public String getStationOui() {
		return stationOui;
	}
	public void setStationOui(String stationOui) {
		this.stationOui = stationOui;
	}
	public short getIsAp() {
		return isAp;
	}
	public void setIsAp(short isAp) {
		this.isAp = isAp;
	}
	public long getFrameNumber() {
		return frameNumber;
	}
	public void setFrameNumber(long frameNumber) {
		this.frameNumber = frameNumber;
	}
	public long getFirstFrameTimeStamp() {
		return firstFrameTimeStamp;
	}
	public void setFirstFrameTimeStamp(long firstFrameTimeStamp) {
		this.firstFrameTimeStamp = firstFrameTimeStamp;
	}
	public long getLastFrameTimeStamp() {
		return lastFrameTimeStamp;
	}
	public void setLastFrameTimeStamp(long lastFrameTimeStamp) {
		this.lastFrameTimeStamp = lastFrameTimeStamp;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public long getIntervalSum() {
		return intervalSum;
	}
	public void setIntervalSum(long intervalSum) {
		this.intervalSum = intervalSum;
	}
	public long getIntervalSquaredSum() {
		return intervalSquaredSum;
	}
	public void setIntervalSquaredSum(long intervalSquaredSum) {
		this.intervalSquaredSum = intervalSquaredSum;
	}
	public long getIntervalCubedSum() {
		return intervalCubedSum;
	}
	public void setIntervalCubedSum(long intervalCubedSum) {
		this.intervalCubedSum = intervalCubedSum;
	}
	public long getMinInterval() {
		return minInterval;
	}
	public void setMinInterval(long minInterval) {
		this.minInterval = minInterval;
	}
	public long getMaxInterval() {
		return maxInterval;
	}
	public void setMaxInterval(long maxInterval) {
		this.maxInterval = maxInterval;
	}
	public int getSigStrength() {
		return sigStrength;
	}
	public void setSigStrength(int sigStrength) {
		this.sigStrength = sigStrength;
	}
	public long getSigStrenghSquared() {
		return sigStrenghSquared;
	}
	public void setSigStrenghSquared(long sigStrenghSquared) {
		this.sigStrenghSquared = sigStrenghSquared;
	}
	public long getSigStrenghCubed() {
		return sigStrenghCubed;
	}
	public void setSigStrenghCubed(long sigStrenghCubed) {
		this.sigStrenghCubed = sigStrenghCubed;
	}
	public byte getMinSigStrength() {
		return minSigStrength;
	}
	public void setMinSigStrength(byte minSigStrength) {
		this.minSigStrength = minSigStrength;
	}
	public byte getMaxSigStrength() {
		return maxSigStrength;
	}
	public void setMaxSigStrength(byte maxSigStrength) {
		this.maxSigStrength = maxSigStrength;
	}
	public byte getFirstSigStrength() {
		return firstSigStrength;
	}
	public void setFirstSigStrength(byte firstSigStrength) {
		this.firstSigStrength = firstSigStrength;
	}
	public byte getLastSigStrength() {
		return lastSigStrength;
	}
	public void setLastSigStrength(byte lastSigStrength) {
		this.lastSigStrength = lastSigStrength;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}