package com.ah.ws.rest.models.ga;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="content")
public class CheckGAServiceRequest {

    private String cid;
    
    private String vhmId;
    
    private int serviceId = 1;
    
    public CheckGAServiceRequest() {}

    //-----------Getter/Setter----------------
    @XmlElement(name="CustomerId")
    public String getCid() {
        return cid;
    }
    
    @XmlElement(name="VHMID")
    public String getVhmId() {
        return vhmId;
    }
    
    
    public void setCid(String cid) {
        this.cid = cid;
    }

    @XmlElement(name="ServiceId")
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public void setVhmId(String vhmId) {
        this.vhmId = vhmId;
    }
    
}
