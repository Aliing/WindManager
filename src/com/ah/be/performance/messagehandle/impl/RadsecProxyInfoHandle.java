package com.ah.be.performance.messagehandle.impl;

import com.ah.be.communication.event.BeRadsecProxyInfoResultEvent;
import com.ah.be.event.BeBaseEvent;
import com.ah.be.performance.messagehandle.MessageHandleInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class RadsecProxyInfoHandle implements MessageHandleInterface {
	
	private static final Tracer log = new Tracer(RadsecProxyInfoHandle.class.getSimpleName());

	@Override
	public void handleMessage(BeBaseEvent event){
		if(!(event instanceof BeRadsecProxyInfoResultEvent)) {
			return;
		}
		
		BeRadsecProxyInfoResultEvent proxyEvent = (BeRadsecProxyInfoResultEvent)event;
		
		try {
			//add IDM proxy.
			if(!proxyEvent.getAddProxy().isEmpty()){
				QueryUtil.updateBo(HiveAp.class, "IDMProxy = true", 
						new FilterParams("macAddress in (:s1) AND IDMProxy = false", new Object[]{proxyEvent.getAddProxy()} ));
			}
			
			//remove IDM proxy.
			if(!proxyEvent.getRemoveProxy().isEmpty()){
				QueryUtil.updateBo(HiveAp.class, "IDMProxy = false", 
						new FilterParams("macAddress in (:s1) AND IDMProxy = true", new Object[]{proxyEvent.getRemoveProxy()} ));
			}
			
			//empty package.
			if(proxyEvent.getAddProxy().isEmpty() && proxyEvent.getRemoveProxy().isEmpty()){
				QueryUtil.updateBo(HiveAp.class, "IDMProxy = false", 
						new FilterParams("macAddress = :s1 AND IDMProxy = true", new Object[]{proxyEvent.getApMac()} ));
			}
		} catch (Exception e) {
			log.error("BeRadsecProxyInfoResultEvent handle failed.", e);
		}
	}

}
