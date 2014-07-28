/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/sshwebproxy/ShellChannel.java,v $
 * $Revision: 1.32.70.1 $
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.session.SessionChannelClient;

import com.ah.be.common.NmsUtil;
import com.ah.util.Tracer;

/**
 * Provide an implementation of the SshChannel for interactive
 * shell sessions.
 * <p>
 * While this is a concrete class that can be instantiated,
 * the VT100ShellChannel should be used as the default ShellChannel
 * because it provides a more robust shell implementation.  This class
 * should be extended by other terminal emulation classes, or instantiated
 * directly for a very simple shell interaction.
 *
 * @author Eric Daugherty
 */
public class ShellChannel extends SshChannel implements SshConstants {

    //***************************************************************
    // Variables
    //***************************************************************

	private static final long serialVersionUID = 1L;

	/** The number of columns to display as a screen */
    protected int screenWidth = 80;

    /** The number of rows to display as a screen */
    protected int screenHeight = 5000;

    /** The total number of rows to store */
    private int bufferMaxSize = 5000;

    /** The total number of command to store */
    private int commandMaxSize = 10;

	/** The number of milliseconds to pause before reading */
    private int readPause = 250;

    /** The size of the buffer to read from the server */
    private int readBufferSize = 8192;

    /** The row that the cursor is currently on */
    private int cursorRow = -1;

    /** The column that the cursor is currently on */
    private int cursorColumn = -1;

	/** The index of the command that was executed previously */
	private int commandIndex = 0;

	/** The Channel for the current Shell Connection */
    protected SessionChannelClient sshChannel;

    /** The Input Reader for the SSH shell connection. */
    private BufferedReader reader;

    /** The Output Writer for the SSH shell connection */
    private PrintWriter writer;

    /** The entire stored buffer */
    private final List<String> buffer;

	/** Which is used to cache incomplete commands and combine them into a complete command to be stored */
	private String cachedCommand = "";

	/** The executed command buffer */
	private final List<String> commandBuffer;

	/** Logger */
	private static final Log log = LogFactory.getLog( ShellChannel.class );

	/** Native logger */
	private static final Tracer tracer = new Tracer(ShellChannel.class.getSimpleName());

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Opens a vt100 terminal session transfer session with the server.
     *
     * @param sshConnection the connection to use.
     * @param sshChannel the SSH API channel.
     * @throws SshConnectException thrown if there is any error opening the connection.
     */
    public ShellChannel( SshConnection sshConnection, SessionChannelClient sshChannel ) throws SshConnectException {
        super( CHANNEL_TYPE_SHELL, sshConnection );

		this.sshChannel = sshChannel;

		// Initialize the channel for a shell session.
		try {
			if ( !sshChannel.requestPseudoTerminal( "vt100", getScreenWidth(), getScreenHeight(), 0, 0, "" ) ) {
				log.warn( "ShellChannel constructor failed, unable to open PseudoTerminal for connection: " + sshConnection.getConnectionInfo() );
				tracer.error( "ShellChannel", "Unable to establish PseudoTerminal for connection: " + sshConnection.getConnectionInfo() );
				throw new SshConnectException( "Unable to establish PseudoTerminal for new ShellChannel." );
			} else if ( !sshChannel.startShell() ) {
				log.warn( "ShellChannel constructor failed, unable to start shell on new channel for connection: " + sshConnection.getConnectionInfo() );
				tracer.error( "ShellChannel", "Unable to start a shell on new channel for connection: " + sshConnection.getConnectionInfo() );
				throw new SshConnectException( "Unable to start Shell for new ShellChannel." );
			}

//			try {
//				Thread.sleep( 300L );
//			} catch ( InterruptedException ie ) {
//				log.warn( "ShellChannel constructor failed, interrupt occurred while sleeping to verify the activity of the newly opened shell channel." );
//			}

			// Verify the activity of the newly opened shell channel. The shell channel will be probably closed automatically if the pseudo shell is not started truly.
			if ( !sshChannel.isOpen() ) {
				throw new SshConnectException( "The number of active CLI users exceeded the max limit 5." );
			}

			writer = new PrintWriter( new OutputStreamWriter( sshChannel.getOutputStream() ) );
			reader = new BufferedReader( new InputStreamReader( sshChannel.getInputStream() ) );
			buffer = Collections.synchronizedList( new ArrayList<String>( bufferMaxSize ) );
			commandBuffer = Collections.synchronizedList( new ArrayList<String>( commandMaxSize ) );
		} catch ( IOException ioException ) {
			log.warn( "ShellChannel constructor failed, IOException occurred while setting up channel for connection: " + sshConnection.getConnectionInfo() + ". IOException: " + ioException, ioException );
			tracer.error( "ShellChannel", "Unable to open a shell channel for connection: " + sshConnection.getConnectionInfo(), ioException );
			throw new SshConnectException( "Cannot open an SSH session channel with the "+NmsUtil.getOEMCustomer().getAccessPonitName()+". Check if the "+NmsUtil.getOEMCustomer().getAccessPonitName()+" has another currently active SSH session." );
		}
	}

