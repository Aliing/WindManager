package com.ah.util.bo.dashboard;

import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.dashboard.AhDashboardWidget;
import com.ah.bo.dashboard.DashboardComponent;
import com.ah.bo.dashboard.DashboardComponentData;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.common.async.Response;
import com.ah.nms.json.Report;
import com.ah.ui.actions.monitor.DashboardAction;
import com.ah.util.Tracer;

public class ReportDataRequestUtil {
//	private static  final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Tracer	log	= new Tracer(ReportDataRequestUtil.class.getSimpleName());
	
	public static void requestForDashboard(Object data, 
			@SuppressWarnings("rawtypes") Response.Handler responder) throws Exception
    {
		Report.request(data, responder);
    }
	
	
	public static RequestData prepareDataWithWidgetID(Long daId, Long wdId,TimeZone tz) {
		return prepareDataWithWidgetID(daId, wdId, tz, null);
	}
	public static RequestData prepareDataWithWidgetID(AhDashboard da, Long wdId,TimeZone tz) {
		return prepareDataWithWidgetID(da, wdId, tz, null);
	}
	public static RequestData prepareDataWithWidgetID(Long daId, Long wdId,TimeZone tz, RequestDataModifier modifier) {
		//prepare widget from id here
		AhDashboard  da = null;
		if (daId != null) {
			da = QueryUtil.findBoById(AhDashboard.class, daId);
		}
		return prepareDataWithWidgetID(da, wdId, tz, modifier);
	}
	public static RequestData prepareDataWithWidgetID(AhDashboard da, Long wdId,TimeZone tz, RequestDataModifier modifier) {
		//prepare widget from id here
		AhDashboardWidget  dw = QueryUtil.findBoById(AhDashboardWidget.class, wdId, new ImplQueryBo());
		
		if (dw == null) {
			log.error("Widget was not found because dashboard layout was modified by others.");
			RequestData data = new RequestData();
			data.setErrMsg("Widget was modified by others, please reload page to apply the modification.");
			return data;
		}
		return prepareDataWithConfig(da,dw, dw.getWidgetConfig(), tz, modifier);
	}
	
	
	public static RequestData prepareDataWithConfigID(Long daId, Long cfId,TimeZone tz) {
		return prepareDataWithConfigID(daId, cfId, tz, null);
	}
	public static RequestData prepareDataWithConfigID(AhDashboard da, Long cfId,TimeZone tz) {
		return prepareDataWithConfigID(da, cfId, tz, null);
	}
	public static RequestData prepareDataWithConfigID(Long daId, Long cfId,TimeZone tz, RequestDataModifier modifier) {
		//prepare widget from id here
		AhDashboard  da = null;
		if (daId != null) {
			da = QueryUtil.findBoById(AhDashboard.class, daId);
		}
		return prepareDataWithConfigID(da, cfId, tz, modifier);
	}
	public static RequestData prepareDataWithConfigID(AhDashboard da, Long cfId,TimeZone tz, RequestDataModifier modifier) {
		DashboardComponent  dc = QueryUtil.findBoById(DashboardComponent.class, cfId, new ImplQueryBo());
		
		return prepareDataWithConfig(da,null, dc, tz, modifier);
	}
	
	
	public static RequestData prepareDataWithConfig(AhDashboard da, AhDashboardWidget wd,TimeZone tz) {
		return prepareDataWithConfig(da, wd, tz, null);
	}
	public static RequestData prepareDataWithConfig(AhDashboard da, AhDashboardWidget wd,TimeZone tz, RequestDataModifier modifier) {
		return prepareDataWithConfig(da,wd, wd.getWidgetConfig(), tz, modifier);
	}
	
	
	public static RequestData prepareDataWithConfig(AhDashboard da, AhDashboardWidget wd,DashboardComponent config,TimeZone tz) {
		return prepareDataWithConfig(da, wd, config, tz, null);
	}
	public static RequestData prepareDataWithConfig(AhDashboard da, AhDashboardWidget wd,DashboardComponent config,TimeZone tz, RequestDataModifier modifier) {
		if (config == null) {
			return null;
		}
		
		RequestData rd = new RequestData();
		
		//in new dashboard case
		if (da == null
				&& wd == null) {
			da = createDefaultTempDashboard(config.getOwner());
		}
		
		if (modifier != null) {
			if (wd != null) {
				modifier.modify(wd, da);
			} 
			if (da != null) {
				modifier.modify(da, wd == null);
			}
			if (config != null) {
				modifier.modify(config);
			}
		}
		rd.setMetricDeviceType(config.getComponentMetric().getFilterDataType());
		rd.setExample(config.getComponentMetric().isEnableExampleData());
		rd.setAxis(config.getComponentMetric().getSourceType());
		rd.setRow(0);
		rd.setRows(config.getComponentMetric().getTopNumber());
		if (config.getComponentMetric().isOrderByDesc()) {
			rd.setOrderBy(config.getComponentMetric().getOrderByMetric(), true);
		}
		if (config.getComponentMetric().getGroupBy()!=0) {
			rd.setGroupBy(config.getComponentMetric().getGroupBy());
		}
		
		setRequestDataSpecifyInfo(wd, config, rd);
		setRequestDomain(da, wd, config, rd);
		
		setGroupAndFilter(da, wd,rd);
		// don't set group for some widget 
		clearGroupAndFilter(config,rd);
		
		setTimeAndSample(da, wd,rd, tz);
		setMetric(config, wd, rd);
		setReuqestDataWithOriginalReuqestId(da, wd, rd);
		
		if (config != null
				&& config.getComponentMetric().isEnabledHtml()) {
			rd.setRenderType("html");
			rd.setHtmlContent(config.getComponentMetric().getCustomHtml());
			setRequestSpecifyName(wd, config, rd);
		}
		
		if (config != null) {
			rd.setBlnOvertime(config.getComponentMetric().isBlnOverTime());
			rd.setBlnAlwaysLastHour(DaHelper.isLastHourWidget(config.getComponentMetric().getKey()));
		}
		
		rd.setCurrentDeviceCount(DaHelper.getDeviceCount(da, rd.getDomain()));
		rd.setComponentKey(config.getKey());
		rd.setDaId(da.getId());
		
		reSetSampleWhenApplicantion(da,rd);
		
		return rd;
	}
	
