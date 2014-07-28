/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/client/core/ProxyReader.java,v $
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
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

//import org.apache.log4j.Logger;

import com.ericdaugherty.soht.client.configuration.ConfigurationManager;

/**
 * Handles the incoming data from the remote host.
 *
 * @author Eric Daugherty
 */
public class ProxyReader extends BaseProxy {

    //***************************************************************
    // Variables
    //***************************************************************

//	private static final Logger log = Logger.getLogger( ProxyReader.class );

//	/** The output stream to write to the local client */
//	private OutputStream out;

    //***************************************************************
    // Constructor
    //***************************************************************

    public ProxyReader( String name, ConfigurationManager configurationManager, long connectionId, SocketChannel clientChannel ) {
        super( name, configurationManager, connectionId, clientChannel );
    }

//	public ProxyReader( String name, ConfigurationManager configurationManager, long connectionId, Socket socket ) throws IOException {
//		super( name, configurationManager, connectionId, socket );
//
//		this.out = socket.getOutputStream();
//	}

    //***************************************************************
    // Thread Methods
    //***************************************************************

	/**
	 * Reads from the remote server until the connection closes.
	 */
	/*-
	@Override
	public void run() {
		try {
			HttpURLConnection urlConnection = configurationManager.getURLConnection();

			// Write parameters.
			BufferedWriter out = new BufferedWriter( new OutputStreamWriter( urlConnection.getOutputStream() ) );
			out.write( "action=read" );
			out.write( "&" );
			out.write( "id=" + connectionId );
			out.flush();
			out.close();

			// Post the read request to the server.
			urlConnection.connect();

			InputStream in = null;

			// Make sure we can do cleanup even if there is an error...
			try {
				in = urlConnection.getInputStream();

				// Read data from the server and write it to our local client.
				byte[] bytes = new byte[1024];
				boolean isFirst = true;

				while ( true ) {
					int count = in.read( bytes );

					// If the server disconnects, disconnect our client.
					if ( count == -1 || ( isFirst && count > 0 && bytes[0] == 0 ) ) {
						out.close();
						socket.close();
						break;
					}

					// Pass the data to the client, minus the status byte.
					int startIndex = isFirst ? 1 : 0;

					try {
						this.out.write( bytes, startIndex, count - startIndex );
					} catch ( IOException e ) {
						// The local connection is closed, so close the server.
						closeServer();
					}

					isFirst = false;
				}
			} finally {
				if ( in != null ) {
					in.close();
				}

				urlConnection.disconnect();
			}
		} catch ( IOException ioe ) {
			if ( out != null ) {
				try {
					out.close();
					socket.close();
				} catch ( IOException e ) {
					System.out.println( "Error in closing output stream to client." );
				}
			}

			System.out.println( "IOException in ProxyReader." );
			ioe.printStackTrace();
		}
	}*/

	@Override
	public void run() {
		HttpURLConnection urlConnection = null;
		InputStream in = null;

		try {
			urlConnection = configurationManager.getURLConnection();

			// Write parameters.
			BufferedWriter out = new BufferedWriter( new OutputStreamWriter( urlConnection.getOutputStream() ) );
			out.write( "action=read" );
			out.write( "&" );
			out.write( "id=" + connectionId );
			out.flush();
			out.close();

			// Post the read request to the server.
			urlConnection.connect();

			in = urlConnection.getInputStream();

			// Read data from the server and then write it to our local client.
			byte[] bytes = new byte[8192];
			ByteBuffer buf = ByteBuffer.allocate( bytes.length );
			boolean isFirst = true;

			while ( clientChannel.isOpen() ) {
				int count = in.read( bytes );

				// If the server disconnects, disconnect our client.
				if ( count == -1 || ( isFirst && count > 0 && bytes[0] == 0 ) ) {
				//	clientChannel.close();
					break;
				}

				// Pass the data to the client, minus the status byte.
				int startIndex = isFirst ? 1 : 0;
				buf.clear();

				try {
					buf.put( bytes, startIndex, count - startIndex );
					buf.flip();

					int totalCount = buf.limit();

					// Write until reach the limit of the buffer.
					while ( totalCount > 0 ) {
						int writeCount = clientChannel.write( buf );

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

				} catch ( IOException ioe ) {
					// The local connection is closed, so close the server.
				//	log.error( "The local connection was closed.", ioe );
					ioe.printStackTrace();
					closeServer();
					break;
				}

				isFirst = false;
			}
//		} catch ( IOException ioe ) {
//			log.error( "IO Error in Read Proxy.", ioe );
//		} catch ( Exception e ) {
//			log.error( "Non IO Error Read Proxy.", e );
		} catch ( Exception e ) {
		//	log.error( "Error occurred while reading from server/writing to client.", e );
			e.printStackTrace();
		} finally {
			if ( in != null ) {
				try {
					in.close();
				} catch ( IOException ioe ) {
				//	log.error( "Error in closing input stream.", ioe );
					ioe.printStackTrace();
				}
			}

			if ( urlConnection != null ) {
				urlConnection.disconnect();
			}

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

}