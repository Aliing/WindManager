package com.ah.be.protocol.ssh.scp;

import java.io.IOException;

public interface AhScpMgmt {

	/**
	 * Initialize SSH connection.
	 * 
	 * @throws java.io.IOException
	 *             If authentication failed.
	 */
	void initializeConnection() throws IOException;

	long getTargetFileSize(String targetFile) throws IOException;

	/**
	 * Get remote file through SCP.
	 * 
	 * @param remoteFile
	 *            File to be gotten from remote host.
	 * @param localDir
	 *            Directory reside in local which is used to save file gotten
	 *            from remote host.
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpGet(String remoteFile, String localDir) throws IOException;

	/**
	 * Get remote file through SCP.
	 * 
	 * @param remoteFiles
	 *            Files to be gotten from remote host.
	 * @param localDir
	 *            Directory reside in local which is used to save file gotten
	 *            from remote host.
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpGet(String[] remoteFiles, String localDir) throws IOException;

	/**
	 * Put local file to remote machine use mode 0600.
	 * 
	 * @param localFile
	 *            File to be put to remote host.
	 * @param remoteDir
	 *            Directory reside in remote which is used to save file gotten
	 *            from local to remote.
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpPut(String localFile, String remoteDir) throws IOException;

	/**
	 * Put local file to remote machine.
	 * 
	 * @param localFile
	 *            File to be put to remote host.
	 * @param remoteDir
	 *            Directory reside in remote which is used to save file gotten
	 *            from local to remote.
	 * @param mode
	 *            A four digit string (e.g., 0644, see "man chmod", "man open")
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpPut(String localFile, String remoteDir, String mode) throws IOException;

	/**
	 * Put local file to remote machine.
	 * 
	 * @param localFile
	 *            File to be put to remote host.
	 * @param remoteFile
	 *            The name of the file which will be created in the remote
	 *            target directory.
	 * @param remoteDir
	 *            Directory reside in remote which is used to save file gotten
	 *            from local to remote.
	 * @param mode
	 *            A four digit string (e.g., 0644, see "man chmod", "man open")
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpPut(String localFile, String remoteFile, String remoteDir, String mode)
			throws IOException;

	/**
	 * Put local file to remote machine use mode 0600.
	 * 
	 * @param localFiles
	 *            Files to be put to remote host.
	 * @param remoteDir
	 *            Directory reside in remote which is used to save file gotten
	 *            from local to remote.
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpPut(String[] localFiles, String remoteDir) throws IOException;

	/**
	 * Put local file to remote machine use mode 0600.
	 * 
	 * @param localFiles
	 *            Files to be put to remote host.
	 * @param remoteDir
	 *            Directory reside in remote which is used to save file gotten
	 *            from local to remote.
	 * @param mode
	 *            A four digit string (e.g., 0644, see "man chmod", "man open")
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpPut(String[] localFiles, String remoteDir, String mode) throws IOException;

	/**
	 * Put local file to remote machine use mode 0600.
	 * 
	 * @param localFiles
	 *            Source files reside in local.
	 * @param remoteFiles
	 *            Target Files to be put to remote host.
	 * @param remoteDir
	 *            Directory reside in remote which is used to save file gotten
	 *            from local to remote.
	 * @param mode
	 *            A four digit string (e.g., 0644, see "man chmod", "man open")
	 * @throws java.io.IOException
	 *             If an I/O error occurs. The nature of the error will be
	 *             reported in the message.
	 */
	void scpPut(String[] localFiles, String[] remoteFiles, String remoteDir, String mode)
			throws IOException;

	/**
	 * Close SSH connection
	 */
	void close();
}
