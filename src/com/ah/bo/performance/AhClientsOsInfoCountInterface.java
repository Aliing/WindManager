package com.ah.bo.performance;

import java.util.TimeZone;

import com.ah.bo.HmBo;

public interface AhClientsOsInfoCountInterface extends HmBo {

	public long getTimeStamp();

	public void setTimeStamp(long timeStamp);

	public String getApName();

	public void setApName(String apName);

	public String getSsid();

	public void setSsid(String ssid);

	public int getClientCount() ;

	public void setClientCount(int clientCount) ;

	public TimeZone getTz();
	public void setTz(TimeZone tz);
	
	public String getTimeStampString();
	
	public String getApMac();

	public void setApMac(String apMac);
	
	public String getOsInfo();

	public void setOsInfo(String osInfo);

}