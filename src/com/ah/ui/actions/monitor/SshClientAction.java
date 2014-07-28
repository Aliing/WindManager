package com.ah.ui.actions.monitor;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.ericdaugherty.sshwebproxy.SshConnectException;
import com.ericdaugherty.sshwebproxy.SshConnectionFactory;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class SshClientAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	private static final Tracer log = new Tracer(SshClientAction.class
			.getSimpleName());

	private Long leafNodeId;
	private Long hiveApId;
	private String hostname;
	private String host = "127.0.0.1";
//	private String user;
//	private String password;
	private int port = 22;
	private String serverURL;
	private String serverUsername;
	private String serverPassword;
	private int proxyTimeout = 30; // minutes
//	private int localPort = 10000;
	private boolean enableHttpProxy;
	private String httpProxyHost;
	private int httpProxyPort = 3128;
	private String httpProxyUsername;
	private String httpProxyPassword;
	private boolean persistentConnectivity = true;

	public void setLeafNodeId(Long leafNodeId) {
		this.leafNodeId = leafNodeId;
	}

	public void setHiveApId(Long hiveApId) {
		this.hiveApId = hiveApId;
	}

	public Long getHiveApId() {
		return hiveApId;
	}

	public String getHostname() {
		return hostname;
	}

	public String getHost() {
		return host;
	}

//	public String getUser() {
//		return user;
//	}
//
//	public String getPassword() {
//		return password;
//	}

	public int getPort() {
		return port;
	}

	public String getServerURL() {
		return serverURL;
	}

	public String getServerUsername() {
		return serverUsername;
	}

	public String getServerPassword() {
		return serverPassword;
	}

//	public int getLocalPort() {
//		return localPort;
//	}

	public String getHttpProxyHost() {
		return httpProxyHost;
	}

	public int getHttpProxyPort() {
		return httpProxyPort;
	}

	public String getHttpProxyUsername() {
		return httpProxyUsername;
	}

	public String getHttpProxyPassword() {
		return httpProxyPassword;
	}

	public boolean isPersistentConnectivity() {
		return persistentConnectivity;
	}

	public int getProxyTimeout() {
		return proxyTimeout;
	}

	public void setProxyTimeout(int proxyTimeout) {
		this.proxyTimeout = proxyTimeout;
	}

	public boolean isEnableHttpProxy() {
		return enableHttpProxy;
	}

	public void setEnableHttpProxy(boolean enableHttpProxy) {
		this.enableHttpProxy = enableHttpProxy;
	}

