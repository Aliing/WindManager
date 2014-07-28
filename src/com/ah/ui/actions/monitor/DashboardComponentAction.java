package com.ah.ui.actions.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.dashboard.DashboardComponentMetric;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.TextItem;

public class DashboardComponentAction extends BaseAction implements QueryBo{
	
	private static final long serialVersionUID = 1L;

	@Override
	public Collection<HmBo> load(HmBo bo) {
		// TODO Auto-generated method stub
		return null;
	}
	
//	@Override
//	public String execute() throws Exception{
//		try {
//			if ("new".equals(operation)) {
//				setSessionDataSource(new DashboardComponent());
//				getDataSource().setOwner(getDomain());
//				getDataSource().setComponentType(componentType);
//				getDataSource().setSpecifyType(specifyType);
//				getDataSource().setSpecifyName(specifyName);
//				if (getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_WIDGET)) {
//					getDataSource().setDaComponent(findBoById(DashboardComponent.class, preWidgetId, this));
//					getDataSource().setSourceType("");
//					if(!getDataSource().getDaComponent().isEnabledHtml()) {
//						getDataSource().setCustomHtml("");
//					}
//					getDataSource().setComponentMetric(null);
//				} else if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_METRIC)) {
//					disableForMetric = true;
//					getDataSource().setDaComponent(null);
//					getDataSource().setSourceType(sourceType);
//					if(!getDataSource().getRealDaComponent().isEnabledHtml()) {
//						getDataSource().setCustomHtml("");
//					}
//					getDataSource().setComponentMetric(findBoById(DashboardComponentMetric.class, preMetricId,this));
//					getDataSource().setDisplayName(displayName);
//					getDataSource().setDisplayValue(displayValue);
//				} else if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_COUSTM)) {
//					getDataSource().setDaComponent(null);
//					getDataSource().setSourceType("");
//					if(!getDataSource().getRealDaComponent().isEnabledHtml()) {
//						getDataSource().setCustomHtml("");
//					}
//					getDataSource().setComponentMetric(null);
////				} else if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_HTML)) {
////					getDataSource().setDaComponent(null);
////					getDataSource().setSourceType("");
////					getDataSource().setComponentMetric(null);
//				}
//				prepareDependObject();
//				return INPUT;
//			} else if ("create".equals(operation)) {
//				jsonObject = new JSONObject();
//				try {
//					if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_WIDGET)){
//						Long metricId = saveDependMetric(getDataSource().getDaComponent().getComponentMetric()==null?
//								new DashboardComponentMetric():getDataSource().getDaComponent().getComponentMetric());
//						if(null != metricId){
//							getDataSource().getDaComponent().setComponentMetric(QueryUtil.findBoById(DashboardComponentMetric.class,metricId));
//							if(!getDataSource().getRealDaComponent().isEnabledHtml()){
//								getDataSource().getDaComponent().setEnabledHtml(false);
//								getDataSource().getDaComponent().setCustomHtml("");
//							} else {
//								getDataSource().getDaComponent().setEnabledHtml(true);
//								getDataSource().getDaComponent().setCustomHtml(getDataSource().getCustomHtml());
//							}
//							QueryUtil.updateBo(getDataSource().getDaComponent());
//						}
//					} else {
//						if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_METRIC)){
//							if(!getDataSource().getRealDaComponent().getComponentMetric().isDefaultFlag()){
//								saveDependMetric(getDataSource().getComponentMetric());
//							}
//						}else{
//							Long metricId = saveDependMetric(new DashboardComponentMetric());
//							if(null != metricId){
//								getDataSource().setComponentMetric(QueryUtil.findBoById(DashboardComponentMetric.class,metricId));
//							}
//						}
//						
//						if(!getDataSource().getRealDaComponent().isEnabledHtml()){
//							getDataSource().getRealDaComponent().setCustomHtml("");
//						}
//					}
//					
//					id = QueryUtil.createBo(getDataSource());
//					jsonObject.put("succFlag", true);
//					jsonObject.put("id", id);
//				} catch (Exception e) {
//					jsonObject.put("succFlag", false);
//					jsonObject.put("msg", e.getMessage());
//				}
//				return "json";
//			} else if ("edit".equals(operation)) {
//				DashboardComponent dc = findBoById(DashboardComponent.class, widgetId, this);
//				if(isBlnCloneWidget()) {
//					dc.setId(null);
//					dc.setComponentName(null);
//					dc.setOwner(getDomain());
//					dc.setDefaultFlag(false);
//				}
//				dc.setComponentType(componentType);
//				if (dc.getComponentType().equals(DashboardComponent.COMPONENT_TYPE_WIDGET)) {
//					dc.setDaComponent(findBoById(DashboardComponent.class, preWidgetId, this));
//					dc.setSourceType("");
//					if(!dc.getRealDaComponent().isEnabledHtml()) {
//						dc.getRealDaComponent().setCustomHtml("");
//					}
//					dc.setComponentMetric(null);
//				} else if(dc.getComponentType().equals(DashboardComponent.COMPONENT_TYPE_METRIC)) {
//					dc.setDaComponent(null);
//					if(!dc.getSourceType().equals(sourceType)){
//						dc.setDisplayName(displayName);
//						dc.setDisplayValue(displayValue);
//					}
//					dc.setSourceType(sourceType);
//					if(!dc.getRealDaComponent().isEnabledHtml()) {
//						dc.getRealDaComponent().setCustomHtml("");
//					}
//					dc.setComponentMetric(findBoById(DashboardComponentMetric.class, preMetricId, this));
//				} else if(dc.getComponentType().equals(DashboardComponent.COMPONENT_TYPE_COUSTM)) {
//					dc.setDaComponent(null);
//					dc.setSourceType("");
//					if(!dc.getRealDaComponent().isEnabledHtml()) {
//						dc.getRealDaComponent().setCustomHtml("");
//					}
//					if(dc.getComponentMetric()!=null) {
//						DashboardComponentMetric metric = findBoById(DashboardComponentMetric.class,dc.getComponentMetric().getId(), this);
//						if(metric.getMetricName()!=null &&  !metric.getMetricName().equals("")) {
//							dc.setComponentMetric(null);
//						} else {
//							if(dc.getSpecifyType() != specifyType){
//								dc.setComponentMetric(null);
//								dc.setDisplayName(null);
//								dc.setDisplayValue(null);
//							}else{
//								dc.setComponentMetric(metric);
//							}
//						}
//					}
////				} else if(dc.getComponentType().equals(DashboardComponent.COMPONENT_TYPE_HTML)) {
////					dc.setDaComponent(null);
////					dc.setSourceType("");
////					dc.setComponentMetric(null);
//				}
//				dc.setSpecifyType(specifyType);
//				dc.setSpecifyName(specifyName);
//				setSessionDataSource(dc);
//				prepareDependObject();
//				return INPUT;
//			} else if ("update".equals(operation)) {
//				jsonObject = new JSONObject();
//				try {
//					if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_WIDGET)){
//						Long metricId = saveDependMetric(getDataSource().getDaComponent().getComponentMetric()==null?
//								new DashboardComponentMetric():getDataSource().getDaComponent().getComponentMetric());
//						if(null != metricId){
//							getDataSource().getDaComponent().setComponentMetric(QueryUtil.findBoById(DashboardComponentMetric.class, metricId));
//							if(!getDataSource().getRealDaComponent().isEnabledHtml()){
//								getDataSource().getDaComponent().setEnabledHtml(false);
//								getDataSource().getDaComponent().setCustomHtml("");
//							} else {
//								getDataSource().getDaComponent().setEnabledHtml(true);
//								getDataSource().getDaComponent().setCustomHtml(getDataSource().getCustomHtml());
//							}
//							QueryUtil.updateBo(getDataSource().getDaComponent());
//						}
//					} else {
//						if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_METRIC)){
//							if(!getDataSource().getRealDaComponent().getComponentMetric().isDefaultFlag()){
//								saveDependMetric(getDataSource().getComponentMetric());
//							}
//						} else{
//							Long metricId = saveDependMetric(getDataSource().getComponentMetric() == null ?
//									new DashboardComponentMetric() : getDataSource().getComponentMetric());
//							if(null != metricId){
//								getDataSource().setComponentMetric(QueryUtil.findBoById(DashboardComponentMetric.class, metricId));
//							}
//						}
//						if(!getDataSource().getRealDaComponent().isEnabledHtml()){
//							getDataSource().getRealDaComponent().setCustomHtml("");
//						}
//					}
//					id=QueryUtil.updateBo(dataSource).getId();
//					jsonObject.put("succFlag", true);
//					jsonObject.put("id", id);
//				} catch (Exception e) {
//					jsonObject.put("succFlag", false);
//					jsonObject.put("id", e.getMessage());
//				}
//				return "json";
//			} else if ("cancel".equals(operation)) {
//				return SUCCESS;
//			} else if("savePremadeWidget".equals(operation)){
//				jsonObject = new JSONObject();
//				if (null == widgetName || "".equals(widgetName.trim())) {
//					return "json";
//				}
//				try {
//					List<?> existBos = QueryUtil.executeQuery(
//							"select id from " + DashboardComponent.class.getSimpleName(), null,
//							new FilterParams("ComponentName", widgetName), getDomain().getId());
//					if (!existBos.isEmpty()) {
//						jsonObject.put("succFlag", false);
//						jsonObject.put("msg", "The widget name already exist.");
//						return "json";
//					}
//					DashboardComponent dc = new DashboardComponent();
//					//TODO
////					if(widgetName.startsWith("def")){
////						dc.setDefaultFlg(true);
////					}
//					dc.setOwner(getDomain());
//					dc.setComponentType(DashboardComponent.COMPONENT_TYPE_COUSTM);
//					dc.setDaComponent(null);
//					//dc.setSourceType(xDataSelect);
//					dc.setEnableExampleData(getDataSource().getRealDaComponent().isEnableExampleData());
//					//dc.setEnableTimeLocal(getDataSource().getRealDaComponent().isEnableTimeLocal());
//					dc.setDisplayName(getDataSource().getRealDaComponent().getDisplayName());
//					dc.setDisplayValue(getDataSource().getRealDaComponent().getDisplayValue());
//					dc.setDisplayValueKey(getDataSource().getRealDaComponent().getDisplayValueKey());
//					dc.setSpecifyType(specifyType);
//					if(!getDataSource().getRealDaComponent().isEnabledHtml()) {
//						dc.setCustomHtml("");
//					} else {
//						dc.setEnabledHtml(true);
//						dc.setCustomHtml(getDataSource().getRealDaComponent().getCustomHtml());
//					}
//					Long metricId = saveDependMetric(new DashboardComponentMetric());
//					if(null != metricId){
//						dc.setComponentMetric(QueryUtil.findBoById(DashboardComponentMetric.class, metricId));
//						dc.setComponentName(widgetName);
//						Long createId = QueryUtil.createBo(dc);
//						jsonObject.put("succFlag", true);
//						jsonObject.put("id", createId);
//						jsonObject.put("name", widgetName);
//						jsonObject.put("msg", "The widget has been created.");
//					}else{
//						jsonObject.put("succFlag", false);
//						jsonObject.put("msg", "The widget created error.");
//					}
//				} catch (Exception e) {
//					jsonObject.put("succFlag", false);
//					jsonObject.put("msg", e.getMessage());
//				}
//				return "json";
//			}else if("savePremadeMetric".equals(operation)){
//				jsonObject = new JSONObject();
//				if (null == metricName || "".equals(metricName.trim())) {
//					return "json";
//				}
//				try {
//					List<?> existBos = QueryUtil.executeQuery(
//							"select id from " + DashboardComponentMetric.class.getSimpleName(), null,
//							new FilterParams("metricName", metricName), getDomain().getId());
//					if (!existBos.isEmpty()) {
//						jsonObject.put("succFlag", false);
//						jsonObject.put("msg", "The metric name already exist.");
//						return "json";
//					}
//					Long metricId = saveDependMetric(new DashboardComponentMetric());
//					if(null != metricId){
//						jsonObject.put("succFlag", true);
//						jsonObject.put("id", metricId);
//						jsonObject.put("name", metricName);
//						jsonObject.put("msg", "The metric has been created.");
//					}else{
//						jsonObject.put("succFlag", false);
//						jsonObject.put("msg", "The metric created error.");
//					}
//				} catch (Exception e) {
//					jsonObject.put("succFlag", false);
//					jsonObject.put("msg", e.getMessage());
//				}
//				return "json";
//			} else if ("fetchCuHtmlPage".equals(operation)) {
//				jsonObject = new JSONObject();
//				try {
//					if(!getUserContext().isSuperUser()) {
//						jsonObject.put("t", false);
//					} else {
//						jsonObject.put("v", getDataSource().getRealDaComponent().getCustomHtml());
//						jsonObject.put("t", true);
//					}
//				} catch (Exception e) {
//					jsonObject.put("t", false);
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
//			} else if ("saveCustomHtmlPanel".equals(operation)) {
//				jsonObject = new JSONObject();
//				try {
//					if(textCuHtml!=null && textCuHtml.length()>5120) {
//						jsonObject.put("t", false);
//						jsonObject.put("m", "The maximum length of HTML characters is 5120.");
//					} else {
//						getDataSource().setCustomHtml(textCuHtml);
//						jsonObject.put("t", true);
//					}
//				} catch (Exception e) {
//					jsonObject.put("t", false);
//					jsonObject.put("m", e.getMessage());
//				}
//				return "json";
////			} else if("saveCuHtmlPanel".equals(operation)) {
////				jsonObject = new JSONObject();
////				try {
////					if(textCuHtml!=null) {
////						String tempHtmp = textCuHtml.trim().toLowerCase();
////						if(tempHtmp.contains("<script") || tempHtmp.contains("<%@include") || tempHtmp.contains("<%@page")){
////							jsonObject.put("t", false);
////							jsonObject.put("m", "cannot include word script.");
////							return "json";
////						}
////					}
////					Long createId;
////					if(getDataSource().getId()==null) {
////						if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_WIDGET)) {
////							getDataSource().getDaComponent().setCustomHtml(textCuHtml);
////							QueryUtil.updateBo(getDataSource().getDaComponent());
////							createId = QueryUtil.createBo(dataSource);
////						} else {
////							getDataSource().setCustomHtml(textCuHtml);
////							createId = QueryUtil.createBo(dataSource);
////						}
////					} else {
////						if(getDataSource().getComponentType().equals(DashboardComponent.COMPONENT_TYPE_WIDGET)) {
////							getDataSource().getDaComponent().setCustomHtml(textCuHtml);
////							QueryUtil.updateBo(getDataSource().getDaComponent());
////							createId = QueryUtil.updateBo(dataSource).getId();
////						} else {
////							getDataSource().setCustomHtml(textCuHtml);
////							createId = QueryUtil.updateBo(dataSource).getId();
////						}
////					}
////					
////					jsonObject.put("t", true);
////					jsonObject.put("v", createId);
////				} catch (Exception e) {
////					jsonObject.put("t", false);
////					jsonObject.put("m", e.getMessage());
////				}
////				return "json";
////			} else if("saveAsCuHtmlPanel".equals(operation)) {
////				jsonObject = new JSONObject();
////				try {
////					List<?> existBos = QueryUtil.executeQuery(
////							"select id from " + DashboardComponent.class.getSimpleName(), null,
////							new FilterParams("ComponentName", widgetName), domainId);
////					if (!existBos.isEmpty()) {
////						jsonObject.put("t", false);
////						jsonObject.put("m", "The widget name already exist.");
////						return "json";
////					}
////					if(textCuHtml!=null) {
////						String tempHtmp = textCuHtml.trim().toLowerCase();
////						if(tempHtmp.contains("<script") || tempHtmp.contains("<%@include") || tempHtmp.contains("<%@page")){
////							jsonObject.put("t", false);
////							jsonObject.put("m", "cannot include word script.");
////							return "json";
////						}
////					}
////					DashboardComponent dc = new DashboardComponent();
////					dc.setOwner(getDomain());
////					dc.setComponentType(DashboardComponent.COMPONENT_TYPE_HTML);
////					dc.setDaComponent(null);
////					dc.setSourceType("");
////					dc.setComponentMetric(null);
////					dc.setCustomHtml(textCuHtml);
////					dc.setComponentName(widgetName);
////					Long createId = QueryUtil.createBo(dc);
////					jsonObject.put("t", true);
////					jsonObject.put("va", createId);
////					jsonObject.put("te", widgetName);
////					jsonObject.put("m", "The widget has been created.");
////				} catch (Exception e) {
////					jsonObject.put("t", false);
////					jsonObject.put("m", e.getMessage());
////				}
////				return "json";
//			}else {
//				return INPUT;
//			}
//		} catch (Exception e) {
//			return prepareActionError(e);
//		}
//	}
//	
//	@Override
//	public void prepare() throws Exception {
//		super.prepare();
//		setDataSource(DashboardComponent.class);
//		//setSelectedL2Feature(L2_FEATURE_DASHBOARD);
//	}
//
//	@Override
//	public DashboardComponent getDataSource() {
//		return (DashboardComponent) dataSource;
//	}
//
//	@Override
//	public Collection<HmBo> load(HmBo bo) {
//		if(null != bo){
//			if(bo instanceof DashboardComponent){
//				DashboardComponent dcComponent = (DashboardComponent)bo;
//				if(dcComponent.getDaComponent()!=null) {
//					dcComponent.getDaComponent().getId();
//					if(null != dcComponent.getDaComponent().getComponentMetric()){
//						dcComponent.getDaComponent().getComponentMetric().getId();
//						if (dcComponent.getDaComponent().getComponentMetric().getComponentData()!=null) {
//							dcComponent.getDaComponent().getComponentMetric().getComponentData().size();
//						}
//					}
//				}
//				if(null != dcComponent.getComponentMetric()){
//					dcComponent.getComponentMetric().getId();
//					if (dcComponent.getComponentMetric().getComponentData()!=null) {
//						dcComponent.getComponentMetric().getComponentData().size();
//					}
//				}
//			}
//			if(bo instanceof DashboardComponentMetric){
//				DashboardComponentMetric dcComponent = (DashboardComponentMetric)bo;
//				if(null != dcComponent.getComponentData()){
//					dcComponent.getComponentData().size();
//				}
//			}
//		}
//		return null;
//	}
//	
//	private final Map<Integer, List<DashboardComponentData>> dashboardComponentGroupMap = new HashMap<Integer,  List<DashboardComponentData>>();
//	
//	private void prepareDependObject(){
//		if(null != getDataSource().getRealDaComponent() &&
//				null != getDataSource().getRealDaComponent().getComponentMetric() ){
//			if(null != getDataSource().getRealDaComponent().getComponentMetric().getSourceType() && !"".equals(getDataSource().getRealDaComponent().getComponentMetric().getSourceType())){
//				xdataSelect = getDataSource().getRealDaComponent().getComponentMetric().getSourceType();
//			}
//			
//			if(!getDataSource().getRealDaComponent().getComponentMetric().getComponentData().isEmpty()){
//				for(DashboardComponentData data:getDataSource().getRealDaComponent().getComponentMetric().getComponentData()){
//					if(dashboardComponentGroupMap.containsKey(data.getGroupIndex())){
//						List<DashboardComponentData> dataList = dashboardComponentGroupMap.get(data.getGroupIndex());
//						dataList.add(data);
//					}else{
//						List<DashboardComponentData> datalList = new ArrayList<DashboardComponentData>();
//						datalList.add(data);
//						dashboardComponentGroupMap.put(data.getGroupIndex(), datalList);
//					}
//				}
//			}
//			
//			if(!"".equals(getDataSource().getRealDaComponent().getComponentMetric().getMetricName())
//					&& null != getDataSource().getRealDaComponent().getComponentMetric().getMetricName()){
//				metricName = getDataSource().getRealDaComponent().getComponentMetric().getMetricName();
//			}
//		}
//	
//		if(null != getDataSource().getRealDaComponent() && null != getDataSource().getRealDaComponent().getComponentName() 
//				&& !"".equals(getDataSource().getRealDaComponent().getComponentName())){
//			widgetName = getDataSource().getRealDaComponent().getComponentName();
//		}
//		
//		if(null != getDataSource().getRealDaComponent() && DashboardComponent.COMPONENT_TYPE_METRIC.equals(getDataSource().getRealDaComponent().getComponentType())){
//			setDisableForMetric(true);
//		}else{
//			setDisableForMetric(false);
//		}
//	}
//	
//	private Long saveDependMetric(DashboardComponentMetric metric){
//		if(null != groupItemMap && !"".equals(groupItemMap)){
//			String groupItemMapTemp[] = groupItemMap.split(",");
//			String enableDisplayTotalTemp[] = enableDisplayTotals.split(",");
//			String enableBreakdownTemp[] = enableBreakdowns.split(",");
//			String validBreakdownTemp[] = validBreakdowns.split(",");
//			List<DashboardComponentData> dataList = new ArrayList<DashboardComponentData>();
//			int itemCount = -1;
//			
//			for(int i=0; i< groupSize;i++){
//				for(int j=0;j<Integer.valueOf(groupItemMapTemp[i]);j++){
//					itemCount ++;
//					if(!"-1".equals(sourceDatas[itemCount])){
//						DashboardComponentData data = new DashboardComponentData();
//						data.setSourceData(sourceDatas[itemCount]);
//						data.setDisplayName(displayNames[itemCount]);
//						data.setDisplayValue(displayValues[itemCount]);
//						data.setDisplayValueKey(displayValuesKey[itemCount]);
//						data.setValidBreakdown("true".equals(validBreakdownTemp[itemCount]) ? false : true);
//						data.setEnableBreakdown("true".equals(enableBreakdownTemp[itemCount])? true :false);
//						
//						if("true".equals(enableBreakdownTemp[itemCount])){
//							data.setLevelBreakDown("".equals(levelBreakDowns[itemCount])? 0:Short.valueOf(levelBreakDowns[itemCount]));
//							data.setEnableDisplayTotal("true".equals(enableDisplayTotalTemp[itemCount])? true:false);
//						}
//						
//						data.setPositionIndex(j);
//						data.setGroupIndex(i);
//						dataList.add(data);
//					}
//				}
//			}
//			if(!dataList.isEmpty()){
//				metric.setComponentData(dataList);
//				metric.setOwner(getDomain());
//				metric.setSourceType(xdataSelect);
//				metric.setSpecifyType(specifyType);
//				if(null != metricName && !"".equals(metricName) && "savePremadeMetric".equals(operation)){
//					metric.setMetricName(metricName);
//					//TODO
////					if(metricName.startsWith("def")){
////						metric.setDefaultFlg(true);
////					}
//				}
//				try {
//					if(null != metric.getId()){
//						QueryUtil.updateBo(metric);
//						return  metric.getId();
//					}else{
//						return QueryUtil.createBo(metric);
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		return null;
//	}
//	
//	public boolean getDisabledHtmlCk(){
//		if(getUserContext().isSuperUser()) {
//			return false;
//		} else {
//			return true;
//		}
//	}
//	
//	public String getDisplayHtmlCk(){
//		if(getUserContext().isSuperUser()) {
//			return "";
//		} else {
//			return "none";
//		}
//	}
//	
//	public String getHtmlLinkClass(){
//		if((getDataSource()!=null && getDataSource().getRealDaComponent().isEnabledHtml()) && getUserContext().isSuperUser()) {
//			return "";
//		}
//		return "customHtmlAItemDisable";
//	}
//	
//	public List<TextItem> getSourceDataList(){
//		return new ArrayList<TextItem>();
//	}
//	
//	public List<CheckItem> getParentDataList(){
//		return new ArrayList<CheckItem>();
//	}
//	
//	public EnumItem[] getDisplayOptionsDefault() {
//		return new EnumItem[] { new EnumItem(DashboardComponentData.DISPLAY_OPTION_DEFAULT,"") };
//	}
//	
//	public EnumItem[] getDisplayOptionsWithAgg() {
//		return new EnumItem[] { new EnumItem(DashboardComponentData.DISPLAY_OPTION_LEVEL_WITH_AGG,"") };
//	}
//	
//	public EnumItem[] getDisplayOptionsWithoutAgg() {
//		return new EnumItem[] { new EnumItem(DashboardComponentData.DISPLAY_OPTION_LEVEL_WITHOUT_AGG,"") };
//	}
//	
//	private String[] dashboardComponentGroupIds;
//	
//	private String[] sourceDatas;
//	
//	private String[] displayNames;
//	
//	private String[] displayValues;
//	
//	private String[] displayValuesKey;
//	
//	private String[] levelBreakDowns;
//	
//	private int groupSize;
//	
//	private String groupItemMap;
//	
//	private Long widgetId;
//	private boolean blnCloneWidget;
//	private String componentType;
//	private Long preWidgetId;
//	private String sourceType;
//	private Long preMetricId;
//	private String textCuHtml;
//	private String widgetName;
//	private String metricName;
//
//	public String[] getDashboardComponentGroupIds() {
//		return dashboardComponentGroupIds;
//	}
//
//	public void setDashboardComponentGroupIds(String[] dashboardComponentGroupIds) {
//		this.dashboardComponentGroupIds = dashboardComponentGroupIds;
//	}
//
//	public String[] getSourceDatas() {
//		return sourceDatas;
//	}
//
//	public void setSourceDatas(String[] sourceDatas) {
//		this.sourceDatas = sourceDatas;
//	}
//
//	public String[] getDisplayNames() {
//		return displayNames;
//	}
//
//	public void setDisplayNames(String[] displayNames) {
//		this.displayNames = displayNames;
//	}
//
//	public String[] getDisplayValues() {
//		return displayValues;
//	}
//
//	public void setDisplayValues(String[] displayValues) {
//		this.displayValues = displayValues;
//	}
//
//	public String[] getLevelBreakDowns() {
//		return levelBreakDowns;
//	}
//
//	public void setLevelBreakDowns(String[] levelBreakDowns) {
//		this.levelBreakDowns = levelBreakDowns;
//	}
//
//	public String getWidgetName() {
//		return widgetName;
//	}
//
//	public void setWidgetName(String widgetName) {
//		this.widgetName = widgetName;
//	}
//
//	public String getMetricName() {
//		return metricName;
//	}
//
//	public void setMetricName(String metricName) {
//		this.metricName = metricName;
//	}
//
//	public Map<Integer, List<DashboardComponentData>> getDashboardComponentGroupMap() {
//		return dashboardComponentGroupMap;
//	}
//
//	public int getGroupSize() {
//		return groupSize;
//	}
//
//	public void setGroupSize(int groupSize) {
//		this.groupSize = groupSize;
//	}
//
//	public String getGroupItemMap() {
//		return groupItemMap;
//	}
//
//	public void setGroupItemMap(String groupItemMap) {
//		this.groupItemMap = groupItemMap;
//	}
//
//	public Long getWidgetId() {
//		return widgetId;
//	}
//
//	public void setWidgetId(Long widgetId) {
//		this.widgetId = widgetId;
//	}
//
//	public boolean isBlnCloneWidget() {
//		return blnCloneWidget;
//	}
//
//	public void setBlnCloneWidget(boolean blnCloneWidget) {
//		this.blnCloneWidget = blnCloneWidget;
//	}
//
//	public String getComponentType() {
//		return componentType;
//	}
//
//	public void setComponentType(String componentType) {
//		this.componentType = componentType;
//	}
//
//	public String getSourceType() {
//		return sourceType;
//	}
//
//	public void setSourceType(String sourceType) {
//		this.sourceType = sourceType;
//	}
//
//	public Long getPreMetricId() {
//		return preMetricId;
//	}
//
//	public void setPreMetricId(Long preMetricId) {
//		this.preMetricId = preMetricId;
//	}
//
//	public Long getPreWidgetId() {
//		return preWidgetId;
//	}
//
//	public void setPreWidgetId(Long preWidgetId) {
//		this.preWidgetId = preWidgetId;
//	}
//
//	public String getTextCuHtml() {
//		return textCuHtml;
//	}
//
//	public void setTextCuHtml(String textCuHtml) {
//		this.textCuHtml = textCuHtml;
//	}
//	
//	private String xdataSelect = "-1";
//	
//	private int specifyType = DashboardComponent.WIDGET_SPECIFY_TYPE_NONE;
//	
//	private String specifyName;
//	
//	private boolean disableForMetric;
//	
//	private String displayName;
//	
//	private String displayValue;
//
//	public boolean isDisableForMetric() {
//		return disableForMetric;
//	}
//
//	public void setDisableForMetric(boolean disableForMetric) {
//		this.disableForMetric = disableForMetric;
//	}
//
//	public String getXdataSelect() {
//		return xdataSelect;
//	}
//
//	public void setXdataSelect(String xdataSelect) {
//		this.xdataSelect = xdataSelect;
//	}
//
//	public int getSpecifyType() {
//		return specifyType;
//	}
//
//	public void setSpecifyType(int specifyType) {
//		this.specifyType = specifyType;
//	}
//
//	public String getSpecifyName() {
//		return specifyName;
//	}
//
//	public void setSpecifyName(String specifyName) {
//		this.specifyName = specifyName;
//	}
//
//	public String getDisplayName() {
//		return displayName;
//	}
//
//	public void setDisplayName(String displayName) {
//		this.displayName = displayName;
//	}
//
//	public String getDisplayValue() {
//		return displayValue;
//	}
//
//	public void setDisplayValue(String displayValue) {
//		this.displayValue = displayValue;
//	}
//	
//	private String enableBreakdowns;
//	
//	private String enableDisplayTotals;
//	
//	private String validBreakdowns;
//
//	public String getEnableBreakdowns() {
//		return enableBreakdowns;
//	}
//
//	public void setEnableBreakdowns(String enableBreakdowns) {
//		this.enableBreakdowns = enableBreakdowns;
//	}
//
//	public String getEnableDisplayTotals() {
//		return enableDisplayTotals;
//	}
//
//	public void setEnableDisplayTotals(String enableDisplayTotals) {
//		this.enableDisplayTotals = enableDisplayTotals;
//	}
//
//	public String getValidBreakdowns() {
//		return validBreakdowns;
//	}
//
//	public void setValidBreakdowns(String validBreakdowns) {
//		this.validBreakdowns = validBreakdowns;
//	}
//
//	public String[] getDisplayValuesKey() {
//		return displayValuesKey;
//	}
//
//	public void setDisplayValuesKey(String[] displayValuesKey) {
//		this.displayValuesKey = displayValuesKey;
//	}
//	
//	public String isDefaultWidgetFlag() {
//		if(getDataSource().getRealDaComponent().isDefaultFlag()){
//			return "disabled";
//		}
//		return "";
//	}
//	
//	public boolean isDefaultMetricFlag() {
//		return getDataSource().getRealDaComponent().getComponentMetric().isDefaultFlag();
//	}
	
}