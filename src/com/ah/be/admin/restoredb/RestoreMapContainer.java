package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.AhAlarm;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.Vertex;
import com.ah.bo.monitor.Wall;
import com.ah.util.EnumConstUtil;

public class RestoreMapContainer {

	private static AhRestoreGetXML xmlParser = null;

	private static HashMap<Long, Long> map_parentMap;
	private static HashMap<String, Set<Long>> parent2children;

	private static List<MapContainerNode> getAllMapContainer()
			throws AhRestoreException, AhRestoreColNotExistException {
		xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_node.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("map_node");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read map_node.xml file.");
			return null;
		}

		AhRestoreGetParamDTO params = new AhRestoreGetParamDTO();
		params.addParam("mapname", MapMgmt.ROOT_MAP_NAME);
		String[] rootMapRow = xmlParser.getRow(params);

		MapContainerNode rootMap = getMapContainerNode(rootMapRow);

//		List<MapContainerNode> mapContainers = new ArrayList<MapContainerNode>();
//		if (null != rootMap) {
//			mapContainers.add(rootMap);
//			getSubMaps(mapContainers, rootMap);
//		}
		
		if(null != rootMap){
			Map<Long, MapContainerNode> allNodes = readAllMapContainers();
			List<MapContainerNode> mapContainers = reorder(allNodes, rootMap.getId());
			return mapContainers;
		}else{
			return Collections.emptyList();
		}
	}
	
	private static Map<Long, MapContainerNode> readAllMapContainers() throws AhRestoreException, AhRestoreColNotExistException {
		Map<Long, MapContainerNode> mapContainers = new HashMap<Long, MapContainerNode>();
		int rowCount = xmlParser.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			String[] row = xmlParser.getRow(i);
			MapContainerNode mapContainerNode = getMapContainerNode(row);
			if(null != mapContainerNode){
				mapContainers.put(mapContainerNode.getId(), mapContainerNode);
			}
		}
		return mapContainers;
	}
	
	private static List<MapContainerNode> reorder(Map<Long, MapContainerNode> allNodes, Long rootId) {
		List<MapContainerNode> mapContainers = new ArrayList<MapContainerNode>();
		addChildren(allNodes, mapContainers, rootId);
		return mapContainers;
	}
	
	private static void addChildren(Map<Long, MapContainerNode> allNodes, List<MapContainerNode> nodes, Long parentId) {
		// add parent first
		nodes.add(allNodes.get(parentId));
		Set<Long> children = parent2children.get(String.valueOf(parentId));
		if(null != children && !children.isEmpty()){
			// add children
			for(Long id : children){
				addChildren(allNodes, nodes, id);
			}
		}
	}

	@SuppressWarnings("unused")
	private static void getSubMaps(List<MapContainerNode> mapContainers,
			MapContainerNode mapNode) throws AhRestoreException,
			AhRestoreColNotExistException {
		String mapId = mapNode.getId().toString();

		AhRestoreGetParamDTO params = new AhRestoreGetParamDTO();
		params.addParam("parent_map_id", mapId);
		params.addParam("node_type", "CN");

		String[][] rows = xmlParser.getRows(params);

		if (null != rows && rows.length > 0) {
			for (int i = 0; i < rows.length; i++) {
				MapContainerNode node = getMapContainerNode(rows[i]);
				if (null != node) {
					mapContainers.add(node);
					getSubMaps(mapContainers, node);
				}
			}
		}
	}

	private static MapContainerNode getMapContainerNode(String[] rowValue)
			throws AhRestoreException, AhRestoreColNotExistException {
		if (null == rowValue) {
			return null;
		}

		String colName;
		boolean isColPresent;
		MapContainerNode node = new MapContainerNode();

		/**
		 * Judge node type
		 */
		colName = "node_type";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		if (!isColPresent) {
			/**
			 * The node_type column must be exist in the table of map_node
			 */
			return null;
		}

		String node_type = xmlParser.getColVal(rowValue, colName);
		if (node_type == null || node_type.trim().equals("")
				|| node_type.trim().equalsIgnoreCase("null")
				|| node_type.trim().equalsIgnoreCase("LN")) {
			return null;
		}

		/**
		 * Set id
		 */
		colName = "id";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		if (!isColPresent) {
			/**
			 * The id column must be exist in the table of map_node
			 */
			return null;
		}

		String id = xmlParser.getColVal(rowValue, colName);
		if (id == null || id.trim().equals("")
				|| id.trim().equalsIgnoreCase("null")) {
			return null;
		}
		node.setId(Long.valueOf(id));

		/**
		 * Set severity
		 */
		colName = "severity";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String severity = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: String.valueOf(AhAlarm.AH_SEVERITY_UNDETERMINED);
		node.setSeverity((short) AhRestoreCommons.convertInt(severity));

		/**
		 * Set x
		 */
		colName = "x";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String x = isColPresent ? xmlParser.getColVal(rowValue, colName) : "";
		node.setX(AhRestoreCommons.convertDouble(x));

		/**
		 * Set y
		 */
		colName = "y";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String y = isColPresent ? xmlParser.getColVal(rowValue, colName) : "";
		node.setY(AhRestoreCommons.convertDouble(y));

		/**
		 * Set background
		 */
		colName = "background";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String background = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : "";
		node.setBackground(AhRestoreCommons.convertString(background));

		/**
		 * Set icon
		 */
		colName = "iconname";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String iconName = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "";
		node.setIconName(getRevisedMapIcon(AhRestoreCommons
				.convertString(iconName)));
		
		/**
		 * Set address
		 */
		colName = "address";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
		        colName);
		String address = isColPresent ? xmlParser.getColVal(rowValue, colName)
		        : "";
		node.setAddress(address.trim().equals("")
                || address.trim().equalsIgnoreCase("null") ? null : address);

		/**
		 * Set height
		 */
		colName = "height";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String height = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "";
		node.setHeight(AhRestoreCommons.convertDouble(height, 1000));

		/**
		 * Set mapname
		 */
		colName = "mapname";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String mapname = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "";
		node.setMapName(AhRestoreCommons.convertString(mapname));

		/**
		 * Set originx
		 */
		colName = "originx";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String originx = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "0";
		node.setOriginX(AhRestoreCommons.convertDouble(originx));
		
		/**
		 * Set originy
		 */
		colName = "originy";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String originy = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "0";
		node.setOriginY(AhRestoreCommons.convertDouble(originy));
		
		/**
		 * Set floorloss
		 */
		colName = "floorloss";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String floorloss = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "20";
		node.setFloorLoss(AhRestoreCommons.convertDouble(floorloss));
		
		/**
		 * Set maporder
		 */
		colName = "maporder";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String maporder = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "1";
		node.setMapOrder(AhRestoreCommons.convertInt(maporder));
		
		/**
		 * Set maptype
		 */
		colName = "maptype";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String maptype = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "1";
		node.setMapType(Short.parseShort(maptype));
		
		/**
		 * actualheight
		 */
		colName = "actualheight";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String actualheight = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : "0";
		node.setActualHeight(AhRestoreCommons.convertDouble(actualheight));

		/**
		 * actualwidth
		 */
		colName = "actualwidth";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String actualwidth = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : "0";
		node.setActualWidth(AhRestoreCommons.convertDouble(actualwidth));

		/**
		 * environment
		 */
		colName = "environment";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String environment = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : String.valueOf(EnumConstUtil.MAP_ENV_ENTERPRISE);
		node.setEnvironment(AhRestoreCommons.convertInt(environment));

		/**
		 * lengthunit
		 */
		colName = "lengthunit";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String lengthunit = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : String.valueOf(MapContainerNode.LENGTH_UNIT_FEET);
		node.setLengthUnit((short) AhRestoreCommons.convertInt(lengthunit));

		/**
		 * apelevation
		 */
		colName = "apelevation";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String apelevation = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : "0";
		node.setApElevation(AhRestoreCommons.convertDouble(apelevation));

		/**
		 * useheatmap
		 */
		colName = "useheatmap";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String useheatmap = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : "false";
		node.setUseHeatmap(AhRestoreCommons.convertStringToBoolean(useheatmap));

		/**
		 * Set owner
		 */
		colName = "owner";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser
				.getColVal(rowValue, colName)) : 1;
 