	private static void setReuqestDataWithOriginalReuqestId(AhDashboard da, AhDashboardWidget wd, RequestData rd) {
		
		if (da==null) {
			return;
		}
		
		int deviceNumber = DashboardAction.calculateAppDevicesNumber(da.getObjectType(), 
				da.getObjectId(), da.getOwner().getId());
		rd.setApNumber(deviceNumber);
		
		if (!(da.isApplicationPerspective()
						&& deviceNumber > DashboardAction.SUPPORT_APPLICATION_ONEUSER_SESSION_APS 
						&& (da.getSelectTimeType() == AhDashboard.TAB_TIME_LASTDAY
								|| da.getSelectTimeType() == AhDashboard.TAB_TIME_LASTWEEK
								|| da.getSelectTimeType() == AhDashboard.TAB_TIME_LAST8HOUR
								|| da.getSelectTimeType() == AhDashboard.TAB_TIME_CUSTOM))) {
			return;
		}
		String oriRequestId = "";
		if (da != null
				&& da.getId() != null) {
			oriRequestId = String.valueOf(da.getId());
		} else {
			oriRequestId = String.valueOf(System.currentTimeMillis());
		}
		oriRequestId += "__" + String.valueOf(wd.getId());
		rd.setRequestId(oriRequestId);
	}
	public static void encapRealRequestDataRequestId(RequestData rd, long grpTimestamp) {
		if (StringUtils.isNotBlank(rd.getRequestId())) {
			rd.setRequestId(rd.getRequestId().replaceFirst("__", "_" + String.valueOf(grpTimestamp) + "_"));
		}
	}
	
