package com.ah.be.admin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.jcraft.jsch.JSchException;

import com.ah.be.common.AhDirTools;
import com.ah.be.communication.event.BeHostIdentificationKeyEvent;
import com.ah.be.protocol.ssh.key.AhSshKeyGenerator;
import com.ah.be.protocol.ssh.key.AhSshKeyGenerator.SshKeyType;
import com.ah.be.protocol.ssh.key.AhSshKeyGenerator.SshPublicKeyFormatType;
import com.ah.util.Tracer;

/**
 * Utility to generate SSH keys without any dependencies instead of using ssh-keygen tool only in Linux shell.
 * We may use this class to generate any kind of SSH keys in different platforms including Windows.
 * So far, we just use it under the deployment of Windows. Another implementation <tt>KeyManager</tt> using ssh-keygen
 * tool as a subassembly supplied by OpenSSH is used under the deployment of Linux.
 */
public class AhSshKeyMgmtImpl implements AhSshKeyMgmt {

	private static final Tracer log = new Tracer( AhSshKeyMgmtImpl.class.getSimpleName() );

	/** The path of host DSA public key */
	private final String AH_SSH_HOST_DSA_PUBLIC_KEY;

	/** The path of host RSA public key */
	private final String AH_SSH_HOST_RSA_PUBLIC_KEY;

	/** The path of authorized_keys */
	private final String AH_SSH_AUTH_KEYS;

	/** The path of authentication private key */
	private String authPrivateKeyPath;

	/** The content of authentication private key */
	private String authPrivateKeyContent;

	/** The content of host public key */
	private String hostPublicKeyContent;

	/** The type of SSH key */
	private SshKeyType keyType = SshKeyType.DSA;

	public AhSshKeyMgmtImpl() {
		String sshHostKeysHome = System.getenv( "SSH_HOST_KEYS_HOME" );

		if ( sshHostKeysHome == null ) {
			log.warning( "AhSshKeyMgmtImpl", "The environment variable 'SSH_HOST_KEYS_HOME' could not be found, " +
					"regarding current path as the repository of SSH Host keys." );
			sshHostKeysHome = "";
		}

		if ( !sshHostKeysHome.endsWith( File.separator ) ) {
			sshHostKeysHome += File.separator;
		}

		AH_SSH_HOST_DSA_PUBLIC_KEY = sshHostKeysHome + "ssh_host_dsa_key.pub";
		AH_SSH_HOST_RSA_PUBLIC_KEY = sshHostKeysHome + "ssh_host_rsa_key.pub";

		// Your ~/.ssh repository which is used to store the 'authorized_keys'.
		// E.g. C:\Documents and Settings\Administrator\Start Menu\Administrator\.ssh
		String sshAuthKeysHome = System.getenv( "SSH_AUTH_KEYS_HOME" );

		if ( sshAuthKeysHome == null ) {
			log.warning( "AhSshKeyMgmtImpl", "The environment variable 'SSH_AUTH_KEYS_HOME' could not be found, " +
					"using current path as output path for new SSH Authentication keys to be generated." );
			sshAuthKeysHome = "";
		}

		if ( !sshAuthKeysHome.isEmpty() && !sshAuthKeysHome.endsWith( File.separator ) ) {
			sshAuthKeysHome += File.separator;
		}

		AH_SSH_AUTH_KEYS = sshAuthKeysHome + "authorized_keys";
	}

	public boolean generateKeys( String type ) {
		// Initialize key contents to reload them later.
		initKeyContents();

		int bits;
		authPrivateKeyPath = AhDirTools.getSshKeyDir();

		if ( SshKeyType.DSA.toString().equalsIgnoreCase( type ) ) {
			keyType = SshKeyType.DSA;
			bits = 1024;
			authPrivateKeyPath += "id_dsa";
		} else {
			keyType = SshKeyType.RSA;
			bits = 2048;
			authPrivateKeyPath += "id_rsa";
		}

		AhSshKeyGenerator keyGen = new AhSshKeyGenerator( keyType, bits, SshPublicKeyFormatType.OPENSSH );

		try {
			// Generate SSH key pair without passphrase.
			keyGen.generateKeyPair( "", authPrivateKeyPath );
			AhSshKeyGenerator.mergePublicKey( keyGen.getPublicKeyName(), AH_SSH_AUTH_KEYS );
		} catch ( IOException ioe ) {
			log.error( "generatekeys", "Failed to generate SSH key pair.", ioe );
			return false;
		} catch ( JSchException jse ) {
			log.error( "generatekeys", "Failed to generate SSH key pair.", jse );
			return false;
		}

		return true;
	}

	public Map<Byte, String> getKeys() {
		// Currently we don't have to generate the host identification keys every time in general, so it is better to
		// use the default host public key for HiveAP to identify HM. We just need to check the existence of the
		// authentication private key here.
		if ( authPrivateKeyPath == null || !new File( authPrivateKeyPath ).exists() ) {
			// Automatically generate the authentication keys with DSA algorithm by default if the specified key doesn't
			// exist.
			boolean keyGen = generateKeys( "DSA" );

			if ( !keyGen ) {
				return null;
			}

			// Initialize key contents to reload them then.
			initKeyContents();
		}

		if ( hostPublicKeyContent == null || hostPublicKeyContent.isEmpty() ||
			 authPrivateKeyContent == null || authPrivateKeyContent.isEmpty() ) {
			try {
				readKeys();
			} catch ( Exception e ) {
				log.error( "getKeys", "Failed to read contents from SSH key files.", e );
				return null;
			}
		}

		if ( hostPublicKeyContent == null || hostPublicKeyContent.isEmpty() ||
			 authPrivateKeyContent == null || authPrivateKeyContent.isEmpty() ) {
			return null;
		}

		Map<Byte, String> sshKeys = new HashMap<Byte, String>( 2 );
		sshKeys.put( BeHostIdentificationKeyEvent.KEYTYPE_PUBLICKEY, hostPublicKeyContent );
		sshKeys.put( BeHostIdentificationKeyEvent.KEYTYPE_PRIVATEKEY, authPrivateKeyContent );

		return sshKeys;
	}

	private void initKeyContents() {
		hostPublicKeyContent = null;
		authPrivateKeyContent = null;
	}

	private void readKeys() throws IOException {
		String hostPublicKeyPath;

		switch ( keyType ) {
			case RSA:
				hostPublicKeyPath = AH_SSH_HOST_RSA_PUBLIC_KEY;
				break;
			case DSA:
			default:
				hostPublicKeyPath = AH_SSH_HOST_DSA_PUBLIC_KEY;
				break;
		}

		hostPublicKeyContent = readFile( hostPublicKeyPath );
		authPrivateKeyContent = readFile( authPrivateKeyPath );
	}

	private String readFile( String filePath ) throws IOException {
		StringBuilder buf = new StringBuilder();
		FileInputStream in = null;

		try {
			in = new FileInputStream( filePath );
			byte[] inputBuf = new byte[1024];
			int len;

			while ( ( len = in.read( inputBuf ) ) != -1 ) {
				buf.append( new String( inputBuf, 0, len ) );
			}
		} finally {
			if ( in != null ) {
				in.close();
			}
		}

		return buf.toString();
	}

}