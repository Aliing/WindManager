package com.ah.util.http;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.lang.StringUtils;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.client.apache.ApacheHttpClient;
import com.sun.jersey.client.apache.config.DefaultApacheHttpClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;

public class HTTPsClientHelper {
    
    public static ClientConfig configureClient() {
        return configureClient(null);
    }
    
    public static ClientConfig configureClient(ProxyConfig proxyConf) {
        TrustManager[ ] certs = new TrustManager[ ] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                }
        };
        SSLContext ctx = null;
        try {
            ctx = SSLContext.getInstance("TLS");
            ctx.init(null, certs, new SecureRandom());
        } catch (java.security.GeneralSecurityException ex) {
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
        
        DefaultApacheHttpClientConfig config = new DefaultApacheHttpClientConfig();
        if(null != proxyConf && proxyConf.isEnabled()) {
            config.getProperties().put(
                    DefaultApacheHttpClientConfig.PROPERTY_PROXY_URI,
                    "http://" + proxyConf.getServerName() + ":"+ proxyConf.getPort());
            // using authentication
            if(StringUtils.isNotBlank(proxyConf.getProxyUser())) {
                config.getState().setProxyCredentials(AuthScope.ANY_REALM,
                        proxyConf.getServerName(), proxyConf.getPort(),
                        proxyConf.getProxyUser(), proxyConf.getProxyPasswd());
            }
        }
        
        try {
            config.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, new HTTPSProperties(
                new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                }, 
                ctx
            ));
        } catch(Exception e) {
        }
        return config;
    }
    
    public static Client createClient() {
        return ApacheHttpClient.create(HTTPsClientHelper.configureClient());
    }
    
    public static Client createClient(ProxyConfig proxyConf) {
        return ApacheHttpClient.create(HTTPsClientHelper.configureClient(proxyConf));
    }
}