	private static void setRequestDataSpecifyInfo(AhDashboardWidget wd, DashboardComponent config, RequestData rd) {
		if (wd == null
				|| wd.getSpecifyType() == DashboardComponent.WIDGET_SPECIFY_TYPE_NONE) {
			if (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_CLIENT) {
				rd.setClientDeviceMAC(config.getSpecifyName());
			} else if (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APPCLIENT) {
				rd.setClientDeviceMAC(config.getSpecifyName());
			} else if (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_DEVICE) {
				rd.setDeviceMac(config.getSpecifyName());
			} else if  (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APP) {
				rd.setApplication(Short.parseShort(config.getSpecifyName()));
			} else if  (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_USER) {
				rd.setUserName(config.getSpecifyName());
			} else if  (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_PORT) {
				String [] macAndPort = config.getSpecifyName().split(";");
				if (macAndPort!=null && macAndPort.length>1) {
					//MAC=
					rd.setClientDeviceMAC(macAndPort[0].substring(4));
					//PORT=
					rd.setPort(macAndPort[1].substring(5));
				}
			}
		} else if (wd.getSpecifyType() != DashboardComponent.WIDGET_SPECIFY_TYPE_NONE) {
			if (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_CLIENT) {
				rd.setClientDeviceMAC(wd.getSpecifyName());
			} else if (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APPCLIENT) {
				rd.setClientDeviceMAC(wd.getSpecifyName());
			} else if (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_DEVICE) {
				rd.setDeviceMac(wd.getSpecifyName());
			} else if  (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APP) {
				rd.setApplication(Short.parseShort(wd.getSpecifyName()));
			} else if  (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_USER) {
				rd.setUserName(wd.getSpecifyName());
			} else if  (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_PORT) {
				String [] macAndPort = wd.getSpecifyName().split(";");
				if (macAndPort!=null && macAndPort.length>1) {
					//MAC=
					rd.setClientDeviceMAC(macAndPort[0].substring(4));
					//PORT=
					rd.setPort(macAndPort[1].substring(5));
				}
			}
		}
	}
	
	private static void setRequestSpecifyName(AhDashboardWidget wd, DashboardComponent config, RequestData rd) {
		if (config == null
				|| rd == null) {
			return;
		}
		if (wd == null
				|| wd.getSpecifyType() == DashboardComponent.WIDGET_SPECIFY_TYPE_NONE) {
			if  (config.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APP) {
				// TODO: fetch application name but not id
				rd.setSpecifyName(config.getSpecifyName());
			} else {
				rd.setSpecifyName(config.getSpecifyName());
			}
		} else if (wd.getSpecifyType() != DashboardComponent.WIDGET_SPECIFY_TYPE_NONE) {
			if  (wd.getSpecifyType()==DashboardComponent.WIDGET_SPECIFY_TYPE_APP) {
				// TODO: fetch application name but not id
				rd.setSpecifyName(wd.getSpecifyName());
			} else {
				rd.setSpecifyName(wd.getSpecifyName());
			}
		}
		
		rd.setSpecifyType(config.getSpecifyType());
	}
	
	private static void setRequestDomain(AhDashboard da, AhDashboardWidget wd,DashboardComponent config, RequestData rd) {
		if (da != null) {
			rd.setDomain(da.getOwner().getId());
		} else if (wd != null) {
			rd.setDomain(wd.getOwner().getId());
		} else if (config != null) {
			rd.setDomain(config.getOwner().getId());
		} else {
			rd.setDomain(0);
		}
	}
	
	private static void setMetric(DashboardComponent cf, AhDashboardWidget wd, RequestData rd){
		if(cf.getComponentMetric()==null) return;
		if(cf.getComponentMetric().getComponentData()==null) return;
		if(cf.getComponentMetric().getComponentData().isEmpty()) return;
		boolean blnDdSpecialType = false;
		if (wd != null) {
			blnDdSpecialType = wd.isBlnDdSpecialType();
		}
		Set<String> metrics = cf.getExpMetrics();
		if (metrics != null
				&& metrics.size() > 1) {
			for (String metric : metrics) {
				if (blnDdSpecialType) {
					rd.getMetrics().put(metric, null);
				} else {
					rd.getMetrics().put(metric, (byte)0);
				}
			}
		}
		byte breakdownleval=0;
		for(DashboardComponentData da :cf.getComponentMetric().getComponentData()){
			metrics = da.getExpMetrics();
			if (metrics != null
					&& !metrics.isEmpty()) {
				for (String metric : metrics) {
					if (blnDdSpecialType) {
						rd.getMetrics().put(metric, null);
					} else {
						breakdownleval= (byte)da.getLevelBreakDown();
						rd.getMetrics().put(metric, (byte)da.getLevelBreakDown());
					}
				}
			}
		}
		
		for (String key:rd.getMetrics().keySet()){
			if (rd.getMetrics().get(key)!=null) {
				rd.getMetrics().put(key, breakdownleval);
			}
		}
	}
	
