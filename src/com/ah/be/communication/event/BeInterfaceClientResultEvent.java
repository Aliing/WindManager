package com.ah.be.communication.event;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationDecodeException;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientStats;
import com.ah.bo.performance.AhDeviceStats;
import com.ah.bo.performance.AhInterfaceStats;
import com.ah.util.coder.AhDecoder;

/**
 * 
 *@filename		BeInterfaceClientResultEvent.java
 *@version		V1.0.0.0
 *@author		juyizhou
 *@createtime	2010-3-17 11:20:17
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modfy history*
 * 
 */
@SuppressWarnings("serial")
public class BeInterfaceClientResultEvent extends BeCapwapClientResultEvent {

	private List<AhInterfaceStats>	interfaceStatsList;

	private List<AhClientStats>		clientStatsList;
	
	private List<AhDeviceStats>    deviceStatsList;
	
	// for parse, transient
	private HmDomain owner;

	public BeInterfaceClientResultEvent() {
		super();
		resultType = BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_INTERFACECLIENT;
	}
	
	/**
	 * parse packet message to event data
	 * 
	 * @param data -
	 */
	@Override
	protected void parsePacket(byte[] data) throws BeCommunicationDecodeException {
		try {
			super.parsePacket(data);
			ByteBuffer buf = ByteBuffer.wrap(resultData);
			
			simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(apMac);
			if (simpleHiveAp == null) {
				throw new BeCommunicationDecodeException("Invalid apMac: (" + apMac
						+ "), Can't find corresponding data in cache.");
			}
			owner = CacheMgmt.getInstance().getCacheDomainById(
					simpleHiveAp.getDomainId());

			interfaceStatsList = new ArrayList<AhInterfaceStats>();
			clientStatsList = new ArrayList<AhClientStats>();
			deviceStatsList = new ArrayList<AhDeviceStats>();
			
			while (buf.hasRemaining()) {
				byte tableType = buf.get();
				short itemsCount = buf.getShort();
				
				if (tableType == BeInterfaceClientEvent.TABLETYPE_INTERFACE) {
					for (short i = 0; i < itemsCount; i++) {
						interfaceStatsList.add(parseInterfaceStats(buf));
					}
				} else if (tableType == BeInterfaceClientEvent.TABLETYPE_CLIENT) {
					for (short i = 0; i < itemsCount; i++) {
						clientStatsList.add(parseClientStats(buf));
					}
				} else if (tableType == BeInterfaceClientEvent.TABLETYPE_DEVICE) {
					for (short i = 0; i < itemsCount; i++) {
						deviceStatsList.add(parseDeviceStats(buf));
					}
				}
			}

		} catch (Exception e) {
			throw new BeCommunicationDecodeException(
					"BeInterfaceClientResultEvent.parsePacket() catch exception", e);
		}
	}

