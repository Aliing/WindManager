package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.performance.AhRouterLTEVZInfo;
import com.ah.util.coder.AhDecoder;

@SuppressWarnings("serial")
public class BeRouterLTEVZInfoResultEvent extends BeCapwapClientResultEvent {
	
	List<AhRouterLTEVZInfo> lteInfoList=new ArrayList<AhRouterLTEVZInfo>();
	
	public BeRouterLTEVZInfoResultEvent()
	{
		super();
		resultType=BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_ROUTER_LTE_VZ_INFO;
	}
	
	/** 
	 * parse packet message to event data
	 * 
	 * @see com.ah.be.communication.event.BeCapwapClientResultEvent#parsePacket(byte[])
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			// count
			short count = buf.getShort();
			for(int i = 0; i < count; i++) {
				short length = buf.getShort();
				int beginPos = buf.position();
				int len = 0;
				AhRouterLTEVZInfo lteInfo = new AhRouterLTEVZInfo();
				lteInfo.setConnectStatus(buf.get());
				lteInfo.setNetworkMode(buf.get());
				lteInfo.setRssi(buf.getShort());
				lteInfo.setRsrp(buf.getShort());
				lteInfo.setRsrq(buf.getShort());
				lteInfo.setBars(buf.get());
				lteInfo.setModemFlag(buf.get());
				
				len = buf.get();
				lteInfo.setInterfaceName(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setFirmwareVersion(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setManufacture(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setHardwareID(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setSimIccid(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setImei(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setCarrier(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setCellID(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setSystemMode(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setSimStatus(AhDecoder.bytes2String(buf, len));
				len=buf.get();
				lteInfo.setModemMode(AhDecoder.bytes2String(buf, len));
				
				lteInfoList.add(lteInfo);
				
				buf.position(beginPos+length);
			}
		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeRouterLTEVZInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public List<AhRouterLTEVZInfo> getLteInfoList() {
		return lteInfoList;
	}
	
}
