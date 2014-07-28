/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/client/core/ProxyReadWrite.java,v $
 * $Revision: 1.5 $
 * $Author: ychen $
 * $Date: 2012/02/22 12:29:27 $
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

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.IOException;
//import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
//import java.net.Socket;
//import java.net.SocketException;
import java.nio.ByteBuffer;
//import java.nio.channels.AsynchronousCloseException;
//import java.nio.channels.CancelledKeyException;
//import java.nio.channels.ClosedByInterruptException;
//import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

//import org.apache.log4j.Logger;

import com.ericdaugherty.soht.client.configuration.ConfigurationManager;

/**
 * Handles the incoming and outgoing data when using
 * stateless connections.
 *
 * @author Eric Daugherty
 */
public class ProxyReadWrite extends BaseProxy implements SelectionKeyHandler {

    //***************************************************************
    // Constants
    //***************************************************************

	private static final long DEFAULT_SLEEP_TIME = 200;
	private static final long MAX_SLEEP_TIME = 2000;

    //***************************************************************
    // Variables
    //***************************************************************

//	private static final Logger log = Logger.getLogger( ProxyReadWrite.class );

//	/** The input stream to read from the local client */
//	private InputStream in;

//	/** The output stream to write to the local client */
//	private OutputStream out;

	/** Client input buffer */
	private final BlockingQueue<byte[]> inputBuffer;

    //***************************************************************
    // Constructor
    //***************************************************************

    public ProxyReadWrite( String name, ConfigurationManager configurationManager, long connectionId, SocketChannel clientChannel ) {
        super( name, configurationManager, connectionId, clientChannel );
		inputBuffer = new LinkedBlockingQueue<byte[]>( 1000 );
    }

//	public ProxyReadWrite( String name, ConfigurationManager configurationManager, long connectionId, Socket socket ) throws IOException {
//		super( name, configurationManager, connectionId, socket );
//
//		this.in = socket.getInputStream();
//		this.out = socket.getOutputStream();
//	}

    //***************************************************************
    // Thread Methods
    //***************************************************************

	/*-
	@Override
	public void run() {
		boolean isRunning = true;
		long sleepTime = DEFAULT_SLEEP_TIME;
		byte[] inputBytes = new byte[1024];
		byte[] outputBytes = new byte[8192];
		boolean inputShutdown = false;
		int outputCount = 0;

		try {
			while ( isRunning ) {
				int inputCount = 0;

				try {
					int available = in.available();

					if ( available > 0 ) {
						if ( available >= inputBytes.length ) {
							inputCount = in.read( inputBytes );
						} else {
							inputCount = in.read( inputBytes, 0, available );
						}
					} else if ( outputCount == 0 ) {
						long sleepTill = System.currentTimeMillis() + sleepTime;
						socket.setSoTimeout( 50 );

						while ( ( sleepTill > System.currentTimeMillis() ) && ( inputCount == 0 ) ) {
							try {
								// This is the only way I know to test for socket
								// connection closed...
								inputCount = in.read( inputBytes );

								if ( inputCount < 0 ) {
									inputShutdown = true;
									break;
								}
							} catch ( Exception e ) {
								//
							}
						}

						if ( inputCount == 0 ) {
							sleepTime += 200;

							if ( sleepTime >= MAX_SLEEP_TIME ) {
								sleepTime = MAX_SLEEP_TIME;
							}
						} else {
							sleepTime = DEFAULT_SLEEP_TIME;
						}
					}
				} catch ( SocketException socketException ) {
					// This is normal. Only print out an error if it was not a
					// Socket Closed Exception.
					if ( !"Socket closed".equals( socketException.getMessage() ) ) {
						System.out.println( "Error in reading data from server: "
								+ socketException );
					}

					closeServer();
					break;
				}

				if ( inputShutdown ) {
					closeServer();
					break;
				}

				HttpURLConnection urlConnection = configurationManager.getURLConnection();

				// Write parameters.
				BufferedWriter out = new BufferedWriter( new OutputStreamWriter( urlConnection
						.getOutputStream() ) );
				out.write( "action=readwrite" );
				out.write( "&" );
				out.write( "id=" + connectionId );
				out.write( "&" );
				out.write( "datalength=" + inputCount );
				out.write( "&" );
				out.write( "data=" );
				out.write( encode( inputBytes, inputCount ) );
				out.flush();
				out.close();

				urlConnection.connect();
				InputStream serverInputStream = urlConnection.getInputStream();

				// Read data from the server and write it to our local client.

				outputCount = 0;
				boolean isFirst = true;

				while ( true ) {
					int count = serverInputStream.read( outputBytes );

					// Read until the server disconnects.
					if ( count == -1 ) {
						break;
					}

					// The requested proxied connection doesn't exist or may have been closed.
					if ( isFirst && count > 0 && outputBytes[0] == 0 ) {
						isRunning = false;
						break;
					}

					// Pass the data to the client, minus the status byte.
					int startIndex = isFirst ? 1 : 0;

					try {
						this.out.write( outputBytes, startIndex, count - startIndex );
					} catch ( IOException e ) {
						// The local connection is closed, so close the server.
						closeServer();
					}

					isFirst = false;
					outputCount += ( count - startIndex );
				}
			}

			// Close the socket.
			socket.close();
		} catch ( Exception ioe ) {
			ioe.printStackTrace();
		}
	}*/

