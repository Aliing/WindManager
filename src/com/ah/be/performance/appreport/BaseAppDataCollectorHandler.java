package com.ah.be.performance.appreport;

import java.nio.ByteBuffer;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.cache.ClientInfoBean;
import com.ah.be.common.cache.ReportCacheMgmt;
import com.ah.be.performance.BeOsInfoProcessor;
import com.ah.bo.ApReportData;
import com.ah.bo.performance.AhAppDataHour;
import com.ah.bo.performance.AhAppDataSeconds;
import com.ah.util.coder.AhDecoder;

public class BaseAppDataCollectorHandler implements AppDataCollectorHandler, AppReportConstants {

	public ApReportData getSingleReportData(String apMac, byte[] data) {
		return getSingleReportData(apMac, FILE_TYPE_HOUR, data);
	}
	
	public boolean handToDataCollector(List<ApReportData> list)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected ApReportData getSingleReportData(String apMac, short fileType, byte[] data) {
		ByteBuffer buffer = ByteBuffer.wrap(data);
		ApReportData entity = (fileType == FILE_TYPE_HOUR) ? new AhAppDataHour() : new AhAppDataSeconds();
		entity.setApMac(apMac);
	
		buffer.get();
		buffer.get();
		String clientMac = AhDecoder.bytes2hex(buffer, 6);
		//String clientMac = Long.toHexString((buffer.getLong())).toUpperCase();
		entity.setClientMac(clientMac);
		entity.setApplication(buffer.getShort());
		
		entity.setInterface4Client((short)buffer.get());
		entity.setPeerInterface((short)buffer.get());
		short s = (short)buffer.get();
		entity.setPassThrough((s == 1) ? true : false);
		entity.setRadioType((short)buffer.get());
		
		long timestamp = AhDecoder.int2long(buffer.getInt()) * 1000;
		int reportMinute = ReportHelper.getReportMinute(timestamp);
		if (fileType == FILE_TYPE_HOUR && reportMinute == 59) {
			timestamp = timestamp + 60 * 1000;  //timestamp delay 1 minute.
		}
		entity.setTimeStamp(timestamp);
		//entity.setTimeStamp(AhDecoder.int2long(buffer.getInt()) * 1000 + 60 * 1000); //timestamp delay 60 second.

		entity.setSeconds(buffer.getShort());
		entity.setAppSeconds((short) (buffer.getShort() * 60));
		
		entity.setPacketsDownLoad(buffer.getInt());
		entity.setBytesDownLoad(buffer.getLong());
		entity.setPacketsUpLoad(buffer.getInt());
		entity.setBytesUpLoad(buffer.getLong());

		long ownerId = ReportHelper.getOwnerIdByApMac(apMac);
		entity.setOwnerId(ownerId);

		ClientInfoBean client = ReportCacheMgmt.getInstance().getClientInfoBean(clientMac, ownerId);
		if (client != null) {
			entity.setUserName(StringUtils.isNotBlank(client.getUserName()) ? client.getUserName() : clientMac);
			//entity.setUserName(ReportHelper.asString(client.getUserName()));
			entity.setUserProfileName(ReportHelper.asString(client.getProfileName()));
			entity.setVLan(client.getVlan());
			entity.setSsid(ReportHelper.asString(client.getSsid()));		
			entity.setHostName(ReportHelper.asString(client.getHostName()));
			String osType = client.getOsInfo();
			entity.setClientOsType(ReportHelper.asString(osType));
			if (StringUtils.isNotBlank(osType)) {
				entity.setOsName(BeOsInfoProcessor.getInstance().getOsName(ownerId, osType));
			} else {
				entity.setOsName(UNKNOWN);
			}
		} else {
//			AhClientSession memdbClient = ReportHelper.queryClientSession(clientMac);
//			if (memdbClient != null) {
//				BeLogTools.info(HmLogConst.M_PERFORMANCE, String.format("[getClientSession] client not in cache, but mem db client exists, clientMac = %s, conn state = %d", clientMac, memdbClient.getConnectstate()));
//			}
			//entity.setUserName(UNKNOWN);
			entity.setUserName(clientMac);
			entity.setUserProfileName(UNKNOWN);
			entity.setSsid(UNKNOWN);		
			entity.setHostName(UNKNOWN);
			entity.setClientOsType(UNKNOWN);
			entity.setOsName(UNKNOWN);
		}
		return entity;
	}

	

}
