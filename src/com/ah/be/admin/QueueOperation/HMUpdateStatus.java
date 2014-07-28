package com.ah.be.admin.QueueOperation;

public class HMUpdateStatus {
	private static final int NO_OPERATION = 0;
	public static final int UPLOAD_FILE = 1;
	public static final int CHECK_FILE = 2; // check file whether released by
											// Aerohive
	public static final int UNTAR_FILE = 3;//
	public static final int CHECK_ENV = 4;// check update environment
	public static final int BACKUP_DATA = 5;

	// restart APP or reboot System
	public static final int RESTART_APP = 6;
	public static final int REBOOT_SYS = 7;

	public static int updateStatus = NO_OPERATION;

	public static void clearStatus() {
		updateStatus = NO_OPERATION;
	}

	public static void setStatus(int status) {
		updateStatus = status;
	}

}
