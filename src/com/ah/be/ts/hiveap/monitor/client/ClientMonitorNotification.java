package com.ah.be.ts.hiveap.monitor.client;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ah.be.common.NmsUtil;
import com.ah.be.communication.BeCommunicationConstant;
import com.ah.be.ts.hiveap.AbstractDebugNotification;
import com.ah.be.ts.hiveap.DebugNotification;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorFilterParams.LogLevel;
import com.ah.bo.admin.HMServicesSettings;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.mgmt.QueryUtil;

public class ClientMonitorNotification extends AbstractDebugNotification implements DebugNotification {

	private static final long serialVersionUID = 1L;

//	private static final int STAGE_80211_STEP_PROBE_REQUEST = 1;

	private static final int STAGE_80211_STEP_PROBE_RESPONSE = 2;

	//***************************************************************
    // Variables
    //***************************************************************

	/** Client Monitor Stages */
	public enum Stage {
		IEEE80211 {
			@Override
			public
			String getTextValue() {
				return "802.11";
			}
		}, RADIUS {
			@Override
			public
			String getTextValue() {
				return "RADIUS";
			}
		}, AUTH {
			@Override
			public
			String getTextValue() {
				return "AUTH";
			}
		}, DHCP {
			@Override
			public
			String getTextValue() {
				return "DHCP";
			}
		};
		
		public abstract String getTextValue();
	}

	/** The MAC of client to monitor */
	private final String clientMac;

	/** Resultant Identifier */
	private boolean success = true;

	/** Total Step */
	private int totalStep;

	/** Current Step */
	private int currentStep;

	/** Log Level for filtering */
	private int logLevel;

	/** Message Sequence Number */
	private long msgSeqNum;

	/** The MAC of AP virtual interface */
	private String bssid;

	/** Current Stage */
	private Stage stage;
	
	private boolean isPerformance = false;

	public ClientMonitorNotification(String hiveApMac, String clientMac) {
		super.hiveApMac = hiveApMac;
		this.clientMac = clientMac;
	}

