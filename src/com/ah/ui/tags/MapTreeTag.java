package com.ah.ui.tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.struts2.ServletActionContext;

import com.ah.be.common.MapNodeUtil;
import com.ah.bo.HmBo;
import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.MapMgmt;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.ui.actions.monitor.MapAlarmsCache;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.util.ValueStack;

public class MapTreeTag extends TagSupport implements QueryBo {
	protected Tracer log = new Tracer(TreeTag.class.getSimpleName());

	protected String root = null;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo == null) {
			return null;
		}
		MapContainerNode map = (MapContainerNode) bo;
		if (map != null) {
			if (map.getParentMap() == null) {
				for (MapNode vhm : map.getChildNodes()) {
					if (worldMapId != null && !vhm.getId().equals(worldMapId)) {
						continue;
					}
					vhm.setSelected(true);
					((MapContainerNode) vhm).getChildNodes().size();
				}
			} else {
				selectPath(map);
				MapContainerNode building = null;
				if (map.getMapType() == MapContainerNode.MAP_TYPE_BUILDING) {
					building = map;
				} else if (map.getMapType() == MapContainerNode.MAP_TYPE_FLOOR) {
					building = map.getParentMap();
				}
				if (building == null) {
					selectSubset(map, 0);
				} else {
					for (MapNode floor : building.getChildNodes()) {
						floor.setSelected(true);
					}
				}
			}
		}
		return null;
	}

	private void selectPath(MapContainerNode map) {
		MapContainerNode parent = map.getParentMap();
		if (parent == null) {
			return;
		}
		selectPath(parent);
		for (MapNode mapNode : parent.getChildNodes()) {
			if (mapNode.getId().equals(map.getId())) {
				mapNode.setSelected(true);
				return;
			}
		}
		log.info_ln("No match ?");
	}

	private int selectSubset(MapContainerNode map, int count) {
		int max = 30;
		int add = map.getChildNodes().size();
		if (count + add > max) {
			add = max - count;
		}
		int i = 0, more = 0;
		for (MapNode mapNode : map.getChildNodes()) {
			if (mapNode.isLeafNode()) {
				continue;
			}
			MapContainerNode child = (MapContainerNode) mapNode;
			if (i < add) {
				mapNode.setSelected(true);
				if (count + add + more < max) {
					more += selectSubset(child, count + add + more);
				} else {
					child.getChildNodes().size();
				}
				i++;
			}
		}
		return add + more;
	}

	Long worldMapId;

	public int doStartTag() throws JspException {
		worldMapId = null;
		Long selectedMapId = (Long) MgrUtil
				.getSessionAttribute(SessionKeys.SELECTED_MAP_ID);
		HmUser userContext = (HmUser) MgrUtil
				.getSessionAttribute(SessionKeys.USER_CONTEXT);
		HmUserGroup userGroup = userContext == null ? null : userContext
				.getUserGroup();

		Long domainId = QueryUtil.getDomainFilter(userContext);
		if (domainId != null) {
			worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
		}
		MapContainerNode selectedMap = null;
		if (selectedMapId != null) {
			selectedMap = (MapContainerNode) QueryUtil.findBoById(
					MapNode.class, selectedMapId, this);
		}
		if (selectedMap != null && domainId != null) {
			if (!domainId.equals(selectedMap.getOwner().getId())) {
				selectedMap = null; // Switch domain is different from selected
									// map domain, don't use this node.
			}
		}
		if (selectedMap == null) {
			selectedMap = (MapContainerNode) QueryUtil.findBoById(
					MapNode.class, BoMgmt.getMapMgmt().getRootMapId(), this);
		}
		MapContainerNode rootMap = selectedMap;
		while (rootMap.getParentMap() != null) {
			rootMap = rootMap.getParentMap();
		}
		StringBuffer results = new StringBuffer("");
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		ValueStack vs = ServletActionContext.getValueStack(request);
		boolean showDomain = (Boolean) vs.findValue("showDomain");
		MapAlarmsCache cache = (MapAlarmsCache) MgrUtil
				.getSessionAttribute(SessionKeys.MAP_ALARMS_CACHE);

		boolean featureWrite = true;
		if (!userGroup.isAdministrator()) {
			HmPermission featurePermission = userGroup.getFeaturePermissions()
					.get(Navigation.L2_FEATURE_MAP_VIEW);
			featureWrite = featurePermission != null
					&& featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE);
		}

		generateTree(results, userGroup, cache, rootMap,
				userContext.isOrderFolders(), worldMapId, showDomain,
				featureWrite, 0, "");

		JspWriter writer = pageContext.getOut();
		try {
			writer.print(results.toString());
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}

	private void generateTree(StringBuffer results, HmUserGroup userGroup,
			MapAlarmsCache cache, final MapContainerNode map,
			final boolean orderFolders, Long worldMapId, boolean showDomain,
			boolean featureWrite, int level, String indent) {
		if (map == null) {
			return;
		}
		if (map.getParentMap() != null) {
			String mapName = map.getMapName();
			boolean uninitialized = MapMgmt.VHM_ROOT_MAP_NAME.equals(mapName);
			boolean expanded = cache == null ? false : cache
					.isTreeNodeExpanded(map.getId());
			// mapName = mapName == null ? "" : mapName.replace("\\", "\\\\")
			// .replace(" ", "&nbsp;").replace("\"", "\\\"")
			// .replace("'", "\\\'");
			// Use space instead of &nbsp; in YUI 2.9.
			// \ and ' still need escape because of javascript.
			mapName = mapName == null ? "" : mapName.replace("\\", "\\\\")
					.replace("'", "\\\'");
			if (map.getParentMap().getParentMap() == null) {
				if (worldMapId != null && !map.getId().equals(worldMapId)) {
					return;
				}
				// update the uninitialized VHM root map name
				if (uninitialized) {
					mapName = "Uninitialized";
				}
				// set root map node expand status default.
				if (null == cache) {
					expanded = true;
				}
				if (showDomain) {
					String domainName = map.getOwner().getDomainName();
					domainName = domainName == null ? "" : domainName;
					mapName = domainName + " - " + mapName;
				}
			}
			results.append(indent
					+ "{ label:'"
					+ StringEscapeUtils.escapeHtml4(mapName)
					+ "', title:'"
					+ StringEscapeUtils.escapeHtml4(mapName)
					+ "', id:'"
					+ map.getId()
					+ "', tp:'"
					+ map.getMapType()
					+ "', wp:"
					+ (getInstanceWrite(userGroup, map, showDomain) && featureWrite)
					+ ", lvl: " + level + ", uiz:" + uninitialized
					+ ", expanded:" + expanded);
			results.append(", items: [");
		}
		if (map.getMapType() != MapContainerNode.MAP_TYPE_FLOOR) {
			// order by id
			Set<MapNode> children = map.getChildNodes();
			List<MapNode> list = MapNodeUtil.sortMapTree(children, orderFolders, map,true);
			boolean needComma = false;
			for (MapNode child : list) {
				if (!child.isSelected()) {
					continue;
				}
				if (child.isLeafNode()) {
					continue;
				}
				if (userGroup != null && !userGroup.isAdministrator()) {
					HmPermission permission = userGroup
							.getInstancePermissions().get(child.getId());
					if (permission == null) {
						continue;
					}
				}
				if (needComma) {
					results.append(",\n");
				} else {
					results.append("\n");
					needComma = true;
				}
				generateTree(results, userGroup, cache,
						(MapContainerNode) child, orderFolders, worldMapId,
						showDomain, featureWrite, level + 1, indent + "  ");
			}
		}
		if (map.getParentMap() != null) {
			results.append("]");
			results.append(indent + "}");
		}
	}

	private boolean getInstanceWrite(HmUserGroup userGroup, MapNode mapNode,
			boolean showDomain) {
		boolean instanceWrite = true;
		if (userGroup != null && !userGroup.isAdministrator()) {
			HmPermission permission = userGroup.getInstancePermissions().get(
					mapNode.getId());
			if (permission != null) {
				instanceWrite = permission
						.hasAccess(HmPermission.OPERATION_WRITE);
			}
		} else if (userGroup != null && userGroup.isAdministrator()) {
			if (showDomain) {
				if (!HmDomain.HOME_DOMAIN.equals(mapNode.getOwner()
						.getDomainName())) {
					instanceWrite = false;
				}
			}
		}
		return instanceWrite;
	}

	public int doEndTag() throws JspException {
		JspWriter writer = pageContext.getOut();
		try {
			writer.print("");
			release();
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_PAGE);
	}

	public void release() {
		root = null;
	}
}
