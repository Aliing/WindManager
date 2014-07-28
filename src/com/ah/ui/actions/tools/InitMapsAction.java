package com.ah.ui.actions.tools;

import java.util.Collection;

import com.ah.bo.HmBo;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.events.BoEvent;
import com.ah.events.BoEvent.BoEventType;
import com.ah.events.impl.BoObserver;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumConstUtil;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class InitMapsAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(
			InitMapsAction.class.getSimpleName());

	@Override
	public String execute() throws Exception {
		try {
			planToolConfig = PlanToolAction.getPlanToolConfig(domainId);
			if ("init".equals(operation)) {
				log.info_ln("Company name: " + mapName);
				Long worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
				if (null != worldMapId) {
					MapContainerNode vhmRoot = QueryUtil.findBoById(
							MapContainerNode.class, worldMapId, this);
					log.info_ln("Root map name: " + vhmRoot.getMapName());
					if (MapMgmt.VHM_ROOT_MAP_NAME.equals(vhmRoot.getMapName())) {
						initRootMap(vhmRoot);
						return SUCCESS;
					}
				}
			}
		} catch (Exception e) {
			log.error("execute", "execute error. operation:" + operation, e);
			addActionError(MgrUtil.getUserMessage(e));
		}
		return null;
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
	}

	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode mapContainerNode = (MapContainerNode) bo;
		if ("init".equals(operation)) {
			// Just to trigger load from database
			mapContainerNode.getChildNodes().size();
		}
		return null;
	}

	private void initRootMap(MapContainerNode vhmRoot) throws Exception {
		vhmRoot.setMapName(mapName);
		vhmRoot.setIconName("oval_32x32.png");
		vhmRoot.setBackground(null);
		vhmRoot.setWidth(0);
		vhmRoot.setHeight(0);
		vhmRoot.setCenterZoom((short) 2);
		BoMgmt.getMapMgmt().updateMapContainer(vhmRoot);
		MapContainerNode city = new MapContainerNode();
		city.setOwner(vhmRoot.getOwner());
		city.setParentMap(vhmRoot);
		city.setMapName(mapCity);
		city.setIconName("oval_32x32.png");
		city.setAddress(mapStreet + ", " + mapCity);
		city.setEnvironment(EnumConstUtil.MAP_ENV_OUTDOOR_FREE_SPACE);
		city.setCenterZoom((short) 12);
		Long cityId = BoMgmt.getMapMgmt().createMapContainer(city, null);
		BoObserver.notifyListeners(new BoEvent(city, BoEventType.CREATED));
		city = QueryUtil.findBoById(MapContainerNode.class, cityId, this);
		MapContainerNode building = new MapContainerNode();
		building.setOwner(vhmRoot.getOwner());
		building.setParentMap(city);
		building.setMapType(MapContainerNode.MAP_TYPE_BUILDING);
		building.setMapName(mapStreet);
		building.setIconName("building_32x32.png");
		building.setAddress(mapStreet + ", " + mapCity);
		building.setEnvironment(EnumConstUtil.MAP_ENV_OUTDOOR_FREE_SPACE);
		buildingId = BoMgmt.getMapMgmt().createMapContainer(building, null);
		BoObserver.notifyListeners(new BoEvent(building, BoEventType.CREATED));
		refreshInstancePermissions();
		planToolConfig.setCountryCode(countryCode);
		PlanToolAction.savePlanToolConfig(planToolConfig, getDomain());
	}

	private void createContainers() throws Exception {
		Long worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
		MapContainerNode parent = QueryUtil.findBoById(MapContainerNode.class,
				worldMapId, this);
		for (int i = 1; i <= 300; i++) {
			MapContainerNode building = new MapContainerNode();
			building.setOwner(parent.getOwner());
			building.setParentMap(parent);
			building.setMapName("container" + i);
			building.setAddress("US");
			Long buildingId = BoMgmt.getMapMgmt().createMapContainer(building,
					null);
			for (int j = 1; j <= 10; j++) {
				MapContainerNode floor = new MapContainerNode();
				floor.setOwner(building.getOwner());
				floor.setParentMap(building);
				floor.setMapName("floorplan" + j);
				floor.setIconName("oval_32x32.png");
				floor.setEnvironment(EnumConstUtil.MAP_ENV_ENTERPRISE);
				floor.setBackground("map_floorplan.png");
				floor.setWidth(2216);
				floor.setHeight(1559);
				floor.setActualWidth(220);
				floor.setActualHeight(155);
				Long floorId = BoMgmt.getMapMgmt().createMapContainer(floor,
						null);
			}
		}
	}

	private String mapName, mapStreet, mapCity;
	private int countryCode;
	private Long buildingId;
	private PlanToolConfig planToolConfig;

	public Long getBuildingId() {
		return buildingId;
	}

	public void setMapName(String mapName) {
		this.mapName = mapName;
	}

	public void setMapStreet(String mapStreet) {
		this.mapStreet = mapStreet;
	}

	public void setMapCity(String mapCity) {
		this.mapCity = mapCity;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}
}
