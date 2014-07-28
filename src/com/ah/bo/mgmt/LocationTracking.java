package com.ah.bo.mgmt;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.Idp;
import com.ah.bo.monitor.LocationRssiReport;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.performance.AhClientSession;

/*
 * @author Chris Scheers
 */

public interface LocationTracking {
	public int computeRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, int canvasWidth, int canvasHeight,
			String bssid, Long acspId, Long nextId, double tx, double ty,
			boolean useA, boolean useBG, short shadesPerColor, long latchId)
			throws Exception;

	public int simApCoverage(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor) throws Exception;

	public Collection<JSONObject> cacheDoubleBuffer(MapContainerNode floor,
			int canvasWidth, int canvasHeight, Map<Long, Short> apIndexMap,
			float spillRssi[][], short spillChannels[][], float mapRssi[][],
			short mapChannels[][], double pixelSizeMetric, int imgScale,
			boolean useA, short shadesPerColor, boolean spillOnly,
			short channelWidth) throws Exception;

	public boolean findSpilledApCoveredArea(MapContainerNode floor,
			float spillRssi[][], float mapRssi[][], short mapChannels[][],
			int imgScale, boolean useA, short shadesPerColor,
			double squareSizeMetric, short channelWidth, PlannedAP plannedAP,
			short apIndex) throws Exception;

	public boolean findCandidateCoverage(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, PlannedAP plannedAP)
			throws Exception;

	public double predictedMapSize(MapContainerNode mapContainerNode,
			double power, short apModel, boolean useA, double rssiThreshold)
			throws Exception;

	public void predictedLap(MapContainerNode mapContainerNode,
			int imageWidthUsed, int imageHeightUsed, boolean useA,
			short shadesPerColor, PlannedAP plannedAP, short channelWidth,
			double squareSizeMetric, int imgScale, int rssiThreshold)
			throws Exception;

	public void lapBoundaries(MapContainerNode mapContainerNode, boolean useA,
			PlannedAP plannedAP, double squareSizeMetric, int imgScale,
			int rssiThreshold) throws Exception;

	public int getRateRssiThreshold(int rate, boolean useA, short channelWidth,
			int fadeMargin);

	public Point2D lli(Point2D l1p1, Point2D l1p2, Point2D l2p1, Point2D l2p2);

	public List<PlannedAP> autoSimAps(MapContainerNode mapContainerNode,
			PlanToolConfig planConfig, int canvasWidth, int canvasHeight,
			boolean useA, short shadesPerColor, double target) throws Exception;

	public BufferedImage createFloorImage(MapContainerNode floor, double scale,
			int floorWidth, int floorHeight, Map<Long, Integer> channelMap,
			Map<Long, Integer> colorMap, int borderX, int borderY,
			double gridSize) throws Exception;

	public List<MapContainerNode> assignBldChannels(
			MapContainerNode mapContainerNode, PlanToolConfig planToolConfig,
			Map<Short, Short> chgIndexMap, Map<Short, Short> chaIndexMap)
			throws Exception;

	public List<MapContainerNode> assignChannels(
			MapContainerNode mapContainerNode, PlanToolConfig planToolConfig,
			Map<Short, Short> chgIndexMap, Map<Short, Short> chaIndexMap)
			throws Exception;

	public boolean estimateRssiHr(MapContainerNode mapContainerNode,
			long latchId);

	public short[][] addChannelBoundaries(short mapColors[][],
			short mapChannels[][]) throws Exception;

	public BufferedImage drawSnrImage(short mapColors[][],
			short shadesPerColor, int fademargin, int imgScale)
			throws Exception;

	public BufferedImage drawRssiImage(short mapColors[][],
			short shadesPerColor, int imgScale) throws Exception;

	public BufferedImage drawChannelImage(short mapColors[][],
			short mapChannels[][], short apChannel[], short channelColor,
			short shadesPerColor, int imgScale) throws Exception;

	public BufferedImage drawChannelImage(short mapColors[][],
			short mapChannels[][], short chis[], short shadesPerColor,
			int imgScale) throws Exception;

	public BufferedImage drawInterferenceImage(short mapColors[][],
			short mapChannels[][], short apInterference[], short shadesPerColor)
			throws Exception;

	public BufferedImage drawRatesImage(short mapColors[][], int rateThreshold,
			short apModel, int fadeMargin, short channelWidth,
			short shadesPerColor, boolean useA, int imgScale) throws Exception;

	public BufferedImage drawRssiArea(MapContainerNode mapContainerNode,
			int canvasWidth, int canvasHeight) throws Exception;

	public Collection<JSONObject> getRssiRange(short shadesPerColor)
			throws Exception;

	public JSONObject acspNbrRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, Long leafNodeId,
			boolean useA) throws Exception;

	public Collection<JSONObject> locateClients(
			MapContainerNode mapContainerNode, Set<MapNode> nodes, Long pageId,
			double imageWidth, BufferedImage image, double scale)
			throws Exception;

	public Collection<JSONObject> clientRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String clientMac)
			throws Exception;

	public JSONObject validateClientLocation(HmDomain domain, Long clientId,
			boolean circle) throws Exception;

	public Collection<JSONObject> locateRogues(
			MapContainerNode mapContainerNode, Set<MapNode> nodes, Long pageId,
			double imageWidth, BufferedImage image, double scale)
			throws Exception;

	public Collection<JSONObject> rogueRssi(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String bssid)
			throws Exception;

	public JSONObject validateRogueLocation(HmDomain domain, Long idpId)
			throws Exception;

	public int calibrateRogue(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String bssid,
			double x, double y) throws Exception;

	public int calibrateClient(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, Long pageId, double scale, String clientMac,
			double x, double y) throws Exception;

	public void findClientRssi(HmDomain domain, List<AhClientSession> clients)
			throws Exception;

	public void findRogueRssi(HmDomain domain, List<Idp> rogues)
			throws Exception;

	public void locateSource(Collection<LocationRssiReport> reports)
			throws Exception;

	public Map<String, MapLeafNode> fetchRadioAttributes(
			Collection<MapNode> nodes);

	public int fetchActiveNbrs(MapContainerNode mapContainerNode,
			Set<MapNode> nodes, boolean useA, long latchId, JSONObject jsonObj)
			throws Exception;

	public static double FEET_TO_METERS = 0.3048;
}
