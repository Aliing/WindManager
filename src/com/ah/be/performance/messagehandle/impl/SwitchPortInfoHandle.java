package com.ah.be.performance.messagehandle.impl;

import java.util.List;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeSwitchPortInfoResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhSwitchPortInfo;

public class SwitchPortInfoHandle implements MessageHandleInterface {
	public void handleMessage(BeBaseEvent event) {
		if(event.getClass() != BeSwitchPortInfoResultEvent.class) {
			return;
		}
		
		BeSwitchPortInfoResultEvent resultEvent = (BeSwitchPortInfoResultEvent)event;
		
		//omit if Device is not exist or is not managed
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
		
		for(AhSwitchPortInfo portInfo:resultEvent.getPortInfoList()) {
			portInfo.setOwner(owner);
		}
		
		if(resultEvent.getSequenceNum() != 0) {
			//refresh all port info
			//delete by mac address
			StringBuffer sql = new StringBuffer();
			sql.append("delete from hm_switch_port_info where mac = '").append(resultEvent.getApMac()).append("'");
			try {
				QueryUtil.executeNativeUpdate(sql.toString());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortInfoHandle:Exception when delete sql:"+sql.toString(), e);
			}
			
			//insert port info
			try {
				QueryUtil.bulkCreateBos(resultEvent.getPortInfoList());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortInfoHandle,Exception when bulk create port info", e);
			}
		} else {
			//update or create port info
			for (AhSwitchPortInfo portInfo : resultEvent.getPortInfoList()) {
				List<AhSwitchPortInfo> updateBos = QueryUtil.executeQuery(AhSwitchPortInfo.class, null,
						new FilterParams("mac=:s1 and portname=:s2",
								new Object[] {portInfo.getMac(),
								portInfo.getPortName()}));
				
				try {
					if (updateBos.isEmpty()) {
						QueryUtil.createBo(portInfo);
					} else {
						AhSwitchPortInfo portInfo_temp = updateBos.get(0);
						portInfo.setId(portInfo_temp.getId());
						portInfo.setVersion(portInfo_temp.getVersion());
						QueryUtil.updateBo(portInfo);
					}
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "SwitchPortInfoHandle,Exception when create or update port info", e);
				}
			}
		}
	}
}