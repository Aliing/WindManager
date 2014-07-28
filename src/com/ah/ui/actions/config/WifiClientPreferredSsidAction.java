package com.ah.ui.actions.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmTableColumn;
import com.ah.bo.hiveap.WifiClientPreferredSsid;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.wlan.SsidProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;

public class WifiClientPreferredSsidAction extends BaseAction implements QueryBo{

	private static final long serialVersionUID = 1L;
	
	public static final int COLUMN_SSID = 1;
	public static final int COLUMN_ACCESS_SECURITY = 2;
	public static final int COLUMN_DESCRIPTION = 3;
	
	protected int keyType3 = 0;

	protected int keyType4 = 0;

	protected int keyType5 = 0;

	protected String firstKeyValue0 = "";

	protected String firstKeyValue1 = "";
	
	protected String firstKeyValue2 = "";

	protected String firstKeyValue0_1 = "";

	protected String firstKeyValue1_1 = "";

	protected String firstKeyValue2_1 = "";
	
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		
		try {
			if ("new".equals(operation)) {
				if (!setTitleAndCheckAccess(getText("config.title.wfcmssid"))) {
					setUpdateContext(true);
					return getLstForward();
				}
				setSessionDataSource(new WifiClientPreferredSsid());
				return getReturnPathWithJsonMode(INPUT, "wpreferredSsidDlg");
			}  else if ("create".equals(operation) 
			        || ("create" + getLstForward()).equals(operation)) {
				prepareGetValues() ;
				
				if (checkNameExists("ssid", getDataSource().getSsid())){
					if (isJsonMode()) {
						jsonObject = new JSONObject();
						jsonObject.put("resultStatus",false);
						jsonObject.put("errMsg", MgrUtil.getUserMessage("error.objectExists", getDataSource().getSsid()));
					}
					
					return getReturnPathWithJsonMode(INPUT, "wpreferredSsidDlg");
				}
	
				if ("create".equals(operation)) {
					id=createBo(dataSource);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("result", true);
						jsonObject.put("id", id);
						jsonObject.put("ssidname", this.getDataSource().getSsid());
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					id = createBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			}else if ("edit".equals(operation)) {							
				String strForward = editBo(this);				
				if (dataSource != null) {
					prepareSetValues();
					addLstTitle(getText("config.title.wfcmssid.edit")
							+ " '" + getChangedSsidName() + "'");
				}

				return isJsonMode()? "wpreferredSsidDlg" : strForward;

			} else if ("update".equals(operation) || ("update"+ getLstForward()).equals(operation)) {							
				jsonObject = new JSONObject();
				jsonObject.put("resultStatus", true);
				prepareGetValues() ;
				if ("update".equals(operation)) {
					updateBo(dataSource);
					if (isJsonMode()){
						jsonObject = new JSONObject();
						jsonObject.put("result", true);
						jsonObject.put("id", id);
						jsonObject.put("ssidname", this.getDataSource().getSsid());
						return "json";
					} else {
						return prepareBoList();
					}
				} else {
					updateBo(dataSource);
					setUpdateContext(true);
					return getLstForward();
				}
			} else if ("clone".equals(operation)) {
				long cloneId = getSelectedIds().get(0);
				WifiClientPreferredSsid profile = (WifiClientPreferredSsid) findBoById(boClass, cloneId, this);
				profile.setId(null);
				profile.setSsid("");
				profile.setOwner(null);
				profile.setVersion(null);
				setSessionDataSource(profile);
				return INPUT;
			} else if (("cancel" + getLstForward()).equals(operation)) {			
				if (!getLstForward().equals("")) {
					setUpdateContext(true);
					return getLstForward();
				} else {
					baseOperation();
					return prepareBoList();
				}
			} else if("remove".equals(operation)){
				if (removeCheck()) {
					baseOperation();
					return prepareBoList();
				}
				return prepareBoList();
			} else{
				baseOperation();
				return prepareBoList();
			}

		}catch(Exception e){
			return prepareActionError(e);
		}
	}
	
