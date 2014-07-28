package com.ah.bo.performance;

import java.util.TimeZone;

import com.ah.bo.HmBo;

public interface AhInterfaceStatsInterf extends HmBo {

	public String getTimeStampString();

	public byte getCrcErrorRate();
	public void setCrcErrorRate(byte crcErrorRate);

	public byte getInterferenceUtilization();
	public void setInterferenceUtilization(byte interferenceUtilization);

	public short getNoiseFloor();
	public void setNoiseFloor(short noiseFloor);

	public byte getRxAirTime();
	public void setRxAirTime(byte rxAirTime);

	public long getRxDrops();
	public void setRxDrops(long rxDrops);

	public byte getRxRetryRate();
	public void setRxRetryRate(byte rxRetryRate);

	public byte getRxUtilization();
	public void setRxUtilization(byte rxUtilization);

	public byte getTotalChannelUtilization();
	public void setTotalChannelUtilization(byte totalChannelUtilization);

	public byte getTxAirTime();
	public void setTxAirTime(byte txAirTime);

	public long getTxDrops();
	public void setTxDrops(long txDrops);

	public byte getTxRetryRate();
	public void setTxRetryRate(byte txRetryRate);

	public byte getTxUtilization();
	public void setTxUtilization(byte txUtilization);

	public String getApMac();
	public void setApMac(String apMac);

	public String getApName();
	public void setApName(String apName);

	public TimeZone getTz();
	public void setTz(TimeZone tz);

	public long getTxByteCount();
	public void setTxByteCount(long txByteCount);

	public long getRxByteCount();
	public void setRxByteCount(long rxByteCount);
	
	public String getIfName();

	public void setIfName(String ifName);
	
	public int getRadioType();

	public void setRadioType(int radioType);
	
	public long getUniTxFrameCount();

	public void setUniTxFrameCount(long uniTxFrameCount);

	public long getUniRxFrameCount();

	public void setUniRxFrameCount(long uniRxFrameCount);
}
