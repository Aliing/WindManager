package com.ah.bo.admin;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.bo.HmBo;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapSettings;
import com.ah.bo.wlan.RadioProfile;
import com.ah.util.EnumConstUtil;
import com.ah.util.EnumItem;

@Entity
@Table(name = "PLAN_TOOL")
public class PlanToolConfig implements HmBo {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	private int countryCode = 840;

	private short channelWidth = RadioProfile.RADIO_PROFILE_CHANNEL_WIDTH_20;

	private int fadeMargin = 10;

	private short defaultApType = HiveAp.HIVEAP_MODEL_121;

	private int wifi0Channel, wifi0Power = AhInterface.POWER_18, wifi1Channel,
			wifi1Power = AhInterface.POWER_15;

	private boolean wifi0Enabled = true, wifi1Enabled = true;

	private short mapEnv = EnumConstUtil.MAP_ENV_ENTERPRISE;

	private double actualWidth, actualHeight, installHeight = 3;

	private String backgroundImg;

	private short lengthUnit = MapContainerNode.LENGTH_UNIT_METERS;

	public static final int DEFAULT_RSSI_THRESHOLD = 70;

	public static final int DEFAULT_SNR_THRESHOLD = 25;

	private Long planToolMapId;

	public static final short BACKGROUND_TYPE_IMAGE = 1;

	public static final short BACKGROUND_TYPE_NO_IMAGE = 2;

	private short backgroundType = BACKGROUND_TYPE_NO_IMAGE;

	public static final String DEFAULT_WALL_COLOR_Bookshelf = "#C3A480";
	public static final String DEFAULT_WALL_COLOR_Cubicle = "#838383";
	public static final String DEFAULT_WALL_COLOR_DryWall = "#B07842";
	public static final String DEFAULT_WALL_COLOR_BrickWall = "#E1390B";
	public static final String DEFAULT_WALL_COLOR_Concrete = "#A10000";
	public static final String DEFAULT_WALL_COLOR_ElevatorShaft = "#3D3C3C";
	public static final String DEFAULT_WALL_COLOR_ThinDoor = "#DF8909";
	public static final String DEFAULT_WALL_COLOR_ThickDoor = "#A1A10D";
	public static final String DEFAULT_WALL_COLOR_ThinWindow = "#5353F2";
	public static final String DEFAULT_WALL_COLOR_ThickWindow = "#5353F2";

	@Column(length = 16)
	private String wallColorBookshelf = DEFAULT_WALL_COLOR_Bookshelf;
	@Column(length = 16)
	private String wallColorCubicle = DEFAULT_WALL_COLOR_Cubicle;
	@Column(length = 16)
	private String wallColorDryWall = DEFAULT_WALL_COLOR_DryWall;
	@Column(length = 16)
	private String wallColorBrickWall = DEFAULT_WALL_COLOR_BrickWall;
	@Column(length = 16)
	private String wallColorConcrete = DEFAULT_WALL_COLOR_Concrete;
	@Column(length = 16)
	private String wallColorElevatorShaft = DEFAULT_WALL_COLOR_ElevatorShaft;
	@Column(length = 16)
	private String wallColorThinDoor = DEFAULT_WALL_COLOR_ThinDoor;
	@Column(length = 16)
	private String wallColorThickDoor = DEFAULT_WALL_COLOR_ThickDoor;
	@Column(length = 16)
	private String wallColorThinWindow = DEFAULT_WALL_COLOR_ThinWindow;
	@Column(length = 16)
	private String wallColorThickWindow = DEFAULT_WALL_COLOR_ThickWindow;

	public static final short WALL_TYPE_SOLID = 0;
	public static final short WALL_TYPE_DASHED = 1;

	private short wallTypeBookshelf = WALL_TYPE_SOLID;

	private short wallTypeCubicle = WALL_TYPE_DASHED;

	private short wallTypeDryWall = WALL_TYPE_SOLID;

	private short wallTypeBrickWall = WALL_TYPE_SOLID;

	private short wallTypeConcrete = WALL_TYPE_SOLID;

	private short wallTypeElevatorShaft = WALL_TYPE_SOLID;

	private short wallTypeThinDoor = WALL_TYPE_SOLID;

	private short wallTypeThickDoor = WALL_TYPE_SOLID;

	private short wallTypeThinWindow = WALL_TYPE_DASHED;

	private short wallTypeThickWindow = WALL_TYPE_SOLID;

	private int bgMapOpacity = MapSettings.DEFAULT_BGMAP_OPACITY;

	private int heatMapOpacity = MapSettings.DEFAULT_HEATMAP_OPACITY;

