package com.ah.ws.rest.models.idm;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ssid")
public class IDMSSID {

    private String name;
    private short authType;
    private long defUPID;

    public IDMSSID() {}
    
    public IDMSSID(String ssidName, short authType, long defUPID) {
        this.name = ssidName;
        this.authType = authType;
        this.defUPID = defUPID;
    }

    public String getName() {
        return name;
    }

    public short getAuthType() {
        return authType;
    }

    public long getDefUPID() {
        return defUPID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAuthType(short authType) {
        this.authType = authType;
    }

    public void setDefUPID(long defUPID) {
        this.defUPID = defUPID;
    }
    
    
}
