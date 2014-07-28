/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/client/core/Proxy.java,v $
 * $Revision: 1.8 $
 * $Author: ychen $
 * $Date: 2010/08/23 09:39:28 $
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

package com.ericdaugherty.soht.client.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
//import java.net.SocketException;
//import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.CancelledKeyException;
//import java.nio.channels.ClosedByInterruptException;
//import java.nio.channels.ClosedChannelException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.MessageFormat;
import java.util.Iterator;

//import org.apache.log4j.Logger;

import com.ericdaugherty.soht.client.configuration.ConfigurationManager;
import com.ericdaugherty.soht.client.configuration.Host;

/**
 * Implements a proxy connection for a single remote host:port.
 * <p>
 * Each remote host:port must have a unique local port that client applications
 * connect to.  This class implements a single connection, or port proxy.
 *
 * @author Eric Daugherty
 */
public class Proxy extends Thread implements SelectionKeyHandler {

    //***************************************************************
    // Variables
    //***************************************************************

//	private static final Logger log = Logger.getLogger( Proxy.class );

	/** The max receive buffer size */
	private static final int MAX_SO_RCVBUF = 65535;

	/** The max send buffer size */
	private static final int MAX_SO_SNDBUF = 65535;

    /** The local -> remote mapping information */
    private final Host host;

    /** The general configuration information */
    private final ConfigurationManager configurationManager;

    /** Keeps track of whether the proxy is running or not */
	private boolean running;

	/** Selector to select ready SelectionKeys */
	private	Selector selector;

	/** ServerSocketChannel to accept new connections */
	private	ServerSocketChannel serverSocketChannel;

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Creates a new Proxy instance.
     * <p>
     * After creating an instance, the proxy should be started using
     * the startProxy Method.
     *
     * @param configurationManager the general configuration information.
     * @param host the specific host mapping to implement.
     */
    public Proxy( ConfigurationManager configurationManager, Host host ) {
        super( "Proxy-" + host.getLocalPort() );

        this.configurationManager = configurationManager;
        this.host = host;

//		if ( log.isDebugEnabled() ) {
//			log.debug( "Proxy Created." );
//		}

		System.out.println( "Proxy Created." );
    }

    //***************************************************************
    // Control Methods
    //***************************************************************

	public synchronized void startProxy() throws IOException {
		if ( isAlive() ) {
		//	log.warn( "Proxy was has already been started on port " + host.getLocalPort() + ", ignore this duplicate start." );
			System.out.println( "Proxy was has already been started on port " + host.getLocalPort() + ", ignore this duplicate start." );
			return;
		}

//		if ( log.isDebugEnabled() ) {
//			log.debug( "Starting Proxy on port: " + host.getLocalPort() );
//		}

		System.out.println( "Starting Proxy on port: " + host.getLocalPort() );

		// Open a Selector.
		selector = Selector.open();

		// Open a ServerSocketChannel.
		serverSocketChannel = ServerSocketChannel.open();

		// The server socket associated with this channel.
		ServerSocket serverSocket = serverSocketChannel.socket();

		// Set receive buffer size.
		serverSocket.setReceiveBufferSize( MAX_SO_RCVBUF );

		InetSocketAddress socketAddress = new InetSocketAddress( host.getLocalPort() );

		// Bind onto a specified local port.
		serverSocket.bind( socketAddress );

		// Configure non-blocking mode for the server socket channel.
		serverSocketChannel.configureBlocking( false );

		// Register the server socket channel with the selector.
		serverSocketChannel.register( selector, SelectionKey.OP_ACCEPT, this );

	//	log.info( MessageFormat.format( "Proxy started to remote host: {0}:{1}, using SOHT Server at: {2}", host.getRemoteHost(), host.getRemotePort(), configurationManager.getServerURL() ) );
		System.out.println( MessageFormat.format( "Proxy started to remote host: {0}:{1}", host.getRemoteHost(), host.getRemotePort() ) );

		running = true;

		start();
	}

	public synchronized void stopProxy() {		
		if ( running && isAlive() ) {
			System.out.println( "Stopping Proxy on port: " + host.getLocalPort() );
			running = false;
		}
	}

    //***************************************************************
    // Public Methods
    //***************************************************************

