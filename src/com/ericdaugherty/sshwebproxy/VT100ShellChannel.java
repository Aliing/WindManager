/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/sshwebproxy/VT100ShellChannel.java,v $
 * $Revision: 1.7 $
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sshtools.j2ssh.session.SessionChannelClient;

/**
 * Extends the default ShellChannel to provide VT100 Terminal
 * emulation.
 *
 * @author Eric Daugherty
 */
public class VT100ShellChannel extends ShellChannel {

	private static final long serialVersionUID = 1L;

	//***************************************************************
    // Constants
    //***************************************************************

    // Control Characters (all values are decimal)

	/** Backspace */
    private static final int CTRL_BS = 8;

    /** Newline */
    private static final int CTRL_NL = 10;

    /** Carriage Return */
    private static final int CTRL_CR = 13;

    /** Cancel */
    private static final int CTRL_CAN = 24;

    /** Substitute */
    private static final int CTRL_SUB = 26;

    /** Escape */
    private static final int CTRL_ESC = 27;

    // Escape Sequence Codes

    /** Start a control sequence */
    private static final int ESC_CTRL = '[';

    /** Enter Application Keypad Mode */
    private static final int ESC_ALT_KEYPAD_APPLICATION = '=';

    /** Enter Numeric Keypad Mode */
    private static final int ESC_ALT_KEYPAD_NUMERIC = '>';

    // Control Sequence Terminators

    /** CUU -- Cursor Up -- ESC [ Pn A */
    private static final int TERM_CUU = 'A';

    /** CUD -- Cursor Down -- ESC [ Pn B */
    private static final int TERM_CUD = 'B';

    /** CUF -- Cursor Forward -- ESC [ Pn C */
    private static final int TERM_CUF = 'C';

    /** CUB -- Cursor Backward -- ESC [ Pn D */
    private static final int TERM_CUB = 'D';

    /** CUP -- Cursor Position -- ESC [ Pn ; Pn H */
    private static final int TERM_CUP = 'H';

    /** Reverse Line Feed */
    private static final int TERM_RLF = 'I';

    /** Erase to End of Screen */
    private static final int TERM_EES = 'J';

    /** Erase to End of Line */
    private static final int TERM_EEL = 'K';

    /** CUP -- Cursor Position -- ESC [ Pn ; Pn f */
    private static final int TERM_CUP_2 = 'f';

    /** Set the Mode */
    private static final int TERM_MODE = 'h';

    /** Reset the Mode */
    private static final int TERM_MODE_RESET = 'l';

    /** SGR -- Select Graphic Rendition */
    private static final int TERM_SGR = 'm';

    // Cursor Movement Constants

    /** Move cursor up one row */
    private static final int CURSOR_UP = 0;

    /** Move cursor down one row */
    private static final int CURSOR_DOWN = 1;

    /** Move cursor forward (right) one column */
    private static final int CURSOR_FORWARD = 2;

    /** Move cursor back (left) one column */
    private static final int CURSOR_BACK = 3;

    //***************************************************************
    // Variables
    //***************************************************************

    /** True if we are in direct cursor manipulation mode */
    private boolean cursorMode = false;

    /** Current (row) location of the cursor */
    private int cursorRow = 0;

    /** Current (column) location of the cursor */
    private int cursorColumn = 0;

    /** The screen buffer used for cursor mode */
    private char[][] screen;

    /** True if the last character was ESC */
    private boolean inEscapeSequence = false;

    /** True if we are currently in the middle of a control sequence. */
    private boolean inControlSequence = false;

    /** The current control sequence */
    private char[] controlSequence = new char[100];

    /** The current size of the control sequence */
    private int controlSequenceSize = 0;

