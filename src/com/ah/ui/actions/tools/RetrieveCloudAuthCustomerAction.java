package com.ah.ui.actions.tools;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.IDMConfig;
import com.ah.be.common.NmsUtil;
import com.ah.be.license.BeLicenseModule;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.http.HTTPsClientHelper;
import com.ah.util.http.ProxyConfig;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RetrieveCloudAuthCustomerAction extends BaseAction {

    private static final long serialVersionUID = 8443869661859251051L;
    private static final String HTTPS_PREFIX = "https://";
    private static final String REST_PATH = "/API";
    private static final String CUSTOMER_PATH = "/customer";
    private static final Tracer LOG = new Tracer(RetrieveCloudAuthCustomerAction.class.getSimpleName());
    
    private String userName;
    private String password;
    private boolean usingProxy;

    @Override
    public String execute() throws Exception {
        if("retrieve".equals(operation)) {
            try {
                retrieveCustomer();
                return "json";
            } catch (Exception e) {
               LOG.error("Error when do the retrieve operation", e);
               return SUCCESS;
            }
        } else {
            return SUCCESS;
        }
    }
    
    // =========== Method ===========
    private ProxyConfig getProxyConfig() {
        ProxyConfig config = null;
        HMServicesSettings settings = QueryUtil.findBoByAttribute(
                HMServicesSettings.class, "owner.id", getDomain().getId());
        if (null != settings && settings.isEnableProxy() && StringUtils.isNotBlank(settings.getProxyServer())) {
            config = new ProxyConfig(settings.isEnableProxy(),
                    settings.getProxyServer(), settings.getProxyPort(),
                    settings.getProxyUserName(), settings.getProxyPassword());
        }
        return config;
    }
    
    private void retrieveCustomer() throws Exception {
        String api = getIDMCustomerAPI();
        if(null == api) {
            return;
        }
        Status status = Status.SERVICE_UNAVAILABLE;
        ProxyConfig proxyConfig = null;
        try {
            proxyConfig = usingProxy ? getProxyConfig() : null;
            Client client = HTTPsClientHelper.createClient(proxyConfig);
            client.addFilter(new HTTPBasicAuthFilter(userName, getDigetPSW()));
            
            ClientResponse cr = client
                    .resource(UriBuilder.fromUri(api).build())
                    .type(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .get(ClientResponse.class);
            
            status = cr.getClientResponseStatus();
            String reStr = cr.getEntity(String.class);
            LOG.debug("Reponse text for retriving Customer ID: "+ reStr);
            JSONObject jsonObj = new JSONObject(reStr);
            
            handleResponse(status, jsonObj);
            
        } catch (UniformInterfaceException | ClientHandlerException
                | IllegalArgumentException | UriBuilderException
                | JSONException e) {
            String errorMsg;
            if (null != proxyConfig && proxyConfig.isEnabled()
                    && e.getMessage().contains("Connection refused")) {
                errorMsg = MgrUtil
                        .getUserMessage("guadalupe_06.error.idm.retrieveCustomerId.fail.proxy.unavailable",
                                new String[] { proxyConfig.getServerName() + ":" + proxyConfig.getPort() });
            } else {
                if(status.getStatusCode() >= 500) {
                    errorMsg = MgrUtil.getUserMessage(
                            "error.idm.retrieveCustomerId.fail.5xx", new String[] {api, BeLicenseModule.HIVEMANAGER_SYSTEM_ID });
                } else if (status.getStatusCode() >= 400) {
                    errorMsg = MgrUtil.getUserMessage(
                            "error.idm.retrieveCustomerId.fail.4xx", new String[] {api});
                } else {
                    errorMsg = MgrUtil.getUserMessage(
                            "error.idm.retrieveCustomerId.fail", new String[] {api, status.getStatusCode()+"" });
                }
            }
            LOG.error(errorMsg, e);
            putErrorMsg(errorMsg);
        }
        
    }

    private String getDigetPSW() throws Exception {
        return MgrUtil.digest(password);
    }

    private void handleResponse(Status status, JSONObject jsonObj)
            throws JSONException, Exception {
        String errorMsg;
        final int returnCode = jsonObj.optInt("returnCode", -1);
        String message = jsonObj.optString("message", "");
        
        message = convertMessageFromIDM(message);
        
        LOG.info("Retrieve CustomerID --> Response status = "+status);
        if (status == Status.OK) {
            if(returnCode == Status.OK.getStatusCode()) {
                // Success code
                if(jsonObj.isNull("customerId") || null == jsonObj.getString("customerId")) {
                    errorMsg = MgrUtil.getUserMessage("error.idm.retrieveCustomerId.fail.get", BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
                    LOG.error("retrieveCustomer", errorMsg + ", reponse text: " + jsonObj.toString());
                    putErrorMsg(errorMsg);
                    return;
                } else {
                    String customerId = jsonObj.getString("customerId");
                    // successful retrieve the customerId from CA cert server 
                    updateCustomerId(customerId);
                    
                    jsonObject = new JSONObject();
                    jsonObject.put("succ", true);
                }
            } else {
                // Error code
                errorMsg = MgrUtil.getUserMessage("error.idm.retrieveCustomerId.fail.response", BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
                LOG.error(errorMsg + message + " Error Code=" + returnCode + ", reponse text: " + jsonObj.toString());
                putErrorMsg(errorMsg + message);
            }
        } else {
            errorMsg = MgrUtil.getUserMessage("error.idm.retrieveCustomerId.fail.response", BeLicenseModule.HIVEMANAGER_SYSTEM_ID);
            LOG.error(errorMsg + message + " Error Code=" + returnCode + ", reponse text: " + jsonObj.toString());
            putErrorMsg(errorMsg + message);
        }
    }
    
    private String convertMessageFromIDM(String message) {
        if(StringUtils.isNotBlank(message)) {
            if(message.toLowerCase().equals("authentication failure")) {
                message = "Authentication was unsuccessful.";
            }
        }
        return message;
    }

    private String getIDMCustomerAPI() throws JSONException {
        String errorMsg;
        final String methodName = "getCertServer";
        
        IDMConfig radsecConfig = new HmCloudAuthCertMgmtImpl().getRadSecConfig(getDomainId());
        if(null == radsecConfig) {
            errorMsg = MgrUtil.getUserMessage("error.idm.retrieveCustomerId.fail.settings");
            LOG.error(methodName, errorMsg);
            putErrorMsg(errorMsg);
            return null;
        }
        String caWebServer = null;
        String cloudAuthWebServer = radsecConfig.getIdmCustomerAPI();
        if (null != cloudAuthWebServer) {
            if (cloudAuthWebServer.trim().startsWith(HTTPS_PREFIX)) {
                caWebServer = cloudAuthWebServer.trim();
            } else {
                caWebServer = HTTPS_PREFIX + cloudAuthWebServer.trim();
            }
        }
        
        if(null == caWebServer) {
            errorMsg = MgrUtil.getUserMessage("error.idm.retrieveCustomerId.fail.settings.server");
            LOG.error(methodName, errorMsg);
            putErrorMsg(errorMsg);
            return null;
        }
        
        if(!caWebServer.contains(CUSTOMER_PATH)) {
            int index = caWebServer.indexOf(REST_PATH);
            if(-1 == index) {
                errorMsg = MgrUtil.getUserMessage("error.idm.retrieveCustomerId.fail.settings.server.error");
                LOG.error(methodName, errorMsg);
                putErrorMsg(errorMsg);
                return null;
            }
            caWebServer = caWebServer.substring(0, index + REST_PATH.length()) + CUSTOMER_PATH;
        }
        LOG.info("retrieveCustomer", "The URI to retrieve the customer ID :" + caWebServer);
        
        return caWebServer;
    }
    
    private void updateCustomerId(String customerId) throws Exception {
        CloudAuthCustomer customerObj = null;
        // update customer id
        if(NmsUtil.isHostedHMApplication()) {
            // HMOL, per VHM can have private customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", getDomainId());
            if(null == customerObj) {
                customerObj = new CloudAuthCustomer(userName, getDigetPSW(), customerId, getDomain());
                QueryUtil.createBo(customerObj);
            } else {
                customerObj.setUserName(userName);
                customerObj.setPassword(getDigetPSW());
                customerObj.setCustomerId(customerId);
                QueryUtil.updateBo(customerObj);
            }
        } else {
            //Stand Alone, all VHM share one customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class,
                    "owner.domainName", HmDomain.HOME_DOMAIN);
            if(null == customerObj) {
                HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", HmDomain.HOME_DOMAIN);
                customerObj = new CloudAuthCustomer(userName, getDigetPSW(), customerId, domain);
                customerObj.setUsingProxy(usingProxy);
                QueryUtil.createBo(customerObj);
            } else {
                customerObj.setUserName(userName);
                customerObj.setPassword(getDigetPSW());
                customerObj.setCustomerId(customerId);
                customerObj.setUsingProxy(usingProxy);
                QueryUtil.updateBo(customerObj);
            }
        }
    }

    private void putErrorMsg(String msg) throws JSONException {
        if(null == jsonObject) {
            jsonObject = new JSONObject();
        }
        jsonObject.put("err", msg);
    }
    
    private void prepareValues() {
        CloudAuthCustomer customerObj = null;
        if(NmsUtil.isHostedHMApplication()) {
            // HMOL, per VHM can have private customerId
            customerObj  = QueryUtil.findBoById(CloudAuthCustomer.class, domainId);
        } else {
            //Stand Alone, all VHM share one customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class,
                    "owner.domainName", HmDomain.HOME_DOMAIN);
        }
        if (null != customerObj 
                && StringUtils.isNotBlank(customerObj.getUserName())
                && StringUtils.isNotBlank(customerObj.getPassword())) {
            userName = customerObj.getUserName();
            password = customerObj.getPassword();
        }
    }
    // =========== Override ===========
    @Override
    public void prepare() throws Exception {
        super.prepare();
        prepareValues();
        setSelectedL2Feature(L2_FEATURE_CLOUDAUTH_CUSTOMER_RETRIEVE);
    }
    
    @Override
    public String getWriteDisabled() {
        if(NmsUtil.isHostedHMApplication()) {
            // enable for HMOL vHM
            return super.getWriteDisabled();
        } else {
            if(getUserContext().getDomain().isHomeDomain()) {
                return super.getWriteDisabled();
            } else {
                // disable for Stand alone vHM
                return "disabled";
            }
        }
    }
    // =========== Getter/Setter ===========
    public String getUserName() {
        return userName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUsingProxy() {
        return usingProxy;
    }

    public void setUsingProxy(boolean usingProxy) {
        this.usingProxy = usingProxy;
    }
    
}
