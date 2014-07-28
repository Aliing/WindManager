package com.ah.bo.mgmt.impl;

/*
 * @author Chris Scheers
 */

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.netlib.blas.DGEMV;
import org.netlib.blas.DNRM2;
import org.netlib.lapack.DGELS;
import org.netlib.util.intW;

import com.ah.be.common.AhConvertBOToSQL;
import com.ah.be.common.DBOperationUtil;
import com.ah.be.common.cache.CacheMgmt;
import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.HmTimeStamp;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApWifi;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.GroupByParams;
import com.ah.bo.mgmt.LocationTracking;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.monitor.LocationHistory;
import com.ah.bo.monitor.LocationRssiReport;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Trex;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.bo.performance.AhAssociation;
import com.ah.bo.performance.AhClientSession;
import com.ah.bo.performance.AhLatestACSPNeighbor;
import com.ah.bo.performance.AhLatestRadioAttribute;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.performance.AhXIf;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.CountryCode;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Search;
import com.ah.util.Search.WallLoss;
import com.ah.util.Tracer;
import com.jama.Matrix;

public final class LocationTrackingImpl implements LocationTracking {

	private static final Tracer log = new Tracer(LocationTrackingImpl.class,
			"locationlog");

	private static final Tracer logt = new Tracer(LocationTrackingImpl.class,
			"tracerlog");

	private LocationTrackingImpl() {
	}

	private static LocationTracking instance;

	public synchronized static LocationTracking getInstance() {
		if (instance == null) {
			instance = new LocationTrackingImpl();
		}

		return instance;
	}

	private enum CciType {
		CCI_CONTAINED, CCI_NO_OVERLAP, CCI_TWO_OVERLAP, CCI_THREE_OVERLAP
	}

	private static String fontPath = System.getenv("HM_ROOT")
			+ "/resources/fonts/";

	private static int plfStep = 2;

	private static int attenuationStep = 1;

	private static int noiseFloor = 95;

	private static double elevationMargin = 1;

	private static int lastRssiMaxAge = 25; // minutes

	private static int rssiWindow = Integer.parseInt(System.getProperty(
			"hm.rssiWindow", "900")); // seconds

	public static int hmRssiFrom = Integer.parseInt(System.getProperty(
			"hm.rssiFrom", "-85"));

	public static int hmRssiUntil = Integer.parseInt(System.getProperty(
			"hm.rssiUntil", "-5"));

	private static int attenuationRange = Integer.parseInt(System.getProperty(
			"hm.attenuationRange", "15"));

	private static double svdPrecision = Double.parseDouble(System.getProperty(
			"hm.svd.precision", "1e-3"));

	private static int maxSrlsIters = 8;

	private static int gnIterations = Integer.parseInt(System.getProperty(
			"hm.svd.iterations", "20"));

	private static double startingTxPower = Double.parseDouble(System
			.getProperty("hm.svd.startingTxPower", "14"));

	private static double gammaTxPower = Double.parseDouble(System.getProperty(
			"hm.svd.gammaTxPower", "0.15"));

	private static double perimeterMargin = 2; // meters

	private static double mle = Double.parseDouble(System.getProperty(
			"hm.svd.mle", "3.2"));

	private static int maxAutoPlace = Integer.parseInt(System.getProperty(
			"hm.auto.max", "100"));

	private static double mfloorle = 33;

	private static boolean lstrVerbose = false;

	private static boolean lsgnVerbose = false;

	private static Color scaleColor(int red, int green, int blue) {
		double scale = 0.6;
		return new Color((int) Math.round(red * scale), (int) Math.round(green
				* scale), (int) Math.round(blue * scale));
	}

	private static Color[] scaledStartColors = new Color[] {
			scaleColor(255, 0, 0), scaleColor(0, 255, 0),
			scaleColor(0, 0, 255), scaleColor(255, 0, 255),
			scaleColor(255, 255, 0), scaleColor(0, 255, 255),
			scaleColor(255, 128, 0), scaleColor(255, 0, 128),
			scaleColor(0, 255, 128), scaleColor(128, 255, 0),
			scaleColor(128, 0, 255), scaleColor(0, 128, 255) };

	/*
	 * private Color[] startChannelColors = new Color[] { new Color(255, 0, 0),
	 * new Color(0, 255, 0), new Color(0, 0, 255), new Color(255, 0, 255), new
	 * Color(255, 255, 0), new Color(0, 255, 255), new Color(255, 128, 0), new
	 * Color(255, 0, 128), new Color(0, 255, 128), new Color(128, 255, 0), new
	 * Color(128, 0, 255), new Color(0, 128, 255) };
	 * 
	 * private Color[] endChannelColors = new Color[] { new Color(255, 200,
	 * 200), new Color(200, 255, 200), new Color(200, 200, 255), new Color(255,
	 * 200, 255), new Color(255, 255, 200), new Color(200, 255, 255), new
	 * Color(255, 255, 200), new Color(255, 200, 255), new Color(200, 255, 255),
	 * new Color(255, 255, 200), new Color(255, 200, 255), new Color(200, 255,
	 * 255) };
	 */

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

	private Color[] startSeverityColors = new Color[] { new Color(51, 255, 51),
			new Color(255, 255, 51), new Color(255, 153, 51),
			new Color(238, 52, 36) };

	private Color[] endSeverityColors = new Color[] { new Color(200, 255, 200),
			new Color(255, 255, 150), new Color(255, 200, 140),
			new Color(255, 200, 200) };

	private Color[] endColors_dark = new Color[] { new Color(35, 0, 0),
			new Color(0, 35, 0), new Color(0, 0, 35), new Color(35, 0, 35),
			new Color(35, 35, 0), new Color(0, 35, 35), new Color(35, 18, 0),
			new Color(35, 0, 18), new Color(0, 35, 18), new Color(18, 35, 0),
			new Color(18, 0, 35), new Color(0, 18, 35) };

	private Color[] endColors_white = new Color[] { new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 255, 255),
			new Color(255, 255, 255), new Color(255, 255, 255),
			new Color(255, 255, 255) };

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
					log.debug(
							"initChannelColors",
							"Color[" + i + "] = (" + c.getRed() + ", "
									+ c.getGreen() + ", " + c.getBlue() + ")");
				}
			}
		}
		return channelColors;
	}

	private static Color[] rssiColors = new Color[] { new Color(128, 0, 0),
			new Color(149, 21, 0), new Color(170, 43, 0),
			new Color(191, 64, 0), new Color(212, 85, 0),
			new Color(234, 107, 0), new Color(255, 128, 0),
			new Color(255, 140, 0), new Color(255, 151, 0),
			new Color(255, 163, 0), new Color(255, 174, 0),
			new Color(255, 186, 0), new Color(255, 197, 0),
			new Color(255, 209, 0), new Color(255, 220, 0),
			new Color(255, 232, 0), new Color(255, 243, 0),
			new Color(255, 255, 0), new Color(212, 255, 11),
			new Color(170, 255, 21), new Color(128, 255, 32),
			new Color(85, 255, 43), new Color(43, 255, 53),
			new Color(0, 255, 64), new Color(0, 255, 88),
			new Color(0, 255, 112), new Color(0, 255, 136),
			new Color(0, 255, 159), new Color(0, 255, 183),
			new Color(0, 255, 207), new Color(0, 255, 231),
			new Color(0, 255, 255), new Color(0, 241, 254),
			new Color(1, 228, 252), new Color(1, 214, 251),
			new Color(1, 200, 249), new Color(2, 187, 248),
			new Color(2, 173, 246), new Color(2, 159, 245),
			new Color(2, 146, 244), new Color(3, 132, 242),
			new Color(3, 118, 241), new Color(6, 109, 227),
			new Color(9, 99, 212), new Color(12, 90, 198),
			new Color(15, 80, 184), new Color(18, 70, 170),
			new Color(21, 61, 156), new Color(24, 51, 141),
			new Color(27, 42, 127), new Color(30, 32, 113),
			new Color(33, 22, 98), new Color(36, 12, 84), new Color(39, 3, 70),
			new Color(42, 0, 56), new Color(45, 0, 42), /*
														 * Channel boundary
														 * color
														 */
			new Color(21, 61, 156) };

	private static boolean fetchNeighborRssi(Map<String, Integer> apMacs,
			double[] apX, double[] apY, double[] apPower, short[] apChannel,
			double[] apBackhaulPower, short[] apBackhaulChannel,
			int[][] neighborRSSI, Collection<?> nodes, boolean useA,
			int rssiFrom, int rssiUntil, boolean verbose) {
		MapLeafNode[] aps = new MapLeafNode[apX.length];
		int i = 0;
		for (MapNode node : (Collection<MapNode>) nodes) {
			if (!node.isLeafNode()) {
				continue;
			}
			MapLeafNode leafNode = (MapLeafNode) node;
			apMacs.put(leafNode.getApId(), i);
			apX[i] = leafNode.getXm();
			apY[i] = leafNode.getYm();
			if (apPower != null && leafNode.getHiveAp() != null) {
				// Place holder nodes for mesh links have no HiveAP link
				// apPower[i] = getERP(leafNode, useA);
				apPower[i] = useA ? leafNode.getRadioEirpA() : leafNode
						.getRadioEirpBG();
				apChannel[i] = getChannel(leafNode, useA);
			}
			aps[i] = leafNode;
			i++;
		}
		if (apMacs.keySet().size() == 0) {
			return false;
		}

		boolean hasNeighbors = fetchAcspNeighbors(apMacs, aps, apBackhaulPower,
				apBackhaulChannel, neighborRSSI, rssiFrom, rssiUntil, useA);

		if (verbose) {
			logVectors(aps, apX, apY, apPower, apChannel, neighborRSSI);
			StringBuffer str = new StringBuffer("neighborPower = [");
			for (i = 0; i < aps.length; i++) {
				str.append(apBackhaulPower[i] + "; ");
			}
			log.info_non(str.toString() + "];");
			str = new StringBuffer("neighborChannel = [");
			for (i = 0; i < aps.length; i++) {
				str.append(apBackhaulChannel[i] + "; ");
			}
			log.info_non(str.toString() + "];");
		}
		return hasNeighbors;
	}

	private static void logVectors(MapLeafNode[] aps, double[] apX,
			double[] apY, double[] apPower, short[] apChannel,
			int[][] neighborRSSI) {
		int i;
		StringBuffer str = new StringBuffer("ap = [");
		for (i = 0; i < aps.length; i++) {
			str.append("'" + aps[i].getApId() + "'; ");
		}
		log.info_non(str.toString() + "];");
		str = new StringBuffer("apX = [");
		for (i = 0; i < aps.length; i++) {
			str.append(apX[i] + "; ");
		}
		log.info_non(str.toString() + "];");
		str = new StringBuffer("apY = [");
		for (i = 0; i < aps.length; i++) {
			str.append(apY[i] + "; ");
		}
		log.info_non(str.toString() + "];");
		if (apPower != null) {
			str = new StringBuffer("apPower = [");
			for (i = 0; i < aps.length; i++) {
				str.append(apPower[i] + "; ");
			}
			log.info_non(str.toString() + "];");
			str = new StringBuffer("apChannel = [");
			for (i = 0; i < aps.length; i++) {
				str.append(apChannel[i] + "; ");
			}
			log.info_non(str.toString() + "];");
		}
		log.info_non("neighborRSSI = [ ...");
		for (i = 0; i < aps.length; i++) {
			str = new StringBuffer();
			for (int j = 0; j < neighborRSSI[i].length; j++) {
				str.append(String.format("%4d", neighborRSSI[i][j]));
			}
			if (i + 1 == aps.length) {
				str.append("]");
			}
			log.info_non(str.toString() + ";");
		}
	}

	private static boolean fetchAcspNeighbors(Map<String, Integer> apMacs,
			MapLeafNode[] aps, double[] apBackhaulPower,
			short[] apBackhaulChannel, int[][] neighborRSSI, int rssiFrom,
			int rssiUntil, boolean useA) {
		boolean hasNeighbors = false;
		List neighbors = QueryUtil.executeQuery(
				"select apMac, neighborMac, rssi, txPower, channelNumber from "
						+ AhLatestACSPNeighbor.class.getSimpleName(),
				new SortParams("lastSeen", false), new FilterParams(
						"apMac in (:s1)", new Object[] { apMacs.keySet() }));
		for (Object[] obj : (List<Object[]>) neighbors) {
			String apMac = (String) obj[0];
			int apIndex = (Integer) apMacs.get(apMac);
			String neighborMac = (String) obj[1];
			int rssi = (Byte) obj[2] - noiseFloor;
			int txPower = (Byte) obj[3];
			int channelNumber = (Integer) obj[4];
			Integer neighborApIndex = (Integer) apMacs.get(neighborMac);
			if (neighborApIndex == null) {
				continue;
			}
			if (rssi >= rssiFrom
					&& rssi <= rssiUntil
					&& ((!useA && channelNumber < 15) || (useA && channelNumber > 15))) {
				hasNeighbors = true;
				MapLeafNode mapLeafNode = aps[neighborApIndex];
				float eirp = useA ? mapLeafNode.getRadioEirpA() : mapLeafNode
						.getRadioEirpBG();
				log.info_ln("% AP: " + apMac + " (" + apIndex + "), acsp nbr: "
						+ neighborMac + " (" + neighborApIndex + "), rssi: "
						+ rssi + ", nbr txPower: " + txPower + ", nbr EIRP: "
						+ eirp + ", channel: " + channelNumber);
				apBackhaulPower[neighborApIndex] = eirp;
				// apBackhaulPower[neighborApIndex] = txPower +
				// getAverageGain(mapLeafNode.getHiveAp().getHiveApModel(),
				// useA);
				apBackhaulChannel[neighborApIndex] = (short) channelNumber;
				neighborRSSI[apIndex][neighborApIndex] = rssi;
			} else {
				log.debug("% measurement " + rssi + " dBm filtered.");
			}
		}
		return hasNeighbors;
	}

	/*
	 * Using a grid of rectangles where each rectangle has its own loss exponent
	 */
	private void estimateRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, int rssiFrom, int rssiUntil)
			throws Exception {
		double mapToMetric = mapContainerNode.getMapToMetric();
		int apCount = setMetricNodePositions(nodes, mapToMetric);
		fetchRadioAttributes(nodes);

		double[] apX = new double[apCount];
		double[] apY = new double[apCount];
		double[] apPower = new double[apCount];
		short[] apChannel = new short[apCount];
		double[] apBackhaulPower = new double[apCount];
		short[] apBackhaulChannel = new short[apCount];
		int[][] neighborRSSI = new int[apCount][apCount];
		fetchNeighborRssi(new HashMap(), apX, apY, apPower, apChannel,
				apBackhaulPower, apBackhaulChannel, neighborRSSI, nodes, useA,
				rssiFrom, rssiUntil, true);
		double apElevation = mapContainerNode.getApElevationMetric();

		double squareSize = 1;
		double mapWidthMetric = mapContainerNode.getActualWidthMetric();
		double mapHeightMetric = mapContainerNode.getActualHeightMetric();
		logt.info("estimateRssi", "Neighbors mapX = " + mapWidthMetric);
		logt.info("estimateRssi", "Neighbors mapY = " + mapHeightMetric);

		while (mapWidthMetric / squareSize * mapHeightMetric / squareSize > 1000) {
			squareSize *= 2;
		}
		logt.info("estimateRssi", "square size: " + squareSize);

		double defaultLE = 30;
		double[] gridX = Search.sgrd(mapWidthMetric, squareSize, apX);
		double[] gridY = Search.sgrd(mapHeightMetric, squareSize, apY);
		double[][] lossExponents = Search.clbrle(apX, apY, apBackhaulPower,
				apBackhaulChannel, gridX, gridY, neighborRSSI, defaultLE);
		if (lossExponents == null) {
			return;
		}
		logt.info("heatmap", "canvas (" + canvasWidth + ", " + canvasHeight
				+ ")");
		double imageWidth = canvasWidth;
		double imageHeight = canvasHeight;

		double grids = lossExponents.length * lossExponents[0].length;
		double complexity = imageWidth * imageHeight * apX.length
				* Math.sqrt(grids);
		double cost = complexity / 3500.0;
		logt.info("estimateRssi", "Complexity: " + complexity + ", cost: "
				+ cost);
		while (cost > 15000) {
			imageWidth /= 2;
			imageHeight /= 2;
			complexity /= 4;
			cost = complexity / 3500.0;
			logt.info("estimateRssi", "Complexity: " + complexity + ", cost: "
					+ cost);
		}
		imageWidth = (int) imageWidth;
		imageHeight = (int) imageHeight;
		logt.info("estimateRssi", "image: (" + imageWidth + ", " + imageHeight
				+ ")");

		short mapColors[][] = new short[(int) imageWidth][(int) imageHeight];
		short mapChannels[][] = new short[(int) imageWidth][(int) imageHeight];

		Date start = new Date();
		Search.hmle(apX, apY, apPower, apChannel, apElevation, gridX, gridY,
				lossExponents, useA, mapWidthMetric, mapHeightMetric,
				squareSize, imageWidth, imageHeight, mapColors, mapChannels,
				shadesPerColor);
		Date end = new Date();
		logt.info("estimateRssi", "RSSI model evaluation: "
				+ (end.getTime() - start.getTime()) + " ms.");

		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);
	}

	private static boolean matchCachedRssi(MrHeatMap mrHeatMap, double[] apX,
			double[] apY, double[] apPower, short[] apChannel,
			double apElevation, double[] apBackhaulPower,
			short[] apBackhaulChannel, int[][] neighborRSSI, boolean useA,
			double mapWidthMetric, double mapHeightMetric, int canvasWidth,
			int canvasHeight, int heatmapResolution, double xray) {
		if (mrHeatMap == null) {
			logt.info("matchCachedRssi", "No match other heat map is null");
			return false;
		}
		if (mrHeatMap.xray != xray) {
			logt.info("matchCachedRssi", "xray mismatch");
			return false;
		}
		if (mrHeatMap.mapWidthMetric != mapWidthMetric) {
			logt.info("matchCachedRssi", "Map Width metric mismatch: "
					+ mapWidthMetric + ", " + mrHeatMap.mapWidthMetric);
			return false;
		}
		if (mrHeatMap.mapHeightMetric != mapHeightMetric) {
			logt.info("matchCachedRssi", "Map Height metric mismatch: "
					+ mapHeightMetric + ", " + mrHeatMap.mapHeightMetric);
			return false;
		}
		if (canvasWidth > 0 && canvasHeight > 0) {
			if (mrHeatMap.mapColors == null || mrHeatMap.mapChannels == null) {
				return false;
			}
			if (canvasWidth != mrHeatMap.canvasWidth
					|| canvasHeight != mrHeatMap.canvasHeight) {
				logt.info("matchCachedRssi",
						"Canvas must have been resized, new size ("
								+ canvasWidth + ", " + canvasHeight
								+ ") vs. cached (" + mrHeatMap.canvasWidth
								+ ", " + mrHeatMap.canvasHeight + ")");
				return false;
			}
		}
		if (heatmapResolution > 0) {
			if (heatmapResolution == MapSettings.HEATMAP_RESOLUTION_AUTO
					&& mrHeatMap.heatmapResolution != MapSettings.HEATMAP_RESOLUTION_HIGH) {
				log.info("matchCachedRssi",
						"Resolution is auto, but cached image is low or medium resolution.");
				return false;
			}
			if (heatmapResolution > mrHeatMap.heatmapResolution) {
				logt.info("matchCachedRssi",
						"Resolution of cached image is lower than required.");
				return false;
			}
		}
		if (!match(apX, mrHeatMap.apX)) {
			logt.info("matchCachedRssi", "AP X mismatch.");
			return false;
		}
		if (!match(apY, mrHeatMap.apY)) {
			logt.info("matchCachedRssi", "AP X mismatch.");
			return false;
		}
		if (!match(apPower, mrHeatMap.apPower)) {
			logt.info("matchCachedRssi", "AP Power mismatch.");
			return false;
		}
		if (!match(apChannel, mrHeatMap.apChannel)) {
			logt.info("matchCachedRssi", "AP Channel mismatch.");
			return false;
		}
		if (mrHeatMap.apElevation != apElevation) {
			logt.info("matchCachedRssi", "AP elevation mismatch: "
					+ apElevation + ", " + mrHeatMap.apElevation);
			return false;
		}
		if (!match(apBackhaulPower, mrHeatMap.apBackhaulPower)) {
			logt.info("matchCachedRssi", "Backhaul Power mismatch.");
			return false;
		}
		if (!match(apBackhaulChannel, mrHeatMap.apBackhaulChannel)) {
			logt.info("matchCachedRssi", "Backhaul Channel mismatch.");
			return false;
		}
		if (neighborRSSI.length != mrHeatMap.neighborRSSI.length) {
			logt.info("matchCachedRssi", "Neighbor rssi length mismatch.");
			return false;
		}
		for (int i = 0; i < neighborRSSI.length; i++) {
			if (!match(neighborRSSI[i], mrHeatMap.neighborRSSI[i])) {
				logt.info("matchCachedRssi", "Neighbor rssi mismatch.");
				return false;
			}
		}
		if (mrHeatMap.useA != useA) {
			logt.info("matchCachedRssi", "Frequency band mismatch.");
			return false;
		}
		return true;
	}

	private static boolean match(double[] a1, double[] a2) {
		if (a1.length != a2.length) {
			return false;
		}
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	private static boolean match(int[] a1, int[] a2) {
		if (a1.length != a2.length) {
			return false;
		}
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	private static boolean match(short[] a1, short[] a2) {
		if (a1.length != a2.length) {
			return false;
		}
		for (int i = 0; i < a1.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	private static WallLoss[] getWallsMetric(List<Wall> walls,
			double mapToMetric) {
		return getWallsMetric(walls, mapToMetric, walls.size());
	}

	private static WallLoss[] getWallsMetric(List<Wall> walls,
			double mapToMetric, int size) {
		WallLoss[] wallsMetric = null;
		if (size > 0) {
			wallsMetric = new Search.WallLoss[size];
		}
		if (walls.size() == 0) {
			return wallsMetric;
		}
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

	private int getClientCoverage(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			String clientMac, double tx, double ty, boolean useA,
			short shadesPerColor, MapSettings mapSettings, long latchId,
			boolean verbose) throws Exception {
		double mapWidthMetric = mapContainerNode.getActualWidthMetric();
		double mapHeightMetric = mapContainerNode.getActualHeightMetric();
		if (verbose) {
			log.info_ln("% START client coverage at: " + new Date());
			log.info_ln("mapX = " + mapWidthMetric + "; mapY = "
					+ mapHeightMetric + ";");
		}
		double mapToMetric = mapContainerNode.getMapToMetric();
		int apCount = setMetricNodePositions(nodes, mapToMetric);
		fetchRadioAttributes(nodes);

		double[] apX = new double[apCount];
		double[] apY = new double[apCount];
		double[] apPower = new double[apCount];
		short[] apChannel = new short[apCount];
		double[] apBackhaulPower = new double[apCount];
		short[] apBackhaulChannel = new short[apCount];
		int[][] neighborRSSI = new int[apCount][apCount];
		Map<String, Integer> apIndexes = new HashMap<String, Integer>();
		boolean hasNbrs = fetchNeighborRssi(apIndexes, apX, apY, apPower,
				apChannel, apBackhaulPower, apBackhaulChannel, neighborRSSI,
				nodes, useA, mapSettings.getRssiFrom(),
				mapSettings.getRssiUntil(), verbose);
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		log.info_ln("% # AP macs: " + nodesMap.keySet().size());
		if (nodesMap.keySet().size() == 0) {
			return -1;
		}
		List<LocationRssiReport> reports = getClientReports(mapContainerNode,
				nodesMap, clientMac, mapSettings);
		Search.ClientDetected[] clients = getClientDetected(reports, nodesMap,
				mapSettings);
		if (clients == null) {
			return -1;
		}

		double erp = getClientErp(mapContainerNode, nodesMap, nodes,
				mapSettings, clients);

		WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapToMetric);

		int n = clients.length;
		double[] RX = new double[n];
		double[] RY = new double[n];
		double[] TX = new double[n];
		double[] TY = new double[n];
		double[] TH = new double[n];
		double[] power = new double[n];
		int[] frequency = new int[n];
		int[] RSSI = new int[n];
		for (int i = 0, j = 0; j < n; j++) {
			Search.ClientDetected client = clients[j];
			RX[i] = client.getXm();
			RY[i] = client.getYm();
			TX[i] = tx * mapToMetric;
			TY[i] = ty * mapToMetric;
			log.info_ln("% AP at (" + RX[i] + ", " + RY[i] + "), client at: ("
					+ TX[i] + ", " + TY[i] + "), rssi: " + client.getRssi());
			TH[i] = mapContainerNode.getApElevationMetric() - elevationMargin;
			if (TH[i] < 0) {
				TH[i] = 0;
			}
			power[i] = erp;
			frequency[i] = (int) Search.getFrequency(client.getChannel());
			RSSI[i] = client.getRssi();
			i++;
		}
		if (verbose) {
			logSurvey(RX, RY, TX, TY, TH, power, frequency, RSSI);
		}
		double leb = Search.lsle(RX, RY, TX, TY, TH, power, frequency, RSSI);
		if (verbose) {
			log.info_ln("% leb = " + leb);
		}
		double squareSize = 3;
		while (mapWidthMetric / squareSize * mapHeightMetric / squareSize > 500) {
			squareSize *= 2;
		}
		double[] gridX = Search.cgrd(mapWidthMetric, squareSize);
		double[] gridY = Search.cgrd(mapHeightMetric, squareSize);

		double gamma = 0.02;
		double dle = 0;
		double[][] B = Search.clbrb_w(RX, RY, TX, TY, TH, power, frequency,
				RSSI, gridX, gridY, gamma, leb, walls, false);

		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}

		double lsle = 0;
		double xray = 0;
		int heatmapResolution = mapSettings.getHeatmapResolution();
		apX = new double[1];
		apY = new double[1];
		apPower = new double[1];
		apChannel = new short[1];
		apX[0] = tx * mapToMetric;
		apY[0] = ty * mapToMetric;
		apPower[0] = erp;
		apChannel[0] = 1;

		MrHeatMap mrHeatMap = new MrHeatMap(apX, apY, null, apPower, apChannel,
				apElevation, null, null, null, gridX, gridY, B, lsle, dle,
				useA, mapWidthMetric, mapHeightMetric, canvasWidth,
				canvasHeight, heatmapResolution, xray, latchId);

		estimateRssi(mapContainerNode, mrHeatMap, heatmapResolution);
		return heatmapResolution;
	}

	private double getWallsXray(MapContainerNode mapContainerNode) {
		double xray = 0;
		for (Wall wall : mapContainerNode.getWalls()) {
			xray += wall.getX1();
			xray += wall.getY1();
			xray -= wall.getX2();
			xray -= wall.getY2();
			xray += wall.getType();
		}
		return xray;
	}

	private MrHeatMap getHeatMap(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			boolean useA, int heatmapResolution, long latchId,
			boolean calibrateHeatmap, int rssiFrom, int rssiUntil) {
		return getHeatMap(mapContainerNode, nodes, canvasWidth, canvasHeight,
				useA, heatmapResolution, latchId, calibrateHeatmap, rssiFrom,
				rssiUntil, true, true);
	}

	private MrHeatMap getHeatMap(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			boolean useA, int heatmapResolution, long latchId,
			boolean calibrateHeatmap, int rssiFrom, int rssiUntil,
			boolean match, boolean verbose) {
		double mapWidthMetric = mapContainerNode.getActualWidthMetric();
		double mapHeightMetric = mapContainerNode.getActualHeightMetric();
		if (verbose) {
			log.info_ln("% START mrhm for " + (useA ? "a" : "g") + " at: "
					+ new Date());
			log.info_ln("mapX = " + mapWidthMetric + "; mapY = "
					+ mapHeightMetric + ";");
		}
		double mapToMetric = mapContainerNode.getMapToMetric();
		int apCount = setMetricNodePositions(nodes, mapToMetric);
		fetchRadioAttributes(nodes);

		double[] apX = new double[apCount];
		double[] apY = new double[apCount];
		double[] apPower = new double[apCount];
		short[] apChannel = new short[apCount];
		double[] apBackhaulPower = new double[apCount];
		short[] apBackhaulChannel = new short[apCount];
		int[][] neighborRSSI = new int[apCount][apCount];
		Map<String, Integer> apIndexes = new HashMap<String, Integer>();
		boolean hasNbrs = fetchNeighborRssi(apIndexes, apX, apY, apPower,
				apChannel, apBackhaulPower, apBackhaulChannel, neighborRSSI,
				nodes, useA, rssiFrom, rssiUntil, verbose);
		// Adjust for use in hmb, not clbrb
		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}
		if (verbose) {
			log.info_ln("apElevation = " + apElevation + ";");
		}
		List<Trex> clients = (List<Trex>) QueryUtil.executeQuery(Trex.class,
				new SortParams("id"), new FilterParams(
						"parentMap = :s1 AND rssi >= :s2 AND rssi <= :s3 AND "
								+ (useA ? "frequency > 4000"
										: "frequency < 3000"), new Object[] {
								mapContainerNode, rssiFrom, rssiUntil }),
				mapContainerNode.getOwner().getId());
		if (verbose) {
			log.info_ln("% # TR sample measurements: " + clients.size());
		}
		double xray = 0;
		for (Trex client : clients) {
			xray += client.rx + client.ry - client.tx - client.ty
					+ client.elevation + client.erp + client.rssi;
		}
		xray += getWallsXray(mapContainerNode);

		MrHeatMap mrHeatMap;
		if (match) {
			mrHeatMap = (MrHeatMap) MgrUtil
					.getSessionAttribute(SessionKeys.MR_HEAT_MAP);
			boolean cached = matchCachedRssi(mrHeatMap, apX, apY, apPower,
					apChannel, apElevation, apBackhaulPower, apBackhaulChannel,
					neighborRSSI, useA, mapWidthMetric, mapHeightMetric,
					canvasWidth, canvasHeight, heatmapResolution, xray);
			if (verbose) {
				log.info_ln("% Matches with cached RSSI: " + cached);
			}
			if (cached) {
				if (verbose) {
					log.info_ln("% END cached mrhm at: " + new Date());
				}
				return mrHeatMap;
			}
		}

		double lsle = mle;
		double dle = 0;
		if (hasNbrs) {
			dle = Search.lsle(apX, apY, apBackhaulPower, apBackhaulChannel,
					neighborRSSI);
			lsle = lsle(dle);
			if (verbose) {
				log.info_ln("le = " + dle + ";");
				log.info_ln("le = " + lsle + ";");
			}
			dle = lsle - dle;
		} else if (verbose) {
			log.info_ln("le = " + lsle + ";");
		}

		double[][] B = null;
		double[] gridX = null, gridY = null;
		if (calibrateHeatmap) {
			double squareSize = 3;
			while (mapWidthMetric / squareSize * mapHeightMetric / squareSize > 500) {
				squareSize *= 2;
			}
			gridX = Search.cgrd(mapWidthMetric, squareSize);
			gridY = Search.cgrd(mapHeightMetric, squareSize);
			double gamma = 0.02;
			if (verbose) {
				log.info_ln("squareSize = " + squareSize + "; % total "
						+ (gridX.length - 3) * (gridY.length - 3));
				log.info_ln("gridX = createGrid(mapX, squareSize);");
				log.info_ln("gridY = createGrid(mapY, squareSize);");
				log.info_ln("% Canvas (" + canvasWidth + ", " + canvasHeight
						+ ")");
				log.info_ln("gamma = " + gamma + ";");
			}

			WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
					mapToMetric);

			int n = clients.size();
			if (n > 0) {
				double[] RX = new double[n];
				double[] RY = new double[n];
				double[] TX = new double[n];
				double[] TY = new double[n];
				double[] TH = new double[n];
				double[] power = new double[n];
				int[] frequency = new int[n];
				int[] RSSI = new int[n];
				for (int i = 0; i < n; i++) {
					Trex client = clients.get(i);
					RX[i] = client.rx;
					RY[i] = client.ry;
					TX[i] = client.tx;
					TY[i] = client.ty;
					TH[i] = client.elevation - elevationMargin;
					if (TH[i] < 0) {
						TH[i] = 0;
					}
					power[i] = client.erp;
					frequency[i] = client.frequency;
					// frequency[i] = (int) Search.getFrequency((short) 1);
					RSSI[i] = client.rssi;
				}
				if (verbose) {
					logSurvey(RX, RY, TX, TY, TH, power, frequency, RSSI);
				}
				double leb = Search.lsle(RX, RY, TX, TY, TH, power, frequency,
						RSSI);
				if (verbose) {
					log.info_ln("% leb = " + leb);
				}
				dle = 0; // Don't adjust
				B = Search.clbrb_w(RX, RY, TX, TY, TH, power, frequency, RSSI,
						gridX, gridY, gamma, leb, walls, false);
			} else if (hasNbrs) {
				B = Search.clbrb_w(apX, apY, apBackhaulPower,
						apBackhaulChannel, gridX, gridY, neighborRSSI, gamma,
						walls, false);
				dle = 0; // Don't adjust
				if (verbose) {
					log.info_ln("B = calibrate(apX, apY, neighborPower, neighborChannel, gridX, gridY, neighborRSSI, 0.1, gamma);");
				}
			}
			if (B == null) {
				log.info_ln("% Failed to find a fit.");
				return null;
			}
		}

		if (verbose) {
			log.info_ln("dle = " + dle + ";");
		}
		mrHeatMap = new MrHeatMap(apX, apY, apIndexes, apPower, apChannel,
				apElevation, apBackhaulPower, apBackhaulChannel, neighborRSSI,
				gridX, gridY, B, lsle, dle, useA, mapWidthMetric,
				mapHeightMetric, canvasWidth, canvasHeight, heatmapResolution,
				xray, latchId);

		if (verbose) {
			log.info_ln("% END mrhm at: " + new Date());
		}
		return mrHeatMap;
	}

	private int estimateRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, int heatmapResolution,
			long latchId, int rssiFrom, int rssiUntil) throws Exception {
		MrHeatMap mrHeatMap = getHeatMap(mapContainerNode, nodes, canvasWidth,
				canvasHeight, useA, heatmapResolution, latchId, true, rssiFrom,
				rssiUntil);
		return estimateHmb(mapContainerNode, mrHeatMap, heatmapResolution);
	}

	private int estimateHmb(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap, int heatmapResolution) {
		log.info_ln("% START hmb at: " + new Date());
		if (mrHeatMap == null) {
			heatmapResolution = -1;
		} else if (mrHeatMap.mapColors != null) {
			// Cached
			mapContainerNode.setMapColors(mrHeatMap.mapColors);
			mapContainerNode.setMapChannels(mrHeatMap.mapChannels);
			mapContainerNode.setApChannel(mrHeatMap.apChannel);
			mapContainerNode.setApIndexes(mrHeatMap.apIndexes);
			// To make sure there will be no update request.
			heatmapResolution = MapSettings.HEATMAP_RESOLUTION_HIGH;
		} else {
			estimateRssi(mapContainerNode, mrHeatMap, heatmapResolution);
			if (heatmapResolution != MapSettings.HEATMAP_RESOLUTION_AUTO) {
				// Only cache final image
				mrHeatMap.mapColors = mapContainerNode.getMapColors();
				mrHeatMap.mapChannels = mapContainerNode.getMapChannels();
			}
			MgrUtil.setSessionAttribute(SessionKeys.MR_HEAT_MAP, mrHeatMap);
		}
		log.info_ln("% END hmb (" + heatmapResolution + ") at: " + new Date());
		return heatmapResolution;
	}

	public static void estimateRssi(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap, int heatmapResolution) {
		double imageWidth = mrHeatMap.canvasWidth;
		double imageHeight = mrHeatMap.canvasHeight;

		double grids = mrHeatMap.gridX.length * mrHeatMap.gridY.length;
		double complexity = imageWidth * imageHeight * mrHeatMap.apX.length
				* Math.sqrt(grids);
		double costFactor = 1300;
		double cost = complexity / costFactor;
		log.info_ln("% Complexity: " + complexity + ", cost: " + cost);

		int costLimit = 2000;
		if (heatmapResolution == MapSettings.HEATMAP_RESOLUTION_MEDIUM) {
			costLimit = 10000;
		} else if (heatmapResolution == MapSettings.HEATMAP_RESOLUTION_HIGH) {
			costLimit = 60000;
		}
		while (cost > costLimit) {
			imageWidth /= 2;
			imageHeight /= 2;
			complexity /= 4;
			cost = complexity / costFactor;
			log.info_ln("% Complexity: " + complexity + ", cost: " + cost);
		}
		imageWidth = (int) imageWidth;
		imageHeight = (int) imageHeight;
		log.info_ln("% image: (" + imageWidth + ", " + imageHeight + ")");

		Date start = new Date();
		Search.ler(mrHeatMap.gridX, mrHeatMap.gridY, mrHeatMap.B,
				mrHeatMap.mapWidthMetric, mrHeatMap.mapHeightMetric,
				imageWidth, imageHeight);
		Date end = new Date();
		log.info_ln("% loss range in: " + (end.getTime() - start.getTime())
				+ " ms.");

		short[][] mapColors = new short[(int) imageWidth][(int) imageHeight];
		short[][] mapChannels = new short[(int) imageWidth][(int) imageHeight];
		if (heatmapResolution != MapSettings.HEATMAP_RESOLUTION_AUTO) {
			log.info_ln("heatmap(apX, apY, apElevation, apPower, apChannel, gridX, gridY, B, mapX, mapY, resolution, false, false);");
		}
		Search.WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapContainerNode.getMapToMetric());
		start = new Date();
		log.info_ln("% apX length: " + mrHeatMap.apX.length + ", power: "
				+ mrHeatMap.apPower[0] + ", channel: " + mrHeatMap.apChannel[0]);
		Search.hmb(mrHeatMap.apX, mrHeatMap.apY, mrHeatMap.apPower,
				mrHeatMap.apChannel, mrHeatMap.apElevation, mrHeatMap.gridX,
				mrHeatMap.gridY, mrHeatMap.B, mrHeatMap.useA,
				mrHeatMap.mapWidthMetric, mrHeatMap.mapHeightMetric,
				imageWidth, imageHeight, mapColors, mapChannels, false, 0.5, 4,
				mrHeatMap.dle, walls);
		end = new Date();
		log.info_ln("% RSSI estimation: " + (end.getTime() - start.getTime())
				+ " ms.");

		clearBeyondNinety(mapContainerNode, mrHeatMap, mapColors, mapChannels);
	}

	public boolean estimateRssiHr(MapContainerNode mapContainerNode,
			long latchId) {
		MrHeatMap mrHeatMap = (MrHeatMap) MgrUtil
				.getSessionAttribute(SessionKeys.MR_HEAT_MAP);
		if (mrHeatMap == null || latchId != mrHeatMap.latchId) {
			logt.info("estimateRssi",
					"No previous heat map, or latchId doesn't match.");
			return false;
		}
		Date start = new Date();
		log.info_non("% START hmbr at: " + start);
		if (mrHeatMap.mapRssi == null) {
			estimateRssi(mapContainerNode, mrHeatMap,
					MapSettings.HEATMAP_RESOLUTION_HIGH);
		} else {
			log.info_non("HR refresh of an incremental heat map.");
			overlayAcspNbrCoverage(mapContainerNode, mrHeatMap, latchId);
		}
		Date end = new Date();
		log.info_non("% END hmbr at: " + end + " ("
				+ (end.getTime() - start.getTime()) + " ms.");
		mrHeatMap = (MrHeatMap) MgrUtil
				.getSessionAttribute(SessionKeys.MR_HEAT_MAP);
		if (mrHeatMap == null || latchId != mrHeatMap.latchId) {
			// Must have been a more recent request.
			logt.info("estimateRssi",
					"LatchId doesn't match, must have changed.");
			return false;
		}
		mrHeatMap.mapColors = mapContainerNode.getMapColors();
		mrHeatMap.mapChannels = mapContainerNode.getMapChannels();
		mrHeatMap.heatmapResolution = MapSettings.HEATMAP_RESOLUTION_HIGH;
		return true;
	}

	public int fetchActiveNbrs(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, boolean useA, long latchId, JSONObject jsonObj)
			throws Exception {
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		MrHeatMap mrHeatMap = (MrHeatMap) MgrUtil
				.getSessionAttribute(SessionKeys.MR_HEAT_MAP);
		if (mrHeatMap != null && mrHeatMap.mapRssi != null) {
			long delta = (latchId - mrHeatMap.latchId) / 1000;
			log.info_ln("LatchId " + latchId + ", incremental request came "
					+ delta + " sec later.");
			int active = 180;
			// Expires after active seconds
			if (mrHeatMap.useA == useA
					&& delta < active
					&& !mrHeatMap.overlayBusy
					&& (mrHeatMap.overlayFinished || mrHeatMap.remainingIds
							.size() > 0)) {
				// Overlay calculation should not be busy and either a high
				// resolution overlay should exist or there should be a few
				// remaining AP ids to calculate
				log.info_ln("LatchId " + latchId
						+ ", same band, reuse coverage data.");
				mrHeatMap.latchId = latchId; // Override latchId
				for (MapNode mapNode : nodes) {
					if (mapNode.isLeafNode()) {
						MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
						boolean remains = mrHeatMap.remainingIds
								.contains(mapLeafNode.getApId());
						if (remains) {
							log.info_ln("LatchId " + latchId + ", AP "
									+ mapLeafNode.getApId()
									+ " in remaining IDs.");
						}
						mapLeafNode.setSelected(remains);
					}
				}
				log.info_ln("LatchId " + latchId + ", updated remaining IDs: "
						+ mrHeatMap.remainingIds);
				jsonObj.put("missing", ""); // missing empty, seen is not used
				return mapSettings.getHeatmapResolution();
			}
		}

		int apCount = setMetricNodePositions(nodes,
				mapContainerNode.getMapToMetric());
		Map<String, MapLeafNode> leafNodes = fetchRadioAttributes(nodes);

		double[] apX = new double[apCount];
		double[] apY = new double[apCount];
		double[] apPower = new double[apCount];
		short[] apChannel = new short[apCount];
		double[] apBackhaulPower = new double[apCount];
		short[] apBackhaulChannel = new short[apCount];
		int[][] neighborRSSI = new int[apCount][apCount];
		Map<String, Integer> apIndexes = new HashMap<String, Integer>();
		boolean hasNbrs = fetchNeighborRssi(apIndexes, apX, apY, apPower,
				apChannel, apBackhaulPower, apBackhaulChannel, neighborRSSI,
				nodes, useA, mapSettings.getRssiFrom(),
				mapSettings.getRssiUntil(), true);
		MapLeafNode[] aps = new MapLeafNode[apX.length];
		for (String apMac : apIndexes.keySet()) {
			aps[apIndexes.get(apMac)] = leafNodes.get(apMac);
		}
		Set<String> remainingIds = new HashSet<String>();
		for (int j = 0; j < aps.length; j++) {
			StringBuffer target = new StringBuffer("Target AP: "
					+ aps[j].getApId() + " (pwr " + apPower[j] + ") [");
			boolean seen = false;
			for (int i = 0; i < aps.length; i++) {
				if (neighborRSSI[i][j] != 0) {
					seen = true;
				}
				target.append(neighborRSSI[i][j]);
				if (i < aps.length - 1) {
					target.append(", ");
				}
			}
			aps[j].setSelected(seen);
			if (seen) {
				remainingIds.add(aps[j].getApId());
			}
			log.info_non(target.append("] (" + seen + ")").toString());
		}
		mrHeatMap = new MrHeatMap(apX, apY, apIndexes, apPower, apChannel, 0,
				apBackhaulPower, apBackhaulChannel, neighborRSSI, null, null,
				null, 0, 0, useA, 0, 0, 0, 0, -1, 0, latchId);
		mrHeatMap.remainingIds = remainingIds;
		log.info_ln("LatchId " + latchId + ", remaining IDs: "
				+ mrHeatMap.remainingIds);
		MgrUtil.setSessionAttribute(SessionKeys.MR_HEAT_MAP, mrHeatMap);

		boolean seen = false;
		String missing = "";
		for (MapNode mapNode : nodes) {
			if (mapNode.isLeafNode()) {
				MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
				if (mapLeafNode.isSelected()) {
					seen = true;
				} else {
					if (missing.length() > 0) {
						missing += ", ";
					}
					missing += mapLeafNode.getApName();
				}
			}
		}
		jsonObj.put("seen", seen);
		jsonObj.put("missing", missing);
		if (missing.length() > 0) {
			jsonObj.put("range",
					" in the range between " + mapSettings.getRssiUntil()
							+ " and " + mapSettings.getRssiFrom()
							+ " dBm are available for ");
		}
		return mapSettings.getHeatmapResolution();
	}

	public int computeRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			String bssid, Long acspId, Long nextId, double tx, double ty,
			boolean useA, boolean useBG, short shadesPerColor, long latchId)
			throws Exception {
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		if (bssid != null) {
			return getClientCoverage(mapContainerNode, nodes, canvasWidth,
					canvasHeight, bssid, tx, ty, useA, shadesPerColor,
					mapSettings, latchId, true);
		}
		if (acspId != null) {
			log.info_ln("This is an ACSP nbr heat map.");
			return getAcspNbrCoverage(mapContainerNode, nodes, canvasWidth,
					canvasHeight, useA, shadesPerColor,
					mapSettings.getHeatmapResolution(), latchId, acspId, true);
		}
		logt.info("computeRssi",
				"Calibrate heat map: " + mapSettings.isCalibrateHeatmap());
		if (mapSettings.isCalibrateHeatmap()) {
			int heatmapResolution = mapSettings.getHeatmapResolution();
			logt.info("computeRssi", "Latch Id: " + latchId);
			if (latchId == 0) { // for PDF
				heatmapResolution = MapSettings.HEATMAP_RESOLUTION_HIGH;
			}
			if (nextId != null) {
				log.info_non("This is an incremental heat map.");
				return getNextAcspNbrCoverage(mapContainerNode, nodes,
						canvasWidth, canvasHeight, useA, shadesPerColor,
						mapSettings.getHeatmapResolution(), latchId, nextId,
						true);
			}
			return estimateRssi(mapContainerNode, nodes, canvasWidth,
					canvasHeight, useA, shadesPerColor, heatmapResolution,
					latchId, mapSettings.getRssiFrom(),
					mapSettings.getRssiUntil());
		}
		double imageWidth = canvasWidth;
		double imageHeight = canvasHeight;
		double complexity = imageWidth * imageHeight * nodes.size();

		double mapToMetric = mapContainerNode.getMapToMetric();
		double squareSize = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;
		int complexityLimit = Integer.parseInt(System
				.getProperty("heatMap.complexity"));
		logt.info("computeRssi", "Complexity: " + (int) complexity
				+ " out of: " + complexityLimit + ", meters: "
				+ mapContainerNode.getWidth() * mapToMetric + ", square size: "
				+ squareSize);

		Search.WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapToMetric);
		if (walls != null) {
			imageWidth /= 2;
			imageHeight /= 2;
			complexity /= 4;
			squareSize *= 2;
		}
		logt.info("computeRssi", "# units: " + (int) complexity
				+ ", square size: " + squareSize + " meters.");
		while (complexity > complexityLimit || squareSize < 0.01) {
			imageWidth /= 2;
			imageHeight /= 2;
			complexity /= 4;
			squareSize *= 2;
			logt.info("computeRssi", "# units: " + (int) complexity
					+ ", square size: " + squareSize + " meters.");
		}
		imageWidth = (int) imageWidth;
		imageHeight = (int) imageHeight;
		logt.info("computeRssi", "canvas: (" + canvasWidth + ", "
				+ canvasHeight + "), image: (" + imageWidth + ", "
				+ imageHeight + ")  useA: " + useA + ", useBG: " + useBG);

		double n = lsle(mapContainerNode, nodes, mapSettings.getRssiFrom(),
				mapSettings.getRssiUntil()) * 10;
		if (mapContainerNode.getEnvironment() != EnumConstUtil.MAP_ENV_AUTO) {
			n = getPathLossFactor(mapContainerNode.getEnvironment());
		} else if (n < 23) {
			n = 23;
		}
		logt.info("computeRssi", "Estimated n: " + n);

		double imageToMetric = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;
		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}
		log.info_ln("apElevation = " + apElevation + ";");
		double firstSegment = 1;
		double firstSegmentLoss = 15;
		List<MapNode> sortedNodes = new ArrayList<MapNode>(nodes);
		Map<String, Integer> apIndexes = new HashMap<String, Integer>();
		short[] apChannel = new short[nodes.size()];
		for (int i = 0; i < apChannel.length; i++) {
			MapNode mapNode = sortedNodes.get(i);
			if (mapNode.isLeafNode()) {
				apChannel[i] = getChannel((MapLeafNode) mapNode, useA);
				apIndexes.put(((MapLeafNode) mapNode).getApId(), i);
			} else {
				apChannel[i] = -1;
			}
		}
		Date start = new Date();
		short mapColors[][] = new short[(int) imageWidth][(int) imageHeight];
		short mapChannels[][] = new short[(int) imageWidth][(int) imageHeight];
		for (double x = 0; x < imageWidth; x += 1) {
			double x_m = (x + 0.5) * imageToMetric;
			for (double y = 0; y < imageHeight; y += 1) {
				double y_m = (y + 0.5) * imageToMetric;
				double rssi = -1000;
				short channel = -1;
				for (short i = 0; i < apChannel.length; i++) {
					MapNode mapNode = sortedNodes.get(i);
					if (!mapNode.isLeafNode()) {
						continue;
					} else if (null != ((MapLeafNode) mapNode).getHiveAp() 
					        && ((MapLeafNode) mapNode).getHiveAp().isSwitchProduct()) {
					    // disable power-channel for SR
					    continue;
					}
					double node_x_m = ((MapLeafNode) mapNode).getXm();
					double node_y_m = ((MapLeafNode) mapNode).getYm();
					double dx_m = node_x_m - x_m;
					double dy_m = node_y_m - y_m;
					double d = Math.sqrt(dx_m * dx_m + dy_m * dy_m
							+ apElevation * apElevation);
					double powerLevel = 0;
					double pathLoss = 0;
					if (useA && hasA((MapLeafNode) mapNode)) {
						powerLevel = getERP((MapLeafNode) mapNode, true);
						pathLoss = getPathLossA(d, n);
						double delta = getPathLossA(firstSegment,
								firstSegmentLoss)
								- getPathLossA(firstSegment, n);
						pathLoss += delta;
						if (useBG && hasBG((MapLeafNode) mapNode)) {
							double powerLevelBG = getERP((MapLeafNode) mapNode,
									false);
							double pathLossBG = pathLoss - (PL_C_A - PL_C_BG);
							if (powerLevelBG - pathLossBG > powerLevel
									- pathLoss) {
								powerLevel = powerLevelBG;
								pathLoss = pathLossBG;
							}
						}
					} else if (useBG && hasBG((MapLeafNode) mapNode)) {
						powerLevel = getERP((MapLeafNode) mapNode, false);
						pathLoss = getPathLossBG(d, n);
						double delta = getPathLossBG(firstSegment,
								firstSegmentLoss)
								- getPathLossBG(firstSegment, n);
						pathLoss += delta;
					}
					if (powerLevel != 0) {
						double wallLoss = 0;
						if (walls != null) {
							wallLoss = Search.wl(walls, x_m, y_m, node_x_m,
									node_y_m, false);
						}
						double newRssi = powerLevel - pathLoss - wallLoss;
						if (newRssi > rssi) {
							channel = i;
							rssi = newRssi;
						}
					}
				}
				short rssiColor = -1;
				if (rssi != -1000) {
					if (rssi > -35) {
						rssi = -35;
					}
					rssiColor = (short) (-35 - rssi);
					if (rssiColor >= shadesPerColor) {
						rssiColor = -1;
					}
				}
				mapColors[(int) x][(int) y] = rssiColor;
				mapChannels[(int) x][(int) y] = channel;
			}
		}
		Date end = new Date();
		logt.info("computeRssi",
				"RSSI computation: " + (end.getTime() - start.getTime())
						+ " ms.");
		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);
		mapContainerNode.setApChannel(apChannel);
		mapContainerNode.setApIndexes(apIndexes);
		return MapSettings.HEATMAP_RESOLUTION_HIGH;
	}

	private static double getAverageGain(short apModel, boolean useA) {
		switch (apModel) {
		case HiveAp.HIVEAP_MODEL_20:
			return 0 + 3.0 - 1;
		case HiveAp.HIVEAP_MODEL_350:
			if (useA) {
				return 4.77 + 4.0;
			} else {
				return 4.77 + 4.0;
			}
		case HiveAp.HIVEAP_MODEL_340:
			if (useA) {
				return 4.77 + 2.0;
			} else {
				return 4.77 + 3.0;
			}
		case HiveAp.HIVEAP_MODEL_330:
			if (useA) {
				return 4.77 + 4.7;
			} else {
				return 4.77 + 4.4;
			}
		case HiveAp.HIVEAP_MODEL_320:
			if (useA) {
				return 4.77 + 1.0;
			} else {
				return 4.77 + 1.0;
			}
		case HiveAp.HIVEAP_MODEL_110:
			if (useA) {
				return 3.0 + 3.0;
			} else {
				return 3.0 + 2.0;
			}
		case HiveAp.HIVEAP_MODEL_120:
			if (useA) {
				return 3.0 + 3.8;
			} else {
				return 3.0 + 3.2;
			}
		case HiveAp.HIVEAP_MODEL_121:
			if (useA) {
				return 3.0 + 5.2;
			} else {
				return 3.0 + 4.1;
			}
		case HiveAp.HIVEAP_MODEL_141:
			if (useA) {
				return 3.0 + 5.9;
			} else {
				return 3.0 + 4.6;
			}
		case HiveAp.HIVEAP_MODEL_BR100:
			if (useA) {
				return 0; // Not supported
			} else {
				return 0.0 + 3.2;
			}
		case HiveAp.HIVEAP_MODEL_BR200_WP:
		case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
			if (useA) {
				return 4.77 + 2.0;
			} else {
				return 4.77 + 1.0;
			}
		case HiveAp.HIVEAP_MODEL_170:
			if (useA) {
				return 3.0 + 8.0;
			} else {
				return 3.0 + 7.0;
			}
		case HiveAp.HIVEAP_MODEL_370:
			if (useA) {
				return 4.77 + 5.8;
			} else {
				return 4.77 + 4.8;
			}
		case HiveAp.HIVEAP_MODEL_390:
			if (useA) {
				return 4.77 + 5.9;
			} else {
				return 4.77 + 4.9;
            }
        case HiveAp.HIVEAP_MODEL_230:
            if (useA) {
                return 4.77 + 5.6;
            }
            else {
                return 4.77 + 4.6;
            }
		default:
			return 0;
		}
	}

	private static double getAverageShadow(short apModel, boolean useA) {
		switch (apModel) {
		case HiveAp.HIVEAP_MODEL_20:
			return 8.0;
		case HiveAp.HIVEAP_MODEL_390:
		case HiveAp.HIVEAP_MODEL_350:
		case HiveAp.HIVEAP_MODEL_340:
		case HiveAp.HIVEAP_MODEL_170:
			return 6.0;
		case HiveAp.HIVEAP_MODEL_370:
		case HiveAp.HIVEAP_MODEL_330:
		case HiveAp.HIVEAP_MODEL_320:
        case HiveAp.HIVEAP_MODEL_230:
			return 8.0;
		case HiveAp.HIVEAP_MODEL_120:
		case HiveAp.HIVEAP_MODEL_110:
		case HiveAp.HIVEAP_MODEL_BR100:
		case HiveAp.HIVEAP_MODEL_BR200_WP:
		case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
		case HiveAp.HIVEAP_MODEL_121:
		case HiveAp.HIVEAP_MODEL_141:
			return 8.0;
		default:
			return 0;
		}
	}

	public double predictedMapSize(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, boolean useA, double rssiThreshold)
			throws Exception {
		double power = useA ? planConfig.getWifi1Power() : planConfig
				.getWifi0Power();
		// horizontal, not down to floor level.
		return predictedMapSize(mapContainerNode, power,
				planConfig.getDefaultApType(), useA, rssiThreshold,
				channelWidthLoss(useA, planConfig.getChannelWidth()));
	}

	public double predictedMapSize(MapContainerNode mapContainerNode,
			double power, short apModel, boolean useA, double rssiThreshold)
			throws Exception {
		return predictedMapSize(mapContainerNode, power, apModel, useA,
				rssiThreshold, 0);
	}

	public static double predictedMapSize(MapContainerNode mapContainerNode,
			double power, short apModel, boolean useA, double rssiThreshold,
			double loss) {
		return predictedMapSize(power, apModel,
				getPathLossFactor(mapContainerNode), useA, rssiThreshold, loss);
	}

	public static double predictedMapSize(double power, short apModel,
			double n, boolean useA, double rssiThreshold, double loss) {
		double erp = power + getAverageGain(apModel, useA);
		double distance;
		if (useA) {
			distance = getDistanceA(erp - rssiThreshold - loss, n);
		} else {
			distance = getDistanceBG(erp - rssiThreshold - loss, n);
		}
		return distance;
	}

	public double predictedRssi(double n, double power, short apModel,
			boolean useA, double distance) throws Exception {
		double erp = power + getAverageGain(apModel, useA);
		double rssi;
		if (useA) {
			rssi = erp - getPathLossA(distance, n);
		} else {
			rssi = erp - getPathLossBG(distance, n);
		}
		logt.debug("predictedRssi", "AP: " + HiveAp.getModelEnumString(apModel)
				+ ", power: " + power + ", distance is: " + distance
				+ ", rssi at that distance: " + rssi);
		return rssi;
	}

	public boolean relevantWall(Point2D v1, Point2D v2, Point2D topLeft,
			Point2D topRight, Point2D bottomLeft, Point2D bottomRight,
			double leftEdge, double topEdge, double rightEdge, double bottomEdge) {
		if (v1.getY() < topEdge && v2.getY() < topEdge) {
			// Wall is above top edge.
			return false;
		}
		if (v1.getY() > bottomEdge && v2.getY() > bottomEdge) {
			// Wall is below bottom edge.
			return false;
		}
		if (v1.getX() < leftEdge && v2.getX() < leftEdge) {
			// Wall is left of left edge.
			return false;
		}
		if (v1.getX() > rightEdge && v2.getX() > rightEdge) {
			// Wall is right of right edge.
			return false;
		}
		if (v1.getY() == v2.getY()) {
			logt.debug("relevantWall",
					"Wall is parallel with top and bottom edge.");
		} else {
			Point2D ip = llip(topLeft, topRight, v1, v2);
			logt.debug("relevantWall", "Intersection with top edge: " + ip);
			if (ip.getX() > leftEdge && ip.getX() < rightEdge) {
				logt.debug("relevantWall", "Wall intersects with top edge.");
				return true;
			}
			ip = llip(bottomLeft, bottomRight, v1, v2);
			logt.debug("relevantWall", "Intersection with bottom edge: " + ip);
			if (ip.getX() > leftEdge && ip.getX() < rightEdge) {
				logt.debug("relevantWall", "Wall intersects with bottom edge.");
				return true;
			}
		}
		if (v1.getX() == v2.getX()) {
			logt.debug("relevantWall",
					"Wall is parallel with left and right edge.");
		} else {
			Point2D ip = llip(topLeft, bottomLeft, v1, v2);
			logt.debug("relevantWall", "Intersection with left edge: " + ip);
			if (ip.getY() > topEdge && ip.getY() < bottomEdge) {
				logt.debug("relevantWall", "Wall intersects with left edge.");
				return true;
			}
			ip = llip(topRight, bottomRight, v1, v2);
			logt.debug("relevantWall", "Intersection with right edge: " + ip);
			if (ip.getY() > topEdge && ip.getY() < bottomEdge) {
				logt.debug("relevantWall", "Wall intersects with right edge.");
				return true;
			}
		}
		return false;
	}

	private double channelWidthLoss(boolean useA, short channelWidth) {
		if (useA) {
			if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80) {
				return 5.2;
			} else if (channelWidth != RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) {
				return 2.6;
			}
		}
		return 0;
	}

	private short adjustChannelWidth(short channelWidth, short apModel,
			boolean useA) {
		if (useA) {
			if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80) {
                if (apModel != HiveAp.HIVEAP_MODEL_370 && apModel != HiveAp.HIVEAP_MODEL_390
                        && apModel != HiveAp.HIVEAP_MODEL_230) {
                    // override, this AP does not support 80 MHz channel width
                    return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A;
                }
            }
            return channelWidth;
        }
        else {
            return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20;
        }
    }

	public void predictedLap(MapContainerNode mapContainerNode,
			int imageWidthUsed, int imageHeightUsed, boolean useA,
			short shadesPerColor, PlannedAP plannedAP, short channelWidth,
			double squareSizeMetric, int imgScale, int rssiThreshold)
			throws Exception {
		double n = getPathLossFactor(mapContainerNode);
		double mapToMetric = mapContainerNode.getMapToMetric();

		List<Point2D.Double> v1s = new ArrayList<Point2D.Double>();
		List<Point2D.Double> v2s = new ArrayList<Point2D.Double>();
		List<Short> tis = new ArrayList<Short>();
		relevantWalls(mapContainerNode, squareSizeMetric, v1s, v2s, tis);

		int xi1 = mapContainerNode.x1;
		int xi2 = mapContainerNode.x2;
		int yi1 = mapContainerNode.y1;
		int yi2 = mapContainerNode.y2;

		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}

		double apx_m = plannedAP.x * mapToMetric;
		double apy_m = plannedAP.y * mapToMetric;
		logt.debug("predictedLap", "AP position (" + plannedAP.x + ", "
				+ plannedAP.y + "), metric (" + apx_m + ", " + apy_m + ")");
		Point2D l1p1 = new Point2D.Double(apx_m, apy_m);
		double power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;

		channelWidth = adjustChannelWidth(channelWidth, plannedAP.apModel, useA);
		double gain = getAverageGain(plannedAP.apModel, useA)
				- channelWidthLoss(useA, channelWidth);
		boolean enabled = useA ? plannedAP.wifi1Enabled
				: plannedAP.wifi0Enabled;

		Date start = new Date();
		int imageWidth = xi2 - xi1 + 1;
		int imageHeight = yi2 - yi1 + 1;
		short mapColors[][] = new short[imageWidth][imageHeight];
		short mapChannels[][] = new short[imageWidth][imageHeight];
		for (double x = xi1; x <= xi2; x += 1) {
			double x_m = (x + 0.5) * squareSizeMetric;
			for (double y = yi1; y <= yi2; y += 1) {
				if (x < 0 || y < 0 || !enabled) {
					mapColors[(int) x - xi1][(int) y - yi1] = -1;
					mapChannels[(int) x - xi1][(int) y - yi1] = 0;
					continue;
				}
				if (x >= imageWidthUsed || y >= imageHeightUsed) {
					mapColors[(int) x - xi1][(int) y - yi1] = -1;
					mapChannels[(int) x - xi1][(int) y - yi1] = 0;
					continue;
				}
				double y_m = (y + 0.5) * squareSizeMetric;
				double dx_m = apx_m - x_m;
				double dy_m = apy_m - y_m;
				double dxy = dx_m * dx_m + dy_m * dy_m;
				double d = Math.sqrt(dxy + apElevation * apElevation);
				mapColors[(int) x - xi1][(int) y - yi1] = rssiColor(useA, d, n,
						shadesPerColor, power, gain, l1p1, x_m, y_m, dxy, v1s,
						v2s, tis);
				mapChannels[(int) x - xi1][(int) y - yi1] = 0;
			}
		}

		for (PlannedAP nbrAP : mapContainerNode.getPlannedAPs()) {
			if (nbrAP.getId().equals(plannedAP.getId())) {
				continue;
			}
			boolean nbrEnabled = useA ? nbrAP.wifi1Enabled : nbrAP.wifi0Enabled;
			if (!nbrEnabled) {
				continue;
			}

			logt.debug("predictedLap",
					"Verify interference from: " + plannedAP.getId()
							+ ", with: " + nbrAP.getId());
			lapBoundaries(mapContainerNode, useA, nbrAP, squareSizeMetric,
					imgScale, rssiThreshold);

			if (mapContainerNode.x2 < xi1) {
				continue;
			}
			if (mapContainerNode.x1 > xi2) {
				continue;
			}
			if (mapContainerNode.y2 < yi1) {
				continue;
			}
			if (mapContainerNode.y1 > yi2) {
				continue;
			}

			v1s = new ArrayList<Point2D.Double>();
			v2s = new ArrayList<Point2D.Double>();
			tis = new ArrayList<Short>();
			relevantWalls(mapContainerNode, squareSizeMetric, v1s, v2s, tis);

			int xo1 = Math.max(mapContainerNode.x1, xi1);
			int xo2 = Math.min(mapContainerNode.x2, xi2);
			int yo1 = Math.max(mapContainerNode.y1, yi1);
			int yo2 = Math.min(mapContainerNode.y2, yi2);
			logt.debug("predictedLap", "Overlap area X (" + xo1 + ", " + xo2
					+ "), Y (" + yo1 + ", " + yo2 + ")");

			apx_m = nbrAP.x * mapToMetric;
			apy_m = nbrAP.y * mapToMetric;
			logt.debug("predictedLap", "nbr AP position (" + nbrAP.x + ", "
					+ nbrAP.y + "), metric (" + apx_m + ", " + apy_m + ")");
			l1p1 = new Point2D.Double(apx_m, apy_m);
			power = useA ? nbrAP.wifi1Power : nbrAP.wifi0Power;
			gain = getAverageGain(nbrAP.apModel, useA)
					- channelWidthLoss(useA, channelWidth);

			for (double x = xo1; x <= xo2; x += 1) {
				double x_m = (x + 0.5) * squareSizeMetric;
				for (double y = yo1; y <= yo2; y += 1) {
					double y_m = (y + 0.5) * squareSizeMetric;
					short rssiColor = mapColors[(int) x - xi1][(int) y - yi1];
					if (rssiColor < 0) {
						// beyond threshold
						continue;
					}
					double dx_m = apx_m - x_m;
					double dy_m = apy_m - y_m;
					double dxy = dx_m * dx_m + dy_m * dy_m;
					double d = Math.sqrt(dxy + apElevation * apElevation);
					short nbrColor = rssiColor(useA, d, n, shadesPerColor,
							power, gain, l1p1, x_m, y_m, dxy, v1s, v2s, tis);
					if (nbrColor < 0) {
						// beyond threshold
						continue;
					}
					short overlap = 0;
					if (nbrColor + overlap < rssiColor) {
						mapColors[(int) x - xi1][(int) y - yi1] = -1;
					}
				}
			}
		}
		Date end = new Date();
		logt.debug("predictedLap",
				"RSSI prediction: " + (end.getTime() - start.getTime())
						+ " ms.");
		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);
		mapContainerNode.setApChannel(null);
	}

	private void relevantWalls(MapContainerNode mapContainerNode,
			double squareSizeMetric, List<Point2D.Double> v1s,
			List<Point2D.Double> v2s, List<Short> tis) {
		double leftEdge = mapContainerNode.x1 * squareSizeMetric;
		double rightEdge = (mapContainerNode.x2 + 1) * squareSizeMetric;
		double topEdge = mapContainerNode.y1 * squareSizeMetric;
		double bottomEdge = (mapContainerNode.y2 + 1) * squareSizeMetric;

		double mapToMetric = mapContainerNode.getMapToMetric();
		Point2D topLeft = new Point2D.Double(leftEdge, topEdge);
		Point2D topRight = new Point2D.Double(rightEdge, topEdge);
		Point2D bottomLeft = new Point2D.Double(leftEdge, bottomEdge);
		Point2D bottomRight = new Point2D.Double(rightEdge, bottomEdge);
		for (int i = 0; i < mapContainerNode.getWalls().size(); i++) {
			Wall wall = mapContainerNode.getWalls().get(i);
			Point2D.Double v1 = new Point2D.Double(wall.getX1() * mapToMetric,
					wall.getY1() * mapToMetric);
			Point2D.Double v2 = new Point2D.Double(wall.getX2() * mapToMetric,
					wall.getY2() * mapToMetric);
			if (relevantWall(v1, v2, topLeft, topRight, bottomLeft,
					bottomRight, leftEdge, topEdge, rightEdge, bottomEdge)
					&& wall.getType() >= 0) {
				v1s.add(v1);
				v2s.add(v2);
				tis.add(Wall.getWallIndex(wall.getType()));
			}
		}
		int relevantWallCount = v1s.size();
		if (mapContainerNode.getPerimeter().size() > 0) {
			Vertex vertex = mapContainerNode.getPerimeter().get(0);
			Point2D.Double first = new Point2D.Double(vertex.getX()
					* mapToMetric, vertex.getY() * mapToMetric);
			Point2D.Double v1 = first;
			int perimId = vertex.getId();
			short perimType = vertex.getType();
			for (int i = 1; i < mapContainerNode.getPerimeter().size(); i++) {
				vertex = mapContainerNode.getPerimeter().get(i);
				Point2D.Double v2 = new Point2D.Double(vertex.getX()
						* mapToMetric, vertex.getY() * mapToMetric);
				if (vertex.getId() != perimId) {
					if (relevantWall(v1, first, topLeft, topRight, bottomLeft,
							bottomRight, leftEdge, topEdge, rightEdge,
							bottomEdge)) {
						v1s.add(v1);
						v2s.add(first);
						tis.add(getPerimTypeIndex(perimType));
					}
					first = v2;
					perimId = vertex.getId();
					perimType = vertex.getType();
				} else if (relevantWall(v1, v2, topLeft, topRight, bottomLeft,
						bottomRight, leftEdge, topEdge, rightEdge, bottomEdge)) {
					v1s.add(v1);
					v2s.add(v2);
					tis.add(getPerimTypeIndex(perimType));
				}
				v1 = v2;
			}
			if (relevantWall(v1, first, topLeft, topRight, bottomLeft,
					bottomRight, leftEdge, topEdge, rightEdge, bottomEdge)) {
				v1s.add(v1);
				v2s.add(first);
				tis.add(getPerimTypeIndex(perimType));
			}
		}
		logt.debug("relevantWalls", "Wall count ("
				+ mapContainerNode.getWalls().size() + ", " + relevantWallCount
				+ "), perimeter wall count ("
				+ mapContainerNode.getPerimeter().size() + ", "
				+ (v1s.size() - relevantWallCount) + "), total relevant: "
				+ v1s.size());
	}

	private short getPerimTypeIndex(short type) {
		return Wall
				.getWallIndex(type == 1003 || type == 1004 || type == 1005 ? type
						: 1003);
	}

	private short rssiColor(boolean useA, double d, double n,
			short shadesPerColor, double power, double gain, Point2D l1p1,
			double x_m, double y_m, double dxy, List<Point2D.Double> v1s,
			List<Point2D.Double> v2s, List<Short> tis) {
		long rssi = Math.round(rssi(useA, d, n, shadesPerColor, power, gain,
				l1p1, x_m, y_m, dxy, v1s, v2s, tis));
		if (rssi > -35) {
			rssi = -35;
		}
		short rssiColor = (short) (-35 - rssi);
		if (rssiColor >= shadesPerColor) {
			rssiColor = -1;
		}
		return rssiColor;
	}

	private float rssi(boolean useA, double d, double n, short shadesPerColor,
			double power, double gain, Point2D l1p1, double x_m, double y_m,
			double dxy, List<Point2D.Double> v1s, List<Point2D.Double> v2s,
			List<Short> tis) {
		double pathLoss = 0;
		if (d < 1) {
			d = 1;
		}
		if (useA) {
			pathLoss = getPathLossA(d, n);
		} else {
			pathLoss = getPathLossBG(d, n);
		}
		if (dxy > 1e-5 && v1s.size() > 0) {
			pathLoss += getWallLoss(l1p1, x_m, y_m, v1s, v2s, tis);
		}
		return (float) (power + gain - pathLoss);
	}

	private double getWallLoss(Point2D l1p1, double x_m, double y_m,
			List<Point2D.Double> v1s, List<Point2D.Double> v2s, List<Short> tis) {
		double loss = 0;
		Point2D l1p2 = new Point2D.Double(x_m, y_m);
		for (int i = 0; i < v1s.size(); i++) {
			Point2D l2p1 = v1s.get(i);
			Point2D l2p2 = v2s.get(i);
			short ti = tis.get(i);
			if (lli(l1p1, l1p2, l2p1, l2p2) != null) {
				double angle = lla(l1p1, l1p2, l2p1, l2p2);
				double absorption = Wall.wallAbsorption[ti];
				double thickness = Wall.wallWidth[ti];
				double travel = thickness / Math.sin(angle);
				double wallLoss = travel * absorption;
				loss += wallLoss;
			}
		}
		return loss;
	}

	public void lapBoundaries(MapContainerNode mapContainerNode, boolean useA,
			PlannedAP plannedAP, double squareSizeMetric, int imgScale,
			int rssiThreshold) throws Exception {
		lapBoundaries(mapContainerNode, getPathLossFactor(mapContainerNode),
				useA, plannedAP, squareSizeMetric, imgScale, rssiThreshold, 0);
	}

	public void lapBoundaries(MapContainerNode mapContainerNode, double plf,
			boolean useA, PlannedAP plannedAP, double squareSizeMetric,
			int imgScale, int rssiThreshold, double floorLoss) throws Exception {
		short power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
		double distance = predictedMapSize(power, plannedAP.apModel, plf, useA,
				rssiThreshold, floorLoss);
		double mapToMetric = mapContainerNode.getMapToMetric();
		double apx_m = plannedAP.x * mapToMetric;
		double apy_m = plannedAP.y * mapToMetric;
		double ax1 = apx_m - distance;
		double ay1 = apy_m - distance;
		double ax2 = apx_m + distance;
		double ay2 = apy_m + distance;

		int aix1 = (int) (ax1 / squareSizeMetric);
		int aiy1 = (int) (ay1 / squareSizeMetric);
		int aix2 = (int) (ax2 / squareSizeMetric);
		int aiy2 = (int) (ay2 / squareSizeMetric);

		logt.debug("lapBoundaries", "     area X from: " + ax1 + " to: " + ax2
				+ ", square: " + aix1 + ", " + aix2 + " (" + (aix2 - aix1 + 1)
				+ "), pixel: " + aix1 * imgScale + ", " + aix2 * imgScale
				+ " (" + (aix2 - aix1 + 1) * imgScale + ")");

		power -= floorLoss;
		int rssi = 0, count = 0, maxCount = 20;
		do {
			double dx = apx_m - (--aix1 + 0.5) * squareSizeMetric;
			if (dx < 1) {
				break;
			}
			rssi = (int) predictedRssi(plf, power, plannedAP.apModel, useA, dx);
		} while (rssi >= rssiThreshold && count++ < maxCount);
		logt.debug("lapBoundaries", "left edge at: " + aix1 + ", rssi: " + rssi);
		rssi = 0;
		count = 0;
		do {
			double dx = (++aix2 + 0.5) * squareSizeMetric - apx_m;
			if (dx < 1) {
				break;
			}
			rssi = (int) predictedRssi(plf, power, plannedAP.apModel, useA, dx);
		} while (rssi >= rssiThreshold && count++ < maxCount);
		log.debug("% right edge at: " + aix2 + ", rssi: " + rssi + ", count: "
				+ count);

		logt.debug("lapBoundaries", "adj. area X from: " + ax1 + " to: " + ax2
				+ ", square: " + aix1 + ", " + aix2 + " (" + (aix2 - aix1 + 1)
				+ "), pixel: " + aix1 * imgScale + ", " + aix2 * imgScale
				+ " (" + (aix2 - aix1 + 1) * imgScale + ")");

		logt.debug("lapBoundaries", "     area Y from: " + ay1 + " to: " + ay2
				+ ", square: " + aiy1 + ", " + aiy2 + " (" + (aiy2 - aiy1 + 1)
				+ "), pixel: " + aiy1 * imgScale + ", " + aiy2 * imgScale
				+ " (" + (aiy2 - aiy1 + 1) * imgScale + ")");
		rssi = 0;
		count = 0;
		do {
			double dy = apy_m - (--aiy1 + 0.5) * squareSizeMetric;
			if (dy < 1) {
				break;
			}
			rssi = (int) predictedRssi(plf, power, plannedAP.apModel, useA, dy);
		} while (rssi >= rssiThreshold && count++ < maxCount);
		logt.debug("lapBoundaries", "top edge at: " + aiy1 + ", rssi: " + rssi);
		rssi = 0;
		count = 0;
		do {
			double dy = (++aiy2 + 0.5) * squareSizeMetric - apy_m;
			if (dy < 1) {
				break;
			}
			rssi = (int) predictedRssi(plf, power, plannedAP.apModel, useA, dy);
		} while (rssi >= rssiThreshold && count++ < maxCount);
		logt.debug("lapBoundaries", "bottom edge at: " + aiy2 + ", rssi: "
				+ rssi);

		logt.debug("lapBoundaries", "Adj. area Y from: " + ay1 + " to: " + ay2
				+ ", square: " + aiy1 + ", " + aiy2 + " (" + (aiy2 - aiy1 + 1)
				+ "), pixel: " + aiy1 * imgScale + ", " + aiy2 * imgScale
				+ " (" + (aiy2 - aiy1 + 1) * imgScale + ")");

		mapContainerNode.x1 = aix1;
		mapContainerNode.y1 = aiy1;
		mapContainerNode.x2 = aix2;
		mapContainerNode.y2 = aiy2;
	}

	public Point2D lli(Point2D l1p1, Point2D l1p2, Point2D l2p1, Point2D l2p2) {
		return llis(l1p1, l1p2, l2p1, l2p2);
	}

	private static Point2D llis(Point2D l1p1, Point2D l1p2, Point2D l2p1,
			Point2D l2p2) {
		double l1x1 = l1p1.getX();
		double l1x2 = l1p2.getX();
		if (l1x2 < l1x1) {
			l1x1 = l1x2;
			l1x2 = l1p1.getX();
		}
		double l1y1 = l1p1.getY();
		double l1y2 = l1p2.getY();
		if (l1y2 < l1y1) {
			l1y1 = l1y2;
			l1y2 = l1p1.getY();
		}
		double l2x1 = l2p1.getX();
		double l2x2 = l2p2.getX();
		if (l2x2 < l2x1) {
			l2x1 = l2x2;
			l2x2 = l2p1.getX();
		}
		double l2y1 = l2p1.getY();
		double l2y2 = l2p2.getY();
		if (l2y2 < l2y1) {
			l2y1 = l2y2;
			l2y2 = l2p1.getY();
		}
		Point2D ip = llip(l1p1, l1p2, l2p1, l2p2);
		double ipx = ip.getX();
		double ipy = ip.getY();
		double tol = 1e-6;
		if (Math.abs(l1x1 - l1x2) < tol) {
			ipx = l1x1;
		} else if (Math.abs(l2x1 - l2x2) < tol) {
			ipx = l2x1;
		}
		if (Math.abs(l1y1 - l1y2) < tol) {
			ipy = l1y1;
		} else if (Math.abs(l2y1 - l2y2) < tol) {
			ipy = l2y1;
		}
		if (ipx >= l1x1 && ipx <= l1x2 && ipx >= l2x1 && ipx <= l2x2
				&& ipy >= l1y1 && ipy <= l1y2 && ipy >= l2y1 && ipy <= l2y2) {
			return ip;
		} else {
			return null;
		}
	}

	public static double lla(Point2D l1p1, Point2D l1p2, Point2D l2p1,
			Point2D l2p2) {
		double a1 = l1p1.getY() - l1p2.getY();
		double b1 = l1p1.getX() - l1p2.getX();
		double a2 = l2p1.getY() - l2p2.getY();
		double b2 = l2p1.getX() - l2p2.getX();

		double tan;
		if (b1 == 0) {
			tan = b2 / a2;
		} else if (b2 == 0) {
			tan = b1 / a1;
		} else {
			double m1 = a1 / b1;
			double m2 = a2 / b2;
			tan = (m2 - m1) / (1.0 + m1 * m2);
		}
		double angle = Math.atan(Math.abs(tan));
		if (angle > Math.PI / 2) {
			logt.info("lla", "Angle should always be less than 90 degrees: "
					+ angle * 180.0 / Math.PI);
		}
		logt.debug("lla", "Tan: " + tan + ", angle: " + angle + ", degrees: "
				+ angle * 180.0 / Math.PI);
		return angle;
	}

	public Collection<JSONObject> cacheDoubleBuffer(MapContainerNode floor,
			int canvasWidth, int canvasHeight, Map<Long, Short> apIndexMap,
			float spillRssi[][], short spillChannels[][], float mapRssi[][],
			short mapChannels[][], double pixelSizeMetric, int imgScale,
			boolean useA, short shadesPerColor, boolean spillOnly,
			short channelWidth) throws Exception {
		Date start = new Date();
		List<MapContainerNode> floors = sortFloors(floor.getParentMap());
		MapContainerNode higherFloor = null, lowerFloor = null;
		for (MapContainerNode container : floors) {
			if (container.getId().equals(floor.getId())) {
				lowerFloor = container;
			} else if (lowerFloor == null) {
				higherFloor = container;
			} else {
				lowerFloor = container;
				break;
			}
		}
		if (lowerFloor.getId().equals(floor.getId())) {
			lowerFloor = null;
		}
		logt.info(
				"nbrSimApCoverage",
				"Floor: "
						+ floor.getMapName()
						+ ", higher: "
						+ (higherFloor == null ? "<none>" : higherFloor
								.getMapName())
						+ ", lower: "
						+ (lowerFloor == null ? "<none>" : lowerFloor
								.getMapName()));
		double squareSizeMetric = pixelSizeMetric * imgScale;

		if (higherFloor != null && higherFloor.getActualWidthMetric() > 0) {
			for (PlannedAP nbrAP : higherFloor.getPlannedAPs()) {
				Short apIndex = apIndexMap.get(nbrAP.getId());
				if (apIndex == null) {
					logt.info("AP " + nbrAP.hostName
							+ " must have been added by another user.");
				} else {
					findSpillHigherFloorCoveredArea(floor, higherFloor,
							spillRssi, spillChannels, imgScale, useA,
							shadesPerColor, squareSizeMetric, channelWidth,
							nbrAP, apIndex);
				}
			}
		}
		if (lowerFloor != null && lowerFloor.getActualWidthMetric() > 0) {
			for (PlannedAP nbrAP : lowerFloor.getPlannedAPs()) {
				Short apIndex = apIndexMap.get(nbrAP.getId());
				if (apIndex == null) {
					logt.info("AP " + nbrAP.hostName
							+ " must have been added by another user.");
				} else {
					findSpillLowerFloorCoveredArea(floor, lowerFloor,
							spillRssi, spillChannels, imgScale, useA,
							shadesPerColor, squareSizeMetric, channelWidth,
							nbrAP, apIndex);
				}
			}
		}
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		if (!spillOnly) {
			// Recalculate current floor as well.
			for (PlannedAP ap : floor.getPlannedAPs()) {
				Short apIndex = apIndexMap.get(ap.getId());
				if (apIndex == null) {
					logt.info("AP " + ap.hostName
							+ " must have been added by another user.");
				} else {
					boolean affected = findSpilledApCoveredArea(floor,
							spillRssi, mapRssi, mapChannels, imgScale, useA,
							shadesPerColor, squareSizeMetric, channelWidth, ap,
							apIndex);
					if (affected) {
						JSONObject jo = new JSONObject();
						jo.put("nodeId", "s" + ap.getId());
						jsonNodes.add(jo);
					}
				}
			}
		}
		Date end = new Date();
		logt.info("Spill coverage calculation: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return jsonNodes;
	}

	private void findSpillHigherFloorCoveredArea(MapContainerNode floor,
			MapContainerNode nbrFloor, float spillRssi[][],
			short spillChannels[][], int imgScale, boolean useA,
			short shadesPerColor, double squareSizeMetric, short channelWidth,
			PlannedAP plannedAP, short apIndex) throws Exception {
		if (useA) {
			if (!plannedAP.wifi1Enabled) {
				return;
			}
		} else if (!plannedAP.wifi0Enabled) {
			return;
		}
		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double imageWidth = spillRssi.length;
		double imageHeight = spillRssi[0].length;
		double h2 = nbrFloor.getApElevationMetric();
		double apElevation = h2 + floor.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}
		double els = apElevation * apElevation;
		// Should tighten the boundaries because from higher floor
		lapBoundaries(nbrFloor, mfloorle, useA, plannedAP, squareSizeMetric,
				imgScale, rssiThreshold, nbrFloor.getFloorLoss());

		double align_x_m = nbrFloor.getOriginXmetric()
				- floor.getOriginXmetric();
		double align_y_m = nbrFloor.getOriginYmetric()
				- floor.getOriginYmetric();
		double leftEdge = nbrFloor.x1 * squareSizeMetric + align_x_m;
		double rightEdge = (nbrFloor.x2 + 1) * squareSizeMetric + align_x_m;
		double topEdge = nbrFloor.y1 * squareSizeMetric + align_y_m;
		double bottomEdge = (nbrFloor.y2 + 1) * squareSizeMetric + align_y_m;

		List<Point2D.Double> nbr_v1s = new ArrayList<Point2D.Double>();
		List<Point2D.Double> nbr_v2s = new ArrayList<Point2D.Double>();
		List<Short> nbr_tis = new ArrayList<Short>();
		relevantWalls(nbrFloor, squareSizeMetric, nbr_v1s, nbr_v2s, nbr_tis);
		nbr_v1s = adjustForAlignment(nbr_v1s, align_x_m, align_y_m);
		nbr_v2s = adjustForAlignment(nbr_v2s, align_x_m, align_y_m);
		floor.x1 = (int) Math.round(leftEdge / squareSizeMetric);
		floor.x2 = (int) Math.round(rightEdge / squareSizeMetric);
		floor.y1 = (int) Math.round(topEdge / squareSizeMetric);
		floor.y2 = (int) Math.round(bottomEdge / squareSizeMetric);
		List<Point2D.Double> v1s = new ArrayList<Point2D.Double>();
		List<Point2D.Double> v2s = new ArrayList<Point2D.Double>();
		List<Short> tis = new ArrayList<Short>();
		relevantWalls(floor, squareSizeMetric, v1s, v2s, tis);

		double start_x = (int) (leftEdge / squareSizeMetric);
		if (start_x < 0) {
			start_x = 0;
		}
		double end_x = (int) (rightEdge / squareSizeMetric) + 1;
		if (end_x > imageWidth) {
			end_x = imageWidth;
		}
		double start_y = (int) (topEdge / squareSizeMetric);
		if (start_y < 0) {
			start_y = 0;
		}
		double end_y = (int) (bottomEdge / squareSizeMetric) + 1;
		if (end_y > imageHeight) {
			end_y = imageHeight;
		}

		double power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
		channelWidth = adjustChannelWidth(channelWidth, plannedAP.apModel, useA);
		double gain = getAverageGain(plannedAP.apModel, useA)
				- channelWidthLoss(useA, channelWidth)
				- nbrFloor.getFloorLoss();

		double nbrMapToMetric = nbrFloor.getMapToMetric();
		double node_x_m = plannedAP.x * nbrMapToMetric + align_x_m;
		double node_y_m = plannedAP.y * nbrMapToMetric + align_y_m;
		Point2D l1p1 = new Point2D.Double(node_x_m, node_y_m);
		for (double x = start_x; x < end_x; x += 1) {
			double x_m = (x + 0.5) * squareSizeMetric;
			for (double y = start_y; y < end_y; y += 1) {
				float rssi = spillRssi[(int) x][(int) y];
				double y_m = (y + 0.5) * squareSizeMetric;
				double dx_m = node_x_m - x_m;
				double dy_m = node_y_m - y_m;
				double dxy = dx_m * dx_m + dy_m * dy_m;
				double d = Math.sqrt(dxy + apElevation * apElevation);
				if (d < 1) {
					d = 1;
				}
				double loss = useA ? getPathLossA(d, mfloorle) : getPathLossBG(
						d, mfloorle);
				float newRssi = (float) (power + gain - loss);
				if (rssi == 0 || newRssi > rssi) {
					if (newRssi >= rssiThreshold) {
						double dxi = getFloorIntersection(dx_m, els, h2);
						double dyi = getFloorIntersection(dy_m, els, h2);
						double xi = x_m + dxi;
						double yi = y_m + dyi;
						if (dxy > 1e-5) {
							if (nbr_v1s.size() > 0) {
								double wallLoss = getWallLoss(l1p1, xi, yi,
										nbr_v1s, nbr_v2s, nbr_tis);
								newRssi -= wallLoss;
							}
							if (v2s.size() > 0) {
								double wallLoss = getWallLoss(
										new Point2D.Double(xi, yi), x_m, y_m,
										v1s, v2s, tis);
								newRssi -= wallLoss;
							}
						}
						// reserve 0 for not covered
						if (newRssi > -1) {
							newRssi = -1;
						}
						spillRssi[(int) x][(int) y] = newRssi;
						spillChannels[(int) x][(int) y] = apIndex;
					}
				}
			}
		}
	}

	private List<Point2D.Double> adjustForAlignment(List<Point2D.Double> vs,
			double align_x, double align_y) {
		List<Point2D.Double> vs_new = new ArrayList<Point2D.Double>();
		for (Point2D.Double v : vs) {
			vs_new.add(new Point2D.Double(v.x + align_x, v.y + align_y));
		}
		return vs_new;
	}

	private double getFloorIntersection(double delta, double els, double h2) {
		double dr = delta / Math.sqrt(delta * delta + els);
		double r2 = h2 / Math.sqrt(1.0 - dr * dr);
		return r2 * dr;
	}

	private void findSpillLowerFloorCoveredArea(MapContainerNode floor,
			MapContainerNode nbrFloor, float spillRssi[][],
			short spillChannels[][], int imgScale, boolean useA,
			short shadesPerColor, double squareSizeMetric, short channelWidth,
			PlannedAP plannedAP, short apIndex) throws Exception {
		if (useA) {
			if (!plannedAP.wifi1Enabled) {
				return;
			}
		} else if (!plannedAP.wifi0Enabled) {
			return;
		}
		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double imageWidth = spillRssi.length;
		double imageHeight = spillRssi[0].length;
		// Elevation from ceiling of floor below
		double apElevation = elevationMargin;
		// Should tighten the boundaries because from lower floor,
		// Tightening will benefit relevantWalls as well.
		lapBoundaries(nbrFloor, mfloorle, useA, plannedAP, squareSizeMetric,
				imgScale, rssiThreshold, floor.getFloorLoss()
						+ getAverageShadow(plannedAP.apModel, useA));

		double align_x_m = nbrFloor.getOriginXmetric()
				- floor.getOriginXmetric();
		double align_y_m = nbrFloor.getOriginYmetric()
				- floor.getOriginYmetric();
		double leftEdge = nbrFloor.x1 * squareSizeMetric + align_x_m;
		double rightEdge = (nbrFloor.x2 + 1) * squareSizeMetric + align_x_m;
		double topEdge = nbrFloor.y1 * squareSizeMetric + align_y_m;
		double bottomEdge = (nbrFloor.y2 + 1) * squareSizeMetric + align_y_m;

		floor.x1 = (int) Math.round(leftEdge / squareSizeMetric);
		floor.x2 = (int) Math.round(rightEdge / squareSizeMetric);
		floor.y1 = (int) Math.round(topEdge / squareSizeMetric);
		floor.y2 = (int) Math.round(bottomEdge / squareSizeMetric);
		List<Point2D.Double> v1s = new ArrayList<Point2D.Double>();
		List<Point2D.Double> v2s = new ArrayList<Point2D.Double>();
		List<Short> tis = new ArrayList<Short>();
		relevantWalls(floor, squareSizeMetric, v1s, v2s, tis);

		double start_x = (int) (leftEdge / squareSizeMetric);
		if (start_x < 0) {
			start_x = 0;
		}
		double end_x = (int) (rightEdge / squareSizeMetric) + 1;
		if (end_x > imageWidth) {
			end_x = imageWidth;
		}
		double start_y = (int) (topEdge / squareSizeMetric);
		if (start_y < 0) {
			start_y = 0;
		}
		double end_y = (int) (bottomEdge / squareSizeMetric) + 1;
		if (end_y > imageHeight) {
			end_y = imageHeight;
		}

		double power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
		channelWidth = adjustChannelWidth(channelWidth, plannedAP.apModel, useA);
		double gain = getAverageGain(plannedAP.apModel, useA)
				- channelWidthLoss(useA, channelWidth) - floor.getFloorLoss()
				- getAverageShadow(plannedAP.apModel, useA);

		double thickness = 0.1;
		double absorption = floor.getFloorLoss() / thickness;
		double h2 = apElevation - thickness;

		double nbrMapToMetric = nbrFloor.getMapToMetric();
		double node_x_m = plannedAP.x * nbrMapToMetric + align_x_m;
		double node_y_m = plannedAP.y * nbrMapToMetric + align_y_m;
		Point2D l1p1 = new Point2D.Double(node_x_m, node_y_m);
		for (double x = start_x; x < end_x; x += 1) {
			double x_m = (x + 0.5) * squareSizeMetric;
			for (double y = start_y; y < end_y; y += 1) {
				float rssi = spillRssi[(int) x][(int) y];
				double y_m = (y + 0.5) * squareSizeMetric;
				double dx_m = node_x_m - x_m;
				double dy_m = node_y_m - y_m;
				double dxy = dx_m * dx_m + dy_m * dy_m;
				double d = Math.sqrt(dxy + apElevation * apElevation);
				float newRssi = rssi(useA, d, mfloorle, shadesPerColor, power,
						gain, l1p1, x_m, y_m, dxy, v1s, v2s, tis);
				if (rssi == 0 || newRssi > rssi) {
					if (false) {
						double dxi = getFloorIntersection(dx_m, 1, h2);
						double dyi = getFloorIntersection(dy_m, 1, h2);
						double ri = Math.sqrt(dxi * dxi + dyi * dyi + h2 * h2);
						double travel = d - ri;
						double loss = travel * absorption;
						newRssi -= loss - floor.getFloorLoss();
					}
					if (newRssi >= rssiThreshold) {
						// reserve 0 for not covered
						if (newRssi > -1) {
							newRssi = -1;
						}
						spillRssi[(int) x][(int) y] = newRssi;
						spillChannels[(int) x][(int) y] = apIndex;
					}
				}
			}
		}
	}

	public boolean findSpilledApCoveredArea(MapContainerNode floor,
			float spillRssi[][], float mapRssi[][], short mapChannels[][],
			int imgScale, boolean useA, short shadesPerColor,
			double squareSizeMetric, short channelWidth, PlannedAP plannedAP,
			short apIndex) throws Exception {
		if (useA) {
			if (!plannedAP.wifi1Enabled) {
				return false;
			}
		} else if (!plannedAP.wifi0Enabled) {
			return false;
		}
		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double imageWidth = spillRssi.length;
		double imageHeight = spillRssi[0].length;
		double apElevation = floor.getApElevationMetric() - elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}
		double n = getPathLossFactor(floor);
		lapBoundaries(floor, useA, plannedAP, squareSizeMetric, imgScale,
				rssiThreshold);

		double leftEdge = floor.x1 * squareSizeMetric;
		double rightEdge = (floor.x2 + 1) * squareSizeMetric;
		double topEdge = floor.y1 * squareSizeMetric;
		double bottomEdge = (floor.y2 + 1) * squareSizeMetric;

		List<Point2D.Double> v1s = new ArrayList<Point2D.Double>();
		List<Point2D.Double> v2s = new ArrayList<Point2D.Double>();
		List<Short> tis = new ArrayList<Short>();
		relevantWalls(floor, squareSizeMetric, v1s, v2s, tis);

		double start_x = (int) (leftEdge / squareSizeMetric);
		if (start_x < 0) {
			start_x = 0;
		}
		double end_x = (int) (rightEdge / squareSizeMetric) + 1;
		if (end_x > imageWidth) {
			end_x = imageWidth;
		}
		double start_y = (int) (topEdge / squareSizeMetric);
		if (start_y < 0) {
			start_y = 0;
		}
		double end_y = (int) (bottomEdge / squareSizeMetric) + 1;
		if (end_y > imageHeight) {
			end_y = imageHeight;
		}

		double power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
		channelWidth = adjustChannelWidth(channelWidth, plannedAP.apModel, useA);
		double gain = getAverageGain(plannedAP.apModel, useA)
				- channelWidthLoss(useA, channelWidth);

		boolean affected = false;
		double mapToMetric = floor.getMapToMetric();
		double node_x_m = plannedAP.x * mapToMetric;
		double node_y_m = plannedAP.y * mapToMetric;
		Point2D l1p1 = new Point2D.Double(node_x_m, node_y_m);
		for (double x = start_x; x < end_x; x += 1) {
			double x_m = (x + 0.5) * squareSizeMetric;
			for (double y = start_y; y < end_y; y += 1) {
				float s_rssi = spillRssi[(int) x][(int) y];
				float m_rssi = mapRssi[(int) x][(int) y];
				double y_m = (y + 0.5) * squareSizeMetric;
				double dx_m = node_x_m - x_m;
				double dy_m = node_y_m - y_m;
				double dxy = dx_m * dx_m + dy_m * dy_m;
				double d = Math.sqrt(dxy + apElevation * apElevation);
				float newRssi = rssi(useA, d, n, shadesPerColor, power, gain,
						l1p1, x_m, y_m, dxy, v1s, v2s, tis);
				if (s_rssi != 0 && newRssi <= s_rssi) {
					// Spill image covers this AP's coverage area
					affected = true;
				}
				if (m_rssi == 0 || newRssi > m_rssi) {
					// round() not really required
					if (Math.round(newRssi) >= rssiThreshold) {
						// reserve 0 for not covered
						if (newRssi > -1) {
							newRssi = -1;
						}
						mapRssi[(int) x][(int) y] = newRssi;
						mapChannels[(int) x][(int) y] = apIndex;
					}
				}
			}
		}
		return affected;
	}

	public int simApCoverage(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor) throws Exception {
		double squareSizeMetric = getCoverageScale(mapContainerNode,
				planConfig, canvasWidth, canvasHeight, useA, shadesPerColor,
				true);
		short mapColors[][] = mapContainerNode.getMapColors();
		short mapChannels[][] = mapContainerNode.getMapChannels();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;

		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double dtw = getMinDistanceToPerimeter(predictedMapSize(
				mapContainerNode, planConfig, useA, rssiThreshold));

		Date start = new Date();
		double perimPenalties[][] = new double[(int) imageWidth][(int) imageHeight];
		int candidateCount = setPerimeterPenalties(mapContainerNode, mapColors,
				mapChannels, mapContainerNode.getPlannedAPs().size(),
				perimPenalties, squareSizeMetric, false, dtw, true);

		double r = predictedMapSize(mapContainerNode, planConfig, useA,
				rssiThreshold);
		findCoveredArea(mapContainerNode, new ArrayList<PlannedAP>(
				mapContainerNode.getPlannedAPs()), useA, shadesPerColor,
				squareSizeMetric, candidateCount, r,
				planConfig.getChannelWidth());
		Date end = new Date();
		logt.info("simApCoverage", "Auto: " + (end.getTime() - start.getTime())
				+ " ms.");

		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);
		return MapSettings.HEATMAP_RESOLUTION_HIGH;
	}

	private int findCoveredArea(MapContainerNode mapContainerNode,
			List<PlannedAP> aps, boolean useA, short shadesPerColor,
			double squareSizeMetric, int candidateCount, double r,
			short channelWidth) throws Exception {
		int dotCount = 0;
		for (short i = 0; i < aps.size(); i++) {
			PlannedAP plannedAP = aps.get(i);
			if (plannedAP.getId() != null || !plannedAP.isSelected()) {
				int apDotCount = findApCoveredArea(mapContainerNode, useA,
						shadesPerColor, squareSizeMetric, channelWidth,
						plannedAP, i);
				dotCount += apDotCount;
			}
		}
		double percentage = dotCount / (double) candidateCount * 100;
		int tile = (int) (Math.PI * r * r / squareSizeMetric);
		String fmt = "Covered %1$d dots (%2$d remaining) or %3$.2f%% coverage, max %4$d dots per AP.";
		log.info_ln(String.format(fmt, dotCount, candidateCount - dotCount,
				percentage, tile));
		return dotCount;
	}

	private int findApCoveredArea(MapContainerNode mapContainerNode,
			boolean useA, short shadesPerColor, double squareSizeMetric,
			short channelWidth, PlannedAP plannedAP, short apId)
			throws Exception {
		int rssiThreshold = -(shadesPerColor + 35 - 1);
		short mapColors[][] = mapContainerNode.getMapColors();
		short mapChannels[][] = mapContainerNode.getMapChannels();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;
		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}
		double n = getPathLossFactor(mapContainerNode);
		double mapToMetric = mapContainerNode.getMapToMetric();
		double imageToMetric = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;
		lapBoundaries(mapContainerNode, useA, plannedAP, squareSizeMetric, 1,
				rssiThreshold);

		double leftEdge = mapContainerNode.x1 * imageToMetric;
		double rightEdge = (mapContainerNode.x2 + 1) * imageToMetric;
		double topEdge = mapContainerNode.y1 * imageToMetric;
		double bottomEdge = (mapContainerNode.y2 + 1) * imageToMetric;

		List<Point2D.Double> v1s = new ArrayList<Point2D.Double>();
		List<Point2D.Double> v2s = new ArrayList<Point2D.Double>();
		List<Short> tis = new ArrayList<Short>();
		relevantWalls(mapContainerNode, imageToMetric, v1s, v2s, tis);

		double start_x = (int) (leftEdge / squareSizeMetric);
		if (start_x < 0) {
			start_x = 0;
		}
		double end_x = (int) (rightEdge / squareSizeMetric) + 1;
		if (end_x > imageWidth) {
			end_x = imageWidth;
		}
		double start_y = (int) (topEdge / squareSizeMetric);
		if (start_y < 0) {
			start_y = 0;
		}
		double end_y = (int) (bottomEdge / squareSizeMetric) + 1;
		if (end_y > imageHeight) {
			end_y = imageHeight;
		}

		double power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
		channelWidth = adjustChannelWidth(channelWidth, plannedAP.apModel, useA);
		double gain = getAverageGain(plannedAP.apModel, useA)
				- channelWidthLoss(useA, channelWidth);

		int apDotCount = 0;
		double node_x_m = plannedAP.x * mapToMetric;
		double node_y_m = plannedAP.y * mapToMetric;
		Point2D l1p1 = new Point2D.Double(node_x_m, node_y_m);
		for (double x = start_x; x < end_x; x += 1) {
			double x_m = (x + 0.5) * imageToMetric;
			for (double y = start_y; y < end_y; y += 1) {
				short mapColor = mapColors[(int) x][(int) y];
				if (mapColor == -2) {
					continue;
				}
				double y_m = (y + 0.5) * imageToMetric;
				double dx_m = node_x_m - x_m;
				double dy_m = node_y_m - y_m;
				double dxy = dx_m * dx_m + dy_m * dy_m;
				double d = Math.sqrt(dxy + apElevation * apElevation);
				short rssiColor = rssiColor(useA, d, n, shadesPerColor, power,
						gain, l1p1, x_m, y_m, dxy, v1s, v2s, tis);

				if (rssiColor >= 0) {
					if (mapColor < 0) {
						apDotCount++;
					}
					if (apId != -1) {
						if (mapColor < 0 || rssiColor < mapColor) {
							mapColors[(int) x][(int) y] = rssiColor;
							mapChannels[(int) x][(int) y] = apId;
						}
					}
				}
			}
		}
		return apDotCount;
	}

	private double getCoverageScale(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, boolean verbose)
			throws Exception {
		double imageWidth = canvasWidth;
		double imageHeight = canvasHeight;
		double complexity = imageWidth * imageHeight;
		double mapToMetric = mapContainerNode.getMapToMetric();
		double squareSizeMetric = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;

		if (mapContainerNode.getWalls().size() > 0 && !verbose) {
			imageWidth /= 2;
			imageHeight /= 2;
			complexity /= 4;
			squareSizeMetric *= 2;
		}
		logt.info("getCoverageScale", "# units: " + (int) complexity
				+ ", square size: " + squareSizeMetric + " meters.");

		int complexityLimit = Integer.parseInt(System
				.getProperty("heatMap.complexity"));
		complexityLimit = 80000;
		logt.info("getCoverageScale", "Complexity: " + (int) complexity
				+ " out of: " + complexityLimit + ", meters: "
				+ mapContainerNode.getWidth() * mapToMetric + ", square size: "
				+ squareSizeMetric);

		if (!verbose) {
			while (complexity > complexityLimit || squareSizeMetric < 0.4) {
				imageWidth /= 2;
				imageHeight /= 2;
				complexity /= 4;
				squareSizeMetric *= 2;
				logt.info("getCoverageScale", "# units: " + (int) complexity
						+ ", square size: " + squareSizeMetric + " meters.");
			}
		}
		imageWidth = (int) imageWidth;
		imageHeight = (int) imageHeight;
		logt.info("getCoverageScale", "canvas: (" + canvasWidth + ", "
				+ canvasHeight + "), image: (" + imageWidth + ", "
				+ imageHeight + ")  useA: " + useA);

		// Adjust squareSizeMetric for actual image width
		squareSizeMetric = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;

		short mapColors[][] = new short[(int) imageWidth][(int) imageHeight];
		short mapChannels[][] = new short[(int) imageWidth][(int) imageHeight];
		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);

		return squareSizeMetric;
	}

	private double getMinDistanceToPerimeter(double r) {
		double dtw = r / 2;
		dtw = (1 + Math.sqrt(3)) * r / 4;
		return dtw;
	}

	public int setPerimeterPenalties(MapContainerNode floor,
			short mapColors[][], short mapChannels[][], int apCount,
			double perimPenalties[][], double imageToMetric, boolean relax,
			double dtw, boolean initArea) {
		int candidateCount = 0;
		double mapToMetric = floor.getMapToMetric();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;
		for (double y = 0; y < imageHeight; y += 1) {
			double y_m = (y + 0.5) * imageToMetric;
			List<Double> edgesX = findEdgesX(floor.getPerimeter(), mapToMetric,
					y_m);
			int leftEdge = 0;
			boolean inside = floor.getPerimeter().size() == 0;
			for (double x = 0; x < imageWidth; x += 1) {
				double x_m = (x + 0.5) * imageToMetric;
				for (; leftEdge < edgesX.size() && edgesX.get(leftEdge) <= x_m; leftEdge++) {
					inside = !inside;
				}
				if (inside) {
					if (initArea) {
						mapColors[(int) x][(int) y] = -1;
					}
					boolean verbose = false;
					perimPenalties[(int) x][(int) y] = getPerimeterPenalty(
							floor, leftEdge, edgesX, x_m, y_m, relax, dtw,
							apCount, verbose);
					if (perimPenalties[(int) x][(int) y] == -2) {
						perimPenalties[(int) x][(int) y] = 0;
						mapColors[(int) x][(int) y] = -2;
					} else {
						candidateCount++;
					}
				} else {
					mapColors[(int) x][(int) y] = -2;
				}
				if (initArea) {
					mapChannels[(int) x][(int) y] = -1;
				}
			}
		}
		log.info_ln("# candidates: " + candidateCount);
		return candidateCount;
	}

	public void setWallPenalties(MapContainerNode mapContainerNode,
			short mapColors[][], short mapChannels[][],
			double wallPenalties[][], double imageToMetric, boolean relax) {
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;
		for (double y = 0; y < imageHeight; y += 1) {
			double y_m = (y + 0.5) * imageToMetric;
			for (double x = 0; x < imageWidth; x += 1) {
				if (mapColors[(int) x][(int) y] == -2) {
					continue;
				}
				double x_m = (x + 0.5) * imageToMetric;
				wallPenalties[(int) x][(int) y] = getWallPenalty(
						mapContainerNode, relax, x_m, y_m, false);
			}
		}
	}

	private int getNbrPlannedApCount(MapContainerNode floor,
			MapContainerNode higherFloor, MapContainerNode lowerFloor) {
		int apCount = floor.getPlannedAPs().size();
		if (higherFloor != null) {
			apCount += higherFloor.getPlannedAPs().size();
		}
		if (lowerFloor != null) {
			apCount += lowerFloor.getPlannedAPs().size();
		}
		return apCount;
	}

	public boolean findCandidateCoverage(MapContainerNode floor,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, PlannedAP plannedAP)
			throws Exception {
		double squareSizeMetric = getCoverageScale(floor, planConfig,
				canvasWidth, canvasHeight, useA, shadesPerColor, false);
		short mapColors[][] = floor.getMapColors();
		short mapChannels[][] = floor.getMapChannels();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;

		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double r = predictedMapSize(floor, planConfig, useA, rssiThreshold);
		double dtw = getMinDistanceToPerimeter(r);
		plannedAP.r = r;

		double mapToMetric = floor.getMapToMetric();
		WallLoss[] walls = getWallsMetric(floor.getWalls(), mapToMetric);

		Date start = new Date();
		double perimPenalties[][] = new double[(int) imageWidth][(int) imageHeight];
		int candidateCount = setPerimeterPenalties(floor, mapColors,
				mapChannels, floor.getPlannedAPs().size(), perimPenalties,
				squareSizeMetric, false, dtw, true);
		double wallPenalties[][] = new double[(int) imageWidth][(int) imageHeight];
		setWallPenalties(floor, mapColors, mapChannels, wallPenalties,
				squareSizeMetric, false);

		List<MapContainerNode> floors = null;
		MapContainerNode higherFloor = null, lowerFloor = null;
		if (floor.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			// Consider all floors in this building.
			floors = sortFloors(floor.getParentMap());
			for (MapContainerNode container : floors) {
				if (container.getId().equals(floor.getId())) {
					lowerFloor = container;
				} else if (lowerFloor == null) {
					higherFloor = container;
				} else {
					lowerFloor = container;
					break;
				}
			}
			if (lowerFloor.getId().equals(floor.getId())) {
				lowerFloor = null;
			}
			logt.info(
					"findCandidateCoverage",
					"Floor: "
							+ floor.getMapName()
							+ ", higher: "
							+ (higherFloor == null ? "<none>" : higherFloor
									.getMapName())
							+ ", lower: "
							+ (lowerFloor == null ? "<none>" : lowerFloor
									.getMapName()));
		}

		boolean found;
		int dotCount = 0;
		PlannedAP plannedAP2 = null;
		int apCount = getNbrPlannedApCount(floor, higherFloor, lowerFloor);
		if (apCount == 0) {
			found = findFirstCandidate(floor, perimPenalties, wallPenalties,
					dtw, plannedAP);
		} else {
			List<PlannedAP> aps = new ArrayList<PlannedAP>(
					floor.getPlannedAPs());
			setPlannedApRange(aps, getPathLossFactor(floor), useA,
					planConfig.getChannelWidth(), rssiThreshold);
			if (higherFloor != null) {
				setPlannedApRange(higherFloor.getPlannedAPs(), mfloorle, useA,
						planConfig.getChannelWidth(), rssiThreshold);
			}
			if (lowerFloor != null) {
				setPlannedApRange(lowerFloor.getPlannedAPs(), mfloorle, useA,
						planConfig.getChannelWidth(), rssiThreshold);
			}
			dotCount = findCoveredArea(floor, aps, useA, shadesPerColor,
					squareSizeMetric, candidateCount, r,
					planConfig.getChannelWidth());
			double percentage = dotCount / (double) candidateCount * 100;
			if (percentage > 90) {
				log.info_ln("Relax perim & wall dte min.");
				candidateCount = setPerimeterPenalties(floor, mapColors,
						mapChannels, floor.getPlannedAPs().size(),
						perimPenalties, squareSizeMetric, true, dtw, false);
				setWallPenalties(floor, mapColors, mapChannels, wallPenalties,
						squareSizeMetric, true);
			}
			plannedAP2 = createSimAp(planConfig, r);
			boolean verbose = false;
			found = findNextCandidate(floor, aps, walls, perimPenalties,
					wallPenalties, higherFloor, lowerFloor, plannedAP,
					plannedAP2, r, planConfig.getChannelWidth(), useA,
					rssiThreshold, verbose);
		}
		if (found) {
			int newDotCount = findApCoveredArea(floor, useA, shadesPerColor,
					squareSizeMetric, planConfig.getChannelWidth(), plannedAP,
					(short) -1);
			int maxDotCount = (int) (Math.PI * r * r / squareSizeMetric);
			double newPercentage = newDotCount / (double) candidateCount
					* 100.0;
			double percentage = (dotCount + newDotCount)
					/ (double) candidateCount * 100;

			if (plannedAP2 != null && newDotCount < maxDotCount / 2
					&& percentage > 85) {
				PlannedAP plannedAP1 = createSimAp(planConfig, r);
				plannedAP1.x = plannedAP.x;
				plannedAP1.y = plannedAP.y;
				int lookAhead = 10;
				newDotCount = findAlternateCandidate(floor, perimPenalties,
						wallPenalties, higherFloor, lowerFloor,
						squareSizeMetric, walls, lookAhead, useA,
						shadesPerColor, r, planConfig.getChannelWidth(),
						plannedAP, plannedAP1, plannedAP2, newDotCount,
						(int) (maxDotCount * 0.1), -1);
			}
			double apx = plannedAP.x * mapToMetric;
			double apy = plannedAP.y * mapToMetric;
			String fmt = "New AP (%1$.2f, %2$.2f) adds %3$d (%4$d remaining) dots or %5$.2f%% coverage, total %6$.2f%%.";
			log.info_ln(String.format(fmt, apx, apy, newDotCount,
					candidateCount - dotCount, newPercentage, percentage));
		}
		Date end = new Date();
		logt.info("findCandidateCoverage",
				"Auto: " + (end.getTime() - start.getTime()) + " ms.");
		return found;
	}

	private int findAlternateCandidate(MapContainerNode mapContainerNode,
			double perimPenalties[][], double wallPenalties[][],
			MapContainerNode higherFloor, MapContainerNode lowerFloor,
			double squareSizeMetric, WallLoss[] walls, int lookAhead,
			boolean useA, short shadesPerColor, double r, short channelWidth,
			PlannedAP plannedAP, PlannedAP plannedAP1, PlannedAP plannedAP2,
			int dotCount, int minDotCount, int maxDotCount) throws Exception {
		double mapToMetric = mapContainerNode.getMapToMetric();
		double apx = plannedAP1.x * mapToMetric;
		double apy = plannedAP1.y * mapToMetric;
		String fmt = "First solution (%1$.2f, %2$.2f) would add %3$d dots, max is %4$d.";
		log.info_ln(String.format(fmt, apx, apy, dotCount, maxDotCount));
		if (dotCount > maxDotCount) {
			plannedAP.x = plannedAP1.x;
			plannedAP.y = plannedAP1.y;
			maxDotCount = dotCount;
		}
		if (plannedAP2.x != -1) {
			int secondDotCount = findApCoveredArea(mapContainerNode, useA,
					shadesPerColor, squareSizeMetric, channelWidth, plannedAP2,
					(short) -1);
			apx = plannedAP2.x * mapToMetric;
			apy = plannedAP2.y * mapToMetric;
			fmt = "Second solution (%1$.2f, %2$.2f) would add %3$d dots.";
			log.info_ln(String.format(fmt, apx, apy, secondDotCount));
			if (secondDotCount > maxDotCount) {
				maxDotCount = secondDotCount;
				plannedAP.x = plannedAP2.x;
				plannedAP.y = plannedAP2.y;
			}
		}
		if (maxDotCount < minDotCount && lookAhead > 0) {
			/*
			 * Look for bigger fish ...
			 */
			List<PlannedAP> aps = new ArrayList<PlannedAP>(
					mapContainerNode.getPlannedAPs());
			findApCoveredArea(mapContainerNode, useA, shadesPerColor,
					squareSizeMetric, channelWidth, plannedAP1,
					(short) aps.size());
			int rssiThreshold = -(shadesPerColor + 35 - 1);
			if (findNextCandidate(mapContainerNode, aps, walls, perimPenalties,
					wallPenalties, higherFloor, lowerFloor, plannedAP1,
					plannedAP2, r, channelWidth, useA, rssiThreshold, false)) {
				int newDotCount = findApCoveredArea(mapContainerNode, useA,
						shadesPerColor, squareSizeMetric, channelWidth,
						plannedAP1, (short) -1);
				return findAlternateCandidate(mapContainerNode, perimPenalties,
						wallPenalties, higherFloor, lowerFloor,
						squareSizeMetric, walls, lookAhead - 1, useA,
						shadesPerColor, r, channelWidth, plannedAP, plannedAP1,
						plannedAP2, newDotCount, minDotCount, maxDotCount);
			}
		}
		return maxDotCount;
	}

	private boolean findFirstCandidate(MapContainerNode mapContainerNode,
			double perimPenalties[][], double wallPenalties[][], double dtw,
			PlannedAP plannedAP) {
		short mapColors[][] = mapContainerNode.getMapColors();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;
		double mapToMetric = mapContainerNode.getMapToMetric();
		double imageToMetric = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;
		double apx = 0, apy = 0;
		double minPenalty = Double.MAX_VALUE;
		for (double y = 0; y < imageHeight; y += 1) {
			double y_m = (y + 0.5) * imageToMetric;
			for (double x = 0; x < imageWidth; x += 1) {
				if (mapColors[(int) x][(int) y] == -2) {
					continue;
				}
				double x_m = (x + 0.5) * imageToMetric;

				double wallPenalty = wallPenalties[(int) x][(int) y];
				if (wallPenalty < 0) {
					continue;
				}

				double perimeterPenalty = perimPenalties[(int) x][(int) y];
				if (perimeterPenalty < 0) {
					continue;
				}
				if (perimeterPenalty < 1) {
					String fmt = "Good first candidate at (%1$.2f, %2$.2f), penalty: %3$.2f";
					log.info_ln(String.format(fmt, x_m, y_m, perimeterPenalty));
					if (x == 0 && y == 0) {
						if (mapContainerNode.getWalls().size() > 0) {
							dtw /= 3;
						}
						plannedAP.x = dtw;
						plannedAP.y = dtw;
						if (dtw * 2 > mapContainerNode.getActualWidthMetric()) {
							plannedAP.x = mapContainerNode
									.getActualWidthMetric() / 2;
						}
						if (dtw * 2 > mapContainerNode.getActualHeightMetric()) {
							plannedAP.y = mapContainerNode
									.getActualHeightMetric() / 2;
						}
					} else {
						plannedAP.x = x_m / mapToMetric;
						plannedAP.y = y_m / mapToMetric;
					}
					return true;
				}
				if (perimeterPenalty < minPenalty) {
					minPenalty = perimeterPenalty;
					apx = x_m;
					apy = y_m;
				}
			}
		}
		logt.info("findFirstCandidate", String.format(
				"Best candidate at (%1$.2f, %2$.2f), penalty: %3$.2f", apx,
				apy, minPenalty));
		plannedAP.x = apx / mapToMetric;
		plannedAP.y = apy / mapToMetric;
		return true;
	}

	private boolean findNextCandidate(MapContainerNode floor,
			List<PlannedAP> aps, WallLoss[] walls, double perimPenalties[][],
			double wallPenalties[][], MapContainerNode higherFloor,
			MapContainerNode lowerFloor, PlannedAP plannedAP1,
			PlannedAP plannedAP2, double r, short channelWidth, boolean useA,
			int rssiThreshold, boolean verbose) {
		short mapColors[][] = floor.getMapColors();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;
		double mapToMetric = floor.getMapToMetric();
		double imageToMetric = floor.getWidth() * mapToMetric / imageWidth;
		double higherApElevation = floor.getApElevationMetric() - 1;
		if (higherFloor != null) {
			higherApElevation += higherFloor.getApElevationMetric();
			if (higherApElevation < elevationMargin) {
				higherApElevation = elevationMargin;
			}
		}
		double lowerApElevation = 1;
		double originX = floor.getOriginXmetric();
		double originY = floor.getOriginYmetric();
		int plannedApPower = useA ? plannedAP1.wifi1Power
				: plannedAP1.wifi0Power;
		channelWidth = adjustChannelWidth(channelWidth, plannedAP1.apModel,
				useA);
		double channelWidthLoss = channelWidthLoss(useA, channelWidth);
		double plf = getPathLossFactor(floor);
		double dtcMin1 = Double.MAX_VALUE;
		double dtcMin2 = Double.MAX_VALUE;
		double dtcMin1PenaltyCount = 0;
		double apx1 = -1, apy1 = -1, apx2 = -1, apy2 = -1;
		for (double y = 0; y < imageHeight; y += 1) {
			double y_m = (y + 0.5) * imageToMetric;
			for (double x = 0; x < imageWidth; x += 1) {
				if (mapColors[(int) x][(int) y] == -2) {
					continue;
				}
				if (mapColors[(int) x][(int) y] >= 0) {
					continue; // Covered
				}

				double x_m = (x + 0.5) * imageToMetric;

				double wallPenalty = wallPenalties[(int) x][(int) y];
				if (wallPenalty < 0) {
					if (verbose) {
						log.info_ln("Candidate (" + x_m + ", " + y_m
								+ " is too close to interior wall.");
					}
					continue;
				}

				double perimeterPenalty = perimPenalties[(int) x][(int) y];
				if (perimeterPenalty < 0) {
					if (verbose) {
						log.info_ln("Candidate (" + x_m + ", " + y_m
								+ " is too close to perimeter wall.");
					}
					continue;
				}

				double dbeMin1 = Double.MAX_VALUE;
				double dbeMin2 = Double.MAX_VALUE;
				int dbeInd1 = -1, dbeInd2 = -1;
				if (verbose) {
					log.info_non(String.format("(%1$.2f, %2$.2f)", x_m, y_m));
				}
				double penalty = 0;
				double penaltyCount = 0;
				boolean tooClose[] = new boolean[aps.size()];
				for (int i = 0; i < aps.size(); i++) {
					PlannedAP nbr = aps.get(i);
					double d = Math.sqrt(3) * (r + nbr.r) / 2;
					double nbrx = nbr.x * mapToMetric;
					double nbry = nbr.y * mapToMetric;
					double dx_m = nbrx - x_m;
					double dy_m = nbry - y_m;
					double dac = Math.sqrt(dx_m * dx_m + dy_m * dy_m);
					double wallLoss = 0;
					if (dac > 1 && dac < 2 * r) {
						if (walls != null) {
							wallLoss += Search.wl(walls, x_m, y_m, nbrx, nbry,
									false);
						}
						if (wallLoss > 0) {
							double new_r = predictedMapSize(plannedApPower,
									plannedAP1.apModel, plf, useA,
									rssiThreshold, wallLoss + channelWidthLoss);
							if (verbose) {
								log.info_ln("Default r: " + r
										+ ", adjusted r: " + new_r);
							}
							d = Math.sqrt(3) * (new_r + nbr.r) / 2;
						}
					}
					// Distance from this AP to candidate position
					// d is desired distance
					// dac is actual distance
					if (dac < d) {
						// This one is too close
						tooClose[i] = true;
						penalty += d - dac;
						penaltyCount++;
					}
					// Distance between coverage edges
					// Find the 2 APs that are closest to
					// the new candidate.
					double dbe = Math.abs(dac - d);
					if (dbe < dbeMin1) {
						if (dbeMin1 < dbeMin2) {
							dbeMin2 = dbeMin1;
							dbeInd2 = dbeInd1;
						}
						dbeMin1 = dbe;
						dbeInd1 = i;
					} else if (dbe < dbeMin2) {
						dbeMin2 = dbe;
						dbeInd2 = i;
					}
					if (verbose) {
						log.info_non(String.format(", %1$.2f", dac));
					}
				}
				if (verbose) {
					log.info_ln("");
				}
				double highestPenalty = 0;
				double lowestPenalty = Double.MAX_VALUE;
				if (higherFloor != null) {
					double higherToMetric = higherFloor.getMapToMetric();
					double alignX = higherFloor.getOriginXmetric() - originX;
					double alignY = higherFloor.getOriginYmetric() - originY;
					double new_r = predictedMapSize(plannedApPower,
							plannedAP1.apModel, mfloorle, useA, rssiThreshold,
							higherFloor.getFloorLoss());
					for (PlannedAP nbr : higherFloor.getPlannedAPs()) {
						double nbrx = nbr.x * higherToMetric;
						double nbry = nbr.y * higherToMetric;
						double dx_m = nbrx - x_m + alignX;
						double dy_m = nbry - y_m + alignY;
						double dac = Math.sqrt(dx_m * dx_m + dy_m * dy_m
								+ higherApElevation * higherApElevation);
						double d = Math.sqrt(3) * (new_r + nbr.r) / 2;
						// Distance from this AP to candidate position
						// d is desired distance
						// dac is actual distance
						if (dac < d) {
							// This one is too close
							double newPenalty = d - dac;
							if (newPenalty < lowestPenalty) {
								lowestPenalty = newPenalty;
							}
							if (newPenalty > highestPenalty) {
								highestPenalty = newPenalty;
							}
							penalty += newPenalty;
							penaltyCount++;
						}
					}
				}
				if (lowerFloor != null) {
					double lowerToMetric = lowerFloor.getMapToMetric();
					double alignX = lowerFloor.getOriginXmetric() - originX;
					double alignY = lowerFloor.getOriginYmetric() - originY;
					double loss = floor.getFloorLoss()
							+ getAverageShadow(plannedAP1.apModel, useA);
					double new_r = predictedMapSize(plannedApPower,
							plannedAP1.apModel, mfloorle, useA, rssiThreshold,
							loss);
					for (PlannedAP nbr : lowerFloor.getPlannedAPs()) {
						double nbrx = nbr.x * lowerToMetric;
						double nbry = nbr.y * lowerToMetric;
						double dx_m = nbrx - x_m + alignX;
						double dy_m = nbry - y_m + alignY;
						double dac = Math.sqrt(dx_m * dx_m + dy_m * dy_m
								+ lowerApElevation * lowerApElevation);
						double d = Math.sqrt(3) * (new_r + nbr.r) / 2;
						// Distance from this AP to candidate position
						// d is desired distance
						// dac is actual distance
						if (dac < d) {
							// This one is too close
							double newPenalty = d - dac;
							if (newPenalty < lowestPenalty) {
								lowestPenalty = newPenalty;
							}
							if (newPenalty > highestPenalty) {
								highestPenalty = newPenalty;
							}
							penalty += newPenalty;
							penaltyCount++;
						}
					}
				}
				double dtc1 = perimeterPenalty;
				double dtc2 = perimeterPenalty;
				if (dbeInd1 < 0) {
					// Not a single AP yet, just look for min
					// perimeter OR too close penalty
					if (penaltyCount > 1) {
						// penaltyCount == 1 will place an AP directly above
						if (penaltyCount == 2) {
							// Use average as metric
							dtc1 = penalty / penaltyCount;
						} else {
							// Use difference between min and max penalty
							dtc1 = highestPenalty - lowestPenalty;
						}
						// if ((penaltyCount > dtcMin1PenaltyCount)
						// || dtcMin1PenaltyCount == 0) {
						if ((penaltyCount > dtcMin1PenaltyCount && penaltyCount < 4)
								|| dtcMin1PenaltyCount == 0
								|| (dtcMin1PenaltyCount > 3 && penaltyCount < dtcMin1PenaltyCount)) {
							// give preference to 2 or 3 penalty count
							// make sure a solution is found even if none exist
							// with penaltyCount < 4
							log.info_ln("New high penalty count."
									+ penaltyCount);
							dtcMin1PenaltyCount = penaltyCount;
							dtcMin1 = dtc1;
							apx1 = x_m;
							apy1 = y_m;
						} else if (penaltyCount == dtcMin1PenaltyCount) {
							if (dtc1 < dtcMin1) {
								dtcMin1 = dtc1;
								apx1 = x_m;
								apy1 = y_m;
							}
						}
					} else if (dtcMin1PenaltyCount == 0 && dtc1 < dtcMin1) {
						dtcMin1 = dtc1;
						apx1 = x_m;
						apy1 = y_m;
					}
				} else {
					if (!tooClose[dbeInd1]) {
						// if too close, already included in penalty
						penalty += dbeMin1;
						penaltyCount++;
					}
					// dtc2 only includes 1 close nbr and a perimeter penalty
					dtc2 += penalty / penaltyCount;
					if (aps.size() > 1) {
						// include second closest
						if (!tooClose[dbeInd2]) {
							// if too close, already included in penalty
							penalty += dbeMin2;
							penaltyCount++;
						}
					}
					// dtc1 includes 2 close nbrs and possibly no perimeter
					// penalty
					dtc1 += penalty / penaltyCount;
					String newMin = "";
					if (dtc1 < dtcMin1) {
						dtcMin1 = dtc1;
						newMin = ", new dtc";
						apx1 = x_m;
						apy1 = y_m;
					}
					if (perimeterPenalty > 0 && dtc2 < dtcMin2) {
						dtcMin2 = dtc2;
						apx2 = x_m;
						apy2 = y_m;
					}
					if (verbose) {
						String fmt = "(%1$.2f, %2$.2f), min dbe: %3$.2f, %4$.2f, total: %5$.2f%6$s.";
						log.info_ln(String.format(fmt, x_m, y_m, dbeMin1,
								dbeMin2, dtc1, newMin));
					}
				}
			}
		}

		if (apx1 < 0) {
			return false;
		} else {
			plannedAP1.x = apx1 / mapToMetric;
			plannedAP1.y = apy1 / mapToMetric;
			if (plannedAP2 != null) {
				if (apx2 < 0) {
					plannedAP2.x = -1;
				} else if (apx1 == apx2 && apy1 == apy2) {
					plannedAP2.x = -1;
				} else {
					plannedAP2.x = apx2 / mapToMetric;
					plannedAP2.y = apy2 / mapToMetric;
				}
			}
			return true;
		}
	}

	public List<PlannedAP> autoSimAps(MapContainerNode floor,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, double target) throws Exception {
		double squareSizeMetric = getCoverageScale(floor, planConfig,
				canvasWidth, canvasHeight, useA, shadesPerColor, false);
		short mapColors[][] = floor.getMapColors();
		short mapChannels[][] = floor.getMapChannels();
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;

		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double r = predictedMapSize(floor, planConfig, useA, rssiThreshold);
		double dtw = getMinDistanceToPerimeter(r);

		List<MapContainerNode> floors = null;
		MapContainerNode higherFloor = null, lowerFloor = null;
		if (floor.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			// Consider all floors in this building.
			floors = sortFloors(floor.getParentMap());
			for (MapContainerNode container : floors) {
				if (container.getId().equals(floor.getId())) {
					lowerFloor = container;
				} else if (lowerFloor == null) {
					higherFloor = container;
				} else {
					lowerFloor = container;
					break;
				}
			}
			if (lowerFloor.getId().equals(floor.getId())) {
				lowerFloor = null;
			}
			logt.info(
					"autoSimAps",
					"Floor: "
							+ floor.getMapName()
							+ ", higher: "
							+ (higherFloor == null ? "<none>" : higherFloor
									.getMapName())
							+ ", lower: "
							+ (lowerFloor == null ? "<none>" : lowerFloor
									.getMapName()));
		}

		List<PlannedAP> aps = new ArrayList<PlannedAP>(floor.getPlannedAPs());
		setPlannedApRange(aps, getPathLossFactor(floor), useA,
				planConfig.getChannelWidth(), rssiThreshold);
		if (higherFloor != null) {
			setPlannedApRange(higherFloor.getPlannedAPs(), mfloorle, useA,
					planConfig.getChannelWidth(), rssiThreshold);
		}
		for (PlannedAP ap : floor.getPlannedAPs()) {
			ap.setSelected(true); // Don't recreate these
		}

		Date start = new Date();
		double perimPenalties[][] = new double[(int) imageWidth][(int) imageHeight];
		int candidateCount = setPerimeterPenalties(floor, mapColors,
				mapChannels, aps.size(), perimPenalties, squareSizeMetric,
				false, dtw, true);
		double wallPenalties[][] = new double[(int) imageWidth][(int) imageHeight];
		setWallPenalties(floor, mapColors, mapChannels, wallPenalties,
				squareSizeMetric, false);

		int apCount = getNbrPlannedApCount(floor, higherFloor, lowerFloor);
		if (apCount == 0) {
			PlannedAP plannedAP = createSimAp(planConfig, r);
			if (findFirstCandidate(floor, perimPenalties, wallPenalties, dtw,
					plannedAP)) {
				aps.add(plannedAP);
			} else {
				return aps;
			}
		}
		if (aps.size() > 0) {
			candidateCount = setPerimeterPenalties(floor, mapColors,
					mapChannels, aps.size(), perimPenalties, squareSizeMetric,
					false, dtw, true);
		}
		WallLoss[] walls = getWallsMetric(floor.getWalls(),
				floor.getMapToMetric());

		double percentage = autoPlaceAps(floor, planConfig, useA,
				shadesPerColor, perimPenalties, wallPenalties, walls,
				higherFloor, lowerFloor, aps, maxAutoPlace, squareSizeMetric,
				candidateCount);

		if (percentage < 100 && aps.size() < maxAutoPlace) {
			candidateCount = setPerimeterPenalties(floor, mapColors,
					mapChannels, aps.size(), perimPenalties, squareSizeMetric,
					true, dtw, true);
			setWallPenalties(floor, mapColors, mapChannels, wallPenalties,
					squareSizeMetric, true);
			/*
			 * Try again with lesser restrictions
			 */
			percentage = autoPlaceAps(floor, planConfig, useA, shadesPerColor,
					perimPenalties, wallPenalties, walls, higherFloor,
					lowerFloor, aps, maxAutoPlace, squareSizeMetric,
					candidateCount);
		}
		Date end = new Date();
		log.info_ln("Auto placement of " + aps.size() + " aps: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return aps;
	}

	private double autoPlaceAps(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, boolean useA, short shadesPerColor,
			double perimPenalties[][], double wallPenalties[][],
			WallLoss[] walls, MapContainerNode higherFloor,
			MapContainerNode lowerFloor, List<PlannedAP> aps, int maxApCount,
			double squareSizeMetric, int candidateCount) throws Exception {
		short mapColors[][] = mapContainerNode.getMapColors();
		short mapChannels[][] = mapContainerNode.getMapChannels();
		double imageWidth = mapColors.length;
		int rssiThreshold = -(shadesPerColor + 35 - 1);
		double mapToMetric = mapContainerNode.getMapToMetric();
		double imageToMetric = mapContainerNode.getWidth() * mapToMetric
				/ imageWidth;
		double r = predictedMapSize(mapContainerNode, planConfig, useA,
				rssiThreshold);
		double dtw = getMinDistanceToPerimeter(r);
		int maxDotCount = (int) (Math.PI * r * r / squareSizeMetric);
		int dotCount = findCoveredArea(mapContainerNode, aps, useA,
				shadesPerColor, squareSizeMetric, candidateCount, r,
				planConfig.getChannelWidth());
		double percentage = dotCount / (double) candidateCount * 100;
		while (percentage < 100 && aps.size() < maxApCount) {
			PlannedAP plannedAP = createSimAp(planConfig, r);
			if (findNextCandidate(mapContainerNode, aps, walls, perimPenalties,
					wallPenalties, higherFloor, lowerFloor, plannedAP, null, r,
					planConfig.getChannelWidth(), useA, rssiThreshold, false)) {
				int newDotCount = findApCoveredArea(mapContainerNode, useA,
						shadesPerColor, squareSizeMetric,
						planConfig.getChannelWidth(), plannedAP,
						(short) aps.size());
				String rejected = ".";
				log.info_ln("newDotCount: " + newDotCount + ", maxDotCount: "
						+ maxDotCount + ", threshold: " + maxDotCount * 0.08);
				double newPercentage = newDotCount / (double) candidateCount
						* 100.0;
				if (newDotCount < maxDotCount * 0.08 && newPercentage < 2) {
					rejected = ", rejected.";
					plannedAP.setSelected(true);
				}
				dotCount += newDotCount;
				percentage = dotCount / (double) candidateCount * 100;
				double apx = plannedAP.x * mapToMetric;
				double apy = plannedAP.y * mapToMetric;
				String fmt = "New AP (%1$.2f, %2$.2f) adds %3$d (%4$d remaining) dots or %5$.2f%% coverage, total %6$.2f%%";
				log.info_ln(String.format(fmt, apx, apy, newDotCount,
						candidateCount - dotCount, newPercentage, percentage)
						+ rejected);
				aps.add(plannedAP);
				if (aps.size() < 3) {
					// Reset perimeter penalties
					candidateCount = setPerimeterPenalties(mapContainerNode,
							mapColors, mapChannels, aps.size(), perimPenalties,
							imageToMetric, false, dtw, false);
				}
				if (newDotCount == 0) {
					log.info_ln(String.format(
							"Candidate (%1$.2f, %2$.2f) has 0 dot count.", apx,
							apy));
					break;
				}
			} else {
				log.info_ln("Remaining gaps are too small.");
				break;
			}
		}
		return percentage;
	}

	private void setPlannedApRange(Collection<PlannedAP> aps, double plf,
			boolean useA, short channelWidth, double rssiThreshold)
			throws Exception {
		for (PlannedAP ap : aps) {
			int power = useA ? ap.wifi1Power : ap.wifi0Power;
			short adjustedChannelWidth = adjustChannelWidth(channelWidth,
					ap.apModel, useA);
			ap.r = predictedMapSize(power, ap.apModel, plf, useA,
					rssiThreshold, channelWidthLoss(useA, adjustedChannelWidth));
		}
	}

	private static double getWallPenalty(MapContainerNode mapContainerNode,
			boolean relax, double cx, double cy, boolean verbose) {
		double dteMin = 3;
		if (relax) {
			dteMin = 1;
		}
		double mapToMetric = mapContainerNode.getMapToMetric();
		for (Wall wall : mapContainerNode.getWalls()) {
			if (wall.getType() == 1002) {
				// Cubicle wall
				continue;
			}
			double x1 = wall.getX1() * mapToMetric;
			double x2 = wall.getX2() * mapToMetric;
			double y1 = wall.getY1() * mapToMetric;
			double y2 = wall.getY2() * mapToMetric;
			Double xi = findIntersectionX(x1, y1, x2, y2, cy, null);
			if (xi != null) {
				double dte = Math.abs(cx - xi);
				if (dte < dteMin) {
					if (verbose) {
						String fmt = "Wall (%1$.2f, %2$.2f) - (%3$.2f, %4$.2f) is too close in X direction to (%5$.2f, %6$.2f) at %7$.2f, dte: %8$.2f.";
						log.info_ln(String.format(fmt, x1, y1, x2, y2, cx, cy,
								xi, dte));
					}
					return -1;
				}
			}
			Double yi = findIntersectionY(x1, y1, x2, y2, cx, null);
			if (yi != null) {
				double dte = Math.abs(cy - yi);
				if (dte < dteMin) {
					if (verbose) {
						String fmt = "Wall (%1$.2f, %2$.2f) - (%3$.2f, %4$.2f) is too close in Y direction to (%5$.2f, %6$.2f) at %7$.2f, dte: %8$.2f.";
						log.info_ln(String.format(fmt, x1, y1, x2, y2, cx, cy,
								yi, dte));
					}
					return -1;
				}
			}
		}
		return 0;
	}

	private static double getPerimeterPenalty(
			MapContainerNode mapContainerNode, int leftEdge,
			List<Double> edgesX, double cx, double cy, boolean relax,
			double dtw, int apCount, boolean verbose) {
		double penalty = 0;
		double penaltyCount = 0;
		double dteMin = 3, dteHardMin = 1;
		if (relax) {
			dteMin = 1;
			dteHardMin = 1;
		}
		double mapToMetric = mapContainerNode.getMapToMetric();
		double dteXY = 0;
		double extraPenalty = 0;
		if (leftEdge > 0 && leftEdge < edgesX.size()) {
			double edgeX1 = edgesX.get(leftEdge - 1);
			double edgeX2 = edgesX.get(leftEdge);
			if (verbose) {
				log.info_ln(String.format(
						"Horizontal edges (%1$.2f => %2$.2f)", edgeX1, edgeX2));
			}
			double leftPenalty = 0;
			double dteX1 = cx - edgeX1;
			double dteX2 = edgeX2 - cx;
			if (dteX1 < dteHardMin || dteX2 < dteHardMin) {
				if (verbose) {
					log.info_ln("Hard X limit reached: " + dteX1 + " or "
							+ dteX2);
				}
				if (apCount > 0) {
					return -1;
				}
			}
			if (dteX1 < dtw) {
				penaltyCount++;
				leftPenalty = dtw - dteX1;
				if (dteX1 < dteMin || dteX2 < dteMin) {
					if (dteX2 < dtw) {
						if (verbose) {
							log.info_ln("Too close both to left and right edge (1)");
						}
						if (dteX2 < dteX1) {
							leftPenalty = dtw - dteX2;
						}
					} else {
						return -1;
					}
				}
				if (verbose) {
					log.info_ln("Candidate is too close to left edge, penalty: "
							+ leftPenalty);
				}
			}
			double rightPenalty = 0;
			if (dteX2 < dtw) {
				penaltyCount++;
				rightPenalty = dtw - dteX2;
				if (dteX1 < dteMin || dteX2 < dteMin) {
					if (dteX1 < dtw) {
						if (verbose) {
							log.info_ln("Too close both to left and right edge (2)");
						}
						if (dteX1 < dteX2) {
							rightPenalty = dtw - dteX1;
						}
					} else {
						return -1;
					}
				}
				if (verbose) {
					log.info_ln("Candidate is too close to right edge, penalty: "
							+ rightPenalty);
				}
			}
			penalty += leftPenalty + rightPenalty;
			if (apCount == 0 && penalty == 0) {
				log.debug("Need to add a penalty for closest X edge.");
				if (dteX1 < dteX2) {
					extraPenalty = dteX1 - dtw;
				} else {
					extraPenalty = dteX2 - dtw;
				}
			}
			if (apCount == 1) {
				dteXY = Math.min(dteX1, dteX2);
			}
		}
		List<Double> edgesY = findEdgesY(mapContainerNode.getPerimeter(),
				mapToMetric, cx);
		int topEdge = 0;
		if (mapContainerNode.getPerimeter().size() > 0) {
			boolean inside = false;
			while (topEdge < edgesY.size() && edgesY.get(topEdge) <= cy) {
				inside = !inside;
				topEdge++;
			}
			if (!inside) {
				// still outside
				return -2;
			}
		}
		if (topEdge > 0 && topEdge < edgesY.size()) {
			double edgeY1 = edgesY.get(topEdge - 1);
			double edgeY2 = edgesY.get(topEdge);
			if (verbose) {
				log.info_ln(String.format("Vertical edges (%1$.2f => %2$.2f)",
						edgeY1, edgeY2));
			}
			double topPenalty = 0;
			double dteY1 = cy - edgeY1;
			double dteY2 = edgeY2 - cy;
			if (dteY1 < dteHardMin || dteY2 < dteHardMin) {
				if (verbose) {
					log.info_ln("Hard Y limit reached: " + dteY1 + " or "
							+ dteY2);
				}
				if (apCount > 0) {
					return -1;
				}
			}
			if (dteY1 < dtw) {
				penaltyCount++;
				topPenalty = dtw - dteY1;
				if (dteY1 < dteMin || dteY2 < dteMin) {
					if (dteY2 < dtw) {
						if (verbose) {
							log.info_ln("Too close both to top and bottom edge (1)");
						}
						if (dteY2 < dteY1) {
							topPenalty = dtw - dteY2;
						}
					} else {
						return -1;
					}
				}
				if (verbose) {
					log.info_ln("Candidate is too close to top edge, penalty: "
							+ topPenalty);
				}
			}
			double bottomPenalty = 0;
			if (dteY2 < dtw) {
				penaltyCount++;
				bottomPenalty = dtw - dteY2;
				if (dteY1 < dteMin || dteY2 < dteMin) {
					if (dteY1 < dtw) {
						if (verbose) {
							log.info_ln("Too close both to top and bottom edge (2)");
						}
						if (dteY1 < dteY2) {
							bottomPenalty = dtw - dteY1;
						}
					} else {
						return -1;
					}
				}
				if (verbose) {
					log.info_ln("Candidate is too close to bottom edge, penalty: "
							+ bottomPenalty);
				}
			}
			penalty += topPenalty + bottomPenalty;
			if (apCount == 0 && topPenalty + bottomPenalty == 0) {
				log.debug("Need to add a penalty for closest Y edge.");
				if (dteY1 < dteY2) {
					extraPenalty += dteY1 - dtw;
				} else {
					extraPenalty += dteY2 - dtw;
				}
			}
			if (apCount == 1) {
				if (dteXY == 0) {
					dteXY = Math.min(dteY1, dteY2);
				} else {
					dteXY = Math.min(dteXY, Math.min(dteY1, dteY2));
				}
			}
		}
		if (penaltyCount > 0) {
			penalty = penalty / penaltyCount;
		}
		penalty += extraPenalty;
		if (dteXY > 0 && dteXY > dtw) {
			penalty += dteXY - dtw;
		}
		return penalty;
	}

	private static PlannedAP createSimAp(PlanToolConfig planConfig, double r) {
		PlannedAP ap = new PlannedAP();
		ap.apModel = planConfig.getDefaultApType();
		ap.countryCode = planConfig.getCountryCode();
		ap.wifi0Channel = (short) AhInterface.CHANNEL_BG_AUTO;
		ap.wifi1Channel = (short) AhInterface.CHANNEL_A_AUTO;
		ap.wifi0Power = (short) planConfig.getWifi0Power();
		ap.wifi1Power = (short) planConfig.getWifi1Power();
		ap.wifi0Enabled = planConfig.isWifi0Enabled();
		ap.wifi1Enabled = planConfig.isWifi1Enabled();
		ap.r = r;
		return ap;
	}

	private static List<Double> findEdgesX(List<Vertex> perimeter,
			double mapToMetric, double cy) {
		List<Double> edges = new ArrayList<Double>();
		if (perimeter.size() == 0) {
			return edges;
		}
		Vertex vertex = perimeter.get(0);
		double x1 = vertex.getX() * mapToMetric;
		double y1 = vertex.getY() * mapToMetric;
		double first_x = x1;
		double first_y = y1;
		int perimId = vertex.getId();
		for (int i = 1; i < perimeter.size(); i++) {
			vertex = perimeter.get(i);
			double x2 = vertex.getX() * mapToMetric;
			double y2 = vertex.getY() * mapToMetric;
			if (vertex.getId() != perimId) {
				findIntersectionX(x1, y1, first_x, first_y, cy, edges);
				first_x = x2;
				first_y = y2;
				perimId = vertex.getId();
			} else {
				findIntersectionX(x1, y1, x2, y2, cy, edges);
			}
			x1 = x2;
			y1 = y2;
		}
		findIntersectionX(x1, y1, first_x, first_y, cy, edges);
		Collections.sort(edges, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});
		return edges;
	}

	private static Double findIntersectionX(double x1, double y1, double x2,
			double y2, double cy, List<Double> edges) {
		double tol = 1e-6;
		double dy = y2 - y1;
		if (Math.abs(dy) < tol) {
			// This wall is parallel to x axis.
			return null;
		}
		if (dy > 0) {
			if (cy < y1 || cy >= y2) {
				// Candidate is outside the Y boundaries.
				return null;
			}
		} else {
			if (cy < y2 || cy >= y1) {
				// Candidate is outside the Y boundaries.
				return null;
			}
		}
		Double xi = llix(x1, y1, x2, y2, cy);
		if (edges == null) {
			return xi;
		} else {
			edges.add(xi);
			return null;
		}
	}

	private static double llix(double x1, double y1, double x2, double y2,
			double cy) {
		// Line between l1p1 and l1p2
		double a1 = y2 - y1;
		double b1 = x1 - x2;
		double c1 = a1 * x1 + b1 * y1;
		// Intersection point
		double x = (c1 - b1 * cy) / a1;
		return x;
	}

	private static List<Double> findEdgesY(List<Vertex> perimeter,
			double mapToMetric, double cx) {
		List<Double> edges = new ArrayList<Double>();
		if (perimeter.size() == 0) {
			return edges;
		}
		Vertex vertex = perimeter.get(0);
		double x1 = vertex.getX() * mapToMetric;
		double y1 = vertex.getY() * mapToMetric;
		double first_x = x1;
		double first_y = y1;
		int perimId = vertex.getId();
		for (int i = 1; i < perimeter.size(); i++) {
			vertex = perimeter.get(i);
			double x2 = vertex.getX() * mapToMetric;
			double y2 = vertex.getY() * mapToMetric;
			if (vertex.getId() != perimId) {
				findIntersectionY(x1, y1, first_x, first_y, cx, edges);
				first_x = x2;
				first_y = y2;
				perimId = vertex.getId();
			} else {
				findIntersectionY(x1, y1, x2, y2, cx, edges);
			}
			x1 = x2;
			y1 = y2;
		}
		findIntersectionY(x1, y1, first_x, first_y, cx, edges);
		Collections.sort(edges, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});
		return edges;
	}

	private static Double findIntersectionY(double x1, double y1, double x2,
			double y2, double cx, List<Double> edges) {
		double tol = 1e-6;
		double dx = x2 - x1;
		if (Math.abs(dx) < tol) {
			// This wall is parallel to y axis.
			return null;
		}
		if (dx > 0) {
			if (cx < x1 || cx >= x2) {
				// Candidate is outside the X boundaries.
				return null;
			}
		} else {
			if (cx < x2 || cx >= x1) {
				// Candidate is outside the X boundaries.
				return null;
			}
		}
		Double yi = lliy(x1, y1, x2, y2, cx);
		if (edges == null) {
			return yi;
		} else {
			edges.add(yi);
			return null;
		}
	}

	private static double lliy(double x1, double y1, double x2, double y2,
			double cx) {
		// Line between l1p1 and l1p2
		double a1 = y2 - y1;
		double b1 = x1 - x2;
		double c1 = a1 * x1 + b1 * y1;

		// Intersection point
		double y = (c1 - a1 * cx) / b1;
		return y;
	}

	private static final String MAPS_PATH_PREFIX = System.getenv("HM_ROOT")
			+ "/domains/";

	public BufferedImage createFloorImage(MapContainerNode floor, double scale,
			int floorWidth, int floorHeight, Map<Long, Integer> channelMap,
			Map<Long, Integer> colorMap, int borderX, int borderY,
			double gridSize) throws Exception {
		BufferedImage image = new BufferedImage(floorWidth + borderX + 1,
				floorHeight + borderY + 1, BufferedImage.TYPE_INT_ARGB);
		if (floor == null) {
			return image;
		}
		Font font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath
				+ "arialbd.ttf"));
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		if (floor.getActualWidthMetric() == 0) {
			g2.setColor(new Color(255, 255, 255));
			g2.fillRect(borderX, 0, floorWidth + 1, borderY);
			g2.fillRect(0, 0, borderX, borderY + floorHeight + 1);
			g2.setColor(new Color(120, 120, 120));
			g2.drawLine(0, borderY, floorWidth + borderX, borderY);
			g2.drawLine(borderX, 0, borderX, floorHeight + borderY);
			g2.setColor(new Color(255, 255, 204));
			g2.fillRect(borderX + 2, borderY + 2, 162, 25);
			g2.setColor(new Color(0, 51, 102));
			// g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
			g2.setFont(font.deriveFont(Font.BOLD, 12));
			g2.drawString("Please size this floor plan.", borderX + 8,
					borderY + 19);
			return image;
		}
		double screenWidth = scale * floor.getActualWidthMetric();
		double imageScale = screenWidth / floor.getWidth();
		int originX = (int) (floor.getOriginXmetric() * scale);
		int originY = (int) (floor.getOriginYmetric() * scale);
		g2.setColor(new Color(255, 255, 255));
		if (floor.getBackground() != null && floor.getBackground().length() > 0) {
			String path = MAPS_PATH_PREFIX + floor.getOwner().getDomainName()
					+ "/maps/" + floor.getBackground();
			try {
				//BufferedImage map = ImageIO.read(new File(path));
				BufferedImage bufferedImage = buildBufferedImage(path);
				
				AffineTransform transform = new AffineTransform();
				transform.scale(imageScale, imageScale);
				g2.drawImage(bufferedImage, new AffineTransformOp(transform, null),
						getFloorX(0, borderX, originX),
						getFloorY(0, borderY, originY));
			} catch (IIOException e) {
				logt.info_ln("File: " + path + " not found.");
				double screenHeight = scale * floor.getActualHeightMetric();
				g2.fillRect(getFloorX(0, borderX, originX),
						getFloorY(0, borderY, originY), (int) screenWidth,
						(int) screenHeight);
			}
		} else {
			double screenHeight = scale * floor.getActualHeightMetric();
			g2.fillRect(getFloorX(0, borderX, originX),
					getFloorY(0, borderY, originY), (int) screenWidth,
					(int) screenHeight);
		}
		g2.setColor(new Color(204, 204, 204));
		// Right edge border
		g2.drawLine(borderX + floorWidth, borderY + 1, borderX + floorWidth,
				borderY + floorHeight);
		// Left edge border (right of tick marks)
		g2.drawLine(borderX + 1, borderY + floorHeight, borderX + floorWidth,
				borderY + floorHeight);
		g2.setColor(new Color(255, 255, 255));
		g2.fillRect(borderX, 0, floorWidth + 1, borderY);
		g2.fillRect(0, 0, borderX, borderY + floorHeight + 1);
		g2.setColor(new Color(120, 120, 120));
		g2.drawLine(0, borderY, floorWidth + borderX, borderY);
		g2.drawLine(borderX, 0, borderX, floorHeight + borderY);

		// Font font = new Font(Font.SANS_SERIF, Font.BOLD, 12);
		g2.setFont(font.deriveFont(Font.BOLD, 12));
		double actualWidth = floorWidth / scale;
		double actualHeight = floorHeight / scale;
		String firstLabel;
		double unitScale = scale;
		if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
			firstLabel = "0 feet";
			actualWidth /= LocationTracking.FEET_TO_METERS;
			actualHeight /= LocationTracking.FEET_TO_METERS;
			unitScale *= LocationTracking.FEET_TO_METERS;
		} else {
			firstLabel = "0 meters";
		}
		g2.drawString(firstLabel, borderX + 4, 12);
		double gridX = gridSize;
		while (gridX < actualWidth) {
			int x = (int) (gridX * unitScale) + borderX;
			g2.drawLine(x, 0, x, borderY);
			boolean label = true;
			if (gridX + gridSize >= actualWidth) {
				// Last mark
				if (x + getNumberPixelWidth(gridX) + 2 > floorWidth) {
					label = false;
				}
			}
			if (label) {
				g2.drawString("" + (int) gridX, x + 4, 12);
			}
			gridX += gridSize;
		}

		double gridY = 0;
		while (gridY < actualHeight) {
			int y = (int) (gridY * unitScale) + borderY;
			g2.drawLine(0, y, borderX, y);
			double lx = gridY;
			int dx = 1;
			for (int bx = borderX; bx >= 16; bx -= 7) {
				if (lx < 10) {
					dx += 7;
				} else {
					lx /= 10;
				}
			}
			boolean label = true;
			if (gridY + gridSize >= actualHeight) {
				// Last mark
				if (y - borderY + 13 > floorHeight) {
					label = false;
				}
			}
			if (label) {
				g2.drawString("" + (int) gridY, dx, y + 13);
			}
			gridY += gridSize;
		}

		double mapToImage = floor.getMapToMetric() * scale;
		if (floor.getPerimeter().size() > 0) {
			g2.setStroke(new BasicStroke(2));
			g2.setColor(new Color(2, 159, 245));
			int[] xPoints = new int[floor.getPerimeter().size()];
			int[] yPoints = new int[floor.getPerimeter().size()];
			int nPoints = 0;
			int perimId = floor.getPerimeter().get(0).getId();
			for (int i = 0; i < floor.getPerimeter().size(); i++) {
				Vertex vertex = floor.getPerimeter().get(i);
				if (vertex.getId() != perimId) {
					g2.drawPolygon(xPoints, yPoints, nPoints);
					nPoints = 0;
					perimId = vertex.getId();
				}
				xPoints[nPoints] = getFloorX(
						(int) (vertex.getX() * mapToImage), borderX, originX);
				yPoints[nPoints++] = getFloorY(
						(int) (vertex.getY() * mapToImage), borderY, originY);
			}
			g2.drawPolygon(xPoints, yPoints, nPoints);
		}

		g2.setStroke(new BasicStroke(1));
		g2.setColor(new Color(0, 170, 0));
		g2.setFont(font.deriveFont(Font.BOLD, 11));
		for (MapNode mapNode : floor.getChildNodes()) {
			if (!mapNode.isLeafNode()) {
				continue;
			}
			double x = mapNode.getX() * mapToImage;
			double y = mapNode.getY() * mapToImage;
			createNodeImage(mapNode.getId(), channelMap, colorMap,
					getFloorX((int) x, borderX, originX),
					getFloorY((int) y, borderY, originY), g2);
		}
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			double x = plannedAP.x * mapToImage;
			double y = plannedAP.y * mapToImage;
			createNodeImage(plannedAP.getId(), channelMap, colorMap,
					getFloorX((int) x, borderX, originX),
					getFloorY((int) y, borderY, originY), g2);
		}
		return image;
	}

    /**
     * Below code,<br>
     * <code><b>BufferedImage map = ImageIO.read(new File(path));</b></code> <br>
     * will throws exception --
     * IllegalArgumentException with the detail: "numbers of source Raster bands and source color space components do not match"
     * for some images which are gray-scale.
     * <br>
     * see {@link http://stackoverflow.com/questions/10416378/imageio-read-illegal-argument-exception-raster-bands-colour-space-components/11571181#11571181}
     * @author Yunzhi Lin
     * - Time: Feb 28, 2014 6:12:48 PM
     * @param filePath
     * @return
     * @throws IOException
     * @throws Exception
     */
    private BufferedImage buildBufferedImage(String filePath) throws IOException,
            Exception {
        BufferedImage bufferedImage = null;
        ImageInputStream stream = ImageIO.createImageInputStream(new File(filePath));
        Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
        Exception lastException = null;
        while (iter.hasNext()) {
            ImageReader reader = null;
            try {
                reader = (ImageReader) iter.next();
                ImageReadParam param = reader.getDefaultReadParam();
                reader.setInput(stream, true, true);
                Iterator<ImageTypeSpecifier> imageTypes = reader.getImageTypes(0);
                while (imageTypes.hasNext()) {
                    ImageTypeSpecifier imageTypeSpecifier = imageTypes.next();
                    int bufferedImageType = imageTypeSpecifier.getBufferedImageType();
                    if (bufferedImageType == BufferedImage.TYPE_BYTE_GRAY) {
                        param.setDestinationType(imageTypeSpecifier);
                        break;
                    }
                }
                bufferedImage = reader.read(0, param);
                if (null != bufferedImage)
                    break;
            } catch (Exception e) {
                lastException = e;
            } finally {
                if (null != reader)
                    reader.dispose();
            }
        }
        // If you don't have an image at the end of all readers
        if (null == bufferedImage) {
            if (null != lastException) {
                throw lastException;
            }
        }
        return bufferedImage;
    }

	private void createNodeImage(long id, Map<Long, Integer> channelMap,
			Map<Long, Integer> colorMap, int x, int y, Graphics2D g2) {
		Integer channel = channelMap.get(id);
		Integer colorIndex = colorMap.get(id);
		int radius = 11;
		if (channel == null || channel == 0 || colorIndex == null
				|| colorIndex > startChannelColors.length - 1) {
			g2.setColor(new Color(180, 180, 180));
			g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
			return;
		}
		g2.setColor(startChannelColors[colorIndex]);
		g2.fillOval(x - radius, y - radius, 2 * radius, 2 * radius);
		int dy = 6, w = 21;
		g2.fillRect(x, y - dy, w, dy + dy);
		dy = 5;
		g2.setColor(new Color(255, 255, 255));
		g2.fillRect(x + 1, y - dy, w - 2, dy + dy);
		g2.setColor(new Color(0, 51, 102));
		int chx = x + 2;
		if (channel < 100) {
			chx += 3;
		}
		if (channel < 10) {
			chx += 3;
		}
		g2.drawString("" + channel, chx, y + 4);
	}

	private int getFloorX(int x, int borderX, int origin) {
		return x + borderX + origin;
	}

	protected int getFloorY(int y, int borderY, int origin) {
		return y + borderY + origin;
	}

	protected int getNumberPixelWidth(double d) {
		int size = 0;
		while (d > 100) {
			size += 7;
			d /= 10;
		}
		return size;
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

	public List<MapContainerNode> assignBldChannels(MapContainerNode building,
			PlanToolConfig planToolConfig, Map<Short, Short> chgIndexMap,
			Map<Short, Short> chaIndexMap) throws Exception {
		List<MapContainerNode> floors = sortFloors(building);
		int apCount = 0;
		for (MapContainerNode floor : floors) {
			log.debug(floor.getPlannedAPs().size() + " planned APs on floor "
					+ floor.getMapName());
			apCount += floor.getPlannedAPs().size();
		}
		log.info_ln("Building " + building.getMapName() + " has "
				+ floors.size() + " floors, total of " + apCount
				+ " planned APs.");

		short[] channels = getAchannels(building, floors, planToolConfig);
		if (channels != null) {
			assignBldChannels(floors, planToolConfig, apCount, channels, true,
					false);
		}
		assignBldChannels(floors, planToolConfig, apCount,
				getBGchannels(building), false, false);
		boolean[] chgIndexes = initChannelIndexes(chgIndexMap);
		boolean[] chaIndexes = initChannelIndexes(chaIndexMap);
		for (MapContainerNode floor : floors) {
			addPlannedChannelIndexes(floor, chgIndexMap, chaIndexMap);
			addChannelIndexes(floor, chgIndexMap, chaIndexMap);
		}
		updateChannelIndexes(chgIndexMap, chgIndexes);
		updateChannelIndexes(chaIndexMap, chaIndexes);
		return floors;
	}

	public void assignBldChannels(List<MapContainerNode> floors,
			PlanToolConfig planToolConfig, int apCount, short[] channels,
			boolean useA, boolean verbose) throws Exception {
		if (apCount == 0) {
			return;
		}
		Date start = new Date();
		double nbrCost[][] = new double[apCount][apCount];
		PlannedAP aps[] = new PlannedAP[apCount];

		int shelve = 0;
		int autoCount = 0;
		for (int i = 0; i < floors.size(); i++) {
			MapContainerNode floor = floors.get(i);
			autoCount += calculateNbrCost(floor, useA, nbrCost, aps, shelve,
					true);
			shelve += floor.getPlannedAPs().size();
		}
		shelve = 0;
		for (int i = 0; i < floors.size(); i++) {
			MapContainerNode floor = floors.get(i);
			double loss = floor.getFloorLoss();
			double height = floor.getApElevationMetric();
			int lowerShelve = shelve + floor.getPlannedAPs().size();
			for (int j = i + 1; j < floors.size(); j++) {
				MapContainerNode lowerFloor = floors.get(j);
				calculateFloorNbrCost(floor, lowerFloor, useA, nbrCost, aps,
						shelve, lowerShelve, loss, height, verbose);
				loss += lowerFloor.getFloorLoss();
				height += lowerFloor.getApElevationMetric();
				lowerShelve += lowerFloor.getPlannedAPs().size();
			}
			shelve += floor.getPlannedAPs().size();
		}
		if (verbose) {
			show(nbrCost, "nbr lists (" + autoCount + " auto)");
		}
		aps = setInterferenceCost(aps, channels, nbrCost, autoCount, useA);
		dca(aps, planToolConfig, channels, useA, verbose);
		convertToChannels(aps, channels, useA);
		log.info_non("Bld Channel selection for " + (useA ? "a" : "g")
				+ " in: " + (new Date().getTime() - start.getTime()) + " ms.");
	}

	private void calculateFloorNbrCost(MapContainerNode floor,
			MapContainerNode lowerFloor, boolean useA, double nbrCost[][],
			PlannedAP aps[], int shelve, int lowerShelve, double floorLoss,
			double height, boolean verbose) {
		if (verbose) {
			log.info_ln("Spill between " + floor.getMapName() + "(" + shelve
					+ ") and " + lowerFloor.getMapName() + "(" + lowerShelve
					+ "), floorLoss: " + floorLoss + ", height: " + height);
		}
		double floorToMetric = floor.getMapToMetric();
		double floor_plf = mfloorle;
		double lowerFloorToMetric = lowerFloor.getMapToMetric();
		int i = shelve;
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			plannedAP = aps[i];
			double x_m = plannedAP.x * floorToMetric + floor.getOriginXmetric();
			double y_m = plannedAP.y * floorToMetric + floor.getOriginYmetric();
			double pwr = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
			double gn = getAverageGain(plannedAP.apModel, useA);
			int j = lowerShelve;
			for (PlannedAP nbrAP : lowerFloor.getPlannedAPs()) {
				nbrAP = aps[j];
				double nbr_x_m = nbrAP.x * lowerFloorToMetric
						+ lowerFloor.getOriginXmetric();
				double nbr_y_m = nbrAP.y * lowerFloorToMetric
						+ lowerFloor.getOriginYmetric();
				double dx_m = x_m - nbr_x_m;
				double dy_m = y_m - nbr_y_m;
				double d = Math.sqrt(dx_m * dx_m + dy_m * dy_m + height
						* height);
				double nbr_pwr = useA ? nbrAP.wifi1Power : nbrAP.wifi0Power;
				double nbr_gn = getAverageGain(nbrAP.apModel, useA)
						- getAverageShadow(nbrAP.apModel, useA);
				double d1 = d
						/ (1.0 + Math.pow(10, (nbr_pwr + nbr_gn - pwr - gn)
								/ floor_plf));
				if (verbose) {
					String p1 = String.format(" (%1$.2f,%2$.2f)", x_m, y_m);
					String p2 = String.format(" (%1$.2f,%2$.2f)", nbr_x_m,
							nbr_y_m);
					log.info_ln("AP" + plannedAP.getId() + p1 + " <-> AP"
							+ nbrAP.getId() + p2
							+ String.format(" d %1$.2f, d1 %2$.2f", d, d1));
				}
				double loss = 0;
				if (d1 > 1) {
					if (useA) {
						loss = getPathLossA(d1, floor_plf);
					} else {
						loss = getPathLossBG(d1, floor_plf);
					}
				}
				double cost = pwr + gn - loss - floorLoss;
				if (cost < -1000) {
					cost = -1000;
				}
				nbrCost[j][i] = cost;
				nbrCost[i][j++] = cost;
			}
			i++;
		}
	}

	private short[] getAchannels(MapContainerNode mapContainerNode,
			List<MapContainerNode> floors, PlanToolConfig planToolConfig) {
		boolean[] allChannels = new boolean[200];
		if (floors != null) {
			for (MapContainerNode floor : floors) {
				for (PlannedAP plannedAP : floor.getPlannedAPs()) {
					markUsedChannels(plannedAP, planToolConfig, allChannels);
				}
			}
		} else {
			for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
				markUsedChannels(plannedAP, planToolConfig, allChannels);
			}
		}
		int channelCount = 0;
		for (int i = 1; i < allChannels.length; i++) {
			if (allChannels[i]) {
				channelCount++;
			}
		}
		if (channelCount > 0) {
			short[] channels = new short[channelCount];
			int j = 0;
			for (short i = 1; i < allChannels.length; i++) {
				if (allChannels[i]) {
					channels[j++] = i;
				}
			}
			return channels;
		}
		return null;
	}

	private void markUsedChannels(PlannedAP plannedAP,
			PlanToolConfig planToolConfig, boolean[] allChannels) {
		short channelWidth = planToolConfig.getChannelWidth();
		short apModel = plannedAP.apModel;
		if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80) {
            if (apModel != HiveAp.HIVEAP_MODEL_370 && apModel != HiveAp.HIVEAP_MODEL_390
                    && apModel != HiveAp.HIVEAP_MODEL_230) {
				// override, this AP does not support 80 MHz channel width
				channelWidth = RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A;
			}
		}
		int[] channelList = CountryCode.getChannelList_5GHz(
				planToolConfig.getCountryCode(), channelWidth, false, false,
				apModel, apModel == HiveAp.HIVEAP_MODEL_170);
		if (channelList.length == 1 && apModel == HiveAp.HIVEAP_MODEL_170) {
			log.info_ln("Try with dfs enabled.");
			channelList = CountryCode.getChannelList_5GHz(
					planToolConfig.getCountryCode(), channelWidth, true, false,
					apModel, true);
		}
		for (int i = 1; i < channelList.length; i++) {
			allChannels[channelList[i]] = true;
		}
	}

	private short findFirstValidChannel(PlannedAP plannedAP,
			PlanToolConfig planToolConfig, short[] channels,
			boolean[] validChannels) {
		markUsedChannels(plannedAP, planToolConfig, validChannels);
		short i = 0;
		for (; i < channels.length; i++) {
			if (validChannels[channels[i]]) {
				break;
			}
		}
		if (i == channels.length) {
			// No valid channels ? must be a problem with the
			// CountryCode.getChannelList_5GHz function
			i = 0;
			validChannels[channels[i]] = true;
		}
		return i;
	}

	private short[] getBGchannels(MapContainerNode mapContainerNode) {
		short[] bgChannels = new short[] { 1, 5, 9, 13 };
		boolean three = false;
		if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			for (MapNode mapNode : mapContainerNode.getChildNodes()) {
				if (mapNode.isLeafNode()) {
					continue;
				}
				if (hasNonEuropeanAP((MapContainerNode) mapNode)) {
					three = true;
					break;
				}
			}
		} else {
			three = hasNonEuropeanAP(mapContainerNode);

		}
		if (three) {
			bgChannels = new short[] { 1, 6, 11 };
		}
		return bgChannels;
	}

	private boolean hasNonEuropeanAP(MapContainerNode mapContainerNode) {
		for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
			if (!CountryCode.isEuropeCountry(plannedAP.countryCode)) {
				return true;
			}
		}
		return false;
	}

	public List<MapContainerNode> assignChannels(
			MapContainerNode mapContainerNode, PlanToolConfig planToolConfig,
			Map<Short, Short> chgIndexMap, Map<Short, Short> chaIndexMap)
			throws Exception {
		if (mapContainerNode.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
			// Consider all floors in this building.
			return assignBldChannels(mapContainerNode.getParentMap(),
					planToolConfig, chgIndexMap, chaIndexMap);
		} else {
			short[] channels = getAchannels(mapContainerNode, null,
					planToolConfig);
			if (channels != null) {
				assignChannels(mapContainerNode, planToolConfig, channels,
						true, false);
			}
			assignChannels(mapContainerNode, planToolConfig,
					getBGchannels(mapContainerNode), false, false);
			boolean[] chgIndexes = initChannelIndexes(chgIndexMap);
			boolean[] chaIndexes = initChannelIndexes(chaIndexMap);
			addPlannedChannelIndexes(mapContainerNode, chgIndexMap, chaIndexMap);
			updateChannelIndexes(chgIndexMap, chgIndexes);
			updateChannelIndexes(chaIndexMap, chaIndexes);
			return null;
		}
	}

	private void updateChannelIndexes(Map<Short, Short> chIndexMap,
			boolean[] chIndexes) {
		List<Short> channels = new ArrayList<Short>(chIndexMap.keySet());
		Collections.sort(channels);
		short i = 0;
		for (Short channel : channels) {
			short chi = chIndexMap.get(channel);
			if (chi < 0) {
				while (chIndexes[i]) {
					i++;
				}
				chIndexes[i] = true;
				chIndexMap.put(channel, i);
			}
		}
	}

	private boolean[] initChannelIndexes(Map<Short, Short> chIndexMap) {
		boolean[] chIndexes = new boolean[50];
		for (Short chi : chIndexMap.values()) {
			chIndexes[chi] = true;
		}
		return chIndexes;
	}

	private void addChannelIndexes(MapContainerNode floor,
			Map<Short, Short> chgIndexMap, Map<Short, Short> chaIndexMap) {
		fetchRadioAttributes(floor.getChildNodes());
		for (MapNode mapNode : floor.getChildNodes()) {
			if (!mapNode.isLeafNode()) {
				continue;
			}
			MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
			short wifi0Channel = mapLeafNode.getRadioChannelBG();
			if (wifi0Channel > 0 && chgIndexMap.get(wifi0Channel) == null) {
				chgIndexMap.put(wifi0Channel, (short) -1);
			}
			short wifi1Channel = mapLeafNode.getRadioChannelA();
			if (wifi1Channel > 0 && chaIndexMap.get(wifi1Channel) == null) {
				chaIndexMap.put(wifi1Channel, (short) -1);
			}
		}
	}

	private void addPlannedChannelIndexes(MapContainerNode floor,
			Map<Short, Short> chgIndexMap, Map<Short, Short> chaIndexMap) {
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			short wifi0Channel = plannedAP.autoWifi0Channel;
			if (wifi0Channel < 0) {
				wifi0Channel = plannedAP.wifi0Channel;
			}
			if (chgIndexMap.get(wifi0Channel) == null) {
				chgIndexMap.put(wifi0Channel, (short) -1);
			}

			short wifi1Channel = plannedAP.autoWifi1Channel;
			if (wifi1Channel < 0) {
				wifi1Channel = plannedAP.wifi1Channel;
			}
			if (chaIndexMap.get(wifi1Channel) == null) {
				chaIndexMap.put(wifi1Channel, (short) -1);
			}
		}
	}

	private void assignChannels(MapContainerNode mapContainerNode,
			PlanToolConfig planToolConfig, short[] channels, boolean useA,
			boolean verbose) throws Exception {
		int apCount = mapContainerNode.getPlannedAPs().size();
		if (apCount == 0) {
			return;
		}
		double nbrCost[][] = new double[apCount][apCount];
		PlannedAP aps[] = new PlannedAP[apCount];
		int autoCount = calculateNbrCost(mapContainerNode, useA, nbrCost, aps,
				0, false);
		if (verbose) {
			show(nbrCost, "nbr lists");
		}
		aps = setInterferenceCost(aps, channels, nbrCost, autoCount, useA);
		dca(aps, planToolConfig, channels, useA, verbose);
		convertToChannels(aps, channels, useA);
	}

	private int calculateNbrCost(MapContainerNode mapContainerNode,
			boolean useA, double nbrCost[][], PlannedAP aps[], int shelve,
			boolean sort) {
		double mapToMetric = mapContainerNode.getMapToMetric();
		double n = getPathLossFactor(mapContainerNode);
		int i = shelve;
		int autoCount = 0;
		Collection<PlannedAP> plannedAPs = mapContainerNode.getPlannedAPs();
		if (sort) {
			List<PlannedAP> list = new ArrayList<PlannedAP>(plannedAPs);
			Collections.sort(list, new Comparator<PlannedAP>() {
				@Override
				public int compare(PlannedAP o1, PlannedAP o2) {
					return (int) (o1.getId() - o2.getId());
				}
			});
			plannedAPs = list;
		}
		for (PlannedAP plannedAP : plannedAPs) {
			if (autoChannelRadio(plannedAP, useA)) { // auto
				autoCount++;
			}
			aps[i] = plannedAP;
			double x_m = plannedAP.x * mapToMetric;
			double y_m = plannedAP.y * mapToMetric;
			double pwr = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
			double gn = getAverageGain(plannedAP.apModel, useA);
			int j = shelve;
			for (PlannedAP nbrAP : plannedAPs) {
				if (nbrAP.getId().equals(plannedAP.getId())) {
					break;
				}
				double nbr_x_m = nbrAP.x * mapToMetric;
				double nbr_y_m = nbrAP.y * mapToMetric;
				double dx_m = x_m - nbr_x_m;
				double dy_m = y_m - nbr_y_m;
				double d = Math.sqrt(dx_m * dx_m + dy_m * dy_m);
				double nbr_pwr = useA ? nbrAP.wifi1Power : nbrAP.wifi0Power;
				double nbr_gn = getAverageGain(nbrAP.apModel, useA);
				/*
				 * d1 is where signal stength is the same for both
				 */
				double d1 = d
						/ (1.0 + Math
								.pow(10, (nbr_pwr + nbr_gn - pwr - gn) / n));
				double loss = 0;
				if (d1 > 1) {
					if (useA) {
						loss = getPathLossA(d1, n);
					} else {
						loss = getPathLossBG(d1, n);
					}
				}
				double cost = pwr + gn - loss;
				if (cost < -1000) {
					cost = -1000;
				}
				nbrCost[j][i] = cost;
				nbrCost[i][j++] = cost;
			}
			i++;
		}
		return autoCount;
	}

	private boolean autoChannelRadio(PlannedAP ap, boolean useA) {
		if (getChannel(ap, useA) == 0) { // auto
			if (useA) {
				if (ap.wifi1Enabled) {
					return true;
				}
			} else {
				if (ap.wifi0Enabled) {
					return true;
				}
			}
		}
		return false;
	}

	private void convertToChannels(PlannedAP aps[], short[] channels,
			boolean useA) {
		// Convert back to channels
		for (int i = 0; i < aps.length; i++) {
			if (getAutoChannel(aps[i], useA) < 0) {
				continue;
			}
			setAutoChannel(aps[i], useA, channels[getAutoChannel(aps[i], useA)]);
		}
	}

	private boolean addToAssigned(PlannedAP aps[], double nbrCost[][],
			short[] channels, boolean useA, boolean verbose) {
		double maxCost = -Double.MAX_VALUE;
		int maxCostAp = -1;
		short minCostChannel = -1;
		for (int i = 0; i < aps.length; i++) {
			if (getAutoChannel(aps[i], useA) >= 0) {
				continue;
			}
			// aps[i] is unassigned
			double[] channelCost = new double[channels.length];
			for (int j = 0; j < channelCost.length; j++) {
				channelCost[j] = -Double.MAX_VALUE;
			}
			for (int j = 0; j < aps.length; j++) {
				if (getAutoChannel(aps[j], useA) < 0) {
					continue;
				}
				// aps[j] is assigned
				short channelIndex = getAutoChannel(aps[j], useA);
				if (nbrCost[i][j] > channelCost[channelIndex]) {
					channelCost[channelIndex] = nbrCost[i][j];
				}
			}
			double lowestCost = Double.MAX_VALUE, totalCost = 0, totalCostCount = 0;
			short lowestCostChannel = -1, newChannel = -1;
			for (short j = 0; j < channelCost.length; j++) {
				if (channelCost[j] > -Double.MAX_VALUE) {
					totalCost += channelCost[j];
					totalCostCount++;
					if (channelCost[j] < lowestCost) {
						lowestCost = channelCost[j];
						lowestCostChannel = j;
					}
				} else if (newChannel < 0) {
					// Not all channels are used yet in the cluster.
					// Go for lowest available channel index.
					newChannel = j;
				}
			}
			double averageCost = totalCost / totalCostCount;
			if (verbose) {
				log.info_ln("% from: " + aps[i].hostName + ", average cost: "
						+ averageCost + ", lowest cost: " + lowestCost
						+ ", channel: " + channels[lowestCostChannel] + " ("
						+ lowestCostChannel + ")");
			}
			double averageOrLowestCost = averageCost;
			// averageOrLowestCost = lowestCost;
			if (averageOrLowestCost > maxCost) {
				maxCost = averageOrLowestCost;
				maxCostAp = i;
				if (newChannel < 0) {
					minCostChannel = lowestCostChannel;
				} else {
					minCostChannel = newChannel;
				}
			}
		}
		if (maxCostAp < 0) {
			return false;
		} else {
			if (verbose) {
				log.info_ln("% The winner is: " + aps[maxCostAp].hostName
						+ ", channel index: " + minCostChannel);
			}
			setAutoChannel(aps[maxCostAp], useA, minCostChannel);
			return true;
		}
	}

	private void setAutoChannel(PlannedAP plannedAP, boolean useA, short channel) {
		if (useA) {
			plannedAP.autoWifi1Channel = channel;
		} else {
			plannedAP.autoWifi0Channel = channel;
		}
	}

	private short getAutoChannel(PlannedAP plannedAP, boolean useA) {
		return useA ? plannedAP.autoWifi1Channel : plannedAP.autoWifi0Channel;
	}

	private short getChannel(PlannedAP plannedAP, boolean useA) {
		return useA ? plannedAP.wifi1Channel : plannedAP.wifi0Channel;
	}

	private short getChannelIndex(short channel, short[] channels) {
		for (short i = 0; i < channels.length; i++) {
			if (channel == channels[i]) {
				return i;
			}
		}
		return -1;
	}

	private void dca(PlannedAP aps[], PlanToolConfig planToolConfig,
			short[] channels, boolean useA, boolean verbose) {
		if (verbose) {
			for (int i = 0; i < aps.length; i++) {
				show(aps[i].inNetworkCost, "In-network interference");
			}
			for (int i = 0; i < aps.length; i++) {
				show(aps[i].outOfNetworkCost,
						"Out-of-network interference for " + aps[i].hostName);
			}
		}

		int startAP = findStartAP(aps, channels, false);
		if (startAP < 0) {
			return; // all channels have been statically assigned.
		}
		short startChannel = findStartChannel(aps[startAP],
				useA ? planToolConfig : null, channels);
		setAutoChannel(aps[startAP], useA, startChannel);
		log.info_non("Starting point for cluster is " + aps[startAP].hostName
				+ ", channel: " + channels[startChannel]);

		Date start = new Date();
		dca_heuristic(aps, useA ? planToolConfig : null, channels, useA,
				startAP, false);
		log.info_non("Channel selection heuristic in: "
				+ (new Date().getTime() - start.getTime()) + " ms.");

		short assignments[] = new short[aps.length];
		for (int i = 0; i < aps.length; i++) {
			assignments[i] = getAutoChannel(aps[i], useA);
		}

		long heuristicCost = getTotalCost(aps, assignments, Long.MAX_VALUE);
		log.info_non("Interference cost is: " + heuristicCost);

		if (true) {
			return;
		}
		log.info_ln("Starting exhaustive search.");
		start = new Date();
		long totalCount = (long) Math.pow(channels.length, aps.length);
		Point better = new Point();
		long subset = exhaustiveSearch(aps, channels.length,
				new short[aps.length], 0, start, 0, heuristicCost, better, 0,
				false);
		showScore(totalCount, -1, better.x, heuristicCost, better.y, start);
		log.info_ln("# solutions evaluated: " + subset + " (out of "
				+ totalCount + ") in: "
				+ (new Date().getTime() - start.getTime()) + " ms.");
	}

	private int findStartAP(PlannedAP aps[], short[] channels, boolean verbose) {
		long maxCost = Long.MIN_VALUE;
		int maxIndex = -1;
		for (int i = 0; i < aps.length; i++) {
			long[] cost = aps[i].inNetworkCost.clone();
			Arrays.sort(cost);
			int thresholdIndex = cost.length - channels.length + 1;
			if (thresholdIndex < 0) {
				thresholdIndex = 0;
			}
			while (thresholdIndex < cost.length && cost[thresholdIndex] == 0) {
				thresholdIndex++;
			}
			long threshold = 0;
			long totalCloseNbrCost = 0;
			int nbrCount = 0;
			if (thresholdIndex < cost.length) {
				threshold = cost[thresholdIndex];
				for (int j = 0; j < aps.length; j++) {
					if (aps[i].inNetworkCost[j] >= threshold) {
						totalCloseNbrCost += aps[i].inNetworkCost[j];
						nbrCount++;
					}
				}
			}
			// Add out-of-network interference
			long maxAverageCost = Long.MIN_VALUE;
			for (int j = 0; j < channels.length; j++) {
				long outOfNetworkCost = aps[i].outOfNetworkCost[j];
				long averageCloseNbrCost = 0;
				if (outOfNetworkCost > threshold) {
					averageCloseNbrCost = (totalCloseNbrCost + outOfNetworkCost + outOfNetworkCost
							/ channels.length * nbrCount / 4)
							/ (nbrCount + 1);
				} else if (nbrCount > 0) {
					averageCloseNbrCost = totalCloseNbrCost / nbrCount;
				}
				if (verbose) {
					log.info_ln("Average close nbr cost for channel: " + j
							+ " is: " + averageCloseNbrCost + ", nbr count: "
							+ nbrCount);
				}
				if (averageCloseNbrCost > maxAverageCost) {
					maxAverageCost = averageCloseNbrCost;
				}
			}
			if (verbose) {
				log.info_ln(aps[i].hostName + " is broadcasting "
						+ maxAverageCost + " as its starting score.");
			}
			if (maxAverageCost > maxCost) {
				maxCost = maxAverageCost;
				maxIndex = i;
			}
		}
		if (verbose) {
			log.info_ln("Winner is " + aps[maxIndex].hostName + " with score "
					+ maxCost);
		}
		return maxIndex;
	}

	private short findStartChannel(PlannedAP startAP,
			PlanToolConfig planToolConfig, short[] channels) {
		// Add out-of-network interference
		long minCost = Long.MAX_VALUE;
		short i = 0;
		boolean[] validChannels = null;
		if (planToolConfig != null) {
			validChannels = new boolean[200];
			i = findFirstValidChannel(startAP, planToolConfig, channels,
					validChannels);
            if (startAP.apModel == HiveAp.HIVEAP_MODEL_370 || startAP.apModel == HiveAp.HIVEAP_MODEL_390
                    || startAP.apModel == HiveAp.HIVEAP_MODEL_230) {
                if (i + 1 < channels.length && validChannels[channels[i + 1]]) {
                    // For 11ac use next channel if valid.
                    i++;
                }
            }
        }
        short minCostChannel = i;
        for (short j = i; j < channels.length; j++) {
            if (validChannels == null || validChannels[channels[j]]) {
                long outOfNetworkCost = startAP.outOfNetworkCost[j];
                // Should also take into account 11ac channel width
                if (outOfNetworkCost < minCost) {
                    minCost = outOfNetworkCost;
                    minCostChannel = j;
                }
            }
        }
        return minCostChannel;
    }

	private long getTotalCost(PlannedAP aps[], short[] assignments,
			long upperBound) {
		long totalCost = 0;
		for (int i = 0; i < aps.length; i++) {
			int channel = assignments[i];
			for (int k = 0; k < aps.length; k++) {
				if (channel == assignments[k]) {
					totalCost += aps[i].inNetworkCost[k];
				}
			}
			if (totalCost > upperBound) {
				return totalCost;
			}
		}
		return totalCost;
	}

	private void dca_heuristic(PlannedAP aps[], PlanToolConfig planToolConfig,
			short[] channels, boolean useA, int startAp, boolean verbose) {
		long[] cluster = new long[aps.length];
		cluster[startAp] = new Date().getTime();
		if (verbose) {
			showDCA(aps, channels, useA, cluster);
		}

		do {
			/*
			 * The first AP joining the cluster, i.e. the first AP going to
			 * Listen state will be the arbiter AP.
			 */
			int[] arbiterAPs = new int[aps.length];
			short[] candidateChannelIndex = new short[aps.length];
			long[] costToCluster = new long[aps.length];
			for (int i = 0; i < arbiterAPs.length; i++) {
				arbiterAPs[i] = -1;
			}

			boolean anyOutsideCluster = false;
			for (int candidateAP = 0; candidateAP < aps.length; candidateAP++) {
				if (cluster[candidateAP] > 0) {
					continue;
				}
				// AP[candidateAP] is not part of any cluster yet (not in Listen
				// state).
				anyOutsideCluster = true;
				/*
				 * Calculate the best candidate channel for this AP, referred to
				 * as candidateChannelIndex[candidateAP], and the from this AP
				 * to the cluster (= the cost to the APs already in Listen
				 * state).
				 */
				findCandidateChannel(aps, planToolConfig, channels, useA,
						cluster, candidateAP, arbiterAPs,
						candidateChannelIndex, costToCluster, verbose);
			}
			if (!anyOutsideCluster) {
				break;
			}

			for (int clusterAP = 0; clusterAP < aps.length; clusterAP++) {
				if (cluster[clusterAP] == 0) {
					continue;
				}
				int closest = makeArbiterRequest(aps, clusterAP, arbiterAPs,
						candidateChannelIndex, channels, costToCluster, verbose);
				/*
				 * The AP which is the closest to the cluster (= closest to all
				 * the APs already in Listen state, will be granted the channel
				 * request. The remaining APs either go back to SCAN and
				 * recalculate the cost function or are being notified that this
				 * AP will change to the requested channel.
				 */
				if (closest >= 0) {
					cluster[closest] = new Date().getTime();
					log.info_non("Assigning channel "
							+ channels[candidateChannelIndex[closest]]
							+ " to AP " + aps[closest].hostName);
					setAutoChannel(aps[closest], useA,
							candidateChannelIndex[closest]);
				}
			}
			if (verbose) {
				showDCA(aps, channels, useA, cluster);
			}
		} while (true);
		if (!verbose) {
			// showDCA(aps, channels, useA, cluster);
		}
	}

	private void findCandidateChannel(PlannedAP aps[],
			PlanToolConfig planToolConfig, short[] channels, boolean useA,
			long[] cluster, int candidateAP, int[] arbiterAP,
			short[] candidateChannelIndex, long[] costToCluster, boolean verbose) {
		long[] channelCost = new long[channels.length];
		long[] closeChannelCost = new long[channels.length];
		int[] channelCostAP = new int[channels.length];
		for (int i = 0; i < channelCost.length; i++) {
			channelCost[i] = Long.MIN_VALUE;
			closeChannelCost[i] = Long.MIN_VALUE;
		}
		long oldestClusterMemberAge = Long.MAX_VALUE;
		int oldestClusterMember = -1;
		for (int clusterAP = 0; clusterAP < aps.length; clusterAP++) {
			if (cluster[clusterAP] == 0) {
				continue;
			}
			if (cluster[clusterAP] < oldestClusterMemberAge) {
				oldestClusterMemberAge = cluster[clusterAP];
				oldestClusterMember = clusterAP;
			}
			short channelIndex = getAutoChannel(aps[clusterAP], useA);
			/*
			 * For AP[candidateAP] which is not part of the cluster yet, look
			 * for the AP[clusterAP] in the cluster with channel channelIndex
			 * which has the highest cost for AP[candidateAP] operating on the
			 * same channel.
			 */
			long candidateInNetworkCost = aps[candidateAP].inNetworkCost[clusterAP];
			if (verbose) {
				log.info_non("Cost for candidate to operate in channel: "
						+ channelIndex + " (" + channels[channelIndex]
						+ ") is: " + candidateInNetworkCost);
			}
			if (candidateInNetworkCost > channelCost[channelIndex]) {
				channelCost[channelIndex] = candidateInNetworkCost;
				channelCostAP[channelIndex] = clusterAP;
			}
			short clusterApModel = aps[clusterAP].apModel;
			if (useA
                    && (clusterApModel == HiveAp.HIVEAP_MODEL_370 || clusterApModel == HiveAp.HIVEAP_MODEL_390 || clusterApModel == HiveAp.HIVEAP_MODEL_230)) {
				long firstCloseChannelInNetworkCost = aps[candidateAP].inNetworkCost[clusterAP] / 2;
				long secondCloseChannelInNetworkCost = firstCloseChannelInNetworkCost / 2;
				long thirdCloseChannelInNetworkCost = secondCloseChannelInNetworkCost / 2;
				boolean closeRightChannel = false, closeLeftChannel = false;
				int closeChannels = 0;
				if (channelIndex < channels.length - 1) {
					// Try first right
					closeRightChannel = updateCloseChannelCost(aps, channels,
							firstCloseChannelInNetworkCost, closeChannelCost,
							channelIndex, channelIndex + 1, verbose);
					if (closeRightChannel) {
						closeChannels++;
					}
				}
				if (channelIndex > 0) {
					// Try first left
					closeLeftChannel = updateCloseChannelCost(aps, channels,
							firstCloseChannelInNetworkCost, closeChannelCost,
							channelIndex, channelIndex - 1, verbose);
					if (closeLeftChannel) {
						closeChannels++;
					}
				}
				if (planToolConfig.getChannelWidth() == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80) {
					// Try second right
					if (closeRightChannel && channelIndex < channels.length - 2) {
						closeRightChannel = updateCloseChannelCost(aps,
								channels, secondCloseChannelInNetworkCost,
								closeChannelCost, channelIndex + 1,
								channelIndex + 2, verbose);
						if (closeRightChannel) {
							closeChannels++;
						}
					}
					// Try second left
					if (closeLeftChannel && channelIndex > 1
							&& closeChannels < 3) {
						closeLeftChannel = updateCloseChannelCost(aps,
								channels, secondCloseChannelInNetworkCost,
								closeChannelCost, channelIndex - 1,
								channelIndex - 2, verbose);
						if (closeLeftChannel) {
							closeChannels++;
						}
					}
					// Try third right
					if (closeRightChannel && channelIndex < channels.length - 3) {
						closeRightChannel = updateCloseChannelCost(aps,
								channels, thirdCloseChannelInNetworkCost,
								closeChannelCost, channelIndex + 2,
								channelIndex + 3, verbose);
						if (closeRightChannel) {
							closeChannels++;
						}
					}
					// Try third left
					if (closeLeftChannel && channelIndex > 2
							&& closeChannels < 3) {
						closeLeftChannel = updateCloseChannelCost(aps,
								channels, thirdCloseChannelInNetworkCost,
								closeChannelCost, channelIndex - 2,
								channelIndex - 3, verbose);
						if (closeLeftChannel) {
							closeChannels++;
						}
					}
				}
			}
		}
		if (verbose) {
			show(closeChannelCost, "CloseChannelCost");
		}
		long lowestCost = Long.MAX_VALUE, totalCost = 0;
		int totalCostCount = 0;
		short lowestCostChannelIndex = -1, newChannelIndex = -1;
		short j = 0;
		boolean[] validChannels = null;
		if (planToolConfig != null) {
			validChannels = new boolean[200];
			j = findFirstValidChannel(aps[candidateAP], planToolConfig,
					channels, validChannels);
		}
		for (short i = j; i < channelCost.length; i++) {
			if (validChannels == null || validChannels[channels[i]]) {
				// Add the per channel out of network interference
				long outOfNetworkCost = aps[candidateAP].outOfNetworkCost[i];
				if (channelCost[i] > Long.MIN_VALUE) {
					channelCost[i] += outOfNetworkCost;
				} else if (closeChannelCost[i] > Long.MIN_VALUE) {
					channelCost[i] = closeChannelCost[i] + outOfNetworkCost;
				} else if (outOfNetworkCost > 0) {
					channelCost[i] = outOfNetworkCost;
				}
				if (verbose) {
					log.info_non("% channelCost[" + i + "] = " + channelCost[i]);
				}
				if (channelCost[i] > Long.MIN_VALUE) {
					totalCost += channelCost[i];
					totalCostCount++;
					if (channelCost[i] < lowestCost) {
						// Look for the lowest cost channel for candidateAP
						lowestCost = channelCost[i];
						lowestCostChannelIndex = i;
					}
				} else if (newChannelIndex < 0) {
					// Not all channels are used yet in the cluster.
					// Go for lowest available channel index.
					newChannelIndex = i;
					short candidateApModel = aps[candidateAP].apModel;
					if (useA
                            && (candidateApModel == HiveAp.HIVEAP_MODEL_370
                                    || candidateApModel == HiveAp.HIVEAP_MODEL_390 || candidateApModel == HiveAp.HIVEAP_MODEL_230)) {
                        if (i < channelCost.length - 1 && validChannels[channels[i + 1]]
								&& channelCost[i + 1] == Long.MIN_VALUE) {
							// For 11ac, use next one instead
							newChannelIndex++;
						}
					}
				}
			}
		}
		long averageCost = totalCostCount == 0 ? 0 : totalCost / totalCostCount;
		// costToCluster[candidateAP] = lowestCost;
		// use average instead
		costToCluster[candidateAP] = averageCost;
		if (newChannelIndex < 0) {
			arbiterAP[candidateAP] = channelCostAP[lowestCostChannelIndex];
			// Always use oldestClusterMember as arbiter
			arbiterAP[candidateAP] = oldestClusterMember;
			/*
			 * All channels have been used at least once in the cluster, so the
			 * best candidate channel for candidateAP is the lowest cost channel
			 * w.r.t. cluster.
			 */
			candidateChannelIndex[candidateAP] = lowestCostChannelIndex;
		} else {
			arbiterAP[candidateAP] = oldestClusterMember;
			/*
			 * Use new channel
			 */
			candidateChannelIndex[candidateAP] = newChannelIndex;
		}
		if (verbose) {
			log.info_non("% from: " + aps[candidateAP].hostName
					+ ", average cost: " + averageCost + ", lowest cost: "
					+ lowestCost + ", channel index: " + lowestCostChannelIndex
					+ ", candidate channel index: "
					+ candidateChannelIndex[candidateAP]);
		}
	}

	private boolean updateCloseChannelCost(PlannedAP aps[], short[] channels,
			long candidateCloseChannelInNetworkCost, long[] closeChannelCost,
			int channelIndex, int closeChannelIndex, boolean verbose) {
		short closeChannel = channels[closeChannelIndex];
		if (Math.abs(closeChannel - channels[channelIndex]) != 4) {
			return false;
		}
		// 20 MHz apart
		if (verbose) {
			log.info_non("Cost for candidate to operate in close channel: "
					+ closeChannelIndex + " (" + closeChannel + ") is: "
					+ candidateCloseChannelInNetworkCost);
		}
		if (candidateCloseChannelInNetworkCost > closeChannelCost[closeChannelIndex]) {
			closeChannelCost[closeChannelIndex] = candidateCloseChannelInNetworkCost;
		}
		return true;
	}

	private int makeArbiterRequest(PlannedAP aps[], int clusterAP,
			int[] arbiterAPs, short[] candidateChannelIndex, short[] channels,
			long[] costToCluster, boolean verbose) {
		long ctc = Long.MIN_VALUE;
		int requestCount = 0, closest = -1;
		for (int i = 0; i < arbiterAPs.length; i++) {
			if (arbiterAPs[i] == clusterAP) {
				if (verbose) {
					log.info_non(aps[i].hostName + " requests "
							+ aps[clusterAP].hostName + " for channel: "
							+ channels[candidateChannelIndex[i]]
							+ ", cost to cluster: " + costToCluster[i]);
				}
				requestCount++;
				if (costToCluster[i] > ctc) {
					// This one is closer
					ctc = costToCluster[i];
					closest = i;
				}
			}
		}
		if (closest >= 0 && verbose) {
			log.info_non(requestCount + " requests against arbiter "
					+ aps[clusterAP].hostName + ", winner is "
					+ aps[closest].hostName + ", channel: "
					+ channels[candidateChannelIndex[closest]]);
		}
		return closest;
	}

	private long exhaustiveSearch(PlannedAP aps[], int channelCount,
			short[] assignments, int apIndex, Date start, long cost,
			long costThreshold, Point better, long solutionsCount,
			boolean verbose) {
		if (apIndex > 1) {
			PlannedAP newAP = aps[apIndex - 1];
			short channel = assignments[apIndex - 1];
			for (int i = 0; i < apIndex - 1; i++) {
				if (channel == assignments[i]) {
					cost += aps[i].inNetworkCost[apIndex - 1]
							+ newAP.inNetworkCost[i];
				}
			}
			if (cost > costThreshold) {
				// don't even try this branch.
				return 0;
			}
		}
		if (apIndex == aps.length) {
			if (cost <= costThreshold) {
				if (cost < costThreshold) {
					if (better.x == 0 || cost < better.y) {
						better.y = (int) cost;
					}
					better.x++;
				}
				if (verbose) {
					String matching = "% matching";
					if (cost < costThreshold) {
						matching = "% better";
					}
					show(assignments, matching + " score (" + cost + "): ");
				}
			}
			return 1;
		}
		long assignmentsCount = 0;
		for (short c = 0; c < channelCount; c++) {
			assignments[apIndex] = c;
			assignmentsCount += exhaustiveSearch(aps, channelCount,
					assignments, apIndex + 1, start, cost, costThreshold,
					better, solutionsCount * channelCount + c, verbose);
		}

		if (apIndex > 0 && aps.length - apIndex == 17) {
			showScore((solutionsCount + 1) * (long) Math.pow(channelCount, 17),
					-1, better.x, costThreshold, better.y, start);
		}
		return assignmentsCount;
	}

	private PlannedAP[] setInterferenceCost(PlannedAP aps[], short[] channels,
			double[][] nbrCost, int autoCount, boolean useA) {
		PlannedAP newAPs[] = new PlannedAP[autoCount];
		int auto_i = 0;
		for (int i = 0; i < aps.length; i++) {
			if (!autoChannelRadio(aps[i], useA)) { // !auto
				continue;
			}
			long[] inNetwork = new long[autoCount];
			aps[i].inNetworkCost = inNetwork;
			long[] outOfNetwork = new long[channels.length];
			aps[i].outOfNetworkCost = outOfNetwork;
			newAPs[auto_i++] = aps[i];
			int auto_j = 0;
			for (int j = 0; j < aps.length; j++) {
				if (!autoChannelRadio(aps[j], useA)) { // !auto
					continue;
				}
				double cost = nbrCost[i][j];
				inNetwork[auto_j++] = (long) ((cost < 0 ? cost + 1000 : 0) * 100);
			}
		}
		for (int i = 0; i < aps.length; i++) {
			if (autoChannelRadio(aps[i], useA)) { // auto
				continue;
			}
			short channel = getChannel(aps[i], useA);
			channel = getChannelIndex(channel, channels);
			if (channel < 0) { // no interference with this one
				continue;
			}
			for (int j = 0; j < aps.length; j++) {
				if (i == j || !autoChannelRadio(aps[j], useA)) { // !auto
					continue;
				}
				double cost = nbrCost[j][i];
				long channelCost = (long) ((cost < 0 ? cost + 1000 : 0) * 100);
				if (channelCost > aps[j].outOfNetworkCost[channel]) {
					aps[j].outOfNetworkCost[channel] = channelCost;
				}
			}
		}
		return newAPs;
	}

	private void showScore(long totalCount, long matchingCount,
			long betterCount, double threshold, double bestScore, Date start) {
		String better = ".";
		if (betterCount > 0) {
			better = ", best solution is "
					+ String.format("%1$.2f", (threshold - bestScore)
							/ threshold * 100) + "% better.";
		}
		log.info_ln("Solutions count: " + totalCount + " in "
				+ (new Date().getTime() - start.getTime())
				+ " ms, better count: " + betterCount + better);
	}

	private void showDCA(PlannedAP aps[], short[] channels, boolean useA,
			long[] cluster) {
		log.info_non("Channel selections:");
		for (int i = 0; i < aps.length; i++) {
			String channel = cluster[i] == 0 ? "?" : ""
					+ channels[getAutoChannel(aps[i], useA)];
			log.info_non(aps[i].hostName + " -> " + channel);
		}
	}

	public static void show(double[][] a, String name) {
		log.info_ln(name + " = [");
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				String fmt = "%4$8.2f";
				log.info_non(String.format(fmt, name, i, j, a[i][j]));
			}
			log.info_ln(";");
		}
		log.info_ln("];");
	}

	public static void show(long[][] a, String name) {
		log.info_ln(name + " = [");
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < a[i].length; j++) {
				String fmt = "%4$14d";
				log.info_non(String.format(fmt, name, i, j, a[i][j]));
			}
			log.info_ln(";");
		}
		log.info_ln("];");
	}

	public static void show(long[] a, String name) {
		log.info_non(name + " = [");
		for (int i = 0; i < a.length; i++) {
			String fmt = "%3$14d";
			log.info_non(String.format(fmt, name, i, a[i]));
		}
		log.info_ln(";];");
	}

	public static void show(short[] a, String name) {
		log.info_non(name + " = [");
		for (int i = 0; i < a.length; i++) {
			String fmt = "%3$14d";
			log.info_non(String.format(fmt, name, i, a[i]));
		}
		log.info_ln(";];");
	}

	private long getTotalCost(PlannedAP aps[], short[] channels,
			short[] assignment) {
		long ca[][] = new long[aps.length][channels.length];
		for (int i = 0; i < aps.length; i++) {
			ca[i][assignment[i]] = 1;
		}
		int totalCost = 0;
		for (int i = 0; i < aps.length; i++) {
			for (int j = 0; j < channels.length; j++) {
				long wij = 0;
				for (int k = 0; k < aps.length; k++) {
					wij += aps[i].inNetworkCost[k] * ca[k][j];
				}
				totalCost += wij * ca[i][j];
			}
		}
		return totalCost;
	}

	private void searchAssignments(PlannedAP aps[], short[] channels,
			long costThreshold, boolean verbose) {
		Date start = new Date();
		short[] assignments = new short[aps.length];
		long totalCount = 0, matchingCount = 0, betterCount = 0;
		long bestScore = Long.MAX_VALUE;
		while (true) {
			long score = getTotalCost(aps, assignments, costThreshold);
			if (score <= costThreshold) {
				if (score == costThreshold) {
					matchingCount++;
				} else {
					betterCount++;
					if (score < bestScore) {
						bestScore = score;
					}
				}
				if (verbose) {
					String matching = "% matching";
					if (score == costThreshold) {
					} else {
						matching = "% better";
					}
					show(assignments, matching + " score (" + score + "): ");
				}
			}
			totalCount++;
			if (totalCount % 10000000 == 0) {
				showScore(totalCount, matchingCount, betterCount,
						costThreshold, bestScore, start);
			}
			int digit = aps.length - 1;
			while (digit >= 0 && assignments[digit] == channels.length - 1) {
				assignments[digit--] = 0;
			}
			if (digit < 0) {
				break;
			}
			assignments[digit]++;
		}
		showScore(totalCount, matchingCount, betterCount, costThreshold,
				bestScore, start);
	}

	private static void fetchPowerChannelConfig(MapLeafNode mapLeafNode) {
		List<Object[]> hiveAps = (List<Object[]>) QueryUtil
				.executeQuery(
						"select wifi0.radioMode, wifi0.channel, wifi0.power, wifi1.radioMode, wifi1.channel, wifi1.power, wifi0.adminState, wifi1.adminState from "
								+ HiveAp.class.getSimpleName(), null,
						new FilterParams("macAddress", mapLeafNode.getApId()),
						mapLeafNode.getOwner().getId());
		log.debug("fetchPowerChannelConfig", "# hiveAps: " + hiveAps.size());
		for (Object[] hiveAp : hiveAps) {
			if (hiveAp.length == 8) {
				Short wifi0RadioMode = (Short) hiveAp[0];
				Integer wifi0Channel = (Integer) hiveAp[1];
				Integer wifi0Power = (Integer) hiveAp[2];
				Short wifi1RadioMode = (Short) hiveAp[3];
				Integer wifi1Channel = (Integer) hiveAp[4];
				Integer wifi1Power = (Integer) hiveAp[5];
				Short wifi0AdminState = (Short) hiveAp[6];
				Short wifi1AdminState = (Short) hiveAp[7];
				log.debug("fetchPowerChannelConfig", "Channel/Power settings: "
						+ wifi0RadioMode + ", " + wifi0Channel + ", "
						+ wifi0Power + ", " + wifi1RadioMode + ", "
						+ wifi1Channel + ", " + wifi1Power);
				setPowerChannelConfig(mapLeafNode, wifi0RadioMode,
						wifi0Channel, wifi0Power, wifi0AdminState);
				setPowerChannelConfig(mapLeafNode, wifi1RadioMode,
						wifi1Channel, wifi1Power, wifi1AdminState);
			}
		}
	}

	private static void setPowerChannelConfig(MapLeafNode mapLeafNode,
			short radioMode, int channel, int power, Short adminState) {
		if (radioMode == HiveApWifi.RADIO_MODE_A
				|| radioMode == HiveApWifi.RADIO_MODE_NA
				|| radioMode == HiveApWifi.RADIO_MODE_AC) {
			mapLeafNode.setAutoChannelA(channel == HiveApWifi.CHANNEL_A_AUTO);
			mapLeafNode.setAutoTxPowerA(power == HiveApWifi.POWER_AUTO);
			if (adminState == AhInterface.ADMIN_STATE_DOWM) {
				mapLeafNode.setRadioTxPowerA((short) 0);
				mapLeafNode.setRadioChannelA((short) 0);
			}
		} else if (radioMode == HiveApWifi.RADIO_MODE_BG
				|| radioMode == HiveApWifi.RADIO_MODE_NG) {
			mapLeafNode.setAutoChannelBG(channel == HiveApWifi.CHANNEL_BG_AUTO);
			mapLeafNode.setAutoTxPowerBG(power == HiveApWifi.POWER_AUTO);
			if (adminState == AhInterface.ADMIN_STATE_DOWM) {
				mapLeafNode.setRadioTxPowerBG((short) 0);
				mapLeafNode.setRadioChannelBG((short) 0);
			}
		}
	}

	public Map<String, MapLeafNode> fetchRadioAttributes(
			Collection<MapNode> nodes) {
		Map<String, MapLeafNode> leafNodes = new HashMap<String, MapLeafNode>(
				nodes.size());
		for (MapNode mapNode : nodes) {
			if (mapNode.isLeafNode()) {
				MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
				leafNodes.put(mapLeafNode.getApId(), mapLeafNode);
			}
		}
		filledChannelPowers(leafNodes);
		return leafNodes;
	}

	private static void filledChannelPowers(Map<String, MapLeafNode> leafNodes) {
		if (!leafNodes.isEmpty()) {
			List<?> radioList = QueryUtil.executeQuery(AhLatestXif.class, null,
					new FilterParams("apMac", leafNodes.keySet()));
			List<?> attributeList = QueryUtil.executeQuery(
					AhLatestRadioAttribute.class, null, new FilterParams(
							"apMac", leafNodes.keySet()));
			Map<String, AhLatestXif> indexNameMapping = new HashMap<String, AhLatestXif>(
					radioList.size());
			for (Object object : radioList) {
				AhLatestXif xif = (AhLatestXif) object;
				String mac = xif.getApMac();
				indexNameMapping.put(mac + xif.getIfIndex(), xif);
			}
			for (Object object : attributeList) {
				AhLatestRadioAttribute attributes = (AhLatestRadioAttribute) object;
				String mac = attributes.getApMac();
				long channel = attributes.getRadioChannel();
				long power = attributes.getRadioTxPower();
				float eirp = attributes.getEirp();
				String wifiName ="";
				byte ifMode = 0;
				AhLatestXif xif=indexNameMapping.get(mac + attributes.getIfIndex());
				if(null!=xif){
					wifiName=xif.getIfName();
					ifMode=xif.getIfMode();
				}
				log.debug("filledChannelPowers",
						"ifIndex: " + attributes.getIfIndex() + ", channel: "
								+ channel + ", power: " + power + ", eirp: "
								+ eirp + ", ifName: " + wifiName);
				//fix bug 32858, don't show channel value on sensor mode
				if (channel == 0 || power == 0
						|| ifMode==AhXIf.IFMODE_SENSOR) {
					continue;
				}
				if ("wifi0".equalsIgnoreCase(wifiName)
						|| "wifi1".equalsIgnoreCase(wifiName)) {
					if (channel > 15) {
						// a
						leafNodes.get(mac).setRadioChannelA((short) channel);
						leafNodes.get(mac).setRadioTxPowerA((short) power);
						leafNodes.get(mac).setRadioEirpA((short) eirp);
					} else {
						// b/g
						leafNodes.get(mac).setRadioChannelBG((short) channel);
						leafNodes.get(mac).setRadioTxPowerBG((short) power);
						leafNodes.get(mac).setRadioEirpBG((short) eirp);
					}
				}
				fetchPowerChannelConfig(leafNodes.get(mac));
			}
		}
	}

	public short[][] addChannelBoundaries(short oldColors[][],
			short mapChannels[][]) throws Exception {
		int imageWidth = oldColors.length;
		int imageHeight = oldColors[0].length;
		short[][] mapColors = new short[imageWidth][imageHeight];
		short bci = (short) (rssiColors.length - 1);
		for (int x = 0; x < imageWidth; x++) {
			mapColors[x][0] = oldColors[x][0];
		}
		for (int y = 1; y < imageHeight; y++) {
			mapColors[0][y] = oldColors[0][y];
		}
		for (int x = 1; x < imageWidth; x++) {
			for (int y = 1; y < imageHeight; y++) {
				mapColors[x][y] = oldColors[x][y];
				if (mapChannels[x][y] != mapChannels[x][y - 1]) {
					if (mapColors[x][y] >= 0 && mapChannels[x][y - 1] >= 0) {
						mapColors[x][y] = bci;
						if (imageWidth > 500) {
							mapColors[x][y - 1] = bci;
						}
					}
				}
				if (mapChannels[x][y] != mapChannels[x - 1][y]) {
					if (mapColors[x][y] >= 0 && mapChannels[x - 1][y] >= 0) {
						mapColors[x][y] = bci;
						if (imageWidth > 500) {
							mapColors[x - 1][y] = bci;
						}
					}
				}
			}
		}
		return mapColors;
	}

	public BufferedImage drawRssiArea(MapContainerNode mapContainerNode,
			int canvasWidth, int canvasHeight) throws Exception {
		BufferedImage image = new BufferedImage(canvasWidth, canvasHeight,
				BufferedImage.TYPE_INT_ARGB);
		if (mapContainerNode.area == null) {
			return image;
		}
		double canvasWidth_d = canvasWidth;
		double metricToCanvas_x = canvasWidth_d
				/ mapContainerNode.getActualWidthMetric();
		double canvasHeight_d = canvasHeight;
		double metricToCanvas_y = canvasHeight_d
				/ mapContainerNode.getActualHeightMetric();
		double[][] hma = mapContainerNode.area;
		double gridXsize = mapContainerNode.gridXsize;
		double gridYsize = mapContainerNode.gridYsize;
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		g2.setColor(new Color(0, 180, 247));
		for (int i = 1; i < hma.length - 1; i++) {
			for (int j = 1; j < hma[i].length - 1; j++) {
				if (hma[i][j] == 0) {
					continue;
				}
				double left = gridXsize * (i - 1.0);
				double right = left + gridXsize;
				if (right > mapContainerNode.getActualWidthMetric()) {
					right = mapContainerNode.getActualWidthMetric();
				}
				double top = gridYsize * (j - 1.0);
				double bottom = top + gridYsize;
				if (bottom > mapContainerNode.getActualHeightMetric()) {
					bottom = mapContainerNode.getActualHeightMetric();
				}
				double left_canvas_d = metricToCanvas_x * left;
				double right_canvas_d = metricToCanvas_x * right;
				double top_canvas_d = metricToCanvas_y * top;
				double bottom_canvas_d = metricToCanvas_y * bottom;
				int area_x = (int) left_canvas_d;
				int area_y = (int) top_canvas_d;
				int area_w = (int) (right_canvas_d - left_canvas_d + 1.0);
				int area_h = (int) (bottom_canvas_d - top_canvas_d + 1.0);
				g2.fillRect(area_x, area_y, area_w, area_h);
			}
		}
		return image;
	}

	public BufferedImage drawRssiImage(short mapColors[][],
			short shadesPerColor, int imgScale) throws Exception {
		int imageWidth = mapColors.length;
		int imageHeight = mapColors[0].length;
		short bci = (short) (rssiColors.length - 1);
		Date start = new Date();
		BufferedImage image = new BufferedImage(imageWidth * imgScale,
				imageHeight * imgScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex < shadesPerColor || colorIndex == bci) {
					if (colorIndex >= 0) {
						g2.setColor(rssiColors[colorIndex]);
						g2.fillRect(x * imgScale, y * imgScale, imgScale,
								imgScale);
					}
				}
			}
		}
		Date end = new Date();
		logt.debug("drawRssiImage", "draw RSSI image: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return image;
	}

	public BufferedImage drawSnrImage(short mapColors[][],
			short shadesPerColor, int fadeMargin, int imgScale)
			throws Exception {
		int imageWidth = mapColors.length;
		int imageHeight = mapColors[0].length;
		Date start = new Date();
		BufferedImage image = new BufferedImage(imageWidth * imgScale,
				imageHeight * imgScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex >= 0 && colorIndex < shadesPerColor) {
					// fadeMargin is reflected in shadesperColor, so adding to
					// colorIndex will not cause out of bounds
					colorIndex += fadeMargin;
					g2.setColor(rssiColors[colorIndex]);
					g2.fillRect(x * imgScale, y * imgScale, imgScale, imgScale);
				}
			}
		}
		Date end = new Date();
		logt.debug("drawSnrImage",
				"draw SNR image: " + (end.getTime() - start.getTime()) + " ms.");
		return image;
	}

	public BufferedImage drawInterferenceImage(short mapColors[][],
			short mapChannels[][], short apInterference[], short shadesPerColor)
			throws Exception {
		Color[][] severityColors = initColorShades(startSeverityColors,
				endSeverityColors, startSeverityColors.length, shadesPerColor);
		short bci = (short) (rssiColors.length - 1);
		int imageWidth = mapColors.length;
		int imageHeight = mapColors[0].length;
		Date start = new Date();
		BufferedImage image = new BufferedImage(imageWidth, imageHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex >= 0 && colorIndex < shadesPerColor) {
					short apIndex = mapChannels[x][y];
					if (apIndex >= 0) {
						short severityColor = apInterference[apIndex];
						if (severityColor > 0 && severityColor < 5) {
							g2.setColor(severityColors[severityColor - 1][colorIndex]);
							// g2.setColor(startSeverityColors[severityColor -
							// 1]);
							g2.fillRect(x, y, 1, 1);
						}
					}
				} else if (colorIndex == bci) {
					g2.setColor(rssiColors[colorIndex]);
					g2.fillRect(x, y, 1, 1);
				}
			}
		}
		Date end = new Date();
		logt.info("drawInterferenceImage",
				"draw Interference image: " + (end.getTime() - start.getTime())
						+ " ms.");
		return image;
	}

	public BufferedImage drawChannelImage(short mapColors[][],
			short mapChannels[][], short apChannel[], short channelColor,
			short shadesPerColor, int imgScale) throws Exception {
		Color[][] channelColors = initColorShades(startChannelColors,
				endChannelColors, startChannelColors.length, shadesPerColor);
		Map<Short, Short> channelColorMap = null;
		if (apChannel != null) {
			channelColorMap = createColorMap(apChannel);
		}
		int imageWidth = mapColors.length;
		int imageHeight = mapColors[0].length;
		Date start = new Date();
		BufferedImage image = new BufferedImage(imageWidth * imgScale,
				imageHeight * imgScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex >= 0 && colorIndex < shadesPerColor) {
					short channelIndex = mapChannels[x][y];
					if (channelIndex >= 0) {
						if (channelColorMap != null && channelColorMap.size() > 0) {
							channelColor = channelColorMap
									.get(apChannel[channelIndex]);
						}
						if(channelColor >= 0){
							g2.setColor(channelColors[channelColor][colorIndex]);
						}
						
						g2.fillRect(x * imgScale, y * imgScale, imgScale,
								imgScale);
					}
				}
			}
		}
		Date end = new Date();
		logt.debug("drawChannelImage", "draw Channel image: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return image;
	}

	public BufferedImage drawChannelImage(short mapColors[][],
			short mapChannels[][], short chis[], short shadesPerColor,
			int imgScale) throws Exception {
		Color[][] channelColors = initColorShades(startChannelColors,
				endChannelColors, startChannelColors.length, shadesPerColor);
		int imageWidth = mapColors.length;
		int imageHeight = mapColors[0].length;
		Date start = new Date();
		BufferedImage image = new BufferedImage(imageWidth * imgScale,
				imageHeight * imgScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex >= 0 && colorIndex < shadesPerColor) {
					short apIndex = mapChannels[x][y];
					if (apIndex >= 0) {
						short channelColor = chis[apIndex];
						g2.setColor(channelColors[channelColor][colorIndex]);
						g2.fillRect(x * imgScale, y * imgScale, imgScale,
								imgScale);
					}
				}
			}
		}
		Date end = new Date();
		logt.debug("drawChannelImage", "draw Channel image: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return image;
	}

	public static final short dnma92_gg[][] = { { 6, -90 }, { 9, -90 },
			{ 12, -89 }, { 18, -88 }, { 24, -85 }, { 36, -82 }, { 48, -78 },
			{ 54, -76 } };

	public static final short dnma92_aa[][] = { { 6, -94 }, { 9, -94 },
			{ 12, -93 }, { 18, -91 }, { 24, -87 }, { 36, -84 }, { 48, -80 },
			{ 54, -77 } };

	public static final short dnma92_g[][] = { { 1, -92 }, { 6, -91 },
			{ 9, -90 }, { 12, -89 }, { 18, -88 }, { 24, -85 }, { 36, -82 },
			{ 48, -78 }, { 54, -76 } };

	public static final short dnma92_a[][] = { { 1, -94 }, { 6, -93 },
			{ 9, -92 }, { 12, -91 }, { 18, -90 }, { 24, -87 }, { 36, -84 },
			{ 48, -80 }, { 54, -77 } };

	public static final short dnma92_gn_ht20[][] = { { 1, -92 },
			{ 6, -91 } /* ly */, { 13, -90 }, { 26, -89 }, { 39, -87 },
			{ 52, -84 }, { 78, -81 }, { 104, -76 }, { 117, -75 }, { 130, -73 } };

	public static final short dnma92_gn_ht40[][] = { { 1, -92 }, { 6, -91 },
			{ 9, -90 }, { 12, -89 } /* ly */, { 27, -86 }, { 54, -86 },
			{ 81, -83 }, { 108, -81 }, { 162, -78 }, { 216, -73 },
			{ 247, -73 }, { 270, -70 } };

	public static final short dnma92_an_ht20[][] = { { 1, -94 },
			{ 6, -93 } /* ly */, { 13, -94 }, { 26, -91 }, { 39, -91 },
			{ 52, -86 }, { 78, -83 }, { 104, -79 }, { 117, -77 }, { 130, -75 } };

	public static final short dnma92_an_ht40[][] = { { 1, -94 }, { 6, -93 },
			{ 9, -92 }, { 12, -91 } /* ly */, { 27, -90 }, { 54, -88 },
			{ 81, -86 }, { 108, -83 }, { 162, -79 }, { 216, -76 },
			{ 247, -74 }, { 270, -72 } };

	public static final short mb82_g[][] = { { 1, -92 }, { 6, -91 },
			{ 9, -91 }, { 12, -91 }, { 18, -90 }, { 24, -87 }, { 36, -84 },
			{ 48, -80 }, { 54, -78 } };

	public static final short mb82_a[][] = { { 1, -91 }, { 6, -90 },
			{ 9, -90 }, { 12, -90 }, { 18, -89 }, { 24, -85 }, { 36, -82 },
			{ 48, -78 }, { 54, -77 } };

	public static final short mb82_gn_ht20[][] = { { 1, -92 },
			{ 6, -91 } /* ly */, { 13, -90 }, { 26, -89 }, { 39, -87 },
			{ 52, -84 }, { 78, -81 }, { 104, -76 }, { 117, -74 }, { 130, -72 } };

	public static final short mb82_gn_ht40[][] = { { 1, -92 }, { 6, -91 },
			{ 9, -90 }, { 12, -89 } /* ly */, { 27, -85 }, { 54, -84 },
			{ 81, -83 }, { 108, -81 }, { 162, -78 }, { 216, -73 },
			{ 247, -71 }, { 270, -68 } };

	public static final short mb82_an_ht20[][] = { { 1, -91 },
			{ 6, -90 } /* ly */, { 13, -89 }, { 26, -88 }, { 39, -86 },
			{ 52, -82 }, { 78, -79 }, { 104, -75 }, { 117, -74 }, { 130, -71 } };

	public static final short mb82_an_ht40[][] = { { 1, -91 }, { 6, -90 },
			{ 9, -90 }, { 12, -89 } /* ly */, { 27, -86 }, { 54, -84 },
			{ 81, -82 }, { 108, -79 }, { 162, -76 }, { 216, -72 },
			{ 247, -70 }, { 270, -67 } };

	public static final short mb11_gn_ht20[][] = { { 1, -94 },
			{ 6, -93 } /* ly */, { 19, -92 }, { 39, -91 }, { 58, -89 },
			{ 78, -86 }, { 117, -82 }, { 156, -77 }, { 175, -75 }, { 195, -73 } };

	public static final short mb11_gn_ht40[][] = { { 1, -94 }, { 6, -93 },
			{ 9, -92 }, { 12, -91 } /* ly */, { 40, -87 }, { 81, -86 },
			{ 121, -85 }, { 162, -83 }, { 247, -79 }, { 324, -74 },
			{ 364, -72 }, { 405, -69 } };

	public static final short mb11_an_ht20[][] = { { 1, -93 },
			{ 6, -92 } /* ly */, { 19, -91 }, { 39, -90 }, { 58, -88 },
			{ 78, -84 }, { 117, -80 }, { 156, -76 }, { 175, -75 }, { 195, -72 } };

	public static final short mb11_an_ht40[][] = { { 1, -94 }, { 6, -93 },
			{ 9, -92 }, { 12, -91 } /* ly */, { 40, -88 }, { 81, -86 },
			{ 121, -84 }, { 162, -81 }, { 247, -77 }, { 324, -73 },
			{ 364, -71 }, { 405, -68 } };

	public static final short mll_gn_ht20[][] = { { 1, -95 }, { 6, -94 },
			{ 21, -93 }, { 43, -92 }, { 65, -90 }, { 86, -87 }, { 130, -84 },
			{ 173, -81 }, { 195, -79 }, { 216, -77 } };

	public static final short mll_gn_ht40[][] = { { 1, -95 }, { 6, -93 },
			{ 45, -91 }, { 90, -89 }, { 135, -87 }, { 180, -84 }, { 270, -81 },
			{ 360, -78 }, { 405, -76 }, { 450, -74 } };

	public static final short mll_an_ht20[][] = { { 1, -95 }, { 6, -93 },
			{ 21, -91 }, { 43, -89 }, { 65, -87 }, { 86, -84 }, { 130, -81 },
			{ 173, -78 }, { 195, -76 }, { 216, -74 } };

	public static final short mll_an_ht40[][] = { { 1, -95 }, { 6, -93 },
			{ 21, -91 }, { 45, -88 }, { 90, -86 }, { 135, -84 }, { 180, -81 },
			{ 270, -78 }, { 360, -75 }, { 405, -73 }, { 450, -71 } };

	public static final short mll_ac_ht80[][] = { { 1, -95 }, { 6, -93 },
			{ 21, -91 }, { 45, -88 }, { 90, -86 }, { 135, -84 }, { 180, -81 },
			{ 270, -78 }, { 360, -75 }, { 405, -73 }, { 450, -71 },
			{ 3 * 180, -62 }, { 3 * 200, -60 }, { 3 * 390, -59 },
			{ 3 * 433, -57 } };

	private static void showRatesSpec(String desc, short ratesSpec[][]) {
		log.info_non("% " + desc);
		for (int i = 0; i < ratesSpec.length; i++) {
			log.info_non(" (" + ratesSpec[i][0] + ", " + (ratesSpec[i][1])
					+ ")");
		}
		log.info_ln(";");
	}

	public int getRateRssiThreshold(int rate, boolean useA, short channelWidth,
			int fadeMargin) {
		int rssiThreshold = -50;
		if (useA) {
			rssiThreshold = getRateRssiThreshold(rate, dnma92_a, rssiThreshold);
			if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) {
				rssiThreshold = getRateRssiThreshold(rate, mb11_an_ht20,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mb82_an_ht20,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, dnma92_an_ht20,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mll_an_ht20,
						rssiThreshold);
			} else {
				rssiThreshold = getRateRssiThreshold(rate, mb11_an_ht40,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mb82_an_ht40,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, dnma92_an_ht40,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mll_an_ht40,
						rssiThreshold);
				if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80) {
					rssiThreshold = getRateRssiThreshold(rate, mll_ac_ht80,
							rssiThreshold);
				}
			}
		} else {
			rssiThreshold = getRateRssiThreshold(rate, dnma92_g, rssiThreshold);
			if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) {
				rssiThreshold = getRateRssiThreshold(rate, mb11_gn_ht20,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mb82_gn_ht20,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, dnma92_gn_ht20,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mll_gn_ht20,
						rssiThreshold);
			} else {
				rssiThreshold = getRateRssiThreshold(rate, mb11_gn_ht40,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mb82_gn_ht40,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, dnma92_gn_ht40,
						rssiThreshold);
				rssiThreshold = getRateRssiThreshold(rate, mll_gn_ht40,
						rssiThreshold);
			}
		}
		rssiThreshold += fadeMargin;
		if (rssiThreshold < -90) { // cap at -90
			rssiThreshold = -90;
		}
		return rssiThreshold;
	}

	private int getRateRssiThreshold(int rate, short rateSpec[][],
			int rateRssiThreshold) {
		for (int i = 0; i < rateSpec.length; i++) {
			if (rateSpec[i][0] >= rate) {
				int newThreshold;
				if (i == 0 || rateSpec[i][0] == rate) {
					newThreshold = rateSpec[i][1];
				} else {
					newThreshold = rateSpec[i - 1][1];
				}
				log.info_non("new threshold: " + newThreshold);
				if (newThreshold < rateRssiThreshold) {
					rateRssiThreshold = newThreshold;
				}
				log.info_non("updated rateRssiThreshold: " + rateRssiThreshold);
				return rateRssiThreshold;
			}
		}
		if (rateSpec[rateSpec.length - 1][1] < rateRssiThreshold) {
			rateRssiThreshold = rateSpec[rateSpec.length - 1][1];
		}
		return rateRssiThreshold;
	}

	private void setRateColors(short ratesSpec[][], short rateColors[],
			int fadeMargin, short channelWidth, int rateThreshold) {
		if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20) {
			setRateColorsHT20(ratesSpec, rateColors, fadeMargin, rateThreshold);
		} else if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80) {
			setRateColorsHT80(ratesSpec, rateColors, fadeMargin, rateThreshold);
		} else {
			setRateColorsHT40(ratesSpec, rateColors, fadeMargin, rateThreshold);
		}
	}

	private void setRateColorsHT20(short ratesSpec[][], short rateColors[],
			int fadeMargin, int rateThreshold) {
		short bit_rates[] = { 1, 6, 12, 18, 24, 36, 52, 78, 104, 117, 130 };
		int rateIndex = 0;
		int rate = bit_rates[rateIndex];
		short colorIndex = 55;
		short[] rateMapping = new short[colorIndex + 1];
		double fromRate = 0;
		for (short i = colorIndex; i >= 0; i--) {
			if (fromRate + 1.5 > rate) {
				if (rateIndex + 1 < bit_rates.length) {
					rate = bit_rates[++rateIndex];
					colorIndex = i == 13 ? 14 : i;
				} else {
					colorIndex = 3;
				}
			}
			rateMapping[i] = colorIndex;
			fromRate += 2.5;
		}
		rateMapping[24] = 34;
		rateMapping[34] = 41;
		rateMapping[51] = 50;
		rateMapping[54] = 53;
		// logVector("HT20 rate mapping (" + rateThreshold + "): ",
		// rateMapping);
		this.setRateColors(ratesSpec, rateColors, fadeMargin, bit_rates,
				rateMapping, rateThreshold);
	}

	private void setRateColorsHT40(short ratesSpec[][], short rateColors[],
			int fadeMargin, int rateThreshold) {
		short bit_rates[] = { 1, 6, 12, 18, 24, 27, 36, 48, 54, 81, 108, 130,
				162, 216, 247, 270 };
		int rateIndex = 0;
		int rate = bit_rates[rateIndex];
		short colorIndex = 55;
		short[] rateMapping = new short[colorIndex + 1];
		double fromRate = 0;
		for (short i = colorIndex; i >= 0; i--) {
			if (fromRate + 4 > rate) {
				if (rateIndex + 1 < bit_rates.length) {
					rate = bit_rates[++rateIndex];
					colorIndex = i == 6 ? 8 : i == 12 ? 14 : i;
				} else {
					colorIndex = 3;
				}
			}
			rateMapping[i] = colorIndex;
			fromRate += 5;
		}
		// logVector("HT40 rate mapping (" + rateThreshold + "): ",
		// rateMapping);
		this.setRateColors(ratesSpec, rateColors, fadeMargin, bit_rates,
				rateMapping, rateThreshold);
	}

	/*
	 * rateMapping distributes the list of bit_rates rates across the RSSI
	 * colors range. rateMapping[0] holds the color for the highest rate.
	 */
	private void setRateColorsHT80(short ratesSpec[][], short rateColors[],
			int fadeMargin, int rateThreshold) {
		short bit_rates[] = { 1, 6, 24, 54, 81, 108, 130, 162, 216, 260, 390,
				520, 650, 780, 910, 1040, 1170 };
		int rateIndex = 3;
		int rate = bit_rates[rateIndex];
		short colorIndex = 55;
		short[] rateMapping = new short[colorIndex + 1];
		rateMapping[colorIndex] = colorIndex--;
		rateMapping[colorIndex] = colorIndex--;
		rateMapping[colorIndex] = colorIndex--;
		double fromRate = 66;
		for (short i = colorIndex; i >= 0; i--) {
			if (fromRate + 20 > rate) {
				if (rateIndex + 1 < bit_rates.length) {
					rate = bit_rates[++rateIndex];
					colorIndex = i;
				} else {
					colorIndex = 3;
				}
			}
			rateMapping[i] = colorIndex;
			fromRate += 22;
		}
		this.setRateColors(ratesSpec, rateColors, fadeMargin, bit_rates,
				rateMapping, rateThreshold);
	}

	private void setRateColors(short ratesSpec[][], short rateColors[],
			int fadeMargin, short bit_rates[], short[] rateMapping,
			int rateThreshold) {
		int rateIndex = 0;
		int rate = bit_rates[rateIndex];
		short colorIndex = 55;
		rateIndex = bit_rates.length - 1;
		rate = bit_rates[rateIndex];
		int rateSpecIndex = ratesSpec.length - 1;
		int rateSpec = ratesSpec[rateSpecIndex][0];
		colorIndex = rateMapping[0];
		for (short i = 0; i < 56; i++) {
			if (rateMapping[i] != colorIndex) {
				rate = bit_rates[--rateIndex];
				colorIndex = rateMapping[i];
			}
			log.debug("% rate: " + rate + ", rateColor: " + colorIndex
					+ ", at rssi: " + (-i - 35));
			if (rate < rateThreshold) {
				break;
			}
			// Any rate equal or higher than rateSpec should
			// use rateColor colorIndex
			while (rateSpec >= rate) {
				int rssi = ratesSpec[rateSpecIndex][1] + fadeMargin;
				int rssiColor = -35 - rssi;
				if (rssiColor > 55) {
					log.debug("% adjusting rssiColor: " + rssiColor + " to 55");
					// If this rate is supported at a level below -90 dBm,
					// then at least at -90 dBm as well.
					rssiColor = 55;
				}
				rateColors[rssiColor] = (short) colorIndex;
				log.debug("% rssi: " + rssi + " (" + rateSpec
						+ " Mbps) or rssi color " + rssiColor
						+ " maps to rate color index: " + colorIndex);
				if (rateSpecIndex-- == 0) {
					break;
				}
				rateSpec = ratesSpec[rateSpecIndex][0];
			}
			if (rateSpecIndex < 0) {
				break;
			}
		}
		short rateColor = -1;
		for (short rssiColor = 55; rssiColor >= 0; rssiColor--) {
			if (rateColors[rssiColor] == 0) {
				rateColors[rssiColor] = rateColor;
			} else {
				rateColor = rateColors[rssiColor];
			}
		}
	}

	private void logVector(String n, short[] v) {
		log.info_non("% " + n + " = [" + v[0]);
		for (int i = 1; i < v.length; i++) {
			log.info_non(", " + v[i]);
		}
		log.info_ln("];");
	}

	public BufferedImage drawRatesImage(short mapColors[][], int rateThreshold,
			short apModel, int fadeMargin, short channelWidth,
			short shadesPerColor, boolean useA, int imgScale) throws Exception {
		short rateColors[] = new short[56];
		short rateSpec[][];
		switch (apModel) {
		case HiveAp.HIVEAP_MODEL_20:
		case HiveAp.HIVEAP_MODEL_28:
			rateSpec = useA ? dnma92_a : dnma92_g;
			break;
		case HiveAp.HIVEAP_MODEL_350:
		case HiveAp.HIVEAP_MODEL_330:
			if (useA) {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? mb11_an_ht20
						: mb11_an_ht40;
			} else {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? mb11_gn_ht20
						: mb11_gn_ht40;
			}
			break;
		case HiveAp.HIVEAP_MODEL_340:
		case HiveAp.HIVEAP_MODEL_320:
			if (useA) {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? mb82_an_ht20
						: mb82_an_ht40;
			} else {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? mb82_gn_ht20
						: mb82_gn_ht40;
			}
			break;
		case HiveAp.HIVEAP_MODEL_170:
		case HiveAp.HIVEAP_MODEL_120:
		case HiveAp.HIVEAP_MODEL_110:
		case HiveAp.HIVEAP_MODEL_BR100:
		case HiveAp.HIVEAP_MODEL_BR200_WP:
		case HiveAp.HIVEAP_MODEL_BR200_LTE_VZ:
		case HiveAp.HIVEAP_MODEL_121:
		case HiveAp.HIVEAP_MODEL_141:
			if (useA) {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? dnma92_an_ht20
						: dnma92_an_ht40;
			} else {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? dnma92_gn_ht20
						: dnma92_gn_ht40;
			}
			break;
		case HiveAp.HIVEAP_MODEL_370:
		case HiveAp.HIVEAP_MODEL_390:
        case HiveAp.HIVEAP_MODEL_230:
			if (useA) {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? mll_an_ht20
						: channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80 ? mll_ac_ht80
								: mll_an_ht40;
			} else {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? mll_gn_ht20
						: mll_gn_ht40;
			}
			break;
		default:
			if (useA) {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? dnma92_an_ht20
						: dnma92_an_ht40;
			} else {
				rateSpec = channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20 ? dnma92_gn_ht20
						: dnma92_gn_ht40;
			}
		}
		setRateColors(rateSpec, rateColors, fadeMargin, channelWidth,
				rateThreshold);
		int imageWidth = mapColors.length;
		int imageHeight = mapColors[0].length;
		Date start = new Date();
		BufferedImage image = new BufferedImage(imageWidth * imgScale,
				imageHeight * imgScale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex < shadesPerColor) {
					if (colorIndex >= 0) {
						colorIndex = rateColors[colorIndex];
						if (colorIndex >= 0) {
							g2.setColor(rssiColors[colorIndex]);
							g2.fillRect(x * imgScale, y * imgScale, imgScale,
									imgScale);
						}
					}
				}
			}
		}
		Date end = new Date();
		logt.debug("drawRatesImage", "draw Data Rates image: "
				+ (end.getTime() - start.getTime()) + " ms.");
		return image;
	}

	private Map<Short, Short> createColorMap(short apChannel[]) {
		Map<Short, Short> colorMap = new HashMap<Short, Short>();
		short color = 0;
		for (int i = 0; i < apChannel.length; i++) {
			if (apChannel[i] > 0) {
				if (colorMap.get(apChannel[i]) == null) {
					colorMap.put(apChannel[i], color++);
				}
			}
		}
		logt.info("createColorMap", "Color map size: " + colorMap.size());
		return colorMap;
	}

	private String toHex(int n) {
		String s = Integer.toHexString(n);
		return s.length() == 1 ? "0" + s : s;
	}

	public Collection<JSONObject> getRssiRange(short shadesPerColor)
			throws Exception {
		Color[][] channelColors = initColorShades(startChannelColors,
				endChannelColors, 1, shadesPerColor);
		Collection<JSONObject> rssiRange = new Vector<JSONObject>();
		for (int step = 0; step < shadesPerColor; step++) {
			Color c = channelColors[0][step];
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("rc", "#" + toHex(c.getRed()) + toHex(c.getGreen())
					+ toHex(c.getBlue()));
			rssiRange.add(jsonObj);
		}
		return rssiRange;
	}

	private static double PL_C_A = 20 * Math.log10(5500) - 28;

	private static double PL_C_BG = 20 * Math.log10(2400) - 28;

	private static double getPathLossFactor(int environment) {
		String plc = MgrUtil.getString("resources.hmConfig", "plc.mapEnv."
				+ environment);
		return Double.parseDouble(plc) * 10;
	}

	private double getPathLossFactor(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap, boolean useCalibratedLe) {
		if (mapContainerNode.getEnvironment() == EnumConstUtil.MAP_ENV_AUTO
				|| useCalibratedLe) {
			return mrHeatMap.lsle * 10;
		} else {
			return getPathLossFactor(mapContainerNode.getEnvironment());
		}
	}

	private static double getPathLossFactor(MapContainerNode mapContainerNode) {
		double plf = mle * 10;
		if (mapContainerNode.getEnvironment() != EnumConstUtil.MAP_ENV_AUTO) {
			plf = getPathLossFactor(mapContainerNode.getEnvironment());
		}
		return plf;
	}

	/*
	 * 5Ghz
	 */
	private double getPathLossA(double distance, double n) {
		return getPathLoss(distance, PL_C_A, n);
	}

	/*
	 * 2.4Ghz
	 */
	private double getPathLossBG(double distance, double n) {
		return getPathLoss(distance, PL_C_BG, n);
	}

	/*
	 * Distance has to be in meters
	 */
	private double getPathLoss(double distance, double c, double n) {
		return c + n * Math.log10(distance);
	}

	private static double getDistanceA(double pathLoss, double n) {
		return getDistance(pathLoss, PL_C_A, n);
	}

	private static double getDistanceBG(double pathLoss, double n) {
		return getDistance(pathLoss, PL_C_BG, n);
	}

	/*
	 * Always returns in metric
	 */
	private static double getDistance(double pathLoss, double c, double n) {
		double distance = Math.pow(10, (pathLoss - c) / n);
		return distance;
	}

	private boolean hasA(MapLeafNode mapLeafNode) {
		return mapLeafNode.getRadioChannelA() != 0
				&& mapLeafNode.getRadioTxPowerA() != 0;
	}

	private boolean hasBG(MapLeafNode mapLeafNode) {
		return mapLeafNode.getRadioChannelBG() != 0
				&& mapLeafNode.getRadioTxPowerBG() != 0;
	}

	/*
	 * Effective radiated power. Takes into account losses in the transmission
	 * line and connectors and the gain of the antenna
	 */
	private static double getERP(MapLeafNode mapLeafNode, boolean useA) {
		double power = useA ? mapLeafNode.getRadioTxPowerA() : mapLeafNode
				.getRadioTxPowerBG();
		return power
				+ getAverageGain(mapLeafNode.getHiveAp().getHiveApModel(), useA);
	}

	private static double getClientPowerLevel() {
		return startingTxPower;
	}

	private static short getChannel(MapLeafNode mapLeafNode, boolean useA) {
		if (useA) {
			return mapLeafNode.getRadioChannelA();
		} else {
			return mapLeafNode.getRadioChannelBG();
		}
	}

	/*
	 * Find location of all the clients on a map
	 */
	public Collection<JSONObject> locateClients(
			MapContainerNode mapContainerNode, Set<MapNode> nodes, Long pageId,
			double imageWidth, BufferedImage image, double scale)
			throws Exception {
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		logt.info("locateClients",
				"Use calibrated heat map: " + mapSettings.isUseHeatmap());
		Collection<JSONObject> clientLocations = new Vector<JSONObject>();
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		logt.info("locateClients", "# AP macs: " + nodesMap.keySet().size());

		if (nodesMap.keySet().size() == 0) {
			return clientLocations;
		}

		// CJS
		// createLocate(mapContainerNode, "0022410C44FE", "droid",
		// "00197730E8C0", 5600, new String[] { "00197730E8C0", "001977036740"
		// }, new int[] { -78, -60 }, -78);

		// createLocate(mapContainerNode, "0022410C66AC", "iPhone",
		// "00197703B640", 5600, new String[] { "0019770C8BC0", "00197703B640",
		// "00197730E8C0", "001977036740" }, new int[] { -60, -60, -46, -68 },
		// -60);
		// CJS

		Collection<String> clientMacs = (List<String>) QueryUtil.executeQuery(
				"select distinct clientMac from "
						+ LocationRssiReport.class.getSimpleName(), null,
				new FilterParams("reporterMac in (:s1) and rssi < 0",
						new Object[] { nodesMap.keySet() }));
		logt.info("locateClients", "# client macs: " + clientMacs.size());
		if (clientMacs.size() == 0) {
			return clientLocations;
		}
		// Collection<Object[]> activeClients = (List<Object[]>) QueryUtil
		// .executeQuery("select distinct clientMac, clientHostname from "
		// + AhClientSession.class.getSimpleName(), null,
		// new FilterParams(
		// "clientMac in (:s1) and connectstate = :s2",
		// new Object[] { clientMacs,
		// AhClientSession.CONNECT_STATE_UP }));
		StringBuffer where = new StringBuffer();
		int i = 0;
		where.append("clientMac in (");
		for (i = 0; i < clientMacs.size(); i++) {
			where.append("?");
			if (i != clientMacs.size() - 1)
				where.append(",");
		}
		where.append(") and connectstate = ?");
		List<Object> paraList = new ArrayList<Object>();
		paraList.addAll(clientMacs);
		paraList.add(AhClientSession.CONNECT_STATE_UP);
		Collection<Object[]> activeClients = (List<Object[]>) DBOperationUtil
				.executeQuery(
						"select distinct clientMac, clientHostname from ah_clientsession",
						null,
						new FilterParams(where.toString(), paraList.toArray()));
		logt.info("locateClients",
				"# active client macs: " + activeClients.size());
		if (activeClients.size() == 0) {
			return clientLocations;
		}

		Graphics2D g2 = null;
		double metricToImage = 0;
		double mapToMetric = mapContainerNode.getMapToMetric();
		if (image != null) {
			g2 = image.createGraphics();
			g2.setStroke(new BasicStroke(2));
			metricToImage = imageWidth / mapContainerNode.getWidth()
					/ mapToMetric;
		}
		MrHeatMap heatMap_a = getHeatMap(mapContainerNode, nodes, 0, 0, true,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		MrHeatMap heatMap_g = getHeatMap(mapContainerNode, nodes, 0, 0, false,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		if (heatMap_a != null || heatMap_g != null) {
			Date start = new Date();
			locateClientMacs(mapContainerNode, nodesMap, heatMap_a, heatMap_g,
					mapSettings, pageId, g2, metricToImage, scale,
					activeClients, false, clientLocations, null, true, false);
			Date end = new Date();
			log.info_ln("% " + clientMacs.size() + " client macs in "
					+ (end.getTime() - start.getTime()) + " ms.");
		}
		return clientLocations;
	}

	private void createRogueLocate(MapContainerNode mapContainerNode,
			Map<String, MapLeafNode> nodesMap) {
		try {
			QueryUtil.removeBos(Idp.class, (FilterParams) null);
			long rowCount = QueryUtil.findRowCount(Idp.class, null);
			log.info_ln("% IDP count after removal: " + rowCount);
		} catch (Exception e) {
			log.info_ln("% IDP removal failed.");
		}
		Calendar c = Calendar.getInstance();
		short delta = 4;
		for (String apId : nodesMap.keySet()) {
			Idp idp = new Idp();
			idp.setOwner(mapContainerNode.getOwner());
			idp.setReportTime(HmTimeStamp.getTimeStamp(new Date().getTime(),
					(byte) ((c.get(Calendar.ZONE_OFFSET) + c
							.get(Calendar.DST_OFFSET)) / (60 * 60 * 1000))));
			idp.setIfMacAddress("0022410C63AD");
			idp.setRssi((short) (45 + delta++));
			idp.setIdpType(BeCommunicationConstant.IDP_TYPE_ROGUE);
			idp.setStationType(BeCommunicationConstant.IDP_STATION_TYPE_AP);
			idp.setReportNodeId(apId);
			try {
				QueryUtil.createBo(idp);
			} catch (Exception e) {
				log.info_ln("% Create IDP failed ...");
			}
		}
		long rowCount = QueryUtil.findRowCount(Idp.class, null);
		log.info_ln("% IDP count after re-create: " + rowCount);
	}

	private void createLocate(MapContainerNode mapContainerNode,
			String clientMac, String clientHostname, String apMac, int channel,
			String[] apMacs, int[] rssi, int clientRssi) {
		// List<AhClientSession> sessions = QueryUtil.executeQuery(
		// AhClientSession.class, null, new FilterParams("clientMac",
		// clientMac));
		List<AhClientSession> sessions = DBOperationUtil.executeQuery(
				AhClientSession.class, null, new FilterParams("clientMac",
						clientMac));
		log.info_ln("% Client session count: " + sessions.size()
				+ " for client: " + clientMac);
		try {
			// QueryUtil.removeBos(AhClientSession.class, new FilterParams(
			// "clientMac", clientMac));
			DBOperationUtil
					.executeUpdate("delete from ah_clientsession where clientmac = '"
							+ clientMac + "'");
			sessions = DBOperationUtil.executeQuery(AhClientSession.class,
					null, new FilterParams("clientMac", clientMac));
			log.info_ln("% Client session count after removal: "
					+ sessions.size() + " for client: " + clientMac);
			QueryUtil.removeBos(AhAssociation.class, new FilterParams(
					"clientMac", clientMac));
		} catch (Exception e) {
			log.info_ln("% Client session removal failed.");
		}

		AhClientSession clientSession = new AhClientSession();
		clientSession.setOwner(mapContainerNode.getOwner());
		clientSession.setApMac(apMac);
		clientSession.setApName("AH-" + apMac);
		clientSession.setApSerialNumber("112233");
		clientSession.setClientSSID("s1");
		clientSession.setClientMac(clientMac);
		clientSession.setClientBSSID("001977036760");
		clientSession.setClientIP("192.168.10.66");
		clientSession.setClientHostname(clientHostname);
		clientSession.setConnectstate(AhClientSession.CONNECT_STATE_UP);
		clientSession.setClientChannel(8);
		clientSession.setClientRssi(clientRssi);
		clientSession.setStartTimeStamp(new Date().getTime());
		clientSession
				.setClientAuthMethod(AhAssociation.CLIENTAUTHMETHOD_DYNAMICWEP);
		AhAssociation association = new AhAssociation();
		association.setOwner(mapContainerNode.getOwner());
		association.setClientMac(clientMac);
		association.setClientRSSI(clientRssi);
		HmTimeStamp timeStamp = new HmTimeStamp(new Date().getTime(), null);
		association.setTimeStamp(timeStamp);
		try {
			// QueryUtil.createBo(clientSession);
			List<AhClientSession> boList = new ArrayList<AhClientSession>();
			boList.add(clientSession);
			DBOperationUtil.executeUpdate(AhConvertBOToSQL
					.convertClientSessionToSQL(boList));

			QueryUtil.createBo(association);
		} catch (Exception e) {
			log.info_ln("% Create client session failed ...");
		}

		List<LocationRssiReport> reports = QueryUtil.executeQuery(
				LocationRssiReport.class, null, new FilterParams("clientMac",
						clientMac));
		log.info_ln("% Client rssi count: " + reports.size());
		try {
			QueryUtil.removeBos(LocationRssiReport.class, new FilterParams(
					"clientMac", clientMac));
		} catch (Exception e) {
			log.info_ln("% Client rssi removal failed.");
		}

		Date now = new Date();
		reports = new ArrayList<LocationRssiReport>();
		for (int i = 0; i < apMacs.length; i++) {
			LocationRssiReport report = new LocationRssiReport();
			report.setOwner(mapContainerNode.getOwner());
			report.setClientMac(clientMac);
			report.setReporterMac(apMacs[i]);
			report.setReportTime(now);
			report.setRssi((byte) rssi[i]);
			report.setChannel(channel);
			reports.add(report);
		}
		try {
			QueryUtil.bulkCreateBos(reports);
		} catch (Exception e) {
			log.info_ln("% Create client rssi failed ...");
		}
	}

	private int findAcspNbrRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long toNodeId, boolean useA, MapLeafNode[] aps,
			int[] rssiTo) {
		int toNodeIndex = -1;
		Map<String, Integer> apMacs = new HashMap<String, Integer>();
		int i = 0;
		for (MapNode node : (Collection<MapNode>) nodes) {
			if (!node.isLeafNode()) {
				continue;
			}
			MapLeafNode leafNode = (MapLeafNode) node;
			// toNodeId could be null or invalid
			if (leafNode.getId().equals(toNodeId)) {
				toNodeIndex = i;
			}
			apMacs.put(leafNode.getApId(), i);
			aps[i++] = leafNode;
		}
		if (toNodeIndex < 0) {
			return -1;
		}
		List neighbors = QueryUtil
				.executeQuery(
						"select apMac, neighborMac, rssi, txPower, channelNumber from "
								+ AhLatestACSPNeighbor.class.getSimpleName(),
						new SortParams("lastSeen", false), new FilterParams(
								"apMac in (:s1) and neighborMac = :s2",
								new Object[] {
										apMacs.keySet(),
										aps[toNodeIndex].getHiveAp()
												.getMacAddress() }));
		for (Object[] obj : (List<Object[]>) neighbors) {
			String apMac = (String) obj[0];
			String neighborMac = (String) obj[1];
			int rssi = (Byte) obj[2] - noiseFloor;
			int channelNumber = (Integer) obj[4];
			Integer apIndex = apMacs.get(apMac);
			if (apIndex != null /*
								 * Don't filter by calibration thresholds &&
								 * rssi >= mapSettings.getRssiFrom() && rssi <=
								 * mapSettings.getRssiUntil()
								 */) {
				if ((!useA && channelNumber < 15)
						|| (useA && channelNumber > 15)) {
					logt.info("AP: " + apMac + " sees AP: " + neighborMac
							+ " on channel: " + channelNumber + " at: " + +rssi);
					if (rssi > -35) {
						rssi = -35; // max -35 for calibration
					}
					rssiTo[apIndex] = rssi;
				}
			}
		}
		return toNodeIndex;
	}

	public JSONObject acspNbrRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, Long toNodeId,
			boolean useA) throws Exception {
		int apCount = setMetricNodePositions(nodes,
				mapContainerNode.getMapToMetric());
		MapLeafNode[] aps = new MapLeafNode[apCount];
		int[] rssiTo = new int[apCount];
		int toNodeIndex = findAcspNbrRssi(mapContainerNode, nodes, toNodeId,
				useA, aps, rssiTo);
		JSONObject jsonObject = new JSONObject();
		if (toNodeIndex < 0) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.cli.object.notfind"));
			return jsonObject;
		}
		MapLeafNode toNode = aps[toNodeIndex];
		// sensor mode doesn't support acsp in fuji
		if (!toNode.getHiveAp().isSupportAcsp(useA)) {
			jsonObject.put("e",
					MgrUtil.getUserMessage("error.map.acsp.sensor.mode"));
			return jsonObject;
		}
		Collection<JSONObject> nbrs = new Vector<JSONObject>();
		int i = 0;
		for (MapLeafNode ap : aps) {
			if (rssiTo[i] < 0) {
				logt.info_ln("AP: " + ap.getApId() + " sees AP: "
						+ toNode.getApId() + " at: " + rssiTo[i]);
				JSONObject nbr = new JSONObject();
				nbr.put("apId", "n" + ap.getId());
				nbr.put("rssi", rssiTo[i]);
				nbrs.add(nbr);
			}
			i++;
		}
		if (nbrs.size() == 0) {
			String[] mps = new String[] { toNode.getHiveAp().getHostName(),
					useA ? "5" : "2.4" };
			jsonObject.put("e",
					MgrUtil.getUserMessage("info.map.acsp.nbrs", mps));
			return jsonObject;
		}
		jsonObject.put("apId", "n" + toNode.getId());
		jsonObject.put("nbrs", nbrs);
		return jsonObject;
	}

	private int getNextAcspNbrCoverage(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, int heatmapResolution,
			long latchId, Long toNodeId, boolean verbose) throws Exception {
		MrHeatMap mrHeatMap = (MrHeatMap) MgrUtil
				.getSessionAttribute(SessionKeys.MR_HEAT_MAP);
		if (mrHeatMap == null || mrHeatMap.latchId != latchId
				|| mrHeatMap.apIndexes == null) {
			log.info_ln("Next ACSP nbr coverage mistmatch.");
			return heatmapResolution;
		}
		int apCount = mrHeatMap.apX.length;
		MapLeafNode toNode = null;
		MapLeafNode[] aps = new MapLeafNode[apCount];
		for (MapNode node : (Collection<MapNode>) nodes) {
			if (!node.isLeafNode()) {
				continue;
			}
			MapLeafNode mapLeafNode = (MapLeafNode) node;
			aps[mrHeatMap.apIndexes.get(mapLeafNode.getApId())] = mapLeafNode;
			// toNodeId could be null or invalid
			if (node.getId().equals(toNodeId)) {
				toNode = (MapLeafNode) node;
			}
		}
		if (toNode == null) {
			log.error("Target AP not found: " + toNodeId);
			return heatmapResolution;
		}
		Integer toNodeIndex = mrHeatMap.apIndexes.get(toNode.getApId());
		log.info_non("Adding coverage for AP: " + toNode.getApId() + " (id "
				+ toNodeId + ", idx " + toNodeIndex + ")");
		if (toNodeIndex == null) {
			log.error("Target AP index not found: " + toNodeId);
			return heatmapResolution;
		}

		int[][] neighborRSSI = new int[apCount][apCount];
		boolean hasNbrs = false;
		for (int i = 0; i < apCount; i++) {
			neighborRSSI[i][toNodeIndex] = mrHeatMap.neighborRSSI[i][toNodeIndex];
			if (neighborRSSI[i][toNodeIndex] != 0) {
				hasNbrs = true;
			}
		}
		double[] apPower = new double[apCount];
		if (hasNbrs) {
			apPower[toNodeIndex] = mrHeatMap.apPower[toNodeIndex];
		} else {
			log.info_ln(toNode.getApId()
					+ " has no ACSP neighbors, set power to 0.");
			return heatmapResolution;
		}

		if (mrHeatMap.gridX == null) {
			mrHeatMap.canvasWidth = canvasWidth;
			mrHeatMap.canvasHeight = canvasHeight;
			double apElevation = mapContainerNode.getApElevationMetric()
					- elevationMargin;
			if (apElevation < elevationMargin) {
				apElevation = elevationMargin;
			}
			double mapWidthMetric = mapContainerNode.getActualWidthMetric();
			double mapHeightMetric = mapContainerNode.getActualHeightMetric();
			mrHeatMap.apElevation = apElevation;
			mrHeatMap.mapWidthMetric = mapWidthMetric;
			mrHeatMap.mapHeightMetric = mapHeightMetric;

			double squareSize = 3;
			while (mapWidthMetric / squareSize * mapHeightMetric / squareSize > 500) {
				squareSize *= 2;
			}
			mrHeatMap.gridX = Search.cgrd(mapWidthMetric, squareSize);
			mrHeatMap.gridY = Search.cgrd(mapHeightMetric, squareSize);
			if (verbose) {
				log.info_non("squareSize = " + squareSize + "; % total "
						+ (mrHeatMap.gridX.length - 3)
						* (mrHeatMap.gridY.length - 3));
				log.info_non("gridX = createGrid(mapX, squareSize);");
				log.info_non("gridY = createGrid(mapY, squareSize);");
				log.info_non("% Canvas (" + canvasWidth + ", " + canvasHeight
						+ ")");
			}
		}

		if (verbose) {
			logVectors(aps, mrHeatMap.apX, mrHeatMap.apY, apPower,
					mrHeatMap.apChannel, neighborRSSI);
		}

		addAcspNbrCoverage(mapContainerNode, mrHeatMap, toNodeIndex,
				neighborRSSI, apPower, heatmapResolution, verbose);

		clearBeyondNinety(mapContainerNode, mrHeatMap);

		if (mrHeatMap.latchId == latchId) {
			mrHeatMap.remainingIds.remove(toNode.getApId());
			log.info_ln("LatchId " + latchId + ", removing " + toNode.getApId()
					+ ", remaining IDs: " + mrHeatMap.remainingIds);
		}

		return heatmapResolution;
	}

	private void addWallLoss(Search.WallLoss[] walls, int i, Vertex v1,
			Vertex v2, short perimType, double mapToMetric) {
		WallLoss wallLoss = new WallLoss();
		walls[i] = wallLoss;
		wallLoss.x1 = v1.getX() * mapToMetric;
		wallLoss.x2 = v2.getX() * mapToMetric;
		wallLoss.y1 = v1.getY() * mapToMetric;
		wallLoss.y2 = v2.getY() * mapToMetric;
		short wi = getPerimTypeIndex(perimType);
		wallLoss.width = Wall.wallWidth[wi];
		wallLoss.absorption = Wall.wallAbsorption[wi];
	}

	private void addAcspNbrCoverage(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap, int targetIndex, int[][] neighborRSSI,
			double[] apPower, int heatmapResolution, boolean verbose) {
		double mapToMetric = mapContainerNode.getMapToMetric();
		Search.WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapToMetric, mapContainerNode.getWalls().size()
						+ mapContainerNode.getPerimeter().size());

		if (mapContainerNode.getPerimeter().size() > 0) {
			int wallIndex = mapContainerNode.getWalls().size();
			Vertex v1 = mapContainerNode.getPerimeter().get(0);
			Vertex first = v1;
			int perimId = v1.getId();
			short perimType = v1.getType();
			for (int i = 1; i < mapContainerNode.getPerimeter().size(); i++) {
				Vertex v2 = mapContainerNode.getPerimeter().get(i);
				if (v2.getId() != perimId) {
					// Start of new perimeter
					addWallLoss(walls, wallIndex, v1, first, perimType,
							mapToMetric);
					first = v2;
					perimId = v2.getId();
					perimType = v2.getType();
				} else {
					addWallLoss(walls, wallIndex, v1, v2, perimType,
							mapToMetric);
				}
				v1 = v2;
				wallIndex++;
			}
			addWallLoss(walls, wallIndex, v1, first, perimType, mapToMetric);
		}

		double gamma = 0.02;
		double[][] B = Search.clbrb_w(mrHeatMap.apX, mrHeatMap.apY, apPower,
				mrHeatMap.apChannel, mrHeatMap.gridX, mrHeatMap.gridY,
				neighborRSSI, gamma, walls, false);
		if (verbose) {
			log.info_non("B = calibrate(apX, apY, neighborPower, neighborChannel, gridX, gridY, neighborRSSI, 0.1, gamma);");
		}

		if (mrHeatMap.mapColors == null) {
			double imageWidth = mrHeatMap.canvasWidth;
			double imageHeight = mrHeatMap.canvasHeight;

			double grids = mrHeatMap.gridX.length * mrHeatMap.gridY.length;
			// AP count not in complexity
			double complexity = imageWidth * imageHeight * Math.sqrt(grids);
			double costFactor = 1300;
			double cost = complexity / costFactor;
			log.info_non("% Complexity: " + complexity + ", cost: " + cost);

			int costLimit = 2000;
			if (heatmapResolution == MapSettings.HEATMAP_RESOLUTION_MEDIUM) {
				costLimit = 10000;
			} else if (heatmapResolution == MapSettings.HEATMAP_RESOLUTION_HIGH) {
				costLimit = 60000;
			}
			while (cost > costLimit) {
				imageWidth /= 2;
				imageHeight /= 2;
				complexity /= 4;
				cost = complexity / costFactor;
				log.info_non("% Complexity: " + complexity + ", cost: " + cost);
			}
			imageWidth = (int) imageWidth;
			imageHeight = (int) imageHeight;
			log.info_non("% image: (" + imageWidth + ", " + imageHeight + ")");

			Date start = new Date();
			Search.ler(mrHeatMap.gridX, mrHeatMap.gridY, B,
					mrHeatMap.mapWidthMetric, mrHeatMap.mapHeightMetric,
					imageWidth, imageHeight);
			Date end = new Date();
			log.info_non("% loss range in: "
					+ (end.getTime() - start.getTime()) + " ms.");

			mrHeatMap.mapColors = new short[(int) imageWidth][(int) imageHeight];
			mrHeatMap.mapChannels = new short[(int) imageWidth][(int) imageHeight];
			mrHeatMap.mapRssi = new double[(int) imageWidth][(int) imageHeight];
			for (int x = 0; x < imageWidth; x++) {
				for (int y = 0; y < imageHeight; y++) {
					mrHeatMap.mapColors[(int) x][(int) y] = -1;
					mrHeatMap.mapChannels[(int) x][(int) y] = -1;
					mrHeatMap.mapRssi[(int) x][(int) y] = -100000;
				}
			}
			if (heatmapResolution != MapSettings.HEATMAP_RESOLUTION_AUTO) {
				log.info_non("heatmap(apX, apY, apElevation, apPower, apChannel, gridX, gridY, B, mapX, mapY, resolution, false, false);");
			}
		}
		Date start = new Date();
		log.info_non("% apX length: " + mrHeatMap.apX.length);
		double imageWidth = mrHeatMap.mapColors.length;
		double imageHeight = mrHeatMap.mapColors[0].length;
		log.info_non("% image: (" + imageWidth + ", " + imageHeight + ")");
		Search.hmbr(mrHeatMap.apX, mrHeatMap.apY, apPower, mrHeatMap.apChannel,
				mrHeatMap.apElevation, mrHeatMap.gridX, mrHeatMap.gridY, B,
				mrHeatMap.useA, mrHeatMap.mapWidthMetric,
				mrHeatMap.mapHeightMetric, imageWidth, imageHeight,
				mrHeatMap.mapColors, mrHeatMap.mapChannels, mrHeatMap.mapRssi,
				false, 0.5, 4, mrHeatMap.dle, walls, (short) targetIndex,
				(short) targetIndex);
		Date end = new Date();
		log.info_non("% RSSI estimation: " + (end.getTime() - start.getTime())
				+ " ms.");
	}

	private void overlayAcspNbrCoverage(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap, long latchId) {
		if (mrHeatMap.overlayFinished) {
			log.info_ln("Use high res data from cache.");
			// Cached
			mapContainerNode.setMapColors(mrHeatMap.mapColors);
			mapContainerNode.setMapChannels(mrHeatMap.mapChannels);
			mapContainerNode.setApChannel(mrHeatMap.apChannel);
			mapContainerNode.setApIndexes(mrHeatMap.apIndexes);
			return;
		}
		mrHeatMap.overlayBusy = true;
		int apCount = mrHeatMap.apX.length;
		mrHeatMap.mapColors = null; // Make sure that complexity is
									// recalculated.
		for (int j = 0; j < apCount; j++) {
			StringBuffer target = new StringBuffer("Target AP index: " + j
					+ " [");
			boolean seen = false;
			int[][] neighborRSSI = new int[apCount][apCount];
			for (int i = 0; i < apCount; i++) {
				if (mrHeatMap.neighborRSSI[i][j] != 0) {
					neighborRSSI[i][j] = mrHeatMap.neighborRSSI[i][j];
					seen = true;
				}
				target.append(mrHeatMap.neighborRSSI[i][j]);
				if (i < apCount - 1) {
					target.append(", ");
				}
			}
			log.info_non(target.append("] (" + seen + ")").toString());
			if (seen) {
				double[] apPower = new double[apCount];
				apPower[j] = mrHeatMap.apPower[j];
				addAcspNbrCoverage(mapContainerNode, mrHeatMap, j,
						neighborRSSI, apPower,
						MapSettings.HEATMAP_RESOLUTION_HIGH, true);
			}
		}
		if (latchId == mrHeatMap.latchId) {
			clearBeyondNinety(mapContainerNode, mrHeatMap);
			mrHeatMap.overlayBusy = false;
			mrHeatMap.overlayFinished = true;
		} else {
			// Must have been a more recent request.
			logt.info_ln("LatchId doesn't match, must have been been updated.");
		}
	}

	private static void clearBeyondNinety(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap) {
		clearBeyondNinety(mapContainerNode, mrHeatMap, mrHeatMap.mapColors,
				mrHeatMap.mapChannels);
	}

	private static void clearBeyondNinety(MapContainerNode mapContainerNode,
			MrHeatMap mrHeatMap, short[][] mapColors, short[][] mapChannels) {
		double imageWidth = mapColors.length;
		double imageHeight = mapColors[0].length;
		short bci = (short) (rssiColors.length - 1);
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				short colorIndex = mapColors[x][y];
				if (colorIndex >= bci) { // Clear beyond -90 dBm
					mapColors[x][y] = -1;
				}
			}
		}
		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);
		mapContainerNode.setApChannel(mrHeatMap.apChannel);
		mapContainerNode.setApIndexes(mrHeatMap.apIndexes);
	}

	private int getAcspNbrCoverage(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, int heatmapResolution,
			long latchId, Long toNodeId, boolean verbose) throws Exception {
		if (latchId == 0) {
			heatmapResolution = MapSettings.HEATMAP_RESOLUTION_HIGH;
		}
		int apCount = setMetricNodePositions(nodes,
				mapContainerNode.getMapToMetric());
		MapLeafNode[] aps = new MapLeafNode[apCount];
		int[] rssiTo = new int[apCount];
		int toNodeIndex = findAcspNbrRssi(mapContainerNode, nodes, toNodeId,
				useA, aps, rssiTo);

		MrHeatMap mrHeatMap = null;
		if (toNodeIndex >= 0) {
			MapLeafNode toNode = aps[toNodeIndex];
			Map<String, MapLeafNode> tnm = new HashMap<String, MapLeafNode>();
			tnm.put(toNode.getApId(), toNode);
			filledChannelPowers(tnm);
			if (verbose) {
				log.info_non("ap = [");
				for (int i = 0; i < aps.length; i++) {
					log.info_non("'" + aps[i].getApId() + "'; ");
				}
				log.info_ln("];");
				log.info_non("rssiTo = [");
				for (int i = 0; i < aps.length; i++) {
					log.info_non(rssiTo[i] + "; ");
				}
				log.info_ln("];");
			}
			mrHeatMap = getAcspHeatmap(mapContainerNode, canvasWidth,
					canvasHeight, useA, heatmapResolution, latchId, aps,
					rssiTo, toNodeIndex, verbose);
		}
		return estimateHmb(mapContainerNode, mrHeatMap, heatmapResolution);
	}

	private MrHeatMap getAcspHeatmap(MapContainerNode mapContainerNode,
			int canvasWidth, int canvasHeight, boolean useA,
			int heatmapResolution, long latchId, MapLeafNode[] aps,
			int[] rssiTo, int toNodeIndex, boolean verbose) {
		double mapToMetric = mapContainerNode.getMapToMetric();
		double mapWidthMetric = mapContainerNode.getActualWidthMetric();
		double mapHeightMetric = mapContainerNode.getActualHeightMetric();

		double[] apX = new double[aps.length];
		double[] apY = new double[aps.length];
		double[] apPower = new double[aps.length];
		short[] apChannel = new short[aps.length];
		int[][] neighborRSSI = new int[aps.length][aps.length];
		boolean hasNbrs = false;
		for (int i = 0; i < aps.length; i++) {
			MapLeafNode ap = aps[i];
			apX[i] = ap.getXm();
			apY[i] = ap.getYm();
			neighborRSSI[i][toNodeIndex] = rssiTo[i];
			if (rssiTo[i] != 0) {
				hasNbrs = true;
			}
		}
		float eirp = useA ? aps[toNodeIndex].getRadioEirpA() : aps[toNodeIndex]
				.getRadioEirpBG();
		log.info_ln("Target AP " + aps[toNodeIndex].getApId() + " EIRP: "
				+ eirp);
		if (hasNbrs) {
			apPower[toNodeIndex] = eirp;
		} else {
			log.info_ln(aps[toNodeIndex].getApId()
					+ " has no ACSP neighbors, set power to 0.");
		}
		apChannel[toNodeIndex] = getChannel(aps[toNodeIndex], useA);

		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < elevationMargin) {
			apElevation = elevationMargin;
		}

		double xray = getWallsXray(mapContainerNode);

		MrHeatMap mrHeatMap = (MrHeatMap) MgrUtil
				.getSessionAttribute(SessionKeys.MR_HEAT_MAP);
		boolean cached = matchCachedRssi(mrHeatMap, apX, apY, apPower,
				apChannel, apElevation, apPower, apChannel, neighborRSSI, useA,
				mapWidthMetric, mapHeightMetric, canvasWidth, canvasHeight,
				heatmapResolution, xray);
		if (cached) {
			log.info_ln("% Matches with cached RSSI: " + cached);
			return mrHeatMap;
		}

		double squareSize = 3;
		while (mapWidthMetric / squareSize * mapHeightMetric / squareSize > 500) {
			squareSize *= 2;
		}
		double[] gridX = Search.cgrd(mapWidthMetric, squareSize);
		double[] gridY = Search.cgrd(mapHeightMetric, squareSize);
		double gamma = 0.02;
		if (verbose) {
			log.info_ln("squareSize = " + squareSize + "; % total "
					+ (gridX.length - 3) * (gridY.length - 3));
			log.info_ln("gridX = createGrid(mapX, squareSize);");
			log.info_ln("gridY = createGrid(mapY, squareSize);");
			log.info_ln("% Canvas (" + canvasWidth + ", " + canvasHeight + ")");
			log.info_ln("gamma = " + gamma + ";");
		}

		Search.WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapToMetric);

		if (verbose) {
			logVectors(aps, apX, apY, apPower, apChannel, neighborRSSI);
		}

		double[][] B = Search.clbrb_w(apX, apY, apPower, apChannel, gridX,
				gridY, neighborRSSI, gamma, walls, false);
		if (verbose) {
			log.info_ln("B = calibrate(apX, apY, neighborPower, neighborChannel, gridX, gridY, neighborRSSI, 0.1, gamma);");
		}
		double lsle = 0;
		double dle = 0;
		return new MrHeatMap(apX, apY, null, apPower, apChannel, apElevation,
				apPower, apChannel, neighborRSSI, gridX, gridY, B, lsle, dle,
				useA, mapWidthMetric, mapHeightMetric, canvasWidth,
				canvasHeight, heatmapResolution, xray, latchId);
	}

	/*
	 * Find location of a particular client on a map and also return the the
	 * reporting APs and the rssi measurements.
	 */
	public Collection<JSONObject> clientRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String clientMac)
			throws Exception {
		Collection<JSONObject> clientRssi = new Vector<JSONObject>();
		if (clientMac == null) {
			return clientRssi;
		}
		boolean circle = false;
		if (clientMac.charAt(0) == '|') {
			circle = true;
			clientMac = clientMac.substring(1);
		}
		// List<Object[]> clientHostname = (List<Object[]>) QueryUtil
		// .executeQuery("select clientHostname, id from "
		// + AhClientSession.class.getSimpleName(), null,
		// new FilterParams(
		// "connectstate = :s1 and clientMac = :s2",
		// new Object[] {
		// AhClientSession.CONNECT_STATE_UP,
		// clientMac }));
		List<Object[]> clientHostname = (List<Object[]>) DBOperationUtil
				.executeQuery(
						"select clientHostname, id from ah_clientsession",
						null, new FilterParams(
								"connectstate = ? and clientMac = ?",
								new Object[] {
										AhClientSession.CONNECT_STATE_UP,
										clientMac }));
		if (clientHostname.size() == 0) {
			return clientRssi;
		}

		String hostname = (String) clientHostname.get(0)[0];
		Long clientId = (Long) clientHostname.get(0)[1];

		// AhClientSession client = (AhClientSession) QueryUtil.findBoById(
		// AhClientSession.class, clientId);
		AhClientSession client = (AhClientSession) DBOperationUtil.findBoById(
				AhClientSession.class, clientId);
		if (client == null) {
			return clientRssi;
		}
		// bug fix for 20142, use rssi&snr value sent by HiveOS
		int rssi = client.getClientRssi();
		log.info_ln("% Client RSSI: " + client.getClientRssi());
		AhAssociation association = getClientAssociation(client);
		if (association != null) {
			log.info_ln("% Override client RSSI: " + client.getClientRssi()
					+ " with association: " + association.getClientRSSI());
			// bug fix for 20142, use rssi&snr value sent by HiveOS
			rssi = association.getClientRSSI();
		}
		log.info_ln("% associated AP is: " + client.getApMac() + ", name: "
				+ client.getApName() + ", rssi: " + rssi + ", channel: "
				+ client.getClientChannel());

		MapLeafNode clientAP = null;
		for (MapNode mapNode : nodes) {
			if (!mapNode.isLeafNode()) {
				continue;
			}
			MapLeafNode leafNode = (MapLeafNode) mapNode;
			if (leafNode.getApId().equals(client.getApMac())) {
				clientAP = leafNode;
			}
		}
		if (clientAP == null) {
			return clientRssi;
		}

		if (circle) {
			return clientRadius(mapContainerNode, nodes, pageId, scale,
					clientMac, client, clientAP, rssi);
		}

		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		logt.info("clientRssi", "# AP macs: " + nodesMap.keySet().size());
		if (nodesMap.keySet().size() == 0) {
			return clientRssi;
		}

		Search.ClientDetected[] clients = findClient(mapContainerNode, nodes,
				nodesMap, pageId, scale, clientMac, hostname, mapSettings,
				true, clientRssi, null, true, true);

		if (clientRssi.size() > 0) {
			JSONObject location = clientRssi.iterator().next();
			location.put("apId", "n" + clientAP.getId());
		}

		return encodeRssi(clients, clientRssi, clientMac,
				mapSettings.isUseHeatmap());
	}

	private Collection<JSONObject> clientRadius(
			MapContainerNode mapContainerNode, Set<MapNode> nodes, Long pageId,
			double scale, String clientMac, AhClientSession client,
			MapLeafNode clientAP, int rssi) throws Exception {
		Collection<JSONObject> radius = new Vector<JSONObject>();
		JSONObject location = new JSONObject();
		location.put("pageId", pageId);
		location.put("mac", clientMac);
		location.put("x", -1);
		radius.add(location);
		if (client.getClientHostname() == null
				|| client.getClientHostname().length() == 0) {
			location.put("lbl", clientMac);
		} else {
			location.put("lbl", client.getClientHostname());
		}

		double plf = getPathLossFactor(mapContainerNode);
		boolean useA = client.getClientChannel() > 14;
		double erp = 12;
		while (!findRadiusIntersection(erp, mapContainerNode, plf, useA, rssi,
				location, scale, clientAP) && erp > -30) {
			erp -= 1;
		}

		location.put("apId", "n" + clientAP.getId());
		JSONObject link = new JSONObject();
		link.put("nodeId", "n" + clientAP.getId());
		link.put("rssi", rssi + " dBm");
		radius.add(link);
		return radius;
	}

	private boolean findRadiusIntersection(double erp,
			MapContainerNode mapContainerNode, double plf, boolean useA,
			double rssi, JSONObject location, double scale,
			MapLeafNode associatedAP) throws Exception {
		double mapToMetric = mapContainerNode.getMapToMetric();
		double apx = associatedAP.getX() * mapToMetric;
		double apy = associatedAP.getY() * mapToMetric;
		double width = mapContainerNode.getActualWidthMetric();
		double height = mapContainerNode.getActualHeightMetric();
		int parts = 27;
		double angleIncrement = 2 * Math.PI / parts;
		double angle = angleIncrement * 2;
		double distance;
		if (useA) {
			distance = getDistanceA(erp - rssi, plf);
		} else {
			distance = getDistanceBG(erp - rssi, plf);
		}
		log.info_ln("AP position " + apx + ", " + apy + "), distance: "
				+ distance + ", erp: " + erp);
		boolean useNext = false;
		for (int i = 0; i < parts; i++) {
			double dx = distance * Math.cos(angle);
			double dy = distance * Math.sin(angle);
			double cx = apx + dx;
			double cy = apy - dy;
			if (cx > 0 && cx < width && cy > 0 && cy < height) {
				if (useNext) {
					log.info_ln("Client position: (" + cx + ", " + cy + ")");
					double x = Math.round(cx / mapToMetric * scale);
					double y = Math.round(cy / mapToMetric * scale);
					location.put("x", x);
					location.put("y", y);
					location.put("circle",
							Math.round(distance / mapToMetric * scale));
					return true;
				} else {
					useNext = true;
				}
			}
			angle += angleIncrement;
		}
		return false;
	}

	public Search.ClientDetected[] findClient(
			MapContainerNode mapContainerNode, Set<MapNode> nodes,
			Map<String, MapLeafNode> nodesMap, Long pageId, double scale,
			String clientMac, String clientHostname, MapSettings mapSettings,
			boolean setDistance, Collection<JSONObject> clientRssi,
			double[] xp, boolean verifySampled, boolean verbose)
			throws Exception {
		logt.info("findClient",
				"Use calibrated heat map: " + mapSettings.isUseHeatmap());
		MrHeatMap heatMap_a = getHeatMap(mapContainerNode, nodes, 0, 0, true,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		MrHeatMap heatMap_g = getHeatMap(mapContainerNode, nodes, 0, 0, false,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		Search.ClientDetected[] clients = null;
		if (heatMap_a != null || heatMap_g != null) {
			Collection<Object[]> clientMacs = new Vector<Object[]>();
			clientMacs.add(new String[] { clientMac, clientHostname });
			clients = locateClientMacs(mapContainerNode, nodesMap, heatMap_a,
					heatMap_g, mapSettings, pageId, null, 0, scale, clientMacs,
					setDistance, clientRssi, xp, verifySampled, verbose);
		}
		return clients;
	}

	private Search.ClientDetected[] locateClientMacs(
			MapContainerNode mapContainerNode,
			Map<String, MapLeafNode> nodesMap, MrHeatMap heatMap_a,
			MrHeatMap heatMap_g, MapSettings mapSettings, Long pageId,
			Graphics2D g2, double metricToImage, double scale,
			Collection<Object[]> activeClients, boolean setDistance,
			Collection<JSONObject> clientLocations, double[] xp,
			boolean verifySampled, boolean verbose) throws Exception {
		log.info_ln("% locateClientMacs at: " + new Date());
		boolean useHeatmap = mapSettings.isUseHeatmap();
		boolean calibrateHeatmap = mapSettings.isCalibrateHeatmap();
		if (useHeatmap) {
			if (heatMap_a != null) {
				hmv(heatMap_a, mapContainerNode, calibrateHeatmap, g2,
						metricToImage, verbose);
			}
			if (heatMap_g != null) {
				hmv(heatMap_g, mapContainerNode, calibrateHeatmap, g2,
						metricToImage, verbose);
			}
		}
		Calendar calendar = Calendar.getInstance();
		Date reportTime = new Date();
		calendar.setTime(reportTime);
		if (mapSettings.isRealTime()) {
			calendar.add(Calendar.MINUTE, -lastRssiMaxAge);
		} else {
			calendar.add(Calendar.MINUTE, -mapSettings.getLocationWindow());
		}
		reportTime = calendar.getTime();
		double mapToMetric = mapContainerNode.getMapToMetric();
		log.info_ln("mapX = " + mapContainerNode.getActualWidthMetric()
				+ "; mapY = " + mapContainerNode.getActualHeightMetric() + ";");
		double plf_a = getPathLossFactor(mapContainerNode,
				heatMap_a != null ? heatMap_a : heatMap_g, useHeatmap
						&& calibrateHeatmap);
		double plf_g = getPathLossFactor(mapContainerNode,
				heatMap_g != null ? heatMap_g : heatMap_a, useHeatmap
						&& calibrateHeatmap);
		if (heatMap_a != null) {
			log.info_ln("gamma = " + gammaTxPower + "; ps = " + startingTxPower
					+ "; apElevation = "
					+ mapContainerNode.getApElevationMetric() + "; tol = "
					+ svdPrecision + "; le = " + heatMap_a.lsle + "; dle = "
					+ heatMap_a.dle + "; plf = " + plf_a + "; % a");
		}
		if (heatMap_g != null) {
			log.info_ln("gamma = " + gammaTxPower + "; ps = " + startingTxPower
					+ "; apElevation = "
					+ mapContainerNode.getApElevationMetric() + "; tol = "
					+ svdPrecision + "; le = " + heatMap_g.lsle + "; dle = "
					+ heatMap_g.dle + "; plf = " + plf_g + "; % g");
		}
		int inside = 0, outside = 0, srlsConverge = 0, totalSrlsIters = 0, totalGnIters = 0, srlsOutsideConverge = 0, totalOutsideSrlsIters = 0, totalOutsideGnIters = 0;
		int[] iters = { 0, 0 };
		double[] x = null;
		Search.ClientDetected[] clients = null;
		boolean useA = false;
		for (Object[] activeClient : activeClients) {
			String clientMac = (String) activeClient[0];
			String clientHostname = (String) activeClient[1];
			List<LocationRssiReport> reports;
			if (mapSettings.isRealTime()) {
				reports = findLastRssiReports(clientMac, nodesMap, reportTime,
						mapContainerNode.getOwner().getId());
			} else {
				reports = findRssiReports(clientMac, nodesMap, reportTime,
						mapContainerNode.getOwner().getId(),
						mapSettings.getClientRssiThreshold());
			}
			int count = countQualifyingClientRssi(reports, nodesMap,
					mapSettings.getClientRssiThreshold());
			useA = count < 0;
			count = Math.abs(count);
			if (count < mapSettings.getMinRssiCount()) {
				log.info_ln("% only " + count
						+ " measurements for Client MAC: " + clientMac);
				continue;
			}
			if (useA && heatMap_a == null) {
				log.info_ln("% no heat map for a, " + count
						+ " measurements for Client MAC: " + clientMac);
				continue;
			}
			if (!useA && heatMap_g == null) {
				log.info_ln("% no heat map for g, " + count
						+ " measurements for Client MAC: " + clientMac);
				continue;
			}
			clients = new Search.ClientDetected[count];
			findQualifyingRssi(clients, reports, nodesMap,
					useA ? heatMap_a.apIndexes : heatMap_g.apIndexes,
					mapSettings.getClientRssiThreshold());
			log.info_ln("% " + clients.length + (useA ? " a" : " g")
					+ " measurements for Client MAC: " + clientMac);
			if (verbose) {
				logRssiMeasurements(clients);
			}
			x = locateMac(mapContainerNode, nodesMap, useA ? heatMap_a
					: heatMap_g, useHeatmap, calibrateHeatmap, clients,
					clientMac, iters, useA ? plf_a : plf_g, g2, metricToImage,
					setDistance, verbose);
			if (x != null) {
				if (xp != null) {
					xp[0] = x[0];
					xp[1] = x[1];
					xp[2] = x[2];
				}
				x = postls(mapContainerNode, clients, x);
			}
			if (iters[0] > maxSrlsIters) {
				iters[0]--;
			}
			if (iters[1] < 0) {
				clients = null;
			}
			if (x == null) {
				outside++;
				totalOutsideSrlsIters += iters[0];
				totalOutsideGnIters += iters[1];
				if (iters[1] == 0) {
					srlsOutsideConverge++;
				}
			} else {
				inside++;
				totalSrlsIters += iters[0];
				totalGnIters += iters[1];
				if (iters[1] == 0) {
					srlsConverge++;
				}
				if (g2 == null) {
					JSONObject location = new JSONObject();
					if (clientLocations.size() == 0) {
						location.put("pageId", pageId);
					}
					location.put("x", Math.round(x[0] / mapToMetric * scale));
					location.put("y", Math.round(x[1] / mapToMetric * scale));
					location.put("mac", clientMac);
					if (clientHostname == null || clientHostname.length() == 0) {
						location.put("lbl", clientMac);
					} else {
						location.put("lbl", clientHostname);
					}
					if (verifySampled) {
						List samples = QueryUtil.executeQuery(
								"select distinct tid from "
										+ Trex.class.getSimpleName(), null,
								new FilterParams("tid", clientMac),
								mapContainerNode.getOwner().getId());
						location.put("sd", samples.size() > 0);
					}
					clientLocations.add(location);
				}
			}
		}
		postLocate(clients, x, useA ? plf_a : plf_g, scale, mapToMetric,
				setDistance, inside, outside, srlsConverge, totalSrlsIters,
				totalGnIters, srlsOutsideConverge, totalOutsideSrlsIters,
				totalOutsideGnIters);
		return clients;
	}

	public void findClientMacs(MapContainerNode mapContainerNode,
			MapSettings mapSettings, Long pageId, double scale,
			Collection<Object[]> activeClients,
			Collection<JSONObject> clientLocations, boolean verifySampled)
			throws Exception {
		double mapToMetric = mapContainerNode.getMapToMetric();
		Date reportTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(reportTime);
		calendar.add(Calendar.MINUTE, -mapSettings.getLocationWindow());
		reportTime = calendar.getTime();
		for (Object[] activeClient : activeClients) {
			String clientMac = (String) activeClient[0];
			String clientHostname = (String) activeClient[1];
			List<LocationHistory> history = (List<LocationHistory>) QueryUtil
					.executeQuery(LocationHistory.class, null,
							new FilterParams(
									"clientMac = :s1 and version >= :s2",
									new Object[] { clientMac, reportTime }),
							mapContainerNode.getOwner().getId());
			log.info_ln("% " + history.size() + " historical entries for mac: "
					+ clientMac);
			if (history.size() == 0) {
				continue;
			}
			double count = 0, x = 0, y = 0;
			for (LocationHistory location : history) {
				double weight = location.weight;
				if (location.weight < 3) {
					weight = 1;
				}
				x += location.x1 * weight;
				y += location.x2 * weight;
				count += weight;
			}
			x = x / count;
			y = y / count;
			count = history.size();
			log.info_ln("% estimate (" + x + ", " + y + ")");
			JSONObject location = new JSONObject();
			if (clientLocations.size() == 0) {
				location.put("pageId", pageId);
			}
			location.put("x", Math.round(x / mapToMetric * scale));
			location.put("y", Math.round(y / mapToMetric * scale));
			location.put("mac", clientMac);
			if (clientHostname == null || clientHostname.length() == 0) {
				location.put("lbl", clientMac);
			} else {
				location.put("lbl", clientHostname);
			}
			if (verifySampled) {
				List samples = QueryUtil.executeQuery(
						"select distinct tid from "
								+ Trex.class.getSimpleName(), null,
						new FilterParams("tid", clientMac), mapContainerNode
								.getOwner().getId());
				location.put("sd", samples.size() > 0);
			}
			clientLocations.add(location);
		}
	}

	public void locateSource(Collection<LocationRssiReport> reports)
			throws Exception {
		long mapId = 0;
		int count = 1;
		int otherCount = 0;
		HmDomain domain = null;
		String clientMac = null;
		for (LocationRssiReport report : reports) {
			if (report.getRssi() < hmRssiFrom || report.getRssi() > hmRssiUntil) {
				continue;
			}
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					report.getReporterMac());
			if (hiveAp.getMapContainerId() != null) {
				if (mapId == 0) {
					mapId = hiveAp.getMapContainerId();
					domain = report.getOwner();
				} else if (hiveAp.getMapContainerId() == mapId) {
					count++;
				} else {
					otherCount++;
				}
			}
			clientMac = report.getClientMac();
		}
		log.info_ln("% locateSource: " + clientMac);
		if (domain == null) {
			return;
		}
		MapSettings mapSettings = BeTopoModuleUtil.getMapGlobalSetting(domain);
		if (otherCount >= mapSettings.getMinRssiCount()) {
			Map<Long, Integer> mapCounts = getMapCounts(reports);
			count = 0;
			for (Long mapCount : mapCounts.keySet()) {
				otherCount = mapCounts.get(mapCount);
				if (otherCount > count) {
					count = otherCount;
					mapId = mapCount;
				}
			}
		}
		MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
				.findBoById(MapNode.class, mapId, new QueryMapContainer());
		domain = mapContainerNode.getOwner();
		mapSettings = BeTopoModuleUtil.getMapGlobalSetting(domain);
		if (count < mapSettings.getMinRssiCount()) {
			return;
		}
		Set<MapNode> nodes = mapContainerNode.getChildNodes();
		MrHeatMap mrHeatMap = getHeatMap(mapContainerNode, nodes, 0, 0, false,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil(), false,
				false);
		if (mrHeatMap == null) {
			return;
		}
		boolean useHeatmap = mapSettings.isUseHeatmap();
		boolean calibrateHeatmap = mapSettings.isCalibrateHeatmap();
		if (useHeatmap) {
			hmv(mrHeatMap, mapContainerNode, calibrateHeatmap, null, 0, false);
		}
		double plf = mrHeatMap.lsle * 10;
		int[] iters = { 0, 0 };
		double[] x = null;

		Search.ClientDetected[] clients = new Search.ClientDetected[count];
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		findQualifyingRssi(clients, reports, nodesMap, mrHeatMap.apIndexes,
				mapSettings.getClientRssiThreshold());
		x = locateMac(mapContainerNode, nodesMap, mrHeatMap, useHeatmap,
				calibrateHeatmap, clients, clientMac, iters, plf, null, 0,
				false, false);
		if (x == null) {
			return;
		}

		Date reportTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(reportTime);
		calendar.add(Calendar.MINUTE, -mapSettings.getLocationWindow());
		reportTime = calendar.getTime();
		int removed = QueryUtil.bulkRemoveBos(LocationHistory.class,
				new FilterParams("version < :s1", new Object[] { reportTime }),
				domain.getId());

		LocationHistory locationHistory = new LocationHistory();
		locationHistory.setOwner(domain);
		Date detectedTime = clients[0].getDetectedTime();
		Timestamp version = detectedTime != null ? new Timestamp(
				detectedTime.getTime()) : null;
		locationHistory.setVersion(version);
		locationHistory.clientMac = clientMac;
		locationHistory.x1 = x[0];
		locationHistory.x2 = x[1];
		locationHistory.x3 = x[2];
		locationHistory.weight = clients.length;
		QueryUtil.createBo(locationHistory);
		log.info_ln("% locateSource remove count: " + removed);
	}

	private Map<Long, Integer> getMapCounts(
			Collection<LocationRssiReport> reports) {
		Map<Long, Integer> mapCounts = new HashMap<Long, Integer>();
		for (LocationRssiReport report : reports) {
			if (report.getRssi() < hmRssiFrom || report.getRssi() > hmRssiUntil) {
				continue;
			}
			SimpleHiveAp hiveAp = CacheMgmt.getInstance().getSimpleHiveAp(
					report.getReporterMac());
			if (hiveAp.getMapContainerId() != null) {
				Integer count = mapCounts.get(hiveAp.getMapContainerId());
				if (count == null) {
					mapCounts.put(hiveAp.getMapContainerId(), 1);
				} else {
					mapCounts.put(hiveAp.getMapContainerId(), count + 1);
				}
			}
		}
		return mapCounts;
	}

	private double[] locateMac(MapContainerNode mapContainerNode,
			Map<String, MapLeafNode> nodesMap, MrHeatMap mrHeatMap,
			boolean useHeatmap, boolean calibrateHeatmap,
			Search.ClientDetected[] clients, String mac, int[] iters,
			double plf, Graphics2D g2, double metricToImage, boolean area,
			boolean verbose) throws Exception {
		double[] x;
		if (useHeatmap) {
			x = leastSquare(mapContainerNode, nodesMap.values(), clients, plf,
					g2, metricToImage, iters);
			if (postls(mapContainerNode, clients, x) != null) {
				double[][] hma = null;
				if (area) {
					hma = new double[mrHeatMap.hmv.length][mrHeatMap.hmv[0].length];
					mapContainerNode.area = hma;
					mapContainerNode.gridXsize = mrHeatMap.gridXsize;
					mapContainerNode.gridYsize = mrHeatMap.gridYsize;
				}
				if (calibrateHeatmap) {
					Trex trex = findTrex(mac);
					if (trex == null) {
						x = Search.hmm_b(mrHeatMap.hmv, hma,
								mrHeatMap.gridXsize, mrHeatMap.gridYsize,
								clients, x[2], g2, metricToImage, 0.05, false);
						if (false) { // Calculate are using function above
							x = Search.hmm(mrHeatMap.hmv, mrHeatMap.gridXsize,
									mrHeatMap.gridYsize, clients, x[2], g2,
									metricToImage, false);
						}
						log.info_ln("x = [" + x[0] + "; " + x[1] + "; " + x[2]
								+ "]; % with HMM-LS using erp from LSTR");
						if (false) { // Use the solution above
							x = Search.hmm_a(mrHeatMap.hmv, hma,
									mrHeatMap.gridXsize, mrHeatMap.gridYsize,
									clients, 10, 25, g2, metricToImage, 0.10,
									false);
						}
					} else {
						if (verbose) {
							log.info_ln("% client erp: " + trex.erp);
						}
						x = Search.hmm_b(mrHeatMap.hmv, hma,
								mrHeatMap.gridXsize, mrHeatMap.gridYsize,
								clients, trex.erp, g2, metricToImage, 0.05,
								false);
					}
				} else {
					// Use power level of lstr
					x = Search.hmm_b(mrHeatMap.hmv, hma, mrHeatMap.gridXsize,
							mrHeatMap.gridYsize, clients, x[2], g2,
							metricToImage, 0.10, false);
				}
				log.info_ln("x = [" + x[0] + "; " + x[1] + "; " + x[2]
						+ "]; % with HMM-LS");
			}
		} else {
			x = leastSquare(mapContainerNode, nodesMap.values(), clients, plf,
					g2, metricToImage, iters);
		}
		return x;
	}

	private Trex findTrex(String mac) {
		if (mac == null) {
			return null;
		}
		List<Trex> clients = (List<Trex>) QueryUtil.executeQuery(Trex.class,
				null, new FilterParams("tid", mac), 1);
		if (clients.size() > 0) {
			return clients.get(0);
		} else {
			return null;
		}
	}

	private int countQualifyingClientRssi(List<LocationRssiReport> reports,
			Map<String, MapLeafNode> nodesMap, int rssiThreshold) {
		if (reports.size() == 0) {
			return 0;
		}
		int n = 0;
		int channel = -1;
		for (LocationRssiReport report : reports) {
			MapLeafNode mapLeafNode = (MapLeafNode) nodesMap.get(report
					.getReporterMac());
			if (mapLeafNode != null && report.getRssi() >= rssiThreshold
					&& report.getRssi() <= hmRssiUntil) {
				if (channel < 0) {
					channel = report.getChannel();
					n++;
				} else if (channel < 3000 && report.getChannel() < 3000) {
					n++;
				} else if (channel > 4000 && report.getChannel() > 4000) {
					n++;
				}
			}
		}
		if (channel > 4000) {
			return -n;
		} else {
			return n;
		}
	}

	private void findQualifyingRssi(Search.ClientDetected[] clients,
			Collection<LocationRssiReport> reports,
			Map<String, MapLeafNode> nodesMap, Map apIndexes, int rssiThreshold) {
		int i = 0;
		int channel = -1;
		for (LocationRssiReport report : reports) {
			MapLeafNode mapLeafNode = (MapLeafNode) nodesMap.get(report
					.getReporterMac());
			if (mapLeafNode == null || report.getRssi() < rssiThreshold
					|| report.getRssi() > hmRssiUntil) {
				continue;
			}
			if (channel < 0) {
				channel = report.getChannel();
			} else if (channel < 3000 && report.getChannel() < 3000) {
			} else if (channel > 4000 && report.getChannel() > 4000) {
			} else {
				continue;
			}
			Search.ClientDetected client = new Search.ClientDetected();
			client.setClientMac(report.getClientMac());
			client.setRssi(report.getRssi());
			client.setDetectedTime(report.getReportTime());
			// channel attribute in LocationRssiReport is actually the frequency
			// in KHz
			client.setC(20 * Math.log10(report.getChannel()) - 28);
			// client.setC(20 * Math.log10(Search.getFrequency((short) 1)) -
			// 28);
			client.setXm(mapLeafNode.getXm());
			client.setYm(mapLeafNode.getYm());
			client.setId(mapLeafNode.getId());
			if (apIndexes != null) {
				Integer apIndex = (Integer) apIndexes
						.get(mapLeafNode.getApId());
				if (apIndex != null) {
					client.apIndex = apIndex;
				}
			}
			clients[i++] = client;
		}
	}

	private List<LocationRssiReport> findRssiReports(String clientMac,
			Map<String, MapLeafNode> nodesMap) {
		return (List<LocationRssiReport>) QueryUtil.executeQuery(
				LocationRssiReport.class, null, new FilterParams(
						"clientMac = :s1", new Object[] { clientMac }));
	}

	/*
	 * Find most recent measuremrents, but not older than reportTime
	 */
	private List<LocationRssiReport> findLastRssiReports(String clientMac,
			Map<String, MapLeafNode> nodesMap, Date reportTime, Long domainId) {
		List<Date> reportTimes = (List<Date>) QueryUtil.executeQuery(
				"select reportTime from "
						+ LocationRssiReport.class.getSimpleName(),
				new SortParams("reportTime", false), new FilterParams(
						"clientMac = :s1 and reportTime >= :s2", new Object[] {
								clientMac, reportTime }), domainId, 1);
		if (reportTimes.size() == 0) {
			return Collections.EMPTY_LIST;
		} else {
			return (List<LocationRssiReport>) QueryUtil.executeQuery(
					LocationRssiReport.class, null, new FilterParams(
							"clientMac = :s1 and reportTime = :s2",
							new Object[] { clientMac, reportTimes.get(0) }));
		}
	}

	/*
	 * Find all measuremrents since reportTime
	 */
	private List<LocationRssiReport> findRssiReports(String clientMac,
			Map<String, MapLeafNode> nodesMap, Date reportTime, Long domainId,
			int rssiThreshold) {
		List<LocationRssiReport> rawReports = (List<LocationRssiReport>) QueryUtil
				.executeQuery(
						LocationRssiReport.class,
						new SortParams("reporterMac", false),
						new FilterParams(
								"clientMac = :s1 and reportTime >= :s2 and owner.id = :s3",
								new Object[] { clientMac, reportTime, domainId }));
		String reporterMac = null;
		LocationRssiReport report = null;
		int rssi = 0;
		int count = 0;
		List<LocationRssiReport> reports = new ArrayList<LocationRssiReport>();
		for (LocationRssiReport rawReport : rawReports) {
			if (rawReport.getRssi() < rssiThreshold
					|| rawReport.getRssi() > hmRssiUntil) {
				continue;
			}
			if (rawReport.getReporterMac().equals(reporterMac)) {
				if (report.getChannel() == rawReport.getChannel()) {
					count++;
					rssi += rawReport.getRssi();
				}
			} else {
				if (report != null) {
					report.setRssi((byte) (rssi / count));
					reports.add(report);
				}
				reporterMac = rawReport.getReporterMac();
				report = new LocationRssiReport();
				report.setClientMac(rawReport.getClientMac());
				report.setReporterMac(rawReport.getReporterMac());
				report.setChannel(rawReport.getChannel());
				report.setReportTime(rawReport.getReportTime());
				count = 1;
				rssi = rawReport.getRssi();
			}
		}
		if (report != null) {
			report.setRssi((byte) (rssi / count));
			reports.add(report);
		}
		return reports;
	}

	public Collection<JSONObject> locateRogues(
			MapContainerNode mapContainerNode, Set<MapNode> nodes, Long pageId,
			double imageWidth, BufferedImage image, double scale)
			throws Exception {
		Collection<JSONObject> rogueLocations = null;
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		logt.info("locateRogues",
				"Use calibrated heat map: " + mapSettings.isUseHeatmap());
		Graphics2D g2 = null;
		double metricToImage = 0;
		double mapToMetric = mapContainerNode.getMapToMetric();
		if (image != null) {
			g2 = image.createGraphics();
			g2.setStroke(new BasicStroke(2));
			metricToImage = imageWidth / mapContainerNode.getWidth()
					/ mapToMetric;
		}
		MrHeatMap mrHeatMap = getHeatMap(mapContainerNode, nodes, 0, 0, false,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		if (mrHeatMap == null) {
			return rogueLocations;
		}
		mapContainerNode.setApElevation(0); // assume rogue APs are at the same
		// elevation
		if (image != null) {
			return rogueLocations;
		}
		// Rogues command from UI
		rogueLocations = new Vector<JSONObject>();

		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		logt.info("locateRogues", "# AP macs: " + nodesMap.keySet().size());

		if (nodesMap.keySet().size() == 0) {
			return rogueLocations;
		}

		// CJS
		// createRogueLocate(mapContainerNode, nodesMap);
		// CJS

		List<String> foreignApBssids = (List<String>) QueryUtil
				.executeQuery(
						"select distinct ifMacAddress from "
								+ Idp.class.getSimpleName(),
						null,
						new FilterParams(
								"idpType = :s1 AND stationType = :s2 AND reportNodeId in (:s3)",
								new Object[] {
										BeCommunicationConstant.IDP_TYPE_ROGUE,
										BeCommunicationConstant.IDP_STATION_TYPE_AP,
										nodesMap.keySet() }));

		Date start = new Date();
		locateBssids(mapContainerNode, nodesMap, mrHeatMap, mapSettings,
				pageId, g2, metricToImage, scale, foreignApBssids, false,
				rogueLocations, null, false);
		Date end = new Date();
		log.info_ln("% " + foreignApBssids.size() + " foreign BSSIDs in "
				+ (end.getTime() - start.getTime()) + " ms.");
		return rogueLocations;
	}

	public Collection<JSONObject> rogueRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String bssid)
			throws Exception {
		Collection<JSONObject> rogueRssi = new Vector<JSONObject>();
		if (bssid == null) {
			return rogueRssi;
		}
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		logt.info("rogueRssi", "# AP macs: " + nodesMap.keySet().size());
		if (nodesMap.keySet().size() == 0) {
			return rogueRssi;
		}

		Search.ClientDetected[] clients = findRogue(mapContainerNode, nodes,
				nodesMap, pageId, scale, bssid, mapSettings, true, rogueRssi,
				null, true);

		return encodeRssi(clients, rogueRssi, bssid, mapSettings.isUseHeatmap());
	}

	public Search.ClientDetected[] findRogue(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Map<String, MapLeafNode> nodesMap, Long pageId,
			double scale, String bssid, MapSettings mapSettings,
			boolean setDistance, Collection<JSONObject> rogueRssi, double[] xp,
			boolean verbose) throws Exception {
		MrHeatMap mrHeatMap = getHeatMap(mapContainerNode, nodes, 0, 0, false,
				0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		Search.ClientDetected[] clients = null;
		if (mrHeatMap != null) {
			mapContainerNode.setApElevation(0); // assume rogue APs are at the
			// same elevation
			Collection<String> rogueBssids = new Vector<String>();
			rogueBssids.add(bssid);
			clients = locateBssids(mapContainerNode, nodesMap, mrHeatMap,
					mapSettings, pageId, null, 0, scale, rogueBssids, true,
					rogueRssi, xp, true);
		}
		return clients;
	}

	private Collection<JSONObject> encodeRssi(Search.ClientDetected[] clients,
			Collection<JSONObject> rssis, String mac, boolean useHeatmap)
			throws Exception {
		if (clients == null || rssis.size() == 0) {
			rssis = new Vector<JSONObject>();
			JSONObject location = new JSONObject();
			location.put("mac", mac);
			location.put("x", -1);
			rssis.add(location);
			return rssis;
		}
		log.info_ln("x = iwsrls(rssi, p, q, apChannel, apElevation, le, gamma, ps)");
		for (Search.ClientDetected client : clients) {
			JSONObject rssi = new JSONObject();
			rssi.put("nodeId", "n" + client.getId());
			rssi.put("rssi", client.getRssiString() + " dBm");
			if (useHeatmap) {
				rssi.put("r", 0);
			} else {
				rssi.put("r", (int) client.getDistance());
				rssi.put("r", 0); // No circles
			}
			rssi.put("cx", (int) client.getXm());
			rssi.put("cy", (int) client.getYm());
			rssis.add(rssi);
		}
		return rssis;
	}

	public int calibrateRogue(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String bssid,
			double x, double y) throws Exception {
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
		double mapToMetric = mapContainerNode.getMapToMetric();
		double xm = x * mapToMetric;
		double ym = y * mapToMetric;
		log.info_ln("% xm: " + xm + ", ym: " + ym);
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		log.info_ln("% # AP macs: " + nodesMap.keySet().size());
		List<Idp> idps = (List<Idp>) QueryUtil.executeQuery(Idp.class,
				new SortParams("reportTime.time", false), new FilterParams(
						"ifMacAddress = :s1 AND stationType = :s2",
						new Object[] { bssid,
								BeCommunicationConstant.IDP_STATION_TYPE_AP }));
		if (idps.size() < mapSettings.getMinRssiCount()) {
			return 0;
		}
		int count = countQualifyingRssi(idps, nodesMap,
				mapSettings.getClientRssiThreshold());
		if (count < mapSettings.getMinRssiCount()) {
			return 0;
		}
		Collection<Trex> clients = new Vector<Trex>();
		log.info_ln("% " + count + " measurements for BSSID: " + bssid);
		findQualifyingRssi(idps, nodesMap, clients,
				mapSettings.getClientRssiThreshold(),
				mapContainerNode.getOwner(), xm, ym, 0);
		int removed = QueryUtil.bulkRemoveBos(Trex.class, new FilterParams(
				"tid", bssid), mapContainerNode.getOwner().getId());
		log.info_ln("% " + removed + " measurements removed.");
		QueryUtil.bulkCreateBos(clients);
		return clients.size();
	}

	public int calibrateClient(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String clientMac,
			double x, double y) throws Exception {
		MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
		double mapToMetric = mapContainerNode.getMapToMetric();
		double xm = x * mapToMetric;
		double ym = y * mapToMetric;
		log.info_ln("% xm: " + xm + ", ym: " + ym);
		Map<String, MapLeafNode> nodesMap = createNodesMap(nodes);
		log.info_ln("% # AP macs: " + nodesMap.keySet().size());
		if (nodesMap.keySet().size() == 0) {
			return 0;
		}

		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		List<LocationRssiReport> reports = getClientReports(mapContainerNode,
				nodesMap, clientMac, mapSettings);
		Search.ClientDetected[] clients = getClientDetected(reports, nodesMap,
				mapSettings);
		if (clients == null) {
			return 0;
		}

		double erp = getClientErp(mapContainerNode, nodesMap, nodes,
				mapSettings, clients);

		String note = "";
		// List<String> notes = (List<String>) QueryUtil
		// .executeQuery(
		// "select comment2 from "
		// + AhClientSession.class.getSimpleName(), null,
		// new FilterParams("clientMac", clientMac));
		List<String> notes = (List<String>) DBOperationUtil.executeQuery(
				"select comment2 from ah_clientsession", null,
				new FilterParams("clientMac", clientMac));
		if (notes.size() > 0) {
			note = notes.get(0);
		}

		Collection<Trex> trexs = new Vector<Trex>();
		for (LocationRssiReport report : reports) {
			MapLeafNode mapLeafNode = (MapLeafNode) nodesMap.get(report
					.getReporterMac());
			if (mapLeafNode == null) {
				continue;
			}
			if (report.getRssi() > hmRssiUntil) {
				continue;
			}
			Trex client = new Trex();
			client.setOwner(mapContainerNode.getOwner());
			client.setParentMap(mapContainerNode);
			client.tid = report.getClientMac();
			client.tx = xm;
			client.ty = ym;
			client.elevation = mapContainerNode.getApElevationMetric();
			client.rssi = report.getRssi();
			client.setTimeStamp(new HmTimeStamp(report.getReportTime()
					.getTime(), client.getOwner().getTimeZoneString()));
			// channel attribute in LocationRssiReport is actually the frequency
			// in KHz
			client.frequency = report.getChannel();
			client.erp = erp;
			client.rid = mapLeafNode.getApId();
			client.rx = mapLeafNode.getXm();
			client.ry = mapLeafNode.getYm();
			client.note = note;
			trexs.add(client);
		}

		// int removed = QueryUtil.bulkRemoveBos(Trex.class, new FilterParams(
		// "tid", clientMac), mapContainerNode.getOwner().getId());
		// logt.info("% " + removed + " measurements removed.");
		QueryUtil.bulkCreateBos(trexs);
		return trexs.size();
	}

	private List<LocationRssiReport> getClientReports(
			MapContainerNode mapContainerNode,
			Map<String, MapLeafNode> nodesMap, String clientMac,
			MapSettings mapSettings) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		List<LocationRssiReport> reports;
		if (mapSettings.isRealTime()) {
			calendar.add(Calendar.MINUTE, -lastRssiMaxAge);
			reports = findLastRssiReports(clientMac, nodesMap,
					calendar.getTime(), mapContainerNode.getOwner().getId());
		} else {
			calendar.add(Calendar.MINUTE, -mapSettings.getLocationWindow());
			reports = findRssiReports(clientMac, nodesMap, calendar.getTime(),
					mapContainerNode.getOwner().getId(),
					mapSettings.getClientRssiThreshold());
		}
		log.info_ln("% " + reports.size() + " measurements for BSSID: "
				+ clientMac);
		return reports;
	}

	private Search.ClientDetected[] getClientDetected(
			List<LocationRssiReport> reports,
			Map<String, MapLeafNode> nodesMap, MapSettings mapSettings) {
		int count = Math.abs(countQualifyingClientRssi(reports, nodesMap,
				mapSettings.getClientRssiThreshold()));
		if (count == 0) {
			return null;
		}
		Search.ClientDetected[] clients = new Search.ClientDetected[count];
		findQualifyingRssi(clients, reports, nodesMap, null,
				mapSettings.getClientRssiThreshold());
		return clients;
	}

	private double getClientErp(MapContainerNode mapContainerNode,
			Map<String, MapLeafNode> nodesMap, Set<MapNode> nodes,
			MapSettings mapSettings, Search.ClientDetected[] clients)
			throws Exception {
		double erp = mapSettings.getSurveyErp();
		if (!mapSettings.isUseSurveyErp()) {
			MrHeatMap mrHeatMap = getHeatMap(mapContainerNode, nodes, 0, 0,
					false, 0, 0, false, mapSettings.getRssiFrom(),
					mapSettings.getRssiUntil());
			if (mrHeatMap != null) {
				double plf = getPathLossFactor(
						mapContainerNode,
						mrHeatMap,
						mapSettings.isUseHeatmap()
								&& mapSettings.isCalibrateHeatmap());
				int[] iters = { 0, 0 };
				double[] s = leastSquare(mapContainerNode, nodesMap.values(),
						clients, plf, null, 0, iters);
				s = postls(mapContainerNode, clients, s);
				if (s != null) {
					erp = s[2];
				}
				while (erp > 12) {
					erp -= 1;
				}
			}
		}
		log.info_ln("% client erp: " + erp);
		return erp;
	}

	private Map<String, MapLeafNode> createNodesMap(Set<MapNode> nodes) {
		Map<String, MapLeafNode> nodesMap = new HashMap<String, MapLeafNode>();
		for (MapNode mapNode : nodes) {
			if (!mapNode.isLeafNode()) {
				continue;
			}
			MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
			nodesMap.put(mapLeafNode.getApId(), mapLeafNode);
		}
		return nodesMap;
	}

	private Search.ClientDetected[] locateBssids(
			MapContainerNode mapContainerNode,
			Map<String, MapLeafNode> nodesMap, MrHeatMap mrHeatMap,
			MapSettings mapSettings, Long pageId, Graphics2D g2,
			double metricToImage, double scale,
			Collection<String> foreignApBssids, boolean setDistance,
			Collection<JSONObject> clientLocations, double[] xp, boolean verbose)
			throws Exception {
		log.info_ln("% locateBssids at: " + new Date() + ", RSSI threshold: "
				+ mapSettings.getClientRssiThreshold());
		boolean useHeatmap = mapSettings.isUseHeatmap();
		boolean calibrateHeatmap = mapSettings.isCalibrateHeatmap();
		if (useHeatmap) {
			hmv(mrHeatMap, mapContainerNode, calibrateHeatmap, g2,
					metricToImage, verbose);
		}
		log.info_ln("mapX = " + mapContainerNode.getActualWidthMetric()
				+ "; mapY = " + mapContainerNode.getActualHeightMetric() + ";");
		double plf = getPathLossFactor(mapContainerNode, mrHeatMap, useHeatmap
				&& calibrateHeatmap);
		log.info_ln("gamma = " + gammaTxPower + "; ps = " + startingTxPower
				+ "; apElevation = " + mapContainerNode.getApElevationMetric()
				+ "; tol = " + svdPrecision + "; le = " + mrHeatMap.lsle
				+ "; dle = " + mrHeatMap.dle + "; plf = " + plf + ";");
		double mapToMetric = mapContainerNode.getMapToMetric();
		int inside = 0, outside = 0, srlsConverge = 0, totalSrlsIters = 0, totalGnIters = 0, srlsOutsideConverge = 0, totalOutsideSrlsIters = 0, totalOutsideGnIters = 0;
		int[] iters = { 0, 0 };
		Search.ClientDetected[] clients = null;
		double[] x = null;
		for (String bssid : foreignApBssids) {
			List<Idp> idps = (List<Idp>) QueryUtil
					.executeQuery(
							Idp.class,
							new SortParams("reportTime.time", false),
							new FilterParams(
									"ifMacAddress = :s1 AND stationType = :s2",
									new Object[] {
											bssid,
											BeCommunicationConstant.IDP_STATION_TYPE_AP }));
			if (idps.size() < mapSettings.getMinRssiCount()) {
				continue;
			}
			int count = countQualifyingRssi(idps, nodesMap,
					mapSettings.getClientRssiThreshold());
			if (count < mapSettings.getMinRssiCount()) {
				continue;
			}
			clients = new Search.ClientDetected[count];
			log.info_ln("% " + clients.length + " measurements for BSSID: "
					+ bssid);
			boolean rogue = findQualifyingRssi(idps, nodesMap,
					mrHeatMap.apIndexes, clients,
					mapSettings.getClientRssiThreshold(), verbose);
			if (g2 != null) {
				logt.info("locateBssids", "PRE-LS: just to draw hmm location.");
				x = Search.hmm(mrHeatMap.hmv, mrHeatMap.gridXsize,
						mrHeatMap.gridYsize, clients, 5, 25, g2, metricToImage,
						false);
				log.info_ln("% HMM-LS: (" + x[0] + ", " + x[1] + ", erp = "
						+ x[2]);
				x = leastSquare(mapContainerNode, nodesMap.values(), clients,
						plf, g2, metricToImage, iters);
				x = postls(mapContainerNode, clients, x);
			} else {
				x = locateMac(mapContainerNode, nodesMap, mrHeatMap,
						useHeatmap, calibrateHeatmap, clients, null, iters,
						plf, g2, metricToImage, setDistance, verbose);
				if (x != null) {
					if (xp != null) {
						xp[0] = x[0];
						xp[1] = x[1];
						xp[2] = x[2];
					}
					x = postls(mapContainerNode, clients, x);
				}
			}
			if (iters[0] > maxSrlsIters) {
				iters[0]--;
			}
			if (iters[1] < 0) {
				clients = null;
			}
			if (x == null) {
				outside++;
				totalOutsideSrlsIters += iters[0];
				totalOutsideGnIters += iters[1];
				if (iters[1] == 0) {
					srlsOutsideConverge++;
				}
			} else {
				inside++;
				totalSrlsIters += iters[0];
				totalGnIters += iters[1];
				if (iters[1] == 0) {
					srlsConverge++;
				}
				if (g2 == null) {
					JSONObject location = new JSONObject();
					location.put("x", Math.round(x[0] / mapToMetric * scale));
					location.put("y", Math.round(x[1] / mapToMetric * scale));
					location.put("tp", rogue ? "R" : "F");
					location.put("mac", bssid);
					if (clientLocations.size() == 0) {
						location.put("pageId", pageId);
					}
					clientLocations.add(location);
				}
			}
		}
		postLocate(clients, x, plf, scale, mapToMetric, setDistance, inside,
				outside, srlsConverge, totalSrlsIters, totalGnIters,
				srlsOutsideConverge, totalOutsideSrlsIters, totalOutsideGnIters);
		return clients;
	}

	private void postLocate(Search.ClientDetected[] clients, double[] x,
			double plf, double scale, double mapToMetric, boolean setDistance,
			int inside, int outside, int srlsConverge, int totalSrlsIters,
			int totalGnIters, int srlsOutsideConverge,
			int totalOutsideSrlsIters, int totalOutsideGnIters) {
		log.info_ln("% POST-LS: inside: " + inside + " (" + srlsConverge
				+ " c, " + totalSrlsIters + " tr, " + totalGnIters
				+ " gn), outside: " + outside + " (" + srlsOutsideConverge
				+ " c, " + totalOutsideSrlsIters + " tr, "
				+ totalOutsideGnIters + " gn)");
		int total = inside + outside;
		if (total == 0) {
			total = 1;
		}
		srlsConverge += srlsOutsideConverge;
		totalSrlsIters += totalOutsideSrlsIters;
		totalGnIters += totalOutsideGnIters;
		log.info_ln("% POST-LS: total: "
				+ total
				+ " ("
				+ srlsConverge
				+ " c, "
				+ totalSrlsIters
				+ " tr, "
				+ totalGnIters
				+ String.format(" gn) or (%.2f%% c, %.2f tr, %.2f gn)", 100.0
						* srlsConverge / total, (float) totalSrlsIters / total,
						(float) totalGnIters / total + 0.0));
		if (clients != null && x != null && setDistance) {
			for (Search.ClientDetected client : clients) {
				client.setDistance(getDistance(x[2] - client.getRssi(),
						client.getC(), plf)
						/ mapToMetric * scale);
				client.setXm(client.getXm() / mapToMetric * scale);
				client.setYm(client.getYm() / mapToMetric * scale);
			}
		}
	}

	private void hmv(MrHeatMap mrHeatMap, MapContainerNode mapContainerNode,
			boolean calibrateHeatmap, Graphics2D g2, double metricToImage,
			boolean verbose) {
		double apElevation = mapContainerNode.getApElevationMetric()
				- elevationMargin;
		if (apElevation < 0) {
			apElevation = 0;
		}
		double actualWidthMetric = mapContainerNode.getActualWidthMetric();
		double actualHeightMetric = mapContainerNode.getActualHeightMetric();
		double squareSize = 0.4;
		int squareCount = 6000;
		int gridXcount = (int) (actualWidthMetric / squareSize);
		int gridYcount = (int) (actualHeightMetric / squareSize);
		if (gridXcount * gridYcount > squareCount) {
			squareSize = Math.sqrt(actualWidthMetric * actualHeightMetric
					/ squareCount);
			gridXcount = (int) (actualWidthMetric / squareSize);
			gridYcount = (int) (actualHeightMetric / squareSize);
		}
		double gridXsize = actualWidthMetric / gridXcount;
		double gridYsize = actualHeightMetric / gridYcount;
		if (verbose) {
			log.info_ln("% HMV-LS: map size (" + actualWidthMetric + ", "
					+ actualHeightMetric + "), grid " + gridXcount * gridYcount
					+ " (" + gridXcount + ", " + gridYcount + "), size ("
					+ gridXsize + ", " + gridYsize + ")");
		}
		WallLoss[] walls = getWallsMetric(mapContainerNode.getWalls(),
				mapContainerNode.getMapToMetric());
		double plf = getPathLossFactor(mapContainerNode, mrHeatMap,
				calibrateHeatmap);
		Date start = new Date();
		mrHeatMap.hmv = Search.hmv(mrHeatMap.apX, mrHeatMap.apY, apElevation,
				mrHeatMap.gridX, mrHeatMap.gridY, mrHeatMap.B, gridXcount + 1,
				gridXsize, gridYcount + 1, gridYsize, calibrateHeatmap,
				plf / 10.0, mrHeatMap.dle, null /* g2 */, metricToImage, walls);
		Date end = new Date();
		if (verbose) {
			log.info_ln("% HMV-LS: hmv created in "
					+ (end.getTime() - start.getTime()) + " ms.");
		}
		mrHeatMap.gridXsize = gridXsize;
		mrHeatMap.gridYsize = gridYsize;
	}

	private double[] leastSquare(MapContainerNode mapContainerNode,
			Collection<?> nodes, Search.ClientDetected[] clients, double plf,
			Graphics2D g2, double metricToImage, int[] iters) throws Exception {
		double h2 = mapContainerNode.getApElevationMetric() - elevationMargin;
		if (h2 > 0) {
			h2 = h2 * h2;
		} else {
			h2 = 0;
		}
		// double[] x = iwsrls(mapContainerNode, clients, imageWidth, plf,
		// image, useA, h2, iters);
		if (g2 != null) {
			// Draw distances for startingTxPower
			for (Search.ClientDetected client : clients) {
				double pathLoss = getClientPowerLevel() - client.getRssi();
				double distance = getDistance(pathLoss, client.getC(), plf);
				int radius = (int) Math.round(distance * metricToImage);
				int x = (int) Math.round(client.getXm() * metricToImage);
				int y = (int) Math.round(client.getYm() * metricToImage);
				g2.setColor(Color.yellow);
				// g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);
			}

			// Trilateration only needs the highest 3 rssi
			java.util.Arrays.sort(clients,
					new Comparator<Search.ClientDetected>() {
						public int compare(Search.ClientDetected client1,
								Search.ClientDetected client2) {
							Integer rssi1 = client1.getRssi();
							Integer rssi2 = client2.getRssi();
							return rssi2.compareTo(rssi1);
						}
					});
			Point2D solution = trilaterate(mapContainerNode, clients, plf, g2,
					metricToImage, false);
			if (solution == null) {
				solution = new Point2D.Double(
						mapContainerNode.getActualWidthMetric() / 2,
						mapContainerNode.getActualHeightMetric() / 2);
			} else {
				logt.info("leastSquare", "Trilaterate: (" + solution.getX()
						+ ", " + solution.getY() + ")");
			}
			double[] x = leastSquare(mapContainerNode, clients, plf, g2,
					metricToImage, h2,
					new double[] { solution.getX(), solution.getY(),
							startingTxPower }, iters);
			if (g2 != null && x != null) {
				g2.setColor(Color.black);
				Point2D be = new Point2D.Double(x[0], x[1]);
				Search.fillOval(g2, be, metricToImage);
			}
		}
		double[] x;
		Date start = new Date();
		if (clients.length == 2) {
			double dx = clients[1].getXm() - clients[0].getXm();
			double dy = clients[1].getYm() - clients[0].getYm();
			double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			double d1 = d
					/ (1 + Math
							.pow(10,
									(clients[0].getRssi() - clients[1]
											.getRssi()) / plf));
			x = new double[] {
					clients[0].getXm() + dx * d1 / d,
					clients[0].getYm() + dy * d1 / d,
					clients[0].getRssi() + clients[0].getC() + plf
							* Math.log10(d1) };
			log.info_ln("Only 2 measurements, pwr: " + x[2]);
		} else {
			x = Search.lstr(clients, metricToImage, plf, getClientPowerLevel(),
					h2, Math.sqrt(gammaTxPower), maxSrlsIters, svdPrecision,
					g2, iters, lstrVerbose);
			if (x != null && x[2] < 5) {
				log.info_ln("x = [" + x[0] + "; " + x[1] + "; " + x[2]
						+ "] % in " + iters[0]
						+ " LSTR iterations, try higher gamma.");
				iters[0] = 0;
				x = Search.lstr(clients, metricToImage, plf,
						getClientPowerLevel(), h2, Math.sqrt(3 * gammaTxPower),
						maxSrlsIters, svdPrecision, g2, iters, lstrVerbose);
			}
		}
		Date end = new Date();
		int tri = iters[0];
		if (tri > maxSrlsIters) {
			tri--;
		}
		if (x != null) {
			log.info_ln("x = [" + x[0] + "; " + x[1] + "; " + x[2] + "] % in "
					+ tri + " LSTR iterations - "
					+ (end.getTime() - start.getTime()) + " ms.");
		}

		iters[1] = 0;
		if (iters[0] > maxSrlsIters || x == null) {
			x = leastSquare(mapContainerNode, clients, plf, g2, metricToImage,
					h2, x, iters);
		}
		if (x == null) {
			return null;
		}
		if (g2 != null) {
			drawUpdatedContours(g2, clients, metricToImage, plf, x[2]);
			g2.setColor(Color.black);
			Point2D be = new Point2D.Double(x[0], x[1]);
			Search.fillOval(g2, be, metricToImage);
		}
		return x;
	}

	private double[] postls(MapContainerNode mapContainerNode,
			Search.ClientDetected[] clients, double[] x) {
		if (x == null) {
			return null;
		} else {
			x = boundaryCondition(mapContainerNode, x);
		}
		if (x == null) {
			return null;
		}
		MapSettings mapSettings = BeTopoModuleUtil
				.getMapGlobalSetting(mapContainerNode.getOwner());
		if (!mapSettings.isPeriVal()) {
			return x;
		}
		if (convexCondition(clients, x)) {
			return x;
		} else {
			return null;
		}
	}

	private double[] boundaryCondition(MapContainerNode mapContainerNode,
			double[] x) {
		double actualWidth = mapContainerNode.getActualWidth();
		double actualHeight = mapContainerNode.getActualHeight();
		if (mapContainerNode.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
			actualWidth *= LocationTracking.FEET_TO_METERS;
			actualHeight *= LocationTracking.FEET_TO_METERS;
		}
		double x1 = x[0];
		double x2 = x[1];
		if (x1 < 0 || x1 > actualWidth || x2 < 0 || x2 > actualHeight) {
			log.info_ln("% POST-LS: Estimate outside boundaries: (" + x1 + ", "
					+ x2 + ")");
			if (true) {
				return null;
			}
			if (x1 < 0) {
				x1 = 0;
			}
			if (x1 > mapContainerNode.getActualWidth()) {
				x1 = mapContainerNode.getActualWidth();
			}
			if (x2 < 0) {
				x2 = 0;
			}
			if (x2 > mapContainerNode.getActualHeight()) {
				x2 = mapContainerNode.getActualHeight();
			}
		}
		return x;
	}

	private boolean convexCondition(Search.ClientDetected[] clients, double[] x) {
		double left = Double.MAX_VALUE, right = 0, top = Double.MAX_VALUE, bottom = 0;
		String clientMac = null;
		for (Search.ClientDetected client : clients) {
			clientMac = client.getClientMac();
			if (client.getXm() < left) {
				left = client.getXm();
			}
			if (client.getXm() > right) {
				right = client.getXm();
			}
			if (client.getYm() < top) {
				top = client.getYm();
			}
			if (client.getYm() > bottom) {
				bottom = client.getYm();
			}
		}
		left -= perimeterMargin;
		right += perimeterMargin;
		top -= perimeterMargin;
		bottom += perimeterMargin;
		if (x[0] > left && x[0] < right && x[1] > top && x[1] < bottom) {
			return true;
		} else {
			log.info_ln("% POST-LS: Convex condition failed for: " + clientMac);
			return false;
		}
	}

	private static int setMetricNodePositions(Collection<?> nodes,
			double mapToMetric) {
		int apCount = 0;
		for (MapNode node : (Collection<MapNode>) nodes) {
			if (!node.isLeafNode()) {
				continue;
			}
			MapLeafNode leafNode = (MapLeafNode) node;
			leafNode.setXm(node.getX() * mapToMetric);
			leafNode.setYm(node.getY() * mapToMetric);
			apCount++;
		}
		return apCount;
	}

	private int countQualifyingRssi(List<Idp> idps,
			Map<String, MapLeafNode> nodesMap, int rssiFrom) {
		short idpCount = 0;
		for (Idp idp : idps) {
			MapLeafNode mapLeafNode = (MapLeafNode) nodesMap.get(idp
					.getReportNodeId());
			if (mapLeafNode == null) {
				// Reporting AP is not on this map.
				continue;
			}
			if (idp.getRssi() - noiseFloor < rssiFrom
					|| idp.getRssi() - noiseFloor > hmRssiUntil) {
				log.debug("findQualifyingRssi",
						"Rejected RSSI: " + (idp.getRssi() - noiseFloor));
				continue;
			}
			idpCount++;
		}
		return idpCount;
	}

	private static boolean findQualifyingRssi(List<Idp> idps,
			Map<String, MapLeafNode> nodesMap, Map apIndexes,
			Search.ClientDetected[] clients, int rssiFrom, boolean verbose) {
		boolean rogue = false;
		int idpIndex = 0;
		for (Idp idp : idps) {
			MapLeafNode mapLeafNode = (MapLeafNode) nodesMap.get(idp
					.getReportNodeId());
			if (mapLeafNode == null) {
				// Reporting AP is not on this map.
				continue;
			}
			if (idp.getRssi() - noiseFloor < rssiFrom
					|| idp.getRssi() - noiseFloor > hmRssiUntil) {
				continue;
			}
			if (idp.getIdpType() == BeCommunicationConstant.IDP_TYPE_ROGUE) {
				rogue = true;
			}
			Search.ClientDetected client = new Search.ClientDetected();
			client.setClientMac(idp.getIfMacAddress());
			client.setRssi(idp.getRssi() - noiseFloor);
			client.setDetectedTime(new Date(idp.getReportTime().getTime()));
			client.setChannel(idp.getChannel());
			client.setC(20 * Math.log10(Search.getFrequency(client.getChannel())) - 28);
			client.setXm(mapLeafNode.getXm());
			client.setYm(mapLeafNode.getYm());
			client.setId(mapLeafNode.getId());
			if (apIndexes != null) {
				Integer apIndex = (Integer) apIndexes
						.get(mapLeafNode.getApId());
				if (apIndex != null) {
					client.apIndex = apIndex;
				}
			}
			double distance = getDistance(
					getClientPowerLevel() - client.getRssi(), client.getC(), 32);
			log.debug(
					"findQualifyingRssi",
					"starting distance between reporting AP: "
							+ mapLeafNode.getApName() + " and rogue: "
							+ client.getClientMac() + " is: " + distance);
			clients[idpIndex++] = client;
		}
		if (verbose) {
			logRssiMeasurements(clients);
		}
		return rogue;
	}

	private static void logSurvey(double[] RX, double[] RY, double[] TX,
			double[] TY, double[] TH, double[] power, int[] frequency,
			int[] RSSI) {
		log("RX", RX);
		log("RY", RY);
		log("TX", TX);
		log("TY", TY);
		log("TH", TH);
		log("power", power);
		log("frequency", frequency);
		log("RSSI", RSSI);
	}

	private static void log(String name, double[] v) {
		log.info_non(name + " = [");
		for (int i = 0; i < v.length; i++) {
			log.info_non(v[i] + "; ");
			if ((i + 1) % 5 == 0 && i != v.length - 1) {
				log.info_non("\n      ");
			}
		}
		log.info_ln("];");
	}

	private static void log(String name, int[] v) {
		log.info_non(name + " = [");
		for (int i = 0; i < v.length; i++) {
			log.info_non(v[i] + "; ");
			if ((i + 1) % 5 == 0 && i != v.length - 1) {
				log.info_non("\n      ");
			}
		}
		log.info_ln("];");
	}

	private static void logRssiMeasurements(Search.ClientDetected[] clients) {
		log.info_non("p = [");
		for (Search.ClientDetected client : clients) {
			log.info_non(client.getXm() + "; ");
		}
		log.info_non("];\nq = [");
		for (Search.ClientDetected client : clients) {
			log.info_non(client.getYm() + "; ");
		}
		log.info_non("];\napi = [");
		for (Search.ClientDetected client : clients) {
			log.info_non(client.apIndex + "; ");
		}
		log.info_non("];\nrssi = [");
		for (Search.ClientDetected client : clients) {
			log.info_non(client.getRssiString() + "; ");
		}
		log.info_non("];\napChannel = [");
		for (Search.ClientDetected client : clients) {
			log.info_non(client.getChannel() + "; ");
		}
		log.info_non("];\nC = [");
		for (Search.ClientDetected client : clients) {
			log.info_non(client.getC() + "; ");
		}
		log.info_ln("];");
	}

	private static void findQualifyingRssi(List<Idp> idps,
			Map<String, MapLeafNode> nodesMap, Collection<Trex> clients,
			int rssiFrom, HmDomain owner, double tx, double ty, double elevation) {
		for (Idp idp : idps) {
			MapLeafNode mapLeafNode = (MapLeafNode) nodesMap.get(idp
					.getReportNodeId());
			if (mapLeafNode == null) {
				// Reporting AP is not on this map.
				continue;
			}
			if (idp.getRssi() - noiseFloor < rssiFrom
					|| idp.getRssi() - noiseFloor > hmRssiUntil) {
				continue;
			}
			Trex client = new Trex();
			client.setOwner(owner);
			client.rssi = idp.getRssi() - noiseFloor;
			client.erp = startingTxPower;
			client.tid = idp.getIfMacAddress();
			client.tx = tx;
			client.ty = ty;
			client.rid = mapLeafNode.getApId();
			client.rx = mapLeafNode.getXm();
			client.ry = mapLeafNode.getYm();
			client.elevation = elevation;
			client.frequency = (int) Search.getFrequency(idp.getChannel());
			client.setTimeStamp(new HmTimeStamp(idp.getReportTime().getTime(),
					client.getOwner().getTimeZoneString()));

			log.info_ln("AP: " + client.rid + "rssi: " + client.rssi);
			clients.add(client);
		}
	}

	private void syncRssi() {
		Date reportTime = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(reportTime);
		calendar.add(Calendar.SECOND, -rssiWindow);
		Date from = calendar.getTime();
		calendar.add(Calendar.SECOND, 2 * rssiWindow);
		Date until = calendar.getTime();
	}

	public Point2D trilaterate(MapContainerNode mapContainerNode,
			Search.ClientDetected[] nodes, double plfStart, Graphics2D g2,
			double metricToImage, boolean adjustPLE) throws Exception {
		double attenuation = 0;
		initPlParams(nodes, plfStart, attenuation);
		double plf = plfStart;

		Date start = new Date();
		Point2D[] p1i = { new Point2D.Double(), new Point2D.Double() };
		Point2D[] p2i = { new Point2D.Double(), new Point2D.Double() };
		Point2D[] p3i = { new Point2D.Double(), new Point2D.Double() };

		boolean validSolution = false;
		boolean upwardAdjusted = false;
		boolean downwardAdjusted = false;
		boolean reverseDirection = true; // Don't ever reverse direction
		Point2D t1 = null, t2 = null, t3 = null, t1alt = null, t2alt = null, t3alt = null;
		do {
			Point2D ap1 = null, ap2 = null, ap3 = null;
			double r1 = 0, r2 = 0, r3 = 0;
			for (int i = 0; i < 3; i++) {
				Search.ClientDetected client = nodes[i];
				double rssi = client.getRssi();
				double pathLoss = getClientPowerLevel()
						- client.getAttenuation() - rssi;
				double distance = getDistance(pathLoss, client.getC(),
						client.getPlf());
				log.debug("trilaterate", "distance: " + distance);
				Point2D ap = new Point2D.Double(client.getXm(), client.getYm());
				if (ap1 == null) {
					ap1 = ap;
					r1 = distance;
				} else if (ap2 == null) {
					ap2 = ap;
					r2 = distance;
				} else {
					ap3 = ap;
					r3 = distance;
				}
				if (g2 != null) {
					int radius = (int) Math.round(distance * metricToImage);
					int x = (int) Math.round(client.getXm() * metricToImage);
					int y = (int) Math.round(client.getYm() * metricToImage);
					if (!upwardAdjusted && !downwardAdjusted) {
						g2.setColor(Color.yellow);
					} else {
						g2.setColor(Color.black);
					}
					g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);
				}
			}

			/*
			 * Calculate the 2 intersection points between any of the 3 pair of
			 * APs. If only 2 circles overlap, still draw the intersection
			 * points in debug mode.
			 */
			CciType cci1 = circleCircleIntersection(ap1, r1, ap2, r2, p1i[0],
					p1i[1]);
			CciType cci2 = circleCircleIntersection(ap1, r1, ap3, r3, p2i[0],
					p2i[1]);
			CciType cci3 = circleCircleIntersection(ap2, r2, ap3, r3, p3i[0],
					p3i[1]);

			boolean adjustHigher = false, adjustLower = false;
			if (cci1 == CciType.CCI_NO_OVERLAP
					|| cci2 == CciType.CCI_NO_OVERLAP
					|| cci3 == CciType.CCI_NO_OVERLAP) {
				// Circles need to grow to overlap, so lower the path
				// loss exponent.
				adjustLower = true;
			} else if (cci1 == CciType.CCI_CONTAINED
					|| cci2 == CciType.CCI_CONTAINED
					|| cci3 == CciType.CCI_CONTAINED) {
				// Try to find a solution first by shrinking the circles, so
				// increase the path loss exponent.
				adjustHigher = true;
			}
			if (adjustHigher || adjustLower) {
				if ((adjustHigher && !downwardAdjusted) || upwardAdjusted) {
					/*
					 * Adjust higher if either a prior upward adjustment was
					 * made, or a request is made to adjust higher in the case
					 * of no prior adjustment.
					 */
					if (adjustPLE) {
						if (plf < 100) {
							// Adjust the path loss component higher
							plf += plfStep;
							upwardAdjusted = true;
							logt.info("trilaterate",
									"Upward adjustment on path loss exponent: "
											+ plf);
							updatePlf(nodes, plfStep);
							continue;
						} else if (!reverseDirection) {
							log.info("trilaterate",
									"Done adjusting higher, try the other direction.");
							plf = plfStart;
							upwardAdjusted = false;
							downwardAdjusted = true;
							reverseDirection = true;
							initPlParams(nodes, plf, attenuation);
							continue;
						} else {
							log.info("trilaterate",
									"Done adjusting higher or lower, no solution.");
							break;
						}
					} else {
						if (attenuation < attenuationRange) {
							// Adjust the path loss component higher
							attenuation += attenuationStep;
							upwardAdjusted = true;
							log.debug("trilaterate",
									"Upward adjustment on attenuation: "
											+ attenuation);
							updateAttenuation(nodes, attenuationStep);
							continue;
						} else if (!reverseDirection) {
							log.info("trilaterate",
									"Done adjusting higher, try the other direction.");
							attenuation = 0;
							upwardAdjusted = false;
							downwardAdjusted = true;
							reverseDirection = true;
							initPlParams(nodes, plf, attenuation);
							continue;
						} else {
							log.info("trilaterate",
									"Done adjusting higher or lower, no solution.");
							break;
						}
					}
				} else if ((adjustLower && !upwardAdjusted) || downwardAdjusted) {
					/*
					 * Adjust lower if either a prior downward adjustment was
					 * made, or a request is made to adjust lower in the case of
					 * no prior adjustment.
					 */
					if (adjustPLE) {
						if (plf > 10) {
							// Adjust the path loss component lower
							plf -= plfStep;
							downwardAdjusted = true;
							logt.info("trilaterate",
									"Downward adjustment on path loss exponent: "
											+ plf);
							updatePlf(nodes, -plfStep);
							continue;
						} else if (!reverseDirection) {
							log.info("trilaterate",
									"Done adjusting lower, try the other direction.");
							plf = plfStart;
							downwardAdjusted = false;
							upwardAdjusted = true;
							reverseDirection = true;
							initPlParams(nodes, plf, attenuation);
							continue;
						} else {
							logt.info("trilaterate",
									"Done adjusting lower, no solution.");
							break;
						}
					} else {
						if (attenuation > -attenuationRange) {
							// Adjust the path loss component lower
							attenuation -= attenuationStep;
							downwardAdjusted = true;
							log.debug("trilaterate",
									"Downward adjustment on attenuation: "
											+ attenuation);
							updateAttenuation(nodes, -attenuationStep);
							continue;
						} else if (!reverseDirection) {
							log.info("trilaterate",
									"Done adjusting lower, try the other direction.");
							attenuation = 0;
							downwardAdjusted = false;
							upwardAdjusted = true;
							reverseDirection = true;
							initPlParams(nodes, plf, attenuation);
							continue;
						} else {
							logt.info("trilaterate",
									"Done adjusting lower, no solution.");
							break;
						}
					}
				} else {
					logt.info("trilaterate", "Done adjusting, no solution.");
					break;
				}
			} else {
				logt.info("trilaterate",
						"Found solution with attenuation adjustment of: "
								+ attenuation);
				double minSides = Double.MAX_VALUE;
				for (int p1Index = 0; p1Index < 2; p1Index++) {
					for (int p2Index = 0; p2Index < 2; p2Index++) {
						for (int p3Index = 0; p3Index < 2; p3Index++) {
							double newSides = triangleSides(p1i[p1Index],
									p2i[p2Index], p3i[p3Index]);
							if (newSides < minSides) {
								t1 = p1i[p1Index];
								t1alt = p1i[1 - p1Index];
								t2 = p2i[p2Index];
								t2alt = p2i[1 - p2Index];
								t3 = p3i[p3Index];
								t3alt = p3i[1 - p3Index];
								minSides = newSides;
							}
						}
					}
				}
			}

			log.debug("trilaterate", "t1: (" + t1.getX() + ", " + t1.getY()
					+ ")");
			log.debug("trilaterate", "t2: (" + t2.getX() + ", " + t2.getY()
					+ ")");
			log.debug("trilaterate", "t3: (" + t3.getX() + ", " + t3.getY()
					+ ")");
			if (g2 != null) {
				g2.setColor(Color.red);
				drawCrossHair(g2, t1, metricToImage);
				drawCrossHair(g2, t2, metricToImage);
				drawCrossHair(g2, t3, metricToImage);
				if (!upwardAdjusted && !downwardAdjusted) {
					g2.setColor(Color.black);
				} else {
					g2.setColor(Color.yellow);
				}
				drawCrossHair(g2, t1alt, metricToImage);
				drawCrossHair(g2, t2alt, metricToImage);
				drawCrossHair(g2, t3alt, metricToImage);
			}
			validSolution = true;
		} while (!validSolution);

		if (!validSolution) {
			// No valid slution, even after adjusting the path loss
			// exponent.
			return null;
		}

		Point2D midT1T2 = getMidPoint(t1, t2);
		Point2D midT1T3 = getMidPoint(t1, t3);
		Point2D be = llip(midT1T2, t3, midT1T3, t2);

		Date end = new Date();
		log.debug("trilaterate", "trilateration algorithm: "
				+ (end.getTime() - start.getTime()) + " ms.");
		if (g2 != null) {
			if (adjustPLE) {
				g2.setColor(Color.yellow);
			} else {
				g2.setColor(Color.yellow);
			}
			Search.fillOval(g2, be, metricToImage);
		}
		log.debug("trilaterate", "be: (" + be.getX() + ", " + be.getY() + ")");
		return be;
	}

	public void initPlParams(Search.ClientDetected[] nodes, double plf,
			double attenuation) {
		for (Search.ClientDetected client : nodes) {
			client.setPlf(plf);
			client.setAttenuation(attenuation);
		}
	}

	public void updatePlf(Search.ClientDetected[] nodes, double delta) {
		for (Search.ClientDetected client : nodes) {
			client.setPlf(client.getPlf() + delta);
		}
	}

	public void updateAttenuation(Search.ClientDetected[] nodes, double delta) {
		for (Search.ClientDetected client : nodes) {
			client.setAttenuation(client.getAttenuation() + delta);
		}
	}

	public double triangleSides(Point2D p1, Point2D p2, Point2D p3) {
		return p1.distance(p2) + p1.distance(p3) + p2.distance(p3);
	}

	private double piDiff(Point2D pi, Point2D pi_prime, Point2D p2, Point2D p3) {
		double pi_sides = pi.distance(p2) + pi.distance(p3);
		double pi_prime_sides = pi_prime.distance(p2) + pi_prime.distance(p3);
		return pi_sides - pi_prime_sides;
	}

	private static Point2D llip(Point2D l1p1, Point2D l1p2, Point2D l2p1,
			Point2D l2p2) {
		// Line between l1p1 and l1p2
		double a1 = l1p2.getY() - l1p1.getY();
		double b1 = l1p1.getX() - l1p2.getX();
		double c1 = a1 * l1p1.getX() + b1 * l1p1.getY();

		// Line between l2p1 and l2p2
		double a2 = l2p2.getY() - l2p1.getY();
		double b2 = l2p1.getX() - l2p2.getX();
		double c2 = a2 * l2p1.getX() + b2 * l2p1.getY();

		// Intersection point
		double det = a1 * b2 - a2 * b1;
		double x = (b2 * c1 - b1 * c2) / det;
		double y = (a1 * c2 - a2 * c1) / det;

		return new Point2D.Double(x, y);
	}

	private Point2D llip(Point2D l1p1, Point2D l1p2, Wall wall) {
		// Line between l1p1 and l1p2
		double a1 = l1p2.getY() - l1p1.getY();
		double b1 = l1p1.getX() - l1p2.getX();
		double c1 = a1 * l1p1.getX() + b1 * l1p1.getY();

		// Line between l2p1 and l2p2
		double a2 = wall.getY2() - wall.getY1();
		double b2 = wall.getX1() - wall.getX2();
		double c2 = a2 * wall.getX1() + b2 * wall.getY1();

		// Intersection point
		double det = a1 * b2 - a2 * b1;
		double x = (b2 * c1 - b1 * c2) / det;
		double y = (a1 * c2 - a2 * c1) / det;

		return new Point2D.Double(x, y);
	}

	private Point2D getMidPoint(Point2D p1, Point2D p2) {
		return new Point2D.Double(p1.getX() + (p2.getX() - p1.getX()) / 2,
				p1.getY() + (p2.getY() - p1.getY()) / 2);
	}

	private void drawCrossHair(Graphics2D g2, Point2D p, double scale) {
		int x = (int) Math.round(p.getX() * scale);
		int y = (int) Math.round(p.getY() * scale);
		int edge = 10;
		g2.drawLine(x - edge, y, x + edge, y);
		g2.drawLine(x, y - edge, x, y + edge);
	}

	private void drawLine(Graphics2D g2, Point2D p1, Point2D p2, double scale) {
		int x1 = (int) Math.round(p1.getX() * scale);
		int y1 = (int) Math.round(p1.getY() * scale);
		int x2 = (int) Math.round(p2.getX() * scale);
		int y2 = (int) Math.round(p2.getY() * scale);
		g2.drawLine(x1, y1, x2, y2);
	}

	private CciType circleCircleIntersection(Point2D p1, double r1, Point2D p2,
			double r2, Point2D pi, Point2D pi_prime) {
		double a, dx, dy, d, h, rx, ry;
		double x2, y2;
		double xi, yi, xi_prime, yi_prime;

		/*
		 * dx and dy are the vertical and horizontal distances between the
		 * circle centers.
		 */
		dx = p2.getX() - p1.getX();
		dy = p2.getY() - p1.getY();

		/* Determine the straight-line distance between the centers. */
		d = hypot(dx, dy);

		/* Check for solvability. */
		if (d > (r1 + r2)) {
			/* No solution, circles do not intersect. */
			log.debug("circleCircleIntersection",
					"No solution, circles do not intersect.");
			return CciType.CCI_NO_OVERLAP;
		}
		if (d < Math.abs(r1 - r2)) {
			/* No solution, one circle is contained in the other */
			log.debug("circleCircleIntersection",
					"No solution, one circle is contained in the other.");
			return CciType.CCI_CONTAINED;
		}

		/*
		 * Point 2 is the point where the line through the circle intersection
		 * points crosses the line between the circle centers.
		 */

		/* Determine the distance from point 0 to point 2. */
		a = ((r1 * r1) - (r2 * r2) + (d * d)) / (2.0 * d);

		/* Determine the coordinates of point 2. */
		x2 = p1.getX() + (dx * a / d);
		y2 = p1.getY() + (dy * a / d);

		/*
		 * Determine the distance from point 2 to either of the intersection
		 * points.
		 */
		h = Math.sqrt((r1 * r1) - (a * a));

		/*
		 * Now determine the offsets of the intersection points from point 2.
		 */
		rx = -dy * (h / d);
		ry = dx * (h / d);

		/* Determine the absolute intersection points. */

		xi = x2 + rx;
		xi_prime = x2 - rx;
		yi = y2 + ry;
		yi_prime = y2 - ry;

		log.debug("circleCircleIntersection", "pi: (" + xi + ", " + yi
				+ "), pi_prime: (" + xi_prime + ", " + yi_prime + ")");

		pi.setLocation(xi, yi);
		pi_prime.setLocation(xi_prime, yi_prime);
		return CciType.CCI_TWO_OVERLAP;
	}

	private CciType circleCircleIntersection(Point2D p1, double r1, Point2D p2,
			double r2, Point2D p3, double r3, Point2D pi, Point2D pi_prime) {
		circleCircleIntersection(p1, r1, p2, r2, pi, pi_prime);
		if (pi.distance(p3) < r3) {
			if (pi_prime.distance(p3) > r3) {
				return CciType.CCI_THREE_OVERLAP;
			} else {
				// No solution, both ap1 and ap2 inside r3
				return CciType.CCI_TWO_OVERLAP;
			}
		} else if (pi_prime.distance(p3) < r3) {
			// Swap
			double xi = pi.getX();
			double yi = pi.getY();
			pi.setLocation(pi_prime.getX(), pi_prime.getY());
			pi_prime.setLocation(xi, yi);
			return CciType.CCI_THREE_OVERLAP;
		} else {
			// No solution, both ap1 and ap2 outside r3
			return CciType.CCI_TWO_OVERLAP;
		}
	}

	private static double hypot(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}

	private double[] leastSquare(MapContainerNode mapContainerNode,
			Search.ClientDetected[] nodes, double plf, Graphics2D g2,
			double metricToImage, double h2, double[] startx, int[] iters)
			throws Exception {
		double[] x = getStartingPosition(nodes[0], nodes[1], startx);

		if (false) {
			// Using jama library
			double attenuation = 0;
			for (Search.ClientDetected client : nodes) {
				client.setPlf(plf);
				client.setAttenuation(attenuation);
				client.setDistance(getRssiDistance(client,
						getClientPowerLevel()));
			}
			Date start = new Date();
			// location = leastSquareCustom(g2, nodes, metricToImage, x,
			// location);
			Date end = new Date();
			logt.info(
					"leastSquare",
					"Least square algorithm: "
							+ (end.getTime() - start.getTime()) + " ms.");
		}

		// assumes matrix A has full rank
		Date start = new Date();
		int iterations = 0;
		int tc = 1;
		double[] sx = tc > 1 ? new double[] { x[0], x[1], x[2] } : null;
		for (int c = 0; c < tc; c++) {
			// found = gnDistance(nodes, x);
			// found = gnRssiBT(g2, nodes, x, useA,
			// iterations = gnRssiTxPowerBT(nodes, g2, metricToImage, x, plf,
			// h2);
			iterations = Search.lsgnbt(nodes, g2, metricToImage, x, plf, h2,
					gnIterations, gammaTxPower, startingTxPower, svdPrecision,
					lsgnVerbose);
			if (c + 1 < tc) {
				x[0] = sx[0];
				x[1] = sx[1];
				x[2] = sx[2];
			}
		}
		Date end = new Date();
		String iterationCount = iterations < 0 ? "> " + gnIterations : ""
				+ iterations;
		log.info_ln("x = [" + x[0] + "; " + x[1] + "; " + x[2] + "] % in "
				+ iterationCount + " GN iterations - "
				+ (end.getTime() - start.getTime()) + " ms.");
		if (iterations < 0) {
			iters[1] = -iterations;
			return null;
		}
		iters[1] = iterations;
		return x;
	}

	private double getRssiDistance(Search.ClientDetected client,
			double clientPowerLevel) {
		double rssi = client.getRssi();
		double pathLoss = clientPowerLevel - client.getAttenuation() - rssi;
		double distance = getDistance(pathLoss, client.getC(), client.getPlf());
		log.debug("getRssiDistance", "distance: " + distance);
		return distance;
	}

	private double[] getStartingPosition(Search.ClientDetected client1,
			Search.ClientDetected client2, double[] startx) throws Exception {
		double x1 = 0;
		double x2 = 0;
		double x3 = 0;
		if (startx == null) {
			// Use position between 2 highest RSSI APs as a starting point
			x1 = (client1.getXm() + client2.getXm()) / 2;
			x2 = (client1.getYm() + client2.getYm()) / 2;
			x3 = startingTxPower;
			// Usually 0,0 is better as a starting position
			x1 = 0;
			x2 = 0;
		} else {
			x1 = startx[0];
			x2 = startx[1];
			x3 = startx[2];
		}
		log.debug("getStartingPosition", "Starting position: " + x1 + ", " + x2
				+ ", " + x3);
		return new double[] { x1, x2, x3 };
	}

	/*
	 * Assumes matrix A has full rank
	 */
	private boolean gnDistance(List<Search.ClientDetected> nodes, double[] x) {
		for (int iteration = 0; iteration < gnIterations; iteration++) {
			double x1 = x[0];
			double x2 = x[1];
			logt.info("gnDistance", "Iteration: " + iteration + ", (" + x1
					+ ", " + x2 + ")");
			int m = nodes.size();
			int n = 2;
			double[] r = new double[m];
			double[][] a = new double[m][n];
			for (int i = 0; i < m; i++) {
				Search.ClientDetected client = nodes.get(i);
				double p = client.getXm();
				double q = client.getYm();
				double d = Math.sqrt(Math.pow((x1 - p), 2)
						+ Math.pow((x2 - q), 2));
				// This is actually (- r)
				r[i] = client.getDistance() - d;
				a[i][0] = (x1 - p) / d;
				a[i][1] = (x2 - q) / d;
			}

			double alpha = 2;
			double beta = 0;
			double[] atr = new double[n];
			// Bereken ATR = 2 * A' * R
			DGEMV.DGEMV("T", m, n, alpha, a, r, 1, beta, atr, 1);
			double norm2 = DNRM2.DNRM2(n, atr, 1);
			log.debug("gnDistance", "Norm2: " + norm2);
			if (norm2 < svdPrecision) {
				return true;
			}

			int nrhs = 1;
			double[][] b = new double[m][nrhs];
			// b = a * x - r
			for (int i = 0; i < m; i++) {
				double s = 0;
				for (int j = 0; j < n; j++) {
					s += a[i][j] * x[j];
				}
				b[i][0] = s + r[i];
			}

			intW info = new intW(1);
			double[] work = new double[1];
			DGELS.DGELS("N", m, n, nrhs, a, b, work, -1, info);
			int lwork = (int) work[0];
			work = new double[lwork];
			DGELS.DGELS("N", m, n, nrhs, a, b, work, lwork, info);
			log.debug("gnDistance", "Work: " + lwork + ", result code: "
					+ info.val);
			if (info.val < 0) {
				logt.info("gnDistance", "Illegal value for argument: "
						+ -info.val);
				return false;
			}
			if (!Search.vlss(b)) {
				return false;
			}
			x[0] = b[0][0];
			x[1] = b[1][0];
		}
		return false;
	}

	/*
	 * Assumes matrix A has full rank
	 */
	private boolean gnRssi(List<Search.ClientDetected> nodes, double[] x,
			boolean useA, double lossExponent) {
		for (int iteration = 0; iteration < gnIterations; iteration++) {
			double x1 = x[0];
			double x2 = x[1];
			logt.info("gnRssi", "Iteration: " + iteration + ", (" + x1 + ", "
					+ x2 + ")");
			int m = nodes.size();
			int n = 2;
			double[] r = new double[m];
			double[][] a = new double[m][n];
			double clientPowerLevel = getClientPowerLevel();
			double drc = -lossExponent / Math.log(10);
			for (int i = 0; i < m; i++) {
				Search.ClientDetected client = nodes.get(i);
				double p = client.getXm();
				double q = client.getYm();
				double ds = Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2);
				double d = Math.sqrt(ds);
				double pathLoss = 0;
				if (useA) {
					pathLoss = getPathLossA(d, lossExponent);
				} else {
					pathLoss = getPathLossBG(d, lossExponent);
				}
				double estimatedRssi = clientPowerLevel - pathLoss;
				log.debug("gnRssi", "Estimated RSSI: " + estimatedRssi
						+ ", measured RSSI: " + client.getRssi());
				r[i] = estimatedRssi - client.getRssi();
				a[i][0] = drc * (x1 - p) / ds;
				a[i][1] = drc * (x2 - q) / ds;
			}

			double alpha = 2;
			double beta = 0;
			double[] atr = new double[n];
			// Bereken ATR = 2 * A' * R
			DGEMV.DGEMV("T", m, n, alpha, a, r, 1, beta, atr, 1);
			double norm2 = DNRM2.DNRM2(n, atr, 1);
			log.debug("gnRssi", "Norm2: " + norm2);
			if (norm2 < svdPrecision) {
				return true;
			}

			int nrhs = 1;
			double[][] b = new double[m][nrhs];
			// b = a * x - r
			for (int i = 0; i < m; i++) {
				double s = 0;
				for (int j = 0; j < n; j++) {
					s += a[i][j] * x[j];
				}
				b[i][0] = s - r[i];
			}

			intW info = new intW(1);
			double[] work = new double[1];
			DGELS.DGELS("N", m, n, nrhs, a, b, work, -1, info);
			int lwork = (int) work[0];
			work = new double[lwork];
			DGELS.DGELS("N", m, n, nrhs, a, b, work, lwork, info);
			log.debug("gnRssi", "Work: " + lwork + ", result code: " + info.val);
			if (info.val < 0) {
				logt.info("gnRssi", "Illegal value for argument: " + -info.val);
				return false;
			}
			if (!Search.vlss(b)) {
				return false;
			}
			x[0] = b[0][0];
			x[1] = b[1][0];
		}
		return false;
	}

	/*
	 * Assumes matrix A has full rank
	 */
	private boolean gnRssiTxPower(Graphics2D g2,
			List<Search.ClientDetected> nodes, double[] x, boolean useA,
			double lossExponent) {
		for (int iteration = 0; iteration < gnIterations; iteration++) {
			double x1 = x[0];
			double x2 = x[1];
			double x3 = x[2];
			logt.info("gnRssiTxPower", "Iteration: " + iteration + ", (" + x1
					+ ", " + x2 + ", " + x3 + ")");
			int m = nodes.size() + 1;
			int n = 3;
			double[] r = new double[m];
			double[][] a = new double[m][n];
			// Not a constant anymore
			double clientPowerLevel = x3;
			double drc = -lossExponent / Math.log(10);
			for (int i = 0; i < m - 1; i++) {
				Search.ClientDetected client = nodes.get(i);
				double p = client.getXm();
				double q = client.getYm();
				double ds = Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2);
				double pathLoss = 0;
				if (useA) {
					pathLoss = getPathLossA(ds, lossExponent / 2);
				} else {
					pathLoss = getPathLossBG(ds, lossExponent / 2);
				}
				double estimatedRssi = clientPowerLevel - pathLoss;
				log.debug("gnRssiTxPower", "Estimated RSSI: " + estimatedRssi
						+ ", measured RSSI: " + client.getRssi());
				r[i] = estimatedRssi - client.getRssi();
				a[i][0] = drc * (x1 - p) / ds;
				a[i][1] = drc * (x2 - q) / ds;
				a[i][2] = 1;
			}
			double gammaSqrt = Math.sqrt(gammaTxPower);
			// Extra term in de kostfunktie
			r[m - 1] = gammaSqrt * (x3 - startingTxPower);
			// Eerste afgeleide
			a[m - 1][0] = 0;
			a[m - 1][1] = 0;
			a[m - 1][2] = gammaSqrt;

			double alpha = 2;
			double beta = 0;
			double[] atr = new double[n];
			// Bereken ATR = 2 * A' * R
			DGEMV.DGEMV("T", m, n, alpha, a, r, 1, beta, atr, 1);
			double norm2 = DNRM2.DNRM2(n, atr, 1);
			logt.info("gnRssiTxPower", "Norm2: " + norm2);
			if (norm2 < svdPrecision) {
				return true;
			}

			int nrhs = 1;
			double[][] b = new double[m][nrhs];
			// b = a * x - r
			for (int i = 0; i < m; i++) {
				double s = 0;
				for (int j = 0; j < n; j++) {
					s += a[i][j] * x[j];
				}
				b[i][0] = s - r[i];
			}

			intW info = new intW(1);
			double[] work = new double[1];
			DGELS.DGELS("N", m, n, nrhs, a, b, work, -1, info);
			int lwork = (int) work[0];
			work = new double[lwork];
			DGELS.DGELS("N", m, n, nrhs, a, b, work, lwork, info);
			log.debug("gnRssiTxPower", "Work: " + lwork + ", result code: "
					+ info.val);
			if (info.val < 0) {
				logt.info("gnRssiTxPower", "Illegal value for argument: "
						+ -info.val);
				return false;
			}
			if (!Search.vlss(b)) {
				return false;
			}
			x[0] = b[0][0];
			x[1] = b[1][0];
			x[2] = b[2][0];
		}
		return false;
	}

	private void drawUpdatedContours(Graphics2D g2,
			Search.ClientDetected[] nodes, double metricToImage,
			double lossExponent, double clientPowerLevel) {
		for (Search.ClientDetected client : nodes) {
			double rssi = client.getRssi();
			double pathLoss = clientPowerLevel - rssi;
			double distance = getDistance(pathLoss, client.getC(), lossExponent);
			log.debug("drawUpdatedContours", "distance: " + distance);
			int radius = (int) Math.round(distance * metricToImage);
			int x = (int) Math.round(client.getXm() * metricToImage);
			int y = (int) Math.round(client.getYm() * metricToImage);
			g2.setColor(Color.cyan);
			g2.drawOval(x - radius, y - radius, radius * 2, radius * 2);
		}
	}

	private Point2D leastSquareCustom(Graphics2D g2,
			List<Search.ClientDetected> nodes, double metricToImage,
			double[] x, Point2D location) throws Exception {
		double x1 = x[0];
		double x2 = x[1];
		double x3 = x[2];
		for (int i = 0; i < gnIterations; i++) {
			logt.info("leastSquareCustom", "iteration: " + i + ", (" + x1
					+ ", " + x2 + ", " + x3 + ")");
			if (Double.isInfinite(x1) || Double.isInfinite(x2)) {
				logt.info("leastSquareCustom",
						"Infinite least square solution is invalid.");
				return location;
			} else if (Double.isNaN(x1) || Double.isNaN(x2)) {
				logt.info("leastSquareCustom",
						"NaN least square solution is invalid.");
				return location;
			}
			Matrix mx = null;
			try {
				mx = leastSquareIteration(nodes, x1, x2);
			} catch (RuntimeException e) {
				logt.info("leastSquareCustom",
						"Iteration failed, no least square solution found.", e);
				return location;
			}
			if (mx == null) {
				if (g2 != null) {
					g2.setColor(Color.blue);
					Point2D be = new Point2D.Double(x1, x2);
					Search.fillOval(g2, be, metricToImage);
				}
				logt.info("leastSquareCustom",
						"Found least square solution in " + i
								+ " iterations: (" + x1 + ", " + x2 + ")");
				if (location == null) {
					location = new Point2D.Double();
				}
				location.setLocation(x1, x2);
				return location;
			}
			x1 = mx.get(0, 0);
			x2 = mx.get(1, 0);
			if (mx.getRowDimension() > 2) {
				x3 = mx.get(2, 0);
			}
		}
		logt.info("leastSquareCustom", "No least square solution found.");
		return location;
	}

	private Matrix leastSquareIteration(List<Search.ClientDetected> nodes,
			double x1, double x2) {
		int m = nodes.size();
		int n = 2;
		double[][] r = new double[m][1];
		double[][] a = new double[m][n];
		for (int i = 0; i < m; i++) {
			Search.ClientDetected client = nodes.get(i);
			double p = client.getXm();
			double q = client.getYm();
			double d = Math.sqrt(Math.pow((x1 - p), 2) + Math.pow((x2 - q), 2));
			r[i][0] = d - client.getDistance();
			a[i][0] = (x1 - p) / d;
			a[i][1] = (x2 - q) / d;
		}

		Matrix mr = new Matrix(r);
		Matrix ma = new Matrix(a);
		Matrix mat = ma.transpose();
		Matrix matr = mat.times(mr).timesEquals(2);

		double norm = matr.norm2();
		logt.info("leastSquareIteration", "2norm: " + norm);

		if (norm < svdPrecision) {
			return null;
		} else {
			Matrix mx = new Matrix(n, 1);
			mx.set(0, 0, x1);
			mx.set(1, 0, x2);
			Matrix mb = ma.times(mx).minusEquals(mr);
			mx = mat.times(ma).inverse().times(mat).times(mb);
			return mx;
		}
	}

	/*
	 * For troubleshooting client location. Check if all conditions for locating
	 * a client are met.
	 */
	public JSONObject validateClientLocation(HmDomain domain, Long clientId,
			boolean circle) throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (clientId == null) {
			return jsonObject;
		}
		/*
		 * Find active client
		 */
		// AhClientSession client = (AhClientSession) QueryUtil.findBoById(
		// AhClientSession.class, clientId);
		AhClientSession client = (AhClientSession) DBOperationUtil.findBoById(
				AhClientSession.class, clientId);
		if (client == null) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.location.clientNotFound"));
			return jsonObject;
		}

        // Check if is AP230/AP370/AP390, Temp for not supporting.
        List<Short> apModels = (List<Short>) QueryUtil.executeNativeQuery(
                "select hiveapmodel from hive_ap where macAddress='" + client.getApMac() + "'", 1);
        if (apModels.size() == 1
                && (apModels.get(0) == HiveAp.HIVEAP_MODEL_370 || apModels.get(0) == HiveAp.HIVEAP_MODEL_390 || apModels
                        .get(0) == HiveAp.HIVEAP_MODEL_230)) {
            jsonObject.put("msg", MgrUtil.getUserMessage("error.location.millaunotsupport"));
            return jsonObject;
        }

		/*
		 * Find all reporting APs with rssi < 0
		 */
		Collection<String> reporterMacs = (List<String>) QueryUtil
				.executeQuery("select distinct reporterMac from "
						+ LocationRssiReport.class.getSimpleName(), null,
						new FilterParams("clientMac = :s1 and rssi < 0",
								new Object[] { client.getClientMac() }),
						null != domain ? domain.getId() : null);
		log.info_ln("% # reporting APs: " + reporterMacs.size()
				+ " for client: " + client.getClientMac());
		MapSettings mapSettings = BeTopoModuleUtil.getMapGlobalSetting(domain);

		MapContainerNode mapContainerNode = validateTargetLocation(jsonObject,
				reporterMacs, mapSettings, "Client");
		if (mapContainerNode == null) {
			return validateClientRadius(jsonObject, client, circle);
		}

		Map<String, MapLeafNode> nodesMap = createNodesMap(mapContainerNode
				.getChildNodes());
		if (nodesMap.keySet().size() == 0) {
			return validateClientRadius(jsonObject, client, circle);
		}

		MrHeatMap heatMap_a = getHeatMap(mapContainerNode,
				mapContainerNode.getChildNodes(), 0, 0, true, 0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		MrHeatMap heatMap_g = getHeatMap(mapContainerNode,
				mapContainerNode.getChildNodes(), 0, 0, false, 0, 0,
				mapSettings.isCalibrateHeatmap() && mapSettings.isUseHeatmap(),
				mapSettings.getRssiFrom(), mapSettings.getRssiUntil());
		if (heatMap_a == null && heatMap_g == null) {
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.noRssiCali",
					new String[] { "" + mapSettings.getRssiUntil(),
							"" + mapSettings.getRssiFrom() }));
			return validateClientRadius(jsonObject, client, circle);
		}

		Collection<JSONObject> clientRssi = new Vector<JSONObject>();
		double[] x = new double[3];
		Search.ClientDetected[] clients = findClient(mapContainerNode,
				mapContainerNode.getChildNodes(), nodesMap, null, 1,
				client.getClientMac(), client.getClientHostname(), mapSettings,
				false, clientRssi, x, true, true);
		if (clients == null) {
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.insufficientRssi", new String[] { "client",
							"" + mapSettings.getClientRssiThreshold() }));
			return validateClientRadius(jsonObject, client, circle);
		}

		validateTargetBoundaries(jsonObject, reporterMacs, mapContainerNode,
				mapSettings.isPeriVal(), clients, x, "Client");
		try {
			jsonObject.get("msg");
		} catch (JSONException e) {
			return jsonObject;
		}
		return validateClientRadius(jsonObject, client, circle);
	}

	private JSONObject validateClientRadius(JSONObject jsonObject,
			AhClientSession client, boolean circle) throws Exception {
		if (!circle) {
			return jsonObject;
		}

		// bug fix for 20142, use rssi&snr value sent by HiveOS
		int rssi = client.getClientRssi();
		log.info_ln("% Client RSSI: " + client.getClientRssi());
		AhAssociation association = getClientAssociation(client);
		if (association != null) {
			log.info_ln("% Override client RSSI: " + client.getClientRssi()
					+ " with association: " + association.getClientRSSI());
			// bug fix for 20142, use rssi&snr value sent by HiveOS
			rssi = association.getClientRSSI();
		}
		log.info_ln("% associated AP is: " + client.getApMac() + ", name: "
				+ client.getApName() + ", rssi: " + rssi);

		Collection<Object[]> parentMap = (List<Object[]>) QueryUtil
				.executeQuery(
						"select parentMap.id, apId, parentMap.mapName from "
								+ MapNode.class.getSimpleName(), null,
						new FilterParams("apId", client.getApMac()));
		if (parentMap.size() == 0) {
			jsonObject.put("msg", "Associated AP is not on a map.");
			return jsonObject;
		} else {
			Long mapId = (Long) parentMap.iterator().next()[0];
			jsonObject.put("mapId", mapId);
			jsonObject.put("circle", true);
			jsonObject.remove("msg");
			return jsonObject;
		}
	}

	private AhAssociation getClientAssociation(AhClientSession client) {
		String where = "clientMac = :s1 AND time >= :s2";
		Object values[] = new Object[2];
		values[0] = client.getClientMac();
		values[1] = client.getStartTimeStamp();
		FilterParams f_params = new FilterParams(where, values);
		SortParams s_params = new SortParams("time", false);

		List<AhAssociation> associations = QueryUtil.executeQuery(
				AhAssociation.class, s_params, f_params, 1);
		if (associations.size() > 0) {
			return associations.get(0);
		} else {
			return null;
		}
	}

	public JSONObject validateRogueLocation(HmDomain domain, Long idpId)
			throws Exception {
		JSONObject jsonObject = new JSONObject();
		if (idpId == null) {
			return jsonObject;
		}
		/*
		 * Find idp
		 */
		Idp idp = (Idp) QueryUtil.findBoById(Idp.class, idpId);
		if (idp == null) {
			jsonObject.put("msg",
					MgrUtil.getUserMessage("error.location.rogueNotFound"));
			return jsonObject;
		}
		/*
		 * Find all reporting APs
		 */
		MapSettings mapSettings = BeTopoModuleUtil.getMapGlobalSetting(domain);
		Collection<String> reporterMacs = (List<String>) QueryUtil
				.executeQuery(
						"select distinct reportNodeId from "
								+ Idp.class.getSimpleName(),
						null,
						new FilterParams(
								"ifMacAddress = :s1 and rssi >= :s2 and rssi <= :s3",
								new Object[] {
										idp.getIfMacAddress(),
										(short) (mapSettings
												.getClientRssiThreshold() + noiseFloor),
										(short) (hmRssiUntil + noiseFloor) }),
						null != domain ? domain.getId() : null);
		log.info_ln("% # reporting APs: " + reporterMacs.size() + " for idp: "
				+ idp.getIfMacAddress());

		MapContainerNode mapContainerNode = validateTargetLocation(jsonObject,
				reporterMacs, mapSettings, "Rogue");
		if (mapContainerNode == null) {
			return jsonObject;
		}

		Map<String, MapLeafNode> nodesMap = createNodesMap(mapContainerNode
				.getChildNodes());
		if (nodesMap.keySet().size() == 0) {
			return jsonObject;
		}

		Collection<JSONObject> rogueRssi = new Vector<JSONObject>();

		double[] x = new double[3];
		Search.ClientDetected[] clients = findRogue(mapContainerNode,
				mapContainerNode.getChildNodes(), nodesMap, null, 1,
				idp.getIfMacAddress(), mapSettings, false, rogueRssi, x, true);
		if (clients == null) {
			return jsonObject;
		}

		validateTargetBoundaries(jsonObject, reporterMacs, mapContainerNode,
				mapSettings.isPeriVal(), clients, x, "Rogue");
		return jsonObject;
	}

	public MapContainerNode validateTargetLocation(JSONObject jsonObject,
			Collection<String> reporterMacs, MapSettings mapSettings,
			String target) throws Exception {
		if (reporterMacs.size() == 0) {
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.noRssi",
					new String[] { target.toLowerCase() }));
			return null;
		}
		/*
		 * Sufficient measurements ?
		 */
		if (reporterMacs.size() < mapSettings.getMinRssiCount()) {
			String rms = reporterMacs.toString();
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.fewRssi",
					new String[] { target, "" + reporterMacs.size(),
							rms.substring(1, rms.length() - 1),
							"" + mapSettings.getClientRssiThreshold(),
							"" + mapSettings.getMinRssiCount() }));
			return null;
		}
		/*
		 * Find the maps that the reporting APs are on, and a count for each
		 * map.
		 */
		List<Object[]> mapIds = (List<Object[]>) QueryUtil
				.executeQuery("select parentMap.id, count(*) from "
						+ MapNode.class.getSimpleName() + " bo", null,
						new FilterParams("apId in (:s1)",
								new Object[] { reporterMacs }),
						new GroupByParams(new String[] { "parentMap.id" }),
						null);
		log.info_ln("% # maps: " + mapIds.size());
		if (mapIds.size() == 0) {
			/*
			 * None of the reporting APs are on a map.
			 */
			String rms = reporterMacs.toString();
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.noMap",
					new String[] { target, "" + reporterMacs.size(),
							rms.substring(1, rms.length() - 1) }));
			return null;
		}
		long maxRssiCount = -1;
		Long maxMapId = null;
		for (Object[] mapIdCount : mapIds) {
			Long rssiCount = (Long) mapIdCount[1];
			if (rssiCount > maxRssiCount) {
				maxRssiCount = rssiCount;
				maxMapId = (Long) mapIdCount[0];
			}
		}
		if (maxRssiCount < mapSettings.getMinRssiCount()) {
			if (mapIds.size() > 1) {
				/*
				 * Reporting APs are on more than 1 map.
				 */
				List<String> mapNames = (List<String>) QueryUtil
						.executeQuery("select distinct parentMap.mapName from "
								+ MapNode.class.getSimpleName(),
								new SortParams("parentMap.mapName"),
								new FilterParams("apId in (:s1)",
										new Object[] { reporterMacs }));
				log.info_ln("% map names: " + mapNames);
				String rms = reporterMacs.toString();
				String mns = mapNames.toString();
				jsonObject.put("msg", MgrUtil.getUserMessage(
						"error.location.manyMaps",
						new String[] { target, "" + reporterMacs.size(),
								rms.substring(1, rms.length() - 1),
								"" + mapSettings.getClientRssiThreshold(),
								mns.substring(1, mns.length() - 1) }));
				return null;
			} else {
				/*
				 * Insufficient measurements on a single map.
				 */
				String rms = reporterMacs.toString();
				jsonObject.put("msg", MgrUtil.getUserMessage(
						"error.location.fewOnMap",
						new String[] { target, "" + reporterMacs.size(),
								rms.substring(1, rms.length() - 1) }));
				return null;
			}
		}

		MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
				.findBoById(MapNode.class, (Long) maxMapId,
						new QueryMapContainer());
		log.info_ln("% map: " + mapContainerNode.getMapName());
		/*
		 * Has this map been sized ?
		 */
		if (mapContainerNode.getActualWidth() <= 0) {
			String rms = reporterMacs.toString();
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.invalidMap",
					new String[] { target, "" + reporterMacs.size(),
							rms.substring(1, rms.length() - 1),
							mapContainerNode.getMapName() }));
			return null;
		}

		return mapContainerNode;
	}

	public boolean validateTargetBoundaries(JSONObject jsonObject,
			Collection<String> reporterMacs, MapContainerNode mapContainerNode,
			boolean perival, Search.ClientDetected[] clients, double[] x,
			String target) throws Exception {
		double x1 = x[0];
		double x2 = x[1];
		x = boundaryCondition(mapContainerNode, x);
		if (x == null) {
			/*
			 * Estimate is outside map boundaries
			 */
			String rms = reporterMacs.toString();
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.outsideMap",
					new String[] { target, "" + reporterMacs.size(),
							rms.substring(1, rms.length() - 1),
							mapContainerNode.getMapName(),
							String.format("%.2f", x1),
							String.format("%.2f", x2) }));
			return false;
		}
		if (perival && !convexCondition(clients, x)) {
			/*
			 * Estimate is outside reporting AP boundaries
			 */
			String rms = reporterMacs.toString();
			jsonObject.put("msg", MgrUtil.getUserMessage(
					"error.location.outsidePerimeter",
					new String[] { target, "" + reporterMacs.size(),
							rms.substring(1, rms.length() - 1),
							mapContainerNode.getMapName(),
							String.format("%.2f", x1),
							String.format("%.2f", x2) }));
			return false;
		}
		jsonObject.put("mapId", mapContainerNode.getId());
		return true;
	}

	@SuppressWarnings("unchecked")
	public void findClientRssi(HmDomain domain, List<AhClientSession> clients)
			throws Exception {
		if (clients.size() == 0) {
			return;
		}
		Map<String, AhClientSession> clientMacs = new HashMap<String, AhClientSession>();
		Collection<String> apIds = new Vector<String>();
		for (AhClientSession client : clients) {
			clientMacs.put(client.getClientMac(), client);
			apIds.add(client.getApMac());
		}

		/*
		 * Find the map names that the associated APs are on.
		 */
		Collection<Object[]> apMapNames = (List<Object[]>) QueryUtil
				.executeQuery(
						"select apId, " + getMapNameField()
								+ ", parentMap.actualWidth from "
								+ MapNode.class.getSimpleName(),
						new SortParams("parentMap.mapName"), new FilterParams(
								"apId in (:s1)", new Object[] { apIds }));
		Map<String, Object[]> apMaps = new HashMap<String, Object[]>();
		for (Object[] apMapName : apMapNames) {
			apMaps.put((String) apMapName[0], apMapName);
		}

		for (AhClientSession client : clients) {
			Object[] mapName = apMaps.get(client.getApMac());
			if (mapName != null) {
				client.setMapName((String) mapName[1]);
				if ((Double) mapName[2] > 0) {
					client.setRssiCount(1);
				}
			}
		}

		if (true) {
			return;
		}

		/*
		 * Find the number of rssi measurements < 0 for each client mac
		 */
		MapSettings mapSettings = BeTopoModuleUtil.getMapGlobalSetting(domain);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		if (mapSettings.isRealTime()) {
			calendar.add(Calendar.MINUTE, -lastRssiMaxAge);
		} else {
			calendar.add(Calendar.MINUTE, -mapSettings.getLocationWindow());
		}
		Collection<Object[]> rssiCounts = (List<Object[]>) QueryUtil
				.executeQuery(
						"select clientMac, count(*) from "
								+ LocationRssiReport.class.getSimpleName()
								+ " bo",
						null,
						new FilterParams(
								"clientMac in (:s1) and rssi < 0 and reportTime >= :s2",
								new Object[] { clientMacs.keySet(),
										calendar.getTime() }),
						new GroupByParams(new String[] { "clientMac" }), null);
		for (Object[] rssiCount : rssiCounts) {
			String clientMac = (String) rssiCount[0];
			AhClientSession client = clientMacs.get(clientMac);
			client.setRssiCount((int) (long) (Long) rssiCount[1]);
		}

		for (AhClientSession client : clients) {
			Object[] apMap = apMaps.get(client.getApMac());
			String mapName = apMap == null ? null : (String) apMap[1];
			if (client.getRssiCount() > 0 && mapName == null) {
				/*
				 * Rare case. The associated AP for this client is not on a map,
				 * but rssi measurements from other APs for this client exist.
				 * Find a map name that at least 1 of the reporting APs is on.
				 */
				Collection<String> reporterMacs = (List<String>) QueryUtil
						.executeQuery("select distinct reporterMac from "
								+ LocationRssiReport.class.getSimpleName(),
								null, new FilterParams(
										"clientMac = :s1 and rssi < 0",
										new Object[] { client.getClientMac() }));
				if (reporterMacs.size() > 0) {
					List<String> mapNames = (List<String>) QueryUtil
							.executeQuery(
									"select distinct parentMap.mapName from "
											+ MapNode.class.getSimpleName(),
									null, new FilterParams("apId in (:s1)",
											new Object[] { reporterMacs }));
					if (mapNames.size() > 0) {
						mapName = mapNames.get(0);
					}
				}
			}
			client.setMapName(mapName);
			if (mapName == null
					|| client.getRssiCount() < mapSettings.getMinRssiCount()) {
				/*
				 * No drill down link
				 */
				client.setRssiCount(-1);
			}
		}
	}

	private String getMapNameField() {
		return "case when parentMap.mapType=3 then (parentMap.parentMap.mapName||'_'||parentMap.mapName) "
				+ "else parentMap.mapName end as mapName";
	}

	/*
	 * Determines for each idp which map it is on, and the number of
	 * measurements. If rssiCount is set to -1 it means there are not enough
	 * measurements to estimate a location.
	 */
	@SuppressWarnings("unchecked")
	public void findRogueRssi(HmDomain domain, List<Idp> rogues)
			throws Exception {
		if (rogues.isEmpty()) {
			return;
		}
		Map<String, List<Idp>> idpMacs = new HashMap<String, List<Idp>>();
		Collection<String> apIds = new Vector<String>();
		for (Idp idp : rogues) {
			if (null == idpMacs.get(idp.getIfMacAddress())) {
				List<Idp> list = new ArrayList<Idp>();
				idpMacs.put(idp.getIfMacAddress(), list);
			}
			idpMacs.get(idp.getIfMacAddress()).add(idp);
			apIds.add(idp.getReportNodeId());
		}
		/*
		 * Find the number of rssi measurements for each idp mac
		 */
		MapSettings mapSettings = BeTopoModuleUtil.getMapGlobalSetting(domain);
		Collection<Object[]> rssiCounts = (List<Object[]>) QueryUtil
				.executeQuery(
						"select ifMacAddress, count(*) from "
								+ Idp.class.getSimpleName() + " bo",
						null,
						new FilterParams(
								"ifMacAddress in (:s1) and rssi >= :s2 and rssi <= :s3",
								new Object[] {
										idpMacs.keySet(),
										(short) (mapSettings
												.getClientRssiThreshold() + noiseFloor),
										(short) (hmRssiUntil + noiseFloor) }),
						new GroupByParams(new String[] { "ifMacAddress" }),
						domain.getId());
		for (Object[] rssiCount : rssiCounts) {
			String idpMac = (String) rssiCount[0];
			List<Idp> list = idpMacs.get(idpMac);
			if (null != list) {
				for (Idp idp : list) {
					idp.setRssiCount((int) (long) (Long) rssiCount[1]);
				}
			}
		}

		/*
		 * Find the map names that the reporting APs are on.
		 */
		Collection<Object[]> apMaps = (List<Object[]>) QueryUtil.executeQuery(
				"select apId, " + getMapNameField() + ", parentMap.id from "
						+ MapNode.class.getSimpleName(), new SortParams(
						"parentMap.mapName"), new FilterParams("apId in (:s1)",
						new Object[] { apIds }));
		Map<String, String> apMapNames = new HashMap<String, String>();
		Map<String, Long> apMapIds = new HashMap<String, Long>();
		for (Object[] apMap : apMaps) {
			apMapNames.put((String) apMap[0], (String) apMap[1]);
			apMapIds.put((String) apMap[0], (Long) apMap[2]);
		}

		for (Idp idp : rogues) {
			String mapName = apMapNames.get(idp.getReportNodeId());
			Long mapId = apMapIds.get(idp.getReportNodeId());
			if (mapName == null
					|| idp.getRssiCount() < mapSettings.getMinRssiCount()) {
				/*
				 * No drill down link
				 */
				idp.setRssiCount(-1);
			}
			idp.setMapName(mapName);
			idp.setMapId(mapId);
		}
	}

	public double lsle(MapContainerNode mapContainerNode,
			Collection<MapNode> nodes, int rssiFrom, int rssiUntil) {
		double mapToMetric = mapContainerNode.getMapToMetric();
		int apCount = setMetricNodePositions(nodes, mapToMetric);
		fetchRadioAttributes(nodes);
		double[] apX = new double[apCount];
		double[] apY = new double[apCount];
		double[] apBackhaulPower = new double[apCount];
		short[] apBackhaulChannel = new short[apCount];
		int[][] neighborRSSI = new int[apCount][apCount];
		if (!fetchNeighborRssi(new HashMap(), apX, apY, null, null,
				apBackhaulPower, apBackhaulChannel, neighborRSSI, nodes, true,
				rssiFrom, rssiUntil, true)) {
			return lsle(0);
		}
		double le = Search.lsle(apX, apY, apBackhaulPower, apBackhaulChannel,
				neighborRSSI);
		log.info_ln("le = " + le + ";");
		log.info_ln("le = lsle(apX, apY, neighborPower, neighborChannel, neighborRSSI)");
		return le;
	}

	public static double lsle(double lsle) {
		if (lsle < mle) {
			lsle += 0.5;
		}
		if (lsle < mle) {
			lsle = mle;
		}
		return lsle;
	}

	private static class MrHeatMap {
		public MrHeatMap(double[] apX, double[] apY, Map apIndexes,
				double[] apPower, short[] apChannel, double apElevation,
				double[] apBackhaulPower, short[] apBackhaulChannel,
				int[][] neighborRSSI, double[] gridX, double[] gridY,
				double[][] B, double lsle, double dle, boolean useA,
				double mapWidthMetric, double mapHeightMetric, int canvasWidth,
				int canvasHeight, int heatmapResolution, double xray,
				long latchId) {
			this.latchId = latchId;
			this.apX = apX;
			this.apY = apY;
			this.apIndexes = apIndexes;
			this.apPower = apPower;
			this.apChannel = apChannel;
			this.apElevation = apElevation;
			this.apBackhaulPower = apBackhaulPower;
			this.apBackhaulChannel = apBackhaulChannel;
			this.neighborRSSI = neighborRSSI;

			this.gridX = gridX;
			this.gridY = gridY;
			this.B = B;
			this.lsle = lsle;
			this.dle = dle;
			this.useA = useA;
			this.mapWidthMetric = mapWidthMetric;
			this.mapHeightMetric = mapHeightMetric;
			this.canvasWidth = canvasWidth;
			this.canvasHeight = canvasHeight;
			this.heatmapResolution = heatmapResolution;
			this.xray = xray;
		}

		private long latchId;

		private boolean useA, overlayBusy, overlayFinished;

		private int canvasWidth, canvasHeight, heatmapResolution;

		private double lsle, dle, apElevation, mapWidthMetric, mapHeightMetric,
				gridXsize, gridYsize, xray;

		private short[] apChannel, apBackhaulChannel;

		private double[] apX, apY, apPower, apBackhaulPower, gridX, gridY;

		private double[][] B;

		private short[][] mapColors, mapChannels;

		private double mapRssi[][];

		private int[][] neighborRSSI;

		private Map<String, Integer> apIndexes;

		private double[][][] hmv;

		private Set<String> remainingIds;
	}

	final class QueryMapContainer implements QueryBo {
		public Collection<HmBo> load(HmBo bo) {
			if (bo instanceof MapContainerNode) {
				MapContainerNode mapContainerNode = (MapContainerNode) bo;
				// Just to trigger load from database
				for (Vertex vertex : mapContainerNode.getPerimeter())
					;
				for (Wall wall : mapContainerNode.getWalls())
					;
				for (MapNode mapNode : mapContainerNode.getChildNodes()) {
					if (mapNode.isLeafNode()) {
						HiveAp hiveAp = ((MapLeafNode) mapNode).getHiveAp();
						if (hiveAp != null) {
							// Trigger load from DB
							hiveAp.getHiveApModel(); // we need this to
							// calculate ERP
						}
					}
				}
			}
			return null;
		}
	}
}
