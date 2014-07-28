package com.ah.bo.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.bo.report.annotation.AhReportConfig;
import com.ah.bo.report.impl.BandwidthOverTimeReport;
import com.ah.bo.report.impl.BandwidthUsageBySsidReport;
import com.ah.bo.report.impl.ClientDistributionBySsidReport;
import com.ah.bo.report.impl.ClientTypeDistributionReport;
import com.ah.bo.report.impl.ClientsNumberByAPReport;
import com.ah.bo.report.impl.ClientsNumberOverTimeReport;
import com.ah.bo.report.impl.ClientsTopNReport;
import com.ah.bo.report.impl.DeviceBandwidthUsageReport;
import com.ah.bo.report.impl.SLAClientReport;
import com.ah.bo.report.impl.SLADeviceReport;
import com.ah.bo.report.impl.UsageDeviceErrorReport;
import com.ah.bo.report.impl.UsageSsidClientBandwidthReport;
import com.ah.bo.report.impl.UsageSsidClientReport;
import com.ah.util.bo.report.builder.AhReportBuilder;
import com.ah.util.bo.report.builder.DefaultNewReportBuilder;

import edu.emory.mathcs.backport.java.util.Arrays;

public class AhReportContainer {
	//private static final Tracer log = new Tracer(AhReportContainer.class
	//		.getSimpleName());
	private static void prepareReports() {
		// example report chart
		reportClasses.add(ExampleTestReport.class);
		reportClasses.add(ExampleDatetimeReport.class);
		
		// businesses
		reportClasses.add(BandwidthOverTimeReport.class);
		reportClasses.add(UsageSsidClientReport.class);
		reportClasses.add(UsageSsidClientBandwidthReport.class);
		reportClasses.add(DeviceBandwidthUsageReport.class);
		reportClasses.add(BandwidthUsageBySsidReport.class);
		reportClasses.add(ClientsNumberByAPReport.class);
		reportClasses.add(ClientsNumberOverTimeReport.class);
		reportClasses.add(ClientDistributionBySsidReport.class);
		reportClasses.add(UsageDeviceErrorReport.class);
		reportClasses.add(ClientTypeDistributionReport.class);
		reportClasses.add(SLADeviceReport.class);
		reportClasses.add(SLAClientReport.class);
		reportClasses.add(ClientsTopNReport.class);
	}
	
	
	public static Map<Long, Class<? extends AhAbstractReport>> mapOfReportIdAndClass;
	public static Map<Long, Class<? extends AhReportBuilder>> mapOfReportIdAndBuilder;
	public static Class<? extends AhReportBuilder> DEFAULT_AH_REPORT_BUILDER = DefaultNewReportBuilder.class;
	
	public static List<Class<? extends AhAbstractReport>> reportClasses = 
		new ArrayList<Class<? extends AhAbstractReport>>();
	
	public static Map<Long, AhReportConfigElement> mapOfReportIdAndConfig;
	
	@SuppressWarnings("unchecked")
	public static void reScanAhReportConfigs() {
		if (reportClasses.size() > 0) {
			clearReports();
		}
		prepareReports();
		
		if (mapOfReportIdAndClass != null) {
			mapOfReportIdAndClass.clear();
		} else {
			mapOfReportIdAndClass = new HashMap<Long, Class<? extends AhAbstractReport>>(reportClasses.size());
		}
		if (mapOfReportIdAndBuilder != null) {
			mapOfReportIdAndBuilder.clear();
		} else {
			mapOfReportIdAndBuilder = new HashMap<Long, Class<? extends AhReportBuilder>>(reportClasses.size());
		}
		if (mapOfReportIdAndConfig != null) {
			mapOfReportIdAndConfig.clear();
		} else {
			mapOfReportIdAndConfig = new HashMap<Long, AhReportConfigElement>(reportClasses.size());
		}
		
		for (Class<? extends AhAbstractReport> rpClass : reportClasses) {
			AhReportConfig rpConfig = rpClass.getAnnotation(AhReportConfig.class);
			if (rpConfig != null) {
				mapOfReportIdAndClass.put(rpConfig.id(), rpClass);
				if (rpConfig.builder() != DEFAULT_AH_REPORT_BUILDER
						&& rpConfig.builder().getInterfaces() != null
						&& rpConfig.builder().getInterfaces().length > 0) {
					if(Arrays.asList(rpConfig.builder().getInterfaces()).contains(AhReportBuilder.class)) {
						mapOfReportIdAndBuilder.put(rpConfig.id(), (Class<? extends AhReportBuilder>)rpConfig.builder());
					}
				}
				AhReportConfigElement rpConfigEl = new AhReportConfigElement();
				mapOfReportIdAndConfig.put(rpConfig.id(), rpConfigEl);
				rpConfigEl.setBlnGroupCalEnabled(rpConfig.groupCalEnabled());
			}
		}
	}
	
	public static Class<? extends AhAbstractReport> getReportClassById(Long id) {
		if (mapOfReportIdAndClass != null
				&& mapOfReportIdAndClass.containsKey(id)) {
			return mapOfReportIdAndClass.get(id);
		}
		return null;
	}
	
	public static Class<? extends AhReportBuilder> getReportBuilderClassById(Long id) {
		if (mapOfReportIdAndBuilder != null
					&& mapOfReportIdAndBuilder.containsKey(id)) {
				return mapOfReportIdAndBuilder.get(id);
		} 
		return DEFAULT_AH_REPORT_BUILDER;
	}
	
	public static boolean isContainReportId(Long id) {
		if (mapOfReportIdAndClass != null
				&& mapOfReportIdAndClass.containsKey(id)) {
			return true;
		}
		return false;
	}
	
	public static AhReportConfigElement getReportConfig(Long id) {
		if (mapOfReportIdAndConfig != null
				&& mapOfReportIdAndConfig.containsKey(id)) {
			return mapOfReportIdAndConfig.get(id);
		}
		return null;
	}
	
	private static void clearReports() {
		reportClasses.clear();
	}
}