	@Override
	public void run() {
		boolean serverClosed = false;
		long sleepTime = DEFAULT_SLEEP_TIME;
		byte[] outputBytes = new byte[8192];
		ByteBuffer outputBuf = ByteBuffer.allocate( outputBytes.length );
	//	int outputCount = 0;

		try {
			while ( clientChannel.isOpen() ) {
				byte[] inputBytes = inputBuffer.poll( sleepTime, TimeUnit.MILLISECONDS );

				if ( inputBytes == null ) {
					// Add the delay if there is no data ready.
					sleepTime += 200;

					if ( sleepTime >= MAX_SLEEP_TIME ) {
						sleepTime = MAX_SLEEP_TIME;
					}
				} else {
					sleepTime = DEFAULT_SLEEP_TIME;
				}

				if ( inputBytes == null ) {
					inputBytes = new byte[0];
				}

				int inputCount = inputBytes.length;
				HttpURLConnection urlConnection = configurationManager.getURLConnection();

				// Write parameters.
				BufferedWriter out = new BufferedWriter( new OutputStreamWriter( urlConnection
						.getOutputStream() ) );
				out.write( "action=readwrite" );
				out.write( "&" );
				out.write( "id=" + connectionId );
				out.write( "&" );
				out.write( "datalength=" + inputCount );
				out.write( "&" );
				out.write( "data=" );
				out.write( encode( inputBytes, inputCount ) );
				out.flush();
				out.close();

				urlConnection.connect();

				InputStream serverInputStream = urlConnection.getInputStream();

				// Read data from the server and write it to our local client.

			//	outputCount = 0;
				boolean isFirst = true;

				// Make sure we can do cleanup even if there is an error...
				try {
					while ( true ) {
						int count = serverInputStream.read( outputBytes );

						// Read until the server disconnects.
						if ( count == -1 ) {
							break;
						}

						// The requested proxied connection doesn't exist or may have been closed.
						if ( isFirst && count > 0 && outputBytes[0] == 0 ) {
							serverClosed = true;
							return;
						}

						// Pass the data to the client, minus the status byte.
						int startIndex = isFirst ? 1 : 0;
						int outputLength = count - startIndex;

						outputBuf.clear();

//						try {
							outputBuf.put( outputBytes, startIndex, outputLength );
							outputBuf.flip();

							int totalCount = outputBuf.limit();

							// Write until reach the limit of the buffer.
							while ( totalCount > 0 ) {
								int writeCount = clientChannel.write( outputBuf );

								if ( writeCount == 0 ) {
									try {
										Thread.sleep( 500 );
									} catch ( InterruptedException ie ) {
									//	log.error( "Interrupt occurred while writing to the client.", ie );
										ie.printStackTrace();
									}
								} else {
									totalCount -= writeCount;
								}
							}
//						} catch ( IOException e ) {
//							// The local connection is closed, so close the server.
//							closeServer();
//						}

						isFirst = false;
					//	outputCount += ( outputLength );
					}
				} finally {
					if ( serverInputStream != null ) {
						try {
							serverInputStream.close();
						} catch ( IOException ioe ) {
						//	log.error( "Error in closing input stream.", ioe );
							ioe.printStackTrace();
						}
					}
				}
			}
//		} catch ( InterruptedException ie ) {
//			log.error( "Interrupt occurred in ReadWrite Proxy.", ie );
//		} catch ( ClosedByInterruptException cbie ) {
//			log.error( "Interrupt occurred while the write operation is in progress.", cbie );
//		} catch ( AsynchronousCloseException ace ) {
//			log.error( "The client socket channel was closed by another thread while the write operation is in progress.", ace );
//		} catch ( ClosedChannelException cce ) {
//			log.error( "The client socket channel was closed while the write operation is in progress.", cce );
//		} catch ( IOException ioe ) {
//			log.error( "IO Error in ReadWrite Proxy.", ioe );
//		} catch ( Exception e ) {
//			log.error( "Non IO Error in ReadWrite Proxy.", e );
		} catch ( Exception e ) {
		//	log.error( "Error occurred while reading from server/writing to client.", e );
			e.printStackTrace();
		} finally {
			// Notify server to close the proxy connection.
			if ( !serverClosed ) {
				closeServer();
			}			

			// Close the client connection.
			if ( clientChannel != null && clientChannel.isOpen() ) {
				try {
					clientChannel.close();
				} catch ( IOException ioe ) {
				//	log.error( "Error in closing connection to client.", ioe );
					ioe.printStackTrace();
				}
			}

		//	log.info( getName() + " closed." );
			System.out.println( getName() + " closed." );
		}
	}

