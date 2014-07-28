/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/server/ConnectionInfo.java,v $
 * $Revision: 1.1 $
 * $Author: ychen $
 * $Date: 2010/06/01 10:42:19 $
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
import java.io.OutputStream;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Stores all information for a specific connection.
 * <p>
 * Instances of this class are stored in the SessionContext.
 *
 * @author Eric Daugherty
 */
public class ConnectionInfo {

    //***************************************************************
    // Variables
    //***************************************************************

//	private static long nextConnectionId = 0;

	private static final AtomicLong nextConnectionId = new AtomicLong( 0 );

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat( "MM/dd/yyyy hh:mm:ss a zzz" );

	private User user;
	private long connectionId;
	private String clientHost;
	private String targetHost;
	private int targetPort;
	private Socket socket;
	private Date timeOpened;
	private InputStream inputStream;
	private OutputStream outputStream;
	private SocketChannel socketChannel;
	private Selector selector;

    //***************************************************************
    // Constructor
    //***************************************************************

	public ConnectionInfo( User user, String clientHost, String targetHost,
						   int targetPort, Socket socket, Date timeOpened,
						   InputStream inputStream, OutputStream outputStream ) {
//		synchronized ( ConnectionInfo.class ) {
//			connectionId = nextConnectionId++;
//		}

		synchronized ( nextConnectionId ) {
			connectionId = nextConnectionId.getAndIncrement();
		}

		this.user = user;
		this.clientHost = clientHost;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.socket = socket;
		this.timeOpened = timeOpened;
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}

	public ConnectionInfo( User user, String clientHost, String targetHost,
						   int targetPort, SocketChannel socketChannel,
						   Date timeOpened, Selector selector ) {
		synchronized ( nextConnectionId ) {
			connectionId = nextConnectionId.getAndIncrement();
		}

		this.user = user;
		this.clientHost = clientHost;
		this.targetHost = targetHost;
		this.targetPort = targetPort;
		this.socketChannel = socketChannel;
		this.timeOpened = timeOpened;
		this.selector = selector;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

    public long getConnectionId() {
        return connectionId;
    }

    public User getUser() {
        return user;
    }

    public String getClientHost() {
        return clientHost;
    }

    public String getTargetHost() {
        return targetHost;
    }

    public int getTargetPort() {
        return targetPort;
    }

	public Socket getSocket() {
		return socket;
	}

	public Date getTimeOpened() {
		return timeOpened;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public Selector getSelector() {
		return selector;
	}

	//***************************************************************
    // Public Methods
    //***************************************************************

    /**
     * Returns a string containing the clientHost, targetHost, and targetPort.
     */
	@Override
    public String toString() {
        return "Connection from: " + clientHost + " to " + targetHost + ":" + targetPort + " Opened: " + dateFormat.format( timeOpened )  + " ID: " + connectionId;
    }

}
//EOF