    /**
     * Process incoming connections.
     */
	/*-
	@Override
    public void run() {
		if ( log.isDebugEnabled() ) {
			log.debug( "Proxy starting on port: " + host.getLocalPort() );
		}

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket( host.getLocalPort() );
            serverSocket.setSoTimeout( 1000 );
            running = true;
        } catch ( IOException ioException ) {
            log.fatal( "Error in creating Server Socket", ioException );
            log.fatal( "Thread Ending" );
            return;
        }

        log.info( MessageFormat.format( "Proxy started to remote host: {0}:{1}, using SOHT Server at: {2}", host.getRemoteHost(), String.valueOf( host.getRemotePort() ), configurationManager.getServerURL() ) );

        while ( running ) {
            try {
                // Accept incoming connections.
                Socket socket = serverSocket.accept();

				if ( log.isDebugEnabled() ) {
                	log.debug( "New connection received." );
				}

                // Initiate the connection to the remote host.
                long connectionId = openHost();

				if ( log.isDebugEnabled() ) {
                	log.debug( "Connection opened to Server." );
				}

                // Start the proxy threads.
                if ( configurationManager.isUseStatelessConnection() ) {
					if ( log.isDebugEnabled() ) {
                    	log.debug( "Using ReadWrite Thread." );
					}

                    new ProxyReadWrite( getName() + "-ReadWrite", configurationManager, connectionId, socket ).start();
                } else { // Use the normal stateful read connection.
					if ( log.isDebugEnabled() ) {
                    	log.debug( "Using separate Read and Write threads." );
					}

                    new ProxyReader( getName() + "-Reader", configurationManager, connectionId, socket ).start();
                    new ProxyWriter( getName() + "-Writer", configurationManager, connectionId, socket ).start();
                }
            } catch ( IOException ioException ) {
            	// Ignore IOExceptions.  They occur every second when the block timeout expires.
			} catch ( Exception e ) {
				log.error( "Error in creating new connection.", e );
			//	TODO: Need an error sink!
            }
        }
    }*/

    /**
     * Process incoming connections.
     */
	@Override
    public void run() {
		try {
			while ( running && selector.isOpen() && serverSocketChannel.isOpen() ) {
				if ( selector.select( 1000 ) > 0 ) {
					for ( Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator(); keyIter.hasNext(); ) {
						SelectionKey key = keyIter.next();
						keyIter.remove();

						SelectionKeyHandler	handler = (SelectionKeyHandler) key.attachment();
						handler.handle( key );
					}
				}
			}
		} catch ( ClosedSelectorException cse ) {
		//	log.error( "Selector was closed.", cse );
			cse.printStackTrace();
		} catch ( IOException ioe ) {
		//	log.error( "I/O Error in Selector.select().", ioe );
			ioe.printStackTrace();
		} catch ( Exception e ) {
		//	log.error( "Error in handing SelectionKey.", e );
			e.printStackTrace();
		} finally {
			// Close Selector.
			if ( selector != null && selector.isOpen() ) {
				try {
					selector.close();
				} catch ( IOException ioe ) {
				//	log.error( "IO Error in closing Selector.", ioe );
					ioe.printStackTrace();
				}
			}

			// Close ServerSocketChannel.
			if ( serverSocketChannel.isOpen() ) {
				try {
					serverSocketChannel.close();
				} catch ( IOException ioe ) {
				//	log.error( "IO Error in closing ServerSocketChannel.", ioe );
					ioe.printStackTrace();
				}
			}
		}
    }

