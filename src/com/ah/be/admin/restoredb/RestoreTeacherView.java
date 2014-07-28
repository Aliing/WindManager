package com.ah.be.admin.restoredb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ah.be.log.BeLogTools;
import com.ah.be.log.HmLogConst;
import com.ah.bo.admin.HmUser;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.teacherView.TvClass;
import com.ah.bo.teacherView.TvClassSchedule;
import com.ah.bo.teacherView.TvComputerCart;
import com.ah.bo.teacherView.TvComputerCartMacName;
import com.ah.bo.teacherView.TvResourceMap;
import com.ah.bo.teacherView.TvStudentRoster;

public class RestoreTeacherView {

	// if restore data before Geneva(6.1r2)
	public static boolean RESTORE_FROM_61R2_BEFORE = false;
	
	private static List<TvClass> getAllTVClass() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		final String tableName = "tv_class";

		/**
		 * Check validation of tv_class.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}
		
		RESTORE_FROM_61R2_BEFORE = RestoreUsersAndAccess.isDataFromOldVersion((float)6.1, 2);

		/**
		 * No one row data stored in tv_class table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<TvClass> tvClassInfo = new ArrayList<TvClass>();
		boolean isColPresent;
		String colName;
		TvClass tvClass;

		for (int i = 0; i < rowCount; i++) {
			tvClass = new TvClass();

			/**
			 * Set className
			 */
			colName = "className";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_class' data be lost, cause: 'className' column is not exist.");
				/**
				 * The className column must be exist in the table of tv_class
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_class' data be lost, cause: 'className' column value is null.");
				continue;
			}
			tvClass.setClassName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_class' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of tv_class
				 */
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			tvClass.setId(Long.valueOf(id));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvClass.setDescription(AhRestoreCommons.convertString(description));

			/**
			 * Set subject
			 */
			colName = "subject";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String subject = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvClass.setSubject(AhRestoreCommons.convertString(subject));

			/**
			 * Set teacherId
			 */
			colName = "teacherId";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String teacherId = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvClass.setTeacherId(AhRestoreCommons.convertString(teacherId));

			/**
			 * Set rosterType
			 */
			colName = "rosterType";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String rosterType = isColPresent ? xmlParser.getColVal(i, colName)
					: "1";
			tvClass.setRosterType(AhRestoreCommons.convertInt(rosterType));

