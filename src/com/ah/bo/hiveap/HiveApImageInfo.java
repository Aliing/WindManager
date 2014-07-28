/**
 *@filename		HiveApImageInfo.java
 *@version
 *@author		Fiona
 *@createtime	Jul 8, 2009 10:23:15 AM
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modify history*
 *
 */
package com.ah.bo.hiveap;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Index;
import org.jfree.util.Log;

import com.ah.be.common.NmsUtil;
import com.ah.be.common.PresenceUtil;
import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.be.parameter.device.DevicePropertyManage;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.util.devices.impl.Device;
import com.ah.xml.deviceProperties.DevicePropertyAttrOptionObj;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
@Entity
@Table(name = "HIVEAP_IMAGE_INFO")
@org.hibernate.annotations.Table(appliesTo = "HIVEAP_IMAGE_INFO", indexes = {
		@Index(name = "HIVE_AP_IMAGE_INFO_OWNER", columnNames = { "OWNER" })
		})
public class HiveApImageInfo implements HmBo {

	private static final long serialVersionUID = 1L;

	public static final byte SOURCE_TYPE_LICENSESERVER 	= 0;

	public static final byte SOURCE_TYPE_LOCAL 			= 1;

	public static final byte SOURCE_TYPE_SCP 			= 2;

	@Id
	@GeneratedValue
	private Long id;

	@Version
	private Timestamp version;

	@Column(length = DEFAULT_DESCRIPTION_LENGTH)
	private String imageName = "";

	@Column(length = DEFAULT_STRING_LENGTH)
	private String productName = "";

	@Column(length = 5)
	private String majorVersion = "";

	@Column(length = 5)
	private String minorVersion = "";

	@Column(length = 5)
	private String relVersion = "";

	@Column(length = 5)
	private String patchVersion = "";

	private int imageUid = 0;
	
	private Long releaseTime;

	@Transient
	private String releaseData = "";
	
	private static DateFormat rlDataFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyy", Locale.US);

	private long imageSize;

	private byte sourceType = SOURCE_TYPE_LICENSESERVER;

	public int getImageUid()
	{
		return imageUid;
	}

