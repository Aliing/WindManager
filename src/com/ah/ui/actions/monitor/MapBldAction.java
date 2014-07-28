package com.ah.ui.actions.monitor;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.LocationTracking;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.tools.PlanToolAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.UserSettingsUtil;

public class MapBldAction extends DrawHeatmapAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			MapBldAction.class.getSimpleName());

	public String execute() throws Exception {
		try {
			if ("loadingMap2".equals(operation)) {
				return loadCanvas();
			} else if ("floor".equals(operation)) {
				log.info("Floor id: " + id);
				streamImage(createFloorImage());
				return null;
			} else if ("spill".equals(operation)) {
				log.info("Spill main floor id: " + id + ", spillOnly: "
						+ showSpill);
				FloorCache cache = getFloorCache(id);
				if (cache == null || cache.mapChannels == null) {
					jsonObject.put("success", false);
					return "json";
				}
				log.info("Draw from cache.");
				short[][] mapColors = mergeSpillRssi(cache.spillRssi,
						cache.mapRssi, cache.shadesPerColor, showSpill);
				streamSpillImage(cache, mapColors, cache.spillChannels);
				return null;
			} else if ("spilled".equals(operation)) {
				log.info("Spilled floor id: " + id + ", node: " + nodeId
						+ ", showSpill: " + showSpill);
				MapContainerNode floor = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id);
				long plannedId = Long.parseLong(nodeId.substring(1));
				PlannedAP plannedAP = BoMgmt.getPlannedApMgmt().findPlannedAP(
						plannedId);
				FloorCache cache = getFloorCache(id);
				if (cache == null) {
					return null;
				}
				Short apIndex = cache.apIndexMap.get(plannedAP.getId());
				if (apIndex != null) {
					spilledApImage(floor, plannedAP, apIndex, cache);
					streamSpillImage(cache, floor.getMapColors(),
							floor.getMapChannels());
				}
				return null;
			} else if ("repairSpilled".equals(operation)) {
				log.info("Repair spilled floor id: " + id);
				jsonObject = new JSONObject();
				MapContainerNode floor = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				FloorCache cache = getFloorCache(id);
				if (cache == null) {
					jsonObject.put("success", false);
					return "json";
				}
				repairSpilled(floor, cache);
				jsonObject.put("success", true);
				return "json";
			} else if ("updateOrigin".equals(operation)) {
				log.info("Origin (" + originX + ", " + originY + ")");
				MapContainerNode floor = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id);
				floor.setOriginX(originX);
				floor.setOriginY(originY);
				QueryUtil.updateBo(floor);
				jsonObject = new JSONObject();
				jsonObject.put("success", true);
				return "json";
			} else if ("removeSimAp".equals(operation)) {
				long plannedId = Long.parseLong(nodeId.substring(1));
				log.info("execute", "removing: " + nodeId + ", " + plannedId);
				MapContainerNode floor = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				BoMgmt.getPlannedApMgmt().removePlannedAP(plannedId);
				clearNbrAps(floor, null, plannedId, getFloorCache(id));
				jsonObject = new JSONObject();
				jsonObject.put("x", ch1);
				jsonObject.put("y", ch2);
				jsonObject.put("w", pwr1);
				jsonObject.put("h", pwr2);
				return "json";
			} else if ("removeAllSimAps".equals(operation)) {
				MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				log.info("execute", "Removing "
						+ mapContainerNode.getPlannedAPs().size()
						+ " planned APs.");
				clearMapRssi(getFloorCache(id));
				for (PlannedAP plannedAP : mapContainerNode.getPlannedAPs()) {
					BoMgmt.getPlannedApMgmt()
							.removePlannedAP(plannedAP.getId());
				}
				return "json";
			} else if ("saveWalls".equals(operation)) {
				log.info("execute", "saveWalls, scale: " + scale);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				List<Wall> newWalls = new ArrayList<Wall>();
				if (xs != null) {
					for (int i = 0; i < xs.size(); i += 2) {
						Wall wall = new Wall();
						wall.setX1(reverseScale(xs.get(i)));
						wall.setY1(reverseScale(ys.get(i)));
						wall.setX2(reverseScale(xs.get(i + 1)));
						wall.setY2(reverseScale(ys.get(i + 1)));
						wall.setType(Wall.wallIds[tps.get(i)]);
						newWalls.add(wall);
					}
				}
				MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				List<Wall> oldWalls = mapContainerNode.getWalls();
				mapContainerNode.setWalls(newWalls);
				QueryUtil.updateBo(mapContainerNode);
				FloorCache cache = getFloorCache(id);
				if (cache != null) {
					List<Wall> deltaWalls = diffWalls(oldWalls, newWalls);
					Set<Long> repairs = clearAffectedAps(mapContainerNode,
							deltaWalls, cache);
					resetRepairs(cache, repairs);
				}
				return "json";
			} else if ("validateGeoPerimeter".equals(operation)) {
				jsonObject = new JSONObject();
				if (lats != null) {
					jsonObject.put("success",
							validateGeoPerimeter((MapContainerNode) null));
				}
				return "json";
			} else if ("saveGeoPerimeter".equals(operation)) {
				jsonObject = new JSONObject();
				if (lats != null) {
					MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
							.findBoById(MapNode.class, id, this);
					log.info_ln("Building: " + mapContainerNode.getMapName()
							+ " already has "
							+ mapContainerNode.getChildNodes().size()
							+ " floors.");
					if (mapContainerNode.getChildNodes().size() == 0) {
						MapContainerNode floor = new MapContainerNode();
						floor.setParentMap(mapContainerNode);
						floor.setOwner(mapContainerNode.getOwner());
						floor.setMapName("floor1");
						floor.setEnvironment(EnumConstUtil.MAP_ENV_AUTO);
						floor.setMapType(MapContainerNode.MAP_TYPE_FLOOR);
						floor.setLengthUnit(MapContainerNode.LENGTH_UNIT_METERS);
						floor.setApElevation(3);
						if (validateGeoPerimeter(floor)) {
							Long mapId = BoMgmt.getMapMgmt()
									.createMapContainer(floor, null);
							jsonObject.put("floorId", mapId);
							BoObserver.notifyListeners(new BoEvent(floor,
									BoEventType.CREATED));

							// refresh the instance permissions
							refreshInstancePermissions();
						}
					}
				}
				return "json";
			} else if ("savePerimeter".equals(operation)) {
				log.info("execute", "Scale: " + scale);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				int maxId = -1;
				if (xs != null) {
					MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
							.findBoById(MapNode.class, id, this);
					List<Vertex> perimeter = mapContainerNode.getPerimeter();
					for (Vertex vertex : perimeter) {
						if (vertex.getId() > maxId) {
							maxId = vertex.getId();
						}
					}
					List<Wall> walls = new ArrayList<Wall>();
					for (int i = 0; i < xs.size(); i++) {
						log.info("execute", "Perimeter node: (" + xs.get(i)
								+ ", " + ys.get(i) + ")");
						Vertex vertex = new Vertex();
						vertex.setId(maxId + 1);
						vertex.setX(reverseScale(xs.get(i)));
						vertex.setY(reverseScale(ys.get(i)));
						perimeter.add(vertex);
						Wall wall = new Wall();
						wall.setX1(vertex.getX());
						wall.setY1(vertex.getY());
						walls.add(wall);
					}
					QueryUtil.updateBo(mapContainerNode);
					setPerimeterRepairs(mapContainerNode, walls);
				}
				jsonObject = new JSONObject();
				jsonObject.put("id", maxId + 1);
				jsonObject.put("tp", Wall.getWallIndex((short) 1003));
				return "json";
			} else if ("removePerimeter".equals(operation)) {
				log.info("Execute", "Remove Perim, scale: " + scale
						+ ", prm id: " + pwr1);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				List<Vertex> perimeter = mapContainerNode.getPerimeter();
				List<Vertex> newPerimeter = new ArrayList<Vertex>();
				List<Wall> walls = new ArrayList<Wall>();
				for (Vertex vertex : perimeter) {
					if (vertex.getId() == pwr1) {
						Wall wall = new Wall();
						wall.setX1(vertex.getX());
						wall.setY1(vertex.getY());
						walls.add(wall);
					} else {
						newPerimeter.add(vertex);
					}
				}
				mapContainerNode.setPerimeter(newPerimeter);
				QueryUtil.updateBo(mapContainerNode);
				setPerimeterRepairs(mapContainerNode, walls);
				return "json";
			} else if ("updatePerimeter".equals(operation)) {
				log.info("Execute", "Update perim, scale: " + scale
						+ ", prm id: " + pwr1 + ", wall type: " + pwr2);
				MgrUtil.removeSessionAttribute(SessionKeys.MR_HEAT_MAP);
				MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id, this);
				List<Vertex> perimeter = mapContainerNode.getPerimeter();
				List<Wall> walls = new ArrayList<Wall>();
				for (Vertex vertex : perimeter) {
					if (vertex.getId() == pwr1) {
						vertex.setType(pwr2 >= 0 && pwr2 < Wall.wallIds.length ? Wall.wallIds[pwr2]
								: 1003);
						Wall wall = new Wall();
						wall.setX1(vertex.getX());
						wall.setY1(vertex.getY());
						walls.add(wall);
					}
				}
				QueryUtil.updateBo(mapContainerNode);
				setPerimeterRepairs(mapContainerNode, walls);
				return "json";
			} else if ("updateGeo".equals(operation)) {
				log.info_ln("Updating lat/long " + ctrLat + "/" + ctrLong
						+ ", zm: " + ctrZm + ", vt: " + ctrVt);
				MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id);
				mapContainerNode.setCenterLatitude(ctrLat);
				mapContainerNode.setCenterLongitude(ctrLong);
				mapContainerNode.setCenterZoom(ctrZm);
				mapContainerNode.setViewType(ctrVt);
				QueryUtil.updateBo(mapContainerNode);
				return "json";
			} else if ("updateGeoContainer".equals(operation)) {
				log.info_ln("Updating lat/long " + ctrLat + "/" + ctrLong
						+ " for container: " + id);
				MapContainerNode mapContainerNode = (MapContainerNode) QueryUtil
						.findBoById(MapNode.class, id);
				mapContainerNode.setLatitude(ctrLat);
				mapContainerNode.setLongitude(ctrLong);
				QueryUtil.updateBo(mapContainerNode);
				return "json";
			} else if ("updateSortFolders".equals(operation)
					&& sortFolders != null) {
				log.info_ln("New sort folders: " + sortFolders);
				/*HmUser user = QueryUtil.findBoById(HmUser.class,
						userContext.getId());
				user.setOrderFolders(!sortFolders);
				QueryUtil.updateBo(user);*/
				// changed in Geneva, for user setting columns separated from hm_user
				UserSettingsUtil.updateOrderFolders(this.userContext.getEmailAddress(), !sortFolders);
				
				userContext.setOrderFolders(!sortFolders);
				return "json";
			} else if ("updateTreeWidth".equals(operation) && treeWidth > 0
					&& treeWidth != userContext.getTreeWidth()) {
				log.info_ln("New tree width: " + treeWidth);
				/*HmUser user = QueryUtil.findBoById(HmUser.class,
						userContext.getId());
				user.setTreeWidth(treeWidth);
				QueryUtil.updateBo(user);*/
				// changed in Geneva, for user setting columns separated from hm_user
				UserSettingsUtil.updateTreeWidth(this.userContext.getEmailAddress(), treeWidth);
				
				userContext.setTreeWidth(treeWidth);
				return "json";
			} else if ("apChannels".equals(operation)) {
				log.info_ln("Channels for AP model: " + treeWidth);
				jsonArray = new JSONArray(getChannelNaType(treeWidth));
				return "json";
			}
		} catch (Exception e) {
			log.error("execute", "execute error. operation:" + operation, e);
			addActionError(MgrUtil.getUserMessage(e));
		}
		return null;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode mapContainerNode = (MapContainerNode) bo;
		if ("loadingMap2".equals(operation)) {
			for (MapNode mapNode : mapContainerNode.getChildNodes()) {
				if (mapNode.isLeafNode()) {
					continue;
				}
				MapContainerNode floor = (MapContainerNode) mapNode;
				floor.getChildNodes().size();
				for (MapNode childNode : floor.getChildNodes()) {
					if (childNode.isLeafNode()) {
						MapLeafNode leafNode = ((MapLeafNode) childNode);
						if (null != leafNode.getHiveAp()) {
							leafNode.getHiveAp().getDeviceType();
						}
					}
				}
				BoMgmt.getPlannedApMgmt().loadPlannedAPs(floor,
						floor.getChildNodes());
				floor.getPerimeter().size();
			}
		} else if ("floor".equals(operation)
				|| "repairSpilled".equals(operation)
				|| "removeSimAp".equals(operation)
				|| "removeAllSimAps".equals(operation)) {
			// Just to trigger load from database
			mapContainerNode.getChildNodes().size();
			BoMgmt.getPlannedApMgmt().loadPlannedAPs(mapContainerNode,
					mapContainerNode.getChildNodes());
			mapContainerNode.getPerimeter().size();
			mapContainerNode.getWalls().size();
		} else if ("saveWalls".equals(operation)) {
			mapContainerNode.getWalls().size();
			BoMgmt.getPlannedApMgmt().loadPlannedAPs(mapContainerNode,
					mapContainerNode.getChildNodes());
		} else if ("savePerimeter".equals(operation)
				|| "removePerimeter".equals(operation)
				|| "updatePerimeter".equals(operation)) {
			mapContainerNode.getPerimeter().size();
			BoMgmt.getPlannedApMgmt().loadPlannedAPs(mapContainerNode,
					mapContainerNode.getChildNodes());
		} else if ("saveGeoPerimeter".equals(operation)) {
			// Just to trigger load from database
			mapContainerNode.getChildNodes().size();
		}
		return null;
	}

	protected String loadCanvas() throws Exception {
		MapContainerNode selectedMap = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, getSelectedMapId(), this);
		PlanToolConfig planToolConfig = PlanToolAction
				.getPlanToolConfig(domainId);
		if (selectedMap != null
				&& selectedMap.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
			Map<Short, Short> ch1IndexMap = new HashMap<Short, Short>();
			Map<Short, Short> ch2IndexMap = new HashMap<Short, Short>();
			BoMgmt.getLocationTracking().assignBldChannels(selectedMap,
					planToolConfig, ch1IndexMap, ch2IndexMap);
			List<FloorView> floors = calculateFloorSize(
					selectedMap.getChildNodes(), true);
			Collections.sort(floors, new Comparator<FloorView>() {
				@Override
				public int compare(FloorView o1, FloorView o2) {
					return o2.getFloor().getMapOrder() - o1.getFloor().getMapOrder();
				}
			});
			setChannelIndexes(floors, ch1IndexMap, ch2IndexMap);
			page = floors;
			setMapWritePermission(selectedMap);
			return "loadingMap3";
		}
		if (isPlanningOnly()) {
			bgMapOpacity = planToolConfig.getBgMapOpacity();
			heatMapOpacity = planToolConfig.getHeatMapOpacity();
			wallsOpacity = planToolConfig.getWallsOpacity();
		} else {
			MapSettings mapSettings = BeTopoModuleUtil
					.getMapGlobalSetting(getDomain());
			bgMapOpacity = mapSettings.getBgMapOpacity();
			heatMapOpacity = mapSettings.getHeatMapOpacity();
			wallsOpacity = mapSettings.getWallsOpacity();
		}
		return "loadingMap2";
	}

	private boolean mapWritePermission;

	private void setMapWritePermission(MapContainerNode mapNode) {
		if (getShowDomain()) {
			if (!HmDomain.HOME_DOMAIN
					.equals(mapNode.getOwner().getDomainName())) {
				mapWritePermission = false;
				return;
			}
		}
		id = mapNode.getId();
		mapWritePermission = getWritePermission()
				&& getWriteInstancePermission();
	}

	public String getMapWriteDisabled() {
		if (mapWritePermission)
			return "";
		return "disabled";
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
	}

	protected BufferedImage createFloorImage() throws Exception {
		MapContainerNode selectedMap = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, getSelectedMapId(), this);
		calculateFloorSize(selectedMap.getChildNodes(), false);
		MapContainerNode floor = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, id, this);
		double actualWidth = floorWidth / floorScale;
		if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
			actualWidth /= LocationTracking.FEET_TO_METERS;
		}
		double gridSize = getGridSize(actualWidth);
		Map<Long, Integer> channelMap = new HashMap<Long, Integer>();
		Map<Long, Integer> colorMap = new HashMap<Long, Integer>();
		if (ids != null && cis != null && chs != null) {
			for (int i = 0; i < ids.size(); i++) {
				channelMap.put(ids.get(i), chs.get(i));
				colorMap.put(ids.get(i), cis.get(i));
			}
		}
		return BoMgmt.getLocationTracking().createFloorImage(floor, floorScale,
				floorWidth, floorHeight, channelMap, colorMap, borderX,
				borderY, gridSize);
	}

	protected int getNumberWidth(double d) {
		int size = 0;
		while (d > 100) {
			size += 7;
			d /= 10;
		}
		return size;
	}

	protected List<FloorView> calculateFloorSize(Set<MapNode> nodes,
			boolean view) throws Exception {
		List<FloorView> floors = null;
		if (view) {
			floors = new ArrayList<FloorView>();
		}
		short lengthUnit = MapContainerNode.LENGTH_UNIT_METERS;
		double maxWidth = 0, maxHeight = 0;
		for (MapNode mapNode : nodes) {
			if (mapNode.isLeafNode()) {
				continue;
			}

			MapContainerNode container = (MapContainerNode) mapNode;
			if (container.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
				lengthUnit = MapContainerNode.LENGTH_UNIT_FEET;
			}
			if (view) {
				FloorView floor = new FloorView(container);
				int count = getPerimeterCount(floor);
				if (count > 0) {
					Perimeter[] perimeters = new Perimeter[count];
					calculatePerimeterAreas(container, perimeters);
					calculatePerimeterHierarchy(container, perimeters);
					floor.setAreaMetric(calculateCoverageArea(perimeters));
					log.info(container.getMapName() + " coverage area: "
							+ floor.getArea() + " " + floor.getAreaUnit());
				}
				floors.add(floor);
			}
			double newWidth = container.getOriginXmetric()
					+ container.getActualWidthMetric();
			if (newWidth > maxWidth) {
				maxWidth = newWidth;
			}
			double newHeight = container.getOriginYmetric()
					+ container.getActualHeightMetric();
			if (newHeight > maxHeight) {
				maxHeight = newHeight;
			}
		}
		floorWidth = 500;
		floorScale = floorWidth / maxWidth;
		floorHeight = (int) (floorScale * maxHeight);
		if (floorHeight == 0) {
			floorHeight = 300;
		}
		borderX = 16;
		borderY = 15;
		if (lengthUnit == MapContainerNode.LENGTH_UNIT_FEET) {
			maxHeight /= LocationTracking.FEET_TO_METERS;
		}
		borderX += getNumberWidth(maxHeight);
		return floors;
	}

	protected void setChannelIndexes(List<FloorView> floors,
			Map<Short, Short> ch1IndexMap, Map<Short, Short> ch2IndexMap)
			throws Exception {
		Collection<JSONObject> jsonNodes = new Vector<JSONObject>();
		for (FloorView floor : floors) {
			JSONObject jo = new JSONObject();
			jo.put("id", floor.getFloor().getId());
			setChannelIndexes(floor.getFloor(), ch1IndexMap, ch2IndexMap, jo);
			jsonNodes.add(jo);
		}
		jsonArray = new JSONArray(jsonNodes);
	}

	protected void setChannelIndexes(MapContainerNode floor,
			Map<Short, Short> ch1IndexMap, Map<Short, Short> ch2IndexMap,
			JSONObject floorChannels) throws Exception {
		StringBuffer i1s = new StringBuffer();
		StringBuffer i2s = new StringBuffer();
		StringBuffer c1s = new StringBuffer();
		StringBuffer c2s = new StringBuffer();
		StringBuffer ids = new StringBuffer();
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			short wifi0Channel = plannedAP.autoWifi0Channel;
			if (wifi0Channel < 0) {
				wifi0Channel = plannedAP.wifi0Channel;
			}
			Short ch1i = ch1IndexMap.get(wifi0Channel);

			short wifi1Channel = plannedAP.autoWifi1Channel;
			if (wifi1Channel < 0) {
				wifi1Channel = plannedAP.wifi1Channel;
			}
			Short ch2i = ch2IndexMap.get(wifi1Channel);
			i1s.append("&cis=" + ch1i);
			i2s.append("&cis=" + ch2i);
			c1s.append("&chs=" + wifi0Channel);
			c2s.append("&chs=" + wifi1Channel);
			ids.append("&ids=" + plannedAP.getId());
		}
		for (MapNode mapNode : floor.getChildNodes()) {
			if (!mapNode.isLeafNode()) {
				continue;
			}
			MapLeafNode mapLeafNode = (MapLeafNode) mapNode;
			short wifi0Channel = mapLeafNode.getRadioChannelBG();
			Short ch1i = 0;
			if (wifi0Channel > 0) {
				ch1i = ch1IndexMap.get(wifi0Channel);
			}
			short wifi1Channel = mapLeafNode.getRadioChannelA();
			Short ch2i = 0;
			if (wifi1Channel > 0) {
				ch2i = ch2IndexMap.get(wifi1Channel);
			}
			i1s.append("&cis=" + ch1i);
			i2s.append("&cis=" + ch2i);
			c1s.append("&chs=" + wifi0Channel);
			c2s.append("&chs=" + wifi1Channel);
			ids.append("&ids=" + mapNode.getId());
		}
		floorChannels.put("ch1is", i1s);
		floorChannels.put("ch2is", i2s);
		floorChannels.put("ch1s", c1s);
		floorChannels.put("ch2s", c2s);
		floorChannels.put("apids", ids);
	}

	protected int getPerimeterCount(FloorView floor) {
		List<Vertex> perimeters = floor.getFloor().getPerimeter();
		if (perimeters.size() == 0) {
			return 0;
		}
		int count = 1;
		Vertex vertex = perimeters.get(0);
		int perimId = vertex.getId();
		for (int i = 1; i < perimeters.size(); i++) {
			vertex = perimeters.get(i);
			if (vertex.getId() != perimId) {
				perimId = vertex.getId();
				count++;
			}
		}
		return count;
	}

	protected void calculatePerimeterAreas(MapContainerNode floor,
			Perimeter[] perimeters) {
		if (floor.getPerimeter().size() == 0) {
			return;
		}
		double mapToMetric = floor.getMapToMetric();
		int start = 0;
		double area = 0;
		Vertex vertex = floor.getPerimeter().get(0);
		double x1 = vertex.getX();
		double y1 = vertex.getY();
		double first_x = x1;
		double first_y = y1;
		int perimId = vertex.getId();
		int perimIndex = 0;
		for (int i = 1; i < floor.getPerimeter().size(); i++) {
			vertex = floor.getPerimeter().get(i);
			double x2 = vertex.getX();
			double y2 = vertex.getY();
			if (vertex.getId() != perimId) {
				area = (area + x1 * first_y - first_x * y1) / 2 * mapToMetric
						* mapToMetric;
				perimeters[perimIndex++] = new Perimeter(start, i - 1, area,
						perimeters.length);
				start = i;
				area = 0;
				first_x = x2;
				first_y = y2;
				perimId = vertex.getId();
			} else {
				area += x1 * y2 - x2 * y1;
			}
			x1 = x2;
			y1 = y2;
		}
		area = (area + x1 * first_y - first_x * y1) / 2 * mapToMetric
				* mapToMetric;
		perimeters[perimIndex] = new Perimeter(start, floor.getPerimeter()
				.size() - 1, area, perimeters.length);
	}

	protected void calculatePerimeterHierarchy(MapContainerNode floor,
			Perimeter[] perimeters) {
		for (int i = 0; i < perimeters.length; i++) {
			Vertex vertex = floor.getPerimeter().get(perimeters[i].start);
			double x = vertex.getX();
			double y = vertex.getY();
			short[] inside = perimeters[i].inside;
			short depth = 0;
			for (int j = 0; j < inside.length; j++) {
				if (i == j) {
					inside[j] = 1;
					continue;
				}
				if (inside[j] == 0) {
					if (inside(floor, x, y, perimeters[j])) {
						inside[j] = 2;
						depth++;
						if (j < i) {
							for (int k = j + 1; k < inside.length; k++) {
								if (perimeters[j].inside[k] == 2) {
									inside[k] = 2;
									depth++;
								}
							}
						}
					} else {
						inside[j] = 1;
					}
				}
			}
			perimeters[i].depth = depth;
		}
	}

	protected double calculateCoverageArea(Perimeter[] perimeters) {
		double aggregate = 0;
		for (int i = 0; i < perimeters.length; i++) {
			if (perimeters[i].depth % 2 != 0) {
				aggregate -= perimeters[i].area;
			} else {
				aggregate += perimeters[i].area;
			}
		}
		return aggregate;
	}

	public String insideVector(short[] a, String name, short depth, double area) {
		StringBuffer s = new StringBuffer(name + " = [");
		for (int i = 0; i < a.length; i++) {
			s.append((a[i] - 1) + "; ");
		}
		s.append("]; depth: " + depth + ", area: " + area);
		return s.toString();
	}

	protected boolean inside(MapContainerNode floor, double x, double y,
			Perimeter perimeter) {
		List<Double> edgesX = findEdgesX(floor, y, perimeter);
		boolean inside = false;
		for (int leftEdge = 0; leftEdge < edgesX.size()
				&& edgesX.get(leftEdge) <= x; leftEdge++) {
			inside = !inside;
		}
		return inside;
	}

	protected List<Double> findEdgesX(MapContainerNode floor, double y,
			Perimeter perimeter) {
		List<Double> edges = new ArrayList<Double>();
		Vertex vertex = floor.getPerimeter().get(perimeter.start);
		double x1 = vertex.getX();
		double y1 = vertex.getY();
		double first_x = x1;
		double first_y = y1;
		for (int i = perimeter.start + 1; i <= perimeter.end; i++) {
			vertex = floor.getPerimeter().get(i);
			double x2 = vertex.getX();
			double y2 = vertex.getY();
			findIntersectionX(x1, y1, x2, y2, y, edges);
			x1 = x2;
			y1 = y2;
		}
		findIntersectionX(x1, y1, first_x, first_y, y, edges);
		Collections.sort(edges, new Comparator<Double>() {
			@Override
			public int compare(Double o1, Double o2) {
				return o1.compareTo(o2);
			}
		});
		return edges;
	}

	protected void findIntersectionX(double x1, double y1, double x2,
			double y2, double cy, List<Double> edges) {
		double tol = 1e-6;
		double dy = y2 - y1;
		if (Math.abs(dy) < tol) {
			// This wall is parallel to x axis.
			return;
		}
		if (dy > 0) {
			if (cy < y1 || cy >= y2) {
				// Candidate is outside the Y boundaries.
				return;
			}
		} else {
			if (cy < y2 || cy >= y1) {
				// Candidate is outside the Y boundaries.
				return;
			}
		}
		Double xi = llix(x1, y1, x2, y2, cy);
		edges.add(xi);
	}

	protected double llix(double x1, double y1, double x2, double y2, double cy) {
		// Line between l1p1 and l1p2
		double a1 = y2 - y1;
		double b1 = x1 - x2;
		double c1 = a1 * x1 + b1 * y1;
		// Intersection point
		double x = (c1 - b1 * cy) / a1;
		return x;
	}

	private short[][] mergeSpillRssi(float spillRssi[][], float mapRssi[][],
			short shadesPerColor, boolean spillOnly) {
		int imageWidth = spillRssi.length;
		int imageHeight = spillRssi[0].length;
		short[][] mapColors = new short[imageWidth][imageHeight];
		for (int x = 0; x < imageWidth; x += 1) {
			for (int y = 0; y < imageHeight; y += 1) {
				long s_rssi = Math.round(spillRssi[x][y]);
				long m_rssi = Math.round(mapRssi[x][y]);
				if (s_rssi == 0) {
					mapColors[(int) x][(int) y] = -1;
				} else if (!spillOnly && m_rssi != 0
						&& mapRssi[x][y] > spillRssi[x][y]) {
					mapColors[(int) x][(int) y] = -1;
				} else {
					if (s_rssi > -35) {
						s_rssi = -35;
					}
					short rssiColor = (short) (-35 - s_rssi);
					if (rssiColor >= shadesPerColor) {
						// Should be filtered already really
						rssiColor = -1;
					}
					mapColors[(int) x][(int) y] = rssiColor;
				}
			}
		}
		return mapColors;
	}

	private void spilledApImage(MapContainerNode mapContainerNode,
			PlannedAP plannedAP, short apIndex, FloorCache cache)
			throws Exception {
		double squareSizeMetric = cache.pixelSizeMetric * cache.imgScale;
		BoMgmt.getLocationTracking().lapBoundaries(mapContainerNode,
				cache.useA, plannedAP, squareSizeMetric, cache.imgScale,
				-(cache.shadesPerColor + 35 - 1));

		int imageWidthUsed = cache.mapRssi.length;
		int imageHeightUsed = cache.mapRssi[0].length;

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

		int xi1 = mapContainerNode.x1;
		int xi2 = mapContainerNode.x2;
		int yi1 = mapContainerNode.y1;
		int yi2 = mapContainerNode.y2;

		int mapWidth = cache.mapRssi.length;
		int mapHeight = cache.mapRssi[0].length;
		int imageWidth = xi2 - xi1 + 1;
		int imageHeight = yi2 - yi1 + 1;
		short mapColors[][] = new short[imageWidth][imageHeight];
		short mapChannels[][] = new short[imageWidth][imageHeight];
		for (int x = xi1; x <= xi2; x += 1) {
			for (int y = yi1; y <= yi2; y += 1) {
				if (x < 0 || y < 0) {
					mapColors[x - xi1][y - yi1] = -1;
					mapChannels[x - xi1][y - yi1] = 0;
					continue;
				}
				if (x >= mapWidth || y >= mapHeight) {
					mapColors[x - xi1][y - yi1] = -1;
					mapChannels[x - xi1][y - yi1] = 0;
					continue;
				}
				if (cache.mapRssi[x][y] != 0
						&& apIndex == cache.mapChannels[x][y]) {
					float s_rssi = cache.spillRssi[(int) x][(int) y];
					short rssiColor = -1;
					if (showSpill && s_rssi != 0
							&& cache.mapRssi[x][y] <= s_rssi) {
						// spill image will cover this area
					} else {
						long m_rssi = Math.round(cache.mapRssi[x][y]);
						if (m_rssi > -35) {
							m_rssi = -35;
						}
						rssiColor = (short) (-35 - m_rssi);
						if (rssiColor >= cache.shadesPerColor) {
							// Should be filtered already really
							rssiColor = -1;
						}
					}
					mapColors[x - xi1][y - yi1] = rssiColor;
					mapChannels[x - xi1][y - yi1] = cache.mapChannels[x][y];
				} else {
					mapColors[x - xi1][y - yi1] = -1;
					mapChannels[x - xi1][y - yi1] = 0;
				}
			}
		}
		mapContainerNode.setMapColors(mapColors);
		mapContainerNode.setMapChannels(mapChannels);
	}

	protected void streamSpillImage(FloorCache cache, short mapColors[][],
			short mapChannels[][]) throws Exception {
		int auto = 1;
		String userAgent = request.getHeader("user-agent");
		if (userAgent != null && userAgent.contains("MSIE")) {
			auto = cache.imgScale;
		}
		if ((cache.layers & 1) > 0) {
			streamImage(BoMgmt.getLocationTracking().drawRssiImage(mapColors,
					cache.shadesPerColor, auto));
		} else if ((cache.layers & 2) > 0) {
			short chis[] = cache.useA ? cache.ch2is : cache.ch1is;
			streamImage(BoMgmt.getLocationTracking().drawChannelImage(
					mapColors, mapChannels, chis, cache.shadesPerColor, auto));
		}
	}

	protected void repairSpilled(MapContainerNode floor, FloorCache cache)
			throws Exception {
		Set<Long> repairs = cache.repairs;
		cache.repairs = null;
		if (repairs == null) {
			return;
		}
		for (PlannedAP plannedAP : floor.getPlannedAPs()) {
			if (repairs.contains(plannedAP.getId())) {
				Short apIndex = cache.apIndexMap.get(plannedAP.getId());
				if (apIndex == null) {
					continue;
				}
				double squareSizeMetric = cache.pixelSizeMetric
						* cache.imgScale;
				boolean affected = BoMgmt.getLocationTracking()
						.findSpilledApCoveredArea(floor, cache.spillRssi,
								cache.mapRssi, cache.mapChannels,
								cache.imgScale, cache.useA,
								cache.shadesPerColor, squareSizeMetric,
								cache.channelWidth, plannedAP, apIndex);
			}
		}
	}

	protected List<Wall> diffWalls(List<Wall> oldWalls, List<Wall> newWalls) {
		if (newWalls.size() == 0) {
			log.info("All " + oldWalls.size() + " walls were removed.");
			return oldWalls;
		}
		List<Wall> deltaWalls = new ArrayList<Wall>();
		int i;
		for (i = 0; i < oldWalls.size(); i++) {
			Wall oldWall = oldWalls.get(i);
			Wall newWall = null;
			if (i < newWalls.size()) {
				newWall = newWalls.get(i);
			} else {
				log.info("Wall Removed: " + oldWall.toString());
				deltaWalls.add(oldWall);
				return deltaWalls;
			}
			if (oldWall.getX1() == newWall.getX1()
					&& oldWall.getY1() == newWall.getY1()) {
				// One side matches
				if (oldWall.getX2() == newWall.getX2()
						&& oldWall.getY2() == newWall.getY2()) {
					if (oldWall.getType() == newWall.getType()) {
						log.debug("Matching wall: " + oldWall.toString());
					} else {
						log.info("Wall type updated: " + oldWall.toString());
						deltaWalls.add(oldWall);
						return deltaWalls;
					}
				} else {
					log.info("Wall moved (old): " + oldWall.toString());
					log.info("Wall moved (new): " + newWall.toString());
					deltaWalls.add(oldWall);
					deltaWalls.add(newWall);
				}
			} else if (oldWall.getX2() == newWall.getX2()
					&& oldWall.getY2() == newWall.getY2()) {
				log.info("Wall moved (old): " + oldWall.toString());
				log.info("Wall moved (new): " + newWall.toString());
				deltaWalls.add(oldWall);
				deltaWalls.add(newWall);
			} else if (oldWalls.size() == newWalls.size()) {
				log.info("Wall moved both ends (old): " + oldWall.toString());
				log.info("Wall moved both ends (new): " + newWall.toString());
				deltaWalls.add(oldWall);
				deltaWalls.add(newWall);
			} else {
				log.info("Wall Removed: " + oldWall.toString());
				deltaWalls.add(oldWall);
				// Assumes only 1 wall can be removed as once
				return deltaWalls;
			}
		}
		while (i < newWalls.size()) {
			Wall newWall = newWalls.get(i++);
			deltaWalls.add(newWall);
			log.info("Wall added: " + newWall.toString());
		}
		return deltaWalls;
	}

	protected void setPerimeterRepairs(MapContainerNode floor, List<Wall> walls)
			throws Exception {
		FloorCache cache = getFloorCache(id);
		if (cache != null) {
			Wall prevWall = walls.get(walls.size() - 1);
			for (Wall wall : walls) {
				prevWall.setX2(wall.getX1());
				prevWall.setY2(wall.getY1());
				prevWall = wall;
			}
			Set<Long> repairs = clearAffectedAps(floor, walls, cache);
			resetRepairs(cache, repairs);
		}
	}

	protected boolean validateGeoPerimeter(MapContainerNode floor)
			throws Exception {
		double degToRad = Math.PI / 180.0;
		double lat1 = lats.get(0);
		double lng1 = lngs.get(0);
		double firstLat = lat1;
		double firstLng = lng1;
		double dlat1cos = Math.pow(Math.cos(degToRad * lat1), 2);
		double x_min = 0, x_max = 0, y_min = 0, y_max = 0;
		List<Vertex> perimeter = new ArrayList<Vertex>();
		int perimId = 0;
		Vertex v = new Vertex();
		v.setX(0);
		v.setY(0);
		perimeter.add(v);
		for (int i = 1; i < lats.size() - 1; i++) {
			double lat2 = lats.get(i);
			double dLat = degToRad * (lat2 - lat1);
			double lng2 = lngs.get(i);
			double dLon = degToRad * (lng2 - lng1);

			if (lat2 == firstLat && lng2 == firstLng) {
				perimId++;
				i++;
				firstLat = lats.get(i);
				firstLng = lngs.get(i);
				lat2 = lats.get(i);
				dLat = degToRad * (lat2 - lat1);
				lng2 = lngs.get(i);
				dLon = degToRad * (lng2 - lng1);
			}

			double adx = dlat1cos * Math.sin(dLon / 2) * Math.sin(dLon / 2);
			double cdx = 2 * Math.atan2(Math.sqrt(adx), Math.sqrt(1 - adx));
			double dx = 6371000.0 * cdx;
			if (lng2 < lng1) {
				dx = -dx;
			}
			if (dx < x_min) {
				x_min = dx;
			}
			if (dx > x_max) {
				x_max = dx;
			}

			double ady = Math.pow(Math.sin(dLat / 2), 2);
			double cdy = 2 * Math.atan2(Math.sqrt(ady), Math.sqrt(1 - ady));
			double dy = 6371000.0 * cdy;
			if (lat1 < lat2) {
				dy = -dy;
			}
			if (dy < y_min) {
				y_min = dy;
			}
			if (dy > y_max) {
				y_max = dy;
			}
			v = new Vertex();
			v.setId(perimId);
			v.setX(dx);
			v.setY(dy);
			log.info_ln("adding vertex[" + perimeter.size() + "]: (" + v.getX()
					+ ", " + v.getY() + ")");
			perimeter.add(v);
		}
		double width = x_max - x_min;
		double px = width * 0.1;
		double height = y_max - y_min;
		double py = height * 0.1;
		for (Vertex vertex : perimeter) {
			vertex.setX(vertex.getX() - x_min + px);
			vertex.setY(vertex.getY() - y_min + py);
		}
		if (!validateGeoPerimeter(perimeter)) {
			return false;
		}
		if (floor != null) {
			floor.setWidth(width + px * 2);
			floor.setHeight(height + py * 2);
			floor.setActualWidth(floor.getWidth());
			floor.setActualHeight(floor.getHeight());
			floor.setPerimeter(perimeter);
		}
		return true;
	}

	private boolean validateGeoPerimeter(List<Vertex> perimeter)
			throws Exception {
		int lastIndex = perimeter.size() - 1;
		Vertex vertex = perimeter.get(lastIndex);
		Point2D lastPoint = new Point2D.Double(vertex.getX(), vertex.getY());
		Point2D l1p1 = lastPoint;
		int perimId = vertex.getId();
		for (int i = lastIndex - 1; i > 0; i--) {
			vertex = perimeter.get(i);
			Point2D l1p2 = new Point2D.Double(vertex.getX(), vertex.getY());
			log.info_ln("Compare wall (" + (i + 1) + ", " + i + ")");
			if (!validateGeoPerimeter(l1p1, l1p2, perimeter, perimId)) {
				return false;
			}
			vertex = perimeter.get(i - 1);
			if (vertex.getId() != perimId) {
				log.info_ln("Not last perimeter anymore, almost done "
						+ vertex.getId());
				log.info_ln("Compare also wall (" + i + ", " + lastIndex + ")");
				return validateGeoPerimeter(l1p2, lastPoint, perimeter, perimId);
			}
			Point2D l2p1 = new Point2D.Double(vertex.getX(), vertex.getY());
			int l2p1i = i - 1;
			for (int j = i - 2; j >= 0; j--) {
				vertex = perimeter.get(j);
				if (vertex.getId() != perimId) {
					log.info_ln("Not last perimeter anymore, break "
							+ vertex.getId());
					break;
				}
				Point2D l2p2 = new Point2D.Double(vertex.getX(), vertex.getY());
				log.info_ln("with wall (" + (j + 1) + ", " + j + ")");
				if (!validateGeoPerimeter(l1p1, l1p2, l2p1, l2p2)) {
					return false;
				}
				l2p1 = l2p2;
				l2p1i = j;
			}
			if (i + 1 != lastIndex) {
				// One more wall
				log.info_ln("and wall (" + l2p1i + ", " + lastIndex + ")");
				if (!validateGeoPerimeter(l1p1, l1p2, l2p1, lastPoint)) {
					return false;
				}
			}
			l1p1 = l1p2;
		}
		return true;
	}

	private boolean validateGeoPerimeter(Point2D l1p1, Point2D l1p2,
			List<Vertex> perimeter, int lastPerimId) throws Exception {
		Point2D l2p1 = null;
		Point2D firstPoint = null;
		int firstPointIndex = 0;
		int perimId = -1;
		for (int i = 0; i < perimeter.size(); i++) {
			Vertex vertex = perimeter.get(i);
			Point2D l2p2 = new Point2D.Double(vertex.getX(), vertex.getY());
			if (vertex.getId() != perimId) {
				perimId = vertex.getId();
				log.info_ln("New perimeter: " + perimId);
				if (firstPoint != null) {
					log.info_ln("compare last wall (" + (i - 1) + ", "
							+ firstPointIndex + ")");
					if (!validateGeoPerimeter(l1p1, l1p2, l2p1, firstPoint)) {
						return false;
					}
				}
				if (perimId == lastPerimId) {
					log.info_ln("This is the last perimeter, we're done.");
					return true;
				}
				firstPoint = l2p2;
				firstPointIndex = i;
			} else {
				log.info_ln("compare with wall (" + (i - 1) + ", " + i + ")");
				if (!validateGeoPerimeter(l1p1, l1p2, l2p1, l2p2)) {
					return false;
				}
			}
			l2p1 = l2p2;
		}
		return true;
	}

	protected boolean validateGeoPerimeter(Point2D l1p1, Point2D l1p2,
			Point2D l2p1, Point2D l2p2) throws Exception {
		Point2D ip = BoMgmt.getLocationTracking().lli(l1p1, l1p2, l2p1, l2p2);
		if (ip != null) {
			log.info_ln("Intersection at: " + ip);
			return false;
		}
		return true;
	}

	private int bgMapOpacity, heatMapOpacity, wallsOpacity;

	public String getBgMapOpacity() {
		if (bgMapOpacity == 100) {
			return "1.0";
		} else {
			return "." + bgMapOpacity;
		}
	}

	public String getHeatMapOpacity() {
		if (heatMapOpacity == 100) {
			return "1.0";
		} else {
			return "." + heatMapOpacity;
		}
	}

	public String getWallsOpacity() {
		if (wallsOpacity == 100) {
			return "1.0";
		} else {
			return "." + wallsOpacity;
		}
	}

	public String getBgMapOpacityAlpha() {
		return bgMapOpacity + "";
	}

	public String getHeatMapOpacityAlpha() {
		return heatMapOpacity + "";
	}

	protected int floorWidth, floorHeight, borderX, borderY;

	protected double floorScale, originX, originY;

	protected List<Long> ids;

	protected List<Integer> cis, chs;

	protected String nodeId;

	protected Float ctrLat, ctrLong;

	protected short ctrZm;

	protected String ctrVt;

	private short treeWidth;

	protected Boolean sortFolders;

	protected List<Double> lats, lngs;

	public void setLats(List<Double> lats) {
		this.lats = lats;
	}

	public void setLngs(List<Double> lngs) {
		this.lngs = lngs;
	}

	public void setSortFolders(Boolean sortFolders) {
		this.sortFolders = sortFolders;
	}

	public void setTreeWidth(short treeWidth) {
		this.treeWidth = treeWidth;
	}

	public void setCtrLat(Float ctrLat) {
		this.ctrLat = ctrLat;
	}

	public void setCtrLong(Float ctrLong) {
		this.ctrLong = ctrLong;
	}

	public void setCtrZm(short ctrZm) {
		this.ctrZm = ctrZm;
	}

	public void setCtrVt(String ctrVt) {
		this.ctrVt = ctrVt;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public void setIds(List<Long> ids) {
		this.ids = ids;
	}

	public void setCis(List<Integer> cis) {
		this.cis = cis;
	}

	public void setChs(List<Integer> chs) {
		this.chs = chs;
	}

	public int getImageWidth() {
		return floorWidth + borderX + 1;
	}

	public int getImageHeight() {
		return floorHeight + borderY + 1;
	}

	public void setOriginX(String originX) {
		this.originX = originX == null || originX.length() == 0 ? 0 : Double
				.parseDouble(originX);
	}

	public void setOriginY(String originY) {
		this.originY = originY == null || originY.length() == 0 ? 0 : Double
				.parseDouble(originY);
	}

	private class Perimeter {
		private Perimeter(int start, int end, double area, int count) {
			this.start = start;
			this.end = end;
			this.area = Math.abs(area);
			inside = new short[count];
		}

		private int start, end;
		private double area;
		private short[] inside;
		private short depth;
	}

}
