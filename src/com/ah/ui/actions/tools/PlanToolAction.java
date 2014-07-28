package com.ah.ui.actions.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.common.NmsUtil;
import com.ah.be.topo.BeTopoModuleUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.wlan.RadioProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.monitor.MapSettingsAction;
import com.ah.ui.actions.monitor.MapsAction;
import com.ah.util.CheckItem;
import com.ah.util.CountryCode;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.util.Tracer;

public class PlanToolAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_PLAN_TOOL_MAP = "Planning map";

	private static final Tracer log = new Tracer(
			PlanToolAction.class.getSimpleName());

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("update".equals(operation) || "planning".equals(operation)) {
				if (fromTopology) {
					// from topology map
					setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
					resetPermission();// reset permission after set feature node
				}
				log.info("execute", "operation:" + operation);
				Long newMapId = saveObjects();
				setFormChanged(false);
				if ("planning".equals(operation)
						|| ("update".equals(operation) && (null != newMapId) && fromTopology)) {
					// planning redirect or
					// update from topology, if create map, then refresh map.
					Long redirectMapId = newMapId == null ? getDataSource()
							.getPlanToolMapId() : newMapId;
					if (null != redirectMapId) {
						MgrUtil.setSessionAttribute(
								SessionKeys.SELECTED_MAP_ID, redirectMapId);
					}
					return "landingMap";
				}
			} else if ("requestChannels".equals(operation)) {
				log.info("execute", "operation:" + operation);
				jsonObject = prepareChannels();
				return "json";
			} else if ("upload".equals(operation)) {
				if (fromTopology) {
					// from topology map
					setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
					resetPermission();// reset permission after set feature node
				}
				jsonObject = new JSONObject();
				String domainName = QueryUtil.findBoById(HmDomain.class,
						domainId).getDomainName();
				String fileName = FiledataFileName;
				File file = Filedata;
				boolean showSucMsg = false;
				if (null == FiledataFileName && null == Filedata) {
					fileName = imagedataFileName;
					file = imagedata;
					showSucMsg = true;
				}
				try {
				    //check permission
				    AccessControl.checkUserAccess(getUserContext(), getSelectedL2FeatureKey(), CrudOperation.UPDATE);
				    
				    if(fileName.endsWith(".jpg") || fileName.endsWith(".png")) {
				        BeTopoModuleUtil.addBackgroundImage(fileName, file,
				                domainName);
				        jsonObject.put("uploaded", true);
				        jsonObject.put("image", fileName);
				        jsonObject.put(
				                "imageFull",
				                MapsAction.getBackgroundPathByDomain(request,
				                        domainName) + fileName);
				        if (showSucMsg) {
				            jsonObject.put("sucMsg", MgrUtil.getUserMessage(
				                    "info.fileUploaded", fileName));
				        }
				    } else {
				        jsonObject.put("uploaded", false);
				        jsonObject.put("error", MgrUtil.getUserMessage("error.topo.import.background"));
				    }
				} catch (Exception e) {
					log.error("execute", "upload image error.", e);
					String msg = e.getMessage();
					if (null != msg && !"".equals(msg)) {
						jsonObject.put("uploaded", false);
						jsonObject.put("error", msg);
					}
				}
				return "json";
			} else if ("initPlanningPanelFromTopo".equals(operation)) {
				// from topology map
				setSelectedL2Feature(L2_FEATURE_MAP_VIEW);
				resetPermission();// reset permission after set feature node
				fromTopology = true;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			log.error("execute", e);
		}
		initValues();
		if (fromTopology) {
			return "initPlanningPanelFromTopo";
		} else {
			return SUCCESS;
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setDataSource(PlanToolConfig.class);
		setSelectedL2Feature(L2_FEATURE_PLANNING_TOOL);// by default
	}

	@Override
	public PlanToolConfig getDataSource() {
		return (PlanToolConfig) dataSource;
	}

	private Long saveObjects() throws Exception {
		if (plannedMap) {
			return savePlannedSimulatedValues();
		} else {
			saveSimulatedValue();
			return null;
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode mapContainerNode = (MapContainerNode) bo;
		if ("planning".equals(operation) || "update".equals(operation)) {
			// Just to trigger load from database
			mapContainerNode.getChildNodes().size();
		}
		return null;
	}

	private Long createPlanToolMap(short mapEnv, short lengthUnit,
			String mapImage, Double sizeX, Double sizeY, Double mapWidth,
			Double installHeight) throws Exception {
		MapContainerNode mapNode = (MapContainerNode) QueryUtil.findBoById(
				MapNode.class, BoMgmt.getMapMgmt().getWorldMapId(domainId),
				this);
		if (null != mapNode) {
			// if the vhm root map not initialize, update this map,
			// or create a new sub-map.
			if (MapMgmt.VHM_ROOT_MAP_NAME.equals(mapNode.getMapName())) {
				MapSettingsAction.updateMapContainerNode(mapNode, mapEnv,
						DEFAULT_PLAN_TOOL_MAP, mapImage,
						BeTopoModuleUtil.DEFAULT_MAP_ICON, lengthUnit, sizeX,
						sizeY, mapWidth, installHeight, null, null);
				return mapNode.getId();
			} else {
				return MapSettingsAction.createMapContainerNode(mapNode,
						mapEnv, DEFAULT_PLAN_TOOL_MAP, (short) 1, lengthUnit,
						mapImage, sizeX, sizeY, mapWidth, installHeight,
						BeTopoModuleUtil.DEFAULT_MAP_ICON, null, null, null);
			}
		}
		return null;
	}

	private void saveSimulatedValue() throws Exception {
		if (null != getDataSource().getId()) {
			this.id = getDataSource().getId();
			updateBo(getDataSource());
		} else {
			// create mapNode only for the first time
			createBo(getDataSource());
		}
	}

	private Long savePlannedSimulatedValues() throws Exception {
		Long newToolMapId = null;
		Double sizeX = null, sizeY = null, mapWidth = null;
		String backgroundImage = null;
		if (getDataSource().getBackgroundType() == PlanToolConfig.BACKGROUND_TYPE_IMAGE) {
			backgroundImage = getDataSource().getBackgroundImg();
			getDataSource().setActualWidth(0);
			String fileName = BeTopoModuleUtil
					.getRealTopoBgImagePath(getDomain().getDomainName())
					+ File.separator + getDataSource().getBackgroundImg();
			ImageIcon image = new ImageIcon(fileName);
			if (null != mapWidth && mapWidth > 0 && null != image) {
				getDataSource()
						.setActualHeight(
								mapWidth * image.getIconHeight()
										/ image.getIconWidth());
			}
		} else if (getDataSource().getBackgroundType() == PlanToolConfig.BACKGROUND_TYPE_NO_IMAGE) {
			sizeX = getDataSource().getActualWidth();
			sizeY = getDataSource().getActualHeight();
			getDataSource().setBackgroundImg(null);
		}

		if (null != getDataSource().getId()) {
			this.id = getDataSource().getId();
			Long oldToolMapId = getDataSource().getPlanToolMapId();
			MapContainerNode mapNode = null;
			if (null != oldToolMapId) {
				mapNode = (MapContainerNode) QueryUtil.findBoById(
						MapNode.class, oldToolMapId);
			}
			if (null == mapNode && !fromTopology) {
				newToolMapId = createPlanToolMap(getDataSource().getMapEnv(),
						getDataSource().getLengthUnit(), backgroundImage,
						sizeX, sizeY, mapWidth, getDataSource()
								.getInstallHeight());
				getDataSource().setPlanToolMapId(newToolMapId);
			}
			updateBo(getDataSource());
		} else {
			if (!fromTopology) {
				// create planned mapNode for the first time
				newToolMapId = createPlanToolMap(getDataSource().getMapEnv(),
						getDataSource().getLengthUnit(), backgroundImage,
						sizeX, sizeY, mapWidth, getDataSource()
								.getInstallHeight());
				getDataSource().setPlanToolMapId(newToolMapId);
			}
			createBo(getDataSource());
		}
		return newToolMapId;
	}

	private void initValues() {
		setSessionDataSource(getPlanToolConfig(domainId));
		prepareDependentObjects();
	}

	public static PlanToolConfig getPlanToolConfig(Long domainId) {
		List<PlanToolConfig> list = QueryUtil.executeQuery(
				PlanToolConfig.class, null, null, domainId);
		if (list.isEmpty()) {
			return new PlanToolConfig();
		} else {
			return list.get(0);
		}
	}

	public static void savePlanToolConfig(PlanToolConfig planToolConfig,
			HmDomain domain) throws Exception {
		if (null == planToolConfig.getId()) {
			planToolConfig.setOwner(domain);
			QueryUtil.createBo(planToolConfig);
		} else {
			QueryUtil.updateBo(planToolConfig);
		}
	}

	private void prepareDependentObjects() {
		if (fromTopology) {
			mapConfigSectionStyle = "none";
		} else if (getDataSource().getPlanToolMapId() != null) {
			MapContainerNode mapNode = (MapContainerNode) QueryUtil.findBoById(
					MapNode.class, getDataSource().getPlanToolMapId());
			if (null != mapNode) {
				mapConfigSectionStyle = "none";
			}
		}
	}

	private JSONObject prepareChannels() throws JSONException {
		JSONObject jsonObject = new JSONObject();
		if (null != getDataSource()) {
			int countryCode = getDataSource().getCountryCode();
			short channelWidth = getDataSource().getChannelWidth();
			EnumItem[] wifi0Channels = getWifi0ChannelItems(countryCode,
					channelWidth);
			EnumItem[] wifi1Channels = getWifi1ChannelItems(countryCode,
					channelWidth, getDataSource().getDefaultApType());
			JSONArray channel0 = new JSONArray();
			JSONArray channel1 = new JSONArray();
			for (EnumItem wifi0Channel : wifi0Channels) {
				JSONObject json = new JSONObject();
				json.put("text", wifi0Channel.getValue());
				json.put("value", wifi0Channel.getKey());
				channel0.put(json);
			}
			for (EnumItem wifi1Channel : wifi1Channels) {
				JSONObject json = new JSONObject();
				json.put("text", wifi1Channel.getValue());
				json.put("value", wifi1Channel.getKey());
				channel1.put(json);
			}
			jsonObject.put("wifi0", channel0);
			jsonObject.put("wifi1", channel1);
		}
		return jsonObject;
	}

	public static EnumItem[] getWifi0ChannelItems(int countryCode,
			short channelWidth) {
		// FIXME current only use width20 for 2.4GHz for planner
		int[] channelList = CountryCode.getChannelList_2_4GHz(countryCode,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20);
		return MgrUtil.enumItems("enum.interface.channel.", channelList, 0);
	}

	public static EnumItem[] getWifi1ChannelItems(int countryCode,
			short channelWidth, short hiveApModel) {
		int[] channelList = CountryCode.getChannelList_5GHz(countryCode,
				channelWidth, false, false, hiveApModel);
		return MgrUtil.enumItems("enum.interface.channel.", channelList, 0);
	}

	public static EnumItem[] getEnumPowerType() {
		return MgrUtil.enumItems("enum.interface.power.", new int[] {
				AhInterface.POWER_1, AhInterface.POWER_2, AhInterface.POWER_3,
				AhInterface.POWER_4, AhInterface.POWER_5, AhInterface.POWER_6,
				AhInterface.POWER_7, AhInterface.POWER_8, AhInterface.POWER_9,
				AhInterface.POWER_10, AhInterface.POWER_11,
				AhInterface.POWER_12, AhInterface.POWER_13,
				AhInterface.POWER_14, AhInterface.POWER_15,
				AhInterface.POWER_16, AhInterface.POWER_17,
				AhInterface.POWER_18, AhInterface.POWER_19,
				AhInterface.POWER_20 });
	}

	public String getBackgroundImageStyle() {
		if (null != getDataSource()
				&& getDataSource().getBackgroundType() == PlanToolConfig.BACKGROUND_TYPE_IMAGE) {
			return "";
		}
		return "none";
	}

	public String getBackgroundNoImageStyle() {
		if (null != getDataSource()
				&& getDataSource().getBackgroundType() == PlanToolConfig.BACKGROUND_TYPE_NO_IMAGE) {
			return "";
		}
		return "none";
	}

	public EnumItem[] getEnumHiveApType() {
		return NmsUtil.filterHiveAPModelPlanning(HiveAp.HIVEAP_MODEL_PLANNING,
				this.isEasyMode());
	}

	public static EnumItem[] getEnumChannelWidth() {
		// return RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH;
		return MgrUtil.enumItems("enum.radioProfileChannelWidth.", new int[] {
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40A,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_40B,
				RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_80 });
	}

	private static final List<CheckItem> opacityValues;

	static {
		opacityValues = new ArrayList<CheckItem>();
		for (int ol = 10; ol <= 100; ol += 10) {
			CheckItem item = new CheckItem((long) ol, ol + "");
			opacityValues.add(item);
		}
	}

	public static List<CheckItem> getOpacityValues() {
		return opacityValues;
	}

	public static final EnumItem[] fadeMarginEnumItems;

	static {
		int[] margins = new int[] { 5, 10, 15, 20 };
		fadeMarginEnumItems = new EnumItem[margins.length];
		for (int i = 0; i < margins.length; i++) {
			fadeMarginEnumItems[i] = new EnumItem(margins[i], -95 + margins[i]
					+ "");
		}
	}

	public static EnumItem[] getEnumFadeMargin() {
		return fadeMarginEnumItems;
	}

	public long getImageMaxSize() {
		return BeTopoModuleUtil.IMAGE_MAX_SIZE;
	}

	public String getWebAppHttpUrl() {
		return NmsUtil.getWebAppHttpUrl(request);
	}

	public EnumItem[] getEnumMapEnv() {
		EnumItem[] envs = EnumConstUtil.ENUM_MAP_ENV;
		List<EnumItem> envs2 = new ArrayList<EnumItem>();
		for (EnumItem env : envs) {
			if (env.getKey() == EnumConstUtil.MAP_ENV_AUTO) {
				continue;
			}
			envs2.add(env);
		}
		return envs2.toArray(new EnumItem[envs2.size()]);
	}

	public List<Entry<Integer, String>> getCountryCodeValues() {
		return CountryCode.getCountryCodeList();
	}

	public EnumItem[] getEnumChannelWifi0Type() {
		int countryCode = getDataSource().getCountryCode();
		return getWifi0ChannelItems(countryCode, getDataSource()
				.getChannelWidth());
	}

	public EnumItem[] getEnumChannelWifi1Type() {
		int countryCode = getDataSource().getCountryCode();
		return getWifi1ChannelItems(countryCode, getDataSource()
				.getChannelWidth(), getDataSource().getDefaultApType());
	}

	public EnumItem[] getMapUnits() {
		return MapContainerNode.LENGTH_UNITS;
	}

	public EnumItem[] getRadios() {
		return PlanToolConfig.RADIOS;
	}

	public List<String> getBackgroundImages() {
		return BeTopoModuleUtil
				.getBackgroundImages(getDomain().getDomainName());
	}

	public EnumItem[] getBackgroundType0() {
		return new EnumItem[] { new EnumItem(
				PlanToolConfig.BACKGROUND_TYPE_IMAGE,
				getText("hm.planning.config.background.withmap")) };
	}

	public EnumItem[] getBackgroundType1() {
		return new EnumItem[] { new EnumItem(
				PlanToolConfig.BACKGROUND_TYPE_NO_IMAGE,
				getText("hm.planning.config.background.withoutmap")) };
	}

	public List<TextItem> getMapReviewImages() {
		List<TextItem> list = new ArrayList<TextItem>();
		List<String> images = BeTopoModuleUtil.getBackgroundImages(getDomain()
				.getDomainName());
		if (null != images && !images.isEmpty()) {
			for (String image : images) {
				TextItem item = new TextItem(
						MapsAction.getBackgroundPathByDomain(request,
								getDomain().getDomainName()) + image, image);
				list.add(item);
			}
		}
		return list;
	}

	private String mapConfigSectionStyle = ""; // by default

	private boolean plannedMap = true;

	private boolean fromTopology; // by default;

	private short radio;

	/** file object */
	private File Filedata;

	/** file name */
	private String FiledataFileName;

	/** file content type */
	// private String FiledataContentType;

	private File imagedata;

	private String imagedataFileName;

	/**
	 * @param filedata
	 *            the filedata to set
	 */
	public void setFiledata(File filedata) {
		Filedata = filedata;
	}

	/**
	 * @param filedataFileName
	 *            the filedataFileName to set
	 */
	public void setFiledataFileName(String filedataFileName) {
		FiledataFileName = filedataFileName;
	}

	/*
	 * @param filedataContentType the filedataContentType to set
	 */
	// public void setFiledataContentType(String filedataContentType) {
	// FiledataContentType = filedataContentType;
	// }
	public void setImagedata(File imagedata) {
		this.imagedata = imagedata;
	}

	public void setImagedataFileName(String imagedataFileName) {
		this.imagedataFileName = imagedataFileName;
	}

	public void setRadio(short radio) {
		this.radio = radio;
	}

	public short getRadio() {
		return radio;
	}

	public String getMapConfigSectionStyle() {
		return mapConfigSectionStyle;
	}

	public void setPlannedMap(boolean plannedMap) {
		this.plannedMap = plannedMap;
	}

	public boolean isPlannedMap() {
		return plannedMap;
	}

	public String getPlannedMapStyle() {
		return plannedMap ? "" : "none";
	}

	public boolean isFromTopology() {
		return fromTopology;
	}

	public void setFromTopology(boolean fromTopology) {
		this.fromTopology = fromTopology;
	}
}