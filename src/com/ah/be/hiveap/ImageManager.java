package com.ah.be.hiveap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ah.be.admin.adminOperateImpl.BeVersionInfo;
import com.ah.be.common.NmsUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.hiveap.DeviceInfo;
import com.ah.bo.hiveap.DeviceInfo.DeviceOption;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.hiveap.HiveApImageInfo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.util.MgrUtil;
import com.ah.util.TextItem;
import com.ah.xml.deviceProperties.DeviceObj;
import com.ah.xml.deviceProperties.Devices;

public class ImageManager {

	private static final Logger	log	= Logger.getLogger("ImageManager");

	private static final Map<Short, List<DeviceOption>> imageVesionsMap = new HashMap<Short, List<DeviceOption>>();

	private static final Map<Short, List<DeviceOption>> imageInternalNamesMap = new HashMap<Short, List<DeviceOption>>();

	static {
		try {
			Devices devices = DevicePropertyManage.getInstance().getDevices();
			for (DeviceObj device : devices.getDevice()) {
				if(device.getKey() == null){
					continue;
				}
				DeviceInfo deviceInfo = new DeviceInfo(Short.parseShort(device.getKey().toString()));
				DeviceObj property = DevicePropertyManage.getInstance().getDeviceProperty(Short.parseShort(device.getKey().toString()));
				DevicePropertyManage.getInstance().clone(property, deviceInfo);
				deviceInfo.init();

				List<DeviceOption> deviceOptions = deviceInfo.getDeviceOptions(DeviceInfo.SPT_IMAGE_LS_NAME);

				imageVesionsMap.put(Short.valueOf(String.valueOf(device.getKey())), deviceOptions);
			}
		} catch (Exception e) {
			log.error("ImageManager.SPT_IMAGE_TYPE_NAME:", e);
		}

		try {
			Devices devices = DevicePropertyManage.getInstance().getDevices();
			for (DeviceObj device : devices.getDevice()) {
				if(device.getKey() == null){
					continue;
				}
				DeviceInfo deviceInfo = new DeviceInfo(Short.parseShort(device.getKey().toString()));
				DeviceObj property = DevicePropertyManage.getInstance().getDeviceProperty(Short.parseShort(device.getKey().toString()));
				DevicePropertyManage.getInstance().clone(property, deviceInfo);
				deviceInfo.init();

				List<DeviceOption> deviceOptions = deviceInfo.getDeviceOptions(DeviceInfo.SPT_IMAGE_INTERNAL_NAME);

				imageInternalNamesMap.put(Short.valueOf(String.valueOf(device.getKey())), deviceOptions);
			}
		} catch (Exception e) {
			log.error("ImageManager.SPT_IMAGE_INTERNAL_NAME:", e);
		}
	}

	/**
	 * Get the hardware name of device which matches the HiveManager version
	 *
	 *@return Map<String, Integer> : hardware target and uid
	 */
	public static Map<String, Integer> getHardwaresMatchCurrentHMVersion() {
		BeVersionInfo versionInfo = NmsUtil.getVersionInfo();
		String version = versionInfo.getMainVersion()+"r"+versionInfo.getSubVersion();
		String query = "SELECT DISTINCT hiveApModel FROM " + HiveAp.class.getSimpleName();
		List<?> allModel = QueryUtil.executeQuery(query, new SortParams("hiveApModel"), null);
		Map<String, Integer> nameList = new HashMap<String, Integer>();
		//Map<String, List<HiveApImageInfo>> existFile = ImageManager.getImageInfoFromDb();
		List<HiveApImageInfo> images = findImagesByMinorVersion(version);

		for (Object obj : allModel) {
			if (null == obj) {
				continue;
			}
			int apModel = Integer.parseInt(String.valueOf(obj));
			String targetName = getHardwareTargetNameByMode((short)apModel, version);

			for(HiveApImageInfo image: images){
				// look if download finished for a specific target name.
				if(image.getProductName().equals(targetName)){
					nameList.put(targetName, image.getImageUid());
				}
			}
			if(!nameList.containsKey(targetName)){
				nameList.put(targetName, 0);
			}
		}

		return nameList.isEmpty() ? null : nameList;
	}

