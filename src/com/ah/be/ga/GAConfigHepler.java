package com.ah.be.ga;

import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.GuestAnalyticsInfo;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.cloudauth.CloudAuthCustomer;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.util.Tracer;

public class GAConfigHepler implements IGAConfigHepler {

    private final Tracer LOG = new Tracer(GAConfigHepler.class.getSimpleName());
    private final boolean HMOL_FLAG = NmsUtil.isHostedHMApplication();
    
    private CloudAuthCustomer customer;
    private GuestAnalyticsInfo gaInfo;
    private boolean enabledBeta;
    
    public GAConfigHepler(long domainId) {
        try {
            if (HMOL_FLAG) {
                // HMOL
                customer = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.id", domainId);
                gaInfo = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.id", domainId);
                enabledBeta = isEnableBetaService(new FilterParams("owner.id", domainId));
            } else {
                // Stand Alone
                customer = QueryUtil.findBoByAttribute(CloudAuthCustomer.class, "owner.domainName",
                        HmDomain.HOME_DOMAIN);
                gaInfo = QueryUtil.findBoByAttribute(GuestAnalyticsInfo.class, "owner.domainName",
                        HmDomain.HOME_DOMAIN);
                enabledBeta = isEnableBetaService(new FilterParams("owner.domainName", HmDomain.HOME_DOMAIN));
            }
            LOG.info("init social analytics hepler successfully");
        } catch (Exception e) {
            LOG.error("init social analytics hepler fail...", e);
        }
    }
    
    private boolean isEnableBetaService(FilterParams filter) {
        boolean betaFlag = false;
        List<?> betaFlags = QueryUtil.executeQuery(
                "SELECT enabledBetaIDM FROM " + HMServicesSettings.class.getSimpleName(), 
                null, filter, (Long) null, 1);
        if (betaFlags != null && !betaFlags.isEmpty()) {
            betaFlag = (Boolean) betaFlags.get(0);
        }
        return betaFlag;
    }
    
    @Override
    public String getRootURL() {
        return GAConfigUtil.getACCPURLRoot(enabledBeta);
    }

    @Override
    public String getHealthCheckURL() {
        return GAConfigUtil.getACCPCheckServiceAPI(enabledBeta);
    }
    
    @Override
    public String getWebAccessURL() {
        return GAConfigUtil.getACCPWebAccessURL(enabledBeta);
    }
    
    @Override
    public int getServiceId() {
        return 1;
    }

    @Override
    public String getCustomerId() {
        return null == customer ? null : customer.getCustomerId();
    }

    @Override
    public String getAPIKey() {
        return null == gaInfo ? null : gaInfo.getApiKey();
    }

    @Override
    public String getAPINonce() {
        return null == gaInfo ? null : gaInfo.getApiNonce();
    }

}
