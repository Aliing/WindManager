package com.ah.util.bo.dashboard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.AhDirTools;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.report.AhDatetimeSeries;
import com.ah.bo.report.AhLinearSeries;
import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.ui.actions.monitor.NewReportExporting.ReportDataHolder;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.report.NumberTypeUtil;
import com.ah.util.bo.report.freechart.AhChartExportInterf;
import com.ah.util.bo.report.freechart.AhFreechartOption;
import com.ah.util.bo.report.freechart.AhFreechartPie;

import edu.emory.mathcs.backport.java.util.Collections;

public class DaDataCalculateUtil {
	private static final Tracer log = new Tracer(DaDataCalculateUtil.class
			.getSimpleName());
	
	private static ScriptEngineManager mgr;
	private static ScriptEngine engine;
	private static Invocable inv;
	private static Object nameSpaceDaSimpleHelper;
	private static Object nameSpaceDashDataBase;
	private static Object nameSpaceAdditionalOption;
	
	static {
		prepareJsFile();
	}
	
	private static void prepareJsFile() {
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByExtension("js");
		String basePath = AhDirTools.getHmRoot();
		String jsFileLangLoader = basePath + "js" + File.separator + "widget" + File.separator+ "chart" 
				+ File.separator+ "lang" + File.separator + "loader.js";
		String jsFileLangTxt = basePath + "js" + File.separator + "widget" + File.separator+ "chart" 
				+ File.separator+ "lang" + File.separator + "en_US.js";
		String jsFile1 = basePath + "js" + File.separator + "report" + File.separator + "en_US.js";
		String jsFile2 = basePath + "js" + File.separator + "report" + File.separator + "data.js";
		String jsFile3 = basePath + "js" + File.separator + "report" + File.separator + "simpleHelper.js";
		String jsFile4 = basePath + "js" + File.separator + "widget" + File.separator+ "chart" 
							+ File.separator+ "dataRender" + File.separator + "dashDataBase.js";
		String jsFile5 = basePath + "js" + File.separator + "underscore-min.js";
		String jsFileAdditional = basePath + "js" + File.separator + "report" + File.separator + "widgetAdditional.js";
		try {
			engine.eval(new FileReader(jsFileLangLoader));
			engine.eval(new FileReader(jsFileLangTxt));
			engine.eval(new FileReader(jsFile1));
			engine.eval(new FileReader(jsFile2));
			engine.eval(new FileReader(jsFile3));
			engine.eval(new FileReader(jsFile4));
			engine.eval(new FileReader(jsFile5));
			engine.eval(new FileReader(jsFileAdditional));
			nameSpaceDaSimpleHelper = engine.eval("AHDaSimpleHelper");
			nameSpaceDashDataBase = engine.eval("ah_dashDataRender_base_data");
			nameSpaceAdditionalOption = engine.eval("REPORT_ADDITIONAL.FUNCS");
			_doStringReplaceForOEMPurpose();
		} catch (FileNotFoundException e) {
			log.error("No js configuration file found", e);
			e.printStackTrace();
		} catch (ScriptException e) {
			log.error("Script execution wrong.", e);
			e.printStackTrace();
		}
		inv = (Invocable)engine;
	}
	
	private static void _doStringReplaceForOEMPurpose() throws ScriptException {
		engine.eval("Aerohive.lang.chart.device.status.group.title.name='" +
						MgrUtil.getUserMessage("hm.dashboard.widget.clientdevice.config.group.name.device")+ "';");
	}
	
	private static Object _invokeMethodWithNamespaceObject(Object _namespace, String methodName, Object... args) {
		try {
			return inv.invokeMethod(_namespace, methodName, args);
		} catch (NoSuchMethodException e) {
			log.error("No such js method", e);
			e.printStackTrace();
		} catch (ScriptException e) {
			log.error("Script execution wrong.", e);
			e.printStackTrace();
		}
		return null;
	}
	
	private static Object invokeMethodWithDashDataBase(String methodName, Object... args) {
		return _invokeMethodWithNamespaceObject(nameSpaceDashDataBase, methodName, args);
	}
	
	private static Object invokeMethodWithDaSimpleHelper(String methodName, Object... args) {
		return _invokeMethodWithNamespaceObject(nameSpaceDaSimpleHelper, methodName, args);
	}
	
