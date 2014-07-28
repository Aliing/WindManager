package com.ah.be.ls.data;

import java.util.ArrayList;
import java.util.List;

public class PacketVersion2ResponseData {

	private byte m_data_type;

	private int m_count;

	private List<VersionInfoData> m_version_list = new ArrayList<VersionInfoData>();

	public void setDataType(byte dataType) {
		m_data_type = dataType;
	}

	public byte getDataType() {
		return m_data_type;
	}

	public void setCount(int iCount) {
		m_count = iCount;
	}

	public int getCount() {
		return m_count;
	}

	public void setVersionList(List<VersionInfoData> oVersionList) {
		m_version_list = oVersionList;
	}

	public List<VersionInfoData> getVersionList() {
		return m_version_list;
	}

}