    //***************************************************************
    // SshChannel Methods
    //***************************************************************

    /**
     * Closes the Reader and Writer after the Channel has been closed.
     * This should only be called by the SshConnection
     * class and never directly called from this class.
     */
    public synchronized void close() {
        // Close Readers and Writers.
		if ( log.isInfoEnabled() ) {
			log.info( "Closing ShellChannel connected to: " + sshConnection.getConnectionInfo() );
		}

		if ( reader != null ) {
			try {
				reader.close();
			} catch ( IOException ioException ) {
				log.warn( "Error closing BufferedReader for Shell Connection to: " + sshConnection.getConnectionInfo() + ".  IOException: " + ioException );
			}

			reader = null;
		}

		if ( writer != null ) {
			writer.close();
			writer = null;
        }

		// Close the channel if it is open.
		if ( sshChannel.isOpen() ) {
			try {
				sshChannel.close();
			} catch ( IOException ioException ) {
                log.warn( "Error closing SessionChannelClient for Shell Connection to: " + sshConnection.getConnectionInfo() + ".  IOException: " + ioException );
			}
		}
	}

    /**
     * Indicates whether this connection is still active.
     *
     * @return true if this connection is still active.
     */
    public boolean isConnected() {
        return !sshChannel.isClosed();
    }

    /**
     * Returns the page that should be used to display this Channel.
     *
     * @return the page that should be used to display this Channel.
     */
    public String getPage() {
        return PAGE_SHELL_HOME + "?connection=" + sshConnection.getConnectionInfo() + "&channel=" + getChannelId();
    }

    //***************************************************************
    // Public Parameter Access
    //***************************************************************


    /**
     * The number of columns to display as a screen.
	 *
	 * @return The number of columns to display as a screen.
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     * The number of rows to display as a screen.
	 *
	 * @return The number of rows to display as a screen.
     */
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * The total number of rows to store in the buffer.
     *
     * @return number of rows to store.
     */
    public int getBufferMaxSize() {
        return bufferMaxSize;
    }

    /**
     * The number of milliseconds to pause before reading
     * data.  This helps reduce the number of read requests
     * after a write request.
     *
     * @return the number of milliseconds to pause.
     */
    public int getReadPause() {
        return readPause;
    }

    /**
     * The maximum amount of data to read from the server
     * for each read() call.
     *
     * @return the max read buffer size.
     */
    public int getReadBufferSize() {
        return readBufferSize;
    }

    /**
     * The index of the row the cursor is on.  The cursor location
     * is assumed to be after that last character from the server.
     *
     * @return the row index of the cursor.
     */
    public int getCursorRow() {
        return cursorRow;
    }

    /**
     * The index of the row the cursor is on.  The cursor location
     * is assumed to be after that last character from the server.
     *
     * @return the column index of the cursor.
     */
    public int getCursorColumn() {
        return cursorColumn;
    }

