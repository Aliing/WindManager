package com.ah.util;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.ah.be.communication.RemotePortalOperationRequest;
import com.ah.be.communication.mo.VhmInfo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;

public class SupportAccessUtil {
	
	private static final Tracer	log	= new Tracer(SupportAccessUtil.class.getSimpleName());
	
	public static final String ACCESS_OPTION_PREFIX="hm.config.start.supportAccess.option.";
	
	public static ScheduledExecutorService getAccessMonitorScheduler(long initialDelay,
			long period, TimeUnit timeUnit) {
		ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				Long currentDate=new Date().getTime();
				try {
//					QueryUtil.updateBos(HmDomain.class, "accessMode=:s1,authorizationEndDate=:s2", 
//									"(accessMode=:s3 OR accessMode=:s4) AND authorizationEndDate<=:s5",
//									new Object[]{HmDomain.ACCESS_MODE_TECH_OP_PARTNER_DENY,-1L,HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_R,HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_RW,currentDate}
//									);
					List<?> vhmInfos=QueryUtil.executeQuery("select id,domainName,accessMode,authorizationEndDate from "+HmDomain.class.getSimpleName(), new SortParams("id"), 
							new FilterParams("(accessMode=:s1 OR accessMode=:s2) AND authorizationEndDate<=:s3",new Object[]{HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_R,HmDomain.ACCESS_MODE_TECH_OP_PARTNER_24H_RW,currentDate}));
					if(!vhmInfos.isEmpty())
					{
						Iterator<?> iter=vhmInfos.iterator();
						VhmInfo vhmInfo=new VhmInfo();
						while(iter.hasNext())
						{
							Object[] vhmInfoObj=(Object[])iter.next();
							vhmInfo.setVhmName(String.valueOf(vhmInfoObj[1]));
							vhmInfo.setAccessMode(HmDomain.ACCESS_MODE_TECH_OP_PARTNER_DENY);
							vhmInfo.setAuthorizationEndDate(-1L);
							try{
								RemotePortalOperationRequest.modifyVhmInfo(vhmInfo);
								QueryUtil.updateBo(HmDomain.class, "accessMode=:s1,authorizationEndDate=:s2", new FilterParams("id=:s3",new Object[]{HmDomain.ACCESS_MODE_TECH_OP_PARTNER_DENY,-1L,(Long)vhmInfoObj[0]}));
							} catch (Exception e) {
								log.error("AccessMonitorTask", "accessMonitorScheduler" + " process failed,vhmId="+String.valueOf(vhmInfoObj[0])
										+"vhmName="+String.valueOf(vhmInfoObj[1])
										+"accessMode="+ vhmInfoObj[2]
										+"authorizationEndDate="+ vhmInfoObj[3], e);
							} 
						}
					}
				} catch (Exception e) {
					log.error("AccessMonitorTask", "AccessMonitor Exception", e);
				}
			}
		}, initialDelay, period, timeUnit);
		return scheduler;
	}

}