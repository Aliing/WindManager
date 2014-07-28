package com.ah.be.config.image;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.ah.be.app.HmBeLogUtil;
import com.ah.be.app.HmBeOsUtil;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.config.hiveap.UpdateParameters;
import com.ah.be.config.image.imageconfig.ImageConfig;
import com.ah.be.config.image.imageconfig.ImageType;
import com.ah.be.hiveap.ImageInfo;
import com.ah.be.os.FileManager;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmSystemLog;
import com.ah.bo.admin.LicenseServerSetting;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInfo.DeviceOption;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.MgrUtil;
import com.ah.util.NameValuePair;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;
import com.ah.xml.deviceProperties.DevicePropertyAttrOptionObj;

public class ImageManager {

	private static final Tracer log = new Tracer(
			ImageManager.class.getSimpleName());

	private static final String LS_IMAGE_DOWN_PROTOCOL = "https://";
	private static final String LS_IMAGE_DOWN_ACTION = "/imagedown.action";
	private static final int SYNCHRONIZED_IMAGE_INTERVAL = 1000 * 60 * 60;

	public static final short GET_ALL_IMAGE_BY_VER = 0;
	public static final short GET_ALL_IMAGE_BY_NAME = 1;

	private ImageSynFromLS imageSyn;

	public ImageManager() {
		init();
		imageSyn = new ImageSynFromLS();
	}

//	public static ImageManager getInstance() {
//		if (instance == null) {
//			instance = new ImageManager();
//		}
//		return instance;
//	}

	public void start() {
		imageSyn.start();
	}
	
	public boolean isStart() {
		return imageSyn != null && imageSyn.isStart();
	}

	private void init() {
		
		
		//get all image from disk.
		List<String> allImges = getAllImage(GET_ALL_IMAGE_BY_NAME);
		
		//update image release time, size, platform
		List<HiveApImageInfo> allImageInfos = QueryUtil.executeQuery(HiveApImageInfo.class, null, null);
		for(HiveApImageInfo imageInfo : allImageInfos){
			try{
				allImges.remove(imageInfo.getImageName());
				updateHiveApImageInfo(imageInfo);
				QueryUtil.updateBo(imageInfo);
			}catch(Exception e){
				log.error("com.ah.be.config.image.ImageManager init()", e);
			}
		}
		
		//delay 30s waiting Home domain create
		try{
			Thread.sleep(1000 * 15);
		}catch(Exception e){}
		
		//supplement image info that disk exists but HiveApImageInfo table losed.
		for(String imageName : allImges){
			try{
				HiveApImageInfo infoObj = new HiveApImageInfo();
				infoObj.setImageName(imageName);
				infoObj.setSourceType(HiveApImageInfo.SOURCE_TYPE_LOCAL);
				updateHiveApImageInfo(infoObj);
				QueryUtil.createBo(infoObj);
			}catch(Exception e){
				log.error("com.ah.be.config.image.ImageManager init()", e);
			}
		}
	}

	public boolean shutdown() {
		imageSyn.stop();
		return true;
	}

	public void downloadImageManual() {
		imageSyn.run();
	}

	public List<String> getAllImage(short type) {
		List<String> resList = new ArrayList<String>();
		if (type == GET_ALL_IMAGE_BY_VER) {
			List<HiveApImageInfo> imageList = getAllImageInfoFromHm();
			String imageVerStr = null;
			for (HiveApImageInfo imageInfo : imageList) {
				imageVerStr = imageInfo.getImageVersion();
				if (!resList.contains(imageVerStr)) {
					resList.add(imageVerStr);
				}
			}
		} else {
			String imagePath = AhDirTools.getImageDir("home");
			File imagDir = new File(imagePath);
			File[] files = imagDir.listFiles();
			int imageCounts = files != null ? files.length : 0;
			for (int i = 0; i < imageCounts; i++) {
				String fileName = files[i].getName();
				if (fileName.endsWith(".hm") || fileName.endsWith(".img")
						|| fileName.endsWith(".S")) {
					resList.add(fileName);
				}
			}
		}

		Collections.sort(resList);
		return resList;
	}
	