	@Override
	public void handle( SelectionKey key ) {
		SocketChannel clientChannel = null;
		boolean closingClientChannel = false;
		
		try {
			if ( !key.isReadable() ) {
			//	log.warn( "The given SelectionKey was unreadable, ignore handling it." );
				System.out.println( "The given SelectionKey was unreadable, ignore handling it." );
				return;
			}

			ByteBuffer buf = ByteBuffer.allocate( 1024 );
			clientChannel = (SocketChannel) key.channel();
			int count = clientChannel.read( buf );

			// A count of -1 indicates that the input has been shut down.
			if ( count == -1 ) {
			//	if ( log.isDebugEnabled() ) {
			//		log.debug( "The input has been shut down." );
			//	}

				System.out.println( "The input has been shut down." );

				/*-
				if ( key.isValid() && key.isReadable() ) {
					// Deregister read operation with this key.
					log.info( "Deregister the read operation with this key." );
					key.interestOps( key.interestOps() & ~SelectionKey.OP_READ );
				}*/

				closingClientChannel = true;
		   		closeServer();
			} else {
				byte[] bytes = new byte[count];

				buf.flip();
				buf.get( bytes );

				// Add the data read from client into queue so that it can be taken and sent to the server in the run().
				inputBuffer.add( bytes );
			}
//		} catch ( CancelledKeyException cke ) {
//			log.error( "The given SelectionKey has been cancelled.", cke);
//			closingConnection = true;
//		} catch ( ClosedByInterruptException cbie ) {
//			log.error( "Interrupt occurred while the read operation is in progress.", cbie );
//			closingConnection = true;
//		} catch ( AsynchronousCloseException ace ) {
//			log.error( "The client socket channel has been closed by another thread while the read operation is in progress.", ace );
//			closingConnection = true;
//		} catch ( ClosedChannelException cce ) {
//			log.error( "The client socket channel has been closed while the read operation is in progress.", cce );
//			closingConnection = true;
//		} catch ( IOException e ) {
//			log.error( "IO Error in ReadWrite Proxy.", e );
//			closingConnection = true;
		} catch ( Exception e ) {
		//	log.error( "Error occurred while reading from client/writing to server.", e );
			e.printStackTrace();
			closingClientChannel = true;
			
//			if ( clientChannel != null && clientChannel.isOpen() ) {
//				try {
//					clientChannel.close();
//				} catch ( IOException ioe ) {
//				//	log.error( "Error in closing connection to client.", ioe );
//					ioe.printStackTrace();
//				}
//			}
		} finally {
			if ( closingClientChannel && clientChannel != null && clientChannel.isOpen() ) {
				try {
					clientChannel.close();
				} catch ( IOException ioe ) {
					ioe.printStackTrace();
				}
			}			
		}
	}

}