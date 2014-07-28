package com.ah.ui.actions.teacherView;

import com.ah.ui.actions.BaseAction;
import com.ah.ui.actions.Navigation;
import com.ah.ui.actions.admin.NavigationCustomizationUtil;

import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class TvGuideAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(TvGuideAction.class
			.getSimpleName());

	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}

		try {
			if ("configClasses".equals(operation)
					|| "configComputerCart".equals(operation)
					|| "configStudentRoster".equals(operation)
					|| "configResourceMap".equals(operation)) {
				log.info("execute", "operation:" + operation);
				return operation;
			} else if ("addClass".equals(operation)
					|| "addComputerCart".equals(operation)
					|| "addStudentRoster".equals(operation)
					|| "addResourceMap".equals(operation)) {
				log.info("execute", "operation:" + operation);
				addLstForward("guidedTeacherView");
				return operation;
			} else {
				removeSessionAttributes();
				return SUCCESS;
			}
		} catch (Exception e) {
			addActionError(MgrUtil.getUserMessage(e));
			return SUCCESS;
		}
	}

	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_TV_GUIDE);
	}	

}