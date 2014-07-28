package com.ah.ui.actions.config;

import com.ah.bo.HmBo;
import com.ah.bo.network.AirScreenAction;
import com.ah.bo.network.AirScreenBehavior;
import com.ah.bo.network.AirScreenSource;
import com.ah.ui.actions.BaseAction;
import com.ah.util.MgrUtil;
import com.ah.util.Tracer;

public class AirScreenSourceAction extends BaseAction {
	
	private static final long serialVersionUID = 1L;
	
	private static final Tracer log = new Tracer(AirScreenSourceAction.class
			.getSimpleName());
	
	public static final String	AIR_SCREEN_SOURCE_TYPE	= "AIR_SCREEN_SOURCE_TYPE";
	
	private String actionType = "source";

	@Override
	public String execute() throws Exception {
		String fw = globalForward();
		if (fw != null) {
			return fw;
		}
		try {
			if ("viewBehaviors".equals(operation)) {
				MgrUtil.setSessionAttribute(AIR_SCREEN_SOURCE_TYPE, "behavior");
			} else if ("viewActions".equals(operation)) {
				MgrUtil.setSessionAttribute(AIR_SCREEN_SOURCE_TYPE, "action");
			} else if ("viewSources".equals(operation)) {
				MgrUtil.setSessionAttribute(AIR_SCREEN_SOURCE_TYPE, "source");
			}
			actionType = (String)MgrUtil.getSessionAttribute(AIR_SCREEN_SOURCE_TYPE);
			
			if ("behavior".equals(actionType)) {
				setDataSource(AirScreenBehavior.class);
			} else if ("action".equals(actionType)) {
				setDataSource(AirScreenAction.class);
			} else {
				setDataSource(AirScreenSource.class);
			}
			if ("return".equals(operation)) {
				setUpdateContext(false);
				return "airScreenRule";
			}
			log.info("execute", "operation:" + operation);
			baseOperation();
			clearDataSource();
			preparePage();
			setTableColumns();
			return SUCCESS;
		} catch (Exception e) {
			return prepareActionError(e);
		}
	}

	@Override
	public void prepare() throws Exception {
		super.prepare();
		setSelectedL2Feature(L2_FEATURE_AIR_SCREEN_RULE);
	}

	@Override
	public HmBo getDataSource() {	
		if ("behavior".equals(actionType)) {
			return (AirScreenBehavior) dataSource;
		} else if ("action".equals(actionType)) {
			return (AirScreenAction) dataSource;
		} else {
			return (AirScreenSource) dataSource;
		}
	}

	public String getDisplayLabel() {
		if ("behavior".equals(actionType)) {
			return MgrUtil.getUserMessage("config.air.screen.rule.behaviorBtn");
		} else if ("action".equals(actionType)) {
			return MgrUtil.getUserMessage("config.air.screen.rule.actionBtn");
		} else {
			return MgrUtil.getUserMessage("config.air.screen.rule.sourceBtn");
		}
	}

	public String getActionType()
	{
		return actionType;
	}

	public void setActionType(String actionType)
	{
		this.actionType = actionType;
	}

}
