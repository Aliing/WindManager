package com.ah.util.bo.dashboard;

import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import com.ah.be.performance.BeNetworkReportScheduleModule;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.dashboard.AhDashBoardGroup;
import com.ah.bo.dashboard.AhDashboard;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryCertainBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.report.AhReportElement;
import com.ah.bo.report.AhSeries;
import com.ah.bo.report.AhSeriesData;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.ui.actions.monitor.NewHeaderFooterPage;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.dashboard.DaDataCalculateUtil.MetricGroupInfo;
import com.ah.util.bo.report.freechart.AhFreechartWrapper;
import com.ah.util.bo.report.freechart.AhNullFreechartWrapper;
import java.awt.Graphics2D;
import java.awt.Color;
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

public class DaExportPdfUtil {
	private static final Tracer log = new Tracer(DaExportPdfUtil.class.getSimpleName());
	
	public final static String fileDirPath = "/tmp/dapdf";
	public final static String fileDirPathCurrent = "/tmp/dacurpdf";
	
	private final static String fontPath = FontFactory.TIMES_ROMAN;
	private final static Font fontHeadTitle = FontFactory.getFont(fontPath, 18, Font.BOLD, new Color(0,0,0));
	private final static Font fontHeadTitleGray = FontFactory.getFont(fontPath, 15, Font.BOLD, new Color(145,145,145));
	private final static Font fontTitle = FontFactory.getFont(fontPath, 16, Font.BOLD,new Color(9,35,95));
	private final static Font fontSubTitle = FontFactory.getFont(fontPath, 14, Font.BOLD,new Color(40,70,140));
	private final static Font fontSubBlackTitle = FontFactory.getFont(fontPath , 12, Font.BOLD);
	private final static Font textFonts = FontFactory.getFont(fontPath, 11);
	private final static Font textRedFonts = FontFactory.getFont(fontPath, 11,new Color(255,0,0));
	private final static Font textGreenFonts = FontFactory.getFont(fontPath, 11,new Color(0,255,0));
	private final static Font textLinkFonts = FontFactory.getFont(fontPath, 10,new Color(20,150,200));
	private final static Font textItalicFonts = FontFactory.getFont(fontPath, 11, Font.ITALIC);
	private final static Font commonFonts = FontFactory.getFont(fontPath, 9);
	private final static Color tableTitleColor = new Color(190,200,150);
	private final static float tablePadding=5;
	
	public static boolean excutePerformance(DaExportedData reportData, boolean schedule, HmDomain owner, TimeZone tz) {
		return excutePerformance(reportData, schedule, owner, tz, null);
	}
	
	public static boolean excutePerformance(DaExportedData reportData, boolean schedule, HmDomain owner, TimeZone tz, 
			@SuppressWarnings("rawtypes") ExportPdfResponse response) {
		if (schedule) {
			File tmpFileDir = new File(fileDirPath + File.separator
					+ owner.getDomainName());
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
		} else {
			File tmpFileDir = new File(fileDirPathCurrent + File.separator
					+ owner.getDomainName());
			if (!tmpFileDir.exists()) {
				tmpFileDir.mkdirs();
			}
		}
		return setPdfReport(reportData, schedule, owner, tz, response);
	}
	
	private static boolean setPdfReport(DaExportedData reportData, boolean schedule, HmDomain owner, TimeZone tz, 
			@SuppressWarnings("rawtypes") ExportPdfResponse response){
		try {
			generalCurrentPdfFile(reportData, schedule, owner, tz, response);
			return true;
		} catch (Exception e) {
			log.error(e);
			return false;
		}
	}
	