	/**
     * The index of command.  The cursor is currently on
     * after user uses the upwards or downwards key.
     *
     * @return the index of command.
     */
	public int getCommandIndex() {
		return commandIndex;
	}

	public String getLastCommand() {
		synchronized ( commandBuffer ) {
			int size = commandBuffer.size();

			if ( size > 0 ) {
				if ( commandIndex > 0 && commandIndex <= size ) {
					return commandBuffer.get( --commandIndex );
				} else if ( commandIndex == 0 ) {
					return commandBuffer.get( 0 );
				} else {
					commandIndex = size;
					return commandBuffer.get( --commandIndex );
				}
			} else {
				return "";
			}
		}
	}

	public String getNextCommand() {
		synchronized ( commandBuffer ) {
			int size = commandBuffer.size();

			if ( size > 0 ) {
				if ( commandIndex >= 0 && commandIndex < size - 1 ) {
					return commandBuffer.get( ++commandIndex );
				} else {
					commandIndex = size;
					return "";
				}
			} else {
				return "";
			}
		}
	}

	public void addCommand( String command ) {
		if ( log.isDebugEnabled() ) {
			log.debug( "Saving command " + command );
		}

		synchronized ( commandBuffer ) {
			commandBuffer.add( command );
			commandIndex = commandBuffer.size();
		}
	}

	//***************************************************************
    // Public Data Manipulation
    //***************************************************************

    /**
     * Performs a read of the input data and fills the buffer.
     * This should be called before getScreen or getBuffer.
     */
    public synchronized void read() {
		if ( log.isDebugEnabled() ) {
			log.debug( "read called for ShellConnection to: " + sshConnection.getConnectionInfo() );
		}

        // We want to read even if the channel has been closed, because
        // the BufferedReader may have buffered some input, so do the
        // check after this read.  But if the reader is null, just
        // ignore the call.
        if ( reader == null ) {
			log.warn( "read called on null reader. Ignoring." );
			return;
        }

        // Read from the server
		try {
			// Initialize the input buffer.
			char[] inputBuffer = new char[readBufferSize];

			// Sleep for the read pause.  This allows the server
			// to send us the 'full' data.  If we don't sleep,
			// the user may just have to do a refresh right away
			// anyway.
			try {
				Thread.sleep( getReadPause() );
			} catch ( InterruptedException ie ) {
				log.warn( "Read Pause interrupted in read()." );
			}

			// If there is data ready, go ahead and read it.
			if ( reader.ready() ) {
				// read the data and run it through the processor.
				int count = reader.read( inputBuffer );

				if ( count != -1 ) {
					if ( log.isDebugEnabled() ) {
						log.debug( count + " characters are read from server." );
					}

					String input = process( inputBuffer, count );
					fillBuffer( input );

					// Check to see if the channel was closed.
					if ( !isConnected() ) {
						if ( !reader.ready() ) {
							if ( log.isDebugEnabled() ) {
								log.debug( "ShellChannel for connecton: " + sshConnection.getConnectionInfo() + " Closed, closing streams." );
							}

							// Notify the sshConnection that this channel is closed.
							sshConnection.closeChannel( this );
						} else {
							if ( log.isDebugEnabled() ) {
								log.debug( "Connection Closed but there is more data to be read." );
							}
						}
					}
				} else {
					if ( log.isDebugEnabled() ) {
						log.debug( "None characters are read from server." );
					}
				}
            } else {
				if ( log.isDebugEnabled() ) {
					log.debug( "ShellChannel for connection: " + sshConnection.getConnectionInfo() + " has no data to read." );
				}
			}
        } catch ( IOException ioException ) {
			ioException.printStackTrace();
			log.error( "Error reading ShellChannel for connection: " + sshConnection.getConnectionInfo() + ".  IOException while in read(): " + ioException, ioException );
		}
	}

