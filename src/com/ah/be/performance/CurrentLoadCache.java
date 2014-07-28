package com.ah.be.performance;

import com.ah.be.app.DebugUtil;

public class CurrentLoadCache {

	private static CurrentLoadCache	instance;

	public synchronized static CurrentLoadCache getInstance() {
		if (instance == null) {
			instance = new CurrentLoadCache();
		}

		return instance;
	}

	/* CAPWAP */
	private long	numberOfCAPWAP;

	private long	numberOfLastCAPWAP;

	private long	resultOfCAPWAP;			// average value per 5 seconds

	/* Event */
	private long	numberOfEvent;

	private long	numberOfLastEvent;

	private long	resultOfEvent;				// average value per 5 seconds

	/* Alarm */
	private long	numberOfAlarm;

	private long	numberOfLastAlarm;

	private long	resultOfAlarm;				// average value per 5 seconds

	/* Configuration */
	private long	numberOfConfigRunning;

	private long	numberOfConfigRequest;

	private long	maxNumberOfConfigRunning;	// in last 5 minutes

	private long	recordTimeOfConfigRunning;	// it related with the above number

	private long	maxNumberOfConfigRequest;	// in last 5 minutes

	private long	recordTimeOfConfigRequest;	// it related with the above number

	/* Backup */
	private long	numberOfBackupRunning;

	private long	numberOfBackupRequest;

	private long	maxNumberOfBackupRunning;	// in last 5 minutes

	private long	recordTimeOfBackupRunning;	// it related with the above number

	private long	maxNumberOfBackupRequest;	// in last 5 minutes

	private long	recordTimeOfBackupRequest;	// it related with the above number

	/* Restore */
	private long	numberOfRestoreRunning;

	private long	numberOfRestoreRequest;

	private long	maxNumberOfRestoreRunning;	// in last 5 minutes

	private long	recordTimeOfRestoreRunning; // it related with the above number

	private long	maxNumberOfRestoreRequest;	// in last 5 minutes

	private long	recordTimeOfRestoreRequest; // it related with the above number

	/* Online Users */
	private long	numberOfOnlineUser;

	private long	maxNumberOfOnlineUser;		// in last 5 minutes

	private long	recordTimeOfOnlineUser;	// it related with the above number

	/* Upgrade */
	private long	numberOfUpgradeRunning;

	private long	numberOfUpgradeRequest;

	private long	maxNumberOfUpgradeRunning;	// in last 5 minutes

	private long	recordTimeOfUpgradeRunning; // it related with the above number

	private long	maxNumberOfUpgradeRequest;	// in last 5 minutes

	private long	recordTimeOfUpgradeRequest; // it related with the above number

	public long getNumberOfUpgradeRunning() {
		return numberOfUpgradeRunning;
	}

	protected void setNumberOfUpgradeRunning(long numberOfUpgradeRunning) {
		this.numberOfUpgradeRunning = numberOfUpgradeRunning;
	}

	public long getNumberOfUpgradeRequest() {
		return numberOfUpgradeRequest;
	}

	protected void setNumberOfUpgradeRequest(long numberOfUpgradeRequest) {
		this.numberOfUpgradeRequest = numberOfUpgradeRequest;
	}

	public long getMaxNumberOfUpgradeRunning() {
		return maxNumberOfUpgradeRunning;
	}

	protected void setMaxNumberOfUpgradeRunning(long maxNumberOfUpgradeRunning) {
		this.maxNumberOfUpgradeRunning = maxNumberOfUpgradeRunning;
	}

	protected long getRecordTimeOfUpgradeRunning() {
		return recordTimeOfUpgradeRunning;
	}

	protected void setRecordTimeOfUpgradeRunning(long recordTimeOfUpgradeRunning) {
		this.recordTimeOfUpgradeRunning = recordTimeOfUpgradeRunning;
	}

	public long getMaxNumberOfUpgradeRequest() {
		return maxNumberOfUpgradeRequest;
	}

	protected void setMaxNumberOfUpgradeRequest(long maxNumberOfUpgradeRequest) {
		this.maxNumberOfUpgradeRequest = maxNumberOfUpgradeRequest;
	}

	protected long getRecordTimeOfUpgradeRequest() {
		return recordTimeOfUpgradeRequest;
	}

	protected void setRecordTimeOfUpgradeRequest(long recordTimeOfUpgradeRequest) {
		this.recordTimeOfUpgradeRequest = recordTimeOfUpgradeRequest;
	}

	protected long getNumberOfCAPWAP() {
		return numberOfCAPWAP;
	}

	protected void setNumberOfCAPWAP(long numberOfCAPWAP) {
		this.numberOfCAPWAP = numberOfCAPWAP;
	}

