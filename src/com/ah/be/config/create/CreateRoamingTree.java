package com.ah.be.config.create;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.config.create.common.GenerateXMLDebug;
import com.ah.be.config.create.source.RoamingProfileInt;
import com.ah.xml.be.config.AhEnable;
import com.ah.xml.be.config.CacheBroadcastNeighborType;
import com.ah.xml.be.config.CacheUpdateInterval;
import com.ah.xml.be.config.IncludeIp;
import com.ah.xml.be.config.NeighborQueryInterval;
import com.ah.xml.be.config.RoamingObj;

/**
 * 
 * @author zhang
 *
 */
public class CreateRoamingTree {

	private RoamingProfileInt roamingImpl;
	private RoamingObj roamingObj;
	
	private GenerateXMLDebug oDebug;

	private List<Object> raomingChildList_1 = new ArrayList<Object>();
	private List<Object> raomingChildList_2 = new ArrayList<Object>();

	public CreateRoamingTree(RoamingProfileInt roamingImpl, GenerateXMLDebug oDebug) {
		this.roamingImpl = roamingImpl;
		this.oDebug = oDebug;
	}
	
	public void generate() throws Exception{
		roamingObj = new RoamingObj();
		generateRoamingLevel_1();
	}

	public RoamingObj getRoamingObj() {
		return this.roamingObj;
	}

