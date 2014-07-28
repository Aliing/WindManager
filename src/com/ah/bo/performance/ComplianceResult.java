package com.ah.bo.performance;

import java.util.ArrayList;
import java.util.List;

import com.ah.bo.network.CompliancePolicy;
import com.ah.util.MgrUtil;

public class ComplianceResult {

	private String hiveApName;
	
	private String apMac;


	private List<ComplianceSsidListInfo> ssidList = new ArrayList<ComplianceSsidListInfo>();

	public static final int PASSWORD_STRENGTH_VERYWEAK = 1;
	public static final int PASSWORD_STRENGTH_WEAK = 2;
	public static final int PASSWORD_STRENGTH_MODERATE = 3;
	public static final int PASSWORD_STRENGTH_ACCEPTABLE = 4;
	public static final int PASSWORD_STRENGTH_STRONG = 5;
	public static final int PASSWORD_STRENGTH_VERYSTRONG = 6;
	public static final int PASSWORD_STRENGTH_NA = 7;
	private int hivePass;
//	private int hmPass;
	private int hiveApPass;
	private int capwapPass;
	
	private boolean hivePassStrong=false;
	private boolean hiveApPassStrong=false;
	private String apType="";
	private String apSn="";

	public String getHiveApName() {
		return hiveApName;
	}

	public void setHiveApName(String hiveApName) {
		this.hiveApName = hiveApName;
	}

	public List<ComplianceSsidListInfo> getSsidList() {
		return ssidList;
	}

	public void setSsidList(List<ComplianceSsidListInfo> ssidList) {
		this.ssidList = ssidList;
	}

	public int getHivePass() {
		return hivePass;
	}
	public String getHivePassString() {
		return getPasswordStrengthString(hivePass);
	}
	
	public String getHivePassDefault() {
		if (hivePass == PASSWORD_STRENGTH_VERYWEAK || hivePass == PASSWORD_STRENGTH_WEAK){
			return "Default";
		}
		return "Not default";
	}

	public void setHivePass(int hivePass) {
		this.hivePass = hivePass;
	}

//	public int getHmPass() {
//		return hmPass;
//	}
//
//	public String getHmPassString() {
//		return getPasswordStrengthString(hmPass);
//	}
//
//	public void setHmPass(int hmPass) {
//		this.hmPass = hmPass;
//	}

	public int getHiveApPass() {
		return hiveApPass;
	}
	public String getHiveApPassString() {
		return getPasswordStrengthString(hiveApPass);
	}
	
	public String getHiveApPassDefault() {
		if (hiveApPass == PASSWORD_STRENGTH_VERYWEAK || hiveApPass == PASSWORD_STRENGTH_WEAK){
			return "Default";
		}
		return "Not default";
	}

	public void setHiveApPass(int hiveApPass) {
		this.hiveApPass = hiveApPass;
	}

	public int getCapwapPass() {
		return capwapPass;
	}
	public String getCapwapPassString() {
		return getPasswordStrengthString(capwapPass);
	}

	public void setCapwapPass(int capwapPass) {
		this.capwapPass = capwapPass;
	}

	public static String getPasswordStrengthString(int type){
		switch (type) {
		case PASSWORD_STRENGTH_VERYWEAK:
			return MgrUtil.getUserMessage("report.reportList.compliance.veryweak");
		case PASSWORD_STRENGTH_WEAK:
			return MgrUtil.getUserMessage("report.reportList.compliance.weak");
		case PASSWORD_STRENGTH_MODERATE:
			return MgrUtil.getUserMessage("report.reportList.compliance.moderate");
		case PASSWORD_STRENGTH_ACCEPTABLE:
			return MgrUtil.getUserMessage("report.reportList.compliance.acceptable");
		case PASSWORD_STRENGTH_STRONG:
			return MgrUtil.getUserMessage("report.reportList.compliance.strong");
		case PASSWORD_STRENGTH_VERYSTRONG:
			return MgrUtil.getUserMessage("report.reportList.compliance.verystrong");
		default:
			return "N/A";
		}
	}

