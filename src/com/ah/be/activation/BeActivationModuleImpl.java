/**
 *@filename		BeActivationModuleCentOsImpl.java
 *@version		v1.19
 *@author		Fiona
 *@createtime	2007-9-3 02:05:58 PM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */

package com.ah.be.activation;

import com.ah.be.app.BaseModule;
import com.ah.be.app.HmBeActivationUtil;

/**
 * @author Fiona
 * @version v1.19
 */
public class BeActivationModuleImpl extends BaseModule implements
		BeActivationModule {

	/**
	 * Constructor
	 */
	public BeActivationModuleImpl() {
		setModuleId(BaseModule.ModuleID_Activation);
		setModuleName("BeActivationModule");
	}

	/**
	 * Start license scheduler
	 */
	@Override
	public boolean run() {
		HmBeActivationUtil.ACTIVATION_KEY_TIMER = new AeroActivationTimer();
		HmBeActivationUtil.ACTIVATION_KEY_TIMER.startAllActiveTimer();
		return true;
	}

	/**
	 * @see com.ah.be.app.BaseModule#shutdown()
	 */
	@Override
	public boolean shutdown() {
		if (null != HmBeActivationUtil.ACTIVATION_KEY_TIMER) {
			HmBeActivationUtil.ACTIVATION_KEY_TIMER.stopAllActiveTimer();
		}
		return true;
	}

}