	private int wallsOpacity = MapSettings.DEFAULT_WALLS_OPACITY;

	public static final short RADIO_24GHZ = 1;

	public static final short RADIO_5GHZ = 2;

	public static EnumItem[] RADIOS = new EnumItem[] {
			new EnumItem(RADIO_24GHZ, "2.4 GHz"),
			new EnumItem(RADIO_5GHZ, "5 GHz") };

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	@Override
	public Timestamp getVersion() {
		return version;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return "Plan Tool";
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public short getChannelWidth() {
		return channelWidth;
	}

	public void setChannelWidth(short channelWidth) {
		this.channelWidth = channelWidth;
	}

	public int getFadeMargin() {
		return fadeMargin;
	}

	public void setFadeMargin(int fadeMargin) {
		this.fadeMargin = fadeMargin;
	}

	public short getDefaultApType() {
		return defaultApType;
	}

	public void setDefaultApType(short defaultApType) {
		this.defaultApType = defaultApType;
	}

	public int getWifi0Channel() {
		return wifi0Channel;
	}

	public void setWifi0Channel(int wifi0Channel) {
		this.wifi0Channel = wifi0Channel;
	}

	public int getWifi0Power() {
		return wifi0Power;
	}

	public void setWifi0Power(int wifi0Power) {
		this.wifi0Power = wifi0Power;
	}

	public int getWifi1Channel() {
		return wifi1Channel;
	}

	public void setWifi1Channel(int wifi1Channel) {
		this.wifi1Channel = wifi1Channel;
	}

	public int getWifi1Power() {
		return wifi1Power;
	}

	public void setWifi1Power(int wifi1Power) {
		this.wifi1Power = wifi1Power;
	}

	public short getMapEnv() {
		return mapEnv;
	}

	public void setMapEnv(short mapEnv) {
		this.mapEnv = mapEnv;
	}

	public double getActualWidth() {
		return actualWidth;
	}

	@Transient
	public String getActualWidthString() {
		return format(actualWidth);
	}

	public void setActualWidth(double actualWidth) {
		this.actualWidth = actualWidth;
	}

	public double getActualHeight() {
		return actualHeight;
	}

	@Transient
	public String getActualHeightString() {
		return format(actualHeight);
	}

	public void setActualHeight(double actualHeight) {
		this.actualHeight = actualHeight;
	}

	public double getInstallHeight() {
		return installHeight;
	}

	@Transient
	public String getInstallHeightString() {
		return format(installHeight);
	}

	public void setInstallHeight(double installHeight) {
		this.installHeight = installHeight;
	}

	public short getLengthUnit() {
		return lengthUnit;
	}
	
	@Transient
	public short getLengthUnit1() {
	    // handle OGNL error - Invalid field value for field "dataSource.lengthUnit" (since xwork-core-2.3.4)
	    return this.lengthUnit;
	}
	
	public void setLengthUnit(short lengthUnit) {
		this.lengthUnit = lengthUnit;
	}

	public String getBackgroundImg() {
		return backgroundImg;
	}

	public void setBackgroundImg(String backgroundImg) {
		this.backgroundImg = backgroundImg;
	}

	public short getBackgroundType() {
		return backgroundType;
	}

	public void setBackgroundType(short backgroundType) {
		this.backgroundType = backgroundType;
	}

	public String getWallColorBookshelf() {
		return wallColorBookshelf;
	}

	public void setWallColorBookshelf(String wallColorBookshelf) {
		this.wallColorBookshelf = wallColorBookshelf;
	}

	public String getWallColorCubicle() {
		return wallColorCubicle;
	}

	public void setWallColorCubicle(String wallColorCubicle) {
		this.wallColorCubicle = wallColorCubicle;
	}

	public String getWallColorDryWall() {
		return wallColorDryWall;
	}

	public void setWallColorDryWall(String wallColorDryWall) {
		this.wallColorDryWall = wallColorDryWall;
	}

	public String getWallColorBrickWall() {
		return wallColorBrickWall;
	}

	public void setWallColorBrickWall(String wallColorBrickWall) {
		this.wallColorBrickWall = wallColorBrickWall;
	}

	public String getWallColorConcrete() {
		return wallColorConcrete;
	}

	public void setWallColorConcrete(String wallColorConcrete) {
		this.wallColorConcrete = wallColorConcrete;
	}

	public String getWallColorElevatorShaft() {
		return wallColorElevatorShaft;
	}

	public void setWallColorElevatorShaft(String wallColorElevatorShaft) {
		this.wallColorElevatorShaft = wallColorElevatorShaft;
	}

	public String getWallColorThinDoor() {
		return wallColorThinDoor;
	}

	public void setWallColorThinDoor(String wallColorThinDoor) {
		this.wallColorThinDoor = wallColorThinDoor;
	}

	public String getWallColorThickDoor() {
		return wallColorThickDoor;
	}

	public void setWallColorThickDoor(String wallColorThickDoor) {
		this.wallColorThickDoor = wallColorThickDoor;
	}

	public String getWallColorThinWindow() {
		return wallColorThinWindow;
	}

	public void setWallColorThinWindow(String wallColorThinWindow) {
		this.wallColorThinWindow = wallColorThinWindow;
	}

	public String getWallColorThickWindow() {
		return wallColorThickWindow;
	}

	public void setWallColorThickWindow(String wallColorThickWindow) {
		this.wallColorThickWindow = wallColorThickWindow;
	}

	public boolean getWallTypeBookshelf() {
		return wallTypeBookshelf == WALL_TYPE_DASHED;
	}

	public void setWallTypeBookshelf(boolean wallTypeBookshelf) {
		this.wallTypeBookshelf = wallTypeBookshelf ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeCubicle() {
		return wallTypeCubicle == WALL_TYPE_DASHED;
	}

	public void setWallTypeCubicle(boolean wallTypeCubicle) {
		this.wallTypeCubicle = wallTypeCubicle ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeDryWall() {
		return wallTypeDryWall == WALL_TYPE_DASHED;
	}

	public void setWallTypeDryWall(boolean wallTypeDryWall) {
		this.wallTypeDryWall = wallTypeDryWall ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeBrickWall() {
		return wallTypeBrickWall == WALL_TYPE_DASHED;
	}

	public void setWallTypeBrickWall(boolean wallTypeBrickWall) {
		this.wallTypeBrickWall = wallTypeBrickWall ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeConcrete() {
		return wallTypeConcrete == WALL_TYPE_DASHED;
	}

	public void setWallTypeConcrete(boolean wallTypeConcrete) {
		this.wallTypeConcrete = wallTypeConcrete ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeElevatorShaft() {
		return wallTypeElevatorShaft == WALL_TYPE_DASHED;
	}

	public void setWallTypeElevatorShaft(boolean wallTypeElevatorShaft) {
		this.wallTypeElevatorShaft = wallTypeElevatorShaft ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeThinDoor() {
		return wallTypeThinDoor == WALL_TYPE_DASHED;
	}

	public void setWallTypeThinDoor(boolean wallTypeThinDoor) {
		this.wallTypeThinDoor = wallTypeThinDoor ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeThickDoor() {
		return wallTypeThickDoor == WALL_TYPE_DASHED;
	}

	public void setWallTypeThickDoor(boolean wallTypeThickDoor) {
		this.wallTypeThickDoor = wallTypeThickDoor ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeThinWindow() {
		return wallTypeThinWindow == WALL_TYPE_DASHED;
	}

	public void setWallTypeThinWindow(boolean wallTypeThinWindow) {
		this.wallTypeThinWindow = wallTypeThinWindow ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public boolean getWallTypeThickWindow() {
		return wallTypeThickWindow == WALL_TYPE_DASHED;
	}

	public void setWallTypeThickWindow(boolean wallTypeThickWindow) {
		this.wallTypeThickWindow = wallTypeThickWindow ? WALL_TYPE_DASHED
				: WALL_TYPE_SOLID;
	}

	public int getBgMapOpacity() {
		return bgMapOpacity;
	}

	public void setBgMapOpacity(int bgMapOpacity) {
		this.bgMapOpacity = bgMapOpacity;
	}

	public int getHeatMapOpacity() {
		return heatMapOpacity;
	}

	public void setHeatMapOpacity(int heatMapOpacity) {
		this.heatMapOpacity = heatMapOpacity;
	}

	public int getWallsOpacity() {
		return wallsOpacity;
	}

	public void setWallsOpacity(int wallsOpacity) {
		this.wallsOpacity = wallsOpacity;
	}

	public Long getPlanToolMapId() {
		return planToolMapId;
	}

	public void setPlanToolMapId(Long planToolMapId) {
		this.planToolMapId = planToolMapId;
	}

	public boolean isWifi0Enabled() {
		return wifi0Enabled;
	}

	public void setWifi0Enabled(boolean wifi0Enabled) {
		this.wifi0Enabled = wifi0Enabled;
	}

	public boolean isWifi1Enabled() {
		return wifi1Enabled;
	}

	public void setWifi1Enabled(boolean wifi1Enabled) {
		this.wifi1Enabled = wifi1Enabled;
	}

	@Transient
	private boolean selected;

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public static String format(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(value);
	}

}