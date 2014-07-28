package com.ah.be.config.create.source.impl.branchRouter;

import com.ah.be.common.NmsUtil;
import com.ah.be.config.create.source.impl.baseImpl.PseProfileBaseImpl;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
/**
 * @author llchen
 * @version 2011-12-29 9:36:43 AM
 */
public class PseProfileBRImpl extends PseProfileBaseImpl {
	
	private HiveAp hiveAp;
	public PseProfileBRImpl(HiveAp hiveAp) {
		this.hiveAp = hiveAp;
	}
	
	public boolean isConfigPse(){
		return hiveAp.getMaxPowerSource() > 0;
	}
	
	public int getMaxPowerSource() {
		if(NmsUtil.compareSoftwareVersion(hiveAp.getSoftVer(),"5.1.1.0") > 0){
			return hiveAp.getMaxPowerSource();
		}else{
			return hiveAp.getMaxPowerSource() > 40 ? 40 : hiveAp.getMaxPowerSource();
		}
	}
	
	public String getWlanGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.networkPolicy");
	}
	
	public String getWlanName(){
		return hiveAp.getConfigTemplate().getConfigName();
	}
	
	public String getPseGuiName(){
		return MgrUtil.getUserMessage("config.upload.debug.pseConfiguration");
	}
}
