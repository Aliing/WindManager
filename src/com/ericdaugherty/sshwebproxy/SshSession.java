/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/sshwebproxy/SshSession.java,v $
 * $Revision: 1.13 $
 * $Author: ychen $
 * $Date: 2009/07/07 06:14:53 $
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ah.bo.hiveap.HiveAp;

/**
 * Wrapper class for the HTTPSession class.
 *
 * @author Eric Daugherty
 */
public class SshSession implements SshConstants {

    //***************************************************************
    // Constants
    //***************************************************************

	private static final String USER = "User";

    private static final String RESTRICTED_MODE_HOST = "RestrictedModeHost";

    private static final String ERROR_MESSAGE = "ErrorMessage";

//	private static final String SSH_CONNECTIONS = "SshConnections";

//	private static final String SSH_CHANNELS = "SshChannels";

	//***************************************************************
    // Variables
    //***************************************************************

    /** The HttpSession this class is wrapping. */
    private HttpSession session;
    
    private static final long serialVersionUID = 1L;

    /** Logger */
    private static final Log log = LogFactory.getLog( SshSession.class );

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Helper constructor.  Creates a new SSHWebProxy using
     * the current HttpServletRequest.
     *
     * @param request the current HttpServletRequest
     */
    public SshSession( HttpServletRequest request ) {
        this( request.getSession() );
    }

    /**
     * Creates a wrapper for the current HttpSession.
     *
     * @param session user's HttpSession
     */
    public SshSession( HttpSession session ) {
        this.session = session;
    }

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

    /**
     * Checks to makes sure that a user has logged into the system.
     *
     * @return true if a user is logged in.
     */
    public boolean isValid() {
        // If we are in restricted mode, the session is valid.
		if ( isRestrictedMode() ) {
			return true;
		}

        // Otherwise, check for a user.
        String username = (String) session.getAttribute( USER );

		if ( log.isDebugEnabled() ) {
			log.debug( "Verifying user is logged in.  Username: " + username );
		}

//		if ( username == null ) {
//			return false;
//		}
//
//		return true;

		return username != null;
	}

    /**
     * Returns the name of the current logged-in user,
     * or null if no user is logged in.
     *
     * @return Username, or null.
     */
    public String getUser() {
        return (String) session.getAttribute( USER );
    }

    /**
     * Sets a username into the session.
     *
     * @param user username.
     */
    public void setUser( String user ) {
        session.setAttribute( USER, user );
    }

    public boolean isRestrictedMode() {
        String restrictedModeHost = getRestrictedModeHost();

//		if ( restrictedModeHost != null && restrictedModeHost.length() > 0 ) {
//			return true;
//		}
//
//		return false;

		return restrictedModeHost != null && restrictedModeHost.length() > 0;
	}

    public String getRestrictedModeHost() {
        return (String) session.getAttribute( RESTRICTED_MODE_HOST );
    }

    public void setRestrictedModeHost( String host ) {
        session.setAttribute( RESTRICTED_MODE_HOST, host );
    }

    public String getErrorMessage() {
        return (String) session.getAttribute( ERROR_MESSAGE );
    }

    public void setErrorMessage( String errorMessage ) {
        session.setAttribute( ERROR_MESSAGE, errorMessage );
    }

//	/**
//	 * Returns the SshConnection for the given connectionInfo.
//	 * Returns null if the connection does not exist or has been closed.
//	 *
//	 * @param connectionInfo the connectionInfo unique to this connection.
//	 * @return the SshConnection or null if it does not exist or has been closed.
//	 */
//	public SshConnection getSshConnection( String connectionInfo ) {
//		Map<String, SshConnection> sshConnections = getConnectionMap();
//
//		synchronized ( sshConnections ) {
//			SshConnection sshConnection = sshConnections.get( connectionInfo );
//
//			// If it is unknown, or open, return it.
//			if ( sshConnection == null || sshConnection.isOpen() ) {
//				return sshConnection;
//			} else {
//				// If it has been closed, remove it and return null.
//				if ( log.isDebugEnabled() ) {
//					log.debug( connectionInfo + " connection is closed, removing from session." );
//				}
//
//				sshConnections.remove( connectionInfo );
//
//				return null;
//			}
//		}
//	}

