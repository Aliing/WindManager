package com.ah.be.config.create;
import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.ApplicationProfileInt;
import com.ah.xml.be.config.AppCdpIndex;
import com.ah.xml.be.config.ApplicationIdentification;
import com.ah.xml.be.config.ApplicationObj;
import com.ah.xml.be.config.ApplicationReporting;
import com.ah.xml.be.config.ApplicationReportingAppId;
/**
 * @author llchen
 * @version 2012-09-25 9:36:43 AM
 */

public class CreateApplicationTree {
	private ApplicationProfileInt applicationProfileImpl;
	private ApplicationObj applicationObj;
	private GenerateXMLDebug oDebug;
	private final List<Object> applicationChildList_1 = new ArrayList<Object>();

	public CreateApplicationTree(ApplicationProfileInt applicationProfileImpl, GenerateXMLDebug oDebug) throws Exception {
		this.applicationProfileImpl = applicationProfileImpl;
		this.oDebug = oDebug;
	}
	
	public ApplicationObj getApplicationObj(){
		return this.applicationObj;
	}
	
	public void generate() throws Exception{
		if(this.applicationProfileImpl != null){
			applicationObj = new ApplicationObj();
			generateApplicationLevel_1();
		}
	}	
	
	private void generateApplicationLevel_1(){
		/**
		 * <application>		ApplicationObj
		 */
		
		/** element: <application>.<reporting> */
		ApplicationReporting reportingObj = new ApplicationReporting();
		applicationChildList_1.add(reportingObj);
		applicationObj.setReporting(reportingObj);
		
		/** element: <application>.<identification> */
		ApplicationIdentification identification = new ApplicationIdentification();
		applicationChildList_1.add(identification);
		applicationObj.setIdentification(identification);
		
		generateApplicationLevel_2();
	}
	
	private void generateApplicationLevel_2(){
		for(Object childObj : applicationChildList_1) {
			
			if(childObj instanceof ApplicationReporting){
				ApplicationReporting reportingObj = (ApplicationReporting)childObj;
				/** element: <application>.<reporting>.<enable> */
				//reportingObj.setEnable(CLICommonFunc.getAhOnlyAct(applicationProfileImpl.isConfigApplicationReporting()));
				//reportingObj.setEnable("");
				reportingObj.setAuto(CLICommonFunc.getAhOnlyAct(true));
				
				
				for(int i=0;i<applicationProfileImpl.getReportingAppSize();i++){
					ApplicationReportingAppId appId = createApplicationReportingAppId(i);
					/** element: <application>.<reporting>.<app-id> */
					reportingObj.getAppId().add(appId);
					/** element: <application>.<reporting>.<watch-list> */
					reportingObj.getWatchList().add(appId);
				}
				
			}
			
			if(childObj instanceof ApplicationIdentification){
				ApplicationIdentification identification = (ApplicationIdentification) childObj;
				/** element: <application>.<identification>.<shutdown> */
				identification.setShutdown(CLICommonFunc.getAhOnlyAct(!applicationProfileImpl.isEnableL7Switch()));
				
				for (int i = 0; i < applicationProfileImpl.getCustomAppSize(); i++) {
					/** element: <application>.<identification>.<cdp-index> */
					AppCdpIndex cdpIndex = createAppCdpIndex(i);
					identification.getCdpIndex().add(cdpIndex);
				}
				
			}
		}
	}
	
	private AppCdpIndex createAppCdpIndex(int appIndex) {
		AppCdpIndex cdpIndex = new AppCdpIndex();
		cdpIndex.setName(applicationProfileImpl.getCustomAppCode(appIndex));
		cdpIndex.setCdpName(CLICommonFunc.getAhString(applicationProfileImpl.getCustomAppName(appIndex)));
		cdpIndex.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		for (int ruleIndex = 0; ruleIndex < applicationProfileImpl.getCustomAppRuleSize(appIndex); ruleIndex++) {
			/** element: <application>.<identification>.<cdp-index>.<cdp-rule> */
			String ruleValue = applicationProfileImpl.getCustomAppRuleValue(appIndex, ruleIndex);
			//cdpIndex.getCdpRule().add(CLICommonFunc.createAhNameActValue(ruleValue, true));
			cdpIndex.getCdpRule().add(CLICommonFunc.createAhNameActValueQuoteProhibited(ruleValue, true, true));
		}
		return cdpIndex;
	}
	
	private ApplicationReportingAppId createApplicationReportingAppId(int index){
		ApplicationReportingAppId appidObj = new ApplicationReportingAppId();
		
		/** element: <application>.<reporting>.<app-id>.<enable> */
		appidObj.setEnable(CLICommonFunc.getAhOnlyAct(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		oDebug.debug("/configuration/application/reporting/app-id", 
				"name", GenerateXMLDebug.SET_NAME,
				applicationProfileImpl.getApplicationGuiName(), applicationProfileImpl.getApplicationName());
		appidObj.setName(applicationProfileImpl.getReportAppId(index));
		
		/** attribute: operation */
		appidObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		return appidObj;
	}
}
