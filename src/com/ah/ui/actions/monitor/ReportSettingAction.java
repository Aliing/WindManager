package com.ah.ui.actions.monitor;

/* import com.ah.be.common.ConfigUtil; */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.ah.be.app.HmBePerformUtil;
import com.ah.be.db.configuration.ConfigurationProcessor.ConfigurationType;
import com.ah.be.db.configuration.ConfigurationResources;
import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.be.service.ApplicationService;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.LogSettings;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.network.Application;
import com.ah.bo.network.ApplicationDTO;
import com.ah.bo.network.ApplicationProfile;
import com.ah.bo.network.CustomApplication;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.HmContextListener;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.bo.report.ApplicationUtil;

public class ReportSettingAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer	log	= new Tracer(ReportSettingAction.class.getSimpleName());

	private int	maxPerfRecord=500000;

	private int	maxClientHistory=2000000;
	
	private int interfacePollInterval=30;
	
	private int statsStartMinute;
	
	
	private int maxOriginalCount=24;
	private int maxHourValue=2;
	private int maxDayValue=5;
	private int maxWeekValue=12;
	private int slaPeriod=3;
	private int clientPeriod=10;
	private int maxSupportAp=200;
	
	private int reportIntervalMinute = 10;
	private int reportMaxApCount = 20;//for per vhm
	private int reportMaxApForSystem = 100;//for system
	
	private int reportDbHourly = 30000000;
	private int reportDbDaily = 30000000;
	private int reportDbWeekly = 30000000;

	private int selectedAppNum;
	private int unSelectedAppNum;
	private String selectedAppIds;
	private List<Application> selectedAppList;
	private List<Application> unSelectedAppList;
	//Table partition for statistics
	private int intervalTablePartPer=1;
	private int maxTimeTablePerSave=3;
	private int intervalTablePartCli=8;
	private int maxTimeTableCliSave=3;
	
	private File localFile;

	private String localFileContentType;

	private String localFileFileName;
	
	private String previousFilePath;
	
	private boolean logoExsit;
	
	public static final String logoFileName="report-logo.png";
	
	public static final String reportRootDir="domains";

	public String execute() throws Exception {
		try {
			if ("update".equals(operation)) {
				if (updateLogConfig()) {
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.update.report.setting"));
					addActionMessage(MgrUtil.getUserMessage("message.report.updated.success"));
				} else {
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.update.report.setting"));
					addActionError(MgrUtil.getUserMessage("action.error.unable.update.report.setting"));
				}
				initValue();

				return SUCCESS;
				
			}
			else if("deleteLogo".equals(operation))
			{
				// path of up load destination
				String savePath=HmContextListener.context
						.getRealPath(reportRootDir
								+ File.separator
								+ getDomain().getDomainName()
								+ File.separator
								+"vhm-report"
								+ File.separator
								+"images");
				File savePathDir=new File(savePath);
				if(savePathDir.exists())
				{
					File uploadFile=new File(savePath + File.separator + logoFileName);
					if(uploadFile.isFile()&&uploadFile.exists())
					{
						boolean delLogo=removeCurrentDomainFile(savePath);
						if(!delLogo)
						{
							addActionError("Delete report pdf logo failed");
							log.error("Delete report pdf logo failed");
						}
					}
				}
				initValue();
				return SUCCESS;
			}
			else if("uploadLogo".equals(operation))
			{
				if(getLocalFile().length()>1024*1024)
				{
					addActionError("Uploaded logo image size cann't be greater than 1Mb");
					log.error("Uploaded logo image size cann't be greater than 1Mb");
					initValue();
					return SUCCESS;
				}
				// path of up load destination
				String savePath=HmContextListener.context
						.getRealPath(reportRootDir
								+ File.separator
								+ getDomain().getDomainName()
								+ File.separator
								+"vhm-report"
								+ File.separator
								+"images");
				File savePathDir=new File(savePath);
				if(!savePathDir.exists())
				{
					boolean blnCreateDir=savePathDir.mkdirs();
					if(!blnCreateDir)
					{
						addActionError("Can not generate file saving diretory");
						log.error("generate logo file saving diretory failed");
						initValue();
						return SUCCESS;
					}
				}
				File uploadFile=new File(savePath + File.separator + getLocalFileFileName());
				if(uploadFile.isFile()&&uploadFile.exists())
				{
					removeCurrentDomainFile(savePath);
				}
				FileOutputStream fos=null;
				FileInputStream fis=null;
				
				try{
					fos = new FileOutputStream(savePath + File.separator + getLocalFileFileName());
					fis = new FileInputStream(getLocalFile());
					byte[] buffer = new byte[1024];
					int len;
					while ((len = fis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
				}catch(Exception e)
				{
					log.error("upload logo pic failed", e);
				}
				finally{
					try{
						if(null!=fos)
						{
							fos.close();
						}
					}catch(IOException e)
					{
						log.error("ReportSettingAction", "FileOutputStream close failed", e);
					}
					try{
						if(null!=fis)
						{
							fis.close();
						}
					}catch(IOException e)
					{
						log.error("ReportSettingAction", "FileInputStream close failed", e);
					}
				}

				initValue();

				return SUCCESS;
			}else if("cleanWatchlist".equals(operation)) {
//				List<ApplicationProfile> list = QueryUtil.executeQuery(ApplicationProfile.class, null, 
//						new FilterParams("owner.id = :s1", new Object[] {getDomain().getId()}));
//				if (list != null && list.size() > 0) {
//					ApplicationProfile profile = list.get(0);
//					profile.setApplicationList(null);
//					profile.setCustomApplicationList(null);
//					QueryUtil.updateBo(profile);
//				}
//				List<HMServicesSettings> hmsslist = QueryUtil.executeQuery(HMServicesSettings.class, null,
//						new FilterParams("owner.id", getDomain().getId()));
//				if (list.isEmpty()) {
//					HMServicesSettings bo = new HMServicesSettings();
//					bo.setNotifyCleanWatchList(true);
//					QueryUtil.createBo(bo);
//				} else {
//					HMServicesSettings bo = hmsslist.get(0);
//					bo.setNotifyCleanWatchList(true);
//					QueryUtil.updateBo(bo);
//				}
				initValue();
				return SUCCESS;				
			}
			else {
				initValue();
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}
	
	private  boolean removeCurrentDomainFile(String savePath){
		String[] string_Path_Array = new String[3];
		string_Path_Array[0] = "bash";
		string_Path_Array[1] = "-c";
		string_Path_Array[2] = "cd " + savePath+ " && rm -rf "+logoFileName;
		try {
			Process process = Runtime.getRuntime().exec(string_Path_Array);
			// wait restart network end
			process.waitFor();
			if (process.exitValue() > 0) {
				String errorMsg = "remove logo file error in report setting";
				log.error("ReportSettingAction",errorMsg);
				return false;
			}
		} catch (Exception e) {
			log.error("ReportSettingAction",e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * update log config info
	 * 
	 * @return -
	 */
	private boolean updateLogConfig() {
		// 1. update db
		LogSettings bo;

		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
		if (list.isEmpty()) {
			log.error("updateLogConfig", "No Log setting bo in DB.");
			return false;
		} else {
			bo = list.get(0);
		}

		bo.setMaxPerfRecord(maxPerfRecord);
		bo.setMaxHistoryClientRecord(maxClientHistory);

		int preValue = bo.getInterfaceStatsInterval();
		int preStart = bo.getStatsStartMinute();
		
		if (interfacePollInterval<=0) {
			interfacePollInterval=30;
		}
		
		bo.setInterfaceStatsInterval(interfacePollInterval);
		bo.setStatsStartMinute(statsStartMinute);
		
		bo.setMaxOriginalCount(maxOriginalCount);
		bo.setMaxHourValue(maxHourValue);
		bo.setMaxDayValue(maxDayValue);
		bo.setMaxWeekValue(maxWeekValue);
		bo.setSlaPeriod(slaPeriod);
		bo.setClientPeriod(clientPeriod);
		bo.setMaxSupportAp(maxSupportAp);
		
		bo.setReportIntervalMinute(reportIntervalMinute);
		bo.setReportMaxApCount(reportMaxApCount);
		bo.setReportMaxApForSystem(reportMaxApForSystem);
		
		bo.setReportDbHourly(reportDbHourly);
		bo.setReportDbDaily(reportDbDaily);
		bo.setReportDbWeekly(reportDbWeekly);
		
		bo.setIntervalTablePartPer(intervalTablePartPer);
		bo.setMaxTimeTablePerSave(maxTimeTablePerSave);
		bo.setIntervalTablePartCli(intervalTablePartCli);
		bo.setMaxTimeTableCliSave(maxTimeTableCliSave);
		try {
			saveApplicationMap();
			if (getIsInHomeDomain()) {
				QueryUtil.updateBo(bo);
				if (preValue != bo.getInterfaceStatsInterval()) {
					HmBePerformUtil.updateInterfaceStatsPollInterval(bo.getInterfaceStatsInterval());
				}
				if(preStart != bo.getStatsStartMinute()){
					HmBePerformUtil.updateStatsStartMinute(bo.getStatsStartMinute());
				}
			}
			return true;
		} catch (Exception e) {
			log.error("updateLogConfig", "Update log settings catch exception!", e);
			return false;
		}
	}
	
	private void saveApplicationMap() throws Exception {
		long ownerId = getDomain().getId();
		ApplicationProfile profile;
		List<ApplicationProfile> list = QueryUtil.executeQuery(ApplicationProfile.class, null, 
				new FilterParams("owner.id = :s1", new Object[] {ownerId}));
		if (list != null && list.size() > 0) {
			profile = list.get(0);
		} else {
			profile = new ApplicationProfile();
		}
		Set<Application> appList = new HashSet<>();
		if (StringUtils.isNotBlank(selectedAppIds)) {
			String[] array = selectedAppIds.split(",");
			for (String id : array) {
				appList.add(QueryUtil.findBoById(Application.class, Long.parseLong(id)));
			}
		}
		profile.setApplicationList(appList);
		
		Set<CustomApplication> customAppList = new HashSet<CustomApplication>();
		if (StringUtils.isNotBlank(selectedCustomAppIds)) {
			String[] array = selectedCustomAppIds.split(",");
			for (String id : array) {
				customAppList.add(QueryUtil.findBoById(CustomApplication.class, Long.parseLong(id)));
			}
		}
		profile.setCustomApplicationList(customAppList);
		
		profile.setProfileName("default" + ownerId);//in fact, profile name is useless
		
		if (profile.getId() == null || profile.getId() < 1) {
			profile.setOwner(getDomain());
			profile.setDefaultFlag(false);
			QueryUtil.createBo(profile);
		} else {
			QueryUtil.updateBo(profile);
		}
		
		try {
			BoMgmt.getHiveApMgmt().updateConfigIndicationForL7Device(getDomain().getId(), true, ConfigurationResources.CONFIGURATION_CHANGE, 
					"Application Watch List", ConfigurationType.Configuration);
		} catch (Exception e) {
			BeLogTools.info(HmLogConst.M_PERFORMANCE, "Change device configuration indication error.", e);
			//log.error("Change device configuration indication error", e);
		}
		
		
	}
	
	private void initApplicationMap() {
		ApplicationService appService = new ApplicationService();
		Map<String, List<Application>> map = appService.initApplicationMap(getDomain());
		Map<String, List<CustomApplication>> customMap = appService.initCustomApplicationMap(getDomain());
		List<ApplicationDTO> list = new ArrayList<ApplicationDTO>();
	    if (map != null) {
	    	selectedAppList = map.get("selectedAppList");
	    	unSelectedAppList = map.get("unSelectedAppList");
	    	if (selectedAppList != null) {
	    		selectedAppNum = selectedAppList.size();
	    		for(Application app : selectedAppList){
	    			ApplicationDTO dto = new ApplicationDTO();
	    			dto.setId(app.getId());
	    			dto.setAppName(app.getAppName());
	    			dto.setAppCode(app.getAppCode());
	    			dto.setAppGroupName(app.getAppGroupName());
	    			dto.setDescription(app.getDescription());
	    			dto.setAppType(0);
	    			dto.setLastDayUsage(app.getLastDayUsage());
	    			dto.setLastDayUsageStr(app.getLastDayUsageStr());
	    			dto.setLastMonthUsage(app.getLastMonthUsage());
	    			dto.setLastMonthUsageStr(app.getLastMonthUsageStr());
	    			list.add(dto);
	    			allAppNames.add(app.getAppName());
					if(null != allGroupNames && !allGroupNames.isEmpty()){
						if(!allGroupNames.contains(app.getAppGroupName())){
							allGroupNames.add(app.getAppGroupName());
						}
					}else{
						allGroupNames.add(app.getAppGroupName());
					}
	    		}
	    		
	    	}
	    	if (unSelectedAppList != null) {
	    		unSelectedAppNum = unSelectedAppList.size();
	    		for(Application app: unSelectedAppList){
	    			allAppNames.add(app.getAppName());
					if(null != allGroupNames && !allGroupNames.isEmpty()){
						if(!allGroupNames.contains(app.getAppGroupName())){
							allGroupNames.add(app.getAppGroupName());
						}
					}else{
						allGroupNames.add(app.getAppGroupName());
					}
	    		}
	    	}
	    	
	    }
	    if (customMap != null) {
	    	selectedCustomAppList = customMap.get("selectedCustomAppList");
	    	unSelectedCustomAppList = customMap.get("unSelectedCustomAppList");
	    	if (selectedCustomAppList != null) {
	    		selectedCustomAppNum = selectedCustomAppList.size();
	    		for(CustomApplication app : selectedCustomAppList){
	    			ApplicationDTO dto = new ApplicationDTO();
	    			dto.setId(app.getId());
	    			dto.setAppName(app.getCustomAppName());
	    			dto.setAppCode(app.getAppCode());
	    			dto.setAppGroupName("Custom");
	    			dto.setDescription(app.getDescription());
	    			dto.setAppType(1);
	    			dto.setLastDayUsage(app.getLastDayUsage());
	    			dto.setLastDayUsageStr(app.getLastDayUsageStr());
	    			dto.setLastMonthUsage(app.getLastMonthUsage());
	    			dto.setLastMonthUsageStr(app.getLastMonthUsageStr());
	    			list.add(dto);
	    		}
	    	}
	    	if (unSelectedCustomAppList != null) {
	    		unSelectedCustomAppNum = unSelectedCustomAppList.size();
	    		for(CustomApplication app : unSelectedCustomAppList){
	    			String name = app.getCustomAppName().replace("\\", "\\\\");
	    			allCustomNames.add(name);
	    		}
	    	}
	    }
	    selectedAppDtoList = list;
	}

	/**
	 * init interval value
	 *
	 * @throws Exception -
	 */
	private void initValue() throws Exception {
		initApplicationMap();
		List<LogSettings> list = QueryUtil.executeQuery(LogSettings.class, null, null);
		if (list.isEmpty()) {
			// set default value
			maxPerfRecord = 500000;
			maxClientHistory = 2000000;

			return;
		}

		LogSettings currentSetting = list.get(0);
		maxPerfRecord = currentSetting.getMaxPerfRecord();
		maxClientHistory = currentSetting.getMaxHistoryClientRecord();
		interfacePollInterval = currentSetting.getInterfaceStatsInterval();
		statsStartMinute = currentSetting.getStatsStartMinute();
		
		maxOriginalCount= currentSetting.getMaxOriginalCount();
		maxHourValue= currentSetting.getMaxHourValue();
		maxDayValue= currentSetting.getMaxDayValue();
		maxWeekValue= currentSetting.getMaxWeekValue();
		slaPeriod= currentSetting.getSlaPeriod();
		clientPeriod= currentSetting.getClientPeriod();
		maxSupportAp= currentSetting.getMaxSupportAp();
		
		reportIntervalMinute = currentSetting.getReportIntervalMinute();
		reportMaxApCount = currentSetting.getReportMaxApCount();
		reportMaxApForSystem = currentSetting.getReportMaxApForSystem();
		
		reportDbHourly = currentSetting.getReportDbHourly();
		reportDbDaily = currentSetting.getReportDbDaily();
		reportDbWeekly = currentSetting.getReportDbWeekly();
		
		intervalTablePartPer=currentSetting.getIntervalTablePartPer();
		maxTimeTablePerSave=currentSetting.getMaxTimeTablePerSave();
		intervalTablePartCli=currentSetting.getIntervalTablePartCli();
		maxTimeTableCliSave=currentSetting.getMaxTimeTableCliSave();
		checkPreviousLogoFile();
		}
		
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_REPORTSETTING);
	}
	
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof ApplicationProfile) {
			ApplicationProfile profile = (ApplicationProfile) bo;
			if (profile.getApplicationList() != null) {
				profile.getApplicationList().size();
			}
			if(profile.getCustomApplicationList() != null){
				profile.getCustomApplicationList().size();
			}
		}
		return null;
	}
	
	public List<EnumItem> getPollIntervalList(){
		List<EnumItem> lst = new ArrayList<EnumItem>(3);
		lst.add(new EnumItem(10, "10 minutes"));
		lst.add(new EnumItem(30, "30 minutes"));
		//lst.add(new EnumItem(45, "45 minutes"));
		lst.add(new EnumItem(60, "60 minutes"));
//		lst.add(new EnumItem(75, "75 minutes"));
//		lst.add(new EnumItem(90, "90 minutes"));
//		lst.add(new EnumItem(120, "2 hours"));
//		lst.add(new EnumItem(180, "3 hours"));
//		lst.add(new EnumItem(240, "4 hours"));
//		lst.add(new EnumItem(300, "5 hours"));
//		lst.add(new EnumItem(360, "6 hours"));
		return lst;
		
	}
	
	public List<EnumItem> getStatsStartMinuteList(){
		List<EnumItem> lst = new ArrayList<EnumItem>(11);
		for(int i=0;i<60; i=i+5){
			lst.add(new EnumItem(i, String.valueOf(i)));
		}
		return lst;
		
	}

	public int getMaxPerfRecord() {
		return maxPerfRecord;
	}

	public void setMaxPerfRecord(int maxPerfRecord) {
		this.maxPerfRecord = maxPerfRecord;
	}
	
	public String getReportSummarySettingDisplay(){
		//if (isEasyMode()){ return "none";}
		if (getUserContext().isSuperUser()) {
			return "";
		}
		return "none";
		
	}

	// mark: QA suggest not support prompt changes in this feature.
	@Override
	public boolean isTrackFormChanges() {
		return false;
	}

	public int getMaxClientHistory() {
		return maxClientHistory;
	}

	public void setMaxClientHistory(int maxClientHistory) {
		this.maxClientHistory = maxClientHistory;
	}

	public int getInterfacePollInterval() {
		return interfacePollInterval;
	}

	public void setInterfacePollInterval(int interfacePollInterval) {
		this.interfacePollInterval = interfacePollInterval;
	}

	public int getStatsStartMinute() {
		return statsStartMinute;
	}

	public void setStatsStartMinute(int statsStartMinute) {
		this.statsStartMinute = statsStartMinute;
	}
	

	public int getMaxOriginalCount() {
		return maxOriginalCount;
	}

	public void setMaxOriginalCount(int maxOriginalCount) {
		this.maxOriginalCount = maxOriginalCount;
	}

	public int getMaxHourValue() {
		return maxHourValue;
	}

	public void setMaxHourValue(int maxHourValue) {
		this.maxHourValue = maxHourValue;
	}

	public int getMaxDayValue() {
		return maxDayValue;
	}

	public void setMaxDayValue(int maxDayValue) {
		this.maxDayValue = maxDayValue;
	}

	public int getMaxWeekValue() {
		return maxWeekValue;
	}

	public void setMaxWeekValue(int maxWeekValue) {
		this.maxWeekValue = maxWeekValue;
	}

	public int getSlaPeriod() {
		return slaPeriod;
	}

	public void setSlaPeriod(int slaPeriod) {
		this.slaPeriod = slaPeriod;
	}

	public int getClientPeriod() {
		return clientPeriod;
	}

	public void setClientPeriod(int clientPeriod) {
		this.clientPeriod = clientPeriod;
	}
	
	public int getMaxSupportAp() {
		return maxSupportAp;
	}

	public void setMaxSupportAp(int maxSupportAp) {
		this.maxSupportAp = maxSupportAp;
	}

	public int getReportIntervalMinute() {
		return reportIntervalMinute;
	}

	public void setReportIntervalMinute(int reportIntervalMinute) {
		this.reportIntervalMinute = reportIntervalMinute;
	}

	public int getReportMaxApCount() {
		return reportMaxApCount;
	}

	public void setReportMaxApCount(int reportMaxApCount) {
		this.reportMaxApCount = reportMaxApCount;
	}

	public int getIntervalTablePartPer() {
		return intervalTablePartPer;
	}

	public void setIntervalTablePartPer(int intervalTablePartPer) {
		this.intervalTablePartPer = intervalTablePartPer;
	}

	public int getMaxTimeTablePerSave() {
		return maxTimeTablePerSave;
	}

	public void setMaxTimeTablePerSave(int maxTimeTablePerSave) {
		this.maxTimeTablePerSave = maxTimeTablePerSave;
	}

	public int getIntervalTablePartCli() {
		return intervalTablePartCli;
	}

	public void setIntervalTablePartCli(int intervalTablePartCli) {
		this.intervalTablePartCli = intervalTablePartCli;
	}

	public int getMaxTimeTableCliSave() {
		return maxTimeTableCliSave;
	}

	public void setMaxTimeTableCliSave(int maxTimeTableCliSave) {
		this.maxTimeTableCliSave = maxTimeTableCliSave;
	}

	public File getLocalFile() {
		return localFile;
	}

	public void setLocalFile(File localFile) {
		this.localFile = localFile;
	}

	public String getLocalFileContentType() {
		return localFileContentType;
	}

	public void setLocalFileContentType(String localFileContentType) {
		this.localFileContentType = localFileContentType;
	}

	public String getLocalFileFileName() {
		if(!localFileFileName.equals(logoFileName))
		{
			localFileFileName=logoFileName;
		}
		return localFileFileName;
	}

	public void setLocalFileFileName(String localFileFileName) {
		this.localFileFileName = localFileFileName;
	}

	public String getPreviousFilePath() {
		return previousFilePath;
	}
	public void setPreviousFilePath(String previousFilePath) {
		this.previousFilePath= previousFilePath;
	}

	public boolean isLogoExsit() {
		return logoExsit;
	}

	public void setLogoExsit(boolean logoExsit) {
		this.logoExsit = logoExsit;
	}
	private void checkPreviousLogoFile()
	{
		// path of up load destination
		String savePath=HmContextListener.context
				.getRealPath(reportRootDir
						+ File.separator
						+ getDomain().getDomainName()
						+ File.separator
						+"vhm-report"
						+ File.separator
						+"images");
		File previousLogo=new File(savePath+File.separator+logoFileName);
		if(previousLogo.isFile()&&previousLogo.exists())
		{
			setLogoExsit(true);
			HttpServletRequest request = ServletActionContext.getRequest();
			String url=request.getRequestURL().toString();
			int schemePos=url.indexOf("://");
			String requestContext=url.substring(0, url.indexOf("/",schemePos+3));
			String logoContextPath="domains"
					+File.separator
					+ getDomain().getDomainName()
					+ File.separator
					+"vhm-report"
					+ File.separator
					+"images"+File.separator+logoFileName;
			if(!request.getContextPath().equals(""))
			{
				previousFilePath=requestContext+File.separator+request.getContextPath()+File.separator+logoContextPath;
			}
			else{
				previousFilePath=requestContext+File.separator+logoContextPath;
			}
		}
	}

	public List<Application> getSelectedAppList() {
		return selectedAppList;
	}

	public void setSelectedAppList(List<Application> selectedAppList) {
		this.selectedAppList = selectedAppList;
	}

	public List<Application> getUnSelectedAppList() {
		return unSelectedAppList;
	}

	public void setUnSelectedAppList(List<Application> unSelectedAppList) {
		this.unSelectedAppList = unSelectedAppList;
	}

	public int getSelectedAppNum() {
		return selectedAppNum;
	}

	public void setSelectedAppNum(int selectedAppNum) {
		this.selectedAppNum = selectedAppNum;
	}
	
	public int getWatchlistLimitation() {
		return ApplicationUtil.getWatchlistLimitation();
	}
	
	public int getUnSelectedAppNum() {
		return unSelectedAppNum;
	}

	public String getSelectedAppIds() {
		return selectedAppIds;
	}

	public void setSelectedAppIds(String selectedAppIds) {
		this.selectedAppIds = selectedAppIds;
	}

	public int getReportMaxApForSystem() {
		return reportMaxApForSystem;
	}

	public void setReportMaxApForSystem(int reportMaxApForSystem) {
		this.reportMaxApForSystem = reportMaxApForSystem;
	}

	public int getReportDbHourly() {
		return reportDbHourly;
	}

	public void setReportDbHourly(int reportDbHourly) {
		this.reportDbHourly = reportDbHourly;
	}

	public int getReportDbDaily() {
		return reportDbDaily;
	}

	public void setReportDbDaily(int reportDbDaily) {
		this.reportDbDaily = reportDbDaily;
	}

	public int getReportDbWeekly() {
		return reportDbWeekly;
	}

	public void setReportDbWeekly(int reportDbWeekly) {
		this.reportDbWeekly = reportDbWeekly;
	}
	
	private List<CustomApplication> selectedCustomAppList;
	private List<CustomApplication> unSelectedCustomAppList;
	private int selectedCustomAppNum;
	private int unSelectedCustomAppNum;
	private String selectedCustomAppIds;
	private List<ApplicationDTO> selectedAppDtoList;
	private List<String> allGroupNames = new ArrayList<>();
	private List<String> allAppNames = new ArrayList<>();
	private List<String> allCustomNames = new ArrayList<>();
	
	
	public List<String> getAllGroupNames() {
		return allGroupNames;
	}

	public void setAllGroupNames(List<String> allGroupNames) {
		this.allGroupNames = allGroupNames;
	}

	public List<String> getAllAppNames() {
		return allAppNames;
	}

	public void setAllAppNames(List<String> allAppNames) {
		this.allAppNames = allAppNames;
	}

	public List<String> getAllCustomNames() {
		return allCustomNames;
	}

	public void setAllCustomNames(List<String> allCustomNames) {
		this.allCustomNames = allCustomNames;
	}

	public List<ApplicationDTO> getSelectedAppDtoList() {
		return selectedAppDtoList;
	}

	public void setSelectedAppDtoList(List<ApplicationDTO> selectedAppDtoList) {
		this.selectedAppDtoList = selectedAppDtoList;
	}

	public String getSelectedCustomAppIds() {
		return selectedCustomAppIds;
	}

	public void setSelectedCustomAppIds(String selectedCustomAppIds) {
		this.selectedCustomAppIds = selectedCustomAppIds;
	}

	public List<CustomApplication> getSelectedCustomAppList() {
		return selectedCustomAppList;
	}

	public void setSelectedCustomAppList(
			List<CustomApplication> selectedCustomAppList) {
		this.selectedCustomAppList = selectedCustomAppList;
	}

	public List<CustomApplication> getUnSelectedCustomAppList() {
		return unSelectedCustomAppList;
	}

	public void setUnSelectedCustomAppList(
			List<CustomApplication> unSelectedCustomAppList) {
		this.unSelectedCustomAppList = unSelectedCustomAppList;
	}

	public int getSelectedCustomAppNum() {
		return selectedCustomAppNum;
	}

	public void setSelectedCustomAppNum(int selectedCustomAppNum) {
		this.selectedCustomAppNum = selectedCustomAppNum;
	}

	public int getUnSelectedCustomAppNum() {
		return unSelectedCustomAppNum;
	}

	public void setUnSelectedCustomAppNum(int unSelectedCustomAppNum) {
		this.unSelectedCustomAppNum = unSelectedCustomAppNum;
	}

	public int getTotalSystemAppNum() {
		return selectedAppNum + unSelectedAppNum;
	}

	public int getTotalCustomAppNum() {
		return selectedCustomAppNum + unSelectedCustomAppNum;
	}
	
	public int getTotalSelectedAppNum() {
		return selectedAppNum + selectedCustomAppNum;
	}
	
}