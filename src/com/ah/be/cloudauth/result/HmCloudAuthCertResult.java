package com.ah.be.cloudauth.result;

public class HmCloudAuthCertResult {
    
    private UpdateCAStatus status = UpdateCAStatus.UNKNOW;
    
    private String certFileName;
    
    private String certPath;
    
    private String externalMessage;

    public HmCloudAuthCertResult() {
    }
    
    public HmCloudAuthCertResult(UpdateCAStatus status) {
        this.status = status;
    }
    
    public HmCloudAuthCertResult(UpdateCAStatus status, String message) {
        this.status = status;
        this.externalMessage = message;
    }
    
    public HmCloudAuthCertResult(String certFileName, String certPath) {
        this.status = UpdateCAStatus.SUCCESS;
        this.certFileName = certFileName;
        this.certPath = certPath;
    }
    
    public UpdateCAStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateCAStatus status) {
        this.status = status;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertFileName() {
        return certFileName;
    }

    public void setCertFileName(String certFileName) {
        this.certFileName = certFileName;
    }
    
    public String getResultMessage() {
        if(this.status == UpdateCAStatus.IDM_SERVER_ERR) {
            return this.externalMessage;
        } else {
            return this.status.getMessage();
        }
    }
}

