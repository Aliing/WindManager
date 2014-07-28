package com.ah.ui.actions;

public class PushAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	static boolean first;

	private long eventCount;

	public String execute() throws Exception {
		String forward = globalForward();
		if (forward != null) {
			return forward;
		}
		if ("init".equals(operation)) {
			eventCount = 0;
			first = true;
		} else if ("alarms".equals(operation)) {
			if (first) {
				eventCount = 0;
				first = false;
			} else {
				eventCount = Math.round(Math.random() * 10) + 1;
				Thread.sleep(4000);
			}
		}
		return SUCCESS;
	}

	public long getEventCount() {
		return eventCount;
	}

}