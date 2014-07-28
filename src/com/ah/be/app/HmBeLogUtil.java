/**
 *@filename		HmBeLogUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5 04:20:18 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

import java.util.TimeZone;

import com.ah.be.log.BeLogModule;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ha.HAUtil;
import com.ah.util.Tracer;

/**
 * @author xiaolanbao
 * @version V1.0.0.0
 */
public class HmBeLogUtil {

	private static final Tracer log = new Tracer(HmBeLogUtil.class);

	public static void addSystemLog(short logLevel, String moduleName, String msg) {
		BeLogModule logModule = AhAppContainer.HmBe.getLogModule();

		if (logModule != null) {
			logModule.addSystemLog(logLevel, moduleName, msg);
		}
	}
	
	public static void generateAuditLog(short status, String comment) {
		// passive node cannot operate database
		if (HAUtil.isSlave()) {
			return;
		}
		try {
			HmAuditLog log = new HmAuditLog();
			log.setStatus(status);
			log.setOpeationComment(comment);
			log.setHostIP("127.0.0.1");
			
			HmDomain domain = BoMgmt.getDomainMgmt().getHomeDomain();
			log.setOwner(domain);
			log.setLogTimeStamp(System.currentTimeMillis());
			log.setLogTimeZone(domain != null ? domain.getTimeZoneString()
					: TimeZone.getDefault().getID());

			QueryUtil.createBo(log);
		} catch (Exception e) {
			log.error("generateAuditLog", "Create audit log error.", e);
		}
	}

}