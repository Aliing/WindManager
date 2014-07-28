package com.ah.be.communication.mo;

import java.io.Serializable;
import java.util.TimeZone;

import com.ah.be.communication.event.BeSpectralAnalysisEvent;
import com.ah.util.datetime.AhDateTimeUtil;

@SuppressWarnings("serial")
public class SpectralAnalysisInterference implements Serializable,Comparable<SpectralAnalysisInterference> {

	private String apName;
	
	private byte deviceType;
	
	private short signalMin;
	
	private short signalMax;
	
	private long time;
	
	private int centerFreq;
	
	private short bandwidth=BeSpectralAnalysisEvent.CHANNEL_WIDTH_20;
	
	private TimeZone	timeZone = TimeZone.getDefault();
	
	public String getDeviceTypeString() {
		if (deviceType==0) {
			return "None";
		} else if (deviceType==1) {
			return "Microwave Oven";
		} else if (deviceType==2) {
			return "Bluetooth";
		} else if (deviceType==3) {
			return "Cordless phone";
		} else if (deviceType==4) {
			return "Video bridge";
		} else {
			return "Other";
		}
	}
	
	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getTimeString() {
		return AhDateTimeUtil.getSpecifyDateTimeReport(time, timeZone);
	}
	
	public int getCenterFreq() {
		return centerFreq;
	}

	public void setCenterFreq(int centerFreq) {
		this.centerFreq = centerFreq;
	}

	public short getBandwidth() {
		return bandwidth;
	}
	
	public short getBandwidthValue() {
		if (bandwidth==BeSpectralAnalysisEvent.CHANNEL_WIDTH_20){
			return 20;
		}
		return 40;
	}

	public void setBandwidth(short bandwidth) {
		this.bandwidth = bandwidth;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public String getApName() {
		return apName;
	}

	public void setApName(String apName) {
		this.apName = apName;
	}

	public short getSignalMin() {
		return signalMin;
	}

	public void setSignalMin(short signalMin) {
		this.signalMin = signalMin;
	}

	public short getSignalMax() {
		return signalMax;
	}

	public void setSignalMax(short signalMax) {
		this.signalMax = signalMax;
	}

	public byte getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(byte deviceType) {
		this.deviceType = deviceType;
	}
	
	public int getChannel(){
		if (centerFreq==2484) {
			return 14;
		} else if (centerFreq < 2484) {
			  return (centerFreq-2407)/5;
		} else if (centerFreq < 5000) {
			  return 15+((centerFreq-2512)/20);
		} else {
			return (centerFreq - 5000)/5;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SpectralAnalysisInterference) {
			SpectralAnalysisInterference sai = (SpectralAnalysisInterference)obj;
			if (sai.getApName().equals(this.apName) &&
					sai.getBandwidth() == this.bandwidth &&
					sai.getCenterFreq() == this.centerFreq &&
					sai.getDeviceType() == this.deviceType) {
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public int compareTo(SpectralAnalysisInterference o) {
		long cmp = this.centerFreq - o.getCenterFreq();
		if (cmp == 0) {
			cmp = o.getTime() - this.time; 
		}
		return (int) cmp;
	}
}
