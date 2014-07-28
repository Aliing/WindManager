package com.ah.util.coder;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

import com.ah.util.Tracer;

public class AhCodePrinter {

	private static final Tracer log = new Tracer(AhCodePrinter.class.getSimpleName());

	public static String printHexString( byte[] bb ) {
		return printHexString( bb, log );
	}

    /*-
     * We want the dump to look something like this:
     *
     * 0000: xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx >abcdefghiklmnopq<
     * 0010: xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx xx >abcdefghiklmnopq<
     * 0020: xx xx                                           >ab              <
     *
     * where "xx" is the hex value of the octet, and the printable character
     * value is printed between ">" and "<". Non-printing chars will display
     * a "." (period).
     */
	public static String printHexString( byte[] bb, Tracer logger ) {
   	    int len = bb.length;
		int offset = 0;
		int lineMax = 16;
		byte[] buf = bb;

		StringBuilder printBuf = new StringBuilder( "\n" );

		while ( len > 0 ) {
			char[] charDump = new char[lineMax];
			StringBuilder hexDumpBuf = new StringBuilder();

			/* print line offset */
			hexDumpBuf.append( String.format( "%04x  ", offset ) );
			offset += lineMax;

			/* output one line at a time */
			for ( int i = 0; i < lineMax; i++ ) {
				if ( i == len ) {
					/* partial line - print rest of line */
					for ( int j = lineMax - 1; j >= len; j-- )	{
						hexDumpBuf.append( "   " );
						charDump[j] = ' ';
					}

					break;
				} else {
					hexDumpBuf.append( String.format( "%02x ", buf[i] ) );

					if ( isPrintChar( buf[i] ) ) {
						charDump[i] = (char) buf[i];
					} else {
						charDump[i] = '.';
					}
				}
			} /* end for ( i = 0; i < lineMax; i++ ) */

			/* copy hex codes and ascii chars into print buffer with one line */
			copy( printBuf, hexDumpBuf, charDump );

			/* update buffer length, advance pointer */
			len -= lineMax;

			if ( len > 0 ) {
				byte[] tempBuf = new byte[len];
				System.arraycopy( buf, lineMax, tempBuf, 0, len );
				buf = tempBuf;
			}
		} /* end while */
		
		String hexString = printBuf.toString();

		/* print total lines at a time */
		logger.debug( "printHexString", hexString );
		
		return hexString;
	}

	public static String printHexString( ByteBuffer bb ) {
		return printHexString( bb, log );
	}

	/**
	 * It is much more efficient using this method than invoking <tt>printHexString(byte[] bb)</tt>
	 *
	 * @param bb A buffer of bytes to be printed.
	 * @param logger log file in which the buffered messages will be printed.
	 * @return a hex string.
	 */
	public static String printHexString( ByteBuffer bb, Tracer logger ) {
		int offset = 0;
		int lineMax = 16;

		StringBuilder printBuf = new StringBuilder( "\n" );

		while ( bb.hasRemaining() ) {
			CharBuffer charDumpBuf = CharBuffer.allocate( lineMax );
			StringBuilder hexDumpBuf = new StringBuilder();

			/* print line offset */
			hexDumpBuf.append( String.format( "%04x  ", offset ) );
			offset += lineMax;

			/* output one line at a time */
			for ( int i = 0; i < lineMax; i++ ) {
				if ( !bb.hasRemaining() ) {
					/* partial line - print rest of line */
					while ( charDumpBuf.hasRemaining() ) {
						hexDumpBuf.append( "   " );
						charDumpBuf.append( ' ' );
					}

					break;
				} else {
					byte b = bb.get();
					hexDumpBuf.append( String.format( "%02x ", b ) );

					if ( isPrintChar( b ) ) {
						charDumpBuf.append( (char) b );
					} else {
						charDumpBuf.append( '.' );
					}
				}
			} /* end for ( i = 0; i < lineMax; i++ ) */

			/* copy hex codes and ascii chars into print buffer with one line */
			copy( printBuf, hexDumpBuf, charDumpBuf );
		} /* end while */

		String hexString = printBuf.toString();

		/* print total lines at a time */
		logger.debug( "printHexString", hexString );

		return hexString;
	}

	private static void copy( StringBuilder printBuf, StringBuilder hexDumpBuf, char[] charDump ) {
		printBuf.append( hexDumpBuf ).append( "   " ).append( charDump ).append( "\n" );
	}

	private static void copy( StringBuilder printBuf, StringBuilder hexDumpBuf, CharBuffer charDumpBuf ) {
		char[] charDump;

		if ( charDumpBuf.hasArray() ) {
			charDump = charDumpBuf.array();
		} else {
			charDumpBuf.flip();
			charDump = new char[charDumpBuf.limit()];
			charDumpBuf.get( charDump );
		}

		copy( printBuf, hexDumpBuf, charDump );
	}

	private static boolean isPrintChar(byte b) {
		return b > 0X20 && b < 0x7F;
	}

	/*-
	public static void main(String[] args) {
		byte[] buf = new byte[33];

		for (int i = 0 ; i < 33; i++) {
			buf[i] = (byte) (100 + i);
		}

		ByteBuffer bb = ByteBuffer.allocate(33);
		bb.put(buf);
		bb.flip();

		long l1 = System.currentTimeMillis();
		printHexString(buf);
		long l2 = System.currentTimeMillis();

		System.out.println("Cost: " + (l2 - l1) + " ms.\n");

		long l3 = System.currentTimeMillis();
		printHexString(bb);
		long l4 = System.currentTimeMillis();

		System.out.println("Cost: " + (l4 - l3) + " ms.");
	}*/

}