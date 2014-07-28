package com.ah.be.communication.mo;

public class VhmRumStatus {
	// general status
	public static final int	STATUS_WAITTING					= 1;
	public static final int	STATUS_RUNNING					= 2;
	public static final int	STATUS_FINISHED					= 3;

	// Revert process status
	public static final int	R_PROCESS_STATUS_NO_OPERATION	= 0;
	public static final int	R_PROCESS_STATUS_BACKUP_DATA	= 1;
	public static final int	R_PROCESS_STATUS_TRANSFER_DATA	= 2;
	public static final int	R_PROCESS_STATUS_RESTORE_DATA	= 3;
	public static final int	R_PROCESS_STATUS_CHANGE_CONFIG	= 4;

	// Upgrade process status
	public static final int	U_PROCESS_STATUS_NO_OPERATION	= 0;
	public static final int	U_PROCESS_STATUS_BACKUP_DATA	= 1;
	public static final int	U_PROCESS_STATUS_TRANSFER_DATA	= 2;
	public static final int	U_PROCESS_STATUS_RESTORE_DATA	= 3;
	public static final int	U_PROCESS_STATUS_CHANGE_CONFIG	= 4;

	// Moving process status
	public static final int	M_PROCESS_STATUS_NO_OPERATION	= 0;
	public static final int	M_PROCESS_STATUS_BACKUP_DATA	= 1;
	public static final int	M_PROCESS_STATUS_TRANSFER_DATA	= 2;
	public static final int	M_PROCESS_STATUS_RESTORE_DATA	= 3;
	public static final int	M_PROCESS_STATUS_CHANGE_CONFIG	= 4;

	private String			vhmName;

	private String			srcHmolAddress;

	private String			destHmolAddress;

	private int				status;

	private int				processStatus;

	private boolean			success;

	private String			failureInfo;

	public String getVhmName() {
		return vhmName;
	}

	public void setVhmName(String vhmName) {
		this.vhmName = vhmName;
	}

	public String getSrcHmolAddress() {
		return srcHmolAddress;
	}

	public void setSrcHmolAddress(String srcHmolAddress) {
		this.srcHmolAddress = srcHmolAddress;
	}

	public String getDestHmolAddress() {
		return destHmolAddress;
	}

	public void setDestHmolAddress(String destHmolAddress) {
		this.destHmolAddress = destHmolAddress;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(int processStatus) {
		this.processStatus = processStatus;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getFailureInfo() {
		return failureInfo;
	}

	public void setFailureInfo(String failureInfo) {
		this.failureInfo = failureInfo;
	}

}
