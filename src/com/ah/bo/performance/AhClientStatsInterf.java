package com.ah.bo.performance;

import java.util.TimeZone;

import com.ah.bo.HmBo;

public interface AhClientStatsInterf extends HmBo {
	
	public String getClientMac();
	public void setClientMac(String clientMac);

	public byte getRxAirTime();
	public void setRxAirTime(byte rxAirTime);

	public int getRxFrameDropped();
	public void setRxFrameDropped(int rxFrameDropped);

	public String getSsidName();
	public void setSsidName(String ssidName);

	public long getTimeStamp();
	public void setTimeStamp(long timeStamp);

	public String getTimeStampString();

	public byte getTxAirTime();
	public void setTxAirTime(byte txAirTime);

	public int getTxFrameDropped();
	public void setTxFrameDropped(int txFrameDropped);

	public String getApMac();
	public void setApMac(String apMac);
	
	public String getApName();
	public void setApName(String apName);

	public int getBandWidthUsage();
	public void setBandWidthUsage(int bandWidthUsage);

	public TimeZone getTz();
	public void setTz(TimeZone tz);

	public long getTxFrameByteCount();
	public void setTxFrameByteCount(long txFrameByteCount);

	public long getRxFrameByteCount();
	public void setRxFrameByteCount(long rxFrameByteCount);
}