    /** Logger */
    private static Log log = LogFactory.getLog( VT100ShellChannel.class );

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Initializes a new VT100ShellChannel.  Uses ShellChannel constructor
     * to setup the channel.
     *
     * @param sshConnection the connection to use.
     * @param sshChannel the SSH API channel.
     * @throws SshConnectException thrown if there is any error opening
     * the connection.
     */
    public VT100ShellChannel( SshConnection sshConnection, SessionChannelClient sshChannel )
        throws SshConnectException {
        super( sshConnection, sshChannel );

        // Initialize the local screen buffer.
        screen = new char[getScreenHeight()][getScreenWidth()];
        clearScreen();
    }

    //***************************************************************
    // Static Public Methods
    //***************************************************************

    /**
     * Determines if the specified character is a control character.
     * A control character is any character less then 32 or greater than
     * 126 (decimal).
     *
     * @param character the character to test
     * @return true if it is a control character.
     */
    public static boolean isControlChar( char character ) {
		return ( character < 32 || character > 126 );
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

    /**
     * The index of the row the cursor is on, if we are
     * in cursor mode.   Otherwise, return the value of ShellChannel.getCursorRow().
     *
     * @return the row index of the cursor.
     */
    public int getCursorRow() {
//		if ( cursorMode ) {
//			return cursorRow;
//		} else {
//			return super.getCursorRow();
//		}

		return cursorMode ? cursorRow : super.getCursorRow();
	}

    /**
     * The index of the column the cursor is on, if we are
     * in cursor mode.  Otherwise, return the value of ShellChannel.getCursorColumn().
     *
     * @return the column index of the cursor.
     */
    public int getCursorColumn() {
//		if ( cursorMode ) {
//			return cursorColumn;
//		} else {
//			return super.getCursorColumn();
//		}

		return cursorMode ? cursorColumn : super.getCursorColumn();
	}

    //***************************************************************
    // Public Methods
    //***************************************************************

    /**
     * Parses the data read from the server for control characters.
     *
     * @param inputBuffer the data read from the server.
     * @param count the number of bytes in the inputBuffer that are valid.
     * @return a string containing only valid characters.
     */
    public String process( char[] inputBuffer, int count ) {
        StringBuffer output = new StringBuffer();
//		char currentCharacter;

        // Process each character
//		for ( int index = 0; index < count; index++ ) {
//			char currentCharacter = inputBuffer[index];

        for ( char currentCharacter : inputBuffer ) {
			// Last character was escape.
			if ( inEscapeSequence ) {
                if ( currentCharacter == ESC_CTRL ) {
                    inEscapeSequence = false;
                    inControlSequence = true;
                    controlSequenceSize = 0;
                } else if ( currentCharacter == CTRL_ESC ) {
                    log.debug( "Received back-to-back escape characters!" );
                } else if ( escapeSequenceComplete( currentCharacter ) ) {// Check to see if the escape sequence is complete.
					inEscapeSequence = false;
                } else {// We received an escape code we can't handle.  Ignore it.
					inEscapeSequence = false;
                    log.debug( "Unknown Escape Code received: " + (int) currentCharacter );
                }
            } else if ( inControlSequence ) {// Already in a control sequence.
                if ( currentCharacter == CTRL_CAN || currentCharacter == CTRL_SUB ) {// Abort the control sequence
                    inControlSequence = false;
                    controlSequenceSize = 0;
                }  else if ( currentCharacter == CTRL_ESC ) {// Abort current sequence and start a new one
                    inEscapeSequence = true;
                    inControlSequence = false;
                    controlSequenceSize = 0;
                } else if ( sequenceComplete( currentCharacter ) ) {// The sequence is complete, process it.
                    inControlSequence = false;
                    controlSequenceSize = 0;
                } else {// Add the current character to the sequence.
                    controlSequence[controlSequenceSize++] = currentCharacter;
                }
            } else {// Not in a control sequence.
                if ( currentCharacter == CTRL_ESC ) {// Escape character starts an escape sequence.
                    inEscapeSequence = true;
                } else if ( controlCharacter( currentCharacter ) ) {// Check for control characters.  All processing is done in the method call.
                    // Handling is done in controlCharacter method
                } else {// Otherwise, it is a valid printable character.
                    if ( cursorMode ) { // If we are in cursorMode, add it to our local copy.
                        screen[cursorRow][cursorColumn] = currentCharacter;
                        cursorColumn++;

                        // Handle 'off the screen'.
                        if ( cursorColumn >= screenWidth ) {
                            cursorColumn = 0;
                            cursorRow++;
                        }
                    } else {// Only return it if we are not keeping out own copy.
                        output.append( currentCharacter );
                    }
                }
            }
        }

		return output.toString();
    }

    /**
     * Returns a String array of the currently visible
     * rows.  The number of rows returned will always match
     * the Screen Size.
     *
     * @return Array of Strings that represent the current data on the screen.
     */
    public String[] getScreen() {
        if ( !cursorMode ) {// If we are not in cursorMode, just display the default screen.
            return super.getScreen();
        } else {// Otherwise, display our screen version.
            String[] output = new String[screenHeight];

			for( int index = 0; index < screenHeight; index++ ) {
                output[index] = String.valueOf( screen[index] );
            }

            return output;
        }
    }

    //***************************************************************
    // Private Helper Methods
    //***************************************************************

    /**
     * Check to see if the current character is a control character.
     * If so, handle it and return true.  Otherwise, return false.
     *
     * @param currentCharacter the character to handle.
     * @return true if it is a control character.
     */
    private boolean controlCharacter( char currentCharacter ) {
        boolean returnValue = false;

        if ( cursorMode ) { // Process the character according to mode.
            returnValue = true;

			if ( currentCharacter == CTRL_BS ) {
                moveCursor( CURSOR_BACK, 1 );
            } else if( currentCharacter == CTRL_NL ) {
                moveCursor( CURSOR_DOWN, 1 );
            } else if( currentCharacter == CTRL_CR ) {
                cursorColumn = 0;
            } else if( isControlChar( currentCharacter ) ) {
                if( log.isDebugEnabled() ) log.debug( "Ignoring unknown control character: " + (int) currentCharacter + " in cursorMode." );
            } else {
                returnValue = false;
            }
        } else {// Treat CR and NL as normal characters in default mode.
            if ( isControlChar( currentCharacter ) && currentCharacter != CTRL_NL && currentCharacter != CTRL_CR ) {
                if ( log.isDebugEnabled() ) {
					log.debug( "Ignoring unknown control character: " + (int) currentCharacter + " in normal mode." );
				}

				returnValue = true;
            }
        }

		return returnValue;
    }

    /**
     * Checks to see if this character ends a control sequence.
     * If so, execute the sequence if it is recognized.
     *
     * @param currentCharacter the character to check.
     * @return true if the sequence is complete.
     */
    private boolean sequenceComplete( char currentCharacter ) {
        boolean returnValue = true;

        if ( currentCharacter == TERM_CUU ) {
            moveCursor( CURSOR_UP, getCursorArgument() );
        } else if ( currentCharacter == TERM_CUD ) {
            moveCursor( CURSOR_DOWN, getCursorArgument() );
        } else if ( currentCharacter == TERM_CUF ) {
            moveCursor( CURSOR_FORWARD, getCursorArgument() );
        } else if ( currentCharacter == TERM_CUB ) {
            moveCursor( CURSOR_BACK, getCursorArgument() );
        } else if ( currentCharacter == TERM_CUP || currentCharacter == TERM_CUP_2 ) {
            int[] cursorPosition = getCursorArguments();
            cursorRow = cursorPosition[0];
            cursorColumn = cursorPosition[1];
            log.debug( "Moved cursor to row:column" + cursorRow + ":" + cursorColumn );
        } else if ( currentCharacter == TERM_RLF ) {
//			log.debug( "Received Reverse Line Feed Sequence: " + String.valueOf( controlSequence, 0, controlSequenceSize ) );
			log.debug( "Received Reverse Line Feed Sequence: " + new String( controlSequence, 0, controlSequenceSize ) );
		} else if ( currentCharacter == TERM_EES ) {
            if ( cursorRow == 0 && cursorColumn == 0 ) {// If we are 'at home', just wack the entire screen.
                clearScreen();
                log.debug( "Cleared entire screen." );
            } else {// Otherwise, wack the rest of this row and all of the other rows.
                boolean first = true;

				for ( int rowIndex = cursorRow; rowIndex < screenHeight; rowIndex++ ) {
                    if ( first ) {
                        first = false;

						for( int columnIndex = cursorColumn; columnIndex < screenWidth; columnIndex++ ) {
                            screen[cursorRow][columnIndex] = ' ';
                        }
                    } else {
                        clearLine( rowIndex );
                    }
                }

				if ( log.isDebugEnabled() ) {
					log.debug( "Cleared screen from row:column" + cursorRow + ":" + cursorColumn );
				}
            }
        } else if ( currentCharacter == TERM_EEL ) {
            clearLine( cursorRow, cursorColumn );

			if ( log.isDebugEnabled() ) {
				log.debug( "Cleared row from row:column" + cursorRow + ":" + cursorColumn );
			}
        } else if ( currentCharacter == TERM_MODE ) {
//			String mode = String.valueOf( controlSequence, 0, controlSequenceSize );
			String mode = new String( controlSequence, 0, controlSequenceSize );

			if( "?1".equals( mode ) ) {
                cursorMode = true;
                log.debug( "Entering cursor mode." );
            } else {
                log.warn( "Unknown mode requested: " + mode );
            }
        } else if ( currentCharacter == TERM_MODE_RESET ) {
//			String mode = String.valueOf( controlSequence, 0, controlSequenceSize );
			String mode = new String( controlSequence, 0, controlSequenceSize );

			if ( "?1".equals( mode ) ) {
                cursorMode = false;
                log.debug( "Exiting cursor mode." );
            } else {
                log.warn( "Unknown mode requested: " + mode );
            }
        } else if ( currentCharacter == TERM_SGR ) {
//			log.warn( "SGR Requested but ignored: " + String.valueOf( controlSequence, 0, controlSequenceSize ) );
			log.warn( "SGR Requested but ignored: " + new String( controlSequence, 0, controlSequenceSize ) );
		} else {
            returnValue = false;
        }

        return returnValue;
    }

    /**
     * Checks for valid escape sequences.
     *
     * @param currentCharacter the character to test.
     * @return true if we recognized it.
     */
    private boolean escapeSequenceComplete( char currentCharacter ) {
        if ( currentCharacter == ESC_ALT_KEYPAD_APPLICATION ) {
            log.debug( "Application Keypad Mode Enabled." );
            return true;
        } else if( currentCharacter == ESC_ALT_KEYPAD_NUMERIC ) {
            log.debug( "Numeric Keypad Mode Enabled." );
            return true;
        } else {
			return false;
		}
    }

    /**
     * Moves the cursor a certain number of rows or columns.
     *
     * @param mode The way to move the cursor.
     * @param amount the length to move the cursor.
     */
    private void moveCursor( int mode, int amount ) {
        switch( mode ) {
            case CURSOR_UP:
                cursorRow = cursorRow - amount;
                validateRow();

				if ( log.isDebugEnabled() ) {
					log.debug( "Moved cursor up " + amount + " rows." );
				}
                break;
            case CURSOR_DOWN:
                cursorRow = cursorRow + amount;
                validateRow();

				if ( log.isDebugEnabled() ) {
					log.debug( "Moved cursor down " + amount + " rows." );
				}
                break;
            case CURSOR_FORWARD:
                cursorColumn = cursorColumn + amount;
                validateColumn();

				if ( log.isDebugEnabled() ) {
					log.debug( "Moved cursor forward " + amount + " columns." );
				}
                break;
            case CURSOR_BACK:
                cursorColumn = cursorColumn - amount;
                validateColumn();

				if ( log.isDebugEnabled() ) {
					log.debug( "Moved cursor back " + amount + " columns." );
				}
                break;
            default:
                log.error( "Invalid mode argument in move cursor! " + mode );
                break;
        }
    }

    /**
     * Parses the command sequence for the number of
     * row or column to move the character.
     *
     * @return parsed number of rows.
     */
    private int getCursorArgument() {
//		String argumentString = String.valueOf( controlSequence, 0, controlSequenceSize );
		String argumentString = new String(controlSequence, 0, controlSequenceSize);

//		if ( argumentString == null || argumentString.length() == 0 ) {
		if ( argumentString.length() == 0 ) {
			log.debug( "Empty cursor argument, defaulting to 1.");
			return 1;
		} else {
			int argument = Integer.parseInt( argumentString );

			if ( log.isDebugEnabled() ) {
				log.debug( "Cursor argument parsed as: " + argument );
			}

			return argument;
		}
	}

    /**
     * Parses the command sequence for the number row and column
     * position to move the cursor to.
     *
     * @return Array of size 2. 0 - row index, 1 - column index.
     */
    private int[] getCursorArguments() {
        int[] position = new int[2];

//		String argumentString = String.valueOf( controlSequence, 0, controlSequenceSize );
		String argumentString = new String(controlSequence, 0, controlSequenceSize);
		int index = argumentString.indexOf( ';' );

        if (argumentString.length() == 0 || index == -1) {
            log.debug( "Empty cursor argument, defaulting to 1,1 (0,0).");
            position[0] = 0;
            position[1] = 0;
        } else {
            String[] arguments = argumentString.split( ";" );
            String rowString = arguments[0];
            String columnString = ( arguments.length == 2 ) ? arguments[1] : "";

            if ( rowString == null || rowString.length() == 0 ) {
                position[0] = 0;
            } else {
                position[0] = Integer.parseInt( rowString ) - 1;
            }

            if ( columnString == null || columnString.length() == 0 ) {
                position[1] = 0;
            } else {
                position[1] = Integer.parseInt( columnString ) - 1;
            }

			if ( log.isDebugEnabled() ) {
				log.debug( "Cursor argument parsed as: " + position[0] + ":" + position[1] );
			}
        }

        return position;
    }

    /**
     * Validates that the cursor is currently on the screen and moves
     * it back to the first or last row if it is not.
     */
    private void validateRow() {
        if ( cursorRow >= screenHeight ) {
            log.warn( "Cursor moved off screen!" );
            cursorRow = screenHeight - 1;
        } else if ( cursorRow < 0 ) {
            log.warn( "Cursor moved off screen!" );
            cursorRow = 0;
        }
    }

    /**
     * Validates that the cursor is currently on the screen and moves
     * it back to the first or last column if it is not.
     */
    private void validateColumn() {
        if ( cursorColumn >= screenWidth ) {
            log.warn( "Cursor moved off screen!" );
            cursorColumn = screenWidth - 1;
        } else if ( cursorColumn < 0 ) {
            log.warn( "Cursor moved off screen!" );
            cursorColumn = 0;
        }
    }

    /** Clear the entire screen */
    private void clearScreen() {
		for ( int index = 0; index < screenHeight; index++ ) {
			clearLine( index );
		}
    }

    /**
     * Clears an individual line.
     *
     * @param line the line to clear.
     */
    private void clearLine( int line ) {
		clearLine( line, 0 );
    }

    /**
     * Clears a line (row) starting with the specified index.
	 *
     * @param line an individual line.
     * @param startIndex start index.
     */
    private void clearLine( int line, int startIndex ) {
		for ( int index = startIndex; index < screenWidth; index++ ) {
			screen[line][index] = ' ';
		}
	}

}