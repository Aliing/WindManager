package com.ah.be.ts.hiveap.monitor.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ah.be.common.NmsUtil;
import com.ah.be.ts.hiveap.DebugNotificationSorting;
import com.ah.be.ts.hiveap.monitor.client.ClientMonitorSortParams.SortType;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.mgmt.QueryUtil;

public class ClientMonitorSortImpl implements DebugNotificationSorting<ClientMonitorNotification> {

    //***************************************************************
    // Variables
    //***************************************************************

	private ClientMonitorSortParams sortParams;

	private ClientMonitorSortParams[] sortParamsGroup;

	private ClientMonitorFilterParams filterParams;

	public ClientMonitorSortImpl(ClientMonitorSortParams sortParams) {
		this.sortParams = sortParams;
	}

	public ClientMonitorSortImpl(ClientMonitorSortParams[] sortParamsGroup) {
		this.sortParamsGroup = sortParamsGroup;
	}

	public ClientMonitorSortImpl(ClientMonitorSortParams sortParams, ClientMonitorFilterParams filterParams) {
		this(sortParams);
		this.filterParams = filterParams;
	}

	public ClientMonitorSortImpl(ClientMonitorSortParams[] sortParamsGroup, ClientMonitorFilterParams filterParams) {
		this(sortParamsGroup);
		this.filterParams = filterParams;
	}

    //***************************************************************
    // Parameter Access Methods
    //***************************************************************

	public ClientMonitorSortParams getSortParams() {
		return sortParams;
	}

	public ClientMonitorSortParams[] getSortParamsGroup() {
		return sortParamsGroup;
	}

	public ClientMonitorFilterParams getFilterParams() {
		return filterParams;
	}

