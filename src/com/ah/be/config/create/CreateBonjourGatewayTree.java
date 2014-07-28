package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.BonjourGatewayInt;
import com.ah.xml.be.config.BonjourGatewayFilter;
import com.ah.xml.be.config.BonjourGatewayFilterRule;
import com.ah.xml.be.config.BonjourGatewayObj;
import com.ah.xml.be.config.BonjourGatewayPriority;
import com.ah.xml.be.config.BonjourGatewayRuleBefore;
import com.ah.xml.be.config.BonjourGatewayRuleFrom;
import com.ah.xml.be.config.BonjourGatewayRuleMetric;
import com.ah.xml.be.config.BonjourGatewayRuleService;
import com.ah.xml.be.config.BonjourGatewayRuleTo;

public class CreateBonjourGatewayTree {
	
	private BonjourGatewayInt bonjourGatewayImpl;
	private GenerateXMLDebug oDebug;
	private final List<Object> bonjourChildList_1 = new ArrayList<Object>();
	private final List<Object> ruleChildList_1 = new ArrayList<Object>();
	private final List<Object> ruleChildList_2 = new ArrayList<Object>();
	private final List<Object> ruleChildList_3 = new ArrayList<Object>();
	private final List<Object> ruleChildList_4 = new ArrayList<Object>();
//	private final List<Object> ruleChildList_5 = new ArrayList<Object>();
	private BonjourGatewayObj bonjourGatewayObj;
	private String realmName;
	
	public CreateBonjourGatewayTree(BonjourGatewayInt bonjourGatewayImpl, GenerateXMLDebug oDebug){
		this.bonjourGatewayImpl = bonjourGatewayImpl;
		this.oDebug = oDebug;
	}
	
	public BonjourGatewayObj getBonjourGatewayObj(){
		return this.bonjourGatewayObj;
	}
	
	public void generate() throws Exception{
		bonjourGatewayObj = new BonjourGatewayObj();
		generateBonjourGatewayLevel_1();
		
	}
	
	private void generateBonjourGatewayLevel_1() throws Exception{
				
		/**element: <bonjour-gateway>.<enable> */
		bonjourGatewayObj.setEnable(CLICommonFunc.getAhOnlyAct(bonjourGatewayImpl.isEnableBonjourGateway()));
		
		/**element: <bonjour-gateway>.<filter> */
		if(bonjourGatewayImpl.isEnableBonjourGateway()){
			BonjourGatewayFilter filterObj = new BonjourGatewayFilter();
			bonjourChildList_1.add(filterObj);
			bonjourGatewayObj.setFilter(filterObj);
		}
		
		/**element: <bonjour-gateway>.<vlan> */
		oDebug.debug("/configuration/bonjour-gateway", "vlan", GenerateXMLDebug.CONFIG_ELEMENT,
				bonjourGatewayImpl.getWlanName(), bonjourGatewayImpl.getBonjourGatewayGuiName());
		for(int i=0;i<bonjourGatewayImpl.getBonjourVlanSize();i++){
			bonjourGatewayObj.getVlan().add(CLICommonFunc.createAhNameActValueQuoteProhibited(bonjourGatewayImpl.getBonjourValn(i),
			CLICommonFunc.getYesDefault(),CLICommonFunc.getYesDefault()));
		}
		
		if(bonjourGatewayImpl.isConfigPriority()) {
			/**element: <bonjour-gateway>.<priority> */
			oDebug.debug("/configuration/bonjour-gateway", "priority", GenerateXMLDebug.CONFIG_ELEMENT,
					bonjourGatewayImpl.getWlanName(), bonjourGatewayImpl.getBonjourGatewayGuiName());
			Object[][] priorityParm = {
					{CLICommonFunc.ATTRIBUTE_VALUE, bonjourGatewayImpl.getPriority()},
					{CLICommonFunc.ATTRIBUTE_OPERATION, CLICommonFunc.getYesDefault()}
			};
			bonjourGatewayObj.setPriority(
					(BonjourGatewayPriority)CLICommonFunc.createObjectWithName(
							BonjourGatewayPriority.class, priorityParm));
			}
		
		if (StringUtils.isNotBlank(bonjourGatewayImpl.getRealmName())) {
			bonjourGatewayObj.setRealm(CLICommonFunc.createAhStringActObj(bonjourGatewayImpl.getRealmName(), true));
		}
				
		generateBonjourGatewayLevel_2();
	}
	