	/**
	 * Returns the SshConnection by the HiveAP MAC given.
	 * Returns null if the connection does not exist or has been closed.
	 *
	 * @param hiveApMac the MAC of HiveAP connected with a specific SshConnection to be returned.
	 * @return the SshConnection or null if it does not exist or has been closed.
	 */
	public SshConnection getSshConnection( String hiveApMac ) {
		if ( hiveApMac == null || hiveApMac.trim().equals( "" ) ) {
			return null;
		}

		Map<String, SshConnection> sshConnections = getConnectionMap();

		synchronized ( sshConnections ) {
			SshConnection sshConnection = sshConnections.get( hiveApMac );

			if ( sshConnection != null && !sshConnection.isOpen() ) {
				// If it has been closed, close tunnel if applicable, remove it and return null.
				SshConnectionFactory.getInstance().closeSshConnection( sshConnection );

				String connectionId = sshConnection.getConnectionId();

				if ( log.isDebugEnabled() ) {
					log.debug( sshConnection.getConnectionInfo() + " - Connection " + connectionId + " has been closed, removing it from ConnectionMap and Session ChannelMap." );
				}

				sshConnections.remove( hiveApMac );

				if ( log.isDebugEnabled() ) {
					log.debug( sshConnection.getConnectionInfo() + " - Connection " + connectionId + " was removed from ConnectionMap." );
				}

				Map<String, Collection<String>> sshChannels = getChannelMap();

				synchronized ( sshChannels ) {
					Collection<String> channelIds = sshChannels.remove( connectionId );

					if ( channelIds != null ) {
						if ( log.isDebugEnabled() ) {
							log.debug( sshConnection.getConnectionInfo() + " - Connection " + connectionId + " as well as corresponding Channels were removed from Session ChannelMap." );
						}
					}
				}

				sshConnection = null;
			}

			return sshConnection;
		}
	}

