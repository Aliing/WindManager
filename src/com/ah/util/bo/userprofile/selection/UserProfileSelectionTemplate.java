package com.ah.util.bo.userprofile.selection;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ah.be.parameter.BeParaModule;
import com.ah.bo.HmBo;
import com.ah.bo.mgmt.FilterParams;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.UserProfile;
import com.ah.ui.actions.BaseAction;
import com.ah.util.CheckItem2;
import com.ah.util.MgrUtil;


public abstract class UserProfileSelectionTemplate {
	
	protected UserProfileSelectionTemplate() {
	}
	
	protected UserProfileSelectionTemplate(Object boToDeal, Long domainId, boolean blnPrepareData) {
		this.boToDeal = boToDeal;
		this.domainId = domainId;
		if (blnPrepareData) {
			doDataPrepare();
		}
	}
	
	protected UserProfileSelectionTemplate(Long id, Class<? extends HmBo> c, QueryBo queryBo, Long domainId, boolean blnPrepareData) {
		if (id != null && id > 0L) {
			this.boToDeal = QueryUtil.findBoById(c, id, queryBo);
			this.domainId = domainId;
			if (blnPrepareData) {
				doDataPrepare();
			}
		}
	}
	
	/**
	 * fetch out object and convert to the type you need
	 *
	 * @return -
	 */
	protected abstract <T extends HmBo> T getMyObject();
	
	protected abstract void doDataPrepare();
	
	public void addNewlyAddedUserProfile(Long addedId, short type, boolean blnRePrepareData) {
		this.newUpId = addedId;
		this.upType = type;
		
		if (this.newUpId != null && this.newUpId > 0L) {
			this.newlyAddedUserProfile = QueryUtil.findBoById(UserProfile.class, this.newUpId);
			if (blnRePrepareData) {
				this.doDataPrepare();
			}
		}
	}
	
	protected Map<Long, CheckItem2> getBoCheckItems(String fieldName,
			Class<? extends HmBo> boClass, FilterParams filterPa) {
		if (fieldName == null || fieldName.length() == 0 || boClass == null) {
			return null;
		}
		
		// get list of id and name from database
		String sql = "SELECT bo.id, bo." + fieldName + " FROM "
				+ boClass.getSimpleName() + " bo";
		List<?> bos = QueryUtil.executeQuery(sql, null, filterPa,
				this.domainId);
		
		Map<Long, CheckItem2> items = new HashMap<Long, CheckItem2>();
		
		boolean ifIsSsid = boClass.getName().equals(
		"com.ah.bo.wlan.SsidProfile");
		for (Object obj : bos) {
			Object[] item = (Object[]) obj;
			String profileName = (String) item[1];
			if (ifIsSsid) {
				if (BeParaModule.SSID_PROFILE_TEMPLATE_SYMBOL_SCANNER
						.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_LEGACY_CLIENTS
								.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_HIGH_CAPACITY
								.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_BLACK_BERRY
								.equals(profileName)
						|| BeParaModule.SSID_PROFILE_TEMPLATE_SPECTRA_LINK
								.equals(profileName)) {
					continue;
				}
			}
			CheckItem2 checkItem = new CheckItem2((Long) item[0], profileName);
			items.put(checkItem.getId(), checkItem);
		}
		
		if (items.isEmpty()) {
			items.put((long) BaseAction.CHECK_ITEM_ID_NONE, 
					new CheckItem2((long) BaseAction.CHECK_ITEM_ID_NONE, MgrUtil
					.getUserMessage("config.optionsTransfer.none")));
		}
		
		return items;
	}
	
	protected CheckItem2 encapUpToCheckItem(UserProfile userProfile, boolean checked) {
		if (userProfile == null) {
			return null;
		}
		
		return new CheckItem2(userProfile.getId(), getDisplayName(userProfile), checked);
	}
	
	protected final class CheckItem2ComparatorWithId implements Comparator<CheckItem2> {
		@Override
		public int compare(CheckItem2 item1, CheckItem2 item2) {
			return item1.getId().compareTo(item2.getId());
		}
	}
	
	private String getDisplayName(UserProfile userProfile) {
		if (userProfile == null) {
			return null;
		}
		return userProfile.getUserProfileName() + " (" + userProfile.getAttributeValue() + ")";
	}

	private Long domainId;
	
	private Object boToDeal;
	
	private Long newUpId;
	
	private short upType = UserProfileSelection.USER_PROFILE_TYPE_NONE;
	
	private UserProfile newlyAddedUserProfile;

	public Object getBoToDeal() {
		return boToDeal;
	}

	public void setBoToDeal(Object boToDeal) {
		this.boToDeal = boToDeal;
	}

	public Long getNewUpId() {
		return newUpId;
	}

	public void setNewUpId(Long newUpId) {
		this.newUpId = newUpId;
	}

	public short getUpType() {
		return upType;
	}

	public void setUpType(short upType) {
		this.upType = upType;
	}

	public UserProfile getNewlyAddedUserProfile() {
		return newlyAddedUserProfile;
	}

	public void setNewlyAddedUserProfile(UserProfile newlyAddedUserProfile) {
		this.newlyAddedUserProfile = newlyAddedUserProfile;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
	protected List<CheckItem2> defaultUserProfiles;
	protected List<CheckItem2> selfUserProfiles;
	protected List<CheckItem2> authUserProfiles;
	protected List<CheckItem2> authDataUserProfiles; //Data&Phone
	protected List<CheckItem2> authFailUserProfiles; //Data&Phone
	protected List<CheckItem2> guestUserProfiles; // AirWatch Non-Compliance

	protected String upSelectionTipOfDefault;
	protected String upSelectionTipOfReg;
	protected String upSelectionTipOfAuth;
	protected String upSelectionTipOfAuthData; //Data&Phone
	protected String upSelectionTipOfAuthFail; //Data&Phone
	protected String upSelectionTipOfGuest; // AirWatch Non-Compliance
	
	protected boolean defaultUserProfileSupport;
	protected boolean selfUserProfileSupport;
	protected boolean authUserProfileSupport;
	protected boolean authDataUserProfileSupport; //Data&Phone
	protected boolean authFailUserProfileSupport; //Data&Phone
	protected boolean guestUserProfileSupport; // AirWatch Non-Compliance
	
	protected void sortListByalpha(List<CheckItem2> options){
		//sort by alphabetically
		if(null != options && options.size() > 2){
			Collections.sort(options, new Comparator<CheckItem2>() {
				@Override
				public int compare(CheckItem2 o1, CheckItem2 o2) {
					return o1.getValue().compareToIgnoreCase(o2.getValue());
				}
			});
		}
	}
	
}