	protected long getNumberOfLastCAPWAP() {
		return numberOfLastCAPWAP;
	}

	protected void setNumberOfLastCAPWAP(long numberOfLastCAPWAP) {
		this.numberOfLastCAPWAP = numberOfLastCAPWAP;
	}

	public long getResultOfCAPWAP() {
		return resultOfCAPWAP;
	}

	protected void setResultOfCAPWAP(long resultOfCAPWAP) {
		this.resultOfCAPWAP = resultOfCAPWAP;
	}

	protected long getNumberOfEvent() {
		return numberOfEvent;
	}

	protected void setNumberOfEvent(long numberOfEvent) {
		this.numberOfEvent = numberOfEvent;
	}

	protected long getNumberOfLastEvent() {
		return numberOfLastEvent;
	}

	protected void setNumberOfLastEvent(long numberOfLastEvent) {
		this.numberOfLastEvent = numberOfLastEvent;
	}

	public long getResultOfEvent() {
		return resultOfEvent;
	}

	protected void setResultOfEvent(long resultOfEvent) {
		this.resultOfEvent = resultOfEvent;
	}

	protected long getNumberOfAlarm() {
		return numberOfAlarm;
	}

	protected void setNumberOfAlarm(long numberOfAlarm) {
		this.numberOfAlarm = numberOfAlarm;
	}

	protected long getNumberOfLastAlarm() {
		return numberOfLastAlarm;
	}

	protected void setNumberOfLastAlarm(long numberOfLastAlarm) {
		this.numberOfLastAlarm = numberOfLastAlarm;
	}

	public long getResultOfAlarm() {
		return resultOfAlarm;
	}

	protected void setResultOfAlarm(long resultOfAlarm) {
		this.resultOfAlarm = resultOfAlarm;
	}

	public long getNumberOfConfigRunning() {
		return numberOfConfigRunning;
	}

	protected void setNumberOfConfigRunning(long numberOfConfigRunning) {
		this.numberOfConfigRunning = numberOfConfigRunning;
	}

	public long getNumberOfConfigRequest() {
		return numberOfConfigRequest;
	}

	protected void setNumberOfConfigRequest(long numberOfConfigRequest) {
		this.numberOfConfigRequest = numberOfConfigRequest;
	}

	public long getMaxNumberOfConfigRunning() {
		return maxNumberOfConfigRunning;
	}

	protected void setMaxNumberOfConfigRunning(long maxNumberOfConfigRunning) {
		this.maxNumberOfConfigRunning = maxNumberOfConfigRunning;
	}

	protected long getRecordTimeOfConfigRunning() {
		return recordTimeOfConfigRunning;
	}

	protected void setRecordTimeOfConfigRunning(long recordTimeOfConfigRunning) {
		this.recordTimeOfConfigRunning = recordTimeOfConfigRunning;
	}

	public long getMaxNumberOfConfigRequest() {
		return maxNumberOfConfigRequest;
	}

	protected void setMaxNumberOfConfigRequest(long maxNumberOfConfigRequest) {
		this.maxNumberOfConfigRequest = maxNumberOfConfigRequest;
	}

	protected long getRecordTimeOfConfigRequest() {
		return recordTimeOfConfigRequest;
	}

	protected void setRecordTimeOfConfigRequest(long recordTimeOfConfigRequest) {
		this.recordTimeOfConfigRequest = recordTimeOfConfigRequest;
	}

	public long getNumberOfBackupRunning() {
		return numberOfBackupRunning;
	}

	protected void setNumberOfBackupRunning(long numberOfBackupRunning) {
		this.numberOfBackupRunning = numberOfBackupRunning;
	}

	public long getNumberOfBackupRequest() {
		return numberOfBackupRequest;
	}

	protected void setNumberOfBackupRequest(long numberOfBackupRequest) {
		this.numberOfBackupRequest = numberOfBackupRequest;
	}

	public long getMaxNumberOfBackupRunning() {
		return maxNumberOfBackupRunning;
	}

	protected void setMaxNumberOfBackupRunning(long maxNumberOfBackupRunning) {
		this.maxNumberOfBackupRunning = maxNumberOfBackupRunning;
	}

	protected long getRecordTimeOfBackupRunning() {
		return recordTimeOfBackupRunning;
	}

	protected void setRecordTimeOfBackupRunning(long recordTimeOfBackupRunning) {
		this.recordTimeOfBackupRunning = recordTimeOfBackupRunning;
	}

	public long getMaxNumberOfBackupRequest() {
		return maxNumberOfBackupRequest;
	}

	protected void setMaxNumberOfBackupRequest(long maxNumberOfBackupRequest) {
		this.maxNumberOfBackupRequest = maxNumberOfBackupRequest;
	}

	protected long getRecordTimeOfBackupRequest() {
		return recordTimeOfBackupRequest;
	}

