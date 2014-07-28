package com.ah.ws.rest.models.ga;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ga")
public class GuestAnalyticsRequst {
    private String vhmId;
    private int operation;
    
    public GuestAnalyticsRequst() {}

    //-----------Getter/Setter----------------
    public String getVhmId() {
        return vhmId;
    }

    public void setVhmId(String vhmId) {
        this.vhmId = vhmId;
    }

    public int getOperation() {
        return operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }
    
    
}
