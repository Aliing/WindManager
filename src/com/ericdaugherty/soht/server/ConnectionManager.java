/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/server/ConnectionManager.java,v $
 * $Revision: 1.3 $
 * $Author: ychen $
 * $Date: 2010/06/09 11:09:11 $
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
 * http://www.ericdaugherty.com/dev/soht
 *
 * or contact the author at:
 * soht@ericdaugherty.com
 *****************************************************************************/

package com.ericdaugherty.soht.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SocketChannel;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * Handles and caches all open connections.
 *
 * @author Eric Daugherty
 */
public class ConnectionManager {

    /** Handles the logging of messages */
    private static final Logger log = Logger.getLogger( ConnectionManager.class );

    //***************************************************************
    // Variables
    //***************************************************************

    final Map<Long, ConnectionInfo> _connections = Collections.synchronizedMap( new HashMap<Long, ConnectionInfo>() );

    //***************************************************************
    // Public Methods
    //***************************************************************

    /**
     * Adds a new connection to the manager.
	 *
	 * @param connectionInfo the connection information.
     */
    public void addConnection( ConnectionInfo connectionInfo ) {
		synchronized ( _connections ) {
			 _connections.put( connectionInfo.getConnectionId(), connectionInfo );
		}
    }

    /**
     * Returns the ConnectionInfo object for the specified connection.
     * <p>
	 *
	 * @param connectionId which is used to specify a specific ConnectionInfo.
	 * @return null if the connection is not cached.
     */
    public ConnectionInfo getConnection( long connectionId ) {
        return _connections.get( connectionId );
    }

    /**
     * Closes the input and output streams of the specified connection
     * and removes it from the cache.
	 *
	 * Note: this method will also remove other connections that are
	 * detected to be closed except for the connection with the id
	 * specified. 
	 *
	 * @param connectionId which is used to specify a specific ConnectionInfo.
     */
    public void removeConnection( long connectionId ) {
	//	ConnectionInfo info;

		synchronized ( _connections ) {
		//	info = _connections.remove( connectionId );
			
			Collection<ConnectionInfo> removedConnections = new ArrayList<ConnectionInfo>( _connections.size() );

			for ( Iterator<ConnectionInfo> connIter = _connections.values().iterator(); connIter.hasNext(); ) {
				ConnectionInfo info = connIter.next();

				// Remove the connection with the id specified.
				if ( info.getConnectionId() == connectionId ) {
					connIter.remove();
					removedConnections.add( info );
				} else {
					SocketChannel socketChannel = info.getSocketChannel();
					Selector selector = info.getSelector();

					// Remove other connections closed.
					if ( socketChannel == null || !socketChannel.isOpen() || selector == null || !selector.isOpen() ) {
						connIter.remove();
						removedConnections.add( info );
					}
				}
			}

			closeConnections( removedConnections );
		}
    }

    /**
     * Closes the input and output streams for all of cached
	 * connections and then removes them from the cache.
     */
    public void removeAllConnections() {
		synchronized ( _connections ) {			
			closeConnections( _connections.values() );

			_connections.clear();
		}
    }

    /**
     * Returns the number of active connections.
     *
     * @return number of active connections.
     */
    public int getConnectionCount() {
        return _connections.size();
    }

    /**
     * Returns a connection of all the active connections.
     *
     * @return a connection of ConnectionInfo instances.
     */
    public Collection<ConnectionInfo> getConnections() {
        return _connections.values();
    }

	/**
	 * Close a list of connections.
	 *
	 * @param infos connections to be closed.
	 */
	private void closeConnections( Collection<ConnectionInfo> infos ) {
		for ( ConnectionInfo info : infos ) {
			closeConnection( info );
		}
	}

	/**
	 * Close a single connection.
	 *
	 * @param info connection to be closed.
	 */
	private void closeConnection( ConnectionInfo info ) {
		log.info( info + " - Closing the proxied connection." );
		InputStream in = info.getInputStream();

		if ( in != null ) {
			try {
				in.close();
			} catch ( IOException e ) {
				log.error( "InputStream Close Error." );
			}
		}

		OutputStream out = info.getOutputStream();

		if ( out != null ) {
			try {
				out.close();
			} catch ( IOException e ) {
				log.error( "OutputStream Close Error." );
			}
		}

		Selector selector = info.getSelector();

		if ( selector != null && selector.isOpen() ) {
			try {
				selector.close();
			} catch ( IOException e ) {
				log.error( "Selector Close Error." );
			}
		}

		SocketChannel channel = info.getSocketChannel();

		if ( channel != null && channel.isOpen() ) {
			try {
				channel.close();
			} catch ( IOException e ) {
				log.error( "SocketChannel Close Error." );
			}
		}

		log.info( info + " - The proxied connection was completely closed." );
	}

}
//EOF