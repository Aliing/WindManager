package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class DefaultWidgetConfig {
	private static final Tracer	log	= new Tracer(DefaultWidgetConfig.class
			.getSimpleName());
	
	public static void createAllDefaultWidgets(){
		createApplicationInformation();
		createApsForApplication();
		createBandwithUsageTrendOverTime();
		createClientsForApplication();
		createClientStatus();
		createDeviceStatus();
		createDevicesWithUser();
		createDistributionOfClientOs();
		createNumOfActiveClientsOverTime();
		createSSIDsForApplication();
		createSSIDsWithUser();
		createTimeForApplicateion();
		createTopApplicationsAccessed();
		createTopNApplicationsByUsage();
		createTopNUsersByBandwidthUsage();
		createTopUsernamesView();
		createTopDevicesByBandwidthUsage();
		//.......
	}

	/**
	 * Top N applications by usage ( in bytes)
	 */
	public static void createTopNApplicationsByUsage(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Top N applications by usage", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "bandwidth application", "Application","Top Applications by Data Usage",groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Top N users by bandwidth usage  display as table  columns ( username, usage in bytes, top app used, latest client device MAC used)
	 */
	public static void createTopNUsersByBandwidthUsage(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		yaxisList.add("top application");
		yaxisList.add("last client MAC");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		displayNameList.add("Top App");
		displayNameList.add("Last Client");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		displayValueList.add("[Top Application by Data Usage]");
		displayValueList.add("[The Last Client Device Used]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		validateBreakdownList.add(true);
		validateBreakdownList.add(true);
	
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(3);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Top N users by bandwidth usage", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "bandwidth user","User","Top Users by Data Usage", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * No of active clients over time
	 */
	public static void createNumOfActiveClientsOverTime(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("total distinct client devices");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Clients");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Total Number of Client Devices]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("No of active clients over time", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "time","Time","Over Time", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Top N Aerohive devices by bandwidth usage
	 */
	public static void createTopDevicesByBandwidthUsage(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("inbound 2.4 GHz bytes");
		yaxisList.add("inbound 5 GHz bytes");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("2.4 GHz");
		displayNameList.add("5 GHz");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Inbound 2.4 GHz Byte Usage] + [Outbound 2.4 GHz Byte Usage]");
		displayValueList.add("[Inbound 5 GHz Byte Usage] + [Outbound 5 GHz Byte Usage]");
		
		List<String> displayValueKeyList = new ArrayList<String>();
		displayValueKeyList.add("[inbound 2.4 GHz bytes] + [outbound 2.4 GHz bytes]");
		displayValueKeyList.add("[inbound 5 GHz bytes] + [outbound 5 GHz bytes]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		validateBreakdownList.add(true);
		
		List<Short> levelBreakDownList = new ArrayList<Short>();
		levelBreakDownList.add((short)-1);
		levelBreakDownList.add((short)-1);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(2,itemMap,yaxisList,displayNameList,displayValueList,displayValueKeyList,levelBreakDownList,validateBreakdownList);
		createDeafultWidget("Top N Aerohive devices", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "bandwidth provider","Aerohive Device(s) (Port)","Top Aerohive Devices (Aggregations/Ports) by Data Usage", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Bandwidth usage trend over time
	 * 
	 */
	public static void createBandwithUsageTrendOverTime(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("inbound 2.4 GHz BPS");
		yaxisList.add("outbound 5 GHz BPS");
		yaxisList.add("inbound 2.4 GHz BPS");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Inbound");
		displayNameList.add("Outbound");
		displayNameList.add("Total");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Inbound 2.4 GHz BPS] + [Inbound 5 GHz BPS]");
		displayValueList.add("[Outbound 2.4 GHz BPS] + [Outbound 5 GHz BPS]");
		displayValueList.add("[Outbound 2.4 GHz BPS] + [Outbound 5 GHz BPS] + [Inbound 2.4 GHz BPS] + [Inbound 5 GHz BPS]");
		
		
		List<String> displayValueKeyList = new ArrayList<String>();
		displayValueKeyList.add("[inbound 2.4 GHz BPS] + [inbound 5 GHz BPS]");
		displayValueKeyList.add("[outbound 2.4 GHz BPS] + [outbound 5 GHz BPS]");
		displayValueKeyList.add("[outbound 2.4 GHz BPS] + [outbound 5 GHz BPS] + [inbound 2.4 GHz BPS] + [inbound 5 GHz BPS]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		validateBreakdownList.add(true);
		validateBreakdownList.add(true);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		itemMap.add(1);
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(3,itemMap,yaxisList,displayNameList,displayValueList,displayValueKeyList,null,validateBreakdownList);
		createDeafultWidget("Bandwidth usage trend over time", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "time", "Time","Over Time",groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Distribution of client OS
	 */
	public static void createDistributionOfClientOs(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("total distinct client devices");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Clients");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Total Number of Client Devices]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Distribution of client OS", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "client device OS/type", "Client Type","Distribution by Client Device Type",groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Application page(Change me)
	 */
	public static void createApplicationInformation(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);

		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("application information", DashboardComponent.WIDGET_SPECIFY_TYPE_APP, 
				"1", "application information","Last Time","Application Information", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Time-series chart for usage over time (bandwidth in bps)
	 */
	public static void createTimeForApplicateion(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("inbound 2.4 GHz BPS");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Bandwidth in BPS");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Outbound 2.4 GHz BPS] + [Outbound 5 GHz BPS] + [Inbound 2.4 GHz BPS] + [Inbound 5 GHz BPS]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Time-series for usage over time", DashboardComponent.WIDGET_SPECIFY_TYPE_APP, 
				"1", "time for application", "Time","Over Time",groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Top N username view
	 */
	public static void createTopUsernamesView(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(false);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Top N username view", DashboardComponent.WIDGET_SPECIFY_TYPE_APP, 
				"1", "bandwidth user applied","User","Top Users by Data Usage", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * List of SSIDs where this application was seen - which ssids used the application
	 */
	public static void createSSIDsForApplication(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		yaxisList.add("top SSId");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		displayNameList.add("Top SSID");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		displayValueList.add("[Top SSID by Data Usage]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		validateBreakdownList.add(false);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(2);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("SSIDs for this application", DashboardComponent.WIDGET_SPECIFY_TYPE_APP, 
				"1", "application information","Last Time","Application Information", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * List of clients that accessed this application
	 */
	public static void createClientsForApplication(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("top client device");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Top Client Device MAC");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Top Client Device MAC Address by Data Usage]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(false);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Clients for this application", DashboardComponent.WIDGET_SPECIFY_TYPE_APP, 
				"1", "application information","Last Time","Application Information", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * List of APs that saw this application. For each AP, total usage, 
	 */
	public static void createApsForApplication(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		
		List<Short> levelBreakDownList = new ArrayList<Short>();
		levelBreakDownList.add((short)-1);
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(true);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,levelBreakDownList,validateBreakdownList);
		createDeafultWidget("List of APs for this application", DashboardComponent.WIDGET_SPECIFY_TYPE_APP, 
				"1", "application information","Last Time","Application Information", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * User page(change me)
	 */
	public static void createUserPage(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("1st time seen ever");
		yaxisList.add("User Profile");
		yaxisList.add("eMail");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("1st Time");
		displayNameList.add("User Profile");
		displayNameList.add("Email");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[1st Time This User Was Ever Seen]");
		displayValueList.add("[User Profile]");
		displayValueList.add("[Email]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(false);
		validateBreakdownList.add(false);
		validateBreakdownList.add(false);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(3);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("User page", DashboardComponent.WIDGET_SPECIFY_TYPE_USER, 
				"user information", "user information", "Comment","User Information",groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Top N applications accessed
	 */
	public static void createTopApplicationsAccessed(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("bytes");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Data Usage");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Byte Usage]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(false);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Top N applications accessed", DashboardComponent.WIDGET_SPECIFY_TYPE_USER, 
				"bandwidth application used", "bandwidth application used","Application","Top Used Applications by Data Usage", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Devices associated with user
	 */
	public static void createDevicesWithUser(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("OS/type");
		yaxisList.add("MAC");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Type");
		displayNameList.add("MAC");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Type]");
		displayValueList.add("[MAC Address]");
		
		List<Boolean> validateBreakdownList = new ArrayList<Boolean>();
		validateBreakdownList.add(false);
		validateBreakdownList.add(false);
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(2);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,validateBreakdownList);
		createDeafultWidget("Devices associated with user", DashboardComponent.WIDGET_SPECIFY_TYPE_USER, 
				"device used", "device used","Name","Devices Used", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * SSIDs associated with user
	 */
	public static void createSSIDsWithUser(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("authentication");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Authentication Method");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Authentication Method]");
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(1);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,null);
		createDeafultWidget("SSIDs associated with user", DashboardComponent.WIDGET_SPECIFY_TYPE_USER, 
				"SSId authenticated", "SSId authenticated", "SSID","SSIDs This User Was Authenticated",groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Client status (existing widget from current dashboard)
	 */
	public static void createClientStatus(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("clients - active");
		yaxisList.add("clients - maximum");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Active Clients");
		displayNameList.add("Maximum Clients");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Number of Currently Active Clients]");
		displayValueList.add("[Maximum Number of Clients]");
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(2);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,null);
		createDeafultWidget("Client status", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "system information","HiveManager(s) version","System Information", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	/**
	 * Device Status (existing widget from current dashboard)
	 */
	public static void createDeviceStatus(){
		List<String> yaxisList = new ArrayList<String>();
		yaxisList.add("network devices - up");
		yaxisList.add("network devices - down");
		yaxisList.add("network devices - unmanaged");
		yaxisList.add("network devices - alarmed");
		yaxisList.add("network devices - disconfigured");
		
		List<String> displayNameList = new ArrayList<String>();
		displayNameList.add("Aerohive Devices - Up");
		displayNameList.add("Aerohive Devices - Down");
		displayNameList.add("Aerohive Devices - New");
		displayNameList.add("Aerohive Devices - Alarmed");
		displayNameList.add("Aerohive Devices - Disconfigured");
		
		List<String> displayValueList = new ArrayList<String>();
		displayValueList.add("[Number of Aerohive Devices with Management Up]");
		displayValueList.add("[Number of Aerohive Devices with Management Down]");
		displayValueList.add("[Number of New Aerohive Devices]");
		displayValueList.add("[Number of Aerohive Devices with Alarm Conditions]");
		displayValueList.add("[Number of Aerohive Devices with Outdated Configurations]");
		
		List<Integer> itemMap = new ArrayList<Integer>();
		itemMap.add(5);
		
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = getGroupItemMap(1,itemMap,yaxisList,displayNameList,displayValueList,null,null,null);
		createDeafultWidget("Device Status", DashboardComponent.WIDGET_SPECIFY_TYPE_NONE, 
				"", "system information","HiveManager(s) version","System Information", groupItemMap, HmDomain.GLOBAL_DOMAIN);
	}
	
	
	
	/**
	 * 
	 * @param groupCount
	 * @param itemMap
	 * @param yaxisList
	 * @param displayNameList
	 * @param displayValueList
	 * @param displayValueKeyList
	 * @param levelBreakDownList
	 * @return
	 */
	public static HashMap<Integer, List<DashboardComponentData>>  getGroupItemMap(int groupCount,List<Integer> itemMap,List<String> yaxisList,
			List<String> displayNameList,List<String> displayValueList,List<String> displayValueKeyList,
			List<Short> levelBreakDownList,List<Boolean> validBreakDownList){
		HashMap<Integer, List<DashboardComponentData>> groupItemMap  = new HashMap<Integer, List<DashboardComponentData>>();
		int itemCount = -1;
		for(int i=0; i< groupCount;i++){
			List<DashboardComponentData> dataList = new ArrayList<DashboardComponentData>();
			for(int j=0;j<Integer.valueOf(itemMap.get(i));j++){
				itemCount ++;
				DashboardComponentData dcd = new DashboardComponentData();
				dcd.setGroupIndex(i);
				dcd.setPositionIndex(j);
				dcd.setSourceData(yaxisList.get(itemCount));
				
				if(null == displayNameList){
					dcd.setDisplayName(yaxisList.get(itemCount));
				}else{
					dcd.setDisplayName(displayNameList.get(itemCount));
				}
				
				if(null == displayValueList){
					dcd.setDisplayValue("[" + yaxisList.get(itemCount) +"]");
				}else{
					dcd.setDisplayValue(displayValueList.get(itemCount));
				}
				
				if(null == displayValueKeyList){
					dcd.setDisplayValueKey("[" + yaxisList.get(itemCount) +"]");
				}else{
					dcd.setDisplayValueKey(displayValueKeyList.get(itemCount));
				}
		
				if(null != validBreakDownList){
					if(validBreakDownList.get(itemCount)){
						dcd.setValidBreakdown(true);
						if(null != levelBreakDownList){
							if(levelBreakDownList.get(itemCount) != 0){
								dcd.setEnableBreakdown(true);
								dcd.setLevelBreakDown(levelBreakDownList.get(itemCount));
								dcd.setEnableDisplayTotal(true);
							}
						}
					}
				}
				
				dataList.add(dcd);
				
			}
			groupItemMap.put(i, dataList);
		}
		
		return groupItemMap;
	}
	

	/**
	 * 
	 * @param widgetName
	 * @param specifyType
	 * @param specifyName
	 * @param targetSelect
	 * @param groupItemMap
	 * @param domainName
	 * @return
	 */
	public static boolean createDeafultWidget(String widgetName,int specifyType,String specifyName,
			String targetSelect,String displayName,String displayValue,HashMap<Integer, List<DashboardComponentData>> groupItemMap,
			String domainName){
		if (null == widgetName || "".equals(widgetName.trim()) 
				|| null == domainName || "".equals(domainName)) {
			return false;
		}
		try {
			HmDomain domain = null;
			domain = QueryUtil.findBoByAttribute(
					HmDomain.class, "domainName", domainName);
			if(null == domain){
				return false;
			}
			List<?> existBos = QueryUtil.executeQuery(
					"select id from " + DashboardComponent.class.getSimpleName(), null,
					new FilterParams("ComponentName", widgetName), domain.getId());
			if (!existBos.isEmpty()) {
				//log.error("The default widget already exist.");
				return false;
			}
			DashboardComponent dc = new DashboardComponent();
			dc.setDefaultFlag(true);
			dc.setOwner(domain);
			dc.setComponentType(DashboardComponent.COMPONENT_TYPE_COUSTM);
			dc.setSpecifyType(specifyType);
			dc.setSpecifyName(specifyName);
//			dc.setDaComponent(null);
//			dc.setEnableExampleData(false);
//			dc.setDisplayName(displayName);
//			dc.setDisplayValue("["+displayValue+"]");
//			dc.setDisplayValueKey("["+targetSelect+"]");
			/*if(!getDataSource().getRealDaComponent().isEnabledHtml()) {
				dc.setCustomHtml("");
			} else {
				dc.setEnabledHtml(true);
				dc.setCustomHtml(getDataSource().getRealDaComponent().getCustomHtml());
			}*/
			Long metricId = createDefaultMetric(targetSelect,specifyType,groupItemMap,domain);
			
			if(null != metricId){
				dc.setComponentMetric(QueryUtil.findBoById(DashboardComponentMetric.class, metricId));
				dc.setComponentName(widgetName);
				QueryUtil.createBo(dc);
			}
		} catch (Exception e) {
			log.error(e);
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param targetSelect
	 * @param specifyType
	 * @param groupItemMap
	 * @param owner
	 * @return
	 */
	public static Long createDefaultMetric(String targetSelect,int specifyType,HashMap<Integer,  List<DashboardComponentData>> groupItemMap,HmDomain owner){
		if(null != groupItemMap && !groupItemMap.isEmpty()){
			List<DashboardComponentData> dataList = new ArrayList<DashboardComponentData>();
			for(int groupIndex : groupItemMap.keySet()){
				List<DashboardComponentData> itemList = groupItemMap.get(groupIndex);
				for(int j=0;j<itemList.size();j++){
					DashboardComponentData data = new DashboardComponentData();
					data.setSourceData(itemList.get(j).getSourceData());
					data.setDisplayName(itemList.get(j).getDisplayName());
					data.setDisplayValue(itemList.get(j).getDisplayValue());
					data.setDisplayValueKey(itemList.get(j).getDisplayValueKey());
					data.setValidBreakdown(itemList.get(j).isValidBreakdown());
					data.setEnableBreakdown(itemList.get(j).isEnableBreakdown());
					
					if(itemList.get(j).isEnableBreakdown()){
						data.setLevelBreakDown(itemList.get(j).getLevelBreakDown());
						data.setEnableDisplayTotal(itemList.get(j).isEnableDisplayTotal());
					}
					
					data.setPositionIndex(j);
					data.setGroupIndex(groupIndex);
					dataList.add(data);
				}
			}
			
			if(!dataList.isEmpty()){
				DashboardComponentMetric metric = new DashboardComponentMetric();
				metric.setComponentData(dataList);
				metric.setOwner(owner);
				metric.setSourceType(targetSelect);
				metric.setSpecifyType(specifyType);
				try {
					return QueryUtil.createBo(metric);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
}
