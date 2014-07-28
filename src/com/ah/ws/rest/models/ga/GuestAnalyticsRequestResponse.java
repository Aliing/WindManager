package com.ah.ws.rest.models.ga;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GuestAnalyticsRequestResponse {
    private int status;
    private String apiKey;
    private String apiNonce;
    private int internalErrorCode;
    private String message;
    
    public GuestAnalyticsRequestResponse() {}
    
    //-----------Getter/Setter----------------
    public int getStatus() {
        return status;
    }
    public String getApiKey() {
        return apiKey;
    }
    public String getApiNonce() {
        return apiNonce;
    }
    public int getInternalErrorCode() {
        return internalErrorCode;
    }
    public String getMessage() {
        return message;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    public void setApiNonce(String apiNonce) {
        this.apiNonce = apiNonce;
    }
    public void setInternalErrorCode(int internalErrorCode) {
        this.internalErrorCode = internalErrorCode;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
