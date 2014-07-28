package com.ah.ws.rest.models.idm;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Account")
public class IDMSalesforceResponse {
    private String id;
    private String success;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }


}
