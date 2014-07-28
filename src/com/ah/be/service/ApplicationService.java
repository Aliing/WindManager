package com.ah.be.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.CustomApplication;
import com.ah.bo.performance.AhAppFlowDay;
import com.ah.bo.performance.AhAppFlowMonth;

public class ApplicationService implements QueryBo {
	
	public static final String SELECTED_APPLICATION_LIST = "selectedAppList";
	
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof ApplicationProfile) {
			ApplicationProfile profile = (ApplicationProfile) bo;
			if (profile.getApplicationList() != null) {
				profile.getApplicationList().size();
			}
			if (profile.getCustomApplicationList() != null) {
				profile.getCustomApplicationList().size();
				for (CustomApplication ca : profile.getCustomApplicationList()) {
					if (ca.getRules() != null) {
						ca.getRules().size();
					}
				}
 			}
		}
		return null;
	}
	
	public List<CustomApplication> getCustomApplicationWithBytes(HmDomain owner) {
		List<CustomApplication> caList = QueryUtil.executeQuery(CustomApplication.class, new SortParams("customAppName"), 
				new FilterParams("deletedFlag = :s1 and owner.id = :s2", new Object[] {false, owner.getId()}));
		Map<Integer, Long> dayMap = new HashMap<Integer, Long>();
		Map<Integer, Long> monthMap = new HashMap<Integer, Long>();
		this.setAppFlowData(dayMap, monthMap, owner);
		for (CustomApplication ca : caList) {
			if (dayMap.get(ca.getAppCode()) != null) {
				ca.setLastDayUsage(dayMap.get(ca.getAppCode()));
			}
			if (monthMap.get(ca.getAppCode()) != null) {
				ca.setLastMonthUsage(monthMap.get(ca.getAppCode()));
			}
		}
		//sortAppFlowList(fixedAppList);
		return caList;
	}
	
	public List<Application> getApplicationWithBytes(HmDomain owner) {
		List<Application> fixedAppList = QueryUtil.executeQuery(Application.class, new SortParams("appName"), 
				new FilterParams("appCode > :s1", new Object[] {0}));
		Map<Integer, Long> dayMap = new HashMap<Integer, Long>();
		Map<Integer, Long> monthMap = new HashMap<Integer, Long>();
		this.setAppFlowData(dayMap, monthMap, owner);
		for (Application app : fixedAppList) {
			if (dayMap.get(app.getAppCode()) != null) {
				app.setLastDayUsage(dayMap.get(app.getAppCode()));
			}
			if (monthMap.get(app.getAppCode()) != null) {
				app.setLastMonthUsage(monthMap.get(app.getAppCode()));
			}
		}
		sortAppFlowList(fixedAppList);
		return fixedAppList;
	}
	
	private void setAppFlowData(Map<Integer, Long> dayMap, Map<Integer, Long> monthMap, HmDomain owner) {
		Calendar c = Calendar.getInstance();
		long endTime = c.getTimeInMillis();
		c.add(Calendar.DATE, -1);
		long startTime = c.getTimeInMillis();
		c.add(Calendar.DATE, -29);
		long monthStartTime = c.getTimeInMillis();
	
		List<AhAppFlowDay> appFlowList = QueryUtil.executeQuery(AhAppFlowDay.class, null, 
				new FilterParams("owner.id = :s1 and createdAt >= :s2 and createdAt < :s3", new Object[] {owner.getId(), startTime, endTime}));
		if (appFlowList != null) {
			for (AhAppFlowDay bean : appFlowList) {
				dayMap.put(bean.getAppCode(), bean.getByteNum());
			}
		}
		
		List<AhAppFlowMonth> appFlowMonthList = QueryUtil.executeQuery(AhAppFlowMonth.class, null, 
				new FilterParams("owner.id = :s1", new Object[] {owner.getId()}));
		if (appFlowMonthList != null) {
			for (AhAppFlowMonth bean : appFlowMonthList) {
				monthMap.put(bean.getAppCode(), bean.getByteNum());
			}
		}					
	}
	
	private void sortAppFlowList(List<Application> appList) {
		Collections.sort(appList, new Comparator<Application>() {
			public int compare(Application app1, Application app2) {
				return (int) (app2.getLastDayUsage() - app1.getLastDayUsage());
			}
		});
	}
	
	public Map<String, List<Application>> initApplicationMap(HmDomain owner) {
		Map<String, List<Application>> map = new HashMap<String, List<Application>>();
		List<Application> selectedAppList = new ArrayList<Application>();
		List<Application> unSelectedAppList = new ArrayList<Application>();
		map.put("selectedAppList", selectedAppList);
		map.put("unSelectedAppList", unSelectedAppList);
		
		Map<Integer, Long> dayMap = new HashMap<Integer, Long>();
		Map<Integer, Long> monthMap = new HashMap<Integer, Long>();
		this.setAppFlowData(dayMap, monthMap, owner);
		
		List<Application> fixedAppList = QueryUtil.executeQuery(Application.class, new SortParams("appName"), 
				new FilterParams("appCode > :s1", new Object[] {0}));
		List<ApplicationProfile> list = QueryUtil.executeQuery(ApplicationProfile.class, null, 
				new FilterParams("owner.id = :s1", new Object[] {owner.getId()}), owner.getId(), this);
		ApplicationProfile profile = (list != null && list.size() > 0) ? list.get(0) : null;

		for (Application app : fixedAppList) {
			if (dayMap.get(app.getAppCode()) != null) {
				app.setLastDayUsage(dayMap.get(app.getAppCode()));
			}
			if (monthMap.get(app.getAppCode()) != null) {
				app.setLastMonthUsage(monthMap.get(app.getAppCode()));
			}
			if (profile != null && profile.getApplicationList() != null && profile.getApplicationList().contains(app)) {
				selectedAppList.add(app);
			} else {
				unSelectedAppList.add(app);
			}	
		}
		sortAppFlowList(unSelectedAppList);
		return map;
	}
	
	public Map<String, List<CustomApplication>> initCustomApplicationMap(HmDomain owner) {
		Map<String, List<CustomApplication>> map = new HashMap<String, List<CustomApplication>>();
		List<CustomApplication> selectedCustomAppList = new ArrayList<CustomApplication>();
		List<CustomApplication> unSelectedCustomAppList = new ArrayList<CustomApplication>();
		map.put("selectedCustomAppList", selectedCustomAppList);
		map.put("unSelectedCustomAppList", unSelectedCustomAppList);
		
		Map<Integer, Long> dayMap = new HashMap<Integer, Long>();
		Map<Integer, Long> monthMap = new HashMap<Integer, Long>();
		this.setAppFlowData(dayMap, monthMap, owner);
		
		List<CustomApplication> fixedAppList = QueryUtil.executeQuery(CustomApplication.class, new SortParams("customAppName",true), 
				new FilterParams("deletedFlag = :s1 and owner.id = :s2", new Object[] {false, owner.getId()}));
		List<ApplicationProfile> list = QueryUtil.executeQuery(ApplicationProfile.class, null, 
				new FilterParams("owner.id = :s1", new Object[] {owner.getId()}), owner.getId(), this);
		ApplicationProfile profile = (list != null && list.size() > 0) ? list.get(0) : null;
		if(null != fixedAppList && !fixedAppList.isEmpty()){
			for (CustomApplication app : fixedAppList) {
				if (dayMap.get(app.getAppCode()) != null) {
					app.setLastDayUsage(dayMap.get(app.getAppCode()));
				}
				if (monthMap.get(app.getAppCode()) != null) {
					app.setLastMonthUsage(monthMap.get(app.getAppCode()));
				}
				if (profile != null && profile.getCustomApplicationList() != null && profile.getCustomApplicationList().contains(app)) {
					selectedCustomAppList.add(app);
				} else {
					unSelectedCustomAppList.add(app);
				}	
			}
		}
		return map;
	}
	
	
	public static void main(String[] args) {
		ApplicationService service = new ApplicationService();
		System.out.println(System.currentTimeMillis());

	}

}
