/**
 *@filename		HmBeConfigUtil.java
 *@version
 *@author		Steven
 *@createtime	2007-9-5 04:13:23 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.be.app;

import com.ah.be.config.hiveap.UpdateManager;
import com.ah.be.config.hiveap.UpdateObjectBuilder;
import com.ah.be.config.hiveap.UpdateResponseListener;
import com.ah.be.config.hiveap.distribution.ImageDistributor;
import com.ah.be.config.hiveap.provision.ProvisionProcessor;

/**
 * @author Steven
 * @version V1.0.0.0
 */
public class HmBeConfigUtil {
	public static UpdateManager getUpdateManager() {
		return AhAppContainer.getBeConfigModule().getUpdateManager();
	}

	public static UpdateResponseListener getUpdateResponseListener() {
		return AhAppContainer.getBeConfigModule().getUpdateResponseListener();
	}

	public static ImageDistributor getImageDistributor() {
		return AhAppContainer.getBeConfigModule().getImageDistributor();
	}

	public static UpdateObjectBuilder getUpdateObjectBuilder() {
		return AhAppContainer.getBeConfigModule().getUpdateObjectBuilder();
	}
	
	public static ProvisionProcessor getProvisionProcessor() {
		return AhAppContainer.getBeConfigModule().getProvisionProcessor();
	}
}