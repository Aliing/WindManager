package com.ah.be.ts.hiveap;

import java.io.Serializable;
import java.util.TimeZone;

import com.ah.bo.hiveap.HiveAp;

public interface Debug extends Serializable {

	enum Category {
		CLIENT_MONITOR, VLAN_PROBE, CLIENT_PERFORMANCE
	}

	int getCookieId();

	long getTimstamp();

	void setTimstamp(long timstamp);

	TimeZone getTimeZone();

	void setTimeZone(TimeZone timeZone);

	String getHiveApMac();

	void setHiveApMac(String hiveApMac);

	HiveAp getHiveAp();

	void setHiveAp(HiveAp hiveAp);

	Category getCategory();

	/**
	 * @return CAPWAP type relative to the <code>DebugCategory</code> from <code>getDebugCategory()</code>.     
	 */
	short getCapwapType();

}