	public boolean removeCheck(){
		StringBuffer idStrBuf = new StringBuffer();
		Set<Long> allSelectedIds = getAllSelectedIds();
		if (allItemsSelected) {
			List<String> referencedSsids = (List<String>)QueryUtil.executeNativeQuery("select wf.ssid from wificlient_preferred_ssid wf inner join hiveap_preferred_ssid ap on wf.id=ap.preferredid");
			if (referencedSsids!=null && referencedSsids.size()>0) {
				for(String referencedSsid : referencedSsids){
					idStrBuf.append(referencedSsid).append(",");
				}
				idStrBuf.deleteCharAt(idStrBuf.lastIndexOf(","));
				addActionError(MgrUtil.getUserMessage("error.preferredssid.hiveap",idStrBuf.toString()));
				return false;
			}
		} else if (allSelectedIds != null && !allSelectedIds.isEmpty()) {
			Collection<Long> toRemoveIds = new ArrayList<Long>(allSelectedIds);
			StringBuffer toRemoveIdsBuf = new StringBuffer();
			for(Long toRemoveId : toRemoveIds){
				toRemoveIdsBuf.append(toRemoveId).append(",");
			}
			toRemoveIdsBuf.deleteCharAt(toRemoveIdsBuf.lastIndexOf(","));
			List<String> referencedSsids = (List<String>)QueryUtil.executeNativeQuery("select wf.ssid from wificlient_preferred_ssid wf inner join hiveap_preferred_ssid ap on wf.id=ap.preferredid where wf.id in ("+toRemoveIdsBuf.toString()+")");
			if (referencedSsids!=null && referencedSsids.size()>0) {
				for(String referencedSsid : referencedSsids){
					idStrBuf.append(referencedSsid).append(",");
				}
				idStrBuf.deleteCharAt(idStrBuf.lastIndexOf(","));
				addActionError(MgrUtil.getUserMessage("error.preferredssid.hiveap",idStrBuf.toString()));
				return false;
			}
		}
		return true;
	}
	
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_WIFICLIENT_PREFERRED_SSID);
		setDataSource(WifiClientPreferredSsid.class);
		keyColumnId = COLUMN_SSID;
		this.tableId = HmTableColumn.TABLE_CONFIGURATION_WIFICLIENT_PREFERRED_SSID;
	}
	
	protected String getColumnDescription(int id) {
		String code = null;
		switch (id) {
		case COLUMN_SSID:
			code = "config.ssid.head.ssid";
			break;
		case COLUMN_ACCESS_SECURITY:
			code = "report.reportList.compliance.accessSecurity";
			break;
		case COLUMN_DESCRIPTION:
			code = "config.ssid.description";
            break;
		}

		return MgrUtil.getUserMessage(code);
	}
	
	protected List<HmTableColumn> getDefaultSelectedColums() {
		List<HmTableColumn> columns = new ArrayList<HmTableColumn>();
		columns.add(new HmTableColumn(COLUMN_SSID));
		columns.add(new HmTableColumn(COLUMN_ACCESS_SECURITY));
		columns.add(new HmTableColumn(COLUMN_DESCRIPTION));
		
		return columns;
	}
	
	protected List<HmBo> paintbrushBos(Long paintbrushSource,
			Set<Long> destinationIds) {
		WifiClientPreferredSsid source = QueryUtil.findBoById(WifiClientPreferredSsid.class,
				paintbrushSource, this);
		if (source == null) {
			return null;
		}
		List<WifiClientPreferredSsid> list = QueryUtil.executeQuery(WifiClientPreferredSsid.class,
				null, new FilterParams("id", destinationIds), domainId, this);
		if (list.isEmpty()) {
			return null;
		}
		List<HmBo> hmBos = new ArrayList<HmBo>(list.size());
		for (WifiClientPreferredSsid profile : list) {
			
			if (profile.getId().equals(paintbrushSource)) {
				continue;
			}

			WifiClientPreferredSsid up = source.clone();
			if (null == up) {
				continue;
			}
			
			up.setId(profile.getId());
			up.setVersion(profile.getVersion());
			up.setSsid(profile.getSsid());
			up.setOwner(profile.getOwner());
			hmBos.add(up);
		}
		return hmBos;
	}
	
	public EnumItem[] getEnumKeyMgmt() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA
				|| getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_PSK) {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] { 
					SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK,
					SsidProfile.KEY_MGMT_WPA_PSK, SsidProfile.KEY_MGMT_WPA2_PSK});
		} else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_8021X) {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] {
					SsidProfile.KEY_MGMT_AUTO_WPA_OR_WAP2_EAP_802_1_X, 
					SsidProfile.KEY_MGMT_WPA_EAP_802_1_X, SsidProfile.KEY_MGMT_WPA2_EAP_802_1_X });
		} else if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP) {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] { SsidProfile.KEY_MGMT_WEP_PSK,
					SsidProfile.KEY_MGMT_DYNAMIC_WEP });
		} else {
			return MgrUtil.enumItems("enum.keyMgmt.", new int[] { SsidProfile.KEY_MGMT_OPEN });
		}
	}
	
	public void prepareGetValues() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
			getDataSource().setKeyType(keyType3);
			if (keyType3 == 0) {
				getDataSource().setKeyValue(firstKeyValue0);
			} else {
				getDataSource().setKeyValue(firstKeyValue0_1);
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			getDataSource().setKeyType(keyType4);
			if (keyType4 == 0) {
				getDataSource().setKeyValue(firstKeyValue1);
			} else {
				getDataSource().setKeyValue(firstKeyValue1_1);
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			getDataSource().setKeyType(keyType5);
			if (keyType5 == 0) {
				getDataSource().setKeyValue(firstKeyValue2);
			} else {
				getDataSource().setKeyValue(firstKeyValue2_1);
			}
		}
	}
	
	public void prepareSetValues(){
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA2_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WPA_PSK
				|| getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_AUTO_WPA_OR_WPA2_PSK) {
			keyType3 = getDataSource().getKeyType();
			if (keyType3 == 0) {
				firstKeyValue0 = getDataSource().getKeyValue();
			} else {
				firstKeyValue0_1 = getDataSource().getKeyValue();
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			keyType4 = getDataSource().getKeyType();
			if (keyType4 == 0) {
				firstKeyValue1 = getDataSource().getKeyValue();
			} else {
				firstKeyValue1_1 = getDataSource().getKeyValue();
			}
		}

		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			keyType5 = getDataSource().getKeyType();
			if (keyType5 == 0) {
				firstKeyValue2 = getDataSource().getKeyValue();
			} else {
				firstKeyValue2_1 = getDataSource().getKeyValue();
			}
		}
	}
	
	public WifiClientPreferredSsid getDataSource() {
		return (WifiClientPreferredSsid) dataSource;
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		
		return null;
	}
	
	public String getHideThird() {
		if (getDataSource().getAccessMode()==SsidProfile.ACCESS_MODE_WPA){
			return "";
		} else {
			return "none";
		}
	}

	public String getHideFourth() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			return "";
		} else {
			return "none";
		}
	}

	public String getHideFifth() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			return "";
		} else {
			return "none";
		}
	}
	
	public String getHideAuthMethord() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WEP
				&& getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK) {
			return "";
		}
		return "none";
	}
	
	public String getHideKeyManagement() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_OPEN) {
			return "none";
		}
		return "";
	}

	public String getHideThird_one() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
			if (keyType3 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideThird_two() {
		if (getDataSource().getAccessMode() == SsidProfile.ACCESS_MODE_WPA) {
			if (keyType3 == 1) {
				return "";
			}
		}
		return "none";
	}

	public String getHideFourth_one() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			if (keyType4 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideFourth_two() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP104) {
			if (keyType4 == 1) {
				return "";
			}
		}
		return "none";
	}

	public String getHideFifth_one() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			if (keyType5 == 1) {
				return "none";
			} else {
				return "";
			}
		}
		return "none";
	}

	public String getHideFifth_two() {
		if (getDataSource().getMgmtKey() == SsidProfile.KEY_MGMT_WEP_PSK
				&& getDataSource().getEncryption() == SsidProfile.KEY_ENC_WEP40) {
			if (keyType5 == 1) {
				return "";
			}
		}
		return "none";
	}
	
	public String getChangedSsidName() {
		return getDataSource().getSsid().replace("\\", "\\\\").replace("'", "\\'");
	}

	public int getKeyType3() {
		return keyType3;
	}

	public void setKeyType3(int keyType3) {
		this.keyType3 = keyType3;
	}

	public int getKeyType4() {
		return keyType4;
	}

	public void setKeyType4(int keyType4) {
		this.keyType4 = keyType4;
	}

	public int getKeyType5() {
		return keyType5;
	}

	public void setKeyType5(int keyType5) {
		this.keyType5 = keyType5;
	}

	public String getFirstKeyValue0() {
		return firstKeyValue0;
	}

	public void setFirstKeyValue0(String firstKeyValue0) {
		this.firstKeyValue0 = firstKeyValue0;
	}

	public String getFirstKeyValue1() {
		return firstKeyValue1;
	}

	public void setFirstKeyValue1(String firstKeyValue1) {
		this.firstKeyValue1 = firstKeyValue1;
	}

	public String getFirstKeyValue2() {
		return firstKeyValue2;
	}

	public void setFirstKeyValue2(String firstKeyValue2) {
		this.firstKeyValue2 = firstKeyValue2;
	}

	public String getFirstKeyValue0_1() {
		return firstKeyValue0_1;
	}

	public void setFirstKeyValue0_1(String firstKeyValue0_1) {
		this.firstKeyValue0_1 = firstKeyValue0_1;
	}

	public String getFirstKeyValue1_1() {
		return firstKeyValue1_1;
	}

	public void setFirstKeyValue1_1(String firstKeyValue1_1) {
		this.firstKeyValue1_1 = firstKeyValue1_1;
	}

	public String getFirstKeyValue2_1() {
		return firstKeyValue2_1;
	}

	public void setFirstKeyValue2_1(String firstKeyValue2_1) {
		this.firstKeyValue2_1 = firstKeyValue2_1;
	}
}
