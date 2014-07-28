/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/server/SocketProxyServlet.java,v $
 * $Revision: 1.5 $
 * $Author: ychen $
 * $Date: 2012/02/22 12:56:16 $
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

//import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
//import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
//import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
//import java.nio.channels.AsynchronousCloseException;
//import java.nio.channels.CancelledKeyException;
//import java.nio.channels.ClosedByInterruptException;
//import java.nio.channels.ClosedChannelException;
//import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ah.be.common.NmsUtil;

/**
 * Handles communication between client and proxy.
 * Implements the IO functionality for the Socket over HTTP
 * proxy service.  This servlet accepts incoming HTTP connections,
 * and opens the requested socket session and proxies the IO over
 * the HTTP connection.
 *
 * @author Eric Daugherty
 */
public class SocketProxyServlet extends HttpServlet {

    //***************************************************************
    // Variables
    //***************************************************************

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger( SocketProxyServlet.class );

    private static final ConnectionManager connectionManager = new ConnectionManager();

//	private static int readerCount = 0;
//	private static int writerCount = 0;

	/** Number of read actions at present */
    private static final AtomicInteger readerCount = new AtomicInteger( 0 );

	/** Number of write actions at present */
    private static final AtomicInteger writerCount = new AtomicInteger( 0 );

	/** The max receive buffer size */
	private static final int MAX_SO_RCVBUF = 65535;

	/** The max send buffer size */
	private static final int MAX_SO_SNDBUF = 65535;

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static int getReaderCount() {
	//	return readerCount;
		return readerCount.get();
    }

    public static int getWriterCount() {
	//	return writerCount;
		return writerCount.get();
    }

    //***************************************************************
    // HttpServlet Methods
    //***************************************************************

