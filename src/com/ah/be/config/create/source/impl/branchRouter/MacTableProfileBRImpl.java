package com.ah.be.config.create.source.impl.branchRouter;

import com.ah.be.config.create.source.MacTableProfileInt;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
/**
 * @author llchen
 * @version 2012-01-04 9:36:43 AM
 */
public class MacTableProfileBRImpl implements MacTableProfileInt {
	
	private HiveAp hiveAp;
	public MacTableProfileBRImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}
	
	public String getWlanGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.8021xMacTableConfiguration");
	}
	
	public String getWlanName(){
		return hiveAp.getConfigTemplate().getConfigName();
	}

	public int getExpireTime() {
		return hiveAp.getConfigTemplate().getClientExpireTime8021X();
	}

	public int getSuppressInterval() {
		return hiveAp.getConfigTemplate().getClientSuppressInterval8021X();
	}
}