	public void setImageUid(int imageUid)
	{
		this.imageUid = imageUid;
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
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public void setVersion(Timestamp version) {
		this.version = version;
	}

	@Override
	public String getLabel() {
		return imageName;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER", nullable = false)
	private HmDomain owner;

	@Override
	public HmDomain getOwner() {
		return owner;
	}

	@Override
	public void setOwner(HmDomain owner) {
		this.owner = owner;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getMajorVersion() {
		return majorVersion;
	}

	public void setMajorVersion(String majorVersion) {
		this.majorVersion = majorVersion;
	}

	public String getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(String minorVersion) {
		this.minorVersion = minorVersion;
	}

	public String getRelVersion() {
		return relVersion;
	}

	public void setRelVersion(String relVersion) {
		this.relVersion = relVersion;
	}

	public String getPatchVersion() {
		return patchVersion;
	}

	public void setPatchVersion(String patchVersion) {
		this.patchVersion = patchVersion;
	}

	public String getImagePlatformString() {
//		Map<Short, String> latestVerMap = DevicePropertyManage.getInstance().getDeviceModelValueMapping(DeviceInfo.START_VERSION);
		Map<Short, List<DevicePropertyAttrOptionObj>> optionMap = DevicePropertyManage.getInstance().getDeviceModelOptionsMapping(DeviceInfo.SPT_IMAGE_INTERNAL_NAME);
		String imageVerNum = getImageVersionNum();
		
//		//version map
//		Iterator<Entry<Short, String>> latestVerItem = latestVerMap.entrySet().iterator();
//		while(latestVerItem.hasNext()){
//			Entry<Short, String> latestVerEntry = latestVerItem.next();
//			if(NmsUtil.compareSoftwareVersion(imageVerNum, latestVerEntry.getValue()) < 0){
//				latestVerItem.remove();
//			}
//		}
		
		//filter platform
		Iterator<Entry<Short, List<DevicePropertyAttrOptionObj>>> optionIterator = optionMap.entrySet().iterator();
		while(optionIterator.hasNext()){
			Entry<Short, List<DevicePropertyAttrOptionObj>> optionEntry = optionIterator.next();
//			if(!latestVerMap.containsKey(optionEntry.getKey())){
//				optionIterator.remove();
//				continue;
//			}
			if(optionEntry.getValue() == null || optionEntry.getValue().isEmpty()){
				optionIterator.remove();
				continue;
			}
			boolean pFound = false;
			for(DevicePropertyAttrOptionObj optObj : optionEntry.getValue()){
				if(optObj.getValue().equals(this.productName)){
					pFound = true;
					break;
				}
			}
			if(!pFound){
				optionIterator.remove();
				continue;
			}
			
			String[] supportVersions = (String[])AhConstantUtil.getEnumValues(Device.SUPPORTED_HIVEOS_VERSIONS, optionEntry.getKey());
			if(supportVersions == null){
				continue;
			}
			pFound = false;
			for(String versionNum : supportVersions){
				if(NmsUtil.compareSoftwareVersion(versionNum, imageVerNum) == 0){
					pFound = true;
					break;
				}
			}
			if(!pFound){
				optionIterator.remove();
				continue;
			}
		}
		
		StringBuilder sb = new StringBuilder();
		String pName = null;
		for (Short apModel : optionMap.keySet()) {
			pName = AhConstantUtil.getString(Device.NAME, apModel);
			if(StringUtils.isEmpty(pName) || "null".equalsIgnoreCase(pName)){
				continue;
			}else if (sb.length() == 0) {
				sb.append(pName);
			} else {
				sb.append(", ").append(pName);
			}
		}

		return sb.toString();
	}

	@Transient
	public String getImageVersion() {
		if (!"".equals(majorVersion) && !"".equals(minorVersion) && !"".equals(relVersion)) {
			return majorVersion+"."+minorVersion+"r"+relVersion
				+((patchVersion==null||"".equals(patchVersion))?"":patchVersion);//leave a blank char for sort image version used
		}
		return "";
	}
	
	@Transient
	public String getImageVersionNum() {
		if (!"".equals(majorVersion) && !"".equals(minorVersion) && !"".equals(relVersion)) {
			return majorVersion+"."+minorVersion+"."+relVersion+".0";
		}
		return "";
	}

	@Transient
	public void setImageVersion(String imageVerStr) {
		if (imageVerStr == null || "".equals(imageVerStr)) {
			return;
		}
		
		imageVerStr = imageVerStr.replace("r", ".").replace("R", ".");
		String[] verArgs = imageVerStr.split("\\.");
		if(verArgs.length < 3){
			return;
		}

		majorVersion = verArgs[0];
		minorVersion = verArgs[1];
		relVersion = verArgs[2];
	}
	
	@Transient
	public String getImageSizeString(){
		return PresenceUtil.convertValue(imageSize);
	}

	@Transient
	public String getReleaseData() {
		if(!StringUtils.isEmpty(releaseData)){
			return releaseData;
		}
		return rlDataFormat.format(new Date(releaseTime));
	}

	@Transient
	public void setReleaseData(String releaseData) {
		this.releaseData = releaseData;
		try{
			Date date = rlDataFormat.parse(releaseData);
			this.releaseTime = date.getTime();
		}catch(Exception e){
			Log.error("HOS Image release data\" + releaseData + \" parse error", e);
		}
	}

	public long getImageSize() {
		return imageSize;
	}

	public void setImageSize(long imageSize) {
		this.imageSize = imageSize;
	}

	public byte getSourceType() {
		return sourceType;
	}

	public void setSourceType(byte sourceType) {
		this.sourceType = sourceType;
	}

	public Long getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Long releaseTime) {
		this.releaseTime = releaseTime;
	}

}