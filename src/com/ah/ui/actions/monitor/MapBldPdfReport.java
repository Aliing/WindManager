package com.ah.ui.actions.monitor;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.LocationTracking;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.PlannedAP;
import com.ah.bo.monitor.Vertex;

public class MapBldPdfReport {
	private static final Log log = LogFactory.getLog("commonlog.MapBldPdf");

	private DrawHeatmapAction drawHeatmap;

	public MapBldPdfReport(DrawHeatmapAction drawHeatmap) {
		this.drawHeatmap = drawHeatmap;
	}

	Map<Long, FloorView> fvs = new HashMap<Long, FloorView>();

	protected void loadCanvas(MapContainerNode selectedMap,
			PlanToolConfig planToolConfig) throws Exception {
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

		// below is new add
		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject json = jsonArray.getJSONObject(i);
			Long id = (Long) json.get("id");
			StringBuffer ch1is = (StringBuffer) json.get("ch1is");
			StringBuffer ch2is = (StringBuffer) json.get("ch2is");
			StringBuffer ch1s = (StringBuffer) json.get("ch1s");
			StringBuffer ch2s = (StringBuffer) json.get("ch2s");
			StringBuffer apids = (StringBuffer) json.get("apids");
			mapCh1is.put(id, getIntegerListFromString(ch1is, "&cis="));
			mapCh2is.put(id, getIntegerListFromString(ch2is, "&cis="));
			mapCh1s.put(id, getIntegerListFromString(ch1s, "&chs="));
			mapCh2s.put(id, getIntegerListFromString(ch2s, "&chs="));
			mapApids.put(id, getLongListFromString(apids, "&ids="));
		}

