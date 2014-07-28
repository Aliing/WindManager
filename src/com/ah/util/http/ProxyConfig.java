package com.ah.util.http;

public class ProxyConfig {
    private boolean enabled;
    
    private String serverName;
    private int port;
    private String proxyUser;
    private String proxyPasswd;
    
    public ProxyConfig(boolean enabled, String serverName, int port, String proxyUser, String proxyPasswd) {
        this.enabled= enabled; 
        this.serverName = serverName;
        this.port = port; 
        this.proxyUser = proxyUser;
        this.proxyPasswd = proxyPasswd;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public String getServerName() {
        return serverName;
    }
    public int getPort() {
        return port;
    }
    public String getProxyUser() {
        return proxyUser;
    }
    public String getProxyPasswd() {
        return proxyPasswd;
    }
}