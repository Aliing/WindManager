package com.ah.be.performance;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.jfree.chart.JFreeChart;

import com.ah.be.app.DebugUtil;
import com.ah.be.app.HmBeLogUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.performance.AhNewReport;
import com.ah.bo.report.AhAbstractReport;
import com.ah.bo.report.AhReportProperties;
import com.ah.bo.report.impl.BandwidthOverTimeReport;
import com.ah.bo.report.impl.ClientDistributionBySsidReport;
import com.ah.bo.report.impl.ClientsTopNReport;
import com.ah.bo.report.impl.SLAClientReport;
import com.ah.bo.report.impl.SLADeviceReport;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.monitor.HeaderFooterPage;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.report.AhNewReportUtil;
import com.ah.util.bo.report.AhReportInvoker;
import com.ah.util.bo.report.AhReportInvokerInterface;
import com.ah.util.bo.report.AhReportRequest;
import com.ah.util.bo.report.freechart.AhFreechartWrapper;
import com.ah.util.bo.report.freechart.AhNullFreechartWrapper;
import com.ah.util.datetime.AhDateTimeUtil;
//import com.lowagie.awt.PdfGraphics2D;
//import com.lowagie.text.BaseColor;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 *
 *@filename		BeNetworkReportScheduleImpl.java
 *@version		V1.0.0.0
 *@author		Fisher
 *@createtime	2008-5-6 11:12:23
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 * modify history*
 */
public class BeNetworkReportScheduleImpl {
	private static final Tracer log = new Tracer(BeNetworkReportScheduleImpl.class.getSimpleName());
	
