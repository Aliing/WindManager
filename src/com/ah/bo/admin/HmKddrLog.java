package com.ah.bo.admin;

import java.sql.Timestamp;
import java.util.TimeZone;

import com.ah.bo.HmBo;
import com.ah.util.datetime.AhDateTimeUtil;

public class HmKddrLog implements HmBo {

	private static final long serialVersionUID = 1L;

	private String deviceName;

	private String fileName;

	private String parentFileName;

	private long logTimeStamp;
	private TimeZone logTimeZone = TimeZone.getDefault();

	public String getLogTime() {
		return AhDateTimeUtil.getSpecifyDateTime(logTimeStamp, logTimeZone,
				owner);
	}

	private HmDomain owner;

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLogTimeStamp() {
		return logTimeStamp;
	}

	public void setLogTimeStamp(long logTimeStamp) {
		this.logTimeStamp = logTimeStamp;
	}

	public HmDomain getOwner() {
		return owner;
	}

	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getParentFileName() {
		return parentFileName;
	}

	public void setParentFileName(String parentFileName) {
		this.parentFileName = parentFileName;
	}

	public TimeZone getLogTimeZone() {
		return logTimeZone;
	}

	public void setLogTimeZone(TimeZone logTimeZone) {
		this.logTimeZone = logTimeZone;
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setId(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public Timestamp getVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVersion(Timestamp version) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		// TODO Auto-generated method stub

	}
}