    /**
     * Writes the data to the SSH server and sends a newline character
     * "\n" if the sendNewLine boolean is true.
     *
     * @param data the data to write.
     * @param sendNewLine true if a newline should be sent.
     */
//    public synchronized void write( String data, boolean sendNewLine ) {
//		// Don't write if the channel is closed.
//		if ( !isConnected() ) {
//			log.info( "Write call on closed ShellChannel for connection: " + sshConnection.getConnectionInfo() + ".  Ignoring." );
//			return;
//		}
//
//		// Verify the writer is not null.
//		if ( writer == null ) {
//			log.info( "Write call on closed ShellChannel Writer for connection: " + sshConnection.getConnectionInfo() + ".  Ignoring." );
//			return;
//		}
//
//		// Encode the data for output.  Convert any control characters to
//		// the correct char value.
////		char[] output = encodeOutput( data );
//		char[] output = encodeOutput( data, sendNewLine );
//
//		if ( log.isDebugEnabled() ) {
//			log.debug( "Wrote " + output.length + " characters to ShellChannel for connection: " + sshConnection.getConnectionInfo() );
//		}
//
//		// Write the output, and send a new line if requested.
//		writer.print( output );
//
////		if ( sendNewLine ) {
////			writer.print( "\n" );
////		}
//
//		writer.flush();
//	}

    /**
     * Writes the data to the SSH server and sends a newline character
     * "\n" if the sendNewLine boolean is true.
     *
     * @param data the data to write.
	 * @param keyCode Keyboard value.
     */
    public synchronized void write( String data, int keyCode ) {
		// Don't write if the channel is closed.
		if ( !isConnected() ) {
			if ( log.isInfoEnabled() ) {
				log.info( "Write call on closed ShellChannel for connection: " + sshConnection.getConnectionInfo() + ".  Ignoring." );
			}

			return;
		}

		// Verify the writer is not null.
		if ( writer == null ) {
			if ( log.isInfoEnabled() ) {
				log.info( "Write call on closed ShellChannel Writer for connection: " + sshConnection.getConnectionInfo() + ".  Ignoring." );
			}

			return;
		}

		// Encode the data for output.  Convert any control characters to
		// the correct char value.
//		char[] output = encodeOutput( data );
		char[] output = encodeOutput( data, keyCode );

		if ( log.isDebugEnabled() ) {
			log.debug( "Wrote " + output.length + " characters to ShellChannel for connection: " + sshConnection.getConnectionInfo() );
		}

		// Write the output, and send a new line if requested.
		writer.print( output );

//		if ( sendNewLine ) {
//			writer.print( "\n" );
//		}

		writer.flush();

		saveCommand( data, keyCode );
	}

