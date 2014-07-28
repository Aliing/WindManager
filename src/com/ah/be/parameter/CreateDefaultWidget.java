package com.ah.be.parameter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.Ostermiller.util.CSVParser;
import com.ah.be.common.AhDirTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class CreateDefaultWidget {
	private static final Tracer	log	= new Tracer(CreateDefaultWidget.class.getSimpleName());
	
	public static void insertDefaultWidget() {
		List<DashboardComponentMetric> lstMetric = new ArrayList<DashboardComponentMetric>();
		List<DashboardComponent>	listComponent = new ArrayList<DashboardComponent>();
		List<DashboardComponentMetric> lstMetricUpdate = new ArrayList<DashboardComponentMetric>();
		List<DashboardComponent>	listComponentUpdate = new ArrayList<DashboardComponent>();
		CSVParser shredder=null;
		try {
			String uploadFileName = AhDirTools.getConstantConfigDir() + "defaultWidgetForImport.csv";
			if (null != uploadFileName) {
			
				log.info("\n====================Begin Create widget====================\n\n");
	
				// get the data from file
				shredder = new CSVParser(
						new InputStreamReader(new FileInputStream(uploadFileName))
				);
				FilterParams filterParams = new FilterParams("domainName", HmDomain.GLOBAL_DOMAIN);
				List<HmDomain> list = QueryUtil.executeQuery(HmDomain.class, null, filterParams);
				HmDomain globalDomain = list.get(0);
				
				//shredder.setCommentStart("#*//");
				String[][] allvalue = shredder.getAllValues();
				if (null == allvalue || allvalue.length == 0) {
					log.info("\n====================file is empty====================\n\n");
					log.info("\n====================End Create widget====================\n\n");
					return;
				} else {
					DashboardComponent comp = null;
					DashboardComponentMetric metric =null;
					lineValue:
					for (String[] value : allvalue) {
						if (!checkTheLineValue(value)) {
							continue;
						}
						
						int ci=0;
						
						//need create widget
						boolean isCreateWidget = cBln(value[ci++]);
						// key
						int key = cInt(value[ci++]);
						
						List<DashboardComponentMetric> mlst = QueryUtil.executeQuery(DashboardComponentMetric.class, null,
								new FilterParams("key=:s1 and defaultFlag=:s2", new Object[]{key,true}), null,
								new ImplQueryBo());
						if (mlst.isEmpty()) {
							metric= new DashboardComponentMetric();
							metric.setDefaultFlag(true);
							metric.setOwner(globalDomain);
							lstMetric.add(metric);
						} else {
							metric = mlst.get(0);
							metric.getComponentData().clear();
							lstMetricUpdate.add(metric);
						}
						
						if (isCreateWidget) {
							List<DashboardComponent> clst = QueryUtil.executeQuery(DashboardComponent.class, null,
									new FilterParams("key=:s1 and defaultFlag=:s2", new Object[]{key,true}), null,
									new ImplQueryBo());
							if (clst.isEmpty()) {
								comp = new DashboardComponent();
								comp.setDefaultFlag(true);
								comp.setOwner(globalDomain);
								comp.setComponentType(DashboardComponent.COMPONENT_TYPE_METRIC);
								comp.setComponentMetric(metric);
								listComponent.add(comp);
							} else {
								comp = clst.get(0);
								comp.setComponentMetric(metric);
								listComponentUpdate.add(comp);
							}
						}
						
						metric.setKey(key);
						//metricName
						String metricName = cStr(value[ci++]);
						metric.setMetricName(metricName);
						
						//filterDataType
						int filterDataType = cInt(value[ci++]);
						metric.setFilterDataType(filterDataType);
						
						metric.setCreateWidget(isCreateWidget);

						//componentGroup
						int componentGroup = cInt(value[ci++]);
						metric.setComponentGroup(componentGroup);
						//specifyType
						int specifyType = cInt(value[ci++]);
						metric.setSpecifyType(specifyType);
						//chartType
						String chartType = cStr(value[ci++]);
						metric.setChartType(chartType);
						//chartType
						boolean chartInverted = cBln(value[ci++]);
						metric.setChartInverted(chartInverted);
						//blnOverTime
						boolean blnOverTime = cBln(value[ci++]);
						metric.setBlnOverTime(blnOverTime);
						//valueRange
						String valueRange = cStr(value[ci++]);
						metric.setValueRange(valueRange);
						// homeonly
						boolean homeonly = cBln(value[ci++]);
						metric.setHomeonly(homeonly);
						// sourceType
						String sourceType = cStr(value[ci++]);
						metric.setSourceType(sourceType);
						
						if (isCreateWidget) {
							comp.setKey(key);
							comp.setComponentName(metricName);
							comp.setComponentGroup(componentGroup);
							comp.setSpecifyType(specifyType);
							comp.setHomeonly(homeonly);
							comp.setSourceType(sourceType);
						} 
						
						// displayName
						String displayName = cStr(value[ci++]);
						metric.setDisplayName(displayName);
						// displayValue
						String displayValue = cStr(value[ci++]);
						metric.setDisplayValue(displayValue);
						// displayValueKey
						String displayValueKey = cStr(value[ci++]);
						metric.setDisplayValueKey(displayValueKey);
						// orderByMetric
						String orderByMetric = cStr(value[ci++]);
						metric.setOrderByMetric(orderByMetric);
						//orderByAsc
						boolean orderByAsc = cBln(value[ci++]);
						metric.setOrderByDesc(orderByAsc);
						//groupby
						int groupBy = cInt(value[ci++]);
						metric.setGroupBy(groupBy);
						//topNumber
						int topNumber = cInt(value[ci++]);
						metric.setTopNumber(topNumber);
						//drillDownType && drillDownValue
						String drillDownMetric = cStr(value[ci++]);
						
						if (!drillDownMetric.equals("")){
							String drillDownType = drillDownMetric.substring(0, 2);
							String drillDownValue = drillDownMetric.substring(2);
							metric.setDrillDownType(drillDownType);
							metric.setDrillDownValue(drillDownValue==null?"":drillDownValue);
						} else {
							metric.setDrillDownType("");
							metric.setDrillDownValue("");
						}
						
						for (int i = ci; i < value.length-1; i=i+10) {
							// sourceData
							String sourceData = cStr(value[i]);
							if ("".equals(sourceData)) {
								continue lineValue;
							}
							DashboardComponentData dcd = new DashboardComponentData();
							dcd.setSourceData(sourceData);
							// displayName1
							String displayName1 = cStr(value[i+1]);
							dcd.setDisplayName(displayName1);
							// displayValue1
							String displayValue1 = cStr(value[i+2]);
							dcd.setDisplayValue(displayValue1);
							// displayValueKey
							String displayValueKey1 = cStr(value[i+3]);
							dcd.setDisplayValueKey(displayValueKey1);
							// enableBreakdown
							boolean enableBreakdown = cBln(value[i+4]);
							dcd.setEnableBreakdown(enableBreakdown);
							//levelBreakDown
							short levelBreakDown = cSht(value[i+5]);
							dcd.setLevelBreakDown(levelBreakDown);
							//enableDisplayTotal
							boolean enableDisplayTotal = cBln(value[i+6]);
							dcd.setEnableDisplayTotal(enableDisplayTotal);
							
							//groupIndex
							int groupIndex = cInt(value[i+7]);
							dcd.setGroupIndex(groupIndex);
							//positionIndex
							int positionIndex = cInt(value[i+8]);
							dcd.setPositionIndex(positionIndex);
							
							//drillDownType && drillDownValue
							String drillDownStr = cStr(value[i+9]);
							
							if (!drillDownStr.equals("")){
								String drillDownType = drillDownStr.substring(0, 2);
								String drillDownValue = drillDownStr.substring(2);
								dcd.setDrillDownType(drillDownType);
								dcd.setDrillDownValue(drillDownValue==null?"":drillDownValue);
							} else {
								dcd.setDrillDownType("");
								dcd.setDrillDownValue("");
							}

							metric.getComponentData().add(dcd);
							 
						}
					}
				}
				log.info("\n====================begin insert to DB====================\n\n");
				if (!lstMetric.isEmpty()) {
					QueryUtil.bulkCreateBos(lstMetric);
				}
				if (!lstMetricUpdate.isEmpty()) {
					QueryUtil.bulkUpdateBos(lstMetricUpdate);
				}
				if (!listComponentUpdate.isEmpty()) {
					QueryUtil.bulkUpdateBos(listComponentUpdate);
				}
				if (!listComponent.isEmpty()) {
					QueryUtil.bulkCreateBos(listComponent);
				}
				log.info("\n====================End insert to DB====================\n\n");
				
				log.info("\n====================End Create widget====================\n\n");
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (shredder!=null) {
				try {
					shredder.close();
					shredder=null;
				} catch (IOException e) {
					shredder=null;
					log.error(e);
				}
			}
		}
	}
	
	static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof DashboardComponentMetric) {
				DashboardComponentMetric profile = (DashboardComponentMetric) bo;
				if (profile.getComponentData() != null) {
					profile.getComponentData().size();
				}
			}

			if(bo instanceof DashboardComponent) {
				DashboardComponent profile = (DashboardComponent)bo;

				if(profile.getComponentMetric() != null) {
					profile.getComponentMetric().getId();
				}
			}

			return null;
		}
	}
		
	private static boolean cBln(String obj) {
		if (obj==null || obj.trim().equals("")){
			return false;
		}
		if (!obj.toLowerCase().startsWith("t")) {
			return false;
		}
		return true;
	}
	
	private static String cStr(String obj) {
		if (obj==null || obj.trim().equals("")){
			return "";
		}
		return obj.trim();
	}
	
	private static int cInt(String obj) {
		if (obj==null || obj.trim().equals("")){
			return 0;
		}
		return Integer.parseInt(obj);
	}
	
	private static short cSht(String obj) {
		if (obj==null || obj.trim().equals("")){
			return 0;
		}
		return Short.parseShort(obj);
	}
	
	private static boolean checkTheLineValue (String[] arg_Value) {
		boolean boolResult = true;
		int length = arg_Value.length;
		if (length == 0) {
			boolResult = false;
		}
		if (arg_Value[0].startsWith("*") || arg_Value[0].startsWith("#")) {
			boolResult = false;
		}
		if (arg_Value[0].startsWith("//")) {
			boolResult = false;
		}
		// ignore blank line
		if(StringUtils.strip(arg_Value[0]).isEmpty()&&StringUtils.strip(arg_Value[length-1]).isEmpty())
			if(StringUtils.isBlank(StringUtils.join(arg_Value))){
				boolResult=false;
			}
		return boolResult;
	}
	
	
}