	protected void setRecordTimeOfBackupRequest(long recordTimeOfBackupRequest) {
		this.recordTimeOfBackupRequest = recordTimeOfBackupRequest;
	}

	public long getNumberOfRestoreRunning() {
		return numberOfRestoreRunning;
	}

	protected void setNumberOfRestoreRunning(long numberOfRestoreRunning) {
		this.numberOfRestoreRunning = numberOfRestoreRunning;
	}

	public long getNumberOfRestoreRequest() {
		return numberOfRestoreRequest;
	}

	protected void setNumberOfRestoreRequest(long numberOfRestoreRequest) {
		this.numberOfRestoreRequest = numberOfRestoreRequest;
	}

	public long getMaxNumberOfRestoreRunning() {
		return maxNumberOfRestoreRunning;
	}

	protected void setMaxNumberOfRestoreRunning(long maxNumberOfRestoreRunning) {
		this.maxNumberOfRestoreRunning = maxNumberOfRestoreRunning;
	}

	protected long getRecordTimeOfRestoreRunning() {
		return recordTimeOfRestoreRunning;
	}

	protected void setRecordTimeOfRestoreRunning(long recordTimeOfRestoreRunning) {
		this.recordTimeOfRestoreRunning = recordTimeOfRestoreRunning;
	}

	public long getMaxNumberOfRestoreRequest() {
		return maxNumberOfRestoreRequest;
	}

	protected void setMaxNumberOfRestoreRequest(long maxNumberOfRestoreRequest) {
		this.maxNumberOfRestoreRequest = maxNumberOfRestoreRequest;
	}

	protected long getRecordTimeOfRestoreRequest() {
		return recordTimeOfRestoreRequest;
	}

	protected void setRecordTimeOfRestoreRequest(long recordTimeOfRestoreRequest) {
		this.recordTimeOfRestoreRequest = recordTimeOfRestoreRequest;
	}

	public long getNumberOfOnlineUser() {
		return numberOfOnlineUser;
	}

	protected void setNumberOfOnlineUser(long numberOfOnlineUser) {
		this.numberOfOnlineUser = numberOfOnlineUser;
	}

	public long getMaxNumberOfOnlineUser() {
		return maxNumberOfOnlineUser;
	}

	protected void setMaxNumberOfOnlineUser(long maxNumberOfOnlineUser) {
		this.maxNumberOfOnlineUser = maxNumberOfOnlineUser;
	}

	protected long getRecordTimeOfOnlineUser() {
		return recordTimeOfOnlineUser;
	}

	protected void setRecordTimeOfOnlineUser(long recordTimeOfOnlineUser) {
		this.recordTimeOfOnlineUser = recordTimeOfOnlineUser;
	}

	private final long	ONE_VALUE	= 10000;

	public synchronized void increaseNumberOfCAPWAP() {
		if (this.numberOfCAPWAP + 1 > Long.MAX_VALUE) {
			this.numberOfCAPWAP -= (Long.MAX_VALUE - ONE_VALUE);
			this.numberOfLastCAPWAP -= (Long.MAX_VALUE - ONE_VALUE);
		}
		this.numberOfCAPWAP++;
	}

	public synchronized void increaseNumberOfAlarm() {
		if (this.numberOfAlarm + 1 > Long.MAX_VALUE) {
			this.numberOfAlarm -= (Long.MAX_VALUE - ONE_VALUE);
			this.numberOfLastAlarm -= (Long.MAX_VALUE - ONE_VALUE);
		}
		this.numberOfAlarm++;
	}

	public synchronized void increaseNumberOfEvent() {
		if (this.numberOfEvent + 1 > Long.MAX_VALUE) {
			this.numberOfEvent -= (Long.MAX_VALUE - ONE_VALUE);
			this.numberOfLastEvent -= (Long.MAX_VALUE - ONE_VALUE);
		}
		this.numberOfEvent++;
	}

	/* Configuration */
	private synchronized void changeNumberOfConfigRequest(int n) {
		this.numberOfConfigRequest += n;
	}

	private synchronized void changeNumberOfConfigRunning(int n) {
		this.numberOfConfigRunning += n;
	}

	public void increaseNumberOfConfigRequest() {
		this.changeNumberOfConfigRequest(1);
	}

	public void decreaseNumberOfConfigRequest() {
		this.changeNumberOfConfigRequest(-1);
	}

	public void increaseNumberOfConfigRunning() {
		this.changeNumberOfConfigRunning(1);
	}

	public void decreaseNumberOfConfigRunning() {
		this.changeNumberOfConfigRunning(-1);
	}

	public void decreaseNumberOfConfigRequestWithRunning() {
		this.changeNumberOfConfigRunning(-1);
		this.changeNumberOfConfigRequest(-1);
	}