	private void generateRoamingLevel_1() throws Exception {
		/**
		 * <roaming> RoamingObj
		 */

		/** attribute: updateTime */
		roamingObj.setUpdateTime(roamingImpl.getUpdateTime());

		/** element: <roaming>.<port> */
		oDebug.debug("/configuration/roaming", 
				"port", GenerateXMLDebug.SET_VALUE,
				roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
		Object[][] portParm = {
				{ CLICommonFunc.ATTRIBUTE_VALUE, roamingImpl.getRoamingPort() },
				{ CLICommonFunc.ATTRIBUTE_OPERATION,
						CLICommonFunc.getYesDefault() } };
		roamingObj.setPort((RoamingObj.Port) CLICommonFunc
				.createObjectWithName(RoamingObj.Port.class, portParm));

		/** element: <roaming>.<neighbor> */
		oDebug.debug("/configuration/roaming", 
				"neighbor", GenerateXMLDebug.CONFIG_ELEMENT,
				roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
		if (roamingImpl.isConfigureNeighbor()) {
			RoamingObj.Neighbor neighborObj = new RoamingObj.Neighbor();
			raomingChildList_1.add(neighborObj);
			roamingObj.setNeighbor(neighborObj);
		}

		oDebug.debug("/configuration/roaming", 
				"cache", GenerateXMLDebug.CONFIG_ELEMENT,
				roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
		if (roamingImpl.isEnabledL3Setting()) {

			/** element: <roaming>.<cache> */
			RoamingObj.Cache cacheObj = new RoamingObj.Cache();
			raomingChildList_1.add(cacheObj);
			roamingObj.setCache(cacheObj);
			
			/** element: <roaming>.<cache-broadcast> */
			RoamingObj.CacheBroadcast cacheBroadcastObj = new RoamingObj.CacheBroadcast();
			raomingChildList_1.add(cacheBroadcastObj);
			roamingObj.setCacheBroadcast(cacheBroadcastObj);
		}

		generateRoamingLevel_2();
	}

	private void generateRoamingLevel_2() throws Exception {
		/**
		 * <roaming>.<cache> RoamingObj.Cache 
		 * <roaming>.<neighbor>		RoamingObj.Neighbor
		 * <roaming>.<cache-broadcast>			RoamingObj.CacheBroadcast
		 */
		for (Object childObj : raomingChildList_1) {

			/** element: <roaming>.<cache> */
			if (childObj instanceof RoamingObj.Cache) {
				RoamingObj.Cache cacheObj = (RoamingObj.Cache) childObj;

				/** element: <roaming>.<cache>.<update-interval> */
				CacheUpdateInterval updateIntervalObj = new CacheUpdateInterval();
				raomingChildList_2.add(updateIntervalObj);
				cacheObj.setUpdateInterval(updateIntervalObj);
			}

			/** element: <roaming>.<neighbor> */
			if (childObj instanceof RoamingObj.Neighbor) {
				RoamingObj.Neighbor neighborObj = (RoamingObj.Neighbor) childObj;

				/** element: <roaming>.<neighbor>.<query-interval> */
				oDebug.debug("/configuration/roaming/neighbor", 
						"query-interval", GenerateXMLDebug.CONFIG_ELEMENT,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				if(roamingImpl.isConfigNeighborQuery()){
					NeighborQueryInterval queryIntervalObj = new NeighborQueryInterval();
					raomingChildList_2.add(queryIntervalObj);
					neighborObj.setQueryInterval(queryIntervalObj);
				}
				
				/** element: <roaming>.<neighbor>.<include> */
				oDebug.debug("/configuration/roaming/neighbor", 
						"include", GenerateXMLDebug.CONFIG_ELEMENT,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				if (roamingImpl.isConfigureNeighborInclude()) {
					RoamingObj.Neighbor.Include includeObj = new RoamingObj.Neighbor.Include();
					raomingChildList_2.add(includeObj);
					neighborObj.setInclude(includeObj);
				}

				/** element: <roaming>.<neighbor>.<exclude> */
				oDebug.debug("/configuration/roaming/neighbor", 
						"exclude", GenerateXMLDebug.CONFIG_ELEMENT,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				if (roamingImpl.isConfigureNeighborExclude()) {
					RoamingObj.Neighbor.Exclude excludeObj = new RoamingObj.Neighbor.Exclude();
					raomingChildList_2.add(excludeObj);
					neighborObj.setExclude(excludeObj);
				}
			}
			
			/** element: <roaming>.<cache-broadcast> */
			if(childObj instanceof RoamingObj.CacheBroadcast){
				RoamingObj.CacheBroadcast cacheBroadcastObj = (RoamingObj.CacheBroadcast)childObj;
				
				/** element: <roaming>.<cache-broadcast>.<neighbor-type> */
				CacheBroadcastNeighborType neighborTypeObj = new CacheBroadcastNeighborType();
				raomingChildList_2.add(neighborTypeObj);
				cacheBroadcastObj.setNeighborType(neighborTypeObj);
			}
		}
		raomingChildList_1.clear();
		generateRoamingLevel_3();
	}

	private void generateRoamingLevel_3() throws Exception {
		/**
		 * <roaming>.<cache>.<update-interval> 		CacheUpdateInterval
		 * <roaming>.<neighbor>.<query-interval>	NeighborQueryInterval
		 * RoamingObj.Neighbor.QueryInterval
		 * <roaming>.<neighbor>.<include>			RoamingObj.Neighbor.Include
		 * <roaming>.<neighbor>.<exclude>			RoamingObj.Neighbor.Exclude
		 * <roaming>.<cache-broadcast>.<neighbor-type>		CacheBroadcastNeighborType
		 */
		for (Object childObj : raomingChildList_2) {

			/** element: <roaming>.<cache>.<update-interval> */
			if (childObj instanceof CacheUpdateInterval) {
				CacheUpdateInterval updateIntervalObj = (CacheUpdateInterval) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/roaming/cache", 
						"update-interval", GenerateXMLDebug.SET_VALUE,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				updateIntervalObj.setValue(roamingImpl.getUpdateInterval());

				/** attribute: operation */
				updateIntervalObj.setOperation(CLICommonFunc
						.getAhEnumAct(CLICommonFunc.getYesDefault()));

				/** element: <roaming>.<cache>.<update-interval>.<ageout> */
				oDebug.debug("/configuration/roaming/cache/update-interval", 
						"ageout", GenerateXMLDebug.SET_VALUE,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				Object[][] ageoutParm = { { CLICommonFunc.ATTRIBUTE_VALUE,
						roamingImpl.getCacheAgeout() } };
				updateIntervalObj
						.setAgeout((CacheUpdateInterval.Ageout) CLICommonFunc
								.createObjectWithName(
										CacheUpdateInterval.Ageout.class,
										ageoutParm));
			}

			/** element: <roaming>.<neighbor>.<query-interval> */
			if (childObj instanceof NeighborQueryInterval) {
				NeighborQueryInterval queryIntervalObj = (NeighborQueryInterval) childObj;

				/** attribute: value */
				oDebug.debug("/configuration/roaming/neighbor", 
						"query-interval", GenerateXMLDebug.SET_VALUE,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				queryIntervalObj
						.setValue(roamingImpl.getNeighborQueryInteval());

				/** attribute: operation */
				queryIntervalObj.setOperation(CLICommonFunc
						.getAhEnumAct(CLICommonFunc.getYesDefault()));

				/** element: <roaming>.<neighbor>.<query-interval>.<query-times> */
				oDebug.debug("/configuration/roaming/neighbor/query-interval", 
						"query-times", GenerateXMLDebug.SET_VALUE,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				Object[][] queryTimeParm = { { CLICommonFunc.ATTRIBUTE_VALUE,
						roamingImpl.getNeighborQueryTime() } };
				queryIntervalObj
						.setQueryTimes((NeighborQueryInterval.QueryTimes) CLICommonFunc
								.createObjectWithName(
										NeighborQueryInterval.QueryTimes.class,
										queryTimeParm));
			}

			/** element: <roaming>.<neighbor>.<include> */
			if (childObj instanceof RoamingObj.Neighbor.Include) {
				RoamingObj.Neighbor.Include includeObj = (RoamingObj.Neighbor.Include) childObj;

				/** element: <roaming>.<neighbor>.<include>.<ip> */
				oDebug.debug("/configuration/roaming/neighbor/include", 
						"ip", GenerateXMLDebug.CONFIG_ELEMENT,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				for (int i = 0; i < roamingImpl.getNeighborIncludeSize(); i++) {
					includeObj.getIp().add(createIncludeIp(i));
				}
			}

			/** element: <roaming>.<neighbor>.<exclude> */
			if (childObj instanceof RoamingObj.Neighbor.Exclude) {
				RoamingObj.Neighbor.Exclude excludeObj = (RoamingObj.Neighbor.Exclude) childObj;

				/** element: <roaming>.<neighbor>.<exclude>.<ip> */
				oDebug.debug("/configuration/roaming/neighbor/exclude", 
						"ip", GenerateXMLDebug.CONFIG_ELEMENT,
						roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
				for (int i = 0; i < roamingImpl.getNeighborExcludeSize(); i++) {
					
					oDebug.debug("/configuration/roaming/neighbor/exclude", 
							"ip", GenerateXMLDebug.SET_NAME,
							roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
					excludeObj.getIp().add(
							CLICommonFunc.createAhNameActValue(roamingImpl.getNeighborExcludeIp(i), CLICommonFunc.getYesDefault())
					);
					
				}
			}
			
			/** element: <roaming>.<cache-broadcast>.<neighbor-type> */
			if(childObj instanceof CacheBroadcastNeighborType){
				CacheBroadcastNeighborType neighborTypeObj = (CacheBroadcastNeighborType)childObj;
				
				/** element: <roaming>.<cache-broadcast>.<neighbor-type>.<access> */
				neighborTypeObj.setAccess(this.createAhEnable(roamingImpl.isRoamingCacheAccessEnable()));
				
				/** element: <roaming>.<cache-broadcast>.<neighbor-type>.<backhaul> */
				neighborTypeObj.setBackhaul(this.createAhEnable(roamingImpl.isRoamingCacheBackhaulEnable()));
			}
		}
		raomingChildList_2.clear();
	}
	
	private AhEnable createAhEnable(boolean isEnable){
		AhEnable enable = new AhEnable();
		enable.setEnable(CLICommonFunc.getAhOnlyAct(isEnable));
		return enable;
	}

	private IncludeIp createIncludeIp(int index) {
		IncludeIp includeIpObj = new IncludeIp();

		/** attribute: name */
		oDebug.debug("/configuration/roaming/neighbor/include", 
				"ip", GenerateXMLDebug.SET_NAME,
				roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
		includeIpObj.setName(roamingImpl.getNeighborIncludeIp(index));

		/** attribute: operation */
		includeIpObj.setOperation(CLICommonFunc.getAhEnumActValue(CLICommonFunc
				.getYesDefault()));

		/** element: <cr> */
		oDebug.debug("/configuration/roaming/neighbor/include/ip[@name='"+includeIpObj.getName()+"']",
				"cr", GenerateXMLDebug.SET_VALUE,
				roamingImpl.getHiveProfileGuiName(), roamingImpl.getHiveProfileName());
		IncludeIp.Cr crObj = new IncludeIp.Cr();
		includeIpObj.setCr(crObj);
		// set cr attribute
		crObj.setValue(roamingImpl.getNeighborIncludeNetMask(index));

		return includeIpObj;
	}

}