	private static Object invokeMethodWithAdditionalOption(String methodName, Object... args) {
		return _invokeMethodWithNamespaceObject(nameSpaceAdditionalOption, methodName, args);
	}
	@SuppressWarnings("rawtypes")
	public static List<MetricGroupInfo> getMetricsGroupOption(String axis, int key) {
		Object result = invokeMethodWithAdditionalOption("getAddiOption", axis, "list", "group", "forkey", key);
		if (result != null
				&& result instanceof List) {
			List lstGrps = (List)result;
			List<MetricGroupInfo> groups = new ArrayList<>(lstGrps.size());
			for (Object obj : lstGrps) {
				MetricGroupInfo groupInfo = new MetricGroupInfo();
				Map singleGrp = (Map)obj;
				Object nameObj = singleGrp.get("name");
				if (nameObj instanceof Map) {
					groupInfo.setName(((Map)nameObj).get("name").toString());
				} else {
					groupInfo.setName(nameObj.toString());
				}
				if (singleGrp.containsKey("metrics")) {
					List metrics = (List)singleGrp.get("metrics");
					if (metrics != null
							&& metrics.size() > 0) {
						for (Object metricObj : metrics) {
							groupInfo.getMetrics().add(metricObj.toString());
						}
					}
				}
				groups.add(groupInfo);
			}
			return groups;
		}
		return null;
	}
	public static class MetricGroupInfo {
		private String name;
		private List<String> metrics = new ArrayList<>();
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<String> getMetrics() {
			return metrics;
		}
		public void setMetrics(List<String> metrics) {
			this.metrics = metrics;
		}
	}
	
	
	private static String getMetricDataType(String axis, String metric) {
		Object obj = invokeMethodWithDaSimpleHelper("_getMetricDataType", axis, metric);
		if (obj != null) {
			return obj.toString();
		}
		return null;
	}
	
	private static boolean isMetricDataIntType(String axis, String metric) {
		Object obj = invokeMethodWithDaSimpleHelper("_isMetricDataIntType", axis, metric);
		if (obj != null) {
			return Boolean.valueOf(obj.toString());
		}
		return false;
	}
	
	static class DataAndUnit {
		DataAndUnit(Object data) {
			this.data = data;
		}
		Object data;
		String unit;
		String formatUnit;
	}
	
	private static DataAndUnit prepareDataAndUnit(Object data, String baseUnit) {
		DataAndUnit result = new DataAndUnit(data);
		Object obj = invokeMethodWithDaSimpleHelper("_selectProperUnitForSeriesExpress", data, baseUnit);
		if (obj != null
				&& obj instanceof Map) {
			Map valueTmpMap = (Map)obj;
			if (valueTmpMap != null) {
				Object valueTmp = valueTmpMap.get("data");
				if (valueTmp != null) {
					result.data = valueTmp;
				}
				valueTmp = valueTmpMap.get("unit");
				if (valueTmp != null
						&& StringUtils.isNotBlank(valueTmp.toString())) {
					result.unit = valueTmp.toString();
				}
				valueTmp = valueTmpMap.get("formatUnit");
				if (valueTmp != null
						&& StringUtils.isNotBlank(valueTmp.toString())) {
					result.formatUnit = valueTmp.toString();
				}
			}
		}
		return result;
	}
	
	//use overtime configuration of dashboard component metric
	/*private static boolean isChartDefaultOvertime(String axis) {
		boolean result = false;
		Object obj = invokeMethodWithDaSimpleHelper("_isOvertime", axis);//inv.invokeMethod(nameSpaceDaSimpleHelper, "_isOvertime", axis);
		if (obj != null) {
			result = Boolean.valueOf(obj.toString());
		}
		return result;
	}*/
	
	private static final String CHART_PRESENTATION_NAME = "text";
	private static final String CHART_PRESENTATION_COLOR = "color";
	private static final String CHART_PRESENTATION_COLOR_TYPE = "color_type";
	private static Map<String, String> getPreferPresentation(String axis, String metric, String data) {
		Object obj = invokeMethodWithDaSimpleHelper("_getPreferPresentation", axis, metric, data);//inv.invokeMethod(nameSpaceDaSimpleHelper, "_isOvertime", axis);
		return getPreferPresentation(obj);
	}
	private static Map<String, String> getPreferPresentation(String axis, String metric) {
		Object obj = invokeMethodWithDaSimpleHelper("_getPreferPresentation", axis, metric);//inv.invokeMethod(nameSpaceDaSimpleHelper, "_isOvertime", axis);
		return getPreferPresentation(obj);
	}
	private static Map<String, String> getPreferPresentation(String axis, String metric, Object options) {
		Object obj = invokeMethodWithDaSimpleHelper("_getPreferPresentation", axis, metric, options);
		return getPreferPresentation(obj);
	}
	private static Map<String, String> getPreferPresentation(Object obj) {
		Map<String, String> result = new HashMap<>();
		if (obj != null) {
			Map valueTmpMap = (Map)obj;
			Object valueTmp = valueTmpMap.get(CHART_PRESENTATION_NAME);
			if (valueTmp != null) {
				result.put(CHART_PRESENTATION_NAME, valueTmp.toString());
			}
			valueTmp = valueTmpMap.get(CHART_PRESENTATION_COLOR);
			if (valueTmp != null) {
				result.put(CHART_PRESENTATION_COLOR, valueTmp.toString());
			}
			valueTmp = valueTmpMap.get(CHART_PRESENTATION_COLOR_TYPE);
			if (valueTmp != null) {
				result.put(CHART_PRESENTATION_COLOR_TYPE, valueTmp.toString());
			}
		}
		return result;
	}
	