	/* Backup */
	private synchronized void changeNumberOfBackupRequest(int n) {
		this.numberOfBackupRequest += n;
	}

	private synchronized void changeNumberOfBackupRunning(int n) {
		this.numberOfBackupRunning += n;
	}

	public void increaseNumberOfBackupRequest() {
		this.changeNumberOfBackupRequest(1);
	}

	public void decreaseNumberOfBackupRequest() {
		this.changeNumberOfBackupRequest(-1);
	}

	public void increaseNumberOfBackupRunning() {
		this.changeNumberOfBackupRunning(1);
	}

	public void decreaseNumberOfBackupRunning() {
		this.changeNumberOfBackupRunning(-1);
	}

	public void decreaseNumberOfBackupRequestWithRunning() {
		this.changeNumberOfBackupRunning(-1);
		this.changeNumberOfBackupRequest(-1);
	}

	/* Restore */
	private synchronized void changeNumberOfRestoreRequest(int n) {
		this.numberOfRestoreRequest += n;
	}

	private synchronized void changeNumberOfRestoreRunning(int n) {
		this.numberOfRestoreRunning += n;
	}

	public void increaseNumberOfRestoreRequest() {
		this.changeNumberOfRestoreRequest(1);
	}

	public void decreaseNumberOfRestoreRequest() {
		this.changeNumberOfRestoreRequest(-1);
	}

	public void increaseNumberOfRestoreRunning() {
		this.changeNumberOfRestoreRunning(1);
	}

	public void decreaseNumberOfRestoreRunning() {
		this.changeNumberOfRestoreRunning(-1);
	}

	public void decreaseNumberOfRestoreRequestWithRunning() {
		this.changeNumberOfRestoreRunning(-1);
		this.changeNumberOfRestoreRequest(-1);
	}

	/* Online User */
	private synchronized void changeNumberOfOnlineUser(int n) {
		this.numberOfOnlineUser += n;
	}

	public void increaseNumberOfOnlineUser() {
		this.changeNumberOfOnlineUser(1);
	}

	public void decreaseNumberOfOnlineUser() {
		this.changeNumberOfOnlineUser(-1);
	}

	/* Upgrade */
	private synchronized void changeNumberOfUpgradeRequest(int n) {
		this.numberOfUpgradeRequest += n;
	}

	private synchronized void changeNumberOfUpgradeRunning(int n) {
		this.numberOfUpgradeRunning += n;
	}

	public void increaseNumberOfUpgradeRequest() {
		this.changeNumberOfUpgradeRequest(1);
	}

	public void decreaseNumberOfUpgradeRequest() {
		this.changeNumberOfUpgradeRequest(-1);
	}

	public void increaseNumberOfUpgradeRunning() {
		this.changeNumberOfUpgradeRunning(1);
	}

	public void decreaseNumberOfUpgradeRunning() {
		this.changeNumberOfUpgradeRunning(-1);
	}

	public void decreaseNumberOfUpgradeRequestWithRunning() {
		this.changeNumberOfUpgradeRunning(-1);
		this.changeNumberOfUpgradeRequest(-1);
	}

	public void printValues() {
		StringBuilder sb = new StringBuilder();
		sb.append("CAPWAP info:[" + numberOfCAPWAP + "," + numberOfLastCAPWAP + ","
				+ resultOfCAPWAP + "]\n");
		sb.append("Event info:[" + numberOfEvent + "," + numberOfLastEvent + "," + resultOfEvent
				+ "]\n");
		sb.append("Alarm info:[" + numberOfAlarm + "," + numberOfLastAlarm + "," + resultOfAlarm
				+ "]\n");
		sb.append("Config info:[" + numberOfConfigRunning + "," + numberOfConfigRequest + ","
				+ maxNumberOfConfigRunning + "," + maxNumberOfConfigRequest + "]\n");
		sb.append("Backup info:[" + numberOfBackupRunning + "," + numberOfBackupRequest + ","
				+ maxNumberOfBackupRunning + "," + maxNumberOfBackupRequest + "]\n");
		sb.append("Restore info:[" + numberOfRestoreRunning + "," + numberOfRestoreRequest + ","
				+ maxNumberOfRestoreRunning + "," + maxNumberOfRestoreRequest + "]\n");
		sb.append("Upgrade info:[" + numberOfUpgradeRunning + "," + numberOfUpgradeRequest + ","
				+ maxNumberOfUpgradeRunning + "," + maxNumberOfUpgradeRequest + "]\n");
		sb.append("OnlineUser info:[" + numberOfOnlineUser + "," + maxNumberOfOnlineUser + "]\n");

		DebugUtil.performanceDebugInfo(sb.toString());
	}

}