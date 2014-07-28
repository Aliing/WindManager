package com.ah.bo.port;

import java.io.Serializable;
import java.util.Arrays;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang3.math.NumberUtils;

import com.ah.be.common.NmsUtil;

@Embeddable
public class PortBasicProfile implements Serializable{

    private static final long serialVersionUID = -3636241677623643016L;
    public static final String PORTS_SPERATOR = ",";
    public static final String SPACE_SPERATOR = " ";
    public static final String DEFAULT_USB_PORTS = "0";
    
    private static final String ETH_PREFIX = "Eth";
    public static final String GIGABIT_PREFIX = "Eth1/";
    private static final String AGG_PREFIX = "Agg";
    private static final String SFP_PREFIX = "Eth1/";
    private static final String USB_PREFIX = "USB";

    //------- basic-----------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCESSPROFILE_ID")
    private PortAccessProfile accessProfile;
    
    private String ethPorts;
    private String sfpPorts;
    private String usbPorts;
    
    public static final short PORT_MODE_ETH = 1;
    public static final short PORT_MODE_SFP = 2;
    public static final short PORT_MODE_USB = 3;
    
    //------- Link Aggregation-----------
    private boolean enabledlinkAggregation;
    private short portChannel; // 1~30, change maximum from 64 to 30
    private static final short PORT_CHANNEL_MAX_NUM = 30;
    
