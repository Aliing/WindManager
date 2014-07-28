/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/sshwebproxy/SshConnection.java,v $
 * $Revision: 1.26.10.1 $
 * $Author: cchen $
 * $Date: 2013/01/15 09:19:02 $
 ******************************************************************************
 * Copyright (c) 2003, Eric Daugherty (http://www.ericdaugherty.com)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Eric Daugherty nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 * *****************************************************************************
 * For current versions and more information, please visit:
 * http://www.ericdaugherty.com/dev/sshwebproxy
 *
 * or contact the author at:
 * web@ericdaugherty.com
 *****************************************************************************/

package com.ericdaugherty.sshwebproxy;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.text.MessageFormat;
import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.SshClient;
import com.sshtools.j2ssh.authentication.AuthenticationProtocolState;
import com.sshtools.j2ssh.authentication.PasswordAuthenticationClient;
import com.sshtools.j2ssh.authentication.PublicKeyAuthenticationClient;
import com.sshtools.j2ssh.configuration.ConfigurationException;
import com.sshtools.j2ssh.configuration.ConfigurationLoader;
import com.sshtools.j2ssh.configuration.SshConnectionProperties;
import com.sshtools.j2ssh.session.SessionChannelClient;
import com.sshtools.j2ssh.transport.IgnoreHostKeyVerification;
import com.sshtools.j2ssh.transport.publickey.InvalidSshKeyException;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;

import com.ah.bo.hiveap.HiveAp;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

/**
 * SshConnection represents an SSH connection
 * between the local host and a remote SSH daemon.
 * A single SshConnection may contain multiple channels,
 * which may be file transfer channels or shell channels.
 *
 * @author Eric Daugherty
 */
public class SshConnection implements SshConstants {

    //***************************************************************
    // Variables
    //***************************************************************

	private static final long serialVersionUID = 1L;

	/** The base ID to calculate new ID to assign a new connection */
	private static int baseId;

	/** The SSHClient instance */
    private SshClient sshClient;

    /** Information about the current connection */
    private String connectionInfo;

	/** Stores all active SshChannels. */
    private final Map<String, SshChannel> channelMap;

    /** The next ID to assign to a channel */
    private int nextChannelId = 0;

	/** The next ID to assign to a connection */
	private String connectionId;

	/** The remote HiveAP to connect to */
	private HiveAp hiveAp;

	/** SSH Connect Mode to be used */
	private SshConnectMode sshConnectMode;

	/** Logger */
    private static final Log log = LogFactory.getLog( SshConnection.class );

	/** Native logger */
	private static final Tracer tracer = new Tracer(SshConnection.class.getSimpleName());

    //***************************************************************
    // Static Initialization
    //***************************************************************

    // Initialize the SSH Library
    static {
        try {
            ConfigurationLoader.initialize( false );
        } catch ( ConfigurationException e ) {
            log.error( "Error configuring SSH Library: " + e, e );
            throw new ExceptionInInitializerError( "Unable to initialize SSH Library: " + e );
        }
    }

    //***************************************************************
    // Constructors
    //***************************************************************

    /**
     * Performs common constructor logic.
     */
    private SshConnection() {
		channelMap = Collections.synchronizedMap( new HashMap<String, SshChannel>() );
    }

    /**
     * Initialize a new SshConnection with the SshClient connection.
     *
     * @param sshClient the sshClient that represents the connection.
	 * @param connectionInfo the information about the current connection.
     */
	public SshConnection( SshClient sshClient, String connectionInfo ) {
		this();
		this.sshClient = sshClient;
		this.connectionInfo = connectionInfo;
	}

    /**
     * Create a new SshConnection to the specified location
     * with the specified username and password.
     *
     * @param host the remote host to connect to.
     * @param port the port to connect to.
     * @param username the username to login with.
     * @param password the password to login with.
	 * @param timeout timeout in milliseconds without any activity before the timeout event occurs.
	 * @param hiveAp the remote HiveAP to connect to.
	 * @param sshConnectMode the connection mode to be selected. Default or Tunnel.
     * @throws SshConnectException thrown if the connection attempt fails for any reason.
     */
    public SshConnection( String host, int port, String username, String password, int timeout, HiveAp hiveAp, SshConnectMode sshConnectMode ) throws SshConnectException {
        this();

        // Verify the parameters are not null or invalid.
//		if ( host == null || host.trim().length() == 0 || port < 1 ||
//			username == null || username.trim().length() == 0 ||
//			password == null || password.trim().length() == 0 ) {
//			throw new SshConnectException( "Missing parameter.  All parameters must be at least one character." );
//		}

		if ( host == null || host.trim().length() == 0 ) {
			throw new SshConnectException( "Invalid host: " + host );
		}

		if ( port < 1 ) {
			throw new SshConnectException( "Invalid port: " + port + ". The available number should be between 1 and 65535." );
		}

		if ( username == null || username.trim().length() == 0 ) {
			throw new SshConnectException( "Invalid username: " + username );
		}

		if ( password == null || password.trim().length() == 0 ) {
			throw new SshConnectException( "Invalid password: " + password );
		}

		this.connectionInfo = getConnectionInfo( host, port, username );
		this.hiveAp = hiveAp;
		this.sshConnectMode = sshConnectMode;

		if ( log.isDebugEnabled() ) {
			log.debug( connectionInfo + " - Attempting to Open Connection." );
		}

		// Initialize the SSH library
        SshConnectionProperties properties = new SshConnectionProperties();
        properties.setHost( host );
        properties.setPort( port );

		sshClient = new SshClient();
		sshClient.setSocketTimeout( timeout );
		sshClient.setUseDefaultForwarding( false );

		// Sleep before connecting to host so as to alleviate the problem of java.net.ConnectException: Connection refused
//		try {
//			Thread.sleep( 10000L );
//		} catch (InterruptedException ie) {
//			log.error( "Sleep Interrupted.", ie );
//		}

		// Connect to the host
        try {
            sshClient.connect( properties, new IgnoreHostKeyVerification() );

			if ( log.isDebugEnabled() ) {
				log.debug( "Connect is Successful." );
			}

			// Initialize the authentication data.
            PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
            pwd.setUsername( username );
            pwd.setPassword( password );

            // Authenticate.
            int result = sshClient.authenticate( pwd );

			if ( result != AuthenticationProtocolState.COMPLETE ) {
                throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.connection.open.auth.failed" ) );
            }

			if ( log.isDebugEnabled() ) {
				log.debug( "Authentication is Successful." );
			}

			// Assign connection id to the new connection opened.
			connectionId = generateNewConnectionId();
		} catch ( UnknownHostException unknownHostException ) {
			String errorMsg = MgrUtil.getUserMessage( "error.ssh.connection.open.unknown.host" );
			tracer.error( "SshConnection", errorMsg, unknownHostException );
			throw new SshConnectException( errorMsg );
        } catch ( ConnectException connectException ) {
			String errorMsg = MgrUtil.getUserMessage( "error.ssh.connection.open.connection.refused" );
			tracer.error( "SshConnection", errorMsg, connectException );
			throw new SshConnectException( errorMsg );
		} catch ( NoRouteToHostException noRouteToHostException ) {
			String errorMsg = MgrUtil.getUserMessage( "error.ssh.connection.open.no.route" );
			tracer.error( "SshConnection", errorMsg, noRouteToHostException );
			throw new SshConnectException( errorMsg );
		} catch ( IOException ioException ) {
			String errorMsg = MgrUtil.getUserMessage( "error.ssh.connection.open.failed.withReason", ioException.getMessage() );
			tracer.error( "SshConnection", errorMsg, ioException );
			throw new SshConnectException( errorMsg );
        }

        // Success!
        if ( log.isInfoEnabled() ) {
			log.info( connectionInfo + " - Connection opened successfully." );
		}
    }

	public SshConnection( String host, int port, String username, String password ) throws SshConnectException {
		this( host, port, username, password, 900000, null, null );
	}

	/**
	 *
	 * @param host the remote host to connect to.
	 * @param port the port to connect to.
	 * @param username the username to login with.
	 * @param key the SSH Key as a byte array.
	 * @param keyPassPhrase the passPharse for the key (optional)
	 * @throws SshConnectException thrown if the connection attempt fails for any reason.
	 */
	public SshConnection( String host, int port, String username, byte[] key, String keyPassPhrase ) throws SshConnectException {
		this();

		// Verify the parameters are not null or invalid.
		if ( host == null || host.trim().length() == 0 || port < 1 ||
			username == null || username.trim().length() == 0 ||
			key == null ) {
			throw new SshConnectException( "Missing parameter.  All parameters must be at least one character." );
		}

		connectionInfo = getConnectionInfo( host, port, username );

		if ( log.isDebugEnabled() ) {
			log.debug( connectionInfo + " - Attempting to Open Connection." );
		}

		// Initialize the SSH library
		sshClient = new SshClient();
		sshClient.setSocketTimeout( 30000 );
		SshConnectionProperties properties = new SshConnectionProperties();
		properties.setHost( host );
		properties.setPort( port );

		// Connect to the host
		try {
			sshClient.connect( properties, new IgnoreHostKeyVerification() );

			if ( log.isDebugEnabled() ) {
				log.debug( "Connect Successful." );
			}

			// Initialize the authentication data.
			PublicKeyAuthenticationClient publicKeyAuth = new PublicKeyAuthenticationClient();

			publicKeyAuth.setUsername( username );

			SshPrivateKeyFile file = SshPrivateKeyFile.parse( key );
			SshPrivateKey privateKey = file.toPrivateKey( keyPassPhrase );
			publicKeyAuth.setKey( privateKey );

			// Authenticate
			int result = sshClient.authenticate( publicKeyAuth );

			if ( result != AuthenticationProtocolState.COMPLETE ) {
				throw new SshConnectException( "Authentication Error.  Invalid username or password." );
			}

			if ( log.isDebugEnabled() ) {
				log.debug( "Authentication Successful." );
			}
		} catch ( InvalidSshKeyException invalidSshKeyException ) {
			throw new SshConnectException( "Unable to connect.  Invalid SSH Key.  " + invalidSshKeyException.getMessage() );
		} catch ( UnknownHostException unknownHostException ) {
			throw new SshConnectException( "Unable to connect.  Unknown host." );
		} catch ( IOException ioException ) {
			log.warn( "IOException occurred in SshConnection constructor.  " + ioException, ioException );
			throw new SshConnectException( "Unable to connect to host." );
		}

		// Success!
		if ( log.isInfoEnabled() ) {
			log.info( connectionInfo + " - Connection opened successfully." );
		}
	}

	//***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public String getConnectionId() {
		return connectionId;
	}

	public HiveAp getHiveAp() {
		return hiveAp;
	}

	public SshConnectMode getSshConnectMode() {
		return sshConnectMode;
	}

	/**
     * Returns information about this connection.  The information
     * consists of the username, the host, and the port.  The result
     * is formatted as: username@host:port
     *
     * @return formated string: username@host:port
     */
    public String getConnectionInfo() {
        return connectionInfo;
    }

	/**
	 * Helper method to return the connection info.
	 *
	 * @param host the remote host to connect to.
	 * @param port the port to connect to.
	 * @param username the username to login with.
	 * @return a property formatted connection info string.
	 */
	public static String getConnectionInfo( String host, String port, String username ) {
		return MessageFormat.format( "{0}@{1}:{2}", username.trim(), host.trim(), port.trim() );
	}

	/**
	 * Helper method to return the connection info.
	 *
	 * @param host the remote host to connect to.
	 * @param port the port to connect to.
	 * @param username the username to login with.
	 * @return a property formatted connection info string.
	 */
	public static String getConnectionInfo( String host, int port, String username ) {
		return getConnectionInfo( host, String.valueOf( port ), username );
	}

	public static Object[] parseConnectionInfo( String connectionInfo ) throws ParseException {
    	MessageFormat format = new MessageFormat( "{0}@{1}:{2}" );
    	return format.parse( connectionInfo );
    }

	//***************************************************************
    // Public Methods
    //***************************************************************

    /**
     * Returns true if this SshConnection is open.
     *
     * @return true if it is open.
     */
    public boolean isOpen() {
        return sshClient.isConnected();
    }

    /**
     * Closes all open channels and the current SshConnection.
     */
    public void close() {
		if ( !isOpen() ) {
			return;
		}

		if ( log.isInfoEnabled() ) {
			log.info( connectionInfo + " - Closing Connection." );
		}

		synchronized ( channelMap ) {
//			for (SshChannel sshChannel : channelMap.values()) {
//				String channelId = sshChannel.getChannelId();
//
//				if ( log.isInfoEnabled() ) {
//					log.info( "Closing channel " + channelId + " for Connection " + connectionInfo );
//				}
//
//				sshChannel.close();
//
//				if ( log.isInfoEnabled() ) {
//					log.info( "Channel " + channelId + " for Connection " + connectionInfo + " was closed." );
//				}
//			}
//
//			channelMap.clear();

			for ( Iterator<SshChannel> sshChannelInterator = channelMap.values().iterator(); sshChannelInterator.hasNext(); ) {
				SshChannel sshChannel = sshChannelInterator.next();
				sshChannelInterator.remove();
				String channelId = sshChannel.getChannelId();

				sshChannel.close();

				if ( log.isInfoEnabled() ) {
					log.info( "Channel " + channelId + " for Connection " + connectionInfo + " was closed." );
				}
			}
		}

		sshClient.disconnect();

        if ( log.isInfoEnabled() ) {
			log.info( connectionInfo + " - Connection was closed." );
		}
	}

	public int getActiveChannelCount() {
		return sshClient != null ? sshClient.getActiveChannelCount() : 0;
	}

	/**
     * Returns the requested channel.
     *
     * @param channelId the channel's unique id.
     * @return the requested channel, or null if it does not exist.
     */
    public SshChannel getChannel( String channelId ) {
        return channelMap.get( channelId );
    }

    /**
	 * @return all channels.
     */
    public Collection<SshChannel> getChannels() {
        return channelMap.values();
    }

	/**
     * Open a new Shell Channel for this connection.
     *
     * @return a newly opened ShellChannel
     * @throws SshConnectException if the channel could not be opened.
     */
    public ShellChannel openShellChannel() throws SshConnectException {
        if ( log.isInfoEnabled() ) {
			log.info( connectionInfo + " - Opening new ShellChannel" );
		}

        try {
            SessionChannelClient sessionChannelClient = sshClient.openSessionChannel();
			ShellChannel shellChannel = new VT100ShellChannel( this, sessionChannelClient );

            // Generate a channelId for the channel and add it to the local map.
//			String channelId = String.valueOf( nextChannelId++ );
			String channelId = generateNewChannelId();
			shellChannel.setChannelId( channelId );

			synchronized ( channelMap ) {
				channelMap.put( channelId, shellChannel );
			}

            return shellChannel;
        } catch ( IOException ioException ) {
            log.warn( "openShellChannel failed, unable to open Session Channel: " + ioException, ioException );
			tracer.error( "openShellChannel", "Unable to open SessionChannel.", ioException );
            throw new SshConnectException( MgrUtil.getUserMessage( "error.ssh.session.channel.open.failed" ) );
        }
    }

    /**
     * Open a new File Channel for this connection.
     *
     * @return a newly opened FileChannel
     * @throws SshConnectException if the channel could not be opened.
     */
    public FileChannel openFileChannel() throws SshConnectException {
		if ( log.isInfoEnabled() ) {
			log.info( connectionInfo + " - Opening new FileChannel" );
		}

		FileChannel fileChannel = new FileChannel( this, sshClient );

		// Generate a channelId for the channel and add it to the local map.
//		String channelId = String.valueOf( nextChannelId++ );
		String channelId = generateNewChannelId();
		fileChannel.setChannelId( channelId );

		synchronized ( channelMap ) {
			channelMap.put( channelId, fileChannel );
		}

		return fileChannel;
    }

    /**
     * Returns the requested channel.
     *
     * @param channelId the channel's unique id.
     * @return the requested channel, or null if it does not exist.
     */
    public ShellChannel getShellChannel( String channelId ) {
		SshChannel channel = channelMap.get( channelId );

		// Return null if it does not exist or is the wrong type of channel.
//		if ( channel == null || !( channel instanceof ShellChannel ) ) {
//			return null;
//		}
//
//		return (ShellChannel) channel;

		return channel != null && channel instanceof ShellChannel ? (ShellChannel) channel : null;
	}

    /**
     * Returns a collection of all ShellChannels associated with this connection.
     *
     * @return will never be null.
     */
    public Collection<SshChannel> getShellChannels() {
        Collection<SshChannel> shellChannels = new ArrayList<SshChannel>();
//		Iterator channelIterator = channelMap.values().iterator();
//		SshChannel sshChannel;
//
//		while( channelIterator.hasNext() )  {
//			sshChannel = (SshChannel) channelIterator.next();
//
//			if ( CHANNEL_TYPE_SHELL.equals( sshChannel.getChannelType() ) ) {
//				shellChannels.add( sshChannel );
//			}
//		}

        for ( SshChannel sshChannel : channelMap.values() ) {
			if ( CHANNEL_TYPE_SHELL.equals( sshChannel.getChannelType() ) ) {
				shellChannels.add( sshChannel );
			}
        }

		return shellChannels;
    }

    /**
     * Returns the requested channel.
     *
     * @param channelId the channel's unique id.
     * @return the requested channel, or null if it does not exist.
     */
    public FileChannel getFileChannel( String channelId ) {
		SshChannel channel = channelMap.get( channelId );

        // Return null if it does not exist or is the wrong type of channel.
//		if ( channel == null || !( channel instanceof FileChannel ) ) {
//			return null;
//		}
//
//		return (FileChannel) channel;

		return channel != null && channel instanceof FileChannel ? (FileChannel) channel : null;
	}

    /**
     * Close a specific channel.  This calls channel.close()
     * and removes it from the channel list.
     *
     * @param channelId the channel to remove.
     */
    public void closeChannel( String channelId ) {
//		SshChannel sshChannel = getChannel( channelId );
//
//		if ( sshChannel != null ) {
//			sshChannel.close();
//
//			synchronized ( channelMap ) {
//				channelMap.remove( sshChannel.getChannelId() );
//			}
//		}

		synchronized ( channelMap ) {
			SshChannel sshChannel = channelMap.remove( channelId );

			if ( sshChannel != null ) {
				sshChannel.close();

				if ( log.isInfoEnabled() ) {
					log.info( "Channel " + channelId + " for Connection " + connectionInfo + " was closed." );
				}
			}
		}
	}

    /**
     * Close a specific channel.  This calls channel.close()
     * and removes it from the channel list.
     *
     * @param sshChannel the channel to remove.
     */
    public void closeChannel( SshChannel sshChannel ) {
//      sshChannel.close();
//
//		synchronized ( channelMap ) {
//			channelMap.remove( sshChannel.getChannelId() );
//		}

		closeChannel( sshChannel.getChannelId() );
	}

    //***************************************************************
    // Object Methods
    //***************************************************************

    /**
     * @return a string representation of this connection.
     */
    public String toString() {
		return MessageFormat.format( "Connected to {0} with {1} open channels.", getConnectionInfo(), String.valueOf( sshClient.getActiveChannelCount() ) );
    }

	private synchronized String generateNewChannelId() {
		return String.valueOf( nextChannelId++ );
	}

	private static synchronized String generateNewConnectionId() {
		int nextConnectionId = baseId >= Integer.MAX_VALUE || baseId <= Integer.MIN_VALUE ? 0 : baseId++;

		return String.valueOf( nextConnectionId );
	}

}