package com.ah.ui.actions.admin;

import com.ah.be.admin.adminOperateImpl.BeRootCADTO;
import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.be.common.NmsUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class HiveManagerCAAction extends BaseAction {

	private static final long	serialVersionUID	= 1L;

	private String				commonName			= null;
	private String				orgName				= null;
	private String				orgUnit				= null;
	private String				localName			= null;
	private String				stateName			= null;
	private String				countryCode			= null;
	private String				email				= null;
	private String				validity			= "365";
	private String				keySize				= "1";
	private String				password			= null;

	private static EnumItem[]	enumKeySize			= getEnums_KeySize();

	private static EnumItem[] getEnums_KeySize() {
		String[] keySizeArray = new String[] { "512", "1024", "2048" };
		EnumItem[] enumItems = new EnumItem[keySizeArray.length];
		for (int i = 0; i < keySizeArray.length; i++) {
			EnumItem item = new EnumItem(i, keySizeArray[i]);
			enumItems[i] = item;
		}

		return enumItems;
	}

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("create".equals(operation)) {
				try {
					boolean isSucc = createHMCA();
					if (!isSucc) {
						addActionError(HmBeResUtil.getString("hiveManagerCA.create.error"));
						generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.create.hivemanager.ca"));
					} else {
						addActionMessage(HmBeResUtil.getString("hiveManagerCA.create.success"));
						generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.create.ca",NmsUtil.getOEMCustomer().getNmsName()));
					}
				} catch (Exception e) {
					addActionError(HmBeResUtil.getString("hiveManagerCA.create.error") + " "
							+ e.getMessage());
					generateAuditLog(HmAuditLog.STATUS_FAILURE,  MgrUtil.getUserMessage("hm.audit.log.create.ca",NmsUtil.getOEMCustomer().getNmsName()));
				}

				return SUCCESS;
			} else if ("cancel".equals(operation)) {
				cancelOperation();
				return SUCCESS;
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_HIVEMANAGERCA);
	}

	private boolean createHMCA() throws Exception {
		// call be
		String keySizeStr = "1024";
		for (EnumItem item : enumKeySize) {
			if (item.getKey() == Integer.valueOf(keySize)) {
				keySizeStr = item.getValue();
			}
		}

		BeRootCADTO dto = new BeRootCADTO();
		dto.setCommName(commonName);
		dto.setCountryCode(countryCode);
		dto.setEmailAddress(email == null ? "" : email);
		dto.setKeySize(keySizeStr);
		dto.setLocalityName(localName);
		dto.setOrgName(orgName);
		dto.setOrgUnit(orgUnit);
		dto.setPassword(password);
		dto.setStateName(stateName);
		dto.setValidity(validity);
		dto.setDomainName(getDomain().getDomainName());

		return HmBeAdminUtil.createRootCA(dto);
	}

	private void cancelOperation() {
		commonName = null;
		orgName = null;
		orgUnit = null;
		localName = null;
		stateName = null;
		countryCode = null;
		email = null;
		validity = "365";
		keySize = null;
		password = null;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getKeySize() {
		return keySize;
	}

	public void setKeySize(String keySize) {
		this.keySize = keySize;
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgUnit() {
		return orgUnit;
	}

	public void setOrgUnit(String orgUnit) {
		this.orgUnit = orgUnit;
	}

	// WHS issue 45563330, avoid user input password expose on input page when not pass back end validation
/*	public String getPassword() {
		return password;
	}*/

	public void setPassword(String password) {
		this.password = password;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getValidity() {
		return validity;
	}

	public void setValidity(String validity) {
		this.validity = validity;
	}

	public static EnumItem[] getEnumKeySize() {
		return enumKeySize;
	}

	public static void setEnumKeySize(EnumItem[] enumKeySize) {
		HiveManagerCAAction.enumKeySize = enumKeySize;
	}

	public int getCommonNameLength() {
		return 64;
	}

	public int getOrgNameLength() {
		return 64;
	}

	public int getOrgUnitLength() {
		return 64;
	}

	public int getLocalNameLength() {
		return 64;
	}

	public int getStateNameLength() {
		return 64;
	}

	public int getCountryCodeLength() {
		return 2;
	}

	public int getEmailAddrLength() {
		return 64;
	}

	public int getPasswordLength() {
		return 20;
	}

	public int getValidityLength() {
		return 4;
	}

}