			/**
			 * Set cart_id
			 */
			colName = "cart_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String cart_id = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			if (!cart_id.equals("") && !cart_id.trim().equalsIgnoreCase("null")) {
				Long newCartId = AhRestoreNewMapTools.getMapTvComputerCart(Long
						.parseLong(cart_id.trim()));
				tvClass.setComputerCart(AhRestoreNewTools.CreateBoWithId(
						TvComputerCart.class, newCartId));
			}

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_class' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			tvClass.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			// fix bug 23956 in Geneva, make VHM  with IDM can create teacher. convert value of teacherId from user name to user email
			if (RESTORE_FROM_61R2_BEFORE) {
				// must place at last, before this query need reference column owner
				HmUser hmUser = QueryUtil.findBoByAttribute(HmUser.class, "lower(userName)", StringUtils.lowerCase(tvClass.getTeacherId()), tvClass.getOwner().getId());
				if (hmUser != null) {
					tvClass.setTeacherId(hmUser.getEmailAddress());
				}
			}
			
			tvClassInfo.add(tvClass);
		}
		return tvClassInfo;
	}

	private static Map<String, List<TvClassSchedule>> getAllTvClassSchedule()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of tv_class_schedule.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("tv_class_schedule");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<TvClassSchedule>> tvClassScheduleInfo = new HashMap<String, List<TvClassSchedule>>();
		TvClassSchedule tvClassSchedule;
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			tvClassSchedule = new TvClassSchedule();
			/**
			 * Set tv_class_id
			 */
			colName = "tv_class_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_class_schedule", colName);
			if (!isColPresent) {
				/**
				 * The tv_class_id column must be exist in the table of
				 * tv_class_schedule
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
					|| profileId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			/**
			 * Set starttime
			 */
			colName = "starttime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_class_schedule", colName);
			String starttime = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvClassSchedule.setStartTime(AhRestoreCommons
					.convertString(starttime));

			/**
			 * Set endtime
			 */
			colName = "endtime";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_class_schedule", colName);
			String endtime = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvClassSchedule.setEndTime(AhRestoreCommons.convertString(endtime));

			/**
			 * Set weekday
			 */
			colName = "weekday";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_class_schedule", colName);
			String weekday = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvClassSchedule.setWeekday(AhRestoreCommons.convertString(weekday));

			/**
			 * Set weekdaysec
			 */
			colName = "weekdaysec";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_class_schedule", colName);
			String weekdaysec;
			if (isColPresent){
				weekdaysec=xmlParser.getColVal(i, colName);
			} else {
				if (tvClassSchedule.getWeekday().equalsIgnoreCase("Monday")){
					weekdaysec= "0100000";
				} else if (tvClassSchedule.getWeekday().equalsIgnoreCase("Tuesday")){
					weekdaysec= "0010000";
				} else if (tvClassSchedule.getWeekday().equalsIgnoreCase("Wednesday")){
					weekdaysec= "0001000";
				} else if (tvClassSchedule.getWeekday().equalsIgnoreCase("Thursday")){
					weekdaysec= "0000100";
				} else if (tvClassSchedule.getWeekday().equalsIgnoreCase("Friday")){
					weekdaysec= "0000010";
				} else if (tvClassSchedule.getWeekday().equalsIgnoreCase("Saturday")){
					weekdaysec= "0000001";
				} else if (tvClassSchedule.getWeekday().equalsIgnoreCase("Sunday")){
					weekdaysec= "1000000";
				} else {
					weekdaysec= "0111110";
				}
			}
			tvClassSchedule.setWeekdaySec(weekdaysec);

			/**
			 * Set room
			 */
			colName = "room";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_class_schedule", colName);
			String room = isColPresent ? xmlParser.getColVal(i, colName) : "";
			tvClassSchedule.setRoom(AhRestoreCommons.convertString(room));

			if (tvClassScheduleInfo.get(profileId) == null) {
				List<TvClassSchedule> scheduleList = new ArrayList<TvClassSchedule>();
				scheduleList.add(tvClassSchedule);
				tvClassScheduleInfo.put(profileId, scheduleList);
			} else {
				tvClassScheduleInfo.get(profileId).add(tvClassSchedule);
			}
		}

		return tvClassScheduleInfo;
	}

	public static boolean restoreTvClass() {
		try {
			List<TvClass> allClass = getAllTVClass();
			Map<String, List<TvClassSchedule>> allClassSchedule = null;
			if (allClass != null && allClass.size() > 0) {
				allClassSchedule = getAllTvClassSchedule();
			}
			if (null == allClass) {
				AhRestoreDBTools.logRestoreMsg("all TvClass is null");

				return false;
			} else {
				for (TvClass tempTvClass : allClass) {
					if (tempTvClass != null && allClassSchedule != null) {
						tempTvClass.setItems(allClassSchedule.get(tempTvClass
								.getId().toString()));
					}
				}
				List<Long> lOldId = new ArrayList<Long>();

				for (TvClass allClas : allClass) {
					lOldId.add(allClas.getId());
				}

				QueryUtil.restoreBulkCreateBos(allClass);

				for (int i = 0; i < allClass.size(); ++i) {
					AhRestoreNewMapTools.setMapTvClass(lOldId.get(i), allClass
							.get(i).getId());
				}

			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<TvComputerCart> getAllTVComputerCart() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		final String tableName = "tv_computer_cart";

		/**
		 * Check validation of tv_computer_cart.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in tv_computer_cart table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<TvComputerCart> tvComputerCartInfo = new ArrayList<TvComputerCart>();
		boolean isColPresent;
		String colName;
		TvComputerCart tvComputerCart;

		for (int i = 0; i < rowCount; i++) {
			tvComputerCart = new TvComputerCart();

			/**
			 * Set cartname
			 */
			colName = "cartname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_computer_cart' data be lost, cause: 'cartname' column is not exist.");
				/**
				 * The cartname column must be exist in the table of
				 * tv_computer_cart
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_computer_cart' data be lost, cause: 'cartname' column value is null.");
				continue;
			}
			tvComputerCart.setCartName(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_computer_cart' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of tv_computer_cart
				 */
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			tvComputerCart.setId(Long.valueOf(id));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvComputerCart.setDescription(AhRestoreCommons
					.convertString(description));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_computer_cart' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			tvComputerCart.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			tvComputerCartInfo.add(tvComputerCart);
		}
		return tvComputerCartInfo;
	}

	private static Map<String, List<TvComputerCartMacName>> getAllTvComputerCartMac()
			throws AhRestoreColNotExistException, AhRestoreException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();

		/**
		 * Check validation of tv_computer_cart_mac.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile("tv_computer_cart_mac");
		if (!restoreRet) {
			return null;
		}

		int rowCount = xmlParser.getRowCount();
		Map<String, List<TvComputerCartMacName>> tvComputerCartInfo = new HashMap<String, List<TvComputerCartMacName>>();
		boolean isColPresent;
		String colName;

		for (int i = 0; i < rowCount; i++) {

			/**
			 * Set tv_cart_id
			 */
			colName = "tv_cart_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_computer_cart_mac", colName);
			if (!isColPresent) {
				/**
				 * The tv_cart_id column must be exist in the table of
				 * tv_computer_cart_mac
				 */
				continue;
			}

			String profileId = xmlParser.getColVal(i, colName);
			if (profileId == null || profileId.trim().equals("")
					|| profileId.trim().equalsIgnoreCase("null")) {
				continue;
			}

			TvComputerCartMacName tmpMacName = new TvComputerCartMacName();

			/**
			 * Set items
			 */
			colName = "items";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					"tv_computer_cart_mac", colName);
			if (isColPresent){
				String items = xmlParser.getColVal(i, colName);
				tmpMacName.setStuMac(items);
			} else {
				/**
				 * Set stumac
				 */
				colName = "stumac";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tv_computer_cart_mac", colName);
				String stumac = isColPresent ? xmlParser.getColVal(i, colName):"";
				if (stumac.equals("")){
					continue;
				}
				tmpMacName.setStuMac(stumac);

				/**
				 * Set stuname
				 */
				colName = "stuname";
				isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
						"tv_computer_cart_mac", colName);
				String stuname = isColPresent ? xmlParser.getColVal(i, colName):"";
				tmpMacName.setStuName(stuname);
			}

			if (tvComputerCartInfo.get(profileId) == null) {
				List<TvComputerCartMacName> macList = new ArrayList<TvComputerCartMacName>();
				macList.add(tmpMacName);
				tvComputerCartInfo.put(profileId, macList);
			} else {
				tvComputerCartInfo.get(profileId).add(tmpMacName);
			}

		}

		return tvComputerCartInfo;
	}

	public static boolean restoreTvComputerCart() {
		try {
			List<TvComputerCart> allCart = getAllTVComputerCart();
			Map<String, List<TvComputerCartMacName>> allCartMac = null;
			if (allCart != null && allCart.size() > 0) {
				allCartMac = getAllTvComputerCartMac();
			}
			if (null == allCart) {
				AhRestoreDBTools.logRestoreMsg("all TVComputerCart is null");

				return false;
			} else {
				for (TvComputerCart tempTvCart : allCart) {
					if (tempTvCart != null && allCartMac != null) {
						tempTvCart.setItems(allCartMac.get(tempTvCart.getId()
								.toString()));

					}
				}
				List<Long> lOldId = new ArrayList<Long>();

				for (TvComputerCart cart : allCart) {
					lOldId.add(cart.getId());
				}

				QueryUtil.restoreBulkCreateBos(allCart);

				for (int i = 0; i < allCart.size(); ++i) {
					AhRestoreNewMapTools.setMapTvComputerCart(lOldId.get(i),
							allCart.get(i).getId());
				}

			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<TvStudentRoster> getAllTVStudentRoster() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		final String tableName = "tv_student_roster";

		/**
		 * Check validation of tv_student_roster.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in tv_student_roster table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<TvStudentRoster> tvStudentRosterInfo = new ArrayList<TvStudentRoster>();
		boolean isColPresent;
		String colName;
		TvStudentRoster tvStudentRoster;

		for (int i = 0; i < rowCount; i++) {
			tvStudentRoster = new TvStudentRoster();

			/**
			 * Set studentid
			 */
			colName = "studentid";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_student_roster' data be lost, cause: 'studentid' column is not exist.");
				/**
				 * The studentid column must be exist in the table of
				 * tv_student_roster
				 */
				continue;
			}

			String studentid = xmlParser.getColVal(i, colName);
			if (studentid == null || studentid.trim().equals("")
					|| studentid.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_student_roster' data be lost, cause: 'studentid' column value is null.");
				continue;
			}
			tvStudentRoster.setStudentId(studentid.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_student_roster' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of tv_student_roster
				 */
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			tvStudentRoster.setId(Long.valueOf(id));

			/**
			 * Set studentname
			 */
			colName = "studentname";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String studentname = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvStudentRoster.setStudentName(AhRestoreCommons
					.convertString(studentname));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvStudentRoster.setDescription(AhRestoreCommons
					.convertString(description));

			/**
			 * Set class_id
			 */
			colName = "class_id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String class_id = isColPresent ? xmlParser.getColVal(i, colName): "";
			if (!class_id.equals("") && !class_id.trim().equalsIgnoreCase("null")) {
				Long newClassId = AhRestoreNewMapTools.getMapTvClass(Long
						.parseLong(class_id.trim()));
				tvStudentRoster.setTvClass(AhRestoreNewTools.CreateBoWithId(
						TvClass.class, newClassId));
			}


			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_student_roster' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			tvStudentRoster.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			tvStudentRosterInfo.add(tvStudentRoster);
		}
		return tvStudentRosterInfo;
	}

	public static boolean restoreTvStudentRoster() {
		try {
			List<TvStudentRoster> allBo = getAllTVStudentRoster();
			if (null == allBo) {
				AhRestoreDBTools.logRestoreMsg("all TvStudentRoster is null");
				return false;
			} else {
				List<Long> lOldId = new ArrayList<Long>();

				for (TvStudentRoster bo : allBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(allBo);

				for (int i = 0; i < allBo.size(); ++i) {
					AhRestoreNewMapTools.setMapTvStudentRoster(lOldId.get(i),
							allBo.get(i).getId());
				}

			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

	private static List<TvResourceMap> getAllTVResource() throws AhRestoreException,
			AhRestoreColNotExistException {
		AhRestoreGetXML xmlParser = new AhRestoreGetXML();
		final String tableName = "tv_resource_map";

		/**
		 * Check validation of tv_resource_map.xml
		 */
		boolean restoreRet = xmlParser.readXMLFile(tableName);
		if (!restoreRet) {
			return null;
		}

		/**
		 * No one row data stored in tv_resource_map table is allowed
		 */
		int rowCount = xmlParser.getRowCount();
		List<TvResourceMap> tvResourceMapInfo = new ArrayList<TvResourceMap>();
		boolean isColPresent;
		String colName;
		TvResourceMap tvResourceMap;

		for (int i = 0; i < rowCount; i++) {
			tvResourceMap = new TvResourceMap();

			/**
			 * Set resource
			 */
			colName = "resource";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_resource_map' data be lost, cause: 'resource' column is not exist.");
				/**
				 * The resource column must be exist in the table of
				 * tv_resource_map
				 */
				continue;
			}

			String name = xmlParser.getColVal(i, colName);
			if (name == null || name.trim().equals("")
					|| name.trim().equalsIgnoreCase("null")) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_resource_map' data be lost, cause: 'resource' column value is null.");
				continue;
			}
			tvResourceMap.setResource(name.trim());

			/**
			 * Set id
			 */
			colName = "id";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			if (!isColPresent) {
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_resource_map' data be lost, cause: 'id' column is not exist.");
				/**
				 * The id column must be exist in the table of tv_resource_map
				 */
				continue;
			}
			String id = isColPresent ? xmlParser.getColVal(i, colName) : "1";
			tvResourceMap.setId(Long.valueOf(id));

			/**
			 * Set alias
			 */
			colName = "alias";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String alias = isColPresent ? xmlParser.getColVal(i, colName): "";
			tvResourceMap.setAlias(AhRestoreCommons.convertString(alias));

			/**
			 * Set port
			 */
			colName = "port";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String port = isColPresent ? xmlParser.getColVal(i, colName): "";
			tvResourceMap.setPort(AhRestoreCommons.convertInt(port));

			/**
			 * Set description
			 */
			colName = "description";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			String description = isColPresent ? xmlParser.getColVal(i, colName)
					: "";
			tvResourceMap.setDescription(AhRestoreCommons
					.convertString(description));

			/**
			 * Set owner
			 */
			colName = "owner";
			isColPresent = AhRestoreCommons.isColumnPresent(xmlParser,
					tableName, colName);
			long ownerId = isColPresent ? AhRestoreCommons
					.convertLong(xmlParser.getColVal(i, colName)) : 1;
			if(null == AhRestoreNewMapTools.getHmDomain(ownerId))
			{
				BeLogTools.debug(HmLogConst.M_RESTORE, "Restore table 'tv_resource_map' data be lost, cause: 'owner' column is not available.");
				continue;
			}
			tvResourceMap.setOwner(AhRestoreNewMapTools.getHmDomain(ownerId));

			tvResourceMapInfo.add(tvResourceMap);
		}
		return tvResourceMapInfo;
	}

	public static boolean restoreTvResourceMap() {
		try {
			List<TvResourceMap> allBo = getAllTVResource();
			if (null == allBo) {
				AhRestoreDBTools.logRestoreMsg("all TvResourceMap is null");
				return false;
			} else {
				List<Long> lOldId = new ArrayList<Long>();

				for (TvResourceMap bo : allBo) {
					lOldId.add(bo.getId());
				}

				QueryUtil.restoreBulkCreateBos(allBo);

				for (int i = 0; i < allBo.size(); ++i) {
					AhRestoreNewMapTools.setMapTvResourceMap(lOldId.get(i),
							allBo.get(i).getId());
				}
			}
		} catch (Exception e) {
			AhRestoreDBTools.logRestoreMsg(e.getMessage());
			return false;
		}
		return true;
	}

}