//		if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
//		{
//		   return null;
//		}		
		node.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

		/**
		 * Set width
		 */
		colName = "width";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String width = isColPresent ? xmlParser.getColVal(rowValue, colName)
				: "";
		node.setWidth(AhRestoreCommons.convertDouble(width, 2000));

		/**
		 * Set parent_map_id
		 */
		colName = "parent_map_id";
		isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "map_node",
				colName);
		String parent_map_id = isColPresent ? xmlParser.getColVal(rowValue,
				colName) : "";

		map_parentMap.put(AhRestoreCommons.convertLong(id), AhRestoreCommons
				.convertLong(parent_map_id));
		Set<Long> children = parent2children.get(parent_map_id);
		if(null == children){
			children = new HashSet<Long>();
			parent2children.put(parent_map_id, children);
		}
		children.add(node.getId());
		return node;
	}

	private static String getRevisedMapIcon(String oldIconName) {
		String newIconName = oldIconName;
		if ("floor.png".equals(oldIconName)) {
			newIconName = "floor_32x32.png";
		} else if ("building.png".equals(oldIconName)) {
			newIconName = "building_32x32.png";
		} else if ("hexagon.png".equals(oldIconName)) {
			newIconName = "hexagon_32x32.png";
		} else if ("oval.png".equals(oldIconName)) {
			newIconName = "oval_32x32.png";
		} else if ("house.png".equals(oldIconName)) {
			newIconName = "house_32x32.png";
		} else if ("penant.png".equals(oldIconName)) {
			newIconName = "penant_32x32.png";
		} else if ("star.png".equals(oldIconName)) {
			newIconName = "star_32x32.png";
		}
		return newIconName;
	}

	public static boolean restoreMapContainer() {
		map_parentMap = new HashMap<Long, Long>();
		parent2children = new HashMap<String, Set<Long>>();
		try {
			return restoreMap();
		} catch (Exception ex) {
			AhRestoreDBTools.logRestoreMsg(ex.getMessage());

			return false;
		}
	}

	private static boolean restoreMap() throws AhRestoreException,
			AhRestoreColNotExistException {
		
		long start = System.currentTimeMillis();

		List<MapContainerNode> allContainer = getAllMapContainer();

		if (null == allContainer || allContainer.isEmpty()) {
			AhRestoreDBTools.logRestoreMsg("Total map container is empty.");
			return false;
		}
//
//		Map<String, List<Wall>> allWall = null;
//		try {
//			allWall = getAllWalls();
//		} catch (Exception e1) {
//			AhRestoreDBTools.logRestoreMsg("Cannot get walls from xml file.",
//					e1);
//		}
//		Map<String, List<Vertex>> allPerimeter = null;
//		try {
//			allPerimeter = getAllPerimeters();
//		} catch (Exception e1) {
//			AhRestoreDBTools.logRestoreMsg(
//					"Cannot get perimeters from xml file.", e1);
//		}

		for (int i = 0; i < allContainer.size(); i++) {
			MapContainerNode node = allContainer.get(i);

			Long parent_Id = map_parentMap.get(node.getId());

			if (null == parent_Id || parent_Id <= 0) {
				if (MapMgmt.ROOT_MAP_NAME.equals(node.getMapName())) {
					try {
						List<MapContainerNode> bos = BoMgmt.findBos(
								MapContainerNode.class, null, new FilterParams(
										"mapName", MapMgmt.ROOT_MAP_NAME),
								null, null);
						Long oldId = node.getId();
						Long newId;
						if (null != bos && bos.size() > 0) {
							newId = bos.get(0).getId();
						} else {
							node.setParentMap(null);
							node.setId(null);
							node.setOwner(BoMgmt.getDomainMgmt()
									.getGlobalDomain());
							newId = QueryUtil.createBo(node);
						}
						// set map container id mapping
						AhRestoreNewMapTools.setMapMapContainer(oldId, newId);
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg(
								"Cannot get/update root map while restoring.",
								e);
					}
				}
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restored root map node: " + node.getMapName());
				continue;
			}

			Long map_parent_id_new = AhRestoreNewMapTools
					.getMapMapContainer(parent_Id);
			if (null != map_parent_id_new) {
				try {
					MapContainerNode parentContainer = QueryUtil.findBoById(
							MapContainerNode.class, map_parent_id_new);
					if (null != parentContainer) {
						String parentMapName = parentContainer.getMapName();
						MapContainerNode mapContainer = null;
						if (MapMgmt.ROOT_MAP_NAME.equals(parentMapName)) {
							// world map for per domain;
							List<MapContainerNode> worldMaps = QueryUtil
									.executeQuery(MapContainerNode.class, null,
											new FilterParams(
													"parentMap.mapName",
													parentMapName), node
													.getOwner().getId());
							if (!worldMaps.isEmpty()) {
								mapContainer = worldMaps.get(0);
							}
						}
						Long oldId = node.getId();
						Long newId;
						if (null == mapContainer) {
							node.setParentMap(parentContainer);
//							if (null != allWall) {
//								node.setWalls(allWall.get(String.valueOf(node
//										.getId())));
//							}
//							if (null != allPerimeter) {
//								node.setPerimeter(allPerimeter.get(String
//										.valueOf(node.getId())));
//							}
							node.setId(null);

							newId = QueryUtil.createBo(node);
						} else {
							// update the mapContainer if it already exist
							mapContainer
									.setActualHeight(node.getActualHeight());
							mapContainer.setActualWidth(node.getActualWidth());
							mapContainer.setBackground(node.getBackground());
							mapContainer.setHeight(node.getHeight());
							mapContainer.setIconName(node.getIconName());
							mapContainer.setMapName(node.getMapName());
							mapContainer.setParentMap(parentContainer);
							mapContainer.setSeverity(node.getSeverity());
							mapContainer.setWidth(node.getWidth());
							mapContainer.setX(node.getX());
							mapContainer.setY(node.getY());
							mapContainer.setApElevation(node.getApElevation());
							mapContainer.setEnvironment(node.getEnvironment());
							mapContainer.setLengthUnit(node.getLengthUnit());
							mapContainer.setUseHeatmap(node.isUseHeatmap());
//							if (null != allWall) {
//								mapContainer.setWalls(allWall.get(String
//										.valueOf(node.getId())));
//							}
//							if (null != allPerimeter) {
//								mapContainer.setPerimeter(allPerimeter
//										.get(String.valueOf(node.getId())));
//							}
							QueryUtil.updateBo(mapContainer);
							newId = mapContainer.getId();
						}
						// set map container id mapping
						AhRestoreNewMapTools.setMapMapContainer(oldId, newId);
					}
				} catch (Exception e) {
					AhRestoreDBTools
							.logRestoreMsg(
									"Cannot get/update map containers while restoring.",
									e);
				}
			} else {
				AhRestoreDBTools
						.logRestoreMsg("Cound not find the new map container id mapping to old id:"
								+ parent_Id);
			}
		}
		xmlParser = null;
		map_parentMap.clear();
		parent2children.clear();
		map_parentMap = null;
		parent2children = null;
		long end = System.currentTimeMillis();
		AhRestoreDBTools
				.logRestoreMsg("Restore Topology map completely. count:"
						+ allContainer.size() + ", cost:" + (end - start)
						+ " ms.");
		// restore walls
		AhRestoreDBTools.logRestoreMsg("Restore Topology walls begin.");
		int count = restoreWalls();
		long end2 = System.currentTimeMillis();
		AhRestoreDBTools
		.logRestoreMsg("Restore Topology walls completely. count:"
				+ count + ", cost:" + (end2 - end)
				+ " ms.");
		
		// restore perimeters
		AhRestoreDBTools.logRestoreMsg("Restore Topology perimeters begin");
		count = restorePerimeters();
		long end3 = System.currentTimeMillis();
		AhRestoreDBTools
		.logRestoreMsg("Restore Topology perimeters completely. count:"
				+ count + ", cost:" + (end3 - end2)
				+ " ms.");
		
		return true;
	}
	
	// the key is the old id in xml, same as the wall key
	private static int restoreWalls() {
		int count = 0, index = 0;
		while (true) {
			// loop wall files
			String fileName = "map_wall";
			if (index > 0) {
				fileName = "map_wall_" + index;
			}
			index++;
			try {
				Map<String, List<Wall>> allWall = getAllWalls(fileName);
				if (null == allWall) {
					break;
				}
				// restore walls
				Set<String> keys = allWall.keySet();
				for (String key : keys) {
					List<Wall> walls = allWall.get(key);
					if (null == walls || walls.isEmpty()) {
						continue;
					}
					// find out map node
					MapContainerNode node = findMapContainerNodeByOldId(key,
							true, false);
					if (null == node) {
						continue;
					}
					count += walls.size();
					List<Wall> origin = node.getWalls();
					if (null == origin) {
						origin = new ArrayList<Wall>();
						node.setWalls(origin);
					}
					// add new walls into the list
					origin.addAll(walls);
					try {
						node = QueryUtil.updateBo(node);
					} catch (Exception e) {
						AhRestoreDBTools.logRestoreMsg(
								"Cannot update walls for node: "
										+ node.getMapName()
										+ " while restoring.", e);
					}
				}
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg(
						"Cannot get walls from xml file: " + fileName, e);
			}
		}
		return count;
	}
	
	// the key is the old id in xml, same as the perimeter key
	private static int restorePerimeters() {
		int count = 0, index = 0;
		while (true) {
			// loop perimeters files
			String fileName = "map_perimeter";
			if (index > 0) {
				fileName = "map_perimeter_" + index;
			}
			index++;
			
			try {
				Map<String, List<Vertex>> allPerimeter = getAllPerimeters(fileName);
				if(null == allPerimeter){
					break;
				}
				// restore perimeters
				Set<String> keys = allPerimeter.keySet();
				for(String key : keys){
					List<Vertex> walls = allPerimeter.get(key);
					if(null == walls || walls.isEmpty()){
						continue;
					}
					// find out map node
					MapContainerNode node = findMapContainerNodeByOldId(key, false, true);
					if(null == node){
						continue;
					}
					count += walls.size();
					List<Vertex> origin = node.getPerimeter();
					if(null == origin){
						origin = new ArrayList<Vertex>();
						node.setPerimeter(origin);
					}
					// add new perimeters into the list
					origin.addAll(walls);
					try {
						node = QueryUtil.updateBo(node);
					} catch (Exception e) {
						AhRestoreDBTools
						.logRestoreMsg(
								"Cannot update perimeters for node: " + node.getMapName() + " while restoring.",
								e);
					}
				}
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg(
						"Cannot get perimeters from xml file: " + fileName, e);
			}
		}
		return count;
	}

	private static Map<String, List<Wall>> getAllWalls(String fileName)
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_wall.xml
		 */
		//boolean restoreRet = xmlParser.readXMLFile("map_wall");
		boolean restoreRet = xmlParser.readXMLOneFile(fileName);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<Wall>> walls = new HashMap<String, List<Wall>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			Wall wall = new Wall();
			/**
			 * Set map_id
			 */
			colName = "map_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_wall", colName);
			if (!isColPresent) {
				/**
				 * The map_id column must be exist in the table of map_wall
				 */
				continue;
			}

			String map_id = xmlParser.getColVal(i, colName);
			if (map_id == null || map_id.trim().equals("")
					|| map_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_wall", colName);
			String type = isColPresent ? xmlParser.getColVal(i, colName)
					: String.valueOf(1001);
			wall.setType((short) AhRestoreCommons.convertInt(type));

			/**
			 * Set x1
			 */
			colName = "x1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_wall", colName);
			String x1 = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			wall.setX1(AhRestoreCommons.convertDouble(x1));

			/**
			 * Set y1
			 */
			colName = "y1";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_wall", colName);
			String y1 = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			wall.setY1(AhRestoreCommons.convertDouble(y1));

			/**
			 * Set x2
			 */
			colName = "x2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_wall", colName);
			String x2 = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			wall.setX2(AhRestoreCommons.convertDouble(x2));

			/**
			 * Set y2
			 */
			colName = "y2";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_wall", colName);
			String y2 = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			wall.setY2(AhRestoreCommons.convertDouble(y2));

			if (null == walls.get(map_id)) {
				List<Wall> list = new ArrayList<Wall>();
				walls.put(map_id, list);
			}
			walls.get(map_id).add(wall);
		}
		return walls;
	}

	private static Map<String, List<Vertex>> getAllPerimeters(String filename)
			throws AhRestoreException, AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_perimeter.xml
		 */
		//boolean restoreRet = xmlParser.readXMLFile("map_perimeter");
		boolean restoreRet = xmlParser.readXMLOneFile(filename);
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<Vertex>> perimeters = new HashMap<String, List<Vertex>>();

		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {
			Vertex perimeter = new Vertex();
			/**
			 * Set map_id
			 */
			colName = "map_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_perimeter", colName);
			if (!isColPresent) {
				/**
				 * The map_id column must be exist in the table of map_wall
				 */
				continue;
			}

			String map_id = xmlParser.getColVal(i, colName);
			if (map_id == null || map_id.trim().equals("")
					|| map_id.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_perimeter", colName);
			String id = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(101);
			perimeter.setId(AhRestoreCommons.convertInt(id));

			/**
			 * Set x
			 */
			colName = "x";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_perimeter", colName);
			String x = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			perimeter.setX(AhRestoreCommons.convertDouble(x));

			/**
			 * Set y
			 */
			colName = "y";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_perimeter", colName);
			String y = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			perimeter.setY(AhRestoreCommons.convertDouble(y));

			/**
			 * Set type
			 */
			colName = "type";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"map_perimeter", colName);
			String type = isColPresent ? xmlParser.getColVal(i, colName) : String
					.valueOf(0);
			perimeter.setType((short)AhRestoreCommons.convertInt(type));
			
			
			if (null == perimeters.get(map_id)) {
				List<Vertex> list = new ArrayList<Vertex>();
				perimeters.put(map_id, list);
			}
			perimeters.get(map_id).add(perimeter);
		}
		return perimeters;
	}
	
	private static MapContainerNode findMapContainerNodeByOldId(String id, final boolean loadWalls, final boolean loadPerimeters){
		if(null == id || "".equals(id.trim())){
			return null;
		}
		Long pid = Long.parseLong(id);
		Long idInDb = AhRestoreNewMapTools.getMapMapContainer(pid);
		if(null == idInDb){
			return null;
		}
		
		MapContainerNode node = QueryUtil.findBoById(MapContainerNode.class, idInDb, new QueryBo() {
			
			@Override
			public Collection<HmBo> load(HmBo bo) {
				if(null == bo){
					return null;
				}
				if(bo instanceof MapContainerNode){
					MapContainerNode node = (MapContainerNode)bo;
					if(loadWalls){
						node.getWalls().size();
					}
					if(loadPerimeters){
						node.getPerimeter().size();
					}
				}
				return null;
			}
		});
		return node;
	}
}
