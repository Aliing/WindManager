package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.bo.hhm.HhmUpgradeVersionInfo;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeVersionUpdateInfoEvent extends BePortalHMPayloadEvent {
	
	private List<HhmUpgradeVersionInfo> versionInfoList;
	
	@Override
	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		// total length
		buf.getInt();
		
		int size = buf.getInt();
		if (size >= 0) {
			versionInfoList = new ArrayList<HhmUpgradeVersionInfo>();
			for (int i = 0; i < size; i++) {
				HhmUpgradeVersionInfo versionInfo = new HhmUpgradeVersionInfo();
				versionInfo.setIpAddress(AhDecoder.getString(buf));
				versionInfo.setHmVersion(AhDecoder.getString(buf));
				
				versionInfo.setLeftApCount(buf.getInt());
				versionInfo.setLeftVhmCount(buf.getInt());
				versionInfoList.add(versionInfo);
			}
		}
		
		return null;
	}
	
	public BeVersionUpdateInfoEvent() {
		super();
		operationType = OPERATIONTYPE_HMOL_VERSIONUPDATEINFO;
	}

	public List<HhmUpgradeVersionInfo> getVersionInfoList() {
		return versionInfoList;
	}

	public void setVersionInfoList(List<HhmUpgradeVersionInfo> versionInfoList) {
		this.versionInfoList = versionInfoList;
	}
}
