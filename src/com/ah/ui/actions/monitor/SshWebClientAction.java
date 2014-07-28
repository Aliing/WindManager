package com.ah.ui.actions.monitor;

import java.util.Collection;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmUser;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.AccessControl;
import com.ah.bo.mgmt.AccessControl.CrudOperation;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.hiveap.HiveApAction;
import com.ah.util.HmException;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.ericdaugherty.sshwebproxy.ShellChannel;
import com.ericdaugherty.sshwebproxy.SshConnectException;
import com.ericdaugherty.sshwebproxy.SshConnection;
import com.ericdaugherty.sshwebproxy.SshConnectionFactory;
import com.ericdaugherty.sshwebproxy.SshSession;

public class SshWebClientAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;
	private static final Tracer log = new Tracer(SshWebClientAction.class
			.getSimpleName());

	@Override
	public String execute() {
		try {
			String forward = globalForward();
			if (forward != null) {
				if (null == operation || "login".equals(operation)
						|| "webSsh".equals(operation)) {
					addActionMessage(MgrUtil
							.getUserMessage("error.webSsh.access.failed"));
					return INPUT;
				} else {
					jsonObject = new JSONObject();
					jsonObject.put("error", MgrUtil
							.getUserMessage("error.webSsh.access.failed"));
					return "json";
				}
			}
			if ("requestCli".equals(operation)) {
				log.info("execute", "request cli info:" + inputConsole
						+ ", connection info:" + connectionInfo
						+ ", connection id:" + connectionId + ", channel id:"
						+ channelId + ", keyCode:" + keyCode);
				if ("quit".equalsIgnoreCase(inputConsole)
						|| "exit".equalsIgnoreCase(inputConsole)) {
					disconnectChannel();
				} else {
					write();
				}
				return "json";
			} else if ("disconnectChannel".equals(operation)) {
				log.info("execute", "disconnect of connection:"
						+ connectionInfo + ", connection id:" + connectionId
						+ ", channel id:" + channelId);
				disconnectChannel();
				return "json";
			} else if ("disconnectConnection".equals(operation)) {
				log.info("execute", "disconnect of connection:"
						+ connectionInfo + ", connection id:" + connectionId
						+ ", channel id:" + channelId);
				// disconectConnection();
				// just disconnect channel for ssh tunnel function;
				disconnectChannel();
				return "json";
			} else if ("connect".equals(operation)) {
				log.info("execute", "connect of connection:" + connectionInfo
						+ ", username:" + username + ", tunnelTimeout:"
						+ tunnelTimeout);
				connect(username, pwd, tunnelTimeout);
				return "json";
			} else if ("requestUp".equals(operation)) {
				log.info("execute", "request up keyboard.");
				keyBoardUpDown(true);
				return "json";
			} else if ("requestDown".equals(operation)) {
				log.info("execute", "request down keyboard.");
				keyBoardUpDown(false);
				return "json";
			} else if ("webSsh".equals(operation)) {
				log.info("execute", "leafNodeId:" + leafNodeId + ", hiveApId:"
						+ hiveApId);
				HiveAp hiveAp = getSelectedHiveAp();
				if (null != hiveAp) {
					// host = hiveAp.getIpAddress();
					username = hiveAp.getAdminUser();
					pwd = hiveAp.getAdminPassword();
					tunnelTimeout = 30; // tunnel timeout in minute
					connectionInfo = hiveAp.getMacAddress();
				}
				// **Change to disconnect by default.
				// login(hiveAp, username, pwd, tunnelTimeout);
				connected = false;
				
				checkAccessCtrl(hiveAp);
				
				return INPUT;
			}
		} catch (Exception e) {
			e.printStackTrace();
			addActionError(e.getMessage());
		}
		return INPUT;
	}
	
	@Override
	public void prepare() throws Exception {
	    super.prepare();
        String listTypeFromSession = (String) MgrUtil
                .getSessionAttribute(HiveApAction.HM_LIST_TYPE);
        if ("managedVPNGateways".equals(listTypeFromSession)) {
            setSelectedL2Feature(L2_FEATURE_VPN_GATEWAYS);
        } else if ("managedRouters".equals(listTypeFromSession)) {
            setSelectedL2Feature(L2_FEATURE_BRANCH_ROUTERS);
        } else if ("managedSwitches".equals(listTypeFromSession)) {
            setSelectedL2Feature(L2_FEATURE_SWITCHES);
        } else if ("managedDeviceAPs".equals(listTypeFromSession)) {
            setSelectedL2Feature(L2_FEATURE_DEVICE_HIVEAPS);
        } else if ("managedHiveAps".equals(listTypeFromSession)) {
            setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
        } else {
            setSelectedL2Feature(L2_FEATURE_MANAGED_HIVE_APS);
        }
	}
	private boolean denyAccess = false;
	private String promptMessage;
	private void checkAccessCtrl(HiveAp hiveAp) {
	    if(null == hiveAp) {
	        denyAccess = true;
	        promptMessage = MgrUtil.getMessageString("error.ssh.client.device.notfound");
	    } else {
	        String feature = getSelectedL2FeatureKey();
	        if(null != feature && (feature.equals(L2_FEATURE_CONFIG_HIVE_APS) || feature.equals(L2_FEATURE_MANAGED_HIVE_APS)
	                || feature.equals(L2_FEATURE_CONFIG_VPN_GATEWAYS) || feature.equals(L2_FEATURE_VPN_GATEWAYS)
	                || feature.equals(L2_FEATURE_CONFIG_BRANCH_ROUTERS) || feature.equals(L2_FEATURE_BRANCH_ROUTERS)
	                || feature.equals(L2_FEATURE_CONFIG_SWITCHES) || feature.equals(L2_FEATURE_SWITCHES)
	                || feature.equals(L2_FEATURE_CONFIG_DEVICE_HIVEAPS) || feature.equals(L2_FEATURE_DEVICE_HIVEAPS))) {
	            final HmUser user = getUserContext();
                try {
	                AccessControl.checkUserAccess(user, feature, CrudOperation.UPDATE);
	                if(hiveAp.getOwner().getId().compareTo(getDomain().getId()) != 0) {
	                    denyAccess = true;
	                }
	            } catch (HmException e) {
                    log.error("Not permission for " + user.getId() + ":"
                                    + user.getUserName() + "("
                                    + user.getUserGroup().getId() + ":"
                                    + user.getUserGroup().getGroupName() + ")", e);
                    denyAccess = true;
	            }
	        } else {
	            denyAccess = true;
	        }
	        promptMessage = MgrUtil.getMessageString("error.ssh.client.access.permit.device");
	    }
	}

	private void keyBoardUpDown(boolean isUp) throws JSONException {
		jsonObject = new JSONObject();
		SshSession session = new SshSession(request);
		SshConnection sshConnection = session.getSshConnection(connectionInfo,
				connectionId);

		if (sshConnection != null && sshConnection.isOpen()) {
			ShellChannel shellChannel = sshConnection
					.getShellChannel(channelId);
			String command = isUp ? shellChannel.getLastCommand()
					: shellChannel.getNextCommand();
			jsonObject.put("command", command);
		} else {
			jsonObject.put("closed", true);
		}
	}

	private void disconnectChannel() throws JSONException {
		jsonObject = new JSONObject();
		SshConnectionFactory.getInstance().closeChannel(connectionInfo,
				connectionId, channelId, request.getSession());
		jsonObject.put("closed", true);
	}

	// private void disconectConnection() throws JSONException {
	// jsonObject = new JSONObject();
	// SshConnectionFactory.getInstance().closeConnection(connectionInfo);
	// jsonObject.put("closed", true);
	// }

	private void connect(String username, String password, int tunnelTimeout)
			throws JSONException {
		jsonObject = new JSONObject();
		if (null == connectionInfo) {
			return;
		}
		try {
			// if (null == host || "".equals(host.trim())) {
			// get the host from connection info
			// Object[] objects = SshConnection
			// .parseConnectionInfo(connectionInfo);
			// host = (String)objects[0];
			// }
			List<HiveAp> list = QueryUtil.executeQuery(HiveAp.class, null,
					new FilterParams("macAddress", connectionInfo));
			HiveAp hiveAp = null;
			if (!list.isEmpty()) {
				hiveAp = list.get(0);
			}
			login(hiveAp, username, password, tunnelTimeout);
		} catch (SshConnectException e) {
			log.error("connect", "ssh connect error", e);
			jsonObject.put("error", e.getMessage());
			return;
		}
		jsonObject.put("opened", true);
		jsonObject.put("connectionInfo", connectionInfo);
		jsonObject.put("connectionId", connectionId);
		jsonObject.put("channelId", channelId);
		String value = getContent(connectionInfo, connectionId, channelId);
		jsonObject.put("context", value);
		// jsonObject.put("prompt", prompt);
	}

	/*-
	private void login(String host, String username, String password, int port)
			throws SshConnectException {
		ShellChannel shellChannel = SshConnectionFactory.getInstance()
				.openConnection(host, port, 900000, username, password,
						request.getSession());
		if (null != shellChannel) {
			channelId = shellChannel.getChannelId();
			connectionInfo = SshConnection.getConnectionInfo(host, port,
					username);
		}
	}*/

	private void login(HiveAp hiveAp, String username, String password,
			int tunnelTimeout) throws SshConnectException {
		if (null == hiveAp) {
			log.error("login", "the specify HiveAP does not existed.");
			return;
		}

		ShellChannel shellChannel = SshConnectionFactory.getInstance()
				.openChannel(hiveAp, username, password, 900000,
						tunnelTimeout, request.getSession());
		if (null != shellChannel) {
			connectionId = shellChannel.getSshConnection().getConnectionId();
			channelId = shellChannel.getChannelId();
		}
	}

	private void write() throws JSONException {
		log.debug("write", "Write request received.");
		jsonObject = new JSONObject();
		boolean valid = false;
		SshSession session = new SshSession(request);
		SshConnection sshConnection = session.getSshConnection(connectionInfo,
				connectionId);

		// Get the Channel and write to it.
		if (sshConnection != null && sshConnection.isOpen()) {
			ShellChannel shellChannel = sshConnection
					.getShellChannel(channelId);

			if (shellChannel != null && shellChannel.isConnected()) {
				shellChannel.write(inputConsole, keyCode);
				valid = true;
			}
		}

		// Redirect to the result page.
		if (valid) {
			log.debug("write", "Successful Write to " + connectionInfo + " "
					+ connectionId + " " + channelId);
			String value = getContent(connectionInfo, connectionId, channelId);
			jsonObject.put("suc", true);
			jsonObject.put("context", value);
		} else {
			jsonObject.put("closed", true);
			jsonObject.put("error", "Connection must have been closed.");
			log.debug("write", "Write request to invalid channel.");
		}
	}

	private String getContent(String connectionInfo, String connectionId,
			String channelId) {
		if (connectionInfo == null || connectionInfo.trim().equals("")
				|| connectionId == null || connectionId.trim().equals("")
				|| channelId == null || channelId.trim().equals("")) {
			return "";
		}

		StringBuffer buffer = new StringBuffer("");
		// StringBuffer promptBuffer = new StringBuffer("");
		SshSession session = new SshSession(request);
		SshConnection sshConnection = session.getSshConnection(connectionInfo,
				connectionId);
		ShellChannel shellChannel = sshConnection.getShellChannel(channelId);

		if (null != shellChannel) {
			shellChannel.read();
			String[] lines = shellChannel.getScreen();
			int cursorRowIndex = shellChannel.getCursorRow();
			int cursorColumnIndex = shellChannel.getCursorColumn();

			for (int index = 0; index < lines.length; index++) {
				String row = lines[index];

				// Display the cursor if applicable.
				if (cursorRowIndex == index && cursorColumnIndex != -1) {
					int rowSize = row.length();

					for (int columnIndex = 0; columnIndex < rowSize; columnIndex++) {
						if (cursorColumnIndex == columnIndex) {
							buffer.append("<span id='shell-cursor'>");
							buffer.append(shellChannel.encodeHTML(row
									.substring(columnIndex, columnIndex + 1)));
							buffer.append("</span>");
							buffer
									.append("<input type=\"text\" id=\"input\" onfocus=\"focusAction(event);\" onblur=\"setCursor(false);\" onkeypress=\"keyPressAction(event);\" onkeydown=\"return keyDownAction(event);\" />");
						} else {
							buffer.append(shellChannel.encodeHTML(row
									.substring(columnIndex, columnIndex + 1)));
						}
					}
				} else if (cursorRowIndex == index && cursorColumnIndex == -1) {
					// Display the line with the cursor at the end.
					buffer.append(shellChannel.encodeHTML(row));
					buffer.append("<span id='shell-cursor'></span>");
					buffer
							.append("<input type=\"text\" id=\"input\" onfocus=\"focusAction(event);\" onblur=\"setCursor(false);\" onkeypress=\"keyPressAction(event);\" onkeydown=\"return keyDownAction(event);\" />");
				} else {
					// Display the entire line, no cursor.
					if (!row.isEmpty()) {
						buffer.append(shellChannel.encodeHTML(row));
						buffer.append("<br>");
					}
				}
			}
		}
		// prompt = promptBuffer.toString();
		return buffer.toString();
	}

	public String getInitialContent() {
		// Look for an existing open connection.
		// String connectionInfo = SshConnection.getConnectionInfo(host, port,
		// username);
		return getContent(connectionInfo, connectionId, channelId);
	}

	protected JSONArray jsonArray = null;

	protected JSONObject jsonObject = null;

	public String getJSONString() {
		if (jsonArray == null) {
			log.debug("getJSONString", "JSON string: " + jsonObject.toString());
			return jsonObject.toString();
		} else {
			log.debug("getJSONString", "JSON string: " + jsonArray.toString());
			return jsonArray.toString();
		}
	}

	private String username;
	private String pwd;
	// private String host;
	// private int port;
	private int tunnelTimeout;
	private String connectionId;
	private String channelId;
	private String connectionInfo;
	private String inputConsole;
	private int keyCode;
	private Long leafNodeId;
	private Long hiveApId;
	private boolean connected;

	public boolean isConnected() {
		return connected;
	}

	// private boolean writeLine;

	public void setUsername(String username) {
		this.username = username;
	}

	// public void setHost(String host) {
	// this.host = host;
	// }

	// public void setPort(int port) {
	// this.port = port;
	// }

	public String getUsername() {
		return username;
	}

	// public String getHost() {
	// return host;
	// }

	// public int getPort() {
	// return port;
	// }

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getConnectionId() {
		return connectionId;
	}

	public void setConnectionId(String connectionId) {
		this.connectionId = connectionId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(String connectionInfo) {
		this.connectionInfo = connectionInfo;
	}

	public void setInputConsole(String inputConsole) {
		this.inputConsole = inputConsole;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public void setLeafNodeId(Long leafNodeId) {
		this.leafNodeId = leafNodeId;
	}

	// public void setWriteLine(boolean writeLine) {
	// this.writeLine = writeLine;
	// }

	public void setHiveApId(Long hiveApId) {
		this.hiveApId = hiveApId;
	}

	private HiveAp getSelectedHiveAp() throws Exception {
		HiveAp hiveAp = null;
		if (null != hiveApId) {
			hiveAp = QueryUtil.findBoById(HiveAp.class, hiveApId);
		} else {
			MapLeafNode leafNode = QueryUtil.findBoById(
					MapLeafNode.class, leafNodeId, this);
			if (null != leafNode) {
				hiveAp = leafNode.getHiveAp();
			}
		}
		return hiveAp;
	}

	public Collection<HmBo> load(HmBo bo) {
		if (null == bo) {
			return null;
		}
		if (bo instanceof MapLeafNode) {
			((MapLeafNode) bo).getHiveAp().getId();
		}
		return null;
	}

	public int getTunnelTimeout() {
		return tunnelTimeout;
	}

	public void setTunnelTimeout(int tunnelTimeout) {
		this.tunnelTimeout = tunnelTimeout;
	}

    public boolean isDenyAccess() {
        return denyAccess;
    }

    public String getPromptMessage() {
        return promptMessage;
    }

    public void setDenyAccess(boolean denyAccess) {
        this.denyAccess = denyAccess;
    }

    public void setPromptMessage(String promptMessage) {
        this.promptMessage = promptMessage;
    }

}