	private void generateBonjourGatewayLevel_2() throws Exception{
		for(Object childObj : bonjourChildList_1){
			if(childObj instanceof BonjourGatewayFilter){
				/**element: <bonjour-gateway>.<filter> */
				BonjourGatewayFilter filterObj = (BonjourGatewayFilter)(childObj);
				for(int index =0;index < bonjourGatewayImpl.getBonjourGatewayFiletrRulesize();index++){
					/**element: <bonjour-gateway>.<filter>.<rule> */
					oDebug.debug("/configuration/bonjour-gateway/filter", "rule", GenerateXMLDebug.CONFIG_ELEMENT,
					bonjourGatewayImpl.getWlanName(), bonjourGatewayImpl.getBonjourGatewayGuiName());
					filterObj.getRule().add(createBonjourGatewayFilterRule(index));
				}
			}
		}
		bonjourChildList_1.clear();
	}
	
	private BonjourGatewayFilterRule createBonjourGatewayFilterRule(int index) throws Exception{
		BonjourGatewayFilterRule filterRule = new BonjourGatewayFilterRule();
		/** attribute: operation */
		filterRule.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		/** attribute: name */
		
		filterRule.setName(bonjourGatewayImpl.getBonjourGatewayFiletrRuleId(index));
		/** element: <cr> */
		if(bonjourGatewayImpl.isConfigBonjouGateway()){
			oDebug.debug("/configuration/bonjour-gateway/filter/rule", "cr", GenerateXMLDebug.CONFIG_ELEMENT,
					bonjourGatewayImpl.getWlanName(), bonjourGatewayImpl.getBonjourGatewayGuiName());
			filterRule.setCr(CLICommonFunc.getAhString(bonjourGatewayImpl.getBonjourGatewayFiletrRuleName(index)));
		}
		/**element: <bonjour-gateway>.<filter>.<rule>.<before>*/
		if(bonjourGatewayImpl.isConfigBefore(index)){
			BonjourGatewayRuleBefore beforeObj = new BonjourGatewayRuleBefore();
			ruleChildList_1.add(beforeObj);
			filterRule.setBefore(beforeObj);
		}
		
		/**element: <bonjour-gateway>.<filter>.<rule>.<from>*/
		BonjourGatewayRuleFrom fromObj = new BonjourGatewayRuleFrom();
		ruleChildList_1.add(fromObj);
		filterRule.setFrom(fromObj);
		
		genereateBonjourFilterLevel_1(index);
		
		return filterRule;
	}
	