	/**
     * Adds the data that was read to the buffer.
     *
     * @param input the processed data read from the server
     */
    public void fillBuffer( String input ) {
		// Add data to the buffer.
		String processedData = input.replace( "^M", "\r" );// Replace control character 'CR'.
    	processedData = processedData.replace( "^J", "\n" );// Replace control character 'LF'.
		String[] lines = processedData.split( "\r\n" );
		int startIndex = 0;

		synchronized ( buffer ) {
			if ( buffer.size() > 0 && lines.length > 0 ) {
				String firstLine = lines[0];
				int lastIndex = buffer.size() - 1;
				String lastLine = buffer.get( lastIndex );

				// The messages behind the " --More-- " should be placed in a new line,
				// otherwise append the first line to the end of the last line.
				if ( lastLine.trim().endsWith( "--More--" ) ) {
					if ( firstLine.startsWith( "                                      " ) ) {
						int lastButOneIndex = lastIndex - 1;

						if ( lastButOneIndex >= 0 ) {
							String lastButOneLine = buffer.get( lastButOneIndex );
							buffer.set( lastButOneIndex, lastButOneLine + firstLine.replace( "                                      ", "" ) );
						}
					} else {
						// Remove the first ten redundant spaces which comes from the " --More-- " indication.
						if ( firstLine.length() >= 10 && firstLine.startsWith( "          " ) ) {
							firstLine = firstLine.substring( 10 );
						}

						// Get rid of 28 spaces ahead of the string.
						firstLine = firstLine.replace( "                            ", "" );

						int pos = firstLine.indexOf( " --More-- " );

						if ( pos >= 0 ) {
							buffer.add( firstLine.substring( 0 , pos ) );
							buffer.add( firstLine.substring( pos ) );
						} else {
							buffer.add( firstLine );
						}
					}					
				} else {
					buffer.set( lastIndex, lastLine + firstLine );
				}

				startIndex = 1;
			}

			// Add the rest of the new lines.
			for ( int index = startIndex; index < lines.length; index++ ) {
				boolean endsWithNewLine = false;

				// Get rid of 28 spaces ahead of the string.
				String line = lines[index].replace( "                            ", "" );

				if ( line.startsWith( "\r" ) ) {
					buffer.add( "" );
					line = line.substring( 1 );
				}

				// The " --More-- " should be separated from others and placed in a single line.
				if ( index == lines.length - 1 ) {
					int moreIndex = line.indexOf( " --More-- " );

					if ( moreIndex != -1 ) {
						String frontMore = line.substring( 0, moreIndex );

						if ( !frontMore.trim().equals( "" ) ) {
							buffer.add( frontMore );
						}

						line = line.substring( moreIndex );
					} else {
						matchCompleteCommand( line );
					}
				}

				if ( line.endsWith( "\r" ) ) {
					endsWithNewLine = true;
					line = line.substring( 0, line.length() - 1 );
				}

				if ( !line.trim().equals( "" ) ) {
					buffer.add( line );
				}

				if ( endsWithNewLine ) {
					buffer.add( "" );
				}
			}

			// Append a new empty line if the last line ended with a line feed.
//			if ( input.lastIndexOf( "\r\n" ) == input.length() - 2 ) {
//				buffer.add( "" );
//			}

			if ( input.endsWith( "\n" ) ) {
				buffer.add( "" );
			}

			removeRedundantMores( buffer );

			// Remove any extra rows from the beginning of the buffer.
			int currentBufferSize = buffer.size();

			if ( currentBufferSize > bufferMaxSize ) {
				int trimCount = currentBufferSize - bufferMaxSize;

				if ( log.isDebugEnabled() ) {
					log.debug( "Removing " + trimCount + " rows from the buffer." );
				}

				for ( int index = 0; index < trimCount; index++ ) {
					buffer.remove( 0 );
				}
			}
		}
	}

	/**
     * Returns a String array of the currently visible
     * rows.  The number of rows returned will always match
     * the Screen Size.
     *
     * @return Array of Strings that represent the current data on the screen.
     */
    public String[] getScreen() {
		String[] screen = new String[screenHeight];

		synchronized ( buffer ) {
			int currentBufferSize = buffer.size();

			if ( currentBufferSize <= screenHeight ) {
//				int index;
//
//				// Fill the screen array with the buffer.
//				for ( index = 0; index < currentBufferSize; index++ ) {
//					screen[index] = buffer.get( index );
//				}
//
//				// Fill out any remaining rows.
//				for ( ; index < screenHeight; index++ ) {
//					screen[index] = "";
//				}

				// Fill the screen array with the buffer and empty strings.
				for ( int index = 0; index < screenHeight; index++ ) {
					screen[index] = index < currentBufferSize ? buffer.get( index ) : "";
				}

				cursorRow = currentBufferSize - 1;
//				cursorColumn = -1;
			} else {
//				int screenIndex = 0;
//
//				for ( int bufferIndex = currentBufferSize - screenHeight ; bufferIndex < currentBufferSize; bufferIndex++ ) {
//					screen[screenIndex++] = buffer.get( bufferIndex );
//				}

				for ( int screenIndex = 0, bufferIndex = currentBufferSize - screenHeight; bufferIndex < currentBufferSize; bufferIndex++ ) {
					screen[screenIndex++] = buffer.get( bufferIndex );
				}

				cursorRow = screenHeight - 1;
//				cursorColumn = -1;
			}
		}

		cursorColumn = -1;

		return screen;
    }

