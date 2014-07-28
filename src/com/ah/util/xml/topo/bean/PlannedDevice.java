package com.ah.util.xml.topo.bean;

import com.ah.bo.monitor.PlannedAP;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias(value = "plannedAP")
public class PlannedDevice {
    public PlannedDevice() {
    }
    public PlannedDevice(PlannedAP plannedAP) {
        this.apModel = plannedAP.apModel;
        this.wifi0Power = plannedAP.wifi0Power;
        this.wifi0Channel = plannedAP.wifi0Channel;
        this.wifi0ChannelWidth = plannedAP.wifi0ChannelWidth;
        this.wifi1Power = plannedAP.wifi1Power;
        this.wifi1Channel = plannedAP.wifi1Channel;
        this.wifi1ChannelWidth = plannedAP.wifi1ChannelWidth;
        
        this.wifi0Enabled = plannedAP.wifi0Enabled;
        this.wifi1Enabled = plannedAP.wifi1Enabled;
        
        this.countryCode = plannedAP.countryCode;
        this.hostName = plannedAP.hostName;
        this.x = plannedAP.x;
        this.y = plannedAP.y;
    }
    public PlannedAP toPlannedAP() {
        PlannedAP plannedAp = new PlannedAP();
        plannedAp.apModel = this.apModel;
        plannedAp.apModel = this.apModel;
        plannedAp.wifi0Power = this.wifi0Power;
        plannedAp.wifi0Channel = this.wifi0Channel;
        plannedAp.wifi0ChannelWidth = this.wifi0ChannelWidth;
        plannedAp.wifi1Power = this.wifi1Power;
        plannedAp.wifi1Channel = this.wifi1Channel;
        plannedAp.wifi1ChannelWidth = this.wifi1ChannelWidth;
        
        plannedAp.wifi0Enabled = this.wifi0Enabled;
        plannedAp.wifi1Enabled = this.wifi1Enabled;
        
        plannedAp.countryCode = this.countryCode;
        plannedAp.hostName = this.hostName;
        plannedAp.x = this.x;
        plannedAp.y = this.y;
        return plannedAp;
    }
    public short apModel, wifi0Power, wifi0Channel, wifi0ChannelWidth, wifi1Power, wifi1Channel,
            wifi1ChannelWidth;
    public boolean wifi0Enabled = true, wifi1Enabled = true;
    public int countryCode;
    @XStreamAsAttribute
    public String hostName;
    public double x, y;
}
