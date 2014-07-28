package com.ah.be.communication.mo;

import com.ah.be.communication.event.BeSpectralAnalysisEvent;

public class SpectralAnalysisData implements Comparable<SpectralAnalysisData> {
	
	private long timeStamp;

	private byte tag;

	private int length;

	private int chnInfoLen;
	
	private short chnFreq;
	
	private byte chnWidth = BeSpectralAnalysisEvent.CHANNEL_WIDTH_20;
	
	private short pwrRspLen;
	
	private short pwrRsp[];
	
	private short dutyCycleLen;
	
	private short dutyCycle[];
	
	private byte interfCount;
	
	private byte interfType[];
	
	private short interfMin[];

	private short interfMax[];

	public byte getTag() {
		return tag;
	}

	public void setTag(byte tag) {
		this.tag = tag;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getChnInfoLen() {
		return chnInfoLen;
	}

	public void setChnInfoLen(int chnInfoLen) {
		this.chnInfoLen = chnInfoLen;
	}

	public short getChnFreq() {
		return chnFreq;
	}

	public void setChnFreq(short chnFreq) {
		this.chnFreq = chnFreq;
	}

	public short getPwrRspLen() {
		return pwrRspLen;
	}

	public void setPwrRspLen(short pwrRspLen) {
		this.pwrRspLen = pwrRspLen;
	}

	public short[] getPwrRsp() {
		return pwrRsp;
	}

	public void setPwrRsp(short[] pwrRsp) {
		this.pwrRsp = pwrRsp;
	}

	public byte getInterfCount() {
		return interfCount;
	}

	public void setInterfCount(byte interfCount) {
		this.interfCount = interfCount;
	}

	public byte[] getInterfType() {
		return interfType;
	}

	public void setInterfType(byte[] interfType) {
		this.interfType = interfType;
	}

	public short[] getInterfMin() {
		return interfMin;
	}

	public void setInterfMin(short[] interfMin) {
		this.interfMin = interfMin;
	}

	public short[] getInterfMax() {
		return interfMax;
	}

	public void setInterfMax(short[] interfMax) {
		this.interfMax = interfMax;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public byte getChnWidth() {
		return chnWidth;
	}

	public void setChnWidth(byte chnWidth) {
		this.chnWidth = chnWidth;
	}
	
	public short getDutyCycleLen() {
		return dutyCycleLen;
	}

	public void setDutyCycleLen(short dutyCycleLen) {
		this.dutyCycleLen = dutyCycleLen;
	}

	public short[] getDutyCycle() {
		return dutyCycle;
	}

	public void setDutyCycle(short[] dutyCycle) {
		this.dutyCycle = dutyCycle;
	}
	
	@Override
	public int compareTo(SpectralAnalysisData o) {
		return (this.chnFreq - o.getChnFreq());
	}

	public short[] getPwrRsp(short pwrRspLen) {
		if (this.pwrRsp == null){
			this.pwrRsp = new short[pwrRspLen];
			SpectralAnalysisDataSample.setDefault(this.pwrRsp, (short)-1);
		}
		
		return this.pwrRsp;
	}
	
	public short[] getDutyCycle(short dutyCycleLen) {
		if (this.dutyCycle == null){
			this.dutyCycle = new short[dutyCycleLen];
			SpectralAnalysisDataSample.setDefault(this.dutyCycle, (short)-1);
		}
		
		return this.dutyCycle;
	}
}