    /**
     * Handles GET requests.  We want to ignore these requests because all
     * clients use the POST method.  This will return a blank page so that
     * anyone who snoops around will not get any information about the
     * services offered.
     *
     * @param request an {@link HttpServletRequest} object that
     *                  contains the request the client has made
     *                  of the servlet
     * @param response an {@link HttpServletResponse} object that
     *                  contains the response the servlet sends
     *                  to the client
     * @throws IOException if an input or output error is
     *                              detected when the servlet handles
     *                              the GET request
     * @throws ServletException  if the request for the POST
     *                                  could not be handled
     */
	@Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        out.println( "<HTML>" );
        out.println( "<BODY>" );
        out.println( "</BODY>" );
        out.println( "</HTML>" );
    }

    /**
     * Handles POST requests.  Clients post a request here to initiate a
     * session or to read, write or close an existing session.
     *
     * @param request an {@link HttpServletRequest} object that
     *                  contains the request the client has made
     *                  of the servlet
     * @param response an {@link HttpServletResponse} object that
     *                  contains the response the servlet sends
     *                  to the client
     * @throws IOException if an input or output error is
     *                              detected when the servlet handles
     *                              the GET request
     * @throws ServletException  if the request for the POST
     *                                  could not be handled
     */
	@Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		// Extract the remote address.
		String remoteAddr = getRemoteAddr( request );

        // Extract the action parameter.
        String action = request.getParameter( "action" );

        if ( action != null && action.equals( "open" ) ) { // If it is a new session, that means we need to open a new connection.
            log.info( remoteAddr + " - Open connection request received." );

            open( request, response );
        } else { // Otherwise, we need to perform some action on the existing session.
			String id = request.getParameter( "id" );

			if ( id == null || id.trim().isEmpty() ) {
				throw new ServletException( "Invalid connection ID - " + id );
			}

			long connectionId;

			try {
				connectionId = Long.parseLong( id.trim() );
			} catch ( NumberFormatException numberFormatException ) {
				throw new ServletException( "Invalid connection ID - " + id, numberFormatException );
			}

            if ( action != null && action.equals( "close" ) ) { // CLOSE
                log.info( remoteAddr + " - Close connection request received." );

                connectionManager.removeConnection( connectionId );
            } else if ( action != null && action.equals( "read" ) ) { // READ
				if ( log.isDebugEnabled() ) {
					log.debug( remoteAddr + " - Read request received." );
				}

			//	read( connectionId, response, true );

				// Use non-blocking mode instead of the blocking mode to improve the performance on the data handling.
				read( connectionId, response, true );
            } else if ( action != null && action.equals( "write" ) ) { // WRITE
				if ( log.isDebugEnabled() ) {
					log.debug( remoteAddr + " - Write request received." );
				}

                write( connectionId, request );
            } else if ( action != null && action.equals( "readwrite" ) ) {
				if ( log.isDebugEnabled() ) {
					log.debug( remoteAddr + " - Read/Write request received." );	
				}

                readWrite( connectionId, request, response );
            }
        }
    }

    //***************************************************************
    // Private Methods
    //***************************************************************

    /**
     * Handles all connection setup for open connection requests.
	 *
     * @param request an {@link HttpServletRequest} object that
     *                  contains the request the client has made
     *                  of the servlet
     * @param response an {@link HttpServletResponse} object that
     *                  contains the response the servlet sends
     *                  to the client
     * @throws IOException
     *			if an input or output exception occurred
     */
	/*-
    private void open( HttpServletRequest request, HttpServletResponse response ) throws IOException {
	//	PrintWriter out = new PrintWriter( response.getWriter() );
		PrintWriter out = response.getWriter();

        try {
            String host = request.getParameter( "host" );
            String port = request.getParameter( "port" );

            if ( host == null || port == null ) {
                throw new Exception( "Host or port parameter not specified." );
            }

            // If a password is required, check to make sure one
            // is specified and correct.
            User user = null;

            if ( SocketProxyAdminServlet.isPasswordRequired() ) {
                String username = request.getParameter( "username" );
                String password = request.getParameter( "password" );

                if ( username == null || password == null ) {
                    throw new Exception( "Username and Password Required!" );
                }

                user = SocketProxyAdminServlet.getUser( username );

                if ( user == null ) {
                    throw new Exception( "Invalid Username!" );
                }

                if ( !user.isPasswordValid( password ) ) {
                    throw new Exception( "Invalid Password!" );
                }
            }

            // Open the connection.
            Socket socket = new Socket( InetAddress.getByName( host ), Integer.parseInt( port ) );

            // Capture the information about the connection.
            ConnectionInfo connectionInfo = new ConnectionInfo(
                    user,
                    request.getRemoteHost(),
                    host,
                    port,
                    socket,
                    new Date(),
                    socket.getInputStream(),
                    socket.getOutputStream() );

            // Add the connection to the manager.
            connectionManager.addConnection( connectionInfo );

            log.info( "Connection Opened: " + connectionInfo.toString() );

            // Let the client know the connection was opened successfully.
            out.println( "SUCCESS" );
            out.println( connectionInfo.getConnectionId() );
        } catch ( Exception e ) {
            log.error( "Error in processing request for new connection. " + e.getMessage() );

            // Let the client know the connection failed.
            out.println( "FAIL - " + e.getMessage() );
        }
    }*/

    private void open( HttpServletRequest request, HttpServletResponse response ) throws IOException {
		PrintWriter out = response.getWriter();

		try {
			// Host to connect.
			String host = getHost( request );

			// Port on which the host is listening.
			int port = getPort( request );

			// User Name.
			String username = getUserName( request );

			// Password.
			String password = getPassword( request );

			boolean authenticated = authenticate( username, password );

			if ( !authenticated ) {
				throw new Exception( "Authentication failed." );
			}

			// User.
			User user = new User( username, password );

			// Remote client.
			String remoteHost = getRemoteAddr( request );

			// Open a new proxy connection based on the information provided.
			ConnectionInfo connectionInfo = setupProxyConnection( user, remoteHost, host, port );

			// Add the connection to the manager.
			connectionManager.addConnection( connectionInfo );

			log.info( "Connection Opened: " + connectionInfo );

			// Let the client know the proxy connection was opened successfully.
			out.println( "SUCCESS" );
			out.println( connectionInfo.getConnectionId() );
		} catch ( Exception e ) {
            log.error( "Error in processing request for opening a new proxy connection.", e );

            // Let the client know the proxy connection opening failed.
            out.println( "FAIL - " + e.getMessage() );
		}
    }

    /**
     * Handles a READ request from the client.  Data is read from the
     * proxied connection and written to the client.
     *
     * @param id the proxied connection id.
     * @param response By using it, data read from the proxied connection will be sent to the client.
     * @param blocking true if the thread should block while waiting for data.
     */
	/*-
    private void read( long id, HttpServletResponse response, boolean blocking ) {
		byte[] bytes = new byte[1024 * SocketProxyAdminServlet.getBlockSize()];
		int count;

		if ( log.isDebugEnabled() ) {
			log.debug( "Read Buffer Size: " + bytes.length );
		}

		try {
			// Add to the count of "Reader Threads"
			addReader();

			// Open the output stream to write to the client.
			OutputStream out = response.getOutputStream();
			ConnectionInfo info = connectionManager.getConnection( id );

			if ( info == null ) {
				log.error( "Client requested a proxied connection that doesn't exist or might have been closed (connectionId:" + id + ")." );
				out.write( 0 );
				out.close();
				return;
			}

			// Open the input stream to read from the proxied connection.
			InputStream in = info.getInputStream();

			// If we are not blocking, set the correct SoTimeout.
//			if ( !blocking ) {
//				info.getSocket().setSoTimeout( 100 );
//			} else {
//				info.getSocket().setSoTimeout( 0 );
//			}

			int timeout = blocking ? 0 : 100;
			info.getSocket().setSoTimeout( timeout );

			boolean isFirst = true;

			while ( true ) {
				count = 0;

				// Only block for a read if we are in blocking mode.
				if ( blocking ) {
					// Read data from the proxied session.
					count = in.read( bytes );
				} else {
					try {
						count = in.read( bytes );
					} catch ( SocketTimeoutException timeoutException ) {
						// This is normal, this just means that no data was
						// ready to be read.
					}
				}

				// A count of -1 indicates that the inputstream has been closed.
				if ( count == -1 ) {
					out.write( 0 );
					out.close();
					connectionManager.removeConnection( info.getConnectionId() );
					log.info( "Removing connection because the remote server closed the connection." );
					break;
				}

				// Log the actual bytes read/written.
				if ( log.isDebugEnabled() ) {
					log.debug( "Client read " + count + " bytes." );
					StringBuilder debugOut = new StringBuilder( "Data: " );

					for ( int index = 0; index < count; index++ ) {
						debugOut.append( (int) bytes[index] );
						debugOut.append( "," );
					}

					log.debug( debugOut );
				}

				// Write the data to the HTTP client.
				if ( isFirst ) {
					out.write( 1 );
					isFirst = false;
				}

				out.write( bytes, 0, count );
				out.flush();

				// If we are not in blocking mode, break out of the loop.
				if ( !blocking ) {
					out.close();
					break;
				}
			}
		} catch ( IOException ioe ) {
			// This just means the connection was closed.  This is fine.
			log.error( "The connection may have been closed.", ioe );
		} catch ( Exception e ) {
			log.error( "Non IO Error occurred while reading from the proxied connection. " + e.getMessage(), e );
		} finally {
			// This "ReaderThread" is ending, so remove it from the count.
			removeReader();

			if ( blocking ) {
				connectionManager.removeConnection( id );
			}
		}
    }*/

    private void read( long id, HttpServletResponse response, boolean blocking ) {
		boolean closingConnection = false;
		OutputStream out = null;
		SocketChannel proxySocketChannel = null;
		ConnectionInfo info = null;

        try {
            // Add to the counter of "Reader Threads"
            addReader();

            // Open the output stream to write to the client.
            out = response.getOutputStream();

			// Get the proxied connection.
            info = connectionManager.getConnection( id );

            if ( info == null ) {
                log.error( "Client requested a non-existent or closed proxied connection with id - " + id );
                out.write( 0 );
			//	out.close();
                return;
            }

			byte[] bytes = new byte[8192];
			ByteBuffer buf = ByteBuffer.allocate( bytes.length );
            boolean isFirst = true;

            for ( Selector selector = info.getSelector(); selector.isOpen() && info.getSocketChannel().isOpen(); ) {
				if ( selector.select( 100 ) > 0 ) {
					for ( Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator(); keyIter.hasNext(); ) {
						SelectionKey key = keyIter.next();
						keyIter.remove();

						if ( !key.isReadable() ) {
							log.warn( "The SelectionKey was unreadable, ignore handling it." );
							continue;
						}

						// Get the proxy connection channel.
						proxySocketChannel = (SocketChannel) key.channel();

						buf.clear();

						// Read data from the proxy connection channel.
						int count = proxySocketChannel.read( buf );

						if ( count == -1 ) {
							// A count of -1 indicates that the input has been shut down.
							if ( isFirst ) {
								// Write byte 0 to the HTTP client just in the first time.
								out.write( 0 );
								closingConnection = true;
							}

							log.info( "The remote host closed the connection - " + info );
							return;
						}

						buf.flip();
						buf.get( bytes, 0, count );

						// Log the actual bytes read/to write.
						if ( log.isDebugEnabled() ) {
							log.debug( "Client read " + count + " bytes." );
							StringBuilder debugOut = new StringBuilder( "Data: " );

							for ( int index = 0; index < count; index++ ) {
								debugOut.append( (int) bytes[index] );
								debugOut.append( "," );
							}

							log.debug( debugOut.toString() );
						}

						// Write the data to the HTTP client.
						if ( isFirst ) {
							out.write( 1 );
							isFirst = false;
						}

						out.write( bytes, 0, count );
						out.flush();
					}
				}

				// If we are not in blocking mode, break out of the loop.
				if ( !blocking ) {
					break;
				}
            }
//		} catch ( ClosedSelectorException cse ) {
//			closingConnection = true;
//			log.error( "Selector was closed.", cse );
//		} catch ( ClosedByInterruptException cbie ) {
//			closingConnection = true;
//			log.error( "Interrupt occurred while the read operation is in progress.", cbie );
//		} catch ( AsynchronousCloseException ace ) {
//			closingConnection = true;
//			log.error( "The proxy socket channel has been closed by another thread while the read operation is in progress.", ace );
//		} catch ( ClosedChannelException cce ) {
//			closingConnection = true;
//			log.error( "The proxy socket channel has been closed while the read operation is in progress.", cce );
//		} catch ( CancelledKeyException cke ) {
//			closingConnection = true;
//			log.error( "The SelectionKey has been cancelled.", cke );
//		} catch ( IOException ioe ) {
//			// This just means the connection was closed.  This is fine.
//			closingConnection = true;
//			log.error( "IO Error in read method.", ioe );
//		} catch ( Exception e ) {
//			closingConnection = true;
//			log.error( "Non IO Error while reading data from the proxied connection. ", e );
		} catch ( Exception e ) {
			// If we are not in blocking mode, we should close proxy connection actively when error happening.
			if ( !blocking ) {
				closingConnection = true;
			}

			log.error( "Error occurred while reading data from the specified proxy connection - " + info, e );
		} finally {
			if ( out != null ) {
				try {
					out.close();
				} catch ( IOException ioe ) {
					log.error( "Error in closing output stream for HttpServletResponse.", ioe );
				}
			}

			if ( proxySocketChannel != null && proxySocketChannel.isOpen() ) {
				try {
					proxySocketChannel.close();
				} catch ( IOException ioe ) {
					log.error( "Error in closing proxy socket channel.", ioe );
				}
			}

            // This "ReaderThread" is ending, so remove it from the counter.
            removeReader();

			if ( blocking || closingConnection ) {
				connectionManager.removeConnection( id );
			}
        }
    }

    /**
     * Processes a WRITE request from the client and writes the data sent from
     * the client to the proxied connection.
     *
     * @param id the proxied connection id.
     * @param request used to read the WRITE data from the client.
     */
    private void write( long id, HttpServletRequest request ) {
		ConnectionInfo info = null;

		try {
			addWriter();

			// Get the proxied connection.
			info = connectionManager.getConnection( id );

			if ( info != null ) {
				// Read the data and decode it into bytes.
				String data = request.getParameter( "data" );
				int dataLength = Integer.parseInt( request.getParameter( "datalength" ) );
				byte[] decodedBytes = decode( data, dataLength );

				if ( log.isDebugEnabled() ) {
					log.debug( "Client wrote " + dataLength + " bytes." );
					StringBuilder debugOut = new StringBuilder( "Data: " );

					for ( byte decodedByte : decodedBytes ) {
						debugOut.append( (int) decodedByte) ;
						debugOut.append( "," );
					}

					log.debug( debugOut.toString() );
				}

				SocketChannel proxySocketChannel = info.getSocketChannel();

				if ( proxySocketChannel != null ) {
					ByteBuffer buf = ByteBuffer.wrap( decodedBytes );
					int totalCount = buf.limit();

					// Write until reach the limit of the buffer.
					while ( totalCount > 0 ) {
						int writeCount = proxySocketChannel.write( buf );
						
						if ( writeCount == 0 ) {
							try {
								Thread.sleep( 500 );
							} catch ( InterruptedException ie ) {
								log.error( "Interrupt occurred while writing to the specified proxied connection - " + info, ie );
							}
						} else {
							totalCount -= writeCount;
						}
					}
				} else {
					OutputStream socketOut = info.getOutputStream();
					socketOut.write( decodedBytes );
				}
			} else {
				log.warn( "Write method attempted to write to a non-existent or closed proxied connection with id - " + id );
			}
//		} catch ( ClosedByInterruptException cbie ) {
//			log.error( "Interrupt occurred while the write operation is in progress.", cbie );
//		} catch ( AsynchronousCloseException ace ) {
//			log.error( "The client socket channel was closed by another thread while the write operation is in progress.", ace );
//		} catch ( ClosedChannelException cce ) {
//			log.error( "The client socket channel was closed while the write operation is in progress.", cce );
//		} catch ( IOException ioe ) {
//			// This just means the connection was closed.  This is fine.
//			log.error( "IO Error in write method.", ioe );
//		} catch ( Exception e ) {
//			log.error( "Non IO Error while writing to the proxied connection.", e );
		} catch ( Exception e ) {
			log.error( "Error occurred while writing to the specified proxy connection - " + info, e );
			connectionManager.removeConnection( id );
		} finally {
			removeWriter();
		}
    }

    /**
     * Processes a READ/WRITE request from the client.  Writes the data sent from
     * the client to the proxied connection and reads data from the proxied connection
     * and writes it to the client.
     *
     * @param id the proxied connection id.
     * @param request used to read the WRITE data from the client.
     * @param response used to write the READ data to the client.
     */
    private void readWrite( long id, HttpServletRequest request, HttpServletResponse response ) {
        // Do the WRITE part.
        write( id, request );
        read( id, response, false );
    }

    /**
     * Converts the data payload into a byte array.  See the
     * <a href='http://www.ericdaugherty.com/dev/soht/protocol.html'>
     * Protocol definition</a> for more details.
     *
     * @param inputData encoded String
     * @param decodedLength the number of 'real' bytes sent.
     * @return byte array.
     */
    private byte[] decode( String inputData, int decodedLength ) {
		byte[] rawBytes = inputData.getBytes();
		byte[] decodedBytes = new byte[decodedLength];
		int rawIndex = 0;

		for ( int decodedIndex = 0; decodedIndex < decodedLength; decodedIndex++ ) {
			char character = (char) rawBytes[rawIndex];

			if ( character == '#' ) {
				int decodedInt = Integer.decode( "#" + (char) rawBytes[rawIndex + 1] + (char) rawBytes[rawIndex + 2] );
				decodedBytes[decodedIndex] = (byte) decodedInt;
				rawIndex += 3;
			} else {
				decodedBytes[decodedIndex] = rawBytes[rawIndex++];
			}
		}

		return decodedBytes;
    }

	/**
	 * Returns the remote address through parsing the "x-forwarded-for" header from the request given.
	 *
	 * @param request the request we are processing.
	 * @return the remote address gotten from the header of request given.
	 */
	private String getRemoteAddr( HttpServletRequest request ) {
		String remoteAddr = request.getHeader( "x-forwarded-for" );

//		if ( remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase( remoteAddr ) ) {
//			remoteAddr = request.getHeader( "Proxy-Client-IP" );
//		}
//
//		if ( remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase( remoteAddr ) ) {
//			remoteAddr = request.getHeader( "WL-Proxy-Client-IP" );
//		}

		if ( remoteAddr == null || remoteAddr.isEmpty() || "unknown".equalsIgnoreCase( remoteAddr ) ) {
			remoteAddr = request.getRemoteAddr();
		}

		return remoteAddr;
	}

	/**
	 * Get the host to which the proxied server will connect from the request.
	 *
	 * @param request the request we are processing.
	 * @return the host to connect.
	 */
	private String getHost( HttpServletRequest request ) {
		String host = request.getParameter( "host" );

		if ( host == null ) {
			throw new IllegalArgumentException( "Missing required parameter: 'host'" );
		}

		host = host.trim();

		if ( host.isEmpty() ) {
			throw new IllegalArgumentException( "Invalid parameter of 'host' - " + host );
		}

		// Avoid HM working as the proxy server connecting any other hosts except for HM itself.
//		if ( !host.equals( "127.0.0.1" ) && !host.equalsIgnoreCase( "localhost" ) ) {
//			throw new IllegalArgumentException( "The host must be 'localhost'" );
//		}

		return host;
	}

	/**
	 * Get the port of host to connect.
	 *
	 * @param request the request we are processing.
	 * @return the port of host to connect.
	 */
	private int getPort( HttpServletRequest request ) {
		String strPort = request.getParameter( "port" );

		if ( strPort == null ) {
			throw new IllegalArgumentException( "Missing required parameter: 'port'" );
		}

		strPort = strPort.trim();

		return Integer.parseInt( strPort );
	}

	/**
	 * Get the username for authentication.
	 *
	 * @param request the request we are processing.
	 * @return the username for authentication.
	 */
	private String getUserName( HttpServletRequest request ) {
		String username = request.getParameter( "username" );

		if ( username == null ) {
			throw new IllegalArgumentException( "Missing required parameter: 'username'" );
		}

		username = username.trim();

		if ( username.isEmpty() ) {
			throw new IllegalArgumentException( "Invalid parameter of 'username' - " + username );
		}

		return username;
	}

	/**
	 * Ge the password for the username for authentication.
	 *
	 * @param request the request we are processing.
	 * @return the password together with username for authentication.
	 */
	private String getPassword( HttpServletRequest request ) {
		String password = request.getParameter( "password" );

		if ( password == null ) {
			throw new IllegalArgumentException( "Missing required parameter: 'password'" );
		}

		password = password.trim();

		if ( password.isEmpty() ) {
			throw new IllegalArgumentException( "Invalid parameter of 'password' - " + password );
		}

		return password;
	}

	/**
	 * Returns the client authentication result based on the username and password provided
	 *
	 * @param username client user name.
	 * @param password the password for the username.
	 * @return true if authentication is success, false otherwise.
	 */
	private boolean authenticate( String username, String password ) {
		return NmsUtil.getHMScpUser().equals( username ) && NmsUtil.getHMScpPsd().equals( password );
	}

	/**
	 * Set up a proxy connection with the parameters specified.
	 *
	 * @param user client user principal.
	 * @param getRemoteAddr the ip address of the client.
	 * @param host the host to connect to.
	 * @param port on which the host is listening.
	 * @return a connection information object.
	 * @throws IOException if any error occurs while setting up the proxy connection.
	 */
    private ConnectionInfo setupProxyConnection( User user, String getRemoteAddr, String host, int port ) throws IOException {
		// Open a SocketChannel.
		SocketChannel proxyChannel = SocketChannel.open();

		// Set non-blocking mode for this channel.
		proxyChannel.configureBlocking( false );

		Socket proxySocket = proxyChannel.socket();

		// Set receive buffer size.
		proxySocket.setReceiveBufferSize( MAX_SO_RCVBUF );

		// Set send buffer size.
		proxySocket.setSendBufferSize( MAX_SO_SNDBUF );

		// Enable SO_KEEPALIVE
		proxySocket.setKeepAlive( true );

		// Disable the Nagle's algorithm.
		proxySocket.setTcpNoDelay( true );

		InetSocketAddress remote = new InetSocketAddress( host, port );

		// Connect to remote host.
		proxyChannel.connect( remote );

		// Open a Selector.
		Selector selector = Selector.open();
		proxyChannel.register( selector, SelectionKey.OP_CONNECT );
		int timeout = 60000;

		if ( selector.select( timeout ) > 0 ) {
			for ( Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator(); keyIter.hasNext(); ) {
				SelectionKey key = keyIter.next();
				keyIter.remove();

				if ( key.isConnectable() ) {
					SocketChannel channel = (SocketChannel) key.channel();

					if ( channel.isConnectionPending() ) {
						channel.finishConnect();
					}

					key.interestOps( SelectionKey.OP_READ );
				}
			}
		} else {
			log.warn( "Cannot connect to " + host + ":" + port + " within " + timeout + "ms." );
			proxyChannel.close();
			throw new IOException( "Cannot connect to " + host + ":" + port );
		}

		return new ConnectionInfo( user, getRemoteAddr, host, port, proxyChannel, new Date(), selector );
	}

    /**
     * Provides synchronized access to the total number of readers count.
     */
//	private static synchronized void addReader() {
//		readerCount++;
//	}

    private static void addReader() {
		synchronized ( readerCount ) {
			readerCount.incrementAndGet();
		}
    }

    /**
     * Provides synchronized access to the total number of readers count.
     */
//	private static synchronized void removeReader() {
//		readerCount--;
//	}

	private static void removeReader() {
		synchronized ( readerCount ) {
			readerCount.decrementAndGet();
		}
	}

    /**
     * Provides synchronized access to the total number of writers count.
     */
//	private static synchronized void addWriter() {
//		writerCount++;
//	}

    private static void addWriter() {
		synchronized ( writerCount ) {
			writerCount.incrementAndGet();
		}
    }

    /**
     * Provides synchronized access to the total number of writers count.
     */
//	private static synchronized void removeWriter() {
//		writerCount--;
//	}

    private static void removeWriter() {
		synchronized ( writerCount ) {
			writerCount.decrementAndGet();	
		}
    }

}
//EOF