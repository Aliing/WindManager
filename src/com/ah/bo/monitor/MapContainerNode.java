package com.ah.bo.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.LocationTracking;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/*
 * @author Chris Scheers
 */

@Entity
@DiscriminatorValue("CN")
public class MapContainerNode extends MapNode {

	private static final long serialVersionUID = 1L;

	private String mapName;

	private String background;

	private double width, height;

	private double actualWidth, actualHeight;

	private double apElevation;

	private double originX, originY;

	private double floorLoss = 15;

	private int mapOrder = 1;

	private String viewType;

	public static final short MAP_TYPE_BUILDING = 2;
	public static final short MAP_TYPE_FLOOR = 3;

	private short mapType = 1;

	public static final short LENGTH_UNIT_METERS = 1;

	public static final short LENGTH_UNIT_FEET = 2;
	public static EnumItem[] LENGTH_UNITS = MgrUtil.enumItems(
			"enum.map.length.unit.", new int[] { LENGTH_UNIT_METERS,
					LENGTH_UNIT_FEET });

	private short lengthUnit = LENGTH_UNIT_FEET;

	private int environment;

	private boolean useHeatmap;

	public boolean isUseHeatmap() {
		return useHeatmap;
	}

	public void setUseHeatmap(boolean useHeatmap) {
		this.useHeatmap = useHeatmap;
	}

	public short getLengthUnit() {
		return lengthUnit;
	}

	public void setLengthUnit(short lengthUnit) {
		this.lengthUnit = lengthUnit;
	}

	public int getEnvironment() {
		return environment;
	}

	public void setEnvironment(int environment) {
		this.environment = environment;
	}

	@OneToMany(mappedBy = "parentMap", fetch = FetchType.LAZY)
	private Set<MapNode> childNodes = new HashSet<MapNode>();

	public Set<MapNode> getChildNodes() {
		return childNodes;
	}

