package com.ah.be.config.create;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.HivemanagerProfileInt;
import com.ah.xml.be.config.HivemanagerObj;

/**
 * @author zhang
 * @version 2008-1-4 10:09:11 AM
 */

public class CreateHivemanagerTree {

	private HivemanagerProfileInt hmImpl;
	private HivemanagerObj hmObj;
	
	private GenerateXMLDebug oDebug;

	public CreateHivemanagerTree(HivemanagerProfileInt hmImpl, GenerateXMLDebug oDebug) {
		this.hmImpl = hmImpl;
		this.oDebug = oDebug;
	}
	
	public void generate(){
		hmObj = new HivemanagerObj();
		generateHivemanagerLevel_1();
	}

	public HivemanagerObj getHivemanagerObj() {
		return this.hmObj;
	}

	public void generateHivemanagerLevel_1() {
		/**
		 * <hivemanager> HivemanagerObj
		 */

		/** attribute: updateTime */
		hmObj.setUpdateTime(hmImpl.getUpdateTime());

		/** attribute: operation */
		hmObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc
				.getYesDefault()));

		/** attribute: value */
		oDebug.debug("/configuration", 
				"hivemanager", GenerateXMLDebug.SET_VALUE,
				hmImpl.getHiveApGuiName(), hmImpl.getHiveApName());
		hmObj.setValue(hmImpl.getHiverManagerIp());
	}

}