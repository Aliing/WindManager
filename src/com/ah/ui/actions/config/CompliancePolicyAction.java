package com.ah.ui.actions.config;

/* import com.ah.be.common.ConfigUtil; */

import java.util.List;
import com.ah.bo.mgmt.QueryUtil;

import com.ah.bo.network.CompliancePolicy;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;

public class CompliancePolicyAction extends BaseAction {

	private static final long		serialVersionUID	= 1L;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("update".equals(operation)) {
				if (dataSource != null) {
					setId(getDataSource().getId());
					updateBo(dataSource);
				}
				editBo();
				setFormChanged(false);
				return SUCCESS;
			}else if ("create".equals(operation)) {
				if (dataSource != null) {
					createBo(dataSource);
				}
				setId(getDataSource().getId());
				editBo();
				setFormChanged(false);
				return SUCCESS;
			} else {
				List<CompliancePolicy> configData = QueryUtil.executeQuery(CompliancePolicy.class, null, null, getDomain().getId());
				if (configData.isEmpty()) {
					setSessionDataSource(new CompliancePolicy());
				}  else {
					setSessionDataSource(findBoById(CompliancePolicy.class, configData.get(0).getId()));
				}
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return ERROR;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_COMPLIANCE_POLICY);
		setDataSource(CompliancePolicy.class);
	}

}