	public static boolean isChartDefaultInverted(String axis) {
		boolean result = false;
		Object obj = invokeMethodWithDaSimpleHelper("_isChartDefaultInverted", axis);//inv.invokeMethod(nameSpaceDaSimpleHelper, "_isOvertime", axis);
		if (obj != null) {
			result = Boolean.valueOf(obj.toString());
		}
		return result;
	}
	
	public static String getDefaultChartType(String axis) {
		String result = "column";
		Object obj = invokeMethodWithDaSimpleHelper("_getDefaultChartType", axis);//inv.invokeMethod(nameSpaceDaSimpleHelper, "_getDefaultChartType", axis);
		if (obj != null) {
			result = obj.toString();
		}
		return result;
	}
	
	public static void testUnderscore() {
		//invokeMethodWithDaSimpleHelper("_test_underscore");//inv.invokeMethod(nameSpaceDaSimpleHelper, "_myHelloTestUnderscore");
	}
	
	public static String decorateDoubleQuotes(String value) {
		if (value == null
				|| StringUtils.isBlank(value)) {
			return "";
		}
		return value.replaceAll("\\\"", "\\\\\"");
	}
	
	public static String decorateSingleQuote(String value) {
		if (value == null
				|| StringUtils.isBlank(value)) {
			return "";
		}
		return value.replaceAll("\'", "\\\\'");
	}
	
	public static DaExportedSingleData doDataCalculate(ReportDataHolder dataHolder, HmDomain owner, TimeZone tz) {
		return new DashDataRenderClass(dataHolder).setOwner(owner).setTimeZone(tz).run();
	}
	
	public static class DashDataRenderClass {
		private BigDecimal minValue;
		private BigDecimal maxValue;
		private List<Object> categories;
		private List<SeriesInfo> series;
		private AhReportElement reportEl;
		private ReportDataHolder dataHolder;
		
		private String curConfigInfo = "";
		private String curConfigAdditionalInfo = "";
		private WidgetConfigInfo widgetInfo = new WidgetConfigInfo();
		
		private HmDomain owner;
		private TimeZone tz;
		
		private boolean blnStacked;
		private boolean blnRangeIntValue = true;
		
		private List<String> dataColors = null;
		private List<String> dataNames = null;
		
		private static final String VAL_RANGE_MARK = "valRange";
		private static final String CATEGORIES_MARK = "curCategories";
		private static final String SERIES_MARK = "curSortedSeries";
		
		private static SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy, h:mm:ssa");
		
		public DashDataRenderClass(ReportDataHolder dataHolder) {
			this.dataHolder = dataHolder;
		}
		
		public DashDataRenderClass setOwner(HmDomain owner) {
			this.owner = owner;
			return this;
		}
		
		public DashDataRenderClass setTimeZone(TimeZone tz) {
			this.tz = tz;
			dateFormat.setTimeZone(this.tz);
			return this;
		}
		
		private DaExportedSingleData getSurroundedExportedSingleData(DaExportedSingleData data) {
			return data.setChartType(this.widgetInfo.chartType)
					.setChartInverted(this.widgetInfo.chartInverted)
					.setColumn(dataHolder.getColumn())
					.setOrder(dataHolder.getOrder())
					.setAxisKey(this.widgetInfo.axis)
					.setUuKey(this.widgetInfo.uuKey);
		}
		
		public DaExportedSingleData run() {
			if (this.dataHolder == null
					|| this.dataHolder.getWidgetId() == null
					|| this.dataHolder.getWidgetId() <= 0L) {
				return null;
			}
			
			try {
				this.prepareWidgetAndConfigInfo(dataHolder.getWidgetId());
			} catch (Exception e) {
				log.error("Failed to prepare config data", e);
			}
			
			try {
				Object obj = engine.eval(prepareDataRenderOptions(dataHolder.getData().replace("\\\"", "\\\\\"")));
//				Object obj = engine.eval(prepareDataRenderOptions(dataHolder.getData()));
				Object result = invokeMethodWithDashDataBase("dataRender", obj);
				if (!(result instanceof Map)) {
					return getSurroundedExportedSingleData(new DaExportedSingleNullData(this.reportEl));
				}
				Map resultMap = (Map)result;
				if (resultMap == null
						|| resultMap.isEmpty()) {
					return getSurroundedExportedSingleData(new DaExportedSingleNullData(this.reportEl));
				}
				printMapKeys(resultMap);
				fetchOutValueRange(resultMap);
				fetchCategoriesFromResult(resultMap);
				fetchSeriesFromResult(resultMap);
				adjustReportElementProperties();
				
				//printResultInfo();
			} catch (ScriptException e) {
				log.error("Failed to execute script for data rendering", e);
				e.printStackTrace();
			} catch (JSONException e) {
				log.error("Failed to deal with JSON object for chart", e);
			}
			
			return getExportedResultData();
		}
		
