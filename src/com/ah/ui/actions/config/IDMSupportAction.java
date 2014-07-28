package com.ah.ui.actions.config;

import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.DomainValidator;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.be.cloudauth.HmCloudAuthCertMgmtImpl;
import com.ah.be.cloudauth.IDMConfig;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ah.util.values.BooleanMsgPair;
import com.ah.ws.rest.client.utils.ClientUtils;
import com.ah.ws.rest.models.idm.IDMSalesforceAccount;
import com.ah.ws.rest.models.idm.IDMTrialGuestAccount;
import com.ah.ws.rest.models.idm.IDMTrialSettings;
import com.ah.ws.rest.models.idm.VHMCustomerInfo;

public class IDMSupportAction extends BaseAction {

    private static final Tracer log = new Tracer(IDMSupportAction.class.getSimpleName());
    private static final long serialVersionUID = 1L;
    
    protected boolean usabledIDM;
    private boolean allowedTrial;

    private String customerFields = "{}"; // plain JSON object
    
    private String firstName;
    private String lastName;
    private String companyName;
    
    private int maxGuests;
    private boolean enabledGuestDirectory;
    private String guestDomainName;
    
    private final HmCloudAuthCertMgmtImpl idmMgmtImpl = new HmCloudAuthCertMgmtImpl();
    
    public String getHelpLink4IDM() {
        String link = NmsUtil.getOEMCustomer().getTellMeMoreIDM();
        if(StringUtils.isBlank(link)) {
            return "#";
        } else {
            return link;
        }
    }
    
    public String getManageGuestLink4IDM() {
        final IDMConfig idmConfig = getIDMConfig();
        if(null == idmConfig || StringUtils.isBlank(idmConfig.getIdmWebServer())){
            return "#";
        } else {
            return idmConfig.getIdmWebServer();
        }
    }
    
    private IDMConfig getIDMConfig() {
        final HmDomain domain = getDomain();
        return null == domain ? null : idmMgmtImpl.getRadSecConfig(domain.getId());
    }
    
    protected void prepareIDMStatus() {
        if(getWritePermission()) {
            this.usabledIDM = idmMgmtImpl.isIDManagerEnabled(getDomain().getId());
            this.allowedTrial = idmMgmtImpl.isAllowedTrial(getDomain().getId());
            
            prepareCustomerFields();
        }
    }
    
    protected void refreshIDMStatus() {
        idmMgmtImpl.refreshIDManagerStatus(getDomain().getId(), getUserContext());
    }
    
    protected void createIDMCustomer() throws JSONException {
        try {
            IDMSalesforceAccount salesforceAccout = initIDMCustomer();
            if(null == salesforceAccout) {
                jsonObject.put("errMsg", getErrorMessage4TrialProcess());
            } else {
                //validate the domain
                if(salesforceAccout.isDirectoryIntegration()) {
                    final String domain = salesforceAccout.getDomain();
                    if(StringUtils.isBlank(domain) || !DomainValidator.getInstance().isValid(domain)) {
                        jsonObject.put("errMsg", MgrUtil.getUserMessage("error.common.invalid", "Domain"));
                        return;
                    }
                }
                
                final IDMConfig idmConfig = idmMgmtImpl.getRadSecConfig(getDomain().getId());
                ClientUtils.getSalesforceResUtils().createCustomer(
                        idmConfig.getSalesforceCreateIDMAPI(), salesforceAccout);
                
                refreshCostomerStatus();
                // update the tried field
                updateTrialStatus();
                
                jsonObject.put("succ", true);
                jsonObject.put("idmWeb", idmConfig.getIdmWebServer());
                
                generateAuditLog(HmAuditLog.STATUS_SUCCESS,
                        (null == getUserContext() ? "" : getUserContext().getUserName())
                                + " request a trial for " + getDomain().getDomainName() + " successfully.");
            }
        } catch (Exception e) {
            log.error("createIDMCustomer", "Error when invoke Salesforce to create the IDM Account.", e);
            jsonObject.put("errMsg", getErrorMessage4TrialProcess());
            
            generateAuditLog(HmAuditLog.STATUS_FAILURE,
                    (null == getUserContext() ? "" : getUserContext().getUserName())
                    + " request a trial for " + getDomain().getDomainName() + " fail. " + MgrUtil.getUserMessage(e));
        }
    }

