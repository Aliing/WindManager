package com.ah.be.protocol.ssh.key;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.sshtools.j2ssh.openssh.OpenSSHPrivateKeyFormat;
import com.sshtools.j2ssh.transport.publickey.OpenSSHPublicKeyFormat;
import com.sshtools.j2ssh.transport.publickey.SECSHPublicKeyFormat;
import com.sshtools.j2ssh.transport.publickey.SshKeyPair;
import com.sshtools.j2ssh.transport.publickey.SshKeyPairFactory;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKey;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFile;
import com.sshtools.j2ssh.transport.publickey.SshPrivateKeyFormat;
import com.sshtools.j2ssh.transport.publickey.SshPublicKey;
import com.sshtools.j2ssh.transport.publickey.SshPublicKeyFile;
import com.sshtools.j2ssh.transport.publickey.SshPublicKeyFormat;
import com.sshtools.j2ssh.transport.publickey.SshtoolsPrivateKeyFormat;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import com.ah.util.Tracer;

public class AhSshKeyGenerator {

	private static final Tracer log = new Tracer(AhSshKeyGenerator.class.getSimpleName());

	/** SSH Key Type */
	public enum SshKeyType {
	   DSA, RSA
	}

	/** SSH Public Key Format Type */
	public enum SshPublicKeyFormatType {
		OPENSSH, SECSH
	}

	/** SSH Private Key Format Type */
	public enum SshPrivateKeyFormatType {
		OPENSSH, SSHTOOLS
	}

	/** The number of bits in the key */
	private int bits = 1024;

	/** Which is made as the illumination appended to the end of the public key */
	private String comment = "";

	/** The type of key to create */
	private SshKeyType keyType = SshKeyType.DSA;

	/** The type of formatter to format the generated public key */
	private SshPublicKeyFormatType publicKeyFormatType = SshPublicKeyFormatType.OPENSSH;

	/** The type of formatter to format the generated private key */
	private SshPrivateKeyFormatType privateKeyFormatType = SshPrivateKeyFormatType.OPENSSH;

	/** The name of user for login */
	private String userName = "root";

	/** Which is used for generating the private key */
	private String passphrase = "";

	/** The name of the private key file to be created */
	private String privateKeyName = "/root/.ssh/id_dsa";

	/** The name of the public key file to be created */
	private String publicKeyName = "/root/.ssh/id_dsa.pub";


	public AhSshKeyGenerator() {
		super();
	}

	public AhSshKeyGenerator(SshKeyType keyType) {
		this.keyType = keyType;
	}

	public AhSshKeyGenerator(int bits) {
		this.bits = bits;
	}

	public AhSshKeyGenerator(SshKeyType keyType, int bits) {
		this.keyType = keyType;
		this.bits = bits;
	}

	public AhSshKeyGenerator(SshKeyType keyType, int bits, SshPublicKeyFormatType publicKeyFormatType) {
		this.keyType = keyType;
		this.bits = bits;
		this.publicKeyFormatType = publicKeyFormatType;
	}

	public AhSshKeyGenerator(SshKeyType keyType, int bits, SshPublicKeyFormatType publicKeyFormatType, SshPrivateKeyFormatType privateKeyFormatType) {
		this.keyType = keyType;
		this.bits = bits;
		this.publicKeyFormatType = publicKeyFormatType;
		this.privateKeyFormatType = privateKeyFormatType;
	}