	public int getSummarySecurity(){
		int result=0;
		result = compareInt(hivePass,hiveApPass);
		result = compareInt(result,capwapPass);
		if (result==PASSWORD_STRENGTH_VERYWEAK || result ==PASSWORD_STRENGTH_WEAK){
			result = CompliancePolicy.COMPLIANCE_POLICY_POOR;
			return result;
		} else if (result== PASSWORD_STRENGTH_MODERATE || result==PASSWORD_STRENGTH_ACCEPTABLE){
			result = CompliancePolicy.COMPLIANCE_POLICY_GOOD;
		} else {
			result = CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		}
		
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getRating());
			result = compareInt(result,tmpClass.getBlnSsh());
			result = compareInt(result,tmpClass.getBlnTelnet());
			result = compareInt(result,tmpClass.getBlnPing());
			result = compareInt(result,tmpClass.getBlnSnmp());
			if (tmpClass.getSsidPass()!=0){
				result = compareInt(result,tmpClass.getSsidPass());
			}
			if (result == CompliancePolicy.COMPLIANCE_POLICY_POOR){
				return result;
			}
		}
		return result;
	}
	
	public boolean getHiveAndApPassStrong(){
		return hiveApPassStrong && hivePassStrong;
	}
	
	public int getPciSummarySecurity(){
		int result=0;
		result = compareInt(hivePass,hiveApPass);
		if (result==PASSWORD_STRENGTH_VERYWEAK || result ==PASSWORD_STRENGTH_WEAK){
			result = CompliancePolicy.COMPLIANCE_POLICY_POOR;
			return result;
		} else if (result== PASSWORD_STRENGTH_MODERATE || result==PASSWORD_STRENGTH_ACCEPTABLE){
			result = CompliancePolicy.COMPLIANCE_POLICY_GOOD;
		} else {
			result = CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		}
		
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getBlnSsh());
			result = compareInt(result,tmpClass.getBlnTelnet());
			result = compareInt(result,tmpClass.getBlnPing());
			result = compareInt(result,tmpClass.getBlnSnmp());
			if (result == CompliancePolicy.COMPLIANCE_POLICY_POOR){
				return result;
			}
		}
		return result;
	}
	
	public int getPciWeakApPassSummarySecurity(){
		return compareInt(getPciWeakHivePassLevel(), getPciWeakHiveApPassLevel());
	}
	
	public int getPciWeakHivePassLevel(){
		return getPciWeakApPassLevel(hivePass);
	}
	
	public int getPciWeakHiveApPassLevel(){
		int result = getPciWeakApPassLevel(hiveApPass);
//		if (result == CompliancePolicy.COMPLIANCE_POLICY_POOR
//				&& !this.isBlnDefaultWeakDevicePwd()) {
//			result = CompliancePolicy.COMPLIANCE_POLICY_GOOD;
//		}
		return result;
	}
	
	private int getPciWeakApPassLevel(int password){
		int result=0;
		result = password;
		if (result==PASSWORD_STRENGTH_VERYWEAK || result ==PASSWORD_STRENGTH_WEAK){
			result = CompliancePolicy.COMPLIANCE_POLICY_POOR;
			return result;
		} else if (result== PASSWORD_STRENGTH_MODERATE || result==PASSWORD_STRENGTH_ACCEPTABLE){
			result = CompliancePolicy.COMPLIANCE_POLICY_GOOD;
		} else {
			result = CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		}
		return result;
	}
	
	public int getPciWeakApServiceSummarySecurity(){
		int result=CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getBlnSsh());
			result = compareInt(result,tmpClass.getBlnTelnet());
			result = compareInt(result,tmpClass.getBlnPing());
			result = compareInt(result,tmpClass.getBlnSnmp());
			if (result == CompliancePolicy.COMPLIANCE_POLICY_POOR){
				return result;
			}
		}
		return result;
	}
	
	public String getPciSsh(){
		int result=CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getBlnSsh());
			if (result == CompliancePolicy.COMPLIANCE_POLICY_GOOD){
				return "Enabled";
			}
		}
		return "Disabled";
	}
	public String getPciTelnet(){
		int result=CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getBlnTelnet());
			if (result == CompliancePolicy.COMPLIANCE_POLICY_POOR){
				return "Enabled";
			}
		}
		return "Disabled";
	}
	public String getPciPing(){
		int result=CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getBlnPing());
			if (result == CompliancePolicy.COMPLIANCE_POLICY_GOOD){
				return "Enabled";
			}
		}
		return "Disabled";
	}
	public String getPciSnmp(){
		int result=CompliancePolicy.COMPLIANCE_POLICY_EXCELLENT;
		for(ComplianceSsidListInfo tmpClass:ssidList){
			result = compareInt(result,tmpClass.getBlnSnmp());
			if (result == CompliancePolicy.COMPLIANCE_POLICY_POOR){
				return "Enabled";
			}
		}
		return "Disabled";
	}
	
	public int compareInt(int first, int second) {
		if (first>second) {
			return second;
		}
		return first;
	}
	
	private boolean blnDefaultWeakDevicePwd;

	public boolean isBlnDefaultWeakDevicePwd() {
		return blnDefaultWeakDevicePwd;
	}

	public void setBlnDefaultWeakDevicePwd(boolean blnDefaultWeakDevicePwd) {
		this.blnDefaultWeakDevicePwd = blnDefaultWeakDevicePwd;
	}

	public String getApMac() {
		return apMac;
	}

	public void setApMac(String apMac) {
		this.apMac = apMac;
	}

	public boolean isHivePassStrong() {
		return hivePassStrong;
	}

	public String getHivePassStrongString() {
		if (hivePassStrong) {
			return "Strong";
		}
		return "Weak";
	}
	
	public void setHivePassStrong(boolean hivePassStrong) {
		this.hivePassStrong = hivePassStrong;
	}

	public boolean isHiveApPassStrong() {
		return hiveApPassStrong;
	}
	
	public String getHiveApPassStrongString() {
		if (hiveApPassStrong) {
			return "Strong";
		}
		return "Weak";
	}

	public void setHiveApPassStrong(boolean hiveApPassStrong) {
		this.hiveApPassStrong = hiveApPassStrong;
	}

	public String getApType() {
		return apType;
	}

	public void setApType(String apType) {
		this.apType = apType;
	}

	public String getApSn() {
		return apSn;
	}

	public void setApSn(String apSn) {
		this.apSn = apSn;
	}

}
