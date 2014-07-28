/**
 *@filename		SingleTableItem.java
 *@version
 *@author		Fiona
 *@createtime	2007-12-17 PM 03:33:15
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.bo.network;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.ah.bo.HmBo;
import com.ah.bo.HmBoBase;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.port.PortGroupProfile;
import com.ah.ui.actions.admin.DeviceTagUtil;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
@Embeddable
public class SingleTableItem implements Serializable, Cloneable
{
	private static final long	serialVersionUID	= 1L;

	public static final short TYPE_NONE = -1;

	public static final short TYPE_GLOBAL = 1;

	public static final short TYPE_MAP = 2;

	public static final short TYPE_HIVEAPNAME = 3;

	public static final short TYPE_CLASSIFIER = 4;

	public static final short TYPE_NAMESPACE_REALM = 1;

	public static final short TYPE_NAMESPACE_TADIG = 2;

	public static final short TYPE_NAMESPACE_E212 = 3;

	public static final short TYPE_NAMESPACE_ICC = 4;

	public static EnumItem[] ENUM_RADIUS_USE_TYPE = MgrUtil.enumItems(
			"enum.radiusAttrs.", new int[] { TYPE_NAMESPACE_REALM,
					TYPE_NAMESPACE_TADIG, TYPE_NAMESPACE_E212, TYPE_NAMESPACE_ICC });

	public static EnumItem[] ENUM_ADDRESS_USE_TYPE = MgrUtil.enumItems(
			"enum.ipAddress.", new int[] { TYPE_GLOBAL,
				TYPE_MAP, TYPE_HIVEAPNAME, TYPE_CLASSIFIER });

	public static EnumItem[] ENUM_ADDRESS_USE_TYPE_VPNSERVICE = MgrUtil.enumItems(
			"enum.ipAddress.", new int[] { TYPE_GLOBAL});

	public static EnumItem[] ENUM_ADDRESS_RESERVE_TYPE = MgrUtil.enumItems(
			"enum.ipAddress.",
			new int[] { TYPE_MAP, TYPE_HIVEAPNAME, TYPE_CLASSIFIER });

	@Column(length = HmBoBase.IP_ADDRESS_WEBPAGE_URL)
	private String	ipAddress;

	@Column(length = HmBoBase.DEFAULT_DESCRIPTION_LENGTH)
	private String operatorName;

	@Column(length = HmBo.DEFAULT_DESCRIPTION_LENGTH)
	private String	description;

	@Column(length = HmBoBase.IP_ADDRESS_LENGTH)
	private String	netmask;

	@Column(length = 12)
	private String macEntry;

	private int vlanId = 1;

	@Column(length = 128)
	private String attributeValue;

	private short nameSpaceId = TYPE_NAMESPACE_REALM;

	private short	type	= TYPE_GLOBAL;

	private String	typeName;

	private boolean tag1Checked = true;

	private String	tag1;

	private boolean tag2Checked = true;

	private String	tag2;

	private boolean tag3Checked = true;

	private String	tag3;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LOCATION_ID")
	private MapContainerNode location;

	@Column(length = 12)
	private String	macRangeFrom;

	@Column(length = 12)
	private String	macRangeTo;
	
	private long configTemplateId = -1;
	
	private long nonGlobalId = -1;
	
	@Transient
	private PortGroupProfile nonDefault;
	
	public long getNonGlobalId() {
		return nonGlobalId;
	}

	public void setNonGlobalId(long nonGlobalId) {
		this.nonGlobalId = nonGlobalId;
	}

	public long getConfigTemplateId() {
		return configTemplateId;
	}

	public void setConfigTemplateId(long configTemplateId) {
		this.configTemplateId = configTemplateId;
	}

	public String getIpAddress()
	{
		return ipAddress;
	}

	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public short getNameSpaceId() {
		return nameSpaceId;
	}

	public void setNameSpaceId(short nameSpaceId) {
		this.nameSpaceId = nameSpaceId;
	}

	public String getDescription()
	{
		if(description==null)return "";
		return description;
	}
	
	public String getDescriptionStr()
	{
		return getDescription().replace("\\", "\\\\").replace("'", "\\'");
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getNetmask()
	{
		return netmask;
	}

	public void setNetmask(String netmask)
	{
		this.netmask = netmask;
	}

	public short getType()
	{
		return type;
	}

	public void setType(short type)
	{
		this.type = type;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public String getTag1()
	{
		return tag1;
	}

	public void setTag1(String tag1)
	{
		this.tag1 = tag1;
	}

	public String getTag2()
	{
		return tag2;
	}

	public void setTag2(String tag2)
	{
		this.tag2 = tag2;
	}

	public String getTag3()
	{
		return tag3;
	}

	public void setTag3(String tag3)
	{
		this.tag3 = tag3;
	}

	public MapContainerNode getLocation()
	{
		return location;
	}

	public void setLocation(MapContainerNode location)
	{
		this.location = location;
	}

	@Transient
	public String getUseTypeName()
	{
		return MgrUtil.getEnumString("enum.ipAddress." + String.valueOf(type));
	}

	@Transient
	public String getRadiusTypeName(){
		return MgrUtil.getEnumString("enum.radiusAttrs." + String.valueOf(nameSpaceId));
	}

	@Transient
	public String getTagValue(long domainId) {
	    String value = "";
	    switch (this.type) {
        case TYPE_MAP:
        	String mapName="";
        	if(this.location!=null&&this.location.getMapName()!=null)mapName=this.location.getMapName();
        	if(!mapName.equals(""))mapName=mapName.replace("\\", "\\\\").replace("'", "\\'");
            value = null == this.location ? "" : mapName;
            break;
        case TYPE_HIVEAPNAME:
            value = this.typeName;
            break;
        case TYPE_CLASSIFIER:        	
            value = getClassifierName(domainId);
            break;

        default:
            break;
        }
	    return value;
	}
	
	public String getTagValue() {
		//just use non-exist domainId
		return getTagValue(-99999l);
	}
		
	/**
	 * 
	 * @param domainId
	 * @return 
	 */
	public String getClassifierName(long domainId)
	{
		Map<String, String> map=DeviceTagUtil.getInstance().getClassifierCustomTag(domainId);
		String customTag1=map.get(DeviceTagUtil.CUSTOM_TAG1);
		String customTag2=map.get(DeviceTagUtil.CUSTOM_TAG2);
		String customTag3=map.get(DeviceTagUtil.CUSTOM_TAG3);
		
		StringBuffer tagResult = new StringBuffer();
		tagResult.append(tag1Checked?"(T)":"(F)");
		tagResult.append(customTag1+"=");
		if (tag1 != null && tag1.length() > 0) {
			tagResult.append(tag1);
		}
		tagResult.append(";");
		tagResult.append(tag2Checked?"(T)":"(F)");
		tagResult.append(customTag2+"=");
		if (tag2 != null && tag2.length() > 0) {
			tagResult.append(tag2);
		}
		tagResult.append(";");
		tagResult.append(tag3Checked?"(T)":"(F)");
		tagResult.append(customTag3+"=");
		if (tag3 != null && tag3.length() > 0) {
			tagResult.append(tag3);
		}
		return tagResult.toString();
	}
	
	public String getClassifierName1(long domainId)
	{
		Map<String, String> map=DeviceTagUtil.getInstance().getClassifierCustomTag(domainId);
		String customTag1=map.get(DeviceTagUtil.CUSTOM_TAG1);
 
		StringBuffer tagResult = new StringBuffer();
		tagResult.append(customTag1+" : ");
		if (tag1 != null && tag1.length() > 0) {
			tagResult.append(tag1);
		}
		return tagResult.toString();
	}
	
	public String getClassifierName2(long domainId)
	{
		Map<String, String> map=DeviceTagUtil.getInstance().getClassifierCustomTag(domainId);
		String customTag2=map.get(DeviceTagUtil.CUSTOM_TAG2);
 	
		StringBuffer tagResult = new StringBuffer();
		tagResult.append(customTag2+" : ");
		if (tag2 != null && tag2.length() > 0) {
			tagResult.append(tag2);
		}
		return tagResult.toString();
	}
	
	public String getClassifierName3(long domainId)
	{
		Map<String, String> map=DeviceTagUtil.getInstance().getClassifierCustomTag(domainId);
		String customTag3=map.get(DeviceTagUtil.CUSTOM_TAG3);
 
		StringBuffer tagResult = new StringBuffer();
		tagResult.append(customTag3+" : ");
		if (tag3 != null && tag3.length() > 0) {
			tagResult.append(tag3);
		}
		return tagResult.toString();
	}

	@Transient
	public String getSingleClassifierName()
	{
		StringBuffer tagResult = new StringBuffer();
		if (tag1Checked && StringUtils.isNotBlank(tag1)) {
			tagResult.append("Tag1");
			tagResult.append("(");
			tagResult.append(tag1);
			tagResult.append(")");
		} else if(tag2Checked && StringUtils.isNotBlank(tag2)) {
			tagResult.append("Tag2");
			tagResult.append("(");
			tagResult.append(tag2);
			tagResult.append(")");
		} else if(tag3Checked && StringUtils.isNotBlank(tag3)){
			tagResult.append("Tag3");
			tagResult.append("(");
			tagResult.append(tag3);
			tagResult.append(")");
		}
		return tagResult.toString();
	}

	@Transient
	private String restoreId;

	public String getRestoreId()
	{
		return restoreId;
	}

	public void setRestoreId(String restoreId)
	{
		this.restoreId = restoreId;
	}

	public String getMacEntry()
	{
		return macEntry;
	}

	public void setMacEntry(String macEntry)
	{
		this.macEntry = macEntry;
	}

	public int getVlanId()
	{
		return vlanId;
	}

	public void setVlanId(int vlanId)
	{
		this.vlanId = vlanId;
	}

	public String getAttributeValue()
	{
		return attributeValue;
	}

	public void setAttributeValue(String attributeValue)
	{
		this.attributeValue = attributeValue;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof SingleTableItem))
			return false;

		final SingleTableItem item = (SingleTableItem) o;

		if ((macEntry != null && macEntry.length() > 0) ? !macEntry.equalsIgnoreCase(item.macEntry)
				: (item.macEntry != null && item.macEntry.length() > 0)) {
			return false;
		}

		if (type != item.type || vlanId != item.vlanId) {
			return false;
		}

		if ((ipAddress != null && ipAddress.length() > 0) ? !ipAddress.equals(item.ipAddress)
				: (item.ipAddress != null && item.ipAddress.length() > 0)) {
			return false;
		}

		if ((netmask != null && netmask.length() > 0) ? !netmask.equals(item.netmask)
				: (item.netmask != null && item.netmask.length() > 0)) {
			return false;
		}

		if ((attributeValue != null && attributeValue.length() > 0) ? !attributeValue
				.equals(item.attributeValue) : (item.attributeValue != null && item.attributeValue
				.length() > 0)) {
			return false;
		}

		if ((typeName != null && typeName.length() > 0) ? !typeName.equals(item.typeName)
				: (item.typeName != null && item.typeName.length() > 0)) {
			return false;
		}

		if ((tag1 != null && tag1.length() > 0) ? !tag1.equals(item.tag1)
				: (item.tag1 != null && item.tag1.length() > 0)) {
			return false;
		}

		if ((tag2 != null && tag2.length() > 0) ? !tag2.equals(item.tag2)
				: (item.tag2 != null && item.tag2.length() > 0)) {
			return false;
		}

		if ((tag3 != null && tag3.length() > 0) ? !tag3.equals(item.tag3)
				: (item.tag3 != null && item.tag3.length() > 0)) {
			return false;
		}

		if (location != null ? !location.getMapName().equals(item.location.getMapName())
				: item.location != null) {
			return false;
		}

		if ((macRangeFrom != null && macRangeFrom.length() > 0) ? !macRangeFrom.equals(item.macRangeFrom)
				: (item.macRangeFrom != null && item.macRangeFrom.length() > 0)) {
			return false;
		}

		if ((macRangeTo != null && macRangeTo.length() > 0) ? !macRangeTo.equals(item.macRangeTo)
				: (item.macRangeTo != null && item.macRangeTo.length() > 0)) {
			return false;
		}
		
		if(configTemplateId == item.configTemplateId ? nonGlobalId != item.nonGlobalId : true){
			return false;
		}
		

		return true;
	}

	public int hashCode() {
		int result;
		result = (macEntry != null ? macEntry.hashCode() : 0);
		
		result = 29 * result + type;
		return result;
	}

	public boolean isTag1Checked()
	{
		return tag1Checked;
	}

	public void setTag1Checked(boolean tag1Checked)
	{
		this.tag1Checked = tag1Checked;
	}

	public boolean isTag2Checked()
	{
		return tag2Checked;
	}

	public void setTag2Checked(boolean tag2Checked)
	{
		this.tag2Checked = tag2Checked;
	}

	public boolean isTag3Checked()
	{
		return tag3Checked;
	}

	public void setTag3Checked(boolean tag3Checked)
	{
		this.tag3Checked = tag3Checked;
	}

	public String getMacRangeFrom() {
		return macRangeFrom;
	}

	public void setMacRangeFrom(String macRangeFrom) {
		this.macRangeFrom = macRangeFrom;
	}

	public String getMacRangeTo() {
		return macRangeTo;
	}

	public void setMacRangeTo(String macRangeTo) {
		this.macRangeTo = macRangeTo;
	}

	@Transient
	public int getKey() {
		return this.vlanId;
	}

	public void setKey(int key) {
		this.vlanId = key;
	}

	public PortGroupProfile getNonDefault() {
		return nonDefault;
	}

	public void setNonDefault(PortGroupProfile nonDefault) {
		this.nonDefault = nonDefault;
	}

	@Override
	public SingleTableItem clone() {
		try {
			return (SingleTableItem) super.clone();
		} catch (CloneNotSupportedException e) {
			return null;
		}
	}
}
