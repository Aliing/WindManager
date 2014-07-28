package com.ah.be.cloudauth;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.ssl.PEMItem;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.pkcs.CertificationRequestInfo;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.app.HmBeCommunicationUtil;
import com.ah.be.cloudauth.exception.HmCloudAuthException;
import com.ah.be.cloudauth.result.CloudAuthParam;
import com.ah.be.cloudauth.result.HmCloudAuthCertResult;
import com.ah.be.cloudauth.result.UpdateCAStatus;
import com.ah.be.common.AhDirTools;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapClientResultEvent;
import com.ah.be.communication.event.BeRadSecCertCreationEvent;
import com.ah.be.communication.event.BeRadSecCertCreationResultEvent;
import com.ah.be.license.BeLicenseModule;
import com.ah.be.rest.client.models.CustomerModel;
import com.ah.be.rest.client.services.CustomerService;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.http.HttpCommunication;
import com.ah.util.http.ProxyConfig;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;

public class HmCloudAuthCertMgmtImpl implements ICloudAuthCertMgmt<HmCloudAuthCertResult>, Serializable {

    private static final long serialVersionUID = 1L;

	private static final Tracer LOG = new Tracer(HmCloudAuthCertMgmtImpl.class.getSimpleName());

    private static final String ORGANIZATION = "Aerohive Networks, Inc.";
    private static final String COUNTRY = "US";
    private static final String CERTIFICATE_REQUEST = "CERTIFICATE REQUEST";

    private static final String CLOUTHAUTH_CERTIFICATE_HOME = "/cloudauthca";
    public static final String CERTIFICATE_NAME = "CloudAuth-CA.pem";

    private static final String REST_PATH = "/rest";
    private static final String CERTIFICATION_REST_PATH = "/certificate.json";
    
    private String CLOUDAUTH_CERT_SERVER = null;
    
    private static final boolean HMOL_FLAG = NmsUtil.isHostedHMApplication();
    
    private String certificationContent;
    
    // The filed should be set (retrieve from CloudAuth)
    private String CUSTOMER_ID = null; // "22"
    
    private ProxyConfig proxyConfig = null;
    
    public HmCloudAuthCertMgmtImpl() {
        // empty constructor
    }

    @Override
    public HmCloudAuthCertResult updateCertification(HiveAp device) {
        return updateCertification(device, false);
    }

    @Override
    public HmCloudAuthCertResult forceUpdateCertification(HiveAp device) {
        return updateCertification(device, true);
    }
    
    @Override
    public HmCloudAuthCertResult renewCertificationByRequest(HiveAp device,
            BeRadSecCertCreationResultEvent response) {
        HmCloudAuthCertResult result = new HmCloudAuthCertResult();
        LOG.info("renewCertificationByRequest", "*** start acquire CloudAuth cert. ***");
        try {
            CUSTOMER_ID = initCustomerId(device.getOwner().getId());
            LOG.info("renewCertificationByRequest", "finish init customer Id.");
            
            result = acquireCertFromIDM(device, response);
        } catch (HmCloudAuthException e) {
            result = new HmCloudAuthCertResult(e.getErrorStatus(), e.getExternalErrMsg());
        }
        LOG.info("renewCertificationByRequest", "*** end acquire CloudAuth cert. ***");
        return result;
    }
    
    @Override
    @Deprecated
    public boolean isCustomerIdExist(Long domainId) {
        LOG.info("isCustomerIdExist", "*** start check whether is the customerID exist. ***");
        boolean flag = false;
        try {
            initCustomerId(domainId);
            flag = true;
        } catch (HmCloudAuthException e) {
            LOG.error("isCustomerIdExist", "Error the check the customer ID." + e);
        }
        LOG.info("isCustomerIdExist", "*** end check whether is the customerID exist. ***");
        return flag;
    }
    
