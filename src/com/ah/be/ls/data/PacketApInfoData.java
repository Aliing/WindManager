package com.ah.be.ls.data;

import java.util.List;

import com.ah.be.hiveap.HiveApVersionInfo;

public class PacketApInfoData {

	private byte m_data_type;

	private boolean m_need_Act_key_Flag;

	private String m_strActKey;

	private String m_strHMIP;

	private String m_strSystemId;

	private String m_strViewVersion;

	private byte m_bProType;

	private String m_strNetIp;

	private int m_mesh_ap_count;

	private int m_port_ap_count;

	private int m_Ap_Version_count;

	private List<HiveApVersionInfo> m_ApVersionList;

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

	public void setHMIP(String strHMIP) {
		m_strHMIP = strHMIP;
	}

	public String getHMIP() {
		return m_strHMIP;
	}

	public void setSystemId(String strSystemId) {
		m_strSystemId = strSystemId;
	}

	public String getSystemId() {
		return m_strSystemId;
	}

	public void setViewVersion(String strInnerVersion) {
		m_strViewVersion = strInnerVersion;
	}

	public String getViewVersion() {
		return m_strViewVersion;
	}

	public void setProType(byte bProtype) {
		m_bProType = bProtype;
	}

	public byte getProType() {
		return m_bProType;
	}

	public void setNetIp(String strNetIp) {
		m_strNetIp = strNetIp;
	}

	public String getNetIp() {
		return m_strNetIp;
	}

	public void setMeshApcount(int icount) {
		m_mesh_ap_count = icount;
	}

	public int getMeshApcount() {
		return m_mesh_ap_count;
	}

	public void setPortApcount(int icount) {
		m_port_ap_count = icount;
	}

	public int getPortApcount() {
		return m_port_ap_count;
	}

	public void setApVersionCount(int icount) {
		m_Ap_Version_count = icount;
	}

	public int getApVersionCount() {
		return m_Ap_Version_count;
	}

	public void setApVersionList(List<HiveApVersionInfo> apVersionList) {
		m_ApVersionList = apVersionList;
	}

	public List<HiveApVersionInfo> getApVersionList() {
		return m_ApVersionList;
	}

}