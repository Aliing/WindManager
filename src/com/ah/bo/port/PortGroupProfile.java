package com.ah.bo.port;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.AhInterface;
import com.ah.bo.hiveap.AhInterface.DeviceInfType;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.network.SingleTableItem;
import com.ah.util.MgrUtil;

@Entity
@Table(name = "PORT_TEMPLATE_PROFILE")
@org.hibernate.annotations.Table(appliesTo = "PORT_TEMPLATE_PROFILE", indexes = {
        @Index(name = "PORT_TEMPLATE_PROFILE_OWNER", columnNames = { "OWNER" })
        })
@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class PortGroupProfile implements HmBo,Comparable<PortGroupProfile> {
    
    private static final long serialVersionUID = 6206683852170254421L;

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER", nullable = false)
    private HmDomain owner;
    
    @Version
    private Timestamp version;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    // a flag for determine create by which network policy
    private Long parentNPId;
    
    private String deviceModels;
    private short deviceType = HiveAp.Device_TYPE_SWITCH;
    private short portNum =24 ; // 1, 2, 5, 24, 48
    
    // set the link aggregation - load balance mode as global field for the chip limitation (not allow to set for specific port channel yet)
    public static final short LOADBLANCE_MODE_AUTO = 1;
    public static final short LOADBLANCE_MODE_SRC_DST_MAC = 2;
    public static final short LOADBLANCE_MODE_SRC_DET_IP = 3;
    public static final short LOADBLANCE_MODE_SRC_DET_IP_PORT = 4;
    public static final short LOADBLANCE_MODE_SRC_DET_MAC_IP_PORT = 5;
    private short loadBalanceMode = LOADBLANCE_MODE_SRC_DET_MAC_IP_PORT;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "POSITION")
    @CollectionTable(name = "PORT_BASIC_PROFILE", joinColumns = @JoinColumn(name = "PORTGROUPS_ID", nullable = false))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<PortBasicProfile> basicProfiles = new ArrayList<PortBasicProfile>();

    //PSE Settings
	@ElementCollection(fetch = FetchType.LAZY)
	@OrderColumn(name = "POSITION")
	@CollectionTable(name = "PORT_PSE_PROFILE", joinColumns = @JoinColumn(name = "PORT_GROUP_PROFILE_ID", nullable = false))
	@Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private List<PortPseProfile> portPseProfiles = new ArrayList<PortPseProfile>();
	
    // Monitor Port Source Specification
    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "POSITION")
    @CollectionTable(name = "PORT_MONITOR_PROFILE", joinColumns = @JoinColumn(name = "PORTGROUPS_ID", nullable = false))
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private List<PortMonitorProfile> monitorProfiles = new ArrayList<PortMonitorProfile>();
	
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn(name = "POSITION")
    @CollectionTable(name = "PORT_TEMPLATE_PROFILE_ITEM", joinColumns = @JoinColumn(name = "PORTPROFILES_ID", nullable = false))
    @Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
    private List<SingleTableItem> items = new ArrayList<SingleTableItem>();

    //TODO WAN Settings
    
    /*----------Transient field--------------*/
    @Transient
    private boolean selected;
    
    @Transient
    public String getPseSettingsDisplayStyle(){
    	return "none";
    }
    
    /*-----------public methods--------------*/
    @Transient
    public String getTemplDesc() {
        StringBuilder builder = new StringBuilder();
        builder.append("for ");
        String[] devices = getDeviceModelStrs();
        if(null != devices) {
            boolean notFirst = false;
            for (String device : devices) {
                if(notFirst) {
                    builder.append(" and ");
                } else {
                    notFirst = true;
                }
                builder.append(MgrUtil.getEnumString("enum.hiveAp.model."+device));
            }
        }
        builder.append(" as ");
        builder.append(MgrUtil.getEnumString("enum.hiveAp.deviceType."+deviceType));
        
        return builder.toString();
    }
    
    @Transient
    public String getTemplDescInfo() {
        StringBuilder builder = new StringBuilder();
        builder.append("for ");
        String[] devices = getDeviceModelStrs();
        if(null != devices) {
            boolean notFirst = false;
            for (String device : devices) {
                if(notFirst) {
                    builder.append(", ");
                } else {
                    notFirst = true;
                }
                builder.append(MgrUtil.getEnumString("enum.hiveAp.model."+device));
            }
        }
        builder.append(" as ");
        builder.append(MgrUtil.getEnumString("enum.hiveAp.deviceType."+deviceType));
        
        return builder.toString();
    }
    
    @Transient
    public String getSelectedItemPurposeDesc() {
        StringBuilder builder = new StringBuilder();
        String[] devices = getDeviceModelStrs();
        if(null != devices) {
            boolean notFirst = false;
            for (String device : devices) {
                if(notFirst) {
                    builder.append(", ");
                } else {
                    notFirst = true;
                }
                builder.append(MgrUtil.getEnumString("enum.hiveAp.model."+device));
            }
        }
        builder.append(" as ");
        builder.append(MgrUtil.getEnumString("enum.hiveAp.deviceType."+deviceType));
        
        return builder.toString();
    }
    /**
     * Get the device models array.
     * @return Null or String[]
     */
    @Transient
    public String[] getDeviceModelStrs() {
        if(StringUtils.isEmpty(deviceModels)) {
            return null;
        } else {
            return deviceModels.split(PortBasicProfile.PORTS_SPERATOR);
        }
    }
    
    @Transient
    public short getFirstHiveApModel(){
    	String[] devcieModels = getDeviceModelStrs();
    	if(devcieModels != null && devcieModels.length > 0){
    		return Short.valueOf(devcieModels[0]);
    	}else{
    		return -1;
    	}
    }
    /**
     * For the device models dialog: the argument for the update method.<br>
     * E.g., {portsNum: 24, deviceType: 4, deviceModels: [17]}
     * @return empty string or a source JSON text string
     */
    @Transient
    public String getPortSetting() {
        if(StringUtils.isEmpty(deviceModels)) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            builder.append("portsNum: "+portNum);
            builder.append(", deviceType: "+deviceType);
            builder.append(",deviceModels: ["+deviceModels+"]");
            builder.append("}");
            return builder.toString();
        }
    }
    @Transient
    public String getPortsDesc() {
        // Port Template Profiles
        if (this.getBasicProfiles().isEmpty()) {
            return "";
        } else {
            Map<String, PortBasicProfile> tmpBasic = getPortGroupBasicInfo();
            // ------------------------------- this crap is just for sorting the port description :: start
            Map<String, PortAccessProfile> normalMap = new TreeMap<String, PortAccessProfile>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    if(!o1.equals(o2) && o1.contains(PortBasicProfile.GIGABIT_PREFIX) 
                            && o2.contains(PortBasicProfile.GIGABIT_PREFIX)) {
                        // Eth1/5 should show before Eth1/12
                        String o1Ports = o1.split(PortBasicProfile.PORTS_SPERATOR)[0].split("-")[0];
                        String o2Ports = o2.split(PortBasicProfile.PORTS_SPERATOR)[0].split("-")[0];
                        final int subLength = o1Ports.length() - o2Ports.length();
                        if(subLength > 0 || subLength < 0) {
                            return subLength;
                        } 
                    }
                    return o1.compareTo(o2);
                }
            });
            Map<String, PortAccessProfile> aggMap = new TreeMap<String, PortAccessProfile>(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            for (PortBasicProfile aBasic : tmpBasic.values()) {
                final PortAccessProfile acc = aBasic.getAccessProfile();
                if(aBasic.isExistPortChannel()) {
                    aggMap.put(aBasic.getAllPortDesc(isChesapeake(), this.portNum), acc);
                } else {
                    normalMap.put(aBasic.getAllPortDesc(isChesapeake(), this.portNum), acc);
                }
            }
            StringBuilder builder = new StringBuilder("[");
            boolean notFirstFlag = false;
            
            for (String key : normalMap.keySet()) {
                if (notFirstFlag) {
                    builder.append(",");
                } else {
                    notFirstFlag = true;
                }
                PortAccessProfile acc = normalMap.get(key);
                builder.append(buildDescByKey(key, acc));
            }
            for (String key : aggMap.keySet()) {
                if (notFirstFlag) {
                    builder.append(",");
                } else {
                    notFirstFlag = true;
                }
                PortAccessProfile acc = aggMap.get(key);
                builder.append(buildDescByKey(key, acc));
            }
            // ------------------------------- sort the port description :: end
            builder.append("]");
            return builder.toString();
        }
    }

    private String buildDescByKey(String key, PortAccessProfile acc) {
        return "{key:'" + key + "', id:" + (null == acc ? 0 : acc.getId())
        		+ ", value: '" + (null == acc ? "None Access Profile" : acc.getName())
                + "'}";
    }
    @Transient
    private Map<String, PortBasicProfile> portGroupBasicInfo;
    
    public Map<String, PortBasicProfile> getPortGroupBasicInfo() {
        if(null == portGroupBasicInfo || portGroupBasicInfo.isEmpty()) {
            portGroupBasicInfo= groupPortBasicInfo();
        }
        return portGroupBasicInfo;
    }

    private Map<String, PortBasicProfile> groupPortBasicInfo() {
        Map<String, PortBasicProfile> tmpBasic = new HashMap<>();
        int index = 0;
        for (PortBasicProfile profile : this.getBasicProfiles()) {
            final PortAccessProfile acc = profile.getAccessProfile();
            if(profile.isExistPortChannel()) {
                tmpBasic.put("port-channel_"+profile.getPortChannel()+"_"+index++, profile);
            } else {
                if(null == tmpBasic.get(acc.getName())) {
                    tmpBasic.put(acc.getName(), profile);
                } else {
                    PortBasicProfile prvBasic = tmpBasic.get(acc.getName());
                    
                    if(StringUtils.isNotBlank(profile.getEthPorts())) {
                        prvBasic.setEthPorts((StringUtils.isBlank(prvBasic.getEthPorts()) 
                                ? "" : prvBasic.getEthPorts()+PortBasicProfile.PORTS_SPERATOR)+profile.getEthPorts());
                    }
                    if(StringUtils.isNotBlank(profile.getSfpPorts())) {
                        prvBasic.setSfpPorts((StringUtils.isBlank(prvBasic.getSfpPorts()) 
                                ? "" : prvBasic.getSfpPorts()+PortBasicProfile.PORTS_SPERATOR)+profile.getSfpPorts());
                    }
                    if(StringUtils.isNotBlank(profile.getUsbPorts())) {
                        prvBasic.setUsbPorts((StringUtils.isBlank(prvBasic.getUsbPorts()) 
                                ? "" : prvBasic.getUsbPorts()+PortBasicProfile.PORTS_SPERATOR)+profile.getUsbPorts());
                    }
                }
            }

        }
        return tmpBasic;
    }
    
    @Transient
    public boolean isChesapeake() {
        if (StringUtils.isNotBlank(deviceModels)) {
            if (deviceModels.contains("" + HiveAp.HIVEAP_MODEL_SR24)
            		|| deviceModels.contains("" + HiveAp.HIVEAP_MODEL_SR2124P)
            		|| deviceModels.contains("" + HiveAp.HIVEAP_MODEL_SR2024P)
            		|| deviceModels.contains("" + HiveAp.HIVEAP_MODEL_SR2148P)
                    || deviceModels.contains("" + HiveAp.HIVEAP_MODEL_SR48)) {
                return true;
            }
        }
        return false;
    }
    
    @Transient
    public boolean isConfigured() {
        return !portPseProfiles.isEmpty() || !monitorProfiles.isEmpty() || isExistPortChannel();
    }
    
    @Transient
    public List<String> getAllETHs() {
        if(basicProfiles == null){
        	return null;
        }
        List<String> allEthList = new ArrayList<>();
        for(PortBasicProfile portBasicProfile : basicProfiles){
        	if(portBasicProfile.getETHs() == null) continue;
        	for(String eth : portBasicProfile.getETHs()){
        		allEthList.add(eth);
        	}
    	}
        return allEthList;
    }
    
    @Transient
    public List<String> getAllSFPs() {
        if(basicProfiles == null){
        	return null;
        }
        List<String> allSfpList = new ArrayList<>();
        for(PortBasicProfile portBasicProfile : basicProfiles){
        	if(portBasicProfile.getSFPs()== null) continue;
        	for(String spf : portBasicProfile.getSFPs()){
        		allSfpList.add(spf);
        	}
    	}
        return allSfpList;
    }
    
    @Transient
    public List<String> getAllUSBs() {
        if(basicProfiles == null){
        	return null;
        }
        List<String> allUSBList = new ArrayList<>();
        for(PortBasicProfile portBasicProfile : basicProfiles){
        	if(portBasicProfile.getUSBs() == null) continue;
        	for(String usb : portBasicProfile.getUSBs()){
        		allUSBList.add(usb);
        	}
    	}
        return allUSBList;
    }
    
    /**
     * Get the ports value (defined in {@link AhInterface}) list 
     * 
     * @author Yunzhi Lin
     * - Time: Dec 11, 2012 5:19:58 PM
     * @param interfaceType - see {@link DeviceInfType}
     * @param portType - see the constant defined in {@link PortAccessProfile}
     * @return <b>null</b> or <b>a not empty list</b>
     */
    @Transient
    public List<Short> getPortFinalValuesByPortType(DeviceInfType interfaceType, short portType) {
        if(null == basicProfiles || basicProfiles.isEmpty()) {
            return null;
        }
        List<Short> finalPortValues = new ArrayList<>();
        for(PortBasicProfile basic : basicProfiles) {
            if(null != basic && null != basic.getAccessProfile()) {
                if(basic.getAccessProfile().getPortType() == portType) {
                    switch (interfaceType) {
                    case Gigabit:
                        if(ArrayUtils.isNotEmpty(basic.getETHs())) {
                            for (String ethStr : basic.getETHs()) {
                                if(NumberUtils.isNumber(ethStr)) {
                                    finalPortValues.add(DeviceInfType.Gigabit.getFinalValue(NumberUtils.toInt(ethStr), getFirstHiveApModel()));
                                }
                            }
                        }
                        break;
                    case SFP:
                        if(ArrayUtils.isNotEmpty(basic.getSFPs())) {
                            for (String sfpStr : basic.getSFPs()) {
                                if(NumberUtils.isNumber(sfpStr)) {
                                    finalPortValues.add(DeviceInfType.SFP.getFinalValue(NumberUtils.toInt(sfpStr), getFirstHiveApModel()));
                                }
                            }
                        }
                        break;
                    case USB:
                        if(ArrayUtils.isNotEmpty(basic.getUSBs())) {
                            for (String usbStr : basic.getUSBs()) {
                                if(NumberUtils.isNumber(usbStr)) {
                                    finalPortValues.add(DeviceInfType.USB.getFinalValue(NumberUtils.toInt(usbStr), getFirstHiveApModel()));
                                }
                            }
                        }
                        
                        break;
                    case PortChannel:
                        if (basic.isExistPortChannel()) {
                            finalPortValues.add(DeviceInfType.PortChannel
                                    .getFinalValue(basic.getPortChannel(), getFirstHiveApModel()));
                        }
                        
                        break;
                        
                    default:
                        break;
                    }
                }
            }
        }
        return finalPortValues.isEmpty() ? null : finalPortValues;
    }
    
    @Transient
    public String getPortsBasicData() {
        if(this.getBasicProfiles().isEmpty()) {
            return "";
        } else {
            StringBuilder builder = new StringBuilder("[");
            boolean notFirstFlag = false;
            Map<String, PortBasicProfile> tmpBasic = getPortGroupBasicInfo();
            for (PortBasicProfile basic : tmpBasic.values()) {
                StringBuilder subBuilder = new StringBuilder();
                //String selected = "";
                if(notFirstFlag) {
                    subBuilder.append(",");
                } else {
                    notFirstFlag = true;
                    //selected = ", selected: true";
                }
                subBuilder.append("{");
                subBuilder.append("portType: " + (null == basic.getAccessProfile() ? PortAccessProfile.PORT_TYPE_ACCESS
                                        : basic.getAccessProfile().getPortType()));
                subBuilder.append(", groupNum: " +basic.getAccessProfile().getId());
                subBuilder.append(", accessProfileId: " +basic.getAccessProfile().getId());
                subBuilder.append(", portChannel: " +basic.getPortChannel());
                //subBuilder.append(selected);
                subBuilder.append(", ports: {");
                
                if(isValidatePorts(basic.getEthPorts())) {
                    subBuilder.append("ETH: [" + basic.getEthPorts()+"]");
                }
                if(isValidatePorts(basic.getSfpPorts())) {
                    if(isValidatePorts(basic.getEthPorts())) {
                        subBuilder.append(", ");
                    }
                    subBuilder.append("SFP: [" + basic.getSfpPorts()+"]");
                }
                if(isValidatePorts(basic.getUsbPorts())) {
                    if(isValidatePorts(basic.getEthPorts())
                            ||isValidatePorts(basic.getSfpPorts())) {
                        subBuilder.append(", ");
                    }
                    subBuilder.append("USB: [" + basic.getUsbPorts()+"]");
                }
                
                subBuilder.append("}}");
                
                builder.append(subBuilder.toString());
            }
            builder.append("]");
            return builder.toString();
        }
    }
    private boolean isValidatePorts(String ports) {
        return StringUtils.isNotBlank(ports)
                && !ports.equalsIgnoreCase("null");
    }
    @Transient
    public boolean isExistPortChannel() {
        for (PortBasicProfile basic : this.basicProfiles) {
            if(basic.isExistPortChannel()) {
                return true;
            }
        }
        return false;
    }
    @Transient
    public List<PortAccessProfile> getAllAccessProfiles() {
        List<PortAccessProfile> list = new ArrayList<>();
        for (PortBasicProfile basicProfile : this.basicProfiles) {
            list.add(basicProfile.getAccessProfile());
        }
        return list;
    }
    /*-----------Override Object methods--------------*/
    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this.id, ((PortGroupProfile) obj).id)
                .append(this.name, ((PortGroupProfile) obj).name).isEquals();
    }
    @Override
	public int compareTo(PortGroupProfile port) {
    	int cop = name.compareTo(port.name);
//    	if(cop == 0){
//    		return (int) (id.longValue() - port.getId().longValue());
//    	}else{
    		return cop;
//    	}
	}
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.id).append(this.name).toHashCode();
    }
    @Override
    public PortGroupProfile clone() {
        try {
            return (PortGroupProfile) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
    @Override
    public String toString() {
        return new StringBuilder().append(
                "{id:" + this.id + ", name:" + this.name + "}").toString();
    }

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
        return this.name;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    //=======Getter&Setter==========//
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public short getDeviceType() {
        return deviceType;
    }
    public short getPortNum() {
        return portNum;
    }
    public void setDeviceType(short deviceType) {
        this.deviceType = deviceType;
    }
    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }
    public List<PortBasicProfile> getBasicProfiles() {
        return basicProfiles;
    }
    public void setBasicProfiles(List<PortBasicProfile> basicProfiles) {
        this.basicProfiles = basicProfiles;
    }
    public short getLoadBalanceMode() {
        return loadBalanceMode;
    }
    public void setLoadBalanceMode(short loadBalanceMode) {
        this.loadBalanceMode = loadBalanceMode;
    }
    public Long getParentNPId() {
        return parentNPId;
    }
    public void setParentNPId(Long parentNPId) {
        this.parentNPId = parentNPId;
    }
    public String getDeviceModels() {
        return deviceModels;
    }
    public void setDeviceModels(String deviceModels) {
        this.deviceModels = deviceModels;
    }
	public List<PortPseProfile> getPortPseProfiles() {
		return portPseProfiles;
	}
	public void setPortPseProfiles(List<PortPseProfile> portPseProfiles) {
		this.portPseProfiles = portPseProfiles;
	}
    public List<PortMonitorProfile> getMonitorProfiles() {
        return monitorProfiles;
    }
    public void setMonitorProfiles(List<PortMonitorProfile> monitorProfiles) {
        this.monitorProfiles = monitorProfiles;
    }

	public List<SingleTableItem> getItems() {
		return items;
	}

	public void setItems(List<SingleTableItem> items) {
		this.items = items;
	}
	
	public List<SingleTableItem> getCurrentPolicyItems(Long netWorkPolicyId){
	    List<SingleTableItem> tmpItems = new ArrayList<>();
		for(Iterator<SingleTableItem> it = items.iterator();it.hasNext();){
			SingleTableItem nonDefaults = it.next();
			if(nonDefaults.getConfigTemplateId() == netWorkPolicyId.longValue()){
				tmpItems.add(nonDefaults);
			}
		}
		return tmpItems;
	}
	//She added this block to fix bug 25952 for correcting the warn message
	public List<List<SingleTableItem>> getPortTemplateForDisplay() {
		List<String> nameList = getDifferentNum(getPortTemplateNameList(items));
		List<List<SingleTableItem>> dis = new ArrayList<List<SingleTableItem>>();
		for (int i = 0; i < nameList.size(); i++) {
			List<SingleTableItem> temp = new ArrayList<SingleTableItem>();
			for (SingleTableItem s : getSortedItemByPortTemplateName()) {
				if (s.getNonDefault() != null) {
					if (s.getNonDefault().getName().equals(nameList.get(i))) {
						temp.add(s);
					}
				}
			}
			dis.add(temp);
		}
		return dis;
	}

	public List<SingleTableItem> getSortedItemByPortTemplateName() {
		return getSortedSingleTableItem(
				getDifferentNum(getPortTemplateNameList(items)), items);
	}

	public List<SingleTableItem> getSortedSingleTableItem(List<String> name,
			List<SingleTableItem> items) {
		List<SingleTableItem> item = new ArrayList<SingleTableItem>();
		for (int i = 0; i < name.size(); i++) {
			List<SingleTableItem> temp = new ArrayList<SingleTableItem>();
			for (SingleTableItem s : items) {
				if (s.getNonDefault() != null) {
					if (s.getNonDefault().getName().equals(name.get(i))) {
						temp.add(s);
					}
				}
			}
			item.addAll(temp);
		}
		return item;
	}

	public List<String> getDifferentNum(List<String> str) {
		List<String> tempStr = new ArrayList<String>();
		for (String s : str) {
			if (tempStr.contains(s)) {
				continue;
			} else {
				tempStr.add(s);
			}
		}
		return tempStr;
	}

	public List<String> getPortTemplateNameList(List<SingleTableItem> items) {
		List<String> str = new ArrayList<String>();
		for (SingleTableItem sin : items) {
			if (sin.getNonDefault() != null) {
				str.add(sin.getNonDefault().getName());
			}
		}
		return str;
	}
	//end
	
}
