package com.ah.ui.actions.monitor.enrolledclient.tools;

public class MacAddressUtil {
	public static String removeDelimiter(String macAddress)
	{
		return macAddress.replaceAll("[:|-]*", "");
	}

	public static String addDelimiter(String macAddress, int gap, String delimiter)
	{
		StringBuffer st = new StringBuffer(macAddress.toUpperCase().replaceAll("[:|-]*", ""));
		String ret = "";
		for (int i = 0; i < st.length(); i += gap)
		{
			ret = ret.concat(delimiter).concat(st.substring(i, i + gap));
		}
		return ret.substring(1);
	}
}