	private AhInterfaceStats parseInterfaceStats(ByteBuffer buf) {
		AhInterfaceStats bo = new AhInterfaceStats();
		bo.setOwner(owner);
		bo.setApMac(apMac);
		bo.setApName(simpleHiveAp.getHostname());
		
		short itemLength = buf.getShort();
		int beginPos = buf.position();
		
		bo.setTimeStamp((AhDecoder.int2long(buf.getInt())/60) * 60 * 1000);
		bo.setCollectPeriod(buf.getShort());
		bo.setIfIndex(buf.getInt());

		byte len = buf.get();
		bo.setIfName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)));

		if(simpleHiveAp.getHiveApModel()==HiveAp.HIVEAP_MODEL_110 || 
				simpleHiveAp.getHiveApModel()==HiveAp.HIVEAP_MODEL_BR200_WP ||
				simpleHiveAp.getHiveApModel()==HiveAp.HIVEAP_MODEL_BR200_LTE_VZ) {
			if(bo.getIfName().equalsIgnoreCase("wifi0")) {
				bo.setRadioType(simpleHiveAp.getWifi0RadioType());
			}
		} else {
			if(bo.getIfName().equalsIgnoreCase("wifi0")) {
				bo.setRadioType(AhInterfaceStats.RADIOTYPE_24G);
			} else if(bo.getIfName().equalsIgnoreCase("wifi1")) {
				bo.setRadioType(AhInterfaceStats.RADIOTYPE_5G);
			} else {
				bo.setRadioType(AhInterfaceStats.RADIOTYPE_OTHER);
			}
		}
		bo.setTxDrops(buf.getLong());
		bo.setRxDrops(buf.getLong());
		bo.setCrcErrorRate(buf.get());
		bo.setTxRetryRate(buf.get());
		bo.setRxRetryRate(buf.get());
		bo.setUniTxFrameCount(buf.getLong());
		bo.setUniRxFrameCount(buf.getLong());
		bo.setBcastTxFrameCount(buf.getLong());
		bo.setBcastRxFrameCount(buf.getLong());
		bo.setTotalChannelUtilization(buf.get());
		bo.setInterferenceUtilization(buf.get());
		bo.setTxUtilization(buf.get());
		bo.setRxUtilization(buf.get());
		bo.setNoiseFloor(buf.getShort());
		bo.setTxAirTime(buf.get());
		bo.setRxAirTime(buf.get());

		byte txCount = buf.get();
		StringBuffer strBuf = new StringBuffer();
		for (byte i = 0; i < txCount; i++) {
			strBuf.append(";");
			strBuf.append(buf.getInt());
			strBuf.append(",");
			strBuf.append(buf.get());
			strBuf.append(",");
			strBuf.append(buf.get());
		}
		if (txCount > 0) {
			bo.setTxRateInfo(strBuf.substring(1));
		}

		byte rxCount = buf.get();
		strBuf = new StringBuffer();
		for (byte i = 0; i < rxCount; i++) {
			strBuf.append(";");
			strBuf.append(buf.getInt());
			strBuf.append(",");
			strBuf.append(buf.get());
			strBuf.append(",");
			strBuf.append(buf.get());
		}
		if (rxCount > 0) {
			bo.setRxRateInfo(strBuf.substring(1));
		}
		
		bo.setAlarmFlag(buf.getInt());
		bo.setBandSteerSuppressCount(buf.getInt());
		bo.setLoadBalanceSuppressCount(buf.getInt());
		bo.setWeakSnrSuppressCount(buf.getInt());
		bo.setSafetyNetAnswerCount(buf.getInt());
		bo.setProbeRequestSuppressCount(buf.getInt());
		bo.setAuthRequestSuppressCount(buf.getInt());
		
		if(itemLength > (buf.position()-beginPos)) {
			bo.setTxByteCount(buf.getLong());
			bo.setRxByteCount(buf.getLong());
			bo.setTotalTxBitSuccessRate(buf.get());
			bo.setTotalRxBitSuccessRate(buf.get());
		}
		
		if(itemLength > (buf.position()-beginPos)) {
			bo.setCrcerrorframe(buf.getLong());
			bo.setTxretryframe(buf.getLong());
			bo.setRxretryframe(buf.getLong());
			bo.setTxbcastbytecount(buf.getLong());
			bo.setRxbcastbytecount(buf.getLong());
			bo.setTxdropframebyhw(buf.getLong());
			bo.setTxdropframebysw(buf.getLong());
			bo.setRxdropframebysw(buf.getLong());
		}
		
		buf.position(beginPos+itemLength);
		return bo;
	}

	private AhClientStats parseClientStats(ByteBuffer buf) {
		AhClientStats bo = new AhClientStats();
		bo.setOwner(owner);
		bo.setApMac(apMac);
		bo.setApName(simpleHiveAp.getHostname());
		bo.setVendor(AhConstantUtil.getMacOuiComName(apMac.substring(0, 6)));

		short itemLength = buf.getShort();
		int beginPos = buf.position();
		
		bo.setTimeStamp((AhDecoder.int2long(buf.getInt())/60) * 60 * 1000);
		bo.setCollectPeriod(buf.getShort());
		bo.setIfIndex(buf.getInt());
		bo.setClientMac(AhDecoder.bytes2hex(buf, 6).toUpperCase());

		byte len = buf.get();
		bo.setSsidName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(len)));

		byte clientScore = buf.get();
		clientScore = (clientScore >= 0 && clientScore <= 100) ? clientScore : 100;
		bo.setSlaConnectScore(clientScore);
		bo.setOverallClientHealthScore(clientScore);
		bo.setBandWidthUsage(buf.getInt());
		bo.setSlaViolationTraps(buf.getInt());
		bo.setTxFrameDropped(buf.getInt());
		bo.setRxFrameDropped(buf.getInt());
		bo.setTxFrameCount(buf.getInt());
		if (NmsUtil.compareSoftwareVersion("3.5.1.0", simpleHiveAp.getSoftVer()) >= 0)
			bo.setTxFrameByteCount(buf.getInt());
		else
			bo.setTxFrameByteCount(buf.getLong());
		bo.setRxFrameCount(buf.getInt());
		if (NmsUtil.compareSoftwareVersion("3.5.1.0", simpleHiveAp.getSoftVer()) >= 0)
			bo.setRxFrameByteCount(buf.getInt());
		else
			bo.setRxFrameByteCount(buf.getLong());
		bo.setAverageSNR(buf.getInt());
		bo.setPowerSaveModeTimes(buf.getInt());
		bo.setTxAirTime(buf.get());
		bo.setRxAirTime(buf.get());

		byte txCount = buf.get();
		StringBuffer strBuf = new StringBuffer();
		for (byte i = 0; i < txCount; i++) {
			strBuf.append(";");
			strBuf.append(buf.getInt());
			strBuf.append(",");
			strBuf.append(buf.get());
			strBuf.append(",");
			strBuf.append(buf.get());
		}
		if (txCount > 0) {
			bo.setTxRateInfo(strBuf.substring(1));
		}

		byte rxCount = buf.get();
		strBuf = new StringBuffer();
		for (byte i = 0; i < rxCount; i++) {
			strBuf.append(";");
			strBuf.append(buf.getInt());
			strBuf.append(",");
			strBuf.append(buf.get());
			strBuf.append(",");
			strBuf.append(buf.get());
		}
		if (rxCount > 0 ) {
			bo.setRxRateInfo(strBuf.substring(1));
		}
		
		bo.setAlarmFlag(buf.getInt());
		
		if(itemLength > (buf.position()-beginPos)) {
			clientScore = buf.get();
			clientScore = (clientScore >= 0 && clientScore <= 100) ? clientScore : 100;
			bo.setIpNetworkConnectivityScore(clientScore);
			clientScore = buf.get();
			clientScore = (clientScore >= 0 && clientScore <= 100) ? clientScore : 100;
			bo.setApplicationHealthScore(clientScore);
			clientScore = buf.get();
			clientScore = (clientScore >= 0 && clientScore <= 100) ? clientScore : 100;
			bo.setOverallClientHealthScore(clientScore);
		}
		
		if(itemLength > (buf.position()-beginPos)) {
			bo.setTotalTxBitSuccessRate(buf.get());
			bo.setTotalRxBitSuccessRate(buf.get());
		}
		
		//Get osname from cache when AP didn't send osinfo
		bo.setOsname("unknown");
		ClientInfoBean myClientInfoBean = ReportCacheMgmt.getInstance().getClientInfoBean(bo.getClientMac(), owner.getId());
		if(null != myClientInfoBean){		
			String osName = BeOsInfoProcessor.getInstance().getOsName(owner.getId(), myClientInfoBean.getOsInfo());
			bo.setOsname(osName);
		}
		bo.setUserName("Unknown");
		if(null != myClientInfoBean){		
			int radioType = myClientInfoBean.getRadioType();
			bo.setRadioType(radioType);
			
			String bUserName = myClientInfoBean.getUserName();
			String bHostName = myClientInfoBean.getHostName();
			if (bUserName!=null && !bUserName.isEmpty()) {
				bo.setUserName(bUserName);
			}
			if (bHostName!=null && !bHostName.isEmpty()) {
				bo.setHostName(bHostName);
			}
		}
		
		if(itemLength > (buf.position()-beginPos)) {
			byte osLen = buf.get();
			bo.setClientosinfo(AhDecoder.bytes2String(buf, AhDecoder.byte2int(osLen)));
			switch(buf.get())
			{
			case AhAssociation.CLIENTMACPROTOCOL_BMODE:
			case AhAssociation.CLIENTMACPROTOCOL_GMODE:
			case AhAssociation.CLIENTMACPROTOCOL_NGMODE:
					bo.setRadioType(AhInterfaceStats.RADIOTYPE_24G);
					break;
			case AhAssociation.CLIENTMACPROTOCOL_AMODE:
			case AhAssociation.CLIENTMACPROTOCOL_NAMODE:
			case AhAssociation.CLIENTMACPROTOCOL_ACMODE:
					bo.setRadioType(AhInterfaceStats.RADIOTYPE_5G);
					break;
			default:
					break;			
			}
			//bo.setRadioType(buf.get());
			bo.setRssi(buf.getInt());
			byte userNameLen = buf.get();
			String apUserName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(userNameLen));
			if (apUserName!=null && !apUserName.isEmpty()) {
				bo.setUserName(apUserName);
			}
			
			byte hostNameLen = buf.get();
			String apHostName = AhDecoder.bytes2String(buf, AhDecoder.byte2int(hostNameLen));
			if (apHostName!=null && !apHostName.isEmpty()) {
				bo.setHostName(apHostName);
			}
			
			byte userProfileName = buf.get();
			bo.setUserProfileName(AhDecoder.bytes2String(buf, AhDecoder.byte2int(userProfileName)));
			if (bo.getClientosinfo()!=null && !bo.getClientosinfo().isEmpty()) {
				bo.setOsname(BeOsInfoProcessor.getInstance().getOsName(owner.getId(), bo.getClientosinfo()));
			}
		}
		
		buf.position(beginPos+itemLength);
		return bo;
	}

	private AhDeviceStats parseDeviceStats(ByteBuffer buf) {
		AhDeviceStats bo = new AhDeviceStats();
		bo.setOwner(owner);
		bo.setApMac(apMac);
		bo.setApName(simpleHiveAp.getHostname());
		
		short itemLength = buf.getShort();
		int beginPos = buf.position();		
		
		bo.setTimeStamp((AhDecoder.int2long(buf.getInt())/60) * 60 * 1000);
		bo.setCollectPeriod(buf.getShort());
		bo.setMaxCpu(buf.get());
		bo.setAverageCpu(buf.get());
		bo.setMaxMem(buf.get());
		bo.setAverageMem(buf.get());
		
		buf.position(beginPos+itemLength);		
		return bo;
	}
	
	public List<AhClientStats> getClientStatsList() {
		return clientStatsList;
	}

	public void setClientStatsList(List<AhClientStats> clientStatsList) {
		this.clientStatsList = clientStatsList;
	}

	public List<AhInterfaceStats> getInterfaceStatsList() {
		return interfaceStatsList;
	}

	public void setInterfaceStatsList(List<AhInterfaceStats> interfaceStatsList) {
		this.interfaceStatsList = interfaceStatsList;
	}
	
	public List<AhDeviceStats> getDeviceStatsList() {
		return deviceStatsList;
	}
	
	public void setDeviceStatsList(List<AhDeviceStats> deviceStatsList) {
		this.deviceStatsList = deviceStatsList;
	}
}