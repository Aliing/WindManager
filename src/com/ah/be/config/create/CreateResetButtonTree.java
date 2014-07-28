package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ResetButtonProfileInt;
import com.ah.xml.be.config.ResetButtonObj;

/**
 * @author zhang
 * @version 2007-12-3  04:11:26
 */

public class CreateResetButtonTree {
	
	private ResetButtonProfileInt resetButtonImpl;
	private ResetButtonObj resetButtonObj;
	
	private GenerateXMLDebug oDebug;

	public CreateResetButtonTree(ResetButtonProfileInt resetButtonImpl, GenerateXMLDebug oDebug){
		this.resetButtonImpl = resetButtonImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		if(resetButtonImpl.isConfigureResetButton()){
			resetButtonObj = new ResetButtonObj();
			generateChildLevel_1();
		}
	}
	
	public ResetButtonObj getResetButtonObj(){
		return this.resetButtonObj;
	}
	
	private void generateChildLevel_1() {
		/**
		 * <reset-button>		ResetButtonObj
		 */
		
		/** attribute: updateTime */
		resetButtonObj.setUpdateTime(resetButtonImpl.getUpdateTime());
		
		/** element: <reset-button>.<reset-config-enable> */
		oDebug.debug("/configuration/reset-button", 
				"reset-config-enable", GenerateXMLDebug.SET_OPERATION,
				resetButtonImpl.getMgmtServiceGuiName(), resetButtonImpl.getMgmtServiceName());
		resetButtonObj.setResetConfigEnable(
				CLICommonFunc.getAhOnlyAct(resetButtonImpl.isEnableResetConfig())
		);
	}
}