	private static void clearGroupAndFilter(DashboardComponent config,RequestData rd) {
		if (config!=null 
				&& (config.getKey()==1 || 
					config.getKey()==7 || 
//					config.getKey()==11 ||
					config.getKey()==19 ||
					config.getKey()==21 ||
					config.getKey()==34)) {
			rd.setNetworkPolicy(RequestData.NO_DATA_SET);
			rd.setTopology(RequestData.NO_DATA_SET);
			rd.setSsid(null);
		}
	}
	
	private static void reSetSampleWhenApplicantion(AhDashboard da,RequestData rd){
		if (da.getDaType()!=AhDashboard.DASHBOARD_TYPE_REPORT) {
			if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEDAY) {
				if (DaHelper.isApplicationChangeTimeModeWidgetKey(rd.getComponentKey())){
					rd.setSample(60*60*24);
				}
			} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK) {
				if (DaHelper.isApplicationChangeTimeModeWidgetKey(rd.getComponentKey())){
					rd.setSample(60*60*24*7);
				}
			}
		}
	}
	
	private static void setGroupAndFilter(AhDashboard da, AhDashboardWidget wd,RequestData rd){
		if (wd!=null && wd.isBlnChecked()) {
			
			if (!wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) && 
					!wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ALL)) {
				
				if (wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL)) {
					if (!wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL)) {
						if (wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP)){
							rd.setDeviceType("AP");
							rd.setFilterDeviceType(1);
						} else if (wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR)){
							rd.setDeviceType("BR");
							rd.setFilterDeviceType(1);
						} else if (wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR)){
							rd.setDeviceType("SR");
							rd.setFilterDeviceType(2);
						} else {
							rd.setDeviceModel(wd.getObjectId());
							if (wd.getObjectId().startsWith("SR")){
								rd.setFilterDeviceType(2);
							} else {
								rd.setFilterDeviceType(1);
							}
						}
					}
				} else if (wd.getObjectType().endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
					if (wd.getObjectType().startsWith("SR")){
						rd.setFilterDeviceType(2);
					} else {
						rd.setFilterDeviceType(1);
					}
					rd.setDeviceMac(wd.getObjectId());
				} else if (wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)){
					if (!wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)) {
						rd.setNetworkPolicy(Long.parseLong(wd.getObjectId()));
					}
				} else if (wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)){
					if (!wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)) {
						rd.setTag1(wd.getObjectId());
					}
				} else if (wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)){
					if (!wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)) {
						rd.setTag2(wd.getObjectId());
					}
				} else if (wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)){
					if (!wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)) {
						rd.setTag3(wd.getObjectId());
					}
				} else if (wd.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)){
					if (!wd.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
						rd.setTopology(wd.getObjectId()==null? RequestData.NO_DATA_SET:Long.parseLong(wd.getObjectId()));
					}
				}
			}
			
			if(wd.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_SSID) && 
					!wd.getFilterObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_SSID)) {
				rd.setSsid(wd.getFilterObjectId());
			} else if (wd.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL) && 
					!wd.getFilterObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL)) {
				rd.setUserProfile(wd.getFilterObjectId());
			}
			
		} else if (da != null) {
//			rd.setTopology(da.getLocation()==null? RequestData.NO_DATA_SET:da.getLocation().getId());
			if (!da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_OBJECT_ALL) && 
					!da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ALL)) {
				
				if (da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL)) {
					if (!da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ALL)) {
						if (da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_AP)){
							rd.setDeviceType("AP");
							rd.setFilterDeviceType(1);
						} else if (da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_BR)){
							rd.setDeviceType("BR");
							rd.setFilterDeviceType(1);
						} else if (da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_SR)){
							rd.setDeviceType("SR");
							rd.setFilterDeviceType(2);
						} else {
							rd.setDeviceModel(da.getObjectId());
							if (da.getObjectId().startsWith("SR")){
								rd.setFilterDeviceType(2);
							} else {
								rd.setFilterDeviceType(1);
							}
						}
					}
				} else if (da.getObjectType().endsWith(AhDashBoardGroup.DA_GROUP_TYPE_DEVICE_ENDWITH)){
					if (da.getObjectType().startsWith("SR")){
						rd.setFilterDeviceType(2);
					} else {
						rd.setFilterDeviceType(1);
					}
					rd.setDeviceMac(da.getObjectId());
				} else if (da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)){
					if (!da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY)) {
						rd.setNetworkPolicy(Long.parseLong(da.getObjectId()));
					}
				} else if (da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)){
					if (!da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)) {
						rd.setTag1(da.getObjectId());
					}
				} else if (da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)){
					if (!da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)) {
						rd.setTag2(da.getObjectId());
					}
				} else if (da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)){
					if (!da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)) {
						rd.setTag3(da.getObjectId());
					}
				} else if (da.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)){
					if (!da.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
						rd.setTopology(da.getObjectId()==null? RequestData.NO_DATA_SET:Long.parseLong(da.getObjectId()));
					}
				}
			}
			
			if(da.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_SSID) && 
					!da.getFilterObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_SSID)) {
				rd.setSsid(da.getFilterObjectId());
			} else if (da.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL) && 
					!da.getFilterObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL)) {
				rd.setUserProfile(da.getFilterObjectId());
			}
				
		} else {
			rd.setTopology(RequestData.NO_DATA_SET);
		}
	}
	
	public static int getDaWidgetSample(AhDashboard da, AhDashboardWidget wd, TimeZone tz, String daType) {
		Calendar ca = Calendar.getInstance(tz);
		int result = 60;
		if (!da.isBlnNullNewDa()) {
			if ("da".equals(daType)) {
				if (wd!=null && wd.isBlnChecked()) {
					if(wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTHOUR) {
						result = 60;
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTDAY) {
						result = 60*60;
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LAST8HOUR) {
						result = 60*60;
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEDAY) {
						result = 60*60;
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTWEEK) {
						result = 60*60*24;
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK) {
						result = 60*60*24;
					} else {
						long timespace = wd.getCustomEndTime()-wd.getCustomStartTime();
						result = calculateSampleValue(timespace);
					}
				} else {
					if(da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTHOUR) {
						result = 60;
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTDAY) {
						result = 60*60;
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LAST8HOUR) {
						result = 60*60;
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEDAY) {
						result = 60*60;
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTWEEK) {
						result = 60*60*24;
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK) {
						result = 60*60*24;
					} else {
						long timespace = da.getCustomEndTime()-da.getCustomStartTime();
						result = calculateSampleValue(timespace);
					}
				}
			} else {
				long[] aa = da.getScheduleStartTimePast(ca);
				long timespace = aa[1]-aa[0];
				result = calculateSampleValue(timespace);
			}
		} else {
			if ("recurp".equals(daType)){
				result = 60*60;
			} else {
				result = 60;
			}
		}
		
		return result;
	}
	private static void setTimeAndSample(AhDashboard da, AhDashboardWidget wd,RequestData rd, TimeZone tz){
		Calendar ca = Calendar.getInstance(tz);
		long currentTime = System.currentTimeMillis();
		if (!da.isBlnNullNewDa()) {
			if (da.isBlnTypeRecurReport()) {
				long[] aa = da.getScheduleStartTimePast(ca);
//				if (da.isEnableTimeLocal()) {
//					ca.setTimeInMillis(aa[0]);
//					rd.setStartTime(Long.valueOf(FORMAT.format(ca.getTime())));
//					ca.setTimeInMillis(aa[1]);
//					rd.setEndTime(Long.valueOf(FORMAT.format(ca.getTime())));
//					rd.setApTimezone(tz.getID());
//				} else {
					rd.setStartTime(aa[0]);
					rd.setEndTime(aa[1]);
//				}
				long timespace = aa[1]-aa[0];
				rd.setSample(calculateSampleValue(timespace));
			} else {
				if (wd!=null && wd.isBlnChecked()) {
					if(wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTHOUR) {
						ca.setTimeInMillis(currentTime-3600000);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						rd.setStartTime(ca.getTimeInMillis());
						rd.setEndTime(rd.getStartTime()+3600000);
						rd.setSample(60);
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LAST8HOUR) {
						ca.setTimeInMillis(currentTime -3600000L * 8);
						rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
						rd.setEndTime(currentTime/3600000L*3600000L);
						rd.setSample(60*60);
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTDAY) {
						ca.setTimeInMillis(currentTime -3600000L * 24);
						rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
						rd.setEndTime(currentTime/3600000L*3600000L);
						rd.setSample(60*60);
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEDAY) {
						ca.setTimeInMillis(currentTime -3600000L * 24);
						ca.set(Calendar.HOUR_OF_DAY,0);
						rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
						ca.add(Calendar.DAY_OF_MONTH,1);
						rd.setEndTime(ca.getTimeInMillis()/3600000L*3600000L);
						rd.setSample(60*60);
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTWEEK) {
						ca.setTimeInMillis(currentTime-3600000L * 24 * 7);
						ca.clear(Calendar.MINUTE);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						ca.set(Calendar.HOUR_OF_DAY,0);
						rd.setStartTime(ca.getTimeInMillis());
						
						ca.setTimeInMillis(currentTime);
						ca.clear(Calendar.MINUTE);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						ca.set(Calendar.HOUR_OF_DAY,0);
						rd.setEndTime(ca.getTimeInMillis());
						rd.setSample(60*60*24);
					} else if (wd.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK) {
						ca.setTimeInMillis(currentTime-3600000L * 24 * 7);
						ca.clear(Calendar.MINUTE);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						ca.set(Calendar.HOUR_OF_DAY,0);
						ca.add(Calendar.DAY_OF_MONTH,(-1* (ca.get(Calendar.DAY_OF_WEEK)-1)));
						rd.setStartTime(ca.getTimeInMillis());
						
						ca.add(Calendar.DAY_OF_MONTH,7);
						rd.setEndTime(ca.getTimeInMillis());
						rd.setSample(60*60*24);
					} else {
//						if (wd.isEnableTimeLocal()) {
//							ca.setTimeInMillis(wd.getCustomStartTime());
//							rd.setStartTime(Long.valueOf(FORMAT.format(ca.getTime())));
//							ca.setTimeInMillis(wd.getCustomEndTime());
//							rd.setEndTime(Long.valueOf(FORMAT.format(ca.getTime())));
//							rd.setApTimezone(tz.getID());
//						} else {
						rd.setStartTime(wd.getCustomStartTime());
						rd.setEndTime(wd.getCustomEndTime());
//						}
						long timespace = wd.getCustomEndTime()-wd.getCustomStartTime();
						rd.setSample(calculateSampleValue(timespace));
					}
				} else {
					if(da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTHOUR) {
						ca.setTimeInMillis(currentTime-3600000);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						rd.setStartTime(ca.getTimeInMillis());
						rd.setEndTime(rd.getStartTime()+3600000);
						rd.setSample(60);
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LAST8HOUR) {
						ca.setTimeInMillis(currentTime -3600000L * 8);
						rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
						rd.setEndTime(currentTime/3600000L*3600000L);
						rd.setSample(60*60);
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTDAY) {
						ca.setTimeInMillis(currentTime -3600000L * 24);
						rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
						rd.setEndTime(currentTime/3600000L*3600000L);
						rd.setSample(60*60);
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEDAY) {
						ca.setTimeInMillis(currentTime -3600000L * 24);
						ca.set(Calendar.HOUR_OF_DAY,0);
						rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
						ca.add(Calendar.DAY_OF_MONTH,1);
						rd.setEndTime(ca.getTimeInMillis()/3600000L*3600000L);
						rd.setSample(60*60);
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTWEEK) {
						ca.setTimeInMillis(currentTime-3600000L * 24 * 7);
						ca.clear(Calendar.MINUTE);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						ca.set(Calendar.HOUR_OF_DAY,0);
						rd.setStartTime(ca.getTimeInMillis());
						
						ca.setTimeInMillis(currentTime);
						ca.clear(Calendar.MINUTE);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						ca.set(Calendar.HOUR_OF_DAY,0);
						rd.setEndTime(ca.getTimeInMillis());
						rd.setSample(60*60*24);
					} else if (da.getSelectTimeType()==AhDashboard.TAB_TIME_LASTCALEWEEK) {
						ca.setTimeInMillis(currentTime-3600000L * 24 * 7);
						ca.clear(Calendar.MINUTE);
						ca.clear(Calendar.SECOND);
						ca.clear(Calendar.MILLISECOND);
						ca.set(Calendar.HOUR_OF_DAY,0);
						ca.add(Calendar.DAY_OF_MONTH,(-1* (ca.get(Calendar.DAY_OF_WEEK)-1)));
						rd.setStartTime(ca.getTimeInMillis());
						
						ca.add(Calendar.DAY_OF_MONTH,7);
						rd.setEndTime(ca.getTimeInMillis());
						rd.setSample(60*60*24);
					} else {
//						if (da.isEnableTimeLocal()) {
//							ca.setTimeInMillis(da.getCustomStartTime());
//							rd.setStartTime(Long.valueOf(FORMAT.format(ca.getTime())));
//							ca.setTimeInMillis(da.getCustomEndTime());
//							rd.setEndTime(Long.valueOf(FORMAT.format(ca.getTime())));
//							rd.setApTimezone(tz.getID());
//						} else {
							rd.setStartTime(da.getCustomStartTime());
							rd.setEndTime(da.getCustomEndTime());
//						}
						long timespace = da.getCustomEndTime()-da.getCustomStartTime();
						rd.setSample(calculateSampleValue(timespace));
					}
				}
			}
		} else {
			if (da.isBlnTypeRecurReport()){
				long[] aa = da.getScheduleStartTimePast(ca);
				rd.setStartTime(aa[0]);
				rd.setEndTime(aa[1]);
				rd.setSample(60*60);
			} else {
				ca.setTimeInMillis(currentTime -3600000L * 24);
				rd.setStartTime(ca.getTimeInMillis()/3600000L*3600000L);
				rd.setEndTime(currentTime/3600000L*3600000L);
				rd.setSample(60*60);
			}
		}
	}
	
	public static int calculateSampleValue(long timerange){
		if(timerange<=3600000 * 2){
			return 60;
		} else if(timerange<=3600000L * 24 *2) {
			return 60*60;
		} else if(timerange<=3600000L * 24 * 35) {
			return 60*60*24;
		} else {
			return 60*60*24 *7;
//		} else if(timerange<=3600000L * 24 * 30 * 12) {
//			return 60*60*24 *7;
//		} else {
//			return -1;
		}
	}
	
    static class ImplQueryBo implements QueryBo {
		@Override
		public Collection<HmBo> load(HmBo bo) {
//			if (bo instanceof AhDashboard) {
//				AhDashboard p = (AhDashboard) bo;
//				if (p.getLocation()!=null) {
//					p.getLocation().getId();
//				}
//			}
			if (bo instanceof DashboardComponent) {
				DashboardComponent p = (DashboardComponent) bo;

				if (p.getComponentMetric()!=null) {
					p.getComponentMetric().getId();
					if(p.getComponentMetric().getComponentData()!=null) {
						p.getComponentMetric().getComponentData().size();
					}
				}
			}
			if (bo instanceof AhDashboardWidget) {
				AhDashboardWidget p = (AhDashboardWidget) bo;
//				if(p.getLocation()!=null) {
//					p.getLocation().getId();
//				}
				if (p.getWidgetConfig()!=null) {
					p.getWidgetConfig().getId();
					
					if (p.getWidgetConfig().getComponentMetric()!=null) {
						p.getWidgetConfig().getComponentMetric().getId();
						if(p.getWidgetConfig().getComponentMetric().getComponentData()!=null) {
							p.getWidgetConfig().getComponentMetric().getComponentData().size();
						}
					}
					
				}
				
				
			}
			return null;
		}
    }
    
    public static Set<String> getMetricsFromExpression(String expression) {
    	if (StringUtils.isBlank(expression)) {
    		return null;
    	}
    	
    	Set<String> result = new HashSet<>();
    	String str = expression;
    	int leftPos = str.indexOf('[');
		int rightPos = str.indexOf(']');
		while (StringUtils.isNotBlank(str) 
				&& leftPos >= 0) {
			if (rightPos - leftPos > 1) {
				result.add(str.substring(leftPos+1, rightPos));
				str = str.substring(rightPos+1);
			}
			if (StringUtils.isNotBlank(str)) {
				leftPos = str.indexOf('[');
				rightPos = str.indexOf(']');
			}
		}
		
		return result;
    }
    
    private static AhDashboard createDefaultTempDashboard(HmDomain owner) {
		AhDashboard da = new AhDashboard();
		da.setOwner(owner);
		da.setDaType(AhDashboard.DASHBOARD_TYPE_DASH);
		da.setDefaultFlag(false);
		da.setBlnNullNewDa(true);
		
		return da;
	}
    
    public static abstract class RequestDataModifier {
    	public void modify(AhDashboardWidget widget, AhDashboard da) {}
    	public void modify(AhDashboard da, boolean blnAnyway) {}
    	public void modify(DashboardComponent dc) {}
    }
}