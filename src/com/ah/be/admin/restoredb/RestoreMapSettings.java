package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.List;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.PlanToolConfig;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.util.EnumConstUtil;

public class RestoreMapSettings {

	public static boolean restoreMapSettings() {
		boolean restoreMapSettings = true, restorePlannedConfigs = true;
		try {
			List<MapSettings> settings = getAllMapSettings();
			if (null != settings && !settings.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(settings);
			}
			BeLogTools.debug(HmLogConst.M_RESTORE,
					"Restore MapSettings finished.");
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"Restore MapSettings catch exception ", e);
			restoreMapSettings = false;
		}
		try {
			List<PlanToolConfig> plannedConfigs = getAllPlannedMapSettings();
			if (null != plannedConfigs && !plannedConfigs.isEmpty()) {
				QueryUtil.restoreBulkCreateBos(plannedConfigs);
			}
			BeLogTools.debug(HmLogConst.M_RESTORE,
					"Restore Planned Configs finished.");
		} catch (Exception e) {
			BeLogTools.restoreLog(BeLogTools.ERROR,
					"Restore Planned Configs catch exception ", e);
			restorePlannedConfigs = false;
		}
		return restorePlannedConfigs && restoreMapSettings;
	}

	private static List<MapSettings> getAllMapSettings()
			throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of map_settings.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("map_settings");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read map_settings.xml file.");
			return null;
		}

		/**
		 * No one row data stored in map_settings table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<MapSettings> settings = new ArrayList<MapSettings>();

		boolean isColPresent;
		String colName;
		MapSettings setting;
		for (int i = 0; i < rowCount; i++) {
			try {
				setting = new MapSettings();

				/**
				 * Set pollinginterval
				 */
				colName = "pollinginterval";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int pollinginterval = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_POLLING_INTERVAL);
				setting.setPollingInterval(pollinginterval);

				/**
				 * Set bgmapopacity
				 */
				colName = "bgmapopacity";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int bgmapopacity = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_BGMAP_OPACITY);
				setting.setBgMapOpacity(bgmapopacity);

				/**
				 * Set summaryflag
				 */
				colName = "summaryflag";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String summaryflag = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				setting.setSummaryFlag(AhRestoreCommons
						.convertStringToBoolean(summaryflag));

				/**
				 * Set neighborrssiflag
				 */
				colName = "neighborrssiflag";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String neighborrssiflag = (isColPresent ? xmlParser.getColVal(
						i, colName) : "false");
				setting.setNeighborRssiFlag(AhRestoreCommons
						.convertStringToBoolean(neighborrssiflag));

				/**
				 * Set onhoverflag
				 */
				colName = "onhoverflag";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String onhoverflag = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				setting.setOnHoverFlag(AhRestoreCommons
						.convertStringToBoolean(onhoverflag));

				/**
				 * Set calibrateheatmap
				 */
				colName = "calibrateheatmap";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String calibrateheatmap = (isColPresent ? xmlParser.getColVal(
						i, colName) : "true");
				setting.setCalibrateHeatmap(AhRestoreCommons
						.convertStringToBoolean(calibrateheatmap));

				/**
				 * Set useheatmap
				 */
				colName = "useheatmap";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String useheatmap = (isColPresent ? xmlParser.getColVal(i,
						colName) : "false");
				setting.setUseHeatmap(AhRestoreCommons
						.convertStringToBoolean(useheatmap));

				/**
				 * Set perival
				 */
				colName = "perival";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String perival = (isColPresent ? xmlParser
						.getColVal(i, colName) : "false");
				setting.setPeriVal(AhRestoreCommons
						.convertStringToBoolean(perival));

				/**
				 * Set usesurveyerp
				 */
				colName = "usesurveyerp";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String usesurveyerp = (isColPresent ? xmlParser.getColVal(i,
						colName) : "true");
				setting.setUseSurveyErp(AhRestoreCommons
						.convertStringToBoolean(usesurveyerp));

				/**
				 * Set surveyerp
				 */
				colName = "surveyerp";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String surveyerp = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(MapSettings.DEFAULT_SURVEY_ERP));
				setting.setSurveyErp(AhRestoreCommons.convertDouble(surveyerp));

				/**
				 * Set rssifrom
				 */
				colName = "rssifrom";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int rssifrom = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_RSSI_FROM);
				setting.setRssiFrom(rssifrom);

				/**
				 * Set rssiuntil
				 */
				colName = "rssiuntil";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int rssiuntil = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_RSSI_UNTIL);
				setting.setRssiUntil(rssiuntil);

				/**
				 * Set clientrssithreshold
				 */
				colName = "clientrssithreshold";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int clientrssithreshold = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_CLIENT_RSSI_THRESHOLD);
				setting.setClientRssiThreshold(clientrssithreshold);

				/**
				 * Set heatmapresolution
				 */
				colName = "heatmapresolution";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int heatmapresolution = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.HEATMAP_RESOLUTION_AUTO);
				setting.setHeatmapResolution(heatmapresolution);

				/**
				 * Set heatmapopacity
				 */
				colName = "heatmapopacity";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int heatmapopacity = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_HEATMAP_OPACITY);
				setting.setHeatMapOpacity(heatmapopacity);

				/**
				 * Set wallsOpacity
				 */
				colName = "wallsOpacity";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String wallsOpacity = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(MapSettings.DEFAULT_WALLS_OPACITY));
				setting.setWallsOpacity(AhRestoreCommons
						.convertInt(wallsOpacity));

				/**
				 * Set minrssicount
				 */
				colName = "minrssicount";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int minrssicount = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_MIN_RSSI);
				setting.setMinRssiCount(minrssicount);

				/**
				 * Set realTime
				 */
				colName = "realTime";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				String realTime = (isColPresent ? xmlParser.getColVal(i,
						colName) : "true");
				setting.setRealTime(AhRestoreCommons
						.convertStringToBoolean(realTime));

				/**
				 * Set locationWindow
				 */
				colName = "locationWindow";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int locationWindow = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: MapSettings.DEFAULT_LOCATION_WINDOW);
				setting.setLocationWindow(locationWindow);

				/**
				 * Set useStreetMaps
				 */
				colName = "useStreetMaps";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				int useStreetMaps = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: 0);
				setting.setUseStreetMapsForRestore(useStreetMaps);
				
				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"map_settings", colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'map_settings' data be lost, cause: 'owner' column is not available.");
															continue;
				}

				setting.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				settings.add(setting);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get mapSettings", e);
			}
		}

		return settings.size() > 0 ? settings : null;
	}

	private static List<PlanToolConfig> getAllPlannedMapSettings()
			throws AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of plan_tool.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("plan_tool");
		if (!restoreRet) {
			AhRestoreDBTools
					.logRestoreMsg("SAXReader cannot read plan_tool.xml file.");
			return null;
		}

		/**
		 * No one row data stored in plan_tool table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<PlanToolConfig> settings = new ArrayList<PlanToolConfig>();

		boolean isColPresent;
		String colName;
		PlanToolConfig setting;
		for (int i = 0; i < rowCount; i++) {
			try {
				setting = new PlanToolConfig();

				/**
				 * Set pollinginterval
				 */
				colName = "actualheight";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				double actualheight = (isColPresent ? AhRestoreCommons
						.convertDouble(xmlParser.getColVal(i, colName)) : 0);
				setting.setActualHeight(actualheight);

				/**
				 * Set actualwidth
				 */
				colName = "actualwidth";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				double actualwidth = (isColPresent ? AhRestoreCommons
						.convertDouble(xmlParser.getColVal(i, colName)) : 0);
				setting.setActualWidth(actualwidth);

				/**
				 * Set installheight
				 */
				colName = "installheight";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				double installheight = (isColPresent ? AhRestoreCommons
						.convertDouble(xmlParser.getColVal(i, colName)) : 0);
				setting.setInstallHeight(installheight);

				/**
				 * Set backgroundimg
				 */
				colName = "backgroundimg";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String backgroundimg = (isColPresent ? xmlParser.getColVal(i,
						colName) : "");
				setting.setBackgroundImg(backgroundimg);

				/**
				 * Set backgroundtype
				 */
				colName = "backgroundtype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String backgroundtype = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(PlanToolConfig.BACKGROUND_TYPE_IMAGE));
				setting.setBackgroundType((short) AhRestoreCommons
						.convertInt(backgroundtype));

				/**
				 * Set countrycode
				 */
				colName = "countrycode";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String countrycode = (isColPresent ? xmlParser.getColVal(i,
						colName) : "840");
				setting
						.setCountryCode(AhRestoreCommons
								.convertInt(countrycode));

				/**
				 * Set rssithreshold
				 */
				/*-
				colName = "rssithreshold";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String rssithreshold = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(PlanToolConfig.DEFAULT_RSSI_THRESHOLD));
				setting.setRssiThreshold(AhRestoreCommons
						.convertInt(rssithreshold));
				 */

				/**
				 * Set defaultaptype
				 */
				colName = "defaultaptype";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String defaultaptype = (isColPresent ? xmlParser.getColVal(i,
						colName) : String.valueOf(HiveAp.HIVEAP_MODEL_340));
				setting.setDefaultApType((short) AhRestoreCommons
						.convertInt(defaultaptype));

				/**
				 * Set lengthunit
				 */
				colName = "lengthunit";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String lengthunit = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(MapContainerNode.LENGTH_UNIT_FEET));
				setting.setLengthUnit((short) AhRestoreCommons
						.convertInt(lengthunit));

				/**
				 * Set mapenv
				 */
				colName = "mapenv";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String mapenv = (isColPresent ? xmlParser.getColVal(i, colName)
						: String.valueOf(EnumConstUtil.MAP_ENV_OUTDOOR_FREE_SPACE));
				setting.setMapEnv((short) AhRestoreCommons.convertInt(mapenv));

				/**
				 * Set bgmapopacity
				 */
				colName = "bgmapopacity";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String bgmapopacity = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(MapSettings.DEFAULT_BGMAP_OPACITY));
				setting.setBgMapOpacity(AhRestoreCommons
						.convertInt(bgmapopacity));

				/**
				 * Set heatmapopacity
				 */
				colName = "heatmapopacity";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String heatmapopacity = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(MapSettings.DEFAULT_HEATMAP_OPACITY));
				setting.setHeatMapOpacity(AhRestoreCommons
						.convertInt(heatmapopacity));

				/**
				 * Set wallsOpacity
				 */
				colName = "wallsOpacity";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallsOpacity = (isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(MapSettings.DEFAULT_WALLS_OPACITY));
				setting.setWallsOpacity(AhRestoreCommons
						.convertInt(wallsOpacity));

				/**
				 * Set wallcolorbookshelf
				 */
				colName = "wallcolorbookshelf";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorbookshelf = (isColPresent ? xmlParser
						.getColVal(i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_Bookshelf);
				setting.setWallColorBookshelf(wallcolorbookshelf);

				/**
				 * Set wallcolorbrickwall
				 */
				colName = "wallcolorbrickwall";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorbrickwall = (isColPresent ? xmlParser
						.getColVal(i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_BrickWall);
				setting.setWallColorBrickWall(wallcolorbrickwall);

				/**
				 * Set wallcolorconcrete
				 */
				colName = "wallcolorconcrete";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorconcrete = (isColPresent ? xmlParser.getColVal(
						i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_Concrete);
				setting.setWallColorConcrete(wallcolorconcrete);

				/**
				 * Set wallcolorcubicle
				 */
				colName = "wallcolorcubicle";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorcubicle = (isColPresent ? xmlParser.getColVal(
						i, colName) : PlanToolConfig.DEFAULT_WALL_COLOR_Cubicle);
				setting.setWallColorCubicle(wallcolorcubicle);

				/**
				 * Set wallcolordrywall
				 */
				colName = "wallcolordrywall";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolordrywall = (isColPresent ? xmlParser.getColVal(
						i, colName) : PlanToolConfig.DEFAULT_WALL_COLOR_DryWall);
				setting.setWallColorDryWall(wallcolordrywall);

				/**
				 * Set wallcolorelevatorshaft
				 */
				colName = "wallcolorelevatorshaft";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorelevatorshaft = (isColPresent ? xmlParser
						.getColVal(i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_ElevatorShaft);
				setting.setWallColorElevatorShaft(wallcolorelevatorshaft);

				/**
				 * Set wallcolorthickdoor
				 */
				colName = "wallcolorthickdoor";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorthickdoor = (isColPresent ? xmlParser
						.getColVal(i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_ThickDoor);
				setting.setWallColorThickDoor(wallcolorthickdoor);

				/**
				 * Set wallcolorthickwindow
				 */
				colName = "wallcolorthickwindow";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorthickwindow = (isColPresent ? xmlParser
						.getColVal(i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_ThickWindow);
				setting.setWallColorThickWindow(wallcolorthickwindow);

				/**
				 * Set wallcolorthindoor
				 */
				colName = "wallcolorthindoor";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorthindoor = (isColPresent ? xmlParser.getColVal(
						i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_ThinDoor);
				setting.setWallColorThinDoor(wallcolorthindoor);

				/**
				 * Set wallcolorthinwindow
				 */
				colName = "wallcolorthinwindow";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wallcolorthinwindow = (isColPresent ? xmlParser
						.getColVal(i, colName)
						: PlanToolConfig.DEFAULT_WALL_COLOR_ThinWindow);
				setting.setWallColorThinWindow(wallcolorthinwindow);

				/**
				 * Set walltypebookshelf
				 */
				colName = "walltypebookshelf";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypebookshelf = isColPresent ? xmlParser.getColVal(
						i, colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeBookshelf(walltypebookshelf.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypebrickwall
				 */
				colName = "walltypebrickwall";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypebrickwall = isColPresent ? xmlParser.getColVal(
						i, colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED);
				setting.setWallTypeBrickWall(walltypebrickwall.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypeconcrete
				 */
				colName = "walltypeconcrete";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypeconcrete = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeConcrete(walltypeconcrete.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypecubicle
				 */
				colName = "walltypecubicle";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypecubicle = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeCubicle(walltypecubicle.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypedrywall
				 */
				colName = "walltypedrywall";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypedrywall = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeDryWall(walltypedrywall.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypeelevatorshaft
				 */
				colName = "walltypeelevatorshaft";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypeelevatorshaft = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting
						.setWallTypeElevatorShaft(walltypeelevatorshaft.equals(String
								.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypethickdoor
				 */
				colName = "walltypethickdoor";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypethickdoor = isColPresent ? xmlParser.getColVal(
						i, colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeThickDoor(walltypethickdoor.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypethickwindow
				 */
				colName = "walltypethickwindow";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypethickwindow = isColPresent ? xmlParser
						.getColVal(i, colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeThickWindow(walltypethickwindow.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypethindoor
				 */
				colName = "walltypethindoor";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypethindoor = isColPresent ? xmlParser.getColVal(i,
						colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED);
				setting.setWallTypeThinDoor(walltypethindoor.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set walltypethinwindow
				 */
				colName = "walltypethinwindow";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String walltypethinwindow = isColPresent ? xmlParser.getColVal(
						i, colName) : String
						.valueOf(PlanToolConfig.WALL_TYPE_SOLID);
				setting.setWallTypeThinWindow(walltypethinwindow.equals(String
						.valueOf(PlanToolConfig.WALL_TYPE_DASHED)));

				/**
				 * Set wifi0channel
				 */
				colName = "wifi0channel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wifi0channel = (isColPresent ? xmlParser.getColVal(i,
						colName) : "0");
				setting.setWifi0Channel(AhRestoreCommons
						.convertInt(wifi0channel));

				/**
				 * Set wifi0enabled
				 */
				colName = "wifi0enabled";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wifi0enabled = (isColPresent ? xmlParser.getColVal(i,
						colName) : "true");
				setting.setWifi0Enabled(AhRestoreCommons
						.convertStringToBoolean(wifi0enabled));

				/**
				 * Set wifi0power
				 */
				colName = "wifi0power";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				int wifi0power = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: AhInterface.POWER_20);
				setting.setWifi0Power(wifi0power);

				/**
				 * Set wifi1channel
				 */
				colName = "wifi1channel";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wifi1channel = (isColPresent ? xmlParser.getColVal(i,
						colName) : "0");
				setting.setWifi1Channel(AhRestoreCommons
						.convertInt(wifi1channel));

				/**
				 * Set wifi1power
				 */
				colName = "wifi1power";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				int wifi1power = (isColPresent ? AhRestoreCommons
						.convertInt(xmlParser.getColVal(i, colName))
						: AhInterface.POWER_20);
				setting.setWifi1Power(wifi1power);

				/**
				 * Set wifi1enabled
				 */
				colName = "wifi1enabled";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String wifi1enabled = (isColPresent ? xmlParser.getColVal(i,
						colName) : "true");
				setting.setWifi1Enabled(AhRestoreCommons
						.convertStringToBoolean(wifi1enabled));

				/**
				 * Set plantoolmapid
				 */
				colName = "plantoolmapid";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				String plantoolmapid = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
						colName)) : "";
				if (!"".equals(plantoolmapid)) {
					Long plantoolmapid_new = AhRestoreNewMapTools
							.getMapMapContainer(AhRestoreCommons.convertLong(plantoolmapid));
					if (null != plantoolmapid_new) {
						setting.setPlanToolMapId(plantoolmapid_new);
					} else {
						AhRestoreDBTools
								.logRestoreMsg("Cound not find the new map container id mapping to old id:"
										+ plantoolmapid);
					}
				}
				/**
				 * Set owner
				 */
				colName = "owner";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"plan_tool", colName);
				long ownerId = isColPresent ? AhRestoreCommons
						.convertLong(xmlParser.getColVal(i, colName)) : 1;

				if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
				{
					continue;
				}

				setting.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

				settings.add(setting);
			} catch (Exception e) {
				AhRestoreDBTools.logRestoreMsg("get planned map settings", e);
			}
		}

		return settings.size() > 0 ? settings : null;
	}

}