    /**
     * Returns a String array of the entire buffer.
     * rows.
     *
     * @return Array of Strings that represent the entire buffer.
     */
    public String[] getBuffer() {
//		int currentBufferSize = buffer.size();
//		String[] bufferArray = new String[currentBufferSize];
//
//		for ( int index = 0; index < currentBufferSize; index++ ) {
//			bufferArray[index] = buffer.get( index );
//		}
//
//		return bufferArray;

		return buffer.toArray( new String[buffer.size()] );
	}

    /**
     * Process the incoming request into a string.
	 *
	 * @param inputBuffer input buffer.
	 * @param count The number of characters read.
     * @return the string representation of a specific subarray of the <code>char</code> array argument.
     */
    protected String process( char[] inputBuffer, int count ) {
//		return String.valueOf( inputBuffer, 0, count );
		return new String( inputBuffer, 0, count );
	}

    /**
     * Parse the data to write to the server for control characters.
     *
     * @param input the data read from the client.
     * @return a char array to write to the server.
     */
//    private char[] encodeOutput( String input ) {
//		int originalCount = input.length();
//		char[] translateBuffer = new char[originalCount];
//		int outputCount = 0;
//		boolean ctrlPressed = false;
//
//		for ( int index = 0; index < originalCount; index++ ) {
//			// Check if the last key was a control key.
//			if ( ctrlPressed ) {
//				ctrlPressed = false;
//				String shiftedKey = String.valueOf( input.charAt( index ) );
//				shiftedKey = shiftedKey.toUpperCase();
//				char newChar = (char) ( shiftedKey.charAt( 0 ) - 64 );
//				translateBuffer[outputCount++] = newChar;
//			} else if ( input.charAt( index ) == '#' ) { // Encode control characters.
//				// Make sure we have a full sequence.
//				if ( originalCount < ( index + 3 ) ) {
//					log.error( "Invalid input data.  Failed encoding.  There must be 2 characters after the '#' character." );
//					return new char[0];
//				}
//
//				try {
//					String charNumber = input.substring( index + 1, index + 3 );
//					int charValue = Integer.parseInt( charNumber, 16 );
//
//					if ( charValue == -1 ) {
//						ctrlPressed = true;
//					}
//
//					if ( log.isDebugEnabled() ) {
//						log.debug( "Encoded #" + charNumber + " to decimal: " + charValue );
//					}
//
//					index = index + 2;
//					translateBuffer[outputCount++] = (char) charValue;
//				} catch( NumberFormatException numberFormatException ) {
//					log.error( "Invalid input data.  failed encoding.  The control character did not contain a valid hex value." );
//					return new char[0];
//				}
//			} else {
//				translateBuffer[outputCount++] = input.charAt( index );
//			}
//		}
//
//		char[] outputBuffer = new char[outputCount];
//		System.arraycopy( translateBuffer, 0, outputBuffer, 0, outputCount );
//
//		return outputBuffer;
//	}

    /**
     * Parse the data to write to the server for control characters.
     *
     * @param input the data read from the client.
	 * @param keyCode key code value relative to keyboard.
     * @return a char array to write to the server.
     */
	private char[] encodeOutput( String input, int keyCode ) {
		char[] outputBuffer;
		char[] inputBuffer;
		int size;

		switch ( keyCode ) {
			case 0x09: // 'Tab'
			case 0x3F: // '?'
				inputBuffer = input.toCharArray();
				size = inputBuffer.length;
				outputBuffer = new char[size + 1];
				System.arraycopy( inputBuffer, 0, outputBuffer, 0, size );
				outputBuffer[size] = (char) keyCode;
				break;
			case 0x0D: // 'Enter'
				outputBuffer = ( input + "\n" ).toCharArray();
				break;
			case 0x11: // 'Ctrl'
				char controlCharacter = input != null && input.length() > 0 ? input.toUpperCase().charAt( 0 ) : 'C';
				outputBuffer = new char[2];
				outputBuffer[0] = (char) - 1;
				outputBuffer[1] = (char) ( controlCharacter - 0x40 );
				break;
			default:
				outputBuffer = new char[1];
				outputBuffer[0] = (char) keyCode;
				break;
		}

		return outputBuffer;
	}

