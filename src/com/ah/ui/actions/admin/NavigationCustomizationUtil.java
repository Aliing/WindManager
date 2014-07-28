package com.ah.ui.actions.admin;

import java.util.HashMap;
import java.util.Map;

import com.ah.bo.admin.HmUserSettings;
import com.ah.ui.actions.Navigation;
import com.ah.util.UserSettingsUtil;

public class NavigationCustomizationUtil {

	private static Map<String, Integer> dictionoryMap = null;	
	
	static {
		dictionoryMap = new HashMap<String, Integer>();
		dictionoryMap.put(Navigation.L1_FEATURE_HOME, 1);
		dictionoryMap.put(Navigation.L1_FEATURE_MONITOR, 2);
		dictionoryMap.put(Navigation.L1_FEATURE_REPORT, 3);
		dictionoryMap.put(Navigation.L1_FEATURE_TOPOLOGY, 4);
		dictionoryMap.put(Navigation.L1_FEATURE_CONFIGURATION, 5);
		dictionoryMap.put(Navigation.L1_FEATURE_TOOLS, 6);
		dictionoryMap.put(Navigation.L1_FEATURE_USER_MGR, 7);
		dictionoryMap.put(Navigation.L1_FEATURE_USER_REPORTS, 8);
		dictionoryMap.put(Navigation.L1_FEATURE_TEACHERVIEW, 9);
		dictionoryMap.put(Navigation.L1_FEATURE_DASH, 10);
	}
	private static Map<String, Integer> userMap =  new HashMap<String, Integer>();
	
	private static int MAX_NAV_CUSTOM_SIZE = 15;

	public static String isNeedDisplay(int oriValue, int menuId) {
		String target = tranAndRevert(oriValue);		
		char c = target.charAt(menuId - 1);
		if (c == '0')
			return "expanded";
		else		
		    return "collapsed";
	}	

	public static int getMenuIdByName(String menuKey) {
		int menuId = dictionoryMap.get(menuKey);
		return menuId;
	}

	/**
	 * 
	 * @param source
	 * @return 
	 */
	private static String trans(int source) {
		String resultString = Integer.toBinaryString(source);
		int length = resultString.length();
		if (length >= MAX_NAV_CUSTOM_SIZE) {

		}
		if (length < MAX_NAV_CUSTOM_SIZE) {
			int needAppend = MAX_NAV_CUSTOM_SIZE - length;
			for (int k = 0; k < needAppend; k++) {
				resultString = "0" + resultString;
			}
		}
		return resultString;
	}

	/**
	 * ]
	 * @param source
	 * @return
	 */
	private static String revert(String source) {
		String target = "";
		for (int i = source.length() - 1; i >= 0; i--) {
			target = target + source.charAt(i);
		}
		return target;
	}

	private static String tranAndRevert(int res) {
		String binaryString = trans(res);
		String target = revert(binaryString);
		return target;
	}
	/**
	 * 
	 * @param origineValue
	 * @param menuName
	 * @param showValue True means show,False means hide.
	 * @return
	 */

	public static boolean isNeedChange(int origineValue, String menuName,
			boolean showValue) {
		String target = tranAndRevert(origineValue);
		int menuId = getMenuIdByName(menuName);
		char c = target.charAt(menuId - 1);
		if (c == '0')
			return !showValue;
		else if (c == '1')
			return showValue;
		else {
			// should not reach here.need log this error.
			return true;
		}

	}

	public static int getUpdateValue(int source, String menuName,boolean isShow) {
		int menuId=getMenuIdByName(menuName);
		char target='1';
		if(isShow)target='0';
		return changeHideOfMenu(source, menuId, target);
	}

	/**
	 * 
	 * @param origineValue
	 * @param menuId
	 * @param targetChar
	 *            '1' means hide, 0 means show.
	 * @return
	 */
	private static int changeHideOfMenu(int origineValue, int menuId,char targetChar) {
		String target = tranAndRevert(origineValue);
		char c = target.charAt(menuId - 1);
		if (targetChar == c) {
			// do nothing
			return origineValue;
		} else {
			String result = "";
			int length = target.length();
			for (int k = 0; k < length; k++) {
				if (k == menuId - 1)
					result = result + targetChar;
				else
					result = result + target.charAt(k);
			}
			result = revert(result);
			Integer s = Integer.valueOf(result, 2);
			return s;
		}

	}
	
	public static int getNavCustomizationByUser(long userId,String emailAddress) {
		if(userMap.get(emailAddress)==null){
//			HmUser currentUser = QueryUtil.findBoById(HmUser.class, userId);
			HmUserSettings userSettings = UserSettingsUtil.getUserSettings(emailAddress); // changed in Geneva by xtong
			if(userSettings==null)
				userMap.put(emailAddress, 16);
			else
				userMap.put(emailAddress, userSettings.getNavCustomization());
		}
		return userMap.get(emailAddress);
	}
	
	/**
	 * 
	 * @param userId
	 * @param updateValue
	 * @param emailAddress 
	 */
	public static synchronized void updateNavCustomization( String emailAddress,int updateValue) {
		userMap.put(emailAddress, updateValue);			
	}	
}
