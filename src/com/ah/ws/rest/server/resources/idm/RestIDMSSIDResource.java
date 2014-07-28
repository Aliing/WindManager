package com.ah.ws.rest.server.resources.idm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.mgmt.SortParams;
import com.ah.bo.wlan.Cwp;
import com.ah.bo.wlan.SsidProfile;
import com.ah.util.Tracer;
import com.ah.ws.rest.models.ExceptionModel;
import com.ah.ws.rest.models.idm.IDMSSID;
import com.ah.ws.rest.server.auth.exception.ApplicationException;
import com.ah.ws.rest.server.resources.providers.RootExceptionMapper;

@ApplicationPath(value = "api")
@Path(value = "idm")
public class RestIDMSSIDResource {

    private static final Tracer LOG = new Tracer(RestIDMSSIDResource.class.getSimpleName());
    
    private static final ApplicationException HTTP_404_RESPONSE = new ApplicationException(
            Status.NOT_FOUND, new ExceptionModel(Status.NOT_FOUND, "The requested resource is not available."));

    /**
     * Provide an API for ID Manager to get the SSIDs which are enabled ID Manager.<br/>
     * And this API is only available for HMOL.<br/>
     * Unable to use the regex in @Path("{vhmid: VHM-[a-zA-Z0-9]{6}}/ssids/") due to the provider {@link RootExceptionMapper}.
     * <p>
     * About the return field - authType, the authentication type defined in IDM:
     * <ul>
     * <li>default - 0</li>
     * <li>802.1x - 1</li>
     * <li>PPSK - 2</li>
     * <li>Anonymous - 3</li>
     * </ul>
     * </p> 
     * 
     * @author Yunzhi Lin
     * - Time: Apr 22, 2013 1:22:37 PM
     * @param vhmId
     * @return success - [{name: 'idm-ssid', authType: '1', defUPID: '1'}]; or failure - {status: '', message: ''}
     * @throws ApplicationException
     */
    @GET
    @Path("{vhmid}/ssids/")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<IDMSSID> getIDMSSIDs(@PathParam("vhmid") String vhmId)
            throws ApplicationException {
        if (!NmsUtil.isHostedHMApplication()) {
            LOG.error("The API is only available for HMOL.");
            throw HTTP_404_RESPONSE;
        }
        if (StringUtils.isNotBlank(vhmId)
                && !Pattern.matches("^VHM-[a-zA-Z0-9]{6}$", vhmId)) {
            LOG.error("The VHM-ID is invalid: "+vhmId);
            throw HTTP_404_RESPONSE;
        }
        
        List<IDMSSID> ssids = new ArrayList<>();
        try {
            HmDomain domain = QueryUtil.findBoByAttribute(HmDomain.class, "vhmID", vhmId);
            if (null == domain) {
                LOG.error("Unable to find the domain for VHM-ID: "+vhmId);
                throw HTTP_404_RESPONSE;
            }
            // find out the SSIDs which are applied to devices
            String sql = "SELECT distinct(ssid_profile_id) from config_template_ssid a, hive_ap b where b.owner = "
                    + domain.getId()
                    + " and ssid_profile_id is not null and a.config_template_id = b.template_id";
            List<?> idmSSIDs = QueryUtil.executeNativeQuery(sql);
            
            if(!idmSSIDs.isEmpty()) {
                List<Long> convertIds = new ArrayList<>();
                for (Object id : idmSSIDs) {
                    convertIds.add(Long.parseLong(id.toString()));
                }
                List<SsidProfile> ssidList = QueryUtil.executeQuery(SsidProfile.class,
                        new SortParams("ssid"),
                        new FilterParams("id in (:s1) and enabledIDM = :s2", new Object[]{convertIds, true}), domain.getId(),
                        new QueryBo() {
                    @Override
                    public Collection<HmBo> load(HmBo bo) {
                        if (bo instanceof SsidProfile) {
                            SsidProfile ssid = (SsidProfile) bo;

                            if (null != ssid.getUserProfileDefault()) {
                                ssid.getUserProfileDefault().getAttributeValue();
                            }
                            if (null != ssid.getCwp()) {
                                ssid.getCwp().getRegistrationType();
                            }
                        }
                        return null;
                    }
                });
                
                for (SsidProfile ssidProfile : ssidList) {
                    IDMSSID ssid = new IDMSSID(ssidProfile.getSsid(), 
                            getAuthType(ssidProfile),
                            null == ssidProfile.getUserProfileDefault() ? 0
                                    : ssidProfile.getUserProfileDefault()
                                    .getAttributeValue());
                    ssids.add(ssid);
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to find the SSIDs for VHM-ID: "+vhmId, e);
        }
        return ssids;
    }

    private short getAuthType(SsidProfile ssidProfile) {
        IDMAuthType authType = IDMAuthType.AUTH_DEFAULT;
        
        final int accessMode = ssidProfile.getAccessMode();
        final Cwp cwp = ssidProfile.getCwp();
        if (accessMode == SsidProfile.ACCESS_MODE_PSK) {
            authType = IDMAuthType.AUTH_PPSK;
        } else if (accessMode == SsidProfile.ACCESS_MODE_OPEN && null != cwp
                && cwp.getRegistrationType() == Cwp.REGISTRATION_TYPE_EULA) {
            authType = IDMAuthType.AUTH_ANONYMOUS;
        } else if (accessMode == SsidProfile.ACCESS_MODE_8021X || null != cwp) {
            authType = IDMAuthType.AUTH_8021X;
        }
        return (short) authType.getType();
    }
    
    private enum IDMAuthType {
        AUTH_DEFAULT(0), AUTH_8021X(1), AUTH_PPSK(2), AUTH_ANONYMOUS(3);
        private int type;
        private IDMAuthType(int type) {
            this.type = type;
        }
        public int getType() {
            return type;
        }
        
    }
}