	public static List<HiveApImageInfo> getImageList(List<Short> modelList, String... version) {
		List<String> platformList = new ArrayList<String>();
		for(Short apModel : modelList){
			List<DeviceOption> options = NmsUtil.getDeviceInfo(apModel).getDeviceOptions(DeviceInfo.SPT_IMAGE_INTERNAL_NAME);
			if(options == null || options.isEmpty()){
				return null;
			}
			for(DeviceOption opObj : options){
				platformList.add(opObj.getValue());
			}
		}
		
		if(platformList.isEmpty()){
			return null;
		}
		
		String whereStr;
		Object[] paramArg;
		if(version.length > 0){
			String versionStr = getVersionNum(version[0]);
			String[] versionArg = versionStr.split("\\.");
			whereStr = "productName in (:s1) and majorVersion = :s2 and minorVersion = :s3 and relVersion = :s4";
			paramArg = new Object[]{platformList, versionArg[0], versionArg[1], versionArg[2]};
		}else{
			whereStr = "productName in (:s1)";
			paramArg = new Object[]{platformList};
		}
		
		List<HiveApImageInfo> imageInfoList = QueryUtil.executeQuery(HiveApImageInfo.class, null, new FilterParams(whereStr, paramArg));
		Collections.sort(imageInfoList, new HiveApImageInfoComparator());
		return imageInfoList;
	}
	
	public static HiveApImageInfo getLatestImageName(short hiveApModel, String... version) {
		//load platform list
		List<DeviceOption> options = NmsUtil.getDeviceInfo(hiveApModel).getDeviceOptions(DeviceInfo.SPT_IMAGE_INTERNAL_NAME);
		if(options == null || options.isEmpty()){
			return null;
		}
		List<String> platformList = new ArrayList<String>();
		for(DeviceOption opObj : options){
			platformList.add(opObj.getValue());
		}
		
		//get latest image version for platform
		String latestVersion = NmsUtil.getDeviceInfo(hiveApModel).getStringValue(DeviceInfo.SPT_LATEST_VERSION);
		if(StringUtils.isEmpty(latestVersion)){
			latestVersion = NmsUtil.getHMCurrentVersion();
		}
		
		String whereStr;
		Object[] paramArg;
		if(version.length > 0){
			String versionStr = getVersionNum(version[0]);
			String[] versionArg = versionStr.split("\\.");
			whereStr = "productName in (:s1) and majorVersion = :s2 and minorVersion = :s3 and relVersion = :s4";
			paramArg = new Object[]{platformList, versionArg[0], versionArg[1], versionArg[2]};
		}else{
			whereStr = "productName in (:s1)";
			paramArg = new Object[]{platformList};
		}
		
		List<HiveApImageInfo> imageInfoList = QueryUtil.executeQuery(HiveApImageInfo.class, null, new FilterParams(whereStr, paramArg));
		if(imageInfoList != null && !imageInfoList.isEmpty()){
			Collections.sort(imageInfoList, new HiveApImageInfoComparator());
			for(HiveApImageInfo imageInfo : imageInfoList){
				if(NmsUtil.compareSoftwareVersion(imageInfo.getImageVersionNum(), latestVersion) > 0){
					continue;
				}
				//return match image.
				return imageInfo;
			}
		}
		return null;
	}
	
	public static boolean imageCheck(String imageName){
		String imagePath = getImagePath(imageName);
		
		//check image file whether exists.
		if(!HmBeOsUtil.isFileExist(imagePath)){
			return false;
		}
		
		//parse image info.
		ImageInfo imageFileInfo = NmsUtil.getImageInfoFromFile(imagePath);
		if (imageFileInfo == null) {
			return false;
		}
		
		// image version check
		String version = imageFileInfo.getReversion();
		String[] versions = imageFileInfo.getReversion().split("\\.");
		if (version == null || version.length() < 7 || versions.length < 4) {
			log.error("getImageConfig_Local", MgrUtil.getUserMessage(
					"error.image.versionFormatInvalid",
					new String[] {imageName, imageFileInfo.getReversion() }));
			return false;
		}

		//image platform string check
		if (StringUtils.isEmpty(imageFileInfo.getTargetName())) {
			return false;
		}
		
		return true;
	}
	
	public static void removeImage(String imageName){
		String imagePath = getImagePath(imageName);
		try{
			//remove image file from disk.
			File imageFile = new File(imagePath);
			if(imageFile.isFile()){
				imageFile.delete();
			}
			
			//remove image from DB.
			QueryUtil.removeBos(HiveApImageInfo.class, new FilterParams("imageName", imageName));
		}catch(Exception e){
			log.error("Remove image "+imagePath+"failed.", e);
		}
	}

	public List<HiveApImageInfo> getAllImageInfoFromHm() {
		List<HiveApImageInfo> imageList = QueryUtil.executeQuery(HiveApImageInfo.class, null, null);
		if(imageList != null && !imageList.isEmpty()){
			return imageList;
		}
		Iterator<HiveApImageInfo> imageItems = imageList.iterator();
		HiveApImageInfo imageInfo = null;
		while(imageItems.hasNext()){
			imageInfo = imageItems.next();
			if (!HmBeOsUtil.isFileExist(getImagePath(imageInfo.getImageName()))) {
				try {
					QueryUtil.removeBoBase(imageInfo);
					imageItems.remove();
				} catch (Exception e) {
					log.error("exception occured while remove hive ap image info(Back-end).", e);
				}
			}
		}
		
		return imageList;
	}

