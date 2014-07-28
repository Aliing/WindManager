package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.source.SpanningTreeInt;
import com.ah.xml.be.config.SpanningTreeMode;
import com.ah.xml.be.config.SpanningTreeMstInstance;
import com.ah.xml.be.config.SpanningTreeObj;

public class CreateSpanningTree {
	
	private SpanningTreeInt spanImpl;
	
	private SpanningTreeObj spanningTreeObj;
	
	private List<Object> spanningList_1 = new ArrayList<Object>();

	public CreateSpanningTree(SpanningTreeInt spanImpl){
		this.spanImpl = spanImpl;
	}
	
	public SpanningTreeObj getSpanningTreeObj(){
		return this.spanningTreeObj;
	}
	
	public void generate(){
		
		if(spanImpl.isConfigSpanningTree()){
			
			/** element: <spanning-tree> */
			spanningTreeObj = new SpanningTreeObj();
			
			generateSpanningTreeLevel_1();
		}
	}
	
	private void generateSpanningTreeLevel_1(){
		
		/** element: <spanning-tree>.<enable> */
		spanningTreeObj.setEnable(CLICommonFunc.getAhOnlyAct(spanImpl.isEnableSpanningTree()));
		
		if(spanImpl.isEnableSpanningTree()){
			
			/** element: <spanning-tree>.<mode> */
			SpanningTreeMode modeObj = new SpanningTreeMode();
			spanningList_1.add(modeObj);
			spanningTreeObj.setMode(modeObj);
			
			if(spanImpl.isModeMstp()){
				
				/** element: <spanning-tree>.<region> */
				if(spanImpl.isConfigRegion()){
					spanningTreeObj.setRegion(CLICommonFunc.createAhStringActObj(
							spanImpl.getRegionValue(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <spanning-tree>.<revision> */
				if(spanImpl.isConfigRevision()){
					spanningTreeObj.setRevision(CLICommonFunc.createAhIntActObj(
							spanImpl.getRevision(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <spanning-tree>.<max-hops> */
				if(spanImpl.isConfigMaxHops()){
					spanningTreeObj.setMaxHops(CLICommonFunc.createAhIntActObj(
							spanImpl.getMaxHops(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <spanning-tree>.<mst-instance> */
				for(int index=0; index<spanImpl.getMstInstanceSize(); index++){
					spanningTreeObj.getMstInstance().add(this.createSpanningTreeMstInstance(index));
				}
			}
			
			if(spanImpl.isEnableDeviceSpanningTree()){
				
				/** element: <spanning-tree>.<forward-time> */
				spanningTreeObj.setForwardTime(CLICommonFunc.createAhIntActObj(
						spanImpl.getForwardTime(), CLICommonFunc.getYesDefault()));
				
				/** element: <spanning-tree>.<hello-time> */
				if(spanImpl.isConfigHelloTime()){
					spanningTreeObj.setHelloTime(CLICommonFunc.createAhIntActObj(
							spanImpl.getHelloTime(), CLICommonFunc.getYesDefault()));
				}
				
				/** element: <spanning-tree>.<max-age> */
				spanningTreeObj.setMaxAge(CLICommonFunc.createAhIntActObj(
						spanImpl.getMaxAge(), CLICommonFunc.getYesDefault()));
				
				/** element: <spanning-tree>.<priority> */
				spanningTreeObj.setPriority(CLICommonFunc.createAhIntActObj(
						spanImpl.getPriority(), CLICommonFunc.getYesDefault()));
				
				/** element: <spanning-tree>.<force-version> */
				if(spanImpl.isConfigForceVersion()){
					spanningTreeObj.setForceVersion(CLICommonFunc.createAhIntActObj(
							spanImpl.getForceVersion(), CLICommonFunc.getYesDefault()));
				}
			}
		}
		
		generateSpanningTreeLevel_2();
	}
	
	private void generateSpanningTreeLevel_2(){
		/**
		 * <spanning-tree>.<mode>					SpanningTreeMode
		 */
		for(Object childObj : spanningList_1){
			
			/** element: <spanning-tree>.<mode> */
			if(childObj instanceof SpanningTreeMode){
				SpanningTreeMode modeObj = (SpanningTreeMode)childObj;
				
				/** attribute: operation */
				modeObj.setOperation(CLICommonFunc.getAhEnumAct(CLICommonFunc.getYesDefault()));
				
				/** attribute: value */
				modeObj.setValue(spanImpl.getSpanningMode());
			}
		}
		spanningList_1.clear();
	}
	
	private SpanningTreeMstInstance createSpanningTreeMstInstance(int index){
		SpanningTreeMstInstance instance = new SpanningTreeMstInstance();
		
		/** attribute: operation */
		instance.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc.getYesDefault()));
		
		/** attribute: name */
		instance.setName(spanImpl.getMstInstanceName(index));
		
		/** element: <spanning-tree>.<mst-instance>.<cr> */
		instance.setCr("");
		
		/** element: <spanning-tree>.<mst-instance>.<priority> */
		instance.setPriority(CLICommonFunc.createAhIntActObj(
				spanImpl.getInstancePriority(index), CLICommonFunc.getYesDefault()));
		
		/** element: <spanning-tree>.<mst-instance>.<vlan> */
		for(int i=0; i<spanImpl.getInstanceVlanSize(index); i++){
			instance.getVlan().add(CLICommonFunc.createAhNameActValueQuoteProhibited(
					spanImpl.getInstanceVlanName(index, i), CLICommonFunc.getYesDefault(), CLICommonFunc.getYesDefault() ));
		}
		
		return instance;
	}
	
}