	/**
	 * Returns the SshConnection by the HiveAP MAC given.
	 * Returns null if the connection does not exist or has been closed.
	 *
	 * @param hiveApMac the MAC of HiveAP connected with a specific SshConnection to be returned.
	 * @param connectionId the SshConnection returned with its id must match this value.
	 * @return the SshConnection or null if it does not exist or has been closed.
	 */
	public SshConnection getSshConnection( String hiveApMac, String connectionId ) {
		if ( hiveApMac == null || hiveApMac.trim().equals( "" ) || connectionId == null || connectionId.trim().equals( "" ) ) {
			return null;
		}

		Map<String, SshConnection> sshConnections = getConnectionMap();

		synchronized ( sshConnections ) {
			SshConnection sshConnection = sshConnections.get( hiveApMac );

			if ( sshConnection != null ) {
				if ( sshConnection.isOpen() ) {
					if ( !sshConnection.getConnectionId().equals( connectionId ) ) {
						if ( log.isDebugEnabled() ) {
							log.debug( "Connection " + connectionId + " has been closed, removing it from Session ChannelMap." );
						}

						Map<String, Collection<String>> sshChannels = getChannelMap();

						synchronized ( sshChannels ) {
							Collection<String> channelIds =	sshChannels.remove( connectionId );

							if ( channelIds != null ) {
								if ( log.isDebugEnabled() ) {
									log.debug( "Connection " + connectionId + " as well as corresponding Channels were removed from Session ChannelMap." );
								}
							}
						}

						sshConnection = null;
					}
				} else {
					// If it has been closed, close tunnel if applicable, remove it and return null.
					SshConnectionFactory.getInstance().closeSshConnection( sshConnection );

					if ( log.isDebugEnabled() ) {
						log.debug( sshConnection.getConnectionInfo() + " - Connection " + sshConnection.getConnectionId() + " has been closed, removing it from ConnectionMap and Session ChannelMap." );
					}

					sshConnections.remove( hiveApMac );

					if ( log.isDebugEnabled() ) {
						log.debug( sshConnection.getConnectionInfo() + " - Connection " + sshConnection.getConnectionId() + " was removed from ConnectionMap." );
					}

					Map<String, Collection<String>> sshChannels = getChannelMap();

					synchronized ( sshChannels ) {
						Collection<String> channelIds = sshChannels.remove( sshConnection.getConnectionId() );

						if ( channelIds != null ) {
							if ( log.isDebugEnabled() ) {
								log.debug( sshConnection.getConnectionInfo() + " - Connection " + sshConnection.getConnectionId() + " as well as corresponding Channels were removed from Session ChannelMap." );
							}
						}

						if ( !sshConnection.getConnectionId().equals( connectionId ) ) {
							channelIds = sshChannels.remove( connectionId );

							if ( channelIds != null ) {
								if ( log.isDebugEnabled() ) {
									log.debug( "Connection " + connectionId + " as well as corresponding Channels were removed from Session ChannelMap." );
								}
							}
						}
					}

					sshConnection = null;
				}
			}

			return sshConnection;
		}
	}

//	/**
//	 * Stores a new SshConnection in the session.
//	 *
//	 * @param sshConnection the connection to store.
//	 * @return false if this SshConnection is a duplicate.
//	 */
//	public boolean addSshConnection( SshConnection sshConnection ) {
//		String connectionInfo = sshConnection.getConnectionInfo();
//		Map<String, SshConnection> sshConnections = getConnectionMap();
//
//		synchronized ( sshConnections ) {
//			if ( sshConnections.containsKey( connectionInfo ) ) {
//				log.warn( "Error Adding new SshConnection. A connection already exists with the same connection info: " + connectionInfo );
//				return false;
//			}
//
//			sshConnections.put( connectionInfo, sshConnection );
//		}
//
//		if ( log.isDebugEnabled() ) {
//			log.debug( connectionInfo + " connection added to session.");
//		}
//
//		return true;
//	}

	/**
	 * Stores a new SshConnection into the ConnectionMap.
	 *
	 * @param sshConnection the connection to store.
	 * @return false if this SshConnection is a duplicate.
	 */
	public boolean addSshConnection( SshConnection sshConnection ) {
		if ( sshConnection == null ) {
			return false;
		}

		if ( log.isDebugEnabled() ) {
			log.debug( sshConnection.getConnectionInfo() + " - Adding Connection " + sshConnection.getConnectionId() + " into ConnectionMap." );
		}

		HiveAp hiveAp = sshConnection.getHiveAp();
		String hiveApMac = hiveAp.getMacAddress();
		Map<String, SshConnection> sshConnections = getConnectionMap();

		synchronized ( sshConnections ) {
			if ( sshConnections.containsKey( hiveApMac ) ) {
				log.warn( "Error Adding new SshConnection. A connection already exists for HiveAP " + hiveApMac );
				return false;
			}

			sshConnections.put( hiveApMac, sshConnection );
		}

		if ( log.isDebugEnabled() ) {
			log.debug( sshConnection.getConnectionInfo() + " - Connection " + sshConnection.getConnectionId() + " was added into ConnectionMap." );
		}

		return true;
	}

//	/**
//	 * Removes an SshConnection from the session.
//	 *
//	 * @param connectionInfo connection information.
//	 */
//	public void removeConnection( String connectionInfo ) {
//		if ( log.isDebugEnabled() ) {
//			log.debug( connectionInfo + " connection removed from session.");
//		}
//
//		Map<String, SshConnection> sshConnections = getConnectionMap();
//
//		synchronized ( sshConnections ) {
//			sshConnections.remove( connectionInfo );
//		}
//	}