	public static void updateHiveApImageInfo(HiveApImageInfo imageInfo) {
		if(imageInfo == null){
			return;
		}
		
		HmDomain homeDomain;
		if(BoMgmt.getDomainMgmt().getHomeDomain() != null){
			homeDomain = BoMgmt.getDomainMgmt().getHomeDomain();
		}else{
			homeDomain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN);
		}
		imageInfo.setOwner(homeDomain);
//		imageInfo.setSourceType(HiveApImageInfo.SOURCE_TYPE_LICENSESERVER);
		
		// validate image file format
		ImageInfo imageFileInfo = NmsUtil.getImageInfoFromFile(getImagePath(imageInfo.getImageName()));
		if (imageFileInfo == null) {
			return;
		}
		
		imageInfo.setProductName(imageFileInfo.getType());
		imageInfo.setImageSize(Long.valueOf(imageFileInfo.getSize()));
		imageInfo.setReleaseData(imageFileInfo.getDate());
		imageInfo.setImageVersion(imageFileInfo.getReversion());
	}

	private static String getVersionNum(String verStr){
		if(verStr == null){
			return verStr;
		}
		String versionStr = verStr.replace("r", ".");
		return versionStr += ".0";
	}
	
	private static String getImagePath(String imageName){
		return AhDirTools.getImageDir("home") + imageName;
	}
	
	//mapping between platform name and latest version.
	public Map<String, String> getPlatformLatestVerMap(){
		Map<String, String> resMap = new HashMap<String, String>();
		Map<Short, String> latestVerMap = DevicePropertyManage.getInstance().getDeviceModelValueMapping(DeviceInfo.SPT_LATEST_VERSION);
		Map<Short, List<DevicePropertyAttrOptionObj>> optionMap = DevicePropertyManage.getInstance().getDeviceModelOptionsMapping(DeviceInfo.SPT_IMAGE_LS_NAME);
		String defLatestVer = NmsUtil.getHMCurrentVersion();
		
		Iterator<Entry<Short, List<DevicePropertyAttrOptionObj>>> optionEntry = optionMap.entrySet().iterator();
		String softVer = null;
		List<DevicePropertyAttrOptionObj> optionList = null;
		while(optionEntry.hasNext()){
			Entry<Short, List<DevicePropertyAttrOptionObj>> itemOption = optionEntry.next();
			if(latestVerMap.containsKey(itemOption.getKey())){
				softVer = latestVerMap.get(itemOption.getKey());
			}else{
				softVer = defLatestVer;
			}
			
			optionList = itemOption.getValue();
			if(optionList == null || optionList.isEmpty()){
				continue;
			}
			
			for(DevicePropertyAttrOptionObj optionObj : optionList){
				resMap.put(optionObj.getValue(), softVer);
			}
		}
		
		return resMap;
	}

	public class ImageSynFromLS implements Runnable {

		private ScheduledExecutorService imageTimer;

		public void start() {
			if (imageTimer == null || imageTimer.isShutdown()) {
				imageTimer = Executors.newSingleThreadScheduledExecutor();
				imageTimer.scheduleWithFixedDelay(this,
						UpdateParameters.TIMER_DELAY,
						SYNCHRONIZED_IMAGE_INTERVAL, TimeUnit.MILLISECONDS);
			}
		}

		public void stop() {
			if (imageTimer != null && !imageTimer.isShutdown()) {
				imageTimer.shutdown();
			}
		}

		public boolean isStart() {
			return imageTimer != null && !imageTimer.isShutdown();
		}

		@Override
		public void run() {
			try {
				downloadImage();
			} catch (Throwable t) {
				log.error("ImageSynFromLS failed: ", t);
			}
		}

		public void downloadImage() {
			List<HiveApImageInfo> imageNeedDownload = syncImageWithLS();
			if (imageNeedDownload == null || imageNeedDownload.isEmpty()) {
				return;
			}

			StringBuffer dImage = new StringBuffer();
			for (HiveApImageInfo imageInfo : imageNeedDownload) {
				if (!downloadImageFromLS(imageInfo)) {
					log.error("Download image '" + imageInfo.getImageName()
							+ "' from license server failed.");
					continue;
				}
				if(dImage.length() > 0){
					dImage.append(", ");
				}
				dImage.append(imageInfo.getImageName());
			}
			
			if(dImage.length() > 0){
				String allImagesStr = null;
				if(dImage.length() > 400){
					allImagesStr = dImage.substring(0, dImage.indexOf(",", 400)) + " ...";
				}else{
					allImagesStr = dImage.toString();
				}
				String message = MgrUtil.getUserMessage("geneva_06.info.image.download.from.ls", new String[]{allImagesStr});
				HmBeLogUtil.addSystemLog(HmSystemLog.LEVEL_MAJOR, HmSystemLog.FEATURE_ADMINISTRATION, message);
			}
		}

		private boolean downloadImageFromLS(HiveApImageInfo imageInfo) {
			try {
				String lsUrl = this.getLSUrl();
				if (lsUrl == null || "".equals(lsUrl)) {
					return false;
				}
				if (imageInfo == null) {
					return false;
				}

				// download image from license server
				String imageName = imageInfo.getImageName();
				HttpCommunication httpCommunication = new HttpCommunication(
						lsUrl);
				List<NameValuePair> pairList = new ArrayList<NameValuePair>();
				pairList.add(new NameValuePair("operation", "downloadImage"));
				pairList.add(new NameValuePair("imageName", imageName));
				HttpEntity responseEntity = httpCommunication
						.sendRequestByGet(pairList);
				byte[] resBts = EntityUtils.toByteArray(responseEntity);
				
				//remove image on HM
				removeImage(imageName);
				
				//save new image in disk
				String imagePath = getImagePath(imageName);
				FileManager.getInstance().createFile(resBts, imagePath);
				
				// insert into DB
				updateHiveApImageInfo(imageInfo);
				QueryUtil.createBo(imageInfo);
				
				return true;
			} catch (Exception e) {
				log.error("downloadImageFromLS", e);
				return false;
			}
		}

		private List<HiveApImageInfo> syncImageWithLS() {
			List<HiveApImageInfo> ls_ImageInfoList = getAllImageInfoFromLS();
			List<HiveApImageInfo> local_ImageInfoList = getAllImageInfoFromHm();
			
			// remove image from HM local that has been remove in license server.
			Iterator<HiveApImageInfo> localItems = local_ImageInfoList.iterator();
			HiveApImageInfo localImageInfo = null;
			boolean isFound = false;
			while(localItems.hasNext()){
				localImageInfo = localItems.next();
				if(localImageInfo.getSourceType() != HiveApImageInfo.SOURCE_TYPE_LICENSESERVER){
					continue;
				}
				isFound = false;
				for(HiveApImageInfo lsInfo : ls_ImageInfoList){
					if(lsInfo.getImageName().equals(localImageInfo.getImageName()) && 
							lsInfo.getImageUid() == localImageInfo.getImageUid()){
						isFound = true;
						break;
					}
				}
				if(!isFound){
					removeImage(localImageInfo.getImageName());
					localItems.remove();
				}
			}
			
			//remove image info that version higher than current HM can support.
			Map<String, String> versionMap = getPlatformLatestVerMap();
			Iterator<HiveApImageInfo> lsItems = ls_ImageInfoList.iterator();
			HiveApImageInfo lsimageInfo = null;
			while(lsItems.hasNext()){
				lsimageInfo = lsItems.next();
				if(!versionMap.containsKey(lsimageInfo.getProductName())){
					lsItems.remove();
					continue;
				}
				if(NmsUtil.compareSoftwareVersion(lsimageInfo.getImageVersionNum(), 
						versionMap.get(lsimageInfo.getProductName())) > 0){
					lsItems.remove();
				}
			}
			
			//get latest HOS image.
			Map<String, List<HiveApImageInfo>> imageGroupMap = new HashMap<String, List<HiveApImageInfo>>();
			for(HiveApImageInfo imageInfo : ls_ImageInfoList){
				if(!imageGroupMap.containsKey(imageInfo.getProductName())){
					imageGroupMap.put(imageInfo.getProductName(), new ArrayList<HiveApImageInfo>());
				}
				imageGroupMap.get(imageInfo.getProductName()).add(imageInfo);
			}
			for(List<HiveApImageInfo> infoList : imageGroupMap.values()){
				if(infoList != null && !infoList.isEmpty()){
					Collections.sort(infoList, new HiveApImageInfoComparator());
				}
			}
			List<HiveApImageInfo> latestImageList = new ArrayList<HiveApImageInfo>();
			for(List<HiveApImageInfo> infoList : imageGroupMap.values()){
				if(infoList != null && !infoList.isEmpty()){
					latestImageList.add(infoList.get(0));
				}
			}
			//filter that image that has been exists in HM local.
			Iterator<HiveApImageInfo> latestItems = latestImageList.iterator();
			while(latestItems.hasNext()){
				lsimageInfo = latestItems.next();
				isFound = false;
				for(HiveApImageInfo localInfo : local_ImageInfoList){
					if(localInfo.getImageUid() == lsimageInfo.getImageUid() && 
							localInfo.getImageName().equals(lsimageInfo.getImageName())){
						isFound = true;
						break;
					}
				}
				if(isFound){
					latestItems.remove();
				}
			}

			return latestImageList;
		}

		private List<HiveApImageInfo> getAllImageInfoFromLS() {
			List<HiveApImageInfo> imageInfoList = new ArrayList<HiveApImageInfo>();
			try {
				String lsUrl = getLSUrl();
				if (lsUrl == null || "".equals(lsUrl)) {
					return imageInfoList;
				}

				HttpCommunication httpCommunication = new HttpCommunication(
						lsUrl);
				List<NameValuePair> pairList = new ArrayList<NameValuePair>();
				pairList.add(new NameValuePair("operation", "queryImageInfo"));
				HttpEntity responseEntity = httpCommunication
						.sendRequestByGet(pairList);
				String results = EntityUtils.toString(responseEntity);

				ImageConfig imageConfig = MgrUtil.unmarshalImageConfig(results);
				if(imageConfig == null || imageConfig.getImage().isEmpty()){
					return imageInfoList;
				}
				
				for(ImageType imageType : imageConfig.getImage()){
					HiveApImageInfo imageInfo = new HiveApImageInfo();
					imageInfo.setImageName(imageType.getImageName());
					//set imageid
					String imageUIdStr = imageType.getId().substring(imageType.getId().indexOf("_")+1);
					imageInfo.setImageUid(Integer.valueOf(imageUIdStr));
					imageInfo.setProductName(imageType.getPlatform());
					imageInfo.setImageVersion(imageType.getVersion());
					imageInfo.setSourceType(HiveApImageInfo.SOURCE_TYPE_LICENSESERVER);
					
					imageInfoList.add(imageInfo);
				}
				return imageInfoList;
			} catch (Exception e) {
				log.error("getImageConfig_LS()", e);
			}

			return null;
		}

		private String getLSUrl() {
			String sqlStr = "select lserverUrl from " + LicenseServerSetting.class.getSimpleName();
			List<?> resList = QueryUtil.executeQuery(sqlStr, null, null, 1);
			if(resList == null || resList.isEmpty()){
				return null;
			}else{
				return LS_IMAGE_DOWN_PROTOCOL + resList.get(0) + LS_IMAGE_DOWN_ACTION;
			}
		}
	}
	
	public static class HiveApImageInfoComparator implements Comparator<HiveApImageInfo>{

		@Override
		public int compare(HiveApImageInfo o1, HiveApImageInfo o2) {
			if(o1 == null || o2 == null){
				return -1;
			}
			
			int o1_MajorVersion, o1_MinorVersion, o1_RelVersion;
			int o2_MajorVersion, o2_MinorVersion, o2_RelVersion;
			
			o1_MajorVersion = Integer.valueOf(o1.getMajorVersion());
			o1_MinorVersion = Integer.valueOf(o1.getMinorVersion());
			o1_RelVersion = Integer.valueOf(o1.getRelVersion());
			
			o2_MajorVersion = Integer.valueOf(o2.getMajorVersion());
			o2_MinorVersion = Integer.valueOf(o2.getMinorVersion());
			o2_RelVersion = Integer.valueOf(o2.getRelVersion());
			
			if(o1_MajorVersion != o2_MajorVersion){
				return o2_MajorVersion - o1_MajorVersion;
			}
			
			if(o1_MinorVersion != o2_MinorVersion){
				return o2_MinorVersion - o1_MinorVersion;
			}
			
			if(o1_RelVersion != o2_RelVersion){
				return o2_RelVersion - o1_RelVersion;
			}
			
			if(o2.getReleaseTime() != null && o1.getReleaseTime() != null && 
					!o2.getReleaseTime().equals(o1.getReleaseTime()) ){
				long resLong = o2.getReleaseTime().longValue() - o1.getReleaseTime().longValue();
				if(resLong > 0L){
					return 1;
				}else if(resLong < 0L){
					return -1;
				}else{
					return 0;
				}
			}
			
			if(o1.getImageUid() != o2.getImageUid()){
				return o2.getImageUid() - o1.getImageUid();
			}
			
			if(o1.getSourceType() == HiveApImageInfo.SOURCE_TYPE_LICENSESERVER){
				return -1;
			}
			
			return 0;
		}
		
	}
}
