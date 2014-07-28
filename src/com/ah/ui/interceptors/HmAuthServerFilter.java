/**
 *@filename		HmAuthServerFilter.java
 *@version
 *@author		Fiona
 *@createtime	2010-3-2 PM 05:08:14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */
/**
 *modfy history*
 *
 */
package com.ah.ui.interceptors;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jasig.cas.client.authentication.DefaultGatewayResolverImpl;
import org.jasig.cas.client.authentication.GatewayResolver;
import org.jasig.cas.client.util.AbstractCasFilter;
import org.jasig.cas.client.util.CommonUtils;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.TicketValidationException;

import com.ah.be.common.NmsUtil;
import com.ah.be.search.PageIndex;

/**
 * @author		Fiona
 * @version		V1.0.0.0
 */
public class HmAuthServerFilter extends AbstractCasFilter {

	public static final String[] NOT_NEED_FILTER_RESOURCE = new String[] {"studentRegist.action", "randValidateCode.action",
			"planTool.action", "mapSettings.action", "mapUpload.action" };

    /**
     * The URL to the CAS Server login.
     */
    private String casServerLoginUrl;

    /**
     * Whether to send the renew request or not.
     */
    private boolean renew = false;

    /**
     * Whether to send the gateway request or not.
     */
    private boolean gateway = false;

    private GatewayResolver gatewayStorage = new DefaultGatewayResolverImpl();

    protected void initInternal(final FilterConfig filterConfig) throws ServletException {
        if (!isIgnoreInitConfiguration()) {
            super.initInternal(filterConfig);
            setCasServerLoginUrl(getPropertyFromInitParams(filterConfig, "casServerLoginUrl", null));
            log.trace("Loaded CasServerLoginUrl parameter: " + this.casServerLoginUrl);
            setRenew(parseBoolean(getPropertyFromInitParams(filterConfig, "renew", "false")));
            log.trace("Loaded renew parameter: " + this.renew);
            setGateway(parseBoolean(getPropertyFromInitParams(filterConfig, "gateway", "false")));
            log.trace("Loaded gateway parameter: " + this.gateway);

            final String gatewayStorageClass = getPropertyFromInitParams(filterConfig, "gatewayStorageClass", null);

            if (gatewayStorageClass != null) {
                try {
                    this.gatewayStorage = (GatewayResolver) Class.forName(gatewayStorageClass).newInstance();
                } catch (final Exception e) {
                    log.error(e,e);
                    throw new ServletException(e);
                }
            }
        }
    }

    public void init() {
        super.init();
        CommonUtils.assertNotNull(this.casServerLoginUrl, "casServerLoginUrl cannot be null.");
    }

    public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;
        final HttpSession session = request.getSession(false);
        final Assertion assertion = session != null ? (Assertion) session.getAttribute(CONST_CAS_ASSERTION) : null;

        setServerName("https://"+ request.getServerName() + ":" + NmsUtil.getWebServerRedirectPort());
        final String serviceUrl = constructServiceUrl(request, response);

        if (assertion != null) {
        	try {
        		filterChain.doFilter(request, response);
        	} catch (ServletException e) {
				if (e.getCause() instanceof TicketValidationException) {
					log.warn("Invalid ticket validation has occurred, redirect to " + serviceUrl, e);
					response.sendRedirect(serviceUrl);
				} else {
					throw e;
				}
			}
            return;
        }

        // does not need this filter for these urls
        for (String urlExp : NOT_NEED_FILTER_RESOURCE) {
        	if (serviceUrl.contains(urlExp)) {
        		try {
        			filterChain.doFilter(request, response);
	        	} catch (ServletException e) {
					if (e.getCause() instanceof TicketValidationException) {
						log.warn("Invalid ticket validation has occurred, redirect to " + serviceUrl, e);
						response.sendRedirect(serviceUrl);
					} else {
						throw e;
					}
				}
            	return;
            }
        }

        /*
		 * to support HM Search - Index, since HM3.5R2
		 */
		if(PageIndex.needBuildIndex() && PageIndex.isLocalRequest(request)) {
		   	try {
		   		filterChain.doFilter(request, response);
			} catch (ServletException e) {
				if (e.getCause() instanceof TicketValidationException) {
					log.warn("Invalid ticket validation has occurred, redirect to " + serviceUrl, e);
					response.sendRedirect(serviceUrl);
				} else {
					throw e;
				}
			}
        	return;
     	}

        final String ticket = CommonUtils.safeGetParameter(request,getArtifactParameterName());
        final boolean wasGatewayed = this.gatewayStorage.hasGatewayedAlready(request, serviceUrl);

        if (CommonUtils.isNotBlank(ticket) || wasGatewayed) {
	    	try {
	    		filterChain.doFilter(request, response);
	    	} catch (ServletException e) {
				if (e.getCause() instanceof TicketValidationException) {
					log.warn("Invalid ticket validation has occurred, redirect to " + serviceUrl, e);
					response.sendRedirect(serviceUrl);
				} else {
					throw e;
				}
			}
            return;
        }

        final String modifiedServiceUrl;

        log.debug("no ticket and no assertion found");
        if (this.gateway) {
            log.debug("setting gateway attribute in session");
            modifiedServiceUrl = this.gatewayStorage.storeGatewayInformation(request, serviceUrl);
        } else {
            modifiedServiceUrl = serviceUrl;
        }

        if (log.isDebugEnabled()) {
            log.debug("Constructed service url: " + modifiedServiceUrl);
        }

        final String urlToRedirectTo = CommonUtils.constructRedirectUrl(this.casServerLoginUrl, getServiceParameterName(), modifiedServiceUrl, this.renew, this.gateway);

        if (log.isDebugEnabled()) {
            log.debug("redirecting to \"" + urlToRedirectTo + "\"");
        }

        response.sendRedirect(urlToRedirectTo);
    }

    public final void setRenew(final boolean renew) {
        this.renew = renew;
    }

    public final void setGateway(final boolean gateway) {
        this.gateway = gateway;
    }

    public final void setCasServerLoginUrl(final String casServerLoginUrl) {
        this.casServerLoginUrl = casServerLoginUrl;
    }

    public final void setGatewayStorage(final GatewayResolver gatewayStorage) {
    	this.gatewayStorage = gatewayStorage;
    }
}
