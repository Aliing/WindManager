package com.ah.be.communication.event.portal;

import java.nio.ByteBuffer;

import com.ah.be.communication.BeCommunicationEncodeException;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.ha.HAStatus;
import com.ah.util.coder.AhDecoder;
import com.ah.util.coder.AhEncoder;

@SuppressWarnings("serial")
public class BeHAStatusInfoEvent extends BePortalHMPayloadEvent {

	public static final byte	TYPE_HASTATUS_REPORT	= 1;

	public static final byte	TYPE_HAENABLE_RESULT	= 2;

	public static final byte	TYPE_HAJOIN_RESULT		= 3;

	public static final byte	TYPE_HABREAK_RESULT		= 4;

	public static final byte	TYPE_HASWITCH_RESULT	= 5;

	// hmol status report
	public static final byte	TYPE_HMOLSTATUS_REPORT	= 6;
	// hmol ha status query
	public static final byte	TYPE_HASTATUS_QUERY		= 7;

	public static final byte	TYPE_HADBSTATUS_QUERY	= 8;

	private byte				haInfoType;

	private boolean				isSuccess;

	private String				exceptionMessage;

	// [host][port][db][username][password]
	private String[]			dbSettings = new String[5];

	// enable result
	private String				haSecret;

	private String				primaryIP;

	private String				secondaryIP;

	// ha status report
	private short				haStatus;

	private boolean				isBothNodeOnline;

	private String				statusInfo;

	// add replicate status field
	private String replicateStatus;

	// for break
	public static final byte	TRUE  = 0;
	public static final byte	FALSE = -1;

	private boolean 			force = false;

	// for hmol status report
	public static final int			OK  		= 0;
	public static final int			FAILED  	= -1;

	private int capwapStatus 					= OK;

	private int tomcatStatus 					= OK;

	private int dbConnectionStatus 				= OK;

	private short hmolHaStatus 				= HAStatus.STATUS_UNKNOWN;

	private short maintenance 				= HMServicesSettings.HM_OLINE_STATUS_NORMAL;

	public BeHAStatusInfoEvent() {
		super();
		operationType = OPERATIONTYPE_HA_STATUSINFO;
	}

	protected byte[] buildOperationData() throws BeCommunicationEncodeException {

		ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);

		buf.put(haInfoType);
		AhEncoder.putString(buf, haSecret);
		AhEncoder.putString(buf, primaryIP);
		AhEncoder.putString(buf, secondaryIP);
		buf.put(isSuccess ? (byte) 1 : (byte) 0);
		AhEncoder.putString(buf, exceptionMessage);
		buf.putShort(haStatus);
		AhEncoder.putString(buf, statusInfo);
		buf.put(isBothNodeOnline ? (byte) 1 : (byte) 0);
		AhEncoder.putString(buf, replicateStatus);
		AhEncoder.putString(buf, dbSettings[0]);
		AhEncoder.putString(buf, dbSettings[1]);
		AhEncoder.putString(buf, dbSettings[2]);
		AhEncoder.putString(buf, dbSettings[3]);
		AhEncoder.putString(buf, dbSettings[4]);
		buf.put(force ? TRUE : FALSE);
		buf.putInt(capwapStatus);
		buf.putInt(tomcatStatus);
		buf.putInt(dbConnectionStatus);
		buf.putShort(hmolHaStatus);
		buf.putShort(maintenance);

		buf.flip();

		byte[] array = new byte[buf.limit()];
		buf.get(array);

		return array;
	}

	public byte[] parseRequest() throws Exception {
		ByteBuffer buf = ByteBuffer.wrap(getPacket());
		buf.getInt(); // length

		haInfoType = buf.get();
		haSecret = AhDecoder.getString(buf);
		primaryIP = AhDecoder.getString(buf);
		secondaryIP = AhDecoder.getString(buf);
		isSuccess = buf.get() == 1;
		exceptionMessage = AhDecoder.getString(buf);
		haStatus = buf.getShort();
		statusInfo = AhDecoder.getString(buf);
		isBothNodeOnline = buf.get() == 1;
		if(buf.hasRemaining())
			replicateStatus = AhDecoder.getString(buf);
		if(buf.hasRemaining()) {
			dbSettings[0] = AhDecoder.getString(buf);
			dbSettings[1] = AhDecoder.getString(buf);
			dbSettings[2] = AhDecoder.getString(buf);
			dbSettings[3] = AhDecoder.getString(buf);
			dbSettings[4] = AhDecoder.getString(buf);
			force = (buf.get() == TRUE ? true : false);
			capwapStatus = buf.getInt();
			tomcatStatus = buf.getInt();
			dbConnectionStatus = buf.getInt();
			hmolHaStatus = buf.getShort();
			maintenance = buf.getShort();
		}

		return null;
	}

	public byte getHaInfoType() {
		return haInfoType;
	}

	public void setHaInfoType(byte haInfoType) {
		this.haInfoType = haInfoType;
	}

	public String getHaSecret() {
		return haSecret;
	}

	public void setHaSecret(String haSecret) {
		this.haSecret = haSecret;
	}

	public String getPrimaryIP() {
		return primaryIP;
	}

	public void setPrimaryIP(String primaryIP) {
		this.primaryIP = primaryIP;
	}

	public String getSecondaryIP() {
		return secondaryIP;
	}

	public void setSecondaryIP(String secondaryIP) {
		this.secondaryIP = secondaryIP;
	}

	public String getExceptionMessage() {
		return exceptionMessage;
	}

	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public short getHaStatus() {
		return haStatus;
	}

	public void setHaStatus(short haStatus) {
		this.haStatus = haStatus;
	}

	public String getStatusInfo() {
		return statusInfo;
	}

	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
	}

	public boolean isBothNodeOnline() {
		return isBothNodeOnline;
	}

	public void setBothNodeOnline(boolean isBothNodeOnline) {
		this.isBothNodeOnline = isBothNodeOnline;
	}

	/**
	 * @return the replicateStatus
	 */
	public String getReplicateStatus() {
		return replicateStatus;
	}

	/**
	 * @param replicateStatus
	 *		the replicateStatus to set
	 */
	public void setReplicateStatus(String replicateStatus) {
		this.replicateStatus = replicateStatus;
	}

	public String[] getDbSettings() {
		return dbSettings;
	}

	public void setDbSettings(String[] dbSettings) {
		this.dbSettings = dbSettings;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public int getCapwapStatus() {
		return capwapStatus;
	}

	public void setCapwapStatus(int capwapStatus) {
		this.capwapStatus = capwapStatus;
	}

	public int getTomcatStatus() {
		return tomcatStatus;
	}

	public void setTomcatStatus(int tomcatStatus) {
		this.tomcatStatus = tomcatStatus;
	}

	public int getDbConnectionStatus() {
		return dbConnectionStatus;
	}

	public void setDbConnectionStatus(int dbConnectionStatus) {
		this.dbConnectionStatus = dbConnectionStatus;
	}

	public short getHmolHaStatus() {
		return hmolHaStatus;
	}

	public void setHmolHaStatus(short hmolHaStatus) {
		this.hmolHaStatus = hmolHaStatus;
	}

	public short getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(short maintenance) {
		this.maintenance = maintenance;
	}
}