	public ClientMonitorNotification(String hiveApMac, String clientMac, int cookieId) {
		this(hiveApMac, clientMac);
		super.cookieId = cookieId;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public String getClientMac() {
		return clientMac;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getTotalStep() {
		return totalStep;
	}

	public void setTotalStep(int totalStep) {
		this.totalStep = totalStep;
	}

	public int getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}

	public int getLogLevel() {
		return logLevel;
	}

	public void setLogLevel(int logLevel) {
		this.logLevel = logLevel;
	}

	public long getMsgSeqNum() {
		return msgSeqNum;
	}

	public void setMsgSeqNum(long msgSeqNum) {
		this.msgSeqNum = msgSeqNum;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public boolean isPerformance() {
        return isPerformance;
    }

    public void setPerformance(boolean isPerformance) {
        this.isPerformance = isPerformance;
    }

    @Override
	public short getCapwapType() {
		return BeCommunicationConstant.CAPWAPCLIENTEVENT_TYPE_CLIENTMONITORING;
	}

	@Override
	public Category getCategory() {
		return Category.CLIENT_MONITOR;
	}

	@Override
	public int compareTo(DebugNotification other) {
		// Sort results by message sequence number instead of default sorting property log message time.
		if (other instanceof ClientMonitorNotification) {
			long diff = msgSeqNum - ((ClientMonitorNotification) other).getMsgSeqNum();

			if (diff > 0) {
				diff = 1;
			} else if (diff < 0) {
				diff = -1;
			}

			return (int) diff;
		} else {
			return super.compareTo(other);
		}
	}

	@Override
	public String toString() {
		return "Client Monitor Notification - Client: " + clientMac + "; AP: " + hiveApMac + "; Cookie: " + cookieId;
	}
	
	/**
	 * Algorithm for calculating completion rate.
	 *
	 * float floatRate = 1 - (total stage - stage ordinal) / (total stage) + (current step) / (total stage) / (total step);
	 *          or
	 *                 = 1 + (current step / total step + stage ordinal - total stage) / total stage
	 * int intRate = Math.round(100 * floatRate);
	 *
	 * @return debug completion rate.
	 */
	public int getCompletionRate() {
		int completionRate = 0;

		// Because the probe request and response belong to the 802.11 stage can be received by multiple APs, we could
		// not ensure which is the exact one the target client finally associates to and authenticates from based on
		// these two events reported. Of course, it makes no sense to go on redrawing the progress bar too.
		if (stage != null && (!Stage.IEEE80211.equals(stage) || currentStep > STAGE_80211_STEP_PROBE_RESPONSE)) {
			int currentStepNum = currentStep;
			int totalStepNum = totalStep;

			// The final successful notifications for both AUTH and DHCP stages hold the same number 0 for current and
			// total steps. However, 0 is a disapproved number to calculate the progress. So HM has to consider them as
			// both 1 by default as an 100% complete indication for the progress calculation.
			if (currentStepNum == 0 && totalStepNum == 0 && isSuccess()) {
				currentStepNum = 1;
				totalStepNum = 1;
			}

			if (currentStepNum > 0 && totalStepNum > 0) {
				int totalStage = Stage.values().length;
				int stageOrdinal = stage.ordinal();
				float floatRate = 1 + (1f * currentStepNum / totalStepNum + stageOrdinal - totalStage) / totalStage;
				completionRate = Math.round(100 * floatRate);
			}
		}

		return completionRate;
	}

	/*-
	 * Format Pattern.
	 *
	 *         Time        Client MAC Addr     BSSID     HiveAP Name  Level   Description
	 *	=================================================================================
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  BASIC   wifi-auth request from aaaa:bbbb:cccc
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  BASIC   wifi-auth response to aaaa:bbbb:cccc
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  BASIC   associate request
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  BASIC   associate respond
	 *
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-13579     INFO    request from
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-24680     INFO    response to
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-2345      INFO
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-345678    INFO    ack radius attribute
	 *
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-158       DETAIL  discover
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-1590      DETAIL  offer
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-1380571   DETAIL  request
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-13733     DETAIL  acknowledge
     */
	public String getFormattedMessage(int maxApNameLen) {
		String logMsgTime = getLogMsgTime();

		String clientMac = getLogClientMac();

		String bssid = getLogBSSID();

		String apNodeName = getLogApNodename();

		int paddingSpaces = getLogPaddingSpaces(maxApNameLen, apNodeName);

		for (int i = 0; i < paddingSpaces; i++) {
			apNodeName += " ";
		}

		String logLevelName = getLogLevelName();

		paddingSpaces = 6 - logLevelName.length();

		for (int i = 0; i < paddingSpaces; i++) {
			logLevelName += " ";
		}

		String description = getLogDescription();

		return MessageFormat.format("{0}  {1}  {2}  {3}  {4}  {5}", logMsgTime, clientMac, bssid, apNodeName, logLevelName, "("+msgSeqNum +")"+ description);
	}
	
	public String getFormattedMessage(int maxApNameLen,HmDomain owner) {
		String logMsgTime = getLogMsgTime(owner);

		String clientMac = getLogClientMac();

		String bssid = getLogBSSID();

		String apNodeName = getLogApNodename();

		int paddingSpaces = getLogPaddingSpaces(maxApNameLen, apNodeName);

		for (int i = 0; i < paddingSpaces; i++) {
			apNodeName += " ";
		}

		String logLevelName = getLogLevelName();

		paddingSpaces = 6 - logLevelName.length();

		for (int i = 0; i < paddingSpaces; i++) {
			logLevelName += " ";
		}

		String description = getLogDescription();

		return MessageFormat.format("{0}  {1}  {2}  {3}  {4}  {5}", logMsgTime, clientMac, bssid, apNodeName, logLevelName, "("+msgSeqNum +")"+ description);
	}

	public boolean isInterestEvent() {
        return isPerformance ? true
                : (stage != null && (!Stage.IEEE80211.equals(stage) || currentStep > STAGE_80211_STEP_PROBE_RESPONSE));		
	}

	/**
	 * 
	 * Format for the Client Performance message. Add an onclick event for description.
	 * 
	 * @author Yunzhi Lin
	 * - Time: May 5, 2012 4:58:38 PM
	 * @param maxApNameLen
	 * @return
	 * @see getFormattedMessage(int);
	 */
    public String getPerformanceFormattedMessage(int maxApNameLen) {
        String logMsgTime = getLogMsgTime();

        String clientMac = getLogClientMac();

        String bssid = getLogBSSID();

        String apNodeName = getLogApNodename();

        int paddingSpaces = getLogPaddingSpaces(maxApNameLen, apNodeName);

        for (int i = 0; i < paddingSpaces; i++) {
            apNodeName += " ";
        }

        String logLevelName = getLogLevelName();

        paddingSpaces = 6 - logLevelName.length();

        for (int i = 0; i < paddingSpaces; i++) {
            logLevelName += " ";
        }

        String description = getLogDescription();
        
        if (StringUtils.isNotBlank(description)) {
            description = "<span class=\"detail\" onclick=\"showDescDialog(this, '" + clientMac + "', '"
                    + apNodeName + "');\">" + "("+msgSeqNum +")" + description + "</span>";
        }

        return MessageFormat.format("{0}  {1}  {2}  {3}  {4}  {5}", logMsgTime, clientMac, bssid,
                apNodeName, logLevelName, description);
    }
    
    public String getPerformanceFormattedMessage(int maxApNameLen, HmDomain owner) {
        String logMsgTime = getLogMsgTime(owner);

        String clientMac = getLogClientMac();

        String bssid = getLogBSSID();

        String apNodeName = getLogApNodename();

        int paddingSpaces = getLogPaddingSpaces(maxApNameLen, apNodeName);

        for (int i = 0; i < paddingSpaces; i++) {
            apNodeName += " ";
        }

        String logLevelName = getLogLevelName();

        paddingSpaces = 6 - logLevelName.length();

        for (int i = 0; i < paddingSpaces; i++) {
            logLevelName += " ";
        }

        String description = getLogDescription();
        
        if (StringUtils.isNotBlank(description)) {
            description = "<span class=\"detail\" onclick=\"showDescDialog(this, '" + clientMac + "', '"
                    + apNodeName + "');\">" + "("+msgSeqNum +")" + description + "</span>";
        }

        return MessageFormat.format("{0}  {1}  {2}  {3}  {4}  {5}", logMsgTime, clientMac, bssid,
                apNodeName, logLevelName, description);
    }

    private String getLogApNodename() {
        // HiveAP Name
        String apNodeName = super.hiveAp != null ? super.hiveAp.getHostName() : "AH-" + hiveApMac.substring(4);
        return apNodeName;
    }
    
    private String getLogDescription() {
        // Description
        String description = "";

        if (super.description != null && !super.description.trim().isEmpty()) {
            description = super.description;

            // Get rid of the ended "\n".
            if (description.endsWith("\n")) {
                description = description.substring(0, description.length() - 1);
            }

            // Get rid of the ended "\r".
            if (description.endsWith("\r")) {
                description = description.substring(0, description.length() - 1);
            }

        }
        return description;
    }

    private String getLogLevelName() {
        // Log Level
        String logLevelName = "BASIC";

        for (LogLevel level : LogLevel.values()) {
            if (level.ordinal() == this.logLevel) {
                logLevelName = level.toString();
                break;
            }
        }
        return logLevelName;
    }

    private int getLogPaddingSpaces(int maxApNameLen, String apNodeName) {
        // Append appropriate number of spaces to be sure that all HiveAP Names
        // have the same length.
        int paddingSpaces = (maxApNameLen >= (NmsUtil.getOEMCustomer().getAccessPonitName() + " Name")
                .length() ? maxApNameLen
                : (NmsUtil.getOEMCustomer().getAccessPonitName() + " Name").length())
                - apNodeName.length();
        return paddingSpaces;
    }

    private String getLogBSSID() {
        // BSSID
        String bssid = this.bssid != null ? this.bssid : "            ";
        return bssid;
    }

    private String getLogClientMac() {
        // Client MAC Addr
        String clientMac = this.clientMac != null ? this.clientMac : "            ";
        return clientMac;
    }

    private String getLogMsgTime() {
        // Time
        DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        formatter.setTimeZone(super.timeZone);
        String logMsgTime = formatter.format(new Date(super.timstamp));
        return logMsgTime;
    }
    
    private String getLogMsgTime(HmDomain owner) {
    	HMServicesSettings bo = QueryUtil.findBoByAttribute(HMServicesSettings.class, "owner", owner);
		String dateTimeFormatString = "MM/dd/yyyy hh:mm:ss a";
		if(bo.getTimeType() == HMServicesSettings.TIME_TYPE_1){
			if(bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}else{
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_2;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_2;
				}
			}
		}else {
			if(bo.getDateFormat() == HMServicesSettings.DATE_FORMAT_TYPE_1){
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_1.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}else{
				if(bo.getDateSeparator() == HMServicesSettings.DATE_SEPARATOR_TYPE_1){
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_1) + " " + HMServicesSettings.TIME_FORMAT_1;
				}else{
					dateTimeFormatString = HMServicesSettings.DATE_FORMAT_2.replace(" ", HMServicesSettings.DATE_SEPARATOR_2) + " " + HMServicesSettings.TIME_FORMAT_1;
				}
			}
		}
        // Time
        DateFormat formatter = new SimpleDateFormat(dateTimeFormatString);
        formatter.setTimeZone(super.timeZone);
        String logMsgTime = formatter.format(new Date(super.timstamp));
        return logMsgTime;
    }
}