	public void setChildNodes(Set<MapNode> childNodes) {
		this.childNodes = childNodes;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@MapKeyColumn(name = "mapkey")
	@CollectionTable(name = "MAP_LINK", joinColumns = @JoinColumn(name = "PARENT_MAP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private Map<String, MapLink> childLinks = new HashMap<String, MapLink>();

	public Map<String, MapLink> getChildLinks() {
		return childLinks;
	}

	public void setChildLinks(Map<String, MapLink> childLinks) {
		this.childLinks = childLinks;
	}

	public void addLink(MapLink childLink) {
		this.childLinks.put(childLink.getKey(), childLink);
	}

	public void removeLinks(Collection<MapLink> childLinks) {
		if (null != childLinks && childLinks.size() > 0) {
			for (MapLink link : childLinks) {
				this.childLinks.remove(link.getKey());
			}
		}
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MAP_PERIMETER", joinColumns = @JoinColumn(name = "MAP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<Vertex> perimeter = new ArrayList<Vertex>();

	public List<Vertex> getPerimeter() {
		return perimeter;
	}

	public void setPerimeter(List<Vertex> perimeter) {
		this.perimeter = perimeter;
	}

	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "MAP_WALL", joinColumns = @JoinColumn(name = "MAP_ID", nullable = true))
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<Wall> walls = new ArrayList<Wall>();

	public List<Wall> getWalls() {
		return walls;
	}

	public void setWalls(List<Wall> walls) {
		this.walls = walls;
	}

	@OneToMany(mappedBy = "parentMap", fetch = FetchType.LAZY)
	private Set<PlannedAP> plannedAPs = new HashSet<PlannedAP>();

	public Set<PlannedAP> getPlannedAPs() {
		return plannedAPs;
	}

	public void setPlannedAPs(Set<PlannedAP> plannedAPs) {
		this.plannedAPs = plannedAPs;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getMapName() {
		return mapName;
	}

	@Transient
	public String getMapNameEx() {
		if (mapName != null && mapName.length() > 15) {
			return mapName.substring(0, 15) + "...";
		} else {
			return mapName;
		}
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getActualHeight() {
		return actualHeight;
	}

	public double getActualHeightMetric() {
		if (getLengthUnit() == LENGTH_UNIT_FEET) {
			return actualHeight * LocationTracking.FEET_TO_METERS;
		} else {
			return actualHeight;
		}
	}

	public void setActualHeight(double actualHeight) {
		this.actualHeight = actualHeight;
	}

	public double getActualWidth() {
		return actualWidth;
	}

	public double getDistanceMetric(double distance) {
		if (getLengthUnit() == LENGTH_UNIT_FEET) {
			return distance * LocationTracking.FEET_TO_METERS;
		} else {
			return distance;
		}
	}

	public double getActualWidthMetric() {
		return getDistanceMetric(actualWidth);
	}

	public void setActualWidth(double actualWidth) {
		this.actualWidth = actualWidth;
	}

	public double getApElevation() {
		return apElevation;
	}

	public void setApElevation(double apElevation) {
		this.apElevation = apElevation;
	}

	public double getApElevationMetric() {
		return getDistanceMetric(apElevation);
	}

	public double getOriginXmetric() {
		return getDistanceMetric(originX);
	}

	public double getOriginYmetric() {
		return getDistanceMetric(originY);
	}

	public double getOriginX() {
		return originX;
	}

	public void setOriginX(double originX) {
		this.originX = originX;
	}

	public double getOriginY() {
		return originY;
	}

	public void setOriginY(double originY) {
		this.originY = originY;
	}

	public double getFloorLoss() {
		return floorLoss;
	}

	public void setFloorLoss(double floorLoss) {
		this.floorLoss = floorLoss;
	}

	public double getMapToMetric() {
		double mapToMetric = getActualWidth() / getWidth();
		if (getLengthUnit() == LENGTH_UNIT_FEET) {
			mapToMetric *= LocationTracking.FEET_TO_METERS;
		}
		return mapToMetric;
	}

	public int getMapOrder() {
		return mapOrder;
	}

	public void setMapOrder(int mapOrder) {
		this.mapOrder = mapOrder;
	}

	public String getViewType() {
		return viewType;
	}

	public void setViewType(String viewType) {
		this.viewType = viewType;
	}

	public short getMapType() {
		return mapType;
	}

	public void setMapType(short mapType) {
		this.mapType = mapType;
	}

	public boolean isLeafNode() {
		return false;
	}

	public String getLabel() {
		return mapName;
	}

	@Transient
	private int managedUpApCount = 0;

	@Transient
	private int managedDownApCount = 0;

	@Transient
	private int newUpApCount = 0;

	@Transient
	private int newDownApCount = 0;

	@Transient
	private int activeClientCount = 0;

	public int getManagedDownApCount() {
		return managedDownApCount;
	}

	public void setManagedDownApCount(int managedDownApCount) {
		this.managedDownApCount = managedDownApCount;
	}

	public int getManagedUpApCount() {
		return managedUpApCount;
	}

	public void setManagedUpApCount(int managedUpApCount) {
		this.managedUpApCount = managedUpApCount;
	}

	public int getNewDownApCount() {
		return newDownApCount;
	}

	public void setNewDownApCount(int newDownApCount) {
		this.newDownApCount = newDownApCount;
	}

	public int getNewUpApCount() {
		return newUpApCount;
	}

	public void setNewUpApCount(int newUpApCount) {
		this.newUpApCount = newUpApCount;
	}

	public int getActiveClientCount() {
		return activeClientCount;
	}

	public void setActiveClientCount(int activeClientCount) {
		this.activeClientCount = activeClientCount;
	}

	@Transient
	private short mapColors[][];

	@Transient
	private short mapChannels[][];

	@Transient
	private short apChannel[];

	@Transient
	public double[][] area;

	@Transient
	public double gridXsize, gridYsize;

	@Transient
	private Map<String, Integer> apIndexes;

	@Transient
	public int x1, y1, x2, y2;

	public Map<String, Integer> getApIndexes() {
		return apIndexes;
	}

	public void setApIndexes(Map<String, Integer> apIndexes) {
		this.apIndexes = apIndexes;
	}

	public short[] getApChannel() {
		return apChannel;
	}

	public void setApChannel(short[] apChannel) {
		this.apChannel = apChannel;
	}

	public short[][] getMapChannels() {
		return mapChannels;
	}

	public void setMapChannels(short[][] mapChannels) {
		this.mapChannels = mapChannels;
	}

	public short[][] getMapColors() {
		return mapColors;
	}

	public void setMapColors(short[][] mapColors) {
		this.mapColors = mapColors;
	}

	@Transient
	public boolean isAnyRealHiveAP() {
		for (MapNode node : childNodes) {
			if (!node.isLeafNode()) {
				continue;
			}
			MapLeafNode leafNode = (MapLeafNode) node;
			HiveAp hiveAp = leafNode.getHiveAp();
			if (null != hiveAp && !hiveAp.isSimulated()) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public boolean isAnySimulatedHiveAP() {
		for (MapNode node : childNodes) {
			if (!node.isLeafNode()) {
				continue;
			}
			MapLeafNode leafNode = (MapLeafNode) node;
			HiveAp hiveAp = leafNode.getHiveAp();
			if (null != hiveAp && hiveAp.isSimulated()) {
				return true;
			}
		}
		return false;
	}

	@Transient
	public boolean isAnyPlannedAP() {
		return (null != plannedAPs && !plannedAPs.isEmpty())
		/*
		 * || (null != perimeter && !perimeter.isEmpty()) || (null != walls &
		 * !walls.isEmpty())
		 */;
	}

}