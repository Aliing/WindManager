package com.ah.be.performance.messagehandle.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ah.be.db.discovery.event.AhDiscoveryEvent;
import com.ah.be.db.discovery.event.AhDiscoveryEvent.HiveApType;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.performance.dataretention.NetworkDeviceConfigTracking;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.mgmt.QueryUtil;

public class NewDeviceTopologyHandle implements MessageHandleInterface {
	public void handleMessage(BeBaseEvent event) {
		if(event.getClass() != AhDiscoveryEvent.class) {
			return;
		}
		
		AhDiscoveryEvent resultEvent = (AhDiscoveryEvent)event;
		if(resultEvent.getType() == HiveApType.CREATED && resultEvent.getHiveAp() != null){
			if(resultEvent.getHiveAp().getOwner() != null){
				Long vHMdomain = resultEvent.getHiveAp().getOwner().getId();
				String sql = "select id from map_node where parent_map_id = " +
						"(select id from map_node where parent_map_id is null) and owner="+vHMdomain;
				try{
					String[] tags = null;
					List<String> tagsStr = new ArrayList<>();
					
					if(null != resultEvent.getHiveAp().getClassificationTag1() && !"".equals(resultEvent.getHiveAp().getClassificationTag1())){
						tagsStr.add(resultEvent.getHiveAp().getClassificationTag1());
					}
					if(null != resultEvent.getHiveAp().getClassificationTag2() && !"".equals(resultEvent.getHiveAp().getClassificationTag2())){
						tagsStr.add(resultEvent.getHiveAp().getClassificationTag2());
					}
					if(null != resultEvent.getHiveAp().getClassificationTag3() && !"".equals(resultEvent.getHiveAp().getClassificationTag3())){
						tagsStr.add(resultEvent.getHiveAp().getClassificationTag3());
					}
					if(null != tagsStr && tagsStr.size() > 0){
						tags = new String[tagsStr.size()];
						tagsStr.toArray(tags);
					}
					List<?> list = QueryUtil.executeNativeQuery(sql, 1);
					if(!list.isEmpty()){
							NetworkDeviceConfigTracking.topologyChanged(Calendar.getInstance(), 
									vHMdomain, resultEvent.getHiveAp().getMacAddress(), resultEvent.getHiveAp().getTimeZoneOffset(), new long[]{Long.parseLong(list.get(0).toString())}, tags);
					}
				}catch(Exception e){
					BeLogTools.error(HmLogConst.M_PERFORMANCE,"New Device topology query failure: "+e.getMessage());
				}
			}		
		}
	}
}