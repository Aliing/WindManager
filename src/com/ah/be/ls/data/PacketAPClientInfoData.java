package com.ah.be.ls.data;

import java.util.List;

import com.ah.be.hiveap.ClientMacInfo;

public class PacketAPClientInfoData {

	private byte m_data_type;

	private boolean m_need_Act_key_Flag;

	private String m_strActKey;

	private String m_strSystemId;

	private int m_client_type_count;

	private List<ClientMacInfo> m_ApclientList;

	public void setDataType(byte dataType) {
		m_data_type = dataType;
	}

	public byte getDataType() {
		return m_data_type;
	}

	public void setNeedActKeyFlag(boolean bFlag) {
		m_need_Act_key_Flag = bFlag;
	}

	public boolean getNeedActKeyFlag() {
		return m_need_Act_key_Flag;
	}

	public void setActKey(String strActKey) {
		m_strActKey = strActKey;
	}

	public String getActKey() {
		return m_strActKey;
	}

	public void setSystemId(String strSystemId) {
		m_strSystemId = strSystemId;
	}

	public String getSystemId() {
		return m_strSystemId;
	}

	public void setClientTypecount(int icount) {
		m_client_type_count = icount;
	}

	public int getClientTypecount() {
		return m_client_type_count;
	}

	public void setApClientList(List<ClientMacInfo> apClientList) {
		m_ApclientList = apClientList;
	}

	public List<ClientMacInfo> getApClientList() {

		return m_ApclientList;
	}

}