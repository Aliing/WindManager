package com.ah.ui.actions.tools;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ah.bo.performance.AhCustomReport;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.CheckItem;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

@SuppressWarnings("serial")
public class RTAction extends BaseAction {

	private static final Tracer log = new Tracer(RTAction.class.getSimpleName());

	public String execute() throws Exception {
		try {
			if ("fft".equals(operation)) {
				log.info("Fetch FFT data, sample ID: "
						+ dashboardStatus.sampleId);

				// test
				jsonObject = new JSONObject();
				// if aid < 0, this will stop the client polling
				jsonObject.put("aid", dashboardStatus.aid);
				if (dashboardStatus.aid > 0) {
					if (dashboardStatus.sampleId < 0) {
						// record ID of first sample
						dashboardStatus.sampleId = 1;
					} else {
						// Is a more recent sample available
						if (newFFTDataAvailable(dashboardStatus.sampleId)) {
							dashboardStatus.sampleId++;
						} else {
							// No new samples available, don't return any data,
							// Wait for next client request
							return "json";
						}
					}
					jsonObject.put("t0", fetchFFTData());
					jsonObject.put("t1", fetchDutyData());
					jsonObject.put("t2", true);
					addSweptFftSample(
							dashboardStatus.sweptFftSamples[dashboardStatus
									.addSweptFftSample()],
							dashboardStatus.sweptFftStartChannel,
							dashboardStatus.sweptFftEndChannel);
				}
				return "json";
			} else if ("sweptFft".equals(operation)) {
				calculateSweptFft();
				return null;
			} else if ("updateFFTBand".equals(operation)) {
				updateDashboardParam(getFftBand());
				jsonObject.put("channels", getFftChannels());
				jsonObject.put("center", getFftCenter());
				jsonObject.put("span", getFftSpan());
				return "json";
			} else if ("updateFFTChannels".equals(operation)) {
				return updateDashboardParam(getFftChannels());
			} else if ("updateFFTCenter".equals(operation)) {
				return updateDashboardParam(getFftCenter());
			} else if ("updateFFTSpan".equals(operation)) {
				return updateDashboardParam(getFftSpan());
			} else if ("updateFFTRefLevel".equals(operation)) {
				return updateDashboardParam(getFftRefLevel());
			} else if ("updateFFTVertScale".equals(operation)) {
				return updateDashboardParam(getFftVertScale());
			} else if ("updateDutyBand".equals(operation)) {
				updateDashboardParam(getDutyBand());
				jsonObject.put("channels", getDutyChannels());
				jsonObject.put("center", getDutyCenter());
				jsonObject.put("span", getDutySpan());
				return "json";
			} else if ("updateDutyChannels".equals(operation)) {
				return updateDashboardParam(getDutyChannels());
			} else if ("updateDutyCenter".equals(operation)) {
				return updateDashboardParam(getDutyCenter());
			} else if ("updateDutySpan".equals(operation)) {
				return updateDashboardParam(getDutySpan());
			} else if ("updateDutyMin".equals(operation)) {
				return updateDashboardParam(getDutyMin());
			} else if ("updateDutyMax".equals(operation)) {
				return updateDashboardParam(getDutyMax());
			} else if ("updateSweptFFTBand".equals(operation)) {
				updateDashboardParam(getSweptFftBand());
				jsonObject.put("channels", getSweptFftChannels());
				jsonObject.put("center", getSweptFftCenter());
				jsonObject.put("span", getSweptFftSpan());
				return "json";
			} else if ("updateSweptFFTChannels".equals(operation)) {
				return updateDashboardParam(getSweptFftChannels());
			} else if ("updateSweptFFTCenter".equals(operation)) {
				return updateDashboardParam(getSweptFftCenter());
			} else if ("updateSweptFFTSpan".equals(operation)) {
				return updateDashboardParam(getSweptFftSpan());
			} else if ("updateSweptFFTSample".equals(operation)) {
				return updateDashboardParam(getSweptFftSample());
			} else if ("view".equals(operation)) {
				log.info_ln("Analyze AP: " + id);
				dashboardStatus = initDashboardStatus(id);
				return "analyzer";
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			log.error("execute", e);
		}
		id = new Long(123);
		log.info_ln("Analyze AP: " + id);
		dashboardStatus = initDashboardStatus(id);
		return SUCCESS;
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SPECTRAL_ANALYSIS);// by default
	}

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonObject != null) {
			return jsonObject.toString();
		} else {
			return "{}";
		}
	}

	/*
	 * Check if more recent FFT data is available
	 */
	private boolean newFFTDataAvailable(long sampleId) throws Exception {
		return true; // Math.random() < 0.5;
	}

	/*
	 * Fetch from cache or from DB, use dashboardStatus as filter
	 */
	private JSONArray fetchFFTData() throws Exception {
		float startChannel = 2402;
		float endChannel = 2492;
		if (dashboardStatus.fftBand == 5150) {
			startChannel = 5160;
			endChannel = 5340;
		} else if (dashboardStatus.fftBand == 5470) {
			startChannel = 5470;
			endChannel = 5725;
		} else if (dashboardStatus.fftBand == 5725) {
			startChannel = 5725;
			endChannel = 5850;
		}
		float step = 1;
		int count = 1;
		if (dashboardStatus.fftBand == 2400) {
			step /= 3;
			count = 3;
		}
		JSONArray series = new JSONArray();
		for (int j = 0; j < count; j++) {
			JSONArray x = new JSONArray();
			JSONArray y = new JSONArray();
			for (float channel = startChannel; channel <= endChannel;) {
				double base = Math.random() * 100;
				for (float i = 0; i < 8 && channel <= endChannel; i += step) {
					x.put(String.format("%1$.3f", channel += step));
					double signal = -Math.random() * 10 - base;
					if (signal < -100) {
						signal = -100;
					}
					y.put(Math.round(signal));
				}
			}
			JSONObject xy_set = new JSONObject();
			xy_set.put("x", x);
			xy_set.put("y", y);
			series.put(xy_set);
		}
		return series;
	}

	private JSONArray fetchDutyData() throws Exception {
		JSONArray x = new JSONArray();
		JSONArray y = new JSONArray();
		int startChannel = 2402;
		int endChannel = 2492;
		if (dashboardStatus.dutyBand == 5150) {
			startChannel = 5160;
			endChannel = 5340;
		} else if (dashboardStatus.dutyBand == 5470) {
			startChannel = 5470;
			endChannel = 5725;
		} else if (dashboardStatus.dutyBand == 5725) {
			startChannel = 5725;
			endChannel = 5850;
		}
		for (int channel = startChannel; channel <= endChannel;) {
			double base = Math.random() * 100;
			for (int i = 0; i < 8 && channel <= endChannel; i++) {
				x.put(channel++);
				double busy = -Math.random() * 10 - base;
				if (busy < -100) {
					busy = -100;
				}
				y.put(-Math.round(busy));
			}
		}
		JSONObject xy_set = new JSONObject();
		xy_set.put("x", x);
		xy_set.put("y", y);
		return new JSONArray().put(xy_set);
	}

	private void calculateSweptFft() throws Exception {
		Date start = new Date();
		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		g2.setStroke(new BasicStroke(1));
		int startChannel = dashboardStatus.sweptFftStartChannel;
		int endChannel = dashboardStatus.sweptFftEndChannel;
		float scale_x = (float) width / (endChannel - startChannel);
		float scale_y = (float) height / dashboardStatus.sweptFftCount;
		int y2 = height;
		for (int sweep = 0; sweep < dashboardStatus.sweptFftIndex; sweep++) {
			int x1 = 0;
			int y1 = height - Math.round((sweep + 1) * scale_y);
			int[] sample = dashboardStatus.sweptFftSamples[dashboardStatus.sweptFftIndex
					- 1 - sweep];
			for (int channel = 0; channel < sample.length; channel++) {
				int rssi = sample[channel];
				if (rssi > -35) {
					rssi = -35;
				}
				int color = (short) (-35 - rssi);
				if (color >= rssiColors.length) {
					color = (short) (rssiColors.length - 1);
				}
				int x2 = channel == sample.length - 1 ? width : Math
						.round((channel + (float) 0.5) * scale_x);
				g2.setColor(rssiColors[color]);
				if (y2 - y1 > 0) {
					g2.fillRect(x1, y1, x2 - x1, y2 - y1);
				}
				x1 = x2;
			}
			y2 = y1;
		}
		Date end = new Date();
		log.info_ln("Swept FFT image(" + width + ", " + height + ") in "
				+ (end.getTime() - start.getTime()) + " ms.");
		response.setContentType("image/png");
		OutputStream os = response.getOutputStream();
		ImageIO.write(image, "png", os);
		os.close();
	}

	private void addSweptFftSample(int[] sample, int startChannel,
			int endChannel) {
		for (int channel = startChannel; channel <= endChannel;) {
			double base = Math.random() * 100;
			for (int i = 0; i < 8 && channel <= endChannel; i++, channel++) {
				double rssi = -Math.random() * 10 - base;
				if (rssi < -100) {
					rssi = -100;
				}
				sample[channel - startChannel] = (int) rssi;
			}
		}
	}

	public List<CheckItem> getHiveAPLst() {
		return hiveAPLst;
	}

	private String updateDashboardParam(int param) throws Exception {
		log.info(operation + ": " + param);
		jsonObject = new JSONObject();
		jsonObject.put("aid", dashboardStatus.aid);
		jsonObject.put("value", param);
		return "json";
	}

	private String tilesStatus;
	private DashboardStatus dashboardStatus;
	private int width, height;

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setAid(int aid) {
		dashboardStatus = getDashboardStatus(aid);
	}

	public int getAid() {
		return dashboardStatus.aid;
	}

	public int getFftBand() {
		return dashboardStatus.fftBand;
	}

	public void setFftBand(int fftBand) {
		dashboardStatus.fftBand = fftBand;
		if (fftBand == 2400) {
			dashboardStatus.fftChannels = 1611;
			dashboardStatus.fftCenter = 2442;
			dashboardStatus.fftSpan = 100;
		} else if (fftBand == 5150) {
			dashboardStatus.fftChannels = 3664;
			dashboardStatus.fftCenter = 5250;
			dashboardStatus.fftSpan = 200;
		} else if (fftBand == 5470) {
			dashboardStatus.fftChannels = 100140;
			dashboardStatus.fftCenter = 5600;
			dashboardStatus.fftSpan = 240;
		} else if (fftBand == 5725) {
			dashboardStatus.fftChannels = 149165;
			dashboardStatus.fftCenter = 5785;
			dashboardStatus.fftSpan = 200;
		}
	}

	public int getFftChannels() {
		return dashboardStatus.fftChannels;
	}

	public void setFftChannels(int fftChannels) {
		dashboardStatus.fftChannels = fftChannels;
	}

	public int getFftCenter() {
		return dashboardStatus.fftCenter;
	}

	public void setFftCenter(int fftCenter) {
		dashboardStatus.fftCenter = fftCenter;
	}

	public int getFftSpan() {
		return dashboardStatus.fftSpan;
	}

	public void setFftSpan(int fftSpan) {
		dashboardStatus.fftSpan = fftSpan;
	}

	public int getFftRefLevel() {
		return dashboardStatus.fftRefLevel;
	}

	public void setFftRefLevel(int fftRefLevel) {
		dashboardStatus.fftRefLevel = fftRefLevel;
	}

	public int getFftVertScale() {
		return dashboardStatus.fftVertScale;
	}

	public void setFftVertScale(int fftVertScale) {
		dashboardStatus.fftVertScale = fftVertScale;
	}

	public int getFftSample() {
		return dashboardStatus.fftSample;
	}

	public void setFftSample(int fftSample) {
		dashboardStatus.fftSample = fftSample;
	}

	public int getDutyBand() {
		return dashboardStatus.dutyBand;
	}

	public void setDutyBand(int dutyBand) {
		dashboardStatus.dutyBand = dutyBand;
		if (dutyBand == 2400) {
			dashboardStatus.dutyChannels = 1611;
			dashboardStatus.dutyCenter = 2442;
			dashboardStatus.dutySpan = 100;
		} else if (dutyBand == 5150) {
			dashboardStatus.dutyChannels = 3664;
			dashboardStatus.dutyCenter = 5250;
			dashboardStatus.dutySpan = 200;
		} else if (dutyBand == 5470) {
			dashboardStatus.dutyChannels = 100140;
			dashboardStatus.dutyCenter = 5600;
			dashboardStatus.dutySpan = 240;
		} else if (dutyBand == 5725) {
			dashboardStatus.dutyChannels = 149165;
			dashboardStatus.dutyCenter = 5785;
			dashboardStatus.dutySpan = 200;
		}
	}

	public int getDutyChannels() {
		return dashboardStatus.dutyChannels;
	}

	public void setDutyChannels(int dutyChannels) {
		dashboardStatus.dutyChannels = dutyChannels;
	}

	public int getDutyCenter() {
		return dashboardStatus.dutyCenter;
	}

	public void setDutyCenter(int dutyCenter) {
		dashboardStatus.dutyCenter = dutyCenter;
	}

	public int getDutySpan() {
		return dashboardStatus.dutySpan;
	}

	public void setDutySpan(int dutySpan) {
		dashboardStatus.dutySpan = dutySpan;
	}

	public int getDutyMin() {
		return dashboardStatus.dutyMin;
	}

	public void setDutyMin(int dutyMin) {
		dashboardStatus.dutyMin = dutyMin;
	}

	public int getDutyMax() {
		return dashboardStatus.dutyMax;
	}

	public void setDutyMax(int dutyMax) {
		dashboardStatus.dutyMax = dutyMax;
	}

	public int getSweptFftBand() {
		return dashboardStatus.sweptFftBand;
	}

	public void setSweptFftBand(int sweptFftBand) {
		dashboardStatus.sweptFftBand = sweptFftBand;
		if (sweptFftBand == 2400) {
			dashboardStatus.sweptFftChannels = 1611;
			dashboardStatus.sweptFftCenter = 2442;
			dashboardStatus.sweptFftSpan = 100;
		} else if (sweptFftBand == 5150) {
			dashboardStatus.sweptFftChannels = 3664;
			dashboardStatus.sweptFftCenter = 5250;
			dashboardStatus.sweptFftSpan = 200;
		} else if (sweptFftBand == 5470) {
			dashboardStatus.sweptFftChannels = 100140;
			dashboardStatus.sweptFftCenter = 5600;
			dashboardStatus.sweptFftSpan = 240;
		} else if (sweptFftBand == 5725) {
			dashboardStatus.sweptFftChannels = 149165;
			dashboardStatus.sweptFftCenter = 5785;
			dashboardStatus.sweptFftSpan = 200;
		}
	}

	public int getSweptFftChannels() {
		return dashboardStatus.sweptFftChannels;
	}

	public void setSweptFftChannels(int sweptFftChannels) {
		dashboardStatus.sweptFftChannels = sweptFftChannels;
	}

	public int getSweptFftCenter() {
		return dashboardStatus.sweptFftCenter;
	}

	public void setSweptFftCenter(int sweptFftCenter) {
		dashboardStatus.sweptFftCenter = sweptFftCenter;
	}

	public int getSweptFftSpan() {
		return dashboardStatus.sweptFftSpan;
	}

	public void setSweptFftSpan(int sweptFftSpan) {
		dashboardStatus.sweptFftSpan = sweptFftSpan;
	}

	public int getSweptFftSample() {
		return dashboardStatus.sweptFftSample;
	}

	public void setSweptFftSample(int sweptFftSample) {
		dashboardStatus.sweptFftSample = sweptFftSample;
	}

	private DashboardStatus initDashboardStatus(Long apId) {
		DashboardSessionMgmt sessionMgmt = (DashboardSessionMgmt) MgrUtil
				.getSessionAttribute(SessionKeys.SPECTRUM_ANALYSIS_DASHBOARD);
		if (sessionMgmt == null) {
			sessionMgmt = new DashboardSessionMgmt();
			MgrUtil.setSessionAttribute(SPECTRUM_ANALYSIS_DASHBOARD,
					sessionMgmt);
		}
		return sessionMgmt.initDashboardStatus(apId);
	}

	private DashboardStatus getDashboardStatus(int aid) {
		DashboardSessionMgmt sessionMgmt = (DashboardSessionMgmt) MgrUtil
				.getSessionAttribute(SessionKeys.SPECTRUM_ANALYSIS_DASHBOARD);
		return sessionMgmt.getDashboardStatus(aid);
	}

	public String getTilesStatus() {
		return tilesStatus;
	}

	public void setTilesStatus(String tilesStatus) {
		this.tilesStatus = tilesStatus;
	}

	private Long runAP;
	private int runInterface = AhCustomReport.REPORT_INTERFACE_BOTH;
	private String runChannelWifi0;
	private String runChannelWifi1;
	private int runInterval = 5;
	private int runTime = 5;
	private Long currentApId;

	private List<CheckItem> hiveAPLst = new ArrayList<CheckItem>();
	private EnumItem[] interfaceLst = null;

	public String getShowWifi0Channel() {
		if (runInterface != AhCustomReport.REPORT_INTERFACE_WIFI1
				&& runInterface != -1) {
			return "";
		}
		return "none";
	}

	public String getShowWifi1Channel() {
		if (runInterface != AhCustomReport.REPORT_INTERFACE_WIFI0
				&& runInterface != -1) {
			return "";
		}
		return "none";
	}

	public Long getCurrentApId() {
		return currentApId;
	}

	public void setCurrentApId(Long currentApId) {
		this.currentApId = currentApId;
	}

	public Long getRunAP() {
		return runAP;
	}

	public void setRunAP(Long runAP) {
		this.runAP = runAP;
	}

	public int getRunInterface() {
		return runInterface;
	}

	public void setRunInterface(int runInterface) {
		this.runInterface = runInterface;
	}

	public int getRunInterval() {
		return runInterval;
	}

	public void setRunInterval(int runInterval) {
		this.runInterval = runInterval;
	}

	public int getRunTime() {
		return runTime;
	}

	public void setRunTime(int runTime) {
		this.runTime = runTime;
	}

	public String getStartSNP() {
		return "startSNP";
	}

	public String getStopSNP() {
		return "stopSNP";
	}

	private static Color[] rssiColors = new Color[] { new Color(128, 0, 0),
			new Color(149, 21, 0), new Color(170, 43, 0),
			new Color(191, 64, 0), new Color(212, 85, 0),
			new Color(234, 107, 0), new Color(255, 128, 0),
			new Color(255, 140, 0), new Color(255, 151, 0),
			new Color(255, 163, 0), new Color(255, 174, 0),
			new Color(255, 186, 0), new Color(255, 197, 0),
			new Color(255, 209, 0), new Color(255, 220, 0),
			new Color(255, 232, 0), new Color(255, 243, 0),
			new Color(255, 255, 0), new Color(212, 255, 11),
			new Color(170, 255, 21), new Color(128, 255, 32),
			new Color(85, 255, 43), new Color(43, 255, 53),
			new Color(0, 255, 64), new Color(0, 255, 88),
			new Color(0, 255, 112), new Color(0, 255, 136),
			new Color(0, 255, 159), new Color(0, 255, 183),
			new Color(0, 255, 207), new Color(0, 255, 231),
			new Color(0, 255, 255), new Color(0, 241, 254),
			new Color(1, 228, 252), new Color(1, 214, 251),
			new Color(1, 200, 249), new Color(2, 187, 248),
			new Color(2, 173, 246), new Color(2, 159, 245),
			new Color(2, 146, 244), new Color(3, 132, 242),
			new Color(3, 118, 241), new Color(6, 109, 227),
			new Color(9, 99, 212), new Color(12, 90, 198),
			new Color(15, 80, 184), new Color(18, 70, 170),
			new Color(21, 61, 156), new Color(24, 51, 141),
			new Color(27, 42, 127), new Color(30, 32, 113),
			new Color(33, 22, 98), new Color(36, 12, 84), new Color(39, 3, 70),
			new Color(42, 0, 56), new Color(45, 0, 42) };

	private class DashboardStatus {
		private DashboardStatus(int aid) {
			this.aid = aid;
			initSweptFft();
		}

		private int aid;
		private long sampleId;
		private int fftBand = 2400, fftChannels = 1611, fftCenter = 2442,
				fftSpan = 100, fftRefLevel = 0, fftVertScale = 10,
				fftSample = 0;
		private int dutyBand = 2400, dutyChannels = 1611, dutyCenter = 2442,
				dutySpan = 100, dutyMin = 0, dutyMax = 100;
		private int sweptFftStartChannel = 2402, sweptFftEndChannel = 2482,
				sweptFftIndex;
		private int sweptFftBand = 2400, sweptFftChannels = 1611,
				sweptFftCenter = 2442, sweptFftSpan = 100, sweptFftSample = 0,
				sweptFftCount = 200;
		private int[][] sweptFftSamples;

		private void initSweptFft() {
			sweptFftSamples = new int[sweptFftCount][sweptFftEndChannel
					- sweptFftStartChannel + 1];
			sweptFftIndex = 0;
		}

		private int addSweptFftSample() {
			if (sweptFftIndex < sweptFftCount) {
				return sweptFftIndex++;
			} else {
				for (int i = 0; i < sweptFftCount - 1; i++) {
					for (int j = 0; j < sweptFftSamples[i].length - 1; j++) {
						sweptFftSamples[i][j] = sweptFftSamples[i + 1][j];
					}
				}
				return sweptFftIndex - 1;
			}
		}
	}

	private class DashboardSessionMgmt {
		private DashboardSessionMgmt() {
			apMap = new HashMap<Long, DashboardStatus>();
			aidMap = new HashMap<Integer, DashboardStatus>();
			aidGen = new AtomicInteger(0);
		}

		private DashboardStatus initDashboardStatus(Long apId) {
			int aid = aidGen.incrementAndGet();
			DashboardStatus status = apMap.get(apId);
			if (status == null) {
				status = new DashboardStatus(aid);
				apMap.put(id, status);
			} else {
				status.aid = aid; // Reuse based on AP id, but update aid
			}
			status.sampleId = -1; // sampleId will be set after first poll
			aidMap.put(aid, status);
			return status;
		}

		private DashboardStatus getDashboardStatus(int aid) {
			DashboardStatus status = aidMap.get(aid);
			if (status != null && status.aid == aid) {
				return status;
			} else { // this one has become invalid.
				return new DashboardStatus(-1);
			}
		}

		Map<Long, DashboardStatus> apMap;
		Map<Integer, DashboardStatus> aidMap;
		AtomicInteger aidGen;
	}

	public String getRunChannelWifi0() {
		return runChannelWifi0;
	}

	public void setRunChannelWifi0(String runChannelWifi0) {
		this.runChannelWifi0 = runChannelWifi0;
	}

	public String getRunChannelWifi1() {
		return runChannelWifi1;
	}

	public void setRunChannelWifi1(String runChannelWifi1) {
		this.runChannelWifi1 = runChannelWifi1;
	}
}
