package com.ah.ui.actions.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ah.bo.HmBo;
import com.ah.bo.admin.HmAccessControl;
import com.ah.bo.mgmt.QueryBo;
import com.ah.bo.mgmt.QueryUtil;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class HmAccessControlAction extends BaseAction implements QueryBo {

	private static final long serialVersionUID = 1L;

	public static HmAccessControl CURRENT_ACL;

	private static final Tracer log = new Tracer(HmAccessControlAction.class
			.getSimpleName());

	@Override
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("update".equals(operation)) {
				log.info("execute", "operation:" + operation);
				if (null == getDataSource()) {
					initValues();
					return INPUT;
				}
				boolean result = saveObject();
				if (!result) {
					prepareDependentObjects();
					return SUCCESS;
				}
				// create/update successfully
				CURRENT_ACL = getDataSource();
				setFormChanged(false);
				initValues();
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			prepareDependentObjects();
			return SUCCESS;
		}
		initValues();
		return SUCCESS;
	}

	public void prepare() throws Exception {
		super.prepare();
		setDataSource(HmAccessControl.class);
		setSelectedL2Feature(L2_FEATURE_HM_ACCESS);
	}

	public HmAccessControl getDataSource() {
		return (HmAccessControl) dataSource;
	}

	private void prepareDependentObjects() {
		prepareIpAddress();
	}

	private void prepareIpAddress() {
		deniedIps = new ArrayList<String>();
		allowedIps = new ArrayList<String>();
		if (null != getDataSource()) {
			List<String> ips = getDataSource().getIpAddresses();
			if (getDataSource().getControlType() == HmAccessControl.CONTROL_TYPE_DENY) {
				if (null != ips) {
					deniedIps = ips;
				}
			} else if (getDataSource().getControlType() == HmAccessControl.CONTROL_TYPE_PERMIT) {
				if (null != ips) {
					allowedIps = ips;
				}
			}
		}
	}

	private boolean saveObject() throws Exception {
		if (getDataSource().getControlType() == HmAccessControl.CONTROL_TYPE_PERMIT) {
			getDataSource().setIpAddresses(allowedIps);
		} else {
			getDataSource().setIpAddresses(deniedIps);
		}
		if (getDataSource().getControlType() == HmAccessControl.CONTROL_TYPE_PERMIT) {
			if (null == allowedIps || allowedIps.isEmpty()) {
				addActionError(getText("error.pleaseAddItems"));
				return false;
			}
		}
		if (null != getDataSource().getId()) {
			this.id = getDataSource().getId();
			updateBo(getDataSource());
		} else {
			createBo(getDataSource());
		}
		return true;
	}

	private void initValues() throws Exception {
		List<HmAccessControl> list = QueryUtil
				.executeQuery(HmAccessControl.class, null, null);
		if (list.isEmpty()) {
			setSessionDataSource(new HmAccessControl());
		} else {
			setSessionDataSource(findBoById(HmAccessControl.class, list.get(0).getId(), this));
		}
		prepareDependentObjects();
	}

	public EnumItem[] getControlType1() {
		return new EnumItem[] { new EnumItem(HmAccessControl.CONTROL_TYPE_DENY,
				MgrUtil.getEnumString("enum.hm.access.control.type."
						+ HmAccessControl.CONTROL_TYPE_DENY)) };
	}

	public EnumItem[] getControlType2() {
		return new EnumItem[] { new EnumItem(
				HmAccessControl.CONTROL_TYPE_PERMIT, MgrUtil
						.getEnumString("enum.hm.access.control.type."
								+ HmAccessControl.CONTROL_TYPE_PERMIT)) };
	}

	public EnumItem[] getDenyBehaviors() {
		return HmAccessControl.BEHAVIOR_TYPE;
	}

	public String getAllowStyle() {
		return getDataSource().getControlType() == HmAccessControl.CONTROL_TYPE_PERMIT ? ""
				: "none";
	}

	public String getDenyStyle() {
		return getDataSource().getControlType() == HmAccessControl.CONTROL_TYPE_DENY ? ""
				: "none";
	}

	private List<String> allowedIps;
	private List<String> deniedIps;

	public List<String> getAllowedIps() {
		return allowedIps;
	}

	public void setAllowedIps(List<String> allowedIps) {
		this.allowedIps = allowedIps;
	}

	public List<String> getDeniedIps() {
		return deniedIps;
	}

	public void setDeniedIps(List<String> deniedIps) {
		this.deniedIps = deniedIps;
	}
	
	@Override
	public Collection<HmBo> load(HmBo bo) {
		if (bo instanceof HmAccessControl) {
			dataSource = bo;
			if (getDataSource().getIpAddresses() != null)
				getDataSource().getIpAddresses().size();
		}
		return null;
	}
}