	private static void removeCurrentDomainFile(HmDomain owner){
		String[] string_Path_Array = new String[3];
		string_Path_Array[0] = "bash";
		string_Path_Array[1] = "-c";
		string_Path_Array[2] = "cd " + fileDirPathCurrent + File.separator
				+ owner.getDomainName() + " && rm -rf *";
		try {
			Process process = Runtime.getRuntime().exec(string_Path_Array);
			// wait restart network end
			process.waitFor();
			if (process.exitValue() > 0) {
				String errorMsg = "remove pdf file error in dashboard current";
				DebugUtil
			      .performanceDebugWarn(
			       "DaExportPdfUtil.removeCurrentDomainFile():" + errorMsg);
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
						HmSystemLog.FEATURE_MONITORING, errorMsg);
			}
		} catch (Exception e) {
			DebugUtil
		      .performanceDebugWarn(
		       "DaExportPdfUtil.removeCurrentDomainFile():" + e.getMessage());
			HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MINOR,
					HmSystemLog.FEATURE_MONITORING, e.getMessage());
		}
	}
	
	private static synchronized boolean generalCurrentPdfFile(DaExportedData reportData, boolean schedule, HmDomain owner,TimeZone tz, 
			@SuppressWarnings("rawtypes") ExportPdfResponse response) {
		List<String> reportTitle = new ArrayList<String>();
		if (!schedule) {
			removeCurrentDomainFile(owner);
		}
		
		if (reportData == null
				|| reportData.getData() == null
				|| reportData.getData().isEmpty()) {
			return false;
		}
		
		reportData.sortData();
		if (reportData != null
				&& reportData.getData() != null) {
			for (DaExportedSingleData aData : reportData.getData()) {
				reportTitle.add(aData.getTitle());
			}
		}
		AhDashboard dashboard=reportData.getDashboard();
		OutputStream ostream = null;
		Document document = new Document(PageSize.A4,40,40,72,72);
		try {
			Calendar ca = Calendar.getInstance();
			ca.setTimeZone(tz);
			long caTime = ca.getTimeInMillis();
			
			String pdfPath=null;
			SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
			sf.setTimeZone(tz);
			String mailFileName;
			mailFileName = "Reporting" + "_"  + reportData.getDaName() + "_" + sf.format(new Date()) + ".pdf";
			
			if (schedule) {
				pdfPath = fileDirPath + File.separator
						+ owner.getDomainName() + File.separator + mailFileName;
			} else {
				pdfPath = fileDirPathCurrent + File.separator
						+ owner.getDomainName() + File.separator + mailFileName;
			}
			
			PdfWriter writer;
			if (response != null
					&& response instanceof ExportPdfStreamResponse) {
				ostream = new ByteArrayOutputStream();
				writer = PdfWriter.getInstance(document, ostream);
				((ExportPdfStreamResponse)response).setResponse(ostream, mailFileName);
			} else {
				if (response != null
						&& response instanceof ExportPdfFilePathResponse) {
					((ExportPdfFilePathResponse)response).setResponse(pdfPath, mailFileName);
				}
				writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
			}
			
			NewHeaderFooterPage headerFooter=new NewHeaderFooterPage(tz,owner,dashboard);
			writer.setPageEvent(headerFooter);
			
			document.open();
			
			float tablePadding=5;

			Paragraph graph = new Paragraph();
			graph.setSpacingAfter(15f);
			graph.setSpacingBefore(15f);
			graph.setFont(fontHeadTitle);
			graph.setAlignment(Element.ALIGN_LEFT);
			graph.add("Reporting \n");
			graph.setFont(textFonts);
			ca.setTimeInMillis(caTime);
			graph.add("Report period: " + dashboard.getDashPDFReportTimeString()+ "\n");
			document.add(graph);
			
			//Summary
			if (dashboard.getPdfSummary()!=null && !dashboard.getPdfSummary().equals("")) {
				graph = new Paragraph();
				graph.setSpacingAfter(15f);
				graph.setSpacingBefore(1f);
				graph.setFont(fontSubBlackTitle);
				graph.setAlignment(Element.ALIGN_LEFT);
				graph.add("Summary ");
				graph.setFont(textItalicFonts);
				
				graph.add(dashboard.getPdfSummary() + "\n");
				document.add(graph);
			}
			
			PdfPTable table;
			PdfPCell cell;
			
			if (dashboard.getDaType()==AhDashboard.DASHBOARD_TYPE_DASH
					|| dashboard.getDaType()==AhDashboard.DASHBOARD_TYPE_REPORT) {
				graph = new Paragraph();
				graph.setSpacingAfter(15f);
				graph.setSpacingBefore(15f);
				graph.setFont(fontHeadTitleGray);
				graph.setAlignment(Element.ALIGN_LEFT);
				
				String location = "All";
				if (dashboard.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP) 
						&& dashboard.getObjectId()!=null && Long.valueOf(dashboard.getObjectId())>0) {
					MapContainerNode locationTmp = QueryUtil.findBoById(MapContainerNode.class, Long.valueOf(dashboard.getObjectId()),
							new QueryCertainBo<MapContainerNode>(){
								@Override
								public Collection<HmBo> loadBo(MapContainerNode bo) {
									if (bo.getParentMap() != null) {
										bo.getParentMap().getId();
									}
									return null;
								}
							});
					if (locationTmp.getMapType() == MapContainerNode.MAP_TYPE_FLOOR 
							&& locationTmp.getParentMap() != null) {
						location = locationTmp.getParentMap().getMapName() + "_" + locationTmp.getMapName();
					} else {
						location = locationTmp.getMapName();
					}
				}
				graph.add("Topology: " + location);
				
				
				if (!dashboard.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP)) {
					Map<String,String> tagMaps =  DeviceTagUtil.getInstance().getClassifierCustomTag(dashboard.getOwner().getId());
					String subGroup = "";
					if (dashboard.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY) 
							&& dashboard.getObjectId()!=null && Long.valueOf(dashboard.getObjectId())>0) {
						List<?> tName = QueryUtil.executeQuery("select configName from " + ConfigTemplate.class.getSimpleName(),
								null, new FilterParams("id",Long.valueOf(dashboard.getObjectId())));
						if (tName!=null && !tName.isEmpty()) {
							subGroup = "Network Policy: " + tName.get(0).toString();
						}
					} else if (dashboard.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)){
						if (!dashboard.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)) {
							subGroup = tagMaps.get(DeviceTagUtil.CUSTOM_TAG1) + ": " +  dashboard.getObjectId();
						}
					} else if (dashboard.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)){
						if (!dashboard.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)) {
							subGroup = tagMaps.get(DeviceTagUtil.CUSTOM_TAG2) + ": " +  dashboard.getObjectId();
						}
					} else if (dashboard.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)){
						if (!dashboard.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)) {
							subGroup = tagMaps.get(DeviceTagUtil.CUSTOM_TAG3) + ": " +  dashboard.getObjectId();
						}
					}
					if (!subGroup.equals("")) {
						graph.add("         ");
						graph.add(subGroup);
					}
				}
				
				String subGroup = "";
				if (dashboard.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_SSID) 
						&& !dashboard.getFilterObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_SSID)) {
					subGroup = "Filter SSID: " + dashboard.getFilterObjectId();
				} else if (dashboard.getFilterObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL)
						&& !dashboard.getFilterObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_USERPROFILE_ALL)){
					subGroup = "Filter User Profile: " + dashboard.getFilterObjectId();
				}
				if (!subGroup.equals("")) {
					graph.add("           ");
					graph.add(subGroup);
				}
				
				document.add(graph);
	
				float[] widths = {0.4f, 0.5f};
				table = new PdfPTable(widths);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				cell = new PdfPCell(new Phrase("Number of Devices in Group",fontSubBlackTitle));
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
				prepareSearchDevices(dashboard);
				for(String key: dashboard.getApTypeCountList().keySet()) {
					String model = MgrUtil.getEnumString("enum.hiveAp.model." + key);
					cell = new PdfPCell(new Phrase(model,textFonts));
					cell.setNoWrap(true);
					cell.setPaddingLeft(tablePadding);
					cell.setPaddingBottom(0);
					cell.setPaddingTop(0);
					cell.setBorder(0);
					tableSub.addCell(cell);
					
					int modelCount = dashboard.getApTypeCountList().get(key).size();
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
				
				for(String key: dashboard.getSsidCountList().keySet()) {
					cell = new PdfPCell(new Phrase(key,textFonts));
					cell.setPaddingLeft(tablePadding);
					cell.setNoWrap(true);
					cell.setPaddingBottom(0);
					cell.setPaddingTop(0);
					cell.setBorder(0);
					tableSub.addCell(cell);
					
					int modelCount = dashboard.getSsidCountList().get(key).size();
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
			}
			
//			float[] widths = {0.4f, 0.5f};
//			PdfPTable table = new PdfPTable(widths);
//			table.setWidthPercentage(100);
//			table.setHorizontalAlignment(Element.ALIGN_LEFT);
			
			float[] linkwidths = {0.4f, 0.5f};
			table = new PdfPTable(linkwidths);
			table.setWidthPercentage(100);
			table.setHorizontalAlignment(Element.ALIGN_LEFT);
	
//			PdfPCell cell = new PdfPCell(new Phrase("Selected Reports: ",fontSubBlackTitle));
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
			DaExportedSingleData aDataPreview=null;
			if (reportData != null
					&& reportData.getData() != null) {
				for (DaExportedSingleData aData : reportData.getData()) {
					if (validChartCount == 0) {
						document.newPage();
					} else if (aDataPreview!=null && !aDataPreview.isNull() &&  "table".equals(aDataPreview.getChartType())) {
						if (!aDataPreview.getReportEl().isReportNull() 
								&& ((aDataPreview.isChartInverted() && aDataPreview.getReportEl().getSeries().size()>5)
								|| (!aDataPreview.isChartInverted() && aDataPreview.getReportEl().getSeries().get(0).getSafeData().size()>5))){
							document.newPage();
							validChartCount = 0;
						}
					}
					aDataPreview=aData;
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
					
					if (aData instanceof DaExportedSingleNullData) {
						table = new PdfPTable(1);
						table.setWidthPercentage(100);
						cell = new PdfPCell(new Phrase("There is no data for this report.",fontSubBlackTitle));
						cell.setPadding(tablePadding);
						cell.setBorder(0);
						table.addCell(cell);
						document.add(table);
					} else if (aData.isExportedAsJFreechart()) {
						AhFreechartWrapper chartWrapper = aData.getFreechartWrapper();
						
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
//							PdfGraphics2D g2d = new PdfGraphics2D(tp, document.right()-40, 260, true);
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
						
					} else if ("list".equals(aData.getChartType())) {
						addListDataToPdf(document, aData);
					} else if ("table".equals(aData.getChartType())) {
						if (aData.isChartInverted()) {
							addTableInvertedDataToPdf(document, aData.getReportEl());
						} else {
							addTableDataToPdf(document, aData.getReportEl());
						}
					}
					curChartIdx++;
					
					if (validChartCount == 2) {
						validChartCount = 0;
					}
				}
			}
        } catch(Exception ioe) {
            ioe.printStackTrace();
            return false;
        }
        document.close();
        return true;
	}
	
	private static void prepareSearchDevices(AhDashboard report){
		StringBuffer serSql = new StringBuffer();

		serSql.append("select distinct ap.hostname,ap.hiveApModel,ssid.ssid,ap.macAddress,ssid.id from hive_ap ap left join config_template_ssid temp on ")
			.append(" ap.owner=").append(report.getOwner().getId())	
		.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY)
		.append(" and ap.managestatus=").append(HiveAp.STATUS_MANAGED)
		.append(" and ap.template_id= temp.config_template_id ")
		.append(" and temp.ssid_profile_id is not null ")
		.append(" left join ssid_profile ssid on temp.ssid_profile_id = ssid.id")
		.append(" where ap.owner=").append(report.getOwner().getId())
		.append(" and ap.devicetype!=").append(HiveAp.Device_TYPE_VPN_GATEWAY)
		.append(" and ap.managestatus=").append(HiveAp.STATUS_MANAGED);
		
		if (report.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_MAP) 
				&& report.getObjectId()!=null && Long.valueOf(report.getObjectId())>0) {
			Long mapId = Long.valueOf(report.getObjectId());
			Set<Long> mapIds =  BoMgmt.getMapMgmt().getContainerDownIds(mapId);
			StringBuffer tmpId = new StringBuffer();
			for(Long it: mapIds){
				if (tmpId.length()>0) {
					tmpId.append(",");
				}
				tmpId.append(it);
			}
			serSql.append(" and ap.map_container_id in (").append(tmpId.toString()).append(")");
		} else if (report.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_NETWORKPOLICY) 
				&& report.getObjectId()!=null && Long.valueOf(report.getObjectId())>0) {
			serSql.append(" and temp.config_template_id =").append(Long.valueOf(report.getObjectId()));
		} else if (report.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)){
			if (!report.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_ONE)) {
				serSql.append(" and ap.classificationTag1 ='").append(NmsUtil.convertSqlStr(report.getObjectId())).append("'");
			}
		} else if (report.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)){
			if (!report.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_TWO)) {
				serSql.append(" and ap.classificationTag2 ='").append(NmsUtil.convertSqlStr(report.getObjectId())).append("'");
			}
		} else if (report.getObjectType().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)){
			if (!report.getObjectId().equals(AhDashBoardGroup.DA_GROUP_TYPE_TAG_THREE)) {
				serSql.append(" and ap.classificationTag3 ='").append(NmsUtil.convertSqlStr(report.getObjectId())).append("'");
			}
		}
		serSql.append(" order by ap.hiveApModel, ap.hostname");
		
		List<?> deviceList = QueryUtil.executeNativeQuery(serSql.toString());

		Map<String, Set<String>> apTypeLst = new HashMap<String, Set<String>>();
		Map<String, Set<String>> ssidTypeLst = new HashMap<String, Set<String>>();
		
		Map<Long, String> ssidIdNameLst = new HashMap<Long, String>();
		Collection<Long> ssidIds = new ArrayList<Long>();
		for(Object oneObj: deviceList){
			Object[] oneRec = (Object[]) oneObj;
			String apName = oneRec[0].toString();
			String apType = oneRec[1].toString();
			String ssidName = oneRec[2]==null? null: oneRec[2].toString();
			Long ssidid = (oneRec[4]==null || oneRec[4].toString().equals(""))? null: Long.valueOf(oneRec[4].toString());
			if (apTypeLst.get(apType)==null) {
				Set<String> tmp = new HashSet<String>();
				tmp.add(apName);
				apTypeLst.put(apType, tmp);
			} else {
				apTypeLst.get(apType).add(apName);
			}
			
			if (ssidName!=null && !ssidName.equals("")) {
				if (ssidTypeLst.get(ssidName)==null) {
					Set<String> tmp = new HashSet<String>();
					tmp.add(apName);
					ssidTypeLst.put(ssidName, tmp);
				} else {
					ssidTypeLst.get(ssidName).add(apName);
				}
				
				if (ssidid!=null) {
					ssidIds.add(ssidid);
					ssidIdNameLst.put(ssidid, ssidName);
				}
			}
			
		}
		try {
			if (!ssidIds.isEmpty()) {
				List<?> openSsidList = QueryUtil.executeQuery("select ppskOpenSsid, id from " + SsidProfile.class.getSimpleName(), null, 
						new FilterParams("id in (:s1) and accessMode=:s2 and enablePpskSelfReg=:s3",
								new Object[]{ssidIds, SsidProfile.ACCESS_MODE_PSK, true}));
				if (!openSsidList.isEmpty()) {
					for (Object oneItem: openSsidList) {
						Object[] oneRec = (Object[]) oneItem;
						String ssidName = oneRec[0]==null? null: oneRec[0].toString();
						Long ssidid = (oneRec[1]==null || oneRec[1].toString().equals(""))? null: Long.valueOf(oneRec[1].toString());
						if (ssidid!=null && ssidName!=null) {
							ssidTypeLst.put(ssidName, ssidTypeLst.get(ssidIdNameLst.get(ssidid)));
						}
					}
				}
			}
		
		} catch (Exception e) {
			log.error(e);
		}
		
		report.setSsidCountList(ssidTypeLst);
		report.setApTypeCountList(apTypeLst);
	}
	
	
	private static void addTableDataToPdf(Document document, AhReportElement reportEl) throws Exception {
		if (reportEl == null
				|| reportEl.getSeries() == null
				|| reportEl.getSeries().isEmpty()) {
			return;
		}
		List<Object> categories = getCategories(reportEl);
		if (categories == null) {
			return;
		}
		
		float iWidth = 1.0f/(reportEl.getSeries().size()+1);
		Paragraph graph = new Paragraph();
		graph.setSpacingAfter(15f);
		graph.setSpacingBefore(15f);
		document.add(graph);
		float[] widths = new float[reportEl.getSeries().size()+1];
		for (int i = 0; i < reportEl.getSeries().size()+1; i++) {
			widths[i] = iWidth;
		}
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell cell;
		
		int itemSize = reportEl.getSeries().get(0).getData().size();
		
		cell = new PdfPCell(new Phrase(reportEl.getName(),textFonts));
		cell.setBackgroundColor(tableTitleColor);
		cell.setPadding(tablePadding);
		table.addCell(cell);
		for (AhSeries aSeries : reportEl.getSeries()) {
			Object value = aSeries.getName();
			if (value == null) {
				value = "";
			}
			cell = new PdfPCell(new Phrase(value.toString(), textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
		}
		
		Font currentFont = textFonts;
		int curCategoryPos = 0;
		String valueString = "";
		for (int i = 0; i < itemSize; i++) {
			Object categoryName = categories.get(curCategoryPos++);
			if (categoryName == null) {
				categoryName = "";
			}
			cell = new PdfPCell(new Phrase(categoryName.toString(),textFonts));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			for (AhSeries aSeries : reportEl.getSeries()) {
				AhSeriesData aSeriesData = aSeries.getData().get(i);
				Object value = aSeriesData.getValue();
				if (value == null) {
					value = "";
				}
				valueString = value.toString();
				if (StringUtils.isNotBlank(valueString)
						&& StringUtils.isNotBlank(aSeriesData.getUnit())) {
					valueString += aSeriesData.getUnit();
				}
				cell = new PdfPCell(new Phrase(valueString, currentFont));
				cell.setPadding(tablePadding);
				table.addCell(cell);
			}
		}
		
		document.add(table);
	}
	
	private static List<Object> getCategories(AhReportElement reportEl) {
		if (reportEl == null) {
			return null;
		}
		return reportEl.getJudgedCategories();
	}
	
	private static void addTableInvertedDataToPdf(Document document, AhReportElement reportEl) throws Exception {
		if (reportEl == null) {
			return;
		}
		List<Object> categories = getCategories(reportEl);
		if (categories == null) {
			return;
		}
		
		int itemSize = reportEl.getSeries().get(0).getData().size();
		
		float iWidth = 1.0f/(itemSize+1);
		Paragraph graph = new Paragraph();
		graph.setSpacingAfter(15f);
		graph.setSpacingBefore(15f);
		document.add(graph);
		float[] widths = new float[itemSize+1];
		for (int i = 0; i < itemSize+1; i++) {
			widths[i] = iWidth;
		}
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell cell;
		
		cell = new PdfPCell(new Phrase(reportEl.getName(),textFonts));
		cell.setBackgroundColor(tableTitleColor);
		cell.setPadding(tablePadding);
		table.addCell(cell);
		for (Object category : categories) {
			String categoryName = "";
			if (category != null) {
				categoryName = category.toString();
			}
			cell = new PdfPCell(new Phrase(categoryName,textFonts));
			cell.setBackgroundColor(tableTitleColor);
			cell.setPadding(tablePadding);
			table.addCell(cell);
		}
		
		Font currentFont = textFonts;
		
		String unitString = "";
		for (AhSeries aSeries : reportEl.getSeries()) {
			Object value = aSeries.getName();
			if (value == null) {
				value = "";
			}
			cell = new PdfPCell(new Phrase(value.toString(), currentFont));
			cell.setPadding(tablePadding);
			table.addCell(cell);
			for (int i = 0; i < itemSize; i++) {
				AhSeriesData aSeriesData = aSeries.getData().get(i);
				value = aSeriesData.getValue();
				if (value == null) {
					value = "";
				}
				if (StringUtils.isNotBlank(aSeriesData.getUnit())) {
					unitString = aSeriesData.getUnit();
				} else {
					unitString = "";
				}
				cell = new PdfPCell(new Phrase(value.toString() + unitString, currentFont));
				cell.setPadding(tablePadding);
				table.addCell(cell);
			}
		}
		
		document.add(table);
		
	}
	
	private static Object _getSeriesValueForList(AhSeriesData aSeriesData) {
		Object value = "";
		if (aSeriesData != null) {
			value = aSeriesData.getValue();
		}
		if (value == null) {
			value = "";
		}
		return value;
	}
	private static String _getSeriesUnitForList(AhSeriesData aSeriesData) {
		String unitString = "";
		if (aSeriesData != null
				&& StringUtils.isNotBlank(aSeriesData.getUnit())) {
			unitString = aSeriesData.getUnit();
		} else {
			unitString = "";
		}
		return unitString;
	}
	private static void addListDataToPdf(Document document, DaExportedSingleData aData) throws Exception {
		if (aData == null
				|| aData.getReportEl() == null) {
			return;
		}
		AhReportElement reportEl = aData.getReportEl();
		Paragraph graph = new Paragraph();
		graph.setSpacingAfter(15f);
		graph.setSpacingBefore(15f);
		document.add(graph);
		float[] widths = new float[]{0.45f, 0.55f};
		PdfPTable table = new PdfPTable(widths);
		table.setWidthPercentage(100);
		table.setHorizontalAlignment(Element.ALIGN_LEFT);
		PdfPCell cell;
		
		if (reportEl.getSeries() !=null 
				&& !reportEl.getSeries().isEmpty()) {
			List<AhSeries> seriesList = reportEl.getSeries();
			Font currentFont = textFonts;
			List<MetricGroupInfo> listGroups = DaDataCalculateUtil.getMetricsGroupOption(aData.getAxisKey(), aData.getUuKey());
			// there are groups for this list chart
			if (listGroups != null
					&& listGroups.size() > 0) {
				widths = new float[]{0.35f, 0.65f};
				table = new PdfPTable(widths);
				table.setWidthPercentage(100);
				table.setHorizontalAlignment(Element.ALIGN_LEFT);
				boolean blnFirstGroupGlobal = true;
				for (MetricGroupInfo groupInfo : listGroups) {
					cell = new PdfPCell(new Phrase(groupInfo.getName(), currentFont));
					cell.setPadding(tablePadding);
					cell.setBorderWidthRight(0L);
					cell.setBorderWidthBottom(0L);
					table.addCell(cell);
					boolean firstGroupValue = true;
					int curGrpMetricPos = 0,
						grpMetricLen = groupInfo.getMetrics().size();
					for (String uuKey : groupInfo.getMetrics()) {
						curGrpMetricPos++;
						for (AhSeries aSeries : seriesList) {
							if (uuKey.equals(aSeries.getUuKey())) {
								if (!firstGroupValue) {
									cell = new PdfPCell(new Phrase("", currentFont));
									cell.setPadding(tablePadding);
									cell.setBorderWidthTop(0L);
									cell.setBorderWidthRight(0L);
									if (blnFirstGroupGlobal 
											|| curGrpMetricPos != grpMetricLen) {
										cell.setBorderWidthBottom(0L);
									}
									table.addCell(cell);
								} else {
									firstGroupValue = false;
								}
								AhSeriesData aSeriesData = null;
								if (aSeries.getData() != null
										&& aSeries.getData().size() > 0
										&& aSeries.getData().get(0) != null) {
									aSeriesData = aSeries.getData().get(0);
								}
								Object value = _getSeriesValueForList(aSeriesData);
								String unitString = _getSeriesUnitForList(aSeriesData);
								cell = new PdfPCell(new Phrase(value.toString() + unitString + " " + aSeries.getName(), currentFont));
								cell.setPadding(tablePadding);
								table.addCell(cell);
								break;
							}
						}
					}
					if (blnFirstGroupGlobal) {
						blnFirstGroupGlobal = false;
					}
				}
			} else {
				for (AhSeries aSeries : seriesList) {
					cell = new PdfPCell(new Phrase(aSeries.getName(), currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
					AhSeriesData aSeriesData = null;
					if (aSeries.getData() != null
							&& aSeries.getData().size() > 0
							&& aSeries.getData().get(0) != null) {
						aSeriesData = aSeries.getData().get(0);
					}
					
					Object value = _getSeriesValueForList(aSeriesData);
					String unitString = _getSeriesUnitForList(aSeriesData);
					cell = new PdfPCell(new Phrase(value.toString() + unitString, currentFont));
					cell.setPadding(tablePadding);
					table.addCell(cell);
				}
			}
		} else {
			cell = new PdfPCell(new Phrase(" ",textFonts));
			table.addCell(cell);
			table.addCell(cell);
		}
		document.add(table);
	}
	
	public static interface ExportPdfResponse<T> {
		public void setResponse(T obj, String fileName);
		public T getResponse();
		public String getFileName();
		public void respond();
	}
	public static abstract class ExportPdfStreamResponse implements ExportPdfResponse<OutputStream> {
		private OutputStream outputStream;
		private String fileName;
		
		@Override
		public void setResponse(OutputStream outputStream, String fileName) {
			this.outputStream = outputStream;
			this.fileName = fileName;
		}
		
		@Override
		public String getFileName() {
			return this.fileName;
		}

		@Override
		public OutputStream getResponse() {
			return this.outputStream;
		}
		
		protected void closeStream() {
			if (outputStream != null) {
        		try {
        			outputStream.close();
				} catch (IOException e) {
					log.error("Failed to close output stream for pdf file.", e);
					e.printStackTrace();
				}
        	}
		}
	}
	public static abstract class ExportPdfFilePathResponse implements ExportPdfResponse<String> {
		private String filePath;
		private String fileName;
		
		@Override
		public void setResponse(String filePath, String fileName) {
			this.filePath = filePath;
			this.fileName = fileName;
		}

		@Override
		public String getFileName() {
			return this.fileName;
		}
		
		@Override
		public String getResponse() {
			return this.filePath;
		}
	}
	
}