    /**
     * Process acceptable SelectionKeys.
     */
	@Override
	public void handle( SelectionKey key ) {
		try {
			if ( !key.isAcceptable() ) {
			//	log.warn( "The given SelectionKey is unacceptable, ignore handling it." );
				System.out.println( "The given SelectionKey is unacceptable, ignore handling it." );
				return;
			}
		} catch ( CancelledKeyException cke ) {
		//	log.error( "The given SelectionKey has been cancelled.", cke );
			cke.printStackTrace();
			return;
		}

		SocketChannel clientChannel = null;

		try {
			ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
			clientChannel = serverChannel.accept();

		//	if ( log.isDebugEnabled() ) {
		//		log.debug( "Received a new connection - " + clientChannel.socket() );
		//	}

			System.out.println( "Received a new connection - " + clientChannel.socket() );

			// Configure non-blocking mode for the client socket channel.
			clientChannel.configureBlocking( false );

			Socket clientSocket = clientChannel.socket();

			// Set send buffer size.
			clientSocket.setSendBufferSize( MAX_SO_SNDBUF );

			// Enable SO_KEEPALIVE
			clientSocket.setKeepAlive( true );

			// Disable the Nagle's algorithm.
			clientSocket.setTcpNoDelay( true );

			Selector selector = key.selector();

			// Register the client socket channel with the selector.
			SelectionKey sk = clientChannel.register( selector, SelectionKey.OP_READ );

			// Notify server to open a proxy connection to the specified remote host.
			long connectionId = openHost();

		//	if ( log.isDebugEnabled() ) {
		//		log.debug( "A new proxied connection to the specified remote host was successfully opened by Server." );
		//	}

			SelectionKeyHandler keyHandler;

			// Start the proxy threads.
			if ( configurationManager.isUseStatelessConnection() ) {
			//	if ( log.isDebugEnabled() ) {
			//		log.debug( "Using ReadWrite Thread." );
			//	}

				System.out.println( "Using ReadWrite Thread." );

				ProxyReadWrite proxy = new ProxyReadWrite( Thread.currentThread().getName() + "-ReadWrite", configurationManager, connectionId, clientChannel );
				proxy.start();
				keyHandler = proxy;
			} else { // Use the normal stateful read connection.
			//	if ( log.isDebugEnabled() ) {
			//		log.debug( "Using separate Read and Write threads." );
			//	}

				System.out.println( "Using separate Read and Write threads." );

				new ProxyReader( Thread.currentThread().getName() + "-Reader", configurationManager, connectionId, clientChannel ).start();
				keyHandler = new ProxyWriter( Thread.currentThread().getName() + "-Writer", configurationManager, connectionId );
			}

			// Attach the proxy handler to the key.
			sk.attach( keyHandler );
	/*-	} catch ( ClosedByInterruptException cbie ) {
		//	log.error( "Interrupt occurred while the new connection accept operation is in progress.", cbie );
			cbie.printStackTrace();
		} catch ( AsynchronousCloseException ace ) {
		//	log.error( "The socket channel was closed while the new connection accept operation is in progress.", ace );
			ace.printStackTrace();
		} catch ( ClosedChannelException cce ) {
		//	log.error( "The socket channel was closed while the new connection accept operation is in progress.", cce );
			cce.printStackTrace();
		} catch ( CancelledKeyException cke ) {
		//	log.error( "The SelectionKey for representing the registration of the newly accepted SocketChannel with Selector has been cancelled.", cke );
			cke.printStackTrace();
		} catch ( SocketException se ) {
		//	if ( "Socket is closed".equals( se.getMessage() ) ) {
		//		log.error( "The connection to client was closed.", se );
		//	}

			se.printStackTrace();
		} catch ( IOException ioe ) {
		//	log.error( "I/O Error.", ioe );
			ioe.printStackTrace(); */
		} catch ( Exception e ) {
		//	log.error( e.getMessage(), e );
			e.printStackTrace();

			if ( clientChannel != null && clientChannel.isOpen() ) {
				try {
					clientChannel.close();
				} catch ( IOException ioe ) {
				//	log.error( "Error in closing connection to client.", ioe );
					ioe.printStackTrace();
				}
			}
		}
	}

    /**
     * Connect to the Proxy Server and request a connection to the remote
     * host:port.
     *
     * @return returns a unique connection ID for the new connection.
     * @throws Exception if an error while setting up a proxy connection to the specified remote host.
     */
    public long openHost() throws Exception {
		System.out.println( MessageFormat.format( "Opening a proxy connection from SOHT server to remote host: {0}:{1}.", host.getRemoteHost(), host.getRemotePort() ) );
        HttpURLConnection urlConnection = configurationManager.getURLConnection();

        // Write parameters.
        BufferedWriter out = new BufferedWriter( new OutputStreamWriter( urlConnection.getOutputStream() ) );
        out.write( "action=open" );
        out.write( "&" );
        out.write( "host=" + host.getRemoteHost() );
        out.write( "&" );
        out.write( "port=" + host.getRemotePort() );

        if ( configurationManager.isServerLoginRequired() ) {
            out.write( "&" );
            out.write( "username=" + configurationManager.getServerUsername() );
            out.write( "&" );
            out.write( "password=" + configurationManager.getServerPassword() );
        }

        out.flush();
        out.close();

        // Post the request to the server.
        urlConnection.connect();

        BufferedReader reader = null;

        // Make sure we can do cleanup even if there is an error...
        try {
            // Get the response stream.
            reader = new BufferedReader( new InputStreamReader( urlConnection.getInputStream() ) );

            // If the post was successful, return the new id, otherwise
            // throw an exception.
            String result = reader.readLine().trim();

            if ( !result.startsWith( "SUCCESS" ) ) {
				throw new Exception( "Unable to connect to remote host: " + result );
            }

			long connectionId = Long.parseLong( reader.readLine() );
		//	System.out.println( "Connection Successful" );
		//	log.info( MessageFormat.format( "A new proxy connection identified with " + connectionId + " to remote host: {0}:{1} was successfully opened by SOHT server.", host.getRemoteHost(), host.getRemotePort() ) );
			System.out.println( MessageFormat.format( "A new proxy connection identified with " + connectionId + " to remote host: {0}:{1} was successfully opened by SOHT server.", host.getRemoteHost(), host.getRemotePort() ) );
			return connectionId;
        } finally {
            // Do proper housekeeping.  If the urlConnection is
            // not closed, the read operation will fail.
            if ( reader != null ) {
                reader.close();
            }

			urlConnection.disconnect();
        }
    }

}
//EOF