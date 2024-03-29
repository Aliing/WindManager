/******************************************************************************
 * $Source: /aerohive/aerohm/hivemanager/src/com/ericdaugherty/soht/client/configuration/ConfigurationManager.java,v $
 * $Revision: 1.5 $
 * $Author: ychen $
 * $Date: 2010/08/17 07:57:22 $
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

package com.ericdaugherty.soht.client.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

//import org.apache.log4j.PropertyConfigurator;

//import sun.misc.BASE64Encoder;

/**
 * Handles the configuration data for the SOHT Proxy client.
 *
 * @author Eric Daugherty
 */
public class ConfigurationManager {

    //***************************************************************
    // Constants
    //***************************************************************

//	public static final int MODE_STATEFUL = 0;
//	public static final int MODE_STATELESS = 1;

    //***************************************************************
    // Variables
    //***************************************************************

    private Properties properties;
    private String propertiesFile;

    private String serverURL;
    private boolean serverLoginRequired;
    private String serverUsername;
    private String serverPassword;
    private boolean useStatelessConnection;

    private boolean useHTTPProxy;
    private String proxyHost;
    private String proxyPort;
    private String proxyLogin;
    private String proxyPassword = "";

    private boolean socksServerEnabled;
    private int socksServerPort;
    
    private List<Host> hosts;

	static {
		// from : http://javaalmanac.com/egs/javax.net.ssl/TrustAll.html
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {

					@Override
		  			public X509Certificate[] getAcceptedIssuers() {
						return null;
					}

					@Override
					public void checkClientTrusted( X509Certificate[] certs, String authType ) throws CertificateException {

					}

					@Override
					public void checkServerTrusted( X509Certificate[] certs, String authType ) throws CertificateException {

					}
				}
		};