//	public void setLocalPort(int localPort) {
//		this.localPort = localPort;
//	}

	public void setHttpProxyHost(String httpProxyHost) {
		this.httpProxyHost = httpProxyHost;
	}

	public void setHttpProxyPort(int httpProxyPort) {
		this.httpProxyPort = httpProxyPort;
	}

	public void setHttpProxyUsername(String httpProxyUsername) {
		this.httpProxyUsername = httpProxyUsername;
	}

	public void setHttpProxyPassword(String httpProxyPassword) {
		this.httpProxyPassword = httpProxyPassword;
	}

	public void setPersistentConnectivity(boolean persistentConnectivity) {
		this.persistentConnectivity = persistentConnectivity;
	}

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("sshConfig".equals(operation)) {
				log.info("execute", "sshConfig, hiveApId:" + hiveApId
						+ ", leafNodeId:" + leafNodeId);
				HiveAp hiveAp = getSelectedHiveAp();
				if (null == hiveAp) {
					addActionError(MgrUtil
							.getUserMessage("error.cli.object.notfind"));
				} else {
					if (hiveAp.isSimulated()) {
						addActionError(MgrUtil.getUserMessage("warn.simulated.ap.feature.nonsupport"));
					} else {
						hostname = hiveAp.getHostName();
						if (null == hiveApId) {
							// invoke from map view
							hiveApId = hiveAp.getId();
						}
					}
				}
			} else if ("ssh".equals(operation)) {
				log.info("execute", "ssh, hiveApId:" + hiveApId
						+ ", leafNodeId:" + leafNodeId);
				boolean suc = getHiveAPSshParameters();
				if (suc) {
					return "sshClient";
				}
			} else if ("closeSshTunnel".equals(operation)) {
				log.info("execute", "closeSshTunnel, hiveApId:" + hiveApId
						+ ", leafNodeId:" + leafNodeId);
				stopSshTunnel();
				return null;
			}
		} catch (Exception e) {
			addActionMessage(e.getMessage());
		}

		return "sshClientConfig";
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		//setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		String listTypeFromSession = (String) MgrUtil
				.getSessionAttribute(HiveApAction.HM_LIST_TYPE);
		if("managedVPNGateways".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
		}else if( "managedRouters".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
		}else if( "managedSwitches".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_SWITCHES);
		}else if("managedDeviceAPs".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
		}else if("managedHiveAps".equals(listTypeFromSession)){
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}else{
			setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
		}
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		((MapLeafNode) bo).getHiveAp().getId();
		return null;
	}

	public boolean getHiveAPSshParameters() {
		try {
			HiveAp hiveAp = getSelectedHiveAp();
			if (null == hiveAp) {
				addActionError(MgrUtil
						.getUserMessage("error.cli.object.notfind"));
				return false;
			} else {
				log.info("getHiveAPSshParameters", hiveAp.toString());
				hostname = hiveAp.getHostName();
			//	host = hiveAp.getIpAddress();

				// The timeout for SSH tunnel should be more than that for SSH client proxy.
				int tunnelTimeout = proxyTimeout + 2;
				port = SshConnectionFactory.getInstance().setupSshTunnel(hiveAp, tunnelTimeout);
			//	if (super.writePermission) {
			//		user = hiveAp.getAdminUser();
			//		password = hiveAp.getAdminPassword();
			//	} else {
			//		user = hiveAp.getReadOnlyUser();
			//		password = hiveAp.getReadOnlyPassword();
			//	}
				String serverName = request.getServerName();
				int serverPort = request.getServerPort();
				String contextPath = request.getContextPath();
				serverURL = "https://" + serverName + ":" + serverPort
						+ contextPath + "/proxy";
				serverUsername = NmsUtil.getHMScpUser();
				serverPassword = NmsUtil.getHMScpPsd();
				return true;
			}
		} catch (SshConnectException se) {
			log.error("getHiveAPSshParameters",
					"getHiveAPSshParameters error.", se);
			addActionError(se.getMessage());
			return false;
		} catch (Exception e) {
			log.error("getHiveAPSshParameters",
					"getHiveAPSshParameters error.", e);
			addActionError(MgrUtil.getUserMessage("action.error.cannot.get.ssh.parameter"));
			return false;
		}
	}

	public String getRequiredJreVersion() {
		String version = SystemUtils.JAVA_SPECIFICATION_VERSION;
		String shortVer = StringUtils.isEmpty(version) ? " " : (" " + version
				.substring(version.indexOf(".") + 1));
		return MgrUtil.getUserMessage("hm.ssh.config.no.jre.note", shortVer);
	}

	public String getSshWindowTitle() {
		if (null != hostname) {
			return "SSH window - " + hostname;
		} else {
			return "SSH window";
		}
	}

	public String getSshConfigWindowTitle() {
		if (null != hostname) {
			return "SSH Proxy Settings for "+NmsUtil.getOEMCustomer().getAccessPonitName()+" "
					+ hostname.replace("\\", "\\\\").replace("'", "\\'") + "";
		} else {
			return "SSH Proxy Settings";
		}
	}

	private HiveAp getSelectedHiveAp() throws Exception {
		HiveAp hiveAp = null;
		if (null != hiveApId) {
			hiveAp = findBoById(HiveAp.class, hiveApId);
		} else if (leafNodeId != null) {
			MapLeafNode leafNode = findBoById(MapLeafNode.class, leafNodeId,
					this);
			if (null != leafNode) {
				hiveAp = leafNode.getHiveAp();
			}
		}
		return hiveAp;
	}

	private void stopSshTunnel() throws Exception {
		HiveAp hiveAp = getSelectedHiveAp();
		if (null == hiveAp) {
			log.error("stopSshTunnel", "HiveAP is null");
			return;
		}
		SshConnectionFactory.getInstance().closeSshTunnel(hiveAp);
	}

}