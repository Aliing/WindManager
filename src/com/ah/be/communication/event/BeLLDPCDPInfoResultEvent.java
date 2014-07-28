package com.ah.be.communication.event;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.performance.AhLLDPInformation;
import com.ah.util.Tracer;
import com.ah.util.coder.AhDecoder;

/**
 *
 *@filename		BeLLDPCDPInfoResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2009-8-28 03:44:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 *
 */
@SuppressWarnings("serial")
public class BeLLDPCDPInfoResultEvent extends BeCapwapClientResultEvent {

	private static final Tracer log = new Tracer(BeLLDPCDPInfoResultEvent.class.getSimpleName());

	private Collection<AhLLDPInformation> lldbCdpInfoList;

	// in current version, only existed in eth0 and eth1 interface
	private String						eth0DeviceId;

	private String						eth0PortId;

	private String						eth1DeviceId;

	private String						eth1PortId;

	private String						eth0SystemName;

	private String						eth1SystemName;

	private int							eth0PoePower;

	private int							eth1PoePower;

	public BeLLDPCDPInfoResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_LLDPCDPINFO;
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

			SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}
			HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());

			int itemCount = buf.getShort();
			log.info("reported LLDP information count: " + itemCount);
			
			lldbCdpInfoList = new ArrayList<AhLLDPInformation>(itemCount);
			
			StringBuffer logs = new StringBuffer();
			for (int i = 0; i < itemCount; i++) {
				int itemLength = buf.getShort();
				int start = buf.position();
				int ifIndex = buf.getInt();
				byte ifNameLen = buf.get();
				String ifName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(ifNameLen));
				short shortLen = buf.getShort();
				String deviceID = AhDecoder.bytes2String(buf, AhDecoder.short2int(shortLen));
				shortLen = buf.getShort();
				String portID = AhDecoder.bytes2String(buf, AhDecoder.short2int(shortLen));

				String systemName = null;
				if ((buf.position() - start) < itemLength) {
					shortLen = buf.getShort();
					systemName = AhDecoder.bytes2String(buf, AhDecoder.short2int(shortLen));
				}
				int poePower = 0;
				if ((buf.position() - start) < itemLength) {
					poePower = AhDecoder.short2int(buf.getShort());
				}
				
				short protocolFlag = 0;
				if ((buf.position() - start) < itemLength) {
					protocolFlag = buf.get();
				}

				buf.position(start+itemLength);

				AhLLDPInformation info = new AhLLDPInformation();
				info.setIfIndex(ifIndex);
				info.setIfName(ifName);
				info.setDeviceID(deviceID);
				info.setPortID(portID);
				info.setSystemName(systemName);
				info.setPoePower(poePower);
				info.setProtocol(protocolFlag);
				info.setReporter(this.apMac);
				info.setOwner(owner);

				log.debug("LLDP information: " + info.toString());
				logs.append(info.toString()).append("\n\r");
				lldbCdpInfoList.add(info);

				if (ifName.equalsIgnoreCase("eth0")) {
					eth0DeviceId = deviceID;
					eth0PortId = portID;
					eth0SystemName = systemName;
					eth0PoePower = poePower;
				} else if (ifName.equalsIgnoreCase("eth1")) {
					eth1DeviceId = deviceID;
					eth1PortId = portID;
					eth1SystemName = systemName;
					eth1PoePower = poePower;
				} else {
//					DebugUtil
//							.commonDebugError("BeLLDPCDPInfoResultEvent.parsePacket() interface is not eth0/eth1, name is "
//									+ ifName);
				}
			}
			if (log.getLogger().isDebugEnabled()) {
				String shortName = super.getApMac()
						+ "#"
						+ itemCount
						+ "#"
						+ DateFormatUtils.format(Calendar.getInstance(),
								"yyyy-MM-dd HH-mm-ss") + ".txt";
				String fullName = FileUtils.getTempDirectoryPath()
						+ File.separatorChar + "poe" + File.separatorChar
						+ shortName;
				FileUtils.writeStringToFile(new File(fullName),
						logs.toString(), "UTF-8");
				log.info("parsePacket", "lldp info to file: " + fullName);
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeLLDPCDPInfoResultEvent.parsePacket() catch exception", e);
		}
	}

	public Collection<AhLLDPInformation> getLldbCdpInfoList() {
		return lldbCdpInfoList;
	}

	public String getEth0DeviceId() {
		return eth0DeviceId;
	}

	public String getEth0PortId() {
		return eth0PortId;
	}

	public String getEth1DeviceId() {
		return eth1DeviceId;
	}

	public String getEth1PortId() {
		return eth1PortId;
	}

	public String getEth0SystemName() {
		return eth0SystemName;
	}

	public String getEth1SystemName() {
		return eth1SystemName;
	}

	public int getEth0PoePower() {
		return eth0PoePower;
	}

	public int getEth1PoePower() {
		return eth1PoePower;
	}

}
