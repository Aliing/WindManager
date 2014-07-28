package com.ah.ui.actions.monitor;

//import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Wall;
import com.ah.bo.performance.AhLatestInterferenceStats;
import com.ah.bo.performance.AhLatestXif;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.tools.PlanToolAction;
import com.ah.util.CountryCode;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import java.awt.Graphics2D;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;

/*
 * @author Chris Scheers
 */

public class DrawHeatmapAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			DrawHeatmapAction.class.getSimpleName());

	protected Collection<JSONObject> getPredictedLap(
			MapContainerNode mapContainerNode, boolean useA,
			PlannedAP plannedAP, short chi, short channelWidth, int fadeMargin,
			boolean predict) throws Exception {
		/*
		 * Use either canvas width or height as square image size.
		 */
		double canvasSize_d = canvasWidth;
		double canvasSizeMetric = mapContainerNode.getActualWidthMetric();
		double pixelSizeMetric = canvasSizeMetric / canvasSize_d;
		if (mapContainerNode.getActualHeightMetric() > canvasSizeMetric) {
			/*
			 * Canvas is taller than it is wide.
			 */
			canvasSize_d = canvasHeight;
			canvasSizeMetric = mapContainerNode.getActualHeightMetric();
		}
		int adjustedRssiThreshold = -rssiThreshold;
		if (!useA) {
			channelWidth = RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20;
		}
		if ((layers & 8) > 0) {
			adjustedRssiThreshold = BoMgmt.getLocationTracking()
					.getRateRssiThreshold(rateThreshold, useA, channelWidth,
							fadeMargin);
			log.info("Data rates map, adjust threshold from: -" + rssiThreshold
					+ " to: " + adjustedRssiThreshold);
		} else if ((layers & 4) > 0) {
			adjustedRssiThreshold = -95 + fadeMargin + snrThreshold;
			log.info("puzzlePiece", "This must be a SNR map, fade margin: "
					+ fadeMargin + ", snrThreshold: " + snrThreshold
					+ ", adjust threshold from: -" + rssiThreshold + " to: "
					+ adjustedRssiThreshold);
			rssiThreshold = -adjustedRssiThreshold;
			layers = 16;
		}

		// Try to reuse cache
		FloorCache cache = getFloorCache(mapContainerNode.getId());
		if (layers >= 4) {
			cache = null; // Don't use cache
		}
		double imageSize_d = canvasSize_d;
		int imgScale = 1;
		// image scale fix to 1 for the PDF report. JunYu fix on 22 May, 2011
		if (pdfReport == null) {
			if (cache == null || cache.imgScale < 0) {
				double complexity = puzzleComplexity(mapContainerNode,
						adjustedRssiThreshold, useA, pixelSizeMetric
								* pixelSizeMetric, predict);
				double actualComplexity = complexity;
				int complexityLimit = Integer.parseInt(System
						.getProperty("planLap.complexity"));
				while (complexity > complexityLimit || imgScale < 4) {
					if (imageSize_d / 2 < 80) {
						if (!predict) {
							log.info("getPredictedLap",
									"further adjusting actual imageSize: "
											+ imageSize_d
											+ " would make it dip below 80.");
						}
						break;
					}
					imageSize_d /= 2;
					complexity /= 4;
					imgScale *= 2;
					if (!predict) {
						log.info("getPredictedLap", "adjusting complexity to: "
								+ complexity + ", adjusted pixel size: "
								+ imgScale + ", actual: " + pixelSizeMetric
								* imgScale + "m");
					}
				}
				if (!predict) {
					log.info("getPredictedLap", "complexity: "
							+ actualComplexity + ", adjusted: " + complexity
							+ ", adjusted pixel size: " + imgScale
							+ ", actual: " + pixelSizeMetric * imgScale + "m");
				}
			} else {
				imgScale = cache.imgScale;
				while (imgScale > 1) {
					imageSize_d /= 2;
					imgScale /= 2;
				}
				imgScale = cache.imgScale;
			}
		}
		int imageSize = (int) imageSize_d;
		if (imageSize % 2 > 0) {
			imageSize++;
		} else {
			imageSize += 2;
		}
		int tileSize = imageSize * imgScale;
		if (tileSize < canvasSize_d) {
			log.error("getPredictedLap", "Tile size " + tileSize
					+ " is smaller than canvasSize " + canvasSize_d + " !!!");
		}
		double squareSizeMetric = pixelSizeMetric * imgScale;

		canvasSize_d = tileSize;
		canvasSizeMetric = canvasSize_d * pixelSizeMetric;
		if (!predict) {
			log.info("getPredictedLap", "actual image size (" + imageSize_d
					+ ", " + imageSize + " x " + imageSize + "), canvas ("
					+ canvasWidth + ", " + canvasHeight
					+ "), scaled square image size (" + tileSize + " or "
					+ canvasSizeMetric + "m)");
		}
		int imageWidthUsed = canvasWidth / imgScale;
		if (canvasWidth % imgScale > 0) {
			imageWidthUsed++;
		}
		int imageHeightUsed = canvasHeight / imgScale;
		if (canvasHeight % imgScale > 0) {
			imageHeightUsed++;
		}
		if (!predict) {
			log.info("getPredictedLap", "actual imageSizeUsed ("
					+ imageWidthUsed + ", " + imageHeightUsed
					+ "), scales to (" + imageWidthUsed * imgScale + ", "
					+ imageHeightUsed * imgScale + ")");
		}

		if (predict) {
			puzzlePiece(mapContainerNode, useA, plannedAP,
					adjustedRssiThreshold, chi, channelWidth, fadeMargin,
					imageWidthUsed, imageHeightUsed, squareSizeMetric,
					imgScale, true);
			return null;
		} else {
			/*
			 * Only calculate position and width/height of heat map image.
			 */
			if (plannedAP == null) {
				if (cache != null) {
					log.info("Initializing floor cache.");
					cache.useA = useA;
					cache.channelWidth = channelWidth;
					cache.layers = layers;
					cache.shadesPerColor = getShadesPerColor();
					cache.pixelSizeMetric = pixelSizeMetric;
					cache.imgScale = imgScale;
					cache.spillRssi = new float[imageWidthUsed][imageHeightUsed];
					cache.spillChannels = new short[imageWidthUsed][imageHeightUsed];
					cache.mapRssi = new float[imageWidthUsed][imageHeightUsed];
					cache.mapChannels = new short[imageWidthUsed][imageHeightUsed];
				}

				Collection<JSONObject> jsonNodes = puzzlePieces(
						mapContainerNode, adjustedRssiThreshold, channelWidth,
						fadeMargin, useA, imageWidthUsed, imageHeightUsed,
						squareSizeMetric, imgScale);
				return jsonNodes;
			} else {
				Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
				jsonNodes.add(puzzlePiece(mapContainerNode, useA, plannedAP,
						adjustedRssiThreshold, (short) 0, channelWidth,
						fadeMargin, imageWidthUsed, imageHeightUsed,
						squareSizeMetric, imgScale, false));
				return jsonNodes;
			}
		}
	}

	private double puzzleComplexity(MapContainerNode mapContainerNode,
			int adjustedRssiThreshold, boolean useA,
			double pixelSizeMetricSquared, boolean predict) throws Exception {
		double complexity = 0, interference = 0;
		double actualWidth = mapContainerNode.getActualWidthMetric();
		double actualHeight = mapContainerNode.getActualHeightMetric();
		double mapToMetric = mapContainerNode.getMapToMetric();
		for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
			short power = useA ? plannedAP.wifi1Power : plannedAP.wifi0Power;
			double distance = BoMgmt.getLocationTracking().predictedMapSize(
					mapContainerNode, power, plannedAP.apModel, useA,
					adjustedRssiThreshold);
			double apx_m = plannedAP.x * mapToMetric;
			double apy_m = plannedAP.y * mapToMetric;
			log.debug("puzzleComplexity", "Planned AP (" + apx_m + ", " + apy_m
					+ "), distance: " + distance);
			double ax1 = apx_m - distance;
			double ay1 = apy_m - distance;
			double ax2 = apx_m + distance;
			double ay2 = apy_m + distance;
			log.debug("puzzleComplexity", "affected area X (" + ax1 + ", "
					+ ax2 + "), Y (" + ay1 + ", " + ay2 + ")");
			if (ax1 < 0) {
				ax1 = 0;
			}
			if (ay1 < 0) {
				ay1 = 0;
			}
			if (ax2 > actualWidth) {
				ax2 = actualWidth;
			}
			if (ay2 > actualHeight) {
				ay2 = actualHeight;
			}
			log.debug("puzzleComplexity", "adjusted area X (" + ax1 + ", "
					+ ax2 + "), Y (" + ay1 + ", " + ay2 + ")");
			double pixelCount = (ax2 - ax1) * (ay2 - ay1)
					/ pixelSizeMetricSquared;
			log.debug("puzzleComplexity",
					"# pixels for: " + plannedAP.getLabel() + ": " + pixelCount);
			complexity += pixelCount;
			for (PlannedAP nbrAP : mapContainerNode.getPlannedAPs()) {
				if (nbrAP.getId().equals(plannedAP.getId())) {
					continue;
				}
				log.debug("puzzleComplexity", "Verify interference from: "
						+ plannedAP.getId() + ", with: " + nbrAP.getId());
				power = useA ? nbrAP.wifi1Power : nbrAP.wifi0Power;
				distance = BoMgmt.getLocationTracking().predictedMapSize(
						mapContainerNode, power, nbrAP.apModel, useA,
						adjustedRssiThreshold);

				apx_m = nbrAP.x * mapToMetric;
				apy_m = nbrAP.y * mapToMetric;
				log.debug("puzzleComplexity", "nbr AP (" + apx_m + ", " + apy_m
						+ "), distance: " + distance);
				double nax1 = apx_m - distance;
				double nay1 = apy_m - distance;
				double nax2 = apx_m + distance;
				double nay2 = apy_m + distance;
				log.debug("puzzleComplexity", "nbr area X (" + nax1 + ", "
						+ nax2 + "), Y (" + nay1 + ", " + nay2 + ")");

				if (nax2 < ax1) {
					continue;
				}
				if (nax1 > ax2) {
					continue;
				}
				if (nay2 < ay1) {
					continue;
				}
				if (nay1 > ay2) {
					continue;
				}

				double xo1 = Math.max(nax1, ax1);
				double xo2 = Math.min(nax2, ax2);
				double yo1 = Math.max(nay1, ay1);
				double yo2 = Math.min(nay2, ay2);
				log.debug("puzzleComplexity", "Overlap area X (" + xo1 + ", "
						+ xo2 + "), Y (" + yo1 + ", " + yo2 + ")");
				pixelCount = (xo2 - xo1) * (yo2 - yo1) / pixelSizeMetricSquared;
				log.debug("puzzleComplexity",
						"# nbr pixels for: " + nbrAP.getLabel() + ": "
								+ pixelCount);
				log.debug("puzzleComplexity", "# interference pixels: "
						+ pixelCount);
				interference += pixelCount;
			}
		}
		complexity += interference;
		if (!predict) {
			log.debug("puzzleComplexity", "Complexity: " + complexity
					+ ", from interference: " + interference + " ("
					+ interference / complexity * 100 + "%)");
		}
		return complexity;
	}

	private Collection<JSONObject> puzzlePieces(
			MapContainerNode mapContainerNode, int adjustedRssiThreshold,
			short channelWidth, int fadeMargin, boolean useA,
			int imageWidthUsed, int imageHeightUsed, double squareSizeMetric,
			int imgScale) throws Exception {
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
			jsonNodes.add(puzzlePiece(mapContainerNode, useA, plannedAP,
					adjustedRssiThreshold, (short) 0, channelWidth, fadeMargin,
					imageWidthUsed, imageHeightUsed, squareSizeMetric,
					imgScale, false));
		}
		return jsonNodes;
	}

	private JSONObject puzzlePiece(MapContainerNode mapContainerNode,
			boolean useA, PlannedAP plannedAP, int adjustedRssiThreshold,
			short chi, short channelWidth, int fadeMargin, int imageWidthUsed,
			int imageHeightUsed, double squareSizeMetric, int imgScale,
			boolean predict) throws Exception {
		BoMgmt.getLocationTracking().lapBoundaries(mapContainerNode, useA,
				plannedAP, squareSizeMetric, imgScale, adjustedRssiThreshold);
		if (mapContainerNode.x1 < -imageWidthUsed) {
			mapContainerNode.x1 = -imageWidthUsed;
		}
		if (mapContainerNode.y1 < -imageHeightUsed) {
			mapContainerNode.y1 = -imageHeightUsed;
		}
		if (mapContainerNode.x2 >= imageWidthUsed * 2) {
			mapContainerNode.x2 = imageWidthUsed * 2 - 1;
		}
		if (mapContainerNode.y2 >= imageHeightUsed * 2) {
			mapContainerNode.y2 = imageHeightUsed * 2 - 1;
		}

		if (predict) {
			log.debug(
					"puzzlePiece",
					"adjusted tile X ("
							+ mapContainerNode.x1
							+ ", "
							+ mapContainerNode.x2
							+ "), Y ("
							+ mapContainerNode.y1
							+ ", "
							+ mapContainerNode.y2
							+ "), "
							+ plannedAP.getLabel()
							+ ", or ("
							+ String.format("%.2f, %.2f), (%.2f, %.2f)",
									mapContainerNode.x1 * squareSizeMetric,
									mapContainerNode.x2 * squareSizeMetric,
									mapContainerNode.y1 * squareSizeMetric,
									mapContainerNode.y2 * squareSizeMetric));
		}
		if (predict) {
			if (pdfReport != null) {
				m_x = mapContainerNode.x1 * imgScale;
				m_y = mapContainerNode.y1 * imgScale;
			}
			short shades = getShadesPerColor();
			if ((layers & 8) > 0 || (layers & 16) > 0) {
				// data rates or SNR map, use adjusted threshold
				shades = (short) (-adjustedRssiThreshold - 35 + 1);
			}
			BoMgmt.getLocationTracking().predictedLap(mapContainerNode,
					imageWidthUsed, imageHeightUsed, useA, shades, plannedAP,
					channelWidth, squareSizeMetric, imgScale,
					adjustedRssiThreshold);
			streamMapImage(mapContainerNode,
					MapSettings.HEATMAP_RESOLUTION_LOW, frequency, layers, chi,
					imgScale, plannedAP, channelWidth, fadeMargin,
					adjustedRssiThreshold);
			return null;
		} else {
			JSONObject jo = new JSONObject();
			jo.put("nodeId", "s" + plannedAP.getId());
			JSONObject to = new JSONObject();
			to.put("x", mapContainerNode.x1 * imgScale);
			to.put("y", mapContainerNode.y1 * imgScale);
			to.put("w", (mapContainerNode.x2 - mapContainerNode.x1 + 1)
					* imgScale);
			to.put("h", (mapContainerNode.y2 - mapContainerNode.y1 + 1)
					* imgScale);
			jo.put("tile", to);
			return jo;
		}
	}

	protected void streamMapImage(MapContainerNode mapContainerNode,
			int heatmapResolution, int frequency, int layers,
			short channelColor, int imgScale, PlannedAP plannedAP,
			short channelWidth, int fadeMargin, int adjustedRssiThreshold)
			throws Exception {
		if (layers < 1 || layers > 16) {
			return;
		}
		int auto = 1;
		if (pdfReport == null) {
			String userAgent = request.getHeader("user-agent");
			if (userAgent != null && userAgent.contains("MSIE")) {
				auto = imgScale;
			}
		} else {
			auto = imgScale;
		}
		BufferedImage image;
		if ((layers & 2) > 0) {
			image = BoMgmt.getLocationTracking().drawChannelImage(
					mapContainerNode.getMapColors(),
					mapContainerNode.getMapChannels(),
					mapContainerNode.getApChannel(), channelColor,
					getShadesPerColor(), auto);
		} else {
			short[][] mapColors = mapContainerNode.getMapColors();
			if (heatmapResolution == MapSettings.HEATMAP_RESOLUTION_MEDIUM
					|| heatmapResolution == MapSettings.HEATMAP_RESOLUTION_HIGH) {
				mapColors = BoMgmt.getLocationTracking().addChannelBoundaries(
						mapColors, mapContainerNode.getMapChannels());
			}
			if ((layers & 16) > 0) {
				image = BoMgmt.getLocationTracking().drawSnrImage(mapColors,
						getShadesPerColor(), fadeMargin, auto);
			} else if ((layers & 1) > 0) {
				image = BoMgmt.getLocationTracking().drawRssiImage(mapColors,
						getShadesPerColor(), auto);
			} else if ((layers & 8) > 0) {
				boolean useA = (frequency & 1) > 0;
				image = BoMgmt.getLocationTracking().drawRatesImage(mapColors,
						rateThreshold, plannedAP.apModel, fadeMargin,
						channelWidth,
						(short) (-adjustedRssiThreshold - 35 + 1), useA,
						imgScale);
			} else {
				final class QueryMapContainer implements QueryBo {
					public Collection<HmBo> load(HmBo bo) {
						if (bo instanceof MapContainerNode) {
							for (MapNode mapNode : ((MapContainerNode) bo)
									.getChildNodes()) {
								if (mapNode.isLeafNode()) {
									HiveAp hiveAp = ((MapLeafNode) mapNode)
											.getHiveAp();
									if (hiveAp != null) {
										// Trigger load from DB
										hiveAp.getHiveApModel();
									}
								}
							}
						}
						return null;
					}
				}
				MapContainerNode nodesContainer = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, mapContainerNode.getId(),
								new QueryMapContainer());
				short[] apSeverity = new short[mapContainerNode.getApChannel().length];
				fetchApSeverity(nodesContainer.getChildNodes(),
						mapContainerNode.getApIndexes(), apSeverity,
						(frequency & 1) > 0);
				image = BoMgmt.getLocationTracking().drawInterferenceImage(
						mapColors, mapContainerNode.getMapChannels(),
						apSeverity, getShadesPerColor());
			}
		}
		streamImage(image);
	}

	private void fetchApSeverity(Set<MapNode> nodes,
			Map<String, Integer> apIndexes, short[] apSeverity, boolean useA) {
		Map<String, MapLeafNode> leafNodes = new HashMap<String, MapLeafNode>(
				nodes.size());
		for (MapNode mapNode : nodes) {
			if (mapNode.isLeafNode()) {
				MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
				if (mapLeafNode.getHiveAp() != null) { // for 'M' nodes
					leafNodes.put(mapLeafNode.getApId(), mapLeafNode);
				}
			}
		}
		if (!leafNodes.isEmpty()) {
			List<AhLatestXif> radioList = QueryUtil.executeQuery(
					AhLatestXif.class, null, new FilterParams("apMac",
							leafNodes.keySet()));
			List<AhLatestInterferenceStats> interferenceList = QueryUtil
					.executeQuery(AhLatestInterferenceStats.class, null,
							new FilterParams("apMac", leafNodes.keySet()));
			Map<String, String> indexNameMapping = new HashMap<String, String>(
					radioList.size());
			for (AhLatestXif xif : radioList) {
				String mac = xif.getApMac();
				indexNameMapping.put(mac + xif.getIfIndex(), xif.getIfName());
			}
			for (AhLatestInterferenceStats interference : interferenceList) {
				String mac = interference.getApMac();
				String wifiName = indexNameMapping.get(mac
						+ interference.getIfIndex());
				log.debug("fetchApSeverity",
						"ifIndex: " + interference.getIfIndex()
								+ ", severity: " + interference.getSeverity()
								+ ", ifName: " + wifiName);
				if ("wifi0".equalsIgnoreCase(wifiName)) {
					// b/g
					leafNodes.get(mac).setRadioInterferenceBG(
							(short) interference.getSeverity());
				} else if ("wifi1".equalsIgnoreCase(wifiName)) {
					// a
					leafNodes.get(mac).setRadioInterferenceA(
							(short) interference.getSeverity());
				}
			}
			for (String apMac : leafNodes.keySet()) {
				Integer apIndex = apIndexes.get(apMac);
				MapLeafNode leafNode = leafNodes.get(apMac);
				if (apIndex != null) {
					if (leafNode.getHiveAp().is11nHiveAP()) {
						apSeverity[apIndex] = useA ? leafNode
								.getRadioInterferenceA() : leafNode
								.getRadioInterferenceBG();
					} else {
						apSeverity[apIndex] = 0;
					}
				}
			}
		}
	}

	/*
	 * Only create 1 map alarms cache per session.
	 */
	protected MapAlarmsCache<?> getMapAlarmsCache() throws Exception {
		MapAlarmsCache<?> mapAlarmsCache = (MapAlarmsCache<?>) MgrUtil
				.getSessionAttribute(SessionKeys.MAP_ALARMS_CACHE);
		if (mapAlarmsCache == null) {
			mapAlarmsCache = new MapAlarmsCache(userContext);
			MgrUtil.setSessionAttribute(SessionKeys.MAP_ALARMS_CACHE,
					mapAlarmsCache);
		}
		return mapAlarmsCache;
	}

	public static void setSelectedMapId(Long selectedMapId) {
		MgrUtil.setSessionAttribute(SessionKeys.SELECTED_MAP_ID, selectedMapId);
	}

	public static Long getSelectedMapId() {
		Long selectedMapId = -1L;
		Object obj = MgrUtil.getSessionAttribute(SessionKeys.SELECTED_MAP_ID);
		if (null != obj && obj instanceof Long) {
			selectedMapId = (Long) obj;
		}
		return selectedMapId;
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

	public short adjustChannelWidth(short channelWidth, short apModel) {
		if (channelWidth == RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80 && !HiveAp.is11acHiveAP(apModel)) {
			// override, the non 11ac AP does not support 80 MHz channel width
			return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A;
		}
		return channelWidth;
	}

	public int[] getChannelNaType(short apModel) {
		PlanToolConfig planToolConfig = PlanToolAction
				.getPlanToolConfig(domainId);
		int countryCode = planToolConfig.getCountryCode();
		apModel = apModel < 0 ? planToolConfig.getDefaultApType() : apModel;
		short channelWidth = adjustChannelWidth(
				planToolConfig.getChannelWidth(), apModel);
		int[] channelList = CountryCode.getChannelList_5GHz(countryCode,
				channelWidth, false, false, apModel,
				apModel == HiveAp.HIVEAP_MODEL_170);
		if (channelList.length == 1 && apModel == HiveAp.HIVEAP_MODEL_170) {
			log.info_ln("Try with dfs enabled.");
			channelList = CountryCode.getChannelList_5GHz(
					planToolConfig.getCountryCode(), channelWidth, true, false,
					apModel, true);
		}
		return channelList;
	}

	public EnumItem[] getEnumChannelNaType() {
		return MgrUtil.enumItems("enum.interface.channel.",
				getChannelNaType((short) -1), 0);
	}

	public EnumItem[] getEnumChannelNgType() {
		PlanToolConfig planToolConfig = PlanToolAction
				.getPlanToolConfig(domainId);
		int countryCode = planToolConfig.getCountryCode();
		// FIXME current only use width20 for 2.4GHz for planner
		int[] channelList = CountryCode.getChannelList_2_4GHz(countryCode,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		return MgrUtil.enumItems("enum.interface.channel.", channelList, 0);
	}

	protected void streamImage(BufferedImage image) throws Exception {
		if (image == null) {
			return;
		}
		if (pdfReport == null) {
			try {
				response.setContentType("image/png");
				OutputStream os = response.getOutputStream();
				ImageIO.write(image, "png", os);
				os.close();
			} catch (IOException e) {
				log.info("streamImage",
						"Abort this response, other request is in progress.");
			}
			return;
		}
		// PDF report
		log.debug("image=w:" + image.getWidth() + ",h:" + image.getHeight()
				+ ",t:" + image.getType());

		PdfContentByte cb = pdfReport.writer.getDirectContent();
		PdfGState gs1 = new PdfGState();
		gs1.setFillOpacity(0.8f);
		cb.setGState(gs1);

		// Graphics2D g2 =
		// cb.createGraphics(pdfReport.page.getWidth(),pdfReport.page.getHeight());
//		PdfGraphics2D g2 = new PdfGraphics2D(cb, pdfReport.page.getWidth(),
//				pdfReport.page.getHeight(), true);
		Graphics2D g2 =
				 cb.createGraphicsShapes(pdfReport.page.getWidth(),pdfReport.page.getHeight());

		int x1 = (int) (m_x + pdfReport.document.left());
		int y1 = (int) (m_y + pdfReport.mapStartY);
		g2.drawImage(image, null, x1, y1);

		// fix bug 14338 at 2011/07/05
		m_x = 0;
		m_y = 0;
		// fix end

		g2.dispose();
	}

	protected Set<Long> clearAffectedAps(MapContainerNode floor,
			List<Wall> walls, FloorCache cache) throws Exception {
		double squareSizeMetric = cache.pixelSizeMetric * cache.imgScale;
		double mapToMetric = floor.getMapToMetric();
		Set<Long> repairs = new HashSet<Long>();
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			BoMgmt.getLocationTracking().lapBoundaries(floor, cache.useA,
					plannedAP, squareSizeMetric, cache.imgScale,
					-(cache.shadesPerColor + 35 - 1));
			double leftEdge = floor.x1 * squareSizeMetric;
			double rightEdge = (floor.x2 + 1) * squareSizeMetric;
			double topEdge = floor.y1 * squareSizeMetric;
			double bottomEdge = (floor.y2 + 1) * squareSizeMetric;
			String fmt = "AP boundaries X (%.2f, %.2f) Y (%.2f, %.2f)";
			log.debug(String.format(fmt, leftEdge, rightEdge, topEdge,
					bottomEdge));
			for (Wall wall : walls) {
				double x1 = wall.getX1() * mapToMetric;
				double x2 = wall.getX2() * mapToMetric;
				double y1 = wall.getY1() * mapToMetric;
				double y2 = wall.getY2() * mapToMetric;
				fmt = "Delta wall from (%.2f, %.2f) to (%.2f, %.2f)";
				log.debug(String.format(fmt, x1, y1, x2, y2));
				if (x1 < leftEdge && x2 < leftEdge) {
					continue;
				}
				if (x1 > rightEdge && x2 > rightEdge) {
					continue;
				}
				if (y1 < topEdge && y2 < topEdge) {
					continue;
				}
				if (y1 > bottomEdge && y2 > bottomEdge) {
					continue;
				}
				clearTile(floor, plannedAP, cache);
				repairs.add(plannedAP.getId());
				break;
			}
		}
		return repairs;
	}

	protected FloorCache clearNbrAps(MapContainerNode mapContainerNode,
			PlannedAP plannedAP, Long selfId, FloorCache cache)
			throws Exception {
		if (cache == null) {
			return null;
		}
		double squareSizeMetric = cache.pixelSizeMetric * cache.imgScale;
		PlannedAP oldPlannedAP = null;
		for (PlannedAP ap : mapContainerNode.getPlannedAPs()) {
			if (ap.getId().equals(selfId)) {
				oldPlannedAP = ap;
			}
		}
		if (oldPlannedAP == null) {
			return cache;
		}
		BoMgmt.getLocationTracking().lapBoundaries(mapContainerNode,
				cache.useA, oldPlannedAP, squareSizeMetric, cache.imgScale,
				-(cache.shadesPerColor + 35 - 1));
		clearTile(mapContainerNode, oldPlannedAP, cache);
		Set<Long> repairs = new HashSet<Long>();
		findNbrAps(mapContainerNode, selfId, cache, repairs);
		if (plannedAP == null) {
			// Added or Removed
		} else {
			if (oldPlannedAP.x == plannedAP.x && oldPlannedAP.y == plannedAP.y) {
				// Updated
			} else {
				// Moved
			}
			BoMgmt.getLocationTracking().lapBoundaries(mapContainerNode,
					cache.useA, plannedAP, squareSizeMetric, cache.imgScale,
					-(cache.shadesPerColor + 35 - 1));
			findNbrAps(mapContainerNode, selfId, cache, repairs);
			clearTiles(mapContainerNode, repairs, cache);
			repairs.add(selfId);
		}
		resetRepairs(cache, repairs);
		return cache;
	}

	protected void resetRepairs(FloorCache cache, Set<Long> repairs) {
		log.info("Resetting repairs: " + repairs);
		if (cache.repairs != null) {
			log.info("Pending repairs still: " + cache.repairs);
		}
		cache.repairs = repairs;
	}

	protected void findNbrAps(MapContainerNode mapContainerNode, Long selfId,
			FloorCache cache, Set<Long> repairs) throws Exception {
		int xi1 = mapContainerNode.x1;
		int xi2 = mapContainerNode.x2;
		int yi1 = mapContainerNode.y1;
		int yi2 = mapContainerNode.y2;
		log.info("Boundaries (" + xi1 + ", " + xi2 + "), (" + yi1 + ", " + yi2
				+ ");");
		double squareSizeMetric = cache.pixelSizeMetric * cache.imgScale;
		for (PlannedAP nbrAp : mapContainerNode.getPlannedAPs()) {
			if (nbrAp.getId().equals(selfId)) {
				continue;
			}
			BoMgmt.getLocationTracking().lapBoundaries(mapContainerNode,
					cache.useA, nbrAp, squareSizeMetric, cache.imgScale,
					-(cache.shadesPerColor + 35 - 1));
			if (mapContainerNode.x2 < xi1) {
				continue;
			}
			if (xi2 < mapContainerNode.x1) {
				continue;
			}
			if (mapContainerNode.y2 < yi1) {
				continue;
			}
			if (yi2 < mapContainerNode.y1) {
				continue;
			}
			repairs.add(nbrAp.getId());
			log.info("Nbr (" + nbrAp.getId() + ") boundaries ("
					+ mapContainerNode.x1 + ", " + mapContainerNode.x2 + "), ("
					+ mapContainerNode.y1 + ", " + mapContainerNode.y2 + ");");
		}
	}

	protected void clearMapRssi(FloorCache cache) throws Exception {
		if (cache == null || null == cache.mapRssi) {
			return;
		}
		int mapWidth = cache.mapRssi.length;
		int mapHeight = cache.mapRssi[0].length;
		for (int x = 0; x < mapWidth; x += 1) {
			for (int y = 0; y < mapHeight; y += 1) {
				cache.mapRssi[x][y] = 0;
				cache.mapChannels[x][y] = 0;
			}
		}
	}

	protected void clearTiles(MapContainerNode mapContainerNode,
			Set<Long> repairs, FloorCache cache) throws Exception {
		double squareSizeMetric = cache.pixelSizeMetric * cache.imgScale;
		for (PlannedAP nbrAp : mapContainerNode.getPlannedAPs()) {
			if (repairs.contains(nbrAp.getId())) {
				BoMgmt.getLocationTracking().lapBoundaries(mapContainerNode,
						cache.useA, nbrAp, squareSizeMetric, cache.imgScale,
						-(cache.shadesPerColor + 35 - 1));
				clearTile(mapContainerNode, nbrAp, cache);
			}
		}
	}

	protected void clearTile(MapContainerNode mapContainerNode,
			PlannedAP plannedAP, FloorCache cache) {
		int xi1 = mapContainerNode.x1;
		int xi2 = mapContainerNode.x2;
		int yi1 = mapContainerNode.y1;
		int yi2 = mapContainerNode.y2;
		Short apIndex = cache.apIndexMap.get(plannedAP.getId());
		log.info("Clearing tile: " + plannedAP.getId() + ", index: " + apIndex);
		if (apIndex == null || cache.mapRssi == null) {
			return;
		}
		int mapWidth = cache.mapRssi.length;
		int mapHeight = cache.mapRssi[0].length;
		for (int x = xi1; x <= xi2; x += 1) {
			for (int y = yi1; y <= yi2; y += 1) {
				if (x < 0 || y < 0) {
					continue;
				}
				if (x >= mapWidth || y >= mapHeight) {
					continue;
				}
				if (cache.mapChannels[x][y] == apIndex) {
					cache.mapRssi[x][y] = 0;
					cache.mapChannels[x][y] = 0;
				}
			}
		}
	}

	protected FloorCache addToFloorCache(MapContainerNode floor,
			PlannedAP plannedAP) throws Exception {
		FloorCache cache = getFloorCache(floor.getId());
		if (cache == null) {
			return null;
		}
		short ch1is[] = new short[cache.ch1is.length + 1];
		short ch2is[] = new short[cache.ch2is.length + 1];
		for (int i = 0; i < cache.ch1is.length; i++) {
			ch1is[i] = cache.ch1is[i];
			ch2is[i] = cache.ch2is[i];
		}
		cache.ch1is = ch1is;
		cache.ch2is = ch2is;
		cache.apIndexMap.put(plannedAP.getId(),
				(short) (cache.ch1is.length - 1));
		return cache;
	}

	protected JSONArray jsonArray = null;

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray != null) {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		} else if (jsonObject != null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			return "{}";
		}
	}

	private int m_x, m_y;

	protected short getShadesPerColor() {
		return (short) (rssiThreshold - 35 + 1);
	}

	protected int canvasWidth, canvasHeight, layers, frequency,
			rssiThreshold = 90, snrThreshold = 5, rateThreshold;

	protected int pwr1, ch1, pwr2, ch2;

	protected boolean showSpill;

	protected String apLabels;
	protected String rapLabels;

	protected List<Long> xs, ys;

	protected List<Integer> tps;

	public void setShowSpill(boolean showSpill) {
		this.showSpill = showSpill;
	}

	public List<Long> getXs() {
		return xs;
	}

	public void setXs(List<Long> xs) {
		this.xs = xs;
	}

	public List<Long> getYs() {
		return ys;
	}

	public void setYs(List<Long> ys) {
		this.ys = ys;
	}

	public List<Integer> getTps() {
		return tps;
	}

	public void setTps(List<Integer> tps) {
		this.tps = tps;
	}

	protected double scale;

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public static long scale(double d, double scale) {
		return Math.round(d * scale);
	}

	public double reverseScale(long d) {
		return d / scale;
	}

	public void setCh1(int ch1) {
		this.ch1 = ch1;
	}

	public void setPwr1(int pwr1) {
		this.pwr1 = pwr1;
	}

	public void setCh2(int ch2) {
		this.ch2 = ch2;
	}

	public void setPwr2(int pwr2) {
		this.pwr2 = pwr2;
	}

	public void setCanvasWidth(int canvasWidth) {
		this.canvasWidth = canvasWidth;
	}

	public void setCanvasHeight(int canvasHeight) {
		this.canvasHeight = canvasHeight;
	}

	protected TopoPdfReport pdfReport = null;

	protected FloorCache getFloorCache(Long mapId) {
		if (mapId == null) {
			return null;
		}
		FloorCache cache = (FloorCache) MgrUtil
				.getSessionAttribute(SessionKeys.MAP_FLOOR_CACHE);
		if (cache != null && mapId.equals(cache.mapId)) {
			return cache;
		} else {
			return null;
		}
	}

	protected void setFloorCache(FloorCache cache) {
		MgrUtil.setSessionAttribute(SessionKeys.MAP_FLOOR_CACHE, cache);
	}

	protected void removeFloorCache() {
		MgrUtil.removeSessionAttribute(SessionKeys.MAP_FLOOR_CACHE);
	}

	protected class FloorCache {
		protected FloorCache(Long mapId) {
			this.mapId = mapId;
		}

		private final Long mapId;
		protected Map<Long, Short> apIndexMap;
		protected short ch1is[];
		protected short ch2is[];
		protected double pixelSizeMetric;
		protected int imgScale = -1;
		protected boolean useA;
		protected short channelWidth;
		protected int layers;
		protected short shadesPerColor;
		protected float spillRssi[][];
		protected short spillChannels[][];
		protected float mapRssi[][];
		protected short mapChannels[][];
		protected Set<Long> repairs;
	}

}