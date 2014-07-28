package com.ah.be.protocol.ssh.scp;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.InteractiveCallback;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3FileAttributes;

import com.ah.util.Tracer;

public class AhScpMgmtImpl implements AhScpMgmt {

	private static final Tracer log = new Tracer(AhScpMgmtImpl.class.getSimpleName());

	/* The host where we later want to connect to */
	private final String host;

	/* User name for SSH login */
	private final String userName;

	/* Password associated with userName for SSH login */
	private final String password;

	/* The default SCP port according to RFC 783 is 22 */
	private int port = 22;

	/*
	 * Connect the underlying TCP socket to the server with the given timeout
	 * value (non-negative, in milliseconds). Zero means no timeout. If a proxy
	 * is being used (see setProxyData(ProxyData)), then this timeout is used
	 * for the connection establishment to the proxy
	 */
	private int connTimeout = 10000;

	/* SSH Connection */
	private Connection conn;

	public AhScpMgmtImpl(String host, String userName, String password) {
		this.host = host;
		this.userName = userName;
		this.password = password;
	}

	public AhScpMgmtImpl(String host, int port, String userName, String password) {
		this(host, userName, password);
		this.port = port;
	}

	public AhScpMgmtImpl(String host, int port, String userName, String password, int connTimeout) {
		this(host, userName, password);
		this.port = port;
		this.connTimeout = connTimeout;
	}