	/**
	 * Removes the SshConnection from ConnectionMap. The destination HiveAP MAC should be equal to the argument given.
	 *
	 * @param hiveApMac the MAC of a HiveAP connected with a specific SshConnection to be removed.
	 */
	public void removeConnection( String hiveApMac ) {
		if ( hiveApMac == null || hiveApMac.trim().equals( "" ) ) {
			return;
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "Removing Connection for HiveAP " + hiveApMac );
		}

		Map<String, SshConnection> sshConnections = getConnectionMap();

		synchronized ( sshConnections ) {
			SshConnection sshConnection = sshConnections.remove( hiveApMac );

			if ( sshConnection != null ) {
				String connectionId = sshConnection.getConnectionId();

				if ( log.isDebugEnabled() ) {
					log.debug( sshConnection.getConnectionInfo() + " - Connection " + connectionId + " was removed from ConnectionMap." );
				}

				Map<String, Collection<String>> sshChannels = getChannelMap();

				synchronized ( sshChannels ) {
					Collection<String> channels = sshChannels.remove( connectionId );

					if ( channels != null ) {
						if ( log.isDebugEnabled() ) {
							log.debug( sshConnection.getConnectionInfo() + " - Connection " + connectionId + " as well as corresponding Channels were removed from Session ChannelMap." );
						}
					}
				}
			}
		}
	}

	/**
	 * Removes the SshConnection from ConnectionMap. The SshConnection needs to be removed must own the specified connectionId the same as the second argument as well as the destination HiveAP MAC should be equal to the first argument.
	 *
	 * @param hiveApMac the MAC of a HiveAP connected with a specific SshConnection to be removed.
	 * @param connectionId the identity of the SshConnection to be removed.
	 */
	public void removeConnection( String hiveApMac, String connectionId ) {
		if ( hiveApMac == null || hiveApMac.trim().equals( "" ) || connectionId == null || connectionId.trim().equals( "" ) ) {
			return;
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "Removing Connection " + connectionId + " for HiveAP " + hiveApMac );
		}

		Map<String, SshConnection> sshConnections = getConnectionMap();

		synchronized ( sshConnections ) {
			SshConnection sshConnection = sshConnections.get( hiveApMac );

			if ( sshConnection != null ) {
				if ( sshConnection.getConnectionId().equals( connectionId ) ) {
					sshConnections.remove( hiveApMac );

					if ( log.isDebugEnabled() ) {
						log.debug( sshConnection.getConnectionInfo() + " - Connection " +  connectionId + " was removed from ConnectionMap." );
					}
				}
			}

			Map<String, Collection<String>> sshChannels = getChannelMap();

			synchronized ( sshChannels ) {
				Collection<String> channels = sshChannels.remove( connectionId );

				if ( channels != null ) {
					if ( log.isDebugEnabled() ) {
						log.debug( "Connection " + connectionId + " as well as corresponding Channels were removed from Session ChannelMap." );
					}
				}
			}
		}
	}

	/**
     * Returns a Collection for the current connections.
     *
     * @return null if no connections exist, or an Collection for the current
     * connections.
     */
    public Collection<SshConnection> getConnections() {
//		Map connections = getConnectionMap();
//		return ( connections == null ) ? null : connections.values();

		return getConnectionMap().values();
	}

	/**
	 * Stores a new SshChannel into the channelMap (actually, the session).
	 *
	 * @param connectionId the identity of SshConnection.
	 * @param channelId the identity of SshChannel to be added into channelMap.
	 * @return false if this SshChannel is a duplicate.
	 */
	public boolean addChannel( String connectionId, String channelId ) {
		if ( connectionId == null || connectionId.trim().equals( "" ) || channelId == null || channelId.trim().equals( "" ) ) {
			return false;
		}

		if ( log.isDebugEnabled() ) {
			log.debug( "Adding Channel " + channelId + " mapped by Connection " + connectionId + " into Session ChannelMap." );
		}

		Map<String, Collection<String>> sshChannels = getChannelMap();

		synchronized ( sshChannels ) {
			Collection<String> channelIds = sshChannels.get( connectionId );

			if ( channelIds == null ) {
				channelIds = new ArrayList<String>( 5 );
				sshChannels.put( connectionId, channelIds );
			}

			boolean isAdded = channelIds.add( channelId );

			if ( isAdded ) {
				if ( log.isDebugEnabled() ) {
					log.debug( "Channel " + channelId + " mapped by Connection " + connectionId + " was added into Session ChannelMap." );
				}
			} else {
				log.warn( "Error Adding new SshChannel. A Channel " + channelId + " mapped by Connection " + connectionId + " already exists in the Session ChannelMap.");
			}

			return isAdded;
		}		
	}

	/**
	 * Stores a new SshChannel into the channelMap (actually, the session).
	 *
	 * @param sshConnection which the sshChannel to be stored is using.
	 * @param sshChannel which is going to be stored into the channelMap.
	 * @return false if this SshChannel is a duplicate.
	 */
	public boolean addChannel( SshConnection sshConnection, SshChannel sshChannel ) {
		if ( sshConnection == null || sshChannel == null ) {
			return false;
		}

		String connectionId = sshConnection.getConnectionId();
		String channelId = sshChannel.getChannelId();

		return addChannel( connectionId, channelId );
	}

	/**
	 * Removes a SshChannel from the channelMap (actually, the session).
	 *
	 * @param connectionId the identity of a specific SshConnection.
	 * @param channelId the identity of specific SshChannel.
	 */
	public void removeChannel( String connectionId, String channelId ) {
		if ( connectionId == null || connectionId.trim().equals( "" ) || channelId == null || channelId.trim().equals( "" ) ) {
			return;
		}

		Map<String, Collection<String>> sshChannels = getChannelMap();

		synchronized ( sshChannels ) {
			Collection<String> channelIds = sshChannels.get( connectionId );

			if ( channelIds != null ) {
				boolean isRemoved = channelIds.remove( channelId );

				if ( isRemoved ) {
					if ( log.isDebugEnabled() ) {
						log.debug( "The Channel " + channelId + " mapped by Connection " + connectionId + " was removed from Session ChannelMap." );
					}
				}

				if ( channelIds.isEmpty() ) {
					if ( log.isDebugEnabled() ) {
						log.debug( "None Channels mapped by Connection " + connectionId + ", removing the Connection from Session ChannelMap." );
					}

					sshChannels.remove( connectionId );

					if ( log.isDebugEnabled() ) {
						log.debug( "Connection " + connectionId + " was removed from Session ChannelMap." );
					}
				}
			}
		}
	}

	/**
	 * Removes the ConnectionMap from the session (actually, the ServletContext).
	 */
	public void removeConnectionMap() {
		ServletContext context = session.getServletContext();
		context.removeAttribute( session.getId() + SSH_CONNECTIONS );
	}

	/**
     * Removes the ChannelMap from the session.
     */
	public void removeChannelMap() {
		ServletContext context = session.getServletContext();
		String attrName = session.getId() + SSH_CHANNELS;
		context.removeAttribute( attrName );
	}

	/**
	 * Close all SSH channels that were opened based on current session.
	 */
	public void closeSshChannels() {
		Map<String, Collection<String>> sshChannels = getChannelMap();

		if ( sshChannels.isEmpty() ) {
			return;
		}

		Collection<SshConnection> sshConnections = getConnections();

		synchronized ( sshConnections ) {
			synchronized ( sshChannels ) {
				for ( String connectionId : sshChannels.keySet() ) {
					for ( Iterator<SshConnection> sshConnIter = sshConnections.iterator(); sshConnIter.hasNext(); ) {
						SshConnection sshConnection = sshConnIter.next();

						// Ignore if the connection in the ConnectionMap doesn't match that in the Session ChannelMap. 
						if ( !connectionId.equals( sshConnection.getConnectionId() ) ) {
							continue;
						}

						// Close all the channels based on a specific connection as key contained in the ChannelMap for the session.
						for ( String channleId : sshChannels.get( connectionId ) ) {
							sshConnection.closeChannel( channleId );
						}

						int remainChannelCount = sshConnection.getChannels().size();
						int activeChannelCount = sshConnection.getActiveChannelCount();

						// Close connection automatically if neither remaining nor active channels are inside.
						if ( remainChannelCount == 0 || activeChannelCount == 0 ) {
							if ( log.isDebugEnabled() ) {
								log.debug( sshConnection.getConnectionId() + " - Closing Connection " + sshConnection.getConnectionId() + " due to none open Channels inside." );
							}

							SshConnectionFactory.getInstance().closeSshConnection( sshConnection );
							sshConnIter.remove();
						}  else {
							if ( log.isDebugEnabled() ) {
								log.debug( remainChannelCount + " Channels are remaining over the Connection " + sshConnection.getConnectionInfo() + " and " + activeChannelCount + " of them are active." );
							}
						}
					}
				}

				// Clear the ChannelMap after all channels opened based on the session are closed.
				sshChannels.clear();

				// Remove the ChannelMap from the session.
				removeChannelMap();
			}
		}
	}

	//***************************************************************
    // Private Parameter Access Methods
    //***************************************************************

