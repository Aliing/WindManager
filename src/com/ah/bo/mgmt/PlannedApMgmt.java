package com.ah.bo.mgmt;

import java.util.Set;

import com.ah.bo.admin.HmUser;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.bo.monitor.PlannedAP;

public interface PlannedApMgmt {
	/*
	 * Create planned AP and position it on the map.
	 */
	public PlannedAP createPlannedAP(MapContainerNode mapContainerNode,
			PlannedAP plannedAP, HmUser user, String feature) throws Exception;

	/*
	 * Find planned AP by id.
	 */
	public PlannedAP findPlannedAP(long plannedId) throws Exception;

	/*
	 * Load all planned APs on this map from database.
	 */
	public void loadPlannedAPs(MapContainerNode mapContainerNode,
			Set<MapNode> childNodes);

	/*
	 * Update planned AP.
	 */
	public PlannedAP updatePlannedAP(MapContainerNode mapContainerNode,
			long plannedId, String hostName, short apModel, short wifi0Channel,
			short wifi1Channel, short wifi0Power, short wifi1Power, short radio)
			throws Exception;

	/*
	 * Update planned AP position.
	 */
	public PlannedAP movePlannedAP(MapContainerNode mapContainerNode,
			long plannedId, double x, double y) throws Exception;

	/*
	 * Remove planned AP.
	 */
	public void removePlannedAP(long plannedId) throws Exception;
}
