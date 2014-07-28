package com.ah.bo.mgmt;

import com.ah.be.common.cache.SimpleHiveAp;
import com.ah.bo.hiveap.HiveAp;

/*
 * @author Chris Scheers
 */

public interface MapHierarchyCache {

	void init();

	void register();

	void deregister();

	void destroy();

	Folder getFolder(Long id);

	void hiveApAdded(HiveAp hiveAp);

	void hiveApRemoved(HiveAp hiveAp);

	void hiveApUpdated(HiveAp hiveAp);

	void activeClientAdded(SimpleHiveAp associate_ap, int count);

	void activeClientRemoved(SimpleHiveAp associate_ap, int count);

}