	public String getHost() {
		return host;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnTimeout() {
		return connTimeout;
	}

	public void setConnTimeout(int connTimeout) {
		this.connTimeout = connTimeout;
	}

	public void initializeConnection() throws IOException {
		log.info("initializeConnection", "Connecting to " + host + " with user " + userName
				+ " and pwd " + password);
		conn = new Connection(host, port);
		ConnectionInfo connInfo = conn.connect(null, connTimeout, 0);
		log.debug("initializeConnection",
				"The currently used crypto algorithm for packets from the client to the server ["
						+ connInfo.clientToServerCryptoAlgorithm + "]");
		log.debug("initializeConnection",
				"The currently used MAC algorithm for packets from to the client to the server ["
						+ connInfo.clientToServerMACAlgorithm + "]");
		log.debug("initializeConnection",
				"The currently used crypto algorithm for packets from to the server to the client ["
						+ connInfo.serverToClientCryptoAlgorithm + "]");
		log.debug("initializeConnection",
				"The currently used MAC algorithm for packets from to the server to the client ["
						+ connInfo.serverToClientMACAlgorithm + "]");

		if (conn.isAuthMethodAvailable(userName, "password")) {
			log.info("initializeConnection", "Using 'password' method to connect.");
			boolean isAuthWithPwd = conn.authenticateWithPassword(userName, password);

			if (!isAuthWithPwd) {
				throw new IOException("Password Authentication failure.");
			}
		} else if (conn.isAuthMethodAvailable(userName, "keyboard-interactive")) {
			log.info("initializeConnection", "Using 'keyboard-interactive' method to connect.");
			InteractiveLogic callBack = new InteractiveLogic(password);
			boolean isAuthWithKeyboard = conn.authenticateWithKeyboardInteractive(userName,
					callBack);

			if (!isAuthWithKeyboard) {
				if (callBack.getPromptCount() == 0) {
					/*
					 * aha. the server announced that it supports
					 * "keyboard-interactive", but when we asked for it, it just
					 * denied the request without sending us any prompt. That
					 * happens with some server versions/configurations.
					 */
					log.error("initializeConnection", "Keyboard-Interactive does not work.");
				}

				throw new IOException("Keyboard-Interactive Authentication failure.");
			}
		} else {
			throw new IOException("The 'publickey' authentication is not supported by HiveManager.");
		}
	}

	public long getTargetFileSize(String targetFile) throws IOException {
		if (conn == null) {
			initializeConnection();
		}

		SFTPv3Client client = new SFTPv3Client(conn);
		SFTPv3FileAttributes fa = client.stat(targetFile);

		return fa.size;
	}

	public void scpGet(String remoteFile, String localDir) throws IOException {
		log.info("scpGet", "SCP [" + remoteFile + "] from " + host + " to " + localDir);

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.get(remoteFile, localDir);
	}

	public void scpGet(String[] remoteFiles, String localDir) throws IOException {
		for (String remoteFile : remoteFiles) {
			log.info("scpGet", "SCP [" + remoteFile + "] from " + host + " to " + localDir);
		}

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.get(remoteFiles, localDir);
	}

	public void scpPut(String localFile, String remoteDir) throws IOException {
		log.info("scpPut", "SCP [" + localFile + "] to " + host + remoteDir);

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.put(localFile, remoteDir);
	}

	public void scpPut(String localFile, String remoteDir, String mode) throws IOException {
		log.info("scpPut", "SCP [" + localFile + "] to " + host + remoteDir);

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.put(localFile, remoteDir, mode);
	}

	public void scpPut(String localFile, String remoteFile, String remoteDir, String mode)
			throws IOException {
		log.info("scpPut", "SCP [" + localFile + "] to " + host + remoteDir);

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.put(localFile, remoteFile, remoteDir, mode);
	}

	public void scpPut(String[] localFiles, String remoteDir) throws IOException {
		for (String localFile : localFiles) {
			log.info("scpPut", "SCP [" + localFile + "] to " + host + remoteDir);
		}

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.put(localFiles, remoteDir);
	}

	public void scpPut(String[] localFiles, String remoteDir, String mode) throws IOException {
		for (String localFile : localFiles) {
			log.info("scpPut", "SCP [" + localFile + "] to " + host + remoteDir);
		}

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.put(localFiles, remoteDir, mode);
	}

	public void scpPut(String[] localFiles, String[] remoteFiles, String remoteDir, String mode)
			throws IOException {
		for (int i = 0; i < localFiles.length; i++) {
			log.info("scpPut", "SCP [" + localFiles[i] + "] from local to " + host + remoteDir
					+ "[" + remoteFiles[i] + "]");
		}

		if (conn == null) {
			initializeConnection();
		}

		SCPClient client = new SCPClient(conn);
		client.put(localFiles, remoteFiles, remoteDir, mode);
	}

	public void close() {
		if (conn != null) {
			conn.close();
		}
	}

	/**
	 * The internal class which will be used to determine the responses to the
	 * challenges asked by the SSH server.
	 */
	private class InteractiveLogic implements InteractiveCallback {
		private final String password;

		private int promptCount = 0;

		public InteractiveLogic(String password) {
			this.password = password;
		}

		/*
		 * The callback may be invoked several times which depends on how many
		 * questions-sets the server requires.
		 */
		public String[] replyToChallenge(String name, String instruction, int numPrompts,
				String[] prompt, boolean[] echo) throws IOException {
			log.info("replyToChallenge", "name: " + name + "; instruction: " + instruction
					+ "; numPrompts: " + numPrompts);
			String[] result = new String[numPrompts];

			for (int i = 0; i < numPrompts; i++) {
				/*
				 * Often, servers just send empty strings for "name" and
				 * "instruction" and we just takes care of the "Password: "
				 * prompt so far.
				 */
				log.info("replyToChallenge", "prompt[" + i + "]: " + prompt[i]);

				// if ("Password: ".equalsIgnoreCase(prompt[i]))
				if (prompt[i].toUpperCase().trim().startsWith("PASSWORD")) {
					result[i] = password;
				} else {
					result[i] = "";
				}

				promptCount++;
			}

			return result;
		}

		/*
		 * We maintain a prompt counter which enables the detection of situation
		 * where the SSH server is signaling "authentication failed" even though
		 * if it did not send a single prompt.
		 */
		public int getPromptCount() {
			return promptCount;
		}
	}

}