		private void adjustReportElementProperties() {
			if (series != null
					&& series.size() > 0) {
				if (series.get(0) != null
						&& StringUtils.isNotBlank(series.get(0).myUnit)) {
					this.reportEl.setValueTitle(this.reportEl.getValueTitle() + " (" + series.get(0).myUnit + ")");
					this.widgetInfo.myUnit = series.get(0).myUnit;
				}
			}
		}
		
		private DaExportedSingleData getExportedResultData() {
			paddingDataInReportEl();
			if ("table".equals(this.widgetInfo.chartType)
					|| "list".equals(this.widgetInfo.chartType)
					|| "html".equals(this.widgetInfo.chartType)) {
				return getSurroundedExportedSingleData(new DaExportedSingleData(this.reportEl));
			} else {
				AhFreechartOption chartOption = new AhFreechartOption()
														.setBlnIntValue(this.blnRangeIntValue)
														.setValueRange(this.widgetInfo.valRange)
														.setBlnStacked(this.blnStacked)
														.setMyUnit(this.widgetInfo.myUnit)
														.setDataSample(this.dataHolder.getSample());
				Class<? extends AhChartExportInterf> cl = 
						JfreechartTypeSelectUtil.getCertainExportType(this.widgetInfo.chartType, this.widgetInfo.overtime, this.blnStacked);
				if (cl != null) {
					try {
						if (cl == AhFreechartPie.class) {
							AhFreechartPie clInstance = new AhFreechartPie();
							clInstance.setDataColors(dataColors);
							clInstance.setDataNames(dataNames);
							return new DaExportedSingleData(clInstance.setChartOption(chartOption).invoke(this.reportEl))
										.setColumn(dataHolder.getColumn())
										.setOrder(dataHolder.getOrder());
						} else {
							return new DaExportedSingleData(cl.newInstance().setChartOption(chartOption).invoke(this.reportEl))
										.setColumn(dataHolder.getColumn())
										.setOrder(dataHolder.getOrder());
						}
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
			return getSurroundedExportedSingleData(new DaExportedSingleData(this.reportEl));
		}
		
		private void paddingDataInReportEl() {
			if (!(widgetInfo.overtime || "pie".equals(widgetInfo.chartType))
					&& categories != null) {
				for (Object category : categories) {
					this.reportEl.addCategory(category);
				}
			}
			
			this.reportEl.setTz(this.tz);
			
			if (series != null) {
				Collections.sort(series, new Comparator<SeriesInfo>() {
					@Override
					public int compare(SeriesInfo o1, SeriesInfo o2) {
						if (o1.groupIndex > o2.groupIndex) {
							return 1;
						} else if (o1.groupIndex < o2.groupIndex) {
							return -1;
						} else {
							if (o1.order > o2.order) {
								return 1;
							} else {
								return -1;
							}
						}
					}
				});
				
				
				List<String> stackNames = new ArrayList<>();
				
				for (SeriesInfo seriesInfo : series) {
					AhSeries seriesObj;
					if (widgetInfo.overtime) {
						seriesObj = new AhDatetimeSeries();
					} else {
						seriesObj = new AhLinearSeries();
					}
					seriesObj.setName(seriesInfo.name);
					seriesObj.setShowType(seriesInfo.type);
					seriesObj.setStackGroup(seriesInfo.stackGroup.toString());
					seriesObj.setData(seriesInfo.data);
					seriesObj.setCustomColor(seriesInfo.color);
					seriesObj.setUnit(seriesInfo.myUnit);
					seriesObj.setUuKey(seriesInfo.uuKey);
					
					if (!this.blnStacked) {
						if (stackNames.contains(seriesObj.getStackGroupString())) {
							this.blnStacked = true;
						} else {
							stackNames.add(seriesObj.getStackGroupString());
						}
					}
					
					this.reportEl.addSeries(seriesObj);
				}
				
			}
		}
		
		@SuppressWarnings({ "rawtypes" })
		public void testDaDataRender01() {
			try {
				String axis = "bandwidth user";
				boolean blnOvertime = false;//isChartDefaultOvertime(axis);
				String scriptStr = "(function() { "
								+ "var options = {"
								+ "axis: '"+axis+"',"
								+ "xExp: '[bandwidth user]',"
								+ "overtime: " + blnOvertime + ","
								+ "chartType: '" + getDefaultChartType("bandwidth user") + "',"
								+ "blnPairData: false,"
								+ "data: \"" + decorateDoubleQuotes(getFakeResponseDataNoBreakdown()) + "\","
								+ "blnDds: false,"
								+ "curConfig: '" + prepareConfigStringForTest() +"'"
								+ "};"
								+ "return options;"
								+ "})();";
				Object obj = engine.eval(scriptStr);
				Object result = invokeMethodWithDashDataBase("dataRender", obj);
				if (!(result instanceof Map)) {
					return;
				}
				Map resultMap = (Map)result;
				if (resultMap == null
						|| resultMap.isEmpty()) {
					return;
				}
				printMapKeys(resultMap);
				fetchOutValueRange(resultMap);
				fetchCategoriesFromResult(resultMap);
				fetchSeriesFromResult(resultMap);
				
				//printResultInfo();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private String prepareDataRenderOptions(String data) {
			return "(function() { "
					+ "var options = {"
					+ "axis: '" + widgetInfo.axis + "',"
					+ "xExp: '" + widgetInfo.xExp + "',"
					+ "overtime: " + widgetInfo.overtime + ","
					+ "chartType: '" + widgetInfo.chartType + "',"
					+ "blnPairData: " + widgetInfo.blnPairData + ","
					+ "data: \"" + decorateDoubleQuotes(data) + "\","
					+ "blnDds: " + widgetInfo.blnDds + ","
					+ "curConfig: '" + prepareCurConfigString() +"'"
					+ "};"
					+ "return options;"
					+ "})();";
		}
		
		private String prepareCurConfigString() {
			return "(function(){ var config = {" + curConfigAdditionalInfo + "\"m\":" + curConfigInfo + "}; return config;})();";
		}
		
		@SuppressWarnings("rawtypes")
		private void printMapKeys(Map map) {
			/*if (map != null
					&& !map.isEmpty()) {
				for (Object key : map.keySet()) {
					System.out.println("reuslt key::" + key.toString());
					System.out.println("reuslt value::" + map.get(key));
				}
			}*/
		}
		
		@SuppressWarnings({ "rawtypes" })
		private void fetchOutValueRange(Map result){
			if (result == null
					|| result.isEmpty()
					|| !result.containsKey(VAL_RANGE_MARK)) {
				return;
			}
			printMapKeys(result);
			Object valRange = result.get(VAL_RANGE_MARK);
			if (valRange == null
					|| !(valRange instanceof Map)) {
				return;
			}
			Map valRangeMap = ((Map)valRange);
			if (valRangeMap.isEmpty()) {
				return;
			}
			
			minValue = NumberTypeUtil.convertToBigDecimal(valRangeMap.get("min"));
			maxValue = NumberTypeUtil.convertToBigDecimal(valRangeMap.get("max"));
		}
		
		@SuppressWarnings({ "rawtypes" })
		private void fetchCategoriesFromResult(Map result) {
			if (result == null
					|| result.isEmpty()
					|| !result.containsKey(CATEGORIES_MARK)) {
				return;
			}
			Object value = result.get(CATEGORIES_MARK);
			if (value == null
					|| !(value instanceof List)) {
				return;
			}
			List valueList = ((List)value);
			categories = new ArrayList<>();
			for (Object obj : valueList) {
				categories.add(obj);
			}
		}
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void fetchSeriesFromResult(Map result) throws JSONException, ScriptException {
			if (result == null
					|| result.isEmpty()
					|| !result.containsKey(SERIES_MARK)) {
				return;
			}
			Object value = result.get(SERIES_MARK);
			if (value == null
					|| !(value instanceof List)) {
				return;
			}
			JSONObject usedColorSets = new JSONObject();
			JSONObject colorTypes = new JSONObject();
			colorTypes.put("colorTypes", usedColorSets);
			List valueList = ((List)value);
			series = new ArrayList<>();
			
			Map valueTmpMap;
			Object valueTmp;
			for (Object obj : valueList) {
				if (!(obj instanceof Map)) {
					continue;
				}
				valueTmp = null;
				List<Object> dataList = new ArrayList<>();
				valueTmpMap = (Map)obj;
				SeriesInfo seriesTmp = new SeriesInfo();
				series.add(seriesTmp);
				valueTmp = valueTmpMap.get("name");
				if (valueTmp != null) {
					seriesTmp.name = valueTmp.toString();
					if (StringUtils.isNotBlank(seriesTmp.name)) {
						seriesTmp.name = seriesTmp.name.replaceAll("<br/>", "\n");
					}
				}
				valueTmp = valueTmpMap.get("type");
				if (valueTmp != null) {
					seriesTmp.type = valueTmp.toString();
				}
				valueTmp = valueTmpMap.get("stack");
				if (valueTmp != null) {
					seriesTmp.stackGroup = valueTmp;
				}
				valueTmp = valueTmpMap.get("mygidx");
				if (valueTmp != null) {
					seriesTmp.groupIndex = Float.valueOf(valueTmp.toString()).intValue();
				}
				valueTmp = valueTmpMap.get("myidx");
				if (valueTmp != null) {
					seriesTmp.order = Float.valueOf(valueTmp.toString()).intValue();
				}
				valueTmp = valueTmpMap.get("myUnit");
				if (valueTmp != null) {
					seriesTmp.myUnit = valueTmp.toString();
				}
				
				String metric = "";
				valueTmp = valueTmpMap.get("keyN");
				if (valueTmp != null) {
					metric = valueTmp.toString();
					seriesTmp.uuKey = metric;
				}
				
				if ("table".equals(widgetInfo.chartType) || "list".equals(widgetInfo.chartType)) {
					seriesTmp.dataType = getMetricDataType(this.dataHolder.getXaxis(), metric);
				}
				boolean seriesTimeType = false;
				if ("time".equals(seriesTmp.dataType)) {
					seriesTimeType = true;
				}
				boolean blnIntValue = isMetricDataIntType(this.dataHolder.getXaxis(), metric);
				if (this.blnRangeIntValue) {
					this.blnRangeIntValue = blnIntValue;
				}
				
				valueTmp = valueTmpMap.get("data");
				if (valueTmp != null
						&& valueTmp instanceof List) {
					dataList = (List)valueTmp;
					for (Object dataEl : dataList) {
						if (dataEl instanceof List) {
							if (widgetInfo.overtime) {
								Object objName = ((List)dataEl).get(0);
								Long timeValue = -1L;
								if (objName instanceof Double) {
									timeValue = ((Double)objName).longValue();
								}
								//seriesTmp.addData(new AhSeriesData(timeValue, ((List)dataEl).get(1)));
								addDataToSeries(seriesTmp, widgetInfo.chartType, timeValue, ((List)dataEl).get(1), seriesTmp.myUnit);
							} else {
								//seriesTmp.addData(new AhSeriesData(((List)dataEl).get(0), ((List)dataEl).get(1)));
								addDataToSeries(seriesTmp, widgetInfo.chartType, ((List)dataEl).get(0), ((List)dataEl).get(1), seriesTmp.myUnit);
							}
						} else {
							Object seriesDataValueTmp = dataEl;
							if (seriesTimeType && dataEl != null) {
								//seriesTmp.addData(new AhSeriesData(null, dateFormat.format(new Date(Double.valueOf(dataEl.toString()).longValue()))));
								seriesDataValueTmp = dateFormat.format(new Date(Double.valueOf(dataEl.toString()).longValue()));
							} else {
								if (blnIntValue) {
									//seriesTmp.addData(new AhSeriesData(null, Double.valueOf(dataEl.toString()).longValue()));
									seriesDataValueTmp = Double.valueOf(dataEl.toString()).longValue();
								}
							}
							addDataToSeries(seriesTmp, widgetInfo.chartType, null, seriesDataValueTmp, seriesTmp.myUnit);
						}
					}
				}
				
				Map<String, String> chartPresentation = null;
				if("pie".equals(widgetInfo.chartType)) {
					if (seriesTmp.data != null
							&& !seriesTmp.data.isEmpty()) {
						dataColors = new ArrayList<>();
						dataNames = new ArrayList<>();
						int curColorIdx = 0;
						int defColorsLen = AhReportProperties.SERIES_ALL_COLORS.length;
						for (AhSeriesData data : seriesTmp.data) {
							if (data.getName() != null) {
								chartPresentation = getPreferPresentation(this.dataHolder.getXaxis(), metric, data.getName().toString());
								if (chartPresentation != null) {
									if (StringUtils.isNotBlank(chartPresentation.get(CHART_PRESENTATION_NAME))) {
										dataNames.add(chartPresentation.get(CHART_PRESENTATION_NAME));
									} else {
										dataNames.add(data.getName().toString());
									}
									if (StringUtils.isNotBlank(chartPresentation.get(CHART_PRESENTATION_COLOR))) {
										dataColors.add(chartPresentation.get(CHART_PRESENTATION_COLOR));
									} else {
										dataColors.add(AhReportProperties.SERIES_ALL_COLORS[curColorIdx++%defColorsLen]);
									}
								}
							}
						}
					}
				} else {
					chartPresentation = getPreferPresentation(this.dataHolder.getXaxis(), metric, 
							engine.eval("(function(){return " + colorTypes.toString() + ";})();"));
					if (chartPresentation != null) {
						if (StringUtils.isNotBlank(chartPresentation.get(CHART_PRESENTATION_NAME))) {
							seriesTmp.name = chartPresentation.get(CHART_PRESENTATION_NAME);
						}
						if (StringUtils.isNotBlank(chartPresentation.get(CHART_PRESENTATION_COLOR))) {
							seriesTmp.color = chartPresentation.get(CHART_PRESENTATION_COLOR);
						}
						if (chartPresentation.containsKey(CHART_PRESENTATION_COLOR_TYPE)) {
							String colorType = chartPresentation.get(CHART_PRESENTATION_COLOR_TYPE);
							if (usedColorSets.has(colorType)) {
								usedColorSets.put(colorType, usedColorSets.getInt(colorType) + 1);
							} else {
								usedColorSets.put(colorType, 1);
							}
						}
					}
					
					if (seriesTmp.data != null
							&& !seriesTmp.data.isEmpty()) {
						for (AhSeriesData data : seriesTmp.data) {
							if (data.getValue() != null) {
								chartPresentation = getPreferPresentation(this.dataHolder.getXaxis(), metric, data.getValue().toString());
								if (chartPresentation != null) {
									if (chartPresentation.containsKey(CHART_PRESENTATION_NAME)) {
										data.setValue(chartPresentation.get(CHART_PRESENTATION_NAME));
									}
								}
								break;
							}
						}
					}
				}
				
			}
		}
		
		private static void addDataToSeries(SeriesInfo seriesTmp, String chartType, Object name, Object value, String baseUnit) {
			if (("table".equals(chartType) || "list".equals(chartType))
					&& StringUtils.isNotBlank(baseUnit)) {
				if (value instanceof List) {
					value = ((List)value).get(1);
				}
				DataAndUnit dataAndUnit = prepareDataAndUnit(value, baseUnit);
				seriesTmp.addData(new AhSeriesData(name, dataAndUnit.data, dataAndUnit.formatUnit));
			} else {
				seriesTmp.addData(new AhSeriesData(name, value));
			}
		}
		
		
		private class WidgetConfigInfo {
			protected String xExp;
			protected String axis;
			protected boolean overtime;
			protected String chartType = "column";
			protected boolean blnPairData = false;
			protected boolean blnDds = false;
			
			protected boolean chartInverted;
			protected String valRange;
			protected String myUnit;
			protected int uuKey;
		}
		
		private class SeriesInfo {
			protected String name;
			protected String type;
			protected Object stackGroup;
			protected List<AhSeriesData> data = new ArrayList<>();
			protected int groupIndex;
			protected int order;
			protected String color;
			protected String myUnit;
			protected String dataType;
			protected String uuKey;
			
			public void addData(AhSeriesData dataArg) {
				data.add(dataArg);
			}
		}
		
		private void printResultInfo() {
			if (minValue != null) {
				System.out.println(minValue.longValue());
			}
			if (maxValue != null) {
				System.out.println(maxValue.longValue());
			}
			
			if (categories != null
					&& !categories.isEmpty()) {
				for (Object obj : categories) {
					System.out.println(obj.toString());
				}
			}
			
			if (series != null
					&& !series.isEmpty()) {
				for (SeriesInfo seriesTmp : series) {
					if (seriesTmp == null) {
						continue;
					}
					
					System.out.println("name:: " + seriesTmp.name + ", type:: " + seriesTmp.type + ", stack:: " + seriesTmp.stackGroup.toString());
					System.out.println("groupIndex:: " + seriesTmp.groupIndex + ", order:: " + seriesTmp.order);
					if (!seriesTmp.data.isEmpty()) {
						for (AhSeriesData dataTmp : seriesTmp.data) {
							System.out.println("[" + dataTmp.getName() + ", " + dataTmp.getValue() + "]");
							System.out.println();
						}
					}
				}
			}
		}
		
		private class WidgetLazyLoader implements QueryBo {
			@Override
			public Collection<HmBo> load(HmBo bo) {
				if (bo instanceof AhDashboardWidget) {
					AhDashboardWidget daWidget = (AhDashboardWidget)bo;
					if (daWidget.getWidgetConfig() != null) {
						daWidget.getWidgetConfig().getId();
						if (daWidget.getWidgetConfig().getComponentMetric() != null) {
							daWidget.getWidgetConfig().getComponentMetric().getId();
							if (daWidget.getWidgetConfig().getComponentMetric().getComponentData() != null) {
								daWidget.getWidgetConfig().getComponentMetric().getComponentData().size();
							}
						}
					}
				}
				return null;
			}
		}
		
		private void prepareWidgetAndConfigInfo(Long widgetId) throws Exception {
			if (widgetId == null
					|| owner == null) {
				return;
			}
			AhDashboardWidget widget = QueryUtil.findBoByAttribute(AhDashboardWidget.class, 
						"id", 
						widgetId, 
						owner.getId(), 
						new WidgetLazyLoader());
			if (widget == null) {
				return;
			}
			this.reportEl = new AhReportElement(widget.getId());
			this.reportEl.setTitle(widget.getMainTitle());
			this.reportEl.setValueTitle(widget.getWidgetConfig().getComponentMetric().getDisplayName());
			
			widgetInfo.blnDds = widget.isBlnDdSpecialType();
			
			if (widget.getWidgetConfig() != null) {
				DashboardComponent daConfig = widget.getWidgetConfig();
				if (daConfig != null) {
					//currently, only use the name defined in metric, remove it if we need to use name in widget
					this.reportEl.setTitle(daConfig.getComponentMetric().getMetricName());
					widgetInfo.axis = daConfig.getSourceType();
					widgetInfo.xExp = daConfig.getComponentMetric().getDisplayValueKey();
					//widgetInfo.overtime = isChartDefaultOvertime(widgetInfo.axis);
					widgetInfo.overtime = daConfig.getComponentMetric().isBlnOverTime();
					widgetInfo.valRange = daConfig.getComponentMetric().getValueRange();
					if (StringUtils.isNotBlank(daConfig.getComponentMetric().getChartType())) {
						widgetInfo.chartType = daConfig.getComponentMetric().getChartType();
					} else {
						widgetInfo.chartType = getDefaultChartType(widgetInfo.axis);
					}
					if (widgetInfo.overtime || "pie".equals(widgetInfo.chartType)) {
						widgetInfo.blnPairData = true;
					}
					widgetInfo.chartInverted = daConfig.getComponentMetric().isChartInverted();//isChartDefaultInverted(widgetInfo.axis);
					widgetInfo.uuKey = daConfig.getComponentMetric().getKey();
					this.reportEl.setName(daConfig.getComponentMetric().getDisplayName());
					
					curConfigAdditionalInfo += "blnBkNoDataTotal: " + DaHelper.isBkNoDataNeedTotalWidget(daConfig.getKey()) + ", ";
					
					Map<Integer, List<DashboardComponentData>> dataMap = daConfig.getDaComponentDataGroupMap();
					boolean blnFirstGroup = true;
					if (dataMap != null
							&& !dataMap.isEmpty()) {
						curConfigInfo += "{";
						for (Integer key : dataMap.keySet()) {
							if (key == null) {
								continue;
							}
							String keyStr = key.toString();
							if (!blnFirstGroup) {
								curConfigInfo += ",";
							} else {
								blnFirstGroup = false;
							}
							curConfigInfo += "\"" + keyStr + "\": [";
							
							boolean blnFirstData = true;
							for (DashboardComponentData data : dataMap.get(key)) {
								JSONObject jMObj = new JSONObject();
								jMObj.put("name", data.getDisplayName());
								jMObj.put("metric", data.getSourceData());
								jMObj.put("exp", data.getDisplayValueKey());
								jMObj.put("bkl", data.getLevelBreakDown());
								jMObj.put("bkOption", data.getDisplayOption());
								jMObj.put("gidx", data.getGroupIndex());
								jMObj.put("idx", data.getPositionIndex());
								
								if (!blnFirstData) {
									curConfigInfo += ",";
								} else {
									blnFirstData = false;
								}
								curConfigInfo += jMObj.toString();
							}
							curConfigInfo += "]";
						}
						curConfigInfo += "}";
					}
				}
			}
		}
		
		private String prepareConfigStringForTest() {
			JSONObject jMObj1 = new JSONObject();
			JSONObject jMObj2 = new JSONObject();
			
			try {
				jMObj1.put("name", "Bits Per Second");
				jMObj1.put("metric", "Bits Per Second");
				jMObj1.put("exp", "[Bits Per Second]");
				jMObj1.put("bkl", 0);
				jMObj1.put("bkOption", 1);
				jMObj1.put("gidx", 0);
				jMObj1.put("idx", 0);
				
				jMObj2.put("name", "inbound 5 GHz BPS");
				jMObj2.put("metric", "inbound 5 GHz BPS");
				jMObj2.put("exp", "[inbound 5 GHz BPS]");
				jMObj2.put("bkl", 0);
				jMObj2.put("bkOption", 1);
				jMObj2.put("gidx", 0);
				jMObj2.put("idx", 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return "(function(){ var config = {\"m\": {\"0\": [" + jMObj1.toString() + ", " + jMObj2.toString() + "]}}; return config; })();";
		}
		
		private String getFakeResponseDataNoBreakdown() {
			return "[{ 'bandwidth user' :'TheMost'   , 'Bits Per Second' :1000       , 'inbound 5 GHz BPS' :100},"
					+ "{ 'bandwidth user' :'2ndMost'   , 'Bits Per Second' :500        , 'inbound 5 GHz BPS' :50} ]";
		}
	}
	
	public static void testDaDataRender01() {
		
	}
	
	public static void main(String[] args) {
		//System.out.println(DaDataCalculateUtil.isChartOvertime("time for network device(s)"));
		//System.out.println(DaDataCalculateUtil.getDefaultChartType("time for network device(s)"));
		//DaDataCalculateUtil.testUnderscore();
		DaDataCalculateUtil.testDaDataRender01();
		/*String str1 = "1234\"1234";
		System.out.println(str1);
		System.out.println(DaDataCalculateUtil.decorateDoubleQuotes(str1));*/
	}
}
