package com.ah.be.cloudauth;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

import com.ah.be.cloudauth.annotation.ConfigProp;

@Embeddable
public class IDMConfig implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_RADSEC_TLS_PORT = 2083;
    
    @ConfigProp(name = "idm.gateway.host")
    private String idmGatewayServer;
    
    @ConfigProp(name = "idm.certification.prj")
    @Transient
    private String idmCertServer;
    
    @ConfigProp(name = "idm.webServer.prj")
    @Transient
    private String idmWebServer;
    
    @ConfigProp(name = "idm.apiServer.prj")
    private String idmAPIServer;
    
    @ConfigProp(name = "idm.certification.retrieveCert.api")
    private String idmCertAPI;
    
    @ConfigProp(name = "idm.webServer.retrievecustomerID.api")
    private String idmCustomerAPI;
    
    @ConfigProp(name = "idm.selfReg.device.api")
    private String idmSelfRegDeviceGuestAPI;
    
    @ConfigProp(name = "idm.salesforce.createIDM.api")
    @Transient
    private String salesforceCreateIDMAPI;
    
    @ConfigProp(name = "idm.selfReg.device.crl")
    private String idmSelfRegDeviceCRL;
    
    @ConfigProp(name = "idm.radsec.TLS.port")
    private String idmRadSecTLSPort;
    
    /*--------------Method---------------*/
    public int getTlsPort() {
        if(StringUtils.isNotBlank(this.idmRadSecTLSPort) && StringUtils.isNumeric(this.idmRadSecTLSPort)) {
            return Integer.parseInt(this.idmRadSecTLSPort);
        } else {
            return DEFAULT_RADSEC_TLS_PORT;
        }
    }
    /**
     * Use the old API for the HOS 6.1r4 before
     */
    public String getOldIdmSelfRegDeviceGuestAPI() {
        return this.getIdmAPIServer() + "/API/guest";
    }
    /*--------------Getter/Setter---------------*/
    
    public String getIdmGatewayServer() {
        return idmGatewayServer;
    }
    
    public String getIdmCertAPI() {
        return idmCertAPI;
    }
    
    public String getIdmCustomerAPI() {
        return idmCustomerAPI;
    }
    
    public String getIdmRadSecTLSPort() {
        return idmRadSecTLSPort;
    }
    
    public void setIdmGatewayServer(String idmGatewayServer) {
        this.idmGatewayServer = idmGatewayServer;
    }
    
    public void setIdmCertAPI(String idmCertAPI) {
        this.idmCertAPI = idmCertAPI;
    }
    
    public void setIdmCustomerAPI(String idmCustomerAPI) {
        this.idmCustomerAPI = idmCustomerAPI;
    }
    
    public void setIdmRadSecTLSPort(String idmRadSecTLSPort) {
        this.idmRadSecTLSPort = idmRadSecTLSPort;
    }

    public String getIdmCertServer() {
        return idmCertServer;
    }

    public String getIdmWebServer() {
        return idmWebServer;
    }

    public void setIdmCertServer(String idmCertServer) {
        this.idmCertServer = idmCertServer;
    }

    public void setIdmWebServer(String idmWebServer) {
        this.idmWebServer = idmWebServer;
    }

    public String getIdmSelfRegDeviceGuestAPI() {
        return idmSelfRegDeviceGuestAPI;
    }

    public String getIdmSelfRegDeviceCRL() {
        return idmSelfRegDeviceCRL;
    }

    public void setIdmSelfRegDeviceGuestAPI(String idmSelfRegDeviceGuestAPI) {
        this.idmSelfRegDeviceGuestAPI = idmSelfRegDeviceGuestAPI;
    }

    public void setIdmSelfRegDeviceCRL(String idmSelfRegDeviceCRL) {
        this.idmSelfRegDeviceCRL = idmSelfRegDeviceCRL;
    }

    public String getSalesforceCreateIDMAPI() {
        return salesforceCreateIDMAPI;
    }

    public void setSalesforceCreateIDMAPI(String salesforceCreateIDMAPI) {
        this.salesforceCreateIDMAPI = salesforceCreateIDMAPI;
    }

    public String getIdmAPIServer() {
        return idmAPIServer;
    }

    public void setIdmAPIServer(String idmAPIServer) {
        this.idmAPIServer = idmAPIServer;
    }
    
}