    @Override
    public boolean isIDManagerEnabled(Long domainId) {
        LOG.info("isIDManaagerEnabled", "*** start check whether is the ID Manager available. ***");
        boolean flag = false;
        try {
            if (null == domainId) {
                LOG.error("isIDManagerEnabled", "Error when check the ID Manager. The domain ID should not be null.");
                return false;
            }
            getIDManagerStatusFromDB(domainId);
            flag = true;
        } catch (HmCloudAuthException e) {
            LOG.error("isIDManaagerEnabled", "Error the check the ID Manager." + e.getMessage());
        }
        LOG.info("isIDManaagerEnabled", "The ID Manager is " + (flag ? "available" : "unavailable")  + " domain which id = "+domainId);
        LOG.info("isIDManaagerEnabled", "*** end check whether is the ID Manager available. ***");
        return flag;
    }

    @Override
    public boolean isAllowedTrial(Long domainId) {
        LOG.info("isAllowedTrial", "*** start check whether is allowed request the ID Manager trial. ***");
        boolean flag = false;
        try {
            if (null == domainId) {
                LOG.error("isAllowedTrial", "Error when check the ID Manager trial status. The domain ID should not be null.");
                return false;
            }
            if (HMOL_FLAG) {
                // HMOL, per VHM is allowed to send a request to Portal if the idmanager ID is empty and hasn't try yet
                CloudAuthCustomer customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domainId);
                if (null != customerObj && StringUtils.isBlank(customerObj.getIdmanagerId())/* && !customerObj.isTried()*/) {
                    // Fixed Bug 32775: now allow to re-trial (actually it is re-enable IDM service)
                    flag = true;
                }
            } else {
                LOG.info("isAllowedTrial", "The ID Manager Trial is unavailable for the On-Premise HM.");
            }
            
        } catch (Exception e) {
            LOG.error("isAllowedTrial", "Error the check the ID Manager." + e.getMessage());
        }
        LOG.info("isAllowedTrial", "The ID Manager Trial is " + (flag ? "available" : "unavailable")  + " domain which id = "+domainId);
        LOG.info("isAllowedTrial", "*** end check whether is allowed request the ID Manager trial. ***");
        return flag;
    }
    
    @Override
    public IDMConfig getRadSecConfig(Long domainId) {
        LOG.info("getRadSecConfig", "*** start get the IDM RadSec config. ***");
        try {
            if (null == domainId) {
                LOG.error("getRadSecConfig",
                        "Error when get the RadSec Configuration. The domain ID should not be null.");
                return null;
            }
            List<?> list;
            if (HMOL_FLAG) {
                // HMOL, per VHM can change to standard or beta IDM
                list = QueryUtil.executeNativeQuery(
                        "select enabledBetaIDM from hmservicessettings where owner = " + domainId, 1);
            } else {
                // Stand Alone, all VHM share one customerId
                list = QueryUtil.executeQuery("select enabledBetaIDM from "
                        + HMServicesSettings.class.getSimpleName(), null, new FilterParams(
                        "owner.domainName", HmDomain.HOME_DOMAIN));
            }
            if (list.isEmpty()) {
                LOG.error("getRadSecConfig", "Unable to query the flag in database. Domain ID = " + domainId);
                return null;
            } else {
                return IDMConfigUtil.getRadSecConfig(Boolean.parseBoolean(list.get(0).toString()));
            }
        } catch (Exception e) {
            LOG.error("getRadSecConfig", "Error when get the RadSec Configuration.", e);
        }
        LOG.info("getRadSecConfig", "*** end get the IDM RadSec config. ***");
        return null;
    }
    
    @Override
    @Deprecated
    public void retrieveCustomerIdFromPortal(Long domainId) {
        final String methodName = "retrieveCustomerIdFromPortal";
        LOG.info(methodName, "*** start retrieve the IDM Customer ID from Myhive. ***");
        try {
            if(null == domainId) {
                LOG.error(methodName, "The domain ID should not be Null.");
                return;
            }
            if(!HMOL_FLAG) {
                LOG.warn(methodName, "The operation only works on HMOL.");
                return;
            }
            CloudAuthCustomer customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domainId);
            if (null == customerObj) {
                updateCustomerId(domainId, customerObj);
            } else {
                if(StringUtils.isBlank(customerObj.getCustomerId())) {
                    LOG.info(methodName, "The CustomerID is empty, need to retrieve from Myhive.");
                    updateCustomerId(domainId, customerObj);
                } else {
                    LOG.info(methodName, "The CustomerID is already exist, no need to retrieve from Myhive.");
                }
            }
        } catch (Exception e) {
            LOG.error(methodName, "Error when retrieve from MyHive", e);
        } finally {
            LOG.info(methodName, "*** end retrieve the IDM Customer ID from Myhive. ***");
        }
        return;
    }

    @Override
    public boolean refreshIDManagerStatus(Long domainId, HmUser user) {
        boolean succ = false;
        if(HMOL_FLAG) {
            String message = "refresh the ID Manager status from Portal. ";
            try {
                if(null == MgrUtil.getSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY)) {
                    if(null == domainId || null == user) {
                        LOG.error("refreshIDManagerStatus", "Fail to "+ message
                                + "The method parameters should not be null: domainId="
                                + domainId + ", user=" + user);
                    } else {
                        // no customer information in session yet
                        final HmUserGroup userGroup = user.getUserGroup();
                        if(userGroup.getGroupName().equals(HmUserGroup.ADMINISTRATOR)) {
                            // for switch domain
                            updateIDMCustomer(user);
                            succ = true;
                        } else {
                            HmPermission featurePermission = userGroup
                                    .getFeaturePermissions()
                                    .get(Navigation.L2_FEATURE_SSID_PROFILES); //express
                            if(null == featurePermission) {
                                featurePermission = userGroup
                                        .getFeaturePermissions()
                                        .get(Navigation.L2_FEATURE_SSID_PROFILES_FULL); //enterprise
                            }
                            if (featurePermission != null) {
                                if (featurePermission.hasAccess(HmPermission.OPERATION_WRITE)) {
                                    //check the write permission
                                    updateIDMCustomer(user);
                                    succ = true;
                                }
                            }
                        }
                        if(!succ) {
                            LOG.warn("refreshIDManagerStatus", "Stop "+ message
                                    + " Not support for the unconfigurable user.");
                        }
                    }
                } else {
                    LOG.info("refreshIDManagerStatus","No need to " + message);
                }
            } catch (Exception e) {
                LOG.error("refreshIDManagerStatus", "Fail to "+ message, e);
            }
        }
        return succ;
    }

    // =========================== _method to manager the certification files  =======================================//
    private HmCloudAuthCertResult updateCertification(HiveAp device, boolean forceUpdate) {
        HmCloudAuthCertResult result = new HmCloudAuthCertResult();
        LOG.info("updateCertification", "*** start update CloudAuth cert. ***");
        try {
            // initial customer Id
            CUSTOMER_ID = initCustomerId(device.getOwner().getId());
            LOG.info("updateCertification", "finish init customer Id.");
            
            // communication with the device
            BeRadSecCertCreationEvent event = initEvent(device, forceUpdate);
            LOG.info("updateCertification", "finish init event.");
            
            BeRadSecCertCreationResultEvent response = sendSyncQueryDeviceCACert(event);
            LOG.info("updateCertification", "finish sent event to device.");

            result = acquireCertFromIDM(device, response);
            
        } catch (HmCloudAuthException e) {
            result = new HmCloudAuthCertResult(e.getErrorStatus(), e.getExternalErrMsg());
        }
        LOG.info("updateCertification", "*** end update CloudAuth cert. ***");
        return result;
    }

    private HmCloudAuthCertResult acquireCertFromIDM(HiveAp device,
            BeRadSecCertCreationResultEvent response) throws HmCloudAuthException {
        HmCloudAuthCertResult result;
        if(null == response) {
            result = new HmCloudAuthCertResult(UpdateCAStatus.RESPONSE_EVENTE_ERR);
        } else if (null == device) {
            result = new HmCloudAuthCertResult(UpdateCAStatus.DEVICE_NULL_ERR);
        } else if (response.isExist()) {
            result = new HmCloudAuthCertResult(UpdateCAStatus.NOUPDATE);
        } else if (response.isCreateError()) {
            result = new HmCloudAuthCertResult(UpdateCAStatus.CSR_CREATE_ERR);
        } else {
            verifyCSRContent(response, device.getSerialNumber());
            LOG.info("acquireCertFromIDM", "finish verify the CSR content.");
            
            initCloudAuthCertServer(device.getOwner().getId());
            LOG.info("acquireCertFromIDM", "finish init the CA server.");

            // communication with CA server
            JSONObject jsonObj = sentCSR2CAServer(response);
            LOG.info("acquireCertFromIDM", "finish sent the CSR content to CA server.");
            
            vefiryCAServerResponse(device.getSerialNumber(), jsonObj);
            LOG.info("acquireCertFromIDM", "finish verify the response from CA server.");
            
            String certFilePath = initFilePath(device);
            generateCert(certFilePath);
            LOG.info("acquireCertFromIDM", "finish generate certification in hm.");
            
            result = new HmCloudAuthCertResult(CERTIFICATE_NAME, certFilePath);
        }
        return result;
    }
    
    private void updateCustomerId(Long domainId, CloudAuthCustomer customerObj) throws HmCloudAuthException {
        String methodName = "updateCostumerId";
        
        HmDomain domain = QueryUtil.findBoById(HmDomain.class, domainId);
        final String vhmID = domain.getVhmID();
        if (null != domain && StringUtils.isNotBlank(vhmID)) {
            LOG.warn(methodName, "The HMOL vHMID: " + vhmID
                    + " doesn't exist. Try to retrieve from Myhive");
            CustomerService service = new CustomerService();
            CustomerModel customerModel = service.retrieveCustomerId(vhmID);
            if (customerModel.getReturnCode() == 0) {
                LOG.debug(methodName, "Retrieve the CustomerID from Myhive successfully. ID="
                        + customerModel.getCustomerId());
                try {
                    if(null == customerObj) {
                        customerObj = new CloudAuthCustomer("empty", "empty",
                                customerModel.getCustomerId(), domain);
                        QueryUtil.createBo(customerObj);
                    } else {
                        customerObj.setCustomerId(customerModel.getCustomerId());
                        QueryUtil.updateBo(customerObj);
                    }
                } catch (Exception e) {
                    throw new HmCloudAuthException(methodName,
                            UpdateCAStatus.CUSTOMER_ID_UPDATE_ERR, e);
                }
                LOG.debug(methodName, "Update the CustomerID in VHM successfully. vHMID=" + vhmID);
            }
        }
    }
    
    private void updateIDMCustomer(HmUser user) throws Exception {
        final String vhmEmail = user.getEmailAddress();
        final HmDomain switchDomain = user.getSwitchDomain();
        final boolean isSwitched = null != switchDomain;
        final HmDomain domain = isSwitched ? switchDomain : user.getOwner();
        final String vhmID = domain.getVhmID();
        
        LOG.info("updateIDMCustomer", "Update vHM[" + vhmID + "] for" + " the user:" + vhmEmail);
        // send request to portal
        LOG.info("updateIDMCustomer", "*** start send request to Portal to retrieve the vHM customer infomation. ***");
        VHMCustomerInfo vhmCustomer = ClientUtils.getPortalResUtils().getVHMCustomerInfo(isSwitched? vhmID : vhmEmail);
        LOG.debug("updateIDMCustomer", "the response is " + ReflectionToStringBuilder.toString(vhmCustomer));
        LOG.info("updateIDMCustomer", "*** end send request to Portal to retrieve the vHM customer infomation. ***");
        if(null != vhmCustomer) {
            if(isSwitched) {
                // for the switch domain
                vhmCustomer.setEmail(vhmCustomer.getPrimaryEmail());
            } else {
                // for the normal
                vhmCustomer.setEmail(vhmEmail);
            }
            
            // update the status according the response
            LOG.info("updateIDMCustomer", "*** start update ID Manager status in database from response. ***");
            CloudAuthCustomer idmCustomer = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domain.getId());
            if(null == idmCustomer) {
                idmCustomer = new CloudAuthCustomer(vhmCustomer.getCustomerId(), vhmCustomer.getIdmID(), domain);
                QueryUtil.createBo(idmCustomer);
            } else {
                // avoid to update the same values
                idmCustomer.setCustomerId(vhmCustomer.getCustomerId());
                idmCustomer.setIdmanagerId(vhmCustomer.getIdmID());
                QueryUtil.updateBo(idmCustomer);
            }
            LOG.info("updateIDMCustomer", "*** end update ID Manager status in database from response. ***");
            MgrUtil.setSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY, vhmCustomer);
        }
    }

    private String initFilePath(HiveAp device) {
        return AhDirTools.getDownloadsDir() + device.getOwner().getDomainName() + File.separator
                + CLOUTHAUTH_CERTIFICATE_HOME + File.separator + device.getMacAddress()
                + File.separator + CERTIFICATE_NAME;
    }

    private String generateCert(String filePath) throws HmCloudAuthException {
        try {
            if(StringUtils.isBlank(this.certificationContent)) {
                throw new HmCloudAuthException("generateCert", UpdateCAStatus.HTTPS_CERT_CONTENT_ERR);
            }
            File file = new File(filePath);
            if(file.getParentFile().exists()) {
                FileUtils.cleanDirectory(file.getParentFile());
            }
            
            FileUtils.writeStringToFile(file, this.certificationContent);
            
        } catch (IOException e) {
            throw new HmCloudAuthException("generateCert", UpdateCAStatus.HTTPS_CERT_FILE_IO_ERR, e);
        }
        return null;
    }

    private void vefiryCAServerResponse(String deviceSerialNumber, JSONObject jsonObj) throws HmCloudAuthException {
        String methodName = "vefiryCAServerResponse";
        String message = "";
        try {
            if (jsonObj.has(key(CloudAuthParam.RETURN_CODE))) {
                if (jsonObj.getLong(key(CloudAuthParam.RETURN_CODE)) != 5001L
                        && jsonObj.has(key(CloudAuthParam.RETURN_MESSAGE))) {
                    message = "Error reponse from ID Manager: "
                            //+ "[" 
                            //+ jsonObj.optLong(key(CloudAuthParam.RETURN_CODE)) + "] "
                            + jsonObj.optString(key(CloudAuthParam.RETURN_MESSAGE)); 
                    throw new HmCloudAuthException(methodName, message,
                            UpdateCAStatus.IDM_SERVER_ERR);
                }
            } else {
                if (jsonObj.has(key(CloudAuthParam.DEVICE_ID))
                        && jsonObj.optString(key(CloudAuthParam.DEVICE_ID)).equals(deviceSerialNumber)) {
                    return;
                } else {
                    throw new HmCloudAuthException(methodName,
                            UpdateCAStatus.HTTPS_CERT_MISMATCH_ERR);
                }
            }
        } catch (JSONException e) {
            throw new HmCloudAuthException(methodName, UpdateCAStatus.JSON_PARSE_ERR, e);
            
        }
    }
    
    private void initCloudAuthCertServer(Long domainId) throws HmCloudAuthException {
        try {
            IDMConfig config = getRadSecConfig(domainId);
            
            if (null != config && StringUtils.isNotBlank(config.getIdmCertAPI())) {
                String cloudAuthCertServer = config.getIdmCertAPI();
                if (cloudAuthCertServer.trim().startsWith("https://")) {
                    CLOUDAUTH_CERT_SERVER = cloudAuthCertServer.trim();
                } else {
                    CLOUDAUTH_CERT_SERVER = "https://" + cloudAuthCertServer;
                }
                if (!CLOUDAUTH_CERT_SERVER.contains(REST_PATH + CERTIFICATION_REST_PATH)) {
                    int index = CLOUDAUTH_CERT_SERVER.indexOf(REST_PATH);
                    if (index >= 0) {
                        CLOUDAUTH_CERT_SERVER = CLOUDAUTH_CERT_SERVER.substring(0, index
                                + REST_PATH.length())
                                + CERTIFICATION_REST_PATH;
                    }
                }
                LOG.info("The URI to get the CA cert :" + CLOUDAUTH_CERT_SERVER);
            } else {
                throw new HmCloudAuthException("initCloudAuthCertServer", UpdateCAStatus.CONFIG_LOAD_ERR);
            }
        } catch (Exception e) {
            throw new HmCloudAuthException("initCloudAuthCertServer", UpdateCAStatus.REST_INIT_ERR, e);
        }
    }

    private JSONObject sentCSR2CAServer(BeRadSecCertCreationResultEvent result)
            throws HmCloudAuthException {

        try {
            HttpCommunication httpCommunication = new HttpCommunication(CLOUDAUTH_CERT_SERVER);
            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
            // FIXME set the APP_KEY
            parameters.add(new BasicNameValuePair(key(CloudAuthParam.APP_KEY), "caKY9ktJ0BVjo"));
            parameters.add(new BasicNameValuePair(key(CloudAuthParam.HIVEMANAGER_ID),
                    BeLicenseModule.HIVEMANAGER_SYSTEM_ID));
            parameters.add(new BasicNameValuePair(key(CloudAuthParam.CUSTOMER_ID), CUSTOMER_ID));
            parameters.add(new BasicNameValuePair(key(CloudAuthParam.DEVICE_ID), result.getApNoQuery().getSerialNumber()));
            parameters.add(new BasicNameValuePair(key(CloudAuthParam.CSR), new String(result.getCsrContent())));
            
            LOG.info("CustomerId = "+CUSTOMER_ID);
            
            // IDM Proxy
            if(null != proxyConfig) {
                httpCommunication.setEnableProxyFlag(proxyConfig.isEnabled());
                httpCommunication.setProxyHost(proxyConfig.getServerName());
                httpCommunication.setProxyPort(proxyConfig.getPort());
                httpCommunication.setProxyUsername(proxyConfig.getProxyUser());
                httpCommunication.setProxyPassword(proxyConfig.getProxyPasswd());
            }
            
            HttpEntity responseEntity = httpCommunication.sendParams(parameters);
            
            if (null != responseEntity) {
                String value = EntityUtils.toString(responseEntity);
                
                JSONObject jsonObj = parse2JSON(value);
                
                return jsonObj;
            } else {
                throw new HmCloudAuthException("sentCSR2CAServer",
                        UpdateCAStatus.HTTPS_POST_CLOUDAUTH_ERR);
            }
        } catch (Exception e) {
            throw new HmCloudAuthException("sentCSR2CAServer",
                    UpdateCAStatus.HTTPS_POST_CLOUDAUTH_ERR, e);
        }
    }

    private JSONObject parse2JSON(String reponseStr) throws HmCloudAuthException {
        //FIXME get the first JSON object
        String value = reponseStr;
        if(value.startsWith("[{") && value.endsWith("}]")) {
            value = value.substring(value.indexOf("[{")+2, value.indexOf("}]"));
        } else if(value.startsWith("{") && value.endsWith("}")) {
            value = value.substring(value.indexOf("{")+1, value.indexOf("}"));
        } else {
            throw new HmCloudAuthException("parse2JSON", UpdateCAStatus.JSON_PARSE_ERR, reponseStr);
        }
        
        JSONObject jsonObject= new JSONObject();
        
        try (final Scanner scannerO = new Scanner(value);
                Scanner scanner = scannerO.useDelimiter("\",\"")) {
            // split the JSON object by ","
            int index = 0;
            while (scanner.hasNext()) {
                String nextValue = scanner.next();
                if (nextValue.startsWith("\"")) {
                    nextValue = nextValue.substring(1);
                } else if(nextValue.endsWith("\"")) {
                    nextValue = nextValue.substring(0, nextValue.length()-1);
                }
                //LOG.debug("scan-->" + nextValue);
                
                // split to key:value by ":"
                String[] pairValue = nextValue.split("\":\"");
                if(pairValue.length != 2) {
                    throw new HmCloudAuthException("parse2JSON", UpdateCAStatus.JSON_PARSE_ERR, reponseStr);
                }
                String keyStr = pairValue[0];
                String valueStr = pairValue[1];
                
                if(keyStr.equals(key(CloudAuthParam.RETURN_CONTENT))) {
                    this.certificationContent = valueStr;
                } else {
                    jsonObject.put(keyStr, valueStr);
                }
                index++;
            }
            if(index == 0) {
                throw new HmCloudAuthException("parse2JSON", UpdateCAStatus.JSON_PARSE_ERR, reponseStr);
            }
        } catch (JSONException e) {
            throw new HmCloudAuthException("parse2JSON", UpdateCAStatus.JSON_INIT_ERR, e, reponseStr);
        }
        
        return jsonObject;
    }

    @SuppressWarnings("rawtypes")
    private void verifyCSRContent(BeRadSecCertCreationResultEvent result, String commonName)
            throws HmCloudAuthException {
        String methodName = "verifyCSRContent";
        if (result.isCreateError()) {
            throw new HmCloudAuthException(methodName, UpdateCAStatus.CSR_CREATE_ERR);
        }
        if (result.isNeedCreate()) {
            byte[] csrContent = result.getCsrContent();
            final List pemItems = org.apache.commons.ssl.PEMUtil.decode(csrContent);
            if (pemItems.isEmpty()) {
                throw new HmCloudAuthException(methodName, UpdateCAStatus.CSR_DECODE_ERR);
            }

            final PEMItem csrPemItem = (PEMItem) pemItems.get(0);
            if (csrPemItem.pemType.startsWith(CERTIFICATE_REQUEST)) {
                final PKCS10CertificationRequest csr = new PKCS10CertificationRequest(
                        csrPemItem.getDerBytes());
                CertificationRequestInfo requestInfo = csr.getCertificationRequestInfo();
                X509Name subject = requestInfo.getSubject();

                Vector commondNameVector = subject.getValues(X509Name.CN);
                Vector countryVector = subject.getValues(X509Name.C);
                Vector organizationVector = subject.getValues(X509Name.O);
                if (commondNameVector.isEmpty() || countryVector.isEmpty()
                        || organizationVector.isEmpty()) {
                    throw new HmCloudAuthException(methodName, UpdateCAStatus.CSR_FORMAT_ERR);
                }
                if (!commonName.equalsIgnoreCase(commondNameVector.get(0).toString())
                        || !ORGANIZATION.equals(organizationVector.get(0).toString())
                        || !COUNTRY.equals(countryVector.get(0).toString())) {
                    throw new HmCloudAuthException(methodName, UpdateCAStatus.CSR_VERIFY_ERR);
                }
            } else {
                throw new HmCloudAuthException(methodName, UpdateCAStatus.CSR_DECODE_ERR);
            }
        } else {
            throw new HmCloudAuthException(methodName, UpdateCAStatus.CSR_STATUS_ERR);
        }
        return;
    }

    private String initCustomerId(Long domainId) throws HmCloudAuthException {
        CloudAuthCustomer customerObj;
        String customerId = null; 
        if (HMOL_FLAG) {
            // HMOL, per VHM can have private customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domainId);
        } else {
            // Stand Alone, all VHM share one customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.domainName",
                    HmDomain.HOME_DOMAIN);
        }
        if (null != customerObj && StringUtils.isNotBlank(customerObj.getCustomerId())) {
            customerId = customerObj.getCustomerId();
            
            // IDM Proxy support
            if(customerObj.isUsingProxy()) {
                initProxyConfig(domainId);
            }
        }

        if (StringUtils.isBlank(customerId)) {
            throw new HmCloudAuthException("initCustomerId", UpdateCAStatus.CUSTOMER_ID_EMPTY_ERR);
        } else {
            return customerId;
        }
    }
    
    private void initProxyConfig(Long domainId) {
        if(HMOL_FLAG) {
            // do nothing for HMOL
            return;
        }
        HMServicesSettings settings = QueryUtil.findBoByAttribute(
                HMServicesSettings.class, "owner.domainName", HmDomain.HOME_DOMAIN);
        if (null != settings && settings.isEnableProxy() && StringUtils.isNotBlank(settings.getProxyServer())) {
            proxyConfig = new ProxyConfig(settings.isEnableProxy(),
                    settings.getProxyServer(), settings.getProxyPort(),
                    settings.getProxyUserName(), settings.getProxyPassword());
        }
    
    }
    private String getIDManagerStatusFromDB(Long domainId) throws HmCloudAuthException {
        CloudAuthCustomer customerObj;
        String idmanagerId = null; 
        if (HMOL_FLAG) {
            // HMOL, per VHM can have private customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domainId);
            if (null != customerObj
                    && StringUtils.isNotBlank(customerObj.getCustomerId())
                    && StringUtils.isNotBlank(customerObj.getIdmanagerId())) {
                idmanagerId = customerObj.getIdmanagerId();
            }
        } else {
            // Stand Alone, all VHM share one customerId
            customerObj = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.domainName",
                    HmDomain.HOME_DOMAIN);
            if (null != customerObj && StringUtils.isNotBlank(customerObj.getCustomerId())) {
                idmanagerId = customerObj.getCustomerId();
            }
        }
        
        if (StringUtils.isBlank(idmanagerId)) {
            throw new HmCloudAuthException("getIDManagerStatusFromDB", UpdateCAStatus.ID_MANAGER_NOT_AVAILABLE);
        } else {
            return idmanagerId;
        }
    }

    private BeRadSecCertCreationResultEvent sendSyncQueryDeviceCACert(
            BeRadSecCertCreationEvent event) throws HmCloudAuthException {
        BeCommunicationEvent response = HmBeCommunicationUtil.sendSyncRequest(event);
        // communication error
        if (null == response) {
            throw new HmCloudAuthException("sendSyncQueryDeviceCACert", UpdateCAStatus.SENT_EVENT_ERR);
        }
        BeRadSecCertCreationResultEvent caResultEvent = null;
        if (response.getMsgType() == BeCommunicationConstant.MESSAGEELEMENTTYPE_CAPWAPCLIENTEVENTRESULT) {
            BeCapwapClientResultEvent resultEvent = (BeCapwapClientResultEvent) response;
            if (resultEvent.getResultType() == BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_RADSEC_CERT_CREATION) {
                caResultEvent = (BeRadSecCertCreationResultEvent) response;
            }
        }
        if (null == caResultEvent) {
            throw new HmCloudAuthException("sendSyncQueryDeviceCACert", UpdateCAStatus.RESPONSE_EVENTE_ERR);
        }
        return caResultEvent;
    }

    private BeRadSecCertCreationEvent initEvent(HiveAp device, boolean foceUpdate)
            throws HmCloudAuthException {
        BeRadSecCertCreationEvent event = new BeRadSecCertCreationEvent();
        event.setAp(device);
        event.setSequenceNum(HmBeCommunicationUtil.getSequenceNumber());
        if (foceUpdate) {
            event.setCreateFlag(BeRadSecCertCreationEvent.UPDATE_OVERRIDE);
        }
        try {
            event.buildPacket();
        } catch (BeCommunicationEncodeException e) {
            throw new HmCloudAuthException("initEvent", UpdateCAStatus.INIT_EVENT_ERR, e);
        }
        return event;
    }

    private String key(CloudAuthParam param) {
        return param.toString();
    }
}
