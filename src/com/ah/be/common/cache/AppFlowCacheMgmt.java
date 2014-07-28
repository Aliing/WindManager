package com.ah.be.common.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.performance.AhAppFlowLog;

public class AppFlowCacheMgmt {
	
	private static AppFlowCacheMgmt	cacheMgmt;

	//key1:ownerId, key2: appCode
	private final Map<Long, Map<Integer, AhAppFlowLog>> flowDataCaches;
	
	private AppFlowCacheMgmt() {
		flowDataCaches = Collections.synchronizedMap(new HashMap<Long, Map<Integer, AhAppFlowLog>>());
	}

	public synchronized static AppFlowCacheMgmt getInstance() {
		if(null == cacheMgmt) {
			cacheMgmt = new AppFlowCacheMgmt();
		}
		return cacheMgmt;
	}
	
	public int size() {
		return flowDataCaches.size();
	}
	
	public void cleanFlowData() {
		flowDataCaches.clear();
	}
	
	public void saveFlowData(long ownerId, List<AhAppFlowLog> appFlowLogList) {
		if (appFlowLogList == null || appFlowLogList.size() == 0) {
			return;
		}
		Map<Integer, AhAppFlowLog> singleVhmMap = flowDataCaches.get(ownerId);
		singleVhmMap = (singleVhmMap == null) ? new HashMap<Integer, AhAppFlowLog>() : singleVhmMap;

		for (AhAppFlowLog log : appFlowLogList) {
			if (singleVhmMap.get(log.getAppCode()) == null) {
				singleVhmMap.put(log.getAppCode(), new AhAppFlowLog());
			}
			AhAppFlowLog singleAppData = singleVhmMap.get(log.getAppCode());
			singleAppData.setBytes(singleAppData.getBytes() + log.getBytes());
			singleAppData.setPackets(singleAppData.getPackets() + log.getPackets());
		}
		flowDataCaches.put(ownerId, singleVhmMap);
	}
	       
    public Map<Integer, AhAppFlowLog> getFlowData(long ownerId) {
    	return flowDataCaches.get(ownerId);
    }
    
    public Map<Long, Map<Integer, AhAppFlowLog>> getAllFlowData() {
    	return flowDataCaches;
    }
    
}