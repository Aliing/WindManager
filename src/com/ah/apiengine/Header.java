package com.ah.apiengine;

import java.io.Serializable;

public interface Header extends Serializable {

	short getVersion();

	void setVersion(short version);

	short getSeqNum();

	void setSeqNum(short seqNum);

	String getTimezone();

	void setTimezone(String timezone);

	int getTimestamp();

	void setTimestamp(int timestamp);

}