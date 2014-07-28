package com.ah.bo.dashboard;

public class AhDashBoardGroup {
	
	public static final String DA_GROUP_TYPE_MAP="-1";
	public static final String DA_GROUP_TYPE_OBJECT_ALL="-2";
	public static final String DA_GROUP_TYPE_SSID="-3";
	public static final String DA_GROUP_TYPE_NETWORKPOLICY="-4";
	public static final String DA_GROUP_TYPE_DEVICE_ALL="-5";
	public static final String DA_GROUP_TYPE_USERPROFILE_ALL="-6";
	public static final String DA_GROUP_TYPE_TAG_ALL="-7";
	public static final String DA_GROUP_TYPE_TAG_ONE="-8";
	public static final String DA_GROUP_TYPE_TAG_TWO="-9";
	public static final String DA_GROUP_TYPE_TAG_THREE="-10";
	public static final String DA_GROUP_TYPE_DEVICE_SINGLE="-11";

	
	public static final String DA_GROUP_TYPE_DEVICE_ENDWITH="EndWith";
	public static final String DA_GROUP_TYPE_DEVICE_FETCHMORE="FetchMore";
	public static final String DA_GROUP_TYPE_DEVICE_AP="AP";
	public static final String DA_GROUP_TYPE_DEVICE_AP_20="AP20";
	public static final String DA_GROUP_TYPE_DEVICE_AP_28="AP28";
	public static final String DA_GROUP_TYPE_DEVICE_AP_110="AP110";
	public static final String DA_GROUP_TYPE_DEVICE_AP_120="AP120";
	public static final String DA_GROUP_TYPE_DEVICE_AP_121="AP121";
	public static final String DA_GROUP_TYPE_DEVICE_AP_141="AP141";
	public static final String DA_GROUP_TYPE_DEVICE_AP_170="AP170";
	public static final String DA_GROUP_TYPE_DEVICE_AP_320="AP320";
	public static final String DA_GROUP_TYPE_DEVICE_AP_330="AP330";
	public static final String DA_GROUP_TYPE_DEVICE_AP_340="AP340";
	public static final String DA_GROUP_TYPE_DEVICE_AP_350="AP350";
	public static final String DA_GROUP_TYPE_DEVICE_AP_370="AP370";
	public static final String DA_GROUP_TYPE_DEVICE_AP_380="AP380";
	public static final String DA_GROUP_TYPE_DEVICE_AP_390="AP390";
	public static final String DA_GROUP_TYPE_DEVICE_AP_BR100="APBR100";
	public static final String DA_GROUP_TYPE_DEVICE_AP_230="AP230";
	
	public static final String DA_GROUP_TYPE_DEVICE_BR="BR";
	public static final String DA_GROUP_TYPE_DEVICE_BR_100="BR100";
	public static final String DA_GROUP_TYPE_DEVICE_BR_200="BR200";
	public static final String DA_GROUP_TYPE_DEVICE_BR_200WP="BR200WP";
	public static final String DA_GROUP_TYPE_DEVICE_BR_200LTEVZ="BR200LTEVZ";
	public static final String DA_GROUP_TYPE_DEVICE_BR_AP330="BRAP330";
	public static final String DA_GROUP_TYPE_DEVICE_BR_AP350="BRAP350";
	
	public static final String DA_GROUP_TYPE_DEVICE_SR="SR";
	public static final String DA_GROUP_TYPE_DEVICE_SR_24="SR24";
	public static final String DA_GROUP_TYPE_DEVICE_SR_48="SR48";
	public static final String DA_GROUP_TYPE_DEVICE_SR_2124P="SR2124P";
	public static final String DA_GROUP_TYPE_DEVICE_SR_2024P="SR2024P";
	public static final String DA_GROUP_TYPE_DEVICE_SR_2148P="SR2148P";
	public AhDashBoardGroup(){
		
	}
	
	public AhDashBoardGroup(String id) {
		this.id = id;
	}
	
	public AhDashBoardGroup(String id, String text) {
		this.id = id;
		this.text = text;
	}
	public AhDashBoardGroup(String id, String text, String type) {
		this.id = id;
		this.text = text;
		this.type = type;
	}
	
	public AhDashBoardGroup(String id, String text, String type, int level) {
		this.id = id;
		this.text = text;
		this.type = type;
		this.level = level;
	}
	
	private String id;
	private String text;
	private String type;
//	private String type;
	private int level;
	private boolean hasChild;
	private boolean needCombo=false;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	
	public String getTextConvert(boolean isJson){
		if (isJson) {
			return text == null ? "" : text.replace("\\", "\\\\")
					.replace("'", "\\'");
		} else {
			return text == null ? "" : text.replace("\\", "\\\\\\\\")
					.replace("'", "\\\\\'");
		}
		
	}
	
	public void setText(String text) {
		this.text = text;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (!(other instanceof AhDashBoardGroup)) {
			return false;
		}
		return id.equals(((AhDashBoardGroup)other).getId());
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	public boolean isNeedCombo() {
		return needCombo;
	}

	public void setNeedCombo(boolean needCombo) {
		this.needCombo = needCombo;
	}
}
