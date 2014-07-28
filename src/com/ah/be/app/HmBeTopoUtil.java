/**
 *@filename		HmBeTopoUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5  04:22:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

import com.ah.be.cli.cliwindow.CommandExecutor;
import com.ah.be.topo.MapLinkPolling;
import com.ah.be.topo.MapLinkProcessorViaCapwap;
import com.ah.be.topo.PollingController;
import com.ah.be.topo.idp.IdpEventListener;
import com.ah.be.topo.idp.IdpScheduledExecutor;


/**
 * @author		Steven
 * @version		V1.0.0.0 
 */
public class HmBeTopoUtil
{
	public static IdpEventListener getIdpEventListener() {
		return isNullTopoModule() ? null : AhAppContainer.HmBe.getBeTopoModule().getIdpEventListener();
	}

	public static IdpScheduledExecutor getIdpScheduledExecutor() {
		return isNullTopoModule() ? null : AhAppContainer.HmBe.getBeTopoModule().getIdpScheduledExecutor();
	}
	
	public static MapLinkProcessorViaCapwap getMapLinkProcessor() {
		return isNullTopoModule() ? null : AhAppContainer.HmBe.getBeTopoModule().getMapLinkProcessor();
	}

	public static MapLinkPolling getMapLinkPolling() {
		return isNullTopoModule() ? null : AhAppContainer.HmBe.getBeTopoModule().getMapLinkPolling();
	}
	
	public static CommandExecutor getCommandExecutor() {
		return isNullTopoModule() ? null : AhAppContainer.HmBe.getBeTopoModule().getCommandExecutor();
	}
	
	public static PollingController getPollingController() {
        return isNullTopoModule() ? null : AhAppContainer.HmBe.getBeTopoModule().getPollingController();
	}
	
	private static boolean isNullTopoModule() {
	    return null == AhAppContainer.HmBe.getBeTopoModule();
	}
}