	private void genereateBonjourFilterLevel_1(int index) throws Exception{
		for(Object childObj : ruleChildList_1){
			
			if(childObj instanceof BonjourGatewayRuleFrom){
				BonjourGatewayRuleFrom fromObj = (BonjourGatewayRuleFrom)childObj;
				
				/**element: <bonjour-gateway>.<filter>.<rule><from>*/
				BonjourGatewayRuleService serviceObj = new BonjourGatewayRuleService();
				ruleChildList_2.add(serviceObj);
				fromObj.setCr(serviceObj);
				
				/** attribute: value */
				fromObj.setValue(bonjourGatewayImpl.getFromGroupName(index));
			}
			
			/**element: <bonjour-gateway>.<filter>.<rule>.<before>*/
			if(childObj instanceof BonjourGatewayRuleBefore){
				BonjourGatewayRuleBefore beforeObj = (BonjourGatewayRuleBefore)childObj;
				
				/** attribute: value */
				beforeObj.setValue(bonjourGatewayImpl.getBeforeValue(index));
				
				/** attribute: operation */
				beforeObj.setOperation(CLICommonFunc.getAhEnumShow(CLICommonFunc.getYesDefault()));
				
				/** attribute: quoteProhibited */
				beforeObj.setQuoteProhibited(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/**element: <bonjour-gateway>.<filter>.<rule>.<before>*/
				oDebug.debug("/configuration/bonjour-gateway/filter/rule/before", "rule", 
						GenerateXMLDebug.CONFIG_ELEMENT,
				bonjourGatewayImpl.getWlanName(), bonjourGatewayImpl.getBonjourGatewayGuiName());
				beforeObj.setRule(
						CLICommonFunc.createAhStringObj(bonjourGatewayImpl.getBonjourBeforeIdValue(index))
					);
			}
		}
		ruleChildList_1.clear();
		genereateBonjourFilterLevel_2(index);
	}
	
	private void genereateBonjourFilterLevel_2(int index){
		for(Object childObj : ruleChildList_2){
			BonjourGatewayRuleService serviceObj = (BonjourGatewayRuleService)childObj;
			/** attribute: value */
			serviceObj.setValue(bonjourGatewayImpl.getBonjourGatewayFiletrRuleName(index));
			
			/**element: <bonjour-gateway>.<filter>.<rule><from><to>*/
			BonjourGatewayRuleTo ruleToObj = new BonjourGatewayRuleTo();
			serviceObj.setTo(ruleToObj);
			ruleChildList_3.add(ruleToObj);
		}
		ruleChildList_2.clear();
		genereateBonjourFilterLevel_3(index);
	}
	
	private void genereateBonjourFilterLevel_3(int index){
		for(Object childObj : ruleChildList_3){
			BonjourGatewayRuleTo ruleToObj = (BonjourGatewayRuleTo)childObj;
			/** attribute: value */
			ruleToObj.setValue(bonjourGatewayImpl.getToGroupName(index));
			
			/**element: <bonjour-gateway>.<filter>.<rule><from><to><metric>*/
			BonjourGatewayRuleMetric metricObj = new BonjourGatewayRuleMetric();
			ruleToObj.setMetric(metricObj);
			ruleChildList_4.add(metricObj);
		}
		ruleChildList_3.clear();
		genereateBonjourFilterLevel_4(index);
		
	}
	
	private void genereateBonjourFilterLevel_4(int index){
		for(Object childObj : ruleChildList_4){
			BonjourGatewayRuleMetric metricObj = (BonjourGatewayRuleMetric)childObj;
			/** attribute: value */
			metricObj.setValue(bonjourGatewayImpl.getMetricValue(index));
			
			//from 6.2.1.0 no need config action, all with action permit.
//			/**element: <bonjour-gateway>.<filter>.<rule><from><to><metric><action>*/
//			if(bonjourGatewayImpl.isConfigRuleAction()){
//				BonjourGatewayRuleAction actionObj = new BonjourGatewayRuleAction();
//				metricObj.setAction(actionObj);
//				ruleChildList_5.add(actionObj);
//			}
		}
		ruleChildList_4.clear();
//		genereateBonjourFilterLevel_5(index);
	}
	
//	private void genereateBonjourFilterLevel_5(int index){
//		for(Object childObj : ruleChildList_5){
//			BonjourGatewayRuleAction actionObj = (BonjourGatewayRuleAction)childObj;
//			actionObj.setValue(AhPermitDenyValue.PERMIT);
//			/**element: <bonjour-gateway>.<filter>.<rule><from><to><metric><action><permit>*/
//			if(bonjourGatewayImpl.isCOnfigRuleActionPermit(index)){
//				/** attribute: value */
//				actionObj.setValue(AhPermitDenyValue.PERMIT);
//			}
//			/**element: <bonjour-gateway>.<filter>.<rule><from><to><metric><action><deny>*/
//			if(bonjourGatewayImpl.isCOnfigRuleActionDeny(index)){
//				/** attribute: value */
//				actionObj.setValue(AhPermitDenyValue.DENY);
//			}
//			
//		}
//		ruleChildList_5.clear();
//	}
}
