package com.ah.ui.tags;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts2.ServletActionContext;

import com.ah.bo.admin.HmDomain;
import com.ah.bo.admin.HmPermission;
import com.ah.bo.admin.HmUser;
import com.ah.bo.admin.HmUserGroup;
import com.ah.bo.mgmt.BoMgmt;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.bo.monitor.MapContainerNode;
import com.ah.bo.monitor.MapNode;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.SessionKeys;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;
import com.opensymphony.xwork2.util.ValueStack;

public class TreeTag extends TagSupport {
	protected Tracer log = new Tracer(TreeTag.class.getSimpleName());

	protected String root = null;

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public int doStartTag() throws JspException {
		MapContainerNode rootMap = BoMgmt.getMapMgmt().getRootMap();
		StringBuffer results = new StringBuffer("\n");
		HmUser userContext = (HmUser) MgrUtil
				.getSessionAttribute(SessionKeys.USER_CONTEXT);
		HmUserGroup userGroup = userContext == null ? null : userContext
				.getUserGroup();

		boolean featureWrite = true;
		if (!userGroup.isAdministrator()) {
			HmPermission featurePermission = userGroup.getFeaturePermissions()
					.get(Navigation.L2_FEATURE_MAP_VIEW);
			featureWrite = featurePermission != null
					&& featurePermission
							.hasAccess(HmPermission.OPERATION_WRITE);
		}
		Long domainId = QueryUtil.getDomainFilter(userContext);
		Long worldMapId = null;
		if (domainId != null) {
			worldMapId = BoMgmt.getMapMgmt().getWorldMapId(domainId);
		}
		HttpServletRequest request = (HttpServletRequest) pageContext
				.getRequest();
		ValueStack vs = ServletActionContext.getValueStack(request);
		boolean showDomain = (Boolean) vs.findValue("showDomain");

		results.append("<script>\nvar mps = new Array();\n");
		generateTreePermissions(results, userGroup, rootMap, featureWrite,
				showDomain);
		results.append("var mls = new Array();\n");
		generateTreeLevels(results, userGroup, rootMap, 0);
		results.append("</script>\n");

		generateTree(results, userGroup, rootMap, worldMapId, showDomain, "");

		JspWriter writer = pageContext.getOut();
		try {
			writer.print(results.toString());
		} catch (IOException e) {
			throw new JspException(e.toString());
		}
		return (EVAL_BODY_INCLUDE);
	}

	private void generateTreeLevels(StringBuffer results,
			HmUserGroup userGroup, MapContainerNode map, int level) {
		if (map == null) {
			return;
		}
		level++;
		for (MapNode child : map.getChildNodes()) {
			if (child.isLeafNode()) {
				continue;
			}
			if (userGroup != null && !userGroup.isAdministrator()) {
				HmPermission permission = userGroup.getInstancePermissions()
						.get(child.getId());
				if (permission == null) {
					continue;
				}
			}
			results.append(" mls['" + child.getId() + "'] = " + level + ";\n");
			generateTreeLevels(results, userGroup, (MapContainerNode) child,
					level);
		}
	}

	private void generateTreePermissions(StringBuffer results,
			HmUserGroup userGroup, MapContainerNode map, boolean featureWrite,
			boolean showDomain) {
		if (map == null) {
			return;
		}
		for (MapNode child : map.getChildNodes()) {
			if (child.isLeafNode()) {
				continue;
			}
			boolean instanceWrite = true;
			if (userGroup != null && !userGroup.isAdministrator()) {
				HmPermission permission = userGroup.getInstancePermissions()
						.get(child.getId());
				if (permission == null) {
					continue;
				} else {
					instanceWrite = permission
							.hasAccess(HmPermission.OPERATION_WRITE);
				}
			} else if (userGroup != null && userGroup.isAdministrator()) {
				if (showDomain) {
					if (!HmDomain.HOME_DOMAIN.equals(child.getOwner()
							.getDomainName())) {
						instanceWrite = false;
					}
				}
			}
			results.append(" mps['" + child.getId() + "'] = "
					+ (instanceWrite && featureWrite) + ";\n");
			generateTreePermissions(results, userGroup,
					(MapContainerNode) child, featureWrite, showDomain);
		}
	}

	private void generateTree(StringBuffer results, HmUserGroup userGroup,
			MapContainerNode map, Long worldMapId, boolean showDomain,
			String indent) {
		if (map == null) {
			return;
		}
		if (map.getParentMap() != null) {
			String mapName = map.getMapName();
			mapName = mapName == null ? "" : mapName.replace("\"", "&quot;");
			if (map.getParentMap().getParentMap() == null) {
				if (worldMapId != null && !map.getId().equals(worldMapId)) {
					return;
				}
				if (showDomain) {
					mapName = map.getOwner().getDomainName() + " - " + mapName;
				}
			}
			results.append(indent + "<div dojoType=\"TreeNode\" title=\""
					+ mapName + "\" + id=\"" + map.getId() + "\"");
			if (map.getParentMap().getParentMap() == null) {
				results.append(" expandLevel=\"2\"");
			}
			results.append(">\n");
		}
		for (MapNode child : map.getChildNodes()) {
			if (child.isLeafNode()) {
				continue;
			}
			if (userGroup != null && !userGroup.isAdministrator()) {
				HmPermission permission = userGroup.getInstancePermissions()
						.get(child.getId());
				if (permission == null) {
					continue;
				}
			}
			generateTree(results, userGroup, (MapContainerNode) child,
					worldMapId, showDomain, indent + "  ");
		}
		if (map.getParentMap() != null) {
			results.append(indent + "</div>\n");
		}
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