		for (FloorView floor : floors) {
			fvs.put(floor.getFloor().getId(), floor);
		}
	}

	protected JSONArray jsonArray = null;

	public Map<Long, List<Integer>> mapCh1is = new HashMap<Long, List<Integer>>();
	public Map<Long, List<Integer>> mapCh2is = new HashMap<Long, List<Integer>>();
	public Map<Long, List<Integer>> mapCh1s = new HashMap<Long, List<Integer>>();
	public Map<Long, List<Integer>> mapCh2s = new HashMap<Long, List<Integer>>();
	public Map<Long, List<Long>> mapApids = new HashMap<Long, List<Long>>();

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

	public static List<Integer> getIntegerListFromString(StringBuffer str,
			String bz) {
		// "&cis=1&cis=21&cis=321&cis=4321"
		if (str == null || str.length() == 0) {
			return null;
		}

		List<Integer> lst = new ArrayList<Integer>();

		int idx = 0;
		int idx2 = -1;
		String tmp;
		while (true) {
			idx2 = str.indexOf(bz, idx + 5);
			if (idx2 > idx) {
				tmp = str.substring(idx + 5, idx2);
				lst.add(Integer.valueOf(tmp));
				idx = idx2;
			} else {
				tmp = str.substring(idx + 5, str.length());
				lst.add(Integer.valueOf(tmp));
				break;
			}
		}

		return lst;
	}

	public static List<Long> getLongListFromString(StringBuffer str, String bz) {
		if (str == null || str.length() == 0) {
			return null;
		}

		List<Long> lst = new ArrayList<Long>();

		int idx = 0;
		int idx2 = -1;
		String tmp;
		while (true) {
			idx2 = str.indexOf(bz, idx + 5);
			if (idx2 > idx) {
				tmp = str.substring(idx + 5, idx2);
				lst.add(Long.valueOf(tmp));
				idx = idx2;
			} else {
				tmp = str.substring(idx + 5, str.length());
				lst.add(Long.valueOf(tmp));
				break;
			}
		}

		return lst;
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

	protected BufferedImage createFloorImage(MapContainerNode selectedMap,
			MapNode node) throws Exception {
		calculateFloorSize(selectedMap.getChildNodes(), false);
		MapContainerNode floor = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, node.getId(), new QueryFloor());
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

	protected int floorWidth, floorHeight, borderX, borderY;

	protected double floorScale, originX, originY;

	protected List<Long> ids;

	protected List<Integer> cis, chs;

	protected String nodeId;

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

	class QueryFloor implements QueryBo {
		public Collection<HmBo> load(HmBo bo) {
			if (bo == null) {
				return null;
			}

			if (bo instanceof MapContainerNode) {
				MapContainerNode mapContainerNode = (MapContainerNode) bo;
				mapContainerNode.getChildNodes().size();
				// BoMgmt.getPlannedApMgmt().loadPlannedAPs(mapContainerNode,
				// mapContainerNode.getChildNodes());
				mapContainerNode.getPerimeter().size();
				mapContainerNode.getWalls().size();
				mapContainerNode.getPlannedAPs().size();

			}
			return null;
		}

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
}
class FloorView {
    public FloorView(MapContainerNode floor) {
        this.floor = floor;
        calculateDeviceCounts();
    }

    private MapContainerNode floor;
    private double area;
    private int apCount = 0;
    private int brCount = 0;
    private int srCount = 0;
    private int vpnCount = 0;
    private int count4Average = 0;

    public MapContainerNode getFloor() {
        return floor;
    }

    public void setAreaMetric(double areaMetric) {
        area = areaMetric;
        if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
            area /= Math.pow(LocationTracking.FEET_TO_METERS, 2);
        }
    }

    public String getArea() {
        return String.format(getAreaFormat(), area);
    }

    public String getAreaMeters() {
        double areaMeters = area;
        if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
            areaMeters *= Math.pow(LocationTracking.FEET_TO_METERS, 2);
        }
        return String.format("%1$.2f", areaMeters);
    }

    public String getAreaFeet() {
        double areaFeet = area;
        if (floor.getLengthUnit() != MapContainerNode.LENGTH_UNIT_FEET) {
            areaFeet /= Math.pow(LocationTracking.FEET_TO_METERS, 2);
        }
        return String.format("%1$.0f", areaFeet);
    }

    public String getLengthUnit() {
        if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
            return "feet";
        } else {
            return "meters";
        }
    }

    public String getAreaUnit() {
        if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
            return "sq ft";
        } else {
            return "sq m";
        }
    }

    public String getAreaAp() {
        double count = getAvailableCount();
        if (count > 0) {
            return String.format(getAreaFormat(), area / count);
        } else {
            return "";
        }
    }

    public String getAreaApMeters() {
        double count = getAvailableCount();
        if (count == 0) {
            return "";
        }
        double areaMeters = area / count;
        if (floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET) {
            areaMeters *= Math.pow(LocationTracking.FEET_TO_METERS, 2);
        }
        return String.format("%1$.2f", areaMeters);
    }

    public String getAreaApFeet() {
        double count = getAvailableCount();
        if (count == 0) {
            return "";
        }
        double areaFeet = area / count;
        if (floor.getLengthUnit() != MapContainerNode.LENGTH_UNIT_FEET) {
            areaFeet /= Math.pow(LocationTracking.FEET_TO_METERS, 2);
        }
        return String.format("%1$.0f", areaFeet);
    }

    public int getAvailableCount() {
        return count4Average;
    }

    public int getApCount() {
        return apCount;
    }

    public int getBrCount() {
        return brCount;
    }

    public int getSrCount() {
        return srCount;
    }

    public int getVpnCount() {
		return vpnCount;
	}

	private void calculateDeviceCounts() {
        if(floor.getPlannedAPs().size() == 0) {
            for (MapNode leafNode : floor.getChildNodes()) {
                if (leafNode.isLeafNode()) {
                    final HiveAp hiveAp = ((MapLeafNode) leafNode).getHiveAp();
                    if (null == hiveAp) {
                        // handle the simulate problem
                        continue;
                    }
                    final short deviceModel = hiveAp.getHiveApModel();
                    if (HiveAp.isBranchRouterProduct(deviceModel)) {
                        brCount++;
                        
                        if(deviceModel != HiveAp.HIVEAP_MODEL_BR200) {
                            count4Average++;
                        }
                    } else if (HiveAp.isSwitchProduct(deviceModel)) {
                        srCount++;
                    } else if(hiveAp.isCVGAppliance()){
                    	vpnCount ++;
                    }else{
                    	apCount++;
                        
                        count4Average++;
                    }
                }
            }
        } else {
            // planing
            for (PlannedAP planned : floor.getPlannedAPs()) {
                if(HiveAp.isBranchRouterProduct(planned.apModel)) {
                    brCount++;
                } else {
                    apCount++;
                }
                
                count4Average++;
            }
        }
    }

    public String getOriginX() {
        return String.format(getAreaFormat(), floor.getOriginX());
    }

    public String getOriginY() {
        return String.format(getAreaFormat(), floor.getOriginY());
    }

    private String getAreaFormat() {
        return floor.getLengthUnit() == MapContainerNode.LENGTH_UNIT_FEET ? "%1$.0f"
                : "%1$.2f";
    }
}