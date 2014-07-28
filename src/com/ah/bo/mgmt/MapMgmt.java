package com.ah.bo.mgmt;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ah.be.common.NmsUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.hiveap.ConfigTemplate;
import com.ah.bo.hiveap.HiveAp;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapLeafNode;
import com.ah.bo.monitor.MapLink;
import com.ah.bo.monitor.MapNode;
import com.ah.util.CheckItem;

/*
 * @author Chris Scheers
 */

public interface MapMgmt {

	public static final String ROOT_MAP_NAME = "|root|";

	public static final String VHM_ROOT_MAP_NAME = "|v"
			+ NmsUtil.getOEMCustomer().getNmsNameAbbreviation().toLowerCase()
			+ "_root|";

	public static final String BASE_LEAFNODE_ICON = "target_icon.png";

	public static final String EXTERNAL_ICON = "external_map.png";

	public void init();

	public void destroy();

	public Long getRootMapId();

	public void setRootMapId(Long rootMapId);

	public MapContainerNode getRootMap();

	public Long createWorldMap(HmDomain hmDomain) throws Exception;

	public Long getWorldMapId(Long domainId);

	public Set<Long> getContainerDownIds(Long mapId);

	public List<CheckItem> getMapListView(Long domainId);

	public List<CheckItem> getMapListView(Long domainId,
			Collection<Long> includeOnly);

	public List<CheckItem> getMapListView(Long domainId,
			Collection<Long> includeOnly, boolean allNode);

	public List<CheckItem> getParentMapList(Long domainId, boolean orderFolders);

	/*
	 * Leaf nodes should always be created using this function. This is to make
	 * sure that every leaf node has a parent map and a corresponding HiveAp
	 * object.
	 */
	public HiveAp createMapLeafNode(HiveAp hiveAp, MapLeafNode node,
			MapContainerNode parent) throws Exception;

	/*
	 * Update a hiveAp, and delete corresponding mapLeafNode. This method is be
	 * used in case of update a HiveAp, deselect any topology map.
	 */
	public HiveAp removeMapLeafNode(HiveAp hiveAp) throws Exception;

	/*
	 * Update a HiveAp, and remove the old mapLeafNode, and a new mapLeafNode.
	 * This method is used in case of while update a HiveAp, and change its
	 * topology map;
	 */
	public HiveAp replaceMapLeafNode(HiveAp hiveAp, MapLeafNode newNode,
			MapContainerNode newParent) throws Exception;

	/*
	 * Create the passed HiveAP object(contains synchronizing its mapLeafNode &
	 * mapContainer) and propagate the managed status.
	 */
	public HiveAp createHiveApWithPropagation(HiveAp hiveAp,
			MapContainerNode newParent) throws Exception;

	/*
	 * Update the passed HiveAP object(contains synchronizing its mapLeafNode &
	 * mapContainer) and propagate the managed status, connection status into
	 * cache.
	 */
	public HiveAp updateHiveApWithPropagation(HiveAp hiveAp,
			MapContainerNode newParent, boolean oldConnection,
			short oldManagedStatus) throws Exception;

	/*
	 * Update hiveAp and propagate the host name to leafNode if needed.<br>
	 * Note: this function only can be used within following conditions: <li>
	 * :The parent map container of this hiveAp is not changed. <li>:The
	 * connection status of this hiveAp is not changed. <li>:The managed status
	 * of this hiveAp is not changed.
	 */
	public HiveAp updateHiveAp(HiveAp hiveAp) throws Exception;

	/*
	 * Remove HiveAP objects and its related map nodes and links.
	 */
	public int removeHiveAps(Collection<Long> ids) throws Exception;
	
	/*
	 * Remove HiveAP objects and its related map nodes and links.
	 */
	public int removeHiveAps(Collection<Long> ids, boolean resetFlag) throws Exception;
	
	/*
	 * Remove HiveAP objects and its related map nodes and links.
	 * return removed ids
	 */
	public Collection<Long> removeHiveApsReturnRemovedIds(Collection<Long> ids, boolean removeFromLS, boolean removeFromRedirector, String vhmId, boolean resetFlag) throws Exception;

	/*
	 * Initial (auto) placement of a node icon.
	 */
	public void placeIcon(MapContainerNode mapContainerNode, MapNode mapNode);

	/*
	 * Update map container links.
	 */
	public MapContainerNode updateMapContainerLinks(Long containerId,
			Map<String, MapLink> childLinks, QueryBo queryBo) throws Exception;

	/*
	 * Generally create map container.
	 */
	public Long createMapContainer(MapContainerNode container,
			MapContainerNode peer) throws Exception;

	/*
	 * Generally update map container.
	 */
	public MapContainerNode updateMapContainer(MapContainerNode container)
			throws Exception;

	/*
	 * assign a map leaf node to the specify HiveAp.
	 */
	public void assignMapLeafNodeToDiscoveredHiveAp(HiveAp hiveAp)
			throws Exception;

	/*
	 * Update leaf node severity
	 */
	public Collection<HmBo> updateLeafNodeSeverity(Long leafNodeId,
			short severity, QueryBo queryBo) throws Exception;

	/*
	 * Update leaf node ethId
	 */
	public void updateLeafNodeEthId(Long leafNodeId, String ethId)
			throws Exception;

	/*
	 * Update leaf node fetch links timeout
	 */
	public void updateLeafNodeFetchTimeout(Long leafNodeId, boolean timeout)
			throws Exception;

	/*
	 * Update leaf node position
	 */
	public void updateLeafNodePosition(Long leafNodeId, double x, double y)
			throws Exception;

	void reassignDomain(HiveAp hiveAp, ConfigTemplate newTemplate,
			HmDomain domain) throws Exception;

	MapContainerNode getVHMRootMap(Long vhmId);
}