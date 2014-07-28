package com.ah.be.rest.ahmdm.server.service;

import java.util.List;

import sun.misc.BASE64Decoder;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.event.BeCliEvent;
import com.ah.be.rest.ahmdm.server.models.MdmStatusUpdateRequest;
import com.ah.be.rest.ahmdm.server.models.MdmStatusUpdateResponse;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class MdmStatusUpdateServiceImpl implements MdmStatusUpdateService {
	private static final int TIMEOUT_CLI = 35; // second
	private static final String STATUS_UPDATE_CLI = "exec mobile-device-manager aerohive status-change ";
	private static final String CLI_SUFFIX = "\n";

	public MdmStatusUpdateResponse updateStatus(MdmStatusUpdateRequest req) {
		MdmStatusUpdateResponse response = new MdmStatusUpdateResponse();
		response.setApMacAddress(req.getApMacAddress());
		
		SimpleHiveAp ap = getSimpleHiveAp(req.getCustomerId(), req.getApMacAddress());
		if (ap == null) {
			response.setResultCode(MdmStatusUpdateResponse.RESULT_AP_NOT_FOUND);
			return response;
		}
			
		try {
			byte[] data = new BASE64Decoder().decodeBuffer(req.getData());
			StringBuilder sb = new StringBuilder(STATUS_UPDATE_CLI).append(new String(data)).append(CLI_SUFFIX);
			String[] clis = new String[1];
			clis[0] = sb.toString();
		
			BeCliEvent updateEvent = new BeCliEvent();
			updateEvent.setSimpleHiveAp(ap);
			updateEvent.setClis(clis);
			updateEvent.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
			updateEvent.buildPacket();
			int serialNum = HmBeCommunicationUtil.sendRequest(updateEvent,
					TIMEOUT_CLI);
			response.setResultCode(serialNum == BeCommunicationConstant.SERIALNUM_SENDREQUESTFAILED ?
					MdmStatusUpdateResponse.RESULT_AP_DISCONNECTED : MdmStatusUpdateResponse.RESULT_SUCCESS);
		} catch (Exception e) {
			new Tracer(MdmStatusUpdateService.class.getSimpleName()).error("MdmStatusUpdateService.updateStatus fail", e);
			response.setResultCode(MdmStatusUpdateResponse.RESULT_OTHER_FAILURE);
		}

		return response;
	}
	
	private SimpleHiveAp getSimpleHiveAp(String domain, String macAddress) {
		if (domain == null || macAddress == null)
			return null;
		
		macAddress = macAddress.toUpperCase();
		
		SimpleHiveAp simpleAp = CacheMgmt.getInstance().getSimpleHiveAp(macAddress);
		if (simpleAp != null)
			return simpleAp;
		
		String sql = "select id, softver from hive_ap where macaddress = '" + macAddress + "'";
		List list = QueryUtil.executeNativeQuery(sql);

		if (list == null || list.size() == 0)
			return null;
		
        Object[] values = (Object[]) list.get(0);
		simpleAp = new SimpleHiveAp();
		
		simpleAp.setMacAddress(macAddress);
		simpleAp.setId(Long.valueOf(values[0].toString()));
		simpleAp.setSoftVer(values[1].toString());
		
		return simpleAp;
	}

}
