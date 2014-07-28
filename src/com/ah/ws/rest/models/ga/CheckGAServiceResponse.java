package com.ah.ws.rest.models.ga;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="content")
public class CheckGAServiceResponse {

    private int serviceStuts;
    
    private String apiKey;
    
    private String apiNonce;

    public CheckGAServiceResponse() {}
    
    //-----------Getter/Setter----------------
    @XmlElement(name="ServiceStatus")
    public int getServiceStuts() {
        return serviceStuts;
    }

    @XmlElement(name="ApiKey")
    public String getApiKey() {
        return apiKey;
    }

    @XmlElement(name="ApiNonce")
    public String getApiNonce() {
        return apiNonce;
    }

    public void setServiceStuts(int serviceStuts) {
        this.serviceStuts = serviceStuts;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public void setApiNonce(String apiNonce) {
        this.apiNonce = apiNonce;
    }
    
}