    /*-----------Override Object methods--------------*/
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof PortBasicProfile && new EqualsBuilder()
                .append(this.accessProfile, ((PortBasicProfile) obj).accessProfile)
                .append(this.ethPorts, ((PortBasicProfile) obj).ethPorts)
                .append(this.sfpPorts, ((PortBasicProfile) obj).sfpPorts)
                .append(this.usbPorts, ((PortBasicProfile) obj).usbPorts)
                .isEquals());
    }
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.accessProfile)
                .append(this.ethPorts).append(this.sfpPorts)
                .append(this.usbPorts).toHashCode();
    }
    /*----------Transient field--------------*/
    @Transient
    public String[] getETHs() {
        return getPorts(ethPorts);
    }
    @Transient
    public String[] getSFPs() {
        return getPorts(sfpPorts);
    }
    @Transient
    public String[] getUSBs() {
        return getPorts(usbPorts);
    }
    @Transient
    private String[] getPorts(String ports) {
        if(StringUtils.isBlank(ports)) {
            return null;
        } else {
            return ports.split(PORTS_SPERATOR);
        }
    }
    
    @Deprecated
    @Transient
    public String getAllPortDesc(boolean chesapeake) {
        StringBuilder builder = new StringBuilder();
        if(isExistPortChannel()) {
            builder.append(AGG_PREFIX+portChannel);
            builder.append(" [");
        }
        if(StringUtils.isNotEmpty(ethPorts)) {
            String ethPortSection = NmsUtil.changeNumberSequence2NumberSestion(ethPorts);
            if(null != ethPortSection) {
                builder.append((chesapeake?GIGABIT_PREFIX:ETH_PREFIX)+ethPortSection);
            }
        }
        if(StringUtils.isNotEmpty(sfpPorts)) {
            if(StringUtils.isNotEmpty(ethPorts)) {
                builder.append(PORTS_SPERATOR + SPACE_SPERATOR);
            }
            String sfpPortSection = NmsUtil.changeNumberSequence2NumberSestion(sfpPorts);
            if(null != sfpPortSection) {
                builder.append(SFP_PREFIX+sfpPortSection);
            }
        }
        if(StringUtils.isNotEmpty(usbPorts)) {
            if(StringUtils.isNotEmpty(ethPorts) || StringUtils.isNotEmpty(sfpPorts)) {
                builder.append(PORTS_SPERATOR + SPACE_SPERATOR);
            }
            String usbPortSection = NmsUtil.changeNumberSequence2NumberSestion(usbPorts);
            if(null != usbPortSection) {
                builder.append(USB_PREFIX+usbPortSection);
            }
        }
        if(isExistPortChannel()) {
            builder.append("]");
        }
        return builder.toString();
    }
    
    @Transient
    public String getAllPortDesc(boolean chesapeake, int portNumber) {
        StringBuilder builder = new StringBuilder();
        if(isExistPortChannel()) {
            builder.append(AGG_PREFIX+portChannel);
            builder.append(" [");
        }
        if(StringUtils.isNotEmpty(ethPorts)) {
            String ethPortSection = NmsUtil.changeNumberSequence2NumberSestion(ethPorts);
            if(null != ethPortSection) {
                builder.append((chesapeake?GIGABIT_PREFIX:ETH_PREFIX)+ethPortSection);
            }
        }
        if(StringUtils.isNotEmpty(sfpPorts)) {
            if(StringUtils.isNotEmpty(ethPorts)) {
                builder.append(PORTS_SPERATOR + SPACE_SPERATOR);
            }
            // need to change the SFP1/x to Eth1/x, so add the number of gigabit port
            String[] sfps = getSFPs();
            int[] sfpNumArray = new int[sfps.length];
            int index = 0;
            for (String sfp : sfps) {
                if(NumberUtils.isNumber(sfp.trim())) {
                    Integer port = Integer.valueOf(sfp.trim());
                    port += portNumber;
                    sfpNumArray[index++] = port;
                }
            }
            
            String sfpPortSection = NmsUtil.changeNumberSequence2NumberSestion(getPortStr(sfpNumArray));
            if(null != sfpPortSection) {
                if(StringUtils.isEmpty(ethPorts)) {
                    builder.append(SFP_PREFIX+sfpPortSection);
                } else {
                    builder.append(sfpPortSection);
                }
            }
        }
        if(StringUtils.isNotEmpty(usbPorts)) {
            if(StringUtils.isNotEmpty(ethPorts) || StringUtils.isNotEmpty(sfpPorts)) {
                builder.append(PORTS_SPERATOR + SPACE_SPERATOR);
            }
            String usbPortSection = NmsUtil.changeNumberSequence2NumberSestion(usbPorts);
            if(null != usbPortSection) {
                builder.append(USB_PREFIX+usbPortSection);
            }
        }
        if(isExistPortChannel()) {
            builder.append("]");
        }
        return builder.toString();
    }
    private String getPortStr(int[] ports) {
        if(ports.length > 0) {
            final String str = Arrays.toString(ports);
            return StringUtils.deleteWhitespace(str.substring(1, str.length()-1));
        } else {
            return null;
        }
    }
    @Transient
    public boolean isExistPortChannel() {
        return enabledlinkAggregation && portChannel > 0 && portChannel <= PORT_CHANNEL_MAX_NUM;
    }
    
    @Transient
    public boolean isExistsAuthMode(short deviceType){
    	return accessProfile != null && accessProfile.isExistsAuthMode(deviceType);
    }
    
    //=======Getter&Setter==========

    public short getPortChannel() {
        return portChannel;
    }

    public void setPortChannel(short portChannel) {
        this.portChannel = portChannel;
    }
//    public short getPortType() {
//        return portType;
//    }
//    public void setPortType(short portType) {
//        this.portType = portType;
//    }

    public String getEthPorts() {
        return ethPorts;
    }

    public String getUsbPorts() {
        return usbPorts;
    }

    public void setEthPorts(String ethPorts) {
        this.ethPorts = ethPorts;
    }

    public String getSfpPorts() {
        return sfpPorts;
    }
    public void setSfpPorts(String sfpPorts) {
        this.sfpPorts = sfpPorts;
    }
    public void setUsbPorts(String usbPorts) {
        this.usbPorts = usbPorts;
    }

    public boolean isEnabledlinkAggregation() {
        return enabledlinkAggregation;
    }

    public void setEnabledlinkAggregation(boolean enabledlinkAggregation) {
        this.enabledlinkAggregation = enabledlinkAggregation;
    }

    public PortAccessProfile getAccessProfile() {
        return accessProfile;
    }

    public void setAccessProfile(PortAccessProfile accessProfile) {
        this.accessProfile = accessProfile;
    }
    
}