	@Override
	public int compare(ClientMonitorNotification o1, ClientMonitorNotification o2) {
		int difference = 0;

		if (sortParams != null) {
			difference = computeDifference(o1, o2, sortParams.getSortType(), sortParams.isAscending());
		} else if (sortParamsGroup != null) {
			// Iteratively computing until getting the difference.
			for (ClientMonitorSortParams sortParams : sortParamsGroup) {
				difference = computeDifference(o1, o2, sortParams.getSortType(), sortParams.isAscending());

				if (difference != 0) {
					break;
				}
			}
		}

		return difference;
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
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  INFO    request from
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  INFO    response to
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  INFO
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  INFO    ack radius attribute
	 *
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  DETAIL  discover
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  DETAIL  offer
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  DETAIL  request
	 *	MM/dd/yyyy hh:mm:ss  0019770004B0  0019770004C0  AH-12345678  DETAIL  acknowledge
     */
	public String getFormattedMessages(List<ClientMonitorNotification> notifications, boolean includingHeader) {
		List<ClientMonitorNotification> sortedList = sort(notifications);
		StringBuilder msgBuf = new StringBuilder();

		if (includingHeader) {
			String header = getMessageHeader(sortedList);
			msgBuf.append(header);
		}

		int maxApNameLen = getMaxApNameLen(notifications);

		for (ClientMonitorNotification notification : sortedList) {
			String msg = notification.getFormattedMessage(maxApNameLen);
			msgBuf.append(msg).append("\r\n");
		}

		return msgBuf.toString();
	}

	public String getFormattedMessages(List<ClientMonitorNotification> notifications, boolean includingHeader, String domainName) {
		List<ClientMonitorNotification> sortedList = sort(notifications);
		HmDomain owner = QueryUtil.findBoByAttribute(HmDomain.class, "domainName", domainName);
		StringBuilder msgBuf = new StringBuilder();

		if (includingHeader) {
			String header = getMessageHeader(sortedList);
			msgBuf.append(header);
		}

		int maxApNameLen = getMaxApNameLen(notifications);

		for (ClientMonitorNotification notification : sortedList) {
			String msg = notification.getFormattedMessage(maxApNameLen,owner);
			msgBuf.append(msg).append("\r\n");
		}

		return msgBuf.toString();
	}
	
	@Override
	public List<ClientMonitorNotification> sort(List<ClientMonitorNotification> notifications) {
		// A list of log messages to be filtered and sorted.
		List<ClientMonitorNotification> candidate;

		if (filterParams != null) {
			int ordinal = filterParams.getLogLevel().ordinal();
			candidate = new ArrayList<ClientMonitorNotification>(notifications.size());

			for (ClientMonitorNotification notification : notifications) {
				int logLevel = notification.getLogLevel();

				if (ordinal >= logLevel) {
					candidate.add(notification);	
				}
			}
		} else {
			candidate = notifications;
		}

		Collections.sort(candidate, this);

		return candidate;
	}

	/**
	 * Generate the message header ahead of all formatted messages to be shown.
	 *
	 * @param notifications from which the log messages to format are derived.
	 * @return the message header for client monitor.
	 */
	public String getMessageHeader(List<ClientMonitorNotification> notifications) {
		int maxApNameLen = getMaxApNameLen(notifications);
		String firstLine = "       Time        Client MAC Addr     BSSID     "+NmsUtil.getOEMCustomer().getAccessPonitName()+" Name";
		int paddingSpaces = maxApNameLen - (NmsUtil.getOEMCustomer().getAccessPonitName()+" Name").length();

		for (int i = 0; i < paddingSpaces; i++) {
			firstLine += " ";
		}

		firstLine += "  Level   Description\r\n";
		String secondLine = "";

		for (int i = 0; i < firstLine.length() - "\r\n".length(); i++) {
			secondLine += "=";
		}

		secondLine += "\r\n";

		return firstLine + secondLine;
	}

	/**
	 * Returns the maximum length of AP names each of which is one of the properties involved in a <tt>ClientMonitorNotification</tt> instance contained in the list given as argument.
	 *
	 * @param notifications a list of <tt>ClientMonitorNotification</tt>.
	 * @return the maximum length of AP names contained in a list of <tt>ClientMonitorNotification</tt> instances.
	 */
	public int getMaxApNameLen(List<ClientMonitorNotification> notifications) {
		int maxApNameLen = (NmsUtil.getOEMCustomer().getAccessPonitName()+" Name").length();

		// Compute the maximum AP Name length.
		for (ClientMonitorNotification notification : notifications) {
			HiveAp hiveAp = notification.getHiveAp();

			if (hiveAp != null && hiveAp.getHostName().length() > maxApNameLen) {
				maxApNameLen = hiveAp.getHostName().length();
			}
		}

		return maxApNameLen;
	}

	private int computeDifference(ClientMonitorNotification o1, ClientMonitorNotification o2, SortType sortType, boolean ascending) {
		int difference;

		switch (sortType) {
			case AP_NODE_NAME:
				difference = ascending ? o1.getHiveAp().getHostName().compareTo(o2.getHiveAp().getHostName()) : o2.getHiveAp().getHostName().compareTo(o1.getHiveAp().getHostName());
				break;
			case BSSID:
				difference = ascending ? o1.getBssid().compareTo(o2.getBssid()) : o2.getBssid().compareTo(o1.getBssid());
				break;
			case CLIENT_MAC:
				difference = ascending ? o1.getClientMac().compareTo(o2.getClientMac()) : o2.getClientMac().compareTo(o1.getClientMac());
				break;
			case LOG_MSG_TIME:
			//	difference = ascending ? new Long(o1.getTimstamp() - o2.getTimstamp()).intValue() : new Long(o2.getTimstamp() - o1.getTimstamp()).intValue();

				if (ascending) {
					long timeDiff = o1.getTimstamp() - o2.getTimstamp();

					if (timeDiff > 0) {
						difference = 1;
					} else if (timeDiff == 0) {
						difference = 0;
					} else {
						difference = -1;
					}
				} else {
					long timeDiff = o2.getTimstamp() - o1.getTimstamp();

					if (timeDiff > 0) {
						difference = 1;
					} else if (timeDiff == 0) {
						difference = 0;
					} else {
						difference = -1;
					}
				}
				break;
			case MSG_SEQ_NUM:
			//	difference = ascending ? new Long(o1.getMsgSeqNum() - o2.getMsgSeqNum()).intValue() : new Long(o2.getMsgSeqNum() - o1.getMsgSeqNum()).intValue();

				if (ascending) {
					long timeDiff = o1.getMsgSeqNum() - o2.getMsgSeqNum();

					if (timeDiff > 0) {
						difference = 1;
					} else if (timeDiff == 0) {
						difference = 0;
					} else {
						difference = -1;
					}
				} else {
					long timeDiff = o2.getMsgSeqNum() - o1.getMsgSeqNum();

					if (timeDiff > 0) {
						difference = 1;
					} else if (timeDiff == 0) {
						difference = 0;
					} else {
						difference = -1;
					}
				}
				break;
			default:
				difference = 0;
				break;
		}

		return difference;
	}

}