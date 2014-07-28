/**
m *@filename		RestoreRadiusUserProfileRule.java
 *@version
 *@author		Joseph Chen
 *@since		2008-05-14
 *Copyright (c) 2006-2008 Aerohive Co., Ltd.
 *All right reserved.
 */

/*
 * modify history
 *
 */

package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.useraccess.RadiusUserProfileRule;
import com.ah.bo.useraccess.UserProfile;

public class RestoreRadiusUserProfileRule {

	/**
	 * Restore radius_user_profile_rule table
	 *
	 * @return true if table of radius_user_profile_rule restoration is success, false
	 *         otherwise.
	 */
	public static boolean restoreRadiusUserProfileRule()
	{
		try
		{
			List<RadiusUserProfileRule> allRadiusUserProfileRules = getRadiusUserProfileRules();

			if (null != allRadiusUserProfileRules) {

				List<Long> lOldId = new ArrayList<Long>();

				for (RadiusUserProfileRule radiusUserProfileRule : allRadiusUserProfileRules) {
					lOldId.add(radiusUserProfileRule.getId());
				}

				QueryUtil.restoreBulkCreateBos(allRadiusUserProfileRules);

				for(int i=0; i < allRadiusUserProfileRules.size(); ++i)
				{
					AhRestoreNewMapTools.setMapRadiusUserProfileRule(lOldId.get(i), allRadiusUserProfileRules.get(i).getId());
				}
			}
		}
		catch (Exception e)
		{
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Get all information from radius_user_profile_rule table
	 *
	 * @return List<RadiusUserProfileRule> all RadiusUserProfileRules
	 * @throws AhRestoreColNotExistException -
	 *             if radius_user_profile_rule.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_user_profile_rule.xml.
	 */
	private static List<RadiusUserProfileRule> getRadiusUserProfileRules() throws AhRestoreColNotExistException, AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/*
		 * Check validation of radius_user_profile_rule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_user_profile_rule");

		if (!restoreRet)
		{
			return null;
		}

		/*
		 * No one row data stored in radius_user_profile_rule table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<RadiusUserProfileRule> rules = new ArrayList<RadiusUserProfileRule>();

		boolean isColPresent;
		String colName;
		RadiusUserProfileRule singleRule;

		Map<String, Set<UserProfile>> allUserProfiles = getAllUserProfiles();

		for (int i = 0; i < rowCount; i++)
		{
			singleRule = new RadiusUserProfileRule();

			/*
			 * Set radiusUserProfileRuleName
			 */
			colName = "radiususerprofilerulename";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String name = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";

			if (name.equals(""))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_user_profile_rule' data be lost, cause: 'radiususerprofilerulename' column is not exist.");
				continue;
			}

			singleRule.setRadiusUserProfileRuleName(name);

			/*
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "1";

			//AhRestoreMapTool.setMapRadiusUserProfileRule(id, name);

			/*
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRule.setDescription(AhRestoreCommons.convertString(description));

			/*
			 * Set denyAction
			 */
			colName = "denyaction";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String denyaction = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(RadiusUserProfileRule.DENY_ACTION_BAN);
			singleRule.setDenyAction((short)AhRestoreCommons.convertInt(denyaction));

			/*
			 * Set actionTime
			 */
			colName = "actiontime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String actiontime = isColPresent ? xmlParser.getColVal(i, colName) : String.valueOf(RadiusUserProfileRule.ACTION_TIME_DEFAULT);
			singleRule.setActionTime((long)AhRestoreCommons.convertInt(actiontime));

			/*
			 * Set alluserprofilespermitted
			 */
			colName = "alluserprofilespermitted";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String permitted = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singleRule.setAllUserProfilesPermitted(AhRestoreCommons.convertStringToBoolean(permitted));

			if(!singleRule.getAllUserProfilesPermitted()) {
				/*
				 * Set permitted user profile
				 */
				if(null != allUserProfiles) {
					singleRule.setPermittedUserProfiles(allUserProfiles.get(id));
				}
			}

			/*
			 * Set strict
			 */
			colName = "strict";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_user_profile_rule", colName);
			String strict = isColPresent ? xmlParser.getColVal(i, colName) : "false";
			singleRule.setStrict(AhRestoreCommons.convertStringToBoolean(strict));

			/*
			 * Set defaultflag
			 */
			colName = "defaultflag";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser, "radius_user_profile_rule", colName);
			String defaultflag = isColPresent ? xmlParser.getColVal(i, colName) : "";
			singleRule.setDefaultFlag(AhRestoreCommons.convertStringToBoolean(defaultflag));

			// set owner, joseph chen 05/04/2008
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"radius_user_profile_rule", colName);
			long ownerId = isColPresent ? AhRestoreCommons.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_user_profile_rule' data be lost, cause: 'owner' column is not available.");
			   continue;
			}
			singleRule.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			if(singleRule.isDefaultFlag()) { // joseph chen 05/20/2008
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'radius_user_profile_rule' data be lost, cause: 'defaultFlag' column value is true.");
				continue;
			}

			rules.add(singleRule);
		}

		return rules.size() > 0 ? rules : null;
	}

	/**
	 * Get all information from radius_rule_user_profile table
	 *
	 * @return Map<String, Set<UserProfile>> all UserProfiles
	 * @throws AhRestoreColNotExistException -
	 *             if radius_rule_user_profile.xml is not exist.
	 * @throws AhRestoreException -
	 *             if error in parsing radius_rule_user_profile.xml.
	 */
	private static Map<String, Set<UserProfile>> getAllUserProfiles() throws AhRestoreColNotExistException, AhRestoreException
	{
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/*
		 * Check validation of radius_rule_user_profile.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("radius_rule_user_profile");

		if (!restoreRet)
		{
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, Set<UserProfile>> userProfileInfo = new HashMap<String, Set<UserProfile>>();

		boolean isColPresent;
		String colName;
		Set<UserProfile> userProfiles;

		for (int i = 0; i < rowCount; i++)
		{
			/*
			 * Set radius_user_profile_rule_id
			 */
			colName = "radius_user_profile_rule_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_rule_user_profile", colName);
			String id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i, colName)) : "";
			if ("".equals(id))
			{
				continue;
			}
			userProfiles = userProfileInfo.get(id);

			/**
			 * Set user_profile_id
			 */
			colName = "user_profile_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
				"radius_rule_user_profile", colName);
			String up_id = isColPresent ? AhRestoreCommons.convertString(xmlParser.getColVal(i,
				colName)) : "";

			if (!"".equals(up_id))
			{
				Long newUserProfileId = AhRestoreNewMapTools.getMapUserProfile(Long.parseLong(up_id.trim()));
				UserProfile userProfile = AhRestoreNewTools.CreateBoWithId(UserProfile.class,newUserProfileId);


				if (userProfile!=null) {
					if(userProfiles == null)
					{
						userProfiles = new HashSet<UserProfile>();
						userProfiles.add(userProfile);
						userProfileInfo.put(id, userProfiles);
					}
					else
					{
						userProfiles.add(userProfile);
					}
				}
			}
		}

		return userProfileInfo.size() > 0 ? userProfileInfo : null;
	}

}