	/**
	 * Get all the image files in this domain.
	 * @return List<TextItem>
	 */
	public static List<TextItem> getAllImageFiles() {
		List<TextItem> imageFiles = new ArrayList<TextItem>();
		List<HiveApImageInfo> images = QueryUtil.executeQuery(
				HiveApImageInfo.class, null, null);
		for (HiveApImageInfo image : images) {
			imageFiles.add(new TextItem(image.getImageName(), image
					.getImageVersion() + " - " + image.getImageName()));
		}
		if (imageFiles.size() == 0) {
			String none = MgrUtil.getUserMessage("config.optionsTransfer.none");
			imageFiles.add(new TextItem(none, none));
		}
		// ordered
		Collections.sort(imageFiles, new Comparator<TextItem>() {
			@Override
			public int compare(TextItem o1, TextItem o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return imageFiles;
	}

	/**
	 * Get the HiveOS image versions, each version will contain one HiveAP model
	 * image at least.
	 *
	 * @return -
	 */
	public static List<String> getAllImageVersions(){
		Set<String> versions = new HashSet<String>();
		List<HiveApImageInfo> images = QueryUtil.executeQuery(
				HiveApImageInfo.class, null, null);
		for (HiveApImageInfo image : images) {
			versions.add(image.getImageVersion().trim());
		}
		if(versions.isEmpty()){
			versions.add(MgrUtil.getUserMessage("config.optionsTransfer.none"));
		}
		List<String> list = new ArrayList<String>(versions);
		Collections.sort(list,new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				try {
					return o2.hashCode()-o1.hashCode();
				} catch (Exception e) {
					return 0;
				}
			}
		});
		return list;
	}
	/**
	 * Get HiveAP product names by ap model
	 *
	 * @param apModel -
	 * @return product names
	 */
	public static List<String> getHardwareTargetNamesByMode(short apModel) {
		//product names definition in hardwareTarget field
		List<String> proNames = new ArrayList<String>();
		List<DeviceOption> deviceOptions = NmsUtil.getDeviceInfo(apModel).getDeviceOptions(DeviceInfo.SPT_IMAGE_INTERNAL_NAME);
		for (DeviceOption deviceOption : deviceOptions) {
			proNames.add(deviceOption.getValue());
		}
		return proNames;
	}

	/**
	 * Get device hardware target name by device model and version
	 *
	 * @param apModel -
	 * @return hardware target name
	 */
	public static String getHardwareTargetNameByMode(short apModel, String version) {
		List<DeviceOption> deviceOptions = NmsUtil.getDeviceInfo(apModel).getDeviceOptions(DeviceInfo.SPT_IMAGE_LS_NAME);
		// at least format like X.XrX
		if (version == null || version.length() < 5) return null;

		String versionStr = version.toLowerCase().replace("r", ".") + ".0";
		String[] versions = versionStr.split("\\.");
		if (versions.length < 4) return null;
		versions[2] = versions[2].substring(0, 1);
		versionStr = versions[0] + "." + versions[1] + "." + versions[2] + "." +versions[3];

		for (DeviceOption deviceOption : deviceOptions) {
			if (NmsUtil.compareSoftwareVersion(deviceOption.getVersion(), versionStr) <= 0) {
				return deviceOption.getValue();
			}
		}
		return null;
	}

	/*
	 * Check whether a list of HiveAP uses a same image type
	 */
	public static boolean isSameImageType(Collection<HiveAp> hiveAps) {
		if (null != hiveAps) {
			Set<String> imageSet = new HashSet<String>();
			String imageName = null;
			for (HiveAp hiveAp : hiveAps) {
				imageName = hiveAp.getDeviceInfo().getDeviceOptions(DeviceInfo.SPT_IMAGE_INTERNAL_NAME).get(0).getValue();
				imageSet.add(imageName);
			}
			return imageSet.size() == 1;
		}
		return false;
	}

	public static HiveApImageInfo findImageByFileName(String imageName) {
		HiveApImageInfo image = QueryUtil.findBoByAttribute(
				HiveApImageInfo.class, "imageName", imageName);
		return image;
	}

	/**
	 *
	 * @param imageVer - only include minor version. e.g. 6.1r1, 6.1r2.
	 * @return
	 */
	public static List<HiveApImageInfo> findImagesByMinorVersion(String imageVer) {
		List<HiveApImageInfo> images = QueryUtil.executeQuery(
				HiveApImageInfo.class, null, null);
		List<HiveApImageInfo> matched = new ArrayList<HiveApImageInfo>();
		for (HiveApImageInfo image : images) {
			if (image.getImageVersion().startsWith(imageVer)) {
				matched.add(image);
			}
		}
		return matched;
	}

	/**
	 *
	 * @param imageVer - full version. e.g. 6.1r1, 6.1r1a, 6.1r2, 6.1r2a.
	 * @return
	 */
	public static List<HiveApImageInfo> findImagesByFullVersion(String imageVer) {
		List<HiveApImageInfo> images = QueryUtil.executeQuery(
				HiveApImageInfo.class, null, null);
		List<HiveApImageInfo> matched = new ArrayList<HiveApImageInfo>();
		for (HiveApImageInfo image : images) {
			if (image.getImageVersion().equals(imageVer)) {
				matched.add(image);
			}
		}
		return matched;
	}

	public static List<DeviceOption> getHMTargetsByInternalName(String type) {
		Set<Short> keySet = imageVesionsMap.keySet();
		Set<DeviceOption> hmTargets = new HashSet<DeviceOption>();


		for (Iterator<Short> key = keySet.iterator(); key.hasNext();) {
			Short model = key.next();
			if (model < 0) continue;

			List<DeviceOption> deviceOptions = imageInternalNamesMap.get(model);
			if (deviceOptions != null) {
				for (DeviceOption deviceOption : deviceOptions) {
					if (type.equals(deviceOption.getValue())) {
						hmTargets.addAll(imageVesionsMap.get(model));
						break;
					}
				}
			}
		}

		ArrayList<DeviceOption> lists = new ArrayList<DeviceOption>(hmTargets);
		Collections.sort(lists);

		return lists;
	}
}