	private static List<String> prepareReportsToBeExported(AhNewReport report, boolean schedule, TimeZone tz) {
		List<String> result = new ArrayList<String>();
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_BANDWIDTH_USAGE_OF_DEVICE, "");
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_BANDWIDTH_OVER_TIME, BandwidthOverTimeReport.SUB_TYPE_OUTBOUND);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_BANDWIDTH_OVER_TIME, BandwidthOverTimeReport.SUB_TYPE_INBOUND);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_BANDWIDTH_USAGE_BY_SSID, "");
		
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENTS_NUMBER_BY_AP, "");
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENTS_NUMBER_OVER_TIME, "");
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENT_DISTRIBUTION_BY_SSID, "");
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENT_DISTRIBUTION_BY_SSID, ClientDistributionBySsidReport.CLIENT_DISTRIBUTION_GHZ24);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENT_DISTRIBUTION_BY_SSID, ClientDistributionBySsidReport.CLIENT_DISTRIBUTION_GHZ50);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENT_TYPE_DISTRIBUTION, "");
		if (ClientsTopNReport.isCalForTheRequest(report)) {
			AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_CLIENTS_TOPN, "");
		}
		
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SSID_CLIENTS_OVER_TIME, "");
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SSID_CLIENTS_BANDWIDTH, "");
		
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SLA_DEVICE, SLADeviceReport.SLA_TYPE_THROUGHPUT);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SLA_DEVICE, SLADeviceReport.SLA_TYPE_AIRTIME);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SLA_DEVICE, SLADeviceReport.SLA_TYPE_CRCERROR);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SLA_CLIENT, SLAClientReport.SLA_TYPE_THROUGHPUT);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SLA_CLIENT, SLAClientReport.SLA_TYPE_AIRTIME);
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_SLA_CLIENT, SLAClientReport.SLA_TYPE_HEALTH);
		
		AhNewReportUtil.getReportMarkWithSubType(result, AhReportProperties.REPORT_DEVICE_ERROR, "");
		
		return result;
	}

	public static boolean excutePerformance(AhNewReport report, boolean schedule, TimeZone tz) {
		if (schedule) {
			File tmpFileDir = new File(BeNetworkReportScheduleModule.fileDirPath + File.separator
					+ report.getOwner().getDomainName());
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
		} else {
			File tmpFileDir = new File(BeNetworkReportScheduleModule.fileDirPathCurrent + File.separator
					+ report.getOwner().getDomainName());
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
		}
		return setPdfReport(report, schedule, tz);

	}
	
	private static boolean setPdfReport(AhNewReport report, boolean schedule, TimeZone tz){
		try {
			prepareSearchDevices(report);
			int supportApCount = 200;
			List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class,
					null, null);
			if (!list.isEmpty()) {
				supportApCount = list.get(0).getMaxSupportAp();
			}
			if (report.getApMacList()!=null && report.getApMacList().size()>supportApCount) {
				generateAuditLog(schedule, HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.cannot.run.network.summary.report",report.getName()) + supportApCount + ".",report.getOwner());
				return false;
			}
			generalCurrentPdfFile(report, schedule, tz);
			return true;
		} catch (Exception e) {
			log.error(e);
			return false;
		}
	}
	
	public static void generateAuditLog(boolean schedule, short arg_Status, String arg_Comment, HmDomain dm) {
		HmAuditLog log = new HmAuditLog();
		log.setStatus(arg_Status);
		log.setOpeationComment(arg_Comment);
		log.setHostIP("127.0.0.1");
		try {
			if (schedule) {
				log.setUserOwner("admin");
			} else {
				HmUser us = BaseAction.getSessionUserContext();
				log.setUserOwner(us!=null? us.getUserName(): "admin");
			}
			log.setOwner(dm);
			log.setLogTimeStamp(System.currentTimeMillis());
			log.setLogTimeZone(dm != null ? dm.getTimeZoneString()
					: TimeZone.getDefault().getID());

			BeLogTools.info(HmLogConst.M_GUIAUDIT, "[" + log.getHostIP() + " "
					+ log.getOwner() + "." + log.getUserOwner() + "]" + " "
					+ arg_Comment + ":" + arg_Status);

			QueryUtil.createBo(log);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void removeCurrentDomainFile(AhNewReport report){
		String[] string_Path_Array = new String[3];
		string_Path_Array[0] = "bash";
		string_Path_Array[1] = "-c";
		string_Path_Array[2] = "cd " + BeNetworkReportScheduleModule.fileDirPathCurrent + File.separator
				+ report.getOwner().getDomainName() + " && rm -rf *";
		try {
			Process process = Runtime.getRuntime().exec(string_Path_Array);
			// wait restart network end
			process.waitFor();
			if (process.exitValue() > 0) {
				String errorMsg = "remove pdf file error in report current";
				DebugUtil
			      .performanceDebugWarn(
			       "BeNetworkReportScheduleImpl.removeCurrentDomainFile():" + errorMsg);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
						HmSystemLog.FEATURE_MONITORING, errorMsg);
			}
		} catch (Exception e) {
			DebugUtil
		      .performanceDebugWarn(
		       "BeNetworkReportScheduleImpl.removeCurrentDomainFile():" + e.getMessage());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
					HmSystemLog.FEATURE_MONITORING, e.getMessage());
		}
	}
	
	private static synchronized boolean generalCurrentPdfFile(AhNewReport report,boolean schedule,TimeZone tz) {
		List<String> reportTitle = new ArrayList<String>();
		if (!schedule) {
			removeCurrentDomainFile(report);
		}
		Document document = new Document(PageSize.A4,40,40,72,72);
		try {
			Calendar ca = Calendar.getInstance();
			ca.setTimeZone(tz);
			long caTime = ca.getTimeInMillis();
			
			List<String> reportsToDealIdWithSubType = prepareReportsToBeExported(report, schedule, tz);
			List<AhFreechartWrapper> reportCharts = prepareExportedJFreeCharts(report, reportsToDealIdWithSubType);
			if (reportCharts != null
					&& !reportCharts.isEmpty()) {
				for (String reportMark : reportsToDealIdWithSubType) {
					AhFreechartWrapper chartWrapper = getCertainReportChart(reportCharts, 
							AhNewReportUtil.getIdFromReportMark(reportMark), 
							AhNewReportUtil.getSubTypeFromReportMark(reportMark));
					if (chartWrapper != null) {
						reportTitle.add(chartWrapper.getTitle());
					}
				}
			}
			
			String pdfPath=null;
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			sf.setTimeZone(report.getTz());
			String mailFileName;
			mailFileName = "NetworkSummary" + "_"  + report.getName() + "_" + sf.format(new Date()) + ".pdf";
			
			if (schedule) {
				pdfPath = BeNetworkReportScheduleModule.fileDirPath + File.separator
				+ report.getOwner().getDomainName() + File.separator + mailFileName;
			} else {
				pdfPath = BeNetworkReportScheduleModule.fileDirPathCurrent + File.separator
				+ report.getOwner().getDomainName() + File.separator + mailFileName;
			}
			
			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
			
			writer.setPageEvent(new HeaderFooterPage(tz));
			
			document.open();
			String fontPath = FontFactory.TIMES_ROMAN;
			Font fontHeadTitle = FontFactory.getFont(fontPath, 18, Font.BOLD, new Color(0,0,0));
			Font fontHeadTitleGray = FontFactory.getFont(fontPath, 18, Font.BOLD, new Color(145,145,145));
			Font fontTitle = FontFactory.getFont(fontPath, 16, Font.BOLD,new Color(9,35,95));
			Font fontSubTitle = FontFactory.getFont(fontPath, 14, Font.BOLD,new Color(40,70,140));
			Font fontSubBlackTitle = FontFactory.getFont(fontPath , 12, Font.BOLD);
			Font textFonts = FontFactory.getFont(fontPath, 11);
			Font textRedFonts = FontFactory.getFont(fontPath, 11,new Color(255,0,0));
			Font textGreenFonts = FontFactory.getFont(fontPath, 11,new Color(0,255,0));
			Font textLinkFonts = FontFactory.getFont(fontPath, 10,new Color(20,150,200));
			Font textItalicFonts = FontFactory.getFont(fontPath, 11, Font.ITALIC);
			Font commonFonts = FontFactory.getFont(fontPath, 9);
			Color tableTitleColor = new Color(190,200,150);
			
			float tablePadding=5;

			Paragraph graph = new Paragraph();
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			graph.setFont(fontHeadTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Network Summary Report \n");
			graph.setFont(textFonts);
			ca.setTimeInMillis(caTime);
			String startTime = AhDateTimeUtil.getSpecifyDateTime(report.getRunStartTime(ca),tz);
			ca.setTimeInMillis(caTime);
			String endTime = AhDateTimeUtil.getSpecifyDateTime(report.getRunEndTime(ca),tz);
			graph.add("Report period: " + startTime+" to " + endTime + "\n");
			document.add(graph);
			
//			SimpleDateFormat l_sdf = new SimpleDateFormat("MMM d, yyyy, h:mma");
//			l_sdf.setTimeZone(tz);
//			ca.setTimeInMillis(caTime);
//			ca.setTimeInMillis(report.getRunStartTime(ca));
//			String startTime = l_sdf.format(ca.getTime());
//			ca.setTimeInMillis(caTime);
//			ca.setTimeInMillis(report.getRunEndTime(ca));
//			String endTime = l_sdf.format(ca.getTime());
//			graph.add("Report period: " + startTime+" to " + endTime + "\n");
			
			//Summary
//			graph = new Paragraph();
//			graph.setSpacingAfter(15f);
//			graph.setSpacingBefore(1f);
//			graph.setFont(fontSubBlackTitle);
//			graph.setAlignment(Element.ALIGN_LEFT);
//			graph.add("Summary ");
//			graph.setFont(textItalicFonts);
//			
//			graph.add("Payment Card Industry Data Security Standard PCI DSS" + 
//				" compliance is required for all merchants and service providers that store," + 
//				" process, or transmit payment cardholder data. To achieve compliance," + 
//				" merchants and service providers must adhere to the Payment Card Industry" + 
//				" Data Security Standard." + "\n");
//			document.add(graph);
			
			graph = new Paragraph();
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			graph.setFont(fontHeadTitleGray);
			graph.setAlignment(Element.ALIGN_LEFT);
			
			String location = "All";
			if (report.getLocation()!=null) {
				location = report.getLocation().getMapName();
			}
			graph.add("Group Topology: " + location);
			document.add(graph);

			float[] widths = {0.4f, 0.5f};
			PdfPTable table = new PdfPTable(widths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			PdfPCell cell = new PdfPCell(new Phrase("Number of Devices in Group",fontSubBlackTitle));
			cell.setBorder(0);
			cell.setPadding(tablePadding);
			table.addCell(cell);
			cell = new PdfPCell(new Phrase("Number of Devices per SSID in Group",fontSubBlackTitle));
			cell.setPadding(tablePadding);
			cell.setBorder(0);
			table.addCell(cell);
			
			PdfPTable tableSub = new PdfPTable(widths);
			tableSub.setWidthPercentage(100);
			tableSub.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			for(String key: report.getApTypeCountList().keySet()) {
				String model = MgrUtil.getEnumString("enum.hiveAp.model." + key);
				cell = new PdfPCell(new Phrase(model,textFonts));
				cell.setPaddingLeft(tablePadding);
				cell.setPaddingBottom(0);
				cell.setPaddingTop(0);
				cell.setBorder(0);
				tableSub.addCell(cell);
				
				int modelCount = report.getApTypeCountList().get(key).size();
				cell = new PdfPCell(new Phrase(String.valueOf(modelCount),textFonts));
				cell.setPaddingLeft(tablePadding);
				cell.setPaddingBottom(0);
				cell.setPaddingTop(0);
				cell.setBorder(0);
				tableSub.addCell(cell);
			}
			
			cell = new PdfPCell();
			cell.setPaddingLeft(tablePadding);
			cell.setPaddingBottom(0);
			cell.setPaddingTop(0);
			cell.setBorder(0);
			cell.addElement(tableSub);
			table.addCell(cell);
			
			
			tableSub = new PdfPTable(widths);
			tableSub.setWidthPercentage(100);
			tableSub.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			for(String key: report.getSsidCountList().keySet()) {
				cell = new PdfPCell(new Phrase(key,textFonts));
				cell.setPaddingLeft(tablePadding);
				cell.setPaddingBottom(0);
				cell.setPaddingTop(0);
				cell.setBorder(0);
				tableSub.addCell(cell);
				
				int modelCount = report.getSsidCountList().get(key).size();
				cell = new PdfPCell(new Phrase(String.valueOf(modelCount),textFonts));
				cell.setPaddingLeft(tablePadding);
				cell.setPaddingBottom(0);
				cell.setPaddingTop(0);
				cell.setBorder(0);
				tableSub.addCell(cell);
			}
			
			cell = new PdfPCell();
			cell.setPaddingLeft(tablePadding);
			cell.setPaddingBottom(0);
			cell.setPaddingTop(0);
			cell.setBorder(0);
			cell.addElement(tableSub);
			table.addCell(cell);
			
			document.add(table);
			
			document.add(new Paragraph("\n"));
			
			float[] linkwidths = {0.4f, 0.5f};
			table = new PdfPTable(linkwidths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
	
			cell = new PdfPCell(new Phrase("Selected Reports: ",fontSubBlackTitle));
			cell.setPaddingLeft(tablePadding);
			cell.setPaddingBottom(0);
			cell.setPaddingTop(0);
			cell.setBorder(0);
			table.addCell(cell);
			cell = new PdfPCell();
			cell.setPaddingLeft(tablePadding);
			cell.setPaddingBottom(0);
			cell.setPaddingTop(0);
			cell.setBorder(0);
			table.addCell(cell);
			for (int i=0; i<reportTitle.size(); i++) {
				Chunk ck = new Chunk(reportTitle.get(i), textLinkFonts).setLocalGoto(String.valueOf(i));
				ck.setUnderline(0.1f, -2);
				cell = new PdfPCell();
				cell.setBorder(0);
				if (i%2==0) {
					cell.setPaddingLeft(tablePadding + 10);
				} else {
					cell.setPaddingLeft(tablePadding);
				}
				cell.setPaddingBottom(0);
				cell.setPaddingTop(0);
				cell.addElement(ck);
				table.addCell(cell);
			}
			if (reportTitle.size()%2==1) {
				cell = new PdfPCell();
				cell.setBorder(0);
				cell.setPaddingLeft(tablePadding);
				cell.setPaddingBottom(0);
				cell.setPaddingTop(0);
				table.addCell(cell);
			}
			
			document.add(table);
			int curChartIdx = 0;
			int validChartCount = 0;
			for (String reportMark : reportsToDealIdWithSubType) {
				if (validChartCount == 0) {
					document.newPage();
				}
				validChartCount++;
				
				int paddingTop = -6;
				if (validChartCount == 2) {
					paddingTop = 14;
				}
				PdfTemplate tp = PdfTemplate.createTemplate(writer, document.right()-40, paddingTop);
				if (validChartCount > 1 && validChartCount <= 2){
					tp.setColorStroke(Color.BLACK);
					tp.setLineWidth(0.5f);
					tp.moveTo(0,7);
					tp.lineTo(document.right(),7);
					tp.stroke();
					tp.sanityCheck();
				}
				Image bbbb = Image.getInstance(tp);
	    		document.add(bbbb);
				document.add(new Chunk(reportTitle.get(curChartIdx >= reportTitle.size() ? reportTitle.size()-1 : curChartIdx),fontHeadTitle).setLocalDestination(String.valueOf(curChartIdx)));
				
				AhFreechartWrapper chartWrapper = getCertainReportChart(reportCharts, 
						AhNewReportUtil.getIdFromReportMark(reportMark), 
						AhNewReportUtil.getSubTypeFromReportMark(reportMark));
				
				// add report summary
				if (chartWrapper != null 
						&& !StringUtils.isBlank(chartWrapper.getSummary())) {
					graph = new Paragraph();
					graph.setSpacingAfter(2f);
					graph.setSpacingBefore(1f);
					graph.setFont(fontSubBlackTitle);
					graph.setAlignment(Element.ALIGN_LEFT);
					graph.add("Summary ");
					graph.setFont(textItalicFonts);
					graph.add(chartWrapper.getSummary() + "\n");
					document.add(graph);
				}
				
				if (chartWrapper != null 
						&& !(chartWrapper instanceof AhNullFreechartWrapper)) {
					JFreeChart chart = chartWrapper.getFreeChart();
					tp = PdfTemplate.createTemplate(writer, document.right()-40, 260);
//					PdfGraphics2D g2d = new PdfGraphics2D(tp, document.right()-40, 260, true);
					Graphics2D g2d = tp.createGraphicsShapes(document.right()-40, 260);
			    	Rectangle2D r2d = new Rectangle2D.Double(0, 0, document.right()-40, 260);
					chart.draw(g2d, r2d);
					g2d.dispose();
					tp.sanityCheck();
					bbbb = Image.getInstance(tp);
					table = new PdfPTable(1);
					table.setWidthPercentage(100);
					cell = new PdfPCell();
					cell.setBorder(0);
					cell.setImage(bbbb);
					table.addCell(cell);
					document.add(table);
				} else {
					table = new PdfPTable(1);
					table.setWidthPercentage(100);
					cell = new PdfPCell(new Phrase("There is no data for this report.",fontSubBlackTitle));
					cell.setPadding(tablePadding);
					cell.setBorder(0);
					table.addCell(cell);
					document.add(table);
				}
				curChartIdx++;
				
				if (validChartCount == 2) {
					validChartCount = 0;
				}
			}

        }
        catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
        }
        document.close();
        return true;
	}
	
	private static void prepareSearchDevices(AhNewReport report){
		if (report.isForSample()) {
			report.setApNameList(new HashSet<String>());
			report.setApMacList(new HashSet<String>());
			report.setSsidCountList(new HashMap<String, Set<String>>());
			report.setApTypeCountList(new HashMap<String, Set<String>>());
			Set<String> temp = new HashSet<String>();
			temp.add("aa");
			report.getSsidCountList().put("Testing", temp);
			report.getSsidCountList().put("Employee", temp);
			report.getApTypeCountList().put("6", temp);
			temp = new HashSet<String>();
			temp.add("aa");
			temp.add("bb");
			report.getSsidCountList().put("Guest", temp);
			temp = new HashSet<String>();
			temp.add("aa");
			temp.add("bb");
			temp.add("cc");
			report.getSsidCountList().put("Contractor", temp);
			report.getSsidCountList().put("Partner", temp);
			report.getApTypeCountList().put("3", temp);
			
			temp = new HashSet<String>();
			temp.add("a1");
			temp.add("a2");
			temp.add("a3");
			temp.add("a4");
			temp.add("a5");
			temp.add("a6");
			report.getApTypeCountList().put("5", temp);
			return;
		}
		StringBuffer serSql = new StringBuffer();
		if (report.getSsidName()!=null && !report.getSsidName().equals("All")) {
			serSql.append("select ap.hostname,ap.hiveApModel,ssid.ssid,ap.macAddress from hive_ap ap, config_template_ssid temp, ssid_profile ssid where ap.owner=")
			.append(report.getOwner().getId())
			.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY)
			.append(" and ap.managestatus=").append(HiveAp.STATUS_MANAGED)
			.append(" and temp.ssid_profile_id = ssid.id and ap.template_id= temp.config_template_id ")
			.append(" and ssid.ssid='").append(NmsUtil.convertSqlStr(report.getSsidName())).append("'");
			
		} else {
			serSql.append("select distinct ap.hostname,ap.hiveApModel,ssid.ssid,ap.macAddress from hive_ap ap inner join config_template_ssid temp on ")
				.append(" ap.owner=").append(report.getOwner().getId())	
			.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY)
			.append(" and ap.managestatus=").append(HiveAp.STATUS_MANAGED)
			.append(" and ap.template_id= temp.config_template_id ");
		}
		
		if (report.getLocation()!=null && report.getLocation().getId()>0) {
			Long mapId = report.getLocation().getId();
			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(mapId);
			StringBuffer tmpId = new StringBuffer();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			serSql.append(" and ap.map_container_id in (").append(tmpId.toString()).append(")");
		}
		
		if (report.getSsidName()==null || report.getSsidName().equals("All")) {
			serSql.append(" left join ssid_profile ssid on temp.ssid_profile_id = ssid.id");
		}
		
		serSql.append(" order by ap.hiveApModel, ap.hostname");
		
		List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString());

		Set<String> apNameList = new HashSet<String>();
		Set<String> apMacList = new HashSet<String>();
		Map<String, Set<String>> apTypeLst = new HashMap<String, Set<String>>();
		Map<String, Set<String>> ssidTypeLst = new HashMap<String, Set<String>>();

		for(Object oneObj: deviceList){
			Object[] oneRec = (Object[]) oneObj;
			String apName = oneRec[0].toString();
			String apType = oneRec[1].toString();
			String ssidName = oneRec[2]==null? null: oneRec[2].toString();
			apNameList.add(apName);
			apMacList.add(oneRec[3].toString());
			if (apTypeLst.get(apType)==null) {
				Set<String> tmp = new HashSet<String>();
				tmp.add(apName);
				apTypeLst.put(apType, tmp);
			} else {
				apTypeLst.get(apType).add(apName);
			}
			
			if (ssidName!=null) {
				if (ssidTypeLst.get(ssidName)==null) {
					Set<String> tmp = new HashSet<String>();
					tmp.add(apName);
					ssidTypeLst.put(ssidName, tmp);
				} else {
					ssidTypeLst.get(ssidName).add(apName);
				}
			}
		}
		
		report.setApNameList(apNameList);
		report.setApMacList(apMacList);
		report.setSsidCountList(ssidTypeLst);
		report.setApTypeCountList(apTypeLst);
	}

	private static List<AhFreechartWrapper> prepareExportedJFreeCharts(AhNewReport report, List<String> reportsToDeal) {
		AhReportRequest rp = new AhReportRequest();

		rp.setIdsWithSubTypes(reportsToDeal);
		rp.setDomainId(report.getOwner().getId());
		rp.setTimeZone(report.getTz());
		rp.setDataSource(report);
		
		if (rp.getPeriodType() <= 0
				&& report != null) {
			rp.setPeriodType(report.getReportPeriod());
		}
		
		try {
			AhReportInvokerInterface reportInvoker = new AhReportInvoker();
			reportInvoker.init(rp);
			reportInvoker.invoke();
			
			return reportInvoker.getExportedJFreeCharts();
		} catch (Exception e) {
			log.error("Failed to generate free charts", e);
		}
		
		return null;
	}
	
	private static AhFreechartWrapper getCertainReportChart(List<AhFreechartWrapper> charts, Long reportId, String subType) {
		if (charts != null
				&& !charts.isEmpty()) {
			for (AhFreechartWrapper chart : charts) {
				if (reportId.equals(chart.getReportId())) {
					if (StringUtils.isBlank(subType)
							&& (StringUtils.isBlank(chart.getSubType())
									|| AhAbstractReport.GROUP_MARK_DEFAULT.equals(chart.getSubType()))) {
						return chart;
					} else {
						if (subType.equals(chart.getSubType())) {
							return chart;
						}
					}
				}
			}
		}
		
		return null;
	}
	
}