		// Install the all-trusting trust manager
		try {
			SSLContext context = SSLContext.getInstance( "SSL" );
			context.init( null, trustAllCerts, new SecureRandom() );
			HttpsURLConnection.setDefaultSSLSocketFactory( context.getSocketFactory() );
		} catch ( Exception e ) {
			throw new ExceptionInInitializerError( e );
		}
	}

    //***************************************************************
    // Constructor
    //***************************************************************

    /**
     * Initializes a new ConfigurationManager instance to load/save
     * configuration information to/from the specified properties file.
     *
     * @param propertiesFile the path/filename to the soht properties file.
     * @throws ConfigurationException thrown if there is an error loading the file.
     */
    public ConfigurationManager( String propertiesFile ) throws ConfigurationException {
        this.propertiesFile = propertiesFile;
        loadProperties();

        initializeLogger();
    }

    public ConfigurationManager() {
	//	propertiesFile = "soht.properties";
		initializeLogger();
    }

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL( String serverURL ) {
        this.serverURL = serverURL;
    }

    public boolean isServerLoginRequired() {
        return serverLoginRequired;
    }

	/**
	 * @return Returns the socksServerPort.
	 */
	public int getSocksServerPort() {
		return socksServerPort;
	}
	
	/**
	 * @param socksServerPort The socksServerPort to set.
	 */
	public void setSocksServerPort( int socksServerPort ) {
		this.socksServerPort = socksServerPort;
	}
	
	/**
	 * @return Returns the socksServerEnabled.
	 */
	public boolean isSocksServerEnabled() {
		return socksServerEnabled;
	}
	
	/**
	 * @param socksServerEnabled The socksServerEnabled to set.
	 */
	public void setSocksServerEnabled( boolean socksServerEnabled ) {
		this.socksServerEnabled = socksServerEnabled;
	}
	
    public void setServerLoginRequired( boolean serverLoginRequired ) {
        this.serverLoginRequired = serverLoginRequired;
    }

    public String getServerUsername() {
        return serverUsername;
    }

    public void setServerUsername( String serverUsername ) {
        this.serverUsername = serverUsername;
    }

    public String getServerPassword() {
        return serverPassword;
    }

    public void setServerPassword( String serverPassword ) {
        this.serverPassword = serverPassword;
    }

    public boolean isUseStatelessConnection() {
        return useStatelessConnection;
    }

    public void setUseStatelessConnection( boolean useStatelessConnection ) {
        this.useStatelessConnection = useStatelessConnection;
    }

    public boolean isUseHTTPProxy() {
        return useHTTPProxy;
    }

    public void setUseHTTPProxy( boolean useHTTPProxy ) {
        this.useHTTPProxy = useHTTPProxy;
    }

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost( String proxyHost ) {
        this.proxyHost = proxyHost;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort( String proxyPort ) {
        this.proxyPort = proxyPort;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void setHosts( List<Host> hosts ) {
        this.hosts = hosts;
    }

    public String getProxyLogin() {
        return proxyLogin;
    }

    public void setProxyLogin( String proxyLogin ) {
        this.proxyLogin = proxyLogin;
    }

    public String getProxyPassword() {
        return proxyPassword;
    }

    public void setProxyPassword( String proxyPassword ) {
		if ( proxyPassword != null ) {
			 this.proxyPassword = proxyPassword;
		}
    }

    //***************************************************************
    // Public Helper Methods
    //***************************************************************

    /**
     * Initializes and configures a HttpURLConnection for use.  This includes
     * setting the server URL and any proxy configuration, if necessary.
     *
     * @return a configured HttpURLConnection
     * @throws IOException thrown if unable to connect to URL
     */
    public HttpURLConnection getURLConnection() throws IOException {
        String url_str = getServerURL();
        URL url = new URL( url_str );

        HttpURLConnection urlConnection;

		if ( url.getProtocol().equalsIgnoreCase( "https" ) ) {
			// https connection
			HttpsURLConnection urlSConnection;

			if ( useHTTPProxy ) {
				Proxy httpProxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( proxyHost, Integer.valueOf( proxyPort ) ) );
				urlSConnection = (HttpsURLConnection) url.openConnection( httpProxy );

				// If proxy login specified then sets basic proxy authorization
				if ( proxyLogin != null ) {
				//	String authString = proxyLogin + ":" + proxyPassword;
				//	String auth = "Basic " + new BASE64Encoder().encode( authString.getBytes() );
				//	urlConnection.setRequestProperty( "Proxy-Authorization", auth );

					// Authenticator for HTTP proxy authorization
					Authenticator.setDefault(
							new Authenticator() {
								@Override
								protected PasswordAuthentication getPasswordAuthentication() {
									return new PasswordAuthentication( proxyLogin, proxyPassword.toCharArray() );
								}

								@Override
								protected Authenticator.RequestorType getRequestorType() {
									return Authenticator.RequestorType.PROXY;
								}
							});
				}
			} else {
				urlSConnection = (HttpsURLConnection) url.openConnection();
			}

			// from : http://www.kickjava.com/?http://www.kickjava.com/1930.htm
			urlSConnection.setHostnameVerifier(
					new HostnameVerifier() {
						@Override
						public boolean verify( String hostname, SSLSession session ) {
							// I don't care if the certificate doesn't match host name.
							return true;
						}
					});
			urlConnection = urlSConnection;
		} else {
			// non-https connection
			urlConnection = (HttpURLConnection) url.openConnection();
		}

		urlConnection.setRequestMethod( "POST" );
		urlConnection.setDoOutput( true );

		return urlConnection;
	}

    //***************************************************************
    // Private Helper Methods
    //***************************************************************

    /**
     * Loads configuration information from the configured properties file.
     *
     * @throws ConfigurationException thrown if there is an error loading the file.
     */
    private void loadProperties() throws ConfigurationException {
        // Load the file.
        properties = new Properties();

        try {
            properties.load( new FileInputStream( propertiesFile ) );
        } catch ( IOException e ) {
            throw new ConfigurationException( "Unable to load configuration file: " + propertiesFile + " - " + e );
        }

        // Load server properties
        serverURL = getRequiredProperty( "server.url" );
        String serverLoginRequiredString = properties.getProperty( "server.loginrequired", "false" );
        serverLoginRequired = Boolean.valueOf( serverLoginRequiredString );

        // Load the server username/password if a login is required.
        // they are both required if a login is required
        if ( serverLoginRequired ) {
            serverUsername = getRequiredProperty( "server.username" );
            serverPassword = getRequiredProperty( "server.password" );
        }

        // Load the connection mode.
        String connectionMode = properties.getProperty( "server.stateless", "false" );
        useStatelessConnection = Boolean.valueOf( connectionMode );

        // Load HTTP Proxy
        String useProxyString = properties.getProperty( "proxy.useproxy", "false" );
        useHTTPProxy = Boolean.valueOf( useProxyString );

        if ( useHTTPProxy ) {
            proxyHost = getRequiredProperty( "proxy.host" );
            proxyPort = getRequiredProperty( "proxy.port" );

            // Optional proxy server login information.
            proxyLogin = properties.getProperty( "proxy.login" );
            proxyPassword = properties.getProperty( "proxy.password" );
        }

        // Load SOCKS server settings
        String socksServerEnabledString = properties.getProperty( "socks.server.enabled", "false" );
        socksServerEnabled = Boolean.valueOf( socksServerEnabledString );

        if ( socksServerEnabled ) {
        	String socksServerPortString = properties.getProperty( "socks.server.port", "1080" );
        	socksServerPort = Integer.parseInt( socksServerPortString );
        }
        
        // Load mappings
        hosts = new ArrayList<Host>();

        for ( Enumeration<?> propertyKeys = properties.keys(); propertyKeys.hasMoreElements(); ) {
            String keyName = (String) propertyKeys.nextElement();

            if ( keyName.startsWith( "port." ) ) {
                String localPort = keyName.substring( 5 );
                String keyValue = properties.getProperty( keyName );

                int delimiterIndex = keyValue.indexOf( ":" );

                if ( delimiterIndex == -1 ) {
                    throw new ConfigurationException( "Mapping for local port: " + localPort + " invalid.  Please specify value as <host>:<port>." );
                }
				
                String remoteHost = keyValue.substring( 0, delimiterIndex );
                String remotePort = keyValue.substring( delimiterIndex + 1 );

                hosts.add( new Host( localPort, remoteHost, remotePort ) );
            }
        }
    }

    /**
     * Loads the specified property, and throws a ConfigurationException
     * if the property does not exist.
     *
     * @param propertyName the property key to load.
     * @return the property value.  This will never be null.
     * @throws ConfigurationException thrown if the property is null.
     */
    private String getRequiredProperty( String propertyName ) throws ConfigurationException {
        String property = properties.getProperty( propertyName );

        if ( property == null ) {
            throw new ConfigurationException( "Missing required property: " + propertyName );
        }

        return property;
    }

    /**
     * Configure Logging framework.
     */
    private void initializeLogger() {
	//	PropertyConfigurator.configureAndWatch( propertiesFile );
    }

}