	//***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public int getBits() {
		return bits;
	}

	public void setBits(int bits) {
		this.bits = bits;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public SshKeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(SshKeyType keyType) {
		this.keyType = keyType;
	}

	public SshPrivateKeyFormatType getPrivateKeyFormatType() {
		return privateKeyFormatType;
	}

	public void setPrivateKeyFormatType(SshPrivateKeyFormatType privateKeyFormatType) {
		this.privateKeyFormatType = privateKeyFormatType;
	}

	public SshPublicKeyFormatType getPublicKeyFormatType() {
		return publicKeyFormatType;
	}

	public void setPublicKeyFormatType(SshPublicKeyFormatType publicKeyFormatType) {
		this.publicKeyFormatType = publicKeyFormatType;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassphrase() {
		return passphrase;
	}

	public String getPrivateKeyName() {
		return privateKeyName;
	}

	public String getPublicKeyName() {
		return publicKeyName;
	}

	//***************************************************************
    // Public Methods
    //***************************************************************

	/**
	 * Generate a pair of SSH keys(public and private keys) with the specified user name, passphrase and file name.
	 *
	 * @param passphrase which is used to authenticate the client whilst it is logging in the SSH server.
	 * @param fileName specifies the name of the key file to be generated.
	 * @throws IOException if any problem occurs while generating SSH key pair.
	 * @throws JSchException if error occurs while generating SSH key pair with JSch implementation.
	 */
	public void generateKeyPair(String passphrase, String fileName) throws IOException, JSchException {
		int keyPairType;

		switch (keyType) {
			case DSA:
				keyPairType = KeyPair.DSA;
				break;
			case RSA:
				keyPairType = KeyPair.RSA;
				break;
			default:
				throw new AssertionError("Unsupported SSH Key Type: " + keyType.toString());
		}

		this.comment = bits + "-bit " + keyType.toString().toLowerCase();
		this.passphrase = passphrase;
		this.privateKeyName = fileName;
		this.publicKeyName = fileName + ".pub";

		log.info("generateKeyPair", "****SSH Key Pair Generator with JSch Implementation****");

		// Generate SSH key with specified key type.
		log.info("generateKeyPair", "Generating " + bits + " bit public/private " + keyType.toString().toLowerCase() + " key pair.");
		JSch jsch = new JSch();
		KeyPair kpair = KeyPair.genKeyPair(jsch, keyPairType, bits);

		// Set passphrase.
		kpair.setPassphrase(passphrase);

		// Create private key.
		log.info("generateKeyPair", "Creating identification " + privateKeyName + ".");
		kpair.writePrivateKey(privateKeyName);
		log.info("generateKeyPair", "Your identification has been saved in " + new File(privateKeyName).getAbsolutePath() + ".");

		// Create public key with specified key format.
		log.info("generateKeyPair", "Creating public key " + publicKeyName + ".");

		switch (publicKeyFormatType) {
			case OPENSSH:// OpenSSH-formatted Public Key.
				kpair.writePublicKey(publicKeyName, comment);
				break;
			case SECSH:// SECSH-formatted Public Key.
				kpair.writeSECSHPublicKey(publicKeyName, comment);
				break;
			default:
				throw new AssertionError("Unsupported SSH Public Key Format Type: " + publicKeyFormatType.toString());
		}

		log.info("generateKeyPair", "Your public key has been saved in " + new File(publicKeyName).getAbsolutePath() + ".");

		// Fingerprint.
		log.info("generateKeyPair", "The key fingerprint is: " + kpair.getFingerPrint() + ".");

		// Dispose key pair.
		kpair.dispose();

		log.info("generateKeyPair", "****SSH Key Pair Generation with JSch Implementation finished****");
	}

	/**
	 * Generate a pair of SSH keys(public and private keys) with the specified user name, passphrase and file name.
	 *
     * <blockquote><pre>
     *  generateKeyPair(passphrase, fileName)
     * </pre></blockquote>
     *
     * is equivalent to:
     *
     * <blockquote><pre>
     *  generateKeyPair(passphrase, fileName, "id_dsa")
     * </pre></blockquote>
	 *
	 * or
	 *
     * <blockquote><pre>
     *  generateKeyPair(passphrase, fileName, "id_rsa")
     * </pre></blockquote>
	 *
	 * based on the type of key used for generating the key pair.
	 *
	 * @param passphrase which is used to authenticate the client whilst it is logging in the SSH server.
	 * @throws IOException if any problem occurs while generating SSH key pair.
	 * @throws JSchException if error occurs while generating SSH key pair with JSch implementation.
	 */
	public void generateKeyPair(String passphrase) throws IOException, JSchException {
		String fileName;

		switch (keyType) {
			case DSA:
				fileName = "id_dsa";
				break;
			case RSA:
				fileName = "id_rsa";
				break;
			default:
				throw new AssertionError("Unsupported SSH Key Type: " + keyType.toString());
		}

		generateKeyPair(passphrase, fileName);
	}

	/**
	 * Generate a pair of SSH keys(public and private keys) with the specified user name, passphrase and file name.
	 *
	 * @param userName specifies the name of user for login.
	 * @param passphrase which is used to authenticate the client whilst it is logging in the SSH server.
	 * @param fileName specifies the name of the key file to be generated.
	 * @throws IOException if any problem occurs while generating the SSH key pair.
	 */
	public void generateKeyPair(String userName, String passphrase, String fileName) throws IOException {
		if (passphrase == null) {
			throw new IllegalArgumentException("Invalid passphrase: " + passphrase);
		}

		String algorithmName;

		switch (keyType) {
			case DSA:
				algorithmName = "ssh-dss";
				break;
			case RSA:
				algorithmName = "ssh-rsa";
				break;
			default:
				throw new AssertionError("Unsupported SSH Key Type: " + keyType.toString());
		}

		this.userName = userName;
		this.comment = bits + "-bit " + keyType.toString().toLowerCase();
		this.passphrase = passphrase;
		this.privateKeyName = fileName;
		this.publicKeyName = fileName + ".pub";

		log.info("generateKeyPair", "****SSH Key Pair Generator with Sshtools Implementation****");

		// Generate SSH key with specified key type.
		log.info("generateKeyPair", "Generating " + bits + " bit public/private " + keyType.toString().toLowerCase() + " key pair.");
		SshKeyPair keyPair = SshKeyPairFactory.newInstance(algorithmName);
		keyPair.generate(bits);

		SshPrivateKey privateKey = keyPair.getPrivateKey();
		SshPublicKey publicKey = keyPair.getPublicKey();

		// Now save the private and public keys.
		createPrivateKeyFile(privateKey, userName, passphrase, privateKeyName);
		createPublicKeyFile(publicKey, userName, publicKeyName);

		// Fingerprint.
		log.info("generateKeyPair", "The key fingerprint is: " + publicKey.getFingerprint() + ".");

		log.info("generateKeyPair", "****SSH Key Pair Generation with Sshtools Implementation finished****");
	}

	/**
	 * Create SSH private key file with specified user name, passphrase, file name and private key.
	 *
	 * @param privateKey includes the key blob which is used to create the SshPrivateKeyFile to be returned.
	 * @param userName specifies the name of user for login.
	 * @param passphrase which is used to create the SSH private key to be returned.
	 * @param privateKeyName specifies the name of the private key file to be created.
	 * @return A <code>SshPrivateKeyFile</code> object which is created with the passphrase and private key given.
	 * @throws IOException if the I/O error occurs while creating the private key file.
	 */
	public SshPrivateKeyFile createPrivateKeyFile(SshPrivateKey privateKey, String userName, String passphrase, String privateKeyName) throws IOException {
		log.info("createPrivateKeyFile", "Creating identification " + privateKeyName + ".");

		SshPrivateKeyFormat formatter;

		switch (privateKeyFormatType) {
			case OPENSSH:// OpenSSH-formatted Private Key.
				formatter = new OpenSSHPrivateKeyFormat();
				break;
			case SSHTOOLS:// Sshtools-formatted Private Key.
				formatter = new SshtoolsPrivateKeyFormat(userName, comment);
				break;
			default:
				throw new AssertionError("Unsupported SSH Private Key Format Type: " + privateKeyFormatType.toString());
		}

		// Create SSH private key file with specified key format.
		SshPrivateKeyFile keyFile = SshPrivateKeyFile.create(privateKey, passphrase, formatter);
		createFile(privateKeyName, keyFile.getBytes());

		log.info("createPrivateKeyFile", "Your identification has been saved in " + new File(privateKeyName).getAbsolutePath() + ".");

		return keyFile;
	}

	/**
	 * Create SSH public key file with specified user name, file name and public key.
	 *
	 * @param publicKey includes the key blob which is used to create the SshPublicKeyFile to be returned.
	 * @param userName specifies the name of user for login.
	 * @param publicKeyName specifies the name of the public key file to be created.
	 * @return A <code>SshPublicKeyFile</code> object which is created with the public key given.
	 * @throws IOException if the I/O error occurs while creating the public key file.
	 */
	public SshPublicKeyFile createPublicKeyFile(SshPublicKey publicKey, String userName, String publicKeyName) throws IOException {
		log.info("createPublicKeyFile", "Creating public key " + publicKeyName + ".");

		SshPublicKeyFormat formatter;

		switch (publicKeyFormatType) {
			case OPENSSH:// OpenSSH-formatted Public Key.
				formatter = new OpenSSHPublicKeyFormat(comment);
				break;
			case SECSH:// SECSH-formatted Public Key.
				formatter = new SECSHPublicKeyFormat(userName, comment);
				break;
			default:
				throw new AssertionError("Unsupported SSH Public Key Format Type: " + publicKeyFormatType.toString());
		}

		// Create SSH public key file with specified key format.
		SshPublicKeyFile keyFile = SshPublicKeyFile.create(publicKey, formatter);
		createFile(publicKeyName, keyFile.getBytes());

		log.info("createPublicKeyFile", "Your public key has been saved in " + new File(publicKeyName).getAbsolutePath() + ".");

		return keyFile;
	}

	/**
	 * Merge a specified public key into given authorized keys file which is under the ~/.ssh/authorized_keys.
	 *
	 * @param publicKey the public key file to be merged into the authorized_keys.
	 * @param authorizedKeys which is used to store all kinds of public keys for client login authorization.
	 * @param append if <code>true</code>, then the public key will be written to the end of the authorized keys rather than overridden.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void mergePublicKey(File publicKey, File authorizedKeys, boolean append) throws IOException {
		copyFile(publicKey, authorizedKeys, append);
	}

	/**
	 * Merge a specified public key into given authorized keys file which is under the ~/.ssh/authorized_keys.
	 *
     * <blockquote><pre>
     *  mergePublicKey(publicKey, authorizedKeys)
     * </pre></blockquote>
     *
     * is equivalent to:
     *
     * <blockquote><pre>
     *  mergePublicKey(passphrase, fileName, false)
     * </pre></blockquote>
	 *
	 * @param publicKey the public key file to be merged into the authorized_keys.
	 * @param authorizedKeys which is used to store all kinds of public keys for client login authorization.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void mergePublicKey(File publicKey, File authorizedKeys) throws IOException {
		copyFile(publicKey, authorizedKeys, false);
	}

	/**
	 * Merge a specified public key into given authorized keys file which is under the ~/.ssh/authorized_keys.
	 *
	 * <p>For an example: mergePublicKey("id_dsa.pub", /root/.ssh/authorized_keys, true);</p>
	 *
	 * @param publicKeyPath the path of public key file to be merged into the authorized_keys.
	 * @param authorizedKeysPath the path of the authorized keys file.
	 * @param append if <code>true</code>, then the public key will be written to the end of the authorized keys rather than overridden.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void mergePublicKey(String publicKeyPath, String authorizedKeysPath, boolean append) throws IOException {
		copyFile(new File(publicKeyPath), new File(authorizedKeysPath), append);
	}

	/**
	 * Merge a specified public key into given authorized keys file which is under the ~/.ssh/authorized_keys.
	 *
     * <blockquote><pre>
     *  mergePublicKey(publicKeyPath, authorizedKeysPath)
     * </pre></blockquote>
     *
     * is equivalent to:
     *
     * <blockquote><pre>
     *  mergePublicKey(publicKeyPath, authorizedKeysPath, false)
     * </pre></blockquote>
	 *
	 * @param publicKeyPath the path of public key file to be merged into the authorized_keys.
	 * @param authorizedKeysPath the path of the authorized keys file.
	 * @throws IOException if an I/O error occurs.
	 */
	public static void mergePublicKey(String publicKeyPath, String authorizedKeysPath) throws IOException {
		copyFile(new File(publicKeyPath), new File(authorizedKeysPath), false);
	}

	//***************************************************************
    // Private Parameter Access Methods
    //***************************************************************

	private static void createFile(String fileName, byte[] contents) throws IOException {
		FileOutputStream out = null;

		try {
			out = new FileOutputStream(fileName);
			out.write(contents);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private static void copyFile(File source, File dest, boolean append) throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;

		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest, append);
			byte[] inputBuf = new byte[1024];
			int len;

			while ((len = in.read(inputBuf)) != -1) {
				out.write(inputBuf, 0, len);
			}

			out.flush();
		} finally {
			if (in != null) {
				in.close();
			}

			if (out != null) {
				out.close();
			}
		}
	}

	public static void main(String[] arg){
		try {			
			AhSshKeyGenerator keyGen = new AhSshKeyGenerator(SshKeyType.DSA, 1024, SshPublicKeyFormatType.OPENSSH);
			String fileName = "id_dsa";
			keyGen.generateKeyPair("", fileName);
			String authorizedKeysPath = "/root/.ssh/authorized_keys";
			AhSshKeyGenerator.mergePublicKey(fileName + ".pub", authorizedKeysPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}