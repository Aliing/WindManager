/**
 *@filename		AhAppContainer.java
 *@version
 *@author		Steven
 *@createtime	2007-9-2 04:38:25
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.app;

import com.ah.be.admin.BeAdminModule;
import com.ah.be.communication.BeCommunicationModule;
import com.ah.be.config.BeConfigModule;
import com.ah.be.db.BeDbModule;
import com.ah.be.event.BeEventListener;
import com.ah.be.fault.BeFaultModule;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.misc.BeMiscModule;
import com.ah.be.os.BeOsLayerModule;
import com.ah.be.performance.BePerformModule;
import com.ah.be.ts.TsModule;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class AhAppContainer {

	public static HmBeApp HmBe;

	public static BeAdminModule getBeAdminModule() {
		return HmBe.getAdminModule();
	}

	public static BeCommunicationModule getBeCommunicationModule() {
		return HmBe.getCommunicationModule();
	}

	public static BeConfigModule getBeConfigModule() {
		return HmBe.getBeConfigModule();
	}

	public static BeDbModule getBeDbModule() {
		return HmBe.getBeDbModule();
	}

	public static BeEventListener getBeEventListener() {
		return HmBe.getEventListener();
	}

	public static BeFaultModule getBeFaultModule() {
		return HmBe.getFaultModule();
	}

	public static BeLicenseModule getBeLicenseModule() {
		return HmBe.getLicenseModule();
	}

	public static BeOsLayerModule getBeOsLayerModule() {
		return HmBe.getOsModule();
	}

	public static TsModule getBeTsModule() {
		return HmBe.getBeTsModule();
	}
	
	public static BePerformModule getBePerformModule() {
		return HmBe.getPerformModule();
	}

	public static BeMiscModule getBeMiscModule() {
		return HmBe.getMiscModule();
	}

	public static void main(String args[]) {
		HmBe = new HmBeApp();
		System.out.println("Welcome to use HiveManager!");
	}

}