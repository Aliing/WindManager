package com.ah.ui.actions.admin;

import com.ah.be.app.HmBeAdminUtil;
import com.ah.be.app.HmBeResUtil;
import com.ah.bo.admin.HmAuditLog;
import com.ah.ui.actions.BaseAction;
import com.ah.util.EnumItem;
import com.ah.util.MgrUtil;
//import com.ah.util.Tracer;

@SuppressWarnings("serial")
public class SSHKeysGenAction extends BaseAction {

//	private static final Tracer	log	= new Tracer(SSHKeysGenAction.class.getSimpleName());

	private int genAlgorithm;
	
	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		try {
			if ("genKey".equals(operation)) {
				
				boolean isSuccess = HmBeAdminUtil.generateAuthkeys(genAlgorithm ==1 ? "rsa":"dsa");
				if (isSuccess) {
					generateAuditLog(HmAuditLog.STATUS_SUCCESS, MgrUtil.getUserMessage("hm.audit.log.generate.ssh.key"));
					
					addActionMessage(HmBeResUtil.getString("sshKey.generate.success"));
				}
				else
				{
					generateAuditLog(HmAuditLog.STATUS_FAILURE, MgrUtil.getUserMessage("hm.audit.log.generate.ssh.key"));
					
					addActionError(HmBeResUtil.getString("sshKey.generate.error"));
				}
				
				return SUCCESS;
			} else {
				return SUCCESS;
			}
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_SSHKEYSGEN);
	}

	private static final int	ALGORITHM_RSA	= 1;
	private static final int	ALGORITHM_DSA	= 2;

	public static EnumItem[] getEnumAlgorithm() {
		return MgrUtil.enumItems("enum.sshKeyGen.algorithm.", new int[] { ALGORITHM_RSA,
				ALGORITHM_DSA });
	}

	public int getGenAlgorithm() {
		return genAlgorithm;
	}

	public void setGenAlgorithm(int genAlgorithm) {
		this.genAlgorithm = genAlgorithm;
	}

}