	/**
	 * Save the input command into the command buffer to implement query for previous commands executed as pressing upwards or downwards keyboard.
	 *
	 * @param command specified command to be saved into the command buffer.
	 * @param keyCode key code value relative to keyboard.
	 */
	private void saveCommand( String command, int keyCode ) {
		switch ( keyCode ) {
			case 0x0D: // 'Enter'
				if ( command != null && !command.trim().equals( "" ) ) {
					String savedCmd;

					if ( !cachedCommand.trim().equals( "" ) ) {
						savedCmd = cachedCommand + command ;
					} else {
						savedCmd = command;
					}

					addCommand( savedCmd );
				} else if ( !cachedCommand.trim().equals( "" ) ) {
					addCommand( cachedCommand );
				}

				// Reinitialize the cached command.
				cachedCommand = "";
				break;
			case 0x09: // 'Tab'
			case 0x3F: // '?'
				// Cache incomplete command to a complete command which is going be pushed into the command buffer after receiving a 'Enter' order without any inputs.
				if ( command != null && !command.trim().equals( "" ) ) {
					cachedCommand += command;

					if ( log.isDebugEnabled() ) {
						log.debug( "Merged cached command " + cachedCommand );
					}
				}
				break;
			case 0x11: // 'Ctrl'
			default:   // Unknown key
				break;
		}
	}

	/**
	 * Match complete command.
	 *
	 * For example:
	 *
	 * When user inputs an incomplete command along with a tab key in the SSH console and following a complete command
	 * compared to the incomplete command will be provided and returned automatically by the connected SSH server.
	 * At the same time, the incomplete command which has been stored in the command buffer should be replaced with the
	 * complete command provided as well.
	 *
	 * <p>
	 * ychen#show in
	 * ychen#show interface
	 * </p>
	 *
	 * @param mixedStr which consists of host name, pound character and the complete command compared to the incomplete command user typed before the tab key last time.
	 */
	private void matchCompleteCommand( String mixedStr ) {
		// '#' character position.
		int poundCharPos = mixedStr.indexOf( "#" );

		if ( poundCharPos <= 0 ) {
			return;
		}

		String completeCmd = mixedStr.substring( poundCharPos + 1 );

		if ( completeCmd.equals( "" ) ) {
			return;
		}

		if ( !cachedCommand.trim().equals( "" ) ) {
			// Replace incomplete command with complete command.
			if ( !completeCmd.equals( cachedCommand ) && completeCmd.startsWith( cachedCommand ) ) {
				cachedCommand = completeCmd;
			}
		}
	}

	private void removeRedundantMores( List<String> buf ) {
		String lastLine = buf.get( buf.size() - 1 );

		// Remove other "--More--" strings except for the last one.
		if ( lastLine.trim().equalsIgnoreCase( "--More--" ) ) {
			for ( Iterator<String> iter = buf.iterator(); iter.hasNext(); ) {
				String line = iter.next();

				if ( iter.hasNext() && line.trim().equalsIgnoreCase( "--More--" ) ) {
					iter.remove();
				}
			}
		} else {
		// Remove all "--More--" strings.
			for ( Iterator<String> iter = buf.iterator(); iter.hasNext(); ) {
				String line = iter.next();

				if ( line.trim().equalsIgnoreCase( "--More--" ) ) {
					iter.remove();
				}
			}
		}
	}

}