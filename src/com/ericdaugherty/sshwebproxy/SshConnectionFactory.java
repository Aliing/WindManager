package com.ericdaugherty.sshwebproxy;

//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.HashMap;
import java.util.Map;
//import java.util.StringTokenizer;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ericdaugherty.soht.client.util.AvailablePortFinder;

import com.ah.be.app.AhAppContainer;
import com.ah.be.cli.AhCliFactory;
import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.communication.BeCommunicationEvent;
import com.ah.be.communication.event.BeCapwapCliResultEvent;
import com.ah.be.communication.event.BeCliEvent;
//import com.ah.be.parameter.constant.util.AhConstantUtil;
import com.ah.bo.hiveap.HiveAp;
import com.ah.util.HmContextListener;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class SshConnectionFactory implements SshConstants {

	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog( SshConnectionFactory.class );

	/** Native logger */
	private static final Tracer tracer = new Tracer( SshConnectionFactory.class.getSimpleName() );

	private static SshConnectionFactory instance;

	private final int SSH_TUNNEL_PORT_FROM;

	private final int SSH_TUNNEL_PORT_TO;

	private SshConnectionFactory() {
		String propValue = System.getProperty("ssh.tunnel.port.from");
		int fromPort = 20000;

		if (propValue != null) {
			try {
				fromPort = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				tracer.warning("Constructor",
						"Failed to parse the beginning of SSH tunnel port, using " + fromPort + " instead.", nfe);
			}
		}

		SSH_TUNNEL_PORT_FROM = fromPort;

		propValue = System.getProperty("ssh.tunnel.port.to");
		int toPort = 20099;

		if (propValue != null) {
			try {
				toPort = Integer.parseInt(propValue);
			} catch (NumberFormatException nfe) {
				tracer.warning("Constructor",
						"Failed to parse the end of SSH tunnel port, using " + toPort + " instead.", nfe);
			}
		}

		SSH_TUNNEL_PORT_TO = toPort;
	}

	public synchronized static SshConnectionFactory getInstance() {
		if (instance == null) {
			instance = new SshConnectionFactory();
		}

		return instance;
	}

    //***************************************************************
    // Access Methods
    //***************************************************************

	/**
     * Create a new SshConnection to the specified HiveAP with some necessary information given as arguments.
     *
     * @param hiveAp the specified HiveAP to connect to.
     * @param username the user name to login with.
     * @param password the password to login with.
	 * @param connectTimeout timeout in milliseconds without any activity before the timeout event occurs.
	 * @param tunnelTimeout timeout in minutes during which the SSH tunnel between HiveAP and SSH server will be up.
	 * @param session HTTP session where the open connection is saved as an attribute.
	 * @return A new ShellChannel started on a newly built or exist connection.
     * @throws SshConnectException thrown if the connection attempt fails for any reason.
     */
	public synchronized ShellChannel openChannel( HiveAp hiveAp, String username, String password, int connectTimeout, int tunnelTimeout, HttpSession session ) throws SshConnectException {
        if ( log.isDebugEnabled() ) {
			log.debug( "Open Channel request received." );
		}

		String hiveApMac = hiveAp.getMacAddress();

		// Look for an existing open connection.
		SshSession sshSession = new SshSession( session );
		SshConnection sshConnection = sshSession.getSshConnection( hiveApMac );

		// If the connection does not exist yet, open a new one.
		if ( sshConnection == null || !sshConnection.isOpen() ) {
			if ( log.isDebugEnabled() ) {
				log.debug( "Connection does not exist, opening a new Connection." );
			}

			int port;
			String host;
			SshConnectMode sshConnectMode = getSshConnectMode( hiveAp );

			// If the CAPWAP link IP is equivalent to the actual HiveManager IP, using the 'SSH Tunnel' mode to connect to the specified HiveAP.
			if ( SshConnectMode.TUNNEL.equals( sshConnectMode ) ) {
				if ( log.isDebugEnabled() ) {
					log.debug( "Use " + SshConnectMode.TUNNEL.toString() + " mode to connect to HiveAP " + hiveApMac );
				}

				// Select an unused port in HiveManager for setting up an SSH tunnel on HiveAP to HiveManager.
				port = getAvailableTunnelPort();
				int sshServerPort = AhAppContainer.getBeAdminModule().getSshdPort();
				String sshServerUser = NmsUtil.getHMScpUser();
				String sshServerPwd = NmsUtil.getHMScpPsd();

				// Need to set up an SSH tunnel to HiveManager on the specified HiveAP first if using the tunnel mode.
				syncSetupSshTunnel( hiveAp, hiveAp.getCapwapLinkIp(), sshServerPort, port, sshServerUser, sshServerPwd, tunnelTimeout );

				/*- Wait for at least 5000 milliseconds to connect an SSH tunnel just established.
				try {
					Thread.sleep( 5000L );
				} catch ( InterruptedException ie ) {
					log.error( "Interrupted while sleeping a couple of time before connecting an SSH tunnel with port = " + port + " for HiveAP " + hiveApMac );
				}*/

				host = "127.0.0.1";
			} else {
				if ( log.isDebugEnabled() ) {
					log.debug( "Use " + SshConnectMode.DEFAULT.toString() + " mode to connect to HiveAP " + hiveApMac );
				}

				port = 22;
				host = hiveAp.getIpAddress();
			}

			try {
				sshConnection = new SshConnection( host, port, username, password, connectTimeout, hiveAp, sshConnectMode );
			} catch ( SshConnectException sce ) {
				// Close an SSH tunnel established to free the port used if fails to connect to this tunnel.
				if ( SshConnectMode.TUNNEL.equals( sshConnectMode ) ) {
					try {
						closeSshTunnel( hiveAp );
					} catch ( Exception e ) {
						log.error( "Could not send request to close an SSH tunnel for HiveAP " + hiveApMac, e );
					}
				}

				throw sce;
			}

			// If there is a collision adding it to the SshConnection map, then one must have been created while we were creating ours.  Close ours and use the other one.
			if ( !sshSession.addSshConnection( sshConnection ) ) {
				log.warn( "addSshConnection race condition occurred.  Closing duplicate connection!" );
			//	sshConnection.close();
				closeSshConnection( sshConnection );
				sshConnection = sshSession.getSshConnection( hiveApMac );

				if ( sshConnection == null ) {
					log.error( "Failed to get connection from SshConnection map after closing new connection! Giving up!" );
					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.connection.open.failed" ) );
				}
			}
		}

		try {
			// Open a new shell channel on the current connection.
			ShellChannel shellChannel = sshConnection.openShellChannel();

			// Store the open channel into the current session.
			sshSession.addChannel( sshConnection, shellChannel );

			return shellChannel;
		} catch ( SshConnectException sce ) {
			int remainChannelCount = sshConnection.getChannels().size();
			int activeChannelCount = sshConnection.getActiveChannelCount();

			// Close the connection established if fails to open new channel on it whilst neither remaining nor active channels are inside.
			if ( remainChannelCount == 0 || activeChannelCount == 0 ) {
				closeSshConnection( sshConnection );
			} else {
				if ( log.isDebugEnabled() ) {
					log.debug( remainChannelCount + " Channels are remaining over the Connection " + sshConnection.getConnectionInfo() + " and " + activeChannelCount + " of them are active." );
				}
			}

			throw sce;
		}
	}

	/**
	 * Handles requests to close an existing channel.
	 *
	 * @param hiveApMac the MAC of HiveAP which is being connected with an existing connection from which a specified ShellChannel will be removed.
	 * @param connectionId the identity of connection the channel to be closed is using.
	 * @param channelId the identity of the shell channel to be closed.
	 * @param session HTTP session where the open connection is saved as an attribute.
	 */
	public synchronized void closeChannel( String hiveApMac, String connectionId, String channelId, HttpSession session ) {
		if ( log.isDebugEnabled() ) {
			log.debug( "Close Channel request received." );
		}

		SshSession sshSession = new SshSession( session );
		SshConnection sshConnection = sshSession.getSshConnection( hiveApMac, connectionId );

		if ( sshConnection != null ) {
			sshConnection.closeChannel( channelId );

			// Remove the channel closed out of session.
			sshSession.removeChannel( connectionId, channelId );

			int remainChannelCount = sshConnection.getChannels().size();
			int activeChannelCount = sshConnection.getActiveChannelCount();

			// Close connection automatically if neither remaining nor active channels are inside.
			if ( remainChannelCount == 0 || activeChannelCount == 0 ) {
				if ( log.isInfoEnabled() ) {
					log.info( "Closing Connection " + sshConnection.getConnectionInfo() + " due to no one open Channel inside." );
				}

				closeConnection( hiveApMac, connectionId, session );
			} else {
				if ( log.isDebugEnabled() ) {
					log.debug( remainChannelCount + " Channels are remaining over the Connection " + sshConnection.getConnectionInfo() + " and " + activeChannelCount + " of them are active." );
				}
			}
		} else {
			if ( log.isDebugEnabled() ) {
				log.debug( "The connection could not be found for HiveAP " + hiveApMac + ", giving up closing the shell channel with id " + channelId );
			}
		}
	}

	/**
	 * Handles requests to close an existing channel.
	 *
	 * @param hiveAp which is being connected with an existing connection from which a specified ShellChannel will be removed.
	 * @param connectionId the identity of connection the channel to be closed is using.
	 * @param channelId the identity of the shell channel to be closed.
	 * @param session HTTP session where the open connection is saved as an attribute.
	 */
	public void closeChannel( HiveAp hiveAp, String connectionId, String channelId, HttpSession session ) {
		closeChannel( hiveAp.getMacAddress(), connectionId, channelId, session );
	}

	/**
	 * Close an existing SSH connection for the HiveAP specified.
	 *
	 * @param hiveApMac the MAC of HiveAP which is being connected with the SSH connection to be closed.
	 * @param connectionId the identity of connection to be closed.
	 * @param session HTTP session where the open connection is saved as an attribute.
	 */
	public void closeConnection( String hiveApMac, String connectionId, HttpSession session ) {
		if ( log.isDebugEnabled() ) {
			log.debug( "Close Connection request received." );
		}

		SshSession sshSession = new SshSession( session );
		SshConnection sshConnection = sshSession.getSshConnection( hiveApMac, connectionId );

		if ( sshConnection != null ) {
		//	sshConnection.close();
			closeSshConnection( sshConnection );
			sshSession.removeConnection( hiveApMac, connectionId );
		} else {
			if ( log.isDebugEnabled() ) {
				log.debug( "The connection could not be found for HiveAP " + hiveApMac + ", giving up closing." );
			}
		}
	}

	/**
	 * Close an existing SSH connection for the HiveAP specified.
	 *
	 * @param hiveAp which is being connected with the SSH connection to be closed.
	 * @param connectionId the identity of connection to be closed.
	 * @param session HTTP session where the open connection is saved as an attribute.
	 */
	public void closeConnection( HiveAp hiveAp, String connectionId, HttpSession session ) {
		closeConnection( hiveAp.getMacAddress(), connectionId, session );
	}

	/**
	 * Close an SSH connection given as argument.
	 *
	 * @param sshConnection an SSH connection to be closed.
	 */
	public void closeSshConnection( SshConnection sshConnection ) {
		if ( sshConnection == null ) {
			return;
		}

		sshConnection.close();

		SshConnectMode sshConnectMode = sshConnection.getSshConnectMode();

		if ( !SshConnectMode.TUNNEL.equals( sshConnectMode ) ) {
			return;
		}

		HiveAp hiveAp = sshConnection.getHiveAp();
		String hiveApMac = hiveAp.getMacAddress();

		if ( log.isInfoEnabled() ) {
			log.info( "Closing an SSH tunnel for HiveAP " + hiveApMac );
		}

		try {
			// Close an SSH tunnel for the specified HiveAP.
			closeSshTunnel( hiveAp );
		} catch ( SshConnectException sce ) {
			log.error( "Could not close an SSH tunnel for HiveAP " + hiveApMac, sce );
		}
	}

	/**
	 * Close all SshConnections contained in the SshConnection map.
	 */
	public void closeAllSshConnections() {
		Map<String, SshConnection> sshConnections = (Map<String, SshConnection>) HmContextListener.context.getAttribute( SSH_CONNECTIONS );

		if ( sshConnections != null ) {
			synchronized ( sshConnections ) {
				for ( SshConnection sshConnection : sshConnections.values() ) {
					closeSshConnection( sshConnection );
				}

				// Clear the ConnectionMap after all SshConnections were closed.
				sshConnections.clear();
			}
		}
	}

	public String showSshTunnel( HiveAp hiveAp ) throws SshConnectException {
		String hiveApMac = hiveAp.getMacAddress();
		String showSshTunnelCli = AhCliFactory.showSshTunnel();
		String[] exeClis = new String[] { showSshTunnelCli };
		int reqSeqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		BeCliEvent showSshTunnelReq = new BeCliEvent();
		showSshTunnelReq.setAp( hiveAp );
		showSshTunnelReq.setClis( exeClis );
		showSshTunnelReq.setSequenceNum( reqSeqNum );

		if ( log.isDebugEnabled() ) {
			log.debug( "Sending an SSH tunnel query request to HiveAP " + hiveApMac );
		}

		try {
			showSshTunnelReq.buildPacket();
		} catch ( Exception e ) {
			log.error( "Failed to build an SSH tunnel query request to HiveAP " + hiveApMac, e );
			tracer.error( "showSshTunnel", "Failed to build an SSH tunnel query request to HiveAP " + hiveApMac, e );
			throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.failed" ), e );
		}

		BeCommunicationEvent showSshTunnelResp = AhAppContainer.getBeCommunicationModule().sendSyncRequest( showSshTunnelReq, 110 );
		int respMsgType = showSshTunnelResp.getMsgType();

		switch ( respMsgType ) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
				try {
					showSshTunnelResp.parsePacket();
				} catch ( Exception e ) {
					log.error( "Failed to parse the SSH tunnel query response from HiveAP " + hiveApMac, e );
					tracer.error( "showSshTunnel", "Failed to parse the SSH tunnel query response from HiveAP " + hiveApMac, e );
					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.response.parsing.failed" ), e );
				}

				BeCapwapCliResultEvent cliExeRetEvent = (BeCapwapCliResultEvent) showSshTunnelResp;

				if ( !cliExeRetEvent.isCliSuccessful() ) {
					String errorCli = cliExeRetEvent.getErrorCli();
					log.error( "Failed to retrieve the SSH tunnel information from HiveAP " + hiveApMac + " with CLI '" + errorCli + "'" );
					tracer.error( "showSshTunnel", "Failed to retrieve the SSH tunnel information from HiveAP " + hiveApMac + " with CLI '" + errorCli + "'" );
					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.rejected" ) );
				}

				String sshTunnelInfo = cliExeRetEvent.getCliSucceedMessage();

				if ( log.isInfoEnabled() ) {
					log.info( "Successfully retrieve the SSH tunnel information from HiveAP " + hiveApMac + "; Tunnel Info:\n " + sshTunnelInfo );
				}

				return sshTunnelInfo;
			case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				String errMsg;
				BeCliEvent respEvent = (BeCliEvent) showSshTunnelResp;
				byte respResult = respEvent.getResult();
				tracer.info( "showSshTunnel", "Received CLI response: " + respResult );

				switch ( respResult ) {
					case BeCommunicationConstant.RESULTTYPE_NOFSM:
					case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
					case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.capwap.disconnected" );
						break;
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.capwap.timeout" );
						break;
					case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
					case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.limit.exceeded" );
						break;
					case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
					default:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.failure.unknown" );
						break;
				}

				throw new SshConnectException( errMsg );
			default:
				throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.query.request.failure.unknown" ) );
		}
	}

	public int setupSshTunnel( HiveAp hiveAp, int tunnelTimeout ) throws SshConnectException {
		/*
		String sshTunnelInfo = showSshTunnel( hiveAp );
		Map<String, String> tunnelInfoMap = parseSshTunnelInfo( sshTunnelInfo );
		String tunnelServer = tunnelInfoMap.get( "SSH server" );

		if ( tunnelServer != null && !tunnelServer.trim().isEmpty() ) {
			throw new SshConnectException( "There is an SSH tunnel existing in the selected HiveAP and it cannot set up an additional SSH tunnel to HiveManager for the moment." );
		}*/

		int tunnelPort = getAvailableTunnelPort();
		String sshServer = hiveAp.getCapwapLinkIp();
		int sshServerPort = AhAppContainer.getBeAdminModule().getSshdPort();
		String sshServerUser = NmsUtil.getHMScpUser();
		String sshServerPwd = NmsUtil.getHMScpPsd();

		syncSetupSshTunnel( hiveAp, sshServer, sshServerPort, tunnelPort, sshServerUser, sshServerPwd, tunnelTimeout );

		return tunnelPort;
	}

	/**
	 * Set up an SSH tunnel between HiveAP and SSH server synchronously.
	 *
	 * @param hiveAp where an SSH tunnel connection starts.
	 * @param sshServer domain name or IP address of the SSH server.
	 * @param sshServerPort number of port for SSH server.
	 * @param tunnelPort the port number that the SSH server uses to identify the tunnel.
	 * @param sshServerUser the user name for logging in to the SSH server.
	 * @param sshServerPwd password for logging in to the SSH server.
	 * @param tunnelTimeout tunnel timeout in minutes during which the tunnel between the HiveAP and the SSH server will be up.
	 * @throws SshConnectException thrown if an SSH tunnel setup fails for any reason.
	 */
	public void syncSetupSshTunnel( HiveAp hiveAp, String sshServer, int sshServerPort, int tunnelPort, String sshServerUser, String sshServerPwd, int tunnelTimeout ) throws SshConnectException {
		String hiveApMac = hiveAp.getMacAddress();
		String closeSshTunnelCli = AhCliFactory.closeSshTunnel();
		String setupSshTunnelCli = AhCliFactory.setupSshTunnel( sshServer, sshServerPort, tunnelPort, sshServerUser, sshServerPwd, tunnelTimeout );
		String[] exeClis = new String[] { closeSshTunnelCli, setupSshTunnelCli };
		int reqSeqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		BeCliEvent setupSshTunnelReq = new BeCliEvent();
		setupSshTunnelReq.setAp( hiveAp );
		setupSshTunnelReq.setClis( exeClis );
		setupSshTunnelReq.setSequenceNum( reqSeqNum );

		if ( log.isDebugEnabled() ) {
			log.debug( "Sending request for setting up an SSH tunnel from HiveAP " + hiveApMac + "  to HiveManager. SSH server = " + sshServer + ", tunnel port = " + tunnelPort + ", tunnel timeout = " + tunnelTimeout );
		}

		try {
			setupSshTunnelReq.buildPacket();
		} catch ( Exception e ) {
			log.error( "Failed to build SSH tunnel setup request to HiveAP '" + hiveApMac + "'.", e );
			tracer.error( "syncSetupSshTunnel", "Failed to build SSH tunnel setup request to HiveAP '" + hiveApMac + "'.", e );
			throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.failed" ), e );
		}

		BeCommunicationEvent setupSshTunnelResp = AhAppContainer.getBeCommunicationModule().sendSyncRequest( setupSshTunnelReq, 110 );
		int respMsgType = setupSshTunnelResp.getMsgType();

		switch ( respMsgType ) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
				 // Response of an SSH tunnel setup was received.
				try {
					setupSshTunnelResp.parsePacket();
				} catch ( Exception e ) {
					log.error( "Failed to parse SSH tunnel setup response for HiveAP '" + hiveApMac + "'.", e );
					tracer.error( "syncSetupSshTunnel", "Failed to parse SSH tunnel setup response for HiveAP '" + hiveApMac + "'.", e );
					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.response.parsing.failed" ), e );
				}

				BeCapwapCliResultEvent cliExeRetEvent = (BeCapwapCliResultEvent) setupSshTunnelResp;

				if ( !cliExeRetEvent.isCliSuccessful() ) {
					String errorCli = cliExeRetEvent.getErrorCli();

					if ( errorCli != null ) {
						errorCli = errorCli.replace( sshServerUser, "******" ).replace( sshServerPwd, "******" );
					}

					log.error( "Failed to set up SSH tunnel with CLI '" + errorCli + "' for HiveAP '" + hiveApMac + "'." );
					tracer.error( "syncSetupSshTunnel", "Failed to set up SSH tunnel with CLI '" + errorCli + "' for HiveAP '" + hiveApMac + "'." );
					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.rejected" ) );
				}

				if ( log.isInfoEnabled() ) {
					log.info( "Successfully set up an SSH connection from HiveAP " + hiveApMac + " to HiveManager." );
				}

				break;
			case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				String errMsg;
				BeCliEvent respEvent = (BeCliEvent) setupSshTunnelResp;
				byte respResult = respEvent.getResult();
				tracer.info( "syncSetupSshTunnel", "Received CLI response: " + respResult );

				switch ( respResult ) {
					case BeCommunicationConstant.RESULTTYPE_NOFSM:
					case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
					case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.capwap.disconnected" );
						break;
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.capwap.timeout" );
						break;
					case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
					case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.request.limit.exceeded" );
						break;
					case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
					default:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.failure.unknown" );
						break;
				}

				throw new SshConnectException( errMsg );
			default:
				throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.setup.request.failure.unknown" ) );
		}
	}

	/*-
	private int getTunnelPort( HiveAp hiveAp ) throws SshConnectException {
		String showSshTunnelCli = AhCliFactory.showSshTunnel();
		String[] exeClis = new String[] { showSshTunnelCli };
		int reqSeqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		BeCliEvent showSshTunnelReq = new BeCliEvent();
		showSshTunnelReq.setAp( hiveAp );
		showSshTunnelReq.setClis( exeClis );
		showSshTunnelReq.setSequenceNum( reqSeqNum );
		String hiveApMac = hiveAp.getMacAddress();
		tracer.info( "getTunnelPort", "Sending " + exeClis.toString() + " CLI command to HiveAP " + hiveApMac );

		try {
			showSshTunnelReq.buildPacket();
		} catch ( Exception e ) {
			tracer.error( "getTunnelPort", "Failed to build SSH tunnel display request. HiveAP: " + hiveAp, e );
			throw new SshConnectException( "Due to an internal error, HiveManager was unable to get the SSH tunnel information from selected HiveAP.", e );
		}

		BeCommunicationEvent showSshTunnelResp = AhAppContainer.getBeCommunicationModule().sendSyncRequest( showSshTunnelReq, 110 );
		int respMsgType = showSshTunnelResp.getMsgType();

		switch ( respMsgType ) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
				try {
					showSshTunnelResp.parsePacket();
				} catch ( Exception e ) {
					tracer.error( "getTunnelPort", "Failed to parse SSH tunnel setup response for HiveAP '" + hiveApMac + "'.", e );
					throw new SshConnectException( "Due to an internal error, HiveManager was unable to setup an SSH connection to the selected HiveAP.", e );
				}

				BeCapwapCliResultEvent cliExeRetEvent = (BeCapwapCliResultEvent) showSshTunnelResp;

				if ( !cliExeRetEvent.isCliSuccessful() ) {
					String errorCli = cliExeRetEvent.getErrorCli();
					tracer.error( "getTunnelPort", "Failed to show SSH tunnel with CLI '" + errorCli + "' for HiveAP '" + hiveApMac + "'." );
					throw new SshConnectException( "HiveManager could not get the SSH tunnel information from the selected HiveAP." );
				}

				String sshTunnelInfo = cliExeRetEvent.getCliSucceedMessage();
				int tunnelPortIndex = sshTunnelInfo.indexOf( "Tunnel port:" );
				int enterMarkIndex = sshTunnelInfo.indexOf( "\n", tunnelPortIndex );
				String strTunnelPort = sshTunnelInfo.substring( tunnelPortIndex + "Tunnel port:".length(), enterMarkIndex ).trim();
				tracer.info( "getTunnelPort", "Successfully got SSH tunnel information from HiveAP " + hiveApMac + "\n" + sshTunnelInfo );
				int tunnelPort = 0;

				if ( !strTunnelPort.isEmpty() ) {
					try {
						tunnelPort = Integer.parseInt( strTunnelPort );
					} catch ( NumberFormatException nfe ) {
						tracer.error( "getTunnelPort", "Failed to parse SSH tunnel port " + strTunnelPort + " for HiveAP " + hiveApMac, nfe );
					}
				}

				return tunnelPort;
			case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				String errMsg;
				BeCliEvent respEvent = (BeCliEvent) showSshTunnelResp;
				byte respResult = respEvent.getResult();
				tracer.info( "getTunnelPort", "Received CLI response: " + respResult );

				switch ( respResult ) {
					case BeCommunicationConstant.RESULTTYPE_NOFSM:
					case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
					case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
						errMsg = "HiveManager was unable to setup an SSH connection to the selected HiveAP. Please confirm that the CAPWAP connection between HiveManager and the HiveAP is up.";
						break;
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
						errMsg = "The SSH connection setup request to the selected HiveAP timed out.";
						break;
					case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
					case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
						errMsg = "Since there are two requests being executed by the selected HiveAP, HiveManager is unable to send an SSH connection setup request to the HiveAP for the moment.";
						break;
					case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
					default:
						errMsg = "Due to an internal error, HiveManager was unable to setup an SSH connection to the selected HiveAP.";
						break;
				}

				throw new SshConnectException( errMsg );
			default:
				throw new SshConnectException( "Due to an internal error, HiveManager was unable to setup an SSH connection to the selected HiveAP." );
		}
	}*/

	/**
	 * Close the an SSH tunnel built on the specified HiveAP asynchronously.
	 *
	 * @param hiveAp on which the SSH tunnel connection is to be closed.
	 * @return the sequence number of SSH tunnel close request over CAPWAP to the specified HiveAP.
	 * @throws SshConnectException thrown if an SSH tunnel setup fails for any reason.
	 */
	public int closeSshTunnel( HiveAp hiveAp ) throws SshConnectException {
		String hiveApMac = hiveAp.getMacAddress();
		String closeSshTunnelCli = AhCliFactory.closeSshTunnel();
		String[] exeClis = new String[] { closeSshTunnelCli };
		int reqSeqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		BeCliEvent closeSshTunnelReq = new BeCliEvent();
		closeSshTunnelReq.setAp( hiveAp );
		closeSshTunnelReq.setClis( exeClis );
		closeSshTunnelReq.setSequenceNum( reqSeqNum );

		if ( log.isDebugEnabled() ) {
			log.debug( "Sending request for closing an SSH tunnel for HiveAP " + hiveApMac );
		}

		try {
			closeSshTunnelReq.buildPacket();

			return AhAppContainer.getBeCommunicationModule().sendRequest( closeSshTunnelReq );
		} catch ( Exception e ) {
			log.error( "Failed to build SSH tunnel close request for HiveAP '" + hiveApMac + "'.", e );
			tracer.error( "closeSshTunnel", "Failed to build SSH tunnel close request for HiveAP '" + hiveApMac + "'.", e );
			throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.failed" ), e );
		}
	}

	/**
	 * Close the an SSH tunnel built on the specified HiveAP synchronously.
	 *
	 * @param hiveAp on which the SSH tunnel connection is to be closed.
	 * @throws SshConnectException thrown if an SSH tunnel setup fails for any reason.
	 */
	public void syncCloseSshTunnel( HiveAp hiveAp ) throws SshConnectException {
		String hiveApMac = hiveAp.getMacAddress();
		String closeSshTunnelCli = AhCliFactory.closeSshTunnel();
		String[] exeClis = new String[] { closeSshTunnelCli };
		int reqSeqNum = AhAppContainer.getBeCommunicationModule().getSequenceNumber();
		BeCliEvent closeSshTunnelReq = new BeCliEvent();
		closeSshTunnelReq.setAp( hiveAp );
		closeSshTunnelReq.setClis( exeClis );
		closeSshTunnelReq.setSequenceNum( reqSeqNum );

		if ( log.isDebugEnabled() ) {
			log.debug( "Sending request for closing an SSH tunnel for HiveAP " + hiveApMac );
		}

		try {
			closeSshTunnelReq.buildPacket();
		} catch ( Exception e ) {
			log.error( "Failed to build SSH tunnel close request for HiveAP '" + hiveApMac + "'.", e );
			tracer.error( "syncCloseSshTunnel", "Failed to build SSH tunnel close request for HiveAP '" + hiveApMac + "'.", e );
			throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.failed" ), e );
		}

		BeCommunicationEvent closeSshTunnelResp = AhAppContainer.getBeCommunicationModule().sendSyncRequest( closeSshTunnelReq, 110 );
		int respMsgType = closeSshTunnelResp.getMsgType();

		switch ( respMsgType ) {
			case BeCommunicationConstant.MESSAGEELEMENTTYPE_CLIRESULT:
				 // Response of an SSH tunnel close was received.
				try {
					closeSshTunnelResp.parsePacket();
				} catch ( Exception e ) {
					log.error( "Failed to parse SSH tunnel close response for HiveAP '" + hiveApMac + "'.", e );
					tracer.error( "syncCloseSshTunnel", "Failed to parse SSH tunnel close response for HiveAP '" + hiveApMac + "'.", e );
					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.response.parsing.failed" ), e );
				}

				BeCapwapCliResultEvent cliExeRetEvent = (BeCapwapCliResultEvent) closeSshTunnelResp;

				if ( !cliExeRetEvent.isCliSuccessful() ) {
					String errorCli = cliExeRetEvent.getErrorCli();

					if ( log.isDebugEnabled() ) {
						log.debug( "Failed to close an SSH tunnel with the CLI " + errorCli + " for HiveAP " + hiveApMac );
					}

					throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.rejected" ) );
				}

				if ( log.isInfoEnabled() ) {
					log.info( "Successfully closed an SSH tunnel for HiveAP " + hiveApMac );
				}

				break;
			case BeCommunicationConstant.MESSAGETYPE_CLIRSP:
				String errMsg;
				BeCliEvent respEvent = (BeCliEvent) closeSshTunnelResp;
				byte respResult = respEvent.getResult();
				tracer.info( "syncCloseSshTunnel", "Received CLI response result: " + respResult );

				switch ( respResult ) {
					case BeCommunicationConstant.RESULTTYPE_NOFSM:
					case BeCommunicationConstant.RESULTTYPE_FSMNOTRUN:
					case BeCommunicationConstant.RESULTTYPE_CONNECTCLOSE:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.capwap.disconnected" );
						break;
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT_NORESULT:
					case BeCommunicationConstant.RESULTTYPE_TIMEOUT:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.capwap.timeout" );
						break;
					case BeCommunicationConstant.RESULTTYPE_MESSAGELENEXCEEDLIMIT:
					case BeCommunicationConstant.RESULTTYPE_RESOUCEBUSY:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.limit.exceeded" );
						break;
					case BeCommunicationConstant.RESULTTYPE_UNKNOWNMSG:
					default:
						errMsg = MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.failure.unknown" );
						break;
				}

				throw new SshConnectException( errMsg );
			default:
				throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.client.ssh.tunnel.close.request.failure.unknown" ) );
		}
	}

    //***************************************************************
    // Private Parameter Access Methods
    //***************************************************************

	/**
	 * Returns the SshConnectMode to be used.
	 *
	 * @param hiveAp which contains some useful information (e.g. software version, CAPWAP link IP) used to judge what kind of connect mode to be used.
	 * @return an SSH connect mode to be used.
	 */
	private SshConnectMode getSshConnectMode( HiveAp hiveAp ) {
		// Totally use the tunnel mode to SSH to the HiveAPs with the software version no less than "3.2.0.0".
		return NmsUtil.compareSoftwareVersion( "3.2.0.0", hiveAp.getSoftVer() ) > 0 ? SshConnectMode.DEFAULT : SshConnectMode.TUNNEL;
	}

	/*-
	private Map<String, String> parseSshTunnelInfo( String sshTunnelInfo ) {
		StringTokenizer tokenizer = new StringTokenizer( sshTunnelInfo, "\n" );
		Map<String, String> tunnelInfoMap = new HashMap<String, String>( tokenizer.countTokens() );

		while ( tokenizer.hasMoreTokens() ) {
			String token = tokenizer.nextToken();
			int colonMarkPos = token.indexOf( ":" );

			if ( colonMarkPos != -1 ) {
				String key = token.substring( 0, colonMarkPos ).trim();
				String value = token.substring( colonMarkPos + 1 ).trim();
				tunnelInfoMap.put( key, value );
			}
		}

		return tunnelInfoMap;
	}*/

	private synchronized int getAvailableTunnelPort() throws SshConnectException {
		try {
			return AvailablePortFinder.getNextAvailable( SSH_TUNNEL_PORT_FROM, SSH_TUNNEL_PORT_TO );
		} catch ( Exception e ) {
			throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.tunnel.setup.no.free.port.found" ), e );
		}

		/*-
		Collection<Integer> usingPorts;

		try {
			usingPorts = NmsUtil.checkUsingPorts();
		} catch ( Exception e ) {
			throw new SshConnectException( "Cannot check out an available port for setting up an SSH tunnel from the specified HiveAP to HiveManager, please try again later.", e );
		}

		Collection<Integer> sshTunnelPorts = new ArrayList<Integer>( AhConstantUtil.getSshTunnelPorts() );
		sshTunnelPorts.removeAll( usingPorts );

		if ( sshTunnelPorts.isEmpty() ) {
			throw new SshConnectException( "Cannot check out an available port for setting up an SSH tunnel from the specified HiveAP to HiveManager, please try again later." );
		} else {
			return sshTunnelPorts.iterator().next();
		}*/
	}

}