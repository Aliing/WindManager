package com.ah.test;

/*
 * @author Chris Scheers
 */

import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.test.util.HmTest;
import com.ah.util.Tracer;

public class AdminTest extends HmTest {
	private static final Tracer log = new Tracer(AdminTest.class
			.getSimpleName());

	public void run() {
		try {
			HmUserGroup[] userGroups = new HmUserGroup[5];
			for (int i = 0; i < 5; i++) {
				HmUserGroup group = new HmUserGroup();
				group.setGroupName("group" + (i + 1));
				Long id = BoMgmt.createBo(group, null, null);
				userGroups[i] = (HmUserGroup) BoMgmt.findBoById(
						HmUserGroup.class, id, null, null);
			}
			for (int i = 0; i < 30; i++) {
				HmUser user = new HmUser();
				user.setUserName("user" + (i + 1));
				user.setUserFullName("User" + (i + 1) + " Name");
				user.setEmailAddress("user" + (i + 1) + "@gmail.com");
				user.setUserGroup(userGroups[i % 5]);
				BoMgmt.createBo(user, null, null);
			}
		} catch (Exception e) {
			log.error("run", "Test failed: ", e);
			return;
		}
	}
}
