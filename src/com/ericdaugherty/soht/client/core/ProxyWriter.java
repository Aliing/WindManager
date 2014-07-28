/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/client/core/ProxyWriter.java,v $
 * $Revision: 1.7 $
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
//import java.io.InputStream;
import java.io.IOException;
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

//import org.apache.log4j.Logger;

import com.ericdaugherty.soht.client.configuration.ConfigurationManager;

/**
 * Handles the outgoing data to the remote host.
 *
 * @author Eric Daugherty
 */
public class ProxyWriter extends BaseProxy implements SelectionKeyHandler {

    //***************************************************************
    // Variables
    //***************************************************************

//	private static final Logger log = Logger.getLogger( ProxyWriter.class );

//	/** The input stream to read from the local client */
//	private InputStream in;

    //***************************************************************
    // Constructor
    //***************************************************************

    public ProxyWriter( String name, ConfigurationManager configurationManager, long connectionId ) {
        super( name, configurationManager, connectionId );
    }

//	public ProxyWriter( String name, ConfigurationManager configurationManager, long connectionId, Socket socket ) throws IOException {
//		super( name, configurationManager, connectionId, socket );
//
//		this.in = socket.getInputStream();
//	}

    //***************************************************************
    // Thread Methods
    //***************************************************************

	/*-
	@Override
	public void run() {
		try {
			HttpURLConnection urlConnection;
			BufferedWriter out;

			byte[] bytes = new byte[1024];
			int count;

			while ( true ) {
				try {
					count = in.read( bytes );
				} catch ( SocketException socketException ) {
					// This is normal.  Only print out an error if it was not a
					// Socket Closed Exception.
					if ( !"Socket closed".equals( socketException.getMessage() ) ) {
						System.out.println( "Error in reading data from server: " + socketException );
					}

					break;
				}

				if ( count == -1 ) {
					closeServer();
					break;
				}

				urlConnection = configurationManager.getURLConnection();

				//Write parameters.
				out = new BufferedWriter( new OutputStreamWriter( urlConnection.getOutputStream() ) );
				out.write( "action=write" );
				out.write( "&" );
				out.write( "id=" + connectionId );
				out.write( "&" );
				out.write( "datalength=" + count );
				out.write( "&" );
				out.write( "data=" );
				out.write( encode( bytes, count ) );
				out.flush();
				out.close();

				urlConnection.connect();
				urlConnection.getInputStream();
			}
		} catch ( IOException ioe ) {
			ioe.printStackTrace();
		}
	}*/

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
			int	count = clientChannel.read( buf );

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

				HttpURLConnection urlConnection = configurationManager.getURLConnection();

				// Write parameters.
				BufferedWriter out = new BufferedWriter( new OutputStreamWriter( urlConnection.getOutputStream() ) );
				out.write( "action=write" );
				out.write( "&" );
				out.write( "id=" + connectionId );
				out.write( "&" );
				out.write( "datalength=" + count );
				out.write( "&" );
				out.write( "data=" );
				out.write( encode( bytes, count ) );
				out.flush();
				out.close();

				urlConnection.connect();

				// Get input stream is extremely needed here.
				urlConnection.getInputStream();
			}
//		} catch ( CancelledKeyException cke ) {
//			log.error( "The given SelectionKey has been cancelled.", cke);
//
//			// Deregister for readability
//			key.interestOps( key.interestOps() & ~SelectionKey.OP_READ );
//		} catch ( ClosedByInterruptException cbie ) {
//			log.error( "Interrupt occurred while the read operation is in progress.", cbie );
//		} catch ( AsynchronousCloseException ace ) {
//			log.error( "The client socket channel was closed by another thread while the read operation is in progress.", ace );
//		} catch ( ClosedChannelException cce ) {
//			log.error( "The client socket channel was closed while the read operation is in progress.", cce );
//		} catch ( IOException e ) {
//			log.error( "IO Error in Write Proxy.", e );
//
//			if ( clientChannel.isOpen() ) {
//				try {
//					clientChannel.close();
//				} catch ( IOException ioe ) {
//					log.error( "Error in closing connection to client.", ioe );
//				}
//			}
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