    private void refreshCostomerStatus() {
        MgrUtil.setSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY, null);
        refreshIDMStatus();
    }
    
    protected void prepareCustomerFields() {
        
        JSONObject obj = new JSONObject();
        Object sessionObj = MgrUtil.getSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY);
        if(null != sessionObj && sessionObj instanceof VHMCustomerInfo) {
            VHMCustomerInfo vhmCustomer = (VHMCustomerInfo)sessionObj;
            if(StringUtils.isBlank(vhmCustomer.getPrimaryEmail()) || StringUtils.isBlank(vhmCustomer.getFirstName())
                    || StringUtils.isBlank(vhmCustomer.getLastName()) || StringUtils.isBlank(vhmCustomer.getCompanyName())) {
                try {
                    obj.put("email", vhmCustomer.getPrimaryEmail());
                    obj.put("firstName", replaceSpecialChars(vhmCustomer.getFirstName()));
                    obj.put("lastName", replaceSpecialChars(vhmCustomer.getLastName()));
                    obj.put("companyName", replaceSpecialChars(vhmCustomer.getCompanyName()));
                } catch (JSONException e) {
                    log.warn("prepareCustomerFields", "Error for the convert the Customer info to JSON.");
                }
            }
        }
        customerFields = obj.toString();
        log.debug("prepareCustomerFields", "the customerFields="+customerFields);
    }

    protected void completeCustomerInfo() throws Exception {
        log.debug("completeCustomerInfo", "firstName="+firstName+", lastName="+lastName+", companyName="+companyName);
        if(StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName) || StringUtils.isBlank(companyName)) {
            jsonObject.put("errMsg", "Please fill the required fields");
        } else {
            Object sessionObj = MgrUtil.getSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY);
            if(null != sessionObj && sessionObj instanceof VHMCustomerInfo) {
                VHMCustomerInfo vhmCustomer = (VHMCustomerInfo)sessionObj;
                vhmCustomer.setFirstName(firstName);
                vhmCustomer.setLastName(lastName);
                vhmCustomer.setCompanyName(companyName);
                
                BooleanMsgPair result = ClientUtils.getPortalResUtils().updateVHMCustomerInfo(vhmCustomer);
                if(result.getValue()) {
                    if(getTrialSettigns()) {
                        jsonObject.put("email", vhmCustomer.getPrimaryEmail());
                        jsonObject.put("succ", true);
                    } else {
                        log.error("completeCustomerInfo", "Unable the get the Trial Settings.");
                        jsonObject.put("errMsg", getErrorMessage4TrialSettings());
                    }
                } else {
                    log.error("completeCustomerInfo", "Unable update the customer information to Portal.");
                    jsonObject.put("errMsg", getErrorMessage4TrialProcess());
                }
            } else {
                log.error("completeCustomerInfo", "Unable to get the customer information from session.");
                jsonObject.put("errMsg", "Unknow error.");
            }
        }
    }
    
    protected boolean getTrialSettigns() throws Exception, JSONException {
        boolean flag = false;
        final Long domainId = getDomain().getId();
        CloudAuthCustomer idmCustomer = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domainId);
        if(null == idmCustomer) {
            log.error("getTrialSettigns", "Unable to get the domain ID="+domainId +" related IDM information from database.");
            jsonObject.put("errMsg", getErrorMessage4TrialSettings());
        } else {
            IDMTrialSettings settings = ClientUtils.getPortalResUtils().getIDMTrialSettings();
            if(null == settings) {
                // get the last one from database
                if(null == idmCustomer || StringUtils.isBlank(idmCustomer.getTrialSettingsText())) {
                    log.error("getTrialSettigns", "Unable to get trial setting from the domain ID="+domainId);
                    jsonObject.put("errMsg", getErrorMessage4TrialSettings());
                } else {
                    jsonObject.put("settings", idmCustomer.getTrialSettingsText());
                    flag = true;
                }
            } else {
                if(null == settings.getAccountList() || settings.getAccountList().isEmpty()) {
                    log.error("getTrialSettigns", "Unable to get accounts trial setting from Portal");
                    jsonObject.put("errMsg", getErrorMessage4TrialSettings());
                } else {
                    StringBuilder builder = new StringBuilder();
                    builder.append("[");
                    final int size = settings.getAccountList().size();
                    for (int i = 0; i < size; i++) {
                        IDMTrialGuestAccount account = settings.getAccountList().get(i); 
                        builder.append("{");
                        builder.append("'maxguests':"+account.getMaxGuests());
                        builder.append(",desc:'"+account.getDescription()+"'");
                        builder.append("}");
                        if(i < size -1) {
                            builder.append(",");
                        }
                    }
                    builder.append("]");
                    
                    idmCustomer.setTrialSettingsText(builder.toString());
                    jsonObject.put("settings", idmCustomer.getTrialSettingsText());
                    QueryUtil.updateBo(idmCustomer); // update the last option settings
                    
                    flag = true;
                }
            }
            MgrUtil.setSessionAttribute(SessionKeys.IDM_TRIAL_INFO_KEY, settings);
        }
        return flag;
    }
    
    protected IDMSalesforceAccount initIDMCustomer() throws JSONException {
        Object settingsObj = MgrUtil.getSessionAttribute(SessionKeys.IDM_TRIAL_INFO_KEY);
        IDMSalesforceAccount salesforceAccout = null;
        if(null != settingsObj && settingsObj instanceof IDMTrialSettings) {
            IDMTrialSettings settings = (IDMTrialSettings) settingsObj;
            IDMTrialGuestAccount accout = null;
            for (IDMTrialGuestAccount temp : settings.getAccountList()) {
                if(temp.getMaxGuests() == getMaxGuests()) {
                    accout = temp;
                    break;
                }
            }
            if(null == accout) {
                log.error("initIDMCustomer", "Unable to a match maxguests fields from trial settings in session.");
                jsonObject.put("errMsg", getErrorMessage4TrialProcess());
            } else {
                salesforceAccout = new IDMSalesforceAccount();
                // Entitlement
                salesforceAccout.setTotalUsers(accout.getMaxGuests());
                salesforceAccout.setTotalSMSBoughtLifeTime(accout.getSmsAccout());
                salesforceAccout.setDirectoryIntegration(isEnabledGuestDirectory());
                if(salesforceAccout.isDirectoryIntegration()) {
                    salesforceAccout.setDomain(getGuestDomainName());
                }
                
                Calendar start = Calendar.getInstance(TimeZone.getTimeZone(userContext.getTimeZone()));
                String subscriptionStartDate = getDateStrForRequestSalesforce(start.getTimeInMillis());
                start.add(Calendar.DAY_OF_YEAR, settings.getTrialPeriod());
                String subscriptionEndDate = getDateStrForRequestSalesforce(start.getTimeInMillis());
                
                salesforceAccout.setSubscriptionStartDate(subscriptionStartDate);
                salesforceAccout.setSubscriptionEndDate(subscriptionEndDate);
                
                // Customer
                Object sessionObj = MgrUtil.getSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY);
                if(null != sessionObj && sessionObj instanceof VHMCustomerInfo) {
                    VHMCustomerInfo vhmCustomer = (VHMCustomerInfo)sessionObj;
                    salesforceAccout.setName(vhmCustomer.getCompanyName());
                    salesforceAccout.setIndustry(vhmCustomer.getIndustry());
                    salesforceAccout.setFirstName(vhmCustomer.getFirstName());
                    salesforceAccout.setLastName(vhmCustomer.getLastName());
//                    salesforceAccout.setPrimaryEmail(vhmCustomer.getPrimaryEmail());
                    salesforceAccout.setCurrentAdminEmail(vhmCustomer.getEmail());
                    salesforceAccout.setPhone(vhmCustomer.getPhoneNumber());
                    salesforceAccout.setTitle(vhmCustomer.getJobTitle());
                    salesforceAccout.setState(vhmCustomer.getState());
                    salesforceAccout.setCountry(vhmCustomer.getCountry());
					
                    // add in Gotham for ehhancement IDM trial
					List<String> emails = new ArrayList<>();
					if (vhmCustomer.getPrimaryUsers() != null && !vhmCustomer.getPrimaryUsers().isEmpty()) {
						emails.addAll(vhmCustomer.getPrimaryUsers());
					}
					if (vhmCustomer.getNonPrimaryUsers() != null && !vhmCustomer.getNonPrimaryUsers().isEmpty()) {
						emails.addAll(vhmCustomer.getNonPrimaryUsers());
					}
					salesforceAccout.setEmails(emails);
					salesforceAccout.setCid(vhmCustomer.getCustomerId());
                }
            }
        } else {
            log.error("initIDMCustomer", "Unable to get the trial settings in session.");
            jsonObject.put("errMsg", getErrorMessage4TrialProcess());
        }
        return salesforceAccout;
    }

    protected void prepareTrialSettings() throws Exception, JSONException {
        Object sessionObj = MgrUtil.getSessionAttribute(SessionKeys.VHM_CUSTOMER_INFO_KEY);
        if(null != sessionObj && sessionObj instanceof VHMCustomerInfo) {
            VHMCustomerInfo vhmCustomer = (VHMCustomerInfo)sessionObj;
            if(getTrialSettigns()) {
                jsonObject.put("email", vhmCustomer.getPrimaryEmail());
                jsonObject.put("succ", true);
            } else {
                jsonObject.put("errMsg", getErrorMessage4TrialSettings());
            }
        } else {
            jsonObject.put("errMsg", getErrorMessage4TrialProcess());
        }
    }
    
    protected void updateTrialStatus() throws Exception {
        CloudAuthCustomer idmCustomer = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", getDomain().getId());
        if(null == idmCustomer) {
            log.error("updateTrialStatus", "Unable to get the domain ID="+domainId +" related IDM information from database.");
            jsonObject.put("errMsg", getErrorMessage4TrialProcess());
        } else {
            if(StringUtils.isBlank(idmCustomer.getIdmanagerId())) {
                log.error("updateTrialStatus", "Unable to get the domain ID="+domainId 
                        +" related IDM information from database: ID Manager should not be empty.");
                throw new Exception("Unable to update the trial status because the ID Manager is empty.");
            } else {
                idmCustomer.setTried(true);
                QueryUtil.updateBo(idmCustomer);
                log.debug("updateTrialStatus", "Update the domain ID="+domainId +" related IDM information from database.");
            }
        }
    }
    
    private String getDateStrForRequestSalesforce(long dateLong) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("yyyyMMdd HHmmss.SSS");
        timeFormat.setTimeZone(TimeZone.getTimeZone("US/Eastern"));
        return timeFormat.format(new Date(dateLong));
    }
    
    private String getErrorMessage4TrialProcess() {
        return MgrUtil.getUserMessage("error.cloudauth.complete.customer") + ","  + getCantactInfo();
    }

    private String getErrorMessage4TrialSettings() {
        return MgrUtil.getUserMessage("error.cloudauth.idm.trailSettings") + "," + getCantactInfo();
    }
    
    private String getCantactInfo() {
        return "<ul class=\"contactList\"><li>by email: <span><a href=\"mailto:"
                + NmsUtil.getOEMCustomer().getSupportMail() + "\">"
                + NmsUtil.getOEMCustomer().getSupportMail()
                + "</a></span></li><li>by phone: <span>"
                + NmsUtil.getOEMCustomer().getSupportPhoneNumberUS() + " (US)"
                + "<br><span class=\"interPhone\">"
                + NmsUtil.getOEMCustomer().getSupportPhoneNumber()
                + " (International)" + "</span></span></li></ul>";
    }

    private String replaceSpecialChars(String name) {
        if(null == name) {
            return "";
        } else {
            StringBuilder newNameBuilder = new StringBuilder();
            StringCharacterIterator iterator = new StringCharacterIterator(name);
            char c = iterator.current();
            while(c != CharacterIterator.DONE) {
                if(c == '\\') {
                    newNameBuilder.append("\\\\");
                } else if(c == '\b') {
                    newNameBuilder.append("\\b");
                } else if(c == '\f') {
                    newNameBuilder.append("\\f");
                } else if(c == '\n') {
                    newNameBuilder.append("\\n");
                } else if(c == '\r') {
                    newNameBuilder.append("\\r");
                } else if(c == '\t') {
                    newNameBuilder.append("\\t");
                } else {
                    newNameBuilder.append(c);
                }
                c = iterator.next();
            }
            return newNameBuilder.toString();
        }
    }
    
    /*--------- Getter/Setter ---------*/
    public String getCustomerFields() {
        return customerFields;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public boolean isEnabledGuestDirectory() {
        return enabledGuestDirectory;
    }

    public String getGuestDomainName() {
        return guestDomainName;
    }

    public void setCustomerFields(String customerFields) {
        this.customerFields = customerFields;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public void setEnabledGuestDirectory(boolean enabledGuestDirectory) {
        this.enabledGuestDirectory = enabledGuestDirectory;
    }

    public void setGuestDomainName(String guestDomainName) {
        this.guestDomainName = guestDomainName;
    }
    
    public boolean isUsabledIDM() {
        return usabledIDM;
    }
    
    public boolean isAllowedTrial() {
        return allowedTrial;
    }
}
