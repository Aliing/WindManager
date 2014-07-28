package com.ah.be.cloudauth.exception;

import com.ah.be.cloudauth.result.UpdateCAStatus;
import com.ah.util.Tracer;

public class HmCloudAuthException extends Exception {

    private static final long serialVersionUID = 1L;
    
    private static final Tracer LOG = new Tracer(HmCloudAuthException.class.getSimpleName());
    
    private UpdateCAStatus errorStatus;
    
    private String externalErrMsg;
    
    public HmCloudAuthException(String method, UpdateCAStatus errorStatus) {
        super();
        this.errorStatus = errorStatus;
        LOG.error(method, errorStatus.getMessage());
    }
    
    public HmCloudAuthException(String method, String externalErrMsg, UpdateCAStatus errorStatus) {
        super();
        this.errorStatus = errorStatus;
        this.externalErrMsg =externalErrMsg;
        LOG.error(method, errorStatus.getMessage() + "\nexternal: " +externalErrMsg);
    }
    
    public HmCloudAuthException(String method, UpdateCAStatus errorStatus, String params) {
        super();
        this.errorStatus = errorStatus;
        LOG.error(method, "Parameters: " + params + "\n" + errorStatus.getMessage());
    }
    
    public HmCloudAuthException(String method, UpdateCAStatus errorStatus, Throwable cause) {
        super();
        this.errorStatus = errorStatus;
        LOG.error(method, errorStatus.getMessage(), cause);
    }
    
    public HmCloudAuthException(String method, UpdateCAStatus errorStatus, Throwable cause, String params) {
        super();
        this.errorStatus = errorStatus;
        LOG.error(method, "Parameters: " + params + "\n" + errorStatus.getMessage(), cause);
    }

    public UpdateCAStatus getErrorStatus() {
        return errorStatus;
    }

    public void setErrorStatus(UpdateCAStatus errorStatus) {
        this.errorStatus = errorStatus;
    }

    public String getExternalErrMsg() {
        return externalErrMsg;
    }
    
}
