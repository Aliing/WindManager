package com.ah.be.ls.processor2;

public class ProxySetting {
	private boolean enableProxy;

	private String proxyHost;

	private int proxyPort;

	private String proxyUsername;

	private String proxyPassword;

	public ProxySetting(boolean enableProxy, String proxyHost, int proxyPort, String proxyUsername,
			String proxyPassword) {
		this.enableProxy = enableProxy;
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUsername = proxyUsername;
		this.proxyPassword = proxyPassword;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getProxyUsername() {
		return proxyUsername;
	}

	public void setProxyUsername(String proxyUsername) {
		this.proxyUsername = proxyUsername;
	}

	public String getProxyPassword() {
		return proxyPassword;
	}

	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}

	public void setEnableProxy(boolean enableProxy) {
		this.enableProxy = enableProxy;
	}

	public boolean isEnableProxy() {
		return enableProxy;
	}

}
