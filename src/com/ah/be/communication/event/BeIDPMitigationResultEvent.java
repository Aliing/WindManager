package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.hiveap.IdpMitigation;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeIDPMitigationResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2008-12-5 04:23:07
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeIDPMitigationResultEvent extends BeCapwapClientResultEvent {

	private List<IdpMitigation>	idpMitigationList;

	public BeIDPMitigationResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_IDPMITIGATION;
	}

	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data)
			throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);

			ByteBuffer buf = ByteBuffer.wrap(resultData);
			idpMitigationList = new ArrayList<IdpMitigation>();
			//long current = System.currentTimeMillis();

			while (buf.hasRemaining()) {
				short length = buf.getShort();
				int beginPos = buf.position();
				byte flag = buf.get();
				String bssid = AhDecoder.bytes2hex(buf, 6);
				byte ifindex = buf.get();
				short channel = AhDecoder.byte2short(buf.get());
				byte inNetworkFlag = buf.get();
				short stationData = buf.getShort();
				short compliance = buf.getShort();
				byte ssidLen = buf.get();
				String ssid = AhDecoder.bytes2String(buf, AhDecoder.byte2int(ssidLen));
				short clientNum = buf.getShort();

				IdpMitigation idpMitigation = new IdpMitigation();
				idpMitigation.setFlag(flag);
				idpMitigation.setBssid(bssid);
				
				if (clientNum > 0) {
					List<Idp> idpList = new ArrayList<Idp>(clientNum);

					while (clientNum-- > 0) {
						short len = AhDecoder.byte2short(buf.get());
						int begPos = buf.position();
						String clientMac = AhDecoder.bytes2hex(buf, 6);
						byte removeFlag = buf.get();
						long discoveryTime = AhDecoder.int2long(buf.getInt());
						AhDecoder.int2long(buf.getInt()); // update time
						
						Idp idp = new Idp();
						idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
						idp.setStationType(BeCommunicationConstant.IDP_STATION_TYPE_CLIENT);
						idp.setParentBssid(bssid);
						idp.setIfIndex(ifindex);
						idp.setChannel(channel);
						idp.setInNetworkFlag(inNetworkFlag);
						idp.setStationData(stationData);
						idp.setCompliance(compliance);
						idp.setSsid(ssid);
						idp.setIfMacAddress(clientMac);
						idp.setRemovedFlag(removeFlag);
						idp.setReportTime(new HmTimeStamp(getMessageTimeStamp() - discoveryTime * 1000, getMessageTimeZone()));
						
						if ((buf.position() - begPos) < len) {
							idp.setRssi(buf.get());
						}
						idpList.add(idp);
						buf.position(begPos+len);
					}
					
					idpMitigation.setClients(idpList);
				}

				buf.position(beginPos+length);
				idpMitigationList.add(idpMitigation);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeIDPMitigationResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<IdpMitigation> getIdpMitigationList() {
		return idpMitigationList;
	}

	public void setIdpMitigationList(List<IdpMitigation> idpMitigationList) {
		this.idpMitigationList = idpMitigationList;
	}
}
