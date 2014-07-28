package com.ah.bo.monitor;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;

@Entity
@Table(name = "MAP_SETTINGS")
public class MapSettings implements HmBo {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_POLLING_INTERVAL = 120; // seconds

	public static final double DEFAULT_SURVEY_ERP = 10; // dBm

	public static final int DEFAULT_MIN_RSSI = 3;

	public static final int DEFAULT_LOCATION_WINDOW = 5; // minutes

	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false, unique = true)
	private HmDomain owner;

	@Version
	private Timestamp version;

	private int pollingInterval = DEFAULT_POLLING_INTERVAL;

	private boolean summaryFlag;

	private boolean neighborRssiFlag = true;

	private boolean onHoverFlag;

	private boolean calibrateHeatmap = true;

	private boolean useHeatmap;

	private boolean periVal;

	private boolean useSurveyErp = true;

	private double surveyErp = DEFAULT_SURVEY_ERP;

	public static final int DEFAULT_RSSI_FROM = -75;

	public static final int DEFAULT_RSSI_UNTIL = -45;

	public static final int DEFAULT_CLIENT_RSSI_THRESHOLD = -75;

	private int rssiFrom = DEFAULT_RSSI_FROM;

	private int rssiUntil = DEFAULT_RSSI_UNTIL;

	private int clientRssiThreshold = DEFAULT_CLIENT_RSSI_THRESHOLD;

	public static final int HEATMAP_RESOLUTION_AUTO = 1;

	public static final int HEATMAP_RESOLUTION_LOW = 2;

	public static final int HEATMAP_RESOLUTION_MEDIUM = 3;

	public static final int HEATMAP_RESOLUTION_HIGH = 4;

	private int heatmapResolution = HEATMAP_RESOLUTION_AUTO;

	private int minRssiCount = DEFAULT_MIN_RSSI;

	private boolean realTime = true;

	private int locationWindow = DEFAULT_LOCATION_WINDOW;

	public static final int DEFAULT_BGMAP_OPACITY = 100;

	public static final int DEFAULT_HEATMAP_OPACITY = 90;

	public static final int DEFAULT_WALLS_OPACITY = 100;

	private int bgMapOpacity = DEFAULT_BGMAP_OPACITY;

	private int heatMapOpacity = DEFAULT_HEATMAP_OPACITY;

	private int wallsOpacity = DEFAULT_WALLS_OPACITY;

	public enum TriState {
		None, True, False
	}

	private TriState useStreetMaps = TriState.None;

	public boolean isUseStreetMaps() {
		if (useStreetMaps == TriState.False) {
			return false;
		} else {
			return useStreetMaps == TriState.True
					|| NmsUtil.isHostedHMApplication();
		}
	}

	public void setUseStreetMaps(boolean useStreetMaps) {
		this.useStreetMaps = useStreetMaps ? TriState.True : TriState.False;
	}

	public void setUseStreetMapsForRestore(int value) {
		if (value == 1) {
			this.useStreetMaps = TriState.True;
		} else if (value == 2) {
			this.useStreetMaps = TriState.False;
		} else {
			this.useStreetMaps = TriState.None;
		}
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getWallsOpacity() {
		return wallsOpacity;
	}

	public void setWallsOpacity(int wallsOpacity) {
		this.wallsOpacity = wallsOpacity;
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

	public boolean isRealTime() {
		return realTime;
	}

	public void setRealTime(boolean realTime) {
		this.realTime = realTime;
	}

	public int getLocationWindow() {
		return locationWindow;
	}

	public void setLocationWindow(int locationWindow) {
		this.locationWindow = locationWindow;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Timestamp getVersion() {
		return version;
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

	@Override
	public String getLabel() {
		return "";
	}

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public int getPollingInterval() {
		return pollingInterval;
	}

	public void setPollingInterval(int pollingInterval) {
		this.pollingInterval = pollingInterval;
	}

	public boolean isSummaryFlag() {
		return summaryFlag;
	}

	public void setSummaryFlag(boolean summaryFlag) {
		this.summaryFlag = summaryFlag;
	}

	public boolean isNeighborRssiFlag() {
		return neighborRssiFlag;
	}

	public void setNeighborRssiFlag(boolean neighborRssiFlag) {
		this.neighborRssiFlag = neighborRssiFlag;
	}

	public boolean isOnHoverFlag() {
		return onHoverFlag;
	}

	public void setOnHoverFlag(boolean onHoverFlag) {
		this.onHoverFlag = onHoverFlag;
	}

	public boolean isCalibrateHeatmap() {
		return calibrateHeatmap;
	}

	public void setCalibrateHeatmap(boolean calibrateHeatmap) {
		this.calibrateHeatmap = calibrateHeatmap;
	}

	public boolean isUseHeatmap() {
		return useHeatmap;
	}

	public void setUseHeatmap(boolean useHeatmap) {
		this.useHeatmap = useHeatmap;
	}

	public int getHeatmapResolution() {
		return heatmapResolution;
	}

	public void setHeatmapResolution(int heatmapResolution) {
		this.heatmapResolution = heatmapResolution;
	}

	public boolean isPeriVal() {
		return periVal;
	}

	public void setPeriVal(boolean periVal) {
		this.periVal = periVal;
	}

	public double getSurveyErp() {
		return surveyErp;
	}

	public void setSurveyErp(double surveyErp) {
		this.surveyErp = surveyErp;
	}

	public int getRssiFrom() {
		return rssiFrom;
	}

	public void setRssiFrom(int rssiFrom) {
		this.rssiFrom = rssiFrom;
	}

	public int getRssiUntil() {
		return rssiUntil;
	}

	public void setRssiUntil(int rssiUntil) {
		this.rssiUntil = rssiUntil;
	}

	public int getClientRssiThreshold() {
		return clientRssiThreshold;
	}

	public void setClientRssiThreshold(int clientRssiThreshold) {
		this.clientRssiThreshold = clientRssiThreshold;
	}

	public boolean isUseSurveyErp() {
		return useSurveyErp;
	}

	public void setUseSurveyErp(boolean useSurveyErp) {
		this.useSurveyErp = useSurveyErp;
	}

	public int getMinRssiCount() {
		return minRssiCount;
	}

	public void setMinRssiCount(int minRssiCount) {
		this.minRssiCount = minRssiCount;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

}