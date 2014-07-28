package com.ah.ui.actions.monitor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import search.Search;
import search.Search.WallLoss;

import com.ah.be.common.MapNodeUtil;
import com.ah.be.common.NmsUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.HmBo;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhXIf;
import com.ah.ui.actions.tools.PlanToolAction;
import com.ah.util.CountryCode;
import com.ah.util.MgrUtil;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.ZapfDingbatsList;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfOutline;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class TopoPdfReport implements QueryBo {

	private static final Log log = LogFactory.getLog("commonlog.TopoPdfReport");

	private Graphics2D g2;

	private double scale = 0.0;

	protected Rectangle page = PageSize.A4;

	private String apIcon = "target_icon.png";

	private static final String baseUrl = System.getenv("HM_ROOT") + "/";

	protected Document document = new Document(PageSize.A4, 50, 50, 72, 72);

	private String fontPath = System.getenv("HM_ROOT") + "/resources/fonts/";

	private Font fontReportTitle;

	private Font fontHead1;

	private Font fontHead2;

	private Font fontText;

	private Font fontNote;

	private Font fontTableTitle;

	private Font fontListSymbol = new Font(Font.ZAPFDINGBATS, 5,
			Font.NORMAL, Color.BLACK);

	private Paragraph pgReportTitle = new Paragraph();

	private Paragraph pgHead1 = new Paragraph();

	private Paragraph pgHead2 = new Paragraph();

	private Paragraph pgText = new Paragraph();

	private Paragraph pgNote = new Paragraph();

	private DrawHeatmapAction drawHeatmap;

	public TopoPdfReport(DrawHeatmapAction drawHeatmap) {
		this.drawHeatmap = drawHeatmap;
		this.rssiThreshold = this.drawHeatmap.rssiThreshold;
		this.rateThreshold = this.drawHeatmap.rateThreshold;
		this.snrThreshold = this.drawHeatmap.snrThreshold;
		this.frequency = this.drawHeatmap.frequency;
		this.layers = this.drawHeatmap.layers;
		this.apLabels = this.drawHeatmap.apLabels;
		this.rapLabels = this.drawHeatmap.rapLabels;
		try {
			BaseFont bfArialBd = BaseFont.createFont(fontPath + "arialbd.ttf",
					BaseFont.CP1252, BaseFont.EMBEDDED);
			BaseFont bfTrebuc = BaseFont.createFont(fontPath + "trebuc.ttf",
					BaseFont.CP1252, BaseFont.EMBEDDED);
			BaseFont bfTrebucIt = BaseFont.createFont(
					fontPath + "trebucit.ttf", BaseFont.CP1252,
					BaseFont.EMBEDDED);
			BaseFont bfTrebucBd = BaseFont.createFont(
					fontPath + "trebucbd.ttf", BaseFont.CP1252,
					BaseFont.EMBEDDED);

			fontReportTitle = new Font(bfArialBd, 22, Font.NORMAL,
					new Color(0, 45, 86));

			fontHead1 = new Font(bfArialBd, 14, Font.NORMAL, new Color(0,
					0, 0));

			fontHead2 = new Font(bfArialBd, 11, Font.NORMAL, new Color(0,
					45, 86));

			fontText = new Font(bfTrebuc, 9, Font.NORMAL,
					new Color(0, 0, 0));

			fontNote = new Font(bfTrebucIt, 9, Font.NORMAL, new Color(0, 0,
					0));

			fontTableTitle = new Font(bfTrebucBd, 9, Font.NORMAL,
					new Color(0, 0, 0));

			pgReportTitle.setFont(fontReportTitle);
			pgReportTitle.setSpacingBefore(12);
			pgReportTitle.setSpacingAfter(12);
			pgReportTitle.setLeading(pgReportTitle.getFont().getSize()
					+ pgReportTitle.getSpacingBefore());

			pgHead1.setSpacingBefore(12);
			pgHead1.setSpacingAfter(9);
			pgHead1.setFont(fontHead1);
			pgHead1.setLeading(pgHead1.getFont().getSize()
					+ pgHead1.getSpacingBefore());

			pgHead2.setSpacingBefore(12);
			pgHead2.setSpacingAfter(6);
			pgHead2.setFont(fontHead2);
			pgHead2.setLeading(pgHead2.getFont().getSize()
					+ pgHead2.getSpacingBefore());

			pgText.setSpacingBefore(6);
			pgText.setSpacingAfter(6);
			pgText.setFont(fontText);
			pgText.setAlignment(Element.ALIGN_JUSTIFIED);
			pgText.setLeading(pgText.getFont().getSize()
					+ pgText.getSpacingBefore());

			pgNote.setSpacingBefore(6);
			pgNote.setSpacingAfter(6);
			pgNote.setFont(fontNote);
			pgNote.setAlignment(Element.ALIGN_JUSTIFIED);
			pgNote.setLeading(pgText.getFont().getSize()
					+ pgText.getSpacingBefore());
		} catch (Exception e) {
			log.error("TopoPdfReport() exception", e);
		}
	}

	PdfWriter writer;

	private int layers = 0;
	private int frequency = 0;
	private int rssiThreshold = 70;
	private int rateThreshold = 0;
	private int snrThreshold = 5;
	private String apLabels;
	private String rapLabels;
	private boolean gridChecked = true;
	private short channelWidth = -1;
	private Long selectedMapId = null;

	public boolean createPdf(Long mapId, String fileName, boolean gridChecked,
			short channelWidth, Long acspId, PlanToolConfig planToolConfig) {
		this.gridChecked = gridChecked;
		this.channelWidth = channelWidth;
		this.gridChecked = gridChecked;
		this.channelWidth = channelWidth;

		try {
			this.writer = PdfWriter.getInstance(document, new FileOutputStream(
					fileName));

			writer.setPageEvent(new HeaderFooterPage());
			document.open();

			document.addTitle(NmsUtil.getOEMCustomer().getCompanyName()
					+ " Planning Report");
			document.addAuthor(NmsUtil.getOEMCustomer().getCompanyFullName());
			document.addSubject("");
			document.addKeywords("");

			writer.setViewerPreferences(PdfWriter.PageModeUseOutlines
					| PdfWriter.DisplayDocTitle);

			PdfOutline root = writer.getDirectContent().getRootOutline();

			printIntroduction(root);

			selectedMapId = mapId;
			digMap(mapId, "1", root, acspId, planToolConfig);

		} catch (Exception e) {
			log.error("createPdf() exception", e);
			return false;
		}
		document.close();
		return true;
	}

	private void printIntroduction(PdfOutline root) {
		try {
			pgReportTitle.clear();
			pgReportTitle.add(new Chunk(NmsUtil.getOEMCustomer()
					.getCompanyName() + " Planning Report")
					.setLocalDestination("section1"));
			document.add(pgReportTitle);

			PdfOutline section1 = new PdfOutline(root, PdfAction.gotoLocalPage(
					"section1", false), NmsUtil.getOEMCustomer()
					.getCompanyName() + " Planning Report");

			pgHead2.clear();
			pgHead2.add(new Chunk("Introduction")
					.setLocalDestination("section1-1"));
			document.add(pgHead2);
			new PdfOutline(section1, PdfAction.gotoLocalPage("section1-1",
					false), "Introduction");

			pgText.clear();
			pgText.add("Thank you for using the "
					+ NmsUtil.getOEMCustomer().getCompanyName()
					+ " Planning Tool. This tool is designed to help scope and plan a WiFi Deployment to determine the number of APs required to achieve an intended coverage, AP placement and datarates. This tool calculates the loss in signal strength as it passes through open air and various materials to show predicted coverage.");
			document.add(pgText);

			pgHead2.clear();
			pgHead2.add(new Chunk("RF Prediction with Optional Site Survey")
					.setLocalDestination("section1-2"));
			document.add(pgHead2);
			new PdfOutline(section1, PdfAction.gotoLocalPage("section1-2",
					false), "RF Prediction with Optional Site Survey");

			String[] strs = new String[] {
					"An RF prediction is an estimate of WLAN performance and coverage. It uses intelligent algorithms to examine AP behavior based upon an imported floor plan with assigned building characteristics. The accuracy of an RF prediction is dependent upon the confidence level with which the building's RF characteristics are assigned, and the accuracy of AP placement. It is ideal for typical office environments with uniform wall types. In addition RF itself can be unpredictable, due to the difficulty of characterizing the behavior of RF when interacting with various materials.",
					"Complex environments should be verified with a survey to verify the assumptions used in an RF prediction." };
			for (String str : strs) {
				pgText.clear();
				pgText.add(str);
				document.add(pgText);
			}

			pgHead2.clear();
			pgHead2.add(new Chunk("Assumptions")
					.setLocalDestination("section1-3"));
			document.add(pgHead2);
			new PdfOutline(section1, PdfAction.gotoLocalPage("section1-3",
					false), "Assumptions");

			pgText.clear();
			pgText.add("The guidelines in this document are based on the following conditions and assumptions:");
			document.add(pgText);

			String[] assumptions = new String[] {
					"Client Data Terminal Transmit (Tx) Power: >=15 dBm.",
					"Client Data Terminal Antenna Gain: >=0 dBi.",
					"The map environment type (e.g. Warehouse, Office) relates to an average density which is quantified as a path loss exponent value.  It estimates how quickly an RF signal attenuates with distance.",
					"The indicated wall path-through loss number (e.g. 12dB for a concrete wall) is the attenuation of an RF signal as it travels through the wall under a right angle.  For any other angle, the loss will be higher.",
					"The EIRP (Effective Isotropic Radiated Power) of an AP's radio is determined by the Tx power setting, the antenna gain and cable losses.  The antenna gain is an average gain obtained through measurements for the different AP types.",
					"Data rates are based on receive sensitivity numbers obtained through measurements for the different AP types, and a fade margin which is user configurable.",
//					"The data rates for n-type APs  assume a channel width of 20 MHz (HT20 data rates).",
					"", };

			ZapfDingbatsList zpList;

			Chunk chunk2 = new Chunk();
			chunk2.setFont(fontListSymbol);
			chunk2.setTextRise(2);
			chunk2.append(String.valueOf((char) 108));

			ListItem item2 = new ListItem();
			item2.setListSymbol(chunk2);
			item2.setIndentationRight(0);
			item2.setFont(pgText.getFont());

			for (String str : assumptions) {
				item2.clear();
				item2.add(str);

				zpList = new ZapfDingbatsList(108, 18);
				zpList.setIndentationLeft(18);
				zpList.add(item2);
				document.add(zpList);
			}

			pgNote.clear();
			pgNote.add("Note:   These assumptions are typical for available 802.11 client Data Terminals and typical cubicle densities.");
			document.add(pgNote);
			document.newPage();
		} catch (Exception e) {
			log.error("printIntroduction() exception", e);
		}

	}

	/**
	 * dig the map, if the map is build, draw building view and its all floors.
	 * if the map is general map, draw map and dig it. if the map is floor, draw
	 * map and don't need dig it.
	 * 
	 * @param mapId
	 *            -
	 * @param currentSection
	 *            -
	 * @param outnode
	 *            -
	 */
	private void digMap(Long mapId, String currentSection, PdfOutline outnode,
			Long acspId, PlanToolConfig planToolConfig) {
		MapContainerNode map = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, mapId, this);
		if (map.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			drawBuilding(map, currentSection, outnode, acspId, planToolConfig);
			return;
		}

		try {
			// 0. calculate the number of APs and the number of sub maps
			int apNum = 0;
			int mapNum = 0;
			for (MapNode node : map.getChildNodes()) {
				if (node instanceof MapLeafNode) {
					if(((MapLeafNode) node).getHiveAp() != null){
						apNum++;
					}
				}
				if (node instanceof MapContainerNode) {
					mapNum++;
				}
			}

			boolean plannedFlag = false;
			if (map.getPlannedAPs().size() > 0) {
				apNum = map.getPlannedAPs().size();
				plannedFlag = true;
			}
			// 0. end

			// 1. print paragraph
			String paraName;
			String destName = currentSection;

			if (currentSection.contains(".")) {
				paraName = currentSection + " " + map.getMapName();
			} else {
				paraName = currentSection + ". " + map.getMapName();
			}

			pgHead1.clear();
			pgHead1.add(new Chunk(paraName).setLocalDestination(destName));
			document.add(pgHead1);
			PdfOutline top = new PdfOutline(outnode, PdfAction.gotoLocalPage(
					destName, false), paraName);
			// 1. end

			if (apNum == 0 && mapNum == 0) {
				String msg;
				if (map.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
					msg = "There are no "
							+ NmsUtil.getOEMCustomer().getAccessPonitName()
							+ "s on " + map.getMapName();
				} else {
					msg = "There are no "
							+ NmsUtil.getOEMCustomer().getAccessPonitName()
							+ "s on " + map.getMapName()
							+ ", and there are no sub-maps on it.";
				}
				pgText.clear();
				pgText.add(msg);
				document.add(pgText);
				return;
			}

			// fix bug 23176
			MapContainerNode selectedMap = (MapContainerNode) QueryUtil
					.findBoById(MapNode.class, selectedMapId, this);
			if (selectedMap.getMapType() != MapContainerNode.MAP_TYPE_BUILDING
					&& selectedMap.getMapType() != MapContainerNode.MAP_TYPE_FLOOR
					&& selectedMapId == map.getId()
					&& selectedMap.getChildNodes().size() > 0 && mapNum > 0) {
				paraName = NmsUtil.getOEMCustomer().getAccessPonitName()
						+ " Total For " + selectedMap.getMapName();
				pgHead2.clear();
				pgHead2.add(new Chunk(paraName));
				document.add(pgHead2);

				printDeviceTotalForDomain(map, planToolConfig);
			}

			// 2. place whole map
			if (apNum > 0) {
				placeWholeMap(map, apNum, plannedFlag, acspId, planToolConfig);
			}

			// 3. dig sub-map for this map
			if (mapNum > 0) {
				int start = 1;
				for (MapNode node : map.getChildNodes()) {
					if (node instanceof MapContainerNode) {
						digMap(node.getId(), currentSection + "." + (start++),
								top, acspId, planToolConfig);
					}
				}
			}
		} catch (Exception e) {
			log.error("digMap() exception", e);
		}
	}

	private void placeWholeMap(MapContainerNode map, int apNum,
			boolean plannedFlag, Long acspId, PlanToolConfig planToolConfig)
			throws Exception {
		String paraName = "Summary";
		pgHead2.clear();
		pgHead2.add(new Chunk(paraName));
		document.add(pgHead2);

//		String apSummary = "The required number of "
//				+ NmsUtil.getOEMCustomer().getAccessPonitName()
//				+ "s to estimate locations is " + getCountEn(apNum) + ".";
		
		
		//fix bug 27455
		String apSummary = "Number of " + NmsUtil.getOEMCustomer().getAccessPonitName() + "s assigned to " + map.getMapName();
		pgText.clear();
		pgText.add(apSummary);
		document.add(pgText);
		
		pgText.clear();
		pgHead2.clear();
		
		FloorView fv = new FloorView(map);
		
		ZapfDingbatsList zpList;

		Chunk chunk2 = new Chunk();
		chunk2.setFont(fontListSymbol);
		chunk2.setTextRise(2);
		chunk2.append(String.valueOf((char) 108));

		ListItem item2 = new ListItem();
		item2.setListSymbol(chunk2);
		item2.setIndentationRight(0);
		item2.setFont(pgText.getFont());
		
		if(fv.getApCount() > 0){
			item2.clear();
			if(fv.getApCount() == 1){
				item2.add(fv.getApCount() + " AP");
			}else{
				item2.add(fv.getApCount() + " APs");
			}
			
			zpList = new ZapfDingbatsList(108, 18);
			zpList.setIndentationLeft(18);
			zpList.add(item2);
			document.add(zpList);
		}
		
		if(fv.getBrCount() > 0){
			item2.clear();
			if(fv.getBrCount() == 1){
				item2.add(fv.getBrCount() + " BR");
			}else{
				item2.add(fv.getBrCount() + " BRs");
			}
			
			zpList = new ZapfDingbatsList(108, 18);
			zpList.setIndentationLeft(18);
			zpList.add(item2);
			document.add(zpList);
		}
		
		if(fv.getSrCount() > 0){
			item2.clear();
			if(fv.getSrCount() == 1){
				item2.add(fv.getSrCount() + " SR");
			}else{
				item2.add(fv.getSrCount() + " SRs");
			}
			
			zpList = new ZapfDingbatsList(108, 18);
			zpList.setIndentationLeft(18);
			zpList.add(item2);
			document.add(zpList);
		}
		
		if(fv.getVpnCount() > 0){
			item2.clear();
			if(fv.getVpnCount() == 1){
				item2.add(fv.getVpnCount() + " VPN Gateway");
			}else{
				item2.add(fv.getVpnCount() + " VPN Gateways");
			}
			
			zpList = new ZapfDingbatsList(108, 18);
			zpList.setIndentationLeft(18);
			zpList.add(item2);
			document.add(zpList);
		}

		paraName = NmsUtil.getOEMCustomer().getAccessPonitName() + "s on "
				+ map.getMapName();
		pgHead2.clear();
		pgHead2.add(new Chunk(paraName));
		document.add(pgHead2);

		// fix bug 14336 2011-07-04
		PdfContentByte cb = writer.getDirectContent();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(0.8f);
		cb.setGState(gs1);
		// Graphics2D tempCanvas = cb.createGraphics(page.getWidth(),
		// page.getHeight());
//		Graphics2D tempCanvas = new PdfGraphics2D(cb, page.getWidth(),
//				page.getHeight(), true);
		Graphics2D tempCanvas = cb.createGraphicsShapes(page.getWidth(),
				page.getHeight());
		java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.BOLD, 11);
		tempCanvas.setFont(font);

		FontMetrics fm = tempCanvas.getFontMetrics(font);
		int paraNameLength = fm.stringWidth(paraName);
		tempCanvas.dispose();

		// place legend image
		// drawLegend(paraName.length());
		drawLegend(paraNameLength);
		// fix end

		// place map
		drawMap(map, plannedFlag, acspId, planToolConfig);

		// place perimeter and walls
		drawPerimeter(imageOffsetY, map);
		drawWalls(imageOffsetY, map);

		// draw APs
		if (plannedFlag) {
			drawPlannedAps(writer, imageOffsetY, map.getPlannedAPs());
		} else {
			drawAps(writer, imageOffsetY, map.getChildNodes());
		}

		if (needNewPage) {
			document.newPage();
		}

		paraName = NmsUtil.getOEMCustomer().getAccessPonitName() + " Details";
		pgHead2.clear();
		pgHead2.add(new Chunk(paraName));
		document.add(pgHead2);

		// print AP info
		if (plannedFlag) {
			printPlannedApInfo(map.getPlannedAPs());
		} else {
			printHiveApInfo(map.getChildNodes());
		}

		if (apNum > 0) {
			// US905 Summary of AP counts by type
			paraName = NmsUtil.getOEMCustomer().getAccessPonitName()
					+ " Total For " + map.getMapName();
			pgHead2.clear();
			pgHead2.add(new Chunk(paraName));
			document.add(pgHead2);

			printDeviceTotal(map.getId(), plannedFlag);
		}
	}

	MapBldPdfReport bld;

	private void drawBuilding(MapContainerNode map, String currentSection,
			PdfOutline outnode, Long acspId, PlanToolConfig planToolConfig) {
		try {
			String paraName;
			String destName = currentSection;

			if (currentSection.contains(".")) {
				paraName = currentSection + " " + map.getMapName();
			} else {
				paraName = currentSection + ". " + map.getMapName();
			}

			pgHead1.clear();
			pgHead1.add(new Chunk(paraName).setLocalDestination(destName));
			document.add(pgHead1);

			PdfOutline top = new PdfOutline(outnode, PdfAction.gotoLocalPage(
					destName, false), paraName);

			if (map.getMapType() == MapContainerNode.MAP_TYPE_BUILDING
					&& selectedMapId == map.getId()) {
				paraName = NmsUtil.getOEMCustomer().getAccessPonitName()
						+ " Total For " + map.getMapName();
				pgHead2.clear();
				pgHead2.add(new Chunk(paraName));
				document.add(pgHead2);

				printDeviceTotalForDomain(map, planToolConfig);
			}

			if (g2 == null) {
				PdfContentByte cb = writer.getDirectContent();
				// g2 = cb.createGraphics(page.getWidth(), page.getHeight());
//				g2 = new PdfGraphics2D(cb, page.getWidth(), page.getHeight(),
//						true);
				g2 = cb.createGraphicsShapes(page.getWidth(), page.getHeight());
				g2.dispose();
			}

			MapContainerNode selectedMap = (MapContainerNode) QueryUtil
					.findBoById(MapNode.class, map.getId(), this);

			bld = new MapBldPdfReport(this.drawHeatmap);
			bld.loadCanvas(selectedMap, planToolConfig);

			List<MapContainerNode> floors = sortFloors(selectedMap);
			if (floors.size() > 0) {
				paraName = currentSection + ".0 Building view";
				destName = currentSection + ".0";

				pgHead1.clear();
				pgHead1.add(new Chunk(paraName).setLocalDestination(destName));
				document.add(pgHead1);
				new PdfOutline(top, PdfAction.gotoLocalPage(destName, false),
						paraName);
			} else {
				pgText.clear();
				pgText.add("There are no no floors on this building.");
				document.add(pgText);
				return;
			}

			for (MapContainerNode node : floors) {
				if ((frequency & 1) > 0) {
					bld.cis = bld.mapCh2is.get(node.getId());
					bld.chs = bld.mapCh2s.get(node.getId());
				} else {
					bld.cis = bld.mapCh1is.get(node.getId());
					bld.chs = bld.mapCh1s.get(node.getId());
				}
				bld.ids = bld.mapApids.get(node.getId());
				BufferedImage image = bld.createFloorImage(selectedMap, node);

				putFloorImage(image, node);
				bld.cis = null;
				bld.chs = null;
				bld.ids = null;
			}

			int start = 1;
			for (MapContainerNode node : floors) {
				digMap(node.getId(), currentSection + "." + (start++), top,
						acspId, planToolConfig);
			}

		} catch (Exception e) {
			log.error("digMap() exception", e);
		}
	}

	private void putFloorImage(BufferedImage image, MapContainerNode node) {
		float curY1 = page.getHeight() - writer.getVerticalPosition(false);
		imageOffsetY = curY1;

		this.scale = 0.5;
		log.debug("w,h=(" + w + "," + h + ")");
		log.debug("this.scale=" + scale);

		try {
			float h0 = page.getHeight() - imageOffsetY
					- document.bottomMargin();

			if (image.getHeight() * scale > h0) {
				document.newPage();
				curY1 = page.getHeight() - writer.getVerticalPosition(false);
				imageOffsetY = curY1;
				h0 = page.getHeight() - imageOffsetY - document.bottomMargin();
			}
			double dScale = scale;
			while (image.getHeight() * dScale > h0) {
				dScale = (h0-70)/image.getHeight();
			}
			
			// 2 layout image
			Color bgColor = new Color(0xdd, 0xdd, 0xdd);
			Image imagex = Image.getInstance(image, bgColor);
			//imagex.scalePercent((float) scale * 100);
			imagex.scalePercent((float) scale * 100,(float) dScale * 100);
			document.add(imagex);

			PdfContentByte cb = writer.getDirectContent();
			PdfGState gs1 = new PdfGState();
			gs1.setFillOpacity(0.8f);
			cb.setGState(gs1);
			// textCanvas = cb.createGraphics(page.getWidth(),
			// page.getHeight());
//			textCanvas = new PdfGraphics2D(cb, page.getWidth(),
//					page.getHeight(), true);
			textCanvas = cb.createGraphicsShapes(page.getWidth(),
					 page.getHeight());

			FloorView fv = bld.fvs.get(node.getId());
			String s0 = node.getMapName();
			/*
			 * String s1 = "Number of APs"; String s1n = "" + fv.getApCount();
			 */

			String s2 = "Service Area";
			String s2n = fv.getArea() + " " + fv.getAreaUnit();
			String s2m = "n/a (no perimeter(s) defined)";

			String s3 = "Average Area per " + ((fv.getApCount() > 0 && fv.getBrCount() > 0) ? "AP/BR" : (fv.getApCount() > 0 ? "AP" : "BR"));
			String s3n = fv.getAreaAp() + " " + fv.getAreaUnit();

			String s4 = "Floor Alignment";
			String s41 = "X: " + fv.getOriginX() + " " + fv.getLengthUnit();
			String s42 = "Y: " + fv.getOriginY() + " " + fv.getLengthUnit();

			java.awt.Font floorNameFont = new java.awt.Font("Arial",
					java.awt.Font.BOLD, 10);
			textCanvas.setFont(floorNameFont);
			textCanvas.setColor(new Color(0, 45, 86));
			textCanvas.drawString(s0,
					(float) (page.getLeft() + 60 + image.getWidth() * scale),
					imageOffsetY + 30);

			java.awt.Font font = new java.awt.Font("Arial",
					java.awt.Font.PLAIN, 8);
			textCanvas.setFont(font);
			textCanvas.setColor(Color.black);

			int textY = 44;

			// textCanvas.drawString(s1,
			// (float) (page.getLeft() + 60 + image.getWidth() * scale),
			// imageOffsetY + textY);
			// textCanvas.drawString(s1n,
			// (float) (page.getLeft() + 160 + image.getWidth() * scale),
			// imageOffsetY + textY);
			if (fv.getVpnCount() > 0) {
				String srCountString = "Number of VPN Gateways";
				String vpnCounts = "" + fv.getVpnCount();
				textCanvas
						.drawString(srCountString,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(vpnCounts,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);
			}

			if (fv.getSrCount() > 0) {
				if (fv.getVpnCount() > 0) {
					textY += 14;
				}
				String srCountString = "Number of SRs";
				String srCounts = "" + fv.getSrCount();
				textCanvas
						.drawString(srCountString,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(srCounts,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);
			}

			if (fv.getBrCount() > 0) {
				if (fv.getSrCount() > 0 || fv.getVpnCount() > 0) {
					textY += 14;
				}
				String brCountString = "Number of BRs";
				String brCounts = "" + fv.getBrCount();
				textCanvas
						.drawString(brCountString,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(brCounts,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);
			}

			if (fv.getApCount() > 0) {
				if (fv.getSrCount() > 0 || fv.getBrCount() > 0 || fv.getVpnCount() > 0) {
					textY += 14;
				}
				String apCountString = "Number of APs";
				String apCounts = "" + fv.getApCount();
				textCanvas
						.drawString(apCountString,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(apCounts,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);
			}

			if (fv.getApCount() == 0 && fv.getBrCount() == 0 && fv.getSrCount() == 0 && fv.getVpnCount() == 0) {
				String apCountString = "Number of Devices";
				String apCounts = "" + fv.getApCount();
				textCanvas
						.drawString(apCountString,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(apCounts,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);
			}

			if (node.getPerimeter() != null && node.getPerimeter().size() > 2) {
				textY += 14;
				textCanvas
						.drawString(s2,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(s2n,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);

				if (fv.getAvailableCount() > 0) {
					textY += 14;
					textCanvas.drawString(s3,
							(float) (page.getLeft() + 60 + image.getWidth()
									* scale), imageOffsetY + textY);
					textCanvas.drawString(s3n,
							(float) (page.getLeft() + 170 + image.getWidth()
									* scale), imageOffsetY + textY);
				}
			} else {
				textY += 14;
				textCanvas
						.drawString(s2,
								(float) (page.getLeft() + 60 + image.getWidth()
										* scale), imageOffsetY + textY);
				textCanvas.drawString(s2m,
						(float) (page.getLeft() + 170 + image.getWidth()
								* scale), imageOffsetY + textY);
			}

			textY += 14;
			textCanvas.drawString(s4,
					(float) (page.getLeft() + 60 + image.getWidth() * scale),
					imageOffsetY + textY);
			textY += 14;
			textCanvas.drawString(s41,
					(float) (page.getLeft() + 60 + image.getWidth() * scale),
					imageOffsetY + textY);
			textY += 14;
			textCanvas.drawString(s42,
					(float) (page.getLeft() + 60 + image.getWidth() * scale),
					imageOffsetY + textY);

			textCanvas.dispose();
		} catch (Exception e) {
			log.error("digMap() exception", e);
		}
	}

	private static int[] rssiColors = { 0x800000, 0x951500, 0xaa2b00, 0xbf4000,
			0xd45500, 0xea6b00, 0xff8000, 0xff8c00, 0xff9700, 0xffa300,
			0xffae00, 0xffba00, 0xffc500, 0xffd100, 0xffdc00, 0xffe800,
			0xfff300, 0xffff00, 0xd4ff0b, 0xaaff15, 0x80ff20, 0x55ff2b,
			0x2bff35, 0x00ff40, 0x00ff58, 0x00ff70, 0x00ff88, 0x00ff9f,
			0x00ffb7, 0x00ffcf, 0x00ffe7, 0x00ffff, 0x00f1fe, 0x01e4fc,
			0x01d6fb, 0x01c8f9, 0x02bbf8, 0x02adf6, 0x029ff5, 0x0292f4,
			0x0384f2, 0x0376f1, 0x066de3, 0x0963d4, 0x0c5ac6, 0x0f50b8,
			0x1246aa, 0x153d9c, 0x18338d, 0x1b2a7f, 0x1e2071, 0x211662,
			0x240c54, 0x270346, 0x2a0038, 0x2d002a };

	int color;
	String title;

	private int legendWidth = 3;
	private int legendHeight = 18;
	private int offsetY = 0;
	private int offsetX = 0;
	private Graphics2D textCanvas;
	private Graphics2D legendCanvas;
	private int legendImageWidth = 0;
	private int legendStartX = 0;

	private void drawLegend(int numberOfText) throws Exception {
		if (layers < 1 || layers > 16) {
			return;
		}
		float curY1 = page.getHeight() - writer.getVerticalPosition(false);
		offsetY = (int) curY1 - 6;

		// fix bug 14336 2011-07-04
		// offsetX = (int) (document.left() + numberOfText * 5.5 + 5);
		offsetX = (int) (document.left() + numberOfText + 5);
		// fix end

		PdfContentByte cb = writer.getDirectContent();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(0.8f);
		cb.setGState(gs1);
		// textCanvas = cb.createGraphics(page.getWidth(), page.getHeight());
//		textCanvas = new PdfGraphics2D(cb, page.getWidth(), page.getHeight());
		textCanvas = cb.createGraphicsShapes(page.getWidth(), page.getHeight());
		textCanvas.setColor(Color.black);

		BufferedImage image = new BufferedImage(
				legendWidth * rssiColors.length, legendHeight,
				BufferedImage.TYPE_INT_ARGB);
		legendCanvas = image.createGraphics();
		legendCanvas.setColor(Color.white);

		String s1 = "";
		String s2 = "";

		String band;
		if ((frequency & 1) > 0) {
			band = "5";
		} else {
			band = "2.4";
		}

		if ((layers & 16) > 0 || (layers & 4) > 0) {
			s1 = "(" + band + " GHz, " + "SNR View: 60 ";
			createSnrLegend();
			s2 = title + "dB)";
		} else if ((layers & 1) > 0) {
			s1 = "(" + band + " GHz, " + "RSSI View: -35 ";
			createRssiLegend();
			s2 = title + "dBm)";
		} else if ((layers & 8) > 0) {
			s1 = "(" + band + " GHz, " + "Data Rates View: ";
			legendStartX = offsetX + s1.length() * 5; // legendX shall be used
														// in below method
			createRatesLegend();
			s2 = "Mbps)";
			legendImageWidth = legendWidth * rssiColors.length; // full length
		} else if ((layers & 2) > 0) {
			s1 = "(" + band + " GHz, " + "Channels View: -35 ";
			createChannelsLegend();
			s2 = title + "dBm)";
		} else {
			log.warn("layers error:" + layers);
		}

		java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN, 10);
		textCanvas.setFont(font);

		legendStartX = offsetX + s1.length() * 5;
		textCanvas.drawString(s1, offsetX, offsetY);
		textCanvas.drawString(s2, legendStartX + legendImageWidth + 2, offsetY);

		textCanvas.drawImage(image, null, legendStartX, offsetY - legendHeight);

		textCanvas.dispose();
	}

	private void createRssiLegend() {
		for (int i = 0; i < rssiColors.length; i++) {
			int rssi = -i - 35;
			if (rssi < -rssiThreshold) {
				legendImageWidth = i * legendWidth;
				break;
			}
			title = "" + rssi;
			color = rssiColors[i];
			legendCanvas.setColor(new Color(color));
			legendCanvas.fillRect(i * 3, 0, legendWidth, legendHeight);
		}
	}

	private Color[] startChannelColors = new Color[] { new Color(255, 0, 0),
			new Color(0, 255, 0), new Color(0, 0, 255), new Color(255, 255, 0),
			new Color(0, 255, 255), new Color(255, 0, 255),
			new Color(255, 128, 0), new Color(67, 29, 95),
			new Color(0, 130, 60), new Color(128, 255, 0),
			new Color(128, 0, 255), new Color(0, 128, 255) };

	private Color[] endChannelColors = new Color[] { new Color(255, 200, 200),
			new Color(200, 255, 200), new Color(200, 200, 255),
			new Color(255, 255, 200), new Color(200, 255, 255),
			new Color(255, 200, 255), new Color(255, 188, 128),
			new Color(193, 139, 234), new Color(128, 220, 172),
			new Color(255, 255, 200), new Color(255, 200, 255),
			new Color(200, 255, 255) };

	private Color[][] initColorShades(Color[] startColors, Color[] endColors,
			int colors, short shadesPerColor) {
		boolean lightInCenter = true;
		short maxColors = 56;
		Color[][] channelColors = new Color[colors][maxColors];
		for (int color = 0; color < colors; color++) {
			int startRed = startColors[color].getRed();
			int startGreen = startColors[color].getGreen();
			int startBlue = startColors[color].getBlue();
			int endRed = endColors[color].getRed();
			int endGreen = endColors[color].getGreen();
			int endBlue = endColors[color].getBlue();
			int deltaRed = (startRed - endRed) / (shadesPerColor - 1);
			int deltaGreen = (startGreen - endGreen) / (shadesPerColor - 1);
			int deltaBlue = (startBlue - endBlue) / (shadesPerColor - 1);
			for (int step = 0; step < shadesPerColor; step++) {
				int red = endRed + step * deltaRed;
				int green = endGreen + step * deltaGreen;
				int blue = endBlue + step * deltaBlue;
				Color rgb = new Color(red, green, blue);
				if (lightInCenter) {
					channelColors[color][shadesPerColor - step - 1] = rgb;
				} else {
					channelColors[color][step] = rgb;
				}
			}
			for (int step = shadesPerColor; step < maxColors; step++) {
				channelColors[color][step] = channelColors[color][shadesPerColor - 1];
			}
			if (color == 0) {
				for (int i = 0; i < channelColors[color].length; i++) {
					Color c = channelColors[color][i];
					log.debug("initChannelColors \nColor[" + i + "] = ("
							+ c.getRed() + ", " + c.getGreen() + ", "
							+ c.getBlue() + ")");
				}
			}
		}
		return channelColors;
	}

	public Color[] getRssiRange(short shadesPerColor) throws Exception {
		Color[][] channelColors = initColorShades(startChannelColors,
				endChannelColors, 1, shadesPerColor);
		Collection<Color> colors = new Vector<Color>();
		for (int step = 0; step < shadesPerColor; step++) {
			Color c = channelColors[0][step];
			colors.add(c);
		}

		Color[] cls = new Color[colors.size()];
		colors.toArray(cls);
		return cls;
	}

	private void createChannelsLegend() throws Exception {
		Color[] colors = getRssiRange(getShadesPerColor());

		for (int i = 0; i < rssiColors.length; i++) {
			int rssi = -i - 35;
			if (rssi < -rssiThreshold) {
				legendImageWidth = i * legendWidth;
				break;
			}
			title = "" + rssi;
			color = rssiColors[i];
			legendCanvas.setColor(colors[i]);
			legendCanvas.fillRect(i * 3, 0, legendWidth, legendHeight);
		}
	}

	public void createRatesLegendBar(int bit_rates[], int rate_colors[]) {
		int rateIndex = bit_rates.length - 1;
		int rate = bit_rates[rateIndex];
		int colorIndex = rate_colors[0];
		for (int i = 0; i < rssiColors.length; i++) {
			if (rate_colors[i] != colorIndex) {
				rate = bit_rates[--rateIndex];
				colorIndex = rate_colors[i];
			}
			title = "" + (rate);
			color = rssiColors[colorIndex];

			legendCanvas.setColor(new Color(color));
			legendCanvas
					.fillRect(i * legendWidth, 0, legendWidth, legendHeight);
		}
	}

	private void createHT20RatesLegend() {
		int[] bit_rates_ht20 = { 1, 6, 12, 18, 24, 36, 52, 78, 104, 117, 130 };
		int rateIndex = 0;
		int rate = bit_rates_ht20[rateIndex];
		int colorIndex = rssiColors.length - 1;
		float fromRate = 0;
		int[] rate_colors_ht20 = new int[rssiColors.length];
		for (int i = colorIndex; i >= 0; i--) {
			if (fromRate + 1.5 > rate) {
				if (rateIndex + 1 < bit_rates_ht20.length) {
					rate = bit_rates_ht20[++rateIndex];
					colorIndex = i == 13 ? 14 : i;
				} else {
					colorIndex = 3;
				}
			}
			rate_colors_ht20[i] = colorIndex;
			fromRate += 2.5;
		}
		if (rate_colors_ht20[25] == 34) {
			rate_colors_ht20[24] = 34;
		} else {
			log.warn("it seems that there is an error when draw HT20 legend!");
		}
		if (rate_colors_ht20[35] == 41) {
			rate_colors_ht20[34] = 41;
		}
		if (rate_colors_ht20[50] == 50) {
			rate_colors_ht20[51] = 50;
		}
		if (rate_colors_ht20[53] == 53) {
			rate_colors_ht20[54] = 53;
		}
		createRatesLegendBar(bit_rates_ht20, rate_colors_ht20);
	}

	public void createHT40RatesLegend() {
		int[] bit_rates_ht40 = { 1, 6, 12, 18, 24, 27, 36, 48, 54, 81, 108,
				130, 162, 216, 247, 270 };
		int rateIndex = 0;
		int rate = bit_rates_ht40[rateIndex];
		int colorIndex = rssiColors.length - 1;
		int fromRate = 0;
		int[] rate_colors_ht40 = new int[rssiColors.length];
		for (int i = colorIndex; i >= 0; i--) {
			if (fromRate + 4 > rate) {
				if (rateIndex + 1 < bit_rates_ht40.length) {
					rate = bit_rates_ht40[++rateIndex];
					colorIndex = i == 6 ? 8 : i == 12 ? 14 : i;
				} else {
					colorIndex = 3;
				}
			}
			rate_colors_ht40[i] = colorIndex;
			fromRate += 5;
		}
		createRatesLegendBar(bit_rates_ht40, rate_colors_ht40);
	}

	private void createRatesLegend() {
		boolean show20 = (this.channelWidth == 1) || !((frequency & 1) > 0);
		if (show20) {
			createHT20Mark();
			createHT20RatesLegend();
		} else {
			createHT40Mark();
			createHT40RatesLegend();
		}
	}

	private void createHT20Mark() {
		textCanvas.setStroke(new BasicStroke(0.1f));
		java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN, 6);
		textCanvas.setFont(font);

		int tlen = 12;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("130", legendStartX + tlen - 6, offsetY + 10);

		tlen += 15;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 15;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("104", legendStartX + tlen - 6, offsetY + 10);

		tlen += 30;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("78", legendStartX + tlen - 4, offsetY + 10);

		tlen += 30;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("52", legendStartX + tlen - 4, offsetY + 10);

		tlen += 24;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("36", legendStartX + tlen - 4, offsetY + 10);

		tlen += 12;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 9;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("18", legendStartX + tlen - 4, offsetY + 10);

		tlen += 9;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 9;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("6", legendStartX + tlen - 2, offsetY + 10);
	}

	private void createHT40Mark() {
		textCanvas.setStroke(new BasicStroke(0.1f));
		java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN, 6);
		textCanvas.setFont(font);

		int tlen = 6;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("270", legendStartX + tlen - 6, offsetY + 10);

		tlen += 15;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 18;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("216", legendStartX + tlen - 6, offsetY + 10);

		tlen += 33;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("162", legendStartX + tlen - 6, offsetY + 10);

		tlen += 18;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 15;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("108", legendStartX + tlen - 6, offsetY + 10);

		tlen += 15;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 15;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("54", legendStartX + tlen - 4, offsetY + 10);

		tlen += 18;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);

		tlen += 12;
		textCanvas.drawLine(legendStartX + tlen, offsetY, legendStartX + tlen,
				offsetY + 3);
		textCanvas.drawString("6", legendStartX + tlen - 2, offsetY + 10);
	}

	private void createSnrLegend() {
		for (int i = 0; i < rssiColors.length; i++) {
			int rssi = -i - 35;
			if (rssi < -rssiThreshold) {
				legendImageWidth = i * legendWidth;
				break;
			}
			title = "" + (60 - i);
			color = 60 - i >= snrThreshold ? rssiColors[i] : 0xfff;
			legendCanvas.setColor(new Color(color));
			legendCanvas.fillRect(i * 3, 0, legendWidth, legendHeight);
		}
	}

	private static final String[] oneDigitEn = new String[] { "", "one", "two",
			"three", "four", "five", "six", "seven", "eight", "nine" };
	private static final String[] tenDigitEn = new String[] { "", "", "twenty",
			"thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety" };

	private static final String[] number20En = new String[] { "zero", "one",
			"two", "three", "four", "five", "six", "seven", "eight", "nine",
			"ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen",
			"sixteen", "seventeen", "eighteen", "nineteen" };

	private String getCountEn(int apNum) {
		if (apNum >= 100) {
			return String.valueOf(apNum);
		}

		if (apNum >= 20) {
			int a2 = apNum / 10;
			int a1 = apNum % 10;
			return tenDigitEn[a2] + " " + oneDigitEn[a1];
		}

		return number20En[apNum];
	}

	private void drawPerimeter(float imageOffsetY, MapContainerNode map) {
		List<Vertex> perimeter_x = map.getPerimeter();
		if (perimeter_x.size() < 3) {
			return;
		}

		List<List<Vertex>> multiplePerimeter = new ArrayList<List<Vertex>>();
		List<Vertex> onePerimeter = null;
		int lastId = -1;
		for (Vertex v : perimeter_x) {
			if (v.getId() != lastId) {
				if (onePerimeter != null) {
					multiplePerimeter.add(onePerimeter);
				}
				onePerimeter = new ArrayList<Vertex>();
			}
			onePerimeter.add(v);
			lastId = v.getId();
		}
		multiplePerimeter.add(onePerimeter);

		Color color = g2.getColor();
		Stroke stroke = g2.getStroke();

		g2.setColor(new Color(50, 176, 246));
		g2.setStroke(new BasicStroke(1f));

		for (List<Vertex> perimeter : multiplePerimeter) {
			int nPoints = perimeter.size();
			int[] xPoints = new int[nPoints];
			int[] yPoints = new int[nPoints];

			int i = 0;
			for (Vertex v : perimeter) {
				log.debug("v:" + v.getId() + " = (" + v.getX() + "," + v.getY()
						+ ")");

				long x = scale(v.getX(), scale);
				long y = scale(v.getY(), scale);

				xPoints[i] = (int) (x + document.leftMargin());
				yPoints[i] = (int) (y + imageOffsetY);

				i++;
			}
			g2.drawPolygon(xPoints, yPoints, nPoints);
		}
		g2.setColor(color);
		g2.setStroke(stroke);
	}

	private void drawWalls(float imageOffsetY, MapContainerNode map) {
		List<Wall> wall = map.getWalls();

		Color color = g2.getColor();
		Stroke stroke = g2.getStroke();

		g2.setColor(new Color(112, 41, 99));
		g2.setStroke(new BasicStroke(1f));

		for (Wall w : wall) {
			log.debug("w:" + w.getType() + " = (" + w.getX1() + "," + w.getY1()
					+ ")");

			long xx1 = scale(w.getX1(), scale);
			long yy1 = scale(w.getY1(), scale);
			long xx2 = scale(w.getX2(), scale);
			long yy2 = scale(w.getY2(), scale);

			int x1 = (int) (xx1 + document.leftMargin());
			int x2 = (int) (xx2 + document.leftMargin());
			int y1 = (int) (yy1 + imageOffsetY);
			int y2 = (int) (yy2 + imageOffsetY);

			g2.drawLine(x1, y1, x2, y2);
		}

		g2.setColor(color);
		g2.setStroke(stroke);
	}

	private void printPlannedApInfo(Set<PlannedAP> plannedAPs)
			throws DocumentException {
		PdfPCell titleCell = buildTitleCell();

		PdfPTable table = new PdfPTable(8);
		int[] widths = new int[] { 150, 150, 150, 100, 100, 100, 100, 150 };
		table.setWidths(widths);

		// data
		table = new PdfPTable(8);
		table.setWidths(widths);
		for (PlannedAP apNode : plannedAPs) {
			HiveAp hiveAp = hiveApAttrs.get(apNode.getId());
			if (null != hiveAp) {
				// String hwModel = MgrUtil.getEnumString("enum.hiveAp.model." +
				// apNode.apModel);
				// String hostName = hwModel.substring(4) + "-" +
				// Long.toString(1000000000 +
				// apNode.getId()).substring(3);
				String hostName = apNode.hostName;
				table.addCell(new Phrase(hostName, fontText));
				table.addCell(new Phrase(HiveAp
						.getModelEnumString(apNode.apModel), fontText));
				table.addCell(new Phrase(HiveAp
						.getModelProtocalString(apNode.apModel), fontText));

				String ch0;
				String ch1;
				String pw0;
				String pw1;
				if (apNode.wifi0Enabled) {
					if (hiveAp.getWifi0().getChannel() == 0) {
						short wifi0Channel = apNode.wifi0Channel;
						if (wifi0Channel == 0) {
							wifi0Channel = apNode.autoWifi0Channel;
						}
						ch0 = "Auto("
								+ (wifi0Channel < 0 ? -wifi0Channel
										: wifi0Channel) + ")";
					} else {
						ch0 = String.valueOf(hiveAp.getWifi0().getChannel());
					}
					pw0 = String.valueOf(apNode.wifi0Power) + " dBm";
				} else {
					ch0 = "";
					pw0 = "";
				}

				if (apNode.wifi1Enabled) {
					if (hiveAp.getWifi1().getChannel() == 0) {
						short wifi1Channel = apNode.wifi1Channel;
						if (wifi1Channel == 0) {
							wifi1Channel = apNode.autoWifi1Channel;
						}
						ch1 = "Auto("
								+ (wifi1Channel < 0 ? -wifi1Channel
										: wifi1Channel) + ")";
					} else {
						ch1 = String.valueOf(hiveAp.getWifi1().getChannel());
					}
					pw1 = String.valueOf(apNode.wifi1Power) + " dBm";
				} else {
					ch1 = "";
					pw1 = "";
				}

				table.addCell(new Phrase(ch0, fontText));
				table.addCell(new Phrase(pw0, fontText));
				table.addCell(new Phrase(ch1, fontText));
				table.addCell(new Phrase(pw1, fontText));
				table.addCell(new Phrase("", fontText));
			} else {
				log.warn("no this ap:" + apNode.getId());
			}
		}
		PdfPCell contentCell = new PdfPCell(table);
		contentCell.setBorderWidth(1);

		PdfPTable wholeTable = new PdfPTable(1);
		wholeTable.addCell(titleCell);
		wholeTable.addCell(contentCell);

		wholeTable.setWidthPercentage(100);
		wholeTable.setHeaderRows(1);
		wholeTable.setSpacingBefore(6);
		wholeTable.setSpacingAfter(3);
		wholeTable.setSplitLate(false);

		document.add(wholeTable);
	}

	private PdfPCell buildTitleCell() throws DocumentException {
		PdfPTable table = new PdfPTable(8);
		int[] widths = new int[] { 150, 150, 150, 100, 100, 100, 100, 150 };
		table.setWidths(widths);

		// title
		PdfPCell pcell = new PdfPCell(new Phrase("Name", fontTableTitle));
		pcell.setRowspan(2);
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(pcell);
		pcell = new PdfPCell(new Phrase("Model", fontTableTitle));
		pcell.setRowspan(2);
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(pcell);
		pcell = new PdfPCell(new Phrase("Type", fontTableTitle));
		pcell.setRowspan(2);
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("2.4 GHz", fontTableTitle));
		pcell.setColspan(2);
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("5 GHz", fontTableTitle));
		pcell.setColspan(2);
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Description", fontTableTitle));
		pcell.setRowspan(2);
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Channel", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Power", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Channel", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Power", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(pcell);

		PdfPCell titleCell = new PdfPCell(table);
		titleCell.setBackgroundColor(new Color(242, 242, 242));
		titleCell.setBorderWidth(1);
		return titleCell;
	}

	private void printHiveApInfo(Set<MapNode> childNodes)
			throws DocumentException {
		PdfPCell titleCell = buildTitleCell();

		PdfPTable table = new PdfPTable(8);
		int[] widths = new int[] { 150, 150, 150, 100, 100, 100, 100, 150 };
		table.setWidths(widths);

		table = new PdfPTable(8);
		table.setWidths(widths);
		// get all AhLatestRadioAttribute data by macAddressList
		List<String> apMacList = new ArrayList<String>();
		Map<String, List<AhLatestRadioAttribute>> attributeMap = new HashMap<String, List<AhLatestRadioAttribute>>();
		for (MapNode node : childNodes) {
			if (node instanceof MapLeafNode) {
				MapLeafNode apNode = (MapLeafNode) node;
				apMacList.add(apNode.getApId());
			}
		}
		if (!apMacList.isEmpty()) {
			List<AhLatestRadioAttribute> attributeList = QueryUtil
					.executeQuery(AhLatestRadioAttribute.class, null,
							new FilterParams("apMac", apMacList));
			for (AhLatestRadioAttribute attribute : attributeList) {
				String key = attribute.getApMac();
				if (!attributeMap.containsKey(key)) {
					attributeMap.put(key,
							new ArrayList<AhLatestRadioAttribute>());
				}
				attributeMap.get(key).add(attribute);
			}
		}
		//get wifi0 and wifi1 AhLatestXif data
		Map<String, List<AhLatestXif>> wifiRadioMap =MapNodeUtil.getWifiRadioMap(childNodes);
		for (MapNode node : childNodes) {
			if (node instanceof MapLeafNode) {
				MapLeafNode apNode = (MapLeafNode) node;
				HiveAp hiveAp = hiveApAttrs.get(apNode.getId());
				if (null != hiveAp) {
					table.addCell(new Phrase(hiveAp.getHostName(), fontText));
					table.addCell(new Phrase(HiveAp.getModelEnumString(hiveAp
							.getHiveApModel()), fontText));
					table.addCell(new Phrase(HiveAp
							.getModelProtocalString(hiveAp.getHiveApModel()),
							fontText));

					String ch0;
					String pw0;
					String ch1;
					String pw1;
					// get wifiInfoMap data
					Map<String, List<Long>> curCh = getCurrentWifiInfo(
							wifiRadioMap, attributeMap.get(hiveAp.getMacAddress()));
					// fix bug 32858, don't show channel value on sensor mode
					if (HiveAp.isWifi0Available(hiveAp.getHiveApModel())
							&& !wifiXifSensor(wifiRadioMap,hiveAp.getMacAddress(), "wifi0")) {
						if (hiveAp.getWifi0().getChannel() == 0) {
							if (curCh.get("wifi0").get(0) == 0) {
								ch0 = "Auto";
							} else {
								ch0 = "Auto(" + curCh.get("wifi0").get(0) + ")";
							}
							pw0 = curCh.get("wifi0").get(1) + " dBm";
						} else {
							// fix bug 30183
							ch0 = "" + apNode.getRadioChannelBG();
							pw0 = "" + apNode.getRadioTxPowerBG() + " dBm";
						}
					} else {
						ch0 = "";
						pw0 = "";
					}

					if (HiveAp.isWifi1Available(hiveAp.getHiveApModel())
							&& !wifiXifSensor(wifiRadioMap,hiveAp.getMacAddress(), "wifi1")) {
						if (hiveAp.getWifi1().getChannel() == 0) {
							if (curCh.get("wifi1").get(0) == 0) {
								ch1 = "Auto";
							} else {
								ch1 = "Auto(" + curCh.get("wifi1").get(0) + ")";
							}
							pw1 = curCh.get("wifi1").get(1) + " dBm";
						} else {
							// fix bug 30183
							ch1 = "" + apNode.getRadioChannelA();
							pw1 = "" + apNode.getRadioTxPowerA() + " dBm";
						}
					} else {
						ch1 = "";
						pw1 = "";
					}

					table.addCell(new Phrase(ch0, fontText));
					table.addCell(new Phrase(pw0, fontText));
					table.addCell(new Phrase(ch1, fontText));
					table.addCell(new Phrase(pw1, fontText));

					table.addCell(new Phrase("", fontText));

				} else {
					log.warn("no this ap:" + node.getId());
				}
			}
		}
		PdfPCell contentCell = new PdfPCell(table);
		contentCell.setBorderWidth(1);

		PdfPTable wholeTable = new PdfPTable(1);
		wholeTable.addCell(titleCell);
		wholeTable.addCell(contentCell);

		wholeTable.setWidthPercentage(100);
		wholeTable.setHeaderRows(1);
		wholeTable.setSpacingBefore(6);
		wholeTable.setSpacingAfter(3);
		wholeTable.setSplitLate(false);

		document.add(wholeTable);
	}

	// fix bug 32858, don't show channel value on sensor mode
	private boolean wifiXifSensor(Map<String, List<AhLatestXif>> wifiRadioMap,String apMac,
			String ifName) {
		if (null == wifiRadioMap) {
			return false;
		}
		byte wifiIfMode = 0;
		for (Object obj : wifiRadioMap.keySet().toArray()) {
			String radioMapKey=obj.toString();
			if(!radioMapKey.equals(apMac)){
				continue;
			}
			List<AhLatestXif> xifList = wifiRadioMap.get(obj);
			for(AhLatestXif xif:xifList){
				if (StringUtils.isBlank(xif.getIfName())) {
					continue;
				}
				if (ifName.equals(xif.getIfName().toLowerCase().trim())) {
					wifiIfMode = xif.getIfMode();
					break;
				}
			}
		}
		return wifiIfMode == AhXIf.IFMODE_SENSOR;
	}

	float w = page.getWidth()
			- (document.leftMargin() + document.rightMargin());

	float h = page.getHeight()
			- (document.topMargin() + document.bottomMargin());

	float imageOffsetY = 0;

	boolean useHeightScale = false;

	boolean needNewPage = false;

	float mapStartX = 0;

	float mapStartY = 0;

	Image mapImage;

	private void drawMap(MapContainerNode map, boolean plannedFlag,
			Long acspId, PlanToolConfig planToolConfig) {
		useHeightScale = false;
		needNewPage = false;

		float curY1 = page.getHeight() - writer.getVerticalPosition(false);
		imageOffsetY = curY1 + 12;

		this.scale = w / map.getWidth();
		log.debug("w,h=(" + w + "," + h + ")");
		log.debug("this.scale=" + scale);

		try {

			float w0, h0;
			w0 = w;
			h0 = page.getHeight() - imageOffsetY - document.bottomMargin();

			if (h0 < 300) {
				document.newPage();
				curY1 = page.getHeight() - writer.getVerticalPosition(false);
				imageOffsetY = curY1 + 12;
				log.debug("curY1..2=" + curY1);
				log.debug("imageOffsetY..2=" + imageOffsetY);
				h0 = page.getHeight() - imageOffsetY - document.bottomMargin();
			}

			// 2 layout image
			String bg = map.getBackground();
			String path;

			float w1, h1;

			Image image;
			String mapPath;

			boolean imagePosAbsolute = false;
			if (bg == null || bg.length() == 0) {
				long mwidth = scale(map.getWidth(), scale);
				long mheight = scale(map.getHeight(), scale);

				if (mheight > h0) {
					scale = h0 / map.getHeight();
					log.debug("new scale=" + scale);

					useHeightScale = true;

					mwidth = scale(map.getWidth(), scale);
					mheight = scale(map.getHeight(), scale);
				}
				BufferedImage img1 = new BufferedImage(1, 1,
						BufferedImage.TYPE_BYTE_GRAY);
				img1.setRGB(0, 0, 0xffffff);
				image = Image.getInstance(img1, Color.white);

				image.scaleAbsolute(mwidth, mheight);
				w1 = image.getScaledWidth();
				h1 = image.getScaledHeight();

				if ((imageOffsetY + h1 + 45 + document.bottomMargin()) > page
						.getHeight()) {
					image.setAbsolutePosition(document.left(), page.getHeight()
							- (imageOffsetY + h1));
					imagePosAbsolute = true;
				}
			} else {
				path = TopoPdfReport.baseUrl + "/domains/"
						+ map.getOwner().getDomainName() + "/maps/";
				mapPath = path + bg;
				image = Image.getInstance(mapPath);

				image.scaleToFit(w0, h0);
				w1 = image.getScaledWidth();
				h1 = image.getScaledHeight();

				// fix bug start 2010.1.5
				if ((h0 - h1) < (w0 - w1)) {
					scale = h1 / map.getHeight();
					useHeightScale = true;
				}
				// fix bug end

				// when height > page.getHeight() - 45, the position will auto move to next page.
				if ((imageOffsetY + h1 + 45 + document.bottomMargin()) > page
						.getHeight()) {
					image.setAbsolutePosition(document.left(), page.getHeight()
							- (imageOffsetY + h1));
					imagePosAbsolute = true;
				}
			}

			mapStartX = document.left();

			mapImage = image;
			document.add(mapImage);

			if (imagePosAbsolute) {
				needNewPage = true;
				mapStartY = imageOffsetY;
			} else {
				float currentVerPos = writer.getVerticalPosition(false);
				mapStartY = page.getHeight() - currentVerPos - h1;
			}

			// 2009.12.2
			drawHeatmap.canvasWidth = (int) w1;
			drawHeatmap.canvasHeight = (int) h1;
			drawHeatmap.pdfReport = this;
			if(layers != 0){
				if (plannedFlag) {
					drawHeatmapForPlanAP(map, planToolConfig);
				} else {
					drawHeatmapForAP(map, acspId);
				}
			} else {
				if(plannedFlag){
					setPlannedAPsName(map);
				}
			}

			PdfContentByte cb = writer.getDirectContent();
			// g2 = cb.createGraphics(page.getWidth(), page.getHeight());
//			g2 = new PdfGraphics2D(cb, page.getWidth(), page.getHeight());
			g2 = cb.createGraphicsShapes(page.getWidth(), page.getHeight());
			g2.dispose();

			if (gridChecked) {
				drawGrid(w1, h1, imageOffsetY, map, useHeightScale);
			}
		} catch (Exception e) {
			log.error("drawMap() exception", e);
		}
	}

	private List<MapContainerNode> sortFloors(MapContainerNode building) {
		List<MapContainerNode> floors = new ArrayList<MapContainerNode>();
		for (MapNode mapNode : building.getChildNodes()) {
			if (mapNode.isLeafNode()) {
				continue;
			}
			floors.add((MapContainerNode) mapNode);
		}
		Collections.sort(floors, new Comparator<MapContainerNode>() {
			@Override
			public int compare(MapContainerNode o1, MapContainerNode o2) {
				return o2.getMapOrder() - o1.getMapOrder();
			}
		});
		return floors;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}

		if (bo instanceof MapContainerNode) {
			MapContainerNode mapContainerNode = (MapContainerNode) bo;
			if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {

				for (MapNode mapNode : mapContainerNode.getChildNodes()) {
					if (mapNode.isLeafNode()) {
						continue;
					}
					MapContainerNode floor = (MapContainerNode) mapNode;
					floor.getChildNodes().size();
					BoMgmt.getPlannedApMgmt().loadPlannedAPs(floor,
							floor.getChildNodes());
					floor.getPerimeter().size();
				}
			} else if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
				for (MapNode child : mapContainerNode.getChildNodes()) {
					if (child.isLeafNode()) {
						if (null != ((MapLeafNode) child).getHiveAp()) {
							((MapLeafNode) child).getHiveAp().getHiveApModel();
						}
					}
				}
				mapContainerNode.getPerimeter().size();
				mapContainerNode.getWalls().size();
				mapContainerNode.getPlannedAPs().size();

				MapContainerNode building = (mapContainerNode).getParentMap();
				for (MapNode mapNode : building.getChildNodes()) {
					if (mapNode.isLeafNode()) {
						continue; // There should not be any leaf nodes anyway
					}
					MapContainerNode floor = (MapContainerNode) mapNode;
					floor.getChildNodes().size();
					floor.getPlannedAPs().size();
				}
			} else {
				for (MapNode child : mapContainerNode.getChildNodes()) {
					if (child.isLeafNode()) {
						if (((MapLeafNode) child).getHiveAp() != null) {
							((MapLeafNode) child).getHiveAp().getHiveApModel();
						}
					}
				}
				mapContainerNode.getPerimeter().size();
				mapContainerNode.getWalls().size();
				mapContainerNode.getPlannedAPs().size();
			}

			if (mapContainerNode.getParentMap() != null) {
				mapContainerNode.getParentMap().getId();
			}

			if (mapContainerNode.getChildNodes() != null) {
				mapContainerNode.getChildNodes().size();
				for (MapNode node : mapContainerNode.getChildNodes()) {
					if (!node.isLeafNode()) {
						if (((MapContainerNode) node).getChildNodes().size() > 0) {
							((MapContainerNode) node).getChildNodes().size();
						}
					}
				}
			}

			if (mapContainerNode.getChildLinks() != null) {
				mapContainerNode.getChildLinks().size();
			}
		}

		if (bo instanceof MapNode) {
			MapNode mapNode = (MapNode) bo;
			if (mapNode.isLeafNode()) {
				if (((MapLeafNode) mapNode).getHiveAp() != null) {
					((MapLeafNode) mapNode).getHiveAp().getId();
				}
			} else {
				if (((MapContainerNode) mapNode).getChildNodes() != null) {
					for (MapNode child : ((MapContainerNode) mapNode)
							.getChildNodes()) {
						if (child.isLeafNode()) {
							if (((MapLeafNode) child).getHiveAp() != null) {
								((MapLeafNode) child).getHiveAp().getId();
							}
						} else {
							if (((MapContainerNode) child).getChildNodes() != null) {
								for (MapNode childMapNode : ((MapContainerNode) child)
										.getChildNodes()) {
									if (childMapNode.isLeafNode()) {
										if (((MapLeafNode) childMapNode)
												.getHiveAp() != null) {
											((MapLeafNode) childMapNode)
													.getHiveAp().getId();
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	private void drawHeatmapForAP(MapContainerNode mapContainerNode, Long acspId)
			throws Exception {
		Set<MapNode> nodes = mapContainerNode.getChildNodes();

		log.info("drawHeatmapForAP# scale: " + scale);
		log.info("drawHeatmapForAP# rssiThreshold: -" + rssiThreshold);

		if (scale > 0) {
			log.info("drawHeatmapForAP# canvas: (" + drawHeatmap.canvasWidth
					+ ", " + drawHeatmap.canvasHeight + "), layers: " + layers
					+ ", actualWidth: " + mapContainerNode.getActualWidth());
			if (nodes.size() > 0 && drawHeatmap.canvasWidth > 0
					&& drawHeatmap.canvasHeight > 0
					&& mapContainerNode.getActualWidth() > 0) {
				log.info("drawHeatmapForAP# nodes: " + nodes.size());
				double x = 0, y = 0;

				log.info("drawHeatmapForAP# x: " + x + ", y: " + y);
				int heatmapResolution = BoMgmt.getLocationTracking()
						.computeRssi(mapContainerNode, nodes,
								drawHeatmap.canvasWidth,
								drawHeatmap.canvasHeight, null, acspId, null,
								x, y, (frequency & 1) > 0, (frequency & 2) > 0,
								getShadesPerColor(), 0);

				if (heatmapResolution >= 0) {
					computeRssiMapForPdf(mapContainerNode, nodes);
					drawHeatmap.streamMapImage(mapContainerNode,
							heatmapResolution, frequency, layers, (short) -1,
							1, null, (short) 0, 0, 0);
				}

			}
		}

	}

	private void computeRssiMapForPdf(MapContainerNode mapContainerNode,
			Set<MapNode> nodes) {
		WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapContainerNode.getMapToMetric());
		int p = 1;

		double complexity = drawHeatmap.canvasWidth * drawHeatmap.canvasHeight
				* nodes.size();
		double mapToMetric = mapContainerNode.getMapToMetric();
		double squareSize = mapContainerNode.getWidth() * mapToMetric
				/ drawHeatmap.canvasWidth;
		int complexityLimit = Integer.parseInt(System
				.getProperty("heatMap.complexity"));
		if (null != walls) {
			p = p * 2;
			complexity /= 4;
			squareSize *= 2;
		}
		while (complexity > complexityLimit || squareSize < 0.01) {
			p = p * 2;
			complexity /= 4;
			squareSize *= 2;

		}

		if (p != 1) {
			short _mapColors[][] = mapContainerNode.getMapColors();
			short _mapChannels[][] = mapContainerNode.getMapChannels();
			
			if(_mapColors == null || _mapChannels ==null){
				return;
			}
			short tmp_mapColors[][] = new short[(int) mapContainerNode
					.getMapColors().length][(int) drawHeatmap.canvasHeight];
			short tmp_mapChannels[][] = new short[(int) mapContainerNode
					.getMapChannels().length][(int) drawHeatmap.canvasHeight];

			short mapColors[][] = new short[(int) drawHeatmap.canvasWidth][(int) drawHeatmap.canvasHeight];
			short mapChannels[][] = new short[(int) drawHeatmap.canvasWidth][(int) drawHeatmap.canvasHeight];

			// Lateral extension
			for (int i = 0; i < _mapColors.length; i += 1) {
				for (int j = 0; j < _mapColors[i].length; j += 1) {
					for (int k = 0; k < p; k++) {
						tmp_mapColors[i][k + j * p] = _mapColors[i][j];
					}
				}
			}
			for (int i = 0; i < _mapChannels.length; i += 1) {
				for (int j = 0; j < _mapChannels[i].length; j += 1) {
					for (int k = 0; k < p; k++) {
						tmp_mapChannels[i][k + j * p] = _mapChannels[i][j];
					}
				}
			}

			// Longitudinal extension
			for (int i = 0; i < tmp_mapColors.length; i += 1) {
				for (int j = 0; j < tmp_mapColors[i].length; j += 1) {
					for (int k = 0; k < p; k++) {
						mapColors[k + i * p][j] = tmp_mapColors[i][j];
					}
				}
			}
			for (int i = 0; i < tmp_mapChannels.length; i += 1) {
				for (int j = 0; j < tmp_mapChannels[i].length; j += 1) {
					for (int k = 0; k < p; k++) {
						mapChannels[k + i * p][j] = tmp_mapChannels[i][j];
					}
				}
			}
			mapContainerNode.setMapChannels(mapChannels);
			mapContainerNode.setMapColors(mapColors);
		}
	}

	private static WallLoss[] getWallsMetric(List<Wall> walls,
			double mapToMetric) {
		if (walls.size() == 0) {
			return null;
		}
		WallLoss[] wallsMetric = new Search.WallLoss[walls.size()];
		for (int i = 0; i < walls.size(); i++) {
			Wall wall = walls.get(i);
			WallLoss wallLoss = new WallLoss();
			wallsMetric[i] = wallLoss;
			wallLoss.x1 = wall.getX1() * mapToMetric;
			wallLoss.x2 = wall.getX2() * mapToMetric;
			wallLoss.y1 = wall.getY1() * mapToMetric;
			wallLoss.y2 = wall.getY2() * mapToMetric;
			short wi = Wall.getWallIndex(wall.getType());
			wallLoss.width = Wall.wallWidth[wi];
			wallLoss.absorption = Wall.wallAbsorption[wi];
		}
		return wallsMetric;
	}

	private void drawHeatmapForPlanAP(MapContainerNode map,
			PlanToolConfig planToolConfig) throws Exception {
		if (scale > 0 && map != null && drawHeatmap.canvasWidth > 0
				&& map.getActualWidth() > 0) {
			log.info("drawHeatmapForPlanAP# canvas (" + drawHeatmap.canvasWidth
					+ ", " + drawHeatmap.canvasHeight + "), layers: " + layers
					+ ", rssiThreshold: -" + rssiThreshold + ", frequency: "
					+ frequency);

			boolean useA = (frequency & 1) > 0;
			Map<Short, Short> ch1IndexMap = new HashMap<Short, Short>();
			Map<Short, Short> ch2IndexMap = new HashMap<Short, Short>();
			BoMgmt.getLocationTracking().assignChannels(map, planToolConfig,
					ch1IndexMap, ch2IndexMap);
			setPlannedAPsName(map);
			Collection<JSONObject> jsons = preparePlannedAPs(map, ch1IndexMap,
					ch2IndexMap);

			int chci = -1;
			for (PlannedAP ap : map.getPlannedAPs()) {
				for (JSONObject json : jsons) {
					long apid = Long.valueOf(json.getString("nodeId")
							.substring(1));
					if (ap.getId() == apid) {
						String key;
						if (useA) {
							key = "ch2i";
						} else {
							key = "ch1i";
						}
						if(json.has(key)){
							chci = json.getInt(key);
						} else  {
							chci=-1;
						}
						break;
					}
				}
				log.debug("drawHeatmapForPlanAP# [" + ap.getId()
						+ "] channel color index: " + chci);

				// reset all page parameters for each AP
				drawHeatmap.rssiThreshold = rssiThreshold;
				drawHeatmap.rateThreshold = rateThreshold;
				drawHeatmap.snrThreshold = snrThreshold;
				drawHeatmap.frequency = frequency;
				drawHeatmap.layers = layers; // layers was changed to 16 in
												// DrawHeatmapAction, so
				// need reset.

				drawHeatmap
						.getPredictedLap(map, useA, ap, (short) chci,
								this.channelWidth,
								planToolConfig.getFadeMargin(), true);
			}
		}

	}

	private void setPlannedAPsName(MapContainerNode map) {
		for (PlannedAP plannedAP : map.getPlannedAPs()) {
			String hwModel = MgrUtil.getEnumString("enum.hiveAp.model."
					+ plannedAP.apModel);
			// String hostName = hwModel.substring(4) + "-" +
			// Long.toString(1000000000 +
			// plannedAP.getId()).substring(3);
			String hostName = plannedAP.hostName;

			boolean useA = (frequency & 1) > 0;

			if (apLabels == null) {
				apLabels = "hn";
			}
			if (apLabels.equals("hn")) {
				plannedAP.hostName = hostName;
			} else if (apLabels.equals("cp")) {
				String ch0;
				String ch1;
				short wifi0Channel = plannedAP.wifi0Channel;
				if (wifi0Channel == 0) {
					wifi0Channel = plannedAP.autoWifi0Channel;
					ch0 = ""
							+ (wifi0Channel < 0 ? -wifi0Channel : wifi0Channel);
				} else {
					ch0 = ""
							+ (wifi0Channel < 0 ? -wifi0Channel : wifi0Channel)
							+ "*";
				}
				short wifi1Channel = plannedAP.wifi1Channel;
				if (wifi1Channel == 0) {
					wifi1Channel = plannedAP.autoWifi1Channel;
					ch1 = ""
							+ (wifi1Channel < 0 ? -wifi1Channel : wifi1Channel);
				} else {
					ch1 = ""
							+ (wifi1Channel < 0 ? -wifi1Channel : wifi1Channel)
							+ "*";
				}

				if (useA) {
					plannedAP.hostName = "Ch " + ch1 + " - Pwr "
							+ plannedAP.wifi1Power;
				} else {
					plannedAP.hostName = "Ch " + ch0 + " - Pwr "
							+ plannedAP.wifi0Power;
				}
			} else if (apLabels.equals("at")) {
				plannedAP.hostName = hwModel;
			} else if (apLabels.equals("no")) {
				plannedAP.hostName = "";
			} else {
				plannedAP.hostName = hostName;
			}
		}
	}

	private Collection<JSONObject> preparePlannedAPs(
			MapContainerNode mapContainerNode, Map<Short, Short> ch1IndexMap,
			Map<Short, Short> ch2IndexMap) throws Exception {
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
			JSONObject jo;
			jo = new JSONObject();
			jo.put("nodeId", "s" + plannedAP.getId());

			// wifi0
			short wifi0Channel = plannedAP.wifi0Channel;
			if (wifi0Channel == 0) {
				wifi0Channel = plannedAP.autoWifi0Channel;
			}
			Short ch1i = ch1IndexMap.get(wifi0Channel);

			// wifi1
			short wifi1Channel = plannedAP.wifi1Channel;
			if (wifi1Channel == 0) {
				wifi1Channel = plannedAP.autoWifi1Channel;
			}
			Short ch2i = ch2IndexMap.get(wifi1Channel);
			jo.put("ch1i", ch1i);
			jo.put("ch2i", ch2i);

			jsonNodes.add(jo);
		}
		return jsonNodes;
	}

	private short getShadesPerColor() {
		return (short) (rssiThreshold - 35 + 1);
	}

	private void drawGrid(float w, float h, float curY, MapContainerNode map2,
			boolean useHeightScale2) {
		double actualGridSize = getGridSize(map2.getActualWidth());

		if (useHeightScale2) {
			actualGridSize = getGridSize(map2.getActualHeight());
		}

		double gridSize = actualGridSize * map2.getWidth()
				/ map2.getActualWidth();

		int wGridCount = (int) (map2.getActualWidth() / actualGridSize) + 1;
		int hGridCount = (int) (map2.getActualHeight() / actualGridSize) + 1;

		int x1, y1, x2, y2, x, y;

		Color color = g2.getColor();
		Stroke stroke = g2.getStroke();

		g2.setColor(Color.gray);
		g2.setStroke(new BasicStroke(0.1f));

		int txtHeight = 6;
		int txtWidth = 4;

		java.awt.Font font = new java.awt.Font("Arial", java.awt.Font.PLAIN,
				txtHeight);
		g2.setFont(font);

		// draw unit
		String unitName = MapContainerNode.LENGTH_UNITS[map2.getLengthUnit() - 1]
				.getValue();

		// draw vertical line
		y1 = (int) (0 + curY - txtHeight);
		y2 = (int) (h + curY);

		for (int i = 0; i < wGridCount; i++) {
			x = (int) (gridSize * scale * i + document.leftMargin());

			if ((i + 1 == wGridCount)
					&& ((int) actualGridSize * i == (int) map2.getActualWidth())) {
				break;
			}
			g2.drawLine(x, y1, x, y2);

			g2.drawString(String.valueOf((int) (actualGridSize * i)), x + 1, y1
					+ txtHeight - 2);

			if (i == 0) {
				g2.drawString(unitName, x + 2 + txtWidth, y1 + txtHeight - 2);
			}
		}

		String valuex = String.valueOf((int) (hGridCount * actualGridSize));
		int txtCount = valuex.length();
		int totalTxtWidth = txtWidth * txtCount;
		// draw horizontal line
		x1 = (int) (0 + document.leftMargin() - totalTxtWidth);
		x2 = (int) (w + document.leftMargin());
		for (int i = 0; i < hGridCount; i++) {
			y = (int) (gridSize * scale * i + curY);
			g2.drawLine(x1, y, x2, y);

			String value = String.valueOf((int) (actualGridSize * i));

			g2.drawChars(value.toCharArray(), 0, value.length(), x1 + txtWidth
					* (txtCount - value.length()), y + txtHeight);
		}

		g2.setColor(color);
		g2.setStroke(stroke);
	}

	protected double getGridSize(double actualSize) {
		int gridCount = 8;
		double gridSize = actualSize / gridCount;
		double scale = 1;
		while (gridSize >= 10) {
			gridSize = gridSize / 10;
			scale *= 10;
		}
		if (gridSize > 5.5) {
			gridSize = 10 * scale;
		} else if (gridSize > 3.2) {
			gridSize = 5 * scale;
		} else if (gridSize > 1.2) {
			gridSize = 2.5 * scale;
		} else {
			gridSize = scale;
		}
		if ((int) (actualSize / gridSize) > gridCount
				|| (int) gridSize != gridSize) {
			gridSize *= 2;
		}
		return gridSize;
	}

	private long scale(double d, double scale) {
		return Math.round(d * scale);
	}

	public BufferedImage readImage(String fileName) throws IOException {
		String type = fileName.substring(fileName.lastIndexOf(".") + 1);
		InputStream in = null;
		BufferedImage bi;
		try {
			in = new FileInputStream(fileName);
			bi = readImage(in, type);
			
		} finally {
			if(in != null){
				in.close();
			}
		}
		
		return bi;
	}

	public BufferedImage readImage(InputStream in, String type)
			throws IOException {
		BufferedImage bi;
		ImageInputStream iis = null;
		try{
			Iterator<ImageReader> readers = ImageIO
					.getImageReadersByFormatName(type);
			ImageReader reader = readers.next();
			iis = ImageIO.createImageInputStream(in);
			reader.setInput(iis, true);
			bi= reader.read(0);
		} finally {
			if(iis != null){
				iis.close();
			}
		}
		return bi;
	}

	private void drawAp(int x, int y, String apName)
			throws MalformedURLException, IOException, DocumentException {
		int iconradius = 9;
		int textRectHeight = 7;
		int textRectWidth = apName.length() * 4 + 6;
		int iconMargin = 1;

		if (apName.contains("Pwr ")) {
			textRectWidth -= 8;
		}

		String apIconPath = baseUrl + "/images/nodes/" + "green/green_"
				+ this.apIcon;
		BufferedImage image = readImage(apIconPath);

		g2.drawImage(image, x - iconradius, y - iconradius, 2 * iconradius,
				2 * iconradius, null);

		if (apName.length() == 0) {
			return; // No label
		}

		Color color = g2.getColor();
		Stroke stroke = g2.getStroke();

		g2.setColor(Color.white);
		g2.fillRect(x + iconradius - iconMargin, y - (textRectHeight / 2) - 1,
				textRectWidth + 1, textRectHeight + 1);

		g2.setColor(Color.gray);
		g2.setStroke(new BasicStroke(0.1f));
		g2.drawRect(x + iconradius - iconMargin, y - (textRectHeight / 2) - 1,
				textRectWidth + 1, textRectHeight + 1);

		g2.setColor(new Color(0, 51, 102));
		java.awt.Font font = new java.awt.Font("arial", java.awt.Font.PLAIN,
				textRectHeight);
		g2.setFont(font);
		g2.drawChars(apName.toCharArray(), 0, apName.length(), x + iconradius,
				y + (textRectHeight / 2));

		g2.setColor(color);
		g2.setStroke(stroke);
	}

	private Map<String, List<Long>> getCurrentWifiInfo(
			Map<String, List<AhLatestXif>> wifiRadioMap,
			List<AhLatestRadioAttribute> attributeList) {
		Map<String, List<Long>> wifiInfoMap = new HashMap<String, List<Long>>();
		wifiInfoMap.put("wifi0", Arrays.asList(new Long[] { 0l, 0l }));
		wifiInfoMap.put("wifi1", Arrays.asList(new Long[] { 0l, 0l }));
		if (null == wifiRadioMap || null == attributeList) {
			return wifiInfoMap;
		}
		for (AhLatestRadioAttribute attribute : attributeList) {
			String radioMapKey = attribute.getApMac();
			if (!wifiRadioMap.containsKey(radioMapKey)) {
				continue;
			}
			List<AhLatestXif> xifList = wifiRadioMap.get(radioMapKey);
			for(AhLatestXif xif:xifList){
				String ifName = xif.getIfName().trim();
				if ("wifi0".equals(ifName)) {
					List<Long> wifiInfoList = new ArrayList<Long>();
					wifiInfoList.add(attribute.getRadioChannel());
					wifiInfoList.add(attribute.getRadioTxPower());
					wifiInfoMap.put("wifi0", wifiInfoList);
				} else if ("wifi1".equals(ifName)) {
					List<Long> wifiInfoList = new ArrayList<Long>();
					wifiInfoList.add(attribute.getRadioChannel());
					wifiInfoList.add(attribute.getRadioTxPower());
					wifiInfoMap.put("wifi1", wifiInfoList);
				}
			}
			
		}
		return wifiInfoMap;
	}

	private Map<Long, HiveAp> hiveApAttrs = new HashMap<Long, HiveAp>();

	private void drawPlannedAps(PdfWriter writer, float offsety,
			Set<PlannedAP> plannedAPs) throws MalformedURLException,
			IOException, DocumentException {
		PdfContentByte cb = writer.getDirectContent();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(1f);
		cb.setGState(gs1);

		List<Long> plannedApIds = new ArrayList<Long>();
		for (PlannedAP ap : plannedAPs) {
			plannedApIds.add(ap.getId());

			HiveAp hiveAp = new HiveAp(ap.apModel);
			hiveAp.setHostName(ap.hostName);

			HiveApWifi wifi0 = new HiveApWifi();
			wifi0.setChannel(ap.wifi0Channel);
			wifi0.setPower(ap.wifi0Power);
			hiveAp.setWifi0(wifi0);

			HiveApWifi wifi1 = new HiveApWifi();
			wifi1.setChannel(ap.wifi1Channel);
			wifi1.setPower(ap.wifi1Power);
			hiveAp.setWifi1(wifi1);

			hiveAp.setSimulated(true);

			hiveApAttrs.put(ap.getId(), hiveAp);
		}

		log.debug("plannedAPs size=" + plannedAPs.size());
		for (PlannedAP mapNode : plannedAPs) {
			long x = scale(mapNode.x, scale);
			long y = scale(mapNode.y, scale);
			String iconName = "target_icon.png";
			log.debug("x,y=(" + mapNode.x + "," + mapNode.y + ")->(" + x + ","
					+ y + "), iconName=" + iconName);

			HiveAp hiveAp = hiveApAttrs.get(mapNode.getId());
			if (null != hiveAp) {
				this.apIcon = iconName;
				drawAp((int) (x + document.leftMargin()), (int) (y + offsety),
						mapNode.hostName);
			}
		}
	}

	private void drawAps(PdfWriter writer, float offsety,
			Set<MapNode> childNodes) throws MalformedURLException, IOException,
			DocumentException {
		PdfContentByte cb = writer.getDirectContent();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(1f);
		cb.setGState(gs1);

		List<Long> leafNodeIds = new ArrayList<Long>();
		for (MapNode mapNode : childNodes) {
			if (mapNode.isLeafNode()) {
				leafNodeIds.add(mapNode.getId());
			}
		}

		if (!leafNodeIds.isEmpty()) {
			List<?> attributes_list = QueryUtil
					.executeQuery(
							"select bo.id, bo.hiveAp.hostName, bo.hiveAp.hiveApModel, bo.hiveAp.hiveApType,"
									+ " bo.hiveAp.wifi0.channel, bo.hiveAp.wifi0.power, bo.hiveAp.wifi1.channel, bo.hiveAp.wifi1.power, "
									+ " bo.hiveAp.simulated, bo.hiveAp.macAddress, bo.hiveAp.ipAddress from "
									+ MapLeafNode.class.getSimpleName() + " bo",
							null, new FilterParams("id", leafNodeIds));
			for (Object obj : attributes_list) {
				Object[] attributes = (Object[]) obj;
				HiveAp hiveAp = new HiveAp();

				hiveAp.setHostName((String) attributes[1]);
				hiveAp.setHiveApModel((Short) attributes[2]);
				hiveAp.setHiveApType((Short) attributes[3]);
				hiveAp.init();
				hiveAp.initInterface();

				HiveApWifi wifi0 = new HiveApWifi();
				wifi0.setChannel((Integer) attributes[4]);
				wifi0.setPower((Integer) attributes[5]);
				hiveAp.setWifi0(wifi0);

				HiveApWifi wifi1 = new HiveApWifi();
				wifi1.setChannel((Integer) attributes[6]);
				wifi1.setPower((Integer) attributes[7]);
				hiveAp.setWifi1(wifi1);

				Boolean simulated = (Boolean) attributes[8];
				if (null != simulated) {
					hiveAp.setSimulated(simulated);
				}

				hiveAp.setMacAddress((String) attributes[9]);
				hiveAp.setIpAddress((String) attributes[10]);
				hiveApAttrs.put((Long) attributes[0], hiveAp);
			}
		}
		
		//fix bug 30076
		BoMgmt.getLocationTracking().fetchRadioAttributes(childNodes);
		
		log.debug("AP size=" + childNodes.size());
		for (MapNode mapNode : childNodes) {
			long x = scale(mapNode.getX(), scale);
			long y = scale(mapNode.getY(), scale);
			log.debug("x,y=(" + mapNode.getX() + "," + mapNode.getY() + ")->("
					+ x + "," + y + ")");

			if (mapNode.isLeafNode()) {
				// String apName = ((MapLeafNode) mapNode).getApName();
				HiveAp hiveAp = hiveApAttrs.get(mapNode.getId());

				if (null != hiveAp) {
					String apName = getApName((MapLeafNode) mapNode, hiveAp);
					this.apIcon = mapNode.getIconName();
					drawAp((int) (x + document.leftMargin()),
							(int) (y + offsety), apName);
				}
			} else {
			}
		}
	}

	private String getApName(MapLeafNode mapLeafNode, HiveAp hiveAp) {
		String apName = "";

		boolean useA = (frequency & 1) > 0;

		if (rapLabels == null) {
			rapLabels = "hn";
		}
		if (rapLabels.equals("hn")) {
			apName = hiveAp.getHostName();
		} else if (rapLabels.equals("cp")) {

			short channel = useA ? mapLeafNode.getRadioChannelA() : mapLeafNode
					.getRadioChannelBG();
			boolean autoChannel = useA ? mapLeafNode.isAutoChannelA()
					: mapLeafNode.isAutoChannelBG();
			float eirp = useA ? mapLeafNode.getRadioEirpA() : mapLeafNode
					.getRadioEirpBG();
			//fix bug 30076 keep the same power with page display.
			float power = useA ? mapLeafNode.getRadioTxPowerA() : mapLeafNode
					.getRadioTxPowerBG();
			boolean autoPower = useA ? mapLeafNode.isAutoTxPowerA()
					: mapLeafNode.isAutoTxPowerBG();

			if (channel == 0 || power == 0) {
				apName = hiveAp.getHostName();
			} else {
				String ch = channel + (autoChannel ? "" : "*");
				String pwr = (int) power + "";
				if (power != (int) power) {
					pwr = String.format("%1$.2f", power);
				}
				if (!autoPower) {
					pwr += "*";
				}
				apName = "Ch " + ch + " - Pwr " + pwr;
			}

		} else if (rapLabels.equals("ni")) {
			apName = hiveAp.getMacAddress();
		} else if (rapLabels.equals("ip")) {
			apName = hiveAp.getIpAddress();
		} else if (rapLabels.equals("ac")) {
			SimpleHiveAp s_hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					hiveAp.getMacAddress());
			apName = s_hiveAp.getActiveClientCount() + " clients";
		} else if (rapLabels.equals("no")) {
			apName = "";
		} else {
			apName = hiveAp.getHostName();
		}

		return apName;
	}

	private void printDeviceTotal(Long mapId, boolean plannedFlag)
			throws DocumentException {
		PdfPTable titleTable = new PdfPTable(3);
		int[] widths = new int[] { 150, 150, 150 };
		titleTable.setWidths(widths);

		// title
		PdfPCell pcell = new PdfPCell(new Phrase("Model", fontTableTitle));

		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		titleTable.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Part number", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		titleTable.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Total", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		titleTable.addCell(pcell);

		PdfPCell titleCell = new PdfPCell(titleTable);
		titleCell.setBackgroundColor(new Color(242, 242, 242));
		titleCell.setBorderWidth(1);

		PdfPTable contentTable = new PdfPTable(3);
		contentTable.setWidths(widths);

		// data
		String query = "";
		if (plannedFlag) {
			query = "select apmodel,count(1) from planned_ap where parent_map_id='"
					+ mapId + "' GROUP BY apmodel";
		} else {
			query = "select hiveapmodel,count(1) from hive_ap where map_container_id = '"
					+ mapId + "' group by hiveapmodel";
		}

		List<?> totalMap = QueryUtil.executeNativeQuery(query);
		MapContainerNode map = QueryUtil.findBoById(MapContainerNode.class,
				mapId);
		PlanToolConfig planToolConfig = PlanToolAction.getPlanToolConfig(map
				.getOwner().getId());
		int suffixIndex = 0;
		if (planToolConfig.getCountryCode() != CountryCode.COUNTRY_CODE_US) {
			suffixIndex = 1;
		}
		for (Object total : totalMap) {
			Object[] objects = (Object[]) total;
			contentTable.addCell(new Phrase(HiveAp
					.getModelEnumString(((Short) objects[0]).shortValue()),
					fontText));
			if (MgrUtil.getEnumString("enum.hiveAp.part.number.model."
					+ objects[0]) == null) {
				contentTable.addCell(new Phrase(" ", fontText));
			} else {
				if (((Short) objects[0]).shortValue() == HiveAp.HIVEAP_MODEL_BR200
						|| ((Short) objects[0]).shortValue() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
						|| HiveAp.isSwitchProduct(((Short) objects[0])
								.shortValue())) {
					contentTable.addCell(new Phrase(MgrUtil
							.getEnumString("enum.hiveAp.part.number.model."
									+ objects[0]), fontText));
				} else {
					contentTable
							.addCell(new Phrase(
									MgrUtil.getEnumString("enum.hiveAp.part.number.model."
											+ objects[0])
											+ MgrUtil
													.getEnumString("enum.hiveAp.part.number.suffix."
															+ suffixIndex),
									fontText));
				}
			}
			contentTable.addCell(new Phrase(objects[1].toString(), fontText));
		}

		if (totalMap.isEmpty()) {
			String msg = "There are no "
					+ NmsUtil.getOEMCustomer().getAccessPonitName() + "s on "
					+ map.getMapName() + ", and there are no sub-maps on it.";
			contentTable = new PdfPTable(1);
			contentTable.addCell(new Phrase(msg, fontText));
		}

		PdfPCell contentCell = new PdfPCell(contentTable);
		contentCell.setBorderWidth(1);

		PdfPTable wholeTable = new PdfPTable(1);
		wholeTable.addCell(titleCell);
		wholeTable.addCell(contentCell);

		wholeTable.setWidthPercentage(100);
		wholeTable.setHeaderRows(1);
		wholeTable.setSpacingBefore(6);
		wholeTable.setSpacingAfter(3);
		wholeTable.setSplitLate(false);

		document.add(wholeTable);
	}

	private void printDeviceTotalForDomain(MapContainerNode map,
			PlanToolConfig planToolConfig) throws DocumentException {
		PdfPTable titleTable = new PdfPTable(3);
		int[] widths = new int[] { 150, 150, 150 };
		titleTable.setWidths(widths);

		// title
		PdfPCell pcell = new PdfPCell(new Phrase("Model", fontTableTitle));

		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		titleTable.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Part number", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		titleTable.addCell(pcell);

		pcell = new PdfPCell(new Phrase("Total", fontTableTitle));
		pcell.setHorizontalAlignment(Element.ALIGN_CENTER);
		pcell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		titleTable.addCell(pcell);

		PdfPCell titleCell = new PdfPCell(titleTable);
		titleCell.setBackgroundColor(new Color(242, 242, 242));
		titleCell.setBorderWidth(1);

		PdfPTable contentTable = new PdfPTable(3);
		contentTable.setWidths(widths);

		String str = Arrays.toString(getAllNodesId(map).toArray());
		str = str.substring(1, str.length() - 1);

		// data
		String query = "";
		if (str != null && !str.isEmpty()) {
			query = "select model, sum(count) from (select hiveapmodel as model,count(1) from hive_ap where owner = '"
					+ map.getOwner().getId()
					+ "' and map_container_id in ("
					+ str
					+ ") group by hiveapmodel union all select apmodel as model,count(1) from planned_ap  where owner = '"
					+ map.getOwner().getId()
					+ "' and parent_map_id in ("
					+ str + ") group by apmodel) t group by model";
		} else {
			query = "select model, sum(count) from (select hiveapmodel as model,count(1) from hive_ap where owner = '"
					+ map.getOwner().getId()
					+ "' and map_container_id is not null group by hiveapmodel union all select apmodel as model,count(1) from planned_ap  where owner = '"
					+ map.getOwner().getId()
					+ "' and parent_map_id is not null group by apmodel) t group by model";
		}
		List<?> totalMap = QueryUtil.executeNativeQuery(query);
		int suffixIndex = 0;
		if (planToolConfig.getCountryCode() != CountryCode.COUNTRY_CODE_US) {
			suffixIndex = 1;
		}
		for (Object total : totalMap) {
			Object[] objects = (Object[]) total;
			contentTable.addCell(new Phrase(HiveAp
					.getModelEnumString(((Short) objects[0]).shortValue()),
					fontText));
			if (MgrUtil.getEnumString("enum.hiveAp.part.number.model."
					+ objects[0]) == null) {
				contentTable.addCell(new Phrase(" ", fontText));
			} else {
				if (((Short) objects[0]).shortValue() == HiveAp.HIVEAP_MODEL_BR200
						|| ((Short) objects[0]).shortValue() == HiveAp.HIVEAP_MODEL_BR200_LTE_VZ
						|| HiveAp.isSwitchProduct(((Short) objects[0])
								.shortValue())) {
					contentTable.addCell(new Phrase(MgrUtil
							.getEnumString("enum.hiveAp.part.number.model."
									+ objects[0]), fontText));
				} else {
					contentTable
							.addCell(new Phrase(
									MgrUtil.getEnumString("enum.hiveAp.part.number.model."
											+ objects[0])
											+ MgrUtil
													.getEnumString("enum.hiveAp.part.number.suffix."
															+ suffixIndex),
									fontText));
				}
			}
			contentTable.addCell(new Phrase(objects[1].toString(), fontText));
		}

		if (totalMap.isEmpty()) {
			String msg = "There are no "
					+ NmsUtil.getOEMCustomer().getAccessPonitName() + "s on "
					+ map.getMapName() + ", and there are no sub-maps on it.";
			contentTable = new PdfPTable(1);
			contentTable.addCell(new Phrase(msg, fontText));
		}

		PdfPCell contentCell = new PdfPCell(contentTable);
		contentCell.setBorderWidth(1);

		PdfPTable wholeTable = new PdfPTable(1);
		wholeTable.addCell(titleCell);
		wholeTable.addCell(contentCell);

		wholeTable.setWidthPercentage(100);
		wholeTable.setHeaderRows(1);
		wholeTable.setSpacingBefore(6);
		wholeTable.setSpacingAfter(3);
		wholeTable.setSplitLate(false);

		document.add(wholeTable);
	}

	private List<Long> getAllNodesId(MapContainerNode mapNode) {
		MapContainerNode map = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, mapNode.getId(), this);
		List<Long> array = new ArrayList<Long>();
		array.add(map.getId());
		for (MapNode node : map.getChildNodes()) {
			if (node instanceof MapLeafNode) {
				if (((MapLeafNode) node).getHiveAp() != null) {
					array.add(node.getId());
				}
			}

			if (node instanceof MapContainerNode) {
				array.add(node.getId());
				if (((MapContainerNode) node).getChildNodes() != null
						&& ((MapContainerNode) node).getChildNodes().size() > 0) {
					array.addAll(getAllNodesId((MapContainerNode) node));
				}
			}
		}
		return array;
	}
}