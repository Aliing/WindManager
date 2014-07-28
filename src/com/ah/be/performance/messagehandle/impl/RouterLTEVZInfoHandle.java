package com.ah.be.performance.messagehandle.impl;

import java.util.List;

import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.event.BeRouterLTEVZInfoResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhRouterLTEVZInfo;

public class RouterLTEVZInfoHandle implements MessageHandleInterface {
	public void handleMessage(BeBaseEvent event) {
		if(event.getClass() != BeRouterLTEVZInfoResultEvent.class) {
			return;
		}
		
		BeRouterLTEVZInfoResultEvent resultEvent = (BeRouterLTEVZInfoResultEvent)event;
		
		//omit if Device is not exist or is not managed
		SimpleHiveAp simpleHiveAp = CacheMgmt.getInstance().getSimpleHiveAp(resultEvent.getApMac());
		if (simpleHiveAp == null || simpleHiveAp.getManageStatus() != HiveAp.STATUS_MANAGED) {
			return;
		}
		HmDomain owner = CacheMgmt.getInstance().getCacheDomainById(simpleHiveAp.getDomainId());
		
		for(AhRouterLTEVZInfo lteInfo:resultEvent.getLteInfoList()) {
			lteInfo.setMac(resultEvent.getApMac());
			lteInfo.setOwner(owner);
		}
		
		if(resultEvent.getSequenceNum() != 0) {
			//refresh all port info
			//delete by mac address
			StringBuffer sql = new StringBuffer();
			sql.append("delete from hm_router_lte_vz_info where mac = '").append(resultEvent.getApMac()).append("'");
			try {
				QueryUtil.executeNativeUpdate(sql.toString());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "RouterLTEVZInfoHandle:Exception when delete sql:"+sql.toString(), e);
			}
			
			//insert port info
			try {
				QueryUtil.bulkCreateBos(resultEvent.getLteInfoList());
			} catch (Exception e) {
				BeLogTools.error(HmLogConst.M_PERFORMANCE, "RouterLTEVZInfoHandle,Exception when bulk create port info", e);
			}
		} else {
			//update or create LTE_VZ info
			for (AhRouterLTEVZInfo lteInfo:resultEvent.getLteInfoList()) {
				List<AhRouterLTEVZInfo> updateBos = QueryUtil.executeQuery(AhRouterLTEVZInfo.class, null,
						new FilterParams("mac",lteInfo.getMac()));
				try {
					if (updateBos.isEmpty()) {
						QueryUtil.createBo(lteInfo);
					} else {
						AhRouterLTEVZInfo lteInfo_temp = updateBos.get(0);
						lteInfo.setId(lteInfo_temp.getId());
						lteInfo.setVersion(lteInfo_temp.getVersion());
						QueryUtil.updateBo(lteInfo);
					}
				} catch (Exception e) {
					BeLogTools.error(HmLogConst.M_PERFORMANCE, "RouterLTEVZInfoHandle,Exception when create or update LTE_VZ info", e);
				}
			}
		}
	}
}