//	/**
//	 * Retrieve the ConnectionMap for this session.  The map is actually stored
//	 * in the ServletContext so it will be accessible by the SessionCleanup class
//	 * after the session is destroyed.
//	 *
//	 * @return A Map of SshConnections, keyed by the connectionInfo String.
//	 */
//	private Map<String, SshConnection> getConnectionMap() {
//		ServletContext context = session.getServletContext();
//		String attrName = session.getId() + SSH_CONNECTIONS;
//		Map<String, SshConnection> sshConnections = (Map<String, SshConnection>) context.getAttribute( attrName );
//
//		if ( sshConnections == null ) {
//			sshConnections = Collections.synchronizedMap( new HashMap<String, SshConnection>() );
//			context.setAttribute( attrName, sshConnections );
//		}
//
//		return sshConnections;
//	}

	/**
	 * Retrieves the global ConnectionMap from this session.  The map is actually stored
	 * in the ServletContext so it will be accessible by the CurrentUserCache class
	 * after the session is unbound.
	 *
	 * @return A Map of SshConnections, keyed by the HiveAP MAC String.
	 */
	private Map<String, SshConnection> getConnectionMap() {
		ServletContext context = session.getServletContext();
		Map<String, SshConnection> sshConnections = (Map<String, SshConnection>) context.getAttribute( SSH_CONNECTIONS );

		if ( sshConnections == null ) {
			sshConnections = Collections.synchronizedMap( new HashMap<String, SshConnection>() );
			context.setAttribute( SSH_CONNECTIONS, sshConnections );
		}

		return sshConnections;
	}

	/**
	 * Retrieves the ChannelMap from this session.
	 *
	 * @return A Map of SshChannels, keyed by the connectionId String.
	 */
	private Map<String, Collection<String>> getChannelMap() {
		ServletContext context = session.getServletContext();
		String attrName = session.getId() + SSH_CHANNELS;
		Map<String, Collection<String>> sshChannels = (Map<String, Collection<String>>) context.getAttribute( attrName );

		if ( sshChannels == null ) {
			sshChannels = Collections.synchronizedMap( new HashMap<String, Collection<String>>() );
			context.setAttribute( attrName, sshChannels );
		}

		return sshChannels;
	}

}