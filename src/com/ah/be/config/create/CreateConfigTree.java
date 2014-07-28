package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ConfigProfileInt;
import com.ah.xml.be.config.ConfigObj;
import com.ah.xml.be.config.ConfigRollback;
import com.ah.xml.be.config.ConfigRollbackEnable;

public class CreateConfigTree {
	
	private ConfigProfileInt configProfileImpl;
	private GenerateXMLDebug oDebug;
	
	private ConfigObj configObj;
	
	private List<Object> configChildList_1 = new ArrayList<Object>();
	private List<Object> configChildList_2 = new ArrayList<Object>();

	public CreateConfigTree(ConfigProfileInt configProfileImpl, GenerateXMLDebug oDebug) {
		this.configProfileImpl = configProfileImpl;
		this.oDebug = oDebug;
	}
	
	public ConfigObj getConfigObj(){
		return this.configObj;
	}
	
	public void generate(){
		configObj = new ConfigObj();
		generateChildLevel_1();
	}
	
	private void generateChildLevel_1(){
		/**
		 * <config>			ConfigObj
		 */
		
		/** element: <config>.<rollback> */
		ConfigRollback rollback = new ConfigRollback();
		configChildList_1.add(rollback);
		configObj.setRollback(rollback);
		
		generateChildLevel_2();
	}
	
	private void generateChildLevel_2(){
		/**
		 * <config>.<rollback>						ConfigRollback
		 */
		for(Object childObj : configChildList_1){
			
			if(childObj instanceof ConfigRollback){
				ConfigRollback rollback = (ConfigRollback)childObj;
				
				/** element: <config>.<rollback>.<enable> */
				ConfigRollbackEnable enable = new ConfigRollbackEnable();
				configChildList_2.add(enable);
				rollback.setEnable(enable);
			}
		}
		configChildList_1.clear();
		generateChildLevel_3();
	}
	
	private void generateChildLevel_3(){
		/**
		 * <config>.<rollback>.<enable>					ConfigRollbackEnable
		 */
		for(Object childObj : configChildList_2){
			
			if(childObj instanceof ConfigRollbackEnable){
				ConfigRollbackEnable enableObj = (ConfigRollbackEnable)childObj;
				
				/** attribute: operation */
				enableObj.setOperation(CLICommonFunc.getAhEnumAct(configProfileImpl.isConfigRollbackEnable()));
			}
		}
		configChildList_2.clear();
		
	}
	
}
