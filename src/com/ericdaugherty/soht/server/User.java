/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/server/User.java,v $
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Represents a user of the system.  Provides helper methods
 * to handle validation of passwords and mapping to the
 * properties file.
 *
 * @author Eric Daugherty
 */
public class User {

    //***************************************************************
    // Variables
    //***************************************************************

    /** Handles the logging of messages */
    private static final Logger log = Logger.getLogger( User.class );


	/**
	 * The SHA message digest provider.
	 */
	protected static final MessageDigest shaHelper;

	/**
	 * Initialize the digest provider with the algorithm of 'SHA'.
	 */
	static {
        try {
			shaHelper = MessageDigest.getInstance( "SHA" );
        } catch ( NoSuchAlgorithmException e ) {
            throw new ExceptionInInitializerError( e );
        }
	}

    private String userName;
    private String password;

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Creates a new user.  All parameters are validated.
     * <ul>
     *   <li>The username must not be null or an empty string.</li>
     *   <li>The passwords must not be null or empty strings.</li>
     *   <li>The passwords must be equal</li>
     * </ul>
     *
     * @param userName the requested username.
     * @param password plain text password.
     * @param password2 plain text password.
     * @throws UIException contains a user friendly error message if validation fails.
     */
    public User( String userName, String password, String password2 ) throws UIException {
        if ( userName == null || userName.length() < 1 ) {
            throw new UIException( "Please specify a UserName!" );
        }

        this.userName = userName;
        setPassword( password, password2 );
    }

    /**
     * Creates a new User using the specified username and encrypted password.
     *
     * @param userName username
     * @param password encrypted password.
     */
    public User( String userName, String password ) {
        this.userName = userName;
        this.password = password;
    }

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

    /**
     * Returns the username.
     *
     * @return username.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Returns the encrypted password for this user.
     *
     * @return encrypted password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Validates the plain text passwords match and are at
     * least 6 characters long.
     *
     * @param password plain text password
     * @param password2 plain text password
     * @throws UIException contains a user friendly error message if validation fails.
     */
    public void setPassword( String password, String password2 ) throws UIException {
        validatePasswords( password,  password2 );

        password = encryptPassword( password );

        if ( password == null ) {
            throw new UIException( "Error in encrypting password." );
        }

        this.password = password;
    }

    //***************************************************************
    // Public Helper Methods
    //***************************************************************

    /**
     * Checks to see if the specified password matches this user's password.
     *
     * @param password plain text password
     * @return true if the password matches the user's password.
     */
    public boolean isPasswordValid( String password ) {
        if ( password == null ) {
            return false;
        }

        String encryptedPassword = encryptPassword( password );

        if ( encryptedPassword == null ) {
            log.error( "Error in encrypting password for user: " + userName );
            return false;
        }

        return encryptedPassword.equals( this.password );
    }

    //***************************************************************
    // Static Helper Methods
    //***************************************************************

    /**
     * Returns a List of User instances loaded from the
     * specified properties file.
     *
     * @param properties the properties from which the users will be loaded.
     * @return a List of User instances.
     */
    public static List<User> loadUsers( Properties properties ) {
        List<User> users = new ArrayList<User>();
        Enumeration<?> propertyNames = properties.propertyNames();

        while ( propertyNames.hasMoreElements() ) {
            String propertyName = (String) propertyNames.nextElement();

            if ( propertyName.startsWith( "user." ) ) {
                String userName = propertyName.substring( 5 );
                String password = properties.getProperty( propertyName );
                users.add( new User( userName, password ) );
            }
        }

        return users;
    }

    /**
     * Sets the user into the specified properties.
     *
     * @param user user to store
     * @param properties properties to modify
     */
    public static void setUser( User user, Properties properties ) {
        properties.setProperty( "user." + user.getUserName(), user.getPassword() );
    }

    /**
     * Returns the user for the specified username.  Returns null
     * if the user does not exist.
     *
     * @param userName the username of the user to load.
     * @param properties the properties file to load the user from.
     * @return the loaded User, or null if the userName does not exist.
     */
    public static User getUser( String userName, Properties properties ) {
        String password = properties.getProperty( "user." + userName );

        if ( password != null ) {
            return new User( userName, password );
        }

        return null;
    }

    /**
     * Validates that the plain text passwords match and are at
     * least 6 characters long.
     *
     * @param password plain text password
     * @param password2 plain text password
     * @throws UIException contains a user friendly error message if validation fails.
     */
    public static void validatePasswords( String password, String password2 ) throws UIException {
        if ( password == null || password2 == null ) {
            throw new UIException( "Invalid (null) parameter." );
        }

        if ( !password.equals( password2) ) {
            throw new UIException( "Passwords do not match!" );
        }

        if ( password.length() < 6 ) {
            throw new UIException( "Password must be at least 6 characters" );
        }
    }

    /**
     * Creates a one-way has of the specified password.  This allows passwords to be
     * safely stored without an easy way to retrieve the original value.
     *
     * @param password the string to encrypt.
     * @return the encrypted password, or null if encryption failed.
     */
    public static String encryptPassword( String password ) {
		/*-
		try {
			MessageDigest md = MessageDigest.getInstance( "SHA" );

			//Create the encrypted Byte[]
			md.update( password.getBytes() );
			byte[] hash = md.digest();

			//Convert the byte array into a String

			StringBuilder hashStringBuf = new StringBuilder();
			String byteString;
			int byteLength;

			for ( int index = 0; index < hash.length; index++ ) {

				byteString = String.valueOf( hash[index ] + 128 );

				//Pad string to 3.  Otherwise hash may not be unique.
				byteLength = byteString.length();
				switch ( byteLength ) {
				case 1:
					byteString = "00" + byteString;
					break;
				case 2:
					byteString = "0" + byteString;
					break;
				}
				hashStringBuf.append( byteString );
			}

			return hashStringBuf.toString();
		}
		catch ( NoSuchAlgorithmException nsae ) {
			log.error( "Error in getting password hash - " + nsae.getMessage() );
			return null;
		}*/

		shaHelper.update( password.getBytes() );
		byte[] hash = shaHelper.digest();

		// Convert the byte array into a String
		StringBuilder hashStringBuf = new StringBuilder();

		for ( byte hashByte : hash ) {
			String byteString = String.valueOf( hashByte + 128 );

			// Pad string to 3.  Otherwise hash may not be unique.
			int byteLength = byteString.length();

			switch ( byteLength ) {
				case 1:
					byteString = "00" + byteString;
					break;
				case 2:
					byteString = "0" + byteString;
					break;
			}

			hashStringBuf.append( byteString );
		}

		return hashStringBuf.toString();
    }

}