package com.ah.bo.report;

public interface AhReportProperties {
	
	public static final short MAX_DISPLAY_DATA_COUNT_FOR_REPORT = 20;
	
	// define report id here
	public static final long REPORT_EXAMPLE_FOR_TEST_LINEAR = -1;
	public static final long REPORT_EXAMPLE_FOR_TEST_DATETIME = -2;
	

	public static final long REPORT_BANDWIDTH_USAGE_OF_DEVICE = 1002;
	public static final long REPORT_BANDWIDTH_OVER_TIME = 1003;
	public static final long REPORT_BANDWIDTH_USAGE_BY_SSID = 1004;
	
	public static final long REPORT_CLIENTS_NUMBER_BY_AP = 1021;
	public static final long REPORT_CLIENTS_NUMBER_OVER_TIME = 1022;
	public static final long REPORT_CLIENT_DISTRIBUTION_BY_SSID = 1023;
	public static final long REPORT_CLIENT_TYPE_DISTRIBUTION = 1024;
	public static final long REPORT_CLIENTS_TOPN = 1025;
	
	public static final long REPORT_SLA_DEVICE = 1041;
	public static final long REPORT_SLA_CLIENT = 1042;
	
	public static final long REPORT_SSID_CLIENTS_OVER_TIME = 1301;
	public static final long REPORT_SSID_CLIENTS_BANDWIDTH = 1302;
	
	public static final long REPORT_DEVICE_ERROR=1501;

	
	
	// define report series type here
	public static final String SERIES_TYPE_LINE = "line";
	public static final String SERIES_TYPE_AREA = "area";
	public static final String SERIES_TYPE_BAR = "bar";
	public static final String SERIES_TYPE_PIE = "pie";
	public static final String SERIES_TYPE_COLUMN = "column";
	public static final String SERIES_TYPE_SCATTER = "scatter";
	public static final String SERIES_TYPE_SPLINE = "spline";
	
	public static final String[] SERIES_ALL_COLORS = 
		new String[]{"#0093D1", "#002848", "#C84B00", "#F7A520", "#FFC20E", "#BDBAAB", "#6F6F6F", "#0066FF", "#00CCCC"
					, "#330099", "#339999", "#3399CC", "#663300", "#66CCFF", "#993399", "#999900", "#CC0066", "#CC6633"
					,"#CCCCFF", "#FF6699", "#FF66FF", "#FFCC99", "#FFFF66"
					,"#00FF99", "#336600", "#FF0000", "#FF3399", "#CCFF00", "#CCCCCC", "#666699"
					,"#000000"};
	public static final int SERIES_ALL_COLORS_SIZE = SERIES_ALL_COLORS.length;
	
	public enum namedColors {
		RED("#FF0000"), YELLOW("#FFFF00"), 
		GREEN("#00FF00"), SEAGREEN("#006600");
		
		private String value;
		public String getValue() {
			return this.value;
		}
		namedColors(String value) {
			this.value = value;
		}
	}
	
	// define error codes here
	public static final Short ERROR_CODE_CALCULATE_EXCEPTION = 1;
	
}
